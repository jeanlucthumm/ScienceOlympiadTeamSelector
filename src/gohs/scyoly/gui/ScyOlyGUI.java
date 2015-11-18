package gohs.scyoly.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class ScyOlyGUI extends JPanel implements ActionListener {

	private final Dimension SIZE = new Dimension(400, 400);

	final JFileChooser templateLocationChooser = new JFileChooser();
	final JFileChooser inputLocationChooser = new JFileChooser();
	protected JButton templateButton;
	protected JButton inputButton;

	public ScyOlyGUI() {
		templateButton = new JButton("Template Location");
		templateButton.addActionListener(this);

		inputButton = new JButton("Input Location");
		inputButton.addActionListener(this);

		this.setPreferredSize(SIZE);
		this.add(templateButton);
		this.add(inputButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == templateButton) {
			templateLocationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int fcReturnValue = templateLocationChooser.showOpenDialog(this);

			if (fcReturnValue == JFileChooser.APPROVE_OPTION) {
				File file = templateLocationChooser.getSelectedFile();

				System.out.println("Template Location: " + file); // DEBUG
			}
		} else if (e.getSource() == inputButton) {
			inputLocationChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			inputLocationChooser.setAcceptAllFileFilterUsed(false);
			inputLocationChooser.addChoosableFileFilter(new SpreadsheetFilter());
			
			if (inputLocationChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = inputLocationChooser.getSelectedFile();
				System.out.println("Input File: " + file);
			}
		}
	}

	private static void createAndShowGUI() {

		JFrame frame = new JFrame("Science Olympiad Team Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ScyOlyGUI gui = new ScyOlyGUI();
		gui.setOpaque(true);
		frame.setContentPane(gui);

		frame.setResizable(false);
		frame.setFocusable(true);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		createAndShowGUI();
	}
	
	private class SpreadsheetFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			// allows user to travel around the system
			if (f.isDirectory())
				return true;
			
			String ext = getExtension(f);
			if (ext != null) {
				if (ext.equals("xls"))
					return true;
				else
					return false;
			}
			
			return false;
		}

		@Override
		public String getDescription() {
			return "Excell 2003 (.xls)";
		}
		
		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			
			int i = s.lastIndexOf(".");
			
			if (i > 0 && i < s.length() - 1)
				ext = s.substring(i+1).toLowerCase();
			
			return ext;
		}
		
	}

}
