package version3;

import com.apple.eawt.Application; //IMPORT FOR CALCULATOR ICON
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;

public class StandardCalculator_v3 extends Calculator_v3 {

    private static Logger LOGGER;
    static
    {
        LOGGER = LogManager.getLogger(StandardCalculator_v3.class);
    }

	private static final long serialVersionUID = 1L;
	
	/*
	* All standard calculators have the same:
    * menu bar
    * addition button
    * subtraction button
    * multiplication button
    * division button
    * equals button
    * negate button
    */
    final protected JMenuBar bar = new JMenuBar();
	final protected JButton buttonAdd = new JButton("+");
	final protected JButton buttonSubtract = new JButton("-");
    final protected JButton buttonMultiply = new JButton("*");
    final protected JButton buttonDivide = new JButton("/");
    final protected JButton buttonEquals = new JButton("=");
    final protected JButton buttonNegate = new JButton("\u00B1");
    //TODO: Think about it... move class specific booleans here

    public StandardCalculator_v3() throws HeadlessException
    {}
	public StandardCalculator_v3(GraphicsConfiguration gc)
    {
		super(gc);
		// TODO Auto-generated constructor stub
	}
    public StandardCalculator_v3(String title, GraphicsConfiguration gc)
    {
        super(title, gc);
        // TODO Auto-generated constructor stub
    }
	//TODO: Implement first
	public StandardCalculator_v3(String title) throws Exception
    {
		super(StringUtils.isBlank(title) ? title : CalcType_v3.BASIC.getName()); // default title is Basic
		setCurrentPanel(new JPanelBasic_v3(this));
		setupStandardCalculator_v3();
		setupMenuBar();
        setCalcType(determineCalcType());
		setImageIcons();
        // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        Application.getApplication().setDockIconImage(createImageIcon("src/main/resources/images/calculatorOriginal.jpg").getImage());
        setIconImage(calculatorImage1.getImage());
		setMinimumSize(getCurrentPanel().getSize());
		pack();
		setVisible(true);
	}

