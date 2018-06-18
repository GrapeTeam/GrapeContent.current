package Model;

import common.java.apps.appsProxy;
import common.java.database.db;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.string.StringHelper;
import interfaceApplication.ContentGroup;
import java.util.Collections;
import java.util.Comparator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WsCount
{
  private GrapeTreeDBModel content;
  private GrapeDBSpecField gDbSpecField;
  private ContentGroup group;
  private CommonModel model;
  
  public WsCount()
  {
    this.group = new ContentGroup();
    this.model = new CommonModel();
    
    this.content = new GrapeTreeDBModel();
    this.gDbSpecField = new GrapeDBSpecField();
    this.gDbSpecField.importDescription(appsProxy.tableConfig("Content"));
    this.content.descriptionModel(this.gDbSpecField);
    this.content.bindApp();
  }
  
  public JSONObject getChannleCount(String wbID, long startUT, long endUT)
  {
    JSONArray channelArray = JSONArray.toJSONArray(this.group.getPrevColumn(wbID));
    
    JSONArray newTree = rootTree(channelArray, "_id", "fatherid");
    appendInfo2Tree(newTree, "_id", "children", wbID, startUT, endUT);
    return new JSONObject(wbID, newTree);
  }
  
  private void appendInfo2Tree(JSONArray array, String mid, String childid, String wid, long startUT, long endUT)
  {
    for (Object _obj : array)
    {
      JSONObject json = (JSONObject)_obj;
      if ((json.containsKey(childid)) && (!json.get(childid).toString().isEmpty()))
      {
        String _id = ((JSONObject)json.get(mid)).getString("$oid");
        JSONArray childArray = (JSONArray)json.get(childid);
        appendInfo2Tree(childArray, mid, childid, wid, startUT, endUT);
        
        long allCnt = getChannelAllCount(wid, _id, startUT, endUT);
        long argCnt = getChannelAgreeCount(wid, _id, startUT, endUT);
        long disArg = getChannelDisagreeCount(wid, _id, startUT, endUT);
        long chking = allCnt - argCnt - disArg;
        for (Object childObj : childArray)
        {
          JSONObject childJson = (JSONObject)childObj;
          allCnt += childJson.getLong("count");
          argCnt += childJson.getLong("checked");
          disArg += childJson.getLong("uncheck");
          chking += childJson.getLong("checking");
        }
        json.put("count", Long.valueOf(allCnt));
        json.put("checked", Long.valueOf(argCnt));
        json.put("uncheck", Long.valueOf(disArg));
        json.put("checking", Long.valueOf(chking));
      }
    }
  }
  
  private JSONArray rootTree(JSONArray array, String mid, String fid)
  {
    JSONArray newArray = new JSONArray();
    for (Object _obj : array)
    {
      JSONObject json = (JSONObject)_obj;
      if (json.get(fid).toString().equals("0"))
      {
        JSONArray childArray = line2tree(((JSONObject)json.get(mid)).getString("$oid"), array, mid, fid);
        
        json.put("children", childArray);
        
        newArray.add(json);
      }
    }
    return newArray;
  }
  
  private JSONArray line2tree(Object rootID, JSONArray array, String mid, String fid)
  {
    JSONArray newArray = new JSONArray();
    for (Object _obj : array)
    {
      JSONObject json = (JSONObject)_obj;
      if (json.get(fid).equals(rootID))
      {
        JSONArray childArray = line2tree(((JSONObject)json.get(mid)).getString("$oid"), array, mid, fid);
        
        json.put("children", childArray);
        
        newArray.add(json);
      }
    }
    return newArray;
  }
  
  public JSONObject getAllCount(JSONObject robj, String rootID, String rootName, String fatherID)
  {
    return getAllCount(robj, rootID, rootName, fatherID, 0L, 0L);
  }
  
  public JSONObject getAllCount(JSONObject robj, String rootID, String rootName, String fatherID, long startUT, long endUT)
  {
    String[] trees = null;
    JSONObject nObj = new JSONObject();
    
    rootID = this.model.getRWbid(rootID);
    
    long click = getClick_ckd(rootID, startUT, endUT);
    long allCnt = getCount(rootID, startUT, endUT);
    long argCnt = getAgreeCount(rootID, startUT, endUT);
    long disArg = getDisagreeCount(rootID, startUT, endUT);
    long chking = allCnt - argCnt - disArg;
    nObj.put("id", rootID);
    nObj.put("fatherid", fatherID);
    nObj.put("name", rootName);
    String tree = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getChildrenweb/s:" + rootID);
    if (!tree.equals("")) {
      trees = tree.split(",");
    }
    JSONObject newJSON = new JSONObject();
    if (trees != null)
    {
      JSONObject webInfos = 
        JSONObject.toJSON((String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebInfo/s:" + tree));
      int l = trees.length;
      for (int i = 0; i < l; i++) {
        getAllCount(newJSON, trees[i], webInfos.getString(trees[i]), rootID, startUT, endUT);
      }
    }
    JSONArray jsonArray = new JSONArray();
    for (Object obj : newJSON.keySet()) {
      try
      {
        JSONObject json = (JSONObject)newJSON.get(obj);
        
        allCnt += json.getLong("count");
        argCnt += json.getLong("checked");
        disArg += json.getLong("uncheck");
        chking += json.getLong("checking");
        click += json.getLong("clickcount");
        
        jsonArray.add(json);
      }
      catch (Exception e)
      {
        e.getMessage();
      }
    }
    sortJson_ckd(jsonArray, "count");
    nObj.put("count", Long.valueOf(allCnt));
    nObj.put("checked", Long.valueOf(argCnt));
    nObj.put("uncheck", Long.valueOf(disArg));
    nObj.put("checking", Long.valueOf(chking));
    nObj.put("clickcount", Long.valueOf(click));
    
    nObj.put("children", jsonArray);
    
    robj.put(rootID, nObj);
    return robj;
  }
  
  public void sortJson_ckd(JSONArray jsonArray, final String key)
  {
    Collections.sort(jsonArray, new Comparator()
    {
      public int compare(JSONObject o1, JSONObject o2)
      {
        long long1 = o1.getLong(key);
        long long2 = o2.getLong(key);
        if (long1 > long2) {
          return -1;
        }
        if (long1 < long2) {
          return 1;
        }
        return 0;
      }

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return 0;
	}
    });
  }
  
  public static int partition(JSONArray array, int lo, int hi, String keyName)
  {
    JSONObject json = (JSONObject)array.get(lo);
    long key = json.getLong(keyName);
    while (lo < hi)
    {
      while ((((JSONObject)array.get(hi)).getLong(keyName) <= key) && (hi > lo)) {
        hi--;
      }
      array.set(lo, array.get(hi));
      while ((((JSONObject)array.get(lo)).getLong(keyName) >= key) && (hi > lo)) {
        lo++;
      }
      array.set(hi, array.get(lo));
    }
    array.set(hi, json);
    return hi;
  }
  
  public static void sortJson(JSONArray array, int lo, int hi, String keyName)
  {
    if (lo >= hi) {
      return;
    }
    int index = partition(array, lo, hi, keyName);
    sortJson(array, lo, index - 1, keyName);
    sortJson(array, index + 1, hi, keyName);
  }
  
  private long getChannelAllCount(String wid, String cid)
  {
    return 0L;
  }
  
  private long getChannelAllCount(String wid, String cid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.eq("wbid", wid).eq("ogid", cid).count();
    }
    return count;
  }
  
  private long getChannelAgreeCount(String wid)
  {
    return 0L;
  }
  
  private long getChannelAgreeCount(String wid, String cid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.and().eq("wbid", wid).eq("ogid", cid).eq("state", Integer.valueOf(2)).count();
    }
    return count;
  }
  
  private long getChannelDisagreeCount(String wid)
  {
    return 0L;
  }
  
  private long getChannelDisagreeCount(String wid, String cid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.and().eq("wbid", wid).eq("ogid", cid).eq("state", Integer.valueOf(1)).count();
    }
    return count;
  }
  
  private long getCount(String wid)
  {
    return getCount(wid, 0L, 0L);
  }
  
  private long getCount(String wid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.eq("wbid", wid).eq("isdelete", Integer.valueOf(0)).eq("isvisble", Integer.valueOf(0)).count();
    }
    return count;
  }
  
  private long getAgreeCount(String wid)
  {
    return getAgreeCount(wid, 0L, 0L);
  }
  
  private long getAgreeCount(String wid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.and().eq("wbid", wid).eq("state", Integer.valueOf(2)).eq("isdelete", Integer.valueOf(0)).eq("isvisble", Integer.valueOf(0)).count();
    }
    return count;
  }
  
  private long getDisagreeCount(String wid)
  {
    return getDisagreeCount(wid, 0L, 0L);
  }
  
  private long getDisagreeCount(String wid, long startUT, long endUT)
  {
    long count = 0L;
    if (wid != null)
    {
      if (startUT > 0L) {
        this.content.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        this.content.and().lte("time", Long.valueOf(endUT));
      }
      count = this.content.and().eq("wbid", wid).eq("state", Integer.valueOf(1)).eq("isdelete", Integer.valueOf(0)).eq("isvisble", Integer.valueOf(0)).count();
    }
    return count;
  }
  
  private long getClick_ckd(String wid, long startUT, long endUT)
  {
    long count = 0L;long temp = 0L;
    
    JSONArray array = null;
    if (wid != null)
    {
      db db = this.content.bind().eq("isdelete", Integer.valueOf(0)).eq("isvisble", Integer.valueOf(0));
      if (startUT > 0L) {
        db.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        db.and().lte("time", Long.valueOf(endUT));
      }
      array = db.and().eq("wbid", wid).field("clickcount,readCount").select();
    }
    if ((array != null) && (array.size() > 0)) {
      for (Object obj : array)
      {
        JSONObject object = (JSONObject)obj;
        temp = object.getLong("clickcount");
        count += temp;
      }
    }
    return count;
  }
  
  private long getClick(String wid, long startUT, long endUT)
  {
    int count = 0;int temp = 0;
    String tempString = "0";
    JSONArray array = null;
    if (wid != null)
    {
      db db = this.content.bind().eq("isdelete", Integer.valueOf(0)).eq("isvisble", Integer.valueOf(0));
      if (startUT > 0L) {
        db.and().gte("time", Long.valueOf(startUT));
      }
      if (endUT > 0L) {
        db.and().lte("time", Long.valueOf(endUT));
      }
      array = db.and().eq("wbid", wid).field("clickcount,readCount").select();
    }
    if ((array != null) && (array.size() > 0)) {
      for (Object obj : array)
      {
        JSONObject object = (JSONObject)obj;
        tempString = object.getString("clickcount");
        tempString = !StringHelper.InvaildString(tempString) ? "0" : tempString;
        if (tempString.contains("$numberLong")) {
          tempString = JSONObject.toJSON(tempString).getString("$numberLong");
        }
        temp = Integer.parseInt(tempString);
        count += temp;
      }
    }
    return count;
  }
  
  private String[] getCid(String wid)
  {
    String[] trees = null;
    if ((wid != null) && (!wid.equals("")))
    {
      wid = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/s:" + wid);
      if (!wid.equals("")) {
        trees = wid.split(",");
      }
    }
    return trees;
  }
}
