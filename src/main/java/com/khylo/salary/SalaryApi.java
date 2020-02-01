package com.khylo.salary;

public interface SalaryApi {
		public static final String SalaryPath = "/s";

		// The path to search videos by title
		public static final String CalculateSalaryUrl = SalaryPath + "/calc";
		public static final String CalculateNetSolSalaryUrl = SalaryPath + "/netSol";
		
		// Params
		public static final String SalaryParam = "sal";
		public static final String Gross = "g";
		public static final String TaxCredits = "tc";
		public static final String StdRateCutoff = "src";
		public static final String PrsiParam = "prsi";
		public static final String MaritalStatusParam = "m";
		public static final String MedicalCardParam = "mc";
		public static final String Salary2Param = "s2";

}
