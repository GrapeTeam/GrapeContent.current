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
import common.java.time.TimeHelper;
import org.json.simple.JSONObject;

public class ContentRecord {
    private GrapeTreeDBModel Record = new GrapeTreeDBModel();
    private GrapeDBSpecField gDbSpecField = new GrapeDBSpecField();
    private session se;
    private JSONObject userInfo = null;
    private String currentUser = null;

    public ContentRecord() {
        this.gDbSpecField.importDescription(appsProxy.tableConfig("ContentRecord"));
        this.Record.descriptionModel(this.gDbSpecField);
        this.Record.bindApp();
        this.se = new session();
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() != 0) {
            this.currentUser = this.userInfo.getMongoID("_id");
        }

    }

    public String AddReader(String oid) {
        Object tip = null;
        String result = rMsg.netMSG(100, "新增失败");
        JSONObject info = new JSONObject();
        info.put("uid", this.currentUser);
        info.put("oid", oid);
        info.put("time", TimeHelper.nowMillis());
        tip = this.Record.data(info).insertEx();
        return tip != null ? rMsg.netMSG(0, "新增成功") : result;
    }
}
