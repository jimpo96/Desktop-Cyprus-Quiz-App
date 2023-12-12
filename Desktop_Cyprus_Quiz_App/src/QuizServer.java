//This is the server class that is being used to check the correct answers and calculate the grade accordingly

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

public class QuizServer extends JFrame implements Runnable {
	
	private static final int WIDTH = 1450;
	private static final int HEIGHT = 1450;
	
	private static final int PORT_NUMBER = 8080;
	
	JTextArea textArea = null;
	List<QuizQuestion> questions;
	
//	This QuizServer is used to establish a connection with QuizClient to make it a synchronous process
	
	public QuizServer() {
		super("Quiz Server");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createMenu();
		this.initQuestions();
		
		textArea = new JTextArea(45,45);
		textArea.setEditable(false);
		this.add(textArea);
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
//	Here, I have implemented the connection with the QuizClient for the "file" and "exit" buttons
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
//	This function will be used to implement the questions along with the choices, their answers, and the picture below the question
	
	private void initQuestions() {
		questions = new ArrayList();
		questions.add(new QuizQuestion("Which submerged ship is shown in the picture?", "q1.jpeg", "Kyrenia ship", "Georgios Grivas ship", "EDRO III (Known as Oniro By The Sea)", "Zenobia Wreck", "C"));
		questions.add(new QuizQuestion("Where is the Faneromenis church located?", "q2.jpeg", "Christian Orthodox church located in the middle of the old city of Nicosia", "Christian Orthodox church found in Larnaca-Cyprus", "The Cathedral of Nicosia", "Saint Savvas", "A"));
		questions.add(new QuizQuestion("Which Christian Orthodox monastery is shown in the picture?", "q3.jpeg","Stavrovouni Monastery", "The Kykkos Monastery on the Troodos mountains", "The Monastery of Saint Stefanos", "The Maheras Monastery", "B"));
		questions.add(new QuizQuestion("Where is Sandy beach located?", "q4.jpeg", "Limassol", "Paphos", "Larnaca", "Pachyammos - Tylliria area of Cyprus ", "D"));
		questions.add(new QuizQuestion("What is the capital of Cyprus?", "q5.jpeg", "Limassol", "Paphos", "Larnaca", "Nicosia ", "D"));
		questions.add(new QuizQuestion("What is the name of the beach that is considered the birthplace of Aphrodite, the goddess of love and beauty?", "q6.jpeg", "Govern Beach", "Paleokastritsa", "Petra tou Romiou", "Makronisos", "C"));
		questions.add(new QuizQuestion("Which libary is shown in the picture?", "q7.jpeg", "The Nicosia Library", "The Severios Library", "The Larnaca Library", "The Paphos Library", "B"));
		questions.add(new QuizQuestion("Which street is it?", "q8.jpeg", "Ledra Street", "Ermou Street", "Saint Antoniou Street", "Anexartisias Street", "A"));
		questions.add(new QuizQuestion("Which entrance of museum is shown in the picture?", "q9.jpeg", "Leventis Municipal Museum of Nicosia", "Museum of the National Struggle", "Cyprus Museum", "Cyprus Classic Motorcycle Museum", "C"));
		questions.add(new QuizQuestion("Which Castle is shown in the picture?", "q10.jpeg", "Limassol Castle", "Kantara Castle", "Kolossi Castle", "Paphos Castle", "D"));
		questions.add(new QuizQuestion("Can you guess how 'Cyprus' is written in the Greek language?", "q11.jpeg", "Κύπρος", "Λάος", "Σεϋχέλλες", "Μαυρίκιος", "A"));
		questions.add(new QuizQuestion("What is the name of the cheese in the image??", "q12.jpeg", "Feta", "Blue Cheese", "Asiago", "Halloumi", "D"));
		questions.add(new QuizQuestion("Can you guess how 'Hi' is written in the Greek language?", "q13.jpeg", "Τυρί", "Πάω", "Γειά σου", "Αυγό", "C"));
		questions.add(new QuizQuestion("Can you guess how 'Yes' is written in the Greek language?", "q14.jpeg", "Οχι(όχι)", "Ναί", "Γάτα", "Νερό", "B"));
		questions.add(new QuizQuestion("Can you guess how 'No' is written in the Greek language?", "q15.jpeg", "Οχι(όχι)", "Ναί", "Σκύλος", "Σχολείο", "A"));

		Collections.shuffle(questions);
	}
	
	public static void main(String[] args) {
		QuizServer quizServer = new QuizServer();
		quizServer.setVisible(true);
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);//The server starts from here
			Socket socket = serverSocket.accept();
			textArea.append("Player has entered the game. Setting up network...\n");
			
			DataInputStream fromClient = new DataInputStream(socket.getInputStream());
			DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
			
//			Questions are displayed one by one on the desktop player
			
			int questionNo = 1;
			for (QuizQuestion quizQuestion: questions) {
				
//				 There is more questions
				
				toClient.writeBoolean(true);
				
				textArea.append("Sending question number " + questionNo + "\n");
				
//				 Question
		
				toClient.writeUTF(quizQuestion.question);
				toClient.flush();
				
//				 Image
				
				Path imagePath = Paths.get(quizQuestion.imagePath);
				byte[] imageBytes = Files.readAllBytes(imagePath);
				toClient.writeInt(imageBytes.length);
				toClient.flush();
				toClient.write(imageBytes);
				toClient.flush();
				
//				 Options
				
				toClient.writeUTF(quizQuestion.optionA);
				toClient.flush();
				toClient.writeUTF(quizQuestion.optionB);
				toClient.flush();
				toClient.writeUTF(quizQuestion.optionC);
				toClient.flush();
				toClient.writeUTF(quizQuestion.optionD);
				toClient.flush();
				
//				 Correct Answer
				
				toClient.writeUTF(quizQuestion.answer);
				toClient.flush();
								
//				 Should go next
				
				boolean shouldGoNext = fromClient.readBoolean();
				textArea.append("User has answered question number " + questionNo++ + "\n");

			}
			
//			 No more questions
			
			toClient.writeBoolean(false);
			textArea.append("Player has completed the quiz.\n");
			
			
		} catch(Exception e) {
			textArea.append(e.getMessage() + "\n");
		}
		
	}

}
