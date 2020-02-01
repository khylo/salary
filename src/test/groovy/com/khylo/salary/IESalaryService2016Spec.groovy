package com.khylo.salary

import com.khylo.salary.calc.IESalaryService2015
import com.khylo.salary.calc.IESalaryService2016

import static com.khylo.common.Utils.bd

import java.math.MathContext

import spock.lang.Unroll

import com.khylo.common.Tuple3

class IESalaryService2016Spec extends spock.lang.Specification{
	
	@Unroll("testUscTable for Col = #i Row = #j")
	public void testUscTable() {
		when:
			def exp = bd(0);
			if(a==1)
				exp = IESalaryService2016.uscRates[i][j]
			
		then:
			 exp == bd(val)		
							
		when:
			exp = bd(0);
			if(s==1)
				exp = IESalaryService2016.uscRatesS[i][j]
			
		then:
			exp == bd(valS)
	
		where:
		//usc[x][0] = %Rate 
		//usc[x][1] = Gros slaary this rate is changable over
		//usc[x][2] = Actual gross amount taxable in band
		//usc[x][3] = Total amount of usc taxin band
		i | j | a | s | val       | valS 
		0 | 2 | 1 | 1 | 12012	  | 12012	  
		1 | 2 | 1 | 1 | 6656      | 6656      
		2 | 2 | 1 | 1 | 51376     | 51376     
		3 | 2 | 1 | 1 | 99929955  | 29956
		4 | 2 | 0 | 1 | 0		  | 99899999  
		0 | 3 | 1 | 1 | "120.12"  | "120.12" 
		1 | 3 | 1 | 1 | "199.68"  | "199.68" 
		2 | 3 | 1 | 1 | "2825.68" | "2825.68"
		3 | 3 | 1 | 1 | "0"       | "2396.48"   // paye usc does nto go above 8%, so this value is hardcoded to zero
		/*		
		assertThat(IESalaryService2016.uscRatesS[0][2], new BigDecimalCloseTo(bd("12012"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[1][2], new BigDecimalCloseTo(bd("5564"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[2][2], new BigDecimalCloseTo(bd("52468"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[3][2], new BigDecimalCloseTo(bd("29956"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[4][2], new BigDecimalCloseTo(bd("99899999"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[0][3], new BigDecimalCloseTo(bd("180.18"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[1][3], new BigDecimalCloseTo(bd("194.74"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[2][3], new BigDecimalCloseTo(bd("3672.76"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[3][3], new BigDecimalCloseTo(bd("2396.48"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.uscRatesS[4][3], new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		assertThat(IESalaryService2016.prsiRates[0][2], new BigDecimalCloseTo(bd("18304"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.prsiRates[1][2], new BigDecimalCloseTo(bd("18512"), BigDecimal.ZERO));
		assertThat(IESalaryService2016.prsiRates[2][2], new BigDecimalCloseTo(bd("26000"), BigDecimal.ZERO));

		assertThat(IESalaryService2016.prsiRatesS[0][2], new BigDecimalCloseTo(bd("26000"), BigDecimal.ZERO));*/
	}

	@Unroll("testCalcUsc for Gross #gross and prsi #prsi")
	public void testCalcUsc() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
			Salary s = new Salary();
			s.gross=bd(gross);
			s.prsiClass=prsi;
			BigDecimal res = service.calcUsc(s);
			
		expect:
			res == bd(usc)

