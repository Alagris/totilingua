<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Error</title>
	</head>
<body>
	<h1>ERROR!</h1>
	<h4><%=session.getAttribute("errorMessage")%></h4> 
	<a href="<%=session.getAttribute("returnLink")%>">return</a> 
</body>
</html>