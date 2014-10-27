package ca.ualberta.ssrg.movies.es;

import java.util.List;


public interface IMovieManager {

	public List<Movie> searchMovies(String searchString, String field);
	public Movie getMovie(int id);
	public void addMovie(Movie movie);
	public void deleteMovie(int id);
}
