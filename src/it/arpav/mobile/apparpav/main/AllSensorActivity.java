package it.arpav.mobile.apparpav.main;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class AllSensorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sensor);
        
        updateDispaly();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_all_sensor, menu);
        return true;
    }

    
    public void updateDispaly(){
    	
		// btn_favorites 
		final Button btnFavorites = (Button) this.findViewById(R.id.btn_favorites );
		btnFavorites.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer));
		btnFavorites.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnFavorites.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.bg_footer_reversed));
				Intent newintent = new Intent();
				newintent.setClass( getApplication(), FavoritesSensorActivity.class);

				startActivity(newintent);
			}
		});

		
		// btn_all is active
		final Button btnAll = (Button) this.findViewById(R.id.btn_all);
		btnAll.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_footer_reversed));
    	


    }
    
    
    
    
}


