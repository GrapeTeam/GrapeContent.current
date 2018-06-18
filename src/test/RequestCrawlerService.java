package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * 程度请求爬虫服务
 * @author wuren
 *
 */
public class RequestCrawlerService {

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 */
	public static String get(String url) {
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setConnectTimeout(1000*30);//连接超时时间
			connection.setReadTimeout(1000*30);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
//			return sb.toString();
			return "请求【"+url+"】成功    返回内容：【"+sb.toString()+"】";
		} catch (Exception e) {
//			System.out.println("Exception occur when send http get request!");
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return "请求【"+url+"】失败！！！！！！！！！！！！！";
	}     
	
	/**
	 * 发送HttpPost请求
	 * 
	 * @param strURL
	 *            服务地址
	 * @param params
	 * 
	 * @return 成功:返回json字符串<br/>
	 */
	public static String jsonPost(String strURL, Map<String, String> params) {
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST"); // 设置请求方式
			connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.connect();
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
			out.append(JSONObject.toJSONString(params));
			out.flush();
			out.close();

			int code = connection.getResponseCode();
			InputStream is = null;
			if (code == 200) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}

			// 读取响应
			int length = (int) connection.getContentLength();// 获取长度
			if (length != -1) {
				byte[] data = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, data, destPos, readLen);
					destPos += readLen;
				}
				String result = new String(data, "UTF-8"); // utf-8编码
				return result;
			}

		} catch (IOException e) {
			System.out.println("Exception occur when send http post request!");
		}
		return "error"; // 自定义错误信息
	}
	
	public static void main(String[] args) {
		String rootPath = "http://nlp.putao282.com:8088/temp/testCrawler/";
		String[] ids_sjw = new String[] {"131","133","135","137","139","143","145","149"};//市纪委
		String[] ids_tgq = new String[] {"123","125","127"};//铜官区五务
		String[] ids_yaq = new String[] {"101","103","105","106","108","110","112","116","117","119","121"};//义安区五务
		String[] ids_qw = new String[] {"11","13","15","17"};//铜陵市企务
		
		String[] ids = new String[]{"105","110","112","116","117","119","121"};
		
		for(String id: ids){
			String url = rootPath + id;
			System.out.println(get(url));
		}

	}

}
