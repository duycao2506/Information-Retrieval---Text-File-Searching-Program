package query;

import java.util.Arrays;

public class DocVector {
	int id;
	double[] values;
	
	public DocVector(int id, double[] values) {
		this.id = id;
		this.values = values;
	}
	
	public DocVector(int id, int size) {
		this.id = id;
		this.values = new double[size];
		Arrays.fill(values, 0);
	}
	
	public double calcLength() {
		double l = 0;
		for (int i = 0; i < values.length; ++i) {
			l += this.values[i] * this.values[i];
		}
		l = Math.sqrt(l);
		return l;
	}
	
	public double calcDistance(DocVector dv) {
		double d = 0;
		for (int i = 0; i < values.length; ++i) {
			d += (this.values[i] - dv.values[i])*(this.values[i] - dv.values[i]);
		}		
		d = Math.sqrt(d);		
		return d;
	}
	
	public double multiDocVector(DocVector dv) {
		double result = 0;
		for (int i = 0; i < values.length; ++i) {
			result += this.values[i]*dv.values[i];
		}
		return result;
	}

	public void setValue(int term, double value) {
		// TODO Auto-generated method stub
		this.values[term] = value;
	}
	
	public boolean isZero(int term) {
		return (values[term] == 0);
	}
	
	public void calcTF() {
		for (int i = 0; i < values.length; i++) {
			if (this.values[i] != 0)
				this.values[i] = 1 + Math.log(this.values[i]+1);
		}
	}
	
	public int getTermSize() {
		return values.length;
	}
	
	public void multiDoubleArray(double[] values) {
		for (int i = 0; i < values.length; i++) {
			this.values[i] = this.values[i]*values[i];
		}
	}
	
	// ADD THIS FUNCTION
	
	public boolean isZero() {
		for (int i = 0; i < this.values.length; i++) {
			if (this.values[i] >= 1) {
				return false;
			}
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
	
}
