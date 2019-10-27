package version3;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// The face for a basic calculator
public class JPanelBasic_v3 extends JPanel {
	
    private static final long serialVersionUID = 1L;
    protected final static Logger LOGGER;
    
    private GridBagLayout basicLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    
    final private FractionButtonHandler fractionButtonHandler = new FractionButtonHandler();
    final private JButton buttonFraction = new JButton("1/x");
    
    final private PercentButtonHandler perButtonHandler = new PercentButtonHandler();
    final private JButton buttonPercent = new JButton("%");
    
    final private String SQRT = "\u221A";
    final private SquareRootButtonHandler sqrtButtonHandler = new SquareRootButtonHandler();
    final private JButton buttonSqrt = new JButton(SQRT);

    // TODO: Move MemorySuite into Calculator_v3
    final private MemoryClearButtonHandler memoryClearButtonHandler = new MemoryClearButtonHandler();
    final private JButton buttonMC = new JButton("MC");
    
    final private MemoryRecallButtonHandler memoryRecallButtonHandler = new MemoryRecallButtonHandler();
    final private JButton buttonMR = new JButton("MR");
    
    final private MemoryStoreButtonHandler memoryStoreButtonHandler = new MemoryStoreButtonHandler();
    final private JButton buttonMS = new JButton("MS");
    
    final private MemoryAddButtonHandler memoryAddButtonHandler = new MemoryAddButtonHandler();
    final private JButton buttonMA = new JButton("M+");

    final private MemorySubButtonHandler memorySubButtonHandler = new MemorySubButtonHandler();
    final private JButton buttonMSub = new JButton("M-");
    
    final private int memoryPosition = 0;
    final private String[] memoryValues = {"", "", "", "", ""}; // holds last 5 operations
    
//    private boolean firstNumBool = true;
//    private boolean numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false;
//    private boolean decimal = false;
    
    private StandardCalculator_v3 calculator;
    public Calculator_v3 getCalculator() { return calculator; }
    private void setCalculator(StandardCalculator_v3 calculator) { this.calculator = calculator; }


    static {
        System.setProperty("appName", "Calculator");
        LOGGER = LogManager.getLogger(JPanelBasic_v3.class);
    }
    
    public JPanelBasic_v3(StandardCalculator_v3 calculator) {
        setMinimumSize(new Dimension(100,200));
        basicLayout = new GridBagLayout();
        setLayout(basicLayout); // set frame layout
        constraints = new GridBagConstraints(); // instanitate constraints
        setupPanel_v3();
        setCalculator(calculator);
        addComponentsToPanel_v3();
        //performCalculatorTypeSwitchOperations();
    }

//    public static void performBasicSetup(Calculator_v3 calculator) {
//        LOGGER.info("Preparing programmer buttons");
//
//        //setAllNumberButtons(true);
//        LOGGER.info("Finished preparing buttons.");
//    }

