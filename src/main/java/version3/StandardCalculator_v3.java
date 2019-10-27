package version3;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;


public class StandardCalculator_v3 extends Calculator_v3 {

	private static final long serialVersionUID = 1L;
	private static Logger LOGGER;
	
	// All standard calculators have:
    // The same menu bar
    JMenuBar bar = new JMenuBar();
//	final protected AddButtonHandler addButtonHandler = new AddButtonHandler();
	final protected JButton buttonAdd = new JButton("+");
//	final protected SubtractButtonHandler subtractButtonHandler = new SubtractButtonHandler();
	final protected JButton buttonSubtract = new JButton("-");
//	final protected MultiplyButtonHandler multiplyButtonHandler = new MultiplyButtonHandler();
    final protected JButton buttonMultiply = new JButton("*");
//    final protected DivideButtonHandler divideButtonHandler = new DivideButtonHandler();
    final protected JButton buttonDivide = new JButton("/");
//    final protected EqualsButtonHandler equalsButtonHandler = new EqualsButtonHandler();
    final protected JButton buttonEquals = new JButton("=");

    /** Used for testing purposes and to avoid recreating objects */
//    public AddButtonHandler getAddButtonHandler() { return addButtonHandler; }
//    public SubtractButtonHandler getSubtractButtonHandler() { return subtractButtonHandler; }
//    public MultiplyButtonHandler getMultiplyButtonHandler() { return multiplyButtonHandler; }
//    public DivideButtonHandler getDivideButtonHandler() { return divideButtonHandler; }
//    public EqualsButtonHandler getEqualsButtonHandler() { return equalsButtonHandler; }
    
    final java.lang.String negate = "\u00B1";
//    final protected NegateButtonHandler negButtonHandler = new NegateButtonHandler();
    final protected JButton buttonNegate = new JButton(negate);
    // moving up protected boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false;

    private Calculator_v3 calculator;
    public Calculator_v3 getCalculator() { return calculator; }
    protected void setCalculator(Calculator_v3 calculator) { this.calculator = calculator; }

    // 4 types of Standard Calculators: create getters and setters
    protected JPanel currentPanel;
//    public JPanelBasic_v3 getJPanelBasic() { return new JPanelBasic_v3(this); }
//    protected JPanel panelProgrammer;
//    protected JPanel panelScientific;
//    protected JPanel panelDate;
	public JPanel getCurrentJPanel() { return currentPanel; }

