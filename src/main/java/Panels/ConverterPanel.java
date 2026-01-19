package Panels;

import Calculators.Calculator;
import Converters.AngleMethods;
import Converters.AreaMethods;
import Types.CalculatorConverterType;
import Types.CalculatorConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;

import static Calculators.Calculator.mainFont;
import static Calculators.Calculator.verdanaFontBold;
import static Types.CalculatorConverterType.ANGLE;
import static Types.CalculatorConverterType.AREA;
import static Types.CalculatorConverterUnits.*;
import static Types.CalculatorView.VIEW_CONVERTER;
import static Types.Texts.ZERO;
import static Utilities.LoggingUtil.confirm;
import static Utilities.LoggingUtil.logActionButton;

/**
 * ConverterPanel
 * <p>
 * This class contains components and actions
 * for the ConverterPanel of the Calculator.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class ConverterPanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(ConverterPanel.class.getSimpleName());

    private Calculator calculator;
    private GridBagConstraints constraints;
    private CalculatorConverterType converterType;
    private JLabel converterTypeName;
    private JTextField textField1, textField2;
    private JComboBox<CalculatorConverterUnits> unitOptions1, unitOptions2;
    private JPanel currentConverterPanel;
    private boolean isTextField1Selected, isInitialized;

    /************* CONSTRUCTORS ******************/
    /**
     * A zero argument constructor for creating a ConverterPanel
     */
    public ConverterPanel()
    {
        this(ANGLE);
    }

    /**
     * Creates a ConverterPanel with the specified
     * converter type or defaults to ANGLE if null
     */
    public ConverterPanel(CalculatorConverterType converterType)
    {
        setName(VIEW_CONVERTER.getValue());
        setConverterType(converterType == null ? ANGLE : converterType);
        LOGGER.info("Converter panel created");
    }

    /**
     * The main constructor used to create a ConverterPanel
     *
     * @param calculator    the Calculator to use
     * @param converterType the converter type to use
     */
    public ConverterPanel(Calculator calculator, CalculatorConverterType converterType)
    {
        setupConverterPanel(calculator, converterType);
    }

    /**************** START OF METHODS ****************/
    /**
     * The main method used to define the ConverterPanel
     * and all of its components and their actions
     *
     * @param calculator    the Calculator object
     * @param converterType the CalculatorConverterType to use
     */
    public void setupConverterPanel(Calculator calculator, CalculatorConverterType converterType)
    {
        if (!isInitialized) {
            setCalculator(calculator);
            setLayout(new GridBagLayout());
            setConstraints(new GridBagConstraints()); // instantiate constraints
            setSize(new Dimension(200, 400)); // keep!!
        }
        setConverterType(converterType == null ? ANGLE : converterType);
        setupConverterPanelComponents(getConverterType());
        setupHelpMenu(getConverterType());
        addComponentsToPanel();
        setName(VIEW_CONVERTER.getValue());
        isInitialized = true;
        LOGGER.info("Finished setting up {} panel", VIEW_CONVERTER.getValue());
    }

    /**
     * Sets up the converter panel components
     */
    public void setupConverterPanelComponents(CalculatorConverterType converterType)
    {
        calculator.clearButtonActions();
        calculator.setupConverterPanelButtons();
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(converterType);
        switch (converterType) {
            case ANGLE: {
                setupAngleConverter();
                break;
            }
            case AREA: {
                setupAreaConverter();
                break;
            }
            default: {
                LOGGER.error("Unknown converterType: {}", calculator.getConverterType());
            }
        }
        textField1.requestFocusInWindow();
        LOGGER.info("Finished setting up the panel");
    }

    /**
     * Specifies where each button is placed on the ConverterPanel
     */
    public void addComponentsToPanel()
    {
        LOGGER.info("Add components to converter panel");
        // Logic is similar to updatingJPanel() in Calculator. Remove previous panel if present. Was overlapping due to recent changes
        if (currentConverterPanel != null) {
            remove(currentConverterPanel);
        }
        currentConverterPanel = new JPanel(new GridBagLayout());

        JPanel entryPanel = new JPanel(new GridBagLayout());
        calculator.addComponent(this, constraints, entryPanel, converterTypeName, 0, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        calculator.addComponent(this, constraints, entryPanel, textField1, 1, 0, null, 0, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, entryPanel, unitOptions1, 2, 0, null, 0, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, entryPanel, textField2, 3, 0, null, 0, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, entryPanel, unitOptions2, 4, 0, null, 0, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        calculator.addComponent(this, constraints, currentConverterPanel, entryPanel, 0, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        //
        //setNumbersPanel(new JPanel());
        JPanel numbersPanel = new JPanel(new GridBagLayout());
        //numbersPanel.setBackground(Color.BLACK);
        //numbersPanel.setBorder(new LineBorder(Color.BLACK));
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButtonBlank1(), 0, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButtonClearEntry(), 0, 1, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButtonDelete(), 0, 2, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton7(), 1, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton8(), 1, 1, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton9(), 1, 2, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton4(), 2, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton5(), 2, 1, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton6(), 2, 2, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton1(), 3, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton2(), 3, 1, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton3(), 3, 2, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButtonBlank2(), 4, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButtonDecimal(), 4, 1, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        calculator.addComponent(this, constraints, numbersPanel, calculator.getButton0(), 4, 2, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        // numbers are added on top of a single panel
        //constraints.anchor = GridBagConstraints.PAGE_START;
        calculator.addComponent(this, constraints, currentConverterPanel, numbersPanel, 1, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.PAGE_START);
        calculator.addComponent(this, constraints, currentConverterPanel);
        LOGGER.info("Finished adding components to panel");
    }

    /**
     * The action to perform when the ClearEntry button is clicked
     *
     * @param actionEvent the click action
     */
    public void performClearEntryButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearEntryButtonHandler started for Converter");
        logActionButton(actionEvent, LOGGER);
        textField1.setText(ZERO);
        textField2.setText(ZERO);
        calculator.getButtonDecimal().setEnabled(true);
        textField1.requestFocusInWindow();
        isTextField1Selected = true;
        LOGGER.info("ClearEntryButtonHandler finished for Converter");
        confirm(calculator, LOGGER, "ClearEntryButton pushed");
    }

    /**
     * The action to perform when the Decimal button is clicked
     *
     * @param actionEvent the click action
     */
    public void performDecimalButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Decimal button pushed");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (isTextField1Selected) {
            textField1.setText(textField1.getText() + ".");
        } else {
            textField2.setText(textField2.getText() + ".");
        }
        calculator.getButtonDecimal().setEnabled(false);
        LOGGER.info("DotButtonHandler() finished for Converter");
        confirm(calculator, LOGGER, "DotButton pushed");
    }

    /**
     * The action to perform when clicking any number button
     *
     * @param actionEvent the click event
     */
    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonValue = actionEvent.getActionCommand();
        logActionButton(buttonValue, LOGGER);
        if (isTextField1Selected) {
            if (textField1.getText().equals(ZERO)) {
                textField1.setText(buttonValue);
            } else {
                textField1.setText(textField1.getText() + buttonValue);
            }
        } else {
            if (textField2.getText().equals(ZERO)) {
                textField2.setText(buttonValue);
            } else {
                textField2.setText(textField2.getText() + buttonValue);
            }
        }
        LOGGER.info("Finished numberButtonFunctionality");
        performConversions();
    }

    /**
     * The actions to perform when the Delete button is clicked
     *
     * @param actionEvent the click action
     */
    public void performDeleteButtonActions(ActionEvent actionEvent)
    {
        String buttonValue = actionEvent.getActionCommand();
        logActionButton(buttonValue, LOGGER);
        if (isTextField1Selected && !textField1.getText().isEmpty()) {
            //if (textField1.getText().length() == 1) textField1.setText("0");
            //else
            textField1.setText(textField1.getText().substring(0, textField1.getText().length() - 1));
        } else if (!textField2.getText().isEmpty()) {
            //if (textField2.getText().length() == 1) textField2.setText("0");
            //else
            textField2.setText(textField2.getText().substring(0, textField2.getText().length() - 1));
        }
        LOGGER.info("DeleteButtonHandler() finished for Converter");
        //convertAndUpdatePanel();
        confirm(calculator, LOGGER, "DeleteButton pushed");
    }

    /**
     * Switches the converter type and updates the ConverterPanel
     */
    private void performConversions()
    {
        LOGGER.debug("Performing automatic conversion after each number button");
        switch (calculator.getConverterType()) {
            case ANGLE:
                AngleMethods.convertValues(calculator);
                break;
            case AREA:
                AreaMethods.convertValues(calculator);
                break;
        }
        confirm(calculator, LOGGER, "Conversion done");
    }

    /**
     * The main method which sets the help text
     * to be used in the help menu for the ConverterPanel
     */
    public void setupHelpMenu(CalculatorConverterType converterType)
    {
        String helpString = null;
        switch (converterType) {
            case ANGLE: {
                helpString = """
                        How to use the %s Converter
                        "Step 1. Select the unit for each conversion first.
                        "Step 2. Enter a number into either text field.
                        "Step 3. Observe the changed unit upon each entry.
                        """
                        .formatted(ANGLE.getValue());
                break;
            }
            case AREA: {
                helpString = """
                        How to use the %s Converter
                        "Step 1. Select the unit for each conversion first.
                        "Step 2. Enter a number into either text field.
                        "Step 3. Observe the changed unit upon each entry.
                        """
                        .formatted(AREA.getValue());
                break;
            }
            default: {
                LOGGER.warn("IMPLEMENT Converter Help text");
            }
        }
        calculator.setHelpString(helpString);
        calculator.updateShowHelp();
    }

    /**
     * The main method used to set up the Edit menu item
     */
    private void setupEditMenu()
    {
        for (int i = 0; i < calculator.getCalculatorMenuBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getCalculatorMenuBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Edit")) {
                LOGGER.info("Found the edit option");
                for (int j = 0; j < menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("Copy")) {
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
                    } else if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("Paste")) {
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

    /**
     * Specific copy functionality for ConverterPanel
     *
     * @param actionEvent the click action
     */
    private void createCopyFunctionalityForConverter(ActionEvent actionEvent)
    {
        if (isTextField1Selected) {
            calculator.getValues()[2] = textField1.getText();
        } else {
            calculator.getValues()[2] = textField2.getText();
        }
        confirm(calculator, LOGGER, "Copied " + calculator.getValues()[2]);
    }

    /**
     * Specific paste functionality for ConverterPanel
     *
     * @param actionEvent the click action
     */
    private void createPasteFunctionalityForConverter(ActionEvent actionEvent)
    {
        if (isTextField1Selected) {
            textField1.setText(calculator.getValues()[2]);
        } else {
            textField2.setText(calculator.getValues()[2]);
        }
        confirm(calculator, LOGGER, "Pasted " + calculator.getValues()[2]);
    }

    /**
     * The main method used to define the AngleConverter
     */
    public void setupAngleConverter()
    {
        LOGGER.info("Starting ANGLE specific setup");
        setupConverter(ANGLE.getValue());
        setupHelpMenu(ANGLE);
        setConverterType(ANGLE);
        setUnitOptions1(new JComboBox<>()
        {{
            addItem(DEGREES);
            addItem(RADIANS);
            addItem(GRADIANS);
        }});
        setUnitOptions2(new JComboBox<>()
        {{
            addItem(DEGREES);
            addItem(RADIANS);
            addItem(GRADIANS);
        }});
        getUnitOptions1().addActionListener(this::performAngleUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAngleUnitsSwitch);
        LOGGER.info("Ending Angle specific setup");
    }

    /**
     * The main method used to define the AreaConverter
     */
    public void setupAreaConverter()
    {
        LOGGER.info("Starting AREA specific setup");
        setupConverter(AREA.getValue());
        setupHelpMenu(AREA);
        setConverterType(AREA);
        setUnitOptions1(new JComboBox<>()
        {{
            addItem(SQUARE_MILLIMETERS);
            addItem(SQUARE_CENTIMETERS);
            addItem(SQUARE_METERS);
            addItem(HECTARES);
            addItem(SQUARE_KILOMETERS);
            addItem(SQUARE_INCHES);
            addItem(SQUARE_FEET);
            addItem(SQUARE_YARD_ACRES);
            addItem(SQUARE_MILES);
        }});
        setUnitOptions2(new JComboBox<>()
        {{
            addItem(SQUARE_MILLIMETERS);
            addItem(SQUARE_CENTIMETERS);
            addItem(SQUARE_METERS);
            addItem(HECTARES);
            addItem(SQUARE_KILOMETERS);
            addItem(SQUARE_INCHES);
            addItem(SQUARE_FEET);
            addItem(SQUARE_YARD_ACRES);
            addItem(SQUARE_MILES);
        }});
        //setBottomSpaceAboveNumbers(new JTextArea(1,10));
        //bottomSpaceAboveNumbers.setEnabled(false);
        getUnitOptions1().addActionListener(this::performAreaUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAreaUnitsSwitch);
        LOGGER.info("Ending Area specific setup");
    }

    /**
     * Builds the converter based on the CalculatorConverterType
     *
     * @param nameOfConverter
     */
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
        textField1.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                isTextField1Selected = true;
            }

            @Override
            public void focusLost(FocusEvent e)
            {
            }
        });
        setTextField2(new JTextField());
        textField2.setText("0");
        textField2.setFont(verdanaFontBold);
        textField2.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                isTextField1Selected = false;
            }

            @Override
            public void focusLost(FocusEvent e)
            {
            }
        });
        textField1.grabFocus();
        //setNumbersPanel(new JPanel());
        //numbersPanel.setLayout(new GridBagLayout());
        //numbersPanel.setBackground(Color.BLACK);
        //numbersPanel.setBorder(new LineBorder(Color.BLACK));
