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
@WebServlet("/dictionary")
public class Dictionary extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Dictionary()
	{
		super();
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// mysql -u admin9JeMdDK -h 127.0.0.1 -P 3306 -p
		// pass: hrlhCL_7Kf1h
		// SELECT `"+languageTag+"` FROM `Languages` WHERE `index`="+index
		String languageTag = request.getParameter("lang");
		if (languageTag == null)
		{
			languageTag = "en";
		}
		String indexString = request.getParameter("index");
		int index;
		try
		{
			index = Integer.parseInt(indexString);
		}
		catch (NumberFormatException e)
		{
			index = 1;
		}
		MySqlManager mySQl = new MySqlManager();

		String text = "MySQL ERROR!";
//		int allItemsCount = -1;
		mySQl.connect();
		try
		{
			Connection conn = mySQl.getConnection();
			if (conn == null)
			{
				Utils.forwardToErrorSite(getServletContext(), request, response, "SQL connection is NULL!", "/totilingua/index.html");
				return;
			}

			Statement s = conn.createStatement();
			if (s == null)
			{
				Utils.forwardToErrorSite(getServletContext(), request, response, "SQL statement is NULL!", "/totilingua/index.html");
				return;
			}
			ResultSet set = s.executeQuery(mySQl.sqlSelectWord(index, languageTag));
			if (!set.next())
			{
				Utils.forwardToErrorSite(getServletContext(), request, response, "Word query result is invalid!", "/totilingua/index.html");
				return;
			}
			text = set.getString(1);
//			set = s.executeQuery(mySQl.sqlCountRows());
//			if (!set.next())
//			{
//				Utils.forwardToErrorSite(getServletContext(), request, response, "Rows query result is invalid!", "/totilingua/index.html");
//				return;
//			}
//			allItemsCount = set.getInt(1);
		}
		catch (SQLException e)
		{
			Utils.forwardToErrorSite(getServletContext(), request, response, e.getMessage(), "/totilingua/index.html");
		}
		if (text == null)
		{
			text = "Not set yet!";
		}
		// It doesn't work without .getSession()
		request.getSession().setAttribute("img_path", DownloadFromDataDir.getURLtoDataThroughThisServlet(DataDirectories.getPathTo(DataDirectories.IMAGES, index + ".PNG")));
		request.getSession().setAttribute("index", index);
		request.getSession().setAttribute("text", text);
		getServletContext().getRequestDispatcher("/dictionary.jsp").forward(request, response);

	}

}
