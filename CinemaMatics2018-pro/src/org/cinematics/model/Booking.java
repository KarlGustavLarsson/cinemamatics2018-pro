package org.cinematics.model;

import org.cinematics.handlers.DataManager;

public class Booking {
	
	private int bookingId;
	private Integer customerID;
	private Integer showID;
		
	public void setCustomerID(Integer id) {
		customerID = id;
	}
	public Integer getCustomerID() {
		return customerID;
	}
	
	public void setShowID(Integer showID) {
		this.showID = showID;
	}
	
	public Show getShow() {
		return DataManager.getShowFromID(showID);
	}
	
	public void setBookingId(Integer showID) {
		this.showID = showID;
	}
	
	public int getBookingId() {
		return bookingId;
	}
}
