package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.types.Station;
import it.arpav.mobile.apparpav.utils.Util;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.R;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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


public class MapStationActivity extends MapActivity {
	
	// User's preferences key
	String MY_PREFERENCES = "MyPreferences";
	String GPS_ALERT_DIALOG_KEY =	"gps_alert_dialog_preferences";
	
	
	//action id of button_menu
	private static final int ID_MY_LOCATION     = 1;
	private static final int ID_NEAREST_STATION = 2;
	private static final int ID_ACTIVE_GPS 		= 3;

	// -------------------------------------------------------
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private TapControlledMapView mapView;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;
	private StationItemizedOverlay idroStationItemizedOverlay;
	private StationItemizedOverlay meteoStationItemizedOverlay;
	
	// -------------------------------------------------------
	// initial coordinates to center map
	private static int initialLat = (int) (45.6945683 *1E6);
	private static int initialLon = (int) (11.8765886 *1E6);
	// -------------------------------------------------------
	
	
	//private CheckBox checkBoxGpsAlert = null;
	private ProgressDialog pdToLoadStations = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_map_sensor);
        
        Log.d("111", "111");
        
		// Configure the Map
		mapView = (TapControlledMapView) findViewById(R.id.mapview);
		Log.d("222", "111");
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapController = mapView.getController();
        
		// dismiss balloon upon single tap of MapView (iOS behavior) 
		mapView.setOnSingleTapListener(new OnSingleTapListener() {		
			@Override
			public boolean onSingleTap(MotionEvent e) {
				if(idroStationItemizedOverlay != null)
					idroStationItemizedOverlay.hideAllBalloons();
				return true;
			}
		});
        Log.d("333", "111");
		// jump the center of the map to these coordinates
        GeoPoint point = new GeoPoint(initialLat , initialLon);
        mapController.animateTo(point);
        
		mapController.setZoom(9); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("444", "111");

		
		// -------------------------------------------------------

		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000,
				50, new GeoUpdateHandler());
        Log.d("555", "111");
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		
		mapOverlays = mapView.getOverlays();
		
		updateDisplay();
		
		if( Util.isOnline(this)){
			new InitialTask().execute();
		}
		else{
			if( Util.listStationIsLoaded() )
				populateMap();
			showNetworkAlertDialog();
		}
		
        Log.d("666", "111");
		
		
//		Drawable drawable = this.getResources().getDrawable(R.drawable.red16);
//		idroStationItemizedOverlay = new StationItemizedOverlay(drawable, mapView);
//        Log.d("666", "222");
//		// set iOS behavior attributes for overlay
//		idroStationItemizedOverlay.setShowClose(false);
//		Log.d("777", "111");
//		idroStationItemizedOverlay.setShowDisclosure(true);
//		idroStationItemizedOverlay.setSnapToCenter(false);
//		
//		
//		GeoPoint point2 = new GeoPoint((int) (45.7945683 *1E6),(int) (11.8165886 *1E6));
//		
//		OverlayItem overlayitem = new OverlayItem(point2, "Hola, Mundo!", "I'm in Mexico City!");
//		
//		idroStationItemizedOverlay.addOverlay(overlayitem);
//		mapOverlays.add(idroStationItemizedOverlay);
//        Log.d("888", "111");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_favorites_sensor, menu);
        return true;
    }

    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    
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
    
    
    
    private void showGpsAlertDialog(){	
		final CheckBox checkBoxGpsAlert = new CheckBox(this);
		checkBoxGpsAlert.setText( R.string.checkBoxGps);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		    LinearLayout.LayoutParams.FILL_PARENT));
		linearLayout.setOrientation(1);     
		linearLayout.addView(checkBoxGpsAlert);
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog
			.setView(linearLayout)
			.setTitle(R.string.alertDialogGpsTitle)
			.setMessage(R.string.alertDialogGpsMessage)
			.setPositiveButton("Si",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(checkBoxGpsAlert.isChecked() ){
						Toast.makeText(getApplicationContext(), "checked", Toast.LENGTH_SHORT).show();
						SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(GPS_ALERT_DIALOG_KEY, "not show");
						editor.commit();
					}
						
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					if(checkBoxGpsAlert.isChecked() ){
						Toast.makeText(getApplicationContext(), "checked", Toast.LENGTH_SHORT).show();
						SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(GPS_ALERT_DIALOG_KEY, "not show");
						editor.commit();
					}
				}
			})
	       	.show();
    }
    
    
    public void updateDisplay(){
		// ---------------------------------------------
    	// create menu option
		ActionItem myLocationItem 	= new ActionItem(ID_MY_LOCATION, "Mia posizione", getResources().getDrawable(R.drawable.location));
		ActionItem nearestItem   	= new ActionItem(ID_NEAREST_STATION, "Stazione piu vicina", getResources().getDrawable(R.drawable.location));
        ActionItem activeGpsItem 	= new ActionItem(ID_ACTIVE_GPS, "Attiva GPS", getResources().getDrawable(R.drawable.gps));
    	
//		ActionItem myLocationItem 	= new ActionItem(ID_MY_LOCATION, "Next");
//		ActionItem nearestItem   	= new ActionItem(ID_NEAREST_STATION, "Prev");
//        ActionItem activeGpsItem 	= new ActionItem(ID_ACTIVE_GPS, "Find");
    	
        final QuickAction mQuickAction 	= new QuickAction(this );
        
		mQuickAction.addActionItem(myLocationItem);
		mQuickAction.addActionItem(nearestItem);
		mQuickAction.addActionItem(activeGpsItem);
		
		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
				
				if (actionId == ID_MY_LOCATION) {
					//Intent newintent = new Intent( getApplicationContext(), ConfActivity.class);
					//newintent.putExtra("reload", true);
					//startActivity(newintent);
				} else if (actionId == ID_NEAREST_STATION ) {
					Toast.makeText(getApplicationContext(), "I have no info this time", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
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
		// ---------------------------------------------
        
    	// btn_favorites is active da togliere
		final Button btnFavorites = (Button) this.findViewById(R.id.btn_favorites);
		btnFavorites.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer_reversed));
		
		// btn_all
		final Button btnAll = (Button) this.findViewById(R.id.btn_all);
		btnAll.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer));
		btnAll.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnAll.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.bg_footer_reversed));
				Intent newintent = new Intent();
				newintent.setClass( getApplication(), AllSensorActivity.class);

				startActivity(newintent);
			}
		});
		// ---------------------------------------------S
		
    }
    

    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	//finish();
    }
    
    
    // parse the xml index stations and create the Util.listStations
    private void loadStations(){
		try{
			Util.getListStations(this);
		} catch( XmlNullExc e ){
			// TODO
			Toast.makeText(getApplicationContext(), "XmlNullExc - loadStatios() Map Activity ", Toast.LENGTH_SHORT).show();
		}
    }
    
    
