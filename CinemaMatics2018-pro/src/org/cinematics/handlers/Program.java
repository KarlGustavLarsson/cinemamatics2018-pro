package org.cinematics.handlers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;

import org.cinematics.db.DBUtils;
import org.cinematics.model.Booking;
import org.cinematics.model.Movie;
import org.cinematics.model.Seat;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;

public class Program {

	public static void main(String[] args) {
		boolean done = false;
		DataManager dataManager = new DataManager();
		UserInterface ui = new UserInterface();
		
		try {
			InputStream in = Files.newInputStream(Paths.get("logging.properties"));
			LogManager.getLogManager().readConfiguration(in);
		} catch (SecurityException | IOException e) {
			System.err.println(e);
		}
		
		// Historically used to setup some data to test with
		// setup(dataManager);
		
		while(!done) {
			
			ui.show_menu();
			Integer choice = UserInterface.inputInt();
			
			switch(choice) {
			case 1:
				viewAllShows(dataManager);	
				break;
			case 2:
				viewAllShowInTheatre(dataManager);
				break;
			case 3:
				createShow(dataManager);
				break;
			case 4:
				addTheatre(dataManager);
				break;
			case 5:
				addMovie(dataManager); 
				break;
			case 6:
				makeBooking(dataManager, choice); 
				break;
			case 7:
				done = true;
				break;
			default:
				System.out.println("That is not a valid menu option");
			}
			DBUtils.closeConnection();
		}
	}
	
	/* No longer needing initializing values to test with
	public static void setup(DataManager dataManager) {
		Movie m1 = new Movie();
		m1.setName("Terminator");
		m1.setDescription("I'll be back");
		
		Movie m2 = new Movie();
		m2.setName("Scarface");
		m2.setDescription("Say hello to my....");
		
		dataManager.addMovie(m1);
		dataManager.addMovie(m2);
		dataManager.addTheatre(new Theatre("Salong1"));
		dataManager.addTheatre(new Theatre("Salong2"));
		dataManager.addTheatre(new Theatre("Salong3"));
		dataManager.addTheatre(new Theatre("Salong4"));

		Show show = new Show(LocalDateTime.now(), LocalDateTime.now(), m1.getId());
		dataManager.addShowToTheatre(show, "Salong1");
	}
	*/

	public static void addMovie(DataManager dataManager) {
		Movie movie = new Movie();
		System.out.println("Add movie title(leave blank to exit):");
		String title = UserInterface.getUserInputString();
		if(title.equals("")) {
			return;
		}
		System.out.println("Add movie description(leave blank to exit):");
		String description = UserInterface.getUserInputString();
		if(description.equals("")) {
			return;
		}
		movie.setDescription(description);
		movie.setName(title);
		if( dataManager.addMovie(movie) )
			System.out.println("The movie was added successfully.");
		else
			System.out.println("The movie insertion failed.");
	}
	
	public static void addTheatre(DataManager dataManager) {
		
		System.out.println("Add theatre name(leave blank to exit):");
		String name = UserInterface.getUserInputString();
		if(name.equals("")) {
			return;
		}
		if(dataManager.addTheatre(new Theatre(name))){
			System.out.println("The new theatre was added successfully");
			return;
		} else {
			System.out.println("That theatre name was already in use.");
			return;
		}
	}
	
	public static void makeBooking(DataManager dataManager, Integer choice) {
		boolean doneWithBooking = false; 
		while(!doneWithBooking) {
			
			// Userhandling may be added later on
			int customerID = dataManager.createCustomer(""); //customer id added to separate bookings
			
			//Choose show
			System.out.println("Choice "+choice+" please");
			for (Theatre cT : dataManager.getTheatres()) {
				
				for(Show show : cT.getAllShows()) {
					System.out.println(show.toString());
					show.showAllSeats();
				}
			}
			int showId = UserInterface.getShowId();
			if(showId == Integer.MIN_VALUE) return;
			Theatre theatre = dataManager.getTheatreForShow(showId);
			if(theatre == null) {
				System.out.println("No such show id");
				break;
			}
			Show show = theatre.getShow(showId);

			int numberOfSeats = UserInterface.chooseNumberOfSeats();
			if(numberOfSeats == Integer.MIN_VALUE) return;
			
			//Booking seats together
			String answer = UserInterface.checkIfSeatsShouldBeTogether();
			boolean seatsTogether = false;
			if(answer.equals("y")) {
				seatsTogether = true;
			} else if(answer.equals("n")) {
				seatsTogether = false;
			} else {
				System.out.println("You have to input y or n!");
				return;
			}
			
			// Seats booked together handled differently from individually picked seat booking
			if(seatsTogether) {
				System.out.println("Choose starting seat:");
				int startingRow = UserInterface.chooseSeatRow();
				if(startingRow == Integer.MIN_VALUE) return;//Exit booking 

				int startingCol = UserInterface.chooseSeatCol();
				if(startingCol == Integer.MIN_VALUE) return; //Exit booking
				
				Seat[] seats = show.getSeats(startingRow, startingCol, numberOfSeats);

				Booking booking = new Booking();
				booking.setShowID(show.getId());
				booking.setCustomerID(customerID);
				boolean bookingInserted = true;
				for(Seat currentSeat : seats) {
					// Will not insert into db if the seat is taken
					bookingInserted &= dataManager.startBooking(booking, currentSeat.row, currentSeat.col, show.getId(), theatre.getName());
				}
				if(!bookingInserted) { 
					System.out.println("Those seats are not available");
					break;
				}
				System.out.println("Booking succeeded");
				show.showAllSeats();
				showTickets(customerID, showId, Arrays.asList(seats));

			// Seats booked together handled differently from individually picked seat booking	
			} else { // Pick seats
				List<Seat> seats = new ArrayList<Seat>();
				Booking booking = new Booking();
				booking.setCustomerID(customerID);
				booking.setShowID(show.getId());
				while(!(seats.size() == numberOfSeats)) {
					int startingRow = UserInterface.chooseSeatRow();
					if(startingRow == Integer.MIN_VALUE) { //Exit booking
						dataManager.endBooking(true);
						return;
					}	
					int startingCol = UserInterface.chooseSeatCol();
					if(startingCol == Integer.MIN_VALUE) { //Exit booking
						dataManager.endBooking(true);
						return;
					}

					Seat seat = new Seat(startingRow, startingCol);
					if(seats.contains(seat)) {
						System.out.println("You have already selected that seat");
						continue;
					}
					boolean theSeatWasFree=dataManager.startBooking(
							booking, startingRow, startingCol, show.getId(), theatre.getName());
					if(!theSeatWasFree) {
						System.out.println("That seat is not available");
						continue;
					} else {
						seats.add(seat);
					}

				} //While
			
				System.out.println("Booking succeeded");
				show.showAllSeats();
				showTickets(customerID, showId, seats);
			}//Else
			
			dataManager.endBooking(false);
			doneWithBooking = true;
		}//While
	}
	