		where:
			gross    | prsi 	| usc
			10000 	 | Prsi.A1	| "0"
			13000	 | Prsi.A1	| "0"
			13001	 | Prsi.A1	| "149.79"
			20000	 | Prsi.A1	| "393.06" // was "544.6" in 2015
			50000	 | Prsi.A1	| "2043.06" // was "2644.6"
			70044	 | Prsi.A1	| "3145.48" // was "4047.68"
			75000	 | Prsi.A1	| "3541.96"  // was "4444.16"
			100000	 | Prsi.A1	| "5541.96" //was "6444.16"
			120000	 | Prsi.A1	| "7141.96" // "8044.16"
			13000	 | Prsi.S	| "0" // was "214.76"
			20000	 | Prsi.S	| "393.06" // was "544.6" in 2015
			50000	 | Prsi.S	| "2043.06" // was "2644.6"
			70044	 | Prsi.S	| "3145.48" // was "4047.68"
			75000	 | Prsi.S	| "3541.96"  // was "4444.16"
			100000	 | Prsi.S	| "5541.96" //was "6444.16"
			120000	 | Prsi.S	| "7741.96" //"8644.16"
	}
	
	@Unroll("testCalcPrsiNew Prsi changes for Gross #weekly and prsi #prsi")
	public void testCalcPrsiNew() {
		given:
			IESalaryService2016 s2016 = new IESalaryService2016();
			IESalaryService2015 s2015 = new IESalaryService2015();
			Salary s = new Salary();
			s.gross=bd(weekly).multiply(52);
			s.prsiClass=Prsi.A1;
			BigDecimal res2016 = s2016.calcPrsi(s).t1.divide(bd(52), MathContext.DECIMAL64 ).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal res2015 = s2015.calcPrsi(s).t1.divide(bd(52), MathContext.DECIMAL64 ).setScale(2, BigDecimal.ROUND_HALF_UP);;
			s.setPrsiClass(Prsi.S)
			BigDecimal res2016S = s2016.calcPrsi(s).t1.divide(bd(52), MathContext.DECIMAL64 ).setScale(2, BigDecimal.ROUND_HALF_UP);;
			
		expect:
			res2016 == bd(prsi)
			res2015 == bd(oldRate)
			res2016S== bd(prsiS)

		where:
		// this table is copied from http://www.welfare.ie/en/Pages/Advance-Notice-of-PRSI-Changes-For-Computer-Users-2016.aspx#prsicredit we are not using all data
			weekly		| excess352	| one6th	| maxPrsi	|prsiCredit	| oldRate | prsi   | prsiS   
			"250"       | 	"0.00"	| 	"0.00"	| 	"12.00"	| 	"0"	    | "0"     |  "0"   | "10"     //13000 per year
			"352"       | 	"0.00"	| 	"0.00"	| 	"12.00"	| 	"0"	    | "0"     |  "0"   | "14.08"     //18304 per year
			"352.01"	| 	"0.00"	| 	"0.00"	| 	"12.00"	| 	"12.00"	| "14.08" | "2.08" | "14.08"
			"355.00"	| 	"2.99"	| 	"0.50"	| 	"12.00"	| 	"11.50"	| "14.20" | "2.70" | "14.20"
			"360.00"	| 	"7.99"	| 	"1.33"	| 	"12.00"	| 	"10.67"	| "14.40" | "3.73" | "14.40"
			"365.00"	| 	"12.99"	| 	"2.17"	| 	"12.00"	| 	"9.84"	| "14.60" | "4.77" | "14.60"
			"370.00"	| 	"17.99"	| 	"3.00"	| 	"12.00"	| 	"9.00"	| "14.80" | "5.80" | "14.80"
			"375.00"	| 	"22.89"	| 	"3.83"	| 	"12.00"	| 	"8.17"	| "15.00" | "6.83" | "15.00"
			"380.00"	| 	"27.99"	| 	"4.67"	| 	"12.00"	| 	"7.34"	| "15.20" | "7.87" | "15.20"
			"385.00"	| 	"32.99"	| 	"5.50"	| 	"12.00"	| 	"6.50"	| "15.40" | "8.90" | "15.40"
			"390.00"	| 	"37.99"	| 	"6.33"	| 	"12.00"	| 	"5.67"	| "15.60" | "9.93" | "15.60"
			"395.00"	| 	"42.99"	| 	"7.17"	| 	"12.00"	| 	"4.84"	| "15.80" | "10.97"| "15.80"
			"400.00"	| 	"47.99"	| 	"8.00"	| 	"12.00"	| 	"4.00"	| "16.00" | "12.00"| "16.00"
			"405.00"	| 	"52.99"	| 	"8.83"	| 	"12.00"	| 	"3.17"	| "16.20" | "13.03"| "16.20"
			"410.00"	| 	"57.99"	| 	"9.67"	| 	"12.00"	| 	"2.34"	| "16.40" | "14.07"| "16.40"
			"415.00"	| 	"62.99"	| 	"10.50"	| 	"12.00"	| 	"1.50"	| "16.60" | "15.10"| "16.60"
			"420.00"	| 	"67.99"	| 	"11.33"	| 	"12.00"	| 	"0.67"	| "16.80" | "16.13"| "16.80"
			"424.00"	| 	"71.99"	| 	"12.00"	| 	"12.00"	| 	"0.00"	| "16.96" | "16.96"| "16.96"
			"377.00"	| 	"00.00"	| 	"0"  	| 	"12.00"	| 	"0"		| "15.08" | "7.25" | "15.08"
	}
	
	@Unroll("testCalcPrsi for Gross #gross and prsi #prsi")
	public void testCalcPrsi() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
			SalaryInputParams s = new SalaryInputParams();
			s.gross=bd(gross);
			s.prsiClass=prsiCls;
			BigDecimal res = service.calcPrsi(s).t1;
			
		expect:
			res == bd(prsi) //.setScale(2, RoundingMode.CEILING)
		where:
			gross    | prsiCls 	| prsi
			19604 	 | Prsi.A1	| "376.74"   // example from welfare.ie (377 per week.. ironically works out at 377 per year) 	
			10000 	 | Prsi.A1	| "0" 
			13000	 | Prsi.A1	| "0"
			13001	 | Prsi.A1	| "0"
			18304	 | Prsi.A1	| "0"
			18305	 | Prsi.A1	| "108.28"
			18305	 | Prsi.S	| "732.2"
			20000	 | Prsi.A1	| "458.58" // was "544.6" in 2015
			20000	 | Prsi.S	| "800"
			50000	 | Prsi.A1	| "2000" // 
			70044	 | Prsi.A1	| "2801.76"
			75000	 | Prsi.A1	| "3000"  
			100000	 | Prsi.A1	| "4000" 
			120000	 | Prsi.A1	| "4800"
			13000	 | Prsi.S	| "520" 
			20000	 | Prsi.S	| "800" 
			50000	 | Prsi.S	| "2000" 
			70044	 | Prsi.S	| "2801.76"  
			75000	 | Prsi.S	| "3000"     
			100000	 | Prsi.S	| "4000"     
			120000	 | Prsi.S	| "4800" 
	}
	
	@Unroll("testCalcPaye for Gross #gross and taxCredit #credits, stdRateCutoff #stdRC")
	public void testCalcPaye() {
		
	given:
		IESalaryService2016 service = new IESalaryService2016();
		Salary s = new Salary();
		s.setStdRateCutoff(bd(stdRC));
		s.setTaxCredits(bd(credits));
		s.setGross(bd(gross));
		s.setPrsiClass(pClass);
		Tuple3<BigDecimal, BigDecimal, BigDecimal> tup = service.calcPaye(s);
		BigDecimal res = tup.t1
		BigDecimal payeL = tup.t2
		BigDecimal payeH = tup.t3
				
	expect:
		res == bd(paye)
		println "PYE = $res, low  = $low ($payeL), high = $high ($payeH)"
		bd(low) == payeL
		bd(high) == payeH

	where:
		gross | pClass | stdRC |credits| paye | low  | high
		4125  | Prsi.A1| 33800 | 1650  | 0    |   0	 | 0
		8250  | Prsi.A1| 0     | 3300  | 0    |   0	 | 0
		10000 | Prsi.A1| 33800 | 1650  |350   | 350  | 0
		10000 | Prsi.A1|  0    | 1650  |2350  | 0    | 2350
		34000 | Prsi.A1| 33800 | 1650  |5190  | 5110 | 80
		34000 | Prsi.S | 33800 | 1650  |5190  | 5110 | 80
		60000 | Prsi.A1| 33800 | 1650  |15590 | 5110 | 10480
		60000 | Prsi.A1| 33800 | 3300  |13940 | 3460 | 10480
	}
	
	
	/*@Unroll("testCalcUscWith2Salaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2)")
	public void testSalaryPaymentsForFixedSalary() {
	given:
		Employee e = (new EmployeeBuilder().withGross(salary).withPrsiClass(pClass).withStdRateCutoff(stdCutoff).withTaxCredits(credits)).build();
		SalaryPayment sp = new SalaryPaymentBuilder().copy(e.toSalaryPayment()).withAcuGross(s2Date).build();
		IESalaryService2016 service = new IESalaryService2016();
		Salary sal1 = new Salary();
		Salary sal2 = new Salary();
		sal1.gross=bd(salary1);
		sal1.prsiClass=prsi1
		sal2.gross=bd(salary2);
		sal2.prsiClass=prsi2
		Salary[] sa = [sal1, sal2];
		SalaryPayment res = service.calcSalaryPayment()
		
	expect:
		res.gross == bd(expGross)

	where:
		salary      | pClass | stdCutoff  | credits   |s2Date     |startMonth      | expGross
		bd("32800") | Prsi.A1|bd("33800") | bd("1650")|bd("0")	  | Month.JANUARY  | bd("25000")
		bd("32800") | Prsi.A1|bd("33800") | bd("1650")|bd("8200") | Month.JANUARY  | bd("25000")
		bd("32800") | Prsi.A1|bd("33800") | bd("1650")|bd("0")	  | Month.JUNE     | bd("25000")
		bd("32800") | Prsi.A1|bd("33800") | bd("1650")|bd("8200") | Month.JUNE     | bd("25000")
		bd("33800") | Prsi.S |bd("33800") | bd("1650")|bd("0")    | Month.JANUARY  | bd("25000")
	}*/
	
	@Unroll("testCalcUscWith2Salaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2)")
	public void testCalcUscWith2Salaries() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
			Salary sal1 = new Salary();
			Salary sal2 = new Salary();
			sal1.gross=bd(salary1);
			sal1.prsiClass=prsi1
			sal2.gross=bd(salary2);
			sal2.prsiClass=prsi2
			Salary[] sa = [sal1, sal2];
			service.addUsc(sa);
			BigDecimal res = sa[0].getUsc().add(sa[1].getUsc());
			
		expect:
			res == bd(usc)

		where:
			salary1 | prsi1 	| salary2 | prsi2 	| usc
			10000 	| Prsi.A1	| 3000    | Prsi.A1	|"0" // was "214.76"
			7000	| Prsi.A1	| 13000	  | Prsi.A1	|"393.06" // was "544.6"
			100		| Prsi.A1	| 49900	  | Prsi.A1	|"2043.06" // was "2644.6"
			70000	| Prsi.A1	| 44	  | Prsi.A1	|"3145.48" // was "4047.68"
			5000	| Prsi.A1	| 70000	  | Prsi.A1	|"3541.96" // was "4444.16"
			50000	| Prsi.A1	| 50000	  | Prsi.A1	|"5541.96" // was "6444.16"
			100000	| Prsi.A1	| 20000	  | Prsi.A1	|"7141.96" // was "8044.16"
			120000 	| Prsi.S	| 60000   | Prsi.A1	|"12541.96" // was "13444.16"  (20k at 11% 160k at 8%)
			7000	| Prsi.S	| 13000	  | Prsi.A1	|"393.06" /// was "544.6"
			100		| Prsi.S	| 49900	  | Prsi.A1	|"2043.06" // was "2644.6"
			44		| Prsi.S	| 70000	  | Prsi.A1	|"3145.48" // was "4047.68"
			70000	| Prsi.S	| 5000	  | Prsi.A1	|"3541.96" // was "4444.16"
			50000	| Prsi.S	| 50000	  | Prsi.A1	|"5541.96" // was "6444.16"
			20000	| Prsi.S	| 100000  | Prsi.A1	|"7141.96" // was "8044.16"
            120000 	| Prsi.S	| 60000   | Prsi.S  |"14341.96"
			
	}
	
	@Unroll("calcUscWithMultipleSalaries: Sal1(#salary1, #prsi1) and Sal2(#salary2, #prsi2),  Sal3(#salary3, #prsi3) and Sal4(#salary4, #prsi4)")
	public void calcUscWithMultipleSalaries() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
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
			120000 	| Prsi.S	| 30000   | Prsi.A1	| 30000  | Prsi.A1	| null    | null	|"12541.96" // was "13444.16"
			100000 	| Prsi.S	| 30000   | Prsi.A1	| 30000  | Prsi.A1	| 20000   | Prsi.A1	|"11941.96" // was "13444.16"
			60000	| Prsi.S	| 60000	  | Prsi.S	| 30000	 | Prsi.A1	| 30000	  | Prsi.A1 |"12541.96" // new  (20k at 11%, 160k at classA (since 120k at class S , so only 20k at 11%))
			60000	| Prsi.S	| 60000	  | Prsi.S	| 30000	 | Prsi.A1	| 30000	  | Prsi.S  |"13441.96" // new  (50k at 11% 130K at class A (since 150k at class S , so only 50k over 11%)
			60000	| Prsi.S	| 60000	  | Prsi.S	| 30000	 | Prsi.S	| 30000	  | Prsi.S  |"14341.96"
			
	}
	
	/*@Unroll("calcSalaryFromEmployee: gross=#gross, prsi=#prsiCls stdRateCutoff=#cutoff Taxcredits=#credits")
	public void calcSalaryFromEmployee() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
			Employee e = new Employee(bd(gross), prsiCls, redUsc, bd(cutoff), bd(credits));
			Salary res = service.calcSalary(e);
			
		expect:
			res.paye == paye
			res.prsi == prsi
			res.usc == usc
			res.net == net
			
		where:  // values cal'ed
			gross  | prsiCls| redUsc | cutoff |credits| paye  | prsi  |  usc   | net    	
			120000 | Prsi.S	| false	 | 33800  | 1650  | 39590 | 4800  | 7741.96| 67868.04  // was 67565.84 // see http://ie.thesalarycalculator.co.uk/salary.php
			120000 | Prsi.S	| false	 | 33800  | 2200  | 39040 | 4800  | 7741.96| 68418.04  // 
			120000 | Prsi.A | false	 | 33800  | 3300  | 37940 | 4800  | 7141.96| 70118.04 // was 69215.84 
			60000  | Prsi.A | false	 | 33800  | 3300  | 13940 | 2400  | 2593.06| 41066.94 // was 40315.4
			60000  | Prsi.A	| false	 | 42800  | 3300  | 12140 | 2400  | 2593.06| 42866.94 // was 42115.4
			60000  | Prsi.A	| false	 | 33800  | 3300  | 8700  | 2400  | 2593.06| 46306.94 // was 45555.4
			60000  | Prsi.A	| false	 | 33800  | 1650  | 8700  | 2400  | 2593.06| 46306.94 // was 45555.4				
			 
	}*/
	/*
	def test(){
		given:
			IESalaryService2016 service = new IESalaryService2016();
			Salary s = new Salary();
			s.gross=bd("120000");
			s.prsiClass=Prsi.S;
			Salary paye = new Salary();
			paye.gross=bd("30000");
			paye.prsiClass=Prsi.A;
			Salary[] sa = (Salary[])([s, paye, paye])
			sa = service.addUsc(sa);
			BigDecimal res = sa[0].getUsc();
			assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
			
			Salary paye2 = new Salary();
			paye2.gross=bd("30000");
			paye2.prsiClass=Prsi.A;
			sa = (Salary[])([s, paye, paye2])
			sa = service.addUsc(sa);
			res = sa[0].getUsc();
			assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
			
			s.gross=bd("60000");
			sa = (Salary[])([s, s, paye, paye]);
			sa = service.addUsc(sa);
			res = sa[0].getUsc();
			assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
			
			// Test reduced Usc Rate	First standard usc if earning 60000
			sa = service.addUsc(sa);
			res = sa[0].getUsc();
			assertThat(res, new BigDecimalCloseTo(bd("3344.6"), BigDecimal.ZERO));
			
			//Now compare with reduced Usc
			s.eligbleForReducedUsc=true;
			sa = (Salary[])([s]);
			sa = service.addUsc(sa);
			res = sa[0].getUsc();
			assertThat(res, new BigDecimalCloseTo(bd("1859.76"), BigDecimal.ZERO));
		
	}*/
	

	def testGetBestDeal() {
		given:
			IESalaryService2016 service = new IESalaryService2016();
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
