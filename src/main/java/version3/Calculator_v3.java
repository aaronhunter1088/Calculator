package version3;

import org.apache.commons.lang3.StringUtils;
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

	protected CalcType_v3 calcType = null;
	public CalcType_v3 getCalcType() { return this.calcType; }
	protected void setCalcType(CalcType_v3 calcType) { this.calcType = calcType; }

	//
	protected JPanel currentPanel = null;
	public JPanel getCurrentPanelFromParentCalculator() { return this.currentPanel; }
	protected void setCurrentPaneloNParentCalculator(JPanel panel ) { currentPanel = panel; }
	
	final static protected Logger LOGGER = LogManager.getLogger(Calculator_v3.class);
	final static private long serialVersionUID = 1L;
	protected GridBagLayout layout; // layout of the calculator
    protected GridBagConstraints constraints; // layout's constraints
	// All calculators have:
//	final protected NumberButtonHandler buttonHandler = new NumberButtonHandler();
	final protected NumberButtonHandler_v2 buttonHandler_v2 = new NumberButtonHandler_v2();
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
    protected int valuesPosition = 0;

    public String[] getMemoryValues() { return memoryValues; }


    public void setMemoryValueAtPosition(String memoryValue, int memoryPosition) {
        this.memoryValues[memoryPosition] = memoryValue;
        LOGGER.info("memoryValue["+memoryPosition+"]: " + memoryValues[memoryPosition]);
        setMemoryPosition(memoryPosition);
    }

    protected String[] memoryValues = new String[]{"","","","","","","","","",""}; // stores memory values; rolls over after 10 entries
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    protected int memoryPosition = 0;

    protected ImageIcon calculatorImage1, calculator2, macLogo;
    protected JLabel iconLabel;
    protected JLabel textLabel;

    protected JTextArea getTextArea() { return this.textArea; }
    protected JTextArea textArea = new JTextArea(2,5); // rows, columns
    protected StringBuffer textarea = new StringBuffer(); // String representing appropriate visual of number
    
	protected boolean firstNumBool = true, numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false, numberIsNegative = false;
	protected boolean memorySwitchBool = false;
    protected boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false, negatePressed = false;

    // programmer booleans
    boolean isButtonBinSet = true;
    boolean isButtonOctSet = false;
    boolean isButtonDecSet = false;
    boolean isButtonHexSet = false;

    boolean isButtonByteSet = true;
    boolean isButtonWordSet = false;
    boolean isButtonDwordSet = false;
    boolean isButtonQwordSet = false;

    public boolean getDotButtonPressed() { return this.dotButtonPressed; }
    private void setDotButtonPressed(boolean bool) { this.dotButtonPressed = bool; }
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
        constraints = new GridBagConstraints();
		setupCalculator_v3();
		//setupMenuBar();
		setMinimumSize(new Dimension(100,200));
	}

	public Calculator_v3(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

    /**
     * Handles the logic for setting up a Calculator_v3. Should
     * run once. Anything that needs to be reset, should be reset
     * where appropriate.
     */
	public void setupCalculator_v3() {
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
        button0.addActionListener(buttonHandler_v2);
        
        button1.setFont(font);
        button1.setPreferredSize(new Dimension(35, 35) );
        button1.setBorder(new LineBorder(Color.BLACK));
        button1.setEnabled(true);
        button1.addActionListener(buttonHandler_v2);
        
        button2.setFont(font);
        button2.setPreferredSize(new Dimension(35, 35) );
        button2.setBorder(new LineBorder(Color.BLACK));
        button2.setEnabled(true);
        button2.addActionListener(buttonHandler_v2);
        
        button3.setFont(font);
        button3.setPreferredSize(new Dimension(35, 35) );
        button3.setBorder(new LineBorder(Color.BLACK));
        button3.setEnabled(true);
        button3.addActionListener(buttonHandler_v2);
        
        button4.setFont(font);
        button4.setPreferredSize(new Dimension(35, 35) );
        button4.setBorder(new LineBorder(Color.BLACK));
        button4.setEnabled(true);
        button4.addActionListener(buttonHandler_v2);
        
        button5.setFont(font);
        button5.setPreferredSize(new Dimension(35, 35) );
        button5.setBorder(new LineBorder(Color.BLACK));
        button5.setEnabled(true);
        button5.addActionListener(buttonHandler_v2);
        
        button6.setFont(font);
        button6.setPreferredSize(new Dimension(35, 35) );
        button6.setBorder(new LineBorder(Color.BLACK));
        button6.setEnabled(true);
        button6.addActionListener(buttonHandler_v2);
        
        button7.setFont(font);
        button7.setPreferredSize(new Dimension(35, 35) );
        button7.setBorder(new LineBorder(Color.BLACK));
        button7.setEnabled(true);
        button7.addActionListener(buttonHandler_v2);
        
        button8.setFont(font);
        button8.setPreferredSize(new Dimension(35, 35) );
        button8.setBorder(new LineBorder(Color.BLACK));
        button8.setEnabled(true);
        button8.addActionListener(buttonHandler_v2);
        
        button9.setFont(font);
        button9.setPreferredSize(new Dimension(35, 35) );
        button9.setBorder(new LineBorder(Color.BLACK));
        button9.setEnabled(true);
        button9.addActionListener(buttonHandler_v2);
        
        buttonClear.setFont(font);
        buttonClear.setPreferredSize(new Dimension(35, 35) );
        buttonClear.setBorder(new LineBorder(Color.BLACK));
        buttonClear.setEnabled(true);
        buttonClear.addActionListener(clearButtonHandler);
        
        buttonClearEntry.setFont(font);
        buttonClearEntry.setPreferredSize(new Dimension(35, 35) );
        buttonClearEntry.setBorder(new LineBorder(Color.BLACK));
        buttonClearEntry.setEnabled(true);
        buttonClearEntry.addActionListener(clearEntryButtonHandler);
        
        buttonDelete.setFont(font);
        buttonDelete.setPreferredSize(new Dimension(35, 35) );
        buttonDelete.setBorder(new LineBorder(Color.BLACK));
        buttonDelete.setEnabled(true);
        buttonDelete.addActionListener(deleteButtonHandler);
        
        buttonDot.setFont(font);
        buttonDot.setPreferredSize(new Dimension(35, 35) );
        buttonDot.setBorder(new LineBorder(Color.BLACK));
        buttonDot.setEnabled(true);
        LOGGER.info("Finished. Leaving setupButtonsAndSuch()");
	}

    public void performBasicCalculatorNumberButtonActions(String buttonChoice) {
	    LOGGER.info("Starting basic calculator number button actions");
        if (firstNumBool == true) {
            performNumberButtonActions(buttonChoice);
        }
        else { // do for second number
            if (dotButtonPressed == false) {
                textArea.setText("");
                textarea = new StringBuffer().append(textArea.getText());
                if (firstNumBool == false) {
                    firstNumBool = true;
                    numberIsNegative = false;
                } else
                    dotButtonPressed = true;
                buttonDot.setEnabled(true);
            }
            performNumberButtonActions(buttonChoice);
        }
    }

    public void performProgrammerCalculatorNumberButtonActions(String buttonChoice) {
	    LOGGER.info("Starting programmer calculator number button actions");
	    if (firstNumBool) {
            performProgrammerNumberButtonActions(buttonChoice);
        }
	    else {
	        firstNumBool = true;
	        textArea.setText("");
	        textarea = new StringBuffer();
	        performProgrammerNumberButtonActions(buttonChoice);
        }
    }

    public void performNumberButtonActions(String buttonChoice) {
        performInitialChecks();
        clearNewLineFromTextArea();
        updateTextareaFromTextArea();
        LOGGER.info("Performing basic actions...");
        if (!numberIsNegative && dotButtonPressed == false) {
            LOGGER.info("firstNumBool = true | positive number = true & dotButtonPressed = false");
            LOGGER.debug("before: " + textArea.getText());
            if (textArea.getText().equals("")) {
                textArea.setText("\n" + buttonChoice);
                setValuesToTextAreaValue();
                textArea.setText("\n" + textArea.getText());
            }
            else {
                textArea.setText("\n" + textArea.getText() + buttonChoice); // update textArea
                setValuesToTextAreaValue();
                textArea.setText("\n" + textArea.getText());
            }

        }
        else if (isNegativeNumber(values[valuesPosition]) && dotButtonPressed == false) { // logic for negative numbers
            LOGGER.info("firstNumBool = true | negative number = true & dotButtonPressed = false");
            setTextareaToValuesAtPosition(buttonChoice);
        }
        else {
            LOGGER.info("firstNumBool = true & dotButtonPressed = true");
            performLogicForDotButtonPressed(buttonChoice);
        }
    }

    public void performProgrammerNumberButtonActions(String buttonChoice) {
	    performInitialChecks();
	    LOGGER.info("Performing programmer actions...");
	    if (getTextArea().getText().length() > getBytes()) {
	        return; // don't allow length to get any longer
        }
        clearNewLineFromTextArea();
        if (textArea.getText().equals("")) {
            textArea.setText(addNewLineCharacters(1) + buttonChoice);
        } else {
            textArea.setText(addNewLineCharacters(1) + textArea.getText() + buttonChoice); // update textArea
        }
        setValuesToTextAreaValue();
    }

    public String addNewLineCharacters(int number) {
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<number; i++) {
	        sb.append("\n");
        }
	    return String.valueOf(sb);
    }

    public void clearNewLineFromTextArea() {
        textArea.setText(textArea.getText().replaceAll("\n", ""));
    }

    public void updateTextareaFromTextArea() {
        textarea = new StringBuffer().append(textArea.getText());
    }

    public void performLogicForDotButtonPressed(String buttonChoice) {
        if (!textarea.equals("") && dotButtonPressed) {
            textarea.append(values[valuesPosition] + buttonChoice);
            textArea.setText("\n" + textarea );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = textArea.getText().replaceAll("\n", "");
        } else if (!textarea.equals("") && !dotButtonPressed) {
            textarea.append(values[valuesPosition] + buttonChoice);
            textArea.setText("\n" + textarea );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = textArea.getText().replaceAll("\n", "");
        }
    }

    public void performInitialChecks() {
	    boolean checkFound = false;
        if (memAddBool || memSubBool) {
            textArea.setText("");
            checkFound = true;
        }
	    else if (textAreaContainsBadText()) {
            textArea.setText("");
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
            checkFound = true;
        }
        else if (textArea.getText().equals("\n0") && getCalcType().equals(CalcType_v3.BASIC)) {
            LOGGER.debug("textArea equals 0 no matter the form. setting to blank.");
            textArea.setText("");
            values[valuesPosition] = "";
            firstNumBool = true;
            dotButtonPressed = false;
            checkFound = true;
        }
        else if (values[0].equals("") && !values[1].equals("")) {
            values[0] = values[1];
            values[1] = "";
            valuesPosition = 0;
            checkFound = true;
        }
        LOGGER.info("Performing initial checks.... results: " + checkFound);
    }

    public void setTextareaToValuesAtPosition(String buttonChoice) {
        textarea.append(values[valuesPosition]);
        LOGGER.debug("textarea: '", textarea + "'");
        textarea.append(textarea + buttonChoice); // update textArea
        LOGGER.debug("textarea: '" + textarea + "'");
        values[valuesPosition] = textarea.toString(); // store textarea
        textArea.setText("\n" + convertToPositive(textarea.toString()) + "-");
        LOGGER.debug("textArea: '" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    public void setValuesToTextAreaValue() {
        clearNewLineFromTextArea();
        updateTextareaFromTextArea();
        values[valuesPosition] = textarea.toString(); // store textarea in values
        LOGGER.debug("textArea: '\\n" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    /**
     * Resets all operators to the given boolean argument
     *
     * @param operatorBool
     * @return
     *
     * Fully tested
     */
	public boolean resetOperator(boolean operatorBool) {
        if (operatorBool == true) {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("values[0]: '" + values[0] + "'");
            valuesPosition = 0;
            dotButtonPressed = false;
            firstNumBool = true;
            return false;
        } else {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            valuesPosition = 0;
            dotButtonPressed = false;
            firstNumBool = true;
            return true;
        }
    }

    /**
     *  Returns the results of the last action
     *
     * TODO: Test
     */
    public void confirm() {
	    confirm("");
    }

    /**
     *  Returns the results of the last action with a specific message to display
     *
     * @param message a message to send into the confirm results view
     *
     * TODO: Test
     */
    public void confirm(String message) {
        if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
        else { LOGGER.info("Confirm Results"); }
        LOGGER.info("---------------- ");
        LOGGER.info("textarea: '\\n"+textarea.toString().replaceAll("\n", "")+"'");
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
        LOGGER.info("values["+0+"]: '" + values[0] + "'");
        LOGGER.info("values["+1+"]: '" + values[1] + "'");
        LOGGER.info("values["+2+"]: '" + values[2] + "'");
        LOGGER.info("valuesPosition: '"+valuesPosition+"'"); 
        LOGGER.info("memoryPosition: '"+memoryPosition+"'");
        // TODO: print out all values in memory
        LOGGER.info("firstNumBool: '"+firstNumBool+"'"); 
        LOGGER.info("dotButtonPressed: '"+dotButtonPressed+"'");
        LOGGER.info("isNegative: '"+numberIsNegative+"'");
        LOGGER.info("calcType: '" + calcType + "'");
        LOGGER.info("-------- End Confirm Results --------\n");
    }

    /**
     * This method returns all the values in memory which are not blank
     *
     * @param someValues
     * @return
     *
     * TODO: Test
     */
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
	
    /**
     * This method resets the 4 main operators to the boolean you pass in
     *
     * Fully tested
     */
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
     *
     * @param currentNumber
     * @return updated currentNumber
     *
     * TODO: Test
     */
    protected StringBuffer clearZeroesAtEnd(String currentNumber) {
        LOGGER.info("starting clearZeroesAtEnd()");
        LOGGER.debug("currentNumber: " + currentNumber);
        textarea = new StringBuffer().append(textarea.toString().replaceAll("\n",""));
        boolean justZeroes = true;
        int index = 0;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) textarea = new StringBuffer().append(textarea);
        else {
            textarea = new StringBuffer().append(currentNumber.substring(0, index));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + String.valueOf(textarea));
        return textarea;
    }

    /**
     * Tests whether a number is a decimal or not
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public boolean isDecimal(String number) {
        boolean answer = false;
        if (number.contains(".")) answer = true;
        LOGGER.info("isDecimal("+number.replaceAll("\n","")+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is positive
     *
     * @param result
     * @return
     *
     * Fully tested
     */
    public boolean isPositiveNumber(String result) {
        boolean answer = !isNegativeNumber(result);
        LOGGER.info("isPositiveNumber("+result+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is negative
     *
     * @param result
     * @return
     *
     * Fully tested
     */
    public boolean isNegativeNumber(String result) {
        boolean answer = false;
        if (result.contains("-")) {
            answer = true;
        }
        LOGGER.info("isNegativeNumber("+result.replaceAll("\n", "")+") == " + answer);
        return answer;
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public String convertToNegative(String number) {
        LOGGER.info("convertToNegative() running");
    	LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.debug("New: "  + number);
        LOGGER.info("convertToNegative("+number+") done");
        numberIsNegative = true;
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public String convertToPositive(String number) {
    	LOGGER.info("convertToPositive() running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        LOGGER.debug("New: " + number);
        LOGGER.info("convertToPositive("+number+") running");
        numberIsNegative = false;
        return number;
    }

    /**
     * Tests whether the TextArea contains a String which shows a previous error
     *
     * @return
     *
     * TODO: Test
     */
    public boolean textAreaContainsBadText() {
        boolean result = false;
        if (textArea.getText().equals("Invalid textarea")   ||
            textArea.getText().equals("Cannot divide by 0") ||
            textArea.getText().replace("\n","").equals("Not a Number") ) {

            result = true;
        }
        return result;
    }

    /** Used for testing purposes */
    public NumberButtonHandler_v2 getNumberButtonHandler_v2() { return buttonHandler_v2; }

    class NumberButtonHandler_v2 implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("NumberButtonHandler_v2 started");
            String buttonChoice = e.getActionCommand();
            LOGGER.debug("button: " + buttonChoice);
            if (getCalcType() == CalcType_v3.BASIC) {
                performBasicCalculatorNumberButtonActions(buttonChoice);
            }
            else if (getCalcType() == CalcType_v3.PROGRAMMER) {
                performProgrammerCalculatorNumberButtonActions(buttonChoice);
            }
            //TODO: add more types here
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            confirm("NumberButtonHandler_v2() finishing");
        }
    }

    //@Deprecated
    /** Used for testing purposes */
    /*public NumberButtonHandler getNumberButtonHandler() { return new NumberButtonHandler(); }*/

    //@Deprecated
    /**
     * This class handles the logic when a number button is pushed
     *
     * TODO: In test to phase out
     */
    /*class NumberButtonHandler implements ActionListener {
 		public void actionPerformed(ActionEvent e) {
 	        LOGGER.info("NumberButtonHandler() started");
 	        LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
 	        if (firstNumBool == true) {
 	            if (memAddBool == true || memSubBool == true) { // || valuesPosition == 0: essentially resetting value
 	                textArea.setText("");
 	            }
 	            else performInitialChecks();

                if (getCalcType() == CalcType_v3.BASIC) {
 	                // TODO: reformat. should be much more condensed
                    if (firstNumBool == true && isPositiveNumber(values[valuesPosition]) && dotButtonPressed == false) {
                        LOGGER.info("firstNumBool = true | positive number = true | dotButtonPressed = false");
                        LOGGER.info("before: " + textArea.getText().replaceAll("\n", ""));
                        textArea.setText("\n" + textArea.getText().replaceAll("\n","") + e.getActionCommand()); // update textArea
                        setValuesToTextAreaValue();
                    }
                    else if (firstNumBool == true && isNegativeNumber(values[valuesPosition]) && dotButtonPressed == false) { // logic for negative numbers
                        LOGGER.info("firstNumBool = true | negative number = true | dotButtonPressed = false");
                        setTextareaToValuesAtPosition(e);
                    }
                    else { // dotPressed = true
                        LOGGER.info("firstNumBool = true | positive number = true | dotButtonPressed = true");
                        performLogicForDotButtonPressed(e);
                    }
                }
 	            else if (getCalcType() == CalcType_v3.PROGRAMMER) {
 	                // Calculator needs to know about programmer booleans
 	                int appropriateLength = getBytes();
 	                textarea.append(textArea.getText().replaceAll("\n", ""));
 	                if (textarea.length() == appropriateLength) {
 	                    // do nothing. do not append number
                    } else {
 	                    textarea.append(textarea + e.getActionCommand());
 	                    textArea.setText("\n" + textarea);
                    }
                }
 	        } 
 	        else { // do for second number
 	            if (firstNumBool == false && dotButtonPressed == false) {
                    LOGGER.info("Resetting textArea");
                    textArea.setText("");
                    textarea.append("").append(textArea.getText());
                    if (firstNumBool == false) {
                        firstNumBool = true;
                        numberIsNegative = false;
                    }
 	                else
 	                    dotButtonPressed = true;
 	                buttonDot.setEnabled(true);
 	            } 
 	            *//* if (textarea.equals(".0"))  {
 	                textArea.setText("\n" + "0.");
 	                textarea = "0.";
 	            } else *//*
 	            if (textarea.equals("0")) {
 	            	values[valuesPosition] = "";
 	                textArea.setText("\n");
 	                valuesPosition = 1;
 	                firstNumBool = true;
 	                dotButtonPressed = false;
                    dotButtonPressed = false;
                    numberIsNegative = false;
 	            } else if (values[0].equals("") && !values[1].equals("") && negatePressed == false) {
 	                values[0] = values[1];
 	                values[1] = "";
 	                valuesPosition = 1;
 	            }
 	            if (firstNumBool == false && negatePressed == false) {
 	            	textArea.setText("\n " + textarea.toString().replaceAll("\n", "") + e.getActionCommand());
 	                values[valuesPosition] = textArea.getText(); // store textarea
 	                LOGGER.info("textArea: '", textArea.getText() + "'");
 	                LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'"); // print out stored textarea confirmation
 	            } else if (firstNumBool == true && negatePressed == true) { 
 	            	// we did something such as 9 + -
 	            	// indicating that the second number will be negative
 	            	//textArea.setText(e.getActionCommand()+"-");
 	            	textArea.setText("\n " + e.getActionCommand() + "-" + textarea.toString().replaceAll("\n", ""));
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
 	        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
 	        LOGGER.info("NumberButtonHandler() finished");
 	        confirm();    
 	    }
 	}*/

 	/** Used for testing purposes */
 	public ClearButtonHandler getClearButtonHandler() { return new ClearButtonHandler(); }

    /**
     * The class which handles the logic when the clear button is pushed
     *
     * Fully tested
     */
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

    /**
     * Clears the calculator
     *
     * Fully tested
     */
    public void clear() {
        LOGGER.info("Inside clear()");
        // firstNum/total, secondNum, copy/paste
        for (int i=0; i < 3; i++) {
            values[i] = "";
        }
        textArea.setText("\n0");
        textarea = new StringBuffer();
        resetOperators(false);
        valuesPosition = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotButtonPressed = false;
        buttonDot.setEnabled(true);

    }

    /** Used for testing purposes */
    public ClearEntryButtonHandler getClearEntryButtonHandler() { return new ClearEntryButtonHandler(); }

    /**
     * The class which handles the logic when the clear entry button is pushed
     *
     * Tested
     */
 	class ClearEntryButtonHandler implements ActionListener { 
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("ClearEntryButtonHandler() started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textarea = new StringBuffer();
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
            textarea.append(textArea.getText());
            LOGGER.info("ClearEntryButtonHandler() finished");
            confirm();
        }
    }

    /** Used for testing purposes */
    public DeleteButtonHandler getDeleteButtonHandler() { return new DeleteButtonHandler(); }

    /**
     * The class which handles the logic for when the delete button is clicked
     *
     * Fully tested
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

            LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
            LOGGER.info("textarea: " + textarea);
            numberIsNegative = isNegativeNumber(values[valuesPosition]);
            // this check has to happen
            dotButtonPressed = isDecimal(textarea.toString());

            clearNewLineFromTextArea();
            updateTextareaFromTextArea();
            if (addBool == false && subBool == false && mulBool == false && divBool == false) {
                if (numberIsNegative == false) {
                    // if no operator has been pushed; number is positive; number is whole
                    if (getDotButtonPressed() == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = new StringBuffer().append("0");
                        }
                        else if (textarea.length() >= 2) { // ex: 56 or 2346

                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        }
                        LOGGER.info("output: '" + textarea.toString().replaceAll("\n","") + "'");
                        values[valuesPosition] = textarea.toString();
                        getTextArea().setText("\n"+textarea);
                    }
                    // if no operator has been pushed; number is positive; number is decimal
                    else if (getDotButtonPressed()) {
                        if (textarea.length() == 2) { // ex: 3. .... recall textarea looks like .3
                            textarea = new StringBuffer().append(textarea.substring(textarea.length()-1)); // ex: 3
                            dotButtonPressed = false;
                            //dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 2)); // ex: 3 or 0
                            dotButtonPressed = false;
                            //dotActive = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50 or 5.02
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 1)); // inclusive
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
                        values[valuesPosition] = textarea.toString();
                    }
                }
                else if (numberIsNegative) {
                    // if no operator has been pushed; number is negative; number is whole
                    if (dotButtonPressed == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = new StringBuffer();
                            textArea.setText(textarea.toString());
                            values[valuesPosition] = "";
                        }
                        else if (textarea.length() >= 3) { // ex: -32 or + 6-
                            textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("output: " + textarea);
                    }
                    // if no operator has been pushed; number is negative; number is decimal
                    else if (dotButtonPressed == true) {
                        if (textarea.length() == 4) { // -3.2
                            textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2
                            textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3
                            dotButtonPressed = false;
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
                            textarea.append(convertToPositive(textarea.toString())); // 3.00 or 0.00
                            textarea.append(textarea.substring(0, textarea.length())); // 3.0 or 0.0
                            textarea.append(clearZeroesAtEnd(textarea.toString())); // 3 or 0
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("output: " + textarea);
                    }
                }
                
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                if (numberIsNegative == false) {
                    // if an operator has been pushed; number is positive; number is whole
                    if (dotButtonPressed == false) {
                        if (textarea.length() == 1) { // ex: 5
                            textarea = new StringBuffer();
                        } else if (textarea.length() == 2) {
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        } else if (textarea.length() >= 2) { // ex: 56 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = new StringBuffer().append(values[valuesPosition]);
                        		addBool = false;
                        		subBool = false;
                        		mulBool = false;
                        		divBool = false;
                        	} else {
                        		textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        	}
                        } 
                        LOGGER.info("output: " + textarea);
                        textArea.setText("\n" + textarea);
                        values[valuesPosition] = textarea.toString();
                        confirm();
                    }
                    // if an operator has been pushed; number is positive; number is decimal
                    else if (dotButtonPressed == true) {
                        if (textarea.length() == 2) // ex: 3.
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        else if (textarea.length() == 3) { // ex: 3.2 0.0
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // 3 or 0
                            dotButtonPressed = false;
                            dotButtonPressed = false;
                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = new StringBuffer().append(values[valuesPosition]);
                        	} else {
                        		textarea = new StringBuffer().append(textarea.substring(0, textarea.length() -1));
                                textarea.append(clearZeroesAtEnd(textarea.toString()));
                        	}
                        }
                        LOGGER.info("output: " + textarea);
                        textArea.setText("\n"+textarea);
                        values[valuesPosition] = textarea.toString();
                        confirm();
                    }
                } else if (numberIsNegative == true) {
                    // if an operator has been pushed; number is negative; number is whole
                    if (dotButtonPressed == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = new StringBuffer();
                            textArea.setText(textarea.toString());
                            values[valuesPosition] = "";
                        } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea.append(values[valuesPosition]);
                        	}
                        	textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                            textArea.setText("\n" + textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                        LOGGER.info("textarea: " + textarea);
                        confirm();
                    }
                    // if an operator has been pushed; number is negative; number is decimal
                    else if (dotButtonPressed == true) {
                        if (textarea.length() == 4) { // -3.2
                            textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2 or 0.0
                            textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3 or 0
                            dotButtonPressed = false;
                            dotButtonPressed = false;
                            textArea.setText(textarea + "-"); // 3- or 0-
                            values[valuesPosition] = "-" + textarea; // -3 or -0
                        } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
                            textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.25 or 0.00
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length())); // 3.2 or 0.0
                            textarea = clearZeroesAtEnd(textarea.toString());
                            LOGGER.info("textarea: " + textarea);
                            if (textarea.equals("0")) {
                                textArea.setText(textarea.toString());
                                values[valuesPosition] = textarea.toString();
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

    /** Used for testing purposes */
    public DotButtonHandler getDotButtonHandler() { return new DotButtonHandler(); }

    /**
     * The class which handles the logic when the dot button is clicked
     */
    class DotButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            LOGGER.info("DotButtonHandler() started");
            LOGGER.info("button: " + event.getActionCommand()); // print out button confirmation
            textarea.delete(0, textarea.length()-1).append(getTextArea().getText().replaceAll("\n",""));
            if (StringUtils.isBlank(textarea)) {
                textarea.append("0.");
                getTextArea().setText("\n.0");
            } else {
                getTextArea().setText("\n."+textarea);
                textarea.append(textarea.toString() + ".");
            }
            values[valuesPosition] = textarea.toString();
            buttonDot.setEnabled(false);
            setDotButtonPressed(true);
            LOGGER.info("DotButtonHandler() finished");
            confirm();
        }
    }
    
    /**
     * Calls createImageIcon(String path, String description
     *
     * @throws Exception
     */
    public ImageIcon createImageIcon(String path) throws Exception {
    	return createImageIcon(path, "No description given.");
    }
    
    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @throws Exception
     */
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

    /**
     * Adds the components to the container
     *
     * @param c
     * @param row
     * @param column
     * @param width
     * @param height
     */
    public void addComponent(Component c, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public int getBytes() {
        if (isButtonByteSet) { return 8; }
        else if (isButtonWordSet) { return 16; }
        else if (isButtonDwordSet) { return 32; }
        else if (isButtonQwordSet) { return 64; }
        else { return 0; } // shouldn't ever come here
    }

}


