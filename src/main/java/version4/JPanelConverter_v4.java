package version4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

import static version4.Calculator_v4.font2;

public class JPanelConverter_v4 extends JPanel
{
    protected final static Logger LOGGER;
    static
    {
        System.setProperty("appName", "JPanelDate_v4");
        LOGGER = LogManager.getLogger(JPanelBasic_v4.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout converterLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    private JLabel converterTypeName;
    private JTextField option1;
    private JTextField option2;
    private JComboBox<String> unitOptions1;
    private JComboBox<String> unitOptions2;
    private JTextArea bottomSpaceAboveNumbers;
    private Calculator_v4 calculator;
    private ConverterType_v4 converterType;
    private JPanel numbersPanel;
    // TODO: move up to Calculator class, remove from JPanelProgrammer_v4
    private JButton buttonBlank;


    /************* Constructor ******************/
    public JPanelConverter_v4(StandardCalculator_v4 calculator, ConverterType_v4 converterType) throws ParseException, CalculatorError_v4
    {
        setCalculator(calculator);
        setConverterType(converterType);
        setMinimumSize(new Dimension(300,400));
        setConverterLayout(new GridBagLayout());
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setLayout(getConverterLayout());
        setupJPanelConverter_v4();
        addStartupComponentsToJPanelConverter_v4();
    }

    /************* Start of methods here ******************/
    private void setupJPanelConverter_v4() throws ParseException, CalculatorError_v4
    {
        LOGGER.info("Starting setupJPanelConverter_v4");
        switch (converterType)
        {
            case ANGLE: {
                setupAngleConverter();
                break;
            }
            case AREA: {
                setupAreaConverter();
                break;
            }
            default: {
                LOGGER.error("Unknown converterType: " + converterType);
                throw new CalculatorError_v4("Unknown converterType: " + converterType);
            }
        }
        LOGGER.info("Finished setupJPanelConverter_v4");
    }

    private void addStartupComponentsToJPanelConverter_v4()
    {
        LOGGER.info("Starting addStartupComponentsToJPanelConverter_v4");
        // all the following components are added no matter the option selected

        addComponent(getConverterTypeName(), 0,0,1,1, 1.0,1.0);
        addComponent(getOption1(), 1, 0, 0, 1,1.0,1.0);
        addComponent(getUnitOptions1(), 3, 0, 1,1, 1.0,1.0);
        addComponent(getOption2(), 4, 0, 1, 1,1.0,1.0);
        addComponent(getUnitOptions2(), 6, 0, 1,1, 1.0,1.0);
        addComponent(getBottomSpaceAboveNumbers(), 7, 0, 1,1, 1.0,1.0);
        // numbers are added on top of a single panel
        addComponent(getNumbersPanel(), 8, 0, 1, 1, 1.0, 1.0);
        LOGGER.info("Finished addStartupComponentsToJPanelConverter_v4");
    }

    private void addComponent(Component c, int row, int column, int width, int height, double weighty, double weightx)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = weighty;
        constraints.weightx = weightx;
        constraints.insets = new Insets(0, 0, 0, 0);
        getConverterLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
    }

    private void addComponentToNumbersPanel(Component c, int row, int column, int width, int height, double weighty, double weightx)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = weighty;
        constraints.weightx = weightx;
        constraints.insets = new Insets(0, 0, 0, 0);
        GridBagLayout layout = (GridBagLayout) getNumbersPanel().getLayout();
        layout.setConstraints(c, getConstraints()); // set constraints
        getNumbersPanel().add(c);
    }

    private void setupAngleConverter()
    {
        setupConverter(ConverterType_v4.ANGLE.getName());
        setUnitOptions1(new JComboBox<>(new String[]{"Square millimeters", "Square centimeters", "ADD OTHERS"}));
        setUnitOptions2(new JComboBox<>(new String[]{"Square millimeters", "Square centimeters", "ADD OTHERS"}));
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
    }

    private void setupAreaConverter()
    {
        setupConverter(ConverterType_v4.AREA.getName());
        setUnitOptions1(new JComboBox<>(new String[]{"Square millimeters", "Square centimeters", "ADD OTHERS"}));
        setUnitOptions2(new JComboBox<>(new String[]{"Square millimeters", "Square centimeters", "ADD OTHERS"}));
        setBottomSpaceAboveNumbers(new JTextArea(1,10));
    }

    private void setupConverter(String nameOfConverter)
    {
        LOGGER.info("Starting " + nameOfConverter + " Converter");
        setConverterTypeName(new JLabel(nameOfConverter));
        getConverterTypeName().setFont(font2);

        setOption1(new JTextField());
        getOption1().setText("0");
        getOption1().setFont(font2);

        setOption2(new JTextField());
        getOption2().setText("0");
        getOption2().setFont(font2);

        setButtonBlank(new JButton());
        getButtonBlank().setPreferredSize(new Dimension(35, 35));

        setNumbersPanel(new JPanel());
        getNumbersPanel().setLayout(new GridBagLayout());
        addComponentToNumbersPanel(getCalculator().getButtonClear(), 0, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonClearEntry(), 0, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonDelete(), 0, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton7(), 1, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton8(), 1, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton9(), 1, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton4(), 2, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton5(), 2, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton6(), 2, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton1(), 3, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton2(), 3, 1, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButton3(), 3, 2, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getButtonBlank(), 4, 0, 1, 1, 1.0, 1.0);
        addComponentToNumbersPanel(getCalculator().getButtonDot(), 4, 1, 1, 1, 1.0, 1.0);
        getCalculator().getButton0().setPreferredSize(new Dimension(35, 35));
        addComponentToNumbersPanel(getCalculator().getButton0(), 4, 2, 1, 1, 1.0, 1.0);

    }

    /************* All Getters ******************/
    public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JLabel getConverterTypeName() { return converterTypeName; }
    public JTextField getOption1() { return option1; }
    public JTextField getOption2() { return option2; }
    public JComboBox<String> getUnitOptions1() { return unitOptions1; }
    public JComboBox<String> getUnitOptions2() { return unitOptions2; }
    public JTextArea getBottomSpaceAboveNumbers() { return bottomSpaceAboveNumbers; }
    public static Logger getLOGGER()
    {
        return LOGGER;
    }
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }
    public Calculator_v4 getCalculator() { return calculator; }
    public ConverterType_v4 getConverterType() { return converterType; }
    public JPanel getNumbersPanel() { return numbersPanel; }
    public JButton getButtonBlank() { return buttonBlank; }

    /************* All Setters ******************/
    public void setConverterLayout(GridBagLayout converterLayout) { this.converterLayout = converterLayout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setConverterTypeName(JLabel converterTypeName) { this.converterTypeName = converterTypeName; }
    public void setOption1(JTextField option1) { this.option1 = option1; }
    public void setOption2(JTextField option2) {
        this.option2 = option2;;
    }
    public void setUnitOptions1(JComboBox<String> unitOptions1) { this.unitOptions1 = unitOptions1; }
    public void setUnitOptions2(JComboBox<String> unitOptions2) { this.unitOptions2 = unitOptions2; }
    public void setBottomSpaceAboveNumbers(JTextArea bottomSpaceAboveNumbers) { this.bottomSpaceAboveNumbers = bottomSpaceAboveNumbers; }
    public void setCalculator(StandardCalculator_v4 calculator) { this.calculator = calculator; }
    public void setConverterType(ConverterType_v4 converterType) { this.converterType = converterType; }
    public void setNumbersPanel(JPanel numbersPanel) { this.numbersPanel = numbersPanel; }
    public void setButtonBlank(JButton buttonBlank) { this.buttonBlank = buttonBlank; }

}
