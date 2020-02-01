package com.khylo.salary.calc;

import com.khylo.common.Tuple2;
import com.khylo.common.Tuple3;
import com.khylo.common.Utils;
import com.khylo.salary.MaritalTaxStatus;
import com.khylo.salary.Prsi;
import com.khylo.salary.Salary;
import com.khylo.salary.SalaryInputParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static com.khylo.common.Utils.bd;

@Service
@Validated
public class IESalaryService2018 {
	
	private Logger log  = LogManager.getLogger(this.getClass());
	
	BigDecimal MaxSal = bd("18300");
	
	private static BigDecimal uscZeroCutoff=bd("13000");
	private static String[][] uscA = { {".5", "12012"}, // up to 12012
										{"2", "19372"},
										{"4.75", "70044"},
										{"8", "99999999"}};
	
	private static String[][] uscS = { {".5", "12012"},
										{"2", "19372"},
										{"4.75", "70044"},
										{"8", "100000"},
										{"11", "99999999"}};
	
	private static String[][] uscR = { 	{".5", "12012"},   // Reduced usc for MedicalCard holders and those over 70 iff total earnings less than 60000
										{"2", "999999"}};
	private static final BigDecimal uscReducedMax = bd("60000");
	// Prsi array {employees%, employer%, max weekly earnings, prsiCredit}
	// Note this this not work for people earning 38 or less per week (1976 per year)

	// TODO below
	private static String[][] prsiA = { {"0", "8.5", "352", null}, // = 18304 per year
										{"4", "8.5", "376", "do prsiCredit"}, //19552 per year
										{"4", "10.5", "425", "do prsiCredit"},
										{"4", "10.75", "424", null}};
	
	private static String[][] prsiS = {	{"4", "0", "500", null}};
	
	static final BigDecimal[][] uscRates, uscRatesS, uscRatesReduced;
	static final BigDecimal[][] prsiRates, prsiRatesS;
	
	static final BigDecimal payeStdRate = bd("0.20");
	static final BigDecimal payeHighRate = bd("0.40");
	
	static final BigDecimal TaxCreditsS = bd("1650");
	static final BigDecimal TaxCreditsA = bd("3300");
	static final BigDecimal TaxCreditsD =TaxCreditsA;

	private static final BigDecimal StdRateCutOffSingle = bd("33800");
	private static final BigDecimal StdRateCutOffSingleParent= bd("36800");
	private static final BigDecimal StdRateCutOffMarried1Income = bd("42800");
	private static final BigDecimal StdRateCutOffMarried2IncomeMax = StdRateCutOffSingle.add(StdRateCutOffSingle);
	private static final BigDecimal StdRateCutOffMarried2IncomeDiff = StdRateCutOffMarried2IncomeMax.subtract(StdRateCutOffMarried1Income);
	private static final BigDecimal StdRateCutOffMarried2IncomesIndependentlyAssessed = null;
	private static final BigDecimal StdRateCutOffMarried2IncomesJointAssesment = null;
	private static final BigDecimal StdRateCutOffMarried2IncomesSingleAssesment = null;
	private static final BigDecimal StdRateCutOffWidowed = null;
	
