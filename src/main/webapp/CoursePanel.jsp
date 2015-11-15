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
       
        <div id="left_bar">
            <pre>
 here will be
 some links
            </pre>
        </div>  


        <div id="right_bar">
            <ul>
                <li><a href="servlets/CoursePanelServlet?lang=en" id="english_link">English</a></li>
                <li><a href="" id="german_link">German</a></li>
                <li><a href="servlets/CoursePanelServlet?lang=pl" id="polish_link">Polish</a></li>
                <li><a href="" id="russian_link">Russian</a></li>
                <li><a href="" id="spanish_link">Spanish</a></li>
            </ul>
        </div> 

        <div id="word_div">
            <a id="word" class="inside_word_div">
                <%=session.getAttribute("text") %>
            </a>
            <audio id="audio" class="inside_word_div" controls autoplay >
                <source id="audio_source" src="audio/<%=session.getAttribute("index") %>.mp3" type="audio/mpeg">
                Your browser does not support the audio element.
            </audio>
        </div>
         
        <img id="image" src="images/<%=session.getAttribute("index") %>.jpg" alt="Error! Image not found!">
        
    </body>
</html>