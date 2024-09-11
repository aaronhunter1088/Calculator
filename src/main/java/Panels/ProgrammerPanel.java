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
            // TODO: look into these. may not be needed
            isOrPressed = false, isModulusPressed = false,
            isXorPressed = false, /*negateButtonBool = false,*/
            isNotPressed = false, isAndPressed = false,
            isShiftPressed = false;

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
        if (calculator.getCalculatorBase() == null) { calculator.setCalculatorBase(BASE_DECIMAL); }
        if (calculator.getCalculatorByte() == null) { calculator.setCalculatorByte(BYTE_BYTE); }
        setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
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
        String helpString = """
                How to use the %s Calculator
                
                Using the programmer operators:
                Steps listed here...
                
                Using the function operators:
                Steps listed here...
                
                Described below are how each button works from the top left down in detail.
                """
                .formatted(VIEW_PROGRAMMER.getValue());
        JMenu helpMenuItem = calculator.getHelpMenu();
        JMenuItem viewHelp = helpMenuItem.getItem(0);
        // remove any and all other view help actions
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel(helpString));
        helpMenuItem.add(viewHelp, 0);
        LOGGER.debug("Help menu configured for {}", calculator.getCalculatorView());
    }

    /**
     * Displays the help text in a scrollable pane
     * @param helpString the help text to display
     */
    public void showHelpPanel(String helpString)
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
        JOptionPane.showMessageDialog(calculator, scrollPane, "Viewing " + VIEW_PROGRAMMER.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        calculator.confirm("Viewing " + VIEW_PROGRAMMER.getValue() + " Calculator Help");
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
    public void appendToPane(String text)
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
                else if (calculator.getValueFromTextPaneForProgrammerPanel().isEmpty() && calculator.getValues()[0].isEmpty())
                {
                    calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
                    calculator.confirm("No need to perform " + DELETE + " operation");
                }
                else
                {
                    if (calculator.getValuesPosition() == 1 && calculator.getValues()[1].isEmpty())
                    { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete
                    if (calculator.getValues()[0].isEmpty())
                    { calculator.getValues()[0] = calculator.getValueFromTextPaneForProgrammerPanel(); }
                    LOGGER.debug("values[{}]: {}", calculator.getValuesPosition(), calculator.getValues()[calculator.getValuesPosition()]);
                    LOGGER.debug("textPane: {}", calculator.getValueFromTextPaneForProgrammerPanel());
                    // if no operator has been pushed but text pane has value
                    if (!calculator.isAdding() && !calculator.isSubtracting()
                        && !calculator.isMultiplying() && !calculator.isDividing()
                        && !calculator.getTextPaneValue().isEmpty())
                    {
                        String substring = calculator.getValueFromTextPaneForProgrammerPanel().substring(0, calculator.getValueFromTextPaneForProgrammerPanel().length()-1);
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
                            String substring = calculator.getValueFromTextPaneForProgrammerPanel().substring(0,calculator.getValueFromTextPaneForProgrammerPanel().length()-1);
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
     * The actions to perform when you click MemorySubtraction
     * @param actionEvent the click action
     */
    public void performButtonModulusAction(ActionEvent actionEvent)
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
            calculator.setIsFirstNumber(false);
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
    public void performButtonOrAction(ActionEvent actionEvent)
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
            calculator.setIsFirstNumber(false);
            calculator.getTextPane().setText(calculator.addNewLines()
                    + buttonChoice + " " + calculator.getValues()[0]);
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0] + " " + buttonChoice));
            calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            calculator.confirm("OR complete");
        }
        else if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            //setOrPressed(false);
            calculator.setIsFirstNumber(true);
            calculator.confirm("Pressed OR. Doing nothing");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            String sb = performOr(); // 2 OR 3 OR button presses
            //TODO: need to convert sb to DECIMAL form before storing in getValues()
            calculator.getValues()[0] = sb;
            calculator.getTextPane().setText(calculator.addNewLines(1)+calculator.getValues()[0]);
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
    public void performButtonXorAction(ActionEvent actionEvent)
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
            calculator.getTextPane().setText(calculator.addNewLines(1)+
                    calculator.getTextPaneValue() + " " + "XOR");
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
    public void performButtonNotAction(ActionEvent actionEvent)
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
        appendToPane(addBaseRepresentation());
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
                converted = calculator.getValueFromTextPaneForProgrammerPanel();
                updateValues = getAllowedLengthsOfTextPane().contains(converted.length());
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_OCTAL, calculator.getValueFromTextPaneForProgrammerPanel());
                    calculator.getValues()[calculator.getValuesPosition()] = converted;
                    calculator.setPreviousBase(BASE_BINARY);
                    calculator.setCalculatorBase(BASE_OCTAL);
                    appendToPane(calculator.getValues()[calculator.getValuesPosition()]);
                }
            }
            case BASE_OCTAL -> {
                calculator.setCalculatorBase(BASE_DECIMAL);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_OCTAL, BASE_DECIMAL, calculator.getValueFromTextPaneForProgrammerPanel());
                    calculator.getValues()[calculator.getValuesPosition()] = converted;
                    calculator.setPreviousBase(BASE_OCTAL);
                    appendToPane(calculator.getValues()[calculator.getValuesPosition()]);
                }
            }
            case BASE_DECIMAL  -> {
                calculator.setCalculatorBase(BASE_HEXADECIMAL);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    converted = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_HEXADECIMAL, calculator.getValueFromTextPaneForProgrammerPanel());
                    calculator.setPreviousBase(BASE_DECIMAL);
                    appendToPane(converted);
                }
            }
            case BASE_HEXADECIMAL -> {
                converted = calculator.convertFromBaseToBase(BASE_HEXADECIMAL, BASE_BINARY, calculator.getValueFromTextPaneForProgrammerPanel());
                calculator.setCalculatorBase(BASE_BINARY);
                // TODO: Create similar logic as i did for setting this for binary.
                updateValues = true;
                if (updateValues) {
                    calculator.setPreviousBase(BASE_HEXADECIMAL);
                    appendToPane(separateBits(converted));
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
            String textPaneText = calculator.getValueFromTextPaneForProgrammerPanel();
            if (allowedLengthMinusNewLines.contains(textPaneText.length()))
            { calculator.confirm("Byte length "+allowedLengthMinusNewLines+" already reached"); }
            else
            {
                appendToPane(separateBits(textPaneText + buttonChoice));
                calculator.writeHistory(buttonChoice, false);
                textPaneText = calculator.getValueFromTextPaneForProgrammerPanel();
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
        else if (BASE_DECIMAL == calculator.getCalculatorBase())
        { calculator.performNumberButtonAction(actionEvent); }
        else if (BASE_OCTAL == calculator.getCalculatorBase()) { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else /* (HEXADECIMAL == calculator.getCalculatorBase()) */ { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }
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

    /* Getters */
    public JTextPane getHistoryTextPane() { return programmerHistoryTextPane; }

    /* Setters */
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) { super.setLayout(panelLayout); }
    public void setProgrammerHistoryTextPane(JTextPane programmerHistoryTextPane) { this.programmerHistoryTextPane = programmerHistoryTextPane; }
}
