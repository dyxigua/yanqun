<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="js/jquery-1.8.3.js"></script>

<script type="text/javascript">
	function register()
	{
			var $mobile = $("#mobile").val();
			/*
			$.ajax({
				url:"MobileServlet",
				请求方式:"post",
				data:"mobile="+$mobile, 
				success:function(result,testStatus)
				{
					if(result == "true"){
						alert("已存在！注册失败！");
					}else{
						alert("注册成功！");
					}
				},
				error:function(xhr,errrorMessage,e){
					alert("系统异常！");
				}


				});
				
				
				$.post(
						"MobileServlet",
						"mobile="+$mobile,
					function (result){
							if(result == "true"){
								alert("已存在！注册失败！");
							}else{
								alert("注册成功！");
							}
					},
					"text"
					);
			
				
				$.get(
						"MobileServlet",
						"mobile="+$mobile,
					function (result){
							if(result == "true"){
								alert("已存在！注册失败！");
							}else{
								alert("注册成功！");
							}
					}
					);
				
				$("#tip").load(
						"MobileServlet",
						"mobile="+$mobile
				);
			
			
			var student = {"name":"zs" ,  "age":23} ;
			
			//alert(student.name +"--" +student.age) ;
			//var name = ["xx","xx","xx"] ;
			var students =[
					
					{"name":"zs" ,  "age":23} ,
					{"name":"ls" ,  "age":24} ,
					{"name":"ww" ,  "age":25} 
					
			];
			alert(students[1].name +"--" +students[1].age) ;
			*/
			$.getJSON(
					"MobileServlet",
				//	"mobile="+$mobile,
					{"mobile":$mobile},
				function (result){//msg:true|false
						alert(123);
						if(result.msg == "true"){
							alert("已存在！注册失败！");
						}else{
							alert("注册成功！");
						}
				}

			);

	}
	

</script>


<title>Insert title here</title>
</head>
<body>
		手机：<input  id="mobile"/>
		<br/>
		<input type="button" value="注册" onclick="register()" />
		<span id="tip"></span>

</body>
</html>