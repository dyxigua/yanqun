package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.deploy.uitoolkit.impl.fx.ui.UITextArea;

import net.sf.json.JSONObject;
import util.Util;

/**
 * Servlet implementation class GetUserInfo
 */
@WebServlet("/GetUserInfo")
public class GetUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取code
		String code = request.getParameter("code");
		//换取accesstoken的地址
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		url=url.replace("APPID", "wxb6777fffdf5b64a4").replace("SECRET", "6b55d3bb4d9c5373c8a30915d900ca13").replace("CODE", code);
		String result = Util.get(url);
		String at = JSONObject.fromObject(result).getString("access_token");
		String openid = JSONObject.fromObject(result).getString("openid");
		//拉取用户的基本信息
		url="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		url=url.replace("ACCESS_TOKEN", at).replace("OPENID", openid);
		result = Util.get(url);
		System.out.println(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
