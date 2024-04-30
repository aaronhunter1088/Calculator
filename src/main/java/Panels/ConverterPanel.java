package Panels;

import Calculators.Calculator_v4;
import Converters.AreaMethods;
import Types.ConverterType;
import Types.ConverterUnits;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Converters.AngleMethods;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static Calculators.Calculator_v4.*;
import static Types.ConverterType.*;
import static Types.CalculatorType.*;
import static Types.ConverterUnits.*;

public class ConverterPanel extends JPanel
{
    static { System.setProperty("appName", "ConverterPanel"); }
    private static final Logger LOGGER = LogManager.getLogger(ConverterPanel.class.getSimpleName());
    private static final long serialVersionUID = 4L;

    private GridBagLayout converterLayout;
    private GridBagConstraints constraints;
    private JLabel converterTypeName;
    private ConverterType converterType;
    private JTextField textField1, textField2;
    private JComboBox<ConverterUnits> unitOptions1, unitOptions2;
    private JTextArea bottomSpaceAboveNumbers;
    private Calculator_v4 calculator;
    private JPanel numbersPanel;
    private boolean isTextField1Selected;

    /************* Constructors ******************/
    public ConverterPanel() { LOGGER.info("Converter panel created"); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator the Calculator_v4 to use
     * @param converterType the converter type to use
     */
    public ConverterPanel(Calculator_v4 calculator, ConverterType converterType) { setupConverterPanel(calculator, converterType); }

    /************* Start of methods here ******************/
    private void setupConverterPanel(Calculator_v4 calculator, ConverterType converterType)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setMaximumSize(new Dimension(300,400));
        //setupEditMenu();
        setupHelpMenu(converterType);
        setupConverterPanelComponents(converterType);
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up converter panel");
    }

    private void setupConverterPanelComponents(ConverterType converterType)
    {
        calculator.setCalculatorType(CONVERTER);
        calculator.setConverterType(converterType);
        setupAllConverterButtonsFunctionalities();
        switch (calculator.getConverterType())
        {
            case ANGLE: {
                setupAngleConverter();
                break;
            }
            case AREA: {
                setupAreaConverter();
                break;
            }
            default: {
                LOGGER.error("Unknown converterType: " + calculator.getConverterType());
            }
        }
        textField1.requestFocusInWindow();
        LOGGER.info("Finished setting up the panel");
    }

    private void addComponentsToPanel()
    {
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        addComponent(converterTypeName, 0,0,1,1, 1.0,1.0);
        addComponent(textField1, 1, 0, 0, 1,1.0,1.0);
        addComponent(unitOptions1, 3, 0, 0,1, 1.0,1.0);
        addComponent(textField2, 4, 0, 0, 1,1.0,1.0);
        addComponent(unitOptions2, 6, 0, 0,1, 1.0,1.0);
        // numbers are added on top of a single panel
        constraints.anchor = GridBagConstraints.PAGE_START;
        addComponent(numbersPanel, 7, 0, 0, 1, 1.0, 1.0);
        LOGGER.info("Finished adding components to panel");
    }

    private void setupAllConverterButtonsFunctionalities()
    {
        LOGGER.info("Starting to setup all converter button functions");
        // Clear the buttons I will use of their functionality (other than numbers)
        calculator.clearAllOtherBasicCalculatorButtons();
        // Clear Entry button functionality
        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonFunctionality);
        // Delete button functionality
        calculator.getButtonDelete().addActionListener(this::performDeleteButtonFunctionality);
        // Set up decimal button
        calculator.getButtonDot().addActionListener(this::performDotButtonFunctionality);
        // Number button functionalities
        // First clear all functionality assigned to them
        calculator.clearNumberButtonFunctionalities();
        setupNumberButtons();
        calculator.setupButtonBlank1();
        calculator.setupButtonBlank2();

