package processing;



//Merge.java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Merge {
	public String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "InvertedIndex/";
	public String path1 = "/file_sorted_1";
	public String path2 = "/file_sorted_2";
	char[] cAlpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public String pathName(int i, boolean odd){
		String tmp = (odd) ? path1 : path2;
		return path + cAlpha[i] + tmp;
	}
	public void Merge(File file1, File file2, String fileName) throws IOException{
		BufferedReader in1 = new BufferedReader(new FileReader(file1));
		BufferedReader in2 = new BufferedReader(new FileReader(file2));
		PrintWriter out = new PrintWriter(fileName);
		String string1 = in1.readLine(), string2 = in2.readLine();
		while (string1 != null && string2 != null){
			String word1[] = string1.split(" ", 2);
			String word2[] = string2.split(" ", 2);
			if (word1[0].compareTo(word2[0]) < 0){
				out.println(string1);
				string1 = in1.readLine();
			}
			else {
				if (word1[0].compareTo(word2[0]) > 0){
					out.println(string2);
					string2 = in2.readLine();
				}
				else {
					out.println(word1[0] + commonDoc(word1[1], word2[1]));
					string1 = in1.readLine();
					string2 = in2.readLine();
				}
			}
		}
		while (string1 != null && string2 == null){
			out.println(string1);
			string1 = in1.readLine();
		}
		while (string2 != null && string1 == null){
			out.println(string2);
			string2 = in2.readLine();
		}
		in1.close();
		in2.close();
		out.close();
	}
	
	
	private String commonDoc(String word1, String word2){
		String docfre = "";
		String doc_fre1[] = word1.split(" ");
		String doc_fre2[] = word2.split(" ");
		int length1 = doc_fre1.length, length2 = doc_fre2.length, doc1 = 0, doc2 = 0;
		while (doc1 < length1 && doc2 < length2){
			String docfre1[] = doc_fre1[doc1].split("_");
			String docfre2[] = doc_fre2[doc2].split("_");
			int tmp1 = Integer.parseInt(docfre1[0]);
			int tmp2 = Integer.parseInt(docfre2[0]);
			if (tmp1 < tmp2){
				docfre += " " + doc_fre1[doc1++];
			}
			else {
				if (tmp1 > tmp2){
					docfre += " " + doc_fre2[doc2++];
				}
				else{
					int tmp = Integer.parseInt(docfre1[1]) + Integer.parseInt(docfre2[1]);
					docfre += " " + docfre1[0] + "_" + Integer.toString(tmp);
					doc1++;
					doc2++;
				}
			}
		}
		while (doc1 < length1){
			docfre += " " + doc_fre1[doc1++];
		}
		while (doc2 < length2){
			docfre += " " + doc_fre2[doc2++];
		}
		return docfre;
	}
	public void mergeFolder(boolean renew) throws FileNotFoundException, IOException{
		File[] allfolders = new File(path).listFiles();
		int j = 0;
		for (int i = 0; i < allfolders.length; i++){
			if (allfolders[i].isDirectory()){
				System.out.println(cAlpha[j]);
				mergeFile(allfolders[i].getPath() + path1, j++, true, true, renew);
			}
		}
		System.out.println("File Index created");
	}
	private void mergeFile(String fileName, int k, boolean odd, boolean clone, boolean renew) throws FileNotFoundException, IOException{
		File[] allfiles = new File(fileName).listFiles();
		if (allfiles == null) return;
		int length = allfiles.length;
		System.out.println("Merging " + length + " files");
		if (length == 0){
			if (renew){
				Path path1 = emptyFile().toPath();
				Path path2 = Paths.get(path).resolve(cAlpha[k] + ".txt");
    			Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING);
			}
			return;
		}
		if (length > 1 || (length == 1 && clone) || !renew){
			if (odd){
				odd = false;
				fileName = pathName(k, odd);
			}
			else {
				odd = true;
				fileName = pathName(k, odd);
			}
		}
		if (length == 1){			
			fileName += "/" + allfiles[0].getName();
			if (clone || !renew){
				mergeOld(allfiles[0], fileName, clone, renew, k);
				allfiles[0].delete();
			}
			Path path1 = Paths.get(fileName);
			Path path2 = Paths.get(path).resolve(cAlpha[k] + ".txt");
    		Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING);
			return;
		}
        int i = 0, j = length - 1;
        while (i <= j){
        	if (i != j){
        		Merge(allfiles[i], allfiles[j], fileName + "/" + allfiles[i].getName());
        		allfiles[i++].delete();
        		allfiles[j--].delete();
        	}
        	else{
        		Path path1 = allfiles[i++].toPath();
        		Path path2 = Paths.get(fileName).resolve(allfiles[j--].getName());
        		Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING);
        	}
        }
        mergeFile(fileName, k, odd, false, renew);
	}
	private void mergeOld(File file, String fileName, boolean clone, boolean renew, int k) throws IOException{
		File tmp = null;
		if (!renew){
			tmp = new File(path + cAlpha[k] + ".txt");	
		}
		else 
			if (clone){
				tmp = emptyFile();
			}
		Merge(file, tmp, fileName);
	}
	private File emptyFile() throws IOException{
		File file = new File("src/clone.txt");
		file.createNewFile();
		return file;
	}
}
