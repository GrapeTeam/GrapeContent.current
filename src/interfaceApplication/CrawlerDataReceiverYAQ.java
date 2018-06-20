package interfaceApplication;

import org.json.simple.JSONObject;

import common.java.httpServer.grapeHttpUnit;
import common.java.rpc.execRequest;

/**
 * 义安区五务公开爬虫服务数据解析器
 * @author wuren
 *
 */
public class CrawlerDataReceiverYAQ {
	private String wbid = "597ff7609c93690f5a54291b";
	
	// 通知公告,ogid:"59b0e9d5780c9b21501398bd"
	public String publishTZGG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String publishDate = receiveJSON.get("publishDate") == null ?"":(String)receiveJSON.get("publishDate");
			String source = receiveJSON.get("source") == null ?"铜陵市义安区人民政府":(String)receiveJSON.get("source");
			
			publishDate = publishDate.substring(publishDate.indexOf("：")+1).trim();
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9d5780c9b21501398bd", wbid);
	}
	
	// 热点关注,ogid:"59b0ea0a780c9b21501398c3"
	public String publishRDGZ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null != receiveJSON){
			String source = receiveJSON.get("source") == null ? "安徽纪检监察网" : (String)receiveJSON.get("source");
			receiveJSON.put("source",source);
		}
		return CrawlerDataReceiver.publish(receiveJSON, "59b0ea0a780c9b21501398c3" ,wbid);
	
	}
	
	// 工作动态,ogid:"59b0e9e1780c9b21501398be"
	public String publishGZDT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String publishDate = receiveJSON.get("publishDate") == null ?"":(String)receiveJSON.get("publishDate");
			String source = receiveJSON.get("source") == null ?"铜陵市义安区人民政府":(String)receiveJSON.get("source");
			
			publishDate = publishDate.substring(publishDate.indexOf("：")+1).trim();
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9e1780c9b21501398be" ,wbid);
	}
	
	// 政策法规,ogid:"59b0e9f1780c9b21501398c1"
	public String publishZCFG_1(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String source = receiveJSON.get("source") == null ? "铜陵市人力资源和社会保障局" : (String)receiveJSON.get("source");
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9f1780c9b21501398c1" ,wbid);
	}
	
	public String publishZCFG_2(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String source = receiveJSON.get("source") == null ? "铜陵市卫生和计划生育委员会" : (String)receiveJSON.get("source");
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9f1780c9b21501398c1" ,wbid);
	}
	
	public String publishZCFG_3(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String source = receiveJSON.get("source") == null ? "铜陵市民政局" : (String)receiveJSON.get("source");
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9f1780c9b21501398c1" ,wbid);
	}
	
	public String publishZCFG_4(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			String source = receiveJSON.get("source") == null ? "铜陵市教育局" : (String)receiveJSON.get("source");
			receiveJSON.put("source",source);
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59b0e9f1780c9b21501398c1" ,wbid);
	}
	
	// 咨询选登（后台管理系统没看到有该栏目）,ogid:
	public String publishZXXD(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			receiveJSON.put("source","铜陵市义安区人民政府");
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "" ,wbid);
	}
	
	// 反腐动态,ogid:"59b0ea01780c9b21501398c2"
	public String publishFZDT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "59b0ea01780c9b21501398c2" ,wbid);
	}
	
	// 监督曝光,ogid:"5a2f30ec64ebaeb5b0005e9c"
	public String publishJDBG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "5a2f30ec64ebaeb5b0005e9c" ,wbid);
	}
}