    /************* Start of methods here ******************/
	public void setupMenuBar()
    {
        LOGGER.info("Inside setupMenuBar()");
        // Menu Bar and Options
        setJMenuBar(bar); // add menu bar to application

        JMenu lookMenu = new JMenu("Look");
        lookMenu.setFont(this.font);
        // Look options
        JMenuItem metal = new JMenuItem("Metal");
        metal.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
                super.pack();
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });
        JMenuItem system = new JMenuItem("System");
        system.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.system.SystemLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
                super.pack();
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });
        JMenuItem windows = new JMenuItem("Windows");
        windows.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });
        JMenuItem motif = new JMenuItem("Motif");
        motif.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
                super.pack();
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });
        JMenuItem gtk = new JMenuItem("GTK");
        gtk.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
                super.pack();
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });

        lookMenu.add(metal);
        lookMenu.add(motif);
        String os = System.getProperty("os.name");
        if (!StringUtils.contains(os.toLowerCase(), "Mac".toLowerCase()))
        {
            lookMenu.add(windows);
            lookMenu.add(system);
            lookMenu.add(gtk);
        }
        this.bar.add(lookMenu);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(this.font);
        // View options
        JMenuItem basic = new JMenuItem("Basic");
        basic.setFont(this.font);
        viewMenu.add(basic);
        basic.addActionListener(action ->
            performTasksWhenChangingJPanels(new JPanelBasic_v3(this), CalcType_v3.BASIC)
        );
        JMenuItem programmer = new JMenuItem("Programmer");
        programmer.setFont(this.font);
        viewMenu.add(programmer);
        programmer.addActionListener(action ->
            performTasksWhenChangingJPanels(new JPanelProgrammer_v3(this), CalcType_v3.PROGRAMMER)
        );
        this.bar.add(viewMenu); // add viewMenu to menu bar
        JMenuItem dates = new JMenuItem("Dates");
        dates.setFont(this.font);
        viewMenu.add(dates);
        dates.addActionListener(action -> {
            try
            {
                performTasksWhenChangingJPanels(new JPanelDate_v3(this), CalcType_v3.DATE);
            }
            catch (ParseException e)
            {
                LOGGER.error("Couldn't change to JPanelDate_v3 because {}", e.getMessage());
            }
        });

        // Edit Menu and Actions
        JMenu editMenu = new JMenu("Edit"); // create edit menu
        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(editMenu); // add editMenu to menu bar
        // Edit options
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
        editMenu.add(copyItem);
        copyItem.addActionListener(action -> {
            values[3] = textArea.getText(); // to copy
            textarea = new StringBuffer().append(textArea.getText());
            confirm();
        });

        editMenu.add(pasteItem);
        pasteItem.addActionListener(action -> {
            if (values[3].equals(""))
                LOGGER.info("Temp[3] is null");
            else
                LOGGER.info("temp[3]: " + values[3]);
            textArea.setText(values[3]); // to paste
            values[valuesPosition] = textArea.getText();
            textarea = new StringBuffer().append(textArea.getText());
            confirm();
        });
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
        viewHelpItem.addActionListener(action -> {
            String COPYRIGHT = "\u00a9";
            iconLabel = new JLabel();
            JPanel iconPanel = new JPanel(new GridBagLayout());
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
            JOptionPane.showMessageDialog(StandardCalculator_v3.this,
                   mainPanel, "Help", JOptionPane.PLAIN_MESSAGE);
        });
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem);
        aboutCalculatorItem.addActionListener(action -> {
                java.lang.String COPYRIGHT = "\u00a9";
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
                JOptionPane.showMessageDialog(StandardCalculator_v3.this, mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);

            });
        LOGGER.info("Finished. Leaving setupMenuBar()");
    } // end public setMenuBar
	public void setupStandardCalculator_v3()
    {
		buttonAdd.setFont(font);
        buttonAdd.setPreferredSize(new Dimension(35, 35) );
        buttonAdd.setBorder(new LineBorder(Color.BLACK));
        buttonAdd.setEnabled(true);
        buttonAdd.addActionListener(action -> {
            performAdditionButtonActions(action);
        });
        
        buttonSubtract.setFont(font);
        buttonSubtract.setPreferredSize(new Dimension(35, 35) );
        buttonSubtract.setBorder(new LineBorder(Color.BLACK));
        buttonSubtract.setEnabled(true);
        buttonSubtract.addActionListener(action -> {
            performSubtractionButtonActions(action);
        });

        buttonMultiply.setFont(font);
        buttonMultiply.setPreferredSize(new Dimension(35, 35) );
        buttonMultiply.setBorder(new LineBorder(Color.BLACK));
        buttonMultiply.setEnabled(true);
        buttonMultiply.addActionListener(action -> {
            performMultiplicationActions(action);
        });
        
        buttonDivide.setFont(font);
        buttonDivide.setPreferredSize(new Dimension(35, 35) );
        buttonDivide.setBorder(new LineBorder(Color.BLACK));
        buttonDivide.setEnabled(true);
        buttonDivide.addActionListener(action -> {
            performDivideButtonActions(action);
        });
        
        buttonEquals.setFont(font);
        buttonEquals.setPreferredSize(new Dimension(35, 70) );
        buttonEquals.setBorder(new LineBorder(Color.BLACK));
        buttonEquals.setEnabled(true);
        buttonEquals.addActionListener(action -> {
            try {
                LOGGER.info("Equals button pressed");
                performButtonEqualsActions();
            } catch (Exception calculator_v3Error) {
                calculator_v3Error.printStackTrace();
            }
        });
        // TODO: does not work. fix
        buttonEquals.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    LOGGER.info("Return button Pressed");
                    int code = e.getKeyCode();
                    LOGGER.info("   Code: " + KeyEvent.getKeyText(code));
                    performButtonEqualsActions();
                } catch (Exception calculator_v3Error) {
                    calculator_v3Error.printStackTrace();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        buttonNegate.setFont(font);
        buttonNegate.setPreferredSize(new Dimension(35, 35) );
        buttonNegate.setBorder(new LineBorder(Color.BLACK));
        buttonNegate.setEnabled(true);
        buttonNegate.addActionListener(action -> {
            performNegateButtonActions(action);
        });
        setCalcType(CalcType_v3.BASIC);

        add(getCurrentPanel());
	}
    public void performTasksWhenChangingJPanels(JPanel currentPanel, CalcType_v3 calcType_v3)
    {
	    setTitle(calcType_v3.getName());
        JPanel oldPanel = updateJPanel(currentPanel);
        String nameOfOldPanel = "";
        switch (getCalcType())
        {
            case BASIC: nameOfOldPanel = CalcType_v3.BASIC.getName(); break;
            case PROGRAMMER: nameOfOldPanel = CalcType_v3.PROGRAMMER.getName(); break;
            case DATE: nameOfOldPanel = CalcType_v3.DATE.getName(); break;
            default: LOGGER.error("Unknown calculator type");
        }
        // don't switch calc_types here; later...
        if (getCurrentPanel() instanceof JPanelBasic_v3)
        {
            ((JPanelBasic_v3)getCurrentPanel()).performBasicCalculatorTypeSwitchOperations(oldPanel);
        }
        else if (getCurrentPanel() instanceof JPanelProgrammer_v3)
        {
            ((JPanelProgrammer_v3)getCurrentPanel()).performProgrammerCalculatorTypeSwitchOperations();
        }
        else if (getCurrentPanel() instanceof JPanelDate_v3)
        {
            LOGGER.debug("Switching to Date Calculator from {}", nameOfOldPanel);
        }
        pack();
    }

    public void performAdditionButtonActions(ActionEvent action)
    {
        LOGGER.info("AddButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText())
            {
                textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + textarea);
                LOGGER.debug("textArea: " + textArea.getText().replaceAll("\n", "")); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.debug("values["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
                if (memorySwitchBool == true) {
                    memoryValues[memoryPosition] += " + ";
                }
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.error("already chose an operator. choose another number.");
            }
            textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters().replace("+","").strip());
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;

            //performValuesConversion();
            confirm();
        }
    }
    public void addition()
    {
    	LOGGER.info("value[0]: '" + values[0] + "'");
    	LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        values[0] = Double.toString(result);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
        	LOGGER.info("We have a whole positive number");
        	textarea = clearZeroesAtEnd(String.valueOf(result));
            values[0] = textarea.toString(); // textarea changed to whole number, or int
            textArea.setText("\n" + textarea.toString());
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
        	LOGGER.info("We have a decimal");
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"] '" + values[valuesPosition] + "'");
            } else {
                textArea.setText("\n" + formatNumber(values[0]));
                values[0] = formatNumber(values[0]);
            }
        }
    }
    public void addition(CalcType_v3 calcType_v3)
    {
        if (getCalcType().equals(CalcType_v3.BASIC)) {
            addition();
        }
        else if (getCalcType().equals(CalcType_v3.PROGRAMMER)) {
            // convert values
//            convertFromTypeToType("Binary", "Decimal");
            // run add
            addition();
            textarea = new StringBuffer().append(convertFromTypeToTypeOnValues("Decimal", "Binary", String.valueOf(textarea))[0]);
            textArea.setText(addNewLineCharacters(1)+textarea);
        }
    }

    public void performSubtractionButtonActions(ActionEvent action)
    {
        LOGGER.info("SubtractButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText()) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + buttonChoice + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. next number is negative...");
                negatePressed = true;
            }
            textarea = new StringBuffer().append(textArea.getText());
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;
            performValuesConversion();
            confirm();
        }
    }
    public void subtract()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(Double.toString(result));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        {// if double == double, keep decimal and number afterwards
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: " + textarea);
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            }
            else {
                textArea.setText("\n" + formatNumber(values[0]));
            }
        }
    }
    public void subtract(CalcType_v3 calcType_v3)
    {
	    if (calcType_v3.equals(CalcType_v3.BASIC)) {
	        subtract();
        }
	    else if (calcType_v3.equals(CalcType_v3.PROGRAMMER)) {
//            convertFromTypeToType("Binary", "Decimal");
            subtract();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performMultiplicationActions(ActionEvent action)
    {
        LOGGER.info("performMultiplicationActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false &&
                    divBool == false && !textArea.getText().equals("") &&
                    !textArea.getText().equals("Invalid textarea") &&
                    !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + buttonChoice + " " + textarea);
                LOGGER.info("textArea: \\n" + textArea.getText().replaceAll("\n","")); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("values["+valuesPosition+"] is "+values[valuesPosition]+ " after buttonMultiply was pushed"); // confirming proper textarea before moving on
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. choose another number.");
            }
            textarea = new StringBuffer().append(textArea.getText());
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void multiply()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !values[0].contains("E") && !isNegativeNumber(values[0]))
        {
            textArea.setText(values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (values[0].contains("E"))
        {
            textArea.setText("\n" + values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            values[0] = textarea.toString(); // update storing
        }
        else if (isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(values[0]));
        }
        else
        {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
    }
    public void multiply(CalcType_v3 calcType_v3)
    {
        if (calcType_v3.equals(CalcType_v3.BASIC)) {
            multiply();
        }
        else if (calcType_v3.equals(CalcType_v3.PROGRAMMER)) {
            //convertFromTypeToType("Binary", "Decimal");
            multiply();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performDivideButtonActions(ActionEvent action)
    {
        LOGGER.info("performDivideButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform division. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textAreaContainsBadText())
            {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + buttonChoice + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals(""))
            {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("") & !values[1].equals("0"))
            {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText())  {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true)
            {
                LOGGER.info("already chose an operator. choose another number.");
            }
            textarea = new StringBuffer().append(textArea.getText());
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void divide()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        if (!values[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            double result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            // TODO: fix logic here to tie in with existing logic above
            if (getCalcType().equals(CalcType_v3.PROGRAMMER)) {
                result = Double.valueOf(String.valueOf(clearZeroesAtEnd(String.valueOf(result))));
            } // PROGRAMMER mode only supports whole numbers at this time

            LOGGER.info(values[0] + " / " + values[1] + " = " + result);
            values[0] = Double.toString(result); // store result
            LOGGER.info("addBool: " + addBool);
            LOGGER.info("subBool: " + subBool);
            LOGGER.info("mulBool: " + mulBool);
            LOGGER.info("divBool: " + divBool);
            if (Double.parseDouble(values[0]) % 1 == 0 && !isNegativeNumber(values[0]))
            {
                // if int == double, cut off decimal and zero
                textArea.setText("\n" + values[0]);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
                values[0] = textarea.toString(); // update storing
                textArea.setText("\n" + values[0]);
                updateTextareaFromTextArea();
                if (Integer.parseInt(values[0]) < 0 ) {
                    textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", "")); // temp[2]
                    LOGGER.info("textarea: " + textarea);
                    textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                    LOGGER.info("textarea: " + textarea);
                    textArea.setText("\n" + textarea + "-"); // update textArea
                    updateTextareaFromTextArea();
                    LOGGER.info("values["+valuesPosition+"]: " + values[valuesPosition]);
                }
            }
            else if (Double.parseDouble(values[0]) % 1 == 0 && isNegativeNumber(values[0]))
            {
                LOGGER.info("We have a whole negative number");
                textarea = new StringBuffer().append(convertToPositive(values[0]));
                textarea = clearZeroesAtEnd(textarea.toString());
                textArea.setText(addNewLineCharacters(1)+textarea+"-");
                textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                values[0] = textarea.toString();
                dotButtonPressed = false;
                buttonDot.setEnabled(true);
            }
            else {
                // if double == double, keep decimal and number afterwards
                textArea.setText("\n" + formatNumber(values[0]));
            }


        }
        else if (values[1].equals("0")) {
            java.lang.String result = "0";
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = result;
            firstNumBool = true;
        }
    }
    public void divide(CalcType_v3 calcType_v3)
    {
        if (calcType_v3.equals(CalcType_v3.BASIC)) {
            divide();
        }
        else if (calcType_v3.equals(CalcType_v3.PROGRAMMER))
        {
//            convertFromTypeToType("Binary", "Decimal");
            divide();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
        }
    }

    public void performButtonEqualsActions()
    {
        LOGGER.info("performButtonEqualsActions");
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (getCalcType() == CalcType_v3.BASIC)
        {
            if (addBool) {
                addition(getCalcType()); // addition();
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(getCalcType());
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(getCalcType());
                divBool = resetOperator(divBool);
            }
        }
        else if (getCalcType() == CalcType_v3.PROGRAMMER)
        {
            if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonBin().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonOct().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonDec().isSelected())
            {
                // DO NOTHING
            }
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonHex().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[1])[0];
            }

            if (orButtonBool)
            {
                ((JPanelProgrammer_v3)getCurrentPanel()).performOr();
                getTextArea().setText(addNewLineCharacters(1) + values[0]);
                performValuesConversion();

            }
            else if (isModButtonPressed())
            {
                LOGGER.info("Modulus result");
                ((JPanelProgrammer_v3)getCurrentPanel()).performModulus();
                // update values and textArea accordingly
                performValuesConversion();
                valuesPosition = 0;
                modButtonBool = false;
            }
            // after converting to decimal, perform same logic
            else if (addBool) {
                addition(CalcType_v3.BASIC); // forced addition of Basic type
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(CalcType_v3.BASIC);
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(CalcType_v3.BASIC);
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(CalcType_v3.BASIC);
                divBool = resetOperator(divBool);
            }
        }

        //TODO: add more calculator types here

        if (values[0].equals("") && values[1].equals(""))
        {
            // if temp[0] and temp[1] do not have a number
            valuesPosition = 0;
        }
        else if (textAreaContainsBadText())
        {
            textArea.setText("=" + " " +  values[valuesPosition]); // "userInput +" // temp[valuesPosition]
            valuesPosition = 1;
            firstNumBool = true;
        }

        values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        values[3] = "";
        //updateTextareaFromTextArea();
        firstNumBool = true;
        dotButtonPressed = false;
        valuesPosition = 0;
        confirm("");
    }
    public void performNegateButtonActions(ActionEvent action)
    {
        LOGGER.info("performNegateButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot negate number. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            getTextArea().setText(getTextAreaWithoutNewLineCharacters());
            updateTextareaFromTextArea();
            if (!textarea.equals("")) {
                if (numberIsNegative) {
                    textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                    getTextArea().setText("\n"+textarea);

                }
                else {
                    getTextArea().setText("\n"+textarea+"-");
                    textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                }
            }
            values[valuesPosition] = textarea.toString();
            confirm("");
        }
    }

    // TODO: move/use method in Calculator
    public java.lang.String formatNumber(java.lang.String num)
    {
        DecimalFormat df = new DecimalFormat();
        if (!numberIsNegative) {
            if (num.length() == 2) df = new DecimalFormat("0.00");
            if (num.length() == 3) df = new DecimalFormat("0.000");
        } else {
            if (num.length() == 3) df = new DecimalFormat("0.0");
            if (num.length() == 4) df = new DecimalFormat("0.00");
            if (num.length() == 5) df = new DecimalFormat("0.000");
        }
        double number = Double.parseDouble(num);
        number = Double.valueOf(df.format(number));
        java.lang.String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }
    /*
     * TODO: Change method to be a list of tasks that should
     * occur when we switch panels.
     *
     * method: performTasksWhenChangingJPanels
     *
     * tasks: set currentPanel
     * 		  set title of frame
     *        set the mode of the calculator
     *        perform setup tasks based on mode
     */

    public JPanel updateJPanel(JPanel currentPanel)
    {
        JPanel oldPanel = getCurrentPanel();
        remove(oldPanel);
        setCurrentPanel(currentPanel);
        add(getCurrentPanel());
        return oldPanel;
    }

    public void convertAllValuesToDecimal()
    {
        if (getCalcType().equals(CalcType_v3.PROGRAMMER))
        {
            values = convertFromTypeToTypeOnValues("Binary", "Decimal", values);
//            values[1] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[1])[0];
//            values[3] = convertFromTypeToTypeOnValues(CalcType_v3.BINARY2.getName(), CalcType_v3.DECIMAL.getName(), values[3])[0];
        }
        // TODO: Add more CalcType_v3's here
    }

    /**
     * IMPORTANT! You must convert values back to decimal within the same method this
     * method is called. It is an error to not convert the numbers back.
     *
     * This method exists to quickly convert the values to binary, for whatever
     * the purpose may be. Once the logic for needing the numbers to be in binary
     * mode, you must convert the values back to decimal!
     */
    public void convertAllValuesToBinary()
    {
        values = convertFromTypeToTypeOnValues("Decimal", "Binary", values);
//        values[1] = convertFromTypeToTypeOnValues("Decimal", "Binary", values[1])[0];
//        values[3] = convertFromTypeToTypeOnValues(CalcType_v3.DECIMAL.getName(), CalcType_v3.BINARY2.getName(), values[3])[0];
    }

    /**
     * IMPORTANT: Remember that this method returns a String array!!
     * @param type1
     * @param type2
     * @param strings
     * @return
     */
    public String[] convertFromTypeToTypeOnValues(String type1, String type2, String... strings)
    {
        LOGGER.debug("convertFromTypeToTypeOnValues(from: '"+type1+"', "+ "to: '"+type2+"' + " + Arrays.toString(strings));
        String[] arrToReturn = new String[strings.length];
        int countOfStrings = 0;
        if (StringUtils.isEmpty(strings[0])) return new String[]{"", "", "", ""};
        else countOfStrings = 1;
        if (type1.equals("Decimal") && type2.equals("Binary"))
        {
            for(String str : Arrays.asList(strings)) {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                int number = 0;
                try {
                    number = Integer.parseInt(str);
                    LOGGER.debug("number: " + number);
                    int i = 0;
                    while (i <= Integer.parseInt(str)) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for(int k = i; k<getBytes(); k++) {
                                sb.append("0");
                            }
                            break;
                        } else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for(int k = i+1; k<getBytes(); k++) {
                                sb.append("0");
                            } break;
                        }
                        i++;
                        number /= 2;
                    }
                } catch (NumberFormatException nfe) { LOGGER.error(nfe.getMessage()); }
                sb = sb.reverse();
                String strToReturn = sb.toString();
                LOGGER.debug("convertFrom("+type1+")To("+type2+") = "+ sb);
                arrToReturn[countOfStrings-1] = strToReturn;
                countOfStrings++;
            }
        }
        else if (type1.equals("Binary") && type2.equals("Decimal"))
        {
            for(String str : Arrays.asList(strings)) {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                sb.append(str);

                int appropriateLength = getBytes();
                LOGGER.debug("sb: " + sb);
                LOGGER.debug("appropriateLength: " + appropriateLength);

                if (sb.length() < appropriateLength)
                {
                    if (sb.length() == 0) { arrToReturn[(countOfStrings-1)] = ""; LOGGER.debug("arrToReturn["+(countOfStrings-1)+"]: '" + arrToReturn[countOfStrings-1] + "'"); countOfStrings++; continue; }
                    LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                    // user had entered 101, which really is 00000101
                    // but they aren't showing the first 5 zeroes
                    int difference = appropriateLength - sb.length();
                    StringBuffer missingZeroes = new StringBuffer();
                    for (int i=0; i<difference; i++) {
                        missingZeroes.append("0");
                    }
                    sb.append(missingZeroes);
                    LOGGER.debug("sb: " + sb);
                }

                double result = 0.0;
                double num1 = 0.0;
                double num2 = 0.0;
                for(int i=0, k=appropriateLength-1; i<appropriateLength; i++, k--)
                {
                    num1 = Double.valueOf(String.valueOf(sb.charAt(i)));
                    num2 = Math.pow(2,k);
                    result = (num1 * num2) + result;
                }

                arrToReturn[countOfStrings-1] = String.valueOf(Double.valueOf(result));


                if (isDecimal(arrToReturn[countOfStrings-1]))
                {
                    arrToReturn[countOfStrings-1] = String.valueOf(clearZeroesAtEnd(arrToReturn[countOfStrings-1]));
                }
                LOGGER.debug("arrToReturn["+(countOfStrings - 1)+"]: " + arrToReturn[countOfStrings-1]);
                countOfStrings++;
            }
        }
        return arrToReturn;
    }

    @Deprecated
    public void convertFromTypeToType(String type1, String type2)
    {
        if (type1.equals("Binary") && type2.equals("Decimal")) {
            // converting both numbers in values if applicable
            if (!values[0].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[0].length()-1; i<values[0].length(); i++, k--) {
                        String c = Character.toString(values[0].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[0] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
            if (!values[1].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[1].length()-1; i<values[1].length(); i++, k--) {
                        String c = Character.toString(values[1].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[1] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
        }
    }

    /**
     * The purpose of this method is to determine at the end of a "cycle", values[0]
     * and values[1] are numbers in base 10.
     * It also determines if textArea and textarea are displayed properly.
     * Remember textArea is all characters, and textarea is simply the value represented
     */
    public void performValuesConversion()
    {
        // make sure no matter the mode, values[0] and values[1] are numbers and textArea's display correctly
        if (getCalcType() == CalcType_v3.PROGRAMMER) {
            if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonDec().isSelected())
            {
                LOGGER.debug("even though we are in programmer mode, decimal base is selected so no conversion is needed");
            }
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonBin().isSelected())
            {
                LOGGER.debug("Programmer mode buttonBin selected");
                getTextArea().setText(addNewLineCharacters(1)+
                        convertFromTypeToTypeOnValues("Decimal", "Binary", getValues()[0])[0]);
                updateTextareaFromTextArea();
            }
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonOct().isSelected()) {}
            else if (((JPanelProgrammer_v3)getCurrentPanel()).getButtonHex().isSelected()) {}
            else {
                convertAllValuesToDecimal();
            }
        }
        //TODO: add more Calctypes here

    }

    public void performOrLogic(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<values[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(values[0].charAt(i)).equals("0") &&
                    String.valueOf(values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(String.valueOf(values[0].charAt(i))+" + "+String.valueOf(values[1].charAt(i))+
                    " = "+ letter);
        }
        values[0] = String.valueOf(sb);
        getTextArea().setText(addNewLineCharacters(1)+values[0]);
        orButtonBool = false;
        valuesPosition = 0;
    }

    /************* All Getters and Setters ******************/

    public JMenuBar getBar() { return bar; }
    public JButton getButtonAdd() { return buttonAdd; }
    public JButton getButtonSubtract() { return buttonSubtract; }
    public JButton getButtonMultiply() { return buttonMultiply; }
    public JButton getButtonDivide() { return buttonDivide; }
    public JButton getButtonEquals() { return buttonEquals; }
    public JButton getButtonNegate() { return buttonNegate; }
    // 4 types of Standard Calculators: create getters and setters
    public JPanel getCurrentPanel() { return super.getCurrentPanel(); }
    public static Logger getLOGGER() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }

    private static void setLOGGER(Logger LOGGER) { StandardCalculator_v3.LOGGER = LOGGER; }
    public void setCurrentPanel(JPanel currentPanel) { super.setCurrentPanel(currentPanel);}

}