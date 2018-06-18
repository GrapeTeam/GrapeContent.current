package interfaceApplication;

import org.json.simple.JSONObject;

import common.java.httpServer.grapeHttpUnit;
import common.java.rpc.execRequest;

/**
 * 铜陵市纪委廉田网手机站爬虫服务数据解析器
 * @author wuren
 *
 */
public class CrawlerDataReceiverSJW {
	private JSONObject publishCommon(JSONObject receiveJSON){
		if(null!=receiveJSON && null!=receiveJSON.get("publishDate")){
			// 格式"2018-06-11 14:46 作者：安徽日报"
			String source = (String) receiveJSON.get("publishDate");
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source", source);
		}
		return receiveJSON;
	} 
	
	
	// 纪监要闻,ogid:"5a2a22d912f81640f849a32d"
	public String publishJJYW(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a22d912f81640f849a32d", "5a2a0aee95a4acee6a70e4c3");
	}
	
	// 图片新闻,ogid:"5a2a2caa12f81640f849a33f"
	public String publishTPXW(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a2caa12f81640f849a33f", "5a2a0aee95a4acee6a70e4c3");
	}	
	
	// 本地动态,ogid:"5a2a22e312f81640f849a32e"
	public String publishBDDT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a22e312f81640f849a32e", "5a2a0aee95a4acee6a70e4c3");
	}	
	
	// 县区传真,ogid:"5a2a230112f81640f849a332"
	public String publishXQCZ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a230112f81640f849a332", "5a2a0aee95a4acee6a70e4c3");
	}	
	
	// 八面来风,ogid:"5a2a22f912f81640f849a331"
	public String publishBMLF(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a22f912f81640f849a331", "5a2a0aee95a4acee6a70e4c3");
	}	
	
	// 廉政镜鉴,ogid:"5a2a231512f81640f849a334"
	public String publishLZJJ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a231512f81640f849a334", "5a2a0aee95a4acee6a70e4c3");
	}	
	
	// 反腐论坛,ogid:"5a2a230a12f81640f849a333"
	public String publishFFLT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a230a12f81640f849a333", "5a2a0aee95a4acee6a70e4c3");
	}
	
	// 廉政文化,ogid:"5a2a231c12f81640f849a335"
	public String publishLZWH(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a231c12f81640f849a335", "5a2a0aee95a4acee6a70e4c3");
	}
	
	// 通知公告,ogid:"5a2a22f112f81640f849a330"
	public String publishTZGG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		receiveJSON = publishCommon(receiveJSON);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2a22f112f81640f849a330", "5a2a0aee95a4acee6a70e4c3");
	}
	
	
}
