package microsoftcalculator;

import javax.swing.JFrame;

public class CalcTest {
    public static void main(String[] args) {
        Calc calc = new Calc();
        calc.setSize(600, 405);
        calc.setLocation(600,300);
        calc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        calc.setResizable(false);
        calc.setMenuBar();
        calc.setButton();
        calc.pack();
        calc.setVisible(true);
    }
}
