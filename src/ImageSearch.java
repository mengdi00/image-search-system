import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

/*path of the dataset, and the size of search result could be changed here*/


public class ImageSearch extends JFrame
                              implements ActionListener {
    JFileChooser fc;
	JPanel contentPane;
	JPanel checkBoxPanel = new JPanel(new GridLayout(0,2));
	JPanel QIP = new JPanel();
	
	static int resultsize = 20;    //size of the searching result
	String datasetpath = "C:\\Users\\rithel\\Desktop\\CS2108\\Assignment1\\"; //the path of image dataset
    ColorHist colorhist = new ColorHist();
	VisualConceptMatch vc = new VisualConceptMatch();
	VisualWordsMatch vw = new VisualWordsMatch();
	TagsMatch text = new TagsMatch();
    JButton openButton, searchButton;
    JCheckBox CHButton, VWButton, VCButton, TextButton;
	BufferedImage bufferedimage;
	HashMap<Integer,Double> finalResult = new HashMap<Integer,Double>();
    double VWWeight,VCWeight,CHWeight,TextWeight;
	JLabel [] imageLabels = new JLabel [ resultsize ];
	
	File file = null;


    public ImageSearch() throws IOException {
        
        openButton = new JButton("Select an image...",
                createImageIcon("images/Open16.gif"));
        openButton.addActionListener(this);
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        
        CHButton = new JCheckBox("Color Histogram Match");
        CHButton.addActionListener(this);
        
        VWButton = new JCheckBox("Visual Keywords Match");
        VWButton.addActionListener(this);
        
        VCButton = new JCheckBox("Visual Concept Match");
        VCButton.addActionListener(this);
        
        TextButton = new JCheckBox("Text Tags Match");
        TextButton.addActionListener(this);


        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //use FlowLayout
        buttonPanel.add(openButton);
        
        checkBoxPanel.add(CHButton);

        checkBoxPanel.add(VWButton);

        checkBoxPanel.add(VCButton);

        checkBoxPanel.add(TextButton);
        
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalStrut(80));
        buttonPanel.add(checkBoxPanel,BorderLayout.PAGE_END);
        
		
        
    	JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0,5));
        
        
        for (int i = 0; i<imageLabels.length;i++){
        	imageLabels[i] = new JLabel();
        	imageLabels[i].setSize(5, 5);
        	imagePanel.add(imageLabels[i]);
        }

		contentPane = (JPanel)this.getContentPane();
		setSize(700,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        contentPane.add(buttonPanel,BorderLayout.SOUTH);
        contentPane.add(imagePanel, BorderLayout.CENTER);
        //contentPane.add(checkBoxPanel,BorderLayout.NORTH);
        
        contentPane.setVisible(true);
		setVisible(true);
//        add(logScrollPane, BorderLayout.CENTER);
        
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ImageSearch.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        //Set up the file chooser.
        if (e.getSource() == openButton) {
        if (fc == null) {
            fc = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);

	    //Add custom icons for file types.
            fc.setFileView(new ImageFileView());

	    //Add the preview pane.
            fc.setAccessory(new ImagePreview(fc));
        } 
        

        //Show it.
        int returnVal = fc.showDialog(ImageSearch.this,
                                      "Select an image..");

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            JLabel QI = new JLabel();
    		try {
				QI.setIcon(new ImageIcon(ImageIO.read(new File(file.getAbsolutePath()))));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            QIP.add(QI,BorderLayout.BEFORE_FIRST_LINE);
        }

        fc.setSelectedFile(null);
        }else if (e.getSource() == searchButton) {
        	VWWeight = 0.2;
            VCWeight = 0.3;
            CHWeight = 0.1;
            TextWeight = 0.4;
        	BufferedImage [] imgs = null;
        	if(VWButton.isSelected()){
        		try {
        			imgs = vw.search(datasetpath, file.getAbsolutePath(), resultsize);
        		} catch (Exception e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        		}
        		HashMap<Integer,Double> VWMap = VisualWordsMatch.getSearchResults();
        		for(int i=0; i<VWMap.size(); i++){
        			finalResult.put(i, (finalResult.get(i)+(VWMap.get(i)*VWWeight)));
        		}
        		
        	}if(VCButton.isSelected()){
        		try {
					imgs = vc.search(datasetpath, file.getAbsolutePath(), resultsize);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		HashMap<Integer,Double> VCMap = vc.getSearchResults();
        		for(int i=0; i<VCMap.size(); i++){
        			finalResult.put(i, finalResult.get(i)+(VCMap.get(i)*VCWeight));
        		}
        		
        	}if(CHButton.isSelected()){
        		try {
        			imgs = colorhist.search (datasetpath, file.getAbsolutePath(), resultsize);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		HashMap<Integer,Double> CHMap = colorhist.getSearchResults();
        		System.out.println(CHMap.size());
        		for(int i=0; i<CHMap.size(); i++){
        			finalResult.put(i, finalResult.get(i)+(CHMap.get(i)*CHWeight));
        		}
        		
        	}if(TextButton.isSelected()){
        		try {
        			imgs = text.search (datasetpath, file.getAbsolutePath(), resultsize);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		HashMap<Integer,Double> TextMap = text.getSearchResults();
        		for(int i=0; i<TextMap.size(); i++){
        			finalResult.put(i, finalResult.get(i)+(TextMap.get(i)*TextWeight));
        		}
        	}
        	
        	if(!TextButton.isSelected() && !CHButton.isSelected()
        			&& !VCButton.isSelected() && !VWButton.isSelected()){
        		System.out.println("Please Tell Me What To Do");
        	}
        	
        	ArrayList<Integer> result = getRankedDocs(finalResult);

        	try {
				imgs = returnResult(result);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
			for(int i = 0; i<imageLabels.length;i++)
				imageLabels[i].setIcon(new ImageIcon(imgs[i]));
				
        }
    }

    public static void main(String[] args) throws IOException {
		ImageSearch example = new ImageSearch();
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
    
    public BufferedImage[] returnResult(ArrayList<Integer> result) throws IOException{
    	BufferedImage[] imgs = new BufferedImage[resultsize];
    	FileInputStream fistream = new FileInputStream("image_indexes_train");
    	BufferedReader br = new BufferedReader(new InputStreamReader(fistream));
    	ArrayList<String> Database = new ArrayList<String>();
    	String strLine = "";
    	while((strLine = br.readLine()) != null){
    		Database.add(strLine);
    	}
    	br.close();
    	
    	for(int i=0; i<resultsize; i++){
    		int FileNo = result.get(i);
    		String Path = Database.get(FileNo);
    		imgs[i] = ImageIO.read(new File(Path));
    	}
    	return imgs;
    }
}
