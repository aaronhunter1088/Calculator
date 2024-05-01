package Calculators;

import Panels.*;
import Types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static Types.CalculatorBase.*;
import static Types.CalculatorType.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;

// TODO: Rename Calculator
public class Calculator extends JFrame
{
    static { System.setProperty("appName", "Calculator"); }
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
    private static final long serialVersionUID = 4L;

    // Fonts
    public static final Font
            mainFont = new Font("Segoe UI", Font.PLAIN, 12), // all panels
            mainFontBold = new Font("Segoe UI", Font.BOLD, 12), // Date panel
            verdanaFontBold = new Font("Verdana", Font.BOLD, 20); // Converter, Date panels

    // Basic Buttons
    private final JButton
            button0 = new JButton("0"), button1 = new JButton("1"),
            button2 = new JButton("2"), button3 = new JButton("3"),
            button4 = new JButton("4"), button5 = new JButton("5"),
            button6 = new JButton("6"), button7 = new JButton("7"),
            button8 = new JButton("8"), button9 = new JButton("9"),
            buttonClear = new JButton("C"), buttonClearEntry = new JButton("CE"),
            buttonDelete = new JButton("←"), buttonDot = new JButton("."),
            buttonFraction = new JButton("1/x"), buttonPercent = new JButton("%"),
            buttonSqrt = new JButton("√"), buttonMemoryClear = new JButton("MC"),
            buttonMemoryRecall = new JButton("MR"), buttonMemoryStore = new JButton("MS"),
            buttonMemoryAddition = new JButton("M+"), buttonMemorySubtraction = new JButton("M-"),
            buttonAdd = new JButton("+"), buttonSubtract = new JButton("-"),
            buttonMultiply = new JButton("*"), buttonDivide = new JButton("/"),
            buttonEquals = new JButton("="), buttonNegate = new JButton("±"),
    // Used in programmer and converter... until a button is determined
    buttonBlank1 = new JButton(""), buttonBlank2 = new JButton("");
    // Values used to store input
    protected String[]
            values = new String[]{"", "", "", ""}; // firstNum or total, secondNum, copy, temporary storage
    protected String[] memoryValues = new String[]{"", "", "", "", "", "", "", "", "", ""}; // stores memory values; rolls over after 10 entries
    private int
            valuesPosition = 0,
            memoryPosition = 0,
            memoryRecallPosition = 0;
    //TODO: Rename textPane
    private JTextPane textArea;

    private CalculatorType calculatorType;
    private CalculatorBase calculatorBase;
    private ConverterType converterType;
    private JPanel currentPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    private JLabel iconLabel, textLabel;
    // Menubar
    private JMenuBar menuBar;
    // Flags
    private boolean
            isFirstNumber = true,
            isNumberNegative = false,
    //memorySwitchBool = false,
    isAdding = false, isSubtracting = false,
            isMultiplying = false, isDividing = false,
    //isMemoryAdding = false, isMemorySubtracting = false,
    isNegating = false, isDotPressed = false;

    /**
     * Starts the calculator with the BASIC CalculatorType
     *
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(BASIC, null, null, null);
    }

    /**
     * Starts the Calculator with a specific CalculatorType
     *
     * @param calculatorType the type of Calculator to create
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorType, null, null, null);
    }

    /**
     * Starts the calculator with the PROGRAMMER CalculatorType with a specified CalculatorBase
     *
     * @param calculatorType the type of Calculator to create, expecting Programmer
     * @param calculatorBase the base to set that Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType, CalculatorBase calculatorBase) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorType, calculatorBase, null, null);
    }

    /**
     * This constructor is used to create a Date Calculator with a specific DateOperation
     *
     * @param calculatorType the type of Calculator to create, expecting Date
     * @param dateOperation  the option to open the DateCalculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType, DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorType, null, null, dateOperation);
    }

    /**
     * This constructor is used to create a Converter Calculator starting with a specific ConverterType
     *
     * @param calculatorType the type of Calculator to create, expecting Converter
     * @param converterType  the type of unit to start the Converter Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType, ConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorType, null, converterType, null);
    }

    /**
     * MAIN CONSTRUCTOR USED
     *
     * @param calculatorType the type of Calculator to create
     * @param converterType  the type of Converter to use
     * @param dateOperation  the type of DateOperation to start with
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType, CalculatorBase calculatorBase, ConverterType converterType, DateOperation dateOperation) throws CalculatorError, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calculatorType.getName());
        setCalculatorType(calculatorType);
        setupMenuBar();
        setupCalculatorImages();
        if (converterType == null && dateOperation == null) setCurrentPanel(determinePanel(calculatorType, calculatorBase));
        else if (converterType != null) setCurrentPanel(determinePanel(calculatorType, null, converterType, null));
        else setCurrentPanel(determinePanel(calculatorType, null, null, dateOperation));
        add(currentPanel);
        LOGGER.info("Panel added to calculator");
        setCalculatorBase(determineCalculatorBase(calculatorBase));
        setMaximumSize(currentPanel.getSize());
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator");
    }

    /* Creation methods */

