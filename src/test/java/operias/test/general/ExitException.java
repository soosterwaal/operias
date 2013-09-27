package operias.test.general;

@SuppressWarnings("serial")
public class ExitException extends SecurityException 
{
    public final int status;
    public ExitException(int status) 
    {
        super("There is no escape!");
        this.status = status;
    }
}
