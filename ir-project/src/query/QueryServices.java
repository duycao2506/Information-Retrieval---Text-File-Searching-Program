package query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.DocumentParser;
import processing.Main;

public class QueryServices {
	private ArrayList<String> keywords;
	private ArrayList<Double> fr;
	private double[] frequencies;
	private int totaldocs = 0;
	private DocMatrix dm;
	public QueryServices(String raw, int totaldoc){
		keywords = new ArrayList<String>();
		fr = new ArrayList<Double>();
		String[] keywordstmp = raw.replaceAll(DocumentParser.token, " ").trim().split(" ");
		for (int i = 0; i < keywordstmp.length; i++){
			int id = keywords.indexOf(keywordstmp[i]);
			if ( id == -1){
				keywords.add(keywordstmp[i]);
				fr.add(1.0);
			}else{
				fr.set(id, fr.get(id) + 1);
			}
			
		}
		this.totaldocs = totaldoc;
		
		this.dm = new DocMatrix(totaldoc, keywords.size());
	}
	
	public DocVector buildDocVector(){
		double[] a = new double[fr.size()];
		for (int i = 0; i < fr.size();i++)
			a[i] = fr.get(i).doubleValue();
		DocVector dv = new DocVector(-1,a);
		return dv;
	}
	
	public DocMatrix buildDocMarix(String path) throws IOException {
		DocMatrix dm = new DocMatrix(totaldocs, keywords.size());
		
		for (int i = 0; i < keywords.size(); ++i) {
			ArrayList<String> al = this.invertedIndexOf(keywords.get(i));
			for (int j = 0; j < al.size(); ++j) {
				String[] dump = al.get(j).split("_");
				dm.setEle(Integer.parseInt(dump[0])-1, i, Integer.parseInt(dump[1]));
			}
		}
		dm.removeVectorZero();
		return dm;
	}
	
	public ArrayList<String> invertedIndexOf(String querykey) throws IOException{
		BufferedReader in = null;
		String keyfind = querykey.toLowerCase();
		char start = DocumentParser.removeAccent(keyfind).charAt(0);
		int index = start % 'a';
		char filename = (char) ('a' + index);
		String fileIndexPath = Main.dicPath + filename + ".txt";
		File f = new File(fileIndexPath);
		in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf8"));
		
		String s = null;
		ArrayList<String> invertedIndices = new ArrayList<String>();
		while ((s=in.readLine()) != null){
			List<String> tmp = Arrays.asList(s.split(" "));
			if(tmp.get(0).compareTo(keyfind) == 0){
				invertedIndices.addAll(tmp);
				invertedIndices.remove(0);
				return invertedIndices;
			}
				
		}
		
//		in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf16"));   
//		StringBuilder sb = new StringBuilder();   
//		String s = null;   
//		while ((s = in.readLine()) != null) {   
//			sb.append(s + "\n");   
//		}
		return invertedIndices;
	}
}
