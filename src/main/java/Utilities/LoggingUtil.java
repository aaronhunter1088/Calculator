package Utilities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import Calculators.Calculator;
import Panels.ConverterPanel;
import Panels.DatePanel;
import Panels.ProgrammerPanel;
import Types.CalculatorBase;
import Types.CalculatorByte;
import Types.CalculatorView;
import Types.DateOperation;
import org.apache.logging.log4j.Logger;

import static Types.CalculatorBase.BASE_BINARY;
import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.DateOperation.DIFFERENCE_BETWEEN_DATES;
import static Types.Texts.*;

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
        logger.info("Confirm Results: {}", message);
        logger.info("----------------");
        CalculatorView currentView = calculator.getCalculatorView();
        logger.info("View: {}", currentView);
        switch (calculator.getCalculatorView()) {
            case VIEW_BASIC, VIEW_PROGRAMMER -> {
                CalculatorBase currentBase = calculator.getCalculatorBase();
                logger.info("Base: {}", currentBase);
                CalculatorByte currentByte = calculator.getCalculatorByte();
                logger.info("Byte: {}", currentByte);
                if (BASE_BINARY == currentBase) logger.info("textPane: '{}'", ((ProgrammerPanel) calculator.getProgrammerPanel()).separateBits(textPaneValue));
                else logger.info("textPane: '{}'", textPaneValue);
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
                    logger.debug("addBool: {}", calculator.isAdding() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("subBool: {}", calculator.isSubtracting() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("mulBool: {}", calculator.isMultiplying() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("divBool: {}", calculator.isDividing() ? YES.toLowerCase() : NO.toLowerCase());
                    if (VIEW_PROGRAMMER == currentView)
                    {
                        logger.debug("orBool: {}", ((ProgrammerPanel) calculator.getProgrammerPanel()).isOr() ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("modBool: {}", ((ProgrammerPanel) calculator.getProgrammerPanel()).isModulus() ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("xorBool: {}", ((ProgrammerPanel) calculator.getProgrammerPanel()).isXor() ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("notBool: {}", ((ProgrammerPanel) calculator.getProgrammerPanel()).isNot() ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("andBool: {}", ((ProgrammerPanel) calculator.getProgrammerPanel()).isAnd() ? YES.toLowerCase() : NO.toLowerCase());
                        logger.debug("pemdasBool: {}", calculator.isPemdasActive() ? YES.toLowerCase() : NO.toLowerCase());
                    }
                    logger.debug("values[0]: '{}'", calculator.getValues()[0]);
                    logger.debug("values[1]: '{}'", calculator.getValues()[1]);
                    logger.debug("values[2]: '{}'", calculator.getValues()[2]);
                    logger.debug("values[3]: '{}'", calculator.getValues()[3]);
                    logger.debug("valuesPosition: {}", calculator.getValuesPosition());
                    logger.debug("isFirstNumber: {}", calculator.isFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.debug("isDotEnabled: {}", calculator.isDotPressed() ? YES.toLowerCase() : NO.toLowerCase());
                    // TODO: exclude if: v[vP] is empty, bc no need to print if an empty value is negative
                    // TODO: exclude if: the negative sign is not present
                    logger.debug("isNegative: {}", calculator.isNumberNegative() ? YES.toLowerCase() : NO.toLowerCase());

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
                    if (calculator.isAdding()) logger.info("addBool: {}", YES.toLowerCase());
                    if (calculator.isSubtracting()) logger.info("subBool: {}", YES.toLowerCase());
                    if (calculator.isMultiplying()) logger.info("mulBool: {}", YES.toLowerCase());
                    if (calculator.isDividing()) logger.info("divBool: {}", YES.toLowerCase());
                    if (calculator.getCalculatorView().equals(VIEW_PROGRAMMER))
                    {
                        if (calculator.getProgrammerPanel().isOr()) logger.info("orBool: {}", YES.toLowerCase());
                        if (calculator.getProgrammerPanel().isModulus()) logger.info("modBool: {}", YES.toLowerCase());
                        if (calculator.getProgrammerPanel().isXor()) logger.info("xorBool: {}", YES.toLowerCase());
                        if (calculator.getProgrammerPanel().isNot()) logger.info("notBool: {}", YES.toLowerCase());
                        if (calculator.getProgrammerPanel().isAnd()) logger.info("andBool: {}", YES.toLowerCase());
                        if (calculator.isPemdasActive()) logger.info("pemdasBool: {}", calculator.isPemdasActive() ? YES.toLowerCase() : NO.toLowerCase());
                    }
                    if (!calculator.getValues()[0].isEmpty()) logger.info("values[0]: '{}'", calculator.getValues()[0]);
                    if (!calculator.getValues()[1].isEmpty()) logger.info("values[1]: '{}'", calculator.getValues()[1]);
                    if (!calculator.getValues()[2].isEmpty()) logger.info("values[2]: '{}'", calculator.getValues()[2]);
                    if (!calculator.getValues()[3].isEmpty()) logger.info("values[3]: '{}'", calculator.getValues()[3]);
                    logger.info("valuesPosition: {}", calculator.getValuesPosition());
                    logger.info("isFirstNumber: {}", calculator.isFirstNumber() ? YES.toLowerCase() : NO.toLowerCase());
                    logger.info("isDotEnabled: {}", calculator.isDotPressed() ? YES.toLowerCase() : NO.toLowerCase());
                    // TODO: exclude if: v[vP] is empty, bc no need to print if an empty value is negative
                    // TODO: exclude if: the negative sign is not present
                    logger.info("isNegative: {}", calculator.isNumberNegative() ? YES.toLowerCase() : NO.toLowerCase());
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
                logger.info("Converter: {}", ((ConverterPanel) calculator.getConverterPanel()).getConverterType());
                logger.info("text field 1: {}", ((ConverterPanel) calculator.getConverterPanel()).getTextField1().getText() + SPACE
                        + ((ConverterPanel) calculator.getConverterPanel()).getUnitOptions1().getSelectedItem());
                logger.info("text field 2: {}", ((ConverterPanel) calculator.getConverterPanel()).getTextField2().getText() + SPACE
                        + ((ConverterPanel) calculator.getConverterPanel()).getUnitOptions2().getSelectedItem());
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
}
