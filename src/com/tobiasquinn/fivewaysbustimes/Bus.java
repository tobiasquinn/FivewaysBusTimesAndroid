package com.tobiasquinn.fivewaysbustimes;

import java.util.Calendar;

import android.text.format.DateFormat;

public class Bus {
	private String number;
	private String destination;
	private Calendar arrivetime;

	private char state = ' ';

	public Bus(String number, String destination, Calendar arrivetime) {
		this.number = number;
		this.destination = destination;
		this.arrivetime = arrivetime;
	}

	public String getNumber() {
		return number;
	}

	public String getDestination() {
		return destination;
	}

	public String getArrivetimeText() {
		return (String) DateFormat.format("kk:mm", arrivetime);
	}

	public void setState(char state) {
		this.state = state;
	}

	public char getState() {
		return state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivetime == null) ? 0 : arrivetime.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bus other = (Bus) obj;
		if (arrivetime == null) {
			if (other.arrivetime != null)
				return false;
		} else if ((arrivetime.get(Calendar.HOUR_OF_DAY) != other.arrivetime.get(Calendar.HOUR_OF_DAY))
				|| (arrivetime.get(Calendar.MINUTE) != other.arrivetime.get(Calendar.MINUTE)))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}
}
