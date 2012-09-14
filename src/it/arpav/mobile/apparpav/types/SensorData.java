package it.arpav.mobile.apparpav.types;

import java.util.Date;


/**
 * Class that consist in the data station
 * @author Giacomo Lorigiola
 */
public class SensorData {

	private String type = null;		// data type: LIVIDRO or PREC
	private Date[] date = null;	// array with the date of each value
	private String unitMeasurement = null; // unit of measurement for the value
	private double[] value = null;	// array with the value

	
	public SensorData(){}
	
	public SensorData( String type, Date[] date, String unitMeasurement, double[]value ){
		this.type=type;
		this.date=date;
		this.unitMeasurement= unitMeasurement;
		this.value=value;
	}

	public String getType(){
		return type;
	}
	
	public Date[] getDate(){
		return date;
	}

	public String getUnitMeasurement(){
		return unitMeasurement;
	}
	
	public double[] getValue(){
		return value;
	}
	
}
