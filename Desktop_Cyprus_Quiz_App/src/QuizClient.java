//This is the quizClient class, which is essentially used to implement all the logic related to the quiz and grading

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

//This quizClient function extends the properties from JFrame and implements the Runnable class, which is built into Java
//That is being used to perform multiple tasks

public class QuizClient extends JFrame implements Runnable  {
	private static final int WIDTH = 1450;
	private static final int HEIGHT = 1450;
	private static final int HEIGHT_IMAGE = 400;
	
//	Networking: Here, I am specifying the port number and the host to run my application
	
	private static final String HOST = "localhost";
	private static final int PORT_NUMBER = 8080;
	Socket socket = null;
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
//	UI Components: Here, I am defining all the variables related to the user perspective
	
    private JLabel questionLabel;
    private ImageIcon imageIcon;
    private JLabel imageLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton submitButton;
    private JPanel optionsPanel;
    
//  UI State
    
    private String correctAnswer;
    private int score;
	
//  In QuizClient, I am starting my application with the default value, which is passed into the QuizClient constructor.
    
	public QuizClient() {
		super("Cyprus Quiz");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.createMenu();
		this.correctAnswer = null;
		this.score = 0;
	
		setHomeScreen();
		Thread thread = new Thread(this);
		thread.start();
	}
	
//	This is the homePanel, which extends the properties from the JPanel class and 
//	performs many tasks that we can see within the class itself.
	
	public class HomePanel extends JPanel {
	    private Image backgroundImage;

	    public HomePanel(ActionListener startGameListener) {
	        backgroundImage = new ImageIcon("home_background.jpeg").getImage();

	        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	        setOpaque(false); 

//	       From here, my quiz application is starting
	        
	        JLabel welcomeLabel = new JLabel("Welcome to the Cyprus Quiz !!!");
	        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
	        welcomeLabel.setForeground(Color.BLACK);
	        
//	        These lines serve as the "remote control" for running my application
	        
	        JLabel instructionsLabel = new JLabel("Click below to explore Cyprus");
	        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        instructionsLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
	        instructionsLabel.setForeground(Color.BLACK);
	        
	        ImageIcon forwardIcon = new ImageIcon(new ImageIcon("forward_arrow_icon.jpeg").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
            JButton startButton = new JButton("Start",forwardIcon);
	        startButton.setIcon(forwardIcon);
	        startButton.setIcon(forwardIcon);
	        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	        startButton.addActionListener(startGameListener);
	        startButton.setFont(new Font("Times New Roman", Font.BOLD, 22));
	        startButton.setForeground(Color.BLACK);
	        Color customColor = new Color(0,128,0); 
	        startButton.setBackground(customColor);

	        startButton.setOpaque(true);
	        
	        JLabel reservedLabel = new JLabel("Created by Dimitrios Kaoutzanis @copyright reserved 2023");
	        reservedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	        reservedLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
	        reservedLabel.setForeground(Color.BLACK);
	        
	        add(welcomeLabel);
	        add(Box.createRigidArea(new Dimension(0, 20)));
	        add(instructionsLabel);
	        add(Box.createRigidArea(new Dimension(0, 20)));
	        add(startButton);
	        add(Box.createRigidArea(new Dimension(0, 20)));
	        add(Box.createVerticalGlue());
	        add(reservedLabel);
	        add(Box.createRigidArea(new Dimension(0, 20)));
	    }

//	This function is used to set the background with a user-defined size
	    
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        if (backgroundImage != null) {
	            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
	        }
	    }
	}
	
//	This function is used to set the home screen with user-defined parameters
	
	private void setHomeScreen() {
        this.add(new HomePanel(new StartGameListener()));
	}
	
//	This function is used to clear components with user-defined parameters
	
	private void clearComponents() {
	    this.getContentPane().removeAll();
	    this.revalidate();
	    this.repaint();
	}

//  In this QuizComponent, 
//	I am passing every value to the corresponding function to set their values according to their specific needs
	
