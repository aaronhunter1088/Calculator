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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
    public static final Font
            mainFont = new Font(SEGOE_UI, Font.PLAIN, 12), // all panels
            mainFontBold = new Font(SEGOE_UI, Font.BOLD, 12), // Date panel
            verdanaFontBold = new Font(VERDANA, Font.BOLD, 20); // Converter, Date panels
    public static final Color
            MOTIF_GRAY = new Color(174,178,195); // Motif look
    // Menubar
    private JMenuBar menuBar;
    // Menubar Items
    private JMenu styleMenu, viewMenu, editMenu, helpMenu;
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
            buttonBlank1 = new JButton(EMPTY), buttonBlank2 = new JButton(EMPTY);
    // Preferences
    private Preferences preferences;
    // Values used to store inputs. MemoryValues used for storing memory values.
    protected String[]
            values = new String[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, // num1, num2, operation, result, copy
            memoryValues = new String[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}; // stores memory values; rolls over after 10 entries
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
    private JPanel currentPanel, // Holds reference to
                   memoryPanel,
                   buttonsPanel,
                   historyPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    // Flags
    private boolean
            isFirstNumber = true,
            negativeNumber, // used to determine is current value is negative or not
            // main operators
            isPemdasActive,
            // look options
            isMetal, isSystem, isWindows, isMotif, isGtk, isApple;
    private String helpString = EMPTY;

    /**************** CONSTRUCTORS ****************/
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
        basicPanel = new BasicPanel();
        programmerPanel = new ProgrammerPanel();
        scientificPanel = new ScientificPanel();
        datePanel = new DatePanel();
        converterPanel = new ConverterPanel();
        setCurrentPanel(switch (calculatorView)
        {
            case VIEW_BASIC -> basicPanel;
            case VIEW_PROGRAMMER -> programmerPanel;
            case VIEW_SCIENTIFIC -> scientificPanel;
            case VIEW_DATE -> datePanel;
            case VIEW_CONVERTER -> converterPanel;
        });
        add(currentPanel);
        setMemoryPanel(new JPanel(new GridBagLayout()));
        memoryPanel.setName("MemoryPanel");
        setButtonsPanel(new JPanel(new GridBagLayout()));
        buttonsPanel.setName("ButtonsPanel");
        setupHistoryTextPane();
        setupPreferences(); // must happen before menubar
        configureMenuBar();
        setupCalculatorImages();
        setupHistoryPanel();
        setupPanel(null);
        setMinimumSize(currentPanel.getSize());
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addKeyListener(new CalculatorKeyListener(this)); // controls keyboard input
        addMouseListener(new CalculatorMouseListener(this)); // controls mouse input
        LOGGER.debug("Finished constructing the calculator");
    }

    /**************** CONFIGURATION ****************/
    /**
     * Configures the menu options on the bar
     */
    private void configureMenuBar()
    {
        setCalculatorMenuBar(new JMenuBar());
        setupStyleMenu();
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
        String look = preferences.get(STYLE, "");

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
        if (isMetal) preferences.put(STYLE, METAL);
        else if (isSystem) preferences.put(STYLE, SYSTEM);
        else if (isWindows) preferences.put(STYLE, WINDOWS);
        else if (isMotif) preferences.put(STYLE, MOTIF);
        else if (isGtk) preferences.put(STYLE, GTK);
        else if (isApple) preferences.put(STYLE, APPLE);
        else preferences.put(STYLE, "");
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
        if (textPane == null)
        {
            setTextPane(new JTextPane());
            textPane.setName("TextPane");
        }
        if (calculatorView == VIEW_BASIC)
        {
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            textPane.setPreferredSize(new Dimension(70, 30));
            textPane.setText(EMPTY);
            textPane.setParagraphAttributes(attribs, true);
        }
        else if (calculatorView == VIEW_PROGRAMMER)
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
        textPane.setFont(mainFont);
        LOGGER.debug("TextPane configured");
    }

    /**
     * The main method to set up the History Panel
     */
    public void setupHistoryTextPane()
    {
        historyTextPane = new JTextPane();
        historyTextPane.setName("HistoryTextPane");
        LOGGER.debug("Configuring History TextPane...");
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        historyTextPane.setParagraphAttributes(attribs, true);
        historyTextPane.setFont(mainFont);
        historyTextPane.setEditable(false);
        // set size based on panel
        switch (calculatorView)
        {
            case VIEW_BASIC -> historyTextPane.setSize(new Dimension(70, 200)); // sets size at start
            case VIEW_PROGRAMMER -> historyTextPane.setSize(new Dimension(70, 205)); // sets size at start
        }
        // set background and border based on look
        if (isMotif)
        {
            historyTextPane.setBackground(MOTIF_GRAY);
            historyTextPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        {
            historyTextPane.setBackground(Color.WHITE);
            historyTextPane.setBorder(new LineBorder(Color.BLACK));
        }
        if (List.of(VIEW_BASIC, VIEW_PROGRAMMER).contains(calculatorView))
        { LOGGER.debug("History TextPane configured"); }
        else
        { LOGGER.warn("No history panel has been set up for the panel:{}", calculatorView.getName()); }
    }

    /**
     * Configures the history panel for the Basic Panel
     */
    private void setupHistoryPanel()
    {
        historyPanel = new JPanel(new GridBagLayout());
        historyPanel.setName("HistoryPanel");
        GridBagConstraints constraints = switch (calculatorView)
        {
            case VIEW_BASIC -> basicPanel.getConstraints();
            case VIEW_PROGRAMMER -> programmerPanel.getConstraints();
            case VIEW_SCIENTIFIC -> scientificPanel.getConstraints();
            default -> null;
        };
        if (constraints == null)
        {
            logException(new CalculatorError("No constraints have been set up for the panel: " + calculatorView.getName()));
            return;
        }
        constraints.anchor = GridBagConstraints.WEST;
        JLabel historyLabel = new JLabel(HISTORY);
        historyLabel.setName("HistoryLabel");
        addComponent(currentPanel, constraints, historyPanel, historyLabel, 0, 0); // space before with jtextarea

        JScrollPane scrollPane = new JScrollPane(historyTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        LOGGER.debug("HistoryTextPane added to JScrollPane");
        scrollPane.setName("HistoryScrollPane");
        scrollPane.setPreferredSize(historyTextPane.getSize());

        addComponent(currentPanel, constraints, historyPanel, scrollPane, 1, 0, new Insets(0,0,0,0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("Basic History Panel configured");
    }

    /**
     * Configures the buttons used on the Basic Panel
     */
    public void setupBasicPanelButtons()
    {
        setupNumberButtons();
        setupMemoryButtons();
        setupCommonButtons();
        LOGGER.debug("Buttons configured for Basic Panel");
    }

    /**
     * Configures the buttons used on the Programmer Panel
     */
    public void setupProgrammerPanelButtons()
    {
        setupNumberButtons();
        setupButtonBlank1();
        setupMemoryButtons(); // MR MC MS M+ M- H
        setupCommonButtons(); // common
        programmerPanel.setupProgrammerPanelButtons();
        LOGGER.debug("Number buttons configured for Programmer Panel");
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
        buttonBlank1.setName(EMPTY);
        LOGGER.debug("Blank button 1 configured");

        buttonBlank2.setName(EMPTY);
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
        buttonHistory.setName(HISTORY);
        buttonHistory.addActionListener(this::performHistoryAction);
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
        { buttonMultiply.addActionListener(this::performMultiplyButtonAction); }
        LOGGER.debug("Multiply button configured");

        buttonSubtract.setName(SUBTRACTION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonSubtract.addActionListener(this::performSubtractButtonAction); }
        LOGGER.debug("Subtract button configured");

        buttonAdd.setName(ADDITION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER)
        { buttonAdd.addActionListener(this::performAddButtonAction); }
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
            { button.addActionListener(this::performNumberButtonAction); }
            else if (calculatorView.equals(VIEW_CONVERTER))
            { button.addActionListener(converterPanel::performNumberButtonActions); }
            else
            { LOGGER.warn("Add other Panels to work with Number buttons"); }
        });
        if (calculatorView == VIEW_PROGRAMMER)
        {
            programmerPanel.getButtonA().setName(A);
            programmerPanel.getButtonA().setName(B);
            programmerPanel.getButtonA().setName(C);
            programmerPanel.getButtonA().setName(D);
            programmerPanel.getButtonA().setName(E);
            programmerPanel.getButtonA().setName(F);
            programmerPanel.getAllHexadecimalButtons().forEach(hexadecimalNumberButton ->
                hexadecimalNumberButton.addActionListener(this::performNumberButtonAction)
            );
            LOGGER.debug("Hexadecimal buttons configured");
        }
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
        buttonBlank1.setName(EMPTY);
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
        buttonBlank2.setName(EMPTY);
        LOGGER.debug("Button Blank2 configured");
    }

    /**
     * The main operations to perform to set up
     * the Style Menu item
     */
    private void setupStyleMenu()
    {
        LOGGER.debug("Configuring Style menu...");
        JMenuItem metal = new JMenuItem(METAL);
        metal.setFont(mainFont);
        metal.setName(METAL);
        metal.addActionListener(this::performStyleMenuAction);

        JMenuItem system = new JMenuItem(SYSTEM);
        system.setFont(mainFont);
        system.setName(SYSTEM);
        system.addActionListener(this::performStyleMenuAction);

        JMenuItem windows = new JMenuItem(WINDOWS);
        windows.setFont(mainFont);
        windows.setName(WINDOWS);
        windows.addActionListener(this::performStyleMenuAction);

        JMenuItem motif = new JMenuItem(MOTIF);
        motif.setFont(mainFont);
        motif.setName(MOTIF);
        motif.addActionListener(this::performStyleMenuAction);

        JMenuItem gtk = new JMenuItem(GTK);
        gtk.setFont(mainFont);
        gtk.setName(GTK);
        gtk.addActionListener(this::performStyleMenuAction);

        JMenuItem apple = new JMenuItem(APPLE);
        apple.setFont(mainFont);
        apple.setName(APPLE);
        apple.addActionListener(this::performStyleMenuAction);

        setStyleMenu(new JMenu(STYLE));
        styleMenu.add(metal);
        styleMenu.add(motif);
        styleMenu.add(apple);
        if (!isOSMac()) // add more options if using Windows
        {
            styleMenu.add(windows);
            styleMenu.add(system);
            styleMenu.add(gtk);
            styleMenu.remove(apple);
        }
        String look = preferences.get(STYLE, "");
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
        styleMenu.setFont(mainFont);
        styleMenu.setName(STYLE);
        menuBar.add(styleMenu);
        LOGGER.debug("Style menu configured");
    }

    /**************** STYLE MENU ACTIONS ****************/
    /**
     * Performs the action for the Style Menu item
     * @param actionEvent the action event
     */
    private void performStyleMenuAction(ActionEvent actionEvent)
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
            if (textPane != null) // not used in all Calculators
            {
                textPane.setBackground(Color.WHITE);
                textPane.setBorder(new LineBorder(Color.BLACK));
            }
            if (historyTextPane != null)
            {
                getHistoryTextPane().setBackground(Color.WHITE);
                getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
            }
            else
            {
                LOGGER.warn("Implement changing history panel for {}", calculatorView.getName());
            }
            updateLookAndFeel();
            savePreferences();
            resetLook();
        }
        catch (ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException e)
        { logException(e); }
    }
    /**************** END STYLE MENU ACTIONS ****************/

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
        String currentView = calculatorView.getName();
        String newView = updatedView.getName();
        LOGGER.debug("from '{}' to '{}'", currentView, newView);
        if (currentView.equals(newView))
        { confirm(this, LOGGER, "Not changing to " + newView + " when already showing " + currentView); }
        else if (newView.equals(converterType.getValue()))
        { confirm(this, LOGGER, "Not changing panels when the converterType is the same"); }
        else
        {
            String currentValueInTextPane = getTextPaneValue();
            LOGGER.debug("Current value in textPane: '{}'", currentValueInTextPane);
            switch (updatedView) {
                case VIEW_BASIC -> {
                    //currentValueInTextPane = getTextPaneValue();
                    switchPanels(basicPanel, actionEvent);
                    // TODO: Must only show number and appropriate operator
                    if (!currentValueInTextPane.isEmpty()) {
                        if (isDecimalNumber(values[valuesPosition])) {
                            buttonDecimal.setEnabled(false);
                        }
                        /* TODO: Handle what happens if the currentValue = v[0] + programmerOperator
                         * and we switch to Basic. Right now it just drops the operator but vP = 1
                         * because we hit a programmerOperator. So vP needs to go back to 0??
                         */
                        if (isOperatorActive()) {
                            appendTextToPane(addCommas(values[valuesPosition-1]) + SPACE + getActiveOperator());
                        } else {
                            appendTextToPane(addCommas(values[valuesPosition]));
                        }
                    }
                }
                case VIEW_PROGRAMMER -> {
                    switchPanels(programmerPanel, actionEvent);
                    if (!currentValueInTextPane.isEmpty()) {
                        if (isDecimalNumber(values[valuesPosition])) {
                            buttonDecimal.setEnabled(false);
                        }
                        if (isOperatorActive()) {
                            programmerPanel.appendTextForProgrammerPanel(values[0] + SPACE + getActiveOperator());
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
                    switchPanels(converterPanel, actionEvent);
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
        LOGGER.debug("Switching to panel: {}", newPanel.getName());
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
        LOGGER.debug("Updating panel {}", newPanel.getClass().getSimpleName());
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
        updateLookAndFeel();
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
        logActionButton(actionEvent, LOGGER);
        if (historyTextPane != null)
        {
            historyTextPane.setText(EMPTY);
            LOGGER.debug("History TextPane cleared");
        }
        else
        {
            LOGGER.warn("No history panel has been set up for the panel:{}", calculatorView.getName());
        }
    }

    /**
     * Displays all the memory values in the history panel
     */
    public void performShowMemoriesAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        if (!isMemoryValuesEmpty())
        {
            StringBuilder memoriesString = new StringBuilder();
            memoriesString.append("Memories: ");
            for(int i=0; i<memoryPosition; i++)
            {
                memoriesString.append("[").append(memoryValues[i]).append("]");
                if ((i+1) < memoryValues.length && !memoryValues[i+1].equals(EMPTY))
                { memoriesString.append(", "); }
            }
            historyTextPane.setText(historyTextPane.getText() +
                    addNewLines() + memoriesString);
        }
        else
        {
            historyTextPane.setText(historyTextPane.getText() +
                    addNewLines() + "No Memories Stored");
        }
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
                             JComponent c, int row, int column)
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
                              JComponent c, int column, Insets insets)
    { addComponent(calculatorPanel, constraints, panel, c, 1, column, insets, 1, 1, 1.0, 1.0, 0, 0); }

    /**
     * Used to add the panel to the frame
     * @param panel the panel to add to the frame, not null
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel)
    {
        if (calculatorView == VIEW_CONVERTER) {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, null, 0, 0, 1.0, 1.0, 0, GridBagConstraints.CENTER);
        }
        else {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, new Insets(0,0,0,0),
                    0, 0, 1.0, 1.0, 0, GridBagConstraints.NORTH);

        }
    }

    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             JComponent c, int row, double weightx, double weighty)
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
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel, JComponent c,
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
            LOGGER.debug("Added {} to {}", c.getName(), panel.getName()); // Ex: Added % to ButtonsPanel
        }
        else {
            calculatorPanel.add(panel, constraints);
            LOGGER.debug("Added {} to calculator frame", panel.getName()); // Ex: Added BasicPanel to calculator frame
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
            memoryValues[memoryPosition] = EMPTY;
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
            BigDecimal result = new BigDecimal(getValueAt()).add(new BigDecimal(memoryValues[(memoryPosition-1)]));
            LOGGER.debug("{} + {} = {}", getTextPaneValue(), memoryValues[(memoryPosition-1)], result);
            memoryValues[(memoryPosition-1)] = String.valueOf(result);
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
            BigDecimal result = new BigDecimal(memoryValues[(memoryPosition-1)]).subtract(new BigDecimal(getValueAt()));
            LOGGER.debug("{} - {} = {}", memoryValues[(memoryPosition-1)], getValueAt(), result);
            memoryValues[(memoryPosition-1)] = String.valueOf(result);
            writeHistoryWithMessage(buttonChoice, false, " Subtracted " + getTextPaneValue() + " to memories at " + (memoryPosition) + SPACE + EQUALS + SPACE + memoryValues[(memoryPosition-1)]);
            confirm(this, LOGGER, "The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * The actions to perform when History is clicked
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        GridBagConstraints currentConstraints = switch (calculatorView)
        {
            case VIEW_BASIC -> basicPanel.getConstraints();
            case VIEW_PROGRAMMER -> programmerPanel.getConstraints();
            case VIEW_SCIENTIFIC -> scientificPanel.getConstraints();
            default -> null;
        };
        JPanel panelContainer = switch (calculatorView)
        {
            case VIEW_BASIC -> basicPanel.getBasicPanel();
            case VIEW_PROGRAMMER -> programmerPanel.getProgrammerPanel();
            case VIEW_SCIENTIFIC -> scientificPanel.getScientificPanel();
            default -> null;
        };
        if (currentPanel != null && panelContainer != null)
        {
            if (HISTORY_OPEN.equals(buttonHistory.getText()))
            {
                LOGGER.debug("Closing History");
                buttonHistory.setText(HISTORY_CLOSED);
                panelContainer.remove(historyPanel);
                addComponent(currentPanel, currentConstraints, panelContainer, buttonsPanel, 2, 0);
            }
            else
            {
                LOGGER.debug("Opening history");
                buttonHistory.setText(HISTORY_OPEN);
                panelContainer.remove(buttonsPanel);
                addComponent(currentPanel, currentConstraints, panelContainer, historyPanel, 2, 0);
            }
            updateLookAndFeel();
        }
        else
        {
            confirm(this, LOGGER, "No history panel setup for " + calculatorView.getName());
        }
    }

    // TODO: Update
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
            double result = Math.sqrt(Double.parseDouble(getValueAt()));
            LOGGER.debug("{} squared = {}", getValueAt(), result);
            if (result % 1 == 0)
            {
                values[valuesPosition] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            }
            else
            {
                values[valuesPosition] = String.valueOf(result);
            }
            appendTextToPane(addCommas(getValueAt()));
            writeHistory(buttonChoice, false);
            buttonDecimal.setEnabled(!isDecimalNumber(getValueAt()));
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
        else if (valuesPosition == 0 || getValueAt(1).isEmpty())
        {
            values[0] = EMPTY;
            resetOperators(false);
            valuesPosition = 0;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
            clearTextInTextPane();
            writeHistoryWithMessage(buttonChoice, false, SPACE + buttonChoice + " performed");
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
        else
        {
            values[1] = EMPTY;
            isFirstNumber = false;
            valuesPosition = 1;
            negativeNumber = false;
            buttonDecimal.setEnabled(true);
            clearTextInTextPane();
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
            values[i] = EMPTY;
        }
        values[0] = ZERO;
        for(int i=0; i < 10; i++)
        { memoryValues[i] = EMPTY; }
        appendTextToPane(ZERO);
        resetOperators(false);
        valuesPosition = 0;
        memoryPosition = 0;
        isFirstNumber = true;
        negativeNumber = false;
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
            if (getValueAt(1).isEmpty())
            { valuesPosition = 0; } // presume they could have pressed an operator then wish to delete
            boolean isNegative = isNegativeNumber(textPaneTextValue);
            String value = getValueWithoutAnyOperator(textPaneTextValue);
            if (isNegative) value = SUBTRACTION + value;
            if (getValueAt().isEmpty() || !getValueAt().equals(value))
            { values[0] = textPaneTextValue; }
            logValuesAtPosition(this, LOGGER);
            LOGGER.debug("textPane: {}", textPaneTextValue);
            if (isNoOperatorActive())
            {
                values[valuesPosition] = getValueAt().substring(0, getValueAt().length()-1);
                appendTextToPane(addCommas(values[valuesPosition]));
            }
            else if (isOperatorActive())
            {
                LOGGER.debug("An operator has been pushed");
                if (valuesPosition == 0)
                {
                    values[2] = EMPTY;
                    values[valuesPosition] = value;
                    appendTextToPane(addCommas(getValueAt()));
                }
                else
                {
                    values[valuesPosition] = getValueAt().substring(0, getValueAt().length()-1);
                    appendTextToPane(addCommas(getValueAt()));
                }
            }
            buttonDecimal.setEnabled(!isDecimalNumber(getValueAt()));
            negativeNumber = getValueAt().startsWith(SUBTRACTION);
            writeHistory(buttonChoice, false);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public void performAddButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = removeCommas(getTextPaneValue());
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(ADDITION)); }
        else if (getValueAt(0).isEmpty() || getValueAt(1).isEmpty() || textPaneValue.isEmpty())
        {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(ADDITION));
        }
        else if (isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Maximum number met"); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Minimum number met"); }
        else
        {
            if (isNoOperatorActive() && !getValueAt(0).isEmpty())
            {
                values[2] = buttonChoice;
                appendTextToPane(textPaneValue + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                negativeNumber = false;
                valuesPosition = 1;
                confirm(this, LOGGER, pressedButton(buttonChoice));
            }
            else if (isOperatorActive() && !getValueAt(1).isEmpty())
            {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 + ...
                String result = performMathOperation(ADDITION).toPlainString();
                setActiveOperator(buttonChoice);
                resetCalculatorOperations(false);
                if (isMaximumValue(result)) // we can add to the minimum number, not to the maximum
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(result));
                }
                else
                {
                    appendTextToPane(addCommas(result) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
                confirm(this, LOGGER, pressedButton(buttonChoice));
            }
            else if (!textPaneValue.isBlank() && getValueAt(0).isBlank())
            {
                logEmptyValue(buttonChoice, this, LOGGER);
                LOGGER.info("Setting values[0] to textPane value");
                appendTextToPane(textPaneValue + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                values[2] = buttonChoice;
                isFirstNumber = false;
                valuesPosition = 1;
                confirm(this, LOGGER, pressedButton(buttonChoice));
            }
            else if (isOperatorActive())
            { confirm(this, LOGGER, cannotPerformOperation(ADDITION)); }
        }
    }
    /**
     * The inner logic for addition.
     * This method performs the addition between values[0] and values[1],
     * clears any trailing zeroes if the result is a whole number (15.0000)
     * resets dotPRESSED + SPACE to false, enables buttonDot and stores the result
     * back in values[0]
     */
    private BigDecimal addition(String continuedOperation)
    {
        BigDecimal result = performBinaryOperation();
        if (continuedOperation != null)
            writeContinuedHistory(continuedOperation, ADDITION, result, true);
        else
            writeContinuedHistory(EQUALS, ADDITION, result, false);
        buttonDecimal.setEnabled(isDecimalNumber(values[3]));
        confirm(this, LOGGER, "Finished adding");
        return result;
    }

    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public void performSubtractButtonAction(ActionEvent actionEvent)
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
            if (isNoOperatorActive() && !getValueAt(0).isEmpty())
            {
                values[2] = buttonChoice;
                values[valuesPosition] = getTextPaneValue();
                appendTextToPane(getValueAt() + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                negativeNumber = false;
                valuesPosition += 1;
            }
            // No operator pushed, textPane is empty
            else if (isNoOperatorActive() && getTextPaneValue().isEmpty())
            {
                values[valuesPosition] = EMPTY;
                appendTextToPane(buttonChoice);
                writeHistory(buttonChoice, true);
                negativeNumber = true;
                values[2] = EMPTY;
            }
            else if (values[2].equals(ADDITION) && !getValueAt(1).isEmpty())
            {
                addition(SUBTRACTION); // 5 + 3 - ...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                values[2] = buttonChoice;
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(SUBTRACTION) && !values[1].isEmpty())
            {
                subtract(SUBTRACTION); // 5 - 3 - ...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(values[3] + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(MULTIPLICATION) && !values[1].isEmpty())
            {
                multiply(SUBTRACTION); // 5 ✕ 3 - ...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(DIVISION) && !values[1].isEmpty())
            {
                divide(SUBTRACTION); // 5 ÷ 3 - ...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (isOperatorActive() && !negativeNumber)
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                writeHistory(buttonChoice, true);
                appendTextToPane(buttonChoice);
                negativeNumber = true;
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank() )//&& !isNegativeNumber(getTextPaneValue()))
            {
                LOGGER.warn("The user pushed subtract but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = removeCommas(getTextPaneValue());
                setIsNumberNegative(isNegativeNumber(getValueAt()));
                appendTextToPane(addCommas(getValueAt()) + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                values[2] = buttonChoice;
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
    private BigDecimal subtract(String continuedOperation)
    {
        BigDecimal result = performBinaryOperation();
        if (continuedOperation != null)
            writeContinuedHistory(continuedOperation, SUBTRACTION, result, true);
        else
            writeContinuedHistory(EQUALS, SUBTRACTION, result, false);
        values[3] = String.valueOf(result);
        appendTextToPane(addCommas(values[3]));
        setIsNumberNegative(isNegativeNumber(values[3]));
        buttonDecimal.setEnabled(isDecimalNumber(values[3]));
        confirm(this, LOGGER, "Finished subtracting");
        return result;
    }

    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public void performMultiplyButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION)); }
        else if (getTextPaneValue().isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION));
        }
        else if (isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met"); }
        else
        {
            if (isNoOperatorActive() && !getValueAt(0).isBlank())
            {
                values[2] = buttonChoice;
                appendTextToPane(getValueAt() + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                valuesPosition = 1;
            }
            // No basic operator pushed, textPane is empty, and values is not set
            else if (isNoOperatorActive())
            {
                values[valuesPosition] = getTextPaneValue();
                appendTextToPane(getValueAt() + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                negativeNumber = isNegativeNumber(getValueAt());
                values[2] = buttonChoice;
            }
            else if (values[2].equals(ADDITION) && !values[1].isEmpty())
            {
                addition(MULTIPLICATION); // 5 + 3 ✕...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(SUBTRACTION) && !values[1].isEmpty())
            {
                subtract(MULTIPLICATION); // 5 - 3 ✕...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(MULTIPLICATION) && !values[1].isEmpty())
            {
                multiply(MULTIPLICATION); // 5 ✕ 3 ✕...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(DIVISION) && !values[1].isEmpty())
            {
                divide(MULTIPLICATION); // 5 ÷ 3 ✕...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice);
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
                values[2] = buttonChoice;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isOperatorActive())
            { confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION)); }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }
    /**
     * The inner logic for multiplying
     */
    private BigDecimal multiply(String continuedOperation)
    {
        BigDecimal result = performBinaryOperation();
        if (continuedOperation != null)
            writeContinuedHistory(continuedOperation, MULTIPLICATION, result, true);
        else
            writeContinuedHistory(EQUALS, MULTIPLICATION, result, false);
        values[3] = String.valueOf(result);
        appendTextToPane(addCommas(values[3]));
        buttonDecimal.setEnabled(isDecimalNumber(values[3]));
        confirm(this, LOGGER, "Finished multiplying");
        return result;
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
        else if (getTextPaneValue().isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(DIVISION));
        }
        else if (isMinimumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met"); }
        else if (isMaximumValue())
        { confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else
        {
            // No basic operator pushed, textPane has a value, and values is set
            if (isNoOperatorActive() && !getTextPaneValue().isEmpty() && !getValueAt().isBlank())
            {
                values[2] = buttonChoice;
                appendTextToPane(values[valuesPosition] + SPACE + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (values[2].equals(ADDITION) && !values[1].isEmpty())
            {
                addition(DIVISION); // 5 + 3 ÷
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }

            }
            else if (values[2].equals(SUBTRACTION) && !values[1].isEmpty())
            {
                subtract(DIVISION); // 5 - 3 ÷
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }

            }
            else if (values[2].equals(MULTIPLICATION) && !values[1].isEmpty())
            {
                multiply(DIVISION); // 5 ✕ 3 ÷...
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3]))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[3]));
                }
                else
                {
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (values[2].equals(DIVISION) && !values[1].isEmpty())
            {
                divide(DIVISION); // 5 ÷ 3 ÷
                values[2] = buttonChoice;
                resetCalculatorOperations(false);
                if (!getTextPaneValue().equals(INFINITY) && (isMinimumValue(values[3]) || isMaximumValue(values[3])))
                {
                    values[2] = EMPTY;
                    appendTextToPane(addCommas(values[0]));
                }
                else if (getTextPaneValue().equals(INFINITY))
                {
                    values[2] = EMPTY;
                    valuesPosition = 0;
                }
                else
                {
                    values[2] = buttonChoice;
                    appendTextToPane(addCommas(values[3]) + SPACE + buttonChoice, true);
                    resetCalculatorOperations(true);
                }
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank())
            {
                LOGGER.error("The user pushed divide but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",", "");
                appendTextToPane(values[0] + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                values[2] = buttonChoice;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isOperatorActive())
            { confirm(this, LOGGER, cannotPerformOperation(DIVISION)); }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }
    /**
     * The inner logic for dividing
     */
    private BigDecimal divide(String continuedOperation)
    {
        BigDecimal result = new BigDecimal(ZERO);
        if (ZERO.equals(getValueAt(1)))
        {
            appendTextToPane(INFINITY);
            values[0] = EMPTY;
            values[1] = EMPTY;
            values[2] = EMPTY;
            values[3] = EMPTY;
            isFirstNumber = true;
            confirm(this, LOGGER, cannotPerformOperation(DIVISION));
        }
        else
        {
            result = performBinaryOperation();
            if (continuedOperation != null)
                writeContinuedHistory(continuedOperation, DIVISION, result, true);
            else
                writeContinuedHistory(EQUALS, DIVISION, result, false);
            values[3] = String.valueOf(result);
            appendTextToPane(addCommas(values[3]));
            //buttonDecimal.setEnabled(isDecimalNumber(values[3]));
            confirm(this, LOGGER, "Finished dividing");
        }
        return result;
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
        }
        if (performInitialChecks())
        {
            LOGGER.warn("Invalid entry in textPane. Clearing...");
            appendTextToPane(EMPTY);
            values[valuesPosition] = EMPTY;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        values[valuesPosition] += buttonChoice;
        if (negativeNumber && !isNegativeNumber(getValueAt()))
            values[valuesPosition] = convertToNegative(getValueAt());
        if (BASE_BINARY == getCalculatorBase())
        {
            var allowedLengthMinusNewLines = programmerPanel.getAllowedLengthsOfTextPane();
            // TEST: removed 0 from allowed lengths
            allowedLengthMinusNewLines.remove((Object)0);
            String textPaneText = getTextPaneValue();
            LOGGER.debug("textPaneText: {}", textPaneText);
            if (allowedLengthMinusNewLines.contains(textPaneText.length()))
            { confirm(this, LOGGER, "Byte length "+allowedLengthMinusNewLines+" already reached"); }
            else
            {
                programmerPanel.appendTextForProgrammerPanel(programmerPanel.separateBits(textPaneText + buttonChoice));
                writeHistory(buttonChoice, false);
                textPaneText = getTextPaneValue();
                if (allowedLengthMinusNewLines.contains(textPaneText.length()))
                {
                    setPreviousBase(BASE_BINARY); // set previousBase since number is fully formed
                    getValues()[getValuesPosition()] = textPaneText;
                    getValues()[getValuesPosition()] = convertValueToDecimal();
                    LOGGER.debug("Byte length {} reached with this input", allowedLengthMinusNewLines);
                }
                confirm(this, LOGGER, "Pressed " + buttonChoice);
            }
        }
        else if (BASE_OCTAL == getCalculatorBase())
        { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else if (BASE_DECIMAL == getCalculatorBase())
        { appendTextToPane(addCommas(getValueAt())); }
        else /* (HEXADECIMAL == calculator.getCalculatorBase()) */
        { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }

        writeHistory(buttonChoice, false);
        confirm(this, LOGGER, pressedButton(buttonChoice));
    }

    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public void performNegateButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String valueToNegate = getValueAt();
        if (valueToNegate.isEmpty()) valueToNegate = getTextPaneValue();
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(NEGATE)); }
        else if (valueToNegate.isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(NEGATE));
        }
        else if (isOperatorActive() && valueToNegate.contains(getActiveOperator()))
        { confirm(this, LOGGER, cannotPerformOperation(NEGATE)); }
        else if (ZERO.equals(valueToNegate))
        { confirm(this, LOGGER, cannotPerformOperation(NEGATE)); }
        else
        {
            if (isNegativeNumber(valueToNegate))
                appendTextToPane(addCommas(convertToPositive(valueToNegate)), true);
            else
                appendTextToPane(addCommas(convertToNegative(valueToNegate)), true);
            setIsNumberNegative(isNegativeNumber(getValueAt()));
            writeHistory(buttonChoice, false);
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
        final var appropriateValue = getValueAt();
        if (appropriateValue.isBlank() && !negativeNumber) // !isNegating
        {
            values[valuesPosition] = ZERO + DECIMAL;
            appendTextToPane(values[valuesPosition]);
        }
        else if (appropriateValue.isBlank() && negativeNumber) // isNegating
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
        if (textPaneContainsBadText())
        { confirm(this, LOGGER, cannotPerformOperation(EQUALS)); }
        else if (getValueAt(0).isEmpty() || getValueAt(1).isEmpty())
        {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(buttonChoice));
        }
        else
        {
            BigDecimal result = performMathOperation(null);
            if (textPaneContainsBadText())
                appendTextToPane(getBadText());
            else
                appendTextToPane(addCommas(String.valueOf(result)));
            resetValues();
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }
    /**
     * This method determines which operation to perform,
     * performs that operation, stores the result in v[3]
     * and resets any necessary flags.
     */
    public BigDecimal performMathOperation(String continuedOperation)
    {
        BigDecimal result = new BigDecimal(ZERO);
        switch (getActiveOperator()) {
            case ADDITION ->
            {
                result = addition(continuedOperation);
                resetCalculatorOperations(true);
            }
            case SUBTRACTION ->
            {
                result = subtract(continuedOperation);
                resetCalculatorOperations(true);
            }
            case MULTIPLICATION ->
            {
                result = multiply(continuedOperation);
                resetCalculatorOperations(true);
            }
            case DIVISION ->
            {
                result = divide(continuedOperation);
                resetCalculatorOperations(true);
            }
            case PERCENT ->
            {
                result = performUnaryOperation();
            }
            case SQUARED ->
            {
                result = performUnaryOperation();
            }
            case FRACTION ->
            {
                result = performUnaryOperation();
            }
            case AND ->
            {
                String andResult = programmerPanel.performAnd();
                //andResult = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, andResult);
                //values[3] = andResult;
                result = new BigDecimal(andResult);
                writeContinuedHistory(EQUALS, AND, andResult, false);
                resetCalculatorOperations(false);
            }
            case OR ->
            {
                String orResult = programmerPanel.performOr();
                //values[3] = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, orResult);
                result = new BigDecimal(orResult);
                writeContinuedHistory(OR, OR, orResult, false);
                resetCalculatorOperations(false);
            }
            case XOR ->
            {
                String xorResult = programmerPanel.performXor();
                //values[3] = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, xorResult);
                result = new BigDecimal(xorResult);
                writeContinuedHistory(OR, OR, xorResult, false);
                resetCalculatorOperations(false);
            }
            case MODULUS ->
            {
                result = performBinaryOperation();
            }
            default -> LOGGER.warn("Unknown operation: {}", getActiveOperator());
        }
        return result;
    }

    /**
     * Performs the appropriate binary operation
     * with values[0] and values[1], performing
     * the operation in values[2]. It logs the
     * operation performed and stored in values[3].
     * The result may be stripped of any trailing
     * zeros but ultimately returned.
     * @return the result of the operation
     */
    private BigDecimal performBinaryOperation()
    {
        BigDecimal firstNumber = new BigDecimal(values[0]);
        BigDecimal secondNumber = new BigDecimal(values[1]);
        // TODO: Change to String result = EMPTY;
        BigDecimal result = new BigDecimal(ZERO);
        valuesPosition = 3;
        switch (getActiveOperator())
        {
            case ADDITION ->
            {
                result = firstNumber.add(secondNumber);
                if (isDecimalNumber(String.valueOf(result)))
                {
                    result = result.stripTrailingZeros();
                }
            }
            case SUBTRACTION ->
            {
                result = firstNumber.subtract(secondNumber);
                if (isDecimalNumber(String.valueOf(result)))
                {
                    result = result.stripTrailingZeros();
                }
            }
            case MULTIPLICATION ->
            {
                result = firstNumber.multiply(secondNumber);
                if (isDecimalNumber(String.valueOf(result)))
                {
                    result = result.stripTrailingZeros();
                }
            }
            case DIVISION ->
            {
                result = firstNumber.divide(secondNumber);
                if (isDecimalNumber(String.valueOf(result)))
                {
                    result = result.stripTrailingZeros();
                }
            }
            case AND ->
            {
                String andResult = programmerPanel.performAnd();
                andResult = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, andResult);
                result = new BigDecimal(andResult); // TODO: check
                //writeContinuedHistory(EQUALS, AND, Double.parseDouble(andResult), false);
                //resetValues();
                //resetCalculatorOperations(false);
            }
            case OR ->
            {
                String orResult = programmerPanel.performOr();
                orResult = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, orResult);
                result = new BigDecimal(orResult); // TODO: check
                //writeContinuedHistory(OR, OR, resultInBase, false);
                //values[3] = resultInBase;
                //programmerPanel.getCalculator().getValues()[2] = EMPTY;
                //resetCalculatorOperations(false);
            }
            case XOR ->
            {
                String xorResult = programmerPanel.performXor();
                xorResult = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, xorResult);
                result = new BigDecimal(xorResult); // TODO: check
                //writeContinuedHistory(OR, OR, resultInBase, false);
                //values[3] = resultInBase;
                //resetCalculatorOperations(false);
            }
            case MODULUS ->
            {
                result = new BigDecimal(programmerPanel.performModulus());
            }
            default -> LOGGER.warn("Unknown operation: {}", values[2]);
        }
        values[valuesPosition] = String.valueOf(result);
        buttonDecimal.setEnabled(isDecimalNumber(getValueAt()));
        logOperation(LOGGER, values);
        return result;
    }

    /**
     * Performs the appropriate unary operation
     * on the current value at position, and logs
     * the operation performed.
     * @return
     */
    private BigDecimal performUnaryOperation()
    {
        BigDecimal result = new BigDecimal(ZERO);
        BigDecimal firstNumber = new BigDecimal(getValueAt());
        switch (getActiveOperator())
        {
            case PERCENT ->
            {
                BigDecimal oneHundred = new BigDecimal(ONE+ZERO+ZERO);
                if (ZERO.equals(getValueAt()))
                {
                    appendTextToPane(INFINITY);
                    values[0] = EMPTY;
                    values[1] = EMPTY;
                    values[2] = EMPTY;
                    values[3] = EMPTY;
                    isFirstNumber = true;
                    confirm(this, LOGGER, cannotPerformOperation(PERCENT));
                }
                else
                {
                    result = firstNumber.divide(oneHundred);
                    if (isDecimalNumber(String.valueOf(result)))
                    {
                        result = result.stripTrailingZeros();
                    }
                    LOGGER.debug("{} / {} = {}%", firstNumber, oneHundred, result);
                }
            }
            case SQUARED ->
            {
                double number = firstNumber.doubleValue();
                result = BigDecimal.valueOf(Math.pow(number, 2));
                if (isDecimalNumber(String.valueOf(result)))
                {
                    result = result.stripTrailingZeros();
                }
                LOGGER.debug("{} squared = {}", number, result.toPlainString());
            }
            case FRACTION ->
            {
                BigDecimal one = new BigDecimal(ONE);
                if (ZERO.equals(getValueAt()))
                {
                    appendTextToPane(INFINITY);
                    values[0] = EMPTY;
                    values[1] = EMPTY;
                    values[2] = EMPTY;
                    values[3] = EMPTY;
                    isFirstNumber = true;
                    confirm(this, LOGGER, cannotPerformOperation(FRACTION));
                }
                else
                {
                    result = one.divide(firstNumber);
                    LOGGER.debug("{} / {} = {}", one, firstNumber, result);
                }
            }
            default -> LOGGER.warn("Unknown unary operation: {}", getActiveOperator());
        }
        values[3] = result.toPlainString();
        return result;
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
    public boolean isOperatorActive() { return !getValueAt(2).isEmpty(); }

    /**
     * This method returns true if no operator is active
     * independent of the current view.
     * @return true if no operator is active, false otherwise
     */
    public boolean isNoOperatorActive()  { return getValueAt(2).isEmpty(); }

    /**
     * This method returns the operator
     * that was pushed.
     * @return the operator that was pushed
     */
    public String getActiveOperator() { return getValueAt(2); }

    /**
     * Sets the active operator
     * @param operator the operator to set
     */
    public void setActiveOperator(String operator) { values[2] = operator; }

    /**
     * Records the buttonChoice to the appropriate history panel
     * Ex: Press 5, History shows: (5) Result: 5
     * Ex: Press +, History shows: (+) Result: 5 +
     * @param buttonChoice String the button choice
     */
    public void writeHistory(String buttonChoice, boolean addButtonChoiceToEnd) { writeHistoryWithMessage(buttonChoice, addButtonChoiceToEnd, null); }

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
        String paneValue;
        if (message == null)
        {
            LOGGER.debug("no message");
            paneValue = addCommas(values[valuesPosition]);
        }
        else
        {
            LOGGER.debug("with a specific message: {}", message);
            paneValue = message;
        }
        if (addButtonChoiceToEnd)
        {
            paneValue += SPACE + buttonChoice;
        }
        var currentHistoryText = getHistoryTextPane().getText();
        if (message == null)
        {
            paneValue = " Result: " + paneValue;
        }
        getHistoryTextPane().setText(
            currentHistoryText + NEWLINE +
            LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS + paneValue
        );
    }

    /**
     * Records the buttonChoice specifically if it was a continued operation
     * Ex: Already entered 5 + 6, then press +, History shows: (+) Result: 5 + 6 = 11 +
     * @param continuedOperation String whether to display the operation or equals
     * @param operation String the operation performed (add, subtract, etc)
     * @param result double the result from the operation
     * @param addContinuedOperationToEnd boolean whether to append the operation to the end
     */
    public void writeContinuedHistory(String continuedOperation, String operation, Object result, boolean addContinuedOperationToEnd)
    {
        LOGGER.info("Writing continued history");
        var paneValue = addCommas(values[0]) + SPACE + operation + SPACE + addCommas(values[1]) + SPACE + EQUALS + SPACE + addCommas(String.valueOf(result));
        if (addContinuedOperationToEnd)
        {
            paneValue += SPACE + continuedOperation;
        }
        var currentHistory = getHistoryTextPane().getText();
        getHistoryTextPane().setText(
            currentHistory + NEWLINE +
            LEFT_PARENTHESIS + continuedOperation + RIGHT_PARENTHESIS + " Result: " + paneValue
        );
    }

    /**
     * This method resets default values
     * Primarily used for testing purposes
     */
    public void resetValues()
    {
        values[0] = EMPTY;
        values[1] = EMPTY;
        values[2] = EMPTY;
        values[3] = EMPTY;
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
        values[1] = EMPTY;
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
            buttonDecimal.setEnabled(!isDecimalNumber(getValueAt()));
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
     * Tests whether a number contains the "." symbol or not
     * @param number the number to test
     * @return true if the number contains a decimal point,
     * false otherwise
     */
    public boolean isDecimalNumber(String number)  { return number.contains(DECIMAL); }

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
            appendTextToPane(EMPTY);
            values[valuesPosition] = EMPTY;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        boolean issueFound = false;
        if (textPaneContainsBadText())
        {
            LOGGER.debug("TextPane contains bad text");
            issueFound = true;
        }
        else if (getValueAt(0).isEmpty() && valuesPosition == 1)
        {
            LOGGER.debug("v[0] is empty. placing v[1] in v[0]. clearing v[1].");
            values[0] = values[1];
            values[1] = EMPTY;
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
        LOGGER.debug("values[{}] left of decimal length: {}", valuesPosition, getNumberOnLeftSideOfDecimal(getValueAt()).length());
        LOGGER.debug("values[{}] right of decimal length: {}", valuesPosition, getNumberOnRightSideOfDecimal(getValueAt()).length());
        return getNumberOnLeftSideOfDecimal(getValueAt()).length() > 7 ||
               getNumberOnRightSideOfDecimal(getValueAt()).length() > 7 ;
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
        if (EMPTY.equals(val)) return result;
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
        String badText = EMPTY;
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
        boolean control = isNegativeNumber(valueToAdjust);
        //if (valueToAdjust.contains(delimiter)) return valueToAdjust;
        if (valueToAdjust.contains(delimiter)) valueToAdjust = removeCommas(valueToAdjust);
        if (valueToAdjust.isBlank()) return valueToAdjust;
        if (control) {
            valueToAdjust = getValueWithoutAnyOperator(valueToAdjust);
        }
        if (!isDecimalNumber(valueToAdjust) && valueToAdjust.length() <= 3) {
            if (control) return SUBTRACTION + valueToAdjust;
            else return valueToAdjust;
        }
        else {
            String numberOnLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            if (numberOnLeft.length() <= 3) {
                if (control) return SUBTRACTION + valueToAdjust;
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
        if (control) adjusted = new StringBuffer(SUBTRACTION).append(adjusted);
        LOGGER.debug("commas added: {}", adjusted);
        return adjusted.toString();
    }

    /**
     * Removes commas from the number if appropriate
     * @param valueToAdjust the number to adjust
     * @return the value without commas
     */
    public String removeCommas(String valueToAdjust)  { return valueToAdjust.replace(COMMA, EMPTY); }

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
    public String getValueAt() { return values[valuesPosition]; }

    /**
     * Returns values[vP] for the given position
     * @param vP the position to get
     * @return the value at vP
     */
    public String getValueAt(int vP) { return values[vP]; }

    /**
     * Converts the current value to its binary representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToBinary()
    {
        String valueToConvert = getValueAt();
        if (valueToConvert.isEmpty()) return EMPTY;
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
        String valueToConvert = getValueAt();
        if (valueToConvert.isEmpty()) return EMPTY;
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
        String valueToConvert = getValueAt();
        if (valueToConvert.isEmpty()) return EMPTY;
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
        String valueToConvert = getValueAt();
        if (valueToConvert.isEmpty()) return EMPTY;
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
        valueToConvert = valueToConvert.replace(COMMA, EMPTY).replace(SPACE, EMPTY);
        if (valueToConvert.isEmpty()) return EMPTY;
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

            String fracConverted = EMPTY;
            if (parts.length > 1 && !parts[1].isEmpty()) {
                // Convert fractional part from 'fromBase' to a BigDecimal value
                BigDecimal frac = BigDecimal.ZERO;
                BigDecimal baseFrom = BigDecimal.valueOf(getPreviousRadix(fromBase));
                for (int i = 0; i < parts[1].length(); i++) {
                    int digit = Character.digit(parts[1].charAt(i), getPreviousRadix(fromBase));
                    if (digit < 0) { digit = 0; }
                    frac = frac.add(BigDecimal.valueOf(digit)
                            .divide(baseFrom.pow(i + 1), 64, RoundingMode.DOWN));
                }
                // Convert fractional BigDecimal into target base digits (fixed precision)
                int precision = 20;
                StringBuilder sb = new StringBuilder();
                BigDecimal baseTo = BigDecimal.valueOf(getPreviousRadix(toBase));
                BigDecimal current = frac;
                for (int i = 0; i < precision && current.compareTo(BigDecimal.ZERO) > 0; i++) {
                    current = current.multiply(baseTo);
                    int digit = current.intValue();
                    sb.append(Character.forDigit(digit, getPreviousRadix(toBase)));
                    current = current.subtract(new BigDecimal(digit));
                }
                fracConverted = sb.toString();
            }
            convertedNumber = intConverted + (fracConverted.isEmpty() ? EMPTY : (DECIMAL + fracConverted));
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
                .replace(ADDITION, EMPTY) // target, replacement
                .replace(SUBTRACTION, EMPTY)
                .replace(MULTIPLICATION, EMPTY)
                .replace(DIVISION, EMPTY)
                .replace(MODULUS, EMPTY)
                //.replace(LEFT_PARENTHESIS, BLANK)
                //.replace(RIGHT_PARENTHESIS, BLANK)
                .replace(ROL, EMPTY)
                .replace(ROR, EMPTY)
                .replace(XOR, EMPTY) // XOR must be checked before OR, otherwise OR will be replaced when XOR is there, leaving X, which is not desired
                .replace(OR, EMPTY)
                .replace(AND, EMPTY)
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
                .replaceAll(NEWLINE, EMPTY)
                .strip(); }
        else if (calculatorView == VIEW_PROGRAMMER)
        { return getTextPaneValueForProgrammerPanel()
                .replaceAll(NEWLINE, EMPTY)
                .strip();
        }
        else
        {
            LOGGER.warn("Implement");
            return EMPTY;
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
        String currentValue = EMPTY;
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
                                    .replace(COMMA, EMPTY);
                        }
                        case BYTE_DWORD -> {
                            currentValue = textPane.getText().split(NEWLINE)[2]
                                    + textPane.getText().split(NEWLINE)[3]
                                    .replace(COMMA, EMPTY);
                        }
                        case BYTE_QWORD -> {
                            currentValue = textPane.getText().split(NEWLINE)[2]
                                    + textPane.getText().split(NEWLINE)[3]
                                    + textPane.getText().split(NEWLINE)[4]
                                    + textPane.getText().split(NEWLINE)[5]
                                    .replace(COMMA, EMPTY);
                        }
                    }
                    currentValue = currentValue.replace(SPACE, EMPTY);
                    // TODO: Determine where this was used, then move method to proper location
                    //currentValue = adjustBinaryNumber(currentValue);
                }
                case BASE_OCTAL,
                     BASE_DECIMAL,
                     BASE_HEXADECIMAL -> {
                    currentValue = textPane.getText().split(NEWLINE)[2];
                }
            }
            if (currentValue.isEmpty()) {
                LOGGER.warn("Attempted to retrieve value from text pane but it was empty. Returning blank.");
                return EMPTY;
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
                        ? EMPTY
                        : splitTextValue[2].replace(COMMA, EMPTY);
            } catch (ArrayIndexOutOfBoundsException ae2)
            {
                logException(new CalculatorError("Attempted to retrieve value from text pane but got ArrayIndexOutOfBoundsException. Returning blank."));
                return EMPTY;
            }
        }
        catch (Exception e)
        {
            logException(e);
            return EMPTY;
        }
        return currentValue;
    }

    /**
     * Returns the text in the current panel's history pane
     * @return the history text for the current panel
     */
    public String getHistoryPaneTextWithoutNewLineCharacters()
    {
        if (List.of(VIEW_BASIC, VIEW_PROGRAMMER, VIEW_SCIENTIFIC).contains(calculatorView))
        { return historyTextPane.getText().replace(NEWLINE, EMPTY).strip(); }
        else
        {
            LOGGER.warn("Add other panels history pane if intended to have one");
            return EMPTY;
        }
    }

    /**
     * Adds a specific amount of newLine characters
     * @param amount int the amount of newLine characters to add
     * @return String representing newLine characters
     */
    public String addNewLines(int amount)
    {
        String newLines = null;
        if (amount == 0)
        {
            if (calculatorView == VIEW_BASIC) newLines = NEWLINE.repeat(1);
            else if (calculatorView == VIEW_PROGRAMMER) newLines = NEWLINE.repeat(1);
            else if (calculatorView == VIEW_SCIENTIFIC) newLines = NEWLINE.repeat(3);
        }
        else
        { newLines = NEWLINE.repeat(amount); }
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
        return !number.startsWith(SUBTRACTION);
    }

    /**
     * Determines whether a number is negative
     *
     * @param number the value to test
     * @return either true or false based on result
     */
    public boolean isNegativeNumber(String number)
    {
        return number.startsWith(SUBTRACTION);
    }

    /**
     * Converts a number to its negative equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.debug("Converting {} to negative number", number.replaceAll(NEWLINE, EMPTY));
        if (!number.startsWith(SUBTRACTION))
            number = SUBTRACTION + number.replace(NEWLINE, EMPTY);
        setIsNumberNegative(true);
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.debug("Converting {} to positive number", number.replaceAll(NEWLINE, EMPTY));
        number = number.replaceAll(SUBTRACTION, EMPTY).trim();
        setIsNumberNegative(false);
        return number;
    }

    /**
     * Resets the operators to the boolean passed in
     * @param reset a boolean to reset the operators to
     */
    public void resetOperators(boolean reset)
    {
        isPemdasActive = reset;
        setIsFirstNumber(reset);
        values[2] = EMPTY; // operator
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
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = EMPTY;
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
        if (index < 0 || (index + 1) >= currentNumber.length()) rightSide = EMPTY;
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

        }
        else
        {
            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) <= Double.parseDouble(MIN_VALUE);
            LOGGER.debug("is v[0]: '{}' <= {}: {}", values[0], MIN_VALUE, v1);
        }
        return v1 || v2;
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
            v1 = !values[0].isEmpty() && new BigDecimal(values[0]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
            LOGGER.debug("is v[0]: '{}' >= {}: {}", values[0], MAX_VALUE, v1);
            v2 = !values[1].isEmpty() && new BigDecimal(values[1]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
            LOGGER.debug("is v[1]: '{}' >= {}: {}", values[1], MAX_VALUE, v2);
        }
        else {
            v1 = !values[0].isEmpty() && new BigDecimal(values[0]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
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
        boolean v1 = !valueToCheck.isEmpty() && new BigDecimal(valueToCheck).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
        LOGGER.debug("is value: '{}' >= {}: {}", valueToCheck, MAX_VALUE, v1);
        return v1;
    }

    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and if true, updates the
     * values[valuesPosition] with the text provided, without
     * any commas.
     * @param text the text to add
     */
    public void appendTextToPane(String text, boolean updateValues)
    {
        appendTextToPane(text);
        LOGGER.debug("Text appended to pane. Updating values? {}", updateValues);
        boolean negativeControl = isNegativeNumber(text);
        if (updateValues) {
            values[valuesPosition] = removeCommas(getValueWithoutAnyOperator(text));
        }
        if (negativeControl) {
            values[valuesPosition] = convertToNegative(getValueAt());
        }
        LOGGER.debug("values[{}]: {}", getValuesPosition(), getValueAt());
    }

    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and only what you provide.
     * Commas are not added here. Decimals are not added here.
     * What it does do is keep the value displaying in the
     * same base and/or byte that is currently selected.
     * @param text the text to add
     */
    public void appendTextToPane(String text)
    {
        LOGGER.debug("adding text: '{}'", text);
        if (calculatorView == VIEW_BASIC)
        {
            textPane.setText(addNewLines(1) + text);
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            switch (calculatorBase) {
                case BASE_BINARY -> {
                    programmerPanel.appendTextForProgrammerPanel(programmerPanel.separateBits(convertValueToBinary()));
                }
                case BASE_OCTAL -> {
                    programmerPanel.appendTextForProgrammerPanel(convertValueToOctal());
                }
                case BASE_DECIMAL -> {
                    programmerPanel.appendTextForProgrammerPanel(text);
                }
                case BASE_HEXADECIMAL -> {
                    programmerPanel.appendTextForProgrammerPanel(convertValueToHexadecimal());
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
     * independent of knowing which panel is
     * the current one.
     */
    public void clearTextInTextPane()
    {
        if (calculatorView == VIEW_BASIC)
        {
            appendTextToPane(EMPTY);
        }
        else if (calculatorView == VIEW_PROGRAMMER)
        {
            programmerPanel.appendTextForProgrammerPanel(EMPTY);
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
                performHistoryAction(actionEvent);
            else if (calculatorView == VIEW_PROGRAMMER)
                performHistoryAction(actionEvent);
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
    public boolean isNegativeNumber() { return negativeNumber; }
    public boolean isPemdasActive() { return isPemdasActive; }
    public boolean isDotPressed() { return buttonDecimal.isEnabled(); }
    public JMenu getStyleMenu() { return styleMenu; }
    public JMenu getViewMenu() { return viewMenu; }
    public JMenu getEditMenu() { return editMenu; }
    public JMenu getHelpMenu() { return helpMenu; }
    public BasicPanel getBasicPanel() { return basicPanel; }
    public JPanel getMemoryPanel() { return memoryPanel; }
    public JPanel getButtonsPanel() { return buttonsPanel; }
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
    public JTextPane getHistoryTextPane() { return historyTextPane; }


    /**************** SETTERS ****************/
    public void setHelpString(String helpString) { this.helpString = helpString; }
    private void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    private void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setMemoryRecallPosition(int memoryRecallPosition) { this.memoryRecallPosition = memoryRecallPosition; }
    public void setTextPane(JTextPane textPane) { this.textPane = textPane; LOGGER.debug("TextPane setup"); }
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
    public void setHistoryPanel(JPanel historyPanel) { this.historyPanel = historyPanel; LOGGER.debug("History Panel set"); }
    public void setMemoryPanel(JPanel memoryPanel) { this.memoryPanel = memoryPanel; LOGGER.debug("Memory Panel set"); }
    public void setButtonsPanel(JPanel buttonsPanel) { this.buttonsPanel = buttonsPanel; LOGGER.debug("Buttons Panel set"); }
    public void setCalculatorIcon(ImageIcon calculatorIcon) { this.calculatorIcon = calculatorIcon; }
    public void setMacIcon(ImageIcon macIcon) { this.macIcon = macIcon; }
    public void setWindowsIcon(ImageIcon windowsIcon) { this.windowsIcon = windowsIcon; }
    public void setBlankIcon(ImageIcon blankIcon) { this.blankIcon = blankIcon; }
    public void setIsFirstNumber(boolean firstNumber) { this.isFirstNumber = firstNumber; LOGGER.debug("isFirstNumber set to {}", firstNumber); }
    public void setIsNumberNegative(boolean numberNegative) { this.negativeNumber = numberNegative; LOGGER.debug("isNumberNegative set to {}", numberNegative); }
    public void setIsPemdasActive(boolean isPemdasActive) { this.isPemdasActive = isPemdasActive; }
    public void setCalculatorMenuBar(JMenuBar menuBar) { this.menuBar = menuBar; setJMenuBar(menuBar); LOGGER.debug("Menubar set"); }
    public void setStyleMenu(JMenu styleMenu) { this.styleMenu = styleMenu; LOGGER.debug("Style Menu set"); }
    public void setViewMenu(JMenu viewMenu) { this.viewMenu = viewMenu; LOGGER.debug("View Menu set"); }
    public void setEditMenu(JMenu editMenu) { this.editMenu = editMenu; LOGGER.debug("Edit Menu set"); }
    public void setHelpMenu(JMenu helpMenu) { this.helpMenu = helpMenu; LOGGER.debug("Help Menu set"); }
    public void setIsMetal(boolean isMetal) { this.isMetal = isMetal; }
    public void setIsSystem(boolean isSystem) { this.isSystem = isSystem; }
    public void setIsWindows(boolean isWindows) { this.isWindows = isWindows; }
    public void setIsMotif(boolean isMotif) { this.isMotif = isMotif; }
    public void setIsGtk(boolean isGtk) { this.isGtk = isGtk; }
    public void setIsApple(boolean isApple) { this.isApple = isApple; }
    public void setHistoryTextPane(JTextPane historyTextPane) { this.historyTextPane = historyTextPane; LOGGER.debug("History Text Pane set"); }
}