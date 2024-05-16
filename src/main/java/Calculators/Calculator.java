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
import java.util.concurrent.atomic.AtomicInteger;

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
        LOGGER.info("Panel added to calculator");
        setCalculatorBase(determineCalculatorBase(calculatorBase)); // update
        setMinimumSize(currentPanel.getSize());
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator");
    }

    /* Start of methods here */

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
        setupLookMenu(new JMenu(LOOK.getValue()));
        setupViewMenu(new JMenu(VIEW.getValue()));
        setupEditMenu(new JMenu(EDIT.getValue()));
        setupHelpMenu(new JMenu(HELP.getValue()));
        LOGGER.info("menuBar created");
    }

    /**
     * The main operations to perform to set up
     * the Look Menu item
     * @param lookMenu the look menu to configure
     */
    private void setupLookMenu(JMenu lookMenu)
    {
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
    }

    /**
     * The main operations to perform to set up
     * the View Menu item
     * @param viewMenu the view menu to configure
     */
    private void setupViewMenu(JMenu viewMenu)
    {
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
        JMenuItem angleConverter = new JMenuItem(ANGLE.getName());
        angleConverter.setFont(mainFont);
        angleConverter.setName(ANGLE.getName());
        angleConverter.addActionListener(this::switchPanels);
        JMenuItem areaConverter = new JMenuItem(AREA.getName()); // The converterMenu is an "item" which is a menu of more choices
        areaConverter.setFont(mainFont);
        areaConverter.setName(AREA.getName());
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
    }

    /**
     * The main operations to perform to set up
     * the Edit Menu item
     * @param editMenu the edit menu to configure
     */
    private void setupEditMenu(JMenu editMenu)
    {
        JMenuItem copyItem = new JMenuItem(COPY.getValue());
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.setFont(mainFont);
        copyItem.setName(COPY.getValue());
        copyItem.addActionListener(this::performCopyFunctionality);

        JMenuItem pasteItem = new JMenuItem(PASTE.getValue());
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setFont(mainFont);
        pasteItem.setName(PASTE.getValue());
        pasteItem.addActionListener(this::performPasteFunctionality);

        JMenuItem clearHistoryItem = new JMenuItem(CLEAR_HISTORY.getValue());
        clearHistoryItem.setFont(mainFont);
        clearHistoryItem.setName(CLEAR_HISTORY.getValue());
        clearHistoryItem.addActionListener(this::performClearHistoryActions);

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
        menuBar.add(editMenu); // add editMenu to menu bar
    }

    /**
     * The main operations to perform to set up
     * the Help Menu item
     * @param helpMenu the help menu to configure
     */
    private void setupHelpMenu(JMenu helpMenu)
    {
        JMenuItem viewHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();
        helpMenu.add(viewHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        helpMenu.setFont(mainFont);
        helpMenu.setName(HELP.getValue());
        setHelpMenu(helpMenu);
        menuBar.add(helpMenu); // add helpMenu to menu bar
    }

    /**
     * Sets the images used in the Calculator
     */
    private void setupCalculatorImages()
    {
        LOGGER.info("Creating images...");
        setImageIcons();
        LOGGER.info("Creating images completed");
        final Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(calculatorIcon.getImage()); // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        setIconImage(calculatorIcon.getImage());
        LOGGER.info("Taskbar icon set");
    }

    /**
     * Sets the image icons
     */
    private void setImageIcons()
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
    private ImageIcon createImageIcon(String path)
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
//        switch (currentPanel)
//        {
//            case BASIC -> ((BasicPanel)currentPanel).setupBasicPanel(this);
//            case "Programmer" -> ((ProgrammerPanel)currentPanel).setupProgrammerPanel(this, calculatorBase);
//            //case "Scientific" -> ((ScientificPanel)currentPanel).setupScientificPanel(this, calculatorBase);
//            case "Date" -> ((DatePanel)currentPanel).setupDatePanel(this, dateOperation);
//            case "Converter" -> ((ConverterPanel)currentPanel).setupConverterPanel(this, converterType);
//        }
    }

    /**
     * This method creates the View Help menu option under Help
     * The help text is added by the currentPanel.
     */
    public JMenuItem createViewHelpJMenuItem()
    {
        JMenuItem viewHelpItem = new JMenuItem(VIEW_HELP.getValue());
        viewHelpItem.setName(VIEW_HELP.getValue());
        viewHelpItem.setFont(mainFont);
        return viewHelpItem;
    }

    /**
     * This method creates the About Calculator menu option under Help
     */
    public JMenuItem createAboutCalculatorJMenuItem()
    {
        JMenuItem aboutCalculatorItem = new JMenuItem(ABOUT_CALCULATOR.getValue());
        aboutCalculatorItem.setName(ABOUT_CALCULATOR.getValue());
        aboutCalculatorItem.setFont(mainFont);
        aboutCalculatorItem.addActionListener(this::performAboutCalculatorFunctionality);
        return aboutCalculatorItem;
    }

    /**
     * The main method to set up the textPane
     */
    public void setupTextPane()
    {
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
        LOGGER.info("TextPane configured");
    }

    /**
     * The main method to set up the Basic History Pane
     */
    public void setupBasicHistoryTextPane()
    {
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
        LOGGER.info("BasicHistoryTextPane configured");
    }

    //TODO: Rework, add cases like 5 + 3 +... to show (+) Result: 5 + 3 = 8 +
    //TODO: Currently force updating history pane to get this to work
    /**
     * Updates the history pane for the basic Calculator
     * @param buttonChoice String the button choice
     */
    public void updateBasicHistoryPane(String buttonChoice, String valueToAppend)
    {
        // TODO: Add conditions for operators, functions, and memory buttons
        if (valueToAppend.equals(BLANK.getValue()))
        {
            getBasicHistoryTextPane().setText(
            getBasicHistoryTextPane().getText() +
            addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
            + " Result: " + addCourtesyCommas(values[valuesPosition])
            );
        }
        else /*if (!valueToAppend.isBlank()) */
        {
            if (EQUALS.getValue().equals(buttonChoice))
            {
                getBasicHistoryTextPane().setText(
                getBasicHistoryTextPane().getText() +
                addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                + " Result: " + valueToAppend
                );
            }
            else if (ADDITION.getValue().equals(buttonChoice))
            {
                getBasicHistoryTextPane().setText(
                getBasicHistoryTextPane().getText() +
                addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                + " Result: " + addCourtesyCommas(values[0]) + " " + buttonChoice
                );
            }
            else
            {
                getBasicHistoryTextPane().setText(
                getBasicHistoryTextPane().getText() +
                addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
                + " Result: " + addCourtesyCommas(values[0]) + " " + buttonChoice
                );
            }
        }
    }

    /**
     * The main method to set up the Memory buttons
     */
    public void setupMemoryButtons()
    {
        getAllMemoryButtons().forEach(memoryButton -> {
            memoryButton.setFont(mainFont);
            memoryButton.setPreferredSize(new Dimension(30, 30));
            memoryButton.setBorder(new LineBorder(Color.BLACK));
            memoryButton.setEnabled(false);
        });
        getButtonMemoryClear().setName(MEMORY_CLEAR.getValue());
        getButtonMemoryRecall().setName(MEMORY_RECALL.getValue());
        getButtonMemoryAddition().setName(MEMORY_ADDITION.getValue());
        getButtonMemorySubtraction().setName(MEMORY_SUBTRACTION.getValue());
        getButtonMemoryStore().setEnabled(true); // Enable memoryStore
        getButtonMemoryStore().setName(MEMORY_STORE.getValue());
        getButtonHistory().setEnabled(true);
        getButtonHistory().setName(HISTORY_CLOSED.getValue());
//      reset buttons to enabled if memories are saved
//        if (!getMemoryValues()[0].isEmpty())
//        {
//            getButtonMemoryClear().setEnabled(true);
//            getButtonMemoryRecall().setEnabled(true);
//            getButtonMemoryAddition().setEnabled(true);
//            getButtonMemorySubtraction().setEnabled(true);
//        }
        if (BASIC.getValue().equals(currentPanel.getName()))
        {
            getButtonMemoryStore().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryStoreActions(actionEvent));
            getButtonMemoryRecall().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryRecallActions(actionEvent));
            getButtonMemoryClear().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryClearActions(actionEvent));
            getButtonMemoryAddition().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemoryAdditionActions(actionEvent));
            getButtonMemorySubtraction().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMemorySubtractionActions(actionEvent));
            getButtonHistory().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performHistoryActions(actionEvent));
        }
        LOGGER.info("Memory buttons configured");
    }

    /**
     * The main method to set up the remaining
     * Basic button panels
     */
    public void setupRemainingBasicButtons()
    {
        Collection<JButton> allBasicOperatorButtons = new ArrayList<>(getAllBasicOperatorButtons());
        Collection<JButton> allNumberButtons = new ArrayList<>(getAllNumberButtons());
        allBasicOperatorButtons.addAll(allNumberButtons);
        getAllBasicOperatorButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35) );
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        getButtonPercent().setName(PERCENT.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonPercent().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performPercentButtonActions(actionEvent)); }
        LOGGER.info("Percent button configured");
        getButtonSquareRoot().setName(SQUARE_ROOT.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSquareRoot().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSquareRootButtonActions(actionEvent)); }
        LOGGER.info("SquareRoot button configured");
        getButtonSquared().setName(SQUARED.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSquared().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSquaredButtonActions(actionEvent)); }
        LOGGER.info("Delete button configured");
        getButtonFraction().setName(FRACTION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonFraction().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performFractionButtonActions(actionEvent)); }
        LOGGER.info("Fraction button configured");
        getButtonClearEntry().setName(CLEAR_ENTRY.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonClearEntry().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performClearEntryButtonActions(actionEvent)); }
        LOGGER.info("ClearEntry button configured");
        getButtonClear().setName(CLEAR.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonClear().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performClearButtonActions(actionEvent)); }
        LOGGER.info("Clear button configured");
        getButtonDelete().setName(DELETE.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDelete().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDeleteButtonActions(actionEvent)); }
        LOGGER.info("Delete button configured");
        getButtonDivide().setName(DIVISION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDivide().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDivideButtonActions(actionEvent)); }
        LOGGER.info("Divide button configured");
        getButtonMultiply().setName(MULTIPLICATION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonMultiply().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performMultiplicationActions(actionEvent)); }
        LOGGER.info("Multiply button configured");
        getButtonSubtract().setName(SUBTRACTION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonSubtract().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performSubtractionButtonActions(actionEvent)); }
        LOGGER.info("Subtract button configured");
        getButtonAdd().setName(ADDITION.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonAdd().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performAdditionButtonActions(actionEvent)); }
        LOGGER.info("Addition button configured");
        getButtonNegate().setName(NEGATE.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonNegate().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performNegateButtonActions(actionEvent)); }
        LOGGER.info("Add button configured");
        getButtonDecimal().setName(DECIMAL.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonDecimal().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performDecimalButtonActions(actionEvent)); }
        LOGGER.info("Decimal button configured");
        getButtonEquals().setName(EQUALS.name());
        if (BASIC.getValue().equals(currentPanel.getName()))
        { getButtonEquals().addActionListener(actionEvent -> ((BasicPanel)currentPanel).performEqualsButtonActions(actionEvent)); }
        LOGGER.info("Equals button configured");
    }

    /**
     * The main method to set up the buttons
     * used on the Converter panel
     */
    public void setupConverterButtons()
    {
        Arrays.asList(buttonBlank1, buttonBlank2, buttonClearEntry, buttonDelete, buttonDecimal).forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonBlank1.setName(BLANK.name());
        LOGGER.info("Blank button 1 configured");
        buttonBlank2.setName(BLANK.name());
        LOGGER.info("Blank button 2 configured");
        getButtonClearEntry().setName(CLEAR_ENTRY.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonClearEntry().addActionListener(ConverterPanel::performClearEntryButtonActions); }
        LOGGER.info("ClearEntry button configured");
        getButtonDelete().setName(DELETE.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonDelete().addActionListener(ConverterPanel::performDeleteButtonActions); }
        LOGGER.info("Delete button configured");
        getButtonDecimal().setName(DECIMAL.name());
        if (CONVERTER.getValue().equals(currentPanel.getName()))
        { getButtonDecimal().addActionListener(ConverterPanel::performDecimalButtonActions); }
        LOGGER.info("Decimal button configured");
    }

    /**
     * The main method to set up all number buttons, 0-9
     */
    public void setupNumberButtons()
    {
        AtomicInteger i = new AtomicInteger(0);
        getNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(true);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            if (BASIC.getValue().equals(currentPanel.getName()))
            { button.addActionListener(actionEvent -> ((BasicPanel)currentPanel).performNumberButtonActions(actionEvent)); }
            else if (PROGRAMMER.getValue().equals(currentPanel.getName()))
            { button.addActionListener(ProgrammerPanel::performProgrammerCalculatorNumberButtonActions); }
            else if (CONVERTER.getValue().equals(currentPanel.getName()))
            { button.addActionListener(ConverterPanel::performNumberButtonActions); }
            else
            { LOGGER.info("Add other options here"); }
        });
        LOGGER.info("Number buttons configured");
    }

    /**
     * The main method to set up the Blank1 button
     */
    public void setupButtonBlank1()
    {
        buttonBlank1.setFont(mainFont);
        buttonBlank1.setPreferredSize(new Dimension(35, 35));
        buttonBlank1.setBorder(new LineBorder(Color.BLACK));
        buttonBlank1.setEnabled(true);
        buttonBlank1.setName(BLANK.name());
        LOGGER.info("Blank button 1 configured");
    }

    /**
     * The main method to set up the Blank2 button
     */
    public void setupButtonBlank2()
    {
        buttonBlank2.setFont(mainFont);
        buttonBlank2.setPreferredSize(new Dimension(35, 35));
        buttonBlank2.setBorder(new LineBorder(Color.BLACK));
        buttonBlank2.setEnabled(true);
        buttonBlank2.setName(BLANK.name());
        LOGGER.info("Blank button 2 configured");
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
        setNumberNegative(false);
        resetBasicOperators(false);
    }

    /**
     * Clears all actions from the number buttons
     */
    public void clearNumberButtonActions()
    {
        LOGGER.debug("Number buttons cleared of action listeners");
        getNumberButtons().forEach(button -> Arrays.stream(button.getActionListeners()).toList().forEach(al -> {
            LOGGER.debug("Removing action listener from button: " + button.getName());
            button.removeActionListener(al);
        }));
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
        LOGGER.debug("resetting operator...");
        if (operatorBool)
        {
            values[1] = BLANK.getValue();
            valuesPosition = 1;
            if (isDecimal(values[0]))
            { buttonDecimal.setEnabled(false); }
            else
            { buttonDecimal.setEnabled(true); }
            isFirstNumber = false;
            return false;
        }
        else
        {
            values[1] = BLANK.getValue();
            valuesPosition = 0;
            if (isDecimal(values[0]))
            { buttonDecimal.setEnabled(false); }
            else
            { buttonDecimal.setEnabled(true); }
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
        return result;
    }

    /**
     * Returns the lowest position in memory that contains a value
     */
    public int getLowestMemoryPosition()
    {
        var lowestMemory = 0;
        for (int i = 0; i < 10; i++) {
            if (!StringUtils.isBlank(memoryValues[i])) {
                lowestMemory = i;
                break;
            }
        }
        LOGGER.info("Lowest position in memory is {}", lowestMemory);
        return lowestMemory;
    }

    /**
     * Returns all the basicPanel main operator buttons
     * @return Collection of all basicPanel main operators
     */
    public Collection<JButton> getAllBasicPanelOperatorButtons()
    { return Arrays.asList(buttonAdd, buttonSubtract, buttonMultiply, buttonDivide); }

    /**
     * Returns all the number buttons
     * @return Collection of all number buttons
     */
    public Collection<JButton> getAllNumberButtons()
    { return Arrays.asList(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9); }

    /**
     * Returns all the memory buttons
     * @return Collection of all memory buttons
     */
    public Collection<JButton> getAllMemoryButtons()
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
    public Collection<JButton> getAllBasicOperatorButtons()
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
    }

    /**
     * Tests whether a number has the "." symbol in it or not
     *
     * @param number the number to test
     * @return boolean if the given number contains a decimal
     */
    public boolean isDecimal(String number)
    {
        LOGGER.debug("isDecimal(" + number.replaceAll("\n", "") + ") == " + number.contains(DECIMAL.getValue()));
        return number.contains(DECIMAL.getValue());
    }

    /**
     * When we hit a number button this method is called
     * to ensure valid entries are allowed and any previous
     * errors or unexpected conditions are cleared
     */
    public boolean performInitialChecks()
    {
        LOGGER.info("Performing initial checks...");
        boolean checkFound = false;
        if (textPaneContainsBadText())
        { checkFound = true; }
        else if (getTextPaneWithoutAnyOperator().equals(ZERO.getValue()) &&
                (calculatorType.equals(BASIC) ||
                        (calculatorType == PROGRAMMER && calculatorBase == BASE_DECIMAL)))
        {
            LOGGER.debug("textArea equals 0 no matter the CalculatorType. setting to blank.");
            textPane.setText(BLANK.getValue());
            values[valuesPosition] = BLANK.getValue();
            isFirstNumber = true;
            buttonDecimal.setEnabled(true);
        }
        else if (StringUtils.isBlank(values[0]) && StringUtils.isNotBlank(values[1]))
        {
            values[0] = values[1];
            values[1] = BLANK.getValue();
            valuesPosition = 0;
            checkFound = true;
        }
        LOGGER.info("Initial checks result: " + checkFound);
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
        LOGGER.debug("Length: " + values[valuesPosition].length());
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
        return getTextPaneWithoutAnyOperator().equals(CANNOT_DIVIDE_BY_ZERO.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(NOT_A_NUMBER.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(NUMBER_TOO_BIG.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(ENTER_A_NUMBER.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(ONLY_POSITIVES.getValue()) ||
               getTextPaneWithoutAnyOperator().contains(E.getValue()) ||
               getTextPaneWithoutAnyOperator().equals(INFINITY.getValue());
    }

    /**
     * This method appends the button choice to the values array
     * at the current valuesPosition and then sets the textPane
     * to the new value
     * @param buttonChoice the chosen button
     */
    public void updateValuesAtPositionThenUpdateTextPane(String buttonChoice)
    {
        values[valuesPosition] = values[valuesPosition] + buttonChoice; // store in values, values[valuesPosition] + buttonChoice
        if (isNegating)
        {
            values[valuesPosition] = convertToNegative(values[valuesPosition]);
            textPane.setText(addNewLineCharacters() + addCourtesyCommas(values[valuesPosition])); //values[valuesPosition]
            setNegating(isSubtracting && isNumberNegative);
        }
        else
        { textPane.setText(addNewLineCharacters() + addCourtesyCommas(values[valuesPosition])); } //values[valuesPosition])
        LOGGER.debug("textPane: '{}'", getTextPaneWithoutNewLineCharacters());
        LOGGER.debug("values[{}]: {}'",valuesPosition,values[valuesPosition]);
    }

    /**
     * Adds commas to the number if appropriate
     * @param valueToAdjust the textPane value
     * @return String the textPane value with commas
     */
    public String addCourtesyCommas(String valueToAdjust)
    {
        LOGGER.info("addingCourtesyCommas to {}", valueToAdjust);
        //LOGGER.debug("valueToAdjust: " + valueToAdjust);
        var temp = valueToAdjust;
        String adjusted = "";
        String toTheLeft = "";
        String toTheRight = "";
        if (isDecimal(valueToAdjust))
        {
            //LOGGER.debug("temp: " + temp);
            toTheLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            toTheRight = getNumberOnRightSideOfDecimal(valueToAdjust);
            if (toTheLeft.length() <= 3)
            {
                valueToAdjust = temp;
                //LOGGER.debug("valueFromTemp: " + valueToAdjust);
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
        //LOGGER.debug("valueToAdjust: " + valueToAdjust);
        if (valueToAdjust.length() >= 4)
        {
            //LOGGER.debug("ValueToAdjust length: " + valueToAdjust.length());
            StringBuffer reversed = new StringBuffer().append(valueToAdjust).reverse();
            //LOGGER.debug("reversed: " + reversed);
            if (reversed.length() <= 6)
            {
                //LOGGER.debug("Length is : " + reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(COMMA.getValue()).append(reversed.substring(3,reversed.length()));
                adjusted = reversed.reverse().toString();
            }
            else
            {
                //LOGGER.debug("Length is : " + reversed.length());
                reversed = new StringBuffer().append(reversed.substring(0,3)).append(COMMA.getValue()).append(reversed.substring(3,6)).append(COMMA.getValue()).append(reversed.substring(6));
                adjusted = reversed.reverse().toString();
            }
        }
        else
        {
            adjusted = valueToAdjust;
            //LOGGER.debug("'adjusted' = valueToAdjust: " + adjusted);
            if (isDecimal(temp)) {
                getButtonDecimal().setEnabled(!isDecimal(temp));
                adjusted += toTheRight;
                //LOGGER.debug("'adjusted' = valueToAdjust: " + adjusted);
            }
//            else
//            { LOGGER.debug("'adjusted' = valueToAdjust: " + adjusted); }
            //LOGGER.debug("'adjusted': " + adjusted);
        }
        if (!getButtonDecimal().isEnabled() && isDecimal(temp))
        {
            adjusted += DECIMAL.getValue() + toTheRight;
            getButtonDecimal().setEnabled(false);
        }
        LOGGER.debug("adjusted: " + adjusted);
        return adjusted;
    }

    /**
     * Returns all the number buttons
     * @return Collection of number buttons
     */
    public Collection<JButton> getNumberButtons()
    {
        return Arrays.asList(button0, button1, button2, button3, button4,
                button5, button6, button7, button8, button9);
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

    /**
     * The main actions to perform when switch panels
     * @param actionEvent the click action
     */
    public void switchPanels(ActionEvent actionEvent)
    {
        LOGGER.info("Starting to changing jPanels");
        String oldPanelName = currentPanel.getClass().getSimpleName().replace("Panel", "");
        String selectedPanel = actionEvent.getActionCommand();
        LOGGER.info("oldPanel: {}", oldPanelName);
        LOGGER.info("newPanel: {}", selectedPanel);
        if (oldPanelName.equals(selectedPanel))
        { confirm("Not changing to " + selectedPanel + " when already showing " + oldPanelName); }
        else if (converterType != null && converterType.getName().equals(selectedPanel))
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
            LOGGER.info("Finished changing jPanels\n");
            confirm("Switched from " + oldPanelName + " to " + currentPanel.getClass().getSimpleName());
        }
    }

    /**
     * The inner logic to perform when switching panels
     * @param newPanel the new panel to switch to
     */
    private void switchPanelsInner(JPanel newPanel)
    {
        LOGGER.info("Performing switchPanelsInner...");
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
        LOGGER.info("sb: " + sb);
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
            if (isPositiveNumber(convertedValue))
            { convertedValue = String.valueOf(clearZeroesAndDecimalAtEnd(convertedValue)); }
            LOGGER.debug("convertedValue: {}", convertedValue);
        }
        else if (fromType == BASE_BINARY && toType == BASE_OCTAL) {
            confirm("IMPLEMENT");
        }

        LOGGER.info("base set to: {}", calculatorBase);
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
        LOGGER.info("ClearZeroesAndDot: {}", currentNumber);
        if (currentNumber.contains(E.getValue())) return currentNumber;
        int index;
        index = currentNumber.indexOf(DECIMAL.getValue());
        LOGGER.debug("index: " + index);
        if (index == -1) return currentNumber;
        else
        { currentNumber = currentNumber.substring(0, index); }
        LOGGER.info("ClearZeroesAndDot Result: {}", addCourtesyCommas(currentNumber));
        return currentNumber;
    }

    /**
     * Returns the text in the textPane without
     * any new line characters or operator text
     * @return the plain textPane text
     */
    public String getTextPaneWithoutAnyOperator()
    {
        return getTextPaneWithoutNewLineCharactersOrWhiteSpace()
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
     * any new line characters or white space
     * @return the textPane text without new lines or whitespace
     */
    public String getTextPaneWithoutNewLineCharactersOrWhiteSpace()
    {
        return getTextPaneWithoutNewLineCharacters()
               .strip();
    }

    /**
     * Returns the text in the textPane without
     * any new line characters
     * @return the textPane text without new lines or whitespace
     */
    public String getTextPaneWithoutNewLineCharacters()
    { return textPane.getText().replaceAll("\n", "").strip(); }

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
                LOGGER.info("textPane: '{}'", getTextPaneWithoutNewLineCharacters());
                if (isMemoryValuesEmpty()) {
                    LOGGER.info("no memories stored!");
                } else {
                    LOGGER.info("memoryPosition: '{}'", memoryPosition);
                    LOGGER.info("memoryRecallPosition: '{}'", memoryRecallPosition);
                    for (int i = 0; i < 10; i++) {
                        if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues[{}] : {}", i, memoryValues[i]);
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
                LOGGER.info("isDotEnabled: '{}'", getButtonDecimal().isEnabled());
                LOGGER.info("isNegative: '{}'", isNumberNegative);
                LOGGER.info("isNegating: '{}'", isNegating);
                LOGGER.info("calculatorType: '{}", calculatorType);
                LOGGER.info("calculatorBase: '{}'", calculatorBase);
                break;
            }
            case PROGRAMMER: {
                LOGGER.info("textPane: '{}'", getTextPaneWithoutNewLineCharacters());
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
                LOGGER.info("isDotEnabled: '{}'", getButtonDecimal().isEnabled());
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

    /**
     * Adds new lines based on the given input
     * @param number the number of '\n' characters to add
     */
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

    /**
     * Returns the text to display in About Calculator
     * @return String the About Calculator text
     */
    public String getAboutCalculatorString()
    {
        LOGGER.info("Setting " + ABOUT_CALCULATOR.getValue() + " text...");
        String computerText, version = "";
        if (isMacOperatingSystem()) { computerText = "Apple"; }
        else                        { computerText = "Windows"; }
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
     * This method updates the panel removes the old panel,
     * sets up the new panel, and adds it to the frame
     *
     * @param newPanel the panel to update on the Calculator
     * @return the old panel
     */
    //TODO: Determine if necessary or change to void return @SuppressWarnings("all")
    public JPanel updateJPanel(JPanel newPanel)
    {
        JPanel oldPanel = currentPanel;
        remove(oldPanel);
        setCurrentPanel(newPanel);
        setupPanel();
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
        LOGGER.info("isPositiveNumber(" + number + ") == " + !number.contains("-"));
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
        LOGGER.info("isNegativeNumber(" + number + ") == " + number.contains("-"));
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
    public String getNumberOnLeftSideOfDecimal(String currentNumber)
    {
        String leftSide;
        if (!isDecimal(currentNumber)) return currentNumber;
        int index = currentNumber.indexOf(".");
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = "";
        else
        {
            leftSide = currentNumber.substring(0, index);
            if (StringUtils.isEmpty(leftSide)) leftSide = ZERO.getValue();
        }
        //LOGGER.debug("number to the left of the decimal: '{}'", leftSide);
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
        //LOGGER.debug("number to the right of the decimal: '{}'", rightSide);
        return rightSide;
    }

    /**
     * Formats the number based on the length
     * @param num the number to format
     * @return String representation of the formatted number
     */
    public String formatNumber(String num)
    {
        DecimalFormat df = null;
        LOGGER.info("Number to format: " + num);
        if (!isNumberNegative)
        {
            if (num.length() <= 2) df = new DecimalFormat("0.00");
            if (num.length() >= 3) df = new DecimalFormat("0.000");
        }
        else
        {
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
     * the panel set on the  If we pass in a specific base,
     * we will simply return that CalculatorBase
     *
     * @param calculatorBase the CalculatorBase to set
     * @return CalculatorBase, the base to use
     */
    public CalculatorBase determineCalculatorBase(CalculatorBase calculatorBase)
    {
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
    public void performCopyFunctionality(ActionEvent actionEvent)
    {
        values[2] = getTextPaneWithoutNewLineCharactersOrWhiteSpace();
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
    public void performPasteFunctionality(ActionEvent actionEvent)
    {
        if (values[2].isEmpty())
        { confirm("Values[2] is empty. Nothing to paste"); }
        else
        {
            LOGGER.info("Values[2]: " + values[2]);
            textPane.setText(addNewLineCharacters() + values[2]);
            values[valuesPosition] = getTextPaneWithoutNewLineCharactersOrWhiteSpace();
            confirm("Pressed " + PASTE.getValue());
        }
    }

    /**
     * Clears the history for current panel
     */
    public void performClearHistoryActions(ActionEvent actionEvent)
    {
        if (currentPanel instanceof BasicPanel)
        { basicHistoryTextPane.setText(""); }
    }

    // TODO: Move memories into Panel
    /**
     * Displays all the memory values in the history panel
     */
    public void performShowMemoriesAction(ActionEvent actionEvent)
    {
        if (currentPanel instanceof BasicPanel && !isMemoryValuesEmpty())
        { basicHistoryTextPane.setText(
                basicHistoryTextPane.getText() +
                addNewLineCharacters() + "Memories: [" + memoryValues[0] + "], [" + memoryValues[1] + "], [" +
                memoryValues[2] + "], [" + memoryValues[3] + "], [" + memoryValues[4] + "], [" + memoryValues[5] + "], [" +
                memoryValues[6] + "], [" + memoryValues[7] + "], [" + memoryValues[8] + "], [" + memoryValues[9] + "]"
        );
        }
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
    }

    /**
     * Returns true if the minimum value has
     * been met or false otherwise
     * @return boolean is minimum value has been met
     */
    public boolean isMinimumValue()
    {
        return values[0].equals("0.0000001") ||
               values[1].equals("0.0000001"); // 10^-7
    }

    /**
     * Returns true if the maximum value has
     * been met or false otherwise
     * @return boolean if maximum value has been met
     */
    public boolean isMaximumValue()
    {
        return values[0].equals("9999999") || values[0].contains(E.getValue()) ||
               values[1].equals("9999999") || values[1].contains(E.getValue());  // 9,999,999 or (10^8) -1
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