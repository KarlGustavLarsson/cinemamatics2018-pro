package org.cinematics.model;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

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
