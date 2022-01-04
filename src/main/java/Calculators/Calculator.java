package Calculators;

import Types.CalculatorBase;
import Types.CalculatorType;
import Types.ConverterType;
import Panels.*;
import com.apple.eawt.Application;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static Panels.DatePanel.*;
import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;

public class Calculator extends JFrame
{
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class);
    private static final long serialVersionUID = 1L;
    public static final Font font = new Font("Segoe UI", Font.PLAIN, 12),
                             font2 = new Font("Verdana", Font.BOLD, 20),
                             font_Bold = new Font("Segoe UI", Font.BOLD, 12);
    public final JButton button0 = new JButton("0"), button1 = new JButton("1"),
            button2 = new JButton("2"), button3 = new JButton("3"),
            button4 = new JButton("4"), button5 = new JButton("5"),
            button6 = new JButton("6"), button7 = new JButton("7"),
            button8 = new JButton("8"), button9 = new JButton("9"),
            buttonClear = new JButton("C"), buttonClearEntry = new JButton("CE"),
            buttonDelete = new JButton("\u2190"), buttonDot = new JButton("."),
            buttonFraction = new JButton("1/x"), buttonPercent = new JButton("%"),
            buttonSqrt = new JButton("\u221A"), buttonMemoryClear = new JButton("MC"),
            buttonMemoryRecall = new JButton("MR"), buttonMemoryStore = new JButton("MS"),
            buttonMemoryAddition = new JButton("M+"), buttonMemorySubtraction = new JButton("M-"),
            buttonAdd = new JButton("+"), buttonSubtract = new JButton("-"),
            buttonMultiply = new JButton("*"), buttonDivide = new JButton("/"),
            buttonEquals = new JButton("="), buttonNegate = new JButton("\u00B1"),
            buttonBlank1 = new JButton(""), buttonBlank2 = new JButton("");
    public String[] values = new String[]{"","","",""}, // firstNum or total, secondNum, copy/paste, temporary storage. memory values now in MemorySuite.memoryValues
                    memoryValues = new String[]{"","","","","","","","","",""}; // stores memory values; rolls over after 10 entries
    public int valuesPosition = 0 ,memoryPosition = 0, memoryRecallPosition = 0;
    public JTextArea textArea = new JTextArea(2,5); // rows, columns
    public StringBuffer textareaValue = new StringBuffer(); // String representing appropriate visual of number
    public CalculatorType calcType;
    public CalculatorBase calcBase;
    public ConverterType converterType;
    public JPanel currentPanel;
    public ImageIcon windowsCalculator, macLogo, windowsLogo, blankImage;
    public JLabel iconLabel, textLabel;
    public JMenuBar bar;
    public boolean firstNumBool = true,
            numberIsNegative = false, memorySwitchBool = false,
            addBool = false, subBool = false,
            mulBool = false, divBool = false,
            memAddBool = false, memSubBool = false,
            negatePressed = false, dotButtonPressed = false,
    // programmer related fields
            isButtonBinSet = true, isButtonOctSet = false,
            isButtonDecSet = false, isButtonHexSet = false,
            isButtonByteSet = true, isButtonWordSet = false,
            isButtonDWordSet = false, isButtonQWordSet = false,
            orButtonBool = false, modButtonBool = false,
            xorButtonBool = false, negateButtonBool = false,
            notButtonBool = false, andButtonBool = false;

    /**
     * Starts up a Basic Calculator
     * @throws Exception when Calculator fails to build
     */
    public Calculator() throws Exception { this(BASIC, null, null); }
    /**
     * This constructor is used to create a Calculator starting with a specific type
     * @param calcType the type of Calculator to create
     */
    public Calculator(CalculatorType calcType) throws Exception { this(calcType, null, null); }
    /**
     * This constructor is used to start up a Programmer Calculator in a specific mode
     * @param calcType the type of Calculator to create, expecting Programmer
     * @param base the base to set that Calculator in
     */
    public Calculator(CalculatorType calcType, CalculatorBase base)
    {
        super(calcType.getName()); // default title is Basic
        setCalculatorType(calcType);
        setupMenuBar(); // setup here for all types
        setupCalculatorImages();
        setCurrentPanel(new ProgrammerPanel(this, base));
        add(currentPanel);
        LOGGER.info("Panel added to calculator");
        setMaximumSize(currentPanel.getSize());
        setCalculatorBase(base); // dbl check to make sure it's set
        pack();
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator\n");
    }
    /**
     * This constructor is used to create a Date Calculator with a specific DatePanel option to start in
     * @param calcType the type of Calculator to create, expecting Date
     * @param chosenOption the option to open the DateCalculator in
     */
    public Calculator(CalculatorType calcType, String chosenOption) throws Exception { this(calcType, null, chosenOption); }
    /**
     * This constructor is used to create a Converter Calculator starting with a specific conversion type
     * @param calcType the type of Calculator to create, expecting Converter
     * @param converterType the type of unit to start the Converter Calculator in
     * @throws Exception when Calculator fails to build
     */
    public Calculator(CalculatorType calcType, ConverterType converterType) throws Exception { this(calcType, converterType, null); }
    /**
     * MAIN CONSTRUCTOR USED
     * @param calcType the type of Calculator to create
     * @param converterType the type of unit to start the Converter Calculator in
     * @param chosenOption the option to open the DateCalculator in
     */
    public Calculator(CalculatorType calcType, ConverterType converterType, String chosenOption)  throws CalculatorError, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calcType.getName());
        setCalculatorType(calcType);
        setupMenuBar(); // setup here for all types
        setupCalculatorImages();
        if (converterType == null && chosenOption == null) setCurrentPanel(determinePanel(calcType));
        else if (converterType != null) setCurrentPanel(determinePanel(calcType, converterType, null));
        else                            setCurrentPanel(determinePanel(calcType, null, chosenOption));
        add(currentPanel);
        updateJPanel(currentPanel);
        LOGGER.info("Panel added to calculator");
        setCalculatorBase(determineCalculatorBase());
        setMaximumSize(currentPanel.getSize());
        pack();
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator");
    }

    /******************** Start of methods here **********************/
    public void setupTextArea()
    {
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setFont(Calculator.font);
        textArea.setEditable(false);
        if (calcType == BASIC) textArea.setPreferredSize(new Dimension(70, 30));
        else if (calcType == PROGRAMMER) {
            LOGGER.warn("Setup");
        }
        else {
            LOGGER.warn("Setup");
        }
        LOGGER.info("Text Area configured");
    }

    /**
     * Handles the logic for setting up a Calculator. Should
     * run once. Anything that needs to be reset, should be reset
     * where appropriate.
     */
    public void setupCalculatorImages()
    {
        setImageIcons();
        // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        Application.getApplication().setDockIconImage(windowsCalculator.getImage());
        setIconImage(windowsCalculator.getImage());
        LOGGER.info("All images set for Calculator");
    }

    public void setupAddButton()
    {
        buttonAdd.setFont(font);
        buttonAdd.setPreferredSize(new Dimension(35, 35) );
        buttonAdd.setBorder(new LineBorder(Color.BLACK));
        buttonAdd.setEnabled(true);
        buttonAdd.addActionListener(this::performAdditionButtonActions);
        LOGGER.info("Add button configured");
    }

    public void setupSubtractButton()
    {
        buttonSubtract.setFont(font);
        buttonSubtract.setPreferredSize(new Dimension(35, 35) );
        buttonSubtract.setBorder(new LineBorder(Color.BLACK));
        buttonSubtract.setEnabled(true);
        buttonSubtract.addActionListener(this::performSubtractionButtonActions);
        LOGGER.info("Subtract button configured");
    }

    public void setupMultiplyButton()
    {
        buttonMultiply.setFont(font);
        buttonMultiply.setPreferredSize(new Dimension(35, 35) );
        buttonMultiply.setBorder(new LineBorder(Color.BLACK));
        buttonMultiply.setEnabled(true);
        buttonMultiply.addActionListener(this::performMultiplicationActions);
        LOGGER.info("Multiply button configured");
    }

    public void setupDivideButton()
    {
        buttonDivide.setFont(font);
        buttonDivide.setPreferredSize(new Dimension(35, 35) );
        buttonDivide.setBorder(new LineBorder(Color.BLACK));
        buttonDivide.setEnabled(true);
        buttonDivide.addActionListener(this::performDivideButtonActions);
        LOGGER.info("Divide button configured");
    }

    public JPanel determinePanel(CalculatorType calcType) throws ParseException, CalculatorError
    { return determinePanel(calcType, null, null); }

    public JPanel determinePanel(CalculatorType calcType, ConverterType converterType, String chosenOption) throws ParseException, CalculatorError
    {
        if (calcType == null)
        {
            LOGGER.error("CalcType cannot be null");
            throw new CalculatorError("CalcType cannot be null");
        }
        if (calcType == BASIC)
        {
            return new BasicPanel(this);
        }
        else if (calcType == PROGRAMMER)
        {
            return new ProgrammerPanel(this);
        }
        else if (calcType == SCIENTIFIC)
        {
            return new ScientificPanel();
        }
        else if (calcType == DATE)
        {
            return new DatePanel(this, chosenOption);
        }
        else //if (calcType == CONVERTER) {
        {
            if (converterType != null) {
                return new ConverterPanel(this, converterType);
            } else {
                LOGGER.error("Add the specific converter panel now");
                throw new CalculatorError("Add the specific converter panel now");
            }
        }
    }

    public CalculatorBase determineCalculatorBase()
    {
        if (currentPanel instanceof BasicPanel) return DECIMAL;
        else if (currentPanel instanceof ProgrammerPanel)
        {
            if (isButtonBinSet) return BINARY;
            else if (isButtonDecSet) return DECIMAL;
            else if (isButtonOctSet) return OCTAL;
            else return HEXADECIMAL;
        }
        else if (currentPanel instanceof ScientificPanel) return BINARY;
        else if (currentPanel instanceof DatePanel) return null;
        else return null;
    }

    public void setupFractionButton()
    {
        buttonFraction.setFont(Calculator.font);
        buttonFraction.setPreferredSize(new Dimension(35, 35) );
        buttonFraction.setBorder(new LineBorder(Color.BLACK));
        buttonFraction.setEnabled(true);
        buttonFraction.addActionListener(this::performFractionButtonActions);
    }

    public void performFractionButtonActions(ActionEvent action)
    {
        LOGGER.info("FracStoreButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            confirm("Cannot perform fraction operation. Number too big!");
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (!textArea.getText().equals("")) {
                double result = Double.parseDouble(values[valuesPosition]);
                result = 1 / result;
                LOGGER.info("result: " + result);
                values[valuesPosition] = Double.toString(result);
                textArea.setText("\n" + values[valuesPosition]);
                LOGGER.info("temp[" + valuesPosition+"] is " + values[valuesPosition]);
            }
            confirm();
        }
    }

    public void setupPercentButton()
    {
        buttonPercent.setFont(Calculator.font);
        buttonPercent.setPreferredSize(new Dimension(35, 35) );
        buttonPercent.setBorder(new LineBorder(Color.BLACK));
        buttonPercent.setEnabled(true);
        buttonPercent.addActionListener(this::performPercentButtonActions);
    }

    public void performPercentButtonActions(ActionEvent action)
    {
        LOGGER.info("PercentStoreButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform percent operation. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (!textArea.getText().equals("")) {
                //if(textArea.getText().substring(textArea.getText().length()-1).equals("-")) { // if the last index equals '-'
                // if the number is negative
                if (isNegativeNumber(textArea.getText().replaceAll("\\n", ""))) {
                    LOGGER.info("if condition true");
                    //temp[valuesPosition] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(values[valuesPosition]);
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    values[valuesPosition] = Double.toString(percent);
                    textareaValue = new StringBuffer().append(formatNumber(values[valuesPosition]));
                    LOGGER.info("Old " + values[valuesPosition]);
                    values[valuesPosition] = values[valuesPosition].substring(1, values[valuesPosition].length());
                    LOGGER.info("New " + values[valuesPosition] + "-");
                    textArea.setText(values[valuesPosition] + "-"); // update textArea
                    LOGGER.info("temp["+valuesPosition+"] is " + values[valuesPosition]);
                    //textArea.setText(textarea);
                    values[valuesPosition] = textareaValue.toString();//+textarea;
                    textareaValue = new StringBuffer();
                    LOGGER.info("temp["+valuesPosition+"] is " + values[valuesPosition]);
                    LOGGER.info("textArea: "+textArea.getText());
                } else {
                    double percent = Double.parseDouble(values[valuesPosition]);
                    percent /= 100;
                    values[valuesPosition] = Double.toString(percent);
                    textArea.setText("\n" + formatNumber(values[valuesPosition]));
                    values[valuesPosition] = textArea.getText().replaceAll("\\n", "");
                    LOGGER.info("temp["+valuesPosition+"] is " + values[valuesPosition]);
                    LOGGER.info("textArea: "+textArea.getText());
                }
            }
            dotButtonPressed = true;
            textareaValue = new StringBuffer().append(textArea.getText());
            confirm();
        }
    }

    public void setupSquareRootButton()
    {
        buttonSqrt.setFont(Calculator.font);
        buttonSqrt.setPreferredSize(new Dimension(35, 35) );
        buttonSqrt.setBorder(new LineBorder(Color.BLACK));
        buttonSqrt.setEnabled(true);
        buttonSqrt.addActionListener(this::performSquareRootButtonActions);
    }

    public void performSquareRootButtonActions(ActionEvent action)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        String errorStringNaN = "Not a Number";
        LOGGER.debug("text: " + textArea.getText().replace("\n",""));
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform square root operation. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            if (textArea.getText().equals("") || isNegativeNumber(textArea.getText()))
            {
                textArea.setText("\n"+errorStringNaN);
                textareaValue = new StringBuffer().append("\n"+errorStringNaN);
                confirm(errorStringNaN + "Cannot perform square root operation on blank/negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.valueOf(textArea.getText())));
                result = formatNumber(result);
                textArea.setText("\n"+result);
                textareaValue = new StringBuffer().append(result);
                confirm();
            }
        }
    }

    public void setupEqualsButton()
    {
        buttonEquals.setFont(font);
        buttonEquals.setPreferredSize(new Dimension(35, 70) );
        buttonEquals.setBorder(new LineBorder(Color.BLACK));
        buttonEquals.setEnabled(true);
        buttonEquals.addActionListener(action -> {
            try
            {
                performButtonEqualsActions();
            }
            catch (Exception calculator_v3Error)
            {
                calculator_v3Error.printStackTrace();
            }
        });
    }

    public void setupNegateButton()
    {
        buttonNegate.setFont(font);
        buttonNegate.setPreferredSize(new Dimension(35, 35) );
        buttonNegate.setBorder(new LineBorder(Color.BLACK));
        buttonNegate.setEnabled(true);
        buttonNegate.addActionListener(this::performNegateButtonActions);
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
        getNumberButtons().forEach(button -> {
            button.setFont(font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0") &&
                calcType != CONVERTER)
                    { button.setPreferredSize(new Dimension(70, 35)); }
            else
                { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performNumberButtonActions);
        });
        LOGGER.info("Number buttons configured");
    }

    public void setupButtonBlank1()
    {
        buttonBlank1.setFont(font);
        buttonBlank1.setPreferredSize(new Dimension(35, 35));
        buttonBlank1.setBorder(new LineBorder(Color.BLACK));
        buttonBlank1.setEnabled(true);
        buttonBlank1.setName("x");
        buttonBlank1.addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Blank button 1 configured");
    }

    public void setupButtonBlank2()
    {
        buttonBlank2.setFont(font);
        buttonBlank2.setPreferredSize(new Dimension(35, 35));
        buttonBlank2.setBorder(new LineBorder(Color.BLACK));
        buttonBlank2.setEnabled(true);
        buttonBlank2.setName("x");
        buttonBlank2.addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Blank button 2 configured");
    }

    public void setupMenuBar()
    {
        LOGGER.info("Setting up the default menu bar");
        // Menu Bar and Menu Choices and each menu options
        setBar(new JMenuBar());
        setJMenuBar(bar); // add menu bar to application

        // Start 4 Menu Choices
        JMenu lookMenu = new JMenu("Look");
        JMenu viewMenu = new JMenu("View");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // commonalities
        lookMenu.setFont(font);
        lookMenu.setName("Look");
        viewMenu.setFont(font);
        viewMenu.setName("View");
        editMenu.setFont(font);
        editMenu.setName("Edit");
        helpMenu.setFont(font);
        helpMenu.setName("Help");

        // Look menu options
        JMenuItem metal = new JMenuItem("Metal");
        JMenuItem system = new JMenuItem("System");
        JMenuItem windows = new JMenuItem("Windows");
        JMenuItem motif = new JMenuItem("Motif");
        JMenuItem gtk = new JMenuItem("GTK");

        metal.setFont(font);
        system.setFont(font);
        windows.setFont(font);
        motif.setFont(font);
        gtk.setFont(font);

        // functions
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
        if (!StringUtils.contains(System.getProperty("os.name").toLowerCase(), "Mac".toLowerCase()))
        {
            lookMenu.add(windows);
            lookMenu.add(system);
            lookMenu.add(gtk);
        } // add more options if using Windows
         bar.add(lookMenu);

        // View menu options, the converterMenu is an option which is a menu of more choices
        JMenuItem basic = new JMenuItem(CalculatorType.BASIC.getName());
        JMenuItem programmer = new JMenuItem(CalculatorType.PROGRAMMER.getName());
        JMenuItem date = new JMenuItem(CalculatorType.DATE.getName());
        JMenu converterMenu = new JMenu(CONVERTER.getName());

        // commonalities
        basic.setFont(font);
        basic.setName(BASIC.getName());
        basic.addActionListener(this::performTasksWhenChangingJPanels);

        programmer.setFont(font);
        programmer.setName(PROGRAMMER.getName());
        programmer.addActionListener(this::performTasksWhenChangingJPanels);

        date.setFont(font);
        date.setName(DATE.getName());
        date.addActionListener(this::performTasksWhenChangingJPanels);

        converterMenu.setFont(font);
        converterMenu.setName(CONVERTER.getName());
        // options for converterMenu
        JMenuItem angleConverter = new JMenuItem(ANGLE.getName());
        JMenuItem areaConverter = new JMenuItem(AREA.getName());

        // commonalities
        angleConverter.setFont(font);
        angleConverter.setName(ANGLE.getName());
        areaConverter.setFont(font);
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
         bar.add(viewMenu); // add viewMenu to menu bar

        // Edit Menu options
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");

        // commonalities
        copyItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.setFont(font);
        copyItem.setName("Copy");
        copyItem.addActionListener(this::performCopyFunctionality);

        pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setFont(font);
        pasteItem.setName("Paste");
        pasteItem.addActionListener(this::performPasteFunctionality);

        editMenu.add(copyItem);
        editMenu.add(pasteItem);
         bar.add(editMenu); // add editMenu to menu bar

        // Help Options
        JMenuItem viewHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();

        helpMenu.add(viewHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
         bar.add(helpMenu); // add helpMenu to menu bar
        // End All Menu Choices
        LOGGER.info("Finished creating the menu bar");
    }

    /**
     * This method creates the View Help menu option under Help
     * It also sets the functionality
     */
    public JMenuItem createViewHelpJMenuItem()
    {
        JMenuItem viewHelpItem = new JMenuItem("View Help");
        viewHelpItem.setName("View Help");
        viewHelpItem.setFont(font);
        //viewHelpItem.addActionListener(this::performViewHelpFunctionality); performed by panel
        return viewHelpItem;
    }

    /** This method creates the About Calculator menu option under Help */
    public JMenuItem createAboutCalculatorJMenuItem()
    {
        JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");
        aboutCalculatorItem.setName("About Calculator");
        aboutCalculatorItem.setFont(font);
        aboutCalculatorItem.addActionListener(this::performAboutCalculatorFunctionality);
        return aboutCalculatorItem;
    }

    public void setupDeleteButton()
    {
        buttonDelete.setFont(font);
        buttonDelete.setPreferredSize(new Dimension(35, 35));
        buttonDelete.setBorder(new LineBorder(Color.BLACK));
        buttonDelete.setEnabled(true);
        buttonDelete.setName("\u2190");
        buttonDelete.addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }

    public void setupClearEntryButton()
    {
        buttonClearEntry.setFont(font);
        buttonClearEntry.setMaximumSize(new Dimension(35, 35));
        buttonClearEntry.setBorder(new LineBorder(Color.BLACK));
        buttonClearEntry.setEnabled(true);
        buttonClearEntry.setName("CE");
        buttonClearEntry.addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }

    public void setupClearButton()
    {
        buttonClear.setFont(font);
        buttonClear.setPreferredSize(new Dimension(35, 35));
        buttonClear.setBorder(new LineBorder(Color.BLACK));
        buttonClear.setEnabled(true);
        buttonClear.setName("C");
        buttonClear.addActionListener(action -> {
            performClearButtonActions(action);
            if (calcType == CalculatorType.PROGRAMMER)
            {
                ((ProgrammerPanel) currentPanel).resetProgrammerOperators();
            }
        });
        LOGGER.info("Clear button configured");
    }

    public void setupDotButton()
    {
        buttonDot.setFont(font);
        buttonDot.setPreferredSize(new Dimension(35, 35));
        buttonDot.setBorder(new LineBorder(Color.BLACK));
        buttonDot.setEnabled(true);
        buttonDot.setName(".");
        buttonDot.addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }

	public void setupMemoryButtons()
    {
        buttonMemoryStore.setFont(Calculator.font);
        buttonMemoryStore.setPreferredSize(new Dimension(35, 35));
        buttonMemoryStore.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryStore.setEnabled(true);
        buttonMemoryStore.setName("MS");
        buttonMemoryStore.addActionListener(this::performMemoryStoreActions);
        LOGGER.info("Memory Store button configured");
        buttonMemoryClear.setFont(Calculator.font);
        buttonMemoryClear.setPreferredSize(new Dimension(35, 35));
        buttonMemoryClear.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryClear.setEnabled(false);
        buttonMemoryClear.setName("MC");
        buttonMemoryClear.addActionListener(this::performMemoryClearActions);
        LOGGER.info("Memory Clear button configured");
        buttonMemoryRecall.setFont(Calculator.font);
        buttonMemoryRecall.setPreferredSize(new Dimension(35, 35));
        buttonMemoryRecall.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryRecall.setEnabled(false);
        buttonMemoryRecall.setName("MR");
        buttonMemoryRecall.addActionListener(this::performMemoryRecallActions);
        LOGGER.info("Memory Recall button configured");
        buttonMemoryAddition.setFont(Calculator.font);
        buttonMemoryAddition.setPreferredSize(new Dimension(35, 35));
        buttonMemoryAddition.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryAddition.setEnabled(false);
        buttonMemoryAddition.setName("M+");
        buttonMemoryAddition.addActionListener(this::performMemoryAddActions);
        LOGGER.info("Memory Add button configured");
        buttonMemorySubtraction.setFont(Calculator.font);
        buttonMemorySubtraction.setPreferredSize(new Dimension(35, 35));
        buttonMemorySubtraction.setBorder(new LineBorder(Color.BLACK));
        buttonMemorySubtraction.setEnabled(false);
        buttonMemorySubtraction.setName("M-");
        buttonMemorySubtraction.addActionListener(this::performMemorySubtractionActions);
        LOGGER.info("Memory Subtract button configured");
        // reset buttons to enabled if memories are saved
        if (!memoryValues[0].equals(""))
        {
            buttonMemoryClear.setEnabled(true);
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryAddition.setEnabled(true);
            buttonMemorySubtraction.setEnabled(true);
        }
    }

    public void performInitialChecks()
    {
        boolean checkFound = false;
        if (textArea1ContainsBadText())
        {
            textArea.setText("");
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
            numberIsNegative = false;
            checkFound = true;
        }
        else if (getTextAreaWithoutAnything().equals("0") &&
                (calcType.equals(BASIC) ||
                (calcType == PROGRAMMER && calcBase == DECIMAL)))
        {
            LOGGER.debug("textArea equals 0 no matter the form. setting to blank.");
            textArea.setText("");
            textareaValue = new StringBuffer();
            values[valuesPosition] = "";
            firstNumBool = true;
            dotButtonPressed = false;
            checkFound = true;
        }
        else if (StringUtils.isBlank(values[0]) && StringUtils.isNotBlank(values[1]))
        {
            values[0] = values[1];
            values[1] = "";
            valuesPosition = 0;
            checkFound = true;
        }
        LOGGER.info("Performing initial checks.... results: " + checkFound);
    }

    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        if (!firstNumBool) // do for second number
        {
            if (!dotButtonPressed)
            {
                textArea.setText("");
                setTextareaValue(new StringBuffer().append(textArea.getText()));
                if (!firstNumBool) {
                    setFirstNumBool(true);
                    setNumberIsNegative(false);
                } else
                    setDotButtonPressed(true);
                buttonDot.setEnabled(true);
            }
        }
        performInitialChecks();
        LOGGER.info("Performing number button actions...");
        if (isPositiveNumber(values[valuesPosition]) && !dotButtonPressed)
        {
            LOGGER.info("positive number & dot button was not pushed");
            LOGGER.debug("before: '" + values[valuesPosition] + "'");
            if (StringUtils.isBlank(values[valuesPosition]))
            {
                textareaValue = new StringBuffer().append(buttonChoice);
            }
            else
            {
                textareaValue = new StringBuffer().append(values[valuesPosition]).append(buttonChoice);
            }
            LOGGER.debug("after: '" + textareaValue + "'");
            setValuesToTextAreaValue();
            updateTheTextAreaBasedOnTheTypeAndBase();

        }
        else if (numberIsNegative && !dotButtonPressed)
        { // logic for negative numbers
            LOGGER.info("negative number & dot button had not been pushed");
            setTextareaToValuesAtPosition(buttonChoice);
        }
        else if (isPositiveNumber(values[valuesPosition]))
        {
            LOGGER.info("positive number & dot button had been pushed");
            performLogicForDotButtonPressed(buttonChoice);
        }
        else
        {
            LOGGER.info("dot button was pushed");
            performLogicForDotButtonPressed(buttonChoice);
        }
        confirm("Pressed " + buttonChoice);
    }

    public void performMemoryStoreActions(ActionEvent action)
    {
        LOGGER.info("MemoryStoreButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharactersOrWhiteSpace())) // if there is a number in the textArea
        {
            if (memoryPosition == 10) // reset to 0
            {
                memoryPosition = 0;
            }
            memoryValues[memoryPosition] = getTextAreaWithoutNewLineCharactersOrWhiteSpace();
            memoryPosition++;
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryClear.setEnabled(true);
            buttonMemoryAddition.setEnabled(true);
            buttonMemorySubtraction.setEnabled(true);
            confirm(values[valuesPosition] + " is stored in memory at position: " + (memoryPosition-1));
        }
        else {
            confirm("No number added to memory. Blank entry");
        }
    }

    public void performMemoryRecallActions(ActionEvent action)
    {
        LOGGER.info("MemoryRecallButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (memoryRecallPosition == 10 || StringUtils.isBlank(memoryValues[memoryRecallPosition]))
        {
            memoryRecallPosition = 0;
        }
        if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + memoryValues[memoryRecallPosition]);
        else textArea.setText(addNewLineCharacters(3) + memoryValues[memoryRecallPosition]);
        memoryRecallPosition++;
        confirm("Recalling number in memory: " + memoryValues[(memoryRecallPosition-1)] + " at position: " + (memoryRecallPosition-1));
    }

    public void performMemoryClearActions(ActionEvent action)
    {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand());
        if (memoryPosition == 10 || StringUtils.isBlank(memoryValues[memoryPosition]))
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            memoryPosition = 0;
        }
        if (!isMemoryValuesEmpty())
        {
            LOGGER.info("Clearing memoryValue["+memoryPosition+"] = " + memoryValues[memoryPosition]);
            memoryValues[memoryPosition] = "";
            memoryPosition++;
            // MemorySuite now could potentially be empty
            if (isMemoryValuesEmpty())
            {
                memoryPosition = 0;
                memoryRecallPosition = 0;
                buttonMemoryClear.setEnabled(false);
                buttonMemoryRecall.setEnabled(false);
                buttonMemoryAddition.setEnabled(false);
                buttonMemorySubtraction.setEnabled(false);
                textArea.setText(addNewLineCharacters(1)+"0");
                setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                confirm("MemorySuite is blank");
                return;
            }
            // MemorySuite now could potentially have gaps in front
            // {"5", "7", "", ""...} ... {"", "7", "", ""...}
            // If the first is a gap, then we have 1 too many gaps
            // TODO: fix for gap being in the middle of the memory values
            if(memoryValues[0].equals(""))
            {
                // Determine where the first number is that is not a gap
                int alpha = 0;
                for(int i = 0; i < 9; i++)
                {
                    if ( !memoryValues[i].equals("") )
                    {
                        alpha = i;
                        textArea.setText(addNewLineCharacters(1)+memoryValues[alpha]);
                        setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                        break;
                    }
                    else
                    {
                        textArea.setText(addNewLineCharacters(1)+"0");
                    }
                }
                // Move the first found value to the first position
                // and so on until the end
                String[] newMemoryValues = new String[10];
                for(int i = 0; i <= alpha; i++)
                {
                    if (memoryValues[alpha].equals("")) break;
                    newMemoryValues[i] = memoryValues[alpha];
                    alpha++;
                    if (alpha == memoryValues.length) break;
                }
                setMemoryValues(newMemoryValues);
                setMemoryPosition(alpha); // next blank spot
            }
        }
        confirm("Reset memory at position: " + (memoryPosition-1) + ".");
    }

    public void performMemoryAddActions(ActionEvent action)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        // if there is a number in the textArea, and we have A number stored in memory, add the
        // number in the textArea to the value in memory. value in memory should be the last number
        // added to memory
        if (isMemoryValuesEmpty())
        {
            confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+"]: '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                    + Double.parseDouble(memoryValues[(memoryPosition-1)]); // create result forced double
            LOGGER.info(getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " + " + memoryValues[(memoryPosition-1)] + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]);
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            textArea.setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
            setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    public void performMemorySubtractionActions(ActionEvent action)
    {
        LOGGER.info("MemorySubButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (isMemoryValuesEmpty())
        {
            confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+": '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(memoryValues[(memoryPosition-1)])
                    - Double.parseDouble(getTextAreaWithoutNewLineCharactersOrWhiteSpace()); // create result forced double
            LOGGER.info(memoryValues[(memoryPosition-1)] + " - " + getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]);
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            textArea.setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
            setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * When the user clicks the Clear button, everything in the
     * calculator returns to initial start mode.
     * @param action the action performed
     */
    public void performClearButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        // clear values
        for (int i=0; i < 3; i++)
        {
            values[i] = "";
        }
        // clear memory
        for(int i=0; i < 10; i++)
        {
            memoryValues[i] = "";
        }
        if (calcType == BASIC) textArea.setText(addNewLineCharacters(1)+"0");
        else if (calcType == PROGRAMMER) {
            textArea.setText(addNewLineCharacters(3)+"0");
            resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((ProgrammerPanel)currentPanel).resetProgrammerOperators();
            ((ProgrammerPanel)currentPanel).buttonByte.setSelected(true);
            isButtonByteSet = true;
        }
        updateTextareaFromTextArea();
        resetBasicOperators(false);
        valuesPosition = 0;
        memoryPosition = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        numberIsNegative = false;
        buttonMemoryRecall.setEnabled(false);
        buttonMemoryClear.setEnabled(false);
        buttonDot.setEnabled(true);
        LOGGER.info("ClearButtonHandler() finished");
        confirm();
    }

    public void performClearEntryButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        textArea.setText("");
        updateTextareaFromTextArea();
        if (values[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
            values[0] = "";
            addBool = false;
            subBool = false;
            mulBool = false;
            divBool = false;
            valuesPosition = 0;
            firstNumBool = true;
        } else {
            values[1] = "";
        }
        if (calcType == PROGRAMMER) {
            resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((ProgrammerPanel)currentPanel).resetProgrammerOperators();
            ((ProgrammerPanel)currentPanel).buttonByte.setSelected(true);
            isButtonByteSet = true;
        }
        dotButtonPressed = false;
        buttonDot.setEnabled(true);
        numberIsNegative = false;
        textareaValue.append(textArea.getText());
        LOGGER.info("ClearEntryButtonHandler() finished");
        confirm();
    }

    public void performDeleteButtonActions(ActionEvent action)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (values[1].equals("")) {
            valuesPosition = 0;
        } // assume they just previously pressed an operator

        LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
        LOGGER.info("textarea: " + textareaValue);
        numberIsNegative = isNegativeNumber(values[valuesPosition]);
        // this check has to happen
        dotButtonPressed = isDecimal(textareaValue.toString());
        textArea.setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        updateTextareaFromTextArea();
        if (!addBool && !subBool && !mulBool && !divBool)
        {
            if (!numberIsNegative)
            {
                // if no operator has been pushed; number is positive; number is whole
                if (!dotButtonPressed)
                {
                    if (textareaValue.length() == 1)
                    { // ex: 5
                        textArea.setText("");
                        textareaValue = new StringBuffer().append(" ");
                        values[valuesPosition] = "";
                    }
                    else if (textareaValue.length() >= 2)
                    { // ex: 56 or 2346
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                        if (calcType == BASIC) textArea.setText(addNewLineCharacters(1)+ textareaValue);
                        else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3)+ textareaValue);
                        values[valuesPosition] = textareaValue.toString();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else if (dotButtonPressed) {
                    if (textareaValue.length() == 2) { // ex: 3. .... recall textarea looks like .3
                        textareaValue = new StringBuffer().append(textareaValue.substring(textareaValue.length()-1)); // ex: 3
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textareaValue.length() == 3) { // ex: 3.2 or 0.5
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length() - 2)); // ex: 3 or 0
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textareaValue.length() > 3) { // ex: 3.25 or 0.50 or 5.02 or 78.9
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length() - 1)); // inclusive
                        if (textareaValue.toString().endsWith("."))
                            textareaValue = new StringBuffer().append(getNumberOnLeftSideOfNumber(textareaValue.toString()));
                    }
                    LOGGER.info("output: " + textareaValue);
                    if (calcType == BASIC) textArea.setText(addNewLineCharacters(1)+ textareaValue);
                    else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3)+ textareaValue);
                    values[valuesPosition] = textareaValue.toString();
                }
            }
            else if (numberIsNegative)
            {
                // if no operator has been pushed; number is negative; number is whole
                if (!dotButtonPressed) {
                    if (textareaValue.length() == 2) { // ex: -3
                        textareaValue = new StringBuffer();
                        textArea.setText(textareaValue.toString());
                        values[valuesPosition] = "";
                    }
                    else if (textareaValue.length() >= 3) { // ex: -32 or + 6-
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString()));
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()));
                        textArea.setText(textareaValue + "-");
                        values[valuesPosition] = "-" + textareaValue;
                    }
                    LOGGER.info("output: " + textareaValue);
                }
                // if no operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed) {
                    if (textareaValue.length() == 4) { // -3.2
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString())); // 3.2
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, 1)); // 3
                        dotButtonPressed = false;
                        textArea.setText(textareaValue + "-");
                        values[valuesPosition] = "-" + textareaValue;
                    } else if (textareaValue.length() > 4) { // ex: -3.25 or -0.00
                        textareaValue.append(convertToPositive(textareaValue.toString())); // 3.00 or 0.00
                        textareaValue.append(textareaValue.substring(0, textareaValue.length())); // 3.0 or 0.0
                        textareaValue.append(clearZeroesAndDecimalAtEnd(textareaValue.toString())); // 3 or 0
                        textArea.setText(textareaValue + "-");
                        values[valuesPosition] = "-" + textareaValue;
                    }
                    LOGGER.info("output: " + textareaValue);
                }
            }

        }
        else if (addBool || subBool || mulBool || divBool)
        {
            if (!numberIsNegative) {
                // if an operator has been pushed; number is positive; number is whole
                if (!dotButtonPressed) {
                    if (textareaValue.length() == 1) { // ex: 5
                        textareaValue = new StringBuffer();
                    } else if (textareaValue.length() == 2) {
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                    } else if (textareaValue.length() >= 2) { // ex: 56 or + 6-
                        if (addBool || subBool || mulBool || divBool) {
                            textareaValue = new StringBuffer().append(values[valuesPosition]);
                            addBool = false;
                            subBool = false;
                            mulBool = false;
                            divBool = false;
                        } else {
                            textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                        }
                    }
                    LOGGER.info("output: " + textareaValue);
                    textArea.setText("\n" + textareaValue);
                    values[valuesPosition] = textareaValue.toString();
                    confirm();
                }
                // if an operator has been pushed; number is positive; number is decimal
                else if (dotButtonPressed) {
                    if (textareaValue.length() == 2) // ex: 3.
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                    else if (textareaValue.length() == 3) { // ex: 3.2 0.0
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-2)); // 3 or 0
                        dotButtonPressed = false;
                    } else if (textareaValue.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        if (addBool || subBool || mulBool || divBool) {
                            textareaValue = new StringBuffer().append(values[valuesPosition]);
                        } else {
                            textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length() -1));
                            textareaValue.append(clearZeroesAndDecimalAtEnd(textareaValue.toString()));
                        }
                    }
                    LOGGER.info("output: " + textareaValue);
                    textArea.setText("\n"+ textareaValue);
                    values[valuesPosition] = textareaValue.toString();
                    confirm();
                }
            } else if (numberIsNegative) {
                // if an operator has been pushed; number is negative; number is whole
                if (!dotButtonPressed) {
                    if (textareaValue.length() == 2) { // ex: -3
                        textareaValue = new StringBuffer();
                        textArea.setText(textareaValue.toString());
                        values[valuesPosition] = "";
                    } else if (textareaValue.length() >= 3) { // ex: -32 or + 6-
                        if (addBool || subBool || mulBool || divBool) {
                            textareaValue.append(values[valuesPosition]);
                        }
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString()));
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()));
                        textArea.setText("\n" + textareaValue + "-");
                        values[valuesPosition] = "-" + textareaValue;
                    }
                    LOGGER.info("textarea: " + textareaValue);
                    confirm();
                }
                // if an operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed) {
                    if (textareaValue.length() == 4) { // -3.2
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString())); // 3.2 or 0.0
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, 1)); // 3 or 0
                        dotButtonPressed = false;
                        textArea.setText(textareaValue + "-"); // 3- or 0-
                        values[valuesPosition] = "-" + textareaValue; // -3 or -0
                    } else if (textareaValue.length() > 4) { // ex: -3.25  or -0.00
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString())); // 3.25 or 0.00
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length())); // 3.2 or 0.0
                        values[0] = clearZeroesAndDecimalAtEnd(textareaValue.toString());
                        LOGGER.info("textarea: " + textareaValue);
                        if (textareaValue.toString().equals("0")) {
                            textArea.setText(textareaValue.toString());
                            values[valuesPosition] = textareaValue.toString();
                        } else {
                            textArea.setText(textareaValue + "-");
                            values[valuesPosition] = "-" + textareaValue;
                        }
                    }
                    LOGGER.info("textarea: " + textareaValue);
                }
            }
            resetBasicOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        confirm();
    }

    public void performDot(String buttonChoice)
    {

        if (StringUtils.isBlank(values[valuesPosition]) || !firstNumBool)
        {   // dot pushed with nothing in textArea
            textareaValue = new StringBuffer().append("0").append(buttonChoice);
            setValuesToTextAreaValue();
            updateTheTextAreaBasedOnTheTypeAndBase();
        }
        else if (isPositiveNumber(values[valuesPosition]) && !dotButtonPressed)
        {   // number and then dot is pushed ex: 5 -> .5
            //StringBuffer lodSB = new StringBuffer(textareaValue);
            textareaValue = new StringBuffer().append(values[valuesPosition]).append(buttonChoice);
            setValuesToTextAreaValue();
            textareaValue = new StringBuffer(values[valuesPosition].replace(".",""));
            textareaValue = new StringBuffer().append(buttonChoice).append(textareaValue);
            updateTheTextAreaBasedOnTheTypeAndBase();
            textareaValue = new StringBuffer(values[valuesPosition].replace(".","")+".");
            dotButtonPressed = true; // !LEAVE. dot logic should not be executed anymore for the current number
        }
        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
        {
            textareaValue = new StringBuffer().append(convertToPositive(values[valuesPosition]));
            textareaValue.append(buttonChoice);
            textareaValue = new StringBuffer().append(convertToNegative(textareaValue.toString()));
            setValuesToTextAreaValue();
            updateTheTextAreaBasedOnTheTypeAndBase();
        }
        buttonDot.setEnabled(false); // deactivate button now that its active for this number
        setDotButtonPressed(true); // control variable used to check if we have pushed the dot button
    }

    public void performDotButtonActions(ActionEvent action)
    {
        LOGGER.info("Starting Dot button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (values[0].contains("E")) { confirm("Cannot press dot button. Number too big!"); }
        else
        {
            if (calcType == BASIC)
            {
                LOGGER.info("Basic dot operations");
                performDot(buttonChoice);
            }
            else if (calcType == PROGRAMMER)
            {
                LOGGER.info("Programmer dot operations");
                if (calcBase == BINARY)
                {
                    LOGGER.warn("Setup");
                }
                else if (calcBase == DECIMAL)
                {
                    LOGGER.info("DECIMAL base");
                    performDot(buttonChoice);
                }
                else
                {
                    LOGGER.warn("Add other bases");
                }
            }
            else
            {
                LOGGER.warn("Add other types");
            }
        }
        confirm("Pressed the Dot button");
    }

    public void performLogicForDotButtonPressed(String buttonChoice)
    {
        textareaValue = new StringBuffer(values[valuesPosition]);
        LOGGER.info("textareaValue {}", textareaValue);
        if (StringUtils.isNotBlank(textareaValue) && dotButtonPressed && numberIsNegative)
        {
            LOGGER.info("isDotButtonPressed {} isNumberNegative {}", dotButtonPressed, numberIsNegative);
            textareaValue = new StringBuffer().append(convertToPositive(getTextAreaWithoutAnything()));
            textareaValue.append(buttonChoice);
            textareaValue = new StringBuffer().append(convertToNegative(textareaValue.toString()));
            dotButtonPressed = false;
            updateTheTextAreaBasedOnTheTypeAndBase();
            setValuesToTextAreaValue();
        }
        else if (StringUtils.isNotBlank(textareaValue) && dotButtonPressed && !numberIsNegative)
        {
            LOGGER.info("isDotButtonPressed {} isNumberNegative {}", dotButtonPressed, numberIsNegative);
            String leftOfDecimal = getNumberOnLeftSideOfNumber(values[valuesPosition]);
            String rightOfDecimal = getNumberOnRightSideOfNumber(values[valuesPosition]);
            if (!leftOfDecimal.equals("0") && !leftOfDecimal.equals("") &&
                !rightOfDecimal.equals("0") && !rightOfDecimal.equals("")) {
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
                updateTheTextAreaBasedOnTheTypeAndBase();
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
            }
            else if (leftOfDecimal.equals("0") && rightOfDecimal.equals("0"))
            {
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
                updateTheTextAreaBasedOnTheTypeAndBase();
            }
            else if (!leftOfDecimal.equals("0") && rightOfDecimal.equals(""))
            {
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(buttonChoice);
                updateTheTextAreaBasedOnTheTypeAndBase();
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(buttonChoice);
            }
            else if (!leftOfDecimal.equals("0")) // && !rightOfDecimal.equals(""))
            {
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
                LOGGER.info("textareaValue {}", textareaValue);
                updateTheTextAreaBasedOnTheTypeAndBase();
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
            }
            else
            {
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
                LOGGER.info("textareaValue {}", textareaValue);
                updateTheTextAreaBasedOnTheTypeAndBase();
                textareaValue = new StringBuffer().append(leftOfDecimal).append(".").append(rightOfDecimal).append(buttonChoice);
            }
            setValuesToTextAreaValue();
        }
        else if (StringUtils.isNotBlank(textareaValue) && !dotButtonPressed)
        {
            LOGGER.info("textareaValue {} isDotButtonPressed {}", getTextareaValueWithoutAnything(), dotButtonPressed);
            textareaValue.append(buttonChoice).append(getTextAreaWithoutAnything());
            textareaValue.reverse();
            updateTheTextAreaBasedOnTheTypeAndBase();
            setValuesToTextAreaValue();
        }
    }

    public StringBuffer getTextareaValueWithoutAnything() {
        return new StringBuffer().append(textareaValue.toString().replaceAll("\n",""));
    }

    public String addNewLineCharacters(int number)
    { return "\n".repeat(Math.max(0, number)); }

    @Deprecated(since = "use updateTexAreaUsingBaseAndType")
    public void updateTextareaFromTextArea() { setTextareaValue(new StringBuffer().append(getTextAreaWithoutAnything())); }

    /**
     *  Returns the results of the last action
     */
    public void confirm()
    {
	    confirm("", calcType);
    }

    public void confirm(String message)
    {
        confirm(message, calcType);
    }

    /**
     *  Returns the results of the last action with a specific message to display
     *
     * @param message a message to send into the confirm results view
     */
    public void confirm(String message, CalculatorType calculatorType)
    {
        if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: {}", message); }
        else { LOGGER.info("Confirm Results"); }
        LOGGER.info("---------------- ");
        switch (calculatorType) {
            case BASIC : {
                LOGGER.info("textareaValue: '{}'", getTextareaValueWithoutAnything());
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition]))
                { LOGGER.info("no memories stored!"); }
                else
                {
                    LOGGER.info("memoryPosition: '{}'", memoryPosition);
                    LOGGER.info("memoryRecallPosition: '{}'", memoryRecallPosition);
                    for(int i = 0; i < 10; i++)
                        {if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues[{}]: ", memoryValues[i]);}}}
                LOGGER.info("addBool: '{}'", addBool);
                LOGGER.info("subBool: '{}'", subBool);
                LOGGER.info("mulBool: '{}'", mulBool);
                LOGGER.info("divBool: '{}'", divBool);
                LOGGER.info("values[{}]: '{}'",0, values[0]);
                LOGGER.info("values[{}]: '{}'",1, values[1]);
                LOGGER.info("values[{}]: '{}'",2, values[2]);
                LOGGER.info("valuesPosition: '{}'", valuesPosition);
                LOGGER.info("firstNumBool: '{}'", firstNumBool);
                LOGGER.info("dotButtonPressed: '{}'", dotButtonPressed);
                LOGGER.info("isNegative: '{}'", numberIsNegative);
                LOGGER.info("calcType: '{}", calcType);
                LOGGER.info("calcBase: '{}'", calcBase);
                break;
            }
            case PROGRAMMER : {
                LOGGER.info("textareaValue: '{}'", getTextareaValueWithoutAnything());
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition]))
                { LOGGER.info("no memories stored!"); }
                else
                {
                    LOGGER.info("memoryPosition: '{}'", memoryPosition);
                    LOGGER.info("memoryRecallPosition: '{}'", memoryRecallPosition);
                    for(int i = 0; i < 10; i++)
                    {if (StringUtils.isNotBlank(memoryValues[i])) {
                        LOGGER.info("memoryValues[{}]: ", memoryValues[i]);}}}
                LOGGER.info("addBool: '{}'", addBool);
                LOGGER.info("subBool: '{}'", subBool);
                LOGGER.info("mulBool: '{}'", mulBool);
                LOGGER.info("divBool: '{}'", divBool);
                LOGGER.info("orButtonBool: '{}'", orButtonBool);
                LOGGER.info("modButtonBool: '{}'", modButtonBool);
                LOGGER.info("xorButtonBool: '{}'", xorButtonBool);
                LOGGER.info("notButtonBool: '{}'", notButtonBool);
                LOGGER.info("andButtonBool: '{}'", andButtonBool);
                LOGGER.info("values[{}]: '{}'",0, values[0]);
                LOGGER.info("values[{}]: '{}'",1, values[1]);
                LOGGER.info("values[{}]: '{}'",2, values[2]);
                LOGGER.info("valuesPosition: '{}'", valuesPosition);
                LOGGER.info("firstNumBool: '{}'", firstNumBool);
                LOGGER.info("dotButtonPressed: '{}'", dotButtonPressed);
                LOGGER.info("isNegative: '{}'", numberIsNegative);
                LOGGER.info("calcType: '{}", calcType);
                LOGGER.info("calcBase: '{}'", calcBase);
                try { LOGGER.info("calcByte: '{} {}'", getBytes(), getByteWord()); } catch (CalculatorError c) { logException(c); }
                break;
            }
            case SCIENTIFIC : { LOGGER.warn("Confirm message not setup for " + calculatorType); break; }
            case DATE : {
                if (((DatePanel)currentPanel).selectedOption.equals(OPTIONS1))
                {
                    LOGGER.info("{} Selected", OPTIONS1);
                    int year = ((DatePanel)currentPanel).getTheYearFromTheFromDatePicker();
                    int month = ((DatePanel)currentPanel).getTheMonthFromTheFromDatePicker();
                    int day = ((DatePanel)currentPanel).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " +date);
                    year = ((DatePanel)currentPanel).getTheYearFromTheToDatePicker();
                    month = ((DatePanel)currentPanel).getTheMonthFromTheToDatePicker();
                    day = ((DatePanel)currentPanel).getTheDayOfTheMonthFromTheToDatePicker();
                    date = LocalDate.of(year, month, day);
                    LOGGER.info("ToDate(yyyy-mm-dd): " + date);
                    LOGGER.info("Difference");
                    LOGGER.info("Year: " + ((DatePanel)currentPanel).yearsDifferenceLabel.getText());
                    LOGGER.info("Month: " + ((DatePanel)currentPanel).monthsDifferenceLabel.getText());
                    LOGGER.info("Weeks: " + ((DatePanel)currentPanel).monthsDifferenceLabel.getText());
                    LOGGER.info("Days: " + ((DatePanel)currentPanel).daysDifferenceLabel.getText());
                }
                else {
                    LOGGER.info("{} Selected", OPTIONS2);
                    int year = ((DatePanel)currentPanel).getTheYearFromTheFromDatePicker();
                    int month = ((DatePanel)currentPanel).getTheMonthFromTheFromDatePicker();
                    int day = ((DatePanel)currentPanel).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " + date);
                    boolean isAddSelected = ((DatePanel)currentPanel).addRadioButton.isSelected();
                    LOGGER.info("Add or Subtract Selection: " + (isAddSelected ? "Add" : "Subtract") );
                    LOGGER.info("New Date: " + ((DatePanel)currentPanel).resultsLabel.getText());
                }
                break;
            }
            case CONVERTER:  {
                LOGGER.info("Converter: '{}'", ((ConverterPanel)currentPanel).converterName);
                LOGGER.info("text field 1: '{}'", ((ConverterPanel)currentPanel).textField1.getText() + " "
                        + ((ConverterPanel)currentPanel).unitOptions1.getSelectedItem());
                LOGGER.info("text field 2: '{}'", ((ConverterPanel)currentPanel).textField2.getText() + " "
                        + ((ConverterPanel)currentPanel).unitOptions2.getSelectedItem());
                break;
            }
        }
        LOGGER.info("-------- End Confirm Results --------\n");
    }

    /**
     * This method resets the 4 main operators to the boolean you pass in
     */
    public void resetBasicOperators(boolean bool)
    {
        addBool = bool;
        subBool = bool;
        mulBool = bool;
        divBool = bool;
    }

    public void resetProgrammerByteOperators(boolean byteOption)
    {
        isButtonByteSet = byteOption;
        isButtonWordSet = byteOption;
        isButtonDWordSet = byteOption;
        isButtonQWordSet = byteOption;
    }

    /**
     * Resets all operators to the given boolean argument
     *
     * @param operatorBool the operator to reset (add, sub, mul, or div)
     * @return
     *
     * Fully tested
     */
    public boolean resetOperator(boolean operatorBool)
    {
        LOGGER.info("resetting operator...");
        if (operatorBool) {
            values[1]= "";
            valuesPosition = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            return false;
        } else {
            values[1]= "";
            valuesPosition = 0;
            dotButtonPressed = false;
            firstNumBool = true;
            return true;
        }
    }

    protected String getNumberOnLeftSideOfNumber(String currentNumber)
    {
        String leftSide;
        int index = currentNumber.indexOf(".");
        if (index <= 0 || (index+1)>currentNumber.length()) leftSide = "";
        else {
            leftSide = currentNumber.substring(0,index);
            if (StringUtils.isEmpty(leftSide)) leftSide = "0";
        }
        LOGGER.info("number to the left of the decimal: '{}'", leftSide);
        return leftSide;
    }

    protected String getNumberOnRightSideOfNumber(String currentNumber)
    {
        String rightSide;
        int index = currentNumber.indexOf(".");
        if (index == -1 || (index+1)>=currentNumber.length()) rightSide = "";
        else {
            rightSide = currentNumber.substring(index+1);
            if (StringUtils.isEmpty(rightSide)) rightSide = "0";
        }
        LOGGER.info("number to the right of the decimal: '{}'", rightSide);
        return rightSide;
    }

    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     *
     * @param currentNumber the value to clear
     * @return updated currentNumber
     *
     * TODO: Test
     */
    protected String clearZeroesAndDecimalAtEnd(String currentNumber)
    {
        LOGGER.info("Starting clear zeroes at the end");
        LOGGER.debug("currentNumber: " + currentNumber);
        textareaValue = new StringBuffer().append(textareaValue.toString().replaceAll("\n",""));
        int index;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) textareaValue = new StringBuffer().append(currentNumber);
        else {
            textareaValue = new StringBuffer().append(currentNumber.substring(0, index));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + textareaValue);
        return textareaValue.toString();
    }

    public int getBytes() throws CalculatorError
    {
        if (isButtonByteSet) { return 8; }
        else if (isButtonWordSet) { return 16; }
        else if (isButtonDWordSet) { return 32; }
        else if (isButtonQWordSet) { return 64; }
        else { throw new CalculatorError("Unable to determine bytes"); } // shouldn't ever come here
    }

    public String getByteWord() throws CalculatorError
    {
        if (getBytes() == 8)       return "Byte";
        else if (getBytes() == 16) return "Word";
        else if (getBytes() == 32) return "DWord";
        else                       return "QWord";
    }

    /**
     * Tests whether a number is a decimal or not
     *
     * @param number the number to test
     * @return
     *
     * Fully tested
     */
    public boolean isDecimal(String number)
    {
        boolean answer = number.contains(".");
        LOGGER.info("isDecimal("+number.replaceAll("\n","")+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is positive
     *
     * @param number the value to test
     * @return either true or false based on result
     *
     * Fully tested
     */
    public boolean isPositiveNumber(String number)
    {
        boolean answer = !number.contains("-");
        LOGGER.info("isPositiveNumber("+number+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is negative
     *
     * @param number the value to test
     * @return
     *
     * Fully tested
     */
    public boolean isNegativeNumber(String number)
    {
        boolean answer = number.contains("-");
        LOGGER.info("isNegativeNumber("+number.replaceAll("\n", "")+") == " + answer);
        return answer;
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number the value to convert
     * @return
     *
     * Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.info("convertToNegative("+number+") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.debug("New: "  + number);
        numberIsNegative = true;
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     *
     * @param number the value to convert
     * @return
     *
     * Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.info("convertToPositive("+number+") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        LOGGER.debug("New: " + number);

        numberIsNegative = false;
        return number;
    }

    public String getTextAreaWithoutAnything()
    {
        return textArea.getText()
                .replaceAll("\n", "")
                .replace("+","")
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
    {
        return textArea.getText().replaceAll("\n", "");
    }

    public boolean isMemoryValuesEmpty()
    {
        boolean result = false;
        for(int i = 0; i < 10; i++)
        {
            if (StringUtils.isBlank(memoryValues[i]))
            {
                result = true;
            }
            else
            {
                result = false;
                break;
            }
        }
        return result;
    }

    @Deprecated(since = "use getCurrentPanel")
    public JPanel getCurrentPanelFromParentCalculator()
    {
        return this.currentPanel;
    }

    /**
     * Tests whether the TextArea contains a String which shows a previous error
     *
     * @return
     *
     * TODO: Test
     */
    public boolean textArea1ContainsBadText()
    {
        return getTextAreaWithoutAnything().equals("Invalid textarea") ||
        getTextAreaWithoutAnything().equals("Cannot divide by 0") ||
        getTextAreaWithoutAnything().equals("Not a Number") ||
        getTextAreaWithoutAnything().equals("Enter a Number") ||
        getTextAreaWithoutAnything().equals("Only positive numbers") ||
        getTextAreaWithoutAnything().contains("E");
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the image
     */
    public ImageIcon createImageIcon(String path) throws FileNotFoundException
    {
        //LOGGER.info("Inside createImageIcon(): creating image for " + description);
        ImageIcon retImageIcon;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) {
            retImageIcon = new ImageIcon(resource);
        }
        else {
            resource = classLoader.getResource(path.substring(19));
            if (resource != null) {
                retImageIcon = new ImageIcon(resource);
            } else {
                LOGGER.error("The path '" + path + "' could not find an image there after removing src/main/resources/!. Returning null!");
                throw new FileNotFoundException("The resource you are attempting to use cannot be found at: " + path);
            }

        }
        return retImageIcon;
    }

    /** Sets the image icons */
    public void setImageIcons()
    {
        try
        {
            setWindowsCalculator(createImageIcon("src/main/resources/images/windowsCalculator.jpg"));
            setMacLogo(createImageIcon("src/main/resources/images/solidBlackAppleLogo.jpg"));
            setWindowsLogo(createImageIcon("src/main/resources/images/windows11.jpg"));
            setBlankImage(null); // new ImageIcon()
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error(new CalculatorError("The image to set doesn't exist", e));
        }
    }

    public void setTextareaToValuesAtPosition(String buttonChoice)
    {
        textareaValue.append(values[valuesPosition]);
        LOGGER.debug("textAreaValue: '{}'", textareaValue);
        textareaValue.append(textareaValue).append(buttonChoice); // update textArea
        LOGGER.debug("textarea: '{}'", textareaValue);
        values[valuesPosition] = textareaValue.toString(); // store textarea
        textArea.setText("\n" + convertToPositive(textareaValue.toString()) + "-");
        LOGGER.debug("textArea: '" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    public void setValuesToTextAreaValue()
    {
        values[valuesPosition] = textareaValue.toString();
        if (values[valuesPosition].startsWith(".")) {
            values[valuesPosition] = values[valuesPosition].replace(".","") + ".";
        }
    }

    public Collection<JButton> getNumberButtons()
    {
        return new LinkedList<>() {{ add(button0); add(button1); add(button2); add(button3); add(button4);
                                     add(button5); add(button6); add(button7); add(button8); add(button9); }};
    }

    public Collection<JButton> getButtonClearEntryAndButtonDeleteAndButtonDot()
    {
        return Arrays.asList(buttonClearEntry, buttonDelete, buttonDot);
    }

    public void clearNumberButtonFunctionalities()
    {
        LOGGER.debug("Number buttons cleared of action listeners");
        getNumberButtons().forEach(button -> Arrays.stream(button.getActionListeners()).collect(Collectors.toList()).forEach(al -> {
            LOGGER.debug("Removing action listener from button: " + button.getName());
            button.removeActionListener(al);
        }));
    }

    public void logException(Exception e) { LOGGER.error(e.getClass().getName() + ": " + e.getMessage()); }

    public void performTasksWhenChangingJPanels(ActionEvent actionEvent)
    {
        LOGGER.info("Starting to changing jPanels");
        LOGGER.info("actionEvent: {}", actionEvent.getActionCommand());
        String oldPanelName = currentPanel.getClass().getSimpleName();
        LOGGER.info("oldPanel: {}", oldPanelName);
        String viewChoice = actionEvent.getActionCommand();
        if (currentPanel.getClass().getSimpleName().contains(viewChoice))
        {
            confirm("Not changing to " + currentPanel.getClass().getName()
                    + " when already showing " + currentPanel.getClass().getName());
            return;
        }
        else if (converterType != null && converterType.getName().equals(viewChoice))
        {
            confirm("Not changing panels when the conversion type is the same");
            return;
        }
        if (viewChoice.equals("Basic"))
        {
            BasicPanel basicPanel = new BasicPanel(this);
            updateJPanel(basicPanel);
        }
        else if (viewChoice.equals("Programmer"))
        {
            ProgrammerPanel programmerPanel = new ProgrammerPanel(this, null);
            updateJPanel(programmerPanel);
        }
        else if (viewChoice.equals("Scientific")) { LOGGER.warn("Setup"); }
        else if (viewChoice.equals("Date"))
        {
            DatePanel datePanel = new DatePanel(this, OPTIONS1);
            updateJPanel(datePanel);
        }
        else if (viewChoice.equals("Angle"))
        {
            ConverterPanel converterPanel = new ConverterPanel(this, ANGLE);
            updateJPanel(converterPanel);
        }
        else //if (viewChoice.equals("Area"))
        {
            ConverterPanel converterPanel = new ConverterPanel(this, AREA);
            updateJPanel(converterPanel);
        }
        LOGGER.info("newPanel: {}", currentPanel.getClass().getSimpleName());
        setTitle(viewChoice);
        pack();
        LOGGER.info("Finished changing jPanels\n");
        confirm("Switched from " + oldPanelName + " to " + currentPanel.getClass().getSimpleName());
    }

    public void performTasksWhenChangingJPanels(JPanel newPanel, CalculatorType calculatorType) throws CalculatorError
    {
        performTasksWhenChangingJPanels(newPanel, calculatorType, null);
    }

    public void performTasksWhenChangingJPanels(JPanel newPanel, CalculatorType calculatorType, ConverterType converterType) throws CalculatorError
    {
        LOGGER.info("Starting to changing jPanels");
        JPanel oldPanel = updateJPanel(newPanel);
        LOGGER.info("oldPanel: {}", oldPanel.getClass().getSimpleName());
        LOGGER.info("newPanel: {}", currentPanel.getClass().getSimpleName());
        // if oldPanel is same as newPanel, don't change
        if (oldPanel.getClass().getSimpleName().equals(currentPanel.getClass().getSimpleName()) ||
                (oldPanel.getClass().getSimpleName().equals("Panels.ConverterPanel") && newPanel.getClass().getSimpleName().equals("Panels.ConverterPanel")))
        {
            confirm("Not changing when "+oldPanel.getClass().getName()+" == "+currentPanel.getClass().getName());
        }
        else if (converterType == this.converterType)
        {
            confirm("Not changing panels when the conversion type is the same");
        }
        else if (currentPanel instanceof BasicPanel)
        {
            ((BasicPanel)currentPanel).performBasicCalculatorTypeSwitchOperations(this);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
            LOGGER.info("Finished changing jPanels");
            confirm("Switched from " + oldPanel.getClass().getSimpleName() + " to " + newPanel.getClass().getSimpleName());
        }
        else if (currentPanel instanceof ProgrammerPanel)
        {
            ((ProgrammerPanel)currentPanel).performProgrammerCalculatorTypeSwitchOperations(this, null);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
            LOGGER.info("Finished changing jPanels\n");
            confirm("Switched from " + oldPanel.getClass().getSimpleName() + " to " + newPanel.getClass().getSimpleName());
        }
        else if (currentPanel instanceof DatePanel)
        {
            ((DatePanel)currentPanel).performDateCalculatorTypeSwitchOperations(this, OPTIONS1);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
            LOGGER.info("Finished changing jPanels\n");
            confirm("Switched from " + oldPanel.getClass().getSimpleName() + " to " + newPanel.getClass().getSimpleName());
        }
        else // if (currentPanel instanceof ConverterPanel)
        {
            ((ConverterPanel)newPanel).performConverterCalculatorTypeSwitchOperations(this, converterType);
            setTitle(calculatorType.getName());
            setCurrentPanel(newPanel);
            pack();
            LOGGER.info("Finished changing jPanels\n");
            confirm("Switched from " + oldPanel.getClass().getSimpleName() + " to " + newPanel.getClass().getSimpleName());
        }
    }

    public Collection<JButton> getBasicOperationButtons()
    {
        return Arrays.asList(buttonAdd, buttonSubtract, buttonMultiply, buttonDivide);
    }

    /**
     * This method clears the functions off of + - * /
     */
    public void clearAllBasicOperationButtons()
    {
        getBasicOperationButtons().forEach(button ->
            Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener));
    }

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

    public void performAdditionButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Add Button actions");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!addBool && !subBool && !mulBool && !divBool &&
                    StringUtils.isNotBlank(textArea.getText()) && !textArea1ContainsBadText())
            {
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
                textareaValue.reverse();
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool && !values[1].equals("")) {
                addition();
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(calcType);
                subBool = resetOperator(subBool);
                addBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(calcType);
                mulBool = resetOperator(mulBool);
                addBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("")) {
                divide(calcType);
                divBool = resetOperator(divBool);
                addBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (textArea1ContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(getTextAreaWithoutAnything())) {
                LOGGER.warn("The user pushed plus but there is no number.");
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.error("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;

            if (calcType == PROGRAMMER && calcBase != DECIMAL && !textArea1ContainsBadText())
            {
                String number = "";
                if (calcBase == BINARY)
                {
                    number = getTextAreaWithoutAnything();
                    number = addZeroesToNumber(number);
                    // now update textArea with newly formatted binary number plus the operator
                    textArea.setText(addNewLineCharacters(3) + "+" + " " + number);
                    updateTextareaFromTextArea();
                }

                try { number = convertFromTypeToTypeOnValues(BINARY, DECIMAL, number);
                    LOGGER.info("number converted after add pushed: " + number);
                }
                catch (CalculatorError c) { logException(c); }
                values[0] = number;
            }
            confirm("Pressed: " + buttonChoice);
        }
    }

    public String addZeroesToNumber(String number)
    {
        int lengthOfNumber = number.length();
        int zeroesToAdd = 0;
        ((ProgrammerPanel)currentPanel).setTheBytesBasedOnTheNumbersLength(number);
        if (isButtonByteSet) { zeroesToAdd = 8 - lengthOfNumber; }
        else if (isButtonWordSet) { zeroesToAdd = 16 - lengthOfNumber; }
        else if (isButtonDWordSet) { zeroesToAdd = 32 - lengthOfNumber; }
        else /* (isButtonQWordSet) */ { zeroesToAdd = 64 - lengthOfNumber; }
        if (zeroesToAdd != 0) {
            LOGGER.debug("zeroesToAdd: " + zeroesToAdd);
            StringBuffer zeroes = new StringBuffer();
            for(int i=0; i<zeroesToAdd; i++) zeroes = zeroes.append("0");
            number = zeroes + number;
            LOGGER.info("After adding zeroes: " + number);
        } else {
            LOGGER.info("Not adding any zeroes. Number appropriate length.");
        }
        return number;
    }

    public void addition()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        //values[0] = Double.toString(result);
        if (isPositiveNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (isNegativeNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(values[0]));
            textareaValue = new StringBuffer(clearZeroesAndDecimalAtEnd(convertToPositive(values[0])));
            values[0] = convertToNegative(textareaValue.toString()).toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
            numberIsNegative = true;
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            values[0] = String.valueOf(result);
        }
        textareaValue = new StringBuffer().append(values[0]);
    }

    public void updateTheTextAreaBasedOnTheTypeAndBase()
    {
        LOGGER.info("Updating the textArea");
        if (calcType == BASIC)
        {
            textArea.setText(addNewLineCharacters(1) + values[valuesPosition]);
        }
        else if (calcType == PROGRAMMER)
        {
            if (calcBase == BINARY) {
                String convertedToBinary = "";
                try { convertedToBinary = convertFromTypeToTypeOnValues(DECIMAL, BINARY, values[0]); }
                catch (CalculatorError c) { logException(c); }
                textArea.setText(addNewLineCharacters(3) + convertedToBinary);
            }
            else if (calcBase == DECIMAL) {
                if (!numberIsNegative)
                    textArea.setText(addNewLineCharacters(3) + textareaValue);
                else {
                    textArea.setText(addNewLineCharacters(3) + convertToPositive(values[0]) + "-");
                    textareaValue = new StringBuffer("-"+values[valuesPosition]);
                    numberIsNegative = true;
                }
            }
            else if (calcBase == OCTAL) {
                LOGGER.warn("Setup");
            }
            else /* (calcBase == HEXADECIMAL) */ {
                LOGGER.warn("Setup");
            }
        }
        else {
            LOGGER.warn("Add more types here");
        }
    }

    public void performSubtractionButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Subtract Button actions");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!addBool && !subBool && !mulBool && !divBool &&
                    !textArea1ContainsBadText()) {
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
                updateTextareaFromTextArea();
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool && !values[1].equals("")) {
                addition();
                addBool = resetOperator(addBool);
                subBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(calcType);
                resetOperator(subBool); // subBool = resetOperator(subBool);
                subBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(calcType);
                mulBool = resetOperator(mulBool);
                subBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("")) {
                divide(calcType);
                divBool = resetOperator(divBool);
                subBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (textArea1ContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(getTextAreaWithoutAnything())) {
                LOGGER.warn("The user pushed subtract but there is no number.");
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
            }
            else if (addBool || subBool || mulBool || divBool) {
                LOGGER.info("already chose an operator. next number is negative...");
                negatePressed = true;
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;
            confirm("Pressed: " + buttonChoice);
        }
    }

    public void subtract()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        if (isPositiveNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result)); // textarea changed to whole number, or int
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (isNegativeNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            values[0] = convertToPositive(values[0]);
            values[0] = clearZeroesAndDecimalAtEnd(values[0]);
            values[0] = convertToNegative(values[0]);
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            values[0] = Double.toString(result);
        }
        textareaValue = new StringBuffer(values[0]);
    }

    @Deprecated(since = "No longer need to specify the type. Call default subtract")
    public void subtract(CalculatorType calculatorType)
    {

        if (calculatorType.equals(CalculatorType.BASIC))
        {
            subtract();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType.equals(CalculatorType.PROGRAMMER))
        {
            subtract();
            String operator = determineIfProgrammerPanelOperatorWasPushed();
            if (!operator.equals("")) textArea.setText(addNewLineCharacters(3) + operator + " " +values[0]);
            else textArea.setText(addNewLineCharacters(1) + textareaValue);
            updateTextareaFromTextArea();
            updateTextareaFromTextArea();
        }
    }

    public void performMultiplicationActions(ActionEvent action)
    {
        LOGGER.info("Performing Multiply Button actions");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + action.getActionCommand());
            if (!addBool && !subBool && !mulBool && !divBool && !textArea1ContainsBadText())
            {
                if (calcType == BASIC)textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
                updateTextareaFromTextArea();
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool && !values[1].equals("")) {
                addition();
                addBool = resetOperator(addBool);
                mulBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(calcType);
                subBool = resetOperator(subBool);
                mulBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(calcType);
                resetOperator(mulBool); // mulBool = resetOperator(mulBool);
                mulBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("")) {
                divide(calcType);
                divBool = resetOperator(divBool);
                mulBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (textArea1ContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(getTextAreaWithoutAnything())) {
                LOGGER.warn("The user pushed multiply but there is no number.");
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
            }
            else if (addBool || subBool || mulBool || divBool) { LOGGER.info("already chose an operator. choose another number."); }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            confirm("Pressed: " + buttonChoice);
        }
    }

    public void multiply()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        // new
        if (isPositiveNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            textareaValue = new StringBuffer(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
            values[0] = textareaValue.toString(); // textarea changed to whole number, or int
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (isNegativeNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(values[0]));
            textareaValue = new StringBuffer(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
            values[0] = convertToNegative(textareaValue.toString());
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            values[0] = String.valueOf(result);
        }


        // old
        if (result % 1 == 0 && !values[0].contains("E") && isPositiveNumber(values[0]))
        {
            values[0] = textareaValue.toString(); // update storing
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            values[0] = convertToPositive(values[0]);
            values[0] = clearZeroesAndDecimalAtEnd(values[0]);
            textArea.setText(addNewLineCharacters(1)+values[0]+"-");
            updateTextareaFromTextArea();
            values[0] = textareaValue.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (values[0].contains("E"))
        {
            textArea.setText(addNewLineCharacters(1) + values[0]);
            updateTextareaFromTextArea();
            values[0] = textareaValue.toString(); // update storing
        }
        else if (isNegativeNumber(values[0]))
        {
            textareaValue = new StringBuffer().append(convertToPositive(values[0]));
            textArea.setText(addNewLineCharacters(1) + textareaValue +"-");
            textareaValue = new StringBuffer().append(convertToNegative(values[0]));
        }
        else
        {// if double == double, keep decimal and number afterwards
            textArea.setText(addNewLineCharacters(1) + formatNumber(values[0]));
        }
    }

    public void multiply(CalculatorType calculatorType)
    {
        if (calculatorType.equals(CalculatorType.BASIC)) {
            multiply();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType.equals(CalculatorType.PROGRAMMER)) {
            //convertFromTypeToType("Binary", "Decimal");
            multiply();
            textArea.setText(addNewLineCharacters(3) + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performDivideButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Divide Button actions");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform division.";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!addBool && !subBool && !mulBool && !divBool && !textArea1ContainsBadText())
            {
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
                updateTextareaFromTextArea();
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool && !values[1].equals("")) {
                addition();
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(calcType);
                subBool = resetOperator(subBool);
                divBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(calcType);
                mulBool = resetOperator(mulBool);
                divBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("") & !values[1].equals("0")) {
                divide(calcType);
                resetOperator(divBool); // divBool = resetOperator(divBool)
                divBool = true;
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (textArea1ContainsBadText())  {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(getTextAreaWithoutAnything())) {
                LOGGER.warn("The user pushed divide but there is no number.");
                if (calcType == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (calcType == SCIENTIFIC) LOGGER.warn("SETUP");
            }
            else if (addBool || subBool || mulBool || divBool) { LOGGER.info("already chose an operator. choose another number."); }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            confirm("Pressed: " + buttonChoice);
        }
    }

    public void divide()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = 0.0;
        if (!values[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info(values[0] + " / " + values[1] + " = " + result);
            values[0] = Double.toString(result); // store result
        }
        else {
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = "0";
            firstNumBool = true;
        }

        if (isPositiveNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textarea changed to whole number, or int
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (isNegativeNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            values[0] = convertToPositive(values[0]);
            values[0] = clearZeroesAndDecimalAtEnd(values[0]);
            values[0] = convertToNegative(values[0]);
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            if (Double.parseDouble(values[0]) < 0.0 )
            {   // negative decimal
                values[0] = formatNumber(values[0]);
                LOGGER.info("textarea: '" + textareaValue + "'");
                textareaValue = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: '" + textareaValue + "'");
                textareaValue = new StringBuffer().append(textareaValue.substring(1, textareaValue.length()));
                LOGGER.info("textarea: '" + textareaValue + "'");
            }
            else
            {   // positive decimal
                values[0] = formatNumber(values[0]);
            }
        }
        textareaValue = new StringBuffer(values[0]);
    }

    public void divide(CalculatorType calculatorType)
    {
        if (calculatorType.equals(CalculatorType.BASIC)) {
            divide();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType.equals(CalculatorType.PROGRAMMER))
        {
//            convertFromTypeToType("Binary", "Decimal");
            divide();
            textArea.setText(addNewLineCharacters(3) + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performButtonEqualsActions()
    {
        LOGGER.info("Performing Equal Button actions");
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calcType == BASIC)
        {
            determineAndPerformBasicCalculatorOperation();
            String operator = determineIfBasicPanelOperatorWasPushed();
            if (!operator.equals("")) textArea.setText(addNewLineCharacters(1) + operator + " " + textareaValue);
            else textArea.setText(addNewLineCharacters(1) + textareaValue);
        }
        else if (calcType == PROGRAMMER)
        {
            // Get the current number first. save
            String numberInTextArea = getTextAreaWithoutAnything();
            if (((ProgrammerPanel)currentPanel).buttonBin.isSelected())
            {
                try { values[1] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
                catch (CalculatorError c) { logException(c); }
                LOGGER.info("Values[1] saved to {}", values[1]);
                LOGGER.info("Now performing operation...");
                determineAndPerformBasicCalculatorOperation();
            }
            else if (((ProgrammerPanel)currentPanel).buttonOct.isSelected())
            {
                try { values[1] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
                catch (CalculatorError c) { logException(c); }
            }
            else if (((ProgrammerPanel)currentPanel).buttonDec.isSelected())
            {
                determineAndPerformBasicCalculatorOperation();
            }
            else if (((ProgrammerPanel)currentPanel).buttonHex.isSelected())
            {
                values[0] = "";
                try { values[0] = convertFromTypeToTypeOnValues(HEXADECIMAL, DECIMAL, values[0]); }
                catch (CalculatorError c) { logException(c); }
                values[1] = "";
                try { values[0] = convertFromTypeToTypeOnValues(HEXADECIMAL, DECIMAL, values[1]); }
                catch (CalculatorError c) { logException(c); }
            }

            if (orButtonBool)
            {
                ((ProgrammerPanel)currentPanel).performOr();
                textArea.setText(addNewLineCharacters(1) + values[0]);

            }
            else if (modButtonBool)
            {
                LOGGER.info("Modulus result");
                ((ProgrammerPanel)currentPanel).performModulus();
                // update values and textArea accordingly
                valuesPosition = 0;
                modButtonBool = false;
            }
            updateTheTextAreaBasedOnTheTypeAndBase();
        }
        if (values[0].equals("") && values[1].equals(""))
        {
            // if temp[0] and temp[1] do not have a number
            valuesPosition = 0;
        }
        else if (textArea1ContainsBadText())
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

    public void determineAndPerformBasicCalculatorOperation()
    {
        if (addBool) {
            addition();
            addBool = resetOperator(addBool);
        }
        else if (subBool){
            subtract();
            subBool = resetOperator(subBool);
        }
        else if (mulBool){
            multiply(calcType);
            mulBool = resetOperator(mulBool);
        }
        else if (divBool){
            divide(calcType);
            divBool = resetOperator(divBool);
        }
    }

    public void performNegateButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Negate Button actions");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot negate number. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            textArea.setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
            updateTextareaFromTextArea();
            if (!textareaValue.toString().equals("")) {
                if (numberIsNegative) {
                    textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString()));
                    LOGGER.debug("textarea: " + textareaValue);
                    if (calcType == BASIC) textArea.setText(addNewLineCharacters(1)+ textareaValue);
                    else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3)+ textareaValue);
                }
                else {
                    if (calcType == BASIC) textArea.setText(addNewLineCharacters(1)+ textareaValue +"-");
                    else if (calcType == PROGRAMMER) textArea.setText(addNewLineCharacters(3)+ textareaValue +"-");

                    textareaValue = new StringBuffer().append(convertToNegative(textareaValue.toString()));
                    LOGGER.debug("textarea: " + textareaValue);
                }
            }
            values[valuesPosition] = textareaValue.toString();
            confirm("");
        }
    }

    public String formatNumber(String num)
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
        number = Double.parseDouble(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }

    /**
     * This method updates the JPanel to the one sent in
     * and returns the oldPanel
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
     * This method returns true or false depending on if an operator was pushed or not
     * @return the result
     */
    @Deprecated(since = "Use String return method")
    public boolean determineIfMainOperatorWasPushedBoolean()
    {
        return addBool || subBool || mulBool || divBool;
    }

    public String determineIfBasicPanelOperatorWasPushed()
    {
        String results = "";
        if (addBool) { results = "+"; }
        else if (subBool) { results = "-"; }
        else if (mulBool) { results = "*"; }
        else if (divBool) { results = "/"; }
        LOGGER.info("operator: " + (results.equals("") ? "no basic operator pushed" : results));
        return results;
    }

    public String determineIfProgrammerPanelOperatorWasPushed()
    {
        String results = "";
        // what operations can be pushed: MOD, OR, XOR, NOT, AND
        if (modButtonBool) { results = "MOD"; }
        else if (orButtonBool) { results = "OR"; }
        else if (xorButtonBool) { results = "XOR"; }
        else if (notButtonBool) { results = "NOT"; }
        else if (andButtonBool) { results = "AND"; }
        LOGGER.info("operator: " + (results.equals("") ? "no programmer operator pushed" : results));
        return results;
    }

    /**
     * This method converts the memory values
     */
    public void convertMemoryValues()
    {
        String[] newMemoryValues = new String[10];
        int i = 0;
        if (calcType == CalculatorType.PROGRAMMER)
        {
            for(String numberInMemory : memoryValues)
            {
                newMemoryValues[i] = "";
                try { newMemoryValues[i] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInMemory); }
                catch (CalculatorError c) { logException(c); }
                LOGGER.debug("new number in memory is: " + newMemoryValues[i]);
                i++;
            }
        }
    }

    public String convertFromTypeToTypeOnValues(CalculatorBase fromType, CalculatorBase toType, String strings) throws CalculatorError
    {
        LOGGER.debug("convert from {} to {}", fromType, toType);
        LOGGER.debug("on value: {}", strings);
        StringBuffer sb = new StringBuffer();
        if (strings.contains(" ")) {
            String[] strs = strings.split(" ");
            for(String s : strs) {
                sb.append(s); }
        } else { sb.append(strings); }
        LOGGER.info("sb: " + sb);
        String convertedValue = "";
        if (StringUtils.isEmpty(strings)) return "";
        // All from HEXADECIMAL to any other option
        if (fromType == HEXADECIMAL && toType == DECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == HEXADECIMAL && toType == OCTAL) { confirm("IMPLEMENT"); }
        else if (fromType == HEXADECIMAL && toType == BINARY) { confirm("IMPLEMENT"); }
        // All from DECIMAL to any other option
        else if (fromType == DECIMAL && toType == HEXADECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == DECIMAL && toType == OCTAL) { confirm("IMPLEMENT"); }
        else if (fromType == DECIMAL && toType == BINARY)
        {
            LOGGER.debug("Converting str("+sb+")");
            sb = new StringBuffer();
            int number;
            try {
                number = Integer.parseInt(strings);
                LOGGER.debug("number: " + number);
                int i = 0;
                if (number == 0) sb.append("00000000");
                else {
                    while (i < getBytes()) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (sb.length() == 8) sb.append(" ");
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for(int k = i; k<getBytes(); k++) {
                                sb.append("0");
                                if (k == 7) sb.append(" ");
                            }
                            break;
                        }
                        else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for(int k = i+1; k<getBytes(); k++) {
                                sb.append("0");
                                if (k == 7) sb.append(" ");
                            } break;
                        }
                        i++;
                        number /= 2;
                    }
                }
            }
            catch (NumberFormatException nfe) { LOGGER.error(nfe.getMessage()); }
            // Determine bytes and add zeroes if needed
            int sizeOfSecond8Bits;
            try {
                // start counting at 9. The 8th bit is a space
                sizeOfSecond8Bits = sb.toString().substring(10).length();
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
            LOGGER.debug("convertFrom("+fromType+")To("+toType+") = "+ sb);
            LOGGER.warn("ADD CODE THAT MAKES SURE RETURNED VALUE UPDATES BYTES");
            LOGGER.warn("IF AFTER REVERSE IS LONGER THAN 8 BITS, WE NEED TO ADD");
            LOGGER.warn("A SPACE BETWEEN THE BYTES");
            convertedValue = strToReturn;
        }
        // All from OCTAL to any other option
        else if (fromType == OCTAL && toType == HEXADECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == OCTAL && toType == DECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == OCTAL && toType == BINARY) { confirm("IMPLEMENT"); }
        // All from BINARY to any other option
        else if (fromType == BINARY && toType == HEXADECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == BINARY && toType == DECIMAL)
        {
            LOGGER.debug("Converting str("+sb+")");

            int appropriateLength = getBytes();
            LOGGER.debug("sb: " + sb);
            LOGGER.debug("appropriateLength: " + appropriateLength);

            if (sb.length() != appropriateLength)
            {
                LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                // user had entered 101, which really is 00000101, but they aren't showing the first 5 zeroes
                int difference = appropriateLength - sb.length();
                StringBuffer missingZeroes = new StringBuffer();
                missingZeroes.append("0".repeat(Math.max(0, difference)));
                missingZeroes.append(sb);
                sb = new StringBuffer(missingZeroes);
                LOGGER.debug("sb: " + sb);
            }

            double result = 0.0;
            double num1;
            double num2;
            for(int i=0, k=appropriateLength-1; i<appropriateLength; i++, k--)
            {
                num1 = Double.valueOf(String.valueOf(sb.charAt(i)));
                num2 = Math.pow(2,k);
                result = (num1 * num2) + result;
            }

            convertedValue = String.valueOf(Double.valueOf(result));


            if (isPositiveNumber(convertedValue))
            {
                convertedValue = String.valueOf(clearZeroesAndDecimalAtEnd(convertedValue));
            }
            LOGGER.debug("convertedValue: {}", convertedValue);
        }
        else if (fromType == BINARY && toType == OCTAL) { confirm("IMPLEMENT"); }

        LOGGER.info("base set to: " + calcBase + addNewLineCharacters(1));
        return convertedValue;
    }

    public void performOrLogic(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<values[0].length(); i++) {
            String letter;
            if (String.valueOf(values[0].charAt(i)).equals("0") &&
                    String.valueOf(values[1].charAt(i)).equals("0") )
            { letter = "0"; } // if the characters at both values at the same position are the same and equal 0
            else
            { letter = "1"; }
            sb.append(letter);
            LOGGER.info(values[0].charAt(i) + " + " + values[1].charAt(i) +" = " + letter);
        }
        values[0] = String.valueOf(sb);
        textArea.setText(addNewLineCharacters(1)+values[0]);
        orButtonBool = false;
        valuesPosition = 0;
    }

    public void performCopyFunctionality(ActionEvent ae)
    {
        values[2] = getTextAreaWithoutNewLineCharactersOrWhiteSpace();
        textareaValue = new StringBuffer().append(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        confirm("Pressed Copy");
    }

    public void performPasteFunctionality(ActionEvent ae)
    {
        if (StringUtils.isEmpty(values[2]) && StringUtils.isBlank(values[2]))
            LOGGER.info("Values[2] is empty and blank");
        else
            LOGGER.info("Values[2]: " + values[2]);
        textArea.setText(addNewLineCharacters(1) + values[2]); // to paste
        values[valuesPosition] = getTextAreaWithoutNewLineCharactersOrWhiteSpace();
        textareaValue = new StringBuffer().append(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        confirm("Pressed Paste");
    }

    public String getHelpString()
    {
        String COPYRIGHT = "\u00a9";
        return "<html>Apple MacBook Air "
                + "Version 4<br>"
                + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
                + "Mac OS mojave and its user interface are protected by trademark and all other<br>"
                + "pending or existing intellectual property right in the United States and other<br>"
                + "countries/regions."
                + "<br><br><br>"
                + "This product is licensed under the License Terms to:<br>"
                + "Michael Ball</html>";
    }

    @Deprecated(since = "performed by the panel")
    public void performViewHelpFunctionality(ActionEvent ae)
    {
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        textLabel = new JLabel(getHelpString(),
                macLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(Calculator.this,
                mainPanel, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
    }

    public void performAboutCalculatorFunctionality(ActionEvent ae)
    {
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        textLabel = new JLabel(getHelpString(),
                macLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(Calculator.this,
                mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);

    }

    /************* All Setters ******************/
    public void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    public void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setTextArea(JTextArea textArea) { this.textArea = textArea; }
    public void setTextareaValue(StringBuffer textareaValue) { this.textareaValue = textareaValue; }
    public void setCalculatorType(CalculatorType calcType) { this.calcType = calcType; }
    public void setConverterType(ConverterType converterType) { this.converterType = converterType;}
    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setWindowsCalculator(ImageIcon windowsCalculator) { this.windowsCalculator = windowsCalculator; }
    public void setMacLogo(ImageIcon macLogo) { this.macLogo = macLogo; }
    public void setWindowsLogo(ImageIcon windowsLogo) { this.windowsLogo = windowsLogo; }
    public void setBlankImage(ImageIcon blankImage) { this.blankImage = blankImage; }
    public void setFirstNumBool(boolean firstNumBool) { this.firstNumBool = firstNumBool; }
    public void setNumberIsNegative(boolean numberIsNegative) { this.numberIsNegative = numberIsNegative; }
    public void setAddBool(boolean addBool) { this.addBool = addBool; }
    public void setSubBool(boolean subBool) { this.subBool = subBool; }
    public void setMulBool(boolean mulBool) { this.mulBool = mulBool; }
    public void setDivBool(boolean divBool) { this.divBool = divBool; }
    public void setDotButtonPressed(boolean dotButtonPressed) { this.dotButtonPressed = dotButtonPressed; }
    public void setButtonByte(boolean buttonByteSet) { isButtonByteSet = buttonByteSet; }
    public void setButtonWord(boolean buttonWordSet) { isButtonWordSet = buttonWordSet; }
    public void setButtonDWord(boolean buttonDwordSet) { isButtonDWordSet = buttonDwordSet; }
    public void setButtonQWord(boolean buttonQWordSet) { isButtonQWordSet = buttonQWordSet; }
    public void setOrButtonBool(boolean orButtonBool) { this.orButtonBool = orButtonBool; }
    public void setModButtonBool(boolean modButtonBool) { this.modButtonBool = modButtonBool; }
    public void setXorButtonBool(boolean xorButtonBool) { this.xorButtonBool = xorButtonBool; }
    public void setNotButtonBool(boolean notButtonBool) { this.notButtonBool = notButtonBool; }
    public void setAndButtonBool(boolean andButtonBool) { this.andButtonBool = andButtonBool; }
    public void setCalculatorBase(CalculatorBase calcBase) { this.calcBase = calcBase; }
    public void setBar(JMenuBar bar) { this.bar = bar; }
}