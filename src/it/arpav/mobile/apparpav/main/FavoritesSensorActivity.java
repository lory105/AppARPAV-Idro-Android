package it.arpav.mobile.apparpav.main;

import it.arpav.mobile.apparpav.utils.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class FavoritesSensorActivity extends Activity {
	private ProgressDialog pdToLoadStations = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_favorites_sensor);
        
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
