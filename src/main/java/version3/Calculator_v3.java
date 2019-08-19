package version3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;


public abstract class Calculator_v3 extends JFrame {
	
	final public static String BASIC = "Basic";
	protected Mode mode = Mode.BASIC;
	public Mode getMode() { return this.mode; }
	protected void setMode(Mode mode) { this.mode = mode; }
    final public static String PROGRAMMER = "Programmer";
    final public static String SCIENTIFIC = "Scientific";
    final public static String DATE = "Date";
	
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
    final protected ClearEntryButtonHandler clearEntryButtonHandler = new ClearEntryButtonHandler();
    final protected JButton buttonClearEntry = new JButton("CE");
    //final String delete = "\u2190";
    final protected DeleteButtonHandler deleteButtonHandler = new DeleteButtonHandler();
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
    protected String textarea = ""; // String representing appropriate visual of number
    
	protected boolean firstNumBool = true, numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false, numberIsNegative = false;
	protected boolean memorySwitchBool = false;
    protected boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false, negatePressed = false;
	protected boolean dotButtonPressed = false;

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
		//setupMenuBar();
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

        // these buttons while are apart of Calculator_v3, must be defined
        // in the JPanel that uses them.
        // TODO: Implement in JPanels
        button0.setFont(font);
        button0.setPreferredSize(new Dimension(70, 35) );
        button0.setBorder(new LineBorder(Color.BLACK));
        button0.setEnabled(true);
        button0.addActionListener(buttonHandler);
        
        button1.setFont(font);
        button1.setPreferredSize(new Dimension(35, 35) );
        button1.setBorder(new LineBorder(Color.BLACK));
        button1.setEnabled(true);
        button1.addActionListener(buttonHandler);
        
        button2.setFont(font);
        button2.setPreferredSize(new Dimension(35, 35) );
        button2.setBorder(new LineBorder(Color.BLACK));
        button2.setEnabled(true);
        button2.addActionListener(buttonHandler);
        
        button3.setFont(font);
        button3.setPreferredSize(new Dimension(35, 35) );
        button3.setBorder(new LineBorder(Color.BLACK));
        button3.setEnabled(true);
        button3.addActionListener(buttonHandler);
        
        button4.setFont(font);
        button4.setPreferredSize(new Dimension(35, 35) );
        button4.setBorder(new LineBorder(Color.BLACK));
        button4.setEnabled(true);
        button4.addActionListener(buttonHandler);
        
        button5.setFont(font);
        button5.setPreferredSize(new Dimension(35, 35) );
        button5.setBorder(new LineBorder(Color.BLACK));
        button5.setEnabled(true);
        button5.addActionListener(buttonHandler);
        
        button6.setFont(font);
        button6.setPreferredSize(new Dimension(35, 35) );
        button6.setBorder(new LineBorder(Color.BLACK));
        button6.setEnabled(true);
        button6.addActionListener(buttonHandler);
        
        button7.setFont(font);
        button7.setPreferredSize(new Dimension(35, 35) );
        button7.setBorder(new LineBorder(Color.BLACK));
        button7.setEnabled(true);
        button7.addActionListener(buttonHandler);
        
        button8.setFont(font);
        button8.setPreferredSize(new Dimension(35, 35) );
        button8.setBorder(new LineBorder(Color.BLACK));
        button8.setEnabled(true);
        button8.addActionListener(buttonHandler);
        
        button9.setFont(font);
        button9.setPreferredSize(new Dimension(35, 35) );
        button9.setBorder(new LineBorder(Color.BLACK));
        button9.setEnabled(true);
        button9.addActionListener(buttonHandler);
        
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

    public boolean getDotButtonPressed() { return this.dotButtonPressed; }
    private void setDotButtonPressed(boolean bool) { this.dotButtonPressed = bool; }


    /**
     *  Returns the results of the last action
     *
     */
    public void confirm() {
	    confirm("");
    }
	

    /**
     *  Returns the results of the last action with a specific message to display
     * @param message a message to send into the confirm results view
     */
    public void confirm(String message) {
        LOGGER.info("Confirm Results: " + message);
        LOGGER.info("---------------- ");
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
        LOGGER.info("mode: " + mode);
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
        if (justZeroes) {
            // happy path; delete all from index onward
            textarea = textarea.substring(0,index);
        }
        
        LOGGER.info("output of clearZeroesAtEnd(): " + textarea);
        return textarea;
    }
    
