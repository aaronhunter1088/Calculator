package Panels;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.Serial;

public class ScientificPanel extends JPanel
{
    private static final Logger LOGGER = LogManager.getLogger(ScientificPanel.class.getSimpleName());
    @Serial
    private static final long serialVersionUID = 4L;
    private boolean isInitialized;

    public ScientificPanel()
    {
        super();
        LOGGER.warn("Implement Scientific Panel...");
    }

    public boolean isInitialized() { return isInitialized; }
}
