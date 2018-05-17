package org.cinematics.handlers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.cinematics.model.Booking;
import org.cinematics.model.Customer;
import org.cinematics.model.Movie;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;
import org.cinematics.model.Ticket;
/**
 * This class should be used to store all data that the cinema program needs
 * 
 *
 */
//TEST
public class DataManager {
	
	DataBaseHandler myDbh = new DataBaseHandler();
	
	public boolean checkIfShowOverlaps(Show show) {
		return myDbh.checkIfShowOverlapsDb(show);
	}
	
	public DataManager() {
		
	}
	
	public Theatre getTheatre(String name) {
		return myDbh.getTheatreFromDb(name);
	}
	
	public ArrayList<Movie> getAllMovies(){
		return myDbh.getAllMoviesFromDb();
	}
	
	public boolean addMovie(Movie movie) {
		return myDbh.saveMovieToDb(movie);
	}
	
	public List<Theatre> getTheatres(){
		return myDbh.getAllTheatresFromDb();
	}
	
	public boolean addTheatre(Theatre theatre) {
		return myDbh.saveTheatreToDb(theatre);
	}
	
	public boolean addShow(Show show) {
		return myDbh.saveShowToDb(show);
	}
	
	public Show getShow(int showId) {
		return myDbh.getShowFromDb(showId);
	}
	public int saveBooking(Booking booking) {
		return myDbh.saveBooking(booking);
	}
	public boolean saveTicket(Show show, Booking booking, int row, int colum) {
		return myDbh.saveTicket(show, booking, row, colum);
	}
	public ArrayList<Ticket> getAllTicketsInShow(int showId){
		return myDbh.getAllTicketInShowFromDb(showId);
	}
	

	public Booking getBooking(Integer bookingId) {
		return bookings.get(bookingId);
	}

	public ArrayList<Theatre> getAllTheatres() {	
		return myDbh.getAllTheatresFromDb();
	}

	public ArrayList<Show> getShowInTheatre(int id) {	
		return myDbh.getShowInTheatre(id);
	}

	public boolean areSeatsAvailable(Show selectedShow, int numberOfSeats, int startingRow, int startingCol) {
		ArrayList<Ticket> tickets = myDbh.getAllTicketInShowFromDb(selectedShow.getId());
		
		for (Ticket cTick : tickets) {
			if (cTick.getRow() == startingRow) {
				//on the same row
				//
				if (startingCol == cTick.getColum()) {
					return false;
				}
				for (int col = startingCol; col < (startingCol + (numberOfSeats+1)); col++) {
					if (cTick.getColum()==col) {
						return false;
					}
				}	
			}	
		}	
		return true;
	}
	public Movie getMovie(int movieId) {
		return myDbh.getMovie(movieId);
	}

}