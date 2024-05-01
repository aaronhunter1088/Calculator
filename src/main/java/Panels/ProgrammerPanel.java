package Panels;

import Calculators.Calculator_v4;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Calculators.Calculator_v4.*;
import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;

public class ProgrammerPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanel.class.getSimpleName());
    private static final long serialVersionUID = 4L;

    private GridBagLayout programmerLayout;
    private GridBagConstraints constraints; // layout's constraints
    private final ButtonGroup btnGroupOne = new ButtonGroup();
    private final ButtonGroup btnGroupTwo = new ButtonGroup();
    private final JPanel
            btnGroupOnePanel = new JPanel(), // contains the first button group
            btnGroupTwoPanel = new JPanel(); // contains the second button group
    final private JButton
            buttonModulus = new JButton("MOD"),
            buttonLPar = new JButton("("), buttonRPar = new JButton(")"),
            buttonRol = new JButton ("RoL"), buttonRor = new JButton ("RoR"),
            buttonOr = new JButton ("OR"), buttonXor = new JButton ("XOR"),
            buttonLSh = new JButton ("Lsh"), buttonRSh = new JButton ("Rsh"),
            buttonNot = new JButton ("NOT"), buttonAnd = new JButton ("AND"),
            buttonA = new JButton("A"), buttonB = new JButton("B"),
            buttonC = new JButton("C"), buttonD = new JButton("D"),
            buttonE = new JButton("E"), buttonF = new JButton("F");
    final private JRadioButton
            buttonHex = new JRadioButton("Hex"), buttonDec = new JRadioButton("Dec"),
            buttonOct = new JRadioButton("Oct"), buttonBin = new JRadioButton("Bin"),
            buttonQWord = new JRadioButton("QWord"), buttonDWord = new JRadioButton("DWord"),
            buttonWord = new JRadioButton("Word"), buttonByte = new JRadioButton("Byte");
    private boolean
            isBaseBinary = true, isBaseOctal = false,
            isBaseDecimal = false, isBaseHexadecimal = false,
            isByte = true, isWord = false,
            isDWord = false, isQWord = false,
            isOrPressed = false, isModulusPressed = false,
            isXorPressed = false, negateButtonBool = false, // TODO: REMOVE negateButtonBool
            isNotPressed = false, isAndPressed = false;
    private Calculator_v4 calculator;

    /************* Constructors ******************/
    public ProgrammerPanel()
    { LOGGER.info("Empty Programmer panel created"); }

    public ProgrammerPanel(Calculator_v4 calculator)
    { this(calculator, null); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator the Calculator_v4 to use
     */
    public ProgrammerPanel(Calculator_v4 calculator, CalculatorBase base)
    {
        setupProgrammerPanel(calculator, base);
        LOGGER.info("Programmer panel created");
    }

    /************* Start of methods here ******************/
    private void setupProgrammerPanel(Calculator_v4 calculator, CalculatorBase base)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setMaximumSize(new Dimension(600,400));
        setupProgrammerPanelComponents(base);
        addComponentsToPanel();
        setName(PROGRAMMER.getName());
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up programmer panel");
    }

    private void setupProgrammerPanelComponents(CalculatorBase base)
    {
        List<JButton> allButtons = Stream.of(
                        calculator.getAllOtherBasicCalculatorButtons(),
                        calculator.getBasicOperationButtons(),
                        calculator.getBasicNumberButtons())
                .flatMap(Collection::stream) // Flatten the stream of collections into a stream of JButton objects
                .collect(Collectors.toList());

        allButtons.forEach(button -> Stream.of(button.getActionListeners())
                .forEach(button::removeActionListener));

        calculator.setCalculatorType(PROGRAMMER);
        setProgrammerBase(base);
        calculator.setConverterType(null);
        setupHelpMenu();
        setupTextArea();
        setupButtonBlank1();
        setupButtonModulus();
        setupButtonLPar();
        setupButtonRPar();
        setupButtonRol();
        setupButtonRor();
        setupButtonOr();
        setupButtonXor();
        setupButtonLSh();
        setupButtonRSh();
        setupButtonNot();
        setupButtonAnd();
        setupButtonsAToF();
        setupMemoryButtons(); // MR MC MS M+ M-
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
        resetProgrammerByteOperators(false);
        setByte(true);
        setupButtonGroupOne();
        setupButtonGroupTwo();
        LOGGER.info("Finished configuring the buttons");
    }

    private void setProgrammerBase(CalculatorBase base)
    {
        if (base == DECIMAL || StringUtils.isNotBlank(calculator.getValues()[0]))
        {
            calculator.setCalculatorBase(DECIMAL);
            buttonDec.setSelected(true);
            setBaseDecimal(true);
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
            buttonOct.setSelected(true);
            setBaseOctal(true);
        }
        else if (base == HEXADECIMAL)
        {
            calculator.setCalculatorBase(base);
            setButtons2To9(true);
            setButtonsAToF(true);
            buttonHex.setSelected(true);
            setBaseHexadecimal(true);
        }
        else
        {
            calculator.setCalculatorBase(BINARY);
            setButtons2To9(false);
            setButtonsAToF(false);
            buttonBin.setSelected(true);
            setBaseBinary(true);
        }
    }

    private void addComponentsToPanel()
    {
        constraints.insets = new Insets(5,0,5,0);
        addComponent(calculator.getTextArea(), 0, 0, 9, 2);
        constraints.insets = new Insets(5,5,5,5);
        addComponent(btnGroupOnePanel, 4, 0, 1, 4, GridBagConstraints.HORIZONTAL);
        addComponent(btnGroupTwoPanel, 7, 0, 1, 4, GridBagConstraints.HORIZONTAL);
        constraints.insets = new Insets(5,0,1,5);
        addComponent(calculator.getButtonBlank1(), 4, 1, 1, 1);
        addComponent(buttonModulus, 4, 2, 1, 1);
        addComponent(buttonA, 4, 3, 1, 1);
        addComponent(calculator.getButtonMemoryStore(), 4, 4, 1, 1);
        addComponent(calculator.getButtonMemoryClear(), 4, 5, 1, 1);
        addComponent(calculator.getButtonMemoryRecall(), 4, 6, 1, 1);
        addComponent(calculator.getButtonMemoryAddition(), 4, 7, 1, 1);
        addComponent(calculator.getButtonMemorySubtraction(), 4, 8, 1, 1);
        addComponent(buttonLPar, 5, 1, 1, 1);
        addComponent(buttonRPar, 5, 2, 1, 1);
        addComponent(buttonB, 5, 3, 1, 1);
        addComponent(calculator.getButtonDelete(), 5, 4, 1, 1);
        addComponent(calculator.getButtonClearEntry(), 5, 5, 1, 1);
        addComponent(calculator.getButtonClear(), 5, 6, 1, 1);
        addComponent(calculator.getButtonNegate(), 5, 7, 1, 1);
        addComponent(calculator.getButtonSqrt(), 5, 8, 1, 1);
        addComponent(buttonRol, 6, 1, 1, 1);
        addComponent(buttonRor, 6, 2, 1, 1);
        addComponent(buttonC, 6, 3, 1, 1);
        addComponent(calculator.getButton7(), 6, 4, 1, 1);
        addComponent(calculator.getButton8(), 6, 5, 1, 1);
        addComponent(calculator.getButton9(), 6, 6, 1, 1);
        addComponent(calculator.getButtonDivide(), 6, 7, 1, 1);
        addComponent(calculator.getButtonPercent(), 6, 8, 1, 1);
        addComponent(buttonOr, 7, 1, 1, 1);
        addComponent(buttonXor, 7, 2, 1, 1);
        addComponent(buttonD, 7, 3, 1, 1);
        addComponent(calculator.getButton4(), 7, 4, 1, 1);
        addComponent(calculator.getButton5(), 7, 5, 1, 1);
        addComponent(calculator.getButton6(), 7, 6, 1, 1);
        addComponent(calculator.getButtonMultiply(), 7, 7, 1, 1);
        addComponent(calculator.getButtonFraction(), 7, 8, 1, 1);
        addComponent(buttonLSh, 8, 1, 1, 1);
        addComponent(buttonRSh, 8, 2, 1, 1);
        addComponent(buttonE, 8, 3, 1, 1);
        addComponent(calculator.getButton1(), 8, 4, 1, 1);
        addComponent(calculator.getButton2(), 8, 5, 1, 1);
        addComponent(calculator.getButton3(), 8, 6, 1, 1);
        addComponent(calculator.getButtonSubtract(), 8, 7, 1, 1);
        addComponent(calculator.getButtonEquals(), 8, 8, 1, 2);
        addComponent(buttonNot, 9, 1, 1, 1);
        addComponent(buttonAnd, 9, 2, 1, 1);
        addComponent(buttonF, 9, 3, 1, 1);
        addComponent(calculator.getButton0(), 9, 4, 2, 1);
        addComponent(calculator.getButtonDot(), 9, 6, 1, 1);
        addComponent(calculator.getButtonAdd(), 9, 7, 1, 1);
        LOGGER.info("Buttons added to programmer panel");
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
        programmerLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    private void setupHelpMenu()
    { LOGGER.warn("IMPLEMENT Help Menu"); }

    private void setupTextArea()
    {
        calculator.setTextArea(new JTextPane());
        calculator.getTextArea().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        calculator.getTextArea().setFont(mainFont);
        calculator.getTextArea().setBorder(new LineBorder(Color.BLACK));
        calculator.getTextArea().setEditable(false);
        calculator.getTextArea().setPreferredSize(new Dimension(105, 60));
        LOGGER.info("Text Area configured");
    }

    private void setupButtonBlank1()
    {
        calculator.getButtonBlank1().setPreferredSize(new Dimension(35,35));
        calculator.getButtonBlank1().setBorder(new LineBorder(Color.BLACK));
    }

    private void setupButtonModulus()
    {
        buttonModulus.setFont(Calculator_v4.mainFont);
        buttonModulus.setPreferredSize(new Dimension(35,35));
        buttonModulus.setBorder(new LineBorder(Color.BLACK));
        buttonModulus.addActionListener(this::performButtonModActions);
    }
    public void performButtonModActions(ActionEvent action)
    {
        LOGGER.debug("performModButtonActions begins");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("button: " + buttonChoice);
        LOGGER.debug("is it enabled? " + buttonModulus.isEnabled() + " Setting to false.");
        setModulusPressed(true);
        buttonModulus.setEnabled(false);
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
            calculator.getTextArea().setText(calculator.getTextArea().getText() + " " + buttonChoice);
            //calculator.updateTextAreaValueFromTextArea();
//            calculator.getValues()[0] = calculator.getTextAreaWithoutNewLineCharacters();
            LOGGER.debug("setting setModButtonBool to true");
            setModulusPressed(true);
            calculator.setFirstNumber(false);
        }
        calculator.confirm("Modulus Actions finished");
    }
    private void performModulus()
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

    //TODO: Add actionListener
    private void setupButtonLPar()
    {
        buttonLPar.setFont(Calculator_v4.mainFont);
        buttonLPar.setPreferredSize(new Dimension(35,35));
        buttonLPar.setBorder(new LineBorder(Color.BLACK));
        buttonLPar.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    private void setupButtonRPar()
    {
        buttonRPar.setFont(Calculator_v4.mainFont);
        buttonRPar.setPreferredSize(new Dimension(35,35));
        buttonRPar.setBorder(new LineBorder(Color.BLACK));
        buttonRPar.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    private void setupButtonRol()
    {
        buttonRol.setFont(Calculator_v4.mainFont);
        buttonRol.setPreferredSize(new Dimension(35,35));
        buttonRol.setBorder(new LineBorder(Color.BLACK));
        buttonRol.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    private void setupButtonRor()
    {
        buttonRor.setFont(Calculator_v4.mainFont);
        buttonRor.setPreferredSize(new Dimension(35,35));
        buttonRor.setBorder(new LineBorder(Color.BLACK));
        buttonRor.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    private void setupButtonOr()
    {
        buttonOr.setFont(mainFont);
        buttonOr.setPreferredSize(new Dimension(35,35));
        buttonOr.setBorder(new LineBorder(Color.BLACK));
        buttonOr.addActionListener(this::performButtonOrActions);
    }
    public void performButtonOrActions(ActionEvent action)
    {
        LOGGER.info("performOrLogic starts here");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setOrPressed(true);
        buttonOr.setEnabled(false);
//        if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isNotEmpty(calculator.getValues()[1]))
//        {
//            String msg = "calculator.getValues()[1] is set and not calculator.getValues()[0]. This is not allowed.";
//            throw new CalculatorError(msg);
//        }
//        else if below
        if (StringUtils.isNotEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            LOGGER.debug("getValues()[0] is set, but not getValues()[1]");
            calculator.setFirstNumber(false);
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                    + buttonChoice + " " + calculator.getValues()[0]);
            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0] + " " + buttonChoice));
            calculator.setValuesPosition(calculator.getValuesPosition() + 1);
            calculator.confirm("OR complete");
        }
        else if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            setOrPressed(false);
            calculator.setFirstNumber(true);
            calculator.confirm("Pressed OR. Doing nothing");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            String sb = performOr(); // 2 OR 3 OR button presses
            //TODO: need to convert sb to DECIMAL form before storing in getValues()
            calculator.getValues()[0] = sb;
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+calculator.getValues()[0]);
            setOrPressed(false);
            calculator.setValuesPosition(0);
        }
    }
    private String performOr()
    {
        LOGGER.debug("performing Or");
        StringBuffer sb = new StringBuffer();
        //TODO: if getValues()[0] and getValues()[1] in decimal and difference length, this will fail.
        //In order for this to work, we need to convert to most appropriate base
        //Check to make sure both are same length

        for (int i=0; i<calculator.getValues()[0].length(); i++)
        {
            String letter = "0";
            if (String.valueOf(calculator.getValues()[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.getValues()[1].charAt(i)).equals("0") )
            { // if the characters at both getValues() at the same position are the same and equal 0
                letter = "0";
            }
            else
            {
                letter = "1";
            }
            sb.append(letter);
            LOGGER.info(calculator.getValues()[0].charAt(i)+" OR "+calculator.getValues()[1].charAt(i)+" = "+ letter);
        }
        calculator.getValues()[3] = sb.toString();
        //calculator.convertAllValuesToDecimal();
        LOGGER.info(calculator.getValues()[0]+" OR "+calculator.getValues()[1]+" = "+ calculator.getValues()[3]);
        calculator.getValues()[0] = calculator.getValues()[3];
        buttonOr.setEnabled(true);
        return String.valueOf(sb);
    }

    private void setupButtonXor()
    {
        buttonXor.setFont(mainFont);
        buttonXor.setPreferredSize(new Dimension(35,35));
        buttonXor.setBorder(new LineBorder(Color.BLACK));
        buttonXor.addActionListener(this::performButtonXorActions);
    }
    public void performButtonXorActions(ActionEvent action)
    {
        LOGGER.info("performing XOR button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setXorPressed(true);
        buttonXor.setEnabled(false);
        if (StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1])) {

            calculator.confirm("No getValues() set");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && StringUtils.isEmpty(calculator.getValues()[1]))
        {
            calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+
                    calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace() + " " + "XOR");
        }
        else if (!StringUtils.isEmpty(calculator.getValues()[0]) && !StringUtils.isEmpty(calculator.getValues()[1]))
        {
            performXor();
        }
    }
    private String performXor()
    {
        LOGGER.info("performing Xor");
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<calculator.getValues()[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(calculator.getValues()[0].charAt(i)).equals("0") &&
                    String.valueOf(calculator.getValues()[1].charAt(i)).equals("0") )
            { // if the characters at both getValues() at the same position are the same and equal 0
                letter = "0";
            }
            else
            {
                letter = "1";
            }
            sb.append(letter);
            LOGGER.info(calculator.getValues()[0].charAt(i) + " + " + calculator.getValues()[1].charAt(i)+ " = " + letter);
        }
        return String.valueOf(sb);
    }

    //TODO: Add actionListener
    private void setupButtonLSh()
    {
        buttonLSh.setFont(Calculator_v4.mainFont);
        buttonLSh.setPreferredSize(new Dimension(35,35));
        buttonLSh.setBorder(new LineBorder(Color.BLACK));
        buttonLSh.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    //TODO: Add actionListener
    private void setupButtonRSh()
    {
        buttonRSh.setFont(Calculator_v4.mainFont);
        buttonRSh.setPreferredSize(new Dimension(35,35));
        buttonRSh.setBorder(new LineBorder(Color.BLACK));
        buttonRSh.addActionListener(action -> LOGGER.warn("IMPLEMENT"));
    }

    private void setupButtonNot()
    {
        buttonNot.setFont(Calculator_v4.mainFont);
        buttonNot.setPreferredSize(new Dimension(35,35));
        buttonNot.setBorder(new LineBorder(Color.BLACK));
        buttonNot.addActionListener(this::performButtonNotActions);
    }
    public void performButtonNotActions(ActionEvent action)
    {
        LOGGER.info("performing not operation...");
        String buttonChoice = action.getActionCommand();
        LOGGER.info("button: " + buttonChoice);
        setNotPressed(false);
        buttonNot.setEnabled(false);
        //calculator.setTextAreaValue(new StringBuffer(calculator.getTextArea().getText().replaceAll("\n", "")));
        LOGGER.debug("before operation execution: " + calculator.getTextArea().getText().toString());
        StringBuffer newBuffer = new StringBuffer();
        for (int i = 0; i < calculator.getTextArea().getText().length(); i++) {
            String s = Character.toString(calculator.getTextArea().getText().charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: " + newBuffer);
        //calculator.setTextAreaValue(new StringBuffer(newBuffer));
        calculator.getTextArea().setText("\n"+calculator.getTextArea().getText().toString());
        LOGGER.info("not operation completed.");
    }

    //TODO: Add actionListener
    private void setupButtonAnd()
    {
        buttonAnd.setFont(Calculator_v4.mainFont);
        buttonAnd.setPreferredSize(new Dimension(35,35));
        buttonAnd.setBorder(new LineBorder(Color.BLACK));
        //buttonAnd.addActionListener(this::performButtonAndActions);
    }

    /**
     * Group Base
     */
    private void setupButtonGroupOne()
    {
        btnGroupOnePanel.setLayout(new GridLayout(4,1));
        btnGroupOnePanel.add(buttonHex);
        btnGroupOnePanel.add(buttonDec);
        btnGroupOnePanel.add(buttonOct);
        btnGroupOnePanel.add(buttonBin);
        Border border = btnGroupOnePanel.getBorder();
        Border margin = new TitledBorder("Base");
        btnGroupOnePanel.setBorder(new CompoundBorder(border, margin));
        btnGroupOne.add(buttonHex);
        btnGroupOne.add(buttonDec);
        btnGroupOne.add(buttonOct);
        btnGroupOne.add(buttonBin);
        buttonHex.addActionListener(this::performBaseButtonSwitch);
        buttonDec.addActionListener(this::performBaseButtonSwitch);
        buttonOct.addActionListener(this::performBaseButtonSwitch);
        buttonBin.addActionListener(this::performBaseButtonSwitch);
    }
    public void performBaseButtonSwitch(ActionEvent action)
    {
        LOGGER.info("Changing bases...");
        CalculatorBase previousBase = calculator.getCalculatorBase();
        String baseChoice = action.getActionCommand();
        CalculatorBase newBase = determineCalculatorBase(baseChoice);
        //calculator.setCalculatorBase(newBase);
        String nameOfOperatorPushed = determineIfProgrammerPanelOperatorWasPushed(); // could be blank
        LOGGER.info("Operator {} will be cleared", nameOfOperatorPushed);
        calculator.resetBasicOperators(false);
        resetProgrammerByteOperators(false);
        resetProgrammerOperators();
        LOGGER.info("baseChoice: " + newBase);
        if (newBase == BINARY)
        {
            calculator.setCalculatorBase(BINARY);
            buttonBin.setSelected(true);
            setByte(true);
            setButtons2To9(false);
            getAToFButtons().forEach(button -> button.setEnabled(false));
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
        else if (newBase == OCTAL)
        {
            buttonOct.setSelected(true);
            setBaseOctal(true);
            setButtons2To9(true);
            calculator.getButton8().setEnabled(false);
            calculator.getButton9().setEnabled(false);
            getAToFButtons().forEach(button -> button.setEnabled(false));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        else if (newBase == DECIMAL)
        {
            //calculator.resetProgrammerByteOperators(false);
            buttonByte.setSelected(true);
            setByte(true);
            buttonDec.setSelected(true);
            setBaseDecimal(true);
            setButtons2To9(true);
            setButtonsAToF(false);
            calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + calculator.getValues()[calculator.getValuesPosition()]);
            calculator.setCalculatorBase(DECIMAL);// we don't always "update getTextArea()", like when it's blank
        }
        else // newBase == HEXIDECIMAL
        {
            buttonHex.setSelected(true);
            setBaseHexadecimal(true);
            setButtons2To9(true);
            getAToFButtons().forEach(button -> button.setEnabled(true));
            updateTextAreaAfterBaseChange(previousBase, newBase, nameOfOperatorPushed);
        }
        calculator.confirm("Bases changed");
    }

    /**
     * Group Byte
     */
    private void setupButtonGroupTwo()
    {
        btnGroupTwoPanel.setLayout(new GridLayout(4,1)); //rows and columns
        btnGroupTwoPanel.add(buttonQWord);
        btnGroupTwoPanel.add(buttonDWord);
        btnGroupTwoPanel.add(buttonWord);
        btnGroupTwoPanel.add(buttonByte);
        Border border2 = btnGroupTwoPanel.getBorder();
        Border margin2 = new TitledBorder("Byte");
        btnGroupTwoPanel.setBorder(new CompoundBorder(border2, margin2));
        btnGroupTwo.add(buttonQWord);
        btnGroupTwo.add(buttonDWord);
        btnGroupTwo.add(buttonWord);
        btnGroupTwo.add(buttonByte);
        buttonQWord.addActionListener(this::performByteButtonSwitch);
        buttonDWord.addActionListener(this::performByteButtonSwitch);
        buttonWord.addActionListener(this::performByteButtonSwitch);
        buttonByte.addActionListener(this::performByteButtonSwitch);
        buttonByte.setSelected(true);
    }
    public void performByteButtonSwitch(ActionEvent action)
    {
        LOGGER.info("Performing byte button actions");
        String buttonChoice = action.getActionCommand();
        LOGGER.debug("buttonChoice: " + buttonChoice);
        resetProgrammerByteOperators(false);
        switch (buttonChoice) {
            case "Byte":
                buttonByte.setEnabled(true);
                setByte(true);
                break;
            case "Word":
                buttonWord.setEnabled(true);
                setWord(true);
                break;
            case "DWord":
                buttonDWord.setEnabled(true);
                setDWord(true);
                break;
            case "QWord":
                buttonQWord.setEnabled(true);
                setQWord(true);
                break;
        }
        calculator.confirm("Pressed: " + buttonChoice);
    }

    private void updateTextAreaAfterBaseChange(CalculatorBase previousBase, CalculatorBase newBase, String nameOfOperatorPushed)
    {
        String textAreaValueToConvert = calculator.getTextAreaWithoutAnyOperator();
        if (StringUtils.isNotBlank(textAreaValueToConvert)) {
            if (StringUtils.isEmpty(textAreaValueToConvert)) {
                textAreaValueToConvert = "0";
            }
            try {
                textAreaValueToConvert = calculator.convertFromTypeToTypeOnValues(previousBase, newBase, textAreaValueToConvert);
                if (StringUtils.equals(textAreaValueToConvert, "") || textAreaValueToConvert == null)
                {
                    textAreaValueToConvert = calculator.getTextAreaWithoutAnyOperator();
                }
            }
            catch (CalculatorError c) { calculator.logException(c); }
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
        //calculator.updateTextAreaValueFromTextArea();
    }

    private CalculatorBase determineCalculatorBase(String base)
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

    private void clearBasesFunctionality()
    { getBaseButtons().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener)); }
    public Collection<JRadioButton> getBaseButtons()
    { return Arrays.asList(buttonBin, buttonOct, buttonDec, buttonHex); }

    private void clearBytesFunctionality()
    { getByteButtons().forEach(button -> Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener)); }
    public Collection<JRadioButton> getByteButtons()
    { return Arrays.asList(buttonByte, buttonWord, buttonDWord, buttonQWord); }

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
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        String errorStringNaN = "Not a Number";
        LOGGER.debug("text: " + calculator.getTextArea().getText().replace("\n",""));
        if (calculator.getValues()[0].contains("E"))
        {
            String errorMsg = "Cannot perform square root operation. Number too big!";
            calculator.confirm(errorMsg);
        }
        else
        {
            if (calculator.getTextArea().getText().equals("") || calculator.isNegativeNumber(calculator.getTextArea().getText()))
            {
                calculator.getTextArea().setText("\n"+errorStringNaN);
                //calculator.setTextAreaValue(new StringBuffer().append("\n").append(errorStringNaN));
                calculator.confirm(errorStringNaN + "Cannot perform square root operation on blank/negative number");
            }
            else
            {
                String result = String.valueOf(Math.sqrt(Double.valueOf(calculator.getTextArea().getText())));
                result = calculator.formatNumber(result);
                calculator.getTextArea().setText("\n"+result);
                //calculator.setTextAreaValue(new StringBuffer().append(result));
                calculator.confirm();
            }
        }
    }

    private void setupNumberButtons(boolean isEnabled)
    {
        AtomicInteger i = new AtomicInteger(0);
        calculator.getNumberButtons().forEach(button -> {
            button.setFont(Calculator_v4.mainFont);
            button.setEnabled(isEnabled);
            if (button.getText().equals("0")) { button.setPreferredSize(new Dimension(70, 35)); }
            else { button.setPreferredSize(new Dimension(35, 35)); }
            button.setBorder(new LineBorder(Color.BLACK));
            button.setName(String.valueOf(i.getAndAdd(1)));
            button.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
            // setup in the panel, panel specific
        });
    }
    private void setupButtonsAToF()
    {
        for(JButton hexidecimalButton : getAToFButtons())
        {
            hexidecimalButton.setFont(Calculator_v4.mainFont);
            hexidecimalButton.setPreferredSize(new Dimension(35, 35));
            hexidecimalButton.setBorder(new LineBorder(Color.BLACK));
            hexidecimalButton.addActionListener(this::performProgrammerCalculatorNumberButtonActions);
        }
    }
    public void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer number button actions");
        LOGGER.info("buttonChoice: " + actionEvent.getActionCommand());
        if (!calculator.isFirstNumber())
        {
            calculator.setFirstNumber(true);
            calculator.getTextArea().setText("");
            //calculator.textarea = new StringBuffer();
            calculator.setValuesPosition(1);
        }
        try {
            performProgrammerNumberButtonActions(actionEvent);
        } catch (CalculatorError c) { calculator.logException(c); }
    }
    private void performProgrammerNumberButtonActions(ActionEvent actionEvent) throws CalculatorError
    {
        String buttonChoice = actionEvent.getActionCommand();
        LOGGER.info("Performing programmer number button actions...");
        LOGGER.info("Update bytes based on length of number in getTextArea()");
        LOGGER.info("Adding '{}' to getTextArea()", buttonChoice);
        if (calculator.getCalculatorBase() == BINARY) {
            int lengthOfTextArea = calculator.getTextAreaWithoutNewLineCharacters().length();
            if (lengthOfTextArea == 8 || lengthOfTextArea == 17 || lengthOfTextArea == 26 || lengthOfTextArea == 35 || lengthOfTextArea == 44)
            {   // add a space add the "end" if the length of the number matches the bytes
                StringBuffer newNumber = new StringBuffer();
                newNumber.append(buttonChoice).append(" ").append(calculator.getTextAreaWithoutNewLineCharacters());
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + newNumber);
                // update the bytes here or when we add next number
                if (isByte) { resetProgrammerByteOperators(false); setWord(true); buttonWord.setSelected(true); }
                else if (isWord) { resetProgrammerByteOperators(false); setDWord(true); buttonDWord.setSelected(true); }
                else if (isDWord && lengthOfTextArea == 17) {
                    resetProgrammerByteOperators(false);
                    setQWord(true);
                    buttonQWord.setSelected(true);
                }
                else if (lengthOfTextArea == 35) {
                    resetProgrammerByteOperators(false);
                    setQWord(true);
                    buttonQWord.setSelected(true);
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
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + newNumber);
            }
        }
        else if (calculator.getCalculatorBase() == DECIMAL) {
            if (!calculator.isFirstNumber()) // do for second number
            {
                if (!calculator.isDotPressed())
                {
                    calculator.getTextArea().setText("");
                    if (!calculator.isFirstNumber()) {
                        calculator.setFirstNumber(true);
                        calculator.setNumberNegative(false);
                    } else
                        calculator.setDotPressed(true);
                    calculator.getButtonDot().setEnabled(true);
                }
            }

            // TODO: Determine how to best call BasicPanel.performNumberButtonActions()
            //BasicPanel.performNumberButtonActions(actionEvent);
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
                //LOGGER.debug("before: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
                if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]))
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextAreaWithoutNewLineCharacters()));
                    calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[calculator.getValuesPosition()] + buttonChoice);
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(buttonChoice).reverse());
                    //calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().reverse().toString();
                }
                //LOGGER.debug("after: '" + calculator.getTextArea().getText() + "'");
                //calculator.setValuesToTextAreaValue();
                //calculator.getValues()[calculator.getValuesPosition()] = buttonChoice;
                //calculator.updateTheTextAreaBasedOnTheTypeAndBase();
                ////calculator.updateTextAreaValueFromTextArea();
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
        else if (calculator.getCalculatorBase() == OCTAL) { LOGGER.warn("IMPLEMENT Octal number button actions"); }
        else /* (calculator.getCalculatorBase() == HEXADECIMAL */ { LOGGER.warn("IMPLEMENT Hexadecimal number button actions"); }
    }
    public Collection<JButton> getAToFButtons()
    { return Arrays.asList(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF); }

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
            // TODO: fix for gap being in the middle of the memory values
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
        { calculator.setValuesPosition(0); } // assume they just previously pressed an operator

        LOGGER.info("values["+calculator.getValuesPosition()+"]: '" + calculator.getValues()[calculator.getValuesPosition()] + "'");
        LOGGER.info("textarea: " + calculator.getTextArea().getText());
        calculator.setNumberNegative(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        // this check has to happen
        calculator.setDotPressed(calculator.isDecimal(calculator.getTextArea().getText().toString()));
        calculator.getTextArea().setText(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
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
                        //calculator.setTextAreaValue(new StringBuffer().append(" "));
                        calculator.getValues()[calculator.getValuesPosition()] = "";
                    }
                    else if (calculator.getTextArea().getText().length() >= 2)
                    {
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length()-1)));
                        calculator.getTextArea().setText(calculator.addNewLineCharacters()+ calculator.getTextArea().getText());
                        calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();
                    }
                }
                // if no operator has been pushed; number is positive; number is decimal
                else { //if (calculator.isDotPressed()) {
                    if (calculator.getTextArea().getText().length() == 2)
                    { // ex: 3. .... recall textarea looks like .3
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(calculator.getTextArea().getText().length() -1 ))); // ex: 3
                        calculator.setDotPressed(false);
                        calculator.getButtonDot().setEnabled(true);
                    }
                    else if (calculator.getTextArea().getText().length() == 3) { // ex: 3.2 or 0.5
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 2))); // ex: 3 or 0
                        calculator.setDotPressed(false);
                        calculator.getButtonDot().setEnabled(true);
                    }
                    else if (calculator.getTextArea().getText().length() > 3)
                    { // ex: 3.25 or 0.50 or 5.02 or 78.9
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 1))); // inclusive
                        if (calculator.getTextArea().getText().toString().endsWith(".")) {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getNumberOnLeftSideOfNumber(calculator.getTextArea().getText().toString())));
                        }
                    }
                    LOGGER.info("output: " + calculator.getTextArea().getText());
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextArea().getText());
                    calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().toString();
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
                    {
                        // ex: -3.25 or -0.00
                        //calculator.getTextArea().getText().append(calculator.convertToPositive(calculator.getTextArea().getText().toString())); // 3.00 or 0.00
                        //calculator.getTextArea().getText().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length())); // 3.0 or 0.0
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
                    { // ex: 3.2 0.0
                        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText().substring(0, calculator.getTextArea().getText().length() - 2 ))); // 3 or 0
                        calculator.setDotPressed(false);
                    }
                    else if (calculator.getTextArea().getText().length() > 3)
                    { // ex: 3.25 or 0.50  or + 3.25-
                        if (calculator.isAdding() || calculator.isSubtracting() || calculator.isMultiplying() || calculator.isDividing())
                        {
                            //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]));
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
        calculator.confirm();
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
        if (calculator.getValues()[1].isEmpty()) { // if temp[1] is empty, we know we are at temp[0]
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
            calculator.resetBasicOperators(false);
            resetProgrammerByteOperators(false);
            ((ProgrammerPanel)calculator.getCurrentPanel()).resetProgrammerOperators();
            ((ProgrammerPanel)calculator.getCurrentPanel()).buttonByte.setSelected(true);
            setByte(true);
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
        // clear values
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
        calculator.resetBasicOperators(false);
        resetProgrammerByteOperators(false);
        ((ProgrammerPanel)calculator.getCurrentPanel()).resetProgrammerOperators();
        ((ProgrammerPanel)calculator.getCurrentPanel()).buttonByte.setSelected(true);
        setByte(true);
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
        calculator.getButtonNegate().setEnabled(false);
        calculator.getButtonNegate().addActionListener(this::performNegateButtonActions);
    }
    public void performNegateButtonActions(ActionEvent action)
    {
        String buttonChoice = action.getActionCommand();
        LOGGER.info("Performing {} Button actions", buttonChoice);
        if (calculator.getValues()[calculator.getValuesPosition()].contains("E"))
        {
            calculator.confirm("Number too big!");
        }
        else if (calculator.getValues()[calculator.getValuesPosition()].isEmpty())
        {
            calculator.confirm("No value to negate");
        }
        else
        {
            //textArea.setText(getTextAreaWithoutNewLineCharactersOrWhiteSpace() + calculator.getValues()[calculator.getValuesPosition()]);
            //updateTextAreaValueFromTextArea();
            if (calculator.isNumberNegative()) {
                calculator.setNumberNegative(false);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextArea().getText());
            }
            else {
                calculator.setNumberNegative(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextArea().getText() + '-');
            }
            //calculator.updateTextAreaValueFromTextArea();
            //calculator.getValues()[calculator.getValuesPosition()] = calculator.getTextArea().getText().reverse().toString();
            //LOGGER.debug("textArea: " + new StringBuffer().append(getTextAreaWithoutNewLineCharacters()).reverse());
            //LOGGER.debug("textAreaValue: " + textAreaValue);
            calculator.confirm("Pressed " + buttonChoice);
        }
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
            LOGGER.info("Programmer dot operations");
            if (calculator.getCalculatorBase() == BINARY)
            {
                LOGGER.warn("IMPLEMENT OR DISABLE");
            }
            else if (calculator.getCalculatorBase() == DECIMAL)
            {
                LOGGER.info("DECIMAL base");
                performDot(buttonChoice);
            }
            else
            {
                LOGGER.warn("IMPLEMENT Add other bases");
            }
        }
        calculator.confirm("Pressed the Dot button");
    }
    private void performDot(String buttonChoice)
    {
        if (StringUtils.isBlank(calculator.getValues()[calculator.getValuesPosition()]) || !calculator.isFirstNumber())
        {   // dot pushed with nothing in textArea
            //calculator.setTextAreaValue(new StringBuffer().append("0").append(buttonChoice));
            calculator.setValuesToTextAreaValue();
            calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        }
        else if (calculator.isPositiveNumber(calculator.getValues()[calculator.getValuesPosition()]) && !calculator.isDotPressed())
        {   // number and then dot is pushed ex: 5 -> .5
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
        calculator.getButtonDot().setEnabled(false); // deactivate button now that its active for this number
        calculator.setDotPressed(true); // control variable used to check if we have pushed the dot button
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
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextAreaWithoutAnyOperator() + "-");
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(' ').append(buttonChoice));
                    calculator.getValues()[calculator.getValuesPosition()] = '-' + calculator.getTextAreaWithoutAnyOperator();
                }
                else
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextAreaWithoutAnyOperator());
                    //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[calculator.getValuesPosition()]).append(' ').append(buttonChoice));
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
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //if (calculatorType == BASIC) calculator.getTextArea().setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty())
            {
                subtract();
                calculator.setSubtracting(calculator.resetOperator(calculator.isSubtracting()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty())
            {
                multiply();
                calculator.setMultiplying(calculator.resetOperator(calculator.isMultiplying()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty())
            {
                divide();
                calculator.setDividing(calculator.resetOperator(calculator.isDividing()));
                calculator.setAdding(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //if (calculatorType == BASIC) textArea.setText(addNewLineCharacters(1) + buttonChoice + " " + textAreaValue);
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + textAreaValue);
            }
            else if (calculator.textArea1ContainsBadText())
            {
                calculator.getTextArea().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // calculator.getValues()[valuesPosition]
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
            //textarea = new StringBuffer().append(convertToPositive(calculator.getValues()[0]));
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(calculator.convertToPositive(calculator.getValues()[0]))));
            calculator.getValues()[0] = calculator.convertToNegative(calculator.getTextArea().getText().toString()).toString();
            calculator.setDotPressed(false);
            calculator.getButtonDot().setEnabled(true);
            calculator.setNumberNegative(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
            LOGGER.info("We have a decimal");
            calculator.getValues()[0] = String.valueOf(result);
        }
        //calculator.setTextAreaValue(new StringBuffer().append(calculator.getValues()[0]));
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
            if (!calculator.isAdding() && !calculator.isSubtracting() && !calculator.isMultiplying() && !calculator.isDividing() &&
                    !calculator.textArea1ContainsBadText()) {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextAreaWithoutAnyOperator());
                //else if (calculatorType == PROGRAMMER) textArea.setText(addNewLineCharacters(3) + buttonChoice + " " + calculator.getTextAreaWithoutAnything());
                //else if (calculatorType == SCIENTIFIC) LOGGER.warn("SETUP");
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setSubtracting(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].equals("")) {
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
            calculator.getValues()[0] = calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result)); // textarea changed to whole number, or int
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
                    && !calculator.textArea1ContainsBadText())
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setMultiplying(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].isEmpty()) {
                addition();
                calculator.resetOperator(calculator.isAdding());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].isEmpty()) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].isEmpty()) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying()); // mulBool = resetOperator(mulBool);
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].isEmpty()) {
                divide();
                calculator.resetOperator(calculator.isDividing());
                calculator.setMultiplying(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.textArea1ContainsBadText()) {
                calculator.getTextArea().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
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
            //calculator.setTextAreaValue(new StringBuffer(calculator.clearZeroesAndDecimalAtEnd(String.valueOf(result))));
            calculator.getValues()[0] = calculator.getTextArea().getText().toString(); // textarea changed to whole number, or int
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
                    && !calculator.textArea1ContainsBadText())
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
                //calculator.updateTextAreaValueFromTextArea();
                calculator.setDividing(true); // sets logic for arithmetic
                calculator.setFirstNumber(false); // sets logic to perform operations when collecting second number
                calculator.setDotPressed(false);
                calculator.setValuesPosition(calculator.getValuesPosition() + 1); // increase valuesPosition for storing textarea
            }
            else if (calculator.isAdding() && !calculator.getValues()[1].equals("")) {
                addition();
                calculator.resetOperator(calculator.isAdding()); // sets addBool to false
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isSubtracting() && !calculator.getValues()[1].equals("")) {
                subtract();
                calculator.resetOperator(calculator.isSubtracting());
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isMultiplying() && !calculator.getValues()[1].equals("")) {
                multiply();
                calculator.resetOperator(calculator.isMultiplying());
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.isDividing() && !calculator.getValues()[1].equals("") & !calculator.getValues()[1].equals("0")) {
                divide();
                calculator.resetOperator(calculator.isDividing()); // divBool = resetOperator(divBool)
                calculator.setDividing(true);
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + buttonChoice + " " + calculator.getTextArea().getText());
            }
            else if (calculator.textArea1ContainsBadText())  {
                calculator.getTextArea().setText(buttonChoice + " " +  calculator.getValues()[0]); // "userInput +" // temp[valuesPosition]
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
            calculator.setDividing(false);
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
    { calculator.confirm("Button not enabled for programmer panel"); }

    private void setupFractionButton()
    {
        calculator.getButtonFraction().setFont(mainFont);
        calculator.getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        calculator.getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        calculator.getButtonFraction().setEnabled(true);
        calculator.getButtonFraction().addActionListener(this::performFractionButtonActions);
    }
    public void performFractionButtonActions(ActionEvent action)
    { calculator.confirm("Button not configured for programmer panel"); }

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
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        // Get the current number first. save
        String numberInTextArea = calculator.getTextAreaWithoutAnyOperator();
        if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonBin.isSelected())
        {
            try { calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
            catch (CalculatorError c) { calculator.logException(c); }
            LOGGER.info("Values[1] saved to {}", calculator.getValues()[1]);
            LOGGER.info("Now performing operation...");
            determineAndPerformBasicCalculatorOperation();
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonOct.isSelected())
        {
            try { calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, numberInTextArea); }
            catch (CalculatorError c) { calculator.logException(c); }
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonDec.isSelected())
        {
            determineAndPerformBasicCalculatorOperation();
        }
        else if (((ProgrammerPanel)calculator.getCurrentPanel()).buttonHex.isSelected())
        {
            calculator.getValues()[0] = "";
            try { calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(HEXADECIMAL, DECIMAL, calculator.getValues()[0]); }
            catch (CalculatorError c) { calculator.logException(c); }
            calculator.getValues()[1] = "";
            try { calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(HEXADECIMAL, DECIMAL, calculator.getValues()[1]); }
            catch (CalculatorError c) { calculator.logException(c); }
        }

        if (isOrPressed)
        {
            ((ProgrammerPanel)calculator.getCurrentPanel()).performOr();
            calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getValues()[0]);

        }
        else if (isModulusPressed)
        {
            LOGGER.info("Modulus result");
            performModulus();
            // update values and textArea accordingly
            calculator.setValuesPosition(0);
            setModulusPressed(false);
        }
        calculator.updateTheTextAreaBasedOnTheTypeAndBase();
        if (calculator.getValues()[0].equals("") && calculator.getValues()[1].equals(""))
        {
            // if temp[0] and temp[1] do not have a number
            calculator.setValuesPosition(0);
        }
        else if (calculator.textArea1ContainsBadText())
        {
            calculator.getTextArea().setText("=" + " " +  calculator.getValues()[calculator.getValuesPosition()]); // "userInput +" // temp[valuesPosition]
            calculator.setValuesPosition(1);
            calculator.setFirstNumber(true);
        }

        calculator.getValues()[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        calculator.getValues()[3] = "";
        //updateTextareaFromTextArea();
        calculator.setFirstNumber(true);
        calculator.setDotPressed(false);
        calculator.setValuesPosition(0);
        calculator.confirm("");
    }

    /* Helper methods */
    public void resetProgrammerOperators()
    {
        buttonModulus.setEnabled(true);
        setModulusPressed(false);
        buttonOr.setEnabled(true);
        setOrPressed(false);
        buttonXor.setEnabled(true);
        setXorPressed(false);
        buttonNot.setEnabled(true);
        setNotPressed(false);
        buttonAnd.setEnabled(true);
        setAndPressed(false);
    }

    public void addBytesToTextArea()
    {
        // the first two rows in the programmer calculator are reserved for bytes
        if (buttonByte.isSelected())
        {
            calculator.getTextArea().setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonWord.isSelected())
        {
            calculator.getTextArea().setText(
                    calculator.addNewLineCharacters(3) +
                    calculator.getValues()[3]);
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonDWord.isSelected())
        {
            calculator.getTextArea().setText(
                    calculator.addNewLineCharacters(2) +
                    "00000000 00000000 00000000 00000000"+
                    calculator.addNewLineCharacters(1) +
                    calculator.getValues()[3]+" 00000000 00000000 00000000");
            //calculator.getTextArea().setWrapStyleWord(true);
        }
        else if (buttonQWord.isSelected())
        {
            calculator.getTextArea().setText(
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 00000000\n" +
                            "00000000 00000000 00000000 00000000\n"+calculator.getValues()[3]+" 00000000 00000000 00000000");
            //calculator.getTextArea().setWrapStyleWord(true);
        }
    }

    /**
     * Converts all getValues(), and restores in getValues()
     */
    public void convertValue0AndDisplayInTextArea() throws CalculatorError
    {
        LOGGER.info("Converting getValues()");
        String convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[calculator.getValuesPosition()]);
        calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + convertedValue);
        LOGGER.info("Values converted");
    }

    public void convertToOctal()
    {
        if ( isBaseBinary ) {
            // logic for Binary to Octal
        } else if ( isBaseDecimal ) {
            // logic for Decimal to Octal
        } else if ( isBaseHexadecimal ) {
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
        String nameOfButton = determineIfProgrammerPanelOperatorWasPushed(); // could be null
        if (previousBase == DECIMAL && currentBase == BINARY ||
                calculator.getCalculatorType() == CalculatorType.BASIC)
        {
            try {
                calculator.getValues()[0] = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[0]);
                calculator.getValues()[1] = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[1]);
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (previousBase == BINARY && currentBase == DECIMAL)
        {
        }
        else
        if (calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace().equals("")) { return; }
        if (buttonBin.isSelected()) {
            // logic for Binary to Decimal
            try {
                calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+
                        calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL,
                                calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
            } catch (CalculatorError e) {
                calculator.logException(e);
            }
        }
        else if (isBaseOctal) {
            // logic for Octal to Decimal
        } else if (isBaseHexadecimal) {
            // logic for Hexadecimal to Decimal
        } else if (buttonDec.isSelected()) {
            if (calculator.getTextArea().getText().equals("")) { return; }
            String operator = determineIfProgrammerPanelOperatorWasPushed();
            if (!operator.equals(""))
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3)+operator+" "+calculator.getValues()[calculator.getValuesPosition()]);
            }
            else
            {
                calculator.getTextArea().setText(calculator.addNewLineCharacters(3)+calculator.getValues()[calculator.getValuesPosition()]);
            }
            //calculator.updateTextAreaValueFromTextArea();
        }

        LOGGER.info("convertToDecimal finished");
    }

    public void convertToHexadecimal()
    {
        if ( isBaseBinary ) {
            // logic for Binary to Hexadecimal
        } else if ( isBaseOctal ) {
            // logic for Octal to Hexadecimal
        } else if ( isBaseDecimal ) {
            // logic for Decimal to Hexadecimal
        }
    }

    /**
     * Resets the 4 main byte as String operators to the
     * boolean passed in
     *
     * @param byteOption a boolean to reset the operators to
     */
    public void resetProgrammerByteOperators(boolean byteOption)
    {

        isByte = byteOption;
        isWord = byteOption;
        isDWord = byteOption;
        isQWord = byteOption;
    }

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
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator_v4
                {
                    if (calculator.isAdding())
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                                + " + " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " +"));
                    }
                    else if (calculator.isSubtracting())
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                                + " - " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " -"));
                    }
                    else if (calculator.isMultiplying())
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                                + " * " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " *"));
                    }
                    else if (calculator.isDividing())
                    {
                        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                                + " / " + convertedValue);
                        //calculator.setTextAreaValue(new StringBuffer().append(convertedValue + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but getTextArea() has some value
                {
                    calculator.getTextArea().setText(calculator.addNewLineCharacters(1)
                            + convertedValue);
                    //calculator.setTextAreaValue(new StringBuffer().append(convertedValue));
                }
            }
        }
        // TODO: add logic for Scientific
    }

    public void setButtons2To9(boolean isEnabled)
    {
        Collection<JButton> buttonsWithout0Or1 = calculator.getNumberButtons();
        buttonsWithout0Or1.removeIf(btn -> btn.getName().equals("0") || btn.getName().equals("1"));
        buttonsWithout0Or1.forEach(button -> button.setEnabled(isEnabled));
    }

    public void setButtonsAToF(boolean isEnabled)
    { getAToFButtons().forEach(button -> button.setEnabled(isEnabled)); }

    public void setTheBytesBasedOnTheNumbersLength(String number)
    {
        LOGGER.info("setting the bytes based on the length of the number");
        int lengthOfNumber = number.length();
        resetProgrammerByteOperators(false);
        if (lengthOfNumber <= 8) { buttonByte.setSelected(true); setByte(true); }
        else if (lengthOfNumber <= 16) { buttonWord.setSelected(true); setWord(true); }
        else if (lengthOfNumber <= 32) { buttonDWord.setSelected(true); setDWord(true); }
        else { buttonQWord.setSelected(true); setQWord(true); }
        LOGGER.info("setButtonByteSet();: " + isByte());
        LOGGER.info("isButtonWordSet: " + isWord());
        LOGGER.info("isButtonDWordSet: " + isDWord());
        LOGGER.info("isButtonQWordSet: " + isQWord());
    }

    public void updateTheTextAreaBasedOnTheTypeAndBase()
    {
        if (BINARY == calculator.getCalculatorBase()) {
            String convertedToBinary = "";
            try {
                convertedToBinary = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[0]);
            } catch (CalculatorError c) {
                calculator.logException(c);
            }
            calculator.getTextArea().setText(calculator.addNewLineCharacters() + convertedToBinary);
        } else if (DECIMAL == calculator.getCalculatorBase()) {
            if (!calculator.isNumberNegative())
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.getTextArea().getText());
            else {
                calculator.getTextArea().setText(calculator.addNewLineCharacters() + calculator.convertToPositive(calculator.getValues()[0]) + "-");
                //calculator.setTextAreaValue(new StringBuffer("-" + calculator.getValues()[calculator.getValuesPosition()]));
                calculator.setNumberNegative(true);
            }
        } else if (OCTAL == calculator.getCalculatorBase()) {
            LOGGER.warn("Setup");
        } else /* (calculatorBase == HEXADECIMAL) */ {
            LOGGER.warn("Setup");
        }
    }

    public void determineAndPerformBasicCalculatorOperation()
    {
        if (calculator.isAdding()) {
            //addition();
            calculator.setAdding(calculator.resetOperator(calculator.isAdding()));
        }
        else if (calculator.isSubtracting()){
            //subtract();
            calculator.setSubtracting(calculator.resetOperator(calculator.isSubtracting()));
        }
        else if (calculator.isMultiplying()){
            //multiply();
            calculator.setMultiplying(calculator.resetOperator(calculator.isMultiplying()));
        }
        else if (calculator.isDividing()){
            //divide();
            calculator.setDividing(calculator.resetOperator(calculator.isDividing()));
        }
    }

    /**
     * Used to determine what the String is of the button pushed.
     * Will use
     * @return String the name of the button pushed
     */
    public String determineIfProgrammerPanelOperatorWasPushed()
    {
        String results = "";
        // what operations can be pushed: MOD, OR, XOR, NOT, AND
        if (isModulusPressed) { results = "MOD"; }
        else if (isOrPressed) { results = "OR"; }
        else if (isXorPressed) { results = "XOR"; }
        else if (isNotPressed) { results = "NOT"; }
        else if (isAndPressed) { results = "AND"; }
        if (results.isEmpty()) { results = ((BasicPanel)calculator.getCurrentPanel()).determineIfBasicPanelOperatorWasPushed(); }
        LOGGER.info("operator: " + results);

        return results;
    }
