package version3;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;


public class StandardCalculator_v3 extends Calculator_v3 {

	private static final long serialVersionUID = 1L;
	
	// All standard calculators have:
	final protected AddButtonHandler addButtonHandler = new AddButtonHandler();
	final protected JButton buttonAdd = new JButton("+");
	final protected SubtractButtonHandler subButtonHandler = new SubtractButtonHandler();
	final protected JButton buttonSub = new JButton("-");
	final protected MultiplyButtonHandler mulButtonHandler = new MultiplyButtonHandler();
    final protected JButton buttonMultiply = new JButton("*");
    final protected DivideButtonHandler divButtonHandler = new DivideButtonHandler();
    final protected JButton buttonDivide = new JButton("/");
    final protected EqualsButtonHandler equalsButtonHandler = new EqualsButtonHandler();
    final protected JButton buttonEquals = new JButton("=");
    
    final String negate = "\u00B1";
    final protected NegateButtonHandler negButtonHandler = new NegateButtonHandler();
    final protected JButton buttonNegate = new JButton(negate);
    // moving up protected boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false;
    private Calculator_v3 calculator;
    public Calculator_v3 getCalculatorImage1() { return calculator; }
    private void setCalculator(Calculator_v3 calculator) { this.calculator = calculator; }
    
    // 4 types of Standard Calculators: create getters and setters
    protected JPanel currentPanel = null;
    protected JPanel panelBasic = new JPanelBasic_v3(this), panelProgrammer = new JPanelProgrammer_v3(), panelScientific = new JPanelScientific_v3(), panelDate = new JPanelDate_v3();
	public JPanel getCurrentJPanel() { return currentPanel; }
	
	/*
	 * TODO: Change method to be a list of tasks that should
	 * occur when we switch panels.
	 * 
	 * method: performTasksWhenChangingJPanels
	 * 
	 * tasks: set currentPanel
	 * 		  set title of frame
	 */
	public void performTasksWhenChangingJPanels(JPanel currentPanel, String title) {
		setCurrentJPanel(currentPanel);
		// TODO: implement changing frame title based upon jpanel
	}
	
	public void setCurrentJPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    
	public StandardCalculator_v3() throws HeadlessException {
		// TODO Auto-generated constructor stub
	
	}

	public StandardCalculator_v3(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	// TODO: Implement first
	public StandardCalculator_v3(String title) throws HeadlessException {
		super(Calculator_v3.BASIC); // default title is Basic
		setCalculator(this);
		setCurrentJPanel(panelBasic);
		setupStandardCalculator_v3();
		setImageIcons();
		//setupMenuBar();
		
		// adding the buttons is done in the panels
		setMinimumSize(getCurrentPanel().getSize());
		
	}

	public StandardCalculator_v3(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}
	
	public void setupStandardCalculator_v3() {
		buttonAdd.setFont(font);
        buttonAdd.setPreferredSize(new Dimension(35, 35) );
        buttonAdd.setBorder(new LineBorder(Color.BLACK));
        buttonAdd.setEnabled(true);
        buttonAdd.addActionListener(addButtonHandler);
        
        buttonSub.setFont(font);
        buttonSub.setPreferredSize(new Dimension(35, 35) );
        buttonSub.setBorder(new LineBorder(Color.BLACK));
        buttonSub.setEnabled(true);
        buttonSub.addActionListener(subButtonHandler);

        buttonMultiply.setFont(font);
        buttonMultiply.setPreferredSize(new Dimension(35, 35) );
        buttonMultiply.setBorder(new LineBorder(Color.BLACK));
        buttonMultiply.setEnabled(true);
        buttonMultiply.addActionListener(mulButtonHandler);
        
        buttonDivide.setFont(font);
        buttonDivide.setPreferredSize(new Dimension(35, 35) );
        buttonDivide.setBorder(new LineBorder(Color.BLACK));
        buttonDivide.setEnabled(true);
        buttonDivide.addActionListener(divButtonHandler);
        
        buttonEquals.setFont(font);
        buttonEquals.setPreferredSize(new Dimension(35, 70) );
        buttonEquals.setBorder(new LineBorder(Color.BLACK));
        buttonEquals.setEnabled(true);
        buttonEquals.addActionListener(equalsButtonHandler);
        
        buttonNegate.setFont(font);
        buttonNegate.setPreferredSize(new Dimension(35, 35) );
        buttonNegate.setBorder(new LineBorder(Color.BLACK));
        buttonNegate.setEnabled(true);
        //buttonNegate.addActionListener(negButtonHandler);
        
        add(getCurrentPanel());
	}
	
	public void add() {
    	LOGGER.info("temp[0]: '" + values[0] + "'");
    	LOGGER.info("temp[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        values[0] = Double.toString(result);
        LOGGER.info("addBool: "+addBool);
        LOGGER.info("subBool: "+subBool);
        LOGGER.info("mulBool: "+mulBool);
        LOGGER.info("divBool: "+divBool);
        if (result % 1 == 0) {
        	LOGGER.info("We have a whole number");
        	textarea = Double.toString(result);
            values[0] = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
            textArea.setText("\n" + values[0]);
            if (Integer.parseInt(values[0]) < 0 ) {
                textarea = textArea.getText(); // temp[valuesPosition]
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = textarea.substring(1, textarea.length());
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n "+textarea.replaceAll("-", "") + "-"); // update textArea
            }
            dotActive = false;
            buttonDot.setEnabled(true);
        } else { // if double == double, keep decimal and number afterwards
        	LOGGER.info("We have a decimal");
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = values[0];
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = textarea.substring(1, textarea.length());
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"] '" + values[valuesPosition] + "'");
            } else {
                textArea.setText("\n" + formatNumber(values[0]));
                values[0] = formatNumber(values[0]);
            }
        }
    }
    
