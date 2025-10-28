package Panels;

import Calculators.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.Serial;

import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.CalculatorView.VIEW_SCIENTIFIC;

public class ScientificPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ScientificPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private boolean isInitialized;

    /* Constructors */
    /**
     * A zero argument constructor for creating a OLDProgrammerPanel
     */
    public ScientificPanel()
    {
        setName(VIEW_SCIENTIFIC.getValue());
        setupScientificPanel();
        isInitialized = false;
        LOGGER.info("Empty Scientific panel created");
    }

    public void setupScientificPanel()
    {
        LOGGER.warn("IMPLEMENT SCIENTIFIC PANEL");
        LOGGER.debug("Set isInitialized to true once implemented");
    }

    public boolean isInitialized() { return isInitialized; }
}
