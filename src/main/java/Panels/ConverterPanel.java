package Panels;

import Calculators.Calculator;
import Converters.AreaMethods;
import Types.ConverterType;
import Types.ConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Converters.AngleMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.Objects;

import static Calculators.Calculator.*;
import static Types.ConverterType.*;
import static Types.CalculatorType.*;
import static Types.ConverterUnits.*;

public class ConverterPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ConverterPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;

    private GridBagLayout converterLayout;
    private GridBagConstraints constraints;
    private JLabel converterTypeName;
    private ConverterType converterType;
    private static JTextField textField1, textField2;
    private static JComboBox<ConverterUnits> unitOptions1, unitOptions2;
    //private JTextArea bottomSpaceAboveNumbers;
    private static Calculator calculator;
    private static boolean isTextField1Selected;

    /************* Constructors ******************/

    public ConverterPanel()
    {
        setName(CONVERTER.getValue());
        LOGGER.info("Converter panel created");
    }

    /**
     * A zero argument constructor for creating a ConverterPanel
     */
    public ConverterPanel(ConverterType converterType)
    {
        setName(CONVERTER.getValue());
        if (converterType == null) setConverterType(ANGLE);
        else setConverterType(converterType);
        LOGGER.info("Converter panel created");
    }

    /**
     * The main constructor used to create a ConverterPanel
     * @param calculator the Calculator to use
     * @param converterType the converter type to use
     */
    public ConverterPanel(Calculator calculator, ConverterType converterType)
    { setupConverterPanel(calculator, converterType); }

    /************* Start of methods here ******************/

    /**
     * The main method used to define the ConverterPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     * @param converterType the ConverterType to use
     */
    public void setupConverterPanel(Calculator calculator, ConverterType converterType)
    {
        setCalculator(calculator);
        setConverterType(converterType);
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setSize(new Dimension(200,400)); // keep!!
        //setupEditMenu();
        setupConverterPanelComponents(converterType != null ? converterType: ANGLE);
        setupHelpMenu(getConverterType());
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        setName(CONVERTER.getValue());
        LOGGER.info("Finished setting up converter panel");
    }

    /**
     * Clears button actions, sets the CalculatorType,
     * CalculatorBase, ConverterType, and finally
     * sets up the ConverterPanel and its components
     */
    private void setupConverterPanelComponents(ConverterType converterType)
    {
        calculator.setCalculatorType(CONVERTER);
        calculator.setConverterType(converterType);
        setupAllConverterButtonsFunctionalities();
        switch (converterType)
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

    /**
     * Specifies where each button is placed on the ConverterPanel
     */
    private void addComponentsToPanel()
    {
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.anchor = GridBagConstraints.CENTER;
        JPanel converterPanel = new JPanel(new GridBagLayout());

        JPanel entryPanel = new JPanel(new GridBagLayout());
        addComponent(entryPanel, converterTypeName, 0,0,1,1, 1.0,1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        addComponent(entryPanel, textField1, 1, 0, 0, 1,1.0,1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(entryPanel, unitOptions1, 2, 0, 0,1, 1.0,1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(entryPanel, textField2, 3, 0, 0, 1,1.0,1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(entryPanel, unitOptions2, 4, 0, 0,1, 1.0,1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        addComponent(converterPanel, entryPanel, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        //
        //setNumbersPanel(new JPanel());
        JPanel numbersPanel = new JPanel(new GridBagLayout());
        //numbersPanel.setBackground(Color.BLACK);
        //numbersPanel.setBorder(new LineBorder(Color.BLACK));
        addComponent(numbersPanel, calculator.getButtonBlank1(), 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButtonClearEntry(), 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButtonDelete(), 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton7(), 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton8(), 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton9(), 1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton4(), 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton5(), 2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton6(), 2, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton1(), 3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton2(), 3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton3(), 3, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButtonBlank2(), 4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButtonDecimal(), 4, 1, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        addComponent(numbersPanel, calculator.getButton0(), 4, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        // numbers are added on top of a single panel
        //constraints.anchor = GridBagConstraints.PAGE_START;
        addComponent(converterPanel, numbersPanel, 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.PAGE_START);
        addComponent(converterPanel);
        LOGGER.info("Finished adding components to panel");
    }

    /**
     * Adding a component enforcing the GridBagConstraints.BOTH
     * @param c the component to add
     * @param row the row to add the component to
     * @param column the column to add the component to
     * @param width the number of columns the component takes up
     * @param height the number of rows the component takes up
     */
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

    /**
     * Adding the number button component enforcing the GridBagConstraints.BOTH
     * @param c the component to add
     * @param row the row to add the component to
     * @param column the column to add the component to
     * @param width the number of columns the component takes up
     * @param height the number of rows the component takes up
     */
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
        //((GridBagLayout)numbersPanel.getLayout()).setConstraints(c, constraints); // set constraints
        //numbersPanel.add(c);
    }

    /**
     * Adds a component to a panel
     * @param panel the panel to add to
     * @param c the component to add to a panel
     * @param row the row to place the component in
     * @param column the column to place the component in
     * @param gridWidth the number of columns the component should use
     * @param gridHeight the number of rows the component should use
     * @param weightXRow set to allow the button grow horizontally
     * @param weightYColumn set to allow the button grow horizontally
     * @param fill set to make the component resize if any unused space
     * @param anchor set to place the component in a specific location on the frame
     */
    private void addComponent(JPanel panel, Component c, int row, int column, int gridWidth, int gridHeight, double weightXRow, double weightYColumn, int fill, int anchor)
    {
        constraints.gridy = row;
        constraints.gridx = column;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.weighty = weightXRow;
        constraints.weightx = weightYColumn;
        constraints.insets = new Insets(0, 0, 0, 0);
        if (fill != 0)   constraints.fill = fill;
        if (anchor != 0) constraints.anchor = anchor;
        if (c != null) panel.add(c, constraints);
        else           add(panel, constraints);
    }

    /** Primarily used to add the basicPanel to the frame */
    private void addComponent(JPanel panel)
    { addComponent(panel, null, 0, 0, 0, 0, 1.0, 1.0, 0, GridBagConstraints.CENTER); }

    /**
     * The main method used to set up the component functionalities
     */
    private void setupAllConverterButtonsFunctionalities()
    {
        LOGGER.info("Starting to setup all converter button functions");
        // Clear the buttons I will use of their functionality (other than numbers)
        calculator.clearAllOtherBasicCalculatorButtons();
        //setupClearEntryButton();
        //setupDeleteButton();
        //setupDotButton();
        //calculator.getButtonDecimal().addActionListener(this::performDotButtonFunctionality);
        // Number button functionalities
        // First clear all functionality assigned to them
        calculator.clearNumberButtonActions();
        calculator.setupNumberButtons();
        calculator.setupConverterButtons();
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
//
//    /**
//     * The main method to set up the ClearEntry button
//     */
//    private void setupClearEntryButton()
//    {
//        calculator.getButtonClearEntry().setFont(mainFont);
//        calculator.getButtonClearEntry().setMaximumSize(new Dimension(35, 35));
//        calculator.getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
//        calculator.getButtonClearEntry().setEnabled(true);
//        calculator.getButtonClearEntry().setName("CE");
//        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
//        LOGGER.info("Clear Entry button configured");
//    }
    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public static void performClearEntryButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearEntryButtonHandler started for Converter");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        textField1.setText("0");
        textField2.setText("0");
        //calculator.setDotPressed(false);
        calculator.getButtonDecimal().setEnabled(true);
        textField1.requestFocusInWindow();
        isTextField1Selected = true;
        LOGGER.info("ClearEntryButtonHandler finished for Converter");
        calculator.confirm("ClearEntryButton pushed");
    }

    /**
     * The action to perform when the Decimal button is clicked
     * @param actionEvent the click action
     */
    public static void performDecimalButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Decimal button pushed");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (isTextField1Selected) {
            textField1.setText(textField1.getText() + ".");
        }
        else {
            textField2.setText(textField2.getText() + ".");
        }
        calculator.getButtonDecimal().setEnabled(false);
        LOGGER.info("DotButtonHandler() finished for Converter");
        calculator.confirm("DotButton pushed");
    }

//    /**
//     * The main method to set up all number buttons, 0-9
//     */
//    private void setupNumberButtons()
//    {
//        AtomicInteger i = new AtomicInteger(0);
//        getCalculator().getNumberButtons().forEach(button -> {
//            button.setFont(mainFont);
//            button.setEnabled(true);
//            if (button.getText().equals("0") &&
//                    getCalculator().getCalculatorType() != CONVERTER)
//            { button.setPreferredSize(new Dimension(35, 35)); }
//            else
//            { button.setPreferredSize(new Dimension(35, 35)); }
//            button.setBorder(new LineBorder(Color.BLACK));
//            button.setName(String.valueOf(i.getAndAdd(1)));
//            button.addActionListener(this::performNumberButtonActions);
//        });
//        LOGGER.info("Number buttons configured");
//    }
    /**
     * The action to perform when clicking any number button
     * @param actionEvent the click event
     */
    public static void performNumberButtonActions(ActionEvent actionEvent)
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
                    if (textField2.getText().equals("0")) {
                        textField2.setText(buttonValue);
                    }
                    else {
                        textField2.setText(textField2.getText() + buttonValue);
                    }
                    break;
                }
                default : { LOGGER.error("Unknown unit"); break; }
            }
        }
        LOGGER.info("Finished numberButtonFunctionality");
        performConversions();
    }

//    private void setupDeleteButton()
//    {
//        calculator.getButtonDelete().setFont(mainFont);
//        calculator.getButtonDelete().setPreferredSize(new Dimension(35, 35));
//        calculator.getButtonDelete().setBorder(new LineBorder(Color.BLACK));
//        calculator.getButtonDelete().setEnabled(true);
//        calculator.getButtonDelete().setName("←");
//        calculator.getButtonDelete().addActionListener(this::performDeleteButtonActions);
//        LOGGER.info("Delete button configured");
//    }
    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public static void performDeleteButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("DeleteButtonHandler() started for Converter");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (isTextField1Selected && !textField1.getText().isEmpty()) {
            //if (textField1.getText().length() == 1) textField1.setText("0");
            //else
            textField1.setText(textField1.getText().substring(0, textField1.getText().length()-1));
        }
        else if (!textField2.getText().isEmpty()) {
            //if (textField2.getText().length() == 1) textField2.setText("0");
            //else
            textField2.setText(textField2.getText().substring(0, textField2.getText().length()-1));
        }
        LOGGER.info("DeleteButtonHandler() finished for Converter");
        //convertAndUpdatePanel();
        calculator.confirm("DeleteButton pushed");
    }

//    /**
//     * The main method to set up the Dot button
//     */
//    private void setupDotButton()
//    {
//        calculator.getButtonDecimal().setFont(mainFont);
//        calculator.getButtonDecimal().setPreferredSize(new Dimension(35, 35));
//        calculator.getButtonDecimal().setBorder(new LineBorder(Color.BLACK));
//        calculator.getButtonDecimal().setEnabled(true);
//        calculator.getButtonDecimal().setName(".");
//        calculator.getButtonDecimal().addActionListener(this::performDecimalButtonActions);
//        LOGGER.info("Dot button configured");
//    }
//    /**
//     * The actions to perform when the Dot button is click
//     * @param actionEvent the click action
//     */
//    public static void performDecimalButtonActions(ActionEvent actionEvent)
//    {
//        LOGGER.info("Starting Decimal button actions");
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("button: " + buttonChoice); // print out button confirmation
//        if (calculator.getValues()[0].contains("E")) { calculator.confirm("Cannot press dot button. Number too big!"); }
//        else
//        {
//            LOGGER.debug("Decimal Button Actions!");
//            performDecimal(buttonChoice);
//        }
//        calculator.confirm("Pressed the Dot button");
//    }
//    /**
//     * The inner logic of performing Dot actions
//     * @param buttonChoice the button choice
//     */
//    private static void performDecimal(String buttonChoice)
//    {
//        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]) || !calculator.isFirstNumber())
//        {
//            // dot pushed with nothing in textArea
//            calculator.getTextPane().setText("0" + buttonChoice);
//            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
//        }
//        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]) && !calculator.isDotPressed())
//        {
//            // number and then dot is pushed ex: 5 -> .5
//            calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
//            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
//            //StringBuffer lodSB = new StringBuffer(textareaValue);
//            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice));
//            //calculator.setValuesToTextAreaValue();
//            //calculator.setTextAreaValue(new StringBuffer().append(buttonChoice).append(calculator.getValues()[calculator.getValuesPosition()]));
//            //calculator.updateTheTextAreaBasedOnTheTypeAndBase();
//            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
//            calculator.getButtonDecimal().setEnabled(false);
//            //calculator.setDotPressed(true); //!LEAVE. dot logic should not be executed anymore for the current number
//        }
//        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
//        {
//            //.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()])));
//            //calculator.getTextArea().getText().append(buttonChoice);
//            //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToNegative(calculator.getTextArea().getText().toString())));
//            calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
//            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
//        }
//        calculator.getButtonDecimal().setEnabled(false); // deactivate button now that its active for this number
//        //calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button
//    }

    /**
     * Switches the converter type and updates the ConverterPanel
     */
    private static void performConversions()
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
        //repaint();
    }

    /**
     * Calls the main setup method when switching
     * from another panel to the ConverterPanel
     * @param calculator the Calculator object
     */
    public void performConverterCalculatorTypeSwitchOperations(Calculator calculator, ConverterType converterType)
    { setupConverterPanel(calculator, converterType); }

    /**
     * The main method which sets the help text
     * to be used in the help menu for the ConverterPanel
     */
    private void setupHelpMenu(ConverterType converterType)
    {
        String helpString = null;
        switch (converterType)
        {
            case ANGLE : {
                //LOGGER.debug("Size of MenuBar: " + getCalculator().bar.getMenuCount());
                // which is the number of menu choices
                helpString = "<html>How to use the " + ANGLE.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                break;
            }
            case AREA : {
                helpString = "<html>How to use the " + AREA.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                break;
            }
            default : {
                LOGGER.warn("IMPLEMENT Converter Help text");
            }
        }
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
                String finalHelpString = helpString;
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(finalHelpString,
                            calculator.getBlankIcon(), SwingConstants.CENTER);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                    JPanel mainPanel = new JPanel();
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(this,
                            mainPanel, "Viewing " + getConverterType().getName() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
    }

    /**
     * The main method used to set up the Edit menu item
     */
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

    /**
     * Specific copy functionality for ConverterPanel
     * @param actionEvent the click action
     */
    private void createCopyFunctionalityForConverter(ActionEvent actionEvent)
    {
        if (isTextField1Selected) {
            calculator.getValues()[2] = textField1.getText();
        } else {
            calculator.getValues()[2] = textField2.getText();
        }
        calculator.confirm("Copied " + calculator.getValues()[2]);
    }

    /**
     * Specific paste functionality for ConverterPanel
     * @param actionEvent the click action
     */
    private void createPasteFunctionalityForConverter(ActionEvent actionEvent)
    {
        if (isTextField1Selected) {
            textField1.setText(calculator.getValues()[2]);
        } else {
            textField2.setText(calculator.getValues()[2]);
        }
        calculator.confirm("Pasted " + calculator.getValues()[2]);
    }

    /**
     * The main method used to define the AngleConverter
     */
    private void setupAngleConverter()
    {
        LOGGER.info("Starting ANGLE specific setup");
        setupConverter(ANGLE.getName());
        setupHelpMenu(ANGLE);
        setConverterType(ANGLE);
        setUnitOptions1(new JComboBox<>(){{ addItem(DEGREES); addItem(RADIANS); addItem(GRADIANS); }});
        setUnitOptions2(new JComboBox<>(){{ addItem(DEGREES); addItem(RADIANS); addItem(GRADIANS); }});
        //setBottomSpaceAboveNumbers(new JTextArea(1,10));
        //bottomSpaceAboveNumbers.setEnabled(false);
        getUnitOptions1().addActionListener(this::performAngleUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAngleUnitsSwitch);
        LOGGER.info("Ending Angle specific setup");
    }

    /**
     * The main method used to define the AreaConverter
     */
    private void setupAreaConverter()
    {
        LOGGER.info("Starting AREA specific setup");
        setupConverter(AREA.getName());
        setupHelpMenu(AREA);
        setConverterType(AREA);
        setUnitOptions1(new JComboBox<>(){{ addItem(SQUARE_MILLIMETERS); addItem(SQUARE_CENTIMETERS); addItem(SQUARE_METERS); addItem(HECTARES); addItem(SQUARE_KILOMETERS); addItem(SQUARE_INCHES); addItem(SQUARE_FEET); addItem(SQUARE_YARD_ACRES); addItem(SQUARE_MILES); }});
        setUnitOptions2(new JComboBox<>(){{ addItem(SQUARE_MILLIMETERS); addItem(SQUARE_CENTIMETERS); addItem(SQUARE_METERS); addItem(HECTARES); addItem(SQUARE_KILOMETERS); addItem(SQUARE_INCHES); addItem(SQUARE_FEET); addItem(SQUARE_YARD_ACRES); addItem(SQUARE_MILES); }});
        //setBottomSpaceAboveNumbers(new JTextArea(1,10));
        //bottomSpaceAboveNumbers.setEnabled(false);
        getUnitOptions1().addActionListener(this::performAreaUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAreaUnitsSwitch);
        LOGGER.info("Ending Area specific setup");
    }

    /**
     * Builds the converter based on the ConverterType
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
     * @param actionEvent the click action
     */
    private void performAngleUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Angle units switch");
        LOGGER.debug("action: " + actionEvent.getActionCommand());
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

    /**
     * The actions to perform when converting areas
     * @param actionEvent the click action
     */
    private void performAreaUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Area units switch");
        AreaMethods.convertValues(calculator);
        LOGGER.info("Finished performing Area units switch");
        calculator.confirm("IMPLEMENT: Units switched. Conversions executed");
    }

    /************* All Getters ******************/
    public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JLabel getConverterTypeName() { return converterTypeName; }
    public ConverterType getConverterType() { return converterType; }
    public JTextField getTextField1() { return textField1; }
    public JTextField getTextField2() { return textField2; }
    public JComboBox<ConverterUnits> getUnitOptions1() { return unitOptions1; }
    public JComboBox<ConverterUnits> getUnitOptions2() { return unitOptions2; }
    //public JTextArea getBottomSpaceAboveNumbers() { return bottomSpaceAboveNumbers; }
    public Calculator getCalculator() { return calculator; }
    //public JPanel getNumbersPanel() { return numbersPanel; }
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
    //public void setBottomSpaceAboveNumbers(JTextArea bottomSpaceAboveNumbers) { this.bottomSpaceAboveNumbers = bottomSpaceAboveNumbers; }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    //public void setNumbersPanel(JPanel numbersPanel) { this.numbersPanel = numbersPanel; }
    public void setIsTextField1Selected(boolean isTextField1Selected) { this.isTextField1Selected = isTextField1Selected; }
}