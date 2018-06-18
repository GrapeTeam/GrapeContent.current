//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import org.json.simple.JSONObject;

public class ContentError {
    private GrapeTreeDBModel content = new GrapeTreeDBModel();
    private GrapeDBSpecField _gDbSpecField = new GrapeDBSpecField();

    public ContentError() {
        this._gDbSpecField.importDescription(appsProxy.tableConfig("contentError"));
        this.content.descriptionModel(this._gDbSpecField);
        this.content.bindApp();
    }

    public String insert(String contents, String oid) {
        String info = null;
        JSONObject object = new JSONObject();
        object.put("oid", oid);
        object.put("errorContent", contents);
        object.put("createtime", TimeHelper.nowMillis());
        String _id = this.find(oid);
        if (!StringHelper.InvaildString(_id)) {
            info = (String)this.content.data(object).autoComplete().insertOnce();
        } else {
            this.update(_id, object);
            info = _id;
        }

        return info;
    }

    private String find(String oid) {
        String _id = null;
        JSONObject object = this.content.eq("oid", oid).field("_id").find();
        if (object != null && object.size() > 0) {
            _id = object.getString("_id");
        }

        return _id;
    }

    public String update(String cid, JSONObject obj) {
        String result = rMsg.netMSG(1, "添加文章id失败");
        if (StringHelper.InvaildString(cid)) {
            obj = this.content.eq("_id", cid).data(obj).update();
        }

        return obj != null ? rMsg.netMSG(0, "") : result;
    }

    public String delete(String id) {
        JSONObject object = null;
        String result = rMsg.netMSG(1, "删除失败");
        String _id = this.find(id);
        if (!StringHelper.InvaildString(_id)) {
            object = this.content.eq("_id", _id).delete();
        }

        return object != null ? rMsg.netMSG(0, "删除成功") : result;
    }

    public String get(String oid) {
        JSONObject object = this.content.eq("oid", oid).find();
        return rMsg.netMSG(true, object);
    }
}
