package Want.Demo;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class servletDemo11 extends HttpServlet {
	
	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String name = null;
		String xz = null;
		String [] mc = null;
		PrintWriter out = null;
		String allmc = null;
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		name = (String)request.getSession().getAttribute("uname");
		xz = (String)request.getSession().getAttribute("uxz");
		mc = (String [])request.getSession().getAttribute("umc");
		
		if(name == null || xz == null ||mc ==null){
			try {
				response.sendRedirect("./index.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
				System.out.print(name);
				try {
					request.setCharacterEncoding("utf-8");
//					session.getAttribute("uname");;
//					xz=request.getParameter("xz");
//					mc=request.getParameterValues("mc");
					
				
				//由于cheakBox对应数据库的数据只定义了一个varchar(20)的大小，所以整合一下cheakBox的数据存入数据库
					
					for(String s1:mc) {
						if(allmc!=null) {
							allmc = allmc +s1;
						}else {
							allmc = s1;
						}
						
					}
					
					
					out = response.getWriter();
					out.println("企业名称："+name+"<br/>");
					out.println("企业性质："+xz+"<br/>");
					out.println("生产产品名称 :");
					for(String s1:mc) {
						out.print(s1+"      ");
					}
					out.println("<br/>");
				}catch(IOException e) {
					e.printStackTrace();
				}catch(Exception e) {
					e.printStackTrace();
				}finally {//关闭out
					if(out!=null) {
						out.close();
					}
				}
				
				
				ServletConfig config = this.getServletConfig();
				
				//从web.xml获取uer、password、以及存储数据表的路径；
				String u = config.getInitParameter("u");
				String psd = config.getInitParameter("pssd");
				String way = config.getInitParameter("way");
				
				
				Connection connection = null;
				PreparedStatement pstmt = null;
				
				
				
				try {
					//导入驱动
					Class.forName("com.mysql.jdbc.Driver");
					
					//建立与数据库的连接
					connection = DriverManager.getConnection(way,u,psd);
					//connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/select_test","root","110412");
					
					//sql语句及PreparedStatement的预编译
					String sql_1 = "insert into work values(?,?,?);";
					pstmt = connection.prepareStatement(sql_1);
					
					
					
					//替换占位符
					pstmt.setString(1, name);
					pstmt.setString(2, xz);
					pstmt.setString(3, allmc);
					
					//执行数据库插入语句
					int count = 0;
					count = pstmt.executeUpdate();
					
					if(count > 0) {
						System.out.println("修改成功！");
					}
					
					//out把request的信息 输出到servlet显示
				
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					
					try {
						if(pstmt!=null) {
							pstmt.close();
						}
						if(connection!=null) {
							connection.close();
						}
					} catch (SQLException e) {
							e.printStackTrace();
					}
				}

		}
	}
}
