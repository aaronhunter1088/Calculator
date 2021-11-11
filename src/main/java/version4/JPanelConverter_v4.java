package version4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;

import static version4.Calculator_v4.*;
import static version4.ConverterType_v4.*;
import static version4.CalcType_v4.*;
import static version4.AngleMethods.*;
import static version4.AreaMethods.*;

public class JPanelConverter_v4 extends JPanel
{
    protected final static Logger LOGGER;
    static
    {
        System.setProperty("appName", "JPanelDate_v4");
        LOGGER = LogManager.getLogger(JPanelBasic_v4.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout converterLayout; // layout of the panel
    private GridBagConstraints constraints; // layout's constraints
    private JLabel converterTypeName;
    private String converterName;
    private JTextField textField1;
    private JTextField textField2;
    private JComboBox<String> unitOptions1;
    private JComboBox<String> unitOptions2;
    private JTextArea bottomSpaceAboveNumbers;
    private StandardCalculator_v4 calculator;
    private ConverterType_v4 converterType;
    private JPanel numbersPanel;
    private JButton buttonBlank;
    private JButton buttonStartConversion;
    private boolean isTextField1Selected;
    //private final String degreesSymbol = "\u00B0";
    //private final String piSymbol = "\u03C0";
    //private final String gradianSymbol = "grad";
    // Angle measurements
    private final String PI = "\u03C0";


    /************* Constructor ******************/
    public JPanelConverter_v4(StandardCalculator_v4 calculator, ConverterType_v4 converterType) throws ParseException, CalculatorError_v4
    {
        LOGGER.info("Inside JPanelConverter_v4 constructor");
        setCalculator(calculator);
        setConverterType(converterType);
        setMinimumSize(new Dimension(300,400));
        setConverterLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setLayout(getConverterLayout());
        setupJPanelConverter_v4();
        setupAllConverterButtonsFunctionalities();
        addStartupComponentsToJPanelConverter_v4();
        LOGGER.info("Finished with JPanelConverter_v4 constructor\n");
    }

    /************* Start of methods here ******************/
    public void performConverterCalculatorTypeSwitchOperations()
    {
        getTextField1().requestFocusInWindow();
        // if coming from programmer calculator, make sure these things are set
        getCalculator().getNumberButtons().forEach(button -> button.setEnabled(true));
        setupAllConverterButtonsFunctionalities();
        // end coming from programmer calculator, make sure to do these things
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void setupJPanelConverter_v4() throws CalculatorError_v4
    {
        LOGGER.info("Starting setupJPanelConverter_v4");
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
                LOGGER.error("Unknown converterType: " + converterType);
                throw new CalculatorError_v4("Unknown converterType: " + converterType);
            }
        }
        LOGGER.info("Finished setupJPanelConverter_v4");
    }

    private void addStartupComponentsToJPanelConverter_v4()
    {
        LOGGER.info("Starting addStartupComponentsToJPanelConverter_v4");
        // all the following components are added no matter the option selected
        getConstraints().fill = GridBagConstraints.BOTH;
        getConstraints().anchor = GridBagConstraints.CENTER;
        addComponent(getConverterTypeName(), 0,0,1,1, 1.0,1.0);
        addComponent(getTextField1(), 1, 0, 0, 1,1.0,1.0);
        addComponent(getUnitOptions1(), 3, 0, 0,1, 1.0,1.0);
        addComponent(getTextField2(), 4, 0, 0, 1,1.0,1.0);
        addComponent(getUnitOptions2(), 6, 0, 0,1, 1.0,1.0);
        addComponent(getBottomSpaceAboveNumbers(), 7, 0, 1,1, 1.0,1.0);
        // numbers are added on top of a single panel
        getConstraints().anchor = GridBagConstraints.PAGE_START;
        addComponent(getNumbersPanel(), 8, 0, 0, 1, 1.0, 1.0);
        LOGGER.info("Finished addStartupComponentsToJPanelConverter_v4");
    }

    private void setupAllConverterButtonsFunctionalities()
    {
        // Clear the buttons I will use of their functionality (other than numbers)
        getCalculator().clearVariableNumberOfButtonsFunctionalities(Arrays.asList(
                getCalculator().getButtonClearEntry(), getCalculator().getButtonDelete(), getCalculator().getButtonDot()));
        // Clear Entry button functionality
        getCalculator().getButtonClearEntry().addActionListener(action -> {
            LOGGER.info("ClearEntryButtonHandler() started for Converter");
            LOGGER.info("button: " + action.getActionCommand());
            getTextField1().setText("0");
            getTextField2().setText("0");
            getTextField1().requestFocusInWindow();
            isTextField1Selected = true;
            LOGGER.info("ClearEntryButtonHandler() finished for Converter");
            getCalculator().confirm("ClearEntryButton pushed", CONVERTER);
        });
        // Delete button functionality
        getCalculator().getButtonDelete().addActionListener(action -> {
            LOGGER.info("DeleteButtonHandler() started for Converter");
            LOGGER.info("button: " + action.getActionCommand());
            if (isTextField1Selected) {
                if (getTextField1().getText().length() == 1) getTextField1().setText("0");
                else getTextField1().setText(getTextField1().getText().substring(0, getTextField1().getText().length()-1));
            }
            else {
                if (getTextField2().getText().length() == 1) getTextField2().setText("0");
                else getTextField2().setText(getTextField2().getText().substring(0, getTextField2().getText().length()-1));
            }
            LOGGER.info("DeleteButtonHandler() finished for Converter");
            getCalculator().confirm("DeleteButton pushed", CONVERTER);
        });
        // Set up decimal button
        getCalculator().getButtonDot().addActionListener(action -> {
            LOGGER.info("DotButtonHandler() started for Converter");
            LOGGER.info("button: " + action.getActionCommand());
            if (isTextField1Selected) {
                getTextField1().setText(getTextField1().getText() + ".");
            }
            else {
                getTextField2().setText(getTextField2().getText() + ".");
            }
            LOGGER.info("DotButtonHandler() finished for Converter");
            getCalculator().confirm("DotButton pushed", CONVERTER);
        });
        // Number button functionalities
        // First clear all functionality assigned to them
        getCalculator().clearNumberButtonFunctionalities();
        // Next, set up each number button. 0 is a bit different from the rest.
        getCalculator().getButton0().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton1().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton2().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton3().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton4().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton5().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton6().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton7().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton8().addActionListener(this::performNumberButtonFunctionality);
        getCalculator().getButton9().addActionListener(this::performNumberButtonFunctionality);
        // End button functionalities
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
            switch ((String)Objects.requireNonNull(getUnitOptions1().getSelectedItem())) {
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
                    if (!getTextField1().getText().equals("0")) {
                        getTextField1().setText(getTextField1().getText() + buttonValue);
                    } else {
                        if (getTextField1().getText().equals("0")) {
                            getTextField1().setText(buttonValue);
                        }
                        else {
                            getTextField1().setText(getTextField1().getText() + buttonValue);
                        }
                    }
                    break;
                }
                default : { LOGGER.error("Unknown unit"); break; }
            }
        }
        else {
            switch ((String)Objects.requireNonNull(getUnitOptions2().getSelectedItem())) {
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
                    if (!getTextField2().getText().equals("0")) {
                        getTextField2().setText(getTextField2().getText() + buttonValue);
                    } else {
                        if (getTextField2().getText().equals("0")) {
                            getTextField2().setText(buttonValue);
                        }
                        else {
                            getTextField2().setText(getTextField2().getText() + buttonValue);
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
        AngleMethods.convertValues(getCalculator());
        getCalculator().confirm("Conversion done", CONVERTER);
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
        getConverterLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
    }

    private void addComponentToNumbersPanel(Component c, int row, int column, int width, int height, double weighty, double weightx)
    {
        getConstraints().gridx = column;
        getConstraints().gridy = row;
        getConstraints().gridwidth = width;
        getConstraints().gridheight = height;
        getConstraints().fill = GridBagConstraints.BOTH;
        getConstraints().anchor = GridBagConstraints.CENTER;
        getConstraints().weighty = weighty;
        getConstraints().weightx = weightx;
        getConstraints().insets = new Insets(0, 0, 0, 0);
        GridBagLayout layout = (GridBagLayout) getNumbersPanel().getLayout();
        layout.setConstraints(c, getConstraints()); // set constraints
        getNumbersPanel().add(c);
    }

    private void createHelpMenu(String helpString)
    {
        for(int i=0; i < getCalculator().getBar().getMenuCount(); i++) {
            JMenu menuOption = getCalculator().getBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Help")) {
                // get the options. remove viewHelpItem
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("viewHelpItem")) {
                        LOGGER.debug("Found the current View Help option");
                        break;
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(font);
                viewHelpItem.setName("viewHelpItem");
                menuOption.add(viewHelpItem, 0);
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(helpString,
                            getCalculator().getBlankImage(), SwingConstants.CENTER);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                    JPanel mainPanel = new JPanel();
                    mainPanel.setBackground(Color.white);
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(this,
                            mainPanel, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                //break; //?? for just changing one option could be ok. issue maybe if changing other options
            }
        }
    }
    // To set up the help menu in the menu bar for each converter
    private void setupHelpMenu(ConverterType_v4 converterType)
    {
        switch (converterType)
        {
            case ANGLE : {
                //LOGGER.debug("Size of MenuBar: " + getCalculator().getBar().getMenuCount());
                // which is the number of menu choices
                String helpString = "<html>How to use the " + ANGLE.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                createHelpMenu(helpString);
                break;
            }
            case AREA : {
                String helpString = "<html>How to use the " + AREA.getName() + " Converter<br><br>" +
                        "Step 1. Select the unit for each conversion first.<br>" +
                        "Step 2. Enter a number into either text field.<br>" +
                        "Step 3. Observe the changed unit upon each entry.</html>";
                createHelpMenu(helpString);
                break;
            }
            default : {

            }
        }
    }

    private void setupEditMenu()
    {
        for(int i = 0; i < calculator.getBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Edit")) {
                System.out.println("Found the edit option");
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("Copy")) {
                        System.out.println("Found copy");
                        break;
                    }
                }
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
            }
        }
    }

