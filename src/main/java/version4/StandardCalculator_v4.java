package version4;

import com.apple.eawt.Application;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.runtime.directive.Parse;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;

import static version4.CalcType_v4.*;
import static version4.ConverterType_v4.*;

public class StandardCalculator_v4 extends Calculator_v4
{
    private static Logger LOGGER;
    static
    {
        LOGGER = LogManager.getLogger(StandardCalculator_v4.class);
    }

	private static final long serialVersionUID = 1L;
	
	/*
	* All standard calculators have the same:
    * menu bar
    * addition button
    * subtraction button
    * multiplication button
    * division button
    * equals button
    * negate button
    */
    protected JMenuBar bar;
	final protected JButton buttonAdd = new JButton("+");
	final protected JButton buttonSubtract = new JButton("-");
    final protected JButton buttonMultiply = new JButton("*");
    final protected JButton buttonDivide = new JButton("/");
    final protected JButton buttonEquals = new JButton("=");
    final protected JButton buttonNegate = new JButton("\u00B1");

    public StandardCalculator_v4() throws Exception
    {
        this(BASIC, null);
    }

    /**
     * This constructor is used to create a calculator with a specific panel
     * @param calcType the type of calculator to create. sets the title
     */
    public StandardCalculator_v4(CalcType_v4 calcType) throws Exception
    {
        this(calcType, null);
    }

    /**
     * This constructor is used to create a calculator with a specific converter panel
     * @param calcType the type of calculator to create. sets the title
     */
    public StandardCalculator_v4(CalcType_v4 calcType, ConverterType_v4 converterType) throws Exception
    {
        super(StringUtils.isBlank(calcType.getName()) ? CalcType_v4.BASIC.getName() : calcType.getName()); // default title is Basic
        setupMenuBar();
        setCurrentPanel(determinePanelType(calcType, converterType));
        setupStandardCalculator();
        setCalcType(calcType);
        setImageIcons();
        // This sets the icon we see when we run the GUI. If not set, we will see the jar icon.
        Application.getApplication().setDockIconImage(createImageIcon("src/main/resources/images/calculatorOriginal.jpg").getImage());
        setIconImage(calculatorImage1.getImage());
        setMinimumSize(getCurrentPanel().getSize());
        pack();
        setVisible(true);
    }

