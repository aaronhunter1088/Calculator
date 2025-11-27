package Utilities;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import Calculators.Calculator;
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
                        for (int i = 0; i < calculator.getMemoryPosition(); i++) {
                            logger.debug("memoryValues[{}]: {}", i, calculator.getMemoryValues()[i]);
                        }
                    }
                    logger.debug("values[0]: '{}'", calculator.getValues()[0]); // first number
                    logger.debug("values[1]: '{}'", calculator.getValues()[1]); // second number
                    logger.debug("values[2]: '{}'", calculator.getValues()[2]); // operator
                    logger.debug("values[3]: '{}'", calculator.getValues()[3]); // result
                    logger.debug("valuesPosition: {}", calculator.getValuesPosition());
                    logger.debug("obtaining first number: {}", calculator.isObtainingFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("is dot enabled: {}", calculator.isDecimalPressed() ? YES.toLowerCase() : NO.toLowerCase());
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
                    for (int i = 0; i <= calculator.getValuesPosition(); i++) {
                        logger.info("values[{}]: '{}'", i, calculator.getValues()[i]);
                    }
                    if (!calculator.getValues()[2].isEmpty()) logger.info("values[2]: '{}'", calculator.getValueAt(2));
                    if (!calculator.getValues()[3].isEmpty()) logger.info("values[3]: '{}'", calculator.getValueAt(3));
                    logger.info("valuesPosition: {}", calculator.getValuesPosition());
                    logger.info("obtaining first number: {}", calculator.isObtainingFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.info("is dot enabled: {}", calculator.isDecimalPressed() ? YES.toLowerCase() : NO.toLowerCase());
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
                LocalDateTime date = LocalDateTime.of(calculator.getDatePanel().getTheDateFromTheFromDate(), LocalTime.now());
                var updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                var capitalizedDay = updatedDate.split(SPACE)[0].toUpperCase();
                var capitalizedMonth = updatedDate.split(SPACE)[1].toUpperCase();
                updatedDate = capitalizedDay + SPACE + capitalizedMonth + SPACE + updatedDate.split(SPACE)[2] + SPACE + updatedDate.split(SPACE)[3];
                logger.info("From Date: {}", updatedDate);
                if (currentDateOperation == DIFFERENCE_BETWEEN_DATES)
                {
                    date = LocalDateTime.of(calculator.getDatePanel().getTheDateFromTheToDate(), LocalTime.now());
                    updatedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
                    capitalizedDay = updatedDate.split(SPACE)[0].toUpperCase();
                    capitalizedMonth = updatedDate.split(SPACE)[1].toUpperCase();
                    updatedDate = capitalizedDay + SPACE + capitalizedMonth + SPACE + updatedDate.split(SPACE)[2] + SPACE + updatedDate.split(SPACE)[3];
                    logger.info("To Date: {}", updatedDate);
                    logger.info("Difference");
                    logger.info("Year: {}", calculator.getDatePanel().getYearsDifferenceLabel().getText());
                    logger.info("Month: {}", calculator.getDatePanel().getMonthsDifferenceLabel().getText());
                    logger.info("Weeks: {}", calculator.getDatePanel().getWeeksDifferenceLabel().getText());
                    logger.info("Days: {}", calculator.getDatePanel().getDaysDifferenceLabel().getText());
                }
                else // dateOperation == ADD_SUBTRACT_DAYS
                {
                    boolean isAddSelected = calculator.getDatePanel().getAddRadioButton().isSelected();
                    if (isAddSelected) logger.info("Add Selected");
                    else               logger.info("Subtract Selected");
                    var addSubYears = calculator.getDatePanel().getYearsTextField().getText();
                    if (!addSubYears.isBlank()) logger.info("Years: {}", addSubYears);
                    var addSubMonths = calculator.getDatePanel().getMonthsTextField().getText();
                    if (!addSubMonths.isBlank()) logger.info("Months: {}", addSubMonths);
                    var addSubWeeks = calculator.getDatePanel().getWeeksTextField().getText();
                    if (!addSubWeeks.isBlank()) logger.info("Weeks: {}", addSubWeeks);
                    var addSubDays = calculator.getDatePanel().getDaysTextField().getText();
                    if (!addSubDays.isBlank()) logger.info("Days: {}", addSubDays);
                    logger.info("Result: " + calculator.getDatePanel().getResultsLabel().getText());
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
        logger.info("--- End Confirm Results ---{}", NEWLINE);
    }

    /**
     * This method is used at the start of any action button.
     * It signifies the start of an action for logging purposes.
     * @param buttonChoice the button that was pressed
     * @param logger the Logger needed to log the action
     */
    public static void logActionButton(String buttonChoice, Logger logger)
    { logger.info("Action for {} started", buttonChoice); }

    /**
     * This method is used at the start of any action button.
     * It signifies the start of an action for logging purposes.
     * @param actionEvent the button that created the action
     * @param logger the Logger needed to log the action
     */
    public static void logActionButton(ActionEvent actionEvent, Logger logger)
    { logger.info("Action for {} started", actionEvent.getActionCommand()); }

    public static void logException(Exception e, Logger logger)
    { logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e); }

    /**
     * Logs the operation being performed.
     */
    public static void logOperation(Logger logger, Calculator calculator)
    {
        switch (calculator.getValueAt(2))
        {
            case PERCENT -> logger.debug("{} / {} = {}%", calculator.getAppropriateValue(), 100, calculator.getValueAt(3));
            case SQUARE_ROOT -> logger.debug("{} squared = {}", calculator.getAppropriateValue(), calculator.getValueAt(3));
            case SQUARED -> logger.debug("{} squared = {}", calculator.getAppropriateValue(), calculator.getValueAt(3));
            case FRACTION -> logger.info("{} / {} = {}", 1, calculator.getAppropriateValue(), calculator.textPaneContainsBadText() ? calculator.getBadText(EMPTY) : calculator.getValueAt(3));
            case NEGATE -> logger.info("{} negated is {}", calculator.getAppropriateValue(), calculator.getValueAt(3));
            case MEMORY_ADDITION -> logger.info("{} + {} = {}", calculator.getMemoryValues()[calculator.getMemoryPosition()-1], calculator.getAppropriateValue(), calculator.getValueAt(3));
            case MEMORY_SUBTRACTION -> logger.info("{} - {} = {}", calculator.getMemoryValues()[calculator.getMemoryPosition()-1], calculator.getAppropriateValue(), calculator.getValueAt(3));
            default -> logger.info("{} {} {} = {}", calculator.getValueAt(0), calculator.getValueAt(2), calculator.getValueAt(1), calculator.getValueAt(3));
        }
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
     * Logs the current value in the text pane of the calculator.
     * @param calculator the Calculator object containing the text pane
     * @param logger the Logger needed to log the text pane value
     */
    public static void logValueInTextPane(Calculator calculator, Logger logger)
    { logger.debug("textPane: {}", calculator.getTextPaneValue()); }

    /**
     * Logs a failed operation due to a blank value.
     * @param operation the operation attempted
     * @param calculator the calculator
     * @param logger the Logger needed to log the warning
     */
    public static void logEmptyValue(String operation, Calculator calculator, Logger logger)
    { logger.warn("Attempted to perform {} but values[{}] is empty", operation, calculator.getValuesPosition()); }

    /**
     * Logs a warning when the user attempts to use a button
     * but there is no number, so the textPane value is used instead.
     * @param buttonChoice the button the user pushed
     * @param calculator the calculator
     * @param logger the Logger needed to log the warning
     */
    public static void logUseTextPaneValueWarning(Calculator calculator, Logger logger, String buttonChoice)
    { logger.warn("The user pushed {} but there is no number. Using textPane value: '{}'", buttonChoice, calculator.getTextPaneValue()); }

}
