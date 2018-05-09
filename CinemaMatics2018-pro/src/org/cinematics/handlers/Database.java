package org.cinematics.handlers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;


class Database {

    Connection conn;

    public Database(){
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


    private void addEX1NewOwner(int owner, int pet){
        open();
        try {
            String query =
                    "INSERT INTO pet (id, name, age, owner_id) " +
                            "SELECT " + pet+10 + ", name, age, " + owner + " FROM ex1.pet " +
                            " WHERE id=" + pet;

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);



            // return query result

            while ( rs.next () ){
                System.out.println(
                        "Petname: " + rs.getString("name") +
                                "\nAge: " + rs.getInt("age") + "\n");
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
    }

    private void getEX1Pets(){
        open();
        try {
            String query = "SELECT * FROM ex1.pet";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);

            // return query result

            while ( rs.next () ){
                System.out.println(
                        rs.getInt("id") + ". " + rs.getString("name"));
            }

        } catch(SQLException e){
            System.out.println("Query failed");
        } finally {
            close();
        }
    }
    private void getEX1Owners(){
        open();
        try {
            String query = "SELECT * FROM ex1.owner";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);

            // return query result
            while ( rs.next () ){
                System.out.println(
                        rs.getInt("id") + ". " + rs.getString("name"));
            }

        } catch(SQLException e){
            System.out.println("Query failed");
        } finally {
            close();
        }
    }
    public void insertOwner() {
	   open();
       try {
           String query =
        		   "INSERT INTO customer (id, name)" + 
        		   " VALUES (1, 'Kunden')";
            System.out.println(query);
            
        
           
            

           
           
           // execute query

           Statement statement = conn.createStatement ();
           ResultSet rs = statement.executeQuery (query);
           }
       catch(SQLException e){
           System.out.println("Query failed " + e.getMessage());
       } finally {
           close();
       }
   }
    
    private void getEX1PetsWithOwner(){
        open();
        try {
            String query =
                    "SELECT o.name as owner, p.name as pet, p.age as age" +
                    " FROM ex1.pet p " +
                    " INNER JOIN ex1.owner o ON o.id=p.owner_id";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);



            // return query result

            while ( rs.next () ){
                System.out.println(
                        "Owner: " + rs.getString("owner") +
                        "\nPetname: " + rs.getString("pet") +
                        "\nAge: " + rs.getInt("age") + "\n");
            }

        } catch(SQLException e){
            System.out.println("Query failed" + e.getMessage());
        } finally {
            close();
        }
    }


    private void addEX2NewOwner(int owner, int pet){
        open();
        try {
            String query = "INSERT INTO ex2.owner_pet (owner_id, pet_id) values ("+ owner + ", " + pet + ")";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);



            // return query result

            while ( rs.next () ){
                System.out.println(
                        "Petname: " + rs.getString("name") +
                                "\nAge: " + rs.getInt("age") + "\n");
            }

        } catch(SQLException e){
            System.out.println(e.getMessage());
        } finally {
            close();
        }
    }

    private void getEX2Pets(){
        open();
        try {
            String query = "SELECT * FROM ex2.pet";

            // execute query

            Statement statement = conn.createStatement ();

            ResultSet rs = statement.executeQuery (query);



            // return query result

            while ( rs.next () ){
                System.out.println(
                        rs.getInt("id") + ". " + rs.getString("name"));
            }

        } catch(SQLException e){
            System.out.println("Query failed:" + e.getMessage());
            
        } finally {
            close();
        }
    }

    public static void main (String args[]) {
        Database db = new Database();
        //db.getEX1PetsWithOwner();
        //System.out.println();
        //db.getEX1PetsWithOwner();
        //db.getEX1Owners();
        //System.out.println("EX1");
        //db.givePetNewOwner(db);
        //db.getEX1Pets();
        //System.out.println("EX2");
        //db.getEX2Pets();
        //db.insertOwner();
    }

    private static void givePetNewOwner(Database db){
        Scanner scan = new Scanner(System.in);
        db.getEX1Owners();
        System.out.println("Owner id:");
        int owner = scan.nextInt();
        db.getEX1Pets();
        System.out.println("Pet id:");
        int pet = scan.nextInt();
        db.addEX1NewOwner(owner, pet);
        db.addEX2NewOwner(owner, pet);

    }

}
