package org.cinematics.handlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cinematics.exceptions.OutOfSeatingBoundsException;
import org.cinematics.model.Booking;
import org.cinematics.model.Customer;
import org.cinematics.model.Movie;
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
			
			//print out theater
			for (Theatre cT : dataManager.getTheatres()) {
				//print out seats in theatre..
				for(Show show : dataManager.getShowInTheatre(cT.getId())) {
					ArrayList<Ticket> tickets = dataManager.getAllTicketsInShow(show.getId());
					System.out.println("Showid:" + show.getId()); 
					System.out.println("Movie:" + dataManager.getMovie(show.getMovieId()).getName());
					System.out.println("Starttime:" + show.getStart());
					System.out.println("Theatre:" + cT.getName());
					//print out seats....
					System.out.println("  0 1 2 3 4 5 6 7 8 9");
					for	(int row = 0; row < 5; row++) {
						System.out.print(row + " ");
						for (int col = 0; col < 10; col++) {
							boolean seatPrinted = false;
							for (Ticket cTick : tickets) {
								
								if (cTick.getRow() == row && cTick.getColum() == col) {
									System.out.print("X ");
								seatPrinted = true;
								}
							}
							if(!seatPrinted) {
								System.out.print("O ");
							}
						}
						System.out.println("");
					}
				}
			}
			
			//Choose show
			int showId = UserInterface.getShowId();
			Show selectedShow = dataManager.getShow(showId);
			if (selectedShow == null) {
				System.out.println("Show does not exist");
				return;
			}
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
				
				if(dataManager.seatsExist(selectedShow,  numberOfSeats, startingRow, startingCol) && dataManager.areSeatsAvailable(selectedShow, numberOfSeats, startingRow, startingCol)) {
					//save booking
					Booking myBooking = new Booking();
					myBooking.setCustomerId(selectedCust.getCustId());
					myBooking.setShowId(selectedShow.getId());
					myBooking.setBookingId(dataManager.saveBooking(myBooking));
		
					//Save tickets
					for (int i = 0; i < numberOfSeats; i++) {
						dataManager.saveTicket(selectedShow, myBooking, startingRow, startingCol + i);
					}
					
					break;
				} else {
					System.out.println("Those seats do not exist or is occupied");
					continue;
				}	
			} 
			//Booking seats separately
			else {
					ArrayList<Ticket> tickets = new ArrayList<>();
					boolean allSeatsAvalible = true;
					
					int startingRow = Integer.MIN_VALUE;
					int startingCol = Integer.MIN_VALUE;
					//prepare booking
					for (int noOfTickets = 0; noOfTickets < numberOfSeats; noOfTickets++) {
						startingRow = UserInterface.chooseSeatRow();
						if(startingRow == Integer.MIN_VALUE) return;
						startingCol = UserInterface.chooseSeatCol();
						if(startingCol == Integer.MIN_VALUE) return;
						if (!dataManager.areSeatsAvailable(selectedShow, 1, startingRow, startingCol)) {
							allSeatsAvalible = false;
						}
						if (!dataManager.seatsExist(selectedShow, 1, startingRow, startingCol)) {
							allSeatsAvalible = false;
						}
						Ticket ticketToAdd = new Ticket();
						ticketToAdd.setRow(startingRow);
						ticketToAdd.setColum(startingCol);
						tickets.add(ticketToAdd);
					}
					//comitt to booking
					if (allSeatsAvalible) {
						Booking myBooking = new Booking();
						myBooking.setCustomerId(selectedCust.getCustId());
						myBooking.setShowId(selectedShow.getId());
						myBooking.setBookingId(dataManager.saveBooking(myBooking));
						
						for (Ticket cTicket : tickets) {
							dataManager.saveTicket(selectedShow, myBooking, cTicket.getRow(), cTicket.getColum());
						}
						System.out.println("Booking successfull");
						//put code for recipt here. 
					}
					else {
						System.out.println("Those seats do not exist or is occupied");
					}	
				}	
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
		
		boolean movieExist = false;
		//check if selected movie exists
		for(Movie cMovie : movies) {
			if (movieId == cMovie.getId()){
				movieExist = true;
			}
			
					
		}
		if (!movieExist) {
			System.out.println("Movie does not exist");
			return;
		}
		
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
		
		show.setStart(startTime);
		show.setEnd(endTime);
		show.setMovieId(movieId);
		show.setTheatreId(dataManager.getTheatre(chosenTheatre).getId());
		if (!dataManager.checkIfShowOverlaps(show)) {
			dataManager.addShow(show);
		}
		else {
			System.out.println("Show overlaps");
		}
	}

	public static void viewAllShowInTheatre(DataManager dataManager) {
		//Print out all theatres
		ArrayList<Theatre> theatres = dataManager.getAllTheatres();
		for (Theatre cTheatre : theatres) {
			System.out.println(cTheatre.getName());
		}
		
		String theatreName = UserInterface.getTheatreName();
		Theatre cT = dataManager.getTheatre(theatreName);
		
		if(cT == null) {
			System.out.println("That theatre does not exist.");
			return;
		}
		for(Show cS : dataManager.getShowInTheatre(cT.getId())) {
			System.out.println("Movie:" + dataManager.getMovie(cS.getMovieId()).getName() + " Theatre:" + cS.getTheatreId() + " Start:" +cS.getStart() + " End:" + cS.getEnd());
		}
		
	}

	public static void viewAllShows(DataManager dataManager) {
		System.out.println("All shows:");
		for (Theatre cT : dataManager.getTheatres()) {
			System.out.println("-"+cT.toString());
			//TODO fetch all shows in all theatres. 
			
			for(Show show : dataManager.getShowInTheatre(cT.getId())) {
				System.out.println("--- Movie:"+ dataManager.getMovie(show.getMovieId()).getName() + " " + show.toString());				
			}
			System.out.println("");
		}
	}
	
	
}
