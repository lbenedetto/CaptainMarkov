package CaptainMarkov.gui; 
 
import CaptainMarkov.generators.Generator; 
import javax.swing.*; 
import javax.swing.border.EmptyBorder; 
import java.awt.datatransfer.Clipboard; 
import java.awt.datatransfer.StringSelection; 
import java.beans.*; //property change stuff 
import java.awt.*; 
import java.awt.event.*; 
 
/* 1.4 example used by ChainBuilder.java. */ 
class CustomDialog extends JDialog implements ActionListener, PropertyChangeListener { 
  private ChainBuilder parent; 
  private JOptionPane optionPane; 
  private JTextArea output; 
  private Generator generator; 
  private String btnString1 = "Again"; 
  private String btnString2 = "Back"; 
 
  /** 
   * Creates the reusable dialog. 
   */ 
  public CustomDialog(Frame aFrame, Generator generator, ChainBuilder parent) { 
    super(aFrame, true); 
    this.parent = parent; 
    setTitle("Generating Phrases"); 
 
    //Create an array specifying the number of dialog buttons and their text. 
    Object[] options = {btnString1, btnString2}; 
    this.generator = generator; 
    //Create the JOptionPane. 
    optionPane = new JOptionPane("Generated phrase is displayed below and has been copied to the clipboard", 
        JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]); 
    output = new JTextArea(3, 50); 
    output.setLineWrap(true); 
    output.setText(this.generator.generate()); 
    copyToClipboard(output.getText()); 
    output.selectAll(); 
    output.setEditable(false); 
    output.setBorder(new EmptyBorder(10, 10, 10, 10)); 
    JScrollPane scrollPaneOutput = new JScrollPane(output); 
    scrollPaneOutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
    scrollPaneOutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
    //Make this dialog display it. 
    optionPane.add(scrollPaneOutput); 
    setContentPane(optionPane); 
 
    //Handle window closing correctly. 
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
    addWindowListener(new WindowAdapter() { 
      public void windowClosing(WindowEvent we) { 
          /* 
           * Instead of directly closing the window, 
                 * we're going to change the JOptionPane's 
                 * value property. 
                 */ 
        optionPane.setValue(JOptionPane.CLOSED_OPTION); 
      } 
    }); 
 
    //Register an event handler that reacts to option pane state changes. 
    optionPane.addPropertyChangeListener(this); 
  } 
 
  /** 
   * This method handles events for the text field. 
   */ 
  public void actionPerformed(ActionEvent e) { 
    optionPane.setValue(btnString1); 
  } 
 
  /** 
   * This method reacts to state changes in the option pane. 
   */ 
  public void propertyChange(PropertyChangeEvent e) { 
    String prop = e.getPropertyName(); 
 
    if (isVisible() && (e.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop) || 
        JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) { 
      Object value = optionPane.getValue(); 
 
      if (value == JOptionPane.UNINITIALIZED_VALUE) { 
        //ignore reset 
        return; 
      } 
 
      //Reset the JOptionPane's value. 
      //If you don't do this, then if the user 
      //presses the same button next time, no 
      //property change event will be fired. 
      optionPane.setValue( 
          JOptionPane.UNINITIALIZED_VALUE); 
 
      if (btnString1.equals(value)) { 
        if (generator == null) { 
          //Something fucked up 
          dispose(); 
        } else { 
          //TODO:Make this update on screen 
          output.setText(generator.generate()); 
          copyToClipboard(output.getText()); 
        } 
      } else { //user closed dialog or clicked cancel 
        parent.setLabel("Change settings and try again, or cancel"); 
        dispose(); 
      } 
    } 
  } 
  public static void copyToClipboard(String s){ 
    Toolkit toolkit = Toolkit.getDefaultToolkit(); 
    Clipboard clipboard = toolkit.getSystemClipboard(); 
    StringSelection strSel = new StringSelection(s); 
    clipboard.setContents(strSel, null); 
  } 
}