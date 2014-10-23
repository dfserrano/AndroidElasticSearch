package ca.ualberta.ssrg.androidelasticsearch.data;

import java.util.List;

public class SearchResultElasticSearchResponse<T> {

	private int took;
	private boolean timed_out;
	private Shard _shards;
	private Hits<T> hits;
	
	public SearchResultElasticSearchResponse() {}

	public int getTook() {
		return took;
	}

	public void setTook(int took) {
		this.took = took;
	}

	public boolean isTimed_out() {
		return timed_out;
	}

	public void setTimed_out(boolean timed_out) {
		this.timed_out = timed_out;
	}

	public Shard get_shards() {
		return _shards;
	}

	public void set_shards(Shard _shards) {
		this._shards = _shards;
	}

	public Hits<T> getHits() {
		return hits;
	}

	public void setHits(Hits<T> hits) {
		this.hits = hits;
	}	   
}

class Hits<T> {
	private int total;
	private float max_score;
	private List<SimpleElasticSearchResponse<T>> hits;
	
	public Hits() {}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public float getMax_score() {
		return max_score;
	}

	public void setMax_score(float max_score) {
		this.max_score = max_score;
	}

	public List<SimpleElasticSearchResponse<T>> getHits() {
		return hits;
	}

	public void setHits(List<SimpleElasticSearchResponse<T>> hits) {
		this.hits = hits;
	}

	@Override
	public String toString() {
		return "Hits [total=" + total + ", max_score=" + max_score + ", hits="
				+ hits + "]";
	}
}
	

class Shard {
	private int total;
	private int successful;
	private int failed;
	
	public Shard() {}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccessful() {
		return successful;
	}
	public void setSuccessful(int successful) {
		this.successful = successful;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
}