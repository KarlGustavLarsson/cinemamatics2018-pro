package org.cinematics.handlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cinematics.exceptions.OutOfSeatingBoundsException;
import org.cinematics.model.Booking;
import org.cinematics.model.Customer;
import org.cinematics.model.Movie;
import org.cinematics.model.Seat;
import org.cinematics.model.Show;
import org.cinematics.model.Theatre;
import org.cinematics.model.Ticket;

import java.sql.Connection;

public class Program {

	public static void main(String[] args) {
		boolean done = false;
		DataManager dataManager = new DataManager();
		UserInterface ui = new UserInterface();
		
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
				
			case 8:
				// testing
				
				break;
			default:
				System.out.println("That is not a valid menu option");
			}
		}
	}
	
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
		dataManager.addMovie(movie);
	}
	
	public static void addTheatre(DataManager dataManager) {
		
		System.out.println("Add theatre name(leave blank to exit):");
		String name = UserInterface.getUserInputString();
		if(name.equals("")) {
			return;
		}
		Theatre cTheatre = new Theatre();
		cTheatre.setName(name);
		if(dataManager.addTheatre(cTheatre)){
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
			//Printing out all shows and avalible seats
			//System.out.println("Choice "+choice+" please");
			
			//print out theater
			for (Theatre cT : dataManager.getTheatres()) {
				//print out seats in theatre..
				for(Show show : dataManager.getShowInTheatre(cT.getId())) {
					System.out.println("showid:" + show.getId());
					//print out seats....
					for (Ticket cTicket: dataManager.getAllTicketsInShow(show.getId())) {
						System.out.println("Row:" + cTicket.getRow() + " Colum:" + cTicket.getColum() );
					}
				}
			}
			
			
			//Choose show
			int showId = UserInterface.getShowId();
			Show selectedShow = dataManager.getShow(showId);
			Customer selectedCust = new Customer();
			selectedCust.setCustId(1);
			selectedCust.setName("kunden");
			//choose number of seats.. 
			int numberOfSeats = UserInterface.chooseNumberOfSeats();
			if(numberOfSeats == Integer.MIN_VALUE) return;
			
			//Find out if seats should be together... 
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
			
			//Booking seats together
			if(seatsTogether) {
				System.out.println("Choose starting seat:");
				int startingRow = UserInterface.chooseSeatRow();
				if(startingRow == Integer.MIN_VALUE) return;
				int startingCol = UserInterface.chooseSeatCol();
				if(startingCol == Integer.MIN_VALUE) return;
				
				if(dataManager.areSeatsAvailable()) {
					//save booking
					Booking myBooking = new Booking();
					myBooking.setCustomerId(selectedCust.getCustId());
					myBooking.setShowId(selectedShow.getId());
					myBooking.setBookingId(dataManager.saveBooking(myBooking));
					
					// 
					//Save tickets
					for (int i = 0; i < numberOfSeats; i++) {
						dataManager.saveTicket(selectedShow, myBooking, startingRow, startingCol + i);
					}
					
					break;
				} else {
					System.out.println("Those seats are not available");
					continue;
				}	
			} 
			//Booking seats separatly
			else {
				
				int startingRow = UserInterface.chooseSeatRow();
					if(startingRow == Integer.MIN_VALUE) return;
					int startingCol = UserInterface.chooseSeatCol();
					if(startingCol == Integer.MIN_VALUE) return;
				}
				
				
				System.out.println("Booking succeeded");
				
			}
			doneWithBooking = true;
		
	}
	
	public static void createShow(DataManager dataManager) {
		Show show = new Show();
		System.out.println("MOVIES: ");
		ArrayList<Movie> movies = dataManager.getAllMovies();
		for(Movie movie : movies) {
			System.out.println(movie.toString());
		}
		int movieId = UserInterface.enterMovieId();
		
		System.out.println("Theatres: ");
		
		ArrayList<Theatre> theatres = dataManager.getAllTheatres();
		
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
//		//List<Show> shows = theatre.getAllShows();
//		List<Show> overlappingShows = new ArrayList<Show>();
//		for(Show currentShow : shows) {
//			if(currentShow.checkOverlap(startTime, endTime)) {
//				overlappingShows.add(currentShow);
//			}
//		}
//		if(!overlappingShows.isEmpty()) {
//			System.out.println("Show is overlapping with :");
//			overlappingShows.forEach(System.out::println);
//			return;
//		}

		show.setStart(startTime);
		show.setEnd(endTime);
		show.setMovieId(movieId);
		show.setTheatreId(dataManager.getTheatre(chosenTheatre).getId());
		
		dataManager.addShow(show);
		
	}

	public static void viewAllShowInTheatre(DataManager dataManager) {
		String theatreName = UserInterface.getTheatreName();
		Theatre cT = dataManager.getTheatre(theatreName);
		
		if(cT == null) {
			System.out.println("That theatre does not exist.");
			return;
		}
		for(Show cS : dataManager.getShowInTheatre(cT.getId())) {
			System.out.println("Movie:" + cS.getMovieId() + " Theatre:" + cS.getTheatreId() + " Start:" +cS.getStart() + " End:" + cS.getEnd());
		}
		
	}

	public static void viewAllShows(DataManager dataManager) {
		System.out.println("All shows:");
		for (Theatre cT : dataManager.getTheatres()) {
			System.out.println("-"+cT.toString());
			//TODO fetch all shows in all theatres. 
			
			for(Show show : dataManager.getShowInTheatre(cT.getId())) {
				System.out.println("---"+show.toString());
			}
			System.out.println("");
		}
	}
	
	
}
