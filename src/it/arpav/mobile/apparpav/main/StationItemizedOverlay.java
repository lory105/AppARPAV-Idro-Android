package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.types.Station;
import it.arpav.mobile.apparpav.utils.Util;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.londatiga.android.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;



public class StationItemizedOverlay extends BalloonItemizedOverlay<StationOverlayItem> {
	private ArrayList<StationOverlayItem> mOverlays = new ArrayList<StationOverlayItem>();
	private Context context;
	private ProgressDialog pdToLoadStationData = null;
	
	public StationItemizedOverlay( Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
	}

	@Override
	protected StationOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}

	public void addOverlay(StationOverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected boolean onBalloonTap(int index, StationOverlayItem item) {
		Station station=item.getStation();
		if(station.getData()==null){
			if(Util.isOnline(context)){
				DownloadStationDataTask dt = new DownloadStationDataTask();
				dt.execute(station);
				try{
					dt.get(100, TimeUnit.MILLISECONDS);
//					dt.get();
				} 
				catch( TimeoutException e){
					Toast.makeText(context, context.getString(R.string.interruptedException), Toast.LENGTH_SHORT).show();
				} 
				catch( InterruptedException e){
					Toast.makeText(context, context.getString(R.string.interruptedException), Toast.LENGTH_SHORT).show();
				} catch( ExecutionException e){
					Toast.makeText(context, context.getString(R.string.interruptedException), Toast.LENGTH_SHORT).show();
				}
				
			}
			else
				Toast.makeText(context, context.getString(R.string.notOnlineNote), Toast.LENGTH_SHORT).show();
		}
		else
			startGraphActivity(station);
		
		
		// TODO: start chart or get sul asynch task per attendere un tempo massimo
		
		return true;
	}
	
	
	
	private void startGraphActivity(Station station){
		Graph graph = new Graph(context);
		graph.setTime( station.getData().getTime() );
		graph.setValue( station.getData().getValue() );
		String[] date=station.getData().getDate();
		graph.setType(station.getData().getType());
		
		if(date!=null)
			graph.setTitle( station.getName() + ", dati dal "+ date[0]+ " al " + date[date.length-1]);

		
		Intent graphIntent = graph.getIntent( context );
		context.startActivity(graphIntent);
	}
	
	
	
	
	private class DownloadStationDataTask extends AsyncTask<Station, Void, Station> {
		protected void onPreExecute() {
				pdToLoadStationData = ProgressDialog.show(context, context.getString(R.string.loading), context.getString(R.string.loadingData), true, false);
		}
		
		protected Station doInBackground(Station... station) {
			
			try{
				
					Util.loadStationData(station[0]);
			} catch (XmlNullExc e){ 
				// TODO
			}
			
			return station[0];
		}
		
		protected void onPostExecute(Station station) {
			try {
				if (pdToLoadStationData != null)
					pdToLoadStationData.dismiss();
			} catch (Exception e) {}
			
			pdToLoadStationData.dismiss();
 			startGraphActivity(station);
		}
				
			



	} 

}
