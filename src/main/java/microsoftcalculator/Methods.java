/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microsoftcalculator;

import javax.swing.JTextArea;

/**
 *
 * @author aaron
 */
public class Methods {
    
    String[] temp = {"0", "0", "0", "0"};
    
    public String[] getTemp() {
        return temp;
    }
    
    
    public String convertToNegative(String number) { 
        //System.out.print("\nconvertToNegative() running");
        //temp[position] = textArea.getText(); // input
        System.out.println("\nOld: " + number);
        //temp[position] = temp[position].substring(1, temp[position].length());
        System.out.println("New: "  + "-" + number);
        //textArea.setText(number + "-"); // update textArea
        number = "-" + number;
         
        //System.out.printf("Converted Number: %s\n", input); // confirm();
        //System.out.printf("temp[%d]: %s\n",position,temp[position]);  
        return number;
    }
    
    /**
     * This method takes the number and
     * eliminates the negative sign on it.
     * @param number
     * @return number as a positive
     */
    public String convertToPositive(String number) { 
        //System.out.println("\nconvertToPositive() running");
        //System.out.printf("Number to convert: %s\n", number);
        if (number.endsWith("-")) {
            System.out.printf("Converted Number: %s\n", number.substring(0, number.length()-1) );
            number = number.substring(0, number.length()-1);
        } else {
            System.out.printf("Converted Number: %s\n", number.substring(1, number.length()) );
            number = number.substring(1, number.length());
        }
        
        
        
        return number;
    }
    
    /**
     * This method returns true if the number
     * has a decimal and false if the number
     * does not have a decimal
     * @param number
     * @return 
     */
    public boolean isDecimal(String number) {
        //System.out.println("isDecimal() running");
        boolean answer = false;
        //int intRep = (int)Double.parseDouble(result);
        for(int i = 0; i < number.length(); i++) {
            if (number.substring(i).startsWith(".")) {
                System.out.println("We have a decimal number");
                answer = true;
            }
        }
        return answer;
    }
    
    /**
     * Returns true if result is negative
     * and false if result is true.
     * @param result
     * @return 
     */
    public boolean isNegativeNumber(String result) { 
        //System.out.println("\nisNegativeNumber() running");
        boolean answer = false;
        //int intRep = (int)Double.parseDouble(result);
        if (result.startsWith("-")) { // if int == double, cut off decimal and zero
            answer = true;
        }
        //System.out.println("result is = " + answer);
        return answer;
    }
    
    /**
     * This method clears all of the following:
     * All of temp
     * Input
     * TextArea
     * Operators
     * Position = 0
     * FirstNumBool = true
     * DotOperatorPressed = false
     */
    public void clear() {
        
        int position;
        String input = "0";
        JTextArea textArea = new JTextArea();
        boolean addBool = true, subBool = true, mulBool = true, divBool = true,
                firstNumBool = false, dotButtonPressed = true;
        // firstNum, secondNum, total, copy/paste, memory
        for ( position=0; position < 4; position++) {
            temp[position] = "";
            if(temp[position].equals(""))
                System.out.printf("temp[%d]: \'%s\'\n",position, temp[position]);
        }
        input = "";
        textArea.setText("");
        addBool = false;
        subBool = false;
        mulBool = false;
        divBool = false;
        position = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        System.out.printf("input: \'%s\'\n"
                        + "textArea: \'%s\'\n"
                        + "addBool: %s\n"
                        + "subBool: %s\n"
                        + "mulBool: %s\n"
                        + "divBool: %s\n"
                        + "position: %s\n"
                        + "firstNumBool: %s\n"
                        + "dotButtonPressed: %s\n",
                input, textArea.getText(), addBool, subBool, mulBool, divBool, position, firstNumBool, dotButtonPressed);
        
    }
    
    /**
     * This method takes the inputs and adds them.
     */
    public void addition() {}
    
    /**
     * This method takes the inputs and subtracts them.
     */
    public void subtraction() {}
    
    /**
     * This method takes the inputs and multiplies them.
     */
    public void multiply() {}
    
    /**
     * This method takes the inputs and divides them.
     */
    public void divide() {}
    
    /**
     * This method takes the operatorBool and reverses its result.
     * @param operatorBool
     * @return the opposite of the operatorBool's result.
     */
    public boolean resetOperator(boolean operatorBool) {
        if (operatorBool == false) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Takes the current number and makes sure that the return
     * only has two decimal places at the end if there are more than two.
     * @param number
     * @return 
     */
    public String formatNumber(String number) { return "";}
    
    /**
     * This method prints out the inputs and operators and the text area.
     */
    public void confirm() {}
    
    /**
     * This method finds the decimal, checks the numbers after it, and if they are
     * all zeroes, clears the decimal and zeroes. If there is any other number there
     * then the operation is canceled.
     * @param currentNumber
     * @return updated currentNumber
     */
    private String clearZeroesAtEnd(String currentNumber) {return "";} 
}
