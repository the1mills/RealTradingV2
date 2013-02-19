package main;


import gui.MainFrame;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Kickoff {

	public static void main(String[] args) {

//		MainFrame mf = new MainFrame();

		 SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					MainFrame mf = new MainFrame();
					mf.setVisible(true);
				}
			});
		 
		// NewTradingEngine.testThisClass();
		
	}

	 static public void inform( final Component parent, final String str) {
	        if( SwingUtilities.isEventDispatchThread() ) {
	        	showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE);
	        }
	        else {
	            SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE);
					}
				});
	        }
	    }

	    static private void showMsg( Component parent, String str, int type) {    	
	        // this function pops up a dlg box displaying a message
	        JOptionPane.showMessageDialog( parent, str, "IB Java Test Client", type);
	    }
}
