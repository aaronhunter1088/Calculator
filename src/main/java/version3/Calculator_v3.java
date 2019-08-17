package version3;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class Calculator_v3 extends JFrame {
	
	final public static String BASIC = "Basic";
    final public static String PROGRAMMER = "Programmer";
    final public static String SCIENTIFIC = "Scientific";
    final public static String DATE = "Date";
    final public static String CALCULATOR = "Calculator";
	
	final static protected Logger LOGGER = LogManager.getLogger(Calculator_v3.class);
	final static private long serialVersionUID = 1L;
	protected GridBagLayout layout; // layout of the calculator
    protected GridBagConstraints constraints; // layout's constraints
	// All calculators have:
	
	final protected NumberButtonHandler buttonHandler = new NumberButtonHandler();
	final protected JButton button0 = new JButton("0");
    final protected JButton button1 = new JButton("1");
    final protected JButton button2 = new JButton("2");
    final protected JButton button3 = new JButton("3");
    final protected JButton button4 = new JButton("4");
    final protected JButton button5 = new JButton("5");
    final protected JButton button6 = new JButton("6");
    final protected JButton button7 = new JButton("7");
    final protected JButton button8 = new JButton("8");
    final protected JButton button9 = new JButton("9");
    final protected ClearButtonHandler clearButtonHandler = new ClearButtonHandler();
    final protected JButton buttonClear = new JButton("C");
    final protected ClearEntryButtonHandler CEButtonHandler = new ClearEntryButtonHandler();
    final protected JButton buttonClearEntry = new JButton("CE");
    //final String delete = "\u2190";
    final protected DeleteButtonHandler delButtonHandler = new DeleteButtonHandler();
    final protected JButton buttonDelete = new JButton("DEL");
    final protected DotButtonHandler dotButtonHandler = new DotButtonHandler();
    final protected JButton buttonDot = new JButton(".");
    
    final protected static Font font = new Font("Segoe UI", Font.PLAIN, 12);
    protected String[] values = {"","",""}; // firstNum (total), secondNum, copy/paste. memory values now in MemorySuite.getMemoryValues()
    protected String[] memoryValues = new String[]{"","","","","","","","","",""}; // stores memory values; rolls over after 10 entries
    protected int valuesPosition = 0;
    protected int memoryPosition = 0;
    protected ImageIcon calculatorImage1, calculator2, macLogo;
    protected JLabel iconLabel;
    protected JLabel textLabel;
    protected JTextArea textArea = new JTextArea(2,5); // rows, columns
    protected String textarea = "";
    
	protected boolean firstNumBool = true, numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false;
	protected boolean memorySwitchBool = false;
    protected boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false, negatePressed = false;
	protected boolean dotButtonPressed = false;
	protected boolean dotActive = false;

	public Calculator_v3() throws HeadlessException {
		super();
	}

	public Calculator_v3(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public Calculator_v3(String title) throws HeadlessException {
		super(title);
		layout = new GridBagLayout();
        setLayout(layout); // set frame layout
        constraints = new GridBagConstraints(); // instanitate constraints
		setupButtonsAndSuch();
		setupMenuBar();
		setMinimumSize(new Dimension(100,200));
	}

	public Calculator_v3(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}
	
	public void setupButtonsAndSuch() {
		LOGGER.info("Inside setupButtonsAndSuch()");
		LOGGER.info("preparing buttons");
		textArea.getCaret().isSelectionVisible();
		textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setFont(font);
        textArea.setPreferredSize(new Dimension(70, 35));
        textArea.setEditable(false);
        
        button0.setFont(font);
        button0.setPreferredSize(new Dimension(70, 35) );
        button0.setBorder(new LineBorder(Color.BLACK));
        button0.setEnabled(true);
        
        button1.setFont(font);
        button1.setPreferredSize(new Dimension(35, 35) );
        button1.setBorder(new LineBorder(Color.BLACK));
        button1.setEnabled(true);
        
        button2.setFont(font);
        button2.setPreferredSize(new Dimension(35, 35) );
        button2.setBorder(new LineBorder(Color.BLACK));
        button2.setEnabled(true);
        
        button3.setFont(font);
        button3.setPreferredSize(new Dimension(35, 35) );
        button3.setBorder(new LineBorder(Color.BLACK));
        button3.setEnabled(true);
        
        button4.setFont(font);
        button4.setPreferredSize(new Dimension(35, 35) );
        button4.setBorder(new LineBorder(Color.BLACK));
        button4.setEnabled(true);
        
        button5.setFont(font);
        button5.setPreferredSize(new Dimension(35, 35) );
        button5.setBorder(new LineBorder(Color.BLACK));
        button5.setEnabled(true);
        
        button6.setFont(font);
        button6.setPreferredSize(new Dimension(35, 35) );
        button6.setBorder(new LineBorder(Color.BLACK));
        button6.setEnabled(true);
        
        button7.setFont(font);
        button7.setPreferredSize(new Dimension(35, 35) );
        button7.setBorder(new LineBorder(Color.BLACK));
        button7.setEnabled(true);
        
        button8.setFont(font);
        button8.setPreferredSize(new Dimension(35, 35) );
        button8.setBorder(new LineBorder(Color.BLACK));
        button8.setEnabled(true);
        
        button9.setFont(font);
        button9.setPreferredSize(new Dimension(35, 35) );
        button9.setBorder(new LineBorder(Color.BLACK));
        button9.setEnabled(true);
        
        buttonClear.setFont(font);
        buttonClear.setPreferredSize(new Dimension(35, 35) );
        buttonClear.setBorder(new LineBorder(Color.BLACK));
        buttonClear.setEnabled(true);
        
        buttonClearEntry.setFont(font);
        buttonClearEntry.setPreferredSize(new Dimension(35, 35) );
        buttonClearEntry.setBorder(new LineBorder(Color.BLACK));
        buttonClearEntry.setEnabled(true);
        
        buttonDelete.setFont(font);
        buttonDelete.setPreferredSize(new Dimension(35, 35) );
        buttonDelete.setBorder(new LineBorder(Color.BLACK));
        buttonDelete.setEnabled(true);
        
        buttonDot.setFont(font);
        buttonDot.setPreferredSize(new Dimension(35, 35) );
        buttonDot.setBorder(new LineBorder(Color.BLACK));
        buttonDot.setEnabled(true);
        LOGGER.info("Finished. Leaving setupButtonsAndSuch()");
	}
	
	public void setupMenuBar() {
    	LOGGER.info("Inside setMenuBar()");
        // Menu Bar and Options
        JMenuBar bar = new JMenuBar(); // create menu bar
        setJMenuBar(bar); // add menu bar to application
        
        // View Menu and Actions
        JMenu viewMenu = new JMenu("View"); // create view menu
        viewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(viewMenu); // add viewMenu to menu bar
        
            JMenuItem standard = new JMenuItem("Standard");
            standard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JMenuItem binary = new JMenuItem("Binary");
            standard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            viewMenu.add(standard);
            standard.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    // remove all panels
                	// add basic panel
                }
            });
            
            viewMenu.add(binary);
            binary.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    
                }
            });
        // Edit Menu and Actions
        JMenu editMenu = new JMenu("Edit"); // create edit menu
        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(editMenu); // add editMenu to menu bar
        
            JMenuItem copyItem = new JMenuItem("Copy"); 
            copyItem.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
            copyItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                // create first item in editMenu
            JMenuItem pasteItem = new JMenuItem("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK));
            pasteItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                // create second item in editMenu
            JMenu historyMenu = new JMenu("History");
            historyMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                /// create third time in editMenu
            
            // add JMenuItems to editMenu
            editMenu.add(copyItem);
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    values[3] = textArea.getText(); // to copy
                    textarea = textArea.getText();
                    confirm();
                }
            });
            
            editMenu.add(pasteItem);
            pasteItem.addActionListener(
            new ActionListener() {
                // paste from copyItem
                @Override
                public void actionPerformed(ActionEvent event) {
                    if (values[3].equals(""))
                    	LOGGER.info("Temp[3] is null");
                    else
                    	LOGGER.info("temp[3]: " + values[3]);
                    textArea.setText(values[3]); // to paste
                    values[valuesPosition] = textArea.getText();
                    textarea = textArea.getText();
                    confirm();
                }
            }
        );
        editMenu.addSeparator();
        editMenu.add(historyMenu);
            
        JMenuItem copyHistoryItem = new JMenuItem("Copy History");
        copyHistoryItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
            // create first item in historyMenu
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
            // create second item in historyMenu
        JMenuItem cancelEditItem = new JMenuItem("Cancel Edit");
        cancelEditItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
            // create third item in historyMenu
        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
            // create fourth item in historyMenu

        // add JMenuItems to historyMenu
        historyMenu.add(copyHistoryItem);
        historyMenu.add(editItem);
        historyMenu.add(cancelEditItem);
        historyMenu.add(clearItem);
                    
        // Help  Menu and Actions
        JMenu helpMenu = new JMenu("Help"); // create help menu
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(helpMenu); // add helpMenu to menu bar
        
            JMenuItem viewHelpItem = new JMenuItem("View Help");
            viewHelpItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
            
            JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");
            aboutCalculatorItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                // create second item in helpMenu
            
                // add JMenuItems to helpMenu
                helpMenu.add(viewHelpItem);
                // NEEDS UPDATING
                // Needs a scroll bar
                // Text with hyperlinks 
                // Info about how to use
                viewHelpItem.addActionListener(new ActionListener() {
                    // display message dialog box when user selects Help....
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        String COPYRIGHT = "\u00a9";
                        iconLabel = new JLabel();
                        //iconLabel.setIcon(calculator);
                        //iconLabel.setText(" ");
                        JPanel iconPanel = new JPanel(new GridBagLayout() );
                        
                        iconPanel.add(iconLabel);
                        textLabel = new JLabel("<html>Apple MacBook Air "
                            + "Version 3.0.1<br>"
                            + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
                            + "Mac OS mojave and its user interface are protected by trademark and all other<br>"
                            + "pending or existing intellectual property right in the United States and other<br>"
                            + "countries/regions."
                            + "<br><br><br>"
                            + "This product is licensed under the License Terms to:<br>"
                            + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
                        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                        
                        JPanel mainPanel = new JPanel();
                        mainPanel.setBackground(Color.white);
                        mainPanel.add(iconLabel);
                        mainPanel.add(textLabel);
                        JOptionPane.showMessageDialog(Calculator_v3.this,
                            mainPanel, "Help", JOptionPane.PLAIN_MESSAGE); 
                    }
                } 
            );
            helpMenu.addSeparator();
            helpMenu.add(aboutCalculatorItem);
            aboutCalculatorItem.addActionListener(
            new ActionListener() // anonymous inner class
            {
                // display message dialog box when user selects About....
                @Override
                public void actionPerformed(ActionEvent event)
                {
                    String COPYRIGHT = "\u00a9";
                    //iconLabel = new JLabel(calculator2);
                    //iconLabel.setIcon(calculator2);
                    //iconLabel.setVisible(true);
                    JPanel iconPanel = new JPanel(new GridBagLayout() );
                    iconPanel.add(iconLabel);
                    textLabel = new JLabel("<html>Apple MacBook Air"
                        + "Version 3.0.1 (Build 1)<br>"
                        + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
                        + "Mac OS mojave and its user interface are<br>"
                        + "protected by trademark and all other pending or existing intellectual property<br>"
                        + "right in the United States and other countries/regions."
                        + "<br><br><br>"
                        + "This product is licensed under the License Terms to:<br>"
                        + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                    
                    JPanel mainPanel = new JPanel();
                    mainPanel.add(iconLabel);
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(Calculator_v3.this,
                        mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE); 
                }
            } 
        );
        LOGGER.info("Finished. Leaving setupMenuBar()");
    } // end public setMenuBar
	
	public boolean resetOperator(boolean operatorBool) {
        if (operatorBool == true) {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            valuesPosition = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            textarea = "";
            return false;
        } else {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            valuesPosition = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            textarea = "";
            return true;
        }
    }
	
	public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setMemoryValueAtPosition(String memoryValue, int memoryPosition) { 
    	this.memoryValues[memoryPosition] = memoryValue;
    	LOGGER.info("memoryValue["+memoryPosition+"]: " + memoryValues[memoryPosition]);
    	setMemoryPosition(memoryPosition);
    }
	
	/**
     *  Returns the results of the last action
     *
     */
    public void confirm() {
        LOGGER.info("Confirm Results: ");
        LOGGER.info("---------------- ");
//        for(int i=0; i<3; i++)
//            LOGGER.info("temp["+i+"]: \'"+values[i]+"\'");
//        int local = --memoryPosition;
//    	if (local != -1) {
//    		for (String memories : memoryValues) {
//        		LOGGER.info("memoryValues.get("+local+"): " + memoryValues.get(local));
//        		local--;
//        	}
//    	} else {
//    		LOGGER.info("no memories stored!");
//    	}
        LOGGER.info("textarea: '\\n"+textarea.replaceAll("\n", "")+"'");
        LOGGER.info("textArea: '\\n"+textArea.getText().replaceAll("\n", "")+"'");
        if (!memoryValues[memoryPosition].equals("")) {
        	ArrayList<String> nonBlankValues = getNonBlankValues(memoryValues);
        	int lengthOfNonBlankValues = nonBlankValues.size();
        	int index = 0;
    		for (String memory : nonBlankValues) {
        		LOGGER.info("memoryValues["+index+"]: " + memory);
        		if (index < lengthOfNonBlankValues) { index++; }
        	}
    	} else {
    		LOGGER.info("no memories stored!");
    	}
        LOGGER.info("addBool: '"+addBool+"'");
        LOGGER.info("subBool: '"+subBool+"'"); 
        LOGGER.info("mulBool: '"+mulBool+"'"); 
        LOGGER.info("divBool: '"+divBool+"'"); 
        LOGGER.info("valuesPosition: '"+valuesPosition+"'"); 
        LOGGER.info("memoryPosition: '"+memoryPosition+"'");
        LOGGER.info("firstNumBool: '"+firstNumBool+"'"); 
        LOGGER.info("dotButtonPressed: '"+dotButtonPressed+"'");
        LOGGER.info("-------- End Confirm Results --------\n");
    }
    
    public ArrayList<String> getNonBlankValues(String[] someValues) {
    	ArrayList<String> listToReturn = new ArrayList<>();
    	for (int i=0; i<10; i++) {
    		String thisValue = someValues[i];
    		if (!thisValue.equals("")) {
    			listToReturn.add(thisValue);
    		} else {
    			break;
    		}
    	}
    	return listToReturn;
    }
	
    /** This method resets the 4 main operators to the boolean you pass in */
    public void resetOperators(boolean bool) {
    	addBool = bool;
    	subBool = bool;
    	mulBool = bool;
    	divBool = bool;
    }
    
    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     * @param currentNumber
     * @return updated currentNumber
     */
    private String clearZeroesAtEnd(String currentNumber) {
        LOGGER.info("starting clearZeroesAtEnd()");
        LOGGER.info("currentNumber: " + currentNumber);
        textarea = currentNumber; // copy of currentNumber
        boolean justZeroes = true;
        int index = 0;
        index = textarea.indexOf(".");
        if (index == -1) return currentNumber;
        //int indexStartPosition = index;
        //int keepIndex = index;
        // this for loop checks first to make sure its all zeroes after the decimal point
        check: 
        for(int i = index + 1; i < textarea.length(); i++) {
            // from decimal point to the end of the number
            //System.out.println("i: " + i);
            // eliminate decimal first
            if (!textarea.substring(i, i+1).equals("0")) {
                // if the string value at valuesPosition x is not a 0
                justZeroes = false;
                break check;
            }
        }
        if (justZeroes == true) {
            // happy path; delete all from index onward
            textarea = textarea.substring(0,index);
        }
        
        LOGGER.info("output of clearZeroesAtEnd(): " + textarea);
        return textarea;
    }
    
    // tested: passed
    public boolean isDecimal(String number) {
    	LOGGER.info("isDecimal() running");
        boolean answer = false;
        for(int i = 0; i < number.length(); i++) {
            if (number.substring(i).startsWith(".")) {
                answer = true;
            }
        }
        return answer;
    }
    
    public boolean isNegativeNumber(String result) {
        boolean answer = false;
        if (result.contains("-")) { // if int == double, cut off decimal and zero
            answer = true;
        }
        LOGGER.info("isNegativeNumber("+result+") == " + answer);
        return answer;
    }
    
    // clear method
    public void clear() {
    	LOGGER.info("Inside clear()");
        // firstNum, secondNum, total, copy/paste, memory
        for ( valuesPosition=0; valuesPosition < 4; valuesPosition++) {
            values[valuesPosition] = "";
        } 
        textArea.setText("\n0");
        textarea = textArea.getText();
        resetOperators(false);
        valuesPosition = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotActive = false;
        buttonDot.setEnabled(true);
               
    }
    
    public String convertToNegative(String number) {
    	LOGGER.info("convertToNegative() running");
        LOGGER.info("Old: " + number.replaceAll("\n", ""));
        LOGGER.info("New: "  + "-" + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.info("Converted Number: " + number);
        return number;
    }
    
    public String convertToPositive(String number) {
    	LOGGER.info("convertToPositive() running");
    	LOGGER.info("Number to convert: " + number);
        if (number.endsWith("-")) {
        	LOGGER.info("1Converted Number: " + number.substring(0, number.length()-1) );
            number = number.substring(0, number.length()-1);
        } else {
        	LOGGER.info("2Converted Number: " + number.substring(1, number.length()) );
            number = number.substring(1, number.length());
        }
        return number;
    }
    
 	class NumberButtonHandler implements ActionListener { 
 		
 		
 		public void actionPerformed(ActionEvent e) {   
 	        LOGGER.info("NumberButtonHandler() started");
 	        LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
 	        if (firstNumBool == true) {
 	            if (memAddBool == true || memSubBool == true) { // || valuesPosition == 0: essentially resetting value
 	                textArea.setText("");
 	            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
 	                textArea.setText(values[0]);
 	                valuesPosition = 0;
 	                firstNumBool = true;
 	                dotButtonPressed = false;
 	                dotActive = false;
 	            } else if (textArea.getText().equals("\n0")) { 
 	                textArea.setText("");
 	                values[valuesPosition] = "";
 	                firstNumBool = true;
 	                dotButtonPressed = false;
 	                dotActive = false;          
 	            } else if (values[0].equals("") && !values[1].equals("")) {
 	                values[0] = values[1];
 	                values[1] = "";
 	                valuesPosition = 0;
 	            }
 	            if (firstNumBool == true && !isNegativeNumber(values[valuesPosition])) {        
 	                LOGGER.info("firstNumBool = true | positive number = true");
 	                LOGGER.info("before: " + textArea.getText().replaceAll("\n", ""));
 	                textArea.setText("\n" + textArea.getText().replaceAll("\n", "") + e.getActionCommand()); // update textArea
 	                values[valuesPosition] = textArea.getText().replaceAll("\n", ""); // store textarea
 	                
 	                LOGGER.info("textArea:'\\n" + textArea.getText().replaceAll("\n", "") + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
 	                textarea = values[valuesPosition];
 	            } else if (dotButtonPressed == false) { // logic for negative numbers
 	            	LOGGER.info("firstNumBool = true | dotButtonPressed = false | negative number = true");
 	                textarea = values[valuesPosition];
 	                LOGGER.info("textarea: '", textarea + "'");
 	                textarea = textarea + e.getActionCommand(); // update textArea
 	                LOGGER.info("textarea: '" + textarea + "'");
 	                values[valuesPosition] = textarea; // store textarea
 	                textArea.setText("\n" + convertToPositive(textarea) + "-");
 	                LOGGER.info("textArea: '" + textArea.getText() + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
 	            }
 	            else { // dotPressed = true
 	            	LOGGER.info("firstNumBool = true | dotButtonPressed = true");
 	                if (!textarea.equals("") && dotButtonPressed) {
 	                	textarea = values[valuesPosition] + "." + e.getActionCommand();
 	                	textArea.setText("\n" + textarea );
 	                	LOGGER.info("textarea: " + textarea);
 	                	values[valuesPosition] = textArea.getText().replaceAll("\n", "");
 	                	dotButtonPressed = false;
 	                } else if (!textarea.equals("") && !dotButtonPressed) {
 	                	textarea = values[valuesPosition] + e.getActionCommand();
 	                	textArea.setText("\n" + textarea );
 	                	LOGGER.info("textarea: " + textarea);
 	                	values[valuesPosition] = textArea.getText().replaceAll("\n", "");
 	                } else if (textarea.equals(".0")) {
 	                    textArea.setText("0.");
 	                    textArea.setText(textArea.getText() + e.getActionCommand()); // update textArea
 	                    values[valuesPosition] =  textArea.getText(); // store textarea
 	                    textArea.setText("\n" + textArea.getText());
 	                    LOGGER.info("textArea: '\\n" + textArea.getText().replaceAll("\n", "") + "'");
 	                    LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
 	                    //s = s.substring(0,s.length());
 	                    LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
 	                    //temp[0] = s;
 	                    textarea = "";
 	                    LOGGER.info("dotButtonPressed: '", dotButtonPressed + "'");
 	                    LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'"); // print out stored textarea confirmation
 	                    dotButtonPressed = false;
 	                    LOGGER.info("dotButtonPressed: '", dotButtonPressed + "'");
 	                } else if (values[valuesPosition].endsWith("-.0")) {
 	                    textarea = values[valuesPosition];
 	                    LOGGER.info("textarea: '", textarea + "'");
 	                    textarea = textarea.substring(2, textarea.length());
 	                    //System.out.printf("\ntextarea: %s\n", textarea);
 	                    textarea = textarea + "." + e.getActionCommand();
 	                    LOGGER.info("textarea: '-", textarea + "'");
 	                    textarea = "-" + textarea;
 	                    textArea.setText("\n" + textarea.replaceAll("-", "") + "-");
 	                    values[valuesPosition] = textarea;
 	                } else if (values[valuesPosition].startsWith("-0.")){
 	                    textarea = textArea.getText().replaceAll("\\n", ""); // collect textarea
 	                    LOGGER.info("Old " + textarea);
 	                    textarea = textarea.substring(0, textarea.length()-1);
 	                    LOGGER.info("New " + textarea);
 	                    textArea.setText("\n" + textarea + e.getActionCommand() + "-"); // update textArea
 	                    values[valuesPosition] = textArea.getText().replaceAll("\\n", ""); // update textarea with decimal
 	                    LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
 	                    LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'"); // print out stored textarea confirmation
 	                    textarea = textArea.getText().replaceAll("\\n", "");
 	                }
 	            }   
 	        } 
 	        else { // do for second number
 	            if (firstNumBool == false && dotButtonPressed == false) {
 	                LOGGER.info("Resetting textArea");
 	                textArea.setText("");
 	                textarea = textArea.getText();
 	                if (firstNumBool == false)
 	                    firstNumBool = true;
 	                else
 	                    dotButtonPressed = true;
 	                buttonDot.setEnabled(true);
 	            } 
 	            if (textarea.equals(".0"))  {
 	                textArea.setText("\n" + "0.");
 	                textarea = "0.";
 	            } else if (textarea.equals("0")) { 
 	            	values[valuesPosition] = "";
 	                textArea.setText("\n");
 	                valuesPosition = 1;
 	                firstNumBool = true;
 	                dotButtonPressed = false;
 	                dotActive = false;
 	            } else if (values[0].equals("") && !values[1].equals("") && negatePressed == false) {
 	                values[0] = values[1];
 	                values[1] = "";
 	                valuesPosition = 1;
 	            }
 	            if (firstNumBool == false && negatePressed == false) {
 	            	textArea.setText("\n " + textarea.replaceAll("\n", "") + e.getActionCommand());
 	                values[valuesPosition] = textArea.getText(); // store textarea
 	                LOGGER.info("textArea: '", textArea.getText() + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'"); // print out stored textarea confirmation
 	            } else if (firstNumBool == true && negatePressed == true) { 
 	            	// we did something such as 9 + -
 	            	// indicating that the second number will be negative
 	            	//textArea.setText(e.getActionCommand()+"-");
 	            	textArea.setText("\n " + e.getActionCommand() + "-" + textarea.replaceAll("\n", ""));
 	            	LOGGER.info("textArea: '" + textArea.getText() + "'");
 	            	values[valuesPosition] = convertToNegative(e.getActionCommand());
 	            	negatePressed = false;
 	        	} else if (firstNumBool == true && negatePressed == false) {
 	                textArea.setText("\n" + e.getActionCommand());
 	                values[valuesPosition] = textArea.getText().replaceAll("\n", ""); // store textarea
 	                LOGGER.info("textArea: '\\n" + textArea.getText().replaceAll("\n", "") + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'"); // print out stored textarea confirmation
 	                firstNumBool = true;
 	            }
 	        }
 	        textarea = textArea.getText();
 	        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
 	        LOGGER.info("NumberButtonHandler() finished");
 	        confirm();    
 	    }
 	}
    
 	class ClearButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("ClearButtonHandler() started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            clear();
            LOGGER.info("ClearButtonHandler() finished");
            confirm();
        }
    }
    
 	// TODO: check
 	class ClearEntryButtonHandler implements ActionListener { 
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("ClearEntryButtonHandler() started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textarea = textArea.getText();
            textArea.setText("");
            if (values[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
                values[0] = "";
                addBool = false;
                subBool = false;
                mulBool = false;
                divBool = false;
                valuesPosition = 0;
                firstNumBool = true;
                dotButtonPressed = false;
            } else {
                values[1] = ""; 
            }
            buttonDot.setEnabled(true);
            textarea = textArea.getText();
            LOGGER.info("ClearEntryButtonHandler() finished");
            confirm();
        }
    }
    
 	class DeleteButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("DeleteButtonHandler() started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (values[1].equals("")) { 
            	//addBool = false;
                //subBool = true;
                //mulBool = true;
                //divBool = true;
            	valuesPosition = 0; 
            } // assume they just previously pressed an operator
            textarea = textArea.getText().replaceAll("\n", "");
            LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
            LOGGER.info("textarea: " + textarea);
            boolean isNegative = isNegativeNumber(values[valuesPosition]);
            LOGGER.info("isNegativeNumber() result: " + isNegative);
            dotActive = isDecimal(values[valuesPosition]);
            LOGGER.info("isDecimal() result: " + dotActive);
            
            if (addBool == false && subBool == false && mulBool == false && divBool == false) {
                if (isNegative == false) {
                    // if no operator has been pushed; number is positive; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = "";
                        } else if (textarea.length() == 2) {
                            textarea = textarea.substring(0, textarea.length()-1);
                        } else if (textarea.length() >= 2) { // ex: 56
                            textarea = textarea.substring(0, textarea.length()-1);
                        } 
                                // dotActive == true and length == 3
                                //textarea = textarea.substring(0, 1);
                        LOGGER.info("output: " + textarea);
                        textArea.setText("\n" + textarea);
                        values[valuesPosition] = textarea;
                        confirm();
                    }
                    // if no operator has been pushed; number is positive; number is decimal
                    else if (dotActive == true) {
                        if (textarea.length() == 2) { // ex: 3.
                            textarea = textarea.substring(0, textarea.length()-1); // ex: 3
                            dotButtonPressed = false;
                            dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
                            textarea = textarea.substring(0, textarea.length() - 2); // ex: 3 or 0
                            dotButtonPressed = false;
                            dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50
                            textarea = textarea.substring(0, textarea.length() - 1); // inclusive
                        }
                        LOGGER.info("output: " + textarea);
                        if (textarea.endsWith(".")) {
                            textarea = textarea.substring(0,textarea.length()-1);
                            dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.startsWith(".")) {
                            textarea = textarea.substring(1,1);
                        } 
                        textarea = clearZeroesAtEnd(textarea);
                        textArea.setText("\n" + textarea);
                        values[valuesPosition] = textarea;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if no operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = "";
                            textArea.setText(textarea);
                            values[valuesPosition] = "";
                        } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                            textarea = convertToPositive(textarea);
                            textarea = textarea.substring(0, textarea.length());
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("output: " + textarea);
                        confirm();
                    }
                    // if no operator has been pushed; number is negative; number is decimal
                    else if (dotActive == true) {
                        if (textarea.length() == 4) { // -3.2
                            textarea = convertToPositive(textarea); // 3.2
                            textarea = textarea.substring(0, 1); // 3 
                            dotButtonPressed = false;
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
                            textarea = convertToPositive(textarea); // 3.00 or 0.00
                            textarea = textarea.substring(0, textarea.length()); // 3.0 or 0.0
                            textarea = clearZeroesAtEnd(textarea); // 3 or 0
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("output: " + textarea);
                        confirm();
                    }
                }
                
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                if (isNegative == false) {
                    // if an operator has been pushed; number is positive; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = "";
                        } else if (textarea.length() == 2) {
                            textarea = textarea.substring(0, textarea.length()-1);
                        } else if (textarea.length() >= 2) { // ex: 56 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = values[valuesPosition];
                        	} else {
                        		textarea = textarea.substring(0, textarea.length()-1);
                        	}
                        } 
                        LOGGER.info("output: " + textarea);
                        textArea.setText("\n" + textarea);
                        values[valuesPosition] = textarea;
                        confirm();
                    }
                    // if an operator has been pushed; number is positive; number is decimal
                    else if (dotActive == true) {
                        if (textarea.length() == 2) // ex: 3.
                            textarea = textarea.substring(0, textarea.length()-1);
                        else if (textarea.length() == 3) { // ex: 3.2 0.0
                            textarea = textarea.substring(0, textarea.length()-2); // 3 or 0
                            dotActive = false;
                            dotButtonPressed = false;
                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = values[valuesPosition];
                        	} else {
                        		textarea = textarea.substring(0, textarea.length() -1);
                                textarea = clearZeroesAtEnd(textarea);
                        	}
                        }
                        LOGGER.info("output: " + textarea);
                        textArea.setText(textarea);
                        values[valuesPosition] = textarea;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if an operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = "";
                            textArea.setText(textarea);
                            values[valuesPosition] = "";
                        } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = values[valuesPosition];
                        	}
                        	textarea = convertToPositive(textarea);
                            textarea = textarea.substring(0, textarea.length());
                            textArea.setText("\n" + textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("textarea: " + textarea);
                        confirm();
                    }
                    // if an operator has been pushed; number is negative; number is decimal
                    else if (dotActive == true) {
                        if (textarea.length() == 4) { // -3.2
                            textarea = convertToPositive(textarea); // 3.2 or 0.0
                            textarea = textarea.substring(0, 1); // 3 or 0
                            dotActive = false;
                            dotButtonPressed = false;
                            textArea.setText(textarea + "-"); // 3- or 0-
                            values[valuesPosition] = "-" + textarea; // -3 or -0
                        } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
                            textarea = convertToPositive(textarea); // 3.25 or 0.00
                            textarea = textarea.substring(0, textarea.length()); // 3.2 or 0.0
                            textarea = clearZeroesAtEnd(textarea);
                            LOGGER.info("textarea: " + textarea);
                            if (textarea.equals("0")) {
                                textArea.setText(textarea);
                                values[valuesPosition] = textarea;
                            } else {
                                textArea.setText(textarea + "-");
                                values[valuesPosition] = "-" + textarea;
                            }
                        }
                        LOGGER.info("textarea: " + textarea);
                    }
                }
                resetOperators(false);
            }
            dotActive = false;
            LOGGER.info("DeleteButtonHandler() finished");
            confirm();
        }
        
    }
    
    // DotButtonHandler operates on this button:
    // final private JButton buttonDot = new JButton(".");
    class DotButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            LOGGER.info("DotButtonHandler() started");
            LOGGER.info("button: " + event.getActionCommand()); // print out button confirmation
            if (!values[valuesPosition].equals("")) { // if the textarea is not null
            	LOGGER.info("dotButtonPressed: " + dotButtonPressed + " | dotActive: " + dotActive);
                LOGGER.info("values["+valuesPosition+"] is " + values[valuesPosition] + " before appending dot"); 
                if (dotButtonPressed == false || dotActive == false) {
                    textArea.setText("\n" + "."+values[valuesPosition]);
                    LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                    firstNumBool = true;
                    dotButtonPressed = true;
                } 
            } else { // the first button we are pushing is the dot operator
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                if (firstNumBool == false)
                    textArea.setText("");
                textarea = ".0";
                textArea.setText("\n" + textarea);
                values[valuesPosition] = "0.";
                dotButtonPressed = true;
            }
            dotActive = true;
            buttonDot.setEnabled(false);
            textarea = textArea.getText();
            LOGGER.info("DotButtonHandler() finished");
            confirm();
        }
    }
    
    /** Calls createImageIcon(String path, String description 
     * @throws Exception */
    public ImageIcon createImageIcon(String path) throws Exception {
    	return createImageIcon(path, "No description given.");
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. 
     * @throws Exception */
    @SuppressWarnings("unused")
	protected ImageIcon createImageIcon(String path, String description) throws Exception {
        LOGGER.info("Inside createImageIcon()");
        File sourceimage = new File(path);
        //java.net.URL imgURL = getClass().getResource(path);
        if (sourceimage != null) {
    	    LOGGER.info("the path '" + path + "' created an image! the imageicon is being returned...");
    	    LOGGER.info("End createImageIcon()");
    	    return new ImageIcon(ImageIO.read(sourceimage), description);
    	    //return new ImageIcon(imgURL, description);
        } else {
        	throw new Exception("The path '" + path + "' could not find an image there!");
        }
    }
    
    /** Sets the image icons */
    public void setImageIcons() {
    	try {
        	calculatorImage1 = createImageIcon("src/main/resources/images/calculatorOriginalCopy.jpg");
        	calculator2 = createImageIcon("src/main/resources/images/calculatorOriginal.jpg");
        	macLogo = createImageIcon("src/main/resources/images/maclogo.png");
        } catch (Exception e) {
        	LOGGER.error(e.getMessage());
        }
    }
    
    /*
     * Thought I might keep this because I felt it 
     * should be here but logic didn't work properly
     */
//    public void printMemoryValuesPosition() {
//    	int local = memoryPosition - 1;
//    	if (local != -1) {
//    		for (String memories : memoryValues) {
//        		LOGGER.info("memoryValues.get("+local+"): " + memoryValues.get(local));
//        		local--;
//        	}
//    	} else {
//    		LOGGER.info("no memories stored!");
//    	}
//    }

    // method to set constraints on
    public void addComponent(Component c, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }
    
    protected JTextArea getTextArea() { return this.textArea; }

}


