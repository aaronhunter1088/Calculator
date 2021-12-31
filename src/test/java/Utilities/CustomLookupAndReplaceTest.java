package Utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CustomLookupAndReplaceTest
{
    static { System.setProperty("appName", "Calculator"); }

    @Test
    public void testLookupReturnsLogFileNameWhenGivenADate()
    {
        LocalDate todaysDate = LocalDate.now();
        String dateAsStr = "";
        String month = todaysDate.getMonthValue() < 10 ? "0"+todaysDate.getMonthValue() : Integer.toString(todaysDate.getMonthValue());
        String date = todaysDate.getDayOfMonth() < 10 ? "0"+todaysDate.getDayOfMonth() : Integer.toString(todaysDate.getDayOfMonth());
        dateAsStr = month + "-" + date + "-" + todaysDate.getYear();

        CustomLookupAndReplace replaceClass = new CustomLookupAndReplace();

        String result = replaceClass.lookup(dateAsStr);

        assertEquals(result, "logs/"+dateAsStr);
    }
}