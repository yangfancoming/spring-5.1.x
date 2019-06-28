

package org.springframework.oxm.jibx;

import java.util.ArrayList;

public class Flights {

	protected ArrayList<FlightType> flightList = new ArrayList<>();

	public void addFlight(FlightType flight) {
		flightList.add(flight);
	}

	public FlightType getFlight(int index) {
		return flightList.get(index);
	}

	public int sizeFlightList() {
		return flightList.size();
	}
}