    // prepare panel's specific objects
	public void setupPanel_v3() {
        LOGGER.info("Starting setupPanel_v3");
        constraints.insets = new Insets(5,5,5,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
        try {
            calculator.getTextArea().setPreferredSize(new Dimension(70, 35));
            setAllNumberButtons(true);
            calculator.button0.setFont(calculator.font);
            calculator.button0.setPreferredSize(new Dimension(70, 35) );
            calculator.button0.setBorder(new LineBorder(Color.BLACK));

            calculator.button1.setFont(calculator.font);
            calculator.button1.setPreferredSize(new Dimension(35, 35) );
            calculator.button1.setBorder(new LineBorder(Color.BLACK));

            calculator.button2.setFont(calculator.font);
            calculator.button2.setPreferredSize(new Dimension(35, 35) );
            calculator.button2.setBorder(new LineBorder(Color.BLACK));

            calculator.button3.setFont(calculator.font);
            calculator.button3.setPreferredSize(new Dimension(35, 35) );
            calculator.button3.setBorder(new LineBorder(Color.BLACK));

            calculator.button4.setFont(calculator.font);
            calculator.button4.setPreferredSize(new Dimension(35, 35) );
            calculator.button4.setBorder(new LineBorder(Color.BLACK));

            calculator.button5.setFont(calculator.font);
            calculator.button5.setPreferredSize(new Dimension(35, 35) );
            calculator. button5.setBorder(new LineBorder(Color.BLACK));

            calculator.button6.setFont(calculator.font);
            calculator. button6.setPreferredSize(new Dimension(35, 35) );
            calculator. button6.setBorder(new LineBorder(Color.BLACK));

            calculator.button7.setFont(calculator.font);
            calculator.button7.setPreferredSize(new Dimension(35, 35) );
            calculator.button7.setBorder(new LineBorder(Color.BLACK));

            calculator.button8.setFont(calculator.font);
            calculator.button8.setPreferredSize(new Dimension(35, 35) );
            calculator.button8.setBorder(new LineBorder(Color.BLACK));

            calculator.button9.setFont(calculator.font);
            calculator.button9.setPreferredSize(new Dimension(35, 35) );
            calculator.button9.setBorder(new LineBorder(Color.BLACK));
        } catch (NullPointerException e) {}

        buttonFraction.setFont(Calculator_v3.font);
        buttonFraction.setPreferredSize(new Dimension(35, 35) );
        buttonFraction.setBorder(new LineBorder(Color.BLACK));
        buttonFraction.setEnabled(true);
        
        buttonPercent.setFont(Calculator_v3.font);
        buttonPercent.setPreferredSize(new Dimension(35, 35) );
        buttonPercent.setBorder(new LineBorder(Color.BLACK));
        buttonPercent.setEnabled(true);
        
        buttonSqrt.setFont(Calculator_v3.font);
        buttonSqrt.setPreferredSize(new Dimension(35, 35) );
        buttonSqrt.setBorder(new LineBorder(Color.BLACK));
        buttonSqrt.setEnabled(true);
        
        buttonMC.setFont(Calculator_v3.font);
        buttonMC.setPreferredSize(new Dimension(35, 35) );
        buttonMC.setBorder(new LineBorder(Color.BLACK));
        if (memoryValues[memoryPosition].equals("")) {
            buttonMC.setEnabled(false);
        }
        
        buttonMR.setFont(Calculator_v3.font);
        buttonMR.setPreferredSize(new Dimension(35, 35) );
        buttonMR.setBorder(new LineBorder(Color.BLACK));
        if (memoryValues[memoryPosition].equals("")) {
            buttonMR.setEnabled(false);
        }
        
        buttonMS.setFont(Calculator_v3.font);
        buttonMS.setPreferredSize(new Dimension(35, 35) );
        buttonMS.setBorder(new LineBorder(Color.BLACK));
        buttonMS.setEnabled(true);
        
        buttonMA.setFont(Calculator_v3.font);
        buttonMA.setPreferredSize(new Dimension(35, 35) );
        buttonMA.setBorder(new LineBorder(Color.BLACK));
        buttonMA.setEnabled(true);
        
        buttonMSub.setFont(Calculator_v3.font);
        buttonMSub.setPreferredSize(new Dimension(35, 35) );
        buttonMSub.setBorder(new LineBorder(Color.BLACK));
        buttonMSub.setEnabled(true);
        LOGGER.info("End setupPanel_v3()");
        // above this comment is all for the buttons
    } // end method setupPanel_v3()

    // Add all components to Calculator
    public void addComponentsToPanel_v3() {
        LOGGER.info("Starting addComponentsToPanel_v3");
    	constraints.fill = GridBagConstraints.BOTH;
        addComponent(calculator.getTextArea(), 0, 0, 5, 2);
        // Also add the action listener for each button
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMC, 2, 0, 1, 1);
        buttonMC.addActionListener(memoryClearButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMR, 2, 1, 1, 1);
        buttonMR.addActionListener(memoryRecallButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMS, 2, 2, 1, 1);
        buttonMS.addActionListener(memoryStoreButtonHandler); 
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMA, 2, 3, 1, 1);
        buttonMA.addActionListener(memoryAddButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMSub, 2, 4, 1, 1);
        buttonMSub.addActionListener(memorySubButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button0, 7, 0, 2, 1);
        //calculator.button0.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDot, 7, 2, 1, 1);
        calculator.buttonDot.addActionListener(this.calculator.dotButtonHandler); 
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonAdd, 7, 3, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button1, 6, 0, 1, 1);
        //calculator.button1.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button2, 6, 1, 1, 1);
        //calculator.button2.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button3, 6, 2, 1, 1);
        //calculator.button3.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonSubtract, 6, 3, 1, 1);

        constraints.fill = GridBagConstraints.BOTH;
        addComponent(calculator.buttonEquals, 6, 4, 1, 2); // idk why its size is not showing on the application; leave a comment for me on why this is
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button4, 5, 0, 1, 1);
        //this.calculator.button4.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button5, 5, 1, 1, 1);
        //this.calculator.button5.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button6, 5, 2, 1, 1);
        //this.calculator.button6.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonMultiply, 5, 3, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonFraction, 5, 4, 1, 1);
        buttonFraction.addActionListener(fractionButtonHandler);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button7, 4, 0, 1, 1);
        //this.calculator.button7.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button8, 4, 1, 1, 1);
        //this.calculator.button8.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button9, 4, 2, 1, 1);
        //this.calculator.button9.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDivide, 4, 3, 1, 1);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPercent, 4, 4, 1, 1);
        buttonPercent.addActionListener(perButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDelete, 3, 0, 1, 1);
