package query;

public abstract class RelevanceCalculator {
	DocMatrix matrix;
	DocVector query;
	
	public RelevanceCalculator(DocMatrix matrix, DocVector query) {
		this.matrix = matrix;
		this.query = query;
		this.matrix.pushback(query);
	}

	public RelevanceCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DocMatrix getMatrix() {
		return matrix;
	}

	public void setMatrix(DocMatrix matrix) {
		this.matrix = matrix;
	}

	public DocVector getQuery() {
		return query;
	}

	public void setQuery(DocVector query) {
		this.query = query;
		this.matrix.pushback(query);
	}
	
	
	abstract public DocResult[] getResult();

	public void makeTF_IDF() {
		// TODO Auto-generated method stub
		this.matrix.calcTF_IDF();
	}
	
	
}
