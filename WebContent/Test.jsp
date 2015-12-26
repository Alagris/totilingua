<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="mul">
    <head>
        <title>
            Totilingua
        </title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
    </head>

    <body>
		<%=session.getAttribute("index") %>
		<%=session.getAttribute("text") %>
		<%=session.getAttribute("lang") %>
    </body>
</html>