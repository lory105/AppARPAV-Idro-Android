package it.arpav.mobile.apparpav.types;


/**
 * Class that consist in the data station
 * @author Giacomo Lorigiola
 */
public class Data {

	private String type = null;		// data type: LIVIDRO or PREC
	private String[] date = null;	// array with the date of each value
	private String[] time = null;	// array with the time of each value
	private String unitMeasurement = null; // unit of measurement for the value
	private float[] value = null;	// array with the value

	
	public Data(){}
	
	public Data( String type, String[] date, String[] time, String unitMeasurement, float[]value ){
		this.type=type;
		this.date=date;
		this.time=time;
		this.unitMeasurement= unitMeasurement;
		this.value=value;
	}

	public String getType(){
		return type;
	}
	
	public String[] getDate(){
		return date;
	}
	
	public String[] getTime(){
		return time;
	}

	public String getUnitMeasurement(){
		return unitMeasurement;
	}
	
	public float[] getValue(){
		return value;
	}
	
}
