package Calculators;

import Panels.*;
import Runnables.CalculatorMain;
import Types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.prefs.Preferences;

import static Types.CalculatorByte.*;
//import static Types.Texts.*;
import static Types.Texts.*;
import static Types.CalculatorView.*;
import static Types.CalculatorBase.*;
import static Types.CalculatorConverterType.*;
import static Types.DateOperation.*;

import static Utilities.LoggingUtil.*;

/**
 * Calculator
 * <p>
 * The calculator class extends JFrame and creates
 * a GUI container to display a calculator application.
 * This class contains the main components that are used
 * throughout the calculator, such as the menu bar and
 * the options, the main buttons such as the numbers,
 * and common methods used by multiple panels.
 * The Calculator stores the users input in a String[]
 * called values. It stores 4 values and is used directly
 * with valuesPosition to determine which value is current.
 * The Calculator also stores memory values in a String[]
 * called memoryValues. It stores 10 values and is used
 * directly with memoryPosition to determine which memory
 * value to store the next memory value in.
 * The CalculatorView is used primarily to determine which
 * panel is currently being displayed but the currentPanel
 * JPanel is also used for this purpose.
 * Any specific function that is unique is handled within
 * that panel.
 *
 * @author Michael Ball
 * @version since 4.0
 */
public class Calculator extends JFrame
{
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    public static final Font
            mainFont = new Font(SEGOE_UI, Font.PLAIN, 12), // all panels
            mainFontBold = new Font(SEGOE_UI, Font.BOLD, 12), // Date panel
            verdanaFontBold = new Font(VERDANA, Font.BOLD, 20); // Converter, Date panels
    public static final Color
            MOTIF_GRAY = new Color(174,178,195); // Motif look
    // Menubar
    private JMenuBar menuBar;
    // Menubar Items
    private JMenu lookMenu, viewMenu, editMenu, helpMenu;
    // Text Pane
    private JTextPane textPane, historyTextPane;
    // Buttons used by multiple Panels
    private final JButton
            button0 = new JButton(ZERO), button1 = new JButton(ONE),
            button2 = new JButton(TWO), button3 = new JButton(THREE),
            button4 = new JButton(FOUR), button5 = new JButton(FIVE),
            button6 = new JButton(SIX), button7 = new JButton(SEVEN),
            button8 = new JButton(EIGHT), button9 = new JButton(NINE),
            buttonClear = new JButton(CLEAR), buttonClearEntry = new JButton(CLEAR_ENTRY),
            buttonDelete = new JButton(DELETE), buttonDecimal = new JButton(DECIMAL),
            buttonFraction = new JButton(FRACTION), buttonPercent = new JButton(PERCENT),
            buttonSquareRoot = new JButton(SQUARE_ROOT), buttonMemoryClear = new JButton(MEMORY_CLEAR),
            buttonMemoryRecall = new JButton(MEMORY_RECALL), buttonMemoryStore = new JButton(MEMORY_STORE),
            buttonMemoryAddition = new JButton(MEMORY_ADDITION), buttonMemorySubtraction = new JButton(MEMORY_SUBTRACTION),
            buttonHistory = new JButton(HISTORY_CLOSED), buttonSquared = new JButton(SQUARED),
            buttonAdd = new JButton(ADDITION), buttonSubtract = new JButton(SUBTRACTION),
            buttonMultiply = new JButton(MULTIPLICATION), buttonDivide = new JButton(DIVISION),
            buttonEquals = new JButton(EQUALS), buttonNegate = new JButton(NEGATE),
            // Used in programmer and converter... until an official button is determined
            buttonBlank1 = new JButton(BLANK), buttonBlank2 = new JButton(BLANK);
    // Preferences
    private Preferences preferences;
    // Values used to store inputs. MemoryValues used for storing memory values.
    protected String[]
            values = new String[]{BLANK,BLANK,BLANK,BLANK}, // firstNum or total, secondNum, copy, temporary storage
            memoryValues = new String[]{BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK,BLANK}; // stores memory values; rolls over after 10 entries
    // Position of values, memoryValues, and memoryRecall
    protected int valuesPosition = 0, memoryPosition = 0, memoryRecallPosition = 0;
    // Current view, base, byte, date operation, converter type, and panel (view)
    private CalculatorView calculatorView;
    private CalculatorBase calculatorBase, previousBase;
    private CalculatorByte calculatorByte;
    private DateOperation dateOperation;
    private CalculatorConverterType converterType;
    private final BasicPanel basicPanel;
    private final ProgrammerPanel programmerPanel;
    private final ScientificPanel scientificPanel;
    private final DatePanel datePanel;
    private final ConverterPanel converterPanel;
    private JPanel currentPanel, historyPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    // Flags
    private boolean
            isFirstNumber = true,
            isNumberNegative, // used to determine is current value is negative or not
            //isNegating = false,
            // main operators
            isAdding, isSubtracting, isMultiplying, isDividing, isPemdasActive,
            // look options
            isMetal, isSystem, isWindows, isMotif, isGtk, isApple;
    private String helpString = BLANK;

    /* Constructors */
    /**
     * Starts the calculator with the BASIC CalculatorView
     *
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_BASIC, BASE_DECIMAL, null, null, null); }

    /**
     * Starts the Calculator with a specific CalculatorView
     * but defaults to BASE_DECIMAL
     *
     * @param calculatorView the type of Calculator to create
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(calculatorView, null, null, null, null); }

    /**
     * Starts the calculator with the PROGRAMMER CalculatorView
     * with a specified CalculatorBase
     *
     * @param calculatorBase the base to set that Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorBase calculatorBase, CalculatorByte calculatorByte) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_PROGRAMMER, calculatorBase, calculatorByte, null, null); }

    /**
     * This constructor is used to create a DATE Calculator with a specific DateOperation
     *
     * @param dateOperation  the option to open the DateCalculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_DATE, null, null, null, dateOperation); }

    /**
     * This constructor is used to create a Converter Calculator starting with a specific CalculatorConverterType
     *
     * @param converterType  the type of unit to start the Converter Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_CONVERTER, null, null, converterType, null); }

    /**
     * MAIN CONSTRUCTOR USED
     *
     * @param calculatorView the type of Calculator to create
     * @param converterType  the type of Converter to use
     * @param calculatorBase the base to set that Calculator in
     * @param calculatorByte the byte size to set that Calculator in
     * @param dateOperation  the type of DateOperation to start with
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView, CalculatorBase calculatorBase,
                      CalculatorByte calculatorByte, CalculatorConverterType converterType,
                      DateOperation dateOperation)
            throws CalculatorError, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calculatorView.getValue());
        setCalculatorView(calculatorView);
        setCalculatorBase(calculatorBase == null ? BASE_DECIMAL : calculatorBase);
        setCalculatorByte(calculatorByte == null ? BYTE_BYTE : calculatorByte);
        setConverterType(converterType == null ? AREA : converterType);
        setDateOperation(dateOperation == null ? DIFFERENCE_BETWEEN_DATES : dateOperation);
        this.basicPanel = new BasicPanel();
        this.programmerPanel = new ProgrammerPanel();
        this.scientificPanel = new ScientificPanel();
        this.datePanel = new DatePanel();
        this.converterPanel = new ConverterPanel();
        setCurrentPanel(switch (calculatorView)
        {
            case VIEW_BASIC -> basicPanel;
            case VIEW_PROGRAMMER -> programmerPanel;
            case VIEW_SCIENTIFIC -> scientificPanel;
            case VIEW_DATE -> datePanel;
            case VIEW_CONVERTER -> converterPanel;
        });
        setupPreferences(); // must happen before menubar
        configureMenuBar();
        setupPanel(null);
        setupCalculatorImages();
        setMinimumSize(currentPanel.getSize());
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addKeyListener(new CalculatorKeyListener(this)); // controls keyboard input
        addMouseListener(new CalculatorMouseListener(this)); // controls mouse input
        add(currentPanel);
        LOGGER.debug("Finished constructing the calculator");
    }

    /**************** CONFIGURATION ****************/
    /**
     * Configures the menu options on the bar
     */
    private void configureMenuBar()
    {
        setCalculatorMenuBar(new JMenuBar());
        setupLookMenu();
        setupViewMenu();
        setupEditMenu();
        setupHelpMenu();
        LOGGER.debug("Menu Bar options configured");
    }

    /**
     * Experimental preferences.
     */
    protected void setupPreferences()
    {
        // Sets the preferences node for this package
        preferences = Preferences.userNodeForPackage(Calculator.class);
    }

    /**
     * Loads the preferences from storage
     * Use get, getInt, getBoolean
     */
    private void loadPreferences()
    {
        // Load preferences from storage
        // used as a place holder. when you want to load a preference,
        // copy and place this method here where you want to load that preference
        String look = preferences.get(LOOK, "");

        // Use the loaded preferences to initialize your Swing components
        // ...
    }

    /**
     * Saves the preferences to storage
     * Use put, putInt, putBoolean
     */
    private void savePreferences()
    {
        // Save preferences to storage
        // used as a place holder. when you want to save a preference,
        // copy and place this method here where you want to save that preference
        if (isMetal) preferences.put(LOOK, METAL);
        else if (isSystem) preferences.put(LOOK, SYSTEM);
        else if (isWindows) preferences.put(LOOK, WINDOWS);
        else if (isMotif) preferences.put(LOOK, MOTIF);
        else if (isGtk) preferences.put(LOOK, GTK);
        else if (isApple) preferences.put(LOOK, APPLE);
        else preferences.put(LOOK, "");
    }

    /**
     * Sets the images used in the Calculator
     */
    private void setupCalculatorImages()
    {
        LOGGER.debug("Creating images...");
        setImageIcons();
        LOGGER.debug("Images created");
        final Taskbar taskbar = Taskbar.getTaskbar();
        // Set the icon we see when we run the GUI to not see the jar icon
        taskbar.setIconImage(calculatorIcon.getImage());
        setIconImage(calculatorIcon.getImage());
        LOGGER.debug("Taskbar icon set");
    }

    /**
     * Sets the image icons
     */
    private void setImageIcons()
    {
        LOGGER.debug("Configuring imageIcons...");
        setBlankIcon(null);
        ImageIcon calculatorIcon = createImageIcon("src/main/resources/images/windowsCalculator.jpg");
        if (null == calculatorIcon)
            logException(new CalculatorError("The icon you are attempting to use cannot be found: calculatorIcon"));
        setCalculatorIcon(calculatorIcon);
        ImageIcon macIcon = createImageIcon("src/main/resources/images/solidBlackAppleLogo.jpg");
        if (null == macIcon)
            logException(new CalculatorError("The icon you are attempting to use cannot be found: macIcon"));
        setMacIcon(macIcon);
        ImageIcon windowsIcon = createImageIcon("src/main/resources/images/windows11.jpg");
        if (null == windowsIcon)
            logException(new CalculatorError("The icon you are attempting to use cannot be found: windowsIcon"));
        setWindowsIcon(windowsIcon);
        LOGGER.debug("ImageIcons configured");
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path the path of the image
     */
    private ImageIcon createImageIcon(String path)
    {
        LOGGER.debug("Creating imageIcon using path: " + path);
        ImageIcon imageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) {
            imageIcon = new ImageIcon(resource);
            LOGGER.debug("ImageIcon created");
        }
        else {
            LOGGER.error("Could not find an image using path: '{}'!", path);
            LOGGER.error("ImageIcon not created. Returning null");
        }
        return imageIcon;
    }

