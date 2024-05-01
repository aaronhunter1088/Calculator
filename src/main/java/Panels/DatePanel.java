package Panels;

import Calculators.Calculator;
import Types.CalculatorType;
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
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static Calculators.Calculator.*;
import static Types.CalculatorType.*;
import static Types.DateOperation.*;

public class DatePanel extends JPanel
{
    static { System.setProperty("appName", "DatePanel"); }
    private static final Logger LOGGER = LogManager.getLogger(DatePanel.class.getSimpleName());
    private static final long serialVersionUID = 4L;

    private GridBagLayout dateLayout;
    private GridBagConstraints constraints;
    private UtilCalendarModel fromModel, toModel;
    private JDatePanelImpl fromDatePanel, toDatePanel;
    private JDatePickerImpl fromDatePicker, toDatePicker;
    private Calculator calculator;
    private JComboBox<DateOperation> optionsBox;
    private JLabel fromDateLabel, toDateLabel, differenceLabel, dateLabel,
                   yearsLabel, monthLabel, weeksLabel, daysLabel, resultsLabel,
                   yearsDifferenceLabel, monthsDifferenceLabel, weeksDifferenceLabel, daysDifferenceLabel;
    private JLabel blankLabel1, blankLabel2, blankLabel3, blankLabel4, blankLabel5;
    private ButtonGroup buttonGroup;
    private JPanel buttonGroupPanel, textFieldsGroupPanel;
    private JRadioButton addRadioButton, subtractRadioButton;
    private JTextField yearsTextField, monthsTextField, weeksTextField, daysTextField;
    private final String SPACE = " ";
    private final String SAME = "Same";
    private final String YEAR = "Year";
    private final String MONTH = "Month";
    private final String WEEK = "Week";
    private final String ADD_OR_SUB_RESULT = "Result";
    private final String DAY = "Day";
    private final String LOWER_CASE_S = "s";
    private final String EMPTY_STRING = "";
    public DateOperation dateOperation = DateOperation.DIFFERENCE_BETWEEN_DATES;

    /************* Constructors ******************/
    public DatePanel()
    { LOGGER.info("Date panel created"); }

    public DatePanel(Calculator calculator)
    { this(calculator, null); }

    /**
     * MAIN CONSTRUCTOR USED
     * @param calculator the Calculator to use
     * @param dateOperation the option to use
     */
    public DatePanel(Calculator calculator, DateOperation dateOperation)
    { setupDatePanel(calculator, dateOperation); }

    /************* Start of methods here ******************/
    private void setupDatePanel(Calculator calculator, DateOperation dateOperation)
    {
        setCalculator(calculator);
        setLayout(new GridBagLayout()); // set frame layout
        setConstraints(new GridBagConstraints()); // instantiate constraints
        setSize(new Dimension(100,400));
        setupHelpMenu();
        setupDatePanelComponents(dateOperation);
        addComponentsToPanel(dateOperation);
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished constructing Date panel");
    }

