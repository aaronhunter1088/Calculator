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
        // all of the following components are added no matter the option selected
        addComponent(getConverterTypeName(), 0,0,1,1, 1.0,1.0);
        addComponent(getOption1(), 1, 0, 2, 2,1.0,1.0);
        addComponent(getUnitOptions1(), 3, 0, 1,1, 1.0,1.0);
        addComponent(getOption2(), 4, 0, 2, 2,1.0,1.0);
        addComponent(getUnitOptions2(), 6, 0, 1,1, 1.0,1.0);
        addComponent(getBottomSpaceAboveNumbers(), 7, 0, 1,1, 1.0,1.0);
        // numbers
        addComponent(getCalculator().getButtonClear(), 8, 0, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButtonClearEntry(), 8, 1, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButtonDelete(), 8, 2, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton7(), 9, 0, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton8(), 9, 1, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton9(), 9, 2, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton4(), 10, 0, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton5(), 10, 1, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton6(), 10, 2, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton1(), 11, 0, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton2(), 11, 1, 1, 1, 1.0, 1.0);
        addComponent(getCalculator().getButton3(), 11, 2, 1, 1, 1.0, 1.0);
        LOGGER.info("Finished addStartupComponentsToJPanelConverter_v4");
    }

    private void addComponent(Component c, int row, int column, int width, int height, double weighty, double weightx)
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
        getConverterLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
    }

    private void setupAngleConverter()
    {
        setupConverter(ConverterType_v4.ANGLE.getName());
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

}