    public void subtract() {
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0) {
            //textArea.setText(temp[0]);
            textarea = Double.toString(result);
            textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
            values[0] = textarea; // update storing
            textArea.setText("\n" + values[0]);
            if (Integer.parseInt(values[0]) < 0 ) {
                //textarea = textArea.getText(); 
                //System.out.printf("\n%s", textarea);
                textarea = textarea.substring(1, textarea.length());
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea.replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
                LOGGER.info("temp[0]: " + values[0]);
            }
        } else {// if double == double, keep decimal and number afterwards
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                textarea = values[0];
                LOGGER.info("textarea: " + textarea);
                textarea = textarea.substring(1, textarea.length());
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            } else {
                textArea.setText("\n" + formatNumber(values[0]));
            }
        }
    }
    
    public void multiply() {
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0) {
            textArea.setText(values[0]);
            textarea = textArea.getText().replaceAll("\\n", "");
            textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
            values[0] = textarea; // update storing
            textArea.setText("\n" + values[0]);
            if (Integer.parseInt(values[0]) < 0 ) {
                textarea = textArea.getText().replaceAll("\\n", ""); // temp[2]
                LOGGER.info("textarea: " + textarea);
                textarea = textarea.substring(1, textarea.length());
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            }
        } else {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
    }
    
    public void divide() {
        if (!values[1].equals("0")) { 
            // if the second number is not zero, divide as usual
            double result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info(values[0] + " / " + values[1] + " = " + result);
            values[0] = Double.toString(result); // store result
            LOGGER.info("addBool: " + addBool);
            LOGGER.info("subBool: " + subBool);
            LOGGER.info("mulBool: " + mulBool);
            LOGGER.info("divBool: " + divBool);
            if (Double.parseDouble(values[0]) % 1 == 0) {
                // if int == double, cut off decimal and zero
                textArea.setText("\n" + values[0]);
                textarea = textArea.getText().replaceAll("\\n", "");
                textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
                values[0] = textarea; // update storing
                textArea.setText("\n" + values[0]);
                if (Integer.parseInt(values[0]) < 0 ) {
                    textarea = textArea.getText().replaceAll("\\n", ""); // temp[2]
                    LOGGER.info("textarea: " + textarea);
                    textarea = textarea.substring(1, textarea.length());
                    LOGGER.info("textarea: " + textarea);
                    textArea.setText("\n" + textarea + "-"); // update textArea
                    LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
                }
            } else {
                // if double == double, keep decimal and number afterwards
                textArea.setText("\n" + formatNumber(values[0]));
            }
        } else if (values[1].equals("0")) {
            String result = "0";
            LOGGER.info("Attempting to divide by zero. Cannog divide by 0!");
            textArea.setText("Cannot divide by 0");
            values[0] = result;
            firstNumBool = true;
        }
    }

    // used to keep 2 decimals on the number at all times
    public String formatNumber(String num) {
        DecimalFormat df = new DecimalFormat("0.00"); 
        double number = Double.parseDouble(num);
        number = Double.valueOf(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }
    
    /*public boolean isNegativeNumber(String result) {
    	LOGGER.info("isNegativeNumber() running");
        boolean answer = false;
        if (result.contains("-")) { // if int == double, cut off decimal and zero
            answer = true;
        }
        return answer;
    }*/
    
    /*public String convertToNegative(String number) {
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
    }*/
	
	class AddButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("AddButtonHandler started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") 
            		&& !textArea.getText().equals("Cannot divide by 0")) {
                //textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText().replaceAll("\\n", "")); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("values["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
                if (memorySwitchBool == true) {
                	memoryValues[memoryPosition] += " + ";
                }
            } else if (addBool == true && !values[1].equals("")) {
                add(); //addition();
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
            } else if (subBool == true && !values[1].equals("")) {
                subtract();
                subBool = resetOperator(subBool);
                addBool = true;
            } else if (mulBool == true && !values[1].equals("")) {
                multiply();
                mulBool = resetOperator(mulBool);
                addBool = true;
            } else if (divBool == true && !values[1].equals("")) {
                divide();
                divBool = resetOperator(divBool);
                addBool = true;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	// subBool can be true because it can be a negative number
            	LOGGER.info("already chose an operator. choose another number.");
            } 
            
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }

	class SubtractButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("SubtractButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                add();
                addBool = resetOperator(addBool);
                subBool = true;
            } else if (subBool == true && !values[1].equals("")) {
                subtract();
                subBool = resetOperator(subBool);
                subBool = true;
            } else if (mulBool == true && !values[1].equals("")) {
                multiply();
                mulBool = resetOperator(mulBool);
                subBool = true;
            } else if (divBool == true && !values[1].equals("")) {
                divide();
                divBool = resetOperator(divBool);
                subBool = true;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	LOGGER.info("already chose an operator. next number is negative...");
            	negatePressed = true;
        	}
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }

	class MultiplyButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MultiplyButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                add(); // addition();
                addBool = resetOperator(addBool);
                mulBool = true;
            } else if (subBool == true && !values[1].equals("")) {
                subtract();
                subBool = resetOperator(subBool);
                mulBool = true;
            } else if (mulBool == true && !values[1].equals("")) {
                multiply();
                mulBool = resetOperator(mulBool);
                mulBool = true;
            } else if (divBool == true && !values[1].equals("")) {
                divide();
                divBool = resetOperator(divBool);
                mulBool = true;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	LOGGER.info("already chose an operator. choose another number.");
            	
        	}
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }

	class DivideButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("DivideButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                add(); // addition();
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
            } else if (subBool == true && !values[1].equals("")) {
                subtract();
                subBool = resetOperator(subBool);
                divBool = true;
            } else if (mulBool == true && !values[1].equals("")) {
                multiply();
                mulBool = resetOperator(mulBool);
                divBool = true;
            } else if (divBool == true && !values[1].equals("") & !values[1].equals("0")) {
                divide();
                divBool = resetOperator(divBool);
                divBool = true;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	LOGGER.info("already chose an operator. choose another number.");
            }
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }
	
	class EqualsButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("EqualsButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == true) {
                add(); // addition();
                addBool = resetOperator(addBool);
                valuesPosition = 0;
            } 
            else if (subBool == true){
                subtract();
                subBool = resetOperator(subBool);
                valuesPosition = 0;
            } 
            else if (mulBool == true){
                multiply();
                mulBool = resetOperator(mulBool);
                valuesPosition = 0;
            } 
            else if (divBool == true){
                divide();
                divBool = resetOperator(divBool);
                valuesPosition = 0;
            } else if (values[0].equals("") && values[1].equals("")) {
                // if temp[0] and temp[1] do not have a number
                valuesPosition = 0;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[valuesPosition]); // "userInput +" // temp[valuesPosition]
                valuesPosition = 1;
                firstNumBool = true;
            } 
            values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
            textarea = textArea.getText();
            firstNumBool = true;
            dotActive = false;
            confirm();
        }
    }
	
	// NegateButtonHandler operates on this button: 
    // final private JButton buttonNegate = new JButton("\u00B1");
    class NegateButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("NegateButtonHandler started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textarea = textArea.getText();
            //textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if (!textArea.getText().equals("")) { 
            // if there is a number in the text area
                if (isNegativeNumber(values[valuesPosition]) == true) { //temp[valuesPosition].substring(0, 1).equals("-")
                // if there is already a negative sign
                	LOGGER.info("Reversing number back to positive");
                    values[valuesPosition] = convertToPositive(values[valuesPosition]);
                    textArea.setText("\n" + values[valuesPosition]);
                } else  {
                // whether first or second number, add "-"
                    values[valuesPosition] = textArea.getText().replaceAll("\n", ""); // textarea
                    textArea.setText("\n" + values[valuesPosition] + "-");
                    values[valuesPosition] = convertToNegative(values[valuesPosition]); 
                    
                    textarea = textarea + "-";
                } 
            }
            firstNumBool = true;
            confirm();    
        }
    }

	public JPanel getCurrentPanel() {
		return currentPanel;
	}

    
}




