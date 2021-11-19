package version4;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static version4.CalcType_v4.DATE;
import static version4.JPanelDate_v4.OPTIONS1;
import static version4.JPanelDate_v4.OPTIONS2;

@RunWith(MockitoJUnitRunner.class)
public class JPanelDate_v4Test {

    private static StandardCalculator_v4 c;
    private static JPanelDate_v4 testTheDatePanel;

    @Mock
    ActionEvent ae = mock(ActionEvent.class);

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelDateTest");
        c = new StandardCalculator_v4(DATE, OPTIONS2);
        testTheDatePanel = (JPanelDate_v4) c.getCurrentPanel();
    }

    @After
    public void beforeEach() throws Exception
    {
        //testTheDatePanel.reset(JPanelDate_v4.OPTIONS2);
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
        LocalDate todaysDate = LocalDate.now();

        System.out.println("setting the fromDate to: " + todaysDate);

        int year = todaysDate.getYear();
        int month = todaysDate.getMonthValue()-1;
        int day = todaysDate.getDayOfMonth();
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(year, month, day);
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));

        testTheDatePanel.getYearsTextField().setText("29");
        testTheDatePanel.getMonthsTextField().setText("2");
        testTheDatePanel.getDaysTextField().setText("21"); // as of 11/19/2021

        System.out.println("pushing subtract radio button");
        testTheDatePanel.performSubtractRadioButtonFunctionality(ae);
        testTheDatePanel.getCalculator().confirm("Pushed Subtract From Test");

        testTheDatePanel.updateThisPanel();

        year = testTheDatePanel.getFromDatePicker().getModel().getYear();
        month = testTheDatePanel.getFromDatePicker().getModel().getMonth()+1;
        day = testTheDatePanel.getFromDatePicker().getModel().getDay();
        todaysDate = LocalDate.of(year, month, day);
        assertEquals(LocalDate.of(1992, 8, 29), todaysDate);

        // Now add the same values back and test that the same date is returned
        testTheDatePanel.performAddRadioButtonFunctionality(ae);
        testTheDatePanel.getCalculator().confirm("Pushed Add From Test");

        year = testTheDatePanel.getFromDatePicker().getModel().getYear();
        month = testTheDatePanel.getFromDatePicker().getModel().getMonth()+1;
        day = testTheDatePanel.getFromDatePicker().getModel().getDay();
        todaysDate = LocalDate.of(year, month, day);
        assertEquals(LocalDate.of(2021, 11, 19), todaysDate);
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
        testTheDatePanel.performAddRadioButtonFunctionality(ae);
        testTheDatePanel.getCalculator().confirm("Pushed Add From Test");

        int year = testTheDatePanel.getFromDatePicker().getModel().getYear();
        int month = testTheDatePanel.getFromDatePicker().getModel().getMonth()+1;
        int day = testTheDatePanel.getFromDatePicker().getModel().getDay();
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

        testTheDatePanel.performDatePickerFunctionality(ae);
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

        testTheDatePanel.performDatePickerFunctionality(ae);
    }
}