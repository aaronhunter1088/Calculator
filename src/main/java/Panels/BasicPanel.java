package Panels;

import Calculators.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static Types.CalculatorBase.*;
import static Types.CalculatorType.*;
import static Types.Texts.*;

public class BasicPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private Calculator calculator;
    private GridBagConstraints constraints;
    private final JPanel basicPanel = new JPanel(new GridBagLayout()),
                         memoryPanel = new JPanel(new GridBagLayout()),
                         buttonsPanel = new JPanel(new GridBagLayout()),
                         historyPanel = new JPanel(new GridBagLayout());
    private JTextPane basicHistoryTextPane;

    /* Constructors */

    /**
     * A zero argument constructor for creating a BasicPanel
     */
    public BasicPanel()
    {
        setName(BASIC.getValue());
        LOGGER.info("Empty Basic panel created");
    }

    /**
     * The main constructor used to create a BasicPanel
     * @param calculator the Calculator to use
     */
    public BasicPanel(Calculator calculator)
    {
        setupBasicPanel(calculator);
        LOGGER.info("Basic panel created");
    }

    /* Start of methods here */

    /**
     * The main method used to define the BasicPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     */
    public void setupBasicPanel(Calculator calculator)
    {
        this.calculator = calculator;
        setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        setSize(new Dimension(200, 336)); // sets main size
        setMinimumSize(new Dimension(200, 336)); // sets minimum size
        setupBasicPanelComponents();
        addComponentsToPanel();
        setName(BASIC.getValue());
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up {} panel", BASIC.getValue());
    }

    /**
     * Clears button actions, sets the CalculatorType,
     * CalculatorBase, ConverterType, and finally
     * sets up the basic panel and its components
     */
    private void setupBasicPanelComponents()
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
        LOGGER.debug("Actions removed...");

        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.setCalculatorType(BASIC);
        calculator.setConverterType(null);
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupNumberButtons();
        calculator.setupMemoryButtons();
        calculator.setupBasicPanelButtons();
        setupBasicHistoryZone();
        LOGGER.debug("Finished configuring the buttons");
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    private void addComponentsToPanel()
    {
        addComponent(basicPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        addComponent(memoryPanel, calculator.getButtonMemoryStore(),0, 0, new Insets(0,1,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0);
        addComponent(memoryPanel, calculator.getButtonMemoryRecall(),0, 1, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0);
        addComponent(memoryPanel, calculator.getButtonMemoryClear(),0, 2, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0);
        addComponent(memoryPanel, calculator.getButtonMemoryAddition(),0, 3, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0);
        addComponent(memoryPanel, calculator.getButtonMemorySubtraction(),0, 4, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0); //right:1
        addComponent(memoryPanel, calculator.getButtonHistory(),0, 5, new Insets(0, 0, 0, 1),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, 0);

        addComponent(basicPanel, memoryPanel, 1, 0, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);

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

        addComponent(basicPanel, buttonsPanel, 2, 0);

        addComponent(basicPanel);
        LOGGER.debug("Buttons added to the frame");
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
        constraints.gridy = row;
        constraints.gridx = column;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.weighty = weightXRow;
        constraints.weightx = weightYColumn;
        constraints.insets = insets == null ? new Insets(1, 1, 1, 1) : insets;
        if (fill != 0)   constraints.fill = fill;
        if (anchor != 0) constraints.anchor = anchor;
        if (c != null) panel.add(c, constraints);
        else           add(panel, constraints);
    }

//    /** Primarily used to add the textPane */
//    private void addComponent(JPanel panel, Component c, int column, Insets insets)
//    { addComponent(panel, c, 1, column, insets, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, 0); }

    /** Primarily used to add the buttons to a panel */
    private void addComponent(JPanel panel, Component c, int row, int column)
    { addComponent(panel, c, row, column, null, 1, 1, 1.0, 1.0, 0, 0); }

    /** Primarily used to add the basicPanel to the frame */
    private void addComponent(JPanel panel)
    { addComponent(panel, null, 0, 0, new Insets(0,0,0,0), 0, 0, 1.0, 1.0, 0, GridBagConstraints.NORTH); }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpMenu()
    {
        String helpString = """
                How to use the %s Calculator
                
                Using the basic operators:
                Step 1. Enter the first number.
                Step 2. Press a basic operator (%s, %s, %s, %s). The operator will be visible alongside the first number.
                Step 3. Enter the second number.
                Step 4. Push the Equals (%s) button to see the result.
                
                Using the function operators:
                Step 1. Enter the first number.
                Step 2. Press a function operator (%s, %s, %s, %s). The operator will not be visible alongside the first number.
                Step 3. Observe the output.
                Note: Attempting to use a function operator without first entering a number will yield an error message '%s'.
                
                Described below are how each button works from the top left down in detail.
                
                MemoryStore: %s
                Pressing this button will save the current value in the textPane to the calculators memory.
                You can save up to %d different numbers in memory.
                The memoryPosition starts at 0 and goes to 9. Once you save a number, the position will increment by 1.
                While no memories are saved, the other memory buttons will be disabled. If all slots are full, you will begin to overwrite values starting at position 0.
                
                MemoryClear: %s
                Pressing this button will clear the value in memory, starting from position 0, and increment over all positions until all memories are cleared.
                
                MemoryRecall: %s
                Pressing this button will recall the first value saved in memory, starting from position 0, and increment over all values.
                Once the last number is reached, it starts over.
                
                MemoryAdd: %s
                Pressing this button will add the current value in the textPane to the previously saved value.
                
                MemorySubtract: %s
                Pressing this button will subtract the current value in the textPane to the previously saved value.
                
                Percent: %s
                Pressing this button will return the value in the textPane as a percentage, or a message '%s', if no value is available in the textPane.
                Ex: 25 -> %s -> 0.25
                
                SquareRoot: %s
                Pressing this button will take the current value in the textPane and return the square root of that value. It will return the result in decimal form.
                If there is no value available in the textPane it will return a message '%s'. Attempting to get the square root of a negative number will return '%s'.
                
                Squared: %s
                Pressing this button will return the value in the textPane squared, or a message '%s', if no value is available in the textPane.
                
                Fraction: %s
                Pressing this button will return the value in the textPane as a fraction, performing 1 divided by the value in the textPane. If no value is available, it will display a message '%s'.
                Ex: 4 -> %s -> 0.25.
                
                ClearEntry: %s
                Pressing this button will clear the value in textPane, the stored value, and any main basic operators pushed if still obtaining the first number.
                If the second number is currently being obtained, only the textPane will clear. You will still have your first number available.
                
                Clear: %s
                Pressing this button will clear the value presented in the textPane, replace it with 0, and clear all values, memory values, and copied values.
                The 0 placed in the textPane is the indicator to state that everything is starting over.
                
                Delete: %s
                Pressing this button will remove the right most value from the textPane until the entire number is gone.
                It will only remove a single value, so 15.2 will become 15., allowing for exact replacement or deletion.
                
                Divide: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Numbers: %s %s %s %s %s %s %s %s %s %s
                Pressing a number button will place that value in the textPane.
                
                Multiply: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Subtract: %s
                Pressing this button will append the %s sign to the value in the the textPane. If you press the subtract button before entering a number,
                that number will become negative.
                
                Addition: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Negate: %s
                Pressing this button will negate the current number. If the current number is already negative, it will make it positive.
                
                Dot: %s
                Pressing this button will append the %s symbol to the value in the textPane. Once pressed the button will be disabled until an operator is pressed, unless the result includes the symbol.
                If the %s button is pressed or present in the textPane, this button will be disabled. Pressing the %s button before any number entry will add a zero before the dot.
                
                Equals: %s
                Pressing this button will perform the operation chosen or nothing if no basic operator was pressed.
                
                The remaining menu options are described below, read left to right.
                
                Look Menu:
                Choosing a new Look will affect how you see the Calculator. The choices are dependent on your operating system.
                
                View Menu:
                Here you can change the type of Calculator to use.
                
                Edit Menu:
                Copy: Selecting Copy will copy the current value in the textPane. Copy your value and use it anywhere.
                Paste: Selecting Paste will only paste the last copied from the Calculator value into the textPane.
                Clear History: Clears the history panel.
                Show Memories: Displays all the currently stored memories in the history panel.
                
                Help Menu:
                View Help: Describes how to use the currently selected view.
                About Calculator: will describe the current version and licensing information about the Calculator.
                """
                .formatted(BASIC.getValue(),
                        ADDITION.getValue(), SUBTRACTION.getValue(), MULTIPLICATION.getValue(), DIVISION.getValue(), EQUALS.getValue(),
                        PERCENT.getValue(), SQUARE_ROOT.getValue(),SQUARED.getValue(), FRACTION.getValue(), ENTER_A_NUMBER.getValue(),
                        MEMORY_STORE.getValue(), calculator.getMemoryValues().length, // MemoryStore,
                        MEMORY_CLEAR.getValue(),
                        MEMORY_RECALL.getValue(),
                        MEMORY_ADDITION.getValue(),
                        MEMORY_SUBTRACTION.getValue(),
                        PERCENT.getValue(), ENTER_A_NUMBER.getValue(), PERCENT.getValue(), // Percent
                        SQUARE_ROOT.getValue(), NOT_A_NUMBER.getValue(), ERR.getValue(),
                        SQUARED.getValue(), ENTER_A_NUMBER.getValue(),
                        FRACTION.getValue(), ENTER_A_NUMBER.getValue(), FRACTION.getValue(),
                        CLEAR_ENTRY.getValue(),
                        CLEAR.getValue(),
                        DELETE.getValue(),
                        DIVISION.getValue(), DIVISION.getValue(), ENTER_A_NUMBER.getValue(),
                        ZERO.getValue(), ONE.getValue(), TWO.getValue(), THREE.getValue(), FOUR.getValue(), FIVE.getValue(),
                        SIX.getValue(), SEVEN.getValue(), EIGHT.getValue(), NINE.getValue(),
                        MULTIPLICATION.getValue(), MULTIPLICATION.getValue(), ENTER_A_NUMBER.getValue(),
                        SUBTRACTION.getValue(), SUBTRACTION.getValue(),
                        ADDITION.getValue(), ADDITION.getValue(), ENTER_A_NUMBER.getValue(),
                        NEGATE.getValue(),
                        DECIMAL.getValue(), DECIMAL.getValue(), DECIMAL.getValue(), DECIMAL.getValue(),
                        EQUALS.getValue());

        JMenu helpMenuItem = calculator.getHelpMenu();
        JMenuItem viewHelp = helpMenuItem.getItem(0);
        // remove any and all other view help actions
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel(helpString));
        helpMenuItem.add(viewHelp, 0);
        LOGGER.debug("Help menu configured for {}", calculator.getCalculatorType());
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
        JOptionPane.showMessageDialog(calculator, scrollPane, "Viewing " + BASIC.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        calculator.confirm("Viewing " + BASIC.getValue() + " Calculator Help");
    }

    /**
     * Displays the history for the BasicPanel
     * during each active instance
     */
    public void setupBasicHistoryZone()
    {
        LOGGER.debug("Configuring BasicHistoryZone...");
        constraints.anchor = GridBagConstraints.WEST;
        addComponent(historyPanel, new JLabel(HISTORY.getValue()), 0, 0); // space before with jtextarea

        calculator.setupHistoryTextPane();
        JScrollPane scrollPane = new JScrollPane(basicHistoryTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(basicHistoryTextPane.getSize());

        addComponent(historyPanel, scrollPane, 1, 0, new Insets(0,0,0,0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("BasicHistoryZone configured");
    }
//    /**
//     * The actions to perform when MemoryStore is clicked
//     * @param actionEvent the click action
//     */
//    public void performMemoryStoreAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.getTextPaneWithoutNewLineCharacters().isBlank())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("No number to add to memory");
//        }
//        else if (calculator.textPaneContainsBadText())
//        { calculator.confirm("Not saving " + calculator.getTextPaneWithoutNewLineCharacters() + " in Memory"); }
//        else
//        {
//            if (calculator.getMemoryPosition() == 10) // reset to 0
//            { calculator.setMemoryPosition(0); }
//            calculator.getMemoryValues()[calculator.getMemoryPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
//            calculator.getButtonMemoryRecall().setEnabled(true);
//            calculator.getButtonMemoryClear().setEnabled(true);
//            calculator.getButtonMemoryAddition().setEnabled(true);
//            calculator.getButtonMemorySubtraction().setEnabled(true);
//            basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                            + " Saved: " + calculator.getMemoryValues()[calculator.getMemoryPosition()] + " to memory location " + (calculator.getMemoryPosition()+1)
//            );
//            calculator.setMemoryPosition(calculator.getMemoryPosition() + 1);
//            calculator.confirm(calculator.getMemoryValues()[calculator.getMemoryPosition()-1] + " is stored in memory at position: " + (calculator.getMemoryPosition()-1));
//
//        }
//    }
//    /**
//     * The actions to perform when MemoryRecall is clicked
//     * @param actionEvent the click action
//     */
//    public void performMemoryRecallAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.getMemoryRecallPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]))
//        { calculator.setMemoryRecallPosition(calculator.getLowestMemoryPosition()); }
//        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]);
//        calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
//        basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                        calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                        + " Recalled: " + calculator.getMemoryValues()[calculator.getMemoryRecallPosition()] + " at memory location " + (calculator.getMemoryRecallPosition()+1)
//        );
//        calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
//        calculator.confirm("Recalling number in memory: " + calculator.getMemoryValues()[(calculator.getMemoryRecallPosition()-1)] + " at position: " + (calculator.getMemoryRecallPosition()-1));
//    }
//    /**
//     * The actions to perform when MemoryClear is clicked
//     * @param actionEvent the click action
//     */
//    public void performMemoryClearAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.getMemoryPosition() == 10)
//        {
//            LOGGER.debug("Resetting memoryPosition to 0");
//            calculator.setMemoryPosition(0);
//        }
//        if (!calculator.isMemoryValuesEmpty())
//        {
//            calculator.setMemoryPosition(calculator.getLowestMemoryPosition());
//            LOGGER.info("Clearing memoryValue[{}] = {}", calculator.getMemoryPosition(), calculator.getMemoryValues()[calculator.getMemoryPosition()]);
//            basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                            + " Cleared " + calculator.getMemoryValues()[calculator.getMemoryPosition()] + " from memory location " + (calculator.getMemoryPosition()+1)
//            );
//            calculator.getMemoryValues()[calculator.getMemoryPosition()] = BLANK.getValue();
//            calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
//            calculator.confirm("Cleared memory at " + calculator.getMemoryPosition());
//            // MemorySuite could now be empty
//            if (calculator.isMemoryValuesEmpty())
//            {
//                calculator.setMemoryPosition(0);
//                calculator.setMemoryRecallPosition(0);
//                calculator.getButtonMemoryClear().setEnabled(false);
//                calculator.getButtonMemoryRecall().setEnabled(false);
//                calculator.getButtonMemoryAddition().setEnabled(false);
//                calculator.getButtonMemorySubtraction().setEnabled(false);
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + ZERO.getValue());
//                calculator.confirm("MemorySuite is empty");
//            }
//        }
//    }
//    /**
//     * The actions to perform when M+ is clicked
//     * Will add the current number in the textPane to the previously
//     * save value in Memory. It then displays that result in the
//     * textPane as a confirmation.
//     * @param actionEvent the click action
//     */
//    public void performMemoryAdditionAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText())
//        { calculator.confirm("Cannot add bad text to memories values"); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isBlank())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("No number to add to memory");
//        }
//        else
//        {
//            LOGGER.debug("textPane: '" + calculator.getTextPaneWithoutNewLineCharacters() + "'");
//            LOGGER.debug("memoryValues[{}] = {}",calculator.getMemoryPosition()-1, calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
//            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters())
//                    + Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); // create result forced double
//            LOGGER.debug("{} + {} = {}",calculator.getTextPaneWithoutNewLineCharacters(), calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)], result);
//            if (result % 1 == 0)
//            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
//            else
//            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result); }
//            basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                            + " Added " + calculator.getTextPaneWithoutNewLineCharacters() + " to memories at " + (calculator.getMemoryPosition()) + " " + EQUALS.getValue() + " " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]
//            );
//            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
//
//        }
//    }
//    /**
//     * The actions to perform when MemorySubtraction is clicked
//     * @param actionEvent the click action
//     */
//    public void performMemorySubtractionAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText())
//        { calculator.confirm("Cannot subtract bad text to memories values"); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isBlank())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("No number to subtract from memory");
//        }
//        else
//        {
//            LOGGER.debug("textPane: '{}'", calculator.getTextPaneWithoutNewLineCharacters());
//            LOGGER.debug("memoryValues[{}] = {}",calculator.getMemoryPosition()-1, calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
//            double result = Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)])
//                    - Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters()); // create result forced double
//            LOGGER.debug("{} - {} = {}",calculator.getTextPaneWithoutNewLineCharacters(), calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)], result);
//            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
//            if (result % 1 == 0)
//            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)); }
//            else
//            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result); }
//            basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                            + " Subtracted " + calculator.getTextPaneWithoutNewLineCharacters() + " to memories at " + (calculator.getMemoryPosition()) + " " + EQUALS.getValue() + " " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]
//            );
//            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
//        }
//    }
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
            basicPanel.remove(historyPanel);
            addComponent(basicPanel, buttonsPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
        else
        {
            LOGGER.debug("{}", actionEvent.getActionCommand());
            calculator.getButtonHistory().setText(HISTORY_OPEN.getValue());
            basicPanel.remove(buttonsPanel);
            addComponent(basicPanel, historyPanel, 2, 0);
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    /**
     * The actions to perform when the Percent button is clicked
     * @param actionEvent the click action
     */
    public void performPercentButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform " + PERCENT + " operation"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            calculator.confirm("Pressed: " + buttonChoice);
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
            result /= 100;
            LOGGER.debug("result: " + result);
            calculator.getValues()[calculator.getValuesPosition()] = Double.toString(result);
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.writeHistory(buttonChoice, false);
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.getTextPaneWithoutNewLineCharacters()
//            );
            LOGGER.debug("values[{}] is {}", calculator.getValuesPosition(), calculator.getValues()[calculator.getValuesPosition()]);
            LOGGER.debug("textPane: {}", calculator.getTextPane().getText());
            calculator.getButtonDecimal().setEnabled(false);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

//    /**
//     * The actions to perform when the SquareRoot button is clicked
//     * @param actionEvent the click action
//     */
//    public void performSquareRootButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        LOGGER.debug("textPane: {}", calculator.getTextPaneWithoutNewLineCharacters());
//        if (calculator.textPaneContainsBadText())
//        { calculator.confirm("Cannot perform " + SQUARE_ROOT + " operation"); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("Cannot perform " + SQUARE_ROOT + " when textPane blank");
//        }
//        else if (calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]))
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ERR.getValue());
//            calculator.confirm("Cannot perform " + SQUARE_ROOT + " on negative number");
//        }
//        else
//        {
//            double result = Math.sqrt(Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters()));
//            LOGGER.debug("result: " + result);
//            if (result % 1 == 0)
//            {
//                calculator.getValues()[calculator.getValuesPosition()] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                calculator.getValues()[calculator.getValuesPosition()] = calculator.formatNumber(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(false);
//            }
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            calculator.writeHistory(buttonChoice, false);
////            basicHistoryTextPane.setText(
////            basicHistoryTextPane.getText() +
////            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////            + " Result: " + calculator.getValues()[calculator.getValuesPosition()]
////            );
//            calculator.confirm("Pressed " + buttonChoice);
//        }
//    }

    /**
     * The actions to perform when the Squared button is clicked
     * @param actionEvent the click action
     */
    public void performSquaredButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform " + SQUARED + " operation"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            calculator.confirm("No number to square");
        }
        else
        {
            double result = Math.pow(Double.parseDouble(calculator.getValues()[0]), 2);
            LOGGER.debug("result: " + result);
            if (result % 1 == 0)
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
                calculator.getButtonDecimal().setEnabled(true);
            }
            else
            {
                calculator.getValues()[calculator.getValuesPosition()] = String.valueOf(result);
                calculator.getButtonDecimal().setEnabled(false);
            }
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.setNumberNegative(false);
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
//            );
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Fraction button is clicked
     * @param actionEvent the click action
     */
    public void performFractionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform " + FRACTION + " operation"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
            calculator.confirm("Cannot perform " + FRACTION + " operation");
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
            result = 1 / result;
            LOGGER.debug("result: " + result);
            if (INFINITY.getValue().equals(String.valueOf(result)))
            {
                calculator.getButtonDecimal().setEnabled(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + INFINITY.getValue());
                calculator.getValues()[calculator.getValuesPosition()] = BLANK.getValue();
            }
            else if (result % 1 == 0)
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
                calculator.getButtonDecimal().setEnabled(true);
            }
            else
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.formatNumber(String.valueOf(result));
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
                calculator.getButtonDecimal().setEnabled(false);
            }
            calculator.setNumberNegative(false);
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
//            );
            calculator.confirm("Pressed " + buttonChoice);
        }
    }
