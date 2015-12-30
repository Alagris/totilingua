package main.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static main.webapp.Utils.*;

public class MySqlManager {
	//////////
	///private
	//////////
	private String getDatabaseUsername(){
    	return getBIfAIsNull(System.getenv("OPENSHIFT_MYSQL_DB_USERNAME"),"admin9JeMdDK");
    }
    private String getDatabasePassword(){
    	return getBIfAIsNull(System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD"),"hrlhCL_7Kf1h");
    }
    private String getDatabaseHost(){
    	return getBIfAIsNull(System.getenv("OPENSHIFT_DB_HOST"),"localhost");//127.0.0.1
    }
    private String getDatabasePort(){
    	return getBIfAIsNull(System.getenv("OPENSHIFT_DB_PORT"),"3306");
    }
    /**It is also name of database to be used*/
    private String getAppName(){
    	return getBIfAIsNull(System.getenv("OPENSHIFT_APP_NAME"),"totilingua");
    }
    private String combineIntoURL(String DB_HOST,String DB_PORT,String APP_NAME){
    	return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + APP_NAME;
    }
	////////////////////
	///variables//////////
	////////////////////
    private String USERNAME,PASSWORD,DB_HOST,DB_PORT ,APP_NAME ,DB_CONN,databaseTable;
    private Connection connection;
    
	////////////////////
	///get&set//////////
	////////////////////
    public String getUSERNAME() {
		return USERNAME;
	}
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getDatabaseTable()
	{
		return databaseTable;
	}
	public void setDatabaseTable(String databaseTable)
	{
		this.databaseTable = databaseTable;
	}
	public String getPASSWORD() {
		return PASSWORD;
	}
	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}
	public String getDB_HOST() {
		return DB_HOST;
	}
	public void setDB_HOST(String dB_HOST) {
		DB_HOST = dB_HOST;
	}
	public String getDB_PORT() {
		return DB_PORT;
	}
	public void setDB_PORT(String dB_PORT) {
		DB_PORT = dB_PORT;
	}
	/**It is also name of database to be used*/
	public String getAPP_NAME() {
		return APP_NAME;
	}
	/**It is also name of database to be used*/
	public void setAPP_NAME(String aPP_NAME) {
		APP_NAME = aPP_NAME;
	}
	public String getDB_CONN() {
		return DB_CONN;
	}
	public void setDB_CONN(String dB_CONN) {
		DB_CONN = dB_CONN;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
	////////////////////
	///public///////////
	////////////////////
	public Connection connect(){
		return connect("Languages");
	}
	public Connection connect(String defaultDatabase){
    	USERNAME = getDatabaseUsername();
		PASSWORD = getDatabasePassword();
		DB_HOST = getDatabaseHost();
		DB_PORT = getDatabasePort();
		APP_NAME = getAppName();
		DB_CONN = combineIntoURL(DB_HOST,DB_PORT,APP_NAME);
		databaseTable=defaultDatabase;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(DB_CONN , USERNAME , PASSWORD);
			return connection;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }
    public void printState(OutputStream stream) throws IOException{
    	stream.write(toString().getBytes());
    }
    public String toString(){
    	return "USERNAME="+USERNAME+
	    		"\nPASSWORD="+PASSWORD+
	    		"\nDB_HOST ="+DB_HOST+
	    		"\nDB_PORT ="+DB_PORT+
	    		"\nAPP_NAME="+APP_NAME+
	    		"\nDB_CONN ="+DB_CONN ;
    }
    
	////////////////////
	///SQL commands/////
	////////////////////
    
    
	/**Index starts with 1*/
    public String sqlSelectWord(int index, LanguageTags languageTag){
    	return "SELECT `"+languageTag.tag+"` FROM `"+databaseTable+"` WHERE `index`="+index;
    }
    /**Index starts with 1*/
    public String sqlSelectWord(int index, String languageTag){
    	return "SELECT `"+languageTag+"` FROM `"+databaseTable+"` WHERE `index`="+index;
    }
    
    public String sqlInsertRow(String[] values,LanguageTags[] languageTags){
    	if(values.length!=languageTags.length || values.length==0)return null;
    	String command = "INSERT INTO `"+databaseTable+"` (`index`,`"+languageTags[0].tag+"`";
    	for(int i = 1;i<languageTags.length;i++){
    		command+=",`"+languageTags[i].tag+"`";
    	}
    	command+=") VALUES (NULL,";
    	for(int i = 0;i<values.length-1;i++){
    		command+="'"+values[i]+"',";
    	}
    	return command+"'"+values[values.length-1]+"')";
    }
    
    public String sqlInsertRow(String[] values,String[] columns){
    	if(values.length!=columns.length || values.length==0)return null;
    	String command = "INSERT INTO `"+databaseTable+"` (`"+columns[0]+"`";
    	for(int i = 1;i<columns.length;i++){
    		command+=",`"+columns[i]+"`";
    	}
    	command+=") VALUES (";
    	for(int i = 0;i<values.length-1;i++){
    		command+="'"+values[i]+"',";
    	}
    	return command+"'"+values[values.length-1]+"')";
    }
    
    public String sqlCountRows(){
    	return "SELECT COUNT(*) FROM `"+databaseTable+"`";
    }
}
