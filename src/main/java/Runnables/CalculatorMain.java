package Runnables;

import Calculators.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import static Types.CalculatorType.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;
import static Types.Texts.*;

/**
 * Main Class. Start here!
 */
public class CalculatorMain
{
    static { System.setProperty("appName", "Calculator"); }
    private final static Logger LOGGER = LogManager.getLogger(CalculatorMain.class.getSimpleName());

	public static void main(String[] args) throws Exception
    {
        LOGGER.info("Starting calculator...");
        String logLevel = System.getenv("logLevel");
        if (null == logLevel) {
            LOGGER.warn("Set env.logLevel. Using default:all");
        } else {
            LOGGER.info(System.getenv("logLevel"));
        }
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        SwingUtilities.invokeLater(() -> {
            try {
                //Start a basic calculator
                Calculator calculator = new Calculator(/*BASIC*/);
                //Start a programmer calculator in BINARY mode
                //Calculator calculator = new Calculator(PROGRAMMER /*, BINARY*/ );
                //Start a programmer calculator in DECIMAL mode
                //Calculator calculator = new Calculator(PROGRAMMER, DECIMAL);
                //Start a date calculator with options1 selected
                //Calculator calculator = new Calculator(DIFFERENCE_BETWEEN_DATES); //(DATE);
                //Start a date calculator with options2 selected
                //Calculator calculator = new Calculator(ADD_SUBTRACT_DAYS);
                //Start an ANGLE converter calculator
                //Calculator calculator = new Calculator(CONVERTER /*,ANGLE*/ );
                //Start an AREA converter calculator
                //Calculator calculator = new Calculator(AREA);
                //Display the window.
                calculator.pack();
                calculator.setVisible(true);
                calculator.confirm(calculator.getCalculatorType().getValue() + " Calculator started");
            }
            catch (Exception e) {
                System.err.printf("Could not create Calculator bc " + e.getMessage());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Closing Calculator")));
    }
}
/*
a. PROGRAMMER: Michael Ball
b. DATE:   Version 4: 2024
c. ENVIRONMENT: Mac Sonoma 14.4.1
d. FILES INCLUDED: The Java class files, 5 images, log4j2.properties, and pom.properties
e. PURPOSE: To recreate the Calculator app (from Windows) and provide more functionality
f. INPUT:  To calculate numbers, dates, units, and more
g. PRECONDITIONS: Basic view is the default view
h. OUTPUT: Displays calculated result from input number buttons
i. POST CONDITIONS: Most Booleans returned to initial state. Some stay switched on, like the decimal boolean,
                    until explicitly turned off.
j. ALGORITHM:
k. ERRORS: Errors are organized by Date and then by what the error was effecting.

o.  HISTORY (in descending order):
Version Four Started: August 14, 2021,
Version Four Concluded: -
            Monday, April 29, 2024:
            Wow, it's been quite some time. The reason I haven't been working on this for some
            time has been because life happens. But today and for the past few days, I have been working
            on fixing a few issues I've seen while playing with the Calculator in the recent past (from
            today's date). And there are still plenty of tests that can be written so I took the time
            to do a few things I've been wanting to do for some time.
            [a] First, I have rearranged the code so that specific logic happens in the panel that is
            ultimately performing the logic. I've also moved some attributes into the appropriate panel.
            [b] I have updated the logging files so that the main log will now be named Runnables.CalculatorMain,
            instead of just 'calculator', added a new separator, and added env.logLevel from run configurations.
            [c] I adjusted several accessors to use private where needed.
            [d] I rearranged the code in such a way to allow a natural pattern to be seen so reading any related
            classes are easier to find specific methods.
            [e] I've added JavaDocs to all methods to allow quick access to know what each method does.
            [f] Fix the 'textArea' to not rely on alignment to be Right_to_Left, and enforce right alignment
            (and renamed it to textPane)
            [g] Edited each panel's setup to display certain buttons on separate panels for better control
            [h] Upgraded Java to language level 18!
            [i] Added commas to longer numbers, displayed only in the textPane
            [j] Added a history panel to display a record of each button press and operation performed, if valid.
            [k] Added the ability to display all stored memories, displayed in the history panel.
            [l] Added keyboard inputs for all numbers, delete, clear, clear entry, equals, all main basic operators, and decimal.
            [m] Added mouse listener to regain focus on Calculator.

            Saturday, Aug 14, 2021:
            I started the Converter version. It should be relatively easy to implement seeing as they all have a
            common face. So build the face, allow for parameters and fill them in as a particular type is added.
---
Version Three Started: October 26, 2017,
Version Three Concluded: February 6, 2021
            October 26, 2017:
            Today, I begin version three. I will start by saying that the project is going to be completely
            redesigned. There will be more structure to this new version of the calculator. There will be a
            main Calculator class, containing certain fields and properties that all calculators have. Then
            subclasses will extend the Calculator class. This will allow certain things to be typed once.
            Calculator
            | Basic - The default calculator for simple calculations

            | Scientific - Added functions such as trigonometric functions TODO: Implement

            | Programmer - Added functions such as algebraic functions TODO: Complete

            | Date Calculation - Difference between dates, and Dates addition/subtraction

            | Converter (has more subclasses; may be on top tier of its own) TODO: Add other versions
              Currency | Volume | Length | Weight and Mass.....
---
Version Two Started: March 7, 2017,
Version Two Concluded: May 6, 2017
            *NOTE* There is no Jar for version2 because it was started using Swing GUI Designer in Netbeans
            And I did not find this to be any easier. I figured I'll just code it out myself which gives me
            more appreciation and understanding for what I am attempting to build. I'll give that tool another
            shot in the future.

            May 6, 2017,
            I found a slight variation in my calculator that is different from Microsoft's. I noticed
            that pushing the negate button, effecting a negative number, it was not returning it to a
            positive number. After looking at my code, I noticed I just was not allowing it to add another
            negative sign. So I changed my method to convert it back to positive. Done actually on May
            8, 2017, but I discovered the issue on the 6th.

            April 30, 2017,
            Today, I worked on the negate button. I was able to simplify the logic by so much.
            I reduced the negate button by 61 lines of code. This is due to the fact that my
            calculator was not doing the same thing as Microsoft's. So after checking and double-checking
            what Microsoft's calculator did, I changed my code to do the same. This
            helped majorly in reducing the code.

            April 29, 2017,
            Today I fixed a bug with the DEL button where when the input was something like 0.5,
            it was returning .0 instead of just 0. I fixed the issue and in the process made some
            additional methods.
            Today I also fixed a bug where the input was taking 0625 as a number instead of eliminating
            the unnecessary zero in the front.

            April 7, 2017,
            DEL button is not working properly. I thought I had fixed this issue. But I was way off. So
            today and on the 11, I made sure that the proper action was performed to make sure the number
            returned was appropriate.

            // add in logic to check to see if operator can be performed on a negative number.
            April 1, 2017,
            I am now trying to add logic into the operator buttons that would allow them to synchronize to the flow
            of a problem. For example, the user could do this: 5+5+5+5 and it would work and update with each
            push of an operator. Plus pushing any other operator would return the updated result and perform
            the operator after.
            // DEL doesn't erase operator if that is the last entry!

            March 26, 2017,
            I had an issue where the negate button broke. This likely happened when I was implementing some
            logic to not allow a button to be pushed when there is no number to associate the button with.
            I had one if statement the same as the initial. This was incorrect. It took about 10 minutes
            to solve.
            I fixed the memory add and memory subtraction button to work with memory by updating the result
            if one of those buttons is pushed.
            I also found a couple more spots where the decimal number was not converted properly. These
            spots were fixed accordingly.

            March 25, 2017,
            I continue to fix issues where a button should not perform any action if there is no number
            in the text area.

            March 23, 2017
            5 + negate + MS + 6 = Result: 5-6 Expected: 6 as temp[1]!

            Today I thought of an issue that I had not thought of yet and when I verified my idea, I was right.
            I had left out some logic that would only allow the operator button to happen once. After that I
            added it to the other operator buttons, and they all checked out. Now you cannot keep pressing an
            operator button. It only allows it once.
            Also, I figured out that memory recall did not display negative numbers properly. It was again displaying
            the negative sign at the end of the number and not the front. So I added in the code I already had to
            convert numbers to display properly and nested my existed logic together in an if else statement. Then
            almost immediately, I thought about what would happen with a decimal number. And it broke. This issue
            took a more thought to fix, but it only took about 15 minutes. I added some logic to print out the
            result if the number was a double in an else if (number.number < 0.0) but that did not work. So I
            tried putting it in the first condition as an or, but that also didn't work. So I changed the entire
            condition to just (number.number < 0.0) and it works for whole and decimal numbers.

            March 23, 2017
            5 + negate + MS + 6 = Result: 5-6 Expected: 6 as temp[1]!

            Today I thought of an issue that I had not thought of yet and when I verified my idea, I was right.
            I had left out some logic that would only allow the operator button to happen once. After that I
            added it to the other operator buttons, and they all checked out. Now you cannot keep pressing an
            operator button. It only allows it once.
            Also, I figured out that memory recall did not display negative numbers properly. It was again displaying
            the negative sign at the end of the number and not the front. So I added in the code I already had to
            convert numbers to display properly and nested my existed logic together in an if else statement. Then
            almost immediately, I thought about what would happen with a decimal number. And it broke. This issue
            took a more thought to fix, but it only took about 15 minutes. I added some logic to print out the
            result if the number was a double in an else if (number.number < 0.0) but that did not work. So I
            tried putting it in the first condition as an or, but that also didn't work. So I changed the entire
            condition to just (number.number < 0.0) and it works for whole and decimal numbers.

            March 21, 2017,
            Today I fixed the error where if you were to type into the calculator this problem: 5 + negate + percent,
            it would return: 0.05-. This is the correct answer, but the output is all wrong. So what I did was I took
            the code that changed the input to negative and reversed it to take the negative away. Then add it to the
            front and display, all while actually keeping the negative input. This took about 30 minutes to solve.

            I also fixed an error where the input could not hold onto decimal numbers longer than one in length! This
            was a clear issue and an unexpected one. But I nailed it down to the logic in the button handler. I needed
            to set the dotButtonPressed boolean to false in the else, else part. Once I did this, logic like 10.356 was
            possible (again)...

            March 18, 2017,
            Today I was about to start on the logic to allow the memory buttons to work. Before starting
            to write my logic, I first tested how the buttons work. During testing, I noticed that the simple
            operation of 85 + 5 was not setting the text area to 90. The storage area was receiving the proper
            result though. So when I looked at my last code change, I remembered that I added in additional logic
            so that if a user tries to divide by zero it will tell them they cannot instead of returning infinity.
            This required me to change when temp[2] actually receives the result. I simply forgot to add this.
            Then when I got the result it was displaying 90.0. Which is weird because I had written logic to keep
            it from doing so. I only added that logic in though with one of the operators in the equals button handler.
            So adding in that logic everywhere helped it display 90 finally.

            Again, got distracted by my memory buttons. I got into the copy/paste functionality and saw paste
            was not working. So I checked the logic there and noticed that there was no action when this was clicked.
            So I added some really fast. So, when copying, set temp[3] equal to the text area. When pasting, set
            the text area to whatever value is in temp[3]; But for some reason, temp[3] is null at this point.
            ... I paused here to go check again. Then I suddenly realized the issue. When I pressed clear,
            it was resetting all values for temp. I actually only wanted to reset input one, two and result.
            Fixing the for loop fixed this issue.

            March 15, 2017,
            See above documentation. As of this date, with the new string input logic implemented,
            I now appear to once again have 20/28 buttons working and functional. The implementation
            of first trying to distinguish between ints and doubles input really messed up the logic.
            Then the implementation of storing the input as string 1) simplified the logic and 2) quickly
            allowed me to fix many buttons that broke during this time frame of the two implementations.

            March 8, 2017,
            Errors Corrected:
            [a] The dot operator is now working perfectly. The logic was a lot simpler
            than I thought before. However, it required restructuring of the logic. Before
            I was only collecting integer inputs. By adding double inputs I 1) had to store
            them somewhere and 2) had to check for each input whether it was changed
            to a double, or not.

            Errors Found:
            [a] Dot operator is doing the same thing as the negate button was. The dot symbol
            is being print out in front of the number and not at the end. However, the
            issue is resolved after adding another number to the flow. I will continue
            to work on this issue so the dot operator does not print out at the front.
---
Version One Started: Unknown date
Version One Concluded: March 6, 2017,
            Status of Calculator: 20/28 buttons are operational (but not validated).
            No other history to report, unfortunately.
*/