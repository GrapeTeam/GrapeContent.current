//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import common.java.JGrapeSystem.rMsg;
import org.json.simple.JSONArray;

public class ContentScan {
    public ContentScan() {
    }

    public String getPrivacyInfo(int idx, int pageSize) {
        JSONArray array = null;
        long total = 0L;
        return rMsg.netPAGE(idx, pageSize, total, (JSONArray)array);
    }

    public String getPrivacyDetailedInfo(String PContentID) {
        return null;
    }
}
