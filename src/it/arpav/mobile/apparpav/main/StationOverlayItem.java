package it.arpav.mobile.apparpav.main;


import it.arpav.mobile.apparpav.model.Station;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


/**
 * Personalization of Overlay Item of google maps. Each OverlayItem contains a Station pointer
 * @author Giacomo Lorigiola
 */
public class StationOverlayItem extends OverlayItem {

	protected Station station;
	
	public StationOverlayItem(GeoPoint point, Station station) {
		super(point, station.getName(), "Bacino: " + station.getReservoir());
		this.station = station;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}
	
}
