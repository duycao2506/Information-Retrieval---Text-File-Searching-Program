package query;

public class CosineCalculator extends RelevanceCalculator{

	public CosineCalculator(DocMatrix matrix, DocVector query) {
		// TODO Auto-generated constructor stub
		super(matrix, query);
		matrix.calcTF_IDF();
	}

	
	public CosineCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}


	public DocResult[] getResult() {
		if (matrix == null) return null;
		DocResult[] dr = matrix.multiLastVector();
		for (int i = 0; i < dr.length - 1; ++i) {
			for (int j = i+1; j < dr.length; ++j) {
				if(dr[i].compare(dr[j]) < 0) {
					DocResult tmp = dr[i];
					dr[i] = dr[j];
					dr[j] = tmp;
				}
			}
		}
		return dr;
	}
}
