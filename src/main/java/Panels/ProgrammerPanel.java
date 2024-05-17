package Panels;

import Calculators.Calculator;
import Types.CalculatorBase;
import Types.CalculatorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Calculators.CalculatorError;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static Calculators.Calculator.*;
import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;

public class ProgrammerPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;

    private GridBagLayout programmerLayout;
    private GridBagConstraints constraints;
    private final JPanel
            btnGroupOnePanel = new JPanel(), // contains the first button group
            btnGroupTwoPanel = new JPanel(); // contains the second button group
    // Programmer specific buttons
    final static private JButton
            buttonModulus = new JButton("MOD"),
            buttonLPar = new JButton("("), buttonRPar = new JButton(")"),
            buttonRol = new JButton ("RoL"), buttonRor = new JButton ("RoR"),
            buttonOr = new JButton ("OR"), buttonXor = new JButton ("XOR"),
            buttonLSh = new JButton ("Lsh"), buttonRSh = new JButton ("Rsh"),
            buttonNot = new JButton ("NOT"), buttonAnd = new JButton ("AND"),
            buttonA = new JButton("A"), buttonB = new JButton("B"),
            buttonC = new JButton("C"), buttonD = new JButton("D"),
            buttonE = new JButton("E"), buttonF = new JButton("F");
    final static private JRadioButton
            buttonHex = new JRadioButton("Hex"), buttonDec = new JRadioButton("Dec"),
            buttonOct = new JRadioButton("Oct"), buttonBin = new JRadioButton("Bin"),
            buttonQWord = new JRadioButton("QWord"), buttonDWord = new JRadioButton("DWord"),
            buttonWord = new JRadioButton("Word"), buttonByte = new JRadioButton("Byte");
    private static boolean
            isBinaryBase = true, isOctalBase = false,
            isDecimalBase = false, isHexadecimalBase = false,
            isByteByte = true, isWordByte = false,
            isDWordByte = false, isQWordByte = false,
            isOrPressed = false, isModulusPressed = false,
            isXorPressed = false, negateButtonBool = false, // TODO: REMOVE negateButtonBool
            isNotPressed = false, isAndPressed = false;
    private static Calculator calculator;

    /************* Constructors ******************/

    /**
     * A zero argument constructor for creating a ProgrammerPanel
     */
    public ProgrammerPanel()
    {
        setName(PROGRAMMER.getValue());
        LOGGER.info("Empty Programmer panel created");
    }

    /**
     * A single argument constructor used to create a ProgrammerPanel
     * @param calculator the Calculator to use
     */
    public ProgrammerPanel(Calculator calculator)
    { this(calculator, null); }

    /**
     * The main constructor used to create a ProgrammerPanel
     * @param calculator the Calculator to use
     * @param base the CalculatorBase to use
     */
    public ProgrammerPanel(Calculator calculator, CalculatorBase base)
    {
        setupProgrammerPanel(calculator, base);
        LOGGER.info("Programmer panel created");
    }

    /************* Start of methods here ******************/

    /**
     * The main method used to define the ProgrammerPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     */
    public void setupProgrammerPanel(Calculator calculator, CalculatorBase base)
    {
        setCalculator(calculator);
        setCalculatorBase(base);
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setSize(new Dimension(425,375));
        setupProgrammerPanelComponents(base);
        addComponentsToPanel();
        setName(PROGRAMMER.getValue());
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up programmer panel");
    }

    /**
     * Clears button actions, sets the CalculatorType,
     * CalculatorBase, ConverterType, and finally
     * sets up the ProgrammerPanel and its components
     */
    private void setupProgrammerPanelComponents(CalculatorBase base)
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllBasicOperatorButtons(),
                        calculator.getAllBasicPanelOperatorButtons(),
                        calculator.getAllNumberButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .toList();

        allButtons.forEach(button -> Stream.of(button.getActionListeners())
                .forEach(button::removeActionListener));
        LOGGER.debug("Removed actions...");

        calculator.setupNumberButtons(); //required first for setProgrammerBase()
        calculator.setCalculatorType(PROGRAMMER);
        setProgrammerBase(base);
        calculator.setConverterType(null);
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupButtonBlank1();
        setupButtonModulus();
        setupButtonLPar();
        setupButtonRPar();
        setupButtonRol();
        setupButtonRor();
        setupButtonOr();
        setupButtonXor();
        setupButtonLSh();
        setupButtonRSh();
        setupButtonNot();
        setupButtonAnd();
        setupButtonsAToF();
        calculator.setupMemoryButtons(); // MR MC MS M+ M- H
        calculator.setupBasicPanelButtons();
