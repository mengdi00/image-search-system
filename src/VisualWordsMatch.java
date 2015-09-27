import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import javax.imageio.ImageIO;

import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;



public class VisualWordsMatch {
	
	public static final String projectFolderPath = "C:\\Users\\rithel\\Desktop\\CS2108\\Assignment1\\";
	public static String[] fileList;
	public static ArrayList<String> categories;
	public static String SIFTResultPath = "C:\\Users\\Rithel\\Desktop\\CS2108\\Assignment1\\Bag-of-Visual-Words-Image";
	
	public static void SIFTGenerator(String QPath) throws IOException{
		
		createImageListFile();
		
		ProcessBuilder pb = new ProcessBuilder(
				"C:\\tools\\python2-x86_32\\python",
				"C:\\Users\\Rithel\\Desktop\\CS2108\\Assignment1\\Bag-of-Visual-Words-Image\\generate.py",
				"-c",
				"C:\\Users\\Rithel\\Desktop\\CS2108\\Assignment1\\ImageData\\train\\data\\codebook_b.file",
				QPath);

		pb.directory(new File(SIFTResultPath));

		Process process = pb.start();		
	}
	
	
	public BufferedImage[] search(String datasetpath, String queryPath, int resultsize) throws Exception{		
		//exe alr puts results in queryimagename.txt in its folder, read the result
		SIFTGenerator(queryPath);
		FileInputStream fistream = new FileInputStream(SIFTResultPath + "\\visual_words_for_test_data");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	String[] temp = br.readLine().split(" ");
    	double[] QueryData = new double[temp.length];
    	for(int i=0; i<temp.length; i++){
    		QueryData[i] = Double.parseDouble(temp[i].split(":")[1]);
    	}
    	br.close();
    	
    	//read indexVC.txt and get similarities
    	fistream = new FileInputStream("indexVW.txt");
    	br = new BufferedReader(new InputStreamReader(fistream));
    	ArrayList<String> imageFileList = new ArrayList<String>();
    	String strLine = "";
    	double[] Data = new double[temp.length];
    	HashMap<Integer, Double> similarityHashMap = new HashMap<Integer,Double>();
    	int index = 0;
    	while((strLine = br.readLine()) != null){
    		imageFileList.add(strLine.split(" ")[0]);
    		Data[index++] = Double.parseDouble(strLine.split(" ")[index].split(":")[1]);
    		similarityHashMap.put(index,MeasureSimilarity.cosSimilarity(QueryData,Data));
    	}
    	br.close();
    	
    	fileList = new String[imageFileList.size()];
    	imageFileList.toArray(fileList);
    	
    	//rank the result docs, and return resultsize many of the docs
    	ArrayList<Integer> result = getRankedDocs(similarityHashMap);
    	HashMap<String, Integer> countCategories = new HashMap<String, Integer>();
    	BufferedImage[] imgs = new BufferedImage[resultsize];
		for (int i = 0; i < resultsize; i ++){
			String path = projectFolderPath+"ImageData\\train\\data\\"+fileList[result.get(i)];
			File resultimage = new File(path);
			String f = resultimage.getParentFile().getName();
			if(countCategories.containsKey(f)){
				countCategories.put(f, countCategories.get(f)+1);
			}
			else {
				countCategories.put(f, 1);
			}
			imgs [i]=ImageIO.read(new File(path));
		}
		File queryImage = new File(queryPath);
		System.out.println("Query Image Categories: "+queryImage.getParentFile().getName());
		System.out.println("Search Result categories:");
		for (String s:countCategories.keySet()){
			System.out.println(s+":"+countCategories.get(s));
		}
    	return imgs;
    	
    }
	
	public static double cosSimilarity(String query, String datasetImage){
		String[] queryScores = query.split(" ");
		String[] dsImageScores = datasetImage.split(" ");
		double lengthOfQ = 0, lengthOfD = 0;
		double dotproduct = 0;
		for (int i = 0; i < queryScores.length; i ++){
			double q = Double.parseDouble(queryScores[i]);
			double d = Double.parseDouble(dsImageScores[i + 1]);
			lengthOfQ += q*q;
			lengthOfD += d*d;
			dotproduct += q*d;
		}
		double dist = dotproduct/(double)Math.sqrt(lengthOfQ*lengthOfD);
		return dist;
	}
	
	public static ArrayList<Integer> getRankedDocs(HashMap<Integer, Double> similarityHashMap){
		ArrayList<Integer> rankedDocs = new ArrayList<Integer>();
        Map<Integer, Double> sortedCrunchifyMapValue = new HashMap<Integer, Double>();
		
		// Sort Map on value by calling crunchifySortMap()
		sortedCrunchifyMapValue = crunchifySortMap(similarityHashMap);
		for (Entry<Integer, Double> entry : sortedCrunchifyMapValue.entrySet()) {
			rankedDocs.add(entry.getKey());
		}
		
	    Collections.reverse(rankedDocs); //decreasing order

	    return rankedDocs;
	    
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
	
	public static void indexAllFileResults() throws Exception{
		FileOutputStream fostream = new FileOutputStream("indexVW.txt");    		  
		Writer writer = new BufferedWriter(new OutputStreamWriter(fostream, "utf-8"));
		FileInputStream fistream = new FileInputStream("visual_words_for_training_data");
		BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
		
		for (int i = 0; i < categories.size(); i++){
  		  	File dir = new File(projectFolderPath+"ImageData\\train\\data\\"+categories.get(i));
  		  	for (File file : dir.listFiles()) {
  		  		String strLine;
  				if(file.getName().endsWith(".jpg"))
  					writer.write(categories.get(i)+"\\"+file.getName()+" ");
  				while((strLine = br.readLine()) != null){
  					String[] splitedStrLine = strLine.split(" ");
  					for(int j=0; j<splitedStrLine.length; j++){
  						double data = Double.parseDouble(splitedStrLine[j].split(":")[1]);
  						writer.write(data+" ");
  					}
  					writer.write("\n");
  					break;
  				}
  		  	}
  		}
		br.close();
		writer.close();	
	}
	
	public static void createImageListFile() throws IOException{
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

	}
}

