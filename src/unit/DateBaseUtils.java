package unit;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DateBaseUtils
{
  public String loop_getAllwbid(JSONObject jo)
  {
    String rString = "";
    if ((jo != null) && (jo.size() > 0))
    {
      String id = jo.getString("pkString????");
      rString = rString + "," + id;
      if (jo.containsKey("itemChildrenData"))
      {
        JSONArray ja = jo.getJsonArray("itemChildrenData");
        if ((ja != null) && (ja.size() > 0))
        {
          String loop_getAllwbid;
          for (Iterator var6 = ja.iterator(); var6.hasNext(); rString = rString + loop_getAllwbid)
          {
            Object object = var6.next();
            JSONObject jo1 = (JSONObject)object;
            loop_getAllwbid = loop_getAllwbid(jo1);
          }
        }
      }
    }
    return rString;
  }
}
