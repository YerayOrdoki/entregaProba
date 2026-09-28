package exceptions;
public class SaleAlreadyExistException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public SaleAlreadyExistException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public SaleAlreadyExistException(String s)
  {
    super(s);
  }
}