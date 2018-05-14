package org.cinematics.model;


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
	
	public Integer getShowID() {
		return showID;
	}
	
	public void setBookingId(Integer showID) {
		this.showID = showID;
	}
	
	public int getBookingId() {
		return bookingId;
	}
}
