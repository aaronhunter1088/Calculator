package version4;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;

import static version4.Calculator_v4.*;
import static version4.CalcType_v4.*;

public class JPanelDate_v4 extends JPanel
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
                   yearsLabel, monthLabel, weeksLabel, daysLabel, resultsLabel,
                   yearsDifferenceLabel, monthsDifferenceLabel, weeksDifferenceLabel, daysDifferenceLabel;
    private JLabel blankLabel1, blankLabel2, blankLabel3, blankLabel4, blankLabel5;
    private ButtonGroup buttonGroup;
    private JPanel buttonGroupPanel, textFieldsGroupPanel;
    private JRadioButton addRadioButton, subtractRadioButton;
    private JTextField //TODO: delete this line: yearsDiffTextField, monthsDiffTextField, daysDiffTextField,
                       yearsTextField, monthsTextField, weeksTextField, daysTextField;

    private String defaultOptionFromOptionsBox = "Difference between dates";
    private final String DIFFERENCE = "Difference";
    public static final String OPTIONS1 = "Difference between dates";
    public static final String OPTIONS2 = "Add or subtract days";
    public String selectedOption = OPTIONS1;
    private final String FROM_DATE = "From Date";
    private final String TO_DATE = "To Date";
    private final String SPACE = " ";
    private final String SAME = "Same";
    private final String YEAR = "Year";
    private final String MONTH = "Month";
    private final String WEEK = "Week";
    private final String DATE = "Date";
    private final String DAY = "Day";
    private final String LOWER_CASE_S = "s";
    private final String SAME_YEAR = SAME + SPACE + YEAR;
    private final String SAME_MONTH = SAME + SPACE + MONTH;
    private final String SAME_WEEK = SAME + SPACE + WEEK;
    private final String SAME_DAY = SAME + SPACE + DAY;
    private final String ADD = "Add";
    private final String SUBTRACT = "Subtract";
    private final String SELECTED = "Selected";
    private final String EMPTY_STRING = "";

    /************* Constructor ******************/
    public JPanelDate_v4(Calculator_v4 calculator) throws ParseException
    {
        this(calculator, null);
    }

    public JPanelDate_v4(Calculator_v4 calculator, String chosenOption) throws ParseException
    {
        setCalculator(calculator);
        setMinimumSize(new Dimension(100,400));
        setDateLayout(new GridBagLayout());
        setLayout(getDateLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setupJPanelDate(chosenOption);
        updateThisPanel();
        getLogger().info("Finished constructing Date panel");
    }

    /************* Start of methods here ******************/
    public void performDateCalculatorTypeSwitchOperations()
    {
        getLogger().info("Performing tasks associated to switching to the Date panel");
        //setupEditMenu();
        createViewHelpMenu();
        SwingUtilities.updateComponentTreeUI(this);
        getLogger().info("Finished tasks associated to switching to the Date panel");
    }

    public void createViewHelpMenu()
    {
        getLogger().info("Creating the view help menu for date panel");
        String helpString = "<html>How to use the " + DATE + " Calculator<br><br>" +
                "Difference Between Dates:<br>" +
                "Enter a date into either field, From or To.<br>" +
                "Only 1 date is required to change to show a difference.<br>" +
                "See the difference between the two dates below.<br><br>" +
                "Add or Subtract Days: <br>" +
                "Select a date from the drop down option.<br>" +
                "Enter values in the choices: Years, Months, or Days.<br>" +
                "Click Add or Subtract to execute that action using your values and date.</html>";
        // 4 menu options: loop through to find the Help option
        for(int i=0; i < getCalculator().getBar().getMenuCount(); i++)
        {
            JMenu menuOption = getCalculator().getBar().getMenu(i);
            JMenuItem valueForThisMenuOption = null;
            if (menuOption.getName() != null && menuOption.getName().equals("Help")) {
                // get the options. remove viewHelpItem
                for(int j=0; j<menuOption.getItemCount(); j++) {
                    valueForThisMenuOption = menuOption.getItem(j);
                    if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("View Help"))
                    {
                        break; // do nothing at this moment
                    }
                    else if (valueForThisMenuOption != null && valueForThisMenuOption.getName() != null &&
                            valueForThisMenuOption.getName().equals("About"))
                    {
                        break; // do nothing at this moment
                    }
                }
                // remove old option
                menuOption.remove(valueForThisMenuOption);
                // set up new viewHelpItem option
                JMenuItem viewHelpItem = new JMenuItem("View Help");
                viewHelpItem.setFont(font);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(helpString,
                            getCalculator().getBlankImage(), SwingConstants.CENTER);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                    JPanel mainPanel = new JPanel();
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(getCalculator(),
                            mainPanel, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
        getLogger().info("Finished creating the view help menu");
    }

    public void reset()
    {
        reset(getSelectedOption());
    }

    public void reset(String chosenOption)
    {
        // for either option
        setSelectedOption(chosenOption);
        setupFromDate();
        // if option1 was chosen
        setupToDate();
        getYearsDifferenceLabel().setText("");
        getMonthsDifferenceLabel().setText("");
        getDaysDifferenceLabel().setText("");
        getResultsLabel().setText("");
        // if option2 was chosen
        getAddRadioButton().setSelected(true);
        getYearsTextField().setText("");
        getMonthsTextField().setText("");
        getDaysTextField().setText("");
        getCalculator().confirm(getClass().getName() + " was reset!");
    }

    private void setupJPanelDate(String chosenOption)
    {
        LOGGER.info("Starting to construct " + getClass().getName());
        getLogger().warn("Edit Menu not set up for " + getCalculator().getCalcType());
        //createEditMenu();
        createViewHelpMenu();

        setupOptionsSelection(chosenOption);

        setupFromDate();
        setupToDate();

        setDifferenceLabel(new JLabel(DIFFERENCE));
        getDifferenceLabel().setFont(font2);
        getDifferenceLabel().setHorizontalAlignment(SwingConstants.LEFT);

        // TODO: change textfields to labels
        setYearsDifferenceLabel(new JLabel(SAME_YEAR));
        setMonthsDifferenceLabel(new JLabel(SAME_MONTH));
        setWeeksDifferenceLabel(new JLabel(SAME_WEEK));
        setDaysDifferenceLabel(new JLabel(SAME_DAY));
        //setYearsDiffTextField(new JTextField(SAME_YEAR));
        //setMonthsDiffTextField(new JTextField(SAME_MONTH));
        //setDaysDiffTextField(new JTextField(SAME_DATE));

        setBlankLabel1(new JLabel(SPACE));
        getBlankLabel1().setHorizontalAlignment(SwingConstants.LEFT);
        //getBlankLabel1();

        setBlankLabel2(new JLabel(SPACE));
        getBlankLabel2().setHorizontalAlignment(SwingConstants.LEFT);
        //getBlankLabel2();

        setBlankLabel3(new JLabel(SPACE));
        getBlankLabel3().setHorizontalAlignment(SwingConstants.LEFT);
        //getBlankLabel3();

        setBlankLabel4(new JLabel(SPACE));
        getBlankLabel4().setHorizontalAlignment(SwingConstants.LEFT);
        //getBlankLabel4();

        setBlankLabel5(new JLabel(SPACE));
        getBlankLabel5().setHorizontalAlignment(SwingConstants.LEFT);
        //getBlankLabel5();

        setAddRadioButton(new JRadioButton(ADD));
        getAddRadioButton().setSelected(true);
        getAddRadioButton().setName(ADD);
        getAddRadioButton().addActionListener(this::performAddRadioButtonFunctionality);

        setSubtractRadioButton(new JRadioButton(SUBTRACT));
        getSubtractRadioButton().setSelected(false);
        getSubtractRadioButton().setName(SUBTRACT);
        getSubtractRadioButton().addActionListener(this::performSubtractRadioButtonFunctionality);

        setButtonGroup(new ButtonGroup());
        setButtonGroupPanel(new JPanel());
        getButtonGroupPanel().setLayout(new GridLayout(1,2));
        getButtonGroupPanel().add(getAddRadioButton());
        getButtonGroupPanel().add(getSubtractRadioButton());

        setYearsLabel(new JLabel(YEAR + LOWER_CASE_S));
        getYearsLabel().setFont(font2);
        getYearsLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setMonthLabel(new JLabel(MONTH + LOWER_CASE_S));
        getMonthsLabel().setFont(font2);
        getMonthsLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setWeeksLabel(new JLabel(WEEK + LOWER_CASE_S));
        getWeeksLabel().setFont(font2);
        getWeeksLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setDaysLabel(new JLabel(DAY + LOWER_CASE_S));
        getDaysLabel().setFont(font2);
        getDaysLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setYearsTextField(new JTextField(EMPTY_STRING, 5));
        getYearsTextField().setEditable(true);
        getYearsTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setMonthsTextField(new JTextField(EMPTY_STRING, 5));
        getMonthsTextField().setEditable(true);
        getMonthsTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setWeeksTextField(new JTextField(EMPTY_STRING, 5));
        getWeeksTextField().setEditable(true);
        getWeeksTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setDaysTextField(new JTextField(EMPTY_STRING, 5));
        getDaysTextField().setEditable(true);
        getDaysTextField().setHorizontalAlignment(SwingConstants.LEFT);

        setTextFieldsGroupPanel(new JPanel());
        getTextFieldsGroupPanel().setLayout(new GridLayout(4, 2, 5, 5));
        getTextFieldsGroupPanel().add(getYearsLabel());
        getTextFieldsGroupPanel().add(getYearsTextField());
        getTextFieldsGroupPanel().add(getMonthsLabel());
        getTextFieldsGroupPanel().add(getMonthsTextField());
        getTextFieldsGroupPanel().add(getWeeksLabel());
        getTextFieldsGroupPanel().add(getWeeksTextField());
        getTextFieldsGroupPanel().add(getDaysLabel());
        getTextFieldsGroupPanel().add(getDaysTextField());

        //setLabelsGroupPanel(new JPanel());
        //getLabelsGroupPanel().setLayout(new GridLayout(1,3, 30, 5));
        //getLabelsGroupPanel().add(getYearsLabel());
        //getLabelsGroupPanel().add(getMonthsLabel());
        //getLabelsGroupPanel().add(getDaysLabel());

        setDateLabel(new JLabel(DATE));
        getDateLabel().setFont(font2);
        getDateLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setupResultsLabel();

        if (chosenOption == null || chosenOption.equals(OPTIONS1)) {
            addStartupComponentsToJPanelDate(OPTIONS1);
            setSelectedOption(OPTIONS1);
        }
        else {
            addStartupComponentsToJPanelDate(chosenOption);
            setSelectedOption(OPTIONS2);
        }
        getOptionsBox().repaint();
        LOGGER.info("Finished setting up " + getClass().getName());
    }

    public void setupOptionsSelection(String chosenOption)
    {
        setOptionsBox(new JComboBox(new String[]{OPTIONS1, OPTIONS2}));
        getOptionsBox().setSelectedItem(chosenOption == null ? OPTIONS1 : chosenOption);
        setSelectedOption(chosenOption);
        getOptionsBox().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getOptionsBox().addActionListener(this::performOptionsBoxFunctionality);
    }

    public void setupFromDate()
    {
        setFromDateLabel(new JLabel(FROM_DATE));
        getFromDateLabel().setFont(font2);
        getFromDateLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setFromModel(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        getFromModel().setDate(year, monthInt, day);
        getFromModel().setSelected(true);
        setFromDatePanel(new JDatePanelImpl(getFromModel()));
        setFromDatePicker(new JDatePickerImpl(getFromDatePanel()));
        getFromDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getFromDatePicker().addActionListener(this::performDatePickerFunctionality);
    }

    public void setupToDate()
    {
        setToDateLabel(new JLabel(TO_DATE));
        getToDateLabel().setFont(font2);
        getToDateLabel().setHorizontalAlignment(SwingConstants.LEFT);

        setToModel(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        getToModel().setDate(year, monthInt, day); // defaults to today
        getToModel().setSelected(true);
        setToDatePanel(new JDatePanelImpl(getToModel()));
        setToDatePicker(new JDatePickerImpl(getToDatePanel()));
        getToDatePicker().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        getToDatePicker().addActionListener(this::performDatePickerFunctionality); //action -> updateResultsTextBox());
    }

    public void setupResultsLabel()
    {
        LocalDate todaysDate = LocalDate.now();
        DayOfWeek dayOfWeek = todaysDate.getDayOfWeek();
        Month month = todaysDate.getMonth();
        setResultsLabel(new JLabel(dayOfWeek + ", " + month  + " " + todaysDate.getDayOfMonth() + ", " + todaysDate.getYear()));
        getResultsLabel().setFont(font_Bold);
        getResultsLabel().setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void performDatePickerFunctionality(ActionEvent ae)
    {
        getLogger().info("Performing date picker functionality");
        if (getOptionsBox().getSelectedItem().toString().equals(OPTIONS1))
        {
            updateResultsTextBox();
        }
        else if (getOptionsBox().getSelectedItem().toString().equals(OPTIONS2))
        {
            int year = getTheYearFromTheFromDatePicker();
            int month = getTheMonthFromTheFromDatePicker();
            int dayOfMonth = getTheDayOfTheMonthFromTheFromDatePicker();
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
            getLogger().debug("new date selected: " + localDateTime.toLocalDate());
            updateResultsLabel(localDateTime);
        }
        getLogger().info("finished performing date picker logic\n");
        getCalculator().confirm("New date, FROM DATE chosen");
    }

    public int getTheYearFromTheFromDatePicker() { return getFromDatePicker().getModel().getYear(); }
    public int getTheMonthFromTheFromDatePicker() { return getFromDatePicker().getModel().getMonth()+1; }
    public int getTheWeekFromTheFromDatePicker() {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker();
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);

        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker();
        day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate toDate = LocalDate.of(year, month, day);

        if (fromDate.isBefore(toDate)) return (int)ChronoUnit.WEEKS.between(fromDate, toDate);
        else if (fromDate.isAfter(toDate)) return (int)ChronoUnit.WEEKS.between(toDate, fromDate);
        else return 0;
    }
    public int getTheDayOfTheMonthFromTheFromDatePicker() { return getFromDatePicker().getModel().getDay(); }

    public int getTheYearFromTheToDatePicker() { return getToDatePicker().getModel().getYear(); }
    public int getTheMonthFromTheToDatePicker() { return getToDatePicker().getModel().getMonth()+1; }
    public int getTheWeekFromTheToDatePicker()
    {
        int year = getTheYearFromTheToDatePicker();
        int month = getTheMonthFromTheToDatePicker();
        int day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);

        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker();
        day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate toDate = LocalDate.of(year, month, day);

        if (fromDate.isBefore(toDate)) return (int)ChronoUnit.WEEKS.between(fromDate, toDate);
        else if (fromDate.isAfter(toDate)) return (int)ChronoUnit.WEEKS.between(toDate, fromDate);
        else return 0;
    }
    public int getTheDayOfTheMonthFromTheToDatePicker() { return getToDatePicker().getModel().getDay(); }

    public void updateThisPanel()
    {
        getLogger().info("Panel updated");
        this.repaint();
        this.revalidate();
        try { UIManager.setLookAndFeel(UIManager.getLookAndFeel()); }
        catch (UnsupportedLookAndFeelException ulfe) {
            getLogger().error("Unable to set the Look and Feel. Defaulting to Metal.");
            try { UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); }
            catch (UnsupportedLookAndFeelException |
                   ClassNotFoundException |
                   InstantiationException |
                   IllegalAccessException e)
            { getLogger().error("An exception has occurred: " + e.getMessage()); }
        }
        SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(getCalculator()));
        getCalculator().pack();
    }

    public void switchComponentsForDateDifference()
    {
        getLogger().debug("Difference between dates selected");
        //defaultOptionFromOptionsBox = OPTIONS1 + SPACE + SELECTED;
        // remove appropriate components first
        remove(getButtonGroupPanel());
        remove(getBlankLabel3());
        //remove(getLabelsGroupPanel());
        remove(getTextFieldsGroupPanel());
        remove(getBlankLabel4());
        remove(getDateLabel());
        remove(getResultsLabel());
        remove(getBlankLabel5());
        // update combobox
        //getOptionsBox().setSelectedIndex(0);
        // add appropriate components
        addComponent(getToDateLabel(), 5, 0, 1,1, 0,1.0);
        addComponent(getToDatePicker(), 6,0,1,1, 0,1.0);
        addComponent(getBlankLabel3(), 7, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(getDifferenceLabel(), 8, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(getYearsDifferenceLabel(), 9, 0, 1, 1, 0,1.0);
        addComponent(getMonthsDifferenceLabel(), 10, 0, 1, 1, 0,1.0);
        addComponent(getWeeksDifferenceLabel(), 11, 0, 1, 1, 0, 1.0);
        addComponent(getDaysDifferenceLabel(), 12, 0, 1, 1, 0,1.0);
        addComponent(getBlankLabel4(), 13, 0, 1, 1,1.0,1.0); // representing a blank space
        updateResultsTextBox();
    }

    public void switchComponentsForAddSubDate()
    {
        getLogger().debug("Add or subtract days selected");
        //defaultOptionFromOptionsBox = OPTIONS2 + SPACE + SELECTED;
        // remove appropriate components first
        remove(getToDateLabel());
        remove(getToDatePicker());
        remove(getBlankLabel3());
        remove(getDifferenceLabel());
        remove(getYearsDifferenceLabel());
        remove(getWeeksDifferenceLabel());
        remove(getMonthsDifferenceLabel());
        remove(getDaysDifferenceLabel());
        remove(getBlankLabel4());
        // update combo box
        //getOptionsBox().setSelectedIndex(1);
        // add appropriate components
        addComponent(getButtonGroupPanel(), 5, 0, 1,1, 0,1.0);
        addComponent(getBlankLabel3(), 6, 0, 1, 1,0, 1.0); // representing a blank space
        addComponent(getTextFieldsGroupPanel(), 7, 0, 1, 1, 0, 1.0);
        addComponent(getBlankLabel4(), 8, 0, 1, 1, 0.0, 1.0);
        addComponent(getDateLabel(), 9, 0, 1, 1, 0, 1.0);
        addComponent(getResultsLabel(), 10, 0, 1, 1, 0, 1.0);
        addComponent(getBlankLabel5(), 11, 0, 1, 1, 1.0, 1.0);

        getYearsTextField().setText("");
        getMonthsTextField().setText("");
        getWeeksTextField().setText("");
        getDaysTextField().setText("");
    }

    private void performOptionsBoxFunctionality(ActionEvent actionEvent)
    {
        getLogger().info("performing optionsBoxFunctionality");
        getLogger().debug("selected option: " + getSelectedOption());
        if (!getOptionsBox().getSelectedItem().equals(OPTIONS2) &&
            !getSelectedOption().equals(OPTIONS1))
        { // Only Switch to OPTIONS1 if and only if 2e are really showing OPTIONS2
            switchComponentsForDateDifference();
            setSelectedOption(OPTIONS1);
            updateThisPanel();
            getCalculator().confirm("Changing to " + OPTIONS1, CalcType_v4.DATE);
        }
        else if (!getOptionsBox().getSelectedItem().toString().equals(OPTIONS1) &&
                 !getSelectedOption().equals(OPTIONS2))
        { // Only Switch to OPTIONS2 if and only if we are really showing OPTIONS1
            switchComponentsForAddSubDate();
            setSelectedOption(OPTIONS2);
            updateThisPanel();
            getCalculator().confirm("Changing to " + OPTIONS2, CalcType_v4.DATE);
        }
        else
        { getCalculator().confirm("Options not changed", CalcType_v4.DATE); }
    }

    public void performRadioButtonFunctionality(ActionEvent actionEvent)
    {
        getLogger().info("Performing radiobutton function");
        int year = getFromDatePicker().getModel().getYear();
        int month = getFromDatePicker().getModel().getMonth()+1;
        int dayOfMonth = getFromDatePicker().getModel().getDay();
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
        getLogger().debug("Date is {}", localDateTime.toLocalDate());

        int years = Integer.parseInt(StringUtils.isBlank(getYearsTextField().getText()) ? "0" : getYearsTextField().getText());
        int months = Integer.parseInt(StringUtils.isEmpty(getMonthsTextField().getText()) ? "0" : getMonthsTextField().getText());
        int weeks = Integer.parseInt(StringUtils.isEmpty(getWeeksTextField().getText()) ? "0" : getWeeksTextField().getText());
        int days = Integer.parseInt(StringUtils.isEmpty(getDaysTextField().getText()) ? "0" : getDaysTextField().getText());

        getLogger().debug("Years is {}", years);
        getLogger().debug("Months is {}", months);
        getLogger().debug("Weeks is {}", weeks);
        getLogger().debug("Days is {}", days);

        if (getAddRadioButton().isSelected())
        {
            getLogger().info("Adding values to the date");
            localDateTime = localDateTime.plusDays(days);
            localDateTime = localDateTime.plusWeeks(weeks);
            localDateTime = localDateTime.plusMonths(months);
            localDateTime = localDateTime.plusYears(years);
        }
        else
        {
            getLogger().info("Subtracting values from the date");
            localDateTime = localDateTime.minusYears(years);
            localDateTime = localDateTime.minusWeeks(weeks);
            localDateTime = localDateTime.minusMonths(months);
            localDateTime = localDateTime.minusDays(days);
        }
        getLogger().debug("Date is now {}", localDateTime.toLocalDate());
        updateFromDate(localDateTime);
        updateResultsLabel(localDateTime);
    }

    public void performAddRadioButtonFunctionality(ActionEvent actionEvent)
    {
        getLogger().info("Performing add button functionality");
        getAddRadioButton().setSelected(true);
        getSubtractRadioButton().setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    public void performSubtractRadioButtonFunctionality(ActionEvent actionEvent)
    {
        getLogger().info("Performing subtract button functionality");
        getSubtractRadioButton().setSelected(true);
        getAddRadioButton().setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    private void addStartupComponentsToJPanelDate(String chosenOption)
    {
        LOGGER.info("Adding components to date panel, using " + chosenOption);
        if (chosenOption.equals(OPTIONS1))
        {
            addComponent(getOptionsBox(), 0,0,1,1, 0,1.0);
            addComponent(getBlankLabel1(), 1, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getFromDateLabel(), 2, 0, 1,1, 0,1.0);
            addComponent(getFromDatePicker(), 3,0,1,1, 0,1.0);
            addComponent(getBlankLabel2(), 4, 0, 1, 1,0,1.0); // representing a blank space

            addComponent(getToDateLabel(), 5, 0, 1,1, 0,1.0);
            addComponent(getToDatePicker(), 6,0,1,1, 0,1.0);
            addComponent(getBlankLabel3(), 7, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getDifferenceLabel(), 8, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getYearsDifferenceLabel(), 9, 0, 1, 1, 0,1.0);
            addComponent(getMonthsDifferenceLabel(), 10, 0, 1, 1, 0,1.0);
            addComponent(getWeeksDifferenceLabel(), 11, 0, 1, 1, 0, 1.0);
            addComponent(getDaysDifferenceLabel(), 12, 0, 1, 1, 0,1.0);
            addComponent(getBlankLabel4(), 13, 0, 1, 1,1.0,1.0); // representing a blank space
            updateResultsTextBox();
        }
        else {
            addComponent(getOptionsBox(), 0,0,1,1, 0,1.0);
            addComponent(getBlankLabel1(), 1, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getFromDateLabel(), 2, 0, 1,1, 0,1.0);
            addComponent(getFromDatePicker(), 3,0,1,1, 0,1.0);
            addComponent(getBlankLabel2(), 4, 0, 1, 1,0,1.0); // representing a blank space
            // add appropriate components
            addComponent(getButtonGroupPanel(), 5, 0, 1,1, 0,1.0);
            addComponent(getBlankLabel3(), 6, 0, 1, 1,0, 1.0); // representing a blank space
            addComponent(getTextFieldsGroupPanel(), 7, 0, 1, 1, 0, 1.0);
            addComponent(getBlankLabel4(), 8, 0, 1, 1, 0.0, 1.0);
            addComponent(getDateLabel(), 9, 0, 1, 1, 0, 1.0);
            addComponent(getResultsLabel(), 10, 0, 1, 1, 0, 1.0);
            addComponent(getBlankLabel5(), 11, 0, 1, 1, 1.0, 1.0);
        }
        LOGGER.info("Finished adding components to date panel");
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

    public void performOptionsBoxFunctionality(String option) throws UnsupportedLookAndFeelException
    {
        getLogger().debug("chosenOption: {}", option);
        if (option.equals(OPTIONS1))
        {
            getLogger().debug("Difference between dates selected");
            //defaultOptionFromOptionsBox = OPTIONS1 + SPACE + SELECTED;
            // remove appropriate components first
            remove(getButtonGroupPanel());
            remove(getBlankLabel3());
            //remove(getLabelsGroupPanel());
            remove(getTextFieldsGroupPanel());
            remove(getBlankLabel4());
            remove(getDateLabel());
            remove(getResultsLabel());
            remove(getBlankLabel5());
            // udpate combobox
            getOptionsBox().setSelectedIndex(0);
            // add appropriate components
            addComponent(getToDateLabel(), 5, 0, 1,1, 0,1.0);
            addComponent(getToDatePicker(), 6,0,1,1, 0,1.0);
            addComponent(getBlankLabel3(), 7, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getDifferenceLabel(), 8, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(getYearsDifferenceLabel(), 9, 0, 1, 1, 0,1.0);
            addComponent(getMonthsDifferenceLabel(), 10, 0, 1, 1, 0,1.0);
            addComponent(getDaysDifferenceLabel(), 11, 0, 1, 1, 0,1.0);
            addComponent(getBlankLabel4(), 12, 0, 1, 1,1.0,1.0); // representing a blank space
            updateResultsTextBox();
        }
        else if (option.equals(OPTIONS2))
        {
            getLogger().debug("Add or subtract days selected");
            //defaultOptionFromOptionsBox = OPTIONS2 + SPACE + SELECTED;
            // remove appropriate components first
            remove(getToDateLabel());
            remove(getToDatePicker());
            remove(getBlankLabel3());
            remove(getDifferenceLabel());
            remove(getYearsDifferenceLabel());
            remove(getMonthsDifferenceLabel());
            remove(getDaysDifferenceLabel());
            remove(getBlankLabel4());
            // update combo box
            getOptionsBox().setSelectedIndex(1);
            // add appropriate components
            addComponent(getButtonGroupPanel(), 5, 0, 1,1, 0,1.0);
            addComponent(getBlankLabel3(), 6, 0, 1, 1,0, 1.0); // representing a blank space
            addComponent(getTextFieldsGroupPanel(), 7, 0, 1, 1, 0, 1.0);
            addComponent(getBlankLabel4(), 8, 0, 1, 1, 0.0, 1.0);
            addComponent(getDateLabel(), 9, 0, 1, 1, 0, 1.0);
            addComponent(getResultsLabel(), 10, 0, 1, 1, 0, 1.0);
            addComponent(getBlankLabel5(), 11, 0, 1, 1, 1.0, 1.0);
        }
        updateThisPanel();
    }

    public int[] calculateDifferenceBetweenDates()
    {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker();
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);
        Instant fromInstance = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker();
        day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate toDate = LocalDate.of(year, month, day);
        Instant toInstance = toDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        int[] numberOfYearsMonthsWeeksAndDays = {0,0,0,0}; // year, month, weeks, days
        LocalDate localFromDate = fromInstance
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localToDate = toInstance
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Period period = Period.between(localFromDate, localToDate);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        if (fromDate.isBefore(toDate))
        {
            getLogger().info(fromDate + " is before " + toDate);
            years = period.getYears();
            months = period.getMonths();
            days = period.getDays();
            while (days/7 > 0)
            { // if there's 7 days, add 1 to weeks, subtract 7 from days
                weeks += 1;
                days -= 7;
            }
        }
        else if (fromDate.isAfter(toDate))
        { // TODO: TEST the negatives
            getLogger().info(fromDate + " is after " + toDate);
            years = period.getYears() * -1;
            months = period.getMonths() * -1;
            days = period.getDays() * -1;
            while (days/7 > 0)
            { // if there's 7 days, add 1 to weeks, subtract 7 from days
                weeks += 1;
                days -= 7;
            }
        }
        else // dates are the same
        {
            getLogger().info(fromDate + " is the same as " + toDate);
            years = 0;
            months = 0;
            weeks = 0;
            days = 0;
        }
        numberOfYearsMonthsWeeksAndDays[0] = years;
        numberOfYearsMonthsWeeksAndDays[1] = months;
        numberOfYearsMonthsWeeksAndDays[2] = weeks;
        numberOfYearsMonthsWeeksAndDays[3] = days;
        getLogger().debug("Years: " + numberOfYearsMonthsWeeksAndDays[0]);
        getLogger().debug("Months: " + numberOfYearsMonthsWeeksAndDays[1]);
        getLogger().debug("Weeks: " + numberOfYearsMonthsWeeksAndDays[2]);
        getLogger().debug("Days: " + numberOfYearsMonthsWeeksAndDays[3]);
        return numberOfYearsMonthsWeeksAndDays;
    }

    public void updateFromDate(LocalDateTime ldt)
    {
        getFromDatePicker().getModel().setYear(ldt.getYear());
        getFromDatePicker().getModel().setMonth(ldt.getMonthValue()-1);
        getFromDatePicker().getModel().setDay(ldt.getDayOfMonth());
    }

    public void updateToDate(LocalDateTime ldt)
    {
        getToDatePicker().getModel().setYear(ldt.getYear());
        getToDatePicker().getModel().setMonth(ldt.getMonthValue()-1);
        getToDatePicker().getModel().setDay(ldt.getDayOfMonth());
    }

    private void updateResultsLabel(LocalDateTime ldt)
    {
        getLogger().info("updating the result label with a new date: " + ldt);
        //getCalendar().set(year, monthInt, date);
        //getCalendar().setTime(getCalendar().getTime());
        //setCalendar(getCalendar());
        //getLogger().debug("Date is now {}", getCalendar().getTime());

        DayOfWeek dayOfWeek = ldt.getDayOfWeek();
        //String dayOfWeek = getCalendar().getTime().toInstant()
        //        .atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        Month month = ldt.getMonth();
        //String month = getCalendar().getTime().toInstant()
        //        .atZone(ZoneId.systemDefault()).toLocalDate().getMonth().getDisplayName(TextStyle.FULL, new Locale("en", "US"));
        getResultsLabel().setText(dayOfWeek + ", " + month + " " + ldt.getDayOfMonth() + ", " + ldt.getYear());
        getLogger().info("result label updated with: " + getResultsLabel().getText());
    }

    public LocalDate getTheDateFromTheFromDate()
    {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker();
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        return LocalDate.of(year, month, day);
    }

    public LocalDate getTheDateFromTheToDate()
    {
        int year = getTheYearFromTheToDatePicker();
        int month = getTheMonthFromTheToDatePicker();
        int day = getTheDayOfTheMonthFromTheToDatePicker();
        return LocalDate.of(year, month, day);
    }

    public void updateResultsTextBox()
    {
        int[] numberOfYearsMonthsWeeksAndDays = calculateDifferenceBetweenDates();
        String wordYear = YEAR.toLowerCase() + LOWER_CASE_S;
        String wordMonth = MONTH.toLowerCase() + LOWER_CASE_S;
        String wordWeek = WEEK.toLowerCase() + LOWER_CASE_S;
        String wordDay = DAY.toLowerCase() + LOWER_CASE_S;
        if (numberOfYearsMonthsWeeksAndDays[0] == 1 || numberOfYearsMonthsWeeksAndDays[0] == -1)
        {
            wordYear = YEAR.toLowerCase();
        }
        if (numberOfYearsMonthsWeeksAndDays[1] == 1 || numberOfYearsMonthsWeeksAndDays[1] == -1)
        {
            wordMonth = MONTH.toLowerCase();
        }
        if (numberOfYearsMonthsWeeksAndDays[2] == 1 || numberOfYearsMonthsWeeksAndDays[2] == -1)
        {
            wordWeek = WEEK.toLowerCase();
        }
        if (numberOfYearsMonthsWeeksAndDays[3] == 1 || numberOfYearsMonthsWeeksAndDays[3] == -1)
        {
            wordDay = DAY.toLowerCase();
        }

        LocalDate fromDate = getTheDateFromTheFromDate();
        //Instant fromInstance = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        LocalDate toDate = getTheDateFromTheToDate();
        if(fromDate.isBefore(toDate))
        {
            getLogger().info(fromDate + " is before " + toDate);
            //numberOfYearsMonthsWeeksAndDays[2] = (int)ChronoUnit.WEEKS.between(fromDate, toDate); //TechTrip - ASSUMPTION fromDate is earlier than toDate
            getYearsDifferenceLabel().setText(numberOfYearsMonthsWeeksAndDays[0] + SPACE + wordYear);
            getMonthsDifferenceLabel().setText(numberOfYearsMonthsWeeksAndDays[1] + SPACE + wordMonth);
            getWeeksDifferenceLabel().setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            getDaysDifferenceLabel().setText(numberOfYearsMonthsWeeksAndDays[3] + SPACE + wordDay);
        }
        else if(fromDate.isAfter(toDate))
        {
            getLogger().info(fromDate + " is after " + toDate);
            //numberOfYearsMonthsWeeksAndDays[2] = (int)ChronoUnit.WEEKS.between(toDate, fromDate); //TechTrip - ASSUMPTION toDate is earlier than fromDate
            getYearsDifferenceLabel().setText((numberOfYearsMonthsWeeksAndDays[0]) + SPACE +  wordYear);
            getMonthsDifferenceLabel().setText((numberOfYearsMonthsWeeksAndDays[1]) + SPACE +  wordMonth);
            getWeeksDifferenceLabel().setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            getDaysDifferenceLabel().setText((numberOfYearsMonthsWeeksAndDays[3]) + SPACE +  wordDay);
        }
        else
        {
            getLogger().info(fromDate + " is the same as " + toDate);
            getYearsDifferenceLabel().setText(SAME + SPACE + YEAR);
            getMonthsDifferenceLabel().setText(SAME + SPACE + MONTH);
            getWeeksDifferenceLabel().setText(SAME + SPACE + WEEK);
            getDaysDifferenceLabel().setText(SAME + SPACE + DATE);
        }
        getLogger().info("Difference Results updated");
    }

    /************* All Getters ******************/
    public static Logger getLogger() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }
    public GridBagLayout getDateLayout() { return dateLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public UtilCalendarModel getFromModel() { return fromModel; }
    public UtilCalendarModel getToModel() { return toModel; }
    public JDatePanelImpl getFromDatePanel() { return fromDatePanel; }
    public JDatePanelImpl getToDatePanel()  { return toDatePanel; }
    public JDatePickerImpl getFromDatePicker() { return fromDatePicker; }
    public JDatePickerImpl getToDatePicker() { return toDatePicker; }
    public Calculator_v4 getCalculator() { return calculator; }
    public JComboBox getOptionsBox() { return optionsBox; }

    public JLabel getYearsDifferenceLabel() { return this.yearsDifferenceLabel; }
    public JLabel getMonthsDifferenceLabel() { return this.monthsDifferenceLabel; }
    public JLabel getWeeksDifferenceLabel() { return this.weeksDifferenceLabel; }
    public JLabel getDaysDifferenceLabel() { return this.daysDifferenceLabel; }
    // TODO: remove these whehn labels are working
    //public JTextField getYearsDiffTextField() { return yearsDiffTextField; }
    //public JTextField getMonthsDiffTextField() { return monthsDiffTextField; }
    //public JTextField getDaysDiffTextField() { return daysDiffTextField; }
    public JLabel getFromDateLabel() { return fromDateLabel; }
    public JLabel getToDateLabel() { return toDateLabel; }
    public JLabel getDifferenceLabel() { return differenceLabel; }
    public JLabel getBlankLabel1() { return blankLabel1; }
    public JLabel getBlankLabel2() { return blankLabel2; }
    public JLabel getBlankLabel3() { return blankLabel3; }
    public JLabel getBlankLabel4() { return blankLabel4;}
    public JLabel getBlankLabel5() { return blankLabel5; }
    public ButtonGroup getButtonGroup() { return buttonGroup; }
    public JRadioButton getAddRadioButton() { return addRadioButton; }
    public JRadioButton getSubtractRadioButton() { return subtractRadioButton; }
    public JPanel getButtonGroupPanel() { return buttonGroupPanel; }
    public JLabel getDateLabel() { return dateLabel; }
    public JLabel getYearsLabel() { return yearsLabel; }
    public JLabel getMonthsLabel() { return monthLabel; }
    public JLabel getWeeksLabel() { return this.weeksLabel; }
    public JLabel getDaysLabel() { return daysLabel; }
    public JPanel getTextFieldsGroupPanel() { return textFieldsGroupPanel; }
    public JTextField getYearsTextField() { return yearsTextField; }
    public JTextField getMonthsTextField() { return monthsTextField; }
    public JTextField getWeeksTextField() { return this.weeksTextField; }
    public JTextField getDaysTextField() { return daysTextField; }
    public JLabel getResultsLabel() { return resultsLabel; }
    public String getSelectedOption() { return this.selectedOption; }

    /************* All Setters ******************/
    private void setDateLayout(GridBagLayout dateLayout) { this.dateLayout = dateLayout; }
    private void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setFromModel(UtilCalendarModel fromModel) { this.fromModel = fromModel; }
    public void setToModel(UtilCalendarModel toModel) { this.toModel = toModel; }
    public void setFromDatePanel(JDatePanelImpl fromDatePanel) { this.fromDatePanel = fromDatePanel; }
    public void setToDatePanel(JDatePanelImpl toDatePanel) { this.toDatePanel = toDatePanel; }
    public void setFromDatePicker(JDatePickerImpl fromDatePicker) { this.fromDatePicker = fromDatePicker; }
    public void setToDatePicker(JDatePickerImpl toDatePicker) { this.toDatePicker = toDatePicker; }
    private void setCalculator(Calculator_v4 calculator) { this.calculator = calculator; }
    private void setOptionsBox(JComboBox optionsBox) { this.optionsBox = optionsBox; }
    private void setYearsDifferenceLabel(JLabel yearsDifferenceLabel) { this.yearsDifferenceLabel = yearsDifferenceLabel; }
    private void setMonthsDifferenceLabel(JLabel monthsDifferenceLabel) { this.monthsDifferenceLabel = monthsDifferenceLabel; }
    private void setWeeksDifferenceLabel(JLabel weeksDifferenceLabel) { this.weeksDifferenceLabel = weeksDifferenceLabel; }
    private void setDaysDifferenceLabel(JLabel daysDifferenceLabel) { this.daysDifferenceLabel = daysDifferenceLabel; }
    // TODO: remove these methods when label is working
    //private void setYearsDiffTextField(JTextField yearResultTextField) { this.yearsDiffTextField = yearResultTextField; }
    //private void setMonthsDiffTextField(JTextField monthsDiffTextField) { this.monthsDiffTextField = monthsDiffTextField; }
    //private void setDaysDiffTextField(JTextField daysDiffTextField) { this.daysDiffTextField = daysDiffTextField; }
    private void setFromDateLabel(JLabel fromDateLabel) { this.fromDateLabel = fromDateLabel; }
    private void setToDateLabel(JLabel toDateLabel) { this.toDateLabel = toDateLabel; }
    private void setDifferenceLabel(JLabel differenceLabel) { this.differenceLabel = differenceLabel; }
    private void setBlankLabel1(JLabel blankLabel1) { this.blankLabel1 = blankLabel1; }
    private void setBlankLabel2(JLabel blankLabel2) { this.blankLabel2 = blankLabel2; }
    private void setBlankLabel3(JLabel blankLabel3) { this.blankLabel3 = blankLabel3; }
    private void setBlankLabel4(JLabel blankLabel4) { this.blankLabel4 = blankLabel4; }
    private void setBlankLabel5(JLabel blankLabel5) { this.blankLabel5 = blankLabel5; }
    private void setButtonGroup(ButtonGroup buttonGroup) { this.buttonGroup = buttonGroup; }
    private void setAddRadioButton(JRadioButton addRadioButton) { this.addRadioButton = addRadioButton; }
    private void setSubtractRadioButton(JRadioButton subtractRadioButton) { this.subtractRadioButton = subtractRadioButton; }
    private void setButtonGroupPanel(JPanel buttonGroupPanel) { this.buttonGroupPanel = buttonGroupPanel; }
    private void setDateLabel(JLabel dateLabel) { this.dateLabel = dateLabel; }
    private void setYearsLabel(JLabel yearsLabel) { this.yearsLabel = yearsLabel; }
    private void setMonthLabel(JLabel monthLabel) { this.monthLabel = monthLabel; }
    private void setWeeksLabel(JLabel weeksLabel) { this.weeksLabel = weeksLabel; }
    private void setDaysLabel(JLabel daysLabel) { this.daysLabel = daysLabel; }
    private void setTextFieldsGroupPanel(JPanel textFieldsGroupPanel) { this.textFieldsGroupPanel = textFieldsGroupPanel; }
    private void setYearsTextField(JTextField yearsTextField) { this.yearsTextField = yearsTextField; }
    private void setMonthsTextField(JTextField monthsTextField) { this.monthsTextField = monthsTextField; }
    private void setWeeksTextField(JTextField weeksTextField) { this.weeksTextField = weeksTextField; }
    private void setDaysTextField(JTextField daysTextField) { this.daysTextField = daysTextField; }
    private void setResultsLabel(JLabel resultsLabel) { this.resultsLabel = resultsLabel; }
    private void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}
