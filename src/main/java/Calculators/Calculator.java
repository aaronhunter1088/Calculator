package Calculators;

import Panels.*;
import Runnables.CalculatorMain;
import Types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.types.LogLevel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static Types.Texts.*;
import static Types.CalculatorType.*;
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
    protected CalculatorKeyListener keyListener;
    protected CalculatorMouseListener mouseListener;
    // Values used to store input. Only one set of values, and memoryValues
    protected String[]
            values = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}, // firstNum or total, secondNum, copy, temporary storage
            memoryValues = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}; // stores memory values; rolls over after 10 entries
    private int valuesPosition = 0, memoryPosition = 0, memoryRecallPosition = 0;
    private JTextPane textPane;

    private CalculatorType calculatorType;
    private CalculatorBase calculatorBase;
    private CalculatorByte calculatorByte;
    private DateOperation dateOperation;
    private ConverterType converterType;
    private JPanel currentPanel;
    // Images used
    private ImageIcon calculatorIcon, macIcon, windowsIcon, blankIcon;
    private JLabel iconLabel, textLabel;
    // Menubar
    private JMenuBar menuBar;
    // Menubar Items
    private JMenu lookMenu, viewMenu, editMenu, helpMenu;
    // Flags
    private boolean
            isFirstNumber = true, isNumberNegative = false,
            isAdding = false, isSubtracting = false,
            isMultiplying = false, isDividing = false,
            isNegating = false,
            isMetal = false, isSystem = false,
            isWindows = false, isMotif = false,
            isGtk = false, isApple = false;

    /* Constructors */

    /**
     * Starts the calculator with the BASIC CalculatorType
     *
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(BASIC, null, null, null); }

    /**
     * Starts the Calculator with a specific CalculatorType
     *
     * @param calculatorType the type of Calculator to create
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorType calculatorType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(calculatorType, BASE_DECIMAL, null, null); }

    /**
     * Starts the calculator with the PROGRAMMER CalculatorType with a specified CalculatorBase
     *
     * @param calculatorBase the base to set that Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(CalculatorBase calculatorBase) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(PROGRAMMER, calculatorBase, null, null); }

    /**
     * This constructor is used to create a DATE Calculator with a specific DateOperation
     *
     * @param dateOperation  the option to open the DateCalculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(DATE, null, null, dateOperation); }

    /**
     * This constructor is used to create a Converter Calculator starting with a specific ConverterType
     *
     * @param converterType  the type of unit to start the Converter Calculator in
     * @throws CalculatorError when Calculator fails to build
     */
    public Calculator(ConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    { this(CONVERTER, null, converterType, null); }

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
        super(calculatorType.getValue());
        setCalculatorType(calculatorType);
        setCalculatorBase(calculatorBase);
        setConverterType(converterType);
        setDateOperation(dateOperation);
        setupMenuBar();
        setupCalculatorImages();
        if (converterType == null && dateOperation == null) setCurrentPanel(determinePanel(calculatorType, calculatorBase));
        else if (converterType != null) setCurrentPanel(determinePanel(calculatorType, null, converterType, null));
        else setCurrentPanel(determinePanel(calculatorType, null, null, dateOperation));
        setupPanel();
        add(currentPanel);
        LOGGER.debug("Panel added to calculator");
        setCalculatorBase(determineCalculatorBase(calculatorBase));
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setMetal(true);
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setSystem(true);
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setWindows(true);
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                resetLook();
                setMotif(true);
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setGtk(true);
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
                    if (null != basicPanel.getBasicHistoryTextPane())
                    {
                        basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                        basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
                    }
                }
                else
                {
                    LOGGER.warn("Implement changing history panel for {}", currentPanel.getName());
                }
                SwingUtilities.updateComponentTreeUI(this);
                resetLook();
                setApple(true);
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
        JMenuItem basic = new JMenuItem(CalculatorType.BASIC.getValue());
        JMenuItem programmer = new JMenuItem(CalculatorType.PROGRAMMER.getValue());
        JMenuItem date = new JMenuItem(CalculatorType.DATE.getValue());
        JMenu converterMenu = new JMenu(CONVERTER.getValue());
        basic.setFont(mainFont);
        basic.setName(BASIC.getValue());
        basic.addActionListener(this::switchPanels);
        programmer.setFont(mainFont);
        programmer.setName(PROGRAMMER.getValue());
        programmer.addActionListener(this::switchPanels);
        date.setFont(mainFont);
        date.setName(DATE.getValue());
        date.addActionListener(this::switchPanels);
        JMenuItem angleConverter = new JMenuItem(ANGLE.getValue());
        angleConverter.setFont(mainFont);
        angleConverter.setName(ANGLE.getValue());
        angleConverter.addActionListener(this::switchPanels);
        JMenuItem areaConverter = new JMenuItem(AREA.getValue()); // The converterMenu is a menu of more choices
        areaConverter.setFont(mainFont);
        areaConverter.setName(AREA.getValue());
        areaConverter.addActionListener(this::switchPanels);
        converterMenu.setFont(mainFont);
        converterMenu.setName(CONVERTER.getValue());
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
        clearHistoryItem.addActionListener(this::performClearBasicHistoryTextPaneAction);

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
            LOGGER.error("Could not find an image using path: " + path + '!');
            LOGGER.error("ImageIcon not created. Returning null");
        }
        return imageIcon;
    }

    /**
     * @param calculatorType the CalculatorType to use
     * @return JPanel, the Panel to use
     */
    private JPanel determinePanel(CalculatorType calculatorType, CalculatorBase calculatorBase)
    { return determinePanel(calculatorType, calculatorBase, null, null); }

    /**
     * @param calculatorType the CalculatorType to use
     * @param calculatorBase the CalculatorBase to use
     * @param converterType  the ConverterType to use
     * @param dateOperation  the DateOperation to use
     * @return JPanel, the Panel to use
     */
    private JPanel determinePanel(CalculatorType calculatorType, CalculatorBase calculatorBase, ConverterType converterType, DateOperation dateOperation)
    {
        LOGGER.debug("DeterminePanel, type:{}", calculatorType);
        return switch (calculatorType)
        {
            case BASIC -> new BasicPanel();
            case PROGRAMMER -> new ProgrammerPanel();
            case SCIENTIFIC -> new ScientificPanel();
            case DATE -> new DatePanel();
            case CONVERTER -> new ConverterPanel(converterType);
        };
    }

    /**
     * The main method that calls the setup method for a specific panel
     */
    private void setupPanel()
    {
        LOGGER.debug("Setting up panel, {}", currentPanel.getClass().getSimpleName());
        if (currentPanel instanceof BasicPanel panel)
        { panel.setupBasicPanel(this); }
        else if (currentPanel instanceof ProgrammerPanel panel)
        { panel.setupProgrammerPanel(this, calculatorBase); }
        else if (currentPanel instanceof ScientificPanel panel)
        { LOGGER.info("IMPLEMENT SCIENTIFIC PANEL"); /*panel.setupScientificPanel(this, calculatorBase);*/ }
        else if (currentPanel instanceof DatePanel panel)
        { panel.setupDatePanel(this, DIFFERENCE_BETWEEN_DATES); }
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
            getTextPane().setParagraphAttributes(attribs, true);
            getTextPane().setFont(mainFont);
            getTextPane().setPreferredSize(new Dimension(70, 30));
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            String[] initString = {
                    addNewLineCharacters(1)+values[valuesPosition]+addNewLineCharacters(1),
                    programmerPanel.addByteRepresentations()
            };
            String[] initStyles = { "regular", "regular"};
            String[] alignmentStyles = {"alignRight", "alignLeft"};

            JTextPane textPane = new JTextPane();
            StyledDocument doc = textPane.getStyledDocument();

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
                    doc.insertString(doc.getLength(), initString[i]
                            ,doc.getStyle(initStyles[i])
                    );
                    doc.setParagraphAttributes(doc.getLength() - initString[i].length(), initString[i].length(), doc.getStyle(style), false);
                }
            } catch(BadLocationException ble) {
                logException(ble);
            } catch(Exception e) {
                logException(e);
            }
            setTextPane(textPane);
            getTextPane().setPreferredSize(new Dimension(105, 120));
        }
        if (isMotif())
        {
            getTextPane().setBackground(new Color(174,178,195));
            getTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        {
            getTextPane().setBackground(Color.WHITE);
            getTextPane().setBorder(new LineBorder(Color.BLACK));
        }
        getTextPane().setEditable(false);
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
            basicPanel.getBasicHistoryTextPane().setParagraphAttributes(attribs, true);
            basicPanel.getBasicHistoryTextPane().setFont(mainFont);
            if (isMotif())
            {
                basicPanel.getBasicHistoryTextPane().setBackground(new Color(174,178,195));
                basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
            }
            else
            {
                basicPanel.getBasicHistoryTextPane().setBackground(Color.WHITE);
                basicPanel.getBasicHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
            }
            basicPanel.getBasicHistoryTextPane().setEditable(false);
            basicPanel.getBasicHistoryTextPane().setSize(new Dimension(70, 200)); // sets size at start
            basicPanel.getBasicHistoryTextPane().setMinimumSize(basicPanel.getBasicHistoryTextPane().getSize()); // keeps size throughout
            LOGGER.debug("BasicHistoryTextPane configured");
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            programmerPanel.setProgrammerHistoryTextPane(new JTextPane());
            programmerPanel.getProgrammerHistoryTextPane().setParagraphAttributes(attribs, true);
            programmerPanel.getProgrammerHistoryTextPane().setFont(mainFont);
            if (isMotif())
            {
                programmerPanel.getProgrammerHistoryTextPane().setBackground(new Color(174,178,195));
                programmerPanel.getProgrammerHistoryTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
            }
            else
            {
                programmerPanel.getProgrammerHistoryTextPane().setBackground(Color.WHITE);
                programmerPanel.getProgrammerHistoryTextPane().setBorder(new LineBorder(Color.BLACK));
            }
            programmerPanel.getProgrammerHistoryTextPane().setEditable(false);
            programmerPanel.getProgrammerHistoryTextPane().setSize(new Dimension(70, 205)); // sets size at start
            programmerPanel.getProgrammerHistoryTextPane().setMinimumSize(programmerPanel.getProgrammerHistoryTextPane().getSize()); // keeps size throughout
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
        getButtonMemoryClear().setName(MEMORY_CLEAR.getValue());
        getButtonMemoryClear().addActionListener(this::performMemoryClearAction);
        getButtonMemoryRecall().setName(MEMORY_RECALL.getValue());
        getButtonMemoryRecall().addActionListener(this::performMemoryRecallAction);
        getButtonMemoryAddition().setName(MEMORY_ADDITION.getValue());
        getButtonMemoryAddition().addActionListener(this::performMemoryAdditionAction);
        getButtonMemorySubtraction().setName(MEMORY_SUBTRACTION.getValue());
        getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionAction);
        getButtonMemoryStore().setEnabled(true); // Enable memoryStore
        getButtonMemoryStore().setName(MEMORY_STORE.getValue());
        getButtonMemoryStore().addActionListener(this::performMemoryStoreAction);
        getButtonHistory().setEnabled(true);
        getButtonHistory().setName(HISTORY_CLOSED.getValue());
        //reset buttons to enabled if memories are saved
        if (!getMemoryValues()[0].isEmpty())
        {
            getButtonMemoryClear().setEnabled(true);
            getButtonMemoryRecall().setEnabled(true);
            getButtonMemoryAddition().setEnabled(true);
            getButtonMemorySubtraction().setEnabled(true);
        }
        LOGGER.warn("Memory buttons only configured for BASIC/PROGRAMMER Panel");
        LOGGER.debug("Memory buttons configured");
        if (currentPanel instanceof BasicPanel basicPanel)
        { getButtonHistory().addActionListener(basicPanel::performHistoryAction); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { getButtonHistory().addActionListener(programmerPanel::performHistoryAction); }
        LOGGER.debug("History button configured");
    }
    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public void performMemoryStoreAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (getTextPaneWithoutNewLineCharacters().isBlank())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("No number to add to memory");
        }
        else if (textPaneContainsBadText())
        { confirm("Not saving " + getTextPaneWithoutNewLineCharacters() + " in Memory"); }
        else
        {
            if (getMemoryPosition() == 10) // reset to 0
            { setMemoryPosition(0); }
            getMemoryValues()[getMemoryPosition()] = getTextPaneWithoutNewLineCharacters();
            getButtonMemoryRecall().setEnabled(true);
            getButtonMemoryClear().setEnabled(true);
            getButtonMemoryAddition().setEnabled(true);
            getButtonMemorySubtraction().setEnabled(true);
            writeHistoryWithMessage(buttonChoice, false, " Saved: " + getMemoryValues()[getMemoryPosition()] + " to memory location " + (getMemoryPosition()+1));
            setMemoryPosition(getMemoryPosition() + 1);
            confirm(getMemoryValues()[getMemoryPosition()-1] + " is stored in memory at position: " + (getMemoryPosition()-1));

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
        if (getMemoryRecallPosition() == 10 || StringUtils.isBlank(getMemoryValues()[getMemoryRecallPosition()]))
        { setMemoryRecallPosition(getLowestMemoryPosition()); }
        getTextPane().setText(addNewLineCharacters() + getMemoryValues()[getMemoryRecallPosition()]);
        getValues()[getValuesPosition()] = getTextPaneWithoutNewLineCharacters();
        writeHistoryWithMessage(buttonChoice, false, " Recalled: " + getMemoryValues()[getMemoryRecallPosition()] + " at memory location " + (getMemoryRecallPosition()+1));
        setMemoryRecallPosition(getMemoryRecallPosition() + 1);
        confirm("Recalling number in memory: " + getMemoryValues()[(getMemoryRecallPosition()-1)] + " at position: " + (getMemoryRecallPosition()-1));
    }
    /**
     * The actions to perform when MemoryClear is clicked
     * @param actionEvent the click action
     */
    public void performMemoryClearAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (getMemoryPosition() == 10)
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            setMemoryPosition(0);
        }
        if (!isMemoryValuesEmpty())
        {
            setMemoryPosition(getLowestMemoryPosition());
            LOGGER.info("Clearing memoryValue[{}] = {}", getMemoryPosition(), getMemoryValues()[getMemoryPosition()]);
            writeHistoryWithMessage(buttonChoice, false, " Cleared " + getMemoryValues()[getMemoryPosition()] + " from memory location " + (getMemoryPosition()+1));
            getMemoryValues()[getMemoryPosition()] = BLANK.getValue();
            setMemoryRecallPosition(getMemoryRecallPosition() + 1);
            confirm("Cleared memory at " + getMemoryPosition());
            // MemorySuite could now be empty
            if (isMemoryValuesEmpty())
            {
                setMemoryPosition(0);
                setMemoryRecallPosition(0);
                getButtonMemoryClear().setEnabled(false);
                getButtonMemoryRecall().setEnabled(false);
                getButtonMemoryAddition().setEnabled(false);
                getButtonMemorySubtraction().setEnabled(false);
                getTextPane().setText(addNewLineCharacters() + ZERO.getValue());
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
        else if (getTextPaneWithoutNewLineCharacters().isBlank())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("No number to add to memory");
        }
        else
        {
            LOGGER.debug("textPane: '" + getTextPaneWithoutNewLineCharacters() + "'");
            LOGGER.debug("memoryValues[{}] = {}", getMemoryPosition()-1, getMemoryValues()[(getMemoryPosition()-1)]);
            double result = Double.parseDouble(getTextPaneWithoutNewLineCharacters())
                    + Double.parseDouble(getMemoryValues()[(getMemoryPosition()-1)]); // create result forced double
            LOGGER.debug("{} + {} = {}", getTextPaneWithoutNewLineCharacters(), getMemoryValues()[(getMemoryPosition()-1)], result);
            if (result % 1 == 0)
            { getMemoryValues()[(getMemoryPosition()-1)] = clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
            else
            { getMemoryValues()[(getMemoryPosition()-1)] = Double.toString(result); }
            writeHistoryWithMessage(buttonChoice, false, " Added " + getTextPaneWithoutNewLineCharacters() + " to memories at " + (getMemoryPosition()) + " " + EQUALS.getValue() + " " + getMemoryValues()[(getMemoryPosition()-1)]);
            confirm("The new value in memory at position " + (getMemoryPosition()-1) + " is " + getMemoryValues()[(getMemoryPosition()-1)]);
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
        else if (getTextPaneWithoutNewLineCharacters().isBlank())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("No number to subtract from memory");
        }
        else
        {
            LOGGER.debug("textPane: '{}'", getTextPaneWithoutNewLineCharacters());
            LOGGER.debug("memoryValues[{}] = {}", getMemoryPosition()-1, getMemoryValues()[(getMemoryPosition()-1)]);
            double result = Double.parseDouble(getMemoryValues()[(getMemoryPosition()-1)])
                    - Double.parseDouble(getTextPaneWithoutNewLineCharacters()); // create result forced double
            LOGGER.debug("{} - {} = {}", getTextPaneWithoutNewLineCharacters(), getMemoryValues()[(getMemoryPosition()-1)], result);
            getMemoryValues()[(getMemoryPosition()-1)] = Double.toString(result);
            if (result % 1 == 0)
            { getMemoryValues()[(getMemoryPosition()-1)] = clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
            else
            { getMemoryValues()[(getMemoryPosition()-1)] = Double.toString(result); }
            writeHistoryWithMessage(buttonChoice, false, " Subtracted " + getTextPaneWithoutNewLineCharacters() + " to memories at " + (getMemoryPosition()) + " " + EQUALS.getValue() + " " + getMemoryValues()[(getMemoryPosition()-1)]);
            confirm("The new value in memory at position " + (getMemoryPosition()-1) + " is " + getMemoryValues()[(getMemoryPosition()-1)]);
        }
    }

    /**
     * The actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public void performNumberButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (!isFirstNumber()) // second number
        {
            LOGGER.debug("!isFirstNumber is: " + !isFirstNumber());
            getTextPane().setText(BLANK.getValue());
            setFirstNumber(true);
            if (!isNegating()) setNumberNegative(false);
            if (!isDotPressed())
            {
                LOGGER.debug("Decimal is disabled. Enabling");
                getButtonDecimal().setEnabled(true);
            }
        }
        if (performInitialChecks())
        {
            LOGGER.warn("Invalid entry in textPane. Clearing...");
            getTextPane().setText(BLANK.getValue());
            getValues()[getValuesPosition()] = BLANK.getValue();
            setFirstNumber(true);
            getButtonDecimal().setEnabled(true);
        }
        if (getValues()[0].isBlank())
        {
            LOGGER.info("Highest size not met. Values[0] is blank");
        }
        else if (checkValueLength())
        {
            LOGGER.info("Highest size of value has been met");
            confirm("Max length of 7 digit number met");
            return;
        }
        if (isNegating() && isNumberNegative() && getValues()[getValuesPosition()].isBlank())
        {
            getValues()[getValuesPosition()] = SUBTRACTION.getValue() + buttonChoice;
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()]));
            writeHistory(buttonChoice, false);
            setNegating(false);
            setNumberNegative(true);
        }
        else if (isNegating() && isNumberNegative() && !getValues()[1].isBlank())
        {
            getValues()[getValuesPosition()] = SUBTRACTION.getValue() + buttonChoice;
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()]));
            writeHistory(buttonChoice, false);
            setNegating(false);
            setNumberNegative(true);
        }
        else
        {
            getValues()[getValuesPosition()] = getValues()[getValuesPosition()] + buttonChoice;
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()]));
            writeHistory(buttonChoice, false);
        }
        confirm("Pressed " + buttonChoice);
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
        else if (getTextPaneWithoutNewLineCharacters().isEmpty() && getValues()[0].isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + DIVISION + " operation");
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!isAdding() && !isSubtracting()  && !isMultiplying() &&!isDividing()
                    && !getTextPane().getText().isBlank() && !getValues()[getValuesPosition()].isBlank())
            {
                getTextPane().setText(addNewLineCharacters() + getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setDividing(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() && !getValues()[1].isEmpty())
            {
                addition(DIVISION.getValue()); // 5 + 3 ÷
                setAdding(resetCalculatorOperations(isAdding()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setDividing(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setDividing(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isSubtracting() && !getValues()[1].isEmpty())
            {
                subtract(DIVISION.getValue()); // 5 - 3 ÷
                setSubtracting(resetCalculatorOperations(isSubtracting()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setDividing(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setDividing(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isMultiplying() && !getValues()[1].isEmpty())
            {
                multiply(DIVISION.getValue()); // 5 ✕ 3 ÷
                setMultiplying(resetCalculatorOperations(isMultiplying()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setDividing(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setDividing(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isDividing() && !getValues()[1].isEmpty() ) //&& !getValues()[1].equals("0"))
            {
                divide(DIVISION.getValue()); // 5 ÷ 3 ÷
                setDividing(resetCalculatorOperations(isDividing()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setDividing(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else if (getTextPaneWithoutAnyOperator().equals(INFINITY.getValue()))
                {
                    setDividing(false);
                    setValuesPosition(0);
                }
                else
                {
                    setDividing(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (!getTextPaneWithoutNewLineCharacters().isBlank() && getValues()[0].isBlank())
            {
                LOGGER.error("The user pushed divide but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                getValues()[0] = getTextPaneWithoutNewLineCharacters();
                getTextPane().setText(addNewLineCharacters() + getValues()[0] + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setDividing(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() || isSubtracting() || isMultiplying() || isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            getButtonDecimal().setEnabled(true);
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
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result;
        if (!ZERO.getValue().equals(getValues()[1]))
        {
            result = Double.parseDouble(getValues()[0]) / Double.parseDouble(getValues()[1]); // create result forced double
            LOGGER.info(getValues()[0] + " " + DIVISION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(EQUALS.getValue(), DIVISION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                getTextPane().setText(addNewLineCharacters() + getValues()[getValuesPosition()]);
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(EQUALS.getValue(), DIVISION.getValue(), result, false);
                getValues()[0] = addCommas(formatNumber(String.valueOf(result)));
            }
            confirm("Finished dividing");
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
            getTextPane().setText(addNewLineCharacters() + INFINITY.getValue());
            getValues()[0] = BLANK.getValue();
            getValues()[1] = BLANK.getValue();
            setFirstNumber(true);
            confirm("Attempted to divide by 0. Values[0] = 0");
        }
    }
    private void divide(String continuedOperation)
    {
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result;
        if (!ZERO.getValue().equals(getValues()[1]))
        {
            result = Double.parseDouble(getValues()[0]) / Double.parseDouble(getValues()[1]); // create result forced double
            LOGGER.info(getValues()[0] + " " + DIVISION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                writeContinuedHistory(continuedOperation, DIVISION.getValue(), result, true);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
                getTextPane().setText(addNewLineCharacters() + getValues()[getValuesPosition()]);
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, DIVISION.getValue(), result, true);
                getValues()[0] = String.valueOf(result);
                getButtonDecimal().setEnabled(false);
            }
            LOGGER.info("Finished " + DIVISION);
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
            getTextPane().setText(addNewLineCharacters() + INFINITY.getValue());
            getValues()[0] = BLANK.getValue();
            getValues()[1] = BLANK.getValue();
            setFirstNumber(true);
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
        else if (getTextPaneWithoutNewLineCharacters().isEmpty() && getValues()[0].isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + MULTIPLICATION + " operation");
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!isAdding() && !isSubtracting()  && !isMultiplying() &&!isDividing()
                    && !getTextPane().getText().isBlank() && !getValues()[getValuesPosition()].isBlank())
            {
                getTextPane().setText(addNewLineCharacters() + getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setMultiplying(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() && !getValues()[1].isEmpty())
            {
                addition(MULTIPLICATION.getValue()); // 5 + 3 ✕...
                setAdding(resetCalculatorOperations(isAdding()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setMultiplying(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setMultiplying(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isSubtracting() && !getValues()[1].isEmpty())
            {
                subtract(MULTIPLICATION.getValue()); // 5 - 3 ✕...
                setSubtracting(resetCalculatorOperations(isSubtracting()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setMultiplying(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setMultiplying(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isMultiplying() && !getValues()[1].isEmpty())
            {
                multiply(MULTIPLICATION.getValue());
                setMultiplying(resetCalculatorOperations(isMultiplying()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setMultiplying(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setMultiplying(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isDividing() && !getValues()[1].isEmpty())
            {
                divide(MULTIPLICATION.getValue());
                setDividing(resetCalculatorOperations(isDividing()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setMultiplying(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setMultiplying(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (!getTextPaneWithoutNewLineCharacters().isBlank() && getValues()[0].isBlank())
            {
                LOGGER.error("The user pushed multiple but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                getValues()[0] = getTextPaneWithoutNewLineCharacters();
                getTextPane().setText(addNewLineCharacters() + getValues()[0] + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setMultiplying(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() || isSubtracting() || isMultiplying() || isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            getButtonDecimal().setEnabled(true);
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
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0])
                * Double.parseDouble(getValues()[1]);
        LOGGER.info(getValues()[0] + " " + MULTIPLICATION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
        try
        {
            int wholeResult = Integer.parseInt(clearZeroesAndDecimalAtEnd(formatNumber(String.valueOf(result))));
            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
            LOGGER.info("We have a whole number");
            writeContinuedHistory(EQUALS.getValue(), MULTIPLICATION.getValue(), result, false);
            getValues()[0] = addCommas(String.valueOf(wholeResult));
            getButtonDecimal().setEnabled(true);
        }
        catch (NumberFormatException nfe)
        {
            logException(nfe);
            LOGGER.info("We have a decimal");
            writeContinuedHistory(EQUALS.getValue(), MULTIPLICATION.getValue(), result, false);
            getValues()[0] = formatNumber(String.valueOf(result));
        }
    }
    private void multiply(String continuedOperation)
    {
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0])
                * Double.parseDouble(getValues()[1]);
        LOGGER.info(getValues()[0] + " " + MULTIPLICATION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
        try
        {
            int wholeResult = Integer.parseInt(clearZeroesAndDecimalAtEnd(formatNumber(String.valueOf(result))));
            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
            LOGGER.info("We have a whole number");
            writeContinuedHistory(continuedOperation, MULTIPLICATION.getValue(), result, true);
            getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(wholeResult));
            getButtonDecimal().setEnabled(true);
        }
        catch (NumberFormatException nfe)
        {
            logException(nfe);
            LOGGER.info("We have a decimal");
            writeContinuedHistory(continuedOperation, MULTIPLICATION.getValue(), result, true);
            getValues()[0] = formatNumber(String.valueOf(result));
            getButtonDecimal().setEnabled(false);
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
        else
        {
            if (!isAdding() && !isSubtracting()  && !isMultiplying() &&!isDividing()
                    && !getTextPane().getText().isBlank() && !getValues()[getValuesPosition()].isBlank())
            {
                getTextPane().setText(addNewLineCharacters() + getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setSubtracting(true);
                setFirstNumber(false);
                setNegating(false);
                setNumberNegative(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (!isAdding() && !isSubtracting()  && !isMultiplying() &&!isDividing()
                    && getTextPaneWithoutNewLineCharacters().isBlank())
            {
                getTextPane().setText(addNewLineCharacters() + buttonChoice);
                writeHistory(buttonChoice, true);
                setNegating(true);
                setNumberNegative(true);
            }
            else if (isAdding() && !getValues()[1].isEmpty())
            {
                addition(SUBTRACTION.getValue());
                setAdding(resetCalculatorOperations(isAdding()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setSubtracting(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setSubtracting(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isSubtracting() && !getValues()[1].isEmpty())
            {
                subtract(SUBTRACTION.getValue());
                setSubtracting(resetCalculatorOperations(isSubtracting()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setSubtracting(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setSubtracting(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isMultiplying() && !getValues()[1].isEmpty())
            {
                multiply(SUBTRACTION.getValue());
                setMultiplying(resetCalculatorOperations(isMultiplying()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setSubtracting(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setSubtracting(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isDividing() && !getValues()[1].isEmpty())
            {
                divide(SUBTRACTION.getValue());
                setDividing(resetCalculatorOperations(isDividing()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setSubtracting(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setSubtracting(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if ((isAdding() || isSubtracting() || isMultiplying() || isDividing())
                    && !isNegating())
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                writeHistory(buttonChoice, true);
                getTextPane().setText(addNewLineCharacters() + buttonChoice);
                setNegating(true);
                setNumberNegative(true);
            }
            else if (!getTextPaneWithoutNewLineCharacters().isBlank() && getValues()[0].isBlank() && !isNegating())
            {
                LOGGER.error("The user pushed subtract but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                getValues()[0] = getTextPaneWithoutNewLineCharacters();
                getTextPane().setText(addNewLineCharacters() + getValues()[0] + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setSubtracting(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            getButtonDecimal().setEnabled(true);
            if (!isNegating()) setNumberNegative(false);
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
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0]) - Double.parseDouble(getValues()[1]);
        if (isNegating())
        {
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                LOGGER.debug(getValues()[0] + " " + ADDITION.getValue() + " " + convertToPositive(getValues()[1]) + " " + EQUALS.getValue() + " " + result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                getValues()[0] = addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                setNegating(false);
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                LOGGER.debug(getValues()[0] + " " + ADDITION.getValue() + " " + convertToPositive(getValues()[1]) + " " + EQUALS.getValue() + " " + result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                getValues()[0] = addCommas(formatNumber(String.valueOf(result)));
                setNegating(false);
                getButtonDecimal().setEnabled(false);
            }
        }
        else
        {
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                LOGGER.debug(getValues()[0] + " " + SUBTRACTION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                getValues()[0] = addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                LOGGER.debug(getValues()[0] + " " + SUBTRACTION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
                writeContinuedHistory(EQUALS.getValue(), SUBTRACTION.getValue(), result, false);
                getValues()[0] = addCommas(formatNumber(String.valueOf(result)));
                getButtonDecimal().setEnabled(false);
            }
        }
    }
    private void subtract(String continuedOperation)
    {
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0]) - Double.parseDouble(getValues()[1]);
        if (result % 1 == 0)
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                if (isNegating())
                {
                    LOGGER.debug("We have a whole number, negating");
                    LOGGER.info(getValues()[0] + " " + ADDITION.getValue() + " " + convertToPositive(getValues()[1]) + " " + EQUALS.getValue() + " " + result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    setNegating(false);
                    getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                    getButtonDecimal().setEnabled(true);
                }
                else
                {
                    LOGGER.debug("We have a whole number");
                    LOGGER.info(getValues()[0] + " " + SUBTRACTION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                    getButtonDecimal().setEnabled(true);
                }
            }
        }
        else
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                if (isNegating())
                {
                    LOGGER.debug("We have a decimal, negating");
                    LOGGER.info(getValues()[0] + " " + ADDITION.getValue() + " " + convertToPositive(getValues()[1]) + " " + EQUALS.getValue() + " " + result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    setNegating(false);
                    getValues()[0] = formatNumber(String.valueOf(result));
                    getButtonDecimal().setEnabled(false);
                }
                else
                {
                    LOGGER.debug("We have a decimal");
                    LOGGER.info(getValues()[0] + " " + SUBTRACTION.getValue() + " " + convertToPositive(getValues()[1]) + " " + EQUALS.getValue() + " " + result);
                    writeContinuedHistory(continuedOperation, SUBTRACTION.getValue(), result, true);
                    getValues()[0] = formatNumber(String.valueOf(result));
                    getButtonDecimal().setEnabled(false);
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
        else if (getTextPaneWithoutNewLineCharacters().isEmpty() && getValues()[0].isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + ADDITION + " operation");
        }
        else
        {
            if (!isAdding() && !isSubtracting()  && !isMultiplying() &&!isDividing()
                    && !getTextPane().getText().isBlank() && !getValues()[getValuesPosition()].isBlank())
            {
                getTextPane().setText(addNewLineCharacters() + getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setAdding(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() && !getValues()[1].isEmpty())
            {
                addition(ADDITION.getValue());  // 5 + 3 + ...
                setAdding(resetCalculatorOperations(isAdding()));
                if (isMaximumValue()) // we can add to the minimum number, not to the maximum
                {
                    setAdding(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setAdding(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isSubtracting() && !getValues()[1].isEmpty())
            {
                subtract(ADDITION.getValue()); // 5 - 3 + ...
                setSubtracting(resetCalculatorOperations(isSubtracting()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setAdding(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setAdding(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isMultiplying() && !getValues()[1].isEmpty())
            {
                multiply(ADDITION.getValue()); // 5 ✕ 3 + ...
                setMultiplying(resetCalculatorOperations(isMultiplying()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setAdding(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setAdding(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (isDividing() && !getValues()[1].isEmpty())
            {
                divide(ADDITION.getValue()); // 5 ÷ 3 + ...
                setDividing(resetCalculatorOperations(isDividing()));
                if (isMinimumValue() || isMaximumValue())
                {
                    setAdding(false);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]));
                }
                else
                {
                    setAdding(true);
                    getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + " " + buttonChoice);
                }
            }
            else if (!getTextPaneWithoutNewLineCharacters().isBlank() && getValues()[0].isBlank())
            {
                LOGGER.error("The user pushed plus but there is no number");
                LOGGER.info("Setting values[0] to textPane value");
                getValues()[0] = getTextPaneWithoutNewLineCharacters();
                getTextPane().setText(addNewLineCharacters() + getValues()[0] + " " + buttonChoice);
                writeHistory(buttonChoice, true);
                setAdding(true);
                setFirstNumber(false);
                setValuesPosition(getValuesPosition() + 1);
            }
            else if (isAdding() || isSubtracting() || isMultiplying() || isDividing())
            { LOGGER.error("Already chose an operator. Choose another number."); }
            getButtonDecimal().setEnabled(true);
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
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0]) + Double.parseDouble(getValues()[1]); // create result forced double
        LOGGER.info(getValues()[0] + " " + ADDITION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            writeContinuedHistory(EQUALS.getValue(), ADDITION.getValue(), result, false);
            getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            getButtonDecimal().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            writeContinuedHistory(EQUALS.getValue(), ADDITION.getValue(), result, false);
            getButtonDecimal().setEnabled(false);
            getValues()[0] = addCommas(formatNumber(String.valueOf(result)));
        }
    }
    private void addition(String continuedOperation)
    {
        LOGGER.info("value[0]: '" + getValues()[0] + "'");
        LOGGER.info("value[1]: '" + getValues()[1] + "'");
        double result = Double.parseDouble(getValues()[0]) + Double.parseDouble(getValues()[1]); // create result forced double
        LOGGER.info(getValues()[0] + " " + ADDITION.getValue() + " " + getValues()[1] + " " + EQUALS.getValue() + " " + result);
        if (result % 1 == 0)
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.debug("We have a whole number");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, true);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
        }
        else
        {
            if (isMinimumValue(String.valueOf(result)))
            {
                LOGGER.debug("Minimum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else if (isMaximumValue(String.valueOf(result)))
            {
                LOGGER.debug("Maximum value met");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, false);
                getValues()[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                writeContinuedHistory(continuedOperation, ADDITION.getValue(), result, true);
                getValues()[0] = formatNumber(String.valueOf(result));
                getButtonDecimal().setEnabled(false);
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
        LOGGER.debug("textPane: {}", getTextPaneWithoutNewLineCharacters());
        if (textPaneContainsBadText())
        { confirm("Cannot perform " + SQUARE_ROOT + " operation"); }
        else if (getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("Cannot perform " + SQUARE_ROOT + " when textPane blank");
        }
        else if (isNegativeNumber(getValues()[getValuesPosition()]))
        {
            getTextPane().setText(addNewLineCharacters() + ERR.getValue());
            confirm("Cannot perform " + SQUARE_ROOT + " on negative number");
        }
        else
        {
            double result = Math.sqrt(Double.parseDouble(getTextPaneWithoutNewLineCharacters()));
            LOGGER.debug("result: " + result);
            if (result % 1 == 0)
            {
                getValues()[getValuesPosition()] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
                getButtonDecimal().setEnabled(true);
            }
            else
            {
                getValues()[getValuesPosition()] = formatNumber(String.valueOf(result));
                getButtonDecimal().setEnabled(false);
            }
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()]));
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
        if (getTextPaneWithoutNewLineCharacters().isEmpty())
        { confirm(CLEAR_ENTRY + " called... nothing to clear"); }
        else if (getValuesPosition() == 0 || getValues()[1].isEmpty())
        {
            getValues()[0] = BLANK.getValue();
            resetBasicOperators(false);
            setValuesPosition(0);
            setFirstNumber(true);
            getButtonDecimal().setEnabled(true);
            getTextPane().setText(BLANK.getValue());
            writeHistoryWithMessage(buttonChoice, false, " Cleared first number & main operators");
            confirm("Pressed: " + buttonChoice);
        }
        else
        {
            String operator = getActiveBasicPanelOperator();
            getValues()[1] = BLANK.getValue();
            setFirstNumber(false);
            setValuesPosition(1);
            setNumberNegative(false);
            getButtonDecimal().setEnabled(true);
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0]) + ' ' + operator);
            writeHistoryWithMessage(buttonChoice, false, " Cleared second number only");
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
        for (int i=0; i < 3; i++)
        {
            if (i == 0) { getValues()[0] = ZERO.getValue(); }
            else { getValues()[i] = BLANK.getValue(); }
        }
        for(int i=0; i < 10; i++)
        { getMemoryValues()[i] = BLANK.getValue(); }
        if (currentPanel instanceof BasicPanel)
        {
            getTextPane().setText(addNewLineCharacters() + ZERO.getValue());
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            programmerPanel.appendToPane(ZERO.getValue());
        }

        resetBasicOperators(false);
        setValuesPosition(0);
        setMemoryPosition(0);
        setFirstNumber(true);
        setNumberNegative(false);
        getButtonMemoryRecall().setEnabled(false);
        getButtonMemoryClear().setEnabled(false);
        getButtonMemoryAddition().setEnabled(false);
        getButtonMemorySubtraction().setEnabled(false);
        getButtonDecimal().setEnabled(true);
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
            getTextPane().setText(BLANK.getValue());
            confirm("Pressed " + buttonChoice);
        }
        else if (getTextPaneWithoutNewLineCharacters().isEmpty() && getValues()[0].isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("No need to perform " + DELETE + " operation");
        }
        else
        {
            if (getValues()[1].isEmpty())
            { setValuesPosition(0); } // assume they could have pressed an operator then wish to delete
            if (getValues()[0].isEmpty())
            { getValues()[0] = getTextPaneWithoutNewLineCharacters(); } // 5 + 3 = 8, values[0] = BLANK.getValue()

            LOGGER.debug("values[{}]: {}", getValuesPosition(), getValues()[getValuesPosition()]);
            LOGGER.debug("textPane: {}", getTextPaneWithoutNewLineCharacters());
            if (!isAdding() && !isSubtracting() && !isMultiplying() && !isDividing()
                    && !getTextPaneWithoutNewLineCharacters().isEmpty())
            {
                getValues()[getValuesPosition()] = getValues()[getValuesPosition()].substring(0,getValues()[getValuesPosition()].length()-1);
                if (currentPanel instanceof BasicPanel)
                { getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()])); }
                else if (currentPanel instanceof ProgrammerPanel programmerPanel)
                {
                    programmerPanel.appendToPane(addCommas(getValues()[getValuesPosition()]));
                }
            }
            else if (isAdding() || isSubtracting() || isMultiplying() || isDividing())
            {
                LOGGER.debug("An operator has been pushed");
                if (getValuesPosition() == 0)
                {
                    if (isAdding()) setAdding(false);
                    else if (isSubtracting()) setSubtracting(false);
                    else if (isMultiplying()) setMultiplying(false);
                    else /*if (isDividing())*/ setDividing(false);
                    getValues()[getValuesPosition()] = getTextPaneWithoutAnyOperator();
                    if (currentPanel instanceof BasicPanel)
                    { getTextPane().setText(addNewLineCharacters() + addCommas(getTextPaneWithoutAnyOperator())); }
                    else if (currentPanel instanceof ProgrammerPanel programmerPanel)
                    { programmerPanel.appendToPane(addCommas(getTextPaneWithoutAnyOperator())); }
                }
                else
                {
                    getValues()[getValuesPosition()] = getValues()[getValuesPosition()].substring(0,getValues()[getValuesPosition()].length()-1);
                    if (currentPanel instanceof BasicPanel)
                    { getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()])); }
                    else if (currentPanel instanceof ProgrammerPanel programmerPanel)
                    { programmerPanel.appendToPane(addCommas(getValues()[getValuesPosition()])); }
                }
            }
            getButtonDecimal().setEnabled(!isDecimal(getValues()[getValuesPosition()]));
            writeHistory(buttonChoice, false);
            confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * This method returns the String operator that was activated
     * Results could be: '+', '-', '*', '/' or '' if no
     * operator was recorded as being activated
     * @return String the basic operation that was pushed
     */
    public String getActiveBasicPanelOperator()
    {
        String results = "";
        if (isAdding()) { results = ADDITION.getValue(); }
        else if (isSubtracting()) { results = SUBTRACTION.getValue(); }
        else if (isMultiplying()) { results = MULTIPLICATION.getValue(); }
        else if (isDividing()) { results = DIVISION.getValue(); }
        LOGGER.info("operator: " + (results.isEmpty() ? "no basic operator pushed" : results));
        return results;
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
        else if (getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            getTextPane().setText(addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            confirm("No value to negate");
        }
        else if (ZERO.getValue().equals(getTextPaneWithoutNewLineCharacters()))
        {
            confirm("Cannot negate zero");
        }
        else
        {
            if (isNumberNegative())
            {
                setNumberNegative(false);
                String textToConvert = getValues()[getValuesPosition()].isBlank() ? getTextPaneWithoutNewLineCharacters() : getValues()[getValuesPosition()];
                getValues()[getValuesPosition()] = convertToPositive(textToConvert);
                writeHistory(buttonChoice, false);
            }
            else
            {
                setNumberNegative(true);
                String textToConvert = getValues()[getValuesPosition()].isBlank() ? getTextPaneWithoutNewLineCharacters() : getValues()[getValuesPosition()];
                getValues()[getValuesPosition()] = convertToNegative(textToConvert);
                writeHistory(buttonChoice, false);
            }
            if (currentPanel instanceof BasicPanel) {
                getTextPane().setText(addNewLineCharacters() + addCommas(values[valuesPosition]));
            } else if (currentPanel instanceof ProgrammerPanel programmerPanel) {
                programmerPanel.appendToPane(addCommas(values[valuesPosition]));
            }
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
        if (getValues()[getValuesPosition()].isBlank() && !isNegating())
        {
            getValues()[getValuesPosition()] = ZERO.getValue() + DECIMAL.getValue();
            getTextPane().setText(addNewLineCharacters() + getValues()[getValuesPosition()]);
        }
        else if (getValues()[getValuesPosition()].isBlank() && isNegating())
        {
            getValues()[getValuesPosition()] = SUBTRACTION.getValue() + ZERO.getValue() + DECIMAL.getValue();
            getTextPane().setText(addNewLineCharacters() + getValues()[getValuesPosition()]);
        }
        else
        {
            getValues()[getValuesPosition()] = getValues()[getValuesPosition()] + buttonChoice;
            getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[getValuesPosition()]));
        }
        getButtonDecimal().setEnabled(false); // deactivate button now that its active for this number
    }

    /**
     * The actions to perform when the Equals button is clicked
     * @param actionEvent the click action
     */
    public void performEqualsButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        String operator = getActiveBasicPanelOperator();
        if (isAdding() && getValues()[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", ADDITION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isSubtracting() && getValues()[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", SUBTRACTION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isMultiplying() && getValues()[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", MULTIPLICATION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        else if (isDividing() && getValues()[1].isBlank())
        {
            LOGGER.warn("Attempted to perform {} but values[1] is blank", DIVISION);
            confirm("Not performing " + buttonChoice);
            return;
        }
        determineAndPerformBasicCalculatorOperation();
        if (!operator.isEmpty() && !textPaneContainsBadText())
        { getTextPane().setText(addNewLineCharacters() + addCommas(getValues()[0])); }
        getValues()[0] = BLANK.getValue();
        getValues()[1] = BLANK.getValue();
        setNegating(false);
        setFirstNumber(false);
        setValuesPosition(0);
        confirm("Pushed " + buttonChoice);
    }

    /**
     * This method determines which basic operation to perform,
     * performs that operation, and resets the appropriate boolean
     */
    public void determineAndPerformBasicCalculatorOperation()
    {
        if (isMaximumValue() && (isAdding() || isMultiplying()) )
        {
            getTextPane().setText(addNewLineCharacters() + ERR.getValue());
            getValues()[0] = BLANK.getValue();
            getValues()[1] = BLANK.getValue();
            setNumberNegative(false);
            resetBasicOperators(false);
            confirm("Maximum value met");
        }
        else if (isMinimumValue() && (isSubtracting() || isDividing()))
        {
            getTextPane().setText(addNewLineCharacters() + ZERO.getValue());
            getValues()[0] = BLANK.getValue();
            getValues()[1] = BLANK.getValue();
            setNumberNegative(false);
            resetBasicOperators(false);
            confirm("Minimum value met");
        }
        else
        {
            if (isAdding())
            {
                addition();
                setAdding(resetCalculatorOperations(isAdding()));
            }
            else if (isSubtracting())
            {
                subtract();
                setSubtracting(resetCalculatorOperations(isSubtracting()));
            }
            else if (isMultiplying())
            {
                multiply();
                setMultiplying(resetCalculatorOperations(isMultiplying()));
            }
            else if (isDividing())
            {
                divide();
                setDividing(resetCalculatorOperations(isDividing()));
            }
        }
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
                    basicPanel.getBasicHistoryTextPane().setText(
                            basicPanel.getBasicHistoryTextPane().getText() +
                                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                    + " Result: " + addCommas(getValues()[getValuesPosition()]) + " " + buttonChoice
                    );
                }
                else {
                    basicPanel.getBasicHistoryTextPane().setText(
                            basicPanel.getBasicHistoryTextPane().getText() +
                                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                                    + " Result: " + addCommas(getValues()[getValuesPosition()])
                    );
                }
            }
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    programmerPanel.getProgrammerHistoryTextPane().setText(
                        programmerPanel.getProgrammerHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + " Result: " + addCommas(getValues()[getValuesPosition()]) + " " + buttonChoice
                    );
                }
                else {
                    programmerPanel.getProgrammerHistoryTextPane().setText(
                        programmerPanel.getProgrammerHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + " Result: " + addCommas(getValues()[getValuesPosition()])
                    );
                }
            }
        } else {
            if (currentPanel instanceof BasicPanel basicPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    basicPanel.getBasicHistoryTextPane().setText(
                        basicPanel.getBasicHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message + " " + buttonChoice
                    );
                }
                else {
                    basicPanel.getBasicHistoryTextPane().setText(
                        basicPanel.getBasicHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message
                    );
                }
            }
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            {
                if (addButtonChoiceToEnd)
                {
                    programmerPanel.getProgrammerHistoryTextPane().setText(
                        programmerPanel.getProgrammerHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                        + message + " " + buttonChoice
                    );
                }
                else {
                    programmerPanel.getProgrammerHistoryTextPane().setText(
                        programmerPanel.getProgrammerHistoryTextPane().getText() +
                        addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
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
                basicPanel.getBasicHistoryTextPane().setText(
                    basicPanel.getBasicHistoryTextPane().getText() +
                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(getValues()[0]) + " " + operation + " " + addCommas(getValues()[1]) + " " + EQUALS.getValue() + " " + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
                );
            } else {
                basicPanel.getBasicHistoryTextPane().setText(
                    basicPanel.getBasicHistoryTextPane().getText() +
                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(getValues()[0]) + " " + operation + " " + addCommas(getValues()[1]) + " " + EQUALS.getValue() + " " + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)))
                );
            }
        }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        {
            if (addContinuedOperationToEnd)
            {
                programmerPanel.getProgrammerHistoryTextPane().setText(
                    programmerPanel.getProgrammerHistoryTextPane().getText() +
                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(getValues()[0]) + " " + operation + " " + addCommas(getValues()[1]) + " " + EQUALS.getValue() + " " + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
                );
            } else {
                programmerPanel.getProgrammerHistoryTextPane().setText(
                    programmerPanel.getProgrammerHistoryTextPane().getText() +
                    addNewLineCharacters(1) + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
                    + " Result: " + addCommas(getValues()[0]) + " " + operation + " " + addCommas(getValues()[1]) + " " + EQUALS.getValue() + " " + addCommas(clearZeroesAndDecimalAtEnd(String.valueOf(result)))
                );
            }
        }
    }

    /**
     * The main method to set up the remaining
     * Basic button panels not in the memory panel
     */
    public void setupBasicPanelButtons()
    {
        LOGGER.debug("Configuring Basic Panel buttons...");
        List<JButton> allButtons =
                Stream.of(getAllBasicOperatorButtons(), getAllNumberButtons())
                .flatMap(Collection::stream) // Flatten into a stream of JButton objects
                .toList();
        allButtons.forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35) );
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        getButtonPercent().setName(PERCENT.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonPercent().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performPercentButtonAction(actionEvent)); }
        LOGGER.debug("Percent button configured");
        getButtonSquareRoot().setName(SQUARE_ROOT.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonSquareRoot().addActionListener(this::performSquareRootButtonAction); }
        LOGGER.debug("SquareRoot button configured");
        getButtonSquared().setName(SQUARED.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSquared().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSquaredButtonAction(actionEvent)); }
        LOGGER.debug("Delete button configured");
        getButtonFraction().setName(FRACTION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonFraction().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performFractionButtonAction(actionEvent)); }
        LOGGER.debug("Fraction button configured");
        getButtonClearEntry().setName(CLEAR_ENTRY.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonClearEntry().addActionListener(this::performClearEntryButtonAction); }
        LOGGER.debug("ClearEntry button configured");
        getButtonClear().setName(CLEAR.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonClear().addActionListener(this::performClearButtonAction); }
        LOGGER.debug("Clear button configured");
        getButtonDelete().setName(DELETE.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonDelete().addActionListener(this::performDeleteButtonAction); }
        LOGGER.debug("Delete button configured");
        getButtonDivide().setName(DIVISION.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonDivide().addActionListener(this::performDivideButtonAction); }
        LOGGER.debug("Divide button configured");
        getButtonMultiply().setName(MULTIPLICATION.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonMultiply().addActionListener(this::performMultiplicationAction); }
        LOGGER.debug("Multiply button configured");
        getButtonSubtract().setName(SUBTRACTION.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonSubtract().addActionListener(this::performSubtractionButtonAction); }
        LOGGER.debug("Subtract button configured");
        getButtonAdd().setName(ADDITION.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonAdd().addActionListener(this::performAdditionButtonAction); }
        LOGGER.debug("Addition button configured");
        getButtonNegate().setName(NEGATE.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonNegate().addActionListener(this::performNegateButtonAction); }
        LOGGER.debug("Add button configured");
        getButtonDecimal().setName(DECIMAL.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonDecimal().addActionListener(this::performDecimalButtonAction); }
        LOGGER.debug("Decimal button configured");
        getButtonEquals().setName(EQUALS.name());
        if (Arrays.asList(BASIC.getValue(),PROGRAMMER.getValue()).contains(currentPanel.getName()))
        { getButtonEquals().addActionListener(this::performEqualsButtonAction); }
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
        getButtonClearEntry().setName(CLEAR_ENTRY.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonClearEntry().addActionListener(ConverterPanel::performClearEntryButtonActions); }
        LOGGER.debug("ClearEntry button configured");
        getButtonDelete().setName(DELETE.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonDelete().addActionListener(ConverterPanel::performDeleteButtonActions); }
        LOGGER.debug("Delete button configured");
        getButtonDecimal().setName(DECIMAL.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonDecimal().addActionListener(ConverterPanel::performDecimalButtonActions); }
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
            //if (BASIC.getValue().equals(currentPanel.getName()))
            if (currentPanel instanceof BasicPanel)
            { button.addActionListener(this::performNumberButtonAction); }
            //else if (PROGRAMMER.getValue().equals(currentPanel.getName()))
            else if (currentPanel instanceof ProgrammerPanel programmerPanel)
            { button.addActionListener(programmerPanel::performProgrammerCalculatorNumberButtonActions); }
            else if (CONVERTER.getValue().equals(currentPanel.getName()))
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

    /* Calculator helper methods */

    /**
     * This method resets default values
     * Primarily used for testing purposes
     */
    public void resetValues()
    {
        getValues()[0] = BLANK.getValue();
        getValues()[1] = BLANK.getValue();
        getValues()[2] = BLANK.getValue();
        getValues()[3] = BLANK.getValue();
        LOGGER.debug("All values reset");
        setNumberNegative(false);
        LOGGER.debug("isNumberNegative set to false");
        resetBasicOperators(false);
        LOGGER.debug("All main basic operators set to false");
    }

    /**
     * Clears all actions from the number buttons
     */
    public void clearNumberButtonActions()
    {
        getAllNumberButtons().forEach(button -> Arrays.stream(button.getActionListeners()).toList().forEach(al -> {
            LOGGER.debug("Removing action listener from button: " + button.getName());
            button.removeActionListener(al);
        }));
        LOGGER.debug("Number buttons cleared of action listeners");
    }

    /**
     * This method returns true or false depending on if an operator was pushed or not
     *
     * @return the result
     */
    public boolean determineIfAnyBasicOperatorWasPushed()
    { return isAdding || isSubtracting || isMultiplying || isDividing; }

    //TODO: Rethink name. It does too much
    /**
     * Sets values[1] to be blank, updates valuesPosition accordingly,
     * updates the Dot button and boolean, resets firstNumber and returns
     * true if no operator was pushed or false otherwise
     *
     * @param operatorBool the operator to pressed
     * @return boolean the operatorBool opposite value
     */
    public boolean resetCalculatorOperations(boolean operatorBool)
    {
        LOGGER.debug("Resetting calculator operations, operatorBool:{}", operatorBool);
        if (operatorBool)
        {
            values[1] = BLANK.getValue();
            valuesPosition = 1;
            buttonDecimal.setEnabled(!isDecimal(values[0]));
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
        boolean result = true;
        for (int i = 0; i < 10; i++) {
            if (!StringUtils.isBlank(memoryValues[i])) {
                result = false;
                break;
            }
        }
        LOGGER.debug("isMemoryValuesEmpty:{}", result);
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
    public List<JButton> getAllBasicOperatorButtons()
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
        getAllBasicOperatorButtons().forEach(button ->
                Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener));
        LOGGER.debug("AllOtherBasicButtons cleared of action listeners");
    }

    /**
     * Tests whether a number has the "." symbol in it or not
     *
     * @param number the number to test
     * @return boolean if the given number contains a decimal
     */
    public boolean isDecimal(String number)
    {
        LOGGER.debug("isDecimal({}) == {}", number.replaceAll("\n", ""), number.contains(DECIMAL.getValue()));
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
        boolean checkFound = false;
        if (textPaneContainsBadText())
        {
            LOGGER.debug("TextPane contains bad text");
            checkFound = true;
        }
        else if (getTextPaneWithoutAnyOperator().equals(ZERO.getValue()) && calculatorType.equals(BASIC))
        {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            textPane.setText(BLANK.getValue());
            values[valuesPosition] = BLANK.getValue();
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        else if (ZERO.getValue().equals(values[valuesPosition]) && calculatorType.equals(PROGRAMMER))
        {
            LOGGER.debug("textPane contains 0. Setting to blank.");
            ((ProgrammerPanel)currentPanel).appendToPane(BLANK.getValue());
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
            checkFound = true;
        }
        LOGGER.debug("Initial checks result: " + checkFound);
        return checkFound;
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
        LOGGER.debug("Length: {}", values[valuesPosition].length());
        return getNumberOnLeftSideOfDecimal(values[valuesPosition]).length() >= 7 ||
               getNumberOnRightSideOfDecimal(values[valuesPosition]).length() >= 7 ;
    }

    /**
     * Tests whether the TextArea contains a String which shows a previous error
     *
     * @return boolean if textArea contains more than just a number
     */
    public boolean textPaneContainsBadText()
    {
        LOGGER.debug("TextPane is {}", getTextPaneWithoutNewLineCharacters());
        return getTextPaneWithoutAnyOperator().equals(CANNOT_DIVIDE_BY_ZERO.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(NOT_A_NUMBER.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(NUMBER_TOO_BIG.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(ENTER_A_NUMBER.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(ONLY_POSITIVES.getValue()) ||
               getTextPaneWithoutAnyOperator().contains(ERR.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(INFINITY.getValue());
    }

    /**
     * Adds commas to the number if appropriate
     * @param valueToAdjust the textPane value
     * @return String the textPane value with commas
     */
    public String addCommas(String valueToAdjust)
    {
        LOGGER.debug("Adding commas to {}", valueToAdjust);
        var temp = valueToAdjust;
        String adjusted = "";
        String toTheLeft = "";
        String toTheRight = "";
        if (isDecimal(valueToAdjust))
        {
            LOGGER.debug("temp: " + temp);
            toTheLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            toTheRight = getNumberOnRightSideOfDecimal(valueToAdjust);
            if (toTheLeft.length() <= 3)
            {
                valueToAdjust = temp;
                LOGGER.debug("valueFromTemp: " + valueToAdjust);
                getButtonDecimal().setEnabled(!isDecimal(temp));
                return valueToAdjust;
            }
            else
            {
                valueToAdjust = toTheLeft;
                getButtonDecimal().setEnabled(!isDecimal(temp));
            }
        }
        valueToAdjust = valueToAdjust.replace("_", "").replace(",","").replace(".","");
        LOGGER.debug("valueToAdjust: {}", valueToAdjust);
        if (valueToAdjust.length() >= 4)
        {
            LOGGER.debug("ValueToAdjust length: {}", valueToAdjust.length());
            StringBuffer reversed = new StringBuffer().append(valueToAdjust).reverse();
            LOGGER.debug("reversed: " + reversed);
            if (reversed.length() <= 6)
            {
                LOGGER.debug("Length is : {}", reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(COMMA.getValue()).append(reversed.substring(3,reversed.length()));
                adjusted = reversed.reverse().toString();
            }
            else
            {
                LOGGER.debug("Length is : {}", reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(COMMA.getValue()).append(reversed.substring(3,6)).append(COMMA.getValue()).append(reversed.substring(6));
                adjusted = reversed.reverse().toString();
            }
        }
        else
        {
            adjusted = valueToAdjust;
            LOGGER.debug("adjusted1: {}", adjusted);
            if (isDecimal(temp)) {
                getButtonDecimal().setEnabled(!isDecimal(temp));
                adjusted += toTheRight;
                LOGGER.debug("adjusted2: {}", adjusted);
            }

        }
        if (!getButtonDecimal().isEnabled() && isDecimal(temp))
        {
            adjusted += DECIMAL.getValue() + toTheRight;
            getButtonDecimal().setEnabled(false);
        }
        LOGGER.debug("adjustedFinal: {}", adjusted);
        return adjusted;
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
        LOGGER.debug("OS Name: {}", System.getProperty("os.name"));
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * The main actions to perform when switch panels
     * @param actionEvent the click action
     */
    public void switchPanels(ActionEvent actionEvent)
    {
        LOGGER.info("Changing view...");
        String oldPanelName = currentPanel.getClass().getSimpleName().replace("Panel", "");
        String selectedPanel = actionEvent.getActionCommand();
        LOGGER.debug("oldPanel: {}", oldPanelName);
        LOGGER.debug("newPanel: {}", selectedPanel);
        if (oldPanelName.equals(selectedPanel))
        { confirm("Not changing to " + selectedPanel + " when already showing " + oldPanelName); }
        else if (converterType != null && converterType.getValue().equals(selectedPanel))
        { confirm("Not changing panels when the conversion type is the same"); }
        else
        {
            String currentValueInTextPane = getTextPaneWithoutNewLineCharacters();
            switch (selectedPanel) {
                case "Basic":
                    BasicPanel basicPanel = new BasicPanel();
                    switchPanelsInner(basicPanel);
                    if (!values[0].isEmpty() || !currentValueInTextPane.isEmpty())
                    {
                        if (!values[0].isEmpty()) textPane.setText(addNewLineCharacters() + values[0]);
                        else {
                            if (!"Hex: 0Dec: 0Oct: 0Bin: 0".equals(currentValueInTextPane)) {
                                textPane.setText(addNewLineCharacters() + currentValueInTextPane);
                            }
                        }
                        if (isDecimal(values[0]))
                        { buttonDecimal.setEnabled(false); }
                        if (determineIfAnyBasicOperatorWasPushed())
                        { textPane.setText(textPane.getText() + EMPTY.getValue() + getActiveBasicPanelOperator()); }
                    }
                    setCalculatorType(BASIC);
                    break;
                case "Programmer":
                    ProgrammerPanel programmerPanel = new ProgrammerPanel();
                    switchPanelsInner(programmerPanel);
                    if (!values[0].isEmpty() || !currentValueInTextPane.isEmpty())
                    {
                        if (!values[0].isEmpty()) {
                            programmerPanel.appendToPane(values[0]);
                            //textPane.setText(addNewLineCharacters() + values[0]);
                        }
                        else {
                            if (!"Hex: 0Dec: 0Oct: 0Bin: 0".equals(currentValueInTextPane)) {
                                programmerPanel.appendToPane(currentValueInTextPane);
                                //textPane.setText(addNewLineCharacters() + currentValueInTextPane);
                            }
                        }
                        setCalculatorBase(BASE_DECIMAL);
                        if (isDecimal(values[0]))
                        { buttonDecimal.setEnabled(false); }
                        if (determineIfAnyBasicOperatorWasPushed())
                        { textPane.setText(textPane.getText() + EMPTY.getValue() + getActiveBasicPanelOperator()); }
                    }
                    setCalculatorType(PROGRAMMER);
                    break;
                case "Scientific":
                    LOGGER.warn("Setup");
                    setCalculatorType(SCIENTIFIC);
                    break;
                case "Date":
                    DatePanel datePanel = new DatePanel();
                    switchPanelsInner(datePanel);
                    setCalculatorType(DATE);
                    break;
                case "Angle": {
                    ConverterPanel converterPanel = new ConverterPanel();
                    setConverterType(ANGLE);
                    switchPanelsInner(converterPanel);
                    setCalculatorType(CONVERTER);
                    break;
                }
                case "Area": {
                    ConverterPanel converterPanel = new ConverterPanel();
                    setConverterType(AREA);
                    switchPanelsInner(converterPanel);
                    setCalculatorType(CONVERTER);
                    break;
                }
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

    public String convertValueToBinary()
    {
        if (values[valuesPosition].isEmpty()) return Integer.toString(0, 2);
        LOGGER.debug("Converting str(" + values[valuesPosition] + ") to " + BASE_BINARY.getValue());
        String base2Number = Integer.toBinaryString(Integer.parseInt(values[valuesPosition]));
        int base2Length = base2Number.length();
        int topLength = 0;
        if ( ((ProgrammerPanel)currentPanel).isByteByte() ) topLength = 8;
        else if ( ((ProgrammerPanel)currentPanel).isWordByte() ) topLength = 16;
        else if ( ((ProgrammerPanel)currentPanel).isDWordByte() ) topLength = 32;
        else if ( ((ProgrammerPanel)currentPanel).isQWordByte() ) topLength = 64;
        if (topLength < base2Length) topLength = topLength + 8;
        int addZeroes = topLength - base2Length;
        base2Number = "0".repeat(addZeroes) + base2Number;
        LOGGER.debug("convertFrom(" + BASE_DECIMAL.getValue() + ") To(" + BASE_BINARY.getValue() + ") = " + base2Number);
        LOGGER.info("The number " + values[valuesPosition] + " in base 10 is " + base2Number + " in base 2.");
        return base2Number;

    }

    public String convertValueToDecimal()
    {
        LOGGER.debug("this is values[valuesPosition]");
        if (values[valuesPosition].isEmpty()) return Integer.toString(0, 10);
        LOGGER.debug("Converting str(" + values[valuesPosition] + ") to " + BASE_DECIMAL.getValue());
        String base10Number = Integer.toString(Integer.parseInt(values[valuesPosition], 10), 10);
        LOGGER.debug("convertFrom(" + BASE_DECIMAL.getValue() + ") To(" + BASE_DECIMAL.getValue() + ") = " + base10Number);
        LOGGER.info("The number " + values[valuesPosition] + " in base 10 is " + base10Number + " in base 10.");
        return base10Number;
    }

    public String convertValueToOctal()
    {
        if (values[valuesPosition].isEmpty()) return Integer.toString(0, 8);
        LOGGER.debug("Converting str(" + values[valuesPosition] + ") to " + BASE_OCTAL.getValue());
        String base8Number = Integer.toOctalString(Integer.parseInt(values[valuesPosition]));
        LOGGER.debug("convertFrom(" + BASE_DECIMAL.getValue() + ") To(" + BASE_OCTAL.getValue() + ") = " + base8Number);
        LOGGER.info("The number " + values[valuesPosition] + " in base 10 is " + base8Number + " in base 8.");
        return base8Number;
    }

    public String convertValueToHexadecimal()
    {
        if (values[valuesPosition].isEmpty()) return Integer.toString(0, 16);
        LOGGER.debug("Converting str(" + values[valuesPosition] + ") to " + BASE_HEXADECIMAL.getValue());
        String base16Number = Integer.toHexString(Integer.parseInt(values[valuesPosition]));
        LOGGER.debug("convertFrom(" + BASE_DECIMAL.getValue() + ") To(" + BASE_HEXADECIMAL.getValue() + ") = " + base16Number);
        LOGGER.info("The number " + values[valuesPosition] + " in base 10 is " + base16Number + " in base 16.");
        return base16Number;
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
     * any new line characters or operator text
     * @return the plain textPane text
     */
    public String getTextPaneWithoutAnyOperator()
    {
        return getTextPaneWithoutNewLineCharacters()
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
     * any new line characters
     * @return the textPane text without new lines or whitespace
     */
    public String getTextPaneWithoutNewLineCharacters()
    { return textPane.getText().replaceAll("\n", "").strip(); }

    public String getBasicHistoryPaneWithoutNewLineCharacters()
    {
        if (currentPanel instanceof BasicPanel basicPanel)
        { return basicPanel.getBasicHistoryTextPane().getText().replaceAll("\n", "").strip(); }
        else if (currentPanel instanceof ProgrammerPanel programmerPanel)
        { return programmerPanel.getProgrammerHistoryTextPane().getText().replaceAll("\n", "").strip(); }
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
        LOGGER.info("Confirm Results: {}", message);
        LOGGER.info("---------------- ");
        switch (calculatorType) {
            case BASIC: {
                LOGGER.info("textPane: {}", getTextPaneWithoutNewLineCharacters());
                if (isMemoryValuesEmpty()) {
                    LOGGER.info("no memories stored!");
                } else {
                    LOGGER.info("memoryPosition: {}", memoryPosition);
                    LOGGER.info("memoryRecallPosition: {}", memoryRecallPosition);
                    for (int i = 0; i < 10; i++) {
                        if (!memoryValues[i].isBlank()) {
                            LOGGER.info("memoryValues[{}]: {}", i, memoryValues[i]);
                        }
                    }
                }
                LOGGER.info("addBool: {}", isAdding ? "yes" : "no");
                LOGGER.info("subBool: {}", isSubtracting ? "yes" : "no");
                LOGGER.info("mulBool: {}", isMultiplying ? "yes" : "no");
                LOGGER.info("divBool: {}", isDividing ? "yes" : "no");
                LOGGER.info("values[{}]: {}", 0, values[0]);
                LOGGER.info("values[{}]: {}", 1, values[1]);
                LOGGER.info("values[{}]: {}", 2, values[2]);
                LOGGER.info("values[{}]: {}", 3, values[3]);
                LOGGER.info("valuesPosition: {}", valuesPosition);
                LOGGER.info("firstNumBool: {}", isFirstNumber ? "yes" : "no");
                LOGGER.info("isDotEnabled: {}", isDotPressed() ? "yes" : "no");
                LOGGER.info("isNegative: {}", isNumberNegative ? "yes" : "no");
                LOGGER.info("isNegating: {}", isNegating ? "yes" : "no");
                LOGGER.info("calculatorType: {}", calculatorType);
                LOGGER.info("calculatorBase: {}", calculatorBase);
                break;
            }
            case PROGRAMMER: {
                LOGGER.info("textPane: {}", getTextPaneWithoutNewLineCharacters());
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition])) {
                    LOGGER.info("no memories stored!");
                } else {
                    LOGGER.info("memoryPosition: {}", memoryPosition);
                    LOGGER.info("memoryRecallPosition: {}", memoryRecallPosition);
                    for (int i = 0; i < 10; i++) {
                        if (!memoryValues[i].isBlank()) {
                            LOGGER.info("memoryValues[{}]: {}", i, memoryValues[i]);
                        }
                    }
                }
                LOGGER.info("addBool: {}", isAdding ? "yes" : "no");
                LOGGER.info("subBool: {}", isSubtracting ? "yes" : "no");
                LOGGER.info("mulBool: {}", isMultiplying ? "yes" : "no");
                LOGGER.info("divBool: {}", isDividing ? "yes" : "no");
//                LOGGER.info("orButtonBool: {}", ((ProgrammerPanel)currentPanel).isOrPressed() ? "yes" : "no");
//                LOGGER.info("modButtonBool: {}", ((ProgrammerPanel)currentPanel).isModulusPressed() ? "yes" : "no");
//                LOGGER.info("xorButtonBool: {}", ((ProgrammerPanel)currentPanel).isXorPressed() ? "yes" : "no");
//                LOGGER.info("notButtonBool: {}", ((ProgrammerPanel)currentPanel).isNotPressed() ? "yes" : "no");
//                LOGGER.info("andButtonBool: {}", ((ProgrammerPanel)currentPanel).isAndPressed() ? "yes" : "no");
                LOGGER.info("values[{}]: {}", 0, values[0]);
                LOGGER.info("values[{}]: {}", 1, values[1]);
                LOGGER.info("values[{}]: {}", 2, values[2]);
                LOGGER.info("values[{}]: {}", 3, values[3]);
                LOGGER.info("valuesPosition: {}", valuesPosition);
                LOGGER.info("firstNumBool: {}", isFirstNumber ? "yes" : "no");
                LOGGER.info("isDotEnabled: {}", isDotPressed() ? "yes" : "no");
                LOGGER.info("isNegative: {}", isNumberNegative ? "yes" : "no");
                LOGGER.info("calculatorByte: {}", calculatorByte);
                LOGGER.info("calculatorBase: {}", calculatorBase);
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
                LOGGER.info("Converter: {}", ((ConverterPanel) currentPanel).getConverterType());
                LOGGER.info("text field 1: {}", ((ConverterPanel) currentPanel).getTextField1().getText() + " "
                        + ((ConverterPanel) currentPanel).getUnitOptions1().getSelectedItem());
                LOGGER.info("text field 2: {}", ((ConverterPanel) currentPanel).getTextField2().getText() + " "
                        + ((ConverterPanel) currentPanel).getUnitOptions2().getSelectedItem());
                break;
            }
        }
        LOGGER.info("-------- End Confirm Results --------\n");
    }

    /**
     * Adds a specific newLinesNumber of newLine characters
     * @param newLinesNumber int the newLinesNumber of newLine characters to add
     * @return String representing newLine characters
     */
    public String addNewLineCharacters(int newLinesNumber)
    {
        String newLines = null;
        if (newLinesNumber == 0)
        {
            LOGGER.debug("Adding {} newLine characters based on panel", newLinesNumber);
            if (currentPanel instanceof BasicPanel) newLines = "\n".repeat(1);
            else if (currentPanel instanceof ProgrammerPanel) newLines = "\n".repeat(1);
            else if (currentPanel instanceof ScientificPanel) newLines = "\n".repeat(3);
        }
        else
        {
            LOGGER.debug("Adding {} newLine character", newLinesNumber);
            newLines = "\n".repeat(newLinesNumber);
        }
        return newLines;
    }

    /**
     * Adds the appropriate amount of newline characters
     * based on the currentPanel
     *
     * @return the newline character repeated 1 to n times
     */
    public String addNewLineCharacters()
    { return addNewLineCharacters(0); }

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
                .formatted(computerText, version,
                        LocalDate.now().getYear(), System.getProperty("os.name"));
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
        setCurrentPanel(newPanel);
        setupPanel();
        add(currentPanel);
        LOGGER.debug("Panel updated");
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
        LOGGER.debug("isPositiveNumber({}) == {}", number, !number.contains("-"));
        return !number.contains("-");
    }

    /**
     * Tests whether a number is negative
     *
     * @param number the value to test
     * @return Fully tested
     */
    public boolean isNegativeNumber(String number)
    {
        LOGGER.debug("isNegativeNumber({}) == {}", number, number.contains("-"));
        return number.contains("-");
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number the value to convert
     * @return Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.debug("Converting {} to negative number", number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        isNumberNegative = true;
        LOGGER.debug("Updated: {}", number);
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
        LOGGER.debug("Converting {} to positive number", number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        isNumberNegative = false;
        LOGGER.debug("Updated: {}", number);
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
        LOGGER.debug("Main basic operators reset to {}", bool);
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
            LOGGER.warn("Formatted again: " + numberToFormat);
        }
        LOGGER.debug("Formatted: " + numberToFormat);
        return numberAsStr;
    }

    /**
     * This method will return a specific CalculatorBase determined by
     * the panel set on the  If we pass in a specific base,
     * we will simply return that CalculatorBase
     *
     * @param calculatorBase the CalculatorBase to set
     * @return CalculatorBase, the base to use
     */
    public CalculatorBase determineCalculatorBase(CalculatorBase calculatorBase)
    {
        LOGGER.debug("Determining calculatorBase: {}", calculatorBase);
        if (calculatorBase != null)
        { return calculatorBase; }
        else
        {
            if (currentPanel instanceof BasicPanel) return BASE_DECIMAL;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && calculatorBase == BASE_BINARY) return BASE_BINARY;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && calculatorBase == BASE_DECIMAL) return BASE_DECIMAL;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && calculatorBase == BASE_OCTAL) return BASE_OCTAL;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && calculatorBase == BASE_HEXADECIMAL) return BASE_HEXADECIMAL;
            else if (currentPanel instanceof ScientificPanel) return BASE_BINARY;
            else if (currentPanel instanceof DatePanel) return null;
            else return BASE_DECIMAL;
        }
    }

    /**
     * Takes the value in the textArea and saves it in values[2]
     *
     * @param actionEvent the click action
     */
    public void performCopyAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        values[2] = getTextPaneWithoutNewLineCharacters();
        StringSelection selection = new StringSelection(values[2]);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        confirm("Pressed " + COPY.getValue());
    }

    /**
     * Pastes the current value in values[2] in the textArea if
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
            LOGGER.debug("Values[2]: " + values[2]);
            textPane.setText(addNewLineCharacters() + values[2]);
            values[valuesPosition] = getTextPaneWithoutNewLineCharacters();
            confirm("Pressed " + PASTE.getValue());
        }
    }

    /**
     * Clears the history for current panel
     */
    public void performClearBasicHistoryTextPaneAction(ActionEvent actionEvent)
    {
        LOGGER.info("Action for {} started", actionEvent.getActionCommand());
        if (currentPanel instanceof BasicPanel basicPanel)
        { basicPanel.getBasicHistoryTextPane().setText(BLANK.getValue()); }
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
                basicPanel.getBasicHistoryTextPane().setText(
                basicPanel.getBasicHistoryTextPane().getText() +
                addNewLineCharacters() + memoriesString
                );
            }
            else
            {
                basicPanel.getBasicHistoryTextPane().setText(
                basicPanel.getBasicHistoryTextPane().getText() +
                addNewLineCharacters() + "No Memories Stored"
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
        LOGGER.debug("Action for {} started", actionEvent.getActionCommand());
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
        LOGGER.debug("is {} minimumValue: {}", values[0], values[0].equals("0.0000001"));
        LOGGER.debug("is {} minimumValue: {}", values[1], values[1].equals("0.0000001"));
        return values[0].equals("0.0000001") ||
               values[1].equals("0.0000001"); // 10^-7
    }

    /**
     * Checks if the resulting answer has met the minimum value
     * @param valueToCheck String the value to check
     * @return boolean true if the minimum value has been met
     */
    public boolean isMinimumValue(String valueToCheck)
    {
        LOGGER.debug("is {} minimumValue: {}", valueToCheck, valueToCheck.equals("0.0000001"));
        return valueToCheck.equals("0.0000001");
    }

    /**
     * Returns true if the maximum value has
     * been met or false otherwise
     * @return boolean if maximum value has been met
     */
    public boolean isMaximumValue()
    {
        LOGGER.debug("is {} minimumValue: {}", values[0], values[0].equals("9999999"));
        LOGGER.debug("does {} minimumValue contain E: {}", values[0], values[0].contains(E.getValue()));
        LOGGER.debug("is {} minimumValue: {}", values[1], values[1].equals("9999999"));
        LOGGER.debug("does {} minimumValue contain E: {}", values[1], values[1].contains(E.getValue()));
        return values[0].equals("9999999") || values[0].contains(E.getValue()) ||
               values[1].equals("9999999") || values[1].contains(E.getValue());  // 9,999,999 or (10^8) -1
    }

    /**
     * Checks if the resulting answer has met the maximum value
     * @param valueToCheck String the value to check
     * @return boolean true if the maximum value has been met
     */
    public boolean isMaximumValue(String valueToCheck)
    {
        LOGGER.debug("is {} maximumValue: {}", valueToCheck, valueToCheck.equals("9999999"));
        LOGGER.debug("does {} minimumValue contain E: {}", valueToCheck, valueToCheck.contains(E.getValue()));
        return valueToCheck.equals("9999999") || valueToCheck.contains(E.getValue());
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
    //public JTextPane getBasicHistoryTextPane() { return basicHistoryTextPane; }
    public CalculatorType getCalculatorType() { return calculatorType; }
    public CalculatorBase getCalculatorBase() { return calculatorBase; }
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
    public boolean isNegating() { return isNegating; }
    public boolean isDotPressed() { return getButtonDecimal().isEnabled(); }
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
    //public void setBasicHistoryTextPane(JTextPane basicHistoryTextPane) { this.basicHistoryTextPane = basicHistoryTextPane; }
    public void setCalculatorType(CalculatorType calculatorType) { this.calculatorType = calculatorType; }
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculatorBase = calculatorBase; }
    public void setCalculatorByte(CalculatorByte calculatorByte) { this.calculatorByte = calculatorByte; }
    public void setDateOperation(DateOperation dateOperation) { this.dateOperation = dateOperation; }
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
    public void setCalculatorMenuBar(JMenuBar menuBar) { this.menuBar = menuBar; }
    public void setLookMenu(JMenu jMenu) { this.lookMenu = jMenu; }
    public void setViewMenu(JMenu jMenu) { this.viewMenu = jMenu; }
    public void setEditMenu(JMenu jMenu) { this.editMenu = jMenu; }
    public void setHelpMenu(JMenu jMenu) { this.helpMenu = jMenu; }
    public void setMetal(boolean isMetal) { this.isMetal = isMetal; }
    public void setSystem(boolean isSystem) { this.isSystem = isSystem; }
    public void setWindows(boolean isWindows) { this.isWindows = isWindows; }
    public void setMotif(boolean isMotif) { this.isMotif = isMotif; }
    public void setGtk(boolean isGtk) { this.isGtk = isGtk; }
    public void setApple(boolean isApple) { this.isApple = isApple; }
}