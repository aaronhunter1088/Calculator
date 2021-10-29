package version4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

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
    private StandardCalculator_v4 calculator;

    public JPanelBasic_v4(StandardCalculator_v4 calculator)
    {
        setCalculator(calculator);
        setMinimumSize(new Dimension(100,200));
        setPanelLayout(new GridBagLayout());
        setLayout(getPanelLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setupPanel(calculator);
        addComponentsToPanel();
    }

    /************* Start of methods here ******************/

    public void setupPanel(StandardCalculator_v4 calculator)
    {
        LOGGER.info("Starting setupPanel_v3");
        constraints.insets = new Insets(5,5,5,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
        calculator.getTextArea().setPreferredSize(new Dimension(70, 35));

        getCalculator().setupNumberButtons(true);

        buttonFraction.setFont(Calculator_v4.font);
        buttonFraction.setPreferredSize(new Dimension(35, 35) );
        buttonFraction.setBorder(new LineBorder(Color.BLACK));
        buttonFraction.setEnabled(true);
        
        buttonPercent.setFont(Calculator_v4.font);
        buttonPercent.setPreferredSize(new Dimension(35, 35) );
        buttonPercent.setBorder(new LineBorder(Color.BLACK));
        buttonPercent.setEnabled(true);
        
        buttonSqrt.setFont(Calculator_v4.font);
        buttonSqrt.setPreferredSize(new Dimension(35, 35) );
        buttonSqrt.setBorder(new LineBorder(Color.BLACK));
        buttonSqrt.setEnabled(true);
        LOGGER.info("End setupPanel_v3()");
    }
    public void addComponentsToPanel()
    {
        LOGGER.info("Starting addComponentsToPanel");
        getConstraints().fill = GridBagConstraints.BOTH;
        getCalculator().getTextArea().setBorder(new LineBorder(Color.BLACK));
        addComponent(getCalculator().getTextArea(), 0, 0, 5, 2);
        getConstraints().insets = new Insets(5,5,5,5);
        // Also add the action listener for each button
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryClear(), 2, 0, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryRecall(), 2, 1, 1, 1);
        getConstraints().fill = GridBagConstraints.HORIZONTAL;
        addComponent(getCalculator().getButtonMemoryStore(), 2, 2, 1, 1);
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

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button1, 6, 0, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button2, 6, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button3, 6, 2, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonSubtract, 6, 3, 1, 1);

        constraints.fill = GridBagConstraints.BOTH;
        addComponent(calculator.buttonEquals, 6, 4, 1, 2); // idk why its size is not showing on the application; leave a comment for me on why this is
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button4, 5, 0, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button5, 5, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button6, 5, 2, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonMultiply, 5, 3, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonFraction, 5, 4, 1, 1);
        buttonFraction.addActionListener(action -> {
            performFractionButtonActions(action);
        });
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button7, 4, 0, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button8, 4, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.button9, 4, 2, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDivide, 4, 3, 1, 1);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPercent, 4, 4, 1, 1);
        buttonPercent.addActionListener(action -> {
            performPercentButtonActions(action);
        });

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonDelete, 3, 0, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonClearEntry, 3, 1, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonClear, 3, 2, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(calculator.buttonNegate, 3, 3, 1, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonSqrt, 3, 4, 1, 1);
        buttonSqrt.addActionListener(action -> {
            performSquareRootButtonActions(action);
        });
        LOGGER.info("Finished addComponentsToPanel");
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
    public void performBasicCalculatorTypeSwitchOperations(JPanel oldPanel)
    {
        LOGGER.info("Starting to performBasicCalculatorTypeSwitchOperations");
        convertTextArea();
        if (!getCalculator().isMemoryValuesEmpty()) getCalculator().convertMemoryValues();
        // set CalcType now
        getCalculator().setBase(CalcType_v4.DECIMAL);
        getCalculator().setCalcType(CalcType_v4.BASIC);
        // setting up all the buttons
        getCalculator().setEnabledForAllNumberButtons(true);
        calculator.buttonNegate.setEnabled(true);
        LOGGER.info("Finished performBasicCalculatorTypeSwitchOperations");
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
    public void convertToDecimal()
    {
        LOGGER.info("convertToDecimal started");
        calculator.textarea = new StringBuffer().append(calculator.getTextAreaWithoutNewLineCharacters());
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
            calculator.textarea = calculator.clearZeroesAtEnd(String.valueOf(result));
        }
        else
        {
            calculator.textarea = new StringBuffer().append(String.valueOf(result));
        }

        if (operatorIncluded && StringUtils.isNotBlank(operator))
        {
            calculator.values[calculator.valuesPosition-1] = String.valueOf(calculator.textarea);
            calculator.textarea = new StringBuffer().append(operator).append(" ").append(calculator.textarea);
            calculator.getTextArea().setText("\n" + calculator.textarea);
        }
        else
        {
            calculator.values[calculator.valuesPosition] = String.valueOf(calculator.textarea);
            calculator.textarea = new StringBuffer().append(calculator.textarea);
            calculator.getTextArea().setText("\n" + calculator.textarea);
        }
        LOGGER.info("textarea: " + calculator.textarea);
        LOGGER.info("convertToDecimal finished");
        calculator.confirm("");
    }

    /**
     * This method converts the textArea from a CalcType to CalcType.DECIMAL
     * Since values should always be in DECIMAL form, converting should be easy as pie.
     */
    public void convertTextArea()
    {
        if (calculator.getCalcType() == CalcType_v4.PROGRAMMER)
        {
            LOGGER.debug("Going from programmer to decimal...");
            calculator.performInitialChecks();
            boolean operatorWasPushed = getCalculator().determineIfMainOperatorWasPushed();
            //String convertedValue = getCalculator().convertFromTypeToTypeOnValues(CalcType_v3.PROGRAMMER.getName(), CalcType_v3.BASIC.getName(), getCalculator().getTextAreaWithoutNewLineCharacters())[0];
            if (StringUtils.isNotBlank(getCalculator().getTextAreaWithoutNewLineCharacters()))
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
        }
        // TODO: conversion from Scientific logic needed
    }

    /************* All Getters and Setters ******************/

    public static Logger getLOGGER() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }
    public GridBagLayout getPanelLayout() { return panelLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JButton getButtonFraction() { return buttonFraction; }
    public JButton getButtonPercent() { return buttonPercent; }
    public JButton getButtonSqrt() { return buttonSqrt; }
    public StandardCalculator_v4 getCalculator() { return calculator; }

    public void setPanelLayout(GridBagLayout panelLayout) { this.panelLayout = panelLayout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(StandardCalculator_v4 calculator) { this.calculator = calculator; }
}
