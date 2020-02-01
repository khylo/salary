package com.khylo.salary.calc;

import com.khylo.salary.MaritalTaxStatus;
import com.khylo.salary.Prsi;
import com.khylo.salary.Salary;
import com.khylo.salary.calc.IESalaryService2015;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.khylo.common.Utils.bd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IESalaryService2015Test {
	
	@Test
	public void testTable() {
		assertThat(IESalaryService2015.uscRates[0][2], new BigDecimalCloseTo(bd("12012"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[1][2], new BigDecimalCloseTo(bd("5564"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[2][2], new BigDecimalCloseTo(bd("52468"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[3][2], new BigDecimalCloseTo(bd("99929955"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[0][3], new BigDecimalCloseTo(bd("180.18"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[1][3], new BigDecimalCloseTo(bd("194.74"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[2][3], new BigDecimalCloseTo(bd("3672.76"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRates[3][3], new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		assertThat(IESalaryService2015.uscRatesS[0][2], new BigDecimalCloseTo(bd("12012"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[1][2], new BigDecimalCloseTo(bd("5564"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[2][2], new BigDecimalCloseTo(bd("52468"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[3][2], new BigDecimalCloseTo(bd("29956"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[4][2], new BigDecimalCloseTo(bd("99899999"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[0][3], new BigDecimalCloseTo(bd("180.18"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[1][3], new BigDecimalCloseTo(bd("194.74"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[2][3], new BigDecimalCloseTo(bd("3672.76"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[3][3], new BigDecimalCloseTo(bd("2396.48"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.uscRatesS[4][3], new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		assertThat(IESalaryService2015.prsiRates[0][2], new BigDecimalCloseTo(bd("18304"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.prsiRates[1][2], new BigDecimalCloseTo(bd("18512"), BigDecimal.ZERO));
		assertThat(IESalaryService2015.prsiRates[2][2], new BigDecimalCloseTo(bd("26000"), BigDecimal.ZERO));

		assertThat(IESalaryService2015.prsiRatesS[0][2], new BigDecimalCloseTo(bd("26000"), BigDecimal.ZERO));
	}

	@Test
	public void testCalcUsi() {
		Salary s = new Salary();
		s.setGross(bd("10000"));
		s.setPrsiClass(Prsi.A1);
		
		IESalaryService2015 service = new IESalaryService2015();
		BigDecimal res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("150"), BigDecimal.ZERO));
		
		s.setGross(bd("13000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("214.76"), BigDecimal.ZERO));
		
		s.setGross(bd("20000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("544.6"), BigDecimal.ZERO));
		
		s.setGross(bd("50000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("2644.6"), BigDecimal.ZERO));
		
		s.setGross(bd("70044"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("4047.68"), BigDecimal.ZERO));
		
		s.setGross(bd("75000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("4444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("100000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("6444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("120000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("8044.16"), BigDecimal.ZERO));
		
		// Test 2 salaries 
		Salary paye = new Salary();
		s.setGross(bd("10000"));
		paye.setGross(bd("3000"));
		Salary[] sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("214.76"), BigDecimal.ZERO));
		
		s.setGross(bd("7000"));
		paye.setGross(bd("13000"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("544.6"), BigDecimal.ZERO));
		
		s.setGross(bd("100"));
		paye.setGross(bd("49900"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("2644.6"), BigDecimal.ZERO));
		
		s.setGross(bd("70000"));
		paye.setGross(bd("44"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("4047.68"), BigDecimal.ZERO));
		
		s.setGross(bd("70000"));
		paye.setGross(bd("5000"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("4444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("50000"));
		paye.setGross(bd("50000"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("6444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("100000"));
		paye.setGross(bd("20000"));
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("8044.16"), BigDecimal.ZERO));
		
		// S class
		s.setPrsiClass(Prsi.S);
		s.setGross(bd("13000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("214.76"), BigDecimal.ZERO));
		
		s.setGross(bd("20000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("544.6"), BigDecimal.ZERO));
		
		s.setGross(bd("50000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("2644.6"), BigDecimal.ZERO));
		
		s.setGross(bd("70044"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("4047.68"), BigDecimal.ZERO));
		
		s.setGross(bd("75000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("4444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("100000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("6444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("120000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("8644.16"), BigDecimal.ZERO));
		
		// Test 2 salaries one S one paye		
		paye.setGross(bd("60000"));
		paye.setPrsiClass(Prsi.A);
		sa = new Salary[]{s, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
		
		paye = new Salary();
		paye.setGross(bd("30000"));
		paye.setPrsiClass(Prsi.A);
		sa = new Salary[]{s, paye, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
		
		s.setGross(bd("60000"));
		sa = new Salary[]{s, s, paye, paye};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("13444.16"), BigDecimal.ZERO));
		
		// Test reduced Usc Rate	First standard usc if earning 60000
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("3344.6"), BigDecimal.ZERO));
		
		//Now compare with reduced Usc
		s.setEligbleForReducedUsc(true);
		sa = new Salary[]{s};
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("1859.76"), BigDecimal.ZERO));
		
	}
	
	private BigDecimal addUsc(Salary[] sals){
		BigDecimal ret = BigDecimal.ZERO;
		for (Salary sal : sals) {
			ret.add(sal.getUsc());
		}
		return ret;
	}
	
	private BigDecimal addPrsi(Salary[] sals){
		BigDecimal ret = BigDecimal.ZERO;
		for (Salary sal : sals) {
			ret.add(sal.getPrsi());
		}
		return ret;
	}
	
	@Test
	public void testCalcPrsi() {
		Salary s = new Salary();
		s.setGross(bd("10000"));
		s.setPrsiClass(Prsi.A1);
		
		IESalaryService2015 service = new IESalaryService2015();
		BigDecimal res = service.calcPrsi(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("850"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("10850"), BigDecimal.ZERO));
		
		s.setGross(bd("18000"));
		res = service.calcPrsi(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("1530"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("19530"), BigDecimal.ZERO));
		

		s.setGross(bd("18600"));
		res = service.calcPrsi(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("744"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("744"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("1999.5"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("20599.5"), BigDecimal.ZERO));
		
		s.setGross(bd("25000"));
		res = service.calcPrsi(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("1000"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("1000"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("2687.5"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("27687.5"), BigDecimal.ZERO));
		
		s.setGross(bd("50000"));
		res = service.calcPrsi(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("2000"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("2000"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("5375"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("55375"), BigDecimal.ZERO));
	}
	
	@Test
	public void testCalcPaye() {
		Salary s = new Salary();
		s.setStdRateCutoff(bd("33800"));
		s.setTaxCredits(bd("1650"));
		s.setGross(bd("10000"));
		
		IESalaryService2015 service = new IESalaryService2015();
		BigDecimal res = service.calcPaye(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		
		s.setGross(bd("34000"));
		res = service.calcPaye(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("5190"), BigDecimal.ZERO));
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("5190"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("80"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("5110"), BigDecimal.ZERO));
		
		s.setGross(bd("10000"));
		s.setStdRateCutoff(bd("0"));
		res = service.calcPaye(s).getT1();
		assertThat(res, new BigDecimalCloseTo(bd("2350"), BigDecimal.ZERO));
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("2350"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("2350"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
	}
	
	@Test
	public void testAdd() {
		Salary s = new Salary();
		s.setStdRateCutoff(bd("33800"));
		s.setTaxCredits(bd("1650"));
		s.setGross(bd("10000"));
		s.setNet(bd("5000"));
		s.setPaye(bd("3000"));
		s.setPayeAtHigherRate(bd("1000"));
		s.setPayeAtLowerRate(bd("2000"));
		s.setUsc(bd("1200"));
		s.setPrsi(bd("800"));
		s.setEmployersPrsi(bd("350"));
		s.setCostOfEmployment(s.getGross().add(s.getEmployersPrsi()));
		Salary s2 = new Salary();
		s2.setStdRateCutoff(bd("0"));
		s2.setTaxCredits(bd("1650"));
		s2.setGross(bd("2000"));
		s2.setNet(bd("1200"));
		s2.setPaye(bd("500"));
		s2.setPayeAtHigherRate(bd("300"));
		s2.setPayeAtLowerRate(bd("200"));
		s2.setEmployersPrsi(bd("0"));
		s2.setUsc(bd("300"));
		s2.setPrsi(bd("200"));
		
		Salary sum = s.add(s2);
		assertThat(sum.getGross(), new BigDecimalCloseTo(bd("12000"), BigDecimal.ZERO));
		assertThat(sum.getPaye(), new BigDecimalCloseTo(bd("3500"), BigDecimal.ZERO));
		assertThat(sum.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("1300"), BigDecimal.ZERO));
		assertThat(sum.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("2200"), BigDecimal.ZERO));
		assertThat(sum.getNet(), new BigDecimalCloseTo(bd("6200"), BigDecimal.ZERO));
		assertThat(sum.getPrsi(), new BigDecimalCloseTo(bd("1000"), BigDecimal.ZERO));
		assertThat(sum.getUsc(), new BigDecimalCloseTo(bd("1500"), BigDecimal.ZERO));
		
		assertThat(s.getGross(), new BigDecimalCloseTo(bd("10000"), BigDecimal.ZERO));
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("3000"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("1000"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("2000"), BigDecimal.ZERO));
		assertThat(s.getNet(), new BigDecimalCloseTo(bd("5000"), BigDecimal.ZERO));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("800"), BigDecimal.ZERO));
		assertThat(s.getUsc(), new BigDecimalCloseTo(bd("1200"), BigDecimal.ZERO));
		
		assertThat(s2.getGross(), new BigDecimalCloseTo(bd("2000"), BigDecimal.ZERO));
		assertThat(s2.getPaye(), new BigDecimalCloseTo(bd("500"), BigDecimal.ZERO));
		assertThat(s2.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("300"), BigDecimal.ZERO));
		assertThat(s2.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("200"), BigDecimal.ZERO));
		assertThat(s2.getNet(), new BigDecimalCloseTo(bd("1200"), BigDecimal.ZERO));
		assertThat(s2.getPrsi(), new BigDecimalCloseTo(bd("200"), BigDecimal.ZERO));
		assertThat(s2.getUsc(), new BigDecimalCloseTo(bd("300"), BigDecimal.ZERO));
		
		IESalaryService2015 service = new IESalaryService2015();
		s.setStdRateCutoff(bd("33800"));
		s.setTaxCredits(bd("1650"));
		s.setGross(bd("35000"));
		s.setPrsiClass(Prsi.S);
		s = service.calcSalary(s);
		assertThat(s.getUsc(), new BigDecimalCloseTo(bd("1595"), BigDecimal.ONE));
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("1400"), BigDecimal.ZERO));
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("5590"), BigDecimal.ZERO));
		assertThat(s.getNet(), new BigDecimalCloseTo(bd("26415"), BigDecimal.ONE));
		s2 = new Salary();
		s2.setStdRateCutoff(bd("33800"));
		s2.setTaxCredits(bd("3300"));
		s2.setGross(bd("5000"));
		s2.setPrsiClass(Prsi.A);

		s2 = service.calcSalary(s2);
		assertThat(s2.getUsc(), new BigDecimalCloseTo(bd("75"), BigDecimal.ZERO));
		assertThat(s2.getPrsi(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s2.getPaye(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s2.getNet(), new BigDecimalCloseTo(bd("4925"), BigDecimal.ZERO));
		
		sum = s.add(s2);
		assertThat(sum.getStdRateCutoff(), new BigDecimalCloseTo(bd("67600"), BigDecimal.ZERO));
	}
	
	/*@Test
	// Compare salaries for PAYE and S-class, simply. Note extra 1650 tax credits for PAYE, however, also employers prsi.
	public void comparePayeAndS() {
		BigDecimal startSal = bd("40000");
		Salary sNetSols = new Salary(), sEmp = new Salary();
		sNetSols.setStdRateCutoff(bd("45000");
		sNetSols.setTaxCredits(bd("3300");
		sNetSols.setGross(startSal;
		sNetSols.setPrsiClass(Prsi.A;
		sEmp.setStdRateCutoff(sNetSols.getGross();
		sEmp.setTaxCredits(bd("1650");
		sEmp.setGross(sNetSols.getGross();
		sEmp.setPrsiClass(Prsi.S;
		IESalaryService s = new IESalaryService();
		

		out("Comparing SEmp income with paye salary");
		for(int i=0;i<100;i+=5){
			sNetSols.setGross(startSal.add(bd(i).multiply(bd("1000")));
			sEmp.setGross(sNetSols.getGross();
			s.calcSalary(sNetSols);
			s.calcSalary(sEmp);
			out(sEmp, sNetSols);
		}
	}*/
	
	private void out(String s){
		System.out.println(s);
	}
	
	private void out(Salary s1, Salary[] sNetSol, BigDecimal netSolTake){
		out(s1, sNetSol[0], sNetSol[1], netSolTake);
	}
	
	private void out(Salary s1, Salary sEmpBase, Salary sNetSols, BigDecimal netSolTake){
		Salary s2 = sEmpBase.add(sNetSols);
		BigDecimal grossGain = s2.getNet().subtract(s1.getNet());
		//BigDecimal netSolTake = grossGain.subtract(s2.getEmployersPrsi()).divide(bd("2"), MathContext.DECIMAL64).add(s2.getEmployersPrsi());
		BigDecimal netGain = grossGain.subtract(sNetSols.getEmployersPrsi());
		BigDecimal netBefore = s1.add(Salary.Net, sEmpBase.getNet().negate() );
		BigDecimal netAfterIncFee = sNetSols.getNet().subtract(netSolTake);
		BigDecimal monthlyBefore = netBefore.divide(bd("12"), MathContext.DECIMAL64);
		BigDecimal monthlyNetAfterFee = netAfterIncFee.divide(bd("12"), MathContext.DECIMAL64);
		BigDecimal percentGain = netAfterIncFee.subtract(netBefore).divide(netBefore, MathContext.DECIMAL64);
		BigDecimal netPercentBefore = netBefore.divide(sNetSols.getGross(), MathContext.DECIMAL64).multiply(bd("100"));
		BigDecimal netPercentAfter = netAfterIncFee.divide(sNetSols.getGross(), MathContext.DECIMAL64).multiply(bd("100"));
		System.out.println(s1.print("\t")+"\t"+s2.print("\t")+"\t"+grossGain+"\t"+netSolTake +"\t"+netGain+"\t"+netBefore+"\t"+netAfterIncFee+"\t"+monthlyBefore+"\t"+monthlyNetAfterFee+"\t"+percentGain+"\t"+netPercentBefore+"\t"+netPercentAfter);
	}
	
	@Test
	public void compareNetSol() {
		BigDecimal[] netSolSalaryTable = new BigDecimal[]{bd(14900),bd(17000), bd(17500), bd(18000), bd(18300),  bd(20000) };
		BigDecimal startSal = bd("40000"), netSolTake=bd("2200");
		Salary sNetSols = new Salary(), sEmpBase = new Salary(), sEmp = new Salary();
		sEmpBase.setStdRateCutoff(bd("33800"));
		sEmpBase.setTaxCredits(bd("1650"));
		sEmpBase.setGross(startSal);
		sEmpBase.setPrsiClass(Prsi.S);
		sNetSols.setTaxCredits(bd("1650"));
		sNetSols.setPrsiClass(Prsi.A);
		sEmp.setTaxCredits(bd("1650"));
		sEmp.setStdRateCutoff(sEmpBase.getStdRateCutoff());
		sEmp.setTaxCredits(bd("1650")); // Extra tax credits since now getting PAYE tax credits
		sEmp.setPrsiClass(Prsi.S);
		IESalaryService2015 s = new IESalaryService2015();
		for(int idx=0;idx<netSolSalaryTable.length;idx++){
			sNetSols.setGross(netSolSalaryTable[idx]);
			out("Comparing SEmp income with split SEmp and NetSol salary :"+sNetSols.getGross()+", Monthly "+sNetSols.getGross().divide(bd(12), MathContext.DECIMAL64)+" with NetSolCut of "+netSolTake);
			out(Salary.pHdr()+"\t"+Salary.pHdr()+"\tEmployee Gain\tNetSol take(netGain plus employersPrsi)\tNet Gain\tNet Sal before\tNet Sal After\tMonthly Before\tMonthlyAfter\tpercentGain\tpercentBefore\tpercentAfter");
			for(int i=0;i<100;i+=5){
				sEmp.setGross(startSal.add(bd(i).multiply(bd("1000"))));
				s.calcSalary(sEmp);	
				//Estimate employers PRsi
				s.calcPrsi(sNetSols);
				sEmpBase.setGross(sEmp.getGross().subtract(sNetSols.getGross()).subtract(netSolTake).subtract(sNetSols.getEmployersPrsi()));
				sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getPayeAtLowerRate()));
				Salary[] netSolSal = new Salary[]{sEmpBase, sNetSols};
				s.calcSalary(netSolSal);
				out(sEmp, netSolSal, netSolTake);
			}
		}
	}
	
	@Test
	public void testStdRate() {
		IESalaryService2015 service = new IESalaryService2015();
		
		Salary s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Single, false);
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("33800"), BigDecimal.ZERO));
		s = service.calcTaxCredits(Prsi.A, MaritalTaxStatus.Married1Income, false);
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("3300"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		s = service.calcTaxCredits(Prsi.A, MaritalTaxStatus.Married1Income, true);
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("3300"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		assertThat(s.isEligbleForReducedUsc(), equalTo(true) );
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married1Income, false);
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false);
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("0"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("10000"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("52800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("20000"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("62800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("22800"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("65600"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("24800"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("67600"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesJointAssesment, false, bd("25000"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("67600"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.S, MaritalTaxStatus.Married2IncomesSingleAssesment, false, bd("25000"));
		assertThat(s.isEligbleForReducedUsc(), equalTo(false) );
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("1650"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("33800"), BigDecimal.ZERO));
		
		s = service.calcTaxCredits(Prsi.A, MaritalTaxStatus.Married1Income, true, bd("25000"));
		assertThat(s.getTaxCredits(), new BigDecimalCloseTo(bd("3300"), BigDecimal.ZERO));
		assertThat(s.getStdRateCutoff(), new BigDecimalCloseTo(bd("42800"), BigDecimal.ZERO));
		assertThat(s.isEligbleForReducedUsc(), equalTo(true) );
		
		
	}

	// Old method that didn't input salary array
	public void compareNetSolOld() {
		BigDecimal[] netSolSalaryTable = new BigDecimal[]{bd(10000), bd(12000),bd(13500),bd(17000), bd(17500), bd(18000), bd(18125), bd(18300),  bd(20000) };
		BigDecimal startSal = bd("40000"), netSolTake = bd(3000);
		Salary sNetSols = new Salary(), sEmpBase = new Salary(), sEmp = new Salary();
		sEmpBase.setStdRateCutoff(bd("33800"));
		sEmpBase.setTaxCredits(bd("1650"));
		sEmpBase.setGross(startSal);
		sEmpBase.setPrsiClass(Prsi.S);
		sNetSols.setTaxCredits(bd("1650"));
		sNetSols.setPrsiClass(Prsi.A);
		sEmp.setTaxCredits(bd("1650"));
		sEmp.setStdRateCutoff(sEmpBase.getStdRateCutoff());
		sEmp.setTaxCredits(bd("1650")); // Extra tax credits since now getting PAYE tax credits
		sEmp.setPrsiClass(Prsi.S);
		IESalaryService2015 s = new IESalaryService2015();
		for(int idx=0;idx<netSolSalaryTable.length;idx++){
			sNetSols.setGross(netSolSalaryTable[idx]);
			out("Comparing SEmp income with split SEmp and Gross paye salary :"+sNetSols.getGross()+", Monthly "+sNetSols.getGross().divide(bd(12), MathContext.DECIMAL64));
			out(Salary.pHdr()+"\t"+Salary.pHdr()+"\tGross Gain\tNetSol take\tNet Gain\tNet Sal before\tNet Sal After\tMonthly Before\tMonthlyAfter");
			for(int i=0;i<100;i+=5){
				sEmp.setGross(startSal.add(bd(i).multiply(bd("1000"))));
				sEmpBase.setGross(sEmp.getGross().subtract(sNetSols.getGross()));
				s.calcSalary(sEmpBase);
				sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getPayeAtLowerRate()));
				s.calcSalary(sNetSols);
				s.calcSalary(sEmp);
				out(sEmp, sEmpBase, sNetSols, netSolTake);
			}
		}
	}
	
	@Test
	public void netSolSalaryProfile() {
		BigDecimal netSolStart = bd(5000);
		BigDecimal startSal = bd("40000");
		Salary sNetSols = new Salary(), sEmpBase = new Salary(), sEmp = new Salary();
		sEmpBase.setStdRateCutoff(bd("33800"));
		sEmpBase.setTaxCredits(bd("1650"));
		sEmpBase.setGross(startSal);
		sEmpBase.setPrsiClass(Prsi.S);
		sNetSols.setTaxCredits(bd("1650"));
		sNetSols.setPrsiClass(Prsi.A);
		sEmp.setTaxCredits(bd("1650"));
		sEmp.setStdRateCutoff(sEmpBase.getStdRateCutoff());
		sEmp.setTaxCredits(bd("1650")); // Extra tax credits since now getting PAYE tax credits
		sEmp.setPrsiClass(Prsi.S);
		out("Calculating max NetSolsalary for given salary");
		IESalaryService2015 s = new IESalaryService2015();
		for(int idx=0;idx<1000;idx+=5){
			sEmp.setGross(startSal.add(bd(idx).multiply(bd("100"))));   
			BigDecimal maxGain= BigDecimal.ZERO;
			BigDecimal maxSal=null;
			BigDecimal sEmpMax=null, maxNetSolTake = BigDecimal.ZERO;
			for(int i=0;i<100;i++){
				sNetSols.setGross(netSolStart.add(bd(i).multiply(bd("100")))); 
				sEmpBase.setGross(sEmp.getGross().subtract(sNetSols.getGross()));
				s.calcSalary(sEmp);
				sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getPayeAtLowerRate()));
				Salary[] netSolSal = new Salary[]{sEmpBase, sNetSols};
				s.calcSalary(netSolSal);
				sNetSols = netSolSal[1];
				sEmpBase= netSolSal[0];
				BigDecimal grossGain = sNetSols.getNet().add(sEmpBase.getNet()).subtract(sEmp.getNet());
				BigDecimal netSolTake = grossGain.subtract(sNetSols.getEmployersPrsi()).divide(bd("2"), MathContext.DECIMAL64).add(sNetSols.getEmployersPrsi());
				BigDecimal netGain = grossGain.subtract(netSolTake);
				BigDecimal netBefore = sEmp.add(Salary.Net, sEmpBase.getNet().negate() );
				BigDecimal netAfterIncFee = sNetSols.getNet().add(sEmpBase.getNet()).subtract(netSolTake);
				if(maxNetSolTake.compareTo(netSolTake)<0){
					maxNetSolTake = netSolTake;
					maxGain=netGain;
					maxSal=sNetSols.getGross();
					sEmpMax = sEmpBase.getGross();
				}
			}
			out(""+sEmp.getGross()+" Max :"+maxNetSolTake+" achieved at NetSol salary of "+maxSal+" and Sclass salary of "+sEmpMax+" with Client paying "+(maxSal.add(maxNetSolTake)));
		}
	}
	
	// Loop seems quicker 
	//@Test
	public void speedTestRecurVsLoop() {
		IESalaryService2015 service = new IESalaryService2015();
		int loops = 10;
		long[] recur = new long[loops],  lp = new long[loops];
		long recurTotal=0, loopTotal=0;
		for(int l=0;l<loops;l++){
			long startRecur =  System.nanoTime();
			for(int i=0;i<10000;i+=100){
				// Test 2 salaries one S one paye	
				Salary paye = new Salary();
				Salary s = new Salary();
				s.setGross(bd("120000"));
				s.setPrsiClass(Prsi.S);
				paye.setGross(bd("60000"));
				paye.setPrsiClass(Prsi.A);
				service.addUsc(new Salary[]{s, paye});
			}
			recur[l] =  System.nanoTime()-startRecur;
			recurTotal += recur[l];
			long startLoop =  System.nanoTime();
			for(int i=0;i<1000;i+=100){
				// Test 2 salaries one S one paye	
				Salary paye = new Salary();
				Salary s = new Salary();
				s.setGross(bd("120000"));
				s.setPrsiClass(Prsi.S);
				paye.setGross(bd("60000"));
				paye.setPrsiClass(Prsi.A);
				service.calcUscGeneral2Loop(new Salary[]{s, paye});
			}
			lp[l] =  System.nanoTime()-startLoop;
			loopTotal += lp[l];
		}
		out("Recur Total "+recurTotal+", Average = "+((double)recurTotal/loops));
		out("Loop Total "+loopTotal+", Average = "+((double)loopTotal/loops));
	}
		
	/*@Test
	public void compareSalaries() {
		BigDecimal startSal = bd("40000");
		Salary sPaye = new Salary(), sEmp = new Salary();
		sPaye.setStdRateCutoff(bd("45000");
		sPaye.setTaxCredits(bd("3300");
		sPaye.setGross(startSal;
		sPaye.setPrsiClass(Prsi.A;
		sEmp.setStdRateCutoff(sPaye.getGross();
		sEmp.setTaxCredits(bd("1650");
		sEmp.setGross(sPaye.getGross();
		sEmp.setPrsiClass(Prsi.S;
		IESalaryService s = new IESalaryService();	
	}*/
		
		
}