    public void setCurrentJPanel(JPanel currentPanel) {
        if (this.currentPanel == null) {}
        else remove(this.currentPanel);
        this.currentPanel = currentPanel;
        add(this.currentPanel);
        setCurrentPaneloNParentCalculator(currentPanel);

        SwingUtilities.updateComponentTreeUI(calculator);
        calculator.setMinimumSize(getCurrentJPanel().getSize());
        calculator.pack();
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
	public void performTasksWhenChangingJPanels(JPanel currentPanel, String title) {
		setCurrentJPanel(currentPanel);
//		setupStandardCalculator_v3();
        super.setTitle(title);
        add(getCurrentPanel());
//		confirm("Switched calculator types to " + super.getTitle());
	}

	/*
	TODO: fix implementation to return type. Add another method to set type
	 */
	public CalcType_v3 determineCalcType() {
	    if (currentPanel instanceof JPanelBasic_v3) { this.calcType = CalcType_v3.BASIC; }
	    else if (currentPanel instanceof  JPanelProgrammer_v3) { this.calcType = CalcType_v3.PROGRAMMER; }
	    else { this.calcType = CalcType_v3.BASIC; }
	    return calcType;
    }

    static {
        LOGGER = LogManager.getLogger(StandardCalculator_v3.class);
    }

    public StandardCalculator_v3() throws HeadlessException {
		// TODO Auto-generated constructor stub
	
	}

	public StandardCalculator_v3(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

    public StandardCalculator_v3(java.lang.String title, GraphicsConfiguration gc) {
        super(title, gc);
        // TODO Auto-generated constructor stub
    }

	// TODO: Implement first
	public StandardCalculator_v3(java.lang.String title) throws HeadlessException {
		super(CalcType_v3.BASIC.getName()); // default title is Basic
		setCalculator(this);
		setCurrentJPanel(new JPanelBasic_v3(this));
		setupStandardCalculator_v3();
		setupMenuBar();
        setCalcType(determineCalcType());
        //setupRestOfCalculator();
		setImageIcons();
		setMinimumSize(getCurrentPanel().getSize());
		
	}

    public void setupMenuBar() {
        LOGGER.info("Inside setupMenuBar()");
        // Menu Bar and Options
        bar = new JMenuBar(); // create menu bar
        setJMenuBar(bar); // add menu bar to application

        JMenu viewMenu = new JMenu("View"); // create view menu
        viewMenu.setFont(this.font );


        JMenuItem basic = new JMenuItem("Basic");
        basic.setFont(this.font);
        viewMenu.add(basic);
        basic.addActionListener(action -> {
            //calcType = CalcType_v3.BASIC;
            JPanelBasic_v3 p = new JPanelBasic_v3(this);
            performTasksWhenChangingJPanels(p, CalcType_v3.BASIC.getName());
            p.performCalculatorTypeSwitchOperations();

        });

        JMenuItem programmer = new JMenuItem("Programmer");
        programmer.setFont(this.font);
        viewMenu.add(programmer);
        programmer.addActionListener(action -> {
            //calcType = CalcType_v3.PROGRAMMER;
            JPanelProgrammer_v3 p = new JPanelProgrammer_v3(this);
            performTasksWhenChangingJPanels(p, CalcType_v3.PROGRAMMER.getName());
            p.performCalculatorTypeSwitchOperations();
        });
        this.bar.add(viewMenu); // add viewMenu to menu bar
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
                textarea = new StringBuffer().append(textArea.getText());
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
                        textarea = new StringBuffer().append(textArea.getText());
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
            java.lang.String COPYRIGHT = "\u00a9";
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
            JOptionPane.showMessageDialog(StandardCalculator_v3.this,
                   mainPanel, "Help", JOptionPane.PLAIN_MESSAGE);
        }
    });
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem);
        aboutCalculatorItem.addActionListener(
            new ActionListener() // anonymous inner class
            {
                // display message dialog box when user selects About....
                @Override
                public void actionPerformed(ActionEvent event)
                {
                    java.lang.String COPYRIGHT = "\u00a9";
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
                    JOptionPane.showMessageDialog(StandardCalculator_v3.this,
                            mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);
                }
            }
        );
        LOGGER.info("Finished. Leaving setupMenuBar()");
    } // end public setMenuBar
	
	public void setupStandardCalculator_v3() {
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
                performButtonEqualsActions(action);
            } catch (Calculator_v3Error calculator_v3Error) {
                calculator_v3Error.printStackTrace();
            }
        });
        
        buttonNegate.setFont(font);
        buttonNegate.setPreferredSize(new Dimension(35, 35) );
        buttonNegate.setBorder(new LineBorder(Color.BLACK));
        buttonNegate.setEnabled(true);
        buttonNegate.addActionListener(action -> {
            performNegateButtonActions(action);
        });
        
        add(getCurrentPanel());
	}

    public String[] convertFromTypeToTypeOnValues(String type1, String type2, String... strings) {
	    String[] arrToReturn = new String[strings.length];
	    int countOfStrings = 0;
	    if (StringUtils.isEmpty(strings[0])) return new String[]{""};
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
	                while (i < Integer.parseInt(str)) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for(int k = i; k<calculator.getBytes(); k++) {
                                sb.append("0");
                            }
                            break;
                        } else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for(int k = i+1; k<calculator.getBytes(); k++) {
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

                int appropriateLength = calculator.getBytes();
                LOGGER.debug("sb: " + sb);
                LOGGER.debug("appropriateLength: " + appropriateLength);

                if (sb.length() < appropriateLength)
                {
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

                arrToReturn[0] = String.valueOf(Double.valueOf(result));

                LOGGER.debug("arrToReturn[0]: " + arrToReturn[0]);
                if (calculator.isDecimal(arrToReturn[0]))
                {
                    arrToReturn[0] = String.valueOf(calculator.clearZeroesAtEnd(arrToReturn[0]));
                }
            }
        }
	    return arrToReturn;
    }

    public void convertFromTypeToType(String type1, String type2) {
        if (type1.equals("Binary") && type2.equals("Decimal")) {
            // converting both numbers in values if applicable
            if (!calculator.values[0].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=calculator.values[0].length()-1; i<calculator.values[0].length(); i++, k--) {
                        String c = Character.toString(calculator.values[0].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    calculator.values[0] = calculator.clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
            if (!calculator.values[1].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=calculator.values[1].length()-1; i<calculator.values[1].length(); i++, k--) {
                        String c = Character.toString(calculator.values[1].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    calculator.values[1] = calculator.clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
        }
    }

    //TODO: Phase this method out. Change to implement logic based on type
	public void addition() {
    	LOGGER.info("value[0]: '" + values[0] + "'");
    	LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        values[0] = Double.toString(result);
        LOGGER.info("addBool: "+addBool);
        LOGGER.info("subBool: "+subBool);
        LOGGER.info("mulBool: "+mulBool);
        LOGGER.info("divBool: "+divBool);
        if (result % 1 == 0) {
        	LOGGER.info("We have a whole number");
        	textarea = clearZeroesAtEnd(String.valueOf(result));
            values[0] = String.valueOf(textarea); // textarea changed to whole number, or int
            textArea.setText("\n" + String.valueOf(textarea));
            if (Integer.parseInt(values[0]) < 0 ) {
                textarea = new StringBuffer().append(textArea.getText()); // temp[valuesPosition]
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n "+textarea.toString().replaceAll("-", "") + "-"); // update textArea
            }
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

    public void addition(CalcType_v3 calcType_v3) {
        if (getCalcType().equals(CalcType_v3.BASIC)) {
            addition();
        }
        else if (getCalcType().equals(CalcType_v3.PROGRAMMER)) {
            // convert values
//            convertFromTypeToType("Binary", "Decimal");
            // run add
            addition();
//            values[0] = p.convertToBinary(calculator.values[0]);
            textArea.setText("\n" + convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0]);
        }
    }

    public void subtract() {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0) {
            //textArea.setText(temp[0]);
            textarea = new StringBuffer().append(Double.toString(result));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
            if (Integer.parseInt(values[0]) < 0 ) {
                //textarea = textArea.getText(); 
                //System.out.printf("\n%s", textarea);
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea.toString().replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
                LOGGER.info("temp[0]: " + values[0]);
            }
        }
        else {// if double == double, keep decimal and number afterwards
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

    public void subtract(CalcType_v3 calcType_v3) {
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
    
    public void multiply() {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0) {
            textArea.setText(values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
            if (Integer.parseInt(values[0]) < 0 ) {
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", "")); // temp[2]
                LOGGER.info("textarea: " + textarea);
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            }
        } else {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
    }

    public void multiply(CalcType_v3 calcType_v3) {
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
    
    public void divide() {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        if (!values[1].equals("0")) { 
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
            if (Double.parseDouble(values[0]) % 1 == 0) {
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
            else {
                // if double == double, keep decimal and number afterwards
                textArea.setText("\n" + formatNumber(values[0]));
            }


        } else if (values[1].equals("0")) {
            java.lang.String result = "0";
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = result;
            firstNumBool = true;
        }
    }

    public void divide(CalcType_v3 calcType_v3) {
        if (calcType_v3.equals(CalcType_v3.BASIC)) {
            divide();
        }
        else if (calcType_v3.equals(CalcType_v3.PROGRAMMER)) {
//            convertFromTypeToType("Binary", "Decimal");
            divide();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
        }
    }
    // used to keep 2 decimals on the number at all times
    public java.lang.String formatNumber(java.lang.String num) {
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

    public void performAdditionButtonActions(ActionEvent action) {
        LOGGER.info("AddButtonHandler started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                !textArea.getText().equals("") && !calculator.textAreaContainsBadText()) {
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            textArea.setText("\n" + " " + buttonChoice + " " + textarea);
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
            addition(calculator.getCalcType());
            addBool = resetOperator(addBool); // sets addBool to false
            addBool = true;
        }
        else if (subBool == true && !values[1].equals("")) {
            subtract(calculator.getCalcType());
            subBool = resetOperator(subBool);
            addBool = true;
        }
        else if (mulBool == true && !values[1].equals("")) {
            multiply(calculator.getCalcType());
            mulBool = resetOperator(mulBool);
            addBool = true;
        }
        else if (divBool == true && !values[1].equals("")) {
            divide(calculator.getCalcType());
            divBool = resetOperator(divBool);
            addBool = true;
        }
        else if (calculator.textAreaContainsBadText()) {
            textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
            addBool = true; // sets logic for arithmetic
            firstNumBool = false; // sets logic to perform operations when collecting second number
            dotButtonPressed = false;
            valuesPosition++; // increase valuesPosition for storing textarea
        }
        else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
            LOGGER.info("already chose an operator. choose another number.");
        }
        textarea = new StringBuffer().append(textArea.getText().replaceAll("\n",""));
        buttonDot.setEnabled(true);
        dotButtonPressed = false;
        numberIsNegative = false;
        confirm("");
    }
    /*class AddButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("AddButtonHandler started");
        	String buttonChoice = e.getActionCommand();

        	// TODO: fix logic later but need to make sure values are in decimal form to add
//            convertAllValuesToDecimal();

            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !calculator.textAreaContainsBadText()) {
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + buttonChoice + " " + textarea);
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
                addition(calculator.getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(calculator.getCalcType());
                subBool = resetOperator(subBool);
                addBool = true;
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(calculator.getCalcType());
                mulBool = resetOperator(mulBool);
                addBool = true;
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(calculator.getCalcType());
                divBool = resetOperator(divBool);
                addBool = true;
            }
            else if (calculator.textAreaContainsBadText()) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.info("already chose an operator. choose another number.");
            }
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\n",""));
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;
            confirm("");
        }
    }*/

    public void performSubtractionButtonActions(ActionEvent action) {
        LOGGER.info("SubtractButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
        if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                !textArea.getText().equals("") && !calculator.textAreaContainsBadText()) {
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
            addition(calculator.getCalcType());
            addBool = resetOperator(addBool);
            subBool = true;
        }
        else if (subBool == true && !values[1].equals("")) {
            subtract(calculator.getCalcType());
            subBool = resetOperator(subBool);
            subBool = true;
        }
        else if (mulBool == true && !values[1].equals("")) {
            multiply(calculator.getCalcType());
            mulBool = resetOperator(mulBool);
            subBool = true;
        }
        else if (divBool == true && !values[1].equals("")) {
            divide(calculator.getCalcType());
            divBool = resetOperator(divBool);
            subBool = true;
        }
        else if (calculator.textAreaContainsBadText()) {
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
        confirm();
    }
    /*class SubtractButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("SubtractButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !calculator.textAreaContainsBadText()) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(calculator.getCalcType());
                addBool = resetOperator(addBool);
                subBool = true;
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(calculator.getCalcType());
                subBool = resetOperator(subBool);
                subBool = true;
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(calculator.getCalcType());
                mulBool = resetOperator(mulBool);
                subBool = true;
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(calculator.getCalcType());
                divBool = resetOperator(divBool);
                subBool = true;
            }
            else if (calculator.textAreaContainsBadText()) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
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
            confirm();
        }
    }*/

    public void performMultiplicationActions(ActionEvent action) {
        LOGGER.info("performMultiplicationActions started");
        String buttonChoice = action.getActionCommand();
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
            addition(calculator.getCalcType());
            addBool = resetOperator(addBool);
            mulBool = true;
        }
        else if (subBool == true && !values[1].equals("")) {
            subtract(calculator.getCalcType());
            subBool = resetOperator(subBool);
            mulBool = true;
        }
        else if (mulBool == true && !values[1].equals("")) {
            multiply(calculator.getCalcType());
            mulBool = resetOperator(mulBool);
            mulBool = true;
        }
        else if (divBool == true && !values[1].equals("")) {
            divide(calculator.getCalcType());
            divBool = resetOperator(divBool);
            mulBool = true;
        }
        else if (calculator.textAreaContainsBadText()) {
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
        confirm();
    }
	/*class MultiplyButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("MultiplyButtonHandler class started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false &&
                    divBool == false && !textArea.getText().equals("") &&
                    !textArea.getText().equals("Invalid textarea") &&
                    !textArea.getText().equals("Cannot divide by 0")) {
            	textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: \\n" + textArea.getText().replaceAll("\n","")); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("values["+valuesPosition+"] is "+values[valuesPosition]+ " after buttonMultiply was pushed"); // confirming proper textarea before moving on
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(calculator.getCalcType());
                addBool = resetOperator(addBool);
                mulBool = true;
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(calculator.getCalcType());
                subBool = resetOperator(subBool);
                mulBool = true;
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(calculator.getCalcType());
                mulBool = resetOperator(mulBool);
                mulBool = true;
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(calculator.getCalcType());
                divBool = resetOperator(divBool);
                mulBool = true;
            }
            else if (calculator.textAreaContainsBadText()) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
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
            confirm();
        }
    }*/

    public void performDivideButtonActions(ActionEvent action) {
        LOGGER.info("performDivideButtonActions started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
        if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") &&
                !textArea.getText().equals("Cannot divide by 0")) {
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
        else if (addBool == true && !values[1].equals("")) {
            addition(calculator.getCalcType());
            addBool = resetOperator(addBool); // sets addBool to false
            divBool = true;
        }
        else if (subBool == true && !values[1].equals("")) {
            subtract(calculator.getCalcType());
            subBool = resetOperator(subBool);
            divBool = true;
        }
        else if (mulBool == true && !values[1].equals("")) {
            multiply(calculator.getCalcType());
            mulBool = resetOperator(mulBool);
            divBool = true;
        }
        else if (divBool == true && !values[1].equals("") & !values[1].equals("0")) {
            divide(calculator.getCalcType());
            divBool = resetOperator(divBool);
            divBool = true;
        }
        else if (calculator.textAreaContainsBadText())  {
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
        confirm();
    }
	/*class DivideButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("DivideButtonHandler class started");
            LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") &&
                    !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(calculator.getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(calculator.getCalcType());
                subBool = resetOperator(subBool);
                divBool = true;
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(calculator.getCalcType());
                mulBool = resetOperator(mulBool);
                divBool = true;
            }
            else if (divBool == true && !values[1].equals("") & !values[1].equals("0")) {
                divide(calculator.getCalcType());
                divBool = resetOperator(divBool);
                divBool = true;
            }
            else if (calculator.textAreaContainsBadText()) {
                textArea.setText(e.getActionCommand() + " " +  values[0]); // "userInput +" // temp[valuesPosition]
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
            confirm();
        }
    }*/

    public void performButtonEqualsActions(ActionEvent action) throws Calculator_v3Error {
        LOGGER.info("performButtonEqualsActions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (!getCalcType().equals(CalcType_v3.BASIC)) {
            convertAllValuesToDecimal();
        }

        if (addBool) {
            addition(calculator.getCalcType()); // addition();
            addBool = resetOperator(addBool);
        }
        else if (subBool){
            subtract(calculator.getCalcType());
            subBool = resetOperator(subBool);
        }
        else if (mulBool){
            multiply(calculator.getCalcType());
            mulBool = resetOperator(mulBool);
        }
        else if (divBool){
            divide(calculator.getCalcType());
            divBool = resetOperator(divBool);
        }
        else if (values[0].equals("") && values[1].equals("")) {
            // if temp[0] and temp[1] do not have a number
            valuesPosition = 0;
        }
        else if (calculator.textAreaContainsBadText()) {
            textArea.setText(action.getActionCommand() + " " +  values[valuesPosition]); // "userInput +" // temp[valuesPosition]
            valuesPosition = 1;
            firstNumBool = true;
        }
        else if (orButtonBool) {
//            performOrLogic(action);
            JPanelProgrammer_v3 p = (JPanelProgrammer_v3) getCurrentPanelFromParentCalculator();
            p.performButtonOrActions(action);
        }
        values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        updateTextareaFromTextArea();
        firstNumBool = true;
        dotButtonPressed = false;
        // TODO: Not a todo, but I did that so this would stand out. It's more an important note
        // TODO: values[0], values[1], values[2] should always be in decimal form.
        confirm("");
    }
	/*class EqualsButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("EqualsButtonHandler class started");
        	String buttonChoice = e.getActionCommand();
        	LOGGER.info("button: " + buttonChoice); // print out button confirmation

            if (addBool) {
                addition(calculator.getCalcType()); // addition();
                addBool = resetOperator(addBool);
            } 
            else if (subBool){
                subtract(calculator.getCalcType());
                subBool = resetOperator(subBool);
            } 
            else if (mulBool){
                multiply(calculator.getCalcType());
                mulBool = resetOperator(mulBool);
            } 
            else if (divBool){
                divide(calculator.getCalcType());
                divBool = resetOperator(divBool);
            }
            else if (values[0].equals("") && values[1].equals("")) {
                // if temp[0] and temp[1] do not have a number
                valuesPosition = 0;
            }
            else if (calculator.textAreaContainsBadText()) {
                textArea.setText(e.getActionCommand() + " " +  values[valuesPosition]); // "userInput +" // temp[valuesPosition]
                valuesPosition = 1;
                firstNumBool = true;
            }
            else if (orButtonBool) {
                performOrLogic();
            }
            values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
            updateTextareaFromTextArea();
            firstNumBool = true;
            dotButtonPressed = false;
            confirm("");
        }
    }*/

	public void performNegateButtonActions(ActionEvent action) {
        LOGGER.info("performNegateButtonActions started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        calculator.clearNewLineFromTextArea();
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
    /**
     * Handles the logic when user clicks the Negate button
     */
    class NegateButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	LOGGER.info("NegateButtonHandler started");
        	LOGGER.info("button: " + e.getActionCommand()); // print out button confirmation
            calculator.clearNewLineFromTextArea();
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
            confirm();    
        }
    }

	public JPanel getCurrentPanel() {
		return currentPanel;
	}

    public void convertAllValuesToDecimal() {
        if (getCalcType().equals(CalcType_v3.PROGRAMMER))
        {
            values[0] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[0])[0];
            values[1] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[1])[0];
        }
        // TODO: Add more CalcType_v3's here
    }

    public void convertAllValuesToBinary() {
        if (!getCalcType().equals(CalcType_v3.PROGRAMMER)) {
            if (getCalcType().equals(CalcType_v3.BASIC)) {
                values[0] = convertFromTypeToTypeOnValues("Decimal", "Binary", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Decimal", "Binary", values[1])[0];
            }
            // TODO: Add more CalcType_v3's here
        }
    }

    public void performOrLogic(ActionEvent actionEvent) {
        LOGGER.info("performOrLogic starts here");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<calculator.values[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(calculator.values[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(String.valueOf(calculator.values[0].charAt(i))+" + "+String.valueOf(calculator.values[1].charAt(i))+
                    " = "+ letter);
        }
        calculator.values[0] = String.valueOf(sb);
        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+calculator.values[0]);
        orButtonBool = false;
        valuesPosition = 0;
    }

}




