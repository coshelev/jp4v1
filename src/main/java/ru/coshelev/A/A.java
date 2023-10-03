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

    public static String LPARTSMAIL_HOST     = "";
	public static String LPARTSMAIL_LOGIN    = "";
	public static String LPARTSMAIL_PASSWORD = "";

	public static void main(String[] args){
		

	    LOG.info("jvm {}",  System.getProperty("java.version"));		
	    LOG.warn("This is my example for warning");
	    LOG.info("LOG.isDebugEnabled()= {}",LOG.isDebugEnabled());
	    LOG.debug("doStop {}", "This is example for debug");

	    if (LOG.isDebugEnabled())
            LOG.debug("{}", "Debug enabled");

	    Gson gson= new Gson();
	    String AUTOBROKERMAILSettings = "{\"autobrokerMail.host\":\"imap.yandex.ru\", \"autobrokerMail.login\":\"luidorexpertALL\", \"autobrokerMail.password\":\"cfirdrfnabxyzmgk\",\"autobrokerMail.fileFound\":\"false\"}";
        HashMap<String, String> map = gson.fromJson(AUTOBROKERMAILSettings, HashMap.class);
	
		try{ 	
			String filename = "AUTOBROKERMAIL.json";
			File f = new File(filename);
        	if (!f.exists())
				LOG.info("settings file {} does not exist", filename);
			if  (f.exists()){
				JsonReader reader = new JsonReader(new FileReader(filename));
    			map = gson.fromJson(reader, HashMap.class);	
				LOG.info("map to string: {} \n", gson.toJson(map));
				};
		}
		catch (Exception e) {
			LOG.error("{}", e.getMessage());
			e.printStackTrace();
		};

		//System.out.printf("gson.toJson(map)=%s \n", gson.toJson(map));
		//System.out.printf("map.toString() = %s \n",map.toString());

		AUTOBROKERMAIL_HOST     = map.get("autobrokerMail.host");
		AUTOBROKERMAIL_LOGIN    = map.get("autobrokerMail.login");
		AUTOBROKERMAIL_PASSWORD = map.get("autobrokerMail.password");
		LOG.info(" AUTOBROKERMAIL: HOST = {}; LOGIN = {}; PASSWORD = {}", AUTOBROKERMAIL_HOST, AUTOBROKERMAIL_LOGIN, AUTOBROKERMAIL_PASSWORD);

        ///////////////////////////////////////////////////////////////

        String LPARTSMAILSettings = "{\"lparts.host\":\"imap.yandex.ru\", \"lparts.login\":\"lparts-leads\", \"lparts.password\":\"yzlbyihxbpyqoqov\",\"lparts.fileFound\":\"false\"}";
        HashMap<String, String> map1 = gson.fromJson(LPARTSMAILSettings, HashMap.class);
	
		try{ 	
			String filename = "LPARTS.json";
			File f = new File(filename);
            if (!f.exists())
				LOG.info("settings file {} does not exist", filename);
			if  (f.exists()){
				JsonReader reader = new JsonReader(new FileReader(filename));
    			map1 = gson.fromJson(reader, HashMap.class);	
				LOG.info("map to string: {} \n", gson.toJson(map1));
				};
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			LOG.error("{}", e.getMessage());
			e.printStackTrace();
		};

		//System.out.printf("gson.toJson(map)=%s \n", gson.toJson(map));
		//System.out.printf("map.toString() = %s \n",map1.toString());

		LPARTSMAIL_HOST     = map1.get("lpart.host");
		LPARTSMAIL_LOGIN    = map1.get("lpart.login");
		LPARTSMAIL_PASSWORD = map1.get("lpart.password");
		LOG.info(" LPARTS: HOST = {}; LOGIN = {}; PASSWORD = {}", LPARTSMAIL_HOST, LPARTSMAIL_LOGIN, LPARTSMAIL_PASSWORD);

		LOG.info("{}", " * class A before jetty *");
		JettyServer server = new JettyServer();
		try {
        		server.start();
        	} catch (Exception e) {           
            		e.printStackTrace();
        	}

		return;  
	}
}
