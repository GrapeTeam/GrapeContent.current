package unit;

import org.json.simple.JSONArray;

public class JSONArrayUtils
{
  public static JSONArray jsonarray_page(JSONArray ja, int idx, int pageSize)
  {
    int size = ja.size();
    JSONArray jsonArray = new JSONArray();
    int preidx = idx - 1;
    if (preidx < 0) {
      preidx = 0;
    }
    for (int i = 0; i < size; i++) {
      if ((i >= preidx * pageSize) && (i < idx * pageSize)) {
        jsonArray.adds(ja.get(i));
      }
    }
    return jsonArray;
  }
}
