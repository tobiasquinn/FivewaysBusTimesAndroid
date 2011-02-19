package com.tobiasquinn.fivewaysbustimes;

public class Bus {
	private String number;
	private String destination;
	private String arrivetime;
	
	public Bus(String number, String destination, String arrivetime)
	{
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
	
	public String getArrivetime() {
		return arrivetime;
	}

	// Intended for debug only
	@Override
	public String toString() {
		return "Number: "+this.getNumber()+" Destination: "+this.getDestination()+" Time: "+this.getArrivetime();
	}
}
