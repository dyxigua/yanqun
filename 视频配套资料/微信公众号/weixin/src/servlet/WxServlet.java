package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.regexp.internal.REUtil;

import service.WxService;

@WebServlet("/wx")
public class WxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		 * signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
			timestamp	时间戳
			nonce	随机数
			echostr	随机字符串
		 */
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		//校验证签名
		if(WxService.check(timestamp,nonce,signature)) {
			System.out.println("接入成功");
			PrintWriter out = response.getWriter();
			//原样返回echostr参数
			out.print(echostr);
			out.flush();
			out.close();
		}else {
			System.out.println("接入失败");
		}
	}

	/**
	 * 接收消息和事件推送
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		//处理消息和事件推送
		Map<String, String> requestMap = WxService.parseRequest(request.getInputStream());
		System.out.println(requestMap);
		//准备回复的数据包
		String respXml = WxService.getRespose(requestMap);
		System.out.println(respXml);
		PrintWriter out = response.getWriter();
		out.print(respXml);
		out.flush();
		out.close();
	}

}
