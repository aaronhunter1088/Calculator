package Calculators;

import Interfaces.CalculatorType;
import Interfaces.OSDetector;
import Panels.*;
import Runnables.CalculatorMain;
import Types.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Types.CalculatorConverterType.ANGLE;
import static Types.CalculatorConverterType.AREA;
import static Types.CalculatorUtility.*;
import static Types.CalculatorView.*;
import static Types.DateOperation.DIFFERENCE_BETWEEN_DATES;
import static Types.Texts.*;
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
    public static final Font
            mainFont = new Font(SEGOE_UI, Font.PLAIN, 12), // all panels
            mainFontBold = new Font(SEGOE_UI, Font.BOLD, 12), // Date panel
            verdanaFontBold = new Font(VERDANA, Font.BOLD, 20); // Converter, Date panels
    public static final Color
            MOTIF_GRAY = new Color(174, 178, 195); // Motif look
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
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
            buttonMemoryAddition = new JButton(MEMORY_ADD), buttonMemorySubtraction = new JButton(MEMORY_SUBTRACT),
            buttonHistory = new JButton(HISTORY_CLOSED), buttonSquared = new JButton(SQUARED),
            buttonAdd = new JButton(ADDITION), buttonSubtract = new JButton(SUBTRACTION),
            buttonMultiply = new JButton(MULTIPLICATION), buttonDivide = new JButton(DIVISION),
            buttonEquals = new JButton(EQUALS), buttonNegate = new JButton(NEGATE),
    // Used in programmer and converter... until an official button is determined
    buttonBlank1 = new JButton(EMPTY), buttonBlank2 = new JButton(EMPTY);
    private final BasicPanel basicPanel;
    private final ProgrammerPanel programmerPanel;
    private final ScientificPanel scientificPanel;
    private final DatePanel datePanel;
    private final ConverterPanel converterPanel;
    // Values used to store inputs. MemoryValues used for storing memory values.
    protected String[]
            values = new String[]{EMPTY, EMPTY, EMPTY, EMPTY}, // num1, num2, operation, result
            memoryValues = new String[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}; // stores memory values; rolls over after 10 entries
    // Position of values, memoryValues, and memoryRecall
    protected int valuesPosition = 0, memoryPosition = 0, memoryRecallPosition = 0;
    // Menubar
    private JMenuBar menuBar;
    // Menubar Items
    private JMenu styleMenu, viewMenu, editMenu, helpMenu;
    // Text Pane
    private JTextPane textPane, historyTextPane;
    // Preferences
    private Preferences preferences;
    // Current view, base, byte, date operation, converter type, and panel (view)
    private CalculatorView calculatorView;
    private CalculatorBase calculatorBase, previousBase;
    private CalculatorByte calculatorByte;
    private DateOperation dateOperation;
    private CalculatorConverterType converterType;
    private JPanel currentPanel, // Holds reference to
            memoryPanel,
            buttonsPanel,
            historyPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    // Flags
    private boolean
            obtainingFirstNumber = true,
            negativeNumber, // used to determine is current value is negative or not
    // main operators
    isPemdasActive;
    private String helpString = EMPTY,
            calculatorStyle = EMPTY;
    private OSDetector systemDetector;

    /**************** CONSTRUCTORS ****************/
    /**
     * Starts the calculator with the BASIC CalculatorView
     *
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(VIEW_BASIC, BASE_DECIMAL, null, null, null, null);
    }

    /**
     * Starts the Calculator with a specific CalculatorView
     * but defaults to BASE_DECIMAL
     *
     * @param calculatorView the type of Calculator to create
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorView, null, null, null, null, null);
    }

    /**
     * Starts the calculator with the Programmer or Scientific view
     * with a specified CalculatorBase and CalculatorByte
     *
     * @param calculatorView the type of Calculator to create
     * @param calculatorBase the base to set that Calculator in
     * @param calculatorByte the byte size to set that Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView, CalculatorBase calculatorBase, CalculatorByte calculatorByte) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(calculatorView, calculatorBase, calculatorByte, null, null, null);
    }

    /**
     * This constructor is used to create a DATE Calculator with a specific DateOperation
     *
     * @param dateOperation the option to open the DateCalculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(VIEW_DATE, null, null, null, dateOperation, null);
    }

    /**
     * This constructor is used to create a Converter Calculator starting with a specific CalculatorConverterType
     *
     * @param converterType the type of unit to start the Converter Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        this(VIEW_CONVERTER, null, null, converterType, null, null);
    }

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
                      DateOperation dateOperation, OSDetector systemDetector)
            throws CalculatorError, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calculatorView.getValue());
        setCalculatorView(calculatorView);
        setCalculatorBase(calculatorBase == null ? BASE_DECIMAL : calculatorBase);
        setCalculatorByte(calculatorByte == null ? BYTE_BYTE : calculatorByte);
        setConverterType(converterType == null ? AREA : converterType);
        setDateOperation(dateOperation == null ? DIFFERENCE_BETWEEN_DATES : dateOperation);
        this.systemDetector = systemDetector == null ? new SystemDetector() : systemDetector;
        basicPanel = new BasicPanel();
        programmerPanel = new ProgrammerPanel();
        scientificPanel = new ScientificPanel();
        datePanel = new DatePanel();
        converterPanel = new ConverterPanel();
        setCurrentPanel(switch (calculatorView) {
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
    private void setupPreferences()
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
        String style = preferences.get(STYLE, "");

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
        preferences.put(STYLE, calculatorStyle);
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
            logException(new CalculatorError("The icon you are attempting to use cannot be found: calculatorIcon"), LOGGER);
        setCalculatorIcon(calculatorIcon);
        ImageIcon macIcon = createImageIcon("src/main/resources/images/appleLogo.jpg");
        if (null == macIcon)
            logException(new CalculatorError("The icon you are attempting to use cannot be found: macIcon"), LOGGER);
        setMacIcon(macIcon);
        ImageIcon windowsIcon = createImageIcon("src/main/resources/images/windowsLogo.jpg");
        if (null == windowsIcon)
            logException(new CalculatorError("The icon you are attempting to use cannot be found: windowsIcon"), LOGGER);
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
        } else {
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
        if (textPane == null) {
            setTextPane(new JTextPane());
            textPane.setName("TextPane");
        } else {
            textPane.setText(EMPTY);
        }
        if (calculatorView == VIEW_BASIC) {
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            textPane.setPreferredSize(new Dimension(70, 30));
            textPane.setText(EMPTY);
            textPane.setParagraphAttributes(attribs, true);
        } else if (calculatorView == VIEW_PROGRAMMER) {
            String[] initString = {
                    programmerPanel.displayByteAndBase() + NEWLINE,
                    values[valuesPosition] + NEWLINE
            };
            String[] alignmentStyles = {"alignLeft", "alignRight"};
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
            }
            catch (Exception e) {
                logException(e, LOGGER);
            }
            textPane.setPreferredSize(new Dimension(100, 120));
        }
        if (calculatorStyle.equals(MOTIF)) {
            textPane.setBackground(MOTIF_GRAY);
            textPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        } else {
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
        switch (calculatorView) {
            case VIEW_BASIC -> historyTextPane.setSize(new Dimension(70, 200)); // sets size at start
            case VIEW_PROGRAMMER -> historyTextPane.setSize(new Dimension(70, 205)); // sets size at start
        }
        // set background and border based on look
        if (calculatorStyle.equals(MOTIF)) {
            historyTextPane.setBackground(MOTIF_GRAY);
            historyTextPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        } else {
            historyTextPane.setBackground(Color.WHITE);
            historyTextPane.setBorder(new LineBorder(Color.BLACK));
        }
        if (List.of(VIEW_BASIC, VIEW_PROGRAMMER).contains(calculatorView)) {
            LOGGER.debug("History TextPane configured");
        } else {
            LOGGER.warn("No history panel has been set up for the panel:{}", calculatorView.getName());
        }
    }

    /**
     * Configures the history panel for the Basic Panel
     */
    private void setupHistoryPanel()
    {
        historyPanel = new JPanel(new GridBagLayout());
        historyPanel.setName("HistoryPanel");
        GridBagConstraints constraints = switch (calculatorView) {
            case VIEW_BASIC -> basicPanel.getConstraints();
            case VIEW_PROGRAMMER -> programmerPanel.getConstraints();
            case VIEW_SCIENTIFIC -> scientificPanel.getConstraints();
            default -> null;
        };
        if (constraints == null) {
            logException(new CalculatorError("No constraints have been set up for the panel: " + calculatorView.getName()), LOGGER);
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

        addComponent(currentPanel, constraints, historyPanel, scrollPane, 1, 0, new Insets(0, 0, 0, 0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("Basic History Panel configured");
    }

    /**************** MENU OPTIONS ****************/
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
        styleMenu.setFont(mainFont);
        styleMenu.setName(STYLE + MENU);
        styleMenu.add(metal);
        styleMenu.add(motif);
        styleMenu.add(apple);
        if (systemDetector.isWindows()) // add more options if using Windows
        {
            styleMenu.add(windows);
            styleMenu.add(system);
            styleMenu.add(gtk);
            styleMenu.remove(apple);
        }
        calculatorStyle = preferences.get(STYLE, "");
        switch (calculatorStyle) {
            case METAL -> metal.doClick();
            case SYSTEM -> system.doClick();
            case WINDOWS -> windows.doClick();
            case MOTIF -> motif.doClick();
            case GTK -> gtk.doClick();
            case APPLE -> apple.doClick();
            default -> {
                setCalculatorStyle(METAL);
                LOGGER.warn("Defaulting to {}", calculatorStyle);
                metal.doClick();
            }
        }
        menuBar.add(styleMenu);
        LOGGER.debug("Style menu configured");
    }

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
        viewMenu.setName(VIEW + MENU);
        viewMenu.setFont(mainFont);
        viewMenu.add(basic);
        viewMenu.add(programmer);
        viewMenu.add(date);
        viewMenu.addSeparator();
        viewMenu.add(converterMenu);
        menuBar.add(viewMenu); // add viewMenu to menu bar
        LOGGER.debug("View menu configured");
    }

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
        clearHistoryItem.addActionListener(this::performClearHistoryAction);

        JMenuItem showMemoriesItem = new JMenuItem(SHOW_MEMORIES);
        showMemoriesItem.setFont(mainFont);
        showMemoriesItem.setName(SHOW_MEMORIES);
        showMemoriesItem.addActionListener(this::performShowMemoriesAction);

        setEditMenu(new JMenu(EDIT));
        editMenu.setName(EDIT + MENU);
        editMenu.setFont(mainFont);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(clearHistoryItem);
        editMenu.add(showMemoriesItem);
        menuBar.add(editMenu);
        LOGGER.debug("Edit menu configured");
    }

    /**
     * The main operations to perform to set up
     * the Help Menu item
     */
    private void setupHelpMenu()
    {
        LOGGER.debug("Configuring Help menu...");
        setHelpMenu(new JMenu(HELP));
        helpMenu.setName(HELP + MENU);
        helpMenu.setFont(mainFont);
        JMenuItem showHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();
        helpMenu.add(showHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        menuBar.add(helpMenu);
        LOGGER.debug("Help menu configured");
    }
    /**************** END MENU OPTIONS ****************/

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
        setupMemoryButtons();
        setupCommonButtons();
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
        if (calculatorView == VIEW_CONVERTER) {
            buttonClearEntry.addActionListener(converterPanel::performClearEntryButtonActions);
        }
        LOGGER.debug("ClearEntry button configured");

        buttonDelete.setName(DELETE);
        if (calculatorView == VIEW_CONVERTER) {
            buttonDelete.addActionListener(converterPanel::performDeleteButtonActions);
        }
        LOGGER.debug("Delete button configured");

        buttonDecimal.setName(DECIMAL);
        if (calculatorView == VIEW_CONVERTER) {
            buttonDecimal.addActionListener(converterPanel::performDecimalButtonActions);
        }
        LOGGER.debug("Decimal button configured");

        LOGGER.debug("Converter Panel buttons configured");
    }

    /**
     * The main method to set up the Memory buttons. This
     * also includes the History button since it is included
     * in the panel that renders it
     */
    private void setupMemoryButtons()
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
        buttonMemoryAddition.setName(MEMORY_ADD);
        buttonMemoryAddition.addActionListener(this::performMemoryAdditionAction);
        buttonMemorySubtraction.setName(MEMORY_SUBTRACT);
        buttonMemorySubtraction.addActionListener(this::performMemorySubtractionAction);
        buttonMemoryStore.setEnabled(true); // Enable memoryStore
        buttonMemoryStore.setName(MEMORY_STORE);
        buttonMemoryStore.addActionListener(this::performMemoryStoreAction);
        buttonHistory.setEnabled(true);
        //reset buttons to enabled if memories are saved
        if (!memoryValues[0].isEmpty()) {
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
    private void setupCommonButtons()
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
        if (calculatorView == VIEW_BASIC) {
            buttonPercent.addActionListener(basicPanel::performPercentButtonAction);
        }
        LOGGER.debug("Percent button configured");

        buttonSquareRoot.setName(SQUARE_ROOT);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonSquareRoot.addActionListener(this::performSquareRootButtonAction);
        }
        LOGGER.debug("SquareRoot button configured");

        buttonSquared.setName(SQUARED);
        if (calculatorView == VIEW_BASIC) {
            buttonSquared.addActionListener(basicPanel::performSquaredButtonAction);
        }
        LOGGER.debug("Squared button configured");

        buttonFraction.setName(FRACTION);
        if (calculatorView == VIEW_BASIC) {
            buttonFraction.addActionListener(basicPanel::performFractionButtonAction);
        }
        LOGGER.debug("Fraction button configured");

        buttonClearEntry.setName(CLEAR_ENTRY);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonClearEntry.addActionListener(this::performClearEntryButtonAction);
        }
        LOGGER.debug("ClearEntry button configured");

        buttonClear.setName(CLEAR);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonClear.addActionListener(this::performClearButtonAction);
        }
        LOGGER.debug("Clear button configured");

        buttonDelete.setName(DELETE);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonDelete.addActionListener(this::performDeleteButtonAction);
        }
