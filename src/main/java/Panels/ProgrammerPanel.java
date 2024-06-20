package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import Types.CalculatorBase;
import Types.Texts;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static Calculators.Calculator.mainFont;
import static Types.CalculatorBase.*;
import static Types.CalculatorBase.BASE_BINARY;
import static Types.CalculatorType.PROGRAMMER;
import static Types.Texts.*;

public class ProgrammerPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private Calculator calculator;
    private GridBagConstraints constraints;
    private final JPanel programmerPanel = new JPanel(new GridBagLayout()),
                         memoryPanel = new JPanel(new GridBagLayout()),
                         buttonsPanel = new JPanel(new GridBagLayout()),
                         historyPanel = new JPanel(new GridBagLayout());
    private JTextPane programmerHistoryTextPane;
    // Programmer specific buttons
    final private JButton
            buttonModulus = new JButton(MODULUS.getValue()),
            buttonLeftParenthesis = new JButton(LEFT_PARENTHESIS.getValue()), buttonRightParenthesis = new JButton(RIGHT_PARENTHESIS.getValue()),
            buttonRotateLeft = new JButton (ROL.getValue()), buttonRotateRight = new JButton (ROR.getValue()),
            buttonOr = new JButton (OR.getValue()), buttonXor = new JButton (XOR.getValue()),
            buttonShiftLeft = new JButton (LSH.getValue()), buttonShiftRight = new JButton (RSH.getValue()),
            buttonNot = new JButton (NOT.getValue()), buttonAnd = new JButton (AND.getValue()),
            buttonA = new JButton(A.getValue()), buttonB = new JButton(B.getValue()),
            buttonC = new JButton(C.getValue()), buttonD = new JButton(D.getValue()),
            buttonE = new JButton(E.getValue()), buttonF = new JButton(F.getValue()),
            buttonBytes = new JButton(BYTE.getValue()), buttonShift = new JButton(SHIFT.getValue());
    private JTextPane allRepresentationsTextPane;
    private JLabel byteLabel = new JLabel(BYTE.getValue()), wordLabel = new JLabel(WORD.getValue()),
                   dWordLabel = new JLabel(DWORD.getValue()), qWordLabel = new JLabel(QWORD.getValue());
    private Texts byteType;
    private static boolean
            isBinaryBase = true, isOctalBase = false,
            isDecimalBase = false, isHexadecimalBase = false,
            isByteByte = true, isWordByte = false,
            isDWordByte = false, isQWordByte = false,
            isOrPressed = false, isModulusPressed = false,
            isXorPressed = false, /*negateButtonBool = false,*/
            isNotPressed = false, isAndPressed = false;

    /* Constructors */

    /**
     * A zero argument constructor for creating a OLDProgrammerPanel
     */
    public ProgrammerPanel()
    {
        setName(PROGRAMMER.getValue());
        LOGGER.info("Empty Programmer panel created");
    }

    /**
     * A single argument constructor used to create a OLDProgrammerPanel
     * @param calculator the Calculator to use
     */
    public ProgrammerPanel(Calculator calculator)
    { this(calculator, null); }

    /**
     * The main constructor used to create a OLDProgrammerPanel
     * @param calculator the Calculator to use
     * @param base the CalculatorBase to use
     */
    public ProgrammerPanel(Calculator calculator, CalculatorBase base)
    {
        setupProgrammerPanel(calculator, base);
        LOGGER.info("Programmer panel created");
    }

    /* Start of methods here */

    /**
     * The main method used to define the OLDProgrammerPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     */
    public void setupProgrammerPanel(Calculator calculator, CalculatorBase base)
    {
        this.calculator = calculator;
        this.calculator.setCalculatorBase(base);
        setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        setSize(new Dimension(227,383)); // sets main size
        setMinimumSize(new Dimension(227, 383)); // sets minimum size
        setupProgrammerPanelComponents(base);
        addComponentsToPanel();
        setName(PROGRAMMER.getValue());
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up programmer panel");
    }

    /**
     * Clears button actions, sets the CalculatorType,
     * CalculatorBase, ConverterType, and finally
     * sets up the OLDProgrammerPanel and its components
     */
    private void setupProgrammerPanelComponents(CalculatorBase base)
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllBasicOperatorButtons(),
                        calculator.getAllBasicPanelOperatorButtons(),
                        calculator.getAllNumberButtons(),
                        calculator.getAllMemoryPanelButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .toList();

        allButtons.forEach(button -> Stream.of(button.getActionListeners())
                .forEach(button::removeActionListener));
        LOGGER.debug("Removed actions...");

        calculator.setupNumberButtons(); //required first for setProgrammerBase()
        calculator.setCalculatorType(PROGRAMMER);
        calculator.setCalculatorBase(base);
        calculator.setConverterType(null);
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupButtonBlank1();
        calculator.setupMemoryButtons(); // MR MC MS M+ M- H
        calculator.setupBasicPanelButtons(); // common
        setupProgrammerHistoryZone();
        setupProgrammerPanelButtons();
        resetProgrammerByteOperators(false);
        setByteType(BYTE);
        //setupButtonGroupOne();
        //setupButtonGroupTwo();
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
            calculator.getButtonDecimal().setSelected(true);
            //setDecimalBase(true);
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
            //buttonOct.setSelected(true);
            //setOctalBase(true);
        }
        else if (base == BASE_HEXADECIMAL)
        {
            calculator.setCalculatorBase(base);
            setButtons2To9(true);
            setButtonsAToF(true);
            //buttonHex.setSelected(true);
            //setHexadecimalBase(true);
        }
        else
        {
            calculator.setCalculatorBase(BASE_BINARY);
            setButtons2To9(false);
            setButtonsAToF(false);
            //buttonBin.setSelected(true);
            //setBinaryBase(true);
        }
    }

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
        calculator.confirm("Viewing " + PROGRAMMER.getValue() + " Calculator Help");
    }

    /**
     * Displays the history for the BasicPanel
     * during each active instance
     */
    public void setupProgrammerHistoryZone()
    {
        LOGGER.debug("Configuring BasicHistoryZone...");
        constraints.anchor = GridBagConstraints.WEST;
        addComponent(historyPanel, new JLabel(HISTORY.getValue()), 0, 0); // space before with jtextarea

        calculator.setupHistoryTextPane();
        JScrollPane scrollPane = new JScrollPane(programmerHistoryTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(programmerHistoryTextPane.getSize());

        addComponent(historyPanel, scrollPane, 1, 0, new Insets(0,0,0,0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("BasicHistoryZone configured");
    }

    /**
     * The actions to perform when History is clicked
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        if (HISTORY_OPEN.getValue().equals(calculator.getButtonHistory().getText()))
        {
            LOGGER.debug("{}", actionEvent.getActionCommand());
            calculator.getButtonHistory().setText(HISTORY_CLOSED.getValue());
            programmerPanel.remove(historyPanel);
            addComponent(programmerPanel, buttonsPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
            System.out.println(programmerPanel.getSize());
        }
        else
        {
            LOGGER.debug("{}", actionEvent.getActionCommand());
            calculator.getButtonHistory().setText(HISTORY_OPEN.getValue());
            programmerPanel.remove(buttonsPanel);
            addComponent(programmerPanel, historyPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
            System.out.println(programmerPanel.getSize());
        }
    }

    /**
     * The main method to set up the Programmer
     * button panels not in the memory panel
     */
    private void setupProgrammerPanelButtons()
    {
        LOGGER.debug("Configuring Programmer Panel buttons...");
        List<JButton> allButtons =
                Stream.of(getAllProgrammerOperatorButtons(),
                                calculator.getAllBasicOperatorButtons(),
                                getAllHexadecimalButtons())
                        .flatMap(Collection::stream) // Flatten into a stream of JButton objects
                        .toList();
        allButtons.forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35) );
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonModulus.setName(MODULUS.name());
        buttonModulus.addActionListener(this::performButtonModActions);
        LOGGER.debug("Modulus button configured");
        buttonLeftParenthesis.setName(LEFT_PARENTHESIS.name());
        buttonLeftParenthesis.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.warn("Left Parenthesis button needs to be configured");
        buttonRightParenthesis.setName(RIGHT_PARENTHESIS.name());
        buttonRightParenthesis.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.warn("Right Parenthesis button needs to be configured");
        buttonRotateLeft.setName(ROL.name());
        buttonRotateLeft.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.warn("RoL button needs to be configured");
        buttonRotateRight.setName(ROR.name());
        buttonRotateRight.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.warn("RoR button needs to be configured");
        buttonOr.setName(OR.name());
        buttonOr.addActionListener(this::performButtonOrActions);
        LOGGER.debug("Or button configured");
        buttonXor.setName(XOR.name());
        buttonXor.addActionListener(this::performButtonXorActions);
        LOGGER.debug("Xor button configured");
        buttonShiftLeft.setName(LSH.name());
        buttonShiftLeft.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.debug("Left Shift button needs to be configured");
        buttonShiftRight.setName(RSH.name());
        buttonShiftRight.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.debug("Right Shift button needs to be configured");
        buttonNot.setName(NOT.name());
        buttonNot.addActionListener(this::performButtonNotActions);
        LOGGER.debug("Not button configured");
        buttonAnd.setName(AND.name());
        buttonAnd.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.debug("And button needs to be configured");
        LOGGER.debug("SquareRoot button configured in Calculator");
        buttonA.setName(A.name());
        buttonB.setName(B.name());
        buttonC.setName(C.name());
        buttonD.setName(D.name());
        buttonE.setName(E.name());
        buttonF.setName(F.name());
        getAllHexadecimalButtons().forEach(hexadecimalNumberButton -> {
            hexadecimalNumberButton.setFont(Calculator.mainFont);
            hexadecimalNumberButton.setPreferredSize(new Dimension(35, 35));
            hexadecimalNumberButton.setBorder(new LineBorder(Color.BLACK));
            hexadecimalNumberButton.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
        });
        buttonShift.setName(SHIFT.name());
        buttonShift.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
        LOGGER.debug("Shift button needs to be configured");
        LOGGER.debug("Hexadecimal buttons configured");
    }

    /**
     * Resets the byte flags to the passed in boolean
     * @param byteOption a boolean to reset the operators to
     */
    public void resetProgrammerByteOperators(boolean byteOption)
    {
        isByteByte = byteOption;
        isWordByte = byteOption;
        isDWordByte = byteOption;
        isQWordByte = byteOption;
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    private void addComponentsToPanel()
    {
        addComponent(programmerPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        //addComponent(programmerPanel, allRepresentationsTextPane, 1, 0, new Insets(1, 1, 1, 1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        addComponent(memoryPanel, calculator.getButtonMemoryStore(), 0, new Insets(0,1,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryRecall(), 1, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryClear(), 2, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryAddition(), 3, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemorySubtraction(), 4, new Insets(0,0,0,0)); // right:1
        addComponent(memoryPanel, calculator.getButtonHistory(), 5, new Insets(0,0,0,1));

        //addComponent(programmerPanel, memoryPanel, 0, new Insets(0,0,0,0));
        addComponent(programmerPanel, memoryPanel, 1, 0, new Insets(0,0,0,0), 1, 1, 1.0, 1.0, 0, 0);

        addComponent(buttonsPanel, buttonShiftLeft, 0, 0);
        addComponent(buttonsPanel, buttonShiftRight, 0, 1);
        addComponent(buttonsPanel, buttonOr, 0, 2);
        addComponent(buttonsPanel, buttonXor, 0, 3);
        addComponent(buttonsPanel, buttonNot, 0, 4);
        addComponent(buttonsPanel, buttonAnd, 0, 5);
        addComponent(buttonsPanel, buttonShift, 1, 0);
        addComponent(buttonsPanel, buttonModulus, 1, 1);
        addComponent(buttonsPanel, calculator.getButtonClearEntry(), 1, 2);
        addComponent(buttonsPanel, calculator.getButtonClear(), 1, 3);
        addComponent(buttonsPanel, calculator.getButtonDelete(), 1, 4);
        addComponent(buttonsPanel, calculator.getButtonDivide(), 1, 5);
        addComponent(buttonsPanel, buttonA, 2, 0);
        addComponent(buttonsPanel, buttonB, 2, 1);
        addComponent(buttonsPanel, calculator.getButton7(), 2, 2);
        addComponent(buttonsPanel, calculator.getButton8(), 2, 3);
        addComponent(buttonsPanel, calculator.getButton9(), 2, 4);
        addComponent(buttonsPanel, calculator.getButtonMultiply(),2, 5);
        addComponent(buttonsPanel, buttonC, 3, 0);
        addComponent(buttonsPanel, buttonD,3, 1);
        addComponent(buttonsPanel, calculator.getButton4(), 3, 2);
        addComponent(buttonsPanel, calculator.getButton5(), 3, 3);
        addComponent(buttonsPanel, calculator.getButton6(), 3, 4);
        addComponent(buttonsPanel, calculator.getButtonSubtract(), 3, 5);
        addComponent(buttonsPanel, buttonE, 4, 0);
        addComponent(buttonsPanel, buttonF,4, 1);
        addComponent(buttonsPanel, calculator.getButton1(), 4, 2);
        addComponent(buttonsPanel, calculator.getButton2(), 4, 3);
        addComponent(buttonsPanel, calculator.getButton3(), 4, 4);
        addComponent(buttonsPanel, calculator.getButtonAdd(), 4, 5);
        addComponent(buttonsPanel, buttonLeftParenthesis, 5, 0);
        addComponent(buttonsPanel, buttonRightParenthesis,5, 1);
        addComponent(buttonsPanel, calculator.getButtonNegate(), 5, 2);
        addComponent(buttonsPanel, calculator.getButton0(), 5, 3);
        addComponent(buttonsPanel, calculator.getButtonDecimal(), 5, 4);
        addComponent(buttonsPanel, calculator.getButtonEquals(), 5, 5);

        //addComponent(programmerPanel, buttonsPanel, 3, 0, null, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.PAGE_END); // was row2
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
    { getAllHexadecimalButtons().forEach(button -> button.setEnabled(isEnabled)); }

    /**
     * Returns the programmer specific calculator buttons. This
     * includes Modulus, LeftParenthesis, RightParenthesis,
     * RotateLeft, RotateRight, Or, ExclusiveOr, LeftShift,
     * RightShift, Not, and And.
     * It does not include the hexadecimal buttons.
     *
     * @return Collection of buttons
     */
    public List<JButton> getAllProgrammerOperatorButtons()
    {
        return Arrays.asList(buttonModulus, buttonLeftParenthesis, buttonRightParenthesis,
                buttonRotateLeft, buttonRotateRight, buttonOr, buttonXor, buttonShiftLeft,
                buttonShiftRight, buttonNot, buttonAnd, buttonShift);
    }

    /**
     * Returns the programmer hexadecimal buttons.
     *
     * @return Collection of buttons
     */
    public List<JButton> getAllHexadecimalButtons()
    { return Arrays.asList(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF); }

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
        //setModulusPressed(true);
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
            //setModulusPressed(true);
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

    /**
     * The actions to perform when Or is clicked
     */
    public void performButtonOrActions(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        //setOrPressed(true);
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
            //setOrPressed(false);
            calculator.setFirstNumber(true);
            calculator.confirm("Pressed OR. Doing nothing");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            String sb = performOr(); // 2 OR 3 OR button presses
            //TODO: need to convert sb to DECIMAL form before storing in getValues()
            calculator.getValues()[0] = sb;
            calculator.getTextPane().setText(calculator.addNewLineCharacters(1)+calculator.getValues()[0]);
            //setOrPressed(false);
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
     * The actions to perform when Xor is clicked
     */
    public void performButtonXorActions(ActionEvent actionEvent)
    {
        LOGGER.info("performing XOR button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        //setXorPressed(true);
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

    /**
     * The actions to perform when the Not button is clicked
     */
    public void performButtonNotActions(ActionEvent actionEvent)
    {
        LOGGER.info("performing not operation...");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        //setNotPressed(false);
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

    /**
     * The actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer number button actions");
        LOGGER.info("buttonChoice: " + actionEvent.getActionCommand());
        if (!calculator.isFirstNumber())
        {
            calculator.setFirstNumber(true);
            calculator.getTextPane().setText(BLANK.getValue() + calculator.addNewLineCharacters(1) + addByteRepresentations());
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
    private void performInnerNumberActions(ActionEvent actionEvent) throws CalculatorError
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing programmer number button actions...");
        LOGGER.info("Adding '{}' to textPane", buttonChoice);
        if (calculator.getCalculatorBase() == BASE_BINARY) {
            int lengthOfTextArea = calculator.getTextPaneWithoutNewLineCharacters().length();
            if (lengthOfTextArea == 8 || lengthOfTextArea == 17 || lengthOfTextArea == 26 || lengthOfTextArea == 35 || lengthOfTextArea == 44)
            {   // add a space add the "end" if the length of the number matches the bytes
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(buttonChoice).append(" ").append(calculator.getTextPaneWithoutNewLineCharacters());
                calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + newNumber);
                // update the bytes here or when we add next number
                if (isByteByte) { resetProgrammerByteOperators(false); isWordByte = (true); /*buttonWord.setSelected(true);*/ }
                else if (isWordByte) { resetProgrammerByteOperators(false); isDWordByte =(true); /*buttonDWord.setSelected(true);*/ }
                else if (isDWordByte && lengthOfTextArea == 17) {
                    resetProgrammerByteOperators(false);
                    isQWordByte = (true);
                    //buttonQWord.setSelected(true);
                }
                else if (lengthOfTextArea == 35) {
                    resetProgrammerByteOperators(false);
                    isQWordByte =(true);
                    //buttonQWord.setSelected(true);
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
            calculator.performInitialChecks();
            if (calculator.isPositiveNumber(buttonChoice) && calculator.getButtonDecimal().isEnabled())
            {
                LOGGER.info("positive number & dot button was not pushed");
                //LOGGER.debug("before: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
                if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]))
                {
                    //calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice + addByteRepresentations());
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaWithoutNewLineCharacters()));
                    calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                    appendToPane(calculator.getTextPane(), calculator.addNewLineCharacters(1)+
                            calculator.getValues()[calculator.getValuesPosition()] + "\n");
                }
                else
                {
                    calculator.getValues()[calculator.getValuesPosition()] += buttonChoice;
                    appendToPane(calculator.getTextPane(), calculator.addNewLineCharacters(1)+
                            calculator.getValues()[calculator.getValuesPosition()] + "\n");
                    //calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice).reverse());
                    //calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().reverse().toString();
                }
                //LOGGER.debug("after: '" + calculator.getTextArea().getText() + "'");
                //calculator.setValuesToTextAreaValue();
                //calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                //calculator.updateTheTextAreaBasedOnTheTypeAndBase();
                ////calculator.updateTextAreaValueFromTextArea();
            }
            else if (calculator.isNumberNegative() && !calculator.getButtonDecimal().isEnabled())
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
                performInnerDecimal(buttonChoice);
            }
            else
            {
                LOGGER.info("dot button was pushed");
                //calculator.performLogicForDotButtonPressed(buttonChoice);
                performInnerDecimal(buttonChoice);
            }
            calculator.confirm("Pressed " + buttonChoice);
        }
        else if (calculator.getCalculatorBase() == BASE_OCTAL) { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else /* (calculator.getCalculatorBase() == HEXADECIMAL */ { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }
    }

    public String addByteRepresentations()
    {
        return """
                \nHex: %s
                Dec: %s
                Oct: %s
                Bin: %s
                """
                .formatted(
                    calculator.convertValueToHexadecimal(),
                    calculator.convertValueToDecimal(),//calculator.getValues()[calculator.getValuesPosition()],
                    calculator.convertValueToOctal(),
                    calculator.convertValueToBinary()
                );
    }

    public void appendToPane(JTextPane textPane, String text) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            // Get the start and end position of the text to be inserted
            doc.remove(0, doc.getLength());
            doc.insertString(0, text, doc.getStyle("alignRight"));
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            doc.setParagraphAttributes(0, text.length(), attribs, false);

            doc.insertString(doc.getLength(), addByteRepresentations(), doc.getStyle("alignLeft"));
            attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
            doc.setParagraphAttributes(doc.getLength() - addByteRepresentations().length(), addByteRepresentations().length(), doc.getStyle("alignLeft"), false);
        } catch (BadLocationException e) {
            calculator.logException(e);
        }
    }

    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public void performDecimalButtonActions(ActionEvent actionEvent)
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
                performInnerDecimal(buttonChoice);
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
    private void performInnerDecimal(String buttonChoice)
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

    /* Getters */
    public JTextPane getProgrammerHistoryTextPane() { return programmerHistoryTextPane; }
    public Texts getByteType() { return byteType; }
    public boolean isByteByte() { return isByteByte; }
    public boolean isWordByte() { return isWordByte; }
    public boolean isDWordByte() { return isDWordByte; }
    public boolean isQWordByte() { return isQWordByte; }

    /* Setters */
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
//        this.basicLayout = panelLayout;
    }
    public void setByteType(Texts byteType) { this.byteType = byteType; };
    public void setProgrammerHistoryTextPane(JTextPane programmerHistoryTextPane) { this.programmerHistoryTextPane = programmerHistoryTextPane; }

}
