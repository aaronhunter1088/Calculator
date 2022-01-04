package Panels;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Calculators.Calculator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static Types.CalculatorBase.*;
import static Types.CalculatorType.*;

public class BasicPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(BasicPanel.class);
    private static final long serialVersionUID = 1L;

    private GridBagLayout basicLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    private Calculator calculator;

    /************* Constructors ******************/
    public BasicPanel() { LOGGER.info("Basic panel created"); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator
     */
    public BasicPanel(Calculator calculator) { setupBasicPanel(calculator); }

    /************* Start of methods here ******************/
    public void setupBasicPanel(Calculator calculator)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints());
        setMaximumSize(new Dimension(100,200));
        setupHelpMenu();
        setupBasicPanelComponents();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up basic panel");
    }

    public void setupBasicPanelComponents()
    {
        calculator.setCalculatorBase(DECIMAL);
        calculator.setCalculatorType(BASIC);
        calculator.setConverterType(null);
        calculator.setupTextArea();
        calculator.setupMemoryButtons(); // MS, MC, MR, M+, M-
        calculator.setupDeleteButton();
        calculator.setupClearEntryButton();
        calculator.setupClearButton();
        calculator.setupNegateButton();
        calculator.setupSquareRootButton();
        calculator.setupNumberButtons(true);
        calculator.setupDotButton();
        calculator.setupAddButton();
        calculator.setupSubtractButton();
        calculator.setupMultiplyButton();
        calculator.setupDivideButton();
        calculator.setupPercentButton();
        calculator.setupFractionButton();
        calculator.setupEqualsButton();
        LOGGER.info("Finished configuring the buttons");
    }

    public void addComponentsToPanel()
    {
        calculator.textArea.setBorder(new LineBorder(Color.BLACK));
        constraints.insets = new Insets(5,0,5,0);
        addComponent(calculator.textArea, 0, 0, 5, 2);
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
    {
        setupBasicPanel(calculator);
    }

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
        for(int i=0; i < calculator.getBar().getMenuCount(); i++) {
            JMenu menuOption = calculator.getBar().getMenu(i);
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
                        // do nothing at this moment
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(Calculator.font);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel text = new JLabel(helpString);

                    JScrollPane scrollPane = new JScrollPane(text);
                    scrollPane.setPreferredSize(new Dimension(150, 200));
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    JOptionPane.showMessageDialog(calculator,
                            scrollPane, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
                //menuOption.add(new JPopupMenu.Separator(), 1);
                //menuOption.add(calculator.createAboutCalculatorJMenuItem(), 2);
                //break; //?? for just changing one option could be ok. issue maybe if changing other options
            }
        }
        LOGGER.info("Finished setting up the help menu");
    }

    public void addComponent(Component c, int row, int column, int width, int height, int fill)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = fill;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = 0;
        constraints.weightx = 0;
        basicLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void addComponent(Component c, int row, int column, int width, int height)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.BOTH;
        //constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        //constraints.weighty = 0;
        //constraints.weightx = 0;
        basicLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
        this.basicLayout = panelLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
}
