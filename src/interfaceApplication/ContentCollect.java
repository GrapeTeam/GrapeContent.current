//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.session.session;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import org.json.simple.JSONObject;

public class ContentCollect {
    private GrapeTreeDBModel _content = new GrapeTreeDBModel();
    private GrapeDBSpecField _gDbSpecField = new GrapeDBSpecField();
    private session se;
    private JSONObject userInfo = null;
    private String currentUserID = null;

    public ContentCollect() {
        this._gDbSpecField.importDescription(appsProxy.tableConfig("ContentCollect"));
        this._content.descriptionModel(this._gDbSpecField);
        this._content.bindApp();
        this.se = new session();
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() > 0) {
            this.currentUserID = this.userInfo.getString("_id");
        }

    }

    public String Collect(String oid) {
        Object info = null;
        String result = rMsg.netMSG(100, "收藏失败");
        JSONObject obj = new JSONObject();
        if (!StringHelper.InvaildString(this.currentUserID)) {
            return rMsg.netMSG(1, "请先登录再进行收藏");
        } else if (!StringHelper.InvaildString(oid)) {
            return rMsg.netMSG(1, "无效文章id");
        } else {
            obj.put("userid", this.currentUserID);
            obj.put("oid", oid);
            obj.put("time", TimeHelper.nowMillis());
            if (obj != null && obj.size() > 0) {
                info = this._content.data(obj).autoComplete().insertOnce();
            }

            return info != null ? rMsg.netMSG(0, "收藏成功") : result;
        }
    }

    public String CancelCollect(String oid) {
        int code = -1;
        String result = rMsg.netMSG(100, "取消失败");
        if (!StringHelper.InvaildString(this.currentUserID)) {
            return rMsg.netMSG(1, "请先登录");
        } else if (!StringHelper.InvaildString(oid)) {
            return rMsg.netMSG(1, "无效文章id");
        } else {
            this._content.eq("oid", oid).eq("userid", this.currentUserID);
            if (this._content.getCondCount() > 0) {
                code = this._content.delete() != null ? 0 : 100;
            }

            return code == 0 ? rMsg.netMSG(0, "取消收藏") : result;
        }
    }
}
