<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
		
		
		<%
		
		request.setCharacterEncoding("utf-8");
		
		String name=request.getParameter("name");
		String xz=request.getParameter("xz");
		String[] mc=request.getParameterValues("mc");
		if(xz==null){
			response.sendRedirect("index.jsp");
		}else if(xz.equals("***") || name == null || mc ==null){
			response.sendRedirect("index.jsp");
		}else{
			session.setAttribute("uname", name);
			session.setAttribute("uxz", xz);
			session.setAttribute("umc", mc);
			
			
			request.getRequestDispatcher("/servletDemo11").forward(request,response);
		}

		%>

</body>
</html>