//    public String determineIfBasicPanelOperatorWasPushed()
//    {
//        String results = "";
//        if (calculator.isAdding()) { results = "+"; }
//        else if (calculator.isSubtracting()) { results = "-"; }
//        else if (calculator.isMultiplying()) { results = "*"; }
//        else if (calculator.isDividing()) { results = "/"; }
//        LOGGER.info("operator: " + (results.equals("") ? "no basic operator pushed" : results));
//        return results;
//    }

    public String addZeroesToNumber(String number)
    {
        int lengthOfNumber = number.length();
        int zeroesToAdd = 0;
        setTheBytesBasedOnTheNumbersLength(number);
        if (isByte) {
            zeroesToAdd = 8 - lengthOfNumber;
        } else if (isWord) {
            zeroesToAdd = 16 - lengthOfNumber;
        } else if (isDWord) {
            zeroesToAdd = 32 - lengthOfNumber;
        } else /* (isButtonQWordSet) */ {
            zeroesToAdd = 64 - lengthOfNumber;
        }
        if (zeroesToAdd != 0) {
            LOGGER.debug("zeroesToAdd: " + zeroesToAdd);
            StringBuffer zeroes = new StringBuffer();
            for (int i = 0; i < zeroesToAdd; i++) zeroes = zeroes.append("0");
            number = zeroes + number;
            LOGGER.info("After adding zeroes: " + number);
        } else {
            LOGGER.info("Not adding any zeroes. Number appropriate length.");
        }
        return number;
    }

    /**
     * Returns the String representation of the number
     * of bytes to return
     *
     * @return String the number of bytes in String format
     */
    public String getByteWord()
    {
        if (getBytes() == 8) return "Byte";
        else if (getBytes() == 16) return "Word";
        else if (getBytes() == 32) return "DWord";
        else return "QWord";
    }

    /**
     * Returns the number of bytes to show
     *
     * @return int the number of bytes
     */
    public int getBytes()
    {
        if (isByte) {
            return 8;
        } else if (isWord) {
            return 16;
        } else if (isDWord) {
            return 32;
        } else {
            return 64;
        }
    }

    public void performProgrammerCalculatorTypeSwitchOperations(Calculator_v4 calculator, CalculatorBase base )
    { setupProgrammerPanel(calculator, base); }

    /************* All Getters ******************/
    public boolean isBaseBinary() { return isBaseBinary; }
    public boolean isBaseOctal() { return isBaseOctal; }
    public boolean isBaseDecimal() { return isBaseDecimal; }
    public boolean isBaseHexadecimal() { return isBaseHexadecimal; }
    public boolean isByte() { return isByte; }
    public boolean isWord() { return isWord; }
    public boolean isDWord() { return isDWord; }
    public boolean isQWord() { return isQWord; }
    public boolean isOrPressed() { return isOrPressed; }
    public boolean isModulusPressed() { return isModulusPressed; }
    public boolean isXorPressed() { return isXorPressed; }
    public boolean isNegateButtonBool() { return negateButtonBool; }
    public boolean isNotPressed() { return isNotPressed; }
    public boolean isAndPressed() { return isAndPressed; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout panelLayout) {
        super.setLayout(panelLayout);
        this.programmerLayout = panelLayout;
    }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setCalculator(Calculator_v4 calculator) { this.calculator = calculator; }
    public void setByte(boolean buttonByteSet) { isByte = buttonByteSet; }
    public void setWord(boolean buttonWordSet) { isWord = buttonWordSet; }
    public void setDWord(boolean buttonDwordSet) { isDWord = buttonDwordSet; }
    public void setQWord(boolean buttonQWordSet) { isQWord = buttonQWordSet; }
    public void setBaseBinary(boolean baseBinary) { this.isBaseBinary = baseBinary; }
    public void setBaseOctal(boolean baseOctal) { this.isBaseOctal = baseOctal; }
    public void setBaseDecimal(boolean baseDecimal) { this.isBaseDecimal = baseDecimal; }
    public void setBaseHexadecimal(boolean baseHexadecimal) { this.isBaseHexadecimal = baseHexadecimal; }
    public void setOrPressed(boolean orPressed) { this.isOrPressed = orPressed; }
    public void setModulusPressed(boolean modulusPressed) { this.isModulusPressed = modulusPressed; }
    public void setXorPressed(boolean xorPressed) {this.isXorPressed = xorPressed; }
    public void setNotPressed(boolean notPressed) {this.isNotPressed = notPressed; }
    public void setAndPressed(boolean andPressed) {this.isAndPressed = andPressed; }
}