//        else if (calculatorView == VIEW_PROGRAMMER)
//        { buttonDelete.addActionListener(programmerPanel::performDeleteButtonAction); }
        LOGGER.debug("Delete button configured");

        buttonDivide.setName(DIVISION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonDivide.addActionListener(this::performDivideButtonAction);
        }
        LOGGER.debug("Divide button configured");

        buttonMultiply.setName(MULTIPLICATION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonMultiply.addActionListener(this::performMultiplyButtonAction);
        }
        LOGGER.debug("Multiply button configured");

        buttonSubtract.setName(SUBTRACTION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonSubtract.addActionListener(this::performSubtractButtonAction);
        }
        LOGGER.debug("Subtract button configured");

        buttonAdd.setName(ADDITION);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonAdd.addActionListener(this::performAddButtonAction);
        }
        LOGGER.debug("Addition button configured");

        buttonNegate.setName(NEGATE);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonNegate.addActionListener(this::performNegateButtonAction);
        }
        LOGGER.debug("Negate button configured");

        buttonDecimal.setName(DECIMAL);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonDecimal.addActionListener(this::performDecimalButtonAction);
        }
        LOGGER.debug("Decimal button configured");

        buttonEquals.setName(EQUALS);
        if (calculatorView == VIEW_BASIC || calculatorView == VIEW_PROGRAMMER) {
            buttonEquals.addActionListener(this::performEqualsButtonAction);
        }
        LOGGER.debug("Equals button configured");

        LOGGER.debug("Common buttons configured");
    }

    /**
     * The main method to set up all number buttons, 0-9
     */
    private void setupNumberButtons()
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
            if (calculatorView.equals(VIEW_BASIC)) {
                button.addActionListener(this::performNumberButtonAction);
            } else if (calculatorView.equals(VIEW_PROGRAMMER)) {
                button.addActionListener(this::performNumberButtonAction);
            } else if (calculatorView.equals(VIEW_CONVERTER)) {
                button.addActionListener(converterPanel::performNumberButtonActions);
            } else {
                LOGGER.warn("Add other Panels to work with Number buttons");
            }
        });
        if (calculatorView == VIEW_PROGRAMMER) {
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
    private void setupButtonBlank1()
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
    private void setupButtonBlank2()
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
     *
     * @return JMenuItem the updated help menu item
     */
    public JMenuItem updateShowHelp()
    {
        JMenuItem viewHelp = helpMenu.getItem(0);
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(this::performShowHelpAction);
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

    /****** ADD COMPONENTS TO PANEL OR PANEL TO FRAME ******/
    /**
     * Used to add a component to a panel
     *
     * @param calculatorPanel the current view panel
     * @param constraints     the grid bag constraints
     * @param panel           the panel to add the component to
     * @param c               the component to add
     * @param row             the row to add the component to
     * @param column          the column to add the component to
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             JComponent c, int row, int column)
    {
        addComponent(calculatorPanel, constraints, panel, c, row, column, null,
                1, 1, 1.0, 1.0, 0, 0);
    }

    /**
     * Used to add a component to a panel
     *
     * @param panel  the panel to add to
     * @param c      the component to add
     * @param column the column to add the component to
     * @param insets the space between the component
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             JComponent c, int column, Insets insets)
    {
        addComponent(calculatorPanel, constraints, panel, c, 1, column, insets, 1, 1, 1.0, 1.0, 0, 0);
    }

    /**
     * Used to add the panel to the frame
     *
     * @param panel the panel to add to the frame, not null
     */
    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel)
    {
        if (calculatorView == VIEW_CONVERTER) {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, null, 0, 0, 1.0, 1.0, 0, GridBagConstraints.CENTER);
        } else {
            addComponent(calculatorPanel, constraints, panel, null, 0, 0, new Insets(0, 0, 0, 0),
                    0, 0, 1.0, 1.0, 0, GridBagConstraints.NORTH);

        }
    }

    public void addComponent(JPanel calculatorPanel, GridBagConstraints constraints, JPanel panel,
                             JComponent c, int row, double weightx, double weighty)
    {
        addComponent(calculatorPanel, constraints, panel, c, row, 0, null, 1, 1, weightx, weighty, 0, 0);
    }

    /**
     * Sets the constraints for a component or panel
     * and adds the component to the specified panel
     * or the specified panel to the frame.
     *
     * @param panel         the panel to add to, not null
     * @param c             the component to add to a panel, can be null
     * @param row           the row to place the component in
     * @param column        the column to place the component in
     * @param insets        the space between the component
     * @param gridwidth     the number of columns the component should use
     * @param gridheight    the number of rows the component should use
     * @param weightXRow    set to allow the button grow horizontally
     * @param weightYColumn set to allow the button grow horizontally
     * @param fill          set to make the component resize if any unused space
     * @param anchor        set to place the component in a specific location on the frame
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
        if (fill != 0) constraints.fill = fill;
        if (anchor != 0) constraints.anchor = anchor;
        if (c != null) {
            panel.add(c, constraints);
            LOGGER.debug("Added {} to {}", c.getName(), panel.getName()); // Ex: Added % to ButtonsPanel
        } else {
            calculatorPanel.add(panel, constraints);
            LOGGER.debug("Added {} to calculator frame", panel.getName()); // Ex: Added BasicPanel to calculator frame
        }
    }
    /****** END ADD COMPONENTS TO PANEL OR PANEL TO FRAME ******/
    /**************** END CONFIGURATION ****************/

    /**************** BUTTON ACTIONS ****************/
    /**
     * Performs the action for the Style Menu item
     *
     * @param actionEvent the action event
     */
    public void performStyleMenuAction(ActionEvent actionEvent)
    {
        String styleSelected = ((JMenuItem) actionEvent.getSource()).getName();
        logActionButton(styleSelected, LOGGER);
        setCalculatorStyle(styleSelected);
        try {
            switch (styleSelected) {
                case METAL -> UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                case SYSTEM -> UIManager.setLookAndFeel("javax.swing.plaf.system.SystemLookAndFeel");
                case WINDOWS -> UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                case MOTIF -> UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                case GTK -> UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                case APPLE -> UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
                default -> UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
        }
        catch (ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException e) {
            logException(e, LOGGER);
        }
        if (textPane != null) // not used in all Calculators
        {
            textPane.setBackground(Color.WHITE);
            textPane.setBorder(new LineBorder(Color.BLACK));
        } else {
            LOGGER.warn("Check that you're adjusting the proper textPane.");
        }
        if (historyTextPane != null) // not used in all Calculators
        {
            getHistoryTextPane().setBackground(Color.WHITE);
            getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
        } else {
            LOGGER.warn("Implement changing history panel for {}", calculatorView.getName());
        }
        updateLookAndFeel();
        savePreferences();
    }

    /**
     * The main actions to perform when switch panels
     *
     * @param actionEvent the click action
     */
    public void performViewMenuAction(ActionEvent actionEvent, CalculatorType updatedView)
    {
        LOGGER.info("Switching views...");
        String currentView = calculatorView.getName();
        String newView = updatedView.getName(); // some converter type
        if (updatedView instanceof CalculatorConverterType)
            updatedView = VIEW_CONVERTER;
        LOGGER.debug("from '{}' to '{}'", currentView, newView);
        if (currentView.equals(newView)) {
            confirm(this, LOGGER, "Not changing to " + newView + " when already showing " + currentView);
        } else if (newView.equals(converterType.getValue())) {
            confirm(this, LOGGER, "Not changing panels when the converterType is the same");
        } else {
            String currentValueInTextPane = getTextPaneValue();
            LOGGER.debug("Current value in textPane: '{}'", currentValueInTextPane);
            switch (updatedView) {
                case VIEW_BASIC -> {
                    //currentValueInTextPane = getTextPaneValue();
                    switchPanels(basicPanel, actionEvent);
                    // TODO: Must only show number and appropriate operator
                    if (!currentValueInTextPane.isEmpty()) {
                        if (isFractionalNumber(getValueWithoutAnyOperator(currentValueInTextPane))) {
                            buttonDecimal.setEnabled(false);
                        }
                        /* TODO: Handle what happens if the currentValue = v[0] + programmerOperator
                         * and we switch to Basic. Right now it just drops the operator but vP = 1
                         * because we hit a programmerOperator. So vP needs to go back to 0??
                         */
                        if (isOperatorActive() && getBasicPanelOperators().contains(getActiveOperator())) {
                            appendTextToPane(addThousandsDelimiter(currentValueInTextPane, getThousandsDelimiter()) + SPACE + getActiveOperator());
                        } else {
                            appendTextToPane(addThousandsDelimiter(getValueWithoutAnyOperator(currentValueInTextPane), getThousandsDelimiter()));
                        }
                    }
                }
                case VIEW_PROGRAMMER -> {
                    switchPanels(programmerPanel, actionEvent);
                    if (!currentValueInTextPane.isEmpty()) {
                        if (isFractionalNumber(getValueWithoutAnyOperator(currentValueInTextPane))) {
                            buttonDecimal.setEnabled(false);
                        }
                        // Adjust value from BASE_DECIMAL to current programmer base
                        currentValueInTextPane = switch (calculatorBase) {
                            case BASE_BINARY ->
                                    convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, currentValueInTextPane);
                            case BASE_OCTAL -> convertFromBaseToBase(BASE_DECIMAL, BASE_OCTAL, currentValueInTextPane);
                            case BASE_HEXADECIMAL ->
                                    convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, currentValueInTextPane);
                            default -> currentValueInTextPane;
                        };
                        if (isOperatorActive() && getProgrammerPanelOperators().contains(getActiveOperator())) {
                            programmerPanel.appendTextForProgrammerPanel(currentValueInTextPane + SPACE + getActiveOperator());
                        } else {
                            programmerPanel.appendTextForProgrammerPanel(currentValueInTextPane);
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
                case VIEW_CONVERTER -> {
                    setConverterType(CalculatorConverterType.valueOf(newView));
                    switchPanels(converterPanel, actionEvent);
                }
                default -> {
                    LOGGER.warn("Add other views");
                }
            }
            updateMemoryButtonsState();
            updateLookAndFeel();
            confirm(this, LOGGER, "Switched from " + currentView + " to " + newView);
        }
    }

    /**
     * The inner logic to perform when switching panels
     *
     * @param newPanel the new panel to switch to
     */
    private void switchPanels(JPanel newPanel, ActionEvent actionEvent)
    {
        LOGGER.debug("Switching to panel: {}", newPanel.getName());
        setTitle(newPanel.getName());
        updatePanel(newPanel, actionEvent);
        setSize(currentPanel.getSize());
        setMinimumSize(currentPanel.getSize());
    }

    /**
     * This method updates the panel by removing the old panel,
     * setting up the new current panel, and adds it to the frame
     *
     * @param newPanel the panel to update on the Calculator
     */
    private void updatePanel(JPanel newPanel, ActionEvent actionEvent)
    {
        LOGGER.debug("Updating panel {}", newPanel.getClass().getSimpleName());
        JPanel oldPanel = currentPanel;
        remove(oldPanel);
        currentPanel = newPanel;
        calculatorView = switch (currentPanel.getClass().getSimpleName()) {
            case "BasicPanel" -> VIEW_BASIC;
            case "ProgrammerPanel" -> VIEW_PROGRAMMER;
            case "ScientificPanel" -> VIEW_SCIENTIFIC;
            case "DatePanel" -> VIEW_DATE;
            case "ConverterPanel" -> VIEW_CONVERTER;
            default -> {
                logException(new IllegalStateException("Unexpected value: " + currentPanel.getClass().getSimpleName()), LOGGER);
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
    private void setupPanel(ActionEvent actionEvent)
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

    /**
     * Saves the current textPane value to the system clipboard
     *
     * @param actionEvent the click action
     */
    public void performCopyAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        StringSelection selection = new StringSelection(getTextPaneValue());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        confirm(this, LOGGER, pressedButton(COPY));
    }

    /**
     * Pastes the current system clipboard value to the textPane
     * and updates the current value at valuesPosition.
     *
     * @param actionEvent the click action
     */
    public void performPasteAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        String text = EMPTY;
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                text = removeThousandsDelimiter(text.trim(), getThousandsDelimiter());
                String value = new BigDecimal(text).toPlainString();
                appendTextToPane(addThousandsDelimiter(value, getThousandsDelimiter()), true);
                confirm(this, LOGGER, pressedButton(PASTE));
            }
            catch (Exception e) {
                logException(new CalculatorError(e), LOGGER);
            }
        }
    }

    /**
     * Clears the history panel text.
     *
     * @param actionEvent the click action
     */
    public void performClearHistoryAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        if (historyTextPane != null) {
            historyTextPane.setText(EMPTY);
            LOGGER.debug("History TextPane cleared");
        } else {
            LOGGER.warn("No history panel has been set up for the panel: {}", calculatorView.getName());
        }
    }

    /**
     * Displays all the memory values in the history panel
     *
     * @param actionEvent the click action
     */
    public void performShowMemoriesAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        String currentHistory = historyTextPane.getText();
        if (isMemoryValuesEmpty()) {
            historyTextPane.setText(currentHistory + NEWLINE + NO_MEMORIES_STORED);
        } else {
            StringBuilder memoriesString = new StringBuilder();
            memoriesString.append(MEMORIES + SPACE);
            for (int i = 0; i < memoryPosition; i++) {
                memoriesString.append(LEFT_BRACKET).append(memoryValues[i]).append(RIGHT_BRACKET);
                if ((i + 1) < memoryValues.length && !memoryValues[i + 1].equals(EMPTY)) {
                    memoriesString.append(COMMA).append(SPACE);
                }
            }
            historyTextPane.setText(currentHistory + NEWLINE + memoriesString);
        }
        LOGGER.debug("Show Memories complete");
    }

    /**
     * Displays the help text in a scrollable pane
     *
     * @param actionEvent the click action
     */
    public void performShowHelpAction(ActionEvent actionEvent)
    {
        JTextArea message = new JTextArea(helpString, 20, 40);
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
     * Display the text for About Calculator menu item
     *
     * @param actionEvent the click action
     */
    public void performAboutCalculatorAction(ActionEvent actionEvent)
    {
        logActionButton(actionEvent, LOGGER);
        JPanel iconPanel = new JPanel(new GridBagLayout());
        JLabel iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        ImageIcon specificLogo = systemDetector.isMac() ? macIcon : windowsIcon;
        JLabel textLabel = new JLabel(getAboutCalculatorString(), specificLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(this, mainPanel, ABOUT_CALCULATOR, JOptionPane.PLAIN_MESSAGE);
        confirm(this, LOGGER, pressedButton(ABOUT_CALCULATOR));
    }

    /**
     * Returns the text to display in About Calculator
     *
     * @return String the About Calculator text
     */
    protected String getAboutCalculatorString()
    {
        LOGGER.debug("Configuring " + ABOUT_CALCULATOR + " text...");
        String computerText = "", version = "";
        boolean isMac = systemDetector.isMac();
        if (isMac) {
            computerText = APPLE;
        } else {
            computerText = WINDOWS;
        }
        try (InputStream is = CalculatorMain.class.getResourceAsStream("/pom.properties")) {
            Properties p = new Properties();
            p.load(is);
            version = p.getProperty("project.version");
        }
        // String osName = System.getProperty("os.name"); TODO: Check out. There are lots of specific options
        catch (IOException | NullPointerException e) {
            logException(e, LOGGER);
        }
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
                        "Your OS");
    }

    /**
     * The actions to perform when MemoryStore is clicked
     *
     * @param actionEvent the click action
     */
    public void performMemoryStoreAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(MEMORY_STORE));
        } else if (getTextPaneValue().isEmpty()) {
            logEmptyValue(MEMORY_STORE, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(MEMORY_STORE));
        } else {
            String valueToStore = getAppropriateValue();
            if (!valueToStore.isEmpty() && isNoOperatorActive()) {
                setActiveOperator(buttonChoice);
                storeValueInMemory(valueToStore);
                writeHistoryWithMessage(buttonChoice, false, savedMemory(memoryValues[memoryPosition - 1], (memoryPosition - 1)));
                updateMemoryButtonsState();
                setActiveOperator(EMPTY); // memory store does not keep operator active
                confirm(this, LOGGER, savedMemory(memoryValues[memoryPosition - 1], (memoryPosition - 1)));
            }
            // Needs to be here!
            else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(MEMORY_STORE));
            }
        }
    }

    /**
     * The inner logic to store the current textPane value
     * into memoryValues at memoryPosition. It also
     * increases the memoryPosition by 1. Finally, the other
     * memory buttons are enabled.
     */
    public void storeValueInMemory(String valueToStore)
    {
        if (memoryPosition == 10) // reset to 0
        {
            setMemoryPosition(0);
        }
        memoryValues[memoryPosition] = valueToStore; //getTextPaneValue();
        setMemoryPosition(memoryPosition + 1);
        updateMemoryButtonsState();
    }

    /**
     * The actions to perform when MemoryRecall is clicked
     *
     * @param actionEvent the click action
     */
    public void performMemoryRecallAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (memoryRecallPosition == 10 || memoryValues[memoryRecallPosition].isBlank()) {
            setMemoryRecallPosition(getLowestMemoryPosition());
        }
        appendTextToPane(memoryValues[memoryRecallPosition]);
        values[valuesPosition] = getTextPaneValue();
        writeHistoryWithMessage(buttonChoice, false, " Recalled: " + memoryValues[memoryRecallPosition] + " at memory location " + (memoryRecallPosition + 1));
        memoryRecallPosition += 1;
        confirm(this, LOGGER, "Recalling number in memory: " + memoryValues[(memoryRecallPosition - 1)] + " at position: " + (memoryRecallPosition - 1));
    }

    /**
     * The actions to perform when MemoryClear is clicked
     *
     * @param actionEvent the click action
     */
    public void performMemoryClearAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (memoryPosition == 10) {
            LOGGER.debug("Resetting memoryPosition to 0");
            memoryPosition = 0;
        }
        if (!isMemoryValuesEmpty()) {
            setMemoryPosition(getLowestMemoryPosition());
            LOGGER.info("Clearing memoryValue[{}] = {}", memoryPosition, memoryValues[memoryPosition]);
            writeHistoryWithMessage(buttonChoice, false, " Cleared " + memoryValues[memoryPosition] + " from memory location " + (memoryPosition + 1));
            memoryValues[memoryPosition] = EMPTY;
            memoryRecallPosition += 1;
            LOGGER.debug("Cleared memory at memories[{}]", memoryPosition);
            // MemorySuite could now be empty
            if (isMemoryValuesEmpty()) {
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
     *
     * @param actionEvent the click action
     */
    public void performMemoryAdditionAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(MEMORY_ADD));
        } else {
            String value = getAppropriateValue();
            if (!getAppropriateValue().isEmpty() && isNoOperatorActive()) {
                setActiveOperator(buttonChoice);
                performOperation();
                //buttonDecimal.setEnabled(!isFractionalNumber(values[3]));
                writeHistoryWithMessage(buttonChoice, false, addedToMemory(value, (memoryPosition - 1), values[3]));
                memoryValues[(memoryPosition - 1)] = values[3];
                setActiveOperator(EMPTY);
                confirm(this, LOGGER, "The new value in memory at position " + (memoryPosition - 1) + " is " + memoryValues[(memoryPosition - 1)]);
            }
            // Needs to be here!
            else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(MEMORY_ADD));
            }
        }
    }

    /**
     * The inner logic for memory addition.
     * This method performs the addition between v[vP] and
     * the memV[memP-1]. It returns the result as a String.
     *
     * @return the result of the memory addition
     */
    public String performMemoryAdd()
    {
        BigDecimal currentNumber = new BigDecimal(getAppropriateValue());
        BigDecimal memoryNumber = new BigDecimal(memoryValues[memoryPosition - 1]);
        String result = memoryNumber.add(currentNumber).toPlainString();
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when MemorySubtraction is clicked
     *
     * @param actionEvent the click action
     */
    public void performMemorySubtractionAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(MEMORY_SUBTRACT));
        } else {
            String value = getAppropriateValue();
            if (!value.isEmpty() && isNoOperatorActive()) {
                setActiveOperator(buttonChoice);
                performOperation();
                writeHistoryWithMessage(buttonChoice, false, subtractedFromMemory(value, (memoryPosition - 1), values[3]));
                memoryValues[(memoryPosition - 1)] = values[3];
                setActiveOperator(EMPTY);
                confirm(this, LOGGER, subtractedFromMemory(value, (memoryPosition - 1), values[3]));
            }
            // Needs to be here!
            else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(MEMORY_SUBTRACT));
            }
        }
    }

    /**
     * The inner logic for memory subtraction.
     * This method performs the subtraction between v[vP] and
     * the memV[memP-1]. It returns the result as a String.
     *
     * @return the result of the memory subtraction
     */
    public String performMemorySubtract()
    {
        BigDecimal currentNumber = new BigDecimal(getAppropriateValue());
        BigDecimal memoryNumber = new BigDecimal(memoryValues[memoryPosition - 1]);
        String result = memoryNumber.subtract(currentNumber).toPlainString();
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when History is clicked
     *
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        GridBagConstraints currentConstraints = switch (calculatorView) {
            case VIEW_BASIC -> basicPanel.getConstraints();
            case VIEW_PROGRAMMER -> programmerPanel.getConstraints();
            case VIEW_SCIENTIFIC -> scientificPanel.getConstraints();
            default -> null;
        };
        JPanel panelContainer = switch (calculatorView) {
            case VIEW_BASIC -> basicPanel.getBasicPanel();
            case VIEW_PROGRAMMER -> programmerPanel.getProgrammerPanel();
            case VIEW_SCIENTIFIC -> scientificPanel.getScientificPanel();
            default -> null;
        };
        if (currentPanel != null && panelContainer != null) {
            if (HISTORY_OPEN.equals(buttonHistory.getText())) {
                LOGGER.debug("Closing History");
                buttonHistory.setText(HISTORY_CLOSED);
                panelContainer.remove(historyPanel);
                addComponent(currentPanel, currentConstraints, panelContainer, buttonsPanel, 2, 0);
            } else {
                LOGGER.debug("Opening history");
                buttonHistory.setText(HISTORY_OPEN);
                panelContainer.remove(buttonsPanel);
                addComponent(currentPanel, currentConstraints, panelContainer, historyPanel, 2, 0);
            }
            updateLookAndFeel();
        } else {
            confirm(this, LOGGER, "No history panel setup for " + calculatorView.getName());
        }
    }

    /**
     * The actions to perform when the SquareRoot button is clicked
     *
     * @param actionEvent the click action
     */
    public void performSquareRootButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
        } else if (getTextPaneValue().isEmpty()) {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
        } else if (CalculatorUtility.isNegativeNumber(getAppropriateValue())) {
            appendTextToPane(ONLY_POSITIVES);
            confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
        } else {
            String value = getAppropriateValue();
            boolean endsWithBinaryOperator = getBasicPanelBinaryOperators().contains(getActiveOperator()) && getTextPaneValue().endsWith(getActiveOperator());
            if (!value.isEmpty() && !endsWithBinaryOperator) {
                String currentOperator = getActiveOperator();
                setActiveOperator(buttonChoice);
                performOperation();
                appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()), true);
                setNegativeNumber(CalculatorUtility.isNegativeNumber(values[3]));
                buttonDecimal.setEnabled(!isFractionalNumber(values[3]));
                writeHistory(buttonChoice, false);
                if (getBasicPanelUnaryOperators().contains(buttonChoice)) {
                    setActiveOperator(EMPTY);
                } else {
                    setActiveOperator(currentOperator);
                }
                confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
            }
            // Needs to be here!
            else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(SQUARE_ROOT));
            }
        }
    }

    /**
     * The inner logic for performing the SquareRoot operation
     *
     * @return the result of the SquareRoot operation
     */
    public String performSquareRoot()
    {
        double value = new BigDecimal(getAppropriateValue()).doubleValue();
        //return String.valueOf(Math.sqrt(value));
        String result = String.valueOf(Math.sqrt(value));
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The action to perform when the ClearEntry button is clicked
     *
     * @param actionEvent the click action
     */
    public void performClearEntryButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (getTextPaneValue().isEmpty()) {
            confirm(this, LOGGER, cannotPerformOperation(buttonChoice));
        } else {
            if (valuesPosition == 0 || values[1].isEmpty()) {
                resetValues(false);
            } else {
                setNegativeNumber(false);
                setObtainingFirstNumber(false);
                values[1] = EMPTY;
                values[2] = EMPTY;
                values[3] = EMPTY;
                setValuesPosition(1);
            }
            clearTextInTextPane();
            writeHistoryWithMessage(buttonChoice, false, performedOperation(buttonChoice));
            updateMemoryButtonsState();
            confirm(this, LOGGER, performedOperation(buttonChoice));
        }
    }

    /**
     * The actions to perform when the Clear button is clicked
     *
     * @param actionEvent the action performed
     */
    public void performClearButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        for (int i = 0; i < 4; i++) {
            values[i] = EMPTY;
        }
        //values[0] = EMPTY;
        for (int i = 0; i < 10; i++) {
            memoryValues[i] = EMPTY;
        }
        appendTextToPane(ZERO);
        valuesPosition = 0;
        memoryPosition = 0;
        obtainingFirstNumber = true;
        negativeNumber = false;
        updateMemoryButtonsState();
        buttonDecimal.setEnabled(true);
        writeHistoryWithMessage(buttonChoice, false, " Cleared all values");
        confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
    }

    /**
     * The actions to perform when the Delete button is clicked
     *
     * @param actionEvent the click action
     */
    public void performDeleteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneTextValue = getTextPaneValue();
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(DELETE));
        } else if (textPaneTextValue.isEmpty()) {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(DELETE));
        } else {
            String value = textPaneTextValue;
            logValuesAtPosition(this, LOGGER);
            logValueInTextPane(this, LOGGER);
            String currentOperator = getActiveOperator();
            setActiveOperator(buttonChoice);
            if (!endsWithOperator(value)) {
                appendTextToPane(addThousandsDelimiter(value.substring(0, value.length() - 1), getThousandsDelimiter()), true);
            } else if (endsWithOperator(value)) {
                LOGGER.debug("Removing active operator from textPane");
                value = getValueWithoutAnyOperator(value);
                setValuesPosition(0);
                appendTextToPane(addThousandsDelimiter(value, getThousandsDelimiter()), true);
                setActiveOperator(EMPTY);
            }
            buttonDecimal.setEnabled(!isFractionalNumber(values[valuesPosition]));
            negativeNumber = values[valuesPosition].startsWith(SUBTRACTION);
            writeHistory(buttonChoice, false);
            if (getBasicPanelUnaryOperators().contains(buttonChoice) && currentOperator.isEmpty()) {
                setActiveOperator(EMPTY);
            } else {
                setActiveOperator(currentOperator);
            }
            updateMemoryButtonsState(); // memory buttons states affected!
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }

    /**
     * The actions to perform when the Addition button is clicked
     *
     * @param actionEvent the click action
     */
    public void performAddButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(ADDITION));
        } else if (textPaneValue.isEmpty()) {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(ADDITION));
        } else if (isMaximumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Maximum number met");
        }
        //else if (isMinimumValue())
        //{ confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + ". Minimum number met"); }
        else {
            if (isNoOperatorActive() && !values[0].isEmpty()) {
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                setObtainingFirstNumber(false);
                setNegativeNumber(false);
                setValuesPosition(1);
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (isOperatorActive() && !values[1].isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 + ...
                performOperation();
                writeContinuedHistory(ADDITION, getActiveOperator(), values[3], true);
                setActiveOperator(buttonChoice);
                if (isMaximumValue(values[3]) || isMinimumValue(values[3])) // we can add to the minimum number, not to the maximum
                {
                    values[2] = EMPTY;
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()));
                    finishedObtainingFirstNumber(false);
                } else {
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    finishedObtainingFirstNumber(true);
                }
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (!textPaneValue.isBlank() && values[0].isBlank()) {
                logEmptyValue(buttonChoice, this, LOGGER);
                LOGGER.info("Setting values[0] to textPane value");
                setActiveOperator(buttonChoice);
                appendTextToPane(textPaneValue + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                valuesPosition = 1;
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(ADDITION));
            }
        }
    }

    /**
     * The inner logic for addition.
     * This method performs the addition between values[0] and values[1],
     * clears any trailing zeroes if the result is a whole number (15.0000)
     * resets dotPRESSED + SPACE to false, enables buttonDot and stores the result
     * back in values[0]
     */
    private String performAdd()
    {
        BigDecimal firstNumber = new BigDecimal(values[0]);
        BigDecimal secondNumber = new BigDecimal(values[1]);
        String result = firstNumber.add(secondNumber).toPlainString();
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when the Subtraction button is clicked
     *
     * @param actionEvent the click action
     */
    public void performSubtractButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(SUBTRACTION));
        } else if (isMinimumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met");
        }
        //else if (isMaximumValue())
        //{ confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met"); }
        else {
            if (isNoOperatorActive() && !values[0].isEmpty()) {
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                negativeNumber = false;
                valuesPosition += 1;
            }
            // No operator pushed, textPane is empty, beginning a negative number
            else if (isNoOperatorActive() && getTextPaneValue().isEmpty()) {
                values[valuesPosition] = EMPTY; // TODO: Do place in negative sign for negative number
                appendTextToPane(buttonChoice);
                writeHistory(buttonChoice, true);
                negativeNumber = true;
                values[2] = EMPTY;
            } else if (isOperatorActive() && !values[1].isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 - ...
                performOperation();
                writeContinuedHistory(SUBTRACTION, getActiveOperator(), values[3], true);
                setActiveOperator(buttonChoice);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3])) {
                    values[2] = EMPTY;
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()));
                    finishedObtainingFirstNumber(false);
                } else {
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    finishedObtainingFirstNumber(true);
                }
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (isOperatorActive() && !negativeNumber) {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                writeHistory(buttonChoice, true);
                appendTextToPane(buttonChoice);
                negativeNumber = true;
            } else if (!getTextPaneValue().isBlank() && values[0].isBlank())//&& !isNegativeNumber(getTextPaneValue()))
            {
                LOGGER.warn("The user pushed subtract but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
                setNegativeNumber(CalculatorUtility.isNegativeNumber(values[valuesPosition]));
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()) + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                setValuesPosition(1);
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The inner logic for subtracting
     */
    private String performSubtract()
    {
        BigDecimal firstNumber = new BigDecimal(values[0]);
        BigDecimal secondNumber = new BigDecimal(values[1]);
        String result = firstNumber.subtract(secondNumber).toPlainString();
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        logOperation(LOGGER, this);
        return result;
    }

    /**
     * The actions to perform when the Multiplication button is clicked
     *
     * @param actionEvent the click action
     */
    public void performMultiplyButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION));
        } else if (getTextPaneValue().isEmpty()) {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION));
        } else if (isMaximumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met");
        } else if (isMinimumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met");
        } else {
            if (isNoOperatorActive() && !values[0].isBlank()) {
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                valuesPosition = 1;
            } else if (isOperatorActive() && !values[1].isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 ✕ ...
                performOperation();
                writeContinuedHistory(MULTIPLICATION, getActiveOperator(), values[3], true);
                setActiveOperator(buttonChoice);
                if (isMinimumValue(values[3]) || isMaximumValue(values[3])) {
                    values[2] = EMPTY;
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()));
                    finishedObtainingFirstNumber(false);
                } else {
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    finishedObtainingFirstNumber(true);
                }
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (!getTextPaneValue().isBlank() && values[0].isBlank()) {
                LOGGER.error("The user pushed multiple but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[0], getThousandsDelimiter()) + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                setValuesPosition(1);
            } else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(MULTIPLICATION));
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
        }
    }

    /**
     * The inner logic for multiplying
     */
    private String performMultiply()
    {
        BigDecimal firstNumber = new BigDecimal(values[0]);
        BigDecimal secondNumber = new BigDecimal(values[1]);
        String result = firstNumber.multiply(secondNumber).toPlainString();
        if (isFractionalNumber(result)) {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        logOperation(LOGGER, this);
        return result;
    }

    /**
     * The actions to perform when the Divide button is clicked
     *
     * @param actionEvent the click action
     */
    public void performDivideButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(DIVISION));
        } else if (textPaneValue.isEmpty()) {
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(DIVISION));
        } else if (isMinimumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Minimum number met");
        } else if (isMaximumValue(values[valuesPosition])) {
            confirm(this, LOGGER, PRESSED + SPACE + buttonChoice + " Maximum number met");
        } else {
            // No basic operator pushed, textPane has a value, and values is set
            if (isNoOperatorActive() && !values[0].isBlank()) {
                setActiveOperator(buttonChoice);
                appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                valuesPosition += 1;
            } else if (isOperatorActive() && !values[1].isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 / ...
                performOperation();
                if (isMinimumValue(values[3]) || isMaximumValue(values[3])) {
                    values[2] = EMPTY;
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()));
                    finishedObtainingFirstNumber(false);
                } else {
                    appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    finishedObtainingFirstNumber(true);
                }
                writeContinuedHistory(DIVISION, getActiveOperator(), values[3], true);
                setActiveOperator(buttonChoice);

                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (!textPaneValue.isBlank() && values[0].isBlank()) {
                LOGGER.error("The user pushed divide but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",", "");
                setActiveOperator(buttonChoice);
                appendTextToPane(values[0] + SPACE + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                obtainingFirstNumber = false;
                valuesPosition += 1;
            } else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(DIVISION));
            }
            buttonDecimal.setEnabled(true);
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }

    /**
     * The inner logic for dividing
     */
    private String performDivide()
    {
        String result = EMPTY;
        if (ZERO.equals(values[1])) {
            appendTextToPane(INFINITY, true);
            setObtainingFirstNumber(true);
            result = INFINITY;
        } else {
            BigDecimal firstNumber = new BigDecimal(values[0]);
            BigDecimal secondNumber = new BigDecimal(values[1]);
            result = firstNumber.divide(secondNumber, MathContext.DECIMAL128).toPlainString();
            if (isFractionalNumber(result)) {
                result = new BigDecimal(result).stripTrailingZeros().toPlainString();
            }
        }
        return result;
    }

    /**
     * The beginning actions to perform when clicking any number button
     *
     * @param actionEvent the click action
     */
    public void performNumberButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (!obtainingFirstNumber && !isPemdasActive) // second number
        {
            LOGGER.debug("obtaining second number...");
            if (getTextPaneValue().startsWith(SUBTRACTION) && getTextPaneValue().length() == 1) {
                clearTextInTextPane();
                appendTextToPane(SUBTRACTION);
            } else {
                clearTextInTextPane();
            }
            obtainingFirstNumber = true;
        }
        if (performInitialChecks()) {
            LOGGER.warn("Invalid entry in textPane. Clearing...");
            appendTextToPane(EMPTY);
            values[valuesPosition] = EMPTY;
            obtainingFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        values[valuesPosition] += buttonChoice;
        if (getTextPaneValue().startsWith(SUBTRACTION) && getTextPaneValue().length() == 1 ||
                getTextPaneValue().length() > 1 && getTextPaneValue().endsWith(SUBTRACTION))
            values[valuesPosition] = SUBTRACTION + values[valuesPosition];
        setNegativeNumber(CalculatorUtility.isNegativeNumber(values[valuesPosition]));
        if (BASE_BINARY == getCalculatorBase()) {
            var allowedLengthMinusNewLines = programmerPanel.getAllowedLengthsOfTextPane();
            // TEST: removed 0 from allowed lengths
            allowedLengthMinusNewLines.remove((Object) 0);
            String textPaneText = getTextPaneValue();
            LOGGER.debug("textPaneText: {}", textPaneText);
            if (allowedLengthMinusNewLines.contains(textPaneText.length())) {
                confirm(this, LOGGER, "Byte length " + allowedLengthMinusNewLines + " already reached");
            } else {
                programmerPanel.appendTextForProgrammerPanel(programmerPanel.separateBits(textPaneText + buttonChoice));
                writeHistory(buttonChoice, false);
                textPaneText = getTextPaneValue();
                if (allowedLengthMinusNewLines.contains(textPaneText.length())) {
                    setPreviousBase(BASE_BINARY); // set previousBase since number is fully formed
                    getValues()[getValuesPosition()] = textPaneText;
                    getValues()[getValuesPosition()] = convertValueToDecimal();
                    LOGGER.debug("Byte length {} reached with this input", allowedLengthMinusNewLines);
                }
                confirm(this, LOGGER, "Pressed " + buttonChoice);
            }
        } else if (BASE_OCTAL == getCalculatorBase()) {
            LOGGER.warn("IMPLEMENT Octal number button actions");
        } else if (BASE_DECIMAL == getCalculatorBase()) {
            appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()));
        } else /* (HEXADECIMAL == calculator.getCalculatorBase()) */ {
            LOGGER.warn("IMPLEMENT Hexadecimal number button actions");
        }

        writeHistory(buttonChoice, false);
        updateMemoryButtonsState();
        confirm(this, LOGGER, pressedButton(buttonChoice));
    }

    /**
     * The actions to perform when you click Negate
     *
     * @param actionEvent the click action
     */
    public void performNegateButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(NEGATE));
        } else if (getTextPaneValue().isEmpty()) {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(NEGATE));
        } else {
            if (!getAppropriateValue().isEmpty()) {
                String currentOperator = getActiveOperator();
                setActiveOperator(buttonChoice);
                performOperation();
                appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()), true);
                setNegativeNumber(CalculatorUtility.isNegativeNumber(values[3]));
                getButtonDecimal().setEnabled(!isFractionalNumber(values[3]));
                writeHistory(buttonChoice, false);
                if (getBasicPanelUnaryOperators().contains(buttonChoice) && currentOperator.isEmpty()) {
                    setActiveOperator(EMPTY);
                } else {
                    setActiveOperator(currentOperator);
                }
                confirm(this, LOGGER, pressedButton(buttonChoice));
            } else if (isOperatorActive()) {
                confirm(this, LOGGER, cannotPerformOperation(NEGATE));
            }

        }
    }

    /**
     * The inner logic for negating a number
     *
     * @return the negated value
     */
    public String performNegate()
    {
        String valueToNegate = getAppropriateValue();
        if (valueToNegate.isEmpty()) return valueToNegate;
        if (calculatorBase != BASE_DECIMAL)
            valueToNegate = convertFromBaseToBase(calculatorBase, BASE_DECIMAL, valueToNegate);

        if (CalculatorUtility.isNegativeNumber(valueToNegate)) {
            setNegativeNumber(false);
            return convertToPositive(valueToNegate);
        } else {
            setNegativeNumber(true);
            return convertToNegative(valueToNegate);
        }
    }

    /**
     * The actions to perform when the Dot button is click
     *
     * @param actionEvent the click action
     */
    public void performDecimalButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            clearTextInTextPane();
            obtainingFirstNumber = true;
        }
        performDecimal(buttonChoice);
        writeHistory(buttonChoice, false);
        confirm(this, LOGGER, PRESSED + SPACE + buttonChoice);
    }

    /**
     * The inner logic of performing Dot actions
     *
     * @param buttonChoice the button choice
     */
    public void performDecimal(String buttonChoice)
    {
        final var appropriateValue = values[valuesPosition];
        if (appropriateValue.isBlank() && !negativeNumber) // !isNegating
        {
            values[valuesPosition] = ZERO + DECIMAL;
            appendTextToPane(values[valuesPosition]);
        } else if (appropriateValue.isBlank() && negativeNumber) // isNegating
        {
            values[valuesPosition] = SUBTRACTION + ZERO + DECIMAL;
            appendTextToPane(values[valuesPosition]);
        } else {
            values[valuesPosition] = appropriateValue + buttonChoice;
            appendTextToPane(addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter()));
        }
        buttonDecimal.setEnabled(false); // deactivate button now that its active for this number
    }

    /**
     * The actions to perform when the Equals button is clicked
     *
     * @param actionEvent the click action
     */
    public void performEqualsButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (textPaneContainsBadText()) {
            confirm(this, LOGGER, cannotPerformOperation(EQUALS));
        } else if (values[0].isEmpty() || values[1].isEmpty()) {
            logEmptyValue(buttonChoice, this, LOGGER);
            appendTextToPane(ENTER_A_NUMBER);
            confirm(this, LOGGER, cannotPerformOperation(buttonChoice));
        } else {
            performOperation();
            if (!textPaneContainsBadText())
                appendTextToPane(addThousandsDelimiter(values[3], getThousandsDelimiter()));
            writeContinuedHistory(EQUALS, getActiveOperator(), values[3], false);
            resetValues(true);
            updateMemoryButtonsState();
            confirm(this, LOGGER, pressedButton(buttonChoice));
        }
    }

    /**
     * This method determines which operation to perform,
     * performs that operation, stores the result in v[3]
     * and resets any necessary flags. Operations are
     * alphabetized.
     */
    public String performOperation()
    {
        String result = EMPTY;
        switch (getActiveOperator()) {
            case ADDITION -> {
                result = performAdd();
            }
            case AND -> {
                result = programmerPanel.performAnd();
            }
            case DIVISION -> {
                result = performDivide();
            }
            case FRACTION -> {
                result = basicPanel.performFraction();
            }
            case MEMORY_ADD -> {
                result = performMemoryAdd();
            }
            case MEMORY_SUBTRACT -> {
                result = performMemorySubtract();
            }
            case MODULUS -> {
                result = programmerPanel.performModulus();
            }
            case MULTIPLICATION -> {
                result = performMultiply();
            }
            case NEGATE -> {
                result = performNegate();
            }
            case OR -> {
                result = programmerPanel.performOr();
            }
            case PERCENT -> {
                result = basicPanel.performPercent();
            }
            case SQUARED -> {
                result = basicPanel.performSquared();
            }
            case SQUARE_ROOT -> {
                result = performSquareRoot();
            }
            case SUBTRACTION -> {
                result = performSubtract();
            }
            case XOR -> {
                result = programmerPanel.performXor();
            }
            default -> LOGGER.warn("Unknown operation: {}", getActiveOperator());
        }
        values[3] = result;
        logOperation(LOGGER, this);
        return result;
    }
    /**************** END BUTTON ACTIONS ****************/

    /**************** HELPER METHODS ****************/
    /**
     * Updates the look and feel of the Calculator
     */
    public void updateLookAndFeel()
    {
        SwingUtilities.updateComponentTreeUI(this);
        pack();
    }

    // TODO: Verify.

    /**
     * This method returns the value at the current
     * position. If the value is empty and no operator
     * has been selected, it returns the text pane value.
     *
     * @return the value at the current position or
     * the text pane value
     */
    public String getAppropriateValue()
    {
        String currentNum = getValueAt();
        if (!currentNum.isEmpty()) return currentNum;
        if (valuesPosition == 1) currentNum = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        if (currentNum.isEmpty() && isNoOperatorActive())
            currentNum = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        else if (!currentNum.isEmpty() && isOperatorActive())
            currentNum = removeThousandsDelimiter(getValueWithoutAnyOperator(getTextPaneValue()), getThousandsDelimiter());
            // rare cases when v[0] & v[1] are empty but textPane has a value and operator is active
        else if (currentNum.isEmpty() && isOperatorActive())
            currentNum = removeThousandsDelimiter(getTextPaneValue(), getThousandsDelimiter());
        return currentNum;
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
        return !values[2].isEmpty();
    }

    /**
     * This method returns true if no operator is active
     * independent of the current view.
     *
     * @return true if no operator is active, false otherwise
     */
    public boolean isNoOperatorActive()
    {
        return values[2].isEmpty();
    }

    /**
     * This method returns the operator
     * that was pushed.
     *
     * @return the operator that was pushed
     */
    public String getActiveOperator()
    {
        return values[2];
    }

    /**
     * Sets the active operator
     *
     * @param operator the operator to set
     */
    public void setActiveOperator(String operator)
    {
        values[2] = operator;
    }

    /**
     * Returns a list of operators found
     * only on the basic panel
     *
     * @return list of basic panel operators
     */
    public List<String> getBasicPanelOperators()
    {
        return List.of(PERCENT, SQUARE_ROOT, SQUARED, FRACTION,
                ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION);
    }

    /**
     * Returns a list of operators found
     * only on the programmer panel
     *
     * @return list of programmer panel operators
     */
    public List<String> getProgrammerPanelOperators()
    {
        return List.of(LSH, RSH, OR, XOR, NOT, AND, MODULUS, ROL, ROR);
    }

    /**
     * Returns a list of operators found
     * only on the scientific panel
     *
     * @return list of scientific panel operators
     */
    public List<String> getScientificPanelOperators()
    {
        LOGGER.warn("Implement");
        return Collections.emptyList();
    }

    /**
     * Returns values[valuesPosition]
     *
     * @return the value at valuesPosition
     */
    public String getValueAt()
    {
        return values[valuesPosition];
    }

    /**
     * Returns values[vP] for the given position
     *
     * @param vP the position to get
     * @return the value at vP
     */
    public String getValueAt(int vP)
    {
        return values[vP];
    }

    /**
     * This method resets default values
     *
     * @param softReset used to determine if the
     *                  decimal button and negative number flag
     *                  should be reset or not.
     */
    public void resetValues(boolean softReset)
    {
        if (softReset) {
            buttonDecimal.setEnabled(!isFractionalNumber(values[3]));
            setNegativeNumber(CalculatorUtility.isNegativeNumber(values[3]));
        } else {
            buttonDecimal.setEnabled(true);
            setNegativeNumber(false);
        }
        setObtainingFirstNumber(false);
        values[0] = EMPTY;
        values[1] = EMPTY;
        values[2] = EMPTY;
        values[3] = EMPTY;
        valuesPosition = 0;
        LOGGER.debug("Reset values");
    }

    /**
     * Records the buttonChoice to the appropriate history panel.<br>
     * Example Scenario:<br>
     * Press 5, History shows: (5) Result: 5<br>
     * Press +, History shows: (+) Result: 5 +
     *
     * @param buttonChoice String the button choice.
     */
    public void writeHistory(String buttonChoice, boolean addButtonChoiceToEnd)
    {
        writeHistoryWithMessage(buttonChoice, addButtonChoiceToEnd, null);
    }

    /**
     * Records the buttonChoice to the appropriate history panel
     * with a specific message instead of the default text
     *
     * @param buttonChoice         String the buttonChoice
     * @param addButtonChoiceToEnd boolean to add the buttonChoice to the end
     * @param message              String the message to display
     */
    public void writeHistoryWithMessage(String buttonChoice, boolean addButtonChoiceToEnd, String message)
    {
        LOGGER.info("Writing history");
        String paneValue;
        if (message == null) {
            LOGGER.debug("no message");
            paneValue = addThousandsDelimiter(values[valuesPosition], getThousandsDelimiter());
        } else {
            LOGGER.debug("with a specific message: {}", message);
            paneValue = SPACE + message;
        }
        if (addButtonChoiceToEnd) {
            paneValue += SPACE + buttonChoice;
        }
        var currentHistoryText = getHistoryTextPane().getText();
        if (message == null) {
            paneValue = SPACE + RESULT + paneValue;
        }
        getHistoryTextPane().setText(
                currentHistoryText + NEWLINE +
                        LEFT_PARENTHESIS + buttonChoice + RIGHT_PARENTHESIS + paneValue
        );
    }

    /**
     * Records the buttonChoice specifically if it was a continued operation
     * Ex: Already entered 5 + 6, then press +, History shows: (+) Result: 5 + 6 = 11 +
     *
     * @param prevOperation              String whether to display the operation or equals
     * @param operation                  String the operation performed (add, subtract, etc)
     * @param result                     double the result from the operation
     * @param addContinuedOperationToEnd boolean whether to append the operation to the end
     */
    public void writeContinuedHistory(String prevOperation, String operation, Object result, boolean addContinuedOperationToEnd)
    {
        LOGGER.info("Writing continued history");
        var paneValue = addThousandsDelimiter(values[0], getThousandsDelimiter()) + SPACE + operation + SPACE + addThousandsDelimiter(values[1], getThousandsDelimiter()) + SPACE + EQUALS + SPACE + addThousandsDelimiter(String.valueOf(result), getThousandsDelimiter());
        if (addContinuedOperationToEnd) {
            paneValue += SPACE + prevOperation;
        }
        var currentHistory = getHistoryTextPane().getText();
        getHistoryTextPane().setText(
                currentHistory + NEWLINE +
                        LEFT_PARENTHESIS + prevOperation + RIGHT_PARENTHESIS + " Result: " + paneValue
        );
    }

    /**
     * @param firstNumberObtained boolean whether
     *                            the first number has been obtained or not
     */
    public void finishedObtainingFirstNumber(boolean firstNumberObtained)
    {
        LOGGER.debug("Finished obtaining first number:{}", firstNumberObtained);
        if (firstNumberObtained) {
            valuesPosition = 1;
            setObtainingFirstNumber(false);
            //buttonDecimal.setEnabled(true);
        } else {
            valuesPosition = 0;
            setObtainingFirstNumber(true);
            //buttonDecimal.setEnabled(!isFractionalNumber(values[valuesPosition]));
        }
    }

    /**
     * Determines if any value is saved in memory
     *
     * @return boolean if any memory value has a value
     */
    public boolean isMemoryValuesEmpty()
    {
        var result = true;
        for (int i = 0; i < 10; i++) {
            if (!memoryValues[i].isEmpty()) {
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
            if (!memoryValues[i].isEmpty()) {
                lowestMemory = i;
                break;
            }
        }
        LOGGER.debug("Lowest position in memory is {}", lowestMemory);
        return lowestMemory;
    }

    /**
     * Returns all the number buttons
     *
     * @return Collection of all number buttons
     */
    public List<JButton> getNumberButtons()
    {
        return Arrays.asList(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9);
    }

    /**
     * Returns all the memory buttons and the history button
     *
     * @return List of buttons in the memory panel
     */
    public List<JButton> getAllMemoryPanelButtons()
    {
        return Arrays.asList(buttonMemoryStore, buttonMemoryClear, buttonMemoryRecall, buttonMemoryAddition, buttonMemorySubtraction, buttonHistory);
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
        if (calculatorView == VIEW_PROGRAMMER) {
            clearButtonActions(programmerPanel.getAllProgrammerOperatorButtons());
            clearButtonActions(programmerPanel.getAllHexadecimalButtons());
            LOGGER.debug("Programmer Panel Buttons cleared of action listeners");
        }
    }

    /**
     * Clears all actions from the given buttons
     *
     * @param buttons the buttons to clear
     */
    protected void clearButtonActions(List<JButton> buttons)
    {
        buttons.forEach(button -> Arrays.stream(button.getActionListeners())
                .forEach(button::removeActionListener));
    }

    public List<String> getBasicPanelUnaryOperators()
    {
        return List.of(PERCENT, SQUARE_ROOT, SQUARED, FRACTION, NEGATE, DELETE);
    }

    public List<String> getBasicPanelBinaryOperators()
    {
        return List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS);
    }

    /**
     * When we hit a number button this method is called
     * to ensure valid entries are allowed and any previous
     * errors or unexpected conditions are cleared
     */
    public boolean performInitialChecks()
    {
        LOGGER.debug("Performing initial checks...");
        if (ZERO.equals(getTextPaneValue())) {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            appendTextToPane(EMPTY);
            //obtainingFirstNumber = true;
            //buttonDecimal.setEnabled(true);
        }
        boolean issueFound = false;
        if (textPaneContainsBadText()) {
            LOGGER.debug("TextPane contains bad text");
            issueFound = true;
        }
//        else if (values[0].isEmpty() && valuesPosition == 1)
//        {
//            LOGGER.debug("v[0] is empty. placing v[1] in v[0]. clearing v[1].");
//            values[0] = values[1];
//            values[1] = EMPTY;
//            issueFound = true;
//        }
//        else if (checkValueLength())
//        {
//            LOGGER.info("Highest size of value has been met");
//            issueFound = true;
//        }
        LOGGER.debug("Initial checks result: {}", issueFound);
        return issueFound;
    }

    // TODO: Check this method. What should it truly be checking for/against?

    /**
     * Checks to see if the max length of a value[position] has been met.
     * The highest number is 9,999,999, with a length of 7 and the
     * lowest number is 0.0000001. So if the current value has a
     * length of 7, the max length has been met, and no more
     * digits will be allowed. Remember that the values[position] will not
     * contain any commas, so the official length will be 7.
     *
     * @return boolean if the max length has been met
     */
    private boolean checkValueLength()
    {
        LOGGER.debug("Checking if max size (7) is met...");
        LOGGER.debug("values[{}] left of decimal length: {}", valuesPosition, getNumberOnLeftSideOfDecimal(values[valuesPosition]).length());
        LOGGER.debug("values[{}] right of decimal length: {}", valuesPosition, getNumberOnRightSideOfDecimal(values[valuesPosition]).length());
        return getNumberOnLeftSideOfDecimal(values[valuesPosition]).length() > 7 ||
                getNumberOnRightSideOfDecimal(values[valuesPosition]).length() > 7;
    }

    /**
     * Tests whether the textPane contains a String which shows a previous error.
     * Takes into account the current panel
     *
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
     * Returns the bad text if the value
     * is equal to one of the known bad texts.
     * If no value is sent in, the textPane
     * value will be tested.
     *
     * @param valueToTest the value to test
     * @return the bad text or the value
     */
    public String getBadText(String valueToTest)
    {
        String badText = valueToTest.isEmpty() ? getTextPaneValue() : valueToTest;
        if (CANNOT_DIVIDE_BY_ZERO.equals(badText)) badText = CANNOT_DIVIDE_BY_ZERO;
        else if (NOT_A_NUMBER.equals(badText)) badText = NOT_A_NUMBER;
        else if (NUMBER_TOO_BIG.equals(badText)) badText = NUMBER_TOO_BIG;
        else if (ENTER_A_NUMBER.equals(badText)) badText = ENTER_A_NUMBER;
        else if (ONLY_POSITIVES.equals(badText)) badText = ONLY_POSITIVES;
        else if (Texts.ERROR.equals(badText)) badText = Texts.ERROR;
        else if (INFINITY.equals(badText)) badText = INFINITY;
        else badText = EMPTY;
        return badText;
    }

    /**
     * Returns a list of all bad texts
     *
     * @return list of bad texts
     */
    public List<String> badTexts()
    {
        return List.of(CANNOT_DIVIDE_BY_ZERO, NOT_A_NUMBER, NUMBER_TOO_BIG,
                ENTER_A_NUMBER, ONLY_POSITIVES, Texts.ERROR, INFINITY);
    }


    /**
     * Returns the delimiter used to separate thousands
     *
     * @return the delimiter to use for thousands
     */
    public String getThousandsDelimiter()
    {
        // TODO: Setup menu option to allow user to choose this value
        return COMMA;
    }

    /**
     * Returns the delimiter used to separate fractional values
     *
     * @return the delimiter to use for fractional values
     */
    public String getFractionalDelimiter()
    {
        // TODO: Setup menu option to allow user to choose this value
        return DECIMAL;
    }

    /**
     * Determines if the OS is Mac or not
     *
     * @return boolean if is running on Mac
     */
    @Deprecated(since = "Use OSDetector", forRemoval = true)
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
        if (calculatorByte == BYTE_BYTE) bits = 8;
        else if (calculatorByte == BYTE_WORD) bits = 16;
        else if (calculatorByte == BYTE_DWORD) bits = 32;
        else if (calculatorByte == BYTE_QWORD) bits = 64;
        if (bits < base2Length) {
            LOGGER.debug("base2Length: {}", base2Length);
            if (base2Length <= 8) {
                bits = 8;
                setCalculatorByte(BYTE_BYTE);
            } else if (base2Length <= 16) {
                bits = 16;
                setCalculatorByte(BYTE_WORD);
            } else if (base2Length <= 32) {
                bits = 32;
                setCalculatorByte(BYTE_DWORD);
            } else { //if (base2Length <= 64) {
                bits = 64;
                setCalculatorByte(BYTE_QWORD);
            }
        }
        return bits;
    }

    /**
     * Converts the current value to its binary representation
     *
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToBinary()
    {
        String valueToConvert = values[valuesPosition];
        if (valueToConvert.isEmpty()) return EMPTY;
        String base2Number = Integer.toBinaryString(Integer.parseInt(valueToConvert));
        base2Number = adjustBinaryNumber(base2Number);
        LOGGER.info("Converting {}({}) to {}({})", BASE_DECIMAL.getValue(), valueToConvert,
                BASE_BINARY.getValue(), base2Number);
        return base2Number;
    }

    /**
     * Converts the current value to its octal representation
     *
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToOctal()
    {
        String valueToConvert = values[valuesPosition];
        if (valueToConvert.isEmpty()) return EMPTY;
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_OCTAL.getValue());
        String base8Number = Integer.toOctalString(Integer.parseInt(valueToConvert));
        LOGGER.debug("convert from({}) to({}) = {}", BASE_DECIMAL.getValue(), BASE_OCTAL.getValue(), base8Number);
        LOGGER.info("The number {} in base 10 is {} in base 8.", valueToConvert, base8Number);
        return base8Number;
    }

    /**
     * Converts the current value to its decimal representation
     *
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToDecimal()
    {
        String valueToConvert = values[valuesPosition];
        if (valueToConvert.isEmpty()) return EMPTY;
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_DECIMAL.getValue());
        String base10Number = Integer.toString(Integer.parseInt(valueToConvert, getPreviousRadix()), 10);
        LOGGER.debug("convert from({}) to({}) = {}", previousBase.getValue(), BASE_DECIMAL.getValue(), base10Number);
        LOGGER.info("The number {} in base {} is {} in base 10", valueToConvert, previousBase.getRadix(), base10Number);
        return base10Number;
    }

    /**
     * Converts the current value to its hexadecimal representation
     *
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToHexadecimal()
    {
        String valueToConvert = values[valuesPosition];
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
     *
     * @param fromBase       the base to convert from
     * @param toBase         the base to convert to
     * @param valueToConvert the value to convert
     * @return the converted value or an empty string
     */
    public String convertFromBaseToBase(CalculatorBase fromBase, CalculatorBase toBase, String valueToConvert)
    {
        valueToConvert = removeThousandsDelimiter(valueToConvert, getThousandsDelimiter()).replace(SPACE, EMPTY);
        if (valueToConvert.isEmpty()) return EMPTY;
        LOGGER.debug("converting {} from {} to {}", valueToConvert, fromBase.getValue(), toBase.getValue());
        String convertedNumber;
//        if (!isDecimalNumber(valueToConvert))
//            convertedNumber = Integer.toString(BigInteger.parseInt(valueToConvert, getPreviousRadix(fromBase)), getPreviousRadix(toBase));
//        else {
//            var value = clearZeroesAndDecimalAtEnd(valueToConvert);
//            convertedNumber = Integer.toString(Integer.parseInt(value, getPreviousRadix(fromBase)), getPreviousRadix(toBase));
//        }
        if (!isFractionalNumber(valueToConvert)) {
            // use BigInteger for arbitrarily large integers
            convertedNumber = new BigInteger(valueToConvert, getPreviousRadix(fromBase))
                    .toString(getPreviousRadix(toBase));
        } else {
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
                    if (digit < 0) {
                        digit = 0;
                    }
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
     *
     * @return the previous radix
     */
    public int getPreviousRadix()
    {
        return getPreviousRadix(previousBase);
    }

    /**
     * Returns the previous radix based on the provided base
     *
     * @return the previous radix
     */
    public int getPreviousRadix(CalculatorBase base)
    {
        return switch (base) {
            case BASE_BINARY -> BASE_BINARY.getRadix();
            case BASE_OCTAL -> BASE_OCTAL.getRadix();
            case BASE_DECIMAL -> BASE_DECIMAL.getRadix();
            case BASE_HEXADECIMAL -> BASE_HEXADECIMAL.getRadix();
        };
    }

    /**
     * Returns the current history text pane text.
     *
     * @return the history text pane text
     */
    public String getHistoryTextPaneValue()
    {
        return historyTextPane.getText();
    }

    /**
     * Used when you want the text pane value without
     * having to think about which panel is current or
     * anything else for that matter. Just the value.
     *
     * @return the value without new lines or whitespace
     */
    public String getTextPaneValue()
    {
        if (calculatorView == VIEW_BASIC) {
            return textPane.getText()
                    .replaceAll(NEWLINE, EMPTY)
                    .strip();
        } else if (calculatorView == VIEW_PROGRAMMER) {
            return getTextPaneValueForProgrammerPanel()
                    .replaceAll(NEWLINE, EMPTY)
                    .strip();
        } else {
            LOGGER.warn("Implement");
            return EMPTY;
        }
    }

    /**
     * Returns the value in the text pane when panel is programmer panel.
     * Value in text pane for programmer panel is as follows:
     * Byte Space Space Base NEWLINE NEWLINE value[valuePosition] NEWLINE
     *
     * @return the value in the programmer text pane
     */
    private String getTextPaneValueForProgrammerPanel()
    {
        String currentValue = EMPTY;
        try {
            LOGGER.debug("calculatorBase: {}", calculatorBase);
            String[] textPaneValue = textPane.getText().split(NEWLINE);
            switch (calculatorBase) {
                case BASE_BINARY -> {
                    LOGGER.debug("calculatorByte: {}", calculatorByte);
                    switch (calculatorByte) {
                        case BYTE_BYTE,
                             BYTE_WORD -> {
                            currentValue = textPaneValue[2];
                        }
                        case BYTE_DWORD -> {
                            currentValue = textPaneValue[2] + textPaneValue[3];
                        }
                        case BYTE_QWORD -> {
                            currentValue = textPaneValue[2] + textPaneValue[3]
                                    + textPaneValue[4] + textPaneValue[5];
                        }
                    }
                    currentValue = currentValue.replace(SPACE, EMPTY);
                }
                case BASE_OCTAL,
                     BASE_DECIMAL,
                     BASE_HEXADECIMAL -> {
                    currentValue = removeThousandsDelimiter(textPaneValue[2], getThousandsDelimiter());
                }
            }
            if (currentValue.isEmpty()) {
                LOGGER.warn("Attempted to retrieve value from text pane but it was empty. Returning blank.");
                return EMPTY;
            }
        }
        catch (ArrayIndexOutOfBoundsException ae1) {
            try {
                var splitTextValue = textPane.getText().split(NEWLINE);
                // TODO: Rework to not use Strings
                var twoSpaces = SPACE.repeat(2);
                var textPaneTopRowExpectedValues = List.of(
                        BYTE_BYTE.getValue() + twoSpaces + BASE_BINARY.getValue(), "Byte  Octal", "Byte  Decimal", "Byte  Hexadecimal"
                        , "Word  Binary", "Word  Octal", "Word  Decimal", "Word  Hexadecimal"
                        , "DWord  Binary", "DWord  Octal", "DWord  Decimal", "DWord  Hexadecimal"
                        , "QWord  Binary", "QWord  Octal", "QWord  Decimal", "QWord  Hexadecimal");
                return splitTextValue.length == 1 && textPaneTopRowExpectedValues.contains(splitTextValue[0])
                        ? EMPTY
                        : removeThousandsDelimiter(splitTextValue[2], getThousandsDelimiter());
            }
            catch (ArrayIndexOutOfBoundsException ae2) {
                logException(new CalculatorError("Attempted to retrieve value from text pane but got ArrayIndexOutOfBoundsException. Returning blank."), LOGGER);
                return EMPTY;
            }
        }
        catch (Exception e) {
            logException(e, LOGGER);
            return EMPTY;
        }
        return currentValue;
    }

    /**
     * Returns the text in the current panel's history pane
     *
     * @return the history text for the current panel
     */
    public String getHistoryPaneTextWithoutNewLineCharacters()
    {
        if (List.of(VIEW_BASIC, VIEW_PROGRAMMER, VIEW_SCIENTIFIC).contains(calculatorView)) {
            return historyTextPane.getText().replace(NEWLINE, EMPTY).strip();
        } else {
            LOGGER.warn("Add other panels history pane if intended to have one");
            return EMPTY;
        }
    }


    /**
     * Resets the operators to the boolean passed in
     *
     * @param reset a boolean to reset the operators to
     */
    @Deprecated(since = "Use resetValues()")
    public void resetOperators(boolean reset)
    {

        values[2] = EMPTY; // operator
        LOGGER.debug("All operators reset to {}", reset);
    }


    /**
     * This method checks if the given value
     * ends with any operator from the current
     * panel. This will only possibly effect
     * the basic, programmer, and scientific
     * panels.
     *
     * @param value the value to check
     * @return true if the value ends with an operator,
     * false otherwise
     */
    public boolean endsWithOperator(String value)
    {
        if (value == null || value.isEmpty()) return false;
        List<String> allOperators = new ArrayList<>();
        return switch (calculatorView) {
            case VIEW_BASIC -> getBasicPanelOperators().stream().anyMatch(value::endsWith);
            case VIEW_PROGRAMMER -> {
                allOperators.addAll(getBasicPanelOperators());
                allOperators.addAll(getProgrammerPanelOperators());
                yield allOperators.stream().anyMatch(value::endsWith);
            }
            case VIEW_SCIENTIFIC -> {
                allOperators.addAll(getBasicPanelOperators());
                allOperators.addAll(getScientificPanelOperators());
                yield allOperators.stream().anyMatch(value::endsWith);
            }
            default -> {
                LOGGER.debug("Testing all operators");
                allOperators.addAll(getBasicPanelOperators());
                allOperators.addAll(getProgrammerPanelOperators());
                allOperators.addAll(getScientificPanelOperators());
                yield allOperators.stream().anyMatch(value::endsWith);
            }
        };
    }


    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and if true, updates the
     * values[valuesPosition] with the text provided, without
     * any commas.
     *
     * @param text the text to add
     */
    public void appendTextToPane(String text, boolean updateValues)
    {
        appendTextToPane(text);
        LOGGER.debug("Text appended to pane. Updating values? {}", updateValues);
        boolean negativeControl = CalculatorUtility.isNegativeNumber(text);
        // If the result is not badText, store value in appropriate location
        int currentValuesPosition = valuesPosition;
        if (updateValues && (!getBadText(text).equals(text) || text.isEmpty())) {
            if (getBasicPanelUnaryOperators().contains(getActiveOperator()) ||
                    (getValueAt().isEmpty() && getBasicPanelBinaryOperators().contains(getActiveOperator()))) {
                values[valuesPosition] = removeThousandsDelimiter(getValueWithoutAnyOperator(text), getThousandsDelimiter());
                values[3] = EMPTY;
            } else {
                values[3] = removeThousandsDelimiter(getValueWithoutAnyOperator(text), getThousandsDelimiter());
                valuesPosition = 3;
            }
        }
        if (negativeControl) {
            values[valuesPosition] = convertToNegative(values[valuesPosition]);
        }
        logValuesAtPosition(this, LOGGER);
        setValuesPosition(currentValuesPosition);
    }

    /**
     * This is the default method to add text to the textPane
     * Adds the text appropriately and only what you provide.
     * Commas are not added here. Decimals are not added here.
     * What it does do is keep the value displaying in the
     * same base and/or byte that is currently selected.
     *
     * @param text the text to add
     */
    public void appendTextToPane(String text)
    {
        LOGGER.debug("adding text: '{}'", text);
        if (calculatorView == VIEW_BASIC) {
            textPane.setText(NEWLINE + text);
        } else if (calculatorView == VIEW_PROGRAMMER) {
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
        } else {
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
        if (calculatorView == VIEW_BASIC) {
            appendTextToPane(EMPTY);
        } else if (calculatorView == VIEW_PROGRAMMER) {
            programmerPanel.appendTextForProgrammerPanel(EMPTY);
        }
        buttonDecimal.setEnabled(true); // natural side effect of clearing textPane
    }

    /**
     * Closes the history pane if it is open
     *
     * @param actionEvent the action event
     */
    private void closeHistoryIfOpen(ActionEvent actionEvent)
    {
        if (actionEvent != null) {
            buttonHistory.setText(HISTORY_OPEN);
            if (calculatorView == VIEW_BASIC)
                performHistoryAction(actionEvent);
            else if (calculatorView == VIEW_PROGRAMMER)
                performHistoryAction(actionEvent);
        }
    }

    /**
     * Sets the state when divided by zero occurs
     */
    public void dividedByZero()
    {
//        appendTextToPane(INFINITY);
//        values[0] = EMPTY;
//        values[1] = EMPTY;
//        values[2] = EMPTY;
//        values[3] = EMPTY;
        setObtainingFirstNumber(true);
    }

    /**
     * Updates the state of the memory buttons based
     * on whether there is a value stored in memory
     * and whether there is a value present or not.
     */
    public void updateMemoryButtonsState()
    {
        boolean valueIsNotEmpty = !getAppropriateValue().isEmpty();
        boolean memoryIsStored = !isMemoryValuesEmpty();
        buttonMemoryRecall.setEnabled(memoryIsStored);
        buttonMemoryClear.setEnabled(memoryIsStored);
        buttonMemoryAddition.setEnabled(valueIsNotEmpty && memoryIsStored);
        buttonMemorySubtraction.setEnabled(valueIsNotEmpty && memoryIsStored);
    }
    /**************** END HELPER METHODS ****************/

    /**************** GETTERS ****************/
    public String getHelpString()
    {
        return helpString;
    }

    /**************** SETTERS ****************/
    public void setHelpString(String helpString)
    {
        this.helpString = helpString;
        LOGGER.debug("Help string set");
    }

    public JButton getButton0()
    {
        return button0;
    }

    public JButton getButton1()
    {
        return button1;
    }

    public JButton getButton2()
    {
        return button2;
    }

    public JButton getButton3()
    {
        return button3;
    }

    public JButton getButton4()
    {
        return button4;
    }

    public JButton getButton5()
    {
        return button5;
    }

    public JButton getButton6()
    {
        return button6;
    }

    public JButton getButton7()
    {
        return button7;
    }

    public JButton getButton8()
    {
        return button8;
    }

    public JButton getButton9()
    {
        return button9;
    }

    public JButton getButtonClear()
    {
        return buttonClear;
    }

    public JButton getButtonClearEntry()
    {
        return buttonClearEntry;
    }

    public JButton getButtonDelete()
    {
        return buttonDelete;
    }

    public JButton getButtonSquared()
    {
        return buttonSquared;
    }

    public JButton getButtonDecimal()
    {
        return buttonDecimal;
    }

    public JButton getButtonFraction()
    {
        return buttonFraction;
    }

    public JButton getButtonPercent()
    {
        return buttonPercent;
    }

    public JButton getButtonSquareRoot()
    {
        return buttonSquareRoot;
    }

    public JButton getButtonMemoryClear()
    {
        return buttonMemoryClear;
    }

    public JButton getButtonMemoryRecall()
    {
        return buttonMemoryRecall;
    }

    public JButton getButtonMemoryStore()
    {
        return buttonMemoryStore;
    }

    public JButton getButtonMemoryAddition()
    {
        return buttonMemoryAddition;
    }

    public JButton getButtonMemorySubtraction()
    {
        return buttonMemorySubtraction;
    }

    public JButton getButtonHistory()
    {
        return buttonHistory;
    }

    public JButton getButtonAdd()
    {
        return buttonAdd;
    }

    public JButton getButtonSubtract()
    {
        return buttonSubtract;
    }

    public JButton getButtonMultiply()
    {
        return buttonMultiply;
    }

    public JButton getButtonDivide()
    {
        return buttonDivide;
    }

    public JButton getButtonEquals()
    {
        return buttonEquals;
    }

    public JButton getButtonNegate()
    {
        return buttonNegate;
    }

    public JButton getButtonBlank1()
    {
        return buttonBlank1;
    }

    public JButton getButtonBlank2()
    {
        return buttonBlank2;
    }

    public String[] getValues()
    {
        return values;
    }

    private void setValues(String[] values)
    {
        this.values = values;
    }

    public String[] getMemoryValues()
    {
        return memoryValues;
    }

    private void setMemoryValues(String[] memoryValues)
    {
        this.memoryValues = memoryValues;
    }

    public int getValuesPosition()
    {
        return valuesPosition;
    }

    public void setValuesPosition(int valuesPosition)
    {
        this.valuesPosition = valuesPosition;
        LOGGER.debug("ValuesPosition set to {}", valuesPosition);
    }

    public int getMemoryPosition()
    {
        return memoryPosition;
    }

    public void setMemoryPosition(int memoryPosition)
    {
        this.memoryPosition = memoryPosition;
        LOGGER.debug("MemoryPosition set to {}", memoryPosition);
    }

    public int getMemoryRecallPosition()
    {
        return memoryRecallPosition;
    }

    public void setMemoryRecallPosition(int memoryRecallPosition)
    {
        this.memoryRecallPosition = memoryRecallPosition;
        LOGGER.debug("MemoryRecallPosition set to {}", memoryRecallPosition);
    }

    public JTextPane getTextPane()
    {
        return textPane;
    }

    public void setTextPane(JTextPane textPane)
    {
        this.textPane = textPane;
        LOGGER.debug("TextPane set");
    }

    public CalculatorView getCalculatorView()
    {
        return calculatorView;
    }

    public void setCalculatorView(CalculatorView calculatorView)
    {
        this.calculatorView = calculatorView;
        LOGGER.debug("CalculatorView set to {}", calculatorView);
    }

    public CalculatorBase getCalculatorBase()
    {
        return calculatorBase;
    }

    public void setCalculatorBase(CalculatorBase calculatorBase)
    {
        this.calculatorBase = calculatorBase;
        LOGGER.debug("CalculatorBase set to {}", calculatorBase);
    }

    public CalculatorBase getPreviousBase()
    {
        return previousBase;
    }

    public void setPreviousBase(CalculatorBase previousBase)
    {
        this.previousBase = previousBase;
        LOGGER.debug("PreviousBase set to {}", previousBase);
    }

    public CalculatorByte getCalculatorByte()
    {
        return calculatorByte;
    }

    public void setCalculatorByte(CalculatorByte calculatorByte)
    {
        this.calculatorByte = calculatorByte;
        LOGGER.debug("CalculatorByte set to {}", calculatorByte);
    }

    public DateOperation getDateOperation()
    {
        return dateOperation;
    }

    public void setDateOperation(DateOperation dateOperation)
    {
        this.dateOperation = dateOperation;
        LOGGER.debug("DateOperation set to {}", dateOperation);
    }

    public CalculatorConverterType getConverterType()
    {
        return converterType;
    }

    public void setConverterType(CalculatorConverterType converterType)
    {
        this.converterType = converterType;
        LOGGER.debug("ConverterType set to {}", converterType);
    }

    public ImageIcon getCalculatorIcon()
    {
        return calculatorIcon;
    }

    public void setCalculatorIcon(ImageIcon calculatorIcon)
    {
        this.calculatorIcon = calculatorIcon;
    }

    public ImageIcon getMacIcon()
    {
        return macIcon;
    }

    public void setMacIcon(ImageIcon macIcon)
    {
        this.macIcon = macIcon;
    }

    public ImageIcon getWindowsIcon()
    {
        return windowsIcon;
    }

    public void setWindowsIcon(ImageIcon windowsIcon)
    {
        this.windowsIcon = windowsIcon;
    }

    public ImageIcon getBlankIcon()
    {
        return blankIcon;
    }

    public void setBlankIcon(ImageIcon blankIcon)
    {
        this.blankIcon = blankIcon;
    }

    public JMenuBar getCalculatorMenuBar()
    {
        return menuBar;
    }

    public void setCalculatorMenuBar(JMenuBar menuBar)
    {
        this.menuBar = menuBar;
        setJMenuBar(menuBar);
        LOGGER.debug("Menubar set");
    }

    public boolean isObtainingFirstNumber()
    {
        return obtainingFirstNumber;
    }

    public void setObtainingFirstNumber(boolean firstNumber)
    {
        this.obtainingFirstNumber = firstNumber;
        LOGGER.debug("isFirstNumber set to {}", firstNumber);
    }

    public boolean isNegativeNumber()
    {
        return negativeNumber;
    }

    public void setNegativeNumber(boolean numberNegative)
    {
        this.negativeNumber = numberNegative;
        LOGGER.debug("isNumberNegative set to {}", numberNegative);
    }

    public boolean isPemdasActive()
    {
        return isPemdasActive;
    }

    public boolean isDecimalPressed()
    {
        return !buttonDecimal.isEnabled();
    }

    public JMenu getStyleMenu()
    {
        return styleMenu;
    }

    public void setStyleMenu(JMenu styleMenu)
    {
        this.styleMenu = styleMenu;
        LOGGER.debug("Style Menu set");
    }

    public JMenu getViewMenu()
    {
        return viewMenu;
    }

    public void setViewMenu(JMenu viewMenu)
    {
        this.viewMenu = viewMenu;
        LOGGER.debug("View Menu set");
    }

    public JMenu getEditMenu()
    {
        return editMenu;
    }

    public void setEditMenu(JMenu editMenu)
    {
        this.editMenu = editMenu;
        LOGGER.debug("Edit Menu set");
    }

    public JMenu getHelpMenu()
    {
        return helpMenu;
    }

    public void setHelpMenu(JMenu helpMenu)
    {
        this.helpMenu = helpMenu;
        LOGGER.debug("Help Menu set");
    }

    public BasicPanel getBasicPanel()
    {
        return basicPanel;
    }

    public JPanel getMemoryPanel()
    {
        return memoryPanel;
    }

    public void setMemoryPanel(JPanel memoryPanel)
    {
        this.memoryPanel = memoryPanel;
        LOGGER.debug("Memory Panel set");
    }

    public JPanel getButtonsPanel()
    {
        return buttonsPanel;
    }

    public void setButtonsPanel(JPanel buttonsPanel)
    {
        this.buttonsPanel = buttonsPanel;
        LOGGER.debug("Buttons Panel set");
    }

    public ProgrammerPanel getProgrammerPanel()
    {
        return programmerPanel;
    }

    public ScientificPanel getScientificPanel()
    {
        return scientificPanel;
    }

    public DatePanel getDatePanel()
    {
        return datePanel;
    }

    public ConverterPanel getConverterPanel()
    {
        return converterPanel;
    }

    public JPanel getCurrentPanel()
    {
        return currentPanel;
    }

    public void setCurrentPanel(JPanel currentPanel)
    {
        this.currentPanel = currentPanel;
        LOGGER.debug("CurrentPanel set to {}", currentPanel.getName());
    }

    public JPanel getHistoryPanel()
    {
        return historyPanel;
    }

    public void setHistoryPanel(JPanel historyPanel)
    {
        this.historyPanel = historyPanel;
        LOGGER.debug("History Panel set");
    }

    public String getCalculatorStyle()
    {
        return calculatorStyle;
    }

    public void setCalculatorStyle(String calculatorStyle)
    {
        this.calculatorStyle = calculatorStyle;
        LOGGER.debug("Calculator Style set to {}", calculatorStyle);
    }

    public JTextPane getHistoryTextPane()
    {
        return historyTextPane;
    }

    public void setHistoryTextPane(JTextPane historyTextPane)
    {
        this.historyTextPane = historyTextPane;
        LOGGER.debug("History Text Pane set");
    }

    public void setCalculatorBase(CalculatorBase calculatorBase, boolean updatePreviousBase)
    {
        if (updatePreviousBase) setPreviousBase(getCalculatorBase());
        setCalculatorBase(calculatorBase);
    }

    public void setSystemDetector(OSDetector systemDetector)
    {
        this.systemDetector = systemDetector;
        LOGGER.debug("System Detector set");
    }

    public void setIsPemdasActive(boolean isPemdasActive)
    {
        this.isPemdasActive = isPemdasActive;
    }
}