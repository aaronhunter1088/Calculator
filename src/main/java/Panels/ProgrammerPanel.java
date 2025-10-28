package Panels;

import Calculators.Calculator;
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
import static Utilities.LoggingUtil.*;

public class ProgrammerPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private Calculator calculator;
    private GridBagConstraints constraints;
    private JPanel programmerPanel,
                   memoryPanel,
                   buttonsPanel,
                   historyPanel;
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
    private boolean isOr, isModulus, isXor, isNot, isAnd, isShiftPressed, isInitialized;

    /**************** CONSTRUCTORS ****************/

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

    /**************** START OF METHODS ****************/

    /**
     * The main method used to define the ProgrammerPanel
     * and all of its components and their actions
     * @param calculator the calculator
     */
    public void setupProgrammerPanel(Calculator calculator)
    {
        if (!isInitialized)
        {
            this.calculator = calculator;
            this.programmerPanel = new JPanel(new GridBagLayout());
            this.programmerPanel.setName("ProgrammerPanel");
            this.memoryPanel = new JPanel(new GridBagLayout());
            this.memoryPanel.setName("MemoryPanel");
            this.buttonsPanel = new JPanel(new GridBagLayout());
            this.buttonsPanel.setName("ButtonsPanel");
            this.historyPanel = new JPanel(new GridBagLayout());
            this.historyPanel.setName("HistoryPanel");
            setLayout(new GridBagLayout());
            constraints = new GridBagConstraints();
            if (calculator.getCalculatorBase() == null) { calculator.setCalculatorBase(BASE_DECIMAL); }
            if (calculator.getCalculatorByte() == null) { calculator.setCalculatorByte(BYTE_BYTE); }
            setSize(new Dimension(227,383)); // sets main size
            setMinimumSize(new Dimension(227, 383)); // sets minimum size
            setupProgrammerHistoryZone();
        }
        setupProgrammerPanelComponents();
        addComponentsToPanel();
        setName(VIEW_PROGRAMMER.getValue());
        isInitialized = true;
        LOGGER.info("Finished setting up {} panel", VIEW_PROGRAMMER.getValue());
    }

    /**
     * Clears button actions, sets the CalculatorView,
     * CalculatorBase, CalculatorConverterType, and finally
     * sets up the OLDProgrammerPanel and its components
     */
    private void setupProgrammerPanelComponents()
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getCommonButtons(),
                        calculator.getNumberButtons(),
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
        setupHelpString();
        calculator.setupTextPane();
        calculator.setupButtonBlank1();
        calculator.setupMemoryButtons(); // MR MC MS M+ M- H
        calculator.setupCommonButtons(); // common
        setupProgrammerPanelButtons();
        LOGGER.info("Finished configuring the buttons");
    }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpString()
    {
        LOGGER.warn("Complete the Help string");
        LOGGER.info("Setting up the help string for programmer panel");
        calculator.setHelpString("""
                How to use the %s Calculator
                
                Using the programmer operators:
                Steps listed here...
                
                Using the function operators:
                Steps listed here...
                
                Described below are how each button works from the top left down in detail.
                """
                .formatted(VIEW_PROGRAMMER.getValue()));
        calculator.updateShowHelp();
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    public void addComponentsToPanel()
    {
        calculator.addComponent(this, constraints, programmerPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryStore(), 0, new Insets(0,1,0,0));
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryRecall(), 1, new Insets(0,0,0,0));
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryClear(), 2, new Insets(0,0,0,0));
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryAddition(), 3, new Insets(0,0,0,0));
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemorySubtraction(), 4, new Insets(0,0,0,0)); // right:1
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonHistory(), 5, new Insets(0,0,0,1));

        calculator.addComponent(this, constraints, programmerPanel, memoryPanel, 1, 0, new Insets(0,0,0,0), 1, 1, 1.0, 1.0, 0, 0);

        calculator.addComponent(this, constraints, buttonsPanel, buttonShiftLeft, 0, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonShiftRight, 0, 1);
        calculator.addComponent(this, constraints, buttonsPanel, buttonOr, 0, 2);
        calculator.addComponent(this, constraints, buttonsPanel, buttonXor, 0, 3);
        calculator.addComponent(this, constraints, buttonsPanel, buttonNot, 0, 4);
        calculator.addComponent(this, constraints, buttonsPanel, buttonAnd, 0, 5);
        calculator.addComponent(this, constraints, buttonsPanel, buttonShift, 1, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonModulus, 1, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonClearEntry(), 1, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonClear(), 1, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDelete(), 1, 4);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDivide(), 1, 5);
        calculator.addComponent(this, constraints, buttonsPanel, buttonA, 2, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonB, 2, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton7(), 2, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton8(), 2, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton9(), 2, 4);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonMultiply(),2, 5);
        calculator.addComponent(this, constraints, buttonsPanel, buttonC, 3, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonD,3, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton4(), 3, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton5(), 3, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton6(), 3, 4);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonSubtract(), 3, 5);
        calculator.addComponent(this, constraints, buttonsPanel, buttonE, 4, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonF,4, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton1(), 4, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton2(), 4, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton3(), 4, 4);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonAdd(), 4, 5);
        calculator.addComponent(this, constraints, buttonsPanel, buttonLeftParenthesis, 5, 0);
        calculator.addComponent(this, constraints, buttonsPanel, buttonRightParenthesis,5, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonNegate(), 5, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton0(), 5, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDecimal(), 5, 4);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonEquals(), 5, 5);

        calculator.addComponent(this, constraints, programmerPanel, buttonsPanel, 2, 0);

        calculator.addComponent(this, constraints, programmerPanel);
        LOGGER.info("Buttons added to the frame");
    }

    /**
     * Displays the history for the BasicPanel
     * during each active instance
     */
    public void setupProgrammerHistoryZone()
    {
        LOGGER.debug("Configuring ProgrammerHistoryZone...");
        constraints.anchor = GridBagConstraints.WEST;
        calculator.addComponent(this, constraints, historyPanel, new JLabel(HISTORY.getValue()), 0, 0); // space before with jtextarea

        calculator.setupHistoryTextPane();
        JScrollPane scrollPane = new JScrollPane(programmerHistoryTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(programmerHistoryTextPane.getSize());

        calculator.addComponent(this, constraints, historyPanel, scrollPane, 1, 0, new Insets(0,0,0,0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("ProgrammerHistoryZone configured");
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
                                calculator.getCommonButtons(),
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
        buttonLeftParenthesis.addActionListener(this::performLeftParenthesisButtonAction);
        LOGGER.warn("Right Parenthesis button needs to be configured");
        buttonRightParenthesis.setName(RIGHT_PARENTHESIS.name());
        buttonRightParenthesis.addActionListener(this::performRightParenthesisButtonAction);
        LOGGER.warn("Right Parenthesis button needs to be configured");
        buttonRotateLeft.setName(ROL.name());
        buttonRotateLeft.addActionListener(this::performRotateLeftButtonAction);
        LOGGER.debug("RoL button configured");
        buttonRotateRight.setName(ROR.name());
        buttonRotateRight.addActionListener(this::performRotateRightButtonAction);
        LOGGER.debug("RoR button configured");
        buttonOr.setName(OR.name());
        buttonOr.addActionListener(this::performOrButtonAction);
        LOGGER.debug("Or button configured");
        buttonXor.setName(XOR.name());
        buttonXor.addActionListener(this::performXorButtonAction);
        LOGGER.debug("Xor button configured");
        buttonShiftLeft.setName(LSH.name());
        buttonShiftLeft.addActionListener(this::performLeftShiftButtonAction);
        LOGGER.debug("Left Shift button configured");
        buttonShiftRight.setName(RSH.name());
        buttonShiftRight.addActionListener(this::performRightShiftButtonAction);
        LOGGER.debug("Right Shift button configured");
        buttonNot.setName(NOT.name());
        buttonNot.addActionListener(this::performNotButtonAction);
        LOGGER.debug("Not button configured");
        buttonAnd.setName(AND.name());
        buttonAnd.addActionListener(this::performAndButtonAction);
        LOGGER.debug("And button configured");
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
        LOGGER.debug("Hexadecimal buttons configured");
        enableDisableNumberButtonsBasedOnBase();
        buttonShift.setName(SHIFT.name());
        buttonShift.addActionListener(this::performShiftButtonAction);
        LOGGER.debug("Shift button configured");
        buttonBytes.setName("Bytes");
        buttonBytes.setPreferredSize(new Dimension(70, 35) );
        buttonBytes.addActionListener(this::performByteButtonAction);
        LOGGER.debug("Bytes button configured");
        buttonBases.setName("Bases");
        buttonBases.setPreferredSize(new Dimension(70, 35) );
        buttonBases.addActionListener(this::performBaseButtonAction);
        LOGGER.debug("Base Button configured");
    }

    /**
     * Enables the appropriate buttons based on
     * the current CalculatorBase
     */
    public void enableDisableNumberButtonsBasedOnBase()
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
        LOGGER.debug("Buttons enabled or disabled based on {}", calculator.getCalculatorBase());
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

        calculator.getNumberButtons()
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
        if (operatorSet) representation = calculator.getValueWithoutAnyOperator(representation);
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
            respaced = respaced + SPACE.getValue() + calculator.getActiveOperator();
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
            String byteAndBaseDisplay = displayByteAndBase();
            doc.insertString(0, byteAndBaseDisplay, doc.getStyle("alignLeft"));
            doc.setParagraphAttributes(0, byteAndBaseDisplay.length(), attribs, false);
            //
            attribs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
            doc.insertString(doc.getLength(), NEWLINE.getValue()+text+NEWLINE.getValue(), doc.getStyle("alignRight"));
            doc.setParagraphAttributes(doc.getLength() - text.length(), text.length(), attribs, false);
        }
        catch (BadLocationException e) { calculator.logException(e); }
    }

    /**
     * This method returns true or false depending
     * on if an operator was pushed or not. The
     * operators checked are: isOr, isModulus,
     * isXor, isNot, and isAnd
     *
     * @return true if any operator was pushed, false otherwise
     */
    public boolean isProgrammerOperatorActive()
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
     * Returns the correct lengths allowed when using base binary
     * and different byte values. This uses only the value in the
     * text pane.
     * @return List the lengths allowed for different bytes
     */
    private List<Integer> getAllowedLengthsOfTextPane()
    {
        List<Integer> lengthsAllowed = new ArrayList<>();
        lengthsAllowed.add(0);  // what if no input??
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

    public void resetProgrammerOperators(boolean reset)
    {
        isOr = reset;
        isModulus = reset;
        isXor = reset;
        isNot = reset;
        isAnd = reset;
        LOGGER.debug("Main programmer operators reset to {}", reset);
    }

    /**************** ACTIONS ****************/

    /**
     * The programmer actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performButtonDeleteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneTextValue = calculator.getTextPaneValue();
        switch (calculator.getCalculatorBase())
        {
            case BASE_BINARY -> {
                if (calculator.textPaneContainsBadText())
                {
                    calculator.appendTextToPane(calculator.getBadText());
                    confirm(calculator, LOGGER, "Contains bad text. Pressed " + buttonChoice);
                }
                else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty())
                {
                    calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
                    confirm(calculator, LOGGER, "No need to perform " + DELETE + " operation");
                }
                else
                {
                    if (calculator.getValuesPosition() == 1 && calculator.getValues()[1].isEmpty())
                    { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete
                    if (calculator.getValues()[0].isEmpty())
                    { calculator.getValues()[0] = calculator.getTextPaneValue(); }
                    logValuesAtPosition(calculator, LOGGER);
                    LOGGER.debug("textPane: {}", calculator.getTextPaneValue());
                    // if no operator has been pushed but text pane has value
                    if (!calculator.isAdding() && !calculator.isSubtracting()
                        && !calculator.isMultiplying() && !calculator.isDividing()
                        && !calculator.getTextPaneValue().isEmpty())
                    {
                        String substring = calculator.getTextPaneValue().substring(0, calculator.getTextPaneValue().length()-1);
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
                            String textWithoutOperator = calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue());
                            calculator.appendTextToPane(textWithoutOperator);
                        }
                        else
                        {
                            String substring = calculator.getTextPaneValue().substring(0,calculator.getTextPaneValue().length()-1);
                            calculator.appendTextToPane(substring);
                        }
                    }
                    calculator.getButtonDecimal().setEnabled(!calculator.isDecimalNumber(calculator.getValues()[calculator.getValuesPosition()]));
                    calculator.setIsNumberNegative(calculator.getValues()[calculator.getValuesPosition()].contains(SUBTRACTION.getValue()));
                    calculator.writeHistory(buttonChoice, false);
                    confirm(calculator, LOGGER, "Pressed " + buttonChoice);
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
        { confirm(calculator, LOGGER, "Pressed "+buttonChoice+". No action"); }
        else if (!calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            isModulus = true;
            String updatedTextValue = calculator.getTextPaneValue() + SPACE.getValue() + buttonChoice;
            //calculator.appendTextToPane(updatedTextValue); // KEPT TO SHOW DIFFERENCE. USE METHOD BELOW
            appendTextToProgrammerPane(updatedTextValue);
            calculator.resetCalculatorOperations(true);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
        else // (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            isModulus = true;
            String modulusResult = performModulus();
            calculator.getValues()[0] = modulusResult;
            calculator.appendTextToPane(modulusResult);
            confirm(calculator, LOGGER, "Modulus Actions finished");
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

    /**
     * The actions to perform when you click (.
     * @param actionEvent the click action
     */
    public void performLeftParenthesisButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.warn("Left Parenthesis button method created. Logic needs to be configured");
        appendTextToProgrammerPane(calculator.getTextPaneValue() + buttonChoice);
        calculator.writeHistory(buttonChoice, false);
        calculator.setIsPemdasActive(true);
        confirm(calculator, LOGGER, "Pressed " + buttonChoice);
    }
    /**
     * The inner logic for (
     * @return the result of the operation
     */
    public String performLeftParenthesis()
    {
        return "";
    }

    /**
     * The actions to perform when you click ).
     * @param actionEvent the click action
     */
    public void performRightParenthesisButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        LOGGER.warn("Right Parenthesis button method created. Logic needs to be configured");
        appendTextToProgrammerPane(calculator.getTextPaneValue() + buttonChoice);
        calculator.writeHistory(buttonChoice, false);
        confirm(calculator, LOGGER, "Pressed " + buttonChoice);
    }
    /**
     * The inner logic for )
     * @return the result of the operation
     */
    public String performRightParenthesis()
    {
        return "";
    }

    /**
     * The actions to perform when you click ROL.
     * @param actionEvent the click action
     */
    public void performRotateLeftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + ROL); }
        else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + ROL);
        }
        else
        {
            String rotateLeftResult = performRotateLeft();
            //String convertedResult = BASE_BINARY == calculator.getCalculatorBase() ? calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), rotateLeftResult) : rotateLeftResult;
            calculator.appendTextToPane(rotateLeftResult);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, rotateLeftResult);
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, LSH + " performed");
        }
    }
    /**
     * The inner logic for ROL
     * @return the result of the operation
     */
    public String performRotateLeft()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before rotate left: {}", positionedValue);
        String valueRotatedLeft = "";
        for (int bit=0; bit<positionedValue.length(); bit++) {
            if ((bit+1) < positionedValue.length()) {
                valueRotatedLeft += positionedValue.charAt(bit+1);
            } else {
                valueRotatedLeft += positionedValue.charAt(0);
            }
        }
        String rotatedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueRotatedLeft);
        LOGGER.debug("rotated left: {} or {} converted", valueRotatedLeft, rotatedAndConverted);
        valueRotatedLeft = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield valueRotatedLeft;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_HEXADECIMAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueRotatedLeft;
    }

    /**
     * The actions to perform when you click ROR.
     * @param actionEvent the click action
     */
    public void performRotateRightButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + ROR); }
        else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + ROR);
        }
        else
        {
            String rotateRightResult = performRotateRight();
            //String convertedResult = BASE_BINARY == calculator.getCalculatorBase() ? calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), rotateRightResult) : rotateRightResult;
            calculator.appendTextToPane(rotateRightResult);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, rotateRightResult);
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, ROR + " performed");
        }
    }
    /**
     * The inner logic for ROR
     * @return the result of the operation
     */
    public String performRotateRight()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before rotate right: {}", positionedValue); // 10000000
        String valueRotatedRight = "";
        String lastToFirst = "";
        for (int bit=0; bit<positionedValue.length(); bit++) {
            if (bit == positionedValue.length()-1) {
                lastToFirst += positionedValue.charAt(bit);
            } else {
                valueRotatedRight += positionedValue.charAt(bit);
            }
        } // 0100 0000
        LOGGER.debug("reversed: {}", lastToFirst +"," + valueRotatedRight);
        valueRotatedRight = lastToFirst + valueRotatedRight;
        String rotatedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueRotatedRight);
        LOGGER.debug("rotated right: {} or {} converted", valueRotatedRight, rotatedAndConverted);
        valueRotatedRight = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield valueRotatedRight;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_HEXADECIMAL -> {
                if (!calculator.isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueRotatedRight;
    }

    /**
     * The actions to perform when Or is clicked
     */
    public void performOrButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        // Ex: 2
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + OR); }
        else if (calculator.getTextPaneValue().isEmpty() || calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + OR + " operation");
        }
        // v[0] is set, then pushes OR
        else if (!calculator.getTextPaneValue().isEmpty() && !calculator.getValues()[0].isBlank() && calculator.getValues()[1].isBlank())
        {
            isOr = true;
            LOGGER.debug("Appending {} to text pane", buttonChoice);
            calculator.setIsFirstNumber(false);
            calculator.appendTextToPane(calculator.getValues()[0] + SPACE.getValue() + buttonChoice); // Ex: 2 OR
            calculator.writeHistory(buttonChoice, true);
            calculator.setValuesPosition(1);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
        // v[0] & v[1] set, OR set, then pushes OR, continued operation
        else //if (!calculator.getValues()[0].isBlank() && !calculator.getValues()[1].isBlank() && isOr)
        {
            String orResult = performOr();
            var resultInBase = BASE_BINARY != calculator.getCalculatorBase() ?
                    calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), orResult) : orResult;
            calculator.writeContinuedHistory(OR.getValue(), OR.getValue(), resultInBase, true);
            calculator.getValues()[0] = resultInBase;
            switch (calculator.getCalculatorBase())
            {
                case BASE_BINARY -> { calculator.appendTextToPane(orResult); }
                case BASE_OCTAL -> { calculator.appendTextToPane(calculator.convertValueToOctal()); }
                case BASE_DECIMAL -> { calculator.appendTextToPane(calculator.getValues()[0]); }
                case BASE_HEXADECIMAL -> { calculator.appendTextToPane(calculator.convertValueToHexadecimal());}
            }
            calculator.resetCalculatorOperations(false);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
    }
    /**
     * The inner logic for Or
     * @return the result of the Or operation in binary
     */
    public String performOr()
    {
        LOGGER.debug("Performing Or");
        String v1InBinary = BASE_BINARY == calculator.getCalculatorBase() ? calculator.getValues()[0] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[0]);
        String v2InBinary = BASE_BINARY == calculator.getCalculatorBase() ? calculator.getValues()[1] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[1]);
        StringBuilder v1AndV2 = new StringBuilder();
        int counter = 0;
        for (char a : v1InBinary.toCharArray()) {
            char b = v2InBinary.charAt(counter);
            if (a == ONE.getValue().charAt(0) || b == ONE.getValue().charAt(0)) {
                v1AndV2.append(ONE.getValue());
            } else {
                v1AndV2.append(ZERO.getValue());
            }
            counter++;
        }
        String orResult = calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), v1AndV2.toString());
        LOGGER.info("{} OR {} = {}", calculator.getValues()[0], calculator.getValues()[1], orResult);
        return v1AndV2.toString();
    }

    /**
     * The actions to perform when Xor is clicked
     */
    public void performXorButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + XOR); }
        else if (calculator.getTextPaneValue().isEmpty() || calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + XOR + " operation");
        }
        // if v[0] is set, v[1] is not, and we have not pushed Xor yet
        else if (!isXor && !calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty())
        {
            isXor = true;
            LOGGER.debug("Appending {} to text pane", buttonChoice);
            calculator.appendTextToPane(calculator.getValues()[0] + SPACE.getValue() + buttonChoice); // Ex: 2 OR
            calculator.writeHistory(buttonChoice, true);
            calculator.resetCalculatorOperations(true);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
        else //if (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            isXor = true;
            String xorResult = performXor();
            var resultInBase = !BASE_BINARY.equals(calculator.getCalculatorBase()) ?
                    calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), xorResult) : xorResult;
            calculator.writeContinuedHistory(OR.getValue(), OR.getValue(), resultInBase, true);
            calculator.getValues()[0] = resultInBase;
            switch (calculator.getCalculatorBase())
            {
                case BASE_BINARY -> { calculator.appendTextToPane(xorResult); }
                case BASE_OCTAL -> { calculator.appendTextToPane(calculator.convertValueToOctal()); }
                case BASE_DECIMAL -> { calculator.appendTextToPane(calculator.getValues()[0]); }
                case BASE_HEXADECIMAL -> { calculator.appendTextToPane(calculator.convertValueToHexadecimal());}
            }
            calculator.resetCalculatorOperations(false);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
    }
    /**
     * The inner logic for Xor
     * @return String the result of the operation
     */
    public String performXor()
    {
        LOGGER.info("Performing {}", XOR);
        LOGGER.info("value[0]: '{}'", calculator.getValues()[0]);
        LOGGER.info("value[1]: '{}'", calculator.getValues()[1]);
        String v1InBinary = calculator.getCalculatorBase() == BASE_BINARY ? calculator.getValues()[0] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[0]);
        String v2InBinary = calculator.getCalculatorBase() == BASE_BINARY ? calculator.getValues()[1] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[1]);
        StringBuilder v1AndV2 = new StringBuilder();
        int counter = 0;
        for (char a : v1InBinary.toCharArray()) {
            char b = v2InBinary.charAt(counter);
            if (a != b &&
                    (a == ONE.getValue().charAt(0) && b == ZERO.getValue().charAt(0) ||
                     b == ONE.getValue().charAt(0) && a == ZERO.getValue().charAt(0))) {
                v1AndV2.append(ONE.getValue());
            } else {
                v1AndV2.append(ZERO.getValue());
            }
            counter++;
        }
        String xorResult = calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), v1AndV2.toString());
        LOGGER.info("{} XOR {} = {}", calculator.getValues()[0], calculator.getValues()[1], xorResult);
        return xorResult;
    }

    /**
     * The actions to perform when the Not button is clicked
     */
    public void performNotButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        String textPaneValue = calculator.getTextPaneValue();
        if (BASE_BINARY != calculator.getCalculatorBase()) {
            textPaneValue = calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, textPaneValue);
        }
        LOGGER.debug("before operation execution: {}", textPaneValue);
        StringBuilder newBuffer = new StringBuilder();
        for (int i = 0; i < calculator.determineRequiredLength(textPaneValue.length()); i++) {
            String s = Character.toString(textPaneValue.charAt(i));
            if (s.equals(ZERO.getValue()))
                { newBuffer.append(ONE.getValue()); LOGGER.debug("appending a {}", ONE.getValue()); }
            else
                { newBuffer.append(ZERO.getValue()); LOGGER.debug("appending a {}", ZERO.getValue()); }
        }
        LOGGER.debug("after operation execution: {}", newBuffer);
        // TODO: Rework. It's a bit sloppy atm
        if (BASE_BINARY != calculator.getCalculatorBase()) {
            newBuffer = new StringBuilder().append(calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), newBuffer.toString()));
            calculator.getValues()[calculator.getValuesPosition()] = newBuffer.toString();
        } else {
            calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, newBuffer.toString());
        }
        calculator.appendTextToPane(newBuffer.toString());

        //calculator.setCalculatorBaseAndUpdatePreviousBase(calculator.getPreviousBase());
        //calculator.setCalculatorBaseAndUpdatePreviousBase(calculator.getPreviousBase());
        LOGGER.info("action {} complete", buttonChoice);
        confirm(calculator, LOGGER, "Pressed " + buttonChoice);
    }

    /**
     * The actions to perform when the And button is clicked
     * @param actionEvent the click action
     */
    public void performAndButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + AND); }
        else if (calculator.getTextPaneValue().isEmpty() || calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + AND + " operation");
        }
        // v[0] is set, then pushes And
        else if (!calculator.getTextPaneValue().isEmpty() && calculator.getValues()[1].isEmpty())
        {
            setAnd(true);
            calculator.appendTextToPane(calculator.getValues()[calculator.getValuesPosition()] + SPACE.getValue() + buttonChoice);
            calculator.writeHistory(buttonChoice, true);
            calculator.setIsFirstNumber(false);
            calculator.setIsNumberNegative(false);
            calculator.setValuesPosition(1);
            confirm(calculator, LOGGER, AND + " performed");
        }
        // v[0] and v[1] are set, isAnd is true, continued operation
        else
        {
            String result = performAnd();
            calculator.setValuesPosition(0);
            calculator.appendTextToPane(result + SPACE.getValue() + buttonChoice);
            // TODO: Should AND replace values[0] and values[1]??
            calculator.resetCalculatorOperations(true);
            calculator.writeContinuedHistory(AND.getValue(), AND.getValue(), Double.parseDouble(result), true);
            setAnd(true);
            calculator.resetCalculatorOperations(true);
        }
    }
    /**
     * The inner logic for And
     * Takes v[0] and ensures it is in binary form
     * Takes v[1] and ensures it is in binary form
     * Ex: 0101 (5) and
     *     0110 (6) =
     *     0100 (4) because 0x0=0, 1x1=1, 0x1=0, 1x0=0
     * return 1 when a=1 and b=1, otherwise 0
     */
    public String performAnd()
    {
        LOGGER.info("value[0]: '{}'", calculator.getValues()[0]);
        LOGGER.info("value[1]: '{}'", calculator.getValues()[1]);
        String v1InBinary = calculator.getCalculatorBase() == BASE_BINARY ? calculator.getValues()[0] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[0]);
        String v2InBinary = calculator.getCalculatorBase() == BASE_BINARY ? calculator.getValues()[1] : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_BINARY, calculator.getValues()[1]);
        String v1AndV2 = BLANK.getValue();
        int counter = 0;
        for (char a : v1InBinary.toCharArray()) {
            char b = v2InBinary.charAt(counter);
            if (a == b && a == ONE.getValue().charAt(0) && b == ONE.getValue().charAt(0)) {
                v1AndV2 += ONE.getValue();
            } else {
                v1AndV2 += ZERO.getValue();
            }
            counter++;
        }
        String andResult = calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), v1AndV2);
        LOGGER.info("{} AND {} = {}", calculator.getValues()[0], calculator.getValues()[1], andResult);
        return andResult;
    }

    /**
     * The actions to perform when the Left Shift button is clicked
     * Unary operation that shifts the bits of a number to the left.
     * Multiplying by 2 has the same effect.
     * @param actionEvent the click action
     */
    public void performLeftShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + LSH); }
        else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + LSH);
        }
        else
        {
            String leftShiftResult = performLeftShift();
            //String convertedResult = BASE_BINARY == calculator.getCalculatorBase() ? calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), leftShiftResult) : leftShiftResult;
            calculator.appendTextToPane(leftShiftResult);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, leftShiftResult);
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, LSH + " performed");
        }
    }
    /**
     * The inner logic for LeftShift
     * Will shift left the current value at the current position
     * by multiplying the number by 2.
     * @return String the result of the operation in the current base
     */
    public String performLeftShift()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before shift left: {}", positionedValue);
        String valueShiftedLeft = "";
        for (int bit=0; bit<positionedValue.length(); bit++) {
            if ((bit+1) < positionedValue.length()) {
                valueShiftedLeft += positionedValue.charAt(bit+1);
            } else {
                valueShiftedLeft += ZERO.getValue();
            }
        }
        String shiftedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueShiftedLeft);
        LOGGER.debug("shifted left: {} or {} converted", valueShiftedLeft, shiftedAndConverted);
        valueShiftedLeft = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!calculator.isMaximumValue(shiftedAndConverted)) {
                    yield valueShiftedLeft;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!calculator.isMaximumValue(shiftedAndConverted)) {
                    yield shiftedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> valueShiftedLeft;
            case BASE_HEXADECIMAL -> {
                if (!calculator.isMaximumValue(shiftedAndConverted)) {
                    yield shiftedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueShiftedLeft;
    }

    /**
     * The actions to perform when the Right Shift button is clicked
     * @param actionEvent the click action
     */
    public void performRightShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, "Cannot perform " + RSH); }
        else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
            confirm(calculator, LOGGER, "Cannot perform " + RSH);
        }
        else
        {
            String rightShiftResult = performRightShift();
            //String convertedResult = BASE_BINARY == calculator.getCalculatorBase() ? calculator.convertFromBaseToBase(BASE_BINARY, calculator.getCalculatorBase(), rightShiftResult) : rightShiftResult;
            calculator.appendTextToPane(rightShiftResult);
            calculator.getValues()[calculator.getValuesPosition()] = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, rightShiftResult);
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, RSH + " performed");
        }
    }
    /**
     * The inner logic for RightShift
     * @return String the result of the operation
     */
    public String performRightShift()
    {
        String positionedValue = calculator.getAppropriateValue();
        if (BASE_DECIMAL != calculator.getCalculatorBase()) positionedValue = calculator.convertValueToDecimal();
        String valueShiftedRight = String.valueOf(Double.parseDouble(positionedValue)/2);
        valueShiftedRight = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!calculator.isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!calculator.isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_OCTAL, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> valueShiftedRight;
            case BASE_HEXADECIMAL -> {
                if (!calculator.isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueShiftedRight;
    }

    /**
     * The actions to perform when the Shift button is clicked
     */
    public void performShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (isShiftPressed) {
            isShiftPressed = false;
            buttonsPanel.remove(buttonRotateLeft);
            buttonsPanel.remove(buttonRotateRight);
            buttonsPanel.remove(buttonBytes);
            buttonsPanel.remove(buttonBases);
            calculator.addComponent(this, constraints, buttonsPanel, buttonShiftLeft, 0, 0);
            calculator.addComponent(this, constraints, buttonsPanel, buttonShiftRight, 0, 1);
            calculator.addComponent(this, constraints, buttonsPanel, buttonOr, 0, 2);
            calculator.addComponent(this, constraints, buttonsPanel, buttonXor, 0, 3);
            calculator.addComponent(this, constraints, buttonsPanel, buttonNot, 0, 4);
            calculator.addComponent(this, constraints, buttonsPanel, buttonAnd, 0, 5);
        } else {
            isShiftPressed = true;
            buttonsPanel.remove(buttonShiftLeft);
            buttonsPanel.remove(buttonShiftRight);
            buttonsPanel.remove(buttonOr);
            buttonsPanel.remove(buttonXor);
            buttonsPanel.remove(buttonNot);
            buttonsPanel.remove(buttonAnd);
            calculator.addComponent(this, constraints, buttonsPanel, buttonRotateLeft, 0, 0);
            calculator.addComponent(this, constraints, buttonsPanel, buttonRotateRight, 0, 1);
            calculator.addComponent(this, constraints, buttonsPanel, buttonBytes, 0, 2, null, 2, 1, 1, 1, 0,0);
            calculator.addComponent(this, constraints, buttonsPanel, buttonBases, 0, 4, null, 2, 1, 1, 1, 0,0);
        }
        buttonsPanel.repaint();
        buttonsPanel.revalidate();
        LOGGER.info("Action for {} completed. isShiftPressed: {}{}", buttonChoice, isShiftPressed, calculator.addNewLines(1));
    }

    /**
     * The actions to perform when the Bytes button is clicked
     */
    public void performByteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        switch(calculator.getCalculatorByte())
        {
            case BYTE_BYTE -> {
                //calculator.setPreviousByte();
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
        confirm(calculator, LOGGER, "Bytes updated");
    }

    /**
     * The actions to perform when the Bases button is clicked
     */
    public void performBaseButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        boolean updateValues = false;
        String converted = BLANK.getValue();
        switch(calculator.getCalculatorBase())
        {
            case BASE_BINARY -> {
                // TODO: Not just is the correct length but also if the length is less than
                converted = calculator.getTextPaneValue();
                updateValues = getAllowedLengthsOfTextPane().contains(converted.length());
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_OCTAL, calculator.getTextPaneValue());
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
                    converted = calculator.convertFromBaseToBase(BASE_OCTAL, BASE_DECIMAL, calculator.getTextPaneValue());
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
                    converted = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, calculator.getTextPaneValue());
                    calculator.setPreviousBase(BASE_DECIMAL);
                    appendTextToProgrammerPane(converted);
                }
            }
            case BASE_HEXADECIMAL -> {
                converted = calculator.convertFromBaseToBase(BASE_HEXADECIMAL, BASE_BINARY, calculator.getTextPaneValue());
                calculator.setCalculatorBase(BASE_BINARY);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    calculator.setPreviousBase(BASE_HEXADECIMAL);
                    appendTextToProgrammerPane(separateBits(converted));
                }
            }
        }
        enableDisableNumberButtonsBasedOnBase();
        //appendToPane(addBaseRepresentation()); // must call to update textPane base value
        calculator.writeHistoryWithMessage(buttonBases.getName(), false, " Updated bases to " + this.calculator.getCalculatorBase().getValue());
        calculator.writeHistoryWithMessage(buttonBases.getName(), false, " Result: " + converted);
        confirm(calculator, LOGGER, "Bases updated to " + calculator.getCalculatorBase());
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
            // TEST: removed 0 from allowed lengths
            allowedLengthMinusNewLines.remove((Object)0);
            String textPaneText = calculator.getTextPaneValue();
            LOGGER.debug("textPaneText: {}", textPaneText);
            if (allowedLengthMinusNewLines.contains(textPaneText.length()))
            { confirm(calculator, LOGGER, "Byte length "+allowedLengthMinusNewLines+" already reached"); }
            else
            {
                appendTextToProgrammerPane(separateBits(textPaneText + buttonChoice));
                calculator.writeHistory(buttonChoice, false);
                textPaneText = calculator.getTextPaneValue();
                if (allowedLengthMinusNewLines.contains(textPaneText.length()))
                {
                    calculator.setPreviousBase(BASE_BINARY); // set previousBase since number is fully formed
                    calculator.getValues()[calculator.getValuesPosition()] = textPaneText;
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.convertValueToDecimal();
                    LOGGER.debug("Byte length {} reached with this input", allowedLengthMinusNewLines);
                }
                confirm(calculator, LOGGER, "Pressed " + buttonChoice);
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
     * The actions to perform when History is clicked
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.debug("Action for {} started", buttonChoice);
        if (HISTORY_OPEN.getValue().equals(calculator.getButtonHistory().getText()))
        {
            calculator.getButtonHistory().setText(HISTORY_CLOSED.getValue());
            programmerPanel.remove(historyPanel);
            calculator.addComponent(this, constraints, programmerPanel, buttonsPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
        else
        {
            calculator.getButtonHistory().setText(HISTORY_OPEN.getValue());
            programmerPanel.remove(buttonsPanel);
            calculator.addComponent(this, constraints, programmerPanel, historyPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    /**************** GETTERS ****************/
    public JTextPane getHistoryTextPane() { return programmerHistoryTextPane; }
    public boolean isOr() { return isOr; }
    public boolean isModulus() { return isModulus; }
    public boolean isXor() { return isXor; }
    public boolean isNot() { return isNot; }
    public boolean isAnd() { return isAnd; }
    public boolean isShiftPressed() { return isShiftPressed; }
    public boolean isInitialized() { return isInitialized; }

    /**************** SETTERS ****************/
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
