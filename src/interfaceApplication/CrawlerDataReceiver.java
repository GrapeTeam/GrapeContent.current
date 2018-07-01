package interfaceApplication;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import cn.wanghaomiao.xpath.model.JXDocument;
import common.java.security.codec;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import unit.LogsUtils;
import unit.WebUtils;

/**
 * 爬虫服务数据收集器公共方法
 * @author wuren
 *
 */
public class CrawlerDataReceiver {
	public static JSONObject parseCrawlerData(JSONObject post){
		if(null == post){
			return null;
		}
		
		if(null != post.get("content")){
			String _httpContent = (String)post.get("content");
			String content = codec.DecodeFastJSON(_httpContent);//对content的内容进行解码
			post.put("content", content);	
		}
		
//		String data = post.toJSONString();
//		JSONObject result = new JSONObject();
//		try {
//			data = new String(data.getBytes("ISO-8859-1"), "UTF-8");
//			result = JSONObject.toJSON(data);
//		} catch (UnsupportedEncodingException e) {
//			System.out.println("字符编码错误！");
//			e.printStackTrace();
//		}  
		return post;
	}
	
	/**
	 * 获取默认的内容实体
	 * @return
	 */
	public static JSONObject getDefaultContentJSONObject(String ogid, String wbid){
		JSONObject obj = new JSONObject();
		obj.put("author", "信息自动采集");//作者姓名
		obj.put("content", "");//内容
		obj.put("mainName", "");//标题
		obj.put("ogid", ogid);//栏目ID
		obj.put("souce", "");//内容来源机构
		obj.put("time", 0);//内容发布时间戳
		obj.put("subName", null);//副标题
		
		obj.put("attribute", "0");
		obj.put("attrid", 0);
		obj.put("desp", "");
		obj.put("fatherid", 0);
		obj.put("image", "");
		obj.put("isSuffix", 0);
		obj.put("isdelete", 0);
		obj.put("isvisble", 0);
		obj.put("manageid", 0);
		obj.put("oid", "");
		obj.put("ownid", 0);
		obj.put("readCount", 0);
		obj.put("slevel", 0);
		obj.put("sort", 0);
		obj.put("state", 2);
		obj.put("substate", 0);
		obj.put("wbid", wbid);
		obj.put("subID", RandomNum());
		obj.put("reason", "");
		obj.put("dMode", null);
		obj.put("point",0);
		obj.put("thirdsdkid", "0");
		obj.put("tempid", "");
		obj.put("cash", 0);
		obj.put("rMode", null);
		obj.put("clickcount", 0);
		obj.put("reasonRead", 0);
		obj.put("uMode", null);
		obj.put("isTop", 0);
		obj.put("md5", "");//内容的md5编码
		return obj;
	}
	
	/**
	 * 铜陵市纪检委廉田网手机站爬虫服务自动采集信息发布方法
	 * @param data	爬虫服务自动采集的数据
	 * @param ogid	栏目ID
	 * @return
	 */
	public static String publish(JSONObject receiveJSON, String ogid, String wbid) {
		if(null == receiveJSON || null == ogid || "".equals(ogid.trim())){
			return "";
		}
		
		JSONObject object = CrawlerDataReceiver.getDefaultContentJSONObject(ogid, wbid);
		
		long time = 0L;
		String temptime = "";
		
		long currentTime = TimeHelper.nowMillis();
		
//        if (receiveJSON.containsKey("time")) {
//            temptime = receiveJSON.getString("time");
//            if (StringHelper.InvaildString(temptime)) {
//                time = Long.parseLong(temptime);
//            }
//        }
        if (time != 0L) {
            time = time < currentTime ? time : currentTime;
        } else {
            time = currentTime;
        }
		
        object.put("time", time);
        object.put("author",receiveJSON.get("author")==null?receiveJSON.get("source"):receiveJSON.get("author")); // 作者
        object.put("content",receiveJSON.get("content"));// 内容
        object.put("mainName",receiveJSON.get("title"));// 标题
        object.put("subName", receiveJSON.get("subtitle"));//副标题
        object.put("souce",receiveJSON.get("source"));// 来源
        object.put("contenturl",receiveJSON.get("contenturl"));// 新闻链接地址
       
        String result = "";
        
        if("59433f4ac6c204111c8aa947".equals(ogid)){
        	// 企务公开->视频锦集，发布链接地址
        	result = publicLink(ogid, object);
        }else{
        	result = publicContent(ogid, object);
        }
        
        
        
        return result;
	
	}
	
