package com.khylo.salary;

import java.util.HashMap;
import java.util.Map;

public enum MaritalTaxStatus {	
	Single("S"),
	SingleParent("SP"),
	Married1Income("M1"),
	Married2IncomesJointAssesment("M2J"),
	Married2IncomesSingleAssesment("M2S");
	

	private static Map<String, MaritalTaxStatus> label2EnumMap = new HashMap<String, MaritalTaxStatus>();
	static{
		for(MaritalTaxStatus m: MaritalTaxStatus.values()){
			label2EnumMap.put(m.label, m);
		}
		
	}
	
	private String label;
	private MaritalTaxStatus (String label){
		this.label=label;
	}
	
	public static MaritalTaxStatus label2Enum(String lab){
		return label2EnumMap.get(lab);
	}

}
