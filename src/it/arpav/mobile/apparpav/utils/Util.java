package it.arpav.mobile.apparpav.utils;

import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.main.MapStationActivity;
import it.arpav.mobile.apparpav.types.Station;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.R;

import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Utilities
 * @author Giacomo Lorigiola
 */

public class Util {
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
	 * Load the list of Stations from xml in the url
	*/
	public static void loadListStations(Context context) throws XmlNullExc{
		
			XMLParser xmlParser = new XMLParser();
			String xml = xmlParser.getXmlFromUrl( KEY_INDEX_STATIONS_URL );
			Document doc = xmlParser.getDomElementFromString(xml);
			// doc way be null if getDomElement return null
			
			listStations = xmlParser.parseXmlIndexStations(doc);
	}
	
	public static List<ArrayList<Station>> getListStations(Context context) throws XmlNullExc {
		if(listStations == null ){
			Log.d("getListStation", "NULL");
			loadListStations(context);
			
		}
		
		return listStations;		
	}
	
	public static boolean listStationIsLoaded(){
		if(listStations == null ) return false;
		return true;
	}
	
	
	
	public static void loadStationData(Station station) throws XmlNullExc{
		
		XMLParser xmlParser = new XMLParser();
		String xml = xmlParser.getXmlFromUrl( station.getLink() );
		Document doc = xmlParser.getDomElementFromString(xml);
		Log.d("PROVA", "no1");
		// doc way be null if getDomElement return null
		
		station.setData(xmlParser.parseXmlStationData(doc));
		Log.d("PROVA", "no2");
		//station.setData( xmlParser.parseXmlIndexStations(doc) );
	}
	
	
//	public static void getStationData(Station station){
//		new DownloadStationDataTask().execute(station);
//	}
	

	
	
	
	
	
	
	
}
