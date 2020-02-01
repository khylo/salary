package com.khylo.salary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.MathContext;

import static com.khylo.common.Utils.bd;

@JsonInclude(Include.NON_DEFAULT)
@Data
@SuperBuilder(toBuilder = true) // Inclde superclass fields
public class Salary extends SalaryInputParams{
	// TODO make field final, and add Builder
	public static final int Gross = 0;
	public static final int Net = 4;
	//private static DateTimeFormatter  df = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Min(0)
    BigDecimal costOfEmployment;
    @Min(0)
    BigDecimal net;
    @Min(0)
    BigDecimal paye;
    @Min(0)
    BigDecimal payeAtLowerRate;
    @Min(0)
    BigDecimal payeAtHigherRate;
    @Min(0)
    BigDecimal prsi;
    @Min(0)
    BigDecimal employersPrsi;
	Prsi prsiClass;
	@Min(0)
    BigDecimal usc;
	/* Inherited from SalaryInputParams
	 * @Max(MaxNum)
	@Min(MinNum)
	BigDecimal gross;
	Prsi prsiClass;
	boolean eligbleForReducedUsc; // See IESalarySErvice.calcTaxCredits for eligibilty rules 
	@Max(MaxNum)
	@Min(MinNum)
	BigDecimal stdRateCutoff;
	@Max(MaxNum)
	@Min(MinNum)
	BigDecimal taxCredits;
	 */

	public Salary(){}
	
	public String csv(){
		return print(",");			
	}
	
	public static String pHdr(){
		return "gross\tcostOfEmployment\tpaye\tprsi\tusc\tnet\temployersPrsi";			
	}
	
	public String print(String d){
		return ""+gross+ d +costOfEmployment+ d +paye+ d +prsi+ d +usc+ d +net+ d +employersPrsi;			
	}
	
	public Salary add(final Salary s){
		Salary ret = this.builder().build(); // copy constructor
		ret.gross = s.gross.add(ret.gross);
		ret.costOfEmployment = s.costOfEmployment.add(ret.costOfEmployment);
		ret.net = s.net.add(ret.net);
		ret.paye = s.paye.add(ret.paye);
		ret.payeAtLowerRate = s.payeAtLowerRate.add(ret.payeAtLowerRate);
		ret.payeAtHigherRate = s.payeAtHigherRate.add(ret.payeAtHigherRate);
		ret.prsi = s.prsi.add(ret.prsi);
		ret.employersPrsi = s.employersPrsi.add(ret.employersPrsi);
		ret.usc = s.usc.add(ret.usc);
		ret.stdRateCutoff = s.stdRateCutoff.add(ret.stdRateCutoff);
		ret.taxCredits = s.taxCredits.add(ret.taxCredits);
		return ret;
	}
	public Salary divide(final BigDecimal d){
		return divide(d, MathContext.DECIMAL64);
	}
	
	public Salary divide(final BigDecimal d, final MathContext mc){
		Salary ret = this.builder().build(); // copy constructor
		ret.gross = ret.gross.divide(d, mc);
		ret.costOfEmployment = ret.costOfEmployment.divide(d, mc);
		ret.net = ret.net.divide(d, mc);
		ret.paye = ret.paye.divide(d, mc);
		ret.payeAtLowerRate = ret.payeAtLowerRate.divide(d, mc);
		ret.payeAtHigherRate = ret.payeAtHigherRate.divide(d, mc);
		ret.prsi = ret.prsi.divide(d, mc);
		ret.employersPrsi = ret.employersPrsi.divide(d, mc);
		ret.usc = ret.usc.divide(d, mc);
		ret.stdRateCutoff = ret.stdRateCutoff.divide(d, mc);
		ret.taxCredits = ret.taxCredits.divide(d, mc);
		return ret;
	}
	
	
	public BigDecimal add(int label, BigDecimal s){
		switch(label){
			case Gross:
				return gross.add(s);
			case Net:
				return net.add(s);
			default:
				throw new UnsupportedOperationException("Adding "+label+" not yet supported");
		}
	}
	
	public String toString(){
		return csv();
	}
	
	public static Salary toSalary(SalaryInputParams s){
		Salary ret = Salary.builder().gross(s.getGross())
				.prsiClass(s.getPrsiClass())
				.taxCredits(s.getTaxCredits())
				.stdRateCutoff(s.getStdRateCutoff())
				.eligbleForReducedUsc(s.isEligbleForReducedUsc())
				.build();
		return ret;
	}
	

	
	public BigDecimal calcNet() {
		net = gross.subtract(paye).subtract(usc).subtract(prsi);
		return net;
	}
	
}
