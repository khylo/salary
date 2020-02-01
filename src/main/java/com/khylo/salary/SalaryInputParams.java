package com.khylo.salary;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class SalaryInputParams {
	public static final int MaxNum = 1000000;
	public static final int MinNum = 0;

	@Max(MaxNum)
	@Min(MinNum)
	protected BigDecimal gross;
	Prsi prsiClass;
	protected boolean eligbleForReducedUsc; // See IESalarySErvice.calcTaxCredits for eligibilty rules 
	@Max(MaxNum)
	@Min(MinNum)
	protected BigDecimal stdRateCutoff;
	@Max(MaxNum)
	@Min(MinNum)
	protected BigDecimal taxCredits;
	
	public Salary toSalary(){
		return Salary.builder()
				.gross(this.gross)
				.prsiClass(this.prsiClass)
				.stdRateCutoff(this.stdRateCutoff)
				.taxCredits(this.taxCredits)
				.build();
	}


}
