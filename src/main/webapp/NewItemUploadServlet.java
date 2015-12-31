package main.webapp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
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

	private boolean verifyAccess(String code)
	{
		return code != null && code.equals("Q#2^mwD`\\sV9-Q<K^}Zt'Mu'mM_-gM");// Q#2^mwD`\sV9-Q<K^}Zt'Mu'mM_-gM
	}

	private boolean verifyIP(String remoteAddr)
	{
		switch (remoteAddr)
		{
			case "77.242.234.122":
			case "127.0.0.1":
				// case "": //other IPs..
				return true;

		}
		return false;
	}

	private void error(ServletContext context, HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException
	{
		Utils.forwardToErrorSite(context, request, response, errorMessage, "/totilingua/uploaditem.html");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log("POST");
		if (!verifyIP(request.getRemoteAddr()))
		{
			error(getServletContext(), request, response, "Untrusted IP adress!" + request.getRemoteAddr());
			return;
		}
		log("IP good!");
		if (!ServletFileUpload.isMultipartContent(request))
		{
			error(getServletContext(), request, response, "Content is not multipart!");
			return;
		}
		log("is multipart");

		DiskFileItemFactory factory = new DiskFileItemFactory(maxItemImageSize, new File(getServletContext().getRealPath("/tmp")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxItemImageSize);
		try
		{
			List<FileItem> fileItems = upload.parseRequest(new ServletRequestContext(request));
			if (fileItems.size() == 4)
			{
				FileItem itemImage = null;
				String engName = "", polName = "";
				boolean hasAccess = false;

				for (FileItem i : fileItems)
				{
					if (i.isFormField())
					{
						switch (i.getFieldName())
						{
							case "english name":
								engName = i.getString();
								break;
							case "polish name":
								polName = i.getString();
								break;
							case "access code":
								if (verifyAccess(i.getString()))
								{
									hasAccess = true;
								}
								else
								{
									error(getServletContext(), request, response, "Access denied! " + i.getString());
									return;
								}
						}
					}
					else
					{
						if (itemImage == null)
						{
							itemImage = i;
						}
						else
						{
							error(getServletContext(), request, response, "You cannot upload multiple images at once! ");
							return;
						}

					}
				}
				if (!hasAccess)
				{
					error(getServletContext(), request, response, "No access code !");
					return;
				}
				if (engName == "")
				{
					error(getServletContext(), request, response, "English name is empty !");
					return;
				}
				if (polName == "")
				{
					error(getServletContext(), request, response, "Polish name is empty !");
					return;
				}
				if (itemImage == null)
				{
					error(getServletContext(), request, response, "No image uploaded !");
					return;
				}
				MySqlManager mySQL = new MySqlManager();
				mySQL.connect();
				int rows = 0;
				try
				{
					Connection conn = mySQL.getConnection();
					if (conn == null)
					{
						error(getServletContext(), request, response, "SQL connection is NULL!");
						return;
					}
					Statement s = conn.createStatement();
					if (s == null)
					{
						error(getServletContext(), request, response, "SQL statement is NULL!");
						return;
					}
					rows = s.executeUpdate(mySQL.sqlInsertRow(new String[] { engName, polName }, new LanguageTags[] { LanguageTags.ENGLISH, LanguageTags.POLISH }));
				}
				catch (SQLException e)
				{
					error(getServletContext(), request, response, e.getMessage());
					return;
				}
				
				if (rows < 1)
				{
					error(getServletContext(), request, response, "Unknown problem with MySQL!");
					return;
				}
				
				try
				{
					File destinationFile =new File(getServletContext().getRealPath("/images/" + rows+".PNG"));
					if(destinationFile.exists()){
						error(getServletContext(), request, response, "FATAL! Item of index "+rows+" already exists!");
						return;
					}
					destinationFile.createNewFile();
					itemImage.write(destinationFile);
					log("FILE SAVED TO::: "+destinationFile.getAbsolutePath());
				}
				catch (Exception e)
				{
					error(getServletContext(), request, response, e.getMessage());
				}
				response.setContentType("text/html");
				response.sendRedirect("/totilingua/dictionary?lang=en&index="+rows);
			}
			else
			{
				error(getServletContext(), request, response, "Invalid count of parameters!");
				return;
			}
		}
		catch (FileUploadException e)
		{
			error(getServletContext(), request, response, e.getMessage());
			return;
		}
	}
}
