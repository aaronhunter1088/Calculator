/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version1;

/**
 *
 * @author aaron
 */
public class LoanCalculator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double promoCitiAPR = .1099;
        double currentLoanAPR = .1197;
        double currentCitiAPR = .2199;
        double currentLoanAmount = 10907.72;
        double availCredit = 5388.00;
        double currentTotalInterest = 0;
        double monthlyTotalInterest = 0;
        double transferTotalInterest = 0;
        
        int monthCounter = 0;
        int yearCounter = 0;
        
        // set up regular interest amount logic
        
        while (currentLoanAmount > 0) {
            monthCounter++;
            currentLoanAmount -= 400;
            if (monthCounter == 12 && currentLoanAmount > 0) {
                yearCounter++;
                monthCounter = 0;
                currentTotalInterest = ((currentLoanAmount - (400*12)) * currentCitiAPR); // every full year, reset cTI to for sure value
                currentLoanAmount = (((currentLoanAmount - (400*12)) * currentCitiAPR)) + (currentLoanAmount);
                System.out.printf("Year %d Report\n", yearCounter);
                System.out.printf("CTI: %.2f | CLA: %.2f\n", currentTotalInterest, currentLoanAmount);
            }
            else if (currentLoanAmount >= 0) {
                //1342.93
                //.018325
                //monthlyTotalInterest += currentLoanAmount * (.018325); // currentCitiAPR/12 
                //currentLoanAmount += monthlyTotalInterest;
                System.out.printf("Month %d Report\n", monthCounter);
                System.out.printf("MTI: %.2f | CLA: %.2f\n", monthlyTotalInterest, currentLoanAmount);
            }
            else { // currentLoanAmount, after removing 400 is now <= 0
                System.out.printf("Total time: %d year(s) | %d month(s)\n", yearCounter, monthCounter);
                System.out.printf("Total interest paid: %.2f\n", currentTotalInterest);
            }
        }
    }
}
