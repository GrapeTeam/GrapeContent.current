package Model;

import common.java.apps.appsProxy;
import common.java.cache.CacheHelper;
import common.java.check.checkHelper;
import common.java.database.dbFilter;
import common.java.httpClient.request;
import common.java.json.JSONHelper;
import common.java.nlogger.nlogger;
import common.java.security.codec;
import common.java.session.session;
import common.java.string.StringHelper;
import interfaceApplication.ContentGroup;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CommonModel
{
  private String APIHost = "";
  private String APIAppid = "";
  private String appid = appsProxy.appidString();
  private session se;
  private JSONObject userInfo = null;
  private String userID = null;
  private String userName = null;
  private final Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>", 2);
  
  public CommonModel()
  {
    this.se = new session();
    this.userInfo = this.se.getDatas();
    if ((this.userInfo != null) && (this.userInfo.size() > 0))
    {
      this.userID = this.userInfo.getString("id");
      this.userName = this.userInfo.getString("name");
    }
  }
  
  public JSONArray getOrCond(String pkString, String ids)
  {
    String[] value = null;
    dbFilter filter = new dbFilter();
    if (StringHelper.InvaildString(ids))
    {
      value = ids.split(",");
      String[] arrayOfString1;
      int j = (arrayOfString1 = value).length;
      for (int i = 0; i < j; i++)
      {
        String id = arrayOfString1[i];
        if ((StringHelper.InvaildString(id)) && ((ObjectId.isValid(id)) || (checkHelper.isInt(id)))) {
          filter.eq(pkString, id);
        }
      }
    }
    return filter.build();
  }
  
  public JSONArray getOrCondArray(String key, String ids)
  {
    String[] value = null;
    dbFilter filter = new dbFilter();
    if (StringHelper.InvaildString(ids))
    {
      value = ids.split(",");
      String[] arrayOfString1;
      int j = (arrayOfString1 = value).length;
      for (int i = 0; i < j; i++)
      {
        String id = arrayOfString1[i];
        if (StringHelper.InvaildString(id)) {
          filter.eq(key, value);
        }
      }
    }
    return filter.build();
  }
  
  public JSONArray setTemplate(JSONArray array)
  {
    long properties = 0L;
    String list = "";String content = "";String columnName = "";
    array = ContentDencode(array);
    if ((array != null) && (array.size() > 0))
    {
      JSONObject tempObj = getTemplate(array);
      if ((tempObj != null) && (tempObj.size() > 0)) {
        for (int i = 0; i < array.size(); i++)
        {
          JSONObject object = (JSONObject)array.get(i);
          String value = object.getString("ogid");
          if ((tempObj != null) && (tempObj.size() != 0))
          {
            String temp = tempObj.getString(value);
            if (StringHelper.InvaildString(temp))
            {
              String[] values = temp.split(",");
              content = values[0];
              list = values[1];
              properties = Long.parseLong(values[2]);
              columnName = values[3];
            }
          }
          object.put("TemplateContent", content);
          object.put("Templatelist", list);
          object.put("ColumnProperty", Long.valueOf(properties));
          object.put("ColumnName", columnName);
          array.set(i, object);
        }
      }
    }
    return array;
  }
  
  private JSONObject getTemplate(JSONArray array)
  {
    String id = "";
    
    long properties = 0L;
    String TemplateContent = "";
    String Templatelist = "";String columnName = "";
    JSONObject tempObj = new JSONObject();
    if ((array != null) && (array.size() >= 0)) {
      for (Object obj : array)
      {
        JSONObject tempobj = (JSONObject)obj;
        String temp = tempobj.getString("ogid");
        if (!id.contains(temp)) {
          id = id + temp + ",";
        }
      }
    }
    if (StringHelper.InvaildString(id))
    {
      id = StringHelper.fixString(id, ',');
      String column = new ContentGroup().getGroupById(id);
      array = JSONArray.toJSONArray(column);
    }
    if ((array != null) && (array.size() != 0))
    {
      int l = array.size();
      for (int i = 0; i < l; i++)
      {
        JSONObject object = (JSONObject)array.get(i);
        if ((object != null) && (object.size() != 0))
        {
          if (object.containsKey("TemplateContent")) {
            TemplateContent = object.getString("TemplateContent");
          }
          if (object.containsKey("TemplateList")) {
            Templatelist = object.getString("TemplateList");
          }
          if (object.containsKey("ColumnProperty")) {
            properties = object.getLong("ColumnProperty");
          }
          if (object.containsKey("name")) {
            columnName = object.getString("name");
          }
          String tid = object.getString("_id");
          tempObj.put(tid, TemplateContent + "," + Templatelist + "," + properties + "," + columnName);
        }
      }
    }
    return tempObj;
  }
  
  public void setKafka(String id, int mode, int newstate)
  {
    this.APIHost = getconfig("APIHost");
    if ((!this.APIHost.equals("")) && (!this.APIAppid.equals(""))) {
      request.Get(this.APIHost + "/sendServer/ShowInfo/getKafkaData/" + id + "/" + this.appid + "/int:1/int:" + mode + "/int:" + newstate);
    }
  }
  
  public void AddLog(int type, String obj, String func, String condString)
  {
    String action = "";
    String columnName = getColumnName(obj);
    switch (type)
    {
    case 0: 
      action = "����[" + columnName + "]����";
      break;
    case 1: 
      action = "����[" + columnName + "]����," + condString;
      break;
    case 2: 
      action = "����[" + columnName + "]����," + condString;
      break;
    case 3: 
      break;
    case 4: 
      break;
    case 5: 
      break;
    }
    appsProxy.proxyCall("/GrapeLog/Logs/AddLogs/" + this.userID + "/" + this.userName + "/" + action + "/" + func, appsProxy.getCurrentAppInfo());
  }
  
  private String getColumnName(String ogid)
  {
    String columnName = ogid;
    if ((StringHelper.InvaildString(ogid)) && (!ogid.equals("0")) && ((ObjectId.isValid(ogid)) || (checkHelper.isInt(ogid))))
    {
      JSONObject temp = JSONObject.toJSON(new ContentGroup().getColumnName(ogid));
      if ((temp != null) && (temp.size() > 0)) {
        columnName = temp.getString(ogid);
      } else {
        columnName = "";
      }
    }
    return columnName;
  }
  
  public String getconfig(String key)
  {
    String value = "";
    try
    {
      JSONObject object = JSONObject.toJSON(appsProxy.configValue().getString("other"));
      if ((object != null) && (object.size() > 0)) {
        value = object.getString(key);
      }
    }
    catch (Exception e)
    {
      nlogger.logout(e);
      value = "";
    }
    return value;
  }
  
  public String getImageUri(String imageURL)
  {
    int i = 0;
    if (imageURL.contains("File//upload"))
    {
      i = imageURL.toLowerCase().indexOf("file//upload");
      imageURL = "\\" + imageURL.substring(i);
    }
    if (imageURL.contains("File\\upload"))
    {
      i = imageURL.toLowerCase().indexOf("file\\upload");
      imageURL = "\\" + imageURL.substring(i);
    }
    if (imageURL.contains("File/upload"))
    {
      i = imageURL.toLowerCase().indexOf("file/upload");
      imageURL = "\\" + imageURL.substring(i);
    }
    return imageURL;
  }
  
  public String dencode(String param)
  {
    if (StringHelper.InvaildString(param))
    {
      param = codec.DecodeHtmlTag(param);
      param = codec.decodebase64(param);
    }
    return param;
  }
  
  public JSONObject buildCondOgid(String info)
  {
    JSONObject obj = new JSONObject();
    
    JSONObject tempObj = JSONObject.toJSON(info);
    if ((tempObj != null) && (tempObj.size() > 0)) {
      obj = buildObj(tempObj);
    } else {
      obj = buildArray(JSONArray.toJSONArray(info));
    }
    return obj;
  }
  
  private JSONObject buildObj(JSONObject object)
  {
    JSONArray condArray = new JSONArray();
    JSONArray CondOgid = new JSONArray();
    JSONObject obj = new JSONObject();
    
    dbFilter filter = new dbFilter();
    if ((object != null) && (object.size() > 0))
    {
      for (Object str : object.keySet())
      {
        String key = str.toString();
        if (key.equals("ogid"))
        {
          String value = object.getString(key);
          CondOgid = getOgidCond(value);
        }
        else
        {
          filter.eq(key, object.get(key));
        }
      }
      condArray = filter.build();
    }
    obj.put("ogid", CondOgid);
    obj.put("cond", condArray);
    return obj;
  }
  
  private JSONObject buildArray(JSONArray array)
  {
    JSONArray condArray = new JSONArray();
    JSONArray CondOgid = new JSONArray();
    JSONObject obj = new JSONObject();
    if ((array != null) && (array.size() > 0)) {
      for (Object object : array)
      {
        JSONObject temp = (JSONObject)object;
        String key = temp.getString("field");
        if (key.equals("ogid"))
        {
          String value = temp.getString("value");
          CondOgid = getOgidCond(value);
        }
        else
        {
          condArray.add(temp);
        }
      }
    }
    obj.put("ogid", CondOgid);
    obj.put("cond", condArray);
    return obj;
  }
  
  private JSONArray getOgidCond(String value)
  {
    String ogid = "";
    String[] values = null;
    dbFilter filter = new dbFilter();
    ogid = new ContentGroup().getLinkOgid(value);
    if (StringHelper.InvaildString(ogid))
    {
      values = ogid.split(",");
      String[] arrayOfString1;
      int j = (arrayOfString1 = values).length;
      for (int i = 0; i < j; i++)
      {
        String string = arrayOfString1[i];
        filter.eq("ogid", string);
      }
    }
    return filter.build();
  }
  
  public JSONArray buildCond(String Info)
  {
    JSONArray condArray = null;
    JSONObject object = JSONObject.toJSON(Info);
    dbFilter filter = new dbFilter();
    if ((object != null) && (object.size() > 0))
    {
      for (Object object2 : object.keySet())
      {
        String key = object2.toString();
        Object value = object.get(key);
        filter.eq(key, value);
      }
      condArray = filter.build();
    }
    else
    {
      condArray = JSONArray.toJSONArray(Info);
    }
    return condArray;
  }
  
  public String[] getWeb(String wbid)
  {
    String value = "";
    CacheHelper ch = new CacheHelper();
    String key = "ChildWebId_" + wbid;
    value = ch.get(key);
    if (value != null) {
      return value.split(",");
    }
    wbid = getRWbid(wbid);
    String temp = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getWebTree/" + wbid);
    ch.setget(key, temp, 86400L);
    return temp.split(",");
  }
  
  public String getRWbid(String wbid)
  {
    String temp = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/VID2RID/" + wbid);
    
    return temp;
  }
  
  public JSONObject ContentEncode(JSONObject object)
  {
    String temp = "";
    if ((object != null) && (object.size() > 0))
    {
      if (object.containsKey("content")) {
        object.escapeHtmlPut("content", object.getString("content"));
      }
      if (object.containsKey("image"))
      {
        temp = object.getString("image");
        if ((temp != null) && (!temp.equals("")) && (!temp.equals("null")))
        {
          temp = codec.DecodeHtmlTag(temp);
          object.put("image", RemoveUrlPrefix(temp));
        }
      }
    }
    return object;
  }
  
  public JSONArray ContentDencode(JSONArray array)
  {
    if ((array != null) && (array.size() > 0))
    {
      int l = array.size();
      for (int i = 0; i < l; i++)
      {
        JSONObject object = (JSONObject)array.get(i);
        object = ContentDencode(object);
        array.set(i, object);
      }
    }
    return array;
  }
  
  public JSONObject ContentDencode(JSONObject object)
  {
    if ((object != null) && (object.size() > 0) && (object.containsKey("content"))) {
      object.put("content", object.escapeHtmlGet("content"));
    }
    return object;
  }
  
  public String RemoveUrlPrefix(String imageUrl)
  {
    String image = "";
    if ((imageUrl.equals("")) || (imageUrl == null)) {
      return imageUrl;
    }
    String[] imgUrl = imageUrl.split(",");
    String[] arrayOfString1;
    int j = (arrayOfString1 = imgUrl).length;
    for (int i = 0; i < j; i++)
    {
      String string = arrayOfString1[i];
      if (string.contains("http://")) {
        string = getImageUri(string);
      }
      image = image + string + ",";
    }
    return StringHelper.fixString(image, ',');
  }
  
  public JSONArray getImgs(JSONArray array)
  {
    if ((array == null) || (array.size() <= 0)) {
      return new JSONArray();
    }
    for (int i = 0; i < array.size(); i++)
    {
      JSONObject object = (JSONObject)array.get(i);
      object = getImgs(object);
      array.set(i, object);
    }
    return array;
  }
  
  public JSONObject getImgs(JSONObject object)
  {
    JSONObject imgobj = new JSONObject();
    JSONObject conobj = new JSONObject();
    if ((object == null) || (object.size() == 0)) {
      return new JSONObject();
    }
    String id = object.getMongoID("_id");
    imgobj.put(id, object.get("image"));
    conobj.put(id, object.get("content"));
    imgobj = getImage(imgobj);
    conobj = getContentImgs(conobj);
    object.put("content", conobj.get(id));
    object.put("image", imgobj.get(id));
    return object;
  }
  
  public JSONArray getDefaultImage(String wbid, JSONArray array)
  {
    CacheHelper ch = new CacheHelper();
    String thumbnail = "";
    String suffix = "";
    String tempString = "0";
    int type = 0;
    if ((!wbid.equals("")) && (array != null) && (array.size() != 0))
    {
      int l = array.size();
      
      String temp = ch.get("DefaultImage_" + wbid);
      if (temp == null)
      {
        temp = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getImage/" + wbid);
        ch.setget("DefaultImage_" + wbid, temp, 86400L);
      }
      JSONObject Obj = JSONObject.toJSON(temp);
      if ((Obj != null) && (Obj.size() != 0))
      {
        if (Obj.containsKey("thumbnail")) {
          thumbnail = Obj.getString("thumbnail");
        }
        if (Obj.containsKey("suffix")) {
          suffix = Obj.getString("suffix");
        }
      }
      for (int i = 0; i < l; i++)
      {
        Obj = (JSONObject)array.get(i);
        if ((Obj != null) && (Obj.size() > 0))
        {
          if (Obj.containsKey("isSuffix"))
          {
            tempString = Obj.getString("isSuffix");
            if (tempString.contains("$numberLong")) {
              tempString = JSONObject.toJSON(tempString).getString("$numberLong");
            }
            tempString = (tempString == null) || (tempString.equals("")) || (tempString.equals("null")) ? "0" : tempString;
            type = Integer.parseInt(tempString);
          }
          if (type == 0) {
            suffix = "";
          }
          Obj.put("thumbnail", thumbnail);
          Obj.put("suffix", suffix);
        }
        array.set(i, Obj);
      }
    }
    return array;
  }
  
  private JSONObject getImage(JSONObject objimg)
  {
    for (Object obj : objimg.keySet())
    {
      String key = obj.toString();
      String value = objimg.getString(key);
      if (!value.contains("http://")) {
        objimg.put(key, AddUrlPrefix(value));
      }
    }
    return objimg;
  }
  
  private String AddUrlPrefix(String imageUrl)
  {
    if ((imageUrl.equals("")) || (imageUrl == null)) {
      return imageUrl;
    }
    String[] imgUrl = imageUrl.split(",");
    List list = new ArrayList();
    String[] arrayOfString1;
    int j = (arrayOfString1 = imgUrl).length;
    for (int i = 0; i < j; i++)
    {
      String string = arrayOfString1[i];
      if (!string.contains("http://")) {
        string = getconfig("fileHost") + string;
      }
      list.add(string);
    }
    return StringHelper.join(list);
  }
  
  private JSONObject getContentImgs(JSONObject objcontent)
  {
    int code = 2;
    for (Iterator localIterator = objcontent.keySet().iterator(); localIterator.hasNext();)
    {
      Object obj = localIterator.next();
      String key = obj.toString();
      String value = objcontent.getString(key);
      if (!value.equals(""))
      {
        Matcher matcher = this.ATTR_PATTERN.matcher(value.toLowerCase());
        if (value.contains("/File/upload")) {
          code = value.contains("/File/upload") ? 1 : matcher.find() ? 0 : 2;
        } else if ((value.contains("/")) || (value.contains("\\"))) {
          code = (value.startsWith("/")) || (value.startsWith("\\")) ? 3 : matcher.find() ? 0 : 2;
        }
        switch (code)
        {
        case 0: 
          value = AddHtmlPrefix(value);
          break;
        case 1: 
          value = AddUrlPrefix(value);
          break;
        case 2: 
          break;
        case 3: 
          value = getconfig("fileHost") + value;
        }
        objcontent.put(key, value);
      }
    }
    return objcontent;
  }
  
  private String AddHtmlPrefix(String Contents)
  {
    String imgurl = getconfig("fileHost");
    String temp = "";
    String newurl = "";
    if ((Contents != null) && (!Contents.equals("")) && (!Contents.equals("null")))
    {
      List list = getCommonAddr(Contents);
      if ((list != null) && (list.size() > 0))
      {
        int l = list.size();
        for (int i = 0; i < l; i++)
        {
          temp = (String)list.get(i);
          if (!temp.contains("http://"))
          {
            if ((temp.startsWith("/")) || (temp.startsWith("\\")) || (temp.startsWith("//")))
            {
              if ((temp.toLowerCase().startsWith("/data")) || (temp.toLowerCase().startsWith("\\data")) || (temp.toLowerCase().startsWith("//data"))) {
                newurl = temp.substring(temp.toLowerCase().indexOf("d"));
              } else {
                newurl = "\\" + temp;
              }
            }
            else
            {
              newurl = "\\" + temp;
              newurl = imgurl + newurl;
            }
            Contents = Contents.replace(temp, newurl);
          }
        }
      }
    }
    return Contents;
  }
  
  private List<String> getCommonAddr(String contents)
  {
    Matcher matcher = this.ATTR_PATTERN.matcher(contents);
    List list = new ArrayList();
    while (matcher.find()) {
      list.add(matcher.group(1));
    }
    return list;
  }
  
  public JSONArray join(JSONArray array)
  {
    array = getImgs(array);
    if ((array == null) || (array.size() <= 0)) {
      return null;
    }
    try
    {
      int len = array.size();
      for (int i = 0; i < len; i++)
      {
        JSONObject object = (JSONObject)array.get(i);
        object = join(object);
        array.set(i, object);
      }
    }
    catch (Exception e)
    {
      System.out.println("content.join:" + e.getMessage());
      array = null;
    }
    return array;
  }
  
  public JSONObject join(JSONObject object)
  {
    JSONObject tmpJSON = object;
    if ((tmpJSON != null) && (tmpJSON.containsKey("tempid"))) {
      tmpJSON.put("tempContent", getTemplate(tmpJSON.get("tempid").toString()));
    }
    return tmpJSON;
  }
  
  private String getTemplate(String tid)
  {
    String temp = "";
    CacheHelper cache = new CacheHelper();
    try
    {
      if (tid.contains("$numberLong")) {
        tid = JSONHelper.string2json(tid).getString("$numberLong");
      }
      if (!"0".equals(tid)) {
        if (cache.get(tid) != null)
        {
          temp = cache.get(tid).toString();
        }
        else
        {
          temp = appsProxy.proxyCall("/GrapeTemplate/TemplateContext/TempFindByTid/s:" + tid).toString();
          cache.setget(tid, temp, 36000L);
        }
      }
    }
    catch (Exception e)
    {
      nlogger.logout(e);
      temp = "";
    }
    return temp;
  }
  
  public JSONArray getDefault(String wbid, JSONArray array)
  {
    String ogids = "";
    JSONObject WebImage = getWebThumbnail(wbid);
    if ((array != null) && (array.size() > 0)) {
      for (Object object : array)
      {
        JSONObject tempObj = (JSONObject)object;
        String temp = tempObj.getString("ogid");
        if (!ogids.contains(temp)) {
          ogids = ogids + temp + ",";
        }
      }
    }
    JSONObject ColumnImage = new ContentGroup().getDefaultById(StringHelper.fixString(ogids, ','));
    if ((array != null) && (array.size() > 0))
    {
      int l = array.size();
      for (int i = 0; i < l; i++)
      {
        JSONObject tempObj = (JSONObject)array.get(i);
        tempObj = FillDefaultImage(tempObj, ColumnImage, WebImage);
        array.set(i, tempObj);
      }
    }
    return array;
  }
  
  public JSONObject getDefault(JSONObject object)
  {
    String ogids = "";String wbid = "";
    JSONObject WebImage = null;JSONObject ColumnImage = null;
    if ((object != null) && (object.size() > 0))
    {
      ogids = object.getString("ogid");
      wbid = object.getString("wbid");
    }
    if ((StringHelper.InvaildString(ogids)) && (!ogids.equals("0"))) {
      ColumnImage = new ContentGroup().getDefaultById(ogids);
    }
    if ((StringHelper.InvaildString(wbid)) && (!wbid.equals("0"))) {
      WebImage = getWebThumbnail(wbid);
    }
    return FillDefaultImage(object, ColumnImage, WebImage);
  }
  
  public JSONObject FillDefaultImage(JSONObject contentInfo, JSONObject ColumnImage, JSONObject WebThumbnail)
  {
    String thumbnail = "";String suffix = "";
    
    contentInfo = FillColumnImage(contentInfo, ColumnImage);
    if ((contentInfo != null) && (contentInfo.size() > 0))
    {
      thumbnail = contentInfo.getString("thumbnail");
      suffix = contentInfo.getString("suffix");
    }
    if ((!StringHelper.InvaildString(thumbnail)) || (!StringHelper.InvaildString(suffix))) {
      contentInfo = FillWebImage(contentInfo, WebThumbnail);
    }
    return contentInfo;
  }
  
  private JSONObject FillColumnImage(JSONObject ContentInfo, JSONObject ColumnImage)
  {
    String ogid = "";String thumbnail = "";String suffix = "";
    if ((ContentInfo != null) && (ContentInfo.size() > 0))
    {
      ogid = ContentInfo.getString("ogid");
      if ((StringHelper.InvaildString(ogid)) && 
        (ColumnImage != null) && (ColumnImage.size() > 0))
      {
        JSONObject temp = ColumnImage.getJson(ogid);
        if ((temp != null) && (temp.size() > 0))
        {
          if (temp.containsKey("thumbnail")) {
            thumbnail = temp.getString("thumbnail");
          }
          if (temp.containsKey("suffix")) {
            suffix = temp.getString("suffix");
          }
        }
      }
    }
    ContentInfo.put("thumbnail", getRandomImage(thumbnail));
    ContentInfo.put("suffix", suffix);
    return ContentInfo;
  }
  
  private String getRandomImage(String thumbnails)
  {
    String[] value = null;
    String thumbnail = thumbnails;
    if (StringHelper.InvaildString(thumbnails))
    {
      value = thumbnails.split(",");
      if (value != null)
      {
        int l = value.length;
        if (l > 1) {
          thumbnail = value[new java.util.Random().nextInt(l)];
        }
      }
    }
    if ((StringHelper.InvaildString(thumbnail)) && 
      (!thumbnail.contains("http://"))) {
      thumbnail = getconfig("fileHost") + thumbnail;
    }
    return thumbnail;
  }
  
  private JSONObject FillWebImage(JSONObject ContentInfo, JSONObject WebImage)
  {
    String wbid = "";String thumbnail = "";String suffix = "";String contents = "";String tempSuffix = "";
    String webThumbnail = "";String webSuffix = "";
    int isSuffix = 0;
    if ((ContentInfo != null) && (ContentInfo.size() > 0))
    {
      wbid = ContentInfo.getString("wbid");
      contents = ContentInfo.getString("content");
      if (ContentInfo.containsKey("isSuffix")) {
        tempSuffix = ContentInfo.getString("isSuffix");
      }
      isSuffix = StringHelper.InvaildString(tempSuffix) ? Integer.parseInt(tempSuffix) : 0;
      thumbnail = ContentInfo.getString("thumbnail");
      suffix = ContentInfo.getString("suffix");
      if ((StringHelper.InvaildString(wbid)) && 
        (WebImage != null) && (WebImage.size() > 0))
      {
        JSONObject temp = WebImage.getJson(wbid);
        if ((temp != null) && (temp.size() > 0))
        {
          if (temp.containsKey("thumbnail")) {
            webThumbnail = temp.getString("thumbnail");
          }
          if (temp.containsKey("suffix")) {
            suffix = temp.getString("suffix");
          }
        }
      }
    }
    if (isSuffix != 0)
    {
      suffix = StringHelper.InvaildString(suffix) ? suffix : webSuffix;
      ContentInfo.put("content", contents + suffix);
    }
    if (!StringHelper.InvaildString(thumbnail)) {
      ContentInfo.put("thumbnail", webThumbnail);
    }
    ContentInfo.remove("suffix");
    return ContentInfo;
  }
  
  private JSONObject getWebThumbnail(String wbid)
  {
    JSONObject Obj = null;
    if ((StringHelper.InvaildString(wbid)) && (!wbid.equals("0")))
    {
      String temp = (String)appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getImage/" + wbid);
      if (StringHelper.InvaildString(temp)) {
        Obj = JSONObject.toJSON(temp);
      }
    }
    return Obj;
  }
}
