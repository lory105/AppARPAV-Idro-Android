package it.arpav.mobile.apparpav.utils;

import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.types.Data;
import it.arpav.mobile.apparpav.types.Station;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Utilities
 * @author Giacomo Lorigiola
 */

public class XMLParser {
	// key for the station's index xml 
	static final String KEY_STATION = 		"STAZIONE";
	static final String KEY_ID =			"ID";
	static final String KEY_NAME = 			"NOME";
	static final String KEY_RESERVOIR = 	"BACINO";
	static final String KEY_COORDINATE_X = 	"X";
	static final String KEY_COORDINATE_Y = 	"Y";
	static final String KEY_QUOTA = 		"QUOTA";
	static final String KEY_LINK = 			"LINK";
	static final String KEY_TYPE = 			"TIPOSTAZ";
	
	
	// key for a specific xml of a station
	static final String KEY_SENSOR = 		"SENSORE";
	static final String KEY_TYPE_SENSOR = 		"TIPO";
	static final String KEY_LIVIDRO = 		"LIVIDRO";
	static final String KEY_PREC = 			"PREC";
	static final String KEY_VALUE = 		"VALORE";
	static final String KEY_INSTANT = 		"istante";
	
	
	/**
	 * Reads an xml file from an url with Http request, and return a string of xml file
	*/
	public String getXmlFromUrl(String url) {
        String xml = null;
 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
	
	
	/**
	 * Parsing XML content from string and getting DOM element 
	*/
	public Document getDomElementFromString( String xml) throws XmlNullExc {
		if(xml == null ) throw new XmlNullExc(); 
		
	    Document doc = null;
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	try {
		 
	        DocumentBuilder db = dbf.newDocumentBuilder();
		 
		    InputSource is = new InputSource();
		            is.setCharacterStream(new StringReader(xml));
		            doc = db.parse(is);
		 
	        } catch (ParserConfigurationException e) {
		            System.out.println("XML parse error: " + e.getMessage());
		            return null;
		    } catch (SAXException e) {
		            System.out.println("Wrong XML file structure: " + e.getMessage());
		            return null;
		    } catch (IOException e) {
		            System.out.println("I/O exeption: " + e.getMessage());
		            return null;
		    }
		 
		    return doc;
		 
	}
	
	/**
	 * Get each xml child element value by passing element node name
	*/
	public String getValue(Element item, String str) {
	    NodeList n = item.getElementsByTagName(str);
	    return this.getElementValue(n.item(0));
	}
	 
	public final String getElementValue( Node elem ) {
		Node child;
	    if( elem != null){
	    	if(elem.hasChildNodes()){
	    		for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	    			if( child.getNodeType() == Node.TEXT_NODE  ){
	    				return child.getNodeValue();
	                }
	            }
	        }
	    }
	    return "";
	} 
	
	
	/**
	 * Parses the main xml containing the index of stations and some of their basic information
	*/
	public List<ArrayList<Station>> parseXmlIndexStations(Document doc){
		//ArrayList<ArrayList<Station>>[] stationList = new ArrayList<ArrayList<Station>>;
		
		List<ArrayList<Station>> listStations = new ArrayList<ArrayList<Station>>();

		
		ArrayList<Station> idroStationList = new ArrayList<Station>();
		ArrayList<Station> meteoStationList = new ArrayList<Station>();
		//Element root=doc.getDocumentElement();
		
		
		NodeList nodesStation=doc.getElementsByTagName( KEY_STATION );
        
		for(int i=0;i<nodesStation.getLength();i++){
			Element elementStation = (Element) nodesStation.item(i);
			Station station = new Station();
            			
		    NodeList nodeName = elementStation.getElementsByTagName(KEY_NAME);
		    Element elementName = (Element) nodeName.item(0);
		    
		    NodeList nodeReservoir = elementStation.getElementsByTagName(KEY_RESERVOIR);
		    Element elementReservoir = (Element) nodeReservoir.item(0);
		    
		    station.setId( getValue(elementStation, KEY_ID) );
		    station.setName( getCharacterDataFromElement(elementName) );
			station.setReservoir( getCharacterDataFromElement(elementReservoir) );
			station.setCoordinateX( getValue(elementStation, KEY_COORDINATE_X ) );
			station.setCoordinateY( getValue(elementStation, KEY_COORDINATE_Y) );
			station.setLink( getValue(elementStation, KEY_LINK) );
			station.setType( getValue(elementStation, KEY_TYPE) );
            
			if( station.getType().equals( Global.KEY_IDRO))
				idroStationList.add(station);
			else
				meteoStationList.add(station);
            
        }
		listStations.add(idroStationList);
		listStations.add(meteoStationList);
		return listStations;
		
	}
	
	
	
	/**
	 * Parses the main xml containing the index of stations and some of their basic information
	*/
	public Data parseXmlStationData(Document doc){
		boolean oddNumber = true;
		
		String type = null;
		String[]date = null;
		String[] time = null;
		String unitMeasurement = null;
		float[] value = null;
		
		// keep sensor node
		NodeList nodesSensor=doc.getElementsByTagName( KEY_SENSOR );
		
		// for each sensor node, test if it is LIVIRDO or PREC sensor
		for(int i=0;i<nodesSensor.getLength();i++){
			Element elementSensor = (Element) nodesSensor.item(i);
			
			// set unity of measurement of value, and type sensor value
		    NodeList nodeUnitMeasurement = elementSensor.getElementsByTagName(Global.KEY_UNIT_MEASUREMENT);
		    Element elementUnitMeasurement = (Element) nodeUnitMeasurement.item(0);
		    unitMeasurement=  getCharacterDataFromElement(elementUnitMeasurement) ;
			
		    type = getValue(elementSensor, Global.KEY_TYPE);
		    
			if( type.equals(Global.KEY_LIVIDRO) || type.equals(Global.KEY_PREC) ){
				
				NodeList nodesValue = elementSensor.getElementsByTagName(KEY_VALUE);
				
				int size = nodesValue.getLength();
				date = new String[size];
				time = new String[size];
				value = new float[size];
				
				for(int x=0;x<nodesValue.getLength();x++){
					Element elementValue = (Element) nodesValue.item(x);
					value[x]= Float.parseFloat( elementValue.getTextContent());
					
					// elaborate the attribute "istante" of "VALORE" tag: divide date and time
					String instantValue = elementValue.getAttribute(KEY_INSTANT);
					String dateValue = instantValue.substring(0, 8);
					String timeValue = instantValue.substring(8, 12);

					// insert some symbols in date and time to have this format: YYYY/MM/DD and HH:MM  
					date[x]= dateValue = new StringBuffer(dateValue).insert(4, "/").insert(7,"/").toString();
					
					// save the time value only once time every two
					if(oddNumber){
						time[x]= timeValue = new StringBuffer(timeValue).insert(2, ":").toString();
						oddNumber=false;
					}
					else{
						time[x]="";
						oddNumber=true;
					}
					
				}

				break;
			}
		
		}
		
		return new Data( type, date, time, unitMeasurement, value);
	}
	
	
	
	
	
	/**
	 * Method used to read the tags "NOME" and "BACINO" that contain CDATA values
	*/
	public static String getCharacterDataFromElement(Element e) {
		  Node child = e.getFirstChild();
		  if (child instanceof CharacterData) {
		    CharacterData cd = (CharacterData) child;
		    return cd.getData();
		  }
		  return "";
		}	
	
}
