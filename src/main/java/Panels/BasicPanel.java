package Panels;

import Calculators.Calculator;
import Types.CalculatorUtility;
import Types.Texts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.MathContext;

import static Types.CalculatorUtility.*;
import static Types.CalculatorView.*;
import static Types.Texts.*;
import static Utilities.LoggingUtil.*;

/**
 * BasicPanel
 * <p>
 * This class contains components and actions
 * for the BasicPanel of the Calculator. Most
 * actions are defined in {@link Calculator}.
 * We construct the BasicPanel like so:
 * This class acts as a container that displays
 * the buttons. The memory buttons are placed
 * on a memory panel. The buttons are placed
 * on a buttons panel. The history is placed
 * ona history panel. This allows for panels
 * to be easily swapped out on the container.
 * This setup makes it easy to switch between
 * displaying the buttons and the history text.
 * Any common actions are defined in the
 * Calculator class.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class BasicPanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class.getSimpleName());
    private Calculator calculator;
    private GridBagConstraints constraints;
    private JPanel basicPanel;
    private boolean isInitialized;

    /**************** CONSTRUCTORS ****************/
    /**
     * A zero argument constructor for creating a BasicPanel
     */
    public BasicPanel()
    {
        setName(VIEW_BASIC.getValue());
        setConstraints(new GridBagConstraints());
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

    /**************** START OF METHODS ****************/
    /**
     * The main method used to define the BasicPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     */
    public void setupBasicPanel(Calculator calculator)
    {
        if (!isInitialized)
        {
            setCalculator(calculator);
            setBasicPanel(new JPanel(new GridBagLayout()));
            basicPanel.setName("BasicPanel");
            setLayout(new GridBagLayout());
            setConstraints(new GridBagConstraints());
            setSize(new Dimension(200, 336)); // sets main size
            setMinimumSize(new Dimension(200, 336)); // sets minimum size
        }
        setupBasicPanelComponents();
        addComponentsToPanel();
        setName(VIEW_BASIC.getValue());
        isInitialized = true;
        LOGGER.info("Finished setting up {} panel", VIEW_BASIC.getValue());
    }

    /**
     * Sets up the basic panel components
     */
    private void setupBasicPanelComponents()
    {
        calculator.clearButtonActions();
        calculator.setupTextPane();
        calculator.setupBasicPanelButtons();
        setupHelpString();
        LOGGER.debug("Finished configuring the buttons");
    }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpString()
    {
        LOGGER.debug("Setting up the help string for basic panel");
        calculator.setHelpString("""
                How to use the %s Calculator
                
                Using the basic operators:
                Step 1. Enter the first number.
                Step 2. Press a basic operator (%s, %s, %s, %s). The operator will be visible alongside the first number.
                Step 3. Enter the second number.
                Step 4. Push the Equals (%s) button to see the result.
                
                Using the function operators:
                Step 1. Enter the first number.
                Step 2. Press a function operator (%s, %s, %s, %s, %s). The operator will not be visible alongside the first number.
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
                        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS, // Basic Operators
                        PERCENT, SQUARE_ROOT,SQUARED, FRACTION, NEGATE,          // Function Operators
                        ENTER_A_NUMBER,
                        MEMORY_STORE, calculator.getMemoryValues().length, // MemoryStore,
                        MEMORY_CLEAR,
                        MEMORY_RECALL,
                        MEMORY_ADD,
                        MEMORY_SUBTRACT,
                        HISTORY_CLOSED, HISTORY_OPEN, PERCENT, PERCENT, // H^, Hv, PERCENT, Ex:Percent
                        PERCENT, ENTER_A_NUMBER, PERCENT, // Percent
                        SQUARE_ROOT, NOT_A_NUMBER, Texts.ERROR,
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
    private void addComponentsToPanel()
    {
        calculator.addComponent(this, constraints, basicPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1), 5, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryStore(),0, 0, new Insets(0,1,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryRecall(),0, 1, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryClear(),0, 2, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemoryAddition(),0, 3, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonMemorySubtraction(),0, 4, new Insets(0,0,0,0),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0); //right:1
        calculator.addComponent(this, constraints, calculator.getMemoryPanel(), calculator.getButtonHistory(),0, 5, new Insets(0, 0, 0, 1),1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, 0);

        calculator.addComponent(this, constraints, basicPanel, calculator.getMemoryPanel(), 1, 0, new Insets(0,0,0,0),1, 1, 0, 1.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);

        //constraints.fill = GridBagConstraints.BOTH;
        calculator.getButtonsPanel().removeAll();
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonPercent(), 0, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonSquareRoot(), 0, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonSquared(), 0, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonFraction(), 0, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonClearEntry(), 1, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonClear(), 1, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDelete(), 1, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDivide(), 1, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton7(), 2, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton8(), 2, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton9(), 2, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonMultiply(), 2, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton4(), 3, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton5(), 3, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton6(), 3, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonSubtract(), 3, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton1(), 4, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton2(),4, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton3(), 4, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonAdd(),4, 3);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonNegate(), 5, 0);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButton0(), 5, 1);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonDecimal(), 5, 2);
        calculator.addComponent(this, constraints, calculator.getButtonsPanel(), calculator.getButtonEquals(), 5, 3);
        //constraints.fill = 0;

        calculator.addComponent(this, constraints, basicPanel, calculator.getButtonsPanel(), 2, 0);

        calculator.addComponent(this, constraints, basicPanel);
        LOGGER.debug("Buttons added to the frame");
    }

    /**************** ACTIONS ****************/
    /**
     * The actions to perform when the Percent button is clicked
     * @param actionEvent the click action
     */
    public void performPercentButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, cannotPerformOperation(PERCENT)); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            logEmptyValue(PERCENT, calculator, LOGGER);
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(PERCENT));
        }
        else
        {
            String value = calculator.getAppropriateValue();
            boolean endsWithBinaryOperator = calculator.getBasicPanelBinaryOperators().contains(calculator.getActiveOperator()) && calculator.getTextPaneValue().endsWith(calculator.getActiveOperator());
            if (!value.isEmpty() && !endsWithBinaryOperator)
            {
                if (calculator.getValueAt().isEmpty()) logUseTextPaneValueWarning(calculator, LOGGER, buttonChoice);
                String currentOperator = calculator.getActiveOperator();
                calculator.setActiveOperator(buttonChoice);
                calculator.performOperation();
                calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()), true);
                calculator.setNegativeNumber(CalculatorUtility.isNegativeNumber(calculator.getValueAt(3)));
                calculator.getButtonDecimal().setEnabled(!isFractionalNumber(calculator.getValueAt(3)));
                calculator.writeHistory(buttonChoice, false);
                if (calculator.getBasicPanelUnaryOperators().contains(buttonChoice) && currentOperator.isEmpty())
                {
                    calculator.setActiveOperator(EMPTY);
                }
                else
                {
                    calculator.setActiveOperator(currentOperator);
                }
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            // Needs to be here!
            else if (calculator.isOperatorActive() && calculator.endsWithOperator(calculator.getTextPaneValue()))
            { confirm(calculator, LOGGER, cannotPerformOperation(PERCENT)); }
        }
    }
    /**
     * The inner logic for performing the Percent operation
     * @return the result of the Percent operation
     */
    public String performPercent()
    {
        String result = EMPTY;
        //BigDecimal currentNumber = new BigDecimal(calculator.getValueAt());
        String currentNum = calculator.getAppropriateValue();
        BigDecimal currentNumber = new BigDecimal(currentNum);
        BigDecimal oneHundred = new BigDecimal(ONE+ZERO+ZERO);
        result = currentNumber.divide(oneHundred, MathContext.DECIMAL128).toPlainString();
        if (isFractionalNumber(result))
        {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when the Squared button is clicked
     * @param actionEvent the click action
     */
    public void performSquaredButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, cannotPerformOperation(SQUARED)); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            logEmptyValue(SQUARED, calculator, LOGGER);
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(SQUARED));
        }
        else
        {
            String value = calculator.getAppropriateValue();
            boolean endsWithBinaryOperator = calculator.getBasicPanelBinaryOperators().contains(calculator.getActiveOperator()) && calculator.getTextPaneValue().endsWith(calculator.getActiveOperator());
            if (!value.isEmpty() && !endsWithBinaryOperator)
            {
                if (calculator.getValueAt().isEmpty()) logUseTextPaneValueWarning(calculator, LOGGER, buttonChoice);
                String currentOperator = calculator.getActiveOperator();
                calculator.setActiveOperator(buttonChoice);
                calculator.performOperation();
                calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()), true);
                calculator.setNegativeNumber(CalculatorUtility.isNegativeNumber(calculator.getValueAt(3)));
                calculator.getButtonDecimal().setEnabled(!isFractionalNumber(calculator.getValueAt(3)));
                calculator.writeHistory(buttonChoice, false);
                if (calculator.getBasicPanelUnaryOperators().contains(buttonChoice) && currentOperator.isEmpty())
                {
                    calculator.setActiveOperator(EMPTY);
                }
                else
                {
                    calculator.setActiveOperator(currentOperator);
                }
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            // Needs to be here!
            else if (calculator.isOperatorActive() && calculator.endsWithOperator(calculator.getTextPaneValue()))
            { confirm(calculator, LOGGER, cannotPerformOperation(FRACTION)); }
        }
    }
    /**
     * The inner logic for performing the Squared operation
     * @return the result of the Squared operation
     */
    public String performSquared()
    {
        String currentNum = calculator.getAppropriateValue();
        BigDecimal currentNumber = new BigDecimal(currentNum);
        double number = currentNumber.doubleValue();
        String result = BigDecimal.valueOf(Math.pow(number, 2)).toPlainString();
        if (isFractionalNumber(result))
        {
            result = new BigDecimal(result).stripTrailingZeros().toPlainString();
        }
        return result;
    }

    /**
     * The actions to perform when the Fraction button is clicked
     * @param actionEvent the click action
     */
    public void performFractionButtonAction(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        if (calculator.textPaneContainsBadText())
        { confirm(calculator, LOGGER, cannotPerformOperation(FRACTION)); }
        else if (calculator.getTextPaneValue().isEmpty())
        {
            logEmptyValue(FRACTION, calculator, LOGGER);
            calculator.appendTextToPane(ENTER_A_NUMBER);
            confirm(calculator, LOGGER, cannotPerformOperation(FRACTION));
        }
        else
        {
            String value = calculator.getAppropriateValue();
            boolean endsWithBinaryOperator = calculator.getBasicPanelBinaryOperators().contains(calculator.getActiveOperator()) && calculator.getTextPaneValue().endsWith(calculator.getActiveOperator());
            if (!value.isEmpty() && !endsWithBinaryOperator)
            {
                if (calculator.getValueAt().isEmpty()) logUseTextPaneValueWarning(calculator, LOGGER, buttonChoice);
                calculator.getValues()[calculator.getValuesPosition()] = value;
                String currentOperator = calculator.getActiveOperator();
                calculator.setActiveOperator(buttonChoice);
                calculator.performOperation();
                boolean updateValue = !calculator.textPaneContainsBadText();
                calculator.appendTextToPane(addThousandsDelimiter(calculator.getValueAt(3), calculator.getThousandsDelimiter()), updateValue);
                calculator.setNegativeNumber(CalculatorUtility.isNegativeNumber(calculator.getValueAt(3)));
                calculator.getButtonDecimal().setEnabled(!isFractionalNumber(calculator.getValueAt(3)));
                calculator.writeHistory(buttonChoice, false);
                if (calculator.getBasicPanelUnaryOperators().contains(buttonChoice) && currentOperator.isEmpty())
                {
                    calculator.setActiveOperator(EMPTY);
                }
                else
                {
                    calculator.setActiveOperator(currentOperator);
                }
                confirm(calculator, LOGGER, pressedButton(buttonChoice));
            }
            // Needs to be here!
            else if (calculator.isOperatorActive() && calculator.endsWithOperator(calculator.getTextPaneValue()))
            { confirm(calculator, LOGGER, cannotPerformOperation(FRACTION)); }
        }
    }
    /**
     * The inner logic for performing the Fraction operation
     * @return the result of the Fraction operation
     */
    public String performFraction()
    {
        String result;
        BigDecimal one = new BigDecimal(ONE);
        String currentNum = calculator.getAppropriateValue();
        BigDecimal currentNumber = new BigDecimal(currentNum);
        if (BigDecimal.ZERO.equals(currentNumber))
        {
            calculator.appendTextToPane(INFINITY);
            calculator.setObtainingFirstNumber(true);
            result = INFINITY;
        }
        else
        {
            result = one.divide(currentNumber, MathContext.DECIMAL128).toPlainString();
        }
        return result;
    }

    /**************** GETTERS ****************/
    public Calculator getCalculator() { return calculator; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JPanel getBasicPanel() { return basicPanel; }
    public boolean isInitialized() { return isInitialized; }

    /**************** SETTERS ****************/
    public void setCalculator(Calculator calculator) { this.calculator = calculator; LOGGER.debug("Calculator set"); }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; LOGGER.debug("Constraints set"); }
    private void setBasicPanel(JPanel basicPanel) { this.basicPanel = basicPanel; LOGGER.debug("Basic Panel set"); }
}