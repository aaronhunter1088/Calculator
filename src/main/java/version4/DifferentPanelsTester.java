package version4;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class DifferentPanelsTester extends JFrame
{

	public static void main(String[] args) throws Exception
	{
		DifferentPanelsTester x = new DifferentPanelsTester();
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);
		x.setSize(new Dimension(500, 500));
	}
	
	//private JButton btn1 = new JButton("Button 1");
	//private JButton btn2 = new JButton("Button 2");
	//private GridLayout gridLayout = new GridLayout(2,0);
	
	private StandardCalculator_v4 calculator_v4 = new StandardCalculator_v4();
	private static final long serialVersionUID = 1L;
	private JPanel basic = new JPanelBasic_v4(calculator_v4);
	private JPanel date = new JPanelDate_v4_withBorders(calculator_v4);
	private JPanel programmer = new JPanelProgrammer_v4(calculator_v4);
	private JPanel currentPanel;
	//private JTextField textField1 = new JTextField("Input text here");
	//private JTextField textField2 = new JTextField("Input text here");
	//private FlowLayout flowLayout = new FlowLayout();
	
	public DifferentPanelsTester() throws Exception
	{
		super("Different Panels");
		setupPanels();
		setupMenuBar();
	}
	
	/* This class shows that panels can be placed
	 * on the frame and can have their own layouts,
	 * and components different to each other.
	*/
	private void setupPanels()
	{
		add(programmer);
		setCurrentPanel(programmer);
	}
	
	private void setupMenuBar()
	{
		JMenuBar bar = new JMenuBar(); // create menu bar
        setJMenuBar(bar); // add menu bar to application
        
        // View Menu and Actions
        JMenu switchMenu = new JMenu("Switch"); // create switch menu
        switchMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12) );
        bar.add(switchMenu); // add viewMenu to menu bar
        
            JMenuItem panel1Item = new JMenuItem("Basic Calc");
            panel1Item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel1Item.addActionListener(action -> {
            	switchPanels(1);
            });
            
            JMenuItem panel2Item = new JMenuItem("Date Picker");
            panel2Item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel2Item.addActionListener(action -> {
            	switchPanels(2);
            });

			JMenuItem panel3Item = new JMenuItem("Programmer");
			panel2Item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			panel2Item.addActionListener(action -> {
				switchPanels(3);
			});
            
         switchMenu.add(panel1Item);
         switchMenu.add(panel2Item);
         switchMenu.add(panel3Item);
	}
	
	private void switchPanels(int choice)
	{
		if (choice == 1)
		{
			remove(getCurrentPanel());
			add(basic);
			setCurrentPanel(basic);
		} else if (choice == 2)
		{
			remove(getCurrentPanel());
			add(date);
			setCurrentPanel(date);
		}
		else if (choice == 3)
		{
			remove(getCurrentPanel());
			add(programmer);
			setCurrentPanel(programmer);
		}
		this.revalidate();
		this.repaint();
	}

	public void setCurrentPanel(JPanel currentPanel)
	{
		this.currentPanel = currentPanel;
	}

	public JPanel getCurrentPanel()
	{
		return this.currentPanel;
	}
}
 