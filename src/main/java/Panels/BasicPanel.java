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
import java.awt.event.ActionListener;
import java.io.Serial;
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
    private Calculator calculator;

    private GridBagLayout basicLayout;
    private GridBagConstraints constraints;

    /************* Constructors ******************/

    /**
     * A zero argument constructor for creating a BasicPanel
     */
    public BasicPanel()
    { LOGGER.info("Empty Basic panel created"); }

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
    private void setupBasicPanel(Calculator calculator)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setMaximumSize(new Dimension(100,200));
        setupBasicPanelComponents();
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

        calculator.setCalculatorBase(DECIMAL);
        calculator.setCalculatorType(BASIC);
        calculator.setConverterType(null);
        setupTextPane();
        setupMemoryButtons(); // MS, MC, MR, M+, M-
        setupDeleteButton();
        setupSquaredButton();
        setupClearEntryButton();
        setupClearButton();
        setupNegateButton();
        setupSquareRootButton();
        setupNumberButtons(true);
        setupDotButton();
        setupAddButton();
        setupSubtractButton();
        setupMultiplyButton();
        setupDivideButton();
        setupPercentButton();
        setupFractionButton();
        setupEqualsButton();
        setupHelpMenu();
        LOGGER.info("Finished configuring the buttons");
    }

    /**
     * Specifies where each button is placed on the BasicPanel
     */
    private void addComponentsToPanel()
    {
        constraints.insets = new Insets(5,5,5,5); //5,0,5,0
        addComponent(calculator.getTextPane(), 0, 0, 5, 2);
        addComponent(calculator.getButtonMemoryStore(), 2, 0, 1, 1);
        addComponent(calculator.getButtonMemoryClear(), 2, 1, 1, 1);
        addComponent(calculator.getButtonMemoryRecall(), 2, 2, 1, 1);
        addComponent(calculator.getButtonMemoryAddition(), 2, 3, 1, 1);
        addComponent(calculator.getButtonMemorySubtraction(), 2, 4, 1, 1);
        addComponent(calculator.getButtonDelete(), 3, 0, 1, 1);
        addComponent(calculator.getButtonClearEntry(), 3, 1, 1, 1);
        addComponent(calculator.getButtonClear(), 3, 2, 1, 1);
        addComponent(calculator.getButtonNegate(), 3, 3, 1, 1);
        addComponent(calculator.getButtonSqrt(), 3, 4, 1, 1);
        addComponent(calculator.getButton7(), 4, 0, 1, 1);
        addComponent(calculator.getButton8(), 4, 1, 1, 1);
        addComponent(calculator.getButton9(), 4, 2, 1, 1);
        addComponent(calculator.getButtonDivide(), 4, 3, 1, 1);
        addComponent(calculator.getButtonPercent(), 4, 4, 1, 1);
        addComponent(calculator.getButton4(), 5, 0, 1, 1);
        addComponent(calculator.getButton5(), 5, 1, 1, 1);
        addComponent(calculator.getButton6(), 5, 2, 1, 1);
        addComponent(calculator.getButtonMultiply(), 5, 3, 1, 1);
        addComponent(calculator.getButtonFraction(), 5, 4, 1, 1);
        addComponent(calculator.getButton1(), 6, 0, 1, 1);
        addComponent(calculator.getButton2(), 6, 1, 1, 1);
        addComponent(calculator.getButton3(), 6, 2, 1, 1);
        addComponent(calculator.getButtonSubtract(), 6, 3, 1, 1);
        addComponent(calculator.getButtonSquared(), 6, 4, 1, 1);
        addComponent(calculator.getButton0(), 7, 0, 2, 1, GridBagConstraints.HORIZONTAL);
        addComponent(calculator.getButtonDot(), 7, 2, 1, 1);
        addComponent(calculator.getButtonAdd(), 7, 3, 1, 1);
        addComponent(calculator.getButtonEquals(), 7, 4, 1, 1);
        LOGGER.info("Buttons added to basic panel");
    }
    /**
     * Adding a component enforcing the GridBagConstraints.BOTH
     * @param c the component to add
     * @param row the row to add the component to
     * @param column the column to add the component to
     * @param width the number of columns the component takes up
     * @param height the number of rows the component takes up
     */
    private void addComponent(Component c, int row, int column, int width, int height)
    { addComponent(c, row, column, width, height, GridBagConstraints.BOTH); }
    /**
     * The main method to used to add a component to the Calculator frame
     * @param c the component to add
     * @param row the row to add the component to
     * @param column the column to add the component to
     * @param width the number of columns the component takes up
     * @param height the number of rows the component takes up
     * @param fill the constrains to use
     */
    private void addComponent(Component c, int row, int column, int width, int height, int fill)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = fill;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = 0;
        constraints.weightx = 0;
        basicLayout.setConstraints(c, constraints);
        add(c);
    }

    /**
     * Calls the main setup method when switching
     * from another panel to the BasicPanel
     * @param calculator the Calculator object
     */