	private void setQuizComponents(String question, ImageIcon imageIcon, String optionAText, String optionBText, String optionCText, String optionDText) {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
//		Question
		
        questionLabel = new JLabel(question);
        questionLabel.setForeground(new Color(72, 0, 255));
        questionLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
        JPanel questionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        questionPanel.add(questionLabel);
        questionLabel.setSize(300, 200);
        
//      Image
        int originalWidth = imageIcon.getIconWidth();
        int originalHeight = imageIcon.getIconHeight();
        double ratio = (double) originalWidth / originalHeight;
        int newWidth = (int) (HEIGHT_IMAGE * ratio);
        Image resizedImage = imageIcon.getImage().getScaledInstance(newWidth, HEIGHT_IMAGE, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(resizedImage);
        imageLabel = new JLabel(imageIcon);
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.add(imageLabel);

        
        optionA = new JRadioButton(optionAText);
        optionB = new JRadioButton(optionBText);
        optionC = new JRadioButton(optionCText);
        optionD = new JRadioButton(optionDText);

        Color optionColor = new Color(255, 192, 0);  // Set the color you want here
        optionA.setFont(new Font("Times New Roman", Font.BOLD, 22));
        optionB.setFont(new Font("Times New Roman", Font.BOLD, 22));
        optionC.setFont(new Font("Times New Roman", Font.BOLD, 22));
        optionD.setFont(new Font("Times New Roman", Font.BOLD, 22));
        optionA.setForeground(optionColor);
        optionB.setForeground(optionColor);
        optionC.setForeground(optionColor);
        optionD.setForeground(optionColor);
        
        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.add(optionA);
        radioPanel.add(optionB);
        radioPanel.add(optionC);
        radioPanel.add(optionD);
        optionsPanel.add(radioPanel);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        Color submitButtonColor = new Color(255, 0, 174);  // You can change these RGB values
        submitButton.setFont(new Font("Times New Roman", Font.BOLD, 22));
        submitButton.setBackground(submitButtonColor);
        submitButton.setOpaque(true);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        
//      Update addComponents to use these panels
        this.add(questionPanel);
        this.add(imagePanel);
        this.add(optionsPanel);
        this.add(buttonPanel);
        
        this.revalidate();
	    this.repaint();
    }
	
//	In the displayScore class,it shows the user their grade when they have completed the Cyprus Quiz
	
	private void displayScore() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

	    class ImagePanel extends JPanel {
	        private Image bgImage;

	        public ImagePanel(ImageIcon imageIcon) {
	            this.bgImage = imageIcon.getImage();
	        }

	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
	        }
	    }

	    ImageIcon backgroundIcon = new ImageIcon("score_background.jpeg");
	    JPanel scorePanel = new ImagePanel(backgroundIcon);
	    scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    JLabel scoreLabel = new JLabel("Your score is " + score + "/15 🏆");
	    scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    scoreLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
	    scoreLabel.setForeground(Color.WHITE);
	    scorePanel.add(scoreLabel);
	    
	    this.add(scorePanel);

	    this.revalidate();
	    this.repaint();
	}

//	Here, I am creating the menu to access the file and exit button
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
//	Here, every action listener is implemented
	
	class StartGameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				socket = new Socket(HOST, PORT_NUMBER);
				fromServer = new DataInputStream(socket.getInputStream());
				toServer = new DataOutputStream(socket.getOutputStream());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(QuizClient.this, 
				        "Unable to connect to the main QuizServer.\n "
				        +"Please be patient and try again later.Thank you for your understanding.", 
				        "⚠️Connection Error", 
				        JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}
	
	class SubmitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!optionA.isSelected() && !optionB.isSelected() && !optionC.isSelected() && !optionD.isSelected()) {
				JOptionPane.showMessageDialog(QuizClient.this, 
				        "You have not selected any options🧠️️", 
				        "⚠️Select your answer of choice", 
				        JOptionPane.ERROR_MESSAGE);
				return;
			}
			boolean isACorrect = optionA.isSelected() && correctAnswer.equals("A");
			boolean isBCorrect = optionB.isSelected() && correctAnswer.equals("B");
			boolean isCCorrect = optionC.isSelected() && correctAnswer.equals("C");
			boolean isDCorrect = optionD.isSelected() && correctAnswer.equals("D");
			if (isACorrect || isBCorrect || isCCorrect || isDCorrect) {
				score++;
				JOptionPane.showMessageDialog(QuizClient.this, 
				        "Well done, you choose the correct answer 😊",
				        "✅ Your score is " + score, 
				        JOptionPane.INFORMATION_MESSAGE);
			} else {
				score+=0;
				JOptionPane.showMessageDialog(QuizClient.this, 
				        "Sorry, you choose the wrong answer 😞", 
				        "❌ Your score is " + score, 
				        JOptionPane.INFORMATION_MESSAGE);
			}
			
			
			try {
				toServer.writeBoolean(true);
				toServer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	
	
	
	public static void main(String[] args) {
		QuizClient quizClient = new QuizClient();
		quizClient.setVisible(true);
	}

//	This run function is used to interact with other necessary classes
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(300);
				while (socket != null && fromServer != null && toServer != null) {
					
//					 Is there more questions?
					
					boolean moreQuestions = fromServer.readBoolean();
					if (!moreQuestions) {
						SwingUtilities.invokeLater(() -> {
							clearComponents();
							displayScore();
						});
					}
					
//					 Question
					
					String question = fromServer.readUTF();
					
//					 Image
					
					int imageSize = fromServer.readInt();
					byte[] imageBytes = new byte[imageSize];
					fromServer.readFully(imageBytes);
					ImageIcon imageIcon = new ImageIcon(imageBytes);
					
//					 Options
					
					String optionAText = fromServer.readUTF();
					String optionBText = fromServer.readUTF();
					String optionCText = fromServer.readUTF();
					String optionDText = fromServer.readUTF();
					
					this.correctAnswer = fromServer.readUTF();
					
					SwingUtilities.invokeLater(() -> {
						clearComponents();
						setQuizComponents(question, imageIcon, optionAText, optionBText, optionCText, optionDText);
					});
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
