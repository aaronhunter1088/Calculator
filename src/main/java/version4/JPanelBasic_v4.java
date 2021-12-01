package version4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static version4.CalculatorBase_v4.*;
import static version4.CalculatorType_v4.*;

// The face for a basic calculator
public class JPanelBasic_v4 extends JPanel
{

    protected final static Logger LOGGER;
    static
    {
        System.setProperty("appName", "Calculator");
        LOGGER = LogManager.getLogger(JPanelBasic_v4.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout panelLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    final private JButton buttonFraction = new JButton("1/x");
    final private JButton buttonPercent = new JButton("%");
    final private JButton buttonSqrt = new JButton("\u221A");
    private Calculator_v4 calculator;

    /************* Constructors ******************/
    public JPanelBasic_v4() {}

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator
     */
    public JPanelBasic_v4(Calculator_v4 calculator)
    {
        setCalculator(calculator);
        setMinimumSize(new Dimension(100,200));
        setPanelLayout(new GridBagLayout());
        setLayout(getPanelLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setupBasicPanel();
        setupHelpMenu();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        getLogger().info("Finished setting up basic panel");
    }

    /************* Start of methods here ******************/
    public void setupBasicPanel()
    {
        LOGGER.info("Configuring the components for the basic view");
        getCalculator().getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        getCalculator().getTextArea().setFont(Calculator_v4.font);
        getCalculator().getTextArea().setPreferredSize(new Dimension(70, 30));
        getCalculator().getTextArea().setEditable(false);
        LOGGER.info("Text Area configured");
        getCalculator().setupMemoryButtons(); // MS, MC, MR, M+, M-
        LOGGER.info("Memory Operators (MC MR MS M+ M-) configured");
        getCalculator().setupOtherCalculatorButtons(); // C, CE, Delete, Dot
        LOGGER.info("Delete button configured");
        LOGGER.info("CE button configured");
        LOGGER.info("C button configured");
        getCalculator().setupOtherBasicCalculatorButtons(); // =, Negate
        LOGGER.info("Negate button configured");
        getButtonSqrt().setFont(Calculator_v4.font);
        getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        getButtonSqrt().setEnabled(true);
        getButtonSqrt().addActionListener(this::performSquareRootButtonActions);
        LOGGER.info("Square Root button configured");
        setupNumberButtons(true);
        LOGGER.info("Number buttons configured");
        getCalculator().setupBasicCalculatorOperationButtons(); // Add, Sub, Multiply, Divide
        LOGGER.info("Basic Operators (+ - * /) configured");
        getButtonPercent().setFont(Calculator_v4.font);
        getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        getButtonPercent().setEnabled(true);
        getButtonPercent().addActionListener(this::performPercentButtonActions);
        LOGGER.info("Percent button configured");
        getButtonFraction().setFont(Calculator_v4.font);
        getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        getButtonFraction().setEnabled(true);
        getButtonFraction().addActionListener(this::performFractionButtonActions);
        LOGGER.info("Fraction button configured");
        LOGGER.info("Equals button configured");
        LOGGER.info("Dot button configured");
        getCalculator().setCalculatorBase(DECIMAL);
        getCalculator().setCalculatorType(BASIC);
        LOGGER.info("Finished configuring the buttons");
    }

    public void addComponentsToPanel()
    {
        getConstraints().fill = GridBagConstraints.BOTH;
        getCalculator().getTextArea().setBorder(new LineBorder(Color.BLACK));
        getConstraints().insets = new Insets(5,0,5,0);
        addComponent(getCalculator().getTextArea(), 0, 0, 5, 2);
        getConstraints().insets = new Insets(5,5,5,5);
        // Also add the action listener for each button
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryStore(), 2, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryClear(), 2, 1, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryRecall(), 2, 2, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryAddition(), 2, 3, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemorySubtraction(), 2, 4, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton0(), 7, 0, 2, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonDot(), 7, 2, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonAdd(), 7, 3, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton1(), 6, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton2(), 6, 1, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton3(), 6, 2, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonSubtract(), 6, 3, 1, 1);

        getConstraints().fill = GridBagConstraints.BOTH;
        addComponent(getCalculator().getButtonEquals(), 6, 4, 1, 2);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton4(), 5, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton5(), 5, 1, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton6(), 5, 2, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMultiply(), 5, 3, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getButtonFraction(), 5, 4, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton7(), 4, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton8(), 4, 1, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButton9(), 4, 2, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonDivide(), 4, 3, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getButtonPercent(), 4, 4, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonDelete(), 3, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonClearEntry(), 3, 1, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonClear(), 3, 2, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonNegate(), 3, 3, 1, 1);

        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getButtonSqrt(), 3, 4, 1, 1);
        LOGGER.info("Buttons added to panel");
    }

