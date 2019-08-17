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
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
//import com.apple.eawt.Application;
import javax.swing.border.LineBorder;

public class BasicCalculator_v3 extends StandardCalculator_v3 {
    
	
    private static final long serialVersionUID = 1L;
    
    private GridBagLayout standardLayout; // layout of the calculator
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
    
    
    static {
    	
    }
    
    public BasicCalculator_v3() {
        super("Basic Calculator");
        setMinimumSize(new Dimension(100,200));
        setResizable(false);
        standardLayout = new GridBagLayout();
        setLayout(standardLayout); // set frame layout
        constraints = new GridBagConstraints(); // instanitate constraints
        setImageIcons();
        setupBasicCalculator_v3();
        addComponentsToCalculator_v3();
    }
    
    /*public void setMenuBar() {
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
                    setCalcType(CalcType.STANDARD);
                    
                    // needs more here
                }
            });
            
            viewMenu.add(binary);
            binary.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    setCalcType(CalcType.BINARY);
                    //setBinaryMode();
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
                    values[position] = textArea.getText();
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
                        JOptionPane.showMessageDialog(BasicCalculator_v3.this,
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
                        JOptionPane.showMessageDialog(BasicCalculator_v3.this,
                            mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE); 
                    }
                } 
            );
    } // end public setMenuBar
*/    
    
    public void setMode(int mode) {
    	if (mode == 1) {
    		
    	} else if (mode == 2) {
    		
    	} else if (mode == 3) {
    		
    	} else {
    		
    	}
    }
    
