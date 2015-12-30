package main.webapp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils {
	public static String getBIfAIsNull(String a,String b){
    	return a==null?b:a;
    }
	/**Forwards errorsite.jsp and sets attributes
	 * @throws IOException 
	 * @throws ServletException */
	public static void forwardToErrorSite(ServletContext cntxt, HttpServletRequest request, HttpServletResponse response,String errorMessage,String returnURL) throws ServletException, IOException{
		request.getSession().setAttribute("returnLink", returnURL);
		request.getSession().setAttribute("errorMessage", errorMessage);
		cntxt.getRequestDispatcher("/errorsite.jsp").forward(request, response);
	}
	/**Also kilobyte to byte*/
	public static int  megaToKiloByte(int megabytes){
		return megabytes*1000;
	}
	public static int  megaToByte(int megabytes){
		return megaToKiloByte(megaToKiloByte(megabytes));
	}
}
