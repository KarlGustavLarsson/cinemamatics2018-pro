package org.cinematics.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    
    public boolean addShowToTheatre(Show show, String theatreName) {
    	
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
    
    //This method must be used together with endBooking, so that the DB transaction is ended properly
    public boolean startBooking(Booking booking, Integer row, Integer col, Integer showId, String theatreName) {
    	String checkIfSeatTakenQuery = "SELECT seat_row, seat_col FROM cinema.bookings"
    			+ " WHERE show_id = ?;";
    	Optional<ResultSet> rso = DBQueryHelper.prepareAndExecuteStatementQuery(
    			checkIfSeatTakenQuery, showId);
    	if(!rso.isPresent()) {
    		return false;
    	}
    	ResultSet rs = rso.get();
    	int bookedCol;
		int bookedRow;
    	try {
	    	while(rs.next()) {
	    		bookedRow = rs.getInt("seat_row");
	    		bookedCol = rs.getInt("seat_col");
	    		
    			if(row == bookedRow && col == bookedCol) {
					return false;
				}		
	    	}
		} catch (SQLException e) {
			System.err.println(e);
		}
    	String insertBookingQuery = "INSERT INTO cinema.bookings "
    			+ "(show_id, seat_row, seat_col, customer_id) VALUES (?,?,?,?);";
    	
		long result = DBQueryHelper.startTransactionUpdate(
    			insertBookingQuery, showId, row, col, booking.getCustomerID());
		return result > 0;
    }
    
    //This method must be used after the method startBooking has been called
    public boolean endBooking(boolean doRollback) {
    	return DBQueryHelper.endTransactionUpdate(doRollback);
    }
    
    public Theatre getTheatreForShow(Integer showId) {
    	String getTheatreForShowQuery = "SELECT * FROM cinema.theatres INNER JOIN cinema.shows ON"
    			+ " (theatres.id = shows.theatre_id) WHERE shows.id=?;";
    	Optional<ResultSet> rso = DBQueryHelper.prepareAndExecuteStatementQuery(getTheatreForShowQuery, showId);
    	if(!rso.isPresent()) {
    		return null;
    	}
    	ResultSet rs = rso.get();
    	try {
			if(rs.next()) {
				String name = rs.getString("name");
				Theatre theatre = new Theatre(name);
				theatre.setId(rs.getInt("id"));
				return theatre;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
        return null;
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
    
    public int createCustomer(String name) {
    	String insertBookingQuery = "INSERT INTO cinema.customers (name) VALUES (?);";
    	ResultSet rsWithKeys = DBQueryHelper.prepareAndExecuteStatementUpdateReturnKeys(insertBookingQuery, name);
    	try {
    		if(rsWithKeys.next())
    			return rsWithKeys.getInt(1);
		} catch (SQLException e) {
			System.err.println(e);
		}
    	return -1;
    }
    
    public static Show getShowFromID(Integer showID) {
		String showQuery = "SELECT * FROM cinema.shows WHERE id = ?";
		Optional<ResultSet> rso = DBQueryHelper.prepareAndExecuteStatementQuery(showQuery, showID);
		if(!rso.isPresent()) {
			return null;
		}
		try {
			ResultSet rs = rso.get();
			if(rs.next()) {
				Show show = new Show();
				show.setId(rs.getInt("id"));
				show.setMovieID(rs.getInt("movie_id"));
				show.setStart(rs.getTimestamp("starttime").toLocalDateTime());
				show.setEnd(rs.getTimestamp("endtime").toLocalDateTime());
				return show;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return null;
	}
   

}