    public void setupBasicCalculator_v3() {
        constraints.insets = new Insets(5,5,5,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE

        buttonFraction.setFont(font);
        buttonFraction.setPreferredSize(new Dimension(35, 35) );
        buttonFraction.setBorder(new LineBorder(Color.BLACK));
        buttonFraction.setEnabled(true);
        
        buttonPercent.setFont(font);
        buttonPercent.setPreferredSize(new Dimension(35, 35) );
        buttonPercent.setBorder(new LineBorder(Color.BLACK));
        buttonPercent.setEnabled(true);
        
        buttonSqrt.setFont(font);
        buttonSqrt.setPreferredSize(new Dimension(35, 35) );
        buttonSqrt.setBorder(new LineBorder(Color.BLACK));
        buttonSqrt.setEnabled(true);
        
        buttonMC.setFont(font);
        buttonMC.setPreferredSize(new Dimension(35, 35) );
        buttonMC.setBorder(new LineBorder(Color.BLACK));
        if (memoryValues[memoryPosition].equals("")) {
            buttonMC.setEnabled(false);
        }
        
        buttonMR.setFont(font);
        buttonMR.setPreferredSize(new Dimension(35, 35) );
        buttonMR.setBorder(new LineBorder(Color.BLACK));
        if (memoryValues[memoryPosition].equals("")) {
            buttonMR.setEnabled(false);
        }
        
        buttonMS.setFont(font);
        buttonMS.setPreferredSize(new Dimension(35, 35) );
        buttonMS.setBorder(new LineBorder(Color.BLACK));
        buttonMS.setEnabled(true);
        
        buttonMA.setFont(font);
        buttonMA.setPreferredSize(new Dimension(35, 35) );
        buttonMA.setBorder(new LineBorder(Color.BLACK));
        buttonMA.setEnabled(true);
        
        buttonMSub.setFont(font);
        buttonMSub.setPreferredSize(new Dimension(35, 35) );
        buttonMSub.setBorder(new LineBorder(Color.BLACK));
        buttonMSub.setEnabled(true);
        
        LOGGER.info("All buttons font set, size set, and border set.");
        
        
        LOGGER.info("End setInitialStartMode()");
        // above this comment is all for the buttons
    } // end method setInitalStartMode()
    
    public void addComponentsToCalculator_v3() {
    	constraints.fill = GridBagConstraints.BOTH;
        addComponent(textArea, 0, 0, 5, 2);
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
        addComponent(button0, 7, 0, 2, 1);
        button0.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonDot, 7, 2, 1, 1);
        buttonDot.addActionListener(dotButtonHandler); 
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonAdd, 7, 3, 1, 1);
        buttonAdd.addActionListener(addButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button1, 6, 0, 1, 1);
        button1.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button2, 6, 1, 1, 1);
        button2.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button3, 6, 2, 1, 1);
        button3.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonMultiply, 6, 3, 1, 1);
        buttonMultiply.addActionListener(subButtonHandler);
        constraints.fill = GridBagConstraints.BOTH;
        addComponent(buttonEquals, 6, 4, 1, 2); // idk why its size is not showing on the application; leave a comment for me on why this is
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button4, 5, 0, 1, 1);
        button4.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button5, 5, 1, 1, 1);
        button5.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button6, 5, 2, 1, 1);
        button6.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonSub, 5, 3, 1, 1);
        buttonSub.addActionListener(mulButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonFraction, 5, 4, 1, 1);
        buttonFraction.addActionListener(fracButtonHandler);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button7, 4, 0, 1, 1);
        button7.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button8, 4, 1, 1, 1);
        button8.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(button9, 4, 2, 1, 1);
        button9.addActionListener(buttonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonDivide, 4, 3, 1, 1);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPercent, 4, 4, 1, 1);
        buttonPercent.addActionListener(perButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonDel, 3, 0, 1, 1);
        buttonDel.addActionListener(delButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonCE, 3, 1, 1, 1);
        buttonCE.addActionListener(CEButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonC, 3, 2, 1, 1);
        buttonC.addActionListener(clearButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonNegate, 3, 3, 1, 1);
        buttonNegate.addActionListener(negButtonHandler);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonSqrt, 3, 4, 1, 1);
        buttonSqrt.addActionListener(sqrtButtonHandler);
        LOGGER.info("All buttons added to the frame.");
    }
  
    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     * @param currentNumber
     * @return updated currentNumber
     *//*
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
                // if the string value at position x is not a 0
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
    }*/
    
    // method that tells what to do for numerical textarea
    
    
    /*public class AddButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("AddButtonHandler started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+position+"] is "+values[position]+ " after addButton pushed"); // confirming proper textarea before moving on
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
            	LOGGER.info("Performing previous addition calculation");
                add(); //addition();
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
            } else if (subBool == true && !values[1].equals("")) {
            	LOGGER.info("We understand the logic");
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
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[position]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
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
    }*/
    
    /*public class SubtractButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("SubtractButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+position+"] is "+values[position]+ " after addButton pushed"); // confirming proper textarea before moving on
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                addition();
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
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[position]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
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
    }*/
    
    /*public class MultiplyButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MultiplyButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+position+"] is "+values[position]+ " after addButton pushed"); // confirming proper textarea before moving on
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                addition();
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
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[position]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	LOGGER.info("already chose an operator. choose another number.");
            	
        	}
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }*/
    
    /*public class DivideButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("DivideButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = textArea.getText().replaceAll("\\n", "");
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+position+"] is "+values[position]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true && !values[1].equals("")) {
                addition();
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
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[position]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing textarea
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
            	LOGGER.info("already chose an operator. choose another number.");
            }
            textarea = textArea.getText();
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotActive = false;
            confirm();
        }
    }*/
    
    /*public class EqualsButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("EqualsButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == true) {
                addition();
                addBool = resetOperator(addBool);
                position = 0;
            } 
            else if (subBool == true){
                subtract();
                subBool = resetOperator(subBool);
                position = 0;
            } 
            else if (mulBool == true){
                multiply();
                mulBool = resetOperator(mulBool);
                position = 0;
            } 
            else if (divBool == true){
                divide();
                divBool = resetOperator(divBool);
                position = 0;
            } else if (values[0].equals("") && values[1].equals("")) {
                // if temp[0] and temp[1] do not have a number
                position = 0;
            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  values[position]); // "userInput +" // temp[position]
                position = 1;
                firstNumBool = true;
            } 
            values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
            textarea = textArea.getText();
            firstNumBool = true;
            dotActive = false;
            confirm();
        }
    }*/
    
    /*public class ClearButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("ClearButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            clear();
            confirm();
        }
    }*/
    
    /*public class ClearEntryButtonHandler implements ActionListener // works, supposedly
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("ClearEntryButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textarea = textArea.getText();
            textArea.setText("");
            if (values[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
                values[0] = "";
                addBool = false;
                subBool = false;
                mulBool = false;
                divBool = false;
                position = 0;
                firstNumBool = true;
                dotButtonPressed = false;
            } else {
                values[1] = ""; 
            }
            buttonDot.setEnabled(true);
            textarea = textArea.getText();
            confirm();
        }
    }*/
    
    // needs double textarea conditioning
    public class SqrtButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("Square Root ButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!textArea.getText().equals("")) {
                if (Double.parseDouble(values[position]) > 0.0) {
                    values[position] = Double.toString(Math.sqrt(Double.parseDouble(values[position]))); //4.0
                    //int intRep = (int)Math.sqrt(Double.parseDouble(temp[position])); // 4
                    if (values[position].contains(".")) { // if int == double, cut off decimal and zero
                        textArea.setText("\n" + values[position]);
                        textarea = textArea.getText().replaceAll("\n", "");
                        textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
                        values[position] = textarea; // update storing
                        textArea.setText("\n" + values[position]);
                        if (Integer.parseInt(values[position]) < 0 ) {
                            textarea = textArea.getText(); // temp[2]
                            LOGGER.info("textarea: " + textarea);
                            textarea = textarea.substring(1, textarea.length());
                            LOGGER.info("textarea: " + textarea);
                            textArea.setText("\n" + textarea.replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
                            LOGGER.info("temp[0]: " + values[0]);
                        }
                    }
                    textArea.setText("\n" + values[position] );
                } else { // number is negative
                    clear();
                    textArea.setText("Invalid textarea");
                    LOGGER.info("textArea: " + textArea.getText());
                }
                
            } // end textArea .= ""
            confirm();
        }
    }
        
    /*// NegateButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("\u00B1");
    public class NegateButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("NegateButtonHandler started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textarea = textArea.getText();
            //textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if (!textArea.getText().equals("")) { 
            // if there is a number in the text area
                if (isNegativeNumber(values[position]) == true) { //temp[position].substring(0, 1).equals("-")
                // if there is already a negative sign
                	LOGGER.info("Reversing number back to positive");
                    values[position] = convertToPositive(values[position]);
                    textArea.setText("\n" + values[position]);
                } else  {
                // whether first or second number, add "-"
                    values[position] = textArea.getText().replaceAll("\\n", ""); // textarea
                    textArea.setText("\n" + values[position] + "-");
                    values[position] = convertToNegative(values[position]); 
                } 
            }
            firstNumBool = true;
            confirm();    
        }
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
    
    /*// tested: passed
    public boolean isDecimal(String number) {
    	LOGGER.info("isDecimal() running");
        boolean answer = false;
        for(int i = 0; i < number.length(); i++) {
            if (number.substring(i).startsWith(".")) {
                answer = true;
            }
        }
        return answer;
    }*/
    
    /*public class DeleteButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("DeleteButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (values[1].equals("")) { 
            	//addBool = false;
                //subBool = true;
                //mulBool = true;
                //divBool = true;
            	position = 0; 
            } // assume they just previously pressed an operator
            textarea = textArea.getText().replaceAll("\n", "");
            LOGGER.info("temp["+position+"]: '" + values[position] + "'");
            LOGGER.info("textarea: " + textarea);
            boolean isNegative = isNegativeNumber(values[position]);
            LOGGER.info("isNegativeNumber() result: " + isNegative);
            dotActive = isDecimal(values[position]);
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
                        values[position] = textarea;
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
                        values[position] = textarea;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if no operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = "";
                            textArea.setText(textarea);
                            values[position] = "";
                        } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                            textarea = convertToPositive(textarea);
                            textarea = textarea.substring(0, textarea.length());
                            textArea.setText(textarea + "-");
                            values[position] = "-" + textarea;
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
                            values[position] = "-" + textarea;
                        } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
                            textarea = convertToPositive(textarea); // 3.00 or 0.00
                            textarea = textarea.substring(0, textarea.length()); // 3.0 or 0.0
                            textarea = clearZeroesAtEnd(textarea); // 3 or 0
                            textArea.setText(textarea + "-");
                            values[position] = "-" + textarea;
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
                        		textarea = values[position];
                        	} else {
                        		textarea = textarea.substring(0, textarea.length()-1);
                        	}
                        } 
                        LOGGER.info("output: " + textarea);
                        textArea.setText("\n" + textarea);
                        values[position] = textarea;
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
                        		textarea = values[position];
                        	} else {
                        		textarea = textarea.substring(0, textarea.length() -1);
                                textarea = clearZeroesAtEnd(textarea);
                        	}
                        }
                        LOGGER.info("output: " + textarea);
                        textArea.setText(textarea);
                        values[position] = textarea;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if an operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (textarea.length() == 2) { // ex: -3
                            textarea = "";
                            textArea.setText(textarea);
                            values[position] = "";
                        } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        	if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                        		textarea = values[position];
                        	}
                        	textarea = convertToPositive(textarea);
                            textarea = textarea.substring(0, textarea.length());
                            textArea.setText("\n" + textarea + "-");
                            values[position] = "-" + textarea;
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
                            values[position] = "-" + textarea; // -3 or -0
                        } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
                            textarea = convertToPositive(textarea); // 3.25 or 0.00
                            textarea = textarea.substring(0, textarea.length()); // 3.2 or 0.0
                            textarea = clearZeroesAtEnd(textarea);
                            LOGGER.info("textarea: " + textarea);
                            if (textarea.equals("0")) {
                                textArea.setText(textarea);
                                values[position] = textarea;
                            } else {
                                textArea.setText(textarea + "-");
                                values[position] = "-" + textarea;
                            }
                        }
                        LOGGER.info("textarea: " + textarea);
                        confirm();
                    }
                }
                resetOperators(false);
            }
            dotActive = false;
        }
        
    }*/
    
    /** This method resets the 4 main operators to the boolean you pass in *//*
    public void resetOperators(boolean bool) {
    	addBool = bool;
    	subBool = bool;
    	mulBool = bool;
    	divBool = bool;
    }
    
    public boolean isNegativeNumber(String result) {
    	LOGGER.info("isNegativeNumber() running");
        boolean answer = false;
        if (result.contains("-")) { // if int == double, cut off decimal and zero
            answer = true;
        }
        return answer;
    }*/
    
    // PercentButtonHandler operates on this button:
    // final private JButton buttonPer = new JButton("%");
    public class PercentButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("PercentStoreButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!textArea.getText().equals("")) {
                //if(textArea.getText().substring(textArea.getText().length()-1).equals("-")) { // if the last index equals '-'
                // if the number is negative
                if (isNegativeNumber(textArea.getText().replaceAll("\\n", ""))) {
                	LOGGER.info("if condition true");
                    //temp[position] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(values[position]); 
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    values[position] = Double.toString(percent);
                    textarea = formatNumber(values[position]);
                    LOGGER.info("Old " + values[position]);
                    values[position] = values[position].substring(1, values[position].length());
                    LOGGER.info("New " + values[position] + "-");
                    textArea.setText(values[position] + "-"); // update textArea
                    LOGGER.info("temp["+position+"] is " + values[position]);
                    //textArea.setText(textarea);
                    values[position] = textarea;//+textarea;
                    textarea = "";
                    LOGGER.info("temp["+position+"] is " + values[position]);
                    LOGGER.info("textArea: "+textArea.getText());
                } else {
                    double percent = Double.parseDouble(values[position]); 
                    percent /= 100;
                    values[position] = Double.toString(percent);
                    textArea.setText("\n" + formatNumber(values[position]));
                    values[position] = textArea.getText().replaceAll("\\n", "");
                    LOGGER.info("temp["+position+"] is " + values[position]);
                    LOGGER.info("textArea: "+textArea.getText());
                }
            }
            dotButtonPressed = true;
            dotActive = true;
            textarea = textArea.getText();
            confirm();
        }
    }
    
    /*// DotButtonHandler operates on this button:
    // final private JButton buttonDot = new JButton(".");
    public class DotButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            LOGGER.info("DotButtonHandler class started");
            LOGGER.info("button: " + event.getActionCommand()); // print out button confirmation
            if (!values[position].equals("")) { // if the textarea is not null
            	LOGGER.info("dotButtonPressed: " + dotButtonPressed + " | dotActive: " + dotActive);
                LOGGER.info("temp["+position+"] is " + values[position] + " before appending dot"); 
                if (dotButtonPressed == false || dotActive == false) {
                    textArea.setText("\n" + "."+values[position]);
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
                values[position] = "0.";
                dotButtonPressed = true;
            }
            dotActive = true;
            buttonDot.setEnabled(false);
            textarea = textArea.getText();
            confirm();
        }
    }*/
    
    // FracButtonHandler operates on this button:
    // final private JButton buttonF = new JButton("1/x");
    public class FracButtonHandler implements ActionListener { 
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("FracStoreButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if (!textArea.getText().equals("")) {
                double result = Double.parseDouble(values[position]);
                result = 1 / result;
                LOGGER.info("result: " + result);
                values[position] = Double.toString(result);
                textArea.setText("\n" + values[position]);
                LOGGER.info("temp["+position+"] is " + values[position]);
            }
            confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MC");
    public class MemoryClearButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryClearButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            values[4] = "";
            LOGGER.info("temp[4]: " + values[4]);
            LOGGER.info("temp["+position+"] is " + values[position]);
            buttonMR.setEnabled(false);
            buttonMC.setEnabled(false);
            memAddBool = false;
            memSubBool = false;
            confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MR");
    public class MemoryRecallButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MemoryRecallButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
        	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textarea = values[4]; 
            if (isNegativeNumber(textarea)) {
            	textarea = convertToPositive(textarea);
            }
            LOGGER.info("textarea: -" + textarea);
            values[position] = values[4];
            textArea.setText("\n" + textarea + "-"); // update textArea
            textarea = textArea.getText();
            LOGGER.info("temp["+position+"] is " + values[position]);
            confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("MS");
    public class MemoryStoreButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryStoreButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            if(!textArea.getText().equals("") && !values[position].equals("")) { // [position-1]
            // if text area has a number AND if storage has a number
                values[4] = values[position];
                LOGGER.info("textArea: '\\n" + textArea.getText().replaceAll("\n", "") + "'");
                LOGGER.info("temp[4]: " + values[4]);
                buttonMR.setEnabled(true);
                buttonMC.setEnabled(true);
            } else if (!values[4].equals("")) {
            // if memory does not have a number
                textarea = textArea.getText();
                double result = Double.parseDouble(values[4]) + Double.parseDouble(textArea.getText());
                LOGGER.info("result: " + result);
                textArea.setText(Double.toString(result));
                values[4] = Double.toHexString(result);
            }
            confirm();
        }
    }
    
    // MemoryAddButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("M+");
    public class MemoryAddButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemoryAddButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
    		textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if(!textArea.getText().equalsIgnoreCase("") && !values[4].equals("")) {
            // if text area has a number and memory storage has a number
            	LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
            	LOGGER.info("temp[4]:(memory) is " + values[4]);
            	LOGGER.info("temp["+position+"]:(add to) is " + values[position]);
                double result = Double.parseDouble(values[4]) + Double.parseDouble(values[position]); // create result forced double
                LOGGER.info(values[4] + " + " + values[position] + " = " + result);
                values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + values[4]);
                if (result % 1 == 0) { // if int == double, cut off decimal and zero
                    //textArea.setText(temp[4]);
                    textarea = values[4];
                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
                    values[4] = textarea; // update storing
                    LOGGER.info("update storing: " + textarea);
                    //textArea.setText(temp[2]);
                    if (Integer.parseInt(values[4]) < 0 ) { // if result is negative
                        textarea = values[4];
                        LOGGER.info("textarea: " + textarea);
                        textarea = textarea.substring(1, textarea.length());
                        LOGGER.info("textarea: " + textarea);
                        LOGGER.info("temp[4] is " + values[4]);
                       }
                } //else {// if double == double, keep decimal and number afterwards
                    //textArea.setText(temp[2]);
            } else if (textArea.getText().equals("")) {
                textArea.setText("0");
                values[position] = "0";
                values[4] = values[position];
            } else if (!values[position].equals("")) { // position-1 ???
                values[4] = values[position];
            } else {
                values[4] = values[position];
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: is " + values[4] + " after memory+ pushed"); // confirming proper textarea before moving on
            dotButtonPressed = false;
            textarea = textArea.getText();
            memAddBool = true;
            confirm();
        }
    }
    
    // MemoryClearButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("M-");
    public class MemorySubButtonHandler implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		LOGGER.info("MemorySubButtonHandler class started");
    		LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if(!textArea.getText().equalsIgnoreCase("") && !values[4].equals("")) {
            // if textArea has a number and memory storage has a number
            	LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
            	LOGGER.info("temp[%d]:(memory) %s\n", 4, values[4]);
            	LOGGER.info("temp[%d]:(sub from) %s\n", position, values[position]);
                double result = Double.parseDouble(values[4]) - Double.parseDouble(values[position]); // create result forced double
                LOGGER.info(values[4] + " - " + values[position] + " = " + result);
                values[4] = Double.toString(result); // store result
                LOGGER.info("temp[4]: " + values[4]);
                int intRep = (int)result;
                if (intRep == result) { // if int == double, cut off decimal and zero
                    textarea = values[4];
                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
                    values[4] = textarea; // update storing
                    LOGGER.info("update storing: " + textarea);
                    if (Integer.parseInt(values[4]) < 0 ) { // if result is negative
                        textarea = values[4];
                        LOGGER.info("textarea: " + textarea);
                        textarea = textarea.substring(1, textarea.length());
                        LOGGER.info("textarea: " + textarea);
                        LOGGER.info("temp[4]: " + values[4]);
                    }
                } 
            } else if (textArea.getText().equals("")) {
                textArea.setText("0");
                values[position] = "0";
                values[4] = values[position];
                LOGGER.info("textArea: " + textArea.getText());
                LOGGER.info("temp[4]: " + values[4]);
                
                double result = 0.0;
                LOGGER.info("result: " + result);
                int intRep = (int)result; // 9 = 9.0
                if (intRep == result) { // if int == double, cut off decimal and zero
                    textArea.setText(Double.toString(result));
                    textarea = textArea.getText();
                    LOGGER.info("textArea: " + textArea.getText());
                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
                    values[4] = textarea; // update storing
                    textArea.setText(values[4]);
                    if (Integer.parseInt(values[4]) < 0 ) {
                        textarea = textArea.getText(); // temp[2]
                        LOGGER.info("textarea: " + textarea);
                        textarea = textarea.substring(1, textarea.length());
                        LOGGER.info("textarea: " + textarea);
                        textArea.setText(textarea + "-"); // update textArea
                        LOGGER.info("temp["+position+"] is " + values[position]);
                    }
                } 
            } else if (!values[position].equals("")) {
                values[4] = convertToNegative(values[position]);
                LOGGER.info("textArea: " + textArea.getText());
                LOGGER.info("temp[4]: " + values[4]);
            } else {
                values[4] = convertToNegative(values[position]);
                LOGGER.info("textArea: " + textArea.getText());
                LOGGER.info("temp[4]: " + values[4]);
            }
            buttonMR.setEnabled(true);
            buttonMC.setEnabled(true);
            LOGGER.info("temp[4]: " + values[4] + " after memory- pushed"); // confirming proper textarea before moving on
            dotButtonPressed = false;
            textarea = textArea.getText();
            memSubBool = true;
            confirm();
        }
    }
    
    // method to set constraints on
    public void addComponent(Component c, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        standardLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }
    
    /*// clear method
    public void clear() {
    	LOGGER.info("Inside clear()");
        // firstNum, secondNum, total, copy/paste, memory
        for ( position=0; position < 4; position++) {
            values[position] = "";
        } 
        textArea.setText("\n0");
        textarea = textArea.getText();
        resetOperators(false);
        position = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotActive = false;
        buttonDot.setEnabled(true);
               
    }*/
    
    /*public void addition() {
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
                textarea = textArea.getText(); // temp[position]
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
                LOGGER.info("temp["+position+"] '" + values[position] + "'");
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
                LOGGER.info("temp["+position+"]: " + values[position]);
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
                LOGGER.info("temp["+position+"]: " + values[position]);
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
                    LOGGER.info("temp["+position+"]: " + values[position]);
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
    }*/
    
    /*public boolean resetOperator(boolean operatorBool) {
        if (operatorBool == true) {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            position = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            textarea = "";
            return false;
        } else {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            position = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            textarea = "";
            return true;
        }
    }*/
    
    /*// used to keep 2 decimals on the number at all times
    public String formatNumber(String num) {
        DecimalFormat df = new DecimalFormat("0.00"); 
        double number = Double.parseDouble(num);
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.endsWith("0")) {
        	numberAsStr = numberAsStr.substring(0, numberAsStr.length()-1);
        	num = df.format(Double.parseDouble(numberAsStr));
        	LOGGER.info("Formatted again: " + num);
        }
        return num;
    }*/
    
    /**
     * Returns temp[0] and temp[1].
     * Returns text area and operator booleans
     *//*
    
    public void confirm() {
        LOGGER.info("Confirm Results: ");
        LOGGER.info("---------------- ");
        for(int i=0; i<5; i++)
            LOGGER.info("temp["+i+"]: \'"+values[i]+"\'");
        LOGGER.info("textarea: '\\n"+textarea.replaceAll("\n", "")+"'");
        LOGGER.info("textArea: '\\n"+textArea.getText().replaceAll("\n", "")+"'"); 
        LOGGER.info("addBool: '"+addBool+"'");
        LOGGER.info("subBool: '"+subBool+"'"); 
        LOGGER.info("mulBool: '"+mulBool+"'"); 
        LOGGER.info("divBool: '"+divBool+"'"); 
        LOGGER.info("position: '"+position+"'"); 
        LOGGER.info("firstNumBool: '"+firstNumBool+"'"); 
        LOGGER.info("dotButtonPressed: '"+dotButtonPressed+"'");
        LOGGER.info("-------- End Confirm Results --------\n");
    }*/
    
    protected void setTemp(String textarea) {
        values[position] = textarea;
    }
    
    public void setFinishedText() {
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setText("Break my calculator");
    }
    
    
    
} //end class Calculator