package version3;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The face for a basic calculator
public class JPanelBasic_v3 extends JPanel {
	
    private static final long serialVersionUID = 1L;
    final static protected Logger LOGGER = LogManager.getLogger(JPanelBasic_v3.class);
    
    private GridBagLayout basicLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    
    final private FracButtonHandler fracButtonHandler = new FracButtonHandler();
    final private JButton buttonFraction = new JButton("1/x");
    
    final private PercentButtonHandler perButtonHandler = new PercentButtonHandler();
    final private JButton buttonPercent = new JButton("%");
    
    final private String SQRT = "\u221A";
    final private SqrtButtonHandler sqrtButtonHandler = new SqrtButtonHandler();
    final private JButton buttonSqrt = new JButton(SQRT);
    
    // Memory Suite TODO: move to Calculator_v3
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
    
    private boolean firstNumBool = true;
    private boolean numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false;
    private boolean decimal = false;
    
    private StandardCalculator_v3 calculator;
    public Calculator_v3 getCalculator() { return calculator; }
    private void setCalculator(StandardCalculator_v3 calculator) { this.calculator = calculator; }
    
    
    static {
    	
    }
    
    public JPanelBasic_v3(StandardCalculator_v3 calculator) {
        setMinimumSize(new Dimension(100,200));
        basicLayout = new GridBagLayout();
        setLayout(basicLayout); // set frame layout
        constraints = new GridBagConstraints(); // instanitate constraints
        setupPanel_v3();
        setCalculator(calculator);
        addComponentsToPanel_v3();
        
    } 

