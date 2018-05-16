package org.cinematics.model;

public class Ticket {
	private int id;
	private int bookingId;
	private int row;
	private int colum;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBookingId() {
		return bookingId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColum() {
		return colum;
	}
	public void setColum(int colum) {
		this.colum = colum;
	}
}