//        calculator.setupDeleteButton();
//        calculator.setupClearEntryButton();
//        calculator.setupClearButton();
//        calculator.setupNegateButton();
//        calculator.setupSquareRootButton();
//        calculator.setupSquaredButton();
//        calculator.setupDotButton();
//        calculator.setupAdditionButton();
//        calculator.setupSubtractButton();
//        calculator.setupMultiplyButton();
//        calculator.setupDivideButton();
//        calculator.setupPercentButton();
//        calculator.setupFractionButton();
//        calculator.setupEqualsButton();
        resetProgrammerByteOperators(false);
        setByteByte(true);
        setupButtonGroupOne();
        setupButtonGroupTwo();
        LOGGER.info("Finished configuring the buttons");
    }

    /**
     * Sets the base to start the ProgrammerPanel in
     * @param base the CalculatorBase to use
     */
    private void setProgrammerBase(CalculatorBase base)
    {
        if (base == BASE_DECIMAL || StringUtils.isNotBlank(calculator.getValues()[0]))
        {
            calculator.setCalculatorBase(BASE_DECIMAL);
            buttonDec.setSelected(true);
            setDecimalBase(true);
            setButtons2To9(true);
            setButtonsAToF(false);
        }
        else if (base == BASE_OCTAL)
        {
            calculator.setCalculatorBase(base);
            setButtons2To9(true);
            calculator.getButton8().setEnabled(false);
            calculator.getButton9().setEnabled(false);
            setButtonsAToF(false);
            buttonOct.setSelected(true);
            setOctalBase(true);
        }
        else if (base == BASE_HEXADECIMAL)
        {
            calculator.setCalculatorBase(base);
            setButtons2To9(true);
            setButtonsAToF(true);
            buttonHex.setSelected(true);
            setHexadecimalBase(true);
        }
        else
        {
            calculator.setCalculatorBase(BASE_BINARY);
            setButtons2To9(false);
            setButtonsAToF(false);
            buttonBin.setSelected(true);
            setBinaryBase(true);
        }
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    private void addComponentsToPanel()
    {
        JPanel programmerPanel = new JPanel(new GridBagLayout());

        addComponent(programmerPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.PAGE_START);

        JPanel memoryPanel = new JPanel(new GridBagLayout());
        addComponent(memoryPanel, calculator.getButtonMemoryStore(), 0, new Insets(0,1,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryRecall(), 1, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryClear(), 2, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryAddition(), 3, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemorySubtraction(), 4, new Insets(0,0,0,0)); // right:1
        addComponent(memoryPanel, calculator.getButtonHistory(), 5, new Insets(0,0,0,1));

        addComponent(programmerPanel, memoryPanel, 0, new Insets(0,0,0,0));

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        addComponent(buttonsPanel, calculator.getButtonPercent(), 0, 0);
        addComponent(buttonsPanel, calculator.getButtonSquareRoot(), 0, 1);
        addComponent(buttonsPanel, calculator.getButtonSquared(), 0, 2);
        addComponent(buttonsPanel, calculator.getButtonFraction(), 0, 3);
        addComponent(buttonsPanel, calculator.getButtonClearEntry(), 1, 0);
        addComponent(buttonsPanel, calculator.getButtonClear(), 1, 1);
        addComponent(buttonsPanel, calculator.getButtonDelete(), 1, 2);
        addComponent(buttonsPanel, calculator.getButtonDivide(), 1, 3);
        addComponent(buttonsPanel, calculator.getButton7(), 2, 0);
        addComponent(buttonsPanel, calculator.getButton8(), 2, 1);
        addComponent(buttonsPanel, calculator.getButton9(), 2, 2);
        addComponent(buttonsPanel, calculator.getButtonMultiply(), 2, 3);
        addComponent(buttonsPanel, calculator.getButton4(), 3, 0);
        addComponent(buttonsPanel, calculator.getButton5(), 3, 1);
        addComponent(buttonsPanel, calculator.getButton6(), 3, 2);
        addComponent(buttonsPanel, calculator.getButtonSubtract(), 3, 3);
        addComponent(buttonsPanel, calculator.getButton1(), 4, 0);
        addComponent(buttonsPanel, calculator.getButton2(),4, 1);
        addComponent(buttonsPanel, calculator.getButton3(), 4, 2);
        addComponent(buttonsPanel, calculator.getButtonAdd(),4, 3);
        addComponent(buttonsPanel, calculator.getButtonNegate(), 5, 0);
        addComponent(buttonsPanel, calculator.getButton0(), 5, 1);
        addComponent(buttonsPanel, calculator.getButtonDecimal(), 5, 2);
        addComponent(buttonsPanel, calculator.getButtonEquals(), 5, 3);

        addComponent(programmerPanel, buttonsPanel, 2, 0);

        addComponent(programmerPanel);
        LOGGER.info("Buttons added to the frame");
    }

    /**
     * Adds a component to a panel
     * @param panel the panel to add to
     * @param c the component to add to a panel
     * @param row the row to place the component in
     * @param column the column to place the component in
     * @param insets the space between the component
     * @param gridWidth the number of columns the component should use
     * @param gridHeight the number of rows the component should use
     * @param weightXRow set to allow the button grow horizontally
     * @param weightYColumn set to allow the button grow horizontally
     * @param fill set to make the component resize if any unused space
     * @param anchor set to place the component in a specific location on the frame
     */
    private void addComponent(JPanel panel, Component c, int row, int column, Insets insets, int gridWidth, int gridHeight, double weightXRow, double weightYColumn, int fill, int anchor)
    {
        constraints.insets = insets == null ? new Insets(1, 1, 1, 1) : insets;
        constraints.gridy = row;
        constraints.gridx = column;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.weighty = weightXRow;
        constraints.weightx = weightYColumn;
        if (fill != 0)   constraints.fill = fill;
        if (anchor != 0) constraints.anchor = anchor;
        if (c != null) panel.add(c, constraints);
        else           add(panel, constraints);
    }

    /** Primarily used to add the textPane */
    private void addComponent(JPanel panel, Component c, int column, Insets insets)
    { addComponent(panel, c, 1, column, insets, 1, 1, 1.0, 1.0, 0, 0); }

    /** Primarily used to add the buttons to a panel */
    private void addComponent(JPanel panel, Component c, int row, int column)
    { addComponent(panel, c, row, column, null, 1, 1, 1.0, 1.0, 0, 0); }

    /** Primarily used to add the basicPanel to the frame */
    private void addComponent(JPanel panel)
    { addComponent(panel, null, 0, 0, new Insets(0,0,0,0), 0, 0, 1.0, 1.0, 0, GridBagConstraints.CENTER); }


//    /**
//     * Specifies where each button is placed on the ProgrammerPanel
//     */
//    private void addComponentsToPanel()
//    {
//        constraints.insets = new Insets(5,5,5,5);
//        addComponent(calculator.getTextPane(), 0, 0, 9, 2);
//        addComponent(btnGroupOnePanel, 4, 0, 1, 4, GridBagConstraints.HORIZONTAL);
//        addComponent(btnGroupTwoPanel, 7, 0, 1, 4, GridBagConstraints.HORIZONTAL);
//        constraints.insets = new Insets(5,0,1,5);
//        addComponent(calculator.getButtonBlank1(), 4, 1, 1, 1);
//        addComponent(buttonModulus, 4, 2, 1, 1);
//        addComponent(buttonA, 4, 3, 1, 1);
//        addComponent(calculator.getButtonMemoryStore(), 4, 4, 1, 1);
//        addComponent(calculator.getButtonMemoryClear(), 4, 5, 1, 1);
//        addComponent(calculator.getButtonMemoryRecall(), 4, 6, 1, 1);
//        addComponent(calculator.getButtonMemoryAddition(), 4, 7, 1, 1);
//        addComponent(calculator.getButtonMemorySubtraction(), 4, 8, 1, 1);
//        addComponent(buttonLPar, 5, 1, 1, 1);
//        addComponent(buttonRPar, 5, 2, 1, 1);
//        addComponent(buttonB, 5, 3, 1, 1);
//        addComponent(calculator.getButtonDelete(), 5, 4, 1, 1);
//        addComponent(calculator.getButtonClearEntry(), 5, 5, 1, 1);
//        addComponent(calculator.getButtonClear(), 5, 6, 1, 1);
//        addComponent(calculator.getButtonNegate(), 5, 7, 1, 1);
//        addComponent(calculator.getButtonSqrt(), 5, 8, 1, 1);
//        addComponent(buttonRol, 6, 1, 1, 1);
//        addComponent(buttonRor, 6, 2, 1, 1);
//        addComponent(buttonC, 6, 3, 1, 1);
//        addComponent(calculator.getButton7(), 6, 4, 1, 1);
//        addComponent(calculator.getButton8(), 6, 5, 1, 1);
//        addComponent(calculator.getButton9(), 6, 6, 1, 1);
//        addComponent(calculator.getButtonDivide(), 6, 7, 1, 1);
//        addComponent(calculator.getButtonPercent(), 6, 8, 1, 1);
//        addComponent(buttonOr, 7, 1, 1, 1);
//        addComponent(buttonXor, 7, 2, 1, 1);
//        addComponent(buttonD, 7, 3, 1, 1);
//        addComponent(calculator.getButton4(), 7, 4, 1, 1);
//        addComponent(calculator.getButton5(), 7, 5, 1, 1);
//        addComponent(calculator.getButton6(), 7, 6, 1, 1);
//        addComponent(calculator.getButtonMultiply(), 7, 7, 1, 1);
//        addComponent(calculator.getButtonFraction(), 7, 8, 1, 1);
//        addComponent(buttonLSh, 8, 1, 1, 1);
//        addComponent(buttonRSh, 8, 2, 1, 1);
//        addComponent(buttonE, 8, 3, 1, 1);
//        addComponent(calculator.getButton1(), 8, 4, 1, 1);
//        addComponent(calculator.getButton2(), 8, 5, 1, 1);
//        addComponent(calculator.getButton3(), 8, 6, 1, 1);
//        addComponent(calculator.getButtonSubtract(), 8, 7, 1, 1);
//        addComponent(calculator.getButtonEquals(), 8, 8, 1, 2);
//        addComponent(buttonNot, 9, 1, 1, 1);
//        addComponent(buttonAnd, 9, 2, 1, 1);
//        addComponent(buttonF, 9, 3, 1, 1);
//        addComponent(calculator.getButton0(), 9, 4, 2, 1);
//        addComponent(calculator.getButtonDot(), 9, 6, 1, 1);
//        addComponent(calculator.getButtonAdd(), 9, 7, 1, 1);
//        LOGGER.info("Buttons added to programmer panel");
//    }
//    /**
//     * Adding a component enforcing the GridBagConstraints.BOTH
//     * @param c the component to add
//     * @param row the row to add the component to
//     * @param column the column to add the component to
//     * @param width the number of columns the component takes up
//     * @param height the number of rows the component takes up
//     */
//    private void addComponent(Component c, int row, int column, int width, int height)
//    { addComponent(c, row, column, width, height, GridBagConstraints.BOTH); }
//    /**
//     * The main method to used to add a component to the Calculator frame
//     * @param c the component to add
//     * @param row the row to add the component to
//     * @param column the column to add the component to
//     * @param width the number of columns the component takes up
//     * @param height the number of rows the component takes up
//     * @param fill the constrains to use
//     */
//    private void addComponent(Component c, int row, int column, int width, int height, int fill)
//    {
//        constraints.gridx = column;
//        constraints.gridy = row;
//        constraints.gridwidth = width;
//        constraints.gridheight = height;
//        constraints.fill = fill;
//        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
//        constraints.weighty = 0;
//        constraints.weightx = 0;
//        programmerLayout.setConstraints(c, constraints); // set constraints
//        add(c); // add component
//    }

//    /**
//     * Calls the main setup method when switching
//     * from another panel to the ProgrammerPanel
//     * @param calculator the Calculator object
//     */
//    public void performProgrammerCalculatorTypeSwitchOperations(Calculator calculator, CalculatorBase base )
//    { setupProgrammerPanel(calculator, base); }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpMenu()
    {
        LOGGER.warn("IMPLEMENT Help Menu");
        JMenu helpMenuItem = calculator.getHelpMenu();
        JMenuItem viewHelp = helpMenuItem.getItem(0);
        // remove any and all other view help actions
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel("IMPLEMENT " + PROGRAMMER.getValue() + " Calculator Help"));
        helpMenuItem.add(viewHelp, 0);
        LOGGER.info("Finished setting up the help menu");
    }

    /**
     * Displays the help text in a scrollable pane
     * @param helpString the help text to display
     */
    private void showHelpPanel(String helpString)
    {
        JTextArea message = new JTextArea(helpString,20,40);
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(message, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(new Dimension(400, 300));
        SwingUtilities.updateComponentTreeUI(calculator);
        JOptionPane.showMessageDialog(calculator, scrollPane, "Viewing " + PROGRAMMER.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        calculator.confirm("Viewing " + BASIC.getValue() + " Calculator Help");
    }

    /**
     * The main method to set up the textPane
     */
    private void setupTextPane()
    {
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        calculator.setTextPane(new JTextPane());
        calculator.getTextPane().setParagraphAttributes(attribs, true);
        calculator.getTextPane().setFont(mainFont);
        if (calculator.isMotif())
        {
            calculator.getTextPane().setBackground(new Color(174,178,195));
            calculator.getTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        { calculator.getTextPane().setBorder(new LineBorder(Color.BLACK)); }
        calculator.getTextPane().setEditable(false);
        calculator.getTextPane().setPreferredSize(new Dimension(105, 60));
        LOGGER.info("TextPane configured");
    }

    /**
     * The main method to set up the Blank button
     */
    private void setupButtonBlank1()
    {
        calculator.getButtonBlank1().setPreferredSize(new Dimension(35,35));
        calculator.getButtonBlank1().setBorder(new LineBorder(Color.BLACK));
    }

    /**
     * The main method to set up the Modulus button
     */
    private void setupButtonModulus()
    {
        buttonModulus.setFont(Calculator.mainFont);
        buttonModulus.setPreferredSize(new Dimension(35,35));
        buttonModulus.setBorder(new LineBorder(Color.BLACK));
        buttonModulus.addActionListener(this::performButtonModActions);
    }
    /**
     * The actions to perform when you click MemorySubtraction
     * @param actionEvent the click action
     */
    public void performButtonModActions(ActionEvent actionEvent)
    {
        LOGGER.debug("performModButtonActions begins");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.debug("button: " + buttonChoice);
        LOGGER.debug("is it enabled? " + buttonModulus.isEnabled() + " Setting to false.");
        setModulusPressed(true);
        buttonModulus.setEnabled(false);
        if (calculator.getValues()[0].equals("") && calculator.getValues()[1].equals("") )
        {
            // do nothing
            LOGGER.debug("Doing nothing because...");
            LOGGER.debug("calculator.getValues()[0]: " + calculator.getValues()[0]);
            LOGGER.debug("calculator.getValues()[1]: " + calculator.getValues()[1]);
        }
        else if (!calculator.getValues()[0].equals("") &&
                !calculator.getValues()[1].equals(""))
        {
            LOGGER.debug("performing Modulus");
            performModulus();
        }
        else if (!calculator.getValues()[0].equals("") &&
                calculator.getValues()[1].equals(""))
        {
            // some value entered then pushed mod ... more input to come
            calculator.getTextPane().setText(calculator.getTextPane().getText() + " " + buttonChoice);
            //calculator.updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = calculator.getTextAreaWithoutNewLineCharacters();
            LOGGER.debug("setting setModButtonBool to true");
            setModulusPressed(true);
            calculator.setFirstNumber(false);
        }
        calculator.confirm("Modulus Actions finished");
    }

    /**
     * The inner logic for modulus
     */
    private void performModulus()
    {
        // some value mod another value returns result:  4 mod 3 == 1; 1 * 3 = 3; 4 - 3 = 1 == result
        int firstResult = Integer.parseInt(calculator.getValues()[0]) / Integer.parseInt(calculator.getValues()[1]); // create result forced double
        LOGGER.debug("firstResult: " + firstResult);
        int secondResult = (firstResult * Integer.parseInt(calculator.getValues()[1]));
        LOGGER.debug("secondResult: " + secondResult);
        int finalResult = Integer.parseInt(calculator.getValues()[0]) - secondResult;
        LOGGER.debug("modulus: " + finalResult);
        calculator.getValues()[0] = String.valueOf(finalResult);
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the LeftParentheses button
     */
    private void setupButtonLPar()
    {
        buttonLPar.setFont(Calculator.mainFont);
        buttonLPar.setPreferredSize(new Dimension(35,35));
        buttonLPar.setBorder(new LineBorder(Color.BLACK));
        buttonLPar.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the RightParentheses button
     */
    private void setupButtonRPar()
    {
        buttonRPar.setFont(Calculator.mainFont);
        buttonRPar.setPreferredSize(new Dimension(35,35));
        buttonRPar.setBorder(new LineBorder(Color.BLACK));
        buttonRPar.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the Rol button
     */
    private void setupButtonRol()
    {
        buttonRol.setFont(Calculator.mainFont);
        buttonRol.setPreferredSize(new Dimension(35,35));
        buttonRol.setBorder(new LineBorder(Color.BLACK));
        buttonRol.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the Ror button
     */
    private void setupButtonRor()
    {
        buttonRor.setFont(Calculator.mainFont);
        buttonRor.setPreferredSize(new Dimension(35,35));
        buttonRor.setBorder(new LineBorder(Color.BLACK));
        buttonRor.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    /**
     * The main method to set up the Or button
     */
    private void setupButtonOr()
    {
        buttonOr.setFont(mainFont);
        buttonOr.setPreferredSize(new Dimension(35,35));
        buttonOr.setBorder(new LineBorder(Color.BLACK));
        buttonOr.addActionListener(this::performButtonOrActions);
    }
    /**
     * The actions to perform when Or is clicked
     */
    public void performButtonOrActions(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setOrPressed(true);
        buttonOr.setEnabled(false);
//        if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isNotEmpty(calculator.getValues()[1]))
//        {
//            String msg = "calculator.getValues()[1] is set and not calculator.getValues()[0]. This is not allowed.";
//            throw new CalculatorError(msg);
//        }
//        else if below
        if (StringUtils.isNotEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            LOGGER.debug("getValues()[0] is set, but not getValues()[1]");
            calculator.setFirstNumber(false);
            calculator.getTextPane().setText(calculator.addNewLineCharacters()
                    + buttonChoice + " " + calculator.getValues()[0]);
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0] + " " + buttonChoice));
            calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            calculator.confirm("OR complete");
        }
        else if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            setOrPressed(false);
            calculator.setFirstNumber(true);
            calculator.confirm("Pressed OR. Doing nothing");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            String sb = performOr(); // 2 OR 3 OR button presses
            //TODO: need to convert sb to DECIMAL form before storing in getValues()
            calculator.getValues()[0] = sb;
            calculator.getTextPane().setText(calculator.addNewLineCharacters(1)+calculator.getValues()[0]);
            setOrPressed(false);
            calculator.setValuesPosition(0);
        }
    }

    /**
     * The inner logic for Or
     * @return String the result of the Or operation
     */
    private String performOr()
    {
        LOGGER.debug("performing Or");
        StringBuffer sb = new StringBuffer();
        //TODO: if getValues()[0] and getValues()[1] in decimal and difference length, this will fail.
        //In order for this to work, we need to convert to most appropriate base
        //Check to make sure both are same length

        for (int i=0; i<calculator.getValues()[0].length(); i++)
        {
            String letter = "0";
            if (String.valueOf(calculator.getValues()[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.getValues()[1].charAt(i)).equals("0") )
            { // if the characters at both getValues() at the same position are the same and equal 0
                letter = "0";
            }
            else
            {
                letter = "1";
            }
            sb.append(letter);
            LOGGER.info(calculator.getValues()[0].charAt(i)+" OR "+calculator.getValues()[1].charAt(i)+" = "+ letter);
        }
        calculator.getValues()[3] = sb.toString();
        //calculator.convertAllValuesToDecimal();
        LOGGER.info(calculator.getValues()[0]+" OR "+calculator.getValues()[1]+" = "+ calculator.getValues()[3]);
        calculator.getValues()[0] = calculator.getValues()[3];
        buttonOr.setEnabled(true);
        return String.valueOf(sb);
    }

    /**
     * The main method to set up the Xor button
     */
    private void setupButtonXor()
    {
        buttonXor.setFont(mainFont);
        buttonXor.setPreferredSize(new Dimension(35,35));
        buttonXor.setBorder(new LineBorder(Color.BLACK));
        buttonXor.addActionListener(this::performButtonXorActions);
    }
    /**
     * The actions to perform when Xor is clicked
     */
    public void performButtonXorActions(ActionEvent actionEvent)
    {
        LOGGER.info("performing XOR button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setXorPressed(true);
        buttonXor.setEnabled(false);
        if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1])) {

            calculator.confirm("No getValues() set");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters(1)+
                    calculator.getTextPaneWithoutNewLineCharacters() + " " + "XOR");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            performXor();
        }
    }
    /**
     * The inner logic for Xor
     * @return
     */
    private String performXor()
    {
        LOGGER.info("performing Xor");
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<calculator.getValues()[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(calculator.getValues()[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.getValues()[1].charAt(i)).equals("0") )
            { // if the characters at both getValues() at the same position are the same and equal 0
                letter = "0";
            }
            else
            {
                letter = "1";
            }
            sb.append(letter);
            LOGGER.info(calculator.getValues()[0].charAt(i) + " + " + calculator.getValues()[1].charAt(i)+ " = " + letter);
        }
        return String.valueOf(sb);
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the LeftShift button
     */
    private void setupButtonLSh()
    {
        buttonLSh.setFont(Calculator.mainFont);
        buttonLSh.setPreferredSize(new Dimension(35,35));
        buttonLSh.setBorder(new LineBorder(Color.BLACK));
        buttonLSh.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the RightShift button
     */
    private void setupButtonRSh()
    {
        buttonRSh.setFont(Calculator.mainFont);
        buttonRSh.setPreferredSize(new Dimension(35,35));
        buttonRSh.setBorder(new LineBorder(Color.BLACK));
        buttonRSh.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    /**
     * The main method to set up the Not button
     */
    private void setupButtonNot()
    {
        buttonNot.setFont(Calculator.mainFont);
        buttonNot.setPreferredSize(new Dimension(35,35));
        buttonNot.setBorder(new LineBorder(Color.BLACK));
        buttonNot.addActionListener(this::performButtonNotActions);
    }
    /**
     * The actions to perform when the Not button is clicked
     */
    public void performButtonNotActions(ActionEvent actionEvent)
    {
        LOGGER.info("performing not operation...");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setNotPressed(false);
        buttonNot.setEnabled(false);
        //calculator.setTextAreaValue(new StringBuffer(calculator.getTextArea().getText().replaceAll("\n", "")));
        LOGGER.debug("before operation execution: " + calculator.getTextPane().getText().toString());
        StringBuffer newBuffer = new StringBuffer();
        for (int i = 0; i < calculator.getTextPane().getText().length(); i++) {
            String s = Character.toString(calculator.getTextPane().getText().charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: " + newBuffer);
        //calculator.setTextAreaValue(new StringBuffer(newBuffer));
        calculator.getTextPane().setText("\n"+calculator.getTextPane().getText().toString());
        LOGGER.info("not operation completed.");
    }

    //TODO: Add actionListener
    /**
     * The main method to set up the And button
     */
    private void setupButtonAnd()
    {
        buttonAnd.setFont(Calculator.mainFont);
        buttonAnd.setPreferredSize(new Dimension(35,35));
        buttonAnd.setBorder(new LineBorder(Color.BLACK));
        //buttonAnd.addActionListener(this::performButtonAndActions);
    }

    /**
     * The main method to set up the first group
     * of radio buttons used for Base
     */
    private void setupButtonGroupOne()
    {
        ButtonGroup btnGroupOne = new ButtonGroup();
        btnGroupOnePanel.setLayout(new GridLayout(4,1));
        btnGroupOnePanel.add(buttonHex);
        btnGroupOnePanel.add(buttonDec);
        btnGroupOnePanel.add(buttonOct);
        btnGroupOnePanel.add(buttonBin);
        Border border = btnGroupOnePanel.getBorder();
        Border margin = new TitledBorder("Base");
        btnGroupOnePanel.setBorder(new CompoundBorder(border, margin));
        btnGroupOne.add(buttonHex);
        btnGroupOne.add(buttonDec);
        btnGroupOne.add(buttonOct);
        btnGroupOne.add(buttonBin);
        buttonHex.addActionListener(this::performBaseButtonSwitch);
        buttonDec.addActionListener(this::performBaseButtonSwitch);
        buttonOct.addActionListener(this::performBaseButtonSwitch);
        buttonBin.addActionListener(this::performBaseButtonSwitch);
    }
    /**
     * The actions to perform when you change bases
     * @param actionEvent the click action
     */
    public void performBaseButtonSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Changing bases...");
        CalculatorBase previousBase = calculator.getCalculatorBase();
        String baseChoice = actionEvent.getActionCommand();
        CalculatorBase newBase = determineCalculatorBase(baseChoice);
        //calculator.setCalculatorBase(newBase);
        String nameOfOperatorPushed = determineIfProgrammerPanelOperatorWasPushed(); // could be blank
        LOGGER.info("Operator {} will be cleared", nameOfOperatorPushed);
        calculator.resetBasicOperators(false);
        resetProgrammerByteOperators(false);
        resetProgrammerOperators();
        LOGGER.info("baseChoice: " + newBase);
        if (newBase == BASE_BINARY)
        {
            calculator.setCalculatorBase(BASE_BINARY);
            buttonBin.setSelected(true);
            setByteByte(true);
            setButtons2To9(false);
            getAToFButtons().forEach(button -> button.setEnabled(false));
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText());
        }
        else if (newBase == BASE_OCTAL)
        {
            buttonOct.setSelected(true);
            setOctalBase(true);
            setButtons2To9(true);
            calculator.getButton8().setEnabled(false);
            calculator.getButton9().setEnabled(false);
            getAToFButtons().forEach(button -> button.setEnabled(false));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        else if (newBase == BASE_DECIMAL)
        {
            //calculator.resetProgrammerByteOperators(false);
            buttonByte.setSelected(true);
            setByteByte(true);
            buttonDec.setSelected(true);
            setDecimalBase(true);
            setButtons2To9(true);
            setButtonsAToF(false);
            calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + calculator.getValues()[calculator.getValuesPosition()]);
            calculator.setCalculatorBase(BASE_DECIMAL);// we don't always "update getTextArea()", like when it's blank
        }
        else // newBase == HEXIDECIMAL
        {
            buttonHex.setSelected(true);
            setHexadecimalBase(true);
            setButtons2To9(true);
            getAToFButtons().forEach(button -> button.setEnabled(true));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        calculator.confirm("Bases changed");
    }

    /**
     * The main method to set up the first group
     * of radio buttons used for Byte
     */
    private void setupButtonGroupTwo()
    {
        ButtonGroup btnGroupTwo = new ButtonGroup();
        btnGroupTwoPanel.setLayout(new GridLayout(4,1)); //rows and columns
        btnGroupTwoPanel.add(buttonQWord);
        btnGroupTwoPanel.add(buttonDWord);
        btnGroupTwoPanel.add(buttonWord);
        btnGroupTwoPanel.add(buttonByte);
        Border border2 = btnGroupTwoPanel.getBorder();
        Border margin2 = new TitledBorder("Byte");
        btnGroupTwoPanel.setBorder(new CompoundBorder(border2, margin2));
        btnGroupTwo.add(buttonQWord);
        btnGroupTwo.add(buttonDWord);
        btnGroupTwo.add(buttonWord);
        btnGroupTwo.add(buttonByte);
        buttonQWord.addActionListener(this::performByteButtonSwitch);
        buttonDWord.addActionListener(this::performByteButtonSwitch);
        buttonWord.addActionListener(this::performByteButtonSwitch);
        buttonByte.addActionListener(this::performByteButtonSwitch);
        buttonByte.setSelected(true);
    }

    /**
     * The actions to perform when you change bytes
     * @param actionEvent the click action
     */
    public void performByteButtonSwitch(ActionEvent actionEvent)
    {
        LOGGER.info("Performing byte button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.debug("buttonChoice: " + buttonChoice);
        resetProgrammerByteOperators(false);
        switch (buttonChoice) {
            case "Byte":
                buttonByte.setEnabled(true);
                setByteByte(true);
                break;
            case "Word":
                buttonWord.setEnabled(true);
                setWordByte(true);
                break;
            case "DWord":
                buttonDWord.setEnabled(true);
                setDWordByte(true);
                break;
            case "QWord":
                buttonQWord.setEnabled(true);
                setQWordByte(true);
                break;
        }
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The method used to update the textPane representation of
     * the value stored in the Calculators values position independent
     * of the actual value and the Base representation
     * @param previousBase the previous base
     * @param newBase the new base
     * @param nameOfOperatorPushed the operator in the textPane if present
     */
    private void updateTextAreaAfterBaseChange(CalculatorBase previousBase, CalculatorBase newBase, String nameOfOperatorPushed)
    {
        String textAreaValueToConvert = calculator.getTextPaneWithoutAnyOperator();
        if (StringUtils.isNotBlank(textAreaValueToConvert)) {
            if (StringUtils.isEmpty(textAreaValueToConvert)) {
                textAreaValueToConvert = "0";
            }
            try {
                textAreaValueToConvert = calculator.convertFromTypeToTypeOnValues(previousBase, newBase, textAreaValueToConvert);
                if (StringUtils.equals(textAreaValueToConvert, "") || textAreaValueToConvert == null)
                {
                    textAreaValueToConvert = calculator.getTextPaneWithoutAnyOperator();
                }
            }
            catch (CalculatorError c) { calculator.logException(c); }
        } else {
            textAreaValueToConvert = "";
        }
        if (StringUtils.isBlank(nameOfOperatorPushed))
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + textAreaValueToConvert);
        }
        else
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + nameOfOperatorPushed + " " + textAreaValueToConvert);
        }
        //calculator.updateTextAreaValueFromTextArea();
    }

    /**
     * The method used to determine the CalculatorBase when clicking
     * on the Base buttons
     * @param base the chosen base
     * @return CalculatorBase, the base chosen
     */
    private CalculatorBase determineCalculatorBase(String base)
    {
        CalculatorBase baseToReturn = null;
        switch (base)
        {
            case "Bin" : {
                baseToReturn = BASE_BINARY;
                break;
            }
            case "Oct" : {
                baseToReturn = BASE_OCTAL;
                break;
            }
            case "Dec" : {
                baseToReturn = BASE_DECIMAL;
                break;
            }
            case "Hex" : {
                baseToReturn = BASE_HEXADECIMAL;
                break;
            }
            default : { LOGGER.error("Unknown base: " + base); }
        }
        return baseToReturn;
    }

    /**
     * Clears all functionality on the base buttons
     */
    private void clearBasesFunctionality()
    { getBaseButtons().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener)); }

    /**
     * Returns all CalculatorBase buttons
     * @return Collection of CalculatorBases
     */
    public Collection<JRadioButton> getBaseButtons()
    { return Arrays.asList(buttonBin, buttonOct, buttonDec, buttonHex); }

    /**
     * Clears all functionality on the byte buttons
     */
    private void clearBytesFunctionality()
    { getByteButtons().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener)); }

    /**
     * Returns all CalculatorByte buttons
     * @return Collection of CalculatorBytes
     */
    public Collection<JRadioButton> getByteButtons()
    { return Arrays.asList(buttonByte, buttonWord, buttonDWord, buttonQWord); }

    /**
     * The main method to set up the SquareRoot button
     */
    private void setupSquareRootButton()
    {
        calculator.getButtonSquareRoot().setFont(mainFont);
        calculator.getButtonSquareRoot().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSquareRoot().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSquareRoot().setEnabled(true);
        calculator.getButtonSquareRoot().addActionListener(this::performSquareRootButtonActions);
    }
    /**
     * The actions to perform when the SquareRoot button is clicked
     * @param actionEvent the click action
     */
    public void performSquareRootButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        String errorStringNaN = "Not a Number";
        LOGGER.debug("text: " + calculator.getTextPane().getText().replace("\n",""));
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform square root operation. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            if (calculator.getTextPane().getText().equals("") || calculator.isNegativeNumber(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText("\n"+errorStringNaN);
                //calculator.setTextAreaValue(new StringBuffer().append("\n").append(errorStringNaN));
                calculator.confirm(errorStringNaN + "Cannot perform square root operation on blank/negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.valueOf(calculator.getTextPane().getText())));
                result = calculator.formatNumber(result);
                calculator.getTextPane().setText("\n"+result);
                //calculator.setTextAreaValue(new StringBuffer().append(result));
                calculator.confirm("Pressed: " + buttonChoice);
            }
        }
    }

//    /**
//     * The main method to set up all number buttons, 0-9
//     */
//    private void setupNumberButtons(boolean isEnabled)
//    {
//        AtomicInteger i = new AtomicInteger(0);
//        calculator.getNumberButtons().forEach(button -> {
//            button.setFont(Calculator.mainFont);
//            button.setEnabled(isEnabled);
//            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
//            else { button.setPreferredSize(new Dimension(35, 35)); }
//            button.setBorder(new LineBorder(Color.BLACK));
//            button.setName(String.valueOf(i.getAndAdd(1)));
//            button.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
//            // setup in the panel, panel specific
//        });
//    }
    /**
     * The main method to set up the hexadecimal number buttons, A-F
     */
    private static void setupButtonsAToF()
    {
        getAToFButtons().forEach(hexadecimalNumberButton -> {
            hexadecimalNumberButton.setFont(Calculator.mainFont);
            hexadecimalNumberButton.setPreferredSize(new Dimension(35, 35));
            hexadecimalNumberButton.setBorder(new LineBorder(Color.BLACK));
            hexadecimalNumberButton.addActionListener(ProgrammerPanel::performProgrammerCalculatorNumberButtonActions);
        });
    }
    /**
     * The actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public static void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer number button actions");
        LOGGER.info("buttonChoice: " + actionEvent.getActionCommand());
        if (!calculator.isFirstNumber())
        {
            calculator.setFirstNumber(true);
            calculator.getTextPane().setText("");
            //calculator.textarea = new StringBuffer();
            calculator.setValuesPosition(1);
        }
        try {
            performInnerNumberActions(actionEvent);
        } catch (CalculatorError c) { calculator.logException(c); }
    }

    /**
     * The inner logic for number buttons
     * @param actionEvent the click action
     * @throws CalculatorError if there is a CalculatorException that occurs
     */
    private static void performInnerNumberActions(ActionEvent actionEvent) throws CalculatorError
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing programmer number button actions...");
        LOGGER.info("Update bytes based on length of number in getTextArea()");
        LOGGER.info("Adding '{}' to getTextArea()", buttonChoice);
        if (calculator.getCalculatorBase() == BASE_BINARY) {
            int lengthOfTextArea = calculator.getTextPaneWithoutNewLineCharacters().length();
            if (lengthOfTextArea == 8 || lengthOfTextArea == 17 || lengthOfTextArea == 26 || lengthOfTextArea == 35 || lengthOfTextArea == 44)
            {   // add a space add the "end" if the length of the number matches the bytes
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(buttonChoice).append(" ").append(calculator.getTextPaneWithoutNewLineCharacters());
                calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + newNumber);
                // update the bytes here or when we add next number
                if (isByteByte) { resetProgrammerByteOperators(false); isWordByte = (true); buttonWord.setSelected(true); }
                else if (isWordByte) { resetProgrammerByteOperators(false); isDWordByte =(true); buttonDWord.setSelected(true); }
                else if (isDWordByte && lengthOfTextArea == 17) {
                    resetProgrammerByteOperators(false);
                    isQWordByte = (true);
                    buttonQWord.setSelected(true);
                }
                else if (lengthOfTextArea == 35) {
                    resetProgrammerByteOperators(false);
                    isQWordByte =(true);
                    buttonQWord.setSelected(true);
                }
            }
            else if (lengthOfTextArea >= 53) { LOGGER.info("No more entries aloud"); }
            else {
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(calculator.getTextPaneWithoutNewLineCharacters());
                if (calculator.getTextPaneWithoutNewLineCharacters().contains(" ")) {
                    String[] bytes = calculator.getTextPaneWithoutNewLineCharacters().split(" ");
                    newNumber = new StringBuffer();
                    for (int i = 1; i < bytes.length; i++) {
                        newNumber.append(bytes[i]).append(" "); // 10000000' '
                    }
                    newNumber.append(bytes[0]); // 10000000' '1
                    newNumber.append(buttonChoice); // 10000000' '10
                    bytes[0] = bytes[0] + buttonChoice;
                    bytes = newNumber.toString().split(" "); // '10000000', '10'
                    newNumber = new StringBuffer();
                    for (int i = bytes.length-1; i > 0; i--) {
                        newNumber.append(bytes[i]).append(" "); // 10' '
                    }
                    newNumber.append(bytes[0]); // 10' '10000000
                }
                else {
                    newNumber.append(buttonChoice);
                }
                calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + newNumber);
            }
        }
        else if (calculator.getCalculatorBase() == BASE_DECIMAL) {
            if (!calculator.isFirstNumber()) // do for second number
            {
                if (!calculator.isDotPressed())
                {
                    calculator.getTextPane().setText("");
                    if (!calculator.isFirstNumber()) {
                        calculator.setFirstNumber(true);
                        calculator.setNumberNegative(false);
                    } else
                        //calculator.setDotPressed(true);
                    calculator.getButtonDecimal().setEnabled(true);
                }
            }

            // TODO: Determine how to best call BasicPanel.performNumberButtonActions()
            //BasicPanel.performNumberButtonActions(actionEvent);
            if (!calculator.isFirstNumber()) // second number
            {
                if (!calculator.isDotPressed())
                {
                    calculator.getTextPane().setText("");
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText()));
                    if (!calculator.isFirstNumber()) {
                        calculator.setFirstNumber(true);
                        calculator.setNumberNegative(false);
                    }
                    else //calculator.setDotPressed(true);
                    calculator.getButtonDecimal().setEnabled(true);
                }
            }
            calculator.performInitialChecks();
            if (calculator.isPositiveNumber(buttonChoice) && !calculator.isDotPressed())
            {
                LOGGER.info("positive number & dot button was not pushed");
                //LOGGER.debug("before: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
                if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]))
                {
                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaWithoutNewLineCharacters()));
                    calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                }
                else
                {
                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice).reverse());
                    //calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().reverse().toString();
                }
                //LOGGER.debug("after: '" + calculator.getTextArea().getText() + "'");
                //calculator.setValuesToTextAreaValue();
                //calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                //calculator.updateTheTextAreaBasedOnTheTypeAndBase();
                ////calculator.updateTextAreaValueFromTextArea();
            }
            else if (calculator.isNumberNegative() && !calculator.isDotPressed())
            { // logic for negative numbers
                LOGGER.info("negative number & dot button had not been pushed");
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()] + buttonChoice; // store in values, values[valuesPosition] + buttonChoice
                if (calculator.isNegating())
                {
                    if (!calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]))
                    {
                        LOGGER.debug("Number not yet showing as negative");
                        calculator.getValues()[calculator.getValuesPosition()] = calculator.convertToNegative(calculator.getValues()[calculator.getValuesPosition()]);
                        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])); //values[valuesPosition]
                        calculator.setNegating(calculator.isSubtracting() && calculator.isNumberNegative());
                    }
                    else
                    {
                        LOGGER.debug("Number is already showing as negative");
                        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])); //values[valuesPosition]
                        calculator.setNegating(calculator.isSubtracting() && calculator.isNumberNegative());
                    }
                }
            }
            else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]))
            {
                LOGGER.info("positive number & dot button had been pushed");
                //calculator.performLogicForDotButtonPressed(buttonChoice);
                performDot(buttonChoice);
            }
            else
            {
                LOGGER.info("dot button was pushed");
                //calculator.performLogicForDotButtonPressed(buttonChoice);
                performDot(buttonChoice);
            }
            calculator.confirm("Pressed " + buttonChoice);
        }
        else if (calculator.getCalculatorBase() == BASE_OCTAL) { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else /* (calculator.getCalculatorBase() == HEXADECIMAL */ { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }
    }
    /**
     * Returns all hexadecimal number buttons
     * @return Collection of hexadecimal number buttons
     */
    public static Collection<JButton> getAToFButtons()
    { return Arrays.asList(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF); }

    /**
     * The main method to set up the Memory buttons
     */
    private void setupMemoryButtons()
    {
        calculator.getAllMemoryPanelButtons().forEach(memoryButton -> {
            memoryButton.setFont(mainFont);
            memoryButton.setPreferredSize(new Dimension(35, 35));
            memoryButton.setBorder(new LineBorder(Color.BLACK));
            memoryButton.setEnabled(false);
        });
        calculator.getButtonMemoryClear().setName("MC");
        calculator.getButtonMemoryClear().addActionListener(this::performMemoryClearActions);
        LOGGER.info("Memory Clear button configured");
        calculator.getButtonMemoryRecall().setName("MR");
        calculator.getButtonMemoryRecall().addActionListener(this::performMemoryRecallActions);
        LOGGER.info("Memory Recall button configured");
        calculator.getButtonMemoryAddition().setName("M+");
        calculator.getButtonMemoryAddition().addActionListener(this::performMemoryAddActions);
        LOGGER.info("Memory Add button configured");
        calculator.getButtonMemorySubtraction().setName("M-");
        calculator.getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionActions);
        LOGGER.info("Memory Subtract button configured");
        calculator.getButtonMemoryStore().setEnabled(true); // Enable memoryStore
        calculator.getButtonMemoryStore().setName("MS");
        calculator.getButtonMemoryStore().addActionListener(this::performMemoryStoreActions);
        LOGGER.info("Memory Store button configured");
        // reset buttons to enabled if memories are saved
        if (!calculator.getMemoryValues()[0].isEmpty())
        {
            calculator.getButtonMemoryClear().setEnabled(true);
            calculator.getButtonMemoryRecall().setEnabled(true);
            calculator.getButtonMemoryAddition().setEnabled(true);
            calculator.getButtonMemorySubtraction().setEnabled(true);
        }
    }
    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public void performMemoryStoreActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryStoreButtonHandler started");
        LOGGER.info("button: " + actionEvent.getActionCommand()); // print out button confirmation
        if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharacters())) // if there is a number in the textArea
        {
            if (calculator.getMemoryPosition() == 10) // reset to 0
            {
                calculator.setMemoryPosition(0);
            }
            calculator.getMemoryValues()[calculator.getMemoryPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
            calculator.setMemoryPosition(calculator.getMemoryPosition() + 1);
            calculator.getButtonMemoryRecall().setEnabled(true);
            calculator.getButtonMemoryClear().setEnabled(true);
            calculator.getButtonMemoryAddition().setEnabled(true);
            calculator.getButtonMemorySubtraction().setEnabled(true);
            calculator.confirm(calculator.getValues()[calculator.getValuesPosition()] + " is stored in memory at position: " + (calculator.getMemoryPosition()-1));
        }
        else
        { calculator.confirm("No number added to memory. Blank entry"); }
    }
    /**
     * The actions to perform when MemoryRecall is clicked
     * @param actionEvent the click action
     */
    public void performMemoryRecallActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryRecallButtonHandler started");
        LOGGER.info("button: " + actionEvent.getActionCommand()); // print out button confirmation
        if (calculator.getMemoryRecallPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]))
        {
            calculator.setMemoryRecallPosition(0);
        }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]);
        calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
        calculator.confirm("Recalling number in memory: " + calculator.getMemoryValues()[(calculator.getMemoryRecallPosition()-1)] + " at position: " + (calculator.getMemoryRecallPosition()-1));
    }
    /**
     * The actions to perform when MemoryClear is clicked
     * @param actionEvent the click action
     */
    public void performMemoryClearActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (calculator.getMemoryPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryPosition()]))
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            calculator.setMemoryPosition(0);
        }
        if (!calculator.isMemoryValuesEmpty())
        {
            LOGGER.info("Clearing memoryValue["+calculator.getMemoryPosition()+"] = " + calculator.getMemoryValues()[calculator.getMemoryPosition()]);
            calculator.getMemoryValues()[calculator.getMemoryPosition()] = "";
            calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
            // MemorySuite now could potentially be empty
            if (calculator.isMemoryValuesEmpty())
            {
                calculator.setMemoryPosition(0);
                calculator.setMemoryRecallPosition(0);
                calculator.getButtonMemoryClear().setEnabled(false);
                calculator.getButtonMemoryRecall().setEnabled(false);
                calculator.getButtonMemoryAddition().setEnabled(false);
                calculator.getButtonMemorySubtraction().setEnabled(false);
                calculator.getTextPane().setText(calculator.addNewLineCharacters()+"0");
                //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                calculator.confirm("MemorySuite is blank");
                return;
            }
            // MemorySuite now could potentially have gaps in front
            // {"5", "7", "", ""...} ... {"", "7", "", ""...}
            // If the first is a gap, then we have 1 too many gaps
            // TODO: fix for gap being in the middle of the memory values
            if(calculator.getMemoryValues()[0].equals(""))
            {
                // Determine where the first number is that is not a gap
                int alpha = 0;
                for(int i = 0; i < 9; i++)
                {
                    if ( !calculator.getMemoryValues()[i].equals("") )
                    {
                        alpha = i;
                        calculator.getTextPane().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[alpha]);
                        //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                        break;
                    }
                    else
                    {
                        calculator.getTextPane().setText(calculator.addNewLineCharacters()+"0");
                    }
                }
                // Move the first found value to the first position
                // and so on until the end
                String[] newMemoryValues = new String[10];
                for(int i = 0; i <= alpha; i++)
                {
                    if (calculator.getMemoryValues()[alpha].equals("")) break;
                    newMemoryValues[i] = calculator.getMemoryValues()[alpha];
                    alpha++;
                    if (alpha == calculator.getMemoryValues().length) break;
                }
                //setMemoryValues(newMemoryValues);
                calculator.setMemoryPosition(alpha); // next blank spot
            }
        }
        calculator.confirm("Reset memory at position: " + (calculator.getMemoryPosition()-1) + ".");
    }
    /**
     * The actions to perform when MemoryAdd is clicked
     * @param actionEvent the click action
     */
    public void performMemoryAddActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand()); // print out button confirmation
        // if there is a number in the textArea, and we have A number stored in memory, add the
        // number in the textArea to the value in memory. value in memory should be the last number
        // added to memory
        if (calculator.isMemoryValuesEmpty())
        {
            calculator.confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharacters())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
        {
            LOGGER.info("textArea: '" + calculator.getTextPaneWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+"]: '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters())
                    + Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); // create result forced double
            LOGGER.info(calculator.getTextPaneWithoutNewLineCharacters() + " + " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isPositiveNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole positive number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            else if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isNegativeNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole negative number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToPositive(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToNegative(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            // update result in text area
            calculator.getTextPane().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }
    /**
     * The actions to perform when MemorySubtraction is clicked
     * @param actionEvent the click action
     */
    public void performMemorySubtractionActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemorySubButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand()); // print out button confirmation
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not subtracting."); }
        else if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharacters())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
        {
            LOGGER.info("textArea: '" + calculator.getTextPaneWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+": '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)])
                    - Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters()); // create result forced double
            LOGGER.info(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " - " + calculator.getTextPaneWithoutNewLineCharacters() + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isPositiveNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole positive number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            else if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isNegativeNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole negative number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToPositive(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToNegative(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            // update result in text area
            calculator.getTextPane().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }

    /**
     * The main method to set up the Delete button
     */
    private void setupDeleteButton()
    {
        calculator.getButtonDelete().setFont(mainFont);
        calculator.getButtonDelete().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDelete().setEnabled(true);
        calculator.getButtonDelete().setName("Delete");
        calculator.getButtonDelete().addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }
    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performDeleteButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[1].isEmpty())
        { calculator.setValuesPosition(0); } // assume they just previously pressed an operator

        LOGGER.info("values["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.info("textarea: " + calculator.getTextPane().getText());
        calculator.setNumberNegative(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        // this check has to happen
        //calculator.setDotPressed(calculator.isDecimal(calculator.getTextPane().getText().toString()));
        calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getTextPane().getText()));
        calculator.getTextPane().setText(calculator.getTextPaneWithoutNewLineCharacters());
        //calculator.updateTextAreaValueFromTextArea();
        if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 1)
                    {
                        calculator.getTextPane().setText("");
                        //calculator.setTextAreaValue(new StringBuffer().append(" "));
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextPane().getText().length() >= 2)
                    {
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()-1)));
                        calculator.getTextPane().setText(calculator.addNewLineCharacters()+ calculator.getTextPane().getText());
                        calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText().toString();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else { //if (calculator.isDotPressed()) {
                    if (calculator.getTextPane().getText().length() == 2)
                    { // ex: 3. .... recall textarea looks like .3
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(calculator.getTextArea().getText().length() -1 ))); // ex: 3
                        //calculator.setDotPressed(false);
                        calculator.getButtonDecimal().setEnabled(true);
                    }
                    else if (calculator.getTextPane().getText().length() == 3) { // ex: 3.2 or 0.5
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 2))); // ex: 3 or 0
                        //calculator.setDotPressed(false);
                        calculator.getButtonDecimal().setEnabled(true);
                    }
                    else if (calculator.getTextPane().getText().length() > 3)
                    { // ex: 3.25 or 0.50 or 5.02 or 78.9
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1))); // inclusive
                        if (calculator.getTextPane().getText().toString().endsWith(".")) {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getNumberOnLeftSideOfNumber(calculator.getTextArea().getText().toString())));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextPane().getText());
                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText().toString();
                }
            }
            else // if (calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 2)
                    { // ex: -3
                        //calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextPane().setText(calculator.getTextPane().getText().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextPane().getText().length() >= 3)
                    { // ex: -32 or + 6-
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())));
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())));
                        calculator.getTextPane().setText(calculator.getTextPane().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText();
                    }
                    LOGGER.info("output: " + calculator.getTextPane().getText());
                }
                // if no operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 4) { // -3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, 1))); // 3
                        //calculator.setDotPressed(false);
                        calculator.getTextPane().setText(calculator.getTextPane().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText();
                    }
                    else if (calculator.getTextPane().getText().length() > 4)
                    {
                        // ex: -3.25 or -0.00
                        //calculator.getTextArea().getText().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())); // 3.00 or 0.00
                        //calculator.getTextArea().getText().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())); // 3.0 or 0.0
                        //calculator.getTextArea().getText().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextArea().getText().toString())); // 3 or 0
                        calculator.getTextPane().setText(calculator.getTextPane().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText();
                    }
                    LOGGER.info("output: " + calculator.getTextPane().getText());
                }
            }

        }
        else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if an operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 1)
                    { // ex: 5
                        //calculator.setTextAreaValue(new StringBuffer());
                    }
                    else if (calculator.getTextPane().getText().length() == 2)
                    {
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                    }
                    else if (calculator.getTextPane().getText().length() >= 2)
                    { // ex: 56 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                            calculator.setAdding(false);
                            calculator.setSubtracting(false);
                            calculator.setMultiplying(false);
                            calculator.setDividing(false);
                        }
                        else {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()-1)));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextPane().getText());
                    calculator.getTextPane().setText("\n" + calculator.getTextPane().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText().toString();
                    calculator.confirm("Pressed: " + buttonChoice);
                }
                // if an operator has been pushed; number is positive; number is decimal
                else //if (isDotPressed)
                {
                    if (calculator.getTextPane().getText().length() == 2) {
                        // ex: 3.
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                    }
                    else if (calculator.getTextPane().getText().length() == 3)
                    { // ex: 3.2 0.0
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 2 ))); // 3 or 0
                        //calculator.setDotPressed(false);
                    }
                    else if (calculator.getTextPane().getText().length() > 3)
                    { // ex: 3.25 or 0.50  or + 3.25-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                        }
                        else {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                            //calculator.getTextArea().getText().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextArea().getText().toString()));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextPane().getText());
                    calculator.getTextPane().setText("\n"+ calculator.getTextPane().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText().toString();
                    calculator.confirm("Pressed: " + buttonChoice);
                }
            }
            else //if (isNumberNegative)
            {
                // if an operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 2) { // ex: -3
                        //calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextPane().setText(calculator.getTextPane().getText().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextPane().getText().length() >= 3) { // ex: -32 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) {
                            //calculator.getTextArea().getText().append(calculator.getValues()[calculator.getValuesPosition()]);
                        }
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())));
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())));
                        calculator.getTextPane().setText("\n" + calculator.getTextPane().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText();
                    }
                    LOGGER.info("textarea: " + calculator.getTextPane().getText());
                    calculator.confirm("Pressed: " + buttonChoice);
                }
                // if an operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextPane().getText().length() == 4) { // -3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.2 or 0.0
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, 1))); // 3 or 0
                        //calculator.setDotPressed(false);
                        calculator.getTextPane().setText(calculator.getTextPane().getText() + "-"); // 3- or 0-
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText(); // -3 or -0
                    }
                    else if (calculator.getTextPane().getText().length() > 4) { // ex: -3.25  or -0.00
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.25 or 0.00
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()))); // 3.2 or 0.0
                        calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getTextPane().getText().toString());
                        LOGGER.info("textarea: " + calculator.getTextPane().getText());
                        if (calculator.getTextPane().getText().toString().equals("0"))
                        {
                            calculator.getTextPane().setText(calculator.getTextPane().getText().toString());
                            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText().toString();
                        }
                        else {
                            calculator.getTextPane().setText(calculator.getTextPane().getText() + "-");
                            calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextPane().getText();
                        }
                    }
                    LOGGER.info("textarea: " + calculator.getTextPane().getText());
                }
            }
            calculator.resetBasicOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The actions to perform when you click ClearEntry
     */
    private void setupClearEntryButton()
    {
        calculator.getButtonClearEntry().setFont(mainFont);
        calculator.getButtonClearEntry().setMaximumSize(new Dimension(35, 35));
        calculator.getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClearEntry().setEnabled(true);
        calculator.getButtonClearEntry().setName("CE");
        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }
    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public void performClearEntryButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        calculator.getTextPane().setText("");
        //calculator.updateTextAreaValueFromTextArea();
        if (calculator.getValues()[1].isEmpty()) { // if temp[1] is empty, we know we are at temp[0]
            calculator.getValues()[0] = "";
            calculator.setAdding(false);
            calculator.setSubtracting(false);
            calculator.setMultiplying(false);
            calculator.setDividing(false);
            calculator.setValuesPosition(0);
            calculator.setFirstNumber(true);
        }
        else {
            calculator.getValues()[1] = "";
            calculator.resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((ProgrammerPanel)calculator.getCurrentPanel()).resetProgrammerOperators();
            ((ProgrammerPanel)calculator.getCurrentPanel()).buttonByte.setSelected(true);
            setByteByte(true);
        }
        //calculator.setDotPressed(false);
        calculator.getButtonDecimal().setEnabled(true);
        calculator.setNumberNegative(false);
        //calculator.getTextArea().getText().append(calculator.getTextArea().getText());
        LOGGER.info("ClearEntryButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The main method to set up the Clear button
     */
    private void setupClearButton()
    {
        calculator.getButtonClear().setFont(mainFont);
        calculator.getButtonClear().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonClear().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClear().setEnabled(true);
        calculator.getButtonClear().setName("C");
        calculator.getButtonClear().addActionListener(this::performClearButtonActions);
        LOGGER.info("Clear button configured");
    }
    /**
     * The actions to perform when the Clear button is clicked
     * @param actionEvent the action performed
     */
    public void performClearButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        // clear values
        for (int i=0; i < 3; i++)
        {
            calculator.getValues()[i] = "";
        }
        // clear memory
        for(int i=0; i < 10; i++)
        {
            calculator.getMemoryValues()[i] = "";
        }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0");
        calculator.resetBasicOperators(false);
        resetProgrammerByteOperators(false);
        ((ProgrammerPanel)calculator.getCurrentPanel()).resetProgrammerOperators();
        ((ProgrammerPanel)calculator.getCurrentPanel()).buttonByte.setSelected(true);
        setByteByte(true);
        //calculator.updateTextAreaValueFromTextArea();
        calculator.resetBasicOperators(false);
        calculator.setValuesPosition(0);
        calculator.setMemoryPosition(0);
        calculator.setFirstNumber(true);
        //calculator.setDotPressed(false);
        calculator.setNumberNegative(false);
        calculator.getButtonMemoryRecall().setEnabled(false);
        calculator.getButtonMemoryClear().setEnabled(false);
        calculator.getButtonDecimal().setEnabled(true);
        LOGGER.info("ClearButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The main method to set up the Negate button
     */
    private void setupNegateButton()
    {
        calculator.getButtonNegate().setFont(mainFont);
        calculator.getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonNegate().setEnabled(false);
        calculator.getButtonNegate().addActionListener(this::performNegateButtonActions);
    }
    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public void performNegateButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getValues()[calculator.getValuesPosition()].contains("E"))
        {
            calculator.confirm("Number too big!");
        }
        else if (calculator.getValues()[calculator.getValuesPosition()].isEmpty())
        {
            calculator.confirm("No value to negate");
        }
        else
        {
            //textArea.setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace() + calculator.getValues()[calculator.getValuesPosition()]);
            //updateTextAreaValueFromTextArea();
            if (calculator.isNumberNegative()) {
                calculator.setNumberNegative(false);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText());
            }
            else {
                calculator.setNumberNegative(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText() + '-');
            }
            //calculator.updateTextAreaValueFromTextArea();
            //calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().reverse().toString();
            //LOGGER.debug("textArea: " + new StringBuffer().append(getTextAreaWithoutNewLineCharacters()).reverse());
            //LOGGER.debug("textAreaValue: " + textAreaValue);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The main method to set up the Dot button
     */
    private void setupDotButton()
    {
        calculator.getButtonDecimal().setFont(mainFont);
        calculator.getButtonDecimal().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDecimal().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDecimal().setEnabled(true);
        calculator.getButtonDecimal().setName(".");
        calculator.getButtonDecimal().addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }
    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public void performDotButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting Dot button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[0].contains("E")) { calculator.confirm("Cannot press dot button. Number too big!"); }
        else
        {
            LOGGER.info("Programmer dot operations");
            if (calculator.getCalculatorBase() == BASE_BINARY)
            {
                LOGGER.warn("IMPLEMENT OR DISABLE");
            }
            else if (calculator.getCalculatorBase() == BASE_DECIMAL)
            {
                LOGGER.info("DECIMAL base");
                performDot(buttonChoice);
            }
            else
            {
                LOGGER.warn("IMPLEMENT Add other bases");
            }
        }
        calculator.confirm("Pressed the Dot button");
    }
    /**
     * The inner logic of performing Dot actions
     * @param buttonChoice the button choice
     */
    private static void performDot(String buttonChoice)
    {
        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]) || !calculator.isFirstNumber())
        {
            // dot pushed with nothing in textArea
            calculator.getTextPane().setText("0" + buttonChoice);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
        }
        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]) && !calculator.isDotPressed())
        {
            // number and then dot is pushed ex: 5 -> .5
            calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
            //calculator.setDotPressed(true); //!LEAVE. dot logic should not be executed anymore for the current number
        }
        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
        {
            calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
        }
        calculator.getButtonDecimal().setEnabled(false); // deactivate button now that its active for this number
        //calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button
    }

    /**
     * The main method to set up the Add button
     */
    private void setupAddButton()
    {
        calculator.getButtonAdd().setFont(mainFont);
        calculator.getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonAdd().setEnabled(true);
        calculator.getButtonAdd().addActionListener(this::performAdditionButtonActions);
        LOGGER.info("Add button configured");
    }
    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public void performAdditionButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()) && !calculator.textPaneContainsBadText())
            {
                if (calculator.isNumberNegative())
                {
                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPaneWithoutAnyOperator() + "-");
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(' ').append(buttonChoice));
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextPaneWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPaneWithoutAnyOperator());
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(' ').append(buttonChoice));
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutAnyOperator();
                }
                calculator.setAdding(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) // 5 + 3 + ...
            {
                addition();
                calculator.resetCalculatorOperations(calculator.isAdding());
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //if (calculatorType == BASIC) calculator.getTextArea().setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.textPaneContainsBadText())
            {
                calculator.getTextPane().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // calculator.getValues()[valuesPosition]
                calculator.setAdding(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.error("The user pushed plus but there is no number");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.error("Already chose an operator. Choose another number."); }
            calculator.getButtonDecimal().setEnabled(true);
            //calculator.setDotPressed(false);
            calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for addition.
     * This method performs the addition between values[0] and values[1],
     * clears and trailing zeroes if the result is a whole number (15.0000)
     * resets dotPressed to false, enables buttonDot and stores the result
     * back in values[0]
     */
    private void addition()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) + Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " + " + calculator.getValues()[1] + " = " + result);
        //calculator.getValues()[0] = Double.toString(result);
        if (calculator.isPositiveNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(calculator.convertToPositive(calculator.getValues()[0]))));
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getTextPane().getText().toString()).toString();
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
            calculator.setNumberNegative(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }
        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0]));
    }

    /**
     * The main method to set up the Subtraction button
     */
    private void setupSubtractButton()
    {
        calculator.getButtonSubtract().setFont(mainFont);
        calculator.getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSubtract().setEnabled(true);
        calculator.getButtonSubtract().addActionListener(this::performSubtractionButtonActions);
        LOGGER.info("Subtract button configured");
    }
    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public void performSubtractionButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Subtract Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing() &&
                    !calculator.textPaneContainsBadText()) {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPaneWithoutAnyOperator());
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + calculator.getTextAreaWithoutAnything());
                //else if (calculatorType == SCIENTIFIC) LOGGER.warn("SETUP");
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].equals("")) {
                addition();
                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].equals("")) {
                subtract();
                calculator.resetCalculatorOperations(calculator.isSubtracting());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].equals("")) {
                multiply();
                calculator.resetCalculatorOperations(calculator.isMultiplying());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].equals("")) {
                divide();
                calculator.resetCalculatorOperations(calculator.isDividing());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.textPaneContainsBadText()) {
                calculator.getTextPane().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator())) {
                LOGGER.warn("The user pushed subtract but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) {
                LOGGER.info("already chose an operator. next number is negative...");
                // TODO: 5 + -4 = 1 ... answer is coming back 10! Fix
                calculator.setNegating(true);
            }
            calculator.getButtonDecimal().setEnabled(true);
            //calculator.setDotPressed(false);
            calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for subtracting
     */
    private void subtract()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) - Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " - " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)); // textarea changed to whole number, or int
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            calculator.getValues()[0] = calculator.convertToPositive(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getValues()[0]);
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = Double.toString(result);
        }
        //calculator.setTextAreaValue(new StringBuffer(calculator.getValues()[0]));

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();
    }

    /**
     * The main method to set up the Multiplication button
     */
    private void setupMultiplyButton()
    {
        calculator.getButtonMultiply().setFont(mainFont);
        calculator.getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMultiply().setEnabled(true);
        calculator.getButtonMultiply().addActionListener(this::performMultiplicationActions);
        LOGGER.info("Multiply button configured");
    }
    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public void performMultiplicationActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Multiply Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + actionEvent.getActionCommand());
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && !calculator.textPaneContainsBadText())
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetCalculatorOperations(calculator.isAdding());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetCalculatorOperations(calculator.isSubtracting());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetCalculatorOperations(calculator.isMultiplying()); // mulBool = resetOperator(mulBool);
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty()) {
                divide();
                calculator.resetCalculatorOperations(calculator.isDividing());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.textPaneContainsBadText()) {
                calculator.getTextPane().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator())) {
                LOGGER.warn("The user pushed multiply but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDecimal().setEnabled(true);
            //calculator.setDotPressed(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for multiplying
     */
    private void multiply()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) * Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " * " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        // new
        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))));
            calculator.getValues()[0] = calculator.getTextPane().getText().toString(); // textarea changed to whole number, or int
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))));
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getTextPane().getText().toString());
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();


        // old