    private void createCopyFunctionalityForConverter(ActionEvent ae)
    {
        getLogger().debug("Functionality CREATED");
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
        getBottomSpaceAboveNumbers().setEnabled(false);
        getUnitOptions1().addActionListener(this::performAngleUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAngleUnitsSwitch);
        LOGGER.info("Ending Angle specific setup");
    }

    private void setupAreaConverter()
    {
        LOGGER.info("Starting Area specific setup");
        setupConverter(AREA.getName());
        setupHelpMenu(AREA);
        setConverterTypeNameAsString(AREA.getName());
        setUnitOptions1(new JComboBox<>(new String[]{SQUARE_MILLIMETERS, SQUARE_CENTIMETERS, SQUARE_METERS, HECTARES, SQUARE_KILOMETERS, SQUARE_INCHES, SQUARE_FEET, SQUARE_YARD_ACRES, SQUARE_MILES}));
        setUnitOptions2(new JComboBox<>(new String[]{SQUARE_MILLIMETERS, SQUARE_CENTIMETERS, SQUARE_METERS, HECTARES, SQUARE_KILOMETERS, SQUARE_INCHES, SQUARE_FEET, SQUARE_YARD_ACRES, SQUARE_MILES}));
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
        getBottomSpaceAboveNumbers().setEnabled(false);
        getUnitOptions1().addActionListener(this::performAreaUnitsSwitch);
        getUnitOptions2().addActionListener(this::performAreaUnitsSwitch);
        LOGGER.info("Ending Area specific setup");
    }

