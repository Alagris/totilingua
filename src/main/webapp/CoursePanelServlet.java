package main.webapp;

//import static main.webapp.Utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/course")
public class CoursePanelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CoursePanelServlet() {
        super();
    }
    
    
    
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//mysql -u admin9JeMdDK -h 127.0.0.1 -P 3306 -p
		//pass: hrlhCL_7Kf1h
		//SELECT `"+languageTag+"` FROM `Languages` WHERE `index`="+index
		String languageTag = request.getParameter("lang");
		if(languageTag==null){
			languageTag="en";
		}
		String indexString=request.getParameter("index");
		int index = indexString==null?1:Integer.parseInt(indexString);
		
		MySqlManager mySQl = new MySqlManager();
		
		String text=null;
		mySQl.connect();
		try {
			Connection conn = mySQl.getConnection();
			if(conn!=null){
				Statement s = conn.createStatement();
				if(s!=null){
					ResultSet set =s.executeQuery(mySQl.selectWord(index, languageTag));
					if(set.next()){//this is very important
						text = set.getString(1);
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(text==null){
			text= "MySQL ERROR!";
		}
		//It doesn't work without .getSession()
		request.getSession().setAttribute("lang", languageTag);
		request.getSession().setAttribute("index", index);
		request.getSession().setAttribute("text", text);
		getServletContext().getRequestDispatcher("/CoursePanel.jsp").forward(request, response);
	
	}

}
