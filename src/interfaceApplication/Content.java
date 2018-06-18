//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package interfaceApplication;

import Model.CommonModel;
import Model.WsCount;
import common.java.Concurrency.distributedLocker;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appIns;
import common.java.apps.appsProxy;
import common.java.browser.PhantomJS;
import common.java.cache.CacheHelper;
import common.java.check.checkHelper;
import common.java.database.dbFilter;
import common.java.httpClient.request;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.json.JSONHelper;
import common.java.nlogger.nlogger;
import common.java.offices.excelHelper;
import common.java.privacyPolicy.privacyPolicy;
import common.java.rpc.execRequest;
import common.java.security.codec;
import common.java.session.session;
import common.java.string.StringHelper;
import common.java.thirdsdk.kuweiCheck;
import common.java.time.TimeHelper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import unit.Ceshi;
import unit.JSONArrayUtils;
import unit.LogsUtils;
import unit.Print_nlogger;
import unit.TXTUtils;
import unit.WebUtils;

public class Content {
    private GrapeTreeDBModel content = this.getDB();
    private CommonModel model = new CommonModel();
    private session se;
    private JSONObject userInfo = null;
    private String currentWeb = null;
    private Integer userType = null;
    private CacheHelper ch = new CacheHelper();
    private String pkString = null;
    private ContentGroup contentGroup = new ContentGroup();
    private String userName;
    private String userId;
    private int a = 0;
    private static ExecutorService rs = Executors.newFixedThreadPool(300);
    private static final long delay = 3600L;

    private GrapeTreeDBModel getDB() {
        GrapeTreeDBModel _content = new GrapeTreeDBModel();
        GrapeDBSpecField _gDbSpecField = new GrapeDBSpecField();
        _gDbSpecField.importDescription(appsProxy.tableConfig("Content"));
        _content.descriptionModel(_gDbSpecField);
        _content.bindApp();
        return _content;
    }

    public Content() {
        this.pkString = this.content.getPk();
        this.se = new session();
        this.userInfo = this.se.getDatas();
        if (this.userInfo != null && this.userInfo.size() != 0) {
            this.currentWeb = this.userInfo.getString("currentWeb");
            this.userName = this.userInfo.getString("name");
            this.userId = this.userInfo.getString("id");
            this.userType = this.userInfo.getInt("userType");
        }

    }

    public void content_to_txt(String id) {
        final File file = new File("c:\\txts\\");
        boolean mkdirs = file.mkdirs();
        this.content.field("content").gte(this.pkString, id).eq("isdelete", 0).ne("content", "").ne("content", (Object)null).scan(new Function<JSONArray, JSONArray>() {
            int i = 0;

            public JSONArray apply(JSONArray arg0) {
                Iterator var3 = arg0.iterator();

                while(var3.hasNext()) {
                    Object object = var3.next();
                    JSONObject o = (JSONObject)object;
                    String content = o.getString("content");
                    ++this.i;
                    TXTUtils.string2Txt(file.getPath() + "\\" + this.i + ".txt", content, "utf-8");
                    if (this.i > 300) {
                        throw new RuntimeException();
                    }
                }

                return arg0;
            }
        }, 30);
    }

    public String read_by_id(String _id) {
        String netMSG = rMsg.netMSG(1, "文章id错误");
        JSONObject find = this.content.eq(this.pkString, _id).find();
        if (find != null) {
            netMSG = rMsg.netMSG(0, find.toJSONString());
        }

        return netMSG;
    }

    public String CheckLink(int idx, int pageSize) {
        final distributedLocker countLocker = distributedLocker.newLocker("BadlinkAndDiedlink_" + this.currentWeb);
        final appIns currentAppInfo = appsProxy.getCurrentAppInfo();
        String rString = null;
        long neartime1 = 0L;
        final CacheHelper ch = new CacheHelper();
        rString = ch.get("BadlinkAndDiedlink_" + this.currentWeb);
        JSONObject jsobj1 = JSONObject.toJSON(rString);
        String object;
        if (jsobj1 != null) {
            object = jsobj1.getString("0");
            neartime1 = Long.parseLong(object);
        }

        if (countLocker != null) {
            boolean flag = countLocker.lock();
            if (flag && (rString == null || neartime1 == 0L || TimeHelper.nowMillis() - neartime1 >= 86400000L)) {
                (new Thread(new Runnable() {
                    public void run() {
                        try {
                            JSONArray rString1 = Content.this.CheckLink1();
                            long nowMillis = TimeHelper.nowMillis();
                            JSONObject puts = (new JSONObject()).puts("0", nowMillis).puts("1", rString1);
                            ch.setNX("BadlinkAndDiedlink_" + Content.this.currentWeb, puts.toJSONString());
                        } catch (Exception var8) {
                            var8.printStackTrace();
                        } finally {
                            appsProxy.setCurrentAppInfo(currentAppInfo);
                            countLocker.unlock();
                            System.out.println("");
                        }

                    }
                })).start();
            }

            countLocker.releaseLocker();
        }

        if (!StringHelper.InvaildString(rString)) {
            rString = rMsg.netMSG(1, "暂时没有错链");
        } else if (jsobj1 != null) {
            object = jsobj1.getString("1");
            JSONArray jsonArray = JSONArray.toJSONArray(object);
            JSONArray jsonarray_page = JSONArrayUtils.jsonarray_page(jsonArray, idx, pageSize);
            int size = jsonArray.size();
            rString = rMsg.netPAGE(idx, pageSize, (long)size, jsonarray_page);
        }

        return rString;
    }

    private JSONArray CheckLink1() {
        JSONArray rArray = this.content.eq("wbid", this.currentWeb).eq("isdelete", 0).field("url,ogid,mainName,time").scan((array) -> {
            JSONArray _rArray = new JSONArray();
            if (array.size() > 0 && array != null) {
                Iterator var4 = array.iterator();

                while(var4.hasNext()) {
                    Object object = var4.next();
                    Print_nlogger.Print_SYSO(this.a++);
                    JSONObject json = (JSONObject)object;
                    String url = json.getString("url");
                    String reponse = "";

                    try {
                        reponse = request.Get(url);
                    } catch (Exception var14) {
                        if (!"host parameter is null".equals(var14.getMessage())) {
                            var14.printStackTrace();
                        }
                    }

                    reponse = reponse.trim();
                    if (!StringHelper.InvaildString(reponse) || !reponse.endsWith("</html>")) {
                        String ogid = json.getString("ogid");
                        GrapeTreeDBModel group = this.contentGroup.getGroup();
                        JSONObject obj = group.eq(this.pkString, ogid).field("name").find();
                        String ogname = obj.getString("name");
                        Long time = json.getLong("time");
                        String mainName = json.getString("mainName");
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.puts("ogname", ogname);
                        jsonObject2.puts("time", time);
                        jsonObject2.puts("mainName", mainName);
                        jsonObject2.puts("url", url);
                        _rArray.add(jsonObject2);
                    }
                }

                return _rArray;
            } else {
                return _rArray;
            }
        }, 10000);
        return rArray;
    }

