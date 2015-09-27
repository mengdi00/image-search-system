import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;


public class TagsMatch {
	static String[] fileList;
	public static final String projectFolderPath = "C:\\Users\\rithel\\Desktop\\CS2108\\Assignment1\\";
	public BufferedImage[] search(String datasetpath, String queryImagePath, int resultsize) throws Exception{
		
		index();
		
		//go into the test folder and read test_tags.txt
		FileInputStream fistream = new FileInputStream(queryImagePath.substring(0, queryImagePath.indexOf("data"))+"test_tags.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	String strLine;
    	String sentence = null;
    	//if the file doesn't contain tags for query image, just return
    	//if it contains, then use the tags as keywords
    	while((strLine = br.readLine()) != null){
    		String filename = strLine.split(" ")[0];
    		if (queryImagePath.contains(filename)){
    			sentence = strLine;
    		}
    	}  
    	br.close();
    	if (sentence == null){
    		return null;
    	}
		String[] keywords = sentence.split(" +");
		keywords = stemmedKeywords(keywords);
		int N = fileList.length;		
		
		HashMap<Integer, Float> searchResults = new HashMap<Integer, Float>();//doc-score
		
		fistream = new FileInputStream("index.txt");
    	br = new BufferedReader(new InputStreamReader(fistream));
    	
		for (int i = 1; i < keywords.length; i ++){
			fistream.getChannel().position(0);//for each keyword, read from the beginning
			br = new BufferedReader(new InputStreamReader(fistream));
			while((strLine = br.readLine()) != null){
				String term = strLine.split(" ")[0];
				if (term.equals(keywords[i])) { // if it finds a match
					String[] docs = strLine.split("->")[1].split(" ");
					//assume tf=1 for all keyword in each file, use idf to represent the weight
					int df = Integer.parseInt(strLine.split("->")[0].split(" ")[1]);
					float idf = (float) (Math.log((float)N/df)+1);
					searchResults = mergeResult(docs,searchResults,idf);//save the doc numbers and add idf
					break; // stop reading and check the next query word
				}
			}
		}
		br.close();
		ArrayList<Integer> rankedDocs = getRankedDocs(searchResults); //rank docs by its idf
		
		String[] resultFiles = new String[rankedDocs.size()];
		for (int i = 0; i < rankedDocs.size(); i ++){
			resultFiles[i] = fileList[rankedDocs.get(i)];//get file names, doc num starts from 0
		}
		resultFiles = checkCategory(resultFiles);
		
		File queryImage = new File(queryImagePath);
		
		BufferedImage[] images = new BufferedImage[resultsize];
		HashMap<String, Integer> countCategories = new HashMap<String, Integer>();
		for (int i = 0; i < resultsize; i ++){
			File result = new File(resultFiles[i]);
			String f = result.getParentFile().getName();
			if(countCategories.containsKey(f)){
				countCategories.put(f, countCategories.get(f)+1);
			}
			else {
				countCategories.put(f, 1);
			}
			images[i] = ImageIO.read(result);
		}
		System.out.println("Query Image Categories: "+queryImage.getParentFile().getName());
		System.out.println("Search Result categories:");
		for (String s:countCategories.keySet()){
			System.out.println(s+":"+countCategories.get(s));
		}
		return images;
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> crunchifySortMap(final Map<K, V> mapToSort) {
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(mapToSort.size());
 
		entries.addAll(mapToSort.entrySet());
 
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
				return entry1.getValue().compareTo(entry2.getValue());
			}
		});
 
		Map<K, V> sortedCrunchifyMap = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : entries) {
			sortedCrunchifyMap.put(entry.getKey(), entry.getValue());
		}
		return sortedCrunchifyMap;
	}
	public static ArrayList<Integer> getRankedDocs(HashMap<Integer, Float> searchResults){
		ArrayList<Integer> rankedDocs = new ArrayList<Integer>();
        Map<Integer, Float> sortedCrunchifyMapValue = new HashMap<Integer, Float>();
		
		// Sort Map on value by calling crunchifySortMap()
		sortedCrunchifyMapValue = crunchifySortMap(searchResults);
		for (Entry<Integer, Float> entry : sortedCrunchifyMapValue.entrySet()) {
			rankedDocs.add(entry.getKey());
		}
		
	    Collections.reverse(rankedDocs); //decreasing order
	    return rankedDocs;
	    
	}
	public static String[] checkCategory(String[] filenames){
		String[] fullPathName = new String[filenames.length];
		String trainFolder = projectFolderPath+"ImageData\\train\\data\\";
		File file = new File(trainFolder);
		String[] directories = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
	    });
		for (int i = 0; i < filenames.length; i ++){
			for (int j = 0; j < directories.length; j ++){
			    boolean exist = new File(trainFolder+directories[j], filenames[i]).exists();
			    if (exist) {
			    	fullPathName[i]=trainFolder+directories[j]+"\\"+filenames[i];
			    }
		    }
		}
		return fullPathName;
	}
	public static String[] stemmedKeywords(String[] keywords){
		return stemTerms(keywords);
	}
	public static HashMap<Integer, Float> mergeResult(String[] docs, HashMap<Integer, Float> result, float idf){
		for (int i = 0; i < docs.length; i ++) {
			int num = Integer.parseInt(docs[i]);
			if (!result.containsKey(num)){
				result.put(num, idf);
			}
			else {
				result.put(num, result.get(num)+idf);
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
    	//search("car digital canon turkey lens");
    }
}
