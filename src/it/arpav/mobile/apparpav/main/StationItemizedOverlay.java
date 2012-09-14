package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.exceptions.MalformedXmlExc;
import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.types.SensorData;
import it.arpav.mobile.apparpav.types.Station;
import it.arpav.mobile.apparpav.utils.Global;
import it.arpav.mobile.apparpav.utils.Util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


/**
 * Personalization of ItemizeOverlay for google maps
 * @author Giacomo Lorigiola
 */
public class StationItemizedOverlay extends BalloonItemizedOverlay<StationOverlayItem> {
	private ArrayList<StationOverlayItem> m_overlays = new ArrayList<StationOverlayItem>();
	private Context context;
	private ProgressDialog pdToLoadStationData = null;
	
	public StationItemizedOverlay( Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
	}

	@Override
	protected StationOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
	  return m_overlays.size();
	}

	public void addOverlay(StationOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected boolean onBalloonTap(int index, StationOverlayItem item) {		
		
		pdToLoadStationData = ProgressDialog.show(context, context.getString(R.string.loading), context.getString(R.string.loadingData), true, true);
		Station station=item.getStation();
		if(! station.isDataLoaded() ){
			if(Util.isOnline(context)){
				DownloadStationDataTask dt = new DownloadStationDataTask();
				dt.execute(station);
				
			}
			else{
				pdToLoadStationData.dismiss();
				Toast.makeText(context, context.getString(R.string.notOnlineNote), Toast.LENGTH_SHORT).show();
			}
		}
		else{
			pdToLoadStationData.dismiss();
			choiceSensorData(station);
		}
		
		return true;
	}
	
	
	
	private void choiceSensorData(final Station station){
		String type= station.getType();
		SensorData sensorData = null;
		
		if(type.equals(Global.KEY_IDRO)){
			sensorData = station.getLividroSensorData();
			if( sensorData!=null)
				startGraphActivity( station, sensorData);
			else showSensorDataAlertDialog();
			return;
		}
		else if(type.equals(Global.KEY_METEO)){
			sensorData=station.getPrecSensorData();
			if( sensorData!=null)
				startGraphActivity( station, sensorData);
			else showSensorDataAlertDialog();
			return;
		}
		else if(station.getType().equals(Global.KEY_IDRO_METEO)){
			station.getLividroSensorData();
			
			final String[] options = { context.getString(R.string.idroValue), context.getString(R.string.pluvioValue) };
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder
				.setTitle("Che valori desideri?")
				.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String option = options[which];
					SensorData sensorData = null;
					
					if(option.contains("idro")){
						sensorData = station.getLividroSensorData();
						if( sensorData!=null)
							startGraphActivity(station, sensorData);
						else 
							showSensorDataAlertDialog();
						return;
					}
					else if(option.contains("pluvio")){
						sensorData = station.getPrecSensorData();
						if( sensorData!=null)
							startGraphActivity(station, sensorData);
						else 
							showSensorDataAlertDialog();
						return;
					}
						
					dialog.dismiss();
				}
			})
				.setCancelable(true)
				.show();
			return;
		}
		
	}
	
	
	
	
	private void startGraphActivity(Station station, SensorData sensorData){
		Graph graph = new Graph(context);
		graph.setUnitMeasurement( sensorData.getUnitMeasurement() );
		graph.setValue( sensorData.getValue() );
		graph.setDate(sensorData.getDate());
		graph.setStationName(station.getName());
		graph.setType(sensorData.getType());

		Intent graphIntent = graph.execute( context );
		context.startActivity(graphIntent);
	}
	
	
	
	private class DownloadStationDataTask extends AsyncTask<Station, Void, Station> {
		
		protected Station doInBackground(Station... station) {
			try{
				Util.loadStationData(station[0]);
			} catch (XmlNullExc e){ 
			} catch (MalformedXmlExc e){}
			
			return station[0];
		}
		
		protected void onPostExecute(Station station) {
			try {
				if (pdToLoadStationData != null)
					pdToLoadStationData.dismiss();
			} catch (Exception e) {}
			
			pdToLoadStationData.dismiss();
			choiceSensorData(station);
		}
	}

	
	public ArrayList<StationOverlayItem> getOverlay(){
		return m_overlays;
	}

	
	
	/**
	 * message displays when a sensorData for a station isn't available
	 */
    private void showSensorDataAlertDialog(){
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog
			.setTitle(R.string.alert)
			.setMessage(R.string.alertSensorDataMessage)
			.setNegativeButton("Chiudi",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			})
	       	.show();
    }
	
}
