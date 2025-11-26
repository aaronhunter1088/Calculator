package Parent;

import Calculators.Calculator;
import Types.SystemDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

@ExtendWith(MockitoExtension.class)
public class TestParent
{
    public Calculator calculator;

    public static AutoCloseable mocks;

    public void postConstructCalculator()
    {
        calculator.pack();
        calculator.setVisible(true);
    }
}
