package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.exceptions.MalformedXmlExc;
import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.utils.Util;
import it.arpav.mobile.apparpav.main.menu.*;
import it.arpav.mobile.apparpav.model.Station;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;


/**
 * Main activity for view the google maps
 * @author Giacomo Lorigiola
 */
public class MapStationActivity extends MapActivity {
	
	// User's preferences key
	String MY_PREFERENCES = "MyPreferences";
	String LOCALIZATION_ALERT_DIALOG_KEY =	"localization_alert_dialog_preferences";
	
	//action id of button_menu
	private static final int ID_UPDATE	        	= 1;
	private static final int ID_MY_LOCATION     	= 2;
	private static final int ID_ACTIVE_LOCALIZATION	= 3;

	// -------------------------------------------------------
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private TapControlledMapView mapView;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;
	private StationItemizedOverlay idroStationItemizedOverlay;
	private StationItemizedOverlay meteoStationItemizedOverlay;
	private StationItemizedOverlay idroMeteoStationItemizedOverlay;
	private ProgressDialog pdToLoadStations = null;
	
	// initial coordinates to center map
	private static int initialLat = 	(int) (45.6945683 *1E6);
	private static int initialLon = 	(int) (11.8765886 *1E6);
	// Veneto limits
	private static int latTopVeneto =	(int) (46.7300000 *1E6);
	private static int latBottomVeneto =(int) (44.8000000 *1E6);
	private static int lonLeftVeneto =	(int) (10.4000000 *1E6);
	private static int lonRightVeneto =	(int) (13.3000000 *1E6);
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_sensor);
                
		// Configure the Map
		mapView = (TapControlledMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapController = mapView.getController();
        
		// dismiss balloon upon single tap of MapView (iOS behavior) 
		mapView.setOnSingleTapListener(new OnSingleTapListener() {		
			@Override
			public boolean onSingleTap(MotionEvent e) {
				if(idroStationItemizedOverlay != null){
					idroStationItemizedOverlay.hideAllBalloons();
				 	meteoStationItemizedOverlay.hideAllBalloons();
				}
				return true;
			}
		});
		
		// jump the center of the map to these coordinates
        GeoPoint point = new GeoPoint(initialLat , initialLon);
        mapController.animateTo(point);
        
		mapController.setZoom(9); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
		// TODO test if work: the point of location had to move in the map if the phone location moves
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				0, new GeoUpdateHandler());
		
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(myLocationOverlay);

		setDisplay();
		
		if( Util.isOnline(this)){
			LoadStationIndexTask ds = new LoadStationIndexTask();
			ds.execute();

		}
		else{
			if( Util.listStationIsLoaded() )
				populateMap();
			
			showNetworkAlertDialog();
		}
		
    }

    @Override
    protected void onStart(){
    	super.onStart();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}

	@Override
	protected void onPause() {
		super.onResume();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
    
    
	/**
	 * create menu button
	 */
    public void setDisplay(){
    	// create menu option
    	ActionItem updateItem	 		 = new ActionItem(ID_UPDATE, "Aggiorna", getResources().getDrawable(R.drawable.update));
		ActionItem myLocationItem 		 = new ActionItem(ID_MY_LOCATION, "Mia posizione", getResources().getDrawable(R.drawable.location));
        ActionItem activeLocalizationItem 		 = new ActionItem(ID_ACTIVE_LOCALIZATION, "Attiva localizzazione", getResources().getDrawable(R.drawable.gps));
        
        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        updateItem.setSticky(true);
        myLocationItem.setSticky(true);
        activeLocalizationItem.setSticky(true);
        
        final QuickAction mQuickAction 	= new QuickAction(this, QuickAction.VERTICAL );
        
        mQuickAction.addActionItem(updateItem);
		mQuickAction.addActionItem(myLocationItem);
		mQuickAction.addActionItem(activeLocalizationItem);
		
		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
				switch (actionId){
					case ID_UPDATE:
						Util.setNullListStations();
						Intent newintent = new Intent( getBaseContext(), MapStationActivity.class);
						startActivity(newintent);
						finish();
						break;
					case ID_MY_LOCATION:
						if(! isLocalizationActive()){
							showLocalizationAlertDialog();
						}
						else{
							GeoPoint myPosition = myLocationOverlay.getMyLocation();
							
							if(myPosition != null )
								animateToMyPosition(myPosition);
							else{
								Toast.makeText(getApplicationContext(), "Attendi il fix del Gps o l'attivazione della rete", Toast.LENGTH_SHORT).show();
								myLocationOverlay.runOnFirstFix(new Runnable() {
									public void run() {
										animateToMyPosition(myLocationOverlay.getMyLocation());
									}
								});
							}
						}
						break;
					case ID_ACTIVE_LOCALIZATION:
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
						break;
				}
				
			}
		});
		
		ImageButton buttonMenu = (ImageButton) findViewById(R.id.button_menu);
	    buttonMenu.setOnClickListener(new View.OnClickListener() {
		       @Override
		       public void onClick(View v) {
					mQuickAction.show(v);
		       }
		    });
        
    }
    
     
	/**
	 * asks Util class to load the list of stations, if this is not already done
	 */
    private void loadStations(){
		try{
			Util.getListStations(this);
		} catch ( XmlNullExc e ){
			Log.d("MapStationActivity-loadStations","1");
		} catch ( MalformedXmlExc e){}
    }
    

	/**
	 * AsynchTask to load the index of station 
	 */
	private class LoadStationIndexTask extends AsyncTask<Void, Void, Void> {
		protected void onPreExecute() {
			if( !Util.listStationIsLoaded() )
				pdToLoadStations = ProgressDialog.show(MapStationActivity.this, getString(R.string.loading), getString(R.string.loadingData), true, false);

		}
		
		protected Void doInBackground(Void... unused) {
			loadStations();
			return null;
		}

		protected void onPostExecute(Void unused) {
			try {
				if (MapStationActivity.this.pdToLoadStations != null)
					MapStationActivity.this.pdToLoadStations.dismiss();
			} catch (Exception e) {}
			populateMap();
			// if localization isn't active
			if(! isLocalizationActive() ){
				// check if localization alert dialog user's preference is stored 
				SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
				String textData = prefs.getString(LOCALIZATION_ALERT_DIALOG_KEY, "show");
				if( textData.equals("show") )
					showInitialLocalizationAlertDialog();
			}
		}
		
	} 
	

	/**
	 * Insert the StationItemizeOverlay on the map 
	 */
	protected void populateMap(){
		List<ArrayList<Station>> listStations = null;
		// if there was some problem with network or with Arpav server, and list stations aren't loaded,
		// return without populated map
		if( !Util.listStationIsLoaded() ){
			Toast.makeText(getApplicationContext(), "Problemi di connessione o di ricezione dati..", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try{
			listStations = Util.getListStations(this);

			ArrayList<Station> idroListStations = listStations.get(0);
			ArrayList<Station> meteoListStations = listStations.get(1);
			ArrayList<Station> idroMeteoListStations = listStations.get(2);
		
			Drawable drawableIdro = this.getResources().getDrawable(R.drawable.red);
			Drawable drawableMeteo = this.getResources().getDrawable(R.drawable.blue);
			Drawable drawableIdroMeteo = this.getResources().getDrawable(R.drawable.green);
		
			for(int i=0; i<idroListStations.size(); i++ ){
				Station station = idroListStations.get(i);
				idroStationItemizedOverlay = new StationItemizedOverlay(drawableIdro, mapView);
				idroStationItemizedOverlay.setShowClose(false);
				idroStationItemizedOverlay.setShowDisclosure(true);
				idroStationItemizedOverlay.setSnapToCenter(false);
			
				GeoPoint point = new GeoPoint((int) ( station.getCoordinateY()*1E6),(int) (station.getCoordinateX()*1E6));
				StationOverlayItem stationOverlayitem = new StationOverlayItem(point, station );
			
				idroStationItemizedOverlay.addOverlay(stationOverlayitem);
				mapOverlays.add(idroStationItemizedOverlay);
			}
		
			for(int i=0; i<meteoListStations.size(); i++ ){
				Station station = meteoListStations.get(i);
				meteoStationItemizedOverlay = new StationItemizedOverlay(drawableMeteo, mapView);
				meteoStationItemizedOverlay.setShowClose(false);
				meteoStationItemizedOverlay.setShowDisclosure(true);
				meteoStationItemizedOverlay.setSnapToCenter(false);
			
				GeoPoint point = new GeoPoint((int) ( station.getCoordinateY()*1E6),(int) (station.getCoordinateX()*1E6));
				StationOverlayItem stationOverlayitem = new StationOverlayItem(point, station );
			
				meteoStationItemizedOverlay.addOverlay(stationOverlayitem);
				mapOverlays.add(meteoStationItemizedOverlay);
			}
			
			
			for(int i=0; i<idroMeteoListStations.size(); i++ ){
				Station station = idroMeteoListStations.get(i);
				idroMeteoStationItemizedOverlay = new StationItemizedOverlay(drawableIdroMeteo, mapView);
				idroMeteoStationItemizedOverlay.setShowClose(false);
				idroMeteoStationItemizedOverlay.setShowDisclosure(true);
				idroMeteoStationItemizedOverlay.setSnapToCenter(false);
			
				GeoPoint point = new GeoPoint((int) ( station.getCoordinateY()*1E6),(int) (station.getCoordinateX()*1E6));
				StationOverlayItem stationOverlayitem = new StationOverlayItem(point, station );
			
				idroMeteoStationItemizedOverlay.addOverlay(stationOverlayitem);
				mapOverlays.add(idroMeteoStationItemizedOverlay);
			}
			
			mapView.invalidate();
		
		} catch(XmlNullExc e){}
		  catch(MalformedXmlExc e){}
		
	}

	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    
	/**
	 * message displays when app is launch if network isn't enabled
	 */
    private void showNetworkAlertDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle(R.string.alertDialogNetworkTitle);
 
		// set dialog message
		alertDialogBuilder
			.setMessage(R.string.alertDialogNetworkMessage)
			.setCancelable(false)
			.setPositiveButton(R.string.btnClose, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
 
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
    }
    
    
	/**
	 * message displays when app is launch if localization isn't active
	 */
    private void showInitialLocalizationAlertDialog(){	
		final CheckBox checkBoxLocalizationAlert = new CheckBox(this);
		checkBoxLocalizationAlert.setText( R.string.dontAskAgain);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		    LinearLayout.LayoutParams.FILL_PARENT));
		linearLayout.setOrientation(1);     
		linearLayout.addView(checkBoxLocalizationAlert);
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog
			.setView(linearLayout)
			.setTitle(R.string.alertDialogLocalizationTitle)
			.setMessage(R.string.alertDialogLocalizationMessage)
			.setPositiveButton("Si",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(checkBoxLocalizationAlert.isChecked() ){
						SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(LOCALIZATION_ALERT_DIALOG_KEY, "not show");
						editor.commit();
					}
						
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					if(checkBoxLocalizationAlert.isChecked() ){
						SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(LOCALIZATION_ALERT_DIALOG_KEY, "not show");
						editor.commit();
					}
				}
			})
	       	.show();
    }
    
    
	/**
	 * message displays when user want to show her position but localization isn't active
	 */
    private void showLocalizationAlertDialog(){
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog
			.setTitle(R.string.alertDialogLocalizationTitle)
			.setMessage(R.string.alertDialogLocalizationMessage)
			.setPositiveButton("Si",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					myLocationOverlay.runOnFirstFix(new Runnable() {
						public void run() {
							animateToMyPosition(myLocationOverlay.getMyLocation());
						}
					});
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			})
	       	.show();
    }
	
    
	/**
	 * message displays when user is out of Veneto limits
	 */
    private void showOutLimitsAlertDialog(final GeoPoint myPosition){ 
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog
			.setTitle(R.string.alert)
			.setMessage(R.string.alertDialogOutLimitMessage)
			.setPositiveButton("Si",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					mapController.animateTo(myPosition);
					mapController.setZoom(10);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			})
	       	.show();
    }
	
	/**
	 * check if Localization is active
	 */
	public boolean isLocalizationActive(){
		if( locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) )
			return true;
		return false;
	}
	
	
	/**
	 * check if user is out of Veneto limits
	 */
	private boolean isUserOutLimits(GeoPoint myPosition){
		int lat = myPosition.getLatitudeE6();

		if( lat > latTopVeneto || lat < latBottomVeneto )
			return true;
		
		int lon = myPosition.getLongitudeE6();		
		if( lon > lonRightVeneto || lon < lonLeftVeneto )
			return true;
		
		return false;
	}

	
	/**
	 * move the map to user position
	 */
	private void animateToMyPosition(GeoPoint myPosition){
		// check if user is out of Veneto limits
		if(isUserOutLimits(myPosition))
			showOutLimitsAlertDialog(myPosition);	
		else{
			mapController.animateTo(myPosition);
			mapController.setZoom(11);
		}
	}

	
	public class GeoUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
	
}
