package it.arpav.mobile.apparpav.main;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.R;
import it.arpav.mobile.apparpav.utils.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MapSensorActivity extends Activity {
	
	//action id of button_menu
	private static final int ID_MY_LOCATION     = 1;
	private static final int ID_NEAREST_STATION = 2;
	private static final int ID_ACTIVE_GPS 		= 3;
	
	private ProgressDialog pdToLoadStations = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_map_sensor);
        
        pdToLoadStations = ProgressDialog.show(this, "Caricamento..", "Ricerca delle stationi", true, true);
        
        new CountDownTimer(1000, 1000) {
           public void onTick(long millisUntilFinished) {}

           public void onFinish() {
               updateDispaly();
           }
        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_favorites_sensor, menu);
        return true;
    }

    
    
    public void updateDispaly(){
		// ---------------------------------------------
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
        
    	 // btn_favorites is active
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


		Util.getListStations();
		if( Util.listStationIsLoaded() != false)
			pdToLoadStations.dismiss();
			
//			Toast toast = Toast.makeText(getBaseContext(), "non memorizzato", Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.BOTTOM, 0, 25);
//			toast.show();
		
		
    }
    

    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	finish();
    }
    
    
}
