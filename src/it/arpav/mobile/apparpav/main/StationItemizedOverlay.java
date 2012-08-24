package it.arpav.mobile.apparpav.main;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class StationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;

	
	public StationItemizedOverlay( Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("This will end the activity");
		builder.setCancelable(true);
		builder.setPositiveButton("I agree", new OkOnClickListener());
		builder.setNegativeButton("No, no", new CancelOnClickListener());
		AlertDialog dialog = builder.create();
		dialog.show();
		return true;
	}

	private final class CancelOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(context, "You clicked yes", Toast.LENGTH_LONG)
					.show();
		}
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(context, "You clicked no", Toast.LENGTH_LONG).show();
		}
	}
}