    /**
     * The main method to set up the textPane
     */
    public void setupTextPane()
    {
        LOGGER.debug("Configuring textPane...");
        setTextPane(new JTextPane());
        if (currentPanel instanceof BasicPanel)
        {
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            textPane.setParagraphAttributes(attribs, false);
            textPane.setPreferredSize(new Dimension(70, 30));
        }
        else if (currentPanel instanceof ProgrammerPanel)
        {
            String[] initString = {
                    programmerPanel.displayByteAndBase() + addNewLines(1),
                    values[valuesPosition] + addNewLines(1)
            };
            String[] initStyles = {"regular", "regular"};
            String[] alignmentStyles = {"alignLeft", "alignRight"};
            // TODO: Remove local object declaration and use global variable textPane on line below
            //JTextPane textPane = new JTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            // Styles
            Style regular = doc.addStyle("regular", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
            StyleConstants.setFontFamily(regular, mainFont.getFamily());
            StyleConstants.setFontSize(regular, mainFont.getSize()); // ensure same size as mainFont
            // Add alignment styles (apply font explicitly so inheritance isn't required)
            Style alignLeft = doc.addStyle("alignLeft", regular);
            StyleConstants.setAlignment(alignLeft, StyleConstants.ALIGN_LEFT);
            StyleConstants.setFontFamily(alignLeft, mainFont.getFamily());
            StyleConstants.setFontSize(alignLeft, mainFont.getSize());

            Style alignRight = doc.addStyle("alignRight", regular);
            StyleConstants.setAlignment(alignRight, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setFontFamily(alignRight, mainFont.getFamily());
            StyleConstants.setFontSize(alignRight, mainFont.getSize());

            try {
                for (int i = 0; i < initString.length; i++) {
                    String styleName = alignmentStyles[i % alignmentStyles.length];
                    Style styleToUse = doc.getStyle(styleName);
                    LOGGER.debug("Style @ {}: {} for string: {}", i, styleName, initString[i]);
                    // Insert using the same AttributeSet and then set paragraph attributes using that same AttributeSet
                    doc.insertString(doc.getLength(), initString[i], styleToUse);
                    doc.setParagraphAttributes(doc.getLength() - initString[i].length(),
                            initString[i].length(),
                            styleToUse,
                            false);
                }
            } catch (Exception e) {
                logException(e);
            }
            textPane.setPreferredSize(new Dimension(100, 120));
        }
        if (isMotif)
        {
            textPane.setBackground(MOTIF_GRAY);
            textPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        {
            textPane.setBackground(Color.WHITE);
            textPane.setBorder(new LineBorder(Color.BLACK));
        }
        textPane.setEditable(false);
        //textPane.setEnabled(false);
        textPane.setFont(mainFont);
        LOGGER.debug("TextPane configured");
    }

    /**
     * The main method to set up the History Panel
     */
    public void setupHistoryTextPane()
    {
        LOGGER.debug("Configuring HistoryTextPane...");
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        if (calculatorView == VIEW_BASIC)
        {
            basicPanel.setHistoryTextPane(new JTextPane());
            basicPanel.getHistoryTextPane().setParagraphAttributes(attribs, true);
            basicPanel.getHistoryTextPane().setFont(mainFont);
            if (isMotif)
            {
                basicPanel.getHistoryTextPane().setBackground(MOTIF_GRAY);
                basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
            }
            else
            {
                basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
            }
            basicPanel.getHistoryTextPane().setEditable(false);
            basicPanel.getHistoryTextPane().setSize(new Dimension(70, 200)); // sets size at start
            basicPanel.getHistoryTextPane().setMinimumSize(basicPanel.getHistoryTextPane().getSize()); // keeps size throughout
            LOGGER.debug("BasicHistoryTextPane configured");
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            programmerPanel.setProgrammerHistoryTextPane(new JTextPane());
            programmerPanel.getHistoryTextPane().setParagraphAttributes(attribs, true);
            programmerPanel.getHistoryTextPane().setFont(mainFont);
            if (isMotif)
            {
                programmerPanel.getHistoryTextPane().setBackground(MOTIF_GRAY);
                programmerPanel.getHistoryTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
            }
            else
            {
                programmerPanel.getHistoryTextPane().setBackground(Color.WHITE);
                programmerPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
            }
            programmerPanel.getHistoryTextPane().setEditable(false);
            programmerPanel.getHistoryTextPane().setSize(new Dimension(70, 205)); // sets size at start
            programmerPanel.getHistoryTextPane().setMinimumSize(programmerPanel.getHistoryTextPane().getSize()); // keeps size throughout
            LOGGER.debug("ProgrammerHistoryTextPane configured");
        }
        else
        {
            LOGGER.warn("No history panel has been set up for the panel:{}", currentPanel.getName());
        }
    }

    /**
     * Configures the buttons used on the Basic Panel
     */
    public void setupBasicPanelButtons()
    {
        LOGGER.debug("Configuring Basic Panel buttons...");
        setupNumberButtons();
        LOGGER.debug("Number buttons configured for Basic Panel");

        setupMemoryButtons();
        setupCommonButtons();
    }

    /**
     * Configures the buttons used on the Programmer Panel
     */
    public void setupProgrammerPanelButtons()
    {
        LOGGER.debug("Configuring Programmer Panel buttons...");
        setupNumberButtons();
        LOGGER.debug("Number buttons configured for Programmer Panel");

        setupButtonBlank1();
        setupMemoryButtons(); // MR MC MS M+ M- H
        setupCommonButtons(); // common

        programmerPanel.setupProgrammerPanelButtons();
    }

    /**
     * Configures the buttons used on the Converter Panel
     */
    public void setupConverterPanelButtons()
    {
        LOGGER.debug("Configuring Converter Panel buttons...");
        setupNumberButtons();
        LOGGER.debug("Number buttons configured for Converter Panel");

        Arrays.asList(buttonBlank1, buttonBlank2, buttonClearEntry, buttonDelete, buttonDecimal).forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonBlank1.setName(BLANK);
        LOGGER.debug("Blank button 1 configured");

        buttonBlank2.setName(BLANK);
        LOGGER.debug("Blank button 2 configured");

        buttonClearEntry.setName(CLEAR_ENTRY);
        if (calculatorView == VIEW_CONVERTER)
        { buttonClearEntry.addActionListener(converterPanel::performClearEntryButtonActions); }
        LOGGER.debug("ClearEntry button configured");

        buttonDelete.setName(DELETE);
        if (calculatorView == VIEW_CONVERTER)
        { buttonDelete.addActionListener(converterPanel::performDeleteButtonActions); }
        LOGGER.debug("Delete button configured");

        buttonDecimal.setName(DECIMAL);
        if (calculatorView == VIEW_CONVERTER)
        { buttonDecimal.addActionListener(converterPanel::performDecimalButtonActions); }
        LOGGER.debug("Decimal button configured");

        LOGGER.debug("Converter Panel buttons configured");
    }

    /**
     * The main method to set up the Memory buttons. This
     * also includes the History button since it is included
     * in the panel that renders it
     */
    public void setupMemoryButtons()
    {
        LOGGER.debug("Configuring Memory buttons...");
        getAllMemoryPanelButtons().forEach(memoryPanelButton -> {
            memoryPanelButton.setFont(mainFont);
            memoryPanelButton.setPreferredSize(new Dimension(30, 30));
            memoryPanelButton.setBorder(new LineBorder(Color.BLACK));
            memoryPanelButton.setEnabled(false);
        });
        buttonMemoryClear.setName(MEMORY_CLEAR);
        buttonMemoryClear.addActionListener(this::performMemoryClearAction);
        buttonMemoryRecall.setName(MEMORY_RECALL);
        buttonMemoryRecall.addActionListener(this::performMemoryRecallAction);
        buttonMemoryAddition.setName(MEMORY_ADDITION);
        buttonMemoryAddition.addActionListener(this::performMemoryAdditionAction);
        buttonMemorySubtraction.setName(MEMORY_SUBTRACTION);
        buttonMemorySubtraction.addActionListener(this::performMemorySubtractionAction);
        buttonMemoryStore.setEnabled(true); // Enable memoryStore
        buttonMemoryStore.setName(MEMORY_STORE);
        buttonMemoryStore.addActionListener(this::performMemoryStoreAction);
        buttonHistory.setEnabled(true);
        //reset buttons to enabled if memories are saved
        if (!memoryValues[0].isEmpty())
        {
            buttonMemoryClear.setEnabled(true);
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryAddition.setEnabled(true);
            buttonMemorySubtraction.setEnabled(true);
        }
        LOGGER.debug("Memory buttons configured");
        if (calculatorView.equals(VIEW_BASIC))
        { buttonHistory.addActionListener(basicPanel::performHistoryAction); }
        else if (calculatorView.equals(VIEW_PROGRAMMER))
        { buttonHistory.addActionListener(programmerPanel::performHistoryAction); }
        LOGGER.debug("History button configured");
    }

    /**
     * Sets up the common buttons used in
     * any panel that uses them
     */
    public void setupCommonButtons()
    {
        LOGGER.debug("Configuring common buttons...");
        getCommonButtons().forEach(button -> {
            button.setFont(mainFont);
            if (calculatorView == VIEW_BASIC)
                // larger buttons
                //button.setPreferredSize(new Dimension(45, 45));
                button.setPreferredSize(new Dimension(35, 35));
            else if (calculatorView == VIEW_PROGRAMMER)
                button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });

        buttonPercent.setName(PERCENT);
        if (calculatorView == VIEW_BASIC)
        { buttonPercent.addActionListener(basicPanel::performPercentButtonAction); }
        LOGGER.debug("Percent button configured");

        buttonSquareRoot.setName(SQUARE_ROOT);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonSquareRoot.addActionListener(this::performSquareRootButtonAction); }
        LOGGER.debug("SquareRoot button configured");

        buttonSquared.setName(SQUARED);
        if (calculatorView == VIEW_BASIC)
        { buttonSquared.addActionListener(basicPanel::performSquaredButtonAction); }
        LOGGER.debug("Squared button configured");

        buttonFraction.setName(FRACTION);
        if (calculatorView == VIEW_BASIC)
        { buttonFraction.addActionListener(basicPanel::performFractionButtonAction); }
        LOGGER.debug("Fraction button configured");

        buttonClearEntry.setName(CLEAR_ENTRY);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonClearEntry.addActionListener(this::performClearEntryButtonAction); }
        LOGGER.debug("ClearEntry button configured");

        buttonClear.setName(CLEAR);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonClear.addActionListener(this::performClearButtonAction); }
        LOGGER.debug("Clear button configured");

        buttonDelete.setName(DELETE);
        if (calculatorView == VIEW_BASIC)
        { buttonDelete.addActionListener(this::performDeleteButtonAction); }
        else if (calculatorView == VIEW_PROGRAMMER)
        { buttonDelete.addActionListener(programmerPanel::performDeleteButtonAction); }
        LOGGER.debug("Delete button configured");

        buttonDivide.setName(DIVISION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonDivide.addActionListener(this::performDivideButtonAction); }
        LOGGER.debug("Divide button configured");

        buttonMultiply.setName(MULTIPLICATION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonMultiply.addActionListener(this::performMultiplicationAction); }
        LOGGER.debug("Multiply button configured");

        buttonSubtract.setName(SUBTRACTION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonSubtract.addActionListener(this::performSubtractionButtonAction); }
        LOGGER.debug("Subtract button configured");

        buttonAdd.setName(ADDITION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonAdd.addActionListener(this::performAdditionButtonAction); }
        LOGGER.debug("Addition button configured");

        buttonNegate.setName(NEGATE);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonNegate.addActionListener(this::performNegateButtonAction); }
        LOGGER.debug("Negate button configured");

