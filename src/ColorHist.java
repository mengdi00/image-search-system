import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ColorHist {
	int dim = 64;
	HashMap<Integer,Double> searchResult = new HashMap<Integer,Double>();
	
	public HashMap<Integer,Double> search(String datasetpath, String queryImagePath, int resultsize) throws IOException{
    	File queryImage = new File(queryImagePath);
		BufferedImage bufferedimage = ImageIO.read(queryImage);
		double[] hist = getHist(bufferedimage);
    	
        ArrayList<File> datasetFiles = new ArrayList<File>();
		
    	FileInputStream fistream = new FileInputStream(datasetpath+"FeatureExtractor\\semanticFeature\\demolist.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream)); //read database
    	String strLine;
    	while((strLine = br.readLine()) != null){
    		File file = new File(strLine);
    		datasetFiles.add(file);
    	}
    	br.close();
		File [] files = new File[datasetFiles.size()];
		datasetFiles.toArray(files);
		double[] sims = new double [files.length];
		int [] indexes = new int [files.length];
		
		/*ranking the search results*/
		for (int count=0; count < files.length;count++){
			BufferedImage i = ImageIO.read(files[count]);
			double[] h = getHist(i);
			double sim = MeasureSimilarity.simBhattacharyyaDistance(hist, h);
			if (count == 0){
				sims[count] = sim;
				indexes [count] = count;
				searchResult.put(count, sim);
			}
			else {
				int index;
				searchResult.put(count, sim);
				for (index =0; index < count; index ++){
					if (sim > sims[index])
						break;
				}
				for (int j = count - 1; j > index - 1; j--){
					sims [j+1] = sims [j];
					indexes [j+1] = indexes[j];
				}
				sims[index] = sim;
				indexes[index] = count;
			}
		}
		   
		return searchResult;
//    	BufferedImage[] imgs = new BufferedImage[resultsize];
//    	HashMap<String,Integer> countCategories = new HashMap<String, Integer>();
//		for (int i=0; i<resultsize;i++){
//			String f = files[indexes[i]].getParentFile().getName();
//			if(countCategories.containsKey(f)){
//				countCategories.put(f, countCategories.get(f)+1);
//			}
//			else {
//				countCategories.put(f, 1);
//			}
//			
//			imgs [i]=ImageIO.read(files[indexes[i]]);
//		}
//		System.out.println("Query Image Categories: "+queryImage.getParentFile().getName());
//		System.out.println("Search Result categories:");
//		for (String s:countCategories.keySet()){
//			System.out.println(s+":"+countCategories.get(s));
//		}
//    	return imgs;
    }
	
	
	public double[] getHist(BufferedImage image) {
		int imHeight = image.getHeight();
        int imWidth = image.getWidth();
        double[] bins = new double[dim*dim*dim];
        int step = 256 / dim;
        Raster raster = image.getRaster();
        for(int i = 0; i < imWidth; i++)
        {
            for(int j = 0; j < imHeight; j++)
            {
            	// rgb->ycrcb
            	int r = raster.getSample(i,j,0);
            	int g = raster.getSample(i,j,1);
            	int b = raster.getSample(i,j,2);
            	
            	//Changed Codes. 
            	int y  = (int)( 0 + 0.299   * r + 0.587   * g + 0.114   * b);
        		int cb = (int)(128 -0.16874 * r - 0.33126 * g + 0.50000 * b);
        		int cr = (int)(128 + 0.50000 * r - 0.41869 * g - 0.08131 * b);
        		
        		int ybin = y / step;
        		int cbbin = cb / step;
        		int crbin = cr / step;

        		//Changed Codes. 
                bins[ybin*dim*dim+cbbin*dim+crbin] ++;
            }
        }
        
        //Changed Codes. 
        for(int i = 0; i < dim*dim*dim; i++) {
        	bins[i] = bins[i]/(imHeight*imWidth);
        }
        
        return bins;
	}
}