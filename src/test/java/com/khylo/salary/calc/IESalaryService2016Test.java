package com.khylo.salary.calc;

import com.khylo.common.Tuple3;
import com.khylo.salary.MaritalTaxStatus;
import com.khylo.salary.Prsi;
import com.khylo.salary.Salary;
import com.khylo.salary.calc.IESalaryService2016;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.khylo.common.Utils.bd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IESalaryService2016Test {
	
	

	@Test
	public void testCalcUsc() {
		Salary s = new Salary();
		s.setGross(bd("10000"));
		s.setPrsiClass(Prsi.A1);
		
		IESalaryService2016 service = new IESalaryService2016();
		BigDecimal res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		s.setGross(bd("13000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		
		
		s.setGross(bd("30000"));
		Salary[] sa = new Salary[]{s, s.builder().build(), s.builder().build(), s.builder().build()}; // N.B. Problem here originally unless we copy the inputs, otherwise all array items point to one object
		res = addUsc(service.addUsc(sa));
		assertThat(res, new BigDecimalCloseTo(bd("7141.96"), BigDecimal.ZERO));
		
		// Test reduced Usc Rate	First standard usc if earning 60000
		s.setGross(bd("60000"));
		res = service.calcUsc(s);
		assertThat(res, new BigDecimalCloseTo(bd("2593.06"), BigDecimal.ZERO));
		
		//Now compare with reduced Usc  (1% up to 12012, and 3% up to 60000)
		s.setEligbleForReducedUsc(true);
		sa = new Salary[]{s};
		res = Arrays.stream(service.addUsc(sa)).map(Salary::getUsc).reduce((l, r) -> l.add(r)).get();
		assertThat(res, new BigDecimalCloseTo(bd("1559.76"), BigDecimal.ZERO));
		
	}
	
	private BigDecimal addUsc(Salary[] sals){
		BigDecimal ret = BigDecimal.ZERO;
		for (Salary sal : sals) {
			ret = ret.add(sal.getUsc());
		}
		return ret;
	}
	
	@Test
	public void testCalcPrsi() {
		Salary s = new Salary();
		s.setGross(bd("10000"));
		s.setPrsiClass(Prsi.A1);
		
		IESalaryService2016 service = new IESalaryService2016();
		service.addPrsi(s);
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("850"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("10850"), BigDecimal.ZERO));
		
		s.setGross(bd("18000"));
		service.addPrsi(s);
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("1530"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("19530"), BigDecimal.ZERO));
		

		/*s.setGross(bd("18600"));
		service.addPrsi(s);
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("744"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("1999.5"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("20599.5"), BigDecimal.ZERO));
		
		s.setGross(bd("25000"));
		service.addPrsi(s);
		assertThat(s.getPrsi(), new BigDecimalCloseTo(bd("1000"), BigDecimal.ZERO));
		assertThat(s.getEmployersPrsi(), new BigDecimalCloseTo(bd("2687.5"), BigDecimal.ZERO));
		assertThat(s.getCostOfEmployment(), new BigDecimalCloseTo(bd("27687.5"), BigDecimal.ZERO));*/
		
		s.setGross(bd("50000"));service.addPrsi(s);
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
		
		IESalaryService2016 service = new IESalaryService2016();
		//Verify both calc and add methods are working as expected
		Tuple3<BigDecimal, BigDecimal, BigDecimal> res = service.calcPaye(s);
		assertThat(res.getT1(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(res.getT2(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(res.getT3(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		service.addPaye(s);
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("350"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		
		s.setGross(bd("34000"));
		res = service.calcPaye(s);
		assertThat(res.getT1(), new BigDecimalCloseTo(bd("5190"), BigDecimal.ZERO));
		assertThat(res.getT3(), new BigDecimalCloseTo(bd("80"), BigDecimal.ZERO));
		assertThat(res.getT2(), new BigDecimalCloseTo(bd("5110"), BigDecimal.ZERO));
		service.addPaye(s);
		assertThat(s.getPaye(), new BigDecimalCloseTo(bd("5190"), BigDecimal.ZERO));
		assertThat(s.getPayeAtHigherRate(), new BigDecimalCloseTo(bd("80"), BigDecimal.ZERO));
		assertThat(s.getPayeAtLowerRate(), new BigDecimalCloseTo(bd("5110"), BigDecimal.ZERO));
		
		s.setGross(bd("10000"));
		s.setStdRateCutoff(bd("0"));
		res = service.calcPaye(s);
		assertThat(res.getT1(), new BigDecimalCloseTo(bd("2350"), BigDecimal.ZERO));
		assertThat(res.getT3(), new BigDecimalCloseTo(bd("2350"), BigDecimal.ZERO));
		assertThat(res.getT2(), new BigDecimalCloseTo(bd("0"), BigDecimal.ZERO));
		service.addPaye(s);
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
		
		IESalaryService2016 service = new IESalaryService2016();
		s.setStdRateCutoff(bd("33800"));
		s.setTaxCredits(bd("1650"));
		s.setGross(bd("35000"));
		s.setPrsiClass(Prsi.S);
		s.setUsc(bd("1595"));
		s.setPrsi(bd("1400"));	
		s.setPaye(bd("5590"));	
		s.setNet(bd("26415"));			
		s2 = new Salary();
		s2.setStdRateCutoff(bd("33800"));
		s2.setTaxCredits(bd("3300"));
		s2.setGross(bd("5000"));
		s2.setPrsiClass(Prsi.A);
		s2.setUsc(bd("75"));
		s2.setPrsi(bd("0"));	
		s2.setPaye(bd("0"));	
		s2.setNet(bd("4925"));
		
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
	
	private void outAll(Salary s1, Salary sEmpBase, Salary sNetSols, BigDecimal salLastIteration, BigDecimal netSolTake){
		Salary s2 = sEmpBase.add(sNetSols);
		BigDecimal grossGain = s2.getNet().subtract(s1.getNet());
		//BigDecimal netSolTake = grossGain.subtract(s2.getEmployersPrsi()).divide(bd("2"), MathContext.DECIMAL64).add(s2.getEmployersPrsi());		
		BigDecimal diff;
		if(salLastIteration!=null)
			diff = grossGain.subtract(salLastIteration);
		else
			diff= BigDecimal.ZERO;
		BigDecimal netBefore = s1.add(Salary.Net, sEmpBase.getNet().negate() );
		BigDecimal netAfterIncFee = sNetSols.getNet().subtract(netSolTake);
		BigDecimal monthlyBefore = netBefore.divide(bd("12"), MathContext.DECIMAL64);
		BigDecimal monthlyNetAfterFee = netAfterIncFee.divide(bd("12"), MathContext.DECIMAL64);
		BigDecimal percentGain = netAfterIncFee.subtract(netBefore).divide(netBefore, MathContext.DECIMAL64);
		BigDecimal netPercentBefore = netBefore.divide(sNetSols.getGross(), MathContext.DECIMAL64).multiply(bd("100"));
		BigDecimal netPercentAfter = netAfterIncFee.divide(sNetSols.getGross(), MathContext.DECIMAL64).multiply(bd("100"));
		System.out.println(s1.print("\t")+"\t"+sEmpBase.print("\t")+"\t"+sNetSols.print("\t")+"\t"+s2.print("\t")+"\t"+grossGain+"\t"+netSolTake +"\t"+diff+"\t"+netBefore+"\t"+netAfterIncFee+"\t"+monthlyBefore+"\t"+monthlyNetAfterFee+"\t"+percentGain+"\t"+netPercentBefore+"\t"+netPercentAfter);
	}
	
	@Test
	public void calcNetSolAll() {
		//BigDecimal[] netSolSalaryTable = new BigDecimal[]{bd(14900),bd(17000), bd(17500), bd(18000), bd(18300),  bd(20000), bd(22500), bd(25000) };
		BigDecimal[] netSolSalaryTable = new BigDecimal[]{bd(4000), bd(4125), bd(4150), bd(17000), bd(18304), bd(18500) };
		BigDecimal startSal = bd("40000"), netSolTake=bd("2000");
		Salary sNetSols = new Salary(), sEmpBase = new Salary(), sEmp = new Salary();
		// Total current salary, paid as S-class only
		sEmp.setStdRateCutoff(bd("33800"));
		sEmp.setTaxCredits(bd("2200"));// Extra tax credits since now getting PAYE tax credits
		sEmp.setPrsiClass(Prsi.S);
		
		// SEmpBase = proportion of salary still paid as s-Class
		sEmpBase.setStdRateCutoff(sEmp.getStdRateCutoff());
		sEmpBase.setTaxCredits(bd("2200")); // was 1650"));
		//sEmpBase.setGross(startSal);
		sEmpBase.setPrsiClass(Prsi.S);
		Map<Integer, BigDecimal> gainLastIteration = new HashMap<>(20);
		// sNetSols is the Salary paid by NetSol at A-class
		sNetSols.setTaxCredits(bd("1650"));
		sNetSols.setPrsiClass(Prsi.A);
		IESalaryService2016 s = new IESalaryService2016();
		for(int idx=0;idx<netSolSalaryTable.length;idx++){
			sNetSols.setGross(netSolSalaryTable[idx]);
			s.calcSalary(sNetSols);	
			BigDecimal theyPay = sNetSols.getGross().add(netSolTake).add(sNetSols.getEmployersPrsi());
			BigDecimal theyPayMonthly = theyPay.divide(bd(12), MathContext.DECIMAL64);
			String out="They Pay "+theyPay+"(+VAT)(of which Net = "+theyPay.multiply(bd(".495"))+" to NetSol. NetSol give them a salary of :"+sNetSols.getGross()+"("+sNetSols.getNet()+"), They Pay Monthly "+theyPayMonthly+"("+theyPayMonthly.multiply(bd("0.495"))+""+" with NetSolCut of "+netSolTake+". THey get a net salary of "+sNetSols.getNet()+", Monthly="+sNetSols.getNet().divide(bd(12), MathContext.DECIMAL64);
			out(out);
			out(Salary.pHdr()+"\t"+Salary.pHdr()+"\t"+Salary.pHdr()+"\t"+Salary.pHdr()+"\tEmployee Gain\tNetSol take(netGain plus employersPrsi)\tDiff Gain\tNet Sal before\tNet Sal After\tMonthly Before\tMonthlyAfter\tpercentGain\tpercentBefore\tpercentAfter");
			
			for(int i=0;i<100;i+=5){
				int ii=i/5;
				sEmp.setGross(startSal.add(bd(i).multiply(bd("1000"))));
				s.calcSalary(sEmp);	
				//Estimate employers PRsi
				s.calcPrsi(sNetSols);
				sEmpBase.setGross(sEmp.getGross().subtract(sNetSols.getGross()).subtract(netSolTake).subtract(sNetSols.getEmployersPrsi()));
				s.calcSalary(sEmpBase);	
				sNetSols.setStdRateCutoff(sEmpBase.getStdRateCutoff().subtract(sEmpBase.getGross()));
				if(sNetSols.getStdRateCutoff().compareTo(BigDecimal.ZERO)<0)
					sNetSols.setStdRateCutoff(BigDecimal.ZERO);
				Salary[] netSolSal = new Salary[]{sEmpBase, sNetSols};
				s.calcSalary(netSolSal);				
				BigDecimal last=null;
				if(gainLastIteration.size()>ii)
					last = gainLastIteration.get(ii);				
				outAll(sEmp, netSolSal[0], netSolSal[1], last, netSolTake); //sEmp.getNet().subtract(netSolSal[0].getNet().add(netSolSal[1].getNet()))
				gainLastIteration.put(ii, netSolSal[0].add(netSolSal[1]).getNet().subtract(sEmp.getNet()));
			}
		}
	}
	
	@Test
	public void testStdRate() {
		IESalaryService2016 service = new IESalaryService2016();
		
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
		IESalaryService2016 s = new IESalaryService2016();
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
		IESalaryService2016 s = new IESalaryService2016();
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
