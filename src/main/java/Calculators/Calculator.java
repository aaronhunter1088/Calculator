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
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static Types.CalculatorByte.*;
import static Types.Texts.*;
import static Types.CalculatorView.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;

public class Calculator extends JFrame
{
    private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    public static final Font
            mainFont = new Font(SEGOE_UI.getValue(), Font.PLAIN, 12), // all panels
            mainFontBold = new Font(SEGOE_UI.getValue(), Font.BOLD, 12), // Date panel
            verdanaFontBold = new Font(VERDANA.getValue(), Font.BOLD, 20); // Converter, Date panels
    // Menubar
    private JMenuBar menuBar;
    // Menubar Items
    private JMenu lookMenu, viewMenu, editMenu, helpMenu;
    // Text Pane
    private JTextPane textPane;
    // Buttons used by multiple Panels
    private final JButton
            button0 = new JButton(ZERO.getValue()), button1 = new JButton(ONE.getValue()),
            button2 = new JButton(TWO.getValue()), button3 = new JButton(THREE.getValue()),
            button4 = new JButton(FOUR.getValue()), button5 = new JButton(FIVE.getValue()),
            button6 = new JButton(SIX.getValue()), button7 = new JButton(SEVEN.getValue()),
            button8 = new JButton(EIGHT.getValue()), button9 = new JButton(NINE.getValue()),
            buttonClear = new JButton(CLEAR.getValue()), buttonClearEntry = new JButton(CLEAR_ENTRY.getValue()),
            buttonDelete = new JButton(DELETE.getValue()), buttonDecimal = new JButton(DECIMAL.getValue()),
            buttonFraction = new JButton(FRACTION.getValue()), buttonPercent = new JButton(PERCENT.getValue()),
            buttonSquareRoot = new JButton(SQUARE_ROOT.getValue()), buttonMemoryClear = new JButton(MEMORY_CLEAR.getValue()),
            buttonMemoryRecall = new JButton(MEMORY_RECALL.getValue()), buttonMemoryStore = new JButton(MEMORY_STORE.getValue()),
            buttonMemoryAddition = new JButton(MEMORY_ADDITION.getValue()), buttonMemorySubtraction = new JButton(MEMORY_SUBTRACTION.getValue()),
            buttonHistory = new JButton(HISTORY_CLOSED.getValue()), buttonSquared = new JButton(SQUARED.getValue()),
            buttonAdd = new JButton(ADDITION.getValue()), buttonSubtract = new JButton(SUBTRACTION.getValue()),
            buttonMultiply = new JButton(MULTIPLICATION.getValue()), buttonDivide = new JButton(DIVISION.getValue()),
            buttonEquals = new JButton(EQUALS.getValue()), buttonNegate = new JButton(NEGATE.getValue()),
            // Used in programmer and converter... until an official button is determined
            buttonBlank1 = new JButton(BLANK.getValue()), buttonBlank2 = new JButton(BLANK.getValue());
    // Other Properties 
    // Key and Mouse Listeners for input recognition
    protected CalculatorKeyListener keyListener;
    protected CalculatorMouseListener mouseListener;
    // Values used to store inputs. Only one set of values, and memoryValues
    protected String[]
            values = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}, // firstNum or total, secondNum, copy, temporary storage
            memoryValues = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}; // stores memory values; rolls over after 10 entries
    // Position of values, memoryValues, and memoryRecall
    private int valuesPosition = 0, memoryPosition = 0, memoryRecallPosition = 0;
    // Current view, base, byte, date operation, converter type, and panel (view)
    private CalculatorView calculatorView;
    private CalculatorBase calculatorBase, previousBase;
    private CalculatorByte calculatorByte;
    private DateOperation dateOperation;
    private ConverterType converterType;
    private JPanel currentPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    private JLabel iconLabel, textLabel;
    // Flags
    private boolean
            isFirstNumber = true,
            isNumberNegative = false, // used to determine is current value is negative or not
            //isNegating = false,
            // main operators
            isAdding = false, isSubtracting = false, isMultiplying = false, isDividing = false,
            // look options
            isMetal = false, isSystem = false, isWindows = false, isMotif = false, isGtk = false, isApple = false;

    /* Constructors */
    /**
     * Starts the calculator with the BASIC CalculatorView
     *
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_BASIC, null, null, null); }

    /**
     * Starts the Calculator with a specific CalculatorView
     *
     * @param calculatorView the type of Calculator to create
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(calculatorView, BASE_DECIMAL, null, null); }

    /**
     * Starts the calculator with the PROGRAMMER CalculatorView with a specified CalculatorBase
     *
     * @param calculatorBase the base to set that Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorBase calculatorBase) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_PROGRAMMER, calculatorBase, null, null); }

    /**
     * This constructor is used to create a DATE Calculator with a specific DateOperation
     *
     * @param dateOperation  the option to open the DateCalculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_DATE, null, null, dateOperation); }

    /**
     * This constructor is used to create a Converter Calculator starting with a specific ConverterType
     *
     * @param converterType  the type of unit to start the Converter Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(ConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(VIEW_CONVERTER, null, converterType, null); }

    /**
     * MAIN CONSTRUCTOR USED
     *
     * @param calculatorView the type of Calculator to create
     * @param converterType  the type of Converter to use
     * @param dateOperation  the type of DateOperation to start with
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorView calculatorView, CalculatorBase calculatorBase, ConverterType converterType, DateOperation dateOperation) throws CalculatorError, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calculatorView.getValue());
        setCalculatorView(calculatorView);
        setCalculatorBase(calculatorBase);
        setCalculatorByte(determineByte());
        setConverterType(converterType);
        setDateOperation(dateOperation);
        setCurrentPanel(determinePanel());
        setupMenuBar();
        setupCalculatorImages();
        //if (converterType == null && dateOperation == null) setCurrentPanel(determinePanel(calculatorView));
        //else if (converterType != null) setCurrentPanel(determinePanel(calculatorView, null, converterType, null));
        //else setCurrentPanel(determinePanel(calculatorView, null, null, dateOperation));
        setupPanel();
        add(currentPanel);
        LOGGER.debug("Panel added to calculator");
        setMinimumSize(currentPanel.getSize());
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        keyListener = new CalculatorKeyListener(this);
        mouseListener = new CalculatorMouseListener(this);
        this.addKeyListener(keyListener);
        this.addMouseListener(mouseListener);
        LOGGER.debug("Finished constructing the calculator");
    }

    /* Start of methods here */
    /**
     * Sets the menu bar used across the entire Calculator
     */
    public void setupMenuBar()
    {
        LOGGER.debug("Configuring MenuBar...");
        setCalculatorMenuBar(new JMenuBar());
        setJMenuBar(menuBar);
        setupLookMenu(new JMenu(LOOK.getValue()));
        setupViewMenu(new JMenu(VIEW.getValue()));
        setupEditMenu(new JMenu(EDIT.getValue()));
        setupHelpMenu(new JMenu(HELP.getValue()));
        LOGGER.debug("MenuBar configured");
    }

    /**
     * The main operations to perform to set up
     * the Look Menu item
     * @param lookMenu the look menu to configure
     */
    private void setupLookMenu(JMenu lookMenu)
    {
        LOGGER.debug("Configuring Look menu...");
        JMenuItem metal = new JMenuItem(METAL.getValue());
        metal.setFont(mainFont);
        metal.setName(METAL.getValue());
        metal.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(Color.WHITE);
                    textPane.setBorder(new LineBorder(Color.BLACK));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setIsMetal(true);
                super.pack();
            }
            catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        JMenuItem system = new JMenuItem(SYSTEM.getValue());
        system.setFont(mainFont);
        system.setName(SYSTEM.getValue());
        system.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("javax.swing.plaf.system.SystemLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(Color.WHITE);
                    textPane.setBorder(new LineBorder(Color.BLACK));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setIsSystem(true);
                pack();
            }
            catch (ClassNotFoundException | InstantiationException |
                   IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        JMenuItem windows = new JMenuItem(WINDOWS.getValue());
        windows.setFont(mainFont);
        windows.setName(WINDOWS.getValue());
        windows.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(Color.WHITE);
                    textPane.setBorder(new LineBorder(Color.BLACK));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setIsWindows(true);
            }
            catch (ClassNotFoundException | InstantiationException |
                   IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        JMenuItem motif = new JMenuItem(MOTIF.getValue());
        motif.setFont(mainFont);
        motif.setName(MOTIF.getValue());
        motif.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(new Color(174,178,195));
                    textPane.setBorder(new LineBorder(Color.GRAY, 1, true));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                resetLook();
                setIsMotif(true);
                SwingUtilities.updateComponentTreeUI(this);
                pack();
            }
            catch (ClassNotFoundException | InstantiationException |
                   IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        JMenuItem gtk = new JMenuItem(GTK.getValue());
        gtk.setFont(mainFont);
        gtk.setName(GTK.getValue());
        gtk.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(Color.WHITE);
                    textPane.setBorder(new LineBorder(Color.BLACK));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setIsGtk(true);
                super.pack();
            }
            catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        JMenuItem apple = new JMenuItem(APPLE.getValue());
        apple.setFont(mainFont);
        apple.setName(APPLE.getValue());
        apple.addActionListener(action -> {
            try
            {
                UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
                if (null != textPane) // not used in all Calculators
                {
                    textPane.setBackground(Color.WHITE);
                    textPane.setBorder(new LineBorder(Color.BLACK));
                }
                if (currentPanel instanceof BasicPanel basicPanel)
                {
                    if (null != basicPanel.getHistoryTextPane())
                    {
                        basicPanel.getHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setIsApple(true);
                super.pack();
            }
            catch (ClassNotFoundException | InstantiationException |
                   IllegalAccessException | UnsupportedLookAndFeelException e)
            { logException(e); }
        });
        lookMenu.add(metal);
        lookMenu.add(motif);
        lookMenu.add(apple);
        if (!isMacOperatingSystem()) // add more options if using Windows
        {
            lookMenu.add(windows);
            lookMenu.add(system);
            lookMenu.add(gtk);
            lookMenu.remove(apple);
        }
        lookMenu.setFont(mainFont);
        lookMenu.setName(LOOK.getValue());
        setLookMenu(lookMenu);
        menuBar.add(lookMenu);
        LOGGER.debug("Look menu configured");
    }

    /**
     * The main operations to perform to set up
     * the View Menu item
     * @param viewMenu the view menu to configure
     */
    private void setupViewMenu(JMenu viewMenu)
    {
        LOGGER.debug("Configuring View menu...");
        JMenuItem basic = new JMenuItem(VIEW_BASIC.getValue());
        JMenuItem programmer = new JMenuItem(VIEW_PROGRAMMER.getValue());
        JMenuItem date = new JMenuItem(VIEW_DATE.getValue());
        JMenu converterMenu = new JMenu(VIEW_CONVERTER.getValue());
        basic.setFont(mainFont);
        basic.setName(VIEW_BASIC.getValue());
        basic.addActionListener(action -> switchPanels(action, VIEW_BASIC));
        programmer.setFont(mainFont);
        programmer.setName(VIEW_PROGRAMMER.getValue());
        programmer.addActionListener(action -> switchPanels(action, VIEW_PROGRAMMER));
        date.setFont(mainFont);
        date.setName(VIEW_DATE.getValue());
        date.addActionListener(action -> switchPanels(action, VIEW_DATE));
        JMenuItem angleConverter = new JMenuItem(ANGLE.getValue());
        angleConverter.setFont(mainFont);
        angleConverter.setName(ANGLE.getValue());
        angleConverter.addActionListener(action -> switchPanels(action, ANGLE));
        JMenuItem areaConverter = new JMenuItem(AREA.getValue()); // The converterMenu is a menu of more choices
        areaConverter.setFont(mainFont);
        areaConverter.setName(AREA.getValue());
        areaConverter.addActionListener(action -> switchPanels(action, AREA));
        converterMenu.setFont(mainFont);
        converterMenu.setName(VIEW_CONVERTER.getValue());
        converterMenu.add(angleConverter);
        converterMenu.add(areaConverter);
        viewMenu.add(basic);
        viewMenu.add(programmer);
        viewMenu.add(date);
        viewMenu.addSeparator();
        viewMenu.add(converterMenu);
        viewMenu.setFont(mainFont);
        viewMenu.setName(VIEW.getValue());
        setViewMenu(viewMenu);
        menuBar.add(viewMenu); // add viewMenu to menu bar
        LOGGER.debug("View menu configured");
    }

    /**
     * The main operations to perform to set up
     * the Edit Menu item
     * @param editMenu the edit menu to configure
     */
    private void setupEditMenu(JMenu editMenu)
    {
        LOGGER.debug("Configuring Edit menu...");
        JMenuItem copyItem = new JMenuItem(COPY.getValue());
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.setFont(mainFont);
        copyItem.setName(COPY.getValue());
        copyItem.addActionListener(this::performCopyAction);
        JMenuItem pasteItem = new JMenuItem(PASTE.getValue());
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setFont(mainFont);
        pasteItem.setName(PASTE.getValue());
        pasteItem.addActionListener(this::performPasteAction);
        JMenuItem clearHistoryItem = new JMenuItem(CLEAR_HISTORY.getValue());
        clearHistoryItem.setFont(mainFont);
        clearHistoryItem.setName(CLEAR_HISTORY.getValue());
        clearHistoryItem.addActionListener(this::performClearHistoryTextPaneAction);
        JMenuItem showMemoriesItem = new JMenuItem(SHOW_MEMORIES.getValue());
        showMemoriesItem.setFont(mainFont);
        showMemoriesItem.setName(SHOW_MEMORIES.getValue());
        showMemoriesItem.addActionListener(this::performShowMemoriesAction);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(clearHistoryItem);
        editMenu.add(showMemoriesItem);
        editMenu.setFont(mainFont);
        editMenu.setName(EDIT.getValue());
        setEditMenu(editMenu);
        menuBar.add(editMenu);
        LOGGER.debug("Edit menu configured");
    }

    /**
     * The main operations to perform to set up
     * the Help Menu item
     * @param helpMenu the help menu to configure
     */
    private void setupHelpMenu(JMenu helpMenu)
    {
        LOGGER.debug("Configuring Help menu...");
        JMenuItem viewHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();
        helpMenu.add(viewHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        helpMenu.setFont(mainFont);
        helpMenu.setName(HELP.getValue());
        setHelpMenu(helpMenu);
        menuBar.add(helpMenu);
        LOGGER.debug("Help menu configured");
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
        } else {
            LOGGER.error("Could not find an image using path: '{}'!", path);
            LOGGER.error("ImageIcon not created. Returning null");
        }
        return imageIcon;
    }

    /**
     * @return JPanel, the Panel to use. Panel will be setup afterwards
     */
    protected JPanel determinePanel()
    {
        LOGGER.debug("DeterminePanel, type:{}", calculatorView);
        return switch (calculatorView)
        {
            case VIEW_BASIC -> new BasicPanel();
            case VIEW_PROGRAMMER -> new ProgrammerPanel();
            case VIEW_SCIENTIFIC -> new ScientificPanel();
            case VIEW_DATE -> new DatePanel();
            case VIEW_CONVERTER -> new ConverterPanel();
        };
    }

    /**
     * Determines the appropriate byte to set
     * @return calculatorByte the set value
     */
    private CalculatorByte determineByte()
    {
        if (calculatorView == VIEW_PROGRAMMER) calculatorByte = BYTE_BYTE;
        else calculatorByte = null;
        return calculatorByte;
    }

    /**
     * The main method that calls the setup method for a specific panel
     */
    protected void setupPanel()
    {
        LOGGER.debug("Setting up panel, {}", currentPanel.getClass().getSimpleName());
        if (currentPanel instanceof BasicPanel panel)
        { panel.setupBasicPanel(this); }
        else if (currentPanel instanceof ProgrammerPanel panel)
        { panel.setupProgrammerPanel(this); }
        else if (currentPanel instanceof ScientificPanel panel)
        { LOGGER.info("IMPLEMENT SCIENTIFIC PANEL"); /*panel.setupScientificPanel(this, calculatorBase);*/ }
        else if (currentPanel instanceof DatePanel panel)
        { panel.setupDatePanel(this, dateOperation); }
        else if (currentPanel instanceof ConverterPanel panel)
        { panel.setupConverterPanel(this, converterType); }
        LOGGER.debug("Panel set up");
    }

    /**
     * This method creates the View Help menu option under Help
     * The help text is added by the currentPanel.
     */
    public JMenuItem createViewHelpJMenuItem()
    {
        LOGGER.debug("Configuring View Help...");
        JMenuItem viewHelpItem = new JMenuItem(VIEW_HELP.getValue());
        viewHelpItem.setName(VIEW_HELP.getValue());
        viewHelpItem.setFont(mainFont);
        LOGGER.debug("View Help configured");
        return viewHelpItem;
    }

    /**
     * This method creates the About Calculator menu option under Help
     */
    public JMenuItem createAboutCalculatorJMenuItem()
    {
        LOGGER.debug("Configuring About Calculator...");
        JMenuItem aboutCalculatorItem = new JMenuItem(ABOUT_CALCULATOR.getValue());
        aboutCalculatorItem.setName(ABOUT_CALCULATOR.getValue());
        aboutCalculatorItem.setFont(mainFont);
        aboutCalculatorItem.addActionListener(this::performAboutCalculatorAction);
        LOGGER.debug("About Calculator configured");
        return aboutCalculatorItem;
    }

    /**
     * The main method to set up the textPane
     */
    public void setupTextPane()
    {
        LOGGER.debug("Configuring textPane...");
        if (currentPanel instanceof BasicPanel)
        {
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            setTextPane(new JTextPane());
            textPane.setParagraphAttributes(attribs, true);
            textPane.setFont(mainFont);
            textPane.setPreferredSize(new Dimension(70, 30));
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            String[] initString = {
                    programmerPanel.displayByteAndBase()+ addNewLines(1),
                    values[valuesPosition]+ addNewLines(1)
            };
            String[] initStyles = { "regular", "regular"};
            String[] alignmentStyles = {"alignLeft", "alignRight"};
            // TODO: Remove local object declaration and use global variable textPane on line below
            JTextPane textPane = new JTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            // Styles
            Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            Style regular = doc.addStyle("regular", defaultStyle);
            StyleConstants.setFontFamily(defaultStyle, mainFont.getFamily());
            // Add alignment styles
            Style alignLeft = doc.addStyle("alignLeft", regular);
            StyleConstants.setAlignment(alignLeft, StyleConstants.ALIGN_LEFT);
            Style alignRight = doc.addStyle("alignRight", regular);
            StyleConstants.setAlignment(alignRight, StyleConstants.ALIGN_RIGHT);
            try {
                for (int i=0; i < initString.length; i++) {
                    String style = alignmentStyles[i % alignmentStyles.length];
                    LOGGER.debug("Style @ {}: {} for string: {}", i, style, initString[i]);
                    doc.insertString( doc.getLength(), initString[i], doc.getStyle(initStyles[i]) );
                    doc.setParagraphAttributes(doc.getLength() - initString[i].length(), initString[i].length(), doc.getStyle(style), false);
                }
            } catch(Exception e) {
                logException(e);
            }
            textPane.setPreferredSize(new Dimension(100, 120));
            textPane.setEditable(false);
            setTextPane(textPane);
        }
        if (isMotif)
        {
            textPane.setBackground(new Color(174,178,195));
            textPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        {
            textPane.setBackground(Color.WHITE);
            textPane.setBorder(new LineBorder(Color.BLACK));
        }
        LOGGER.debug("TextPane configured");
    }

    /**
     * The main method to set up the Basic History Pane
     */
    public void setupHistoryTextPane()
    {
        LOGGER.debug("Configuring BasicHistoryTextPane...");
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        if (currentPanel instanceof BasicPanel basicPanel)
        {
            basicPanel.setBasicHistoryTextPane(new JTextPane());
            basicPanel.getHistoryTextPane().setParagraphAttributes(attribs, true);
            basicPanel.getHistoryTextPane().setFont(mainFont);
            if (isMotif)
            {
                basicPanel.getHistoryTextPane().setBackground(new Color(174,178,195));
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
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            programmerPanel.setProgrammerHistoryTextPane(new JTextPane());
            programmerPanel.getHistoryTextPane().setParagraphAttributes(attribs, true);
            programmerPanel.getHistoryTextPane().setFont(mainFont);
            if (isMotif)
            {
                programmerPanel.getHistoryTextPane().setBackground(new Color(174,178,195));
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
        buttonMemoryClear.setName(MEMORY_CLEAR.getValue());
        buttonMemoryClear.addActionListener(this::performMemoryClearAction);
        buttonMemoryRecall.setName(MEMORY_RECALL.getValue());
        buttonMemoryRecall.addActionListener(this::performMemoryRecallAction);
        buttonMemoryAddition.setName(MEMORY_ADDITION.getValue());
        buttonMemoryAddition.addActionListener(this::performMemoryAdditionAction);
        buttonMemorySubtraction.setName(MEMORY_SUBTRACTION.getValue());
        buttonMemorySubtraction.addActionListener(this::performMemorySubtractionAction);
        buttonMemoryStore.setEnabled(true); // Enable memoryStore
        buttonMemoryStore.setName(MEMORY_STORE.getValue());
        buttonMemoryStore.addActionListener(this::performMemoryStoreAction);
        buttonHistory.setEnabled(true);
        buttonHistory.setName(HISTORY_CLOSED.getValue());
        //reset buttons to enabled if memories are saved
        if (!memoryValues[0].isEmpty())
        {
            buttonMemoryClear.setEnabled(true);
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryAddition.setEnabled(true);
            buttonMemorySubtraction.setEnabled(true);
        }
        LOGGER.warn("Memory buttons only configured for BASIC/PROGRAMMER Panel");
        LOGGER.debug("Memory buttons configured");
        if (currentPanel instanceof BasicPanel basicPanel)
        { buttonHistory.addActionListener(basicPanel::performHistoryAction); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { buttonHistory.addActionListener(programmerPanel::performHistoryAction); }
        LOGGER.debug("History button configured");
    }

    /**
     * The main method to set up the remaining
     * Basic button panels not in the memory panel
     */
    public void setupBasicPanelButtons()
    {
        LOGGER.debug("Configuring Basic Panel buttons...");
        List<JButton> allButtons =
                Stream.of(getAllBasicPanelButtons(), getAllNumberButtons())
                        .flatMap(Collection::stream) // Flatten into a stream of JButton objects
                        .toList();
        allButtons.forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35) );
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonPercent.setName(PERCENT.name());
        if (VIEW_BASIC.getValue().equals(currentPanel.getName()))
        { buttonPercent.addActionListener(actionEvent -> ((BasicPanel)currentPanel).performPercentButtonAction(actionEvent)); }
        LOGGER.debug("Percent button configured");
        buttonSquareRoot.setName(SQUARE_ROOT.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonSquareRoot.addActionListener(this::performSquareRootButtonAction); }
        LOGGER.debug("SquareRoot button configured");
        buttonSquared.setName(SQUARED.name());
        if (VIEW_BASIC.getValue().equals(currentPanel.getName()))
        { buttonSquared.addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSquaredButtonAction(actionEvent)); }
        LOGGER.debug("Squared button configured");
        buttonFraction.setName(FRACTION.name());
        if (VIEW_BASIC.getValue().equals(currentPanel.getName()))
        { buttonFraction.addActionListener(actionEvent -> ((BasicPanel)currentPanel).performFractionButtonAction(actionEvent)); }
        LOGGER.debug("Fraction button configured");
        buttonClearEntry.setName(CLEAR_ENTRY.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonClearEntry.addActionListener(this::performClearEntryButtonAction); }
        LOGGER.debug("ClearEntry button configured");
        buttonClear.setName(CLEAR.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonClear.addActionListener(this::performClearButtonAction); }
        LOGGER.debug("Clear button configured");
        buttonDelete.setName(DELETE.name());
        if (currentPanel instanceof BasicPanel)
        { buttonDelete.addActionListener(this::performDeleteButtonAction); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { buttonDelete.addActionListener(programmerPanel::performButtonDeleteButtonAction); }
        LOGGER.debug("Delete button configured");
        buttonDivide.setName(DIVISION.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonDivide.addActionListener(this::performDivideButtonAction); }
        LOGGER.debug("Divide button configured");
        buttonMultiply.setName(MULTIPLICATION.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonMultiply.addActionListener(this::performMultiplicationAction); }
        LOGGER.debug("Multiply button configured");
        buttonSubtract.setName(SUBTRACTION.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonSubtract.addActionListener(this::performSubtractionButtonAction); }
        LOGGER.debug("Subtract button configured");
        buttonAdd.setName(ADDITION.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonAdd.addActionListener(this::performAdditionButtonAction); }
        LOGGER.debug("Addition button configured");
        buttonNegate.setName(NEGATE.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonNegate.addActionListener(this::performNegateButtonAction); }
        LOGGER.debug("Negate button configured");
        buttonDecimal.setName(DECIMAL.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonDecimal.addActionListener(this::performDecimalButtonAction); }
        LOGGER.debug("Decimal button configured");
        buttonEquals.setName(EQUALS.name());
        if (Arrays.asList(VIEW_BASIC.getValue(), VIEW_PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { buttonEquals.addActionListener(this::performEqualsButtonAction); }
        LOGGER.debug("Equals button configured");
        LOGGER.debug("Common buttons configured");
    }

    /**
     * The main method to set up the buttons
     * used on the Converter panel
     */
    public void setupConverterButtons()
    {
        LOGGER.debug("Configuring Converter Panel buttons...");
        Arrays.asList(buttonBlank1, buttonBlank2, buttonClearEntry, buttonDelete, buttonDecimal).forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonBlank1.setName(BLANK.name());
        LOGGER.debug("Blank button 1 configured");
        buttonBlank2.setName(BLANK.name());
        LOGGER.debug("Blank button 2 configured");
        buttonClearEntry.setName(CLEAR_ENTRY.name());
        if (VIEW_CONVERTER.getValue().equals(currentPanel.getName()))
        { buttonClearEntry.addActionListener(ConverterPanel::performClearEntryButtonActions); }
        LOGGER.debug("ClearEntry button configured");
        buttonDelete.setName(DELETE.name());
        if (VIEW_CONVERTER.getValue().equals(currentPanel.getName()))
        { buttonDelete.addActionListener(ConverterPanel::performDeleteButtonActions); }
        LOGGER.debug("Delete button configured");
        buttonDecimal.setName(DECIMAL.name());
        if (VIEW_CONVERTER.getValue().equals(currentPanel.getName()))
        { buttonDecimal.addActionListener(ConverterPanel::performDecimalButtonActions); }
        LOGGER.debug("Decimal button configured");
        LOGGER.debug("Converter Panel buttons configured");
    }

    /**
     * The main method to set up all number buttons, 0-9
     */
    public void setupNumberButtons()
    {
        LOGGER.debug("Configuring Number buttons...");
        AtomicInteger i = new AtomicInteger(0);
        getAllNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(true);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            if (currentPanel instanceof BasicPanel)
            { button.addActionListener(this::performNumberButtonAction); }
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            { button.addActionListener(programmerPanel::performNumberButtonActions); }
            else if (VIEW_CONVERTER.getValue().equals(currentPanel.getName()))
            { button.addActionListener(ConverterPanel::performNumberButtonActions); }
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
        LOGGER.debug("Configuring Blank Button1...");
        buttonBlank1.setFont(mainFont);
        buttonBlank1.setPreferredSize(new Dimension(35, 35));
        buttonBlank1.setBorder(new LineBorder(Color.BLACK));
        buttonBlank1.setEnabled(true);
        buttonBlank1.setName(BLANK.name());
        LOGGER.debug("Blank Button1 configured");
    }

    /**
     * The main method to set up the Blank2 button
     */
    public void setupButtonBlank2()
    {
        LOGGER.debug("Configuring Blank Button2...");
        buttonBlank2.setFont(mainFont);
        buttonBlank2.setPreferredSize(new Dimension(35, 35));
        buttonBlank2.setBorder(new LineBorder(Color.BLACK));
        buttonBlank2.setEnabled(true);
        buttonBlank2.setName(BLANK.name());
        LOGGER.debug("Blank Button2 configured");
    }
    
    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public void performMemoryStoreAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("No number to add to memory");
        }
        else if (textPaneContainsBadText())
        { confirm("Not saving " + getTextPaneValue() + " in Memory"); }
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
            confirm(memoryValues[memoryPosition - 1] + " is stored in memory at position: " + (memoryPosition - 1));
        }
    }

    /**
     * The actions to perform when MemoryRecall is clicked
     * @param actionEvent the click action
     */
    public void performMemoryRecallAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (memoryRecallPosition == 10 || memoryValues[memoryRecallPosition].isBlank())
        { setMemoryRecallPosition(getLowestMemoryPosition()); }
        appendTextToPane(memoryValues[memoryRecallPosition]);
        values[valuesPosition] = getTextPaneValue();
        writeHistoryWithMessage(buttonChoice, false, " Recalled: " + memoryValues[memoryRecallPosition] + " at memory location " + (memoryRecallPosition+1));
        memoryRecallPosition += 1;
        confirm("Recalling number in memory: " + memoryValues[(memoryRecallPosition-1)] + " at position: " + (memoryRecallPosition-1));
    }
    
    /**
     * The actions to perform when MemoryClear is clicked
     * @param actionEvent the click action
     */
    public void performMemoryClearAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
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
            memoryValues[memoryPosition] = BLANK.getValue();
            memoryRecallPosition += 1;
            confirm("Cleared memory at " + memoryPosition);
            // MemorySuite could now be empty
            if (isMemoryValuesEmpty())
            {
                memoryPosition = 0;
                memoryRecallPosition = 0;
                buttonMemoryClear.setEnabled(false);
                buttonMemoryRecall.setEnabled(false);
                buttonMemoryAddition.setEnabled(false);
                buttonMemorySubtraction.setEnabled(false);
                appendTextToPane(ZERO.getValue());
                confirm("MemorySuite is empty");
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
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText())
        { confirm("Cannot add bad text to memories values"); }
        else if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("No number to add to memory");
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
            writeHistoryWithMessage(buttonChoice, false, " Added " + getTextPaneValue() + " to memories at " + (memoryPosition) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + memoryValues[(memoryPosition-1)]);
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * The actions to perform when MemorySubtraction is clicked
     * @param actionEvent the click action
     */
    public void performMemorySubtractionAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText())
        { confirm("Cannot subtract bad text to memories values"); }
        else if (getTextPaneValue().isBlank())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("No number to subtract from memory");
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
            writeHistoryWithMessage(buttonChoice, false, " Subtracted " + getTextPaneValue() + " to memories at " + (memoryPosition) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + memoryValues[(memoryPosition-1)]);
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * The beginning actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public void performNumberButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (!isFirstNumber) // second number
        {
            LOGGER.debug("obtaining second number...");
            // TODO: Check logic here. Not sure if this is correct.
            if (!isNumberNegative) clearTextInTextPane();
            isFirstNumber = true;
            if (isNumberNegative && !getTextPaneValue().contains(SUBTRACTION.getValue()))
                isNumberNegative = false;
            if (!isDotPressed())
            {
                LOGGER.debug("Decimal is disabled. Enabling");
                buttonDecimal.setEnabled(true);
            }
        }
        if (performInitialChecks())
        {
            LOGGER.warn("Invalid entry in textPane. Clearing...");
            appendTextToPane(BLANK.getValue());
            values[valuesPosition] = BLANK.getValue();
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        if (values[0].isBlank())
        {
            LOGGER.info("Highest size not met. Values[0] is blank");
        }
        else if (checkValueLength())
        {
            LOGGER.info("Highest size of value has been met");
            confirm("Max length of 7 digit number met");
            return;
        }
        if (isNumberNegative && values[valuesPosition].isBlank())
        {
            values[valuesPosition] = SUBTRACTION.getValue() + buttonChoice;
            appendTextToPane(addCommas(values[valuesPosition]));
            writeHistory(buttonChoice, false);
            isNumberNegative = true;
        }
        else if (isNumberNegative && !values[1].isBlank())
        {
            values[valuesPosition] = SUBTRACTION.getValue() + buttonChoice;
            appendTextToPane(addCommas(values[valuesPosition]));
            writeHistory(buttonChoice, false);
            isNumberNegative = true;
        }
        else
        {
            if (currentPanel instanceof BasicPanel
                || (currentPanel instanceof ProgrammerPanel && BASE_DECIMAL == calculatorBase))
                performNumberButtonInnerAction(buttonChoice);
            else return;
        }
        confirm("Pressed " + buttonChoice);
    }

    /**
     * Inner logic of number button actions.
     * @param buttonChoice the clicked button
     */
    public void performNumberButtonInnerAction(String buttonChoice)
    {
        values[valuesPosition] = values[valuesPosition] + buttonChoice;
        appendTextToPane(addCommas(values[valuesPosition]));
        writeHistory(buttonChoice, false);
    }

    /**
     * The actions to perform when the Divide button is clicked
     * @param actionEvent the click action
     */
    public void performDivideButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText() || isMinimumValue() || isMaximumValue())
        { confirm("Cannot perform " + DIVISION); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + DIVISION + " operation");
        }
        else
        {
            // No basic operator pushed, textPane has a value, and values is set
            if (!isAdding && !isSubtracting  && !isMultiplying && !isDividing
                    && !textPane.getText().isBlank() && !values[valuesPosition].isBlank())
            {
                appendTextToPane(values[valuesPosition] + SPACE.getValue() + buttonChoice);
                writeHistory(buttonChoice, true);
                isDividing = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(DIVISION.getValue()); // 5 + 3 ÷
                if (isMinimumValue() || isMaximumValue())
                {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isAdding = false;
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(DIVISION.getValue()); // 5 - 3 ÷
                if (isMinimumValue() || isMaximumValue())
                {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isSubtracting = false;
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(DIVISION.getValue()); // 5 ✕ 3 ÷
                if (isMinimumValue() || isMaximumValue())
                {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isMultiplying = false;
            }
            else if (isDividing && !values[1].isEmpty() )
            {
                divide(DIVISION.getValue()); // 5 ÷ 3 ÷
                if (isMinimumValue() || isMaximumValue())
                {
                    isDividing = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else if (getTextPaneWithoutAnyOperator().equals(INFINITY.getValue()))
                {
                    isDividing = false;
                    valuesPosition = 0;
                }
                else
                {
                    isDividing = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank())
            {
                LOGGER.error("The user pushed divide but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",","");
                appendTextToPane(values[0] + SPACE.getValue() + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isDividing = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (determineIfAnyBasicOperatorWasPushed())
            { LOGGER.info("already chose an operator. choose another number."); }
            buttonDecimal.setEnabled(true);
            if (isMinimumValue())
            { confirm("Pressed: " + buttonChoice + " Minimum number met"); }
            else if (isMaximumValue())
            { confirm("Pressed: " + buttonChoice + " Maximum number met"); }
            else { confirm("Pressed: " + buttonChoice); }
        }
    }
    /**
     * The inner logic for dividing
     */
    public void divide()
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result;
        if (!ZERO.getValue().equals(values[1]))
        {
            result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info("{} / {} = {}", values[0], values[1], result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(EQUALS.getValue(), DIVISION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                appendTextToPane(values[valuesPosition]);
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(EQUALS.getValue(), DIVISION.getValue(), result, false);
                values[0] = addCommas(formatNumber(String.valueOf(result)));
            }
            confirm("Finished dividing");
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
            appendTextToPane(INFINITY.getValue());
            values[0] = BLANK.getValue();
            values[1] = BLANK.getValue();
            isFirstNumber = true;
            confirm("Attempted to divide by 0. Values[0] = 0");
        }
    }
    private void divide(String continuedOperation)
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result;
        if (!ZERO.getValue().equals(values[1]))
        {
            result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            LOGGER.info("{} / {} = {}", values[0], values[1], result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(continuedOperation, DIVISION.getValue(), result, true);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                appendTextToPane(values[valuesPosition]);
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, DIVISION.getValue(), result, true);
                values[0] = String.valueOf(result);
                buttonDecimal.setEnabled(false);
            }
            LOGGER.info("Finished " + DIVISION);
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
            appendTextToPane(INFINITY.getValue());
            values[0] = BLANK.getValue();
            values[1] = BLANK.getValue();
            isFirstNumber = true;
            confirm("Attempted to divide by 0. Values[0] = 0");
        }
    }

    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public void performMultiplicationAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText() || isMinimumValue() || isMaximumValue())
        { confirm("Cannot perform " + MULTIPLICATION); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + MULTIPLICATION + " operation");
        }
        else
        {
            // No basic operator pushed, textPane has a value, and values is set
            if (!isAdding && !isSubtracting  && !isMultiplying && !isDividing
                && !textPane.getText().isBlank() && !values[valuesPosition].isBlank())
            {
                appendTextToPane(values[valuesPosition] + SPACE.getValue() + buttonChoice);
                writeHistory(buttonChoice, true);
                isMultiplying = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(MULTIPLICATION.getValue()); // 5 + 3 ✕...
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isAdding = false;
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(MULTIPLICATION.getValue()); // 5 - 3 ✕...
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isSubtracting = false;
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(MULTIPLICATION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
            }
            else if (isDividing && !values[1].isEmpty())
            {
                divide(MULTIPLICATION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isMultiplying = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isMultiplying = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isDividing = false;
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank())
            {
                LOGGER.error("The user pushed multiple but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",","");
                appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isMultiplying = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (determineIfAnyBasicOperatorWasPushed())
            { LOGGER.info("already chose an operator. choose another number."); }
            buttonDecimal.setEnabled(true);
            if (isMinimumValue())
            { confirm("Pressed: " + buttonChoice + " Minimum number met"); }
            else if (isMaximumValue())
            { confirm("Pressed: " + buttonChoice + " Maximum number met"); }
            else { confirm("Pressed: " + buttonChoice); }
        }
    }
    /**
     * The inner logic for multiplying
     */
    public void multiply()
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]);
        LOGGER.info("{} * {} = {}", values[0], values[1], result);
        try
        {
            int wholeResult = Integer.parseInt(clearZeroesAndDecimalAtEnd(formatNumber(String.valueOf(result))));
            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
            LOGGER.info("We have a whole number");
            writeContinuedHistory(EQUALS.getValue(), MULTIPLICATION.getValue(), result, false);
            values[0] = addCommas(String.valueOf(wholeResult));
            buttonDecimal.setEnabled(true);
        }
        catch (NumberFormatException nfe)
        {
            logException(nfe);
            LOGGER.info("We have a decimal");
            writeContinuedHistory(EQUALS.getValue(), MULTIPLICATION.getValue(), result, false);
            values[0] = formatNumber(String.valueOf(result));
        }
    }
    private void multiply(String continuedOperation)
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]);
        LOGGER.info("{} * {} = {}", values[0], values[1], result);
        try
        {
            int wholeResult = Integer.parseInt(clearZeroesAndDecimalAtEnd(formatNumber(String.valueOf(result))));
            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
            LOGGER.info("We have a whole number");
            writeContinuedHistory(continuedOperation, MULTIPLICATION.getValue(), result, true);
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(wholeResult));
            buttonDecimal.setEnabled(true);
        }
        catch (NumberFormatException nfe)
        {
            logException(nfe);
            LOGGER.info("We have a decimal");
            writeContinuedHistory(continuedOperation, MULTIPLICATION.getValue(), result, true);
            values[0] = formatNumber(String.valueOf(result));
            buttonDecimal.setEnabled(false);
        }
        LOGGER.info("Finished " + MULTIPLICATION);
    }

    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public void performSubtractionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText() || isMinimumValue())
        { confirm("Cannot perform " + SUBTRACTION); }