//
//    /**
//     * The action to perform when the ClearEntry button is clicked
//     * @param actionEvent the click action
//     */
//    public void performClearEntryButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
//        { calculator.confirm(CLEAR_ENTRY + " called... nothing to clear"); }
//        else if (calculator.getValuesPosition() == 0 || calculator.getValues()[1].isEmpty())
//        {
//            calculator.getValues()[0] = BLANK.getValue();
//            calculator.resetBasicOperators(false);
//            calculator.setValuesPosition(0);
//            calculator.setFirstNumber(true);
//            calculator.getButtonDecimal().setEnabled(true);
//            calculator.getTextPane().setText(BLANK.getValue());
//            calculator.writeHistoryWithMessage(buttonChoice, false, " Cleared first number & main operators");
////            basicHistoryTextPane.setText(
////            basicHistoryTextPane.getText() +
////            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////            + " Cleared first number & main operators"
////            );
//            calculator.confirm("Pressed: " + buttonChoice);
//        }
//        else
//        {
//            String operator = determineIfBasicPanelOperatorWasPushed();
//            calculator.getValues()[1] = BLANK.getValue();
//            calculator.setFirstNumber(false);
//            calculator.setValuesPosition(1);
//            calculator.setNumberNegative(false);
//            calculator.getButtonDecimal().setEnabled(true);
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + ' ' + operator);
//            calculator.writeHistoryWithMessage(buttonChoice, false, " Cleared second number only");
////            basicHistoryTextPane.setText(
////            basicHistoryTextPane.getText() +
////            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////            + " Cleared second number only"
////            );
//            calculator.confirm("Pressed: " + buttonChoice);
//        }
//    }
//
//    /**
//     * The actions to perform when the Clear button is clicked
//     * @param actionEvent the action performed
//     */
//    public void performClearButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        for (int i=0; i < 3; i++)
//        {
//            if (i == 0) { calculator.getValues()[0] = ZERO.getValue(); }
//            else { calculator.getValues()[i] = BLANK.getValue(); }
//        }
//        for(int i=0; i < 10; i++)
//        { calculator.getMemoryValues()[i] = BLANK.getValue(); }
//        calculator.getTextPane().setText(calculator.addNewLineCharacters() + ZERO.getValue());
//
//        calculator.resetBasicOperators(false);
//        calculator.setValuesPosition(0);
//        calculator.setMemoryPosition(0);
//        calculator.setFirstNumber(true);
//        calculator.setNumberNegative(false);
//        calculator.getButtonMemoryRecall().setEnabled(false);
//        calculator.getButtonMemoryClear().setEnabled(false);
//        calculator.getButtonMemoryAddition().setEnabled(false);
//        calculator.getButtonMemorySubtraction().setEnabled(false);
//        calculator.getButtonDecimal().setEnabled(true);
//        calculator.writeHistoryWithMessage(buttonChoice, false, " Cleared all values");
////        basicHistoryTextPane.setText(
////        basicHistoryTextPane.getText() +
////        calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////        + " Cleared all values"
////        );
//        calculator.confirm("Pressed: " + buttonChoice);
//    }
//
//    /**
//     * The actions to perform when the Delete button is clicked
//     * @param actionEvent the click action
//     */
//    public void performDeleteButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText())
//        {
//            calculator.getTextPane().setText(BLANK.getValue());
//            calculator.confirm("Pressed " + buttonChoice);
//        }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty() && calculator.getValues()[0].isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("No need to perform " + DELETE + " operation");
//        }
//        else
//        {
//            if (calculator.getValues()[1].isEmpty())
//            { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete
//            if (calculator.getValues()[0].isEmpty())
//            { calculator.getValues()[0] = calculator.getTextPaneWithoutNewLineCharacters(); } // 5 + 3 = 8, values[0] = BLANK.getValue()
//
//            LOGGER.debug("values[{}]: {}", calculator.getValuesPosition(), calculator.getValues()[calculator.getValuesPosition()]);
//            LOGGER.debug("textPane: {}", calculator.getTextPaneWithoutNewLineCharacters());
//            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
//                    && !calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
//            {
//                calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(0,calculator.getValues()[calculator.getValuesPosition()].length()-1);
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            }
//            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
//            {
//                LOGGER.debug("An operator has been pushed");
//                if (calculator.getValuesPosition() == 0)
//                {
//                    if (calculator.isAdding()) calculator.setAdding(false);
//                    else if (calculator.isSubtracting()) calculator.setSubtracting(false);
//                    else if (calculator.isMultiplying()) calculator.setMultiplying(false);
//                    else /*if (calculator.isDividing())*/ calculator.setDividing(false);
//                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutAnyOperator();
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getTextPaneWithoutAnyOperator()));
//                }
//                else
//                {
//                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(0,calculator.getValues()[calculator.getValuesPosition()].length()-1);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//                }
//            }
//            calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[calculator.getValuesPosition()]));
//            calculator.writeHistory(buttonChoice, false);
////            basicHistoryTextPane.setText(
////            basicHistoryTextPane.getText() +
////            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
////            );
//            calculator.confirm("Pressed " + buttonChoice);
//        }
//    }

