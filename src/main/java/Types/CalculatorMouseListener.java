package Types;

import Calculators.Calculator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CalculatorMouseListener implements MouseListener {

    private final Calculator calculator;

    public CalculatorMouseListener(Calculator calculator) {
        this.calculator = calculator;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        calculator.setFocusable(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
