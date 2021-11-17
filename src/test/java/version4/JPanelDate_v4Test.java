package version4;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;
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
import java.util.Locale;

import static org.junit.Assert.*;
import static version4.CalcType_v4.DATE;

@RunWith(MockitoJUnitRunner.class)
public class JPanelDate_v4Test {

    private StandardCalculator_v4 c;
    private JPanelDate_v4 testTheDatePanel;
    private String number;
    private boolean result;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelBasicMethodsTest");
    }

    @Before
    public void beforeEach() throws Exception
    {
        c = new StandardCalculator_v4(DATE);
        testTheDatePanel = new JPanelDate_v4(c);
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
    public void testSubtractRadioButtonWorksWithLocalDate()
    {
        LocalDate todaysDate = LocalDate.now();

        System.out.println("LocalDate before: " + todaysDate);

        todaysDate = todaysDate.minusYears(29);
        todaysDate = todaysDate.minusMonths(2);
        todaysDate = todaysDate.minusDays(18);

        System.out.println("LocalDate after: " + todaysDate);

        assertEquals(LocalDate.of(1992, 8, 29), todaysDate);
    }

    @Test
    public void testMethodCalculateDifferenceBetweenDates()
    {
        testTheDatePanel.getCalculator().setCurrentPanel(testTheDatePanel);
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue()-1;
        int day = LocalDate.now().getDayOfMonth();
        testTheDatePanel.setFromModel(new UtilCalendarModel());
        testTheDatePanel.getFromModel().setDate(year, month, day);
        testTheDatePanel.setFromDatePanel(new JDatePanelImpl(testTheDatePanel.getFromModel()));
        testTheDatePanel.setFromDatePicker(new JDatePickerImpl(testTheDatePanel.getFromDatePanel()));
        //testTheDatePanel.getFromDatePicker().getModel().setDate(year, month, day);

        // get values of from date, set to today's date
        testTheDatePanel.setToModel(new UtilCalendarModel());
        testTheDatePanel.getToModel().setDate(1992, 7, 29);
        testTheDatePanel.setToDatePanel(new JDatePanelImpl(testTheDatePanel.getToModel()));
        testTheDatePanel.setToDatePicker(new JDatePickerImpl(testTheDatePanel.getToDatePanel()));
        //testTheDatePanel.getToDatePicker().getModel().setDate(1992, 7, 29);

        year = testTheDatePanel.getTheYearFromTheFromDatePicker();
        month = testTheDatePanel.getTheMonthFromTheFromDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheFromDatePicker();
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        String monthAsStr = localDateTime.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("fromDate: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        // get values of to date, set to my birthday
        year = testTheDatePanel.getTheYearFromTheToDatePicker();
        month = testTheDatePanel.getTheMonthFromTheToDatePicker();
        day = testTheDatePanel.getTheDayOfTheMonthFromTheToDatePicker();
        localDateTime = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        monthAsStr = localDateTime.getMonth().toString().toLowerCase(Locale.ROOT);
        System.out.println("birthDate: " + LocalDate.of(year, month, day));
        System.out.println("month: " + monthAsStr);

        testTheDatePanel.performDatePickerFunctionality(ae);
    }
}