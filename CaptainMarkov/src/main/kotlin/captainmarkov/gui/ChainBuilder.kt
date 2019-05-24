package captainmarkov.gui

import captainmarkov.generators.*
import captainmarkov.generators.Annotation
import captainmarkov.utils.Series
import java.awt.BorderLayout
import java.awt.Component
import java.awt.GridLayout
import java.util.*
import javax.swing.*

class ChainBuilder
/**
 * Creates the GUI shown inside the frame's content pane.
 */
private constructor(// private ImageIcon icon = createImageIcon("images/middle.gif");
    private val frame: JFrame
) : JPanel(BorderLayout()) {
    private var label: JLabel
    private val customizationPanelDescription = "Mix and match shows: "
    private val presetPanelDescription = "Presets (more will be added)"
    private lateinit var customDialog: CustomDialog
    private var inputForCustom: TextForm? = null

    init {

        //Create the components.
        val customPanel = createCustomizationPanel()
        val presetPanel = createPresetPanel()
        label = JLabel("Click the \"Begin Generating\" button" + " to begin generating phrases", JLabel.CENTER)

        //Lay them out.
        val padding = BorderFactory.createEmptyBorder(20, 20, 5, 20)
        customPanel.border = padding
        presetPanel.border = padding

        val tabbedPane = JTabbedPane()
        tabbedPane.addTab("Build your own", null, customPanel, customizationPanelDescription) //tooltip text
        tabbedPane.addTab("Presets", null, presetPanel, presetPanelDescription) //tooltip text

        add(tabbedPane, BorderLayout.CENTER)
        add(label, BorderLayout.PAGE_END)
        label.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }

    /**
     * Sets the text displayed at the bottom of the frame.
     */
    fun setLabel(newText: String) {
        //TODO: Use this for displaying information when loading markov chain
        label.text = newText
    }

    /**
     * Creates the panel for customizing a generator
     */
    private fun createCustomizationPanel(): JPanel {

        val beginGenerateButton = JButton("Begin Generating")
        val checkBoxes = arrayOf(
            JCheckBox("(TOS) The Original Series"),
            JCheckBox("(TNG) The Next Generation"),
            JCheckBox("(DS9) Deep Space Nine"),
            JCheckBox("(VOY) Voyager"),
            JCheckBox("(ENT) Enterprise")
        )

        checkBoxes.forEach { it.isSelected = true }

        val shows = ArrayList<Series>()
        beginGenerateButton.addActionListener {
            if (checkBoxes[0].isSelected) shows.add(Series.TOS)
            if (checkBoxes[1].isSelected) shows.add(Series.TNG)
            if (checkBoxes[2].isSelected) shows.add(Series.DS9)
            if (checkBoxes[3].isSelected) shows.add(Series.VOY)
            if (checkBoxes[4].isSelected) shows.add(Series.ENT)

            val seed = inputForCustom!!.text
            setLabel("If this is first time running, it will take a long time to download the scripts")
            val keyPhrase = KeyPhrase(seed, true, shows)

            customDialog = CustomDialog(frame, keyPhrase, this)
            customDialog.pack()
            customDialog.isVisible = true
        }
        val box = JPanel()
        val label = JLabel(customizationPanelDescription)
        box.layout = BoxLayout(box, BoxLayout.PAGE_AXIS)
        box.add(label)
        for (checkBox in checkBoxes)
            box.add(checkBox)
        val pane = JPanel(BorderLayout())
        pane.add(box, BorderLayout.PAGE_START)
        inputForCustom = TextForm("Generator Seed: ", 20)
        pane.add(inputForCustom)
        pane.add(beginGenerateButton, BorderLayout.PAGE_END)
        return pane
    }

    /**
     * Creates the panel of preset generators
     */
    private fun createPresetPanel(): JPanel {
        val btnGenerator = ButtonWithTextForm(TextForm("Generator Seed: ", 20), JRadioButton("Captains Log"))
        val btnAnnotation = ButtonWithTextForm(JRadioButton("Annotation"))
        val btnCoherency = ButtonWithTextForm(TextForm("Coherency Threshold: ", 20), JRadioButton("Treknobabble (Experimental)"))
        val buttons = arrayOf(btnGenerator, btnAnnotation, btnCoherency)

        val group = ButtonGroup()
        val generateButton = JButton("Begin Generating")

        btnCoherency.toolTipText =
            "This is an experimental work in progress, \n " + "but it seems to be generating more interesting things than the other settings.\n"
        for (buttonWithForm in buttons) {
            group.add(buttonWithForm.button)
        }
        btnGenerator.button.isSelected = true
        generateButton.addActionListener {
            setLabel("If this is first time running, it will take a long time to download the scripts")
            var generator: Generator? = null
            when {
                btnGenerator.button.isSelected -> {
                    val seed = btnGenerator.form!!.text
                    generator = CaptainsLog(seed)
                }
                btnAnnotation.button.isSelected -> generator = Annotation()
                btnCoherency.button.isSelected -> generator = try {
                    val threshold = java.lang.Double.parseDouble(btnCoherency.form!!.text)
                    Treknobabble(threshold)
                } catch (nfe: NumberFormatException) {
                    setLabel("Must be a number representing the desired ratio of non-trek/trek words. Using default value of .1")
                    Treknobabble(.1)
                }
            }
            customDialog = CustomDialog(frame, generator, this)
            customDialog.pack()
            customDialog.isVisible = true
        }
        val box = JPanel()
        val label = JLabel("$presetPanelDescription:")
        box.layout = BoxLayout(box, BoxLayout.PAGE_AXIS)
        box.add(label)
        for (button in buttons)
            box.add(button)
        val pane = JPanel(BorderLayout())
        pane.add(box, BorderLayout.PAGE_START)
        pane.add(generateButton, BorderLayout.PAGE_END)
        return pane
    }

    /**
     * Custom text form comprised of a JPanel containing a JLabel and a JPanel
     * Also implements ActionListener so that pressing enter is the same as hitting run
     */
    private inner class TextForm// Create a form with the specified labels, tooltips, and sizes.
    internal constructor(label_: String, width: Int) : JPanel() {
        internal val fieldPanel: JPanel = JPanel(GridLayout(1, 2))
        val textField: JTextField
        internal val label: JLabel

        val text: String
            get() = textField.text

        init {
            //Create a JPanel with a 1x2 GridLayout
            add(fieldPanel)
            //Create a text field with specified parameters
            textField = JTextField(JTextField.RIGHT)
            textField.columns = width
            textField.alignmentX = Component.RIGHT_ALIGNMENT
            //Create a label for the field
            label = JLabel(label_, JLabel.LEFT)
            label.labelFor = textField
            label.alignmentX = Component.LEFT_ALIGNMENT
            //Add the label and field to the panel
            fieldPanel.add(label)
            fieldPanel.add(textField)
        }
    }

    private inner class ButtonWithTextForm : JRadioButton {
        private val buttonPanel: JPanel
        var form: TextForm? = null
        val button: JRadioButton
        val hasInputField: Boolean

        internal constructor(form: TextForm, button: JRadioButton) {
            //Create a JPanel with a 1x2 GridLayout
            buttonPanel = JPanel(GridLayout(1, 2))
            add(buttonPanel)
            this.form = form
            this.button = button
            buttonPanel.add(this.button)
            buttonPanel.add(this.form)
            hasInputField = true
        }

        internal constructor(button: JRadioButton) {
            //Create a JPanel with a 1x2 GridLayout
            buttonPanel = JPanel(GridLayout(1, 2))
            add(buttonPanel)
            this.button = button
            buttonPanel.add(this.button)
            hasInputField = false
        }
    }

    companion object {
        lateinit var instance : ChainBuilder

        @JvmStatic
        fun main(args: Array<String>) {
            //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
            SwingUtilities.invokeLater { createAndShowGUI() }
        }

        /**
         * Create the GUI and show it.  For thread safety,
         * this method should be invoked from the
         * event-dispatching thread.
         */
        private fun createAndShowGUI() {
            //Create and set up the window.
            val frame = JFrame("ChainBuilder v1.1")
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

            //Create and set up the content pane.
            val instance = ChainBuilder(frame)
            instance.isOpaque = true //content panes must be opaque
            frame.contentPane = instance

            //Display the window.
            frame.pack()
            frame.isVisible = true
        }

        /**
         * Returns an ImageIcon, or null if the path was invalid.
         */
        private fun createImageIcon(path: String): ImageIcon? {
            val imgURL = ChainBuilder::class.java.getResource(path)
            return if (imgURL != null) {
                ImageIcon(imgURL)
            } else {
                System.err.println("Couldn't find file: $path")
                null
            }
        }
    }
} 