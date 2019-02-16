package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

import entity.Button;
import entity.ClickButton;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.PhotoOrAlbumButton;
import entity.SubButton;
import entity.TextMessage;
import entity.VideoMessage;
import entity.ViewButton;
import entity.VoiceMessage;
import net.sf.json.JSONObject;
import service.WxService;

public class TestWx {
	//设置APPID/AK/SK
	public static final String APP_ID = "11519092";
	public static final String API_KEY = "q3TlGWWqEBG9uGvlFIBtpvY5";
	public static final String SECRET_KEY = "A14W5VRNG8my1GXYYAyNND0RjzBwxI8A";
	
	@Test
	public void testGetUserInfo() {
		String user="oOI9Lw8GIP_7u71L1jr0_OAjJChE";
		String info = WxService.getUserInfo(user);
		System.out.println(info);
	}

	@Test
	public void testQrCode() {
		String ticket = WxService.getQrCodeTicket();
		System.out.println(ticket);
	}
	
	@Test
	public void test() {
		System.out.println(WxService.getAccessToken());
		//15_9DHAkyQuqkpLhvqAN5SAWsamv1ifJhoRBaofIMbavU4Q7qFwjjgTd-a1b13drz2ANkCaJsAXuvSvB3Z7BwJ-giQTCBwOI1ifDdtqccarOQUwWvgt2XYACAdqD3yT3PvZYlBiAMnwfWaxNQfILERcABAOHU
	}
	
	@Test
	public void testUpload() {
		String file = "/Users／Richard/Documents/1.jpg";
		String result = WxService.upload(file, "image");
		System.out.println(result);
		//TLfodwngsUr9rjHdITKiB9uT4Dq7K-QnV00MVd3_U6LnZeAqpZl3vYIICjUq48BY
	}
	
	@Test
	public void testPic() {
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 可选：设置代理服务器地址, http和socket二选一，或者均不设置
		//client.setHttpProxy("proxy_host", proxy_port); // 设置http代理
		//client.setSocketProxy("proxy_host", proxy_port); // 设置socket代理

		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		//System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

		// 调用接口
		String path = "/Users／Richard/Documents/2.png";
		org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
		System.out.println(res.toString(2));
	}

	@Test
	public void testButton() {
		// 菜单对象
		Button btn = new Button();
		// 第一个一级菜单
		btn.getButton().add(new ClickButton("一级点击", "1"));
		// 第二个一级菜单
		btn.getButton().add(new ViewButton("一级跳转", "http://www.baidu.com"));
		// 创建第三个一级菜单
		SubButton sb = new SubButton("有子菜单");
		// 为第三个一级菜单增加子菜单
		sb.getSub_button().add(new PhotoOrAlbumButton("传图", "31"));
		sb.getSub_button().add(new ClickButton("点击", "32"));
		sb.getSub_button().add(new ViewButton("网易新闻", "http://news.163.com"));
		// 加入第三个一级菜单
		btn.getButton().add(sb);
		// 转为json
		JSONObject jsonObject = JSONObject.fromObject(btn);
		System.out.println(jsonObject.toString());
	}

	@Test
	public void testToken() {
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
	}

	@Test
	public void testMsg() {
		Map<String, String> map = new HashMap<>();
		map.put("ToUserName", "to");
		map.put("FromUserName", "from");
		map.put("MsgType", "type");
		TextMessage tm = new TextMessage(map, "还好");
		XStream stream = new XStream();
		// 设置需要处理XStreamAlias("xml")注释的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(tm);
		System.out.println(xml);

	}

}
