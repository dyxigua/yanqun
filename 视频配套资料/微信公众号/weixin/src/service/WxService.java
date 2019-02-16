package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

import entity.AccessToken;
import entity.Article;
import entity.BaseMessage;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.TextMessage;
import entity.VideoMessage;
import entity.VoiceMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.Signal;
import util.Util;

public class WxService {
	private static final String TOKEN = "llzs";
	private static final String APPKEY="1fec136dbd19f44743803f89bd55ca62";
	private static final String GET_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//微信公众号
	private static final String APPID="wxb6777fffdf5b64a4";
	private static final String APPSECRET="6b55d3bb4d9c5373c8a30915d900ca13";
	//百度AI
	public static final String APP_ID = "11519092";
	public static final String API_KEY = "q3TlGWWqEBG9uGvlFIBtpvY5";
	public static final String SECRET_KEY = "A14W5VRNG8my1GXYYAyNND0RjzBwxI8A";
	
	//用于存储token
	private static AccessToken at;
	
	/**
	 * 获取token
	 * by 罗召勇 Q群193557337
	 */
	private static void getToken() {
		String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		String tokenStr = Util.get(url);
		JSONObject jsonObject = JSONObject.fromObject(tokenStr);
		String token = jsonObject.getString("access_token");
		String expireIn = jsonObject.getString("expires_in");
		//创建token对象,并存起来。
		at = new AccessToken(token, expireIn);
	}
	
	/**
	 * 向处暴露的获取token的方法
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static String getAccessToken() {
		if(at==null||at.isExpired()) {
			getToken();
		}
		return at.getAccessToken();
	}
	
	/**
	 * 验证签名
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static boolean check(String timestamp, String nonce, String signature) {
		 //1）将token、timestamp、nonce三个参数进行字典序排序 
		String[] strs = new String[] {TOKEN,timestamp,nonce};
		Arrays.sort(strs);
		 //2）将三个参数字符串拼接成一个字符串进行sha1加密 
		String str = strs[0]+strs[1]+strs[2];
		String mysig = sha1(str);
		 //3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
		return mysig.equalsIgnoreCase(signature);
	}
	
	/**
	 * 进行sha1加密
	 * @param str
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static String sha1(String src) {
		try {
			//获取一个加密对象
			MessageDigest md = MessageDigest.getInstance("sha1");
			//加密
			byte[] digest = md.digest(src.getBytes());
			char[] chars= {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			StringBuilder sb = new StringBuilder();
			//处理加密结果
			for(byte b:digest) {
				sb.append(chars[(b>>4)&15]);
				sb.append(chars[b&15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析xml数据包
	 * @param is
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static Map<String, String> parseRequest(InputStream is) {
		Map<String, String> map = new HashMap<>();
		SAXReader reader = new SAXReader();
		try {
			//读取输入流，获取文档对象
			Document document = reader.read(is);
			//根据文档对象获取根节点
			Element root = document.getRootElement();
			//获取根节点的所有的子节点
			List<Element> elements = root.elements();
			for(Element e:elements) {
				map.put(e.getName(), e.getStringValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 用于处理所有的事件和消息的回复
	 * @param requestMap
	 * @return 返回的是xml数据包
	 * by 罗召勇 Q群193557337
	 */
	public static String getRespose(Map<String, String> requestMap) {
		BaseMessage msg=null;
		String msgType = requestMap.get("MsgType");
		switch (msgType) {
			//处理文本消息
			case "text":
				msg=dealTextMessage(requestMap);
				break;
			case "image":
				msg=dealImage(requestMap);
				break;
			case "voice":
				
				break;
			case "video":
				
				break;
			case "shortvideo":
				
				break;
			case "location":
				
				break;
			case "link":
				
				break;
			case "event":
				msg = dealEvent(requestMap);
				break;
			default:
				break;
		}
		//把消息对象处理为xml数据包
		if(msg!=null) {
			return beanToXml(msg);
		}
		return null;
	}