	// Calculators
	//@Autowired
	//MonthlyCalc monthlyCalc;
	//@Autowired
	//WeeklyCalc weeklyCalc;
	//@Autowired
	//WeeklyWithMonthlyCatchUpCalc weekMonthCalc;
	
	
	
	
	/* Calc usc rates for limits	 */
	static{
		uscRates=new BigDecimal[uscA.length][uscA[0].length+2];
		BigDecimal minus = BigDecimal.ZERO;
		int i;
		for(i=0;i<uscA.length;i++){
			uscRates[i][0]=bd(uscA[i][0]).divide(bd("100"));
			uscRates[i][1]=bd(uscA[i][1]);
			uscRates[i][2]=uscRates[i][1].subtract(minus); // Actual gross amount taxable in band
			uscRates[i][3]=uscRates[i][2].multiply(uscRates[i][0]); // Total amount of usc taxin band
			minus = uscRates[i][1];
		}
		uscRates[i-1][3]= BigDecimal.ZERO;
		
		uscRatesS=new BigDecimal[uscS.length][uscS[0].length+2];
		minus = BigDecimal.ZERO;
		for(i=0;i<uscS.length;i++){
			uscRatesS[i][0]=bd(uscS[i][0]).divide(bd("100"));
			uscRatesS[i][1]=bd(uscS[i][1]);
			uscRatesS[i][2]=uscRatesS[i][1].subtract(minus); // Actual gross amount taxable in band
			uscRatesS[i][3]=uscRatesS[i][2].multiply(uscRatesS[i][0]); // Total amount of usc taxin band
			minus = uscRatesS[i][1];
		}
		uscRatesS[i-1][3]= BigDecimal.ZERO;
		
		uscRatesReduced=new BigDecimal[uscR.length][uscR[0].length+2];
		minus = BigDecimal.ZERO;
		for(i=0;i<uscR.length;i++){
			uscRatesReduced[i][0]=bd(uscR[i][0]).divide(bd("100"));
			uscRatesReduced[i][1]=bd(uscR[i][1]);
			uscRatesReduced[i][2]=uscRatesReduced[i][1].subtract(minus); // Actual gross amount taxable in band
			uscRatesReduced[i][3]=uscRatesReduced[i][2].multiply(uscRatesReduced[i][0]); // Total amount of usc taxin band
			minus = uscRatesReduced[i][1];
		}
		uscRatesReduced[i-1][3]= BigDecimal.ZERO;
		
		prsiRates=new BigDecimal[prsiA.length][prsiA[0].length];
		for(i=0;i<prsiA.length;i++){
			prsiRates[i][0]=bd(prsiA[i][0]).divide(bd("100"));
			prsiRates[i][1]=bd(prsiA[i][1]).divide(bd("100"));
			prsiRates[i][2]=bd(prsiA[i][2]).multiply(bd("52"));
			prsiRates[i][2]=bd(prsiA[i][2]).multiply(bd("52"));
			prsiRates[i][3]=prsiA[i][3]==null ? null: BigDecimal.ZERO;
		}
		prsiRatesS=new BigDecimal[prsiS.length][prsiS[0].length];
		for(i=0;i<prsiS.length;i++){
			prsiRatesS[i][0]=bd(prsiS[i][0]).divide(bd("100"));
			prsiRatesS[i][1]=bd(prsiS[i][1]).divide(bd("100"));
			prsiRatesS[i][2]=bd(prsiS[i][2]).multiply(bd("52"));
			prsiRates[i][3]=prsiA[i][3]==null ? null: BigDecimal.ZERO;
		}
	}
	
	public Salary calcSalary(SalaryInputParams s){
		return calcSalary(s.toSalary());
	}
	
	
	public Salary calcSalary(Salary s){
		calcSalary(new Salary[]{s});
		return s;
	}
	
	public Salary[] calcSalary(SalaryInputParams[] s){
		Salary[] sal = new Salary[s.length];
		for(int i=0;i<s.length;i++)
			sal[i]=s[i].toSalary();
		return calcSalary(sal);
	}
	
	public Salary[] calcSalary(Salary[] sal){
		addPaye(sal);
		addPrsi(sal);
		addUsc(sal);
		calcNet(sal);
		return sal;
	}
	
	public BigDecimal calc2016PrsiCredit(SalaryInputParams s){
		BigDecimal maxCredit=bd(12).multiply(bd(52));
		BigDecimal creditThreshold = bd("352.01").multiply(bd(52));
		BigDecimal reduction = s.getGross().subtract(creditThreshold).divide(bd(6), MathContext.DECIMAL64);
		return maxCredit.subtract(reduction);
	}
	
	/*public SalaryPayment calcSalaryPayment(Employee e, SalaryFrequency freq) {
		return calcSalaryPayment(e, freq, null);
	}
	
	
	
	public SalaryPayment calcSalaryPayment(Employee e, SalaryFrequency freq, SalaryPayment last,  LocalDate start, LocalDate end) {		
		Salary yearlySalary = calcSalary(e);
		
		if(!freq.equals(SalaryFrequency.Monthly))
			throw new UnsupportedOperationException("Salary Frequency of "+freq+" not supported");
		switch(freq) {
			case Monthly:
				return monthlyCalc.calcSalaryPayment(yearlySalary, last, start, end);
			default:
				throw new UnsupportedOperationException("Unexpected SalaryFreq.. "+freq);
			
		}
	}*/
	