//    /**
//     * The actions to perform when the Divide button is clicked
//     * @param actionEvent the click action
//     */
//    public void performDivideButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText() || calculator.isMinimumValue() || calculator.isMaximumValue())
//        { calculator.confirm("Cannot perform " + DIVISION); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty() && calculator.getValues()[0].isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("Cannot perform " + DIVISION + " operation");
//        }
//        else
//        {
//            LOGGER.info("button: " + buttonChoice);
//            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
//                    && !calculator.getTextPane().getText().isBlank() && !calculator.getValues()[calculator.getValuesPosition()].isBlank())
//            {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setDividing(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
//            {
//                addition(DIVISION.getValue()); // 5 + 3 ÷
//                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setDividing(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setDividing(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
//            {
//                subtract(DIVISION.getValue()); // 5 - 3 ÷
//                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setDividing(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setDividing(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
//            {
//                multiply(DIVISION.getValue()); // 5 ✕ 3 ÷
//                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setDividing(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setDividing(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty() ) //&& !calculator.getValues()[1].equals("0"))
//            {
//                divide(DIVISION.getValue()); // 5 ÷ 3 ÷
//                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setDividing(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else if (calculator.getTextPaneWithoutAnyOperator().equals(INFINITY.getValue()))
//                {
//                    calculator.setDividing(false);
//                    calculator.setValuesPosition(0);
//                }
//                else
//                {
//                    calculator.setDividing(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank() && calculator.getValues()[0].isBlank())
//            {
//                LOGGER.error("The user pushed divide but there is no number");
//                LOGGER.info("Setting values[0] to textPane value");
//                calculator.getValues()[0] = calculator.getTextPaneWithoutNewLineCharacters();
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setDividing(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
//            { LOGGER.info("already chose an operator. choose another number."); }
//            calculator.getButtonDecimal().setEnabled(true);
//            if (calculator.isMinimumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Minimum number met"); }
//            else if (calculator.isMaximumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Maximum number met"); }
//            else { calculator.confirm("Pressed: " + buttonChoice); }
//        }
//    }
//    /**
//     * The inner logic for dividing
//     */
//    private void divide()
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result;
//        if (!ZERO.getValue().equals(calculator.getValues()[1]))
//        {
//            result = Double.parseDouble(calculator.getValues()[0]) / Double.parseDouble(calculator.getValues()[1]); // create result forced double
//            LOGGER.info(calculator.getValues()[0] + " " + DIVISION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//            if (result % 1 == 0)
//            {
//                LOGGER.info("We have a whole number");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + DIVISION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.info("We have a decimal");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + DIVISION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.addCommas(calculator.formatNumber(String.valueOf(result)));
//            }
//            calculator.confirm("Finished dividing");
//        }
//        else
//        {
//            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + INFINITY.getValue());
//            calculator.getValues()[0] = BLANK.getValue();
//            calculator.getValues()[1] = BLANK.getValue();
//            calculator.setFirstNumber(true);
//            calculator.confirm("Attempted to divide by 0. Values[0] = 0");
//        }
//    }
//    private void divide(String continuedOperation)
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result;
//        if (!ZERO.getValue().equals(calculator.getValues()[1]))
//        {
//            result = Double.parseDouble(calculator.getValues()[0]) / Double.parseDouble(calculator.getValues()[1]); // create result forced double
//            LOGGER.info(calculator.getValues()[0] + " " + DIVISION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//            if (result % 1 == 0)
//            {
//                LOGGER.info("We have a whole number");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + DIVISION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textPane changed to whole number, or int
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.info("We have a decimal");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + DIVISION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(String.valueOf(result)) + " " + continuedOperation
//                );
//                calculator.getValues()[0] = String.valueOf(result);
//                calculator.getButtonDecimal().setEnabled(false);
//            }
//            LOGGER.info("Finished " + DIVISION);
//        }
//        else
//        {
//            LOGGER.warn("Attempting to divide by zero." + CANNOT_DIVIDE_BY_ZERO.getValue());
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + INFINITY.getValue());
//            calculator.getValues()[0] = BLANK.getValue();
//            calculator.getValues()[1] = BLANK.getValue();
//            calculator.setFirstNumber(true);
//            calculator.confirm("Attempted to divide by 0. Values[0] = 0");
//        }
//    }