        buttonDecimal.setName(DECIMAL);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonDecimal.addActionListener(this::performDecimalButtonAction); }
        LOGGER.debug("Decimal button configured");

        buttonEquals.setName(EQUALS);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonEquals.addActionListener(this::performEqualsButtonAction); }
        LOGGER.debug("Equals button configured");

        LOGGER.debug("Common buttons configured");
    }

    /**
     * The main method to set up all number buttons, 0-9
     */
    public void setupNumberButtons()
    {
        LOGGER.debug("Configuring Number buttons...");
        AtomicInteger i = new AtomicInteger(0);
        getNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(true);
            if (calculatorView == VIEW_BASIC)
                // larger buttons
                //button.setPreferredSize(new Dimension(45, 45));
                button.setPreferredSize(new Dimension(35, 35));
            else if (calculatorView == VIEW_PROGRAMMER)
                button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            if (calculatorView.equals(VIEW_BASIC))
            { button.addActionListener(this::performNumberButtonAction); }
            else if (calculatorView.equals(VIEW_PROGRAMMER))
            { button.addActionListener(programmerPanel::performNumberButtonActions); }
            else if (calculatorView.equals(VIEW_CONVERTER))
            { button.addActionListener(converterPanel::performNumberButtonActions); }
            else
            { LOGGER.warn("Add other Panels to work with Number buttons"); }
        });
        LOGGER.debug("Number buttons configured");
    }

    /**
     * The main method to set up the Blank1 button
     */
    public void setupButtonBlank1()
    {
        LOGGER.debug("Configuring Button Blank1...");
        buttonBlank1.setFont(mainFont);
        buttonBlank1.setPreferredSize(new Dimension(35, 35));
        buttonBlank1.setBorder(new LineBorder(Color.BLACK));
        buttonBlank1.setEnabled(true);
        buttonBlank1.setName(BLANK);
        LOGGER.debug("Button Blank1 configured");
    }

    /**
     * The main method to set up the Blank2 button
     */
    public void setupButtonBlank2()
    {
        LOGGER.debug("Configuring Button Blank2...");
        buttonBlank2.setFont(mainFont);
        buttonBlank2.setPreferredSize(new Dimension(35, 35));
        buttonBlank2.setBorder(new LineBorder(Color.BLACK));
        buttonBlank2.setEnabled(true);
        buttonBlank2.setName(BLANK);
        LOGGER.debug("Button Blank2 configured");
    }

    /**
     * The main operations to perform to set up
     * the Look Menu item
     */
    private void setupLookMenu()
    {
        LOGGER.debug("Configuring Look menu...");
        JMenuItem metal = new JMenuItem(METAL);
        metal.setFont(mainFont);
        metal.setName(METAL);
        metal.addActionListener(this::performLookMenuAction);

        JMenuItem system = new JMenuItem(SYSTEM);
        system.setFont(mainFont);
        system.setName(SYSTEM);
        system.addActionListener(this::performLookMenuAction);

        JMenuItem windows = new JMenuItem(WINDOWS);
        windows.setFont(mainFont);
        windows.setName(WINDOWS);
        windows.addActionListener(this::performLookMenuAction);

        JMenuItem motif = new JMenuItem(MOTIF);
        motif.setFont(mainFont);
        motif.setName(MOTIF);
        motif.addActionListener(this::performLookMenuAction);

        JMenuItem gtk = new JMenuItem(GTK);
        gtk.setFont(mainFont);
        gtk.setName(GTK);
        gtk.addActionListener(this::performLookMenuAction);

        JMenuItem apple = new JMenuItem(APPLE);
        apple.setFont(mainFont);
        apple.setName(APPLE);
        apple.addActionListener(this::performLookMenuAction);

        setLookMenu(new JMenu(LOOK));
        lookMenu.add(metal);
        lookMenu.add(motif);
        lookMenu.add(apple);
        if (!isOSMac()) // add more options if using Windows
        {
            lookMenu.add(windows);
            lookMenu.add(system);
            lookMenu.add(gtk);
            lookMenu.remove(apple);
        }
        String look = preferences.get(LOOK, "");
        switch (look) {
            case METAL -> metal.doClick();
            case SYSTEM -> system.doClick();
            case WINDOWS -> windows.doClick();
            case MOTIF -> motif.doClick();
            case GTK -> gtk.doClick();
            case APPLE -> apple.doClick();
            default -> {
                LOGGER.warn("Look preference not found: '{}'. Defaulting to Metal.", look);
                metal.doClick();
            }
        }
        lookMenu.setFont(mainFont);
        lookMenu.setName(LOOK);
        menuBar.add(lookMenu);
        LOGGER.debug("Look menu configured");
    }

    /**************** LOOK MENU ACTIONS ****************/
    /**
     * Performs the action for the Look Menu item
     * @param actionEvent the action event
     */
    private void performLookMenuAction(ActionEvent actionEvent)
    {
        try
        {
            switch (((JMenuItem)actionEvent.getSource()).getName()) {
                case METAL -> {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    setIsMetal(true);
                }
                case SYSTEM -> {
                    UIManager.setLookAndFeel("javax.swing.plaf.system.SystemLookAndFeel");
                    setIsSystem(true);
                }
                case WINDOWS -> {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    setIsWindows(true);
                }
                case MOTIF -> {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    setIsMotif(true);
                }
                case GTK -> {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    setIsGtk(true);
                }
                case APPLE -> {
                    UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
                    setIsApple(true);
                }
            }
            if (null != textPane) // not used in all Calculators
            {
                textPane.setBackground(Color.WHITE);
                textPane.setBorder(new LineBorder(Color.BLACK));
            }
            if (calculatorView == VIEW_BASIC)
            {
                if (null != basicPanel.getHistoryTextPane())
                {
                    basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                    basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                }
            }
            else if (calculatorView == VIEW_PROGRAMMER)
            {
                if (null != programmerPanel.getHistoryTextPane())
                {
                    programmerPanel.getHistoryTextPane().setBackground(Color.WHITE);
                    programmerPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                }
            }
            else
            {
                LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
            }
            updateLookAndFeel();
            savePreferences();
            resetLook();
        }
        catch (ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException e)
        { logException(e); }
    }
    /**************** END LOOK MENU ACTIONS ****************/

    /**
     * The main operations to perform to set up
     * the View Menu item
     */
    private void setupViewMenu()
    {
        LOGGER.debug("Configuring View menu...");
        JMenuItem basic = new JMenuItem(VIEW_BASIC.getValue());
        JMenuItem programmer = new JMenuItem(VIEW_PROGRAMMER.getValue());
        JMenuItem date = new JMenuItem(VIEW_DATE.getValue());
        JMenu converterMenu = new JMenu(VIEW_CONVERTER.getValue());
        basic.setFont(mainFont);
        basic.setName(VIEW_BASIC.getValue());
        basic.addActionListener(action -> performViewMenuAction(action, VIEW_BASIC));
        programmer.setFont(mainFont);
        programmer.setName(VIEW_PROGRAMMER.getValue());
        programmer.addActionListener(action -> performViewMenuAction(action, VIEW_PROGRAMMER));
        date.setFont(mainFont);
        date.setName(VIEW_DATE.getValue());
        date.addActionListener(action -> performViewMenuAction(action, VIEW_DATE));
        JMenuItem angleConverter = new JMenuItem(ANGLE.getValue());
        angleConverter.setFont(mainFont);
        angleConverter.setName(ANGLE.getValue());
        angleConverter.addActionListener(action -> performViewMenuAction(action, ANGLE));
        JMenuItem areaConverter = new JMenuItem(AREA.getValue()); // The converterMenu is a menu of more choices
        areaConverter.setFont(mainFont);
        areaConverter.setName(AREA.getValue());
        areaConverter.addActionListener(action -> performViewMenuAction(action, AREA));
        converterMenu.setFont(mainFont);
        converterMenu.setName(VIEW_CONVERTER.getValue());
        converterMenu.add(angleConverter);
        converterMenu.add(areaConverter);

        setViewMenu(new JMenu(VIEW));
        viewMenu.add(basic);
        viewMenu.add(programmer);
        viewMenu.add(date);
        viewMenu.addSeparator();
        viewMenu.add(converterMenu);
        viewMenu.setFont(mainFont);
        viewMenu.setName(VIEW);
        menuBar.add(viewMenu); // add viewMenu to menu bar
        LOGGER.debug("View menu configured");
    }

    /**************** VIEW MENU ACTIONS ****************/
    /**
     * The main actions to perform when switch panels
     * @param actionEvent the click action
     */
    public void performViewMenuAction(ActionEvent actionEvent, CalculatorType updatedView)
    {
        LOGGER.info("Switching views...");
        LOGGER.debug("Action: {}", actionEvent.getActionCommand());
        String currentView = calculatorView.getName();
        String newView = updatedView.getName();
        LOGGER.debug("from '{}' to '{}'", currentView, newView);
        if (currentView.equals(newView))
        { confirm(this, LOGGER, "Not changing to " + newView + " when already showing " + currentView); }
        else if (newView.equals(converterType.getValue()))
        { confirm(this, LOGGER, "Not changing panels when the converterType is the same"); }
        else
        {
            String currentValueInTextPane;
            switch (updatedView) {
                case VIEW_BASIC -> {
                    currentValueInTextPane = getTextPaneValue();
                    switchPanels(basicPanel, actionEvent);
                    if (!currentValueInTextPane.isEmpty()) {
                        if (!values[0].isEmpty()) appendTextToPane(addCommas(values[0]));
                        if (isDecimalNumber(values[0])) {
                            buttonDecimal.setEnabled(false);
                        }
                        /* TODO: Handle what happens if the currentValue = v[0] + programmerOperator
                         * and we switch to Basic. Right now it just drops the operator but vP = 1
                         * because we hit a programmerOperator. So vP needs to go back to 0??
                         */
                        if (isOperatorActive()) {
                            appendTextToPane(addCommas(values[0]) + SPACE + getActiveOperator());
                        }
                    }
                }
                case VIEW_PROGRAMMER -> {
                    currentValueInTextPane = getTextPaneValue();
                    switchPanels(programmerPanel, actionEvent);
                    if (!currentValueInTextPane.isEmpty()) {
                        if (!values[0].isEmpty()) {
                            programmerPanel.appendTextToProgrammerPane(values[0]);
                        }
                        else {
                            // see ProgrammerPanel.displayByteAndBase()
                            if (!(calculatorByte.getValue() + SPACE + SPACE + calculatorBase.getValue()).equals(currentValueInTextPane)) {
                                programmerPanel.appendTextToProgrammerPane(currentValueInTextPane);
                            }
                        }
                        if (isDecimalNumber(values[0])) {
                            buttonDecimal.setEnabled(false);
                        }
                        if (isOperatorActive()) {
                            programmerPanel.appendTextToProgrammerPane(values[0] + SPACE + getActiveOperator());
                        }
                    }
                }
                case VIEW_SCIENTIFIC -> {
                    LOGGER.warn("Setup");
                    switchPanels(scientificPanel, actionEvent);
                }
                case VIEW_DATE -> {
                    switchPanels(datePanel, actionEvent);
                }
                case ANGLE -> {
                    setConverterType(ANGLE);
                    switchPanels(converterPanel, actionEvent);
                }
                case AREA -> {
                    setConverterType(AREA);
                    setCalculatorView(VIEW_CONVERTER);
                }
                default -> { LOGGER.warn("Add other views"); }
            }
            updateLookAndFeel();
            confirm(this, LOGGER, "Switched from " + currentView + " to " + newView);
        }
    }

    /**
     * The inner logic to perform when switching panels
     * @param newPanel the new panel to switch to
     */
    private void switchPanels(JPanel newPanel, ActionEvent actionEvent)
    {
        LOGGER.debug("SwitchPanels: {}", newPanel.getName());
        setTitle(newPanel.getName());
        updatePanel(newPanel, actionEvent);
        setSize(currentPanel.getSize());
        setMinimumSize(currentPanel.getSize());
        pack();
    }

    /**
     * This method updates the panel by removing the old panel,
     * setting up the new current panel, and adds it to the frame
     * @param newPanel the panel to update on the Calculator
     */
    private void updatePanel(JPanel newPanel, ActionEvent actionEvent)
    {
        LOGGER.debug("Updating to panel {}...", newPanel.getClass().getSimpleName());
        JPanel oldPanel = currentPanel;
        remove(oldPanel);
        currentPanel = newPanel;
        calculatorView = switch (currentPanel.getClass().getSimpleName())
        {
            case "BasicPanel" -> VIEW_BASIC;
            case "ProgrammerPanel" -> VIEW_PROGRAMMER;
            case "ScientificPanel" -> VIEW_SCIENTIFIC;
            case "DatePanel" -> VIEW_DATE;
            case "ConverterPanel" -> VIEW_CONVERTER;
            default -> {
                logException(new IllegalStateException("Unexpected value: " + currentPanel.getClass().getSimpleName()));
                yield VIEW_BASIC;
            }
        };
        setupPanel(actionEvent);
        add(currentPanel);
        LOGGER.debug("Panel updated");
    }

    /**
     * The main method that calls the setup method for a specific panel
     */
    protected void setupPanel(ActionEvent actionEvent)
    {
        LOGGER.debug("Setting up panel: {}", calculatorView.getValue());
        switch (calculatorView) {
            case VIEW_BASIC -> {
                basicPanel.setupBasicPanel(this);
                closeHistoryIfOpen(actionEvent);
            }
            case VIEW_PROGRAMMER -> {
                programmerPanel.setupProgrammerPanel(this);
                closeHistoryIfOpen(actionEvent);
            }
            case VIEW_SCIENTIFIC -> {
                scientificPanel.setupScientificPanel();
                closeHistoryIfOpen(actionEvent);
            }
            case VIEW_DATE -> {
                datePanel.setupDatePanel(this, dateOperation);
            }
            case VIEW_CONVERTER -> {
                converterPanel.setupConverterPanel(this, converterType);
            }
        }
        LOGGER.debug("Panel set up");
    }
    /**************** END VIEW MENU ACTIONS ****************/

    /**
     * The main operations to perform to set up
     * the Edit Menu item
     */
    private void setupEditMenu()
    {
        LOGGER.debug("Configuring Edit menu...");
        JMenuItem copyItem = new JMenuItem(COPY);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.setFont(mainFont);
        copyItem.setName(COPY);
        copyItem.addActionListener(this::performCopyAction);
        JMenuItem pasteItem = new JMenuItem(PASTE);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setFont(mainFont);
        pasteItem.setName(PASTE);
        pasteItem.addActionListener(this::performPasteAction);
        JMenuItem clearHistoryItem = new JMenuItem(CLEAR_HISTORY);
        clearHistoryItem.setFont(mainFont);
        clearHistoryItem.setName(CLEAR_HISTORY);
        clearHistoryItem.addActionListener(this::performClearHistoryTextPaneAction);
        JMenuItem showMemoriesItem = new JMenuItem(SHOW_MEMORIES);
        showMemoriesItem.setFont(mainFont);
        showMemoriesItem.setName(SHOW_MEMORIES);
        showMemoriesItem.addActionListener(this::performShowMemoriesAction);

        setEditMenu(new JMenu(EDIT));
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(clearHistoryItem);
        editMenu.add(showMemoriesItem);
        editMenu.setFont(mainFont);
        editMenu.setName(EDIT);
        menuBar.add(editMenu);
        LOGGER.debug("Edit menu configured");
    }

    /**************** EDIT MENU ACTIONS ****************/
    /**
     * Takes the value in the textPane and saves it in values[2]
     *
     * @param actionEvent the click action
     */
    public void performCopyAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        values[2] = getTextPaneValue();
        StringSelection selection = new StringSelection(values[2]);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        confirm(this, LOGGER, PRESSED + SPACE + COPY);
    }

    /**
     * Pastes the current value in values[2] in the textPane if
     * values[2] has a value
     *
     * @param actionEvent the click action
     */
    public void performPasteAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        if (values[2].isEmpty()) confirm(this, LOGGER, "Values[2] is empty. Nothing to paste");
        else
        {
            LOGGER.debug("values[2]: {}", values[2]);
            appendTextToPane(values[2]);
            values[valuesPosition] = getTextPaneValue();
            confirm(this, LOGGER, PRESSED + SPACE + PASTE);
        }
    }

    /**
     * Clears the history for current panel
     */
    public void performClearHistoryTextPaneAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        if (currentPanel instanceof BasicPanel)
        { basicPanel.getHistoryTextPane().setText(BLANK); }
        else if (currentPanel instanceof ProgrammerPanel)
        { programmerPanel.getHistoryTextPane().setText(BLANK); }
        else LOGGER.warn("Add other history panels");
    }

    /**
     * Displays all the memory values in the history panel
     */
    public void performShowMemoriesAction(ActionEvent actionEvent)
    {
        LOGGER.debug("Action for {} started", actionEvent.getActionCommand());
        if (currentPanel instanceof BasicPanel)
        {
            if (!isMemoryValuesEmpty())
            {
                StringBuilder memoriesString = new StringBuilder();
                memoriesString.append("Memories: ");
                for(int i=0; i<memoryPosition; i++)
                {
                    memoriesString.append("[").append(memoryValues[i]).append("]");
                    if ((i+1) < memoryValues.length && !memoryValues[i+1].equals(BLANK))
                    { memoriesString.append(", "); }
                }
                basicPanel.getHistoryTextPane().setText(
                        basicPanel.getHistoryTextPane().getText() +
                                addNewLines() + memoriesString
                );
            }
            else
            {
                basicPanel.getHistoryTextPane().setText(
                        basicPanel.getHistoryTextPane().getText() +
                                addNewLines() + "No Memories Stored"
                );
            }
        }
        else if (currentPanel instanceof ProgrammerPanel)
        {
            if (!isMemoryValuesEmpty())
            {
                StringBuilder memoriesString = new StringBuilder();
                memoriesString.append("Memories: ");
                for(int i=0; i<memoryPosition; i++)
                {
                    memoriesString.append("[").append(memoryValues[i]).append("]");
                    if ((i+1) < memoryValues.length && !memoryValues[i+1].equals(BLANK))
                    { memoriesString.append(", "); }
                }
                programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                                addNewLines() + memoriesString
                );
            }
            else
            {
                programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                                addNewLines() + "No Memories Stored"
                );
            }
        }
        else { LOGGER.warn("Add other history panels"); }
        LOGGER.debug("Show Memories complete");
    }
    /**************** END EDIT MENU ACTIONS ****************/

    /**
     * The main operations to perform to set up
     * the Help Menu item
     */
    private void setupHelpMenu()
    {
        LOGGER.debug("Configuring Help menu...");
        setHelpMenu(new JMenu(HELP));
        JMenuItem showHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();

        helpMenu.add(showHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        helpMenu.setFont(mainFont);
        helpMenu.setName(HELP);
        menuBar.add(helpMenu);
        LOGGER.debug("Help menu configured");
    }

    /**************** HELP MENU ACTIONS ****************/
    /**
     * This method creates the View Help menu option under Help
     * The help text is added by the currentPanel.
     */
    private JMenuItem createViewHelpJMenuItem()
    {
        JMenuItem viewHelpItem = new JMenuItem(VIEW_HELP);
        viewHelpItem.setName(VIEW_HELP);
        viewHelpItem.setFont(mainFont);
        LOGGER.debug("View Help configured. Panel adds text");
        return viewHelpItem;
    }

    /**
     * Updates the Help menu item to display the
     * proper help text based on the current panel
     * @return JMenuItem the updated help menu item
     */
    public JMenuItem updateShowHelp()
    {
        JMenuItem viewHelp = helpMenu.getItem(0);
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel(helpString));
        helpMenu.add(viewHelp, 0);
        LOGGER.debug("Show Help updated");
        return viewHelp;
    }

    /**
     * This method creates the About Calculator menu option under Help
     */
    private JMenuItem createAboutCalculatorJMenuItem()
    {
        JMenuItem aboutCalculatorItem = new JMenuItem(ABOUT_CALCULATOR);
        aboutCalculatorItem.setName(ABOUT_CALCULATOR);
        aboutCalculatorItem.setFont(mainFont);
        aboutCalculatorItem.addActionListener(this::performAboutCalculatorAction);
        LOGGER.debug("About Calculator configured");
        return aboutCalculatorItem;
    }

    /**
     * Display the text for About Calculator menu item
     *
     * @param actionEvent the click action
     */
    public void performAboutCalculatorAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        JPanel iconPanel = new JPanel(new GridBagLayout());
        JLabel iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        ImageIcon specificLogo = isOSMac() ? macIcon : windowsIcon;
        JLabel textLabel = new JLabel(getAboutCalculatorString(), specificLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(this, mainPanel, ABOUT_CALCULATOR, JOptionPane.PLAIN_MESSAGE);
        confirm(this, LOGGER, PRESSED + SPACE + ABOUT_CALCULATOR);
    }
    /**************** END HELP MENU ACTIONS ****************/

    /**************** Add Components to Panels or Panels to Frame ****************/

    /**
     * Used to add a component to a panel
     * @param calculatorPanel the current view panel
     * @param constraints the grid bag constraints
     * @param panel the panel to add the component to
     * @param c the component to add
     * @param row the row to add the component to
     * @param column the column to add the component to
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             Component c, int row, int column)
    { addComponent(calculatorPanel, constraints, panel, c, row, column, null,
            1, 1, 1.0, 1.0, 0, 0); }

    /**
     * Used to add a component to a panel
     * @param panel the panel to add to
     * @param c the component to add
     * @param column the column to add the component to
     * @param insets the space between the component
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                              Component c, int column, Insets insets)
    { addComponent(calculatorPanel, constraints, panel, c, 1, column, insets, 1, 1, 1.0, 1.0, 0, 0); }

    /**
     * Used to add the panel to the frame
     * @param panel the panel to add to the frame, not null
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel)
    {
        if (calculatorView == VIEW_CONVERTER) {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, null, 0, 0, 1.0, 1.0, 0, GridBagConstraints.CENTER);
        } else {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, new Insets(0,0,0,0),
                    0, 0, 1.0, 1.0, 0, GridBagConstraints.NORTH);

        }
    }

    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             Component c, int row, double weightx, double weighty)
    { addComponent(calculatorPanel, constraints, panel, c, row, 0, null, 1, 1, weightx, weighty, 0, 0); }

    /**
     * Sets the constraints for a component or panel
     * and adds the component to the specified panel
     * or the specified panel to the frame.
     *
     * @param panel the panel to add to, not null
     * @param c the component to add to a panel, can be null
     * @param row the row to place the component in
     * @param column the column to place the component in
     * @param insets the space between the component
     * @param gridwidth the number of columns the component should use
     * @param gridheight the number of rows the component should use
     * @param weightXRow set to allow the button grow horizontally
     * @param weightYColumn set to allow the button grow horizontally
     * @param fill set to make the component resize if any unused space
     * @param anchor set to place the component in a specific location on the frame
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel, Component c,
                              int row, int column, Insets insets, int gridwidth, int gridheight,
                              double weightXRow, double weightYColumn, int fill, int anchor)
    {
        constraints.gridy = row;
        constraints.gridx = column;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.weightx = weightXRow;
        constraints.weighty = weightYColumn;
        constraints.insets = insets == null ? new Insets(1, 1, 1, 1) : insets;
        if (fill != 0)   constraints.fill = fill;
        if (anchor != 0) constraints.anchor = anchor;
        if (c != null) {
            panel.add(c, constraints);
            LOGGER.debug("Added {} to {}", c.getName(), panel.getName());
        }
        else {
            calculatorPanel.add(panel, constraints);
            LOGGER.debug("Added panel {} to calculator frame", panel.getName());
        }
    }
    /**************** END CONFIGURATION ****************/

    /**************** BUTTON ACTIONS ****************/

    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public void performMemoryStoreAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "No number to add to memory");
        }
        else if (textPaneContainsBadText())
        { confirm(this, LOGGER, "Not saving " + getTextPaneValue() + " in Memory"); }
        else
        {
            if (memoryPosition == 10) // reset to 0
            { setMemoryPosition(0); }
            memoryValues[memoryPosition] = getTextPaneValue();
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryClear.setEnabled(true);
            buttonMemoryAddition.setEnabled(true);
            buttonMemorySubtraction.setEnabled(true);
            writeHistoryWithMessage(buttonChoice, false, " Saved: " + memoryValues[memoryPosition] + " to memory location " + (memoryPosition + 1));
            setMemoryPosition(memoryPosition + 1);
            confirm(this, LOGGER, memoryValues[memoryPosition - 1] + " is stored in memory at position: " + (memoryPosition - 1));
        }
    }

    /**
     * The actions to perform when MemoryRecall is clicked
     * @param actionEvent the click action
     */
    public void performMemoryRecallAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (memoryRecallPosition == 10 || memoryValues[memoryRecallPosition].isBlank())
        { setMemoryRecallPosition(getLowestMemoryPosition()); }
        appendTextToPane(memoryValues[memoryRecallPosition]);
        values[valuesPosition] = getTextPaneValue();
        writeHistoryWithMessage(buttonChoice, false, " Recalled: " + memoryValues[memoryRecallPosition] + " at memory location " + (memoryRecallPosition+1));
        memoryRecallPosition += 1;
        confirm(this, LOGGER, "Recalling number in memory: " + memoryValues[(memoryRecallPosition-1)] + " at position: " + (memoryRecallPosition-1));
    }
    
    /**
     * The actions to perform when MemoryClear is clicked
     * @param actionEvent the click action
     */
    public void performMemoryClearAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (memoryPosition == 10)
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            memoryPosition = 0;
        }
        if (!isMemoryValuesEmpty())
        {
            setMemoryPosition(getLowestMemoryPosition());
            LOGGER.info("Clearing memoryValue[{}] = {}", memoryPosition, memoryValues[memoryPosition]);
            writeHistoryWithMessage(buttonChoice, false, " Cleared " + memoryValues[memoryPosition] + " from memory location " + (memoryPosition+1));
            memoryValues[memoryPosition] = BLANK;
            memoryRecallPosition += 1;
            confirm(this, LOGGER, "Cleared memory at " + memoryPosition);
            // MemorySuite could now be empty
            if (isMemoryValuesEmpty())
            {
                memoryPosition = 0;
                memoryRecallPosition = 0;
                buttonMemoryClear.setEnabled(false);
                buttonMemoryRecall.setEnabled(false);
                buttonMemoryAddition.setEnabled(false);
                buttonMemorySubtraction.setEnabled(false);
                appendTextToPane(ZERO);
                confirm(this, LOGGER, "MemorySuite is empty");
            }
        }
    }

    /**
     * The actions to perform when M+ is clicked
     * Will add the current number in the textPane to the previously
     * save value in Memory. It then displays that result in the
     * textPane as a confirmation.
     * @param actionEvent the click action
     */
    public void performMemoryAdditionAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, "Cannot add bad text to memories values"); }
        else if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "No number to add to memory");
        }
        else
        {
            LOGGER.debug("textPane: '{}'", getTextPaneValue());
            LOGGER.debug("memoryValues[{}] = {}", memoryPosition-1, memoryValues[(memoryPosition-1)]);
            double result = Double.parseDouble(getTextPaneValue())
                    + Double.parseDouble(memoryValues[(memoryPosition-1)]); // create result forced double
            LOGGER.debug("{} + {} = {}", getTextPaneValue(), memoryValues[(memoryPosition-1)], result);
            if (result % 1 == 0)
            { memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
            else
            { memoryValues[(memoryPosition-1)] = Double.toString(result); }
            writeHistoryWithMessage(buttonChoice, false, " Added " + getTextPaneValue() + " to memories at " + (memoryPosition) + SPACE + EQUALS + SPACE + memoryValues[(memoryPosition-1)]);
            confirm(this, LOGGER, "The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * The actions to perform when MemorySubtraction is clicked
     * @param actionEvent the click action
     */
    public void performMemorySubtractionAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, "Cannot subtract bad text to memories values"); }
        else if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "No number to subtract from memory");
        }
        else
        {
            LOGGER.debug("textPane: '{}'", getTextPaneValue());
            LOGGER.debug("memoryValues[{}] = {}", memoryPosition-1, memoryValues[(memoryPosition-1)]);
            double result = Double.parseDouble(memoryValues[(memoryPosition-1)])
                    - Double.parseDouble(getTextPaneValue()); // create result forced double
            LOGGER.debug("{} - {} = {}", getTextPaneValue(), memoryValues[(memoryPosition-1)], result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (result % 1 == 0)
            { memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
            else
            { memoryValues[(memoryPosition-1)] = Double.toString(result); }
            writeHistoryWithMessage(buttonChoice, false, " Subtracted " + getTextPaneValue() + " to memories at " + (memoryPosition) + SPACE + EQUALS + SPACE + memoryValues[(memoryPosition-1)]);
            confirm(this, LOGGER, "The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * The actions to perform when the SquareRoot button is clicked
     * @param actionEvent the click action
     */
    public void performSquareRootButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT)); }
        else if (getTextPaneValue().isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
        }
        else if (isNegativeNumber(values[valuesPosition]))
        {
            appendTextToPane(ONLY_POSITIVES);
            confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
        }
        else
        {
            double result = Math.sqrt(Double.parseDouble(getTextPaneValue()));
            LOGGER.debug("result: " + result);
            if (result % 1 == 0)
            {
                values[valuesPosition] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                values[valuesPosition] = formatNumber(String.valueOf(result));
                buttonDecimal.setEnabled(false);
            }
            appendTextToPane(addCommas(values[valuesPosition]));
            writeHistory(buttonChoice, false);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public void performClearEntryButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        LOGGER.debug("valuesPosition: {}", valuesPosition);
        if (getTextPaneValue().isEmpty())
        { confirm(this, LOGGER, CLEAR_ENTRY + " called... nothing to clear"); }
        else if (valuesPosition == 0 || values[1].isEmpty())
        {
            values[0] = BLANK;
            resetOperators(false);
            if (calculatorView == VIEW_PROGRAMMER)
            { programmerPanel.resetProgrammerOperators(false); }
            valuesPosition = 0;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
            clearTextInTextPane();
            writeHistoryWithMessage(buttonChoice, false, SPACE + buttonChoice + " performed");
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
        else
        {
            String operator = getActiveOperator();
            values[1] = BLANK;
            isFirstNumber = false;
            valuesPosition = 1;
            isNumberNegative = false;
            buttonDecimal.setEnabled(true);
            appendTextToPane(addCommas(values[0]) + SPACE + operator);
            writeHistoryWithMessage(buttonChoice, false, SPACE + buttonChoice + " performed");
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Clear button is clicked
     * @param actionEvent the action performed
     */
    public void performClearButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        for (int i=0; i<4; i++)
        {
            if (i == 0) { values[i] = ZERO; }
            //else if (i==3) { values[i] = PUSHED_CLEAR; }
            else { values[i] = BLANK; }
        }
        for(int i=0; i < 10; i++)
        { memoryValues[i] = BLANK; }
        appendTextToPane(ZERO);
        //values[3] = BLANK;
        resetOperators(false);
        valuesPosition = 0;
        memoryPosition = 0;
        isFirstNumber = true;
        isNumberNegative = false;
        buttonMemoryRecall.setEnabled(false);
        buttonMemoryClear.setEnabled(false);
        buttonMemoryAddition.setEnabled(false);
        buttonMemorySubtraction.setEnabled(false);
        buttonDecimal.setEnabled(true);
        writeHistoryWithMessage(buttonChoice, false, " Cleared all values");
        confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
    }

    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performDeleteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneTextValue = getTextPaneValue();
        if (textPaneContainsBadText())
        {
            appendTextToPane(getBadText());
            confirm(this, LOGGER, cannotPerformOperation(DELETE));
        }
        else if (textPaneTextValue.isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "No need to perform " + DELETE + " operation");
        }
        else
        {
            if (valuesPosition == 1 && getValueAtPosition().isEmpty())
            { valuesPosition = 0; } // presume they could have pressed an operator then wish to delete
            boolean isNegative = isNegativeNumber(textPaneTextValue);
            String value = getValueWithoutAnyOperator(textPaneTextValue);
            if (isNegative) value = SUBTRACTION + value;
            if (getValueAtPosition().isEmpty() || !getValueAtPosition().equals(value))
            { values[0] = textPaneTextValue; }
            logValuesAtPosition(this, LOGGER);
            LOGGER.debug("textPane: {}", textPaneTextValue);
            if (isNoOperatorActive())
            {
                values[valuesPosition] = getValueAtPosition().substring(0,getValueAtPosition().length()-1);
                appendTextToPane(addCommas(values[valuesPosition]));
            }
            else if (isOperatorActive())
            {
                LOGGER.debug("An operator has been pushed");
                if (valuesPosition == 0)
                {
                    if (isAdding) isAdding = false;
                    else if (isSubtracting) isSubtracting = false;
                    else if (isMultiplying) isMultiplying = false;
                    else if (isDividing()) isDividing = false;
                    else if (calculatorView == VIEW_PROGRAMMER)
                    {
                        if (programmerPanel.isOr()) programmerPanel.setOr(false);
                        else if (programmerPanel.isModulus()) programmerPanel.setModulus(false);
                        else if (programmerPanel.isXor()) programmerPanel.setXor(false);
                        else if (programmerPanel.isNot()) programmerPanel.setNot(false);
                        else /*if (programmerPanel.isAnd())*/ programmerPanel.setAnd(false);
                    }
                    values[valuesPosition] = value;
                    appendTextToPane(addCommas(getValueAtPosition()));
                }
                else
                {
                    values[valuesPosition] = getValueAtPosition().substring(0,getValueAtPosition().length()-1);
                    appendTextToPane(addCommas(getValueAtPosition()));
                }
            }
            buttonDecimal.setEnabled(!isDecimalNumber(getValueAtPosition()));
            isNumberNegative = getValueAtPosition().startsWith(SUBTRACTION);
            writeHistory(buttonChoice, false);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public void performAdditionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText() || isMaximumValue())
        { confirm(this, LOGGER, cannotPerformOperation(ADDITION)); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(ADDITION));
        }
        else if (isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Maximum number met"); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Minimum number met"); }
        else
        {
            // TODO: Refactor isPemdasActive logic
            if (!isPemdasActive) {
                // No operator pushed until now, textPane has a value, and values is set
                if (!isAdding && !isSubtracting  && !isMultiplying &&!isDividing
                        && !textPane.getText().isBlank() && !getValueAtPosition().isBlank())
                {
                    isAdding = true;
                    appendTextToPane(getTextPaneValue() + SPACE + buttonChoice);
                    writeHistory(buttonChoice, true);
                    isFirstNumber = false;
                    isNumberNegative = false;
                    valuesPosition += 1;
                }
                else if (isAdding && !values[1].isEmpty())
                {
                    addition(ADDITION);  // 5 + 3 + ...
                    isAdding = true;
                    resetCalculatorOperations(false);
                    if (isMaximumValue()) // we can add to the minimum number, not to the maximum
                    {
                        isAdding = false;
                        appendTextToPane(addCommas(values[0]));
                    }
                    else
                    {
                        isAdding = true;
                        appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                        resetCalculatorOperations(true);
                    }
                }
                else if (isSubtracting && !values[1].isEmpty())
                {
                    subtract(ADDITION); // 5 - 3 + ...
                    isSubtracting = false;
                    isAdding = resetCalculatorOperations(false);
                    if (isMinimumValue() || isMaximumValue())
                    {
                        isAdding = false;
                        appendTextToPane(addCommas(values[0]));
                    }
                    else
                    {
                        isAdding = true;
                        appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                        resetCalculatorOperations(true);
                    }
                }
                else if (isMultiplying && !values[1].isEmpty())
                {
                    multiply(ADDITION); // 5 ✕ 3 + ...
                    isMultiplying = false;
                    isAdding = resetCalculatorOperations(false);
                    if (isMinimumValue() || isMaximumValue())
                    {
                        isAdding = false;
                        appendTextToPane(addCommas(values[0]));
                    }
                    else
                    {
                        isAdding = true;
                        appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                        resetCalculatorOperations(true);
                    }
                }
                else if (isDividing && !values[1].isEmpty())
                {
                    divide(ADDITION); // 5 ÷ 3 + ...
                    isDividing = false;
                    isAdding = resetCalculatorOperations(false);
                    if (isMinimumValue() || isMaximumValue())
                    {
                        isAdding = false;
                        appendTextToPane(addCommas(values[0]));
                    }
                    else
                    {
                        isAdding = true;
                        appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                        resetCalculatorOperations(true);
                    }
                }
                else if (!getTextPaneValue().isBlank() && values[0].isBlank())
                {
                    LOGGER.error("The user pushed plus but there is no number");
                    LOGGER.info("Setting values[0] to textPane value");
                    values[0] = getTextPaneValue().replace(COMMA, BLANK);
                    appendTextToPane(values[0] + SPACE + buttonChoice);
                    LOGGER.debug("values[0]: {}", values[0]);
                    writeHistory(buttonChoice, true);
                    isAdding = true;
                    isFirstNumber = false;
                    valuesPosition += 1;
                }
                else if (isAdding || isSubtracting || isMultiplying || isDividing)
                { LOGGER.error("Already chose an operator. Choose another number."); }
            }
            else {
                if (!isAdding && !isSubtracting  && !isMultiplying &&!isDividing
                        && !textPane.getText().isBlank() && !getValueAtPosition().isBlank())
                {
                    isAdding = true;
                    appendTextToPane(getTextPaneValue() + SPACE + buttonChoice);
                    writeHistory(buttonChoice, true);
                    isFirstNumber = false;
                    isNumberNegative = false;
                    valuesPosition += 1;
                }
                else {
                    isAdding = true;
                    writeHistory(buttonChoice, true);
                    appendTextToPane(getTextPaneValue() + SPACE + buttonChoice);
                }
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic for addition.
     * This method performs the addition between values[0] and values[1],
     * clears and trailing zeroes if the result is a whole number (15.0000)
     * resets dotPRESSED + SPACE to false, enables buttonDot and stores the result
     * back in values[0]
     */
    public void addition()
    {
        logValues(this, LOGGER, 2);
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info("{} + {} = {}", values[0], values[1], result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            writeContinuedHistory(EQUALS, ADDITION, result, false);
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            buttonDecimal.setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            writeContinuedHistory(EQUALS, ADDITION, result, false);
            buttonDecimal.setEnabled(false);
            values[0] = addCommas(formatNumber(String.valueOf(result)));
        }
    }
    private void addition(String continuedOperation)
    {
        logValues(this, LOGGER, 2);
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info("{} + {} = {}", values[0], values[1], result);
        if (result % 1 == 0) // if number is even
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.debug("We have a whole number");
                writeContinuedHistory(continuedOperation, ADDITION, result, true);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
        }
        else
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, ADDITION, result, true);
                values[0] = formatNumber(String.valueOf(result));
                buttonDecimal.setEnabled(false);
            }
        }
        LOGGER.info("Finished " + ADDITION);
    }

    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public void performSubtractionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(SUBTRACTION)); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met"); }
        //else if (isMaximumValue())
        //{ confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else
        {
            // No operator pushed, textPane has a value, and values[0] has a value
            if (isNoOperatorActive() && !getTextPaneValue().isEmpty() && !getValueAtPosition(0).isEmpty())
            {
                isSubtracting = true;
                values[valuesPosition] = getTextPaneValue();
                appendTextToPane(getValueAtPosition() + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                isNumberNegative = false;
                valuesPosition += 1;
            }
            // No operator pushed, textPane is empty
            else if (isNoOperatorActive() && getTextPaneValue().isEmpty())
            {
                values[valuesPosition] = getTextPaneValue();
                appendTextToPane(buttonChoice);
                writeHistory(buttonChoice, true);
                isNumberNegative = true;
                isSubtracting = false;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(SUBTRACTION); // 5 + 3 - ...
                isAdding = false;
                isSubtracting = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(SUBTRACTION); // 5 - 3 - ...
                isSubtracting = true;
                resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(values[0] + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(SUBTRACTION); // 5 ✕ 3 - ...
                isMultiplying = false;
                isSubtracting = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isDividing && !values[1].isEmpty())
            {
                divide(SUBTRACTION); // 5 ÷ 3 - ...
                isDividing = false;
                isSubtracting = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isOperatorActive() && !isNumberNegative)
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                writeHistory(buttonChoice, true);
                appendTextToPane(buttonChoice);
                isNumberNegative = true;
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank() )//&& !isNegativeNumber(getTextPaneValue()))
            {
                LOGGER.warn("The user pushed subtract but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = removeCommas(getTextPaneValue());
                setIsNumberNegative(isNegativeNumber(getValueAtPosition()));
                appendTextToPane(addCommas(getValueAtPosition()) + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isSubtracting = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic for subtracting
     */
    public void subtract()
    {
        logValues(this, LOGGER, 2);
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]);
        if (String.valueOf(result).contains(SUBTRACTION))
        {
            isNumberNegative = true;
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number, negating");
                LOGGER.debug("{} - '{}' = {}", values[0], values[1], result);
                writeContinuedHistory(EQUALS, SUBTRACTION, result, false);
                values[0] = convertToNegative(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                writeContinuedHistory(EQUALS, SUBTRACTION, result, false);
                values[0] = convertToNegative(formatNumber(String.valueOf(result)));
                buttonDecimal.setEnabled(false);
            }
        }
        else
        {
            isNumberNegative = false;
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                if (values[1].contains(SUBTRACTION)) {
                    LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                } else {
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                }
                writeContinuedHistory(EQUALS, SUBTRACTION, result, false);
                values[0] = addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                if (values[1].contains(SUBTRACTION)) {
                    LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                } else {
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                }
                writeContinuedHistory(EQUALS, SUBTRACTION, result, false);
                values[0] = addCommas(formatNumber(String.valueOf(result)));
                buttonDecimal.setEnabled(false);
            }
        }
    }
    private void subtract(String continuedOperation)
    {
        LOGGER.info("subtracting, continued op: {}", continuedOperation);
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]);
        LOGGER.info("{} - {} = {}", values[0], values[1], result);
        // TODO: new logic; test
        isNumberNegative = result < 0;
        if (result % 1 == 0)
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                if (isNumberNegative)
                {
                    LOGGER.debug("We have a negative number");
                    writeContinuedHistory(continuedOperation, SUBTRACTION, result, true);
                    //isNumberNegative = false;
                    values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                    buttonDecimal.setEnabled(true);
                }
                else
                {
                    LOGGER.debug("We have a whole number");
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION, result, true);
                    values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                    buttonDecimal.setEnabled(true);
                }
            }
        }
        else
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                if (isNumberNegative)
                {
                    LOGGER.debug("We have a decimal, negating");
                    LOGGER.info("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION, result, true);
                    isNumberNegative = false;
                    values[0] = formatNumber(String.valueOf(result));
                    buttonDecimal.setEnabled(false);
                }
                else
                {
                    LOGGER.debug("We have a decimal");
                    LOGGER.info("{} - {} = {}", values[0], convertToPositive(values[1]), result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION, result, true);
                    values[0] = formatNumber(String.valueOf(result));
                    buttonDecimal.setEnabled(false);
                }
            }
        }
        LOGGER.info("Finished " + SUBTRACTION);
    }

    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public void performMultiplicationAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText() || isMinimumValue())
        { confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION)); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met"); }
        else if (isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "Cannot perform " + MULTIPLICATION + " operation");
        }
        else
        {
            // No basic operator pushed, textPane has a value, and values is set
            if (isNoOperatorActive() && !textPane.getText().isBlank() && !getValueAtPosition().isBlank())
            {
                isMultiplying = true;
                appendTextToPane(values[valuesPosition] + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                valuesPosition += 1;
            }
            // No basic operator pushed, textPane is empty, and values is not set
            else if (isNoOperatorActive())
            {
                values[valuesPosition] = getTextPaneValue();
                appendTextToPane(getValueAtPosition() + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isNumberNegative = isNegativeNumber(getValueAtPosition());
                isMultiplying = true;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(MULTIPLICATION); // 5 + 3 ✕...
                isAdding = false;
                isMultiplying = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(MULTIPLICATION); // 5 - 3 ✕...
                isSubtracting = false;
                isMultiplying = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(MULTIPLICATION); // 5 ✕ 3 ✕...
                isMultiplying = true;
                resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                }
            }
            else if (isDividing && !values[1].isEmpty())
            {
                divide(MULTIPLICATION); // 5 ÷ 3 ✕...
                isDividing = false;
                isMultiplying = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank())
            {
                LOGGER.error("The user pushed multiple but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = removeCommas(getTextPaneValue());
                appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isMultiplying = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isOperatorActive())
            { LOGGER.info("already chose an operator. choose another number."); }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic for multiplying
     */
    public void multiply()
    {
        logValues(this, LOGGER, 2);
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]);
        String resultString = String.valueOf(result);
        if (result % 1 == 0)
        {
            resultString = clearZeroesAndDecimalAtEnd(resultString);
            LOGGER.debug("We have a whole number: {}", resultString);
            writeContinuedHistory(EQUALS, MULTIPLICATION, resultString, false);
            values[0] = resultString;
            buttonDecimal.setEnabled(true);
        }
        else
        {
            LOGGER.debug("We have a decimal: {}", result);
            writeContinuedHistory(EQUALS, MULTIPLICATION, result, false);
            values[0] = formatNumber(String.valueOf(result));
            buttonDecimal.setEnabled(false);
            return;
        }
    }
    private void multiply(String continuedOperation)
    {
        logValues(this, LOGGER, 2);
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]);
        LOGGER.info("{} * {} = {}", values[0], values[1], result);
        try
        {
            int wholeResult = Integer.parseInt(clearZeroesAndDecimalAtEnd(formatNumber(String.valueOf(result))));
            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
            LOGGER.info("We have a whole number");
            writeContinuedHistory(continuedOperation, MULTIPLICATION, result, true);
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(wholeResult));
            buttonDecimal.setEnabled(true);
        }
        catch (NumberFormatException nfe)
        {
            logException(nfe);
            LOGGER.info("We have a decimal");
            writeContinuedHistory(continuedOperation, MULTIPLICATION, result, true);
            values[0] = formatNumber(String.valueOf(result));
            buttonDecimal.setEnabled(false);
        }
        LOGGER.info("Finished " + MULTIPLICATION);
    }

    /**
     * The actions to perform when the Divide button is clicked
     * @param actionEvent the click action
     */
    public void performDivideButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(DIVISION)); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met"); }
        else if (!isNumberNegative && isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "Cannot perform " + DIVISION + " operation");
        }
        else {
            // No basic operator pushed, textPane has a value, and values is set
            if (!isAdding && !isSubtracting && !isMultiplying && !isDividing
                    && !textPane.getText().isBlank() && !getValueAtPosition().isBlank()) {
                isDividing = true;
                appendTextToPane(values[valuesPosition] + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                valuesPosition += 1;
            } else if (isAdding && !values[1].isEmpty()) {
                addition(DIVISION); // 5 + 3 ÷
                isAdding = false;
                isDividing = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue()) {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                } else {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }

            } else if (isSubtracting && !values[1].isEmpty()) {
                subtract(DIVISION); // 5 - 3 ÷
                isSubtracting = false;
                isDividing = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue()) {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                } else {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }

            } else if (isMultiplying && !values[1].isEmpty()) {
                multiply(DIVISION); // 5 ✕ 3 ÷...
                isMultiplying = false;
                isDividing = resetCalculatorOperations(false);
                if (isMinimumValue() || isMaximumValue()) {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                } else {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            } else if (isDividing && !values[1].isEmpty()) {
                divide(DIVISION); // 5 ÷ 3 ÷
                isDividing = true;
                resetCalculatorOperations(false);
                if (!getValueWithoutAnyOperator(getTextPaneValue()).equals(INFINITY) && (isMinimumValue() || isMaximumValue())) {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                } else if (getValueWithoutAnyOperator(getTextPaneValue()).equals(INFINITY)) {
                    isDividing = false;
                    valuesPosition = 0;
                } else {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE + buttonChoice);
                    resetCalculatorOperations(true);
                }
            } else if (!getTextPaneValue().isBlank() && values[0].isBlank()) {
                LOGGER.error("The user pushed divide but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",", "");
                appendTextToPane(values[0] + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isDividing = true;
                isFirstNumber = false;
                valuesPosition += 1;
            } else if (isOperatorActive()) {
                LOGGER.info("already chose an operator. choose another number.");
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic for dividing
     */
    public void divide()
    {
        logValues(this, LOGGER, 2);
        double result;
        if (!ZERO.equals(values[1]))
        {
            result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info("{} / {} = {}", values[0], values[1], result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(EQUALS, DIVISION, result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                appendTextToPane(values[valuesPosition]);
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(EQUALS, DIVISION, result, false);
                values[0] = addCommas(formatNumber(String.valueOf(result)));
            }
            confirm(this, LOGGER, "Finished dividing");
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO);
            appendTextToPane(INFINITY);
            values[0] = BLANK;
            values[1] = BLANK;
            isFirstNumber = true;
            confirm(this, LOGGER, "Attempted to divide by 0. Values[0] = 0");
        }
    }
    private void divide(String continuedOperation)
    {
        logValues(this, LOGGER, 2);
        double result;
        if (!ZERO.equals(values[1]))
        {
            result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info("{} / {} = {}", values[0], values[1], result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(continuedOperation, DIVISION, result, true);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                appendTextToPane(values[valuesPosition]);
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, DIVISION, result, true);
                values[0] = String.valueOf(result);
                buttonDecimal.setEnabled(false);
            }
            LOGGER.info("Finished " + DIVISION);
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO);
            appendTextToPane(INFINITY);
            values[0] = BLANK;
            values[1] = BLANK;
            isFirstNumber = true;
        }
    }

    /**
     * The beginning actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public void performNumberButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (!isFirstNumber && !isPemdasActive) // second number
        {
            LOGGER.debug("obtaining second number...");
            clearTextInTextPane();
            isFirstNumber = true;
            setIsNumberNegative(false);
        }
        if (performInitialChecks())
        {
            LOGGER.warn("Invalid entry in textPane. Clearing...");
            appendTextToPane(BLANK);
            values[valuesPosition] = BLANK;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        values[valuesPosition] += buttonChoice;
        if (isNumberNegative && !isNegativeNumber(values[valuesPosition])) values[valuesPosition] = SUBTRACTION + values[valuesPosition];
        appendTextToPane(addCommas(values[valuesPosition]));
        writeHistory(buttonChoice, false);
        confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
    }

    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public void performNegateButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = getTextPaneValue();
        if (textPaneContainsBadText() || isMaximumValue())
        { confirm(this, LOGGER, cannotPerformOperation(NEGATE)); }
        else if (textPaneValue.isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, "No value to negate");
        }
        else if (textPaneValue.contains(getActiveOperator()) && !getActiveOperator().equals(BLANK))
        {
            confirm(this, LOGGER, "Cannot negate when an operator is present");
        }
        else if (ZERO.equals(textPaneValue))
        { confirm(this, LOGGER, "Cannot negate zero"); }
        else
        {
            if (isNegativeNumber(getValueAtPosition()))
            {
                isNumberNegative = false;
                values[valuesPosition] = convertToPositive(textPaneValue);
                writeHistory(buttonChoice, false);
            }
            else
            {
                isNumberNegative = true;
                values[valuesPosition] = convertToNegative(textPaneValue);
                writeHistory(buttonChoice, false);
            }
            appendTextToPane(addCommas(values[valuesPosition]));
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }

    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public void performDecimalButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(DECIMAL)); }
        else
        {
            LOGGER.info("Basic dot operations");
            performDecimal(buttonChoice);
            writeHistory(buttonChoice, false);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic of performing Dot actions
     * @param buttonChoice the button choice
     */
    private void performDecimal(String buttonChoice)
    {
        final var appropriateValue = getValueAtPosition();
        if (appropriateValue.isBlank() && !isNumberNegative) // !isNegating
        {
            values[valuesPosition] = ZERO + DECIMAL;
            appendTextToPane(values[valuesPosition]);
        }
        else if (appropriateValue.isBlank() && isNumberNegative) // isNegating
        {
            values[valuesPosition] = SUBTRACTION + ZERO + DECIMAL;
            appendTextToPane(values[valuesPosition]);
        }
        else
        {
            values[valuesPosition] = appropriateValue + buttonChoice;
            appendTextToPane(addCommas(values[valuesPosition]));
        }
        buttonDecimal.setEnabled(false); // deactivate button now that its active for this number
    }

    /**
     * The actions to perform when the Equals button is clicked
     * @param actionEvent the click action
     */
    public void performEqualsButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (getValueAtPosition(1).isBlank()) {
            if (isAdding) {
                logFailedOperation(ADDITION, LOGGER);
                confirm(this, LOGGER, "Not performing " + buttonChoice);
                return;
            }
            else if (isSubtracting) {
                logFailedOperation(SUBTRACTION, LOGGER);
                confirm(this, LOGGER, "Not performing " + buttonChoice);
                return;
            }
            else if (isMultiplying) {
                logFailedOperation(MULTIPLICATION, LOGGER);
                confirm(this, LOGGER, "Not performing " + buttonChoice);
                return;
            }
            else if (isDividing) {
                logFailedOperation(DIVISION, LOGGER);
                confirm(this, LOGGER, "Not performing " + buttonChoice);
                return;
            }
            // TODO: Add programmer operators if needed
        }
        String operator = getActiveOperator();
        performMathOperation();
        if (!List.of(AND, XOR, OR).contains(operator)) {
            if (!operator.isEmpty() && !textPaneContainsBadText()) {
                switch (calculatorBase)
                {
                    case BASE_BINARY -> appendTextToPane(convertValueToBinary());
                    case BASE_OCTAL -> appendTextToPane(convertValueToOctal());
                    case BASE_DECIMAL -> appendTextToPane(values[0]);
                    case BASE_HEXADECIMAL -> appendTextToPane(convertValueToHexadecimal());
                }
            }
            resetValues();
        }
        confirm(this, LOGGER, pressedButton(buttonChoice));
    }
    /**
     * This method determines which operation to perform,
     * performs that operation, and resets the operator
     */
    private void performMathOperation()
    {
        if (isAdding)
        {
            addition(); // calling 'addition' versus 'performAdditionAction'
            setIsAdding(resetCalculatorOperations(isAdding));
        }
        else if (isSubtracting)
        {
            subtract(); // or 'subtraction' versus 'performSubtractionAction'
            setIsSubtracting(resetCalculatorOperations(isSubtracting));
        }
        else if (isMultiplying)
        {
            multiply(); // prevents confirm() from being printed twice
            setIsMultiplying(resetCalculatorOperations(isMultiplying));
        }
        else if (isDividing)
        {
            divide(); // from each operation in this performMathOperation method
            setIsDividing(resetCalculatorOperations(isDividing));
        }
        // add operations here
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            if (programmerPanel.isAnd())
            {
                String result = programmerPanel.performAnd();
                appendTextToPane(result);
                // TODO: Should AND replace values[0] and values[1]??
                writeContinuedHistory(EQUALS, AND, Double.parseDouble(result), false);
                programmerPanel.setAnd(false);
                resetCalculatorOperations(false);
            }
            else if (programmerPanel.isOr())
            {
                String orResult = programmerPanel.performOr();
                var resultInBase = !BASE_BINARY.equals(getCalculatorBase()) ?
                        convertFromBaseToBase(BASE_BINARY, getCalculatorBase(), orResult) : orResult;
                writeContinuedHistory(OR, OR, resultInBase, false);
                getValues()[0] = resultInBase;
                programmerPanel.setOr(false);
                switch (getCalculatorBase())
                {
                    case BASE_BINARY -> { appendTextToPane(orResult); }
                    case BASE_OCTAL -> { appendTextToPane(convertValueToOctal()); }
                    case BASE_DECIMAL -> { appendTextToPane(getValues()[0]); }
                    case BASE_HEXADECIMAL -> { appendTextToPane(convertValueToHexadecimal());}
                }
                resetCalculatorOperations(false);
            }
            else if (programmerPanel.isXor())
            {
                String xorResult = programmerPanel.performXor();
                var resultInBase = !BASE_BINARY.equals(calculatorBase) ?
                        convertFromBaseToBase(BASE_BINARY, calculatorBase, xorResult) : xorResult;
                writeContinuedHistory(OR, OR, resultInBase, false);
                getValues()[0] = resultInBase;
                switch (getCalculatorBase())
                {
                    case BASE_BINARY -> { appendTextToPane(xorResult); }
                    case BASE_OCTAL -> { appendTextToPane(convertValueToOctal()); }
                    case BASE_DECIMAL -> { appendTextToPane(getValues()[0]); }
                    case BASE_HEXADECIMAL -> { appendTextToPane(convertValueToHexadecimal());}
                }
                resetCalculatorOperations(false);
            }
        }
    }

    /**************** HELPER METHODS ****************/
    /**
     * Updates the look and feel of the Calculator
     */
    public void updateLookAndFeel()
    {
        SwingUtilities.updateComponentTreeUI(this);
        pack();
    }

    /**
     * This method returns true or false depending
     * on if an operator was pushed or not. The
     * operators checked are addition, subtraction,
     * multiplication, and division.
     *
     * @return true if any operator was pushed, false otherwise
     */
    public boolean isOperatorActive()
    {
        boolean basic = isAdding || isSubtracting || isMultiplying || isDividing;
        if (calculatorView == VIEW_PROGRAMMER) {
            return basic || programmerPanel.isOperatorActive();
        }
        return basic;
    }

    /**
     * This method returns true if no operator is active
     * independent of the current view.
     * @return true if no operator is active, false otherwise
     */
    public boolean isNoOperatorActive()
    {
        boolean basic = !isAdding && !isSubtracting && !isMultiplying && !isDividing;
        if (calculatorView == VIEW_PROGRAMMER) {
            return basic && programmerPanel.isNoOperatorActive();
        }
        return basic;
    }

    /**
     * This method returns the String operator that was activated
     * @return String the basic operation that was pushed
     */
    public String getActiveOperator()
    {
        String results = BLANK;
        if (isAdding) { results = ADDITION; }
        else if (isSubtracting) { results = SUBTRACTION; }
        else if (isMultiplying) { results = MULTIPLICATION; }
        else if (isDividing) { results = DIVISION; }
        if (calculatorView == VIEW_PROGRAMMER) {
            if (results.isEmpty()) {
                results = programmerPanel.getActiveProgrammerPanelOperator();
            }
        }
        LOGGER.info(results.isEmpty() ? "no operator pushed" : "operator: {}", results);
        return results;
    }

    /**
     * Records the buttonChoice to the appropriate history panel
     * Ex: Press 5, History shows: (5) Result: 5
     * Ex: Press +, History shows: (+) Result: 5 +
     * @param buttonChoice String the button choice
     */
    public void writeHistory(String buttonChoice, boolean addButtonChoiceToEnd)
    {
        writeHistoryWithMessage(buttonChoice, addButtonChoiceToEnd, null);
    }

    /**
     * Records the buttonChoice to the appropriate history panel
     * with a specific message instead of the default text
     * @param buttonChoice String the buttonChoice
     * @param addButtonChoiceToEnd boolean to add the buttonChoice to the end
     * @param message String the message to display
     */
    public void writeHistoryWithMessage(String buttonChoice, boolean addButtonChoiceToEnd, String message)
    {
        LOGGER.info("Writing history");
        if (null == message) {
            LOGGER.info("no message");
            switch (calculatorView) {
                case VIEW_BASIC -> {
                    var paneValue = addCommas(values[valuesPosition]);
                    if (addButtonChoiceToEnd) {
                        paneValue += SPACE + buttonChoice;
                    }
                    basicPanel.getHistoryTextPane().setText(
                        basicPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS
                        + " Result: " + paneValue
                    );
                }
                case VIEW_PROGRAMMER -> {
                    var paneValue = addCommas(values[valuesPosition]);
                    if (addButtonChoiceToEnd) {
                        paneValue += SPACE + buttonChoice;
                    }
                    programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS
                        + " Result: " + paneValue
                    );
                }
                default -> {
                    LOGGER.warn("Add other history panels");
                }
            }
        }
        else {
            LOGGER.info("with a specific message: {}", message);
            switch (calculatorView)
            {
                case VIEW_BASIC -> {
                    var paneValue = message;
                    if (addButtonChoiceToEnd) {
                        paneValue += SPACE + buttonChoice;
                    }
                    var basicHistoryText = basicPanel.getHistoryTextPane().getText();
                    basicPanel.getHistoryTextPane().setText(
                        basicHistoryText +
                        addNewLines(1) + LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS
                        + paneValue
                    );
                }
                case VIEW_PROGRAMMER -> {
                    var paneValue = message;
                    if (addButtonChoiceToEnd)
                    { paneValue += SPACE + buttonChoice; }
                    programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS
                        + SPACE + paneValue
                    );
                }
                default -> {
                    LOGGER.warn("Add other history panels");
                }
            }
        }
    }

    /**
     * Records the buttonChoice specifically if it was a continued operator
     * Ex: Already entered 5 + 6, then press +, History shows: (+) Result: 5 + 6 = 11 +
     * @param continuedOperation String whether to display the operation or equals
     * @param operation String the operation performed (add, subtract, etc)
     * @param result double the result from the operation
     * @param addContinuedOperationToEnd boolean whether to append the operation to the end
     */
    public void writeContinuedHistory(String continuedOperation, String operation, Object result, boolean addContinuedOperationToEnd)
    {
        LOGGER.info("Writing continued history");
        if (calculatorView == VIEW_BASIC)
        {
            var paneValue = addCommas(values[0]) + SPACE + operation + SPACE + addCommas(values[1]) + SPACE + EQUALS + SPACE + addCommas(String.valueOf(result));
            if (addContinuedOperationToEnd) {
                paneValue += SPACE + continuedOperation;
            }
            basicPanel.getHistoryTextPane().setText(
                basicPanel.getHistoryTextPane().getText() +
                addNewLines(1) + LEFT_PARENTHESIS + continuedOperation + RIGHT_PARENTHESIS
                + " Result: " + paneValue
            );
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            var paneValue = addCommas(values[0]) + SPACE + operation + SPACE + addCommas(values[1]) + SPACE + EQUALS + SPACE + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
            if (addContinuedOperationToEnd) {
                paneValue += SPACE + continuedOperation;
            }
            programmerPanel.getHistoryTextPane().setText(
                programmerPanel.getHistoryTextPane().getText() +
                addNewLines(1) + LEFT_PARENTHESIS + continuedOperation + RIGHT_PARENTHESIS
                + " Result: " + paneValue
            );
        }
    }

    /**
     * This method resets default values
     * Primarily used for testing purposes
     */
    public void resetValues()
    {
        values[0] = BLANK;
        values[1] = BLANK;
        values[2] = BLANK;
        values[3] = BLANK;
        valuesPosition = 0;
        buttonDecimal.setEnabled(true);
        setIsNumberNegative(false);
        resetOperators(false);
        LOGGER.debug("Reset values");
    }

    //TODO: Rethink name. It does too much. Combine with above
    /**
     * Resets values[1] to blank.
     * if true, vP = 1, else 0.
     * if true, buttonDecimal = true, else depends.
     * if true, isFirstNumber = false, else true.
     *
     * @param operatorBool the operator to press
     * @return boolean !operatorBool
     */
    public boolean resetCalculatorOperations(boolean operatorBool)
    {
        LOGGER.debug("Resetting calculator operations, operatorBool:{}", operatorBool);
        values[1] = BLANK;
        if (operatorBool)
        {
            valuesPosition = 1;
            isFirstNumber = false;
            buttonDecimal.setEnabled(true); // !isDecimal(values[0])
            return false;
        }
        else
        {
            valuesPosition = 0;
            isFirstNumber = true;
            buttonDecimal.setEnabled(!isDecimalNumber(values[0]));
            return true;
        }
    }

    /**
     * Determines if any value is saved in memory
     * @return boolean if any memory value has a value
     */
    public boolean isMemoryValuesEmpty()
    {
        var result = true;
        for (int i = 0; i < 10; i++)
        {
            if (!memoryValues[i].isBlank())
            {
                result = false;
                break;
            }
        }
        if (result) LOGGER.debug("memoryValues is empty");
        return result;
    }

    /**
     * Returns the lowest position in memory that contains a value
     */
    public int getLowestMemoryPosition()
    {
        var lowestMemory = 0;
        for (int i = 0; i < 10; i++) {
            if (!memoryValues[i].isBlank()) {
                lowestMemory = i;
                break;
            }
        }
        LOGGER.debug("Lowest position in memory is {}", lowestMemory);
        return lowestMemory;
    }

    /**
     * Returns all the number buttons
     * @return Collection of all number buttons
     */
    public List<JButton> getNumberButtons()
    { return Arrays.asList(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9); }

    /**
     * Clears all actions from the number buttons
     */
    public void clearNumberButtonActions()
    {
        getNumberButtons()
                .forEach(button -> Arrays.stream(button.getActionListeners())
                        .forEach(button::removeActionListener));
        LOGGER.debug("Number buttons cleared of action listeners");
    }

    /**
     * Returns all the memory buttons and the history button
     * @return List of buttons in the memory panel
     */
    public List<JButton> getAllMemoryPanelButtons()
    { return Arrays.asList(buttonMemoryStore, buttonMemoryClear, buttonMemoryRecall, buttonMemoryAddition, buttonMemorySubtraction, buttonHistory); }

    /**
     * Clears all actions from the number buttons
     */
    public void clearMemoryButtonActions()
    {
        getAllMemoryPanelButtons()
                .forEach(button -> Arrays.stream(button.getActionListeners())
                        .forEach(button::removeActionListener));
        LOGGER.debug("Number buttons cleared of action listeners");
    }

    /**
     * Returns the commonly used buttons. This includes:
     * Percent, SquareRoot, Squared, Fraction, ClearEntry, Clear,
     * Delete, Divide, Multiply, Subtract, Addition, Negate, Dot,
     * and Equals.
     * It does not include the number buttons.
     *
     * @return Collection of commonly used buttons
     */
    public List<JButton> getCommonButtons()
    {
        return Arrays.asList(buttonPercent, buttonSquareRoot, buttonSquared, buttonFraction,
                buttonClearEntry, buttonClear, buttonDelete, buttonDivide, buttonMultiply,
                buttonSubtract, buttonAdd, buttonNegate, buttonDecimal, buttonEquals);
    }

    /**
     * Clears all actions from the buttons
     * other than the numbers buttons
     */
    public void clearAllCommonButtons()
    {
        getCommonButtons()
            .forEach(button -> Arrays.stream(button.getActionListeners())
                    .forEach(button::removeActionListener));
        LOGGER.debug("AllBasicPanelButtons cleared of action listeners");
    }

    /**
     * Clears all actions from all buttons
     */
    public void clearButtonActions()
    {
        clearButtonActions(getCommonButtons());
        LOGGER.debug("Common Buttons cleared of action listeners");
        clearButtonActions(getNumberButtons());
        LOGGER.debug("Number Buttons cleared of action listeners");
        clearButtonActions(getAllMemoryPanelButtons());
        LOGGER.debug("Memory Panel Buttons cleared of action listeners");
    }

    /**
     * Clears all actions from the given buttons
     * @param buttons the buttons to clear
     */
    private void clearButtonActions(List<JButton> buttons)
    {
        buttons.forEach(button -> Arrays.stream(button.getActionListeners())
                .forEach(button::removeActionListener));
    }

    /**
     * Tests whether a number has the "." symbol in it or not
     *
     * @param number the number to test
     * @return boolean if the given number contains a decimal
     */
    public boolean isDecimalNumber(String number)
    {
        LOGGER.debug("isDecimalNumber({}): {}", number, number.contains(DECIMAL));
        return number.contains(DECIMAL);
    }

    /**
     * When we hit a number button this method is called
     * to ensure valid entries are allowed and any previous
     * errors or unexpected conditions are cleared
     */
    public boolean performInitialChecks()
    {
        LOGGER.debug("Performing initial checks...");
        if (ZERO.equals(getTextPaneValue()))
        {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            appendTextToPane(BLANK);
            values[valuesPosition] = BLANK;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        boolean issueFound = false;
        if (textPaneContainsBadText())
        {
            LOGGER.debug("TextPane contains bad text");
            issueFound = true;
        }
        else if (getValueAtPosition(0).isEmpty() && valuesPosition == 1)
        {
            LOGGER.debug("v[0] is empty. placing v[1] in v[0]. clearing v[1].");
            values[0] = values[1];
            values[1] = BLANK;
            issueFound = true;
        }
        else if (checkValueLength())
        {
            LOGGER.info("Highest size of value has been met");
            issueFound = true;
        }
        LOGGER.debug("Initial checks result: {}", issueFound);
        return issueFound;
    }

    /**
     * Checks to see if the max length of a value[position] has been met.
     * The highest number is 9,999,999, with a length of 7 and the
     * lowest number is 0.0000001. So if the current value has a
     * length of 7, the max length has been met, and no more
     * digits will be allowed. Remember that the values[position] will not
     * contain any commas, so the official length will be 7.
     * @return boolean if the max length has been met
     */
    private boolean checkValueLength()
    {
        LOGGER.debug("Checking if max size (7) is met...");
        LOGGER.debug("values[{}] left of decimal length: {}", valuesPosition, getNumberOnLeftSideOfDecimal(getValueAtPosition()).length());
        LOGGER.debug("values[{}] right of decimal length: {}", valuesPosition, getNumberOnRightSideOfDecimal(getValueAtPosition()).length());
        return getNumberOnLeftSideOfDecimal(getValueAtPosition()).length() > 7 ||
               getNumberOnRightSideOfDecimal(getValueAtPosition()).length() > 7 ;
    }

    /**
     * Tests whether the textPane contains a String which shows a previous error.
     * Takes into account the current panel
     * @return boolean if textPane contains more than just a number
     */
    public boolean textPaneContainsBadText()
    {
        String val = getTextPaneValue();
        boolean result = false;
        if (BLANK.equals(val)) return result;
        else {
            result = CANNOT_DIVIDE_BY_ZERO.equals(val) ||
                     NOT_A_NUMBER.equals(val) ||
                     NUMBER_TOO_BIG.equals(val) ||
                     ENTER_A_NUMBER.equals(val) ||
                     ONLY_POSITIVES.equals(val) ||
                     Texts.ERROR.equals(val) ||
                     INFINITY.equals(val);
        }
        if (result) LOGGER.debug("textPane contains bad text: {}", val);
        else LOGGER.debug("textPane is clean. text is '{}'", val);
        return result;
    }

    /**
     * Returns the bad text in the textPane
     * @return String the bad text
     */
    public String getBadText()
    {
        String badText = BLANK;
        if (CANNOT_DIVIDE_BY_ZERO.equals(getTextPaneValue())) badText = CANNOT_DIVIDE_BY_ZERO;
        else if (NOT_A_NUMBER.equals(getTextPaneValue())) badText = NOT_A_NUMBER;
        else if (NUMBER_TOO_BIG.equals(getTextPaneValue())) badText = NUMBER_TOO_BIG;
        else if (ENTER_A_NUMBER.equals(getTextPaneValue())) badText = ENTER_A_NUMBER;
        else if (ONLY_POSITIVES.equals(getTextPaneValue())) badText = ONLY_POSITIVES;
        else if (Texts.ERROR.equals(getTextPaneValue())) badText = Texts.ERROR;
        else if (INFINITY.equals(getTextPaneValue())) badText = INFINITY;
        return badText;
    }

    /**
     * Adds the delimiter to the number if appropriate
     * Works by reversing the string value. Finding the
     * appropriate decimal index, it will then add a
     * delimiter every three characters.
     * @param valueToAdjust the passed in value
     * @return the value with the delimiter chosen
     */
    public String addCommas(String valueToAdjust)
    {
        String delimiter = getThousandsDelimiter();
        if (valueToAdjust.contains(delimiter)) return valueToAdjust;
        if (valueToAdjust.isBlank()) return valueToAdjust;
        if (isNegativeNumber(valueToAdjust)) {
            valueToAdjust = getValueWithoutAnyOperator(valueToAdjust);
        }
        if (!isDecimalNumber(valueToAdjust) && valueToAdjust.length() <= 3) {
            if (isNumberNegative) return SUBTRACTION + valueToAdjust;
            else return valueToAdjust;
        }
        else {
            String numberOnLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            if (numberOnLeft.length() <= 3) {
                if (isNumberNegative) return SUBTRACTION + valueToAdjust;
                else return valueToAdjust;
            }
        }
        LOGGER.debug("Adding delimiter: '{}' to '{}'", delimiter, valueToAdjust);
        // Possible decimal number: 05.4529 or 4529
        StringBuffer adjusted = new StringBuffer();
        StringBuffer reversed = new StringBuffer(valueToAdjust).reverse();
        int indexOfDecimal = reversed.indexOf(DECIMAL);
        //if (indexOfDecimal == -1) indexOfDecimal = 0;
        LOGGER.debug("index of decimal: {}", indexOfDecimal);
        if (indexOfDecimal > 0) adjusted.append(reversed.substring(0, indexOfDecimal+1));
        if (indexOfDecimal == 0) adjusted.append(DECIMAL);
        for (int i=indexOfDecimal+1; i<reversed.length(); i+=3)
        {
            if (i % 3 == 0 && i != 0)
            {
                if (!adjusted.toString().endsWith(DECIMAL)) adjusted.append(getThousandsDelimiter());
                if (i+1 > reversed.length() || i+2 > reversed.length() || i+3 > reversed.length())
                {
                    adjusted.append(reversed.substring(i));
                }
                else
                {
                    adjusted.append(reversed.substring(i, i+3));
                }
            }
            else
            {
                if (!adjusted.toString().endsWith(DECIMAL)) adjusted.append(getThousandsDelimiter());
                if (i+1 > reversed.length() || i+2 > reversed.length() || i+3 > reversed.length())
                {
                    adjusted.append(reversed.substring(i));
                }
                else
                {
                    adjusted.append(reversed.substring(i, i+3));
                }
            }
        }
        adjusted = new StringBuffer(adjusted).reverse();
        if (adjusted.toString().endsWith(COMMA)) adjusted = new StringBuffer(adjusted.substring(0, adjusted.length() - 1));
        if (isNumberNegative) adjusted = new StringBuffer(SUBTRACTION).append(adjusted);
        LOGGER.debug("commas added: {}", adjusted);
        return adjusted.toString();
    }

    /**
     * Removes commas from the number if appropriate
     * @param valueToAdjust the number to adjust
     * @return the value without commas
     */
    public String removeCommas(String valueToAdjust)
    {
        return valueToAdjust.replace(COMMA, BLANK);
    }

    /**
     * Returns the delimiter used to separate thousands
     * @return the delimiter to use for thousands
     */
    public String getThousandsDelimiter()
    {
        // TODO: Setup menu option to allow user to choose this value
        return COMMA;
    }

    /**
     * Returns the delimiter used to separate fractional values
     * @return the delimiter to use for fractional values
     */
    public String getFractionalDelimiter()
    {
        // TODO: Setup menu option to allow user to choose this value
        return DECIMAL;
    }

    /**
     * Takes an Exception and prints it to LOGGER.error
     *
     * @param e the Exception to log
     */
    public void logException(Exception e)
    { LOGGER.error(e.getClass().getSimpleName() + ": " + e.getMessage()); }

    /**
     * Determines if the OS is Mac or not
     *
     * @return boolean if is running on Mac
     */
    public boolean isOSMac()
    {
        LOGGER.debug("OS Name: {}", System.getProperty("os.name"));
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Adds the appropriate number of zeroes to the
     * beginning portion of a binary number
     */
    public String adjustBinaryNumber(String base2Number)
    {
        int base2Length = base2Number.length();
        int bits = determineBits(base2Length);
        int addZeroes = bits - base2Length;
        base2Number = ZERO.repeat(addZeroes) + base2Number;
        return base2Number;
    }

    // TODO: When Byte, if base2Length is 10, it is because
    // the value is 0011 1100 +. Figure out how to add that
    // here because if it's just the binary and an operator,
    // then that length of 10 is also ok
    /**
     * Determines the appropriate length when
     * converting a number to binary
     */
    public int determineBits(int base2Length)
    {
        var bits = 0;
        if ( calculatorByte == BYTE_BYTE ) bits = 8;
        else if ( calculatorByte == BYTE_WORD ) bits = 16;
        else if ( calculatorByte == BYTE_DWORD ) bits = 32;
        else if ( calculatorByte == BYTE_QWORD ) bits = 64;
        if (bits < base2Length) {
            LOGGER.debug("base2Length: {}", base2Length);
            if (base2Length <= 8) {
                bits = 8;
                setCalculatorByte(BYTE_BYTE);
            }
            else if (base2Length <= 16) {
                bits = 16;
                setCalculatorByte(BYTE_WORD);
            }
            else if (base2Length <= 32) {
                bits = 32;
                setCalculatorByte(BYTE_DWORD);
            }
            else { //if (base2Length <= 64) {
                bits = 64;
                setCalculatorByte(BYTE_QWORD);
            }
        }
        return bits;
    }

    /**
     * Returns values[valuesPosition]
     * @return the value at valuesPosition
     */
    public String getValueAtPosition()
    {
        return values[valuesPosition];
    }

    /**
     * Returns values[vP] for the given position
     * @param vP the position to get
     * @return the value at vP
     */
    public String getValueAtPosition(int vP) { return values[vP]; }

    /**
     * Converts the current value to its binary representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToBinary()
    {
        String valueToConvert = getValueAtPosition();
        if (valueToConvert.isEmpty()) return BLANK;
        String base2Number = Integer.toBinaryString(Integer.parseInt(valueToConvert));
        base2Number = adjustBinaryNumber(base2Number);
        LOGGER.info("Converting {}({}) to {}({})", BASE_DECIMAL.getValue(), valueToConvert,
                BASE_BINARY.getValue(), base2Number);
        return base2Number;
    }

    /**
     * Converts the current value to its octal representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToOctal()
    {
        String valueToConvert = getValueAtPosition();
        if (valueToConvert.isEmpty()) return BLANK;
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_OCTAL.getValue());
        String base8Number = Integer.toOctalString(Integer.parseInt(valueToConvert));
        LOGGER.debug("convert from({}) to({}) = {}", BASE_DECIMAL.getValue(), BASE_OCTAL.getValue(), base8Number);
        LOGGER.info("The number {} in base 10 is {} in base 8.", valueToConvert, base8Number);
        return base8Number;
    }

    /**
     * Converts the current value to its decimal representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToDecimal()
    {
        String valueToConvert = getValueAtPosition();
        if (valueToConvert.isEmpty()) return BLANK;
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_DECIMAL.getValue());
        String base10Number = Integer.toString(Integer.parseInt(valueToConvert, getPreviousRadix()), 10);
        LOGGER.debug("convert from({}) to({}) = {}", previousBase.getValue(), BASE_DECIMAL.getValue(), base10Number);
        LOGGER.info("The number {} in base {} is {} in base 10", valueToConvert, previousBase.getRadix(), base10Number);
        return base10Number;
    }

    /**
     * Converts the current value to its hexadecimal representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToHexadecimal()
    {
        String valueToConvert = getValueAtPosition();
        if (valueToConvert.isEmpty()) return BLANK;
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_HEXADECIMAL.getValue());
        String base16Number = Integer.toHexString(Integer.parseInt(valueToConvert));
        LOGGER.debug("convert from({}) to({}) = {}", BASE_DECIMAL.getValue(), BASE_HEXADECIMAL.getValue(), base16Number);
        LOGGER.info("The number {} in base 10 is {} in base 16.", valueToConvert, base16Number);
        return base16Number;
    }

    /**
     * Converts the given value from the fromBase to
     * the given toBase.
     * @param fromBase the base to convert from
     * @param toBase the base to convert to
     * @param valueToConvert the value to convert
     * @return the converted value or an empty string
     */
    public String convertFromBaseToBase(CalculatorBase fromBase, CalculatorBase toBase, String valueToConvert)
    {
        valueToConvert = valueToConvert.replace(COMMA, BLANK).replace(SPACE, BLANK);
        if (valueToConvert.isEmpty()) return BLANK;
        LOGGER.debug("converting {} from {} to {}", valueToConvert, fromBase.getValue(), toBase.getValue());
        String convertedNumber;
//        if (!isDecimalNumber(valueToConvert))
//            convertedNumber = Integer.toString(BigInteger.parseInt(valueToConvert, getPreviousRadix(fromBase)), getPreviousRadix(toBase));
//        else {
//            var value = clearZeroesAndDecimalAtEnd(valueToConvert);
//            convertedNumber = Integer.toString(Integer.parseInt(value, getPreviousRadix(fromBase)), getPreviousRadix(toBase));
//        }
        if (!isDecimalNumber(valueToConvert)) {
            // use BigInteger for arbitrarily large integers
            convertedNumber = new BigInteger(valueToConvert, getPreviousRadix(fromBase))
                    .toString(getPreviousRadix(toBase));
        }
        else {
            // handle fractional values: split into integer and fractional parts
            String[] parts = valueToConvert.split(DECIMAL);
            String intPartStr = parts.length > 0 && !parts[0].isEmpty() ? parts[0] : ZERO;
            BigInteger intPart = new BigInteger(intPartStr, getPreviousRadix(fromBase));
            String intConverted = intPart.toString(getPreviousRadix(toBase));

            String fracConverted = BLANK;
            if (parts.length > 1 && !parts[1].isEmpty()) {
                // Convert fractional part from 'fromBase' to a BigDecimal value
                java.math.BigDecimal frac = java.math.BigDecimal.ZERO;
                java.math.BigDecimal baseFrom = java.math.BigDecimal.valueOf(getPreviousRadix(fromBase));
                for (int i = 0; i < parts[1].length(); i++) {
                    int digit = Character.digit(parts[1].charAt(i), getPreviousRadix(fromBase));
                    if (digit < 0) { digit = 0; }
                    frac = frac.add(java.math.BigDecimal.valueOf(digit)
                            .divide(baseFrom.pow(i + 1), 64, java.math.RoundingMode.DOWN));
                }
                // Convert fractional BigDecimal into target base digits (fixed precision)
                int precision = 20;
                StringBuilder sb = new StringBuilder();
                java.math.BigDecimal baseTo = java.math.BigDecimal.valueOf(getPreviousRadix(toBase));
                java.math.BigDecimal current = frac;
                for (int i = 0; i < precision && current.compareTo(java.math.BigDecimal.ZERO) > 0; i++) {
                    current = current.multiply(baseTo);
                    int digit = current.intValue();
                    sb.append(Character.forDigit(digit, getPreviousRadix(toBase)));
                    current = current.subtract(new java.math.BigDecimal(digit));
                }
                fracConverted = sb.toString();
            }
            convertedNumber = intConverted + (fracConverted.isEmpty() ? BLANK : (DECIMAL + fracConverted));
        }
        if (BASE_BINARY == toBase) {
            convertedNumber = adjustBinaryNumber(convertedNumber);
        }
        LOGGER.debug("converted: {}", convertedNumber);
        return convertedNumber;
    }

    /**
     * Returns the previous radix based on the previousBase
     * @return the previous radix
     */
    public int getPreviousRadix()
    { return getPreviousRadix(previousBase); }

    public int getPreviousRadix(CalculatorBase base)
    {
        return switch (base)
        {
            case BASE_BINARY -> BASE_BINARY.getRadix();
            case BASE_OCTAL -> BASE_OCTAL.getRadix();
            case BASE_DECIMAL -> BASE_DECIMAL.getRadix();
            case BASE_HEXADECIMAL -> BASE_HEXADECIMAL.getRadix();
        };
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
        LOGGER.debug("ClearZeroesAndDot: {}", currentNumber);
        if (currentNumber.contains(Texts.ERROR)) return currentNumber;
        int index;
        index = currentNumber.indexOf(DECIMAL); // -1 if not found
        LOGGER.debug("decimalIndex: {}", index);
        if (index == -1) return currentNumber;
        else
        {
            getNumberOnLeftSideOfDecimal(currentNumber);
            String toTheRight = getNumberOnRightSideOfDecimal(currentNumber);
            String expected = ZERO.repeat(toTheRight.length());
            boolean allZeroes = expected.equals(toTheRight);
            if (allZeroes) currentNumber = currentNumber.substring(0,index);
        }
        LOGGER.debug("ClearZeroesAndDot Result: {}", currentNumber);
        return currentNumber;
    }

    /**
     * Returns the text in the textPane without
     * any operator.
     * @return the plain textPane text
     */
    public String getValueWithoutAnyOperator(String valueToAdjust)
    {
        return valueToAdjust
                .replace(ADDITION, BLANK) // target, replacement
                .replace(SUBTRACTION, BLANK)
                .replace(MULTIPLICATION, BLANK)
                .replace(DIVISION, BLANK)
                .replace(MODULUS, BLANK)
                //.replace(LEFT_PARENTHESIS, BLANK)
                //.replace(RIGHT_PARENTHESIS, BLANK)
                .replace(ROL, BLANK)
                .replace(ROR, BLANK)
                .replace(XOR, BLANK) // XOR must be checked before OR, otherwise OR will be replaced when XOR is there, leaving X, which is not desired
                .replace(OR, BLANK)
                .replace(AND, BLANK)
                .strip();
    }

    /**
     * Used when you want the text pane value without
     * having to think about which panel is current or
     * anything else for that matter. Just the value.
     * @return the value without new lines or whitespace
     */
    public String getTextPaneValue()
    {
        if (calculatorView == VIEW_BASIC)
        { return textPane.getText()
                .replaceAll(NEWLINE, BLANK)
                .strip(); }
        else if (calculatorView == VIEW_PROGRAMMER)
        { return getTextPaneValueForProgrammerPanel()
                .replaceAll(NEWLINE, BLANK)
                .strip();
        }
        else
        {
            LOGGER.warn("Implement");
            return BLANK;
        }
    }

    /**
     * Returns the value in the text pane when panel is programmer panel.
     * Value in text pane for programmer panel is as follows:
     * Byte Space Space Base NEWLINE NEWLINE value[valuePosition] NEWLINE
     * @return the value in the programmer text pane
     */
    private String getTextPaneValueForProgrammerPanel()
    {
        String currentValue = BLANK;
        try
        {
            LOGGER.debug("calculatorBase: {}", calculatorBase);
            switch (calculatorBase)
            {
                case BASE_BINARY ->
                {
                    LOGGER.debug("calculatorByte: {}", calculatorByte);
                    switch (calculatorByte)
                    {
                        case BYTE_BYTE,
                             BYTE_WORD -> {
                            currentValue = textPane.getText().split(NEWLINE)[2]
                                    .replace(COMMA, BLANK);
                        }
                        case BYTE_DWORD -> {
                            currentValue = textPane.getText().split(NEWLINE)[2]
                                    + textPane.getText().split(NEWLINE)[3]
                                    .replace(COMMA, BLANK);
                        }
                        case BYTE_QWORD -> {
                            currentValue = textPane.getText().split(NEWLINE)[2]
                                    + textPane.getText().split(NEWLINE)[3]
                                    + textPane.getText().split(NEWLINE)[4]
                                    + textPane.getText().split(NEWLINE)[5]
                                    .replace(COMMA, BLANK);
                        }
                    }
                    currentValue = currentValue.replace(SPACE, BLANK);
                    // TODO: Determine where this was used, then move method to proper location
                    //currentValue = adjustBinaryNumber(currentValue);
                }
                case BASE_OCTAL,
                     BASE_DECIMAL,
                     BASE_HEXADECIMAL -> {
                    currentValue = removeCommas(textPane.getText().split(NEWLINE)[2]);
                }
            }
            if (currentValue.isEmpty()) {
                LOGGER.warn("Attempted to retrieve value from text pane but it was empty. Returning blank.");
                return BLANK;
            }
            // TODO: If you want the value without any operator, use values[valuesPosition]
//            if (isOperatorActive() || programmerPanel.isProgrammerOperatorActive())
//            {
//                LOGGER.debug("Removing operator from currentValue: {}", currentValue);
//                currentValue = getValueWithoutAnyOperator(currentValue);
//            }
        }
        catch (ArrayIndexOutOfBoundsException ae1)
        {
            try
            {
                var splitTextValue = textPane.getText().split(NEWLINE);
                // TODO: Rework to not use Strings
                var twoSpaces = SPACE.repeat(2);
                var textPaneTopRowExpectedValues = List.of(
                BYTE_BYTE.getValue()+twoSpaces+BASE_BINARY.getValue(), "Byte  Octal", "Byte  Decimal", "Byte  Hexadecimal"
                        , "Word  Binary", "Word  Octal", "Word  Decimal", "Word  Hexadecimal"
                        , "DWord  Binary", "DWord  Octal", "DWord  Decimal", "DWord  Hexadecimal"
                        , "QWord  Binary", "QWord  Octal", "QWord  Decimal", "QWord  Hexadecimal");
                return splitTextValue.length == 1 && textPaneTopRowExpectedValues.contains(splitTextValue[0])
                        ? BLANK
                        : splitTextValue[2].replace(COMMA, BLANK);
            } catch (ArrayIndexOutOfBoundsException ae2)
            {
                logException(new CalculatorError("Attempted to retrieve value from text pane but got ArrayIndexOutOfBoundsException. Returning blank."));
                return BLANK;
            }
        }
        catch (Exception e)
        {
            logException(e);
            return BLANK;
        }
        return currentValue;
    }

    /**
     * Returns the text in the current panel's history pane
     * @return the history text for the current panel
     */
    public String getBasicHistoryPaneTextWithoutNewLineCharacters()
    {
        if (calculatorView == VIEW_BASIC)
        { return basicPanel.getHistoryTextPane().getText().replace(addNewLines(1), BLANK).strip(); }
        else if (calculatorView == VIEW_PROGRAMMER)
        { return programmerPanel.getHistoryTextPane().getText().replace(addNewLines(1), BLANK).strip(); }
        else
        {
            LOGGER.warn("Add other panels history pane if intended to have one");
            return BLANK;
        }
    }

    /**
     * Adds a specific newLinesNumber of newLine characters
     * @param newLinesNumber int the newLinesNumber of newLine characters to add
     * @return String representing newLine characters
     */
    public String addNewLines(int newLinesNumber)
    {
        String newLines = null;
        if (newLinesNumber == 0)
        {
            if (currentPanel instanceof BasicPanel) newLines = NEWLINE.repeat(1);
            else if (currentPanel instanceof ProgrammerPanel) newLines = NEWLINE.repeat(1);
            else if (currentPanel instanceof ScientificPanel) newLines = NEWLINE.repeat(3);
        }
        else
        { newLines = NEWLINE.repeat(newLinesNumber); }
        return newLines;
    }

    /**
     * Adds the appropriate amount of newline characters
     * based on the currentPanel
     *
     * @return the newline character repeated 1 to n times
     */
    public String addNewLines()
    { return addNewLines(0); }

    /**
     * Returns the text to display in About Calculator
     * @return String the About Calculator text
     */
    public String getAboutCalculatorString()
    {
        LOGGER.debug("Configuring " + ABOUT_CALCULATOR + " text...");
        String computerText = "", version = "";
        if (isOSMac()) { computerText = APPLE; }
        else                        { computerText = WINDOWS; }
        try(InputStream is = CalculatorMain.class.getResourceAsStream("/pom.properties"))
        {
            Properties p = new Properties();
            p.load(is);
            version = p.getProperty("project.version");
        }
        catch (IOException | NullPointerException e) { logException(e); }
        return """
                <html> %s <br>
                Calculator Version %s<br>
                © %d All rights reserved.<br><br>
                %s and its user interface are protected by trademark and all other<br>
                pending or existing intellectual property right in the United States and other<br>
                countries/regions.
                <br><br>
                This product is licensed under the License Terms to:<br>
                Michael Ball<br>
                Github: https://github.com/aaronhunter1088/Calculator</html>
                """
                .formatted(computerText,
                        version,
                        LocalDate.now().getYear(),
                        System.getProperty("os.name"));
    }

    /**
     * Displays the help text in a scrollable pane
     * @param helpString the help text to display
     */
    public void showHelpPanel(String helpString)
    {
        JTextArea message = new JTextArea(helpString,20,40);
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(message, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(new Dimension(400, 300));
        SwingUtilities.updateComponentTreeUI(this);
        JOptionPane.showMessageDialog(this, scrollPane, "Viewing " + calculatorView.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        confirm(this, LOGGER, "Viewing " + calculatorView.getValue() + " Calculator Help");
    }

    /**
     * Determines whether a number is positive
     *
     * @param number the value to test
     * @return either true or false based on result
     */
    public boolean isPositiveNumber(String number)
    {
        LOGGER.debug("isPositiveNumber({}): {}", number, !number.contains(SUBTRACTION));
        return !number.contains(SUBTRACTION);
    }

    /**
     * Determines whether a number is negative
     *
     * @param number the value to test
     * @return either true or false based on result
     */
    public boolean isNegativeNumber(String number)
    {
        LOGGER.debug("isNegativeNumber({}): {}", number, number.startsWith(SUBTRACTION));
        return number.startsWith(SUBTRACTION);
    }

    /**
     * Converts a number to its negative equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.debug("Converting {} to negative number", number.replaceAll(NEWLINE, BLANK));
        if (!number.contains(SUBTRACTION))
            number = SUBTRACTION + number.replaceAll(NEWLINE, BLANK);
        isNumberNegative = true;
        LOGGER.debug("Updated: {}", number);
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.debug("Converting {} to positive number", number.replaceAll(NEWLINE, BLANK));
        number = number.replaceAll(SUBTRACTION, BLANK).trim();
        isNumberNegative = false;
        LOGGER.debug("Updated: {}", number);
        return number;
    }

    /**
     * Resets the operators to the boolean passed in
     * @param reset a boolean to reset the operators to
     */
    public void resetOperators(boolean reset)
    {
        isAdding = reset;
        isSubtracting = reset;
        isMultiplying = reset;
        isDividing = reset;
        isPemdasActive = reset;
        setIsFirstNumber(reset);
        if (calculatorView == VIEW_PROGRAMMER)
        {
            programmerPanel.setAnd(reset);
            programmerPanel.setOr(reset);
            programmerPanel.setXor(reset);
            programmerPanel.setModulus(reset);
        }
        LOGGER.debug("All operators reset to {}", reset);
    }

    /**
     * Returns the number representation to the left of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the left of the decimal
     */
    public String getNumberOnLeftSideOfDecimal(String currentNumber)
    {
        String leftSide;
        if (!isDecimalNumber(currentNumber)) return currentNumber;
        int index = currentNumber.indexOf(DECIMAL);
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = BLANK;
        else
        {
            leftSide = currentNumber.substring(0, index);
            if (StringUtils.isEmpty(leftSide)) {
                LOGGER.debug("Number to the left of the decimal is empty. Setting to {}", ZERO);
                leftSide = ZERO;
            }
        }
        return leftSide;
    }

    /**
     * Returns the number representation to the right of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the right of the decimal
     */
    protected String getNumberOnRightSideOfDecimal(String currentNumber)
    {
        String rightSide;
        int index = currentNumber.indexOf(DECIMAL);
        if (index < 0 || (index + 1) >= currentNumber.length()) rightSide = BLANK;
        else
        {
            rightSide = currentNumber.substring(index + 1);
            if (StringUtils.isEmpty(rightSide)) {
                LOGGER.debug("Number to the right of the decimal is empty. Setting to {}", ZERO);
                rightSide = ZERO;
            }
        }
        return rightSide;
    }

    /**
     * Formats the number based on the length
     * @param numberToFormat the number to format
     * @return String the number formatted
     */
    public String formatNumber(String numberToFormat)
    {
        DecimalFormat df = null;
        LOGGER.debug("Number to format: {}", numberToFormat);
        if (!isNumberNegative)
        {
            if (numberToFormat.length() <= 2) df = new DecimalFormat("0.00");
            if (numberToFormat.length() >= 3) df = new DecimalFormat("0.00");
        }
        else
        {
            if (numberToFormat.length() <= 3) df = new DecimalFormat("0.0");
            if (numberToFormat.length() == 4) df = new DecimalFormat("0.00");
            if (numberToFormat.length() >= 5) df = new DecimalFormat("0.000");
        }
        double number = Double.parseDouble(numberToFormat);
        number = Double.parseDouble(df.format(number));
        String numberAsStr = Double.toString(number);
        numberToFormat = df.format(number);
        if ('.' == numberAsStr.charAt(numberAsStr.length() - 3) && numberAsStr.endsWith(".00"))
        {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length() - 3);
            LOGGER.warn("Formatted again: {}", numberToFormat);
        }
        LOGGER.debug("Formatted: {}", numberToFormat);
        return numberAsStr;
    }

    /**
     * Resets the look and feel booleans to false
     */
    public void resetLook()
    {
        isMetal = false;
        isSystem = false;
        isWindows = false;
        isMotif = false;
        isGtk = false;
        isApple = false;
        LOGGER.debug("Reset all Look booleans");
    }

    /**
     * Returns true if the minimum value has
     * been met or false otherwise
     * 0.0000001 or 10^-7
     * @return boolean is minimum value has been met
     */
    public boolean isMinimumValue()
    {
        boolean v1 = false, v2 = false;
        if (valuesPosition == 1)
        {
            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) <= Double.parseDouble(MIN_VALUE);
            LOGGER.debug("is v[0]: '{}' <= {}: {}", values[0], MIN_VALUE, v1);
            v2 = !values[1].isEmpty() && Double.parseDouble(values[1]) <= Double.parseDouble(MIN_VALUE);
            LOGGER.debug("is v[1]: '{}' <= {}: {}", values[1], MIN_VALUE, v2);
            return v1 || v2;
        }
        else
        {
            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) <= Double.parseDouble(MIN_VALUE);
            LOGGER.debug("is v[0]: '{}' <= {}: {}", values[0], MIN_VALUE, v1);
            return v1;
        }
    }

    /**
     * Checks if the resulting answer has met the minimum value
     * @param valueToCheck String the value to check
     * @return boolean true if the minimum value has been met
     */
    public boolean isMinimumValue(String valueToCheck)
    {
        boolean v1 = !valueToCheck.isEmpty() && Double.parseDouble(valueToCheck) <= Double.parseDouble(MIN_VALUE);
        LOGGER.debug("is value: '{}' <= {}: {}", valueToCheck, MIN_VALUE, v1);
        return v1;
    }

    /**
     * Returns true if the maximum value has
     * been met or false otherwise
     * 9,999,999 or (10^8) -1
     * @return boolean if maximum value has been met
     */
    public boolean isMaximumValue()
    {
        boolean v1 = false, v2 = false;
        if (valuesPosition == 1) {
            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) >= Double.parseDouble(MAX_VALUE);
            LOGGER.debug("is v[0]: '{}' >= {}: {}", values[0], MAX_VALUE, v1);
            v2 = !values[1].isEmpty() && Double.parseDouble(values[1]) >= Double.parseDouble(MAX_VALUE);
            LOGGER.debug("is v[1]: '{}' >= {}: {}", values[1], MAX_VALUE, v2);
        }
        else {
            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) >= Double.parseDouble(MAX_VALUE);
            LOGGER.debug("is v[0]: '{}' >= {}: {}", values[0], MAX_VALUE, v1);

        }
        return v1 || v2;
    }

    /**
     * Checks if the resulting answer has met the maximum value
     * @param valueToCheck String the value to check in its purest form
     * @return boolean true if the maximum value has been met
     */
    public boolean isMaximumValue(String valueToCheck)
    {
        boolean v1 = !valueToCheck.isEmpty() && Double.parseDouble(valueToCheck) >= Double.parseDouble(MAX_VALUE);
        LOGGER.debug("is value: '{}' >= {}: {}", valueToCheck, MAX_VALUE, v1);
        return v1;
    }

    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and if true, updates the values
     * @param text the text to add
     */
    public void appendTextToPane(String text, boolean updateValues)
    {
        appendTextToPane(text);
        LOGGER.debug("Text appended to pane. Updating values? {}", updateValues);
        if (updateValues) {
            values[valuesPosition] = text;
        }
    }

    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and only what you provide.
     * Commas are not added here. Decimals are not added here.
     * @param text the text to add
     */
    public void appendTextToPane(String text)
    {
        if (calculatorView == VIEW_BASIC)
        {
            LOGGER.debug("appending text to Basic Panel: {}", text);
            textPane.setText(addNewLines(1) + text);
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
//            if (PUSHED_CLEAR.equals(values[3]) //||
//                //(!getValueAtPosition().isEmpty() && !text.equals(values[valuesPosition])) ||
//                //(valuesPosition != 0 && !values[valuesPosition-1].isEmpty() && !text.equals(values[valuesPosition-1]))
//            ) {
//                programmerPanel.appendTextToProgrammerPane(text);
//                return;
//            }
            switch (calculatorBase) {
                case BASE_BINARY -> {
                    programmerPanel.appendTextToProgrammerPane(programmerPanel.separateBits(convertValueToBinary()));
                }
                case BASE_OCTAL -> {
                    programmerPanel.appendTextToProgrammerPane(convertValueToOctal());
                }
                case BASE_DECIMAL -> {
                    programmerPanel.appendTextToProgrammerPane(text);
                }
                case BASE_HEXADECIMAL -> {
                    programmerPanel.appendTextToProgrammerPane(convertValueToHexadecimal());
                }
            }
        }
        else
        {
            LOGGER.warn("Implement adding text for <view> here");
        }
    }

    /**
     * Simple method to clear the text pane
     */
    public void clearTextInTextPane()
    {
        if (calculatorView == VIEW_BASIC)
        {
            basicPanel.appendTextToBasicPane(BLANK);
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            programmerPanel.appendTextToProgrammerPane(BLANK);
        }
    }

    /**
     * Closes the history pane if it is open
     * @param actionEvent the action event
     */
    private void closeHistoryIfOpen(ActionEvent actionEvent)
    {
        if (actionEvent != null)
        {
            buttonHistory.setText(HISTORY_OPEN);
            if (calculatorView == VIEW_BASIC)
                basicPanel.performHistoryAction(actionEvent);
            else if (calculatorView == VIEW_PROGRAMMER)
                programmerPanel.performHistoryAction(actionEvent);
        }
    }
    /**************** END HELPER METHODS ****************/

    /**************** GETTERS ****************/
    public String getHelpString() { return helpString; }
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
    public JButton getButtonSquared() { return buttonSquared; }
    public JButton getButtonDecimal() { return buttonDecimal; }
    public JButton getButtonFraction() { return buttonFraction; }
    public JButton getButtonPercent() { return buttonPercent; }
    public JButton getButtonSquareRoot() { return buttonSquareRoot; }
    public JButton getButtonMemoryClear() { return buttonMemoryClear; }
    public JButton getButtonMemoryRecall() { return buttonMemoryRecall; }
    public JButton getButtonMemoryStore() { return buttonMemoryStore; }
    public JButton getButtonMemoryAddition() { return buttonMemoryAddition; }
    public JButton getButtonMemorySubtraction() { return buttonMemorySubtraction; }
    public JButton getButtonHistory() { return buttonHistory; }
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
    public JTextPane getTextPane() { return textPane; }
    public CalculatorView getCalculatorView() { return calculatorView; }
    public CalculatorBase getCalculatorBase() { return calculatorBase; }
    public CalculatorBase getPreviousBase() { return previousBase; }
    public CalculatorByte getCalculatorByte() { return calculatorByte; }
    public DateOperation getDateOperation() { return dateOperation; }
    public CalculatorConverterType getConverterType() { return converterType; }
    public ImageIcon getCalculatorIcon() { return calculatorIcon; }
    public ImageIcon getMacIcon() { return macIcon; }
    public ImageIcon getWindowsIcon() { return windowsIcon; }
    public ImageIcon getBlankIcon() { return blankIcon; }
    public JMenuBar getCalculatorMenuBar() { return menuBar; }
    public boolean isFirstNumber() { return isFirstNumber; }
    public boolean isNumberNegative() { return isNumberNegative; }
    public boolean isAdding() { return isAdding; }
    public boolean isSubtracting() { return isSubtracting; }
    public boolean isMultiplying() { return isMultiplying; }
    public boolean isPemdasActive() { return isPemdasActive; }
    public boolean isDividing() { return isDividing; }
    public boolean isDotPressed() { return buttonDecimal.isEnabled(); }
    public JMenu getLookMenu() { return lookMenu; }
    public JMenu getViewMenu() { return viewMenu; }
    public JMenu getEditMenu() { return editMenu; }
    public JMenu getHelpMenu() { return helpMenu; }
    public BasicPanel getBasicPanel() { return basicPanel; }
    public ProgrammerPanel getProgrammerPanel() { return programmerPanel; }
    public ScientificPanel getScientificPanel() { return scientificPanel; }
    public DatePanel getDatePanel() { return datePanel; }
    public ConverterPanel getConverterPanel() { return converterPanel; }
    public JPanel getCurrentPanel() { return currentPanel; }
    public JPanel getHistoryPanel() { return historyPanel;}
    public boolean isMetal() { return isMetal; }
    public boolean isSystem() { return isSystem; }
    public boolean isWindows() { return isWindows; }
    public boolean isMotif() { return isMotif; }
    public boolean isGtk() { return isGtk; }
    public boolean isApple() { return isApple; }

    /**************** SETTERS ****************/
    public void setHelpString(String helpString) { this.helpString = helpString; }
    private void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    private void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setMemoryRecallPosition(int memoryRecallPosition) { this.memoryRecallPosition = memoryRecallPosition; }
    public void setTextPane(JTextPane textPane) { this.textPane = textPane; }
    public void setCalculatorView(CalculatorView calculatorView) { this.calculatorView = calculatorView; LOGGER.debug("CalculatorView set to {}", calculatorView); }
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculatorBase = calculatorBase; LOGGER.debug("CalculatorBase set to {}", calculatorBase); }
    public void setCalculatorBaseAndUpdatePreviousBase(CalculatorBase calculatorBase) {
        setPreviousBase(getCalculatorBase());
        setCalculatorBase(calculatorBase);
    }
    public void setPreviousBase(CalculatorBase previousBase) { this.previousBase = previousBase; LOGGER.debug("PreviousBase set to {}", previousBase); }
    public void setCalculatorByte(CalculatorByte calculatorByte) { this.calculatorByte = calculatorByte; LOGGER.debug("CalculatorByte set to {}", calculatorByte); }
    public void setDateOperation(DateOperation dateOperation) { this.dateOperation = dateOperation; LOGGER.debug("DateOperation set to {}", dateOperation); }
    public void setConverterType(CalculatorConverterType converterType) { this.converterType = converterType; LOGGER.debug("ConverterType set to {}", converterType); }
    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; LOGGER.debug("CurrentPanel set to {}", currentPanel.getName()); }
    public void setCalculatorIcon(ImageIcon calculatorIcon) { this.calculatorIcon = calculatorIcon; }
    public void setMacIcon(ImageIcon macIcon) { this.macIcon = macIcon; }
    public void setWindowsIcon(ImageIcon windowsIcon) { this.windowsIcon = windowsIcon; }
    public void setBlankIcon(ImageIcon blankIcon) { this.blankIcon = blankIcon; }
    public void setIsFirstNumber(boolean firstNumber) { this.isFirstNumber = firstNumber; LOGGER.debug("isFirstNumber set to {}", firstNumber); }
    public void setIsNumberNegative(boolean numberNegative) { this.isNumberNegative = numberNegative; LOGGER.debug("isNumberNegative set to {}", numberNegative); }
    public void setIsAdding(boolean adding) { this.isAdding = adding; }
    public void setIsSubtracting(boolean subtracting) { this.isSubtracting = subtracting; }
    public void setIsMultiplying(boolean multiplying) { this.isMultiplying = multiplying; }
    public void setIsDividing(boolean dividing) { this.isDividing = dividing; }
    public void setIsPemdasActive(boolean isPemdasActive) { this.isPemdasActive = isPemdasActive; }
    public void setCalculatorMenuBar(JMenuBar menuBar) { this.menuBar = menuBar; setJMenuBar(menuBar); LOGGER.debug("Menubar set"); }
    public void setLookMenu(JMenu jMenu) { this.lookMenu = jMenu; LOGGER.debug("Look Menu set"); }
    public void setViewMenu(JMenu jMenu) { this.viewMenu = jMenu; LOGGER.debug("View Menu set"); }
    public void setEditMenu(JMenu jMenu) { this.editMenu = jMenu; LOGGER.debug("Edit Menu set"); }
    public void setHelpMenu(JMenu jMenu) { this.helpMenu = jMenu; LOGGER.debug("Help Menu set"); }
    public void setIsMetal(boolean isMetal) { this.isMetal = isMetal; }
    public void setIsSystem(boolean isSystem) { this.isSystem = isSystem; }
    public void setIsWindows(boolean isWindows) { this.isWindows = isWindows; }
    public void setIsMotif(boolean isMotif) { this.isMotif = isMotif; }
    public void setIsGtk(boolean isGtk) { this.isGtk = isGtk; }
    public void setIsApple(boolean isApple) { this.isApple = isApple; }
    private void setHistoryPanel(JPanel historyPanel) { this.historyPanel = historyPanel; LOGGER.debug("History Panel set"); }
    private void setHistoryTextPane(JTextPane historyTextPane) { this.historyTextPane = historyTextPane; LOGGER.debug("History Text Pane set"); }
}