//        addComponentToNumbersPanel(calculator.getButtonBlank1(), 0, 0, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButtonClearEntry(), 0, 1, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButtonDelete(), 0, 2, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton7(), 1, 0, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton8(), 1, 1, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton9(), 1, 2, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton4(), 2, 0, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton5(), 2, 1, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton6(), 2, 2, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton1(), 3, 0, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton2(), 3, 1, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButton3(), 3, 2, 1, 1, 1.0, 1.0);
//        // TODO try getButtonBlank1().clone()
//        addComponentToNumbersPanel(calculator.getButtonBlank2(), 4, 0, 1, 1, 1.0, 1.0);
//        addComponentToNumbersPanel(calculator.getButtonDot(), 4, 1, 1, 1, 1.0, 1.0);
//        calculator.getButton0().setPreferredSize(new Dimension(35, 35));
//        addComponentToNumbersPanel(calculator.getButton0(), 4, 2, 1, 1, 1.0, 1.0);
        LOGGER.info("Ending " + nameOfConverter + " Converter setup");
    }

    /**
     * The actions to perform when converting angles
     *
     * @param actionEvent the click action
     */
    private void performAngleUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Angle units switch");
        LOGGER.debug("action: " + actionEvent.getActionCommand());
        if (unitOptions1.hasFocus()) {
            LOGGER.info("UnitOptions1 selected");
            isTextField1Selected = true;
        } else {
            LOGGER.info("UnitOptions2 selected");
            isTextField1Selected = false;
        }
        AngleMethods.convertValues(calculator);
        isTextField1Selected = !isTextField1Selected;
        confirm(calculator, LOGGER, "Finished performing Angle units switch");
    }

    /**
     * The actions to perform when converting areas
     *
     * @param actionEvent the click action
     */
    private void performAreaUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Area units switch");
        AreaMethods.convertValues(calculator);
        LOGGER.info("Finished performing Area units switch");
        confirm(calculator, LOGGER, "IMPLEMENT: Units switched. Conversions executed");
    }

    /**************** GETTERS ****************/
    //public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints()
    {
        return constraints;
    }

    /**************** SETTERS ****************/
    public void setConstraints(GridBagConstraints constraints)
    {
        this.constraints = constraints;
    }

    public JLabel getConverterTypeName()
    {
        return converterTypeName;
    }

    public void setConverterTypeName(JLabel converterTypeName)
    {
        this.converterTypeName = converterTypeName;
        LOGGER.debug("Converter Name: {}", converterTypeName);
    }

    public CalculatorConverterType getConverterType()
    {
        return converterType;
    }

    public void setConverterType(CalculatorConverterType converterType)
    {
        this.converterType = converterType;
        LOGGER.debug("Converter Type: {}", converterType);
    }

    public JTextField getTextField1()
    {
        return textField1;
    }

    public void setTextField1(JTextField textField1)
    {
        this.textField1 = textField1;
        LOGGER.debug("TextField1 set");
    }

    public JTextField getTextField2()
    {
        return textField2;
    }

    public void setTextField2(JTextField textField2)
    {
        this.textField2 = textField2;
        LOGGER.debug("TextField2 set");
    }

    public JComboBox<CalculatorConverterUnits> getUnitOptions1()
    {
        return unitOptions1;
    }

    public void setUnitOptions1(JComboBox<CalculatorConverterUnits> unitOptions1)
    {
        this.unitOptions1 = unitOptions1;
        LOGGER.debug("Unit Options 1 set");
    }

    public JComboBox<CalculatorConverterUnits> getUnitOptions2()
    {
        return unitOptions2;
    }

    public void setUnitOptions2(JComboBox<CalculatorConverterUnits> unitOptions2)
    {
        this.unitOptions2 = unitOptions2;
        LOGGER.debug("Unit Options 2 set");
    }

    public Calculator getCalculator()
    {
        return calculator;
    }

    public void setCalculator(Calculator calculator)
    {
        this.calculator = calculator;
        LOGGER.debug("Calculator set");
    }

    public boolean isTextField1Selected()
    {
        return isTextField1Selected;
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void setIsTextField1Selected(boolean isTextField1Selected)
    {
        this.isTextField1Selected = isTextField1Selected;
        LOGGER.debug("Textfield1 is {} selected", isTextField1Selected ? "" : "not");
    }
}