//    public void performBasicCalculatorTypeSwitchOperations(Calculator calculator)
//    { setupBasicPanel(calculator); }

    /**
     * The main method to set up the Help menu item.
     * Sets the help text for the the BasicPanel
     */
    private void setupHelpMenu()
    {
        String helpString = """
                How to use the %s Calculator
                
                Step 1. Enter the first number.
                Step 2. Enter an main basic operator (%s, %s, %s, %s). The operator will be visible alongside the first number.
                Step 3. Enter the second number.
                Step 4. Push the Equals (%s) button to see the result.
                
                Described below are how each button works from the top left down.
                
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
                
                Delete: %s
                Pressing this button will remove the right most value from the textPane until the entire number is gone.
                It will only remove a single value, so 15.2 will become 15., allowing for exact replacement or deletion.
                
                ClearEntry: %s
                Pressing this button will clear the value in textPane, the stored value, and any main basic operators pushed if still obtaining the first number.
                If the second number is currently being obtained, clear the textPane and move the values position back to zero.
                
                Clear: %s
                Pressing this button will clear the value presented in the textPane, replace it with 0, and clear all values, memory values, and copied values.
                The 0 placed in the textPane is the indicator to state that everything is starting over.
                
                Negate: %s
                Pressing this button will negate the current number. If the current number is already negative, it will make it positive.
                
                SquareRoot: %s
                Pressing this button will take the current value in the textPane and return the square root of that value. It will return the result in decimal form. If there is no value available in the textPane it will return a message '%s'.
                
                Numbers: %s
                Pressing a number button will place that value in the textPane.
                
                Divide: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Percent: %s
                Pressing this button will return the value in the textPane as a percentage, or a message '%s', if no value is available in the textPane.
                Ex: 25 -> %s -> 0.25
                
                Multiply: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Fraction: %s
                Pressing this button will return the value in the textPane as a fraction, performing 1 divided by the value in the textPane. If no value is available, it will display a message '%s'.
                Ex: 4 -> %s -> 0.25.
                
                Subtract: %s
                Pressing this button will append the %s sign to the value in the the textPane, or enforce that the current number will be negative.
                
                Squared: %s
                Pressing this button will return the value in the textPane squared, or a message '%s', if no value is available in the textPane.
                
                Dot: %s
                Pressing this button will append the %s symbol to the value in the textPane. Once pressed the button will be disabled until an operator is pressed, unless the result includes the symbol. If the %s button is pressed or present in the textPane, this button will be disabled. Pressing the %s button once will append a zero before adding the dot.
                
                Addition: %s
                Pressing this button will append the %s sign to the value in the textPane, or a message '%s', if no value is available in the textPane.
                
                Equals: %s
                Pressing this button will perform the operation chosen or nothing if no main, basic, operator was pressed.
                """
                .formatted(BASIC.getName(),
                        calculator.getButtonAdd().getText(), calculator.getButtonSubtract().getText(), calculator.getButtonMultiply().getText(), calculator.getButtonDivide().getText(),
                        calculator.getButtonEquals().getText(), // Step 4
                        calculator.getButtonMemoryStore().getText(), calculator.getMemoryValues().length, // MemoryStore,
                        calculator.getButtonMemoryClear().getText(),
                        calculator.getButtonMemoryRecall().getText(),
                        calculator.getButtonMemoryAddition().getText(),
                        calculator.getButtonMemorySubtraction().getText(),
                        calculator.getButtonDelete().getText(),
                        calculator.getButtonClearEntry().getText(),
                        calculator.getButtonClear().getText(),
                        calculator.getButtonNegate().getText(),
                        calculator.getButtonSqrt().getText(), "Not a Number", // SquareRoot
                        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9",
                        calculator.getButtonDivide().getText(), calculator.getButtonDivide().getText(), "Enter a Number", // Divide
                        calculator.getButtonPercent().getText(), "Enter a Number", calculator.getButtonPercent().getText(), // Percent
                        calculator.getButtonMultiply().getText(), calculator.getButtonMultiply().getText(), "Enter a Number",
                        calculator.getButtonFraction().getText(), "Enter a Number", calculator.getButtonFraction().getText(),
                        calculator.getButtonSubtract().getText(), calculator.getButtonSubtract().getText(),
                        calculator.getButtonSquared().getText(), "Enter a Number",
                        calculator.getButtonDot().getText(), calculator.getButtonDot().getText(), calculator.getButtonDot().getName(), calculator.getButtonDot().getName(), // Dot
                        calculator.getButtonAdd().getText(), calculator.getButtonAdd().getText(), "Enter a Number",
                        calculator.getButtonEquals().getText());

        for(int i=0; i < calculator.getCalculatorMenuBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getCalculatorMenuBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Help")) {
                // get the options. remove viewHelpItem
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("View Help"))
                    {
                        LOGGER.debug("Found the current View Help option");
                        break;
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(mainFont);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JTextArea message = new JTextArea(helpString,20,40);
                    // make it look & act like a label
                    message.setWrapStyleWord(true);
                    message.setLineWrap(true);
                    message.setEditable(false);
                    message.setFocusable(false);
                    message.setOpaque(false);

                    JScrollPane scrollPane =  new JScrollPane(message,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                    );
                    scrollPane.setSize(new Dimension(400, 300));
                    SwingUtilities.updateComponentTreeUI(calculator);
                    JOptionPane.showMessageDialog(calculator,
                            scrollPane, "Viewing " + BASIC.getName() +" Calculator Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
        LOGGER.info("Finished setting up the help menu");
    }

    private void showHelpPanel(ActionEvent actionEvent)
    {


    }

    /**
     * The main method to set up the textPane
     */
    private void setupTextPane()
    {
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        calculator.setTextPane(new JTextPane());
        calculator.getTextPane().setParagraphAttributes(attribs, true);
        calculator.getTextPane().setFont(mainFont);
        if (calculator.isMotif())
        {
            calculator.getTextPane().setBackground(new Color(174,178,195));
            calculator.getTextPane().setBorder(new LineBorder(Color.GRAY, 1, true));
        }
        else
        { calculator.getTextPane().setBorder(new LineBorder(Color.BLACK)); }
        calculator.getTextPane().setEditable(false);
        calculator.getTextPane().setPreferredSize(new Dimension(70, 30));
        LOGGER.info("TextArea configured");
    }

    /**
     * The main method to set up the Memory buttons
     */
    private void setupMemoryButtons()
    {
        calculator.getAllMemoryButtons().forEach(memoryButton -> {
            memoryButton.setFont(mainFont);
            memoryButton.setPreferredSize(new Dimension(35, 35));
            memoryButton.setBorder(new LineBorder(Color.BLACK));
            memoryButton.setEnabled(false);
        });
        calculator.getButtonMemoryClear().setName("MC");
        calculator.getButtonMemoryClear().addActionListener(this::performMemoryClearActions);
        LOGGER.info("Memory Clear button configured");
        calculator.getButtonMemoryRecall().setName("MR");
        calculator.getButtonMemoryRecall().addActionListener(this::performMemoryRecallActions);
        LOGGER.info("Memory Recall button configured");
        calculator.getButtonMemoryAddition().setName("M+");
        calculator.getButtonMemoryAddition().addActionListener(this::performMemoryAddActions);
        LOGGER.info("Memory Add button configured");
        calculator.getButtonMemorySubtraction().setName("M-");
        calculator.getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionActions);
        LOGGER.info("Memory Subtract button configured");
        calculator.getButtonMemoryStore().setEnabled(true); // Enable memoryStore
        calculator.getButtonMemoryStore().setName("MS");
        calculator.getButtonMemoryStore().addActionListener(this::performMemoryStoreActions);
        LOGGER.info("Memory Store button configured");
        // reset buttons to enabled if memories are saved
        if (!calculator.getMemoryValues()[0].isEmpty())
        {
            calculator.getButtonMemoryClear().setEnabled(true);
            calculator.getButtonMemoryRecall().setEnabled(true);
            calculator.getButtonMemoryAddition().setEnabled(true);
            calculator.getButtonMemorySubtraction().setEnabled(true);
        }
    }
    /**
     * The actions to perform when MemoryStore is clicked
     * @param actionEvent the click action
     */
    public void performMemoryStoreActions(ActionEvent actionEvent)
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
        { calculator.confirm("No number added to memory. Blank entry"); }
    }
    /**
     * The actions to perform when MemoryRecall is clicked
     * @param actionEvent the click action
     */
    public void performMemoryRecallActions(ActionEvent actionEvent)
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
    public void performMemoryClearActions(ActionEvent actionEvent) {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: {}", actionEvent.getActionCommand());
        if (calculator.getMemoryPosition() == 10) {
            LOGGER.debug("Resetting memoryPosition to 0");
            calculator.setMemoryPosition(0);
        }
        if (!calculator.isMemoryValuesEmpty()) {
            calculator.setMemoryPosition(calculator.getLowestMemoryPosition());
            LOGGER.info("Clearing memoryValue[" + calculator.getMemoryPosition() + "] = " + calculator.getMemoryValues()[calculator.getMemoryPosition()]);
            calculator.getMemoryValues()[calculator.getMemoryPosition()] = "";
            calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
            // MemorySuite now could potentially be empty
            if (calculator.isMemoryValuesEmpty()) {
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
    public void performMemoryAddActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not adding."); }
        else if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
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
        else
        { calculator.confirm("No number in textPane to add to memory"); }
    }
    /**
     * The actions to perform when MemorySubtraction is clicked
     * @param actionEvent the click action
     */
    public void performMemorySubtractionActions(ActionEvent actionEvent)
    {
        LOGGER.info("MemorySubtractButtonHandler class started");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not subtracting."); }
        else if (StringUtils.isNotBlank(calculator.getTextPaneWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
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
        else
        { calculator.confirm("No number in textPane to subtract to memory"); }
    }

    /**
     * The main method to set up the Delete button
     */
    private void setupDeleteButton()
    {
        calculator.getButtonDelete().setFont(mainFont);
        calculator.getButtonDelete().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDelete().setEnabled(true);
        calculator.getButtonDelete().setName("Delete");
        calculator.getButtonDelete().addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }
    /**
     * The actions to perform when the Delete button is clicked
     * @param actionEvent the click action
     */
    public void performDeleteButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice); // print out button confirmation
        if (calculator.getValues()[1].isEmpty())
        { calculator.setValuesPosition(0); } // assume they could have pressed an operator then wish to delete

        LOGGER.info("calculator.getValues()["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.info("textPane: '" + calculator.getTextPaneWithoutNewLineCharacters() + "'");
        if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
            && !calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters().substring(0,calculator.getTextPaneWithoutNewLineCharacters().length()-1));
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
        }
        else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
        {
            if (calculator.getValuesPosition() == 0)
            {
                if (calculator.isAdding()) calculator.setAdding(false);
                if (calculator.isSubtracting()) calculator.setSubtracting(false);
                if (calculator.isMultiplying()) calculator.setMultiplying(false);
                if (calculator.isDividing()) calculator.setDividing(false);
            }
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters().substring(0,calculator.getTextPaneWithoutNewLineCharacters().length()-1).trim());
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
        }
        if (!calculator.getTextPaneWithoutNewLineCharacters().isEmpty() && !calculator.isDecimal(calculator.getValues()[calculator.getValuesPosition()]))
        {
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        calculator.confirm("Pressed " + buttonChoice);
    }

    /**
     * The main method to set up the Squared x² button
     */
    private void setupSquaredButton()
    {
        calculator.getButtonSquared().setFont(mainFont);
        calculator.getButtonSquared().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonSquared().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSquared().setEnabled(true);
        calculator.getButtonSquared().setName("Squared");
        calculator.getButtonSquared().addActionListener(this::performSquaredButtonActions);
        LOGGER.info("Delete button configured");
    }
    /**
     * The actions to perform when the Squared button is clicked
     * @param actionEvent the click action
     */
    public void performSquaredButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Squared button started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice); // print out button confirmation
        if (calculator.getTextPaneWithoutNewLineCharacters().isEmpty())
        {
            LOGGER.info("Squared button finished");
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            calculator.confirm("No number to square");
        }
        else
        {
            double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
            result = Math.pow(result, 2);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
                calculator.setDotPressed(false);
                calculator.getButtonDot().setEnabled(true);
            }
            else
            {
                LOGGER.info("We have a decimal number");
                calculator.getValues()[0] = String.valueOf(result);
                calculator.setDotPressed(true);
                calculator.getButtonDot().setEnabled(false);
            }
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
            calculator.setNumberNegative(false);
            LOGGER.info("Squared button finished");
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    /**
     * The main method to set up the ClearEntry button
     */
    private void setupClearEntryButton()
    {
        calculator.getButtonClearEntry().setFont(mainFont);
        calculator.getButtonClearEntry().setMaximumSize(new Dimension(35, 35));
        calculator.getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClearEntry().setEnabled(true);
        calculator.getButtonClearEntry().setName("ClearEntry");
        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }
    /**
     * The action to perform when the ClearEntry button is clicked
     * @param actionEvent the click action
     */
    public void performClearEntryButtonActions(ActionEvent actionEvent)
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
        calculator.setDotPressed(false);
        calculator.getButtonDot().setEnabled(true);
        calculator.setNumberNegative(false);
        LOGGER.info("ClearEntryButtonHandler() finished");
        calculator.confirm("Pressed: " + buttonChoice);
    }

    /**
     * The main method to set up the Clear button
     */
    private void setupClearButton()
    {
        calculator.getButtonClear().setFont(mainFont);
        calculator.getButtonClear().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonClear().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClear().setEnabled(true);
        calculator.getButtonClear().setName("Clear");
        calculator.getButtonClear().addActionListener(this::performClearButtonActions);
        LOGGER.info("Clear button configured");
    }
    /**
     * The actions to perform when the Clear button is clicked
     * @param actionEvent the action performed
     */
    public void performClearButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: {}", buttonChoice); // print out button confirmation
        // clear calculator.getValues()
        for (int i=0; i < 3; i++)
        {
            calculator.getValues()[i] = "";
        }
        // clear memory
        for(int i=0; i < 10; i++)
        {
            calculator.getMemoryValues()[i] = "";
        }
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0");
        //calculator.updateTextAreaValueFromTextArea();
        calculator.resetBasicOperators(false);
        calculator.setValuesPosition(0);
        calculator.setMemoryPosition(0);
        calculator.setFirstNumber(true);
        calculator.setDotPressed(false);
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
     * The main method to set up the Negate button
     */
    private void setupNegateButton()
    {
        calculator.getButtonNegate().setFont(mainFont);
        calculator.getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonNegate().setEnabled(true);
        calculator.getButtonNegate().setName("Negate");
        calculator.getButtonNegate().addActionListener(this::performNegateButtonActions);
    }
    /**
     * The actions to perform when you click Negate
     * @param actionEvent the click action
     */
    public void performNegateButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getTextPaneWithoutNewLineCharacters().equals("E"))
        {
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Number too big!");
            calculator.confirm("Number too big!");
        }
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
     * The main method to set up the SquareRoot button
     */
    private void setupSquareRootButton()
    {
        calculator.getButtonSqrt().setFont(mainFont);
        calculator.getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSqrt().setEnabled(true);
        calculator.getButtonSqrt().setName("SquareRoot");
        calculator.getButtonSqrt().addActionListener(this::performSquareRootButtonActions);
    }
    /**
     * The actions to perform when the SquareRoot button is clicked
     * @param actionEvent the click action
     */
    public void performSquareRootButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        LOGGER.debug("text: " + calculator.getTextPaneWithoutNewLineCharacters());
        if (calculator.textPaneContainsBadText())
        {
            calculator.getTextPane().setText("");
            calculator.confirm("Showing bad text. Reset textPane");
        }
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
                String result = String.valueOf(Math.sqrt(Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters())));
                result = calculator.formatNumber(result);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + result);
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPaneWithoutNewLineCharacters();
                calculator.confirm("Pressed " + buttonChoice);
            }
        }
    }

    /**
     * The main method to set up all number buttons, 0-9
     */
    private void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        getCalculator().getNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0") &&
                    getCalculator().getCalculatorType() != CONVERTER)
            { button.setPreferredSize(new Dimension(70, 35)); } //70,35
            else
            { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performNumberButtonActions);
        });
        LOGGER.info("Number buttons configured");
    }
    /**
     * The actions to perform when clicking any number button
     * @param actionEvent the click action
     */
    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} button{} actions...", calculator.getCurrentPanel().getName(), buttonChoice);
        if (!calculator.isFirstNumber()) // second number
        {
            calculator.getTextPane().setText("");
            calculator.setFirstNumber(true);
            if (!calculator.isNegating()) calculator.setNumberNegative(false);
            if (calculator.isDotPressed())
            {
                calculator.setDotPressed(false);
                calculator.getButtonDot().setEnabled(true);
            }
        }
        calculator.performInitialChecks();
        if (!calculator.isDotPressed())
        {
            LOGGER.debug("dot button was not pushed");
            calculator.updateValuesAtPositionThenUpdateTextPane(buttonChoice);
        }
        else
        {
            LOGGER.info("dot button was pushed");
            performDot(buttonChoice);
        }
        calculator.confirm("Pressed " + buttonChoice);
    }

    /**
     * The main method to set up the Dot button
     */
    private void setupDotButton()
    {
        calculator.getButtonDot().setFont(mainFont);
        calculator.getButtonDot().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDot().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDot().setEnabled(true);
        calculator.getButtonDot().setName("Dot");
        calculator.getButtonDot().addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }
    /**
     * The actions to perform when the Dot button is click
     * @param actionEvent the click action
     */
    public void performDotButtonActions(ActionEvent actionEvent)
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
    private void performDot(String buttonChoice)
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
        calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button
    }

    /**
     * The main method to set up the Add button
     */
    private void setupAddButton()
    {
        calculator.getButtonAdd().setFont(mainFont);
        calculator.getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonAdd().setEnabled(true);
        calculator.getButtonAdd().setName("Addition");
        calculator.getButtonAdd().addActionListener(this::performAdditionButtonActions);
        LOGGER.info("Add button configured");
    }
    /**
     * The actions to perform when the Addition button is clicked
     * @param actionEvent the click action
     */
    public void performAdditionButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform addition"); }
        else
        {
            if (!calculator.isAdding() && !calculator.isSubtracting()  && !calculator.isMultiplying() &&!calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextPane().getText()))
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getTextPaneWithoutNewLineCharacters() + " " + buttonChoice);
                calculator.setAdding(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) // 5 + 3 + ...
            {
                addition();
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
    private void addition()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) + Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " + " + calculator.getValues()[1] + " = " + result);
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            calculator.setDotPressed(true);
            calculator.getButtonDot().setEnabled(false);
            calculator.getValues()[0] = String.valueOf(result);
        }
    }

    /**
     * The main method to set up the Subtraction button
     */
    private void setupSubtractButton()
    {
        calculator.getButtonSubtract().setFont(mainFont);
        calculator.getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSubtract().setEnabled(true);
        calculator.getButtonSubtract().setName("Subtract");
        calculator.getButtonSubtract().addActionListener(this::performSubtractionButtonActions);
        LOGGER.info("Subtract button configured");
    }
    /**
     * The actions to perform when the Subtraction button is clicked
     * @param actionEvent the click action
     */
    public void performSubtractionButtonActions(ActionEvent actionEvent)
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
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.setAdding(calculator.resetOperator(calculator.isAdding()));
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty()) {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setSubtracting(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.info("The user pushed subtract but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + " " + buttonChoice);
                //calculator.setSubtracting(true);
                calculator.setNegating(true);
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()
                    && !calculator.isNegating())
            {
                LOGGER.info("operator already selected. then clicked subtract button. second number will be negated");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + buttonChoice);
                calculator.setNegating(true);
            }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            if (!calculator.isNegating()) calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for subtracting
     */
    private void subtract()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0])
                - Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " - " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = Double.toString(result);
        }
    }

    /**
     * The main method to set up the Multiplication button
     */
    private void setupMultiplyButton()
    {
        calculator.getButtonMultiply().setFont(mainFont);
        calculator.getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMultiply().setEnabled(true);
        calculator.getButtonMultiply().setName("Multiply");
        calculator.getButtonMultiply().addActionListener(this::performMultiplicationActions);
        LOGGER.info("Multiply button configured");
    }
    /**
     * The actions to perform when the Multiplication button is clicked
     * @param actionEvent the click action
     */
    public void performMultiplicationActions(ActionEvent actionEvent)
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
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying()); // mulBool = resetOperator(mulBool);
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty()) {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setMultiplying(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
//            else if (calculator.textPaneContainsBadText()) {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice); // "userInput +" // temp[valuesPosition]
//                calculator.setMultiplying(true); // sets logic for arithmetic
//                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
//                calculator.setDotPressed(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
//            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator())) {
                LOGGER.warn("The user pushed multiply but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for multiplying
     */
    private void multiply()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0])
                * Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " * " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        if (result % 1 == 0)
        {
            LOGGER.info("We have a whole number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        {
            // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }
    }

    /**
     * The main method to set up the Divide button
     */
    private void setupDivideButton()
    {
        calculator.getButtonDivide().setFont(mainFont);
        calculator.getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDivide().setEnabled(true);
        calculator.getButtonDivide().setName("Divide");
        calculator.getButtonDivide().addActionListener(this::performDivideButtonActions);
        LOGGER.info("Divide button configured");
    }
    /**
     * The actions to perform when the Divide button is clicked
     * @param actionEvent the click action
     */
    public void performDivideButtonActions(ActionEvent actionEvent)
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
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetOperator(calculator.isAdding()); // sets addBool to false
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty() & !calculator.getValues()[1].equals("0")) {
                divide();
                calculator.resetOperator(calculator.isDividing()); // divBool = resetOperator(divBool)
                calculator.setDividing(true);
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice);
            }
