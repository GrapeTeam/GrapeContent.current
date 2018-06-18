//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appIns;
import common.java.apps.appsProxy;
import common.java.nlogger.nlogger;
import common.java.security.codec;
import common.java.session.session;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PushContentToGov {
    private session se = new session();
    private JSONObject userInfo = null;
    private String currentWeb = null;

    public PushContentToGov() {
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() != 0) {
            this.currentWeb = this.userInfo.getString("currentWeb");
        }

        this.currentWeb = "59301f571a4769cbf5b0a0dd";
    }

    public String getColumnID() {
        String cCode = "";
        if (StringHelper.InvaildString(this.currentWeb)) {
            JSONObject tempobj = JSONObject.toJSON((String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getGovInfoByID/" + this.currentWeb));
            if (tempobj != null && tempobj.size() > 0 && tempobj.containsKey("GovCode")) {
                cCode = tempobj.getString("GovCode");
            }
        }

        if (!StringHelper.InvaildString(cCode)) {
            return rMsg.netMSG(1, "单位组织代码错误");
        } else {
            String temp = (String)appsProxy.proxyCall("/tlsGMWeb/wsServer/getChannels/" + cCode);
            return rMsg.netMSG(0, temp);
        }
    }

    public String pushToGov(JSONObject object, String cols) {
        int code = 0;
        String oid = "";
        String cCode = "";
        String CreateUsername = "";
        JSONObject tempobj = null;
        if (object != null && object.size() > 0) {
            oid = object.getString("subID");
            if (StringHelper.InvaildString(this.currentWeb)) {
                tempobj = JSONObject.toJSON((String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getGovInfoByID/" + this.currentWeb));
                if (tempobj != null && tempobj.size() > 0) {
                    if (tempobj.containsKey("GovUserName")) {
                        CreateUsername = tempobj.getString("GovUserName");
                    }

                    if (tempobj.containsKey("GovCode")) {
                        cCode = tempobj.getString("GovCode");
                    }
                }
            }

            if (!StringHelper.InvaildString(CreateUsername) || !StringHelper.InvaildString(cCode)) {
                return rMsg.netMSG(3, "获取用户名或者单位代码错误");
            }

            JSONObject filejson = this.getFilejson(object);
            String temp = this.getNewArticle(object, CreateUsername);
            if (StringHelper.InvaildString(temp)) {
                if (temp.contains("errorcode")) {
                    return temp;
                }

                JSONObject postParam = new JSONObject("param", codec.encodeFastJSON(temp));
                appIns apps = appsProxy.getCurrentAppInfo();
                temp = (String)appsProxy.proxyCall("/tlsGMWeb/wsServer/pushConent/" + cCode + "/" + cols + "/" + oid + "/" + filejson + "/b:" + true, postParam, apps);
                code = StringHelper.InvaildString(temp) ? Integer.parseInt(temp) : 0;
            }
        }

        return code != 0 ? rMsg.netMSG(0, "同步文章到市政府信息公开网成功") : rMsg.netMSG(0, "同步文章到市政府信息公开网失败");
    }

    private String getNewArticle(JSONObject object, String userName) {
        String title = "";
        String content = "";
        String author = "";
        long time = TimeHelper.nowMillis();
        JSONObject newArticle = new JSONObject();
        if (object != null && object.size() > 0) {
            if (object.containsKey("mainName")) {
                title = object.getString("mainName");
            }

            if (object.containsKey("time")) {
                time = object.getLong("time");
            }

            if (object.containsKey("content")) {
                content = object.getString("content");
            }

            if (object.containsKey("author")) {
                author = object.getString("author");
            }

            if (!StringHelper.InvaildString(title)) {
                return rMsg.netMSG(1, "文章标题不可为空");
            }

            if (!StringHelper.InvaildString(content)) {
                return rMsg.netMSG(2, "文章内容不可为空");
            }

            newArticle.put("title", title);
            newArticle.put("content", content);
            newArticle.put("author", author);
            newArticle.put("CreateUsername", userName);
            newArticle.put("keywords", "");
            newArticle.put("createdate", this.getTime(time));
        }

        return newArticle.toString();
    }

    private JSONObject getFilejson(JSONObject object) {
        String attr = "";
        String filename = "";
        String filepath = "";
        JSONArray attrArray = null;
        JSONObject filejson = null;
        if (object != null && object.size() > 0 && object.containsKey("attrid")) {
            attr = object.getString("attrid");
            attrArray = JSONArray.toJSONArray(attr);
        }

        if (attrArray != null && attrArray.size() > 0) {
            filejson = new JSONObject();
            Iterator var9 = attrArray.iterator();

            while(var9.hasNext()) {
                Object object2 = var9.next();
                JSONObject tempobj = (JSONObject)object2;
                filename = tempobj.getString("fileoldname");
                filepath = tempobj.getString("filepath");
                filejson.put(filename, filepath);
            }
        }

        return filejson;
    }

    private String getTime(long time) {
        Date dates = null;
        String times = "";

        try {
            String date = TimeHelper.stampToDate(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dates = sdf.parse(date);
            times = sdf.format(dates);
        } catch (Exception var7) {
            nlogger.logout(var7);
        }

        return times;
    }
}
