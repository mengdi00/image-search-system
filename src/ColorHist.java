import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ColorHist {
	int dim = 64;

	
	public BufferedImage[] search(String datasetpath, BufferedImage bufferedimage, int resultsize) throws IOException{
    	double[] hist = getHist(bufferedimage);
    	
        ArrayList<File> datasetFiles = new ArrayList<File>();
		
    	FileInputStream fistream = new FileInputStream(datasetpath+"FeatureExtractor\\semanticFeature\\demolist.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
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
			double sim = MeasureSimilarity.cosSimilarity (hist, h);
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
		    	
    	BufferedImage[] imgs = new BufferedImage[resultsize];
		for (int i=0; i<resultsize;i++){
			imgs [i]=ImageIO.read(files[indexes[i]]);
		}
		
    	return imgs;
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