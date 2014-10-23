package ca.ualberta.ssrg.androidelasticsearch.data;

import java.util.List;


public interface MovieManager {

	public List<Movie> searchMovies(String searchString, String field);
	public Movie getMovie(int id);
	public void addMovie(Movie movie);
	public void deleteMovie(int id);
}
