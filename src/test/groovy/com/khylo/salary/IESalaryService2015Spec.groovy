package com.khylo.salary

import com.khylo.salary.calc.IESalaryService2015

import static com.khylo.common.Utils.bd
import static org.hamcrest.MatcherAssert.assertThat

import java.time.Month;

import org.hamcrest.number.BigDecimalCloseTo

import spock.lang.Unroll


class IESalaryService2015Spec extends spock.lang.Specification{

	@Unroll("testCalcUsc for Gross #gross and prsi #prsi")
	public void testCalcUsc() {
		given:
		IESalaryService2015 service = new IESalaryService2015();
			Salary s = new Salary();
			s.gross=bd(gross);
			s.prsiClass=prsi;
			BigDecimal res = service.calcUsc(s);
			
		expect:
			res == bd(usc)

		where:
			gross    | prsi 	| usc
			10000 	 | Prsi.A1	| "150"
			13000	 | Prsi.A1	| "214.76"
			20000	 | Prsi.A1	| "544.6"
			50000	 | Prsi.A1	| "2644.6"
			70044	 | Prsi.A1	| "4047.68"
			75000	 | Prsi.A1	| "4444.16"
			100000	 | Prsi.A1	| "6444.16"
			120000	 | Prsi.A1	| "8044.16"
			13000	 | Prsi.S	| "214.76"
			20000	 | Prsi.S	| "544.6"
			50000	 | Prsi.S	| "2644.6"
			70044	 | Prsi.S	| "4047.68"
			75000	 | Prsi.S	| "4444.16"
			100000	 | Prsi.S	| "6444.16"
			120000	 | Prsi.S	| "8644.16"
	}
	
	/*@Unroll("testCalcUscWith2Salaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2)")
	public void testSalaryPaymentsForFixedSalary() {
		given:
		//Employee e = new EmployeeBuilder().withGross(salary).withPrsiClass(pClass).withStdRateCutoff(stdCutoff).withTaxCredits(credits).build();
		//SalaryPayment sp = new SalaryPaymentBuilder().copy(e.toSalaryPayment()).withAcuGross(s2Date);
		IESalaryService2015 service = new IESalaryService2015();
		Salary sal1 = new Salary();
		Salary sal2 = new Salary();
		sal1.gross=bd(salary1);
		sal1.prsiClass=pClass1
		sal2.gross=bd(salary2);
		sal2.prsiClass=pClass2
		Salary[] sa = [sal1, sal2];
		SalaryPayment res = service.calcSalaryPayment()
		
	expect:
		res.gross == bd(expGross)

	where:
		salary1 | pClass1 | salary2 | pClass2 |s2Date     |startMonth      | expGross
		"32800" | Prsi.A1 |"33800"  | Prsi.A1 |bd("0")	  | Month.JANUARY  | "25000"
		"32800" | Prsi.A1 |"33800"  | Prsi.S  |bd("8200") | Month.JANUARY  | "25000"
		"32800" | Prsi.A1 |"33800"  | Prsi.S  |bd("0")	  | Month.JUNE     | "25000"
		"32800" | Prsi.A1 |"33800"  | Prsi.S  |bd("8200") | Month.JUNE     | "25000"
		"33800" | Prsi.S  |"33800"  | Prsi.S  |bd("0")    | Month.JANUARY  | "25000"
	}*/
	
	@Unroll("testCalcUscWith2Salaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2)")
	public void testCalcUscWith2Salaries() {
		given:
			IESalaryService2015 service = new IESalaryService2015();
			Salary sal1 = new Salary();
			Salary sal2 = new Salary();
			sal1.gross=bd(salary1);
			sal1.prsiClass=prsi1
			sal2.gross=bd(salary2);
			sal2.prsiClass=prsi2
			Salary[] sa = [sal1, sal2];
			service.addUsc(sa);
			BigDecimal res = sa[0].getUsc().add(sa[1].getUsc())
			
		expect:
			res == bd(usc)

		where:
			salary1 | prsi1 	| salary2 | prsi2 	| usc
			10000 	| Prsi.A1	| 3000    | Prsi.A1	|"214.76"
			7000	| Prsi.A1	| 13000	  | Prsi.A1	|"544.6"
			100		| Prsi.A1	| 49900	  | Prsi.A1	|"2644.6"
			70000	| Prsi.A1	| 44	  | Prsi.A1	|"4047.68"
			5000	| Prsi.A1	| 70000	  | Prsi.A1	|"4444.16"
			50000	| Prsi.A1	| 50000	  | Prsi.A1	|"6444.16"
			100000	| Prsi.A1	| 20000	  | Prsi.A1	|"8044.16"
			120000 	| Prsi.S	| 60000   | Prsi.A1	|"13444.16"
			7000	| Prsi.S	| 13000	  | Prsi.A1	|"544.6"
			100		| Prsi.S	| 49900	  | Prsi.A1	|"2644.6"
			44		| Prsi.S	| 70000	  | Prsi.A1	|"4047.68"
			70000	| Prsi.S	| 5000	  | Prsi.A1	|"4444.16"
			50000	| Prsi.S	| 50000	  | Prsi.A1	|"6444.16"
			20000	| Prsi.S	| 100000  | Prsi.A1	|"8044.16"
			
	}
	
	@Unroll("calcUscWithMultipleSalaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2),  Sal3(#salary3, #prsi3) and Sal4(#salary4, #prsi4)")
	public void calcUscWithMultipleSalaries() {
		given:
			IESalaryService2015 service = new IESalaryService2015();
			Salary sal1 = new Salary();
			Salary sal2 = new Salary();
			Salary sal3 = new Salary();
			Salary sal4 = new Salary();
			sal1.gross=bd(salary1);
			sal1.prsiClass=prsi1
			sal2.gross=bd(salary2);
			sal2.prsiClass=prsi2
			if(salary3) {
				sal3.gross=bd(salary3);
				sal3.prsiClass=prsi3
			}
			if(salary4) {
				sal4.gross=bd(salary4);
				sal4.prsiClass=prsi4
			}
			Collection sa = [sal1, sal2];
			if(salary3) sa << sal3
			if(salary4) sa << sal4
			//println "sa = "+Utils.listToString(sa)
			service.addUsc(sa.toArray(new Salary[sa.size()]));
			BigDecimal res = BigDecimal.ZERO;
			for(int i=0;i<sa.size();i++){
				res = res.add(sa[i].getUsc());
			}
			
		expect:
			res == bd(usc)
			
		where:		
			salary1 | prsi1 	| salary2 | prsi2 	|salary3 | prsi3 	| salary4 | prsi4   | usc
			120000 	| Prsi.S	| 30000   | Prsi.A1	| 30000  | Prsi.A1	| null    | null	|"13444.16"
			60000	| Prsi.S	| 60000	  | Prsi.S	| 30000	 | Prsi.A1	| 30000	  | Prsi.A1 |"13444.16"
			60000	| Prsi.S	| 60000	  | Prsi.A1	| 30000	 | Prsi.A1	| 30000	  | Prsi.A1 |"12844.16"
	}
	
	/*@Unroll("calcSalaryFromEmployee: gross=#gross, prsi=#prsiCls stdRateCutoff=#cutoff Taxcredits=#credits")
	public void calcSalaryFromEmployee() {
		given:
			IESalaryService2015 service = new IESalaryService2015();
			Employee e = new Employee(bd(gross), prsiCls, redUsc, bd(cutoff), bd(credits));
			Salary res = service.calcSalary(e);
			
		expect:
			res.net == net
			res.paye == paye
			res.prsi == prsi
			res.usc == usc
			
		where:  // values cal'ed
			gross  | prsiCls| redUsc | cutoff |credits| paye  | prsi  |  usc   | net    	
			120000 | Prsi.S	| false	 | 33800  | 1650  | 39590 | 4800  | 8044.16| 67565.84 // see http://ie.thesalarycalculator.co.uk/salary.php
			120000 | Prsi.A | false	 | 33800  | 3300  | 37940 | 4800  | 8044.16| 69215.84 // see http://ie.thesalarycalculator.co.uk/salary.php
			60000  | Prsi.A | false	 | 33800  | 3300  | 13940 | 2400  | 3344.6 | 40315.4
			60000  | Prsi.A	| false	 | 42800  | 3300  | 12140 | 2400  | 3344.6 | 42115.4
			60000  | Prsi.A	| false	 | 67600  | 3300  | 8700  | 2400  | 3344.6 | 45555.4
	}*/
	
	

	def testGetBestDeal() {
		given:
			IESalaryService2015 service = new IESalaryService2015();
			Salary sal1 = new Salary();
			sal1.gross=bd(salary1);
			sal1.prsiClass = prsi1;
		expect:
			println service.getBestDeal(sal1, null);
		where:
			salary1 | prsi1 	
			50000 	| Prsi.S				
			75000	| Prsi.S
			100000	| Prsi.S
			125000	| Prsi.S
			150000	| Prsi.S
	}
	
	
}
