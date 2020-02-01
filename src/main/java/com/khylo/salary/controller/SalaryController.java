package com.khylo.salary.controller;

import com.khylo.salary.*;
import com.khylo.salary.calc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SalaryController {
/*

    private final AtomicLong counter = new AtomicLong();
    @Autowired
    private IESalaryService2015 salaryService;
    @Autowired
    private Validator validator; // note this is configured in Application.java LocalValidatorFactoryBean
    //private LocalValidatorFactoryBean validator;
    

    @RequestMapping(method=RequestMethod.GET, value=Application.TaxCreditsUrl)
    public Salary calcTaxCreditsStdRate(
    		@RequestParam(SalaryApi.PrsiParam) String prsi,
			@RequestParam(SalaryApi.MaritalStatusParam) String maritalStatus,
			@RequestParam(SalaryApi.MedicalCardParam) boolean medicalCard,
			@RequestParam(SalaryApi.Salary2Param) BigDecimal salary2) {
    	Prsi prsiClass= Prsi.valueOf(prsi);
    	MaritalTaxStatus maritalTaxStatus = MaritalTaxStatus.label2Enum(maritalStatus);
        return salaryService.calcTaxCredits(prsiClass, maritalTaxStatus, medicalCard, salary2);
    }

    @RequestMapping(method=RequestMethod.GET, value=SalaryApi.CalculateSalaryUrl)
    public Salary calculateSalary(@Param(SalaryApi.SalaryParam) SalaryInputParams s) {
        return salaryService.calcSalary(s);
    }
    
    @RequestMapping(method=RequestMethod.GET, value=SalaryApi.CalculateNetSolSalaryUrl)
    public Salary calculateNetSolSalary(@Param(SalaryApi.SalaryParam) Salary s) {
        return salaryService.calcSalary(s);
    }
    

    @RequestMapping(method=RequestMethod.GET, value=Application.GetHolidaysUrl)
    public Map<String, Date> getHolidaysDefault() {
        return getHolidays((LocalDate.now()).getYear(),"IE");
    }
    
    @RequestMapping(method=RequestMethod.GET, value=Application.GetHolidaysUrl + "/{year}"+"/{c}")
	@ResponseBody
	public Map<String, Date> getHolidays(@PathVariable("year") int year, @PathVariable("c") String country){
		return null;
	}
	*/
}
