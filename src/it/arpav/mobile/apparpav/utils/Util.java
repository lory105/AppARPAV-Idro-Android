package it.arpav.mobile.apparpav.utils;

import it.arpav.mobile.apparpav.exceptions.MalformedXmlExc;
import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.model.Station;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Utilities
 * @author Giacomo Lorigiola
 */
public class Util {
	// type station key
	public static final String KEY_IDRO = 		"IDRO";
	public static final String KEY_METEO = 		"METEO";
	public static final String KEY_IDRO_METEO = "IDRO-METEO";
	
	// type sensor key
	public static final String KEY_TYPE = 			  "TIPO";
	public static final String KEY_LIVIDRO = 		  "LIVIDRO";
	public static final String KEY_PREC = 			  "PREC";
	public static final String KEY_UNIT_MEASUREMENT = "UNITAMISURA";
	public static final String KEY_METER = 			  "m";
	public static final String KEY_METER_WORD =       "metri";
	public static final String KEY_MILLIMETER_WORD  = "millimetri";
	
	
	
	static private String KEY_INDEX_STATIONS_URL= "http://www.arpa.veneto.it/upload_teolo/dati_xml/Ultime48ore_idx.xml";
	static private List<ArrayList<Station>> listStations =null;
		
	
	/**
	 * Check for internet connection.
	 * ( use netInfo.isConnected() instead isConnectedOrConnecting() 
	 * if you absolutely need a network connection at the given point in time )
	 * @param context
	 * @return boolean
	*/
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm =
	            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	
	/**
	 * returns the list of stations: if the list isn't already loaded, try to download it, then returns the list
	 */
	public static List<ArrayList<Station>> getListStations(Context context) throws XmlNullExc, MalformedXmlExc{
		if(listStations == null ){
			loadListStations(context);
		}
		
		return listStations;		
	}
	
	/**
	 * Load the list of Stations from xml in the url
	*/
	public static void loadListStations(Context context) throws XmlNullExc, MalformedXmlExc{
		
			XMLParser xmlParser = new XMLParser();
			String xml = xmlParser.getXmlFromUrl( KEY_INDEX_STATIONS_URL );
			if(xml == null || xml.equals("") ){
				throw new XmlNullExc();
			}
			Document doc = xmlParser.getDomElementFromString(xml);
			// doc way be null if getDomElement return null
			
			if(doc == null){
				throw new MalformedXmlExc();
			}
			else
				listStations = xmlParser.parseXmlIndexStations(doc);
	}
	
	
	/**
	 * set to null the list of stations to allow an update of the list
	*/
	public static void setNullListStations(){
		listStations=null;
	}

	
	/**
	 * check if the list stations is loaded
	 */
	public static boolean listStationIsLoaded(){
		if(listStations == null ) return false;
		return true;
	}
	
	
	/**
	 * Load the data of a Station from xml in a specific url
	 */
	public static void loadStationData(Station station) throws XmlNullExc, MalformedXmlExc{
		
		XMLParser xmlParser = new XMLParser();
		String xml = xmlParser.getXmlFromUrl( station.getLink() );
		if(xml == null || xml.equals("") ){
			throw new XmlNullExc();
		}
		Document doc = xmlParser.getDomElementFromString(xml);
		// doc way be null if getDomElement return null
		if(doc == null){
			throw new MalformedXmlExc();
		}
		else
			xmlParser.parseXmlStationData(doc, station);
	}
	
	
}
