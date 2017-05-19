package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class GUI extends JFrame {
	
	public static final String WINDOW_TITLE = "Circle Editor";
	
	public static final int CANVAS_WIDTH = 800;
	public static final int CANVAS_HEIGHT = 500;
	
	private Preferences prefs;
	private static final String IMAGE_PATH = "imagepath";
	private static final String CSV_PATH = "csvpath";
	
	private CircleCanvas canvas;
	private JLabel toolLabel;
	
	public GUI() {
		super();
		setLayout(new BorderLayout());
		
		JMenuBar menuBar = createMenuBar();
		add(menuBar, BorderLayout.NORTH);
		
		canvas = new CircleCanvas(this);
		JScrollPane scroller = new JScrollPane(canvas);
		scroller.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		add(scroller, BorderLayout.CENTER);
		setTool(Tool.PAN);
		
		pack();
        setLocationRelativeTo(null);
		setVisible(true);
		
		prefs = Preferences.userNodeForPackage(GUI.class);
		refreshTitle(true);
		String impath = prefs.get(IMAGE_PATH, "");
		String csvpath = prefs.get(CSV_PATH, "");
		if (!impath.equals("")) loadImage(new File(impath));
		if (!csvpath.equals("")) loadCSV(new File(csvpath));
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createViewMenu());
		menuBar.add(createToolMenu());
		menuBar.add(Box.createHorizontalGlue());
		toolLabel = new JLabel();
		menuBar.add(toolLabel);
		return menuBar;
	}
	
	private void setTool(Tool tool) {
		canvas.setTool(tool);
		toolLabel.setText(tool + "  ");
	}
	
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		
		JMenuItem imageItem = new JMenuItem("Open image...");
		imageItem.setAccelerator(KeyStroke.getKeyStroke("control I"));
		imageItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseImage();
			}
		});
		menu.add(imageItem);
		
		JMenuItem csvItem = new JMenuItem("Open CSV...");
		csvItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
		csvItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseCSV();
			}
		});
		menu.add(csvItem);
		
		JMenuItem saveAs = new JMenuItem("Save as...");
		saveAs.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCSV(true);
			}
		});
		menu.add(saveAs);
		
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke("control S"));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCSV(false);
			}
		});
		menu.add(save);
		
		return menu;
	}
	
	private JMenu createViewMenu() {
		JMenu menu = new JMenu("View");
		
		JMenuItem pan = new JMenuItem(Tool.PAN.toString());
		pan.setAccelerator(KeyStroke.getKeyStroke('w'));
		pan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setTool(Tool.PAN);
			}
		});
		menu.add(pan);
		
		JMenuItem zoomin = new JMenuItem("Zoom in");
		zoomin.setAccelerator(KeyStroke.getKeyStroke('q'));
		zoomin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoomImage(2);
				setTool(Tool.PAN);
			}
		});
		menu.add(zoomin);
		
		JMenuItem zoomout = new JMenuItem("Zoom out");
		zoomout.setAccelerator(KeyStroke.getKeyStroke('e'));
		zoomout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomImage(0.5);
				setTool(Tool.PAN);
			}
		});
		menu.add(zoomout);
		
		return menu;
	}
	
	private JMenu createToolMenu() {
		JMenu menu = new JMenu("Tools");
		
		JMenuItem move = new JMenuItem(Tool.MOVE.toString());
		move.setAccelerator(KeyStroke.getKeyStroke('f'));
		move.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tool.MOVE);
			}
		});
		menu.add(move);
		
		JMenuItem resize = new JMenuItem(Tool.RESIZE.toString());
		resize.setAccelerator(KeyStroke.getKeyStroke('s'));
		resize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tool.RESIZE);
			}
		});
		menu.add(resize);
		
		JMenuItem add = new JMenuItem(Tool.ADD.toString());
		add.setAccelerator(KeyStroke.getKeyStroke('a'));
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tool.ADD);
			}
		});
		menu.add(add);
		
		JMenuItem remove = new JMenuItem(Tool.REMOVE.toString());
		remove.setAccelerator(KeyStroke.getKeyStroke('d'));
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTool(Tool.REMOVE);
			}
		});
		menu.add(remove);
		
		return menu;
	}
	
	private void chooseImage() {
		// user picks an image
		String path = prefs.get(IMAGE_PATH, ".");
		JFileChooser fileChooser = new JFileChooser(path);
		int returnVal = fileChooser.showOpenDialog(null);
		File f = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			f = fileChooser.getSelectedFile();
		}
		else return;
		
		loadImage(f);
	}
	
	private void loadImage(File f) {
		// load the image
		BufferedImage image = null;
		try {
			image = ImageIO.read(f);
			prefs.put(IMAGE_PATH, f.getAbsolutePath());
		} catch (IOException e) {
			return;
		}
		
		// display the image
		canvas.setPicture(image);
	}
	
	private void chooseCSV() {
		// user picks a CSV
		String path = prefs.get(CSV_PATH, ".");
		JFileChooser fileChooser = new JFileChooser(path);
		int returnVal = fileChooser.showOpenDialog(null);
		File f = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			f = fileChooser.getSelectedFile();
		}
		else return;
		
		// load and set circles to canvas
		loadCSV(f);
	}
	
	private void loadCSV(File f) {
		// load the CSV
		ArrayList<Circle> circles = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				if (items.length < 3) continue;
				try {
					int x = Integer.parseInt(items[0]);
					int y = Integer.parseInt(items[1]);
					int r = Integer.parseInt(items[2]);
					circles.add(new Circle(x, y, r));
				} catch (NumberFormatException nfe) {
					continue;
				}
			}
			reader.close();
			prefs.put(CSV_PATH, f.getAbsolutePath());
			refreshTitle(true);
		} catch (IOException e) {
			return;
		}
		
		// put on canvas
		canvas.setCircles(circles);
	}
	
	// askUser: true to create a jfilechooser, else just use
	// the last filepath chosen
	private void saveCSV(boolean askUser) {
		if (canvas.getCircles() == null) return;
		
		// user picks a CSV
		String path = prefs.get(CSV_PATH, ".");
		File f;
		if (askUser || path == ".") {
			JFileChooser fileChooser = new JFileChooser(path);
			fileChooser.setApproveButtonText("Save");
			int returnVal = fileChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				f = fileChooser.getSelectedFile();
			else return;
		}
		else f = new File(path);
		
		// write circles to file
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			writer.write("x,y,r\n");
			for (Circle circle : canvas.getCircles()) {
				writer.write((int)circle.cx + "," + (int)circle.cy + "," + 
						(int)circle.r + "\n");
			}
			writer.close();
			prefs.put(CSV_PATH, f.getAbsolutePath());
			refreshTitle(true);
		} catch (IOException e) {
			return;
		}
	}
	
	private void zoomImage(double zoom) {
		canvas.scaleImage(zoom);
	}
	
	public void refreshTitle(boolean saved) {
		String path = prefs.get(CSV_PATH, "");
		if (path.equals("")) setTitle(WINDOW_TITLE);
		else {
			String indicator = saved ? "" : "*";
			setTitle(WINDOW_TITLE + " - " + path + indicator);
		}
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
