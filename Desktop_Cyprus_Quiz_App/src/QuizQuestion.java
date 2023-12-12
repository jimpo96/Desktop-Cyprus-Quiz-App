//This class is essentially used to pass the questions along with their answers to the QuizClient class

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class QuizQuestion {

	String question;
	String imagePath;
	String optionA;
	String optionB;
	String optionC;
	String optionD;
	String answer;
	
	public QuizQuestion(String question, String imagePath, String optionA, String optionB, String optionC, String optionD, String answer) {
		this.question = question;
		this.imagePath = imagePath;
		this.optionA = optionA;
		this.optionB = optionB;
		this.optionC = optionC;
		this.optionD = optionD;
		this.answer = answer;
	}
	
}
