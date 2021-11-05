package version4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static version4.CalcType_v4.*;

public abstract class Calculator_v4 extends JFrame
{
    final static protected Logger LOGGER;
    static
    {
        LOGGER = LogManager.getLogger(Calculator_v4.class);
    }

    final static private long serialVersionUID = 1L;

    /*
     * All calculators have the same:
     * layout and constraints
     * buttons 0-9
     * clear button
     * clear entry button
     * delete button
     * dot button
     * memory clear button
     * memory recall button
     * memory store button
     * memory addition button
     * memory subtract button
     */
    protected GridBagLayout layout; // layout of the calculator
    protected GridBagConstraints constraints; // layout's constraints
    final protected JButton button0 = new JButton("0");
    final protected JButton button1 = new JButton("1");
    final protected JButton button2 = new JButton("2");
    final protected JButton button3 = new JButton("3");
    final protected JButton button4 = new JButton("4");
    final protected JButton button5 = new JButton("5");
    final protected JButton button6 = new JButton("6");
    final protected JButton button7 = new JButton("7");
    final protected JButton button8 = new JButton("8");
    final protected JButton button9 = new JButton("9");
    final protected JButton buttonClear = new JButton("C");
    final protected JButton buttonClearEntry = new JButton("CE");
    final protected JButton buttonDelete = new JButton("\u2190"); //"<--"
    final protected JButton buttonDot = new JButton(".");
    final protected JButton buttonMemoryClear = new JButton("MC");
    final protected JButton buttonMemoryRecall = new JButton("MR");
    final protected JButton buttonMemoryStore = new JButton("MS");
    final protected JButton buttonMemoryAddition = new JButton("M+");
    final protected JButton buttonMemorySubtraction = new JButton("M-");
    final protected static Font font = new Font("Segoe UI", Font.PLAIN, 12);
    final protected static Font font2 = new Font("Verdana", Font.BOLD, 20);
    protected String[] values = {"","","",""}; // firstNum (total), secondNum, copy/paste, temporary storage. memory values now in MemorySuite.getMemoryValues()
    protected int valuesPosition = 0;
    protected String[] memoryValues = new String[]{"","","","","","","","","",""}; // stores memory values; rolls over after 10 entries
    protected int memoryPosition = 0;
    protected int memoryRecallPosition = 0;
    protected JTextArea textArea = new JTextArea(2,5); // rows, columns
    protected StringBuffer textarea = new StringBuffer(); // String representing appropriate visual of number
    protected CalcType_v4 calcType = null;
    protected JPanel currentPanel = null;
    protected ImageIcon calculatorImage1, calculator2, macLogo, blankImage;
    protected JLabel iconLabel;
    protected JLabel textLabel;

    protected boolean firstNumBool = true;
    protected boolean numberOneNegative = false;
    protected boolean numberTwoNegative = false;
    protected boolean numberThreeNegative = false;
    protected boolean numberIsNegative = false;
    protected boolean memorySwitchBool = false;
    protected boolean addBool = false;
    protected boolean subBool = false;
    protected boolean mulBool = false;
    protected boolean divBool = false;
    protected boolean memAddBool = false;
    protected boolean memSubBool = false;
    protected boolean negatePressed = false;
    protected boolean dotButtonPressed = false;
    // programmer related fields
    boolean isButtonBinSet = true;
    boolean isButtonOctSet = false;
    boolean isButtonDecSet = false;
    boolean isButtonHexSet = false;
    boolean isButtonByteSet = true;
    boolean isButtonWordSet = false;
    boolean isButtonDwordSet = false;
    boolean isButtonQwordSet = false;
    protected boolean orButtonBool = false;
    protected boolean modButtonBool = false;
    protected boolean xorButtonBool = false;
    protected boolean negateButtonBool = false;
    protected boolean notButtonBool = false;
    protected boolean andButtonBool = false;
    protected CalcType_v4 base = CalcType_v4.DECIMAL;


	public Calculator_v4() throws HeadlessException { super(); }
	public Calculator_v4(GraphicsConfiguration gc)
    {
		super(gc);
	}
    public Calculator_v4(String title, GraphicsConfiguration gc)
    {
        super(title, gc);
    }
	public Calculator_v4(String title) throws HeadlessException, IOException
    {
		super(title);
		layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();
		setupCalculator();
		setMinimumSize(new Dimension(100,200));
    }

    /******************** Start of methods here **********************/