    private String getWbid(String wbid) {
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

    public String move_articles_to_another_ogid(String wbid, String sourcogid, String targetogid) {
        long updateAll = this.content.eq("wbid", wbid).eq("ogid", sourcogid).data(new JSONObject("ogid", targetogid)).updateAll();
        String netMSG = rMsg.netMSG(0, "更新了" + updateAll);
        return netMSG;
    }

    public String move_articles_to_another_ogid_SELECT(String ids, String targetogid) {
        String netMSG = rMsg.netMSG(0, "参数错误");
        if (StringHelper.InvaildString(ids) && StringHelper.InvaildString(targetogid)) {
            String[] split = ids.split(",");
            this.content.or();
            String[] var8 = split;
            int var7 = split.length;

            for(int var6 = 0; var6 < var7; ++var6) {
                String id = var8[var6];
                this.content.eq(this.pkString, id);
            }

            long updateAll = this.content.eq("isdelete", 0).data(new JSONObject("ogid", targetogid)).updateAll();
            netMSG = rMsg.netMSG(0, "更新了" + updateAll);
            return netMSG;
        } else {
            return netMSG;
        }
    }

    private String getOgid(String wbid, String ogid) {
        String result = ogid;
        if (StringHelper.InvaildString(ogid)) {
            if (checkHelper.checkChinese(ogid)) {
                result = (String)appsProxy.proxyCall("/GrapeContent/ContentGroup/getOgidByName/" + wbid + "/" + ogid);
            }

            if (!StringHelper.InvaildString(result)) {
                return rMsg.netMSG(1, "无效栏目id");
            }
        }

        return result;
    }

    public String SetTop(String oid) {
        long SetTime = TimeHelper.nowMillis();
        return this.Runtop(oid, SetTime);
    }

    public String cancelTop(String oid) {
        long SetTime = 0L;
        return this.Runtop(oid, SetTime);
    }

    private String Runtop(String oid, long setTime) {
        JSONObject object = new JSONObject("isTop", setTime);
        JSONObject rjson = this.content.eq(this.pkString, oid).data(object).update();
        return rMsg.netMSG(rjson != null, "");
    }

    protected long getContentCount(String ogid) {
        long count = 0L;
        if (StringHelper.InvaildString(ogid)) {
            count = this.content.eq("ogid", ogid).count();
        }

        return count;
    }

    public String pushToColumn(String oid, String ogid) {
        JSONObject rs = new JSONObject();
        String result = rMsg.netMSG(100, "文章移动失败");
        if (!StringHelper.InvaildString(oid)) {
            return rMsg.netMSG(1, "无效文章id");
        } else if (!StringHelper.InvaildString(oid)) {
            return rMsg.netMSG(1, "无效栏目id");
        } else {
            if (ObjectId.isValid(ogid) || checkHelper.isInt(ogid)) {
                JSONObject obj = new JSONObject("ogid", ogid);
                rs = this.content.eq(this.pkString, oid).data(obj).update();
            }

            return rs != null ? rMsg.netMSG(0, "文章移动成功") : result;
        }
    }

    protected String deleteByOgid(String ogid) {
        JSONArray condArray = null;
        String result = rMsg.netMSG(1, "删除失败");
        if (StringHelper.InvaildString(ogid)) {
            condArray = this.model.getOrCondArray("ogid", ogid);
            if (condArray != null && condArray.size() > 0) {
                long code = this.content.or().where(condArray).deleteAll();
                result = code > 0L ? rMsg.netMSG(0, "删除成功") : result;
            }
        }

        return result;
    }

    protected String inserts(JSONObject object) {
        return this.content.data(object).insertOnce().toString();
    }

    public String AddAllArticle(String ArticleInfo) {
        String tip = rMsg.netMSG(100, "新增失败");
        List<String> list = new ArrayList();
        long time = 0L;
        int code = 99;
        JSONArray array = JSONHelper.string2array(ArticleInfo);
        if (array != null && array.size() != 0) {
            Iterator var11 = array.iterator();

            while(var11.hasNext()) {
                Object obj = var11.next();
                JSONObject object = (JSONObject)obj;
                if (object.containsKey("time")) {
                    time = Long.parseLong(object.getString("time"));
                    object.put("time", time);
                }

                String info = this.AddAll(object);
                if (info == null) {
                    if (list.size() != 0) {
                        this.DeleteArticle(StringHelper.join(list));
                    }

                    code = 99;
                    break;
                }

                code = 0;
                list.add(info);
            }

            if (code == 0) {
                tip = rMsg.netMSG(true, this.batch(list));
            } else {
                tip = rMsg.netMSG(100, "新增失败");
            }
        }

        return tip;
    }

    private JSONArray batch(List<String> list) {
        JSONArray array = new JSONArray();
        if (list != null && list.size() > 0) {
            String ids = StringHelper.join(list);
            JSONArray condArray = this.model.getOrCond(this.pkString, ids);
            if (condArray != null && condArray.size() > 0) {
                array = this.content.or().where(condArray).select();
            }
        }

        return this.model.join(this.model.getImgs(this.model.ContentDencode(array)));
    }

    private String AddAll(JSONObject object) {
        String info = this.checkparam(object);
        Object tip = JSONHelper.string2json(info) != null && !info.contains("errorcode") ? this.content.data(info).autoComplete().insertOnce() : null;
        return tip != null ? tip.toString() : null;
    }

    public String ContentIsExist(String ogid, String mainName, String time, int type) {
        String contentInfo = "";
        JSONObject object = JSONObject.toJSON(execRequest.getChannelValue("exData").toString());
        contentInfo = object.getString("param");
        return this.ContentIsExist(ogid, mainName, time, type, contentInfo);
    }

    public String ContentIsExist(String ogid, String mainName, String time, int type, String contentInfo) {
        JSONArray object = null;
        contentInfo = codec.DecodeHtmlTag(contentInfo);
        contentInfo = codec.decodebase64(contentInfo);
        String rt = "1";
        if (StringHelper.InvaildString(ogid)) {
            this.content.eq("ogid", ogid).eq("mainName", codec.DecodeFastJSON(mainName)).eq("isdelete", 0);
            if (type == 0) {
                this.content.ne("content", contentInfo);
            } else {
                this.content.ne("contenturl", contentInfo);
            }

            object = this.content.select();
            Object object2;
            Iterator var9;
            JSONObject o;
            if (object != null && object.size() > 0) {
                var9 = object.iterator();

                while(var9.hasNext()) {
                    object2 = var9.next();
                    o = (JSONObject)object2;
                    long oldtime = o.getLong("time");
                    long newtime = Long.parseLong(time);
                    long a = newtime - oldtime;
                    if (Math.abs(a) <= 604800000L) {
                        String _id = o.getString(this.pkString);
                        this.content.eq(this.pkString, _id).data((new JSONObject()).puts("isdelete", 1)).update();
                        rt = "0";
                    }
                }

                return rt;
            }

            this.content.eq("ogid", ogid).eq("mainName", codec.DecodeFastJSON(mainName)).eq("isdelete", 0);
            if (type == 0) {
                this.content.eq("content", contentInfo);
            } else {
                this.content.eq("contenturl", contentInfo);
            }

            object = this.content.select();
            if (object != null && object.size() > 0) {
                for(var9 = object.iterator(); var9.hasNext(); rt = "0") {
                    object2 = var9.next();
                    o = (JSONObject)object2;
                    String _id = o.getString(this.pkString);
                    this.content.eq(this.pkString, _id).data((new JSONObject()).puts("isdelete", 1)).update();
                }
            } else {
                rt = "0";
            }
        }

        return rt;
    }

    public String AddCrawlerContent(String infp) {
        String contentInfo = "";
        JSONObject object = JSONObject.toJSON(execRequest.getChannelValue("exData").toString());
        contentInfo = object.getString("param");
        return this.AddCrawlerContent(infp, contentInfo);
    }

    public String AddCrawlerContent(String infp, String contentInfo) {
        int state = 2;
        Object info = null;
        String result = rMsg.netMSG(100, "添加失败");
        contentInfo = codec.DecodeFastJSON(contentInfo);
        JSONObject object = JSONObject.toJSON(contentInfo);
        if (object != null && object.size() > 0) {
            if (object.containsKey("ogid")) {
                String ogid = object.getString("ogid");
                String temp = (new ContentGroup()).isPublic(ogid);
                if (temp.equals("1")) {
                    state = 0;
                }

                object.put("state", Integer.valueOf(state));
            }

            info = this.content.data(object).autoComplete().insertOnce();
            result = info != null ? rMsg.netMSG(0, "添加成功") : result;
        }

        return result;
    }

    public String PublishArticle(String ArticleInfo) {
        long state = 2L;
        long time = 0L;
        String temptime = "";
        String ogname = "";
        long currentTime = TimeHelper.nowMillis();
        ArticleInfo = codec.DecodeFastJSON(ArticleInfo);
        
        JSONObject object = JSONHelper.string2json(ArticleInfo);
        if (!StringHelper.InvaildString(this.currentWeb)) {
            return rMsg.netMSG(1, "当前登录信息已失效,请重新登录后再发布文章");
        } else if (object != null && object.size() > 0) {
            if (object.containsKey("time")) {
                temptime = object.getString("time");
                if (StringHelper.InvaildString(temptime)) {
                    time = Long.parseLong(temptime);
                }
            }

            if (time != 0L) {
                time = time < currentTime ? time : currentTime;
            } else {
                time = currentTime;
            }

            object.put("time", time);
            object.put("wbid", this.currentWeb);
            if (object.containsKey("ogid")) {
                ContentGroup contentGroup = new ContentGroup();
                String ogid = object.getString("ogid");
                ogname = WebUtils.getOgname_by_ogid(ogid);
                String temp = contentGroup.isPublic(ogid);
                if (temp.equals("1")) {
                    state = 0L;
                }

                contentGroup.publishActicle_than_update(ogid);
                object.put("state", state);
            }

            object.put("isvisble", 0);
            object.put("isdelete", 0);
            object.put("subID", this.RandomNum());
            
//            System.out.println("**********   object数据   **********");
//            System.out.println(object.toJSONString());
//            System.out.println("****************************************************************");
            
            String insert = this.insert(object);
            JSONObject json = JSONObject.toJSON(insert);
            if (json.getInt("errorcode") == 0) {
                LogsUtils.addLogs(this.userId, this.userName, "在[" + ogname + "]栏目下发布了[" + object.getString("mainName") + "]新闻", this.currentWeb, "PublishArticle");
            }

            return insert;
        } else {
            return rMsg.netMSG(100, "发布失败");
        }
    }

    private int RandomNum() {
        int number = (new Random()).nextInt(2147483647) + 1;
        return number;
    }

    private String insert(JSONObject contentInfo) {
        String info = null;
        int state = 0;
        JSONObject ro = null;
        String result = rMsg.netMSG(100, "新增文章失败");

        try {
            info = this.checkparam(contentInfo);
            JSONObject object = JSONObject.toJSON(info);
            if (object != null && object.size() > 0) {
                if (object.containsKey("errorcode")) {
                    return info;
                }

                state = object.getInt("state");
                info = this.content.data(object).autoComplete().insertOnce().toString();
                ro = this.findOid(info);
                result = ro != null && ro.size() > 0 ? rMsg.netMSG(0, ro) : result;
            }
        } catch (Exception var7) {
            nlogger.logout(var7);
            result = rMsg.netMSG(100, "新增文章失败");
        }

        return result;
    }

    public String Publish(String ArticleInfo) {
        long state = 2L;
        long currentTime = TimeHelper.nowMillis();
        ArticleInfo = codec.DecodeFastJSON(ArticleInfo);
        JSONObject object = JSONHelper.string2json(ArticleInfo);
        if (!StringHelper.InvaildString(this.currentWeb)) {
            return rMsg.netMSG(1, "当前登录信息已失效,请重新登录后再发布文章");
        } else if (object != null && object.size() > 0) {
            if (object.containsKey("time")) {
                long time = Long.parseLong(object.getString("time"));
                if (time > currentTime) {
                    object.put("time", currentTime);
                }
            } else {
                object.put("time", currentTime);
            }

            object.put("wbid", this.currentWeb);
            if (object.containsKey("ogid")) {
                String ogid = object.getString("ogid");
                String temp = (new ContentGroup()).isPublic(ogid);
                if (temp.equals("1")) {
                    state = 0L;
                }

                object.put("state", state);
            }

            object.put("subID", this.RandomNum());
            return this.insertCheckContent(object);
        } else {
            return rMsg.netMSG(100, "发布失败");
        }
    }

    private String insertCheckContent(JSONObject contentInfo) {
        String info = null;
        String result = rMsg.netMSG(100, "新增文章失败");

        try {
            contentInfo.put("isvisble", 1);
            info = this.checkparam(contentInfo);
            JSONObject object = JSONObject.toJSON(info);
            if (object != null && object.size() > 0) {
                if (info.contains("errorcode")) {
                    return info;
                }

                int state = object.getInt("state");
                String _info = this.content.data(object).autoComplete().insertOnce().toString();
                appIns env = appsProxy.getCurrentAppInfo();
                rs.execute(() -> {
                    appsProxy.setCurrentAppInfo(env);
                    this._insertCheck(contentInfo, _info, state);
                });
                result = rMsg.netMSG(0, "文章已进入发布队列");
            }
        } catch (Exception var8) {
            nlogger.logout(var8);
            result = rMsg.netMSG(100, "新增文章失败");
        }

        return result;
    }

    void _insertCheck(JSONObject contentInfo, String info, int state) {
        JSONObject obj = new JSONObject();
        String cid = this.checkContent(contentInfo, info);
        if (StringHelper.InvaildString(cid)) {
            obj.put("isvisble", 4);
        } else {
            (new ContentError()).delete(info);
            obj.put("isvisble", 0);
        }

        this.update(info, obj);
        appsProxy.proxyCall("/GrapeSendKafka/SendKafka/sendData2Kafka/" + info + "/int:1/int:1/int:" + state);
    }

    private void update(String oid, JSONObject obj) {
        GrapeTreeDBModel model = this.getDB();
        if (StringHelper.InvaildString(oid) && obj != null && obj.size() > 0) {
            model.eq("_id", oid).data(obj).update();
        }

    }

    public String searchNotCheck(int idx, int pageSize) {
        GrapeTreeDBModel content = this.getDB();
        long count = 0L;
        content.eq("isvisble", 4);
        count = content.dirty().count();
        JSONArray array = content.page(idx, pageSize);
        return rMsg.netPAGE(idx, pageSize, count, array);
    }

    private String checkContent(JSONObject object, String oid) {
        privacyPolicy pp = new privacyPolicy();
        String contents = "";
        String errorcount = "0";
        String info = null;
        String cid = null;
        if (object != null && object.size() > 0 && object.containsKey("content")) {
            contents = (String)object.escapeHtmlGet("content");
            if (!StringHelper.InvaildString(contents)) {
                return rMsg.netMSG(1, "文章内容不允许为空");
            }

            info = pp.scanText(contents);
            JSONObject obj = JSONObject.toJSON(this.ContentTypos(info));
            String message = obj.getString("message");
            obj = JSONObject.toJSON(message.toLowerCase());
            errorcount = obj.getString("errorcount");
            if (!errorcount.equals("0") || pp.hasPrivacyPolicy()) {
                cid = (new ContentError()).insert(obj.toJSONString(), oid);
            }
        }

        return cid;
    }

    private String ContentTypos(String contents) {
        String result = rMsg.netMSG(100, "错别字识别失败");
        String ukey = "377c9dc160bff6cfa3cc0cbc749bb11a";

        try {
            if (StringHelper.InvaildString(contents)) {
                kuweiCheck check = new kuweiCheck(ukey);
                contents = check.checkContent(contents);
                result = rMsg.netMSG(0, contents);
            }
        } catch (Exception var5) {
            nlogger.logout(var5);
            result = rMsg.netMSG(101, "服务器连接异常，暂无法识别");
        }

        return result;
    }

    private String PushArticleGov(JSONObject object) {
        String GovId = "";
        String result = rMsg.netMSG(100, "同步文章至政府信息公开网失败");
        if (object != null && object.size() > 0) {
            if (object.containsKey("GovId")) {
                GovId = object.getString("GovId");
            }

            if (StringHelper.InvaildString(GovId) && !GovId.equals("0")) {
                result = (new PushContentToGov()).pushToGov(object, GovId);
            }
        }

        return result;
    }

    private String PushGov(JSONObject object) {
        String result = rMsg.netMSG(100, "同步文章至政府信息公开网失败");
        if (object != null && object.size() > 0) {
            String ogid = this.getConnColumn(object);
            if (!StringHelper.InvaildString(ogid) || ogid.equals("0")) {
                return rMsg.netMSG(3, "无关联栏目id，无法同步文章至政府信息公开网");
            }

            result = (new PushContentToGov()).pushToGov(object, ogid);
        }

        return result;
    }

    private String getConnColumn(JSONObject object) {
        String ogid = "";
        String cols = "";
        if (object != null && object.size() > 0) {
            if (object.containsKey("ogid")) {
                ogid = object.getString("ogid");
            }

            if (StringHelper.InvaildString(ogid)) {
                JSONObject tempobj = (new ContentGroup()).find(ogid);
                if (tempobj != null && tempobj.size() > 0 && tempobj.containsKey("connColumn")) {
                    cols = tempobj.getString("connColumn");
                }
            }
        }

        return cols;
    }

    public String Typos(String contents) {
        String result = rMsg.netMSG(100, "错别字识别失败");
        String ukey = "377c9dc160bff6cfa3cc0cbc749bb11a";

        try {
            if (StringHelper.InvaildString(contents)) {
                contents = this.model.dencode(contents);
                kuweiCheck check = new kuweiCheck(ukey);
                contents = check.checkContent(contents);
                result = rMsg.netMSG(0, contents);
            }
        } catch (Exception var5) {
            nlogger.logout(var5);
            result = rMsg.netMSG(101, "服务器连接异常，暂无法识别");
        }

        return result;
    }

    public String EditArticle(String oid, String contents) {
        Object temp = null;
        long time = 0L;
        String temptime = "";
        String contentInfos = "";
        long currentTime = TimeHelper.nowMillis();
        String result = rMsg.netMSG(100, "文章更新失败");
        contents = codec.DecodeFastJSON(contents);
        JSONObject infos = JSONHelper.string2json(contents);
        if (this.userInfo != null && this.userInfo.size() > 0) {
            if (infos != null && infos.size() > 0) {
                contents = this.checkparam(infos);
                if (JSONHelper.string2json(contents) != null && contents.contains("errorcode")) {
                    return contents;
                }

                JSONObject obj = this.content.eq("_id", oid).eq("state", 1).find();
                if (obj != null && obj.size() > 0) {
                    infos.put("state", 0);
                }

                infos.put("editTime", TimeHelper.nowMillis());
                if (infos.containsKey("time")) {
                    temptime = infos.getString("time");
                    if (StringHelper.InvaildString(temptime)) {
                        time = Long.parseLong(temptime);
                    }

                    if (time != 0L) {
                        time = time < currentTime ? time : currentTime;
                    } else {
                        time = currentTime;
                    }

                    infos.put("time", time);
                }

                temp = this.content.eq("_id", oid).data(infos).updateEx();
                result = temp != null ? rMsg.netMSG(0, "文章更新成功") : result;
            }

            return result;
        } else {
            return rMsg.netMSG(1, "当前登录信息已失效,请重新登录后再修改文章");
        }
    }

    public String Edit(String oid, String contents, String flag) {
        Object temp = null;
        String result = rMsg.netMSG(100, "文章更新失败");
        contents = codec.DecodeFastJSON(contents);
        JSONObject infos = JSONHelper.string2json(contents);
        if (this.userInfo != null && this.userInfo.size() > 0) {
            if (infos != null && infos.size() > 0) {
                contents = this.checkparam(infos);
                if (JSONHelper.string2json(contents) != null && contents.contains("errorcode")) {
                    return contents;
                }

                if (!infos.containsKey("content")) {
                    temp = this.content.eq(this.pkString, oid).data(infos).updateEx();
                } else if (flag.equals("0")) {
                    infos.put("isvisble", 0);
                    temp = this.content.eq(this.pkString, oid).data(infos).updateEx();
                } else {
                    temp = this.content.eq(this.pkString, oid).data(infos).updateEx();
                    appIns env = appsProxy.getCurrentAppInfo();
                    rs.execute(() -> {
                        appsProxy.setCurrentAppInfo(env);
                        this._insertCheck(infos, oid, 0);
                    });
                }

                result = rMsg.netMSG(0, "文章已进入发布队列");
            }

            return result;
        } else {
            return rMsg.netMSG(1, "当前登录信息已失效,请重新登录后再修改文章");
        }
    }

    public String DeleteArticle(String oid) {
        int code = 99;
        String result = rMsg.netMSG(100, "删除失败");
        JSONArray condArray = this.model.getOrCond(this.pkString, oid);
        if (condArray != null && condArray.size() > 0) {
            JSONObject object = new JSONObject();
            object.put("isdelete", 1);
            code = this.content.or().where(condArray).data(object).updateAll() > 0L ? 0 : 99;
            result = code == 0 ? rMsg.netMSG(0, "删除成功") : result;
        }

        return result;
    }

    public Object ExportContent(String ContentInfo, String fileName) {
        try {
            ContentInfo = this.SearchExportInfo(ContentInfo);
            if (StringHelper.InvaildString(ContentInfo)) {
                return excelHelper.out(ContentInfo);
            }
        } catch (Exception var4) {
            nlogger.logout(var4, "导出异常");
        }

        return rMsg.netMSG(false, "导出失败");
    }

    public Object ExportContent_ckd(String wbid, String ContentInfo, String fileName) {
        try {
            ContentInfo = this.SearchExportInfo_ckd(ContentInfo, wbid);
            if (StringHelper.InvaildString(ContentInfo)) {
                return excelHelper.out(ContentInfo);
            }
        } catch (Exception var5) {
            nlogger.logout(var5, "导出异常");
        }

        return rMsg.netMSG(false, "导出失败");
    }

    private String SearchExportInfo_ckd(String condString, String wbid) {
        JSONArray array = null;
        if (StringHelper.InvaildString(condString)) {
            JSONArray condArray = JSONArray.toJSONArray(condString);
            array = this.content.eq("wbid", wbid).where(condArray).field("wbid").select();

            Object var5;
            for(Iterator var6 = condArray.iterator(); var6.hasNext(); var5 = var6.next()) {
                ;
            }
        }

        return condString;
    }

    private String SearchExportInfo(String condString) {
        String ogids = "";
        JSONArray array = null;
        if (StringHelper.InvaildString(condString)) {
            JSONArray condArray = JSONArray.toJSONArray(condString);
            array = this.content.eq("wbid", this.currentWeb).where(condArray).field("mainName,state,souce,author,time,ogid").select();
            if (array != null && array.size() > 0) {
                Iterator var9 = array.iterator();

                JSONObject obj;
                String temp;
                while(var9.hasNext()) {
                    Object object = var9.next();
                    obj = (JSONObject)object;
                    temp = obj.getString("ogid");
                    if (!ogids.contains(temp)) {
                        ogids = ogids + temp + ",";
                    }
                }

                String ColumnInfo = (new ContentGroup()).getColumnName(StringHelper.fixString(ogids, ','));
                JSONObject column = JSONObject.toJSON(ColumnInfo);
                if (column != null && column.size() > 0) {
                    int l = array.size();

                    for(int i = 0; i < l; ++i) {
                        obj = (JSONObject)array.get(i);
                        temp = obj.getString("ogid");
                        obj.put("column", column.getString(temp));
                        array.set(i, obj);
                    }
                }
            }
        }

        return this.CollateInfo(array);
    }

    private String CollateInfo(JSONArray array) {
        String rString = null;
        long state = 0L;
        long time = 0L;
        String statString = "审核通过";
        String timeDate = null;
        String mainName = "";
        String author = "";
        String souce = "";
        if (array != null && array.size() > 0) {
            int l = array.size();

            for(int i = 0; i < l; ++i) {
                JSONObject obj = (JSONObject)array.get(i);
                if (obj.containsKey("state")) {
                    state = obj.getLong("state");
                    statString = state == 0L ? "待审核" : (state == 1L ? "审核不通过" : "审核通过");
                }

                if (obj.containsKey("time")) {
                    time = obj.getLong("time");
                    timeDate = time != 0L ? TimeHelper.stampToDate(time) : null;
                }

                if (obj.containsKey("mainName")) {
                    mainName = obj.getString("mainName");
                }

                if (obj.containsKey("author")) {
                    author = obj.getString("author");
                }

                if (obj.containsKey("souce")) {
                    souce = obj.getString("souce");
                }

                obj.put("文章标题", mainName);
                obj.put("来源", souce);
                obj.put("作者", author);
                obj.put("发布时间", timeDate);
                obj.put("文章状态", statString);
                obj.put("所属栏目", obj.getString("column"));
                obj.remove(this.pkString);
                obj.remove("ogid");
                obj.remove("column");
                obj.remove("mainName");
                obj.remove("state");
                obj.remove("souce");
                obj.remove("author");
                obj.remove("time");
                array.set(i, obj);
            }

            rString = array.toJSONString();
        }

        return rString;
    }

    public String FindNewArc(String wbid, int idx, int pageSize) {
        long total = 0L;
        JSONArray array = null;
        this.content.eq("wbid", this.model.getRWbid(wbid)).eq("state", 2).eq("isdelete", 0).eq("isvisble", 0).desc("isTop").desc("time").desc(this.pkString);
        array = this.content.dirty().mask("content").page(idx, pageSize);
        total = this.content.count();
        array = this.model.ContentDencode(array);
        array = this.model.setTemplate(array);
        return rMsg.netPAGE(idx, pageSize, total, array != null && array.size() > 0 ? this.model.join(array) : new JSONArray());
    }

    public String findAllArticles(String ids) {
        String tempID = "";
        JSONObject rJsonObject = new JSONObject();
        String[] value = ids.split(",");
        String[] var9 = value;
        int var8 = value.length;

        for(int var7 = 0; var7 < var8; ++var7) {
            String string = var9[var7];
            if (StringHelper.InvaildString(string) && ObjectId.isValid(string)) {
                this.content.or().eq(this.pkString, string);
            }
        }

        JSONArray array = this.content.and().eq("isdelete", 0).eq("isvisble", 0).field("mainName,content,author,souce,time").select();
        Iterator var12 = array.iterator();

        while(var12.hasNext()) {
            Object object = var12.next();
            JSONObject tempJson = (JSONObject)object;
            tempID = tempJson.getMongoID(this.pkString);
            rJsonObject.put(tempID, this.model.ContentDencode(tempJson));
        }

        return rJsonObject.toJSONString();
    }

    public String findArticle(String oid) {
        String result = rMsg.netMSG(102, "文章不存在");
        GrapeTreeDBModel content = this.getDB();
        JSONObject obj = content.eq("_id", oid).eq("isdelete", 0).eq("isvisble", 0).find();
        int code = this.isShow(obj);
        switch(code) {
        case 0:
            result = this.getSingleArticle(obj, oid);
            break;
        case 1:
            result = rMsg.netMSG(3, "您不属于该单位，无权查看该单位信息");
            break;
        case 2:
            result = rMsg.netMSG(4, "请先登录");
        }

        return result;
    }

    public String getPosition(String oid) {
        String prevCol = "";
        String ogid = "";
        if (!StringHelper.InvaildString(oid)) {
            rMsg.netMSG(1, "无效文章id");
        }

        try {
            JSONObject object = this.content.eq("_id", oid).field("_id,ogid,clickcount").find();
            if (object != null && object.size() > 0) {
                ogid = object.getString("ogid");
                if (StringHelper.InvaildString(ogid)) {
                    prevCol = (new ContentGroup()).getPrevCol(ogid);
                }
            }
        } catch (Exception var5) {
            nlogger.logout(var5);
            prevCol = "";
        }

        return prevCol;
    }

    public void test() {
        JSONObject find = this.content.find();
        String long1 = (String)find.get("clickcount");
        System.out.println();
    }

    public String ShowFront(String wbid, int idx, int pageSize, String condString) {
        long total = 0L;
        JSONArray array = new JSONArray();
        new JSONArray();
        new JSONArray();
        JSONObject obj = this.model.buildCondOgid(condString);
        if (obj != null && obj.size() > 0) {
            JSONArray condArray = obj.getJsonArray("cond");
            JSONArray condOgid = obj.getJsonArray("ogid");
            if (condOgid != null && condOgid.size() > 0) {
                this.content.or().where(condOgid);
            }

            if (condArray != null && condArray.size() > 0) {
                this.content.and().where(condArray);
            }
        }

        if (this.content.getCondCount() > 0) {
            this.content.and().eq("wbid", wbid).eq("slevel", 0).eq("isdelete", 0).eq("isvisble", 0).desc("time").field("_id,mainName,image,time,content");
            array = this.content.dirty().page(idx, pageSize);
            total = this.content.count();
            this.content.clear();
        }

        return rMsg.netPAGE(idx, pageSize, total, this.model.getImgs(this.model.ContentDencode(array)));
    }

    public String ShowByGroupId(String wbid, String ogid) {
        return this.ShowPicByGroupId(wbid, ogid);
    }

    public String ShowPicByGroupId(String wbid, String ogid) {
        JSONArray array = null;
        JSONObject object = null;

        try {
            wbid = this.model.getRWbid(wbid);
            ogid = this.getRogid(ogid);
            if (ogid.contains("errorcode")) {
                return ogid;
            }

            JSONArray condArray = JSONArray.toJSONArray(ogid);
            if (condArray != null && condArray.size() > 0 && StringHelper.InvaildString(wbid)) {
                array = this.content.or().where(condArray).and().eq("wbid", wbid).eq("slevel", 0).eq("isdelete", 0).eq("isvisble", 0).field("_id,mainName,ogid,time,image,contenturl").desc("isTop").desc("time").desc("sort").desc("_id").limit(100).select();
                array = this.model.getImgs(array);
                if (array != null && array.size() > 0) {
                    int l = array.size();

                    for(int i = 0; i < l; ++i) {
                        object = (JSONObject)array.get(i);
                        String img = object.getString("image");
                        object.put("image", img != null && !img.equals("") ? img.split(",")[0] : "");
                        array.set(i, object);
                    }
                }
            }
        } catch (Exception var9) {
            nlogger.logout("Content.findPicByGroupID: " + var9);
            array = null;
        }

        return rMsg.netMSG(true, array);
    }

    private String getRogid(String ogid) {
        String[] value = null;
        dbFilter filter = new dbFilter();
        ogid = (new ContentGroup()).getLinkOgid(ogid);
        if (!StringHelper.InvaildString(ogid)) {
            return rMsg.netMSG(1, "无效栏目id");
        } else if (ogid.contains("errorcode")) {
            return ogid;
        } else {
            value = ogid.split(",");
            if (value != null) {
                String[] var7 = value;
                int var6 = value.length;

                for(int var5 = 0; var5 < var6; ++var5) {
                    String string = var7[var5];
                    if (StringHelper.InvaildString(string)) {
                        filter.eq("ogid", string);
                    }
                }
            }

            return filter.build().toJSONString();
        }
    }

    public String Page(String wbid, int idx, int pageSize) {
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else {
            JSONArray array = this.content.eq("isdelete", 0).eq("isvisble", 0).field("_id,mainName,time,wbid,ogid,image,clickcount,souce,contenturl").desc("isTop").desc("time").desc("sort").desc("_id").page(idx, pageSize);
            array = this.model.setTemplate(array);
            return rMsg.netPAGE(idx, pageSize, this.content.dirty().count(), array);
        }
    }

    public String PageBy(String wbid, int idx, int pageSize, String condString) {
        String out = null;
        long total = 0L;
        new JSONArray();
        new JSONArray();
        new JSONArray();
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else if (!StringHelper.InvaildString(condString)) {
            return rMsg.netMSG(1, "无效条件");
        } else {
            JSONObject obj = this.model.buildCondOgid(condString);
            obj = this.getNextColumn(obj);
            JSONArray array;
            if (obj != null && obj.size() > 0) {
                JSONArray condArray = obj.getJsonArray("cond");
                array = obj.getJsonArray("ogid");
                if (array != null && array.size() > 0) {
                    this.content.or().where(array);
                }

                if (condArray != null && condArray.size() > 0) {
                    this.content.and().where(condArray);
                }
            }

            if (this.content.getCondCount() > 0) {
                this.content.and().eq("wbid", this.model.getRWbid(wbid)).eq("isdelete", 0).eq("isvisble", 0).eq("state", 2);
                total = this.content.dirty().count();
                JSONObject cond = this.content.getCond();
                array = this.content.desc("isTop").desc("time").desc("sort").desc("_id").field("_id,mainName,time,wbid,ogid,image,clickcount,souce,contenturl,isTop,subName,content,desp,author,TemplateContent,Templatelist").page(idx, pageSize);
                array = this.model.setTemplate(array);
                out = rMsg.netPAGE(idx, pageSize, total, this.model.getImgs(this.model.getDefault(wbid, array)));
            } else {
                out = rMsg.netMSG(false, "无效条件");
            }

            return out;
        }
    }

    public String PageBack(int idx, int pageSize) {
        long total = 0L;
        JSONArray array = null;
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else {
            if (10000 > this.userType && this.userType >= 1000) {
                this.content.eq("wbid", this.currentWeb);
            }

            total = this.content.dirty().count();
            array = this.content.desc("isTop").desc("time").desc("sort").page(idx, pageSize);
            array = this.model.setTemplate(array);
            array = this.model.getImgs(this.model.getDefault(this.currentWeb, array));
            array = this.model.ContentDencode(array);
            array = this.FillFileToArray(array);
            return rMsg.netPAGE(idx, pageSize, total, array);
        }
    }

    public String PageByBack(int idx, int pageSize, String condString) {
        long total = 0L;
        JSONArray array = null;
        if (idx <= 0) {
            return rMsg.netMSG(false, "页码错误");
        } else if (pageSize <= 0) {
            return rMsg.netMSG(false, "页长度错误");
        } else {
            new JSONArray();
            new JSONArray();
            if (10000 > this.userType && this.userType >= 1000) {
                this.content.eq("wbid", this.currentWeb);
            }

            if (!StringHelper.InvaildString(condString)) {
                return rMsg.netMSG(1, "无效条件");
            } else {
                JSONObject obj = this.model.buildCondOgid(condString);
                obj = this.getNextColumn(obj);
                if (obj != null && obj.size() > 0) {
                    JSONArray condArray = obj.getJsonArray("cond");
                    JSONArray condOgid = obj.getJsonArray("ogid");
                    if (condOgid != null && condOgid.size() > 0) {
                        this.content.or().where(condOgid);
                    }

                    if (condArray != null && condArray.size() > 0) {
                        this.content.and().where(condArray);
                    }
                }

                if (this.content.getCondCount() > 0) {
                    this.content.and().eq("isdelete", 0).eq("isvisble", 0);
                    array = this.content.dirty().desc("isTop").desc("time").desc("sort").page(idx, pageSize);
                    total = this.content.count();
                    array = this.model.setTemplate(array);
                    array = this.model.getImgs(this.model.getDefault(this.currentWeb, array));
                    array = this.model.ContentDencode(array);
                    array = this.FillFileToArray(array);
                }

                return rMsg.netPAGE(idx, pageSize, total, array);
            }
        }
    }

    private JSONObject FillFileToObj(JSONObject object) {
        if (object != null && object.size() > 0) {
            String attrid = this.getAttr(object);
            JSONObject obj = this.getFiles(attrid);
            if (obj != null && obj.size() > 0) {
                object.put("attrid", this.FillInfo(attrid, obj).toJSONString());
            } else {
                object.put("attrid", (new JSONArray()).toJSONString());
            }
        }

        return object;
    }

    private JSONArray FillFileToArray(JSONArray array) {
        if (array != null && array.size() > 0) {
            int l = array.size();
            String attrid = this.getAttrID(array);
            JSONObject obj = this.getFiles(attrid);

            for(int i = 0; i < l; ++i) {
                JSONArray tempArray = new JSONArray();
                JSONObject tempObj = (JSONObject)array.get(i);
                if (obj != null && obj.size() > 0) {
                    attrid = this.getAttr(tempObj);
                    tempArray = this.FillInfo(attrid, obj);
                }

                tempObj.put("attrid", tempArray.toJSONString());
                array.set(i, tempObj);
            }
        }

        return array;
    }

    private JSONObject getFiles(String attrid) {
        JSONObject object = null;
        if (attrid != null && !attrid.equals("") && !attrid.equals("null") && !attrid.equals("0")) {
            String fileInfo = (String)appsProxy.proxyCall("/GrapeFile/Files/getFileByID/" + attrid);
            object = JSONObject.toJSON(fileInfo);
        }

        return object;
    }

    public void iii() {
        System.out.println("111");
    }

    public String jian_cha_chong_fu(int time_interval, String ogids) {
        JSONArray jsonArray = new JSONArray();
        String[] ogidArr = ogids.split(",");
        String[] var8 = ogidArr;
        int var7 = ogidArr.length;

        for(int var6 = 0; var6 < var7; ++var6) {
            String ogid = var8[var6];
            JSONObject jian_cha_chong_fu1 = this.jian_cha_chong_fu1(time_interval, ogid, (String)null, (String)null);
            if (jian_cha_chong_fu1 != null && jian_cha_chong_fu1.size() > 0) {
                jsonArray.adds(jian_cha_chong_fu1);
            }
        }

        return jsonArray.toJSONString();
    }

    public String jian_cha_chong_fu(int time_interval, String ogids, String starttime, String endtime) {
        JSONArray jsonArray = new JSONArray();
        String[] ogidArr = ogids.split(",");
        String[] var10 = ogidArr;
        int var9 = ogidArr.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            String ogid = var10[var8];
            JSONObject jian_cha_chong_fu1 = this.jian_cha_chong_fu1(time_interval, ogid, starttime, endtime);
            if (jian_cha_chong_fu1 != null && jian_cha_chong_fu1.size() > 0) {
                jsonArray.adds(jian_cha_chong_fu1);
            }
        }

        return jsonArray.toJSONString();
    }

