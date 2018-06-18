package unit;

import common.java.apps.appIns;
import common.java.apps.appsProxy;
import java.io.PrintStream;
import java.util.HashMap;

public class LogsUtils
{
  private static int logout = 1;
  
  public static String addLogs(String uid, String uname, String action, String wbid, String FunctionName)
  {
    if (logout == 0) {
      return null;
    }
    appIns apps = appsProxy.getCurrentAppInfo();
    String temp = (String)appsProxy.proxyCall("/GrapeLog/Logs/AddLogs/s:" + uid + "/s:" + uname + "/s:" + action + "/s:" + wbid + "/s:" + FunctionName, apps);
    return temp;
  }
  
  public static void main(String[] args)
  {
    HashMap<String, Integer> hashMap = new HashMap();
    Integer put = (Integer)hashMap.put("a", Integer.valueOf(1));
    Integer put1 = (Integer)hashMap.put("a", Integer.valueOf(2));
    System.out.println();
  }
}
