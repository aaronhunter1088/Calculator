package Panels;

import Calculators.Calculator;
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
import static Types.CalculatorByte.*;
import static Types.CalculatorView.VIEW_PROGRAMMER;
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
            buttonBytes = new JButton(BYTE.getValue().toUpperCase()), buttonBases = new JButton(BASE.getValue()),
            buttonShift = new JButton(SHIFT.getValue());
    private boolean
            isOr = false, isModulus = false,
            isXor = false, isNot = false,
            isAnd = false, isShiftPressed = false;

    /* Constructors */
    /**
     * A zero argument constructor for creating a OLDProgrammerPanel
     */
    public ProgrammerPanel()
    {
        setName(VIEW_PROGRAMMER.getValue());
        LOGGER.info("Empty Programmer panel created");
    }

    /**
     * The main constructor used to create a ProgrammerPanel
     * @param calculator the calculator to use
     */
    public ProgrammerPanel(Calculator calculator)
    {
        setupProgrammerPanel(calculator);
        LOGGER.info("Programmer panel created");
    }

    /* Start of methods here */
    /**
     * The main method used to define the ProgrammerPanel
     * and all of its components and their actions
     * @param calculator the calculator
     */
    public void setupProgrammerPanel(Calculator calculator)
    {
        this.calculator = calculator;
        setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        if (calculator.getCalculatorBase() == null) { calculator.setCalculatorBase(BASE_DECIMAL); }
        if (calculator.getCalculatorByte() == null) { calculator.setCalculatorByte(BYTE_BYTE); }
        setSize(new Dimension(227,383)); // sets main size
        setMinimumSize(new Dimension(227, 383)); // sets minimum size
        setupProgrammerPanelComponents();
        addComponentsToPanel();
        setName(VIEW_PROGRAMMER.getValue());
        LOGGER.info("Finished setting up {} panel", VIEW_PROGRAMMER.getValue());
    }

    /**
     * Clears button actions, sets the CalculatorView,
     * CalculatorBase, ConverterType, and finally
     * sets up the OLDProgrammerPanel and its components
     */
    private void setupProgrammerPanelComponents()
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllBasicPanelButtons(),
                        calculator.getAllBasicPanelOperatorButtons(),
                        calculator.getAllNumberButtons(),
                        calculator.getAllMemoryPanelButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .toList();
        allButtons
            .forEach(button -> Stream.of(button.getActionListeners())
                .forEach(al -> {
                    LOGGER.debug("Removing action listener from button: " + button.getName());
                    button.removeActionListener(al);
                }));
        LOGGER.debug("Actions removed");
        calculator.setupNumberButtons();
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupButtonBlank1();
        calculator.setupMemoryButtons(); // MR MC MS M+ M- H
        calculator.setupBasicPanelButtons(); // common
        setupProgrammerHistoryZone();
        setupProgrammerPanelButtons();
        LOGGER.info("Finished configuring the buttons");
    }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpMenu()
    {
        LOGGER.warn("IMPLEMENT Help Menu");
        LOGGER.info("Setting up the help menu for programmer panel");
        String helpString = """
                How to use the %s Calculator
                
                Using the programmer operators:
                Steps listed here...
                
                Using the function operators:
                Steps listed here...
                
                Described below are how each button works from the top left down in detail.
                """
                .formatted(VIEW_PROGRAMMER.getValue());
        calculator.updateHelpMenu(helpString);
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
     * The main method to set up the Programmer
     * button panels not in the memory panel
     */
    private void setupProgrammerPanelButtons()
    {
        LOGGER.debug("Configuring Programmer Panel buttons...");
        List<JButton> allButtons =
                Stream.of(getAllProgrammerOperatorButtons(),
                                calculator.getAllBasicPanelButtons(),
                                getAllHexadecimalButtons())
                        .flatMap(Collection::stream) // Flatten into a stream of JButton objects
                        .toList();
        allButtons.forEach(button -> {
            button.setFont(mainFont);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorder(new LineBorder(Color.BLACK));
            button.setEnabled(true);
        });
        buttonModulus.setName(MODULUS.name());
        buttonModulus.addActionListener(this::performButtonModulusAction);
        LOGGER.debug("Modulus button configured");
        buttonLeftParenthesis.setName(LEFT_PARENTHESIS.name());
        buttonLeftParenthesis.addActionListener(action -> LOGGER.warn("IMPLEMENT Left ("));
        LOGGER.warn("Left Parenthesis button needs to be configured");
        buttonRightParenthesis.setName(RIGHT_PARENTHESIS.name());
        buttonRightParenthesis.addActionListener(action -> LOGGER.warn("IMPLEMENT Right )"));
        LOGGER.warn("Right Parenthesis button needs to be configured");
        buttonRotateLeft.setName(ROL.name());
        buttonRotateLeft.addActionListener(action -> LOGGER.warn("IMPLEMENT buttonRotateLeft"));
        LOGGER.warn("RoL button needs to be configured");
        buttonRotateRight.setName(ROR.name());
        buttonRotateRight.addActionListener(action -> LOGGER.warn("IMPLEMENT buttonRotateRight"));
        LOGGER.warn("RoR button needs to be configured");
        buttonOr.setName(OR.name());
        buttonOr.addActionListener(this::performButtonOrAction);
        LOGGER.debug("Or button configured");
        buttonXor.setName(XOR.name());
        buttonXor.addActionListener(this::performButtonXorAction);
        LOGGER.debug("Xor button configured");
        buttonShiftLeft.setName(LSH.name());
        buttonShiftLeft.addActionListener(action -> LOGGER.warn("IMPLEMENT buttonShiftLeft"));
        LOGGER.debug("Left Shift button needs to be configured");
        buttonShiftRight.setName(RSH.name());
        buttonShiftRight.addActionListener(action -> LOGGER.warn("IMPLEMENT buttonShiftRight"));
        LOGGER.debug("Right Shift button needs to be configured");
        buttonNot.setName(NOT.name());
        buttonNot.addActionListener(this::performButtonNotAction);
        LOGGER.debug("Not button configured");
        buttonAnd.setName(AND.name());
        buttonAnd.addActionListener(action -> LOGGER.warn("IMPLEMENT buttonAdd"));
        LOGGER.debug("And button needs to be configured");
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
            hexadecimalNumberButton.addActionListener(this::performNumberButtonActions);
        });
        updateButtonsBasedOnBase();
        LOGGER.debug("Hexadecimal buttons configured");
        buttonShift.setName(SHIFT.name());
        buttonShift.addActionListener(this::performButtonShiftAction);
        LOGGER.debug("Shift button needs to be configured");
        buttonBytes.setName("Bytes");
        buttonBytes.setPreferredSize(new Dimension(70, 35) );
        buttonBytes.addActionListener(this::performButtonByteAction);
        LOGGER.debug("Bytes button configured");
        buttonBases.setName("Bases");
        buttonBases.setPreferredSize(new Dimension(70, 35) );
        buttonBases.addActionListener(this::performButtonBaseAction);
        LOGGER.debug("ButtonBytes needs to be configured");
    }

    /**
     * Enables the appropriate buttons based on
     * the current CalculatorBase
     */
    public void updateButtonsBasedOnBase()
    {
        switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                setButtons2To9(false);
                setButtonsAToF(false);
                calculator.getButton0().setEnabled(true);
                calculator.getButton1().setEnabled(true);
            }
            case BASE_OCTAL -> {
                calculator.getButton0().setEnabled(true);
                calculator.getButton1().setEnabled(true);
                setButtons2To9(true);
                setButtonsAToF(false);
                calculator.getButton8().setEnabled(false);
                calculator.getButton9().setEnabled(false);
            }
            case BASE_DECIMAL -> {
                calculator.getButtonDecimal().setEnabled(true);
                calculator.getButton0().setEnabled(true);
                calculator.getButton1().setEnabled(true);
                setButtons2To9(true);
                setButtonsAToF(false);
            }
            case BASE_HEXADECIMAL -> {
                calculator.getButton0().setEnabled(true);
                calculator.getButton1().setEnabled(true);
                setButtons2To9(true);
                setButtonsAToF(true);
            }
        }
    }

    /**
     * Method used to enable/disable the number buttons 2-9
     * @param isEnabled boolean to set the number buttons to
     */
    public void setButtons2To9(boolean isEnabled)
    {
        //Collection<JButton> buttonsWithout0Or1 = new ArrayList<>(calculator.getAllNumberButtons());
        //buttonsWithout0Or1.removeIf(btn -> btn.getName().equals("0") || btn.getName().equals("1"));
        //buttonsWithout0Or1.forEach(button -> button.setEnabled(isEnabled));

        calculator.getAllNumberButtons()
                .forEach(button -> {
                    if (!"0".equals(button.getName()) ||!"0".equals(button.getName()))
                        button.setEnabled(isEnabled);
                });
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
                buttonShiftRight, buttonNot, buttonAnd, buttonShift, buttonBytes, buttonBases);
    }

    /**
     * Returns the programmer hexadecimal buttons.
     *
     * @return Collection of buttons
     */
    public List<JButton> getAllHexadecimalButtons()
    { return Arrays.asList(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF); }

    /**
     * Returns the value in its alternative base form
     * @return String the base representation of the current value
     */
    public String addBaseRepresentation()
    {
        return switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> separateBits(calculator.convertValueToBinary());
            case BASE_OCTAL -> calculator.convertValueToOctal(); // TODO: add similar separateBits method for octal, grouping by 3s
            case BASE_DECIMAL -> calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]);
            case BASE_HEXADECIMAL -> calculator.getValues()[calculator.getValuesPosition()]; // TODO: may need to add separateBits for hexa numbers
        };
    }

    /**
     * Returns the byte and base value
     * @return String the byte and base
     *
     * Format: <String><Space><Space><String>
     */
    public String displayByteAndBase()
    {
        return """
                %s  %s
                """
                .formatted(
                      calculator.getCalculatorByte().getValue(),
                      calculator.getCalculatorBase().getValue()
                );
    }

    /**
     * Takes a String byte and adds a space between every
     * pair of 4 bits. Will always return a "full byte" based on
     * current byte value.
     * If the representation is empty, or less than 4 bits, it
     * will return the representation as is, otherwise it will
     * first remove any spaces and then add spaces between.
     * @param representation the String to alter
     * @return String the altered representation with spaces
     */
    public String separateBits(String representation)
    {
        if (representation.isEmpty()) return representation;
        else if (representation.length() < 4) return representation;
        StringBuilder sb = new StringBuilder();
        representation = representation.replace(SPACE.getValue(), BLANK.getValue());
        boolean operatorSet = calculator.determineIfAnyBasicOperatorWasPushed();
        if (operatorSet) representation = calculator.returnWithoutAnyOperator(representation);
        String respaced = switch (calculator.getCalculatorByte()) {
            case BYTE_BYTE -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE.getValue());
                sb.append(representation, 4, representation.length());
                yield sb.toString();
            }
            case BYTE_WORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE.getValue());
                sb.append(representation, 4, 8);
                sb.append(SPACE.getValue());
                sb.append(representation, 8, 12);
                sb.append(SPACE.getValue());
                sb.append(representation, 12, representation.length());
                yield sb.toString();
            }
            case BYTE_DWORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE.getValue());
                sb.append(representation, 4, 8);
                sb.append(SPACE.getValue());
                sb.append(representation, 8, 12);
                sb.append(SPACE.getValue());
                sb.append(representation, 12, 16);
                sb.append(calculator.addNewLines(1));
                sb.append(representation, 16, 20);
                sb.append(SPACE.getValue());
                sb.append(representation, 20, 24);
                sb.append(SPACE.getValue());
                sb.append(representation, 24, 28);
                sb.append(SPACE.getValue());
                sb.append(representation, 28, representation.length());
                yield sb.toString();
            }
            case BYTE_QWORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE.getValue());
                sb.append(representation, 4, 8);
                sb.append(SPACE.getValue());
                sb.append(representation, 8, 12);
                sb.append(SPACE.getValue());
                sb.append(representation, 12, 16);
                sb.append(calculator.addNewLines(1));
                sb.append(representation, 16, 20);
                sb.append(SPACE.getValue());
                sb.append(representation, 20, 24);
                sb.append(SPACE.getValue());
                sb.append(representation, 24, 28);
                sb.append(SPACE.getValue());
                sb.append(representation, 28, 32);
                sb.append(calculator.addNewLines(1));
                sb.append(representation, 32, 36);
                sb.append(SPACE.getValue());
                sb.append(representation, 36, 40);
                sb.append(SPACE.getValue());
                sb.append(representation, 40, 44);
                sb.append(SPACE.getValue());
                sb.append(representation, 44, 48);
                sb.append(calculator.addNewLines(1));
                sb.append(representation, 48, 52);
                sb.append(SPACE.getValue());
                sb.append(representation, 52, 56);
                sb.append(SPACE.getValue());
                sb.append(representation, 56, 60);
                sb.append(SPACE.getValue());
                sb.append(representation, 60, representation.length());
                yield sb.toString();
            }
        };
        if (operatorSet && calculator.getValuesPosition() == 0)
            respaced = respaced + SPACE.getValue() + calculator.getActiveBasicPanelOperator();
        return respaced;
    }

    /**
     * Add text to the text pane when using the programmer panel
     * @param text the text to add
     */
    public void appendTextToProgrammerPane(String text)
    {
        StyledDocument doc = calculator.getTextPane().getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            //
            SimpleAttributeSet attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
            String byteRepresentation = displayByteAndBase();
            doc.insertString(0, byteRepresentation, doc.getStyle("alignLeft"));
            doc.setParagraphAttributes(0, byteRepresentation.length(), attribs, false);
            //
            attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            doc.insertString(doc.getLength(), calculator.addNewLines(1)+text+calculator.addNewLines(1), doc.getStyle("alignRight"));
            doc.setParagraphAttributes(doc.getLength() - text.length(), text.length(), attribs, false);
        }
        catch (BadLocationException e) { calculator.logException(e); }
    }

    /**
     * The programmer actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performButtonDeleteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Programmer Action for {} started", buttonChoice);
        switch (calculator.getCalculatorBase())
        {
            case BASE_BINARY -> {
                if (calculator.textPaneContainsBadText())
                {
                    calculator.appendTextToPane(BLANK.getValue());
                    calculator.confirm("Contains bad text. Pressed " + buttonChoice);
                }
                else if (calculator.getTextPaneValueForProgrammerPanel().isEmpty() && calculator.getValues()[0].isEmpty())
                {
                    calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
                    calculator.confirm("No need to perform " + DELETE + " operation");
                }
                else
                {
                    if (calculator.getValuesPosition() == 1 && calculator.getValues()[1].isEmpty())
                    { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete
                    if (calculator.getValues()[0].isEmpty())
                    { calculator.getValues()[0] = calculator.getTextPaneValueForProgrammerPanel(); }
                    LOGGER.debug("values[{}]: {}", calculator.getValuesPosition(), calculator.getValues()[calculator.getValuesPosition()]);
                    LOGGER.debug("textPane: {}", calculator.getTextPaneValueForProgrammerPanel());
                    // if no operator has been pushed but text pane has value
                    if (!calculator.isAdding() && !calculator.isSubtracting()
                        && !calculator.isMultiplying() && !calculator.isDividing()
                        && !calculator.getTextPaneValue().isEmpty())
                    {
                        String substring = calculator.getTextPaneValueForProgrammerPanel().substring(0, calculator.getTextPaneValueForProgrammerPanel().length()-1);
                        if (substring.endsWith(SPACE.getValue())) substring = substring.substring(0,substring.length()-1);
                        boolean updateValue = substring.isEmpty();
                        calculator.appendTextToPane(substring, updateValue);
                    }
                    // if an operator was pushed, remove operator from text and reset operator
                    else if (calculator.determineIfAnyBasicOperatorWasPushed())
                    {
                        LOGGER.debug("An operator has been pushed");
                        if (calculator.getValuesPosition() == 0)
                        {
                            if (calculator.isAdding()) calculator.setIsAdding(false);
                            else if (calculator.isSubtracting()) calculator.setIsSubtracting(false);
                            else if (calculator.isMultiplying()) calculator.setIsMultiplying(false);
                            else /* (calculator.isDividing())*/ calculator.setIsDividing(false);
                            String textWithoutOperator = calculator.getTextPaneWithoutAnyOperator();
                            calculator.appendTextToPane(textWithoutOperator);
                        }
                        else
                        {
                            String substring = calculator.getTextPaneValueForProgrammerPanel().substring(0,calculator.getTextPaneValueForProgrammerPanel().length()-1);
                            calculator.appendTextToPane(substring);
                        }
                    }
                    calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[calculator.getValuesPosition()]));
                    calculator.setIsNumberNegative(calculator.getValues()[calculator.getValuesPosition()].contains(SUBTRACTION.getValue()));
                    calculator.writeHistory(buttonChoice, false);
                    calculator.confirm("Pressed " + buttonChoice);
                }
            }
            case BASE_OCTAL -> {}
            case BASE_DECIMAL -> {
                calculator.performDeleteButtonAction(actionEvent);
            }
            case BASE_HEXADECIMAL -> {}
        }
    }

    /**
     * The actions to perform when you click Modulus.
     * Modulus returns the remainder of a division operation.
     * @param actionEvent the click action
     */
    public void performButtonModulusAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty() )
        { calculator.confirm("Pressed "+buttonChoice+". No action"); }
        else if (!calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            isModulus = true;
            String updatedTextValue = calculator.getTextPaneValueForProgrammerPanel() + SPACE.getValue() + buttonChoice;
            //calculator.appendTextToPane(updatedTextValue); // KEPT TO SHOW DIFFERENCE. USE METHOD BELOW
            appendTextToProgrammerPane(updatedTextValue);
            calculator.resetCalculatorOperations(true);
            calculator.confirm("Pressed " + buttonChoice);
        }
        else // (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            isModulus = true;
            String modulusResult = performModulus();
            calculator.getValues()[0] = modulusResult;
            calculator.appendTextToPane(modulusResult);
            calculator.confirm("Modulus Actions finished");
        }
    }
    /**
     * The inner logic for modulus
     */
    private String performModulus()
    {
        LOGGER.info("Performing modulus");
        int firstResult = 0;
        try {
            firstResult = Integer.parseInt(calculator.getValues()[0]) % Integer.parseInt(calculator.getValues()[1]);
        }
        catch (ArithmeticException ae)
        {
            calculator.logException(ae);
        }
        LOGGER.debug("modulus: " + firstResult);
        return String.valueOf(firstResult);
    }

    /* Calculator helper methods */