    // tested : passed
    public boolean isDecimal(String number) {
        boolean answer = false;
        if (number.contains(".")) answer = true;
        LOGGER.info("isDecimal("+number+") == " + answer);
        return answer;
    }

    // tested : passed
    public boolean isPositiveNumber(String result) {
        boolean answer = !isNegativeNumber(result);
        LOGGER.info("isPositiveNumber("+result+") == " + answer);
        return answer;
    }

    // tested : passed
    public boolean isNegativeNumber(String result) {
        boolean answer = false;
        if (result.contains("-")) {
            answer = true;
        }
        LOGGER.info("isNegativeNumber("+result.replaceAll("\n", "")+") == " + answer);
        return answer;
    }
    
    // clears the entire calculator
    public void clear() {
    	LOGGER.info("Inside clear()");
        // firstNum/total, secondNum, copy/paste
        for ( valuesPosition=0; valuesPosition < 3; valuesPosition++) {
            values[valuesPosition] = "";
        } 
        textArea.setText("\n0");
        textarea = "0";
        resetOperators(false);
        valuesPosition = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotButtonPressed = false;
        buttonDot.setEnabled(true);
               
    }

    // tested : passed
    public String convertToNegative(String number) {
    	LOGGER.info("convertToNegative() running");
        LOGGER.info("Old: " + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.info("New: "  + number);
        LOGGER.info("Number converted");
        return number;
    }

    // tested : passed
    public String convertToPositive(String number) {
    	LOGGER.info("convertToPositive() running");
        LOGGER.info("Old: " + number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        LOGGER.info("New: " + number);
        LOGGER.info("Number Converted");
        return number;
    }

    public boolean textAreaContainsBadText() {
        boolean result = false;
        if (textArea.getText().equals("Invalid textarea")   ||
            textArea.getText().equals("Cannot divide by 0") ||
            textArea.getText().replace("\n","").equals("Not a Number") ) {

            result = true;
        }
        return result;
    }

    class NumberButtonHandler implements ActionListener {
 		public void actionPerformed(ActionEvent e) {
 	        LOGGER.info("NumberButtonHandler() started");
 	        LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
 	        if (firstNumBool == true) {
 	            if (memAddBool == true || memSubBool == true) { // || valuesPosition == 0: essentially resetting value
 	                textArea.setText("");
 	            } else if (textAreaContainsBadText()) {
 	                textArea.setText("");
 	                valuesPosition = 0;
 	                firstNumBool = true;
 	                dotButtonPressed = false;
                    dotButtonPressed = false;
 	            } else if (textArea.getText().equals("\n0")) { 
 	                textArea.setText("");
 	                values[valuesPosition] = "";
 	                firstNumBool = true;
 	                dotButtonPressed = false;
                    dotButtonPressed = false;
 	            } else if (values[0].equals("") && !values[1].equals("")) {
 	                values[0] = values[1];
 	                values[1] = "";
 	                valuesPosition = 0;
 	            }

 	            if (firstNumBool == true && isPositiveNumber(values[valuesPosition]) && dotButtonPressed == false) {
 	                LOGGER.info("firstNumBool = true | positive number = true | dotButtonPressed = false");
 	                LOGGER.info("before: " + textArea.getText().replaceAll("\n", ""));
 	                textArea.setText("\n" + textArea.getText().replaceAll("\n", "") + e.getActionCommand()); // update textArea
 	                values[valuesPosition] = textArea.getText().replaceAll("\n", ""); // store textarea
 	                
 	                LOGGER.info("textArea:'\\n" + textArea.getText().replaceAll("\n", "") + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
 	                textarea = values[valuesPosition];
 	            } else if (firstNumBool == true && isNegativeNumber(values[valuesPosition]) && dotButtonPressed == false) { // logic for negative numbers
                    LOGGER.info("firstNumBool = true | negative number = true | dotButtonPressed = false");
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
 	            	LOGGER.info("firstNumBool = true | positive number = true | dotButtonPressed = true");
 	                if (!textarea.equals("") && dotButtonPressed) {
 	                	textarea = values[valuesPosition] + e.getActionCommand();
 	                	textArea.setText("\n" + textarea );
 	                	LOGGER.info("textarea: " + textarea);
 	                	values[valuesPosition] = textArea.getText().replaceAll("\n", "");
 	                } else if (!textarea.equals("") && !dotButtonPressed) {
 	                	textarea = values[valuesPosition] + e.getActionCommand();
 	                	textArea.setText("\n" + textarea );
 	                	LOGGER.info("textarea: " + textarea);
 	                	values[valuesPosition] = textArea.getText().replaceAll("\n", "");
 	                } /*else if (textarea.equals(".0")) {
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
 	                }*/
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
                    dotButtonPressed = false;
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

    /**
     * The class which handles the logic for when the delete button is clicked
     */
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
            numberIsNegative = isNegativeNumber(values[valuesPosition]);
            // this check has to happen
            dotButtonPressed = isDecimal(textarea);

            
            if (addBool == false && subBool == false && mulBool == false && divBool == false) {
                if (numberIsNegative == false) {
                    // if no operator has been pushed; number is positive; number is whole
                    if (getDotButtonPressed() == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = "0";
                        } else if (textarea.length() >= 2) { // ex: 56 or 2346
                            textarea = textarea.substring(0, textarea.length()-1);
                        }
                        LOGGER.info("output: '" + textarea.replaceAll("\n","") + "'");
                        values[valuesPosition] = textarea;
                        getTextArea().setText("\n"+textarea);
                    }
                    // if no operator has been pushed; number is positive; number is decimal
                    else if (getDotButtonPressed()) {
                        if (textarea.length() == 2) { // ex: 3. .... recall textarea looks like .3
                            textarea = textarea.substring(textarea.length()-1); // ex: 3
                            dotButtonPressed = false;
                            //dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
                            textarea = textarea.substring(0, textarea.length() - 2); // ex: 3 or 0
                            dotButtonPressed = false;
                            //dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50 or 5.02
                            textarea = textarea.substring(0, textarea.length() - 1); // inclusive
                        }
                        LOGGER.info("output: " + textarea);
                        /*if (textarea.endsWith(".")) {
                            textarea = textarea.substring(0,textarea.length()-1);
                            //dotActive = false;
                            dotButtonPressed = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.startsWith(".")) {
                            textarea = textarea.substring(1,1);
                        } */
                        //textarea = clearZeroesAtEnd(textarea);
                        textArea.setText("\n" + textarea);
                        values[valuesPosition] = textarea;
                    }
                } else if (numberIsNegative == true) {
                    // if no operator has been pushed; number is negative; number is whole
                    if (dotButtonPressed == false) {
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
                    }
                    // if no operator has been pushed; number is negative; number is decimal
                    else if (dotButtonPressed == true) {
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
                    }
                }
                
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                if (numberIsNegative == false) {
                    // if an operator has been pushed; number is positive; number is whole
                    if (dotButtonPressed == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = "";
                        } else if (textarea.length() == 2) {
                            textarea = textarea.substring(0, textarea.length()-1);
                        } else if (textarea.length() >= 2) { // ex: 56 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = values[valuesPosition];
                        		addBool = false;
                        		subBool = false;
                        		mulBool = false;
                        		divBool = false;
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
                    else if (dotButtonPressed == true) {
                        if (textarea.length() == 2) // ex: 3.
                            textarea = textarea.substring(0, textarea.length()-1);
                        else if (textarea.length() == 3) { // ex: 3.2 0.0
                            textarea = textarea.substring(0, textarea.length()-2); // 3 or 0
                            dotButtonPressed = false;
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
                        textArea.setText("\n"+textarea);
                        values[valuesPosition] = textarea;
                        confirm();
                    }
                } else if (numberIsNegative == true) {
                    // if an operator has been pushed; number is negative; number is whole
                    if (dotButtonPressed == false) {
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
                    else if (dotButtonPressed == true) {
                        if (textarea.length() == 4) { // -3.2
                            textarea = convertToPositive(textarea); // 3.2 or 0.0
                            textarea = textarea.substring(0, 1); // 3 or 0
                            dotButtonPressed = false;
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
            textarea = getTextArea().getText().replaceAll("\n","");
            getTextArea().setText("\n."+textarea);
            textarea = textarea + ".";
            values[valuesPosition] = textarea;
            buttonDot.setEnabled(false);
            setDotButtonPressed(true);
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


