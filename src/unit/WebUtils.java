//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unit;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.check.checkHelper;
import common.java.string.StringHelper;

public class WebUtils {
    public WebUtils() {
    }

    public static String getWbname_by_wbid(String wbid) {
        String result = null;
        if (StringHelper.InvaildString(wbid)) {
            if (checkHelper.checkChinese(wbid)) {
                result = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWbidByName/" + wbid);
            }

            if (!StringHelper.InvaildString(result)) {
                return rMsg.netMSG(1, "无效网站id");
            }
        }

        return result;
    }

    public static String getOgname_by_ogid(String ogid) {
        String result = null;
        if (StringHelper.InvaildString(ogid)) {
            result = (String)appsProxy.proxyCall("/GrapeContent/ContentGroup/getOgname_By_ogid/" + ogid);
            if (!StringHelper.InvaildString(result)) {
                return rMsg.netMSG(1, "无效网站id");
            }
        }

        return result;
    }

    public static String getOgid_by_wbid_ogname(String wbid, String ogname) {
        String result = ogname;
        if (StringHelper.InvaildString(ogname)) {
            if (checkHelper.checkChinese(ogname)) {
                result = (String)appsProxy.proxyCall("/GrapeContent/ContentGroup/getOgidByName/" + wbid + "/" + ogname);
            }

            if (!StringHelper.InvaildString(result)) {
                return rMsg.netMSG(1, "无效栏目id");
            }
        }

        return result;
    }

    public static void main(String[] args) {
    }
}
