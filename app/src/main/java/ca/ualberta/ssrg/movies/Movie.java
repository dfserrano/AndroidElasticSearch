package ca.ualberta.ssrg.movies;

import java.util.Collection;

public class Movie {
	public int id;
	private String title;
	private String director;
	private int year;
	private Collection<String> genres;
	
	public Movie() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Collection<String> getGenres() {
		return genres;
	}

	public void setGenres(Collection<String> genres) {
		this.genres = genres;
	}

	@Override
	public String toString() {
		return title + " (" + year + ")";
	}
}
