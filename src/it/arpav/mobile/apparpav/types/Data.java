package it.arpav.mobile.apparpav.types;

public class Data {

	private String type = null;		// data type: LIVIDRO or PREC
	private String[] date = null;	// array with the date of each value
	private String[] time = null;	// array with the time of each value
	private float[] value = null;	// array with the value

	
	public Data(){}
	
	public Data( String type, String[] date, String[] time, float[]value ){
		this.type=type;
		this.date=date;
		this.time=time;
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
	
	public float[] getValue(){
		return value;
	}
	
	
	
}