	protected void calcNet(Salary[] sals){
		for(int i=0;i<sals.length;i++){
			sals[i].calcNet();
		}
	}
	
	public Salary[] addPaye(Salary[] sals){
		for(int i=0;i<sals.length;i++){
			Tuple3<BigDecimal, BigDecimal, BigDecimal> paye = calcPaye(sals[i]);
			sals[i].setPaye(paye.getT1());
			sals[i].setPayeAtLowerRate(paye.getT2());
			sals[i].setPayeAtHigherRate(paye.getT3());
		}
		return sals;
	}
	
	public Salary addPaye(Salary sal){
		return addPaye(new Salary[]{sal})[0];
	}
	
	/**
	 * 
	 * @param s
	 * @return Tuple PAYE, PAYELower, PAYEHigher
	 */
	public Tuple3<BigDecimal, BigDecimal, BigDecimal> calcPaye(SalaryInputParams s){
		BigDecimal paye=null;
		BigDecimal payeL=null;
		BigDecimal payeH=null;
		if(s.getGross().compareTo(s.getStdRateCutoff())<=0){
			paye = s.getGross().multiply(payeStdRate);
				if(paye.compareTo(s.getTaxCredits())<0)
					paye= BigDecimal.ZERO;
				else
					paye = paye.subtract(s.getTaxCredits());
			payeL = paye;
			payeH = BigDecimal.ZERO;
			return new Tuple3<>(paye, payeL, payeH);
		}
		payeL = s.getStdRateCutoff().multiply(payeStdRate);
		payeH = s.getGross().subtract(s.getStdRateCutoff()).multiply(payeHighRate);
		// account for tax credits more than tax on earnings
		if(payeL.compareTo(s.getTaxCredits())<0){
			payeH = payeH.subtract(s.getTaxCredits().subtract(payeL));
			if(payeH.compareTo(BigDecimal.ZERO)<0)
				payeH = BigDecimal.ZERO;
			payeL = BigDecimal.ZERO;
		}else
			payeL = payeL.subtract(s.getTaxCredits());
		paye = payeH.add(payeL);
		return new Tuple3<>(paye, payeL, payeH);	
	}
		/* NOte cal above is correct. Tried subtracting taxCredit from high rate, but that is not correct.
		 * In actual fact, once you take tax creit into account, lower and higher rate values dont make as much sense 
		 * since the tax credit can be subtracted off either
		 * 		if(s.getPayeAtHigherRate().compareTo(s.getTaxCredits())<0){
			s.setPayeAtLowerRate(s.getPayeAtLowerRate().subtract(s.getTaxCredits().subtract(s.getPayeAtHigherRate())));
			s.setPayeAtHigherRate(BigDecimal.ZERO);
			if(s.getPayeAtLowerRate().compareTo(BigDecimal.ZERO)<0)
				s.setPayeAtLowerRate(BigDecimal.ZERO);
		}else
			s.setPayeAtHigherRate(s.getPayeAtHigherRate().subtract(s.getTaxCredits()));
		 */
	

	
	public Salary[]  addPrsi(Salary[] sals){
		for(int i=0;i<sals.length;i++){ 			
			Tuple3<BigDecimal, BigDecimal, BigDecimal> prsiTuple = calcPrsi(sals[i]);
			sals[i].setPrsi(prsiTuple.getT1());
			sals[i].setEmployersPrsi(prsiTuple.getT2());
			sals[i].setCostOfEmployment(prsiTuple.getT3());
		}
		return sals;
	}
	
	public Salary addPrsi(Salary s){
		return addPrsi(new Salary[]{s})[0];
	}
	