//        if (result % 1 == 0 && !calculator.getValues()[0].contains("E") && isPositiveNumber(calculator.getValues()[0]))
//        {
//            calculator.getValues()[0] = textAreaValue.toString(); // update storing
//        }
//        else if (result % 1 == 0 && isNegativeNumber(calculator.getValues()[0]))
//        {
//            LOGGER.info("We have a whole negative number");
//            calculator.getValues()[0] = convertToPositive(calculator.getValues()[0]);
//            calculator.getValues()[0] = clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
//            textArea.setText(addNewLineCharacters(1)+calculator.getValues()[0]+"-");
//            updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = textAreaValue.toString();
//            isDotPressed = false;
//            buttonDot.setEnabled(true);
//        }
//        else if (calculator.getValues()[0].contains("E"))
//        {
//            textArea.setText(addNewLineCharacters(1) + calculator.getValues()[0]);
//            updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = textAreaValue.toString(); // update storing
//        }
//        else if (isNegativeNumber(calculator.getValues()[0]))
//        {
//            textAreaValue = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
//            textArea.setText(addNewLineCharacters(1) + textAreaValue +"-");
//            textAreaValue = new StringBuffer().append(convertToNegative(calculator.getValues()[0]));
//        }
//        else
//        {// if double == double, keep decimal and number afterwards
//            textArea.setText(addNewLineCharacters(1) + formatNumber(calculator.getValues()[0]));
//        }
//
//        if (isPositiveNumber(calculator.getValues()[0])) textArea.setText(addNewLineCharacters(1) + calculator.getValues()[0]);
//        else textArea.setText(addNewLineCharacters(1) + convertToPositive(calculator.getValues()[0]) + "-");
//        updateTextAreaValueFromTextArea();
    }

    /**
     * The main method to set up the Divide button
     */
    private void setupDivideButton()
    {
        calculator.getButtonDivide().setFont(mainFont);
        calculator.getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDivide().setEnabled(true);
        calculator.getButtonDivide().addActionListener(this::performDivideButtonActions);
        LOGGER.info("Divide button configured");
    }
    /**
     * The actions to perform when the Divide button is clicked
     * @param actionEvent the click action
     */
    public void performDivideButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Divide Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform division.";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && !calculator.textPaneContainsBadText())
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].equals("")) {
                addition();
                calculator.resetCalculatorOperations(calculator.isAdding()); // sets addBool to false
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].equals("")) {
                subtract();
                calculator.resetCalculatorOperations(calculator.isSubtracting());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].equals("")) {
                multiply();
                calculator.resetCalculatorOperations(calculator.isMultiplying());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].equals("") & !calculator.getValues()[1].equals("0")) {
                divide();
                calculator.resetCalculatorOperations(calculator.isDividing()); // divBool = resetOperator(divBool)
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextPane().getText());
            }
            else if (calculator.textPaneContainsBadText())  {
                calculator.getTextPane().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                //calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator())) {
                LOGGER.warn("The user pushed divide but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDecimal().setEnabled(true);
            calculator.setDividing(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for dividing
     */
    private void divide()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = 0.0;
        if (!calculator.getValues()[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            result = Double.parseDouble(calculator.getValues()[0]) / Double.parseDouble(calculator.getValues()[1]); // create result forced double
            LOGGER.info(calculator.getValues()[0] + " / " + calculator.getValues()[1] + " = " + result);
            calculator.getValues()[0] = Double.toString(result); // store result
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            calculator.getTextPane().setText("\nCannot divide by 0");
            calculator.getValues()[0] = "0";
            calculator.setFirstNumber(true);
        }

        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textarea changed to whole number, or int
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            calculator.getValues()[0] = calculator.convertToPositive(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getValues()[0]);
            //calculator.setDotPressed(false);
            calculator.getButtonDecimal().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            if (Double.parseDouble(calculator.getValues()[0]) < 0.0 )
            {   // negative decimal
                calculator.getValues()[0] = calculator.formatNumber(calculator.getValues()[0]);
                LOGGER.info("textarea: '" + calculator.getTextPane().getText() + "'");
                //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0]));
                LOGGER.info("textarea: '" + calculator.getTextPane().getText() + "'");
                //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(1, calculator.getTextArea().getText().length())));
                LOGGER.info("textarea: '" + calculator.getTextPane().getText() + "'");
            }
            else
            {   // positive decimal
                calculator.getValues()[0] = calculator.formatNumber(calculator.getValues()[0]);
            }
        }
        //calculator.setTextAreaValue(new StringBuffer(calculator.getValues()[0]));

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();
    }

    /**
     * The main method to set up the Percent button
     */
    private void setupPercentButton()
    {
        calculator.getButtonPercent().setFont(mainFont);
        calculator.getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonPercent().setEnabled(true);
        calculator.getButtonPercent().addActionListener(this::performPercentButtonActions);
    }
    /**
     * The actions to perform when the Percent button is clicked
     * @param actionEvent the click action
     */
    public void performPercentButtonActions(ActionEvent actionEvent)
    { calculator.confirm("Button not enabled for programmer panel"); }

    /**
     * The main method to set up the Fraction button
     */
    private void setupFractionButton()
    {
        calculator.getButtonFraction().setFont(mainFont);
        calculator.getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonFraction().setEnabled(true);
        calculator.getButtonFraction().addActionListener(this::performFractionButtonActions);
    }
    /**
     * The actions to perform when the Fraction button is clicked
     * @param actionEvent the click action
     */
    public void performFractionButtonActions(ActionEvent actionEvent)
    { calculator.confirm("Button not configured for programmer panel"); }

    /**
     * The main method to set up the Equals button
     */
    private void setupEqualsButton()
    {
        calculator.getButtonEquals().setFont(mainFont);
        calculator.getButtonEquals().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonEquals().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonEquals().setEnabled(true);
        calculator.getButtonEquals().addActionListener(this::performButtonEqualsActions);
    }
    /**
     * The actions to perform when the Equals button is clicked
     * @param actionEvent the click action
     */
    public void performButtonEqualsActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Equal Button actions");
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        // Get the current number first. save
        String numberInTextArea = calculator.getTextPaneWithoutAnyOperator();
        if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonBin.isSelected())
        {
            try { calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(BASE_BINARY, BASE_DECIMAL, numberInTextArea); }
            catch (CalculatorError c) { calculator.logException(c); }
            LOGGER.info("Values[1] saved to {}", calculator.getValues()[1]);
            LOGGER.info("Now performing operation...");
            determineAndPerformBasicCalculatorOperation();
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonOct.isSelected())
        {
            try { calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(BASE_BINARY, BASE_DECIMAL, numberInTextArea); }
            catch (CalculatorError c) { calculator.logException(c); }
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonDec.isSelected())
        {
            determineAndPerformBasicCalculatorOperation();
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonHex.isSelected())
        {
            calculator.getValues()[0] = "";
            try { calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(BASE_HEXADECIMAL, BASE_DECIMAL, calculator.getValues()[0]); }
            catch (CalculatorError c) { calculator.logException(c); }
            calculator.getValues()[1] = "";
            try { calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(BASE_HEXADECIMAL, BASE_DECIMAL, calculator.getValues()[1]); }
            catch (CalculatorError c) { calculator.logException(c); }
        }

        if (isOrPressed)
        {
            ((ProgrammerPanel)calculator.getCurrentPanel()).performOr();
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);

        }
        else if (isModulusPressed)
        {
            LOGGER.info("Modulus result");
            performModulus();
            // update values and textArea accordingly
            calculator.setValuesPosition(0);
            setModulusPressed(false);
        }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText());
        if (calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            // if temp[0] and temp[1] do not have a number
            calculator.setValuesPosition(0);
        }
        else if (calculator.textPaneContainsBadText())
        {
            calculator.getTextPane().setText("=" + " " +  calculator.getValues()[calculator.getValuesPosition()]); // "userInput +" // temp[valuesPosition]
            calculator.setValuesPosition(1);
            calculator.setFirstNumber(true);
        }

        calculator.getValues()[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        calculator.getValues()[3] = "";
        //updateTextareaFromTextArea();
        calculator.setFirstNumber(true);
        //calculator.setDotPressed(false);
        calculator.setValuesPosition(0);
        calculator.confirm("");
    }

    /************* Helper functions ******************/

    /**
     * Resets main programmer functions to be Enabled and
     * their corresponding flag to false
     */
    public void resetProgrammerOperators()
    {
        buttonModulus.setEnabled(true);
        setModulusPressed(false);
        buttonOr.setEnabled(true);
        setOrPressed(false);
        buttonXor.setEnabled(true);
        setXorPressed(false);
        buttonNot.setEnabled(true);
        setNotPressed(false);
        buttonAnd.setEnabled(true);
        setAndPressed(false);
    }

    //TODO: Rework
    /**
     * Adds bytes to the textPane
     */
    public void addBytesToTextPane()
    {
        // the first two rows in the programmer calculator are reserved for bytes
        if (buttonByte.isSelected())
        {
            calculator.getTextPane().setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonWord.isSelected())
        {
            calculator.getTextPane().setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonDWord.isSelected())
        {
            calculator.getTextPane().setText(
                    calculator.addNewLineCharacters(2) +
                    "00000000 00000000 00000000 00000000"+
                    calculator.addNewLineCharacters(1) +
                    calculator.getValues()[3]+" 00000000 00000000 00000000");
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonQWord.isSelected())
        {
            calculator.getTextPane().setText(
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 00000000\n" +
                            "00000000 00000000 00000000 00000000\n"+calculator.getValues()[3]+" 00000000 00000000 00000000");
            //calculator.getTextArea().setWrapStyleWord(true);
        }
    }

    /**
     * Converts the current value at valuesPosition and
     * displays that converted value in the textPane
     */
    public void convertValue0AndDisplayInTextArea() throws CalculatorError
    {
        LOGGER.info("Converting value...");
        String convertedValue = calculator.convertFromTypeToTypeOnValues(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[calculator.getValuesPosition()]);
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + convertedValue);
        LOGGER.info("Value converted");
    }

    /**
     * Method used to convert the present value from any
     * base to Binary
     */
    public void convertToBinary()
    {
        if (isOctalBase) {
            // logic for Octal to Binary
        } else if (isDecimalBase) {
            // logic for Decimal to Binary
        } else if (isHexadecimalBase) {
            // logic for Hexadecimal to Binary
        }
    }

    /**
     * Method used to convert the present value from any
     * base to Octal
     */
    public void convertToOctal()
    {
        if (isBinaryBase) {
            // logic for Binary to Octal
        } else if (isDecimalBase) {
            // logic for Decimal to Octal
        } else if (isHexadecimalBase) {
            // logic for Hexadecimal to Octal
        }
    }

    /**
     * Method used to convert the present value from any
     * base to Decimal
     */
    public void convertToDecimal()
    {
        LOGGER.debug("convertToDecimal starting");
        CalculatorBase previousBase = calculator.getCalculatorBase();
        // update the current base to binary
        calculator.setCalculatorBase(BASE_DECIMAL);
        CalculatorBase currentBase = calculator.getCalculatorBase();
        LOGGER.info("previous base: " + previousBase);
        LOGGER.info("current base: " + currentBase);
        String nameOfButton = determineIfProgrammerPanelOperatorWasPushed(); // could be null
        if (previousBase == BASE_DECIMAL && currentBase == BASE_BINARY ||
                calculator.getCalculatorType() == CalculatorType.BASIC)
        {
            try {
                calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[0]);
                calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[1]);
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (previousBase == BASE_BINARY && currentBase == BASE_DECIMAL)
        {
        }
        else
        if (calculator.getTextPaneWithoutNewLineCharacters().equals("")) { return; }
        if (buttonBin.isSelected()) {
            // logic for Binary to Decimal
            try {
                calculator.getTextPane().setText(calculator.addNewLineCharacters(1)+
                        calculator.convertFromTypeToTypeOnValues(BASE_BINARY, BASE_DECIMAL,
                                calculator.getTextPaneWithoutNewLineCharacters()));
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (isOctalBase) {
            // logic for Octal to Decimal
        } else if (isHexadecimalBase) {
            // logic for Hexadecimal to Decimal
        } else if (buttonDec.isSelected()) {
            if (calculator.getTextPane().getText().equals("")) { return; }
            String operator = determineIfProgrammerPanelOperatorWasPushed();
            if (!operator.equals(""))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters(3)+operator+" "+calculator.getValues()[calculator.getValuesPosition()]);
            }
            else
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters(3)+calculator.getValues()[calculator.getValuesPosition()]);
            }
            //calculator.updateTextAreaValueFromTextArea();
        }

        LOGGER.info("convertToDecimal finished");
    }

    /**
     * Method used to convert the present value from any
     * base to Hexadecimal
     */
    public void convertToHexadecimal()
    {
        if (isBinaryBase) {
            // logic for Binary to Hexadecimal
        } else if (isOctalBase) {
            // logic for Octal to Hexadecimal
        } else if (isDecimalBase) {
            // logic for Decimal to Hexadecimal
        }
    }

    /**
     * Resets the byte flags to the passed in boolean
     * @param byteOption a boolean to reset the operators to
     */
    public static void resetProgrammerByteOperators(boolean byteOption)
    {
        isByteByte = byteOption;
        isWordByte = byteOption;
        isDWordByte = byteOption;
        isQWordByte = byteOption;
    }

    /**
     * DEPRECATED. No info
     */
    @Deprecated(since = "use updateTextAreaUsingBaseAndType")
    public void convertTextArea()
    {
        if (calculator.getCalculatorType() == CalculatorType.BASIC)
        {
            LOGGER.debug("Going from Basic to Programmer");
            calculator.performInitialChecks();
            boolean operatorWasPushed = calculator.determineIfAnyBasicOperatorWasPushed();
            String convertedValue = "";
            try { convertedValue = calculator.convertFromTypeToTypeOnValues(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[0]); }
            catch (CalculatorError c) { calculator.logException(c); }
            if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharacters()))
            {
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator
                {
                    if (calculator.isAdding())
                    {
                        calculator.getTextPane().setText(calculator.addNewLineCharacters(1)
                                + " + " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " +"));
                    }
                    else if (calculator.isSubtracting())
                    {
                        calculator.getTextPane().setText(calculator.addNewLineCharacters(1)
                                + " - " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " -"));
                    }
                    else if (calculator.isMultiplying())
                    {
                        calculator.getTextPane().setText(calculator.addNewLineCharacters(1)
                                + " * " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " *"));
                    }
                    else if (calculator.isDividing())
                    {
                        calculator.getTextPane().setText(calculator.addNewLineCharacters(1)
                                + " / " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but getTextArea() has some value
                {
                    calculator.getTextPane().setText(calculator.addNewLineCharacters(1)
                            + convertedValue);
                    //calculator.setTextAreaValue(new StringBuffer().append(convertedValue));
                }
            }
        }
    }

    /**
     * Method used to enable/disable the number buttons 2-9
     * @param isEnabled boolean to set the number buttons to
     */
    public void setButtons2To9(boolean isEnabled)
    {
        Collection<JButton> buttonsWithout0Or1 = new ArrayList<>(calculator.getAllNumberButtons());
        buttonsWithout0Or1.removeIf(btn -> btn.getName().equals("0") || btn.getName().equals("1"));
        buttonsWithout0Or1.forEach(button -> button.setEnabled(isEnabled));
    }

    /**
     * Method used to enable/disable the hexadecimal number buttons A-F
     * @param isEnabled boolean to set the hexadecimal numbers to
     */
    public void setButtonsAToF(boolean isEnabled)
    { getAToFButtons().forEach(button -> button.setEnabled(isEnabled)); }

    //TODO: Rework
    /**
     * Method used to automatically update the bytes to show
     * based on the length of the current number
     * @param number the number used to determine the appropriate byte
     */
    public void setTheBytesBasedOnTheNumbersLength(String number)
    {
        LOGGER.info("setting the bytes based on the length of the number");
        int lengthOfNumber = number.length();
        resetProgrammerByteOperators(false);
        if (lengthOfNumber <= 8) { buttonByte.setSelected(true); setByteByte(true); }
        else if (lengthOfNumber <= 16) { buttonWord.setSelected(true); setWordByte(true); }
        else if (lengthOfNumber <= 32) { buttonDWord.setSelected(true); setDWordByte(true); }
        else { buttonQWord.setSelected(true); setQWordByte(true); }
        LOGGER.info("setButtonByteSet();: " + isByteByte());
        LOGGER.info("isButtonWordSet: " + isWordByte());
        LOGGER.info("isButtonDWordSet: " + isDWordByte());
        LOGGER.info("isButtonQWordSet: " + isQWordByte());
    }

    /**
     * Main method used to convert the textPanel value based
     * on the CalculatorBase
     */
    public void updateTheTextAreaBasedOnTheTypeAndBase()
    {
        if (BASE_BINARY == calculator.getCalculatorBase())
        {
            String convertedToBinary = "";
            try {
                convertedToBinary = calculator.convertFromTypeToTypeOnValues(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[0]);
            } catch (CalculatorError c) {
                calculator.logException(c);
            }
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + convertedToBinary);
        }
        else if (BASE_DECIMAL == calculator.getCalculatorBase())
        {
            if (!calculator.isNumberNegative())
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPane().getText());
            else {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
                //calculator.setTextAreaValue(new StringBuffer("-" + calculator.getValues()[calculator.getValuesPosition()]));
                calculator.setNumberNegative(true);
            }
        }
        else if (BASE_OCTAL == calculator.getCalculatorBase())
        {
            LOGGER.warn("Setup");
        }
        else /* (calculatorBase == HEXADECIMAL) */
        {
            LOGGER.warn("Setup");
        }
    }

    /**
     * This method is called when the Equals button is clicked
     * and handles determining which operation to execute. After
     * the operation is handled, it resets that operation to false.
     */
    public void determineAndPerformBasicCalculatorOperation()
    {
        if (calculator.isAdding())
        {
            addition();
            calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
        }
        else if (calculator.isSubtracting())
        {
            subtract();
            calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
        }
        else if (calculator.isMultiplying())
        {
            multiply();
            calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
        }
        else if (calculator.isDividing())
        {
            divide();
            calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
        }
    }

    /**
     * Returns the ProgrammerPanel operation that was pushed,
     * or the BasicPanel if not specific to the ProgrammerPanel
     * @return String the name of the button pushed
     */
    public String determineIfProgrammerPanelOperatorWasPushed()
    {
        String results = "";
        // what operations can be pushed: MOD, OR, XOR, NOT, AND
        if (isModulusPressed) { results = "MOD"; }
        else if (isOrPressed) { results = "OR"; }
        else if (isXorPressed) { results = "XOR"; }
        else if (isNotPressed) { results = "NOT"; }
        else if (isAndPressed) { results = "AND"; }
        if (results.isEmpty()) { results = ((BasicPanel)calculator.getCurrentPanel()).determineIfBasicPanelOperatorWasPushed(); }
        LOGGER.info("operator: " + results);

        return results;
    }