    /**
     * Sets the menu bar used across the entire Calculator
     */
    public void setupMenuBar()
    {
        LOGGER.info("Creating menuBar...");
        // Menu Bar and Menu Choices and each menu options
        setCalculatorMenuBar(new JMenuBar());
        setJMenuBar(menuBar); // add menu bar to application

        // Start 4 Menu Choices
        JMenu lookMenu = new JMenu("Look");
        JMenu viewMenu = new JMenu("View");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // commonalities
        lookMenu.setFont(mainFont);
        lookMenu.setName("Look");
        viewMenu.setFont(mainFont);
        viewMenu.setName("View");
        editMenu.setFont(mainFont);
        editMenu.setName("Edit");
        helpMenu.setFont(mainFont);
        helpMenu.setName("Help");

        // Look menu options
        JMenuItem metal = new JMenuItem("Metal");
        JMenuItem system = new JMenuItem("System");
        JMenuItem windows = new JMenuItem("Windows");
        JMenuItem motif = new JMenuItem("Motif");
        JMenuItem gtk = new JMenuItem("GTK");

        metal.setFont(mainFont);
        system.setFont(mainFont);
        windows.setFont(mainFont);
        motif.setFont(mainFont);
        gtk.setFont(mainFont);

        // Setup Look and Feel
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
        windows.addActionListener(action -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
            } catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException | UnsupportedLookAndFeelException e) {
                LOGGER.error(e.getMessage());
            }
        });
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

        // add options to Look menu
        lookMenu.add(metal);
        lookMenu.add(motif);
        if (!isMacOperatingSystem()) {
            lookMenu.add(windows);
            lookMenu.add(system);
            lookMenu.add(gtk);
        } // add more options if using Windows
        menuBar.add(lookMenu);

        // View menu options, the converterMenu is an option which is a menu of more choices
        JMenuItem basic = new JMenuItem(CalculatorType.BASIC.getName());
        JMenuItem programmer = new JMenuItem(CalculatorType.PROGRAMMER.getName());
        JMenuItem date = new JMenuItem(CalculatorType.DATE.getName());
        JMenu converterMenu = new JMenu(CONVERTER.getName());

        // commonalities
        basic.setFont(mainFont);
        basic.setName(BASIC.getName());
        basic.addActionListener(this::performTasksWhenChangingJPanels);

        programmer.setFont(mainFont);
        programmer.setName(PROGRAMMER.getName());
        programmer.addActionListener(this::performTasksWhenChangingJPanels);

        date.setFont(mainFont);
        date.setName(DATE.getName());
        date.addActionListener(this::performTasksWhenChangingJPanels);

        converterMenu.setFont(mainFont);
        converterMenu.setName(CONVERTER.getName());
        // options for converterMenu
        JMenuItem angleConverter = new JMenuItem(ANGLE.getName());
        JMenuItem areaConverter = new JMenuItem(AREA.getName());

        // commonalities
        angleConverter.setFont(mainFont);
        angleConverter.setName(ANGLE.getName());
        areaConverter.setFont(mainFont);
        areaConverter.setName(AREA.getName());

        // functions
        angleConverter.addActionListener(this::performTasksWhenChangingJPanels);
        areaConverter.addActionListener(this::performTasksWhenChangingJPanels);

        // add JMenuItems to converterMenu
        converterMenu.add(angleConverter);
        converterMenu.add(areaConverter);

        // add view options to viewMenu
        viewMenu.add(basic);
        viewMenu.add(programmer);
        viewMenu.add(date);
        viewMenu.addSeparator();
        viewMenu.add(converterMenu);
        // END NEW OPTION: Converter
        menuBar.add(viewMenu); // add viewMenu to menu bar

        // Edit Menu options
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");

        // commonalities
        copyItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.setFont(mainFont);
        copyItem.setName("Copy");
        copyItem.addActionListener(this::performCopyFunctionality);

        pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setFont(mainFont);
        pasteItem.setName("Paste");
        pasteItem.addActionListener(this::performPasteFunctionality);

        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        menuBar.add(editMenu); // add editMenu to menu bar

        // Help Options
        JMenuItem viewHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();

        helpMenu.add(viewHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        menuBar.add(helpMenu); // add helpMenu to menu bar
        // End All Menu Choices
        LOGGER.info("menuBar created");
    }

    /**
     * Sets the images used in the Calculator
     */
    public void setupCalculatorImages()
    {
        LOGGER.info("Creating images...");
        setImageIcons();
        // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        //Application.getApplication().setDockIconImage(windowsCalculator.getImage());
        final Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(calculatorIcon.getImage());
        setIconImage(calculatorIcon.getImage());
        LOGGER.info("Creating images completed");
    }

    /**
     * Sets the image icons
     */
    public void setImageIcons()
    {
        LOGGER.info("Setting imageIcons...");
        setBlankIcon(null);
        try {
            ImageIcon calculatorIcon = createImageIcon("src/main/resources/images/windowsCalculator.jpg");
            if (null == calculatorIcon)
                throw new FileNotFoundException("The icon you are attempting to use cannot be found: calculatorIcon");
            setCalculatorIcon(calculatorIcon);
        } catch (FileNotFoundException e) {
            LOGGER.error(new CalculatorError("Could not find the path of the windows calculator", e));
        }

        try {
            ImageIcon macIcon = createImageIcon("src/main/resources/images/solidBlackAppleLogo.jpg");
            if (null == macIcon)
                throw new FileNotFoundException("The icon you are attempting to use cannot be found: macIcon");
            setMacIcon(macIcon);
        } catch (FileNotFoundException e) {
            LOGGER.error(new CalculatorError("Could not find the path of the solid black apple logo", e));
        }

        try {
            ImageIcon windowsIcon = createImageIcon("src/main/resources/images/windows11.jpg");
            if (null == windowsIcon)
                throw new FileNotFoundException("The icon you are attempting to use cannot be found: windowsIcon");
            setWindowsIcon(windowsIcon);
        } catch (FileNotFoundException e) {
            LOGGER.error(new CalculatorError("Could not find the path of the windows 11 logo", e));
        }
        LOGGER.info("ImageIcons set completed");
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path the path of the image
     */
    public ImageIcon createImageIcon(String path)
    {
        LOGGER.info("Creating imageIcon using path: " + path);
        ImageIcon imageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) {
            imageIcon = new ImageIcon(resource);
            LOGGER.info("ImageIcon created");
        } else {
            LOGGER.error("Could not find an image using path: " + path + '!');
            LOGGER.error("ImageIcon not created. Returning null");
        }
        return imageIcon;
    }

    /**
     * @param calculatorType the CalculatorType to use
     * @return JPanel, the Panel to use
     * @throws CalculatorError if we fail to return a Panel
     */
    public JPanel determinePanel(CalculatorType calculatorType) throws CalculatorError
    {
        return determinePanel(calculatorType, null, null, null);
    }

    public JPanel determinePanel(CalculatorType calculatorType, CalculatorBase calculatorBase)
    {
        return determinePanel(calculatorType, calculatorBase, null, null);
    }

    /**
     * @param calculatorType the CalculatorType to use
     * @param calculatorBase the CalculatorBase to use
     * @param converterType  the ConverterType to use
     * @param dateOperation  the DateOperation to use
     * @return JPanel, the Panel to use
     */
    public JPanel determinePanel(CalculatorType calculatorType, CalculatorBase calculatorBase, ConverterType converterType, DateOperation dateOperation)
    {
        if (calculatorType == BASIC || null == calculatorType) {
            return new BasicPanel(this);
        } else if (calculatorType == PROGRAMMER) {
            if (null != calculatorBase) return new ProgrammerPanel(this, calculatorBase);
            else                        return new ProgrammerPanel(this);
        } else if (calculatorType == SCIENTIFIC) {
            return new ScientificPanel();
        } else if (calculatorType == DATE) {
            return new DatePanel(this, dateOperation);
        } else //if (calculatorType == CONVERTER)
        {
            if (null == converterType) {
                return new ConverterPanel(this, ANGLE);
            } else {
                return new ConverterPanel(this, converterType);
            }
        }
    }

    /**
     * This method creates the View Help menu option under Help
     * The help text is added by the currentPanel.
     */
    public JMenuItem createViewHelpJMenuItem()
    {
        JMenuItem viewHelpItem = new JMenuItem("View Help");
        viewHelpItem.setName("View Help");
        viewHelpItem.setFont(mainFont);
        return viewHelpItem;
    }

    /**
     * This method creates the About Calculator menu option under Help
     */
    public JMenuItem createAboutCalculatorJMenuItem()
    {
        JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");
        aboutCalculatorItem.setName("About Calculator");
        aboutCalculatorItem.setFont(mainFont);
        aboutCalculatorItem.addActionListener(this::performAboutCalculatorFunctionality);
        return aboutCalculatorItem;
    }

    public void setupButtonBlank1()
    {
        buttonBlank1.setFont(mainFont);
        buttonBlank1.setPreferredSize(new Dimension(35, 35));
        buttonBlank1.setBorder(new LineBorder(Color.BLACK));
        buttonBlank1.setEnabled(true);
        buttonBlank1.setName("x");
        //buttonBlank1.addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Blank button 1 configured");
    }

    public void setupButtonBlank2()
    {
        buttonBlank2.setFont(mainFont);
        buttonBlank2.setPreferredSize(new Dimension(35, 35));
        buttonBlank2.setBorder(new LineBorder(Color.BLACK));
        buttonBlank2.setEnabled(true);
        buttonBlank2.setName("x");
        //buttonBlank2.addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Blank button 2 configured");
    }

    /* Calculator helper methods */

    /**
     * This method resets default values
     * Primarily used for testing purposes
     */
    public void resetValues()
    {
        getValues()[0] = "";
        getValues()[1] = "";
        getValues()[2] = "";
        getValues()[3] = "";
        setNumberNegative(false);
        resetBasicOperators(false);
    }

    public void clearNumberButtonFunctionalities()
    {
        LOGGER.debug("Number buttons cleared of action listeners");
        getNumberButtons().forEach(button -> Arrays.stream(button.getActionListeners()).collect(Collectors.toList()).forEach(al -> {
            LOGGER.debug("Removing action listener from button: " + button.getName());
            button.removeActionListener(al);
        }));
    }

    /**
     * This method returns true or false depending on if an operator was pushed or not
     *
     * @return the result
     */
    @Deprecated(since = "Use String return method")
    public boolean determineIfMainOperatorWasPushedBoolean()
    {
        return isAdding || isSubtracting || isMultiplying || isDividing;
    }

    //TODO: Determine how this was originally used. It does too much
    /**
     * Resets all operators to the given boolean argument
     *
     * @param operatorBool the operator to reset (add, sub, mul, or div)
     * @return boolean the operatorBool value
     */
    @Deprecated(since = "use resetBasicOperators")
    public boolean resetOperator(boolean operatorBool)
    {
        LOGGER.info("resetting operator...");
        if (operatorBool) {
            values[1] = "";
            valuesPosition = 1;
            isDotPressed = false;
            isFirstNumber = false;
            return false;
        } else {
            values[1] = "";
            valuesPosition = 0;
            isDotPressed = false;
            isFirstNumber = true;
            return true;
        }
    }

    public boolean isMemoryValuesEmpty()
    {
        boolean result = false;
        for (int i = 0; i < 10; i++) {
            if (StringUtils.isBlank(memoryValues[i])) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }

    public Collection<JButton> getBasicOperationButtons()
    {
        return Arrays.asList(buttonAdd, buttonSubtract, buttonMultiply, buttonDivide);
    }

    public Collection<JButton> getBasicNumberButtons()
    {
        return Arrays.asList(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9);
    }

    /**
     * Returns the "other" basic calculator buttons. This includes
     * Clear, ClearEntry, Delete, Dot, Equals, Fraction, Negate,
     * MemoryAddition, MemoryClear, MemoryRecall, MemorySubtraction,
     * MemoryStore, Percent, and SquareRoot buttons.
     *
     * @return Collection of buttons
     */
    public Collection<JButton> getAllOtherBasicCalculatorButtons()
    {
        return Arrays.asList(buttonEquals, buttonNegate, buttonClear, buttonClearEntry,
                buttonDelete, buttonDot, buttonFraction, buttonPercent, buttonSqrt,
                buttonMemoryAddition, buttonMemorySubtraction,
                buttonMemoryStore, buttonMemoryClear, buttonMemoryRecall);
    }

    public void clearAllOtherBasicCalculatorButtons()
    {
        getAllOtherBasicCalculatorButtons().forEach(button ->
                Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener));
    }

    /**
     * Tests whether a number is a decimal or not
     *
     * @param number the number to test
     * @return boolean if the given number contains a decimal
     */
    public boolean isDecimal(String number)
    {
        boolean answer = number.contains(".");
        LOGGER.info("isDecimal(" + number.replaceAll("\n", "") + ") == " + answer);
        return answer;
    }

    /**
     * Sets the textAreaValue to getTextAreaWithoutNewLineCharactersOrWhiteSpace
     */
