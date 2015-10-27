package ca.ualberta.ssrg.movies;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import ca.ualberta.ssrg.movies.es.data.SearchHit;
import ca.ualberta.ssrg.movies.es.data.SearchResponse;
import ca.ualberta.ssrg.movies.es.data.SimpleSearchCommand;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ESMovieManager {

	private static final String TAG = "MovieSearch";
	private Gson gson;
	private Movies movies = new Movies();

	public Movies getMovies() {
		return movies;
	}

	public ESMovieManager(String search) {
		gson = new Gson();
	}

	/**
	 * Get a movie with the specified id
	 */
	public Movie getMovie(int id) {
		SearchHit<Movie> sr = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(movies.getResourceUrl() + id);

		HttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		Type searchHitType = new TypeToken<SearchHit<Movie>>() {}.getType();

		try {
			sr = gson.fromJson(
					new InputStreamReader(response.getEntity().getContent()),
					searchHitType);
		} catch (JsonIOException e) {
			throw new RuntimeException(e);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return sr.getSource();

	}

	/**
	 * Get movies with the specified search string. If the search does not
	 * specify fields, it searches on all the fields.
	 */
	public Movies searchMovies(String searchString, String field) {
		Movies result = new Movies();

		/**
		 * Creates a search request from a search string and a field
		 */

		HttpPost searchRequest = new HttpPost(movies.getSearchUrl());

		String[] fields = null;
		if (field != null) {
			throw new UnsupportedOperationException("Not implemented!");
		}

		SimpleSearchCommand command = new SimpleSearchCommand(searchString);

		String query = gson.toJson(command);
		Log.i(TAG, "Json command: " + query);

		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(query);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		searchRequest.setHeader("Accept", "application/json");
		searchRequest.setEntity(stringEntity);
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpResponse response = null;
		try {
			response = httpClient.execute(searchRequest);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		/**
		 * Parses the response of a search
		 */
		Type searchResponseType = new TypeToken<SearchResponse<Movie>>() {
		}.getType();

		SearchResponse<Movie> esResponse;

		try {
			esResponse = gson.fromJson(
					new InputStreamReader(response.getEntity().getContent()),
					searchResponseType);
		} catch (JsonIOException e) {
			throw new RuntimeException(e);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		for (SearchHit<Movie> hit : esResponse.getHits().getHits()) {
			result.add(hit.getSource());
		}

		result.notifyObservers();

		return result;
	}
}
