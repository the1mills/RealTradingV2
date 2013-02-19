package gui;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ErrorTextAreaFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea jta = new JTextArea();
	
	public ErrorTextAreaFrame(){
		
		this.setContentPane(jta);
		this.setSize(500,500);
		this.setVisible(true);
	}
	
	
	public void writeToErrorLog(String errorMsg){
		jta.append(errorMsg);
		jta.append("");
		jta.repaint();
	}

}
