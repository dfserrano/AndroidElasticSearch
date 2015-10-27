package ca.ualberta.ssrg.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import ca.ualberta.ssrg.androidelasticsearch.R;

public class DetailsActivity extends Activity {
	public static String MOVIE_ID = "MOVIE_ID";

	private ESMovieManager movieManager;
	private Movie movie;
	
	private Runnable doUpdateGUIDetails = new Runnable() {
		public void run() {
			TextView title = (TextView) findViewById(R.id.detailsTitle);
			TextView director = (TextView) findViewById(R.id.detailsDirector);
			TextView year = (TextView) findViewById(R.id.detailsYear);
			TextView genre = (TextView) findViewById(R.id.detailsGenre);
			
			title.setText(movie.getTitle());
			director.setText(movie.getDirector());
			year.setText(String.valueOf(movie.getYear()));
			genre.setText(movie.getGenres().toString());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		movieManager = new ESMovieManager("");
		Intent intent = getIntent();

		if (intent != null) {
			Bundle extras = intent.getExtras();

			if (extras != null) {
				int movieId = extras.getInt(MOVIE_ID);
				
				Thread thread = new GetThread(movieId);
				thread.start();
			}
		}
	}
	
	class GetThread extends Thread {
		private int id;

		public GetThread(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			movie = movieManager.getMovie(id);

			runOnUiThread(doUpdateGUIDetails);
		}
	}

}
