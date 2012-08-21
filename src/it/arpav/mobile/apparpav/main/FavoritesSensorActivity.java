package it.arpav.mobile.apparpav.main;

import java.util.ArrayList;

import it.arpav.mobile.apparpav.types.Station;
import it.arpav.mobile.apparpav.utils.Util;
import it.arpav.mobile.apparpav.utils.XMLParser;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class FavoritesSensorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_sensor_empty);
        
        updateDispaly();
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

		final Button btn1 = (Button) this.findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.getListStations();
				boolean check = Util.loaded();
				if(check){
				
					Toast toast = Toast.makeText(getBaseContext(), "memorizzato0", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 0, 25);
					toast.show();
				}
				else{
					
					Toast toast = Toast.makeText(getBaseContext(), "non memorizzato0", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 0, 25);
					toast.show();
				}
				
			}
		});
		

//		String url = "http://www.arpa.veneto.it/upload_teolo/dati_xml/Ultime48ore_idx.xml";
//		XMLParser xmlParser = new XMLParser();
//		String xml = xmlParser.getXmlFromUrl(url);
//		Log.d("xml", xml);
//		Document doc = xmlParser.getDomElementFromString(xml);
//		ArrayList<Station> listStation = xmlParser.parseXmlIndexStations(doc);
//		
//		String num = "n"; 
//		num = listStation.get(1).getLink();
		
		boolean check = Util.loaded();
		if(check){
		
			Toast toast = Toast.makeText(getBaseContext(), "memorizzato", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 25);
			toast.show();
		}
		else{
			
			Toast toast = Toast.makeText(getBaseContext(), "non memorizzato", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 25);
			toast.show();
		}
		
    }
    
    
}
