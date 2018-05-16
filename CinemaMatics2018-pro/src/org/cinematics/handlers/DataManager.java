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
import org.cinematics.model.Movie;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;
/**
 * This class should be used to store all data that the cinema program needs
 * 
 *
 */
//TEST
public class DataManager {
	
	DataBaseHandler myDbh = new DataBaseHandler();
	
	
	public DataManager() {
		
	}
	
	public Theatre getTheatre(String name) {
		
		return myDbh.getTheatreFromDb(name);
	
	}
	
	public ArrayList<Movie> getAllMovies(){
		return myDbh.getAllMoviesFromDb();
	}
	
	public boolean addMovie(Movie movie) {
		//TODO add to db
		
		
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

	public boolean saveBooking(Booking booking, Integer row, Integer col, Integer showId, String theatreName) {
		if(theatres.containsKey(theatreName)) {
			Theatre theatre = theatres.get(theatreName);
			Show show = theatre.getShow(showId);
			show.getBookings()[row][col] = booking;
			bookings.put(booking.getBookingId(), booking);
			return true;
		}
		return false;
	}
	
	public Theatre getTheatreForShow(Integer showId) {
		for(Theatre theatre : getTheatres()) {
			for(Show show : theatre.getAllShows()) {
				if(show.getId() == showId) {
					return theatre;
				}
			}
		}
		return null;
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

}