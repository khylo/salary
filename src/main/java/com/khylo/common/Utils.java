package com.khylo.common;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

	public static BigDecimal bd(String s){
		return new BigDecimal(s, MathContext.DECIMAL64);
	}
	public static BigDecimal bd(int i){
		return new BigDecimal(i, MathContext.DECIMAL64);
	}
	/**
	 * REturn the lesser of 2 values
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal min(BigDecimal a, BigDecimal b) {
		if(a.compareTo(b)>0)
			return b;
		else
			return a;		
	}
	
	/**
	 * REturn the greater of 2 values
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal max(BigDecimal a, BigDecimal b) {
		if(a.compareTo(b)>0)
			return a;
		else
			return b;		
	}
	
	/**
     * Convenience method for converting list to comma seperated String, for use in SDS methods
     * @param cusips
     * @return
     */
	public static String listToString(Collection<Object> col){
		return listToString(col, ",");
	}
	
    /**
     * Convenience method for converting list to seperated String, for use in SDS methods
     * @param cusips
     * @return
     */
    public static String listToString(Collection<Object> list, String sep){
        StringBuilder ret = new StringBuilder();
        for(Object item: list){
            ret.append(item+sep);
        }
        return ret.substring(0,ret.length()-sep.length()); // remove the last comma
    }

    public static XMLGregorianCalendar getXmlDate(Date d) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }

}
