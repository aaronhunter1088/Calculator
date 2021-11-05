/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microsoftcalculator;

import javax.swing.JFrame;

/**
 *
 * @author aaron
 */
public class BinaryTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Calculator calculator = new Calculator();
        BinaryCalculator binarycalculator = new BinaryCalculator();
        binarycalculator.setSize(600, 405);
        binarycalculator.setLocation(600,300);
        binarycalculator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        binarycalculator.setResizable(false);
        binarycalculator.setMenuBar();
        binarycalculator.setButton();
        binarycalculator.pack();
        binarycalculator.setVisible(true);
        
        
    }
    
}

/* **** Here are the notes for BinaryCalculator.
        It is here where I will now record all work
        done to this class. *****

August 2, 2017:
Most of the component itself was set up. The textArea was in place. There is a
different component below that for the bits view. Then the radio button groups
were set up and two buttons were in place. The work I did today included the 
following:
1)  Setting up some of the buttons by creating the handler, setting the font,
    size, and border of the buttons, and adding them to the component.
2)  Going through the code and cleaning up what I know is not needed. This may
    include deleting stuff pertinent to Calculator.class, the sister class
    to this class. 

September 18, 2017:
Finally figured out how to add the panels with the radio buttons on them
to the component. The buttons are all set up too minus the equals button... it
is not sized properly. It is off by just a bit. 
To solve this problem, this is what I did:
    First I thought I needed constraints.fill = GridBagConstraints.HORIZONTAL;
    This was everywhere in my regular calculator. But upon adding it, it did nothing.
    Second, I saw I had constraints.fill = 0 already. So I thought, if I eliminate
    this line, will it work? The answer is YES. Now all buttons are perfect. 
As of today, the gui is basically set up. All that is missing is that table that
describes the bits and their positions. This can be added later.

September 23,24, 2017:
I quickly got the interface set up for the functionality of the calculator. The 
buttons that are not used are disabled. Buttons 0-9 were quickly enabled along with
the operators, the equals button, clear, clear entry, and delete.
Byte, Word, Dword, and Qword were easy to implement. Their logic was simply to 
restrict the length of the text area.
Binary, Octal, Decimal, and Hexadecimal were also easy to implement but will still
require a bit of thought. As of now, you can switch between the modes and if there
is no input, nothing happens. 
Decimal function works just as the other calculator.


*/
