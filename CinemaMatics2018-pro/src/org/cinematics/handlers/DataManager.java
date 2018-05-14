package org.cinematics.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.cinematics.db.DBQueryHelper;
import org.cinematics.model.Booking;
import org.cinematics.model.Movie;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;
/**
* This class should be used to store all data that the cinema program needs
*
*
*/
public class DataManager {
    
    //private Map<String, Theatre> theatres;
    private Map<Integer, Booking> bookings;
    //private Set<Movie> movies;
    
    public DataManager() {
        //theatres = new TreeMap<String, Theatre>();
        bookings = new TreeMap<Integer, Booking>();
        //movies = new TreeSet<Movie>(Comparator.comparing(Movie::getName));
    }
    
    // Place to improve database connectivity
    // Get specific row from entity;
    public Theatre getTheatre(String name) {
    	
    	List<Theatre> theatres = getTheatres();
    	for(Theatre t : theatres) {
    		if(t.getName().equals(name))
    			return t;
    	}
    	return null;
    }
    
    
    public Set<Movie> getAllMovies(){
    	
    	Set<Movie> movies = new TreeSet<Movie>(Comparator.comparing(Movie::getName)); 
    	
        String queryMovies = "SELECT * FROM cinema.movies;";
        ResultSet result = DBQueryHelper.prepareAndExecuteStatementQuery(queryMovies).get();
        try {
        	Movie movie;
            while(result.next()) {
                movie = new Movie();
                movie.setId(result.getInt("id"));
                movie.setName(result.getString("name"));
                movie.setDescription(result.getString("description"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        
        return movies;
    }
    
    public boolean addMovie(Movie movie) {
        String queryMovies = "SELECT * FROM cinema.movies WHERE name = ?;";
        if(doesEntryExist(queryMovies, movie.getName())) {
            return false;
        }
        String insertMovieString = "INSERT INTO cinema.movies (name, description) VALUES (?,?);";
        long result = DBQueryHelper.prepareAndExecuteStatementUpdate(insertMovieString, movie.getName(), movie.getDescription());
        return result > 0;
    }
    
    
    public List<Theatre> getTheatres(){
    	
    	List<Theatre> t = new ArrayList<>();
    	
        String getAllTheatres = "SELECT * FROM cinema.theatres;";
        ResultSet result = DBQueryHelper.prepareAndExecuteStatementQuery(getAllTheatres).get();
        try {
        	Theatre theatre;
            while(result.next()) {
                theatre = new Theatre(result.getString("name"));
                theatre.setId(result.getInt("id")); // Unique DBid created by DBMS
                t.add(theatre);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    	
        return t;
    }
    
    public boolean addTheatre(Theatre theatre) {
        String queryTheatres = "SELECT * FROM cinema.theatres WHERE name = ?;";
        if(doesEntryExist(queryTheatres, theatre.getName())) {
            return false;
        }
        String insertTheatreString = "INSERT INTO cinema.theatres (name, seat_rows, seat_cols) VALUES (?,?,?);";
        long result = DBQueryHelper.prepareAndExecuteStatementUpdate(insertTheatreString, theatre.getName(), Theatre.SEAT_ROWS, Theatre.SEAT_COLS);
        return result > 0;
    }
    
    //TODO DB, DeGlobalize Theatres
    public boolean addShowToTheatre(Show show, String theatreName) {
    	// Map<String, Theatre> theatres = new TreeMap<String, Theatre>();
    	
    	// Theatre t = getTheatre(theatreName); 
    	
    	// show.checkOverlap
    	// TIMESTAMP [ WITHOUT TIMEZONE ]	LocalDateTime
    	// Get all SQL start/end dates
    	// Check overlap
    	// add show
    	
    	
    	// theatre_name -> theatre_id
    	// SELECT * FROM cinema.shows INNER JOIN cinema.theatres ON theatres.id=shows.theatre_id WHERE name = 'Salong 1';
    	
        //String getAllTheatres = "SELECT * FROM cinema.shows INNER JOIN cinema.theatres on theatre_id=id where theatre=?;";
    	
    	String getAllTheatres = "SELECT * FROM cinema.shows INNER JOIN cinema.theatres ON theatres.id=shows.theatre_id WHERE name = ?;";
    	
        ResultSet result = DBQueryHelper.prepareAndExecuteStatementQuery(getAllTheatres,theatreName).get();
        try {
        	
        	LocalDateTime rsStartT;
        	LocalDateTime rsEndT;
        	
            while(result.next()) {
                
                rsStartT = result.getTimestamp("starttime").toLocalDateTime();
                rsEndT = result.getTimestamp("endtime").toLocalDateTime();
                
                
                if( show.checkOverlap(rsStartT, rsEndT) ) {
                	
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm");

                	System.out.println("Show overlaps " + fmt.format(rsStartT) + " to " + fmt.format(rsEndT));
                	return false; // Overlaps
                }
                
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        int theatreID = getTheatre(theatreName).getId();
        String showInsert = "INSERT INTO cinema.shows (starttime, endtime, movie_id, theatre_id) VALUES (?,?,?,?);";

        long updRes = DBQueryHelper.prepareAndExecuteStatementUpdate(showInsert, 
        		show.getStart(),
        		show.getEnd(),
        		show.getMovie().getId(),
        		theatreID);
        
        return updRes > 0;
    }
    
    
    //TODO DB, DeGlobalize Theatres
    public boolean saveBooking(Booking booking, Integer row, Integer col, Integer showId, String theatreName) {
    	Map<String, Theatre> theatres = new TreeMap<String, Theatre>();
    	
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
    
    //TODO DB    
    public Booking getBooking(Integer bookingId) {
        
        return bookings.get(bookingId);
    }
    
    // Check if ROW exists in ENTITY
    private boolean doesEntryExist(String query, Object... values) {
        ResultSet resultSet = DBQueryHelper.prepareAndExecuteStatementQuery(query, values).get();
        try {
            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
        return false;
    }
}