	public Tuple3<BigDecimal, BigDecimal, BigDecimal> calcPrsi(SalaryInputParams s){
		BigDecimal[][] prsiRatesTable = null;
		BigDecimal prsi = BigDecimal.ZERO;
		BigDecimal eprsi = null;
		BigDecimal com = null;
		// Must differentiate between S class and PAYE
		if(s.getPrsiClass()== Prsi.S){
			prsiRatesTable = prsiRatesS;
		}else{
			prsiRatesTable = prsiRates;
		}
		int lim=0;
		while(lim<prsiRatesTable.length-1 && s.getGross().compareTo(prsiRatesTable[lim][2])>0)
			lim++;
		BigDecimal prsiGross = prsiRatesTable[lim][0].multiply(s.getGross());
		if(prsiRatesTable[lim].length >3 && prsiRatesTable[lim][3]!=null) // No credit applied
			prsi = prsiGross.subtract(calc2016PrsiCredit(s));
		else{
			prsi = prsiGross;
		}
		
		if(s.getPrsiClass()==Prsi.S)
			eprsi = BigDecimal.ZERO;
		else
			eprsi = prsiRatesTable[lim][1].multiply(s.getGross());

		com = s.getGross().add(eprsi);
		return new Tuple3<>(prsi, eprsi, com);
	}
	
	
	public BigDecimal calcPrsiGeneral2(Salary s, BigDecimal[][] prsiRatesTable){
		int lim=0;
		while(lim<prsiRatesTable.length-1 && s.getGross().compareTo(prsiRatesTable[lim][2])>0)
			lim++;
		s.setPrsi(prsiRatesTable[lim][0].multiply(s.getGross()));
		if(s.getPrsiClass()==Prsi.S)
			s.setEmployersPrsi(BigDecimal.ZERO);
		else
			s.setEmployersPrsi(prsiRatesTable[lim][1].multiply(s.getGross()));

		s.setCostOfEmployment(s.getGross().add(s.getEmployersPrsi()));
		return s.getPrsi();		
	}
	
	public BigDecimal calcUsc(Salary s){
		return addUsc(new Salary[]{s})[0].getUsc();
	}		
	
	/**
	 * Sals are Mutable. THey get updated as they are calculated 
	 * @param sals
	 * @return
	 */
	public  Salary[] addUsc(Salary[] sals){
		BigDecimal runningTotalGross= BigDecimal.ZERO;
		BigDecimal runningTotalInBand = BigDecimal.ZERO;

		BigDecimal totalGross = BigDecimal.ZERO;
		for(int i=0;i<sals.length;i++){ 
			totalGross=totalGross.add(sals[i].getGross());
		}
		if(totalGross.compareTo(uscZeroCutoff)<=0){
			for(int i=0;i<sals.length;i++){ 
				sals[i].setUsc(BigDecimal.ZERO);
			}
			return sals;
		}
		
		Integer idx = new Integer(0);
		boolean topRate = false; // USed if multiple salaries of both prisiClasses, then we need to switch at topRate
		for(int i=0;i<sals.length;i++){ 
			BigDecimal[][] uscRateTable = null;
			// Must differentiate between S class and PAYE since no higher rate for PAYE
			if(sals[i].getPrsiClass()==Prsi.S){
				uscRateTable = uscRatesS;
			}else{
				uscRateTable = uscRates;
			}
			//Check for reduced USC 
			if(sals[i].isEligbleForReducedUsc() && sals[i].getGross().compareTo(uscReducedMax)<=0)
				uscRateTable = uscRatesReduced;
			
			
			//Quick sanity check to see if we have changed prsiClass and have now overshot limit
			if(idx>uscRateTable.length-1)
				topRate=true;
			if(topRate)
				idx = uscRateTable.length-1;
			
			Tuple3<BigDecimal, Integer, BigDecimal> calcResp = calcUscRecur(runningTotalGross, sals[i].getGross(), uscRateTable, idx, runningTotalInBand);
			BigDecimal usc = calcResp.getT1();
			idx = calcResp.getT2();
			runningTotalInBand = calcResp.getT3();
			sals[i].setUsc(usc);
		}
		return sals;
	}
	
