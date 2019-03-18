package version3;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class Test  {

	public static final Logger LOGGER = LogManager.getLogger(Test.class);
	public static void main(String[] args) {
		LOGGER.info("Start of program.");
		Test test = new Test();
		LOGGER.info("End program.");
	}
	
	Test() {
		LOGGER.info("Creating a Test() object");
		BasicCalcBottomLevel calc = new BasicCalcBottomLevel();
		CalculatorTopLevel otherCalc = calc;
		LOGGER.info("CalculatorTopLevel otherCalc created from BasicCalculatorBottomLevel.");
		CalculatorTopLevel thirdCalc = new CalculatorTopLevel();
	}

    class CalculatorTopLevel extends JFrame {
	
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CalculatorTopLevel() {
	    	setTitle("TopLevel");
		    setVisible(true);
		    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    }
    }
    
    class StandardCalcMiddleLevel extends CalculatorTopLevel {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StandardCalcMiddleLevel() {
    		//setTitle("MiddleLevel");
    	}
    }
    
    class BasicCalcBottomLevel extends StandardCalcMiddleLevel {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BasicCalcBottomLevel() {
			LOGGER.info("Creating a BasicCalculatorBottomLevel() object.");
    		setTitle("Bottom Level");
    		LOGGER.info("BasicCalculatorBottomLevel created.");
    	}
    }
}