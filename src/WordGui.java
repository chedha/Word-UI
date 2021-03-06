import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.*;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.sql.*;

/**
 * 
 * @author Chris Hall
 * This class creates a thread that runs the constructGUI method.
 * constructGUI builds a JFrame using the MyJFrame class.
 * 
 */
public class WordGui {
	private static void constructGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		MyJFrame frame = new MyJFrame();
		frame.setVisible(true);

	}
	
	/**
	 * Initializing thread to build the frame.
	 * @param args Main method
	 */

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				constructGUI();
			}
		});

	}

}

/**
 * 
 * @author Chris Hall
 * This class is used to build a JFrame. The frame
 * will collect data from the user, and use it to
 * scrape a web page to search for the user input. 
 * 
 *
 */

class MyJFrame extends JFrame {
	public JLabel word;
	public JLabel answer;
	public JTextField userInput;
	public JTextArea textArea;
	public JLabel wordFreq;

	public MyJFrame() {
		super();
		init();
	}

	private void init() {

		userInput = new JTextField();
		JButton btn1 = new JButton("Submit");
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		btn1.addActionListener(new MyButtonListener(this));
		word = new JLabel("Enter a word to be counted: ");
		answer = new JLabel("Answer: ");
		wordFreq = new JLabel("Top twenty words found are: ");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Word Count");
		this.setLayout(new GridLayout(3, 2, 10, 10));
		this.setSize(800, 500);
		this.add(word);
		this.add(userInput);
		this.add(btn1);
		this.add(answer);
		this.add(wordFreq);
		this.add(textArea);
		this.pack();
		this.setVisible(true);

	}
}

/**
 * 
 * @author Chris Hall
 * This class collects data from the user.
 * It grabs input from the userInput textfield
 * when the submit button is clicked.
 *
 */

class MyButtonListener implements ActionListener {
	MyJFrame fr;

	// Passing in the frame.

	public MyButtonListener(MyJFrame frame) {
		fr = frame;
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		String userWord = fr.userInput.getText();

		try {
			
			// Performing web page scrape.
			
			Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

			Elements title = doc.getElementsByAttribute("h1");
			Elements author = doc.getElementsByClass("no-break");
			Elements poem = doc.getElementsByClass("chapter");

			try {
				
				// Adding web page text to a text file.
				
				FileOutputStream fileStream = null;
				PrintWriter outFS = null;
				fileStream = new FileOutputStream("RavenPoem.txt");

				outFS = new PrintWriter(fileStream);
				outFS.println(title.text());
				outFS.println(author.text());
				outFS.println(poem.text());
				outFS.close();

				FileReader file = new FileReader("RavenPoem.txt");
				BufferedReader reader = new BufferedReader(file);

				Map<String, Integer> frequency = new HashMap<>();
				
				// Begin reading each line of the text file.

				String line = reader.readLine();

				while (line != null) {

					// Processing each line and splitting to separate words
					// then storing those words into array

					if (!line.trim().equals("")) {
						String[] words = line.split(" ");

						for (String word : words) {

							if (word == null || word.trim().equals("")) {
								continue;
							}
							String processed = word.toLowerCase();

							// Removing special characters

							processed = processed.replaceAll("[^a-zA-Z0-9]", "");

							// searching for current word in keyset
							// if word is found, incrementing the integer value

							if (frequency.containsKey(processed)) {
								frequency.put(processed, frequency.get(processed) + 1);
							} else {
								frequency.put(processed, 1);

							}

						}

					}

					line = reader.readLine();

				}
				
				// Outputs to a JLabel how many times a word was found in the document
				
				

				int mostFrequentlyUsed = 0;
				String clientInput = userWord.toLowerCase();

				for (String word : frequency.keySet()) {
					String theWord = null;
					Integer theVal = frequency.get(word);
					if (clientInput.equals(word)) {
						theWord = word;
						mostFrequentlyUsed = theVal;
						fr.answer.setText("The word \"" + clientInput + "\" was used " + mostFrequentlyUsed + " times");

					}

				}
				if (mostFrequentlyUsed == 0)
					fr.answer.setText("The word \"" + clientInput + "\" was not found in the document");

				TreeMap<String, Integer> sorted = new TreeMap<>(frequency);
				Set<Entry<String, Integer>> mappings = sorted.entrySet();

				List<Entry<String, Integer>> listOfEntries = new ArrayList<Entry<String, Integer>>(mappings);

				Collections.sort(listOfEntries, Collections.reverseOrder(valueComparator));

				LinkedHashMap<String, Integer> sortedbyValue = new LinkedHashMap<String, Integer>(listOfEntries.size());

				for (Entry<String, Integer> entry : listOfEntries) {

					sortedbyValue.put(entry.getKey(), entry.getValue());

				}
				
				// Creates and inserts words counts into a table. Reads and outputs top 20 words to a table.
				
				try {
					wordDatabase.createTable();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			
				
				wordDatabase.insertToTable(listOfEntries);
				
				String result = wordDatabase.queryTable();
				
				fr.textArea.setText(result);
				
				// Outputs the top twenty words found in the document to a JTextArea hardcoded.

//				String topTwenty = "";
//				int count = 0;
//				for (Entry<String, Integer> word : listOfEntries) {
//
//					topTwenty += word.getKey() + ", ";
//					count++;
//					if (count >= 20)
//						break;
//
//				}
//
//				fr.textArea.setText(topTwenty);

				Set<Entry<String, Integer>> entrySetSortedByValue = sortedbyValue.entrySet();

				List<Entry<String, Integer>> sortedListOfEntries = new ArrayList<Entry<String, Integer>>(
						entrySetSortedByValue);

			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	/**
	 * Comparator override to sort the list of entries by value
	 * @return object value comparison
	 */

	static Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String, Integer>>() {

		@Override
		public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
			Integer v1 = e1.getValue();
			Integer v2 = e2.getValue();
			return v1.compareTo(v2);

		}
	};
	

	

	



                                
	
	
	

}
