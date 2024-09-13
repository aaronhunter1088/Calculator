package Panels;

import Calculators.Calculator;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static Types.DateOperation.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatePanelTest
{
    static { System.setProperty("appName", "DatePanelTest"); }
    private static Logger LOGGER;
    private static DatePanel datePanel;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll() throws Exception
    {
        Calculator calculator = new Calculator(ADD_SUBTRACT_DAYS);
        datePanel = (DatePanel) calculator.getCurrentPanel();
        LOGGER = LogManager.getLogger(DatePanelTest.class.getSimpleName());
    }

    @After
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
    }

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
        datePanel.setFromCalendarUtil(new UtilCalendarModel());
        datePanel.getFromCalendarUtil().setDate(startDate.getYear(), startDate.getMonth().getValue()-1, startDate.getDayOfMonth());
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendarUtil()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));

        datePanel.getYearsTextField().setText("29");
        datePanel.getMonthsTextField().setText("2");
        datePanel.getDaysTextField().setText("21");

        datePanel.performSubtractRadioButtonFunctionality(actionEvent);
        datePanel.getCalculator().confirm("Pushed Subtract From Test");

        assertEquals(LocalDate.of(1992, Month.AUGUST, 29), datePanel.getTheDateFromTheToDate().minusMonths(1));

        // Now add the same values back and test that the same date is returned
        datePanel.performAddRadioButtonFunctionality(actionEvent);
        datePanel.getCalculator().confirm("Pushed Add From Test");

        assertEquals(LocalDate.of(2051, Month.FEBRUARY, 10), datePanel.getTheDateFromTheToDate().minusMonths(1));
    }

    @Test
    public void testAddRadioButtonWorksWithLocalDate()
    {
        when(actionEvent.getActionCommand()).thenReturn("Add");
        // LocalDate month value uses 0 based counting
        LocalDate myBirthday = LocalDate.of(1992, Month.AUGUST, 29);
        LOGGER.info("myBirthday: {}", myBirthday);
        datePanel.setFromCalendarUtil(new UtilCalendarModel());
        // REMEMBER, the date month uses 0 based counting
        datePanel.getFromCalendarUtil().setDate(myBirthday.getYear(), myBirthday.getMonth().getValue()-1, myBirthday.getDayOfMonth());
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendarUtil()));
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
        datePanel.setFromCalendarUtil(new UtilCalendarModel());
        datePanel.getFromCalendarUtil().setDate(year, month, day);
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendarUtil()));
        datePanel.setFromDatePicker(new JDatePickerImpl(datePanel.getFromDatePanel()));
        LocalDateTime todayLDT = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());

        // get values of from date, set to today's date
        datePanel.setToCalendarUtil(new UtilCalendarModel());
        datePanel.getToCalendarUtil().setDate(1992, 7, 29);
        datePanel.setToDatePanel(new JDatePanelImpl(datePanel.getToCalendarUtil()));
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
        datePanel.setToCalendarUtil(new UtilCalendarModel());
        datePanel.getToCalendarUtil().setDate(year, month, day);
        datePanel.setToDatePanel(new JDatePanelImpl(datePanel.getToCalendarUtil()));
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
        datePanel.setFromCalendarUtil(new UtilCalendarModel());
        datePanel.getFromCalendarUtil().setDate(1992, 7, 29);
        datePanel.setFromDatePanel(new JDatePanelImpl(datePanel.getFromCalendarUtil()));
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