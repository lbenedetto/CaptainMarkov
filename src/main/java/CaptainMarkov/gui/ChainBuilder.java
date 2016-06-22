package CaptainMarkov.gui;

import CaptainMarkov.generators.Annotation;
import CaptainMarkov.generators.CaptainsLog;
import CaptainMarkov.generators.Generator;
import CaptainMarkov.generators.KeyPhrase;
import CaptainMarkov.utils.Series;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class ChainBuilder extends JPanel {
	public JLabel label;
	// private ImageIcon icon = createImageIcon("images/middle.gif");
	private JFrame frame;
	private String customizationPanelDescription = "Mix and match shows: ";
	private String presetPanelDescription = "Presets (more will be added)";
	private CustomDialog customDialog;
	public static ChainBuilder THIS;
	private TextForm inputForCustom;
	private TextForm inputForPreset;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(ChainBuilder::createAndShowGUI);
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("ChainBuilder");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		ChainBuilder newContentPane = new ChainBuilder(frame);
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Creates the GUI shown inside the frame's content pane.
	 */
	private ChainBuilder(JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
		THIS = this;

		//Create the components.
		JPanel customPanel = createCustomizationPanel();
		JPanel presetPanel = createPresetPanel();
		label = new JLabel("Click the \"Begin Generating\" button" + " to begin generating phrases", JLabel.CENTER);

		//Lay them out.
		Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
		customPanel.setBorder(padding);
		presetPanel.setBorder(padding);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Build your own", null, customPanel, customizationPanelDescription); //tooltip text
		tabbedPane.addTab("Presets", null, presetPanel, presetPanelDescription); //tooltip text

		add(tabbedPane, BorderLayout.CENTER);
		add(label, BorderLayout.PAGE_END);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Sets the text displayed at the bottom of the frame.
	 */
	public void setLabel(String newText) {
		//TODO: Use this for displaying information when loading markov chain
		label.setText(newText);
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	private static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ChainBuilder.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Creates the panel for customizing a generator
	 */
	private JPanel createCustomizationPanel() {
		final int numButtons = 5;
		JCheckBox[] checkBoxes = new JCheckBox[numButtons];
		JButton beginGenerateButton;
		checkBoxes[0] = new JCheckBox("(TOS) The Original Series");
		checkBoxes[1] = new JCheckBox("(TNG) The Next Generation");
		checkBoxes[2] = new JCheckBox("(DS9) Deep Space Nine");
		checkBoxes[3] = new JCheckBox("(VOY) Voyager");
		checkBoxes[4] = new JCheckBox("(ENT) Enterprise");
		for (JCheckBox jcb : checkBoxes) {
			jcb.setSelected(true);
		}
		ArrayList<Series> shows = new ArrayList<>();
		beginGenerateButton = new JButton("Begin Generating");
		beginGenerateButton.addActionListener(e -> {
			if (checkBoxes[0].isSelected()) shows.add(Series.TOS);
			if (checkBoxes[1].isSelected()) shows.add(Series.TNG);
			if (checkBoxes[2].isSelected()) shows.add(Series.DS9);
			if (checkBoxes[3].isSelected()) shows.add(Series.VOY);
			if (checkBoxes[4].isSelected()) shows.add(Series.ENT);

			String seed = inputForCustom.getText();
			KeyPhrase keyPhrase = new KeyPhrase(seed, true, shows);

			customDialog = new CustomDialog(frame, keyPhrase, THIS);
			customDialog.pack();
			customDialog.setVisible(true);
		});
		JPanel box = new JPanel();
		JLabel label = new JLabel(customizationPanelDescription + ":");
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);
		for (JCheckBox checkBox : checkBoxes)
			box.add(checkBox);
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(box, BorderLayout.PAGE_START);
		inputForCustom = new TextForm("Generator Seed: ", 20);
		pane.add(inputForCustom);
		pane.add(beginGenerateButton, BorderLayout.PAGE_END);
		return pane;
	}

	/**
	 * Creates the panel of preset generators
	 */
	private JPanel createPresetPanel() {
		final int numButtons = 2;
		JRadioButton[] radioButtons = new JRadioButton[numButtons];
		final ButtonGroup group = new ButtonGroup();
		JButton generateButton;
		radioButtons[0] = new JRadioButton("Captains Log");
		radioButtons[1] = new JRadioButton("Annotation");
		for (JRadioButton jrb : radioButtons) {
			group.add(jrb);
		}
		radioButtons[0].setSelected(true);
		generateButton = new JButton("Begin Generating");
		generateButton.addActionListener(e -> {
			Generator generator = null;
			if (radioButtons[0].isSelected()) {
				String seed = inputForPreset.getText();
				generator = new CaptainsLog(seed);
			} else if (radioButtons[1].isSelected()) {
				generator = new Annotation();
			}
			customDialog = new CustomDialog(frame, generator, THIS);
			customDialog.pack();
			customDialog.setVisible(true);
		});
		JPanel box = new JPanel();
		JLabel label = new JLabel(presetPanelDescription + ":");
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);
		for (JRadioButton radioButton : radioButtons)
			box.add(radioButton);
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(box, BorderLayout.PAGE_START);
		inputForPreset = new TextForm("Generator Seed: ", 20);
		pane.add(inputForPreset);
		pane.add(generateButton, BorderLayout.PAGE_END);
		return pane;
	}

	/**
	 * Custom text form comprised of a JPanel containing a JLabel and a JPanel
	 * Also implements ActionListener so that pressing enter is the same as hitting run
	 */
	private class TextForm extends JPanel {
		final JPanel fieldPanel;
		public final JTextField field;
		final JLabel label;

		// Create a form with the specified labels, tooltips, and sizes.
		public TextForm(String label_, int width) {
			//Create a JPanel with a 1x2 GridLayout
			fieldPanel = new JPanel(new GridLayout(1, 2));
			add(fieldPanel);
			//Create a text field with specified parameters
			field = new JTextField(JTextField.RIGHT);
			field.setColumns(width);
			field.setAlignmentX(Component.RIGHT_ALIGNMENT);
			//Create a label for the field
			label = new JLabel(label_, JLabel.LEFT);
			label.setLabelFor(field);
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			//Add the label and field to the panel
			fieldPanel.add(label);
			fieldPanel.add(field);
		}

		public String getText() {
			return field.getText();
		}
	}
} 