//            else if (calculator.textPaneContainsBadText())  {
//                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice); // "userInput +" // temp[valuesPosition]
//                calculator.setDividing(true); // sets logic for arithmetic
//                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
//                calculator.setDotPressed(false);
//                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
//            }
            else if (StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()))
            {
                LOGGER.warn("The user pushed divide but there is no number.");
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
     * The inner logic for dividing
     */
    private void divide()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = 0.0;
        if (!calculator.getValues()[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            result = Double.parseDouble(calculator.getValues()[0]) / Double.parseDouble(calculator.getValues()[1]); // create result forced double
            LOGGER.info(calculator.getValues()[0] + " / " + calculator.getValues()[1] + " = " + result);
            if (result % 1 == 0)
            {
                LOGGER.info("We have a whole number");
                calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textarea changed to whole number, or int
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
                calculator.setDotPressed(false);
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
            calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Cannot divide by 0");
            calculator.getValues()[0] = "0";
            calculator.setFirstNumber(true);
            calculator.confirm("Divided by 0. Values[position=0] = 0");
        }
    }

    /**
     * The main method to set up the Percent button
     */
    private void setupPercentButton()
    {
        calculator.getButtonPercent().setFont(mainFont);
        calculator.getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonPercent().setEnabled(true);
        calculator.getButtonPercent().setName("Percent");
        calculator.getButtonPercent().addActionListener(this::performPercentButtonActions);
    }
    /**
     * The actions to perform when the Percent button is clicked
     * @param actionEvent the click action
     */
    public void performPercentButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform percent operation. Number too big!"); }
        else
        {
            if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank())
            {
                // if the number is negative
                if (calculator.isNegativeNumber(calculator.getTextPaneWithoutNewLineCharacters()))
                {
                    LOGGER.info("number is negative for percentage");
                    //temp[valuesPosition] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    calculator.getValues()[calculator.getValuesPosition()] = Double.toString(percent);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.formatNumber(calculator.getValues()[calculator.getValuesPosition()])));
                    LOGGER.info("Old " + calculator.getValues()[calculator.getValuesPosition()]);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(1, calculator.getValues()[calculator.getValuesPosition()].length());
                    LOGGER.info("New " + calculator.getValues()[calculator.getValuesPosition()] + "-");
                    calculator.getTextPane().setText(calculator.getValues()[calculator.getValuesPosition()] + "-"); // update textArea
                    LOGGER.info("values["+calculator.getValuesPosition()+"] is " + calculator.getValues()[calculator.getValuesPosition()]);
                    //textArea.setText(textarea);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextPane().getText();//+textarea;
                    //calculator.setTextAreaValue(new StringBuffer());
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
                calculator.setDotPressed(true);
                calculator.confirm("Pressed " + buttonChoice);
            }
            else
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
                calculator.confirm("Pressed: " + buttonChoice);
            }
        }
    }

    /**
     * The main method to set up the Fraction button
     */
    private void setupFractionButton()
    {
        calculator.getButtonFraction().setFont(mainFont);
        calculator.getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonFraction().setEnabled(true);
        calculator.getButtonFraction().setName("Fraction");
        calculator.getButtonFraction().addActionListener(this::performFractionButtonActions);
    }
    /**
     * The actions to perform when the Fraction button is clicked
     * @param actionEvent the click action
     */
    public void performFractionButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        if (calculator.textPaneContainsBadText())
        { calculator.confirm("Cannot perform fraction operation. Number too big!"); }
        else
        {
            if (!calculator.getTextPaneWithoutNewLineCharacters().isBlank())
            {
                double result = Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters());
                result = 1 / result;
                LOGGER.info("result: " + result);
                calculator.setValuesAtPositionThenUpdateTextPane(String.valueOf(result));
                if (!calculator.isDecimal(calculator.getTextPaneWithoutNewLineCharacters()))
                {
                    calculator.setDotPressed(false);
                    calculator.getButtonDot().setEnabled(true);
                }
                else {
                    calculator.setDotPressed(true);
                    calculator.getButtonDot().setEnabled(false);
                }
                calculator.confirm("Pressed " + buttonChoice);
            }
            else
            {
                calculator.getTextPane().setText(calculator.addNewLineCharacters() + "Enter a Number");
                calculator.confirm("Pressed: " + buttonChoice);
            }
        }
    }

    /**
     * The main method to set up the Equals button
     */
    private void setupEqualsButton()
    {
        calculator.getButtonEquals().setFont(mainFont);
        calculator.getButtonEquals().setPreferredSize(new Dimension(35, 35) ); // 35,70
        calculator.getButtonEquals().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonEquals().setEnabled(true);
        calculator.getButtonEquals().setName("Equals");
        calculator.getButtonEquals().addActionListener(this::performEqualsButtonActions);
    }
    /**
     * The actions to perform when the Equals button is clicked
     * @param actionEvent the click action
     */
    public void performEqualsButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Starting {} button actions", buttonChoice);
        String operator = determineIfBasicPanelOperatorWasPushed();
        determineAndPerformBasicCalculatorOperation();
        if (!operator.isEmpty())
        { calculator.getTextPane().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]); }
        else if (calculator.textPaneContainsBadText())
        { calculator.getTextPane().setText(""); }
        calculator.getValues()[1] = ""; // this is not done in addition, subtraction, multiplication, or division
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
    public String determineIfBasicPanelOperatorWasPushed()
    {
        String results = "";
        if (calculator.isAdding()) { results = "+"; }
        else if (calculator.isSubtracting()) { results = "-"; }
        else if (calculator.isMultiplying()) { results = "*"; }
        else if (calculator.isDividing()) { results = "/"; }
        LOGGER.info("operator: " + (results.isEmpty() ? "no basic operator pushed" : results));
        return results;
    }

    /**
     * This method determines which basic operation to perform,
     * performs that operation, and resets the appropriate boolean
     */
    public void determineAndPerformBasicCalculatorOperation()
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
    public GridBagLayout getBasicLayout() { return basicLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public Calculator getCalculator() { return calculator; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
        this.basicLayout = panelLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
}