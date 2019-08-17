// package version3;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.ComponentOrientation;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Image;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.text.DecimalFormat;
//
////import com.apple.eawt.Application;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTextArea;
//import javax.swing.KeyStroke;
//import javax.swing.SwingConstants;
//import javax.swing.border.LineBorder;
//
//// Page 945 in textbook
//public final class Calculator extends JFrame {
//
//    private final JTextArea textArea = new JTextArea();
//    private final GridBagLayout standardLayout; // layout of the calculator
//    public GridBagConstraints constraints; // layout's constraints
//    private static JLabel iconLabel;
//    private static JLabel textLabel;
//    ImageIcon calculator = new ImageIcon(getClass().getResource("src/main/resources/images/calculatorOriginal - Copy.jpg"));
//    ImageIcon calculator2 = new ImageIcon(getClass().getResource("src/main/resources/images/calculatorOriginal.jpg"));
//    ImageIcon macLogo = new ImageIcon(getClass().getResource("src/main/resources/images/maclogo.png"));
//    private final Font font = new Font("Segeo UI", Font.BOLD, 14);
//    final private JButton buttonP = new JButton("+");
//    final private JButton buttonE = new JButton("=");
//    final private JButton button0 = new JButton("0");
//    final private JButton button1 = new JButton("1");
//    final private JButton button2 = new JButton("2");
//    final private JButton button3 = new JButton("3");
//    final private JButton button4 = new JButton("4");
//    final private JButton button5 = new JButton("5");
//    final private JButton button6 = new JButton("6");
//    final private JButton button7 = new JButton("7");
//    final private JButton button8 = new JButton("8");
//    final private JButton button9 = new JButton("9");
//    final private JButton buttonDot = new JButton(".");
//    final private JButton buttonM = new JButton("-");
//    final private JButton buttonX = new JButton("*");
//    final private JButton buttonD = new JButton("/");
//    final private JButton buttonF = new JButton("1/x");
//    final private JButton buttonPer = new JButton("%");
//    final private JButton buttonCE = new JButton("CE");
//    final private JButton buttonC = new JButton("C");
//    //final String delete = "\u2190";
//    final private JButton buttonDel = new JButton("DEL");
//    final String negate = "\u00B1";
//    final private JButton buttonN = new JButton(negate);
//    final String SQRT = "\u221A";
//    final private JButton buttonSq = new JButton(SQRT);
//    final private JButton buttonMC = new JButton("MC");
//    final private JButton buttonMR = new JButton("MR");
//    final private JButton buttonMS = new JButton("MS");
//    final private JButton buttonMA = new JButton("M+");
//    final private JButton buttonMSub = new JButton("M-");
//    private final String[] temp = {"","","","",""}; // firstNum, secondNum, total, copy/paste, memory
//    // while temp[2] is no longer used, it was left in because I would have to change every occurrence
//    // of temp[3] to temp[2] and temp[4] to temp[3].
//    private int position = 0;
//    private String textarea = "";
//    Boolean addBool = false, subBool = false, mulBool = false, divBool = false, memAddBool = false, memSubBool = false;
//    Boolean firstNumBool = true;
//    Boolean negatePressed = false;
//    Boolean numberOneNegative = false, numberTwoNegative = false, numberThreeNegative = false;
//    Boolean dotButtonPressed = false;
//    Boolean decimal = false;
//    Boolean dotActive = false;
//
//
//    Boolean standardMode = true; // default mode
//    Boolean binaryMode = false;
//
//    // set up GUI
//    public Calculator() {
//        super("Calculator");
//        standardLayout = new GridBagLayout();
//        setLayout(standardLayout); // set frame layout
//        constraints = new GridBagConstraints(); // instanitate constraints
//        setInitialStartMode();
//    }
//
//    public void setupMenuBar() {
//        // Menu Bar and Options
//        JMenuBar bar = new JMenuBar(); // create menu bar
//        setJMenuBar(bar); // add menu bar to application
//
//        // View Menu and Actions
//        JMenu viewMenu = new JMenu("View"); // create view menu
//        viewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//        bar.add(viewMenu); // add viewMenu to menu bar
//
//            JMenuItem standard = new JMenuItem("Standard");
//            standard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//
//            JMenuItem binary = new JMenuItem("Binary");
//            standard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//
//            viewMenu.add(standard);
//            standard.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent event) {
//                    standardMode = setMode(true);
//                    setStandardMode();
//                    // needs more here
//                }
//            });
//
//            viewMenu.add(binary);
//            binary.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent event) {
//                    binaryMode = setMode(true);
//                    setBinaryMode();
//                }
//            });
//        // Edit Menu and Actions
//        JMenu editMenu = new JMenu("Edit"); // create edit menu
//        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//        bar.add(editMenu); // add editMenu to menu bar
//
//            JMenuItem copyItem = new JMenuItem("Copy");
//            copyItem.setAccelerator(KeyStroke.getKeyStroke(
//                java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
//            copyItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                // create first item in editMenu
//            JMenuItem pasteItem = new JMenuItem("Paste");
//            pasteItem.setAccelerator(KeyStroke.getKeyStroke(
//                java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK));
//            pasteItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                // create second item in editMenu
//            JMenu historyMenu = new JMenu("History");
//            historyMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                /// create third time in editMenu
//
//            // add JMenuItems to editMenu
//            editMenu.add(copyItem);
//            copyItem.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    temp[3] = textArea.getText(); // to copy
//                    System.out.println("\ntemp[3]: " + temp[3]);
//                }
//            });
//
//            editMenu.add(pasteItem);
//            pasteItem.addActionListener(
//            new ActionListener()
//            {
//                // paste from copyItem
//                @Override
//                public void actionPerformed(ActionEvent event)
//                {
//                    if (temp[3].equals(""))
//                        System.out.println("Temp[3] is null");
//                    else
//                        System.out.println("\ntemp[3]: " + temp[3]);
//                    textArea.setText(temp[3]); // to paste
//                    temp[position] = textArea.getText();
//                    System.out.println("textArea: " + textArea.getText());
//                }
//            }
//        );
//            editMenu.addSeparator();
//            editMenu.add(historyMenu);
//
//                JMenuItem copyHistoryItem = new JMenuItem("Copy History");
//                copyHistoryItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                    // create first item in historyMenu
//                JMenuItem editItem = new JMenuItem("Edit");
//                editItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                    // create second item in historyMenu
//                JMenuItem cancelEditItem = new JMenuItem("Cancel Edit");
//                cancelEditItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                    // create third item in historyMenu
//                JMenuItem clearItem = new JMenuItem("Clear");
//                clearItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                    // create fourth item in historyMenu
//
//                    // add JMenuItems to historyMenu
//                    historyMenu.add(copyHistoryItem);
//                    historyMenu.add(editItem);
//                    historyMenu.add(cancelEditItem);
//                    historyMenu.add(clearItem);
//
//        // Help  Menu and Actions
//        JMenu helpMenu = new JMenu("Help"); // create help menu
//        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//        bar.add(helpMenu); // add helpMenu to menu bar
//
//            JMenuItem viewHelpItem = new JMenuItem("View Help");
//            viewHelpItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//
//            JMenuItem aboutCalculatorItem = new JMenuItem("About Calculator");
//            aboutCalculatorItem.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
//                // create second item in helpMenu
//
//                // add JMenuItems to helpMenu
//                helpMenu.add(viewHelpItem);
//                // NEEDS UPDATING
//                // Needs a scroll bar
//                // Text with hyperlinks
//                // Info about how to use
//                viewHelpItem.addActionListener(new ActionListener() {
//                    // display message dialog box when user selects Help....
//                    @Override
//                    public void actionPerformed(ActionEvent event) {
//                        String COPYRIGHT = "\u00a9";
//                        iconLabel = new JLabel();
//                        iconLabel.setIcon(calculator);
//                        //iconLabel.setText(" ");
//                        JPanel iconPanel = new JPanel(new GridBagLayout() );
//
//                        iconPanel.add(iconLabel);
//                        textLabel = new JLabel("<html>Apple MacBook Air "
//                            + "Version 3.0.1<br>"
//                            + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
//                            + "Mac OS mojave and its user interface are protected by trademark and all other<br>"
//                            + "pending or existing intellectual property right in the United States and other<br>"
//                            + "countries/regions."
//                            + "<br><br><br>"
//                            + "This product is licensed under the License Terms to:<br>"
//                            + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
//                        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
//                        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
//
//                        JPanel mainPanel = new JPanel();
//                        mainPanel.setBackground(Color.white);
//                        mainPanel.add(iconLabel);
//                        mainPanel.add(textLabel);
//                        JOptionPane.showMessageDialog(Calculator.this,
//                            mainPanel, "Help", JOptionPane.PLAIN_MESSAGE);
//                    }
//                }
//            );
//                helpMenu.addSeparator();
//                helpMenu.add(aboutCalculatorItem);
//                aboutCalculatorItem.addActionListener(
//                new ActionListener() // anonymous inner class
//                {
//                    // display message dialog box when user selects About....
//                    @Override
//                    public void actionPerformed(ActionEvent event)
//                    {
//                        String COPYRIGHT = "\u00a9";
//                        iconLabel = new JLabel(calculator2);
//                        //iconLabel.setIcon(calculator2);
//                        //iconLabel.setVisible(true);
//                        JPanel iconPanel = new JPanel(new GridBagLayout() );
//                        iconPanel.add(iconLabel);
//                        textLabel = new JLabel("<html>Apple MacBook Air"
//                            + "Version 3.0.1 (Build 1)<br>"
//                            + COPYRIGHT + " 2018 Microsoft Corporation. All rights reserved.<br><br>"
//                            + "Mac OS mojave and its user interface are<br>"
//                            + "protected by trademark and all other pending or existing intellectual property<br>"
//                            + "right in the United States and other countries/regions."
//                            + "<br><br><br>"
//                            + "This product is licensed under the License Terms to:<br>"
//                            + "Michael Ball</html>", macLogo, SwingConstants.LEFT);
//                        textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
//                        textLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
//
//                        JPanel mainPanel = new JPanel();
//                        mainPanel.add(iconLabel);
//                        mainPanel.add(textLabel);
//                        JOptionPane.showMessageDialog(Calculator.this,
//                            mainPanel, "About Calculator", JOptionPane.PLAIN_MESSAGE);
//                    }
//                }
//            );
//    } // end public setMenuBar
//
//    public void setInitialStartMode() {
//        // create GUI components
//        /* 1) */ final NumberButtonHandler buttonHandler = new NumberButtonHandler(); // This handler handles btns 0 - 9
//        /* 11) */ final AddButtonHandler addButtonHandler = new AddButtonHandler();
//        /* 12) */ final SubtractButtonHandler subButtonHandler = new SubtractButtonHandler();
//        /* 13) */ final MultiplyButtonHandler mulButtonHandler = new MultiplyButtonHandler();
//        /* 14) */ final DivideButtonHandler divButtonHandler = new DivideButtonHandler();
//        /* 15) */ final EqualsButtonHandler equalsButtonHandler = new EqualsButtonHandler();
//        /* 16) */ final ClearButtonHandler clearButtonHandler = new ClearButtonHandler();
//        /* 17) */ final ClearEntryButtonHandler CEButtonHandler = new ClearEntryButtonHandler();
//        /* 18) */ final SqButtonHandler sqrtButtonHandler = new SqButtonHandler();
//        /* 19) */ final NegateButtonHandler negButtonHandler = new NegateButtonHandler();
//        /* 20) */ final DeleteButtonHandler delButtonHandler = new DeleteButtonHandler();
//        /* 21) */ final PercentButtonHandler perButtonHandler = new PercentButtonHandler();
//        /* 22) */ final DotButtonHandler dotButtonHandler = new DotButtonHandler();
//        /* 23) */ final FracButtonHandler fracButtonHandler = new FracButtonHandler();
//        /* 24) */ final MemoryClearButtonHandler memoryClearButtonHandler = new MemoryClearButtonHandler();
//        /* 25) */ final MemoryRecallButtonHandler memoryRecallButtonHandler = new MemoryRecallButtonHandler();
//        /* 26) */ final MemoryStoreButtonHandler memoryStoreButtonHandler = new MemoryStoreButtonHandler();
//        /* 27) */ final MemoryAddButtonHandler memoryAddButtonHandler = new MemoryAddButtonHandler();
//        /* 28) */ final MemorySubButtonHandler memorySubButtonHandler = new MemorySubButtonHandler();
//
//        constraints.insets = new Insets(5,5,5,5); //THIS LINE ADDS PADDING; LOOK UP TO LEARN MORE
//
//        // Set text area and buttons size and font
//        textArea.getCaret().isSelectionVisible();
//        textArea.setFont(font);
//        textArea.setPreferredSize(new Dimension(70, 35));
//        textArea.setEditable(false);
//        button0.setFont(font);
//        button0.setPreferredSize(new Dimension(70, 35) );
//        button0.setBorder(new LineBorder(Color.BLACK));
//        button0.setEnabled(true);
//        button1.setFont(font);
//        button1.setPreferredSize(new Dimension(35, 35) );
//        button1.setBorder(new LineBorder(Color.BLACK));
//        button1.setEnabled(true);
//        button2.setFont(font);
//        button2.setPreferredSize(new Dimension(35, 35) );
//        button2.setBorder(new LineBorder(Color.BLACK));
//        button2.setEnabled(true);
//        button3.setFont(font);
//        button3.setPreferredSize(new Dimension(35, 35) );
//        button3.setBorder(new LineBorder(Color.BLACK));
//        button3.setEnabled(true);
//        button4.setFont(font);
//        button4.setPreferredSize(new Dimension(35, 35) );
//        button4.setBorder(new LineBorder(Color.BLACK));
//        button4.setEnabled(true);
//        button5.setFont(font);
//        button5.setPreferredSize(new Dimension(35, 35) );
//        button5.setBorder(new LineBorder(Color.BLACK));
//        button5.setEnabled(true);
//        button6.setFont(font);
//        button6.setPreferredSize(new Dimension(35, 35) );
//        button6.setBorder(new LineBorder(Color.BLACK));
//        button6.setEnabled(true);
//        button7.setFont(font);
//        button7.setPreferredSize(new Dimension(35, 35) );
//        button7.setBorder(new LineBorder(Color.BLACK));
//        button7.setEnabled(true);
//        button8.setFont(font);
//        button8.setPreferredSize(new Dimension(35, 35) );
//        button8.setBorder(new LineBorder(Color.BLACK));
//        button8.setEnabled(true);
//        button9.setFont(font);
//        button9.setPreferredSize(new Dimension(35, 35) );
//        button9.setBorder(new LineBorder(Color.BLACK));
//        button9.setEnabled(true);
//        buttonDot.setFont(font);
//        buttonDot.setPreferredSize(new Dimension(35, 35) );
//        buttonDot.setBorder(new LineBorder(Color.BLACK));
//        buttonDot.setEnabled(true);
//        buttonP.setFont(font);
//        buttonP.setPreferredSize(new Dimension(35, 35) );
//        buttonP.setBorder(new LineBorder(Color.BLACK));
//        buttonP.setEnabled(true);
//        buttonM.setFont(font);
//        buttonM.setPreferredSize(new Dimension(35, 35) );
//        buttonM.setBorder(new LineBorder(Color.BLACK));
//        buttonM.setEnabled(true);
//        buttonX.setFont(font);
//        buttonX.setPreferredSize(new Dimension(35, 35) );
//        buttonX.setBorder(new LineBorder(Color.BLACK));
//        buttonX.setEnabled(true);
//        buttonD.setFont(font);
//        buttonD.setPreferredSize(new Dimension(35, 35) );
//        buttonD.setBorder(new LineBorder(Color.BLACK));
//        buttonD.setEnabled(true);
//        buttonE.setFont(font);
//        buttonE.setPreferredSize(new Dimension(35, 70) );
//        buttonE.setBorder(new LineBorder(Color.BLACK));
//        buttonE.setEnabled(true);
//        buttonF.setFont(font);
//        buttonF.setPreferredSize(new Dimension(35, 35) );
//        buttonF.setBorder(new LineBorder(Color.BLACK));
//        buttonF.setEnabled(true);
//        buttonPer.setFont(font);
//        buttonPer.setPreferredSize(new Dimension(35, 35) );
//        buttonPer.setBorder(new LineBorder(Color.BLACK));
//        buttonPer.setEnabled(true);
//        buttonCE.setFont(font);
//        buttonCE.setPreferredSize(new Dimension(35, 35) );
//        buttonCE.setBorder(new LineBorder(Color.BLACK));
//        buttonCE.setEnabled(true);
//        buttonC.setFont(font);
//        buttonC.setPreferredSize(new Dimension(35, 35) );
//        buttonC.setBorder(new LineBorder(Color.BLACK));
//        buttonC.setEnabled(true);
//        buttonN.setFont(font);
//        buttonN.setPreferredSize(new Dimension(35, 35) );
//        buttonN.setBorder(new LineBorder(Color.BLACK));
//        buttonN.setEnabled(true);
//        buttonSq.setFont(font);
//        buttonSq.setPreferredSize(new Dimension(35, 35) );
//        buttonSq.setBorder(new LineBorder(Color.BLACK));
//        buttonSq.setEnabled(true);
//        buttonDel.setFont(font);
//        buttonDel.setPreferredSize(new Dimension(35, 35) );
//        buttonDel.setBorder(new LineBorder(Color.BLACK));
//        buttonDel.setEnabled(true);
//        buttonMC.setFont(font);
//        buttonMC.setPreferredSize(new Dimension(35, 35) );
//        buttonMC.setBorder(new LineBorder(Color.BLACK));
//        if (temp[4].equals("")) {
//            buttonMC.setEnabled(false);
//        }
//        buttonMR.setFont(font);
//        buttonMR.setPreferredSize(new Dimension(35, 35) );
//        buttonMR.setBorder(new LineBorder(Color.BLACK));
//        if (temp[4].equals("")) {
//            buttonMR.setEnabled(false);
//        }
//        buttonMS.setFont(font);
//        buttonMS.setPreferredSize(new Dimension(35, 35) );
//        buttonMS.setBorder(new LineBorder(Color.BLACK));
//        buttonMS.setEnabled(true);
//        buttonMA.setFont(font);
//        buttonMA.setPreferredSize(new Dimension(35, 35) );
//        buttonMA.setBorder(new LineBorder(Color.BLACK));
//        buttonMA.setEnabled(true);
//        buttonMSub.setFont(font);
//        buttonMSub.setPreferredSize(new Dimension(35, 35) );
//        buttonMSub.setBorder(new LineBorder(Color.BLACK));
//        buttonMSub.setEnabled(true);
//        // anchor for all components is CENTER: the default
//        constraints.fill = GridBagConstraints.BOTH;
//        addComponent(textArea, 0, 0, 5, 2);
//
//        // Also add the action listener for each button
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonMC, 2, 0, 1, 1);
//        buttonMC.addActionListener(memoryClearButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonMR, 2, 1, 1, 1);
//        buttonMR.addActionListener(memoryRecallButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonMS, 2, 2, 1, 1);
//        buttonMS.addActionListener(memoryStoreButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonMA, 2, 3, 1, 1);
//        buttonMA.addActionListener(memoryAddButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonMSub, 2, 4, 1, 1);
//        buttonMSub.addActionListener(memorySubButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button0, 7, 0, 2, 1);
//        button0.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonDot, 7, 2, 1, 1);
//        buttonDot.addActionListener(dotButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonP, 7, 3, 1, 1);
//        buttonP.addActionListener(addButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button1, 6, 0, 1, 1);
//        button1.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button2, 6, 1, 1, 1);
//        button2.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button3, 6, 2, 1, 1);
//        button3.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonM, 6, 3, 1, 1);
//        buttonM.addActionListener(subButtonHandler);
//        constraints.fill = GridBagConstraints.BOTH;
//        addComponent(buttonE, 6, 4, 1, 2); // idk why its size is not showing on the application; leave a comment for me on why this is
//        buttonE.addActionListener(equalsButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button4, 5, 0, 1, 1);
//        button4.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button5, 5, 1, 1, 1);
//        button5.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button6, 5, 2, 1, 1);
//        button6.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonX, 5, 3, 1, 1);
//        buttonX.addActionListener(mulButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonF, 5, 4, 1, 1);
//        buttonF.addActionListener(fracButtonHandler);
//
//
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button7, 4, 0, 1, 1);
//        button7.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button8, 4, 1, 1, 1);
//        button8.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(button9, 4, 2, 1, 1);
//        button9.addActionListener(buttonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonD, 4, 3, 1, 1);
//        buttonD.addActionListener(divButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonPer, 4, 4, 1, 1);
//        buttonPer.addActionListener(perButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonDel, 3, 0, 1, 1);
//        buttonDel.addActionListener(delButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonCE, 3, 1, 1, 1);
//        buttonCE.addActionListener(CEButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonC, 3, 2, 1, 1);
//        buttonC.addActionListener(clearButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonN, 3, 3, 1, 1);
//        buttonN.addActionListener(negButtonHandler);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        addComponent(buttonSq, 3, 4, 1, 1);
//        buttonSq.addActionListener(sqrtButtonHandler);
//
//    // above this comment is all for the buttons
//    } // end method setInitalStartMode()
//
//    /**
//     * This method does two things:
//     * Clears any decimal found.
//     * Clears all zeroes after decimal (if that is the case).
//     * @param currentNumber
//     * @return updated currentNumber
//     */
//    private String clearZeroesAtEnd(String currentNumber) {
//        System.out.println("starting clearZeroesAtEnd()");
//        System.out.println("currentNumber: " + currentNumber);
//        textarea = currentNumber; // copy of currentNumber
//        boolean justZeroes = true;
//        int index = 0;
//        index = textarea.indexOf(".");
//        if (index == -1) return currentNumber;
//        //int indexStartPosition = index;
//        //int keepIndex = index;
//        // this for loop checks first to make sure its all zeroes after the decimal point
//        check:
//        for(int i = index + 1; i < textarea.length(); i++) {
//            // from decimal point to the end of the number
//            //System.out.println("i: " + i);
//            // eliminate decimal first
//            if (!textarea.substring(i, i+1).equals("0")) {
//                // if the string value at position x is not a 0
//                justZeroes = false;
//                break check;
//            }
//        }
//        if (justZeroes == true) {
//            // happy path; delete all from index onward
//            textarea = textarea.substring(0,index);
//        }
//
//        System.out.println("output of clearZeroesAtEnd(): " + textarea);
//        return textarea;
//    }
//
//    // method that tells what to do for numerical textarea
//    public class NumberButtonHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nNumberButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//            if (firstNumBool == true) {
//                if (memAddBool == true || memSubBool == true) { // || position == 0: essentially resetting value
//                    textArea.setText("");
//                } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                    textArea.setText(temp[0]);
//                    position = 0;
//                    firstNumBool = true;
//                    dotButtonPressed = false;
//                    dotActive = false;
//                } else if (textArea.getText().equals("\n0")) {
//                    textArea.setText("");
//                    temp[position] = "";
//                    firstNumBool = true;
//                    dotButtonPressed = false;
//                    dotActive = false;
//                } else if (temp[0].equals("") && !temp[1].equals("")) {
//                    temp[0] = temp[1];
//                    temp[1] = "";
//                    position = 0;
//                }
//                confirm();
//                if (dotButtonPressed == false && !textArea.getText().endsWith("-")) {
//                    System.out.println("\nfirstNumBool = true \ndotButtonPressed = false");
//                    System.out.print("before: " + textArea.getText().replaceAll("\n", ""));
//                    textArea.setText("\n" + textArea.getText().replaceAll("\n", "") + e.getActionCommand()); // update textArea
//                    temp[position] = textArea.getText().replaceAll("\n", ""); // store textarea
//                    System.out.printf("\ntextArea: %s\n", textArea.getText());
//                    System.out.printf("temp[%d]: %s\n",position, temp[position]);
//                    //s = s.substring(0,s.length());
//                } else if (dotButtonPressed == false) { // logic for negative numbers
//                    System.out.println("firstNumBool = true \ndotButtonPressed = false \nnegative number = true");
//                    textarea = temp[position];
//                    System.out.printf("\ntextarea: %s\n", textarea);
//                    textarea = textarea + e.getActionCommand(); // update textArea
//                    System.out.printf("\ntextarea: %s", textarea);
//                    temp[position] = textarea; // store textarea
//                    textArea.setText("\n" + convertToPositive(textarea) + "-");
//                    System.out.printf("\ntextArea: %s\n", textArea.getText());
//                    System.out.printf("temp[%d]: %s\n",position, temp[position]);
//                }
//                else { // dotPressed = true
//                    System.out.println("firstNumBool = true : dotButtonPressed = true");
//                    if (textarea.equals(".0")) {
//                        textArea.setText("0.");
//                        textArea.setText(textArea.getText() + e.getActionCommand()); // update textArea
//                        temp[position] =  textArea.getText(); // store textarea
//                        textArea.setText("\n" + textArea.getText());
//                        System.out.printf("\ttextArea: %s\n", textArea.getText());
//                        System.out.printf("temp[%d]: %s\n",position, temp[position]);
//                        //s = s.substring(0,s.length());
//                        System.out.println("\nbutton: " + e.getActionCommand()); // print out button confirmation
//                        //temp[0] = s;
//                        textarea = "";
//                        System.out.printf("dotButtonPressed: %s\n", dotButtonPressed);
//                        System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored textarea confirmation
//                        dotButtonPressed = false;
//                        System.out.printf("dotButtonPressed: %s\n", dotButtonPressed);
//                    } else if (temp[position].endsWith("-.0")) {
//                        textarea = temp[position];
//                        System.out.printf("\ntextarea: %s\n", textarea);
//                        textarea = textarea.substring(2, textarea.length());
//                        //System.out.printf("\ntextarea: %s\n", textarea);
//                        textarea = textarea + "." + e.getActionCommand();
//                        System.out.printf("\ntextarea: -%s\n", textarea);
//                        textarea = "-" + textarea;
//                        //System.out.printf("\ntextarea: -%s\n", textarea);
//                        textArea.setText("\n" + textarea.replaceAll("-", "") + "-");
//                        temp[position] = textarea;
//                    } else if (temp[position].startsWith("-0.")){
//                        textarea = textArea.getText().replaceAll("\\n", ""); // collect textarea
//                        System.out.println("Old " + textarea);
//                        textarea = textarea.substring(0, textarea.length()-1);
//                        System.out.println("New " + textarea);
//                        textArea.setText("\n" + textarea + e.getActionCommand() + "-"); // update textArea
//                        temp[position] = textArea.getText().replaceAll("\\n", ""); // update textarea with decimal
//                        //String number = s + "." + event.getActionCommand(); // 504 + . + textarea
//                        //dotButtonPressed = false;
//                        //System.out.println("TextArea: " + textArea.getText());
//                        System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//                        System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored textarea confirmation
//                        textarea = textArea.getText().replaceAll("\\n", "");
//                    }
//                }
//            }
//            else { // do for second number
//                if (firstNumBool == false && dotButtonPressed == false) {
//                    textArea.setText("");
//                    System.out.println("\nResetting textArea\n");
//                    if (firstNumBool == false)
//                        firstNumBool = true;
//                    else
//                        dotButtonPressed = true;
//                    buttonDot.setEnabled(true);
//                }
//                //System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//                if (textarea.equals(".0"))  {
//                    textArea.setText("\n" + "0.");
//                    textarea = "0.";
//                } else if (textarea.equals("0")) {
//                	temp[position] = "";
//                    textArea.setText("\n");
//                    position = 1;
//                    firstNumBool = true;
//                    dotButtonPressed = false;
//                    dotActive = false;
//                } else if (temp[0].equals("") && !temp[1].equals("") && negatePressed == false) {
//                    temp[0] = temp[1];
//                    temp[1] = "";
//                    position = 1;
//                }
//                //textArea.setText(null);
//                // position++; // increase position for storing textarea
//                if (firstNumBool == false && negatePressed == false) {
//
//                    //textArea.setText(textArea.getText() + e.getActionCommand());
//                    textArea.setText("\n " + textarea.replaceAll("\n", "") + e.getActionCommand());
//                    temp[position] = textArea.getText(); // store textarea
//                    System.out.printf("textArea: %s\n", textArea.getText());
//                    System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored textarea confirmation
//                } else if (firstNumBool == true && negatePressed == true) {
//                	// we did something such as 9 + -
//                	// indicating that the second number will be negative
//                	//textArea.setText(e.getActionCommand()+"-");
//                	textArea.setText("\n " + e.getActionCommand() + "-" + textarea.replaceAll("\n", ""));
//                	System.out.printf("textArea: %s\n", textArea.getText());
//                	temp[position] = convertToNegative(e.getActionCommand());
//                	negatePressed = false;
//            	} else if (firstNumBool == true && negatePressed == false) {
//                    //textArea.setText(textArea.getText() + e.getActionCommand());
//                    textArea.setText("\n" + e.getActionCommand() + textarea.replaceAll("\n", ""));
//                    temp[position] = textArea.getText().replaceAll("\n", ""); // store textarea
//                    System.out.printf("textArea: %s\n", textArea.getText());
//                    System.out.printf("temp[%d]: %s\n",position, temp[position]); // print out stored textarea confirmation
//                    firstNumBool = true;
//                }
//            }
//            //textarea = textArea.getText();
//            confirm();
//        }
//    }
//
//    public class AddButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nAddButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
////            if (negatePressed == true) {
////                temp[0] = Integer.parseInt( textArea.getText().replace("-", ""));
////                negatePressed = false;
////            }
//            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                //textarea = textArea.getText();
//                // first determine if number is whole number or decimal number
//                //ouble number = Double.parseDouble(textArea.getText());
//                //if (decimal == false) {
//                    //temp[0] = Integer.parseInt(textArea.getText());
//                    //temp[0] = temp[0];
//
//                //textArea.setText(e.getActionCommand() + " " +  textArea.getText()); // "userInput +" // temp[position]
//                textarea = textArea.getText().replaceAll("\\n", "");
//                textArea.setText("\n" + " " + e.getActionCommand() + " " + textarea);
//                textarea = textArea.getText();
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]: %s after addButton pushed\n",position,temp[position]); // confirming proper textarea before moving on
//                //}
//                //else { // if number is double
//                    //temp[0] = Double.parseDouble(textArea.getText());
//                    //textArea.setText(event.getActionCommand() + " " + temp[0] ); // "userInput.textarea +"
//                    //System.out.printf("temp[0] "+temp[0]);
//                //}
//                addBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//                //secondNumBool = true;
//            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
//            	// subBool can be true because it can be a negative number
//            	System.out.println("already chose an operator. choose another number.");
//
//        	} else if (addBool == true) {
//                System.out.println("Performing previous addition calculation");
//                addition();
//                addBool = resetOperator(addBool); // sets addBool to false
//                addBool = true;
//            } else if (subBool == true) {
//                System.out.println("We understand the logic");
//                subtract();
//                subBool = resetOperator(subBool);
//                addBool = true;
//            } else if (mulBool == true) {
//                multiply();
//                mulBool = resetOperator(mulBool);
//                addBool = true;
//            } else if (divBool == true) {
//                divide();
//                divBool = resetOperator(divBool);
//                addBool = true;
//                confirm();
//            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
//                addBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            }
//            buttonDot.setEnabled(true);
//            dotButtonPressed = false;
//            dotActive = false;
//            confirm();
//        }
//    }
//
//    public class SubtractButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nSubtractButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                //textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput -"
//                textArea.setText("\n " + e.getActionCommand() + textarea.replaceAll("\n", ""));
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]: %s after subButton pushed",position,temp[position]); // confirming proper textarea before moving on
//                subBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
//            	// subBool can be true because it can be a negative number
//            	System.out.println("already chose an operator. next number is negative...");
//            	// set negative flag to true
//            	negatePressed = true;
//        	} else if (addBool == true) {
//                addition();
//                addBool = resetOperator(addBool);
//                subBool = true;
//            } else if (subBool == true) {
//                subtract();
//                subBool = resetOperator(subBool);
//                subBool = true;
//            } else if (mulBool == true) {
//                multiply();
//                mulBool = resetOperator(mulBool);
//                subBool = true;
//            } else if (divBool == true) {
//                divide();
//                divBool = resetOperator(divBool);
//                subBool = true;
//            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
//                subBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            }
//            buttonDot.setEnabled(true);
//            dotButtonPressed = false;
//            dotActive = false;
//            confirm();
//        }
//    }
//
//    public class MultiplyButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nMultiplyButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                //textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput *"
//                textArea.setText("\n " + e.getActionCommand() + textarea.replaceAll("\n", ""));
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]: %s after mulButton pushed",position,temp[position]); // confirming proper textarea before moving on
//                mulBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
//            	// subBool can be true because it can be a negative number
//            	System.out.println("already chose an operator. choose another number.");
//
//        	} else if (addBool == true) {
//                addition();
//                addBool = resetOperator(addBool);
//                mulBool = true;
//            } else if (subBool == true) {
//                subtract();
//                subBool = resetOperator(subBool);
//                mulBool = true;
//            } else if (mulBool == true) {
//                multiply();
//                mulBool = resetOperator(mulBool);
//                mulBool = true;
//            } else if (divBool == true) {
//                divide();
//                divBool = resetOperator(divBool);
//                mulBool = true;
//            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
//                mulBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            }
//            buttonDot.setEnabled(true);
//            dotButtonPressed = false;
//            dotActive = false;
//            confirm();
//        }
//    }
//
//    public class DivideButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nDivideButtonHandler started");
//                System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (addBool == false && subBool == false && mulBool == false && divBool == false && !textArea.getText().equals("") && !textArea.getText().equals("Invalid textarea") && !textArea.getText().equals("Cannot divide by 0")) {
//
//                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                //textArea.setText(e.getActionCommand() + " " + textArea.getText() ); // "userInput /"
//                textArea.setText("\n " + e.getActionCommand() + textarea.replaceAll("\n", ""));
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]: %s after divButton pushed",position,temp[position]); // confirming proper textarea before moving on
//                divBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
//            	// subBool can be true because it can be a negative number
//            	System.out.println("already chose an operator. choose another number.");
//
//        	} else if (addBool == true) {
//                addition();
//                addBool = resetOperator(addBool); // sets addBool to false
//                divBool = true;
//            } else if (subBool == true) {
//                subtract();
//                subBool = resetOperator(subBool);
//                divBool = true;
//            } else if (mulBool == true) {
//                multiply();
//                mulBool = resetOperator(mulBool);
//                divBool = true;
//            } else if (divBool == true) {
//                divide();
//                divBool = resetOperator(divBool);
//                divBool = true;
//            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setText(e.getActionCommand() + " " +  temp[0]); // "userInput +" // temp[position]
//                divBool = true; // sets logic for arithmetic
//                firstNumBool = false; // sets logic to perform operations when collecting second number
//                dotButtonPressed = false;
//                position++; // increase position for storing textarea
//                textarea = "";
//            }
//            buttonDot.setEnabled(true);
//            dotButtonPressed = false;
//            dotActive = false;
//            confirm();
//        }
//    }
//
//    public class EqualsButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nEqualsButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            //position++;
//            //CharSequence cs = ".";
//            //double num1D =0.0, num2D=0.0;
//            //int num1=0, num2=0;
//            double result = 0.0;
//
//
//            if (addBool == true) {
//                addition();
//                addBool = resetOperator(addBool);
//                position = 0;
//            }
//            else if (subBool == true){
//                subtract();
//                subBool = resetOperator(subBool);
//                position = 0;
//            }
//            else if (mulBool == true){
//                multiply();
//                mulBool = resetOperator(mulBool);
//                position = 0;
//            }
//            else if (divBool == true){
//                divide();
//                divBool = resetOperator(divBool);
//                position = 0;
//            } else if (temp[0].equals("") && temp[1].equals("")) {
//                // if temp[0] and temp[1] do not have a number
//                position = 0;
//            } else if (textArea.getText().equals("Invalid textarea") || textArea.getText().equals("Cannot divide by 0")) {
//                textArea.setText(e.getActionCommand() + " " +  temp[position]); // "userInput +" // temp[position]
//                position = 1;
//                firstNumBool = true;
//            }
//            temp[1] = ""; // this is not done in addition, subtraction, multiplication, or division
//            textarea = textArea.getText();
//            firstNumBool = true;
//            dotActive = false;
//            confirm();
//        }
//    }
//
//    public class ClearButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nClearButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//            clear();
//            confirm();
//        }
//    }
//
//    public class ClearEntryButtonHandler implements ActionListener // works, supposedly
//    {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nClearEntryButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            textarea = textArea.getText();
//            textArea.setText("");
//            if (temp[1].equals("")) { // if temp[1] is empty, we know we are at temp[0]
//                temp[0] = "";
//                addBool = false;
//                subBool = false;
//                mulBool = false;
//                divBool = false;
//                position = 0;
//                firstNumBool = true;
//                dotButtonPressed = false;
//            } else {
//                temp[1] = "";
//            }
//            buttonDot.setEnabled(true);
//            textarea = textArea.getText();
//            confirm();
//        }
//    }
//
//    // needs double textarea conditioning
//    public class SqButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nSquare Root ButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (!textArea.getText().equals("")) {
//                if (Double.parseDouble(temp[position]) > 0.0) {
//                    temp[position] = Double.toString(Math.sqrt(Double.parseDouble(temp[position]))); //4.0
//                    int intRep = (int)Math.sqrt(Double.parseDouble(temp[position])); // 4
//                    if (temp[position].contains(".")) { // if int == double, cut off decimal and zero
//                        textArea.setText("\n" + temp[position]);
//                        textarea = textArea.getText().replaceAll("\n", "");
//                        textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//                        temp[position] = textarea; // update storing
//                        textArea.setText("\n" + temp[position]);
//                        if (Integer.parseInt(temp[position]) < 0 ) {
//                            textarea = textArea.getText(); // temp[2]
//                            System.out.printf("%s", textarea);
//                            textarea = textarea.substring(1, textarea.length());
//                            System.out.printf("\ntextarea: %s",textarea);
//                            textArea.setText("\n" + textarea.replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
//                            System.out.printf("\ntemp[%d] %s\n",0, temp[0]);
//                        }
//                    }
//                    textArea.setText("\n" + temp[position] );
//                } else { // number is negative
//                    clear();
//                    textArea.setText("Invalid textarea");
//                    System.out.println("textArea: "+textArea.getText());
//                }
//
//            } // end textArea .= ""
//            confirm();
//        }
//    }
//
//    // NegateButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("\u00B1");
//    public class NegateButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nNegateButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            textarea = textArea.getText();
//            //textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//            if (!textArea.getText().equals("")) {
//            // if there is a number in the text area
//                if (isNegativeNumber(temp[position]) == true) { //temp[position].substring(0, 1).equals("-")
//                // if there is already a negative sign
//                    System.out.println("Reversing number back to positive");
//                    temp[position] = convertToPositive(temp[position]);
//                    textArea.setText("\n" + temp[position]);
//                } else  {
//                // whether first or second number, add "-"
//                    temp[position] = textArea.getText().replaceAll("\\n", ""); // textarea
//                    textArea.setText("\n" + temp[position] + "-");
//                    temp[position] = convertToNegative(temp[position]);
//                }
//            }
//            firstNumBool = true;
//            confirm();
//        }
//    }
//
//    public String convertToNegative(String number) {
//        System.out.print("\nconvertToNegative() running");
//        //temp[position] = textArea.getText(); // textarea
//        System.out.println("\nOld: " + number.replaceAll("\\n", ""));
//        //temp[position] = temp[position].substring(1, temp[position].length());
//        System.out.println("New: "  + "-" + number.replaceAll("\\n", ""));
//        //textArea.setText(number + "-"); // update textArea
//        textarea = "-" + number.replaceAll("\\n", "");
//
//        System.out.printf("Converted Number: %s\n", textarea); // confirm();
//        //System.out.printf("temp[%d]: %s\n",position,temp[position]);
//        return textarea;
//    }
//
//    public String convertToPositive(String number) {
//        System.out.println("\nconvertToPositive() running");
//        System.out.printf("Number to convert: %s\n", number);
//        if (number.endsWith("-")) {
//            System.out.printf("Converted Number: %s\n", number.substring(0, number.length()-1) );
//            number = number.substring(0, number.length()-1);
//        } else {
//            System.out.printf("Converted Number: %s\n", number.substring(1, number.length()) );
//            number = number.substring(1, number.length());
//        }
//
//
//
//        return number;
//    }
//
//    // tested: passed
//    public boolean isDecimal(String number) {
//        System.out.println("isDecimal() running");
//        boolean answer = false;
//        //int intRep = (int)Double.parseDouble(result);
//        for(int i = 0; i < number.length(); i++) {
//            if (number.substring(i).startsWith(".")) {
//                //System.out.println("We have a decimal number");
//                answer = true;
//            }
//        }
//        System.out.println("result is = " + answer);
//        return answer;
//    }
//
//    public class DeleteButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nDeleteButtonHandler started");
//            System.out.println("button: " + e.getActionCommand() + "\n"); // print out button confirmation
//            textarea = temp[position];
//            System.out.printf("temp[%d]: %s\n",position, temp[position]);
//            System.out.printf("textarea: %s\n",textarea);
//            boolean isNegative = isNegativeNumber(textarea);
//            dotActive = isDecimal(textarea);
//            //System.out.printf("isNegativeNumber() result: %s\n", isNegative);
//            //System.out.printf("isDecimal() result: %s\n", dotActive);
//
//            if (addBool == false && subBool == false && mulBool == false && divBool == false) {
//                if (isNegative == false) {
//                    // if no operator has been pushed; number is positive; number is whole
//                    if (dotActive == false) {
//                        if (textarea.length() == 1) { // ex: 5
//                            textarea = "";
//                        } else if (textarea.length() == 2) {
//                            textarea = textarea.substring(0, textarea.length()-1);
//                        } else if (textarea.length() >= 2) { // ex: 56
//                            textarea = textarea.substring(0, textarea.length()-1);
//                        }
//                                // dotActive == true and length == 3
//                                //textarea = textarea.substring(0, 1);
//                        System.out.printf("output: %s\n",textarea);
//                        textArea.setText("\n" + textarea);
//                        temp[position] = textarea;
//                        confirm();
//                    }
//                    // if no operator has been pushed; number is positive; number is decimal
//                    else if (dotActive == true) {
//                        if (textarea.length() == 2) { // ex: 3.
//                            textarea = textarea.substring(0, textarea.length()-1); // ex: 3
//                            dotButtonPressed = false;
//                            dotActive = false;
//                            buttonDot.setEnabled(true);
//                        } else if (textarea.length() == 3) { // ex: 3.2 or 0.5
//                            textarea = textarea.substring(0, textarea.length() - 2); // ex: 3 or 0
//                            dotButtonPressed = false;
//                            dotActive = false;
//                            buttonDot.setEnabled(true);
//                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50
//                            textarea = textarea.substring(0, textarea.length() - 1); // inclusive
//                        }
//                        System.out.printf("output: %s\n",textarea);
//                        if (textarea.endsWith(".")) {
//                            textarea = textarea.substring(0,0);
//                        } else if (textarea.startsWith(".")) {
//                            textarea = textarea.substring(1,1);
//                        }
//                        textarea = clearZeroesAtEnd(textarea);
//                        textArea.setText("\n" + textarea);
//                        temp[position] = textarea;
//                        confirm();
//                    }
//                } else if (isNegative == true) {
//                    // if no operator has been pushed; number is negative; number is whole
//                    if (dotActive == false) {
//                        if (textarea.length() == 2) { // ex: -3
//                            textarea = "";
//                            textArea.setText(textarea);
//                            temp[position] = "";
//                        } else if (textarea.length() >= 3) { // ex: -32
//                            textarea = convertToPositive(textarea);
//                            textarea = textarea.substring(0, textarea.length());
//                            textArea.setText(textarea + "-");
//                            temp[position] = "-" + textarea;
//                        }
//                        System.out.printf("\noutput: %s\n",textarea);
//                        confirm();
//                    }
//                    // if no operator has been pushed; number is negative; number is decimal
//                    else if (dotActive == true) {
//                        if (textarea.length() == 4) { // -3.2
//                            textarea = convertToPositive(textarea); // 3.2
//                            textarea = textarea.substring(0, 1); // 3
//                            dotButtonPressed = false;
//                            textArea.setText(textarea + "-");
//                            temp[position] = "-" + textarea;
//                        } else if (textarea.length() > 4) { // ex: -3.25 or -0.00
//                            textarea = convertToPositive(textarea); // 3.00 or 0.00
//                            textarea = textarea.substring(0, textarea.length()); // 3.0 or 0.0
//                            textarea = clearZeroesAtEnd(textarea); // 3 or 0
//                            textArea.setText(textarea + "-");
//                            temp[position] = "-" + textarea;
//                        }
//                        System.out.printf("\noutput: %s\n",textarea);
//                        confirm();
//                    }
//                }
//
//            } else if (addBool == true || subBool == true || mulBool == true || divBool == true) {
//                if (isNegative == false) {
//                    // if an operator has been pushed; number is positive; number is whole
//                    if (dotActive == false) {
//                        if (textarea.length() == 1) { // ex: 5
//                            textarea = "";
//                        } else if (textarea.length() == 2) {
//                            textarea = textarea.substring(0, textarea.length()-1);
//                        } else if (textarea.length() >= 2) { // ex: 56
//                            textarea = textarea.substring(0, textarea.length()-1);
//                        }
//                        System.out.printf("output: %s\n",textarea);
//                        textArea.setText(textarea);
//                        temp[position] = textarea;
//                        confirm();
//                    }
//                    // if an operator has been pushed; number is positive; number is decimal
//                    else if (dotActive == true) {
//                        if (textarea.length() == 2) // ex: 3.
//                            textarea = textarea.substring(0, textarea.length()-1);
//                        else if (textarea.length() == 3) { // ex: 3.2 0.0
//                            textarea = textarea.substring(0, textarea.length()-2); // 3 or 0
//                            dotActive = false;
//                            dotButtonPressed = false;
//                        } else if (textarea.length() > 3) { // ex: 3.25 or 0.50
//                            textarea = textarea.substring(0, textarea.length() -1);
//                            textarea = clearZeroesAtEnd(textarea);
//                        }
//                        System.out.printf("output: %s\n",textarea);
//                        textArea.setText(textarea);
//                        temp[position] = textarea;
//                        confirm();
//                    }
//                } else if (isNegative == true) {
//                    // if an operator has been pushed; number is negative; number is whole
//                    if (dotActive == false) {
//                        if (textarea.length() == 2) { // ex: -3
//                            textarea = "";
//                            textArea.setText(textarea);
//                            temp[position] = "";
//                        } else if (textarea.length() >= 3) { // ex: -32
//                            textarea = convertToPositive(textarea);
//                            textarea = textarea.substring(0, textarea.length());
//                            textArea.setText(textarea + "-");
//                            temp[position] = "-" + textarea;
//                        }
//                        System.out.printf("\ntextarea: %s\n",textarea);
//                        confirm();
//                    }
//                    // if an operator has been pushed; number is negative; number is decimal
//                    else if (dotActive == true) {
//                        if (textarea.length() == 4) { // -3.2
//                            textarea = convertToPositive(textarea); // 3.2 or 0.0
//                            textarea = textarea.substring(0, 1); // 3 or 0
//                            dotActive = false;
//                            dotButtonPressed = false;
//                            textArea.setText(textarea + "-"); // 3- or 0-
//                            temp[position] = "-" + textarea; // -3 or -0
//                        } else if (textarea.length() > 4) { // ex: -3.25  or -0.00
//                            textarea = convertToPositive(textarea); // 3.25 or 0.00
//                            textarea = textarea.substring(0, textarea.length()); // 3.2 or 0.0
//                            textarea = clearZeroesAtEnd(textarea);
//                            System.out.println("textarea: " + textarea);
//                            if (textarea.equals("0")) {
//                                textArea.setText(textarea);
//                                temp[position] = textarea;
//                            } else {
//                                textArea.setText(textarea + "-");
//                                temp[position] = "-" + textarea;
//                            }
//                        }
//                        System.out.printf("\ntextarea: %s\n",textarea);
//                        confirm();
//                    }
//                }
//            }
//            dotActive = false;
//        }
//
//    }
//
//    public boolean isNegativeNumber(String result) {
//        System.out.println("\nisNegativeNumber() running");
//        boolean answer = false;
//        //int intRep = (int)Double.parseDouble(result);
//        if (result.startsWith("-")) { // if int == double, cut off decimal and zero
//            answer = true;
//        }
//        System.out.println("result is = " + answer);
//        return answer;
//    }
//
//    // PercentButtonHandler operates on this button:
//    // final private JButton buttonPer = new JButton("%");
//    public class PercentButtonHandler implements ActionListener
//    {
//        @Override
//        public void actionPerformed(ActionEvent e)
//        {
//            System.out.println("\nPercentStoreButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (!textArea.getText().equals("")) {
//                //if(textArea.getText().substring(textArea.getText().length()-1).equals("-")) { // if the last index equals '-'
//                // if the number is negative
//                if (isNegativeNumber(textArea.getText().replaceAll("\\n", ""))) {
//                    System.out.println("if condition true");
//                    //temp[position] = textArea.getText(); // textarea
//                    double percent = Double.parseDouble(temp[position]);
//                    percent /= 100;
//                    System.out.println("percent: "+percent);
//                    temp[position] = Double.toString(percent);
//                    textarea = formatNumber(temp[position]);
//                    System.out.println("\nOld " + temp[position]);
//                    temp[position] = temp[position].substring(1, temp[position].length());
//                    System.out.println("New " + temp[position] + "-");
//                    textArea.setText(temp[position] + "-"); // update textArea
//                    System.out.printf("temp[%d] %s\n",position,temp[position]);
//                    //textArea.setText(textarea);
//                    temp[position] = textarea;//+textarea;
//                    textarea = "";
//                    System.out.printf("temp[%d] %s\n",position,temp[position]);
//                    System.out.println("textArea: "+textArea.getText());
//                } else {
//                    double percent = Double.parseDouble(temp[position]);
//                    percent /= 100;
//                    temp[position] = Double.toString(percent);
//                    textArea.setText("\n" + formatNumber(temp[position]));
//                    temp[position] = textArea.getText().replaceAll("\\n", "");
//                    System.out.printf("temp[%d] %s\n",position,temp[position]);
//                    System.out.println("textArea: "+textArea.getText());
//                }
//            }
//            dotButtonPressed = true;
//            dotActive = true;
//            textarea = textArea.getText();
//            confirm();
//        }
//    }
//
//    // DotButtonHandler operates on this button:
//    // final private JButton buttonDot = new JButton(".");
//    public class DotButtonHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent event) {
//            System.out.println("\nDotButtonHandler");
//            System.out.println("button: " + event.getActionCommand() + "\n"); // print out button confirmation
//            if (!temp[position].equals("")) { // if the textarea is not null
//                System.out.printf("dotButtonPressed: %s\ndotActive: %s\n", dotButtonPressed, dotActive);
////                if (!temp[position].endsWith(".")) {
////                    dotButtonPressed = false;
////                    dotActive = false;
////                }
//
//                System.out.printf("temp[%d]: %s before appending dot\n",position,temp[position]);
//                //number = number.substring(0,number.length()-1);
//                //System.out.printf("\ns adjusted: %s",number);
//                if (dotButtonPressed == false || dotActive == false) {
//                    //String x = s.substring(0,s.length()-1);
//                    textArea.setText("\n" + "."+temp[position]);
//                    System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                    temp[position] = temp[position] + ".";
//                    firstNumBool = true;
//                    dotButtonPressed = true;
//                    //decimal = true;
//                }
//                //System.out.printf("temp[0] %.2f", temp[0]);
//            } else { // the first button we are pushing is the dot operator
//                textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//                if (firstNumBool == false)
//                    textArea.setText("");
//                textarea = ".0";
//                textArea.setText("\n" + textarea);
//                temp[position] = "0.";
//                dotButtonPressed = true;
//                //decimal = true;
//            }
//            dotActive = true;
//            buttonDot.setEnabled(false);
//            confirm();
//        }
//    }
//
//    // FracButtonHandler operates on this button:
//    // final private JButton buttonF = new JButton("1/x");
//    public class FracButtonHandler implements ActionListener // not working
//    {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nFracStoreButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (!textArea.getText().equals("")) {
//                double result = Double.parseDouble(temp[position]);
//                result = 1 / result;
//                System.out.printf("result: %.2f", result);
//                temp[position] = Double.toString(result);
//                textArea.setText("\n" + temp[position]);
//                System.out.printf("\ntemp[%d]: %s\n",position, temp[position]);
//            }
//            confirm();
//        }
//    }
//
//    // MemoryClearButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("MC");
//    public class MemoryClearButtonHandler implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("MemoryClearButtonHandler started");
//            System.out.println("\nbutton: " + e.getActionCommand()); // print out button confirmation
//            temp[4] = "";
//            System.out.println("temp[4]: " + temp[4]);
//            buttonMR.setEnabled(false);
//            buttonMC.setEnabled(false);
//            memAddBool = false;
//            memSubBool = false;
//        }
//
//    }
//
//    // MemoryClearButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("MR");
//    public class MemoryRecallButtonHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nMemoryRecallButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if (Double.parseDouble(temp[4]) < 0.0) {
//                textarea = temp[4];
//                System.out.printf("%s\n", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("textarea: %s\n",textarea);
//                textarea = textarea + "-";
//                temp[position] = temp[4];
//                textArea.setText("\n" + textarea); // update textArea
//                System.out.printf("temp[%d] %s\n",position, temp[position]);
//            } else {
//                textArea.setText("\n" + temp[4]);
//                temp[position] = temp[4];
//                System.out.printf("temp[%d]: %s\n",position, temp[position]);
//                System.out.println("textArea: " + textArea.getText());
//            }
//        }
//
//    }
//
//    // MemoryClearButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("MS");
//    public class MemoryStoreButtonHandler implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nMemoryStoreButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            if(!textArea.getText().equals("") && !temp[position].equals("")) { // [position-1]
//            // if text area has a number AND if storage has a number
//                temp[4] = temp[position];
//                System.out.println("textArea: " + textArea.getText());
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//                buttonMR.setEnabled(true);
//                buttonMC.setEnabled(true);
//            } else if (!temp[4].equals("")) {
//            // if memory does not have a number
//                textarea = textArea.getText();
//                double result = Double.parseDouble(temp[4]) + Double.parseDouble(textArea.getText());
//                System.out.append("result: " + result);
//                textArea.setText(Double.toString(result));
//                temp[4] = Double.toHexString(result);
//            }
//            confirm();
//        }
//    }
//
//    // MemoryAddButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("M+");
//    public class MemoryAddButtonHandler implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nMemoryAddButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
////            if (negatePressed == true) {
////                temp[0] = Integer.parseInt( textArea.getText().replace("-", ""));
////                negatePressed = false;
////            }
//            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//            if(!textArea.getText().equalsIgnoreCase("") && !temp[4].equals("")) {
//            // if text area has a number and memory storage has a number
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]:(memory) %s\n", 4, temp[4]);
//                System.out.printf("temp[%d]:(add to) %s\n", position, temp[position]);
//                double result = Double.parseDouble(temp[4]) + Double.parseDouble(temp[position]); // create result forced double
//                System.out.printf("\n%s + %s = %.0f\n", temp[4], temp[position], result);
//                temp[4] = Double.toString(result); // store result
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//                if (result % 1 == 0) { // if int == double, cut off decimal and zero
//                    //textArea.setText(temp[4]);
//                    textarea = temp[4];
//                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//                    temp[4] = textarea; // update storing
//                    System.out.println("update storing: " + textarea);
//                    //textArea.setText(temp[2]);
//                    if (Integer.parseInt(temp[4]) < 0 ) { // if result is negative
//                        textarea = temp[4];
//                        System.out.printf("%s", textarea);
//                        textarea = textarea.substring(1, textarea.length());
//                        System.out.printf("textarea: %s",textarea);
//                        //textArea.setText(textarea + "-"); // update textArea
//                        System.out.printf("temp[%d] %s\n",position, temp[4]);
//                       }
//                } //else {// if double == double, keep decimal and number afterwards
//                    //textArea.setText(temp[2]);
//            } else if (textArea.getText().equals("")) {
//                textArea.setText("0");
//                temp[position] = "0";
//                temp[4] = temp[position];
//                //System.out.println("textArea: " + textArea.getText());
//                //System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//            } else if (!temp[position].equals("")) { // position-1 ???
//                temp[4] = temp[position];
//                //System.out.println("textArea: " + textArea.getText());
//                //System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//            } else {
//                temp[4] = temp[position];
//                //System.out.println("textArea: " + textArea.getText());
//                //System.out.printf("temp[%d]: %s\n", 0, temp[0]);
//            }
//
//            buttonMR.setEnabled(true);
//            buttonMC.setEnabled(true);
//
//
//
//            System.out.printf("temp[%d]: %s after memory+ pushed\n",4,temp[4]); // confirming proper textarea before moving on
//            //}
//            //else { // if number is double
//                //temp[0] = Double.parseDouble(textArea.getText());
//                //textArea.setText(event.getActionCommand() + " " + temp[0] ); // "userInput.textarea +"
//                //System.out.printf("temp[0] "+temp[0]);
//            //}
//            //addBool = true; // sets logic for arithmetic
//            //firstNumBool = false; // sets logic to perform operations when collecting second number
//            dotButtonPressed = false;
//            //position++; // increase position for storing textarea
//            textarea = textArea.getText();
//            memAddBool = true;
//            confirm();
//        }
//    }
//
//    // MemoryClearButtonHandler operates on this button:
//    // final private JButton buttonMC = new JButton("M-");
//    public class MemorySubButtonHandler implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("\nMemorySubButtonHandler started");
//            System.out.println("button: " + e.getActionCommand()); // print out button confirmation
//            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//            if(!textArea.getText().equalsIgnoreCase("") && !temp[4].equals("")) {
//            // if textArea has a number and memory storage has a number
//                System.out.println("textArea: " + textArea.getText()); // print out textArea has proper value confirmation; recall text area's orientation
//                System.out.printf("temp[%d]:(memory) %s\n", 4, temp[4]);
//                System.out.printf("temp[%d]:(sub from) %s\n", position, temp[position]);
//                double result = Double.parseDouble(temp[4]) - Double.parseDouble(temp[position]); // create result forced double
//                System.out.printf("\n%s - %s = %.0f\n", temp[4], temp[position], result);
//                temp[4] = Double.toString(result); // store result
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//                int intRep = (int)result;
//                if (intRep == result) { // if int == double, cut off decimal and zero
//                    textarea = temp[4];
//                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//                    temp[4] = textarea; // update storing
//                    System.out.printf("update storing: %s\n",textarea);
//                    if (Integer.parseInt(temp[4]) < 0 ) { // if result is negative
//                        textarea = temp[4];
//                        System.out.printf("%s\n", textarea);
//                        textarea = textarea.substring(1, textarea.length());
//                        System.out.printf("textarea: %s\n",textarea);
//                        System.out.printf("temp[%d] %s\n",position, temp[4]);
//                    }
//                }
//            } else if (textArea.getText().equals("")) {
//                textArea.setText("0");
//                temp[position] = "0";
//                temp[4] = temp[position];
//                System.out.println("textArea: " + textArea.getText());
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//
//                double result = 0.0;
//                System.out.println("result: " + result);
//                int intRep = (int)result; // 9 = 9.0
//                if (intRep == result) { // if int == double, cut off decimal and zero
//                    textArea.setText(Double.toString(result));
//                    textarea = textArea.getText();
//                    System.out.println("textArea: " + textArea.getText());
//                    textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//                    temp[4] = textarea; // update storing
//                    textArea.setText(temp[4]);
//                    if (Integer.parseInt(temp[4]) < 0 ) {
//                        textarea = textArea.getText(); // temp[2]
//                        System.out.printf("%s", textarea);
//                        textarea = textarea.substring(1, textarea.length());
//                        System.out.printf("textarea: %s",textarea);
//                        textArea.setText(textarea + "-"); // update textArea
//                        System.out.printf("temp[%d] %s\n",position, temp[position]);
//                    }
//                }
//            } else if (!temp[position].equals("")) {
//                temp[4] = convertToNegative(temp[position]);
//                System.out.println("textArea: " + textArea.getText());
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//            } else {
//                temp[4] = convertToNegative(temp[position]);
//                System.out.println("textArea: " + textArea.getText());
//                System.out.printf("temp[%d]: %s\n", 4, temp[4]);
//            }
//            buttonMR.setEnabled(true);
//            buttonMC.setEnabled(true);
//            System.out.printf("temp[%d]: %s after memory- pushed\n",4,temp[4]); // confirming proper textarea before moving on
//            dotButtonPressed = false;
//            textarea = "";
//            memSubBool = true;
//        }
//    }
//
//    // method to set constraints on
//    public void addComponent(Component c, int row, int column, int width, int height)
//    {
//        constraints.gridx = column;
//        constraints.gridy = row;
//        constraints.gridwidth = width;
//        constraints.gridheight = height;
//        standardLayout.setConstraints(c, constraints); // set constraints
//        add(c); // add component
//    }
//
//    // clear method
//    public void clear() {
//        // firstNum, secondNum, total, copy/paste, memory
//        for ( position=0; position < 4; position++) {
//            temp[position] = "";
//        }
//        textarea = "";
//        textArea.setText("");
//        addBool = false;
//        subBool = false;
//        mulBool = false;
//        divBool = false;
//        position = 0;
//        firstNumBool = true;
//        dotButtonPressed = false;
//        dotActive = false;
//        buttonDot.setEnabled(true);
//
//    }
//
//    public void addition() {
//        System.out.printf("temp[%d]: %s\n", 0, temp[0]);
//        System.out.printf("temp[%d]: %s\n", 1, temp[1]);
//        double result = Double.parseDouble(temp[0]) + Double.parseDouble(temp[1]); // create result forced double
//        System.out.printf("%s + %s = %.0f\n", temp[0], temp[1], result);
//        //temp[0] = formatNumber(Double.toString(result)); // store result
//        //temp[0] = clearZeroesAtEnd(temp[0]);
//        temp[0] = Double.toString(result);
//        System.out.printf("temp[%d]: %s\n", 0, temp[0]);
//        System.out.printf("addBool: "+addBool
//                        + "\nsubBool: "+subBool
//                        + "\nmulBool: "+mulBool
//                        + "\ndivBool: "+divBool);
//        //Integer intRep = (int) result;
//        //if (intRep == result) { // if int == double, cut off decimal and zero
//        if (result % 1 == 0) {
//            System.out.println("\nWe have a whole number");
//            //textArea.setText(temp[2]);
//            textarea = Double.toString(result);
//            temp[0] = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//            textArea.setText("\n" + temp[0]);
//            if (Integer.parseInt(temp[0]) < 0 ) {
//                textarea = textArea.getText(); // temp[position]
//                System.out.printf("%s\n", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("textarea: %s\n",textarea);
//                textArea.setText("\n "+textarea.replaceAll("-", "") + "-"); // update textArea
//                //System.out.printf("temp[%d] %s\n",position, formatNumber(temp[position]));
//            }
//        } else {// if double == double, keep decimal and number afterwards
//            System.out.println("\nWe have a decimal");
//            if (Double.parseDouble(temp[0]) < 0.0 ) {
//                temp[0] = formatNumber(temp[0]);
//                System.out.printf("%s\n", textarea);
//                textarea = temp[0];
//                System.out.printf("%s\n", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("textarea: %s\n",textarea);
//                textArea.setText("\n" + textarea + "-"); // update textArea
//                System.out.printf("temp[%d] %s\n",position, temp[position]);
//            } else {
//                textArea.setText("\n" + formatNumber(temp[0]));
//                temp[0] = formatNumber(temp[0]);
//            }
//        }
//    }
//
//    public void subtract() {
//        double result = Double.parseDouble(temp[0]) - Double.parseDouble(temp[1]); // create result forced double
//        System.out.printf("%s - %s = %.0f\n", temp[0], temp[1], result);
//        temp[0] = Double.toString(result); // store result
//        System.out.printf("temp[%d]: %s\n", 0, temp[0]);
//        System.out.printf("addBool: "+addBool
//                      + "\nsubBool: "+subBool
//                      + "\nmulBool: "+mulBool
//                      + "\ndivBool: "+divBool);
//        if (result % 1 == 0) {
//            //textArea.setText(temp[0]);
//            textarea = Double.toString(result);
//            textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//            temp[0] = textarea; // update storing
//            textArea.setText("\n" + temp[0]);
//            if (Integer.parseInt(temp[0]) < 0 ) {
//                //textarea = textArea.getText();
//                //System.out.printf("\n%s", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("\ntextarea: %s",textarea);
//                textArea.setText("\n" + textarea.replaceAll(" ", "") + "-".replaceAll(" ", "")); // update textArea
//                System.out.printf("\ntemp[%d] %s\n",0, temp[0]);
//            }
//        } else {// if double == double, keep decimal and number afterwards
//            if (Double.parseDouble(temp[0]) < 0.0 ) {
//                temp[0] = formatNumber(temp[0]);
//                textarea = temp[0];
//                System.out.printf("%s", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("textarea: %s",textarea);
//                textArea.setText("\n" + textarea + "-"); // update textArea
//                System.out.printf("temp[%d] %s\n",position, temp[position]);
//            } else {
//                textArea.setText("\n" + formatNumber(temp[0]));
//            }
//        }
//    }
//
//    public void multiply() {
//        double result = Double.parseDouble(temp[0]) * Double.parseDouble(temp[1]); // create result forced double
//        System.out.printf("%s * %s = %.0f\n", temp[0], temp[1], result);
//        temp[0] = Double.toString(result); // store result
//        System.out.printf("temp[%d]: %s\n", position, temp[0]);
//        System.out.printf("addBool: "+addBool
//                      + "\nsubBool: "+subBool
//                      + "\nmulBool: "+mulBool
//                      + "\ndivBool: "+divBool);
//        if (result % 1 == 0) {
//            textArea.setText(temp[0]);
//            textarea = textArea.getText().replaceAll("\\n", "");
//            textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//            temp[0] = textarea; // update storing
//            textArea.setText("\n" + temp[0]);
//            if (Integer.parseInt(temp[0]) < 0 ) {
//                textarea = textArea.getText().replaceAll("\\n", ""); // temp[2]
//                System.out.printf("%s", textarea);
//                textarea = textarea.substring(1, textarea.length());
//                System.out.printf("textarea: %s",textarea);
//                textArea.setText("\n" + textarea + "-"); // update textArea
//                System.out.printf("temp[%d] %s\n",position, temp[position]);
//            }
//        } else {// if double == double, keep decimal and number afterwards
//            textArea.setText("\n" + formatNumber(temp[0]));
//        }
//    }
//
//    public void divide() {
//        if (!temp[1].equals("0")) {
//            // if the second number is not zero, divide as usual
//            double result = Double.parseDouble(temp[0]) / Double.parseDouble(temp[1]); // create result forced double
//            System.out.printf("%s / %s = %.0f\n", temp[0], temp[1], result);
//            temp[0] = Double.toString(result); // store result
//            System.out.printf("temp[%d]: %s\n", 0, temp[0]);
//            System.out.printf("addBool: "+addBool
//                          + "\nsubBool: "+subBool
//                          + "\nmulBool: "+mulBool
//                          + "\ndivBool: "+divBool);
//            if (Double.parseDouble(temp[0]) % 1 == 0) {
//                // if int == double, cut off decimal and zero
//                textArea.setText("\n" + temp[0]);
//                textarea = textArea.getText().replaceAll("\\n", "");
//                textarea = textarea.substring(0, textarea.length()-2); // textarea changed to whole number, or int
//                temp[0] = textarea; // update storing
//                textArea.setText("\n" + temp[0]);
//                if (Integer.parseInt(temp[0]) < 0 ) {
//                    textarea = textArea.getText().replaceAll("\\n", ""); // temp[2]
//                    System.out.printf("%s", textarea);
//                    textarea = textarea.substring(1, textarea.length());
//                    System.out.printf("textarea: %s",textarea);
//                    textArea.setText("\n" + textarea + "-"); // update textArea
//                    System.out.printf("temp[%d] %s\n",position, temp[position]);
//                }
//            } else {
//                // if double == double, keep decimal and number afterwards
//                textArea.setText("\n" + formatNumber(temp[0]));
//            }
//        } else if (temp[1].equals("0")) {
//            String result = "0";
//            System.out.println("\nAttemping to divide by zero");
//            textArea.setText("Cannot divide by 0");
//            temp[0] = result;
//            firstNumBool = true;
//        }
//    }
//
//    public boolean resetOperator(boolean operatorBool) {
//        if (operatorBool == true) {
//            System.out.printf("\noperatorBool: %s", operatorBool);
//            //temp[0] = temp[2];
//            temp[1]= "";
//            //temp[2] = "";
//            System.out.printf("\ntemp[%d]: %s\n",0, temp[0]);
//            position = 1;
//            dotButtonPressed = false;
//            firstNumBool = false;
//            textarea = "";
//            return false;
//        } else {
//            System.out.printf("\noperatorBool: %s", operatorBool);
//            //temp[0] = temp[2];
//            temp[1]= "";
//            //temp[2] = "";
//            System.out.printf("\ntemp[%d]: %s\n",0, temp[0]);
//            position = 1;
//            dotButtonPressed = false;
//            firstNumBool = false;
//            textarea = "";
//            return true;
//        }
//    }
//
//    // used to keep 2 decimals on the number at all times
//    public String formatNumber(String num) {
//        DecimalFormat df = new DecimalFormat("0.00");
//        double number = Double.parseDouble(num);
//        num = df.format(number);
//        System.out.printf("\nFormatted: %s\n", num);
//        return num;
//
//    }
//
//    /**
//     * Returns temp[0] and temp[1].
//     * Returns text area and operator booleans
//     */
//
//    public void confirm() {
//        System.out.println("\nConfirm Results: ");
//        System.out.println("---------------- ");
//        for(int i=0; i<5; i++)
//            System.out.printf("temp[%d]: \'%s\'\n",i, temp[i]);
//        if (temp[0].contains(".")) dotActive = true;
//        System.out.printf("textarea: \'%s\'\n"
//                        + "textArea: \'%s\'\n"
//                        + "addBool: %s\n"
//                        + "subBool: %s\n"
//                        + "mulBool: %s\n"
//                        + "divBool: %s\n"
//                        + "position: %s\n"
//                        + "firstNumBool: %s\n"
//                        + "dotButtonPressed: %s\n"
//                        + "dotActive: %s\n",
//                textarea, textArea.getText(), addBool, subBool, mulBool,
//                divBool, position, firstNumBool, dotButtonPressed, dotActive);
//        System.out.println("\n---------------- ");
//    }
//
//    protected void setTemp(String textarea) {
//        temp[position] = textarea;
//    }
//
//    public void setFinishedText() {
//        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//        textArea.setText("Break my calculator");
//    }
//    //
//
//} //end class Calculator