    public JSONObject jian_cha_chong_fu1(int time_interval, String ogid1, String starttime, String endtime) {
        dbFilter DB2 = new dbFilter();
        DB2.eq("isdelete", 0);
        if (StringHelper.InvaildString(starttime) && StringHelper.InvaildString(endtime)) {
            long starttime1 = Long.parseLong(starttime);
            long endtime1 = Long.parseLong(endtime);
            DB2.gte("time", starttime1).lte("time", endtime1);
        }

        JSONArray build2 = DB2.build();
        this.content.eq("ogid", ogid1).and().where(build2).field("mainName,ogid,url,time");
        HashMap<String, Long> hashMap = new HashMap();
        JSONObject jsonObject = new JSONObject();
        HashMap<String, HashSet<Long>> timeMap = new HashMap();
        JSONArray rArray = this.content.scan((array) -> {
            JSONArray _rArray = new JSONArray();
            Iterator var8 = array.iterator();

            while(true) {
                label35:
                while(true) {
                    JSONObject ob;
                    Long time;
                    String ogid;
                    String mainName;
                    Long put;
                    do {
                        if (!var8.hasNext()) {
                            return _rArray;
                        }

                        Object object = var8.next();
                        ob = (JSONObject)object;
                        time = ob.getLong("time");
                        ogid = ob.getString("ogid");
                        mainName = ob.getString("mainName");
                        put = (Long)hashMap.put(mainName, time);
                    } while(put == null);

                    HashSet<Long> hashSet = (HashSet)timeMap.get(mainName);
                    Long timeexist;
                    if (hashSet != null && hashSet.size() > 0) {
                        Iterator var20 = hashSet.iterator();

                        while(true) {
                            Long day;
                            do {
                                if (!var20.hasNext()) {
                                    continue label35;
                                }

                                timeexist = (Long)var20.next();
                                day = Math.abs(time - timeexist) / 86400000L;
                            } while(day > (long)time_interval && time_interval != -1);

                            String timeStamp2Datex = timeStamp2Date(String.valueOf(time), (String)null);
                            ob.puts("time", timeStamp2Datex);
                            String ognamex = this.contentGroup.getOgname_By_ogid(ogid);
                            ob.puts("ogname", ognamex);
                            jsonObject.put(mainName, ob);
                        }
                    } else {
                        timeexist = Math.abs(time - put) / 86400000L;
                        if (timeexist <= (long)time_interval || time_interval == -1) {
                            String timeStamp2Date = timeStamp2Date(String.valueOf(time), (String)null);
                            ob.puts("time", timeStamp2Date);
                            String ogname = this.contentGroup.getOgname_By_ogid(ogid);
                            ob.puts("ogname", ogname);
                            jsonObject.put(mainName, ob);
                        }

                        hashSet = new HashSet();
                        hashSet.add(time);
                        hashSet.add(put);
                        timeMap.put(mainName, hashSet);
                    }
                }
            }
        }, 30);
        this.content.clear();
        JSONObject jsonObject2 = new JSONObject();
        Iterator var13 = jsonObject.keySet().iterator();

        while(var13.hasNext()) {
            Object object = var13.next();
            JSONObject object2 = (JSONObject)jsonObject.get(object);
            String ogname = object2.getString("ogname");
            JSONArray jsonArray = jsonObject2.getJsonArray(ogname);
            if (jsonArray == null) {
                jsonArray = new JSONArray();
                jsonObject2.puts(ogname, jsonArray);
            }

            jsonArray.add(object2);
            jsonObject2.puts(ogname, jsonArray);
        }

        return jsonObject2;
    }

