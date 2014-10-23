package ca.ualberta.ssrg.androidelasticsearch.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import ca.ualberta.ssrg.androidelasticsearch.command.SimpleSearchCommand;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ElasticSearchMovieManager implements MovieManager {

	private static final String SEARCH_URL = "http://cmput301.softwareprocess.es:8080/testing/movie/_search";
	private static final String GET_URL = "http://cmput301.softwareprocess.es:8080/testing/movie/";
	private static final String TAG = "MovieSearch";

	private Gson gson;

	public ElasticSearchMovieManager() {
		gson = new Gson();
	}

	/**
	 * Get a movie with the specified id
	 */
	public Movie getMovie(int id) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(GET_URL + id);

		HttpResponse response;

		try {
			response = httpClient.execute(httpGet);

			final String json = getEntityContent(response);
			Type collectionType = new TypeToken<SimpleElasticSearchResponse<Movie>>() {
			}.getType();
			SimpleElasticSearchResponse<Movie> sr = gson.fromJson(json,
					collectionType);

			return sr.getSource();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

		if ("".equals(searchString)) {
			searchString = "*";
		}

		Log.i(TAG, "Searching for " + searchString);

		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost searchRequest = createSearchRequest(searchString, field);

			HttpResponse response = httpClient.execute(searchRequest);

			String status = response.getStatusLine().toString();
			Log.i(TAG, status);

			SearchResultElasticSearchResponse<Movie> esResponse = parseResponse(response);
			Hits<Movie> hits = esResponse.getHits();

			if (hits != null) {
				if (hits.getHits() != null) {
					for (SimpleElasticSearchResponse<Movie> sesr : hits
							.getHits()) {
						result.add(sesr.getSource());
					}
				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Adds a new movie
	 */
	public void addMovie(Movie movie) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost addRequest = new HttpPost(GET_URL + movie.getId());

			StringEntity stringEntity = new StringEntity(gson.toJson(movie));
			addRequest.setEntity(stringEntity);
			addRequest.setHeader("Accept", "application/json");

			HttpResponse response = httpClient.execute(addRequest);
			String status = response.getStatusLine().toString();
			Log.i(TAG, status);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the movie with the specified id
	 */
	public void deleteMovie(int movieId) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpDelete deleteRequest = new HttpDelete(GET_URL + movieId);
			deleteRequest.setHeader("Accept", "application/json");

			HttpResponse response = httpClient.execute(deleteRequest);
			String status = response.getStatusLine().toString();
			Log.i(TAG, status);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a search request from a search string and a field
	 */
	private HttpPost createSearchRequest(String searchString, String field)
			throws UnsupportedEncodingException {
		HttpPost searchRequest = new HttpPost(SEARCH_URL);

		String[] fields = null;
		if (field != null) {
			fields = new String[1];
			fields[0] = field;
		}
		SimpleSearchCommand command = new SimpleSearchCommand(searchString,
				fields);
		String query = command.getJsonCommand();
		Log.i("XXX", "Json command: " + query);

		StringEntity stringEntity;
		stringEntity = new StringEntity(query);

		searchRequest.setHeader("Accept", "application/json");
		searchRequest.setEntity(stringEntity);

		return searchRequest;
	}

	/**
	 * Parses the response of a search
	 */
	private SearchResultElasticSearchResponse<Movie> parseResponse(
			HttpResponse response) throws IOException {
		String json;
		json = getEntityContent(response);

		Type elasticSearchSearchResponseType = new TypeToken<SearchResultElasticSearchResponse<Movie>>() {
		}.getType();
		SearchResultElasticSearchResponse<Movie> esResponse = gson.fromJson(
				json, elasticSearchSearchResponseType);

		return esResponse;
	}

	/**
	 * Gets content from an HTTP response
	 */
	public String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
}
