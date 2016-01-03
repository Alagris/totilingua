package main.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadFromDataDir
 */
@WebServlet("/download/*")
public class DownloadFromDataDir extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadFromDataDir()
	{
		super();
	}

	private static final int BUFFER_LENGTH = 4096;

	/**
	 * @param pathRelativeToDataStorageLocation
	 *            - should not start with file separator
	 */
	public static String getURLtoDataThroughThisServlet(String pathRelativeToDataStorageLocation)
	{
		return "/totilingua/download/" + pathRelativeToDataStorageLocation;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		String filePath = ServerFileManager.getDataStorageLocation(getServletContext()) + request.getRequestURI().replace("/totilingua/download", "");
		if (filePath.contains("/../"))
		{
			Utils.forwardToErrorSite(getServletContext(), request, response, "Forbidden path", "/totilingua/index.html");
			return;
		}
		File file = new File(filePath);
		if (!file.isFile())
		{
			Utils.forwardToErrorSite(getServletContext(), request, response, "File does not exist!\n" + file.getPath(), "/totilingua/index.html");
			return;
		}
		InputStream input = new FileInputStream(file);

		response.setContentLength((int) file.length());
		response.setContentType(new MimetypesFileTypeMap().getContentType(file));

		OutputStream output = response.getOutputStream();
		byte[] bytes = new byte[BUFFER_LENGTH];
		int read = 0;
		while ((read = input.read(bytes, 0, BUFFER_LENGTH)) != -1)
		{
			output.write(bytes, 0, read);
			output.flush();
		}

		input.close();
		output.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

}
