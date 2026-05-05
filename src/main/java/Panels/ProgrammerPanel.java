package Panels;

import Calculators.Calculator;
import Utilities.CalculatorUtility;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static Calculators.Calculator.mainFont;
import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Utilities.CalculatorUtility.*;
import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.Texts.*;
import static Utilities.LoggingUtil.*;

/**
 * ProgrammerPanel
 * <p>
 * This class contains components and actions
 * for the ProgrammerPanel of the Calculator.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class ProgrammerPanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class.getSimpleName());
    // Programmer specific buttons
    final protected JButton
            buttonModulus = new JButton(MODULUS),
            buttonLeftParenthesis = new JButton(LEFT_PARENTHESIS), buttonRightParenthesis = new JButton(RIGHT_PARENTHESIS),
            buttonRotateLeft = new JButton(ROL), buttonRotateRight = new JButton(ROR),
            buttonOr = new JButton(OR), buttonXor = new JButton(XOR),
            buttonShiftLeft = new JButton(LSH), buttonShiftRight = new JButton(RSH),
            buttonNot = new JButton(NOT), buttonAnd = new JButton(AND),
            buttonA = new JButton(A), buttonB = new JButton(B),
            buttonC = new JButton(C), buttonD = new JButton(D),
            buttonE = new JButton(E), buttonF = new JButton(F),
            buttonBytes = new JButton(BYTE.toUpperCase()), buttonBases = new JButton(BASE.toUpperCase()),
            buttonShift = new JButton(SHIFT);
    protected Calculator calculator;
    protected GridBagConstraints constraints;
    protected JPanel programmerPanel;
    protected boolean isShiftPressed, isInitialized;
    protected int leftParenthesisClickCount, rightParenthesisClickCount;

    /* CONSTRUCTORS */
    /**
     * A zero argument constructor for creating a ProgrammerPanel
     */
    public ProgrammerPanel()
    {
        setName(VIEW_PROGRAMMER.getView());
        setConstraints(new GridBagConstraints());
        LOGGER.info("Empty Programmer panel created");
    }

    /**
     * The main constructor used to create a ProgrammerPanel
     *
     * @param calculator the calculator to use
     */
    public ProgrammerPanel(Calculator calculator)
    {
        setupProgrammerPanel(calculator);
        LOGGER.info("Programmer panel created");
    }

    /* START OF METHODS */
    /**
     * The main method used to define the ProgrammerPanel
     * and all of its components and their actions
     *
     * @param calculator the calculator
     */
    public void setupProgrammerPanel(Calculator calculator)
    {
        if (!isInitialized) {
            setCalculator(calculator);
            setProgrammerPanel(new JPanel(new GridBagLayout()));
            programmerPanel.setName("ProgrammerPanel");
            setLayout(new GridBagLayout());
            setConstraints(new GridBagConstraints());
            setSize(new Dimension(227, 383)); // sets main size
            setMinimumSize(new Dimension(227, 383)); // sets minimum size
        }
        setupProgrammerPanelComponents();
        addComponentsToPanel();
        setName(VIEW_PROGRAMMER.getView());
        isInitialized = true;
        LOGGER.info("Finished setting up {} panel", VIEW_PROGRAMMER.getView());
    }

    /**
     * Sets up the programmer panel components
     */
    private void setupProgrammerPanelComponents()
    {
        calculator.clearButtonActions();
        calculator.setupTextPane();
        calculator.setupProgrammerPanelButtons();
        this.setupProgrammerPanelButtons();
        setupHelpString();
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
                .formatted(VIEW_PROGRAMMER.getView()));
        calculator.updateShowHelp();
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    public void addComponentsToPanel()
    {
        calculator.addComponent(this, constraints, programmerPanel, calculator.getTextPane(), 0, 0, new Insets(1, 1, 1, 1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryStore(), 0, new Insets(0, 1, 0, 0));
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryRecall(), 1, new Insets(0, 0, 0, 0));
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryClear(), 2, new Insets(0, 0, 0, 0));
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryAddition(), 3, new Insets(0, 0, 0, 0));
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemorySubtraction(), 4, new Insets(0, 0, 0, 0)); // right:1
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonHistory(), 5, new Insets(0, 0, 0, 1));

        calculator.addComponent(this, constraints, programmerPanel, calculator.getMemoryPanel(), 1, 0, new Insets(0, 0, 0, 0), 1, 1, 1.0, 1.0, 0, 0);

        calculator.getButtonsPanel().removeAll();
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonShiftLeft, 0, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonShiftRight, 0, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonOr, 0, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonXor, 0, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonNot, 0, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonAnd, 0, 5);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonShift, 1, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonModulus, 1, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonClearEntry(), 1, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonClear(), 1, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDelete(), 1, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDivide(), 1, 5);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonA, 2, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonB, 2, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton7(), 2, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton8(), 2, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton9(), 2, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonMultiply(), 2, 5);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonC, 3, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonD, 3, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton4(), 3, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton5(), 3, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton6(), 3, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonSubtract(), 3, 5);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonE, 4, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonF, 4, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton1(), 4, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton2(), 4, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton3(), 4, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonAdd(), 4, 5);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonLeftParenthesis, 5, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonRightParenthesis, 5, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonNegate(), 5, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton0(), 5, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDecimal(), 5, 4);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonEquals(), 5, 5);

        calculator.addComponent(this, constraints, programmerPanel, calculator.getButtonsPanel(), 2, 0);

        calculator.addComponent(this, constraints, programmerPanel);
        LOGGER.info("Buttons added to the frame");
    }

    /**
     * The main method to set up the Programmer
     * button panels not in the memory panel
     */
    public void setupProgrammerPanelButtons()
    {
        LOGGER.debug("Configuring Programmer Panel buttons...");
        Stream.of(getAllProgrammerOperatorButtons(), getAllHexadecimalButtons())
                .flatMap(Collection::stream)
                .toList().forEach(button -> {
                    button.setFont(mainFont);
                    button.setPreferredSize(new Dimension(35, 35));
                    button.setBorder(new LineBorder(Color.BLACK));
                    button.setEnabled(true);
                });
        buttonModulus.setName(MODULUS);
        buttonModulus.addActionListener(this::performModulusButtonAction);
        LOGGER.debug("Modulus button configured");

        buttonLeftParenthesis.setName(LEFT_PARENTHESIS);
        buttonLeftParenthesis.addActionListener(this::performLeftParenthesisButtonAction);
        LOGGER.warn("Right Parenthesis button needs to be configured");

        buttonRightParenthesis.setName(RIGHT_PARENTHESIS);
        buttonRightParenthesis.addActionListener(this::performRightParenthesisButtonAction);
        LOGGER.warn("Right Parenthesis button needs to be configured");

        buttonRotateLeft.setName(ROL);
        buttonRotateLeft.addActionListener(this::performRotateLeftButtonAction);
        LOGGER.debug("RoL button configured");

        buttonRotateRight.setName(ROR);
        buttonRotateRight.addActionListener(this::performRotateRightButtonAction);
        LOGGER.debug("RoR button configured");

        buttonOr.setName(OR);
        buttonOr.addActionListener(this::performOrButtonAction);
        LOGGER.debug("Or button configured");

        buttonXor.setName(XOR);
        buttonXor.addActionListener(this::performXorButtonAction);
        LOGGER.debug("Xor button configured");

        buttonShiftLeft.setName(LSH);
        buttonShiftLeft.addActionListener(this::performLeftShiftButtonAction);
        LOGGER.debug("Left Shift button configured");

        buttonShiftRight.setName(RSH);
        buttonShiftRight.addActionListener(this::performRightShiftButtonAction);
        LOGGER.debug("Right Shift button configured");

        buttonNot.setName(NOT);
        buttonNot.addActionListener(this::performNotButtonAction);
        LOGGER.debug("Not button configured");

        buttonAnd.setName(AND);
        buttonAnd.addActionListener(this::performAndButtonAction);
        LOGGER.debug("And button configured");

        enableDisableNumberButtonsBasedOnBase();
        buttonShift.setName(SHIFT);
        buttonShift.addActionListener(this::performShiftButtonAction);
        LOGGER.debug("Shift button configured");

        buttonBytes.setName(BYTE + LOWER_CASE_S);
        buttonBytes.setEnabled(false);
        buttonBytes.setPreferredSize(new Dimension(70, 35));
        buttonBytes.addActionListener(this::performByteButtonAction);
        LOGGER.debug("Bytes button configured");

        buttonBases.setName(BASE + LOWER_CASE_S);
        buttonBases.setPreferredSize(new Dimension(70, 35));
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
     *
     * @param isEnabled boolean to set the number buttons to
     */
    public void setButtons2To9(boolean isEnabled)
    {
        calculator.getNumberButtons()
                .forEach(button -> {
                    if (!ZERO.equals(button.getName()) || !ZERO.equals(button.getName()))
                        button.setEnabled(isEnabled);
                });
    }

    /**
     * Method used to enable/disable the hexadecimal number buttons A-F
     *
     * @param isEnabled boolean to set the hexadecimal numbers to
     */
    public void setButtonsAToF(boolean isEnabled)
    {
        getAllHexadecimalButtons().forEach(button -> button.setEnabled(isEnabled));
    }

    /**
     * Returns the programmer specific calculator buttons. This
     * includes Modulus, LeftParenthesis, RightParenthesis,
     * RotateLeft, RotateRight, Or, Xor, LeftShift,
     * RightShift, Not, And, Shift, Bytes, and Bases.
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
    {
        return Arrays.asList(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF);
    }

    /**
     * Returns the value in its alternative base form
     *
     * @return String the base representation of the current value
     */
    public String addBaseRepresentation()
    {
        return switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> separateBits(calculator.convertValueToBinary());
            case BASE_OCTAL ->
                    calculator.convertValueToOctal(); // TODO: add similar separateBits method for octal, grouping by 3s
            case BASE_DECIMAL ->
                    addThousandsDelimiter(calculator.getValues()[calculator.getValuesPosition()], calculator.getThousandsDelimiter());
            case BASE_HEXADECIMAL ->
                    calculator.getValues()[calculator.getValuesPosition()]; // TODO: may need to add separateBits for hexa numbers
        };
    }

    /**
     * Returns the byte and base value
     *
     * @return String the byte and base
     * <p>
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
     *
     * @param representation the String to alter
     * @return String the altered representation with spaces
     */
    public String separateBits(String representation)
    {
        if (representation.isEmpty()) return representation;
        else if (representation.length() < 4) return representation;
        StringBuilder sb = new StringBuilder();
        representation = representation.replace(SPACE, EMPTY);
        boolean operatorSet = calculator.isOperatorActive();
        if (operatorSet) representation = getValueWithoutAnyOperator(representation);
        String respaced = switch (calculator.getCalculatorByte()) {
            case BYTE_BYTE -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE);
                sb.append(representation, 4, representation.length());
                yield sb.toString();
            }
            case BYTE_WORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE);
                sb.append(representation, 4, 8);
                sb.append(SPACE);
                sb.append(representation, 8, 12);
                sb.append(SPACE);
                sb.append(representation, 12, representation.length());
                yield sb.toString();
            }
            case BYTE_DWORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE);
                sb.append(representation, 4, 8);
                sb.append(SPACE);
                sb.append(representation, 8, 12);
                sb.append(SPACE);
                sb.append(representation, 12, 16);
                sb.append(NEWLINE);
                sb.append(representation, 16, 20);
                sb.append(SPACE);
                sb.append(representation, 20, 24);
                sb.append(SPACE);
                sb.append(representation, 24, 28);
                sb.append(SPACE);
                sb.append(representation, 28, representation.length());
                yield sb.toString();
            }
            case BYTE_QWORD -> {
                sb.append(representation, 0, 4);
                sb.append(SPACE);
                sb.append(representation, 4, 8);
                sb.append(SPACE);
                sb.append(representation, 8, 12);
                sb.append(SPACE);
                sb.append(representation, 12, 16);
                sb.append(NEWLINE);
                sb.append(representation, 16, 20);
                sb.append(SPACE);
                sb.append(representation, 20, 24);
                sb.append(SPACE);
                sb.append(representation, 24, 28);
                sb.append(SPACE);
                sb.append(representation, 28, 32);
                sb.append(NEWLINE);
                sb.append(representation, 32, 36);
                sb.append(SPACE);
                sb.append(representation, 36, 40);
                sb.append(SPACE);
                sb.append(representation, 40, 44);
                sb.append(SPACE);
                sb.append(representation, 44, 48);
                sb.append(NEWLINE);
                sb.append(representation, 48, 52);
                sb.append(SPACE);
                sb.append(representation, 52, 56);
                sb.append(SPACE);
                sb.append(representation, 56, 60);
                sb.append(SPACE);
                sb.append(representation, 60, representation.length());
                yield sb.toString();
            }
        };
        if (operatorSet && calculator.getValuesPosition() == 0)
            respaced = respaced + SPACE + calculator.getActiveOperator();
        return respaced;
    }

    /**
     * Add text to the text pane when using the programmer panel
     *
     * @param text the text to add
     */
    public void appendTextForProgrammerPanel(String text)
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
            doc.insertString(doc.getLength(), NEWLINE + text + NEWLINE, doc.getStyle("alignRight"));
            doc.setParagraphAttributes(doc.getLength() - text.length(), text.length(), attribs, false);
        }
        catch (BadLocationException e) {
            logException(e, LOGGER);
        }
    }

    /**
     * Returns the correct lengths allowed when using base binary
     * and different byte values. This uses only the value in the
     * text pane.
     *
     * @return List the lengths allowed for different bytes
     */
    public List<Integer> getAllowedLengthsOfTextPane()
    {
        List<Integer> lengthsAllowed = new ArrayList<>();
        lengthsAllowed.add(0);  // what if no input??
        switch (calculator.getCalculatorByte()) {
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

    /* BUTTON ACTIONS */
    /**
     * The actions to perform when the Left Shift button is clicked
     * Unary operation that shifts the bits of a number to the left.
     * Multiplying by 2 has the same effect.
     *
     * @param actionEvent the click action
     */
    public void performLeftShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, "Cannot perform " + LSH);
        } else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + LSH);
        } else {
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
     *
     * @return String the result of the operation in the current base
     */
    public String performLeftShift()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before shift left: {}", positionedValue);
        String valueShiftedLeft = EMPTY;
        for (int bit = 0; bit < positionedValue.length(); bit++)
        {
            if ((bit + 1) < positionedValue.length()) {
                valueShiftedLeft += positionedValue.charAt(bit + 1);
            }
            else {
                valueShiftedLeft += ZERO;
            }
        }
        String shiftedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueShiftedLeft);
        LOGGER.debug("shifted left: {} or {} converted", valueShiftedLeft, shiftedAndConverted);
        valueShiftedLeft = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY,
                 BASE_OCTAL,
                 BASE_HEXADECIMAL -> {
                if (!isMaximumValue(shiftedAndConverted)) {
                    yield shiftedAndConverted;
                }
                else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> valueShiftedLeft;
        };
        return valueShiftedLeft;
    }

    /**
     * The actions to perform when the Right Shift button is clicked
     *
     * @param actionEvent the click action
     */
    public void performRightShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, "Cannot perform " + RSH);
        }
        else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + RSH);
        }
        else {
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
     *
     * @return String the result of the operation
     */
    public String performRightShift()
    {
        String positionedValue = calculator.getValues()[calculator.getValuesPosition()]; //calculator.getAppropriateValue();
        if (BASE_DECIMAL != calculator.getCalculatorBase()) positionedValue = calculator.convertValueToDecimal();
        String valueShiftedRight = String.valueOf(Double.parseDouble(positionedValue) / 2);
        valueShiftedRight = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_OCTAL, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> valueShiftedRight;
            case BASE_HEXADECIMAL -> {
                if (!isMaximumValue(valueShiftedRight)) {
                    yield calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, valueShiftedRight);
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueShiftedRight;
    }

    /**
     * The actions to perform when Or is clicked
     */
    public void performOrButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = calculator.getTextPaneValue();
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, cannotPerformOperation(OR));
        }
        else if (calculator.getTextPaneValue().isEmpty()) {
            logEmptyValue(buttonChoice, calculator, LOGGER);
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(OR));
        }
        else if (calculator.textPaneContainsLeftOrRightParentheses() &&
                !parenthesisClickCountIsEqual())
        {
            textPaneValue = textPaneValue.substring(0, textPaneValue.length()-1);
            calculator.appendTextToPane(textPaneValue + buttonChoice + RIGHT_PARENTHESIS);
        }
        else if (calculator.textPaneContainsEqualLeftAndRightParentheses())
        {
            calculator.appendTextToPane(textPaneValue + SPACE + buttonChoice);
        }
        else if (isMaximumValue(calculator.getValueAt())) {
            confirm(calculator, LOGGER, PRESSED + SPACE + buttonChoice + ". Maximum number met");
        }
        else if (isMinimumValue(calculator.getValueAt())) {
            confirm(calculator, LOGGER, PRESSED + SPACE + buttonChoice + ". Minimum number met");
        }
        else {
            // v[0] is set, then pushes OR
            if (!calculator.getTextPaneValue().isEmpty() && !calculator.getValues()[0].isBlank() && calculator.getValues()[1].isBlank()) {
                calculator.getValues()[2] = OR;
                LOGGER.debug("Appending {} to text pane", buttonChoice);
                calculator.setObtainingFirstNumber(false);
                calculator.appendTextToPane(calculator.getValues()[0] + SPACE + buttonChoice); // Ex: 2 OR
                calculator.writeHistory(buttonChoice, true);
                calculator.setValuesPosition(1);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            // v[0] & v[1] set, OR set, then pushes OR, continued operation
            else if (calculator.isOperatorActive() && !calculator.getValueAt(1).isEmpty()) {
                String orResultInDecimal = performOr();
                String resultInBase = calculator.getCalculatorBase() == BASE_DECIMAL
                        ? orResultInDecimal
                        : calculator.convertFromBaseToBase(BASE_DECIMAL, calculator.getCalculatorBase(), orResultInDecimal);
                calculator.writeContinuedHistory(OR, OR, resultInBase, true);

                calculator.getValues()[0] = resultInBase;
                calculator.appendTextToPane(addThousandsDelimiter(resultInBase, calculator.getThousandsDelimiter()), true);
                calculator.finishedObtainingFirstNumber(false);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (!calculator.getTextPaneValue().isBlank() && calculator.getValueAt(0).isBlank()) {
                logEmptyValue(buttonChoice, calculator, LOGGER);
                LOGGER.info("Setting values[0] to textPane value");
                calculator.appendTextToPane(calculator.getTextPaneValue() + SPACE + buttonChoice, true);
                calculator.writeHistory(buttonChoice, true);
                calculator.getValues()[2] = buttonChoice;
                calculator.setObtainingFirstNumber(false);
                calculator.setValuesPosition(1);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (calculator.isOperatorActive()) {
                confirm(calculator, LOGGER, cannotPerformOperation(ADDITION));
            }
        }
    }

    /**
     * The inner logic for OR.
     * Uses signed two's-complement semantics (e.g. -145 OR 8 = -145).
     *
     * @return the result of the OR operation in decimal form
     */
    public String performOr()
    {
        LOGGER.debug("Performing Or");
        String firstValue = calculator.getValueAt(0);
        String secondValue = calculator.getValueAt(1);
        String v1InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? firstValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, firstValue);
        String v2InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? secondValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, secondValue);

        // Bitwise OR operates on integers. Fractional decimals are truncated toward zero.
        BigInteger v1 = new BigDecimal(v1InDecimal).toBigInteger();
        BigInteger v2 = new BigDecimal(v2InDecimal).toBigInteger();
        String orResult = v1.or(v2).toString();

        LOGGER.debug("OR result: {} ({}) OR {} ({}) = {}", firstValue, v1, secondValue, v2, orResult);
        return orResult;
    }

    /**
     * The actions to perform when Xor is clicked
     */
    public void performXorButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = calculator.getTextPaneValue();
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, "Cannot perform " + XOR);
        }
        else if (textPaneValue.isEmpty() || calculator.getValues()[0].isEmpty())
        {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + XOR + " operation");
        }
        else if (calculator.textPaneContainsLeftOrRightParentheses() &&
                !parenthesisClickCountIsEqual())
        {
            textPaneValue = textPaneValue.substring(0, textPaneValue.length()-1);
            calculator.appendTextToPane(textPaneValue + buttonChoice + RIGHT_PARENTHESIS);
        }
        else if (calculator.textPaneContainsEqualLeftAndRightParentheses())
        {
            calculator.appendTextToPane(textPaneValue + SPACE + buttonChoice);
        }
        // if v[0] is set, v[1] is not, and we have not pushed Xor yet
        else if (!calculator.getValues()[2].equals(XOR) && !calculator.getValues()[0].isEmpty() && calculator.getValues()[1].isEmpty()) {
            calculator.setActiveOperator(XOR);
            LOGGER.debug("Appending {} to text pane", buttonChoice);
            calculator.appendTextToPane(calculator.getValues()[0] + SPACE + buttonChoice); // Ex: 2 OR
            calculator.writeHistory(buttonChoice, true);
            calculator.finishedObtainingFirstNumber(true);
            confirm(calculator, LOGGER, pressedButton(buttonChoice));
        } else //if (!calculator.getValues()[0].isEmpty() && !calculator.getValues()[1].isEmpty())
        {
            calculator.getValues()[2] = XOR;
            String xorResultInDecimal = performXor();
            String resultInBase = calculator.getCalculatorBase() == BASE_DECIMAL
                    ? xorResultInDecimal
                    : calculator.convertFromBaseToBase(BASE_DECIMAL, calculator.getCalculatorBase(), xorResultInDecimal);
            calculator.writeContinuedHistory(XOR, XOR, resultInBase, true);

            calculator.getValues()[0] = resultInBase;
            calculator.appendTextToPane(addThousandsDelimiter(resultInBase, calculator.getThousandsDelimiter()), true);
            calculator.finishedObtainingFirstNumber(false);
            confirm(calculator, LOGGER, pressedButton(buttonChoice));
        }
    }

    /**
     * The inner logic for XOR.
     * Uses signed two's-complement semantics via decimal canonical values.
     *
     * @return the result of the XOR operation in decimal form
     */
    public String performXor()
    {
        String firstValue = calculator.getValueAt(0);
        String secondValue = calculator.getValueAt(1);
        String v1InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? firstValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, firstValue);
        String v2InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? secondValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, secondValue);

        BigInteger v1 = new BigInteger(v1InDecimal);
        BigInteger v2 = new BigInteger(v2InDecimal);
        String xorResult = v1.xor(v2).toString();

        LOGGER.debug("XOR result: {} ({}) XOR {} ({}) = {}", firstValue, v1InDecimal, secondValue, v2InDecimal, xorResult);
        return xorResult;
    }

    /**
     * The actions to perform when the Not button is clicked
     */
    public void performNotButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String result = performNot(); // returns decimal form
        String valueToStore = result;
        if (calculator.getCalculatorBase() != BASE_DECIMAL) { // show result in proper base
            String showInBase = calculator.convertFromBaseToBase(BASE_DECIMAL, calculator.getCalculatorBase(), result);
            calculator.appendTextToPane(showInBase, false);
            valueToStore = showInBase;
        } else {
            calculator.appendTextToPane(result, true);
        }
        calculator.getValues()[calculator.getValuesPosition()] = valueToStore;
        calculator.writeHistory(buttonChoice, false);
        confirm(calculator, LOGGER, pressedButton(buttonChoice));
    }

    /**
     * Performs the actual logic of inverting a number's bits for the Not operation.
     * not x = -(x + 1)
     * @return the number inverted
     */
    public String performNot()
    {
        BigDecimal valueAtPosition = new BigDecimal(calculator.getAppropriateValue());
        LOGGER.debug("before operation execution: {}", valueAtPosition);
        valueAtPosition = valueAtPosition.add(BigDecimal.ONE);
        valueAtPosition = valueAtPosition.multiply(new BigDecimal(SUBTRACTION+ONE));
        LOGGER.debug("after operation execution: {}", valueAtPosition);
        calculator.setNegativeNumber(CalculatorUtility.isNegativeNumber(valueAtPosition.toString()));
        return valueAtPosition.toString();
    }

    /**
     * The actions to perform when the And button is clicked
     *
     * @param actionEvent the click action
     */
    public void performAndButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = calculator.getTextPaneValue();
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, cannotPerformOperation(AND));
        }
        else if (calculator.getValueAt().isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(AND));
        }
        else if (calculator.textPaneContainsLeftOrRightParentheses() &&
                !parenthesisClickCountIsEqual())
        {
            textPaneValue = textPaneValue.substring(0, textPaneValue.length()-1);
            calculator.appendTextToPane(textPaneValue + buttonChoice + RIGHT_PARENTHESIS);
        }
        else if (calculator.textPaneContainsEqualLeftAndRightParentheses())
        {
            calculator.appendTextToPane(textPaneValue + SPACE + buttonChoice);
        }
        else {
            if (calculator.isNoOperatorActive() && !calculator.getValueAt(0).isEmpty()) {
                calculator.getValues()[2] = AND;
                calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(), calculator.getThousandsDelimiter()) + SPACE + buttonChoice);
                calculator.writeHistory(buttonChoice, true);
                calculator.setObtainingFirstNumber(false);
                calculator.setNegativeNumber(false);
                calculator.setValuesPosition(1);
                confirm(calculator, LOGGER, performedOperation(AND));
            }
            else if (calculator.isOperatorActive() && !calculator.getValueAt(1).isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 AND ...
                calculator.performOperation();
                calculator.setActiveOperator(buttonChoice);
                calculator.writeContinuedHistory(AND, calculator.getActiveOperator(), calculator.getValueAt(3), true);

                if (isMaximumValue(calculator.getValueAt(3)) || isMinimumValue(calculator.getValueAt(3))) {
                    calculator.getValues()[2] = EMPTY;
                    calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()));
                    calculator.finishedObtainingFirstNumber(false);
                } else {
                    calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    calculator.finishedObtainingFirstNumber(true);
                }
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (!calculator.getTextPaneValue().isBlank() && calculator.getValueAt(0).isBlank()) {
                logEmptyValue(buttonChoice, calculator, LOGGER);
                LOGGER.info("Setting values[0] to textPane value");
                calculator.appendTextToPane(calculator.getTextPaneValue() + SPACE + buttonChoice, true);
                calculator.writeHistory(buttonChoice, true);
                calculator.getValues()[2] = buttonChoice;
                calculator.setObtainingFirstNumber(false);
                calculator.setValuesPosition(1);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (calculator.isOperatorActive()) {
                confirm(calculator, LOGGER, cannotPerformOperation(AND));
            }
        }
    }

    /**
     * The inner logic for And.
     * Uses signed two's-complement semantics via decimal canonical values.
     *
     * @return the result of the AND operation in decimal form
     */
    public String performAnd()
    {
        logValues(calculator, LOGGER, 2);
        String firstValue = calculator.getValueAt(0);
        String secondValue = calculator.getValueAt(1);
        String v1InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? firstValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, firstValue);
        String v2InDecimal = calculator.getCalculatorBase() == BASE_DECIMAL
                ? secondValue
                : calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, secondValue);

        BigInteger v1 = new BigInteger(v1InDecimal);
        BigInteger v2 = new BigInteger(v2InDecimal);
        String andResult = v1.and(v2).toString();

        LOGGER.debug("AND result: {} ({}) AND {} ({}) = {}", firstValue, v1InDecimal, secondValue, v2InDecimal, andResult);
        return andResult;
    }

    /**
     * The actions to perform when you click ROL.
     *
     * @param actionEvent the click action
     */
    public void performRotateLeftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, "Cannot perform " + ROL);
        } else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + ROL);
        } else {
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
     *
     * @return the result of the operation
     */
    public String performRotateLeft()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before rotate left: {}", positionedValue);
        String valueRotatedLeft = EMPTY;
        for (int bit = 0; bit < positionedValue.length(); bit++) {
            if ((bit + 1) < positionedValue.length()) {
                valueRotatedLeft += positionedValue.charAt(bit + 1);
            } else {
                valueRotatedLeft += positionedValue.charAt(0);
            }
        }
        String rotatedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueRotatedLeft);
        LOGGER.debug("rotated left: {} or {} converted", valueRotatedLeft, rotatedAndConverted);
        valueRotatedLeft = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield valueRotatedLeft;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_HEXADECIMAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
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
     *
     * @param actionEvent the click action
     */
    public void performRotateRightButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, "Cannot perform " + ROR);
        } else if (calculator.getTextPaneValue().isEmpty() && calculator.getValues()[0].isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + ROR);
        } else {
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
     *
     * @return the result of the operation
     */
    public String performRotateRight()
    {
        String positionedValue = calculator.convertValueToBinary();
        LOGGER.debug("before rotate right: {}", positionedValue); // 10000000
        String valueRotatedRight = EMPTY;
        String lastToFirst = EMPTY;
        for (int bit = 0; bit < positionedValue.length(); bit++)
        {
            if (bit == positionedValue.length() - 1) {
                lastToFirst += positionedValue.charAt(bit);
            }
            else {
                valueRotatedRight += positionedValue.charAt(bit);
            }
        } // 0100 0000
        LOGGER.debug("reversed: {}", lastToFirst + "," + valueRotatedRight);
        valueRotatedRight = lastToFirst + valueRotatedRight;
        String rotatedAndConverted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, valueRotatedRight);
        LOGGER.debug("rotated right: {} or {} converted", valueRotatedRight, rotatedAndConverted);
        valueRotatedRight = switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield valueRotatedRight;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_OCTAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_DECIMAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
            case BASE_HEXADECIMAL -> {
                if (!isMaximumValue(rotatedAndConverted)) {
                    yield rotatedAndConverted;
                } else {
                    yield positionedValue;
                }
            }
        };
        return valueRotatedRight;
    }

    /**
     * The actions to perform when the Bytes button is clicked
     */
    public void performByteButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        switch (calculator.getCalculatorByte()) {
            case BYTE_BYTE ->
                calculator.setCalculatorByte(BYTE_WORD);
            case BYTE_WORD ->
                calculator.setCalculatorByte(BYTE_DWORD);

            case BYTE_DWORD ->
                calculator.setCalculatorByte(BYTE_QWORD);

            case BYTE_QWORD ->
                calculator.setCalculatorByte(BYTE_BYTE);
        }
        calculator.writeHistoryWithMessage(buttonBytes.getName(), false, "Updated bytes to " + calculator.getCalculatorByte().getValue());
        appendTextForProgrammerPanel(addBaseRepresentation());
        confirm(calculator, LOGGER, "Bytes updated");
    }

    /*
     * TODO: Fix: This logic is not correct. What should happen is the following:
     * Grab the current value at values[valuesPosition].
     * Convert that value from decimal to the base we chose
     * Display that value in the textpane but do not update the value in values[valuesPosition]
     */
    /**
     * The actions to perform when the Bases button is clicked
     */
    public void performBaseButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String currentValue = calculator.getAppropriateValue();
        String converted = EMPTY;
        buttonBytes.setEnabled(false);
        switch (calculator.getCalculatorBase()) {
            case BASE_BINARY -> {
                calculator.setPreviousBase(BASE_BINARY);
                calculator.setCalculatorBase(BASE_OCTAL);
                converted = calculator.convertValueToOctal(currentValue).toUpperCase();
            }
            case BASE_OCTAL -> {
                calculator.setPreviousBase(BASE_OCTAL);
                calculator.setCalculatorBase(BASE_DECIMAL);
                converted = calculator.convertValueToDecimal(currentValue).toUpperCase();
            }
            case BASE_DECIMAL -> {
                calculator.setPreviousBase(BASE_DECIMAL);
                calculator.setCalculatorBase(BASE_HEXADECIMAL);
                converted = calculator.convertValueToHexadecimal(currentValue).toUpperCase();
            }
            case BASE_HEXADECIMAL -> {
                calculator.setPreviousBase(BASE_HEXADECIMAL);
                calculator.setCalculatorBase(BASE_BINARY);
                buttonBytes.setEnabled(true);
                converted = calculator.convertValueToBinary(currentValue).toUpperCase();
            }
        }
        enableDisableNumberButtonsBasedOnBase();
        calculator.writeHistoryWithMessage(buttonBases.getName(), false, " Updated bases to " + calculator.getCalculatorBase().getValue());
        appendTextForProgrammerPanel(converted);
        confirm(calculator, LOGGER, "Bases updated to " + calculator.getCalculatorBase());
    }

    /**
     * The actions to perform when the Shift button is clicked
     */
    public void performShiftButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        LOGGER.info("isShiftPressed: {}", isShiftPressed);
        if (isShiftPressed) {
            isShiftPressed = false;
            calculator.getButtonsPanel().remove(buttonRotateLeft);
            calculator.getButtonsPanel().remove(buttonRotateRight);
            calculator.getButtonsPanel().remove(buttonBytes);
            calculator.getButtonsPanel().remove(buttonBases);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonShiftLeft, 0, 0);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonShiftRight, 0, 1);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonOr, 0, 2);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonXor, 0, 3);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonNot, 0, 4);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonAnd, 0, 5);
        } else {
            isShiftPressed = true;
            calculator.getButtonsPanel().remove(buttonShiftLeft);
            calculator.getButtonsPanel().remove(buttonShiftRight);
            calculator.getButtonsPanel().remove(buttonOr);
            calculator.getButtonsPanel().remove(buttonXor);
            calculator.getButtonsPanel().remove(buttonNot);
            calculator.getButtonsPanel().remove(buttonAnd);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonRotateLeft, 0, 0);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonRotateRight, 0, 1);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonBytes, 0, 2, null, 2, 1, 1, 1, 0, 0);
            calculator.addComponent(this, constraints, calculator.getButtonsPanel(), buttonBases, 0, 4, null, 2, 1, 1, 1, 0, 0);
        }
        calculator.updateLookAndFeel();
    }

    /**
     * The actions to perform when you click Modulus.
     * Modulus returns the remainder of a division operation.
     *
     * @param actionEvent the click action
     */
    public void performModulusButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = calculator.getTextPaneValue();
        if (calculator.textPaneContainsBadText()) {
            confirm(calculator, LOGGER, cannotPerformOperation(buttonChoice));
        }
        else if (textPaneValue.isEmpty()) {
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(ADDITION));
        }
        else {
            if (calculator.isNoOperatorActive() && !calculator.getValueAt(0).isEmpty()) {
                calculator.getValues()[2] = buttonChoice;
                appendTextForProgrammerPanel(calculator.getTextPaneValue() + SPACE + buttonChoice);
                calculator.setNegativeNumber(false);
                calculator.getButtonDecimal().setEnabled(true);
                calculator.finishedObtainingFirstNumber(true);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (calculator.isOperatorActive() && !calculator.getValueAt(1).isEmpty()) {
                // Chained operation: 5 <ANY_BINARY_OPERATOR> 3 AND ...
                calculator.performOperation();
                calculator.setActiveOperator(buttonChoice);
                calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()), true);
                calculator.writeContinuedHistory(MODULUS, calculator.getActiveOperator(), calculator.getValueAt(3), true);
                if (isMaximumValue(calculator.getValueAt(3)) || isMinimumValue(calculator.getValueAt(3))) {
                    calculator.getValues()[2] = EMPTY;
                    calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()));
                    //calculator.resetCalculatorOperations(false);
                } else {
                    calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()) + SPACE + buttonChoice, true);
                    //calculator.resetCalculatorOperations(true);
                }
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (!textPaneValue.isBlank() && calculator.getValueAt(0).isBlank()) {
                logEmptyValue(buttonChoice, calculator, LOGGER);
                calculator.writeHistory(buttonChoice, true);
                calculator.setActiveOperator(buttonChoice);
                calculator.appendTextToPane(calculator.getTextPaneValue() + SPACE + buttonChoice, true);
                calculator.setObtainingFirstNumber(false);
                calculator.setValuesPosition(1);
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            else if (calculator.isOperatorActive()) {
                confirm(calculator, LOGGER, cannotPerformOperation(AND));
            }
        }
    }

    /**
     * The inner logic for modulus
     */
    public String performModulus()
    {
        String result;
        if (ZERO.equals(calculator.getValueAt(1))) {
            //calculator.dividedByZero();
            calculator.setObtainingFirstNumber(true);
            result = INFINITY;
        } else {
            BigDecimal a = new BigDecimal(calculator.getValueAt(0));
            BigDecimal b = new BigDecimal(calculator.getValueAt(1));
            // Euclidean modulo: always returns non-negative result
            BigDecimal remainder = a.remainder(b);
            if (remainder.compareTo(BigDecimal.ZERO) < 0) {
                // If remainder is negative, adjust based on sign of b
                if (b.compareTo(BigDecimal.ZERO) > 0) {
                    remainder = remainder.add(b);
                } else {
                    remainder = remainder.subtract(b);
                }
            }
            result = remainder.toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when you click (.
     *
     * @param actionEvent the click action
     */
    public void performLeftParenthesisButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String updatedExpression = performLeftParenthesis();
        appendTextForProgrammerPanel(updatedExpression);
        calculator.writeHistory(buttonChoice, false);
        calculator.setPemdasIsActive(true);
        confirm(calculator, LOGGER, pressedButton(buttonChoice));
    }

    /**
     * The inner logic for (
     * Inserts a matching right parenthesis and places caret between the pair.
     *
     * @return the updated expression
     */
    public String performLeftParenthesis()
    {
        leftParenthesisClickCount++;
        String textPaneValue = calculator.getTextPaneValue();
        if (ZERO.equals(textPaneValue)) {
            LOGGER.debug("textPane equals 0. Setting to blank.");
            calculator.appendTextToPane(EMPTY);
        }
        int rightParenthesisInValue = 0;
        for(char c : new StringBuffer(textPaneValue).reverse().toString().toCharArray())
        {
            if (c == RIGHT_PARENTHESIS.toCharArray()[0]) rightParenthesisInValue++;
            else break;
        }
        String updatedExpression = !calculator.isPemdasActive()
                ? textPaneValue + LEFT_PARENTHESIS + RIGHT_PARENTHESIS
                : textPaneValue.substring(0, textPaneValue.length()-rightParenthesisInValue)
                  + LEFT_PARENTHESIS + RIGHT_PARENTHESIS + RIGHT_PARENTHESIS.repeat(rightParenthesisInValue);
        // the extra right parenthesis is because we "took x away" with the length-1 logic
        LOGGER.debug("Left ( updated expression: '{}'", updatedExpression);
        return updatedExpression;
    }

    /**
     * The actions to perform when you click ).
     *
     * @param actionEvent the click action
     */
    public void performRightParenthesisButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        String textPaneValue = calculator.getTextPaneValue();
        if (!textPaneValue.contains(LEFT_PARENTHESIS)) {
            confirm(calculator, LOGGER, cannotPerformOperation(buttonChoice) + ". No " + LEFT_PARENTHESIS + " exists yet");
            return;
        }
        performRightParenthesis(textPaneValue, buttonChoice);
        confirm(calculator, LOGGER, pressedButton(buttonChoice));
    }

    /**
     * The inner logic for )
     * Advances over an existing right parenthesis, or inserts one at the caret.
     * @param textPaneValue the text pane value
     * @param buttonChoice the button choice
     */
    public void performRightParenthesis(String textPaneValue, String buttonChoice)
    {
        rightParenthesisClickCount++;
        int rightParenthesisInValue = 0;
        for(char c : textPaneValue.toCharArray())
        {
            if (c == RIGHT_PARENTHESIS.toCharArray()[0]) rightParenthesisInValue++;
        }
        calculator.writeHistory(buttonChoice, false);
        calculator.setPemdasIsActive(rightParenthesisClickCount != rightParenthesisInValue);
    }

    /* GETTERS */
    public Calculator getCalculator()
    {
        return calculator;
    }

    /* SETTERS */
    public void setCalculator(Calculator calculator)
    {
        this.calculator = calculator;
        LOGGER.debug("Calculator set");
    }

    public GridBagConstraints getConstraints()
    {
        return constraints;
    }

    public void setConstraints(GridBagConstraints constraints)
    {
        this.constraints = constraints;
        LOGGER.debug("Constraints set");
    }

    public JPanel getProgrammerPanel()
    {
        return programmerPanel;
    }

    public void setProgrammerPanel(JPanel programmerPanel)
    {
        this.programmerPanel = programmerPanel;
        LOGGER.debug("Programmer panel set");
    }

    public boolean isShiftPressed()
    {
        return isShiftPressed;
    }

    public void setShiftPressed(boolean shiftPressed)
    {
        isShiftPressed = shiftPressed;
        LOGGER.debug("ShiftPressed set: {}", isShiftPressed ? YES.toLowerCase() : NO.toLowerCase());
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public JButton getButtonA()
    {
        return buttonA;
    }

    public JButton getButtonB()
    {
        return buttonB;
    }

    public JButton getButtonC()
    {
        return buttonC;
    }

    public JButton getButtonD()
    {
        return buttonD;
    }

    public JButton getButtonE()
    {
        return buttonE;
    }

    public JButton getButtonF()
    {
        return buttonF;
    }

    public int getLeftParenthesisClickCount()
    {
        return leftParenthesisClickCount;
    }

    public void setLeftParenthesisClickCount(int leftParenthesisClickCount)
    {
        this.leftParenthesisClickCount = leftParenthesisClickCount;
    }

    public int getRightParenthesisClickCount()
    {
        return rightParenthesisClickCount;
    }

    public void setRightParenthesisClickCount(int rightParenthesisClickCount)
    {
        this.rightParenthesisClickCount = rightParenthesisClickCount;
    }

    /**
     * Returns the result of the left and right
     * parentheses click count being equal or not.
     * @return true if the click count is equal
     */
    public boolean parenthesisClickCountIsEqual()
    {
        return leftParenthesisClickCount == rightParenthesisClickCount;
    }
}
