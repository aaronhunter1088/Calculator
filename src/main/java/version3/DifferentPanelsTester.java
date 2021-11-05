package version3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class DifferentPanelsTester extends JFrame
{

	public static void main(String[] args) throws ParseException
	{
		DifferentPanelsTester x = new DifferentPanelsTester();
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);
		x.setSize(new Dimension(500, 500));
	}
	
	//private JButton btn1 = new JButton("Button 1");
	//private JButton btn2 = new JButton("Button 2");
	//private GridLayout gridLayout = new GridLayout(2,0);
	

	private static final long serialVersionUID = 1L;
	private JPanel basic = new JPanelBasic_v3(new StandardCalculator_v3());
	private JPanel date = new JPanelDate_v3_withBorders(new StandardCalculator_v3());
	private JPanel programmer = new JPanelProgrammer_v3(new StandardCalculator_v3());
	private JPanel currentPanel;
	//private JTextField textField1 = new JTextField("Input text here");
	//private JTextField textField2 = new JTextField("Input text here");
	//private FlowLayout flowLayout = new FlowLayout();
	
	public DifferentPanelsTester() throws ParseException
	{
		super("Different Panels");
		setupPanels();
		setupMenuBar();
		setCurrentPanel(basic);
	}
	
	/* This class shows that panels can be placed
	 * on the frame and can have their own layouts,
	 * and components different to each other.
	*/
	private void setupPanels()
	{
		add(programmer);
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
 