package version4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import static version4.CalculatorType_v4.*;
import static version4.CalculatorBase_v4.*;

public class JPanelProgrammer_v4 extends JPanel {

    protected final static Logger LOGGER;
    static {
        LOGGER = LogManager.getLogger(JPanelProgrammer_v4.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout panelLayout; // layout of the calculator
    private GridBagLayout otherButtonLayout = new GridBagLayout();
    private JPanel allOtherButtonsPanel = new JPanel(otherButtonLayout);
    private GridBagConstraints constraints; // layout's constraints

    private final ButtonGroup btnGroupOne = new ButtonGroup();
    private final ButtonGroup btnGroupTwo = new ButtonGroup();
    private final JPanel buttonGroup1ButtonPanel = new JPanel(); // contains the first button group
    private final JPanel buttonGroup2ButtonPanel = new JPanel(); // contains the second button group

    protected JLabel topLeftBytesLabel, topRightBytesLabel, bottomLeftBytesLabel, bottomRightBytesLabel;
    final private String TEXTAREA3_SPACE = "\t";

    final private JButton button = new JButton(" ");
    final private JButton buttonMod = new JButton("MOD");
    final private JButton buttonLPar = new JButton("(");
    final private JButton buttonRPar = new JButton(")");
    final private JButton buttonRol = new JButton ("RoL");
    final private JButton buttonRor = new JButton ("RoR");
    final private JButton buttonOr = new JButton ("OR");
    final private JButton buttonXor = new JButton ("XOR");
    final private JButton buttonLSh = new JButton ("Lsh");
    final private JButton buttonRSh = new JButton ("Rsh");
    final private JButton buttonNot = new JButton ("NOT");
    final private JButton buttonAnd = new JButton ("AND");
    // A - F
    final private JButton buttonA = new JButton("A");
    final private JButton buttonB = new JButton("B");
    final private JButton buttonC = new JButton("C");
    final private JButton buttonD = new JButton("D");
    final private JButton buttonE = new JButton("E");
    final private JButton buttonF = new JButton("F");
    final private JRadioButton buttonHex = new JRadioButton("Hex");
    final private JRadioButton buttonDec = new JRadioButton("Dec");
    final private JRadioButton buttonOct = new JRadioButton("Oct");
    final private JRadioButton buttonBin = new JRadioButton("Bin");
    final private JRadioButton buttonQWord = new JRadioButton("Qword");
    final private JRadioButton buttonDWord = new JRadioButton("Dword");
    final private JRadioButton buttonWord = new JRadioButton("Word");
    final private JRadioButton buttonByte = new JRadioButton("Byte");
    final private JButton buttonFraction = new JButton("1/x");
    final private JButton buttonPercent = new JButton("%");
    final private JButton buttonSqrt = new JButton("\u221A");
    protected StringBuffer conversion = new StringBuffer();
    protected Calculator_v4 calculator;

    /************* Constructors ******************/
    public JPanelProgrammer_v4() {}

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator
     */
    public JPanelProgrammer_v4(Calculator_v4 calculator) throws CalculatorError_v4
    {
        setCalculator(calculator);
        setMinimumSize(new Dimension(600,400));
        setPanelLayout(new GridBagLayout());
        setLayout(getPanelLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setupProgrammerPanel();
        setupHelpMenu();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        getLogger().info("Finished setting up programmer panel");
    }

    /************* Start of methods here ******************/
    public void setupProgrammerPanel() throws CalculatorError_v4
    {
        getLogger().info("Configuring programmer panel buttons");
        getCalculator().getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        getCalculator().getTextArea().setFont(Calculator_v4.font);
        getCalculator().getTextArea().setBorder(new LineBorder(Color.BLACK));
        getCalculator().getTextArea().setPreferredSize(new Dimension(105, 60)); //70, 35
        getCalculator().getTextArea().setEditable(false);
        getLogger().info("TextArea setup");
        getButtonFraction().setFont(Calculator_v4.font);
        getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        getButtonFraction().setEnabled(false);
        getButtonFraction().addActionListener(this::performFractionButtonActions);
        LOGGER.info("Fraction button configured");
        getButtonPercent().setFont(Calculator_v4.font);
        getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        getButtonPercent().setEnabled(false);
        getButtonPercent().addActionListener(this::performPercentButtonActions);
        LOGGER.info("Percent button configured");
        getButtonSqrt().setFont(Calculator_v4.font);
        getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        getButtonSqrt().setEnabled(false);
        getButtonSqrt().addActionListener(this::performSquareRootButtonActions);
        LOGGER.info("Square Root button configured");
        getCalculator().getButtonNegate().setEnabled(false);
        LOGGER.info("Negate button not enabled");

        // A - F
        setupButtonsAToF();

        getCalculator().setupMemoryButtons(); // MR MC MS M+ M-
        getCalculator().setupBasicCalculatorOperationButtons(); // + - * /
        getCalculator().setupOtherBasicCalculatorButtons(); // = Negate
        getCalculator().setupOtherCalculatorButtons(); // C CE DEL Dot
        setupNumberButtons(true);

        // Set the base
        if (StringUtils.isNotBlank(calculator.values[0]))
        {
            calculator.setCalculatorBase(DECIMAL);
            getButtonDec().setSelected(true);
            calculator.isButtonDecSet = true;
            setButtons2To9(true);
            setButtonsAToF(false);
        }
        else
        {
            getCalculator().setCalculatorBase(BINARY);
            setButtons2To9(false);
            setButtonsAToF(false);
            getButtonBin().setSelected(true);
        }
        // Set the byte
        getButtonByte().setSelected(true);
        // Set the type
        getCalculator().setCalculatorType(PROGRAMMER);
        // add buttons to buttonGroupOne
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

        button.setPreferredSize(new Dimension(35,35));
        button.setBorder(new LineBorder(Color.BLACK));

        buttonMod.setFont(Calculator_v4.font);
        buttonMod.setPreferredSize(new Dimension(35,35));
        buttonMod.setBorder(new LineBorder(Color.BLACK));
        buttonMod.addActionListener(action -> {
            performButtonModActions(action);
        });

        buttonLPar.setFont(Calculator_v4.font);
        buttonLPar.setPreferredSize(new Dimension(35,35));
        buttonLPar.setBorder(new LineBorder(Color.BLACK));

        buttonRPar.setFont(Calculator_v4.font);
        buttonRPar.setPreferredSize(new Dimension(35,35));
        buttonRPar.setBorder(new LineBorder(Color.BLACK));

        buttonRol.setFont(Calculator_v4.font);
        buttonRol.setPreferredSize(new Dimension(35,35));
        buttonRol.setBorder(new LineBorder(Color.BLACK));

        buttonRor.setFont(Calculator_v4.font);
        buttonRor.setPreferredSize(new Dimension(35,35));
        buttonRor.setBorder(new LineBorder(Color.BLACK));

        buttonOr.setFont(Calculator_v4.font);
        buttonOr.setPreferredSize(new Dimension(35,35));
        buttonOr.setBorder(new LineBorder(Color.BLACK));
        buttonOr.addActionListener(action -> {
            try {
                performButtonOrActions(action);
            } catch (CalculatorError_v4 calculator_ErrorV4) {
                LOGGER.error(calculator_ErrorV4.getMessage());
            }
        });

        buttonXor.setFont(Calculator_v4.font);
        buttonXor.setPreferredSize(new Dimension(35,35));
        buttonXor.setBorder(new LineBorder(Color.BLACK));

        buttonLSh.setFont(Calculator_v4.font);
        buttonLSh.setPreferredSize(new Dimension(35,35));
        buttonLSh.setBorder(new LineBorder(Color.BLACK));

        buttonRSh.setFont(Calculator_v4.font);
        buttonRSh.setPreferredSize(new Dimension(35,35));
        buttonRSh.setBorder(new LineBorder(Color.BLACK));

        buttonNot.setFont(Calculator_v4.font);
        buttonNot.setPreferredSize(new Dimension(35,35));
        buttonNot.setBorder(new LineBorder(Color.BLACK));
        buttonNot.addActionListener(action -> {
            performButtonNotActions(action);
        });

        buttonAnd.setFont(Calculator_v4.font);
        buttonAnd.setPreferredSize(new Dimension(35,35));
        buttonAnd.setBorder(new LineBorder(Color.BLACK));
        LOGGER.info("Programmer buttons configured");
    }

    public void performBaseButtonSwitch(ActionEvent action)
    {
        getLogger().info("Changing bases...");
        CalculatorBase_v4 previousBase = calculator.getCalculatorBase();
        String baseChoice = action.getActionCommand();
        CalculatorBase_v4 newBase = determineCalculatorBase(baseChoice);
        //calculator.setCalculatorBase(newBase);
        String nameOfOperatorPushed = calculator.determineIfProgrammerPanelOperatorWasPushed(); // could be blank
        nameOfOperatorPushed = calculator.determineIfBasicPanelOperatorWasPushed();
        getLogger().info("baseChoice: " + newBase);
        if (newBase == BINARY)
        {
            getButtonBin().setSelected(true);
            calculator.isButtonBinSet = true;
            setButtons2To9(false);
            getButtonsAToF().forEach(button -> button.setEnabled(false));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        else if (newBase == OCTAL)
        {
            getButtonOct().setSelected(true);
            calculator.isButtonOctSet = true;
            setButtons2To9(true);
            getCalculator().getButton8().setEnabled(false);
            getCalculator().getButton9().setEnabled(false);
            getButtonsAToF().forEach(button -> button.setEnabled(false));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        else if (newBase == DECIMAL)
        {
            getButtonDec().setSelected(true);
            calculator.isButtonDecSet = true;
            setButtons2To9(true);
            setButtonsAToF(false);
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
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

    public void updateTextAreaAfterBaseChange(CalculatorBase_v4 previousBase, CalculatorBase_v4 newBase, String nameOfOperatorPushed)
    {
        String textAreaValueToConvert = calculator.getTextAreaWithoutAnything();
        if (StringUtils.isNotBlank(textAreaValueToConvert)) {
            if (StringUtils.isEmpty(textAreaValueToConvert)) {
                textAreaValueToConvert = "0";
            }
            try {
                textAreaValueToConvert = calculator.convertFromTypeToTypeOnValues(previousBase, newBase, textAreaValueToConvert)[0];
                if (StringUtils.equals(textAreaValueToConvert, "") || textAreaValueToConvert == null)
                {
                    textAreaValueToConvert = calculator.getTextAreaWithoutAnything();
                }
            }
            catch (CalculatorError_v4 c) { calculator.logException(c); }
        } else {
            textAreaValueToConvert = "";
        }
        if (StringUtils.isBlank(nameOfOperatorPushed))
        {
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + textAreaValueToConvert);
        }
        else
        {
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + nameOfOperatorPushed + " " + textAreaValueToConvert);
        }
        calculator.updateTextareaFromTextArea();
    }

    public CalculatorBase_v4 determineCalculatorBase(String base)
    {
        CalculatorBase_v4 baseToReturn = null;
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
                baseToReturn = HEXIDECIMAL;
                break;
            }
            default : { getLogger().error("Unknown base: " + base); }
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
        //constraints.insets = new Insets(5,5,5,5);
        //addComponent(getTopLeftBytesLabel(), 0,0, 0, 1);
        //addComponent(getTopRightBytesLabel(), 0, 5, 0, 1);
        constraints.insets = new Insets(5,0,5,0); //9905
        addComponent(getCalculator().getTextArea(), 0, 0, 9, 2);
        constraints.insets = new Insets(5,5,5,5);
        //addComponent(getBottomLeftBytesLabel(), 1, 0, 0, 1);
        //addComponent(getBottomRightBytesLabel(), 1, 5, 0, 1);
        buttonGroup1ButtonPanel.setLayout(new GridLayout(4,1));
        // add buttons to panel
        buttonGroup1ButtonPanel.add(buttonHex);
        buttonGroup1ButtonPanel.add(buttonDec);
        buttonGroup1ButtonPanel.add(buttonOct);
        buttonGroup1ButtonPanel.add(buttonBin);
        buttonBin.addActionListener(action -> {});
        buttonOct.addActionListener(action -> {});
        buttonDec.addActionListener(action -> {});
        buttonHex.addActionListener(action -> {});
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

        buttonQWord.addActionListener(action -> {});
        buttonDWord.addActionListener(action -> {});
        buttonWord.addActionListener(action -> {});
        buttonByte.addActionListener(action -> {});
        Border border2 = buttonGroup2ButtonPanel.getBorder();
        Border margin2 = new TitledBorder("Byte");
        buttonGroup2ButtonPanel.setBorder(new CompoundBorder(border2, margin2));
        // add panel to Calculator
        addComponent(buttonGroup2ButtonPanel, 8, 0, 1, 4);

        constraints.insets = new Insets(5,0,1,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
        setComponent(button, 0, 0, 1, 1, otherButtonLayout);
        setComponent(buttonMod, 0, 1, 1, 1, otherButtonLayout);
        setComponent(buttonA, 0, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryStore, 0, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryClear, 0, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryRecall, 0, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryAddition, 0, 6, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemorySubtraction, 0, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(button);
        allOtherButtonsPanel.add(buttonMod);
        allOtherButtonsPanel.add(buttonA);
        allOtherButtonsPanel.add(getCalculator().buttonMemoryClear);
        allOtherButtonsPanel.add(getCalculator().buttonMemoryRecall);
        allOtherButtonsPanel.add(getCalculator().buttonMemoryStore);
        allOtherButtonsPanel.add(getCalculator().buttonMemoryAddition);
        allOtherButtonsPanel.add(getCalculator().buttonMemorySubtraction);

        setComponent(buttonLPar, 1, 0, 1, 1, otherButtonLayout);
        setComponent(buttonRPar, 1, 1, 1, 1, otherButtonLayout);
        setComponent(buttonB, 1, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonDelete, 1, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonClearEntry, 1, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonClear, 1, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonNegate, 1, 6, 1, 1, otherButtonLayout);
        setComponent(buttonSqrt, 1, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonLPar);
        allOtherButtonsPanel.add(buttonRPar);
        allOtherButtonsPanel.add(buttonB);
        allOtherButtonsPanel.add(getCalculator().buttonDelete);
        allOtherButtonsPanel.add(getCalculator().buttonClearEntry);
        allOtherButtonsPanel.add(getCalculator().buttonClear);
        allOtherButtonsPanel.add(getCalculator().buttonNegate);
        allOtherButtonsPanel.add(buttonSqrt);

        setComponent(buttonRol, 2, 0, 1, 1, otherButtonLayout);
        setComponent(buttonRor, 2, 1, 1, 1, otherButtonLayout);
        setComponent(buttonC, 2, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button7, 2, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button8, 2, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button9, 2, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonDivide, 2, 6, 1, 1, otherButtonLayout);
        setComponent(buttonPercent, 2, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonRol);
        allOtherButtonsPanel.add(buttonRor);
        allOtherButtonsPanel.add(buttonC);
        allOtherButtonsPanel.add(getCalculator().button7);
        allOtherButtonsPanel.add(getCalculator().button8);
        allOtherButtonsPanel.add(getCalculator().button9);
        allOtherButtonsPanel.add(getCalculator().buttonDivide);
        allOtherButtonsPanel.add(buttonPercent);

        setComponent(buttonOr, 3, 0, 1, 1, otherButtonLayout);
        setComponent(buttonXor, 3, 1, 1, 1, otherButtonLayout);
        setComponent(buttonD, 3, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button4, 3, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button5, 3, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button6, 3, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMultiply, 3, 6, 1, 1, otherButtonLayout);
        setComponent(buttonFraction, 3, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonOr);
        allOtherButtonsPanel.add(buttonXor);
        allOtherButtonsPanel.add(buttonD);
        allOtherButtonsPanel.add(getCalculator().button4);
        allOtherButtonsPanel.add(getCalculator().button5);
        allOtherButtonsPanel.add(getCalculator().button6);
        allOtherButtonsPanel.add(getCalculator().buttonMultiply);
        allOtherButtonsPanel.add(buttonFraction);

        setComponent(buttonLSh, 4, 0, 1, 1, otherButtonLayout);
        setComponent(buttonRSh, 4, 1, 1, 1, otherButtonLayout);
        setComponent(buttonE, 4, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button1, 4, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button2, 4, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().button3, 4, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonSubtract, 4, 6, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonEquals, 4, 7, 1, 2, otherButtonLayout);
        allOtherButtonsPanel.add(buttonLSh);
        allOtherButtonsPanel.add(buttonRSh);
        allOtherButtonsPanel.add(buttonE);
        allOtherButtonsPanel.add(getCalculator().button1);
        allOtherButtonsPanel.add(getCalculator().button2);
        allOtherButtonsPanel.add(getCalculator().button3);
        allOtherButtonsPanel.add(getCalculator().buttonSubtract);
        allOtherButtonsPanel.add(getCalculator().buttonEquals);

        setComponent(buttonNot, 5, 0, 1, 1, otherButtonLayout);
        setComponent(buttonAnd, 5, 1, 1, 1, otherButtonLayout);
        setComponent(buttonF, 5, 2, 1, 1, otherButtonLayout);
        setComponent(getCalculator().getButton0(), 5, 3, 2, 1, otherButtonLayout);
        setComponent(getCalculator().buttonDot, 5, 5, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonAdd, 5, 6, 1, 2, otherButtonLayout);

        allOtherButtonsPanel.add(buttonNot);
        allOtherButtonsPanel.add(buttonAnd);
        allOtherButtonsPanel.add(buttonF);
        allOtherButtonsPanel.add(getCalculator().getButton0());
        allOtherButtonsPanel.add(getCalculator().buttonDot);
        allOtherButtonsPanel.add(getCalculator().buttonAdd);
        // add allOtherButtonsPanel to gui
        constraints.insets = new Insets(0, 0, 5, 0);
        addComponent(allOtherButtonsPanel, 5, 1, 6, 8, panelLayout);
        LOGGER.info("Finished addComponentsToProgrammerPanel");
    }

    public void performProgrammerCalculatorTypeSwitchOperations() throws CalculatorError_v4
    {
        getLogger().info("Switching to the programmer panel...");
        getCalculator().clearNumberButtonFunctionalities();
        getCalculator().clearAllBasicOperationButtons();
        getCalculator().clearAllOtherBasicCalculatorButtons();
        getCalculator().clearVariableNumberOfButtonsFunctionalities();
        clearBasesFunctionality();
        clearBytesFunctionality();
        setupProgrammerPanel();
        getLogger().info("Programmer panel setup");
        // TODO: create method below
        setupHelpMenu();
        String convertedValue = ""; // conversion does not always happen
        String operator = calculator.determineIfBasicPanelOperatorWasPushed(); // operator not always included
        if (calculator.getCalculatorBase() != DECIMAL)
        {   // only convert if base is not decimal
            convertedValue = getCalculator().convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[calculator.valuesPosition])[0];
        }
        if (StringUtils.isBlank(operator) && StringUtils.isBlank(convertedValue))
        {   // no operator, no conversion
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + calculator.getTextAreaWithoutNewLineCharacters());
        }
        else if (StringUtils.isNotBlank(operator) && StringUtils.isBlank(convertedValue))
        {   // operator, no conversion
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + operator + " " + calculator.values[0]);
        }
        else if (StringUtils.isBlank(operator) && StringUtils.isNotBlank(convertedValue))
        {   // no operator, has conversion
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3)  + convertedValue);
        }
        else // if (StringUtils.isNotBlank(operator) && StringUtils.isNotBlank(convertedValue))
        {   // operator, conversion
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + operator + " " + convertedValue);
        }
    }

