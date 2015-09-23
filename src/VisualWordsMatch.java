import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;



public class VisualWordsMatch {
	
	public static void main(String a[]) throws IOException{
		
		String queryPath = "";
		SIFTGenerator();
		SolveQuery(queryPath);
		outputResult();
	
	}
	
	public static void SIFTGenerator() throws IOException{

		ProcessBuilder pb = new ProcessBuilder("D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Assignment1\\FeatureExtractor\\semanticFeature\\image_classification.exe","D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Assignment1\\FeatureExtractor\\semanticFeature\\demolist.txt");

		pb.directory(new File("D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Assignment1\\FeatureExtractor\\semanticFeature"));

		Process process = pb.start();

//		PythonInterpreter py = new PythonInterpreter();
//		String ABPath1 = "D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Assignment1\\ImageData\\train\\data\\codebook_b.file";
//		//String MDPath1 = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\ImageData\\train\\data\\codebook_b.file";
//		String ABPath2 = "D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Assignment1\\ImageData\\test\\data\\bear\\0018_375723120.jpg";
//		//String MDPath2 = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\ImageData\\test\\data\\bear\\0087_2173846805.jpg";
//
//		//String MDpythonScriptPath = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\"
//		//		+ "Bag-of-Visual-Words-Image\\Bag-of-Visual-Words-Image\\generate.py";
//		String ABpythonScriptPath = "D:\\lab\\singapore\\course\\Y4semester1\\CS2108\\Assignments\\Assignment1\\Bag-of-Visual-Words-Image";
//		String[] cmd = new String[5];
//		cmd[0] = "C:\\tools\\python2-x86_32\\python";
//		cmd[1] = ABpythonScriptPath + "\\generate.py";
//		cmd[2] = "-c";
//		cmd[3] = ABPath1;
//		cmd[4] = ABPath2;
//		
//		String pyCommand = "";
//		
//		for(int i=0; i<5; i++){
//			pyCommand += cmd[i] + " ";
//		}
//
//		File f = new File(ABpythonScriptPath);
//		System.out.println(f.getAbsolutePath());
//		//System.out.println(pyCommand);
//		//py.exec(pyCommand);
//		//py.close();
//		
//		Runtime.getRuntime().exec(cmd);
//		
		System.out.println("done");
		
		
		//python.execfile("C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\Bag-of-Visual-Words-Image\\Bag-of-Visual-Words-Image\\learn.py");
	}
	
	public static void SolveQuery(String queryPath) {
	}
	
	public static void outputResult(){
		
	}
}
