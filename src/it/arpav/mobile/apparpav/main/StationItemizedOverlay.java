package it.arpav.mobile.apparpav.main;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class StationItemizedOverlay extends BalloonItemizedOverlay<CustomOverlayItem> {
	private ArrayList<CustomOverlayItem> mOverlays = new ArrayList<CustomOverlayItem>();
	private Context context;

	
	public StationItemizedOverlay( Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}

	public void addOverlay(CustomOverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		
		Toast.makeText(context, "onBalloonTap for overlay index " + index + item.getStation().getId(),
				Toast.LENGTH_LONG).show();
		return true;
	}

}
