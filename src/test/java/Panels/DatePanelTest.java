package Panels;

import Calculators.Calculator;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
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

@RunWith(MockitoJUnitRunner.class)
public class DatePanelTest
{
    static { System.setProperty("appName", "DatePanelTest"); }
    private static DatePanel testTheDatePanel;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void setup() throws Exception {
        Calculator calculator = new Calculator(ADD_SUBTRACT_DAYS);
        testTheDatePanel = (DatePanel) calculator.getCurrentPanel();
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
        LocalDate startingFromDate = LocalDate.of(2021, Month.NOVEMBER, 19);

        int year = startingFromDate.getYear();
        int month = startingFromDate.getMonth().getValue();
        int day = startingFromDate.getDayOfMonth();
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(year, month, day);
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));

        testTheDatePanel.getYearsTextField().setText("29");
        testTheDatePanel.getMonthsTextField().setText("2");
        testTheDatePanel.getDaysTextField().setText("21");

        testTheDatePanel.performSubtractRadioButtonFunctionality(actionEvent);
        testTheDatePanel.getCalculator().confirm("Pushed Subtract From Test");

        assertEquals(LocalDate.of(1992, Month.AUGUST, 29), testTheDatePanel.getTheDateFromTheToDate());

        // Now add the same values back and test that the same date is returned
        testTheDatePanel.performAddRadioButtonFunctionality(actionEvent);
        testTheDatePanel.getCalculator().confirm("Pushed Add From Test");

        assertEquals(LocalDate.of(2051, Month.FEBRUARY, 10), testTheDatePanel.getTheDateFromTheToDate());
    }

    @Test
    public void testAddRadioButtonWorksWithLocalDate()
    {
        // LocalDate month value uses 0 based counting
        LocalDate myBirthday = LocalDate.of(1992, 7, 29);
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(myBirthday.getYear(), myBirthday.getMonthValue(), myBirthday.getDayOfMonth());
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));

        testTheDatePanel.getYearsTextField().setText("29");
        testTheDatePanel.getMonthsTextField().setText("2");
        testTheDatePanel.getDaysTextField().setText("21");

        System.out.println("pushing add radio button");
        testTheDatePanel.performAddRadioButtonFunctionality(actionEvent);
        testTheDatePanel.getCalculator().confirm("Pushed Add From Test");

        int year = testTheDatePanel.getToDatePicker().getModel().getYear();
        int month = testTheDatePanel.getToDatePicker().getModel().getMonth()+1;
        int day = testTheDatePanel.getToDatePicker().getModel().getDay();
        myBirthday = LocalDate.of(year, month, day);
        assertEquals(LocalDate.of(2021, 11, 19), myBirthday);
    }

    @Test
    public void testMethodCalculateDifferenceBetweenDates()
    {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue()-1;
        int day = LocalDate.now().getDayOfMonth();
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(year, month, day);
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));
        LocalDateTime todayLDT = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());

        // get values of from date, set to today's date
        testTheDatePanel.setToModel(new UtilCalendarModel());
        testTheDatePanel.getToModel().setDate(1992, 7, 29);
        testTheDatePanel.setToDatePanel(new JDatePanelImpl(testTheDatePanel.getToModel()));
        testTheDatePanel.setToDatePicker(new JDatePickerImpl(testTheDatePanel.getToDatePanel()));
        //testTheDatePanel.getToDatePicker().getModel().setDate(1992, 7, 29);

        year = testTheDatePanel.getTheYearFromTheFromDatePicker();
        month = testTheDatePanel.getTheMonthFromTheFromDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheFromDatePicker();

        String monthAsStr = todayLDT.getMonth().toString().toLowerCase(Locale.ROOT);
        //System.out.println("fromDate: " + LocalDate.of(year, month, day));
        //System.out.println("month: " + monthAsStr);

        // get values of to date, set to my birthday
        year = testTheDatePanel.getTheYearFromTheToDatePicker();
        month = testTheDatePanel.getTheMonthFromTheToDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheToDatePicker();
        LocalDateTime birthDTL = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        monthAsStr = birthDTL.getMonth().toString().toLowerCase(Locale.ROOT);
        //System.out.println("birthDate: " + LocalDate.of(year, month, day));
        //System.out.println("month: " + monthAsStr);

        testTheDatePanel.performDatePickerFunctionality(actionEvent);
    }

    @Test
    public void testSwitchDatesAndGetSameDifferenceAsTestDirectlyAboveThisOne()
    {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue()-1;
        int day = LocalDate.now().getDayOfMonth();
        testTheDatePanel.setToModel(new UtilCalendarModel());
        testTheDatePanel.getToModel().setDate(year, month, day);
        testTheDatePanel.setToDatePanel(new JDatePanelImpl(testTheDatePanel.getToModel()));
        testTheDatePanel.setToDatePicker(new JDatePickerImpl(testTheDatePanel.getToDatePanel()));
        LocalDateTime todayLDT = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        // get values of to date, set to today's date
        year = testTheDatePanel.getTheYearFromTheToDatePicker();
        month = testTheDatePanel.getTheMonthFromTheToDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheToDatePicker();
        String monthAsStr = todayLDT.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("today's date: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        // get values of from date, set to my birthday
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(1992, 7, 29);
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));
        // get values of to date, set to my birthday
        year = testTheDatePanel.getTheYearFromTheFromDatePicker();
        month = testTheDatePanel.getTheMonthFromTheFromDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDateTime birthDTL = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        monthAsStr = birthDTL.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("birthDate: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        testTheDatePanel.performDatePickerFunctionality(actionEvent);
    }
}