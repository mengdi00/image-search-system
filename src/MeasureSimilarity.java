import org.math.array.LinearAlgebra;

public class MeasureSimilarity {
    public static double cosSimilarity(double[] queryScores, double[] datasetScores){
		
		double lengthOfQ = 0, lengthOfD = 0;
		double dotproduct = 0;
		for (int i = 0; i < queryScores.length; i ++){
			double q = queryScores[i];
			double d = datasetScores[i];
			lengthOfQ += q*q;
			lengthOfD += d*d;
			dotproduct += q*d;
		}
		double sim = dotproduct/(double)Math.sqrt(lengthOfQ*lengthOfD);
		return sim;
	}
    
    public static double simBhattacharyyaDistance(double[] array1, double[] array2)
    {        
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
        return 1 - dist;
    }
    public static double simManhattanDistance(double[] array1, double[] array2){
    	double Sum = 0.0;
    	double[] d = {findMax(array1),findMax(array2)};
    	double max = findMax(d);
        for(int i = 0; i < array1.length; i++) {
           Sum = Sum + Math.abs((array1[i]-array2[i])/(double)max);
        }
        return 1 - Sum;
    }
    public static double findMax(double[] array){
    	double max = 0;
    	for (double a : array){
    		if (a > max){
    			max = a;
    		}
    	}
    	return max;
    }
}
