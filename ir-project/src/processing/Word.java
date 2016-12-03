package processing;

//Word.java
import java.util.ArrayList;

public class Word{
		private String word;
		private ArrayList<Occurence> occurences;
		Word(){
			occurences = new ArrayList<Occurence>();
		}
		Word(String w, int d){
			word = w;
			occurences = new ArrayList<Occurence>();
			occurences.add(new Occurence(d,1));
		}
		public String toString() {
			String tmp = word;
			for (int i = 0; i < occurences.size(); i++)
				tmp += " " + occurences.get(i).toString();
			return tmp;
		}
		
		public void insert(int d){
			this.occurences.add(new Occurence(d,1));
		}
		
		public void incr(int index){
			int curfr = this.occurences.get(index).getFre()+1;
			this.occurences.get(index).setFre(curfr);
		}
		
		public int indexOf(int d){
			for (int i = 0; i < this.occurences.size(); i++)
				if (this.occurences.get(i).getDoc() == d)
					return i;
			return -1;
		}
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
		public ArrayList<Occurence> getOccurences() {
			return occurences;
		}
		public void setOccurences(ArrayList<Occurence> occurences) {
			this.occurences = occurences;
		}
	}

class Occurence{
	private int doc;
	private int fre;
	public int getDoc() {
		return doc;
	}
	public void setDoc(int doc) {
		this.doc = doc;
	}
	public int getFre() {
		return fre;
	}
	public void setFre(int fre) {
		this.fre = fre;
	}
	public Occurence(int doc, int fre) {
		super();
		this.doc = doc;
		this.fre = fre;
	}
	public Occurence() {
		super();
	}
	public String toString() {
		return this.doc + "_" + this.fre;
	}
	
	
	
}