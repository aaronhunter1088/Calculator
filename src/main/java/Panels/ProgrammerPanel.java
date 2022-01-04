package Panels;

import Calculators.Calculator;
import Types.CalculatorBase;
import Types.CalculatorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Calculators.CalculatorError;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;

public class ProgrammerPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class);
    private static final long serialVersionUID = 1L;

    private GridBagLayout programmerLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    private final ButtonGroup btnGroupOne = new ButtonGroup();
    private final ButtonGroup btnGroupTwo = new ButtonGroup();
    private final JPanel buttonGroup1ButtonPanel = new JPanel(); // contains the first button group
    private final JPanel buttonGroup2ButtonPanel = new JPanel(); // contains the second button group
    final private JButton buttonBlank = new JButton(" "), buttonMod = new JButton("MOD"),
                          buttonLPar = new JButton("("), buttonRPar = new JButton(")"),
                          buttonRol = new JButton ("RoL"), buttonRor = new JButton ("RoR"),
                          buttonOr = new JButton ("OR"), buttonXor = new JButton ("XOR"),
                          buttonLSh = new JButton ("Lsh"), buttonRSh = new JButton ("Rsh"),
                          buttonNot = new JButton ("NOT"), buttonAnd = new JButton ("AND"),
                          buttonA = new JButton("A"), buttonB = new JButton("B"),
                          buttonC = new JButton("C"), buttonD = new JButton("D"),
                          buttonE = new JButton("E"), buttonF = new JButton("F");
    final private JRadioButton buttonHex = new JRadioButton("Hex"), buttonDec = new JRadioButton("Dec"),
                               buttonOct = new JRadioButton("Oct"), buttonBin = new JRadioButton("Bin"),
                               buttonQWord = new JRadioButton("QWord"), buttonDWord = new JRadioButton("DWord"),
                               buttonWord = new JRadioButton("Word"), buttonByte = new JRadioButton("Byte");
    protected Calculator calculator;

    /************* Constructors ******************/
    public ProgrammerPanel() { LOGGER.info("Programmer panel created"); }

    public ProgrammerPanel(Calculator calculator) { this(calculator, null); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator
     */
    public ProgrammerPanel(Calculator calculator, CalculatorBase base) { setupProgrammerPanel(calculator, base); }

    /************* Start of methods here ******************/
    public void setupProgrammerPanel(Calculator calculator, CalculatorBase base)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setMaximumSize(new Dimension(600,400));
        calculator.setCalculatorType(PROGRAMMER);
        calculator.setConverterType(null);
        setupHelpMenu();
        setupProgrammerPanelComponents(base);
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up programmer panel");
    }

    public void setupProgrammerPanelComponents(CalculatorBase base)
    {
        LOGGER.info("Configuring programmer panel buttons");
        calculator.textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        calculator.textArea.setFont(Calculator.font);
        calculator.textArea.setBorder(new LineBorder(Color.BLACK));
        calculator.textArea.setPreferredSize(new Dimension(105, 60)); //70, 35
        calculator.textArea.setEditable(false);
        LOGGER.info("TextArea setup");
        calculator.setupFractionButton();
        calculator.getButtonFraction().addActionListener(this::performFractionButtonActions);
        LOGGER.info("Fraction button configured");
        calculator.setupPercentButton();
        calculator.getButtonPercent().addActionListener(this::performPercentButtonActions);
        LOGGER.info("Percent button configured");
        calculator.setupSquareRootButton();
        calculator.getButtonSqrt().addActionListener(this::performSquareRootButtonActions);
        LOGGER.info("Square Root button configured");
        calculator.getButtonNegate().setEnabled(false);
        LOGGER.info("Negate button not enabled");

        // A - F
        setupButtonsAToF();

        calculator.setupMemoryButtons(); // MR MC MS M+ M-
        calculator.setupAddButton();
        calculator.setupSubtractButton();
        calculator.setupMultiplyButton();
        calculator.setupDivideButton();
        calculator.setupEqualsButton();
        calculator.setupNegateButton(); // = Negate
        calculator.setupDeleteButton();
        calculator.setupClearEntryButton();
        calculator.setupClearButton();
        calculator.setupDotButton();
        setupNumberButtons(true);

        // Set the base
        if (base != null)
        {
            calculator.resetProgrammerByteOperators(false);
            calculator.isButtonByteSet = true;
            if (base == DECIMAL) {
                calculator.setCalculatorBase(DECIMAL);
                getButtonDec().setSelected(true);
                calculator.isButtonDecSet = true;
                setButtons2To9(true);
                setButtonsAToF(false);
            }
            else if (base == OCTAL)
            {
                calculator.setCalculatorBase(base);
                setButtons2To9(true);
                calculator.getButton8().setEnabled(false);
                calculator.getButton9().setEnabled(false);
                setButtonsAToF(false);
                getButtonOct().setSelected(true);
                calculator.isButtonOctSet = true;
            }
            else if (base == HEXADECIMAL) {
                calculator.setCalculatorBase(base);
                setButtons2To9(true);
                setButtonsAToF(true);
                getButtonHex().setSelected(true);
                calculator.isButtonHexSet = true;
            }
            else {
                if (StringUtils.isNotBlank(calculator.values[0])) {
                    calculator.setCalculatorBase(DECIMAL);
                    getButtonDec().setSelected(true);
                    calculator.isButtonDecSet = true;
                    setButtons2To9(true);
                    setButtonsAToF(false);
                } else {
                    // base == binary
                    calculator.setCalculatorBase(BINARY);
                    setButtons2To9(false);
                    setButtonsAToF(false);
                    getButtonBin().setSelected(true);
                    calculator.isButtonBinSet = true;
                }
            }
        }
        else
        {
            if (!calculator.textareaValue.toString().equals(""))
            {
                calculator.setCalculatorBase(DECIMAL);
                getButtonDec().setSelected(true);
                calculator.isButtonDecSet = true;
                setButtons2To9(true);
                setButtonsAToF(false);
            }
            else
            {
                calculator.setCalculatorBase(BINARY);
                setButtons2To9(false);
                setButtonsAToF(false);
                getButtonBin().setSelected(true);
                calculator.isButtonBinSet = true;
            }
        }
        // Set the byte
        getButtonByte().setSelected(true);
        // Set the type
        calculator.setCalculatorType(PROGRAMMER);

        getButtonQWord().addActionListener(this::performByteButtonSwitch);
        getButtonDWord().addActionListener(this::performByteButtonSwitch);
        getButtonWord().addActionListener(this::performByteButtonSwitch);
        getButtonByte().addActionListener(this::performByteButtonSwitch);
        getButtonHex().addActionListener(this::performBaseButtonSwitch);
        getButtonDec().addActionListener(this::performBaseButtonSwitch);
        getButtonOct().addActionListener(this::performBaseButtonSwitch);
        getButtonBin().addActionListener(this::performBaseButtonSwitch);
        btnGroupOne.add(buttonHex);
        btnGroupOne.add(buttonDec);
        btnGroupOne.add(buttonOct);
        btnGroupOne.add(buttonBin);
        // add buttons to ButtonGroupTwo
        btnGroupTwo.add(buttonQWord);
        btnGroupTwo.add(buttonDWord);
        btnGroupTwo.add(buttonWord);
        btnGroupTwo.add(buttonByte);

        buttonBlank.setPreferredSize(new Dimension(35,35));
        buttonBlank.setBorder(new LineBorder(Color.BLACK));

        buttonMod.setFont(Calculator.font);
        buttonMod.setPreferredSize(new Dimension(35,35));
        buttonMod.setBorder(new LineBorder(Color.BLACK));
        buttonMod.addActionListener(action -> {
            performButtonModActions(action);
        });

        buttonLPar.setFont(Calculator.font);
        buttonLPar.setPreferredSize(new Dimension(35,35));
        buttonLPar.setBorder(new LineBorder(Color.BLACK));

        buttonRPar.setFont(Calculator.font);
        buttonRPar.setPreferredSize(new Dimension(35,35));
        buttonRPar.setBorder(new LineBorder(Color.BLACK));

        buttonRol.setFont(Calculator.font);
        buttonRol.setPreferredSize(new Dimension(35,35));
        buttonRol.setBorder(new LineBorder(Color.BLACK));

        buttonRor.setFont(Calculator.font);
        buttonRor.setPreferredSize(new Dimension(35,35));
        buttonRor.setBorder(new LineBorder(Color.BLACK));

        buttonOr.setFont(Calculator.font);
        buttonOr.setPreferredSize(new Dimension(35,35));
        buttonOr.setBorder(new LineBorder(Color.BLACK));
        buttonOr.addActionListener(action -> {
            try {
                performButtonOrActions(action);
            } catch (CalculatorError calculator_ErrorV4) {
                LOGGER.error(calculator_ErrorV4.getMessage());
            }
        });

        buttonXor.setFont(Calculator.font);
        buttonXor.setPreferredSize(new Dimension(35,35));
        buttonXor.setBorder(new LineBorder(Color.BLACK));

        buttonLSh.setFont(Calculator.font);
        buttonLSh.setPreferredSize(new Dimension(35,35));
        buttonLSh.setBorder(new LineBorder(Color.BLACK));

        buttonRSh.setFont(Calculator.font);
        buttonRSh.setPreferredSize(new Dimension(35,35));
        buttonRSh.setBorder(new LineBorder(Color.BLACK));

        buttonNot.setFont(Calculator.font);
        buttonNot.setPreferredSize(new Dimension(35,35));
        buttonNot.setBorder(new LineBorder(Color.BLACK));
        buttonNot.addActionListener(action -> {
            performButtonNotActions(action);
        });

        buttonAnd.setFont(Calculator.font);
        buttonAnd.setPreferredSize(new Dimension(35,35));
        buttonAnd.setBorder(new LineBorder(Color.BLACK));
        LOGGER.info("Programmer buttons configured");
    }

    public void performByteButtonSwitch(ActionEvent action)
    {
        LOGGER.info("Performing byte button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("buttonChoice: " + buttonChoice);
        calculator.resetProgrammerByteOperators(false);
        if (buttonChoice.equals("Byte")) {
            getButtonByte().setEnabled(true);
            calculator.setButtonByte(true);
        } else if (buttonChoice.equals("Word")) {
            getButtonWord().setEnabled(true);
            calculator.setButtonWord(true);
        } else if (buttonChoice.equals("DWord")) {
            getButtonDWord().setEnabled(true);
            calculator.setButtonDWord(true);
        } else if (buttonChoice.equals("QWord")) {
            getButtonQWord().setEnabled(true);
            calculator.setButtonQWord(true);
        }
        calculator.confirm("Pressed: " + buttonChoice);
    }

    public void performBaseButtonSwitch(ActionEvent action)
    {
        LOGGER.info("Changing bases...");
        CalculatorBase previousBase = calculator.getCalculatorBase();
        String baseChoice = action.getActionCommand();
        CalculatorBase newBase = determineCalculatorBase(baseChoice);
        //calculator.setCalculatorBase(newBase);
        String nameOfOperatorPushed = calculator.determineIfProgrammerPanelOperatorWasPushed(); // could be blank
        nameOfOperatorPushed = calculator.determineIfBasicPanelOperatorWasPushed();
        LOGGER.info("Operator {} will be cleared", nameOfOperatorPushed);
        calculator.resetBasicOperators(false);
        calculator.resetProgrammerByteOperators(false);
        resetProgrammerOperators();
        LOGGER.info("baseChoice: " + newBase);
        if (newBase == BINARY)
        {
            calculator.setCalculatorBase(BINARY);
            getButtonBin().setSelected(true);
            calculator.isButtonByteSet = true;
            setButtons2To9(false);
            getButtonsAToF().forEach(button -> button.setEnabled(false));
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
        else if (newBase == OCTAL)
        {
            getButtonOct().setSelected(true);
            calculator.isButtonOctSet = true;
            setButtons2To9(true);
            calculator.getButton8().setEnabled(false);
            calculator.getButton9().setEnabled(false);
            getButtonsAToF().forEach(button -> button.setEnabled(false));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        else if (newBase == DECIMAL)
        {
            //calculator.resetProgrammerByteOperators(false);
            getButtonByte().setSelected(true);
            calculator.setButtonByte(true);
            getButtonDec().setSelected(true);
            calculator.isButtonDecSet = true;
            setButtons2To9(true);
            setButtonsAToF(false);
            calculator.textArea.setText(calculator.addNewLineCharacters(3) + calculator.values[calculator.valuesPosition]);
            calculator.setCalculatorBase(DECIMAL);// we don't always "update textArea", like when it's blank
        }
        else // newBase == HEXIDECIMAL
        {
            getButtonHex().setSelected(true);
            calculator.isButtonHexSet = true;
            setButtons2To9(true);
            getButtonsAToF().forEach(button -> button.setEnabled(true));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        calculator.confirm("Bases changed");
    }

    public void updateTextAreaAfterBaseChange(CalculatorBase previousBase, CalculatorBase newBase, String nameOfOperatorPushed)
    {
        String textAreaValueToConvert = calculator.getTextAreaWithoutAnything();
        if (StringUtils.isNotBlank(textAreaValueToConvert)) {
            if (StringUtils.isEmpty(textAreaValueToConvert)) {
                textAreaValueToConvert = "0";
            }
            try {
                textAreaValueToConvert = calculator.convertFromTypeToTypeOnValues(previousBase, newBase, textAreaValueToConvert);
                if (StringUtils.equals(textAreaValueToConvert, "") || textAreaValueToConvert == null)
                {
                    textAreaValueToConvert = calculator.getTextAreaWithoutAnything();
                }
            }
            catch (CalculatorError c) { calculator.logException(c); }
        } else {
            textAreaValueToConvert = "";
        }
        if (StringUtils.isBlank(nameOfOperatorPushed))
        {
            calculator.textArea.setText(calculator.addNewLineCharacters(3) + textAreaValueToConvert);
        }
        else
        {
            calculator.textArea.setText(calculator.addNewLineCharacters(3) + nameOfOperatorPushed + " " + textAreaValueToConvert);
        }
        calculator.updateTextareaFromTextArea();
    }

    public CalculatorBase determineCalculatorBase(String base)
    {
        CalculatorBase baseToReturn = null;
        switch (base)
        {
            case "Bin" : {
                baseToReturn = BINARY;
                break;
            }
            case "Oct" : {
                baseToReturn = OCTAL;
                break;
            }
            case "Dec" : {
                baseToReturn = DECIMAL;
                break;
            }
            case "Hex" : {
                baseToReturn = HEXADECIMAL;
                break;
            }
            default : { LOGGER.error("Unknown base: " + base); }
        }
        return baseToReturn;
    }

    public void clearBasesFunctionality()
    {
        getJRadioButtonBases().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener));
    }

    public void clearBytesFunctionality()
    {
        getJRadioButtonBytes().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener));
    }

    public Collection<JRadioButton> getJRadioButtonBases()
    {
        return Arrays.asList(getButtonBin(), getButtonOct(), getButtonDec(), getButtonHex());
    }

    public Collection<JRadioButton> getJRadioButtonBytes()
    {
        return Arrays.asList(getButtonByte(), getButtonWord(), getButtonDWord(), getButtonQWord());
    }

    public void addComponentsToPanel()
    {
        LOGGER.info("Starting addComponentsToProgrammerPanel");
        constraints.insets = new Insets(5,0,5,0); //9905
        addComponent(calculator.textArea, 0, 0, 9, 2);
        constraints.insets = new Insets(5,5,5,5);

        buttonGroup1ButtonPanel.setLayout(new GridLayout(4,1));
        // add buttons to panel
        buttonGroup1ButtonPanel.add(buttonHex);
        buttonGroup1ButtonPanel.add(buttonDec);
        buttonGroup1ButtonPanel.add(buttonOct);
        buttonGroup1ButtonPanel.add(buttonBin);
        Border border = buttonGroup1ButtonPanel.getBorder();
        Border margin = new TitledBorder("Base");
        buttonGroup1ButtonPanel.setBorder(new CompoundBorder(border, margin));
        addComponent(buttonGroup1ButtonPanel, 4, 0, 1, 4, GridBagConstraints.HORIZONTAL);

        buttonGroup2ButtonPanel.setLayout(new GridLayout(4,1)); //rows and columns
        // add buttons to panel
        buttonGroup2ButtonPanel.add(buttonQWord);
        buttonGroup2ButtonPanel.add(buttonDWord);
        buttonGroup2ButtonPanel.add(buttonWord);
        buttonGroup2ButtonPanel.add(buttonByte);
        Border border2 = buttonGroup2ButtonPanel.getBorder();
        Border margin2 = new TitledBorder("Byte");
        buttonGroup2ButtonPanel.setBorder(new CompoundBorder(border2, margin2));
        // add panel to Calculator
        addComponent(buttonGroup2ButtonPanel, 7, 0, 1, 4, GridBagConstraints.HORIZONTAL);

        constraints.insets = new Insets(5,0,1,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
        addComponent(buttonBlank, 4, 1, 1, 1);
        addComponent(buttonMod, 4, 2, 1, 1);
        addComponent(buttonA, 4, 3, 1, 1);
        addComponent(calculator.buttonMemoryStore, 4, 4, 1, 1);
        addComponent(calculator.buttonMemoryClear, 4, 5, 1, 1);
        addComponent(calculator.buttonMemoryRecall, 4, 6, 1, 1);
        addComponent(calculator.buttonMemoryAddition, 4, 7, 1, 1);
        addComponent(calculator.buttonMemorySubtraction, 4, 8, 1, 1);

        addComponent(buttonLPar, 5, 1, 1, 1);
        addComponent(buttonRPar, 5, 2, 1, 1);
        addComponent(buttonB, 5, 3, 1, 1);
        addComponent(calculator.buttonDelete, 5, 4, 1, 1);
        addComponent(calculator.buttonClearEntry, 5, 5, 1, 1);
        addComponent(calculator.buttonClear, 5, 6, 1, 1);
        addComponent(calculator.buttonNegate, 5, 7, 1, 1);
        addComponent(calculator.buttonSqrt, 5, 8, 1, 1);

        addComponent(buttonRol, 6, 1, 1, 1);
        addComponent(buttonRor, 6, 2, 1, 1);
        addComponent(buttonC, 6, 3, 1, 1);
        addComponent(calculator.button7, 6, 4, 1, 1);
        addComponent(calculator.button8, 6, 5, 1, 1);
        addComponent(calculator.button9, 6, 6, 1, 1);
        addComponent(calculator.buttonDivide, 6, 7, 1, 1);
        addComponent(calculator.buttonPercent, 6, 8, 1, 1);

        addComponent(buttonOr, 7, 1, 1, 1);
        addComponent(buttonXor, 7, 2, 1, 1);
        addComponent(buttonD, 7, 3, 1, 1);
        addComponent(calculator.button4, 7, 4, 1, 1);
        addComponent(calculator.button5, 7, 5, 1, 1);
        addComponent(calculator.button6, 7, 6, 1, 1);
        addComponent(calculator.buttonMultiply, 7, 7, 1, 1);
        addComponent(calculator.buttonFraction, 7, 8, 1, 1);

        addComponent(buttonLSh, 8, 1, 1, 1);
        addComponent(buttonRSh, 8, 2, 1, 1);
        addComponent(buttonE, 8, 3, 1, 1);
        addComponent(calculator.button1, 8, 4, 1, 1);
        addComponent(calculator.button2, 8, 5, 1, 1);
        addComponent(calculator.button3, 8, 6, 1, 1);
        addComponent(calculator.buttonSubtract, 8, 7, 1, 1);
        addComponent(calculator.buttonEquals, 8, 8, 1, 2);

        addComponent(buttonNot, 9, 1, 1, 1);
        addComponent(buttonAnd, 9, 2, 1, 1);
        addComponent(buttonF, 9, 3, 1, 1);
        addComponent(calculator.getButton0(), 9, 4, 2, 1);
        addComponent(calculator.buttonDot, 9, 6, 1, 1);
        addComponent(calculator.buttonAdd, 9, 7, 1, 1);
        LOGGER.info("Finished addComponentsToProgrammerPanel");
    }

    public void performProgrammerCalculatorTypeSwitchOperations(Calculator calculator, CalculatorBase base )
    {
        LOGGER.info("Switching to the programmer panel...");
        setupProgrammerPanel(calculator, base);
    }

    public void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        calculator.getNumberButtons().forEach(button -> {
            button.setFont(Calculator.font);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
            // setup in the panel, panel specific
        });
    }

    public Collection<JButton> getButtonsAToF() {
        return Arrays.asList(getButtonA(), getButtonB(), getButtonC(), getButtonD(), getButtonE(), getButtonF());
    }

    public void setupButtonsAToF()
    {
        for(JButton hexidecimalButton : getButtonsAToF())
        {
            hexidecimalButton.setFont(Calculator.font);
            hexidecimalButton.setPreferredSize(new Dimension(35, 35));
            hexidecimalButton.setBorder(new LineBorder(Color.BLACK));
            hexidecimalButton.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
        }
    }

    public void setupHelpMenu()
    {
        LOGGER.warn("IMPLEMENT");
    }

    public void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer number button actions");
        LOGGER.info("buttonChoice: " + actionEvent.getActionCommand());
        if (!calculator.firstNumBool)
        {
            calculator.firstNumBool = true;
            calculator.textArea.setText("");
            //calculator.textarea = new StringBuffer();
            calculator.valuesPosition = 1;
        }
        try {
            performProgrammerNumberButtonActions(actionEvent.getActionCommand());
        } catch (CalculatorError c) { calculator.logException(c); }
    }

    public void performProgrammerNumberButtonActions(String buttonChoice) throws CalculatorError
    {
        //calculator.performInitialChecks();
        LOGGER.info("Performing programmer number button actions...");
        LOGGER.info("Update bytes based on length of number in textArea");
        LOGGER.info("Adding '{}' to textArea", buttonChoice);
        if (calculator.getCalculatorBase() == BINARY) {
            int lengthOfTextArea = calculator.getTextAreaWithoutNewLineCharacters().length();
            if (lengthOfTextArea == 8 || lengthOfTextArea == 17 || lengthOfTextArea == 26 || lengthOfTextArea == 35 || lengthOfTextArea == 44)
            {   // add a space add the "end" if the length of the number matches the bytes
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(buttonChoice).append(" ").append(calculator.getTextAreaWithoutNewLineCharacters());
                calculator.textArea.setText(calculator.addNewLineCharacters(3) + newNumber);
                // update the bytes here or when we add next number
                if (calculator.isButtonByteSet()) { calculator.resetProgrammerByteOperators(false); calculator.setButtonWord(true); getButtonWord().setSelected(true); }
                else if (calculator.isButtonWordSet()) { calculator.resetProgrammerByteOperators(false); calculator.setButtonDWord(true); getButtonDWord().setSelected(true); }
                else if (calculator.isButtonDWordSet() && lengthOfTextArea == 17) {
                    calculator.resetProgrammerByteOperators(false);
                    calculator.setButtonQWord(true);
                    getButtonQWord().setSelected(true);
                }
                else if (lengthOfTextArea == 35) {
                    calculator.resetProgrammerByteOperators(false);
                    calculator.setButtonQWord(true);
                    getButtonQWord().setSelected(true);
                }
            }
            else if (lengthOfTextArea >= 53) { LOGGER.info("No more entries aloud"); }
            else {
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(calculator.getTextAreaWithoutNewLineCharacters());
                if (calculator.getTextAreaWithoutNewLineCharacters().contains(" ")) {
                    String[] bytes = calculator.getTextAreaWithoutNewLineCharacters().split(" ");
                    newNumber = new StringBuffer();
                    for (int i = 1; i < bytes.length; i++) {
                        newNumber.append(bytes[i]).append(" "); // 10000000' '
                    }
                    newNumber.append(bytes[0]); // 10000000' '1
                    newNumber.append(buttonChoice); // 10000000' '10
                    bytes[0] = bytes[0] + buttonChoice;
                    bytes = newNumber.toString().split(" "); // '10000000', '10'
                    newNumber = new StringBuffer();
                    for (int i = bytes.length-1; i > 0; i--) {
                        newNumber.append(bytes[i]).append(" "); // 10' '
                    }
                    newNumber.append(bytes[0]); // 10' '10000000
                }
                else {
                    newNumber.append(buttonChoice);
                }
                calculator.textArea.setText(calculator.addNewLineCharacters(3) + newNumber);
            }
        }
        else if (calculator.getCalculatorBase() == DECIMAL) {
            if (!calculator.isFirstNumBool()) // do for second number
            {
                if (!calculator.isDotButtonPressed())
                {
                    calculator.textArea.setText("");
                    if (!calculator.isFirstNumBool()) {
                        calculator.setFirstNumBool(true);
                        calculator.setNumberIsNegative(false);
                    } else
                        calculator.setDotButtonPressed(true);
                    calculator.buttonDot.setEnabled(true);
                }
            }
            calculator.performNumberButtonActions(buttonChoice);
        }
        else if (calculator.getCalculatorBase() == OCTAL) {}
        else /* (calculator.getCalculatorBase() == HEXIDECIMAL */ {}
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
        programmerLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void addComponent(Component c, int row, int column, int width, int height)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = 0;
        constraints.weightx = 0;
        programmerLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    // NOT IMPLEMENTED BUT LOGIC EXISTS AND MAY WORK
    public class BinaryButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonBin.isSelected()) {
                calculator.button2.setEnabled(false);
                calculator.button3.setEnabled(false);
                calculator.button4.setEnabled(false);
                calculator.button5.setEnabled(false);
                calculator.button6.setEnabled(false);
                calculator.button7.setEnabled(false);
                calculator.button8.setEnabled(false);
                calculator.button9.setEnabled(false);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
                // lot more to disable
            }

