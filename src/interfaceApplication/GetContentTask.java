package interfaceApplication;

import java.util.Date;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.apps.appIns;
import common.java.apps.appsProxy;
import common.java.json.JSONHelper;

/**
 * 铜官区五务公开-最新公开
 * @author wuren
 *
 */
public class GetContentTask implements Callable<JSONArray> {
	private String name;
	private int days; 
	private appIns apps;
	
	public GetContentTask(appIns apps,String name,int days){
		this.apps = apps;
		this.name = name;
		this.days = days;
	} 
	
	@Override
	public JSONArray call() throws Exception {
		String jsonStr = "";
		JSONArray result = new JSONArray();
		
		switch(name){
			case "cwgk":// 村务公开
				jsonStr = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getAllWeb/int:1/int:1000/s:59816be6c6c204051c9b0c89/s:null",apps).toString();
				result = parseJsonStr_1(apps, jsonStr, days);
				break;
			case "jwgk":// 居务公开
				jsonStr = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getAllWeb/int:1/int:1000/s:59816bc0c6c204051c9b0c88/s:null",apps).toString();
				result = parseJsonStr_1(apps, jsonStr, days);
				break;
			case "xwgk":// 校务公开
				jsonStr = appsProxy.proxyCall("/GrapeWebInfo/WebInfo/getAllWeb/int:1/int:1000/s:59816b9ec6c204051c9b0c87/s:null",apps).toString();
				result = parseJsonStr_1(apps, jsonStr, days);
				break;
			case "ywgk":// 院务公开
				jsonStr = appsProxy.proxyCall("/GrapeContent/Content/FindNewArc/s:598d7046c6c20403447c4560/int:1/int:10",apps).toString();
				result = parseJsonStr_2(jsonStr, days);
				break;
			case "qwgk":// 企务公开
				jsonStr = appsProxy.proxyCall("/GrapeContent/Content/FindNewArc/s:598d7021c6c20403447c455f/int:1/int:10",apps).toString();
				result = parseJsonStr_2(jsonStr, days);
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * 解析“村务公开”、“居务公开”、“校务公开”
	 * @param jsonStr
	 * @param days
	 * @return
	 */
	private JSONArray parseJsonStr_1(appIns apps, String jsonStr, int days){
		JSONArray array = new JSONArray();
		
		Date t = new Date();
		long nowTime = t.getTime();
		
		JSONObject target = JSONHelper.string2json(jsonStr);
        if(target != null && target.getJson("message")!=null && target.getJson("message").getJson("record")!=null){
        	JSONArray jArr = target.getJson("message").getJson("record").getJsonArray("data");
        	if(null!=jArr && jArr.size()>0){
        		for(Object obj:jArr){
        			JSONObject jObj = (JSONObject)obj;
        			if(null!=jObj){
        				String _id = jObj.get("_id").toString();
        				String artItemStr = appsProxy.proxyCall("/GrapeContent/Content/FindNewArc/s:"+_id+"/int:1/int:10", apps).toString();
        				
        				JSONArray itemArr = JSONHelper.string2json(artItemStr).getJson("message").getJson("record").getJsonArray("data");
        				for(Object item:itemArr){
        					JSONObject itemObj = (JSONObject)item;
            				long time = Long.parseLong(itemObj.get("time").toString());
            				if(time+days*24*60*60*1000 > nowTime){
            					// days天内发布的新闻
            					array.add(itemObj);
            				}else{
            					break;
            				}
        				}
        			}
        		}
        	}
        }
		return array;
	}
	
	/**
	 * 解析“院务公开”、“企务公开”
	 * @param jsonStr
	 * @param days
	 * @return
	 */
	private JSONArray parseJsonStr_2(String jsonStr, int days){
		JSONArray array = new JSONArray();
		
		Date t = new Date();
		long nowTime = t.getTime();
		
		JSONObject target = JSONHelper.string2json(jsonStr);
		JSONArray itemArr = target.getJson("message").getJson("record").getJsonArray("data");
		for(Object item:itemArr){
			JSONObject itemObj = (JSONObject)item;
			long time = Long.parseLong(itemObj.get("time").toString());
			if(time+days*24*60*60*1000 > nowTime){
				// days天内发布的新闻
				array.add(itemObj);
			}else{
				break;
			}
		}
		return array;
	}

}