//    /**
//     * The actions to perform when clicking any number button
//     * @param actionEvent the click action
//     */
//    public void performNumberButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (!calculator.isFirstNumber()) // second number
//        {
//            LOGGER.debug("!isFirstNumber is: " + !calculator.isFirstNumber());
//            calculator.getTextPane().setText("");
//            calculator.setFirstNumber(true);
//            if (!calculator.isNegating()) calculator.setNumberNegative(false);
//            if (!calculator.isDotPressed())
//            {
//                LOGGER.debug("Decimal is disabled. Enabling");
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//        }
//        if (calculator.performInitialChecks())
//        {
//            LOGGER.warn("Invalid entry in textPane. Clearing...");
//            calculator.getTextPane().setText(BLANK.getValue());
//            calculator.getValues()[calculator.getValuesPosition()] = BLANK.getValue();
//            calculator.setFirstNumber(true);
//            calculator.getButtonDecimal().setEnabled(true);
//        }
//        if (calculator.getValues()[0].isBlank())
//        {
//            LOGGER.info("Highest size not met. Values[0] is blank");
//        }
//        else if (calculator.checkValueLength())
//        {
//            LOGGER.info("Highest size of value has been met");
//            calculator.confirm("Max length of 7 digit number met");
//            return;
//        }
//        if (calculator.isNegating() && calculator.isNumberNegative() && calculator.getValues()[calculator.getValuesPosition()].isBlank())
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = SUBTRACTION.getValue() + buttonChoice;
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
//            );
//            calculator.setNegating(false);
//            calculator.setNumberNegative(true);
//        }
//        else if (calculator.isNegating() && calculator.isNumberNegative() && !calculator.getValues()[1].isBlank())
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = SUBTRACTION.getValue() + buttonChoice;
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
//            );
//            calculator.setNegating(false);
//            calculator.setNumberNegative(true);
//        }
//        else
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()] + buttonChoice;
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
//            );
//        }
//        calculator.confirm("Pressed " + buttonChoice);
//    }

