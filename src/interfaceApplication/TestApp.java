package interfaceApplication;

import common.java.JGrapeSystem.jGrapeFW_Message;
import common.java.broadCast.broadCastGroup;
import common.java.cache.redis;
import common.java.checkCode.imageCheckCode;
import common.java.database.DBHelper;
import common.java.file.uploadFileInfo;
import common.java.httpClient.request;
import common.java.httpServer.grapeHttpUnit;
import common.java.rpc.execRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestApp {
	public String test(int a,int b){
		return "putao520 love U" + String.valueOf(a) + String.valueOf(b);
	}
	/*
	public File filetest(){
		return excelHelper.out("");
	}
	*/
	public String cacheTest(){
		redis cache = new redis("localCache");
		cache.set("testKey", "testValue");
		return "asd";
	}
	@SuppressWarnings("unchecked")
	public String fileup(){
		JSONObject paramJSON = new JSONObject();
		File nFile = new File("C:\\webtools\\ffmpeg-20170214\\bin\\ffmpeg2.exe");
		paramJSON.put("fileupload",nFile);
		paramJSON.put("desc", "tester");
		return request.doPostFrom("http://192.168.3.141:8080/UploadFile/Uploader", paramJSON);
	}
	public String mongobug(){
		DBHelper helper = new DBHelper("mongodb", "test");
		System.out.println(helper.or().eq("_id", new ObjectId("58e6f748154e73267cd2769c")).eq("_id", new ObjectId("58c11cc01a4769cbf5e7edc3")).delete());
	    return "";
	}
	@SuppressWarnings("unchecked")
	public String bigData(){
		DBHelper dbcontent = new DBHelper("mongodb", "content");
		JSONObject _obj = new JSONObject();
		_obj.put("records", dbcontent.desc("time").eq("ogid", "58f744391a4769cbf53b822d").limit(20).select().toJSONString());
		//return jGrapeFW_Message.netMSG(0, _obj.toJSONString());
		return jGrapeFW_Message.netMSG(0, _obj.toString());
	}
	
	public byte[] image(){
		return imageCheckCode.getCodeimage("asd2");
	}
	public String testmongodb(Object start,Object end){
		DBHelper helper = new DBHelper("mongodb", "Words_13");
    	return helper.and().gte("time",start ).lte("time", end).select().toString();
	}
	public static int testNo = 0;
	public String testCache(){
		testNo++;
		return (new JSONObject("data",new JSONObject("content", "putao520->" + String.valueOf(testNo)) )).toJSONString();
	}
	public String testredis(){
		redis cache = new redis("localcache");
		cache.set("testString", "putao520");
		cache.delete("jkulg2314q23");
		return cache.get("testString").toString();
	}
	
	public boolean wstest(int i1,int i2,String s1){
		broadCastGroup bcg = new broadCastGroup();
		Object wsc = execRequest.getChannelValue( grapeHttpUnit.ws.wsid );
		if( wsc != null ){
			bcg.add( (Channel)wsc );
			bcg.broadCast("tester1:" + s1);
		}
		return false;
	}
	
	public Object upload() throws IOException {
		Object rs = false;
		uploadFileInfo out = null;
		JSONObject post = (JSONObject) execRequest.getChannelValue(grapeHttpUnit.formdata);
		if( post != null ){
			out = (uploadFileInfo) post.get("media");
			File d = new File("d://test.jpg");
			if( out.isBuff() ){
				FileOutputStream fin = new FileOutputStream(d);
				try {
					ByteBuf buff = out.getLocalBytes();
					buff.readBytes(fin,buff.readableBytes());
					//fin.write(  );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					fin.close();
				}
			}
			else{
				File src = out.getLocalFile();
				src.renameTo(d);
				rs = "upload OK";
			}
		}	
		return rs;
	}
}