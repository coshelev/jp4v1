import java.io.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class A{


	//private static final Logger logger = LogManager.getLogger("App");
	private static final Logger LOG = LoggerFactory.getLogger(A.class);

	public static String AUTOBROKERMAIL_HOST     = "";
	public static String AUTOBROKERMAIL_LOGIN    = "";
	public static String AUTOBROKERMAIL_PASSWORD = "";

	public static void main(String[] args){
		

	LOG.info("{}; jvm {}",  "My example for info ", System.getProperty("java.version"));		
	LOG.warn("This is my example for warning");
	LOG.info("LOG.isDebugEnabled()= {}",LOG.isDebugEnabled());
	LOG.debug("doStop {}", "This is exapmle for debug");

	if (LOG.isDebugEnabled())
            LOG.debug("{}", "Debug enabled");

	   Gson gson= new Gson();

	   String buildinSettings = "{\"autobrokerMail.host\":\"imap.yandex.ru\", \"autobrokerMail.login\":\"luidorexpertALL\", \"autobrokerMail.password\":\"cfirdrfnabxyzmgk\",\"autobrokerMail.fileFound\":\"false\"}";

                HashMap<String, String> map = gson.fromJson(buildinSettings, HashMap.class);
		
		try{ 	
			String filename = "myapp-1.1_settings1.json";
			File f = new File(filename);
                	if (!f.exists())
                        	System.out.printf("settings file %1 does not exist \n", filename);
			if  (f.exists()){
				JsonReader reader = new JsonReader(new FileReader(filename));
    				map = gson.fromJson(reader, HashMap.class);	
				System.out.printf( "map to string: %s \n", gson.toJson(map));
				};
		}
		catch (Exception e) {
			System.out.print("exception!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		};

		System.out.printf("gson.toJson(map)=%s \n", gson.toJson(map));
		System.out.printf("map.toString() = %s \n",map.toString());

		AUTOBROKERMAIL_HOST     = map.get("autobrokerMail.host");
		AUTOBROKERMAIL_LOGIN    = map.get("autobrokerMail.login");
		AUTOBROKERMAIL_PASSWORD = map.get("autobrokerMail.password");
		LOG.info(" AUTOBROKERMAIL: HOST = {}; LOGIN = {}; PASSWORD = {}", AUTOBROKERMAIL_HOST, AUTOBROKERMAIL_LOGIN, AUTOBROKERMAIL_PASSWORD);
		
		LOG.info("{}", " * class A before jetty *");
		
		//Start webserever
		JettyServer server = new JettyServer();
		try {
        		server.start();
        	} catch (Exception e) {           
            		e.printStackTrace();
        	}

		return;  
	}
}