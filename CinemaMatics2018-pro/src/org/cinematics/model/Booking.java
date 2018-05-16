package org.cinematics.model;

public class Booking {
	
	
	private int bookingId;
	private int showId;
	private int customerId;
	
	public Booking() {
	}
	public Booking(int id) {
		bookingId = id;
	}
		
	public void setCustomerId(int custId) {
		this.customerId = custId;
	}
	public int getCustomerId() {
		return customerId;
	}
	
	public void setShowId(int showId) {
		this.showId = showId;
	}
	public int getShowId() {
		return this.showId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public int getBookingId() {
		return this.bookingId;
	}
}