    /**
     * Handles the logic for setting up a Calculator_v4. Should
     * run once. Anything that needs to be reset, should be reset
     * where appropriate.
     */
	public void setupCalculator()
    {
		LOGGER.info("Inside setupCalculator()");
		textArea.getCaret().isSelectionVisible();
		textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setFont(font);
        textArea.setPreferredSize(new Dimension(70, 35));
        textArea.setEditable(false);

        button0.setFont(font);
        button0.setPreferredSize(new Dimension(70, 35) );
        button0.setBorder(new LineBorder(Color.BLACK));
        button0.setEnabled(true);
        button0.addActionListener(this::performNumberButtonActions);
        
        button1.setFont(font);
        button1.setPreferredSize(new Dimension(35, 35) );
        button1.setBorder(new LineBorder(Color.BLACK));
        button1.setEnabled(true);
        button1.addActionListener(this::performNumberButtonActions);
        
        button2.setFont(font);
        button2.setPreferredSize(new Dimension(35, 35) );
        button2.setBorder(new LineBorder(Color.BLACK));
        button2.setEnabled(true);
        button2.addActionListener(this::performNumberButtonActions);
        
        button3.setFont(font);
        button3.setPreferredSize(new Dimension(35, 35) );
        button3.setBorder(new LineBorder(Color.BLACK));
        button3.setEnabled(true);
        button3.addActionListener(this::performNumberButtonActions);
        
        button4.setFont(font);
        button4.setPreferredSize(new Dimension(35, 35) );
        button4.setBorder(new LineBorder(Color.BLACK));
        button4.setEnabled(true);
        button4.addActionListener(this::performNumberButtonActions);
        
        button5.setFont(font);
        button5.setPreferredSize(new Dimension(35, 35) );
        button5.setBorder(new LineBorder(Color.BLACK));
        button5.setEnabled(true);
        button5.addActionListener(this::performNumberButtonActions);
        
        button6.setFont(font);
        button6.setPreferredSize(new Dimension(35, 35) );
        button6.setBorder(new LineBorder(Color.BLACK));
        button6.setEnabled(true);
        button6.addActionListener(this::performNumberButtonActions);
        
        button7.setFont(font);
        button7.setPreferredSize(new Dimension(35, 35) );
        button7.setBorder(new LineBorder(Color.BLACK));
        button7.setEnabled(true);
        button7.addActionListener(this::performNumberButtonActions);
        
        button8.setFont(font);
        button8.setPreferredSize(new Dimension(35, 35) );
        button8.setBorder(new LineBorder(Color.BLACK));
        button8.setEnabled(true);
        button8.addActionListener(this::performNumberButtonActions);
        
        button9.setFont(font);
        button9.setPreferredSize(new Dimension(35, 35) );
        button9.setBorder(new LineBorder(Color.BLACK));
        button9.setEnabled(true);
        button9.addActionListener(this::performNumberButtonActions);
        
        buttonClear.setFont(font);
        buttonClear.setPreferredSize(new Dimension(35, 35) );
        buttonClear.setBorder(new LineBorder(Color.BLACK));
        buttonClear.setEnabled(true);
        buttonClear.addActionListener(action -> {
            performClearButtonActions(action);
            if (getCalcType() == CalcType_v4.PROGRAMMER)
            {
                ((JPanelProgrammer_v4) getCurrentPanel()).resetProgrammerOperators();
            }
        });
        
        buttonClearEntry.setFont(font);
        buttonClearEntry.setPreferredSize(new Dimension(35, 35) );
        buttonClearEntry.setBorder(new LineBorder(Color.BLACK));
        buttonClearEntry.setEnabled(true);
        buttonClearEntry.addActionListener(this::performClearEntryButtonActions);
        
        buttonDelete.setFont(font);
        buttonDelete.setPreferredSize(new Dimension(35, 35) );
        buttonDelete.setBorder(new LineBorder(Color.BLACK));
        buttonDelete.setEnabled(true);
        buttonDelete.addActionListener(this::performDeleteButtonActions);
        
        buttonDot.setFont(font);
        buttonDot.setPreferredSize(new Dimension(35, 35) );
        buttonDot.setBorder(new LineBorder(Color.BLACK));
        buttonDot.setEnabled(true);
        buttonDot.addActionListener(this::performDotButtonActions);

        buttonMemoryStore.setFont(Calculator_v4.font);
        buttonMemoryStore.setPreferredSize(new Dimension(35, 35) );
        buttonMemoryStore.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryStore.setEnabled(true);
        buttonMemoryStore.addActionListener(this::performMemoryStoreActions);

        buttonMemoryRecall.setFont(Calculator_v4.font);
        buttonMemoryRecall.setPreferredSize(new Dimension(35, 35) );
        buttonMemoryRecall.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryRecall.setEnabled(false);
        buttonMemoryRecall.addActionListener(this::performMemoryRecallActions);

        buttonMemoryClear.setFont(Calculator_v4.font);
        buttonMemoryClear.setPreferredSize(new Dimension(35, 35) );
        buttonMemoryClear.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryClear.setEnabled(false);
        buttonMemoryClear.addActionListener(this::performMemoryClearActions);

        buttonMemoryAddition.setFont(Calculator_v4.font);
        buttonMemoryAddition.setPreferredSize(new Dimension(35, 35) );
        buttonMemoryAddition.setBorder(new LineBorder(Color.BLACK));
        buttonMemoryAddition.setEnabled(true);
        buttonMemoryAddition.addActionListener(this::performMemoryAddActions);

        buttonMemorySubtraction.setFont(Calculator_v4.font);
        buttonMemorySubtraction.setPreferredSize(new Dimension(35, 35) );
        buttonMemorySubtraction.setBorder(new LineBorder(Color.BLACK));
        buttonMemorySubtraction.setEnabled(true);
        buttonMemorySubtraction.addActionListener(this::performMemorySubtractionActions);

        LOGGER.info("Finished setupCalculator()");
	}