    public void performBasicCalculatorTypeSwitchOperations(JPanel oldPanel)
    {
        LOGGER.info("Switching to the basic panel...");
        getCalculator().clearNumberButtonFunctionalities();
        getCalculator().clearAllBasicOperationButtons();
        getCalculator().clearAllOtherBasicCalculatorButtons();
        getCalculator().clearVariableNumberOfButtonsFunctionalities();
        setupBasicPanel();
        getLogger().info("Basic panel setup");
        setupHelpMenu();
        convertTextArea();
        if (!getCalculator().isMemoryValuesEmpty()) getCalculator().convertMemoryValues();
        LOGGER.info("Finished performBasicCalculatorTypeSwitchOperations");
    }

    // To set up the help menu in the menu bar for each converter
    private void setupHelpMenu()
    {
        String helpString = "<html>" +
                "How to use the " + BASIC.getName() + " Calculator<br><br>" +
                "For basic usage: <br>" +
                "Step 1.<br> Enter your first number followed by<br> an operator. Both will be visible in<br>" +
                "the text area.<br>" +
                "Step 2.<br> Enter the second number.<br>Entering the second number clears<br> the text area. <br>" +
                "Step 3.<br> Push the Equals button to see the<br> result. <br><br>" +
                "See button specific info below. <br>" +
                "The Dot Button: <br>" +
                "Pressing the dot button adds a<br> decimal to the number.<br> The button will become inactive<br>" +
                "until the next number is ready for<br> entry or the text area is cleared. <br><br>" +
                "The 1/x Button: <br>" +
                "This is the fraction button. It is<br> specific to say that when pushed,<br> it will take the number in the<br>" +
                "text area, rewrite it as 1 over the<br> number, and return the result.<br> For example, with 4 " +
                "in the text area,<br> pushing the fraction button<br> would return 0.25. <br><br>" +
                "The Percent button: <br>"+
                "The percent button takes the<br> number in the text area and returns<br> the percentage equivalent. " +
                "For example, with 10 in the text area, pushing the percent button would return 0.1. <br><br>" +
                "The Delete Button: <br>"+
                "The delete button removes the last<br> digit from the right until the entire<br> number is removed. <br><br>" +
                "The CE Button: <br>"+
                "The clear entry button clears the<br> text area. It does not clear what is<br> copied. <br><br>"+
                "The C Button: <br>"+
                "The clear button clears the text area,<br> and what is saved in copy. <br><br>"+
                "The Negate Button <br>"+
                "The negate button makes a positive number<br> a negative and a negative number a<br> positive one. <br><br>"+
                "The Square Root Button: <br>"+
                "The square root button will take<br> the number in the text area and<br> return the square root of it. <br><br>"+
                "Memory Suite: <br>"+
                "The MC Button: <br>"+
                "The memory clear button clears the<br> first number saved in memory,<br> until all numbers are erased. <br><br>"+
                "The MR Button: <br>"+
                "The memory recall button recalls<br> the the last number saved in<br> memory, looping through them<br>"+
                "all, and starts over. <br><br>"+
                "The MS Button: <br>"+
                "The memory store button saved the<br> current number in the text area into<br> memory. You can"+
                "save up to 10<br> different numbers. <br><br>"+
                "The Memory Add Button: <br>"+
                "The memory add button will add<br> the number in the text area to the<br> last saved value in memory."+
                "The text<br> area will update to show the result. <br><br>"+
                "The Memory Subtract Button: <br>"+
                "The memory subtract button will<br> subtract the number in the text area<br> to the last saved value "+
                "in memory.<br> The text area will update to show<br> the result. <br><br>"+

                "</html>";
        createViewHelpMenu(helpString);
    }

