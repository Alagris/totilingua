package main.webapp;

import static main.webapp.Utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.AccessException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

public class ServerFileManager
{
	//////////////////////////////
	/////// Static file accessing methods
	//////////////////////////////
	public static String getDataStorageLocation(ServletContext context)
	{
		return getBIfAIsNull(System.getenv("OPENSHIFT_DATA_DIR"), context.getRealPath("data"));
	}

	public static String getTemporaryDataStorageLocation(ServletContext context)
	{
		return getBIfAIsNull(System.getenv("OPENSHIFT_TMP_DIR"), context.getRealPath("tmp"));
	}

	public static String getFilePath(ServletContext context, String relativePath)
	{
		return getDataStorageLocation(context) + File.separatorChar + relativePath;
	}

	public static String getFilePath(ServletContext context, DataDirectories dir, String fileName)
	{
		return getDataStorageLocation(context) + File.separatorChar + dir.path + File.separatorChar + fileName;
	}

	public static String getTmpFilePath(ServletContext context, String relativePath)
	{
		return getTemporaryDataStorageLocation(context) + File.separatorChar + relativePath;
	}

	public static File getFile(ServletContext context, String relativePath)
	{
		return new File(getFilePath(context, relativePath));
	}

	public static File getFile(ServletContext context, DataDirectories dir, String fileName)
	{
		return new File(getFilePath(context, fileName));
	}

	public static File getTmpFile(ServletContext context, String relativePath)
	{
		return new File(getTmpFilePath(context, relativePath));
	}
	//////////////////////////////
	/////// File accessing methods
	//////////////////////////////

	public File getFile(String path)
	{
		return getFile(context, path);
	}

	public File getTmpFile(String path)
	{
		return getTmpFile(context, path);
	}

	////////////////////
	////// Error shortened
	////////////////////
	/** Redirects to error page */
	public void error(String errorMessage) throws ServletException, IOException
	{
		Utils.forwardToErrorSite(context, request, response, errorMessage, URLtoRedirectInCasOfError);
	}

	////////////////////
	////////// variables
	////////////////////

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final String URLtoRedirectInCasOfError;
	private final ServletContext context;

	////////////////////
	////////// Constructor
	////////////////////
	public ServerFileManager(ServletContext context, HttpServletRequest request, HttpServletResponse response, String URLtoRedirectInCasOfError)
	{
		this.request = request;
		this.response = response;
		this.URLtoRedirectInCasOfError = URLtoRedirectInCasOfError;
		this.context = context;
	}

	////////////////////
	///// Verification methods
	////////////////////
	public void verifyIP(String errorMessage) throws AccessException
	{
		if (!AccessVerifier.checkIP(request)) { throw new AccessException(errorMessage); }
	}

	public void verifyIfMultipart(String errorMessage) throws Exception
	{
		if (!ServletFileUpload.isMultipartContent(request)) { throw new Exception(errorMessage); }
	}

	public ServletFileUpload prepareUpload(int maxSize)
	{
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(maxSize, getTmpFile("")));
		upload.setSizeMax(maxSize);
		return upload;
	}

	public void verifyNumberOfSentFileItems(List<FileItem> items, int expectedCount, String errorMessage) throws Exception
	{
		if (items.size() != expectedCount) { throw new Exception(errorMessage); }
	}

	public void verifyFileExists(File f, String errorMessage) throws FileNotFoundException
	{
		if (!f.exists()) { throw new FileNotFoundException(errorMessage); }
	}

	public void verifyFileNotExists(File f, String errorMessage) throws Exception
	{
		if (f.exists()) { throw new Exception(errorMessage); }
	}

	////////////////////
	///// parsing methods
	////////////////////
	public FileItem findFile(List<FileItem> items, String fieldName, String errorMessage) throws NoSuchFieldException
	{
		for (FileItem i : items)
		{
			if (!i.isFormField() && i.getFieldName().equals(fieldName)) { return i; }
		}
		throw new NoSuchFieldException(errorMessage);
	}

	public String findValue(List<FileItem> items, String fieldName, String errorMessage) throws NoSuchFieldException
	{
		for (FileItem i : items)
		{
			if (i.isFormField() && i.getFieldName().equals(fieldName)) { return i.getString(); }
		}
		throw new NoSuchFieldException(errorMessage);
	}

	////////////////////
	///// writing methods
	////////////////////
	public void writeFile(FileItem item, String path, boolean doNotOverwrite) throws Exception
	{
		write(item, getFile(path), doNotOverwrite);
	}

	public void writeTmpFile(FileItem item, String path, boolean doNotOverwrite) throws Exception
	{
		write(item, getTmpFile(path), doNotOverwrite);
	}

	private void write(FileItem item, File destination, boolean doNotOverwrite) throws Exception
	{
		if (doNotOverwrite)
		{
			verifyFileNotExists(destination, destination.getName() + " already exists!");
		}
		File parent = destination.getParentFile();
		if (!parent.exists())
		{
			parent.mkdirs();
		}
		destination.createNewFile();
		context.log("FILE SAVED TO::: " + destination.getAbsolutePath());
		item.write(destination);
	}
}
