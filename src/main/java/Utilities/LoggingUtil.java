package Utilities;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import Calculators.Calculator;
import Panels.DatePanel;
import Types.CalculatorBase;
import Types.CalculatorByte;
import Types.CalculatorView;
import Types.DateOperation;
import org.apache.logging.log4j.Logger;

import static Types.CalculatorView.VIEW_BASIC;
import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.DateOperation.DIFFERENCE_BETWEEN_DATES;
import static Types.Texts.*;

/**
 * LoggingUtil
 * <p>
 * This class contains static methods used
 * for logging throughout the Calculator application
 *
 * @author Michael Ball
 * @version 4.0
 */
public class LoggingUtil {

    /**
     * This method is used after any result to verify
     * the result of the previous method and see the
     * values of the entire Calculator object
     *
     * @param message the message to pass into confirm
     */
    public static void confirm(Calculator calculator, Logger logger, String message)
    {
        String textPaneValue = calculator.getTextPaneValue();
        CalculatorView currentView = calculator.getCalculatorView();
        logger.info("Confirm {} Results: {}", currentView, message);
        logger.info("----------------");
        switch (currentView) {
            case VIEW_BASIC, VIEW_PROGRAMMER -> {
                if (currentView == VIEW_BASIC)
                {
                    logger.info("textPane: '{}'", textPaneValue);
                }
                if (currentView == VIEW_PROGRAMMER)
                {
                    CalculatorBase currentBase = calculator.getCalculatorBase();
                    logger.info("Base: {}", currentBase);
                    CalculatorByte currentByte = calculator.getCalculatorByte();
                    logger.info("Byte: {}", currentByte);
                    switch (currentBase) {
                        case BASE_BINARY -> logger.info("textPane: '{}'", calculator.getProgrammerPanel().separateBits(textPaneValue));
                        //case BASE_OCTAL -> logger.info("octets: {}", calculator.getProgrammerPanel().getOctetsString());
                        case BASE_DECIMAL -> logger.info("textPane: '{}'", textPaneValue);
                        //case BASE_HEXADECIMAL -> logger.info("hexadects: {}", calculator.getProgrammerPanel().getHexadectsString());
                    }
                }
                // print out all operators' status versus only the active ones
                if (logger.isDebugEnabled())
                {
                    if (calculator.isMemoryValuesEmpty())
                    { logger.debug("no memories stored!"); }
                    else
                    {
                        logger.debug("memoryPosition: {}", calculator.getMemoryPosition());
                        logger.debug("memoryRecallPosition: {}", calculator.getMemoryRecallPosition());
                        for (int i = 0; i < 10; i++) {
                            logger.debug("memoryValues[{}]: {}", i, calculator.getMemoryValues()[i]);
                        }
                    }
                    logger.debug("addBool: {}", calculator.getValueAt(2).equals(ADDITION) ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("subBool: {}", calculator.getValueAt(2).equals(SUBTRACTION) ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("mulBool: {}", calculator.getValueAt(2).equals(MULTIPLICATION) ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("divBool: {}", calculator.getValueAt(2).equals(DIVISION) ? YES.toLowerCase() : NO.toLowerCase());
                    if (currentView == VIEW_PROGRAMMER)
                    {
                        logger.debug("OR set: {}", calculator.getValueAt(2).equals(OR) ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("MODULUS set: {}", calculator.getValueAt(2).equals(MODULUS) ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("XOR set: {}", calculator.getValueAt(2).equals(XOR) ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("NOT set: {}", calculator.getValueAt(2).equals(NOT) ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("AND set: {}", calculator.getValueAt(2).equals(AND) ? YES.toLowerCase() : NO.toLowerCase());
                    }
                    logger.debug("PEMDAS active: {}", calculator.isPemdasActive() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("values[0]: '{}'", calculator.getValues()[0]); // first number
                    logger.debug("values[1]: '{}'", calculator.getValues()[1]); // second number
                    logger.debug("values[2]: '{}'", calculator.getValues()[2]); // operator
                    logger.debug("values[3]: '{}'", calculator.getValues()[3]); // result
                    logger.debug("values[4]: '{}'", calculator.getValues()[4]); // copy value
                    logger.debug("valuesPosition: {}", calculator.getValuesPosition());
                    logger.debug("isFirstNumber: {}", calculator.isFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("isDotEnabled: {}", calculator.isDotPressed() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("is value: '{}' negative?: {}", calculator.getValueAt(), (calculator.isNegativeNumber(calculator.getValueAt()) || calculator.isNegativeNumber()) ? YES.toLowerCase() : NO.toLowerCase());
                }
                else
                {
                    if (calculator.isMemoryValuesEmpty())
                    { logger.info("no memories stored!"); }
                    else {
                        logger.info("memoryPosition: {}", calculator.getMemoryPosition());
                        logger.info("memoryRecallPosition: {}", calculator.getMemoryRecallPosition());
                        for (int i = 0; i < calculator.getMemoryPosition(); i++) {
                            logger.info("memoryValues[{}]: {}", i, calculator.getMemoryValues()[i]);
                        }
                    }
                    if (calculator.getValueAt(2).equals(ADDITION)) logger.info("addBool: {}", YES.toLowerCase());
                    if (calculator.getValueAt(2).equals(SUBTRACTION)) logger.info("subBool: {}", YES.toLowerCase());
                    if (calculator.getValueAt(2).equals(MULTIPLICATION)) logger.info("mulBool: {}", YES.toLowerCase());
                    if (calculator.getValueAt(2).equals(DIVISION)) logger.info("divBool: {}", YES.toLowerCase());
                    if (calculator.getCalculatorView().equals(VIEW_PROGRAMMER))
                    {
                        if (calculator.getValueAt(2).equals(OR)) logger.info("OR set: {}", YES.toLowerCase());
                        if (calculator.getValueAt(2).equals(MODULUS)) logger.info("MODULUS set: {}", YES.toLowerCase());
                        if (calculator.getValueAt(2).equals(XOR)) logger.info("XOR set: {}", YES.toLowerCase());
                        if (calculator.getValueAt(2).equals(NOT)) logger.info("NOT set: {}", YES.toLowerCase());
                        if (calculator.getValueAt(2).equals(AND)) logger.info("AND set: {}", YES.toLowerCase());
                    }
                    if (calculator.isPemdasActive()) logger.info("pemdasBool: {}", calculator.isPemdasActive() ? YES.toLowerCase() : NO.toLowerCase());
                    for (int i = 0; i <= calculator.getValuesPosition(); i++) {
                        logger.info("values[{}]: '{}'", i, calculator.getValues()[i]);
                    }
                    if (!calculator.getValues()[2].isEmpty()) logger.info("values[2]: '{}'", calculator.getValueAt(2));
                    if (!calculator.getValues()[3].isEmpty()) logger.info("values[3]: '{}'", calculator.getValueAt(3));
                    if (!calculator.getValues()[4].isEmpty()) logger.info("values[4]: '{}'", calculator.getValueAt(4));
                    logger.info("valuesPosition: {}", calculator.getValuesPosition());
                    logger.info("isFirstNumber: {}", calculator.isFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.info("isDotEnabled: {}", calculator.isDotPressed() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.info("is value: '{}' negative?: {}", calculator.getValueAt(), calculator.isNegativeNumber(calculator.getValueAt()) ? YES.toLowerCase() : NO.toLowerCase());
                }
            }
            case VIEW_SCIENTIFIC -> {
                // TODO: add to first case once ready
                logger.warn("Confirm message not setup for {}", currentView);
            }
            case VIEW_DATE -> {
                DateOperation currentDateOperation = calculator.getDateOperation();
                logger.info("dateOperation: {}", currentDateOperation);
                LocalDateTime date = LocalDateTime.of(((DatePanel)calculator.getDatePanel()).getTheDateFromTheFromDate(), LocalTime.now());
                var updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                var capitalizedDay = updatedDate.split(SPACE)[0].toUpperCase();
                var capitalizedMonth = updatedDate.split(SPACE)[1].toUpperCase();
                updatedDate = capitalizedDay + SPACE + capitalizedMonth + SPACE + updatedDate.split(SPACE)[2] + SPACE + updatedDate.split(SPACE)[3];
                logger.info("From Date: {}", updatedDate);
                if (currentDateOperation == DIFFERENCE_BETWEEN_DATES)
                {
                    date = LocalDateTime.of(((DatePanel)calculator.getDatePanel()).getTheDateFromTheToDate(), LocalTime.now());
                    updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                    capitalizedDay = updatedDate.split(SPACE)[0].toUpperCase();
                    capitalizedMonth = updatedDate.split(SPACE)[1].toUpperCase();
                    updatedDate = capitalizedDay + SPACE + capitalizedMonth + SPACE + updatedDate.split(SPACE)[2] + SPACE + updatedDate.split(SPACE)[3];
                    logger.info("To Date: {}", updatedDate);
                    logger.info("Difference");
                    logger.info("Year: {}", ((DatePanel) calculator.getDatePanel()).getYearsDifferenceLabel().getText());
                    logger.info("Month: {}", ((DatePanel) calculator.getDatePanel()).getMonthsDifferenceLabel().getText());
                    logger.info("Weeks: {}", ((DatePanel) calculator.getDatePanel()).getWeeksDifferenceLabel().getText());
                    logger.info("Days: {}", ((DatePanel) calculator.getDatePanel()).getDaysDifferenceLabel().getText());
                }
                else // dateOperation == ADD_SUBTRACT_DAYS
                {
                    boolean isAddSelected = ((DatePanel) calculator.getDatePanel()).getAddRadioButton().isSelected();
                    if (isAddSelected) logger.info("Add Selected");
                    else               logger.info("Subtract Selected");
                    var addSubYears = ((DatePanel) calculator.getDatePanel()).getYearsTextField().getText();
                    if (!addSubYears.isBlank()) logger.info("Years: {}", addSubYears);
                    var addSubMonths = ((DatePanel) calculator.getDatePanel()).getMonthsTextField().getText();
                    if (!addSubMonths.isBlank()) logger.info("Months: {}", addSubMonths);
                    var addSubWeeks = ((DatePanel) calculator.getDatePanel()).getWeeksTextField().getText();
                    if (!addSubWeeks.isBlank()) logger.info("Weeks: {}", addSubWeeks);
                    var addSubDays = ((DatePanel) calculator.getDatePanel()).getDaysTextField().getText();
                    if (!addSubDays.isBlank()) logger.info("Days: {}", addSubDays);
                    logger.info("Result: " + ((DatePanel) calculator.getDatePanel()).getResultsLabel().getText());
                }
            }
            case VIEW_CONVERTER -> {
                logger.info("Converter: {}", calculator.getConverterPanel().getConverterType());
                logger.info("text field 1: {}", calculator.getConverterPanel().getTextField1().getText() + SPACE
                        + calculator.getConverterPanel().getUnitOptions1().getSelectedItem());
                logger.info("text field 2: {}", calculator.getConverterPanel().getTextField2().getText() + SPACE
                        + calculator.getConverterPanel().getUnitOptions2().getSelectedItem());
            }
        }
        logger.info("-------- End Confirm Results --------{}", calculator.addNewLines(1));
    }

    /**
     * This method is used at the start of any action button.
     * It signifies the start of an action for logging purposes.
     * @param buttonChoice the button that was pressed
     * @param logger the Logger needed to log the action
     */
    public static void logActionButton(String buttonChoice, Logger logger)
    {
        logger.info("Action for {} started", buttonChoice);
    }

    /**
     * This method is used at the start of any action button.
     * It signifies the start of an action for logging purposes.
     * @param actionEvent the button that created the action
     * @param logger the Logger needed to log the action
     */
    public static void logActionButton(ActionEvent actionEvent, Logger logger)
    {
        logger.info("Action for {} started", actionEvent.getActionCommand());
    }

    /**
     * Logs the operation being performed.
     */
    public static void logOperation(Logger logger, String[] values)
    {
        logger.info("{} {} {} = {}", values[0], values[2], values[1], values[3]);
    }

    /**
     * Logs the values stored in the calculator up to but not including
     * the specified index.
     * @param calculator the Calculator object containing the values
     * @param logger the Logger needed to log the values
     * @param upToIndex the number of indices to print from the values array
     */
    public static void logValues(Calculator calculator, Logger logger, int upToIndex)
    {
        for (int index = 0; index < upToIndex; index++)
        {
            String value = calculator.getValues()[index];
            logger.info("value[{}]: '{}'", index, value);
        }
    }

    /**
     * Logs the value at the current position in the calculator's values array.
     * @param calculator the Calculator object containing the values
     * @param logger the Logger needed to log the value
     */
    public static void logValuesAtPosition(Calculator calculator, Logger logger)
    {
        logger.debug("values[{}]: {}",
                calculator.getValuesPosition(),
                calculator.getValues()[calculator.getValuesPosition()]);
    }

    /**
     * Logs a failed operation due to a blank value.
     * @param operation the operation attempted
     * @param calculator the calculator
     * @param logger the Logger needed to log the warning
     */
    public static void logEmptyValue(String operation, Calculator calculator, Logger logger)
    {
        logger.warn("Attempted to perform {} but values[{}] is blank",
                operation, calculator.getValuesPosition());

    }

}