    public static String timeStamp2Date(String seconds, String format) {
        if (seconds != null && !seconds.isEmpty() && !seconds.equals("null")) {
            if (format == null || format.isEmpty()) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(new Date(Long.valueOf(seconds)));
        } else {
            return "";
        }
    }

    private JSONArray FillInfo(String attrid, JSONObject FileInfo) {
        JSONArray attrArray = new JSONArray();
        JSONObject object = null;
        if (attrid != null && !attrid.equals("") && !attrid.equals("null") && FileInfo != null && FileInfo.size() > 0) {
            String[] value = attrid.split(",");
            String[] var9 = value;
            int var8 = value.length;

            for(int var7 = 0; var7 < var8; ++var7) {
                String id = var9[var7];
                new JSONObject();
                object = JSONObject.toJSON(FileInfo.getString(id));
                if (object != null && object.size() > 0) {
                    attrArray.add(object);
                }
            }
        }

        return attrArray;
    }

    public String ShowArticle(int idx, int pageSize, String condString) {
        String ogids = "";
        long total = 0L;
        JSONArray array = null;
        dbFilter filter = new dbFilter();
        JSONArray webArray = null;
        JSONArray condArray = JSONArray.toJSONArray(condString);
        if (condArray != null && condArray.size() > 0) {
            JSONObject temp = this.findByColumnName(JSONArray.toJSONArray(condString));
            if (temp != null && temp.size() > 0) {
                JSONArray condarray;
                if (temp.containsKey("ogid")) {
                    ogids = temp.getString("ogid");
                    if (StringHelper.InvaildString(ogids)) {
                        condarray = this.model.getOrCondArray("ogid", ogids);
                        if (condarray == null || condarray.size() <= 0) {
                            return rMsg.netMSG(1, "无效条件");
                        }

                        this.content.or().where(condarray);
                    }
                } else {
                    String[] wbids = this.model.getWeb(this.currentWeb);
                    if (wbids != null) {
                        String[] var16 = wbids;
                        int var15 = wbids.length;

                        for(int var14 = 0; var14 < var15; ++var14) {
                            String string = var16[var14];
                            filter.eq("wbid", string);
                        }

                        webArray = filter.build();
                        if (webArray == null || webArray.size() <= 0) {
                            return rMsg.netMSG(1, "无效条件");
                        }

                        this.content.or().where(webArray);
                    }
                }

                if (temp.containsKey("condArray")) {
                    condarray = temp.getJsonArray("condArray");
                    if (condarray == null || condarray.size() <= 0) {
                        return rMsg.netMSG(1, "无效条件");
                    }

                    this.content.and().where(condarray);
                }
            }

            this.content.eq("isdelete", 0).eq("isvisble", 0);
            array = this.content.dirty().desc("_id").page(idx, pageSize);
            total = this.content.count();
            this.content.clear();
            array = this.model.setTemplate(array);
            array = this.model.ContentDencode(array);
            this.model.getImgs(this.model.getDefault(this.currentWeb, array));
            return rMsg.netPAGE(idx, pageSize, total, this.getColumnName(array));
        } else {
            return rMsg.netMSG(1, "无效条件");
        }
    }

