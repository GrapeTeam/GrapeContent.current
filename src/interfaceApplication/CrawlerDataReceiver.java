package interfaceApplication;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.json.simple.JSONObject;

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
		
		String _httpContent = (String)post.get("content");
		String content = codec.DecodeFastJSON(_httpContent);//对content的内容进行解码
		post.put("content", content);
		
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
		String ogname = ""; // 栏目名称
		
		long currentTime = TimeHelper.nowMillis();
		
        if (receiveJSON.containsKey("time")) {
            temptime = receiveJSON.getString("time");
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
        object.put("author",receiveJSON.get("author")==null?receiveJSON.get("source"):receiveJSON.get("author")); // 作者
        object.put("content",receiveJSON.get("content"));// 内容
        object.put("mainName",receiveJSON.get("title"));// 标题
        object.put("subName", receiveJSON.get("subtitle"));//副标题
        object.put("souce",receiveJSON.get("source"));// 来源
        
        ContentGroup contentGroup = new ContentGroup();
        ogname = WebUtils.getOgname_by_ogid(ogid);

        contentGroup.publishActicle_than_update(ogid);
        
		Content content = new Content();
		JSONObject obj = content.CrawlerContentIsExist(ogid, (String)object.get("mainName"));
		
		String result ="";
		
//		System.out.println("**************  爬虫信息采集内容     **************");
//	    System.out.println(object.toJSONString());
//	    System.out.println("*******************************************");
		
		String md5 = codec.md5((String)object.get("content"));// 发布文章内容的md5编码
		
	    String _id = (String)obj.get("_id");
	    String _md5 = (String)obj.get("md5");// 数据库中保存的md5编码
	    Long _time = (Long)obj.get("time");
	    
		if("0".equals(_id)){
			// 新增
			object.put("md5", md5);
			result = content.crawlerInsert(object);
			
	        JSONObject json = JSONObject.toJSON(result);
	        if (json.getInt("errorcode") == 0) {
	            LogsUtils.addLogs("999", "爬虫服务程序", "在[" + ogname + "]栏目下发布了[" + object.getString("mainName") + "]新闻", "1", "PublishArticle");
	            
	        }else{
	        	System.out.println("**************  新增文章【 "+object.getString("mainName")+"】失败  ************** ");
	        }
		}else{
			System.out.println("************** 文章已存在【 "+object.getString("mainName")+" ("+ _id +") 】**************");
			// 文章内容对比，如果内容不同则认为是发布一篇新的文章
			if(!md5.equals(_md5)){
				object.put("md5", md5);
				object.put("content", obj.get("content"));
				result = content.crawlerInsert(object);
				
				/* 更新已有文章内容，实际情况是经常有相同标题的文章出现，会覆盖旧文章内容
				object.put("_id", _id);
				object.put("md5", md5);
				object.put("content", obj.get("content"));
				result = content.crawlerUpdate(object);*/
			}
		}
		System.out.println("**************  采集信息入库结果： "+("".equals(result)?"文章已存在不做更新":result)+"  ***");
        return result;
	
	}
	
    public static int RandomNum() {
        int number = (new Random()).nextInt(2147483647) + 1;
        return number;
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
