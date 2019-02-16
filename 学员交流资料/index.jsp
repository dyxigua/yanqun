<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<form action="cheak.jsp">
		企业名称：<input type="text" name="name"/><br/>
		企业性质：<select name="xz">
					<option value="***"> -请选择-
    				<option value="有限责任公司">有限责任公司
    				<option value="股份有限公司">股份有限公司
				</select><br/>
		生产产品名称<br/>：<input type="checkbox" name="mc" value = "足球"/>足球
		<input type="checkbox" name="mc" value = "篮球"/>篮球
		<input type="checkbox" name="mc" value = "乒乓球"/>乒乓球<br/>
		
		<input type="submit" value ="提交">
	</form>

</body>
</html>