	/**
	 * 发布内容
	 * @return
	 */
	public static String publicContent(String ogid, JSONObject object){
		String result = "";
        ContentGroup contentGroup = new ContentGroup();
        String ogname = WebUtils.getOgname_by_ogid(ogid);
        
		Content content = new Content();
		
		JSONArray objArray = content.CrawlerContentIsExist(ogid, (String)object.get("mainName"));
		
		String contentStr = (String)object.get("content"); // html内容
		if(contentStr.indexOf("&amp;lt;")>-1){// 出现两次转码的情况
			contentStr = StringEscapeUtils.unescapeHtml4(contentStr);
			object.put("content", contentStr);
		}
		
		String domHTML = StringEscapeUtils.unescapeHtml4(contentStr);
		Document document = Jsoup.parse(domHTML);
		List<TextNode> nodes = document.textNodes();
		String textContent = document.text();// 文本内容
		object.put("textContent", textContent);
		
		String md5 = codec.md5(contentStr);// 针对html内容进行MD5编码
		object.put("md5", md5);
		String md5_txt = codec.md5(textContent);// 针对文本内容进行MD5编码
		object.put("md5_text", md5_txt);
		
		if(null == objArray){
			// 新增文章
			result = addContent(content, object);
		}else{
			boolean addFlag = true; // 新增文章标识
			String updateId = ""; // 要更新的记录id
			Long time = 0L;
			
			for(int i = 0; i < objArray.size(); i++){
				JSONObject obj = (JSONObject) objArray.get(i);
			    String _id = (String)obj.get("_id");
			    String _content = (String)(obj.get("content") == null?"":obj.get("content"));
			    String _textContent = (String)(obj.get("textContent") == null?"":obj.get("textContent"));
			    
			    String _md5 = obj.get("md5") == null ? codec.md5(_content) : (String)obj.get("md5");// 数据库中保存的md5编码
			    String _md5_txt = obj.get("md5_text") == null ? codec.md5(_textContent) : (String)obj.get("md5_text");// 数据库中保存的md5_txt编码
			    
			    Long _time = (Long)obj.get("time");
			    
			    if(_md5.equals(md5)){
			    	// 内容完全一致，不需要新增和更新
			    	addFlag = false;
			    	break;
			    }else if(_md5_txt.equals(md5_txt)){
			    	// 文本内容完全一致，但是html内容不一致，更新。可能原因是图片或文件url经过重新编译生成
			    	if("".equals(updateId)){
			    		updateId = _id;
			    		time = _time;
			    	}
			    }
			}
			
			if(addFlag){
				if(!"".equals(updateId)){
					// 更新文章
					object.put("_id",updateId);
					object.put("time",time);
					result = updateContent(content, object);
				}else{
					// 新增文章
					result = addContent(content, object);					
				}
			}else{
				System.out.println("************** 【"+object.getString("mainName")+"】文章已存在不做更新 *************");
			}
		}
		return result;
	}
	
	/**
	 * 更新文章
	 * @param content
	 * @param object
	 * @param ogname
	 */
	public static String updateContent(Content content, JSONObject object){
		// 新增文章
		String result = content.crawlerUpdate(object);
		
        JSONObject json = JSONObject.toJSON(result);
        if (json.getInt("errorcode") == 0) {
            LogsUtils.addLogs("999", "爬虫服务程序", "更新文章【" + object.getString("mainName") + "】成功", "1", "EditArticle");
            
        }else{
        	System.out.println("**************  更新文章【 "+object.getString("mainName")+"】失败  ************** ");
        }
        return result;
	}
	
	/**
	 * 新增文章
	 * @param content
	 * @param object
	 * @param ogname
	 */
	public static String addContent(Content content, JSONObject object){
		// 新增文章
		String result = content.crawlerInsert(object);
		
        JSONObject json = JSONObject.toJSON(result);
        if (json.getInt("errorcode") == 0) {
            LogsUtils.addLogs("999", "爬虫服务程序", "新增文章【" + object.getString("mainName") + "】成功", "1", "PublishArticle");
            
        }else{
        	System.out.println("**************  新增文章【 "+object.getString("mainName")+"】失败  ************** ");
        }
        return result;
	}
	
	/**
	 * 发布链接
	 * @return
	 */
	public static String publicLink(String ogid, JSONObject object){
		String result = "";
		object.remove("content");
        ContentGroup contentGroup = new ContentGroup();
        String ogname = WebUtils.getOgname_by_ogid(ogid);
        
        String contenturl = object.get("contenturl")==null?"":(String)object.get("contenturl");
        object.put("contenturl", contenturl);
        
		Content content = new Content();
		
		JSONArray objArray = content.CrawlerContentIsExist(ogid, (String)object.get("mainName"));
		
		if(null == objArray){
			// 新增文章
			result = content.crawlerInsert(object);
	        JSONObject json = JSONObject.toJSON(result);
	        if (json.getInt("errorcode") == 0) {
	            LogsUtils.addLogs("999", "爬虫服务程序", "在[" + ogname + "]栏目下发布了[" + object.getString("mainName") + "]新闻链接", "1", "PublishArticle");
	            System.out.println("**************  发布新增新闻链接【 "+object.getString("mainName")+"】成功  ************** ");
	            
	        }else{
	        	System.out.println("**************  发布新增新闻链接【 "+object.getString("mainName")+"】失败  ************** ");
	        }
		}else{
			for(int i=0; i <objArray.size(); i++){
				JSONObject obj = (JSONObject)objArray.get(i);
			    String _id = (String)obj.get("_id");
			    Long _time = (Long)obj.get("time");
			    String _contenturl = obj.get("contenturl")==null?"":(String)obj.get("contenturl");
				System.out.println("************** 新闻链接已存在【 "+object.getString("mainName")+" ("+ _id +") 】**************");
				// 标题相同，链接地址不同，认为是更新当前新闻的链接地址
				if(!contenturl.equals(_contenturl)){
					object.put("_id", _id);
					result = content.crawlerUpdate(object);
				}
			}
		}
		System.out.println("**************  采集信息入库结果： "+("".equals(result)?"文章链接已存在不做更新":result)+"  ***");
		return result;
	}
	
    public static int RandomNum() {
        int number = (new Random()).nextInt(2147483647) + 1;
        return number;
    }
    
    public static JXDocument initDoc(String domHTML){
        Document doc = null;
        doc = Jsoup.parse(domHTML);      
        return new JXDocument(doc);     
    }
    
    public static void printConspicuousInfo(String[] text){
//    	System.out.println("***********************************************************************");
//    	System.out.println("*                                                                     *");
//    	for(int i=0;i<text.length;i++){
//    		System.out.println("*                    "+text[i]+"                          *");
//    	}
//    	System.out.println("*                                                                     *");
//    	System.out.println("***********************************************************************");
    }
}
