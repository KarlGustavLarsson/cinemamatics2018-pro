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

    
    public boolean saveMovieToDb(Movie cMovie) {
    	
    	//check if movie exists?
    	
    	//Insert movie 
    	
    	// return boolean if success.  
    	
    	
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
    
    
    
    
   

}