//    /**
//     * The actions to perform when the Multiplication button is clicked
//     * @param actionEvent the click action
//     */
//    public void performMultiplicationAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText() || calculator.isMinimumValue() || calculator.isMaximumValue())
//        { calculator.confirm("Cannot perform " + MULTIPLICATION); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty() && calculator.getValues()[0].isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("Cannot perform " + MULTIPLICATION + " operation");
//        }
//        else
//        {
//            LOGGER.info("button: " + buttonChoice);
//            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
//                    && !calculator.getTextPane().getText().isBlank() && !calculator.getValues()[calculator.getValuesPosition()].isBlank())
//            {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setMultiplying(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
//            {
//                addition(MULTIPLICATION.getValue()); // 5 + 3 ✕...
//                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setMultiplying(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setMultiplying(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
//            {
//                subtract(MULTIPLICATION.getValue()); // 5 - 3 ✕...
//                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setMultiplying(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setMultiplying(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
//            {
//                multiply(MULTIPLICATION.getValue());
//                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setMultiplying(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setMultiplying(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
//            {
//                divide(MULTIPLICATION.getValue());
//                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setMultiplying(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setMultiplying(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank() && calculator.getValues()[0].isBlank())
//            {
//                LOGGER.error("The user pushed multiple but there is no number");
//                LOGGER.info("Setting values[0] to textPane value");
//                calculator.getValues()[0] = calculator.getTextPaneWithoutNewLineCharacters();
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setMultiplying(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
//            { LOGGER.info("already chose an operator. choose another number."); }
//            calculator.getButtonDecimal().setEnabled(true);
//            if (calculator.isMinimumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Minimum number met"); }
//            else if (calculator.isMaximumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Maximum number met"); }
//            else { calculator.confirm("Pressed: " + buttonChoice); }
//        }
//    }
//    /**
//     * The inner logic for multiplying
//     */
//    private void multiply()
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0])
//                * Double.parseDouble(calculator.getValues()[1]);
//        LOGGER.info(calculator.getValues()[0] + " " + MULTIPLICATION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//        try
//        {
//            int wholeResult = Integer.parseInt(calculator.clearZeroesAndDecimalAtEnd(calculator.formatNumber(String.valueOf(result))));
//            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
//            LOGGER.info("We have a whole number");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + MULTIPLICATION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//            );
//            calculator.getValues()[0] = calculator.addCommas(String.valueOf(wholeResult));
//            calculator.getButtonDecimal().setEnabled(true);
//        }
//        catch (NumberFormatException nfe)
//        {
//            calculator.logException(nfe);
//            LOGGER.info("We have a decimal");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + MULTIPLICATION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result)))
//            );
//            calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
//        }
//    }
//    private void multiply(String continuedOperation)
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0])
//                * Double.parseDouble(calculator.getValues()[1]);
//        LOGGER.info(calculator.getValues()[0] + " " + MULTIPLICATION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//        try
//        {
//            int wholeResult = Integer.parseInt(calculator.clearZeroesAndDecimalAtEnd(calculator.formatNumber(String.valueOf(result))));
//            LOGGER.debug("Result is {} but ultimately is a whole number: {}", result, wholeResult);
//            LOGGER.info("We have a whole number");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + MULTIPLICATION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas((String.valueOf(wholeResult))) + " " + continuedOperation
//            );
//            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(wholeResult));
//            calculator.getButtonDecimal().setEnabled(true);
//        }
//        catch (NumberFormatException nfe)
//        {
//            calculator.logException(nfe);
//            LOGGER.info("We have a decimal");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + MULTIPLICATION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result))) + " " + continuedOperation
//            );
//            calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
//            calculator.getButtonDecimal().setEnabled(false);
//        }
//        LOGGER.info("Finished " + MULTIPLICATION);
//    }
//
//    /**
//     * The actions to perform when the Subtraction button is clicked
//     * @param actionEvent the click action
//     */
//    public void performSubtractionButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText() || calculator.isMinimumValue())
//        { calculator.confirm("Cannot perform " + SUBTRACTION); }
//        else
//        {
//            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
//                    && !calculator.getTextPane().getText().isBlank() && !calculator.getValues()[calculator.getValuesPosition()].isBlank())
//            {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setSubtracting(true);
//                calculator.setFirstNumber(false);
//                calculator.setNegating(false);
//                calculator.setNumberNegative(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
//                    && calculator.getTextPaneWithoutNewLineCharacters().isBlank())
//            {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + buttonChoice
//                );
//                calculator.setNegating(true);
//                calculator.setNumberNegative(true);
//            }
//            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
//            {
//                addition(SUBTRACTION.getValue());
//                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setSubtracting(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setSubtracting(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
//            {
//                subtract(SUBTRACTION.getValue());
//                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setSubtracting(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setSubtracting(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
//            {
//                multiply(SUBTRACTION.getValue());
//                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setSubtracting(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setSubtracting(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
//            {
//                divide(SUBTRACTION.getValue());
//                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setSubtracting(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setSubtracting(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if ((calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
//                    && !calculator.isNegating())
//            {
//                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + buttonChoice
//                );
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
//                calculator.setNegating(true);
//                calculator.setNumberNegative(true);
//            }
//            else if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank() && calculator.getValues()[0].isBlank() && !calculator.isNegating())
//            {
//                LOGGER.error("The user pushed subtract but there is no number");
//                LOGGER.info("Setting values[0] to textPane value");
//                calculator.getValues()[0] = calculator.getTextPaneWithoutNewLineCharacters();
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setSubtracting(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            calculator.getButtonDecimal().setEnabled(true);
//            if (!calculator.isNegating()) calculator.setNumberNegative(false);
//            if (calculator.isMinimumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Minimum number met"); }
//            else if (calculator.isMaximumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Maximum number met"); }
//            else { calculator.confirm("Pressed: " + buttonChoice); }
//        }
//    }
//    /**
//     * The inner logic for subtracting
//     */
//    private void subtract()
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0]) - Double.parseDouble(calculator.getValues()[1]);
//        if (calculator.isNegating())
//        {
//            if (result % 1 == 0)
//            {
//                LOGGER.info("We have a whole number");
//                LOGGER.debug(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.convertToPositive(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + result);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)));
//                calculator.setNegating(false);
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.info("We have a decimal");
//                LOGGER.debug(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.convertToPositive(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + result);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.addCommas(calculator.formatNumber(String.valueOf(result)));
//                calculator.setNegating(false);
//                calculator.getButtonDecimal().setEnabled(false);
//            }
//        }
//        else
//        {
//            if (result % 1 == 0)
//            {
//                LOGGER.info("We have a whole number");
//                LOGGER.debug(calculator.getValues()[0] + " " + SUBTRACTION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.info("We have a decimal");
//                LOGGER.debug(calculator.getValues()[0] + " " + SUBTRACTION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.addCommas(calculator.formatNumber(String.valueOf(result)));
//                calculator.getButtonDecimal().setEnabled(false);
//            }
//        }
//    }
//    private void subtract(String continuedOperation)
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0]) - Double.parseDouble(calculator.getValues()[1]);
//        if (result % 1 == 0)
//        {
//            if (calculator.isMinimumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Minimum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else if (calculator.isMaximumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Maximum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                if (calculator.isNegating())
//                {
//                    LOGGER.debug("We have a whole number, negating");
//                    LOGGER.info(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.convertToPositive(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + result);
//                    basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                    calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                    + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.convertToPositive(calculator.getValues()[1])) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
//                    );
//                    calculator.setNegating(false);
//                    calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                    calculator.getButtonDecimal().setEnabled(true);
//                }
//                else
//                {
//                    LOGGER.debug("We have a whole number");
//                    LOGGER.info(calculator.getValues()[0] + " " + SUBTRACTION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//                    basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                    calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                    + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
//                    );
//                    calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                    calculator.getButtonDecimal().setEnabled(true);
//                }
//            }
//        }
//        else
//        {
//            if (calculator.isMinimumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Minimum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else if (calculator.isMaximumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Maximum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                if (calculator.isNegating())
//                {
//                    LOGGER.debug("We have a decimal, negating");
//                    LOGGER.info(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.convertToPositive(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + result);
//                    basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                    calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                    + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.convertToPositive(calculator.getValues()[1])) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result))) + " " + continuedOperation
//                    );
//                    calculator.setNegating(false);
//                    calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
//                    calculator.getButtonDecimal().setEnabled(false);
//                }
//                else
//                {
//                    LOGGER.debug("We have a decimal");
//                    LOGGER.info(calculator.getValues()[0] + " " + SUBTRACTION.getValue() + " " + calculator.convertToPositive(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + result);
//                    basicHistoryTextPane.setText(
//                    basicHistoryTextPane.getText() +
//                    calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                    + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + SUBTRACTION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result))) + " " + continuedOperation
//                    );
//                    calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
//                    calculator.getButtonDecimal().setEnabled(false);
//                }
//            }
//        }
//        LOGGER.info("Finished " + SUBTRACTION);
//    }
//
//    /**
//     * The actions to perform when the Addition button is clicked
//     * @param actionEvent the click action
//     */
//    public void performAdditionButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText() || calculator.isMaximumValue())
//        { calculator.confirm("Cannot perform " + ADDITION); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty() && calculator.getValues()[0].isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("Cannot perform " + ADDITION + " operation");
//        }
//        else
//        {
//            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
//                    && !calculator.getTextPane().getText().isBlank() && !calculator.getValues()[calculator.getValuesPosition()].isBlank())
//            {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setAdding(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
//            {
//                addition(ADDITION.getValue());  // 5 + 3 + ...
//                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
//                if (calculator.isMaximumValue()) // we can add to the minimum number, not to the maximum
//                {
//                    calculator.setAdding(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setAdding(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
//            {
//                subtract(ADDITION.getValue()); // 5 - 3 + ...
//                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setAdding(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setAdding(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
//            {
//                multiply(ADDITION.getValue()); // 5 ✕ 3 + ...
//                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setAdding(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setAdding(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
//            {
//                divide(ADDITION.getValue()); // 5 ÷ 3 + ...
//                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
//                if (calculator.isMinimumValue() || calculator.isMaximumValue())
//                {
//                    calculator.setAdding(false);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]));
//                }
//                else
//                {
//                    calculator.setAdding(true);
//                    calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice);
//                }
//            }
//            else if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank() && calculator.getValues()[0].isBlank())
//            {
//                LOGGER.error("The user pushed plus but there is no number");
//                LOGGER.info("Setting values[0] to textPane value");
//                calculator.getValues()[0] = calculator.getTextPaneWithoutNewLineCharacters();
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + buttonChoice
//                );
//                calculator.setAdding(true);
//                calculator.setFirstNumber(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
//            }
//            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
//            { LOGGER.error("Already chose an operator. Choose another number."); }
//            calculator.getButtonDecimal().setEnabled(true);
//            if (calculator.isMinimumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Minimum number met"); }
//            else if (calculator.isMaximumValue())
//            { calculator.confirm("Pressed: " + buttonChoice + " Maximum number met"); }
//            else { calculator.confirm("Pressed: " + buttonChoice); }
//        }
//    }
//    /**
//     * The inner logic for addition.
//     * This method performs the addition between values[0] and values[1],
//     * clears and trailing zeroes if the result is a whole number (15.0000)
//     * resets dotPressed to false, enables buttonDot and stores the result
//     * back in values[0]
//     */
//    private void addition()
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0]) + Double.parseDouble(calculator.getValues()[1]); // create result forced double
//        LOGGER.info(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//        if (result % 1 == 0)
//        {
//            LOGGER.info("We have a whole number");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//            );
//            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//            calculator.getButtonDecimal().setEnabled(true);
//        }
//        else
//        {
//            LOGGER.info("We have a decimal");
//            basicHistoryTextPane.setText(
//            basicHistoryTextPane.getText() +
//            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + EQUALS.getValue() + RIGHT_PARENTHESIS.getValue()
//            + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.formatNumber(String.valueOf(result)))
//            );
//            calculator.getButtonDecimal().setEnabled(false);
//            calculator.getValues()[0] = calculator.addCommas(calculator.formatNumber(String.valueOf(result)));
//        }
//    }
//    private void addition(String continuedOperation)
//    {
//        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
//        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
//        double result = Double.parseDouble(calculator.getValues()[0]) + Double.parseDouble(calculator.getValues()[1]); // create result forced double
//        LOGGER.info(calculator.getValues()[0] + " " + ADDITION.getValue() + " " + calculator.getValues()[1] + " " + EQUALS.getValue() + " " + result);
//        if (result % 1 == 0)
//        {
//            if (calculator.isMinimumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Minimum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else if (calculator.isMaximumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Maximum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.debug("We have a whole number");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))) + " " + continuedOperation
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//        }
//        else
//        {
//            if (calculator.isMinimumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Minimum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else if (calculator.isMaximumValue(String.valueOf(result)))
//            {
//                LOGGER.debug("Maximum value met");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                 + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)))
//                );
//                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(true);
//            }
//            else
//            {
//                LOGGER.info("We have a decimal");
//                basicHistoryTextPane.setText(
//                basicHistoryTextPane.getText() +
//                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + continuedOperation + RIGHT_PARENTHESIS.getValue()
//                + " Result: " + calculator.addCommas(calculator.getValues()[0]) + " " + ADDITION.getValue() + " " + calculator.addCommas(calculator.getValues()[1]) + " " + EQUALS.getValue() + " " + calculator.addCommas(String.valueOf(result)) + " " + continuedOperation
//                );
//                calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
//                calculator.getButtonDecimal().setEnabled(false);
//            }
//        }
//        LOGGER.info("Finished " + ADDITION);
//    }

