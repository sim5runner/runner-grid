package exceptions;

public class InvalidTaskXMLException extends Exception
{
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
      public InvalidTaskXMLException() {}

      //Constructor that accepts a message
      public InvalidTaskXMLException(String message)
      {
         super(message);
         printStackTrace();
      }
 }