//        else if (getTextPaneWithoutNewLineCharacters().isEmpty() && values[valuesPosition].isEmpty())
//        {
//            if (currentPanel instanceof BasicPanel basicPanel)
//            {
//                textPane.setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            }
//            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
//            {
//                programmerPanel.appendToPane(ENTER_A_NUMBER.getValue());
//            }
//            confirm("Cannot perform " + SUBTRACTION + " operation");
//        }
        else
        {
            // No basic operator pushed, textPane has a value, and values is set
            if (!isAdding && !isSubtracting  && !isMultiplying && !isDividing
                    && !textPane.getText().isBlank() && !values[valuesPosition].isBlank())
            {
                appendTextToPane(getTextPaneValue() + SPACE.getValue() + buttonChoice);
                writeHistory(buttonChoice, true);
                isSubtracting = true;
                isFirstNumber = false;
                isNumberNegative = false;
                valuesPosition += 1;
            }
            // No basic operator pushed, textPane is empty, and values is not set
            else if (!isAdding && !isSubtracting  && !isMultiplying && !isDividing
                    && getTextPaneValue().isBlank() )
            {
                appendTextToPane(buttonChoice);
                writeHistory(buttonChoice, true);
                isNumberNegative = true;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(SUBTRACTION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isAdding = false;
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(SUBTRACTION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(values[0] + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(SUBTRACTION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isMultiplying = false;
            }
            else if (isDividing && !values[1].isEmpty())
            {
                divide(SUBTRACTION.getValue());
                if (isMinimumValue() || isMaximumValue())
                {
                    isSubtracting = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isSubtracting = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isDividing = false;
            }
            else if ( determineIfAnyBasicOperatorWasPushed() && !isNumberNegative)
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                writeHistory(buttonChoice, true);
                appendTextToPane(buttonChoice);
                isNumberNegative = true;
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank() && !isNumberNegative)
            {
                LOGGER.error("The user pushed subtract but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",","");
                appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isSubtracting = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            buttonDecimal.setEnabled(true);
            if (isMinimumValue())
            { confirm("Pressed: " + buttonChoice + " Minimum number met"); }
            else if (isMaximumValue())
            { confirm("Pressed: " + buttonChoice + " Maximum number met"); }
            else { confirm("Pressed: " + buttonChoice); }
        }
    }
    /**
     * The inner logic for subtracting
     */
    public void subtract()
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]);
        if (String.valueOf(result).contains(SUBTRACTION.getValue()))
        {
            isNumberNegative = true;
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number, negating");
                LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                values[0] = convertToNegative(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
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
                if (values[1].contains(SUBTRACTION.getValue())) {
                    LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                } else {
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                }
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                values[0] = addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                if (values[1].contains(SUBTRACTION.getValue())) {
                    LOGGER.debug("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                } else {
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                }
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                values[0] = addCommas(formatNumber(String.valueOf(result)));
                buttonDecimal.setEnabled(false);
            }
        }
    }
    private void subtract(String continuedOperation)
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]);
        if (result % 1 == 0)
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                if (isNumberNegative)
                {
                    LOGGER.debug("We have a whole number, negating");
                    LOGGER.info("{} + {} = {}", values[0], values[1], result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    isNumberNegative = false;
                    values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                    buttonDecimal.setEnabled(true);
                }
                else
                {
                    LOGGER.debug("We have a whole number");
                    LOGGER.debug("{} - {} = {}", values[0], values[1], result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
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
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                if (isNumberNegative)
                {
                    LOGGER.debug("We have a decimal, negating");
                    LOGGER.info("{} + {} = {}", values[0], convertToPositive(values[1]), result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    isNumberNegative = false;
                    values[0] = formatNumber(String.valueOf(result));
                    buttonDecimal.setEnabled(false);
                }
                else
                {
                    LOGGER.debug("We have a decimal");
                    LOGGER.info("{} - {} = {}", values[0], convertToPositive(values[1]), result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    values[0] = formatNumber(String.valueOf(result));
                    buttonDecimal.setEnabled(false);
                }
            }
        }
        LOGGER.info("Finished " + SUBTRACTION);
    }

    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public void performAdditionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText() || isMaximumValue())
        { confirm("Cannot perform " + ADDITION); }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + ADDITION + " operation");
        }
        else
        {
            // No operator pushed until now, textPane has a value, and values is set
            if (!isAdding && !isSubtracting  && !isMultiplying &&!isDividing
                    && !textPane.getText().isBlank() && !values[valuesPosition].isBlank())
            {
                isAdding = true;
                appendTextToPane(getTextPaneValue() + SPACE.getValue() + buttonChoice);
                writeHistory(buttonChoice, true);
                isFirstNumber = false;
                isNumberNegative = false;
                valuesPosition += 1;
            }
            else if (isAdding && !values[1].isEmpty())
            {
                addition(ADDITION.getValue());  // 5 + 3 + ...
                if (isMaximumValue()) // we can add to the minimum number, not to the maximum
                {
                    isAdding = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isAdding = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                //isAdding = false;
            }
            else if (isSubtracting && !values[1].isEmpty())
            {
                subtract(ADDITION.getValue()); // 5 - 3 + ...
                if (isMinimumValue() || isMaximumValue())
                {
                    isAdding = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isAdding = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isSubtracting = false;
            }
            else if (isMultiplying && !values[1].isEmpty())
            {
                multiply(ADDITION.getValue()); // 5 ✕ 3 + ...
                if (isMinimumValue() || isMaximumValue())
                {
                    isAdding = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isAdding = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isMultiplying = false;
            }
            else if (isDividing && !values[1].isEmpty())
            {
                divide(ADDITION.getValue()); // 5 ÷ 3 + ...
                if (isMinimumValue() || isMaximumValue())
                {
                    isAdding = false;
                    appendTextToPane(addCommas(values[0]));
                }
                else
                {
                    isAdding = true;
                    appendTextToPane(addCommas(values[0]) + SPACE.getValue() + buttonChoice);
                }
                resetCalculatorOperations(false);
                isDividing = false;
            }
            else if (!getTextPaneValue().isBlank() && values[0].isBlank())
            {
                LOGGER.error("The user pushed plus but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                values[0] = getTextPaneValue().replace(",","");
                appendTextToPane(values[0] + SPACE.getValue() + buttonChoice);
                LOGGER.debug("values[0]: {}", values[0]);
                writeHistory(buttonChoice, true);
                isAdding = true;
                isFirstNumber = false;
                valuesPosition += 1;
            }
            else if (isAdding || isSubtracting || isMultiplying || isDividing)
            { LOGGER.error("Already chose an operator. Choose another number."); }
            buttonDecimal.setEnabled(true);
            if (isMinimumValue())
            { confirm("Pressed: " + buttonChoice + " Minimum number met"); }
            else if (isMaximumValue())
            { confirm("Pressed: " + buttonChoice + " Maximum number met"); }
            else { confirm("Pressed: " + buttonChoice); }
        }
    }
    /**
     * The inner logic for addition.
     * This method performs the addition between values[0] and values[1],
     * clears and trailing zeroes if the result is a whole number (15.0000)
     * resets dotPressed to false, enables buttonDot and stores the result
     * back in values[0]
     */
    public void addition()
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info("{} + {} = {}", values[0], values[1], result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            writeContinuedHistory(EQUALS.getValue(), ADDITION.getValue(), result, false);
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            buttonDecimal.setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            writeContinuedHistory(EQUALS.getValue(), ADDITION.getValue(), result, false);
            buttonDecimal.setEnabled(false);
            values[0] = addCommas(formatNumber(String.valueOf(result)));
        }
    }
    private void addition(String continuedOperation)
    {
        LOGGER.info("value[0]: '{}'", values[0]);
        LOGGER.info("value[1]: '{}'", values[1]);
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info("{} + {} = {}", values[0], values[1], result);
        if (result % 1 == 0)
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.debug("We have a whole number");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, true);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
        }
        else
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                buttonDecimal.setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, true);
                values[0] = formatNumber(String.valueOf(result));
                buttonDecimal.setEnabled(false);
            }
        }
        LOGGER.info("Finished " + ADDITION);
    }

    /**
     * The actions to perform when the SquareRoot button is clicked
     * @param actionEvent the click action
     */
    public void performSquareRootButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.debug("textPane: {}", getTextPaneValue());
        if (textPaneContainsBadText())
        { confirm("Cannot perform " + SQUARE_ROOT + " operation"); }
        else if (getTextPaneValue().isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + SQUARE_ROOT + " when textPane blank");
        }
        else if (isNegativeNumber(values[valuesPosition]))
        {
            appendTextToPane(ERR.getValue());
            confirm("Cannot perform " + SQUARE_ROOT + " on negative number");
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
            confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public void performClearEntryButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.debug("valuesPosition: {}", valuesPosition);
        if (getTextPaneValue().isEmpty())
        { confirm(CLEAR_ENTRY + " called... nothing to clear"); }
        else if (valuesPosition == 0 || values[1].isEmpty())
        {
            values[0] = BLANK.getValue();
            resetBasicOperators(false);
            if (currentPanel instanceof ProgrammerPanel programmerPanel)
            { programmerPanel.resetProgrammerOperators(false); }
            valuesPosition = 0;
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
            clearTextInTextPane();
            writeHistoryWithMessage(buttonChoice, false, SPACE.getValue() + buttonChoice + " performed");
            confirm("Pressed: " + buttonChoice);
        }
        else
        {
            String operator = getActiveBasicPanelOperator();
            values[1] = BLANK.getValue();
            isFirstNumber = false;
            valuesPosition = 1;
            isNumberNegative = false;
            buttonDecimal.setEnabled(true);
            appendTextToPane(addCommas(values[0]) + SPACE.getValue() + operator);
            writeHistoryWithMessage(buttonChoice, false, SPACE.getValue() + buttonChoice + " performed");
            confirm("Pressed: " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Clear button is clicked
     * @param actionEvent the action performed
     */
    public void performClearButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        for (int i=0; i<4; i++)
        {
            if (i == 0) { values[i] = ZERO.getValue(); }
            else if (i==3) { values[i] = PUSHED_CLEAR.getValue(); }
            else { values[i] = BLANK.getValue(); }
        }
        for(int i=0; i < 10; i++)
        { memoryValues[i] = BLANK.getValue(); }
        appendTextToPane(ZERO.getValue());
        values[3] = BLANK.getValue();
        resetBasicOperators(false);
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
        confirm("Pressed: " + buttonChoice);
    }

    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performDeleteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText())
        {
            appendTextToPane(BLANK.getValue());
            confirm("Contains bad text. Pressed " + buttonChoice);
        }
        else if (getTextPaneValue().isEmpty() && values[0].isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("No need to perform " + DELETE + " operation");
        }
        else
        {
            if (valuesPosition == 1 && values[1].isEmpty())
            { valuesPosition = 0; } // assume they could have pressed an operator then wish to delete
            if (values[0].isEmpty())
            { values[0] = getTextPaneValue(); }
            LOGGER.debug("values[{}]: {}", valuesPosition, values[valuesPosition]);
            LOGGER.debug("textPane: {}", getTextPaneValue());
            if (!isAdding && !isSubtracting && !isMultiplying && !isDividing
                && !getTextPaneValue().isEmpty())
            {
                values[valuesPosition] = values[valuesPosition].substring(0,values[valuesPosition].length()-1);
                appendTextToPane(addCommas(values[valuesPosition]));
            }
            else if (determineIfAnyBasicOperatorWasPushed())
            {
                LOGGER.debug("An operator has been pushed");
                if (valuesPosition == 0)
                {
                    if (isAdding) isAdding = false;
                    else if (isSubtracting) isSubtracting = false;
                    else if (isMultiplying) isMultiplying = false;
                    else /*if (isDividing())*/ isDividing = false;
                    values[valuesPosition] = getTextPaneWithoutAnyOperator();
                    appendTextToPane(addCommas(getTextPaneWithoutAnyOperator()));
                }
                else
                {
                    values[valuesPosition] = values[valuesPosition].substring(0,values[valuesPosition].length()-1);
                    appendTextToPane(addCommas(values[valuesPosition]));
                }
            }
            buttonDecimal.setEnabled(!isDecimal(values[valuesPosition]));
            isNumberNegative = values[valuesPosition].contains(SUBTRACTION.getValue());
            writeHistory(buttonChoice, false);
            confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public void performNegateButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText() || isMaximumValue())
        { confirm("Cannot perform " + NEGATE); }
        else if (getTextPaneValue().isEmpty())
        {
            appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm("No value to negate");
        }
        else if (ZERO.getValue().equals(getTextPaneValue()))
        { confirm("Cannot negate zero"); }
        else
        {
            if (isNumberNegative)
            {
                isNumberNegative = false;
                String textToConvert = getTextToConvert();
                values[valuesPosition] = convertToPositive(textToConvert);
                writeHistory(buttonChoice, false);
            }
            else
            {
                isNumberNegative = true;
                String textToConvert = getTextToConvert();
                values[valuesPosition] = convertToNegative(textToConvert);
                writeHistory(buttonChoice, false);
            }
            appendTextToPane(addCommas(values[valuesPosition]));
            confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public void performDecimalButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (textPaneContainsBadText())
        { confirm("Cannot perform " + DECIMAL + " operation"); }
        else
        {
            LOGGER.info("Basic dot operations");
            performDecimal(buttonChoice);
            writeHistory(buttonChoice, false);
            confirm("Pressed " + buttonChoice);
        }
    }
    /**
     * The inner logic of performing Dot actions
     * @param buttonChoice the button choice
     */
    private void performDecimal(String buttonChoice)
    {
        if (values[valuesPosition].isBlank() && !isNumberNegative) // !isNegating
        {
            values[valuesPosition] = ZERO.getValue() + DECIMAL.getValue();
            appendTextToPane(values[valuesPosition]);
        }
        else if (values[valuesPosition].isBlank() && isNumberNegative) // isNegating
        {
            values[valuesPosition] = SUBTRACTION.getValue() + ZERO.getValue() + DECIMAL.getValue();
            appendTextToPane(values[valuesPosition]);
        }
        else
        {
            values[valuesPosition] = values[valuesPosition] + buttonChoice;
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
        LOGGER.info("Performing {} button actions", buttonChoice);
        if (isAdding && values[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", ADDITION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isSubtracting && values[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", SUBTRACTION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isMultiplying && values[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", MULTIPLICATION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isDividing && values[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", DIVISION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        String operator = getActiveBasicPanelOperator();
        determineAndPerformBasicCalculatorOperation();
        if (!operator.isEmpty() && !textPaneContainsBadText()) {
            switch (calculatorBase)
            {
                case BASE_BINARY -> appendTextToPane(convertValueToBinary());
                case BASE_OCTAL,
                     BASE_DECIMAL,
                     BASE_HEXADECIMAL -> {
                    appendTextToPane(values[0]);
                }
            }
        }
        values[0] = BLANK.getValue();
        values[1] = BLANK.getValue();
        isNumberNegative = false;
        isFirstNumber = false;
        valuesPosition = 0;
        confirm("Pushed " + buttonChoice);
    }
    /**
     * This method determines which basic operation to perform,
     * performs that operation, and resets the appropriate boolean
     */
    public void determineAndPerformBasicCalculatorOperation()
    {
        if (isMaximumValue() && (isAdding || isMultiplying) )
        {
            appendTextToPane(ERR.getValue());
            values[0] = BLANK.getValue();
            values[1] = BLANK.getValue();
            isNumberNegative = false;
            resetBasicOperators(false);
            confirm("Maximum value met");
        }
        else if (isMinimumValue() && (isSubtracting || isDividing))
        {
            appendTextToPane(ZERO.getValue());
            values[0] = BLANK.getValue();
            values[1] = BLANK.getValue();
            isNumberNegative = false;
            resetBasicOperators(false);
            confirm("Minimum value met");
        }
        else
        {
            if (isAdding)
            {
                addition();
                setIsAdding(resetCalculatorOperations(isAdding));
            }
            else if (isSubtracting)
            {
                subtract();
                setIsSubtracting(resetCalculatorOperations(isSubtracting));
            }
            else if (isMultiplying)
            {
                multiply();
                setIsMultiplying(resetCalculatorOperations(isMultiplying));
            }
            else if (isDividing)
            {
                divide();
                setIsDividing(resetCalculatorOperations(isDividing));
            }
        }
    }

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
        confirm("Pressed " + COPY.getValue());
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
        if (values[2].isEmpty()) confirm("Values[2] is empty. Nothing to paste");
        else
        {
            LOGGER.debug("values[2]: {}", values[2]);
            appendTextToPane(values[2]);
            values[valuesPosition] = getTextPaneValue();
            confirm("Pressed " + PASTE.getValue());
        }
    }

    /**
     * Clears the history for current panel
     */
    public void performClearHistoryTextPaneAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        if (currentPanel instanceof BasicPanel basicPanel)
        { basicPanel.getHistoryTextPane().setText(BLANK.getValue()); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { programmerPanel.getHistoryTextPane().setText(BLANK.getValue()); }
        else LOGGER.warn("Add other history panels");
    }

    /**
     * Displays all the memory values in the history panel
     */
    public void performShowMemoriesAction(ActionEvent actionEvent)
    {
        LOGGER.debug("Action for {} started", actionEvent.getActionCommand());
        if (currentPanel instanceof BasicPanel basicPanel)
        {
            if (!isMemoryValuesEmpty())
            {
                StringBuilder memoriesString = new StringBuilder();
                memoriesString.append("Memories: ");
                for(int i=0; i<memoryPosition; i++)
                {
                    memoriesString.append("[").append(memoryValues[i]).append("]");
                    if ((i+1) < memoryValues.length && !memoryValues[i+1].equals(BLANK.getValue()))
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
        LOGGER.debug("Show Memories complete");
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
        iconLabel = new JLabel();
        iconPanel.add(iconLabel);
        ImageIcon specificLogo = isMacOperatingSystem() ? macIcon : windowsIcon;
        textLabel = new JLabel(getAboutCalculatorString(), specificLogo, SwingConstants.LEFT);
        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        JPanel mainPanel = new JPanel();
        mainPanel.add(iconLabel);
        mainPanel.add(textLabel);
        JOptionPane.showMessageDialog(this, mainPanel, ABOUT_CALCULATOR.getValue(), JOptionPane.PLAIN_MESSAGE);
        confirm("Pressed " + ABOUT_CALCULATOR.getValue());
    }

    /* Calculator helper methods */
    /**
     * This method returns the String operator that was activated
     * Results could be: '+', '-', '*', '/' or '' if no
     * operator was recorded as being activated
     * @return String the basic operation that was pushed
     */
    public String getActiveBasicPanelOperator()
    {
        String results = BLANK.getValue();
        if (isAdding) { results = ADDITION.getValue(); }
        else if (isSubtracting) { results = SUBTRACTION.getValue(); }
        else if (isMultiplying) { results = MULTIPLICATION.getValue(); }
        else if (isDividing) { results = DIVISION.getValue(); }
        LOGGER.info("operator: {}", (results.isEmpty() ? "no basic operator pushed" : results));
        return results;
    }

    /**
     * Inner logic for negate button actions
     * @return String the value to convert
     */
    private String getTextToConvert()
    {
        String textToConvert;
        if (values[valuesPosition].isBlank()) {
            textToConvert = getTextPaneValue();
        } else {
            textToConvert = values[valuesPosition];
        }
        return textToConvert;
    }

    /**
     * Records the buttonChoice to the appropriate history panel
     * @param buttonChoice String the button choice
     */
    public void writeHistory(String buttonChoice, boolean addButtonChoiceToEnd)
    { writeHistoryWithMessage(buttonChoice, addButtonChoiceToEnd, null); }
    /**
     * Records the buttonChoice to the appropriate history panel
     * with a specific message
     * @param buttonChoice String the buttonChoice
     * @param addButtonChoiceToEnd boolean to add the buttonChoice to the end
     * @param message String the message to display
     */
    public void writeHistoryWithMessage(String buttonChoice, boolean addButtonChoiceToEnd, String message)
    {
        if (null == message) {
            if (currentPanel instanceof BasicPanel basicPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    basicPanel.getHistoryTextPane().setText(
                            basicPanel.getHistoryTextPane().getText() +
                                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                    + " Result: " + addCommas(values[valuesPosition]) + SPACE.getValue() + buttonChoice
                    );
                }
                else {
                    basicPanel.getHistoryTextPane().setText(
                            basicPanel.getHistoryTextPane().getText() +
                                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                    + " Result: " + addCommas(values[valuesPosition])
                    );
                }
            }
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            {
                switch (calculatorBase)
                {
                    case BASE_BINARY -> {
                        if (addButtonChoiceToEnd)
                        {
                            programmerPanel.getHistoryTextPane().setText(
                            programmerPanel.getHistoryTextPane().getText() +
                            addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                            + " Result: " + getTextPaneValueForProgrammerPanel() + SPACE.getValue() + buttonChoice
                            );
                        }
                        else {
                            programmerPanel.getHistoryTextPane().setText(
                            programmerPanel.getHistoryTextPane().getText() +
                            addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                            + " Result: " + getTextPaneValueForProgrammerPanel()
                            );
                        }
                    }
                    case BASE_OCTAL -> {}
                    case BASE_DECIMAL -> {
                        if (addButtonChoiceToEnd)
                        {
                            programmerPanel.getHistoryTextPane().setText(
                                    programmerPanel.getHistoryTextPane().getText() +
                                            addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                            + " Result: " + addCommas(values[valuesPosition]) + SPACE.getValue() + buttonChoice
                            );
                        }
                        else {
                            programmerPanel.getHistoryTextPane().setText(
                                    programmerPanel.getHistoryTextPane().getText() +
                                            addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                            + " Result: " + addCommas(values[valuesPosition])
                            );
                        }
                    }
                    case BASE_HEXADECIMAL -> {}
                }
            }
        }
        else {
            if (currentPanel instanceof BasicPanel basicPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    basicPanel.getHistoryTextPane().setText(
                        basicPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message + SPACE.getValue() + buttonChoice
                    );
                }
                else {
                    basicPanel.getHistoryTextPane().setText(
                        basicPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message
                    );
                }
            }
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message + SPACE.getValue() + buttonChoice
                    );
                }
                else {
                    programmerPanel.getHistoryTextPane().setText(
                        programmerPanel.getHistoryTextPane().getText() +
                        addNewLines(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message
                    );
                }
            }
        }
    }
    /**
     * Records the buttonChoice specifically if it was a continued operator
     * @param continuedOperation String whether to display the operation or equals
     * @param operation String the operation performed (add, subtract, etc)
     * @param result double the result from the operation
     * @param addContinuedOperationToEnd boolean whether to append the operation to the end
     */
    public void writeContinuedHistory(String continuedOperation, String operation, double result, boolean addContinuedOperationToEnd)
    {
        if (currentPanel instanceof BasicPanel basicPanel)
        {
            if (addContinuedOperationToEnd)
            {
                basicPanel.getHistoryTextPane().setText(
                    basicPanel.getHistoryTextPane().getText() +
                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(values[0]) + SPACE.getValue() + operation + SPACE.getValue() + addCommas(values[1]) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result))) + SPACE.getValue() + continuedOperation
                );
            } else {
                basicPanel.getHistoryTextPane().setText(
                    basicPanel.getHistoryTextPane().getText() +
                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(values[0]) + SPACE.getValue() + operation + SPACE.getValue() + addCommas(values[1]) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)))
                );
            }
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            if (addContinuedOperationToEnd)
            {
                programmerPanel.getHistoryTextPane().setText(
                    programmerPanel.getHistoryTextPane().getText() +
                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(values[0]) + SPACE.getValue() + operation + SPACE.getValue() + addCommas(values[1]) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result))) + SPACE.getValue() + continuedOperation
                );
            } else {
                programmerPanel.getHistoryTextPane().setText(
                    programmerPanel.getHistoryTextPane().getText() +
                    addNewLines(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(values[0]) + SPACE.getValue() + operation + SPACE.getValue() + addCommas(values[1]) + SPACE.getValue() + EQUALS.getValue() + SPACE.getValue() + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)))
                );
            }
        }
    }

    /**
     * This method resets default values
     * Primarily used for testing purposes
     */
    public void resetValues()
    {
        values[0] = BLANK.getValue();
        values[1] = BLANK.getValue();
        values[2] = BLANK.getValue();
        values[3] = BLANK.getValue();
        LOGGER.debug("All values reset");
        isNumberNegative = false;
        LOGGER.debug("isNumberNegative set to false");
        resetBasicOperators(false);
        LOGGER.debug("All main basic operators set to false");
    }

    /**
     * Clears all actions from the number buttons
     */
    public void clearNumberButtonActions()
    {
        getAllNumberButtons()
            .forEach(button -> Arrays.stream(button.getActionListeners())
                .forEach(al -> {
                    LOGGER.debug("Removing action listener from button: {}", button.getName());
                    button.removeActionListener(al);
        }));
        LOGGER.debug("Number buttons cleared of action listeners");
    }

    /**
     * This method returns true or false depending
     * on if an operator was pushed or not. The
     * operators checked are addition, subtraction,
     * multiplication, and division.
     *
     * @return true if any operator was pushed, false otherwise
     */
    public boolean determineIfAnyBasicOperatorWasPushed()
    { return isAdding || isSubtracting || isMultiplying || isDividing; }

    //TODO: Rethink name. It does too much
    /**
     * Sets values[1] to be blank, updates valuesPosition accordingly,
     * updates the Dot button and boolean, resets firstNumber and returns
     * true if no operator was pushed or false otherwise
     *
     * @param operatorBool the operator to press
     * @return boolean the operatorBool opposite value
     */
    public boolean resetCalculatorOperations(boolean operatorBool)
    {
        LOGGER.debug("Resetting calculator operations, operatorBool:{}", operatorBool);
        if (operatorBool)
        {
            values[1] = BLANK.getValue();
            valuesPosition = 1;
            buttonDecimal.setEnabled(true); // !isDecimal(values[0])
            isFirstNumber = false;
            return false;
        }
        else
        {
            values[1] = BLANK.getValue();
            valuesPosition = 0;
            buttonDecimal.setEnabled(!isDecimal(values[0]));
            isFirstNumber = true;
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
        LOGGER.debug("memoryValues is empty? {}", result ? YES.getValue() : NO.getValue());
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
     * Returns all the basicPanel main operator buttons
     * @return Collection of all basicPanel main operators
     */
    public List<JButton> getAllBasicPanelOperatorButtons()
    { return Arrays.asList(buttonAdd, buttonSubtract, buttonMultiply, buttonDivide); }

    /**
     * Returns all the number buttons
     * @return Collection of all number buttons
     */
    public List<JButton> getAllNumberButtons()
    { return Arrays.asList(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9); }

    /**
     * Returns all the memory buttons
     * @return List of buttons in the memory panel
     */
    public List<JButton> getAllMemoryPanelButtons()
    { return Arrays.asList(buttonMemoryStore, buttonMemoryClear, buttonMemoryRecall, buttonMemoryAddition, buttonMemorySubtraction, buttonHistory); }

    /**
     * Returns the "other" basic calculator buttons. This includes
     * Percent, SquareRoot, Squared, Fraction, ClearEntry, Clear,
     * Delete, Divide, Multiply, Subtract, Addition, Negate, Dot,
     * and Equals.
     * It does not include the number buttons.
     *
     * @return Collection of buttons
     */
    public List<JButton> getAllBasicPanelButtons()
    {
        return Arrays.asList(buttonPercent, buttonSquareRoot, buttonSquared, buttonFraction,
                buttonClearEntry, buttonClear, buttonDelete, buttonDivide, buttonMultiply,
                buttonSubtract, buttonAdd, buttonNegate, buttonDecimal, buttonEquals);
    }

    /**
     * Clears all actions from the buttons
     * other than the numbers buttons
     */
    public void clearAllOtherBasicCalculatorButtons()
    {
        getAllBasicPanelButtons()
            .forEach(button -> Arrays.stream(button.getActionListeners())
                .forEach(al -> {   // .toList().forEach(al ...
                    LOGGER.debug("Removing action listener from button: " + button.getName());
                    button.removeActionListener(al);
                }));
        LOGGER.debug("AllBasicPanelButtons cleared of action listeners");
    }

    /**
     * Tests whether a number has the "." symbol in it or not
     *
     * @param number the number to test
     * @return boolean if the given number contains a decimal
     */
    public boolean isDecimal(String number)
    {
        LOGGER.debug("isDecimal({}): {}", number.replace(addNewLines(1), BLANK.getValue()), number.contains(DECIMAL.getValue()));
        return number.contains(DECIMAL.getValue());
    }

    /**
     * When we hit a number button this method is called
     * to ensure valid entries are allowed and any previous
     * errors or unexpected conditions are cleared
     */
    public boolean performInitialChecks()
    {
        LOGGER.debug("Performing initial checks...");
        boolean issueFound = false;
        if (textPaneContainsBadText())
        {
            LOGGER.debug("TextPane contains bad text");
            issueFound = true;
        }
        else if (ZERO.getValue().equals(getTextPaneWithoutAnyOperator()) && VIEW_BASIC.equals(calculatorView))
        {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            appendTextToPane(BLANK.getValue());
            values[valuesPosition] = BLANK.getValue();
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        else if (ZERO.getValue().equals(values[valuesPosition]) && VIEW_PROGRAMMER.equals(calculatorView))
        {
            LOGGER.debug("textPane contains 0. Setting to blank.");
            ((ProgrammerPanel)currentPanel).appendTextToProgrammerPane(BLANK.getValue());
            values[valuesPosition] = BLANK.getValue();
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        else if (values[0].isBlank() && !values[1].isBlank())
        {
            LOGGER.debug("values[0] is blank, values[1] is not");
            values[0] = values[1];
            values[1] = BLANK.getValue();
            valuesPosition = 0;
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
    public boolean checkValueLength()
    {
        LOGGER.debug("Checking if max size (7) is met...");
        LOGGER.debug("values[{}].length: {}", valuesPosition, values[valuesPosition].length());
        return getNumberOnLeftSideOfDecimal(values[valuesPosition]).length() >= 7 ||
               getNumberOnRightSideOfDecimal(values[valuesPosition]).length() >= 7 ;
    }

    /**
     * Tests whether the textPane contains a String which shows a previous error.
     * Takes into account the current panel
     * @return boolean if textPane contains more than just a number
     */
    public boolean textPaneContainsBadText()
    {
        var val = getTextPaneWithoutAnyOperator();
        boolean result = CANNOT_DIVIDE_BY_ZERO.getValue().equals(val) ||
               NOT_A_NUMBER.getValue().equals(val) ||
               NUMBER_TOO_BIG.getValue().equals(val) ||
               ENTER_A_NUMBER.getValue().equals(val) ||
               ONLY_POSITIVES.getValue().equals(val) ||
               ERR.getValue().equals(val) || // ERR.getValue().contains(val)
               INFINITY.getValue().equals(val);
        if (result) LOGGER.debug("textPane contains bad text. text is {}", val);
        else LOGGER.debug("textPane is clean. text is '{}'", val);
        return result;
    }

    /**
     * Adds commas to the number if appropriate
     * @param valueToAdjust the passed in value
     * @return String the value with commas
     */
    public String addCommas(String valueToAdjust)
    {
        if (valueToAdjust.isBlank()) return valueToAdjust;
        var temp = valueToAdjust;
        LOGGER.debug("Stripping any operators");
        valueToAdjust = getValueWithoutAnyOperator(valueToAdjust);
        LOGGER.debug("Adding commas to '{}'", valueToAdjust);
        String adjusted;
        String toTheLeft;
        String toTheRight = "";
        if (isDecimal(valueToAdjust)) // 1.25, Length of 4
        {
            LOGGER.debug("temp: " + temp);
            toTheLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            toTheRight = getNumberOnRightSideOfDecimal(valueToAdjust);
            if (toTheLeft.length() <= 3)
            {
                valueToAdjust = temp;
                LOGGER.debug("valueFromTemp: " + valueToAdjust);
                buttonDecimal.setEnabled(!isDecimal(temp));
                return valueToAdjust;
            }
            else
            {
                valueToAdjust = toTheLeft;
                buttonDecimal.setEnabled(!isDecimal(temp));
            }
        }
        valueToAdjust = valueToAdjust.replace(UNDERSCORE.getValue(), BLANK.getValue())
                .replace(COMMA.getValue(), BLANK.getValue())
                .replace(DECIMAL.getValue(), BLANK.getValue())
                .replace(SUBTRACTION.getValue(), BLANK.getValue());
        LOGGER.debug("adjusted1: {}", valueToAdjust);
        if (valueToAdjust.length() >= 4)
        {
            LOGGER.debug("ValueToAdjust length: {}", valueToAdjust.length());
            StringBuffer reversed = new StringBuffer().append(valueToAdjust).reverse();
            LOGGER.debug("reversed: " + reversed);
            if (reversed.length() <= 6)
            {
                LOGGER.debug("Length is : {}", reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(getThousandsDelimiter()).append(reversed.substring(3,reversed.length()));
                adjusted = reversed.reverse().toString();
            }
            else
            {
                LOGGER.debug("Length is : {}", reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(getThousandsDelimiter()).append(reversed.substring(3,6)).append(getThousandsDelimiter()).append(reversed.substring(6));
                adjusted = reversed.reverse().toString();
            }
        }
        else
        {
            adjusted = valueToAdjust;
            LOGGER.debug("adjusted2: {}", adjusted);
            if (isDecimal(temp)) {
                buttonDecimal.setEnabled(!isDecimal(temp));
                adjusted += toTheRight;
                LOGGER.debug("adjusted2: {}", adjusted);
            }
        }
        if (!isDotPressed() && isDecimal(temp))
        {
            adjusted += DECIMAL.getValue() + toTheRight;
            buttonDecimal.setEnabled(false);
        }
        // if number was originally negative, add back negative symbol
        // if the textPane is only the negative symbol, don't add back
        if ( (isNumberNegative && !SUBTRACTION.getValue().equals(getTextPaneValueForProgrammerPanel()) )
            || temp.contains(SUBTRACTION.getValue())) {
            LOGGER.debug("adding '-' to beginning of number");
            adjusted = SUBTRACTION.getValue() + adjusted;
        }
        if (valuesPosition == 0)
        {
            String operator = getActiveBasicPanelOperator();
            if (!BLANK.getValue().equals(operator))
            { adjusted += SPACE.getValue() + operator; }
            else
            {
                if (VIEW_PROGRAMMER.equals(calculatorView))
                {
                    operator = ((ProgrammerPanel)currentPanel).getActiveProgrammerPanelOperator();
                    if (!BLANK.getValue().equals(operator))
                    { adjusted += SPACE.getValue() + operator; }
                }
            }
        }
        LOGGER.debug("adjustedFinal: {}", adjusted);
        return adjusted;
    }

    /**
     * Returns the thousands delimiter
     * @return the delimiter to use for thousands
     */
    public String getThousandsDelimiter()
    {
        // TODO: Setup menu option to allow user to choose this value
        return COMMA.getValue();
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
    public boolean isMacOperatingSystem()
    {
        LOGGER.debug("OS Name: {}", System.getProperty("os.name"));
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * The main actions to perform when switch panels
     * @param actionEvent the click action
     */
    public void switchPanels(ActionEvent actionEvent, CalculatorType calculatorView)
    {
        LOGGER.info("Switching panels...");
        LOGGER.debug("Action: {}", actionEvent.getActionCommand());
        String oldPanelName = currentPanel.getClass().getSimpleName(); //.replace("Panel", BLANK.getValue());
        String selectedPanel = calculatorView.getName(); //actionEvent.getActionCommand();
        LOGGER.debug("from '{}' to '{}'", oldPanelName, selectedPanel);
        if (oldPanelName.equals(selectedPanel))
        { confirm("Not changing to " + selectedPanel + " when already showing " + oldPanelName); }
        else if (converterType != null && selectedPanel.equals(converterType.getValue()))
        { confirm("Not changing panels when the converterType is the same"); }
        else
        {
            String currentValueInTextPane = getTextPaneValue();
            if (calculatorView.equals(VIEW_BASIC))
            {
                BasicPanel basicPanel = new BasicPanel();
                switchPanelsInner(basicPanel);
                if (!values[0].isEmpty() || !currentValueInTextPane.isEmpty()) {
                    if (!values[0].isEmpty()) appendTextToPane(values[0]);
                    else {
                        // see ProgrammerPanel.displayByteAndBase()
                        if (!(calculatorByte.getValue() + SPACE.getValue() + SPACE.getValue() + getCalculatorBase().getValue()).equals(currentValueInTextPane)) {
                            basicPanel.appendTextToBasicPane(currentValueInTextPane);
                        }
                    }
                    if (isDecimal(values[0])) {
                        buttonDecimal.setEnabled(false);
                    }
                    if (determineIfAnyBasicOperatorWasPushed()) {
                        basicPanel.appendTextToBasicPane(textPane.getText() + SPACE.getValue() + getActiveBasicPanelOperator());
                    }
                }
                setCalculatorView(VIEW_BASIC);
            }
            else if (calculatorView.equals(VIEW_PROGRAMMER)) {
                ProgrammerPanel programmerPanel = new ProgrammerPanel();
                switchPanelsInner(programmerPanel);
                if (!values[0].isEmpty() || !currentValueInTextPane.isEmpty()) {
                    if (!values[0].isEmpty()) {
                        programmerPanel.appendTextToProgrammerPane(values[0]);
                    } else {
                        // see ProgrammerPanel.displayByteAndBase()
                        if (!(calculatorByte.getValue() + SPACE.getValue() + SPACE.getValue() + calculatorBase.getValue()).equals(currentValueInTextPane)) {
                            programmerPanel.appendTextToProgrammerPane(currentValueInTextPane);
                        }
                    }
                    if (isDecimal(values[0])) {
                        buttonDecimal.setEnabled(false);
                    }
                    if (determineIfAnyBasicOperatorWasPushed()) {
                        programmerPanel.appendTextToProgrammerPane(values[0] + SPACE.getValue() + getActiveBasicPanelOperator());
                    }
                }
                setCalculatorView(VIEW_PROGRAMMER);
            }
            else if (calculatorView.equals(VIEW_SCIENTIFIC)) {
                LOGGER.warn("Setup");
                setCalculatorView(VIEW_SCIENTIFIC);
            }
            else if (calculatorView.equals(VIEW_DATE)) {
                // TODO: move the line below. this feels wrong to check at this point. like, once a calc is started, if not in date calc mode first, then just default that value to DIFFERENCE
                dateOperation = dateOperation == null ? DIFFERENCE_BETWEEN_DATES : dateOperation;
                switchPanelsInner(new DatePanel());
                setCalculatorView(VIEW_DATE);
            } else if (calculatorView.equals(ANGLE)) {
                setConverterType(ANGLE);
                switchPanelsInner(new ConverterPanel());
                setCalculatorView(VIEW_CONVERTER);
            } else if (calculatorView.equals(AREA)) {
                setConverterType(AREA);
                switchPanelsInner(new ConverterPanel());
                setCalculatorView(VIEW_CONVERTER);
            }
            confirm("Switched from " + oldPanelName + " to " + currentPanel.getClass().getSimpleName());
        }
    }

    /**
     * The inner logic to perform when switching panels
     * @param newPanel the new panel to switch to
     */
    private void switchPanelsInner(JPanel newPanel)
    {
        LOGGER.debug("SwitchPanelsInner: {}", newPanel.getName());
        setTitle(newPanel.getName());
        updateJPanel(newPanel);
        setSize(currentPanel.getSize());
        setMinimumSize(currentPanel.getSize());
        pack();
    }

    /**
     * Adds the appropriate number of zeroes to the
     * beginning portion of a binary number
     */
    public String adjustBinaryNumber(String base2Number)
    {
        int base2Length = base2Number.length();
        int topLength = determineRequiredLength(base2Length);
        int addZeroes = topLength - base2Length;
        base2Number = ZERO.getValue().repeat(addZeroes) + base2Number;
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
    public int determineRequiredLength(int base2Length)
    {
        var topLength = 0;
        if ( calculatorByte == BYTE_BYTE ) topLength = 8;
        else if ( calculatorByte == BYTE_WORD ) topLength = 16;
        else if ( calculatorByte == BYTE_DWORD ) topLength = 32;
        else if ( calculatorByte == BYTE_QWORD ) topLength = 64;
        if (topLength < base2Length) {
            LOGGER.debug("base2Length: {}", base2Length);
            if (base2Length <= 8) {
                topLength = 8;
                setCalculatorByte(BYTE_BYTE);
            } else if (base2Length <= 16) {
                topLength = 16;
                setCalculatorByte(BYTE_WORD);
            } else if (base2Length <= 32) {
                topLength = 32;
                setCalculatorByte(BYTE_DWORD);
            } else { //if (base2Length <= 64) {
                topLength = 64;
                setCalculatorByte(BYTE_QWORD);
            }
        }
        return topLength;
    }

    /**
     * Determines whether values[0], values[1], or
     * the value in the text pane should be utilized.
     * Defaults to values[valuesPosition] trimmed.
     */
    public String getAppropriateValue()
    {
        String result = values[valuesPosition].replace(COMMA.getValue(), BLANK.getValue()).replace(SPACE.getValue(), BLANK.getValue());
        if (result.isEmpty()) {
            if (valuesPosition != 0) {
                result = values[valuesPosition-1].replace(COMMA.getValue(), BLANK.getValue()).replace(SPACE.getValue(), BLANK.getValue());
            }
            if (result.isEmpty()) {
                result = getTextPaneValue();
                LOGGER.debug("values[0] and values[1] are empty. result is {}", result);
            }
        }
        return result;
    }

    /**
     * Converts the current value to its binary representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToBinary()
    {
        String valueToConvert = getAppropriateValue();
        if (valueToConvert.isEmpty()) return BLANK.getValue();
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_BINARY.getValue());
        String base2Number = Integer.toBinaryString(Integer.parseInt(valueToConvert));
        base2Number = adjustBinaryNumber(base2Number);
        LOGGER.debug("convert from({}) to({}) = {}", BASE_DECIMAL.getValue(), BASE_BINARY.getValue(), base2Number);
        LOGGER.info("The number {} in base 10 is {} in base 2.", valueToConvert, base2Number);
        return base2Number;
    }

    /**
     * Converts the current value to its octal representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToOctal()
    {
        String valueToConvert = getAppropriateValue();
        if (valueToConvert.isEmpty()) return BLANK.getValue();
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
        String valueToConvert = getAppropriateValue();
        if (valueToConvert.isEmpty()) return BLANK.getValue();
        LOGGER.debug("Converting {} to {}", valueToConvert, BASE_DECIMAL.getValue());
        String base10Number = Integer.toString(Integer.parseInt(valueToConvert, getPreviousRadix()), 10);
        LOGGER.debug("convert from({}) to({}) = {}", previousBase.getValue(), BASE_DECIMAL.getValue(), base10Number);
        LOGGER.info("The number {} in base 10 is {} in base 10", valueToConvert, base10Number);
        return base10Number;
    }

    /**
     * Converts the current value to its hexadecimal representation
     * @return the binary representation of value[valuePosition]
     */
    public String convertValueToHexadecimal()
    {
        String valueToConvert = getAppropriateValue();
        if (valueToConvert.isEmpty()) return BLANK.getValue();
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
        valueToConvert = valueToConvert.replace(COMMA.getValue(), BLANK.getValue()).replace(SPACE.getValue(), BLANK.getValue());
        if (valueToConvert.isEmpty()) return BLANK.getValue();
        LOGGER.debug("converting {} from {} to {}", valueToConvert, fromBase.getValue(), toBase.getValue());
        String convertedNumber = Integer.toString(Integer.parseInt(valueToConvert, getPreviousRadix(fromBase)), getPreviousRadix(toBase));
        if (BASE_BINARY == toBase) {
            convertedNumber = adjustBinaryNumber(convertedNumber);
        }
        LOGGER.debug("converted: {}", convertedNumber);
        return convertedNumber;
    }

    public int getPreviousRadix()
    { return getPreviousRadix(previousBase); }

    public int getPreviousRadix(CalculatorBase base)
    {
        return switch (base)
        {
            case BASE_BINARY -> 2;
            case BASE_OCTAL -> 8;
            case BASE_DECIMAL -> 10;
            case BASE_HEXADECIMAL -> 16;
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
        if (currentNumber.contains(ERR.getValue())) return currentNumber;
        int index;
        index = currentNumber.indexOf(DECIMAL.getValue());
        LOGGER.debug("decimalIndex: {}", index);
        if (index == -1) return currentNumber;
        else
        {
            String toTheRight = getNumberOnRightSideOfDecimal(currentNumber);
            String expected = ZERO.getValue().repeat(toTheRight.length());
            boolean allZeroes = expected.equals(toTheRight);
            if (allZeroes) currentNumber = currentNumber.substring(0,index);
        }
        LOGGER.debug("ClearZeroesAndDot Result: {}", addCommas(currentNumber));
        return currentNumber;
    }

    /**
     * Returns the text in the textPane without
     * any new line characters or operator text.
     * Takes into account the current panel.
     * @return the plain textPane text
     */
    public String getTextPaneWithoutAnyOperator()
    {
        return getTextPaneValue()
               .replace(ADDITION.getValue(), BLANK.getValue()) // target, replacement
               .replace(SUBTRACTION.getValue(), BLANK.getValue())
               .replace(MULTIPLICATION.getValue(), BLANK.getValue())
               .replace(DIVISION.getValue(), BLANK.getValue())
               .replace(MODULUS.getValue(), BLANK.getValue())
               .replace(LEFT_PARENTHESIS.getValue(), BLANK.getValue())
               .replace(RIGHT_PARENTHESIS.getValue(), BLANK.getValue())
               .replace(ROL.getValue(), BLANK.getValue())
               .replace(ROR.getValue(), BLANK.getValue())
               .replace(OR.getValue(), BLANK.getValue())
               .replace(XOR.getValue(), BLANK.getValue())
               .replace(AND.getValue(), BLANK.getValue())
               .strip();
    }

    public String getValueWithoutAnyOperator(String valueToAdjust)
    {
        return valueToAdjust
                .replace(ADDITION.getValue(), BLANK.getValue()) // target, replacement
                .replace(SUBTRACTION.getValue(), BLANK.getValue())
                .replace(MULTIPLICATION.getValue(), BLANK.getValue())
                .replace(DIVISION.getValue(), BLANK.getValue())
                .replace(MODULUS.getValue(), BLANK.getValue())
                .replace(LEFT_PARENTHESIS.getValue(), BLANK.getValue())
                .replace(RIGHT_PARENTHESIS.getValue(), BLANK.getValue())
                .replace(ROL.getValue(), BLANK.getValue())
                .replace(ROR.getValue(), BLANK.getValue())
                .replace(OR.getValue(), BLANK.getValue())
                .replace(XOR.getValue(), BLANK.getValue())
                .replace(AND.getValue(), BLANK.getValue())
                .strip();
    }

    /**
     * Returns the text in the textPane without
     * any new line characters or operator text.
     * Takes into account the current panel.
     * @return the plain textPane text
     */
    public String returnWithoutAnyOperator(String text)
    {
        return text
                .replace(ADDITION.getValue(), BLANK.getValue()) // target, replacement
                .replace(SUBTRACTION.getValue(), BLANK.getValue())
                .replace(MULTIPLICATION.getValue(), BLANK.getValue())
                .replace(DIVISION.getValue(), BLANK.getValue())
                .replace(MODULUS.getValue(), BLANK.getValue())
                .replace(LEFT_PARENTHESIS.getValue(), BLANK.getValue())
                .replace(RIGHT_PARENTHESIS.getValue(), BLANK.getValue())
                .replace(ROL.getValue(), BLANK.getValue())
                .replace(ROR.getValue(), BLANK.getValue())
                .replace(OR.getValue(), BLANK.getValue())
                .replace(XOR.getValue(), BLANK.getValue())
                .replace(AND.getValue(), BLANK.getValue())
                .strip();
    }

    /**
     * Returns the value in the textPane without
     * any new line characters or any other text.
     * Takes into account the current panel
     * @return the value without new lines or whitespace
     */
    public String getTextPaneValue()
    {
        if (currentPanel instanceof BasicPanel)
        { return textPane.getText().replaceAll(NEWLINE.getValue(), BLANK.getValue()).strip(); }
        else if (currentPanel instanceof ProgrammerPanel)
        { return getTextPaneValueForProgrammerPanel(); }
        else
        {
            LOGGER.warn("Implement");
            return BLANK.getValue();
        }
    }

    /**
     * Returns the value in the text pane when panel is programmer panel.
     * Value in text pane for programmer panel is as follows:
     * Byte Space Space Base NEWLINE NEWLINE value[valuePosition] NEWLINE
     * @return the value in the programmer text pane
     */
    public String getTextPaneValueForProgrammerPanel()
    {
        String currentValue = BLANK.getValue();
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
                            currentValue = textPane.getText().split(NEWLINE.getValue())[2]
                                    .replace(COMMA.getValue(), BLANK.getValue());
                        }
                        case BYTE_DWORD -> {
                            currentValue = textPane.getText().split(NEWLINE.getValue())[2]
                                    + textPane.getText().split(NEWLINE.getValue())[3]
                                    .replace(COMMA.getValue(), BLANK.getValue());
                        }
                        case BYTE_QWORD -> {
                            currentValue = textPane.getText().split(NEWLINE.getValue())[2]
                                    + textPane.getText().split(NEWLINE.getValue())[3]
                                    + textPane.getText().split(NEWLINE.getValue())[4]
                                    + textPane.getText().split(NEWLINE.getValue())[5]
                                    .replace(COMMA.getValue(), BLANK.getValue());
                        }
                    }
                    currentValue = currentValue.replace(SPACE.getValue(), BLANK.getValue());
                    currentValue = adjustBinaryNumber(currentValue);
                }
                case BASE_OCTAL,
                     BASE_DECIMAL,
                     BASE_HEXADECIMAL -> {
                    currentValue = textPane.getText().split(NEWLINE.getValue())[2]
                            .replace(COMMA.getValue(), BLANK.getValue());
                }
            }
            if (currentValue.isEmpty()) {
                LOGGER.warn("Attempted to retrieve value from text pane but it was empty. Returning blank.");
                return BLANK.getValue();
            }
        }
        catch (ArrayIndexOutOfBoundsException ae1)
        {
            try
            {
                var splitTextValue = textPane.getText().split(NEWLINE.getValue());
                // TODO: Rework to not use Strings
                var textPaneTopRowExpectedValues = List.of("Byte  Binary", "Byte  Octal", "Byte  Decimal", "Byte  Hexadecimal");
                return splitTextValue.length == 1 && textPaneTopRowExpectedValues.contains(splitTextValue[0])
                        ? BLANK.getValue()
                        : splitTextValue[2].replace(COMMA.getValue(), BLANK.getValue());
            } catch (ArrayIndexOutOfBoundsException ae2)
            {
                logException(new CalculatorError("Attempted to retrieve value from text pane but it was null. Returning blank."));
                return BLANK.getValue();
            }
        }
        catch (Exception e)
        {
            logException(e);
            return BLANK.getValue();
        }
        return currentValue;
    }

    /**
     * Returns the text in the current panel's history pane
     * @return the history text for the current panel
     */
    public String getBasicHistoryPaneTextWithoutNewLineCharacters()
    {
        if (currentPanel instanceof BasicPanel basicPanel)
        { return basicPanel.getHistoryTextPane().getText().replace(addNewLines(1), BLANK.getValue()).strip(); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { return programmerPanel.getHistoryTextPane().getText().replace(addNewLines(1), BLANK.getValue()).strip(); }
        else
        {
            LOGGER.warn("Add other panels history pane if intended to have one");
            return BLANK.getValue();
        }
    }

    /**
     * This method is used after any result to verify
     * the result of the previous method and see the
     * values of the entire Calculator object
     *
     * @param message the message to pass into confirm
     */
    public void confirm(String message)
    {
        String textPaneValue = getTextPaneValue();
        LOGGER.info("Confirm Results: {}", message);
        LOGGER.info("----------------");
        switch (calculatorView) {
            case VIEW_BASIC, VIEW_PROGRAMMER -> {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("view: {}", calculatorView);
                    if (BASE_BINARY == calculatorBase) LOGGER.debug("textPane: '{}'", ((ProgrammerPanel)currentPanel).separateBits(textPaneValue));
                    else LOGGER.debug("textPane: '{}'", textPaneValue);
                    if (isMemoryValuesEmpty())
                    { LOGGER.debug("no memories stored!"); }
                    else {
                        LOGGER.debug("memoryPosition: {}", memoryPosition);
                        LOGGER.debug("memoryRecallPosition: {}", memoryRecallPosition);
                        for (int i = 0; i < 10; i++) {
                            if (!memoryValues[i].isBlank()) {
                                LOGGER.debug("memoryValues[{}]: {}", i, memoryValues[i]);
                            }
                        }
                    }
                    LOGGER.debug("addBool: {}", isAdding ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("subBool: {}", isSubtracting ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("mulBool: {}", isMultiplying ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("divBool: {}", isDividing ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    if (VIEW_PROGRAMMER == calculatorView)
                    {
                        LOGGER.debug("orBool: {}", ((ProgrammerPanel)currentPanel).isOr() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                        LOGGER.debug("modBool: {}", ((ProgrammerPanel)currentPanel).isModulus() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                        LOGGER.debug("xorBool: {}", ((ProgrammerPanel)currentPanel).isXor() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                        LOGGER.debug("notBool: {}", ((ProgrammerPanel)currentPanel).isNot() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                        LOGGER.debug("andBool: {}", ((ProgrammerPanel)currentPanel).isAnd() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    }
                    LOGGER.debug("values[0]: '{}'", values[0]);
                    LOGGER.debug("values[1]: '{}'", values[1]);
                    LOGGER.debug("values[2]: '{}'", values[2]);
                    LOGGER.debug("values[3]: '{}'", values[3]);
                    LOGGER.debug("valuesPosition: {}", valuesPosition);
                    LOGGER.debug("firstNumBool: {}", isFirstNumber ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("isDotEnabled: {}", isDotPressed() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("isNegative: {}", isNumberNegative ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.debug("calculatorBase: {}", calculatorBase);
                    LOGGER.debug("calculatorByte: {}", calculatorByte);
                }
                else
                {
                    LOGGER.info("view: {}", calculatorView);
                    if (BASE_BINARY == calculatorBase) LOGGER.info("textPane: '{}'", ((ProgrammerPanel)currentPanel).separateBits(textPaneValue));
                    else LOGGER.info("textPane: '{}'", textPaneValue);
                    if (isMemoryValuesEmpty())
                    { LOGGER.info("no memories stored!"); }
                    else {
                        LOGGER.info("memoryPosition: {}", memoryPosition);
                        LOGGER.info("memoryRecallPosition: {}", memoryRecallPosition);
                        for (int i = 0; i < 10; i++) {
                            if (!memoryValues[i].isBlank()) {
                                LOGGER.info("memoryValues[{}]: {}", i, memoryValues[i]);
                            }
                        }
                    }
                    if (isAdding) LOGGER.info("addBool: {}", YES.getValue().toLowerCase());
                    if (isSubtracting) LOGGER.info("subBool: {}", YES.getValue().toLowerCase());
                    if (isMultiplying) LOGGER.info("mulBool: {}", YES.getValue().toLowerCase());
                    if (isDividing) LOGGER.info("divBool: {}", YES.getValue().toLowerCase());
                    if (VIEW_PROGRAMMER == calculatorView)
                    {
                        if (((ProgrammerPanel)currentPanel).isOr()) LOGGER.info("orBool: {}", YES.getValue().toLowerCase());
                        if (((ProgrammerPanel)currentPanel).isModulus()) LOGGER.info("modBool: {}", YES.getValue().toLowerCase());
                        if (((ProgrammerPanel)currentPanel).isXor()) LOGGER.info("xorBool: {}", YES.getValue().toLowerCase());
                        if (((ProgrammerPanel)currentPanel).isNot()) LOGGER.info("notBool: {}", YES.getValue().toLowerCase());
                        if (((ProgrammerPanel)currentPanel).isAnd()) LOGGER.info("andBool: {}", YES.getValue().toLowerCase());
                    }
                    if (!values[0].isEmpty()) LOGGER.info("values[0]: '{}'", values[0]);
                    if (!values[1].isEmpty()) LOGGER.info("values[1]: '{}'", values[1]);
                    if (!values[2].isEmpty()) LOGGER.info("values[2]: '{}'", values[2]);
                    if (!values[3].isEmpty()) LOGGER.info("values[3]: '{}'", values[3]);
                    LOGGER.info("valuesPosition: {}", valuesPosition);
                    LOGGER.info("firstNumBool: {}", isFirstNumber ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.info("isDotEnabled: {}", isDotPressed() ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    // TODO: exclude if: v[vP] is empty, bc no need to print if an empty value is negative
                    // TODO: exclude if: the negative sign is not present
                    LOGGER.info("isNegative: {}", isNumberNegative ? YES.getValue().toLowerCase() : NO.getValue().toLowerCase());
                    LOGGER.info("calculatorBase: {}", calculatorBase);
                    LOGGER.info("calculatorByte: {}", calculatorByte);
                }
            }
            case VIEW_SCIENTIFIC -> {
                LOGGER.warn("Confirm message not setup for " + calculatorView);
            }
            case VIEW_DATE -> {
                LOGGER.info("dateOperation: {}", dateOperation);
                LocalDateTime date = LocalDateTime.of(((DatePanel)currentPanel).getTheDateFromTheFromDate(), LocalTime.now());
                var updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                var capitalizedDay = updatedDate.split(SPACE.getValue())[0].toUpperCase();
                var capitalizedMonth = updatedDate.split(SPACE.getValue())[1].toUpperCase();
                updatedDate = capitalizedDay + SPACE.getValue() + capitalizedMonth + SPACE.getValue() + updatedDate.split(SPACE.getValue())[2] + SPACE.getValue() + updatedDate.split(SPACE.getValue())[3];
                LOGGER.info("From Date: {}", updatedDate);
                if (dateOperation == DIFFERENCE_BETWEEN_DATES)
                {
                    date = LocalDateTime.of(((DatePanel)currentPanel).getTheDateFromTheToDate(), LocalTime.now());
                    updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                    capitalizedDay = updatedDate.split(SPACE.getValue())[0].toUpperCase();
                    capitalizedMonth = updatedDate.split(SPACE.getValue())[1].toUpperCase();
                    updatedDate = capitalizedDay + SPACE.getValue() + capitalizedMonth + SPACE.getValue() + updatedDate.split(SPACE.getValue())[2] + SPACE.getValue() + updatedDate.split(SPACE.getValue())[3];
                    LOGGER.info("To Date: {}", updatedDate);
                    LOGGER.info("Difference");
                    LOGGER.info("Year: {}", ((DatePanel) currentPanel).getYearsDifferenceLabel().getText());
                    LOGGER.info("Month: {}", ((DatePanel) currentPanel).getMonthsDifferenceLabel().getText());
                    LOGGER.info("Weeks: {}", ((DatePanel) currentPanel).getWeeksDifferenceLabel().getText());
                    LOGGER.info("Days: {}", ((DatePanel) currentPanel).getDaysDifferenceLabel().getText());
                }
                else // dateOperation == ADD_SUBTRACT_DAYS
                {
                    boolean isAddSelected = ((DatePanel) currentPanel).getAddRadioButton().isSelected();
                    if (isAddSelected) LOGGER.info("Add Selected");
                    else               LOGGER.info("Subtract Selected");
                    var addSubYears = ((DatePanel) currentPanel).getYearsTextField().getText();
                    if (!addSubYears.isBlank()) LOGGER.info("Years: {}", addSubYears);
                    var addSubMonths = ((DatePanel) currentPanel).getMonthsTextField().getText();
                    if (!addSubMonths.isBlank()) LOGGER.info("Months: {}", addSubMonths);
                    var addSubWeeks = ((DatePanel) currentPanel).getWeeksTextField().getText();
                    if (!addSubWeeks.isBlank()) LOGGER.info("Weeks: {}", addSubWeeks);
                    var addSubDays = ((DatePanel) currentPanel).getDaysTextField().getText();
                    if (!addSubDays.isBlank()) LOGGER.info("Days: {}", addSubDays);
                    LOGGER.info("Result: " + ((DatePanel)currentPanel).getResultsLabel().getText());
                }
            }
            case VIEW_CONVERTER -> {
                LOGGER.info("Converter: {}", ((ConverterPanel) currentPanel).getConverterType());
                LOGGER.info("text field 1: {}", ((ConverterPanel) currentPanel).getTextField1().getText() + SPACE.getValue()
                        + ((ConverterPanel) currentPanel).getUnitOptions1().getSelectedItem());
                LOGGER.info("text field 2: {}", ((ConverterPanel) currentPanel).getTextField2().getText() + SPACE.getValue()
                        + ((ConverterPanel) currentPanel).getUnitOptions2().getSelectedItem());
            }
        }
        LOGGER.info("-------- End Confirm Results --------{}", addNewLines(1));
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
            if (currentPanel instanceof BasicPanel) newLines = NEWLINE.getValue().repeat(1);
            else if (currentPanel instanceof ProgrammerPanel) newLines = NEWLINE.getValue().repeat(1);
            else if (currentPanel instanceof ScientificPanel) newLines = NEWLINE.getValue().repeat(3);
        }
        else
        { newLines = NEWLINE.getValue().repeat(newLinesNumber); }
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
        LOGGER.debug("Configuring " + ABOUT_CALCULATOR.getValue() + " text...");
        String computerText = "", version = "";
        if (isMacOperatingSystem()) { computerText = APPLE.getValue(); }
        else                        { computerText = WINDOWS.getValue(); }
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
        JOptionPane.showMessageDialog(this, scrollPane, "Viewing " + VIEW_BASIC.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        confirm("Viewing " + VIEW_BASIC.getValue() + " Calculator Help");
    }

    public void updateHelpMenu(String helpString)
    {
        //JMenu helpMenuItem = getHelpMenu();
        JMenuItem viewHelp = helpMenu.getItem(0);
        // remove any and all other view help actions
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel(helpString));
        helpMenu.add(viewHelp, 0);
        LOGGER.debug("Help menu configured for {}", calculatorView);
    }

    /**
     * This method updates the panel by removing the old panel,
     * setting up the new panel, and adds it to the frame
     * @param newPanel the panel to update on the Calculator
     */
    public void updateJPanel(JPanel newPanel)
    {
        LOGGER.debug("Updating to panel {}...", newPanel.getClass().getSimpleName());
        JPanel oldPanel = currentPanel;
        remove(oldPanel);
        this.currentPanel = newPanel;
        this.calculatorView = determineView();
        setupPanel();
        add(currentPanel);
        LOGGER.debug("Panel updated");
    }

    /**
     * Determines the CalculatorView based off the current panel
     * @return CalculatorView the appropriate view
     */
    public CalculatorView determineView()
    {
        return switch (currentPanel.getClass().getSimpleName())
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
        LOGGER.debug("isPositiveNumber({}): {}", number, !number.contains(SUBTRACTION.getValue()));
        return !number.contains(SUBTRACTION.getValue());
    }

    /**
     * Tests whether a number is negative
     *
     * @param number the value to test
     * @return Fully tested
     */
    public boolean isNegativeNumber(String number)
    {
        LOGGER.debug("isNegativeNumber({}): {}", number, number.contains(SUBTRACTION.getValue()));
        return number.contains(SUBTRACTION.getValue());
    }

    // TODO: Remove isNumberNegative = true; This method should only return a copy of the number in its negative form
    /**
     * Converts a number to its negative equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.debug("Converting {} to negative number", number.replaceAll("\n", BLANK.getValue()));
        if (!number.contains(SUBTRACTION.getValue()))
            number = SUBTRACTION.getValue() + number.replaceAll("\n", BLANK.getValue());
        isNumberNegative = true;
        LOGGER.debug("Updated: {}", number);
        return number;
    }

    // TODO: Remove isNumberNegative = false; This method should only return a copy of the number in its positive form
    /**
     * Converts a number to its positive equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.debug("Converting {} to positive number", number.replaceAll("\n", BLANK.getValue()));
        number = number.replaceAll(SUBTRACTION.getValue(), BLANK.getValue()).trim();
        isNumberNegative = false;
        LOGGER.debug("Updated: {}", number);
        return number;
    }

    /**
     * Resets the 4 main operators to the boolean passed in
     * @param reset a boolean to reset the operators to
     */
    public void resetBasicOperators(boolean reset)
    {
        isAdding = reset;
        isSubtracting = reset;
        isMultiplying = reset;
        isDividing = reset;
        LOGGER.debug("Main basic operators reset to {}", reset);
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
        if (!isDecimal(currentNumber)) return currentNumber;
        int index = currentNumber.indexOf(DECIMAL.getValue());
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = BLANK.getValue();
        else
        {
            leftSide = currentNumber.substring(0, index);
            if (StringUtils.isEmpty(leftSide)) leftSide = ZERO.getValue();
        }
        LOGGER.debug("Number to the left of the decimal: {}", leftSide);
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
        int index = currentNumber.indexOf(DECIMAL.getValue());
        if (index == -1 || (index + 1) >= currentNumber.length()) rightSide = BLANK.getValue();
        else
        {
            rightSide = currentNumber.substring(index + 1);
            if (StringUtils.isEmpty(rightSide)) rightSide = ZERO.getValue();
        }
        LOGGER.debug("Number to the right of the decimal: {}", rightSide);
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
     * @return boolean is minimum value has been met
     */
    public boolean isMinimumValue()
    {
        LOGGER.debug("is {} minimumValue: {}", values[0], values[0].equals(MIN_VALUE.getValue()));
        LOGGER.debug("is {} minimumValue: {}", values[1], values[1].equals(MIN_VALUE.getValue()));
        return values[0].equals(MIN_VALUE.getValue()) ||
               values[1].equals(MIN_VALUE.getValue()); // 10^-7
    }

    /**
     * Checks if the resulting answer has met the minimum value
     * @param valueToCheck String the value to check
     * @return boolean true if the minimum value has been met
     */
    public boolean isMinimumValue(String valueToCheck)
    {
        LOGGER.debug("is {} minimumValue: {}", valueToCheck, valueToCheck.equals(MIN_VALUE.getValue()));
        return valueToCheck.equals(MIN_VALUE.getValue());
    }

    /**
     * Returns true if the maximum value has
     * been met or false otherwise
     * @return boolean if maximum value has been met
     */
    public boolean isMaximumValue()
    {
        LOGGER.debug("is '{}' maximumValue: {}", values[0], values[0].equals(MAX_VALUE.getValue()));
        LOGGER.debug("is '{}' maximumValue: {}", values[1], values[1].equals(MAX_VALUE.getValue()));
        LOGGER.debug("does '{}' maximumValue contain E: {}", values[0], values[0].contains(E.getValue()));
        LOGGER.debug("does '{}' maximumValue contain E: {}", values[1], values[1].contains(E.getValue()));
        return values[0].equals(MAX_VALUE.getValue()) || values[0].contains(E.getValue()) ||
               values[1].equals(MAX_VALUE.getValue()) || values[1].contains(E.getValue());  // 9,999,999 or (10^8) -1
    }

    /**
     * Checks if the resulting answer has met the maximum value
     * @param valueToCheck String the value to check
     * @return boolean true if the maximum value has been met
     */
    public boolean isMaximumValue(String valueToCheck)
    {
        LOGGER.debug("is {} maximumValue: {}", valueToCheck, valueToCheck.equals(MAX_VALUE.getValue()));
        LOGGER.debug("does {} minimumValue contain E: {}", valueToCheck, valueToCheck.contains(E.getValue()));
        return valueToCheck.equals(MAX_VALUE.getValue()) || valueToCheck.contains(E.getValue());
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
     * Adds the text appropriately
     * @param text the text to add
     */
    public void appendTextToPane(String text)
    {
        if (currentPanel instanceof BasicPanel basicPanel)
        {
            basicPanel.appendTextToBasicPane(text);
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            if (PUSHED_CLEAR.getValue().equals(values[3]) //||
                //(!values[valuesPosition].isEmpty() && !text.equals(values[valuesPosition])) ||
                //(valuesPosition != 0 && !values[valuesPosition-1].isEmpty() && !text.equals(values[valuesPosition-1]))
            ) {
                programmerPanel.appendTextToProgrammerPane(text);
                return;
            }
            switch (calculatorBase) {
                case BASE_BINARY -> {
                    programmerPanel.appendTextToProgrammerPane(programmerPanel.separateBits(text));
                }
                case BASE_OCTAL -> {
                    programmerPanel.appendTextToProgrammerPane(text);
                }
                case BASE_DECIMAL -> {
                    String textWithCommasAndOperators = addCommas(text);
                    //if (programmerPanel.determineIfAnyProgrammerOperatorWasPushed())
                    //{
                    //    textWithCommasAndOperators += SPACE.getValue() + programmerPanel.getActiveProgrammerPanelOperator();
                    //}
                    programmerPanel.appendTextToProgrammerPane(textWithCommasAndOperators);
                }
                case BASE_HEXADECIMAL -> {
                    programmerPanel.appendTextToProgrammerPane(text);
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
        if (currentPanel instanceof BasicPanel basicPanel)
        {
            basicPanel.appendTextToBasicPane(BLANK.getValue());
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            programmerPanel.appendTextToProgrammerPane(BLANK.getValue());
        }
    }

    /* Getters */
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
    public boolean isAdding() { return isAdding; }
    public boolean isSubtracting() { return isSubtracting; }
    public boolean isMultiplying() { return isMultiplying; }
    public boolean isDividing() { return isDividing; }
    public boolean isDotPressed() { return buttonDecimal.isEnabled(); }
    public JMenu getLookMenu() { return lookMenu; }
    public JMenu getViewMenu() { return viewMenu; }
    public JMenu getEditMenu() { return editMenu; }
    public JMenu getHelpMenu() { return helpMenu; }
    public boolean isMetal() { return isMetal; }
    public boolean isSystem() { return isSystem; }
    public boolean isWindows() { return isWindows; }
    public boolean isMotif() { return isMotif; }
    public boolean isGtk() { return isGtk; }
    public boolean isApple() { return isApple; }

    /* Setters */
    private void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    private void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setMemoryRecallPosition(int memoryRecallPosition) { this.memoryRecallPosition = memoryRecallPosition; }
    public void setTextPane(JTextPane textPane) { this.textPane = textPane; }
    public void setCalculatorView(CalculatorView calculatorView) { this.calculatorView = calculatorView; }
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculatorBase = calculatorBase; }
    public void setCalculatorBaseAndUpdatePreviousBase(CalculatorBase calculatorBase) {
        LOGGER.info("updating previous base, currently set to {}", previousBase);
        setPreviousBase(getCalculatorBase());
        setCalculatorBase(calculatorBase);
        LOGGER.info("previous base now set as {}", previousBase);
    }
    public void setPreviousBase(CalculatorBase previousBase) { this.previousBase = previousBase; }
    public void setCalculatorByte(CalculatorByte calculatorByte) { this.calculatorByte = calculatorByte; }
    public void setDateOperation(DateOperation dateOperation) { this.dateOperation = dateOperation; }
    public void setConverterType(ConverterType converterType) { this.converterType = converterType; }
    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setCalculatorIcon(ImageIcon calculatorIcon) { this.calculatorIcon = calculatorIcon; }
    public void setMacIcon(ImageIcon macIcon) { this.macIcon = macIcon; }
    public void setWindowsIcon(ImageIcon windowsIcon) { this.windowsIcon = windowsIcon; }
    public void setBlankIcon(ImageIcon blankIcon) { this.blankIcon = blankIcon; }
    public void setIsFirstNumber(boolean firstNumber) { this.isFirstNumber = firstNumber; }
    public void setIsNumberNegative(boolean numberNegative) { this.isNumberNegative = numberNegative; }
    public void setIsAdding(boolean adding) { this.isAdding = adding; }
    public void setIsSubtracting(boolean subtracting) { this.isSubtracting = subtracting; }
    public void setIsMultiplying(boolean multiplying) { this.isMultiplying = multiplying; }
    public void setIsDividing(boolean dividing) { this.isDividing = dividing; }
    public void setCalculatorMenuBar(JMenuBar menuBar) { this.menuBar = menuBar; }
    public void setLookMenu(JMenu jMenu) { this.lookMenu = jMenu; }
    public void setViewMenu(JMenu jMenu) { this.viewMenu = jMenu; }
    public void setEditMenu(JMenu jMenu) { this.editMenu = jMenu; }
    public void setHelpMenu(JMenu jMenu) { this.helpMenu = jMenu; }
    public void setIsMetal(boolean isMetal) { this.isMetal = isMetal; }
    public void setIsSystem(boolean isSystem) { this.isSystem = isSystem; }
    public void setIsWindows(boolean isWindows) { this.isWindows = isWindows; }
    public void setIsMotif(boolean isMotif) { this.isMotif = isMotif; }
    public void setIsGtk(boolean isGtk) { this.isGtk = isGtk; }
    public void setIsApple(boolean isApple) { this.isApple = isApple; }
}