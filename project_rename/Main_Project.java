/*
実験的にJavaSE11環境を想定して構築を行う
 */
package project_rename;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main_Project {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				JFrame renmFrm = new RenameFrameGUI();
				renmFrm.setVisible(true);
			}
		});
	}
}
