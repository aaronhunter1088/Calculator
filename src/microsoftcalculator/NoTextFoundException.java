/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microsoftcalculator;

/**
 *
 * @author aaron
 */
public class NoTextFoundException {
    public final String EXCEPTION = "There is no text in the text area.\n";

    public NoTextFoundException() {
    }
    
    public final void display() {
        System.out.println(EXCEPTION);
    }
}
