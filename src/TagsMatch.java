import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;


public class TagsMatch {
	static String[] fileList;
	public static final String projectFolderPath = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\";
	public static void search(String sentence) throws Exception{
		String[] keywords = sentence.split(" +");
		keywords = stemmedKeywords(keywords);
		
		ArrayList<Integer> searchResults = new ArrayList<Integer>();
		
		FileInputStream fistream = new FileInputStream("index.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	
    	String strLine;
		for (int i = 0; i < keywords.length; i ++){
			while((strLine = br.readLine()) != null){
				String[] terms = strLine.split(" ");
				if (terms[0].equals(keywords[i])) { // if it finds a match
					String[] docs = strLine.split("->")[1].split(" ");
					searchResults = mergeResult(docs,searchResults);//save the doc numbers
					break; // stop reading and check the next query word
				}
			}
		}
		String[] finalResults = new String[searchResults.size()];
		for (int i = 0; i < searchResults.size(); i ++){
			//System.out.println(fileList[searchResults.get(i)]+" ");
			finalResults[i] = fileList[searchResults.get(i)];
		}
		checkCategory(finalResults);
	}
	public static void checkCategory(String[] filenames){
		String trainFolder = projectFolderPath+"ImageData\\train\\data\\";
		File file = new File(trainFolder);
		String[] directories = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
	    });
		//System.out.println(Arrays.toString(directories));
		for (int i = 0; i < filenames.length; i ++){
			for (int j = 0; j < directories.length; j ++){
			    boolean exist = new File(trainFolder+directories[j], filenames[i]).exists();
			    if (exist) {
				    System.out.println(filenames[i]+" : "+directories[j]);
			    }
		    }
		}		
	}
	public static String[] stemmedKeywords(String[] keywords){
		return stemTerms(keywords);
	}
	public static ArrayList<Integer> mergeResult(String[] docs, ArrayList<Integer> result){
		for (int i = 0; i < docs.length; i ++) {
			int num = Integer.parseInt(docs[i]);
			if (!result.contains(num)){
				result.add(num);
			}
		}
		return result;
	}
	public static void index() throws Exception{
		separateTerms();
		sortTerms();
		buildLinkedList();
	}
    public static void separateTerms() throws Exception{
    	String trainTagsFile = projectFolderPath+"ImageData\\train\\train_tags.txt";
    	FileInputStream fistream = new FileInputStream(trainTagsFile);
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	
    	FileOutputStream fostream = new FileOutputStream("temp.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		
		ArrayList<String> imageList = new ArrayList<String>(); 
		// to store all image names and give image number
		
    	String strLine;
    	int lineNo = 0;
    	while ((strLine = br.readLine()) != null)   {
    		String[] tokens = strLine.split(" +");
    		
    		String imageName = tokens[0];
    		imageList.add(imageName);
    		
    		String[] keywords = Arrays.copyOfRange(tokens, 1, tokens.length);//remove the first token (file name)
    		keywords = stemTerms(keywords);
    		for(int j = 0; j < keywords.length; j ++){
    			writer.write(keywords[j]+"\t"+ lineNo+",");
    		}
    		lineNo ++;
    	}
    	fileList = new String[imageList.size()];
    	imageList.toArray(fileList);
    	br.close();
    	writer.close();
    }
    public static void sortTerms() throws Exception{
    	FileInputStream fistream = new FileInputStream("temp.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	String[] termdocs = br.readLine().split(",");
    	Arrays.sort(termdocs);
    	FileOutputStream fostream = new FileOutputStream("temp.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		for(int i = 0; i < termdocs.length; i ++){
			writer.write(termdocs[i]+"\n");
		}
		br.close();
		writer.close();
    }
    public static void buildLinkedList() throws Exception{
    	FileInputStream fistream = new FileInputStream("temp.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	
    	FileOutputStream fostream = new FileOutputStream("index.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
    	
    	int tf = 1;
    	String lastTerm = null;
    	LinkedList<Integer> fileNumList = new LinkedList<Integer>();
    	
    	String strLine;
    	while ((strLine = br.readLine()) != null)   {
    		String[] termAndFile = strLine.split("\t");
    		String currentTerm = termAndFile[0];
    		int fileNum = Integer.parseInt(termAndFile[1]);
    		if (lastTerm == null){
    			tf = 1;
    			lastTerm = currentTerm;
    			fileNumList.add(fileNum);
    		}
    		else if (currentTerm.equals(lastTerm)){
    			tf ++;
    			fileNumList.add(fileNum);
    		}
    		else {
    			Collections.sort(fileNumList);
    			writeLinkedList(lastTerm, tf, fileNumList, writer);
    			tf = 1;
    			fileNumList.clear();
    			fileNumList.add(fileNum);
    			lastTerm = currentTerm;
    		}
    		
    	}
    	br.close();
    	writer.close();
    }
    public static void writeLinkedList(String term, int frequency, LinkedList<Integer> fileNumList, Writer writer) throws Exception{
		String files = "";
		for (int i = 0; i < frequency; i ++){
			files += fileNumList.get(i)+" ";
		}
		writer.write(term + " " + frequency + "->" + files+"\n");
    }
    public static String[] stemTerms(String[] words){
    	String[] stemmed = new String[words.length];
    	Stemmer s = new Stemmer();
    	for (int i = 0; i < words.length; i ++){
    			stemmed[i] = s.stemmer(words[i]);
    	}
    	return stemmed;
    }
    public static void main(String[] args) throws Exception{
    	index();
    	search("dog");
    }
}
