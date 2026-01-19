package Panels;

import Calculators.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import static Types.CalculatorView.VIEW_SCIENTIFIC;

/**
 * ScientificPanel
 * <p>
 * This class contains components and actions
 * for the ScientificPanel of the Calculator.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class ScientificPanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = LogManager.getLogger(ScientificPanel.class.getSimpleName());

    private Calculator calculator;
    private GridBagConstraints constraints;
    private JPanel scientificPanel;
    private boolean isInitialized;

    /**************** CONSTRUCTORS ****************/
    /**
     * A zero argument constructor for creating a OLDProgrammerPanel
     */
    public ScientificPanel()
    {
        setName(VIEW_SCIENTIFIC.getValue());
        setConstraints(new GridBagConstraints());
        LOGGER.info("Empty Scientific panel created");
    }

    /**
     * The main constructor for creating a ScientificPanel
     * @param calculator the calculator to associate with the panel
     */
    public ScientificPanel(Calculator calculator)
    {
        this.calculator = calculator;
        setName(VIEW_SCIENTIFIC.getValue());
        setupScientificPanel();
        LOGGER.info("Scientific panel created with calculator");
    }

    /**************** START OF METHODS ****************/
    /**
     * Sets up the Scientific Panel
     */
    public void setupScientificPanel()
    {
        LOGGER.warn("IMPLEMENT SCIENTIFIC PANEL");
        LOGGER.debug("Set isInitialized to true once implemented");
    }

    /**************** ACTIONS ****************/
    /* TODO: Add actions here */

    /**************** GETTERS ****************/
    public Calculator getCalculator() { return calculator; }
    public GridBagConstraints getConstraints() { return constraints; }
    public JPanel getScientificPanel() { return scientificPanel; }
    public boolean isInitialized() { return isInitialized; }

    /**************** SETTERS ****************/
    public void setCalculator(Calculator calculator) { this.calculator = calculator; LOGGER.debug("Calculator set"); }
    public void setConstraints(GridBagConstraints constraints) { this.constraints = constraints; LOGGER.debug("Constraints set"); }

}
