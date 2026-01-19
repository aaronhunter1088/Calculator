package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;

import static Types.DateOperation.ADD_SUBTRACT_DAYS;
import static Utilities.LoggingUtil.confirm;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * DatePanelTest
 * <p>
 * This class tests the {@link DatePanel} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
class DatePanelTest
{
    static { System.setProperty("appName", DatePanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(DatePanelTest.class.getSimpleName());

    private Calculator calculator;
    private DatePanel datePanel;

    @Mock
    ActionEvent actionEvent;

    @BeforeAll
    static void beforeAll()
    {}

    @BeforeEach
    void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        calculator = new Calculator(ADD_SUBTRACT_DAYS);
        calculator.setVisible(true);
        datePanel = calculator.getDatePanel();
        datePanel.setupDatePanel(calculator, ADD_SUBTRACT_DAYS);
        calculator.setCurrentPanel(datePanel);
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        for (Calculator calculator : java.util.List.of(calculator))
        {
            LOGGER.info("Test complete. Closing the calculator...");
            EventQueue.invokeAndWait(() -> {
                calculator.setVisible(false);
                calculator.dispose();
            });
            assertFalse(calculator.isVisible());
        }
    }

    @AfterAll
    static void afterAll()
    { LOGGER.info("Finished running {}", DatePanelTest.class.getSimpleName()); }

    @Test
    public void testSubtractRadioButtonWorks()
    {
        LocalDate birthDate = LocalDate.parse("1992-08-29");

        System.out.println("LocalDate before: " + birthDate);

        birthDate = birthDate.plusDays(18);
        birthDate = birthDate.plusMonths(2);
        birthDate = birthDate.plusYears(29);

        System.out.println("LocalDate after: " + birthDate);

        assertEquals(LocalDate.of(2021, 11, 16), birthDate);
    }

    @Test
    public void testSubtractThenAddBackButtonWorksWithLocalDate()
    {
        LocalDate startDate = LocalDate.of(2021, Month.NOVEMBER, 19);
        datePanel.setFromCalendar(new UtilCalendarModel());
        datePanel.getFromCalendar().setDate(startDate.getYear(), startDate.getMonth().getValue()-1, startDate.getDayOfMonth());
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendar()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));

        datePanel.getYearsTextField().setText("29");
        datePanel.getMonthsTextField().setText("2");
        datePanel.getDaysTextField().setText("21");

        datePanel.performSubtractRadioButtonFunctionality(actionEvent);
        confirm(calculator, LOGGER, "Pushed Subtract From Test");

        assertEquals(LocalDate.of(1992, Month.AUGUST, 29), datePanel.getTheDateFromTheToDate().minusMonths(1));

        // Now add the same values back and test that the same date is returned
        datePanel.performAddRadioButtonFunctionality(actionEvent);
        confirm(calculator, LOGGER, "Pushed Subtract From Test");

        assertEquals(LocalDate.of(2051, Month.FEBRUARY, 10), datePanel.getTheDateFromTheToDate().minusMonths(1));
    }

    @Test
    public void testAddRadioButtonWorksWithLocalDate()
    {
        when(actionEvent.getActionCommand()).thenReturn("Add");
        // LocalDate month value uses 0 based counting
        LocalDate myBirthday = LocalDate.of(1992, Month.AUGUST, 29);
        LOGGER.info("myBirthday: {}", myBirthday);
        datePanel.setFromCalendar(new UtilCalendarModel());
        // REMEMBER, the date month uses 0 based counting
        datePanel.getFromCalendar().setDate(myBirthday.getYear(), myBirthday.getMonth().getValue()-1, myBirthday.getDayOfMonth());
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendar()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));

        datePanel.getYearsTextField().setText("29");
        datePanel.getMonthsTextField().setText("2");
        datePanel.getDaysTextField().setText("21");

        LOGGER.info("pushing add radio button");
        datePanel.performAddRadioButtonFunctionality(actionEvent);

        //int year = testTheDatePanel.getToDatePicker().getModel().getYear();
        //int month = testTheDatePanel.getToDatePicker().getModel().getMonth()+1;
        //int day = testTheDatePanel.getToDatePicker().getModel().getDay();
        myBirthday = datePanel.getTheDateFromTheToDate().minusMonths(1);
        LOGGER.info("myBirthday: {}", myBirthday);
        assertEquals(LocalDate.of(2021, Month.NOVEMBER, 19), myBirthday);
    }

    @Test
    public void testMethodCalculateDifferenceBetweenDates()
    {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue()-1;
        int day = LocalDate.now().getDayOfMonth();
        datePanel.setFromCalendar(new UtilCalendarModel());
        datePanel.getFromCalendar().setDate(year, month, day);
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendar()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));
        LocalDateTime todayLDT = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());

        // get values of from date, set to today's date
        datePanel.setToCalendar(new UtilCalendarModel());
        datePanel.getToCalendar().setDate(1992, 7, 29);
        datePanel.setToDatePanel(new JDatePanelImpl(datePanel.getToCalendar()));
        datePanel.setToDatePicker(new JDatePickerImpl(datePanel.getToDatePanel()));
        //testTheDatePanel.getToDatePicker().getModel().setDate(1992, 7, 29);

        year = datePanel.getTheYearFromTheFromDatePicker();
        month = datePanel.getTheMonthFromTheFromDatePicker();
        day = datePanel.getTheDayOfTheMonthFromTheFromDatePicker();

        String monthAsStr = todayLDT.getMonth().toString().toLowerCase(Locale.ROOT);
        //System.out.println("fromDate: " + LocalDate.of(year, month, day));
        //System.out.println("month: " + monthAsStr);

        // get values of to date, set to my birthday
        year = datePanel.getTheYearFromTheToDatePicker();
        month = datePanel.getTheMonthFromTheToDatePicker();
        day = datePanel.getTheDayOfTheMonthFromTheToDatePicker();
        LocalDateTime birthDTL = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        monthAsStr = birthDTL.getMonth().toString().toLowerCase(Locale.ROOT);
        //System.out.println("birthDate: " + LocalDate.of(year, month, day));
        //System.out.println("month: " + monthAsStr);

        datePanel.performDatePickerFunctionality(actionEvent);
    }

    @Test
    public void testSwitchDatesAndGetSameDifferenceAsTestDirectlyAboveThisOne()
    {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue()-1;
        int day = LocalDate.now().getDayOfMonth();
        datePanel.setToCalendar(new UtilCalendarModel());
        datePanel.getToCalendar().setDate(year, month, day);
        datePanel.setToDatePanel(new JDatePanelImpl(datePanel.getToCalendar()));
        datePanel.setToDatePicker(new JDatePickerImpl(datePanel.getToDatePanel()));
        LocalDateTime todayLDT = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        // get values of to date, set to today's date
        year = datePanel.getTheYearFromTheToDatePicker();
        month = datePanel.getTheMonthFromTheToDatePicker();
        day = datePanel.getTheDayOfTheMonthFromTheToDatePicker();
        String monthAsStr = todayLDT.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("today's date: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        // get values of from date, set to my birthday
        datePanel.setFromCalendar(new UtilCalendarModel());
        datePanel.getFromCalendar().setDate(1992, 7, 29);
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendar()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));
        // get values of to date, set to my birthday
        year = datePanel.getTheYearFromTheFromDatePicker();
        month = datePanel.getTheMonthFromTheFromDatePicker();
        day = datePanel.getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDateTime birthDTL = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        monthAsStr = birthDTL.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("birthDate: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        datePanel.performDatePickerFunctionality(actionEvent);
    }
}