	/**
	 * 进行图片识别
	 * @param requestMap
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static BaseMessage dealImage(Map<String, String> requestMap) {
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
		// 调用接口
		String path = requestMap.get("PicUrl");
		
		//进行网络图片的识别
		org.json.JSONObject res = client.generalUrl(path, new HashMap<String,String>());
		String json = res.toString();
		//转为jsonObject
		JSONObject jsonObject = JSONObject.fromObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("words_result");
		Iterator<JSONObject> it = jsonArray.iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()) {
			JSONObject next = it.next();
			sb.append(next.getString("words"));
		}
		return new TextMessage(requestMap, sb.toString());
	}

	/**
	 * 处理事件推送
	 * @param requestMap
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static BaseMessage dealEvent(Map<String, String> requestMap) {
		String event = requestMap.get("Event");
		switch (event) {
			case "CLICK":
				return dealClick(requestMap);
			case "VIEW":
				return dealView(requestMap);
			default:
				break;
		}
		return null;
	}

	/**
	 * 处理view类型的按钮的菜单
	 * @param requestMap
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static BaseMessage dealView(Map<String, String> requestMap) {
		
		return null;
	}

	/**
	 * 处理click菜单
	 * @param requestMap
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static BaseMessage dealClick(Map<String, String> requestMap) {
		String key = requestMap.get("EventKey");
		switch (key) {
			//点击一菜单点
			case "1":
				//处理点击了第一个一级菜单
				return new TextMessage(requestMap, "你点了一点第一个一级菜单");
			case "32":
				//处理点击了第三个一级菜单的第二个子菜单
				break;
			default:
				break;
		}
		return null;
	}

	/**
	 * 把消息对象处理为xml数据包
	 * @param msg
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream = new XStream();
		//设置需要处理XStreamAlias("xml")注释的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(msg);
		return xml;
	}

	/**
	 * 处理文本消息
	 * @param requestMap
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
		//用户发来的内容
		String msg = requestMap.get("Content");
		if(msg.equals("图文")) {
			List<Article> articles = new ArrayList<>();
			articles.add(new Article("这是图文消息的标题", "这是图文消息的详细介绍", "http://mmbiz.qpic.cn/mmbiz_jpg/dtRJz5K066YczqeHmWFZSPINM5evWoEvW21VZcLzAtkCjGQunCicDubN3v9JCgaibKaK0qGrZp3nXKMYgLQq3M6g/0", "http://www.baidu.com"));
			NewsMessage nm = new NewsMessage(requestMap, articles);
			return nm;
		}
		if(msg.equals("登录")) {
			String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb6777fffdf5b64a4&redirect_uri=http://www.6sdd.com/weixin/GetUserInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
			TextMessage tm = new TextMessage(requestMap, "点击<a href=\""+url+"\">这里</a>登录");
			return tm;
		}
		//调用方法返回聊天的内容
		String resp = chat(msg);
		TextMessage tm = new TextMessage(requestMap, resp);
		return tm;
	}

	/**
	 * 调用图灵机器人聊天
	 * @param msg 	发送的消息
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	private static String chat(String msg) {
        String result =null;
        String url ="http://op.juhe.cn/robot/index";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请到的本接口专用的APPKEY
        params.put("info",msg);//要发送给机器人的内容，不要超过30个字符
        params.put("dtype","");//返回的数据的格式，json或xml，默认为json
        params.put("loc","");//地点，如北京中关村
        params.put("lon","");//经度，东经116.234632（小数点后保留6位），需要写为116234632
        params.put("lat","");//纬度，北纬40.234632（小数点后保留6位），需要写为40234632
        params.put("userid","");//1~32位，此userid针对您自己的每一个用户，用于上下文的关联
        try {
            result =Util.net(url, params, "GET");
            //解析json
            JSONObject jsonObject = JSONObject.fromObject(result);
            //取出error_code
            int code = jsonObject.getInt("error_code");
            if(code!=0) {
            		return null;
            }
            //取出返回的消息的内容
            String resp = jsonObject.getJSONObject("result").getString("text");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	
	/**
	 * 上传临时素材
	 * @param path	上传的文件的路径
	 * @param type	上传的文件类型
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static String upload(String path,String type) {
		File file = new File(path);
		//地址
		String url="https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
		try {
			URL urlObj = new URL(url);
			//强转为案例连接
			HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
			//设置连接的信息
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//设置请求头信息
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "utf8");
			//数据的边界
			String boundary = "-----"+System.currentTimeMillis();
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			//获取输出流
			OutputStream out = conn.getOutputStream();
			//创建文件的输入流
			InputStream is = new FileInputStream(file);
			//第一部分：头部信息
			//准备头部信息
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition:form-data;name=\"media\";filename=\""+file.getName()+"\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			out.write(sb.toString().getBytes());
			System.out.println(sb.toString());
			//第二部分：文件内容
			byte[] b = new byte[1024];
			int len;
			while((len=is.read(b))!=-1) {
				out.write(b, 0, len);
			}
			is.close();
			//第三部分：尾部信息
			String foot = "\r\n--"+boundary+"--\r\n";
			out.write(foot.getBytes());
			out.flush();
			out.close();
			//读取数据
			InputStream is2 = conn.getInputStream();
			StringBuilder resp = new StringBuilder();
			while((len=is2.read(b))!=-1) {
				resp.append(new String(b,0,len));
			}
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取带参数二维码的ticket
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static String getQrCodeTicket() {
		String at = getAccessToken();
		String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+at;
		//生成临时字符二维码
		String data="{\"expire_seconds\": 600, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"llzs\"}}}";
		String result = Util.post(url, data);
		String ticket = JSONObject.fromObject(result).getString("ticket");
		return ticket;
	}
	
	/**
	 * 获取用户的基本信息
	 * @return
	 * by 罗召勇 Q群193557337
	 */
	public static String getUserInfo(String openid) {
		String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("OPENID", openid);
		String result = Util.get(url);
		return result;
	}

}
