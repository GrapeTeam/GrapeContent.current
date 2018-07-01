package interfaceApplication;

import org.json.simple.JSONObject;

import common.java.httpServer.grapeHttpUnit;
import common.java.rpc.execRequest;

/**
 * 企务公开爬虫服务数据解析器
 * @author wuren
 *
 */
public class CrawlerDataReceiverQW {
	private String wbid = "594335af1a4769cbf5d04180";
	
	// 企业动态,ogid:"594336adc6c204111c8aa93b"
	public String publishQYDT(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "594336adc6c204111c8aa93b", wbid);
	}
	
	// 国企党建,ogid:"594cd00ec6c20412eca7dc1a"
	public String publishGQDJ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null != receiveJSON && null!=receiveJSON.get("source")){
			// source格式"文章来源：新闻中心 　　 发布时间：2018-06-07"
			String publishDate = "";
			String source = (String)receiveJSON.get("source");
			
			int pos1=source.indexOf("文章来源：");
			int pos2=source.indexOf("发布时间：");
			int beginIndex = -1;
			int endIndex = -1;
			
			// 发布时间
			if(pos2 > -1){
				beginIndex = pos2+5;
				endIndex = source.indexOf(" ", beginIndex)>0?source.indexOf(" ", beginIndex):source.length();
				publishDate = source.substring(beginIndex, endIndex);
			}
			
			// 时间来源
			if(pos1 > -1){
				beginIndex = pos1+5;
				endIndex = source.indexOf(" ", beginIndex)>0?source.indexOf(" ", beginIndex):source.length();
				source = source.substring(beginIndex, endIndex);
			}else{
				source = "国务院国资委";
			}
			receiveJSON.put("publishDate",publishDate);
			receiveJSON.put("source",source);
		}
		return CrawlerDataReceiver.publish(receiveJSON, "594cd00ec6c20412eca7dc1a", wbid);
	}
	
	// 政策解读,ogid:"594336e6c6c204111c8aa93e"
	public String publishZCJD(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null != receiveJSON && null!=receiveJSON.get("source")){
			// source格式“来源： 人民日报”
			String source = (String)receiveJSON.get("source");
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source",source);
		}
		return CrawlerDataReceiver.publish(receiveJSON, "594336e6c6c204111c8aa93e", wbid);
	}
	
	// 纪检要闻,ogid:"594ccfeec6c20412eca7dc19"
	public String publishJJYW(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON && null!=receiveJSON.get("source")){
			// source格式"2018-05-23 10:04 作者：安徽检察"
			String source = (String)receiveJSON.get("source");
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source", source);
		}
		return CrawlerDataReceiver.publish(receiveJSON, "594ccfeec6c20412eca7dc19", wbid);
	}
	
	// 本网专评,ogid:"594336b6c6c204111c8aa93c"
	public String publishBWZP(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		if(null!=receiveJSON && null!=receiveJSON.get("source")){
			// source格式"来源：中央纪委国家监委网站"
			String source = (String)receiveJSON.get("source");
			source = source.substring(source.indexOf("：")+1).trim();
			receiveJSON.put("source", source);
		}		
		
		return CrawlerDataReceiver.publish(receiveJSON, "594336b6c6c204111c8aa93c", wbid);
	}
	
	// 以案警示,ogid:"59433f32c6c204111c8aa946"
	public String publishYASJ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		
		if(null != receiveJSON){
			if(null!=receiveJSON.get("source")){
				// source格式"来源：中国纪检监察报"
				String source = (String)receiveJSON.get("source");
				int pos1=source.indexOf("：");
				source = source.substring(pos1+1).trim();
				receiveJSON.put("source",source);
			}
			if(null!=receiveJSON.get("publishDate")){
				// publishDate格式"发布时间：2018-06-14 08:41"
				String publishDate = (String)receiveJSON.get("publishDate");
				int pos1=publishDate.indexOf("：");
				publishDate = publishDate.substring(pos1+1).trim();
				receiveJSON.put("publishDate",publishDate);
			}

		}
		
		return CrawlerDataReceiver.publish(receiveJSON, "59433f32c6c204111c8aa946", wbid);
	}
	
	// 视频集锦,ogid:"59433f4ac6c204111c8aa947"
	public String publishSPJJ(){
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		JSONObject receiveJSON = CrawlerDataReceiver.parseCrawlerData(post);
		return CrawlerDataReceiver.publish(receiveJSON, "59433f4ac6c204111c8aa947", wbid);
	}
}