    public void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        getCalculator().getNumberButtons().forEach(button -> {
            button.setFont(Calculator_v4.font);
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
            hexidecimalButton.setFont(Calculator_v4.font);
            hexidecimalButton.setPreferredSize(new Dimension(35, 35));
            hexidecimalButton.setBorder(new LineBorder(Color.BLACK));
            hexidecimalButton.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
        }
    }

    public void setupHelpMenu()
    {
        getLogger().warn("IMPLEMENT");
    }

    public void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer calculator number button actions");
        getLogger().info("buttonChoice: " + actionEvent.getActionCommand());
        if (!getCalculator().firstNumBool)
        {
            getCalculator().firstNumBool = true;
            getCalculator().textArea.setText("");
            getCalculator().textarea = new StringBuffer();
            getCalculator().valuesPosition = 1;
        }
        try {
            performProgrammerNumberButtonActions(actionEvent.getActionCommand());
        } catch (CalculatorError_v4 c) { calculator.logException(c); }
    }

    public void performProgrammerNumberButtonActions(String buttonChoice) throws CalculatorError_v4
    {
        getCalculator().performInitialChecks();
        LOGGER.info("Performing programmer number button actions...");
        if (getCalculator().getTextAreaWithoutNewLineCharacters().length() == getCalculator().getBytes())
        {
            if (calculator.getCalculatorBase() == BINARY) {
                calculator.values[calculator.valuesPosition] = calculator.getTextAreaWithoutNewLineCharacters();
            }
            getLogger().warn("Byte limit reached but the user tried to enter another bit.");
            getLogger().info("Saving current binary value: " + calculator.values[calculator.valuesPosition]);
            return; // don't allow length to get any longer
        }
        if (getCalculator().getTextAreaWithoutNewLineCharacters().equals(""))
        {
            getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(3) + buttonChoice);
        }
        else
        {
            getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(3) +
                    getCalculator().getTextAreaWithoutNewLineCharacters() + buttonChoice); // update textArea
        }
        if (calculator.getCalculatorBase() != BINARY)
        {   // save current number
            calculator.values[calculator.valuesPosition] = calculator.getTextAreaWithoutNewLineCharacters();
        } else if (calculator.getCalculatorBase() == BINARY)
        {   // we are in binary mode, and we have reached the byte setting, so convert binary number, and save in values
            if (calculator.getTextAreaWithoutAnything().length() == calculator.getBytes()) {
                String convertedNumber = "";
                try { convertedNumber = calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, calculator.getTextAreaWithoutAnything())[0]; }
                catch (CalculatorError_v4 e) { calculator.logException(e); }
                calculator.values[calculator.valuesPosition] = convertedNumber;
            }
        }
        getCalculator().updateTextareaFromTextArea();
        getCalculator().confirm("Pressed " + buttonChoice);
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

        panelLayout.setConstraints(c, constraints); // set constraints
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
        constraints.weighty = 0;
        constraints.weightx = 0;

        panelLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void addComponent(Component c, int row, int column, int gwidth, int gheight, GridBagLayout layout)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = gwidth;
        constraints.gridheight = gheight;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void setComponent(Component c, int row, int column, int gwidth, int gheight, GridBagLayout layout)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = gwidth;
        constraints.gridheight = gheight;
        layout.setConstraints(c, constraints); // set constraints
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
            if (!calculator.getTextArea().getText().equals("")) {
                try {
                    convertValue0AndDisplayInTextArea();
                } catch (CalculatorError_v4 ex) {
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
            if (!calculator.getTextArea().getText().equals(""))
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
            if (!calculator.getTextArea().getText().equals(""))
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
            if (!calculator.getTextArea().getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ButtonByteHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 8) {
                if (calculator.getTextArea().getText().equals(""))
                    calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(8,16));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 16) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,16));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonDWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 32) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,32));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonQWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 64) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,64));
                calculator.getTextArea().setText(calculator.textarea.toString());
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
            if (!calculator.getTextArea().getText().equals(""))
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
            if (!calculator.getTextArea().getText().equals(""))
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
            if (!calculator.getTextArea().getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ByteButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 8) {
                if (calculator.getTextArea().getText().equals(""))
                    calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(8,16));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class WordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 16) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,16));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class DWordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 32) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,32));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }
    public class QWordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea().getText().length() > 64) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,64));
                calculator.getTextArea().setText(calculator.textarea.toString());
            }
        }
    }

    // TODO: Broken
    public void performButtonNotActions(ActionEvent action)
    {
        LOGGER.info("performing not operation...");
        String buttonChoice = action.getActionCommand();
        getLogger().info("button: " + buttonChoice);
        getCalculator().setNotButtonBool(false);
        getButtonNot().setEnabled(false);
        calculator.textarea = new StringBuffer(calculator.getTextArea().getText().replaceAll("\n", ""));
        LOGGER.debug("before operation execution: " + calculator.textarea.toString());
        StringBuffer newBuffer = new StringBuffer();
        for (int i = 0; i < calculator.textarea.length(); i++) {
            String s = Character.toString(calculator.textarea.charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: " + newBuffer);
        calculator.textarea = new StringBuffer(newBuffer);
        calculator.getTextArea().setText("\n"+calculator.textarea.toString());
        LOGGER.info("not operation completed.");
    }

    public void performButtonXorActions(ActionEvent action)
    {
        getLogger().info("performing XOR button actions");
        String buttonChoice = action.getActionCommand();
        getLogger().info("button: " + buttonChoice);
        calculator.setXorButtonBool(true);
        getButtonXor().setEnabled(false);
        if (StringUtils.isEmpty(getCalculator().getValues()[0]) && StringUtils.isEmpty(getCalculator().getValues()[1])) {

            getCalculator().confirm("No values set");
        }
        else if (!StringUtils.isEmpty(getCalculator().getValues()[0]) && StringUtils.isEmpty(getCalculator().getValues()[1]))
        {
            getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)+
                    getCalculator().getTextAreaWithoutNewLineCharacters() + " " + "XOR");
        }
        else if (!StringUtils.isEmpty(getCalculator().getValues()[0]) && !StringUtils.isEmpty(getCalculator().getValues()[1]))
        {
            performXor();
        }
    }

    public void performButtonOrActions(ActionEvent action) throws CalculatorError_v4
    {
        LOGGER.info("performOrLogic starts here");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        calculator.setOrButtonBool(true);
        getButtonOr().setEnabled(false);
        if (StringUtils.isEmpty(calculator.values[0]) && StringUtils.isNotEmpty(calculator.values[1]))
        {
            String msg = "calculator.values[1] is set and not calculator.values[0]. This is not allowed.";
            throw new CalculatorError_v4(msg);
        }
        else if (StringUtils.isNotEmpty(calculator.values[0]) && StringUtils.isEmpty(calculator.values[1]))
        {
            getLogger().debug("values[0] is set, but not values[1]");
            calculator.firstNumBool = false;
            getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                                  + buttonChoice + " " + getCalculator().getValues()[0]);
            getCalculator().setTextarea(new StringBuffer().append(getCalculator().getValues()[0] + " " + buttonChoice));
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
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+calculator.values[0]);
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
        if (getCalculator().getValues()[0].equals("") && getCalculator().getValues()[1].equals("") )
        {
            // do nothing
            LOGGER.debug("Doing nothing because...");
            LOGGER.debug("getCalculator().getValues()[0]: " + getCalculator().getValues()[0]);
            LOGGER.debug("getCalculator().getValues()[1]: " + getCalculator().getValues()[1]);
        }
        else if (!getCalculator().getValues()[0].equals("") &&
                 !getCalculator().getValues()[1].equals(""))
        {
            LOGGER.debug("performing Modulus");
            performModulus();
        }
        else if (!getCalculator().getValues()[0].equals("") &&
                  getCalculator().getValues()[1].equals(""))
        {
            // some value entered then pushed mod ... more input to come
            getCalculator().getTextArea().setText(getCalculator().getTextArea().getText() + " " + buttonChoice);
            getCalculator().updateTextareaFromTextArea();
//            getCalculator().getValues()[0] = getCalculator().getTextAreaWithoutNewLineCharacters();
            LOGGER.debug("setting setModButtonBool to true");
            getCalculator().setModButtonBool(true);
            getCalculator().setFirstNumBool(false);
        }
        getCalculator().confirm("Modulus Actions finished");
    }

    public void resetProgrammerOperators()
    {
        getButtonMod().setEnabled(true);
        getButtonOr().setEnabled(true);
        getButtonXor().setEnabled(true);
        getButtonNot().setEnabled(true);
        getButtonAnd().setEnabled(true);

        getCalculator().setModButtonBool(false);
        getCalculator().setOrButtonBool(false);
        getCalculator().setXorButtonBool(false);
        getCalculator().setNotButtonBool(false);
        getCalculator().setAndButtonBool(false);
    }

    public void addBytesToTextArea()
    {
        // the first two rows in the programmer calculator are reserved for bytes
        if (getButtonByte().isSelected())
        {
            getCalculator().getTextArea().setText(
                    getCalculator().addNewLineCharacters(3) +
                    getCalculator().getValues()[3]);
            getCalculator().getTextArea().setWrapStyleWord(true);
        }
        else if (getButtonWord().isSelected())
        {
            getCalculator().getTextArea().setText(
                    getCalculator().addNewLineCharacters(3) +
                    getCalculator().getValues()[3]);
            getCalculator().getTextArea().setWrapStyleWord(true);
        }
        else if (getButtonDWord().isSelected())
        {
            getCalculator().getTextArea().setText(
                    getCalculator().addNewLineCharacters(2) +
                    "00000000 00000000 00000000 00000000"+
                    getCalculator().addNewLineCharacters(1) +
                    getCalculator().getValues()[3]+" 00000000 00000000 00000000");
            getCalculator().getTextArea().setWrapStyleWord(true);
        }
        else if (getButtonQWord().isSelected())
        {
            getCalculator().getTextArea().setText(
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 00000000\n" +
                            "00000000 00000000 00000000 00000000\n"+getCalculator().getValues()[3]+" 00000000 00000000 00000000");
            getCalculator().getTextArea().setWrapStyleWord(true);
        }
    }

    /**
     * Converts all values, and restores in values
     */
    public void convertValue0AndDisplayInTextArea() throws CalculatorError_v4
    {
        LOGGER.info("Converting values");
        String convertedValue = getCalculator().convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[calculator.valuesPosition])[0];
        calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + convertedValue);
        getLogger().info("Values converted");
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
        getLogger().debug("convertToDecimal starting");
        CalculatorBase_v4 previousBase = getCalculator().getCalculatorBase();
        // update the current base to binary
        getCalculator().setCalculatorBase(DECIMAL);
        CalculatorBase_v4 currentBase = getCalculator().getCalculatorBase();
        getLogger().info("previous base: " + previousBase);
        getLogger().info("current base: " + currentBase);
        String nameOfButton = calculator.determineIfProgrammerPanelOperatorWasPushed(); // could be null
        if (previousBase == DECIMAL && currentBase == BINARY ||
                getCalculator().getCalculatorType() == CalculatorType_v4.BASIC)
        {
            try {
                calculator.values = getCalculator().convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values);
            } catch (CalculatorError_v4 e) {
                calculator.logException(e);
            }
        }
        else if (previousBase == BINARY && currentBase == DECIMAL)
        {
        }
        else
        if (getCalculator().getTextAreaWithoutNewLineCharacters().equals("")) { return; }
        if (getButtonBin().isSelected()) {
            // logic for Binary to Decimal
            try {
                getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)+
                        getCalculator().convertFromTypeToTypeOnValues(BINARY, DECIMAL,
                                getCalculator().getTextAreaWithoutNewLineCharacters())[0]);
            } catch (CalculatorError_v4 e) {
                calculator.logException(e);
            }
        }
        else if (calculator.isButtonOctSet == true) {
            // logic for Octal to Decimal
        } else if (calculator.isButtonHexSet == true) {
            // logic for Hexadecimal to Decimal
        } else if (getButtonDec().isSelected()) {
            if (calculator.getTextArea().getText().equals("")) { return; }
            String operator = calculator.determineIfProgrammerPanelOperatorWasPushed();
            if (!operator.equals(""))
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3)+operator+" "+calculator.values[calculator.valuesPosition]);
            }
            else
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3)+calculator.values[calculator.valuesPosition]);
            }
            calculator.updateTextareaFromTextArea();
        }

        getLogger().info("convertToDecimal finished");
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
    public void convertTextArea()
    {
        if (getCalculator().getCalculatorType() == CalculatorType_v4.BASIC)
        {
            getLogger().debug("Going from Basic to Programmer");
            calculator.performInitialChecks();
            boolean operatorWasPushed = getCalculator().determineIfMainOperatorWasPushedBoolean();
            String convertedValue = "";
            try { convertedValue = getCalculator().convertFromTypeToTypeOnValues(DECIMAL, BINARY, getCalculator().getValues()[0])[0]; }
            catch (CalculatorError_v4 c) { calculator.logException(c); }
            if (StringUtils.isNotBlank(getCalculator().getTextAreaWithoutNewLineCharacters()))
            {
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator
                {
                    if (getCalculator().addBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " + " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " +"));
                    }
                    else if (getCalculator().subBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " - " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " -"));
                    }
                    else if (getCalculator().mulBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " * " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " *"));
                    }
                    else if (getCalculator().divBool)
                    {
                        getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                                + " / " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but textArea has some value
                {
                    getCalculator().getTextArea().setText(getCalculator().addNewLineCharacters(1)
                            + convertedValue);
                    getCalculator().setTextarea(new StringBuffer().append(convertedValue));
                }
            }
        }
        // TODO: add logic for Scientific
    }

    public void performModulus()
    {
        // some value mod another value returns result:  4 mod 3 == 1; 1 * 3 = 3; 4 - 3 = 1 == result
        int firstResult = Integer.parseInt(getCalculator().getValues()[0]) / Integer.parseInt(getCalculator().getValues()[1]); // create result forced double
        getLogger().debug("firstResult: " + firstResult);
        int secondResult = (firstResult * Integer.parseInt(getCalculator().getValues()[1]));
        getLogger().debug("secondResult: " + secondResult);
        int finalResult = Integer.parseInt(getCalculator().getValues()[0]) - secondResult;
        getLogger().debug("modulus: " + finalResult);
        getCalculator().getValues()[0] = String.valueOf(finalResult);
    }

    public String performOr()
    {
        getLogger().debug("performing Or");
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
        getCalculator().getValues()[3] = sb.toString();
        //getCalculator().convertAllValuesToDecimal();
        LOGGER.info(calculator.values[0]+" OR "+calculator.values[1]+" = "+ calculator.values[3]);
        getCalculator().getValues()[0] = getCalculator().getValues()[3];
        getButtonOr().setEnabled(true);
        return String.valueOf(sb);
    }
    public String performXor() {
        getLogger().info("performing Xor");
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

    /************* All Getters and Setters ******************/

    public static Logger getLogger() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }
    public GridBagLayout getPanelLayout() { return panelLayout; }
    public GridBagLayout getOtherButtonLayout() { return otherButtonLayout; }
    public JPanel getAllOtherButtonsPanel() { return allOtherButtonsPanel; }
    public GridBagConstraints getConstraints() { return constraints; }
    public ButtonGroup getBtnGroupOne() { return btnGroupOne; }
    public ButtonGroup getBtnGroupTwo() { return btnGroupTwo; }
    public JPanel getButtonGroup1ButtonPanel() { return buttonGroup1ButtonPanel; }
    public JPanel getButtonGroup2ButtonPanel() { return buttonGroup2ButtonPanel; }
    public JButton getButton() { return button; }
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
    public JButton getButtonFraction() { return buttonFraction; }
    public JButton getButtonPercent() { return buttonPercent; }
    public JButton getButtonSqrt() { return buttonSqrt; }
    public StringBuffer getConversion() { return conversion; }
    public Calculator_v4 getCalculator() { return calculator; }
    public JLabel getTopLeftBytesLabel() { return topLeftBytesLabel; }
    public JLabel getTopRightBytesLabel() { return topRightBytesLabel; }
    public JLabel getBottomLeftBytesLabel() { return bottomLeftBytesLabel; }
    public JLabel getBottomRightBytesLabel() { return bottomRightBytesLabel; }

    public void setButtons2To9(boolean isEnabled)
    {
        calculator.button2.setEnabled(isEnabled);
        calculator.button3.setEnabled(isEnabled);
        calculator.button4.setEnabled(isEnabled);
        calculator.button5.setEnabled(isEnabled);
        calculator.button6.setEnabled(isEnabled);
        calculator.button7.setEnabled(isEnabled);
        calculator.button8.setEnabled(isEnabled);
        calculator.button9.setEnabled(isEnabled);
    }

    public void setButtonsAToF(boolean isEnabled)
    {
        buttonA.setEnabled(isEnabled);
        buttonB.setEnabled(isEnabled);
        buttonC.setEnabled(isEnabled);
        buttonD.setEnabled(isEnabled);
        buttonE.setEnabled(isEnabled);
        buttonF.setEnabled(isEnabled);
    }


    public void setCalculator(Calculator_v4 calculator) { this.calculator = calculator; }
    public void setPanelLayout(GridBagLayout panelLayout) { this.panelLayout = panelLayout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setTopLeftBytesLabel(JLabel topLeftBytesLabel) { this.topLeftBytesLabel = topLeftBytesLabel; }
    public void setTopRightBytesLabel(JLabel topRightBytesLabel) { this.topRightBytesLabel = topRightBytesLabel; }
    public void setBottomLeftBytesLabel(JLabel bottomLeftBytesLabel) { this.bottomLeftBytesLabel = bottomLeftBytesLabel; }
    public void setBottomRightBytesLabel(JLabel bottomRightBytesLabel) { this.bottomRightBytesLabel = bottomRightBytesLabel; }

}