    private void setupHelpMenu()
    {
        LOGGER.info("Creating the view help menu for date panel");
        String helpString = "<html>How to use the " + ADD_OR_SUB_RESULT + " Calculator<br><br>" +
                "Difference Between Dates:<br>" +
                "Enter a date into either field, From or To.<br>" +
                "Only 1 date is required to change to show a difference.<br>" +
                "See the difference between the two dates below.<br><br>" +
                "Add or Subtract Days: <br>" +
                "Select a date from the drop down option.<br>" +
                "Enter values in the choices: Years, Months, or Days.<br>" +
                "Click Add or Subtract to execute that action using your values and date.</html>";
        // 4 menu options: loop through to find the Help option
        for(int i=0; i < calculator.getCalculatorMenuBar().getMenuCount(); i++)
        {
            JMenu menuOption = calculator.getCalculatorMenuBar().getMenu(i);
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
                viewHelpItem.setFont(mainFont);
                viewHelpItem.setName("View Help");
                viewHelpItem.addActionListener(action -> {
                    JLabel textLabel = new JLabel(helpString,
                            calculator.getBlankIcon(), SwingConstants.CENTER);
                    textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                    JPanel mainPanel = new JPanel();
                    mainPanel.add(textLabel);
                    JOptionPane.showMessageDialog(calculator,
                            mainPanel, "Viewing Help", JOptionPane.PLAIN_MESSAGE);
                });
                menuOption.add(viewHelpItem, 0);
            }
        }
        LOGGER.info("Finished creating the view help menu");
    }

    private void setupDatePanelComponents(DateOperation dateOperation)
    {
        calculator.setCalculatorType(DATE);
        calculator.setConverterType(null);
        setupOptionsSelection(dateOperation);
        setupFromDate();
        setupToDate();
        setupDifferenceLabel();
        String SAME_YEAR = SAME + SPACE + YEAR;
        setYearsDifferenceLabel(new JLabel(SAME_YEAR));
        String SAME_MONTH = SAME + SPACE + MONTH;
        setMonthsDifferenceLabel(new JLabel(SAME_MONTH));
        String SAME_WEEK = SAME + SPACE + WEEK;
        setWeeksDifferenceLabel(new JLabel(SAME_WEEK));
        String SAME_DAY = SAME + SPACE + DAY;
        setDaysDifferenceLabel(new JLabel(SAME_DAY));
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
        SwingUtilities.updateComponentTreeUI(this);
        LOGGER.info("Finished setting up date panel");
    }

    private void setupDifferenceLabel()
    {
        setDifferenceLabel(new JLabel("Difference"));
        differenceLabel.setFont(verdanaFontBold);
        differenceLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupBlankLabel1()
    {
        setBlankLabel1(new JLabel(SPACE));
        blankLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupBlankLabel2()
    {
        setBlankLabel2(new JLabel(SPACE));
        blankLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupBlankLabel3()
    {
        setBlankLabel3(new JLabel(SPACE));
        blankLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupBlankLabel4()
    {
        setBlankLabel4(new JLabel(SPACE));
        blankLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupBlankLabel5()
    {
        setBlankLabel5(new JLabel(SPACE));
        blankLabel5.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupAddRadioButton()
    {
        String ADD = "Add";
        setAddRadioButton(new JRadioButton(ADD));
        addRadioButton.setSelected(true);
        addRadioButton.setName(ADD);
        addRadioButton.addActionListener(this::performAddRadioButtonFunctionality);
    }

    private void setupSubtractRadioButton()
    {
        String SUBTRACT = "Subtract";
        setSubtractRadioButton(new JRadioButton(SUBTRACT));
        subtractRadioButton.setSelected(false);
        subtractRadioButton.setName(SUBTRACT);
        subtractRadioButton.addActionListener(this::performSubtractRadioButtonFunctionality);
    }

    private void setupButtonGroup()
    {
        setButtonGroup(new ButtonGroup());
        setButtonGroupPanel(new JPanel());
        buttonGroupPanel.setLayout(new GridLayout(1,2));
        buttonGroupPanel.add(addRadioButton);
        buttonGroupPanel.add(subtractRadioButton);
    }

    private void setupYearsLabel()
    {
        setYearsLabel(new JLabel(YEAR + LOWER_CASE_S));
        yearsLabel.setFont(verdanaFontBold);
        yearsLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupMonthsLabel()
    {
        setMonthLabel(new JLabel(MONTH + LOWER_CASE_S));
        monthLabel.setFont(verdanaFontBold);
        monthLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupWeeksLabel()
    {
        setWeeksLabel(new JLabel(WEEK + LOWER_CASE_S));
        weeksLabel.setFont(verdanaFontBold);
        weeksLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupDaysLabel()
    {
        setDaysLabel(new JLabel(DAY + LOWER_CASE_S));
        daysLabel.setFont(verdanaFontBold);
        daysLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupYearsTextField()
    {
        setYearsTextField(new JTextField(EMPTY_STRING, 5));
        yearsTextField.setEditable(true);
        yearsTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupMonthsTextField()
    {
        setMonthsTextField(new JTextField(EMPTY_STRING, 5));
        monthsTextField.setEditable(true);
        monthsTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupWeeksTextField()
    {
        setWeeksTextField(new JTextField(EMPTY_STRING, 5));
        weeksTextField.setEditable(true);
        weeksTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupDaysTextField()
    {
        setDaysTextField(new JTextField(EMPTY_STRING, 5));
        daysTextField.setEditable(true);
        daysTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

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

    private void setupAddOrSubtractResultLabel()
    {
        setDateLabel(new JLabel(ADD_OR_SUB_RESULT));
        dateLabel.setFont(verdanaFontBold);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupResultsText()
    {
        LocalDate todaysDate = LocalDate.now();
        DayOfWeek dayOfWeek = todaysDate.getDayOfWeek();
        Month month = todaysDate.getMonth();
        setResultsLabel(new JLabel(dayOfWeek + ", " + month  + " " + todaysDate.getDayOfMonth() + ", " + todaysDate.getYear()));
        resultsLabel.setFont(mainFontBold);
        resultsLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setTheSelectedOption(DateOperation dateOperation)
    {
        if (dateOperation == null || dateOperation == DateOperation.DIFFERENCE_BETWEEN_DATES)
            setDateOperation(DateOperation.DIFFERENCE_BETWEEN_DATES);
        else setDateOperation(DateOperation.ADD_SUBTRACT_DAYS);
    }

    private void setupOptionsSelection(DateOperation dateOperation)
    {
        setOptionsBox(new JComboBox<>(new DateOperation[]{DIFFERENCE_BETWEEN_DATES, ADD_SUBTRACT_DAYS}));
        optionsBox.setSelectedItem(dateOperation == null ? DIFFERENCE_BETWEEN_DATES : dateOperation);
        setDateOperation(dateOperation);
        optionsBox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        optionsBox.addActionListener(this::performOptionsBoxFunctionality);
    }

    private void setupFromDate()
    {
        setFromDateLabel(new JLabel("From Date"));
        fromDateLabel.setFont(verdanaFontBold);
        fromDateLabel.setHorizontalAlignment(SwingConstants.LEFT);

        setFromModel(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        fromModel.setDate(year, monthInt, day);
        fromModel.setSelected(true);
        setFromDatePanel(new JDatePanelImpl(fromModel));
        setFromDatePicker(new JDatePickerImpl(fromDatePanel));
        fromDatePicker.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        fromDatePicker.addActionListener(this::performDatePickerFunctionality);
    }

    private void setupToDate()
    {
        String TO_DATE = "To Date";
        setToDateLabel(new JLabel(TO_DATE));
        toDateLabel.setFont(verdanaFontBold);
        toDateLabel.setHorizontalAlignment(SwingConstants.LEFT);

        setToModel(new UtilCalendarModel());
        LocalDate todaysDate = LocalDate.now();
        int year = todaysDate.getYear();
        int monthInt = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        toModel.setDate(year, monthInt, day); // defaults to today
        toModel.setSelected(true);
        setToDatePanel(new JDatePanelImpl(toModel));
        setToDatePicker(new JDatePickerImpl(toDatePanel));
        toDatePicker.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        toDatePicker.addActionListener(this::performDatePickerFunctionality); //action -> updateResultsTextBox());
    }

    public void performDatePickerFunctionality(ActionEvent ae)
    {
        if (Objects.requireNonNull(optionsBox.getSelectedItem()) == DIFFERENCE_BETWEEN_DATES )
        {
            updateResultsTextBox();
        }
        else if (optionsBox.getSelectedItem() == ADD_SUBTRACT_DAYS)
        {
            int year = getTheYearFromTheFromDatePicker();
            int month = getTheMonthFromTheFromDatePicker();
            int dayOfMonth = getTheDayOfTheMonthFromTheFromDatePicker();
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
            LOGGER.debug("new date selected: " + localDateTime.toLocalDate());
            updateResultsLabel(localDateTime);
        }
        LOGGER.info("Finished performing date picker logic");
        calculator.confirm("New date, FROM DATE chosen");
    }

    public int getTheYearFromTheFromDatePicker()
    { return fromDatePicker.getModel().getYear(); }

    public int getTheMonthFromTheFromDatePicker()
    { return fromDatePicker.getModel().getMonth(); }

    public int getTheWeekFromTheFromDatePicker()
    {
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

    public int getTheDayOfTheMonthFromTheFromDatePicker()
    { return fromDatePicker.getModel().getDay(); }

    public int getTheYearFromTheToDatePicker()
    { return toDatePicker.getModel().getYear(); }

    public int getTheMonthFromTheToDatePicker()
    { return toDatePicker.getModel().getMonth(); }

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

    public int getTheDayOfTheMonthFromTheToDatePicker()
    { return toDatePicker.getModel().getDay(); }

    private void updateThisPanel()
    {
        SwingUtilities.updateComponentTreeUI(this);
        calculator.pack();
    }

    public void switchComponentsForDateDifference()
    {
        remove(buttonGroupPanel);
        remove(blankLabel3);
        //remove(getLabelsGroupPanel());
        remove(textFieldsGroupPanel);
        remove(blankLabel4);
        remove(dateLabel);
        remove(resultsLabel);
        remove(blankLabel5);
        // update combobox
        //getOptionsBox().setSelectedIndex(0);
        // add appropriate components
        addComponent(toDateLabel, 5, 0, 1,1, 0,1.0);
        addComponent(toDatePicker, 6,0,1,1, 0,1.0);
        addComponent(blankLabel3, 7, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(differenceLabel, 8, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(yearsDifferenceLabel, 9, 0, 1, 1, 0,1.0);
        addComponent(monthsDifferenceLabel, 10, 0, 1, 1, 0,1.0);
        addComponent(weeksDifferenceLabel, 11, 0, 1, 1, 0, 1.0);
        addComponent(daysDifferenceLabel, 12, 0, 1, 1, 0,1.0);
        addComponent(blankLabel4, 13, 0, 1, 1,1.0,1.0); // representing a blank space
        updateResultsTextBox();
    }

    public void switchComponentsForAddSubDate()
    {
        remove(toDateLabel);
        remove(toDatePicker);
        remove(blankLabel3);
        remove(differenceLabel);
        remove(yearsDifferenceLabel);
        remove(monthsDifferenceLabel);
        remove(weeksDifferenceLabel);
        remove(daysDifferenceLabel);
        remove(blankLabel4);
        yearsTextField.setText("");
        monthsTextField.setText("");
        weeksTextField.setText("");
        daysTextField.setText("");
        addComponent(buttonGroupPanel, 5, 0, 1,1, 0,1.0);
        addComponent(blankLabel3, 6, 0, 1, 1,0, 1.0); // representing a blank space
        addComponent(textFieldsGroupPanel, 7, 0, 1, 1, 0, 1.0);
        addComponent(blankLabel4, 8, 0, 1, 1, 0.0, 1.0);
        addComponent(dateLabel, 9, 0, 1, 1, 0, 1.0);
        addComponent(resultsLabel, 10, 0, 1, 1, 0, 1.0);
        addComponent(blankLabel5, 11, 0, 1, 1, 1.0, 1.0);
    }

    private void performOptionsBoxFunctionality(ActionEvent actionEvent)
    {
        if ( optionsBox.getSelectedItem() != ADD_SUBTRACT_DAYS &&
            dateOperation != DIFFERENCE_BETWEEN_DATES)
        { // Only Switch to OPTIONS1 if and only if 2e are really showing OPTIONS2
            switchComponentsForDateDifference();
            setDateOperation(DateOperation.DIFFERENCE_BETWEEN_DATES);
            updateThisPanel();
            calculator.confirm("Changing to " + DIFFERENCE_BETWEEN_DATES);
        }
        else if (optionsBox.getSelectedItem() != DIFFERENCE_BETWEEN_DATES &&
                 dateOperation != ADD_SUBTRACT_DAYS)
        { // Only Switch to OPTIONS2 if and only if we are really showing OPTIONS1
            switchComponentsForAddSubDate();
            setDateOperation(DateOperation.ADD_SUBTRACT_DAYS);
            updateThisPanel();
            calculator.confirm("Changing to " + ADD_SUBTRACT_DAYS);
        }
        else
        { calculator.confirm("Options not changed"); }
    }

    public void performRadioButtonFunctionality(ActionEvent actionEvent)
    {
        int year = fromDatePicker.getModel().getYear();
        int month = fromDatePicker.getModel().getMonth();
        int dayOfMonth = fromDatePicker.getModel().getDay();
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
        LOGGER.debug("FromDate is {}", localDateTime.toLocalDate());

        int years = Integer.parseInt(StringUtils.isBlank(yearsTextField.getText()) ? "0" : yearsTextField.getText());
        int months = Integer.parseInt(StringUtils.isEmpty(monthsTextField.getText()) ? "0" : monthsTextField.getText());
        int weeks = Integer.parseInt(StringUtils.isEmpty(weeksTextField.getText()) ? "0" : weeksTextField.getText());
        int days = Integer.parseInt(StringUtils.isEmpty(daysTextField.getText()) ? "0" : daysTextField.getText());

        LOGGER.info("Years is {}", years);
        LOGGER.info("Months is {}", months);
        LOGGER.info("Weeks is {}", weeks);
        LOGGER.info("Days is {}", days);

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
        //updateFromDate(localDateTime);
        updateToDate(localDateTime);
        updateResultsLabel(localDateTime);
    }

    public void performAddRadioButtonFunctionality(ActionEvent actionEvent)
    {
        addRadioButton.setSelected(true);
        subtractRadioButton.setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    public void performSubtractRadioButtonFunctionality(ActionEvent actionEvent)
    {
        subtractRadioButton.setSelected(true);
        addRadioButton.setSelected(false);
        performRadioButtonFunctionality(actionEvent);
    }

    private void addComponentsToPanel(DateOperation dateOperation)
    {
        addComponent(optionsBox, 0,0,1,1, 0,1.0);
        addComponent(blankLabel1, 1, 0, 1, 1,0,1.0); // representing a blank space
        addComponent(fromDateLabel, 2, 0, 1,1, 0,1.0);
        addComponent(fromDatePicker, 3,0,1,1, 0,1.0);
        addComponent(blankLabel2, 4, 0, 1, 1,0,1.0); // representing a blank space
        if (dateOperation == DateOperation.DIFFERENCE_BETWEEN_DATES)
        {
            addComponent(toDateLabel, 5, 0, 1,1, 0,1.0);
            addComponent(toDatePicker, 6,0,1,1, 0,1.0);
            addComponent(blankLabel3, 7, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(differenceLabel, 8, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(yearsDifferenceLabel, 9, 0, 1, 1, 0,1.0);
            addComponent(monthsDifferenceLabel, 10, 0, 1, 1, 0,1.0);
            addComponent(weeksDifferenceLabel, 11, 0, 1, 1, 0, 1.0);
            addComponent(daysDifferenceLabel, 12, 0, 1, 1, 0,1.0);
            addComponent(blankLabel4, 13, 0, 1, 1,1.0,1.0); // representing a blank space
            updateResultsTextBox();
        }
        else {
            addComponent(buttonGroupPanel, 5, 0, 1,1, 0,1.0);
            addComponent(blankLabel3, 6, 0, 1, 1,0, 1.0); // representing a blank space
            addComponent(textFieldsGroupPanel, 7, 0, 1, 1, 0, 1.0);
            addComponent(blankLabel4, 8, 0, 1, 1, 0.0, 1.0);
            addComponent(dateLabel, 9, 0, 1, 1, 0, 1.0);
            addComponent(resultsLabel, 10, 0, 1, 1, 0, 1.0);
            addComponent(blankLabel5, 11, 0, 1, 1, 1.0, 1.0);
        }
        LOGGER.info("Finished adding components to date panel");
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
        dateLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }

    public void performOptionsBoxFunctionality(String option) throws UnsupportedLookAndFeelException
    {
        if (option.equals(DIFFERENCE_BETWEEN_DATES.getName()))
        {
            LOGGER.debug("Difference between dates selected");
            //defaultOptionFromOptionsBox = OPTIONS1 + SPACE + SELECTED;
            // remove appropriate components first
            remove(buttonGroupPanel);
            remove(blankLabel3);
            //remove(getLabelsGroupPanel());
            remove(textFieldsGroupPanel);
            remove(blankLabel4);
            remove(dateLabel);
            remove(resultsLabel);
            remove(blankLabel5);
            // update combobox
            optionsBox.setSelectedIndex(0);
            // add appropriate components
            addComponent(toDateLabel, 5, 0, 1,1, 0,1.0);
            addComponent(toDatePicker, 6,0,1,1, 0,1.0);
            addComponent(blankLabel3, 7, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(differenceLabel, 8, 0, 1, 1,0,1.0); // representing a blank space
            addComponent(yearsDifferenceLabel, 9, 0, 1, 1, 0,1.0);
            addComponent(monthsDifferenceLabel, 10, 0, 1, 1, 0,1.0);
            addComponent(daysDifferenceLabel, 11, 0, 1, 1, 0,1.0);
            addComponent(blankLabel4, 12, 0, 1, 1,1.0,1.0); // representing a blank space
            updateResultsTextBox();
        }
        else if (option.equals(ADD_SUBTRACT_DAYS.getName()))
        {
            LOGGER.debug("Add or subtract days selected");
            //defaultOptionFromOptionsBox = OPTIONS2 + SPACE + SELECTED;
            // remove appropriate components first
            remove(toDateLabel);
            remove(toDatePicker);
            remove(blankLabel3);
            remove(differenceLabel);
            remove(yearsDifferenceLabel);
            remove(monthsDifferenceLabel);
            remove(daysDifferenceLabel);
            remove(blankLabel4);
            // update combo box
            optionsBox.setSelectedIndex(1);
            // add appropriate components
            addComponent(buttonGroupPanel, 5, 0, 1,1, 0,1.0);
            addComponent(blankLabel3, 6, 0, 1, 1,0, 1.0); // representing a blank space
            addComponent(textFieldsGroupPanel, 7, 0, 1, 1, 0, 1.0);
            addComponent(blankLabel4, 8, 0, 1, 1, 0.0, 1.0);
            addComponent(dateLabel, 9, 0, 1, 1, 0, 1.0);
            addComponent(resultsLabel, 10, 0, 1, 1, 0, 1.0);
            addComponent(blankLabel5, 11, 0, 1, 1, 1.0, 1.0);
        }
        updateThisPanel();
    }

    /**
     * Calculates the difference between the two dates
     * and sets the years, months, weeks, and day differences
     * @return int[] storing the difference between the dates
     */
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
            LOGGER.info(fromDate + " is before " + toDate);
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
            LOGGER.info(fromDate + " is after " + toDate);
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
            LOGGER.info(fromDate + " is the same as " + toDate);
            //years = 0;
            //months = 0;
            //weeks = 0;
            //days = 0;
        }
        numberOfYearsMonthsWeeksAndDays[0] = years;
        numberOfYearsMonthsWeeksAndDays[1] = months;
        numberOfYearsMonthsWeeksAndDays[2] = weeks;
        numberOfYearsMonthsWeeksAndDays[3] = days;
        LOGGER.debug("Years: " + numberOfYearsMonthsWeeksAndDays[0]);
        LOGGER.debug("Months: " + numberOfYearsMonthsWeeksAndDays[1]);
        LOGGER.debug("Weeks: " + numberOfYearsMonthsWeeksAndDays[2]);
        LOGGER.debug("Days: " + numberOfYearsMonthsWeeksAndDays[3]);
        return numberOfYearsMonthsWeeksAndDays;
    }

    private void updateFromDate(LocalDateTime ldt)
    {
        fromDatePicker.getModel().setYear(ldt.getYear());
        fromDatePicker.getModel().setMonth(ldt.getMonth().getValue());
        fromDatePicker.getModel().setDay(ldt.getDayOfMonth());
    }

    private void updateToDate(LocalDateTime ldt)
    {
        toDatePicker.getModel().setYear(ldt.getYear());
        toDatePicker.getModel().setMonth(ldt.getMonth().getValue());
        toDatePicker.getModel().setDay(ldt.getDayOfMonth());
    }

    private void updateResultsLabel(LocalDateTime ldt)
    {
        DayOfWeek dayOfWeek = ldt.getDayOfWeek();
        Month month = ldt.getMonth();
        resultsLabel.setText(dayOfWeek + ", " + month + " " + ldt.getDayOfMonth() + ", " + ldt.getYear());
        LOGGER.info("result label updated with: " + resultsLabel.getText());
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

    private void updateResultsTextBox()
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
            LOGGER.info(fromDate + " is before " + toDate);
            //numberOfYearsMonthsWeeksAndDays[2] = (int)ChronoUnit.WEEKS.between(fromDate, toDate); //TechTrip - ASSUMPTION fromDate is earlier than toDate
            yearsDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[0] + SPACE + wordYear);
            monthsDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[1] + SPACE + wordMonth);
            weeksDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            daysDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[3] + SPACE + wordDay);
        }
        else if(fromDate.isAfter(toDate))
        {
            LOGGER.info(fromDate + " is after " + toDate);
            //numberOfYearsMonthsWeeksAndDays[2] = (int)ChronoUnit.WEEKS.between(toDate, fromDate); //TechTrip - ASSUMPTION toDate is earlier than fromDate
            yearsDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[0]) + SPACE +  wordYear);
            monthsDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[1]) + SPACE +  wordMonth);
            weeksDifferenceLabel.setText(numberOfYearsMonthsWeeksAndDays[2] + SPACE + wordWeek);
            daysDifferenceLabel.setText((numberOfYearsMonthsWeeksAndDays[3]) + SPACE +  wordDay);
        }
        else
        {
            LOGGER.info(fromDate + " is the same as " + toDate);
            yearsDifferenceLabel.setText(SAME + SPACE + YEAR);
            monthsDifferenceLabel.setText(SAME + SPACE + MONTH);
            weeksDifferenceLabel.setText(SAME + SPACE + WEEK);
            daysDifferenceLabel.setText(SAME + SPACE + ADD_OR_SUB_RESULT);
        }
        LOGGER.info("Difference Results updated");
    }

    public void performDateCalculatorTypeSwitchOperations(Calculator calculator, DateOperation dateOperation)
    {
        LOGGER.info("Performing tasks associated to switching to the Date panel");
        setupDatePanel(calculator, dateOperation);
        LOGGER.info("Finished tasks associated to switching to the Date panel");
    }

    /************* All Getters ******************/
    public GridBagLayout getDateLayout() { return dateLayout; }
    public GridBagConstraints getConstraints() { return constraints; }
    public UtilCalendarModel getFromModel() { return fromModel; }
    public UtilCalendarModel getToModel() { return toModel; }
    public JDatePanelImpl getFromDatePanel() { return fromDatePanel; }
    public JDatePanelImpl getToDatePanel() { return toDatePanel; }
    public JDatePickerImpl getFromDatePicker() { return fromDatePicker; }
    public JDatePickerImpl getToDatePicker() { return toDatePicker; }
    public Calculator getCalculator() { return calculator; }
    public JComboBox<DateOperation> getOptionsBox() { return optionsBox; }
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
    public String getSPACE() { return SPACE; }
    public String getSAME() { return SAME; }
    public String getYEAR() { return YEAR; }
    public String getMONTH() { return MONTH; }
    public String getWEEK() { return WEEK; }
    public String getDATE_() { return ADD_OR_SUB_RESULT; }
    public String getDAY() { return DAY; }
    public String getLOWER_CASE_S() { return LOWER_CASE_S; }
    public String getEMPTY_STRING() { return EMPTY_STRING; }
    public DateOperation getDateOperation() { return dateOperation; }

    /************* All Setters ******************/
    public void setLayout(GridBagLayout dateLayout) {
        super.setLayout(dateLayout);
        this.dateLayout = dateLayout;
    }
    public void setCalculator(Calculator calculator) { this.calculator = calculator; }
    private void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setFromModel(UtilCalendarModel fromModel) { this.fromModel = fromModel; }
    public void setToModel(UtilCalendarModel toModel) { this.toModel = toModel; }
    public void setFromDatePanel(JDatePanelImpl fromDatePanel) { this.fromDatePanel = fromDatePanel; }
    public void setToDatePanel(JDatePanelImpl toDatePanel) { this.toDatePanel = toDatePanel; }
    public void setFromDatePicker(JDatePickerImpl fromDatePicker) { this.fromDatePicker = fromDatePicker; }
    public void setToDatePicker(JDatePickerImpl toDatePicker) { this.toDatePicker = toDatePicker; }
    private void setOptionsBox(JComboBox<DateOperation> optionsBox) { this.optionsBox = optionsBox; }
    private void setYearsDifferenceLabel(JLabel yearsDifferenceLabel) { this.yearsDifferenceLabel = yearsDifferenceLabel; }
    private void setMonthsDifferenceLabel(JLabel monthsDifferenceLabel) { this.monthsDifferenceLabel = monthsDifferenceLabel; }
    private void setWeeksDifferenceLabel(JLabel weeksDifferenceLabel) { this.weeksDifferenceLabel = weeksDifferenceLabel; }
    private void setDaysDifferenceLabel(JLabel daysDifferenceLabel) { this.daysDifferenceLabel = daysDifferenceLabel; }
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
    private void setDateOperation(DateOperation dateOperation) { this.dateOperation = dateOperation; }
}