	private Tuple3<BigDecimal, Integer, BigDecimal> calcUscRecur(BigDecimal runningTotalGross, BigDecimal amtToBeTaxed, BigDecimal[][] uscRateTable, Integer idx, BigDecimal runningTotalInBand){
		BigDecimal chunk = null;
		BigDecimal rate = uscRateTable[idx][0];
		BigDecimal usc = null;
		//Do we stay in this tax band for next iteration, or jump up to next?
		if(amtToBeTaxed.compareTo(uscRateTable[idx][2].subtract(runningTotalInBand))<0){
			chunk = amtToBeTaxed; 
			runningTotalInBand = runningTotalInBand.add(chunk);
			usc= chunk.multiply(rate);
		}else{
			chunk = uscRateTable[idx][2].subtract(runningTotalInBand);
			usc= chunk.multiply(rate);
			idx = new Integer(idx+1);
			runningTotalInBand = BigDecimal.ZERO;
		}		 
		runningTotalGross = runningTotalGross.add(chunk);
		amtToBeTaxed = amtToBeTaxed.subtract(chunk);
		if(amtToBeTaxed.compareTo(BigDecimal.ZERO)>0){
			Tuple3<BigDecimal, Integer, BigDecimal> recurRes = calcUscRecur(runningTotalGross, amtToBeTaxed, uscRateTable, idx, runningTotalInBand);
			idx=recurRes.getT2();
			runningTotalInBand=recurRes.getT3();
			usc = usc.add( recurRes.getT1());
		}
		return new Tuple3<BigDecimal, Integer, BigDecimal>(usc, idx, runningTotalInBand);
	}	
	
	public BigDecimal calcUscGeneral2Loop(Salary[] sals){

		BigDecimal[][] uscRateTable = null;
		BigDecimal ret = BigDecimal.ZERO;
		int idx=0;
		BigDecimal gross = BigDecimal.ZERO;
		boolean partiallyFilled = false;
		BigDecimal thisBand = BigDecimal.ZERO, lastBand = BigDecimal.ZERO;
		for(int i=0;i<sals.length;i++){ 
			Salary s = sals[i];
			// Must differentiate between S class and PAYE since no higher rate for PAYE
			if(s.getPrsiClass()==Prsi.S){
				uscRateTable = uscRatesS;
			}else{
				uscRateTable = uscRates;
			}
			gross = gross.add(s.getGross());
			// use this idx as a counter to keep track of where to start usc rates for multiple employments.
			int lim=idx;
			// thisBand is used to subtract later on, so only out-of band amount is included at higher rate.
			BigDecimal thisPortion = BigDecimal.ZERO;
			while(lim<uscRateTable.length-1 && gross.compareTo(uscRateTable[lim][1])>0){
				thisBand = uscRateTable[lim][1].subtract(lastBand);
				if(gross.subtract(s.getGross()).compareTo(thisBand)>0){
					if(partiallyFilled)				
						thisPortion = thisPortion.add(uscRateTable[lim][1].subtract(gross.subtract(s.getGross())).multiply(uscRateTable[lim][0]));
					else
						thisPortion = thisPortion.add(thisBand.multiply(uscRateTable[lim][0]));
				}else{
					if(partiallyFilled)				
						thisPortion = thisPortion.add(thisBand.subtract(gross.subtract(s.getGross())).multiply(uscRateTable[lim][0]));
					else
						thisPortion = thisPortion.add(thisBand.multiply(uscRateTable[lim][0]));
				}
				partiallyFilled=false;
				lastBand = uscRateTable[lim][1];
				lim++;
			}
			BigDecimal bal = null;
			if(lim>0 && lim>uscRateTable.length-1){
				bal = gross.subtract(uscRateTable[lim-1][1]);
				thisPortion = thisPortion.add(bal.multiply(uscRateTable[uscRateTable.length-1][0]));
			}else if(lim>0){
				if(partiallyFilled)
					bal = uscRateTable[lim][1].subtract(gross.subtract(s.getGross()));
				else
					bal = gross.subtract(uscRateTable[lim-1][1]);
				thisPortion = thisPortion.add(bal.multiply(uscRateTable[lim][0]));
			}else{
				thisPortion = thisPortion.add(uscRateTable[lim][0].multiply(s.getGross()));
			}
			partiallyFilled=true;
			thisBand = thisBand.add(s.getGross());
			s.setUsc(thisPortion);	
			idx=lim;
			ret = ret.add(thisPortion);
		}
		return ret;		
	}
	
