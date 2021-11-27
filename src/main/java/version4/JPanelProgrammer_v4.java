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
import java.util.concurrent.atomic.AtomicInteger;

import static version4.CalcType_v4.BASIC;

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
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        getLogger().info("Finished setting up programmer panel");
    }

    /************* Start of methods here ******************/
    public void setupProgrammerPanel() throws CalculatorError_v4
    {
        getLogger().info("Set up programmer panel");
        getCalculator().getTextArea1().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getCalculator().getTextArea1().setFont(Calculator_v4.font);
        getCalculator().getTextArea1().setBorder(new LineBorder(Color.BLACK));
        getCalculator().getTextArea1().setPreferredSize(new Dimension(105, 60)); //70, 35
        getCalculator().getTextArea1().setEditable(false);
        getLogger().info("TextArea1 setup");
        getCalculator().getTextArea2().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        getCalculator().getTextArea2().setFont(Calculator_v4.font);
        getCalculator().getTextArea2().setBorder(new LineBorder(Color.BLACK));
        getCalculator().getTextArea2().setPreferredSize(new Dimension(105, 60)); //70, 35
        getCalculator().getTextArea2().setEditable(false);
        getLogger().info("TextArea2 setup");
        getCalculator().getButtonNegate().setEnabled(false);
        // TODO: change based on number coming from basic or scientific
        getButtonByte().setSelected(true);
        getButtonBin().setSelected(true);

        getButtonSqrt().setFont(Calculator_v4.font);
        getButtonSqrt().setPreferredSize(new Dimension(35, 35) );
        getButtonSqrt().setBorder(new LineBorder(Color.BLACK));
        getButtonSqrt().setEnabled(false);

        getButtonPercent().setFont(Calculator_v4.font);
        getButtonPercent().setPreferredSize(new Dimension(35, 35) );
        getButtonPercent().setBorder(new LineBorder(Color.BLACK));
        getButtonPercent().setEnabled(false);

        getButtonFraction().setFont(Calculator_v4.font);
        getButtonFraction().setPreferredSize(new Dimension(35, 35) );
        getButtonFraction().setBorder(new LineBorder(Color.BLACK));
        getButtonFraction().setEnabled(false);
        // add buttons to buttonGroupOne
        getButtonHex().addActionListener(action -> {});
        getButtonDec().addActionListener(action -> {
            getLogger().debug("Button Dec selected");
            String baseChoice = action.getActionCommand();
            getLogger().info("base: " + baseChoice);
            //TODO: reset other options to selected, false
            getButtonDec().setSelected(true);
            setButtons2To9(true);
            setButtonsAToF(false);
            // determine previous base
            CalcType_v4 previousBase = getCalculator().getBase();
            getLogger().info("previous base: " + previousBase);
            getLogger().info("will set base to: " + CalcType_v4.DECIMAL);

            // how should we convert the textarea. change number and keep possible operation
            String nameOfButton = determineIfProgrammerOperatorWasPushed(); // could be null
            if (nameOfButton == null)
            {
                convertToDecimal();
                getCalculator().confirm();
                return;
            }
            else if (previousBase == CalcType_v4.BINARY)
            {
                //getCalculator().convertAllValuesToBinary();
                //convertTextArea(previousBase);
                convertToDecimal();
            }
            else if (previousBase == CalcType_v4.HEXIDECIMAL)
            {
                getLogger().debug("IMPLEMENT");
            }
            else if (previousBase == CalcType_v4.OCTAL)
            {
                getLogger().debug("IMPLEMENT");
            }
            getCalculator().confirm();

        });
        getButtonOct().addActionListener(action -> {});
        getButtonBin().addActionListener(action -> {
            getLogger().debug("Button Bin selected");
            String baseChoice = action.getActionCommand();
            getLogger().info("baseChoice: " + baseChoice);
            //TODO: set other options to selected, false
            getButtonBin().setSelected(true);
            setButtons2To9(false);
            getButtonA().setEnabled(false);
            getButtonB().setEnabled(false);
            getButtonC().setEnabled(false);
            getButtonD().setEnabled(false);
            getButtonE().setEnabled(false);
            getButtonF().setEnabled(false);
            // determine previous base
            CalcType_v4 previousBase = getCalculator().getBase();
            getLogger().info("previous base: " + previousBase);
            getLogger().info("will set base to: " + CalcType_v4.BINARY);

            // how should we convert the textarea. change number and keep possible operation
            String nameOfButton = determineIfProgrammerOperatorWasPushed(); // could be null
            if (nameOfButton == null)
            {
                convertValues(CalcType_v4.BINARY);
                getCalculator().confirm();
                return;
            }
            else if (previousBase == CalcType_v4.DECIMAL)
            {
                convertValues(CalcType_v4.BINARY);
            }
            else if (previousBase == CalcType_v4.HEXIDECIMAL)
            {
                getLogger().debug("IMPLEMENT");
            }
            else if (previousBase == CalcType_v4.OCTAL)
            {
                getLogger().debug("IMPLEMENT");
            }
            getCalculator().confirm();
        });
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

        buttonMod.setFont(this.calculator.font);
        buttonMod.setPreferredSize(new Dimension(35,35));
        buttonMod.setBorder(new LineBorder(Color.BLACK));
        buttonMod.addActionListener(action -> {
            performButtonModActions(action);
        });

        buttonLPar.setFont(this.calculator.font);
        buttonLPar.setPreferredSize(new Dimension(35,35));
        buttonLPar.setBorder(new LineBorder(Color.BLACK));

        buttonRPar.setFont(this.calculator.font);
        buttonRPar.setPreferredSize(new Dimension(35,35));
        buttonRPar.setBorder(new LineBorder(Color.BLACK));

        buttonRol.setFont(this.calculator.font);
        buttonRol.setPreferredSize(new Dimension(35,35));
        buttonRol.setBorder(new LineBorder(Color.BLACK));

        buttonRor.setFont(this.calculator.font);
        buttonRor.setPreferredSize(new Dimension(35,35));
        buttonRor.setBorder(new LineBorder(Color.BLACK));

        buttonOr.setFont(this.calculator.font);
        buttonOr.setPreferredSize(new Dimension(35,35));
        buttonOr.setBorder(new LineBorder(Color.BLACK));
        buttonOr.addActionListener(action -> {
            try {
                performButtonOrActions(action);
            } catch (CalculatorError_v4 calculator_ErrorV4) {
                LOGGER.error(calculator_ErrorV4.getMessage());
            }
        });

        buttonXor.setFont(this.calculator.font);
        buttonXor.setPreferredSize(new Dimension(35,35));
        buttonXor.setBorder(new LineBorder(Color.BLACK));

        buttonLSh.setFont(this.calculator.font);
        buttonLSh.setPreferredSize(new Dimension(35,35));
        buttonLSh.setBorder(new LineBorder(Color.BLACK));

        buttonRSh.setFont(this.calculator.font);
        buttonRSh.setPreferredSize(new Dimension(35,35));
        buttonRSh.setBorder(new LineBorder(Color.BLACK));

        buttonNot.setFont(this.calculator.font);
        buttonNot.setPreferredSize(new Dimension(35,35));
        buttonNot.setBorder(new LineBorder(Color.BLACK));
        buttonNot.addActionListener(action -> {
            performButtonNotActions(action);
        });

        buttonAnd.setFont(this.calculator.font);
        buttonAnd.setPreferredSize(new Dimension(35,35));
        buttonAnd.setBorder(new LineBorder(Color.BLACK));
        // A - F
        buttonA.setFont(this.calculator.font);
        buttonA.setPreferredSize(new Dimension(35, 35) );
        buttonA.setBorder(new LineBorder(Color.BLACK));

        buttonB.setFont(this.calculator.font);
        buttonB.setPreferredSize(new Dimension(35, 35) );
        buttonB.setBorder(new LineBorder(Color.BLACK));

        buttonC.setFont(this.calculator.font);
        buttonC.setPreferredSize(new Dimension(35, 35));
        buttonC.setBorder(new LineBorder(Color.BLACK));

        buttonD.setFont(this.calculator.font);
        buttonD.setPreferredSize(new Dimension(35, 35));
        buttonD.setBorder(new LineBorder(Color.BLACK));

        buttonE.setFont(this.calculator.font);
        buttonE.setPreferredSize(new Dimension(35, 35));
        buttonE.setBorder(new LineBorder(Color.BLACK));

        buttonF.setFont(this.calculator.font);
        buttonF.setPreferredSize(new Dimension(35, 35));
        buttonF.setBorder(new LineBorder(Color.BLACK));

        //setTopLeftBytesLabel(new JLabel());
        //setBottomLeftBytesLabel(new JLabel());
        //getTopLeftBytesLabel().setText("00000000" + TEXTAREA3_SPACE + "00000000" + TEXTAREA3_SPACE + "00000000" + TEXTAREA3_SPACE + "00000000");
        //getBottomLeftBytesLabel().setText("00000000" + TEXTAREA3_SPACE + "00000000" + TEXTAREA3_SPACE + "00000000" + TEXTAREA3_SPACE + "00000000");
        //getTopLeftBytesLabel().setEnabled(false);

        getCalculator().setupMemoryButtons(); // MR MC MS M+ M-
        getCalculator().setupBasicCalculatorOperationButtons(); // + - * /
        getCalculator().setupOtherBasicCalculatorButtons(); // = Negate
        getCalculator().setupOtherCalculatorButtons(); // C CE DEL Dot
        setupNumberButtons(true);
        setButtons2To9(false);
        setButtonsAToF(false);

        LOGGER.info("End setupProgrammerPanel");
    }

    public void addComponentsToPanel()
    {
        LOGGER.info("Starting addComponentsToProgrammerPanel");
        //constraints.insets = new Insets(5,5,5,5);
        //addComponent(getTopLeftBytesLabel(), 0,0, 0, 1);
        //addComponent(getTopRightBytesLabel(), 0, 5, 0, 1);
        constraints.insets = new Insets(5,0,5,0); //9905
        addComponent(getCalculator().getTextArea1(), 0, 0, 9, 2);
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
        setComponent(getCalculator().buttonMemoryClear, 0, 3, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryRecall, 0, 4, 1, 1, otherButtonLayout);
        setComponent(getCalculator().buttonMemoryStore, 0, 5, 1, 1, otherButtonLayout);
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
        getLogger().info("Starting to switch panels...");
        // possible conversion of the value in the textarea from
        // whatever mode it was in before to binary
        setupProgrammerPanel();
        getLogger().info("Programmer panel setup");
        convertValues();
        addBytesToTextArea1();
        LOGGER.info("Finished performProgrammerCalculatorTypeSwitchOperations");
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

    public void performProgrammerCalculatorNumberButtonActions(ActionEvent actionEvent)
    {
        LOGGER.info("Starting programmer calculator number button actions");
        getLogger().info("buttonChoice: " + actionEvent.getActionCommand());
        if (getCalculator().firstNumBool)
        {
            performProgrammerNumberButtonActions(actionEvent.getActionCommand());
        }
        else
        {
            getCalculator().firstNumBool = true;
            getCalculator().textArea2.setText("");
            getCalculator().textarea = new StringBuffer();
            getCalculator().valuesPosition = 1;
            performProgrammerNumberButtonActions(actionEvent.getActionCommand());
        }
    }

    public void performProgrammerNumberButtonActions(String buttonChoice)
    {
        getCalculator().performInitialChecks();
        LOGGER.info("Performing programmer actions...");
        if (getCalculator().getTextArea2().getText().length() > getCalculator().getBytes())
        {
            return; // don't allow length to get any longer
        }
        if (getCalculator().getTextArea2().getText().equals(""))
        {
            getCalculator().getTextArea2().setText(getCalculator().addNewLineCharacters(1) + buttonChoice);
        }
        else
        {
            getCalculator().getTextArea2().setText(getCalculator().addNewLineCharacters(1) +
                    getCalculator().getTextAreaWithoutNewLineCharacters() + buttonChoice); // update textArea
        }
        getCalculator().updateTextareaFromTextArea();
        getCalculator().values[getCalculator().valuesPosition] = getCalculator().textarea.toString(); // store textarea in values
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
            if (!calculator.getTextArea1().getText().equals(""))
                convertValues();
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
            if (!calculator.getTextArea1().getText().equals(""))
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
            if (!calculator.getTextArea1().getText().equals(""))
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
            if (!calculator.getTextArea1().getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ButtonByteHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 8) {
                if (calculator.getTextArea1().getText().equals(""))
                    calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(8,16));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 16) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,16));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonDWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 32) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,32));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class ButtonQWordHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 64) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,64));
                calculator.getTextArea1().setText(calculator.textarea.toString());
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
            if (!calculator.getTextArea1().getText().equals(""))
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
            if (!calculator.getTextArea1().getText().equals(""))
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
            if (!calculator.getTextArea1().getText().equals(""))
                convertToHexadecimal();
        }
    }
    public class ByteButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 8) {
                if (calculator.getTextArea1().getText().equals(""))
                    calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(8,16));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class WordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 16) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,16));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class DWordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 32) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,32));
                calculator.getTextArea1().setText(calculator.textarea.toString());
            }
        }
    }
    public class QWordButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (calculator.getTextArea1().getText().length() > 64) {
                calculator.textarea = new StringBuffer().append(calculator.getTextArea1().getText());
                calculator.textarea = new StringBuffer().append(calculator.textarea.substring(0,64));
                calculator.getTextArea1().setText(calculator.textarea.toString());
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
        calculator.textarea = new StringBuffer(calculator.getTextArea1().getText().replaceAll("\n", ""));
        LOGGER.debug("before operation execution: " + calculator.textarea.toString());
        StringBuffer newBuffer = new StringBuffer();
        for (int i = 0; i < calculator.textarea.length(); i++) {
            String s = Character.toString(calculator.textarea.charAt(i));
            if (s.equals("0")) { newBuffer.append("1"); LOGGER.debug("appending a 1"); }
            else               { newBuffer.append("0"); LOGGER.debug("appending a 0"); }
        }
        LOGGER.debug("after operation execution: " + newBuffer);
        calculator.textarea = new StringBuffer(newBuffer);
        calculator.getTextArea1().setText("\n"+calculator.textarea.toString());
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
            getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)+
                    getCalculator().getTextAreaWithoutNewLineCharacters() + " " + "XOR");
        }
        else if (!StringUtils.isEmpty(getCalculator().getValues()[0]) && !StringUtils.isEmpty(getCalculator().getValues()[1]))
        {
            performXor();
            getCalculator().performValuesConversion();
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
            getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
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
            calculator.getTextArea1().setText(calculator.addNewLineCharacters(1)+calculator.values[0]);
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
            getCalculator().getTextArea1().setText(getCalculator().getTextArea1().getText() + " " + buttonChoice);
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

    public String determineIfProgrammerOperatorWasPushed()
    {
        String results = "";
        // what operations can be pushed: MOD, OR, XOR, NOT, and AND
        if (getCalculator().isModButtonPressed()) { results = "MOD_true"; }
        else if (getCalculator().isOrButtonPressed()) { results = "OR_true"; }
        else if (getCalculator().isXorButtonPressed()) { results = "XOR_true"; }
        else if (getCalculator().isNotButtonPressed()) { results = "NOT_true"; }
        else if (getCalculator().isAndButtonPressed()) { results = "AND_true"; }
        getLogger().info("results from determineIfProgrammerOperatorWasPushed: " + (results.equals("") ? "no operator pushed" : results));
        return results;
    }

    public JButton getOperatorFromName(String operatorName) {
        switch(operatorName)
        {
            case "MOD": return getButtonMod();
            case "OR": return getButtonOr();
            case "XOR": return getButtonXor();
            case "NOT": return getButtonNot();
            case "AND": return getButtonAnd();
            default: getLogger().error("Add new case or fix logic: " + operatorName);
        }
        return null;
    }

    public void addBytesToTextArea1()
    {
        // the first two rows in the programmer calculator are reserved for bytes
        if (getButtonByte().isSelected())
        {
            getCalculator().getTextArea1().setText(
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 00000000\n" +
                    "00000000 00000000 00000000 00000000\n00000000 00000000 00000000 "+getCalculator().getValues()[3]);
            getCalculator().getTextArea1().setWrapStyleWord(true);
        }
        else if (getButtonWord().isSelected())
        {
            getLogger().warn("Need to add logic");
        }
        else if (getButtonDWord().isSelected())
        {
            getLogger().warn("Need to add logic");
        }
        else if (getButtonQWord().isSelected())
        {
            getLogger().warn("Need to add logic");
        }
    }

    // TODO: fix logic
    public void convertValues(CalcType_v4 newBase)
    {
        CalcType_v4 previousBase = getCalculator().getBase();
        getCalculator().setCalcType(newBase);
        CalcType_v4 currentBase = getCalculator().getCalcType();
        String nameOfButton = determineIfProgrammerOperatorWasPushed(); // could be null
        if (previousBase == CalcType_v4.BASIC && currentBase == CalcType_v4.PROGRAMMER)
        {
            LOGGER.debug("Going from BASIC to PROGRAMMER calculator");
            calculator.values = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.DECIMAL.getName(), CalcType_v4.BINARY.getName(), calculator.values);
        }
        else if (previousBase == CalcType_v4.PROGRAMMER && currentBase == CalcType_v4.BASIC)
        {
            LOGGER.debug("Going from PROGRAMMER to BASIC calculator");
            calculator.values = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.BINARY.getName(), CalcType_v4.DECIMAL.getName(), calculator.values);
        }
        else if (previousBase == CalcType_v4.DECIMAL && currentBase == CalcType_v4.BINARY)
        {
            calculator.values = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.DECIMAL.getName(), CalcType_v4.BINARY.getName(), calculator.values);
        }

        if (calculator.isButtonOctSet)
        {
            // logic for Octal to Binary
        }
        else if (calculator.isButtonDecSet)
        {
            // logic for Decimal to Binary

        }
        else if (getButtonBin().isSelected())
        {
            if (calculator.getTextArea1().getText().equals("")) { return; }
            if (calculator.getTextArea1().getText().length()==calculator.getBytes()) { return; }
            String[] temp = getCalculator().getTextarea().toString().split(" ");
            boolean length = temp.length > 1 ? true : false;
            String operator = "";
            if (length) { operator = temp[1]; } else { operator = nameOfButton.split("_")[0]; }
            boolean operatorIncluded = false;
            switch(operator)
            {
                case "+" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.addBool = true; break;
                case "-" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.subBool = true; break;
                case "*" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.mulBool = true; break;
                case "/" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.divBool = true; break;
                case "MOD" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "OR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "XOR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "NOT" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "AND" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "" : operatorIncluded = false; LOGGER.debug("operator not set"); break;
                default : getLogger().error("Add operator or fix unknown option: " + operator);
            }

            if (operatorIncluded)
            {
                getLogger().info("operator included");
//                calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+operator+" "+calculator.values[calculator.valuesPosition]);
                getCalculator().getTextArea1().setText("\n" + calculator.values[calculator.valuesPosition-1] + operator + " "); // update textArea
                calculator.updateTextareaFromTextArea();
            }
            else
            {
                // after updating values, we update textArea and text area
                getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)+calculator.values[calculator.valuesPosition]);
                getCalculator().updateTextareaFromTextArea();
                LOGGER.debug("TextArea: {} and textarea {}",getCalculator().getTextAreaWithoutNewLineCharacters(), getCalculator().getTextarea() );
                // then reset values to decimal form so we can always see the decimal number for debugging
                calculator.values = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.BINARY.getName(), CalcType_v4.DECIMAL.getName(), calculator.values);
            }

        }
        else if (calculator.isButtonHexSet == true)
        {
            // logic for Hexadecimal to Binary
        }
        //calculator.values = calculator.convertFromTypeToTypeOnValues(CalcType_v3.BINARY2.getName(), CalcType_v3.DECIMAL.getName(), calculator.values);
    }

    /**
     * Converts the current value into binary and stores in values[3]
     */
    public void convertValues()
    {
        LOGGER.info("convertToBinary started");
        LOGGER.info("textarea: " + calculator.textarea);
        // determine previous base
        CalcType_v4 previousBase = getCalculator().getBase();
        getLogger().info("previous base: " + previousBase);
        getLogger().info("will set base to: " + CalcType_v4.BINARY);
        String convertedValue = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.DECIMAL.getName(), CalcType_v4.BINARY.getName(), calculator.values[calculator.valuesPosition])[0];
        getCalculator().getValues()[3] = convertedValue;
        if (calculator.isButtonOctSet)
        {
            // logic for Octal to Binary
        }
        else if (calculator.isButtonDecSet)
        {
            // logic for Decimal to Binary

        }
        else if (getButtonBin().isSelected())
        {
            if (calculator.getTextArea2().getText().equals("")) { return; }
            if (calculator.getTextArea2().getText().length()==calculator.getBytes()) {
                getLogger().warn("textArea2.length is equal to calculator.getBytes(), exiting");
                return;
            }
//            calculator.textarea = new StringBuffer().append(calculator.getTextAreaWithoutNewLineCharacters());
//            getLogger().info("textarea: " + calculator.textarea);
            String[] temp = getCalculator().getTextarea().toString().split(" ");
            boolean length = temp.length > 1 ? true : false;
            String operator = "";
            if (length) { operator = temp[1]; }
            boolean operatorIncluded = false;
            switch(operator)
            {
                case "+" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.addBool = true; break;
                case "-" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.subBool = true; break;
                case "*" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.mulBool = true; break;
                case "/" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.divBool = true; break;
                case "MOD" : operatorIncluded = true; LOGGER.debug("operator: " + operator); operator = "MOD"; break;
                case "OR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); operator = "OR"; break;
                case "XOR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); operator = "XOR"; break;
                case "NOT" : operatorIncluded = true; LOGGER.debug("operator: " + operator); operator = "NOT"; break;
                case "AND" : operatorIncluded = true; LOGGER.debug("operator: " + operator); operator = "AND"; break;
                case "" : operatorIncluded = false; LOGGER.debug("no operator pushed"); break;
                default : getLogger().error("Add operator or fix unknown option: " + operator);
            }

            if (operatorIncluded)
            {
                calculator.getTextArea2().setText(calculator.addNewLineCharacters(4)+operator+" "+convertedValue);
            }
            else
            {
                // KEEP CALCULATOR.VALUES ALWAYS REGULAR NUMBER
                calculator.getTextArea2().setText(calculator.addNewLineCharacters(4)+convertedValue);
            }
            calculator.updateTextareaFromTextArea();
        }
        else if (calculator.isButtonHexSet == true)
        {
            // logic for Hexadecimal to Binary
        }
        getLogger().info("convertToBinary finished");
    }

    public void convertToOctal() {
        if (calculator.isButtonBinSet == true) {
            // logic for Binary to Octal
        } else if (calculator.isButtonDecSet == true) {
            // logic for Decimal to Octal
        } else if (calculator.isButtonHexSet == true) {
            // logic for Hexadecimal to Octal
        }
    }
    public void convertToDecimal() {
        getLogger().debug("convertToDecimal starting");
        CalcType_v4 previousBase = getCalculator().getBase();
        // update the current base to binary
        getCalculator().setBase(CalcType_v4.DECIMAL);
        CalcType_v4 currentBase = getCalculator().getBase();
        getLogger().info("previous base: " + previousBase);
        getLogger().info("current base: " + currentBase);
        String nameOfButton = determineIfProgrammerOperatorWasPushed(); // could be null
        if (previousBase == CalcType_v4.DECIMAL && currentBase == CalcType_v4.BINARY ||
                getCalculator().getCalcType() == CalcType_v4.BASIC)
        {
            calculator.values = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.DECIMAL.getName(), CalcType_v4.BINARY.getName(), calculator.values);
        }
        else if (previousBase == CalcType_v4.BINARY && currentBase == CalcType_v4.DECIMAL)
        {
        }
        else
        if (getCalculator().getTextAreaWithoutNewLineCharacters().equals("")) { return; }
        if (getButtonBin().isSelected()) {
            // logic for Binary to Decimal
            getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)+
                    getCalculator().convertFromTypeToTypeOnValues("Binary", "Decimal",
                            getCalculator().getTextAreaWithoutNewLineCharacters())[0]);
        }
        else if (calculator.isButtonOctSet == true) {
            // logic for Octal to Decimal
        } else if (calculator.isButtonHexSet == true) {
            // logic for Hexadecimal to Decimal
        } else if (getButtonDec().isSelected()) {
            if (calculator.getTextArea1().getText().equals("")) { return; }
            String[] temp = getCalculator().getTextarea().toString().split(" ");
            boolean length = temp.length > 1 ? true : false;
            String operator = "";
            if (length) { operator = temp[1]; } else { operator = nameOfButton.split("_")[0]; }
            boolean operatorIncluded = false;
            switch(operator)
            {
                case "+" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.addBool = true; break;
                case "-" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.subBool = true; break;
                case "*" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.mulBool = true; break;
                case "/" : operatorIncluded = true; LOGGER.debug("operator: " + operator); calculator.divBool = true; break;
                case "MOD" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "OR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "XOR" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "NOT" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "AND" : operatorIncluded = true; LOGGER.debug("operator: " + operator); break;
                case "" : operatorIncluded = false; LOGGER.debug("operator not set"); break;
                default : getLogger().error("Add operator or fix unknown option: " + operator);
            }

            if (operatorIncluded)
            {

                calculator.getTextArea1().setText(calculator.addNewLineCharacters(1)+operator+" "+calculator.values[calculator.valuesPosition]);
                calculator.updateTextareaFromTextArea();
            }
            else
            {
                calculator.getTextArea1().setText(calculator.addNewLineCharacters(1)+calculator.values[calculator.valuesPosition]);
                calculator.updateTextareaFromTextArea();
            }
        }

        getLogger().info("convertToDecimal finished");
    }
    public void convertToHexadecimal() {
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
        if (getCalculator().getCalcType() == CalcType_v4.BASIC)
        {
            getLogger().debug("Going from Basic to Programmer");
            calculator.performInitialChecks();
            boolean operatorWasPushed = getCalculator().determineIfMainOperatorWasPushed();
            String convertedValue = getCalculator().convertFromTypeToTypeOnValues(CalcType_v4.BASIC.getName(), CalcType_v4.PROGRAMMER.getName(), getCalculator().getValues()[0])[0];
            if (StringUtils.isNotBlank(getCalculator().getTextAreaWithoutNewLineCharacters()))
            {
                if (operatorWasPushed) // check all appropriate operators from Programmer calculator that are applicable for Basic Calculator
                {
                    if (getCalculator().addBool)
                    {
                        getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
                                + " + " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " +"));
                    }
                    else if (getCalculator().subBool)
                    {
                        getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
                                + " - " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " -"));
                    }
                    else if (getCalculator().mulBool)
                    {
                        getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
                                + " * " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " *"));
                    }
                    else if (getCalculator().divBool)
                    {
                        getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
                                + " / " + convertedValue);
                        getCalculator().setTextarea(new StringBuffer().append(convertedValue + " /"));
                    }
                    // while coming from PROGRAMMER, some operators we don't care about coming from that CalcType
                }
                else // operator not pushed but textArea has some value
                {
                    getCalculator().getTextArea1().setText(getCalculator().addNewLineCharacters(1)
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
