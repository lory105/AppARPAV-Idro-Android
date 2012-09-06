package it.arpav.mobile.apparpav.types;

/**
 * Class that represent a station
 * @author Giacomo Lorigiola
 */

public class Station {
	private String id			= "";	// id of station
	private String name 		= "";	// name of station
	private String reservoir 	= "";	// reservoir of station
	private String coordinateX 	= "";	// geo Xcoordinates 
	private String coordinateY 	= "";	// geo Ycoordinates
	private String quota	 	= "";	// quota of station
	private String link 		= "";	// link to download station data
	private String type			= "";	// station type: IDRO or METEO

	private Data data= null;

	
	public void setId( String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setName( String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setReservoir( String reservoir){
		this.reservoir = reservoir;
	}
	
	public String getReservoir(){
		return reservoir;
	}
	
	public void setCoordinateX( String coordinateX){
		this.coordinateX = coordinateX;
	}
		
	public double getCoordinateX(){
		return Double.parseDouble( coordinateX );
	}
	
	public void setCoordinateY( String coordinateY){
		this.coordinateY = coordinateY;
	}
	
	public double getCoordinateY(){
		return Double.parseDouble( coordinateY );
	}
	
	public void setquota( String quota){
		this.quota = quota;
	}
	
	public String getQuota(){
		return quota;
	}
	
	public void setLink( String link){
		this.link = link;
	}
	
	public String getLink(){
		return link;
	}

	public void setType( String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}

	public void setData( Data data){
		this.data = data;
	}
	
	public Data getData(){
		return data;
	}
	
}
