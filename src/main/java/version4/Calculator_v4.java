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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static version4.CalcType_v4.*;
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
    protected StringBuffer textarea = new StringBuffer(); // String representing appropriate visual of number
    protected CalcType_v4 calcType = null;
    protected JPanel currentPanel = null;
    protected ImageIcon calculatorImage1, calculator2, macLogo, windowsLogo, blankImage;
    protected JLabel iconLabel;
    protected JLabel textLabel;
    protected JMenuBar bar;

    protected boolean firstNumBool = true;
    protected boolean numberOneNegative = false;
    protected boolean numberTwoNegative = false;
    protected boolean numberThreeNegative = false;
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
    protected CalcType_v4 base = CalcType_v4.DECIMAL;

    public Calculator_v4() throws Exception { this(BASIC, null, null); }
    /**
     * This constructor is used to create a calculator with a specific panel
     * @param calcType the type of calculator to create. sets the title
     */
    public Calculator_v4(CalcType_v4 calcType) throws Exception { this(calcType, null, null); }
    /**
     * This constructor is used to create a calculator with a specific converter panel
     * @param calcType the type of calculator to create. sets the title
     */
    public Calculator_v4(CalcType_v4 calcType, String chosenOption) throws Exception
    { this(calcType, null, chosenOption); }
    /**
     *
     * @param calcType
     * @param converterType
     * @throws Exception
     */
    public Calculator_v4(CalcType_v4 calcType, ConverterType_v4 converterType) throws Exception
    { this(calcType, converterType, null); }
    /**
     * MAIN CONSTRUCTOR USED
     * @param calcType
     * @param converterType
     * @param chosenOption
     */
    public Calculator_v4(CalcType_v4 calcType, ConverterType_v4 converterType, String chosenOption)  throws CalculatorError_v4, ParseException, IOException, UnsupportedLookAndFeelException
    {
        super(calcType.getName()); // default title is Basic
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setCalcType(calcType);
        setupMenuBar(); // setup here for all types
        setupCalculatorImages();
        if (converterType == null && chosenOption == null) setCurrentPanel(determinePanelType(calcType));
        else if (converterType != null) setCurrentPanel(determinePanelType(calcType, converterType, null));
        else if (chosenOption != null)  setCurrentPanel(determinePanelType(calcType, null, chosenOption));
        add(getCurrentPanel());
        getLogger().info("Panel added to calculator");
        setMaximumSize(getCurrentPanel().getSize());
        pack();
        setVisible(true);
        setResizable(false);
        setLocation(750, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getLogger().info("Finished constructing the calculator\n");
    }




    /******************** Start of methods here **********************/

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
        getLogger().info("All images set for Calculator");
    }

    public void setupBasicCalculatorOperationButtons()
    {
        getButtonAdd().setFont(font);
        getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        getButtonAdd().setEnabled(true);
        getButtonAdd().addActionListener(this::performAdditionButtonActions);

        getButtonSubtract().setFont(font);
        getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        getButtonSubtract().setEnabled(true);
        getButtonSubtract().addActionListener(action -> {
            performSubtractionButtonActions(action);
        });

        getButtonMultiply().setFont(font);
        getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        getButtonMultiply().setEnabled(true);
        getButtonMultiply().addActionListener(action -> {
            performMultiplicationActions(action);
        });

        getButtonDivide().setFont(font);
        getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        getButtonDivide().setEnabled(true);
        getButtonDivide().addActionListener(action -> {
            performDivideButtonActions(action);
        });
    }

    public JPanel determinePanelType(CalcType_v4 calcType) throws ParseException, CalculatorError_v4
    {
        return determinePanelType(calcType, null, null);
    }

    public JPanel determinePanelType(CalcType_v4 calcType, ConverterType_v4 converterType, String chosenOption) throws ParseException, CalculatorError_v4
    {
        if (calcType == null) {
            getLogger().error("CalcType cannot be null");
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

    @Deprecated(since = "Needs to make more obvious")
    public void setupOtherBasicCalculatorButtons()
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

        getButtonNegate().setFont(font);
        getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        getButtonNegate().setEnabled(true);
        getButtonNegate().addActionListener(action -> {
            performNegateButtonActions(action);
        });
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
        JMenuItem basic = new JMenuItem(CalcType_v4.BASIC.getName());
        JMenuItem programmer = new JMenuItem(CalcType_v4.PROGRAMMER.getName());
        JMenuItem dates = new JMenuItem(CalcType_v4.DATE.getName());
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
                JPanel panel = new JPanelProgrammer_v4(this);
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
    } // end public setMenuBar

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

	public void setupOtherCalculatorButtons()
    {
        getButtonClear().setFont(font);
        getButtonClear().setPreferredSize(new Dimension(35, 35));
        getButtonClear().setBorder(new LineBorder(Color.BLACK));
        getButtonClear().setEnabled(true);
        getButtonClear().setName("C");
        getButtonClear().addActionListener(action -> {
            performClearButtonActions(action);
            if (getCalcType() == CalcType_v4.PROGRAMMER)
            {
                ((JPanelProgrammer_v4) getCurrentPanel()).resetProgrammerOperators();
            }
        });

        getButtonClearEntry().setFont(font);
        getButtonClearEntry().setPreferredSize(new Dimension(35, 35));
        getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        getButtonClearEntry().setEnabled(true);
        getButtonClearEntry().setName("CE");
        getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);

        getButtonDelete().setFont(font);
        getButtonDelete().setPreferredSize(new Dimension(35, 35));
        getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        getButtonDelete().setEnabled(true);
        getButtonDelete().setName("\u2190");
        getButtonDelete().addActionListener(this::performDeleteButtonActions);

        getButtonDot().setFont(font);
        getButtonDot().setPreferredSize(new Dimension(35, 35));
        getButtonDot().setBorder(new LineBorder(Color.BLACK));
        getButtonDot().setEnabled(true);
        getButtonDot().setName(".");
        getButtonDot().addActionListener(this::performDotButtonActions);
    }

	public void setupMemoryButtons()
    {
        getButtonMemoryStore().setFont(Calculator_v4.font);
        getButtonMemoryStore().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryStore().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryStore().setEnabled(true);
        getButtonMemoryStore().setName("MS");
        getButtonMemoryStore().addActionListener(this::performMemoryStoreActions);

        getButtonMemoryClear().setFont(Calculator_v4.font);
        getButtonMemoryClear().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryClear().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryClear().setEnabled(false);
        getButtonMemoryClear().setName("MC");
        getButtonMemoryClear().addActionListener(this::performMemoryClearActions);

        getButtonMemoryRecall().setFont(Calculator_v4.font);
        getButtonMemoryRecall().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryRecall().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryRecall().setEnabled(false);
        getButtonMemoryRecall().setName("MR");
        getButtonMemoryRecall().addActionListener(this::performMemoryRecallActions);

        getButtonMemoryAddition().setFont(Calculator_v4.font);
        getButtonMemoryAddition().setPreferredSize(new Dimension(35, 35));
        getButtonMemoryAddition().setBorder(new LineBorder(Color.BLACK));
        getButtonMemoryAddition().setEnabled(false);
        getButtonMemoryAddition().setName("M+");
        getButtonMemoryAddition().addActionListener(this::performMemoryAddActions);

        getButtonMemorySubtraction().setFont(Calculator_v4.font);
        getButtonMemorySubtraction().setPreferredSize(new Dimension(35, 35));
        getButtonMemorySubtraction().setBorder(new LineBorder(Color.BLACK));
        getButtonMemorySubtraction().setEnabled(false);
        getButtonMemorySubtraction().setName("M-");
        getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionActions);

    }

    /**
     * Handles the logic for setting up the buttons numbered 0-9.
     */
    public void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        getNumberButtons().forEach(button -> {
            button.setFont(font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performNumberButtonActions);
        });
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
        if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())) // if there is a number in the textArea
        {
            if (memoryPosition == 10) // reset to 0
            {
                memoryPosition = 0;
            }
            memoryValues[memoryPosition] = getTextAreaWithoutNewLineCharacters();
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
        textArea.setText(addNewLineCharacters(1) + memoryValues[memoryRecallPosition]);
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
        values[valuesPosition] = getTextAreaWithoutNewLineCharacters();
        memoryRecallPosition++;
        confirm("Recalling number in memory: " + memoryValues[(memoryRecallPosition-1)] + " at position: " + (memoryRecallPosition-1));
    }

    public void performMemoryClearActions(ActionEvent action)
    {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand());
        if (memoryPosition == 10 || StringUtils.isBlank(memoryValues[memoryPosition]))
        {
            getLogger().debug("Resetting memoryPosition to 0");
            memoryPosition = 0;
        }
        if (!isMemoryValuesEmpty())
        {
            getLogger().info("Clearing memoryValue["+memoryPosition+"] = " + getMemoryValues()[getMemoryPosition()]);
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
                setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
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
                    setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
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
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+"]: '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(getTextAreaWithoutNewLineCharacters())
                    + Double.parseDouble(memoryValues[(memoryPosition-1)]); // create result forced double
            LOGGER.info(getTextAreaWithoutNewLineCharacters() + " + " + memoryValues[(memoryPosition-1)] + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            getTextArea().setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
            setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
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
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+": '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(memoryValues[(memoryPosition-1)])
                    - Double.parseDouble(getTextAreaWithoutNewLineCharacters()); // create result forced double
            LOGGER.info(memoryValues[(memoryPosition-1)] + " - " + getTextAreaWithoutNewLineCharacters() + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            // update result in text area
            getTextArea().setText(addNewLineCharacters(1)+memoryValues[(memoryPosition-1)]);
            setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    public void performNumberButtonActions(ActionEvent action)
    {
        LOGGER.info("NumberButtonHandler_v2 started");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("button: " + buttonChoice);
        if (getCalcType() == BASIC) {
            performBasicCalculatorNumberButtonActions(buttonChoice);
        }
        else if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            performProgrammerCalculatorNumberButtonActions(buttonChoice);
        }
        //TODO: add more types here
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        confirm("NumberButtonHandler_v2() finishing");
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
        textArea.setText("\n0");
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
        resetOperators(false);
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
        textarea = new StringBuffer();
        textArea.setText("");
        if (values[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
            values[0] = "";
            addBool = false;
            subBool = false;
            mulBool = false;
            divBool = false;
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
        } else {
            values[1] = "";
        }
        buttonDot.setEnabled(true);
        textarea.append(textArea.getText());
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
        LOGGER.info("textarea: " + textarea);
        numberIsNegative = isNegativeNumber(values[valuesPosition]);
        // this check has to happen
        dotButtonPressed = isDecimal(textarea.toString());
        getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        updateTextareaFromTextArea();
        if (addBool == false && subBool == false && mulBool == false && divBool == false)
        {
            if (numberIsNegative == false)
            {
                // if no operator has been pushed; number is positive; number is whole
                if (isDotButtonPressed() == false)
                {
                    if (textarea.length() == 1)
                    { // ex: 5
                        getTextArea().setText("");
                        textarea = new StringBuffer().append(" ");
                        values[valuesPosition] = "";
                    }
                    else if (textarea.length() >= 2)
                    { // ex: 56 or 2346
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        getTextArea().setText(addNewLineCharacters(1)+String.valueOf(textarea));
                        values[valuesPosition] = textarea.toString();
                    }
                    LOGGER.debug("result: '" + textarea.toString().replaceAll("\n","") + "'");
                }
                // if no operator has been pushed; number is positive; number is decimal
                else if (isDotButtonPressed()) {
                    if (textarea.length() == 2) { // ex: 3. .... recall textarea looks like .3
                        textarea = new StringBuffer().append(textarea.substring(textarea.length()-1)); // ex: 3
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 2)); // ex: 3 or 0
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textarea.length() > 3) { // ex: 3.25 or 0.50 or 5.02
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 1)); // inclusive
                    }
                    LOGGER.info("output: " + textarea);
                        /*if (textarea.endsWith(".")) {
                            textarea = textarea.substring(0,textarea.length()-1);
                            //dotActive = false;
                            dotButtonPressed = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.startsWith(".")) {
                            textarea = textarea.substring(1,1);
                        } */
                    //textarea = clearZeroesAtEnd(textarea);
                    textArea.setText("\n" + textarea);
                    values[valuesPosition] = textarea.toString();
                }
            }
            else if (numberIsNegative) {
                // if no operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 2) { // ex: -3
                        textarea = new StringBuffer();
                        textArea.setText(textarea.toString());
                        values[valuesPosition] = "";
                    }
                    else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("output: " + textarea);
                }
                // if no operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 4) { // -3.2
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2
                        textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3
                        dotButtonPressed = false;
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
                        textarea.append(convertToPositive(textarea.toString())); // 3.00 or 0.00
                        textarea.append(textarea.substring(0, textarea.length())); // 3.0 or 0.0
                        textarea.append(clearZeroesAtEnd(textarea.toString())); // 3 or 0
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("output: " + textarea);
                }
            }

        }
        else if (addBool == true || subBool == true || mulBool == true || divBool == true)
        {
            if (numberIsNegative == false) {
                // if an operator has been pushed; number is positive; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 1) { // ex: 5
                        textarea = new StringBuffer();
                    } else if (textarea.length() == 2) {
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                    } else if (textarea.length() >= 2) { // ex: 56 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea = new StringBuffer().append(values[valuesPosition]);
                            addBool = false;
                            subBool = false;
                            mulBool = false;
                            divBool = false;
                        } else {
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        }
                    }
                    LOGGER.info("output: " + textarea);
                    textArea.setText("\n" + textarea);
                    values[valuesPosition] = textarea.toString();
                    confirm();
                }
                // if an operator has been pushed; number is positive; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 2) // ex: 3.
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                    else if (textarea.length() == 3) { // ex: 3.2 0.0
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                    } else if (textarea.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea = new StringBuffer().append(values[valuesPosition]);
                        } else {
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length() -1));
                            textarea.append(clearZeroesAtEnd(textarea.toString()));
                        }
                    }
                    LOGGER.info("output: " + textarea);
                    textArea.setText("\n"+textarea);
                    values[valuesPosition] = textarea.toString();
                    confirm();
                }
            } else if (numberIsNegative == true) {
                // if an operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 2) { // ex: -3
                        textarea = new StringBuffer();
                        textArea.setText(textarea.toString());
                        values[valuesPosition] = "";
                    } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea.append(values[valuesPosition]);
                        }
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                        textArea.setText("\n" + textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("textarea: " + textarea);
                    confirm();
                }
                // if an operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 4) { // -3.2
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2 or 0.0
                        textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                        textArea.setText(textarea + "-"); // 3- or 0-
                        values[valuesPosition] = "-" + textarea; // -3 or -0
                    } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.25 or 0.00
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length())); // 3.2 or 0.0
                        textarea = clearZeroesAtEnd(textarea.toString());
                        LOGGER.info("textarea: " + textarea);
                        if (textarea.equals("0")) {
                            textArea.setText(textarea.toString());
                            values[valuesPosition] = textarea.toString();
                        } else {
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                    }
                    LOGGER.info("textarea: " + textarea);
                }
            }
            resetOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        confirm();
    }

    public void performDotButtonActions(ActionEvent action)
    {
        LOGGER.info("DotButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot press dot button. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (StringUtils.isBlank(getTextAreaWithoutNewLineCharacters().strip()))
            {
                textarea.append("0"+buttonChoice);
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+"0");
            }
            else if (!isNegativeNumber(values[valuesPosition]))
            {
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+getTextAreaWithoutNewLineCharacters());
                textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters().replace(".","")
                        + buttonChoice);
            }
            else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
            {
                textarea = new StringBuffer().append(convertToPositive(values[valuesPosition]));
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+textarea+"-");
                LOGGER.info("TextArea: " + getTextArea().getText());
                textarea.append(".");
                textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            }
            values[valuesPosition] = textarea.toString();
            buttonDot.setEnabled(false);
            setDotButtonPressed(true);
            LOGGER.info("DotButtonHandler() finished");
            confirm();
        }
    }

    public void performLogicForDotButtonPressed(String buttonChoice)
    {
        if (!textarea.equals("") && isDotButtonPressed() && isNumberNegative())
        {
            textarea = new StringBuffer().append(convertToPositive(values[valuesPosition]));
            getTextArea().setText(addNewLineCharacters(1)+textarea+buttonChoice+"-");
            LOGGER.info("TextArea: " + getTextArea().getText());
            textarea.append(buttonChoice);
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[valuesPosition] = textarea.toString();
            dotButtonPressed = false;
        }
        else if (!textarea.equals("") && isDotButtonPressed() && !isNumberNegative())
        {
            textarea = new StringBuffer().append(values[valuesPosition] + buttonChoice);
            textArea.setText(addNewLineCharacters(1)+textarea.toString() );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = getTextAreaWithoutNewLineCharacters();
        }
        else if (!textarea.equals("") && !isDotButtonPressed())
        {
            textarea.append(values[valuesPosition] + buttonChoice);
            textArea.setText("\n" + textarea );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = textArea.getText().replaceAll("\n", "");
        }
    }

    public void performNumberButtonActions(String buttonChoice)
    {
        performInitialChecks();
        //getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        //updateTextareaFromTextArea();
        LOGGER.info("Performing basic actions...");
        if (!numberIsNegative && !isDotButtonPressed())
        {
            LOGGER.info("firstNumBool = true | positive number = true & dotButtonPressed = false");
            LOGGER.debug("before: " + getTextAreaWithoutNewLineCharacters());
            if (StringUtils.isBlank(textArea.getText()))
            {
                textArea.setText("\n" + buttonChoice);
            }
            else
            {
                textArea.setText("\n" + textArea.getText() + buttonChoice); // update textArea
            }
            setValuesToTextAreaValue();
            getTextArea().setText(addNewLineCharacters(1) + textArea.getText());
        }
        else if (numberIsNegative && !isDotButtonPressed())
        { // logic for negative numbers
            LOGGER.info("firstNumBool = true | negative number = true & dotButtonPressed = false");
            setTextareaToValuesAtPosition(buttonChoice);
        }
        else if (!numberIsNegative && isDotButtonPressed())
        {
            LOGGER.info("firstNumBool = true | negative number = false & dotButtonPressed = true");
            performLogicForDotButtonPressed(buttonChoice);
        }
        else
        {
            LOGGER.info("firstNumBool = true & dotButtonPressed = true");
            performLogicForDotButtonPressed(buttonChoice);
        }
    }

    public void performBasicCalculatorNumberButtonActions(String buttonChoice)
    {
        LOGGER.info("Starting basic calculator number button actions");
        if (!isFirstNumBool()) // do for second number
        {
            if (!isDotButtonPressed())
            {
                getTextArea().setText("");
                setTextarea(new StringBuffer().append(getTextArea().getText()));
                if (!isFirstNumBool()) {
                    setFirstNumBool(true);
                    setNumberIsNegative(false);
                } else
                    setDotButtonPressed(true);
                buttonDot.setEnabled(true);
            }
        }
        performNumberButtonActions(buttonChoice);
    }

    public void performProgrammerNumberButtonActions(String buttonChoice)
    {
	    performInitialChecks();
	    LOGGER.info("Performing programmer actions...");
	    if (getTextArea().getText().length() > getBytes())
	    {
	        return; // don't allow length to get any longer
        }
        if (textArea.getText().equals(""))
        {
            textArea.setText(addNewLineCharacters(1) + buttonChoice);
        }
        else
        {
            textArea.setText(addNewLineCharacters(1) + getTextAreaWithoutNewLineCharacters() + buttonChoice); // update textArea
        }
        updateTextareaFromTextArea();
        values[valuesPosition] = textarea.toString(); // store textarea in values
    }

    public void performProgrammerCalculatorNumberButtonActions(String buttonChoice)
    {
        LOGGER.info("Starting programmer calculator number button actions");
        if (firstNumBool)
        {
            performProgrammerNumberButtonActions(buttonChoice);
        }
        else
        {
            firstNumBool = true;
            textArea.setText("");
            textarea = new StringBuffer();
            valuesPosition = 1;
            performProgrammerNumberButtonActions(buttonChoice);
        }
    }

    public String addNewLineCharacters(int number)
    {
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<number; i++) {
	        sb.append("\n");
        }
	    return String.valueOf(sb);
    }

    public void updateTextareaFromTextArea()
    {
        setTextarea(new StringBuffer().append(getTextAreaWithoutNewLineCharacters()));
    }

    public void performInitialChecks()
    {
	    boolean checkFound = false;
        if (memAddBool || memSubBool)
        {
            textArea.setText("");
            checkFound = true;
        }
	    else if (textAreaContainsBadText())
	    {
            textArea.setText(addNewLineCharacters(1)+"0");
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
            numberIsNegative = false;
            checkFound = true;
        }
        else if (textArea.getText().equals("\n0") && getCalcType().equals(BASIC))
        {
            LOGGER.debug("textArea equals 0 no matter the form. setting to blank.");
            textArea.setText("");
            textarea = new StringBuffer().append("");
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

    /**
     *  Returns the results of the last action
     */
    public void confirm()
    {
	    confirm("", getCalcType());
    }

    public void confirm(String message)
    {
        confirm(message, getCalcType());
    }

    @Deprecated
    public void confirm(CalcType_v4 calcType)
    {
        confirm("", calcType);
    }

    /**
     *  Returns the results of the last action with a specific message to display
     *
     * @param message a message to send into the confirm results view
     */
    public void confirm(String message, CalcType_v4 calculatorType)
    {
        switch (calculatorType) {
            case BASIC : {
                if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
                else { LOGGER.info("Confirm Results"); }
                LOGGER.info("---------------- ");
                LOGGER.info("textarea: '\\n"+getTextareaWithoutNewLineCharacters()+"'");
                LOGGER.info("textArea: '"+getTextAreaWithoutNewLineCharacters()+"'");
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition]))
                { LOGGER.info("no memories stored!"); }
                else
                {for(int i = 0; i < 10; i++)
                    {if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues["+i+"]: " + memoryValues[i]);}}}
                LOGGER.info("addBool: '"+addBool+"'");
                LOGGER.info("subBool: '"+subBool+"'");
                LOGGER.info("mulBool: '"+mulBool+"'");
                LOGGER.info("divBool: '"+divBool+"'");
                LOGGER.info("orButtonBool: '" +orButtonBool+"'");
                LOGGER.info("modButtonBool: '" +modButtonBool+"'");
                LOGGER.info("xorButtonBool: '" +xorButtonBool+"'");
                LOGGER.info("notButtonBool: '" +notButtonBool+"'");
                LOGGER.info("andButtonBool: '" +andButtonBool+"'");
                LOGGER.info("values["+0+"]: '" + values[0] + "'");
                LOGGER.info("values["+1+"]: '" + values[1] + "'");
                LOGGER.info("values["+2+"]: '" + values[2] + "'");
                LOGGER.info("valuesPosition: '"+valuesPosition+"'");
                LOGGER.info("memoryPosition: '"+memoryPosition+"'");
                LOGGER.info("memoryRecallPosition: '"+memoryRecallPosition+"'");
                LOGGER.info("firstNumBool: '"+firstNumBool+"'");
                LOGGER.info("dotButtonPressed: '"+dotButtonPressed+"'");
                LOGGER.info("isNegative: '"+numberIsNegative+"'");
                LOGGER.info("calcType: '" + calcType + "'");
                LOGGER.info("calcBase: '" + base + "'");
                LOGGER.info("-------- End Confirm Results --------\n");
                break;
            }
            case PROGRAMMER : {
                if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
                else { LOGGER.info("Confirm Results"); }
                LOGGER.info("---------------- ");
                LOGGER.info("Add programmer info here");
                LOGGER.info("-------- End Confirm Results --------\n");
                break;
            }
            case SCIENTIFIC : { LOGGER.warn("Confirm message not setup for " + calculatorType); break; }
            case DATE : {
                if (StringUtils.isNotEmpty(message))
                {LOGGER.info("Confirm Results: " + message);}
                else
                {LOGGER.info("Confirm Results");}
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
                LOGGER.info("-------- End Confirm Results --------\n");
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
                LOGGER.info("-------- End Confirm Results --------\n");
                break;
            }
        }
    }

    /**
     * This method resets the 4 main operators to the boolean you pass in
     */
    public void resetOperators(boolean bool)
    {
        addBool = bool;
        subBool = bool;
        mulBool = bool;
        divBool = bool;
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
        if (operatorBool == true) {
            LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("values[0]: '" + values[0] + "'");
            valuesPosition = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            return false;
        } else {
            LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            valuesPosition = 0;
            dotButtonPressed = false;
            firstNumBool = true;
            return true;
        }
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
    protected StringBuffer clearZeroesAtEnd(String currentNumber)
    {
        LOGGER.info("starting clearZeroesAtEnd()");
        LOGGER.debug("currentNumber: " + currentNumber);
        textarea = new StringBuffer().append(textarea.toString().replaceAll("\n",""));
        boolean justZeroes = true;
        int index = 0;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) textarea = new StringBuffer().append(textarea);
        else {
            textarea = new StringBuffer().append(currentNumber.substring(0, index));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + String.valueOf(textarea));
        return new StringBuffer().append(String.valueOf(textarea));
    }

    public int getBytes()
    {
        if (isButtonByteSet) { return 8; }
        else if (isButtonWordSet) { return 16; }
        else if (isButtonDwordSet) { return 32; }
        else if (isButtonQwordSet) { return 64; }
        else { return 0; } // shouldn't ever come here
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
        boolean answer = !isNegativeNumber(result);
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

    protected String getTextAreaWithoutNewLineCharacters()
    {
        return getTextArea().getText().replaceAll("\n", "").strip();
    }

    protected String getTextareaWithoutNewLineCharacters()
    {
        return getTextarea().toString().replaceAll("\n", "").strip();
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

    @Deprecated
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
    public boolean textAreaContainsBadText()
    {
        boolean result = false;
        if (getTextAreaWithoutNewLineCharacters().equals("Invalid textarea")   ||
            getTextAreaWithoutNewLineCharacters().equals("Cannot divide by 0") ||
            getTextAreaWithoutNewLineCharacters().equals("Not a Number")       ||
            getTextAreaWithoutNewLineCharacters().equals("Only positive numbers") ||
            getTextAreaWithoutNewLineCharacters().contains("E") )
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
                getLogger().error("The path '" + path + "' could not find an image there after removing src/main/resources/!. Returning null!");
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
            getLogger().error(e.getMessage());
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
        textarea.append(values[valuesPosition]);
        LOGGER.debug("textarea: '", textarea + "'");
        textarea.append(textarea + buttonChoice); // update textArea
        LOGGER.debug("textarea: '" + textarea + "'");
        values[valuesPosition] = textarea.toString(); // store textarea
        textArea.setText("\n" + convertToPositive(textarea.toString()) + "-");
        LOGGER.debug("textArea: '" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    public void setValuesToTextAreaValue()
    {
        getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        updateTextareaFromTextArea();
        values[valuesPosition] = textarea.toString(); // store textarea in values
        LOGGER.debug("textArea: '\\n" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    /************* All Getters and Setters ******************/

    public Logger getLogger() { return LOGGER; }
    public long getSerialVersionUID() { return serialVersionUID; }
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
    public Font getFont() { return font; }
    public String[] getValues() { return values; }
    public String[] getMemoryValues() { return memoryValues; }
    public int getValuesPosition() { return valuesPosition; }
    public int getMemoryPosition() { return memoryPosition; }
    protected JTextArea getTextArea() { return this.textArea; }
    public StringBuffer getTextarea() { return textarea; }
    public CalcType_v4 getCalcType() { return this.calcType; }
    public JPanel getCurrentPanel() { return currentPanel; }
    public ImageIcon getCalculatorImage1() { return calculatorImage1; }
    public ImageIcon getCalculator2() { return calculator2; }
    public ImageIcon getMacLogo() { return macLogo; }
    public ImageIcon getWindowsLogo() { return windowsLogo; }
    public ImageIcon getBlankImage() { return blankImage; }
    public JLabel getIconLabel() { return iconLabel; }
    public JLabel getTextLabel() { return textLabel; }

    public boolean isFirstNumBool() { return firstNumBool; }
    public boolean isNumberOneNegative() { return numberOneNegative; }
    public boolean isNumberTwoNegative() { return numberTwoNegative; }
    public boolean isNumberThreeNegative() { return numberThreeNegative; }
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
    public CalcType_v4 getBase() { return base; }
    public JMenuBar getBar() { return bar; }

    public boolean isButtonBinSet() { return isButtonBinSet; }
    public boolean isButtonOctSet() { return isButtonOctSet; }
    public boolean isButtonDecSet() { return isButtonDecSet; }
    public boolean isButtonHexSet() { return isButtonHexSet; }
    public boolean isButtonByteSet() { return isButtonByteSet; }
    public boolean isButtonWordSet() { return isButtonWordSet; }
    public boolean isButtonDwordSet() { return isButtonDwordSet; }
    public boolean isButtonQwordSet() { return isButtonQwordSet; }

    public void setLayout(GridBagLayout layout) { this.layout = layout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    public void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setTextArea(JTextArea textArea) { this.textArea = textArea; }
    public void setTextarea(StringBuffer textarea) { this.textarea = textarea; }
    public void setCalcType(CalcType_v4 calcType) { this.calcType = calcType; }

    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setCalculatorImage1(ImageIcon calculatorImage1) { this.calculatorImage1 = calculatorImage1; }
    public void setCalculator2(ImageIcon calculator2) { this.calculator2 = calculator2; }
    public void setMacLogo(ImageIcon macLogo) { this.macLogo = macLogo; }
    public void setWindowsLogo(ImageIcon windowsLogo) { this.windowsLogo = windowsLogo; }
    public void setBlankImage(ImageIcon blankImage) { this.blankImage = blankImage; }
    public void setIconLabel(JLabel iconLabel) { this.iconLabel = iconLabel; }
    public void setTextLabel(JLabel textLabel) { this.textLabel = textLabel; }
    public void setFirstNumBool(boolean firstNumBool) { this.firstNumBool = firstNumBool; }
    public void setNumberOneNegative(boolean numberOneNegative) { this.numberOneNegative = numberOneNegative; }
    public void setNumberTwoNegative(boolean numberTwoNegative) { this.numberTwoNegative = numberTwoNegative; }
    public void setNumberThreeNegative(boolean numberThreeNegative) { this.numberThreeNegative = numberThreeNegative; }
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
    public void setButtonBinSet(boolean buttonBinSet) { isButtonBinSet = buttonBinSet; }
    public void setButtonOctSet(boolean buttonOctSet) { isButtonOctSet = buttonOctSet; }
    public void setButtonDecSet(boolean buttonDecSet) { isButtonDecSet = buttonDecSet; }
    public void setButtonHexSet(boolean buttonHexSet) { isButtonHexSet = buttonHexSet; }
    public void setButtonByteSet(boolean buttonByteSet) { isButtonByteSet = buttonByteSet; }
    public void setButtonWordSet(boolean buttonWordSet) { isButtonWordSet = buttonWordSet; }
    public void setButtonDwordSet(boolean buttonDwordSet) { isButtonDwordSet = buttonDwordSet; }
    public void setButtonQwordSet(boolean buttonQwordSet) { isButtonQwordSet = buttonQwordSet; }
    public void setOrButtonBool(boolean orButtonBool) { this.orButtonBool = orButtonBool; }
    public void setModButtonBool(boolean modButtonBool) { this.modButtonBool = modButtonBool; }
    public void setXorButtonBool(boolean xorButtonBool) { this.xorButtonBool = xorButtonBool; }
    public void setNegateButtonBool(boolean negateButtonBool) { this.negateButtonBool = negateButtonBool; }
    public void setNotButtonBool(boolean notButtonBool) { this.notButtonBool = notButtonBool; }
    public void setAndButtonBool(boolean andButtonBool) { this.andButtonBool = andButtonBool; }
    public void setBase(CalcType_v4 base) { this.base = base; }
    public void setBar(JMenuBar bar) { this.bar = bar; }

    public Collection<JButton> getNumberButtons() {
        return Arrays.asList(getButton0(), getButton1(), getButton2(), getButton3(), getButton4(), getButton5(),
                getButton6(), getButton7(), getButton8(), getButton9());
    }
    public Collection<JButton> getButtonClearEntryAndButtonDeleteAndButtonDot()
    {
        return Arrays.asList(getButtonClearEntry(), getButtonDelete(), getButtonDot());
    }

    public void clearVariableNumberOfButtonsFunctionalities()
    {
        getLogger().debug("Clear VariableNumber of Buttons");
        getButtonClearEntryAndButtonDeleteAndButtonDot().forEach(button -> {
            getLogger().debug("Removing action listener from button: " + button.getName());
            Arrays.stream(button.getActionListeners()).collect(Collectors.toList()).forEach(al -> {
                button.removeActionListener(al);
            });
        });
        getButtonClearEntryAndButtonDeleteAndButtonDot().forEach(button -> Arrays.stream(button.getKeyListeners()).collect(Collectors.toList()).forEach(button::removeKeyListener));
    }

    public void clearNumberButtonFunctionalities()
    {
        getLogger().debug("Number buttons cleared of action listeners");
        getNumberButtons().forEach(button -> Arrays.stream(button.getActionListeners()).collect(Collectors.toList()).forEach(al -> {
            getLogger().debug("Removing action listener from button: " + button.getName());
            button.removeActionListener(al);
        }));
    }

    public void logException(Exception e)
    {
        LOGGER.error(e.getCause().getClass().getName() + ": " + e.getMessage());
    }






    public CalcType_v4 determineCalcTypeBasedOnCurrentPanel() throws CalculatorError_v4
    {
        if (getCurrentPanel() instanceof JPanelBasic_v4) return BASIC;
        else if (getCurrentPanel() instanceof JPanelProgrammer_v4) return PROGRAMMER;
        else if (getCurrentPanel() instanceof JPanelScientific_v4) return SCIENTIFIC;
        else if (getCurrentPanel() instanceof JPanelDate_v4) return DATE;
        else if (getCurrentPanel() instanceof JPanelConverter_v4) return CONVERTER;
        else throw new CalculatorError_v4("Unknown Panel type: " + getCurrentPanel());
    }

    public void performTasksWhenChangingJPanels(JPanel currentPanel, CalcType_v4 calcType_v4, ConverterType_v4 converterType_v4) throws CalculatorError_v4
    {
        getLogger().info("Starting performTasksWhenChangingJPanels");
        boolean changedPanels = false;
        JPanel oldPanel = updateJPanel(currentPanel);
        if (converterType_v4 == null)
        {
            setTitle(calcType_v4.getName());
            getLogger().debug("oldPanel: " + oldPanel.getClass().getName());
            getLogger().debug("newPanel: " + getCurrentPanel().getClass().getName());
            // if oldPanel is same as newPanel, don't change
            if (oldPanel.getClass().getName().equals(getCurrentPanel().getClass().getName())) {}
            else if (getCurrentPanel() instanceof JPanelBasic_v4)
            {
                ((JPanelBasic_v4)getCurrentPanel()).performBasicCalculatorTypeSwitchOperations(oldPanel);
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
            setTitle(calcType_v4.getName());
            if (getCurrentPanel() instanceof JPanelConverter_v4)
            {
                ((JPanelConverter_v4)currentPanel).performConverterCalculatorTypeSwitchOperations(converterType_v4);
                changedPanels = true;
            }
        }
        setCurrentPanel(currentPanel);
        super.pack();
        getLogger().info("Finished performTasksWhenChangingJPanels\n");
        if (changedPanels) confirm("Switched Calculator Types", getCalcType());
        else confirm("Not changing panels when oldPanel("+oldPanel.getClass().getName()+") == newPanel("+getCurrentPanel().getClass().getName()+")", getCalcType());
    }

    public void performTasksWhenChangingJPanels(JPanel currentPanel, CalcType_v4 calcType_v4) throws CalculatorError_v4
    {
        performTasksWhenChangingJPanels(currentPanel, calcType_v4, null);
    }

    public Collection<JButton> getBasicOperationButtons()
    {
        return Arrays.asList(getButtonAdd(), getButtonSubtract(), getButtonMultiply(), getButtonDivide());
    }

    public void clearAllBasicOperationButtons()
    {
        getBasicOperationButtons().forEach(button -> {
            Arrays.stream(button.getActionListeners()).forEach(al -> button.removeActionListener(al));
        });
    }

    public Collection<JButton> getAllOtherBasicCalculatorButtons()
    {
        return Arrays.asList(getButtonEquals(), getButtonNegate(), getButtonClear(), getButtonClearEntry(),
                getButtonDelete(), getButtonDot(), getButtonMemoryAddition(), getButtonMemorySubtraction(),
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
        LOGGER.info("AddButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText())
            {
                textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + textarea);
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
                if (memorySwitchBool == true) {
                    memoryValues[memoryPosition] += " + ";
                }
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.error("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;

            //performValuesConversion();
            confirm();
        }
    }

    public void addition()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        values[0] = Double.toString(result);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole positive number");
            textarea = clearZeroesAtEnd(String.valueOf(result));
            values[0] = textarea.toString(); // textarea changed to whole number, or int
            textArea.setText("\n" + textarea.toString());
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            if (Double.parseDouble(values[0]) < 0.0 )
            {
                values[0] = formatNumber(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"] '" + values[valuesPosition] + "'");
            }
            else
            {
                textArea.setText("\n" + formatNumber(values[0]));
                values[0] = formatNumber(values[0]);
            }
        }
    }

    public void addition(CalcType_v4 calcType_v4)
    {
        if (getCalcType().equals(CalcType_v4.BASIC)) {
            addition();
        }
        else if (getCalcType().equals(CalcType_v4.PROGRAMMER)) {
            // convert values
//            convertFromTypeToType("Binary", "Decimal");
            // run add
            addition();
            textarea = new StringBuffer().append(convertFromTypeToTypeOnValues("Decimal", "Binary", String.valueOf(textarea))[0]);
            textArea.setText(addNewLineCharacters(1)+textarea);
        }
    }

    public void performSubtractionButtonActions(ActionEvent action)
    {
        LOGGER.info("SubtractButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText()) {
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + values[valuesPosition]);
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. next number is negative...");
                negatePressed = true;
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;
            performValuesConversion();
            confirm();
        }
    }
    public void subtract()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(Double.toString(result));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        {// if double == double, keep decimal and number afterwards
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: " + textarea);
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            }
            else {
                textArea.setText("\n" + formatNumber(values[0]));
            }
        }
    }
    public void subtract(CalcType_v4 calcType_v4)
    {
        if (calcType_v4.equals(CalcType_v4.BASIC)) {
            subtract();
        }
        else if (calcType_v4.equals(CalcType_v4.PROGRAMMER)) {
//            convertFromTypeToType("Binary", "Decimal");
            subtract();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performMultiplicationActions(ActionEvent action)
    {
        LOGGER.info("performMultiplicationActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false &&
                    divBool == false && !textArea.getText().equals("") &&
                    !textArea.getText().equals("Invalid textarea") &&
                    !textArea.getText().equals("Cannot divide by 0")) {
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText("\n" + " " + buttonChoice + " " + values[valuesPosition]);
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void multiply()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !values[0].contains("E") && !isNegativeNumber(values[0]))
        {
            textArea.setText(values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (values[0].contains("E"))
        {
            textArea.setText("\n" + values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            values[0] = textarea.toString(); // update storing
        }
        else if (isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(values[0]));
        }
        else
        {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
    }
    public void multiply(CalcType_v4 calcType_v4)
    {
        if (calcType_v4.equals(CalcType_v4.BASIC)) {
            multiply();
        }
        else if (calcType_v4.equals(CalcType_v4.PROGRAMMER)) {
            //convertFromTypeToType("Binary", "Decimal");
            multiply();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performDivideButtonActions(ActionEvent action)
    {
        LOGGER.info("performDivideButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform division. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textAreaContainsBadText())
            {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + values[valuesPosition]);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals(""))
            {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("") & !values[1].equals("0"))
            {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText())  {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true)
            {
                LOGGER.info("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void divide()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        if (!values[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            double result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            // TODO: fix logic here to tie in with existing logic above
            if (getCalcType().equals(CalcType_v4.PROGRAMMER)) {
                result = Double.valueOf(String.valueOf(clearZeroesAtEnd(String.valueOf(result))));
            } // PROGRAMMER mode only supports whole numbers at this time

            LOGGER.info(values[0] + " / " + values[1] + " = " + result);
            values[0] = Double.toString(result); // store result
            LOGGER.info("addBool: " + addBool);
            LOGGER.info("subBool: " + subBool);
            LOGGER.info("mulBool: " + mulBool);
            LOGGER.info("divBool: " + divBool);
            if (Double.parseDouble(values[0]) % 1 == 0 && !isNegativeNumber(values[0]))
            {
                // if int == double, cut off decimal and zero
                textArea.setText("\n" + values[0]);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
                values[0] = textarea.toString(); // update storing
                textArea.setText("\n" + values[0]);
                updateTextareaFromTextArea();
                if (Integer.parseInt(values[0]) < 0 ) {
                    textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", "")); // temp[2]
                    LOGGER.info("textarea: " + textarea);
                    textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                    LOGGER.info("textarea: " + textarea);
                    textArea.setText("\n" + textarea + "-"); // update textArea
                    updateTextareaFromTextArea();
                    LOGGER.info("values["+valuesPosition+"]: " + values[valuesPosition]);
                }
            }
            else if (Double.parseDouble(values[0]) % 1 == 0 && isNegativeNumber(values[0]))
            {
                LOGGER.info("We have a whole negative number");
                textarea = new StringBuffer().append(convertToPositive(values[0]));
                textarea = clearZeroesAtEnd(textarea.toString());
                textArea.setText(addNewLineCharacters(1)+textarea+"-");
                textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                values[0] = textarea.toString();
                dotButtonPressed = false;
                buttonDot.setEnabled(true);
            }
            else {
                // if double == double, keep decimal and number afterwards
                textArea.setText("\n" + formatNumber(values[0]));
            }


        }
        else if (values[1].equals("0")) {
            String result = "0";
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = result;
            firstNumBool = true;
        }
    }
    public void divide(CalcType_v4 calcType_v4)
    {
        if (calcType_v4.equals(CalcType_v4.BASIC)) {
            divide();
        }
        else if (calcType_v4.equals(CalcType_v4.PROGRAMMER))
        {
//            convertFromTypeToType("Binary", "Decimal");
            divide();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
        }
    }

    public void performButtonEqualsActions()
    {
        LOGGER.info("performButtonEqualsActions");
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (getCalcType() == CalcType_v4.BASIC)
        {
            if (addBool) {
                addition(getCalcType()); // addition();
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(getCalcType());
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(getCalcType());
                divBool = resetOperator(divBool);
            }
        }
        // TODO: is this necessary. values should always be in DECIMAL form
        else if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonBin().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonOct().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonDec().isSelected())
            {
                getLogger().debug("Do nothing");
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonHex().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[1])[0];
            }

            if (orButtonBool)
            {
                ((JPanelProgrammer_v4)getCurrentPanel()).performOr();
                getTextArea().setText(addNewLineCharacters(1) + values[0]);
                performValuesConversion();

            }
            else if (isModButtonPressed())
            {
                LOGGER.info("Modulus result");
                ((JPanelProgrammer_v4)getCurrentPanel()).performModulus();
                // update values and textArea accordingly
                performValuesConversion();
                valuesPosition = 0;
                modButtonBool = false;
            }
            // after converting to decimal, perform same logic
            else if (addBool) {
                addition(CalcType_v4.BASIC); // forced addition of Basic type
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(CalcType_v4.BASIC);
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(CalcType_v4.BASIC);
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(CalcType_v4.BASIC);
                divBool = resetOperator(divBool);
            }
        }

        //TODO: add more calculator types here

        if (values[0].equals("") && values[1].equals(""))
        {
            // if temp[0] and temp[1] do not have a number
            valuesPosition = 0;
        }
        else if (textAreaContainsBadText())
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
    public void performNegateButtonActions(ActionEvent action)
    {
        LOGGER.info("performNegateButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot negate number. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            getTextArea().setText(getTextAreaWithoutNewLineCharacters());
            updateTextareaFromTextArea();
            if (!textarea.equals("")) {
                if (numberIsNegative) {
                    textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                    getTextArea().setText("\n"+textarea);

                }
                else {
                    getTextArea().setText("\n"+textarea+"-");
                    textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                }
            }
            values[valuesPosition] = textarea.toString();
            confirm("");
        }
    }

    // TODO: move/use method in Calculator
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
        number = Double.valueOf(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }
    /*
     * TODO: Change method to be a list of tasks that should
     * occur when we switch panels.
     *
     * method: performTasksWhenChangingJPanels
     *
     * tasks: set currentPanel
     * 		  set title of frame
     *        set the mode of the calculator
     *        perform setup tasks based on mode
     */

    /**
     * This method returns the name of the Panel based on the current set CalcType
     * @return String the name of the Panel based on the CalcType
     */
    public String getNameOfPanel()
    {
        String nameOfPanel = new String();
        switch (getCalcType())
        {
            case BASIC: nameOfPanel = CalcType_v4.BASIC.getName(); break;
            case PROGRAMMER: nameOfPanel = CalcType_v4.PROGRAMMER.getName(); break;
            case SCIENTIFIC: nameOfPanel = CalcType_v4.SCIENTIFIC.getName(); break;
            case DATE: nameOfPanel = CalcType_v4.DATE.getName(); break;
            default: LOGGER.error("Unknown calculator type");
        }
        return nameOfPanel;
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

    @Deprecated
    public void convertAllValuesToDecimal()
    {
        if (getCalcType().equals(CalcType_v4.PROGRAMMER))
        {
            values = convertFromTypeToTypeOnValues("Binary", "Decimal", values);
        }
        // TODO: Add more CalcType_v3's here
    }

    @Deprecated
    public void convertAllValuesToBinary()
    {
        values = convertFromTypeToTypeOnValues("Decimal", "Binary", values);
//        values[1] = convertFromTypeToTypeOnValues("Decimal", "Binary", values[1])[0];
//        values[3] = convertFromTypeToTypeOnValues(CalcType_v3.DECIMAL.getName(), CalcType_v3.BINARY2.getName(), values[3])[0];
    }

    /**
     * This method returns true or false depending on if an operator was pushed or not
     * @return
     */
    public boolean determineIfMainOperatorWasPushed()
    {
        boolean result = false;
        if (isAddBool() || isSubBool() ||
                isMulBool() || isDivBool())
        {
            result = true;
        }
        return result;
    }

    /**
     * This method converts the memory values
     */
    public void convertMemoryValues()
    {
        String[] newMemoryValues = new String[10];
        int i = 0;
        if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            for(String numberInMemory : getMemoryValues())
            {
                newMemoryValues[i] = convertFromTypeToTypeOnValues(CalcType_v4.PROGRAMMER.getName(), CalcType_v4.BASIC.getName(), numberInMemory)[0];
                getLogger().debug("new number in memory is: " + newMemoryValues[i]);
                i++;
            }
        }
    }

    /**
     * This method takes CalcType Types, NOT BASES!
     * @param fromType
     * @param toType
     * @param strings
     * @return
     */
    public String[] convertFromTypeToTypeOnValues(String fromType, String toType, String... strings)
    {
        LOGGER.debug("convertFromTypeToTypeOnValues(from: '"+fromType+"', "+ "to: '"+toType+"' + " + Arrays.toString(strings));
        String[] arrToReturn = new String[strings.length];
        int countOfStrings = 0;
        if (StringUtils.isEmpty(strings[0])) return new String[]{"", "", "", ""};
        else countOfStrings = 1;
        if (fromType.equals(CalcType_v4.DECIMAL.getName()) && toType.equals(CalcType_v4.BINARY.getName()))
        {
            for(String str : Arrays.asList(strings))
            {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                int number = 0;
                try {
                    number = Integer.parseInt(str);
                    LOGGER.debug("number: " + number);
                    int i = 0;
                    while (i <= Integer.parseInt(str)) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for(int k = i; k<getBytes(); k++) {
                                sb.append("0");
                            }
                            break;
                        } else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for(int k = i+1; k<getBytes(); k++) {
                                sb.append("0");
                            } break;
                        }
                        i++;
                        number /= 2;
                    }
                } catch (NumberFormatException nfe) { LOGGER.error(nfe.getMessage()); }
                sb = sb.reverse();
                String strToReturn = sb.toString();
                LOGGER.debug("convertFrom("+fromType+")To("+toType+") = "+ sb);
                arrToReturn[countOfStrings-1] = strToReturn;
                countOfStrings++;
            }
        }
        else if (fromType.equals(CalcType_v4.BINARY.getName()) && toType.equals(DECIMAL.getName()))
        {
            for(String str : Arrays.asList(strings))
            {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                sb.append(str);

                int appropriateLength = getBytes();
                LOGGER.debug("sb: " + sb);
                LOGGER.debug("appropriateLength: " + appropriateLength);

                if (sb.length() < appropriateLength)
                {
                    if (sb.length() == 0) { arrToReturn[(countOfStrings-1)] = ""; LOGGER.debug("arrToReturn["+(countOfStrings-1)+"]: '" + arrToReturn[countOfStrings-1] + "'"); countOfStrings++; continue; }
                    LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                    // user had entered 101, which really is 00000101
                    // but they aren't showing the first 5 zeroes
                    int difference = appropriateLength - sb.length();
                    StringBuffer missingZeroes = new StringBuffer();
                    for (int i=0; i<difference; i++) {
                        missingZeroes.append("0");
                    }
                    sb.append(missingZeroes);
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

                arrToReturn[countOfStrings-1] = String.valueOf(Double.valueOf(result));


                if (isDecimal(arrToReturn[countOfStrings-1]))
                {
                    arrToReturn[countOfStrings-1] = String.valueOf(clearZeroesAtEnd(arrToReturn[countOfStrings-1]));
                    textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
                }
                LOGGER.debug("arrToReturn["+(countOfStrings - 1)+"]: " + arrToReturn[countOfStrings-1]);
                countOfStrings++;
            }
        }
        return arrToReturn;
    }

    @Deprecated
    public void convertFromTypeToType(String type1, String type2)
    {
        if (type1.equals("Binary") && type2.equals("Decimal")) {
            // converting both numbers in values if applicable
            if (!values[0].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[0].length()-1; i<values[0].length(); i++, k--) {
                        String c = Character.toString(values[0].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[0] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
            if (!values[1].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[1].length()-1; i<values[1].length(); i++, k--) {
                        String c = Character.toString(values[1].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[1] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
        }
    }

    /**
     * The purpose of this method is to determine at the end of a "cycle", values[0]
     * and values[1] are numbers in base 10.
     * It also determines if textArea and textarea are displayed properly.
     * Remember textArea is all characters, and textarea is simply the value represented
     */
    public void performValuesConversion()
    {
        // make sure no matter the mode, values[0] and values[1] are numbers and textArea's display correctly
        if (getCalcType() == CalcType_v4.PROGRAMMER) {
            if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonDec().isSelected())
            {
                LOGGER.debug("even though we are in programmer mode, decimal base is selected so no conversion is needed");
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonBin().isSelected())
            {
                LOGGER.debug("Programmer mode buttonBin selected");
                getTextArea().setText(addNewLineCharacters(1)+
                        convertFromTypeToTypeOnValues("Decimal", "Binary", getValues()[0])[0]);
                updateTextareaFromTextArea();
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonOct().isSelected()) {}
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonHex().isSelected()) {}
            else {
                convertAllValuesToDecimal();
            }
        }
        //TODO: add more Calctypes here

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
        values[2] = getTextAreaWithoutNewLineCharacters();
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
        confirm("Pressed Copy");
    }

    public void performPasteFunctionality(ActionEvent ae)
    {
        if (StringUtils.isEmpty(values[2]) && StringUtils.isBlank(values[2]))
            LOGGER.info("Values[2] is empty and blank");
        else
            LOGGER.info("Values[2]: " + values[2]);
        textArea.setText(addNewLineCharacters(1) + values[2]); // to paste
        values[valuesPosition] = getTextAreaWithoutNewLineCharacters();
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
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
}