//    /**
//     * The actions to perform when you click Negate
//     * @param actionEvent the click action
//     */
//    public void performNegateButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText() || calculator.isMaximumValue())
//        { calculator.confirm("Cannot perform " + NEGATE); }
//        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ENTER_A_NUMBER.getValue());
//            calculator.confirm("No value to negate");
//        }
//        else if (ZERO.getValue().equals(calculator.getTextPaneWithoutNewLineCharacters()))
//        {
//            calculator.confirm("Cannot negate zero");
//        }
//        else
//        {
//            if (calculator.isNumberNegative())
//            {
//                calculator.setNumberNegative(false);
//                calculator.getValues()[calculator.getValuesPosition()] = calculator.convertToPositive(calculator.getTextPaneWithoutNewLineCharacters());
//                calculator.writeHistory(buttonChoice, false);
////                basicHistoryTextPane.setText(
////                basicHistoryTextPane.getText() +
////                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////                + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
////                );
//            }
//            else
//            {
//                calculator.setNumberNegative(true);
//                calculator.getValues()[calculator.getValuesPosition()] = calculator.convertToNegative(calculator.getTextPaneWithoutNewLineCharacters());
//                calculator.writeHistory(buttonChoice, false);
////                basicHistoryTextPane.setText(
////                basicHistoryTextPane.getText() +
////                calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////                + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
////                );
//            }
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//            calculator.confirm("Pressed " + buttonChoice);
//        }
//    }

