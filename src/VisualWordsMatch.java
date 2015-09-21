import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;


public class VisualWordsMatch {
	
	public static void main(String a[]) throws IOException{
	 
	//PythonInterpreter python = new PythonInterpreter();

	String pythonScriptPath = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\"
			+ "Bag-of-Visual-Words-Image\\Bag-of-Visual-Words-Image\\generate.py";
	String[] cmd = new String[5];
	cmd[0] = "C:\\tools\\python2-x86_32\\python";
	cmd[1] = pythonScriptPath;
	cmd[2] = "-c";
	cmd[3] = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\ImageData\\train\\data\\codebook_b.file";
	cmd[4] = "C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\ImageData\\test\\data\\bear\\0087_2173846805.jpg";
	
	Runtime rt = Runtime.getRuntime();
	Process pr = rt.exec(cmd);

	System.out.println("done");
	
	//python.execfile("C:\\Users\\zhang_000\\Desktop\\CS2108\\Assignment1\\Bag-of-Visual-Words-Image\\Bag-of-Visual-Words-Image\\learn.py");
	}

}