//    public void updateTextAreaValueFromTextArea()
//    {
//        setTextAreaValue(new StringBuffer().append(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
//    }

    public void performInitialChecks()
    {
        LOGGER.info("Performing initial checks...");
        boolean checkFound = false;
        if (textArea1ContainsBadText()) {
            textArea.setText("");
            valuesPosition = 0;
            isFirstNumber = true;
            isDotPressed = false;
            isNumberNegative = false;
            checkFound = true;
        } else if (getTextAreaWithoutAnyOperator().equals("0") &&
                (calculatorType.equals(BASIC) ||
                        (calculatorType == PROGRAMMER && calculatorBase == DECIMAL))) {
            LOGGER.debug("textArea equals 0 no matter the form. setting to blank.");
            textArea.setText("");
            //textAreaValue = new StringBuffer();
            values[valuesPosition] = "";
            isFirstNumber = true;
            isDotPressed = false;
            checkFound = true;
        } else if (StringUtils.isBlank(values[0]) && StringUtils.isNotBlank(values[1])) {
            values[0] = values[1];
            values[1] = "";
            valuesPosition = 0;
            checkFound = true;
        }
        if (checkFound) LOGGER.info("Invalid entry in textArea...");
        else LOGGER.info("No invalid entry found");
    }

    /**
     * Tests whether the TextArea contains a String which shows a previous error
     *
     * @return boolean if textArea contains more than just a number
     */
    public boolean textArea1ContainsBadText()
    {
        return getTextAreaWithoutAnyOperator().equals("Invalid textarea") ||
                getTextAreaWithoutAnyOperator().equals("Cannot divide by 0") ||
                getTextAreaWithoutAnyOperator().equals("Not a Number") ||
                getTextAreaWithoutAnyOperator().equals("Enter a Number") ||
                getTextAreaWithoutAnyOperator().equals("Only positive numbers") ||
                getTextAreaWithoutAnyOperator().contains("E");
    }

    public void setValuesToTextAreaValue()
    {
        values[valuesPosition] = getTextArea().getText();
//        if (values[valuesPosition].startsWith(".")) {
//            values[valuesPosition] = values[valuesPosition].replace(".", "") + ".";
//        }
    }

    public void setTextareaToValuesAtPosition(String buttonChoice)
    {
        //textAreaValue.append(values[valuesPosition]);
        //LOGGER.debug("textAreaValue: '{}'", textAreaValue);
        //textAreaValue.append(textAreaValue).append(buttonChoice); // update textArea
        //LOGGER.debug("textarea: '{}'", textAreaValue);
        values[valuesPosition] += buttonChoice; // store textarea
        //textArea.setText(addNewLineCharacters() + convertToPositive(textAreaValue.toString()) + "-");
        textArea.setText(addNewLineCharacters() + values[valuesPosition]);
        LOGGER.debug("textArea: '" + textArea.getText() + "'");
        LOGGER.debug("values[" + valuesPosition + "]: '" + values[valuesPosition] + "'");
    }

    public Collection<JButton> getNumberButtons()
    {
        return new LinkedList<>() {{
            add(button0);
            add(button1);
            add(button2);
            add(button3);
            add(button4);
            add(button5);
            add(button6);
            add(button7);
            add(button8);
            add(button9);
        }};
    }

    /**
     * Takes an Exception and prints it to LOGGER.error
     *
     * @param e the Exception to log
     */
    public void logException(Exception e)
    { LOGGER.error(e.getClass().getName() + ": " + e.getMessage()); }

    /**
     * Determines if the OS is Mac or not
     *
     * @return boolean if is running on Mac
     */
    public boolean isMacOperatingSystem()
    {
        LOGGER.info("OS Name: " + System.getProperty("os.name"));
        return StringUtils.contains(System.getProperty("os.name").toLowerCase(), "mac");
    }

    public void performTasksWhenChangingJPanels(ActionEvent actionEvent)
    {
        LOGGER.info("Starting to changing jPanels");
        String oldPanelName = currentPanel.getClass().getSimpleName();
        String selectedPanel = actionEvent.getActionCommand();
        LOGGER.info("oldPanel: {}", oldPanelName);
        LOGGER.info("newPanel: {}", selectedPanel);
        if (oldPanelName.equals(selectedPanel))
        { confirm("Not changing to " + selectedPanel + " when already showing " + oldPanelName); }
        else if (converterType != null && converterType.getName().equals(selectedPanel))
        { confirm("Not changing panels when the conversion type is the same"); }
        switch (selectedPanel) {
            case "Basic":
                BasicPanel basicPanel = new BasicPanel(this);
                performTasksWhenChangingJPanels(basicPanel);
                updateTheTextAreaBasedOnTheTypeAndBase();
                break;
            case "Programmer":
                ProgrammerPanel programmerPanel = new ProgrammerPanel(this, DECIMAL);
                performTasksWhenChangingJPanels(programmerPanel);
                updateTheTextAreaBasedOnTheTypeAndBase();
                break;
            case "Scientific":
                LOGGER.warn("Setup");
                break;
            case "Date":
                DatePanel datePanel = new DatePanel(this, DIFFERENCE_BETWEEN_DATES);
                performTasksWhenChangingJPanels(datePanel);
                break;
            case "Angle": {
                ConverterPanel converterPanel = new ConverterPanel(this, ANGLE);
                performTasksWhenChangingJPanels(converterPanel, ANGLE);
                break;
            }
            case "Area": {
                ConverterPanel converterPanel = new ConverterPanel(this, AREA);
                performTasksWhenChangingJPanels(converterPanel, AREA);
                break;
            }
        }
        setTitle(selectedPanel);
        pack();
        LOGGER.info("Finished changing jPanels\n");
        confirm("Switched from " + oldPanelName + " to " + currentPanel.getClass().getSimpleName());
    }
    private void performTasksWhenChangingJPanels(JPanel newPanel)
    { performTasksWhenChangingJPanels(newPanel, null);}
    private void performTasksWhenChangingJPanels(JPanel newPanel, ConverterType converterType)
    {
        LOGGER.info("Performing JPanel switch...");
        JPanel oldPanel = updateJPanel(newPanel);
        // if oldPanel is same as newPanel, don't change
        if (oldPanel.getClass().getSimpleName().equals(currentPanel.getClass().getSimpleName()) ||
                (oldPanel.getClass().getSimpleName().equals("Panels.ConverterPanel") && newPanel.getClass().getSimpleName().equals("Panels.ConverterPanel"))) {
            //confirm("Not changing when " + oldPanel.getClass().getName() + " == " + currentPanel.getClass().getName());
        }
        else if (converterType == this.converterType) {
            //confirm("Not changing panels when the conversion type is the same");
        }
        else if (currentPanel instanceof BasicPanel) {
            ((BasicPanel) currentPanel).performBasicCalculatorTypeSwitchOperations(this);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
        }
        else if (currentPanel instanceof ProgrammerPanel) {
            ((ProgrammerPanel) currentPanel).performProgrammerCalculatorTypeSwitchOperations(this, null);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
        }
        else if (currentPanel instanceof DatePanel) {
            ((DatePanel) currentPanel).performDateCalculatorTypeSwitchOperations(this, DIFFERENCE_BETWEEN_DATES);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
        }
        else // if (currentPanel instanceof ConverterPanel)
        {
            ((ConverterPanel) newPanel).performConverterCalculatorTypeSwitchOperations(this, converterType);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
        }
    }

    public void updateTheTextAreaBasedOnTheTypeAndBase()
    {
        LOGGER.info("Updating the textArea");
        textArea.setText(addNewLineCharacters() + textArea.getText());
    }

    public String convertFromTypeToTypeOnValues(CalculatorBase fromType, CalculatorBase toType, String strings) throws CalculatorError
    {
        LOGGER.debug("convert from {} to {}", fromType, toType);
        LOGGER.debug("on value: {}", strings);
        StringBuffer sb = new StringBuffer();
        if (strings.contains(" ")) {
            String[] strs = strings.split(" ");
            for (String s : strs) {
                sb.append(s);
            }
        } else {
            sb.append(strings);
        }
        LOGGER.info("sb: " + sb);
        String convertedValue = "";
        if (StringUtils.isEmpty(strings)) return "";
        // All from HEXADECIMAL to any other option
        if (fromType == HEXADECIMAL && toType == DECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == HEXADECIMAL && toType == OCTAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == HEXADECIMAL && toType == BINARY) {
            confirm("IMPLEMENT");
        }
        // All from DECIMAL to any other option
        else if (fromType == DECIMAL && toType == HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == DECIMAL && toType == OCTAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == DECIMAL && toType == BINARY)
        {
            LOGGER.debug("Converting str(" + sb + ")");
            sb = new StringBuffer();
            int number;
            try {
                number = Integer.parseInt(strings);
                LOGGER.debug("number: " + number);
                int i = 0;
                if (number == 0) sb.append("00000000");
                else {
                    while (i < ((ProgrammerPanel)currentPanel).getBytes()) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (sb.length() == 8) sb.append(" ");
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for (int k = i; k < ((ProgrammerPanel)currentPanel).getBytes(); k++) {
                                sb.append("0");
                                if (k == 7) sb.append(" ");
                            }
                            break;
                        }
                        else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for (int k = i + 1; k < ((ProgrammerPanel)currentPanel).getBytes(); k++) {
                                sb.append("0");
                                if (k == 7) sb.append(" ");
                            }
                            break;
                        }
                        i++;
                        number /= 2;
                    }
                }
            } catch (NumberFormatException nfe) {
                LOGGER.error(nfe.getMessage());
            }
            // Determine bytes and add zeroes if needed
            int sizeOfSecond8Bits;
            try {
                // start counting at 9. The 8th bit is a space
                sizeOfSecond8Bits = sb.substring(10).length();
                int zeroesToAdd = 8 - sizeOfSecond8Bits;
                sb.append("0".repeat(Math.max(0, zeroesToAdd)));
            } catch (StringIndexOutOfBoundsException e) {
                LOGGER.warn("No second bits found");
            }
            LOGGER.info("Before reverse: {}", sb);
            // End adding zeroes here

            sb.reverse();
            LOGGER.info("After reverse: {}", sb);
            String strToReturn = sb.toString();
            LOGGER.debug("convertFrom(" + fromType + ")To(" + toType + ") = " + sb);
            LOGGER.warn("ADD CODE THAT MAKES SURE RETURNED VALUE UPDATES BYTES");
            LOGGER.warn("IF AFTER REVERSE IS LONGER THAN 8 BITS, WE NEED TO ADD");
            LOGGER.warn("A SPACE BETWEEN THE BYTES");
            convertedValue = strToReturn;
        }
        // All from OCTAL to any other option
        else if (fromType == OCTAL && toType == HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == OCTAL && toType == DECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == OCTAL && toType == BINARY) {
            confirm("IMPLEMENT");
        }
        // All from BINARY to any other option
        else if (fromType == BINARY && toType == HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BINARY && toType == DECIMAL)
        {
            LOGGER.debug("Converting str(" + sb + ")");

            int appropriateLength = ((ProgrammerPanel)currentPanel).getBytes();
            LOGGER.debug("sb: " + sb);
            LOGGER.debug("appropriateLength: " + appropriateLength);

            if (sb.length() != appropriateLength) {
                LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                // user had entered 101, which really is 00000101, but they aren't showing the first 5 zeroes
                int difference = appropriateLength - sb.length();
                StringBuilder missingZeroes = new StringBuilder();
                missingZeroes.append("0".repeat(Math.max(0, difference)));
                missingZeroes.append(sb);
                sb = new StringBuffer(missingZeroes);
                LOGGER.debug("sb: " + sb);
            }

            double result = 0.0;
            double num1;
            double num2;
            for (int i = 0, k = appropriateLength - 1; i < appropriateLength; i++, k--) {
                num1 = Double.parseDouble(String.valueOf(sb.charAt(i)));
                num2 = Math.pow(2, k);
                result = (num1 * num2) + result;
            }

            convertedValue = String.valueOf(Double.valueOf(result));


            if (isPositiveNumber(convertedValue)) {
                convertedValue = String.valueOf(clearZeroesAndDecimalAtEnd(convertedValue));
            }
            LOGGER.debug("convertedValue: {}", convertedValue);
        }
        else if (fromType == BINARY && toType == OCTAL) {
            confirm("IMPLEMENT");
        }

        LOGGER.info("base set to: " + calculatorBase + addNewLineCharacters(1));
        return convertedValue;
    }

    /**
     * Clears any decimal found.
     * Clears all zeroes after decimal (if any).
     *
     * @param currentNumber the value to clear
     * @return updated currentNumber
     */
    public String clearZeroesAndDecimalAtEnd(String currentNumber)
    {
        LOGGER.info("Starting clear zeroes at the end");
        LOGGER.debug("currentNumber: " + currentNumber);
        //textAreaValue = new StringBuffer().append(textAreaValue.toString().replaceAll("\n", ""));
        int index;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) return currentNumber;
        else {
            currentNumber = currentNumber.substring(0, index);
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + currentNumber);
        return currentNumber;
    }

    public String getTextAreaWithoutAnyOperator()
    {
        return textArea.getText()
                .replaceAll("\n", "")
                .replace("+", "")
                .replace("-", "")
                .replace("*", "")
                .replace("/", "")
                .replace("MOD", "")
                .replace("(", "")
                .replace(")", "")
                .replace("RoL", "")
                .replace("RoR", "")
                .replace("OR", "")
                .replace("XOR", "")
                .replace("AND", "")
                .strip();
    }

    public String getTextAreaWithoutNewLineCharactersOrWhiteSpace()
    {
        return textArea.getText()
                .replaceAll("\n", "")
                .strip();
    }

    public String getTextAreaWithoutNewLineCharacters()
    { return textArea.getText().replaceAll("\n", ""); }

