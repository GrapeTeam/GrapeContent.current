package interfaceApplication;

import org.json.simple.JSONObject;
import common.java.httpServer.grapeHttpUnit;
import common.java.rpc.execRequest;

/**
 * 铜官区五务公开爬虫服务数据解析器
 * @author wuren
 *
 */
public class CrawlerDataReceiverTGQ {
	private String wbid = "59227dda1a4769cbf5983cd5";
	
	// 通知公告,ogid:"59895f44c6c20418046dc112"
	public String publishTZGG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			receiveJSON.put("source","铜官区纪检监察网");
		}
		return CrawlerDataReceiver.publish(receiveJSON, "59895f44c6c20418046dc112", wbid);
	}
	
	// 工作动态,ogid:"5989326cc6c20418046dc110"
	public String publishGZDT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			receiveJSON.put("source","铜官区纪检监察网");
		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "5989326cc6c20418046dc110", wbid);
	}
	
	// 政策法规,ogid:"59895f88c6c20418046dc113"
	public String publishZCFG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "59895f88c6c20418046dc113", wbid);
	}
	
	// 监督曝光,ogid:"59895fa6c6c20418046dc114"
	public String publishJDBG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "59895fa6c6c20418046dc114", wbid);
	}
	
	// 首页图片新闻,ogid:"59899655c6c2041220867141"
	public String publishSYTPXW(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(receiveJSON != null){
			receiveJSON.put("attribute", "1");//轮播
			receiveJSON.put("content", "");
		}
		return CrawlerDataReceiver.publish(receiveJSON, "59899655c6c2041220867141", wbid);
	}
	
	// 阳光公告,ogid:"5b370156a713aba0d4a9ef8f"
	public String publishYGGG(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON){
			// source格式"发布日期：2018-06-14 09:20   发布单位：科室管理员    来源：教育局    阅读：64 次   字体：[] []   保护视力色："
			String initSource = (String)receiveJSON.get("source");
			String source = initSource;
			
			int pos = initSource.indexOf("来源：");
			int beginIndex = pos+3;
			int endIndex = initSource.indexOf(" ", pos);
			source = initSource.substring(beginIndex, endIndex);
			
			pos = initSource.indexOf("发布单位：");
			beginIndex = pos+5;
			endIndex = initSource.indexOf(" ", pos);
			String author = initSource.substring(beginIndex, endIndex);
			
			receiveJSON.put("author",author==null||author.trim().equals("")?"铜官区教育局":author);
			receiveJSON.put("source",source==null||source.trim().equals("")?"铜官区教育局":source);
		}
//		发布日期：2018-06-14 09:20   发布单位：科室管理员    来源：教育局    阅读：64 次   字体：[] []   保护视力色：       
		return CrawlerDataReceiver.publish(receiveJSON, "5b370156a713aba0d4a9ef8f", wbid);
	}
}
