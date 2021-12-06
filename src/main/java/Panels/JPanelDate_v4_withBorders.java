package Panels;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Calculators.Calculator_v4;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static Calculators.Calculator_v4.font2;

/**
 * Tester class. Not in use
 */
public class JPanelDate_v4_withBorders extends JPanel
{
    protected final static Logger LOGGER;
    static
    {
        System.setProperty("appName", "JPanelDate_v4");
        LOGGER = LogManager.getLogger(JPanelBasic_v4.class);
    }

    private static final long serialVersionUID = 1L;

    private GridBagLayout dateLayout; // layout of the calculator
    private GridBagConstraints constraints; // layout's constraints
    private UtilCalendarModel fromModel, toModel;
    private JDatePanelImpl fromDatePanel, toDatePanel;
    private JDatePickerImpl fromDatePicker, toDatePicker;
    private Calculator_v4 calculator;
    private JComboBox optionsBox;
    private JLabel fromDateLabel, toDateLabel, differenceLabel, dateLabel,
                   yearsLabel, monthLabel, daysLabel, resultsLabel;
    private JLabel blankLabel1, blankLabel2, blankLabel3, blankLabel4, blankLabel5;
    private ButtonGroup buttonGroup;
    private JPanel buttonGroupPanel, labelsGroupPanel, textFieldsGroupPanel;
    private JRadioButton addRadioButton, subtractRadioButton;
    private Calendar calendar;
    private JTextField yearsDiffTextField, monthsDiffTextField, daysDiffTextField,
                       yearsTextField, monthsTextField, daysTextField;

    private String defaultOptionFromOptionsBox = "Difference between dates";
    private final String DIFFERENCE = "Difference";
    private final String OPTIONS1 = DIFFERENCE + " between dates";
    private final String OPTIONS2 = "Add or subtract days";
    private final String FROM_DATE = "From Date";
    private final String TO_DATE = "To Date";
    private final String SPACE = " ";
    private final String SAME = "Same";
    private final String YEAR = "Year";
    private final String MONTH = "Month";
    private final String DATE = "Date";
    private final String DAY = "Day";
    private final String LOWER_CASE_S = "s";
    private final String SAME_YEAR = SAME + SPACE + YEAR;
    private final String SAME_MONTH = SAME + SPACE + MONTH;
    private final String SAME_DATE = SAME + SPACE + DATE;
    private final String ADD = "Add";
    private final String SUBTRACT = "Subtract";
    private final String SELECTED = "Selected";
    private final String EMPTY_STRING = "";