	public void setupPanel_v3() {
        constraints.insets = new Insets(5,5,5,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE

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
        
        LOGGER.info("All buttons font set, size set, and border set.");
        
        
        LOGGER.info("End setInitialStartMode()");
        // above this comment is all for the buttons
    } // end method setInitalStartMode()
    
    public void addComponentsToPanel_v3() {
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
        calculator.button0.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDot, 7, 2, 1, 1);
        calculator.buttonDot.addActionListener(this.calculator.dotButtonHandler); 
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonAdd, 7, 3, 1, 1);
        calculator.buttonAdd.addActionListener(this.calculator.addButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button1, 6, 0, 1, 1);
        calculator.button1.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button2, 6, 1, 1, 1);
        calculator.button2.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button3, 6, 2, 1, 1);
        calculator.button3.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonMultiply, 6, 3, 1, 1);
        calculator.buttonMultiply.addActionListener(this.calculator.subButtonHandler);
        constraints.fill = GridBagConstraints.BOTH;
        addComponent(calculator.buttonEquals, 6, 4, 1, 2); // idk why its size is not showing on the application; leave a comment for me on why this is
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button4, 5, 0, 1, 1);
        this.calculator.button4.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button5, 5, 1, 1, 1);
        this.calculator.button5.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button6, 5, 2, 1, 1);
        this.calculator.button6.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonSub, 5, 3, 1, 1);
        this.calculator.buttonSub.addActionListener(this.calculator.mulButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonFraction, 5, 4, 1, 1);
        buttonFraction.addActionListener(fracButtonHandler);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button7, 4, 0, 1, 1);
        this.calculator.button7.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button8, 4, 1, 1, 1);
        this.calculator.button8.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.button9, 4, 2, 1, 1);
        this.calculator.button9.addActionListener(this.calculator.buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonDivide, 4, 3, 1, 1);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPercent, 4, 4, 1, 1);
        buttonPercent.addActionListener(perButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonDel, 3, 0, 1, 1);
        this.calculator.buttonDel.addActionListener(this.calculator.delButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonCE, 3, 1, 1, 1);
        this.calculator.buttonCE.addActionListener(this.calculator.CEButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonC, 3, 2, 1, 1);
        this.calculator.buttonC.addActionListener(this.calculator.clearButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(this.calculator.buttonNegate, 3, 3, 1, 1);
        this.calculator.buttonNegate.addActionListener(this.calculator.negButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonSqrt, 3, 4, 1, 1);
        buttonSqrt.addActionListener(sqrtButtonHandler);
        LOGGER.info("All buttons added to the frame.");
    }
    
    // needs double textarea conditioning
    public class SqrtButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("Square Root ButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!calculator.getTextArea().getText().equals("")) {
                if (Double.parseDouble(calculator.values[calculator.position]) > 0.0) {
                	calculator.values[calculator.position] = Double.toString(Math.sqrt(Double.parseDouble(calculator.values[calculator.position]))); //4.0
                    //int intRep = (int)Math.sqrt(Double.parseDouble(temp[position])); // 4
                    if (calculator.values[calculator.position].contains(".")) { // if int == double, cut off decimal and zero
                    	calculator.getTextArea().setText("\n" + calculator.values[calculator.position]);
                    	calculator.textarea = calculator.getTextArea().getText().replaceAll("\n", "");
                    	calculator.textarea = calculator.textarea.substring(0, calculator.textarea.length()-2); // textarea changed to whole number, or int
                    	calculator.values[calculator.position] = calculator.textarea; // update storing
                    	calculator.textArea.setText("\n" + calculator.values[calculator.position]);
                        if (Integer.parseInt(calculator.values[calculator.position]) < 0 ) {
                        	calculator.textarea = calculator.getTextArea().getText(); // temp[2]
                            LOGGER.info("textarea: " + calculator.textarea);
                            calculator.textarea = calculator.textarea.substring(1, calculator.textarea.length());
                            LOGGER.info("textarea: " + calculator.textarea);
                            calculator.getTextArea().setText("\n" + calculator.textarea.replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
                            LOGGER.info("temp[0]: " + calculator.values[0]);
                        }
                    }
                    calculator.textArea.setText("\n" + calculator.values[calculator.position] );
                } else { // number is negative
                	calculator.clear();
                	calculator.getTextArea().setText("Invalid textarea");
                    LOGGER.info("textArea: " + calculator.getTextArea().getText());
                }
                
            } // end textArea .= ""
            calculator.confirm();
        }
    }
    
    // PercentButtonHandler operates on this button:
    // final private JButton buttonPer = new JButton("%");
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
                    //temp[position] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(calculator.values[calculator.position]); 
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    calculator.values[calculator.position] = Double.toString(percent);
                    calculator.textarea = calculator.formatNumber(calculator.values[calculator.position]);
                    LOGGER.info("Old " + calculator.values[calculator.position]);
                    calculator.values[calculator.position] = calculator.values[calculator.position].substring(1, calculator.values[calculator.position].length());
                    LOGGER.info("New " + calculator.values[calculator.position] + "-");
                    calculator.getTextArea().setText(calculator.values[calculator.position] + "-"); // update textArea
                    LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
                    //textArea.setText(textarea);
                    calculator.values[calculator.position] = calculator.textarea;//+textarea;
                    calculator.textarea = "";
                    LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
                    LOGGER.info("textArea: "+calculator.getTextArea().getText());
                } else {
                    double percent = Double.parseDouble(calculator.values[calculator.position]); 
                    percent /= 100;
                    calculator.values[calculator.position] = Double.toString(percent);
                    calculator.getTextArea().setText("\n" + calculator.formatNumber(calculator.values[calculator.position]));
                    calculator.values[calculator.position] = calculator.getTextArea().getText().replaceAll("\\n", "");
                    LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
                    LOGGER.info("textArea: "+calculator.getTextArea().getText());
                }
            }
            calculator.dotButtonPressed = true;
            calculator.dotActive = true;
            calculator.textarea = calculator.getTextArea().getText();
            calculator.confirm();
        }
    }
    
    // FracButtonHandler operates on this button:
    // final private JButton buttonF = new JButton("1/x");
    public class FracButtonHandler implements ActionListener { 
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("FracStoreButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!calculator.getTextArea().getText().equals("")) {
                double result = Double.parseDouble(calculator.values[calculator.position]);
                result = 1 / result;
                LOGGER.info("result: " + result);
                calculator.values[calculator.position] = Double.toString(result);
                calculator.getTextArea().setText("\n" + calculator.values[calculator.position]);
                LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
            }
            calculator.confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MC");
    public class MemoryClearButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryClearButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
    		calculator.values[4] = "";
            LOGGER.info("temp[4]: " + calculator.values[4]);
            LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
            buttonMR.setEnabled(false);
            buttonMC.setEnabled(false);
            calculator.memAddBool = false;
            calculator.memSubBool = false;
            calculator.confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MR");
    public class MemoryRecallButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MemoryRecallButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
        	calculator.getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        	calculator.textarea = calculator.values[4]; 
            if (calculator.isNegativeNumber(calculator.textarea)) {
            	calculator.textarea = calculator.convertToPositive(calculator.textarea);
            }
            LOGGER.info("textarea: -" + calculator.textarea);
            calculator.values[calculator.position] = calculator.values[4];
            calculator.getTextArea().setText("\n" + calculator.textarea + "-"); // update textArea
            calculator.textarea = calculator.getTextArea().getText();
            LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
            calculator.confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MS");
    public class MemoryStoreButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryStoreButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if(!calculator.getTextArea().getText().equals("") && !calculator.values[calculator.position].equals("")) { // [position-1]
            // if text area has a number AND if storage has a number
            	calculator.values[4] = calculator.values[calculator.position];
                LOGGER.info("textArea: '\\n" + calculator.getTextArea().getText().replaceAll("\n", "") + "'");
                LOGGER.info("temp[4]: " + calculator.values[4]);
                buttonMR.setEnabled(true);
                buttonMC.setEnabled(true);
            } else if (!calculator.values[4].equals("")) {
            // if memory does not have a number
            	calculator.textarea = calculator.getTextArea().getText();
                double result = Double.parseDouble(calculator.values[4]) + Double.parseDouble(calculator.getTextArea().getText());
                LOGGER.info("result: " + result);
                calculator.getTextArea().setText(Double.toString(result));
                calculator.values[4] = Double.toHexString(result);
            }
            calculator.confirm();
        }
    }
    
    // MemoryAddButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("M+");
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
            	LOGGER.info("temp["+calculator.position+"]:(add to) is " + calculator.values[calculator.position]);
                double result = Double.parseDouble(calculator.values[4]) + Double.parseDouble(calculator.values[calculator.position]); // create result forced double
                LOGGER.info(calculator.values[4] + " + " + calculator.values[calculator.position] + " = " + result);
                calculator.values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + calculator.values[4]);
                if (result % 1 == 0) { // if int == double, cut off decimal and zero
                    //textArea.setText(temp[4]);
                	calculator.textarea = calculator.values[4];
                	calculator.textarea = calculator.textarea.substring(0, calculator.textarea.length()-2); // textarea changed to whole number, or int
                	calculator.values[4] = calculator.textarea; // update storing
                    LOGGER.info("update storing: " + calculator.textarea);
                    //textArea.setText(temp[2]);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) { // if result is negative
                    	calculator.textarea = calculator.values[4];
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = calculator.textarea.substring(1, calculator.textarea.length());
                        LOGGER.info("textarea: " + calculator.textarea);
                        LOGGER.info("temp[4] is " + calculator.values[4]);
                       }
                } //else {// if double == double, keep decimal and number afterwards
                    //textArea.setText(temp[2]);
            } else if (calculator.getTextArea().getText().equals("")) {
            	calculator.getTextArea().setText("0");
            	calculator.values[calculator.position] = "0";
            	calculator.values[4] = calculator.values[calculator.position];
            } else if (!calculator.values[calculator.position].equals("")) { // position-1 ???
            	calculator.values[4] = calculator.values[calculator.position];
            } else {
            	calculator.values[4] = calculator.values[calculator.position];
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: is " + calculator.values[4] + " after memory+ pushed"); // confirming proper textarea before moving on
            calculator.dotButtonPressed = false;
            calculator.textarea = calculator.getTextArea().getText();
            calculator.memAddBool = true;
            calculator.confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("M-");
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
            	LOGGER.info("temp[%d]:(sub from) %s\n", calculator.position, calculator.values[calculator.position]);
                double result = Double.parseDouble(calculator.values[4]) - Double.parseDouble(calculator.values[calculator.position]); // create result forced double
                LOGGER.info(calculator.values[4] + " - " + calculator.values[calculator.position] + " = " + result);
                calculator.values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + calculator.values[4]);
                int intRep = (int)result;
                if (intRep == result) { // if int == double, cut off decimal and zero
                	calculator.textarea = calculator.values[4];
                	calculator.textarea = calculator.textarea.substring(0, calculator.textarea.length()-2); // textarea changed to whole number, or int
                	calculator.values[4] = calculator.textarea; // update storing
                    LOGGER.info("update storing: " + calculator.textarea);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) { // if result is negative
                    	calculator.textarea = calculator.values[4];
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = calculator.textarea.substring(1, calculator.textarea.length());
                        LOGGER.info("textarea: " + calculator.textarea);
                        LOGGER.info("temp[4]: " + calculator.values[4]);
                    }
                } 
            } else if (calculator.getTextArea().getText().equals("")) {
            	calculator.getTextArea().setText("0");
            	calculator.values[calculator.position] = "0";
            	calculator.values[4] = calculator.values[calculator.position];
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
                
                double result = 0.0;
                LOGGER.info("result: " + result);
                int intRep = (int)result; // 9 = 9.0
                if (intRep == result) { // if int == double, cut off decimal and zero
                	calculator.getTextArea().setText(Double.toString(result));
                	calculator.textarea = calculator.getTextArea().getText();
                    LOGGER.info("textArea: " + calculator.getTextArea().getText());
                    calculator.textarea = calculator.textarea.substring(0, calculator.textarea.length()-2); // textarea changed to whole number, or int
                    calculator.values[4] = calculator.textarea; // update storing
                    calculator.getTextArea().setText(calculator.values[4]);
                    if (Integer.parseInt(calculator.values[4]) < 0 ) {
                    	calculator.textarea = calculator.getTextArea().getText(); // temp[2]
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.textarea = calculator.textarea.substring(1, calculator.textarea.length());
                        LOGGER.info("textarea: " + calculator.textarea);
                        calculator.getTextArea().setText(calculator.textarea + "-"); // update textArea
                        LOGGER.info("temp["+calculator.position+"] is " + calculator.values[calculator.position]);
                    }
                } 
            } else if (!calculator.values[calculator.position].equals("")) {
            	calculator.values[4] = calculator.convertToNegative(calculator.values[calculator.position]);
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
            } else {
            	calculator.values[4] = calculator.convertToNegative(calculator.values[calculator.position]);
                LOGGER.info("textArea: " + calculator.getTextArea().getText());
                LOGGER.info("temp[4]: " + calculator.values[4]);
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: " + calculator.values[4] + " after memory- pushed"); // confirming proper textarea before moving on
            calculator.dotButtonPressed = false;
            calculator.textarea = calculator.getTextArea().getText();
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
    
}