//            setButtonGroup2Mode();
            // only convert number if textArea has text
            if (!calculator.textArea.getText().equals("")) {
                try {
                    convertValue0AndDisplayInTextArea();
                } catch (CalculatorError ex) {
                    calculator.logException(ex);
                }
            }
        }
    }
    public class ButtonOctHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonOct.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(false);
                calculator.button9.setEnabled(false);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToOctal();
        }
    }
    public class ButtonDecHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonDec.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(true);
                calculator.button9.setEnabled(true);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToDecimal();
        }
    }
    public class ButtonHexidecimalHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonHex.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(true);
                calculator.button9.setEnabled(true);
                buttonA.setEnabled(true);
                buttonB.setEnabled(true);
                buttonC.setEnabled(true);
                buttonD.setEnabled(true);
                buttonE.setEnabled(true);
                buttonF.setEnabled(true);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ButtonByteHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 8) {
                if (calculator.textArea.getText().equals(""))
                    calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(8,16));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    public class ButtonWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 16) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,16));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    public class ButtonDWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 32) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,32));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    public class ButtonQWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 64) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,64));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    public class OctalButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonOct.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(false);
                calculator.button9.setEnabled(false);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToOctal();
        }
    }
    public class DecimalButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonDec.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(true);
                calculator.button9.setEnabled(true);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToDecimal();
        }
    }
    public class HexidecimalButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonHex.isSelected()){
                calculator.button2.setEnabled(true);
                calculator.button3.setEnabled(true);
                calculator.button4.setEnabled(true);
                calculator.button5.setEnabled(true);
                calculator.button6.setEnabled(true);
                calculator.button7.setEnabled(true);
                calculator.button8.setEnabled(true);
                calculator.button9.setEnabled(true);
                buttonA.setEnabled(true);
                buttonB.setEnabled(true);
                buttonC.setEnabled(true);
                buttonD.setEnabled(true);
                buttonE.setEnabled(true);
                buttonF.setEnabled(true);
            }
            if (!calculator.textArea.getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ByteButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 8) {
                if (calculator.textArea.getText().equals(""))
                    calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(8,16));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    public class WordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 16) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,16));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    
    public class DWordButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 32) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,32));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }
    
    public class QWordButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.textArea.getText().length() > 64) {
                calculator.textareaValue = new StringBuffer().append(calculator.textArea.getText());
                calculator.textareaValue = new StringBuffer().append(calculator.textareaValue.substring(0,64));
                calculator.textArea.setText(calculator.textareaValue.toString());
            }
        }
    }

    // TODO: Broken
    public void performButtonNotActions(ActionEvent action)
    {
        LOGGER.info("performing not operation...");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        calculator.setNotButtonBool(false);
        getButtonNot().setEnabled(false);
        calculator.textareaValue = new StringBuffer(calculator.textArea.getText().replaceAll("\n", ""));
        LOGGER.debug("before operation execution: " + calculator.textareaValue.toString());
        StringBuffer newBuffer = new StringBuffer();
        for (int i = 0; i < calculator.textareaValue.length(); i++) {
            String s = Character.toString(calculator.textareaValue.charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: " + newBuffer);
        calculator.textareaValue = new StringBuffer(newBuffer);
        calculator.textArea.setText("\n"+calculator.textareaValue.toString());
        LOGGER.info("not operation completed.");
    }

    public void performButtonXorActions(ActionEvent action)
    {
        LOGGER.info("performing XOR button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        calculator.setXorButtonBool(true);
        getButtonXor().setEnabled(false);
        if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1])) {

            calculator.confirm("No values set");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            calculator.textArea.setText(calculator.addNewLineCharacters(1)+
                    calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " " + "XOR");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            performXor();
        }
    }

    public void performButtonOrActions(ActionEvent action) throws CalculatorError
    {
        LOGGER.info("performOrLogic starts here");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        calculator.setOrButtonBool(true);
        getButtonOr().setEnabled(false);
        if (StringUtils.isEmpty(calculator.values[0]) && StringUtils.isNotEmpty(calculator.values[1]))
        {
            String msg = "calculator.values[1] is set and not calculator.values[0]. This is not allowed.";
            throw new CalculatorError(msg);
        }
        else if (StringUtils.isNotEmpty(calculator.values[0]) && StringUtils.isEmpty(calculator.values[1]))
        {
            LOGGER.debug("values[0] is set, but not values[1]");
            calculator.firstNumBool = false;
            calculator.textArea.setText(calculator.addNewLineCharacters(1)
                                                  + buttonChoice + " " + calculator.getValues()[0]);
            calculator.setTextareaValue(new StringBuffer().append(calculator.getValues()[0] + " " + buttonChoice));
            calculator.valuesPosition++;
            calculator.confirm("OR complete");
        }
        else if (StringUtils.isEmpty(calculator.values[0]) && StringUtils.isEmpty(calculator.values[1]))
        {
            calculator.orButtonBool = false;
            calculator.firstNumBool = true;
            calculator.confirm("Pressed OR. Doing nothing");
        }
        else if (!StringUtils.isEmpty(calculator.values[0]) && !StringUtils.isEmpty(calculator.values[1]))
        {
            String sb = performOr(); // 2 OR 3 OR button presses
            //TODO: need to convert sb to DECIMAL form before storing in values
            calculator.values[0] = sb;
            calculator.textArea.setText(calculator.addNewLineCharacters(1)+calculator.values[0]);
            calculator.orButtonBool = false;
            calculator.valuesPosition = 0;
        }
    }

    public void performButtonModActions(ActionEvent action)
    {
        LOGGER.debug("performModButtonActions begins");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("button: " + buttonChoice);
        LOGGER.debug("is it enabled? " + getButtonMod().isEnabled() + " Setting to false.");
        calculator.setModButtonBool(true);
        getButtonMod().setEnabled(false);
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
            calculator.textArea.setText(calculator.textArea.getText() + " " + buttonChoice);
            calculator.updateTextareaFromTextArea();
//            calculator.getValues()[0] = calculator.getTextAreaWithoutNewLineCharacters();
            LOGGER.debug("setting setModButtonBool to true");
            calculator.setModButtonBool(true);
            calculator.setFirstNumBool(false);
        }
        calculator.confirm("Modulus Actions finished");
    }

    public void resetProgrammerOperators()
    {
        getButtonMod().setEnabled(true);
        getButtonOr().setEnabled(true);
        getButtonXor().setEnabled(true);
        getButtonNot().setEnabled(true);
        getButtonAnd().setEnabled(true);

        calculator.setModButtonBool(false);
        calculator.setOrButtonBool(false);
        calculator.setXorButtonBool(false);
        calculator.setNotButtonBool(false);
        calculator.setAndButtonBool(false);
    }

    public void addBytesToTextArea()
    {
        // the first two rows in the programmer calculator are reserved for bytes
        if (getButtonByte().isSelected())
        {
            calculator.textArea.setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            calculator.textArea.setWrapStyleWord(true);
        }
        else if (getButtonWord().isSelected())
        {
            calculator.textArea.setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            calculator.textArea.setWrapStyleWord(true);
        }
        else if (getButtonDWord().isSelected())
        {
            calculator.textArea.setText(
                    calculator.addNewLineCharacters(2) +
                    "00000000 00000000 00000000 00000000"+
                    calculator.addNewLineCharacters(1) +
                    calculator.getValues()[3]+" 00000000 00000000 00000000");
            calculator.textArea.setWrapStyleWord(true);
        }
        else if (getButtonQWord().isSelected())
        {
            calculator.textArea.setText(
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 00000000\n" +
                            "00000000 00000000 00000000 00000000\n"+calculator.getValues()[3]+" 00000000 00000000 00000000");
            calculator.textArea.setWrapStyleWord(true);
        }
    }

    /**
     * Converts all values, and restores in values
     */
    public void convertValue0AndDisplayInTextArea() throws CalculatorError
    {
        LOGGER.info("Converting values");
        String convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[calculator.valuesPosition]);
        calculator.textArea.setText(calculator.addNewLineCharacters(3) + convertedValue);
        LOGGER.info("Values converted");
    }

    public void convertToOctal()
    {
        if (calculator.isButtonBinSet == true) {
            // logic for Binary to Octal
        } else if (calculator.isButtonDecSet == true) {
            // logic for Decimal to Octal
        } else if (calculator.isButtonHexSet == true) {
            // logic for Hexadecimal to Octal
        }
    }

    public void convertToDecimal()
    {
        LOGGER.debug("convertToDecimal starting");
        CalculatorBase previousBase = calculator.getCalculatorBase();
        // update the current base to binary
        calculator.setCalculatorBase(DECIMAL);
        CalculatorBase currentBase = calculator.getCalculatorBase();
        LOGGER.info("previous base: " + previousBase);
        LOGGER.info("current base: " + currentBase);
        String nameOfButton = calculator.determineIfProgrammerPanelOperatorWasPushed(); // could be null
        if (previousBase == DECIMAL && currentBase == BINARY ||
                calculator.getCalculatorType() == CalculatorType.BASIC)
        {
            try {
                calculator.values[0] = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[0]);
                calculator.values[1] = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[1]);
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (previousBase == BINARY && currentBase == DECIMAL)
        {
        }
        else
        if (calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace().equals("")) { return; }
        if (getButtonBin().isSelected()) {
            // logic for Binary to Decimal
            try {
                calculator.textArea.setText(calculator.addNewLineCharacters(1)+
                        calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL,
                                calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (calculator.isButtonOctSet == true) {
            // logic for Octal to Decimal
        } else if (calculator.isButtonHexSet == true) {
            // logic for Hexadecimal to Decimal
        } else if (getButtonDec().isSelected()) {
            if (calculator.textArea.getText().equals("")) { return; }
            String operator = calculator.determineIfProgrammerPanelOperatorWasPushed();
            if (!operator.equals(""))
            {
                calculator.textArea.setText(calculator.addNewLineCharacters(3)+operator+" "+calculator.values[calculator.valuesPosition]);
            }
            else
            {
                calculator.textArea.setText(calculator.addNewLineCharacters(3)+calculator.values[calculator.valuesPosition]);
            }
            calculator.updateTextareaFromTextArea();
        }

        LOGGER.info("convertToDecimal finished");
    }

    public void convertToHexadecimal()
    {
        if (calculator.isButtonBinSet == true) {
            // logic for Binary to Hexadecimal
        } else if (calculator.isButtonOctSet == true) {
            // logic for Octal to Hexadecimal
        } else if (calculator.isButtonDecSet == true) {
            // logic for Decimal to Hexadecimal
        }
    }

    /**
     * This method resets textArea & text area
     */
    @Deprecated(since = "use updateTextAreaUsingBaseAndType")
    public void convertTextArea()
    {
        if (calculator.getCalculatorType() == CalculatorType.BASIC)
        {
            LOGGER.debug("Going from Basic to Programmer");
            calculator.performInitialChecks();
            boolean operatorWasPushed = calculator.determineIfMainOperatorWasPushedBoolean();
            String convertedValue = "";
            try { convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[0]); }
            catch (CalculatorError c) { calculator.logException(c); }
            if (StringUtils.isNotBlank(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()))
            {
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator
                {
                    if (calculator.addBool)
                    {
                        calculator.textArea.setText(calculator.addNewLineCharacters(1)
                                + " + " + convertedValue);
                        calculator.setTextareaValue(new StringBuffer().append(convertedValue + " +"));
                    }
                    else if (calculator.subBool)
                    {
                        calculator.textArea.setText(calculator.addNewLineCharacters(1)
                                + " - " + convertedValue);
                        calculator.setTextareaValue(new StringBuffer().append(convertedValue + " -"));
                    }
                    else if (calculator.mulBool)
                    {
                        calculator.textArea.setText(calculator.addNewLineCharacters(1)
                                + " * " + convertedValue);
                        calculator.setTextareaValue(new StringBuffer().append(convertedValue + " *"));
                    }
                    else if (calculator.divBool)
                    {
                        calculator.textArea.setText(calculator.addNewLineCharacters(1)
                                + " / " + convertedValue);
                        calculator.setTextareaValue(new StringBuffer().append(convertedValue + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but textArea has some value
                {
                    calculator.textArea.setText(calculator.addNewLineCharacters(1)
                            + convertedValue);
                    calculator.setTextareaValue(new StringBuffer().append(convertedValue));
                }
            }
        }
        // TODO: add logic for Scientific
    }

    public void performModulus()
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

    public String performOr()
    {
        LOGGER.debug("performing Or");
        StringBuffer sb = new StringBuffer();
        //TODO: if values[0] and values[1] in decimal and difference length, this will fail.
        //In order for this to work, we need to convert to most appropriate base
        //Check to make sure both are same length

        for (int i=0; i<calculator.values[0].length(); i++)
        {
            String letter = "0";
            if (String.valueOf(calculator.values[0].charAt(i)).equals("0") &&
                String.valueOf(calculator.values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(calculator.values[0].charAt(i)+" OR "+calculator.values[1].charAt(i)+" = "+ letter);
        }
        calculator.getValues()[3] = sb.toString();
        //calculator.convertAllValuesToDecimal();
        LOGGER.info(calculator.values[0]+" OR "+calculator.values[1]+" = "+ calculator.values[3]);
        calculator.getValues()[0] = calculator.getValues()[3];
        getButtonOr().setEnabled(true);
        return String.valueOf(sb);
    }

    public String performXor()
    {
        LOGGER.info("performing Xor");
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<calculator.values[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(calculator.values[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(String.valueOf(calculator.values[0].charAt(i))+" + "+String.valueOf(calculator.values[1].charAt(i))+
                    " = "+ letter);
        }
        return String.valueOf(sb);
    }

    public void performFractionButtonActions(ActionEvent action) { calculator.confirm("Button not enabled for programmer panel"); }

    public void performPercentButtonActions(ActionEvent action) { calculator.confirm("Button not enabled for programmer panel"); }

    public void performSquareRootButtonActions(ActionEvent action) { calculator.confirm("Button not enabled for programmer panel"); }

    public void setButtons2To9(boolean isEnabled)
    {
        Collection<JButton> buttonsWithout0Or1 = calculator.getNumberButtons();
        buttonsWithout0Or1.removeIf(btn -> btn.getName().equals("0") || btn.getName().equals("1"));
        buttonsWithout0Or1.forEach(button -> button.setEnabled(isEnabled));
    }

    public void setButtonsAToF(boolean isEnabled)
    { getButtonsAToF().forEach(button -> button.setEnabled(isEnabled)); }

    public void setTheBytesBasedOnTheNumbersLength(String number)
    {
        LOGGER.info("setting the bytes based on the length of the number");
        int lengthOfNumber = number.length();
        calculator.resetProgrammerByteOperators(false);
        if (lengthOfNumber <= 8) { getButtonByte().setSelected(true); calculator.isButtonByteSet = true; }
        else if (lengthOfNumber <= 16) { getButtonWord().setSelected(true); calculator.isButtonWordSet = true; }
        else if (lengthOfNumber <= 32) { getButtonDWord().setSelected(true); calculator.isButtonDWordSet = true; }
        else { getButtonQWord().setSelected(true); calculator.isButtonQWordSet = true; }
        LOGGER.info("isButtonByteSet: " + calculator.isButtonByteSet);
        LOGGER.info("isButtonWordSet: " + calculator.isButtonWordSet);
        LOGGER.info("isButtonDWordSet: " + calculator.isButtonDWordSet);
        LOGGER.info("isButtonQWordSet: " + calculator.isButtonQWordSet);
    }

    /************* All Getters and Setters ******************/
    @Override
    public GridBagLayout getLayout() { return programmerLayout; }
    public ButtonGroup getBtnGroupOne() { return btnGroupOne; }
    public ButtonGroup getBtnGroupTwo() { return btnGroupTwo; }
    public JPanel getButtonGroup1ButtonPanel() { return buttonGroup1ButtonPanel; }
    public JPanel getButtonGroup2ButtonPanel() { return buttonGroup2ButtonPanel; }
    public JButton getButtonBlank() { return buttonBlank; }
    public JButton getButtonMod() { return buttonMod; }
    public JButton getButtonLPar() { return buttonLPar; }
    public JButton getButtonRPar() { return buttonRPar; }
    public JButton getButtonRol() { return buttonRol; }
    public JButton getButtonRor() { return buttonRor; }
    public JButton getButtonOr() { return buttonOr; }
    public JButton getButtonXor() { return buttonXor; }
    public JButton getButtonLSh() { return buttonLSh; }
    public JButton getButtonRSh() { return buttonRSh; }
    public JButton getButtonNot() { return buttonNot; }
    public JButton getButtonAnd() { return buttonAnd; }
    public JButton getButtonA() { return buttonA; }
    public JButton getButtonB() { return buttonB; }
    public JButton getButtonC() { return buttonC; }
    public JButton getButtonD() { return buttonD; }
    public JButton getButtonE() { return buttonE; }
    public JButton getButtonF() { return buttonF; }
    public JRadioButton getButtonHex() { return buttonHex; }
    public JRadioButton getButtonDec() { return buttonDec; }
    public JRadioButton getButtonOct() { return buttonOct; }
    public JRadioButton getButtonBin() { return buttonBin; }
    public JRadioButton getButtonQWord() { return buttonQWord; }
    public JRadioButton getButtonDWord() { return buttonDWord; }
    public JRadioButton getButtonWord() { return buttonWord; }
    public JRadioButton getButtonByte() { return buttonByte; }
    public Calculator getCalculator() { return calculator; }

    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
        this.programmerLayout = panelLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }


}
