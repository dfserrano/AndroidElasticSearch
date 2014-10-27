package ca.ualberta.ssrg.movies.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import ca.ualberta.ssrg.movies.es.data.Hits;
import ca.ualberta.ssrg.movies.es.data.SearchHit;
import ca.ualberta.ssrg.movies.es.data.SearchResponse;
import ca.ualberta.ssrg.movies.es.data.SimpleSearchCommand;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ESMovieManager implements IMovieManager {

	private static final String SEARCH_URL = "http://cmput301.softwareprocess.es:8080/testing/movie/_search";
	private static final String RESOURCE_URL = "http://cmput301.softwareprocess.es:8080/testing/movie/";
	private static final String TAG = "MovieSearch";

	private Gson gson;

	public ESMovieManager() {
		gson = new Gson();
	}

	/**
	 * Get a movie with the specified id
	 */
	public Movie getMovie(int id) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(RESOURCE_URL + id);

		HttpResponse response;

		try {
			response = httpClient.execute(httpGet);
			SearchHit<Movie> sr = parseMovieHit(response);
			return sr.getSource();

		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	

	/**
	 * Get movies with the specified search string. If the search does not
	 * specify fields, it searches on all the fields.
	 */
	public List<Movie> searchMovies(String searchString, String field) {
		List<Movie> result = new ArrayList<Movie>();

		// TODO: Implement search movies using ElasticSearch
		
		return result;
	}

	/**
	 * Adds a new movie
	 */
	public void addMovie(Movie movie) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost addRequest = new HttpPost(RESOURCE_URL + movie.getId());

			StringEntity stringEntity = new StringEntity(gson.toJson(movie));
			addRequest.setEntity(stringEntity);
			addRequest.setHeader("Accept", "application/json");

			HttpResponse response = httpClient.execute(addRequest);
			String status = response.getStatusLine().toString();
			Log.i(TAG, status);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the movie with the specified id
	 */
	public void deleteMovie(int movieId) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpDelete deleteRequest = new HttpDelete(RESOURCE_URL + movieId);
			deleteRequest.setHeader("Accept", "application/json");

			HttpResponse response = httpClient.execute(deleteRequest);
			String status = response.getStatusLine().toString();
			Log.i(TAG, status);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a search request from a search string and a field
	 */
	private HttpPost createSearchRequest(String searchString, String field)	throws UnsupportedEncodingException {
		
		HttpPost searchRequest = new HttpPost(SEARCH_URL);

		String[] fields = null;
		if (field != null) {
			fields = new String[1];
			fields[0] = field;
		}
		
		SimpleSearchCommand command = new SimpleSearchCommand(searchString,	fields);
		
		String query = command.getJsonCommand();
		Log.i(TAG, "Json command: " + query);

		StringEntity stringEntity;
		stringEntity = new StringEntity(query);

		searchRequest.setHeader("Accept", "application/json");
		searchRequest.setEntity(stringEntity);

		return searchRequest;
	}
	
	private SearchHit<Movie> parseMovieHit(HttpResponse response) {
		
		try {
			String json = getEntityContent(response);
			Type searchHitType = new TypeToken<SearchHit<Movie>>() {}.getType();
			
			SearchHit<Movie> sr = gson.fromJson(json, searchHitType);
			return sr;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Parses the response of a search
	 */
	private SearchResponse<Movie> parseSearchResponse(HttpResponse response) throws IOException {
		String json;
		json = getEntityContent(response);

		Type searchResponseType = new TypeToken<SearchResponse<Movie>>() {
		}.getType();
		
		SearchResponse<Movie> esResponse = gson.fromJson(json, searchResponseType);

		return esResponse;
	}

	/**
	 * Gets content from an HTTP response
	 */
	public String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
}
