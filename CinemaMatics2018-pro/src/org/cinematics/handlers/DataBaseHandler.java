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
import org.cinematics.model.Ticket;

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

    public boolean saveMovieToDb(Movie cMovie) {
    	
    	open();
        try {
            String query =
            		"INSERT INTO movie (name, description)\r\n" + 
            		"VALUES ('" + cMovie.getName() + "', '" + cMovie.getDescription() + "');";
                    
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
      //TODO fix check
        return true;
    }
    
    public boolean saveTheatreToDb(Theatre cTheatre) {
    	open();
        try {
            String query =
            		"INSERT INTO theatre (name)\r\n" + 
            		"VALUES ('" + cTheatre.getName() + "');";
                    
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
        } 
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        //TODO fix check
        return true;
    }
    
    public Theatre getTheatreFromDb(String name) {
    	Theatre theatreToFetch = new Theatre();
    	open();
        try {
            String query =
            		"SELECT * FROM theatre WHERE name='" + name + "';";
           
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            rs.next();       
            theatreToFetch.setId(rs.getInt("id"));
            theatreToFetch.setName(rs.getString("name"));  
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
    	return theatreToFetch;
    }
    
    public ArrayList<Theatre> getAllTheatresFromDb(){

    	ArrayList<Theatre> theatres = new ArrayList<>();
    	
    	open();
        try {
            String query =
            		"SELECT * FROM theatre;";
                    
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            while (rs.next()) {
            	Theatre theatreToAdd = new Theatre(); 
            	theatreToAdd.setId(rs.getInt("id"));
            	theatreToAdd.setName(rs.getString("name"));
            	theatres.add(theatreToAdd);
            }
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
    	
    	return theatres;
    	
    }
    public ArrayList<Movie> getAllMoviesFromDb(){
    	ArrayList<Movie> movies = new ArrayList<>();
    	
    	open();
        try {
            String query =
            		"SELECT * FROM movie;";
                    
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            while (rs.next()) {
            	Movie movieToAdd = new Movie(); 
            	movieToAdd.setId(rs.getInt("id"));
            	movieToAdd.setName(rs.getString("name"));
            	movieToAdd.setDescription(rs.getString("description"));
            	movies.add(movieToAdd);
            	
            }
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
    	
    	return movies;
    	
    }

	
    
    
    
    
    public boolean saveShowToDb(Show show) {
		//TODO remember to check if possible to save...   
    	open();
        try {
        	
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
//            LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
//            LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
//            
            String query =
            		"INSERT INTO show (movie_id, theatre_id, starttime, endtime)\r\n" + 
            		"VALUES (" + show.getMovieId() + ", " + show.getTheatreId() + ", '" + show.getStart().format(formatter) + "', '" + show.getEnd().format(formatter) + "');";
            System.out.println(query);        
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
      //TODO fix check
        return true;
    }

	public ArrayList<Show> getShowInTheatre(int id) {
		
		ArrayList<Show> shows = new ArrayList<>();
		
		open();
        try {
        	
            String query =
            		"SELECT * FROM show where theatre_id = " + id + ";"; 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
            
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            while (rs.next()) {
            	Show showToAdd = new Show();
            	showToAdd.setId(rs.getInt("id"));
            	showToAdd.setMovieId(rs.getInt("movie_id"));
            	showToAdd.setTheatreId(rs.getInt("theatre_id"));
            	
            	LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
            	LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
            	
            	showToAdd.setStart(startT);
            	showToAdd.setEnd(endT);
            	shows.add(showToAdd);
            }
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        return shows;
        
	}

	public Show getShowFromDb(int showId) {
		
		Show showToReturn = new Show();
		
		open();
        try {
        	
            String query =
            		"SELECT * FROM show where id = " + showId + ";"; 
            
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
            
            rs.next();
            
            showToReturn.setId(rs.getInt("id"));
            showToReturn.setMovieId(rs.getInt("movie_id"));
            showToReturn.setTheatreId(rs.getInt("theatre_id"));
            LocalDateTime startT = LocalDateTime.parse((rs.getString("starttime")), formatter);
        	LocalDateTime endT = LocalDateTime.parse((rs.getString("endtime")), formatter);
            showToReturn.setStart(startT);
            showToReturn.setEnd(endT);
        }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        return showToReturn;
	}

	public ArrayList<Ticket> getAllTicketInShowFromDb(int showId) {
		
		ArrayList<Ticket> tickets = new ArrayList<>();
		
		open();
        try {
            String query =		
            		"SELECT * FROM booking \r\n" + 
            		"INNER JOIN ticket ON booking.id = ticket.booking_id where booking.show_id=" + showId + ";"; 
          
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            while (rs.next()) {
            	Ticket ticketToAdd = new Ticket();
            	ticketToAdd.setId(rs.getInt("id"));
            	ticketToAdd.setBookingId(rs.getInt("booking_id"));
            	ticketToAdd.setRow(rs.getInt("row"));
            	ticketToAdd.setColum(rs.getInt("colum"));
            	tickets.add(ticketToAdd);
            }
       }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        
        return tickets;
	}

	public boolean saveTicket(Show show, Booking booking, int row, int colum) {
		open();
        try {
            String query =		
            		"INSERT INTO ticket (booking_id, row, colum)\r\n" + 
            		"VALUES (" + booking.getBookingId() + ", " + row + ", " + colum + ");";
            		
            System.out.println(query);

            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
                   }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        //TODO fix check... 
        return true;
		
	}

	public int saveBooking(Booking booking) {
		// TODO Auto-generated method stub
		//save booking to db
		int newBookingnr=-1;
		open();
        try {
            String query =		
            		"INSERT INTO booking (show_id, customer_id)\r\n" + 
            		"VALUES (" + booking.getShowId() + ", " + booking.getCustomerId() + ";";
            		
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
       }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
        
        open();
        try {
            String query =		
            		"SELECT MAX (id) FROM booking";
            		
            Statement statement = conn.createStatement ();
            ResultSet rs = statement.executeQuery (query);
            
            rs.next();
            newBookingnr = rs.getInt("id");
       }  
       catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        finally {
            close();
        }
		
		return newBookingnr;
	}
	
		
	
}