    private JSONObject findByColumnName(JSONArray condArray) {
        JSONObject object = null;
        JSONObject temp = new JSONObject();
        JSONArray array = null;
        String value = null;
        if (condArray != null && condArray.size() != 0) {
            array = new JSONArray();
            Iterator var8 = condArray.iterator();

            while(var8.hasNext()) {
                Object object2 = var8.next();
                object = (JSONObject)object2;
                String key = object.getString("field");
                if (key.equals("columnName")) {
                    value = (new ContentGroup()).getOgid(object.getString("value"));
                    temp.put("ogid", value);
                } else {
                    array.add(object);
                }
            }

            temp.put("condArray", array);
        }

        return temp;
    }

    private JSONArray getColumnName(JSONArray array) {
        String ogid = "";
        if (array != null && array.size() > 0) {
            Iterator var7 = array.iterator();

            JSONObject object;
            String temp;
            while(var7.hasNext()) {
                Object obj = var7.next();
                object = (JSONObject)obj;
                temp = object.getString("ogid");
                if (temp != null && !temp.equals("") && !temp.equals("null")) {
                    ogid = ogid + temp + ",";
                }
            }

            if (ogid != null && !ogid.equals("") && !ogid.equals("null")) {
                ogid = StringHelper.fixString(ogid, ',');
            }

            temp = (new ContentGroup()).getColumnName(ogid);
            JSONObject objs = JSONObject.toJSON(temp);
            if (objs != null && objs.size() > 0) {
                int l = array.size();

                for(int i = 0; i < l; ++i) {
                    object = (JSONObject)array.get(i);
                    ogid = object.getString("ogid");
                    object.put("columnName", objs.getString(ogid));
                    array.set(i, object);
                }
            }
        }

        return array;
    }