	private BigDecimal calcUscGeneral2(Salary[] sals, BigDecimal[][] uscRateTable){
		BigDecimal ret = BigDecimal.ZERO;
		int idx=0;
		BigDecimal gross = BigDecimal.ZERO;
		for(int i=0;i<sals.length;i++){ 
			Salary s = sals[i];
			// Must differentiate between S class and PAYE since no higher rate for PAYE
			if(s.getPrsiClass()==Prsi.S){
				uscRateTable = uscRatesS;
			}else{
				uscRateTable = uscRates;
			}
			gross = gross.add(s.getGross());
			// use this idx as a counter to keep track of where to start usc rates for multiple employments.
			int lim=idx;
			// thisBand is used to subtract later on, so only out-of band amount is included at higher rate. 
			BigDecimal thisPortion = BigDecimal.ZERO, thisBand = BigDecimal.ZERO;
			while(lim<uscRateTable.length-1 && gross.compareTo(uscRateTable[lim][1])>0){
				thisPortion = thisPortion.add(uscRateTable[lim][3]);
				thisBand = uscRateTable[lim][1];
				lim++;
			}
			if(lim>0 && lim>uscRateTable.length-1){
				thisPortion = thisPortion.add(s.getGross().subtract(thisBand).multiply(uscRateTable[uscRateTable.length-1][0]));
			}else if(lim>0){
				thisPortion = thisPortion.add(s.getGross().subtract(thisBand).multiply(uscRateTable[lim][0]));
			}else{
				thisPortion = thisPortion.add(uscRateTable[lim][0].multiply(s.getGross()));
			}
			thisBand = thisBand.add(s.getGross());
			s.setUsc(thisPortion);	
			idx=lim;
			ret = ret.add(thisPortion);
		}
		return ret;		
	}
	
	public Salary calcTaxCredits(Prsi prsiClass, MaritalTaxStatus maritalTaxStatus, boolean medicalCard){
		return calcTaxCredits(prsiClass, maritalTaxStatus, medicalCard, bd("0"));
	}

	/**
	 * Sources of information
	 * StdRate Cutoff: http://www.citizensinformation.ie/en/money_and_tax/tax/income_tax/how_your_tax_is_calculated.html#l2729c
	 * 
	 * @param prsiClass
	 * @param maritalTaxStatus
	 * @param medicalCard
	 * @return
	 */
	public Salary calcTaxCredits(@NotNull Prsi prsiClass, @NotNull MaritalTaxStatus maritalTaxStatus, boolean medicalCard, @NotNull BigDecimal salary2) {
		Salary ret = Salary.builder().build();
		
		//
		// Tax Credits
		//
		switch (prsiClass){
			case S:
				ret.setTaxCredits(TaxCreditsS);
				break;
			case A:
				ret.setTaxCredits(TaxCreditsA);
				break;
			default:
				ret.setTaxCredits(TaxCreditsD);
		}
		
		//
		// Std Rate Cutoff
		//
		switch (maritalTaxStatus){
			case Single:
				ret.setStdRateCutoff(StdRateCutOffSingle);
				break;
			case SingleParent:
				ret.setStdRateCutoff(StdRateCutOffSingleParent);
				break;
			case Married1Income:
				ret.setStdRateCutoff(StdRateCutOffMarried1Income);
				break;
			case Married2IncomesSingleAssesment:
				ret.setStdRateCutoff(StdRateCutOffSingle);
				break;
			case Married2IncomesJointAssesment:
				BigDecimal additional = Utils.min(StdRateCutOffMarried2IncomeDiff, salary2);
				ret.setStdRateCutoff(StdRateCutOffMarried1Income.add(additional));
				break;
		}
		// Reduced USC? if total salary less than 60000, AND either (medicalCardHolder  or older than 70)
		if(medicalCard)
			ret.setEligbleForReducedUsc(true);
		
		return ret;
		
	}
	
	
	public Tuple2<Salary, Salary[]> compareNetSol(Salary before) {
		return compareNetSol(before, Salary.builder().gross(MaxSal).build());
	}
	
	/**
	 * TODO:  Probably needs re-work. This is a simple version that makes a lot of assumptions, e.g. TaxCredits etc
	 * @param sEmp
	 * @param sNetSols
	 * @return
	 */
	public Tuple2<Salary, Salary[]> compareNetSol(Salary sEmp, Salary sNetSols) {
		Salary sEmpBase = Salary.builder().stdRateCutoff(bd("33800"))
				.taxCredits(bd("1650"))
				.prsiClass(Prsi.S)
				.gross(sEmp.getGross().subtract(sNetSols.getGross()))
				.build();
		return compareNetSol(sEmp, sNetSols, sEmpBase);
	}
	