//    /**
//     * The actions to perform when the Dot button is click
//     * @param actionEvent the click action
//     */
//    public void performDecimalButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Action for {} started", buttonChoice);
//        if (calculator.textPaneContainsBadText())
//        { calculator.confirm("Cannot perform " + DECIMAL + " operation"); }
//        else
//        {
//            LOGGER.info("Basic dot operations");
//            performDecimal(buttonChoice);
//            calculator.writeHistory(buttonChoice, false);
////            basicHistoryTextPane.setText(
////            basicHistoryTextPane.getText() +
////            calculator.addNewLineCharacters() + LEFT_PARENTHESIS.getValue() + buttonChoice + RIGHT_PARENTHESIS.getValue()
////            + " Result: " + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()])
////            );
//            calculator.confirm("Pressed " + buttonChoice);
//        }
//    }
//    /**
//     * The inner logic of performing Dot actions
//     * @param buttonChoice the button choice
//     */
//    private void performDecimal(String buttonChoice)
//    {
//        if (calculator.getValues()[calculator.getValuesPosition()].isBlank() && !calculator.isNegating())
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = ZERO.getValue() + DECIMAL.getValue();
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
//        }
//        else if (calculator.getValues()[calculator.getValuesPosition()].isBlank() && calculator.isNegating())
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = SUBTRACTION.getValue() + ZERO.getValue() + DECIMAL.getValue();
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
//        }
//        else
//        {
//            calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()] + buttonChoice;
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
//        }
//        calculator.getButtonDecimal().setEnabled(false); // deactivate button now that its active for this number
//    }