//    public StringBuffer getTextAreaValueWithoutAnything()
//    {
//        return new StringBuffer().append(textAreaValue.toString().replaceAll("\n", ""));
//    }

    /**
     * This method is used after any result to verify
     * the result of the previous method and see the
     * values of the entire Calculator object
     */
    public void confirm()
    { confirm(""); }

    /**
     * This method is used after any result to verify
     * the result of the previous method and see the
     * values of the entire Calculator object
     *
     * @param message the message to pass into confirm
     */
    public void confirm(String message)
    {
        LOGGER.info("Confirm Results {}", StringUtils.isNotEmpty(message) ? ": " + message : "");
        LOGGER.info("---------------- ");
        switch (calculatorType) {
            case BASIC: {
                LOGGER.info("textArea: '{}'", getTextAreaWithoutNewLineCharacters());
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition])) {
                    LOGGER.info("no memories stored!");
                } else {
                    LOGGER.info("memoryPosition: '{}'", memoryPosition);
                    LOGGER.info("memoryRecallPosition: '{}'", memoryRecallPosition);
                    for (int i = 0; i < 10; i++) {
                        if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues[{}]: ", memoryValues[i]);
                        }
                    }
                }
                LOGGER.info("addBool: '{}'", isAdding);
                LOGGER.info("subBool: '{}'", isSubtracting);
                LOGGER.info("mulBool: '{}'", isMultiplying);
                LOGGER.info("divBool: '{}'", isDividing);
                LOGGER.info("values[{}]: '{}'", 0, values[0]);
                LOGGER.info("values[{}]: '{}'", 1, values[1]);
                LOGGER.info("values[{}]: '{}'", 2, values[2]);
                LOGGER.info("values[{}]: '{}'", 3, values[3]);
                LOGGER.info("valuesPosition: '{}'", valuesPosition);
                LOGGER.info("firstNumBool: '{}'", isFirstNumber);
                LOGGER.info("dotButtonPressed: '{}'", isDotPressed);
                LOGGER.info("isNegative: '{}'", isNumberNegative);
                LOGGER.info("calculatorType: '{}", calculatorType);
                LOGGER.info("calculatorBase: '{}'", calculatorBase);
                break;
            }
            case PROGRAMMER: {
                LOGGER.info("textArea: '{}'", getTextAreaWithoutNewLineCharacters());
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition])) {
                    LOGGER.info("no memories stored!");
                } else {
                    LOGGER.info("memoryPosition: '{}'", memoryPosition);
                    LOGGER.info("memoryRecallPosition: '{}'", memoryRecallPosition);
                    for (int i = 0; i < 10; i++) {
                        if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues[{}]: ", memoryValues[i]);
                        }
                    }
                }
                LOGGER.info("addBool: '{}'", isAdding);
                LOGGER.info("subBool: '{}'", isSubtracting);
                LOGGER.info("mulBool: '{}'", isMultiplying);
                LOGGER.info("divBool: '{}'", isDividing);
                LOGGER.info("orButtonBool: '{}'", ((ProgrammerPanel)currentPanel).isOrPressed());
                LOGGER.info("modButtonBool: '{}'", ((ProgrammerPanel)currentPanel).isModulusPressed());
                LOGGER.info("xorButtonBool: '{}'", ((ProgrammerPanel)currentPanel).isXorPressed());
                LOGGER.info("notButtonBool: '{}'", ((ProgrammerPanel)currentPanel).isNotPressed());
                LOGGER.info("andButtonBool: '{}'", ((ProgrammerPanel)currentPanel).isAndPressed());
                LOGGER.info("values[{}]: '{}'", 0, values[0]);
                LOGGER.info("values[{}]: '{}'", 1, values[1]);
                LOGGER.info("values[{}]: '{}'", 2, values[2]);
                LOGGER.info("values[{}]: '{}'", 3, values[3]);
                LOGGER.info("valuesPosition: '{}'", valuesPosition);
                LOGGER.info("firstNumBool: '{}'", isFirstNumber);
                LOGGER.info("dotButtonPressed: '{}'", isDotPressed);
                LOGGER.info("isNegative: '{}'", isNumberNegative);
                LOGGER.info("calculatorType: '{}", calculatorType);
                LOGGER.info("calculatorBase: '{}'", calculatorBase);
                LOGGER.info("calculatorByteWord: '{}'", ((ProgrammerPanel)currentPanel).getByteWord());
                break;
            }
            case SCIENTIFIC: {
                LOGGER.warn("Confirm message not setup for " + calculatorType);
                break;
            }
            case DATE: {
                if (((DatePanel) currentPanel).dateOperation == DIFFERENCE_BETWEEN_DATES)
                {
                    LOGGER.info("{} Selected", DIFFERENCE_BETWEEN_DATES);
                    int year = ((DatePanel) currentPanel).getTheYearFromTheFromDatePicker();
                    int month = ((DatePanel) currentPanel).getTheMonthFromTheFromDatePicker();
                    int day = ((DatePanel) currentPanel).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " + date);
                    year = ((DatePanel) currentPanel).getTheYearFromTheToDatePicker();
                    month = ((DatePanel) currentPanel).getTheMonthFromTheToDatePicker();
                    day = ((DatePanel) currentPanel).getTheDayOfTheMonthFromTheToDatePicker();
                    date = LocalDate.of(year, month, day);
                    LOGGER.info("ToDate(yyyy-mm-dd): " + date);
                    LOGGER.info("Difference");
                    LOGGER.info("Year: " + ((DatePanel) currentPanel).getYearsDifferenceLabel().getText());
                    LOGGER.info("Month: " + ((DatePanel) currentPanel).getMonthsDifferenceLabel().getText());
                    LOGGER.info("Weeks: " + ((DatePanel) currentPanel).getWeeksDifferenceLabel().getText());
                    LOGGER.info("Days: " + ((DatePanel) currentPanel).getDaysDifferenceLabel().getText());
                }
                else
                {
                    LOGGER.info("{} Selected", ADD_SUBTRACT_DAYS);
                    int year = ((DatePanel) currentPanel).getTheYearFromTheFromDatePicker();
                    int month = ((DatePanel) currentPanel).getTheMonthFromTheFromDatePicker();
                    int day = ((DatePanel) currentPanel).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " + date);
                    boolean isAddSelected = ((DatePanel) currentPanel).getAddRadioButton().isSelected();
                    if (isAddSelected) LOGGER.info("Add Selected");
                    else               LOGGER.info("Subtract Selected");
                    LOGGER.info("New Date: " + ((DatePanel) currentPanel).getResultsLabel().getText());
                }
                break;
            }
            case CONVERTER: {
                LOGGER.info("Converter: '{}'", ((ConverterPanel) currentPanel).getConverterType());
                LOGGER.info("text field 1: '{}'", ((ConverterPanel) currentPanel).getTextField1().getText() + " "
                        + ((ConverterPanel) currentPanel).getUnitOptions1().getSelectedItem());
                LOGGER.info("text field 2: '{}'", ((ConverterPanel) currentPanel).getTextField2().getText() + " "
                        + ((ConverterPanel) currentPanel).getUnitOptions2().getSelectedItem());
                break;
            }
        }
        LOGGER.info("-------- End Confirm Results --------\n");
    }

    @Deprecated(since = "Use zero argument method")
    public String addNewLineCharacters(int number)
    { return "\n".repeat(Math.max(0, number)); }

    /**
     * Adds the appropriate amount of newline characters
     * based on the currentPanel
     *
     * @return the newline character repeated 1 to n times
     */
    public String addNewLineCharacters()
    {
        String newLines = null;
        if (currentPanel instanceof BasicPanel) newLines = "\n".repeat(1);
        else if (currentPanel instanceof ProgrammerPanel) newLines = "\n".repeat(3);
        else if (currentPanel instanceof ScientificPanel) newLines = "\n".repeat(3);
        return newLines;
    }

    public String getHelpString()
    {
        LOGGER.debug("getHelpString");
        String os = System.getProperty("os.name");
        String computerText = "";
        if (isMacOperatingSystem()) {
            computerText = "Apple";
        } else {
            computerText = "Windows";
        }
        return "<html>" + computerText + "<br>"
                + "Calculator Version 4<br>"
                + "©" + LocalDate.now().getYear() + " All rights reserved.<br><br>"
                + os + " and its user interface are protected by trademark and all other<br>"
                + "pending or existing intellectual property right in the United States and other<br>"
                + "countries/regions."
                + "<br><br><br>"
                + "This product is licensed under the License Terms to:<br>"
                + "Michael Ball<br>"
                + "Github: https://github.com/aaronhunter1088/calculator</html>";
    }

    /**
     * This method updates the JPanel to the one provided
     * and returns the oldPanel
     *
     * @param newPanel the panel to update on the Calculator
     * @return the old panel
     */
    public JPanel updateJPanel(JPanel newPanel)
    {
        JPanel oldPanel = currentPanel;
        remove(oldPanel);
        setCurrentPanel(newPanel);
        add(currentPanel);
        return oldPanel;
    }

    /**
     * Tests whether a number is positive
     *
     * @param number the value to test
     * @return either true or false based on result
     * <p>
     * Fully tested
     */
    public boolean isPositiveNumber(String number)
    {
        boolean answer = !number.contains("-");
        LOGGER.info("isPositiveNumber(" + number + ") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is negative
     *
     * @param number the value to test
     * @return Fully tested
     */
    public boolean isNegativeNumber(String number)
    {
        boolean answer = number.contains("-");
        LOGGER.info("isNegativeNumber(" + number + ") == " + answer);
        return answer;
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.info("convertToNegative(" + number + ") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.debug("New: " + number);
        isNumberNegative = true;
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     *
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.info("convertToPositive(" + number + ") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        LOGGER.debug("New: " + number);

        isNumberNegative = false;
        return number;
    }

    /**
     * Resets the 4 main operators to the boolean passed in
     *
     * @param bool a boolean to reset the operators to
     */
    public void resetBasicOperators(boolean bool)
    {
        isAdding = bool;
        isSubtracting = bool;
        isMultiplying = bool;
        isDividing = bool;
    }

    /**
     * Returns the number representation to the left of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the left of the decimal
     */
    public String getNumberOnLeftSideOfNumber(String currentNumber)
    {
        String leftSide;
        int index = currentNumber.indexOf(".");
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = "";
        else {
            leftSide = currentNumber.substring(0, index);
            if (StringUtils.isEmpty(leftSide)) leftSide = "0";
        }
        LOGGER.info("number to the left of the decimal: '{}'", leftSide);
        return leftSide;
    }

    /**
     * Returns the number representation to the right of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the right of the decimal
     */
    protected String getNumberOnRightSideOfNumber(String currentNumber)
    {
        String rightSide;
        int index = currentNumber.indexOf(".");
        if (index == -1 || (index + 1) >= currentNumber.length()) rightSide = "";
        else {
            rightSide = currentNumber.substring(index + 1);
            if (StringUtils.isEmpty(rightSide)) rightSide = "0";
        }
        LOGGER.info("number to the right of the decimal: '{}'", rightSide);
        return rightSide;
    }

    public String formatNumber(String num)
    {
        DecimalFormat df = null;
        LOGGER.info("Number to format: " + num);
        if (!isNumberNegative) {
            if (num.length() <= 2) df = new DecimalFormat("0.00");
            if (num.length() >= 3) df = new DecimalFormat("0.000");
        } else {
            if (num.length() <= 3) df = new DecimalFormat("0.0");
            if (num.length() == 4) df = new DecimalFormat("0.00");
            if (num.length() >= 5) df = new DecimalFormat("0.000");
        }
        double number = Double.parseDouble(num);
        number = Double.parseDouble(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length() - 3) == '.' && numberAsStr.substring(numberAsStr.length() - 3).equals(".00"))
        {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length() - 3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }

    /**
     * This method will return a specific CalculatorBase determined by
     * the panel set on the calculator. If we pass in a specific base,
     * we will simply return that CalculatorBase
     *
     * @param calculatorBase the CalculatorBase to set
     * @return CalculatorBase, the base to use
     */
    public CalculatorBase determineCalculatorBase(CalculatorBase calculatorBase)
    {
        if (calculatorBase != null) {
            return calculatorBase;
        } else {
            if (currentPanel instanceof BasicPanel) return DECIMAL;
            else if (currentPanel instanceof ProgrammerPanel) {
                if ((((ProgrammerPanel) currentPanel).isBaseBinary())) return BINARY;
                else if (((((ProgrammerPanel) currentPanel).isBaseDecimal()))) return DECIMAL;
                else if (((((ProgrammerPanel) currentPanel).isBaseOctal()))) return OCTAL;
                else return HEXADECIMAL;
            } else if (currentPanel instanceof ScientificPanel) return BINARY;
            else if (currentPanel instanceof DatePanel) return null;
            else return null;
        }
    }

    // Action Listeners

    /**
     * Takes the value in the textArea and saves it in values[2]
     *
     * @param actionEvent the click action
     */
    public void performCopyFunctionality(ActionEvent actionEvent)
    {
        values[2] = getTextAreaWithoutNewLineCharactersOrWhiteSpace();
        //textAreaValue = new StringBuffer().append(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        confirm("Pressed Copy");
    }

    /**
     * Pastes the current value in values[2] in the textArea if
     * values[2] has a value
     *
     * @param actionEvent the click action
     */
    public void performPasteFunctionality(ActionEvent actionEvent)
    {
        if (StringUtils.isEmpty(values[2]) && StringUtils.isBlank(values[2])) {
            LOGGER.info("Values[2] is empty and blank");
        } else {
            LOGGER.info("Values[2]: " + values[2]);
            textArea.setText(addNewLineCharacters() + values[2]); // to paste
            values[valuesPosition] = getTextAreaWithoutNewLineCharactersOrWhiteSpace();
            //textAreaValue = new StringBuffer().append(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        }
        confirm("Pressed Paste");
    }

    /**
     * Display the text for About Calculator menu item
     *
     * @param actionEvent the click action
     */
    public void performAboutCalculatorFunctionality(ActionEvent actionEvent)
    {
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        ImageIcon specificLogo = isMacOperatingSystem() ? macIcon : windowsIcon;
        textLabel = new JLabel(getHelpString(),
                specificLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(this,
                mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);
        confirm("Pressed About Calculator");
    }

    /************* All Getters ******************/
    public JButton getButton0() { return button0; }
    public JButton getButton1() { return button1; }
    public JButton getButton2() { return button2; }
    public JButton getButton3() { return button3; }
    public JButton getButton4() { return button4; }
    public JButton getButton5() { return button5; }
    public JButton getButton6() { return button6; }
    public JButton getButton7() { return button7; }
    public JButton getButton8() { return button8; }
    public JButton getButton9() { return button9; }
    public JButton getButtonClear() { return buttonClear; }
    public JButton getButtonClearEntry() { return buttonClearEntry; }
    public JButton getButtonDelete() { return buttonDelete; }
    public JButton getButtonDot() { return buttonDot; }
    public JButton getButtonFraction() { return buttonFraction; }
    public JButton getButtonPercent() { return buttonPercent; }
    public JButton getButtonSqrt() { return buttonSqrt; }
    public JButton getButtonMemoryClear() { return buttonMemoryClear; }
    public JButton getButtonMemoryRecall() { return buttonMemoryRecall; }
    public JButton getButtonMemoryStore() { return buttonMemoryStore; }
    public JButton getButtonMemoryAddition() { return buttonMemoryAddition; }
    public JButton getButtonMemorySubtraction() { return buttonMemorySubtraction; }
    public JButton getButtonAdd() { return buttonAdd; }
    public JButton getButtonSubtract() { return buttonSubtract; }
    public JButton getButtonMultiply() { return buttonMultiply; }
    public JButton getButtonDivide() { return buttonDivide; }
    public JButton getButtonEquals() { return buttonEquals; }
    public JButton getButtonNegate() { return buttonNegate; }
    public JButton getButtonBlank1() { return buttonBlank1; }
    public JButton getButtonBlank2() { return buttonBlank2; }
    public String[] getValues() { return values; }
    public String[] getMemoryValues() { return memoryValues; }
    public int getValuesPosition() { return valuesPosition; }
    public int getMemoryPosition() { return memoryPosition; }
    public int getMemoryRecallPosition() { return memoryRecallPosition; }
    public JTextPane getTextArea() { return textArea; }
    public CalculatorType getCalculatorType() { return calculatorType; }
    public CalculatorBase getCalculatorBase() { return calculatorBase; }
    public ConverterType getConverterType() { return converterType; }
    public JPanel getCurrentPanel() { return currentPanel; }
    public ImageIcon getCalculatorIcon() { return calculatorIcon; }
    public ImageIcon getMacIcon() { return macIcon; }
    public ImageIcon getWindowsIcon() { return windowsIcon; }
    public ImageIcon getBlankIcon() { return blankIcon; }
    public JLabel getIconLabel() { return iconLabel; }
    public JLabel getTextLabel() { return textLabel; }
    public JMenuBar getCalculatorMenuBar() { return menuBar; }
    public boolean isFirstNumber() { return isFirstNumber; }
    public boolean isNumberNegative() { return isNumberNegative; }
    //public boolean isMemorySwitchBool() { return memorySwitchBool; }
    public boolean isAdding() { return isAdding; }
    public boolean isSubtracting() { return isSubtracting; }
    public boolean isMultiplying() { return isMultiplying; }
    public boolean isDividing() { return isDividing; }
    //public boolean isMemoryAdding() { return isMemoryAdding; }
    //public boolean isMemorySubtracting() { return isMemorySubtracting; }
    public boolean isNegating() { return isNegating; }
    public boolean isDotPressed() { return isDotPressed; }


    /************* All Setters ******************/
    private void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    private void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setMemoryRecallPosition(int memoryRecallPosition) { this.memoryRecallPosition = memoryRecallPosition; }
    public void setTextArea(JTextPane textArea) { this.textArea = textArea; }
    public void setCalculatorType(CalculatorType calculatorType) { this.calculatorType = calculatorType; }
    public void setConverterType(ConverterType converterType) { this.converterType = converterType; }
    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setCalculatorIcon(ImageIcon calculatorIcon) { this.calculatorIcon = calculatorIcon; }
    public void setMacIcon(ImageIcon macIcon) { this.macIcon = macIcon; }
    public void setWindowsIcon(ImageIcon windowsIcon) { this.windowsIcon = windowsIcon; }
    public void setBlankIcon(ImageIcon blankIcon) { this.blankIcon = blankIcon; }
    public void setFirstNumber(boolean firstNumber) { this.isFirstNumber = firstNumber; }
    public void setNumberNegative(boolean numberNegative) { this.isNumberNegative = numberNegative; }
    public void setAdding(boolean adding) { this.isAdding = adding; }
    public void setSubtracting(boolean subtracting) { this.isSubtracting = subtracting; }
    public void setMultiplying(boolean multiplying) { this.isMultiplying = multiplying; }
    public void setDividing(boolean dividing) { this.isDividing = dividing; }
    public void setNegating(boolean negating) { this.isNegating = negating; }
    public void setDotPressed(boolean dotPressed) { this.isDotPressed = dotPressed; }
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculatorBase = calculatorBase; }
    public void setCalculatorMenuBar(JMenuBar menuBar) { this.menuBar = menuBar; }
}