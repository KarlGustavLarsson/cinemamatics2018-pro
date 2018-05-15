package org.cinematics.model;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.cinematics.db.DBQueryHelper;
import org.cinematics.exceptions.OutOfSeatingBoundsException;

// Describes a show in the theatre, Start, End and Movie

public class Show implements Comparable <Show>{
	
	private Integer id; 
	private LocalDateTime start;
	private LocalDateTime end;
	private Integer movieID;
		// The seating arrangement
	
	public Show() {
		
	}
	
	public Show(LocalDateTime start, LocalDateTime end, Integer movie) {
		this.start = start;
		this.end = end;
		this.movieID = movie;
	}
	
	//Make this object sortable in an arraylist
	@Override
	public int compareTo(Show ob) {
		if(this.start.isEqual(ob.getStart()))
			return 0;
		
		if(this.start.isBefore(ob.getStart()))
			return -1;

		return 1;
	}
	
	public long getDuration() {
		return this.getStart().until(this.getEnd(), ChronoUnit.MINUTES);
	}
	
	public Booking[][] getBookings() {
		Booking[][] bookings = new Booking[Theatre.SEAT_ROWS][Theatre.SEAT_COLS];
		String bookingsQuery = "SELECT * FROM cinema.bookings WHERE show_id = ?";
		ResultSet rs = DBQueryHelper.prepareAndExecuteStatementQuery(bookingsQuery, id).get();
		try {
			Booking booking;
			int row;
			int col;
			while(rs.next()) {
				booking = new Booking();
				row = rs.getInt("seat_row");
				col = rs.getInt("seat_col");
				booking.setShowID(this.id);
				booking.setBookingId(rs.getInt("id"));
				booking.setCustomerID(rs.getInt("customer_id"));
				bookings[row][col] = booking;
			}
		} catch (SQLException e) {
			System.err.println(e);		}
		return bookings;
	}

	public boolean checkOverlap(LocalDateTime startTime, LocalDateTime endTime) {
		return (start.isBefore(endTime) && startTime.isBefore(end));
	}
	
	/**
	 * @return the start
	 */
	public LocalDateTime getStart() {
		return start;
	}
	
	/**
	 * @param start the start to set
	 */
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	
	/**
	 * @return the end
	 */
	public LocalDateTime getEnd() {
		return end;
	}
	
	/**
	 * @param end the end to set
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	
	public Movie getMovie() {
		
		String movieQuery = "SELECT * FROM cinema.movies WHERE id = ?";
		ResultSet result = DBQueryHelper.prepareAndExecuteStatementQuery(movieQuery, movieID).get();
		try {
			Movie movie;
			if(result.next()) {
				movie = new Movie();
				movie.setId(result.getInt("id"));
				movie.setName(result.getString("name"));
				movie.setDescription(result.getString("description"));
				return movie;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return null;
	}
	
	/**
	 * @param movieID the movieID to set
	 */
	public void setMovieID(Integer movieID) {
		this.movieID = movieID;
	}
	

	/**
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * @param show is
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	 @Override
	 public String toString() {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm");
		 String startTime = formatter.format(start);
		 String endTime = formatter.format(end);
		 return "ShowId:" + this.id  + " Namn:" + this.getMovie().getName() + " Start:" + startTime + " Slut:" + endTime; 
	 }
	
	public void showAllSeats() {
		System.out.print(" |");
		for(int i = 0; i < Theatre.SEAT_COLS; i++) {
			System.out.print(i+" ");
		}
		System.out.println("");
		for(int i = 0; i < Theatre.SEAT_COLS; i++) {
			System.out.print("--");
		}
		System.out.println("-");
		Booking[][] bookings = getBookings();
		for(int row = 0; row < bookings.length; row++) {
			System.out.print(row+"|");
			for(int col = 0; col < bookings[row].length; col++) {
				System.out.print((bookings[row][col]!=null)?"X ":"O ");
			}
			System.out.println("");
		}
		System.out.print("*\\");
		for(int i = 1; i < Theatre.SEAT_COLS; i++) {
			System.out.print("__");
		}
		System.out.println("/*");
		System.out.println("");
	}

	
	
	public Seat[] getSeats(int startingRow, int startingCol, int numberOfSeats) {
		Seat[] seats = new Seat[numberOfSeats];
		for(int i = 0; i < numberOfSeats; i++) {
			seats[i] = new Seat(startingRow, startingCol+i);
		}
		return seats;
	}

}