//    /**
//     * The actions to perform when the Equals button is clicked
//     * @param actionEvent the click action
//     */
//    public void performEqualsButtonAction(ActionEvent actionEvent)
//    {
//        String buttonChoice = actionEvent.getActionCommand();
//        LOGGER.info("Starting {} button actions", buttonChoice);
//        String operator = calculator.determineIfBasicPanelOperatorWasPushed();
//        if (calculator.isAdding() && calculator.getValues()[1].isBlank())
//        {
//            LOGGER.warn("Attempted to perform {} but values[1] is blank", ADDITION);
//            calculator.confirm("Not performing " + buttonChoice);
//            return;
//        }
//        else if (calculator.isSubtracting() && calculator.getValues()[1].isBlank())
//        {
//            LOGGER.warn("Attempted to perform {} but values[1] is blank", SUBTRACTION);
//            calculator.confirm("Not performing " + buttonChoice);
//            return;
//        }
//        else if (calculator.isMultiplying() && calculator.getValues()[1].isBlank())
//        {
//            LOGGER.warn("Attempted to perform {} but values[1] is blank", MULTIPLICATION);
//            calculator.confirm("Not performing " + buttonChoice);
//            return;
//        }
//        else if (calculator.isDividing() && calculator.getValues()[1].isBlank())
//        {
//            LOGGER.warn("Attempted to perform {} but values[1] is blank", DIVISION);
//            calculator.confirm("Not performing " + buttonChoice);
//            return;
//        }
//        determineAndPerformBasicCalculatorOperation();
//        if (!operator.isEmpty() && !calculator.textPaneContainsBadText())
//        { calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCommas(calculator.getValues()[0])); }
//        calculator.getValues()[0] = BLANK.getValue();
//        calculator.getValues()[1] = BLANK.getValue();
//        calculator.setNegating(false);
//        calculator.setFirstNumber(false);
//        calculator.setValuesPosition(0);
//        calculator.confirm("Pushed " + buttonChoice);
//    }

//    /**
//     * This method returns the String operator that was activated
//     * Results could be: '+', '-', '*', '/' or '' if no
//     * operator was recorded as being activated
//     * @return String the basic operation that was pushed
//     */
//    public String determineIfBasicPanelOperatorWasPushed()
//    {
//        String results = "";
//        if (calculator.isAdding()) { results = ADDITION.getValue(); }
//        else if (calculator.isSubtracting()) { results = SUBTRACTION.getValue(); }
//        else if (calculator.isMultiplying()) { results = MULTIPLICATION.getValue(); }
//        else if (calculator.isDividing()) { results = DIVISION.getValue(); }
//        LOGGER.info("operator: " + (results.isEmpty() ? "no basic operator pushed" : results));
//        return results;
//    }

//    /**
//     * This method determines which basic operation to perform,
//     * performs that operation, and resets the appropriate boolean
//     */
//    public void determineAndPerformBasicCalculatorOperation()
//    {
//        if (calculator.isMaximumValue() && (calculator.isAdding() || calculator.isMultiplying()) )
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ERR.getValue());
//            calculator.getValues()[0] = BLANK.getValue();
//            calculator.getValues()[1] = BLANK.getValue();
//            calculator.setNumberNegative(false);
//            calculator.resetBasicOperators(false);
//            calculator.confirm("Maximum value met");
//        }
//        else if (calculator.isMinimumValue() && (calculator.isSubtracting() || calculator.isDividing()))
//        {
//            calculator.getTextPane().setText(calculator.addNewLineCharacters() + ZERO.getValue());
//            calculator.getValues()[0] = BLANK.getValue();
//            calculator.getValues()[1] = BLANK.getValue();
//            calculator.setNumberNegative(false);
//            calculator.resetBasicOperators(false);
//            calculator.confirm("Minimum value met");
//        }
//        else
//        {
//            if (calculator.isAdding())
//            {
//                calculator.addition();
//                calculator.setAdding(calculator.resetCalculatorOperations(calculator.isAdding()));
//            }
//            else if (calculator.isSubtracting())
//            {
//                calculator.subtract();
//                calculator.setSubtracting(calculator.resetCalculatorOperations(calculator.isSubtracting()));
//            }
//            else if (calculator.isMultiplying())
//            {
//                calculator.multiply();
//                calculator.setMultiplying(calculator.resetCalculatorOperations(calculator.isMultiplying()));
//            }
//            else if (calculator.isDividing())
//            {
//                calculator.divide();
//                calculator.setDividing(calculator.resetCalculatorOperations(calculator.isDividing()));
//            }
//        }
//    }

    /* Getters */
    public JTextPane getBasicHistoryTextPane() { return basicHistoryTextPane; }

    /* Setters */
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
//        this.basicLayout = panelLayout;
    }
    public void setBasicHistoryTextPane(JTextPane basicHistoryTextPane) { this.basicHistoryTextPane = basicHistoryTextPane; }

}