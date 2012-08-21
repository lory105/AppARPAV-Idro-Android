package it.arpav.mobile.apparpav.utils;

import it.arpav.mobile.apparpav.types.Station;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Utilities
 * @author Giacomo Lorigiola
 */

public class Util {
	static private String KEY_INDEX_STATIONS_URL= "http://www.arpa.veneto.it/upload_teolo/dati_xml/Ultime48ore_idx.xml";
	static private ArrayList<Station> listStations =null;
	
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
	 * Load the list of Stations from xml in the url
	*/
	public static void loadListStations(){
		if(listStations == null ){
		
			XMLParser xmlParser = new XMLParser();
			String xml = xmlParser.getXmlFromUrl( KEY_INDEX_STATIONS_URL );
			Document doc = xmlParser.getDomElementFromString(xml);
			listStations = xmlParser.parseXmlIndexStations(doc);
		}
	}
	
	public static ArrayList<Station> getListStations(){
		if(listStations == null )
			loadListStations();
		
		return listStations;		
	}
	
	public static boolean loaded(){
		if(listStations == null ) return false;
		return true;
	
	}
	
}
