package version4;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static version4.CalculatorType_v4.*;
import static version4.CalculatorBase_v4.*;
import static version4.ConverterType_v4.ANGLE;
import static version4.ConverterType_v4.AREA;
import static version4.JPanelDate_v4.OPTIONS1;

public class Calculator_v4 extends JFrame
{
    final static protected Logger LOGGER;
    static
    {
        LOGGER = LogManager.getLogger(Calculator_v4.class);
    }

    final static private long serialVersionUID = 1L;
    final static Font font = new Font("Segoe UI", Font.PLAIN, 12);
    final static Font font_Bold = new Font("Segoe UI", Font.BOLD, 12);
    final static Font font2 = new Font("Verdana", Font.BOLD, 20);
    /*
     * All calculators have the same:
     * layout and constraints
     * buttons 0-9
     * clear button
     * clear entry button
     * delete button
     * dot button
     * memory clear button
     * memory recall button
     * memory store button
     * memory addition button
     * memory subtract button
     */
    private GridBagLayout layout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    final private JButton button0 = new JButton("0");
    final protected JButton button1 = new JButton("1");
    final protected JButton button2 = new JButton("2");
    final protected JButton button3 = new JButton("3");
    final protected JButton button4 = new JButton("4");
    final protected JButton button5 = new JButton("5");
    final protected JButton button6 = new JButton("6");
    final protected JButton button7 = new JButton("7");
    final protected JButton button8 = new JButton("8");
    final protected JButton button9 = new JButton("9");
    final protected JButton buttonClear = new JButton("C");
    final protected JButton buttonClearEntry = new JButton("CE");
    final protected JButton buttonDelete = new JButton("\u2190"); //"<--"
    final protected JButton buttonDot = new JButton(".");
    final protected JButton buttonFraction = new JButton("1/x");
    final protected JButton buttonPercent = new JButton("%");
    final protected JButton buttonSqrt = new JButton("\u221A");
    final protected JButton buttonMemoryClear = new JButton("MC");
    final protected JButton buttonMemoryRecall = new JButton("MR");
    final protected JButton buttonMemoryStore = new JButton("MS");
    final protected JButton buttonMemoryAddition = new JButton("M+");
    final protected JButton buttonMemorySubtraction = new JButton("M-");
    final protected JButton buttonAdd = new JButton("+");
    final protected JButton buttonSubtract = new JButton("-");
    final protected JButton buttonMultiply = new JButton("*");
    final protected JButton buttonDivide = new JButton("/");
    final protected JButton buttonEquals = new JButton("=");
    final protected JButton buttonNegate = new JButton("\u00B1");

    protected String[] values = {"","","",""}; // firstNum or total, secondNum, copy/paste, temporary storage. memory values now in MemorySuite.getMemoryValues()
    protected int valuesPosition = 0;
    protected String[] memoryValues = new String[]{"","","","","","","","","",""}; // stores memory values; rolls over after 10 entries
    protected int memoryPosition = 0;
    protected int memoryRecallPosition = 0;
    protected JTextArea textArea = new JTextArea(2,5); // rows, columns
    protected StringBuffer textareaValue = new StringBuffer(); // String representing appropriate visual of number
    protected CalculatorType_v4 calcType = null;
    protected JPanel currentPanel = null;
    protected ImageIcon calculatorImage1, calculator2, macLogo, windowsLogo, blankImage;
    protected JLabel iconLabel;
    protected JLabel textLabel;
    protected JMenuBar bar;

    protected boolean firstNumBool = true;
    protected boolean numberIsNegative = false;
    protected boolean memorySwitchBool = false;
    protected boolean addBool = false;
    protected boolean subBool = false;
    protected boolean mulBool = false;
    protected boolean divBool = false;
    protected boolean memAddBool = false;
    protected boolean memSubBool = false;
    protected boolean negatePressed = false;
    protected boolean dotButtonPressed = false;
    // programmer related fields
    boolean isButtonBinSet = true;
    boolean isButtonOctSet = false;
    boolean isButtonDecSet = false;
    boolean isButtonHexSet = false;

    boolean isButtonByteSet = true;
    boolean isButtonWordSet = false;
    boolean isButtonDwordSet = false;
    boolean isButtonQwordSet = false;
    protected boolean orButtonBool = false;
    protected boolean modButtonBool = false;
    protected boolean xorButtonBool = false;
    protected boolean negateButtonBool = false;
    protected boolean notButtonBool = false;
    protected boolean andButtonBool = false;
    protected CalculatorBase_v4 base = DECIMAL;