    /**
     * Handles the logic for setting up the buttons numbered 0-9.
     */
    public void setupNumberButtons(boolean isEnabled) {
        for(JButton button : getNumberButtons()) {
            button.setFont(font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.addActionListener(this::performNumberButtonActions);
        }
    }

    public void clearNumberButtonFunctionalities() {
        getNumberButtons().forEach(button -> {
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
        });
    }

    public void setEnabledForAllNumberButtons(boolean isEnabled)
    {
        for(JButton button : getNumberButtons()) { button.setEnabled(isEnabled); }
    }
    
	/**
     * Adds the components to the container
     *
     * @param c
     * @param row
     * @param column
     * @param width
     * @param height
     */
    public void addComponent(Component c, int row, int column, int width, int height)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void performMemoryStoreActions(ActionEvent action)
    {
        LOGGER.info("MemoryStoreButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())) // if there is a number in the textArea
        {
            if (memoryPosition == 10) // reset to 0
            {
                memoryPosition = 0;
            }
            memoryValues[memoryPosition] = getTextAreaWithoutNewLineCharacters();
            memoryPosition++;
            buttonMemoryRecall.setEnabled(true);
            buttonMemoryClear.setEnabled(true);
            confirm(values[valuesPosition] + " is stored in memory at position: " + (memoryPosition-1));
        }
        else {
            confirm("No number added to memory. Blank entry");
        }
    }

    public void performMemoryRecallActions(ActionEvent action)
    {
        LOGGER.info("MemoryRecallButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (memoryRecallPosition == 10 || StringUtils.isBlank(memoryValues[memoryRecallPosition]))
        {
            memoryRecallPosition = 0;
        }
        textArea.setText(addNewLineCharacters(1) + memoryValues[memoryRecallPosition]);
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
        values[valuesPosition] = getTextAreaWithoutNewLineCharacters();
        memoryRecallPosition++;
        confirm("Recalling number in memory: " + memoryValues[(memoryRecallPosition-1)] + " at position: " + (memoryRecallPosition-1));
    }

    public void performMemoryClearActions(ActionEvent action)
    {
        LOGGER.info("MemoryClearButtonHandler started");
        LOGGER.info("button: " + action.getActionCommand());
        if (memoryPosition == 10 || StringUtils.isBlank(memoryValues[memoryPosition]))
        {
            memoryPosition = 0;
        }
        if (!isMemoryValuesEmpty())
        {
            memoryValues[memoryPosition] = "";
            memoryPosition++;
            // MemorySuite now could potentially be empty
            if (isMemoryValuesEmpty())
            {
                memoryPosition = 0;
                memoryRecallPosition = 0;
                buttonMemoryClear.setEnabled(false);
                buttonMemoryRecall.setEnabled(false);
                getTextArea().setText(addNewLineCharacters(1)+"0");
                setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
                confirm("MemorySuite is blank");
                return;
            }
            // MemorySuite now could potentially have gaps in front
            // {"5", "7", "", ""...} ... {"", "7", "", ""...}
            // If the first is a gap, then we have 1 too many gaps
            if(memoryValues[0].equals(""))
            {
                // Determine where the first number is that is not a gap
                int alpha = 0;
                for(int i = 0; i < 9; i++)
                {
                    if ( !memoryValues[i].equals("") )
                    {
                        alpha = i;
                        getTextArea().setText(addNewLineCharacters(1)+memoryValues[alpha]);
                    }
                    else
                    {
                        getTextArea().setText(addNewLineCharacters(1)+"0");

                    }
                    setTextarea(new StringBuffer(getTextAreaWithoutNewLineCharacters()));
                    break;
                }
                // Move the first found value to the first position
                // and so on until the end
                String[] newMemoryValues = new String[10];
                for(int i = 0; i < alpha; i++)
                {
                    if (memoryValues[alpha].equals("")) break;
                    newMemoryValues[i] = memoryValues[alpha];
                    alpha++;
                    if (alpha == memoryValues.length) break;
                }
                setMemoryValues(newMemoryValues);
                setMemoryPosition(alpha); // next blank spot
            }
        }
        confirm("Reset memory at position: " + (memoryPosition-1) + ".");
    }

    public void performMemoryAddActions(ActionEvent action)
    {
        LOGGER.info("MemoryAddButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        // if there is a number in the textArea, and we have A number stored in memory, add the
        // number in the textArea to the value in memory. value in memory should be the last number
        // added to memory
        if (isMemoryValuesEmpty())
        {
            confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+"]: '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(getTextAreaWithoutNewLineCharacters())
                    + Double.parseDouble(memoryValues[(memoryPosition-1)]); // create result forced double
            LOGGER.info(getTextAreaWithoutNewLineCharacters() + " + " + memoryValues[(memoryPosition-1)] + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    public void performMemorySubtractionActions(ActionEvent action)
    {
        LOGGER.info("MemorySubButtonHandler class started");
        LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
        if (isMemoryValuesEmpty())
        {
            confirm("No memories saved. Not adding.");
        }
        else if (StringUtils.isNotBlank(getTextAreaWithoutNewLineCharacters())
                && StringUtils.isNotBlank(memoryValues[(memoryPosition-1)]))
        {
            LOGGER.info("textArea: '" + getTextAreaWithoutNewLineCharacters() + "'");
            LOGGER.info("memoryValues["+(memoryPosition-1)+": '" + memoryValues[(memoryPosition-1)] + "'");
            double result = Double.parseDouble(memoryValues[(memoryPosition-1)])
                    - Double.parseDouble(getTextAreaWithoutNewLineCharacters()); // create result forced double
            LOGGER.info(memoryValues[(memoryPosition-1)] + " - " + getTextAreaWithoutNewLineCharacters() + " = " + result);
            memoryValues[(memoryPosition-1)] = Double.toString(result);
            if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isPositiveNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole positive number
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
            }
            else if (Double.parseDouble(memoryValues[(memoryPosition-1)]) % 1 == 0 && isNegativeNumber(memoryValues[(memoryPosition-1)]))
            {
                // whole negative number
                memoryValues[(memoryPosition-1)] = convertToPositive(memoryValues[(memoryPosition-1)]);
                memoryValues[(memoryPosition-1)] = clearZeroesAtEnd(memoryValues[(memoryPosition-1)]).toString();
                memoryValues[(memoryPosition-1)] = convertToNegative(memoryValues[(memoryPosition-1)]);
            }
            confirm("The new value in memory at position " + (memoryPosition-1) + " is " + memoryValues[(memoryPosition-1)]);
        }
    }

    public void performNumberButtonActions(ActionEvent action)
    {
        LOGGER.info("NumberButtonHandler_v2 started");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("button: " + buttonChoice);
        if (getCalcType() == BASIC) {
            performBasicCalculatorNumberButtonActions(buttonChoice);
        }
        else if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            performProgrammerCalculatorNumberButtonActions(buttonChoice);
        }
        //TODO: add more types here

        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        confirm("NumberButtonHandler_v2() finishing");
    }

    /**
     * When the user clicks the Clear button, everything in the
     * calculator returns to initial start mode.
     * @param action
     */
    public void performClearButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        // clear values
        for (int i=0; i < 3; i++)
        {
            values[i] = "";
        }
        // clear memory
        for(int i=0; i < 10; i++)
        {
            memoryValues[i] = "";
        }
        textArea.setText("\n0");
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
        resetOperators(false);
        valuesPosition = 0;
        memoryPosition = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotButtonPressed = false;
        numberIsNegative = false;
        buttonMemoryRecall.setEnabled(false);
        buttonMemoryClear.setEnabled(false);
        buttonDot.setEnabled(true);
        LOGGER.info("ClearButtonHandler() finished");
        confirm();
    }

    public void performClearEntryButtonActions(ActionEvent action)
    {
        LOGGER.info("ClearEntryButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        textarea = new StringBuffer();
        textArea.setText("");
        if (values[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
            values[0] = "";
            addBool = false;
            subBool = false;
            mulBool = false;
            divBool = false;
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
        } else {
            values[1] = "";
        }
        buttonDot.setEnabled(true);
        textarea.append(textArea.getText());
        LOGGER.info("ClearEntryButtonHandler() finished");
        confirm();
    }

    public void performDeleteButtonActions(ActionEvent action)
    {
        LOGGER.info("DeleteButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (values[1].equals("")) {
            //addBool = false;
            //subBool = true;
            //mulBool = true;
            //divBool = true;
            valuesPosition = 0;
        } // assume they just previously pressed an operator

        LOGGER.info("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
        LOGGER.info("textarea: " + textarea);
        numberIsNegative = isNegativeNumber(values[valuesPosition]);
        // this check has to happen
        dotButtonPressed = isDecimal(textarea.toString());
        getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        updateTextareaFromTextArea();
        if (addBool == false && subBool == false && mulBool == false && divBool == false)
        {
            if (numberIsNegative == false)
            {
                // if no operator has been pushed; number is positive; number is whole
                if (isDotButtonPressed() == false)
                {
                    if (textarea.length() == 1)
                    { // ex: 5
                        getTextArea().setText("");
                        textarea = new StringBuffer().append(" ");
                        values[valuesPosition] = "";
                    }
                    else if (textarea.length() >= 2)
                    { // ex: 56 or 2346
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        getTextArea().setText(addNewLineCharacters(1)+String.valueOf(textarea));
                        values[valuesPosition] = textarea.toString();
                    }
                    LOGGER.debug("result: '" + textarea.toString().replaceAll("\n","") + "'");
                }
                // if no operator has been pushed; number is positive; number is decimal
                else if (isDotButtonPressed()) {
                    if (textarea.length() == 2) { // ex: 3. .... recall textarea looks like .3
                        textarea = new StringBuffer().append(textarea.substring(textarea.length()-1)); // ex: 3
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 2)); // ex: 3 or 0
                        dotButtonPressed = false;
                        //dotActive = false;
                        buttonDot.setEnabled(true);
                    } else if (textarea.length() > 3) { // ex: 3.25 or 0.50 or 5.02
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length() - 1)); // inclusive
                    }
                    LOGGER.info("output: " + textarea);
                        /*if (textarea.endsWith(".")) {
                            textarea = textarea.substring(0,textarea.length()-1);
                            //dotActive = false;
                            dotButtonPressed = false;
                            buttonDot.setEnabled(true);
                        } else if (textarea.startsWith(".")) {
                            textarea = textarea.substring(1,1);
                        } */
                    //textarea = clearZeroesAtEnd(textarea);
                    textArea.setText("\n" + textarea);
                    values[valuesPosition] = textarea.toString();
                }
            }
            else if (numberIsNegative) {
                // if no operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 2) { // ex: -3
                        textarea = new StringBuffer();
                        textArea.setText(textarea.toString());
                        values[valuesPosition] = "";
                    }
                    else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("output: " + textarea);
                }
                // if no operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 4) { // -3.2
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2
                        textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3
                        dotButtonPressed = false;
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
                        textarea.append(convertToPositive(textarea.toString())); // 3.00 or 0.00
                        textarea.append(textarea.substring(0, textarea.length())); // 3.0 or 0.0
                        textarea.append(clearZeroesAtEnd(textarea.toString())); // 3 or 0
                        textArea.setText(textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("output: " + textarea);
                }
            }

        }
        else if (addBool == true || subBool == true || mulBool == true || divBool == true)
        {
            if (numberIsNegative == false) {
                // if an operator has been pushed; number is positive; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 1) { // ex: 5
                        textarea = new StringBuffer();
                    } else if (textarea.length() == 2) {
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                    } else if (textarea.length() >= 2) { // ex: 56 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea = new StringBuffer().append(values[valuesPosition]);
                            addBool = false;
                            subBool = false;
                            mulBool = false;
                            divBool = false;
                        } else {
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                        }
                    }
                    LOGGER.info("output: " + textarea);
                    textArea.setText("\n" + textarea);
                    values[valuesPosition] = textarea.toString();
                    confirm();
                }
                // if an operator has been pushed; number is positive; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 2) // ex: 3.
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-1));
                    else if (textarea.length() == 3) { // ex: 3.2 0.0
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                    } else if (textarea.length() > 3) { // ex: 3.25 or 0.50  or + 3.25-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea = new StringBuffer().append(values[valuesPosition]);
                        } else {
                            textarea = new StringBuffer().append(textarea.substring(0, textarea.length() -1));
                            textarea.append(clearZeroesAtEnd(textarea.toString()));
                        }
                    }
                    LOGGER.info("output: " + textarea);
                    textArea.setText("\n"+textarea);
                    values[valuesPosition] = textarea.toString();
                    confirm();
                }
            } else if (numberIsNegative == true) {
                // if an operator has been pushed; number is negative; number is whole
                if (dotButtonPressed == false) {
                    if (textarea.length() == 2) { // ex: -3
                        textarea = new StringBuffer();
                        textArea.setText(textarea.toString());
                        values[valuesPosition] = "";
                    } else if (textarea.length() >= 3) { // ex: -32 or + 6-
                        if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                            textarea.append(values[valuesPosition]);
                        }
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length()));
                        textArea.setText("\n" + textarea + "-");
                        values[valuesPosition] = "-" + textarea;
                    }
                    LOGGER.info("textarea: " + textarea);
                    confirm();
                }
                // if an operator has been pushed; number is negative; number is decimal
                else if (dotButtonPressed == true) {
                    if (textarea.length() == 4) { // -3.2
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.2 or 0.0
                        textarea = new StringBuffer().append(textarea.substring(0, 1)); // 3 or 0
                        dotButtonPressed = false;
                        dotButtonPressed = false;
                        textArea.setText(textarea + "-"); // 3- or 0-
                        values[valuesPosition] = "-" + textarea; // -3 or -0
                    } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
                        textarea = new StringBuffer().append(convertToPositive(textarea.toString())); // 3.25 or 0.00
                        textarea = new StringBuffer().append(textarea.substring(0, textarea.length())); // 3.2 or 0.0
                        textarea = clearZeroesAtEnd(textarea.toString());
                        LOGGER.info("textarea: " + textarea);
                        if (textarea.equals("0")) {
                            textArea.setText(textarea.toString());
                            values[valuesPosition] = textarea.toString();
                        } else {
                            textArea.setText(textarea + "-");
                            values[valuesPosition] = "-" + textarea;
                        }
                    }
                    LOGGER.info("textarea: " + textarea);
                }
            }
            resetOperators(false);
        }
        LOGGER.info("DeleteButtonHandler() finished");
        confirm();
    }

    public void performDotButtonActions(ActionEvent action)
    {
        LOGGER.info("DotButtonHandler() started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot press dot button. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (StringUtils.isBlank(getTextAreaWithoutNewLineCharacters().strip()))
            {
                textarea.append("0"+buttonChoice);
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+"0");
            }
            else if (!isNegativeNumber(values[valuesPosition]))
            {
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+getTextAreaWithoutNewLineCharacters());
                textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters().replace(".","")
                        + buttonChoice);
            }
            else // number is negative. reverse. add Dot. reverse back -5 -> 5 -> 5. -> -5. <--> .5-
            {
                textarea = new StringBuffer().append(convertToPositive(values[valuesPosition]));
                getTextArea().setText(addNewLineCharacters(1)+buttonChoice+textarea+"-");
                LOGGER.info("TextArea: " + getTextArea().getText());
                textarea.append(".");
                textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            }
            values[valuesPosition] = textarea.toString();
            buttonDot.setEnabled(false);
            setDotButtonPressed(true);
            LOGGER.info("DotButtonHandler() finished");
            confirm();
        }
    }

    public void performNumberButtonActions(String buttonChoice)
    {
        performInitialChecks();
        getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        updateTextareaFromTextArea();
        LOGGER.info("Performing basic actions...");
        if (!numberIsNegative && !isDotButtonPressed())
        {
            LOGGER.info("firstNumBool = true | positive number = true & dotButtonPressed = false");
            LOGGER.debug("before: " + textArea.getText());
            if (textArea.getText().equals(""))
            {
                textArea.setText("\n" + buttonChoice);
                setValuesToTextAreaValue();
                textArea.setText("\n" + textArea.getText());
            }
            else
            {
                textArea.setText("\n" + textArea.getText() + buttonChoice); // update textArea
                setValuesToTextAreaValue();
                textArea.setText("\n" + textArea.getText());
            }

        }
        else if (numberIsNegative && !isDotButtonPressed())
        { // logic for negative numbers
            LOGGER.info("firstNumBool = true | negative number = true & dotButtonPressed = false");
            setTextareaToValuesAtPosition(buttonChoice);
        }
        else if (!numberIsNegative && isDotButtonPressed())
        {
            LOGGER.info("firstNumBool = true | negative number = false & dotButtonPressed = true");
            performLogicForDotButtonPressed(buttonChoice);
        }
        else
        {
            LOGGER.info("firstNumBool = true & dotButtonPressed = true");
            performLogicForDotButtonPressed(buttonChoice);
        }
    }

    public void performBasicCalculatorNumberButtonActions(String buttonChoice)
    {
        LOGGER.info("Starting basic calculator number button actions");
        if (!isFirstNumBool()) // do for second number
        {
            if (!isDotButtonPressed())
            {
                getTextArea().setText("");
                setTextarea(new StringBuffer().append(getTextArea().getText()));
                if (!isFirstNumBool()) {
                    setFirstNumBool(true);
                    setNumberIsNegative(false);
                } else
                    setDotButtonPressed(true);
                buttonDot.setEnabled(true);
            }
        }
        performNumberButtonActions(buttonChoice);
    }
    public void performProgrammerNumberButtonActions(String buttonChoice)
    {
	    performInitialChecks();
	    LOGGER.info("Performing programmer actions...");
	    if (getTextArea().getText().length() > getBytes())
	    {
	        return; // don't allow length to get any longer
        }
        if (textArea.getText().equals(""))
        {
            textArea.setText(addNewLineCharacters(1) + buttonChoice);
        }
        else
        {
            textArea.setText(addNewLineCharacters(1) + getTextAreaWithoutNewLineCharacters() + buttonChoice); // update textArea
        }
        updateTextareaFromTextArea();
        values[valuesPosition] = textarea.toString(); // store textarea in values
    }
    public void performProgrammerCalculatorNumberButtonActions(String buttonChoice)
    {
        LOGGER.info("Starting programmer calculator number button actions");
        if (firstNumBool)
        {
            performProgrammerNumberButtonActions(buttonChoice);
        }
        else
        {
            firstNumBool = true;
            textArea.setText("");
            textarea = new StringBuffer();
            valuesPosition = 1;
            performProgrammerNumberButtonActions(buttonChoice);
        }
    }
    public void performLogicForDotButtonPressed(String buttonChoice)
    {
        if (!textarea.equals("") && isDotButtonPressed() && isNumberNegative())
        {
            textarea = new StringBuffer().append(convertToPositive(values[valuesPosition]));
            getTextArea().setText(addNewLineCharacters(1)+textarea+buttonChoice+"-");
            LOGGER.info("TextArea: " + getTextArea().getText());
            textarea.append(buttonChoice);
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[valuesPosition] = textarea.toString();
            dotButtonPressed = false;
        }
        else if (!textarea.equals("") && isDotButtonPressed() && !isNumberNegative())
        {
            textarea = new StringBuffer().append(values[valuesPosition] + buttonChoice);
            textArea.setText(addNewLineCharacters(1)+textarea.toString() );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = getTextAreaWithoutNewLineCharacters();
        }
        else if (!textarea.equals("") && !isDotButtonPressed())
        {
            textarea.append(values[valuesPosition] + buttonChoice);
            textArea.setText("\n" + textarea );
            LOGGER.info("textarea: " + textarea);
            values[valuesPosition] = textArea.getText().replaceAll("\n", "");
        }
    }

    public String addNewLineCharacters(int number)
    {
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<number; i++) {
	        sb.append("\n");
        }
	    return String.valueOf(sb);
    }

    public void updateTextareaFromTextArea()
    {
        textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
    }

    public void performInitialChecks()
    {
	    boolean checkFound = false;
        if (memAddBool || memSubBool)
        {
            textArea.setText("");
            checkFound = true;
        }
	    else if (textAreaContainsBadText())
	    {
            textArea.setText(addNewLineCharacters(1)+"0");
            valuesPosition = 0;
            firstNumBool = true;
            dotButtonPressed = false;
            numberIsNegative = false;
            checkFound = true;
        }
        else if (textArea.getText().equals("\n0") && getCalcType().equals(BASIC))
        {
            LOGGER.debug("textArea equals 0 no matter the form. setting to blank.");
            textArea.setText("");
            textarea = new StringBuffer().append("");
            values[valuesPosition] = "";
            firstNumBool = true;
            dotButtonPressed = false;
            checkFound = true;
        }
        else if (StringUtils.isBlank(values[0]) && StringUtils.isNotBlank(values[1]))
        {
            values[0] = values[1];
            values[1] = "";
            valuesPosition = 0;
            checkFound = true;
        }
        LOGGER.info("Performing initial checks.... results: " + checkFound);
    }

    /**
     * Resets all operators to the given boolean argument
     *
     * @param operatorBool
     * @return
     *
     * Fully tested
     */
	public boolean resetOperator(boolean operatorBool)
    {
        if (operatorBool == true) {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("values[0]: '" + values[0] + "'");
            valuesPosition = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            return false;
        } else {
        	LOGGER.info("operatorBool: " + operatorBool);
            values[1]= "";
            LOGGER.info("temp[0]: '" + values[0] + "'");
            valuesPosition = 0;
            dotButtonPressed = false;
            firstNumBool = true;
            return true;
        }
    }

    /**
     *  Returns the results of the last action
     */
    public void confirm()
    {
	    confirm("", BASIC);
    }

    public void confirm(String message) {
        confirm(message, BASIC);
    }

    public void confirm(CalcType_v4 calcType) {
        confirm("", calcType);
    }

    /**
     *  Returns the results of the last action with a specific message to display
     *
     * @param message a message to send into the confirm results view
     */
    public void confirm(String message, CalcType_v4 calculatorType)
    {
        switch (calculatorType) {
            case BASIC : {
                if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
                else { LOGGER.info("Confirm Results"); }
                LOGGER.info("---------------- ");
                LOGGER.info("textarea: '"+textarea.toString()+"'");
                LOGGER.info("textArea: '\\n"+getTextAreaWithoutNewLineCharacters()+"'");
                if (StringUtils.isBlank(memoryValues[0]) && StringUtils.isBlank(memoryValues[memoryPosition]))
                {
                    LOGGER.info("no memories stored!");
                }
                else
                {
                    for(int i = 0; i < 10; i++)
                    {
                        if (StringUtils.isNotBlank(memoryValues[i])) {
                            LOGGER.info("memoryValues["+i+"]: " + memoryValues[i]);
                        }
                    }
                }
                LOGGER.info("addBool: '"+addBool+"'");
                LOGGER.info("subBool: '"+subBool+"'");
                LOGGER.info("mulBool: '"+mulBool+"'");
                LOGGER.info("divBool: '"+divBool+"'");
                LOGGER.info("orButtonBool: '" +orButtonBool+"'");
                LOGGER.info("modButtonBool: '" +modButtonBool+"'");
                LOGGER.info("xorButtonBool: '" +xorButtonBool+"'");
                LOGGER.info("notButtonBool: '" +notButtonBool+"'");
                LOGGER.info("andButtonBool: '" +andButtonBool+"'");
                LOGGER.info("values["+0+"]: '" + values[0] + "'");
                LOGGER.info("values["+1+"]: '" + values[1] + "'");
                LOGGER.info("values["+2+"]: '" + values[2] + "'");
                LOGGER.info("valuesPosition: '"+valuesPosition+"'");
                LOGGER.info("memoryPosition: '"+memoryPosition+"'");
                LOGGER.info("memoryRecallPosition: '"+memoryRecallPosition+"'");
                LOGGER.info("firstNumBool: '"+firstNumBool+"'");
                LOGGER.info("dotButtonPressed: '"+dotButtonPressed+"'");
                LOGGER.info("isNegative: '"+numberIsNegative+"'");
                LOGGER.info("calcType: '" + calcType + "'");
                LOGGER.info("calcBase: '" + base + "'");
                LOGGER.info("-------- End Confirm Results --------\n");
                break;
            }
            case DATE : { break; }
            case CONVERTER:  {
                if (StringUtils.isNotEmpty(message)) { LOGGER.info("Confirm Results: " + message); }
                else { LOGGER.info("Confirm Results"); }
                LOGGER.info("---------------- ");
                LOGGER.info("Converter: '" + ((JPanelConverter_v4)getCurrentPanel()).getConverterNameAsString() + "'");
                LOGGER.info("textField1: '" + ((JPanelConverter_v4)getCurrentPanel()).getTextField1().getText() + " "
                        + ((JPanelConverter_v4)getCurrentPanel()).getUnitOptions1().getSelectedItem() + "'");
                LOGGER.info("textField2: '" + ((JPanelConverter_v4)getCurrentPanel()).getTextField2().getText() + " "
                        + ((JPanelConverter_v4)getCurrentPanel()).getUnitOptions2().getSelectedItem() + "'");
                LOGGER.info("-------- End Confirm Results --------\n");
                break;
            }
        }
    }


    /**
     * This method resets the 4 main operators to the boolean you pass in
     */
    public void resetOperators(boolean bool)
    {
        addBool = bool;
        subBool = bool;
        mulBool = bool;
        divBool = bool;
    }

    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     *
     * @param currentNumber
     * @return updated currentNumber
     *
     * TODO: Test
     */
    protected StringBuffer clearZeroesAtEnd(String currentNumber)
    {
        LOGGER.info("starting clearZeroesAtEnd()");
        LOGGER.debug("currentNumber: " + currentNumber);
        textarea = new StringBuffer().append(textarea.toString().replaceAll("\n",""));
        boolean justZeroes = true;
        int index = 0;
        index = currentNumber.indexOf(".");
        LOGGER.debug("index: " + index);
        if (index == -1) textarea = new StringBuffer().append(textarea);
        else {
            textarea = new StringBuffer().append(currentNumber.substring(0, index));
        }
        LOGGER.info("output of clearZeroesAtEnd(): " + String.valueOf(textarea));
        return new StringBuffer().append(String.valueOf(textarea));
    }

    public CalcType_v4 determineCalcType()
    {
        if (currentPanel instanceof JPanelBasic_v4) { return BASIC; }
        else if (currentPanel instanceof JPanelProgrammer_v4) { return PROGRAMMER; }
        else if (currentPanel instanceof JPanelScientific_v4) { return SCIENTIFIC; }
        else if (currentPanel instanceof JPanelDate_v4) { return DATE; }
        else if (currentPanel instanceof JPanelConverter_v4) { return CONVERTER; }
        else LOGGER.error("Unknown currentPanel: " + getCurrentPanel() +  ". Defaulting to BASIC.");
        return BASIC;
    }

    public int getBytes()
    {
        if (isButtonByteSet) { return 8; }
        else if (isButtonWordSet) { return 16; }
        else if (isButtonDwordSet) { return 32; }
        else if (isButtonQwordSet) { return 64; }
        else { return 0; } // shouldn't ever come here
    }

    /**
     * Tests whether a number is a decimal or not
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public boolean isDecimal(String number)
    {
        boolean answer = false;
        if (number.contains(".")) answer = true;
        LOGGER.info("isDecimal("+number.replaceAll("\n","")+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is positive
     *
     * @param result
     * @return either true or false based on result
     *
     * Fully tested
     */
    public boolean isPositiveNumber(String result)
    {
        boolean answer = !isNegativeNumber(result);
        LOGGER.info("isPositiveNumber("+result+") == " + answer);
        return answer;
    }

    /**
     * Tests whether a number is negative
     *
     * @param result
     * @return
     *
     * Fully tested
     */
    public boolean isNegativeNumber(String result)
    {
        boolean answer = false;
        if (result.contains("-")) {
            answer = true;
        }
        LOGGER.info("isNegativeNumber("+result.replaceAll("\n", "")+") == " + answer);
        return answer;
    }

    /**
     * Converts a number to its negative equivalent
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public String convertToNegative(String number)
    {
        LOGGER.info("convertToNegative("+number+") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = "-" + number.replaceAll("\n", "");
        LOGGER.debug("New: "  + number);
        numberIsNegative = true;
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     *
     * @param number
     * @return
     *
     * Fully tested
     */
    public String convertToPositive(String number)
    {
        LOGGER.info("convertToPositive("+number+") running");
        LOGGER.debug("Old: " + number.replaceAll("\n", ""));
        number = number.replaceAll("-", "").trim();
        LOGGER.debug("New: " + number);

        numberIsNegative = false;
        return number;
    }

    protected String getTextAreaWithoutNewLineCharacters()
    {
        return getTextArea().getText().replaceAll("\n", "").strip();
    }

    public boolean isMemoryValuesEmpty()
    {
        boolean result = false;
        for(int i = 0; i < 10; i++)
        {
            if (StringUtils.isBlank(memoryValues[i]))
            {
                result = true;
            }
            else
            {
                result = false;
                break;
            }
        }
        return result;
    }

    @Deprecated
    public JPanel getCurrentPanelFromParentCalculator()
    {
        return this.currentPanel;
    }

    // TODO: add in when number has E in it.
    /**
     * Tests whether the TextArea contains a String which shows a previous error
     *
     * @return
     *
     * TODO: Test
     */
    public boolean textAreaContainsBadText()
    {
        boolean result = false;
        if (getTextAreaWithoutNewLineCharacters().equals("Invalid textarea")   ||
            getTextAreaWithoutNewLineCharacters().equals("Cannot divide by 0") ||
            getTextAreaWithoutNewLineCharacters().equals("Not a Number")       ||
            getTextAreaWithoutNewLineCharacters().equals("Only positive numbers") ||
            getTextAreaWithoutNewLineCharacters().contains("E") )
        {
            result = true;
        }
        return result;
    }

    /**
     * Calls createImageIcon(String path, String description
     *
     * @throws Exception
     */
    public ImageIcon createImageIcon(String path) throws Exception
    {
        return createImageIcon(path, "No description given.");
    }

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the image
     * @param description a description of the image being set
     */
    protected ImageIcon createImageIcon(String path, String description)
    {
        LOGGER.info("Inside createImageIcon(): creating image for " + description);
        ImageIcon retImageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) {
            retImageIcon = new ImageIcon(resource);
            LOGGER.info("the path '" + path + "' created the '"+description+"'! the ImageIcon is being returned...");
            LOGGER.info("End createImageIcon()");
        }
        else {
            LOGGER.debug("The path '" + path + "' could not find an image there!. Trying again by removing 'src/main/resources/' from path!");
            resource = classLoader.getResource(path.substring(19));
            if (resource != null) {
                retImageIcon = new ImageIcon(resource);
                LOGGER.info("the path '" + path + "' created an image after removing 'src/main/resources/'! the ImageIcon is being returned...");
                LOGGER.info("End createImageIcon()");
            } else {
                LOGGER.error("The path '" + path + "' could not find an image there after removing src/main/resources/!. Returning null!");
            }

        }
        return retImageIcon;
    }

    /** Sets the image icons */
    public void setImageIcons()
    {
        try
        {
            calculatorImage1 = createImageIcon("src/main/resources/images/calculatorOriginalCopy.jpg");
            calculator2 = createImageIcon("src/main/resources/images/calculatorOriginal.jpg");
            macLogo = createImageIcon("src/main/resources/images/maclogo.jpg");
            setBlankImage(new ImageIcon());
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage() + " ");
        }
    }

    public String formatNumber(String num)
    {
        DecimalFormat df = new DecimalFormat();
        if (!numberIsNegative) {
            if (num.length() == 2) df = new DecimalFormat("0.00");
            if (num.length() == 3) df = new DecimalFormat("0.000");
        } else {
            if (num.length() == 3) df = new DecimalFormat("0.0");
            if (num.length() == 4) df = new DecimalFormat("0.00");
            if (num.length() == 5) df = new DecimalFormat("0.000");
        }
        double number = Double.parseDouble(num);
        number = Double.valueOf(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }

    /************************ Unsolid Methods ***************************/

    /**
     * This method returns all the values in memory which are not blank
     *
     * @param someValues
     * @return
     *
     * TODO: Test
     */
    @Deprecated
    public ArrayList<String> getNonBlankValues(String[] someValues)
    {
    	ArrayList<String> listToReturn = new ArrayList<>();
    	for (int i=0; i<10; i++) {
    		String thisValue = someValues[i];
    		if (!thisValue.equals("")) {
    			listToReturn.add(thisValue);
    		} else {
    			break;
    		}
    	}
    	return listToReturn;
    }

    public void setTextareaToValuesAtPosition(String buttonChoice)
    {
        textarea.append(values[valuesPosition]);
        LOGGER.debug("textarea: '", textarea + "'");
        textarea.append(textarea + buttonChoice); // update textArea
        LOGGER.debug("textarea: '" + textarea + "'");
        values[valuesPosition] = textarea.toString(); // store textarea
        textArea.setText("\n" + convertToPositive(textarea.toString()) + "-");
        LOGGER.debug("textArea: '" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    public void setValuesToTextAreaValue()
    {
        getTextArea().setText(getTextAreaWithoutNewLineCharacters());
        updateTextareaFromTextArea();
        values[valuesPosition] = textarea.toString(); // store textarea in values
        LOGGER.debug("textArea: '\\n" + textArea.getText() + "'");
        LOGGER.debug("values["+valuesPosition+"]: '" + values[valuesPosition] + "'");
    }

    /************* All Getters and Setters ******************/

    public static Logger getLOGGER() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }
    @Override
    public GridBagLayout getLayout() { return layout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JButton getButton0() { return button0; }
    public JButton getButton1() { return button1; }
    public JButton getButton2() { return button2; }
    public JButton getButton3() { return button3; }
    public JButton getButton4() { return button4; }
    public JButton getButton5() { return button5; }
    public JButton getButton6() { return button6; }
    public JButton getButton7() { return button7; }
    public JButton getButton8() { return button8; }
    public JButton getButton9() { return button9; }
    public JButton getButtonClear() { return buttonClear; }
    public JButton getButtonClearEntry() { return buttonClearEntry; }
    public JButton getButtonDelete() { return buttonDelete; }
    public JButton getButtonDot() { return buttonDot; }
    public JButton getButtonMemoryClear() { return buttonMemoryClear; }
    public JButton getButtonMemoryRecall() { return buttonMemoryRecall; }
    public JButton getButtonMemoryStore() { return buttonMemoryStore; }
    public JButton getButtonMemoryAddition() { return buttonMemoryAddition; }
    public JButton getButtonMemorySubtraction() { return buttonMemorySubtraction; }
    public Font getFont() { return font; }
    public String[] getValues() { return values; }
    public String[] getMemoryValues() { return memoryValues; }
    public int getValuesPosition() { return valuesPosition; }
    public int getMemoryPosition() { return memoryPosition; }
    protected JTextArea getTextArea() { return this.textArea; }
    public StringBuffer getTextarea() { return textarea; }
    public CalcType_v4 getCalcType() { return this.calcType; }
    public JPanel getCurrentPanel() { return currentPanel; }
    public ImageIcon getCalculatorImage1() { return calculatorImage1; }
    public ImageIcon getCalculator2() { return calculator2; }
    public ImageIcon getMacLogo() { return macLogo; }
    public ImageIcon getBlankImage() { return blankImage; }
    public JLabel getIconLabel() { return iconLabel; }
    public JLabel getTextLabel() { return textLabel; }

    public boolean isFirstNumBool() { return firstNumBool; }
    public boolean isNumberOneNegative() { return numberOneNegative; }
    public boolean isNumberTwoNegative() { return numberTwoNegative; }
    public boolean isNumberThreeNegative() { return numberThreeNegative; }
    public boolean isNumberNegative() { return numberIsNegative; }
    public boolean isMemorySwitchBool() { return memorySwitchBool; }
    public boolean isAddBool() { return addBool; }
    public boolean isSubBool() { return subBool; }
    public boolean isMulBool() { return mulBool; }
    public boolean isDivBool() { return divBool; }
    public boolean isMemAddBool() { return memAddBool; }
    public boolean isMemSubBool() { return memSubBool; }
    public boolean isNegatePressed() { return negatePressed; }
    public boolean isDotButtonPressed() { return dotButtonPressed; }
    public boolean isOrButtonPressed() { return orButtonBool; }
    public boolean isModButtonPressed() { return modButtonBool; }
    public boolean isXorButtonPressed() { return xorButtonBool; }
    public boolean isNegateButtonPressed() { return negateButtonBool; }
    public boolean isNotButtonPressed() { return notButtonBool; }
    public boolean isAndButtonPressed() { return andButtonBool; }
    public CalcType_v4 getBase() { return base; }

    public boolean isButtonBinSet() { return isButtonBinSet; }
    public boolean isButtonOctSet() { return isButtonOctSet; }
    public boolean isButtonDecSet() { return isButtonDecSet; }
    public boolean isButtonHexSet() { return isButtonHexSet; }
    public boolean isButtonByteSet() { return isButtonByteSet; }
    public boolean isButtonWordSet() { return isButtonWordSet; }
    public boolean isButtonDwordSet() { return isButtonDwordSet; }
    public boolean isButtonQwordSet() { return isButtonQwordSet; }

    public void setLayout(GridBagLayout layout) { this.layout = layout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setValues(String[] values) { this.values = values; }
    public void setValuesPosition(int valuesPosition) { this.valuesPosition = valuesPosition; }
    public void setMemoryValues(String[] memoryValues) { this.memoryValues = memoryValues; }
    public void setMemoryPosition(int memoryPosition) { this.memoryPosition = memoryPosition; }
    public void setTextArea(JTextArea textArea) { this.textArea = textArea; }
    public void setTextarea(StringBuffer textarea) { this.textarea = textarea; }
    public void setCalcType(CalcType_v4 calcType) { this.calcType = calcType; }

    public void setCurrentPanel(JPanel currentPanel) { this.currentPanel = currentPanel; }
    public void setCalculatorImage1(ImageIcon calculatorImage1) { this.calculatorImage1 = calculatorImage1; }
    public void setCalculator2(ImageIcon calculator2) { this.calculator2 = calculator2; }
    public void setMacLogo(ImageIcon macLogo) { this.macLogo = macLogo; }
    public void setBlankImage(ImageIcon blankImage) { this.blankImage = blankImage; }
    public void setIconLabel(JLabel iconLabel) { this.iconLabel = iconLabel; }
    public void setTextLabel(JLabel textLabel) { this.textLabel = textLabel; }
    public void setFirstNumBool(boolean firstNumBool) { this.firstNumBool = firstNumBool; }
    public void setNumberOneNegative(boolean numberOneNegative) { this.numberOneNegative = numberOneNegative; }
    public void setNumberTwoNegative(boolean numberTwoNegative) { this.numberTwoNegative = numberTwoNegative; }
    public void setNumberThreeNegative(boolean numberThreeNegative) { this.numberThreeNegative = numberThreeNegative; }
    public void setNumberIsNegative(boolean numberIsNegative) { this.numberIsNegative = numberIsNegative; }
    public void setMemorySwitchBool(boolean memorySwitchBool) { this.memorySwitchBool = memorySwitchBool; }
    public void setAddBool(boolean addBool) { this.addBool = addBool; }
    public void setSubBool(boolean subBool) { this.subBool = subBool; }
    public void setMulBool(boolean mulBool) { this.mulBool = mulBool; }
    public void setDivBool(boolean divBool) { this.divBool = divBool; }
    public void setMemAddBool(boolean memAddBool) { this.memAddBool = memAddBool; }
    public void setMemSubBool(boolean memSubBool) { this.memSubBool = memSubBool; }
    public void setNegatePressed(boolean negatePressed) { this.negatePressed = negatePressed; }
    public void setDotButtonPressed(boolean dotButtonPressed) { this.dotButtonPressed = dotButtonPressed; }
    public void setButtonBinSet(boolean buttonBinSet) { isButtonBinSet = buttonBinSet; }
    public void setButtonOctSet(boolean buttonOctSet) { isButtonOctSet = buttonOctSet; }
    public void setButtonDecSet(boolean buttonDecSet) { isButtonDecSet = buttonDecSet; }
    public void setButtonHexSet(boolean buttonHexSet) { isButtonHexSet = buttonHexSet; }
    public void setButtonByteSet(boolean buttonByteSet) { isButtonByteSet = buttonByteSet; }
    public void setButtonWordSet(boolean buttonWordSet) { isButtonWordSet = buttonWordSet; }
    public void setButtonDwordSet(boolean buttonDwordSet) { isButtonDwordSet = buttonDwordSet; }
    public void setButtonQwordSet(boolean buttonQwordSet) { isButtonQwordSet = buttonQwordSet; }
    public void setOrButtonBool(boolean orButtonBool) { this.orButtonBool = orButtonBool; }
    public void setModButtonBool(boolean modButtonBool) { this.modButtonBool = modButtonBool; }
    public void setXorButtonBool(boolean xorButtonBool) { this.xorButtonBool = xorButtonBool; }
    public void setNegateButtonBool(boolean negateButtonBool) { this.negateButtonBool = negateButtonBool; }
    public void setNotButtonBool(boolean notButtonBool) { this.notButtonBool = notButtonBool; }
    public void setAndButtonBool(boolean andButtonBool) { this.andButtonBool = andButtonBool; }
    public void setBase(CalcType_v4 base) { this.base = base; }

    public Collection<JButton> getNumberButtons() {
        return Arrays.asList(getButton0(), getButton1(), getButton2(), getButton3(), getButton4(), getButton5(),
                getButton6(), getButton7(), getButton8(), getButton9());
    }

    public void clearVariableNumberOfButtonsFunctionalities(Collection<JButton> buttonsToClearOfTheirFunctionality)
    {
        for(JButton button : buttonsToClearOfTheirFunctionality)
        {
            // Remove action listeners
            for(ActionListener al : button.getActionListeners()) { button.removeActionListener(al); }
            // Remove key listeners
            for (KeyListener kl : button.getKeyListeners()) { button.removeKeyListener(kl); }
        }
    }

    public void logStandardException(Exception e)
    {
        LOGGER.error(e.getCause().getClass().getName() + ": " + e.getMessage());
    }
}


