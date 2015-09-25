import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;


public class VisualConceptMatch {
	public static final String projectFolderPath = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\";
	public static String[] fileList;
	public static ArrayList<String> categories;
	public static void main(String a[]) throws Exception{
		index();
		//search();
	
	}
	public BufferedImage[] search(String datasetpath, String queryImagePath, int resultsize) throws Exception{
		//write the query image path to searchlist.txt for image_classification.exe
		String listFilePath = projectFolderPath+"FeatureExtractor\\semanticFeature\\searchlist.txt";
		FileOutputStream fostream = new FileOutputStream(listFilePath);    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		writer.write(queryImagePath);
		writer.close();
		
		runClassificationExe(listFilePath);
		
		//exe alr puts results in queryimagename.txt in its folder, read the result
		FileInputStream fistream = new FileInputStream(queryImagePath.replace("jpg","txt"));
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	String queryImageData = br.readLine();
    	br.close();
    	
    	//read indexVC.txt and get similarities
    	fistream = new FileInputStream("indexVC.txt");
    	br = new BufferedReader(new InputStreamReader(fistream));
    	ArrayList<String> imageFileList = new ArrayList<String>();
    	String strLine;
    	HashMap<Integer,Float> similarityHashMap = new HashMap<Integer,Float>();
    	int index = 0;
    	while((strLine = br.readLine()) != null){
    		imageFileList.add(strLine.split(" ")[0]);
    		similarityHashMap.put(index,cosSimilarity(queryImageData,strLine));
    		index ++;
    	}
    	br.close();
    	
    	fileList = new String[imageFileList.size()];
    	imageFileList.toArray(fileList);
    	
    	//rank the result docs, and return resultsize many of the docs
    	ArrayList<Integer> result = getRankedDocs(similarityHashMap);
    	BufferedImage[] imgs = new BufferedImage[resultsize];
		for (int i = 0; i < resultsize; i ++){
			String path = projectFolderPath+"ImageData\\train\\data\\"+fileList[result.get(i)];
			imgs [i]=ImageIO.read(new File(path));
		}
    	return imgs;
    	
    }
	public static float cosSimilarity(String query, String datasetImage){
		String[] queryScores = query.split(" ");
		String[] dsImageScores = datasetImage.split(" ");
		float lengthOfQ = 0, lengthOfD = 0;
		float dotproduct = 0;
		for (int i = 0; i < queryScores.length; i ++){
			float q = Float.parseFloat(queryScores[i]);
			float d = Float.parseFloat(dsImageScores[i + 1]);
			lengthOfQ += q*q;
			lengthOfD += d*d;
			dotproduct += q*d;
		}
		float dist = dotproduct/(float)Math.sqrt(lengthOfQ*lengthOfD);
		return dist;
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
	public static void index() throws Exception{
		String imageListFile = createImageListFile();
		runClassificationExe(imageListFile);
		indexAllFileResults();
	}
	public static String createImageListFile() throws IOException{
		ArrayList<String> filenames = new ArrayList<String>();
		FileInputStream fistream = new FileInputStream(projectFolderPath+"ImageData\\category_names.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	String strLine;
    	
    	categories = new ArrayList<String>();
    	while((strLine = br.readLine()) != null){
    		categories.add(strLine);
    	}
    	br.close();
    	for (int i = 0; i < categories.size(); i ++){
    		  File dir = new File(projectFolderPath+"ImageData\\train\\data\\"+categories.get(i));
    		  for (File file : dir.listFiles()) {
    		    if (file.getName().endsWith((".jpg"))) {
    		      filenames.add(categories.get(i)+"\\"+file.getName());
    		    }
    		  }
    	}
    	FileOutputStream fostream = new FileOutputStream(projectFolderPath+"FeatureExtractor\\semanticFeature\\demolist.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		for (int i = 0; i < filenames.size(); i ++){
			writer.write(projectFolderPath+"ImageData\\train\\data\\"+filenames.get(i)+"\n");
		}
		writer.close();
		
		return projectFolderPath+"FeatureExtractor\\semanticFeature\\demolist.txt";	
	}
	//run the exe file and output in console
	public static void runClassificationExe(String filename) throws Exception{
		String exe = projectFolderPath+"FeatureExtractor\\semanticFeature\\image_classification.exe";
		try {
		    String params[] = new String[]{
		        exe,filename
		    };
		    ProcessBuilder pb = new ProcessBuilder(params);
		    pb.directory(new File(projectFolderPath+"FeatureExtractor\\semanticFeature\\"));
		    pb.redirectErrorStream();
		    Process p = pb.start();
		    InputStream is = p.getInputStream();
		    int in = -1;
		    while ((in = is.read()) != -1) {
		        System.out.print((char) in);
		    }
		    is = p.getErrorStream();
		    in = -1;
		    while ((in = is.read()) != -1) {
		        System.out.print((char) in);
		    }
		    System.out.println("p exited with " + p.exitValue());
		} catch (IOException ex) {
		    System.out.println("hello");
		}
	}
	//put classification result for each doc in one file for indexing
	public static void indexAllFileResults() throws Exception{
		FileOutputStream fostream = new FileOutputStream("indexVC.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		for (int i = 0; i < categories.size(); i ++){
  		  File dir = new File(projectFolderPath+"ImageData\\train\\data\\"+categories.get(i));
  		  for (File file : dir.listFiles()) {
  		    if (file.getName().endsWith((".txt"))) {
  		    	FileInputStream fistream = new FileInputStream(file);
  		    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
  		    	String strLine;
  		    	while((strLine = br.readLine()) != null){
  		    		writer.write(categories.get(i)+"\\"+file.getName().replace("txt", "jpg")+" "+strLine+"\n");
  		    	}
  		    	br.close();
  		    }
  		  }
  		}
		writer.close();
		
	}
	
}
