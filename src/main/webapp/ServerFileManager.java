package main.webapp;

import static main.webapp.Utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.AccessException;
import java.util.Arrays;
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
	
	/** Path ends with file separator */
	public static String getDataStorageLocation(ServletContext context)
	{
		return getBIfAIsNull(System.getenv("OPENSHIFT_DATA_DIR"), context.getRealPath("data" )+ File.separatorChar);
	}

	/** Path ends with file separator */
	public static String getTemporaryDataStorageLocation(ServletContext context)
	{
		return getBIfAIsNull(System.getenv("OPENSHIFT_TMP_DIR"), context.getRealPath("tmp" )+ File.separatorChar);
	}

	/**
	 * @param relativePath
	 *            - should start without file separator
	 */
	public static String getFilePath(ServletContext context, String relativePath)
	{
		return getDataStorageLocation(context) + relativePath;
	}

	/**
	 * @param relativePath
	 *            - should start without file separator
	 */
	public static String getTmpFilePath(ServletContext context, String relativePath)
	{
		return getTemporaryDataStorageLocation(context) + File.separatorChar + relativePath;
	}

	/**
	 * @param relativePath
	 *            - should start without file separator
	 */
	public static File getFile(ServletContext context, String relativePath)
	{
		return new File(getFilePath(context, relativePath));
	}


	/**
	 * @param relativePath
	 *            - should start without file separator
	 */
	public static File getTmpFile(ServletContext context, String relativePath)
	{
		return new File(getTmpFilePath(context, relativePath));
	}
	//////////////////////////////
	/////// File accessing methods
	//////////////////////////////

	/**
	 * @param path
	 *            - should start without file separator
	 */
	public File getFile(String path)
	{
		return getFile(context, path);
	}


	/**
	 * @param path
	 *            - should start without file separator
	 */
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
		if (items.size() != expectedCount) { throw new Exception(errorMessage+"(items count="+items.size()+")"); }
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

	/**Will never return null*/
	public String findValue(List<FileItem> items, String fieldName, String errorMessage) throws NoSuchFieldException
	{
		for (FileItem i : items)
		{
			if (i.isFormField() && i.getFieldName().equals(fieldName)) { return i.getString(); }
		}
		throw new NoSuchFieldException(errorMessage);
	}

	/**Will never return null*/
	public String[] findArray_ignoreEmptyFields(List<FileItem> items, String errorMessage,String... fieldNames) throws NoSuchFieldException
	{
		String[] values = new String[fieldNames.length];
		int j=0, i = 0;
		for (;i<values.length;i++)
		{
			values[j]=findValue(items, fieldNames[i], errorMessage);
			if(!values[j].equals(""))j++;
		}
		
		return i==j? values : Arrays.copyOfRange(values, 0, j);
	}
	
	/**Will never return null*/
	public String[] findArray(List<FileItem> items, String errorMessage,String... fieldNames) throws NoSuchFieldException
	{
		String[] values = new String[fieldNames.length];
		for (int i = 0;i<values.length;i++)
		{
			values[i]=findValue(items, fieldNames[i], errorMessage);
		}
		return values;
	}
	////////////////////
	///// writing methods
	////////////////////


	/**
	 * @param path
	 *            - should start without file separator
	 */
	public void writeFile(FileItem item, String path, boolean doNotOverwrite) throws Exception
	{
		write(item, getFile(path), doNotOverwrite);
	}

	/**
	 * @param path
	 *            - should start without file separator
	 */
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
		item.write(destination);
	}
}
