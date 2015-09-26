import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ColorHist {
	int dim = 64;

	
	public BufferedImage[] search(String datasetpath, BufferedImage bufferedimage, int resultsize) throws IOException{
    	double[] hist = getHist(bufferedimage);
    	
    	ArrayList<File> datasetFiles = new ArrayList<File>();
    	File dir = new File(datasetpath); //path of the dataset
		String[] directories = dir.list(new FilenameFilter() {//all the subfolders
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
	    });
		for (int i = 0; i < directories.length; i ++){ //all the image files in each subfolders
			File folder = new File(datasetpath+directories[i]);
			File[] listOfFiles = folder.listFiles(); 
			 
			for (int j = 0; j < listOfFiles.length; j ++) {
				if (listOfFiles[j].getName().endsWith(".jpg")){
					File file = new File(listOfFiles[j].getAbsolutePath());
					datasetFiles.add(file);
				}
			}
		}		
	
		File [] files = new File[datasetFiles.size()];
		datasetFiles.toArray(files);
		double[] sims = new double [files.length];
		int [] indexes = new int [files.length];
		
		
		/*ranking the search results*/
		for (int count=0; count < files.length;count++){
			System.out.println(files[count]);
			try{
				BufferedImage i = ImageIO.read(files[count]);
				double[] h = getHist(i);
				double sim = computeSimilarity (hist, h);
				if (count == 0){
					sims[count] = sim;
					indexes [count] = count;
				}
				else {
					int index;
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
			catch(Exception e){
				System.out.println(e.getMessage());
			}	
		}
		    	
    	BufferedImage[] imgs = new BufferedImage[resultsize];
		for (int i=0; i<resultsize;i++){
			imgs [i]=ImageIO.read(files[indexes[i]]);
		}
		
    	return imgs;
    }
    
    public double computeSimilarity(double [] hist1, double [] hist2) {
		
		double distance = calculateDistance(hist1, hist2);
		return 1-distance;
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
	
	public double calculateDistance(double[] array1, double[] array2)
    {
		// Euclidean distance
        /*double Sum = 0.0;
        for(int i = 0; i < array1.length; i++) {
           Sum = Sum + Math.pow((array1[i]-array2[i]),2.0);
        }
        return Math.sqrt(Sum);
        */
        
        // Bhattacharyya distance
		double h1 = 0.0;
		double h2 = 0.0;
		int N = array1.length;
        for(int i = 0; i < N; i++) {
        	h1 = h1 + array1[i];
        	h2 = h2 + array2[i];
        }

        double Sum = 0.0;
        for(int i = 0; i < N; i++) {
           Sum = Sum + Math.sqrt(array1[i]*array2[i]);
        }
        double dist = Math.sqrt( 1 - Sum / Math.sqrt(h1*h2));
        return dist;
    }
}
