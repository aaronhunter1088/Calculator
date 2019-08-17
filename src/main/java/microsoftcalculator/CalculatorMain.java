//package microsoftcalculator;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.awt.image.ImageObserver;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//
//import javax.imageio.ImageIO;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//
//// Information about this project is just below main method
//public class CalculatorMain 
//{
//    static String calculatorOriginalPath = "src/main/resources/images/calculatorOriginal.jpg";
//    static {
//    	System.out.println(calculatorOriginalPath);
//    }
//	public static void main(String[] args) throws IOException
//    {
//    	Image image = null;
//    	ImageIcon iconImage = null;
//    	//System.out.println(new ImageIcon(Calculator.class.getResource(calculatorOriginalPath)).getImageLoadStatus());
////    	try { iconImage = new ImageIcon(Calculator.class.getResource(calculatorOriginalPath)); }
////    	catch (NullPointerException e) {
////    		System.out.println("message:" + e.getMessage());
////    		System.out.println("cause: " + e.getCause());
////    		StackTraceElement elem[] = e.getStackTrace();
////    		for (int i=0; i<elem.length; i++) { System.out.println("at" + (i+1) + " " + elem[i]); }
////    		System.out.print("Stack Trace: ");
////    		e.printStackTrace(System.out);
////    		System.out.println("End stack trace");
////    	}
//    	//image = iconImage.getImage();
//    	//imageIcon = new ImageIcon(new File("calculatorOriginal.jpg").getPath()).getImage();
//    	
////    	if (image != null) {
////    		System.out.println("We have the image...");
////    	} else System.out.println("The image was NOT loaded.");
//    	
//    	//Calculator calculator = new Calculator();
//        Calculator calculator = new Calculator();
//        //BinaryCalculator binarycalculator = new BinaryCalculator();
//        
//        //calculator.setIconImage(iconImage.getImage()); // sets icon that is shown in taskbar to specific image. if not set, 
//        // the image that will appear is exactly the same as what I see when I run the GUI.
//        calculator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        calculator.setMinimumSize(new Dimension(100,200)); //228, 325
//        calculator.setResizable(false);
//        calculator.setLocation(1000, 250);
//        calculator.setMenuBar();
//        //calculator.setButtonStandardMode();
//        calculator.pack();
//        //calculator.setFinishedText();
//        calculator.setVisible(true);
//
//        ArrayList<String> list = new ArrayList<>(4);
//        list.add("");
//        
//        
////        while (calculator.isActive() || binarycalculator.isActive()) {
////            if (calculator.getMode(calculator.getBinaryMode()) == true) {
////                //System.out.println(calculator.getLocationOnScreen());
////                binarycalculator.setLocation(calculator.getLocationOnScreen());
////                calculator.setVisible(false);
////                calculator.setMode(false);
////                binarycalculator.setSize(600, 405);
////                binarycalculator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
////                binarycalculator.setResizable(false);
////                binarycalculator.setMenuBar();
////                binarycalculator.setButton();
////                binarycalculator.setVisible(true);
////            }
////            // add more modes here!!
////        }
//        
//    }
//    
//    /** Returns an ImageIcon, or null if the path was invalid. */
//    protected static ImageIcon createImageIcon(String path, String description) {
//      java.net.URL imgURL = Calculator.class.getResource(path);
//      if (imgURL != null) {
//        return new ImageIcon(imgURL, description);
//      } else {
//        System.err.println("Couldn't find file: " + path);
//        return null;
//      }
//    }
//    
//    private static void initLookAndFeel(String lookAndFeel) {
//        String thislookAndFeel = lookAndFeel;
//        final String LOOKANDFEEL = "Metal";
//        
//        if (LOOKANDFEEL.equals("Metal")) {
//            lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
//            //  an alternative way to set the Metal L&F is to replace the 
//            // previous line with:
//            // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
//                
//        } else if (LOOKANDFEEL.equals("System")) {
//            lookAndFeel = "javax.swing.plaf.system.SystemLookAndFeel";
//        } else if (LOOKANDFEEL.equals("Motif")) {
//            lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
//        } else if (LOOKANDFEEL.equals("GTK")) { 
//            lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
//        } else {
//            System.err.println("Unexpected value of LOOKANDFEEL specified: " + LOOKANDFEEL);
//            lookAndFeel = null;
//        }
//        
//    }
//    
//}
//
//
//
///*
//a. PROGRAMMER: Michael Ball 
//b. COURSE:   CSCI 3324.02 : Advanced Java
//c. DATE:   April 3, 2015
//d. ASSIGNMENT:  Project 2
//e. ENVIRONMENT: Windows 8 
//f. FILES INCLUDED: calculator.java, calculatortest.java, calculator.jpg, 
//    calculator2.jpg, windows8.jpg, and a screen shot of the below example. 
//g. PURPOSE: To demonstrate the use of GUI's 
//h. INPUT:  inputs done through program: examples are number buttons and operation buttons
//i. PRECONDITIONS: Booleans addBool, subBool, mulBool, divBool, firstNumBool, all set to false except firstNumBool set to true.
//    temp[] values all set at 0. Each has a specific pursose which is noted in the code.
//j. OUTPUT: displays calculated result from input number buttons
//k. POSTCONDITIONS: All Booleans returned to intial state. 
//l. ALGORITHM: 
//m.  ERRORS:  There were a lot of errors and I failed to write them down. One that I can recall
//    was how to get multiple numbers to appear on the screen when the user wants to enter a 2 digit
//    or 3 digit or bigger than 1 digit number. This took a great deal of thought.
//    Another was the button handling. That was challenging. 
//    Following this point errors will be documented. I began working on the calculator again on March 6, 2017.
//    March 7, 2017: 
//Button  [a] Accidentally broke the class ButtonHandler. While trying to work solve the issue of negative
//Hanlder numbers yesterday, I accidentally removed the boolean variable firstNumBool's value which was set to true.
//        This was causing the if statement to fail ... if (firstNumBool[which was now null] == true) ... this is false
//        so it was failing each time. After resetting the value to true, the class began to work again.
//Text    [b] Today I finally solved the issue with the text area not being as big as I wanted it to be.
//Area    I noticed that all other buttons are set using the method setPreferredSize while I was using the method
//        setSize. So I changed it up and used the setPreferredSize method (using the same size as the 0 button setting)
//        and it came out just as I wanted it to. 
//*       [c] Yesterday I noticed that there is an issue with the multiplication function. All other of the three functions
//Oper    are operating fine and there doesn't appear to be any issue with the code. But after solving the issue described
//        in [b], I changed all the boolean values to false. This resolved the issue.
//+/-     [d] The negate button continues to baffle me. Yesterday I was able to get the text to appear as it should be
//Oper    when written but the orietation was flipped so the text was appearing first on the left-hand side of the textare
//        and that is not what I want. Again, trying to append the "-" symbol in front of the number continues to fail. But
//        I believe that possibly continuing to do that and updating the actual number to negative (but only displaying its
//        positive counterpart), I may be able to solve this problem. To be continued. 
//        As of this date, there are 8/28 are NOT working. 
//
//    March 8, 2017
//dot     Yesterday, I somewhat tinkered with the code here. I was able to get it to print out a number such as 
//Oper    504.2 but it would do the following afterwards: 504.2.5.0 and so on. So today I began to work out how 
//        to fix this and I came upon a realization about this button. The dot operator's primary function should
//        be to determine whether or not to store input in either temp[] or tempD[]. This is because when the user
//        pushes this button, we know that this number will be double. But this flag is also reset to false for each
//        the first and second number because either number may be an int. Also, this will allow the input to continue on
//        and not add another dot to the output. 
//    March 14, 15, 2017 (Early into the morning)
//+-      Today, I got back on. I fixed the logic to accept string inputs. I do all my conversions at the very end.
//* /     Then I repaired each operator to work. This was no longer a big issue. I did not have to determine if a number 
//Oper    was whole or not to print out a proper result... I really simplified the logic. Various tests have proven them
//        to be operating 100%. Further testing will continue.
//dot     So after simplifying my logic, I was able to easily take in the input, add the dot, display it properly,
//Oper    and everything else I needed to do with again simplified logic. I realized that before, I would write pseudocode,
//        but I would write actual code as well. This time I just wrote pseudocode, and I figured out the logic.
//Clear   This was a simple fix but needed to wait for the transition of storing from ints and doubles to string.
//Btn     This problem was fixed today and so I was easily able to change variable names and clear whatever easily.
//Clear   This was exactly the same as the clear button. However there was a slight issue with the logic. When I did, lets
//Entry   say, 6 + 3 = 9... 9 would be temp[0]. And the calculator is programmed to keep going, meaning at this moment,
//Btn     it expects an operator to perform with 9; its also expected temp[1] input. But when I push the CE button
//        I wanted to also start over with collecting input. This was solved by first resetting the temp[1,2] values
//        to "". So when I got to the logic that decides whether we are collecting temp[0] or temp[1] at CE, I could
//        just check to see if temp[1].equals(""). If so then we know we are dealing with collecting the input for number
//        one and we want to start over from the beginning.
//Negate  This was another quick fix but had a slight issue along the way as well. I used similar logic when adding the .
//Btn     to the end of the number. This time I added the - symbol to the end of the number and, when displayed in the
//        textArea, looks normal. But I also had to make sure that the proper number was getting saved as a negative. This
//        was also very quick and easy to change. Both numbers can be negated. Further testing will continue. 
//    March 15, 2017: No this is not a mistake. I programmed early into the morning. Now its night and its the same day.
//%       This evening, I worked on the percent operator. The logic was simple. Changed the input to new storing array.
//Oper    Divided by 100.... there wasn't too much to change here to get this button working. However it did require some 
//        additional logic to be added to the button and dot operators. While testing a simple problem: 5 % = 0.05 + .95
//        = 1... note that .95 would appear as 0.95 in the text area (not an error) ... the calculator was not recognizing
//        that when i started to collect my input with the dot operator, i needed it to say 0. and not just '.'.
//        The dot operator changed a bit. I need a nested if else. to determine if the dot operator was the first to 
//        be collected as input. Once I figured a way out to determine that, it was simple to implement the if else part.
//        The button operator also needed some additional logic. I needed to let it know right away if the dot operator
//        was first to be pushed, change how we store the input properly, and set the text area accordingly. It did take
//        some tinkering to figure out. But button by button, I was able to track down how to allow the calculator to 
//        collect both input one and input two starting as .123 etc. Then again I tested my earlier math problem and it
//        worked. 
//1/x     So like so many other buttons, this was a very simple fix. I had to change the name of the location where
//Oper    I was saving the user's input but no other code change was required to get this button to work again.
//    March 16, 2017
//DEL     This button had a bit of an issue. I beleive this button was working before. But to get this button to at least
//Oper    work now, the following had to happen: I first had to change the name of the location of where I was saving
//        the user's input. Then I had to chop off the end of the input, and update the text area and storage. Then, during
//        testing, the delete button was not saving the updated input properly. This is because I had forgotten to do so.
//        So a simple storing of the new number into temp[position] solved this quickly. Then however, when continuing to test,
//        I noticed that the second number was being truncated each time a new button was pressed. So to solve this issue, I went
//        to the section of code where I reset the text area and reset firstNumBool only if it was false. If not, I reset dotButtonPressed.
//    March 21, 2017
//MS      This button was simple to implement. All I had to do was store the value into the appropriate location in temp. I did
//Btn     have one slight issue where I accidentally set the value into the current position and not in a set location.
//
//MC      This button was easy to implement as well. All I had to do was clear the storage of where memory is stored.
//Btn
//
//MR      This was again easy. I set the text area to memory and reset the storage to memory.
//Btn
//
//    March 23, 2017
//M+      This button did not take much work. I took the logic from the add button along with the logic in the 
//Btn     equals button where the addition takes place, adjusted it for memory, and that was it. I had a couple
//        minor logic issues where I forgot to set a sys.out line to 4 instead of position. Other than that, this
//        simply leaves the M- to complete the calculator!
//
//MR      I added a small feature where the memory recall cannot be pushed until there is memory to recall. So
//Btn     once the memory store button is pushed, the memory recall can be used. Once the memory clear button is
//        pushed, it once again goes to its "off state."
//
//    April 7, 2017
//DEL     Today, I solved the delete button problem. I do not know why I thought it was finished. It simply got
//Btn     rid of the last index of the number and this was not correct for all situations.
//        Today I have set it up to work properly with the first and second number, whether its negative or positive,
//        whether its whole or a decimal. 
//n.  EXAMPLE: Cannot include example. A sample input and output example is all I can submit
//
//User opens calculator.
//User enters into screen 85.
//Screen displays 85.
//User presses + button.
//Screen displays 85 +.
//Users enters into screen 5.
//Screen displays 5.
//Users presses = button.
//Screen displays 90.
//
//o.  HISTORY:
//Version One Started: Unknown date
//Version One Concluded: March 6, 2017
//            Status of Calculator: 20/28 buttons are operational (but not validated).
//            No other history to report unfortunately.
//
//Version Two Started: March 7, 2017
//            Errors Found:
//            [a] Dot operator is doing the same thing as the negate. The dot symbol
//            is being print out in front of the number and not at the end. However, the
//            issue is resolved after adding another number to the flow. I will continue
//            to work on this issue so the dot operator does not print out at the front.
//
//            March 8, 2017
//            Errors Corrected:
//            [a] The dot operator is now working perfectly. The logic was a lot simplier
//            than I thought before. However, it required restructuring of the logic. Before
//            I was only collecting integer inputs. By adding double inputs I 1) had to store
//            them somewhere and 2) had to check for each input whether or not it was changed
//            to a double. 
//
//            March 15, 2017
//            See above documentation. As of this date, with the new string input logic implemented,
//            I now appear to once again have 20/28 buttons working and functional. The implementation
//            of first trying to distinquish between ints and doubles input really messed up the logic.
//            Then the implementation of storing the input as string 1) simplified the logic and 2) quickly
//            allowed me to fix many buttons that broke during this time frame of the two implementations.
//            
//            March 18, 2017
//            Today I was about to start on the logic to allow the memory buttons to work. Before starting
//            to write my logic, I first tested how the buttons work. During testing, I noticed that the simple
//            operation of 85 + 5 was not setting the text area to 90. The storage area was receiving the proper
//            result though. So when I looked at my last code change, I remembered that I added in additional logic
//            so that if a user tries to divide by zero it will tell them they cannot instead of returning infinity.
//            This required me to change when temp[2] actually receives the result. I simply forgot to add this.
//            Then when I got the result it was displaying 90.0. Which is weird because I had written logic to keep
//            it from doing so. I only added that logic in though with one of the operators in the equals button handler.
//            So adding in that logic everywhere helped it display 90 finally.
//
//            Again, got distracted by my memory buttons. I got into the copy/paste functionality and saw paste
//            was not working. So I checked the logic there and noticed that there was no action when this was clicked.
//            So I added some really fast. So, when copying, set temp[3] equal to the text area. When pasting, set 
//            the text area to whatever value is in temp[3]; But for some reason, temp[3] is null at this point.
//            ... I paused here to go check again. Then I suddenly realized the issue. When I pressed clear, 
//            it was resetting all values for temp. I actually only wanted to reset input one, two and result. 
//            Fixing the for loop fixed this issue. 
//
//            March 21, 2017
//            Today I fixed the error where if you were to type into the calculator this problem: 5 + negate + percent,
//            it would return: 0.05-. This is the correct answer, but the output is all wrong. So what I did was I took
//            the code that changed the input to negative and reversed it to take the negative away. Then add it to the 
//            front and display, all while actually keeping the negative input. This took about 30 minutes to solve.
//            
//            I also fixed an error where the input could not hold onto decimal numbers longer than one in length! This
//            was a clear issue and an unexpected one. But I nailed it down to the logic in the button handler. I needed
//            to set the dotButtonPressed boolean to false in the else, else part. Once I did this, logic like 10.356 was
//            possible (again)...
//            
//            March 23, 2017
//            5 + negate + MS + 6 = Result: 5-6 Expected: 6 as temp[1]!
//
//            Today I thought of an issue that I had not thought of yet and when I verified my idea, I was right.
//            I had left out some logic that would only allow the operator button to happen once. After that I
//            added it to the other operator buttons and they all checked out. Now you cannot keep pressing an
//            operator button. It only allows it once. 
//            Also I figured out that memory recall did not display negative numbers properly. It was again displaying
//            the negative sign at the end of the number and not the front. So I added in the code I already had to
//            convert numbers to display properly and nested my existed logic together in an if else statement. Then
//            almost immediately, I thought about what would happen with a decimal number. And it broke. This issue
//            took a more thought to fix but it only took about 15 minutes. I added some logic to print out the 
//            result if the number was a double in an else if (number.number < 0.0) but that did not work. So I 
//            tried putting it in the first condition as an or, but that also didn't work. So I changed the entire
//            condition to just (number.number < 0.0) and it works for whole and decimal numbers. 
//            
//            March 24, 2017
//            I fixed an issue that broke the calculator if the delete button was pushed. It should not do 
//            anything if the text area is empty. This was an easy fix. I then needed to implement this idea
//            for all other buttons where this could happen. 
//
//            March 25, 2017
//            I continue to fix issues where a button should not perform any action if there is no number
//            in the text area. 
//
//            March 26, 2017
//            I had an issue where the negate button broke. This likely happened when I was implementing some
//            logic to not allow a button to be pushed when there is no number to associate the button with.
//            I had one if statement the same as the initial. This was incorrect. It took about 10 minutes
//            to solve.
//            I fixed the memory add and memory subtraction button to work with memory by updating the result
//            if one of those buttons is pushed. 
//            I also found a couple more spots where the decimal number was not converted properly. These
//            spots were fixed accordingly. 
//
//            // add in logic to check to see if operator can be performed on a negative number.
//            April 1, 2017
//            I am now trying to add logic into the operator buttons that would allow syncronize to the flow
//            of a problem. For example, the user could do this: 5+5+5+5 and it would work and update with each 
//            push of an operator. Plus pushing any other operator would return the updated result and perform
//            the operator after.
//            // DEL doesn't erase operator if that is the last entry!
//
//            April 7, 2017
//            DEL button is not working properly. I thought I had fixed this issue. But I was way off. So 
//            today and on the 11, I made sure that the proper action was performed to make sure the number
//            returned was appropriate.
//
//            April 29, 2017
//            Today I fixed a bug with the DEL button where when the input was something like 0.5,
//            it was returning .0 instead of just 0. I fixed the issue and in the process made some 
//            additional methods. 
//            Today I also fixed a bug where the input was taking 0625 as a number instead of eliminating
//            the unnecessary zero in the front.
//
//            April 30, 2017
//            Today, I worked on the negate button. I was able to simplify the logic by so much.
//            I reduced the negate button by 61 lines of code. This is due to the fact that my 
//            calculator was not doing the same thing as Microsofts. So after checking and double
//            checking what Microsoft's calculator did, I changed my code to do the same. This
//            helped majorly in reducing the code.
//
//            May 6, 2017
//            I found a slight variation in my calculator that is different from Microsofts. I noticed
//            that pushing the negate button, effecting a negative number, it was not returning it to a
//            positive number. After looking at my code, I noticed I just was not allowing it to add another
//            negative sign. So I changed my method to convert it back to positive. Done actually on May
//            8, 2017 but I discovered the issue on the 6th.
//Version Two Concluded: March 6, 2017
//Version Three Started: October 26, 2017
//            October 26, 2017:
//            Today, I begin version three. I will start by saying that the project is going to be completely
//            redesigned. There will be more structure to this new version of the calculator. There will be a
//            main Calculator class, containing certain fields and properties that all calculators have. Then
//            subclasses will extend the Calculator class. This will allow certain things to be typed once.
//            Calculator
//            Standard | Scientific | Programmer | Date Calculation | Converter (has more subclasses; may be on top tier of its own)
//                                                                    Currency | Volume | Length | Weight and Mass.....
//            Furthermore, in both version 1, and 2, Swing coding style was used. This version will be built using
//            the principles and style specific properties that JavaFX includes and is ultimately the replacement
//            (at this time) for Swing applications. Also, both version 1 and 2 were written in Netbeans. With
//            version 3, I will be using IntelliJ IDEA + JavaFX Scene Builder to create and design the calculator.
//            Finally, I will be using a Trello Board to keep track of all my work.
//            So what did I do today exactly?
//            Today, I began to set up my Trello board, began the new design for my calculator (which will still go off of the
//            old project requirement: try to mimic Microsoft's calculator), and built some UML's to help me define
//            the scope of the project.
//*/
//
///*
//Also, the pictures included might need to change the file location in the code.
//When run at home, my flashdrive would use f://etc., but when at school it would use
//e://etc. So I will leave it as it is but it may need to be changed according to
//how the computer is identifying the flashdrive. 
//*/