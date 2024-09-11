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

import static Types.CalculatorView.*;
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
        setName(VIEW_BASIC.getValue());
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
        setName(VIEW_BASIC.getValue());
        //SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up {} panel", VIEW_BASIC.getValue());
    }

    /**
     * Clears button actions, sets the CalculatorView,
     * CalculatorBase, ConverterType, and finally
     * sets up the basic panel and its components
     */
    private void setupBasicPanelComponents()
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
        LOGGER.debug("Actions removed...");
        calculator.setupNumberButtons();
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupMemoryButtons();
        calculator.setupBasicPanelButtons();
        setupBasicHistoryZone();
        LOGGER.debug("Finished configuring the buttons");
    }

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
                
                History: %s/%s
                Pressing this button will display the history panel. The icon will display an arrow pointing up when the history panel is open.
                Pressing the button again will change the icon back to a downward pointing arrow and hide the history panel.
                The history panel shows all button actions which are correctly performed. For example, if you press the %s button (%s), with
                no value in the text pane, history will not be recorded since the operation was not performed.
                
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
                .formatted(VIEW_BASIC.getValue(),
                        ADDITION.getValue(), SUBTRACTION.getValue(), MULTIPLICATION.getValue(), DIVISION.getValue(), EQUALS.getValue(),
                        PERCENT.getValue(), SQUARE_ROOT.getValue(),SQUARED.getValue(), FRACTION.getValue(), ENTER_A_NUMBER.getValue(),
                        MEMORY_STORE.getValue(), calculator.getMemoryValues().length, // MemoryStore,
                        MEMORY_CLEAR.getValue(),
                        MEMORY_RECALL.getValue(),
                        MEMORY_ADDITION.getValue(),
                        MEMORY_SUBTRACTION.getValue(),
                        HISTORY_CLOSED.getValue(), HISTORY_OPEN.getValue(), PERCENT.name(), PERCENT.getValue(), // H^, Hv, PERCENT, Ex:Percent
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
        JOptionPane.showMessageDialog(calculator, scrollPane, "Viewing " + VIEW_BASIC.getValue() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        calculator.confirm("Viewing " + VIEW_BASIC.getValue() + " Calculator Help");
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

    /** Primarily used to add the buttons to a panel */
    private void addComponent(JPanel panel, Component c, int row, int column)
    { addComponent(panel, c, row, column, null, 1, 1, 1.0, 1.0, 0, 0); }

    /** Primarily used to add the basicPanel to the frame */
    private void addComponent(JPanel panel)
    { addComponent(panel, null, 0, 0, new Insets(0,0,0,0), 0, 0, 1.0, 1.0, 0, GridBagConstraints.NORTH); }

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

    /**
     * Add text to the text pane when using the basic panel
     * @param text the text to add
     */
    public void appendToPane(String text)
    { calculator.getTextPane().setText(calculator.addNewLines(1) + text); }

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
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
            calculator.confirm("Pressed: " + buttonChoice);
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneValue());
            result /= 100;
            LOGGER.debug("result: " + result);
            calculator.getValues()[calculator.getValuesPosition()] = Double.toString(result);
            calculator.getTextPane().setText(calculator.addNewLines() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.writeHistory(buttonChoice, false);
            LOGGER.debug("values[{}] is {}", calculator.getValuesPosition(), calculator.getValues()[calculator.getValuesPosition()]);
            LOGGER.debug("textPane: {}", calculator.getTextPane().getText());
            calculator.getButtonDecimal().setEnabled(false);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

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
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
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
            calculator.setIsNumberNegative(String.valueOf(result).contains(SUBTRACTION.getValue()));
            calculator.getTextPane().setText(calculator.addNewLines() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
            //calculator.setIsNumberNegative(false);
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
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
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
            calculator.confirm("Cannot perform " + FRACTION + " operation");
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneValue());
            result = 1 / result;
            LOGGER.debug("result: " + result);
            if (INFINITY.getValue().equals(String.valueOf(result)))
            {
                calculator.getButtonDecimal().setEnabled(true);
                calculator.getTextPane().setText(calculator.addNewLines() + INFINITY.getValue());
                calculator.getValues()[calculator.getValuesPosition()] = BLANK.getValue();
            }
            else if (result % 1 == 0)
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
                calculator.getTextPane().setText(calculator.addNewLines() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
                calculator.getButtonDecimal().setEnabled(true);
            }
            else
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.formatNumber(String.valueOf(result));
                calculator.getTextPane().setText(calculator.addNewLines() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
                calculator.getButtonDecimal().setEnabled(false);
            }
            calculator.setIsNumberNegative(false);
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimal(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /* Getters */
    public JTextPane getHistoryTextPane() { return basicHistoryTextPane; }

    /* Setters */
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
//        this.basicLayout = panelLayout;
    }
    public void setBasicHistoryTextPane(JTextPane basicHistoryTextPane) { this.basicHistoryTextPane = basicHistoryTextPane; }

}