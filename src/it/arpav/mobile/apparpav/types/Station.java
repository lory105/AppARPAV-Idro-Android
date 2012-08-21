package it.arpav.mobile.apparpav.types;

/**
 * Utilities
 * @author Giacomo Lorigiola
 */

public class Station {
	private String id			= "";
	private String name 		= "";
	private String reservoir 	= "";
	private String coordinateX 	= "";
	private String coordinateY 	= "";
	private String quota	 	= "";
	private String link 		= "";
	private String type			= "";


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
	
	public String getCoordinateX(){
		return coordinateX;
	}
	
	public void setCoordinateY( String coordinateY){
		this.coordinateY = coordinateY;
	}
	
	public String getCoordinateY(){
		return coordinateY;
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
	



}
