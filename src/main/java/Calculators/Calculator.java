package Calculators;

import Panels.*;
import Runnables.CalculatorMain;
import Types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
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
    // Values used to store input
    protected String[]
            values = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}; // firstNum or total, secondNum, copy, temporary storage
    protected String[] memoryValues = new String[]{BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue(),BLANK.getValue()}; // stores memory values; rolls over after 10 entries
    private int valuesPosition = 0, memoryPosition = 0, memoryRecallPosition = 0;
    private JTextPane textPane, basicHistoryTextPane;

    private CalculatorType calculatorType;
    private CalculatorBase calculatorBase;
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
    { this(calculatorType, null, null, null); }

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
        setCalculatorBase(determineCalculatorBase(calculatorBase)); // update
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(Color.WHITE);
                    basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
                }
                // TODO: add more history panes here
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(Color.WHITE);
                    basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
                }
                // TODO: add more history panes here
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(Color.WHITE);
                    basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
                }
                // TODO: add more history panes here
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(new Color(174,178,195));
                    basicHistoryTextPane.setBorder(new LineBorder(Color.GRAY, 1, true));
                }
                // TODO: add more history panes here
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(Color.WHITE);
                    basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
                }
                // TODO: add more history panes here
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
                if (null != basicHistoryTextPane)
                {
                    basicHistoryTextPane.setBackground(Color.WHITE);
                    basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
                }
                // TODO: add more history panes here
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
        JMenuItem areaConverter = new JMenuItem(AREA.getValue()); // The converterMenu is an "item" which is a menu of more choices
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
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        setTextPane(new JTextPane());
        getTextPane().setParagraphAttributes(attribs, true);
        getTextPane().setFont(mainFont);
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
        getTextPane().setPreferredSize(new Dimension(70, 30));
        LOGGER.debug("TextPane configured");
    }

    /**
     * The main method to set up the Basic History Pane
     */
    public void setupBasicHistoryTextPane()
    {
        LOGGER.debug("Configuring BasicHistoryTextPane...");
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        basicHistoryTextPane = new JTextPane();
        basicHistoryTextPane.setParagraphAttributes(attribs, true);
        basicHistoryTextPane.setFont(mainFont);
        if (isMotif())
        {
            basicHistoryTextPane.setBackground(new Color(174,178,195));
            basicHistoryTextPane.setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        {
            basicHistoryTextPane.setBackground(Color.WHITE);
            basicHistoryTextPane.setBorder(new LineBorder(Color.BLACK));
        }
        basicHistoryTextPane.setEditable(false);
        basicHistoryTextPane.setSize(new Dimension(70, 200)); // sets size at start
        basicHistoryTextPane.setMinimumSize(basicHistoryTextPane.getSize()); // keeps size throughout
        LOGGER.debug("BasicHistoryTextPane configured");
    }

    /**
     * The main method to set up the Memory buttons
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
        getButtonMemoryRecall().setName(MEMORY_RECALL.getValue());
        getButtonMemoryAddition().setName(MEMORY_ADDITION.getValue());
        getButtonMemorySubtraction().setName(MEMORY_SUBTRACTION.getValue());
        getButtonMemoryStore().setEnabled(true); // Enable memoryStore
        getButtonMemoryStore().setName(MEMORY_STORE.getValue());
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
        LOGGER.warn("Memory buttons only configured for BASIC Panel");
        if (BASIC.getValue().equals(currentPanel.getName()))
        {
            getButtonMemoryStore().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryStoreAction(actionEvent));
            getButtonMemoryRecall().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryRecallAction(actionEvent));
            getButtonMemoryClear().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryClearAction(actionEvent));
            getButtonMemoryAddition().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryAdditionAction(actionEvent));
            getButtonMemorySubtraction().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemorySubtractionAction(actionEvent));
            getButtonHistory().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performHistoryAction(actionEvent));
        }
        LOGGER.debug("Memory buttons configured");
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
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSquareRoot().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSquareRootButtonAction(actionEvent)); }
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
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonClearEntry().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performClearEntryButtonAction(actionEvent)); }
        LOGGER.debug("ClearEntry button configured");
        getButtonClear().setName(CLEAR.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonClear().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performClearButtonAction(actionEvent)); }
        LOGGER.debug("Clear button configured");
        getButtonDelete().setName(DELETE.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDelete().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDeleteButtonAction(actionEvent)); }
        LOGGER.debug("Delete button configured");
        getButtonDivide().setName(DIVISION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDivide().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDivideButtonAction(actionEvent)); }
        LOGGER.debug("Divide button configured");
        getButtonMultiply().setName(MULTIPLICATION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonMultiply().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMultiplicationAction(actionEvent)); }
        LOGGER.debug("Multiply button configured");
        getButtonSubtract().setName(SUBTRACTION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSubtract().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSubtractionButtonAction(actionEvent)); }
        LOGGER.debug("Subtract button configured");
        getButtonAdd().setName(ADDITION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonAdd().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performAdditionButtonAction(actionEvent)); }
        LOGGER.debug("Addition button configured");
        getButtonNegate().setName(NEGATE.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonNegate().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performNegateButtonAction(actionEvent)); }
        LOGGER.debug("Add button configured");
        getButtonDecimal().setName(DECIMAL.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDecimal().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDecimalButtonAction(actionEvent)); }
        LOGGER.debug("Decimal button configured");
        getButtonEquals().setName(EQUALS.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonEquals().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performEqualsButtonAction(actionEvent)); }
        LOGGER.debug("Equals button configured");
        LOGGER.debug("Basic Panel buttons configured");
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
            if (BASIC.getValue().equals(currentPanel.getName()))
            { button.addActionListener(actionEvent -> ((BasicPanel)currentPanel).performNumberButtonAction(actionEvent)); }
            else if (PROGRAMMER.getValue().equals(currentPanel.getName()))
            { button.addActionListener(ProgrammerPanel::performProgrammerCalculatorNumberButtonActions); }
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
        else if (getTextPaneWithoutAnyOperator().equals(ZERO.getValue()) &&
                (calculatorType.equals(BASIC) ||
                        (calculatorType == PROGRAMMER && calculatorBase == BASE_DECIMAL)))
        {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            textPane.setText(BLANK.getValue());
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
               getTextPaneWithoutAnyOperator().contains(E.getValue()) ||
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
            switch (selectedPanel) {
                case "Basic":
                    BasicPanel basicPanel = new BasicPanel();
                    switchPanelsInner(basicPanel);
                    if (!values[0].isEmpty())
                    {
                        textPane.setText(addNewLineCharacters() + values[0]);
                        if (isDecimal(values[0]))
                        { buttonDecimal.setEnabled(false); }
                    }
                    break;
                case "Programmer":
                    ProgrammerPanel programmerPanel = new ProgrammerPanel();
                    switchPanelsInner(programmerPanel);
                    if (!values[0].isEmpty())
                    {
                        textPane.setText(addNewLineCharacters() + values[0]);
                        programmerPanel.setCalculatorBase(BASE_DECIMAL);
                        if (isDecimal(values[0]))
                        { buttonDecimal.setEnabled(false); }
                    }
                    break;
                case "Scientific":
                    LOGGER.warn("Setup");
                    break;
                case "Date":
                    DatePanel datePanel = new DatePanel();
                    switchPanelsInner(datePanel);
                    break;
                case "Angle": {
                    ConverterPanel converterPanel = new ConverterPanel();
                    setConverterType(ANGLE);
                    switchPanelsInner(converterPanel);
                    break;
                }
                case "Area": {
                    ConverterPanel converterPanel = new ConverterPanel();
                    setConverterType(AREA);
                    switchPanelsInner(converterPanel);
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
        setPreferredSize(currentPanel.getSize());
        setMinimumSize(currentPanel.getSize());
        if (DATE.equals(getCalculatorType()))
        { setResizable(false); }
        else
        { setResizable(true); }
        pack();
    }

    // TODO: Rework
    /**
     * Converts the current value from one CalculatorBase
     * to another CalculatorBase
     * @param fromType the current CalculatorBase
     * @param toType the CalculatorBase to convert to
     * @param currentValue the value to convert
     * @return String the converted value
     * @throws CalculatorError throws a conversion error
     */
    public String convertFromTypeToTypeOnValues(CalculatorBase fromType, CalculatorBase toType, String currentValue) throws CalculatorError
    {
        LOGGER.debug("convert from {} to {}", fromType, toType);
        LOGGER.debug("on value: {}", currentValue);
        StringBuffer sb = new StringBuffer();
        if (currentValue.contains(" ")) {
            String[] strs = currentValue.split(" ");
            for (String s : strs) {
                sb.append(s);
            }
        } else {
            sb.append(currentValue);
        }
        LOGGER.debug("sb: " + sb);
        String convertedValue = "";
        if (StringUtils.isEmpty(currentValue)) return "";
        // All from HEXADECIMAL to any other option
        if (fromType == BASE_HEXADECIMAL && toType == BASE_DECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_HEXADECIMAL && toType == BASE_OCTAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_HEXADECIMAL && toType == BASE_BINARY) {
            confirm("IMPLEMENT");
        }
        // All from BASE_DECIMAL to any other option
        else if (fromType == BASE_DECIMAL && toType == BASE_HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_DECIMAL && toType == BASE_OCTAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_DECIMAL && toType == BASE_BINARY)
        {
            LOGGER.debug("Converting str(" + sb + ")");
            sb = new StringBuffer();
            int number;
            try {
                number = Integer.parseInt(currentValue);
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
                logException(nfe);
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
            LOGGER.debug("Before reverse: {}", sb);
            // End adding zeroes here

            sb.reverse();
            LOGGER.debug("After reverse: {}", sb);
            String strToReturn = sb.toString();
            LOGGER.debug("convertFrom(" + fromType + ")To(" + toType + ") = " + sb);
            LOGGER.warn("ADD CODE THAT MAKES SURE RETURNED VALUE UPDATES BYTES IF AFTER REVERSE IS LONGER THAN 8 BITS, WE NEED TO ADD A SPACE BETWEEN THE BYTES");
            convertedValue = strToReturn;
        }
        // All from OCTAL to any other option
        else if (fromType == BASE_OCTAL && toType == BASE_HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_OCTAL && toType == BASE_DECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_OCTAL && toType == BASE_BINARY) {
            confirm("IMPLEMENT");
        }
        // All from BINARY to any other option
        else if (fromType == BASE_BINARY && toType == BASE_HEXADECIMAL) {
            confirm("IMPLEMENT");
        }
        else if (fromType == BASE_BINARY && toType == BASE_DECIMAL)
        {
            LOGGER.debug("BINARY TO DECIMAL: {}", sb);
            int appropriateLength = ((ProgrammerPanel)currentPanel).getBytes();
            LOGGER.debug("sb: " + sb);
            LOGGER.debug("appropriateLength: " + appropriateLength);
            if (sb.length() != appropriateLength) {
                LOGGER.warn("sb, '" + sb + "', is too short. adding missing zeroes");
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
            if (isPositiveNumber(convertedValue))
            { convertedValue = String.valueOf(clearZeroesAndDecimalAtEnd(convertedValue)); }
        }
        else if (fromType == BASE_BINARY && toType == BASE_OCTAL) {
            confirm("IMPLEMENT");
        }

        LOGGER.debug("Converted value: {}", convertedValue);
        LOGGER.info("Base set to: {}", calculatorBase);
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
        LOGGER.debug("ClearZeroesAndDot: {}", currentNumber);
        if (currentNumber.contains(E.getValue())) return currentNumber;
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
               .replace(RoL.getValue(), BLANK.getValue())
               .replace(RoR.getValue(), BLANK.getValue())
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
    { return basicHistoryTextPane.getText().replaceAll("\n", "").strip(); }

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
                LOGGER.info("orButtonBool: {}", ((ProgrammerPanel)currentPanel).isOrPressed() ? "yes" : "no");
                LOGGER.info("modButtonBool: {}", ((ProgrammerPanel)currentPanel).isModulusPressed() ? "yes" : "no");
                LOGGER.info("xorButtonBool: {}", ((ProgrammerPanel)currentPanel).isXorPressed() ? "yes" : "no");
                LOGGER.info("notButtonBool: {}", ((ProgrammerPanel)currentPanel).isNotPressed() ? "yes" : "no");
                LOGGER.info("andButtonBool: {}", ((ProgrammerPanel)currentPanel).isAndPressed() ? "yes" : "no");
                LOGGER.info("values[{}]: {}", 0, values[0]);
                LOGGER.info("values[{}]: {}", 1, values[1]);
                LOGGER.info("values[{}]: {}", 2, values[2]);
                LOGGER.info("values[{}]: {}", 3, values[3]);
                LOGGER.info("valuesPosition: {}", valuesPosition);
                LOGGER.info("firstNumBool: {}", isFirstNumber ? "yes" : "no");
                LOGGER.info("isDotEnabled: {}", isDotPressed() ? "yes" : "no");
                LOGGER.info("isNegative: {}", isNumberNegative ? "yes" : "no");
                LOGGER.info("calculatorType: {}", calculatorType);
                LOGGER.info("calculatorBase: {}", calculatorBase);
                LOGGER.info("calculatorByteWord: {}", ((ProgrammerPanel)currentPanel).getByteWord());
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
            LOGGER.debug("Adding {} newLine characters", newLinesNumber);
            if (currentPanel instanceof BasicPanel) newLines = "\n".repeat(1);
            else if (currentPanel instanceof ProgrammerPanel) newLines = "\n".repeat(3);
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
        LOGGER.debug("Updating to panel {}...", currentPanel.getClass().getSimpleName());
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
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && programmerPanel.isBinaryBase()) return BASE_BINARY;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && programmerPanel.isDecimalBase()) return BASE_DECIMAL;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && programmerPanel.isOctalBase()) return BASE_OCTAL;
            else if (currentPanel instanceof ProgrammerPanel programmerPanel && programmerPanel.isHexadecimalBase()) return BASE_HEXADECIMAL;
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
        if (currentPanel instanceof BasicPanel)
        { basicHistoryTextPane.setText(BLANK.getValue()); }
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
                    if ((i+1) < memoryValues.length && !memoryValues[i+1].equals(BLANK.getValue()))
                    { memoriesString.append(", "); }
                }
                basicHistoryTextPane.setText(
                basicHistoryTextPane.getText() +
                addNewLineCharacters() + memoriesString
                );
            }
            else
            {
                basicHistoryTextPane.setText(
                basicHistoryTextPane.getText() +
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
    public JTextPane getBasicHistoryTextPane() { return basicHistoryTextPane; }
    public CalculatorType getCalculatorType() { return calculatorType; }
    public CalculatorBase getCalculatorBase() { return calculatorBase; }
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
    public void setBasicHistoryTextPane(JTextPane basicHistoryTextPane) { this.basicHistoryTextPane = basicHistoryTextPane; }
    public void setCalculatorType(CalculatorType calculatorType) { this.calculatorType = calculatorType; }
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
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculatorBase = calculatorBase; }
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