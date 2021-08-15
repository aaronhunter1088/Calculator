package version4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

import static version4.Calculator_v4.font2;

public class JConverterPanel_v4 extends JPanel
{
    protected final static Logger LOGGER;
    private static final long serialVersionUID = 1L;
    static
    {
        LOGGER = LogManager.getLogger(JConverterPanel_v4.class);
    }

    private GridBagLayout converterLayout; // layout of converter
    private GridBagConstraints constraints; // layouts constraints
    private JLabel converterTypeName = new JLabel();
    private JTextField option1 = new JTextField();
    private JTextField option2 = new JTextField();
    private JComboBox<String> unitOptions = new JComboBox<>(new String[]{});
    private JTextArea bottomSpaceAboveNumbers = new JTextArea();

    /************* Constructor ******************/
    public JConverterPanel_v4(ConverterType_v4 converterType) throws ParseException, CalculatorError_v4
    {
        setMinimumSize(new Dimension(100, 400));
        setConverterLayout(new GridBagLayout());
        setLayout(getConverterLayout());
        setConstraints(new GridBagConstraints());
        setupJConverterPanel_v4(converterType);
        addComponentsToJConverterPanel_v4();
    }

    /************* All Other Methods ******************/
    private void setupJConverterPanel_v4(ConverterType_v4 converterType) throws CalculatorError_v4
    {
        LOGGER.info("Starting setupJConverterPanel_v4");
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
        LOGGER.info("Finished setupJConverterPanel_v4");
    }

    public void addComponentsToJConverterPanel_v4()
    {
        LOGGER.info("Starting addComponentsToJConverterPanel_v4");
        addComponent(getConverterTypeName(), 0, 0, 1, 1, 0, 1.0);
        addComponent(getOption1(), 1, 0, 2, 2, 0, 1.0);

        LOGGER.info("Finished addComponentsToJConverterPanel_v4");
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
    }

    private void setupConverter(String nameOfConverter)
    {
        LOGGER.info("Starting " + nameOfConverter + " Converter");
        getConverterTypeName().setText(nameOfConverter);
        getConverterTypeName().setFont(font2);
        getOption1().setText("0");
        getOption2().setText("0");
    }

    /************* All Getters ******************/
    public GridBagLayout getConverterLayout() { return converterLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JLabel getConverterTypeName() { return converterTypeName; }
    public JTextField getOption1() { return option1; }
    public JTextField getOption2() { return option2; }
    public JComboBox<String> getUnitOptions() { return unitOptions; }
    public JTextArea getBottomSpaceAboveNumbers() { return bottomSpaceAboveNumbers; }

    /************* All Setters ******************/
    public void setConverterLayout(GridBagLayout converterLayout) { this.converterLayout = converterLayout; }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setConverterTypeName(JLabel converterTypeName) { this.converterTypeName = converterTypeName; }
    public void setOption1(JTextField option1) { this.option1 = option1; }
    public void setOption2(JTextField option2) {
        this.option2 = option2;;
    }
    public void setUnitOptions(JComboBox<String> unitOptions) { this.unitOptions = unitOptions; }
    public void setBottomSpaceAboveNumbers(JTextArea bottomSpaceAboveNumbers) { this.bottomSpaceAboveNumbers = bottomSpaceAboveNumbers; }
}
