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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Calculators.Calculator.mainFont;
import static Types.CalculatorBase.*;
import static Types.CalculatorType.*;

public class BasicPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class.getSimpleName());
    private static final long serialVersionUID = 4L;
    private Calculator calculator;

    private GridBagLayout basicLayout;
    private GridBagConstraints constraints;

    /************* Constructors ******************/
    public BasicPanel()
    { LOGGER.info("Empty Basic panel created"); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator the Calculator to use
     */
    public BasicPanel(Calculator calculator)
    {
        setupBasicPanel(calculator);
        LOGGER.info("Basic panel created");
    }

    /************* Start of methods here ******************/
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

    private void setupBasicPanelComponents()
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllOtherBasicCalculatorButtons(),
                        calculator.getBasicOperationButtons(),
                        calculator.getBasicNumberButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .collect(Collectors.toList());

        allButtons.forEach(button -> Stream.of(button.getActionListeners())
                .forEach(button::removeActionListener));

        calculator.setCalculatorBase(DECIMAL);
        calculator.setCalculatorType(BASIC);
        calculator.setConverterType(null);
        setupHelpMenu();
        setupTextArea();
        setupMemoryButtons(); // MS, MC, MR, M+, M-
        setupDeleteButton();
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
        LOGGER.info("Finished configuring the buttons");
    }

    private void addComponentsToPanel()
    {
        calculator.getTextArea().setBorder(new LineBorder(Color.BLACK));
        constraints.insets = new Insets(5,0,5,0);
        addComponent(calculator.getTextArea(), 0, 0, 5, 2);
        constraints.insets = new Insets(5,5,5,5);
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
        addComponent(calculator.getButtonEquals(), 6, 4, 1, 2);
        addComponent(calculator.getButton0(), 7, 0, 2, 1, GridBagConstraints.HORIZONTAL);
        addComponent(calculator.getButtonDot(), 7, 2, 1, 1);
        addComponent(calculator.getButtonAdd(), 7, 3, 1, 1);
        LOGGER.info("Buttons added to basic panel");
    }

    public void performBasicCalculatorTypeSwitchOperations(Calculator calculator)
    { setupBasicPanel(calculator); }

    private void setupHelpMenu()
    {
        String helpString = "<html>" +
                "How to use the " + BASIC.getName() + " Calculator<br><br>" +
                "For basic usage: <br>" +
                "Step 1.<br> Enter your first number followed by<br> an operator. Both will be visible in<br>" +
                "the text area.<br>" +
                "Step 2.<br> Enter the second number.<br>Entering the second number clears<br> the text area. <br>" +
                "Step 3.<br> Push the Equals button to see the<br> result. <br><br>" +
                "See button specific info below. <br>" +
                "The Dot Button: <br>" +
                "Pressing the dot button adds a<br> decimal to the number.<br> The button will become inactive<br>" +
                "until the next number is ready for<br> entry or the text area is cleared. <br><br>" +
                "The 1/x Button: <br>" +
                "This is the fraction button. It is<br> specific to say that when pushed,<br> it will take the number in the<br>" +
                "text area, rewrite it as 1 over the<br> number, and return the result.<br> For example, with 4 " +
                "in the text area,<br> pushing the fraction button<br> would return 0.25. <br><br>" +
                "The Percent button: <br>"+
                "The percent button takes the<br> number in the text area and returns<br> the percentage equivalent. " +
                "For example, with 10 in the text area, pushing the percent button would return 0.1. <br><br>" +
                "The Delete Button: <br>"+
                "The delete button removes the last<br> digit from the right until the entire<br> number is removed. <br><br>" +
                "The CE Button: <br>"+
                "The clear entry button clears the<br> text area. It does not clear what is<br> copied. <br><br>"+
                "The C Button: <br>"+
                "The clear button clears the text area,<br> and what is saved in copy. <br><br>"+
                "The Negate Button <br>"+
                "The negate button makes a positive number<br> a negative and a negative number a<br> positive one. <br><br>"+
                "The Square Root Button: <br>"+
                "The square root button will take<br> the number in the text area and<br> return the square root of it. <br><br>"+
                "Memory Suite: <br>"+
                "The MC Button: <br>"+
                "The memory clear button clears the<br> first number saved in memory,<br> until all numbers are erased. <br><br>"+
                "The MR Button: <br>"+
                "The memory recall button recalls<br> the the last number saved in<br> memory, looping through them<br>"+
                "all, and starts over. <br><br>"+
                "The MS Button: <br>"+
                "The memory store button saved the<br> current number in the text area into<br> memory. You can"+
                "save up to 10<br> different numbers. <br><br>"+
                "The Memory Add Button: <br>"+
                "The memory add button will add<br> the number in the text area to the<br> last saved value in memory."+
                "The text<br> area will update to show the result. <br><br>"+
                "The Memory Subtract Button: <br>"+
                "The memory subtract button will<br> subtract the number in the text area<br> to the last saved value "+
                "in memory.<br> The text area will update to show<br> the result. <br><br>"+

                "</html>";
        // 4 menu options: loop through to find the Help option
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
                    else if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("About"))
                    {
                        LOGGER.warn("IMPLEMENT ABOUT MENU");
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(mainFont);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel text = new JLabel(helpString);

                    JScrollPane scrollPane = new JScrollPane(text);
                    scrollPane.setPreferredSize(new Dimension(150, 200));
                    //TODO: rework to allow text to fit
                    if (!UIManager.getSystemLookAndFeelClassName().equals("javax.swing.plaf.metal.MetalLookAndFeel"))
                    {
                        scrollPane.setSize(new Dimension(250, 200));
                        SwingUtilities.updateComponentTreeUI(calculator);
                    }
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    JOptionPane.showMessageDialog(calculator,
                            scrollPane, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
        LOGGER.info("Finished setting up the help menu");
    }

    private void setupTextArea()
    {
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        calculator.setTextArea(new JTextPane());
        calculator.getTextArea().setParagraphAttributes(attribs, true);
        calculator.getTextArea().setFont(mainFont);
        calculator.getTextArea().setBorder(new LineBorder(Color.BLACK));
        calculator.getTextArea().setEditable(false);
        calculator.getTextArea().setPreferredSize(new Dimension(70, 30));
        LOGGER.info("TextArea_ configured");
    }

    private void setupMemoryButtons()
    {
        calculator.getButtonMemoryStore().setFont(mainFont);
        calculator.getButtonMemoryStore().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonMemoryStore().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMemoryStore().setEnabled(true);
        calculator.getButtonMemoryStore().setName("MS");
        calculator.getButtonMemoryStore().addActionListener(this::performMemoryStoreActions);
        LOGGER.info("Memory Store button configured");
        calculator.getButtonMemoryClear().setFont(mainFont);
        calculator.getButtonMemoryClear().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonMemoryClear().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMemoryClear().setEnabled(false);
        calculator.getButtonMemoryClear().setName("MC");
        calculator.getButtonMemoryClear().addActionListener(this::performMemoryClearActions);
        LOGGER.info("Memory Clear button configured");
        calculator.getButtonMemoryRecall().setFont(mainFont);
        calculator.getButtonMemoryRecall().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonMemoryRecall().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMemoryRecall().setEnabled(false);
        calculator.getButtonMemoryRecall().setName("MR");
        calculator.getButtonMemoryRecall().addActionListener(this::performMemoryRecallActions);
        LOGGER.info("Memory Recall button configured");
        calculator.getButtonMemoryAddition().setFont(mainFont);
        calculator.getButtonMemoryAddition().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonMemoryAddition().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMemoryAddition().setEnabled(false);
        calculator.getButtonMemoryAddition().setName("M+");
        calculator.getButtonMemoryAddition().addActionListener(this::performMemoryAddActions);
        LOGGER.info("Memory Add button configured");
        calculator.getButtonMemorySubtraction().setFont(mainFont);
        calculator.getButtonMemorySubtraction().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonMemorySubtraction().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMemorySubtraction().setEnabled(false);
        calculator.getButtonMemorySubtraction().setName("M-");
        calculator.getButtonMemorySubtraction().addActionListener(this::performMemorySubtractionActions);
        LOGGER.info("Memory Subtract button configured");
        // reset buttons to enabled if memories are saved
        if (!calculator.getMemoryValues()[0].isEmpty())
        {
            calculator.getButtonMemoryClear().setEnabled(true);
            calculator.getButtonMemoryRecall().setEnabled(true);
            calculator.getButtonMemoryAddition().setEnabled(true);
            calculator.getButtonMemorySubtraction().setEnabled(true);
        }
    }
    public void performMemoryStoreActions(ActionEvent action)
    {
        LOGGER.info("MemoryStoreButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (StringUtils.isNotBlank(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())) // if there is a number in the textArea
        {
            if (calculator.getMemoryPosition() == 10) // reset to 0
            {
                calculator.setMemoryPosition(0);
            }
            calculator.getMemoryValues()[calculator.getMemoryPosition()] = calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace();
            calculator.setMemoryPosition(calculator.getMemoryPosition() + 1);
            calculator.getButtonMemoryRecall().setEnabled(true);
            calculator.getButtonMemoryClear().setEnabled(true);
            calculator.getButtonMemoryAddition().setEnabled(true);
            calculator.getButtonMemorySubtraction().setEnabled(true);
            calculator.confirm(calculator.getValues()[calculator.getValuesPosition()] + " is stored in memory at position: " + (calculator.getMemoryPosition()-1));
        }
        else
        { calculator.confirm("No number added to memory. Blank entry"); }
    }
    public void performMemoryRecallActions(ActionEvent action)
    {
        LOGGER.info("MemoryRecallButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (calculator.getMemoryRecallPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]))
        {
            calculator.setMemoryRecallPosition(0);
        }
        calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getMemoryValues()[calculator.getMemoryRecallPosition()]);
        calculator.setMemoryRecallPosition(calculator.getMemoryRecallPosition() + 1);
        calculator.confirm("Recalling number in memory: " + calculator.getMemoryValues()[(calculator.getMemoryRecallPosition()-1)] + " at position: " + (calculator.getMemoryRecallPosition()-1));
    }
    public void performMemoryClearActions(ActionEvent action)
    {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand());
        if (calculator.getMemoryPosition() == 10 || StringUtils.isBlank(calculator.getMemoryValues()[calculator.getMemoryPosition()]))
        {
            LOGGER.debug("Resetting memoryPosition to 0");
            calculator.setMemoryPosition(0);
        }
        if (!calculator.isMemoryValuesEmpty())
        {
            LOGGER.info("Clearing memoryValue["+calculator.getMemoryPosition()+"] = " + calculator.getMemoryValues()[calculator.getMemoryPosition()]);
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
                calculator.getTextArea().setText(calculator.addNewLineCharacters()+"0");
                //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                calculator.confirm("MemorySuite is blank");
                return;
            }
            // MemorySuite now could potentially have gaps in front
            // {"5", "7", "", ""...} ... {"", "7", "", ""...}
            // If the first is a gap, then we have 1 too many gaps
            // TODO: fix for gap being in the middle of the memory calculator.getValues()
            if(calculator.getMemoryValues()[0].equals(""))
            {
                // Determine where the first number is that is not a gap
                int alpha = 0;
                for(int i = 0; i < 9; i++)
                {
                    if ( !calculator.getMemoryValues()[i].equals("") )
                    {
                        alpha = i;
                        calculator.getTextArea().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[alpha]);
                        //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
                        break;
                    }
                    else
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters()+"0");
                    }
                }
                // Move the first found value to the first position
                // and so on until the end
                String[] newMemoryValues = new String[10];
                for(int i = 0; i <= alpha; i++)
                {
                    if (calculator.getMemoryValues()[alpha].equals("")) break;
                    newMemoryValues[i] = calculator.getMemoryValues()[alpha];
                    alpha++;
                    if (alpha == calculator.getMemoryValues().length) break;
                }
                //setMemoryValues(newMemoryValues);
                calculator.setMemoryPosition(alpha); // next blank spot
            }
        }
        calculator.confirm("Reset memory at position: " + (calculator.getMemoryPosition()-1) + ".");
    }
    public void performMemoryAddActions(ActionEvent action)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        // if there is a number in the textArea, and we have A number stored in memory, add the
        // number in the textArea to the value in memory. value in memory should be the last number
        // added to memory
        if (calculator.isMemoryValuesEmpty())
        {
            calculator.confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
        {
            LOGGER.info("textArea: '" + calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+"]: '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                    + Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]); // create result forced double
            LOGGER.info(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " + " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isPositiveNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole positive number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            else if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isNegativeNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole negative number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToPositive(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToNegative(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            // update result in text area
            calculator.getTextArea().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }
    public void performMemorySubtractionActions(ActionEvent action)
    {
        LOGGER.info("MemorySubButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (calculator.isMemoryValuesEmpty())
        { calculator.confirm("No memories saved. Not subtracting."); }
        else if (StringUtils.isNotBlank(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())
                && StringUtils.isNotBlank(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
        {
            LOGGER.info("textArea: '" + calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + "'");
            LOGGER.info("memoryValues["+(calculator.getMemoryPosition()-1)+": '" + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + "'");
            double result = Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)])
                    - Double.parseDouble(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()); // create result forced double
            LOGGER.info(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] + " - " + calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " = " + result);
            calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = Double.toString(result);
            if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isPositiveNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole positive number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            else if (Double.parseDouble(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]) % 1 == 0 && calculator.isNegativeNumber(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]))
            {
                // whole negative number
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToPositive(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.clearZeroesAndDecimalAtEnd(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
                calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)] = calculator.convertToNegative(calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            }
            // update result in text area
            calculator.getTextArea().setText(calculator.addNewLineCharacters()+calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
            //calculator.setTextAreaValue(new StringBuffer(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            calculator.confirm("The new value in memory at position " + (calculator.getMemoryPosition()-1) + " is " + calculator.getMemoryValues()[(calculator.getMemoryPosition()-1)]);
        }
    }

    private void setupDeleteButton()
    {
        calculator.getButtonDelete().setFont(mainFont);
        calculator.getButtonDelete().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDelete().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDelete().setEnabled(true);
        calculator.getButtonDelete().setName("←");
        calculator.getButtonDelete().addActionListener(this::performDeleteButtonActions);
        LOGGER.info("Delete button configured");
    }
    public void performDeleteButtonActions(ActionEvent action)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[1].isEmpty())
        { calculator.setValuesPosition(0); } // assume they could have pressed an operator

        LOGGER.info("calculator.getValues()["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.info("textArea: " + calculator.getTextAreaWithoutNewLineCharacters());
        calculator.setNumberNegative(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        // this check has to happen
        calculator.setDotPressed(calculator.isDecimal(calculator.getTextArea().getText()));
        //calculator.getTextArea().setText(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        //calculator.updateTextAreaValueFromTextArea();
        if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 1)
                    {
                        calculator.getTextArea().setText("");
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextArea().getText().length() >= 2)
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaWithoutNewLineCharacters().substring(0,calculator.getTextAreaWithoutNewLineCharacters().length()-1));
                        calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutNewLineCharacters();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else
                {
                    if (calculator.getTextAreaWithoutNewLineCharacters().length() > 1) // ex: 3. or 3.2 or 0.5 or 3.25 or 0.50 or 5.02 or 78.9
                    { calculator.getTextArea().setText(calculator.addNewLineCharacters() +
                            calculator.getTextAreaWithoutNewLineCharacters().substring(0,calculator.getTextAreaWithoutNewLineCharacters().length()-1)); }
                    else // 3
                    {
                        calculator.getTextArea().setText("");
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    if (!calculator.getTextAreaWithoutNewLineCharacters().contains("."))
                    {
                        calculator.setDotPressed(false);
                        calculator.getButtonDot().setEnabled(true);
                    }
                    else
                    {
                        calculator.setDotPressed(true);
                        calculator.getButtonDot().setEnabled(false);
                    }
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutNewLineCharacters();
                }
            }
            else // if (calculator.isNumberNegative())
            {
                // if no operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 2)
                    { // ex: -3
                        //calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextArea().setText(calculator.getTextArea().getText().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextArea().getText().length() >= 3)
                    { // ex: -32 or + 6-
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())));
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())));
                        calculator.getTextArea().setText(calculator.getTextArea().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText();
                    }
                    LOGGER.info("output: " + calculator.getTextArea().getText());
                }
                // if no operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 4) { // -3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, 1))); // 3
                        calculator.setDotPressed(false);
                        calculator.getTextArea().setText(calculator.getTextArea().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText();
                    }
                    else if (calculator.getTextArea().getText().length() > 4)
                    { // ex: -3.25 or -0.00
                        //calculator.getTextArea().getText().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())); // 3.00 or 0.00
                        //.getTextArea().getText().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())); // 3.0 or 0.0
                        //calculator.getTextArea().getText().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextArea().getText().toString())); // 3 or 0
                        calculator.getTextArea().setText(calculator.getTextArea().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText();
                    }
                    LOGGER.info("output: " + calculator.getTextArea().getText());
                }
            }

        }
        else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
        {
            if (!calculator.isNumberNegative())
            {
                // if an operator has been pushed; number is positive; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 1)
                    { // ex: 5
                        //calculator.setTextAreaValue(new StringBuffer());
                    }
                    else if (calculator.getTextArea().getText().length() == 2)
                    {
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                    }
                    else if (calculator.getTextArea().getText().length() >= 2)
                    { // ex: 56 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                            calculator.setAdding(false);
                            calculator.setSubtracting(false);
                            calculator.setMultiplying(false);
                            calculator.setDividing(false);
                        }
                        else {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()-1)));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextArea().getText());
                    calculator.getTextArea().setText("\n" + calculator.getTextArea().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();
                    calculator.confirm();
                }
                // if an operator has been pushed; number is positive; number is decimal
                else //if (isDotPressed)
                {
                    if (calculator.getTextArea().getText().length() == 2) {
                        // ex: 3.
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                    }
                    else if (calculator.getTextArea().getText().length() == 3)
                    {
                        // ex: 3.2 0.0
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 2 ))); // 3 or 0
                        calculator.setDotPressed(false);
                    }
                    else if (calculator.getTextArea().getText().length() > 3)
                    { // ex: 3.25 or 0.50  or + 3.25-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            //.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
                        }
                        else {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1 )));
                            //calculator.getTextArea().getText().append(calculator.clearZeroesAndDecimalAtEnd(calculator.getTextArea().getText().toString()));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextArea().getText());
                    calculator.getTextArea().setText("\n"+ calculator.getTextArea().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();
                    calculator.confirm();
                }
            }
            else //if (isNumberNegative)
            {
                // if an operator has been pushed; number is negative; number is whole
                if (!calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 2) { // ex: -3
                        //calculator.setTextAreaValue(new StringBuffer());
                        calculator.getTextArea().setText(calculator.getTextArea().getText().toString());
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextArea().getText().length() >= 3) { // ex: -32 or + 6-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) {
                            //calculator.getTextArea().getText().append(calculator.getValues()[calculator.getValuesPosition()]);
                        }
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())));
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())));
                        calculator.getTextArea().setText("\n" + calculator.getTextArea().getText() + "-");
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText();
                    }
                    LOGGER.info("textarea: " + calculator.getTextArea().getText());
                    calculator.confirm();
                }
                // if an operator has been pushed; number is negative; number is decimal
                else //if (calculator.isDotPressed())
                {
                    if (calculator.getTextArea().getText().length() == 4) { // -3.2
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.2 or 0.0
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, 1))); // 3 or 0
                        calculator.setDotPressed(false);
                        calculator.getTextArea().setText(calculator.getTextArea().getText() + "-"); // 3- or 0-
                        calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText(); // -3 or -0
                    }
                    else if (calculator.getTextArea().getText().length() > 4) { // ex: -3.25  or -0.00
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getTextArea().getText().toString()))); // 3.25 or 0.00
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()))); // 3.2 or 0.0
                        calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getTextArea().getText().toString());
                        LOGGER.info("textarea: " + calculator.getTextArea().getText());
                        if (calculator.getTextArea().getText().toString().equals("0"))
                        {
                            calculator.getTextArea().setText(calculator.getTextArea().getText().toString());
                            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();
                        }
                        else {
                            calculator.getTextArea().setText(calculator.getTextArea().getText() + "-");
                            calculator.getValues()[calculator.getValuesPosition()] = "-" + calculator.getTextArea().getText();
                        }
                    }
                    LOGGER.info("textarea: " + calculator.getTextArea().getText());
                }
            }
            calculator.resetBasicOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        calculator.confirm("Pressed " + buttonChoice);
    }
    
    private void setupClearEntryButton()
    {
        calculator.getButtonClearEntry().setFont(mainFont);
        calculator.getButtonClearEntry().setMaximumSize(new Dimension(35, 35));
        calculator.getButtonClearEntry().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClearEntry().setEnabled(true);
        calculator.getButtonClearEntry().setName("CE");
        calculator.getButtonClearEntry().addActionListener(this::performClearEntryButtonActions);
        LOGGER.info("Clear Entry button configured");
    }
    /**
     * The action to perform when clicking the ClearEntry button
     * @param action the click action
     */
    public void performClearEntryButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        calculator.getTextArea().setText("");
        //calculator.updateTextAreaValueFromTextArea();
        if (calculator.getValues()[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
            calculator.getValues()[0] = "";
            calculator.setAdding(false);
            calculator.setSubtracting(false);
            calculator.setMultiplying(false);
            calculator.setDividing(false);
            calculator.setValuesPosition(0);
            calculator.setFirstNumber(true);
        } 
        else {
            calculator.getValues()[1] = "";
        }
        calculator.setDotPressed(false);
        calculator.getButtonDot().setEnabled(true);
        calculator.setNumberNegative(false);
        //calculator.getTextArea().getText().append(calculator.getTextArea().getText());
        LOGGER.info("ClearEntryButtonHandler() finished");
        calculator.confirm();
    }

    private void setupClearButton()
    {
        calculator.getButtonClear().setFont(mainFont);
        calculator.getButtonClear().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonClear().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonClear().setEnabled(true);
        calculator.getButtonClear().setName("C");
        calculator.getButtonClear().addActionListener(this::performClearButtonActions);
        LOGGER.info("Clear button configured");
    }
    /**
     * When the user clicks the Clear button, everything in the
     * calculator returns to initial start mode.
     * @param action the action performed
     */
    public void performClearButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
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
        calculator.getTextArea().setText(calculator.addNewLineCharacters() + "0");
        //calculator.updateTextAreaValueFromTextArea();
        calculator.resetBasicOperators(false);
        calculator.setValuesPosition(0);
        calculator.setMemoryPosition(0);
        calculator.setFirstNumber(true);
        calculator.setDotPressed(false);
        calculator.setNumberNegative(false);
        calculator.getButtonMemoryRecall().setEnabled(false);
        calculator.getButtonMemoryClear().setEnabled(false);
        calculator.getButtonDot().setEnabled(true);
        LOGGER.info("ClearButtonHandler() finished");
        calculator.confirm();
    }
    
    private void setupNegateButton()
    {
        calculator.getButtonNegate().setFont(mainFont);
        calculator.getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonNegate().setEnabled(true);
        calculator.getButtonNegate().addActionListener(this::performNegateButtonActions);
    }
    public void performNegateButtonActions(ActionEvent action)
    {
        String buttonChoice = action.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getValues()[calculator.getValuesPosition()].contains("E"))
        { calculator.confirm("Number too big!"); }
        else if (calculator.getValues()[calculator.getValuesPosition()].isEmpty())
        { calculator.confirm("No value to negate"); }
        else
        {
            if (calculator.isNumberNegative())
            {
                calculator.setNumberNegative(false);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()]));
            }
            else
            {
                calculator.setNumberNegative(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToNegative(calculator.getValues()[calculator.getValuesPosition()]));
            }
            calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutNewLineCharacters();
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    private void setupSquareRootButton()
    {
        calculator.getButtonSqrt().setFont(mainFont);
        calculator.getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSqrt().setEnabled(true);
        calculator.getButtonSqrt().addActionListener(this::performSquareRootButtonActions);
    }
    public void performSquareRootButtonActions(ActionEvent action)
    {
        LOGGER.info("SquareRoot ButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        String errorStringNaN = "Not a Number";
        LOGGER.debug("text: " + calculator.getTextAreaWithoutNewLineCharacters());
        if (calculator.getValues()[0].contains("E"))
        { calculator.confirm("Cannot perform square root operation. Number too big!"); }
        else
        {
            if (calculator.getTextAreaWithoutNewLineCharacters().isEmpty() || calculator.isNegativeNumber(calculator.getTextAreaWithoutNewLineCharacters()))
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + errorStringNaN);
                //calculator.setTextAreaValue(new StringBuffer().append("\n").append(errorStringNaN));
                calculator.confirm("Cannot perform square root operation on blank/negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.parseDouble(calculator.getTextAreaWithoutNewLineCharacters())));
                result = calculator.formatNumber(result);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + result);
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutNewLineCharacters();
                calculator.confirm("Pressed " + buttonChoice);
            }
        }
    }

    /**
     * This method handles the logic when we switch from any type of calculator
     * to the Programmer type
     */
    private void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        getCalculator().getNumberButtons().forEach(button -> {
            button.setFont(mainFont);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0") &&
                    getCalculator().getCalculatorType() != CONVERTER)
            { button.setPreferredSize(new Dimension(70, 35)); }
            else
            { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performNumberButtonActions);
        });
        LOGGER.info("Number buttons configured");
    }
    /**
     * The action to perform when clicking any number button
     * @param actionEvent the click event
     */
    public void performNumberButtonActions(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing {} button{} actions...", calculator.getCurrentPanel().getName(), buttonChoice);
        if (!calculator.isFirstNumber()) // second number
        {
            if (!calculator.isDotPressed())
            {
                calculator.getTextArea().setText("");
                //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText()));
                if (!calculator.isFirstNumber()) {
                    calculator.setFirstNumber(true);
                    calculator.setNumberNegative(false);
                }
                else calculator.setDotPressed(true);
                calculator.getButtonDot().setEnabled(true);
            }
        }
        calculator.performInitialChecks();
        if (calculator.isPositiveNumber(buttonChoice) && !calculator.isDotPressed())
        {
            LOGGER.info("positive number & dot button was not pushed");
            if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]))
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice);
                calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
            }
            else
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
                calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()] + buttonChoice;
            }
        }
        else if (calculator.isNumberNegative() && !calculator.isDotPressed())
        { // logic for negative numbers
            LOGGER.info("negative number & dot button had not been pushed");
            calculator.setTextareaToValuesAtPosition(buttonChoice);
        }
        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]))
        {
            LOGGER.info("positive number & dot button had been pushed");
            //calculator.performLogicForDotButtonPressed(buttonChoice);
            performDot(buttonChoice);
        }
        else
        {
            LOGGER.info("dot button was pushed");
            //calculator.performLogicForDotButtonPressed(buttonChoice);
            performDot(buttonChoice);
        }
        calculator.confirm("Pressed " + buttonChoice);
    }

    private void setupDotButton()
    {
        calculator.getButtonDot().setFont(mainFont);
        calculator.getButtonDot().setPreferredSize(new Dimension(35, 35));
        calculator.getButtonDot().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDot().setEnabled(true);
        calculator.getButtonDot().setName(".");
        calculator.getButtonDot().addActionListener(this::performDotButtonActions);
        LOGGER.info("Dot button configured");
    }
    public void performDotButtonActions(ActionEvent action)
    {
        LOGGER.info("Starting Dot button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (calculator.getValues()[0].contains("E")) { calculator.confirm("Cannot press dot button. Number too big!"); }
        else
        {
            LOGGER.info("Basic dot operations");
            performDot(buttonChoice);
        }
        calculator.confirm("Pressed the Dot button");
    }
    private void performDot(String buttonChoice)
    {
        calculator.getButtonDot().setEnabled(false); // deactivate button now that its active for this number
        calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button

        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]) || !calculator.isFirstNumber())
        {
            // dot pushed with nothing in textArea
            calculator.getValues()[calculator.getValuesPosition()] = "0.";
            calculator.getTextArea().setText(calculator.addNewLineCharacters() + "0.");
        }
        else if (StringUtils.isNotBlank(calculator.getValues()[calculator.getValuesPosition()]) && calculator.isDotPressed())
        {
            // pressed dot and then number ex: 0.5
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()])));
            //calculator.getTextArea().getText().append(buttonChoice);
            String currentValue = calculator.getValues()[calculator.getValuesPosition()];
            calculator.getValues()[calculator.getValuesPosition()] = currentValue + buttonChoice;
            calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
        }
        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]) && !calculator.isDotPressed())
        {
            // number and then dot is pushed ex: 5 -> .5
            //StringBuffer lodSB = new StringBuffer(textareaValue);
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice));
            calculator.setValuesToTextAreaValue();
            //calculator.setTextAreaValue(new StringBuffer().append(buttonChoice).append(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
            calculator.setDotPressed(true); //!LEAVE. dot logic should not be executed anymore for the current number
        }
        else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
        {
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToPositive(calculator.getValues()[calculator.getValuesPosition()])));
            //calculator.getTextArea().getText().append(buttonChoice);
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.convertToNegative(calculator.getTextArea().getText().toString())));
            calculator.setValuesToTextAreaValue();
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
    }

    private void setupAddButton()
    {
        calculator.getButtonAdd().setFont(mainFont);
        calculator.getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonAdd().setEnabled(true);
        calculator.getButtonAdd().addActionListener(this::performAdditionButtonActions);
        LOGGER.info("Add button configured");
    }
    public void performAdditionButtonActions(ActionEvent action)
    {
        String buttonChoice = action.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextArea().getText()) && !calculator.textArea1ContainsBadText())
            {
                if (calculator.isNumberNegative())
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + "-" + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextAreaWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutAnyOperator();
                }
                calculator.setAdding(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) // 5 + 3 + ...
            {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + " " + calculator.getTextArea().getText() + buttonChoice);
                //if (calculatorType == BASIC) calculator.getTextArea().setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.setSubtracting(calculator.resetOperator(calculator.isSubtracting()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + " " + calculator.getTextArea().getText() + buttonChoice);
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.setMultiplying(calculator.resetOperator(calculator.isMultiplying()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + " " + calculator.getTextArea().getText() + buttonChoice);
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.setDividing(calculator.resetOperator(calculator.isDividing()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + " " + calculator.getTextArea().getText() + buttonChoice);
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.textArea1ContainsBadText())
            {
                calculator.getTextArea().setText(calculator.getValues()[0] + " " + buttonChoice); // "userInput +" // calculator.getValues()[valuesPosition]
                calculator.setAdding(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextAreaWithoutAnyOperator()))
            {
                LOGGER.error("The user pushed plus but there is no number");
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
            { LOGGER.error("Already chose an operator. Choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    /**
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
        //calculator.getValues()[0] = Double.toString(result);
        if (calculator.isPositiveNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(String.valueOf(result)) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            String positive = calculator.clearZeroesAndDecimalAtEnd(calculator.convertToPositive(String.valueOf(result)));
            calculator.getValues()[0] = calculator.convertToNegative(positive);
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
            calculator.setNumberNegative(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }
    }

    private void setupSubtractButton()
    {
        calculator.getButtonSubtract().setFont(mainFont);
        calculator.getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonSubtract().setEnabled(true);
        calculator.getButtonSubtract().addActionListener(this::performSubtractionButtonActions);
        LOGGER.info("Subtract button configured");
    }
    public void performSubtractionButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Subtract Button actions");
        String buttonChoice = action.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextArea().getText()) && !calculator.textArea1ContainsBadText())
            {
                if (calculator.isNumberNegative())
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + "-" + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextAreaWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutAnyOperator();
                }
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.setAdding(calculator.resetOperator(calculator.isAdding()));
                calculator.setSubtracting(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].equals("")) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setSubtracting(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].equals("")) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setSubtracting(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].equals("")) {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setSubtracting(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.textArea1ContainsBadText()) {
                calculator.getTextArea().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextAreaWithoutAnyOperator())) {
                LOGGER.warn("The user pushed subtract but there is no number.");
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) {
                LOGGER.info("already chose an operator. next number is negative...");
                // TODO: 5 + -4 = 1 ... answer is coming back 10! Fix
                calculator.setNegating(true);
            }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.setNumberNegative(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    private void subtract()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) - Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " - " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            calculator.getValues()[0] = calculator.convertToPositive(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getValues()[0]);
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = Double.toString(result);
        }
        //calculator.setTextAreaValue(new StringBuffer(calculator.getValues()[0]));

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();
    }

    private void setupMultiplyButton()
    {
        calculator.getButtonMultiply().setFont(mainFont);
        calculator.getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonMultiply().setEnabled(true);
        calculator.getButtonMultiply().addActionListener(this::performMultiplicationActions);
        LOGGER.info("Multiply button configured");
    }
    public void performMultiplicationActions(ActionEvent action)
    {
        LOGGER.info("Performing Multiply Button actions");
        String buttonChoice = action.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + action.getActionCommand());
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextArea().getText()) && !calculator.textArea1ContainsBadText())
            {
                if (calculator.isNumberNegative())
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + "-" + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextAreaWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutAnyOperator();
                }
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying()); // mulBool = resetOperator(mulBool);
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty()) {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.textArea1ContainsBadText()) {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0] + " " + buttonChoice); // "userInput +" // temp[valuesPosition]
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextAreaWithoutAnyOperator())) {
                LOGGER.warn("The user pushed multiply but there is no number.");
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
    private void multiply()
    {
        LOGGER.info("value[0]: '" + calculator.getValues()[0] + "'");
        LOGGER.info("value[1]: '" + calculator.getValues()[1] + "'");
        double result = Double.parseDouble(calculator.getValues()[0]) * Double.parseDouble(calculator.getValues()[1]); // create result forced double
        LOGGER.info(calculator.getValues()[0] + " * " + calculator.getValues()[1] + " = " + result);
        calculator.getValues()[0] = Double.toString(result); // store result
        // new
        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            //textarea = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))));
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getTextArea().getText().toString());
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();


        // old
//        if (result % 1 == 0 && !calculator.getValues()[0].contains("E") && isPositiveNumber(calculator.getValues()[0]))
//        {
//            calculator.getValues()[0] = textAreaValue.toString(); // update storing
//        }
//        else if (result % 1 == 0 && isNegativeNumber(calculator.getValues()[0]))
//        {
//            LOGGER.info("We have a whole negative number");
//            calculator.getValues()[0] = convertToPositive(calculator.getValues()[0]);
//            calculator.getValues()[0] = clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
//            textArea.setText(addNewLineCharacters(1)+calculator.getValues()[0]+"-");
//            updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = textAreaValue.toString();
//            isDotPressed = false;
//            buttonDot.setEnabled(true);
//        }
//        else if (calculator.getValues()[0].contains("E"))
//        {
//            textArea.setText(addNewLineCharacters(1) + calculator.getValues()[0]);
//            updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = textAreaValue.toString(); // update storing
//        }
//        else if (isNegativeNumber(calculator.getValues()[0]))
//        {
//            textAreaValue = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
//            textArea.setText(addNewLineCharacters(1) + textAreaValue +"-");
//            textAreaValue = new StringBuffer().append(convertToNegative(calculator.getValues()[0]));
//        }
//        else
//        {// if double == double, keep decimal and number afterwards
//            textArea.setText(addNewLineCharacters(1) + formatNumber(calculator.getValues()[0]));
//        }
//
//        if (isPositiveNumber(calculator.getValues()[0])) textArea.setText(addNewLineCharacters(1) + calculator.getValues()[0]);
//        else textArea.setText(addNewLineCharacters(1) + convertToPositive(calculator.getValues()[0]) + "-");
//        updateTextAreaValueFromTextArea();
    }

    private void setupDivideButton()
    {
        calculator.getButtonDivide().setFont(mainFont);
        calculator.getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonDivide().setEnabled(true);
        calculator.getButtonDivide().addActionListener(this::performDivideButtonActions);
        LOGGER.info("Divide button configured");
    }
    public void performDivideButtonActions(ActionEvent action)
    {
        LOGGER.info("Performing Divide Button actions");
        String buttonChoice = action.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform division.";
            calculator.confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice);
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing()
                    && StringUtils.isNotBlank(calculator.getTextArea().getText()) && !calculator.textArea1ContainsBadText())
            {
                if (calculator.isNumberNegative())
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + "-" + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextAreaWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextAreaWithoutAnyOperator() + " " + buttonChoice);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextAreaWithoutAnyOperator();
                }
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetOperator(calculator.isAdding()); // sets addBool to false
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty() & !calculator.getValues()[1].equals("0")) {
                divide();
                calculator.resetOperator(calculator.isDividing()); // divBool = resetOperator(divBool)
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice);
            }
            else if (calculator.textArea1ContainsBadText())  {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + " " + buttonChoice); // "userInput +" // temp[valuesPosition]
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (StringUtils.isBlank(calculator.getTextAreaWithoutAnyOperator())) {
                LOGGER.warn("The user pushed divide but there is no number.");
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + "Enter a Number");
            }
            else if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing()) { LOGGER.info("already chose an operator. choose another number."); }
            calculator.getButtonDot().setEnabled(true);
            calculator.setDotPressed(false);
            calculator.confirm("Pressed: " + buttonChoice);
        }
    }
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
            calculator.getValues()[0] = Double.toString(result); // store result
        }
        else
        {
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            calculator.getTextArea().setText("\nCannot divide by 0");
            calculator.getValues()[0] = "0";
            calculator.setFirstNumber(true);
        }

        if (calculator.isPositiveNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole positive number");
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result));// textarea changed to whole number, or int
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else if (calculator.isNegativeNumber(calculator.getValues()[0]) && result % 1 == 0)
        {
            LOGGER.info("We have a whole negative number");
            calculator.getValues()[0] = calculator.convertToPositive(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]);
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getValues()[0]);
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            if (Double.parseDouble(calculator.getValues()[0]) < 0.0 )
            {   // negative decimal
                calculator.getValues()[0] = calculator.formatNumber(calculator.getValues()[0]);
                LOGGER.info("textarea: '" + calculator.getTextArea().getText() + "'");
                //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0]));
                LOGGER.info("textarea: '" + calculator.getTextArea().getText() + "'");
                //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(1, calculator.getTextArea().getText().length())));
                LOGGER.info("textarea: '" + calculator.getTextArea().getText() + "'");
            }
            else
            {   // positive decimal
                calculator.getValues()[0] = calculator.formatNumber(calculator.getValues()[0]);
            }
        }
        //calculator.setTextAreaValue(new StringBuffer(calculator.getValues()[0]));

        if (calculator.isPositiveNumber(calculator.getValues()[0])) calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);
        else calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
        //calculator.updateTextAreaValueFromTextArea();
    }

    private void setupPercentButton()
    {
        calculator.getButtonPercent().setFont(mainFont);
        calculator.getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonPercent().setEnabled(true);
        calculator.getButtonPercent().addActionListener(this::performPercentButtonActions);
    }
    public void performPercentButtonActions(ActionEvent action)
    {
        LOGGER.info("PercentStoreButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        { calculator.confirm("Cannot perform percent operation. Number too big!"); }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (!calculator.getTextAreaWithoutNewLineCharacters().isEmpty())
            {
                // if the number is negative
                if (calculator.isNegativeNumber(calculator.getTextAreaWithoutNewLineCharacters()))
                {
                    LOGGER.info("number is negative for percentage");
                    //temp[valuesPosition] = textArea.getText(); // textarea
                    double percent = Double.parseDouble(calculator.getValues()[calculator.getValuesPosition()]);
                    percent /= 100;
                    LOGGER.info("percent: "+percent);
                    calculator.getValues()[calculator.getValuesPosition()] = Double.toString(percent);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.formatNumber(calculator.getValues()[calculator.getValuesPosition()])));
                    LOGGER.info("Old " + calculator.getValues()[calculator.getValuesPosition()]);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getValues()[calculator.getValuesPosition()].substring(1, calculator.getValues()[calculator.getValuesPosition()].length());
                    LOGGER.info("New " + calculator.getValues()[calculator.getValuesPosition()] + "-");
                    calculator.getTextArea().setText(calculator.getValues()[calculator.getValuesPosition()] + "-"); // update textArea
                    LOGGER.info("values["+calculator.getValuesPosition()+"] is " + calculator.getValues()[calculator.getValuesPosition()]);
                    //textArea.setText(textarea);
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();//+textarea;
                    //calculator.setTextAreaValue(new StringBuffer());
                    LOGGER.info("values["+calculator.getValuesPosition()+"] is " + calculator.getValues()[calculator.getValuesPosition()]);
                    LOGGER.info("textArea: "+calculator.getTextArea().getText());
                }
                else
                {
                    double percent = Double.parseDouble(calculator.getValues()[calculator.getValuesPosition()]);
                    percent /= 100;
                    calculator.getValues()[calculator.getValuesPosition()] = Double.toString(percent);
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.formatNumber(calculator.getValues()[calculator.getValuesPosition()]));
                }
            }
            calculator.setDotPressed(true);
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    private void setupFractionButton()
    {
        calculator.getButtonFraction().setFont(mainFont);
        calculator.getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonFraction().setEnabled(true);
        calculator.getButtonFraction().addActionListener(this::performFractionButtonActions);
    }
    public void performFractionButtonActions(ActionEvent action)
    {
        LOGGER.info("FractionButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (calculator.getValues()[0].contains("E"))
        { calculator.confirm("Cannot perform fraction operation. Number too big!"); }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (!calculator.getTextAreaWithoutNewLineCharacters().isEmpty())
            {
                double result = Double.parseDouble(calculator.getValues()[calculator.getValuesPosition()]);
                result = 1 / result;
                LOGGER.info("result: " + result);
                calculator.getValues()[calculator.getValuesPosition()] = Double.toString(result);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()]);
            }
            calculator.confirm("Pressed " + buttonChoice);
        }
    }

    private void setupEqualsButton()
    {
        calculator.getButtonEquals().setFont(mainFont);
        calculator.getButtonEquals().setPreferredSize(new Dimension(35, 70) );
        calculator.getButtonEquals().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonEquals().setEnabled(true);
        calculator.getButtonEquals().addActionListener(this::performButtonEqualsActions);
    }
    public void performButtonEqualsActions(ActionEvent actionEvent)
    {
        LOGGER.info("Performing Equal Button actions");
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        String operator = determineIfBasicPanelOperatorWasPushed();
        determineAndPerformBasicCalculatorOperation();
        if (!operator.isEmpty())
        { calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]); }
        else if (calculator.textArea1ContainsBadText())
        { calculator.getTextArea().setText(""); }
        calculator.getValues()[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        calculator.getValues()[3] = "";
        //updateTextareaFromTextArea();
        calculator.setFirstNumber(true);
        calculator.setDotPressed(false);
        calculator.setValuesPosition(0);
        calculator.confirm("Pushed " + buttonChoice);
    }

    private void addComponent(Component c, int row, int column, int width, int height)
    { addComponent(c, row, column, width, height, GridBagConstraints.BOTH); }
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
    
    /* Helper functions */

    /**
     * This method returns the operator that was activated
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
}