    private void setupConverter(String nameOfConverter)
    {
        LOGGER.info("Starting " + nameOfConverter + " Converter setup");
        setConverterTypeName(new JLabel(nameOfConverter));
        getConverterTypeName().setFont(font2);

        setupEditMenu();

        setTextField1(new JTextField());
        getTextField1().setText("0");
        getTextField1().setFont(font2);
        getTextField1().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isTextField1Selected = true;
            }
            @Override
            public void focusLost(FocusEvent e) {}
        });

        setTextField2(new JTextField());
        getTextField2().setText("0");
        getTextField2().setFont(font2);
        getTextField2().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isTextField1Selected = false;
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        getTextField1().grabFocus();

        setButtonBlank(new JButton());
        getButtonBlank().setPreferredSize(new Dimension(35, 35));
        getButtonBlank().setBorder(new LineBorder(Color.BLACK));

        setButtonStartConversion(new JButton(""));
        getButtonStartConversion().setPreferredSize(new Dimension(35, 35));
        getButtonStartConversion().setFont(getCalculator().font);
        getButtonStartConversion().setBorder(new LineBorder(Color.BLACK));

        setNumbersPanel(new JPanel());
        getNumbersPanel().setLayout(new GridBagLayout());
        getNumbersPanel().setBackground(Color.BLACK);
        addComponentToNumbersPanel(getButtonStartConversion(), 0, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonClearEntry(), 0, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonDelete(), 0, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton7(), 1, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton8(), 1, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton9(), 1, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton4(), 2, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton5(), 2, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton6(), 2, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton1(), 3, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton2(), 3, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton3(), 3, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getButtonBlank(), 4, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonDot(), 4, 1, 1, 1, 1.0, 1.0);
        getCalculator().getButton0().setPreferredSize(new Dimension(35, 35));
        addComponentToNumbersPanel(getCalculator().getButton0(), 4, 2, 1, 1, 1.0, 1.0);
        LOGGER.info("Ending " + nameOfConverter + " Converter setup");
    }

    private void performAngleUnitsSwitch(ActionEvent action) {
        LOGGER.info("Start performing Angle units switch");
        LOGGER.debug("action: " + action.getActionCommand());
        if (getUnitOptions1().hasFocus())
        {
            LOGGER.info("UnitOptions1 selected");
            isTextField1Selected = true;
        }
        else
        {
            LOGGER.info("UnitOptions2 selected");
            isTextField1Selected = false;
        }
        convertValues(getCalculator());
        isTextField1Selected = !isTextField1Selected;
        getCalculator().confirm("Finished performing Angle units switch", CONVERTER);
    }

    private void performAreaUnitsSwitch(ActionEvent actionEvent) {
        LOGGER.info("Start performing Area units switch");
        // do any converting
        LOGGER.info("Finished performing Area units switch");
        getCalculator().confirm("IMPLEMENT: Units switched. Conversions executed", CONVERTER);
    }

    /************* All Getters ******************/
    public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JLabel getConverterTypeName() { return converterTypeName; }
    public String getConverterNameAsString() { return converterName; }
    public JTextField getTextField1() { return textField1; }
    public JTextField getTextField2() { return textField2; }
    public JComboBox<String> getUnitOptions1() { return unitOptions1; }
    public JComboBox<String> getUnitOptions2() { return unitOptions2; }
    public JTextArea getBottomSpaceAboveNumbers() { return bottomSpaceAboveNumbers; }
    public static Logger getLogger()
    {
        return LOGGER;
    }
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }
    public StandardCalculator_v4 getCalculator() { return calculator; }
    public ConverterType_v4 getConverterType() { return converterType; }
    public JPanel getNumbersPanel() { return numbersPanel; }
    public JButton getButtonBlank() { return buttonBlank; }
    public JButton getButtonStartConversion() { return buttonStartConversion; }
    public boolean isTextField1Selected() { return isTextField1Selected; }

    /************* All Setters ******************/
    public void setConverterLayout(GridBagLayout converterLayout) { this.converterLayout = converterLayout; }
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
    public void setCalculator(StandardCalculator_v4 calculator) { this.calculator = calculator; }
    public void setConverterType(ConverterType_v4 converterType) { this.converterType = converterType; }
    public void setNumbersPanel(JPanel numbersPanel) { this.numbersPanel = numbersPanel; }
    public void setButtonBlank(JButton buttonBlank) { this.buttonBlank = buttonBlank; }
    public void setButtonStartConversion(JButton buttonStartConversion) { this.buttonStartConversion = buttonStartConversion; }
    public void setIsTextField1Selected(boolean isTextField1Selected) { this.isTextField1Selected = isTextField1Selected; }
}