        setupClearEntryButton();
        setupDeleteButton();
        setupDotButton();
        // Next, set up each number button. 0 is a bit different from the rest.
//        calculator.getButton0().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton1().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton2().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton3().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton4().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton5().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton6().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton7().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton8().addActionListener(this::performNumberButtonFunctionality);
//        calculator.getButton9().addActionListener(this::performNumberButtonFunctionality);
        LOGGER.info("Finished setting up all converter button functions");
    }

    public void performClearEntryButtonFunctionality(ActionEvent ae)
    {
        LOGGER.info("ClearEntryButtonHandler() started for Converter");
        LOGGER.info("button: " + ae.getActionCommand());
        textField1.setText("0");
        textField2.setText("0");
        textField1.requestFocusInWindow();
        isTextField1Selected = true;
        LOGGER.info("ClearEntryButtonHandler() finished for Converter");
        calculator.confirm("ClearEntryButton pushed");
    }

    public void performDeleteButtonFunctionality(ActionEvent ae)
    {
        LOGGER.info("DeleteButtonHandler() started for Converter");
        LOGGER.info("button: " + ae.getActionCommand());
        if (isTextField1Selected) {
            if (textField1.getText().length() == 1) textField1.setText("0");
            else textField1.setText(textField1.getText().substring(0, textField1.getText().length()-1));
        }
        else {
            if (textField2.getText().length() == 1) textField2.setText("0");
            else textField2.setText(textField2.getText().substring(0, textField2.getText().length()-1));
        }
        LOGGER.info("DeleteButtonHandler() finished for Converter");
        convertAndUpdatePanel();
        calculator.confirm("DeleteButton pushed");
    }

    public void performDotButtonFunctionality(ActionEvent ae)
    {
        LOGGER.info("DotButtonHandler() started for Converter");
        LOGGER.info("button: " + ae.getActionCommand());
        if (isTextField1Selected) {
            textField1.setText(textField1.getText() + ".");
        }
        else {
            textField2.setText(textField2.getText() + ".");
        }
        LOGGER.info("DotButtonHandler() finished for Converter");
        calculator.confirm("DotButton pushed");
    }

    private void setupNumberButtons()
    {
        AtomicInteger i = new AtomicInteger(0);
        getCalculator().getNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(true);
            if (button.getText().equals("0") &&
                    getCalculator().getCalculatorType() != CONVERTER)
            { button.setPreferredSize(new Dimension(70, 35)); }
            else
            { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performNumberButtonActions);
        });
        LOGGER.info("Number buttons configured");
    }
    /**
     * The action to perform when clicking any number button
     * @param actionEvent the click event
     */
    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        // check if we are in textField1 or textField2
        // check to see what converter we are using
        // check to see what unit we are using TODO: may not be necessary at this point
        // grab the number, add next number, add symbol
        String buttonValue = actionEvent.getActionCommand();
        LOGGER.info("Pressed " + buttonValue);
        if (isTextField1Selected)
        {
            switch ((ConverterUnits) Objects.requireNonNull(unitOptions1.getSelectedItem())) {
                case DEGREES :
                case RADIANS :
                case GRADIANS:
                case SQUARE_MILLIMETERS :
                case SQUARE_CENTIMETERS :
                case SQUARE_METERS :
                case HECTARES :
                case SQUARE_KILOMETERS :
                case SQUARE_INCHES :
                case SQUARE_FEET :
                case SQUARE_YARD_ACRES :
                case SQUARE_MILES :
                {
                    if (!textField1.getText().equals("0")) {
                        textField1.setText(textField1.getText() + buttonValue);
                    } else {
                        if (textField1.getText().equals("0")) {
                            textField1.setText(buttonValue);
                        }
                        else {
                            textField1.setText(textField1.getText() + buttonValue);
                        }
                    }
                    break;
                }
                default : { LOGGER.error("Unknown unit"); break; }
            }
        }
        else
        {
            switch ((ConverterUnits) Objects.requireNonNull(unitOptions2.getSelectedItem())) {
                case DEGREES :
                case RADIANS :
                case GRADIANS:
                case SQUARE_MILLIMETERS :
                case SQUARE_CENTIMETERS :
                case SQUARE_METERS :
                case HECTARES :
                case SQUARE_KILOMETERS :
                case SQUARE_INCHES :
                case SQUARE_FEET :
                case SQUARE_YARD_ACRES :
                case SQUARE_MILES :
                {
                    if (!textField2.getText().equals("0")) {
                        textField2.setText(textField2.getText() + buttonValue);
                    } else {
                        if (textField2.getText().equals("0")) {
                            textField2.setText(buttonValue);
                        }
                        else {
                            textField2.setText(textField2.getText() + buttonValue);
                        }
                    }
                    break;
                }
                default : { LOGGER.error("Unknown unit"); break; }
            }
        }
        LOGGER.info("Finished numberButtonFunctionality");
        convertAndUpdatePanel();
    }

    private void setupClearEntryButton()
    {
        calculator.getButtonClearEntry().setFont(mainFont);
        calculator.getButtonClearEntry().setMaximumSize(new Dimension(35, 35));
        calculator.getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClearEntry().setEnabled(true);
        calculator.getButtonClearEntry().setName("CE");
        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }
    /**
     * The action to perform when clicking the ClearEntry button
     * @param action the click action
     */
    public void performClearEntryButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        calculator.getTextArea().setText("");
        calculator.updateTextAreaValueFromTextArea();
        if (calculator.getValues()[1].isEmpty()) { // if temp[1] is empty, we know we are at temp[0]
            calculator.getValues()[0] = "";
            calculator.setAdding(false);
            calculator.setSubtracting(false);
            calculator.setMultiplying(false);
            calculator.setDividing(false);
            calculator.setValuesPosition(0);
            calculator.setFirstNumber(true);
        }
        else {
            calculator.getValues()[1] = "";
        }
        calculator.setDotPressed(false);
        calculator.getButtonDot().setEnabled(true);
        calculator.setNumberNegative(false);
        calculator.getTextAreaValue().append(calculator.getTextArea().getText());
        LOGGER.info("ClearEntryButtonHandler() finished");
        calculator.confirm();
    }

    private void setupDeleteButton()
    {
        calculator.getButtonDelete().setFont(mainFont);
        calculator.getButtonDelete().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDelete().setEnabled(true);
        calculator.getButtonDelete().setName("←");
        calculator.getButtonDelete().addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }
    public void performDeleteButtonActions(ActionEvent action)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[1].isEmpty())
        { calculator.setValuesPosition(0); } // assume they just previously pressed an operator

        LOGGER.info("values["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.info("textarea: " + calculator.getTextAreaValue());
        calculator.setNumberNegative(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        // this check has to happen
        calculator.setDotPressed(calculator.isDecimal(calculator.getTextAreaValue().toString()));
        calculator.getTextArea().setText(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        calculator.updateTextAreaValueFromTextArea();
        if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 1)
                    {
                        calculator.getTextArea().setText("");
                        calculator.setTextAreaValue(new StringBuffer().append(" "));
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextAreaValue().length() >= 2)
                    {
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length()-1)));
                        calculator.getTextArea().setText(calculator.addNewLineCharacters()+ calculator.getTextAreaValue());
                        calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaValue().toString();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else { //if (calculator.isDotPressed()) {
                    if (calculator.getTextAreaValue().length() == 2)
                    { // ex: 3. .... recall textarea looks like .3
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(calculator.getTextAreaValue().length() -1 ))); // ex: 3
                        calculator.setDotPressed(false);
                        calculator.getButtonDot().setEnabled(true);
                    }
                    else if (calculator.getTextAreaValue().length() == 3) { // ex: 3.2 or 0.5
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 2))); // ex: 3 or 0
                        calculator.setDotPressed(false);
                        calculator.getButtonDot().setEnabled(true);
                    }
                    else if (calculator.getTextAreaValue().length() > 3)
                    { // ex: 3.25 or 0.50 or 5.02 or 78.9
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 1))); // inclusive
                        if (calculator.getTextAreaValue().toString().endsWith("."))
                            calculator.setTextAreaValue(new StringBuffer().append(calculator.getNumberOnLeftSideOfNumber(calculator.getTextAreaValue().toString())));
                    }
                    LOGGER.info("output: " + calculator.getTextAreaValue());
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaValue());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaValue().toString();
                }
            }
            else // if (calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 2)
                    { // ex: -3
                        calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextArea().setText(calculator.getTextAreaValue().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextAreaValue().length() >= 3)
                    { // ex: -32 or + 6-
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextAreaValue().toString())));
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length())));
                        calculator.getTextArea().setText(calculator.getTextAreaValue() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue();
                    }
                    LOGGER.info("output: " + calculator.getTextAreaValue());
                }
                // if no operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 4) { // -3.2
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextAreaValue().toString()))); // 3.2
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, 1))); // 3
                        calculator.setDotPressed(false);
                        calculator.getTextArea().setText(calculator.getTextAreaValue() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue();
                    }
                    else if (calculator.getTextAreaValue().length() > 4)
                    { // ex: -3.25 or -0.00
                        calculator.getTextAreaValue().append(calculator.convertToPositive(calculator.getTextAreaValue().toString())); // 3.00 or 0.00
                        calculator.getTextAreaValue().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length())); // 3.0 or 0.0
                        calculator.getTextAreaValue().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextAreaValue().toString())); // 3 or 0
                        calculator.getTextArea().setText(calculator.getTextAreaValue() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue();
                    }
                    LOGGER.info("output: " + calculator.getTextAreaValue());
                }
            }

        }
        else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if an operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 1)
                    { // ex: 5
                        calculator.setTextAreaValue(new StringBuffer());
                    }
                    else if (calculator.getTextAreaValue().length() == 2)
                    {
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 1 )));
                    }
                    else if (calculator.getTextAreaValue().length() >= 2)
                    { // ex: 56 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                            calculator.setAdding(false);
                            calculator.setSubtracting(false);
                            calculator.setMultiplying(false);
                            calculator.setDividing(false);
                        }
                        else {
                            calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length()-1)));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextAreaValue());
                    calculator.getTextArea().setText("\n" + calculator.getTextAreaValue());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaValue().toString();
                    calculator.confirm();
                }
                // if an operator has been pushed; number is positive; number is decimal
                else //if (isDotPressed)
                {
                    if (calculator.getTextAreaValue().length() == 2) // ex: 3.
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 1 )));
                    else if (calculator.getTextAreaValue().length() == 3)
                    { // ex: 3.2 0.0
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 2 ))); // 3 or 0
                        calculator.setDotPressed(false);
                    }
                    else if (calculator.getTextAreaValue().length() > 3)
                    { // ex: 3.25 or 0.50  or + 3.25-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                        }
                        else {
                            calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length() - 1 )));
                            calculator.getTextAreaValue().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextAreaValue().toString()));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextAreaValue());
                    calculator.getTextArea().setText("\n"+ calculator.getTextAreaValue());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaValue().toString();
                    calculator.confirm();
                }
            }
            else //if (isNumberNegative)
            {
                // if an operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 2) { // ex: -3
                        calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextArea().setText(calculator.getTextAreaValue().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextAreaValue().length() >= 3) { // ex: -32 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) {
                            calculator.getTextAreaValue().append(calculator.getValues()[calculator.getValuesPosition()]);
                        }
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextAreaValue().toString())));
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length())));
                        calculator.getTextArea().setText("\n" + calculator.getTextAreaValue() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue();
                    }
                    LOGGER.info("textarea: " + calculator.getTextAreaValue());
                    calculator.confirm();
                }
                // if an operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextAreaValue().length() == 4) { // -3.2
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextAreaValue().toString()))); // 3.2 or 0.0
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, 1))); // 3 or 0
                        calculator.setDotPressed(false);
                        calculator.getTextArea().setText(calculator.getTextAreaValue() + "-"); // 3- or 0-
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue(); // -3 or -0
                    }
                    else if (calculator.getTextAreaValue().length() > 4) { // ex: -3.25  or -0.00
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextAreaValue().toString()))); // 3.25 or 0.00
                        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaValue().substring(0, calculator.getTextAreaValue().length()))); // 3.2 or 0.0
                        calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getTextAreaValue().toString());
                        LOGGER.info("textarea: " + calculator.getTextAreaValue());
                        if (calculator.getTextAreaValue().toString().equals("0"))
                        {
                            calculator.getTextArea().setText(calculator.getTextAreaValue().toString());
                            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaValue().toString();
                        }
                        else {
                            calculator.getTextArea().setText(calculator.getTextAreaValue() + "-");
                            calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextAreaValue();
                        }
                    }
                    LOGGER.info("textarea: " + calculator.getTextAreaValue());
                }
            }
            calculator.resetBasicOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        calculator.confirm();
    }

    private void setupDotButton()
    {
        calculator.getButtonDot().setFont(mainFont);
        calculator.getButtonDot().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDot().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDot().setEnabled(true);
        calculator.getButtonDot().setName(".");
        calculator.getButtonDot().addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }
    public void performDotButtonActions(ActionEvent action)
    {
        LOGGER.info("Starting Dot button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[0].contains("E")) { calculator.confirm("Cannot press dot button. Number too big!"); }
        else
        {
            LOGGER.warn("IMPLEMENT Dot Button Actions!");
        }
        calculator.confirm("Pressed the Dot button");
    }
    private void performDot(String buttonChoice)
    {
        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]) || !calculator.isFirstNumber())
        {   // dot pushed with nothing in textArea
            calculator.setTextAreaValue(new StringBuffer().append("0").append(buttonChoice));
            calculator.setValuesToTextAreaValue();
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]) && !calculator.isDotPressed())
        {   // number and then dot is pushed ex: 5 -> .5
            //StringBuffer lodSB = new StringBuffer(textareaValue);
            calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice));
            calculator.setValuesToTextAreaValue();
            calculator.setTextAreaValue(new StringBuffer().append(buttonChoice).append(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
            calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.setDotPressed(true); //!LEAVE. dot logic should not be executed anymore for the current number
        }
        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
        {
            calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()])));
            calculator.getTextAreaValue().append(buttonChoice);
            calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToNegative(calculator.getTextAreaValue().toString())));
            calculator.setValuesToTextAreaValue();
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
        calculator.getButtonDot().setEnabled(false); // deactivate button now that its active for this number
        calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button
    }

    private void convertAndUpdatePanel()
    {
        LOGGER.info("Performing automatic conversion after each number button");
        switch (calculator.getConverterType())
        {
            case ANGLE:
                AngleMethods.convertValues(calculator);
                break;
            case AREA:
                AreaMethods.convertValues(calculator);
                break;
        }
        calculator.confirm("Conversion done");
        repaint();
    }

    @SuppressWarnings("Duplicates")
    private void addComponent(Component c, int row, int column, int width, int height, double weighty, double weightx)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = weighty;
        constraints.weightx = weightx;
        constraints.insets = new Insets(0, 0, 0, 0);
        converterLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    private void addComponentToNumbersPanel(Component c, int row, int column, int width, int height, double weighty, double weightx)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = weighty;
        constraints.weightx = weightx;
        constraints.insets = new Insets(0, 0, 0, 0);
        ((GridBagLayout)numbersPanel.getLayout()).setConstraints(c, constraints); // set constraints
        numbersPanel.add(c);
    }

    private void setupHelpMenu(ConverterType converterType)
    {
        switch (converterType)
        {
            case ANGLE : {
                //LOGGER.debug("Size of MenuBar: " + getCalculator().bar.getMenuCount());
                // which is the number of menu choices
                String helpString = "<html>How to use the " + ANGLE.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                createViewHelpMenu(helpString);
                break;
            }
            case AREA : {
                String helpString = "<html>How to use the " + AREA.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                createViewHelpMenu(helpString);
                break;
            }
            default : {

            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void createViewHelpMenu(String helpString)
    {
        // 4 menu options: loop through to find the Help option
        for(int i=0; i < calculator.getCalculatorMenuBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getCalculatorMenuBar().getMenu(i);
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
                        LOGGER.warn("IMPLEMENT ABOUT MENU");
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(mainFont);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(helpString,
                            calculator.getBlankIcon(), SwingConstants.CENTER);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                    JPanel mainPanel = new JPanel();
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(this,
                            mainPanel, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
    }

    private void setupEditMenu()
    {
        for(int i = 0; i < calculator.getCalculatorMenuBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getCalculatorMenuBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Edit")) {
                LOGGER.info("Found the edit option");
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("Copy"))
                    {
                        LOGGER.info("Found copy");
                        // remove old option
                        menuOption.remove(valueForThisMenuOption);
                        // create new copy here
                        JMenuItem copyItem = new JMenuItem("Copy");
                        copyItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
                        copyItem.setFont(mainFont);
                        copyItem.setName("Copy");
                        copyItem.addActionListener(this::createCopyFunctionalityForConverter);
                        menuOption.add(copyItem, 0);
                    }
                    else if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("Paste"))
                    {
                        LOGGER.info("Found paste");
                        // remove old option
                        menuOption.remove(valueForThisMenuOption);
                        // create new copy here
                        JMenuItem pasteItem = new JMenuItem("Paste");
                        pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
                        pasteItem.setFont(mainFont);
                        pasteItem.setName("Paste");
                        pasteItem.addActionListener(this::createPasteFunctionalityForConverter);
                        menuOption.add(pasteItem, 1);
                    }
                }
            }
        }
    }

    private void createCopyFunctionalityForConverter(ActionEvent ae)
    {
        if (isTextField1Selected) {
            calculator.getValues()[2] = textField1.getText();
        } else {
            calculator.getValues()[2] = textField2.getText();
        }
        calculator.confirm("Copied " + calculator.getValues()[2]);
    }

    private void createPasteFunctionalityForConverter(ActionEvent ae)
    {
        if (isTextField1Selected) {
            textField1.setText(calculator.getValues()[2]);
        } else {
            textField2.setText(calculator.getValues()[2]);
        }
        calculator.confirm("Pasted " + calculator.getValues()[2]);
    }

    private void setupAngleConverter()
    {
        LOGGER.info("Starting Angle specific setup");
        setupConverter(ANGLE.getName());
        setupHelpMenu(ANGLE);
        setConverterType(ANGLE);
        setUnitOptions1(new JComboBox<>(){{ addItem(DEGREES); addItem(RADIANS); addItem(GRADIANS); }});
        setUnitOptions2(new JComboBox<>(){{ addItem(DEGREES); addItem(RADIANS); addItem(GRADIANS); }});
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
        bottomSpaceAboveNumbers.setEnabled(false);
        getUnitOptions1().addActionListener(this::performAngleUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAngleUnitsSwitch);
        LOGGER.info("Ending Angle specific setup");
    }

    private void setupAreaConverter()
    {
        LOGGER.info("Starting setup");
        setupConverter(AREA.getName());
        setupHelpMenu(AREA);
        setConverterType(AREA);
        setUnitOptions1(new JComboBox<>(){{ addItem(SQUARE_MILLIMETERS); addItem(SQUARE_CENTIMETERS); addItem(SQUARE_METERS); addItem(HECTARES); addItem(SQUARE_KILOMETERS); addItem(SQUARE_INCHES); addItem(SQUARE_FEET); addItem(SQUARE_YARD_ACRES); addItem(SQUARE_MILES); }});
        setUnitOptions2(new JComboBox<>(){{ addItem(SQUARE_MILLIMETERS); addItem(SQUARE_CENTIMETERS); addItem(SQUARE_METERS); addItem(HECTARES); addItem(SQUARE_KILOMETERS); addItem(SQUARE_INCHES); addItem(SQUARE_FEET); addItem(SQUARE_YARD_ACRES); addItem(SQUARE_MILES); }});
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
        bottomSpaceAboveNumbers.setEnabled(false);
        getUnitOptions1().addActionListener(this::performAreaUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAreaUnitsSwitch);
        LOGGER.info("Ending Area specific setup");
    }

    private void setupConverter(String nameOfConverter)
    {
        LOGGER.info("Starting " + nameOfConverter + " Converter setup");
        setConverterTypeName(new JLabel(nameOfConverter));
        converterTypeName.setFont(verdanaFontBold);

        setupEditMenu();
        setupHelpMenu(calculator.getConverterType());

        setTextField1(new JTextField());
        textField1.setText("0");
        textField1.setFont(verdanaFontBold);
        textField1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isTextField1Selected = true;
            }
            @Override
            public void focusLost(FocusEvent e) {}
        });
        setTextField2(new JTextField());
        textField2.setText("0");
        textField2.setFont(verdanaFontBold);
        textField2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isTextField1Selected = false;
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        textField1.grabFocus();
        setNumbersPanel(new JPanel());
        numbersPanel.setLayout(new GridBagLayout());
        numbersPanel.setBackground(Color.BLACK);
        numbersPanel.setBorder(new LineBorder(Color.BLACK));
        addComponentToNumbersPanel(calculator.getButtonBlank1(), 0, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButtonClearEntry(), 0, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButtonDelete(), 0, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton7(), 1, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton8(), 1, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton9(), 1, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton4(), 2, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton5(), 2, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton6(), 2, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton1(), 3, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton2(), 3, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButton3(), 3, 2, 1, 1, 1.0, 1.0);
        // TODO try getButtonBlank1().clone()
        addComponentToNumbersPanel(calculator.getButtonBlank2(), 4, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.getButtonDot(), 4, 1, 1, 1, 1.0, 1.0);
        calculator.getButton0().setPreferredSize(new Dimension(35, 35));
        addComponentToNumbersPanel(calculator.getButton0(), 4, 2, 1, 1, 1.0, 1.0);
        LOGGER.info("Ending " + nameOfConverter + " Converter setup");
    }

    private void performAngleUnitsSwitch(ActionEvent action)
    {
        LOGGER.info("Start performing Angle units switch");
        LOGGER.debug("action: " + action.getActionCommand());
        if (unitOptions1.hasFocus())
        {
            LOGGER.info("UnitOptions1 selected");
            isTextField1Selected = true;
        }
        else
        {
            LOGGER.info("UnitOptions2 selected");
            isTextField1Selected = false;
        }
        AngleMethods.convertValues(calculator);
        isTextField1Selected = !isTextField1Selected;
        calculator.confirm("Finished performing Angle units switch");
    }

    private void performAreaUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Area units switch");
        AreaMethods.convertValues(calculator);
        LOGGER.info("Finished performing Area units switch");
        calculator.confirm("IMPLEMENT: Units switched. Conversions executed");
    }

    public void performConverterCalculatorTypeSwitchOperations(Calculator_v4 calculator, ConverterType converterType)
    { setupConverterPanel(calculator, converterType); }

    /************* All Getters ******************/
    public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JLabel getConverterTypeName() { return converterTypeName; }
    public ConverterType getConverterType() { return converterType; }
    public JTextField getTextField1() { return textField1; }
    public JTextField getTextField2() { return textField2; }
    public JComboBox<ConverterUnits> getUnitOptions1() { return unitOptions1; }
    public JComboBox<ConverterUnits> getUnitOptions2() { return unitOptions2; }
    public JTextArea getBottomSpaceAboveNumbers() { return bottomSpaceAboveNumbers; }
    public Calculator_v4 getCalculator() { return calculator; }
    public JPanel getNumbersPanel() { return numbersPanel; }
    public boolean isTextField1Selected() { return isTextField1Selected; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout converterLayout) {
        super.setLayout(converterLayout);
        this.converterLayout = converterLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setConverterTypeName(JLabel converterTypeName) { this.converterTypeName = converterTypeName; }
    public void setConverterType(ConverterType converterType) { this.converterType = converterType; }
    public void setTextField1(JTextField textField1) { this.textField1 = textField1; }
    public void setTextField2(JTextField textField2) { this.textField2 = textField2; }
    public void setUnitOptions1(JComboBox<ConverterUnits> unitOptions1) { this.unitOptions1 = unitOptions1; }
    public void setUnitOptions2(JComboBox<ConverterUnits> unitOptions2) { this.unitOptions2 = unitOptions2; }
    public void setBottomSpaceAboveNumbers(JTextArea bottomSpaceAboveNumbers) { this.bottomSpaceAboveNumbers = bottomSpaceAboveNumbers; }
    public void setCalculator(Calculator_v4 calculator) { this.calculator = calculator; }
    public void setNumbersPanel(JPanel numbersPanel) { this.numbersPanel = numbersPanel; }
    public void setIsTextField1Selected(boolean isTextField1Selected) { this.isTextField1Selected = isTextField1Selected; }
}