package operias.test.general;

@SuppressWarnings("serial")
public class ExitException extends SecurityException 
{
    public final int status;
    public ExitException(int status) 
    {
        this.status = status;
    }
}
