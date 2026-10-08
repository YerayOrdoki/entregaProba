package exceptions;
public class FileNotUploadedException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public FileNotUploadedException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public FileNotUploadedException(String s)
  {
    super(s);
  }
}