//        this.calculator.buttonDelete.addActionListener(this.calculator.deleteButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonClearEntry, 3, 1, 1, 1);
//        calculator.buttonClearEntry.addActionListener(calculator.clearEntryButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonClear, 3, 2, 1, 1);
//        calculator.buttonClear.addActionListener(calculator.clearButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonNegate, 3, 3, 1, 1);
//        calculator.buttonNegate.addActionListener(calculator.negButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonSqrt, 3, 4, 1, 1);
        buttonSqrt.addActionListener(sqrtButtonHandler);

        // adding equals button functionality at panel level
//        calculator.buttonAdd.addActionListener(calculator.addButtonHandler);
//        calculator.buttonSubtract.addActionListener(calculator.subtractButtonHandler);
//        calculator.buttonMultiply.addActionListener(calculator.multiplyButtonHandler);
//        calculator.buttonDivide.addActionListener(calculator.divideButtonHandler);
//        calculator.buttonEquals.addActionListener(calculator.equalsButtonHandler);
        LOGGER.info("Finished addComponentsToPanel_v3");
    }

    /**
     * The class which handles operations when the square root button is clicked
     */
    public class SquareRootButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("SquareRoot ButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            String errorStringNaN = "Not a Number";
            LOGGER.debug("text: " + calculator.getTextArea().getText().replace("\n",""));
            if (calculator.getTextArea().getText().equals("") || calculator.isNegativeNumber(calculator.getTextArea().getText())) {
                calculator.getTextArea().setText("\n"+errorStringNaN);
                calculator.textarea = new StringBuffer().append("\n"+errorStringNaN);
                calculator.confirm(errorStringNaN + "Cannot perform square root operation on blank/negative number");
            } else {
                String result = String.valueOf(Math.sqrt(Double.valueOf(calculator.getTextArea().getText())));
                result = calculator.formatNumber(result);
                calculator.getTextArea().setText("\n"+result);
                calculator.textarea = new StringBuffer().append(result);
                calculator.confirm();
            }
        }
    }

    /**
     * The class which handles the logic for when the percent button is click
     */
    public class PercentButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("PercentStoreButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!calculator.getTextArea().getText().equals("")) {
                //if(textArea.getText().substring(textArea.getText().length()-1).equals("-")) { // if the last index equals '-'
                // if the number is negative
                if (calculator.isNegativeNumber(calculator.getTextArea().getText().replaceAll("\\n", ""))) {
                	LOGGER.info("if condition true");
                    //temp[valuesPosition] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(calculator.values[calculator.valuesPosition]); 
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    calculator.values[calculator.valuesPosition] = Double.toString(percent);
                    calculator.textarea = new StringBuffer().append(calculator.formatNumber(calculator.values[calculator.valuesPosition]));
                    LOGGER.info("Old " + calculator.values[calculator.valuesPosition]);
                    calculator.values[calculator.valuesPosition] = calculator.values[calculator.valuesPosition].substring(1, calculator.values[calculator.valuesPosition].length());
                    LOGGER.info("New " + calculator.values[calculator.valuesPosition] + "-");
                    calculator.getTextArea().setText(calculator.values[calculator.valuesPosition] + "-"); // update textArea
                    LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
                    //textArea.setText(textarea);
                    calculator.values[calculator.valuesPosition] = calculator.textarea.toString();//+textarea;
                    calculator.textarea = new StringBuffer();
                    LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
                    LOGGER.info("textArea: "+calculator.getTextArea().getText());
                } else {
                    double percent = Double.parseDouble(calculator.values[calculator.valuesPosition]); 
                    percent /= 100;
                    calculator.values[calculator.valuesPosition] = Double.toString(percent);
                    calculator.getTextArea().setText("\n" + calculator.formatNumber(calculator.values[calculator.valuesPosition]));
                    calculator.values[calculator.valuesPosition] = calculator.getTextArea().getText().replaceAll("\\n", "");
                    LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
                    LOGGER.info("textArea: "+calculator.getTextArea().getText());
                }
            }
            calculator.dotButtonPressed = true;
            calculator.dotButtonPressed = true;
            calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
            calculator.confirm();
        }
    }

    /**
     * The class which handles the logic for when the FractionButton is clicked
     */
    public class FractionButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("FracStoreButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!calculator.getTextArea().getText().equals("")) {
                double result = Double.parseDouble(calculator.values[calculator.valuesPosition]);
                result = 1 / result;
                LOGGER.info("result: " + result);
                calculator.values[calculator.valuesPosition] = Double.toString(result);
                calculator.getTextArea().setText("\n" + calculator.values[calculator.valuesPosition]);
                LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
            }
            calculator.confirm();
        }
    }
    
    // TODO: Implement new logic and move to Calculator
    public class MemoryClearButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryClearButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
    		calculator.values[4] = "";
            LOGGER.info("temp[4]: " + calculator.values[4]);
            LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
            buttonMR.setEnabled(false);
            buttonMC.setEnabled(false);
            calculator.memAddBool = false;
            calculator.memSubBool = false;
            calculator.confirm();
        }
    }
    
    // TODO: Implement new logic and move to Calculator
    public class MemoryRecallButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MemoryRecallButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
        	calculator.getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        	calculator.textarea = new StringBuffer().append(calculator.values[4]);
            if (calculator.isNegativeNumber(calculator.textarea.toString())) {
            	calculator.textarea = new StringBuffer().append(calculator.convertToPositive(calculator.textarea.toString()));
            }
            LOGGER.info("textarea: -" + calculator.textarea);
            calculator.values[calculator.valuesPosition] = calculator.values[4];
            calculator.getTextArea().setText("\n" + calculator.textarea + "-"); // update textArea
            calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
            LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
            calculator.confirm();
        }
    }
    
    // TODO: Implement new logic and move to Calculator
    public class MemoryStoreButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryStoreButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if(!calculator.getTextArea().getText().equals("") && !calculator.values[calculator.valuesPosition].equals("")) { // [valuesPosition-1]
            // if text area has a number AND if storage has a number
            	calculator.values[4] = calculator.values[calculator.valuesPosition];
                LOGGER.info("textArea: '\\n" + calculator.getTextArea().getText().replaceAll("\n", "") + "'");
                LOGGER.info("temp[4]: " + calculator.values[4]);
                buttonMR.setEnabled(true);
                buttonMC.setEnabled(true);
            } else if (!calculator.values[4].equals("")) {
            // if memory does not have a number
            	calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                double result = Double.parseDouble(calculator.values[4]) + Double.parseDouble(calculator.getTextArea().getText());
                LOGGER.info("result: " + result);
                calculator.getTextArea().setText(Double.toString(result));
                calculator.values[4] = Double.toHexString(result);
            }
            calculator.confirm();
        }
    }

    // TODO: Implement new logic and move to Calculator
    public class MemoryAddButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryAddButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
    		calculator.getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if(!calculator.getTextArea().getText().equalsIgnoreCase("") && !calculator.values[4].equals("")) {
            // if text area has a number and memory storage has a number
            	LOGGER.info("textArea: " + calculator.getTextArea().getText()); // print out textArea has proper value confirmation; recall text area's orientation
            	LOGGER.info("temp[4]:(memory) is " + calculator.values[4]);
            	LOGGER.info("temp["+calculator.valuesPosition+"]:(add to) is " + calculator.values[calculator.valuesPosition]);
                double result = Double.parseDouble(calculator.values[4]) + Double.parseDouble(calculator.values[calculator.valuesPosition]); // create result forced double
                LOGGER.info(calculator.values[4] + " + " + calculator.values[calculator.valuesPosition] + " = " + result);
                calculator.values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + calculator.values[4]);
                if (result % 1 == 0) { // if int == double, cut off decimal and zero
                    //textArea.setText(temp[4]);
                	calculator.textarea = new StringBuffer().append(calculator.values[4]);
                	calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0, calculator.textarea.length()-2)); // textarea changed to whole number, or int
                	calculator.values[4] = calculator.textarea.toString(); // update storing
                    LOGGER.info("update storing: " + calculator.textarea);
                    //textArea.setText(temp[2]);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) { // if result is negative
                    	calculator.textarea = new StringBuffer().append(calculator.values[4]);
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = new StringBuffer().append(calculator.textarea.substring(1, calculator.textarea.length()));
                        LOGGER.info("textarea: " + calculator.textarea);
                        LOGGER.info("temp[4] is " + calculator.values[4]);
                       }
                } //else {// if double == double, keep decimal and number afterwards
                    //textArea.setText(temp[2]);
            } else if (calculator.getTextArea().getText().equals("")) {
            	calculator.getTextArea().setText("0");
            	calculator.values[calculator.valuesPosition] = "0";
            	calculator.values[4] = calculator.values[calculator.valuesPosition];
            } else if (!calculator.values[calculator.valuesPosition].equals("")) { // valuesPosition-1 ???
            	calculator.values[4] = calculator.values[calculator.valuesPosition];
            } else {
            	calculator.values[4] = calculator.values[calculator.valuesPosition];
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: is " + calculator.values[4] + " after memory+ pushed"); // confirming proper textarea before moving on
            calculator.dotButtonPressed = false;
            calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
            calculator.memAddBool = true;
            calculator.confirm();
        }
    }

    // TODO: Implement new logic and move to Calculator
    public class MemorySubButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemorySubButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            //textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if(!calculator.getTextArea().getText().equalsIgnoreCase("") && !calculator.values[4].equals("")) {
            // if textArea has a number and memory storage has a number
            	LOGGER.info("textArea: " + calculator.getTextArea().getText()); // print out textArea has proper value confirmation; recall text area's orientation
            	LOGGER.info("temp[%d]:(memory) %s\n", 4, calculator.values[4]);
            	LOGGER.info("temp[%d]:(sub from) %s\n", calculator.valuesPosition, calculator.values[calculator.valuesPosition]);
                double result = Double.parseDouble(calculator.values[4]) - Double.parseDouble(calculator.values[calculator.valuesPosition]); // create result forced double
                LOGGER.info(calculator.values[4] + " - " + calculator.values[calculator.valuesPosition] + " = " + result);
                calculator.values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + calculator.values[4]);
                int intRep = (int)result;
                if (intRep == result) { // if int == double, cut off decimal and zero
                	calculator.textarea = new StringBuffer().append(calculator.values[4]);
                	calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0, calculator.textarea.length()-2)); // textarea changed to whole number, or int
                	calculator.values[4] = calculator.textarea.toString(); // update storing
                    LOGGER.info("update storing: " + calculator.textarea);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) { // if result is negative
                    	calculator.textarea = new StringBuffer().append(calculator.values[4]);
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = new StringBuffer().append(calculator.textarea.substring(1, calculator.textarea.length()));
                        LOGGER.info("textarea: " + calculator.textarea);
                        LOGGER.info("temp[4]: " + calculator.values[4]);
                    }
                } 
            } else if (calculator.getTextArea().getText().equals("")) {
            	calculator.getTextArea().setText("0");
            	calculator.values[calculator.valuesPosition] = "0";
            	calculator.values[4] = calculator.values[calculator.valuesPosition];
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
                
                double result = 0.0;
                LOGGER.info("result: " + result);
                int intRep = (int)result; // 9 = 9.0
                if (intRep == result) { // if int == double, cut off decimal and zero
                	calculator.getTextArea().setText(Double.toString(result));
                	calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                    LOGGER.info("textArea: " + calculator.getTextArea().getText());
                    calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0, calculator.textarea.length()-2)); // textarea changed to whole number, or int
                    calculator.values[4] = calculator.textarea.toString(); // update storing
                    calculator.getTextArea().setText(calculator.values[4]);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) {
                    	calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText()); // temp[2]
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = new StringBuffer().append(calculator.textarea.substring(1, calculator.textarea.length()));
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.getTextArea().setText(calculator.textarea + "-"); // update textArea
                        LOGGER.info("temp["+calculator.valuesPosition+"] is " + calculator.values[calculator.valuesPosition]);
                    }
                } 
            } else if (!calculator.values[calculator.valuesPosition].equals("")) {
            	calculator.values[4] = calculator.convertToNegative(calculator.values[calculator.valuesPosition]);
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
            } else {
            	calculator.values[4] = calculator.convertToNegative(calculator.values[calculator.valuesPosition]);
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: " + calculator.values[4] + " after memory- pushed"); // confirming proper textarea before moving on
            calculator.dotButtonPressed = false;
            calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
            calculator.memSubBool = true;
            calculator.confirm();
        }
    }
    
    // method to set constraints on
    public void addComponent(Component c, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        basicLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void convertToDecimal() {
        LOGGER.info("convertToDecimal started");
        calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText().replaceAll("\n", "").strip());
        int appropriateLength = calculator.getBytes();
        LOGGER.debug("textarea: " + calculator.textarea);
        LOGGER.debug("appropriateLength: " + appropriateLength);
        String operator = null;
        boolean operatorIncluded = false;
        boolean isNumberTrulyNegative = false;
        if (calculator.textarea.length() < appropriateLength) {
            LOGGER.debug("textarea, " + calculator.textarea + ", is too short. adding missing zeroes");
            // user had entered 101, which really is 00000101
            // but they aren't showing the first 5 zeroes
            int difference = appropriateLength - calculator.textarea.length();
            StringBuffer missingZeroes = new StringBuffer();
            for (int i=0; i<difference; i++) {
                missingZeroes.append("0");
            }
            calculator.textarea = new StringBuffer().append(missingZeroes.toString() + calculator.textarea.toString());
            // should result in textarea coming from programmer calculator
            // to always have the same length as to what mode the calculator
            // was set at (binary, octal, decimal, hexidecimal)
            LOGGER.debug("textarea: " + calculator.textarea);
        }
        else if (calculator.textarea.length() > appropriateLength) // user may have pushed an operator
        {
            // text looks like + 00001010
            operator = String.valueOf(calculator.textarea.charAt(0));
            switch(operator) {
                case "+" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.addBool = true; break;
                case "-" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.subBool = true; break;
                case "*" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.mulBool = true; break;
                case "/" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.divBool = true; break;
                default : LOGGER.error("unknown string means there is another else if case: " + operator);
            }
            calculator.textarea = new StringBuffer().append(String.valueOf(calculator.textarea).substring(2,calculator.textarea.length()));
        }

        double result = 0.0;
        double num1 = 0.0;
        double num2 = 0.0;
        for(int i=0, k=appropriateLength-1; i<appropriateLength; i++, k--) {
            num1 = Double.valueOf(String.valueOf(calculator.textarea.charAt(i)));
            num2 = Math.pow(2,k);
            result = (num1 * num2) + result;
        }
        if (operatorIncluded && StringUtils.isNotBlank(operator)) {
            calculator.textarea = new StringBuffer().append(operator).append(" ").append(Double.toString(result));
        }
        else calculator.textarea = new StringBuffer().append(Double.toString(result));
        LOGGER.info("textarea: " + calculator.textarea);
        if (calculator.isDecimal(calculator.textarea.toString())) {
            calculator.textarea = calculator.clearZeroesAtEnd(calculator.textarea.toString());
        }
        calculator.getTextArea().setText("\n" + calculator.textarea);
        LOGGER.info("textarea: " + calculator.textarea);
        LOGGER.info("convertToDecimal finished");
    }

    public void convertTextArea() {
        LOGGER.info("Converting TextArea");
        if (calculator.getCalcType() == CalcType_v3.PROGRAMMER) {
            LOGGER.info("Going from binary to decimal...");
            convertToDecimal();
        } else {
            LOGGER.info("Current CalcType is: " + calculator.getCalcType());
        }
        LOGGER.info("TextArea converted");
    }

    /**
     * This method handles the logic when we switch from any type of calculator
     * to the Programmer type
     *
     * TODO: Implement this method
     */
    public void performCalculatorTypeSwitchOperations() {
        LOGGER.info("Starting to performCalculatorTypeSwitchOperations");
        // possible conversion of the value in the textarea from
        // whatever mode it was in before to decimal
        convertTextArea();
        // set CalcType now
        calculator.setCalcType(CalcType_v3.BASIC);
        // setting up all the buttons
        setAllNumberButtons(true);
        calculator.buttonNegate.setEnabled(true);
        LOGGER.info("Finished performCalculatorTypeSwitchOperations\n");
    }

    public void setAllNumberButtons(boolean isEnabled) {
        calculator.button0.setEnabled(isEnabled);
        calculator.button1.setEnabled(isEnabled);
        calculator.button2.setEnabled(isEnabled);
        calculator.button3.setEnabled(isEnabled);
        calculator.button4.setEnabled(isEnabled);
        calculator.button5.setEnabled(isEnabled);
        calculator.button6.setEnabled(isEnabled);
        calculator.button7.setEnabled(isEnabled);
        calculator.button8.setEnabled(isEnabled);
        calculator.button9.setEnabled(isEnabled);
    }
    
}