//    static public void loadStationData( Context context, Station station){
//    	
//    }
    
    
	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			//createMarker();
			//mapController.animateTo(point); // mapController.setCenter(point);

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
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
	 * ?????????? 
	 */
	private class InitialTask extends AsyncTask<Void, Void, Void> {
		protected void onPreExecute() {
			if( !Util.listStationIsLoaded() )
				pdToLoadStations = ProgressDialog.show(MapStationActivity.this, getString(R.string.loading), getString(R.string.loadingData), true, false);

			// se non si vede il progress dialog, usare qusto:	
//			MapStationActivity.this.pdToLoadStations = new ProgressDialog(MapStationActivity.this);
//			MapStationActivity.this.pdToLoadStations.setTitle(getString(R.string.loading));
//			MapStationActivity.this.pdToLoadStations.setMessage(getString(R.string.loadingData));
//			MapStationActivity.this.pdToLoadStations.setIndeterminate(true);
//			MapStationActivity.this.pdToLoadStations.setCancelable(false);
//			MapStationActivity.this.pdToLoadStations.show();
		}
		
		protected Void doInBackground(Void... unused) {
//			Context c = MapStationActivity.this.getApplicationContext();
			loadStations();
			return null;
		}

		protected void onPostExecute(Void unused) {
			try {
				if (MapStationActivity.this.pdToLoadStations != null)
					MapStationActivity.this.pdToLoadStations.dismiss();
			} catch (Exception e) {}
			populateMap();
			// check if gps is activated
			if(! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
				// check if gps alert dialog user's preference is stored 
				SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
				String textData = prefs.getString(GPS_ALERT_DIALOG_KEY, "show");
				if( textData.equals("show") )
					showGpsAlertDialog();
			}
		}
		
	} 
	
	

	
	
	
	
	protected void populateMap(){
		List<ArrayList<Station>> listStations = null;
		try{
			listStations = Util.getListStations(this);
		} catch(XmlNullExc e){ 
			Toast.makeText(getApplicationContext(), "XmlNullExc - populateMap() Map Activity ", Toast.LENGTH_SHORT).show();
		}
		
		ArrayList<Station> idroListStations = listStations.get(0);
		ArrayList<Station> meteoListStations = listStations.get(1);
		
		Drawable drawableIdro = this.getResources().getDrawable(R.drawable.red16);
		Drawable drawableMeteo = this.getResources().getDrawable(R.drawable.blue16);
		
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
		
		mapView.invalidate();
		
	}


}
