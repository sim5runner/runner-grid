package exceptions;

public class ActionFailedException extends Exception{
	private static final long serialVersionUID = 1L;

    public ActionFailedException() {}

    public ActionFailedException(String message)
    {
       super(message);
    }
}
