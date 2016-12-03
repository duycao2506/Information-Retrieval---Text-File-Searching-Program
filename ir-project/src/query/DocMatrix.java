package query;

import java.util.ArrayList;
import java.util.Arrays;

public class DocMatrix {
	ArrayList<DocVector> matrix;
	
	public DocMatrix(int ds, int ts) {
		matrix = new ArrayList<DocVector>();
		for (int i = 0; i < ds; ++i) {
			matrix.add(new DocVector(i, ts));
		}
	}
	public int getNumDoc() {
		return this.matrix.size();
	}
	
	public double setEle(int doc, int term, double value) {
		matrix.get(doc).setValue(term, value);
		return value;
	}

	public void pushback(DocVector dv) {
		// TODO Auto-generated method stub
		matrix.add(dv);
	}
	
	public int sumRow(int term) {
		int f = 0;
		for (int i = 0; i < matrix.size(); ++i) {
			if (!matrix.get(i).isZero(term)) {
				++f;
			}
		}
		return f;
	}
	
	public void calcTF() {
		for (int i = 0; i < this.matrix.size(); ++i) {
			matrix.get(i).calcTF();
		}
	}
	
	public double[] calcIDF() {
		double[] result = new double[matrix.get(0).getTermSize()];
		Arrays.fill(result, 0);
		for (int i = 0; i < matrix.get(0).getTermSize(); i++) {
			for (int j = 0; j < matrix.size(); j++) {
				if (!matrix.get(j).isZero(i))
					result[i] += 1;
			}
			result[i] = 1 + Math.log(matrix.size()*1.0/result[i]);
		}
		return result;
	}
	
	public void calcTF_IDF() {
		double[] idf = this.calcIDF();
		this.calcTF();
		for (int i = 0; i < matrix.size(); ++i) {
			this.matrix.get(i).multiDoubleArray(idf);
		}
	}
	
	public DocResult[] multiLastVector() {
		DocResult[] dr = new DocResult[this.matrix.size() - 1];
		for (int i = 0; i < dr.length; ++i) {
			dr[i] = new DocResult();
		}
		for (int i = 0; i < this.matrix.size() - 1; ++i) {
			double top = this.matrix.get(i).multiDocVector(this.matrix.get(this.matrix.size() - 1));
			double bot = this.matrix.get(i).calcLength()*this.matrix.get(this.matrix.size() - 1).calcLength();
			dr[i].set(this.matrix.get(i).getId(), top/bot);
		}
		return dr;
	}
	
	public DocResult[] distanceLastVector() {
		DocResult[] dr = new DocResult[this.matrix.size() - 1];
		for (int i = 0; i < dr.length; ++i) {
			dr[i] = new DocResult();
		}
		for (int i = 0; i < this.matrix.size() - 1; ++i) {
			dr[i].set(this.matrix.get(i).getId(), this.matrix.get(i).calcDistance(this.matrix.get(this.matrix.size() - 1)));
		}
		return dr;
	}
	
	public void removeVectorZero() {
		for (int i = this.matrix.size()-1; i >=0 ; i--) {
			if (this.matrix.get(i).isZero()) {
				this.matrix.remove(i);
			}
		}
	}
}
