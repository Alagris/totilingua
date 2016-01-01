<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="mul">
    <head>
        <title>
            Totilingua dictionary
        </title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
    </head>

    <body>
       
        <div id="left_bar">
            <a href="/totilingua/uploaditem.html">Upload custom item</a>
        </div>  


        <div id="right_bar">
            <ul>
                <li><a href="dictionary?lang=en&index=<%=session.getAttribute("index") %>" id="english_link">English</a></li>
                <li><a href="dictionary?lang=de&index=<%=session.getAttribute("index") %>" id="german_link">German</a></li>
                <li><a href="dictionary?lang=pl&index=<%=session.getAttribute("index") %>" id="polish_link">Polish</a></li>
                <li><a href="dictionary?lang=ru&index=<%=session.getAttribute("index") %>" id="russian_link">Russian</a></li>
                <li><a href="dictionary?lang=es&index=<%=session.getAttribute("index") %>" id="spanish_link">Spanish</a></li>
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
        <!-- src="" is case sensitive and you must remember about slashes -->
        <img id="image" src="<%=session.getAttribute("img_path") %>" alt="Error! Image not found!">
        
    </body>
</html>