    /************* Start of methods here ******************/
	public void setupMenuBar()
    {
        LOGGER.info("Inside setupMenuBar()");
        // Menu Bar and Menu Choices and each menu options
        setBar(new JMenuBar());
        setJMenuBar(getBar()); // add menu bar to application

        // Start 4 Menu Choices
        JMenu lookMenu = new JMenu("Look");
        JMenu viewMenu = new JMenu("View");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // commonalities
        lookMenu.setFont(font);
        lookMenu.setName("Look");
        viewMenu.setFont(font);
        viewMenu.setName("View");
        editMenu.setFont(font);
        editMenu.setName("Edit");
        helpMenu.setFont(font);
        helpMenu.setName("Help");

        // Look menu options
            JMenuItem metal = new JMenuItem("Metal");
            JMenuItem system = new JMenuItem("System");
            JMenuItem windows = new JMenuItem("Windows");
            JMenuItem motif = new JMenuItem("Motif");
            JMenuItem gtk = new JMenuItem("GTK");

            // functions
            metal.addActionListener(action -> {
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(this);
                    super.pack();
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error(e.getMessage());
                }
            });
            system.addActionListener(action -> {
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.system.SystemLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(this);
                    super.pack();
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error(e.getMessage());
                }
            });
            windows.addActionListener(action -> {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(this);
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error(e.getMessage());
                }
            });
            motif.addActionListener(action -> {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(this);
                    super.pack();
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error(e.getMessage());
                }
            });
            gtk.addActionListener(action -> {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(this);
                    super.pack();
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error(e.getMessage());
                }
            });

            // add options to Look menu
            lookMenu.add(metal);
            lookMenu.add(motif);
            String os = System.getProperty("os.name");
            if (!StringUtils.contains(os.toLowerCase(), "Mac".toLowerCase()))
            {
                lookMenu.add(windows);
                lookMenu.add(system);
                lookMenu.add(gtk);
            }
        getBar().add(lookMenu);

        // View menu options
            JMenuItem basic = new JMenuItem(CalcType_v4.BASIC.getName());
            JMenuItem programmer = new JMenuItem(CalcType_v4.PROGRAMMER.getName());
            JMenuItem dates = new JMenuItem(CalcType_v4.DATE.getName());
            JMenu converterMenu = new JMenu(CONVERTER.getName());

            // commonalities
            basic.setFont(font);
            basic.setName(BASIC.getName());
            programmer.setFont(font);
            programmer.setName(PROGRAMMER.getName());
            dates.setFont(font);
            dates.setName(DATE.getName());
            converterMenu.setFont(font);
            converterMenu.setName(CONVERTER.getName());

            // functions
            basic.addActionListener(action -> {
                try
                {
                    JPanel panel = new JPanelBasic_v4(this);
                    performTasksWhenChangingJPanels(panel, BASIC);
                }
                catch (CalculatorError_v4 e)
                {
                    LOGGER.error("Couldn't change to JPanelBasic_v4 because {}", e.getMessage());
                }
            });
            programmer.addActionListener(action -> {
                try
                {
                    JPanel panel = new JPanelProgrammer_v4(this);
                    performTasksWhenChangingJPanels(panel, PROGRAMMER);
                    confirm("Finished performTasksWhenChangingJPanels");
                }
                catch (CalculatorError_v4 e)
                {
                    LOGGER.error("Couldn't change to JPanelProgrammer_v4 because {}", e.getMessage());
                }
            });
            dates.addActionListener(action -> {
                try
                {
                    JPanel panel = new JPanelDate_v4(this);
                    performTasksWhenChangingJPanels(panel, DATE);
                    setCalcType(CalcType_v4.DATE);
                }
                catch (ParseException | CalculatorError_v4 e)
                {
                    LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
                }
            });

            // add view options to viewMenu
            viewMenu.add(basic);
            viewMenu.add(programmer);
            viewMenu.add(dates);
                viewMenu.addSeparator();
            viewMenu.add(converterMenu);

            // options for converterMenu
            JMenuItem angleConverter = new JMenuItem(ANGLE.getName());
            JMenuItem areaConverter = new JMenuItem(ConverterType_v4.AREA.getName());

            // commonalities
            angleConverter.setFont(font);
            areaConverter.setFont(font);

            // functions
            angleConverter.addActionListener(action -> {
                try
                {
                    JPanel panel = new JPanelConverter_v4(this, ANGLE);
                    performTasksWhenChangingJPanels(panel, CONVERTER);
                }
                catch (ParseException | CalculatorError_v4 e)
                {
                    LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
                }
            });
            areaConverter.addActionListener(action -> {
                try
                {
                    JPanel panel = new JPanelConverter_v4(this, ConverterType_v4.AREA);
                    performTasksWhenChangingJPanels(panel, CONVERTER);
                }
                catch (ParseException | CalculatorError_v4 e)
                {
                    LOGGER.error("Couldn't change to JPanelDate_v4 because {}", e.getMessage());
                }
            });

            // add JMenuItems to converterMenu
            converterMenu.add(angleConverter);
            converterMenu.add(areaConverter);
            // END NEW OPTION: Converter
        getBar().add(viewMenu); // add viewMenu to menu bar

        // Edit Menu options
            JMenuItem copyItem = new JMenuItem("Copy");
            JMenuItem pasteItem = new JMenuItem("Paste");
            JMenu historyMenu = new JMenu("History");
            JMenuItem copyHistoryItem = new JMenuItem("Copy History");
            JMenuItem editItem = new JMenuItem("Edit");
            JMenuItem cancelEditItem = new JMenuItem("Cancel Edit");
            JMenuItem clearItem = new JMenuItem("Clear");

            // commonalities
            copyItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
            copyItem.setFont(font);
            copyItem.setName("Copy");
            copyItem.addActionListener(action -> {
                values[3] = textArea.getText(); // to copy
                textarea = new StringBuffer().append(textArea.getText());
                confirm();
            });

            pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
            pasteItem.setFont(font);
            pasteItem.setName("Paste");
            pasteItem.addActionListener(action -> {
                if (values[3].equals(""))
                    LOGGER.info("Temp[3] is null");
                else
                    LOGGER.info("temp[3]: " + values[3]);
                textArea.setText(values[3]); // to paste
                values[valuesPosition] = textArea.getText();
                textarea = new StringBuffer().append(textArea.getText());
                confirm();
            });

            //historyMenu.setMneonic(null);
            historyMenu.setFont(font);
            historyMenu.setName("History");
            historyMenu.addActionListener(null);
            // second menu starts here
            copyHistoryItem.setFont(font);
            copyHistoryItem.setName("Copy History");
            editItem.setFont(font);
            editItem.setName("Edit");
            cancelEditItem.setFont(font);
            cancelEditItem.setName("Cancel Edit");
            clearItem.setFont(font);
            clearItem.setName("Clear");

            // add
            historyMenu.add(copyHistoryItem);
            historyMenu.add(editItem);
            historyMenu.add(cancelEditItem);
            historyMenu.add(clearItem);
            editMenu.add(copyItem);
            editMenu.add(pasteItem);
            editMenu.addSeparator();
            editMenu.add(historyMenu);
        getBar().add(editMenu); // add editMenu to menu bar

        // Help Options
            JMenuItem viewHelpItem = new JMenuItem("View Help");
            JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");

            // commonalities
            viewHelpItem.setName("viewHelpItem");
            viewHelpItem.setFont(font);
            aboutCalculatorItem.setName("about");
            aboutCalculatorItem.setFont(font);

            // add
            helpMenu.add(viewHelpItem);
            helpMenu.addSeparator();
            helpMenu.add(aboutCalculatorItem);
            getBar().add(helpMenu); // add helpMenu to menu bar
            // functionality
            // NEW HELP MENU HERE
            LOGGER.debug("Menu Choices count: " + getBar().getMenuCount());
            for(int i=0; i < getBar().getMenuCount(); i++) {
                JMenu menuOption = getBar().getMenu(i);
                JMenuItem valueForThisMenuOption = null;
                if (menuOption.getName() != null && menuOption.getName().equals("Help")) {
                    // get the options. remove viewHelpItem
                    for(int j=0; j<menuOption.getItemCount(); j++) {
                        LOGGER.debug("Number of options in Menu: " + menuOption.getItemCount());
                        valueForThisMenuOption = menuOption.getItem(j);
                        if (valueForThisMenuOption != null) {
                            if (valueForThisMenuOption.getName() != null && valueForThisMenuOption.getName().equals("viewHelpItem")) {
                                break;
                            }
                        }
                    }
                    // remove old option
                    menuOption.remove(valueForThisMenuOption);
                    // set up new viewHelpItem option
                    helpMenu.add(viewHelpItem,0);
                    viewHelpItem.addActionListener(action -> {
                        String COPYRIGHT = "\u00a9";
                        iconLabel = new JLabel();
                        JPanel iconPanel = new JPanel(new GridBagLayout());
                        iconPanel.add(iconLabel);
                        textLabel = new JLabel("<html>Apple MacBook Air "
                                + "Version 3.0.1<br>"
                                + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
                                + "Mac OS mojave and its user interface are protected by trademark and all other<br>"
                                + "pending or existing intellectual property right in the United States and other<br>"
                                + "countries/regions."
                                + "<br><br><br>"
                                + "This product is licensed under the License Terms to:<br>"
                                + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
                        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                        JPanel mainPanel = new JPanel();
                        mainPanel.setBackground(Color.white);
                        mainPanel.add(iconLabel);
                        mainPanel.add(textLabel);
                        JOptionPane.showMessageDialog(StandardCalculator_v4.this,
                                mainPanel, "Help", JOptionPane.PLAIN_MESSAGE);
                    });
                    //break; //?? for just changing one option could be ok. issue maybe if changing other options
                }
            }
            // END NEW HELP MENU HERE
            aboutCalculatorItem.addActionListener(action -> {
                String COPYRIGHT = "\u00a9";
                JPanel iconPanel = new JPanel(new GridBagLayout() );
                iconPanel.add(iconLabel);
                textLabel = new JLabel("<html>Apple MacBook Air"
                        + "Version 3.0.1 (Build 1)<br>"
                        + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
                        + "Mac OS mojave and its user interface are<br>"
                        + "protected by trademark and all other pending or existing intellectual property<br>"
                        + "right in the United States and other countries/regions."
                        + "<br><br><br>"
                        + "This product is licensed under the License Terms to:<br>"
                        + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
                textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

                JPanel mainPanel = new JPanel();
                mainPanel.add(iconLabel);
                mainPanel.add(textLabel);
                JOptionPane.showMessageDialog(StandardCalculator_v4.this, mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);

            });

        // End All Menu Choices
        LOGGER.info("Finished. Leaving setupMenuBar()");
    } // end public setMenuBar
	public void setupStandardCalculator()
    {
        getButtonAdd().setFont(font);
        getButtonAdd().setPreferredSize(new Dimension(35, 35) );
        getButtonAdd().setBorder(new LineBorder(Color.BLACK));
        getButtonAdd().setEnabled(true);
        getButtonAdd().addActionListener(action -> {
            performAdditionButtonActions(action);
        });
        
        getButtonSubtract().setFont(font);
        getButtonSubtract().setPreferredSize(new Dimension(35, 35) );
        getButtonSubtract().setBorder(new LineBorder(Color.BLACK));
        getButtonSubtract().setEnabled(true);
        getButtonSubtract().addActionListener(action -> {
            performSubtractionButtonActions(action);
        });

        getButtonMultiply().setFont(font);
        getButtonMultiply().setPreferredSize(new Dimension(35, 35) );
        getButtonMultiply().setBorder(new LineBorder(Color.BLACK));
        getButtonMultiply().setEnabled(true);
        getButtonMultiply().addActionListener(action -> {
            performMultiplicationActions(action);
        });
        
        getButtonDivide().setFont(font);
        getButtonDivide().setPreferredSize(new Dimension(35, 35) );
        getButtonDivide().setBorder(new LineBorder(Color.BLACK));
        getButtonDivide().setEnabled(true);
        getButtonDivide().addActionListener(action -> {
            performDivideButtonActions(action);
        });
        
        getButtonEquals().setFont(font);
        getButtonEquals().setPreferredSize(new Dimension(35, 70) );
        getButtonEquals().setBorder(new LineBorder(Color.BLACK));
        getButtonEquals().setEnabled(true);
        getButtonEquals().addActionListener(action -> {
            try
            {
                performButtonEqualsActions();
            } catch (Exception calculator_v3Error)
            {
                calculator_v3Error.printStackTrace();
            }
        });
        // TODO: does not work. fix
        getButtonEquals().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    LOGGER.info("Return button Pressed");
                    int code = e.getKeyCode();
                    LOGGER.info("   Code: " + KeyEvent.getKeyText(code));
                    performButtonEqualsActions();
                } catch (Exception calculator_v3Error) {
                    calculator_v3Error.printStackTrace();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        getButtonNegate().setFont(font);
        getButtonNegate().setPreferredSize(new Dimension(35, 35) );
        getButtonNegate().setBorder(new LineBorder(Color.BLACK));
        getButtonNegate().setEnabled(true);
        getButtonNegate().addActionListener(action -> {
            performNegateButtonActions(action);
        });
        setCalcType(CalcType_v4.BASIC);

        add(getCurrentPanel());
	}

    public JPanel determinePanelType(CalcType_v4 calcType, ConverterType_v4 converterType) throws ParseException, CalculatorError_v4
    {
        if (calcType == null) {
            calcType = getCalcType();
            LOGGER.debug("Reset calcType from: null to: " + calcType);
        }
        if (calcType == BASIC) {
            return new JPanelBasic_v4(this);
        } else if (calcType == PROGRAMMER) {
            return new JPanelProgrammer_v4(this);
        } else if (calcType == SCIENTIFIC) {
            return new JPanelScientific_v4();
        } else if (calcType == DATE) {
            return new JPanelDate_v4(this);
        } else if (calcType == CONVERTER) {
            if (converterType == ANGLE) {
                return new JPanelConverter_v4(this, ANGLE);
            }
            else if (converterType == AREA) {
                return new JPanelConverter_v4(this, AREA);
            } else {
                LOGGER.error("Add the specific converter panel now");
                throw new CalculatorError_v4("Add the specific converter panel now");
            }
        } else {
            LOGGER.error("Unknown calcType: " + calcType);
            throw new CalculatorError_v4("Unknown calcType: " + calcType);
        }
    }

	public void performTasksWhenChangingJPanels(JPanel currentPanel, CalcType_v4 calcType_v4) throws CalculatorError_v4
    {
	    setTitle(calcType_v4.getName());
        JPanel oldPanel = updateJPanel(currentPanel);
        //String nameOfOldPanel = getNameOfPanel();
        //if (StringUtils.isBlank(nameOfOldPanel)) throw new CalculatorError_v4("Name of OldPanel not found when switching Panels");
        // don't switch calc_types here; later...
        if (getCurrentPanel() instanceof JPanelBasic_v4)
        {
            ((JPanelBasic_v4)getCurrentPanel()).performBasicCalculatorTypeSwitchOperations(oldPanel);
        }
        else if (getCurrentPanel() instanceof JPanelProgrammer_v4)
        {
            ((JPanelProgrammer_v4)getCurrentPanel()).performProgrammerCalculatorTypeSwitchOperations();
        }
        else if (getCurrentPanel() instanceof JPanelDate_v4)
        {

        }
        else if (getCurrentPanel() instanceof JPanelConverter_v4)
        {
            ((JPanelConverter_v4)currentPanel).getTextField1().requestFocusInWindow();
        }
        repaint();
        pack();
    }

    public void performAdditionButtonActions(ActionEvent action)
    {
        LOGGER.info("AddButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform addition. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText())
            {
                textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + textarea);
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
                if (memorySwitchBool == true) {
                    memoryValues[memoryPosition] += " + ";
                }
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                addBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) { //
                LOGGER.error("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;

            //performValuesConversion();
            confirm();
        }
    }

    public void addition()
    {
    	LOGGER.info("value[0]: '" + values[0] + "'");
    	LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) + Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " + " + values[1] + " = " + result);
        values[0] = Double.toString(result);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
        	LOGGER.info("We have a whole positive number");
        	textarea = clearZeroesAtEnd(String.valueOf(result));
            values[0] = textarea.toString(); // textarea changed to whole number, or int
            textArea.setText("\n" + textarea.toString());
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        { // if double == double, keep decimal and number afterwards
        	LOGGER.info("We have a decimal");
            if (Double.parseDouble(values[0]) < 0.0 )
            {
                values[0] = formatNumber(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: '" + textarea + "'");
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: '" + textarea + "'");
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"] '" + values[valuesPosition] + "'");
            }
            else
            {
                textArea.setText("\n" + formatNumber(values[0]));
                values[0] = formatNumber(values[0]);
            }
        }
    }

    public void addition(CalcType_v4 calcType_v4)
    {
        if (getCalcType().equals(CalcType_v4.BASIC)) {
            addition();
        }
        else if (getCalcType().equals(CalcType_v4.PROGRAMMER)) {
            // convert values
//            convertFromTypeToType("Binary", "Decimal");
            // run add
            addition();
            textarea = new StringBuffer().append(convertFromTypeToTypeOnValues("Decimal", "Binary", String.valueOf(textarea))[0]);
            textArea.setText(addNewLineCharacters(1)+textarea);
        }
    }

    public void performSubtractionButtonActions(ActionEvent action)
    {
        LOGGER.info("SubtractButtonHandler class started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform subtraction. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textArea.getText().equals("") && !textAreaContainsBadText()) {
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + values[valuesPosition]);
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                subBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. next number is negative...");
                negatePressed = true;
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            numberIsNegative = false;
            performValuesConversion();
            confirm();
        }
    }
    public void subtract()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) - Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " - " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(Double.toString(result));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else
        {// if double == double, keep decimal and number afterwards
            if (Double.parseDouble(values[0]) < 0.0 ) {
                values[0] = formatNumber(values[0]);
                textarea = new StringBuffer().append(values[0]);
                LOGGER.info("textarea: " + textarea);
                textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                LOGGER.info("textarea: " + textarea);
                textArea.setText("\n" + textarea + "-"); // update textArea
                LOGGER.info("temp["+valuesPosition+"]: " + values[valuesPosition]);
            }
            else {
                textArea.setText("\n" + formatNumber(values[0]));
            }
        }
    }
    public void subtract(CalcType_v4 calcType_v4)
    {
	    if (calcType_v4.equals(CalcType_v4.BASIC)) {
	        subtract();
        }
	    else if (calcType_v4.equals(CalcType_v4.PROGRAMMER)) {
//            convertFromTypeToType("Binary", "Decimal");
            subtract();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performMultiplicationActions(ActionEvent action)
    {
        LOGGER.info("performMultiplicationActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform multiplication. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + action.getActionCommand()); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false &&
                    divBool == false && !textArea.getText().equals("") &&
                    !textArea.getText().equals("Invalid textarea") &&
                    !textArea.getText().equals("Cannot divide by 0")) {
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText("\n" + " " + buttonChoice + " " + values[valuesPosition]);
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals("")) {
                addition(getCalcType());
                addBool = resetOperator(addBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("")) {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                mulBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText()) {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                LOGGER.info("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void multiply()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        double result = Double.parseDouble(values[0]) * Double.parseDouble(values[1]); // create result forced double
        LOGGER.info(values[0] + " * " + values[1] + " = " + result);
        values[0] = Double.toString(result); // store result
        LOGGER.info("addBool: " + addBool);
        LOGGER.info("subBool: " + subBool);
        LOGGER.info("mulBool: " + mulBool);
        LOGGER.info("divBool: " + divBool);
        if (result % 1 == 0 && !values[0].contains("E") && !isNegativeNumber(values[0]))
        {
            textArea.setText(values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
            values[0] = textarea.toString(); // update storing
            textArea.setText("\n" + values[0]);
        }
        else if (result % 1 == 0 && isNegativeNumber(values[0]))
        {
            LOGGER.info("We have a whole negative number");
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textarea = clearZeroesAtEnd(textarea.toString());
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
            values[0] = textarea.toString();
            dotButtonPressed = false;
            buttonDot.setEnabled(true);
        }
        else if (values[0].contains("E"))
        {
            textArea.setText("\n" + values[0]);
            textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
            values[0] = textarea.toString(); // update storing
        }
        else if (isNegativeNumber(values[0]))
        {
            textarea = new StringBuffer().append(convertToPositive(values[0]));
            textArea.setText(addNewLineCharacters(1)+textarea+"-");
            textarea = new StringBuffer().append(convertToNegative(values[0]));
        }
        else
        {// if double == double, keep decimal and number afterwards
            textArea.setText("\n" + formatNumber(values[0]));
        }
    }
    public void multiply(CalcType_v4 calcType_v4)
    {
        if (calcType_v4.equals(CalcType_v4.BASIC)) {
            multiply();
        }
        else if (calcType_v4.equals(CalcType_v4.PROGRAMMER)) {
            //convertFromTypeToType("Binary", "Decimal");
            multiply();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
            updateTextareaFromTextArea();
        }
    }

    public void performDivideButtonActions(ActionEvent action)
    {
        LOGGER.info("performDivideButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot perform division. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
//            convertAllValuesToDecimal();
            if (addBool == false && subBool == false && mulBool == false && divBool == false &&
                    !textAreaContainsBadText())
            {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textarea = new StringBuffer().append(values[valuesPosition] + " " + buttonChoice);
                textArea.setText(addNewLineCharacters(1) + " " + buttonChoice + " " + values[valuesPosition]);
                LOGGER.info("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                LOGGER.info("temp["+valuesPosition+"] is "+values[valuesPosition]+ " after addButton pushed"); // confirming proper textarea before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true && !values[1].equals(""))
            {
                addition(getCalcType());
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (subBool == true && !values[1].equals("")) {
                subtract(getCalcType());
                subBool = resetOperator(subBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (mulBool == true && !values[1].equals("")) {
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (divBool == true && !values[1].equals("") & !values[1].equals("0"))
            {
                divide(getCalcType());
                divBool = resetOperator(divBool);
                divBool = true;
                textArea.setText("\n" + buttonChoice + " " + textarea);
            }
            else if (textAreaContainsBadText())  {
                textArea.setText(buttonChoice + " " +  values[0]); // "userInput +" // temp[valuesPosition]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                valuesPosition++; // increase valuesPosition for storing textarea
            }
            else if (addBool == true || subBool == true || mulBool == true || divBool == true)
            {
                LOGGER.info("already chose an operator. choose another number.");
            }
            buttonDot.setEnabled(true);
            dotButtonPressed = false;
            dotButtonPressed = false;
            performValuesConversion();
            confirm();
        }
    }
    public void divide()
    {
        LOGGER.info("value[0]: '" + values[0] + "'");
        LOGGER.info("value[1]: '" + values[1] + "'");
        if (!values[1].equals("0"))
        {
            // if the second number is not zero, divide as usual
            double result = Double.parseDouble(values[0]) / Double.parseDouble(values[1]); // create result forced double
            // TODO: fix logic here to tie in with existing logic above
            if (getCalcType().equals(CalcType_v4.PROGRAMMER)) {
                result = Double.valueOf(String.valueOf(clearZeroesAtEnd(String.valueOf(result))));
            } // PROGRAMMER mode only supports whole numbers at this time

            LOGGER.info(values[0] + " / " + values[1] + " = " + result);
            values[0] = Double.toString(result); // store result
            LOGGER.info("addBool: " + addBool);
            LOGGER.info("subBool: " + subBool);
            LOGGER.info("mulBool: " + mulBool);
            LOGGER.info("divBool: " + divBool);
            if (Double.parseDouble(values[0]) % 1 == 0 && !isNegativeNumber(values[0]))
            {
                // if int == double, cut off decimal and zero
                textArea.setText("\n" + values[0]);
                textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", ""));
                textarea = new StringBuffer().append(textarea.substring(0, textarea.length()-2)); // textarea changed to whole number, or int
                values[0] = textarea.toString(); // update storing
                textArea.setText("\n" + values[0]);
                updateTextareaFromTextArea();
                if (Integer.parseInt(values[0]) < 0 ) {
                    textarea = new StringBuffer().append(textArea.getText().replaceAll("\\n", "")); // temp[2]
                    LOGGER.info("textarea: " + textarea);
                    textarea = new StringBuffer().append(textarea.substring(1, textarea.length()));
                    LOGGER.info("textarea: " + textarea);
                    textArea.setText("\n" + textarea + "-"); // update textArea
                    updateTextareaFromTextArea();
                    LOGGER.info("values["+valuesPosition+"]: " + values[valuesPosition]);
                }
            }
            else if (Double.parseDouble(values[0]) % 1 == 0 && isNegativeNumber(values[0]))
            {
                LOGGER.info("We have a whole negative number");
                textarea = new StringBuffer().append(convertToPositive(values[0]));
                textarea = clearZeroesAtEnd(textarea.toString());
                textArea.setText(addNewLineCharacters(1)+textarea+"-");
                textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                values[0] = textarea.toString();
                dotButtonPressed = false;
                buttonDot.setEnabled(true);
            }
            else {
                // if double == double, keep decimal and number afterwards
                textArea.setText("\n" + formatNumber(values[0]));
            }


        }
        else if (values[1].equals("0")) {
            String result = "0";
            LOGGER.warn("Attempting to divide by zero. Cannot divide by 0!");
            textArea.setText("\nCannot divide by 0");
            values[0] = result;
            firstNumBool = true;
        }
    }
    public void divide(CalcType_v4 calcType_v4)
    {
        if (calcType_v4.equals(CalcType_v4.BASIC)) {
            divide();
        }
        else if (calcType_v4.equals(CalcType_v4.PROGRAMMER))
        {
//            convertFromTypeToType("Binary", "Decimal");
            divide();
            values[0] = convertFromTypeToTypeOnValues("Decimal","Binary", values[0])[0];
            textArea.setText("\n" + values[0]);
        }
    }

    public void performButtonEqualsActions()
    {
        LOGGER.info("performButtonEqualsActions");
        String buttonChoice = "=";
        LOGGER.info("button: " + buttonChoice); // print out button confirmation
        if (getCalcType() == CalcType_v4.BASIC)
        {
            if (addBool) {
                addition(getCalcType()); // addition();
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(getCalcType());
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(getCalcType());
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(getCalcType());
                divBool = resetOperator(divBool);
            }
        }
        // TODO: is this necessary. values should always be in DECIMAL form
        else if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonBin().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Binary", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonOct().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Octal", "Decimal", values[1])[0];
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonDec().isSelected())
            {
                getLOGGER().debug("Do nothing");
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonHex().isSelected())
            {
                values[0] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[0])[0];
                values[1] = convertFromTypeToTypeOnValues("Hexidecimal", "Decimal", values[1])[0];
            }

            if (orButtonBool)
            {
                ((JPanelProgrammer_v4)getCurrentPanel()).performOr();
                getTextArea().setText(addNewLineCharacters(1) + values[0]);
                performValuesConversion();

            }
            else if (isModButtonPressed())
            {
                LOGGER.info("Modulus result");
                ((JPanelProgrammer_v4)getCurrentPanel()).performModulus();
                // update values and textArea accordingly
                performValuesConversion();
                valuesPosition = 0;
                modButtonBool = false;
            }
            // after converting to decimal, perform same logic
            else if (addBool) {
                addition(CalcType_v4.BASIC); // forced addition of Basic type
                addBool = resetOperator(addBool);
            }
            else if (subBool){
                subtract(CalcType_v4.BASIC);
                subBool = resetOperator(subBool);
            }
            else if (mulBool){
                multiply(CalcType_v4.BASIC);
                mulBool = resetOperator(mulBool);
            }
            else if (divBool){
                divide(CalcType_v4.BASIC);
                divBool = resetOperator(divBool);
            }
        }

        //TODO: add more calculator types here

        if (values[0].equals("") && values[1].equals(""))
        {
            // if temp[0] and temp[1] do not have a number
            valuesPosition = 0;
        }
        else if (textAreaContainsBadText())
        {
            textArea.setText("=" + " " +  values[valuesPosition]); // "userInput +" // temp[valuesPosition]
            valuesPosition = 1;
            firstNumBool = true;
        }

        values[1] = ""; // this is not done in addition, subtraction, multiplication, or division
        values[3] = "";
        //updateTextareaFromTextArea();
        firstNumBool = true;
        dotButtonPressed = false;
        valuesPosition = 0;
        confirm("");
    }
    public void performNegateButtonActions(ActionEvent action)
    {
        LOGGER.info("performNegateButtonActions started");
        String buttonChoice = action.getActionCommand();
        if (values[0].contains("E"))
        {
            String errorMsg = "Cannot negate number. Number too big!";
            confirm(errorMsg);
        }
        else
        {
            LOGGER.info("button: " + buttonChoice); // print out button confirmation
            getTextArea().setText(getTextAreaWithoutNewLineCharacters());
            updateTextareaFromTextArea();
            if (!textarea.equals("")) {
                if (numberIsNegative) {
                    textarea = new StringBuffer().append(convertToPositive(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                    getTextArea().setText("\n"+textarea);

                }
                else {
                    getTextArea().setText("\n"+textarea+"-");
                    textarea = new StringBuffer().append(convertToNegative(textarea.toString()));
                    LOGGER.debug("textarea: " + textarea);
                }
            }
            values[valuesPosition] = textarea.toString();
            confirm("");
        }
    }

    // TODO: move/use method in Calculator
    public String formatNumber(String num)
    {
        DecimalFormat df = new DecimalFormat();
        if (!numberIsNegative) {
            if (num.length() == 2) df = new DecimalFormat("0.00");
            if (num.length() == 3) df = new DecimalFormat("0.000");
        } else {
            if (num.length() == 3) df = new DecimalFormat("0.0");
            if (num.length() == 4) df = new DecimalFormat("0.00");
            if (num.length() == 5) df = new DecimalFormat("0.000");
        }
        double number = Double.parseDouble(num);
        number = Double.valueOf(df.format(number));
        String numberAsStr = Double.toString(number);
        num = df.format(number);
        LOGGER.info("Formatted: " + num);
        if (numberAsStr.charAt(numberAsStr.length()-3) == '.' && numberAsStr.substring(numberAsStr.length()-3).equals(".00") ) {
            numberAsStr = numberAsStr.substring(0, numberAsStr.length()-3);
            LOGGER.info("Formatted again: " + num);
        }
        return numberAsStr;
    }
    /*
     * TODO: Change method to be a list of tasks that should
     * occur when we switch panels.
     *
     * method: performTasksWhenChangingJPanels
     *
     * tasks: set currentPanel
     * 		  set title of frame
     *        set the mode of the calculator
     *        perform setup tasks based on mode
     */

    /**
     * This method returns the name of the Panel based on the current set CalcType
     * @return String the name of the Panel based on the CalcType
     */
    public String getNameOfPanel()
    {
        String nameOfPanel = new String();
        switch (getCalcType())
        {
            case BASIC: nameOfPanel = CalcType_v4.BASIC.getName(); break;
            case PROGRAMMER: nameOfPanel = CalcType_v4.PROGRAMMER.getName(); break;
            case SCIENTIFIC: nameOfPanel = CalcType_v4.SCIENTIFIC.getName(); break;
            case DATE: nameOfPanel = CalcType_v4.DATE.getName(); break;
            default: LOGGER.error("Unknown calculator type");
        }
        return nameOfPanel;
    }

    /**
     * This method updates the JPanel to the one sent in
     * and returns the oldPanel
     * @param newPanel
     * @return
     */
    public JPanel updateJPanel(JPanel newPanel)
    {
        JPanel oldPanel = getCurrentPanel();
        remove(oldPanel);
        setCurrentPanel(newPanel);
        add(getCurrentPanel());
        return oldPanel;
    }

    @Deprecated
    public void convertAllValuesToDecimal()
    {
        if (getCalcType().equals(CalcType_v4.PROGRAMMER))
        {
            values = convertFromTypeToTypeOnValues("Binary", "Decimal", values);
        }
        // TODO: Add more CalcType_v3's here
    }

    @Deprecated
    public void convertAllValuesToBinary()
    {
        values = convertFromTypeToTypeOnValues("Decimal", "Binary", values);
//        values[1] = convertFromTypeToTypeOnValues("Decimal", "Binary", values[1])[0];
//        values[3] = convertFromTypeToTypeOnValues(CalcType_v3.DECIMAL.getName(), CalcType_v3.BINARY2.getName(), values[3])[0];
    }

    /**
     * This method returns true or false depending on if an operator was pushed or not
     * @return
     */
    public boolean determineIfMainOperatorWasPushed()
    {
        boolean result = false;
        if (isAddBool() || isSubBool() ||
            isMulBool() || isDivBool())
        {
            result = true;
        }
        return result;
    }

    /**
     * This method converts the memory values
     */
    public void convertMemoryValues()
    {
        String[] newMemoryValues = new String[10];
        int i = 0;
        if (getCalcType() == CalcType_v4.PROGRAMMER)
        {
            for(String numberInMemory : getMemoryValues())
            {
                newMemoryValues[i] = convertFromTypeToTypeOnValues(CalcType_v4.PROGRAMMER.getName(), CalcType_v4.BASIC.getName(), numberInMemory)[0];
                getLOGGER().debug("new number in memory is: " + newMemoryValues[i]);
                i++;
            }
        }
    }

    /**
     * This method takes CalcType Types, NOT BASES!
     * @param type1
     * @param type2
     * @param strings
     * @return
     */
    public String[] convertFromTypeToTypeOnValues(String type1, String type2, String... strings)
    {
        LOGGER.debug("convertFromTypeToTypeOnValues(from: '"+type1+"', "+ "to: '"+type2+"' + " + Arrays.toString(strings));
        String[] arrToReturn = new String[strings.length];
        int countOfStrings = 0;
        if (StringUtils.isEmpty(strings[0])) return new String[]{"", "", "", ""};
        else countOfStrings = 1;
        if (type1.equals(CalcType_v4.BASIC.getName()) && type2.equals(CalcType_v4.PROGRAMMER.getName()))
        {
            for(String str : Arrays.asList(strings))
            {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                int number = 0;
                try {
                    number = Integer.parseInt(str);
                    LOGGER.debug("number: " + number);
                    int i = 0;
                    while (i <= Integer.parseInt(str)) {
                        if (number % 2 == 0) {
                            sb.append("0");
                        } else {
                            sb.append("1");
                        }
                        if (number % 2 == 0 && number / 2 == 0) {
                            // 0r0
                            for(int k = i; k<getBytes(); k++) {
                                sb.append("0");
                            }
                            break;
                        } else if (number / 2 == 0 && number % 2 == 1) {
                            // 0r1
                            for(int k = i+1; k<getBytes(); k++) {
                                sb.append("0");
                            } break;
                        }
                        i++;
                        number /= 2;
                    }
                } catch (NumberFormatException nfe) { LOGGER.error(nfe.getMessage()); }
                sb = sb.reverse();
                String strToReturn = sb.toString();
                LOGGER.debug("convertFrom("+type1+")To("+type2+") = "+ sb);
                arrToReturn[countOfStrings-1] = strToReturn;
                countOfStrings++;
            }
        }
        else if (type1.equals(CalcType_v4.PROGRAMMER.getName()) && type2.equals(CalcType_v4.BASIC.getName()))
        {
            for(String str : Arrays.asList(strings))
            {
                LOGGER.debug("Converting str("+str+") or "+countOfStrings+"/"+strings.length);
                StringBuffer sb = new StringBuffer();
                sb.append(str);

                int appropriateLength = getBytes();
                LOGGER.debug("sb: " + sb);
                LOGGER.debug("appropriateLength: " + appropriateLength);

                if (sb.length() < appropriateLength)
                {
                    if (sb.length() == 0) { arrToReturn[(countOfStrings-1)] = ""; LOGGER.debug("arrToReturn["+(countOfStrings-1)+"]: '" + arrToReturn[countOfStrings-1] + "'"); countOfStrings++; continue; }
                    LOGGER.error("sb, '" + sb + "', is too short. adding missing zeroes");
                    // user had entered 101, which really is 00000101
                    // but they aren't showing the first 5 zeroes
                    int difference = appropriateLength - sb.length();
                    StringBuffer missingZeroes = new StringBuffer();
                    for (int i=0; i<difference; i++) {
                        missingZeroes.append("0");
                    }
                    sb.append(missingZeroes);
                    LOGGER.debug("sb: " + sb);
                }

                double result = 0.0;
                double num1 = 0.0;
                double num2 = 0.0;
                for(int i=0, k=appropriateLength-1; i<appropriateLength; i++, k--)
                {
                    num1 = Double.valueOf(String.valueOf(sb.charAt(i)));
                    num2 = Math.pow(2,k);
                    result = (num1 * num2) + result;
                }

                arrToReturn[countOfStrings-1] = String.valueOf(Double.valueOf(result));


                if (isDecimal(arrToReturn[countOfStrings-1]))
                {
                    arrToReturn[countOfStrings-1] = String.valueOf(clearZeroesAtEnd(arrToReturn[countOfStrings-1]));
                    textarea = new StringBuffer().append(getTextAreaWithoutNewLineCharacters());
                }
                LOGGER.debug("arrToReturn["+(countOfStrings - 1)+"]: " + arrToReturn[countOfStrings-1]);
                countOfStrings++;
            }
        }
        return arrToReturn;
    }

    @Deprecated
    public void convertFromTypeToType(String type1, String type2)
    {
        if (type1.equals("Binary") && type2.equals("Decimal")) {
            // converting both numbers in values if applicable
            if (!values[0].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[0].length()-1; i<values[0].length(); i++, k--) {
                        String c = Character.toString(values[0].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[0] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
            if (!values[1].equals("")) {
                try {
                    double result = 0.0;
                    double num1 = 0.0;
                    double num2 = 0.0;
                    for(int i=0, k=values[1].length()-1; i<values[1].length(); i++, k--) {
                        String c = Character.toString(values[1].charAt(i));
                        num1 = Double.valueOf(c);
                        num2 = Math.pow(2,k);
                        result = (num1 * num2) + result;
                    }
                    values[1] = clearZeroesAtEnd(Double.toString(result)).toString();
                } catch (NumberFormatException nfe) {

                }
            }
        }
    }

    /**
     * The purpose of this method is to determine at the end of a "cycle", values[0]
     * and values[1] are numbers in base 10.
     * It also determines if textArea and textarea are displayed properly.
     * Remember textArea is all characters, and textarea is simply the value represented
     */
    public void performValuesConversion()
    {
        // make sure no matter the mode, values[0] and values[1] are numbers and textArea's display correctly
        if (getCalcType() == CalcType_v4.PROGRAMMER) {
            if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonDec().isSelected())
            {
                LOGGER.debug("even though we are in programmer mode, decimal base is selected so no conversion is needed");
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonBin().isSelected())
            {
                LOGGER.debug("Programmer mode buttonBin selected");
                getTextArea().setText(addNewLineCharacters(1)+
                        convertFromTypeToTypeOnValues("Decimal", "Binary", getValues()[0])[0]);
                updateTextareaFromTextArea();
            }
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonOct().isSelected()) {}
            else if (((JPanelProgrammer_v4)getCurrentPanel()).getButtonHex().isSelected()) {}
            else {
                convertAllValuesToDecimal();
            }
        }
        //TODO: add more Calctypes here

    }

    public void performOrLogic(ActionEvent actionEvent)
    {
        LOGGER.info("performOrLogic starts here");
        LOGGER.info("button: " + actionEvent.getActionCommand());
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<values[0].length(); i++) {
            String letter = "0";
            if (String.valueOf(values[0].charAt(i)).equals("0") &&
                    String.valueOf(values[1].charAt(i)).equals("0") )
            { // if the characters at both values at the same position are the same and equal 0
                letter = "0";
                sb.append(letter);
            }
            else
            {
                letter = "1";
                sb.append(letter);
            }
            LOGGER.info(String.valueOf(values[0].charAt(i))+" + "+String.valueOf(values[1].charAt(i))+
                    " = "+ letter);
        }
        values[0] = String.valueOf(sb);
        getTextArea().setText(addNewLineCharacters(1)+values[0]);
        orButtonBool = false;
        valuesPosition = 0;
    }

    /************* All Getters and Setters ******************/


    public JMenuBar getBar() { return bar; }
    public JButton getButtonAdd() { return buttonAdd; }
    public JButton getButtonSubtract() { return buttonSubtract; }
    public JButton getButtonMultiply() { return buttonMultiply; }
    public JButton getButtonDivide() { return buttonDivide; }
    public JButton getButtonEquals() { return buttonEquals; }
    public JButton getButtonNegate() { return buttonNegate; }
    // 4 types of Standard Calculators: create getters and setters
    public JPanel getCurrentPanel() { return super.getCurrentPanel(); }
    public static Logger getLOGGER() { return LOGGER; }
    public static long getSerialVersionUID() { return serialVersionUID; }

    public void setBar(JMenuBar bar) { this.bar = bar; }
    private static void setLOGGER(Logger LOGGER) { StandardCalculator_v4.LOGGER = LOGGER; }
    public void setCurrentPanel(JPanel currentPanel) { super.setCurrentPanel(currentPanel);}

}