    public String findSiteDesp(String id) {
        JSONObject object = new JSONObject();
        if (StringHelper.InvaildString(id) && (ObjectId.isValid(id) || checkHelper.isInt(id))) {
            object = this.content.eq("_id", id).field("_id,mainName,fatherid,ogid,time,content").find();
            object = this.model.ContentDencode(object);
        }

        return rMsg.netMSG(true, object);
    }

    public String SearchArticle(String wbid, int idx, int pageSize, String condString) {
        return this.search(wbid, idx, pageSize, condString, 1);
    }

    public String SearchArticleBack(int idx, int pageSize, String condString) {
        return this.search(this.currentWeb, idx, pageSize, condString, 2);
    }

    private String search(String wbid, int idx, int pageSize, String condString, int type) {
        String out = null;
        long total = 0L;
        JSONArray array = new JSONArray();
        new JSONArray();
        new JSONArray();
        JSONObject obj = this.model.buildCondOgid(condString);
        obj = this.getNextColumn(obj);
        if (obj != null && obj.size() > 0) {
            if (!StringHelper.InvaildString(wbid)) {
                rMsg.netMSG(1, "无效站点id");
            }

            JSONArray condArray = obj.getJsonArray("cond");
            JSONArray condOgid = obj.getJsonArray("ogid");
            if (condOgid != null && condOgid.size() > 0) {
                this.content.or().where(condOgid);
            }

            if (condArray != null && condArray.size() > 0) {
                this.content.and().where(condArray);
            }
        }

        if (this.content.getCondCount() > 0) {
            this.content.and().eq("isdelete", 0).eq("isvisble", 0).desc("time").desc("time").desc("sort").desc("_id");
            total = this.content.dirty().count();
            switch(type) {
            case 1:
                array = this.content.field("_id,mainName,time,wbid,ogid,content").page(idx, pageSize);
                break;
            case 2:
                array = this.content.page(idx, pageSize);
                array = this.model.setTemplate(array);
                array = this.model.getImgs(this.model.getDefault(wbid, array));
                array = this.model.ContentDencode(array);
                array = this.FillFileToArray(array);
            }

            out = rMsg.netPAGE(idx, pageSize, total, array);
        } else {
            out = rMsg.netMSG(false, "无效条件");
        }

        return out;
    }

    private JSONObject getNextColumn(JSONObject obj) {
        dbFilter filter = new dbFilter();
        new JSONArray();
        if (obj != null && obj.size() > 0) {
            JSONArray condOgid = obj.getJsonArray("ogid");
            if (condOgid != null && condOgid.size() > 0) {
                Iterator var8 = condOgid.iterator();

                label29:
                while(true) {
                    String value;
                    do {
                        if (!var8.hasNext()) {
                            break label29;
                        }

                        Object object = var8.next();
                        JSONObject tempobj = (JSONObject)object;
                        value = (new ContentGroup()).getAllColumn(tempobj.getString("value"));
                    } while(!StringHelper.InvaildString(value));

                    String[] ogids = value.split(",");
                    String[] var12 = ogids;
                    int var11 = ogids.length;

                    for(int var10 = 0; var10 < var11; ++var10) {
                        String string = var12[var10];
                        filter.eq("ogid", string);
                    }
                }
            }
        }

        obj.put("ogid", filter.build());
        return obj;
    }

    private void AddArticleClick(JSONObject object) {
        long clicktimes = 1L;
        if (object != null && object.size() > 0) {
            String oid = object.getMongoID("_id");
            if (object.containsKey("clickcount")) {
                clicktimes = Long.parseLong(object.getString("clickcount")) + 1L;
            }

            JSONObject obj = new JSONObject("clickcount", clicktimes);
            this.content.eq("_id", oid).data(obj).update();
        }

    }