	public static void showTickets(Integer customerID, Integer showID, List<Seat> bookedSeats) {
		
		System.out.println("----Ticket----");
		System.out.println("Customer id: "+customerID);
		System.out.println("Show id: "+showID);
		System.out.print("Seats: ");
		bookedSeats.forEach(seat -> {
			System.out.print("(row: "+seat.row+", column: "+seat.col+")"+ " ");
		});
		System.out.println("");
		System.out.println("--------------");
	}
	
	public static void createShow(DataManager dataManager) {
		Show show = new Show();
		System.out.println("MOVIES: ");
		Set<Movie> movies = dataManager.getAllMovies();
		for(Movie movie : movies) {
			System.out.println(movie.toString());
		}
		int movieId = UserInterface.enterMovieId();
		if(movieId == Integer.MIN_VALUE) return;
		for(Movie movie : movies) {
			if(movie.getId() == movieId) {
				show.setMovieID(movie.getId());
				break;
			}
		}
		if(show.getMovie() == null) {
			System.out.println("That movie id does not exist");
			return;
		}
		System.out.println("Theatres: ");
		
		List<Theatre> theatres = dataManager.getTheatres();
		
		for (Theatre cT : theatres) {
			System.out.println(cT.getName());
		}
		String chosenTheatre = UserInterface.getTheatreName();
		Theatre theatre = dataManager.getTheatre(chosenTheatre);
		if(theatre == null) {
			System.out.println("That theatre does not exist.");
			return;
		}
		System.out.println("Leave blank to exit");
		
		LocalDateTime startTime = UserInterface.readDate(null);
		if(startTime == null || startTime.equals(LocalDateTime.MIN) ) {
			return;
		}
		LocalDateTime endTime = UserInterface.readDate(startTime);
		if(endTime == null || endTime.equals(LocalDateTime.MIN)) {
			return;
		}
		if(!startTime.isBefore(endTime)) {
			System.out.println("Start time must be strictly before end time");
			return;
		}
		List<Show> shows = theatre.getAllShows();
		List<Show> overlappingShows = new ArrayList<Show>();
		for(Show currentShow : shows) {
			if(currentShow.checkOverlap(startTime, endTime)) {
				overlappingShows.add(currentShow);
			}
		}
		if(!overlappingShows.isEmpty()) {
			System.out.println("Show is overlapping with :");
			overlappingShows.forEach(System.out::println);
			return;
		}

		show.setStart(startTime);
		show.setEnd(endTime);
		
		if( !dataManager.addShowToTheatre(show, chosenTheatre))
			System.out.println("Could not add show to DB.");
	}

	public static void viewAllShowInTheatre(DataManager dataManager) {
		String theatreName = UserInterface.getTheatreName();
		Theatre cT = dataManager.getTheatre(theatreName);
		
		if(cT == null) {
			System.out.println("That theatre does not exist.");
			return;
		}
		if(cT.getAllShows().isEmpty()) {
			System.out.println("There are no shows");
			return;
		}
		for(Show show : cT.getAllShows()) {
			System.out.println(show.toString());
		}
		
	}

	public static void viewAllShows(DataManager dataManager) {
		System.out.println("All shows:");
		for (Theatre cT : dataManager.getTheatres()) {
			System.out.println("-"+cT.toString());
			for(Show show : cT.getAllShows()) {
				System.out.println("---"+show.toString());
			}
			System.out.println("");
		}
	}
	
	
}
