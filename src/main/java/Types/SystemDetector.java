package Types;

import Interfaces.OSDetector;
import org.apache.commons.lang3.SystemUtils;

public class SystemDetector implements OSDetector
{
    @Override
    public boolean isMac()
    {
        return SystemUtils.IS_OS_MAC;
    }

    @Override
    public boolean isWindows()
    {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Override
    public boolean isLinux()
    {
        return SystemUtils.IS_OS_LINUX;
    }
}
