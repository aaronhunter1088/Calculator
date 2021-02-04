package version3;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static version3.Calculator_v3.font2;

public class JPanelDate_v3 extends JPanel
{
    protected final static Logger LOGGER;
    static
    {
        System.setProperty("appName", "JPanelDate_v3");
        LOGGER = LogManager.getLogger(JPanelBasic_v3.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout dateLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    private UtilCalendarModel fromModel, toModel;
    private JDatePanelImpl fromDatePanel, toDatePanel;
    private JDatePickerImpl fromDatePicker, toDatePicker;
    private Calculator_v3 calculator;
    private JComboBox optionsBox;
    private JLabel fromDateLabel, toDateLabel, differenceLabel;
    private Calendar calendar;

    private JTextField yearResultTextField, monthResultTextField, dayResultTextField;

    public JPanelDate_v3(StandardCalculator_v3 calculator) throws ParseException
    {
        setMinimumSize(new Dimension(100,400));
        setDateLayout(new GridBagLayout());
        setLayout(getDateLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setupJPanelDate_v3(calculator);
        addStartupComponentsToJPanelDate_v3();
        showOptionalComponentsBasedOnComboBoxSelected();
    }

    /************* Start of methods here ******************/

    private void setupJPanelDate_v3(StandardCalculator_v3 calculator) throws ParseException {
        LOGGER.info("Starting setupPanel_v3");
        setCalculator(calculator);
        setCalendar(Calendar.getInstance());

        setOptionsBox(new JComboBox(new String[]{"Difference between dates", "Add or subtract days"}));
        getOptionsBox().setBorder(new LineBorder(Color.BLACK));
        getOptionsBox().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        setFromDateLabel(new JLabel("From Date"));
        getFromDateLabel().setFont(font2);
        getFromDateLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getFromDateLabel().setBorder(new LineBorder(Color.BLACK));

        setFromModel(new UtilCalendarModel());
        int year = getCalendar().get(Calendar.YEAR);
        int month = getCalendar().get(Calendar.MONTH);
        int day = getCalendar().get(Calendar.DATE);
        getFromModel().setDate(year, month, day);
        getFromModel().setSelected(true);
        setFromDatePanel(new JDatePanelImpl(getFromModel()));
        setFromDatePicker(new JDatePickerImpl(getFromDatePanel()));
        getFromDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getFromDatePicker().setBorder(new LineBorder(Color.BLACK));

        setToDateLabel(new JLabel("To Date"));
        getToDateLabel().setFont(font2);
        getToDateLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getToDateLabel().setBorder(new LineBorder(Color.BLACK));

        setToModel(new UtilCalendarModel());
        getToModel().setDate(year, month, day); // defaults to today
        getToModel().setSelected(true);
        setToDatePanel(new JDatePanelImpl(getToModel()));
        setToDatePicker(new JDatePickerImpl(getToDatePanel()));
        getToDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getToDatePicker().setBorder(new LineBorder(Color.BLACK));

        setToDatePicker(new JDatePickerImpl(getToDatePanel()));
        getToDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getToDatePicker().setBorder(new LineBorder(Color.BLACK));

        getFromDatePicker().addActionListener(action -> {
            updateResultsTextBox();
        });
        getToDatePicker().addActionListener(action -> {
            updateResultsTextBox();
        });

        setDifferenceLabel(new JLabel("Difference"));
        getDifferenceLabel().setFont(font2);
        getDifferenceLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getDifferenceLabel().setBorder(new LineBorder(Color.BLACK));

        setYearTextField(new JTextField("Same year"));
        getYearResultTextField().setBorder(new LineBorder(Color.BLACK));
        setMonthResultTextField(new JTextField("Same month"));
        getMonthResultTextField().setBorder(new LineBorder(Color.BLACK));
        setDayResultTextField(new JTextField("Same date"));
        getDayResultTextField().setBorder(new LineBorder(Color.BLACK));
        LOGGER.info("Finished setupPanel_v3");
    }

    private void addStartupComponentsToJPanelDate_v3()
    {
        LOGGER.info("Starting addComponentsToJPanelDate_v3");
        // all of the following components are added no matter the option selected
        addComponent(getOptionsBox(), 0,0,1,1, 0);
        addComponent(new JLabel(" "), 1, 0, 1, 1,0); // representing a blank space
        addComponent(getFromDateLabel(), 2, 0, 1,1, 0);
        addComponent(getFromDatePicker(), 3,0,1,1, 0);
        addComponent(new JLabel(" "), 4, 0, 1, 1,0); // representing a blank space
        addComponent(getToDateLabel(), 5, 0, 1,1, 0);
        addComponent(getToDatePicker(), 6,0,1,1, 0);
        addComponent(new JLabel(" "), 7, 0, 1, 1,0); // representing a blank space
        addComponent(getDifferenceLabel(), 8, 0, 1, 1,0); // representing a blank space
        addComponent(getYearResultTextField(), 9, 0, 1, 1, 0);
        addComponent(getMonthResultTextField(), 10, 0, 1, 1, 0);
        addComponent(getDayResultTextField(), 11, 0, 1, 1, 0);

        // this component is for testing purposes
        //addComponent(getTestingTextField(), 5, 0, 1, 1, 0);
        addComponent(new JLabel(" "), 12, 0, 1, 1,1.0); // representing a blank space
        LOGGER.info("Finished addComponentsToJPanelDate_v3");
    }

    private void addComponent(Component c, int row, int column, int width, int height, double weighty)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor =  GridBagConstraints.FIRST_LINE_START;
        constraints.weighty = weighty;
        constraints.weightx = 1.0;
        getDateLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
    }

    private void showOptionalComponentsBasedOnComboBoxSelected()
    {
        getOptionsBox().addActionListener(action -> {
            getYearResultTextField().setText("");
            if (getOptionsBox().getSelectedItem().toString().equals("Difference between dates"))
            {
                //getResultsTextField().setText("Difference between dates");
                // remove appropriate components first
                // add appropriate components
                addComponent(getToDateLabel(), 5, 0, 1,1, 0);
                addComponent(getFromDatePicker(), 6,0,1,1, 1.0);

            }
            else if (getOptionsBox().getSelectedItem().toString().equals("Add or subtract days"))
            {
                getYearResultTextField().setText("Add or subtract days");
                // remove appropriate components first
                // add appropriate components
            }
        });
        getOptionsBox().revalidate();
        getOptionsBox().repaint();
    }

    private long[] calculateDifferenceBetweenDates()
    {
        setCalendar((Calendar) getFromDatePicker().getModel().getValue());
        Date fromDate = getCalendar().getTime();

        setCalendar((Calendar) getToDatePicker().getModel().getValue());
        Date toDate = getCalendar().getTime();

        long[] numberOfYearsMonthsAndDays = {0,0,0}; // year, month, days
        int numberOfDays = (int) Duration.between(fromDate.toInstant(), toDate.toInstant()).toDays();
        LOGGER.debug("Days between is {}", numberOfDays);
        LocalDate localFromDate = fromDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localToDate = toDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Period period = Period.between(localFromDate, localToDate);

        int years = period.getYears();
        LOGGER.debug("numbers of years is {}", years);
        int months = period.getMonths();
        LOGGER.debug("number of months is {}", months);
        numberOfDays = period.getDays();
        LOGGER.debug("remaining number of days is {}\n", numberOfDays);
        numberOfYearsMonthsAndDays[0] = years;
        numberOfYearsMonthsAndDays[1] = months;
        numberOfYearsMonthsAndDays[2] = numberOfDays;
        return numberOfYearsMonthsAndDays;
    }

    private void updateResultsTextBox()
    {
        long[] numberOfYearsMonthsAndDays = calculateDifferenceBetweenDates();
        String wordYear = " years";
        String wordMonth = " months";
        String wordDay = " days";
        if (numberOfYearsMonthsAndDays[0] == 1)
        {
            wordYear = " year";
        }
        if (numberOfYearsMonthsAndDays[1] == 1)
        {
            wordMonth = " month";
        }
        if (numberOfYearsMonthsAndDays[2] == 1)
        {
            wordDay = " day";
        }
        if((numberOfYearsMonthsAndDays[0] != 0 ||
            numberOfYearsMonthsAndDays[1] != 0 ||
            numberOfYearsMonthsAndDays[2] != 0)
                && ((Calendar) getFromDatePicker().getModel().getValue()).getTime()
                .before(((Calendar) getToDatePicker().getModel().getValue()).getTime()))
        {
            getYearResultTextField().setText(numberOfYearsMonthsAndDays[0] + wordYear);
            getMonthResultTextField().setText(numberOfYearsMonthsAndDays[1] + wordMonth);
            getDayResultTextField().setText(numberOfYearsMonthsAndDays[2] + wordDay);
        }
        else
        {
            getYearResultTextField().setText("Same year");
            getMonthResultTextField().setText("Same month");
            getDayResultTextField().setText("Same date");
        }
    }

    /************* All Getters ******************/
    public static Logger getLOGGER()
    {
        return LOGGER;
    }
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }
    public GridBagLayout getDateLayout()
    {
        return dateLayout;
    }
    public GridBagConstraints getConstraints()
    {
        return constraints;
    }
    public UtilCalendarModel getFromModel()
    {
        return fromModel;
    }
    public UtilCalendarModel getToModel()
    {
        return toModel;
    }
    public JDatePanelImpl getFromDatePanel()
    {
        return fromDatePanel;
    }
    public JDatePanelImpl getToDatePanel()
    {
        return toDatePanel;
    }
    public JDatePickerImpl getFromDatePicker()
    {
        return fromDatePicker;
    }
    public JDatePickerImpl getToDatePicker()
    {
        return toDatePicker;
    }
    public Calculator_v3 getCalculator()
    {
        return calculator;
    }
    public JComboBox getOptionsBox()
    {
        return optionsBox;
    }
    public JTextField getYearResultTextField()
    {
        return yearResultTextField;
    }
    public JTextField getMonthResultTextField()
    {
        return monthResultTextField;
    }
    public JTextField getDayResultTextField()
    {
        return dayResultTextField;
    }
    public JLabel getFromDateLabel()
    {
        return fromDateLabel;
    }
    public JLabel getToDateLabel()
    {
        return toDateLabel;
    }
    public JLabel getDifferenceLabel()
    {
        return differenceLabel;
    }
    public Calendar getCalendar()
    {
        return calendar;
    }

    /************* All Setters ******************/
    private void setDateLayout(GridBagLayout dateLayout)
    {
        this.dateLayout = dateLayout;
    }
    private void setConstraints(GridBagConstraints constraints)
    {
        this.constraints = constraints;
    }
    private void setFromModel(UtilCalendarModel fromModel)
    {
        this.fromModel = fromModel;
    }
    private void setToModel(UtilCalendarModel toModel)
    {
        this.toModel = toModel;
    }
    private void setFromDatePanel(JDatePanelImpl fromDatePanel)
    {
        this.fromDatePanel = fromDatePanel;
    }
    private void setToDatePanel(JDatePanelImpl toDatePanel)
    {
        this.toDatePanel = toDatePanel;
    }
    private void setFromDatePicker(JDatePickerImpl fromDatePicker)
    {
        this.fromDatePicker = fromDatePicker;
    }
    private void setToDatePicker(JDatePickerImpl toDatePicker)
    {
        this.toDatePicker = toDatePicker;
    }
    private void setCalculator(StandardCalculator_v3 calculator)
    {
        this.calculator = calculator;
    }
    private void setOptionsBox(JComboBox optionsBox)
    {
        this.optionsBox = optionsBox;
    }
    private void setYearTextField(JTextField yearResultTextField)
    {
        this.yearResultTextField = yearResultTextField;
    }
    private void setMonthResultTextField(JTextField monthResultTextField)
    {
        this.monthResultTextField = monthResultTextField;
    }
    private void setDayResultTextField(JTextField dayResultTextField)
    {
        this.dayResultTextField = dayResultTextField;
    }
    private void setFromDateLabel(JLabel fromDateLabel)
    {
        this.fromDateLabel = fromDateLabel;
    }
    private void setToDateLabel(JLabel toDateLabel)
    {
        this.toDateLabel = toDateLabel;
    }
    private void setDifferenceLabel(JLabel differenceLabel)
    {
        this.differenceLabel = differenceLabel;
    }
    private void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
    }
}