    public void createViewHelpMenu(String helpString)
    {
        // 4 menu options: loop through to find the Help option
        for(int i=0; i < getCalculator().getBar().getMenuCount(); i++) {
            JMenu menuOption = getCalculator().getBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Help")) {
                // get the options. remove viewHelpItem
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("View Help"))
                    {
                        LOGGER.debug("Found the current View Help option");
                        break;
                    }
                    else if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("About"))
                    {
                        // do nothing at this moment
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(Calculator_v4.font);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel text = new JLabel(helpString);

                    JScrollPane scrollPane = new JScrollPane(text);
                    scrollPane.setPreferredSize(new Dimension(150, 200));
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    JOptionPane.showMessageDialog(getCalculator(),
                            scrollPane, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
                //menuOption.add(new JPopupMenu.Separator(), 1);
                //menuOption.add(getCalculator().createAboutCalculatorJMenuItem(), 2);
                //break; //?? for just changing one option could be ok. issue maybe if changing other options
            }
        }
    }

    public void addComponent(Component c, int row, int column, int width, int height)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        panelLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    /**
     * This method handles the logic when we switch from any type of calculator
     * to the Programmer type
     *
     * TODO: Implement this method
     */
    public void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        getCalculator().getNumberButtons().forEach(button -> {
            button.setFont(Calculator_v4.font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performBasicCalculatorNumberButtonActions);
        });
    }

    public void performBasicCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        getLogger().info("Starting basic calculator number button actions");
        getLogger().info("button: " + actionEvent.getActionCommand());
        if (!getCalculator().isFirstNumBool()) // do for second number
        {
            if (!getCalculator().isDotButtonPressed())
            {
                getCalculator().getTextArea().setText("");
                getCalculator().setTextarea(new StringBuffer().append(getCalculator().getTextArea().getText()));
                if (!getCalculator().isFirstNumBool()) {
                    getCalculator().setFirstNumBool(true);
                    getCalculator().setNumberIsNegative(false);
                } else
                    getCalculator().setDotButtonPressed(true);
                getCalculator().buttonDot.setEnabled(true);
            }
        }
        calculator.performNumberButtonActions(actionEvent.getActionCommand());
    }

    public void performSquareRootButtonActions(ActionEvent action)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        String errorStringNaN = "Not a Number";
        LOGGER.debug("text: " + calculator.getTextArea().getText().replace("\n",""));
        if (calculator.values[0].contains("E"))
        {
            String errorMsg = "Cannot perform square root operation. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            if (calculator.getTextArea().getText().equals("") || calculator.isNegativeNumber(calculator.getTextArea().getText()))
            {
                calculator.getTextArea().setText("\n"+errorStringNaN);
                calculator.textarea = new StringBuffer().append("\n"+errorStringNaN);
                calculator.confirm(errorStringNaN + "Cannot perform square root operation on blank/negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.valueOf(calculator.getTextArea().getText())));
                result = calculator.formatNumber(result);
                calculator.getTextArea().setText("\n"+result);
                calculator.textarea = new StringBuffer().append(result);
                calculator.confirm();
            }
        }
    }

    public void performPercentButtonActions(ActionEvent action)
    {
        LOGGER.info("PercentStoreButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (calculator.values[0].contains("E"))
        {
            String errorMsg = "Cannot perform percent operation. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
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

    public void performFractionButtonActions(ActionEvent action)
    {
        LOGGER.info("FracStoreButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (calculator.values[0].contains("E"))
        {
            String errorMsg = "Cannot perform fraction operation. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
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

    @Deprecated
    public void convertToDecimal() throws CalculatorError_v4
    {
        LOGGER.info("convertToDecimal started");
        String strToConvert = calculator.getTextAreaWithoutAnything();
        int appropriateLength = calculator.getBytes();
        LOGGER.debug("textarea: " + calculator.textarea);
        LOGGER.debug("appropriateLength: " + appropriateLength);
        String operator = null;
        boolean operatorIncluded = false;
        if (getCalculator().addBool || getCalculator().subBool ||
            getCalculator().mulBool || getCalculator().divBool)
            operatorIncluded = true;
        if (!operatorIncluded && StringUtils.isBlank(calculator.textarea.toString()) )
        {
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+"0");
            calculator.updateTextareaFromTextArea();
            calculator.values[calculator.valuesPosition] = "0";
            return; // force out
        }
        else if (calculator.textarea.length() < appropriateLength)
        {
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
        for(int i=0, k=appropriateLength-1; i<appropriateLength; i++, k--)
        {
            num1 = Double.valueOf(String.valueOf(calculator.textarea.charAt(i)));
            num2 = Math.pow(2,k);
            result = (num1 * num2) + result;
        }

        if (calculator.isDecimal(String.valueOf(result)))
        {
            strToConvert = calculator.clearZeroesAtEnd(String.valueOf(result));
        }
        else
        {
            strToConvert = String.valueOf(result);
        }

        if (operatorIncluded && StringUtils.isNotBlank(operator))
        {
            calculator.values[calculator.valuesPosition-1] = strToConvert;
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1) + operator + " " + strToConvert);
        }
        else
        {
            calculator.values[calculator.valuesPosition] = strToConvert;
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1) + strToConvert);
        }
        LOGGER.info("convertToDecimal finished");
        calculator.confirm("converted str: " + strToConvert);
    }

    /**
     * This method converts the textArea from a CalcType to CalcType.DECIMAL
     * Since values should always be in DECIMAL form, converting should be easy as pie.
     */
    public void convertTextArea()
    {
        LOGGER.debug("Going from programmer to decimal...");
            calculator.performInitialChecks();
            boolean operatorWasPushed = getCalculator().determineIfMainOperatorWasPushedBoolean();
            //String convertedValue = getCalculator().convertFromTypeToTypeOnValues(CalcType_v3.PROGRAMMER.getName(), CalcType_v3.BASIC.getName(), getCalculator().getTextAreaWithoutNewLineCharacters())[0];
            if (StringUtils.isNotBlank(getCalculator().getTextAreaWithoutAnything()))
            {
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator
                {
                    if (getCalculator().addBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                            + " + " + getCalculator().getValues()[0]);
                        getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0] + " +"));
                    }
                    else if (getCalculator().subBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " - " + getCalculator().getValues()[0]);
                        getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0] + " -"));
                    }
                    else if (getCalculator().mulBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " * " + getCalculator().getValues()[0]);
                        getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0] + " *"));
                    }
                    else if (getCalculator().divBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " / " + getCalculator().getValues()[0]);
                        getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0] + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but textArea has some value
                {
                    getCalculator().getTextArea().setText(
                        getCalculator().addNewLineCharacters(1) + getCalculator().getValues()[0]);
                    getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0]));
                }
            }
        // TODO: conversion from Scientific logic needed
    }

    /************* All Getters and Setters ******************/

    public static Logger getLogger() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }
    public GridBagLayout getPanelLayout() { return panelLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JButton getButtonFraction() { return buttonFraction; }
    public JButton getButtonPercent() { return buttonPercent; }
    public JButton getButtonSqrt() { return buttonSqrt; }
    public Calculator_v4 getCalculator() { return calculator; }

    public void setPanelLayout(GridBagLayout panelLayout) { this.panelLayout = panelLayout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(Calculator_v4 calculator) { this.calculator = calculator; }
}
