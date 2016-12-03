package processing;

//DocumentParser.java  
import java.io.BufferedReader;   
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import org.apache.commons.io.FileUtils;

public class DocumentParser {   	
	private ArrayList<Alphabet> alphabet = null;
    public static int blocksize = 5, docFolder = 1000, totalDoc;
    private static int[] block = new int[26];
    public static String token = 
    "[^a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêếìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểẾỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựýỳỵỷỹ]+";
	
    
    public void readFolder(String filePath) throws IOException{
		File[] allfiles = new File(filePath).listFiles();
		readFiles(allfiles, true, 0);
	}
	private void cleanDir(Merge merge, int[] block) throws IOException{
		for (int i = 0; i < 26; i++){
			block[i] = 0;
			FileUtils.cleanDirectory(new File(merge.pathName(i, true)));
			FileUtils.cleanDirectory(new File(merge.pathName(i, false)));
		}
	}
	public void readFiles(File[] allfiles, boolean renew, int tDoc) throws IOException{
		alphabet = new ArrayList<Alphabet>(26);
		for (int i = 0; i<26; i++){
			alphabet.add(new Alphabet());
		}
		Merge merge = new Merge();
		cleanDir(merge, block);
		totalDoc = tDoc;
		if (renew)
			System.out.println("Import files...");
		else
			System.out.println("Add files...");
        BufferedReader in = null;               
        for (File f : allfiles) {
        	if (f.getName().endsWith(".txt")){
        		System.out .println("Parsing document " + ++totalDoc);
        		in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf16"));   
        		StringBuilder sb = new StringBuilder();   
        		String s = null;   
        		while ((s = in.readLine()) != null) {   
        			sb.append(s + "\n");   
        		}      		        		        		        		
        		String[] tokenizedTerms = sb.toString().toLowerCase().replaceAll(token, " ").split(" ");
        		for (int i = 0; i<tokenizedTerms.length; i++) { 
        			if (tokenizedTerms[i].length() > 1){
        				char c = removeAccent(tokenizedTerms[i]).charAt(0);
        				int element = (int)c % 97;
        				if (!binaryInsert(element, tokenizedTerms[i])){
        					if (alphabet.get(element).arrayWord.size() == blocksize){
        						String fileName = merge.pathName(element, true) + "/File_Sorted_" + block[element]++ + ".txt";
                				printFile(fileName, alphabet.get(element));
                				alphabet.get(element).arrayWord = new ArrayList<Word>();
        					}
        				}        				
        			}
        		}           
        		in.close();       
        	}
        }
        for (int i = 0; i < 26; i++){
        	if (alphabet.get(i).arrayWord.size() > 0){
        		String fileName = merge.pathName(i, true) + "/File_Sorted_" + block[i] + ".txt";
        		printFile(fileName, alphabet.get(i));
        	}
        }
        merge.mergeFolder(renew); 
	}
	
	private boolean binaryInsert(int element, String value){
		int lower = 0, upper = alphabet.get(element).arrayWord.size() - 1, curIn = (lower + upper) / 2;		
		while (lower <= upper) {
			String tmp = alphabet.get(element).arrayWord.get(curIn).getWord();
			if (tmp.compareTo(value) < 0)
				lower = curIn + 1;
			else if (tmp.compareTo(value) > 0)
				upper = curIn - 1;
			else {
				int id = alphabet.get(element).arrayWord.get(curIn).indexOf(totalDoc);
				if (id == -1)
					alphabet.get(element).arrayWord.get(curIn).insert(totalDoc);
				else
					alphabet.get(element).arrayWord.get(curIn).incr(id);
				return true;
			}
			curIn = (lower + upper) / 2;
		}
		if (lower == alphabet.get(element).arrayWord.size())
			alphabet.get(element).arrayWord.add(new Word(value, totalDoc));
		else
			alphabet.get(element).arrayWord.add(lower, new Word(value, totalDoc));
				
		return false;
	}	
	public static String removeAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d");
	}
	private void printFile(String fileName, Alphabet arrayList) throws IOException{
		FileOutputStream file = new FileOutputStream(fileName);
        for (int i = 0; i < arrayList.arrayWord.size(); i++){
        	Word word = arrayList.arrayWord.get(i);
        	String string = word.toString() + System.getProperty("line.separator");
    		file.write(string.getBytes("utf8"));
    	}
        file.close();
    }
}