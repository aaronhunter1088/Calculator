package Panels;

import Calculators.Calculator;
import Types.DateOperation;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static Calculators.Calculator.*;
import static Types.CalculatorView.*;
import static Types.DateOperation.*;
import static Types.Texts.*;
import static Utilities.LoggingUtil.confirm;
import static Utilities.LoggingUtil.logActionButton;

/**
 * DatePanel
 * <p>
 * This class contains components and actions
 * for the DatePanel of the Calculator.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class DatePanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(DatePanel.class.getSimpleName());

    private Calculator calculator;
    private GridBagConstraints constraints;
    private UtilCalendarModel fromCalendar, toCalendar;
    private JDatePanelImpl fromDatePanel, toDatePanel;
    private JDatePickerImpl fromDatePicker, toDatePicker;
    private JComboBox<DateOperation> dateOperationsDropdown;
    private JLabel fromDateLabel, toDateLabel, differenceLabel, dateLabel,
                   yearsLabel, monthLabel, weeksLabel, daysLabel, resultsLabel,
                   yearsDifferenceLabel, monthsDifferenceLabel, weeksDifferenceLabel, daysDifferenceLabel,
                   blankLabel1, blankLabel2, blankLabel3, blankLabel4, blankLabel5;
    private ButtonGroup buttonGroup;
    private JPanel buttonGroupPanel, textFieldsGroupPanel,
                   datePanel1, datePanel2, commonToBothDateCalculators;
    private JRadioButton addRadioButton, subtractRadioButton;
    private JTextField yearsTextField, monthsTextField, weeksTextField, daysTextField;
    private boolean isInitialized;

    /**************** CONSTRUCTORS ****************/
    /**
     * A zero argument constructor for creating a DatePanel
     */
    public DatePanel()
    {
        setName(VIEW_DATE.getValue());
        LOGGER.info("Empty Date panel created");
    }

    /**
     * The main construction used to create a DatePanel
     * @param calculator the Calculator to use
     * @param dateOperation the option to use
     */
    public DatePanel(Calculator calculator, DateOperation dateOperation)
    {
        setupDatePanel(calculator, dateOperation);
        LOGGER.info("Date panel created");
    }

    /**************** START OF METHODS ****************/
    /**
     * The main method used to define the DatePanel
     * and all of its components and their actions
     * @param calculator the Calculator object
     * @param dateOperation the DateOperation to use
     */
    public void setupDatePanel(Calculator calculator, DateOperation dateOperation)
    {
        if (!isInitialized)
        {
            setCalculator(calculator);
            setLayout(new GridBagLayout());
            setConstraints(new GridBagConstraints());
            calculator.setDateOperation(dateOperation != null ? dateOperation : DIFFERENCE_BETWEEN_DATES);
            setupDatePanelComponents(this.calculator.getDateOperation());
            createCommonPanel();
            datePanel1 = new JPanel(new GridBagLayout());
            datePanel2 = new JPanel(new GridBagLayout());
            isInitialized = true;
        }
        if (dateOperation == DIFFERENCE_BETWEEN_DATES)
        {
            setupDifferenceBetweenDatesPanel();
            calculator.addComponent(this, constraints, datePanel1);
            setSize(datePanel1.getSize());
        }
        else
        {
            setupAddOrSubtractPanel();
            calculator.addComponent(this, constraints, datePanel2);
            setSize(datePanel2.getSize());
        }
        setupHelpMenu();
        setName(VIEW_DATE.getValue());
        LOGGER.info("Finished setting up {} panel", VIEW_DATE.getValue());
    }

    /**
     * The main method which sets the help text
     * to be used in the help menu for the DatePanel,
     * and adds it to the Help menu item
     */
    private void setupHelpMenu()
    {
        LOGGER.info("Setting up the help menu for date panel");
        String helpString = "How to use the %s Calculator".formatted(this.calculator.getDateOperation());
        switch (calculator.getDateOperation())
        {
            case DIFFERENCE_BETWEEN_DATES -> helpString += """
                Enter a date into either field, From or To.
                Only 1 date is required to change to show a difference.
                See the difference between the two dates below.
            """; // .formatted();
            case ADD_SUBTRACT_DAYS -> helpString += """
                Select a date from the drop down option.
                Enter values in the choices: Years, Months, Weeks and/or Days.
                Click Add or Subtract to execute that action using your values and date.
            """; //.formatted();
        }
        calculator.setHelpString(helpString);
        calculator.updateShowHelp();
    }

    /**
     * Sets the date panel components
     */
    private void setupDatePanelComponents(DateOperation dateOperation)
    {
        setupOptionsSelection(dateOperation);
        setupFromDate();
        setupToDate();
        setupDifferenceLabel();
        setYearsDifferenceLabel(new JLabel(SAME + SPACE + YEAR));
        setMonthsDifferenceLabel(new JLabel(SAME + SPACE + MONTH));
        setWeeksDifferenceLabel(new JLabel(SAME + SPACE + WEEK));
        setDaysDifferenceLabel(new JLabel(SAME + SPACE + DAY));
        setupBlankLabel1();
        setupBlankLabel2();
        setupBlankLabel3();
        setupBlankLabel4();
        setupBlankLabel5();
        setupAddRadioButton();
        setupSubtractRadioButton();
        setupButtonGroup();
        setupYearsLabel();
        setupMonthsLabel();
        setupWeeksLabel();
        setupDaysLabel();
        setupYearsTextField();
        setupMonthsTextField();
        setupWeeksTextField();
        setupDaysTextField();
        setupTextFieldsPanel();
        setupAddOrSubtractResultLabel();
        setupResultsText();
        setTheSelectedOption(dateOperation);
        LOGGER.info("Finished setting up date panel");
    }

    /**
     * The main method used to set up the difference label
     */
    private void setupDifferenceLabel()
    {
        setDifferenceLabel(new JLabel("Difference"));
        differenceLabel.setFont(verdanaFontBold);
        differenceLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up the blank1 label
     */
    private void setupBlankLabel1()
    {
        setBlankLabel1(new JLabel(SPACE));
        blankLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up the blank2 label
     */
    private void setupBlankLabel2()
    {
        setBlankLabel2(new JLabel(SPACE));
        blankLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up the blank3 label
     */
    private void setupBlankLabel3()
    {
        setBlankLabel3(new JLabel(SPACE));
        blankLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up the blank4 label
     */
    private void setupBlankLabel4()
    {
        setBlankLabel4(new JLabel(SPACE));
        blankLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up the blank5 label
     */
    private void setupBlankLabel5()
    {
        setBlankLabel5(new JLabel(SPACE));
        blankLabel5.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up
     * the Add button in DateOperation2
     */
    private void setupAddRadioButton()
    {
        setAddRadioButton(new JRadioButton("Add"));
        addRadioButton.setSelected(true);
        addRadioButton.setName("Add");
        addRadioButton.addActionListener(this::performAddRadioButtonFunctionality);
    }

    /**
     * The main method used to set up
     * the Subtract button in DateOperation2
     */
    private void setupSubtractRadioButton()
    {
        setSubtractRadioButton(new JRadioButton("Subtract"));
        subtractRadioButton.setSelected(false);
        subtractRadioButton.setName("Subtract");
        subtractRadioButton.addActionListener(this::performSubtractRadioButtonFunctionality);
    }

    /**
     * The main method used to set up
     * the radio group in DateOperation2
     */
    private void setupButtonGroup()
    {
        setButtonGroup(new ButtonGroup());
        setButtonGroupPanel(new JPanel());
        buttonGroupPanel.setLayout(new GridLayout(1,2));
        buttonGroupPanel.add(addRadioButton);
        buttonGroupPanel.add(subtractRadioButton);
    }

    /**
     * The main method used to set up
     * the Years label in DateOperation2
     */
    private void setupYearsLabel()
    {
        setYearsLabel(new JLabel(YEAR + LOWER_CASE_S));
        yearsLabel.setFont(verdanaFontBold);
        yearsLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }
    /**
     * The main method used to set up
     * the Years text field in DateOperation2
     */
    private void setupYearsTextField()
    {
        setYearsTextField(new JTextField(EMPTY, 5));
        yearsTextField.setEditable(true);
        yearsTextField.setHorizontalAlignment(SwingConstants.LEFT);
        yearsTextField.setFont(mainFont);
    }

    /**
     * The main method used to set up
     * the Months label in DateOperation2
     */
    private void setupMonthsLabel()
    {
        setMonthLabel(new JLabel(MONTH + LOWER_CASE_S));
        monthLabel.setFont(verdanaFontBold);
        monthLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }
    /**
     * The main method used to set up
     * the Months text field in DateOperation2
     */
    private void setupMonthsTextField()
    {
        setMonthsTextField(new JTextField(EMPTY, 5));
        monthsTextField.setEditable(true);
        monthsTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up
     * the Weeks label in DateOperation2
     */
    private void setupWeeksLabel()
    {
        setWeeksLabel(new JLabel(WEEK + LOWER_CASE_S));
        weeksLabel.setFont(verdanaFontBold);
        weeksLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }
    /**
     * The main method used to set up
     * the Weeks text field in DateOperation2
     */
    private void setupWeeksTextField()
    {
        setWeeksTextField(new JTextField(EMPTY, 5));
        weeksTextField.setEditable(true);
        weeksTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to set up
     * the Days label in DateOperation2
     */
    private void setupDaysLabel()
    {
        setDaysLabel(new JLabel(DAY + LOWER_CASE_S));
        daysLabel.setFont(verdanaFontBold);
        daysLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }
    /**
     * The main method used to set up
     * the Days text field in DateOperation2
     */
    private void setupDaysTextField()
    {
        setDaysTextField(new JTextField(EMPTY, 5));
        daysTextField.setEditable(true);
        daysTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * The main method used to add the labels
     * and text fields to the converterPanel
     */
    private void setupTextFieldsPanel()
    {
        setTextFieldsGroupPanel(new JPanel());
        textFieldsGroupPanel.setLayout(new GridLayout(4, 2, 5, 5));
        textFieldsGroupPanel.add(yearsLabel);
        textFieldsGroupPanel.add(yearsTextField);
        textFieldsGroupPanel.add(monthLabel);
        textFieldsGroupPanel.add(monthsTextField);
        textFieldsGroupPanel.add(weeksLabel);
        textFieldsGroupPanel.add(weeksTextField);
        textFieldsGroupPanel.add(daysLabel);
        textFieldsGroupPanel.add(daysTextField);
    }

    /**
     * The main method used to set up
     * the Results label
     */
    private void setupAddOrSubtractResultLabel()
    {
        setDateLabel(new JLabel(ADD_OR_SUB_RESULT));
        dateLabel.setFont(verdanaFontBold);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }
    /**
     * The main method used to set up
     * the Results text field
     */
    private void setupResultsText()
    {
        LocalDate todaysDate = LocalDate.now();
        DayOfWeek dayOfWeek = todaysDate.getDayOfWeek();
        Month month = todaysDate.getMonth();
        setResultsLabel(new JLabel(dayOfWeek + ", " + month  + " " + todaysDate.getDayOfMonth() + ", " + todaysDate.getYear()));
        resultsLabel.setFont(mainFontBold);
        resultsLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * Set the selected DateOperation to use
     * @param dateOperation the chosen DateOperation
     */
    private void setTheSelectedOption(DateOperation dateOperation)
    {
        if (dateOperation == null || dateOperation == DateOperation.DIFFERENCE_BETWEEN_DATES)
            calculator.setDateOperation(DateOperation.DIFFERENCE_BETWEEN_DATES);
        else calculator.setDateOperation(DateOperation.ADD_SUBTRACT_DAYS);
    }

    /**
     * The main method used to set up
     * the selection dropdown box and adds
     * its functionality
     */
    private void setupOptionsSelection(DateOperation dateOperation)
    {
        setDateOperationsDropdown(new JComboBox<>(new DateOperation[]{DIFFERENCE_BETWEEN_DATES, ADD_SUBTRACT_DAYS}));
        dateOperationsDropdown.setSelectedItem(dateOperation == null ? DIFFERENCE_BETWEEN_DATES : dateOperation);
        dateOperationsDropdown.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        dateOperationsDropdown.addActionListener(this::performOptionsBoxFunctionality);
    }
    /**
     * The actions to perform when we change DateOperations
     * @param actionEvent the click action
     */
    private void performOptionsBoxFunctionality(ActionEvent actionEvent)
    {
        if (dateOperationsDropdown.getSelectedItem() == DIFFERENCE_BETWEEN_DATES)
        { // Only Switch to OPTIONS1 if and only if we are really showing OPTIONS2
            calculator.setDateOperation(DateOperation.DIFFERENCE_BETWEEN_DATES);
            switchComponentsForDateDifference();
            setupHelpMenu();
            updateThisPanel();
            confirm(calculator, LOGGER, "Changing to " + DIFFERENCE_BETWEEN_DATES);
        }
        else if (dateOperationsDropdown.getSelectedItem() == ADD_SUBTRACT_DAYS )
        { // Only Switch to OPTIONS2 if and only if we are really showing OPTIONS1
            calculator.setDateOperation(DateOperation.ADD_SUBTRACT_DAYS);
            switchComponentsForAddSubDate();
            setupHelpMenu();
            updateThisPanel();
            confirm(calculator, LOGGER, "Changing to " + ADD_SUBTRACT_DAYS);
        }
        else
        { confirm(calculator, LOGGER, "Options not changed"); }
    }

    /**
     * The main method used to set up
     * the FromDate in DateOperation1
     */
    private void setupFromDate()
    {
        setFromDateLabel(new JLabel("From Date"));
        fromDateLabel.setFont(verdanaFontBold);
        fromDateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        //
        setFromCalendar(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        fromCalendar.setDate(year, monthInt, day);
        fromCalendar.setSelected(true);
        setFromDatePanel(new JDatePanelImpl(fromCalendar));
        setFromDatePicker(new JDatePickerImpl(fromDatePanel));
        fromDatePicker.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        fromDatePicker.addActionListener(this::performDatePickerFunctionality);
    }

    /**
     * The main method used to set up
     * the ToDate in DateOperation1
     */
    private void setupToDate()
    {
        setToDateLabel(new JLabel("To Date"));
        toDateLabel.setFont(verdanaFontBold);
        toDateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        setToCalendar(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        toCalendar.setDate(year, monthInt, day); // defaults to today
        toCalendar.setSelected(true);
        setToDatePanel(new JDatePanelImpl(toCalendar));
        setToDatePicker(new JDatePickerImpl(toDatePanel));
        toDatePicker.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        toDatePicker.addActionListener(this::performDatePickerFunctionality); //action -> updateResultsTextBox());
    }

    /**
     * The actions to perform when we choose a date
     * @param actionEvent the click action
     */
    public void performDatePickerFunctionality(ActionEvent actionEvent)
    {
        LOGGER.info("Performing From Date picker");
        if (Objects.requireNonNull(dateOperationsDropdown.getSelectedItem()) == DIFFERENCE_BETWEEN_DATES )
        { updateResultsTextBox(); }
        else if (dateOperationsDropdown.getSelectedItem() == ADD_SUBTRACT_DAYS)
        {
            int year = getTheYearFromTheFromDatePicker();
            int month = getTheMonthFromTheFromDatePicker()+1;
            int dayOfMonth = getTheDayOfTheMonthFromTheFromDatePicker();
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
            LOGGER.debug("new date selected: {}", localDateTime.toLocalDate());
            updateResultsLabel(localDateTime);
        }
        LOGGER.info("Finished performing date picker logic");
        confirm(calculator, LOGGER, "New date, FROM DATE chosen");
    }

    /**
     * Returns the years from the FromDate
     * @return int the fromDate year
     */
    public int getTheYearFromTheFromDatePicker()
    { return fromDatePicker.getModel().getYear(); }

    /**
     * Returns the month from the FromDate
     * @return int the fromDate month, zero-index based
     */
    public int getTheMonthFromTheFromDatePicker()
    { return fromDatePicker.getModel().getMonth(); }

    /**
     * Returns the weeks from the FromDate
     * @return int the fromDate weeks
     */
    public int getTheWeekFromTheFromDatePicker()
    {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker();
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);
        //
        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker();
        day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate toDate = LocalDate.of(year, month, day);
        //
        if (fromDate.isBefore(toDate)) return (int)ChronoUnit.WEEKS.between(fromDate, toDate);
        else if (fromDate.isAfter(toDate)) return (int)ChronoUnit.WEEKS.between(toDate, fromDate);
        else return 0;
    }

    /**
     * Returns the days from the FromDate
     * @return int the fromDate days
     */
    public int getTheDayOfTheMonthFromTheFromDatePicker()
    { return fromDatePicker.getModel().getDay(); }

    /**
     * Returns the years from the ToDate
     * @return int the toDate year
     */
    public int getTheYearFromTheToDatePicker()
    { return toDatePicker.getModel().getYear(); }

    /**
     * Returns the month from the ToDate
     * @return int the toDate month
     */
    public int getTheMonthFromTheToDatePicker()
    { return toDatePicker.getModel().getMonth(); }

    /**
     * Returns the weeks from the ToDate
     * @return int the toDate weeks
     */
    public int getTheWeekFromTheToDatePicker()
    {
        int year = getTheYearFromTheToDatePicker();
        int month = getTheMonthFromTheToDatePicker();
        int day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);
        //
        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker();
        day = getTheDayOfTheMonthFromTheToDatePicker();
        LocalDate toDate = LocalDate.of(year, month, day);
        //
        if (fromDate.isBefore(toDate)) return (int)ChronoUnit.WEEKS.between(fromDate, toDate);
        else if (fromDate.isAfter(toDate)) return (int)ChronoUnit.WEEKS.between(toDate, fromDate);
        else return 0;
    }

    /**
     * Returns the days from the ToDate
     * @return int the toDate days
     */
    public int getTheDayOfTheMonthFromTheToDatePicker()
    { return toDatePicker.getModel().getDay(); }

    /**
     * Updates the panel and packs it together
     */
    private void updateThisPanel()
    {
        SwingUtilities.updateComponentTreeUI(this);
        calculator.pack();
    }

    /**
     * The main method called to remove certain
     * components when switching to DateOperations1
     * and add other components to the DatePanel
     */
    public void switchComponentsForDateDifference()
    {
        remove(datePanel2);
        setupDifferenceBetweenDatesPanel();
        calculator.addComponent(this, constraints, datePanel1);
    }

    /**
     * The main method called to remove certain
     * components when switching to DateOperations2
     * and add other components to the DatePanel
     */
    public void switchComponentsForAddSubDate()
    {
        remove(datePanel1);
        setupAddOrSubtractPanel();
        calculator.addComponent(this, constraints, datePanel2);
    }

    /**
     * The actions to perform when Add is clicked
     * @param actionEvent the click action
     */
    public void performAddRadioButtonFunctionality(ActionEvent actionEvent)
    {
        addRadioButton.setSelected(true);
        subtractRadioButton.setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    /**
     * The actions to perform when Subtract is clicked
     * @param actionEvent the click action
     */
    public void performSubtractRadioButtonFunctionality(ActionEvent actionEvent)
    {
        subtractRadioButton.setSelected(true);
        addRadioButton.setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    /**
     * The inner logic performed when clicking Add or Subtract
     * when in dateOperation ADD_SUBTRACT_DAYS
     * @param actionEvent the click action
     */
    public void performRadioButtonFunctionality(ActionEvent actionEvent)
    {
        String buttonChoice = actionEvent.getActionCommand();
        logActionButton(buttonChoice, LOGGER);
        LOGGER.debug("Add or Subtract radiobutton");
        LocalDateTime localDateTime = LocalDateTime.of(getTheDateFromTheFromDate(), LocalTime.now());
        LOGGER.debug("FromDate is {}", localDateTime.toLocalDate());
        int years = Integer.parseInt(StringUtils.isBlank(yearsTextField.getText()) ? ZERO : yearsTextField.getText());
        int months = Integer.parseInt(StringUtils.isEmpty(monthsTextField.getText()) ? ZERO : monthsTextField.getText());
        int weeks = Integer.parseInt(StringUtils.isEmpty(weeksTextField.getText()) ? ZERO : weeksTextField.getText());
        int days = Integer.parseInt(StringUtils.isEmpty(daysTextField.getText()) ? ZERO : daysTextField.getText());
        LOGGER.debug("Years is {}", years);
        LOGGER.debug("Months is {}", months);
        LOGGER.debug("Weeks is {}", weeks);
        LOGGER.debug("Days is {}", days);
        if (addRadioButton.isSelected())
        {
            LOGGER.info("Adding values to the date");
            localDateTime = localDateTime.plusDays(days);
            localDateTime = localDateTime.plusWeeks(weeks);
            localDateTime = localDateTime.plusMonths(months);
            localDateTime = localDateTime.plusYears(years);
        }
        else
        {
            LOGGER.info("Subtracting values from the date");
            localDateTime = localDateTime.minusDays(days);
            localDateTime = localDateTime.minusWeeks(weeks);
            localDateTime = localDateTime.minusMonths(months);
            localDateTime = localDateTime.minusYears(years);
        }
        LOGGER.info("Date is now {}", localDateTime.toLocalDate());
        updateToDate(localDateTime);
        updateResultsLabel(localDateTime);
        if (addRadioButton.isSelected()) confirm(calculator, LOGGER, "Pressed Add");
        else confirm(calculator, LOGGER, "Pressed Subtract");
    }

    /**
     * Specifies where each button is placed
     * on the datePanel1
     */
    private void setupDifferenceBetweenDatesPanel()
    {
        calculator.addComponent(this, constraints, datePanel1, commonToBothDateCalculators, 0, 0, 0);
        JPanel differenceBetweenDates = new JPanel(new GridBagLayout());
        calculator.addComponent(this, constraints, differenceBetweenDates, toDateLabel, 0, 0,1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, toDatePicker, 1, 0,1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, blankLabel3, 2, 0,1.0); // representing a blank space
        calculator.addComponent(this, constraints, differenceBetweenDates, differenceLabel, 3, 0,1.0); // representing a blank space
        calculator.addComponent(this, constraints, differenceBetweenDates, yearsDifferenceLabel, 4, 0,1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, monthsDifferenceLabel, 5, 0,1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, weeksDifferenceLabel, 6, 0, 1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, daysDifferenceLabel, 7, 0,1.0);
        calculator.addComponent(this, constraints, differenceBetweenDates, blankLabel4, 8, 1.0,1.0); // representing a blank space
        updateResultsTextBox();
        calculator.addComponent(this, constraints, datePanel1, differenceBetweenDates, 1, 0, 0);
        LOGGER.debug("Finished adding components to DifferenceBetweenDates panel");
    }

    /**
     * Specifies where each button is placed
     * on the datePanel2
     */
    private void setupAddOrSubtractPanel()
    {
        calculator.addComponent(this, constraints, datePanel2, commonToBothDateCalculators, 0, 0, 0);
        JPanel addOrSubtractDates = new JPanel(new GridBagLayout());
        calculator.addComponent(this, constraints, addOrSubtractDates, buttonGroupPanel, 0, 0,1.0);
        calculator.addComponent(this, constraints, addOrSubtractDates, blankLabel3, 1, 0, 1.0); // representing a blank space
        calculator.addComponent(this, constraints, addOrSubtractDates, textFieldsGroupPanel, 2, 0, 1.0);
        calculator.addComponent(this, constraints, addOrSubtractDates, blankLabel4, 3, 0.0, 1.0);
        calculator.addComponent(this, constraints, addOrSubtractDates, dateLabel, 4, 0, 1.0);
        calculator.addComponent(this, constraints, addOrSubtractDates, resultsLabel, 5, 0, 1.0);
        calculator.addComponent(this, constraints, addOrSubtractDates, blankLabel5, 6, 1.0, 1.0);
        calculator.addComponent(this, constraints, datePanel2, addOrSubtractDates, 1, 0, 0);
        LOGGER.debug("Finished adding components to AddOrSubtract panel");
    }

    /**
     * Creates the common panel used by both date operations
     */
    private void createCommonPanel()
    {
        commonToBothDateCalculators = new JPanel(new GridBagLayout());
        calculator.addComponent(this, constraints, commonToBothDateCalculators, dateOperationsDropdown, 0, 0, null, 1,1, 0,0, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        calculator.addComponent(this, constraints, commonToBothDateCalculators, fromDateLabel, 1, 0,1.0);
        calculator.addComponent(this, constraints, commonToBothDateCalculators, fromDatePicker, 2, 0,1.0);
        LOGGER.debug("Finished adding components to the common panel");
    }

    /**
     * Calculates the difference between the two dates
     * and sets the years, months, weeks, and day differences
     * @return int[] storing the difference between the dates
     */
    public int[] calculateDifferenceBetweenDates()
    {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker()+1;
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDate fromDate = LocalDate.of(year, month, day);
        Instant fromInstance = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        year = getTheYearFromTheToDatePicker();
        month = getTheMonthFromTheToDatePicker()+1;
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
            LOGGER.info("{} is before {}", fromDate, toDate);
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
            LOGGER.info("{} is after {}", fromDate, toDate);
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
            LOGGER.info("{} is the same as {}", fromDate, toDate);
        }
        numberOfYearsMonthsWeeksAndDays[0] = years;
        numberOfYearsMonthsWeeksAndDays[1] = months;
        numberOfYearsMonthsWeeksAndDays[2] = weeks;
        numberOfYearsMonthsWeeksAndDays[3] = days;
        LOGGER.debug("Years: {}", numberOfYearsMonthsWeeksAndDays[0]);
        LOGGER.debug("Months: {}", numberOfYearsMonthsWeeksAndDays[1]);
        LOGGER.debug("Weeks: {}", numberOfYearsMonthsWeeksAndDays[2]);
        LOGGER.debug("Days: {}", numberOfYearsMonthsWeeksAndDays[3]);
        return numberOfYearsMonthsWeeksAndDays;
    }

    /**
     * Updates the FromDate
     * @param ldt the date update the FromDate to
     */
    private void updateFromDate(LocalDateTime ldt)
    {
        fromDatePicker.getModel().setYear(ldt.getYear());
        fromDatePicker.getModel().setMonth(ldt.getMonth().getValue());
        fromDatePicker.getModel().setDay(ldt.getDayOfMonth());
    }

    /**
     * Updates the ToDate
     * @param ldt the date update the ToDate to
     */
    private void updateToDate(LocalDateTime ldt)
    {
        toDatePicker.getModel().setYear(ldt.getYear());
        toDatePicker.getModel().setMonth(ldt.getMonth().getValue());
        toDatePicker.getModel().setDay(ldt.getDayOfMonth());
    }

    /**
     * Updates the Results label
     * @param ldt the date update the Result to
     */
    private void updateResultsLabel(LocalDateTime ldt)
    {
        DayOfWeek dayOfWeek = ldt.getDayOfWeek();
        Month month = ldt.getMonth();
        resultsLabel.setText(dayOfWeek + ", " + month + " " + ldt.getDayOfMonth() + ", " + ldt.getYear());
        LOGGER.info("result label updated with: {}", resultsLabel.getText());
    }

    /**
     * Returns the LocalDate currently set in the FromDate
     * @return the FromDate as a LocalDate
     */
    public LocalDate getTheDateFromTheFromDate()
    {
        int year = getTheYearFromTheFromDatePicker();
        int month = getTheMonthFromTheFromDatePicker()+1;
        int day = getTheDayOfTheMonthFromTheFromDatePicker();
        return LocalDate.of(year, month, day);
    }

    /**
     * Returns the LocalDate currently set in the ToDate
     * @return the ToDate as a LocalDate
     */
    public LocalDate getTheDateFromTheToDate()
    {
        int year = getTheYearFromTheToDatePicker();
        int month = getTheMonthFromTheToDatePicker()+1;
        int day = getTheDayOfTheMonthFromTheToDatePicker();
        return LocalDate.of(year, month, day);
    }

    /**
     * Updates the results of the difference between dates
     * when using DateOperation1
     */
    private void updateResultsTextBox()
    {
        int[] numberOfYearsMonthsWeeksAndDays = calculateDifferenceBetweenDates();
        String wordYear = YEAR.toLowerCase() + LOWER_CASE_S;
        String wordMonth = MONTH.toLowerCase() + LOWER_CASE_S;
        String wordWeek = WEEK.toLowerCase() + LOWER_CASE_S;
        String wordDay = DAY.toLowerCase() + LOWER_CASE_S;
        if (numberOfYearsMonthsWeeksAndDays[0] == 1 || numberOfYearsMonthsWeeksAndDays[0] == -1)
        { wordYear = YEAR.toLowerCase(); }
        if (numberOfYearsMonthsWeeksAndDays[1] == 1 || numberOfYearsMonthsWeeksAndDays[1] == -1)
        { wordMonth = MONTH.toLowerCase(); }
        if (numberOfYearsMonthsWeeksAndDays[2] == 1 || numberOfYearsMonthsWeeksAndDays[2] == -1)
        { wordWeek = WEEK.toLowerCase(); }
        if (numberOfYearsMonthsWeeksAndDays[3] == 1 || numberOfYearsMonthsWeeksAndDays[3] == -1)
        { wordDay = DAY.toLowerCase(); }
        LocalDate fromDate = getTheDateFromTheFromDate();
        LocalDate toDate = getTheDateFromTheToDate();
        if (fromDate.isBefore(toDate))
        {
            LOGGER.info("{} is before {}", fromDate, toDate);
            yearsDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[0] + SPACE + wordYear);
            monthsDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[1] + SPACE + wordMonth);
            weeksDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            daysDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[3] + SPACE + wordDay);
        }
        else if (fromDate.isAfter(toDate))
        {
            LOGGER.info("{} is after {}", fromDate, toDate);
            yearsDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[0]) + SPACE +  wordYear);
            monthsDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[1]) + SPACE +  wordMonth);
            weeksDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            daysDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[3]) + SPACE +  wordDay);
        }
        else
        {
            LOGGER.info("{} is the same as {}", fromDate, toDate);
            yearsDifferenceLabel.setText(SAME + SPACE + YEAR);
            monthsDifferenceLabel.setText(SAME + SPACE + MONTH);
            weeksDifferenceLabel.setText(SAME + SPACE + WEEK);
            daysDifferenceLabel.setText(SAME + SPACE + ADD_OR_SUB_RESULT);
        }
        LOGGER.info("Difference Results updated");
    }

    /**************** GETTERS ****************/
    //public GridBagLayout getDateLayout() { return dateLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public UtilCalendarModel getFromCalendar() { return fromCalendar; }
    public UtilCalendarModel getToCalendar() { return toCalendar; }
    public JDatePanelImpl getFromDatePanel() { return fromDatePanel; }
    public JDatePanelImpl getToDatePanel() { return toDatePanel; }
    public JDatePickerImpl getFromDatePicker() { return fromDatePicker; }
    public JDatePickerImpl getToDatePicker() { return toDatePicker; }
    public Calculator getCalculator() { return calculator; }
    public JComboBox<DateOperation> getDateOperationsDropdown() { return dateOperationsDropdown; }
    public JLabel getFromDateLabel() { return fromDateLabel; }
    public JLabel getToDateLabel() { return toDateLabel; }
    public JLabel getDifferenceLabel() { return differenceLabel; }
    public JLabel getDateLabel() { return dateLabel; }
    public JLabel getYearsLabel() { return yearsLabel; }
    public JLabel getMonthLabel() { return monthLabel; }
    public JLabel getWeeksLabel() { return weeksLabel; }
    public JLabel getDaysLabel() { return daysLabel; }
    public JLabel getResultsLabel() { return resultsLabel; }
    public JLabel getYearsDifferenceLabel() { return yearsDifferenceLabel; }
    public JLabel getMonthsDifferenceLabel() { return monthsDifferenceLabel; }
    public JLabel getWeeksDifferenceLabel() { return weeksDifferenceLabel; }
    public JLabel getDaysDifferenceLabel() { return daysDifferenceLabel; }
    public JLabel getBlankLabel1() { return blankLabel1; }
    public JLabel getBlankLabel2() { return blankLabel2; }
    public JLabel getBlankLabel3() { return blankLabel3; }
    public JLabel getBlankLabel4() { return blankLabel4; }
    public JLabel getBlankLabel5() { return blankLabel5; }
    public ButtonGroup getButtonGroup() { return buttonGroup; }
    public JPanel getButtonGroupPanel() { return buttonGroupPanel; }
    public JPanel getTextFieldsGroupPanel() { return textFieldsGroupPanel; }
    public JRadioButton getAddRadioButton() { return addRadioButton; }
    public JRadioButton getSubtractRadioButton() { return subtractRadioButton; }
    public JTextField getYearsTextField() { return yearsTextField; }
    public JTextField getMonthsTextField() { return monthsTextField; }
    public JTextField getWeeksTextField() { return weeksTextField; }
    public JTextField getDaysTextField() { return daysTextField; }
    public boolean isInitialized() { return isInitialized; }

    /**************** SETTERS ****************/
    public void setCalculator(Calculator calculator) { this.calculator = calculator; LOGGER.debug("Calculator set"); }
    private void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; LOGGER.debug("Constraints set"); }
    public void setFromCalendar(UtilCalendarModel fromCalendar) { this.fromCalendar = fromCalendar; LOGGER.debug("From Calendar set"); }
    public void setToCalendar(UtilCalendarModel toCalendar) { this.toCalendar = toCalendar; LOGGER.debug("To Calendar set"); }
    public void setFromDatePanel(JDatePanelImpl fromDatePanel) { this.fromDatePanel = fromDatePanel; LOGGER.debug("From DatePanel set"); }
    public void setToDatePanel(JDatePanelImpl toDatePanel) { this.toDatePanel = toDatePanel; LOGGER.debug("To DatePanel set"); }
    public void setFromDatePicker(JDatePickerImpl fromDatePicker) { this.fromDatePicker = fromDatePicker; LOGGER.debug("From DatePicker set"); }
    public void setToDatePicker(JDatePickerImpl toDatePicker) { this.toDatePicker = toDatePicker; LOGGER.debug("To DatePicker set"); }
    private void setDateOperationsDropdown(JComboBox<DateOperation> dateOperationsDropdown) { this.dateOperationsDropdown = dateOperationsDropdown; LOGGER.debug("DateOperations Dropdown set"); }
    private void setYearsDifferenceLabel(JLabel yearsDifferenceLabel) { this.yearsDifferenceLabel = yearsDifferenceLabel; LOGGER.debug("Years Difference Label set"); }
    private void setMonthsDifferenceLabel(JLabel monthsDifferenceLabel) { this.monthsDifferenceLabel = monthsDifferenceLabel; LOGGER.debug("Months Difference Label set"); }
    private void setWeeksDifferenceLabel(JLabel weeksDifferenceLabel) { this.weeksDifferenceLabel = weeksDifferenceLabel; LOGGER.debug("Weeks Difference Label set"); }
    private void setDaysDifferenceLabel(JLabel daysDifferenceLabel) { this.daysDifferenceLabel = daysDifferenceLabel; LOGGER.debug("Days Difference Label set"); }
    private void setFromDateLabel(JLabel fromDateLabel) { this.fromDateLabel = fromDateLabel; LOGGER.debug("From Date Label set"); }
    private void setToDateLabel(JLabel toDateLabel) { this.toDateLabel = toDateLabel; LOGGER.debug("To Date Label set"); }
    private void setDifferenceLabel(JLabel differenceLabel) { this.differenceLabel = differenceLabel; LOGGER.debug("Difference Label set"); }
    private void setBlankLabel1(JLabel blankLabel1) { this.blankLabel1 = blankLabel1; LOGGER.debug("Blank Label1 set"); }
    private void setBlankLabel2(JLabel blankLabel2) { this.blankLabel2 = blankLabel2; LOGGER.debug("Blank Label2 set"); }
    private void setBlankLabel3(JLabel blankLabel3) { this.blankLabel3 = blankLabel3; LOGGER.debug("Blank Label3 set"); }
    private void setBlankLabel4(JLabel blankLabel4) { this.blankLabel4 = blankLabel4; LOGGER.debug("Blank Label4 set"); }
    private void setBlankLabel5(JLabel blankLabel5) { this.blankLabel5 = blankLabel5; LOGGER.debug("Blank Label5 set"); }
    private void setButtonGroup(ButtonGroup buttonGroup) { this.buttonGroup = buttonGroup; LOGGER.debug("Button Group set"); }
    private void setAddRadioButton(JRadioButton addRadioButton) { this.addRadioButton = addRadioButton; LOGGER.debug("Add RadioButton set"); }
    private void setSubtractRadioButton(JRadioButton subtractRadioButton) { this.subtractRadioButton = subtractRadioButton; LOGGER.debug("Subtract RadioButton set"); }
    private void setButtonGroupPanel(JPanel buttonGroupPanel) { this.buttonGroupPanel = buttonGroupPanel; LOGGER.debug("Button Group Panel set"); }
    private void setDateLabel(JLabel dateLabel) { this.dateLabel = dateLabel; LOGGER.debug("Date Label set"); }
    private void setYearsLabel(JLabel yearsLabel) { this.yearsLabel = yearsLabel; LOGGER.debug("Years Label set"); }
    private void setMonthLabel(JLabel monthLabel) { this.monthLabel = monthLabel; LOGGER.debug("Month Label set"); }
    private void setWeeksLabel(JLabel weeksLabel) { this.weeksLabel = weeksLabel; LOGGER.debug("Weeks Label set"); }
    private void setDaysLabel(JLabel daysLabel) { this.daysLabel = daysLabel; LOGGER.debug("Days Label set"); }
    private void setTextFieldsGroupPanel(JPanel textFieldsGroupPanel) { this.textFieldsGroupPanel = textFieldsGroupPanel; LOGGER.debug("Text Fields Group Panel set"); }
    private void setYearsTextField(JTextField yearsTextField) { this.yearsTextField = yearsTextField; LOGGER.debug("Years TextField set"); }
    private void setMonthsTextField(JTextField monthsTextField) { this.monthsTextField = monthsTextField; LOGGER.debug("Months TextField set"); }
    private void setWeeksTextField(JTextField weeksTextField) { this.weeksTextField = weeksTextField; LOGGER.debug("Weeks TextField set"); }
    private void setDaysTextField(JTextField daysTextField) { this.daysTextField = daysTextField; LOGGER.debug("Days TextField set"); }
    private void setResultsLabel(JLabel resultsLabel) { this.resultsLabel = resultsLabel; LOGGER.debug("Results Label set"); }
}