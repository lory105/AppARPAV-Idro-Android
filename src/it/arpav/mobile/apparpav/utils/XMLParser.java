package it.arpav.mobile.apparpav.utils;

import it.arpav.mobile.apparpav.types.Station;
import it.arpav.mobile.apparpav.exceptions.XmlNullExc;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Utilities
 * @author Giacomo Lorigiola
 */

public class XMLParser {
	static final String KEY_STATION = 		"STAZIONE";
	static final String KEY_ID =			"ID";
	static final String KEY_NAME = 			"NOME";
	static final String KEY_RESERVOIR = 	"BACINO";
	static final String KEY_COORDINATE_X = 	"X";
	static final String KEY_COORDINATE_Y = 	"Y";
	static final String KEY_QUOTA = 		"QUOTA";
	static final String KEY_LINK = 			"LINK";
	static final String KEY_TYPE = 			"TYPE";
	
	
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
	public Document getDomElementFromString(Context context, String xml) throws XmlNullExc {
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
	public ArrayList<Station> parseXmlIndexStations(Document doc){
		ArrayList<Station> stationList = new ArrayList<Station>();
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
			//station.setType( getValue(elementStation, KEY_TYPE) );
            
			Log.d("name", station.getName());
			
            stationList.add(station);
            
        }
		return stationList;
		
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
