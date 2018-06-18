//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import Model.CommonModel;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appIns;
import common.java.apps.appsProxy;
import common.java.cache.CacheHelper;
import common.java.check.checkHelper;
import common.java.database.dbFilter;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.nlogger.nlogger;
import common.java.security.codec;
import common.java.session.session;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ContentGroup {
    private GrapeTreeDBModel group = new GrapeTreeDBModel();
    private GrapeDBSpecField gDbSpecField = new GrapeDBSpecField();
    private CommonModel model = new CommonModel();
    private session se;
    private JSONObject userInfo = null;
    private String currentWeb = null;
    private String pkString = null;
    private CacheHelper cacheHelper;

    public GrapeTreeDBModel getGroup() {
        return this.group;
    }

    public ContentGroup() {
        this.gDbSpecField.importDescription(appsProxy.tableConfig("ContentGroup"));
        this.group.descriptionModel(this.gDbSpecField);
        this.group.bind();
        this.pkString = this.group.getPk();
        this.cacheHelper = new CacheHelper();
        this.se = new session();
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() != 0) {
            this.currentWeb = this.userInfo.getString("currentWeb");
        }

    }

    public String getTemplateContect_by_ogid(String id, String $scope_mode, String acticle) {
        String templateContect_by_ogid = this.getTemplateContect_by_ogid(id, $scope_mode, acticle, (String)null);
        return templateContect_by_ogid;
    }

    public String getTemplateContect_by_ogid(String id, String $scope_mode, String acticle, String redis_key) {
        acticle = codec.DecodeFastJSON(acticle);
        JSONObject json_return = new JSONObject();
        String key = rMsg.netMSG(1, "栏目id不对");
        JSONObject find = this.group.eq(this.pkString, id).find();
        JSONObject redisObject = new JSONObject();
        appIns apps = appsProxy.getCurrentAppInfo();
        JSONObject acticle1 = JSONObject.toJSON(acticle);
        if (find != null && find.size() > 0) {
            String tempYulan = find.getString("tempYulan");
            String wbid;
            if (StringHelper.InvaildString(tempYulan)) {
                wbid = (String)appsProxy.proxyCall("/GrapeTemplate/TemplateContext/getTemplateContect_by_ogid/s:" + tempYulan, apps);
                JSONObject json = JSONObject.toJSON(wbid);
                if (json != null) {
                    redisObject = json.getJson("message");
                }
            }

            wbid = find.getString("wbid");
            String temp1 = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getHost_by_wbid/s:" + wbid, apps);
            JSONObject json1 = JSONObject.toJSON(temp1);
            String host = json1.getString("message");
            redisObject.puts("host", host);
            String ip;
            if ("new".equals($scope_mode)) {
                ip = this.cacheHelper.get(redis_key);
                if (ip != null) {
                    JSONObject json2 = JSONObject.toJSON(ip);
                    String content = json2.getJson("acticle").getString("content");
                    String content_New = acticle1.getString("content");
                    content = content + content_New;
                    acticle1.puts("content", content);
                    this.cacheHelper.delete(redis_key);
                }

                redisObject.puts("acticle", acticle1);
            }

            if ("wait".equals($scope_mode)) {
                ip = acticle1.getString(this.pkString);
                String acticle2 = (new Content()).read_by_id(ip);
                JSONObject json = JSONObject.toJSON(acticle2);
                JSONObject json2 = json.getJson("message");
                redisObject.puts("acticle", json2);
            }

            ip = "";

            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException var20) {
                var20.printStackTrace();
            }

            key = "YuLanActicle_" + ip + "_" + UUID.randomUUID() + TimeHelper.nowMillis();
            this.cacheHelper.setget(key, redisObject, 1800L);
            json_return.puts("host", host);
            json_return.puts("key", key);
        }

        return rMsg.netMSG(0, json_return);
    }

    public String getValue_from_redis(String key) {
        String netMSG = rMsg.netMSG(1, "缓存里没有这个key");
        String string = this.cacheHelper.get(key);
        if (string != null) {
            netMSG = rMsg.netMSG(0, JSONObject.toJSON(string));
        }

        return netMSG;
    }

    public void publishActicle_than_update(String ogid) {
        JSONObject obj = this.group.eq(this.pkString, ogid).find();
        if (obj.size() > 0 && obj != null) {
            long curreditCount = obj.getLong("curreditCount");
            long editCount = obj.getLong("editCount");
            long lastTime = obj.getLong("lastTime");
            long timediff = obj.getLong("timediff");
            long nowMillis = TimeHelper.nowMillis();
            ++curreditCount;
            JSONObject jsonObject = new JSONObject();
            if (lastTime + timediff <= nowMillis && curreditCount >= editCount) {
                jsonObject.puts("curreditCount", 0);
                jsonObject.puts("lastTime", nowMillis);
            } else if (lastTime + timediff <= nowMillis && curreditCount < editCount) {
                jsonObject.puts("curreditCount", curreditCount);
            } else if (lastTime + timediff > nowMillis && curreditCount >= editCount) {
                jsonObject.puts("curreditCount", curreditCount);
            } else if (lastTime + timediff > nowMillis && curreditCount < editCount) {
                jsonObject.puts("curreditCount", curreditCount);
            }

            this.group.eq(this.pkString, ogid).data(jsonObject).update();
        }

    }

    public String SetLinkOgid(String currentOgid, String LinkOgid, int mixMode) {
        JSONObject rs = null;
        JSONObject obj = new JSONObject();
        obj.put("linkOgid", LinkOgid);
        obj.put("MixMode", mixMode);
        rs = this.group.eq(this.pkString, currentOgid).data(obj).update();
        return rs != null ? rMsg.netMSG(true, "设置关联栏目成功") : rMsg.netMSG(false, "设置关联栏目失败");
    }

    public String SetMixMode(String currentOgid, int flag) {
        JSONObject rs = null;
        JSONObject obj = new JSONObject("MixMode", flag);
        rs = this.group.eq(this.pkString, currentOgid).data(obj).update();
        return rs != null ? rMsg.netMSG(true, "设置栏目同步模式成功") : rMsg.netMSG(false, "设置栏目同步模式失败");
    }

    public String groupAdd(String GroupInfo) {
        String result = rMsg.netMSG(100, "新增栏目失败");
        JSONObject groupInfo = JSONObject.toJSON(GroupInfo);
        JSONObject object = null;
        if (groupInfo != null && groupInfo.size() != 0) {
            Object info = this.group.data(groupInfo).autoComplete().insertOnce();
            object = info != null ? this.group.eq("_id", info.toString()).find() : null;
            result = object != null && object.size() > 0 ? object.toJSONString() : result;
        }

        return result;
    }

    public String GroupInsert(String GroupInfo) {
        String result = rMsg.netMSG(100, "新增栏目失败");
        String contant = "0";
        JSONObject groupInfo = JSONObject.toJSON(GroupInfo);
        if (groupInfo != null && groupInfo.size() != 0 && groupInfo.containsKey("Contant")) {
            contant = groupInfo.getString("Contant");
        }

        switch(contant.hashCode()) {
        case 48:
            if (contant.equals("0")) {
                result = this.Add(groupInfo);
            }
            break;
        case 49:
            if (contant.equals("1")) {
                result = this.AddAllColumn(groupInfo);
            }
        }

        if (!result.contains("errorcode")) {
            JSONObject object = this.group.eq("_id", result).find();
            result = object.toString();
        }

        return result;
    }

    private String Add(JSONObject groupinfo) {
        JSONObject obj = this.checkColumn(groupinfo);
        if (obj != null && obj.size() > 0) {
            return rMsg.netMSG(1, "该栏目已存在");
        } else {
            JSONObject rMode = (new JSONObject("chkType", 0)).puts("chkCond", 100);
            JSONObject uMode = (new JSONObject("chkType", 0)).puts("chkCond", 200);
            JSONObject dMode = (new JSONObject("chkType", 0)).puts("chkCond", 300);
            groupinfo.put("rMode", rMode.toJSONString());
            groupinfo.put("uMode", uMode.toJSONString());
            groupinfo.put("dMode", dMode.toJSONString());
            Object info = this.group.data(groupinfo).autoComplete().insertOnce();
            return info != null ? info.toString() : null;
        }
    }

    private String AddAllColumn(JSONObject groupinfo) {
        String result = rMsg.netMSG(100, "新增栏目失败");
        if (groupinfo != null && groupinfo.size() != 0 && groupinfo.containsKey("wbid")) {
            String wbid = groupinfo.getString("wbid");
            if (!wbid.equals("")) {
                String[] value = this.model.getWeb(wbid);
                result = this.AddAll(groupinfo, value);
            }
        }

        return result;
    }

    private String AddAll(JSONObject groupinfo, String[] wbid) {
        String result = rMsg.netMSG(100, "新增栏目失败");
        if (groupinfo.containsKey("fatherid")) {
            String fatherid = groupinfo.getString("fatherid");
            if (!fatherid.equals("") && !fatherid.equals("0")) {
                result = this.AddColumn(groupinfo, fatherid, wbid);
            } else if (wbid != null) {
                String[] var8 = wbid;
                int var7 = wbid.length;

                for(int var6 = 0; var6 < var7; ++var6) {
                    String id = var8[var6];
                    groupinfo.put("wbid", id);
                    result = this.Add(groupinfo);
                }
            }
        }

        return result;
    }

    private String AddColumn(JSONObject groupinfo, String fatherid, String[] wbid) {
        String result = rMsg.netMSG(100, "新增栏目失败");
        String groupName = "";
        JSONObject temp = this.find(fatherid);
        if (temp != null && temp.size() != 0) {
            groupName = temp.getString("name");
            this.group.or();
            String[] var10 = wbid;
            int var9 = wbid.length;

            for(int var8 = 0; var8 < var9; ++var8) {
                String string = var10[var8];
                this.group.eq("wbid", string);
            }

            this.group.and().eq("name", groupName);
            JSONArray ColumnArray = this.group.select();
            JSONObject tempobj = this.getOgid(ColumnArray);
            if (tempobj != null && tempobj.size() != 0) {
                String[] var12 = wbid;
                int var11 = wbid.length;

                for(int var16 = 0; var16 < var11; ++var16) {
                    String string = var12[var16];
                    groupinfo.put("wbid", string);
                    groupinfo.put("fatherid", tempobj.getString(string));
                    result = this.Add(groupinfo);
                }
            }
        }

        return result;
    }

    private JSONObject getOgid(JSONArray ColumnArray) {
        JSONObject rObject = new JSONObject();
        if (ColumnArray != null && ColumnArray.size() != 0) {
            Iterator var7 = ColumnArray.iterator();

            while(var7.hasNext()) {
                Object obj = var7.next();
                JSONObject object = (JSONObject)obj;
                String wbid = object.getString("wbid");
                String ogid = object.getMongoID("_id");
                rObject.put(wbid, ogid);
            }
        }

        return rObject;
    }

    public String getOgid(String name) {
        String ogid = "";
        dbFilter filter = new dbFilter();
        String[] wbids = this.model.getWeb(this.currentWeb);
        String[] var10 = wbids;
        int var9 = wbids.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            String string = var10[var8];
            filter.eq("wbid", string);
        }

        JSONArray condArray = filter.build();
        if (condArray != null && condArray.size() > 0) {
            JSONArray array = this.group.or().where(condArray).and().like("name", name).field(this.pkString + ",name").select();
            String temp;
            if (array != null && array.size() > 0) {
                for(Iterator var11 = array.iterator(); var11.hasNext(); ogid = ogid + temp + ",") {
                    Object object2 = var11.next();
                    JSONObject obj = (JSONObject)object2;
                    temp = obj.getMongoID(this.pkString);
                }
            }

            return this.getLinkOgid(StringHelper.fixString(ogid, ','));
        } else {
            return rMsg.netMSG(2, "当前登录信息已失效，请重新登录");
        }
    }

    public JSONObject find(String ogid) {
        JSONObject object = this.group.eq(this.pkString, ogid).find();
        return object != null ? object : null;
    }

    public String DeleteColumnByWbid(String wbid) {
        long code = 0L;
        String result = rMsg.netMSG(100, "删除失败");
        if (StringHelper.InvaildString(wbid)) {
            code = this.group.eq("wbid", wbid).deleteAll();
        }

        return code > 0L ? rMsg.netMSG(0, "删除成功") : result;
    }

    private JSONObject checkColumn(JSONObject object) {
        JSONObject obj = null;
        String wbid = "";
        String type = "";
        String Fatherid = "";
        String name = "";
        if (object != null && object.size() > 0 && object.containsKey("wbid") && object.containsKey("type") && object.containsKey("fatherid")) {
            wbid = object.get("wbid").toString();
            type = object.get("type").toString();
            name = object.getString("name");
            Fatherid = object.get("fatherid").toString();
            obj = this.group.eq("name", name).eq("wbid", wbid).eq("type", type).eq("fatherid", Fatherid).find();
        }

        return obj;
    }

    public String GroupEdit(String ogid, String groupInfo) {
        int code = 99;
        String result = rMsg.netMSG(100, "栏目修改失败");
        String wbid = "";
        String contant = "0";
        String name = "";
        JSONObject groupinfo = JSONObject.toJSON(groupInfo);
        if (groupinfo != null && groupinfo.size() != 0) {
            if (groupInfo.contains("ColumnProperty")) {
                groupinfo.put("ColumnProperty", Long.parseLong(groupinfo.getString("ColumnProperty")));
            }

            if (groupinfo.containsKey("Contant")) {
                contant = groupinfo.getString("Contant");
                groupinfo.remove("Contant");
            }

            if (groupinfo.containsKey("name")) {
                name = groupinfo.getString("name");
            }

            if (name.equals("")) {
                contant = "0";
            }

            switch(contant.hashCode()) {
            case 48:
                if (contant.equals("0")) {
                    code = this.group.eq(this.pkString, ogid).data(groupinfo).update() != null ? 0 : 99;
                }
                break;
            case 49:
                if (contant.equals("1")) {
                    JSONObject object = this.find(ogid);
                    if (object != null && object.size() != 0) {
                        wbid = object.getString("wbid");
                        name = object.getString("name");
                    }

                    if (StringHelper.InvaildString(wbid)) {
                        code = this.updateCid(this.model.getRWbid(wbid), name, groupinfo);
                    }
                }
            }

            result = code == 0 ? rMsg.netMSG(0, "修改成功") : result;
        }

        return result;
    }

    private int updateCid(String wbid, String name, JSONObject groupinfo) {
        int i = 99;
        String temp = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/" + wbid);
        if (!temp.equals("")) {
            String[] value = temp.split(",");
            this.group.or();
            String[] var11 = value;
            int var10 = value.length;

            for(int var9 = 0; var9 < var10; ++var9) {
                String string = var11[var9];
                this.group.eq("wbid", string);
            }

            if (name.equals("")) {
                return 99;
            }

            JSONArray array = this.group.and().eq("name", name).select();
            JSONObject tempobj = this.getChildData(array, groupinfo);
            if (tempobj != null && tempobj.size() > 0) {
                String[] var13 = value;
                int var12 = value.length;

                for(int var17 = 0; var17 < var12; ++var17) {
                    String string = var13[var17];
                    if (tempobj.containsKey(string)) {
                        JSONObject info = JSONObject.toJSON(tempobj.getString(string));
                        if (info != null && info.size() > 0) {
                            i = this.group.eq("name", name).eq("wbid", string).data(info).update() != null ? 0 : 99;
                        }
                    }
                }
            }
        }

        return i;
    }

    private JSONObject getChildData(JSONArray array, JSONObject objects) {
        JSONObject object = new JSONObject();
        if (array != null && array.size() != 0) {
            Iterator var7 = array.iterator();

            while(var7.hasNext()) {
                Object obj = var7.next();
                JSONObject objtemp = (JSONObject)obj;
                String wbid = objtemp.getString("wbid");
                objtemp.remove(this.pkString);
                objtemp.put("name", objects.getString("name"));
                objtemp.put("contentType", objects.getString("contentType"));
                objtemp.put("tempList", objects.getString("tempContent"));
                objtemp.put("tempContent", objects.getString("tempContent"));
                objtemp.put("isreview", objects.getString("isreview"));
                objtemp.put("slevel", objects.getString("slevel"));
                objtemp.put("sort", objects.getString("sort"));
                objtemp.put("editCount", objects.getString("editCount"));
                objtemp.put("timediff", Long.parseLong(objects.getString("timediff")));
                object.put(wbid, objtemp);
            }
        }

        return object;
    }

    public String GroupDelete(String ogid) {
        String result = rMsg.netMSG(100, "删除失败");
        (new Content()).deleteByOgid(ogid);
        JSONArray condArray = this.model.getOrCond(this.pkString, ogid);
        if (condArray != null && condArray.size() > 0) {
            long code = this.group.or().where(condArray).deleteAll();
            result = code > 0L ? rMsg.netMSG(0, "删除成功") : result;
        }

        return result;
    }

    public String FindByType(String type, int no) {
        JSONArray array = null;
        if (this.currentWeb != null && !this.currentWeb.equals("")) {
            this.group.eq("wbid", this.currentWeb);
            array = this.group.eq("contentType", type).limit(no).select();
        }

        return rMsg.netMSG(true, array != null && array.size() > 0 ? array : new JSONArray());
    }

    public String FindByTypePage(int idx, int pageSize, String type) {
        long total = 0L;
        JSONArray array = null;
        if (this.currentWeb != null && !this.currentWeb.equals("")) {
            this.group.eq("wbid", this.currentWeb);
            this.group.eq("contentType", type);
            array = this.group.dirty().page(idx, pageSize);
            total = this.group.count();
        }

        return rMsg.netPAGE(idx, pageSize, total, array != null && array.size() > 0 ? array : new JSONArray());
    }

    public String GroupFind(String groupinfo) {
        JSONArray array = null;
        JSONArray condArray = this.model.buildCond(groupinfo);
        condArray = condArray != null && condArray.size() > 0 ? condArray : JSONArray.toJSONArray(groupinfo);
        if (condArray != null && condArray.size() > 0) {
            array = this.group.where(condArray).select();
        }

        return rMsg.netMSG(true, array != null && array.size() > 0 ? array : new JSONArray());
    }

    public String GroupPage(String wbid, int idx, int pageSize) {
        long total = 0L;
        JSONArray array = null;
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else if (!StringHelper.InvaildString(wbid)) {
            return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
        } else {
            wbid = this.model.getRWbid(wbid);
            this.group.eq("wbid", wbid).eq("isvisble", 0);
            total = this.group.dirty().count();
            array = this.group.desc("sort").asc(this.pkString).page(idx, pageSize);
            return rMsg.netPAGE(idx, pageSize, total, this.join(array));
        }
    }

    private JSONArray CheckGroup(JSONArray array) {
        String fatherid = "0";
        List<String> fidList = new ArrayList();
        JSONArray rsArray = new JSONArray();
        int cutNo = 0;

        try {
            if (array != null && array.size() > 0) {
                Iterator var9 = array.iterator();

                while(var9.hasNext()) {
                    Object obj = var9.next();
                    JSONObject object = (JSONObject)obj;
                    object.getString(this.pkString);
                    if (object.containsKey("fatherid")) {
                        fatherid = object.getString("fatherid");
                    }

                    if (!fidList.contains(fatherid) && !fatherid.equals("0")) {
                        fidList.add(fatherid);
                    }
                }

                Content _cContent = new Content();
                Iterator var10 = array.iterator();

                label42:
                while(true) {
                    while(true) {
                        if (!var10.hasNext()) {
                            break label42;
                        }

                        Object obj = var10.next();
                        String ogid = ((JSONObject)obj).getString(this.pkString);
                        if (!fidList.contains(ogid) && _cContent.getContentCount(ogid) <= 0L) {
                            ++cutNo;
                        } else {
                            rsArray.add(obj);
                        }
                    }
                }
            }
        } catch (Exception var11) {
            cutNo = 0;
        }

        if (cutNo > 0) {
            rsArray = this.CheckGroup(rsArray);
        }

        return rsArray;
    }

    public String GroupPageBy(String wbid, int idx, int pageSize, String GroupInfo) {
        return this.page(wbid, idx, pageSize, GroupInfo);
    }

    public String GroupPageBack(int idx, int pageSize) {
        return this.page(this.currentWeb, idx, pageSize, (String)null);
    }

    public String GroupPageByBack(int idx, int pageSize, String GroupInfo) {
        return this.page(this.currentWeb, idx, pageSize, GroupInfo);
    }

    private String page(String wbid, int idx, int pageSize, String GroupInfo) {
        long total = 0L;
        JSONArray array = null;
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else {
            wbid = this.model.getRWbid(wbid);
            if (!StringHelper.InvaildString(wbid)) {
                return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
            } else {
                if (StringHelper.InvaildString(GroupInfo)) {
                    JSONArray condArray = this.model.buildCond(GroupInfo);
                    condArray = condArray != null && condArray.size() > 0 ? condArray : JSONArray.toJSONArray(GroupInfo);
                    if (condArray == null || condArray.size() <= 0) {
                        return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
                    }

                    this.group.where(condArray);
                }

                this.group.eq("wbid", wbid).eq("isvisble", 0);
                total = this.group.dirty().count();
                array = this.group.desc("sort").asc(this.pkString).page(idx, pageSize);
                return rMsg.netPAGE(idx, pageSize, total, this.join(array));
            }
        }
    }

    public String update_sort_by_id(String oid, long sort) {
        String netMSG = rMsg.netMSG(1, "栏目id不对");
        if (StringHelper.InvaildString(oid)) {
            JSONObject update = this.group.eq(this.pkString, oid).data((new JSONObject()).puts("sort", sort)).update();
            if (update != null) {
                netMSG = rMsg.netMSG(0, "修改栏目顺序成功");
            }
        }

        return netMSG;
    }

    public String updateBatch_sorts_by_ids(String oids, int d) {
        String netMSG = rMsg.netMSG(1, "栏目id不对");
        if (StringHelper.InvaildString(oids)) {
            String[] split = oids.split(",");
            String[] var8 = split;
            int var7 = split.length;

            for(int var6 = 0; var6 < var7; ++var6) {
                String oid = var8[var6];
                JSONObject update = this.group.eq(this.pkString, oid).data((new JSONObject()).puts("sort", d)).update();
                d += d;
                if (update == null) {
                    netMSG = rMsg.netMSG(2, oid + "错误");
                }
            }

            netMSG = rMsg.netMSG(0, "修改栏目顺序成功");
        }

        return netMSG;
    }

    public String update_sort_fatherid_by_id(String oid, long sort, String fatherid) {
        String netMSG = rMsg.netMSG(1, "栏目id不对");
        if (StringHelper.InvaildString(oid)) {
            JSONObject update = this.group.eq(this.pkString, oid).data((new JSONObject()).puts("sort", sort).puts("fatherid", fatherid)).update();
            if (update != null) {
                netMSG = rMsg.netMSG(0, "修改栏目顺序成功");
            }
        }

        return netMSG;
    }

    public long getMixMode(String ogid) {
        long MixMode = 0L;
        JSONObject object = null;
        if (StringHelper.InvaildString(ogid)) {
            object = this.group.eq("_id", ogid).field("MixMode").find();
            if (object != null && object.size() > 0) {
                MixMode = object.getLong("MixMode");
            }
        }

        return MixMode;
    }

    public String getLinkOgid(String ogids) {
        String id = "0";
        String rsOgid = "";
        long MixMode = 0L;
        JSONArray array = null;
        if (StringHelper.InvaildString(ogids)) {
            JSONArray condArray = this.model.getOrCond(this.pkString, ogids);
            this.group.or().where(condArray);
            if (this.group.getCondCount() > 0) {
                array = this.group.field(this.pkString + ",linkOgid,MixMode").select();
                if (array != null && array.size() > 0) {
                    Iterator var10 = array.iterator();

                    while(var10.hasNext()) {
                        Object object = var10.next();
                        JSONObject obj = (JSONObject)object;
                        if (obj.containsKey("MixMode")) {
                            MixMode = obj.getLong("MixMode");
                        }

                        if (obj.containsKey("linkOgid")) {
                            id = obj.getString("linkOgid");
                            id = MixMode == 1L ? id + "," + obj.getString(this.pkString) : id;
                        }

                        if (!StringHelper.InvaildString(id) || id.equals("0")) {
                            id = obj.getString("_id");
                        }

                        if (!rsOgid.contains(id)) {
                            rsOgid = rsOgid + id + ",";
                        }
                    }
                }
            }
        }

        return StringHelper.fixString(rsOgid, ',');
    }

    public String getPrevCol(String ogid) {
        List<JSONObject> list = new ArrayList();
        JSONArray rList = new JSONArray();
        JSONObject temp = new JSONObject();
        String tempID = ogid;
        if (StringHelper.InvaildString(ogid)) {
            label49:
            while(true) {
                do {
                    do {
                        do {
                            if (temp == null) {
                                break label49;
                            }

                            temp = null;
                        } while(!StringHelper.InvaildString(tempID));
                    } while(tempID.equals("0"));
                } while(!ObjectId.isValid(tempID) && !checkHelper.isInt(tempID));

                temp = this.group.eq(this.pkString, tempID).field(this.pkString + ",wbid,name,fatherid").find();
                if (temp != null && temp.size() > 0) {
                    list.add(temp);
                    if (temp.containsKey("fatherid")) {
                        tempID = temp.getString("fatherid");
                        if (tempID.equals("0")) {
                            temp = null;
                        }
                    } else {
                        temp = null;
                    }
                }
            }
        }

        Collections.reverse(list);
        Iterator var7 = list.iterator();

        while(var7.hasNext()) {
            Object object = var7.next();
            JSONObject object2 = (JSONObject)object;
            rList.add(object2);
        }

        return rMsg.netMSG(true, rList);
    }

    public String getGroupById(String ogid) {
        JSONArray array = null;

        try {
            JSONArray condArray = this.model.getOrCond(this.pkString, ogid);
            if (condArray != null && condArray.size() > 0) {
                array = this.group.or().where(condArray).field(this.pkString + ",name,tempContent,tempList,ColumnProperty").select();
            }
        } catch (Exception var4) {
            nlogger.logout(var4);
            array = null;
        }

        return array != null && array.size() != 0 ? this.join(array).toJSONString() : null;
    }

    public JSONObject getDefaultById(String ogids) {
        dbFilter filter = new dbFilter();
        JSONObject obj = new JSONObject();
        JSONArray array = null;
        String[] value = ogids.split(",");
        String id;
        if (StringHelper.InvaildString(ogids)) {
            String[] var10 = value;
            int var9 = value.length;

            for(int var8 = 0; var8 < var9; ++var8) {
                id = var10[var8];
                if (StringHelper.InvaildString(id) && (ObjectId.isValid(id) || checkHelper.isInt(id))) {
                    filter.eq(this.pkString, id);
                }
            }

            this.group.or().where(filter.build());
        }

        if (this.group.getCondCount() > 0) {
            array = this.group.field(this.pkString + ",thumbnail,suffix").scan((jsArray) -> {
                return jsArray;
            }, 50);
        }

        if (array != null && array.size() > 0) {
            Iterator var12 = array.iterator();

            while(var12.hasNext()) {
                Object object = var12.next();
                JSONObject temp = (JSONObject)object;
                id = temp.getString(this.pkString);
                temp.remove("_id");
                obj.put(id, temp);
            }
        }

        return obj;
    }

    public String getColumnInfo(int idx, int pageSize, String wbid) {
        long total = 0L;
        JSONArray array = null;
        if (StringHelper.InvaildString(wbid)) {
            this.group.eq("wbid", wbid);
            array = this.group.dirty().page(idx, pageSize);
            total = (long)this.group.pageMax(pageSize);
            this.group.clear();
        }

        return rMsg.netPAGE(idx, pageSize, total, array != null && array.size() > 0 ? this.join(array) : new JSONArray());
    }

    public String getColumns(int idx, int pageSize, String wbid) {
        long total = 0L;
        JSONArray array = null;
        if (StringHelper.InvaildString(wbid) && this.IsWeb(wbid)) {
            this.group.eq("wbid", wbid);
            array = this.group.dirty().page(idx, pageSize);
            total = (long)this.group.pageMax(pageSize);
            this.group.clear();
        }

        return rMsg.netPAGE(idx, pageSize, total, array != null && array.size() > 0 ? this.join(array) : new JSONArray());
    }

    public String getColumnName(String ogid) {
        JSONArray array = null;
        JSONObject object = new JSONObject();
        String oid = "";
        String name = "";
        if (!StringHelper.InvaildString(ogid)) {
            return rMsg.netMSG(1, "无效栏目id");
        } else {
            JSONArray condArray = this.model.getOrCond(this.pkString, ogid);
            if (condArray != null && condArray.size() > 0) {
                array = this.group.or().where(condArray).field(this.pkString + ",name").select();
            }

            if (array != null && array.size() > 0) {
                Iterator var9 = array.iterator();

                while(var9.hasNext()) {
                    Object object2 = var9.next();
                    JSONObject obj = (JSONObject)object2;
                    oid = obj.getMongoID(this.pkString);
                    name = obj.getString("name");
                    object.put(oid, name);
                }
            }

            return object.toJSONString();
        }
    }

    private JSONArray join(JSONArray array) {
        String content = "";
        String list = "";
        int l = array.size();
        if (array != null && array.size() != 0) {
            try {
                JSONObject tempTemplateObj = this.getTempInfo(array);
                String tContentID = null;
                String tListID = null;

                for(int i = 0; i < l; ++i) {
                    JSONObject object = (JSONObject)array.get(i);
                    if (tempTemplateObj != null) {
                        tContentID = object.getString("tempContent");
                        if (tempTemplateObj.containsKey(tContentID)) {
                            content = tempTemplateObj.getString(tContentID);
                        }

                        tListID = object.getString("tempList");
                        if (tempTemplateObj.containsKey(tListID)) {
                            list = tempTemplateObj.getString(tListID);
                        }
                    }

                    object.put("TemplateList", list);
                    object.put("TemplateContent", content);
                    array.set(i, object);
                }
            } catch (Exception var10) {
                nlogger.logout(var10);
            }

            return array;
        } else {
            return array;
        }
    }

    private JSONObject getTempInfo(JSONArray array) {
        CacheHelper cas = new CacheHelper();
        new JSONObject();
        JSONObject tempobj = null;
        String content = null;
        String list = null;
        String tid = "";
        String temp = "";
        if (array != null && array.size() != 0) {
            int l = array.size();

            for(int i = 0; i < l; ++i) {
                JSONObject object = (JSONObject)array.get(i);
                content = object.getString("tempContent");
                list = object.getString("tempList");
                if (StringHelper.InvaildString(content) && !content.equals("0") && !tid.contains(content)) {
                    tid = tid + content + ",";
                }

                if (StringHelper.InvaildString(list) && !list.equals("0") && !tid.contains(list)) {
                    tid = tid + list + ",";
                }
            }

            if (!tid.equals("") && tid.length() > 0) {
                if (cas.get("Temp_" + tid) != null) {
                    return JSONObject.toJSON(cas.get("Temp" + tid));
                }

                tid = StringHelper.fixString(tid, ',');
                if (!tid.equals("")) {
                    temp = (String)appsProxy.proxyCall("/GrapeTemplate/TemplateContext/TempFindByTid/s:" + tid);
                    tempobj = JSONObject.toJSON(temp);
                    if (tempobj != null && tempobj.size() > 0) {
                        cas.setget("Temp_" + tid, tempobj, 86400L);
                    }
                }
            }
        }

        return tempobj != null && tempobj.size() != 0 ? tempobj : null;
    }

    private boolean IsWeb(String wbid) {
        String web = "";
        if (StringHelper.InvaildString(wbid)) {
            appIns appIns = appsProxy.getCurrentAppInfo();
            web = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/" + this.currentWeb, appIns).toString();
        }

        return web.contains(wbid);
    }

    public String getClickCount(String ogid) {
        JSONArray array = null;
        JSONObject rObject = new JSONObject();
        if (!StringHelper.InvaildString(ogid)) {
            return rMsg.netMSG(1, "无效栏目id");
        } else {
            JSONArray condArray = this.model.getOrCond(this.pkString, ogid);
            if (condArray != null && condArray.size() > 0) {
                array = this.group.or().where(condArray).select();
            }

            if (array != null && array.size() != 0) {
                Iterator var9 = array.iterator();

                while(var9.hasNext()) {
                    Object object2 = var9.next();
                    JSONObject object = (JSONObject)object2;
                    String id = object.getString("_id");
                    String clickCount = object.getString("clickcount");
                    rObject.put(id, Long.parseLong(clickCount));
                }
            }

            return rObject.toJSONString();
        }
    }

    public String getColumnInfo(String wbid, String name) {
        JSONArray array = null;
        String[] value = null;
        if (!StringHelper.InvaildString(wbid)) {
            return rMsg.netMSG(1, "无效站点id");
        } else {
            JSONArray condArray = this.model.getOrCondArray("wbid", wbid);
            if (condArray != null && condArray.size() > 0) {
                array = this.group.or().where(condArray).and().eq("name", name).select();
            }

            JSONObject rsObject = this.JoinObj((String[])value, array);
            return rsObject != null && rsObject.size() != 0 ? rsObject.toString() : null;
        }
    }

    private JSONObject JoinObj(String[] value, JSONArray array) {
        JSONObject rsObject = null;
        if (array != null && array.size() != 0) {
            rsObject = new JSONObject();
            int l = array.size();

            for(int i = 0; i < l; ++i) {
                JSONObject object = (JSONObject)array.get(i);
                String id = object.getString(this.pkString);
                String wbid = object.getString("wbid");
                String[] var12 = value;
                int var11 = value.length;

                for(int var10 = 0; var10 < var11; ++var10) {
                    String string = var12[var10];
                    if (wbid.equals(string)) {
                        rsObject.put(string, id);
                    }
                }
            }
        }

        return rsObject;
    }

    public String isPublic(String ogid) {
        String slevel = "0";
        JSONObject object = this.group.eq(this.pkString, ogid).find();
        if (object != null && object.size() != 0) {
            String temp = object.getString("slevel");
            slevel = temp;
        }

        return slevel;
    }

    public String getAllColumn(String ogid) {
        JSONArray data = this.group.eq("fatherid", ogid).select();
        String rsString = ogid;

        String tmpWbid;
        for(Iterator var7 = data.iterator(); var7.hasNext(); rsString = rsString + "," + this.getAllColumn(tmpWbid)) {
            Object obj = var7.next();
            JSONObject object = (JSONObject)obj;
            tmpWbid = object.getString(this.pkString);
        }

        return StringHelper.fixString(rsString, ',');
    }

    public String SetPushState(String ogid, String data) {
        long code = 0L;
        String result = rMsg.netMSG(100, "设置失败");
        JSONObject object = JSONObject.toJSON(data);
        if (object != null && object.size() > 0) {
            JSONArray condArray = this.model.getOrCond(this.pkString, ogid);
            if (condArray != null && condArray.size() > 0) {
                code = this.group.or().where(condArray).data(object).updateAll();
                result = code == 0L ? rMsg.netMSG(0, "设置成功") : result;
            }
        }

        return result;
    }

    public String getPushArticle(String wbid) {
        JSONArray array = this.group.eq("wbid", wbid).eq("ispush", "0").select();
        return rMsg.netMSG(true, array != null && array.size() > 0 ? array : new JSONArray());
    }

    public String getPrevColumn(String wbid) {
        JSONArray array = this.group.eq("wbid", wbid).select();
        return array != null && array.size() > 0 ? array.toJSONString() : (new JSONArray()).toJSONString();
    }

    public String getColumnByWbid(String wbid) {
        JSONArray array = null;
        if (StringHelper.InvaildString(wbid)) {
            array = this.group.eq("wbid", wbid).field(this.pkString + ",wbid,name,fatherid").select();
        }

        return array != null && array.size() > 0 ? array.toJSONString() : (new JSONArray()).toJSONString();
    }

    public String getOgidByName(String wbid, String columnName) {
        String ogid = "";
        if (StringHelper.InvaildString(wbid) && StringHelper.InvaildString(columnName)) {
            JSONObject object = this.group.eq("name", columnName).eq("wbid", wbid).field(this.pkString).find();
            if (object != null && object.size() > 0) {
                ogid = object.getString(this.pkString);
            }
        }

        return ogid;
    }

    public String getOgname_By_ogid(String ogid) {
        String ogname = "";
        if (StringHelper.InvaildString(ogid)) {
            JSONObject object = this.group.eq(this.pkString, ogid).find();
            if (object != null && object.size() > 0) {
                ogname = object.getString("name");
            }
        }

        return ogname;
    }
}
