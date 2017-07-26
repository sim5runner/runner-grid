package exceptions;

public class InvalidTaskActionException extends Exception{

	  private static final long serialVersionUID = 1L;

	//Parameterless Constructor
	  public InvalidTaskActionException() {}
	
	  //Constructor that accepts a message
	  public InvalidTaskActionException(String message)
	  {
	     super(message);
	     printStackTrace();
	  }
}