    /************* Constructor ******************/
    public JPanelDate_v4_withBorders(Calculator_v4 calculator) throws ParseException
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
    private void setupJPanelDate_v3(Calculator_v4 calculator) throws ParseException
    {
        LOGGER.info("Starting setupPanel_v3");
        setCalculator(calculator);
        setCalendar(Calendar.getInstance());

        setOptionsBox(new JComboBox(new String[]{OPTIONS1, OPTIONS2}));
        getOptionsBox().setBorder(new LineBorder(Color.BLACK));
        getOptionsBox().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        setFromDateLabel(new JLabel(FROM_DATE));
        getFromDateLabel().setFont(font2);
        getFromDateLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getFromDateLabel().setBorder(new LineBorder(Color.BLACK));

        setFromModel(new UtilCalendarModel());
        int year = getCalendar().get(Calendar.YEAR);
        int monthInt = getCalendar().get(Calendar.MONTH);
        int day = getCalendar().get(Calendar.DATE);
        getFromModel().setDate(year, monthInt, day);
        getFromModel().setSelected(true);
        setFromDatePanel(new JDatePanelImpl(getFromModel()));
        setFromDatePicker(new JDatePickerImpl(getFromDatePanel()));
        getFromDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getFromDatePicker().setBorder(new LineBorder(Color.BLACK));

        setToDateLabel(new JLabel(TO_DATE));
        getToDateLabel().setFont(font2);
        getToDateLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getToDateLabel().setBorder(new LineBorder(Color.BLACK));

        setToModel(new UtilCalendarModel());
        getToModel().setDate(year, monthInt, day); // defaults to today
        getToModel().setSelected(true);
        setToDatePanel(new JDatePanelImpl(getToModel()));
        setToDatePicker(new JDatePickerImpl(getToDatePanel()));
        getToDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getToDatePicker().setBorder(new LineBorder(Color.BLACK));

        setToDatePicker(new JDatePickerImpl(getToDatePanel()));
        getToDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getToDatePicker().setBorder(new LineBorder(Color.BLACK));

        getFromDatePicker().addActionListener(action -> {
            if (getOptionsBox().getSelectedItem().toString().equals("Difference between dates"))
            {
                updateResultsTextBox();
            }
            else if (getOptionsBox().getSelectedItem().toString().equals("Add or subtract days"))
            {
                updateResultsLabel();
            }
        });
        getToDatePicker().addActionListener(action -> {
            updateResultsTextBox();
        });

        setDifferenceLabel(new JLabel(DIFFERENCE));
        getDifferenceLabel().setFont(font2);
        getDifferenceLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getDifferenceLabel().setBorder(new LineBorder(Color.BLACK));

        setYearsDiffTextField(new JTextField(SAME_YEAR));
        getYearsDiffTextField().setBorder(new LineBorder(Color.BLACK));
        setMonthsDiffTextField(new JTextField(SAME_MONTH));
        getMonthsDiffTextField().setBorder(new LineBorder(Color.BLACK));
        setDaysDiffTextField(new JTextField(SAME_DATE));
        getDaysDiffTextField().setBorder(new LineBorder(Color.BLACK));

        setBlankLabel1(new JLabel(SPACE));
        getBlankLabel1().setHorizontalAlignment(SwingConstants.LEFT);
        getBlankLabel1();

        setBlankLabel2(new JLabel(SPACE));
        getBlankLabel2().setHorizontalAlignment(SwingConstants.LEFT);
        getBlankLabel2();

        setBlankLabel3(new JLabel(SPACE));
        getBlankLabel3().setHorizontalAlignment(SwingConstants.LEFT);
        getBlankLabel3();

        setBlankLabel4(new JLabel(SPACE));
        getBlankLabel4().setHorizontalAlignment(SwingConstants.LEFT);
        getBlankLabel4();

        setBlankLabel5(new JLabel(SPACE));
        getBlankLabel5().setHorizontalAlignment(SwingConstants.LEFT);
        getBlankLabel5();

        setAddRadioButton(new JRadioButton(ADD));
        getAddRadioButton().setSelected(true);
        getAddRadioButton().addActionListener(action -> {
            getAddRadioButton().setSelected(true);
            getSubtractRadioButton().setSelected(false);
            if (addRadioButton.isSelected())
            {
                int addYears = Integer.parseInt(StringUtils.isBlank(getYearsTextField().getText()) == true ? "0" : getYearsTextField().getText());
                getLOGGER().debug("Year is {}", getCalendar().get(Calendar.YEAR));
                getCalendar().add(Calendar.YEAR, addYears);
                getLOGGER().debug("Add: Year is now {}", getCalendar().get(Calendar.YEAR));
                int addMonths = Integer.parseInt(StringUtils.isBlank(getMonthsTextField().getText()) == true ? "0" : getMonthsTextField().getText());
                getLOGGER().debug("Month is {}", getCalendar().get(Calendar.MONTH));
                getCalendar().add(Calendar.MONTH, addMonths);
                getLOGGER().debug("Add: Month is now {}", getCalendar().get(Calendar.MONTH));
                int addDays = Integer.parseInt(StringUtils.isBlank(getDaysTextField().getText()) == true ? "0" : getDaysTextField().getText());
                getLOGGER().debug("Date is {}", getCalendar().get(Calendar.DATE));
                getCalendar().add(Calendar.DAY_OF_MONTH, addDays);
                getLOGGER().debug("Add: Date is now {}", getCalendar().get(Calendar.DATE));
                setCalendar(getCalendar());
                getLOGGER().debug("Date is now {}", getCalendar().getTime());
                updateResultsLabel();
            }
        });

        setSubtractRadioButton(new JRadioButton(SUBTRACT));
        getSubtractRadioButton().setSelected(false);
        getSubtractRadioButton().addActionListener(action -> {
            getSubtractRadioButton().setSelected(true);
            getAddRadioButton().setSelected(false);
            if (subtractRadioButton.isSelected())
            {
                int addYears = Integer.parseInt(StringUtils.isBlank(getYearsTextField().getText()) == true ? "0" : getYearsTextField().getText());
                getLOGGER().debug("Year is {}", getCalendar().get(Calendar.YEAR));
                getCalendar().add(Calendar.YEAR, -addYears);
                getLOGGER().debug("Sub: Year is now {}", getCalendar().get(Calendar.YEAR));
                int addMonths = Integer.parseInt(StringUtils.isBlank(getMonthsTextField().getText()) == true ? "0" : getMonthsTextField().getText());
                getLOGGER().debug("Month is {}", getCalendar().get(Calendar.MONTH));
                getCalendar().add(Calendar.MONTH, -addMonths);
                getLOGGER().debug("Sub: Month is now {}", getCalendar().get(Calendar.MONTH));
                int addDays = Integer.parseInt(StringUtils.isBlank(getDaysTextField().getText()) == true ? "0" : getDaysTextField().getText());
                getLOGGER().debug("Date is {}", getCalendar().get(Calendar.DATE));
                getCalendar().add(Calendar.DAY_OF_MONTH, -addDays);
                getLOGGER().debug("Sub: Date is now {}", getCalendar().get(Calendar.DATE));
                setCalendar(getCalendar());
                getLOGGER().debug("Date is now {}", getCalendar().getTime());
                updateResultsLabel();
            }
        });

        setButtonGroup(new ButtonGroup());
        setButtonGroupPanel(new JPanel());
        getButtonGroupPanel().setLayout(new GridLayout(1,2));
        getButtonGroupPanel().add(getAddRadioButton());
        getButtonGroupPanel().add(getSubtractRadioButton());
        getButtonGroupPanel().setBorder(new LineBorder(Color.BLACK));

        setYearsLabel(new JLabel(YEAR + LOWER_CASE_S));
        getYearsLabel().setFont(font2);
        getYearsLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setMonthLabel(new JLabel(MONTH + LOWER_CASE_S));
        getMonthsLabel().setFont(font2);
        getMonthsLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setDaysLabel(new JLabel(DAY + LOWER_CASE_S));
        getDaysLabel().setFont(font2);
        getDaysLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setYearsTextField(new JTextField(EMPTY_STRING, 5));
        getYearsTextField().setEditable(true);
        getYearsTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setMonthsTextField(new JTextField(EMPTY_STRING, 5));
        getMonthsTextField().setEditable(true);
        getMonthsTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setDaysTextField(new JTextField(EMPTY_STRING, 5));
        getDaysTextField().setEditable(true);
        getDaysTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setTextFieldsGroupPanel(new JPanel());
        getTextFieldsGroupPanel().setLayout(new GridLayout(1, 3, 60, 5));
        getTextFieldsGroupPanel().add(getYearsTextField());
        getTextFieldsGroupPanel().add(getMonthsTextField());
        getTextFieldsGroupPanel().add(getDaysTextField());
        getTextFieldsGroupPanel().setBorder(new LineBorder(Color.BLACK));

        setLabelsGroupPanel(new JPanel());
        getLabelsGroupPanel().setLayout(new GridLayout(1,3, 50, 5));
        getLabelsGroupPanel().add(getYearsLabel());
        getLabelsGroupPanel().add(getMonthsLabel());
        getLabelsGroupPanel().add(getDaysLabel());
        getLabelsGroupPanel().setBorder(new LineBorder(Color.BLACK));

        setDateLabel(new JLabel(DATE));
        getDateLabel().setFont(font2);
        getDateLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getDateLabel().setBorder(new LineBorder(Color.BLACK));

        String dayOfWeek = getCalendar().getTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        String month = getCalendar().getTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        int date = getFromDatePicker().getModel().getDay();
        setResultsLabel(new JLabel(dayOfWeek + ", " + month  + " " + date + ", " + year));
        getResultsLabel().setFont(font2);
        getResultsLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getResultsLabel().setBorder(new LineBorder(Color.BLACK));
        LOGGER.info("Finished setupPanel_v3");
    }

