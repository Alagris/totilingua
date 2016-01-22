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
			sfm.verifyNumberOfSentFileItems(items, 8, "Invalid count of parameters!");
			if (!AccessVerifier.checkCode(sfm.findValue(items, "access code", "No access code !")))
			{
				sfm.error("Access denied! ");
				return;
			}
			//
			// Collecting and validating data
			//
			int desiredIndex = Integer.parseInt(sfm.findValue(items, "index", "Index name not sent !"));
			String[] tags = LanguageTags.getTags();
			String[] names = sfm.findArray(items, "Some names not sent!", tags);
			int countOfValidNames = 0;
			if ((countOfValidNames= countValidNames(names))== 0)
			{
				sfm.error("At least one name must be specified!");
				return;
			}

			FileItem image = sfm.findFile(items, "file", "No image sent !");
			boolean isImageEmpty = image.getName() == null || image.getName().equals("");

			if (desiredIndex < 1 && isImageEmpty)
			{
				sfm.error("Index is less than 1! You must specify valid image in this case! ");
				return;
			}
			//
			// Checking if MySQL is available
			// and querying current number of rows
			//
			MySqlManager mySQL = new MySqlManager();
			mySQL.connect();
			int rows = -1;
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
			rows = result.getInt(1);

			if (rows < 0)
			{
				sfm.error("Unknown problem with MySQL query result!");
				return;
			}
			//
			// validating index
			//
			if (desiredIndex > rows || desiredIndex < 1)
			{
				if (isImageEmpty)
				{
					sfm.error("Index is greater than biggest already existing! You must specify valid image in this case! ");
					return;
				}
				desiredIndex = rows + 1;// in this case we're going to add one
										// more item
			} // otherwise we will modify an existing one

			//
			// trying to save upload
			//
			if (!isImageEmpty)
				sfm.writeFile(image, DataDirectories.getPathTo(DataDirectories.IMAGES, desiredIndex + ".PNG"), false);

			//
			// making changes in MySQL
			//
			String[] validTags =new String[countOfValidNames];
			String[] validNames =new String[countOfValidNames];
			for(int i=0,j=0;i<names.length;i++){
				if (names[i].length() > 0){
					validNames[j]=names[i];
					validTags[j++]=tags[i];
				}
			}
			if (desiredIndex == rows + 1)
			{
				s.executeUpdate(mySQL.sqlInsertRow(validNames, validTags));
			}
			else
			{
				s.executeUpdate(mySQL.sqlUpdateRow(validNames, validTags,desiredIndex));
			}

			//
			// sending response
			//
			response.setContentType("text/html");
			response.sendRedirect("/totilingua/dictionary?lang=en&index=" + desiredIndex);
		}
		catch (Exception e)
		{
			sfm.error(e.getMessage());
		}
	}

	private int countValidNames(String[] names)
	{
		int r=0;
		for (String n : names)
			if (n.length() > 0)
				r++;
		return r;
	}
}
