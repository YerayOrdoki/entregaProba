package exceptions;
public class MustBeLaterThanTodayException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public MustBeLaterThanTodayException()
  {
    super();
  }
  /**This exception is triggered if the date of the product to sell is previous than today
  *@param s String of the exception
  */
  public MustBeLaterThanTodayException(String s)
  {
    super(s);
  }
}