    private void addStartupComponentsToJPanelDate_v3()
    {
        LOGGER.info("Starting addComponentsToJPanelDate_v3");
        // all of the following components are added no matter the option selected
        addComponent(getOptionsBox(), 0,0,1,1, 0,1.0);
        addComponent(getBlankLabel1(), 1, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(getFromDateLabel(), 2, 0, 1,1, 0,1.0);
        addComponent(getFromDatePicker(), 3,0,1,1, 0,1.0);
        addComponent(getBlankLabel2(), 4, 0, 1, 1,0,1.0); // representing a blank space
        // these components are specific for date differences, but we start with these added first
        addComponent(getToDateLabel(), 5, 0, 1,1, 0,1.0);
        addComponent(getToDatePicker(), 6,0,1,1, 0,1.0);
        addComponent(getBlankLabel3(), 7, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(getDifferenceLabel(), 8, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(getYearsDiffTextField(), 9, 0, 1, 1, 0,1.0);
        addComponent(getMonthsDiffTextField(), 10, 0, 1, 1, 0,1.0);
        addComponent(getDaysDiffTextField(), 11, 0, 1, 1, 0, 1.0);
        addComponent(getBlankLabel4(), 12, 0, 1, 1,1.0,1.0); // representing a blank space
        LOGGER.info("Finished addComponentsToJPanelDate_v3");
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
        getDateLayout().setConstraints(c, getConstraints()); // set constraints
        add(c); // add component
    }

    private void showOptionalComponentsBasedOnComboBoxSelected()
    {
        getOptionsBox().addActionListener(action -> {
            String chosenOption = getOptionsBox().getSelectedItem().toString();
            getLOGGER().debug("chosenOption: {} and defaultOption: {}", chosenOption, defaultOptionFromOptionsBox);
            if (!defaultOptionFromOptionsBox.equals(chosenOption)
                    && chosenOption.equals(OPTIONS1))
            {
                getLOGGER().debug("Difference between dates selected");
                defaultOptionFromOptionsBox = OPTIONS1 + SPACE + SELECTED;
                // remove appropriate components first
                remove(getButtonGroupPanel());
                remove(getBlankLabel3());
                remove(getLabelsGroupPanel());
                remove(getTextFieldsGroupPanel());
                remove(getBlankLabel4());
                remove(getDateLabel());
                remove(getResultsLabel());
                remove(getBlankLabel5());
                // add appropriate components
                addComponent(getToDateLabel(), 5, 0, 1,1, 0,1.0);
                addComponent(getToDatePicker(), 6,0,1,1, 0,1.0);
                addComponent(getBlankLabel3(), 7, 0, 1, 1,0,1.0); // representing a blank space
                addComponent(getDifferenceLabel(), 8, 0, 1, 1,0,1.0); // representing a blank space
                addComponent(getYearsDiffTextField(), 9, 0, 1, 1, 0,1.0);
                addComponent(getMonthsDiffTextField(), 10, 0, 1, 1, 0,1.0);
                addComponent(getDaysDiffTextField(), 11, 0, 1, 1, 0,1.0);
                addComponent(getBlankLabel4(), 12, 0, 1, 1,1.0,1.0); // representing a blank space
                updateResultsTextBox();
                repaint();
                revalidate();
                this.calculator.pack();
            }
            else if (!defaultOptionFromOptionsBox.equals(chosenOption)
                    && chosenOption.equals(OPTIONS2))
            {
                getLOGGER().debug("Add or subtract days selected");
                defaultOptionFromOptionsBox = OPTIONS2 + SPACE + SELECTED;
                // remove appropriate components first
                remove(getToDateLabel());
                remove(getToDatePicker());
                remove(getBlankLabel3());
                remove(getDifferenceLabel());
                remove(getYearsDiffTextField());
                remove(getMonthsDiffTextField());
                remove(getDaysDiffTextField());
                remove(getBlankLabel4());
                // add appropriate components
                addComponent(getButtonGroupPanel(), 5, 0, 1,1, 0,1.0);
                addComponent(getBlankLabel3(), 6, 0, 1, 1,0, 1.0); // representing a blank space
                addComponent(getLabelsGroupPanel(), 7, 0, 1, 1,0,1.0);
                addComponent(getTextFieldsGroupPanel(), 8, 0, 1, 1, 0, 1.0);
                addComponent(getBlankLabel4(), 9, 0, 1, 1, 0.0, 1.0);
                addComponent(getDateLabel(), 10, 0, 1, 1, 0, 1.0);
                addComponent(getResultsLabel(), 11, 0, 1, 1, 0, 1.0);
                addComponent(getBlankLabel5(), 12, 0, 1, 1, 1.0, 1.0);
                repaint();
                revalidate();
                this.calculator.pack();
            }
        });
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

    private void updateResultsLabel()
    {
        int year = getFromDatePicker().getModel().getYear();
        if (addRadioButton.isSelected())
            year += Integer.parseInt(StringUtils.isBlank(getYearsTextField().getText()) == true ? "0" : getYearsTextField().getText());
        else
            year -= Integer.parseInt(StringUtils.isBlank(getYearsTextField().getText()) == true ? "0" : getYearsTextField().getText());
        int monthInt = getFromDatePicker().getModel().getMonth();
        if (addRadioButton.isSelected())
            monthInt += Integer.parseInt(StringUtils.isBlank(getMonthsTextField().getText()) == true ? "0" : getMonthsTextField().getText());
        else
            monthInt -= Integer.parseInt(StringUtils.isBlank(getMonthsTextField().getText()) == true ? "0" : getMonthsTextField().getText());
        int date = getFromDatePicker().getModel().getDay();
        if (addRadioButton.isSelected())
            date += Integer.parseInt(StringUtils.isBlank(getDaysTextField().getText()) == true ? "0" : getDaysTextField().getText());
        else
            date -= Integer.parseInt(StringUtils.isBlank(getDaysTextField().getText()) == true ? "0" : getDaysTextField().getText());
        getCalendar().set(year, monthInt, date);
        getCalendar().setTime(getCalendar().getTime());
        setCalendar(getCalendar());
        getLOGGER().debug("Date is now {}", getCalendar().getTime());
        getFromDatePicker().getModel().setDate(year,monthInt, date);
        String dayOfWeek = getCalendar().getTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        String month = getCalendar().getTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        date = getFromDatePicker().getModel().getDay();
        year = getFromDatePicker().getModel().getYear();
        getResultsLabel().setText(dayOfWeek + ", " + month + " " + date + ", " + year);
    }

    private void updateResultsTextBox()
    {
        long[] numberOfYearsMonthsAndDays = calculateDifferenceBetweenDates();
        String wordYear = YEAR.toLowerCase() + LOWER_CASE_S;
        String wordMonth = MONTH.toLowerCase() + LOWER_CASE_S;
        String wordDay = DAY.toLowerCase() + LOWER_CASE_S;
        if (numberOfYearsMonthsAndDays[0] == 1 || numberOfYearsMonthsAndDays[0] == -1)
        {
            wordYear = YEAR.toLowerCase();
        }
        if (numberOfYearsMonthsAndDays[1] == 1 || numberOfYearsMonthsAndDays[1] == -1)
        {
            wordMonth = MONTH.toLowerCase();
        }
        if (numberOfYearsMonthsAndDays[2] == 1 || numberOfYearsMonthsAndDays[2] == -1)
        {
            wordDay = DAY.toLowerCase();
        }

        if((numberOfYearsMonthsAndDays[0] != 0 ||
            numberOfYearsMonthsAndDays[1] != 0 ||
            numberOfYearsMonthsAndDays[2] != 0)
                && ((Calendar) getFromDatePicker().getModel().getValue()).getTime()
                .before(((Calendar) getToDatePicker().getModel().getValue()).getTime()))
        {
            getYearsDiffTextField().setText(numberOfYearsMonthsAndDays[0] + SPACE + wordYear);
            getMonthsDiffTextField().setText(numberOfYearsMonthsAndDays[1] + SPACE + wordMonth);
            getDaysDiffTextField().setText(numberOfYearsMonthsAndDays[2] + SPACE + wordDay);
        }
        else if((numberOfYearsMonthsAndDays[0] != 0 ||
                numberOfYearsMonthsAndDays[1] != 0 ||
                numberOfYearsMonthsAndDays[2] != 0)
                && ((Calendar) getFromDatePicker().getModel().getValue()).getTime()
                .after(((Calendar) getToDatePicker().getModel().getValue()).getTime()))
        {
            getYearsDiffTextField().setText((-1*numberOfYearsMonthsAndDays[0])+ wordYear);
            getMonthsDiffTextField().setText((-1*numberOfYearsMonthsAndDays[1]) + wordMonth);
            getDaysDiffTextField().setText((-1*numberOfYearsMonthsAndDays[2]) + wordDay);
        }
        else
        {
            getYearsDiffTextField().setText(SAME + SPACE + YEAR);
            getMonthsDiffTextField().setText(SAME + SPACE + MONTH);
            getDaysDiffTextField().setText(SAME + SPACE + DATE);
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
    public Calculator_v4 getCalculator()
    {
        return calculator;
    }
    public JComboBox getOptionsBox()
    {
        return optionsBox;
    }
    public JTextField getYearsDiffTextField()
    {
        return yearsDiffTextField;
    }
    public JTextField getMonthsDiffTextField()
    {
        return monthsDiffTextField;
    }
    public JTextField getDaysDiffTextField()
    {
        return daysDiffTextField;
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
    public JLabel getBlankLabel1()
    {
        return blankLabel1;
    }
    public JLabel getBlankLabel2()
    {
        return blankLabel2;
    }
    public JLabel getBlankLabel3()
    {
        return blankLabel3;
    }
    public JLabel getBlankLabel4()
    {
        return blankLabel4;
    }
    public JLabel getBlankLabel5()
    {
        return blankLabel5;
    }
    public ButtonGroup getButtonGroup()
    {
        return buttonGroup;
    }
    public JRadioButton getAddRadioButton()
    {
        return addRadioButton;
    }
    public JRadioButton getSubtractRadioButton()
    {
        return subtractRadioButton;
    }
    public JPanel getButtonGroupPanel()
    {
        return buttonGroupPanel;
    }
    public JLabel getDateLabel()
    {
        return dateLabel;
    }
    public JPanel getLabelsGroupPanel()
    {
        return labelsGroupPanel;
    }
    public JLabel getYearsLabel()
    {
        return yearsLabel;
    }
    public JLabel getMonthsLabel()
    {
        return monthLabel;
    }
    public JLabel getDaysLabel()
    {
        return daysLabel;
    }
    public JPanel getTextFieldsGroupPanel()
    {
        return textFieldsGroupPanel;
    }
    public JTextField getYearsTextField()
    {
        return yearsTextField;
    }
    public JTextField getMonthsTextField()
    {
        return monthsTextField;
    }
    public JTextField getDaysTextField()
    {
        return daysTextField;
    }
    public JLabel getResultsLabel()
    {
        return resultsLabel;
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
    private void setCalculator(Calculator_v4 calculator)
    {
        this.calculator = calculator;
    }
    private void setOptionsBox(JComboBox optionsBox)
    {
        this.optionsBox = optionsBox;
    }
    private void setYearsDiffTextField(JTextField yearResultTextField)
    {
        this.yearsDiffTextField = yearResultTextField;
    }
    private void setMonthsDiffTextField(JTextField monthsDiffTextField)
    {
        this.monthsDiffTextField = monthsDiffTextField;
    }
    private void setDaysDiffTextField(JTextField daysDiffTextField)
    {
        this.daysDiffTextField = daysDiffTextField;
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
    private void setBlankLabel1(JLabel blankLabel1)
    {
        this.blankLabel1 = blankLabel1;
    }
    private void setBlankLabel2(JLabel blankLabel2)
    {
        this.blankLabel2 = blankLabel2;
    }
    private void setBlankLabel3(JLabel blankLabel3)
    {
        this.blankLabel3 = blankLabel3;
    }
    private void setBlankLabel4(JLabel blankLabel4)
    {
        this.blankLabel4 = blankLabel4;
    }
    private void setBlankLabel5(JLabel blankLabel5)
    {
        this.blankLabel5 = blankLabel5;
    }
    private void setButtonGroup(ButtonGroup buttonGroup)
    {
        this.buttonGroup = buttonGroup;
    }
    private void setAddRadioButton(JRadioButton addRadioButton)
    {
        this.addRadioButton = addRadioButton;
    }
    private void setSubtractRadioButton(JRadioButton subtractRadioButton)
    {
        this.subtractRadioButton = subtractRadioButton;
    }
    private void setButtonGroupPanel(JPanel buttonGroupPanel)
    {
        this.buttonGroupPanel = buttonGroupPanel;
    }
    private void setDateLabel(JLabel dateLabel)
    {
        this.dateLabel = dateLabel;
    }
    private void setLabelsGroupPanel(JPanel labelsGroupPanel)
    {
        this.labelsGroupPanel = labelsGroupPanel;
    }
    private void setYearsLabel(JLabel yearsLabel)
    {
        this.yearsLabel = yearsLabel;
    }
    private void setMonthLabel(JLabel monthLabel)
    {
        this.monthLabel = monthLabel;
    }
    private void setDaysLabel(JLabel daysLabel)
    {
        this.daysLabel = daysLabel;
    }
    private void setTextFieldsGroupPanel(JPanel textFieldsGroupPanel)
    {
        this.textFieldsGroupPanel = textFieldsGroupPanel;
    }
    private void setYearsTextField(JTextField yearsTextField)
    {
        this.yearsTextField = yearsTextField;
    }
    private void setMonthsTextField(JTextField monthsTextField)
    {
        this.monthsTextField = monthsTextField;
    }
    private void setDaysTextField(JTextField daysTextField)
    {
        this.daysTextField = daysTextField;
    }
    private void setResultsLabel(JLabel resultsLabel)
    {
        this.resultsLabel = resultsLabel;
    }
}
