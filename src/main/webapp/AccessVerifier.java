package main.webapp;

import javax.servlet.http.HttpServletRequest;

public class AccessVerifier
{

	public static boolean checkCode(String code)
	{
		return code != null && code.equals("Q#2^mwD`\\sV9-Q<K^}Zt'Mu'mM_-gM");// Q#2^mwD`\sV9-Q<K^}Zt'Mu'mM_-gM
	}

	public static boolean checkIP(HttpServletRequest request)
	{
		return checkIP(request.getRemoteAddr());
	}
	
	public static boolean checkIP(String ip)
	{
		switch (ip)
		{
			case "77.242.234.122":
			case "127.0.0.1":
				// case "": //other IPs..
				return true;

		}
		return false;
	}

}
