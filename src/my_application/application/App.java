package my_application.application;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import my_application.view.Environment;
import my_application.viewhelper.GameMode;


public class App {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame gameFrame = new JFrame();
			Environment environment = new Environment(GameMode.HUMAN);
			gameFrame.add(environment);
			gameFrame.setTitle("Snake");
			gameFrame.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/imgs/snake_icon.png").getImage());
			gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			gameFrame.setResizable(false);
			gameFrame.pack();
			gameFrame.setVisible(true);
			gameFrame.setLocationRelativeTo(null);
		});
	}
}
