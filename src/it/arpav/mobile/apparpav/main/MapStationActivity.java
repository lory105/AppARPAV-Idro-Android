package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.exceptions.XmlNullExc;
import it.arpav.mobile.apparpav.utils.Util;

import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class MapStationActivity extends MapActivity {
	
	//action id of button_menu
	private static final int ID_MY_LOCATION     = 1;
	private static final int ID_NEAREST_STATION = 2;
	private static final int ID_ACTIVE_GPS 		= 3;

	// -------------------------------------------------------
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;
	
	
	// -------------------------------------------------------
	// initial coordinates to center map
	private static int initialLat = (int) (45.6945683 *1E6);
	private static int initialLon = (int) (11.8765886 *1E6);
	// -------------------------------------------------------
	
	private ProgressDialog pdToLoadStations = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_map_sensor);
        
		// Configure the Map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapController = mapView.getController();

		// jump the center of the map to these coordinates
        GeoPoint point = new GeoPoint(initialLat , initialLon);
        mapController.animateTo(point);
        
		mapController.setZoom(9); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
//		if ( Util.listStationIsLoaded() ){
//			Log.d("listStation", "is loaded");
//			try{
//				Integer i= Util.getListStations(this).size();
//				Log.d("listStation", i.toString());
//			} catch (XmlNullExc e){}
//		}
//		else Log.d("listStation", "is NOT loaded");
		
		// -------------------------------------------------------
		final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000,
				50, new GeoUpdateHandler());

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		
		
		if( Util.isOnline(this)){
			pdToLoadStations = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loadingData), true, true);
        
//			new CountDownTimer(2000, 1000) {
//				public void onTick(long millisUntilFinished) {}
//				public void onFinish() {
					loadStations();
					if(!gpsEnabled )
						showGpsAlertDialog();
					
					updateDispaly();
//				}
//			}.start();
		}
		else{
			showNetworkAlertDialog();
			updateDispaly();
		}
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.red16);
		StationItemizedOverlay itemizedoverlay = new StationItemizedOverlay(drawable, this);
		
		GeoPoint point2 = new GeoPoint((int) (45.7945683 *1E6),(int) (11.8165886 *1E6));
		OverlayItem overlayitem = new OverlayItem(point2, "Hola, Mundo!", "I'm in Mexico City!");
		
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);

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
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle(R.string.alertDialogGpsTitle);
 
		// set dialog message
		alertDialogBuilder
			.setMessage(R.string.alertDialogGpsMessage)
			.setCancelable(false)
				.setPositiveButton("Si",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
    }
    
    
    public void updateDispaly(){
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
		// ---------------------------------------------
		
		if(pdToLoadStations != null)
			pdToLoadStations.dismiss();
        
		
//		loadStations();
//		pdToLoadStations.dismiss();
		
		if( Util.listStationIsLoaded() != false){
			// TODO
		}
		
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
			Toast.makeText(getApplicationContext(), "XmlNullExc", Toast.LENGTH_SHORT).show();
		}
    }
    
    
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
    
}
