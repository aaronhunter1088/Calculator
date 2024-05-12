package Panels;

import Calculators.Calculator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.border.LineBorder;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static Calculators.Calculator.mainFont;
import static Types.CalculatorBase.*;
import static Types.CalculatorType.*;

public class BasicPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private static Calculator calculator;

//    private GridBagLayout basicLayout;
    private GridBagConstraints constraints;

    /************* Constructors ******************/

    /**
     * A zero argument constructor for creating a BasicPanel
     */
    public BasicPanel()
    {
        setName(BASIC.getName());
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

    /************* Start of methods here ******************/

    /**
     * The main method used to define the BasicPanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     */
    public void setupBasicPanel(Calculator calculator)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        setSize(new Dimension(200, 336));
        setupBasicPanelComponents();
        remove(calculator.getCurrentPanel());
        addComponentsToPanel();
        setName(BASIC.getName());
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up basic panel");
    }

    /**
     * Clears button actions, sets the CalculatorType,
     * CalculatorBase, ConverterType, and finally
     * sets up the basic panel and its components
     */
    private void setupBasicPanelComponents()
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllOtherBasicCalculatorButtons(),
                        calculator.getAllBasicPanelOperatorButtons(),
                        calculator.getAllNumberButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .toList();

        allButtons.forEach(button -> Stream.of(button.getActionListeners())
                .forEach(button::removeActionListener));
        LOGGER.debug("Actions removed...");

        calculator.setCalculatorBase(DECIMAL);
        calculator.setCalculatorType(BASIC);
        calculator.setConverterType(null);
        setupHelpMenu();
        calculator.setupTextPane();
        calculator.setupMemoryButtons(); // MS, MC, MR, M+, M-
        calculator.setupPercentButton();
        calculator.setupSquareRootButton();
        calculator.setupSquaredButton();
        calculator.setupFractionButton();
        calculator.setupClearEntryButton();
        calculator.setupClearButton();
        calculator.setupDeleteButton();
        calculator.setupDivideButton();
        calculator.setupNumberButtons();
        calculator.setupMultiplyButton();
        calculator.setupSubtractButton();
        calculator.setupAdditionButton();
        calculator.setupNegateButton();
        calculator.setupDotButton();
        calculator.setupEqualsButton();
        LOGGER.info("Finished configuring the buttons");
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    private void addComponentsToPanel()
    {
        JPanel basicPanel = new JPanel(new GridBagLayout());

        addComponent(basicPanel, calculator.getTextPane(), 0, 0, new Insets(1,1,1,1),
                5, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.PAGE_START);

        JPanel memoryPanel = new JPanel(new GridBagLayout());
        addComponent(memoryPanel, calculator.getButtonMemoryStore(), 0, new Insets(0,1,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryRecall(), 1, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryClear(), 2, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemoryAddition(), 3, new Insets(0,0,0,0));
        addComponent(memoryPanel, calculator.getButtonMemorySubtraction(), 4, new Insets(0,0,0,1));

        addComponent(basicPanel, memoryPanel, 0, new Insets(0,0,0,0));

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        addComponent(buttonsPanel, calculator.getButtonPercent(), 0, 0);
        addComponent(buttonsPanel, calculator.getButtonSqrt(), 0, 1);
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
        addComponent(buttonsPanel, calculator.getButtonDot(), 5, 2);
        addComponent(buttonsPanel, calculator.getButtonEquals(), 5, 3);

        addComponent(basicPanel, buttonsPanel, 2, 0);

        addComponent(basicPanel);
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
                
                Numbers: %s
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
                
                Help Menu:
                Choosing the View Help will describe how to use the currently selected view.
                Choosing the About Calculator will describe the current version and licensing information about the Calculator.
                """
                .formatted(BASIC.getName(),
                        calculator.getButtonAdd().getText(), calculator.getButtonSubtract().getText(), calculator.getButtonMultiply().getText(), calculator.getButtonDivide().getText(), calculator.getButtonEquals().getText(),
                        calculator.getButtonPercent().getText(), calculator.getButtonSqrt().getText(), calculator.getButtonSquared().getText(), calculator.getButtonFraction().getText(), "Enter a Number",
                        calculator.getButtonMemoryStore().getText(), calculator.getMemoryValues().length, // MemoryStore,
                        calculator.getButtonMemoryClear().getText(),
                        calculator.getButtonMemoryRecall().getText(),
                        calculator.getButtonMemoryAddition().getText(),
                        calculator.getButtonMemorySubtraction().getText(),
                        calculator.getButtonPercent().getText(), "Enter a Number", calculator.getButtonPercent().getText(), // Percent
                        calculator.getButtonSqrt().getText(), "Not a Number", "E",
                        calculator.getButtonSquared().getText(), "Enter a Number",
                        calculator.getButtonFraction().getText(), "Enter a Number", calculator.getButtonFraction().getText(),
                        calculator.getButtonClearEntry().getText(),
                        calculator.getButtonClear().getText(),
                        calculator.getButtonDelete().getText(),
                        calculator.getButtonDivide().getText(), calculator.getButtonDivide().getText(), "Enter a Number", // Divide
                        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9",
                        calculator.getButtonMultiply().getText(), calculator.getButtonMultiply().getText(), "Enter a Number",
                        calculator.getButtonSubtract().getText(), calculator.getButtonSubtract().getText(),
                        calculator.getButtonAdd().getText(), calculator.getButtonAdd().getText(), "Enter a Number",
                        calculator.getButtonNegate().getText(),
                        calculator.getButtonDot().getText(), calculator.getButtonDot().getText(), calculator.getButtonDot().getText(), calculator.getButtonDot().getText(), // Dot
                        calculator.getButtonEquals().getText());

        JMenu helpMenuItem = calculator.getHelpMenu();
        JMenuItem viewHelp = helpMenuItem.getItem(0);
        // remove any and all other view help actions
        Arrays.stream(viewHelp.getActionListeners()).forEach(viewHelp::removeActionListener);
        viewHelp.addActionListener(action -> showHelpPanel(helpString));
        helpMenuItem.add(viewHelp, 0);
        LOGGER.info("Finished setting up the help menu");
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
        JOptionPane.showMessageDialog(calculator, scrollPane, "Viewing " + BASIC.getName() + " Calculator Help", JOptionPane.PLAIN_MESSAGE);
        calculator.confirm("Viewing " + BASIC.getName() + " Calculator Help");
    }

    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public static void performMemoryStoreActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryStoreButtonHandler started");
        LOGGER.info("button: {}", actionEvent.getActionCommand());
        if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace())) // if there is a number in the textArea
        {
            if (calculator.textPaneContainsBadText())
            { calculator.confirm("Not saving " + calculator.getTextPaneWithoutNewLineCharacters() + " in Memory"); }
            else
            {
                if (calculator.getMemoryPosition() == 10) // reset to 0
                { calculator.setMemoryPosition(0); }
                calculator.getMemoryValues()[calculator.getMemoryPosition()] = calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace();
                calculator.setMemoryPosition(calculator.getMemoryPosition() + 1);
                calculator.getButtonMemoryRecall().setEnabled(true);
                calculator.getButtonMemoryClear().setEnabled(true);
                calculator.getButtonMemoryAddition().setEnabled(true);
                calculator.getButtonMemorySubtraction().setEnabled(true);
                calculator.confirm(calculator.getMemoryValues()[calculator.getMemoryPosition()] + " is stored in memory at position: " + (calculator.getMemoryPosition()-1));
            }
        }
        else
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("No number added to memory. Blank entry");
        }
    }
    /**
     * The actions to perform when MemoryRecall is clicked
     * @param actionEvent the click action
     */
    public static void performMemoryRecallActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryRecallButtonHandler started");
        LOGGER.info("button: {}", actionEvent.getActionCommand());
        if (calculator.getMemoryRecallPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]))
        { calculator.setMemoryRecallPosition(calculator.getLowestMemoryPosition()); }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]);
        calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
        calculator.confirm("Recalling number in memory: " + calculator.getMemoryValues()[(calculator.getMemoryRecallPosition()-1)] + " at position: " + (calculator.getMemoryRecallPosition()-1));
    }
    /**
     * The actions to perform when MemoryClear is clicked
     * @param actionEvent the click action
     */
    public static void performMemoryClearActions(ActionEvent actionEvent) {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: {}", actionEvent.getActionCommand());
        if (calculator.getMemoryPosition() == 10)
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            calculator.setMemoryPosition(0);
        }
        if (!calculator.isMemoryValuesEmpty())
        {
            calculator.setMemoryPosition(calculator.getLowestMemoryPosition());
            LOGGER.info("Clearing memoryValue[" + calculator.getMemoryPosition() + "] = " + calculator.getMemoryValues()[calculator.getMemoryPosition()]);
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
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0");
                calculator.confirm("MemorySuite is blank");
                calculator.confirm("No saved values");
            }
            calculator.confirm("Reset memory at position: " + (calculator.getMemoryPosition() - 1) + ".");
        }
    }
    /**
     * The actions to perform when MemoryAdd is clicked
     * Will add the current number in the textPane to the previously
     * save value in Memory. Finally displays that result in the
     * textPane as confirmation.
     * @param actionEvent the click action
     */
    public static void performMemoryAddActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not adding."); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        { calculator.confirm("No number in textPane to add to memory"); }
        else
        {
            LOGGER.info("textArea: '" + calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+"]: '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace())
                    + Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); // create result forced double
            LOGGER.info(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace() + " + " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0)
            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); }
            calculator.getTextPane().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }
    /**
     * The actions to perform when MemorySubtraction is clicked
     * @param actionEvent the click action
     */
    public static void performMemorySubtractionActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemorySubtractButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not subtracting."); }
        else if (calculator.getTextPaneWithoutAnyOperator().isEmpty())
        { calculator.confirm("No number in textPane to subtract to memory"); }
        else
        {
            LOGGER.info("textArea: '" + calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+"]: '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)])
                    - Double.parseDouble(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace());
            LOGGER.info(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace() + " - " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0)
            { calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); }
            calculator.getTextPane().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }

    /**
     * The actions to perform when the Percent button is clicked
     * @param actionEvent the click action
     */
    public static void performPercentButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform percent operation. Number too big!"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("Pressed: " + buttonChoice);
        }
        else
        {
            if (calculator.isNegativeNumber(calculator.getTextPaneWithoutNewLineCharacters()))
            {
                LOGGER.info("number is negative for percentage");
                double percent = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
                percent /= 100;
                LOGGER.info("percent: "+percent);
                calculator.getValues()[calculator.getValuesPosition()] = Double.toString(percent);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]); // update textArea
                LOGGER.info("values["+calculator.getValuesPosition()+"] is " + calculator.getValues()[calculator.getValuesPosition()]);
                LOGGER.info("textArea: "+calculator.getTextPane().getText());
            }
            else
            {
                double percent = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
                percent /= 100;
                calculator.getValues()[calculator.getValuesPosition()] = Double.toString(percent);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.formatNumber(calculator.getValues()[calculator.getValuesPosition()]));
            }
            calculator.getButtonDot().setEnabled(false);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the SquareRoot button is clicked
     * @param actionEvent the click action
     */
    public static void performSquareRootButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        LOGGER.debug("text: " + calculator.getTextPaneWithoutNewLineCharacters());
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("TextPane says: " + calculator.getTextPaneWithoutNewLineCharacters()); }
        else
        {
            if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
                calculator.confirm("Cannot perform square root operation when textPane blank");
            }
            else if (calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "E");
                calculator.confirm("Cannot perform square root operation on negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.parseDouble(calculator.getValues()[0])));
                result = calculator.formatNumber(result);
                calculator.getButtonDot().setEnabled(false);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + result);
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
                calculator.confirm("Pressed " + buttonChoice);
            }
        }
    }

    /**
     * The actions to perform when the Squared button is clicked
     * @param actionEvent the click action
     */
    public static void performSquaredButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Squared button started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice);
        if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            LOGGER.info("Squared button finished");
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("No number to square");
        }
        else
        {
            double result = Double.parseDouble(calculator.getValues()[0]);
            result = Math.pow(result, 2);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
                calculator.getButtonDot().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal number");
                calculator.getValues()[0] = String.valueOf(result);
                calculator.getButtonDot().setEnabled(false);
            }
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCourtesyCommas(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.setNumberNegative(false);
            LOGGER.info("Squared button finished");
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Fraction button is clicked
     * @param actionEvent the click action
     */
    public static void performFractionButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform fraction operation. Number too big!"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("Pressed: " + buttonChoice);
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
            result = 1 / result;
            LOGGER.info("result: " + result);
            calculator.setValuesAtPositionThenUpdateTextPane(String.valueOf(result));
            if ("Infinity".equals(String.valueOf(result)))
            {
                calculator.getButtonDot().setEnabled(true);
                calculator.getValues()[calculator.getValuesPosition()] = "";
            }
            else
            { calculator.getButtonDot().setEnabled(false); }
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public static void performClearEntryButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice); // print out button confirmation
        calculator.getTextPane().setText("");
        if (calculator.getValuesPosition() == 0)
        {
            calculator.getValues()[0] = "";
            calculator.resetBasicOperators(false);
            calculator.setFirstNumber(true);
        }
        else
        {
            calculator.getValues()[1] = "";
            calculator.setValuesPosition(0);
        }
        calculator.getButtonDot().setEnabled(true);
        calculator.setNumberNegative(false);
        LOGGER.info("ClearEntryButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The actions to perform when the Clear button is clicked
     * @param actionEvent the action performed
     */
    public static void performClearButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice);
        // clear calculator.getValues()
        for (int i=0; i < 3; i++)
        { calculator.getValues()[i] = ""; }
        // clear memory
        for(int i=0; i < 10; i++)
        { calculator.getMemoryValues()[i] = ""; }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0");
        calculator.resetBasicOperators(false);
        calculator.setValuesPosition(0);
        calculator.setMemoryPosition(0);
        calculator.setFirstNumber(true);
        calculator.setNumberNegative(false);
        calculator.getButtonMemoryRecall().setEnabled(false);
        calculator.getButtonMemoryClear().setEnabled(false);
        calculator.getButtonMemoryAddition().setEnabled(false);
        calculator.getButtonMemorySubtraction().setEnabled(false);
        calculator.getButtonDot().setEnabled(true);
        LOGGER.info("ClearButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public static void performDeleteButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice); // print out button confirmation
        if (calculator.getValues()[1].isEmpty())
        { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete

        LOGGER.debug("calculator.getValues()["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.debug("textPane: '" + calculator.getTextPaneWithoutNewLineCharacters() + "'");
        if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                && !calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(0,calculator.getValues()[calculator.getValuesPosition()].length()-1);
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCourtesyCommas(calculator.getValues()[calculator.getValuesPosition()]));
        }
        else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
        {
            LOGGER.debug("An operator has been pushed");
            if (calculator.getValuesPosition() == 0)
            {
                if (calculator.isAdding()) calculator.setAdding(false);
                else if (calculator.isSubtracting()) calculator.setSubtracting(false);
                else if (calculator.isMultiplying()) calculator.setMultiplying(false);
                else /*if (calculator.isDividing())*/ calculator.setDividing(false);
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutAnyOperator();
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCourtesyCommas(calculator.getTextPaneWithoutAnyOperator()));
            }
            else
            {
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(0,calculator.getValues()[calculator.getValuesPosition()].length()-1);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCourtesyCommas(calculator.getValues()[calculator.getValuesPosition()]));
            }
        }
        calculator.getButtonDot().setEnabled(!calculator.isDecimal(calculator.getValues()[calculator.getValuesPosition()]));
        LOGGER.info("DeleteButtonHandler() finished");
        calculator.confirm("Pressed " + buttonChoice);
    }

    /**
     * The actions to perform when the Divide button is clicked
     * @param actionEvent the click action
     */
    public static void performDivideButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Divide Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform division."); }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                calculator.setDividing(true);
                calculator.setFirstNumber(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
            {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty() & !calculator.getValues()[1].equals("0"))
            {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.warn("The user pushed divide but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for dividing
     */
    private static void divide()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = 0.0;
        if (!calculator.getValues()[1].equals("0"))
        {
            result = Double.parseDouble(calculator.getValues()[0]) / Double.parseDouble(calculator.getValues()[1]); // create result forced double
            LOGGER.info(calculator.getValues()[0] + " ÷ " + calculator.getValues()[1] + " = " + result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textarea changed to whole number, or int
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
                calculator.getButtonDot().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal");
                calculator.getValues()[0] = String.valueOf(result);
            }
            calculator.confirm("Finished dividing");
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Infinity");
            calculator.getValues()[0] = "";
            calculator.getValues()[1] = "";
            calculator.setFirstNumber(true);
            calculator.confirm("Attempted to divide by 0. Values[0] = 0");
        }
    }

    /**
     * The actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public static void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} button{} actions...", calculator.getCurrentPanel().getName(), buttonChoice);
        if (!calculator.isFirstNumber()) // second number
        {
            LOGGER.debug("!isFirstNumber is: " + !calculator.isFirstNumber());
            calculator.getTextPane().setText("");
            calculator.setFirstNumber(true);
            if (!calculator.isNegating()) calculator.setNumberNegative(false);
            if (calculator.isDotPressed())
            {
                LOGGER.debug("dot is pressed. turning off. enabling button");
                calculator.getButtonDot().setEnabled(true);
            }
        }
        if (calculator.performInitialChecks())
        {
            LOGGER.info("Invalid entry in textPane. Must clear entry...");
            calculator.confirm("Pressed " + buttonChoice);
        }
        else if (calculator.checkValueLength() ) // TODO: && calculator.maximumValue / calculator.minimumValue
        {
            LOGGER.info("Highest size of value has been met");
            calculator.confirm("Max length of 7 digit number met");
        }
        else
        {
            LOGGER.info("No invalid entry found");
            if (!calculator.isDotPressed())
            {
                LOGGER.debug("dot button was not pushed");
                // TODO: check, update, if in case...
                //calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()]);
                calculator.updateValuesAtPositionThenUpdateTextPane(buttonChoice);
            }
            else
            {
                LOGGER.info("dot button was pushed");
                // TODO: check, update, if in case...
                //calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()]);
                performDot(buttonChoice);
            }
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public static void performMultiplicationActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Multiplication Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform multiplication. Number too big!"); }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                calculator.setMultiplying(true);
                calculator.setFirstNumber(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
            {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator())) {
                LOGGER.warn("The user pushed multiply but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for multiplying
     */
    private static void multiply()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0])
                * Double.parseDouble(calculator.getValues()[1]);
        LOGGER.info(calculator.getValues()[0] + " * " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = calculator.formatNumber(String.valueOf(result));
        }
    }

    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public static void performSubtractionButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Subtract Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform subtraction. Number too big!"); }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                calculator.setSubtracting(true);
                calculator.setFirstNumber(false);
                calculator.setNegating(false);
                calculator.setNumberNegative(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            }
            else if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
                calculator.setNegating(true);
                calculator.setNumberNegative(true);
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
            {
                addition();
                calculator.setAdding(calculator.resetOperator(calculator.isAdding()));
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.info("The user pushed subtract but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + " " + buttonChoice);
                calculator.setNegating(true);
            }
            else if ((calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                    && !calculator.isNegating())
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
                calculator.setNegating(true);
            }
            calculator.getButtonDot().setEnabled(true);
            if (!calculator.isNegating()) calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for subtracting
     */
    private static void subtract()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0])
                - Double.parseDouble(calculator.getValues()[1]);
        if (calculator.isNegating())
        {
            LOGGER.info(calculator.getValues()[0] + " + " + calculator.convertToPositive(calculator.getValues()[1]) + " = " + result);
            calculator.setNegating(false);
        }
        else
        {
            LOGGER.info(calculator.getValues()[0] + " - " + calculator.getValues()[1] + " = " + result);
        }
        calculator.getValues()[0] = Double.toString(result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = Double.toString(result);
        }
    }

    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public static void performAdditionButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform addition."); }
        else
        {
            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                calculator.setAdding(true);
                calculator.setFirstNumber(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty())
            {
                addition();  // 5 + 3 + ...
                calculator.resetOperator(calculator.isAdding());
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.setSubtracting(calculator.resetOperator(calculator.isSubtracting()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.setMultiplying(calculator.resetOperator(calculator.isMultiplying()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.setDividing(calculator.resetOperator(calculator.isDividing()));
                calculator.setAdding(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.error("The user pushed plus but there is no number");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.error("Already chose an operator. Choose another number."); }
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
    private static void addition()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) + Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " + " + calculator.getValues()[1] + " = " + result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            calculator.getButtonDot().setEnabled(false);
            calculator.getValues()[0] = String.valueOf(result);
        }
    }

    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public static void performNegateButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Number too big!"); }
        else if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("No value to negate");
        }
        else
        {
            if (calculator.isNumberNegative())
            {
                calculator.setNumberNegative(false);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()]));
            }
            else
            {
                calculator.setNumberNegative(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.convertToNegative(calculator.getValues()[calculator.getValuesPosition()]));
            }
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public static void performDotButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting Dot button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.textPaneContainsBadText()) { calculator.confirm("Cannot press dot button. Number too big!"); }
        else
        {
            LOGGER.info("Basic dot operations");
            performDot(buttonChoice);
        }
        calculator.confirm("Pressed the Dot button");
    }
    /**
     * The inner logic of performing Dot actions
     * @param buttonChoice the button choice
     */
    private static void performDot(String buttonChoice)
    {
        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]))
        {
            calculator.getValues()[calculator.getValuesPosition()] = "0.";
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0.");
        }
        else
        {
            calculator.updateValuesAtPositionThenUpdateTextPane(buttonChoice);
        }
        calculator.getButtonDot().setEnabled(false); // deactivate button now that its active for this number
    }

    /**
     * The actions to perform when the Equals button is clicked
     * @param actionEvent the click action
     */
    public static void performEqualsButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        String operator = determineIfBasicPanelOperatorWasPushed();
        determineAndPerformBasicCalculatorOperation();
        if (!operator.isEmpty() && !calculator.textPaneContainsBadText())
        { calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.addCourtesyCommas(calculator.getValues()[0])); }
        calculator.getValues()[1] = "";
        calculator.setFirstNumber(true);
        calculator.setValuesPosition(0);
        calculator.confirm("Pushed " + buttonChoice);
    }

    /**
     * This method returns the String operator that was activated
     * Results could be: '+', '-', '*', '/' or '' if no
     * operator was recorded as being activated
     * @return String the basic operation that was pushed
     */
    public static String determineIfBasicPanelOperatorWasPushed()
    {
        String results = "";
        if (calculator.isAdding()) { results = "+"; }
        else if (calculator.isSubtracting()) { results = "-"; }
        else if (calculator.isMultiplying()) { results = "✕"; }
        else if (calculator.isDividing()) { results = "÷"; }
        LOGGER.info("operator: " + (results.isEmpty() ? "no basic operator pushed" : results));
        return results;
    }

    /**
     * This method determines which basic operation to perform,
     * performs that operation, and resets the appropriate boolean
     */
    public static void determineAndPerformBasicCalculatorOperation()
    {
        if (calculator.isAdding())
        {
            addition();
            calculator.setAdding(calculator.resetOperator(calculator.isAdding()));
        }
        else if (calculator.isSubtracting())
        {
            subtract();
            calculator.setSubtracting(calculator.resetOperator(calculator.isSubtracting()));
        }
        else if (calculator.isMultiplying())
        {
            multiply();
            calculator.setMultiplying(calculator.resetOperator(calculator.isMultiplying()));
        }
        else if (calculator.isDividing())
        {
            divide();
            calculator.setDividing(calculator.resetOperator(calculator.isDividing()));
        }
    }

    /************* All Getters ******************/
    public Calculator getCalculator() { return calculator; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
//        this.basicLayout = panelLayout;
    }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
}