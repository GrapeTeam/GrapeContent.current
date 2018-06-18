//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import Model.CommonModel;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.check.checkHelper;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.json.JSONHelper;
import common.java.security.codec;
import common.java.session.session;
import common.java.string.StringHelper;
import java.util.Iterator;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ContentCache {
    private GrapeTreeDBModel content = new GrapeTreeDBModel();
    private GrapeDBSpecField gDbSpecField = new GrapeDBSpecField();
    private CommonModel model = new CommonModel();
    private session se;
    private JSONObject userInfo = null;
    private String currentWeb = null;

    public ContentCache() {
        this.gDbSpecField.importDescription(appsProxy.tableConfig("ContentCache"));
        this.content.descriptionModel(this.gDbSpecField);
        this.content.bindApp();
        this.se = new session();
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() != 0) {
            this.currentWeb = this.userInfo.getString("currentWeb");
        }

    }

    public String pushArticle(String wbid, String ArticleInfo) {
        int code = 99;
        String result = rMsg.netMSG(100, "推送失败");
        String oid = "";
        String temp = "0";
        long state = 0L;
        ArticleInfo = codec.DecodeFastJSON(ArticleInfo);
        JSONObject object = JSONHelper.string2json(ArticleInfo);
        if (wbid != null && !wbid.equals("") && object != null && object.size() != 0) {
            String[] values = wbid.split(",");
            String[] var14 = values;
            int var13 = values.length;

            for(int var12 = 0; var12 < var13; ++var12) {
                String value = var14[var12];
                if (object.containsKey("ogid")) {
                    object.put("ogid", "");
                }

                if (object.containsKey("_id")) {
                    oid = object.getMongoID("_id");
                    object.remove("_id");
                }

                if (object.containsKey("wbid")) {
                    object.remove("wbid");
                }

                object.put("wbid", value);
                object = this.pushDencode(object);
                if (!this.CheckContentCache(oid, value)) {
                    if (object.containsKey("state")) {
                        temp = object.getString("state");
                        if (temp.contains("$numberLong")) {
                            temp = JSONObject.toJSON(temp).getString("$numberLong");
                        }

                        state = Long.parseLong(temp);
                    }

                    object.put("state", state);
                    code = this.content.data(object).autoComplete().insertOnce() != null ? 0 : 99;
                    result = code == 0 ? rMsg.netMSG(0, "推送成功") : result;
                }
            }
        }

        return result;
    }

    public String pushArticles(String columns, String ArticleInfo) {
        int code = 99;
        String result = rMsg.netMSG(100, "推送失败");
        String[] value = null;
        JSONObject columnInfo = JSONObject.toJSON(columns);
        JSONObject ObjContent = JSONObject.toJSON(ArticleInfo);
        if (columnInfo != null && columnInfo.size() != 0 && ObjContent != null && ObjContent.size() != 0) {
            JSONObject object = ObjContent;

            for(Iterator var12 = columnInfo.keySet().iterator(); var12.hasNext(); result = code == 0 ? rMsg.netMSG(0, "推送成功") : result) {
                Object key = var12.next();
                String wbid = key.toString();
                String ogids = columnInfo.getString(wbid);
                value = ogids.split(",");
                if (value != null && !value.equals("")) {
                    String[] var16 = value;
                    int var15 = value.length;

                    for(int var14 = 0; var14 < var15; ++var14) {
                        String string = var16[var14];
                        if (object.containsKey("ogid")) {
                            object.put("ogid", string);
                        }

                        if (object.containsKey("_id")) {
                            object.remove("_id");
                        }

                        if (object.containsKey("wbid")) {
                            object.put("wbid", wbid);
                        }

                        if (object.containsKey("content")) {
                            object = this.pushDencode(object);
                        }
                    }
                }

                code = this.content.data(object).autoComplete().insertOnce() != null ? 0 : 99;
            }
        }

        return result;
    }

    public String pushToColumn(String oid, String data) {
        Content c = new Content();
        String result = rMsg.netMSG(100, "推送失败");
        JSONObject object = this.content.eq("_id", oid).find();
        if (object != null && object.size() != 0) {
            object.remove("_id");
            object = this.remoNumberLong(object);
            String info = c.inserts(object);
            result = c.EditArticle(info, data);
            if (JSONObject.toJSON(result).getString("errorcode").equals("0")) {
                this.content.eq("_id", oid).delete();
            }
        }

        return result;
    }

    public String searchPushArticle(int idx, int pageSize, String condString) {
        JSONArray array = null;
        long total = 0L;
        if (StringHelper.InvaildString(this.currentWeb) && !this.currentWeb.equals("")) {
            this.content.eq("wbid", this.currentWeb);
            if (condString != null) {
                JSONArray condArray = JSONArray.toJSONArray(condString);
                if (condArray != null && condArray.size() != 0) {
                    this.content.where(condArray);
                }
            }

            array = this.content.dirty().page(idx, pageSize);
            total = this.content.count();
        }

        return rMsg.netPAGE(idx, pageSize, total, this.model.getImgs(this.model.getDefaultImage(this.currentWeb, array)));
    }

    public String DeletePushArticle(String oid) {
        int code = -1;
        String result = rMsg.netMSG(100, "删除失败");
        String[] value = null;
        if (StringHelper.InvaildString(oid)) {
            value = oid.split(",");
        }

        if (value != null) {
            this.content.or();
            String[] var8 = value;
            int var7 = value.length;

            for(int var6 = 0; var6 < var7; ++var6) {
                String id = var8[var6];
                if (StringHelper.InvaildString(id) && ObjectId.isValid(id) && checkHelper.isInt(id)) {
                    this.content.eq("_id", id);
                }
            }

            JSONArray cond = JSONArray.toJSONArray(this.content.condString());
            if (cond != null && cond.size() > 0) {
                code = this.content.deleteAll() > 0L ? 0 : 99;
            }

            result = code == 0 ? rMsg.netMSG(0, "删除成功") : result;
        }

        return result;
    }

    private JSONObject remoNumberLong(JSONObject object) {
        String[] param = new String[]{"attribute", "sort", "type", "isdelete", "isvisble", "state", "substate", "slevel", "readCount", "u", "r", "d", "time"};
        String temp;
        if (object.containsKey("fatherid")) {
            temp = object.getString("fatherid");
            if (temp.contains("$numberLong")) {
                temp = JSONObject.toJSON(temp).getString("$numberLong");
            }

            object.put("fatherid", temp);
        }

        if (object.containsKey("tempid")) {
            temp = object.getString("tempid");
            if (temp.contains("$numberLong")) {
                temp = JSONObject.toJSON(temp).getString("$numberLong");
            }

            object.put("tempid", temp);
        }

        if (param != null && param.length > 0) {
            String[] var7 = param;
            int var6 = param.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                String value = var7[var5];
                if (object.containsKey(value)) {
                    temp = object.getString(value);
                    if (temp.contains("$numberLong")) {
                        temp = JSONObject.toJSON(temp).getString("$numberLong");
                    }

                    object.put(value, Long.parseLong(temp));
                }
            }
        }

        return object;
    }

    private JSONObject pushDencode(JSONObject obj) {
        String value = obj.get("content").toString();
        value = codec.decodebase64(value);
        String image;
        if (obj.containsKey("image")) {
            image = obj.getString("image");
            if (!image.equals("") && image != null) {
                image = codec.DecodeHtmlTag(image);
                obj.put("image", this.model.RemoveUrlPrefix(image));
            }
        }

        if (obj.containsKey("thumbnail")) {
            image = obj.getString("thumbnail");
            if (!image.equals("") && image != null) {
                image = codec.DecodeHtmlTag(image);
                obj.put("thumbnail", this.model.RemoveUrlPrefix(image));
            }
        }

        return obj;
    }

    private boolean CheckContentCache(String oid, String wbid) {
        JSONObject object = this.content.eq("_id", oid).eq("wbid", wbid).find();
        return object != null && object.size() != 0;
    }
}