//    public String determineIfBasicPanelOperatorWasPushed()
//    {
//        String results = "";
//        if (calculator.isAdding()) { results = "+"; }
//        else if (calculator.isSubtracting()) { results = "-"; }
//        else if (calculator.isMultiplying()) { results = "*"; }
//        else if (calculator.isDividing()) { results = "/"; }
//        LOGGER.info("operator: " + (results.equals("") ? "no basic operator pushed" : results));
//        return results;
//    }

    /**
     * Adds zeroes to the beginning of the number
     * to ensure full representation of how many
     * bytes should be shown.
     * @param number the number show
     * @return the number appended with zeroes, if necessary
     */
    public String addZeroesToNumber(String number)
    {
        int lengthOfNumber = number.length();
        int zeroesToAdd = 0;
        setTheBytesBasedOnTheNumbersLength(number);
        if (isByteByte) {
            zeroesToAdd = 8 - lengthOfNumber;
        } else if (isWordByte) {
            zeroesToAdd = 16 - lengthOfNumber;
        } else if (isDWordByte) {
            zeroesToAdd = 32 - lengthOfNumber;
        } else /* (isButtonQWordSet) */ {
            zeroesToAdd = 64 - lengthOfNumber;
        }
        if (zeroesToAdd != 0) {
            LOGGER.debug("zeroesToAdd: " + zeroesToAdd);
            StringBuffer zeroes = new StringBuffer();
            for (int i = 0; i < zeroesToAdd; i++) zeroes = zeroes.append("0");
            number = zeroes + number;
            LOGGER.info("After adding zeroes: " + number);
        } else {
            LOGGER.info("Not adding any zeroes. Number appropriate length.");
        }
        return number;
    }

    //TODO: Rework with Enum
    /**
     * Returns the String representation of the number
     * of bytes to return
     * @return String the number of bytes in String format
     */
    public String getByteWord()
    {
        if (getBytes() == 8) return "Byte";
        else if (getBytes() == 16) return "Word";
        else if (getBytes() == 32) return "DWord";
        else return "QWord";
    }

    /**
     * Returns the number of bytes to show
     * @return int the number of bytes
     */
    public int getBytes()
    {
        if (isByteByte)
        { return 8; }
        else if (isWordByte)
        { return 16; }
        else if (isDWordByte)
        { return 32; }
        else
        { return 64; }
    }

    /************* All Getters ******************/
    public GridBagLayout getProgrammerLayout() { return programmerLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public Calculator getCalculator() { return calculator; }
    public CalculatorBase getCalculatorBase() { return calculator.getCalculatorBase(); }
    public JPanel getBtnGroupOnePanel() { return btnGroupOnePanel; }
    public JPanel getBtnGroupTwoPanel() { return btnGroupTwoPanel; }
    public JButton getButtonModulus() { return buttonModulus; }
    public JButton getButtonLPar() { return buttonLPar; }
    public JButton getButtonRPar() { return buttonRPar; }
    public JButton getButtonRol() { return buttonRol; }
    public JButton getButtonRor() { return buttonRor; }
    public JButton getButtonOr() { return buttonOr; }
    public JButton getButtonXor() { return buttonXor; }
    public JButton getButtonLSh() { return buttonLSh; }
    public JButton getButtonRSh() { return buttonRSh; }
    public JButton getButtonNot() { return buttonNot; }
    public JButton getButtonAnd() { return buttonAnd; }
    public JButton getButtonA() { return buttonA; }
    public JButton getButtonB() { return buttonB; }
    public JButton getButtonC() { return buttonC; }
    public JButton getButtonD() { return buttonD; }
    public JButton getButtonE() { return buttonE; }
    public JButton getButtonF() { return buttonF; }
    public JRadioButton getButtonBin() { return buttonBin; }
    public JRadioButton getButtonDec() { return buttonDec;}
    public JRadioButton getButtonOct() { return buttonOct; }
    public JRadioButton getButtonHex() { return buttonHex; }
    public JRadioButton getButtonByte() { return buttonByte; }
    public JRadioButton getButtonWord() { return buttonWord; }
    public JRadioButton getButtonDWord() { return buttonDWord; }
    public JRadioButton getButtonQWord() { return buttonQWord; }
    public boolean isBinaryBase() { return isBinaryBase; }
    public boolean isOctalBase() { return isOctalBase; }
    public boolean isDecimalBase() { return isDecimalBase; }
    public boolean isHexadecimalBase() { return isHexadecimalBase; }
    public boolean isByteByte() { return isByteByte; }
    public boolean isWordByte() { return isWordByte; }
    public boolean isDWordByte() { return isDWordByte; }
    public boolean isQWordByte() { return isQWordByte; }
    public boolean isOrPressed() { return isOrPressed; }
    public boolean isModulusPressed() { return isModulusPressed; }
    public boolean isXorPressed() { return isXorPressed; }
    public boolean isNegateButtonBool() { return negateButtonBool; }
    public boolean isNotPressed() { return isNotPressed; }
    public boolean isAndPressed() { return isAndPressed; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
        this.programmerLayout = panelLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setCalculatorBase(CalculatorBase calculatorBase) { this.calculator.setCalculatorBase(calculatorBase); }
    public void setByteByte(boolean buttonByteSet) { isByteByte = buttonByteSet; }
    public void setWordByte(boolean buttonWordSet) { isWordByte = buttonWordSet; }
    public void setDWordByte(boolean buttonDwordSet) { isDWordByte = buttonDwordSet; }
    public void setQWordByte(boolean buttonQWordSet) { isQWordByte = buttonQWordSet; }
    public void setBinaryBase(boolean binaryBase) { this.isBinaryBase = binaryBase; }
    public void setOctalBase(boolean octalBase) { this.isOctalBase = octalBase; }
    public void setDecimalBase(boolean decimalBase) { this.isDecimalBase = decimalBase; }
    public void setHexadecimalBase(boolean hexadecimalBase) { this.isHexadecimalBase = hexadecimalBase; }
    public void setOrPressed(boolean orPressed) { this.isOrPressed = orPressed; }
    public void setModulusPressed(boolean modulusPressed) { this.isModulusPressed = modulusPressed; }
    public void setXorPressed(boolean xorPressed) {this.isXorPressed = xorPressed; }
    public void setNotPressed(boolean notPressed) {this.isNotPressed = notPressed; }
    public void setAndPressed(boolean andPressed) {this.isAndPressed = andPressed; }
}