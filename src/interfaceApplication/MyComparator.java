package interfaceApplication;

import java.util.Comparator;

import org.json.simple.JSONObject;

public class MyComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject o1, JSONObject o2) {
        String key1 = o1.getString("time");
        String key2 = o2.getString("time");
        long time1 = Long.parseLong(key1);
        long time2 = Long.parseLong(key2);

        if(time1 < time2){
            return 1;
        }else if(time1 > time2){
            return -1;
        }
        return 0;
    }
}