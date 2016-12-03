package query;

public class DocResult {
	int id;
	double relevance;
	
	public DocResult(int id, double relevance) {
		this.id = id;
		this.relevance = relevance;
	}

	public DocResult() {
		// TODO Auto-generated constructor stub
		this.id = 0;
		this.relevance = 0;
	}
	
	public void set(int id, double relevance) {
		this.id = id;
		this.relevance = relevance;
	}
	
	public double compare(DocResult dr) {
		return this.relevance - dr.relevance;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	
	
	
}