    public Calculator_v4() throws Exception { this(BASIC, null, null); }
    /**
     * This constructor is used to create a calculator with a specific panel
     * @param calcType the type of calculator to create. sets the title
     */
    public Calculator_v4(CalculatorType_v4 calcType) throws Exception { this(calcType, null, null); }
    /**
     * This constructor is used to start up a Programmer Calculator in a specific mode
     * @param calcType
     * @param base
     */
    public Calculator_v4(CalculatorType_v4 calcType, CalculatorBase_v4 base)
    {
        super(calcType.getName()); // default title is Basic
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setCalculatorType(calcType);
        setupMenuBar(); // setup here for all types
        setupCalculatorImages();
        try { setCurrentPanel(new JPanelProgrammer_v4(this, base)); }
        catch (CalculatorError_v4 c) { logException(c); }
        add(getCurrentPanel());
        LOGGER.info("Panel added to calculator");
        setMaximumSize(getCurrentPanel().getSize());
        pack();
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator\n");
    }
    /**
     * This constructor is used to create a calculator with a specific converter panel
     * @param calcType the type of calculator to create. sets the title
     */
    public Calculator_v4(CalculatorType_v4 calcType, String chosenOption) throws Exception { this(calcType, null, chosenOption); }
    /**
     *
     * @param calcType
     * @param converterType
     * @throws Exception
     */
    public Calculator_v4(CalculatorType_v4 calcType, ConverterType_v4 converterType) throws Exception
    { this(calcType, converterType, null); }
    /**
     * MAIN CONSTRUCTOR USED
     * @param calcType
     * @param converterType
     * @param chosenOption
     */
    public Calculator_v4(CalculatorType_v4 calcType, ConverterType_v4 converterType, String chosenOption)  throws CalculatorError_v4, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calcType.getName()); // default title is Basic
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setCalculatorType(calcType);
        setupMenuBar(); // setup here for all types
        setupCalculatorImages();
        if (converterType == null && chosenOption == null) setCurrentPanel(determinePanelType(calcType));
        else if (converterType != null) setCurrentPanel(determinePanelType(calcType, converterType, null));
        else if (chosenOption != null)  setCurrentPanel(determinePanelType(calcType, null, chosenOption));
        add(getCurrentPanel());
        LOGGER.info("Panel added to calculator");
        setMaximumSize(getCurrentPanel().getSize());
        pack();
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LOGGER.info("Finished constructing the calculator\n");
    }

    /******************** Start of methods here **********************/
    public void setupTextArea()
    {
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setFont(Calculator_v4.font);
        textArea.setEditable(false);
        if (getCalculatorType() == BASIC) textArea.setPreferredSize(new Dimension(70, 30));
        else if (getCalculatorType() == PROGRAMMER) {}
        else {}
        LOGGER.info("Text Area configured");
    }

    /**
     * Handles the logic for setting up a Calculator_v4. Should
     * run once. Anything that needs to be reset, should be reset
     * where appropriate.
     */
    public void setupCalculatorImages()
    {
        setImageIcons();
        // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        Application.getApplication().setDockIconImage(getCalculator2().getImage());
        setIconImage(getCalculator2().getImage());
        LOGGER.info("All images set for Calculator");
    }

    public void setupAddButton()
    {
        getButtonAdd().setFont(font);
        getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        getButtonAdd().setEnabled(true);
        getButtonAdd().addActionListener(this::performAdditionButtonActions);
        LOGGER.info("Add button configured");
    }

    public void setupSubtractButton()
    {
        getButtonSubtract().setFont(font);
        getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        getButtonSubtract().setEnabled(true);
        getButtonSubtract().addActionListener(action -> {
            performSubtractionButtonActions(action);
        });
    }

    public void setupMultiplyButton()
    {
        getButtonMultiply().setFont(font);
        getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        getButtonMultiply().setEnabled(true);
        getButtonMultiply().addActionListener(action -> {
            performMultiplicationActions(action);
        });
        LOGGER.info("Multiply button configured");
    }

    public void setupDivideButton()
    {
        getButtonDivide().setFont(font);
        getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        getButtonDivide().setEnabled(true);
        getButtonDivide().addActionListener(action -> {
            performDivideButtonActions(action);
        });
        LOGGER.info("Divide button configured");
    }

    public JPanel determinePanelType(CalculatorType_v4 calcType) throws ParseException, CalculatorError_v4
    { return determinePanelType(calcType, null, null); }

    public JPanel determinePanelType(CalculatorType_v4 calcType, ConverterType_v4 converterType, String chosenOption) throws ParseException, CalculatorError_v4
    {
        if (calcType == null) {
            LOGGER.error("CalcType cannot be null");
            throw new CalculatorError_v4("CalcType cannot be null");
        }
        if (calcType == BASIC) {
            return new JPanelBasic_v4(this);
        } else if (calcType == PROGRAMMER) {
            return new JPanelProgrammer_v4(this);
        } else if (calcType == SCIENTIFIC) {
            return new JPanelScientific_v4();
        } else if (calcType == DATE) {
            return new JPanelDate_v4(this, chosenOption);
        }
        else //if (calcType == CONVERTER) {
        {
            if (converterType == ANGLE)
            {
                return new JPanelConverter_v4(this, ANGLE);
            }
            else if (converterType == AREA)
            {
                return new JPanelConverter_v4(this, AREA);
            }
            else
            {
                LOGGER.error("Add the specific converter panel now");
                throw new CalculatorError_v4("Add the specific converter panel now");
            }
        }
    }

    public void setupFractionButton()
    {
        getButtonFraction().setFont(Calculator_v4.font);
        getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        getButtonFraction().setEnabled(true);
    }

    public void setupPercentButton()
    {
        getButtonPercent().setFont(Calculator_v4.font);
        getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        getButtonPercent().setEnabled(true);
    }

    public void setupSquareRootButton()
    {
        getButtonSqrt().setFont(Calculator_v4.font);
        getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        getButtonSqrt().setEnabled(true);
    }

    public void setupEqualsButton()
    {
        getButtonEquals().setFont(font);
        getButtonEquals().setPreferredSize(new Dimension(35, 70) );
        getButtonEquals().setBorder(new LineBorder(Color.BLACK));
        getButtonEquals().setEnabled(true);
        getButtonEquals().addActionListener(action -> {
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
        getButtonNegate().setFont(font);
        getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        getButtonNegate().setEnabled(true);
        getButtonNegate().addActionListener(action -> {
            performNegateButtonActions(action);
        });
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
            button.setFont(Calculator_v4.font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
        });
        LOGGER.info("Number buttons configured");
    }

    public void setupMenuBar()
    {
        LOGGER.info("Setting up the default menu bar");
        // Menu Bar and Menu Choices and each menu options
        setBar(new JMenuBar());
        setJMenuBar(getBar()); // add menu bar to application

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
        } // add morer options if using Windows
        getBar().add(lookMenu);

        // View menu options, the converterMenu is an option which is a menu of more choices
        JMenuItem basic = new JMenuItem(CalculatorType_v4.BASIC.getName());
        JMenuItem programmer = new JMenuItem(CalculatorType_v4.PROGRAMMER.getName());
        JMenuItem dates = new JMenuItem(CalculatorType_v4.DATE.getName());
        JMenu converterMenu = new JMenu(CONVERTER.getName());

        // commonalities
        basic.setFont(font);
        basic.setName(BASIC.getName());
        basic.addActionListener(action -> {
            try
            {
                JPanel panel = new JPanelBasic_v4(this);
                performTasksWhenChangingJPanels(panel, BASIC);
            }
            catch (CalculatorError_v4 e)
            {
                LOGGER.error("Couldn't change to JPanelBasic_v4 because {}", e.getMessage());
            }
        });

        programmer.setFont(font);
        programmer.setName(PROGRAMMER.getName());
        programmer.addActionListener(action -> {
            try
            {
                JPanel panel = null;
                if (!textareaValue.equals("")) panel = new JPanelProgrammer_v4(this, DECIMAL);
                else panel = new JPanelProgrammer_v4(this);
                performTasksWhenChangingJPanels(panel, PROGRAMMER);
            }
            catch (CalculatorError_v4 e)
            {
                LOGGER.error("Couldn't change to JPanelProgrammer_v4 because {}", e.getMessage());
            }
        });

        dates.setFont(font);
        dates.setName(DATE.getName());
        dates.addActionListener(action -> {
            try
            {
                JPanel panel = new  JPanelDate_v4(this);
                performTasksWhenChangingJPanels(panel, DATE);
            }
            catch (ParseException | CalculatorError_v4 e)
            {
                LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
            }
        });

        converterMenu.setFont(font);
        converterMenu.setName(CONVERTER.getName());
        // options for converterMenu
        JMenuItem angleConverter = new JMenuItem(ANGLE.getName());
        JMenuItem areaConverter = new JMenuItem(ConverterType_v4.AREA.getName());

        // commonalities
        angleConverter.setFont(font);
        areaConverter.setFont(font);

        // functions
        angleConverter.addActionListener(action -> {
            try
            {
                //setCurrentPanel(new JPanelConverter_v4());
                JPanel panel = new JPanelConverter_v4(this, ANGLE);
                performTasksWhenChangingJPanels(panel, CONVERTER, ANGLE);
            }
            catch (ParseException | CalculatorError_v4 e)
            {
                LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
            }
        });
        areaConverter.addActionListener(action -> {
            try
            {
                //setCurrentPanel(new JPanelConverter_v4());
                JPanel panel = new JPanelConverter_v4(this, AREA);
                performTasksWhenChangingJPanels(panel, CONVERTER, AREA);
            }
            catch (ParseException | CalculatorError_v4 e)
            {
                LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
            }
        });

        // add JMenuItems to converterMenu
        converterMenu.add(angleConverter);
        converterMenu.add(areaConverter);

        // add view options to viewMenu
        viewMenu.add(basic);
        viewMenu.add(programmer);
        viewMenu.add(dates);
        viewMenu.addSeparator();
        viewMenu.add(converterMenu);
        // END NEW OPTION: Converter
        getBar().add(viewMenu); // add viewMenu to menu bar

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
        getBar().add(editMenu); // add editMenu to menu bar

        // Help Options
        JMenuItem viewHelpItem = createViewHelpJMenuItem();
        JMenuItem aboutCalculatorItem = createAboutCalculatorJMenuItem();

        helpMenu.add(viewHelpItem, 0);
        helpMenu.addSeparator();
        helpMenu.add(aboutCalculatorItem, 2);
        getBar().add(helpMenu); // add helpMenu to menu bar
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
        getButtonDelete().setFont(font);
        getButtonDelete().setPreferredSize(new Dimension(35, 35));
        getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        getButtonDelete().setEnabled(true);
        getButtonDelete().setName("\u2190");
        getButtonDelete().addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }

    public void setupClearEntryButton()
    {
        getButtonClearEntry().setFont(font);
        getButtonClearEntry().setPreferredSize(new Dimension(35, 35));
        getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        getButtonClearEntry().setEnabled(true);
        getButtonClearEntry().setName("CE");
        getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }

    public void setupClearButton()
    {
        getButtonClear().setFont(font);
        getButtonClear().setPreferredSize(new Dimension(35, 35));
        getButtonClear().setBorder(new LineBorder(Color.BLACK));
        getButtonClear().setEnabled(true);
        getButtonClear().setName("C");
        getButtonClear().addActionListener(action -> {
            performClearButtonActions(action);
            if (getCalculatorType() == CalculatorType_v4.PROGRAMMER)
            {
                ((JPanelProgrammer_v4) getCurrentPanel()).resetProgrammerOperators();
            }
        });
        LOGGER.info("Clear button configured");
    }

    public void setupDotButton()
    {
        getButtonDot().setFont(font);
        getButtonDot().setPreferredSize(new Dimension(35, 35));
        getButtonDot().setBorder(new LineBorder(Color.BLACK));
        getButtonDot().setEnabled(true);
        getButtonDot().setName(".");
        getButtonDot().addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }

	public void setupMemoryButtons()
    {
        buttonMemoryStore.setFont(Calculator_v4.font);
        buttonMemoryStore.setPreferredSize(new Dimension(35, 35));
        buttonMemoryStore.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryStore.setEnabled(true);
        buttonMemoryStore.setName("MS");
        buttonMemoryStore.addActionListener(this::performMemoryStoreActions);
        LOGGER.info("Memory Store button configured");
        getButtonMemoryClear().setFont(Calculator_v4.font);
        getButtonMemoryClear().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryClear().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryClear().setEnabled(false);
        getButtonMemoryClear().setName("MC");
        getButtonMemoryClear().addActionListener(this::performMemoryClearActions);
        LOGGER.info("Memory Clear button configured");
        getButtonMemoryRecall().setFont(Calculator_v4.font);
        getButtonMemoryRecall().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryRecall().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryRecall().setEnabled(false);
        getButtonMemoryRecall().setName("MR");
        getButtonMemoryRecall().addActionListener(this::performMemoryRecallActions);
        LOGGER.info("Memory Recall button configured");
        getButtonMemoryAddition().setFont(Calculator_v4.font);
        getButtonMemoryAddition().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryAddition().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryAddition().setEnabled(false);
        getButtonMemoryAddition().setName("M+");
        getButtonMemoryAddition().addActionListener(this::performMemoryAddActions);
        LOGGER.info("Memory Add button configured");
        getButtonMemorySubtraction().setFont(Calculator_v4.font);
        getButtonMemorySubtraction().setPreferredSize(new Dimension(35, 35));
        getButtonMemorySubtraction().setBorder(new LineBorder(Color.BLACK));
        getButtonMemorySubtraction().setEnabled(false);
        getButtonMemorySubtraction().setName("M-");
        getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionActions);
        LOGGER.info("Memory Subtract button configured");
        // reset buttons to enabled if memories are saved
        if (!getMemoryValues()[0].equals(""))
        {
            getButtonMemoryClear().setEnabled(true);
            getButtonMemoryRecall().setEnabled(true);
            getButtonMemoryAddition().setEnabled(true);
            getButtonMemorySubtraction().setEnabled(true);
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
                (getCalculatorType().equals(BASIC) ||
                (getCalculatorType() == PROGRAMMER && getCalculatorBase() == DECIMAL)))
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

    public void performNumberButtonActions(String buttonChoice)
    {
        performInitialChecks();
        //getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        //updateTextareaFromTextArea();
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

    public void setEnabledForAllNumberButtons(boolean isEnabled)
    {
        getNumberButtons().forEach(button -> button.setEnabled(isEnabled));
    }
    
	/**
     * Adds the components to the container
     *
     * @param c
     * @param row
     * @param column
     * @param width
     * @param height
     */
    public void addComponent(Component c, int row, int column, int width, int height)
    {
        getConstraints().gridx = column;
        getConstraints().gridy = row;
        getConstraints().gridwidth = width;
        getConstraints().gridheight = height;
        getLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
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
            getButtonMemoryAddition().setEnabled(true);
            getButtonMemorySubtraction().setEnabled(true);
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
        if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + memoryValues[memoryRecallPosition]);
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
            LOGGER.info("Clearing memoryValue["+memoryPosition+"] = " + getMemoryValues()[getMemoryPosition()]);
            memoryValues[memoryPosition] = "";
            memoryPosition++;
            // MemorySuite now could potentially be empty
            if (isMemoryValuesEmpty())
            {
                memoryPosition = 0;
                memoryRecallPosition = 0;
                buttonMemoryClear.setEnabled(false);
                buttonMemoryRecall.setEnabled(false);
                getButtonMemoryAddition().setEnabled(false);
                getButtonMemorySubtraction().setEnabled(false);
                getTextArea().setText(addNewLineCharacters(1)+"0");
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
                        getTextArea().setText(addNewLineCharacters(1)+memoryValues[alpha]);
                    }
                    else
                    {
                        getTextArea().setText(addNewLineCharacters(1)+"0");

                    }
                    setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                    break;
                }
                // Move the first found value to the first position
                // and so on until the end
                String[] newMemoryValues = new String[10];
                for(int i = 0; i < alpha; i++)
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
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            getTextArea().setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
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
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAndDecimalAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            getTextArea().setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
            setTextareaValue(new StringBuffer(getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    /**
     * When the user clicks the Clear button, everything in the
     * calculator returns to initial start mode.
     * @param action
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
        if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1)+"0");
        else if (getCalculatorType() == PROGRAMMER) {
            textArea.setText(addNewLineCharacters(3)+"0");
            resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((JPanelProgrammer_v4)getCurrentPanel()).resetProgrammerOperators();
            ((JPanelProgrammer_v4)getCurrentPanel()).getButtonByte().setSelected(true);
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
        if (getCalculatorType() == PROGRAMMER) {
            resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((JPanelProgrammer_v4)getCurrentPanel()).resetProgrammerOperators();
            ((JPanelProgrammer_v4)getCurrentPanel()).getButtonByte().setSelected(true);
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
            //addBool = false;
            //subBool = true;
            //mulBool = true;
            //divBool = true;
            valuesPosition = 0;
        } // assume they just previously pressed an operator

        LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
        LOGGER.info("textarea: " + textareaValue);
        numberIsNegative = isNegativeNumber(values[valuesPosition]);
        // this check has to happen
        dotButtonPressed = isDecimal(textareaValue.toString());
        getTextArea().setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        updateTextareaFromTextArea();
        if (addBool == false && subBool == false && mulBool == false && divBool == false)
        {
            if (!numberIsNegative)
            {
                // if no operator has been pushed; number is positive; number is whole
                if (!isDotButtonPressed())
                {
                    if (textareaValue.length() == 1)
                    { // ex: 5
                        getTextArea().setText("");
                        textareaValue = new StringBuffer().append(" ");
                        values[valuesPosition] = "";
                    }
                    else if (textareaValue.length() >= 2)
                    { // ex: 56 or 2346
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                        if (getCalculatorType() == BASIC) getTextArea().setText(addNewLineCharacters(1)+ textareaValue);
                        else if (getCalculatorType() == PROGRAMMER) getTextArea().setText(addNewLineCharacters(3)+ textareaValue);
                        values[valuesPosition] = textareaValue.toString();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else if (isDotButtonPressed()) {
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
                    if (getCalculatorType() == BASIC) getTextArea().setText(addNewLineCharacters(1)+ textareaValue);
                    else if (getCalculatorType() == PROGRAMMER) getTextArea().setText(addNewLineCharacters(3)+ textareaValue);
                    values[valuesPosition] = textareaValue.toString();
                }
            }
            else if (numberIsNegative) {
                // if no operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
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
                else if (dotButtonPressed == true) {
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
        else if (addBool == true || subBool == true || mulBool == true || divBool == true)
        {
            if (numberIsNegative == false) {
                // if an operator has been pushed; number is positive; number is whole
                if (dotButtonPressed == false) {
                    if (textareaValue.length() == 1) { // ex: 5
                        textareaValue = new StringBuffer();
                    } else if (textareaValue.length() == 2) {
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                    } else if (textareaValue.length() >= 2) { // ex: 56 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
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
                else if (dotButtonPressed == true) {
                    if (textareaValue.length() == 2) // ex: 3.
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-1));
                    else if (textareaValue.length() == 3) { // ex: 3.2 0.0
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length()-2)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                    } else if (textareaValue.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
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
            } else if (numberIsNegative == true) {
                // if an operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
                    if (textareaValue.length() == 2) { // ex: -3
                        textareaValue = new StringBuffer();
                        textArea.setText(textareaValue.toString());
                        values[valuesPosition] = "";
                    } else if (textareaValue.length() >= 3) { // ex: -32 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
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
                else if (dotButtonPressed == true) {
                    if (textareaValue.length() == 4) { // -3.2
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString())); // 3.2 or 0.0
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, 1)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                        textArea.setText(textareaValue + "-"); // 3- or 0-
                        values[valuesPosition] = "-" + textareaValue; // -3 or -0
                    } else if (textareaValue.length() > 4) { // ex: -3.25  or -0.00
                        textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString())); // 3.25 or 0.00
                        textareaValue = new StringBuffer().append(textareaValue.substring(0, textareaValue.length())); // 3.2 or 0.0
                        values[0] = clearZeroesAndDecimalAtEnd(textareaValue.toString());
                        LOGGER.info("textarea: " + textareaValue);
                        if (textareaValue.equals("0")) {
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
            StringBuffer valuesSB = new StringBuffer(values[valuesPosition].replace(".",""));
            textareaValue = new StringBuffer().append(buttonChoice).append(valuesSB);
            updateTheTextAreaBasedOnTheTypeAndBase();
            textareaValue = new StringBuffer(values[valuesPosition].replace(".","")+".");
            dotButtonPressed = true; // !LEAVE. dot logic should not be executed anymore for the current number
        }
        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
        {
            textareaValue = new StringBuffer().append(convertToPositive(values[valuesPosition]));
            //getTextArea().setText(addNewLineCharacters(1)+buttonChoice+ textareaValue +"-");
            textareaValue = new StringBuffer().append(convertToNegative(textareaValue.toString()));
            textareaValue.append(buttonChoice);
            textareaValue.reverse();
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
            if (getCalculatorType() == BASIC)
            {
                LOGGER.info("Basic dot operations");
                performDot(buttonChoice);
            }
            else if (getCalculatorType() == PROGRAMMER)
            {
                LOGGER.info("Programmer dot operations");
                if (getCalculatorBase() == BINARY)
                {

                }
                else if (getCalculatorBase() == DECIMAL)
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
            LOGGER.info("isDotButtonPressed {} isNumberNegative {}", isDotButtonPressed(), isNumberNegative());
            textareaValue = new StringBuffer().append(convertToPositive(getTextAreaWithoutAnything()));
            textareaValue.append(buttonChoice);
            textareaValue = new StringBuffer().append(convertToNegative(textareaValue.toString()));
            dotButtonPressed = false;
            updateTheTextAreaBasedOnTheTypeAndBase();
            setValuesToTextAreaValue();
        }
        else if (StringUtils.isNotBlank(textareaValue) && dotButtonPressed && !numberIsNegative)
        {
            LOGGER.info("isDotButtonPressed {} isNumberNegative {}", isDotButtonPressed(), isNumberNegative());
            String leftOfDecimal = getNumberOnLeftSideOfNumber(values[valuesPosition]);
            String rightOfDecimal = getNumberOnRightSideOfNumber(values[valuesPosition]);
            if (!leftOfDecimal.equals("0") && !leftOfDecimal.equals("") &&
                !rightOfDecimal.equals("0") && !rightOfDecimal.equals("")) {
                StringBuffer lodSB = new StringBuffer(leftOfDecimal);
                StringBuffer rodSB = new StringBuffer(rightOfDecimal);
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
            else if (!leftOfDecimal.equals("0") && !rightOfDecimal.equals(""))
            {
                StringBuffer lodSB = new StringBuffer(leftOfDecimal);
                StringBuffer rodSB = new StringBuffer(rightOfDecimal);
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
        else if (StringUtils.isNotBlank(textareaValue) && !isDotButtonPressed())
        {
            LOGGER.info("textareaValue {} isDotButtonPressed {}", getTextareaValueWithoutAnything(), isDotButtonPressed());
            textareaValue.append(buttonChoice + getTextAreaWithoutAnything());
            textareaValue.reverse();
            updateTheTextAreaBasedOnTheTypeAndBase();
            setValuesToTextAreaValue();
        }

    }

    public StringBuffer getTextareaValueWithoutAnything() {
        return new StringBuffer().append(textareaValue.toString().replaceAll("\n",""));
    }

    public String addNewLineCharacters(int number)
    {
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<number; i++) {
	        sb.append("\n");
        }
	    return String.valueOf(sb);
    }

    @Deprecated(since = "textarea is just temporary usage")
    public void updateTextareaFromTextArea() { setTextareaValue(new StringBuffer().append(getTextAreaWithoutAnything())); }

    /**
     *  Returns the results of the last action
     */
    public void confirm()
    {
	    confirm("", getCalculatorType());
    }

    public void confirm(String message)
    {
        confirm(message, getCalculatorType());
    }

    /**
     *  Returns the results of the last action with a specific message to display
     *
     * @param message a message to send into the confirm results view
     */
    public void confirm(String message, CalculatorType_v4 calculatorType)
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
                LOGGER.info("calcBase: '{}'", base);
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
                LOGGER.info("calcBase: '{}'", base);
                try { LOGGER.info("calcByte: '{} {}'", getBytes(), getByteWord()); } catch (CalculatorError_v4 c) { logException(c); }
                break;
            }
            case SCIENTIFIC : { LOGGER.warn("Confirm message not setup for " + calculatorType); break; }
            case DATE : {
                if (((JPanelDate_v4)getCurrentPanel()).getSelectedOption().equals(OPTIONS1))
                {
                    LOGGER.info("OPTIONS Selected: " +((JPanelDate_v4)getCurrentPanel()).OPTIONS1);
                    int year = ((JPanelDate_v4)getCurrentPanel()).getTheYearFromTheFromDatePicker();
                    int month = ((JPanelDate_v4)getCurrentPanel()).getTheMonthFromTheFromDatePicker();
                    int day = ((JPanelDate_v4)getCurrentPanel()).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " +date);
                    year = ((JPanelDate_v4)getCurrentPanel()).getTheYearFromTheToDatePicker();
                    month = ((JPanelDate_v4)getCurrentPanel()).getTheMonthFromTheToDatePicker();
                    day = ((JPanelDate_v4)getCurrentPanel()).getTheDayOfTheMonthFromTheToDatePicker();
                    date = LocalDate.of(year, month, day);
                    LOGGER.info("ToDate(yyyy-mm-dd): " + date);
                    LOGGER.info("Difference");
                    LOGGER.info("Year: " + ((JPanelDate_v4)getCurrentPanel()).getYearsDifferenceLabel().getText());
                    LOGGER.info("Month: " + ((JPanelDate_v4)getCurrentPanel()).getMonthsDifferenceLabel().getText());
                    LOGGER.info("Weeks: " + ((JPanelDate_v4)getCurrentPanel()).getWeeksDifferenceLabel().getText());
                    LOGGER.info("Days: " + ((JPanelDate_v4)getCurrentPanel()).getDaysDifferenceLabel().getText());
                }
                else {
                    LOGGER.info("OPTIONS Selected: " +((JPanelDate_v4)getCurrentPanel()).OPTIONS2);
                    int year = ((JPanelDate_v4)getCurrentPanel()).getTheYearFromTheFromDatePicker();
                    int month = ((JPanelDate_v4)getCurrentPanel()).getTheMonthFromTheFromDatePicker();
                    int day = ((JPanelDate_v4)getCurrentPanel()).getTheDayOfTheMonthFromTheFromDatePicker();
                    LocalDate date = LocalDate.of(year, month, day);
                    LOGGER.info("FromDate(yyyy-mm-dd): " + date);
                    boolean isAddSelected = ((JPanelDate_v4)getCurrentPanel()).getAddRadioButton().isSelected();
                    LOGGER.info("Add or Subtract Selection: " + (isAddSelected ? "Add" : "Subtract") );
                    LOGGER.info("New Date: " + ((JPanelDate_v4)getCurrentPanel()).getResultsLabel().getText());
                }
                break;
            }
            case CONVERTER:  {
                if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
                else { LOGGER.info("Confirm Results"); }
                LOGGER.info("---------------- ");
                LOGGER.info("Converter: '" + ((JPanelConverter_v4)getCurrentPanel()).getConverterNameAsString() + "'");
                LOGGER.info("textField1: '" + ((JPanelConverter_v4)getCurrentPanel()).getTextField1().getText() + " "
                        + ((JPanelConverter_v4)getCurrentPanel()).getUnitOptions1().getSelectedItem() + "'");
                LOGGER.info("textField2: '" + ((JPanelConverter_v4)getCurrentPanel()).getTextField2().getText() + " "
                        + ((JPanelConverter_v4)getCurrentPanel()).getUnitOptions2().getSelectedItem() + "'");
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
        isButtonDwordSet = byteOption;
        isButtonQwordSet = byteOption;
    }

    /**
     * Resets all operators to the given boolean argument
     *
     * @param operatorBool
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
        //LOGGER.info("Getting number on left side of decimal");
        //LOGGER.info("currentNumber: " + currentNumber);
        String leftSide = "";
        int index = currentNumber.indexOf(".");
        //LOGGER.debug("index: " + index);
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
        //LOGGER.info("Getting number on right side of decimal");
        //LOGGER.info("currentNumber: " + currentNumber);
        String rightSide = "";
        int index = currentNumber.indexOf(".");
        //LOGGER.debug("index: " + index);
        if (index == -1 || (index+1)>=currentNumber.length()) rightSide = "";
        else {
            rightSide = currentNumber.substring(index+1);
            if (StringUtils.isEmpty(rightSide)) rightSide = "0";
        }
        LOGGER.info("number to the right of the decimal: '{}'", rightSide);
        return rightSide;
    }

    protected String clearZeroesAndDecimalAtBeginning(String currentNumber)
    {
        LOGGER.info("Starting clear zeroes at the beginning");
        LOGGER.info("currentNumber: " + currentNumber);
        textareaValue = new StringBuffer().append(textareaValue.toString().replaceAll("\n",""));
        boolean justZeroes = true;
        int index = 0;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1 || (index+1)>= textareaValue.length()) textareaValue = new StringBuffer().append(currentNumber);
        else {
            textareaValue = new StringBuffer().append(currentNumber.substring(index+1));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + String.valueOf(textareaValue));
        return new StringBuffer().append(String.valueOf(textareaValue)).toString();
    }

    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     *
     * @param currentNumber
     * @return updated currentNumber
     *
     * TODO: Test
     */
    protected String clearZeroesAndDecimalAtEnd(String currentNumber)
    {
        LOGGER.info("Starting clear zeroes at the end");
        LOGGER.debug("currentNumber: " + currentNumber);
        textareaValue = new StringBuffer().append(textareaValue.toString().replaceAll("\n",""));
        boolean justZeroes = true;
        int index = 0;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) textareaValue = new StringBuffer().append(currentNumber.substring(0, currentNumber.indexOf("0")));
        else {
            textareaValue = new StringBuffer().append(currentNumber.substring(0, index));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + String.valueOf(textareaValue));
        return new StringBuffer().append(String.valueOf(textareaValue)).toString();
    }

    public int getBytes() throws CalculatorError_v4
    {
        if (isButtonByteSet) { return 8; }
        else if (isButtonWordSet) { return 16; }
        else if (isButtonDwordSet) { return 32; }
        else if (isButtonQwordSet) { return 64; }
        else { throw new CalculatorError_v4("Unable to determine bytes"); } // shouldn't ever come here
    }

    public String getByteWord() throws CalculatorError_v4
    {
        if (getBytes() == 8)       return "Byte";
        else if (getBytes() == 16) return "Word";
        else if (getBytes() == 32) return "DWord";
        else                       return "QWord";
    }

    /**
     * Tests whether a number is a decimal or not
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public boolean isDecimal(String number)
    {
        boolean answer = false;
        if (number.contains(".")) answer = true;
        LOGGER.info("isDecimal("+number.replaceAll("\n","")+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is positive
     *
     * @param result
     * @return either true or false based on result
     *
     * Fully tested
     */
    public boolean isPositiveNumber(String result)
    {
        boolean answer = false;
        if (!result.contains("-")) {
            answer = true;
        }
        LOGGER.info("isPositiveNumber("+result+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is negative
     *
     * @param result
     * @return
     *
     * Fully tested
     */
    public boolean isNegativeNumber(String result)
    {
        boolean answer = false;
        if (result.contains("-")) {
            answer = true;
        }
        LOGGER.info("isNegativeNumber("+result.replaceAll("\n", "")+") == " + answer);
        return answer;
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number
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
     * @param number
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

    protected String getTextAreaWithoutAnything()
    {
        return getTextArea().getText()
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

    protected String getTextAreaWithoutNewLineCharactersOrWhiteSpace()
    {
        return getTextArea().getText()
                            .replaceAll("\n", "")
                            .strip();
    }

    protected String getTextAreaWithoutNewLineCharacters()
    {
        return getTextArea().getText().replaceAll("\n", "");
    }

    protected String getTextareaWithoutNewLineCharacters()
    {
        return getTextareaValue().toString().replaceAll("\n", "").strip();
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
        boolean result = false;
        if (getTextAreaWithoutAnything().equals("Invalid textarea")   ||
            getTextAreaWithoutAnything().equals("Cannot divide by 0") ||
            getTextAreaWithoutAnything().equals("Not a Number")       ||
            getTextAreaWithoutAnything().equals("Enter a Number")     ||
            getTextAreaWithoutAnything().equals("Only positive numbers") ||
            getTextAreaWithoutAnything().contains("E"))
        {
            result = true;
        }
        return result;
    }

    /**
     * Calls createImageIcon(String path, String description
     *
     * @throws FileNotFoundException
     */
    public ImageIcon createImageIcon(String path) throws FileNotFoundException
    {
        return createImageIcon(path, "No description given.");
    }

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the image
     * @param description a description of the image being set
     */
    protected ImageIcon createImageIcon(String path, String description) throws FileNotFoundException
    {
        //LOGGER.info("Inside createImageIcon(): creating image for " + description);
        ImageIcon retImageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) {
            retImageIcon = new ImageIcon(resource);
            //LOGGER.info("the path '" + path + "' created the '"+description+"'! the ImageIcon is being returned...");
            //LOGGER.info("End createImageIcon()");
        }
        else {
            //LOGGER.debug("The path '" + path + "' could not find an image there!. Trying again by removing 'src/main/resources/' from path!");
            resource = classLoader.getResource(path.substring(19));
            if (resource != null) {
                retImageIcon = new ImageIcon(resource);
                //LOGGER.info("the path '" + path + "' created an image after removing 'src/main/resources/'! the ImageIcon is being returned...");
                //LOGGER.info("End createImageIcon()");
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
            setCalculatorImage1(createImageIcon("src/main/resources/images/calculatorOriginalCopy.jpg"));
            setCalculator2(createImageIcon("src/main/resources/images/calculatorOriginal.jpg"));
            setMacLogo(createImageIcon("src/main/resources/images/maclogo.jpg"));
            setWindowsLogo(createImageIcon("src/main/resources/images/windows11.jpg"));
            setBlankImage(null); // new ImageIcon()
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error(e.getMessage());
        }
    }

    /************************ Unsolid Methods ***************************/

    /**
     * This method returns all the values in memory which are not blank
     *
     * @param someValues
     * @return
     *
     * TODO: Test
     */
    @Deprecated
    public ArrayList<String> getNonBlankValues(String[] someValues)
    {
    	ArrayList<String> listToReturn = new ArrayList<>();
    	for (int i=0; i<10; i++) {
    		String thisValue = someValues[i];
    		if (!thisValue.equals("")) {
    			listToReturn.add(thisValue);
    		} else {
    			break;
    		}
    	}
    	return listToReturn;
    }

    public void setTextareaToValuesAtPosition(String buttonChoice)
    {
        textareaValue.append(values[valuesPosition]);
        LOGGER.debug("textarea: '", textareaValue + "'");
        textareaValue.append(textareaValue + buttonChoice); // update textArea
        LOGGER.debug("textarea: '" + textareaValue + "'");
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
        //if (values[valuesPosition].endsWith(".")) {
        //    values[valuesPosition] = values[valuesPosition] + "0";
        //}
    }

    public Collection<JButton> getNumberButtons()
    {
        LinkedList<JButton> buttons = new LinkedList<>();
        buttons.add(getButton0());
        buttons.add(getButton1());
        buttons.add(getButton2());
        buttons.add(getButton3());
        buttons.add(getButton4());
        buttons.add(getButton5());
        buttons.add(getButton6());
        buttons.add(getButton7());
        buttons.add(getButton8());
        buttons.add(getButton9());
        return buttons;
    }

    public Collection<JButton> getButtonClearEntryAndButtonDeleteAndButtonDot()
    {
        return Arrays.asList(getButtonClearEntry(), getButtonDelete(), getButtonDot());
    }

    @Deprecated(since = "clearAllOtherBasicCalculatorButtons handles this")
    public void clearVariableNumberOfButtonsFunctionalities()
    {
        LOGGER.debug("Clear VariableNumber of Buttons");
        getButtonClearEntryAndButtonDeleteAndButtonDot().forEach(button -> {
            LOGGER.debug("Removing action listener from button: " + button.getName());
            Arrays.stream(button.getActionListeners()).collect(Collectors.toList()).forEach(al -> {
                button.removeActionListener(al);
            });
        });
        getButtonClearEntryAndButtonDeleteAndButtonDot().forEach(button -> Arrays.stream(button.getKeyListeners()).collect(Collectors.toList()).forEach(button::removeKeyListener));
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

    public void performTasksWhenChangingJPanels(JPanel newPanel, CalculatorType_v4 calculatorType_v4, ConverterType_v4 converterType_v4) throws CalculatorError_v4
    {
        LOGGER.info("Starting performTasksWhenChangingJPanels");
        boolean changedPanels = false;
        JPanel oldPanel = updateJPanel(newPanel);
        if (converterType_v4 == null)
        {
            setTitle(calculatorType_v4.getName());
            LOGGER.debug("oldPanel: " + oldPanel.getClass().getName());
            LOGGER.debug("newPanel: " + getCurrentPanel().getClass().getName());
            // if oldPanel is same as newPanel, don't change
            if (oldPanel.getClass().getName().equals(getCurrentPanel().getClass().getName())) {}
            else if (getCurrentPanel() instanceof JPanelBasic_v4)
            {
                ((JPanelBasic_v4)getCurrentPanel()).performBasicCalculatorTypeSwitchOperations();
                changedPanels = true;
            }
            else if (getCurrentPanel() instanceof JPanelProgrammer_v4)
            {
                ((JPanelProgrammer_v4)getCurrentPanel()).performProgrammerCalculatorTypeSwitchOperations();
                changedPanels = true;
            }
            else //if (getCurrentPanel() instanceof JPanelDate_v4)
            {
                ((JPanelDate_v4)getCurrentPanel()).performDateCalculatorTypeSwitchOperations();
                changedPanels = true;
            }
        }
        else
        {
            setTitle(calculatorType_v4.getName());
            if (getCurrentPanel() instanceof JPanelConverter_v4)
            {
                ((JPanelConverter_v4)newPanel).performConverterCalculatorTypeSwitchOperations(converterType_v4);
                changedPanels = true;
            }
        }
        setCurrentPanel(newPanel);
        super.pack();
        LOGGER.info("Finished performTasksWhenChangingJPanels\n");
        if (changedPanels) confirm("Switched Calculator Types");
        else confirm("Not changing panels when oldPanel("+oldPanel.getClass().getName()+") == newPanel("+getCurrentPanel().getClass().getName()+")");
    }

    public void performTasksWhenChangingJPanels(JPanel currentPanel, CalculatorType_v4 calculatorType_v4) throws CalculatorError_v4
    {
        performTasksWhenChangingJPanels(currentPanel, calculatorType_v4, null);
    }

    public Collection<JButton> getBasicOperationButtons()
    {
        return Arrays.asList(getButtonAdd(), getButtonSubtract(), getButtonMultiply(), getButtonDivide());
    }

    /**
     * This method clears the functions off of + - * /
     */
    public void clearAllBasicOperationButtons()
    {
        getBasicOperationButtons().forEach(button -> {
            Arrays.stream(button.getActionListeners()).forEach(al -> button.removeActionListener(al));
        });
    }

    public Collection<JButton> getAllOtherBasicCalculatorButtons()
    {
        return Arrays.asList(getButtonEquals(), getButtonNegate(), getButtonClear(), getButtonClearEntry(),
                getButtonDelete(), getButtonDot(), getButtonFraction(), getButtonPercent(), getButtonSqrt(),
                getButtonMemoryAddition(), getButtonMemorySubtraction(),
                getButtonMemoryStore(), getButtonMemoryClear(), getButtonMemoryRecall());
    }

    public void clearAllOtherBasicCalculatorButtons()
    {
        getAllOtherBasicCalculatorButtons().forEach(button -> {
            Arrays.stream(button.getActionListeners()).forEach(al -> button.removeActionListener(al) );
        });
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
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    StringUtils.isNotBlank(textArea.getText()) && !textArea1ContainsBadText())
            {
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
                textareaValue.reverse();
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition();
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalculatorType());
                subBool = resetOperator(subBool);
                addBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalculatorType());
                mulBool = resetOperator(mulBool);
                addBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalculatorType());
                divBool = resetOperator(divBool);
                addBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.error("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;

            if (getCalculatorType() == PROGRAMMER && getCalculatorBase() != DECIMAL && !textArea1ContainsBadText())
            {
                String number = "";
                if (getCalculatorBase() == BINARY)
                {
                    number = getTextAreaWithoutAnything();
                    number = addZeroesToNumber(number);
                    // now update textArea with newly formatted binary number plus the operator
                    getTextArea().setText(addNewLineCharacters(3) + "+" + " " + number);
                    updateTextareaFromTextArea();
                }

                try { number = convertFromTypeToTypeOnValues(BINARY, DECIMAL, number);
                    LOGGER.info("number converted after add pushed: " + number);
                }
                catch (CalculatorError_v4 c) { logException(c); }
                values[0] = number;
            }
            confirm("Pressed: " + buttonChoice);
        }
    }

    public String addZeroesToNumber(String number)
    {
        int lengthOfNumber = number.length();
        int zeroesToAdd = 0;
        ((JPanelProgrammer_v4)getCurrentPanel()).setTheBytesBasedOnTheNumbersLength(number);
        if (isButtonByteSet) { zeroesToAdd = 8 - lengthOfNumber; }
        else if (isButtonWordSet) { zeroesToAdd = 16 - lengthOfNumber; }
        else if (isButtonDwordSet) { zeroesToAdd = 32 - lengthOfNumber; }
        else /* (isButtonQwordSet) */ { zeroesToAdd = 64 - lengthOfNumber; }
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
            values[0] = clearZeroesAndDecimalAtEnd(convertToPositive(values[0]));
            values[0] = new StringBuffer().append(convertToNegative(values[0])).toString();
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
        if (getCalculatorType() == BASIC)
        {
            if (!numberIsNegative)
                textArea.setText(addNewLineCharacters(1) + values[valuesPosition]);
            else {
                textArea.setText(addNewLineCharacters(1) + convertToPositive(values[valuesPosition]) + "-");
                textareaValue = new StringBuffer("-"+values[valuesPosition]);
                numberIsNegative = true;
            }
        }
        else if (getCalculatorType() == PROGRAMMER)
        {
            if (getCalculatorBase() == BINARY) {
                String convertedToBinary = "";
                try { convertedToBinary = convertFromTypeToTypeOnValues(DECIMAL, BINARY, values[0]); }
                catch (CalculatorError_v4 c) { logException(c); }
                textArea.setText(addNewLineCharacters(3) + convertedToBinary);
            }
            else if (getCalculatorBase() == DECIMAL) {
                if (!numberIsNegative)
                    textArea.setText(addNewLineCharacters(3) + textareaValue);
                else {
                    textArea.setText(addNewLineCharacters(3) + convertToPositive(values[0]) + "-");
                    textareaValue = new StringBuffer("-"+values[valuesPosition]);
                    numberIsNegative = true;
                }
            }
            else if (getCalculatorBase() == OCTAL) {}
            else /* (getCalculatorBase() == HEXIDECIMAL) */ {}
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
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea1ContainsBadText()) {
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + getTextAreaWithoutAnything());
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(getCalculatorType());
                subBool = resetOperator(subBool);
                subBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(getCalculatorType());
                mulBool = resetOperator(mulBool);
                subBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("")) {
                divide(getCalculatorType());
                divBool = resetOperator(divBool);
                subBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
    public void subtract(CalculatorType_v4 calculatorType_v4)
    {

        if (calculatorType_v4.equals(CalculatorType_v4.BASIC))
        {
            subtract();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType_v4.equals(CalculatorType_v4.PROGRAMMER))
        {
            subtract();
            String operator = determineIfProgrammerPanelOperatorWasPushed();
            if (operator != "") textArea.setText(addNewLineCharacters(3) + operator + " " +values[0]);
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
                if (getCalculatorType() == BASIC)textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(getCalculatorType());
                subBool = resetOperator(subBool);
                mulBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(getCalculatorType());
                mulBool = resetOperator(mulBool);
                mulBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("")) {
                divide(getCalculatorType());
                divBool = resetOperator(divBool);
                mulBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
            values[0] = clearZeroesAndDecimalAtEnd(String.valueOf(result));
            values[0] = textareaValue.toString(); // textarea changed to whole number, or int
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (isNegativeNumber(values[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(values[0]));
            values[0] = clearZeroesAndDecimalAtEnd(convertToPositive(values[0]));
            values[0] = convertToNegative(values[0]);
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
            textArea.setText(addNewLineCharacters(1)+ textareaValue +"-");
            textareaValue = new StringBuffer().append(convertToNegative(values[0]));
        }
        else
        {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
        textareaValue = new StringBuffer(values[0]);
    }

    public void multiply(CalculatorType_v4 calculatorType_v4)
    {
        if (calculatorType_v4.equals(CalculatorType_v4.BASIC)) {
            multiply();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType_v4.equals(CalculatorType_v4.PROGRAMMER)) {
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (subBool && !values[1].equals("")) {
                subtract(getCalculatorType());
                subBool = resetOperator(subBool);
                divBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (mulBool && !values[1].equals("")) {
                multiply(getCalculatorType());
                mulBool = resetOperator(mulBool);
                divBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
            }
            else if (divBool && !values[1].equals("") & !values[1].equals("0")) {
                divide(getCalculatorType());
                divBool = resetOperator(divBool);
                divBool = true;
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textareaValue);
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textareaValue);
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
                if (getCalculatorType() == BASIC) textArea.setText(addNewLineCharacters(1) + "Enter a Number");
                else if (getCalculatorType() == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + "Enter a Number");
                else if (getCalculatorType() == SCIENTIFIC) LOGGER.warn("SETUP");
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
        else if (values[1].equals("0")) {
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = "0";
            firstNumBool = true;
        } else { LOGGER.warn("UNKNOWN ISSUE"); }

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
                //textArea.setText("\n" + textarea + "-"); // update textArea
                //LOGGER.info("temp["+valuesPosition+"] '" + values[valuesPosition] + "'");
            }
            else
            {   // positive decimal
                values[0] = formatNumber(values[0]);
            }
        }
        textareaValue = new StringBuffer(values[0]);
    }

    public void divide(CalculatorType_v4 calculatorType_v4)
    {
        if (calculatorType_v4.equals(CalculatorType_v4.BASIC)) {
            divide();
            if (isPositiveNumber(values[0])) textArea.setText(addNewLineCharacters(1) + values[0]);
            else textArea.setText(addNewLineCharacters(1) + convertToPositive(values[0]) + "-");
            updateTextareaFromTextArea();
        }
        else if (calculatorType_v4.equals(CalculatorType_v4.PROGRAMMER))
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
        if (getCalculatorType() == BASIC)
        {
            determineAndPerformBasicCalculatorOperation();
            String operator = determineIfBasicPanelOperatorWasPushed();
            if (operator != "") textArea.setText(addNewLineCharacters(1) + operator + " " + textareaValue);
            else textArea.setText(addNewLineCharacters(1) + textareaValue);
        }
        else if (getCalculatorType() == PROGRAMMER)
        {
            // Get the current number first. save
            String numberInTextArea = getTextAreaWithoutAnything();
            if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonBin().isSelected())
            {
                try { values[1] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
                catch (CalculatorError_v4 c) { logException(c); }
                LOGGER.info("Values[1] saved to {}", values[1]);
                LOGGER.info("Now performing operation...");
                determineAndPerformBasicCalculatorOperation();
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonOct().isSelected())
            {
                try { values[1] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
                catch (CalculatorError_v4 c) { logException(c); }
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonDec().isSelected())
            {
                determineAndPerformBasicCalculatorOperation();
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonHex().isSelected())
            {
                values[0] = "";
                try { values[0] = convertFromTypeToTypeOnValues(HEXIDECIMAL, DECIMAL, values[0]); }
                catch (CalculatorError_v4 c) { logException(c); }
                values[1] = "";
                try { values[0] = convertFromTypeToTypeOnValues(HEXIDECIMAL, DECIMAL, values[1]); }
                catch (CalculatorError_v4 c) { logException(c); }
            }

            if (orButtonBool)
            {
                ((JPanelProgrammer_v4)getCurrentPanel()).performOr();
                getTextArea().setText(addNewLineCharacters(1) + values[0]);

            }
            else if (isModButtonPressed())
            {
                LOGGER.info("Modulus result");
                ((JPanelProgrammer_v4)getCurrentPanel()).performModulus();
                // update values and textArea accordingly
                valuesPosition = 0;
                modButtonBool = false;
            }
            updateTheTextAreaBasedOnTheTypeAndBase();
        }
        //TODO: add more calculator types here


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
            multiply(getCalculatorType());
            mulBool = resetOperator(mulBool);
        }
        else if (divBool){
            divide(getCalculatorType());
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
            getTextArea().setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace());
            updateTextareaFromTextArea();
            if (!textareaValue.equals("")) {
                if (numberIsNegative) {
                    textareaValue = new StringBuffer().append(convertToPositive(textareaValue.toString()));
                    LOGGER.debug("textarea: " + textareaValue);
                    if (getCalculatorType() == BASIC) getTextArea().setText(addNewLineCharacters(1)+ textareaValue);
                    else if (getCalculatorType() == PROGRAMMER) getTextArea().setText(addNewLineCharacters(3)+ textareaValue);
                }
                else {
                    if (getCalculatorType() == BASIC) getTextArea().setText(addNewLineCharacters(1)+ textareaValue +"-");
                    else if (getCalculatorType() == PROGRAMMER) getTextArea().setText(addNewLineCharacters(3)+ textareaValue +"-");

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
     * @param newPanel
     * @return
     */
    public JPanel updateJPanel(JPanel newPanel)
    {
        JPanel oldPanel = getCurrentPanel();
        remove(oldPanel);
        setCurrentPanel(newPanel);
        add(getCurrentPanel());
        return oldPanel;
    }

    /**
     * This method returns true or false depending on if an operator was pushed or not
     * @return
     */
    @Deprecated(since = "Use String return method")
    public boolean determineIfMainOperatorWasPushedBoolean()
    {
        boolean result = false;
        if (isAddBool() || isSubBool() ||
                isMulBool() || isDivBool())
        {
            result = true;
        }
        return result;
    }

    public String determineIfBasicPanelOperatorWasPushed()
    {
        String results = "";
        if (isAddBool()) { results = "+"; }
        else if (isSubBool()) { results = "-"; }
        else if (isMulBool()) { results = "*"; }
        else if (isDivBool()) { results = "/"; }
        LOGGER.info("operator: " + (results.equals("") ? "no basic operator pushed" : results));
        return results;
    }

    public String determineIfProgrammerPanelOperatorWasPushed()
    {
        String results = "";
        // what operations can be pushed: MOD, OR, XOR, NOT, AND
        if (isModButtonPressed()) { results = "MOD"; }
        else if (isOrButtonPressed()) { results = "OR"; }
        else if (isXorButtonPressed()) { results = "XOR"; }
        else if (isNotButtonPressed()) { results = "NOT"; }
        else if (isAndButtonPressed()) { results = "AND"; }
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
        if (getCalculatorType() == CalculatorType_v4.PROGRAMMER)
        {
            for(String numberInMemory : getMemoryValues())
            {
                newMemoryValues[i] = "";
                try { newMemoryValues[i] = convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInMemory); }
                catch (CalculatorError_v4 c) { logException(c); }
                LOGGER.debug("new number in memory is: " + newMemoryValues[i]);
                i++;
            }
        }
    }

    public String convertFromTypeToTypeOnValues(CalculatorBase_v4 fromType, CalculatorBase_v4 toType, String strings) throws CalculatorError_v4
    {
        LOGGER.debug("convert from {} to {}", fromType, toType);
        LOGGER.debug("on value: {}", strings);
        StringBuffer sb = new StringBuffer();
        int countOfStrings = 0;
        if (strings.contains(" ")) {
            String[] strs = strings.split(" ");
            for(String s : strs) {
                sb.append(s);
                countOfStrings++;
            }
        } else { sb.append(strings); }
        LOGGER.info("sb: " + sb);
        String convertedValue = "";
        if (StringUtils.isEmpty(strings)) return "";
        // All from HEXIDECIMAL to any other option
        if (fromType == HEXIDECIMAL && toType == DECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == HEXIDECIMAL && toType == OCTAL) { confirm("IMPLEMENT"); }
        else if (fromType == HEXIDECIMAL && toType == BINARY) { confirm("IMPLEMENT"); }
        // All from DECIMAL to any other option
        else if (fromType == DECIMAL && toType == HEXIDECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == DECIMAL && toType == OCTAL) { confirm("IMPLEMENT"); }
        else if (fromType == DECIMAL && toType == BINARY)
        {
            LOGGER.debug("Converting str("+sb+")");
            sb = new StringBuffer();
            int number = 0;
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
            int sizeOfSecond8Bits = 0;
            try {
                // start counting at 9. The 8th bit is a space
                sizeOfSecond8Bits = sb.toString().substring(10).length();
                int zeroesToAdd = 8 - sizeOfSecond8Bits;
                String zeroes = "";
                for(int i = 0; i < zeroesToAdd; i++) zeroes = zeroes + "0";
                sb.append(zeroes);
            } catch (StringIndexOutOfBoundsException e) {
                LOGGER.warn("No second bits found");
            }
            LOGGER.info("Before reverse: {}", sb);
            // End adding zeroes here

            sb = sb.reverse();
            LOGGER.info("After reverse: {}", sb);
            String strToReturn = sb.toString();
            LOGGER.debug("convertFrom("+fromType+")To("+toType+") = "+ sb);
            LOGGER.warn("ADD CODE THAT MAKES SURE RETURNED VALUE UPDATES BYTES");
            LOGGER.warn("IF AFTER REVERSE IS LONGER THAN 8 BITS, WE NEED TO ADD");
            LOGGER.warn("A SPACE BETWEEN THE BYTES");
            convertedValue = strToReturn;
        }
        // All from OCTAL to any other option
        else if (fromType == OCTAL && toType == HEXIDECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == OCTAL && toType == DECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == OCTAL && toType == BINARY) { confirm("IMPLEMENT"); }
        // All from BINARY to any other option
        else if (fromType == BINARY && toType == HEXIDECIMAL) { confirm("IMPLEMENT"); }
        else if (fromType == BINARY && toType == DECIMAL)
        {
            LOGGER.debug("Converting str("+sb+")");

            int appropriateLength = getBytes();
            LOGGER.debug("sb: " + sb);
            LOGGER.debug("appropriateLength: " + appropriateLength);

            if (sb.length() != appropriateLength)
            {
                LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                // user had entered 101, which really is 00000101
                // but they aren't showing the first 5 zeroes
                int difference = appropriateLength - sb.length();
                StringBuffer missingZeroes = new StringBuffer();
                for (int i=0; i<difference; i++) {
                    missingZeroes.append("0");
                }
                missingZeroes.append(sb);
                sb = new StringBuffer(missingZeroes);
                LOGGER.debug("sb: " + sb);
            }

            double result = 0.0;
            double num1 = 0.0;
            double num2 = 0.0;
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

        LOGGER.info("base set to: " + getCalculatorBase() + addNewLineCharacters(1));
        return convertedValue;
    }

    public void performOrLogic(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<values[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(values[0].charAt(i)).equals("0") &&
                    String.valueOf(values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(String.valueOf(values[0].charAt(i))+" + "+String.valueOf(values[1].charAt(i))+
                    " = "+ letter);
        }
        values[0] = String.valueOf(sb);
        getTextArea().setText(addNewLineCharacters(1)+values[0]);
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
        JOptionPane.showMessageDialog(Calculator_v4.this,
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
        JOptionPane.showMessageDialog(Calculator_v4.this,
                mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);

    }

    /************* All Getters and Setters ******************/

    @Override
    public GridBagLayout getLayout() { return layout; }
    public GridBagConstraints getConstraints() { return constraints; }
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
    public String[] getValues() { return values; }
    public String[] getMemoryValues() { return memoryValues; }
    public int getValuesPosition() { return valuesPosition; }
    public int getMemoryPosition() { return memoryPosition; }
    protected JTextArea getTextArea() { return this.textArea; }
    public StringBuffer getTextareaValue() { return textareaValue; }
    public CalculatorType_v4 getCalculatorType() { return this.calcType; }
    public JPanel getCurrentPanel() { return currentPanel; }
    public ImageIcon getCalculatorImage1() { return calculatorImage1; }
    public ImageIcon getCalculator2() { return calculator2; }
    public ImageIcon getMacLogo() { return macLogo; }
    public ImageIcon getWindowsLogo() { return windowsLogo; }
    public ImageIcon getBlankImage() { return blankImage; }
    public JLabel getIconLabel() { return iconLabel; }
    public JLabel getTextLabel() { return textLabel; }

    public boolean isFirstNumBool() { return firstNumBool; }
    public boolean isNumberNegative() { return numberIsNegative; }
    public boolean isMemorySwitchBool() { return memorySwitchBool; }
    public boolean isAddBool() { return addBool; }
    public boolean isSubBool() { return subBool; }
    public boolean isMulBool() { return mulBool; }
    public boolean isDivBool() { return divBool; }
    public boolean isMemAddBool() { return memAddBool; }
    public boolean isMemSubBool() { return memSubBool; }
    public boolean isNegatePressed() { return negatePressed; }
    public boolean isDotButtonPressed() { return dotButtonPressed; }
    public boolean isOrButtonPressed() { return orButtonBool; }
    public boolean isModButtonPressed() { return modButtonBool; }
    public boolean isXorButtonPressed() { return xorButtonBool; }
    public boolean isNegateButtonPressed() { return negateButtonBool; }
    public boolean isNotButtonPressed() { return notButtonBool; }
    public boolean isAndButtonPressed() { return andButtonBool; }
    public CalculatorBase_v4 getCalculatorBase() { return base; }
    public JMenuBar getBar() { return bar; }

    public boolean isButtonBinSet() { return isButtonBinSet; }
    public boolean isButtonOctSet() { return isButtonOctSet; }
    public boolean isButtonDecSet() { return isButtonDecSet; }
    public boolean isButtonHexSet() { return isButtonHexSet; }
    public boolean isButtonByteSet() { return isButtonByteSet; }
    public boolean isButtonWordSet() { return isButtonWordSet; }
    public boolean isButtonDWordSet() { return isButtonDwordSet; }
    public boolean isButtonQWordSet() { return isButtonQwordSet; }

    public void setLayout(GridBagLayout layout) { this.layout = layout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    public void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setTextArea(JTextArea textArea) { this.textArea = textArea; }
    public void setTextareaValue(StringBuffer textareaValue) { this.textareaValue = textareaValue; }
    public void setCalculatorType(CalculatorType_v4 calcType) { this.calcType = calcType; }

    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setCalculatorImage1(ImageIcon calculatorImage1) { this.calculatorImage1 = calculatorImage1; }
    public void setCalculator2(ImageIcon calculator2) { this.calculator2 = calculator2; }
    public void setMacLogo(ImageIcon macLogo) { this.macLogo = macLogo; }
    public void setWindowsLogo(ImageIcon windowsLogo) { this.windowsLogo = windowsLogo; }
    public void setBlankImage(ImageIcon blankImage) { this.blankImage = blankImage; }
    public void setIconLabel(JLabel iconLabel) { this.iconLabel = iconLabel; }
    public void setTextLabel(JLabel textLabel) { this.textLabel = textLabel; }
    public void setFirstNumBool(boolean firstNumBool) { this.firstNumBool = firstNumBool; }
    public void setNumberIsNegative(boolean numberIsNegative) { this.numberIsNegative = numberIsNegative; }
    public void setMemorySwitchBool(boolean memorySwitchBool) { this.memorySwitchBool = memorySwitchBool; }
    public void setAddBool(boolean addBool) { this.addBool = addBool; }
    public void setSubBool(boolean subBool) { this.subBool = subBool; }
    public void setMulBool(boolean mulBool) { this.mulBool = mulBool; }
    public void setDivBool(boolean divBool) { this.divBool = divBool; }
    public void setMemAddBool(boolean memAddBool) { this.memAddBool = memAddBool; }
    public void setMemSubBool(boolean memSubBool) { this.memSubBool = memSubBool; }
    public void setNegatePressed(boolean negatePressed) { this.negatePressed = negatePressed; }
    public void setDotButtonPressed(boolean dotButtonPressed) { this.dotButtonPressed = dotButtonPressed; }
    public void setButtonBin(boolean buttonBinSet) { isButtonBinSet = buttonBinSet; }
    public void setButtonOct(boolean buttonOctSet) { isButtonOctSet = buttonOctSet; }
    public void setButtonDec(boolean buttonDecSet) { isButtonDecSet = buttonDecSet; }
    public void setButtonHex(boolean buttonHexSet) { isButtonHexSet = buttonHexSet; }
    public void setButtonByte(boolean buttonByteSet) { isButtonByteSet = buttonByteSet; }
    public void setButtonWord(boolean buttonWordSet) { isButtonWordSet = buttonWordSet; }
    public void setButtonDWord(boolean buttonDwordSet) { isButtonDwordSet = buttonDwordSet; }
    public void setButtonQWord(boolean buttonQwordSet) { isButtonQwordSet = buttonQwordSet; }
    public void setOrButtonBool(boolean orButtonBool) { this.orButtonBool = orButtonBool; }
    public void setModButtonBool(boolean modButtonBool) { this.modButtonBool = modButtonBool; }
    public void setXorButtonBool(boolean xorButtonBool) { this.xorButtonBool = xorButtonBool; }
    public void setNegateButtonBool(boolean negateButtonBool) { this.negateButtonBool = negateButtonBool; }
    public void setNotButtonBool(boolean notButtonBool) { this.notButtonBool = notButtonBool; }
    public void setAndButtonBool(boolean andButtonBool) { this.andButtonBool = andButtonBool; }
    public void setCalculatorBase(CalculatorBase_v4 base) { this.base = base; }
    public void setBar(JMenuBar bar) { this.bar = bar; }

}