    public String totalArticle(String rootID) {
        String rString = this.ch.get("total_COunt_" + rootID);
        if (rString == null) {
            distributedLocker countLocker = distributedLocker.newLocker("totalArticle_" + rootID);
            if (countLocker != null) {
                boolean flag = countLocker.lock();
                if (flag) {
                    CacheHelper ch = new CacheHelper();
                    if (rString == null || rString.equals("")) {
                        JSONObject json = new JSONObject();
                        JSONObject webInfo = JSONObject.toJSON((String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebInfo/s:" + rootID));
                        json = (new WsCount()).getAllCount(json, rootID, webInfo.getString(rootID), "");
                        rString = json.toJSONString();
                        ch.setget("total_COunt_" + rootID, rString, 86400L);
                    }

                    countLocker.unlock();
                }

                countLocker.releaseLocker();
            }
        }

        Ceshi.ceshi_write_N(rString, 5);
        return rString;
    }

    public void test1() {
        System.out.println("12");
        CacheHelper cacheHelper = new CacheHelper();
        String string = cacheHelper.get("ckdtest");
        System.out.println(string);
        cacheHelper.setget("ckdtest1", "陈凯迪", 30L);
    }

    public String total(String rootID, String starTime, String endTime) {
        String rString = this.ch.get("total_time_Count_" + rootID);
        if (rString == null) {
            distributedLocker countLocker = distributedLocker.newLocker("total_time_Count_" + rootID);
            if (countLocker != null) {
                if (countLocker.lock()) {
                    rootID = this.model.getRWbid(rootID);
                    if (rString == null || rString.equals("")) {
                        JSONObject json = new JSONObject();
                        JSONObject webInfo = JSONObject.toJSON(appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebInfo/s:" + rootID).toString());
                        json = (new WsCount()).getAllCount(json, rootID, webInfo.getString(rootID), "", Long.parseLong(starTime), Long.parseLong(endTime));
                        rString = json.toJSONString();
                        this.ch.setget("total_time_Count_" + rootID, rString, 86400L);
                    }

                    countLocker.unlock();
                }

                countLocker.releaseLocker();
            }
        }

        return rString;
    }

    public String totalColumn(String rootID, String starTime, String endTime) {
        String rString = this.ch.get("total_column_Count_" + rootID);
        if (rString == null) {
            distributedLocker countLocker = distributedLocker.newLocker("total_column_Count" + rootID);
            if (countLocker != null) {
                if (countLocker.lock()) {
                    if (rString == null || rString.equals("")) {
                        rootID = this.model.getRWbid(rootID);
                        JSONObject json = (new WsCount()).getChannleCount(rootID, Long.parseLong(starTime), Long.parseLong(endTime));
                        rString = json.toJSONString();
                        this.ch.setget("total_column_Count_" + rootID, rString, 86400L);
                    }

                    countLocker.unlock();
                }

                countLocker.releaseLocker();
            }
        }

        return rString;
    }

    public String getKeyArticle(String condString, int idx, int PageSize) {
        long total = 0L;
        JSONArray array = null;
        new JSONArray();
        new JSONArray();
        JSONObject obj = this.model.buildCondOgid(condString);
        if (obj != null && obj.size() > 0) {
            JSONArray condArray = obj.getJsonArray("cond");
            JSONArray condOgid = obj.getJsonArray("ogid");
            if (condOgid != null && condOgid.size() > 0) {
                this.content.or().where(condOgid);
            }

            if (condArray != null && condArray.size() > 0) {
                this.content.and().where(condArray);
            }
        }

        if (this.content.getCondCount() > 0) {
            this.content.desc("time").eq("slevel", 0).field("_id,mainName,ogid,time");
            array = this.content.dirty().page(idx, PageSize);
            total = this.content.count();
        }

        return rMsg.netPAGE(idx, PageSize, total, array != null && array.size() > 0 ? array : new JSONArray());
    }

    public String getHotArticle(String condString, int idx, int PageSize) {
        long total = 0L;
        JSONArray array = null;
        new JSONArray();
        new JSONArray();
        new JSONArray();
        if (!StringHelper.InvaildString(condString)) {
            return rMsg.netMSG(1, "无效条件");
        } else {
            JSONObject obj = this.buildCond(condString);
            if (obj != null && obj.size() > 0) {
                JSONArray condArray = obj.getJsonArray("cond");
                JSONArray condOgid = obj.getJsonArray("ogid");
                JSONArray condWeb = obj.getJsonArray("wbid");
                if (condWeb != null && condWeb.size() > 0) {
                    this.content.or().where(condWeb);
                }

                if (condOgid != null && condOgid.size() > 0) {
                    this.content.or().where(condOgid);
                }

                if (condArray != null && condArray.size() > 0) {
                    this.content.and().where(condArray);
                }
            }

            if (this.content.getCondCount() > 0) {
                this.content.and().eq("slevel", 0).desc("clickcount").desc("_id").field("_id,mainName,ogid,time");
                total = this.content.dirty().count();
                array = this.content.page(idx, PageSize);
            }

            return rMsg.netPAGE(idx, PageSize, total, array);
        }
    }

    private JSONObject buildCond(String condString) {
        JSONArray cond = JSONArray.toJSONArray(condString);
        JSONArray condWbid = new JSONArray();
        JSONObject obj = this.model.buildCondOgid(condString);
        String wbids = this.getAllWeb(cond);
        dbFilter filter = new dbFilter();
        if (StringHelper.InvaildString(wbids)) {
            String[] value = wbids.split(",");
            String[] var11 = value;
            int var10 = value.length;

            for(int var9 = 0; var9 < var10; ++var9) {
                String wbid = var11[var9];
                filter.eq("wbid", wbid);
            }

            condWbid = filter.build();
        }

        obj.put("wbid", condWbid);
        return obj;
    }

    private String getAllWeb(JSONArray condArray) {
        String wbids = "";
        if (condArray != null && condArray.size() != 0) {
            int l = condArray.size();

            for(int i = 0; i < l; ++i) {
                JSONObject condObj = (JSONObject)condArray.get(i);
                String field = condObj.getString("field");
                if (field.equals("wbid")) {
                    this.currentWeb = condObj.getString("value");
                    break;
                }
            }

            if (StringHelper.InvaildString(this.currentWeb)) {
                wbids = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/" + this.currentWeb).toString();
            }
        }

        return wbids;
    }

    public String ReviewPass(String oid) {
        return !StringHelper.InvaildString(oid) ? rMsg.netMSG(false, "非法数据") : this.Review(oid, 2);
    }

    public String ReviewNotPass(String oid) {
        return !StringHelper.InvaildString(oid) ? rMsg.netMSG(false, "非法数据") : this.Review(oid, 1);
    }

    public String ReviewCancle(String oid) {
        return !StringHelper.InvaildString(oid) ? rMsg.netMSG(false, "非法数据") : this.Review(oid, 0);
    }

    private String Review(String oid, int NewState) {
        String result = rMsg.netMSG(false, "");
        if (this.userInfo != null && this.userInfo.size() > 0) {
            String isreview = this.userInfo.containsKey("isreview") ? this.userInfo.getString("isreview") : "0";
            if (isreview.equals("0")) {
                return rMsg.netMSG(false, "您暂时还不能审核文章哦");
            } else {
                String[] value = oid.split(",");
                JSONObject object = new JSONObject();
                object.put("state", NewState);
                int l = value.length;
                if (l <= 0) {
                    return result;
                } else {
                    String[] var11 = value;
                    int var10 = value.length;

                    for(int var9 = 0; var9 < var10; ++var9) {
                        String id = var11[var9];
                        this.content.or().eq("_id", id);
                    }

                    return this.content.data(object).updateAll() > 0L ? rMsg.netMSG(true, "") : result;
                }
            }
        } else {
            return result;
        }
    }

    public String Fabulous(String oid) {
        if (oid != null && !oid.equals("") && !oid.equals("null")) {
            int code = this.MessageArticle(oid, "fabulous") ? 0 : 99;
            return code == 0 ? rMsg.netMSG(0, "点赞成功") : rMsg.netMSG(99, "点赞失败");
        } else {
            return rMsg.netMSG(100, "参数异常");
        }
    }

    public String Booing(String oid) {
        if (oid != null && !oid.equals("") && !oid.equals("null")) {
            int code = this.MessageArticle(oid, "booing") ? 0 : 99;
            return code == 0 ? rMsg.netMSG(0, "成功") : rMsg.netMSG(99, "失败");
        } else {
            return rMsg.netMSG(100, "参数异常");
        }
    }

    private boolean MessageArticle(String oid, String field) {
        String temp = "1";
        JSONObject object = null;
        JSONObject obj = null;

        try {
            object = this.content.eq("_id", oid).field("_id,fabulous,booing").find();
            if (object != null && object.size() > 0) {
                if (object.containsKey(field)) {
                    temp = object.getString(field);
                    if (temp.contains("$numberLong")) {
                        temp = JSONObject.toJSON(temp).getString("$numberLong");
                    }
                }

                int fabu = Integer.parseInt(temp) + 1;
                obj = new JSONObject(field, fabu);
                obj = this.content.eq("_id", oid).data(obj).update();
            }
        } catch (Exception var7) {
            nlogger.logout(var7);
            obj = null;
        }

        return obj != null;
    }

    public String AddSuffix(String oid) {
        if (oid != null && !oid.equals("") && !oid.equals("null")) {
            int code = this.SetSuffix(oid, 1) ? 0 : 99;
            return code == 0 ? rMsg.netMSG(0, "成功") : rMsg.netMSG(99, "失败");
        } else {
            return rMsg.netMSG(100, "参数异常");
        }
    }

    public String RemoveSuffix(String oid) {
        if (oid != null && !oid.equals("") && !oid.equals("null")) {
            int code = this.SetSuffix(oid, 0) ? 0 : 99;
            return code == 0 ? rMsg.netMSG(0, "成功") : rMsg.netMSG(99, "失败");
        } else {
            return rMsg.netMSG(100, "参数异常");
        }
    }

    private boolean SetSuffix(String oid, int isSuffix) {
        JSONObject object = new JSONObject("isSuffix", isSuffix);
        object = this.content.eq("_id", oid).data(object).update();
        return object != null;
    }

    public String AddAppend(String id, String info) {
        int code = 99;
        String result = rMsg.netMSG(100, "追加文档失败");
        String contents = "";
        JSONObject object = this.content.eq("_id", id).find();
        JSONObject obj = JSONObject.toJSON(info);
        if (obj != null && obj.size() != 0) {
            if (obj.containsKey("content")) {
                contents = obj.getString("content");
                contents = codec.DecodeHtmlTag(contents);
                contents = codec.decodebase64(contents);
                obj.escapeHtmlPut("content", contents);
                String oldcontent = object.getString("content");
                oldcontent = oldcontent + obj.getString("content");
                obj.put("content", oldcontent);
            }

            code = this.content.eq("_id", id).data(obj).update() != null ? 0 : 99;
        }

        return code == 0 ? rMsg.netMSG(0, "追加文档成功") : result;
    }

    public String FindWechatArticle(String ids) {
        JSONArray array = null;
        String[] value = null;
        if (ids != null && !ids.equals("") && !ids.equals("null")) {
            value = ids.split(",");
        }

        if (value != null) {
            this.content.or();
            String[] var7 = value;
            int var6 = value.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                String string = var7[var5];
                this.content.eq("_id", string);
            }

            array = this.content.field("_id,mainName,author,content,desp,image,wbid").limit(8).select();
        }

        if (array != null && array.size() > 0) {
            JSONObject object = (JSONObject)array.get(0);
            String wbid = object.getString("wbid");
            array = this.model.ContentDencode(array);
            array = this.model.getDefaultImage(wbid, array);
            array = this.model.getImgs(array);
        }

        return array != null && array.size() > 0 ? array.toString() : (new JSONArray()).toJSONString();
    }

    private String[] getAllContent(String wbid) {
        String wbids = "";
        String[] value = null;
        if (wbid != null && !wbid.equals("")) {
            wbids = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/" + this.model.getRWbid(wbid)).toString();
            if (wbids != null && !wbids.equals("")) {
                value = wbids.split(",");
            }
        }

        return value;
    }

    private String getSingleArticle(JSONObject obj, String id) {
        String ogid = "";
        if (obj != null && obj.size() != 0) {
            if (!obj.containsKey("clickcount")) {
                obj.put("clickcount", 0);
            }

            String wbid = obj.getString("wbid");
            ogid = obj.getString("ogid");
            JSONObject preobj = this.content.asc("_id").eq("wbid", wbid).eq("ogid", ogid).eq("slevel", 0).eq("isdelete", 0).eq("isvisble", 0).gt("_id", id).field("_id,mainName").limit(1).find();
            JSONObject nextobj = this.content.desc("_id").eq("wbid", wbid).eq("ogid", ogid).eq("slevel", 0).eq("isdelete", 0).eq("isvisble", 0).lt("_id", id).limit(1).field("_id,mainName").find();
            if (preobj != null && preobj.size() != 0) {
                obj.put("previd", preobj.getMongoID("_id"));
                obj.put("prevname", preobj.get("mainName"));
            }

            if (nextobj != null && nextobj.size() != 0) {
                obj.put("nextid", nextobj.getMongoID("_id"));
                obj.put("nextname", nextobj.get("mainName"));
            }

            this.AddArticleClick(obj);
            (new ContentRecord()).AddReader(id);
        }

        obj = this.model.ContentDencode(obj);
        obj = this.model.getImgs(this.model.getDefault(obj));
        obj = this.FillFileToObj(obj);
        return rMsg.netMSG(true, obj);
    }

    private int isShow(JSONObject object) {
        int code = -1;
        String currentId = "";
        String wbid = "";
        if (object != null && object.size() != 0) {
            String ogid = object.getString("ogid");
            wbid = object.getString("wbid");
            String temp = (new ContentGroup()).isPublic(ogid);
            if (!temp.equals("0")) {
                if (this.userInfo != null && this.userInfo.size() != 0) {
                    currentId = this.getCurrentId();
                    wbid = this.model.getRWbid(wbid);
                    if (!currentId.contains(wbid)) {
                        code = 1;
                    } else {
                        code = 0;
                    }
                } else {
                    code = 2;
                }
            } else {
                code = 0;
            }
        }

        return code;
    }

    private String getCurrentId() {
        String Currentwbid = "";
        if (this.userInfo != null && this.userInfo.size() != 0) {
            JSONArray array = JSONArray.toJSONArray(this.userInfo.getString("webinfo"));
            String wbid;
            if (array != null && array.size() != 0) {
                for(Iterator var6 = array.iterator(); var6.hasNext(); Currentwbid = Currentwbid + wbid + ",") {
                    Object object2 = var6.next();
                    JSONObject object = (JSONObject)object2;
                    wbid = object.getString("wbid");
                }
            }
        }

        return StringHelper.fixString(Currentwbid, ',');
    }

    private synchronized JSONObject findOid(String oid) {
        JSONObject object = this.content.eq("_id", oid).find();
        object = this.model.ContentDencode(object);
        object = this.model.getImgs(object);
        object = this.model.getDefault(object);
        object = this.FillFileToObj(object);
        return object;
    }

    private String getAttrID(JSONArray array) {
        String attrid = "";
        if (array != null && array.size() > 0) {
            Iterator var6 = array.iterator();

            while(var6.hasNext()) {
                Object obj = var6.next();
                JSONObject object = (JSONObject)obj;
                String temp = this.getAttr(object);
                if (temp != null && !temp.equals("") && !temp.equals("null") && !temp.equals("0")) {
                    attrid = attrid + this.getAttr(object) + ",";
                }
            }
        }

        return StringHelper.fixString(attrid, ',');
    }

    private String getAttr(JSONObject object) {
        String attrid = "";
        if (object != null && object.size() > 0 && object.containsKey("attrid")) {
            attrid = object.getString("attrid");
            if (attrid.contains("numberLong")) {
                attrid = JSONObject.toJSON(attrid).getString("numberLong");
            }
        }

        return attrid;
    }

    private String checkparam(JSONObject contentInfo) {
        String mainName = "";
        String contents = "";
        if (contentInfo != null && contentInfo.size() > 0) {
            contentInfo = this.model.ContentEncode(contentInfo);
            if (contentInfo.containsKey("mainName")) {
                mainName = contentInfo.getString("mainName");
                if (mainName.equals("")) {
                    return rMsg.netMSG(2, "请填写正确的文章标题");
                }
            }

            if (contentInfo.containsKey("fatherid")) {
                String fatherid = contentInfo.getString("fatherid");
                if (StringHelper.InvaildString(fatherid) && !fatherid.equals("0")) {
                    contentInfo.remove("ogid");
                }
            }

            if (contentInfo.containsKey("_id")) {
                contentInfo.remove("_id");
            }

            return contentInfo.toJSONString();
        } else {
            return rMsg.netMSG(100, "无效数据");
        }
    }

    private String getNetImage(String contents) {
        String path = "";

        try {
            new PhantomJS();
        } catch (Exception var4) {
            nlogger.logout(var4);
            path = "";
        }

        return path;
    }

    public String getArticleInfo(int idx, int pageSize) {
        JSONArray array = null;
        long total = 0L;
        JSONArray condArray = this.getCondString();
        if (condArray != null && condArray.size() > 0) {
            this.content.or().where(condArray);
            total = this.content.and().eq("isdelete", 0).eq("isvisble", 0).dirty().count();
            array = this.content.page(idx, pageSize);
        }

        array = this.model.ContentDencode(array);
        array = this.model.getImgs(array);
        return rMsg.netPAGE(idx, pageSize, total, array);
    }

    public String checkAllArticle(String perfix) {
        String _eventName = StringHelper.numUUID() + "_" + StringHelper.shortUUID();
        JSONArray condArray = this.getCondString();
        boolean rb = false;
        if (condArray != null && condArray.size() > 0) {
            GrapeTreeDBModel content = this.getDB();
            appIns context = appsProxy.getCurrentAppInfo();
            (new Thread(() -> {
                appsProxy.setCurrentAppInfo(context);
                CacheHelper cache = new CacheHelper();
                JSONArray rArray = content.or().where(condArray).and().eq("state", 2).eq("isdelete", 0).eq("isvisble", 0).scan((_array) -> {
                    String perfixs = codec.DecodeFastJSON(perfix);
                    JSONArray array = this.model.ContentDencode(_array);
                    JSONArray _rArray = new JSONArray();
                    JSONObject json;
                    if (array != null && array.size() > 0) {
                        for(Iterator var11 = array.iterator(); var11.hasNext(); cache.setget(_eventName, rMsg.netMSG(false, perfixs + json.getString("_id")), 3600L)) {
                            Object object = var11.next();
                            json = (JSONObject)object;
                            System.out.println(json.getString("_id"));
                            privacyPolicy pp = new privacyPolicy();
                            String string = pp.scanText(Jsoup.parse(json.getString("content")).text());
                            System.out.println("....ok");
                            if (string != null && pp.hasPrivacyPolicy()) {
                                JSONObject rJson = new JSONObject();
                                rJson.put("_id", perfixs + json.getString("_id"));
                                rJson.put("title", json.get("mainName"));
                                _rArray.add(rJson);
                            }
                        }
                    }

                    return _rArray;
                }, 500);
                cache.setget(_eventName, rMsg.netMSG(true, rArray), 3600L);
            })).start();
            rb = true;
        }

        return rMsg.netMSG(true, rb ? _eventName : "");
    }

    public String getEventProgress(String eventName) {
        CacheHelper cache = new CacheHelper();
        System.out.println(cache.get(eventName));
        JSONObject rJson = JSONObject.toJSON(cache.get(eventName));
        String content = "";
        if (rJson != null && rJson.containsKey("errorcode") && rJson.getInt("errorcode") == 1) {
            content = rJson.getString("message");
        }

        return rMsg.netMSG(true, content);
    }

    public Object getEventReport(String eventName, String file) {
        CacheHelper cache = new CacheHelper();
        JSONObject rJson = JSONObject.toJSON(cache.get(eventName));
        String content = "";
        if (rJson != null && rJson.containsKey("errorcode") && rJson.getInt("errorcode") == 0) {
            content = rJson.getString("message");
            if (content != null) {
                try {
                    return excelHelper.out(content);
                } catch (IOException var7) {
                    nlogger.login(content);
                    nlogger.logout(var7, "导出异常");
                    var7.printStackTrace();
                }
            }
        }

        return rMsg.netMSG(false, "error");
    }

    private JSONArray getCondString() {
        JSONArray condArray = null;
        String[] value = null;
        dbFilter filter = new dbFilter();
        String wbids = this.getCurrentId();
        if (StringHelper.InvaildString(wbids)) {
            value = wbids.split(",");
            if (value != null) {
                String[] var8 = value;
                int var7 = value.length;

                for(int var6 = 0; var6 < var7; ++var6) {
                    String wbid = var8[var6];
                    if (StringHelper.InvaildString(wbid) && (ObjectId.isValid(wbid) || checkHelper.isInt(wbid))) {
                        filter.eq("wbid", wbid);
                    }
                }
            }

            condArray = filter.build();
        }

        return condArray;
    }
    
    
    /**
     * added by wuren
     * @param ogid		栏目ID
     * @param mainName	标题名称
     * @return _id		如果文章已存在则返回主键ID，取第一条标题相同的文章ID
     */
    public JSONObject CrawlerContentIsExist(String ogid, String mainName){
    	JSONObject result = new JSONObject();
    	result.put("_id", "0");
    	
    	JSONArray object = null;
    	this.content.eq("ogid", ogid).eq("mainName", mainName).eq("isdelete", 0);
    	object = this.content.select();
        if (object != null && object.size() > 0) {
        	Iterator var9 = object.iterator();
            while(var9.hasNext()) {
            	Object object2 = var9.next();
            	result = (JSONObject)object2;
                result.put("_id", result.getString(this.pkString));
                break;
            }
        }
    	return result;
    }
    
    protected String crawlerUpdate(JSONObject object) {
    	String result = rMsg.netMSG(100, "文章更新失败");
    	object.put("editTime", TimeHelper.nowMillis());
        Object temp = content.eq("_id", (String)object.get("_id")).data(object).updateEx();
        result = temp != null ? rMsg.netMSG(0, "文章更新成功") : result;
        
        return result;
    }
    
    protected String crawlerInsert(JSONObject contentInfo) {
        String info = null;
        int state = 0;
        JSONObject ro = null;
        String result = rMsg.netMSG(100, "新增文章失败");

        try {
            info = this.checkparam(contentInfo);
            JSONObject object = JSONObject.toJSON(info);
            if (object != null && object.size() > 0) {
                if (object.containsKey("errorcode")) {
                    return info;
                }

                state = object.getInt("state");
                info = this.content.data(object).autoComplete().insertOnce().toString();
                ro = this.findOid(info);
                result = ro != null && ro.size() > 0 ? rMsg.netMSG(0, ro) : result;
            }
        } catch (Exception var7) {
            nlogger.logout(var7);
            result = rMsg.netMSG(100, "新增文章失败");
        }

        return result;
    }
    
}
