package com.khylo.salary

import static com.khylo.common.Utils.bd;

import java.math.MathContext
import java.math.RoundingMode
import java.time.Month

import javax.validation.ValidatorFactory;
import javax.validation.Validator
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.hamcrest.number.BigDecimalCloseTo
import org.junit.Test;

import spock.lang.Unroll



class SalarySpec extends spock.lang.Specification{

	def testLimits(){
		when:
			//try{
			Salary s =  new Salary(bd(gross), bd(costOfEmployment), bd(net), bd(paye), bd(payeAtLowerRate), bd(payeAtHigherRate), bd(prsi), bd(employersPrsi), prsiClass, bd(usc), bd(stdRateCutoff), bd(taxCredits))
			
			// See https://docs.jboss.org/hibernate/validator/4.0.1/reference/en/html/validator-usingvalidator.html#d0e688
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			boolean valid=true;
			Set<ConstraintViolation<Salary>> constraintViolations = validator.validate(s)
			
			println "Result of validation = "+validator.validate(s) +"\n"+constraintViolations
			int vSize = constraintViolations.size()
			ConstraintViolation firstViolation = constraintViolations.iterator().next()
			
		then:
			vSize == violations
			firstViolation.getMessage() == firstViolationMsg
			firstViolation.getPropertyPath().toString() == path;
			
		where:
			gross  	| costOfEmployment| net | paye  |payeAtLowerRate|payeAtHigherRate| prsi  |employersPrsi|prsiClass|  usc  |stdRateCutoff| taxCredits|violations|path        |firstViolationMsg  		
			100		| 110			  | 80	| 	100	| 110			| 80			 |100	 | 110		   | Prsi.A  |100	 | 110		   | -1		| 1		   |"taxCredits"   |'must be greater than or equal to 0'			
			100		| 110			  | 80	| 	100	| 110			| 80			 |100	 | 110		   | Prsi.A  |100	 | -1		   | 100	| 1	   	   |"stdRateCutoff"|'must be greater than or equal to 0'
			-100	| 110			  | 80	| 	100	| 110			| 80			 |100	 | 110		   | Prsi.A  |100	 | -1		   | 100	| 2	   	   |"gross"        |'must be greater than or equal to 0'
			9999999	| 110			  | 80	| 	100	| 110			| 80			 |100	 | 110		   | Prsi.A  |100	 | -1		   | 100	| 2	   	   |"gross"        |'must be less than or equal to '+SalaryInputParams.MaxNum
			
	}

}
