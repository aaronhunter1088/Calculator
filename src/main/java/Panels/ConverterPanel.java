package Panels;

import Calculators.Calculator;
import Types.ConverterType;
import Types.ConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Converters.AngleMethods;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import static Calculators.Calculator.*;
import static Types.ConverterType.*;
import static Types.CalculatorType.*;
import static Converters.AngleMethods.*;
import static Converters.AreaMethods.*;

public class ConverterPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class);
    private static final long serialVersionUID = 1L;

    private GridBagLayout converterLayout;
    private GridBagConstraints constraints;
    public JLabel converterTypeName;
    public String converterName;
    public JTextField textField1, textField2;
    public JComboBox<String> unitOptions1, unitOptions2;
    public JTextArea bottomSpaceAboveNumbers;
    public Calculator calculator;
    public JPanel numbersPanel;
    public boolean isTextField1Selected;

    /************* Constructors ******************/
    public ConverterPanel() { LOGGER.info("Converter panel created"); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator
     * @param converterType
     */
    public ConverterPanel(Calculator calculator, ConverterType converterType) { setupConverterPanel(calculator, converterType); }

    /************* Start of methods here ******************/
    public void setupConverterPanel(Calculator calculator, ConverterType converterType)
    {
        setCalculator(calculator);
        setConverterLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setMaximumSize(new Dimension(300,400));
        setupEditMenu();
        setupHelpMenu(converterType);
        setupConverterPanelComponents(converterType);
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up converter panel");
    }

    public void performConverterCalculatorTypeSwitchOperations(Calculator calculator, ConverterType converterType)
    {
        setupConverterPanel(calculator, converterType);
    }

    private void setupConverterPanelComponents(ConverterType converterType)
    {
        setupAllConverterButtonsFunctionalities();
        calculator.setCalculatorType(CONVERTER);
        calculator.setConverterType(converterType);
        calculator.setupButtonBlank1();
        calculator.setupButtonBlank2();
        calculator.setupNumberButtons(true);
        calculator.setupClearEntryButton();
        calculator.setupDeleteButton();
        calculator.setupDotButton();
        switch (calculator.converterType)
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
                LOGGER.error("Unknown converterType: " + calculator.converterType);
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

    public void setupAllConverterButtonsFunctionalities()
    {
        LOGGER.info("Starting to setup all converter button functions");
        // Clear the buttons I will use of their functionality (other than numbers)
        calculator.clearAllOtherBasicCalculatorButtons();
        // Clear Entry button functionality
        calculator.buttonClearEntry.addActionListener(this::performClearEntryButtonFunctionality);
        // Delete button functionality
        calculator.buttonDelete.addActionListener(this::performDeleteButtonFunctionality);
        // Set up decimal button
        calculator.buttonDot.addActionListener(this::performDotButtonFunctionality);
        // Number button functionalities
        // First clear all functionality assigned to them
        calculator.clearNumberButtonFunctionalities();
        // Next, set up each number button. 0 is a bit different from the rest.
        calculator.button0.addActionListener(this::performNumberButtonFunctionality);
        calculator.button1.addActionListener(this::performNumberButtonFunctionality);
        calculator.button2.addActionListener(this::performNumberButtonFunctionality);
        calculator.button3.addActionListener(this::performNumberButtonFunctionality);
        calculator.button4.addActionListener(this::performNumberButtonFunctionality);
        calculator.button5.addActionListener(this::performNumberButtonFunctionality);
        calculator.button6.addActionListener(this::performNumberButtonFunctionality);
        calculator.button7.addActionListener(this::performNumberButtonFunctionality);
        calculator.button8.addActionListener(this::performNumberButtonFunctionality);
        calculator.button9.addActionListener(this::performNumberButtonFunctionality);
        LOGGER.info("Finished setting up all converter button functions");
    }

    private void performClearEntryButtonFunctionality(ActionEvent ae)
    {
        LOGGER.info("ClearEntryButtonHandler() started for Converter");
        LOGGER.info("button: " + ae.getActionCommand());
        textField1.setText("0");
        textField2.setText("0");
        textField1.requestFocusInWindow();
        isTextField1Selected = true;
        LOGGER.info("ClearEntryButtonHandler() finished for Converter");
        calculator.confirm("ClearEntryButton pushed", CONVERTER);
    }

    private void performDeleteButtonFunctionality(ActionEvent ae)
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
        calculator.confirm("DeleteButton pushed", CONVERTER);
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
        calculator.confirm("DotButton pushed", CONVERTER);
    }

    private void performNumberButtonFunctionality(ActionEvent ae)
    {
        // check if we are in textField1 or textField2
        // check to see what converter we are using
        // check to see what unit we are using
        // grab the number, add next number, add symbol
        String buttonValue = ae.getActionCommand();
        LOGGER.info("Pressed " + buttonValue);
        if (isTextField1Selected) {
            switch ((String)Objects.requireNonNull(unitOptions1.getSelectedItem())) {
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
                case SQUARE_MILES : {
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
        else {
            switch ((String)Objects.requireNonNull(unitOptions2.getSelectedItem())) {
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
                case SQUARE_MILES : {
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

    private void convertAndUpdatePanel()
    {
        LOGGER.info("Performing automatic conversion after each number button");
        AngleMethods.convertValues(calculator);
        calculator.confirm("Conversion done", CONVERTER);
        repaint();
    }

    public ConverterUnits convertStringUnitToConverterUnits(String unit)
    {
        ConverterUnits thisUnit = null;
        switch (unit) {
            case AngleMethods.DEGREES : {
                thisUnit = ConverterUnits.DEGREES;
                break;
            }
            case AngleMethods.RADIANS : {
                thisUnit = ConverterUnits.RADIANS;
                break;
            }
            case AngleMethods.GRADIANS : {
                thisUnit = ConverterUnits.GRADIANS;
                break;
            }
        }
        return thisUnit;
    }

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

    public void createViewHelpMenu(String helpString)
    {
        // 4 menu options: loop through to find the Help option
        for(int i=0; i < calculator.bar.getMenuCount(); i++) {
            JMenu menuOption = calculator.bar.getMenu(i);
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
                viewHelpItem.setFont(font);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(helpString,
                            calculator.blankImage, SwingConstants.CENTER);
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
        for(int i = 0; i < calculator.bar.getMenuCount(); i++) {
            JMenu menuOption = calculator.bar.getMenu(i);
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
                        copyItem.setFont(font);
                        copyItem.setName("Copy");
                        copyItem.addActionListener(this::createCopyFunctionalityForConverter);
                        menuOption.add(copyItem, 0);
                        continue;
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
                        pasteItem.setFont(font);
                        pasteItem.setName("Paste");
                        pasteItem.addActionListener(this::createPasteFunctionalityForConverter);
                        menuOption.add(pasteItem, 1);
                        continue;
                    }
                }
            }
        }
    }

    private void createCopyFunctionalityForConverter(ActionEvent ae)
    {
        if (isTextField1Selected) {
            calculator.values[2] = textField1.getText();
        } else {
            calculator.values[2] = textField2.getText();
        }
        calculator.confirm("Copied " + calculator.values[2], CONVERTER);
    }

    private void createPasteFunctionalityForConverter(ActionEvent ae)
    {
        if (isTextField1Selected) {
            textField1.setText(calculator.values[2]);
        } else {
            textField2.setText(calculator.values[2]);
        }
        calculator.confirm("Pasted " + calculator.values[2], CONVERTER);
    }

    private void setupAngleConverter()
    {
        LOGGER.info("Starting Angle specific setup");
        setupConverter(ANGLE.getName());
        setupHelpMenu(ANGLE);
        setConverterTypeNameAsString(ANGLE.getName());
        setUnitOptions1(new JComboBox<>(new String[]{AngleMethods.DEGREES, AngleMethods.RADIANS, AngleMethods.GRADIANS}));
        setUnitOptions2(new JComboBox<>(new String[]{AngleMethods.DEGREES, AngleMethods.RADIANS, AngleMethods.GRADIANS}));
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
        bottomSpaceAboveNumbers.setEnabled(false);
        unitOptions1.addActionListener(this::performAngleUnitsSwitch);
        unitOptions2.addActionListener(this::performAngleUnitsSwitch);
        LOGGER.info("Ending Angle specific setup");
    }

    private void setupAreaConverter()
    {
        LOGGER.info("Starting setup");
        setupConverter(AREA.getName());
        setupHelpMenu(AREA);
        setConverterTypeNameAsString(AREA.getName());
        setUnitOptions1(new JComboBox<>(new String[]{SQUARE_MILLIMETERS, SQUARE_CENTIMETERS, SQUARE_METERS, HECTARES, SQUARE_KILOMETERS, SQUARE_INCHES, SQUARE_FEET, SQUARE_YARD_ACRES, SQUARE_MILES}));
        setUnitOptions2(new JComboBox<>(new String[]{SQUARE_MILLIMETERS, SQUARE_CENTIMETERS, SQUARE_METERS, HECTARES, SQUARE_KILOMETERS, SQUARE_INCHES, SQUARE_FEET, SQUARE_YARD_ACRES, SQUARE_MILES}));
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
        bottomSpaceAboveNumbers.setEnabled(false);
        unitOptions1.addActionListener(this::performAreaUnitsSwitch);
        unitOptions2.addActionListener(this::performAreaUnitsSwitch);
        LOGGER.info("Ending Area specific setup");
    }

    private void setupConverter(String nameOfConverter)
    {
        LOGGER.info("Starting " + nameOfConverter + " Converter setup");
        setConverterTypeName(new JLabel(nameOfConverter));
        converterTypeName.setFont(font2);

        setupEditMenu();
        setupHelpMenu(calculator.converterType);

        setTextField1(new JTextField());
        textField1.setText("0");
        textField1.setFont(font2);
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
        textField2.setFont(font2);
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
        addComponentToNumbersPanel(calculator.buttonBlank1, 0, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.buttonClearEntry, 0, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.buttonDelete, 0, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button7, 1, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button8, 1, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button9, 1, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button4, 2, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button5, 2, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button6, 2, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button1, 3, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button2, 3, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.button3, 3, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.buttonBlank2, 4, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(calculator.buttonDot, 4, 1, 1, 1, 1.0, 1.0);
        calculator.button0.setPreferredSize(new Dimension(35, 35));
        addComponentToNumbersPanel(calculator.button0, 4, 2, 1, 1, 1.0, 1.0);
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
        convertValues(calculator);
        isTextField1Selected = !isTextField1Selected;
        calculator.confirm("Finished performing Angle units switch", CONVERTER);
    }

    private void performAreaUnitsSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Start performing Area units switch");
        // do any converting
        LOGGER.info("Finished performing Area units switch");
        calculator.confirm("IMPLEMENT: Units switched. Conversions executed", CONVERTER);
    }

    /************* All Setters ******************/
    public void setConverterLayout(GridBagLayout converterLayout) {
        super.setLayout(converterLayout);
        this.converterLayout = converterLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setConverterTypeName(JLabel converterTypeName) { this.converterTypeName = converterTypeName; }
    public void setConverterTypeNameAsString(String converterName) { this.converterName = converterName; }
    public void setTextField1(JTextField textField1) { this.textField1 = textField1; }
    public void setTextField2(JTextField textField2) {
        this.textField2 = textField2;;
    }
    public void setUnitOptions1(JComboBox<String> unitOptions1) { this.unitOptions1 = unitOptions1; }
    public void setUnitOptions2(JComboBox<String> unitOptions2) { this.unitOptions2 = unitOptions2; }
    public void setBottomSpaceAboveNumbers(JTextArea bottomSpaceAboveNumbers) { this.bottomSpaceAboveNumbers = bottomSpaceAboveNumbers; }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setNumbersPanel(JPanel numbersPanel) { this.numbersPanel = numbersPanel; }
    public void setIsTextField1Selected(boolean isTextField1Selected) { this.isTextField1Selected = isTextField1Selected; }
}
