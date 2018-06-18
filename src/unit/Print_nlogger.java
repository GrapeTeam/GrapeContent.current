package unit;

import common.java.security.codec;
import java.io.PrintStream;

public class Print_nlogger
{
  private static int logout = 1;
  
  public static void Print_SYSO(Object in)
  {
    if (logout == 1) {
      System.out.println(in);
    }
  }
  
  public static void main(String[] args)
  {
    String md5 = codec.md5("wzt1314520");
    System.out.println(md5);
  }
}
