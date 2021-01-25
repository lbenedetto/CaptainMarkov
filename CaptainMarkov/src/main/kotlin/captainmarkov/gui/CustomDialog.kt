package captainmarkov.gui

import captainmarkov.generators.Generator
import java.awt.Frame
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.*
import javax.swing.border.EmptyBorder

/* 1.4 example used by ChainBuilder.java. */
internal class CustomDialog
/**
 * Creates the reusable dialog.
 */
    (aFrame: Frame, private val generator: Generator?, private val parent: ChainBuilder) : JDialog(aFrame, true),
    ActionListener, PropertyChangeListener {
    private val optionPane: JOptionPane
    private val output: JTextArea
    private val btnString1 = "Again"
    private val btnString2 = "Back"

    init {
        title = "Generating Phrases"

        //Create an array specifying the number of dialog buttons and their text.
        val options = arrayOf<Any>(btnString1, btnString2)
        //Create the JOptionPane.
        optionPane = JOptionPane(
            "Generated phrase is displayed below and has been copied to the clipboard",
            JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]
        )
        output = JTextArea(3, 50)
        output.lineWrap = true
        output.text = this.generator!!.generate()
        copyToClipboard(output.text)
        output.selectAll()
        output.isEditable = false
        output.border = EmptyBorder(10, 10, 10, 10)
        val scrollPaneOutput = JScrollPane(output)
        scrollPaneOutput.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPaneOutput.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        //Make this dialog display it.
        optionPane.add(scrollPaneOutput)
        contentPane = optionPane

        //Handle window closing correctly.
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(we: WindowEvent?) {
                optionPane.value = JOptionPane.CLOSED_OPTION
            }
        })

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this)
        parent.setLabel("Generating...")
    }

    /**
     * This method handles events for the text field.
     */
    override fun actionPerformed(e: ActionEvent) {
        optionPane.value = btnString1
    }

    /**
     * This method reacts to state changes in the option pane.
     */
    override fun propertyChange(e: PropertyChangeEvent) {
        val prop = e.propertyName

        if (isVisible && e.source === optionPane && (JOptionPane.VALUE_PROPERTY == prop || JOptionPane.INPUT_VALUE_PROPERTY == prop)) {
            val value = optionPane.value

            if (value === JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return
            }
            optionPane.value = JOptionPane.UNINITIALIZED_VALUE
            if (btnString1 == value) {
                if (generator == null) {
                    //Something fucked up
                    dispose()
                } else {
                    output.text = generator.generate()
                    copyToClipboard(output.text)
                }
            } else { //user closed dialog or clicked cancel
                parent.setLabel("Change settings and try again, or cancel")
                dispose()
            }
        }
    }

    companion object {

        fun copyToClipboard(s: String) {
            val toolkit = Toolkit.getDefaultToolkit()
            val clipboard = toolkit.systemClipboard
            val strSel = StringSelection(s)
            clipboard.setContents(strSel, null)
        }
    }
}