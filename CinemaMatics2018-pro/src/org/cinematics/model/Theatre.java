package org.cinematics.model;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cinematics.db.DBQueryHelper;
import org.cinematics.handlers.DataManager;

// Describes the Theatre studio where the movie is shown

public class Theatre {
	private transient int id;
	public static int SEAT_ROWS = 5;
	public static int SEAT_COLS = 10;
	private String name;						// The studio might have a name like "Blue Room"				// The show that is booked for the studio


	// Constructor
	public Theatre(String name) {
		this.name = name;
	}

	public List<Show> getAllShows() {
		String getAllShowsQuery = "SELECT * FROM cinema.shows INNER JOIN cinema.theatres ON theatres.id=shows.theatre_id WHERE name = ?;";
		List<Show> shows = new ArrayList<>();

		try {
			ResultSet rs = DBQueryHelper.prepareAndExecuteStatementQuery(getAllShowsQuery,name).get();
			Show show;
			while(rs.next()) {
				show = new Show();
				show.setId(rs.getInt("id"));
				show.setMovieID(rs.getInt("movie_id"));
				show.setStart(rs.getTimestamp("starttime").toLocalDateTime());
				show.setEnd(rs.getTimestamp("endtime").toLocalDateTime());
				shows.add(show);
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return shows;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the show associated with this theatre id
	 */
	public Show getShow(Integer showID) {
		return DataManager.getShowFromID(showID);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
}