	/**
	 * Probably needs re-work
	 * This is a simple version that makes a lot of assumptions, e.g. TaxCredits etc
	 * @param sEmp
	 * @param sNetSols
	 * @return
	 */
	public Tuple2<Salary, Salary[]> compareNetSol(Salary sEmp, Salary sNetSols, Salary sEmpBase) {		
		sNetSols.setTaxCredits(bd("1650"));
		sNetSols.setPrsiClass(Prsi.A);
		sEmp.setTaxCredits(bd("1650"));
		sEmp.setStdRateCutoff(sEmpBase.getStdRateCutoff());
		sEmp.setTaxCredits(bd("1650")); // Extra tax credits since now getting PAYE tax credits
		sEmp.setPrsiClass(Prsi.S);
		Salary ret1 = calcSalary(sEmp);				
		sEmpBase.setGross(sEmp.getGross().subtract(sNetSols.getGross()));
		sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getPayeAtLowerRate()));
		Salary[] netSolSal = new Salary[]{sEmpBase, sNetSols};
		calcSalary(netSolSal);
		return new Tuple2<Salary, Salary[]>(ret1, netSolSal);		
	}

	/**
	 * Loops through salary split to find max Net gain. Allows netSolTake to be passed in. If not set it will split evenly
	 * Net Gain is grossGain minus netSolTake
	 * @param startSal
	 * @param netSolTake
	 * @return
	 */
	public Map<String, BigDecimal> getBestDeal(Salary startSal, BigDecimal netSolTake){
		BigDecimal netSolStart = bd(5000), maxGain= BigDecimal.ZERO, maxSal = BigDecimal.ZERO, sEmpMax = BigDecimal.ZERO;
		BigDecimal storeNetBefore = BigDecimal.ZERO, storeNetAfterIncFee = BigDecimal.ZERO;
		Salary sNetSols = Salary.builder().build(), sEmpBase = Salary.builder().build();
		sEmpBase.setStdRateCutoff(startSal.getStdRateCutoff());
		sEmpBase.setTaxCredits(startSal.getTaxCredits());
		sEmpBase.setPrsiClass(startSal.getPrsiClass());
		sNetSols.setPrsiClass(Prsi.A);
		log.info("Assuming NetSol Salary is class A");
		sNetSols.setTaxCredits(bd("1650")); // Extra tax credits since now getting PAYE tax credits
		log.info("Assuming NetSol Salary can claim 1650 extra in tax credits as PAYE employee");		
		for(int i=0;i<100;i++){
			sNetSols.setGross(netSolStart.add(bd(i).multiply(bd("100")))); 
			sEmpBase.setGross(startSal.getGross().subtract(sNetSols.getGross()));
			calcSalary(startSal);
			sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getPayeAtLowerRate()));
			Salary[] netSolSal = new Salary[]{sEmpBase, sNetSols};
			calcSalary(netSolSal);
			sNetSols = netSolSal[1];
			sEmpBase= netSolSal[0];
			BigDecimal grossGain = sNetSols.getNet().add(sEmpBase.getNet()).subtract(startSal.getNet());
			if(netSolTake==null)
				netSolTake = grossGain.subtract(sNetSols.getEmployersPrsi()).divide(bd("2"), MathContext.DECIMAL64).add(sNetSols.getEmployersPrsi());
			BigDecimal netGain = grossGain.subtract(netSolTake);
			BigDecimal netBefore = startSal.getNet(); //add(Salary.Net, sEmpBase.net.negate() );
			BigDecimal netAfterIncFee = sNetSols.getNet().add(sEmpBase.getNet()).subtract(netSolTake);
			if(maxGain.compareTo(netGain)<0){
				maxGain=netGain;
				maxSal=sNetSols.getGross();
				sEmpMax = sEmpBase.getGross();
				storeNetBefore = netBefore;
				storeNetAfterIncFee = netAfterIncFee;
			}
		}
		HashMap<String, BigDecimal> m = new HashMap<>();
		m.put("GrossSal", startSal.getGross());
		m.put("netSolTake", netSolTake);
		m.put("NetSolSalary", maxSal);
		m.put("NetSolCharge", maxSal.add(netSolTake));
		m.put("OldNetSalary", storeNetBefore);
		m.put("NewNetSalary", storeNetAfterIncFee);
		return m;
	}
	

}
