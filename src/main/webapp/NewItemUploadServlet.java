package main.webapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

/**
 * Servlet implementation class NewItemUploadServlet
 */
@WebServlet("/new_item_upload_servlet")
public final class NewItemUploadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewItemUploadServlet()
	{
		super();
	}

	public static final int maxItemImageSize = Utils.megaToByte(2);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.getWriter().append("Use POST! ");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ServerFileManager sfm = new ServerFileManager(getServletContext(), request, response, "/totilingua/uploaditem.html");
		try
		{
			//
			// Checking access
			//
			sfm.verifyIP("Untrusted IP adress!");
			//
			// Parsing upload request
			//
			sfm.verifyIfMultipart("Content is not multipart!");
			List<FileItem> items = sfm.prepareUpload(maxItemImageSize).parseRequest(new ServletRequestContext(request));
			sfm.verifyNumberOfSentFileItems(items, 4, "Invalid count of parameters!");
			if (!AccessVerifier.checkCode(sfm.findValue(items, "access code", "No access code !")))
			{
				sfm.error("Access denied! ");
				return;
			}
			String engName = sfm.findValue(items, "english name", "English name is empty !");
			String polName = sfm.findValue(items, "polish name", "Polish name is empty !");
			FileItem image = sfm.findFile(items, "file", "No image uploaded !");

			//
			// Checking if MySQL is available
			// and querying current number of rows
			//
			MySqlManager mySQL = new MySqlManager();
			mySQL.connect();
			int rows = 0;
			Connection conn = mySQL.getConnection();
			if (conn == null)
			{
				sfm.error("SQL connection is NULL! ");
				return;
			}
			Statement s = conn.createStatement();
			if (s == null)
			{
				sfm.error("SQL statement is NULL!");
				return;
			}
			ResultSet result = s.executeQuery(mySQL.sqlCountRows());
			if (!result.next())
			{
				sfm.error("SQL query result in invalid!");
				return;
			}
			rows = result.getInt(1) + 1;// +1 because we are going to add one
										// more entry soon
			if (rows < 1)
			{
				sfm.error("Unknown problem with MySQL!");
				return;
			}

			//
			// trying to save upload
			//
			sfm.writeFile(image, DataDirectories.getPathTo(DataDirectories.IMAGES, rows + ".PNG"), false);

			//
			// making changes in MySQL
			//
			s.executeUpdate(mySQL.sqlInsertRow(new String[] { engName, polName }, new LanguageTags[] { LanguageTags.ENGLISH, LanguageTags.POLISH }));

			//
			// rending response
			//
			response.setContentType("text/html");
			response.sendRedirect("/totilingua/dictionary?lang=en&index=" + rows);
		}
		catch (Exception e)
		{
			sfm.error(e.getMessage());
		}
	}
}
