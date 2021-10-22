import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.*;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WordGui {
	private static void constructGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		MyJFrame frame = new MyJFrame();		
		frame.setVisible(true);
		
	}
	
public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				constructGUI();
			}
		});
		

	}
	
}
	
	

class MyJFrame extends JFrame {
	public JLabel word;
	public JLabel answer;
	public JTextField userInput;
	
	
	

	public MyJFrame() {
		super();
		init();
	}

	private void init() {
		userInput = new JTextField();		
		JButton btn1 = new JButton("Submit");
		btn1.addActionListener(new MyButtonListener(this));		
		word = new JLabel("Enter a word to be counted");
		answer = new JLabel("Answer: ");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Word Count");
		this.setLayout(new GridLayout(2, 2));		
		this.add(word);		
		this.add(userInput);
		this.add(btn1);
		this.add(answer);		
		this.pack();
		this.setVisible(true);

		

	}
}

class MyButtonListener implements ActionListener {
	MyJFrame fr;
	public MyButtonListener(MyJFrame frame)
	{
		fr = frame;
	}

	public void actionPerformed(ActionEvent e) 
	{
		JButton btn = (JButton) e.getSource();		
		String userWord = fr.userInput.getText();
				
		try {
			Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();
			
			Elements title = doc.getElementsByAttribute("h1");
			Elements author = doc.getElementsByClass("no-break");
			Elements poem = doc.getElementsByClass("chapter");
			
			try {			
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
			
			int mostFrequentlyUsed = 0;
			String theWord = null;
			String clientInput = userWord.toLowerCase();
			
						
			for(String word : frequency.keySet()) {
				Integer theVal = frequency.get(word);
				if(clientInput.equals(word)) {
					theWord = word;
					mostFrequentlyUsed = theVal;
					fr.answer.setText("The word " + clientInput + " was used " + mostFrequentlyUsed + " times");
										
				}
				
				
			}
									
			if(mostFrequentlyUsed == 0)
				fr.answer.setText("The word was never found");		
								
									
									
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}
