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
	public static final String projectFolderPath = "C:\\Users\\rithel\\Desktop\\CS2108\\Assignment1\\";
	public static String[] fileList;
	public static ArrayList<String> categories;
	static HashMap<Integer, Double> searchResults;
	public static void main(String a[]) throws Exception{
		index();
		//search();
	
	}
	public HashMap<Integer,Double> search(String datasetpath, String queryImagePath, int resultsize) throws Exception{
		//write the query image path to searchlist.txt for image_classification.exe
		String listFilePath = datasetpath+"FeatureExtractor\\semanticFeature\\searchlist.txt";
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
    	searchResults = new HashMap<Integer,Double>();
    	int index = 0;
    	String[] qData = queryImageData.split(" ");
    	double[] queryScores = new double[qData.length];
    	for (int i = 0; i < qData.length; i ++){
    		queryScores[i] = Double.parseDouble(qData[i]);
    	}
    	while((strLine = br.readLine()) != null){
    		imageFileList.add(strLine.split(" ")[0]);
    		String[] dData = strLine.split(" ");
    		double[] datasetScores = new double[dData.length];
        	for (int i = 1; i < dData.length; i ++){
        		datasetScores[i] = Double.parseDouble(dData[i]);
        	}
    		searchResults.put(index,MeasureSimilarity.cosSimilarity(queryScores,datasetScores));
    		index ++;
    	}
    	br.close();
    	return searchResults;
//    	fileList = new String[imageFileList.size()];
//    	imageFileList.toArray(fileList);
//    	
//    	//rank the result docs, and return resultsize many of the docs
//    	ArrayList<Integer> result = getRankedDocs(searchResults);
//    	HashMap<String, Integer> countCategories = new HashMap<String, Integer>();
//    	BufferedImage[] imgs = new BufferedImage[resultsize];
//		for (int i = 0; i < resultsize; i ++){
//			String path = datasetpath+"ImageData\\train\\data\\"+fileList[result.get(i)];
//			File resultimage = new File(path);
//			String f = resultimage.getParentFile().getName();
//			if(countCategories.containsKey(f)){
//				countCategories.put(f, countCategories.get(f)+1);
//			}
//			else {
//				countCategories.put(f, 1);
//			}
//			imgs [i]=ImageIO.read(resultimage);
//		}
//		File queryImage = new File(queryImagePath);
//		System.out.println("Query Image Categories: "+queryImage.getParentFile().getName());
//		System.out.println("Search Result categories:");
//		for (String s:countCategories.keySet()){
//			System.out.println(s+":"+countCategories.get(s));
//		}
//    	return imgs;
    	
    }
	public HashMap<Integer, Double> getSearchResults(){
		return searchResults;
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
	public static ArrayList<Integer> getRankedDocs(HashMap<Integer, Double> searchResults){
		ArrayList<Integer> rankedDocs = new ArrayList<Integer>();
        Map<Integer, Double> sortedCrunchifyMapValue = new HashMap<Integer, Double>();
		
		// Sort Map on value by calling crunchifySortMap()
		sortedCrunchifyMapValue = crunchifySortMap(searchResults);
		for (Entry<Integer, Double> entry : sortedCrunchifyMapValue.entrySet()) {
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
		String params[] = new String[]{
				exe,filename
				};
	    ProcessBuilder pb = new ProcessBuilder(params);
		pb.directory(new File(projectFolderPath+"FeatureExtractor\\semanticFeature\\"));
		Process p = pb.start();
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
