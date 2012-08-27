package it.arpav.mobile.apparpav.main;


/***
 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */


import it.arpav.mobile.apparpav.types.Station;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class StationOverlayItem extends OverlayItem {

	protected Station station;
	
	public StationOverlayItem(GeoPoint point, Station station) {
		super(point, station.getName(), station.getReservoir());
		this.station = station;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}
	
}
