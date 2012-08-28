package it.arpav.mobile.apparpav.main;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class StationItemizedOverlay extends BalloonItemizedOverlay<StationOverlayItem> {
	private ArrayList<StationOverlayItem> mOverlays = new ArrayList<StationOverlayItem>();
	private Context context;

	
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
		
		Toast.makeText(context, "onBalloonTap for overlay index " + index + item.getStation().getId(),
				Toast.LENGTH_LONG).show();
		
		// TODO: start chart
		Graph graph = new Graph();
		Intent graphIntent = graph.getIntent( context );
		context.startActivity(graphIntent);
		
		return true;
	}

}