//    /**
//     * This method returns the String operator that was activated
//     * Results could be: '+', '-', '*', '/' or '' if no
//     * operator was recorded as being activated
//     * @return String the basic operation that was pushed
//     */
//    public String getActiveBasicPanelOperator()
//    {
//        String results = "";
//        if (isOr) { results = OR.getValue(); }
//        else if (isModulus) { results = MODULUS.getValue(); }
//        else if (isXor) { results = XOR.getValue(); }
//        else if (isNot) { results = NOT.getValue(); }
//        else if (isAnd) { results = AND.getValue(); }
//        LOGGER.info("operator: {}", (results.isEmpty() ? "no basic operator pushed" : results));
//        return results;
//    }

    /**
     * This method returns true or false depending
     * on if an operator was pushed or not. The
     * operators checked are isOr, isModulus,
     * isXor, isNot, and isAnd
     *
     * @return true if any operator was pushed, false otherwise
     */
    public boolean determineIfAnyProgrammerOperatorWasPushed()
    { return isOr || isModulus || isXor || isNot || isAnd; }

    /**
     * This method returns the String operator that was activated
     * Results could be: 'OR', 'MOD', 'XOR', 'NOT' or 'AND'
     * operator was recorded as being activated
     * @return String the basic operation that was pushed
     */
    public String getActiveProgrammerPanelOperator()
    {
        String results = BLANK.getValue();
        if (isOr) { results = OR.getValue(); }
        else if (isModulus) { results = MODULUS.getValue(); }
        else if (isXor) { results = XOR.getValue(); }
        else if (isNot) { results = NOT.getValue(); }
        else if (isAnd) { results = AND.getValue(); }
        if (results.isEmpty()) { LOGGER.info("no programmer operator pushed"); }
        else { LOGGER.info("operator: {}", results); }
        return results;
    }

    /**
     * The actions to perform when Or is clicked
     */
    public void performButtonOrAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.info("button: " + buttonChoice);
        // Ex: 2
        if (calculator.getValues()[0].isEmpty()) // Requires both v[0] and v[1] to be set
        {
            calculator.setIsFirstNumber(true);
            calculator.confirm("Pressed " + buttonChoice + ", no affect");
        }
        else if (!calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            LOGGER.debug("Appending {} to text pane", buttonChoice);
            calculator.setIsFirstNumber(false);
            calculator.appendTextToPane(calculator.getValues()[0] + SPACE.getValue() + buttonChoice); // Ex: 2 OR
            calculator.setValuesPosition(1);
            calculator.confirm("Pressed " + buttonChoice);
        }
        else if (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            String sb = performOr(); // Ex: 2 OR 3, or 2 OR 3 OR (continued operation)
            calculator.getValues()[0] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, sb);
            // because the Or operation always returns the result in binary, we need to append the result
            // of the operation in the same base as we currently are in
            switch (calculator.getCalculatorBase())
            {
                case BASE_BINARY -> { calculator.appendTextToPane(sb); }
                case BASE_OCTAL -> { calculator.appendTextToPane(calculator.convertValueToOctal()); }
                case BASE_DECIMAL -> { calculator.appendTextToPane(calculator.getValues()[0]); }
                case BASE_HEXADECIMAL -> { calculator.appendTextToPane(calculator.convertValueToHexadecimal());}
            }
            calculator.resetCalculatorOperations(false);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }
    /**
     * The inner logic for Or
     * @return the result of the Or operation in binary
     */
    private String performOr()
    {
        LOGGER.debug("Performing Or");
        StringBuilder sb = new StringBuilder();
        // TODO: Double check that the values are the same length (in terms of bytes)
        var value1InBinary = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[0]);
        var value2InBinary = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[1]);
        for (int i=0; i<value1InBinary.length(); i++)
        {
            String letter = "0";
            if (ZERO.getValue().equals(String.valueOf(value1InBinary.charAt(i)))
               && ZERO.getValue().equals(String.valueOf(value2InBinary.charAt(i))))
            { sb.append(ZERO.getValue()); } // if the characters at both getValues() at the same position are the same and equal 0
            else
            { sb.append(ONE.getValue()); } // otherwise
            LOGGER.info("{} OR {} = {}", value1InBinary.charAt(i), value2InBinary.charAt(i), letter);
        }
        LOGGER.info("{} OR {} = {}", calculator.getValues()[0], calculator.getValues()[1], sb.toString());
        return sb.toString();
    }

    /**
     * The actions to perform when Xor is clicked
     */
    public void performButtonXorAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.info("button: " + buttonChoice);
        if (isXor || (calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty()))
        { calculator.confirm("Pressed "+buttonChoice+". No action performed."); }
        // if v[0] is set, v[1] is not, and we have not pushed Xor yet
        else if (!isXor && !calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            String updatedText = calculator.getTextPaneValueForProgrammerPanel() + SPACE.getValue() + buttonChoice;
            appendTextToProgrammerPane(updatedText);
            isXor = true; // TODO: determine when to set to false, currently 2 XOR 3 returns 3 XOR, because XOR is still on, and we are adding it back
            calculator.resetCalculatorOperations(true);
            calculator.confirm("Pressed " + buttonChoice);
        }
        else if (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            String xorResult = performXor();
            appendTextToProgrammerPane(xorResult);
            isXor = true;
            calculator.confirm("Pressed " + buttonChoice);
        }
    }
    /**
     * The inner logic for Xor
     * @return String the result of the operation
     */
    private String performXor()
    {
        LOGGER.info("Performing Xor");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<calculator.getValues()[0].length(); i++) {
            LOGGER.info("i:{}, v[0]:{}, v[1]:{}", i, calculator.getValues()[0].charAt(i), calculator.getValues()[1].charAt(i));
            // if the characters at both getValues() at the same position are the same and equal 0
            if (ZERO.getValue().equals(String.valueOf(calculator.getValues()[0].charAt(i)))
                && ZERO.getValue().equals(String.valueOf(calculator.getValues()[1].charAt(i))) )
            { sb.append(ZERO.getValue()); }
            else { sb.append(ONE.getValue()); }
        }
        return String.valueOf(sb);
    }

    /**
     * The actions to perform when the Not button is clicked
     */
    public void performButtonNotAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        buttonNot.setEnabled(false);
        String textPaneValue = calculator.getTextPaneValueForProgrammerPanel();
        LOGGER.debug("before operation execution: {}", textPaneValue);
        StringBuilder newBuffer = new StringBuilder();
        for (int i = 0; i < textPaneValue.length(); i++) {
            String s = Character.toString(textPaneValue.charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: {}", newBuffer);
        calculator.appendTextToPane(newBuffer.toString());
        calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, newBuffer.toString());
        LOGGER.info("{} complete", buttonChoice);
        calculator.confirm("Pressed " + buttonChoice);
    }

    /**
     * The actions to perform when the Shift button is clicked
     */
    public void performButtonShiftAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.debug("isShiftPressed: {}", isShiftPressed);
        if (isShiftPressed) {
            isShiftPressed = false;
            buttonsPanel.remove(buttonRotateLeft);
            buttonsPanel.remove(buttonRotateRight);
            buttonsPanel.remove(buttonBytes);
            buttonsPanel.remove(buttonBases);
            addComponent(buttonsPanel, buttonShiftLeft, 0, 0);
            addComponent(buttonsPanel, buttonShiftRight, 0, 1);
            addComponent(buttonsPanel, buttonOr, 0, 2);
            addComponent(buttonsPanel, buttonXor, 0, 3);
            addComponent(buttonsPanel, buttonNot, 0, 4);
            addComponent(buttonsPanel, buttonAnd, 0, 5);
        } else {
            isShiftPressed = true;
            buttonsPanel.remove(buttonShiftLeft);
            buttonsPanel.remove(buttonShiftRight);
            buttonsPanel.remove(buttonOr);
            buttonsPanel.remove(buttonXor);
            buttonsPanel.remove(buttonNot);
            buttonsPanel.remove(buttonAnd);
            addComponent(buttonsPanel, buttonRotateLeft, 0, 0);
            addComponent(buttonsPanel, buttonRotateRight, 0, 1);
            addComponent(buttonsPanel, buttonBytes, 0, 2, null, 2, 1, 1, 1, 0,0);
            addComponent(buttonsPanel, buttonBases, 0, 4, null, 2, 1, 1, 1, 0,0);
        }
        buttonsPanel.repaint();
        buttonsPanel.revalidate();
        LOGGER.info("Action for {} completed. isShiftPressed: {}{}", buttonChoice, isShiftPressed, calculator.addNewLines(1));
    }

    /**
     * The actions to perform when the Bytes button is clicked
     */
    public void performButtonByteAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        switch(calculator.getCalculatorByte())
        {
            case BYTE_BYTE -> {
                calculator.setCalculatorByte(BYTE_WORD);
            }
            case BYTE_WORD -> {
                calculator.setCalculatorByte(BYTE_DWORD);
            }
            case BYTE_DWORD  -> {
                calculator.setCalculatorByte(BYTE_QWORD);
            }
            case BYTE_QWORD -> {
                calculator.setCalculatorByte(BYTE_BYTE);
            }
        }
        calculator.writeHistoryWithMessage(buttonBytes.getName(), false, "Updated bytes to " + calculator.getCalculatorByte().getValue());
        appendTextToProgrammerPane(addBaseRepresentation());
        calculator.confirm("Bytes updated");
    }

    /**
     * The actions to perform when the Bases button is clicked
     */
    public void performButtonBaseAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        boolean updateValues = false;
        String converted = BLANK.getValue();
        switch(calculator.getCalculatorBase())
        {
            case BASE_BINARY -> {
                // TODO: Not just is the correct length but also if the length is less than
                converted = calculator.getTextPaneValueForProgrammerPanel();
                updateValues = getAllowedLengthsOfTextPane().contains(converted.length());
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_OCTAL, calculator.getTextPaneValueForProgrammerPanel());
                    calculator.getValues()[calculator.getValuesPosition()] = converted;
                    calculator.setPreviousBase(BASE_BINARY);
                    calculator.setCalculatorBase(BASE_OCTAL);
                    appendTextToProgrammerPane(calculator.getValues()[calculator.getValuesPosition()]);
                }
            }
            case BASE_OCTAL -> {
                calculator.setCalculatorBase(BASE_DECIMAL);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_OCTAL, BASE_DECIMAL, calculator.getTextPaneValueForProgrammerPanel());
                    calculator.getValues()[calculator.getValuesPosition()] = converted;
                    calculator.setPreviousBase(BASE_OCTAL);
                    appendTextToProgrammerPane(calculator.getValues()[calculator.getValuesPosition()]);
                }
            }
            case BASE_DECIMAL  -> {
                calculator.setCalculatorBase(BASE_HEXADECIMAL);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, calculator.getTextPaneValueForProgrammerPanel());
                    calculator.setPreviousBase(BASE_DECIMAL);
                    appendTextToProgrammerPane(converted);
                }
            }
            case BASE_HEXADECIMAL -> {
                converted = calculator.convertFromBaseToBase(BASE_HEXADECIMAL, BASE_BINARY, calculator.getTextPaneValueForProgrammerPanel());
                calculator.setCalculatorBase(BASE_BINARY);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    calculator.setPreviousBase(BASE_HEXADECIMAL);
                    appendTextToProgrammerPane(separateBits(converted));
                }
            }
        }
        updateButtonsBasedOnBase();
        //appendToPane(addBaseRepresentation()); // must call to update textPane base value
        calculator.writeHistoryWithMessage(buttonBases.getName(), false, " Updated bases to " + this.calculator.getCalculatorBase().getValue());
        calculator.writeHistoryWithMessage(buttonBases.getName(), false, " Result: " + converted);
        calculator.confirm("Bases updated to " + calculator.getCalculatorBase());
    }

    /**
     * The actions to perform when clicking a hexadecimal number button
     * @param actionEvent the click action
     */
    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Programmer Action for {} started", buttonChoice);
        if (BASE_BINARY == calculator.getCalculatorBase())
        {
            calculator.performNumberButtonAction(actionEvent);
            var allowedLengthMinusNewLines = getAllowedLengthsOfTextPane();
            String textPaneText = calculator.getTextPaneValueForProgrammerPanel();
            if (allowedLengthMinusNewLines.contains(textPaneText.length()))
            { calculator.confirm("Byte length "+allowedLengthMinusNewLines+" already reached"); }
            else
            {
                appendTextToProgrammerPane(separateBits(textPaneText + buttonChoice));
                calculator.writeHistory(buttonChoice, false);
                textPaneText = calculator.getTextPaneValueForProgrammerPanel();
                if (allowedLengthMinusNewLines.contains(textPaneText.length()))
                {
                    calculator.setPreviousBase(BASE_BINARY); // set previousBase since number is fully formed
                    calculator.getValues()[calculator.getValuesPosition()] = textPaneText;
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.convertValueToDecimal();
                    calculator.confirm("Byte length "+allowedLengthMinusNewLines+" reached with this input");
                }
                calculator.confirm("Pressed " + buttonChoice);
            }
        }
        else if (BASE_OCTAL == calculator.getCalculatorBase())
        { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else if (BASE_DECIMAL == calculator.getCalculatorBase())
        { calculator.performNumberButtonAction(actionEvent); }
        else /* (HEXADECIMAL == calculator.getCalculatorBase()) */
        { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }
    }

    /**
     * Returns the correct lengths allowed when using base binary
     * and different byte values. This uses only the value in the
     * text pane.
     * @return List the lengths allowed for different bytes
     */
    private List<Integer> getAllowedLengthsOfTextPane()
    {
        List<Integer> lengthsAllowed = new ArrayList<>();
        switch (calculator.getCalculatorByte())
        {
            // TODO: check shorthand
            case BYTE_BYTE -> {
                //IntStream.rangeClosed(1,8).forEach(lengthsAllowed::add); // for shorthand
                lengthsAllowed.add(8);  // 00000101, or 8 total characters
                lengthsAllowed.add(10); // 00000101_<operator>, or 10 total characters
            }
            case BYTE_WORD -> {
                //IntStream.rangeClosed(1,18).forEach(lengthsAllowed::add);
                lengthsAllowed.add(16); // 0000010100000101, or 16 total characters
                lengthsAllowed.add(18); // 0000010100000101_<operator>, or 16 + 2 = 18
            }
            case BYTE_DWORD -> {
                //IntStream.rangeClosed(1,38).forEach(lengthsAllowed::add);
                lengthsAllowed.add(32); // double word minus newlines
                lengthsAllowed.add(34); // with operator
            }
            case BYTE_QWORD -> {
                //IntStream.rangeClosed(1,75).forEach(lengthsAllowed::add);
                lengthsAllowed.add(64); // double dword minus newlines
                lengthsAllowed.add(66); // with operator
            }
        }
        return lengthsAllowed;
    }

    /**
     * The actions to perform when History is clicked
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (HISTORY_OPEN.getValue().equals(calculator.getButtonHistory().getText()))
        {
            calculator.getButtonHistory().setText(HISTORY_CLOSED.getValue());
            programmerPanel.remove(historyPanel);
            addComponent(programmerPanel, buttonsPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
        else
        {
            calculator.getButtonHistory().setText(HISTORY_OPEN.getValue());
            programmerPanel.remove(buttonsPanel);
            addComponent(programmerPanel, historyPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    public void resetProgrammerOperators(boolean reset)
    {
        isOr = reset;
        isModulus = reset;
        isXor = reset;
        isNot = reset;
        isAnd = reset;
        LOGGER.debug("Main programmer operators reset to {}", reset);
    }

    /* Getters */
    public JTextPane getHistoryTextPane() { return programmerHistoryTextPane; }
    public boolean isOr() { return isOr; }
    public boolean isModulus() { return isModulus; }
    public boolean isXor() { return isXor; }
    public boolean isNot() { return isNot; }
    public boolean isAnd() { return isAnd; }
    public boolean isShiftPressed() { return isShiftPressed; }

    /* Setters */
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) { super.setLayout(panelLayout); }
    public void setProgrammerHistoryTextPane(JTextPane programmerHistoryTextPane) { this.programmerHistoryTextPane = programmerHistoryTextPane; }

    public void setOr(boolean or) { isOr = or; }
    public void setModulus(boolean modulus) { isModulus = modulus; }
    public void setXor(boolean xor) { isXor = xor; }
    public void setNot(boolean not) { isNot = not; }
    public void setAnd(boolean and) { isAnd = and; }
    public void setShiftPressed(boolean shiftPressed) { isShiftPressed = shiftPressed; }
}
