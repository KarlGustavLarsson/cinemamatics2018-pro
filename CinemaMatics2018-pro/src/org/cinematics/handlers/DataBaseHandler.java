package org.cinematics.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.cinematics.model.Booking;
import org.cinematics.model.Customer;
import org.cinematics.model.Movie;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;

public class DataBaseHandler {
	
	Connection conn;

    public DataBaseHandler(){
        init();
        
    }

    private void init(){
        try {

            Class.forName("org.postgresql.Driver");

        }

        catch (ClassNotFoundException e) {

            System.err.println (e);
            System.exit (-1);

        }
    }
    
    private void open() {
        try {

            // open connection to database

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/cinematics2000", "user", "a");

        } catch (java.sql.SQLException e) {

            System.err.println (e);

            System.exit (-1);

        }
    }

    private void close() {
        try {
            conn.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    
    
    
    public Map<String, Theatre> loadTheatresFromDb() {
    	
    	Map<String, Theatre> theatresToReturn;
    	theatresToReturn = new TreeMap<String, Theatre>();	
    	open();
        try {
            String query =
                    "SELECT * FROM theatre";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            
            
            while ( rs.next () ){
            	
            	Theatre theatreToAdd = new Theatre(rs.getString("name"));
            	theatreToAdd.loadShowFromDb();
            	theatresToReturn.put(theatreToAdd.getName(), theatreToAdd);
            	
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        
        return theatresToReturn; 	
    }
    
    
    
    public Map<Integer, Booking> loadBookingsFromDb(DataManager dmgr) {
    	
    	Map<Integer, Booking> bookingsToReturn;
    	bookingsToReturn = new TreeMap<Integer, Booking>();
    		
    	open();
        try {
            String query =
                    "SELECT * FROM booking";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            
            //Hardcoded customer needs to be fixed.. 
            Customer aCust = new Customer();
            
            aCust.setCustId(1);
            aCust.setAge(25);
            aCust.setName("Kunden");
           
            
            while ( rs.next () ){
            	
            	Booking bookingToAdd = new Booking(rs.getInt("id"));
            	bookingToAdd.setCustomer(aCust);
            	// TODO add show to booking... 
            	Show showToAdd = new Show();
            	
            	bookingToAdd.setShow(loadShowFromDbToBooking(rs.getInt("show_id")));
            	bookingsToReturn.put(bookingToAdd.getBookingId(), bookingToAdd);
            	 
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        
        return bookingsToReturn; 	
    }
    
    
    
    public Show loadShowFromDbToBooking(int id) {
    	List<Show> shows = new ArrayList<>();
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");	
    	open();
        try {
            String query =
                    "SELECT * FROM show WHERE id = " + id + ";";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            
            
            while ( rs.next () ){
            	
            	LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
            	LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
            	
            	Show showToAdd = new Show(rs.getInt("id"), startT, endT, getMovieFromDb(rs.getInt("movie_id")));
            	// add bookings from database...
            	DataManager dmg = new DataManager();
            	
            	shows.add(showToAdd);
            	
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        for (Show cShow : shows) {
        	System.out.println(cShow.getId() + " " + cShow.getMovie().getName());
        }
        return shows.get(0);
    	
    }
    
    
    
    public Booking[][] loadTicketsFromDb(int bookingID, Customer cust) {
		
    	Booking[][] bookings = new Booking[Theatre.SEAT_ROWS][Theatre.SEAT_COLS];
    			
		open();
        try {
            String query =
                    "SELECT * FROM ticket WHERE booking_id = " + bookingID + ";";
            
            // execute query

            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
           
            while ( rs.next () ){
            	System.out.println("row:" + rs.getInt("row") + "col:" + rs.getInt("colum"));
            	Booking newBooking = new Booking(bookingID);
            	newBooking.setCustomer(cust);
            	bookings[rs.getInt("row")][rs.getInt("colum")] = newBooking;
            }
            

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        
        
        return bookings;
	}
    
    
    
    
    
    public int getTheatreId(String theatreName) {
    	
    	int idToReturn = -1;
    	open();
        try {
            String query =
                    "SELECT * FROM theatre WHERE name = '" + theatreName + "';";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            rs.next();
            idToReturn = rs.getInt("id");
           

        } 
        catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }

    	return idToReturn;
    }
    
    public List<Show> loadShowFromDb(int id) {
    	List<Show> shows = new ArrayList<>();
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");	
    	open();
        try {
            String query =
                    "SELECT * FROM show WHERE theatre_id = " + id + ";";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            
            
            while ( rs.next () ){
            	
            	LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
            	LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
            	
            	Show showToAdd = new Show(rs.getInt("id"), startT, endT, getMovieFromDb(rs.getInt("movie_id")));
            	// add bookings from database...
            	
            	shows.add(showToAdd);
            	
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        for (Show cShow : shows) {
        	System.out.println(cShow.getId() + " " + cShow.getMovie().getName());
        }
        return shows;
    	
    }
    
    public List<Show> loadShowsFromDb(DataManager dmgr) {
    	
    	List<Show> shows = new ArrayList<>();
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");	
    	open();
        try {
            String query =
                    "SELECT * FROM show";
            
            // execute query
            
            
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            
            while ( rs.next () ){
            	
            	LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
            	LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
            	Show showToAdd = new Show(rs.getInt("id"), startT, endT, getMovieFromDb(rs.getInt("movie_id")));
            	shows.add(showToAdd);
            
            	String query2 =
                        "SELECT * FROM booking INNER JOIN ticket On id = booking_id WHERE show_id=" + showToAdd.getId();
            	
            	Statement statement2 = conn.createStatement ();
            	ResultSet rs2 = statement2.executeQuery (query2);
            		while (rs2.next()) {
            			Booking newBooking = dmgr.getBooking(rs2.getInt("id"));
                    	dmgr.saveBooking(newBooking, rs.getInt("row"), rs.getInt("colum"), showToAdd.getId(), dmgr.getTheatreForShow(showToAdd.getId()).getName());	
            		}
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        
        return shows;
    	
    }
    
    public Movie getMovieFromDb(int movieId) {
    	Movie retMovie = new Movie();
    	
    	open();
        try {
            String query =
                    "SELECT * FROM movie WHERE id = " + movieId;
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            rs.next();
            
            retMovie.setMovieIdCounter(rs.getInt("id"));
            retMovie.setName(rs.getString("name"));
            retMovie.setDescription(rs.getString("decription"));
            
            
           

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        return retMovie;
    }
    public List<Movie> getAllMoviesFromDb(){
    	List<Movie> movies = new ArrayList<>();

    	open();
        try {
            String query =
                    "SELECT * FROM movie";
            
            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);
            
            
            while ( rs.next () ){
            	
            	
            	Movie movieToAdd = new Movie();
            	movieToAdd.setId(rs.getInt("id"));
            	movieToAdd.setName(rs.getString("name"));
            	movieToAdd.setDescription(rs.getString("decription"));
            			
            	movies.add(movieToAdd);
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        return movies;
    	
    }
    
    
    public void addCustomer(int id, String n){
        open();
        try {
            String query =
                    "INSERT INTO customer (id, name) values (" + id + ", '" + n + "')";
            System.out.println(query);
                            

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
    }

    public Customer getCustomer(int custId) {
    	open();
    	Customer retCust = new Customer();
        try {
            String query = "SELECT * FROM customer WHERE id = " + custId + ";";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);

            // return query result
            
            
            while ( rs.next () ){
            	
            	retCust.setCustId(rs.getInt("id"));
            	retCust.setName(rs.getString("name"));
            }
            

        } catch(SQLException e){
            System.out.println("Query failed");
        } finally {
            close();
        }
        return retCust;
    }
   

}
