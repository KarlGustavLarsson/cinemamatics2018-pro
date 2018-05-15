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
	private String name;	// The studio might have a name like "Blue Room"
	

	// Constructor
	public Theatre(String name) {
		this.name = name;
	}

	public List<Show> getAllShows() {
		String getAllShowsQuery = "SELECT * FROM cinema.shows INNER JOIN cinema.theatres ON theatres.id=shows.theatre_id WHERE name = ?;";
		ResultSet rs = DBQueryHelper.prepareAndExecuteStatementQuery(getAllShowsQuery,name).get();
		List<Show> shows = new ArrayList<>();
		try {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Show getShow(Integer showID) {
		return DataManager.getShowFromID(showID);
	}

	@Override
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
