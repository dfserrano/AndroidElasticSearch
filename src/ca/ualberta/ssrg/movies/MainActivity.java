package ca.ualberta.ssrg.movies;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ca.ualberta.ssrg.androidelasticsearch.R;
import ca.ualberta.ssrg.movies.es.ESMovieManager;
import ca.ualberta.ssrg.movies.es.IMovieManager;
import ca.ualberta.ssrg.movies.es.Movie;

public class MainActivity extends Activity {

	private ListView movieList;
	private List<Movie> movies;
	private ArrayAdapter<Movie> moviesViewAdapter;

	private IMovieManager movieManager;

	private Context mContext = this;

	// Thread to update adapter after an operation
	private Runnable doUpdateGUIList = new Runnable() {
		public void run() {
			moviesViewAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		movieList = (ListView) findViewById(R.id.movieList);
	}

	@Override
	protected void onStart() {
		super.onStart();

		movies = new ArrayList<Movie>();
		moviesViewAdapter = new ArrayAdapter<Movie>(this, R.layout.list_item,movies);
		movieList.setAdapter(moviesViewAdapter);
		movieManager = new ESMovieManager();

		// Show details when click on a movie
		movieList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,	long id) {
				int movieId = movies.get(pos).getId();
				startDetailsActivity(movieId);
			}

		});

		// Delete movie on long click
		movieList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Movie movie = movies.get(position);
				Toast.makeText(mContext, "Deleting " + movie.getTitle(), Toast.LENGTH_LONG).show();

				Thread thread = new DeleteThread(movie.getId());
				thread.start();

				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Refresh the list when visible
		// TODO: Search all
		
	}

	/** 
	 * Search for movies with a given word(s) in the text view
	 * @param view
	 */
	public void search(View view) {
		movies.clear();

		// TODO: Extract search query from text view
		
		// TODO: Run the search thread
		
	}
	
	/**
	 * Starts activity with details for a movie
	 * @param movieId Movie id
	 */
	public void startDetailsActivity(int movieId) {
		Intent intent = new Intent(mContext, DetailsActivity.class);
		intent.putExtra(DetailsActivity.MOVIE_ID, movieId);

		startActivity(intent);
	}
	
	/**
	 * Starts activity to add a new movie
	 * @param view
	 */
	public void add(View view) {
		Intent intent = new Intent(mContext, AddActivity.class);
		startActivity(intent);
	}


	class SearchThread extends Thread {
		// TODO: Implement search thread
		
	}

	
	class DeleteThread extends Thread {
		private int movieId;

		public DeleteThread(int movieId) {
			this.movieId = movieId;
		}

		@Override
		public void run() {
			movieManager.deleteMovie(movieId);

			// Remove movie from local list
			for (int i = 0; i < movies.size(); i++) {
				Movie m = movies.get(i);

				if (m.getId() == movieId) {
					movies.remove(m);
					break;
				}
			}

			runOnUiThread(doUpdateGUIList);
		}
	}
}