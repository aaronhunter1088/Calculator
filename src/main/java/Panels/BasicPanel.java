package Panels;

import Calculators.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static Types.CalculatorBase.BASE_DECIMAL;
import static Types.CalculatorView.*;
import static Types.Texts.*;
import static Utilities.LoggingUtil.confirm;

public class BasicPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private Calculator calculator;
    private GridBagConstraints constraints;
    private JPanel basicPanel,
                   memoryPanel,
                   buttonsPanel,
                   historyPanel;
    private JTextPane historyTextPane;
    private boolean isInitialized;

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
        if (!isInitialized)
        {
            this.calculator = calculator;
            this.basicPanel = new JPanel(new GridBagLayout());
            this.basicPanel.setName("BasicPanel");
            this.memoryPanel = new JPanel(new GridBagLayout());
            this.memoryPanel.setName("MemoryPanel");
            this.buttonsPanel = new JPanel(new GridBagLayout());
            this.buttonsPanel.setName("ButtonsPanel");
            this.historyPanel = new JPanel(new GridBagLayout());
            this.historyPanel.setName("HistoryPanel");
            setLayout(new GridBagLayout());
            constraints = new GridBagConstraints();
            if (calculator.getCalculatorBase() == null) { calculator.setCalculatorBase(BASE_DECIMAL); }
            setSize(new Dimension(200, 336)); // sets main size
            setMinimumSize(new Dimension(200, 336)); // sets minimum size
            setupBasicHistoryZone();
        }
        setupBasicPanelComponents();
        addComponentsToPanel();
        setName(VIEW_BASIC.getValue());
        isInitialized = true;
        LOGGER.info("Finished setting up {} panel", VIEW_BASIC.getValue());
    }

    /**
     * Clears button actions, sets the CalculatorView,
     * CalculatorBase, CalculatorConverterType, and finally
     * sets up the basic panel and its components
     */
    private void setupBasicPanelComponents()
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getCommonButtons(),
                        calculator.getNumberButtons(),
                        calculator.getAllMemoryPanelButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .toList(); // Return as a single list
        allButtons
            .forEach(button -> Stream.of(button.getActionListeners())
                .forEach(al -> {
                    LOGGER.debug("Removing action listener from button: " + button.getName());
                    button.removeActionListener(al);
                }));
        LOGGER.debug("Actions removed...");
        calculator.setupNumberButtons();
        setupHelpString();
        calculator.setupTextPane();
        calculator.setupMemoryButtons();
        calculator.setupCommonButtons();
        LOGGER.debug("Finished configuring the buttons");
    }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpString()
    {
        LOGGER.info("Setting up the help string for basic panel");
        calculator.setHelpString("""
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
                        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS,
                        PERCENT, SQUARE_ROOT,SQUARED, FRACTION, ENTER_A_NUMBER,
                        MEMORY_STORE, calculator.getMemoryValues().length, // MemoryStore,
                        MEMORY_CLEAR,
                        MEMORY_RECALL,
                        MEMORY_ADDITION,
                        MEMORY_SUBTRACTION,
                        HISTORY_CLOSED, HISTORY_OPEN, PERCENT, PERCENT, // H^, Hv, PERCENT, Ex:Percent
                        PERCENT, ENTER_A_NUMBER, PERCENT, // Percent
                        SQUARE_ROOT, NOT_A_NUMBER, ERR,
                        SQUARED, ENTER_A_NUMBER,
                        FRACTION, ENTER_A_NUMBER, FRACTION,
                        CLEAR_ENTRY,
                        CLEAR,
                        DELETE,
                        DIVISION, DIVISION, ENTER_A_NUMBER,
                        ZERO, ONE, TWO, THREE, FOUR, FIVE,
                        SIX, SEVEN, EIGHT, NINE,
                        MULTIPLICATION, MULTIPLICATION, ENTER_A_NUMBER,
                        SUBTRACTION, SUBTRACTION,
                        ADDITION, ADDITION, ENTER_A_NUMBER,
                        NEGATE,
                        DECIMAL, DECIMAL, DECIMAL, DECIMAL,
                        EQUALS));
        calculator.updateShowHelp();
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    public void addComponentsToPanel()
    {
        calculator.addComponent(this, constraints, basicPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryStore(),0, 0, new Insets(0,1,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryRecall(),0, 1, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryClear(),0, 2, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemoryAddition(),0, 3, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonMemorySubtraction(),0, 4, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0); //right:1
        calculator.addComponent(this, constraints, memoryPanel, calculator.getButtonHistory(),0, 5, new Insets(0, 0, 0, 1),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, basicPanel, memoryPanel, 1, 0, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);

        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonPercent(), 0, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonSquareRoot(), 0, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonSquared(), 0, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonFraction(), 0, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonClearEntry(), 1, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonClear(), 1, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDelete(), 1, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDivide(), 1, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton7(), 2, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton8(), 2, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton9(), 2, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonMultiply(), 2, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton4(), 3, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton5(), 3, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton6(), 3, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonSubtract(), 3, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton1(), 4, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton2(),4, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton3(), 4, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonAdd(),4, 3);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonNegate(), 5, 0);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButton0(), 5, 1);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonDecimal(), 5, 2);
        calculator.addComponent(this, constraints, buttonsPanel, calculator.getButtonEquals(), 5, 3);

        calculator.addComponent(this, constraints, basicPanel, buttonsPanel, 2, 0);

        calculator.addComponent(this, constraints, basicPanel);
        LOGGER.debug("Buttons added to the frame");
    }

    /**
     * Displays the history for the BasicPanel
     * during each active instance
     */
    public void setupBasicHistoryZone()
    {
        LOGGER.debug("Configuring BasicHistoryZone...");
        constraints.anchor = GridBagConstraints.WEST;
        calculator.addComponent(this, constraints, historyPanel, new JLabel(HISTORY), 0, 0); // space before with jtextarea

        calculator.setupHistoryTextPane();
        JScrollPane scrollPane = new JScrollPane(historyTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(historyTextPane.getSize());

        calculator.addComponent(this, constraints, historyPanel, scrollPane, 1, 0, new Insets(0,0,0,0),
                1, 6, 0, 0, GridBagConstraints.BOTH, 0);
        LOGGER.debug("BasicHistoryZone configured");
    }

    /**
     * Add text to the text pane when using the basic panel
     * @param text the text to add
     */
    public void appendTextToBasicPane(String text)
    { calculator.getTextPane().setText(calculator.addNewLines(1) + text); }

    /**************** ACTIONS ****************/

    /**
     * The actions to perform when History is clicked
     * @param actionEvent the click action
     */
    public void performHistoryAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Action for {} started", buttonChoice);
        if (HISTORY_OPEN.equals(calculator.getButtonHistory().getText()))
        {
            LOGGER.debug("Closing History");
            calculator.getButtonHistory().setText(HISTORY_CLOSED);
            basicPanel.remove(historyPanel);
            calculator.addComponent(this, constraints, basicPanel, buttonsPanel, 2, 0);
        }
        else
        {
            LOGGER.debug("Opening history");
            calculator.getButtonHistory().setText(HISTORY_OPEN);
            basicPanel.remove(buttonsPanel);
            //var currentHistory = basicHistoryTextPane.getText();
            //setupBasicHistoryZone();
            //basicHistoryTextPane.setText(currentHistory);
            calculator.addComponent(this, constraints, basicPanel, historyPanel, 2, 0);
        }
        SwingUtilities.updateComponentTreeUI(this);
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
        { confirm(calculator, LOGGER, "Cannot perform " + PERCENT + " operation"); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Pressed: " + buttonChoice);
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
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
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
        { confirm(calculator, LOGGER, "Cannot perform " + SQUARED + " operation"); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "No number to square");
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
            calculator.setIsNumberNegative(String.valueOf(result).contains(SUBTRACTION));
            calculator.getTextPane().setText(calculator.addNewLines() + calculator.addCommas(calculator.getValues()[calculator.getValuesPosition()]));
            //calculator.setIsNumberNegative(false);
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimalNumber(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
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
        { confirm(calculator, LOGGER, "Cannot perform " + FRACTION + " operation"); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
            confirm(calculator, LOGGER, "Cannot perform " + FRACTION + " operation");
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneValue());
            result = 1 / result;
            LOGGER.debug("result: " + result);
            if (INFINITY.equals(String.valueOf(result)))
            {
                calculator.getButtonDecimal().setEnabled(true);
                calculator.getTextPane().setText(calculator.addNewLines() + INFINITY);
                calculator.getValues()[calculator.getValuesPosition()] = BLANK;
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
            calculator.getButtonDecimal().setEnabled(!calculator.isDecimalNumber(calculator.getValues()[0]));
            calculator.writeHistory(buttonChoice, false);
            confirm(calculator, LOGGER, "Pressed " + buttonChoice);
        }
    }

    /**************** GETTERS ****************/
    public JPanel getHistoryPanel() { return historyPanel;}
    public JTextPane getHistoryTextPane() { return historyTextPane; }
    public boolean isInitialized() { return isInitialized; }

    /**************** SETTERS ****************/
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
    }
    public void setHistoryTextPane(JTextPane historyTextPane) { this.historyTextPane = historyTextPane; }

}