package version1;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

// Page 945 in textbook
public final class Calc extends JFrame {
    private final JTextArea textArea = new JTextArea();
    private final ButtonGroup btnGroupOne = new ButtonGroup();
    private final ButtonGroup btnGroupTwo = new ButtonGroup();
    final private JRadioButton buttonHex = new JRadioButton("Hex");
    final private JRadioButton buttonDec = new JRadioButton("Dec");
    final private JRadioButton buttonOct = new JRadioButton("Oct");
    final private JRadioButton buttonBin = new JRadioButton("Bin");
    final private JRadioButton buttonQWord = new JRadioButton("Qword");
    final private JRadioButton buttonDWord = new JRadioButton("Dword");
    final private JRadioButton buttonWord = new JRadioButton("Word");
    final private JRadioButton buttonByte = new JRadioButton("Byte");
    private final GridBagLayout standardLayout = new GridBagLayout(); // layout of the calculator
    private final GridBagLayout otherButtonLayout = new GridBagLayout(); // used for the main grouping of buttons
    private GridBagConstraints constraints; // layout's constraints
    private static JLabel iconLabel;
    private static JLabel textLabel;
    private final JPanel buttonGroup1ButtonPanel = new JPanel(); // contains the first button group
    private final JPanel buttonGroup2ButtonPanel = new JPanel(); // contains the second button group
    private final JPanel allOtherButtonsPanel = new JPanel(otherButtonLayout); // contains all other buttons
    ImageIcon calculator = new ImageIcon("C:\\Users\\aaron\\Documents\\NetBeansProjects\\MicrosoftCalculator\\calculatorOriginal - Copy.jpg"); 
    ImageIcon calculator2 = new ImageIcon("C:\\Users\\aaron\\Documents\\NetBeansProjects\\MicrosoftCalculator\\calculator2.jpg"); 
    ImageIcon windowsLogo = new ImageIcon("C:\\Users\\aaron\\Documents\\NetBeansProjects\\MicrosoftCalculator\\windows10.jpg");
    private final Font font = new Font("Segeo UI", Font.BOLD, 14);
    
// buttons listed as displayed as such:
// first two columns, going row by row
    final private JButton buttonB1 = new JButton(" ");
    final private JButton buttonB2 = new JButton(" ");
    final private JButton buttonB3 = new JButton(" ");
    final private JButton buttonB4 = new JButton(" ");
    final private JButton buttonB5 = new JButton (" ");
    final private JButton buttonB6 = new JButton (" ");
    final private JButton buttonB7 = new JButton (" ");
    final private JButton buttonB8 = new JButton (" ");
    final private JButton buttonB9 = new JButton (" ");
    final private JButton buttonB10 = new JButton (" ");
    final private JButton buttonB11 = new JButton (" ");
    final private JButton buttonB12 = new JButton (" ");
// A - F
    final private JButton buttonA = new JButton("A");
    final private JButton buttonB = new JButton("B");
    final private JButton buttonC = new JButton("C");
    final private JButton buttonD = new JButton("D");
    final private JButton buttonE = new JButton("E");
    final private JButton buttonF = new JButton("F");
// numeric
    final private JButton button0 = new JButton("0");
    final private JButton button1 = new JButton("1");
    final private JButton button2 = new JButton("2");
    final private JButton button3 = new JButton("3");
    final private JButton button4 = new JButton("4");
    final private JButton button5 = new JButton("5");
    final private JButton button6 = new JButton("6");
    final private JButton button7 = new JButton("7");
    final private JButton button8 = new JButton("8");
    final private JButton button9 = new JButton("9");
// operators
    final private JButton buttonPlus = new JButton("+");
    final private JButton buttonSub = new JButton("-");
    final private JButton buttonMul = new JButton("*");
    final private JButton buttonDivide = new JButton("/");
    final private JButton buttonEquals = new JButton("=");
// memory
    final private JButton buttonMC = new JButton("MC");
    final private JButton buttonMR = new JButton("MR");
    final private JButton buttonMS = new JButton("MS");
    final private JButton buttonMAdd = new JButton("M+");
    final private JButton buttonMSub = new JButton("M-");
// extras like sqrt
    final private JButton buttonDot = new JButton(".");
    final private JButton buttonFraction = new JButton("1/x");
    final private JButton buttonPercent = new JButton("%");
    final private JButton buttonCE = new JButton("CE");
    final private JButton buttonClear = new JButton("C");
    //final String delete = "\u2190";
    final private JButton buttonDel = new JButton("DEL");
    final String negate = "\u00B1";
    final private JButton buttonNegate = new JButton(negate);
    final String SQRT = "\u221A";
    final private JButton buttonSqrt = new JButton(SQRT);
// storage for calculating
    private final String[] temp = {"","","","",""}; // firstNum, secondNum, total, copy/paste, memory
    private final String[] conversion = new String[64];
    private int position = 0; // used to determine what number we are collecting
    private String input = ""; // used as global temporary storage
    boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false;
    boolean firstNumBool = true;
    boolean negatePressed = true;
    boolean dotButtonPressed = false;
    boolean dotActive = false; // left for now but probably not needed
    boolean standardMode = false;
    boolean binaryMode = true;
    boolean buttonBinMode = false;
    boolean buttonOctMode = false;
    boolean buttonDecMode = false;
    boolean buttonHexMode = false;
    
    
    // set up GUI
    public Calc()
    {
        super("Calc");
        setLayout(standardLayout); // set frame layout
        constraints = new GridBagConstraints(); // instanitate constraints
        
        buttonQWord.setSelected(true);
        
        buttonDec.setSelected(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
        button5.setEnabled(true);
        button6.setEnabled(true);
        button7.setEnabled(true);
        button8.setEnabled(true);
        button9.setEnabled(true);
        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
        buttonD.setEnabled(false);
        buttonE.setEnabled(false);
        buttonF.setEnabled(false);
        buttonSqrt.setEnabled(false);
        buttonPercent.setEnabled(false);
        buttonFraction.setEnabled(false);
        buttonNegate.setEnabled(true);
    }
    
    public void setMenuBar() 
    {
        JMenuBar bar = new JMenuBar(); // create menu bar
        setJMenuBar(bar); // add menu bar to application
        
        JMenu viewMenu = new JMenu("View"); // create view menu
        viewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(viewMenu); // add viewMenu to menu bar
        
            JMenuItem view = new JMenuItem("View");
            view.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JMenuItem hide = new JMenuItem("Hide");
            hide.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            viewMenu.add(view);
            view.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    // add code to do view operation
                }
            });
            
            viewMenu.add(hide);
            hide.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    // add code to do hide operation
                }
            });
        JMenu editMenu = new JMenu("Edit"); // create edit menu
        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(editMenu); // add editMenu to menu bar
        
            JMenuItem copyItem = new JMenuItem("Copy"); 
            copyItem.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
            copyItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                // create first item in editMenu
            JMenuItem pasteItem = new JMenuItem("Paste");
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK));
            pasteItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                // create second item in editMenu
            
            // add JMenuItems to editMenu
            editMenu.add(copyItem);
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    temp[3] = textArea.getText(); // to copy
                    System.out.println("\ntemp[3]: " + temp[3]);
                }
            });
            
            editMenu.add(pasteItem);
            pasteItem.addActionListener(
            new ActionListener()
            {
                // paste from copyItem
                @Override
                public void actionPerformed(ActionEvent event)
                {
                    if (temp[3].equals(""))
                        System.out.println("Temp[3] is null");
                    else
                        System.out.println("\ntemp[3]: " + temp[3]);
                    textArea.setText(temp[3]); // to paste
                    temp[position] = textArea.getText();
                    System.out.println("textArea: " + textArea.getText());
                }
            }
        );
            
        JMenu helpMenu = new JMenu("Help"); // create help menu
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(helpMenu); // add helpMenu to menu bar
        
            JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");
            aboutCalculatorItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
                
                helpMenu.add(aboutCalculatorItem);
                aboutCalculatorItem.addActionListener(
                new ActionListener() // anonymous inner class
                {
                    // display message dialog box when user selects About....
                    @Override
                    public void actionPerformed(ActionEvent event)
                    {
                        String COPYRIGHT = "\u00a9";
                        iconLabel = new JLabel(calculator2);
                        JPanel iconPanel = new JPanel(new GridBagLayout() );
                        iconPanel.add(iconLabel);
                        textLabel = new JLabel("<html>Microsoft Windows"
                            + "Version 1.0 (Build 1)<br>"
                            + COPYRIGHT + " 2017 Microsoft Corporation. All rights reserved (to me).<br><br>"
                            + "The Windows 1.0 Pro operating system and its user interface are<br>"
                            + "procted by trademark and all other pending or existing intellectual property<br>"
                            + "right in the United States and other countries/regions."
                            + "<br><br><br>"
                            + "This product is licensed under the License Terms to:<br>"
                            + "Mohamed El-Malah</html>", windowsLogo, SwingConstants.LEFT);
                        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                        
                        JPanel mainPanel = new JPanel();
                        mainPanel.add(iconLabel);
                        mainPanel.add(textLabel);
                        JOptionPane.showMessageDialog(Calc.this,
                            mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE); 
                    }
                } 
            );
    } // end public setMenuBar
    
    /**
     * Inside setButton(), the following takes place
     * Create button handlers for when we click
     * Set button sizes for adding to guit (in same order as declared buttons)
     */
    public void setButton()
    {
        // create buttonHandlers for actions on buttons
        final ButtonHandler buttonHandler = new ButtonHandler();
        final AddButtonHandler addButtonHandler = new AddButtonHandler();
        final SubtractButtonHandler subtractButtonHandler = new SubtractButtonHandler();
        final MultiplyButtonHandler multiplyButtonHandler = new MultiplyButtonHandler();
        final DivideButtonHandler divideButtonHandler = new DivideButtonHandler();
        final EqualsButtonHandler equalsButtonHandler = new EqualsButtonHandler();
        final ClearButtonHandler clearButtonHandler = new ClearButtonHandler();
        final ClearEntryButtonHandler CEButtonHandler = new ClearEntryButtonHandler();
        final NegateButtonHandler negateButtonHandler = new NegateButtonHandler();
        final DeleteButtonHandler delButtonHandler = new DeleteButtonHandler();
        //final SquareRootButtonHandler sqrtButtonHandler = new SquareRootButtonHandler();
        final DotButtonHandler dotButtonHandler = new DotButtonHandler();
        final FracButtonHandler fracButtonHandler = new FracButtonHandler();
        //final String delete = "\u2190";
        final String negate = "\u00B1";
        final String SQRT = "\u221A";
        final ButtonBinHandler buttonBinHandler = new ButtonBinHandler();
        final ButtonOctHandler buttonOctHandler = new ButtonOctHandler();
        final ButtonDecHandler buttonDecHandler = new ButtonDecHandler();
        final ButtonHexHandler buttonHexHandler = new ButtonHexHandler();
        
        // setup textArea 
        textArea.getCaret().isSelectionVisible();
        textArea.setFont(font);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setPreferredSize(new Dimension(400, 35)); // length, width
        textArea.setEditable(false);
        
        // add buttons to buttonGroupOne
        btnGroupOne.add(buttonHex);
        btnGroupOne.add(buttonDec);
        btnGroupOne.add(buttonOct);
        btnGroupOne.add(buttonBin);
        
        // add buttons to ButtonGroupTwo
        btnGroupTwo.add(buttonQWord);
        btnGroupTwo.add(buttonDWord);
        btnGroupTwo.add(buttonWord);
        btnGroupTwo.add(buttonByte);
        
    // setup regular buttons
    // first two column
        buttonB1.setFont(font);
        buttonB1.setPreferredSize(new Dimension(35,35));
        buttonB1.setBorder(new LineBorder(Color.BLACK));
        buttonB1.setEnabled(false);
        
        buttonB2.setFont(font);
        buttonB2.setPreferredSize(new Dimension(35,35));
        buttonB2.setBorder(new LineBorder(Color.BLACK));
        buttonB2.setEnabled(false);
        
        buttonB3.setFont(font);
        buttonB3.setPreferredSize(new Dimension(35,35));
        buttonB3.setBorder(new LineBorder(Color.BLACK));
        buttonB3.setEnabled(false);
        
        buttonB4.setFont(font);
        buttonB4.setPreferredSize(new Dimension(35,35));
        buttonB4.setBorder(new LineBorder(Color.BLACK));
        buttonB4.setEnabled(false);
        
        buttonB5.setFont(font);
        buttonB5.setPreferredSize(new Dimension(35,35));
        buttonB5.setBorder(new LineBorder(Color.BLACK));
        buttonB5.setEnabled(false);
        
        buttonB6.setFont(font);
        buttonB6.setPreferredSize(new Dimension(35,35));
        buttonB6.setBorder(new LineBorder(Color.BLACK));
        buttonB6.setEnabled(false);
        
        buttonB7.setFont(font);
        buttonB7.setPreferredSize(new Dimension(35,35));
        buttonB7.setBorder(new LineBorder(Color.BLACK));
        buttonB7.setEnabled(false);
        
        buttonB8.setFont(font);
        buttonB8.setPreferredSize(new Dimension(35,35));
        buttonB8.setBorder(new LineBorder(Color.BLACK));
        buttonB8.setEnabled(false);
        
        buttonB9.setFont(font);
        buttonB9.setPreferredSize(new Dimension(35,35));
        buttonB9.setBorder(new LineBorder(Color.BLACK));
        buttonB9.setEnabled(false);
        
        buttonB10.setFont(font);
        buttonB10.setPreferredSize(new Dimension(35,35));
        buttonB10.setBorder(new LineBorder(Color.BLACK));
        buttonB10.setEnabled(false);
        
        buttonB11.setFont(font);
        buttonB11.setPreferredSize(new Dimension(35,35));
        buttonB11.setBorder(new LineBorder(Color.BLACK));
        buttonB11.setEnabled(false);
        
        buttonB12.setFont(font);
        buttonB12.setPreferredSize(new Dimension(35,35));
        buttonB12.setBorder(new LineBorder(Color.BLACK));
        buttonB12.setEnabled(false);
    // A - F
        buttonA.setFont(font);
        buttonA.setPreferredSize(new Dimension(35, 35) );
        buttonA.setBorder(new LineBorder(Color.BLACK));
        
        buttonB.setFont(font);
        buttonB.setPreferredSize(new Dimension(35, 35) );
        buttonB.setBorder(new LineBorder(Color.BLACK));
        
        buttonC.setFont(font);
        buttonC.setPreferredSize(new Dimension(35, 35));
        buttonC.setBorder(new LineBorder(Color.BLACK));
        
        buttonD.setFont(font);
        buttonD.setPreferredSize(new Dimension(35, 35));
        buttonD.setBorder(new LineBorder(Color.BLACK));
        
        buttonE.setFont(font);
        buttonE.setPreferredSize(new Dimension(35, 35));
        buttonE.setBorder(new LineBorder(Color.BLACK));
        
        buttonF.setFont(font);
        buttonF.setPreferredSize(new Dimension(35, 35));
        buttonF.setBorder(new LineBorder(Color.BLACK));
    // numeric    
        button0.setFont(font);
        button0.setPreferredSize(new Dimension(70, 35) );
        button0.setBorder(new LineBorder(Color.BLACK));
        
        button1.setFont(font);
        button1.setPreferredSize(new Dimension(35, 35) );
        button1.setBorder(new LineBorder(Color.BLACK));
        
        button2.setFont(font);
        button2.setPreferredSize(new Dimension(35, 35) );
        button2.setBorder(new LineBorder(Color.BLACK));
        
        button3.setFont(font);
        button3.setPreferredSize(new Dimension(35, 35) );
        button3.setBorder(new LineBorder(Color.BLACK));
        
        button4.setFont(font);
        button4.setPreferredSize(new Dimension(35, 35) );
        button4.setBorder(new LineBorder(Color.BLACK));
        
        button5.setFont(font);
        button5.setPreferredSize(new Dimension(35, 35) );
        button5.setBorder(new LineBorder(Color.BLACK));
        
        button6.setFont(font);
        button6.setPreferredSize(new Dimension(35, 35) );
        button6.setBorder(new LineBorder(Color.BLACK));
        
        button7.setFont(font);
        button7.setPreferredSize(new Dimension(35, 35) );
        button7.setBorder(new LineBorder(Color.BLACK));
        
        button8.setFont(font);
        button8.setPreferredSize(new Dimension(35, 35) );
        button8.setBorder(new LineBorder(Color.BLACK));
        
        button9.setFont(font);
        button9.setPreferredSize(new Dimension(35, 35) );
        button9.setBorder(new LineBorder(Color.BLACK));
    // operators   
        buttonPlus.setFont(font);
        buttonPlus.setPreferredSize(new Dimension(35, 35) );
        buttonPlus.setBorder(new LineBorder(Color.BLACK));
        
        buttonSub.setFont(font);
        buttonSub.setPreferredSize(new Dimension(35, 35) );
        buttonSub.setBorder(new LineBorder(Color.BLACK));
        
        buttonMul.setFont(font);
        buttonMul.setPreferredSize(new Dimension(35, 35) );
        buttonMul.setBorder(new LineBorder(Color.BLACK));
        
        buttonDivide.setFont(font);
        buttonDivide.setPreferredSize(new Dimension(35, 35));
        buttonDivide.setBorder(new LineBorder(Color.BLACK));
        
        buttonEquals.setFont(font);
        buttonEquals.setPreferredSize(new Dimension(35, 70) );
        buttonEquals.setBorder(new LineBorder(Color.BLACK));
    // memory
        buttonMC.setFont(font);
        buttonMC.setPreferredSize(new Dimension(35, 35) );
        buttonMC.setBorder(new LineBorder(Color.BLACK));
        buttonMC.setEnabled(false);
        
        buttonMR.setFont(font);
        buttonMR.setPreferredSize(new Dimension(35, 35) );
        buttonMR.setBorder(new LineBorder(Color.BLACK));
        buttonMR.setEnabled(false);
        
        buttonMS.setFont(font);
        buttonMS.setPreferredSize(new Dimension(35, 35) );
        buttonMS.setBorder(new LineBorder(Color.BLACK));
        buttonMS.setEnabled(false);
        
        buttonMAdd.setFont(font);
        buttonMAdd.setPreferredSize(new Dimension(35, 35) );
        buttonMAdd.setBorder(new LineBorder(Color.BLACK));
        buttonMAdd.setEnabled(false);
        
        buttonMSub.setFont(font);
        buttonMSub.setPreferredSize(new Dimension(35, 35) );
        buttonMSub.setBorder(new LineBorder(Color.BLACK));
        buttonMSub.setEnabled(false);
        
    // other    
        buttonDot.setFont(font);
        buttonDot.setPreferredSize(new Dimension(35, 35) );
        buttonDot.setBorder(new LineBorder(Color.BLACK));
        
        buttonFraction.setFont(font);
        buttonFraction.setPreferredSize(new Dimension(35, 35));
        buttonFraction.setBorder(new LineBorder(Color.BLACK)); 
        
        buttonPercent.setFont(font);
        buttonPercent.setPreferredSize(new Dimension(35, 35) );
        buttonPercent.setBorder(new LineBorder(Color.BLACK));
        
        buttonCE.setFont(font);
        buttonCE.setPreferredSize(new Dimension(35, 35) );
        buttonCE.setBorder(new LineBorder(Color.BLACK));
        
        buttonClear.setFont(font);
        buttonClear.setPreferredSize(new Dimension(35, 35) );
        buttonClear.setBorder(new LineBorder(Color.BLACK));
        
        buttonDel.setFont(font);
        buttonDel.setPreferredSize(new Dimension(35, 35) );
        buttonDel.setBorder(new LineBorder(Color.BLACK));
        
        buttonNegate.setFont(font);
        buttonNegate.setPreferredSize(new Dimension(35, 35) );
        buttonNegate.setBorder(new LineBorder(Color.BLACK));
        
        buttonSqrt.setFont(font);
        buttonSqrt.setPreferredSize(new Dimension(35, 35) );
        buttonSqrt.setBorder(new LineBorder(Color.BLACK));
        
    // ***** Add button to component *****
        // anchor for all components is CENTER: the default
        // row, column, width, height
        constraints.fill = GridBagConstraints.BOTH;
        addComponent(textArea, 0, 0, 10, 2);
        
        buttonGroup1ButtonPanel.setLayout(new GridLayout(4,1));
        // add buttons to panel
        buttonGroup1ButtonPanel.add(buttonHex);
        buttonGroup1ButtonPanel.add(buttonDec);
        buttonGroup1ButtonPanel.add(buttonOct);
        buttonGroup1ButtonPanel.add(buttonBin);
        buttonBin.addActionListener(buttonBinHandler);
        buttonOct.addActionListener(buttonOctHandler);
        buttonDec.addActionListener(buttonDecHandler);
        buttonHex.addActionListener(buttonHexHandler);
        //radioButtonPanel.setBorder(BorderFactory.createTitledBorder(""));
        Border border = buttonGroup1ButtonPanel.getBorder();
        Border margin = new TitledBorder(" ");
        buttonGroup1ButtonPanel.setBorder(new CompoundBorder(border, margin));
        // add panel to Calculator
        addComponent(buttonGroup1ButtonPanel, 4, 0, 1, 4);
        
        buttonGroup2ButtonPanel.setLayout(new GridLayout(4,1)); //rows and columns
        // add buttons to panel
        buttonGroup2ButtonPanel.add(buttonQWord);
        buttonGroup2ButtonPanel.add(buttonDWord);
        buttonGroup2ButtonPanel.add(buttonWord);
        buttonGroup2ButtonPanel.add(buttonByte);
        //allOtherButtonPanel.setBorder(BorderFactory.createTitledBorder(""));
        Border border2 = buttonGroup2ButtonPanel.getBorder();
        buttonGroup2ButtonPanel.setBorder(new CompoundBorder(border2, margin));
        // add panel to Calculator
        addComponent(buttonGroup2ButtonPanel, 8, 0, 1, 4);
        
        //allOtherButtonsPanel.setBorder(BorderFactory.createLineBorder(Color.black)); // used to help me see the component on the gui
        constraints.insets = new Insets(5,0,1,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
        setComponent(buttonB1, 0, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB2, 0, 1, 1, 1, otherButtonLayout);
        setComponent(buttonA, 0, 2, 1, 1, otherButtonLayout);
        setComponent(buttonMC, 0, 3, 1, 1, otherButtonLayout);
        setComponent(buttonMR, 0, 4, 1, 1, otherButtonLayout);
        setComponent(buttonMS, 0, 5, 1, 1, otherButtonLayout);
        setComponent(buttonMAdd, 0, 6, 1, 1, otherButtonLayout);
        setComponent(buttonMSub, 0, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonB1);
        allOtherButtonsPanel.add(buttonB2);
        allOtherButtonsPanel.add(buttonA);
        allOtherButtonsPanel.add(buttonMC);
        allOtherButtonsPanel.add(buttonMR);
        allOtherButtonsPanel.add(buttonMS);
        allOtherButtonsPanel.add(buttonMAdd);
        allOtherButtonsPanel.add(buttonMSub);
        
        setComponent(buttonB3, 1, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB4, 1, 1, 1, 1, otherButtonLayout);
        setComponent(buttonB, 1, 2, 1, 1, otherButtonLayout);
        setComponent(buttonDel, 1, 3, 1, 1, otherButtonLayout);
        buttonDel.addActionListener(delButtonHandler);
        setComponent(buttonCE, 1, 4, 1, 1, otherButtonLayout);
        buttonCE.addActionListener(CEButtonHandler);
        setComponent(buttonClear, 1, 5, 1, 1, otherButtonLayout);
        buttonClear.addActionListener(clearButtonHandler);
        setComponent(buttonNegate, 1, 6, 1, 1, otherButtonLayout);
        buttonNegate.addActionListener(negateButtonHandler);
        setComponent(buttonSqrt, 1, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonB3);
        allOtherButtonsPanel.add(buttonB4);
        allOtherButtonsPanel.add(buttonB);
        allOtherButtonsPanel.add(buttonDel);
        allOtherButtonsPanel.add(buttonCE);
        allOtherButtonsPanel.add(buttonClear);
        allOtherButtonsPanel.add(buttonNegate);
        allOtherButtonsPanel.add(buttonSqrt);
        
        setComponent(buttonB5, 2, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB6, 2, 1, 1, 1, otherButtonLayout);
        setComponent(buttonC, 2, 2, 1, 1, otherButtonLayout);
        setComponent(button7, 2, 3, 1, 1, otherButtonLayout);
        button7.addActionListener(buttonHandler);
        setComponent(button8, 2, 4, 1, 1, otherButtonLayout);
        button8.addActionListener(buttonHandler);
        setComponent(button9, 2, 5, 1, 1, otherButtonLayout);
        button9.addActionListener(buttonHandler);
        setComponent(buttonDivide, 2, 6, 1, 1, otherButtonLayout);
        buttonDivide.addActionListener(divideButtonHandler);
        setComponent(buttonPercent, 2, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonB5);
        allOtherButtonsPanel.add(buttonB6);
        allOtherButtonsPanel.add(buttonC);
        allOtherButtonsPanel.add(button7);
        allOtherButtonsPanel.add(button8);
        allOtherButtonsPanel.add(button9);
        allOtherButtonsPanel.add(buttonDivide);
        allOtherButtonsPanel.add(buttonPercent);
        
        setComponent(buttonB7, 3, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB8, 3, 1, 1, 1, otherButtonLayout);
        setComponent(buttonD, 3, 2, 1, 1, otherButtonLayout);
        setComponent(button4, 3, 3, 1, 1, otherButtonLayout);
        button4.addActionListener(buttonHandler);
        setComponent(button5, 3, 4, 1, 1, otherButtonLayout);
        button5.addActionListener(buttonHandler);
        setComponent(button6, 3, 5, 1, 1, otherButtonLayout);
        button6.addActionListener(buttonHandler);
        setComponent(buttonMul, 3, 6, 1, 1, otherButtonLayout);
        buttonMul.addActionListener(multiplyButtonHandler);
        setComponent(buttonFraction, 3, 7, 1, 1, otherButtonLayout);
        allOtherButtonsPanel.add(buttonB7);
        allOtherButtonsPanel.add(buttonB8);
        allOtherButtonsPanel.add(buttonD);
        allOtherButtonsPanel.add(button4);
        allOtherButtonsPanel.add(button5);
        allOtherButtonsPanel.add(button6);
        allOtherButtonsPanel.add(buttonMul);
        allOtherButtonsPanel.add(buttonFraction);
        
        setComponent(buttonB9, 4, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB10, 4, 1, 1, 1, otherButtonLayout);
        setComponent(buttonE, 4, 2, 1, 1, otherButtonLayout);
        setComponent(button1, 4, 3, 1, 1, otherButtonLayout);
        button1.addActionListener(buttonHandler);
        setComponent(button2, 4, 4, 1, 1, otherButtonLayout);
        button2.addActionListener(buttonHandler);
        setComponent(button3, 4, 5, 1, 1, otherButtonLayout);
        button3.addActionListener(buttonHandler);
        setComponent(buttonSub, 4, 6, 1, 1, otherButtonLayout);
        buttonSub.addActionListener(subtractButtonHandler);
        setComponent(buttonEquals, 4, 7, 1, 2, otherButtonLayout);
        buttonEquals.addActionListener(equalsButtonHandler);
        allOtherButtonsPanel.add(buttonB9);
        allOtherButtonsPanel.add(buttonB10);
        allOtherButtonsPanel.add(buttonE);
        allOtherButtonsPanel.add(button1);
        allOtherButtonsPanel.add(button2);
        allOtherButtonsPanel.add(button3);
        allOtherButtonsPanel.add(buttonSub);
        allOtherButtonsPanel.add(buttonEquals);
        
        setComponent(buttonB11, 5, 0, 1, 1, otherButtonLayout);
        setComponent(buttonB12, 5, 1, 1, 1, otherButtonLayout);
        setComponent(buttonF, 5, 2, 1, 1, otherButtonLayout);
        setComponent(button0, 5, 3, 2, 1, otherButtonLayout);
        button0.addActionListener(buttonHandler);
        setComponent(buttonDot, 5, 5, 1, 1, otherButtonLayout);
        setComponent(buttonPlus, 5, 6, 1, 2, otherButtonLayout);
        buttonPlus.addActionListener(addButtonHandler);
        allOtherButtonsPanel.add(buttonB11);
        allOtherButtonsPanel.add(buttonB12);
        allOtherButtonsPanel.add(buttonF);
        allOtherButtonsPanel.add(button0);
        allOtherButtonsPanel.add(buttonDot);
        allOtherButtonsPanel.add(buttonPlus);
        // add allOtherButtonsPanel to gui
        addComponent(allOtherButtonsPanel, 5, 1, 6, 8, standardLayout);
        
        
    } // end class setButton()
    
    
    
    /**
     * This method takes the calculatorMode you want,
     * set's it to true but not before setting all
     * other modes to false.
     * @param mode
     * @return 
     */
    public Boolean setMode(Boolean mode) {
        standardMode = false;
        binaryMode = false;
        mode = true;
        return mode;
    }
    
    public Boolean getMode(Boolean mode) {
        return mode;
    }
    
    /**
     * Returns standardMode in whatever
     * mode it happens to be in.
     * @return 
     */
    public Boolean getStandardMode() {
        System.out.printf("StandardMode is in %s mode\n", standardMode);
        return standardMode;
    }
    
    
    /**
     * Sets the given mode to true but first resets
     * all modes to false.
     * @param mode
     * @return 
     */
    
    /**
     * This method does two things:
     * Clears any decimal found.
     * Clears all zeroes after decimal (if that is the case).
     * @param currentNumber
     * @return updated currentNumber
     */
    private String clearZeroesAtEnd(String currentNumber) {
        System.out.println("starting clearZeroesAtEnd()");
        System.out.println("currentNumber: " + currentNumber);
        input = currentNumber; // copy of currentNumber
        boolean justZeroes = true;
        int index = 0;
        index = input.indexOf(".");
        //int indexStartPosition = index;
        //int keepIndex = index;
        // this for loop checks first to make sure its all zeroes after the decimal point
        check: 
        for(int i = index + 1; i < input.length(); i++) {
            // from decimal point to the end of the number
            //System.out.println("i: " + i);
            // eliminate decimal first
            if (!input.substring(i, i+1).equals("0")) {
                // if the string value at position x is not a 0
                justZeroes = false;
                break check;
            }
        }
        if (justZeroes == true) {
            // happy path; delete all from index onward
            input = input.substring(0,index);
        }
        
        System.out.println("output of clearZeroesAtEnd(): " + input);
        return input;
    }
    
    // method that tells what to do for numerical input
    public class ButtonHandler implements ActionListener
    {  
        @Override
        public void actionPerformed(ActionEvent e)
        {   
            System.out.println("\nButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if (firstNumBool == true) {
                if (memAddBool == true || memSubBool == true) { // || position == 0: essentially resetting value
                    textArea.setText("");
                } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                    textArea.setText(temp[0]);
                    position = 0;
                    firstNumBool = true;
                    dotButtonPressed = false;
                    dotActive = false;
                } else if (textArea.getText().equals("0")) { 
                    textArea.setText("");
                    temp[position] = textArea.getText();
                    firstNumBool = true;
                    dotButtonPressed = false;
                    dotActive = false;          
                } else if (temp[0].equals("") && !temp[1].equals("")) {
                    temp[0] = temp[1];
                    temp[1] = "";
                    position = 0;
                }
                if (dotButtonPressed == false && !textArea.getText().endsWith("-")) {        
                    System.out.println("firstNumBool = true \ndotButtonPressed = false");
                    System.out.print(textArea.getText());
                    textArea.setText(textArea.getText() + e.getActionCommand()); // update textArea
                    temp[position] = textArea.getText(); // store input
                    System.out.printf("\ntextArea: %s\n", textArea.getText());
                    System.out.printf("temp[%d]: %s\n",position, temp[position]);
                    //s = s.substring(0,s.length());
                } else if (dotButtonPressed == false) { // logic for negative numbers
                    System.out.println("firstNumBool = true \n dotButtonPressed = false : negative number");
                    input = temp[position];
                    System.out.printf("\ninput: %s\n", input);
                    input = input + e.getActionCommand(); // update textArea
                    System.out.printf("\ninput: %s", input);
                    temp[position] = input; // store input
                    textArea.setText(convertToPositive(input) + e.getActionCommand() + "-");
                    System.out.printf("\ntextArea: %s\n", textArea.getText());
                    System.out.printf("temp[%d]: %s\n",position, temp[position]);
                }
                else { // dotPressed = true
                    System.out.println("firstNumBool = true : dotButtonPressed = true");
                    if (input.equals(".0")) {
                        textArea.setText("0.");
                        textArea.setText(textArea.getText() + e.getActionCommand()); // update textArea
                        temp[position] =  textArea.getText(); // store input
                        System.out.printf("\ttextArea: %s\n", textArea.getText());
                        System.out.printf("temp[%d]: %s\n",position, temp[position]);
                        //s = s.substring(0,s.length());
                        System.out.println("\nbutton: " + e.getActionCommand()); // print out button confirmation
                        //temp[0] = s;
                        input = "";
                        System.out.printf("dotButtonPressed: %s\n", dotButtonPressed);
                        System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored input confirmation
                        dotButtonPressed = false;
                        System.out.printf("dotButtonPressed: %s\n", dotButtonPressed);
                    } else if (temp[position].startsWith("-")) {
                        input = temp[position];
                        System.out.printf("\ninput: %s\n", input);
                        input = input.substring(2, input.length());
                        System.out.printf("\ninput: %s\n", input);
                        input = input + "." + e.getActionCommand();
                        System.out.printf("\ninput: %s\n", input);
                        System.out.printf("\ninput: -%s\n", input);
                        textArea.setText(input + "-");
                        temp[position] = "-" + input;
                    } else {
                        temp[position] = textArea.getText(); // collect input
                        System.out.println("Old " + temp[position]);
                        temp[position] = temp[position].substring(1, temp[position].length());
                        System.out.println("New " + temp[position] + ".");
                        textArea.setText(temp[position] + "." + e.getActionCommand()); // update textArea
                        temp[position] = textArea.getText(); // update input with decimal
                        //String number = s + "." + event.getActionCommand(); // 504 + . + input
                        dotButtonPressed = false;
                        //System.out.println("TextArea: " + textArea.getText());
                        System.out.println("button: " + e.getActionCommand()); // print out button confirmation
                        System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored input confirmation
                    }
                }   
            } 
            else { // do for second number
                if (firstNumBool == false && dotButtonPressed == false) {
                    textArea.setText("");
                    System.out.println("\nResetting textArea\n");
                    if (firstNumBool == false)
                        firstNumBool = true;
                    else
                        dotButtonPressed = true;
                } 
                //System.out.println("button: " + e.getActionCommand()); // print out button confirmation
                if (input.equals(".0"))  {
                    textArea.setText("0.");
                    input = "";
                } else if (input.equals("0")) { 
                    textArea.setText("");
                    temp[position] = textArea.getText();
                    position = 1;
                    firstNumBool = true;
                    dotButtonPressed = false;
                    dotActive = false;
                } else if (temp[0].equals("") && !temp[1].equals("")) {
                    temp[0] = temp[1];
                    temp[1] = "";
                    position = 1;
                }
                //textArea.setText(null);
                // position++; // increase position for storing input
                if (firstNumBool == false) {
                    textArea.setText(textArea.getText() + e.getActionCommand());
                    temp[position] = textArea.getText(); // store input
                    System.out.printf("textArea: %s\n", textArea.getText());
                    System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored input confirmation
                } else {
                    textArea.setText(textArea.getText() + e.getActionCommand());
                    temp[position] = textArea.getText(); // store input
                    System.out.printf("textArea: %s\n", textArea.getText());
                    System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored input confirmation
                    firstNumBool = true;
                }
            }
            if (buttonByte.isSelected()) {
                input = textArea.getText();
                if (input.length() > 8) {
                    input = input.substring(0, 8);
                    textArea.setText(input);
                }
            } else if (buttonWord.isSelected()) {
                input = textArea.getText();
                if (input.length() > 16) {
                    input = input.substring(0, 16);
                    textArea.setText(input);
                }
            } else if (buttonDWord.isSelected()) {
                input = textArea.getText();
                if (input.length() > 32) {
                    input = input.substring(0, 32);
                    textArea.setText(input);
                }
                
            } else if (buttonQWord.isSelected()) {
                input = textArea.getText();
                if (input.length() > 64) {
                    input = input.substring(0, 64);
                    textArea.setText(input);
                }
                
            }
            textArea.setLineWrap(true);
            confirm();    
        }
    }
    
    public class AddButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("\nAddButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (negatePressed == true) {
//                temp[0] = Integer.parseInt( textArea.getText().replace("-", ""));
//                negatePressed = false;
//            }
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid input") && !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            
                // first determine if number is whole number or decimal number
                //ouble number = Double.parseDouble(textArea.getText());
                //if (decimal == false) {
                    //temp[0] = Integer.parseInt(textArea.getText());
                    //temp[0] = temp[0];
            
                textArea.setText(e.getActionCommand() + " " +  textArea.getText()); // "userInput +" // temp[position]
                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                System.out.printf("temp[%d]: %s after addButton pushed\n",position,temp[position]); // confirming proper input before moving on
                //}
                //else { // if number is double
                    //temp[0] = Double.parseDouble(textArea.getText());
                    //textArea.setText(event.getActionCommand() + " " + temp[0] ); // "userInput.input +"
                    //System.out.printf("temp[0] "+temp[0]);
                //}
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
                //secondNumBool = true;
            } else if (addBool == true) {
                System.out.println("Performing previous addition calculation");
                addition();
                addBool = resetOperator(addBool); // sets addBool to false
                addBool = true;
            } else if (subBool == true) {
                System.out.println("We understand the logic");
                subtract();
                subBool = resetOperator(subBool);
                addBool = true;
            } else if (mulBool == true) {
                multiply();
                mulBool = resetOperator(mulBool);
                addBool = true;
            } else if (divBool == true) {
                divide();
                divBool = resetOperator(divBool);
                addBool = true;
                confirm();
            } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
                addBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } 
            confirm();
        }
    }
    
    public class SubtractButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nSubtractButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid input") && !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput -" 
                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                System.out.printf("temp[%d]: %s after subButton pushed",position,temp[position]); // confirming proper input before moving on
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } else if (addBool == true) {
                addition();
                addBool = resetOperator(addBool);
                subBool = true;
            } else if (subBool == true) {
                subtract();
                subBool = resetOperator(subBool);
                subBool = true;
            } else if (mulBool == true) {
                multiply();
                mulBool = resetOperator(mulBool);
                subBool = true;
            } else if (divBool == true) {
                divide();
                divBool = resetOperator(divBool);
                subBool = true;
            } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
                subBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } 
        }
    }
    
    public class MultiplyButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nMultiplyButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid input") && !textArea.getText().equals("Cannot divide by 0")) {
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput *" 
                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                System.out.printf("temp[%d]: %s after mulButton pushed",position,temp[position]); // confirming proper input before moving on
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } else if (addBool == true) {
                addition();
                addBool = resetOperator(addBool);
                mulBool = true;
            } else if (subBool == true) {
                subtract();
                subBool = resetOperator(subBool);
                mulBool = true;
            } else if (mulBool == true) {
                multiply();
                mulBool = resetOperator(mulBool);
                mulBool = true;
            } else if (divBool == true) {
                divide();
                divBool = resetOperator(divBool);
                mulBool = true;
            } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
                mulBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } 
        }
    }
    
    public class DivideButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nDivideButtonHandler started");
                System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid input") && !textArea.getText().equals("Cannot divide by 0")) {
                
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput /" 
                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                System.out.printf("temp[%d]: %s after divButton pushed",position,temp[position]); // confirming proper input before moving on
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } else if (addBool == true) {
                addition();
                addBool = resetOperator(addBool); // sets addBool to false
                divBool = true;
            } else if (subBool == true) {
                subtract();
                subBool = resetOperator(subBool);
                divBool = true;
            } else if (mulBool == true) {
                multiply();
                mulBool = resetOperator(mulBool);
                divBool = true;
            } else if (divBool == true) {
                divide();
                divBool = resetOperator(divBool);
                divBool = true;
            } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
                divBool = true; // sets logic for arithmetic
                firstNumBool = false; // sets logic to perform operations when collecting second number
                dotButtonPressed = false;
                position++; // increase position for storing input
                input = "";
            } 
        }
    }
    
    public class EqualsButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nEqualsButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            //position++;
            //CharSequence cs = ".";
            //double num1D =0.0, num2D=0.0;
            //int num1=0, num2=0;
            double result = 0.0;
            
            
            if (addBool == true) {
                addition();
                addBool = resetOperator(addBool);
                position = 0;
            } 
            else if (subBool == true){
                subtract();
                subBool = resetOperator(subBool);
                position = 0;
            } 
            else if (mulBool == true){
                multiply();
                mulBool = resetOperator(mulBool);
                position = 0;
            } 
            else if (divBool == true){
                divide();
                divBool = resetOperator(divBool);
                position = 0;
            } else if (temp[0].equals("") && temp[1].equals("")) {
                // if temp[0] and temp[1] do not have a number
                position = 0;
            } else if (textArea.getText().equals("Invalid input") || textArea.getText().equals("Cannot divide by 0")) {
                textArea.setText(e.getActionCommand() + " " +  temp[position]); // "userInput +" // temp[position]
                position = 1;
                firstNumBool = true;
            } 
            temp[1] = ""; // this is not done in addition, subtraction, multiplication, or division
            confirm();
        }
    }
    
    public class ClearButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nClearButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            clear();
            confirm();
        }
    }
    
    public class ClearEntryButtonHandler implements ActionListener // works, supposedly
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("\nClearEntryButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            input = textArea.getText();
            textArea.setText("");
            if (temp[1].equals("")) {
                temp[0] = "";
                addBool = false;
                subBool = false;
                mulBool = false;
                divBool = false;
                position = 0;
                firstNumBool = true;
                dotButtonPressed = false;
            } else {
                temp[1] = ""; }
            confirm();
        }
    }
    
    // needs double input conditioning
    public class SqButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nSquare Root ButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            if (!textArea.getText().equals("")) {
                if (Double.parseDouble(temp[position]) > 0.0) {
                    temp[position] = Double.toString(Math.sqrt(Double.parseDouble(textArea.getText()))); //4.0
                    int intRep = (int)Math.sqrt(Double.parseDouble(textArea.getText())); // 4
                    if (intRep == Math.sqrt(Double.parseDouble(textArea.getText()))) { // if int == double, cut off decimal and zero
                        textArea.setText(temp[position]);
                        input = textArea.getText();
                        input = input.substring(0, input.length()-2); // input changed to whole number, or int
                        temp[position] = input; // update storing
                        textArea.setText(temp[position]);
                        if (Integer.parseInt(temp[position]) < 0 ) {
                            input = textArea.getText(); // temp[2]
                            System.out.printf("%s", input);
                            input = input.substring(1, input.length());
                            System.out.printf("input: %s",input);
                            textArea.setText(input + "-"); // update textArea
                            System.out.printf("temp[%d] %s\n",position, temp[position]);
                        }
                    }
                textArea.setText(temp[position] );
                } else { // number is negative
                    clear();
                    textArea.setText("Invalid input");
                    System.out.println("textArea: "+textArea.getText());
                }
                
            }
            confirm();
        }
    }
        
    // NegateButtonHandler operates on this button: 
    // final private JButton buttonMC = new JButton("\u00B1");
    public class NegateButtonHandler implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nNegateButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            input = textArea.getText();
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if (!textArea.getText().equals("")) { 
            // if there is a number in the text area
                if (isNegativeNumber(temp[position]) == true) { //temp[position].substring(0, 1).equals("-")
                // if there is already a negative sign
                    System.out.println("Reversing number back to positive");
                    temp[position] = textArea.getText(); // input
                    textArea.setText(temp[position] = convertToPositive(temp[position]));
                } else  {
                // whether first or second number, add "-"
                    temp[position] = textArea.getText(); // input
                    textArea.setText(temp[position] + "-");
                    temp[position] = convertToNegative(temp[position]); 
                } 
            }
            confirm();    
        }
    }
    
    
    public String convertToNegative(String number) {
        System.out.print("\nconvertToNegative() running");
        //temp[position] = textArea.getText(); // input
        System.out.println("\nOld: " + number);
        //temp[position] = temp[position].substring(1, temp[position].length());
        System.out.println("New: "  + "-" + number);
        //textArea.setText(number + "-"); // update textArea
        input = "-" + number;
         
        System.out.printf("Converted Number: %s\n", input); // confirm();
        //System.out.printf("temp[%d]: %s\n",position,temp[position]);  
        return input;
    }
    
    public String convertToPositive(String number) {
        System.out.println("\nconvertToPositive() running");
        System.out.printf("Number to convert: %s\n", number);
        if (number.endsWith("-")) {
            System.out.printf("Converted Number: %s\n", number.substring(0, number.length()-1) );
            number = number.substring(0, number.length()-1);
        } else {
            System.out.printf("Converted Number: %s\n", number.substring(1, number.length()) );
            number = number.substring(1, number.length());
        }
        
        
        
        return number;
    }
    
    // tested: passed
    public boolean isDecimal(String number) {
        System.out.println("isDecimal() running");
        boolean answer = false;
        //int intRep = (int)Double.parseDouble(result);
        for(int i = 0; i < number.length(); i++) {
            if (number.substring(i).startsWith(".")) {
                System.out.println("We have a decimal number");
                answer = true;
            }
        }
        return answer;
    }
    
    public class DeleteButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("\nDeleteButtonHandler started");
            System.out.println("button: " + e.getActionCommand() + "\n"); // print out button confirmation
            input = temp[position];
            System.out.printf("temp[%d]: %s\n",position, temp[position]);
            System.out.printf("input: %s\n",input);
            boolean isNegative = isNegativeNumber(input);
            dotActive = isDecimal(input);
            System.out.printf("isNegativeNumber() result: %s\n", isNegative);
            System.out.printf("isDecimal() result: %s\n", dotActive);
            
            if (addBool == false && subBool == false && mulBool == false && divBool == false) {
                if (isNegative == false) {
                    // if no operator has been pushed; number is positive; number is whole
                    if (dotActive == false) {
                        if (input.length() == 1) { // ex: 5
                            input = "";
                        } else if (input.length() == 2) {
                            input = input.substring(0, input.length()-1);
                        } else if (input.length() >= 2) { // ex: 56
                            input = input.substring(0, input.length()-1);
                        } 
                                // dotActive == true and length == 3
                                //input = input.substring(0, 1);
                        System.out.printf("output: %s\n",input);
                        textArea.setText(input);
                        temp[position] = input;
                        confirm();
                    }
                    // if no operator has been pushed; number is positive; number is decimal
                    else if (dotActive == true) {
                        if (input.length() == 2) { // ex: 3.
                            input = input.substring(0, input.length()-1);
                            dotButtonPressed = false;
                        } else if (input.length() == 3) { // ex: 3.2 or 0.5
                            input = input.substring(0, input.length() - 2);
                            dotButtonPressed = false;
                        } else if (input.length() > 3) { // ex: 3.25 or 0.50
                            input = input.substring(0, input.length() - 1); // inclusive
                        }
                        System.out.printf("output: %s\n",input);
                        if (input.endsWith(".")) {
                            input = input.substring(0,0);
                        } else if (input.startsWith(".")) {
                            input = input.substring(1,1);
                        } 
                        input = clearZeroesAtEnd(input);
                        textArea.setText(input);
                        temp[position] = input;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if no operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (input.length() == 2) { // ex: -3
                            input = "";
                            textArea.setText(input);
                            temp[position] = "";
                        } else if (input.length() >= 3) { // ex: -32
                            input = convertToPositive(input);
                            input = input.substring(0, input.length());
                            textArea.setText(input + "-");
                            temp[position] = "-" + input;
                        }
                        System.out.printf("\noutput: %s\n",input);
                        confirm();
                    }
                    // if no operator has been pushed; number is negative; number is decimal
                    else if (dotActive == true) {
                        if (input.length() == 4) { // -3.2
                            input = convertToPositive(input); // 3.2
                            input = input.substring(0, 1); // 3 
                            dotButtonPressed = false;
                            textArea.setText(input + "-");
                            temp[position] = "-" + input;
                        } else if (input.length() > 4) { // ex: -3.25 or -0.00
                            input = convertToPositive(input); // 3.00 or 0.00
                            input = input.substring(0, input.length()); // 3.0 or 0.0
                            input = clearZeroesAtEnd(input); // 3 or 0
                            textArea.setText(input + "-");
                            temp[position] = "-" + input;
                        }
                        System.out.printf("\noutput: %s\n",input);
                        confirm();
                    }
                }
                
            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
                if (isNegative == false) {
                    // if an operator has been pushed; number is positive; number is whole
                    if (dotActive == false) {
                        if (input.length() == 1) { // ex: 5
                            input = "";
                        } else if (input.length() == 2) {
                            input = input.substring(0, input.length()-1);
                        } else if (input.length() >= 2) { // ex: 56
                            input = input.substring(0, input.length()-1);
                        } 
                        System.out.printf("output: %s\n",input);
                        textArea.setText(input);
                        temp[position] = input;
                        confirm();
                    }
                    // if an operator has been pushed; number is positive; number is decimal
                    else if (dotActive == true) {
                        if (input.length() == 2) // ex: 3.
                            input = input.substring(0, input.length()-1);
                        else if (input.length() == 3) { // ex: 3.2 0.0
                            input = input.substring(0, input.length()-2); // 3 or 0
                            dotActive = false;
                            dotButtonPressed = false;
                        } else if (input.length() > 3) { // ex: 3.25 or 0.50 
                            input = input.substring(0, input.length() -1);
                            input = clearZeroesAtEnd(input);
                        }
                        System.out.printf("output: %s\n",input);
                        textArea.setText(input);
                        temp[position] = input;
                        confirm();
                    }
                } else if (isNegative == true) {
                    // if an operator has been pushed; number is negative; number is whole
                    if (dotActive == false) {
                        if (input.length() == 2) { // ex: -3
                            input = "";
                            textArea.setText(input);
                            temp[position] = "";
                        } else if (input.length() >= 3) { // ex: -32
                            input = convertToPositive(input);
                            input = input.substring(0, input.length());
                            textArea.setText(input + "-");
                            temp[position] = "-" + input;
                        }
                        System.out.printf("\ninput: %s\n",input);
                        confirm();
                    }
                    // if an operator has been pushed; number is negative; number is decimal
                    else if (dotActive == true) {
                        if (input.length() == 4) { // -3.2
                            input = convertToPositive(input); // 3.2 or 0.0
                            input = input.substring(0, 1); // 3 or 0
                            dotActive = false;
                            dotButtonPressed = false;
                            textArea.setText(input + "-"); // 3- or 0-
                            temp[position] = "-" + input; // -3 or -0
                        } else if (input.length() > 4) { // ex: -3.25  or -0.00
                            input = convertToPositive(input); // 3.25 or 0.00
                            input = input.substring(0, input.length()); // 3.2 or 0.0
                            input = clearZeroesAtEnd(input);
                            System.out.println("input: " + input);
                            if (input.equals("0")) {
                                textArea.setText(input);
                                temp[position] = input;
                            } else {
                                textArea.setText(input + "-");
                                temp[position] = "-" + input;
                            }
                        }
                        System.out.printf("\ninput: %s\n",input);
                        confirm();
                    }
                }
            }
            dotActive = false;
        }
        
    }
    
    
    public boolean isNegativeNumber(String result) {
        System.out.println("\nisNegativeNumber() running");
        boolean answer = false;
        //int intRep = (int)Double.parseDouble(result);
        if (result.startsWith("-")) { // if int == double, cut off decimal and zero
            answer = true;
        }
        System.out.println("result is = " + answer);
        return answer;
    }
    
    // DotButtonHandler operates on this button:
    // final private JButton buttonDot = new JButton(".");
    public class DotButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            System.out.println("\nDotButtonHandler");
            System.out.println("button: " + event.getActionCommand() + "\n"); // print out button confirmation
            if (!temp[position].equals("")) { // if the input is not null
                System.out.printf("dotButtonPressed: %s\ndotActive: %s\n", dotButtonPressed, dotActive);
//                if (!temp[position].endsWith(".")) {
//                    dotButtonPressed = false;
//                    dotActive = false;
//                }
                
                System.out.printf("temp[%d]: %s before appending dot\n",position,temp[position]); 
                //number = number.substring(0,number.length()-1);
                //System.out.printf("\ns adjusted: %s",number);
                if (dotButtonPressed == false || dotActive == false) {
                    //String x = s.substring(0,s.length()-1);
                    textArea.setText("."+temp[position]);
                    System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
                    temp[position] = temp[position] + ".";
                    dotButtonPressed = true;
                    //decimal = true;
                } 
                //System.out.printf("temp[0] %.2f", temp[0]);
            } else { // the first button we are pushing is the dot operator
                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                if (firstNumBool == false)
                    textArea.setText("");
                input = ".0";
                textArea.setText(input);
                temp[position] = "0.";
                dotButtonPressed = true;
                //decimal = true;
            }
            dotActive = true;
            confirm();
        }
    }
    
    // FracButtonHandler operates on this button:
    // final private JButton buttonF = new JButton("1/x");
    public class FracButtonHandler implements ActionListener { // not working 
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("\nFracStoreButtonHandler started");
            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
            if (!textArea.getText().equals("")) {
                double result = Double.parseDouble(temp[position]);
                result = 1 / result;
                System.out.printf("result: %.2f", result);
                temp[position] = Double.toString(result);
                textArea.setText(temp[position]);
                System.out.printf("temp[%d]: %s\n",position, temp[position]);
            }
        }
    }
    
    
    public class ButtonBinHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonBin.isSelected()) {
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                button5.setEnabled(false);
                button6.setEnabled(false);
                button7.setEnabled(false);
                button8.setEnabled(false);
                button9.setEnabled(false);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
                // lot more to disable
            }
            
            setButtonGroup2Mode();
            // only convert number if textArea has text
            if (!textArea.getText().equals(""))
                convertToBinary();
        }
    }
    
    public class ButtonOctHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonOct.isSelected()){
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                button5.setEnabled(true);
                button6.setEnabled(true);
                button7.setEnabled(true);
                button8.setEnabled(false);
                button9.setEnabled(false);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            setButtonGroup2Mode();
            if (!textArea.getText().equals(""))
                convertToOctal();
        }  
    }
    
    public class ButtonDecHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonDec.isSelected()){
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                button5.setEnabled(true);
                button6.setEnabled(true);
                button7.setEnabled(true);
                button8.setEnabled(true);
                button9.setEnabled(true);
                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
                buttonE.setEnabled(false);
                buttonF.setEnabled(false);
            }
            setButtonGroup2Mode();
            if (!textArea.getText().equals(""))
                convertToDecimal();
        }  
    }
    
    public class ButtonHexHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonHex.isSelected()){
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                button5.setEnabled(true);
                button6.setEnabled(true);
                button7.setEnabled(true);
                button8.setEnabled(true);
                button9.setEnabled(true);
                buttonA.setEnabled(true);
                buttonB.setEnabled(true);
                buttonC.setEnabled(true);
                buttonD.setEnabled(true);
                buttonE.setEnabled(true);
                buttonF.setEnabled(true);
            }
            setButtonGroup2Mode();
            if (!textArea.getText().equals(""))
                convertToHexadecimal();
        }  
    }

    // method to set constraints on
    public void addComponent(Component c, int row, int column, int gwidth, int gheight)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = gwidth;
        constraints.gridheight = gheight;
        standardLayout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }
    
    public void addComponent(Component c, int row, int column, int gwidth, int gheight, GridBagLayout layout)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = gwidth;
        constraints.gridheight = gheight;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }
    
    public void setComponent(Component c, int row, int column, int gwidth, int gheight, GridBagLayout layout)
    {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = gwidth;
        constraints.gridheight = gheight;
        layout.setConstraints(c, constraints); // set constraints
        add(c); // add component
    }
    
    // clear method
    public void clear() {
        // firstNum, secondNum, total, copy/paste, memory
        for ( position=0; position < 4; position++) {
            temp[position] = "";
            System.out.printf("temp[%d]: \'%s\'\n",position, temp[position]);
        }
        input = "";
        textArea.setText("");
        addBool = false;
        subBool = false;
        mulBool = false;
        divBool = false;
        position = 0;
        firstNumBool = true;
        dotButtonPressed = false;
        dotActive = false;
               
    }
    
    public void addition() {
        System.out.printf("temp[%d]: %s\n", 0, temp[0]);
        System.out.printf("temp[%d]: %s\n", 1, temp[1]);
        double result = Double.parseDouble(temp[0]) + Double.parseDouble(temp[1]); // create result forced double
        System.out.printf("%s + %s = \n", temp[0], temp[1]);
        temp[0] = formatNumber(Double.toString(result)); // store result
        temp[0] = clearZeroesAtEnd(temp[0]);
        System.out.printf("temp[%d]: %s\n", 0, temp[0]);
        System.out.printf("addBool: "+addBool
                        + "\nsubBool: "+subBool
                        + "\nmulBool: "+mulBool
                        + "\ndivBool: "+divBool);
        int intRep = (int)result;
        if (intRep == result) { // if int == double, cut off decimal and zero
            System.out.println("\nWe have a whole number");
            //textArea.setText(temp[2]);
            input = Double.toString(result);
            temp[0] = input.substring(0, input.length()-2); // input changed to whole number, or int
            textArea.setText(temp[0]);
            if (Integer.parseInt(temp[0]) < 0 ) {
                input = textArea.getText(); // temp[position]
                System.out.printf("%s\n", input);
                input = input.substring(1, input.length());
                System.out.printf("input: %s\n",input);
                textArea.setText(input + "-"); // update textArea
                //System.out.printf("temp[%d] %s\n",position, formatNumber(temp[position]));
            }
        } else {// if double == double, keep decimal and number afterwards
            System.out.println("\nWe have a decimal");
            if (Double.parseDouble(temp[0]) < 0.0 ) {
                temp[0] = formatNumber(temp[0]);
                System.out.printf("%s\n", input);
                input = temp[0];
                System.out.printf("%s\n", input);
                input = input.substring(1, input.length());
                System.out.printf("input: %s\n",input);
                textArea.setText(input + "-"); // update textArea
                System.out.printf("temp[%d] %s\n",position, temp[position]);
            } else {
                textArea.setText(formatNumber(temp[0]));
            }
        }
    }
    
    public void subtract() {
        double result = Double.parseDouble(temp[0]) - Double.parseDouble(temp[1]); // create result forced double
        System.out.printf("\n%s - %s = \n", temp[0], temp[1]);
        temp[0] = Double.toString(result); // store result
        System.out.printf("temp[%d]: %s\n", position, temp[0]);
        System.out.printf("addBool: "+addBool
                      + "\nsubBool: "+subBool
                      + "\nmulBool: "+mulBool
                      + "\ndivBool: "+divBool);
        int intRep = (int)result;
        if (intRep == result) { // if int == double, cut off decimal and zero
            textArea.setText(temp[0]);
            input = textArea.getText();
            input = input.substring(0, input.length()-2); // input changed to whole number, or int
            temp[0] = input; // update storing
            textArea.setText(temp[0]);
            if (Integer.parseInt(temp[0]) < 0 ) {
                input = textArea.getText(); // temp[2]
                System.out.printf("%s", input);
                input = input.substring(1, input.length());
                System.out.printf("input: %s",input);
                textArea.setText(input + "-"); // update textArea
                System.out.printf("temp[%d] %s\n",position, temp[position]);
            }
        } else {// if double == double, keep decimal and number afterwards
            if (Double.parseDouble(temp[0]) < 0.0 ) {
                temp[0] = formatNumber(temp[0]);
                input = temp[0];
                System.out.printf("%s", input);
                input = input.substring(1, input.length());
                System.out.printf("input: %s",input);
                textArea.setText(input + "-"); // update textArea
                System.out.printf("temp[%d] %s\n",position, temp[position]);
            } else {
                textArea.setText(formatNumber(temp[0]));
            }
        }
    }
    
    public void multiply() {
        double result = Double.parseDouble(temp[0]) * Double.parseDouble(temp[1]); // create result forced double
        System.out.printf("\n%s * %s = \n", temp[0], temp[1]);
        temp[0] = Double.toString(result); // store result
        System.out.printf("temp[%d]: %s\n", position, temp[0]);
        System.out.printf("addBool: "+addBool
                      + "\nsubBool: "+subBool
                      + "\nmulBool: "+mulBool
                      + "\ndivBool: "+divBool);
        int intRep = (int)result;
        if (intRep == result) { // if int == double, cut off decimal and zero
            textArea.setText(temp[0]);
            input = textArea.getText();
            input = input.substring(0, input.length()-2); // input changed to whole number, or int
            temp[0] = input; // update storing
            textArea.setText(temp[0]);
            if (Integer.parseInt(temp[0]) < 0 ) {
                input = textArea.getText(); // temp[2]
                System.out.printf("%s", input);
                input = input.substring(1, input.length());
                System.out.printf("input: %s",input);
                textArea.setText(input + "-"); // update textArea
                System.out.printf("temp[%d] %s\n",position, temp[position]);
            }
        } else {// if double == double, keep decimal and number afterwards
            textArea.setText(formatNumber(temp[0]));
        }
    }
    
    public void divide() {
        if (!temp[1].equals("0")) { 
            // if the second number is not zero, divide as usual
            double result = Double.parseDouble(temp[0]) / Double.parseDouble(temp[1]); // create result forced double
            System.out.printf("\n%s / %s = \n", temp[0], temp[1]);
            temp[0] = Double.toString(result); // store result
            System.out.printf("temp[%d]: %s\n", position, temp[0]);
            System.out.printf("addBool: "+addBool
                          + "\nsubBool: "+subBool
                          + "\nmulBool: "+mulBool
                          + "\ndivBool: "+divBool);
            int intRep = (int)result;
            if (intRep == result) { 
                // if int == double, cut off decimal and zero
                textArea.setText(temp[0]);
                input = textArea.getText();
                input = input.substring(0, input.length()-2); // input changed to whole number, or int
                temp[0] = input; // update storing
                textArea.setText(temp[0]);
                if (Integer.parseInt(temp[0]) < 0 ) {
                    input = textArea.getText(); // temp[2]
                    System.out.printf("%s", input);
                    input = input.substring(1, input.length());
                    System.out.printf("input: %s",input);
                    textArea.setText(input + "-"); // update textArea
                    System.out.printf("temp[%d] %s\n",position, temp[position]);
                }
            } else {
                // if double == double, keep decimal and number afterwards
                textArea.setText(formatNumber(temp[0]));
            }
        } else if (temp[1].equals("0")) {
            String result = "0";
            System.out.println("\nAttemping to divide by zero");
            textArea.setText("Cannot divide by 0");
            temp[0] = result;
            firstNumBool = true;
        }
    }
    
    public boolean resetOperator(boolean operatorBool) {
        if (operatorBool == true) {
            System.out.printf("\noperatorBool: %s", operatorBool);
            //temp[0] = temp[2];
            temp[1]= "";
            //temp[2] = "";
            System.out.printf("\ntemp[%d]: %s\n",0, temp[0]);
            position = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            input = "";
            return false;
        } else {
            System.out.printf("\noperatorBool: %s", operatorBool);
            //temp[0] = temp[2];
            temp[1]= "";
            //temp[2] = "";
            System.out.printf("\ntemp[%d]: %s\n",0, temp[0]);
            position = 1;
            dotButtonPressed = false;
            firstNumBool = false;
            input = "";
            return true;
        }
    }
    
    // used to keep 2 decimals on the number at all times
    public String formatNumber(String num) {
        DecimalFormat df = new DecimalFormat("0.00"); 
        double number = Double.parseDouble(num);
        num = df.format(number);
        System.out.printf("\nFormated: %s\n", num);
        return num;
        
    }
    
    public void confirm() {
        System.out.println("\nConfirm Results: ");
        System.out.println("---------------- ");
        for(int i=0; i<5; i++)
            System.out.printf("temp[%d]: \'%s\'\n",i, temp[i]);
        System.out.printf("input: \'%s\'\n"
                        + "inputLength: %d\n"
                        + "textArea: \'%s\'\n"
                        + "addBool: %s\n"
                        + "subBool: %s\n"
                        + "mulBool: %s\n"
                        + "divBool: %s\n"
                        + "position: %s\n"
                        + "firstNumBool: %s\n"
                        + "dotButtonPressed: %s\n",
                input, input.length(), textArea.getText(), addBool, subBool, mulBool, divBool, position, firstNumBool, dotButtonPressed);
        
        System.out.println("\n---------------- ");
    }
    
    protected void setTemp(String input) {
        temp[position] = input;
    }
    
    public void convertToBinary() {
        String text = textArea.getText();
        int number = Integer.parseInt(text);
        textArea.setText(Integer.toUnsignedString(number, 2));
    }
    
    public void convertToOctal() {
        String text = textArea.getText();
        int number = Integer.parseInt(text);
        textArea.setText(Integer.toUnsignedString(number, 8));
    }
    
    public void convertToDecimal() {
        String text = textArea.getText();
        int number = Integer.parseInt(text);
        textArea.setText(Integer.toUnsignedString(number, 10));
    }
    
    public void convertToHexadecimal() {
        String text = textArea.getText();
        int number = Integer.parseInt(text);
        String newText = Integer.toUnsignedString(number, 16);
        textArea.setText(newText.toUpperCase());
    }
    
    public void setButtonGroup2Mode() {
        buttonBinMode = false;
        buttonOctMode = false;
        buttonDecMode = false;
        buttonHexMode = false;
        if (buttonBin.isSelected())
            buttonBinMode = true;
        else if (buttonOct.isSelected()) 
            buttonOctMode = true;
        else if (buttonDec.isSelected())
            buttonDecMode = true;
        else if (buttonHex.isSelected())
            buttonHexMode = true;
    }
    
} //end class Calculator