package Types;

import Calculators.Calculator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static Types.Texts.*;

public class CalculatorKeyListener implements KeyListener {

    private final Calculator calculator;

    public CalculatorKeyListener(Calculator calculator) {
        this.calculator = calculator;
        setupKeyInputMap();
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void setupKeyInputMap() {
        KeyStroke key0 = KeyStroke.getKeyStroke(KeyEvent.VK_0, 0, false);
        KeyStroke key1 = KeyStroke.getKeyStroke(KeyEvent.VK_1, 0, false);
        KeyStroke key2 = KeyStroke.getKeyStroke(KeyEvent.VK_2, 0, false);
        KeyStroke key3 = KeyStroke.getKeyStroke(KeyEvent.VK_3, 0, false);
        KeyStroke key4 = KeyStroke.getKeyStroke(KeyEvent.VK_4, 0, false);
        KeyStroke key5 = KeyStroke.getKeyStroke(KeyEvent.VK_5, 0, false);
        KeyStroke key6 = KeyStroke.getKeyStroke(KeyEvent.VK_6, 0, false);
        KeyStroke key7 = KeyStroke.getKeyStroke(KeyEvent.VK_7, 0, false);
        KeyStroke key8 = KeyStroke.getKeyStroke(KeyEvent.VK_8, 0, false);
        KeyStroke key9 = KeyStroke.getKeyStroke(KeyEvent.VK_9, 0, false);
        KeyStroke keyDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        KeyStroke keyBackspaceDelete = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false);
        KeyStroke keyClear = KeyStroke.getKeyStroke(KeyEvent.VK_CLEAR, 0, false);
        KeyStroke keyClearEntry = KeyStroke.getKeyStroke(KeyEvent.VK_CLEAR, InputEvent.SHIFT_DOWN_MASK, false);
        KeyStroke keyEquals = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0, false);
        KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        KeyStroke keyAddition = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0, false);
        KeyStroke keySubtraction = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0, false);
        KeyStroke keyMultiplication = KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0, false);
        KeyStroke keyDivision = KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0, false);
        KeyStroke keyDecimal = KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0, false);
        calculator.getCurrentPanel().getInputMap().put(key0, ZERO);
        calculator.getCurrentPanel().getInputMap().put(key1, ONE);
        calculator.getCurrentPanel().getInputMap().put(key2, TWO);
        calculator.getCurrentPanel().getInputMap().put(key3, THREE);
        calculator.getCurrentPanel().getInputMap().put(key4, FOUR);
        calculator.getCurrentPanel().getInputMap().put(key5, FIVE);
        calculator.getCurrentPanel().getInputMap().put(key6, SIX);
        calculator.getCurrentPanel().getInputMap().put(key7, SEVEN);
        calculator.getCurrentPanel().getInputMap().put(key8, EIGHT);
        calculator.getCurrentPanel().getInputMap().put(key9, NINE);
        calculator.getCurrentPanel().getInputMap().put(keyDelete, DELETE);
        calculator.getCurrentPanel().getInputMap().put(keyBackspaceDelete, DELETE);
        calculator.getCurrentPanel().getInputMap().put(keyClear, CLEAR);
        calculator.getCurrentPanel().getInputMap().put(keyClearEntry, CLEAR_ENTRY);
        calculator.getCurrentPanel().getInputMap().put(keyEquals, EQUALS);
        calculator.getCurrentPanel().getInputMap().put(keyEnter, EQUALS);
        calculator.getCurrentPanel().getInputMap().put(keyAddition, ADDITION);
        calculator.getCurrentPanel().getInputMap().put(keySubtraction, SUBTRACTION);
        calculator.getCurrentPanel().getInputMap().put(keyMultiplication, MULTIPLICATION);
        calculator.getCurrentPanel().getInputMap().put(keyDivision, DIVISION);
        calculator.getCurrentPanel().getInputMap().put(keyDecimal, DECIMAL);

        calculator.getCurrentPanel().getActionMap().put(ZERO, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton0().doClick();
                calculator.getButton0().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(ONE, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton1().doClick();
                calculator.getButton1().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(TWO, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton2().doClick();
                calculator.getButton2().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(THREE, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton3().doClick();
                calculator.getButton3().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(FOUR, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton4().doClick();
                calculator.getButton4().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(FIVE, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton5().doClick();
                calculator.getButton5().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(SIX, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton6().doClick();
                calculator.getButton6().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(SEVEN, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton7().doClick();
                calculator.getButton7().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(EIGHT, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton8().doClick();
                calculator.getButton8().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(NINE, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButton9().doClick();
                calculator.getButton9().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(DELETE, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent evt){
                calculator.getButtonDelete().doClick();
                calculator.getButtonDelete().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(CLEAR, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonClear().doClick();
                calculator.getButtonClear().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(CLEAR_ENTRY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonClearEntry().doClick();
                calculator.getButtonClearEntry().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(EQUALS, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonEquals().doClick();
                calculator.getButtonEquals().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(ADDITION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonAdd().doClick();
                calculator.getButtonAdd().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(SUBTRACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonSubtract().doClick();
                calculator.getButtonSubtract().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(MULTIPLICATION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonMultiply().doClick();
                calculator.getButtonMultiply().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(DIVISION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonDivide().doClick();
                calculator.getButtonDivide().setFocusable(false);
            }
        });
        calculator.getCurrentPanel().getActionMap().put(DECIMAL, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.getButtonDecimal().doClick();
                calculator.getButtonDecimal().setFocusable(false);
            }
        });
    }
}
