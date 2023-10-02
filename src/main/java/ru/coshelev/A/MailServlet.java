import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.*;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.search.*;
import jakarta.mail.internet.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.net.URI;
import java.net.http.*;
import java.util.regex.*;

public class MailServlet extends HttpServlet {
    private String greeting="Hello World from MailServlet";
    public MailServlet(){}
    public MailServlet(String greeting){
        this.greeting=greeting;
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>"+greeting+"</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
        
        //readAutobrokerMail();
        readlpartsMail();
    }
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
	   StringBuffer jb = new StringBuffer();
  	   String line = null;
      String json = "";
   	try {
    	} catch (Exception e) { /*report an error*/ }     
   } 
   
   private void readAutobrokerMail(){

        final String host = A.AUTOBROKERMAIL_HOST;  
	    final String user = A.AUTOBROKERMAIL_LOGIN;
        final String pass = A.AUTOBROKERMAIL_PASSWORD;

        try {
           
            String strg = A.AUTOBROKERMAIL_HOST;
	        System.out.printf("strg = %s", strg);
		 
            Properties props = new Properties();
            props.put("mail.debug", "false");
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props);

            Store store = session.getStore();
            store.connect(host, user, pass);

            Folder inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY);
            
            final FromTerm fromTerm  = new FromTerm(new InternetAddress("no_reply@ab-club.ru"));
	        final FromTerm fromTerm1  = new FromTerm(new InternetAddress("no-reply@bibika.ru"));
                      
            var a = java.time.LocalDate.now().minusDays(1);
            Date  receivedDate = java.sql.Date.valueOf(a);
            System.out.println("receivedDate = " + receivedDate);
            final ReceivedDateTerm received  = new ReceivedDateTerm(ComparisonTerm.GT, receivedDate);
               
            final var foundMessages       = inbox.search(received);  
            System.out.println(" ***** foundMessages = "+foundMessages.length); 

            for (var i: foundMessages){
                System.out.println("********************");
               
               //Filter by sender
               if ((i.match(fromTerm) || i.match(fromTerm1) )!=true) continue;
        
               MimeMessage m = (MimeMessage) i;
               String messageId = m.getMessageID();
               messageId = messageId.replace("<", "");
               messageId = messageId.replace(">", "");
              
               //Multipart mp1 = (Multipart) i.getContent();
               String mp1 = (String) i.getContent();
	       
               Document doc = Jsoup.parse(mp1);
               String text = doc.body().text();  
               System.out.println("text = "+text);

	        System.out.println("regex start..");
	       Pattern p = Pattern.compile(".+([0-9][0-9][0-9]).+");
		   Matcher mr = p.matcher(text);
		boolean hasPhone = mr.matches();
	       	System.out.println(hasPhone);
               if (!hasPhone){
                    System.out.println("text for pattern .+([0-9][0-9][0-9]).+ not found");
                    return;};
	 	System.out.println("pattern ([0-9][0-9][0-9]) found");
              
		Pattern ptrn1 = Pattern.compile(".+([0-9][0-9][0-9]).+[0-9].+[0-9].+[0-9].+[0-9].+");
		Matcher mr1   = ptrn1.matcher(text);
		boolean hasPhone1 = mr1.matches();
		if (!hasPhone1){
                    System.out.println("text for pattern .+([0-9][0-9][0-9]).+[0-9].+[0-9].+[0-9].+[0-9].+ not found");
                    return;};
		System.out.println("pattern ([0-9][0-9][0-9]) found");
		
               int SendWebhook = 1;
               if (SendWebhook == 1){ 
               Gson gson= new Gson();
               Map<String, String> inputMap = new HashMap<String, String>();
               inputMap.put("type",          "autobroker mail message");
               inputMap.put("messageID",     messageId);
               inputMap.put("messageBody",   text);
               String requestBody = gson.toJson(inputMap);
                      
               var client = HttpClient.newHttpClient();
               var request = HttpRequest.newBuilder()
                  .uri(URI.create("http://mainappl.main.luidorauto.ru/sys_agr/hs/webhooks/anypost/v1"))
                  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                  .header("accept", "application/json") 
                  .build();
              
               client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
               };
            }
          
          } catch (Exception e) { 
		  e.printStackTrace();}
   }

      private void rreadlpartsMail(){

        final String host = A.AUTOBROKERMAIL_HOST;  
	    final String user = A.AUTOBROKERMAIL_LOGIN;
        final String pass = A.AUTOBROKERMAIL_PASSWORD;

        try {
           
            String strg = A.AUTOBROKERMAIL_HOST;
	        System.out.printf("strg = %s", strg);
		 
            Properties props = new Properties();
            props.put("mail.debug", "false");
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props);

            Store store = session.getStore();
            store.connect(host, user, pass);

            Folder inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY);
            
            final FromTerm fromTerm  = new FromTerm(new InternetAddress("no_reply@ab-club.ru"));
	        final FromTerm fromTerm1  = new FromTerm(new InternetAddress("no-reply@bibika.ru"));
                      
            var a = java.time.LocalDate.now().minusDays(1);
            Date  receivedDate = java.sql.Date.valueOf(a);
            System.out.println("receivedDate = " + receivedDate);
            final ReceivedDateTerm received  = new ReceivedDateTerm(ComparisonTerm.GT, receivedDate);
               
            final var foundMessages       = inbox.search(received);  
            System.out.println(" ***** foundMessages = "+foundMessages.length); 

            for (var i: foundMessages){
                System.out.println("********************");
               
               //Filter by sender
               if ((i.match(fromTerm) || i.match(fromTerm1) )!=true) continue;
        
               MimeMessage m = (MimeMessage) i;
               String messageId = m.getMessageID();
               messageId = messageId.replace("<", "");
               messageId = messageId.replace(">", "");
              
               //Multipart mp1 = (Multipart) i.getContent();
               String mp1 = (String) i.getContent();
	       
               Document doc = Jsoup.parse(mp1);
               String text = doc.body().text();  
               System.out.println("text = "+text);

	        System.out.println("regex start..");
	       Pattern p = Pattern.compile(".+([0-9][0-9][0-9]).+");
		   Matcher mr = p.matcher(text);
		boolean hasPhone = mr.matches();
	       	System.out.println(hasPhone);
               if (!hasPhone){
                    System.out.println("text for pattern .+([0-9][0-9][0-9]).+ not found");
                    return;};
	 	System.out.println("pattern ([0-9][0-9][0-9]) found");
              
		Pattern ptrn1 = Pattern.compile(".+([0-9][0-9][0-9]).+[0-9].+[0-9].+[0-9].+[0-9].+");
		Matcher mr1   = ptrn1.matcher(text);
		boolean hasPhone1 = mr1.matches();
		if (!hasPhone1){
                    System.out.println("text for pattern .+([0-9][0-9][0-9]).+[0-9].+[0-9].+[0-9].+[0-9].+ not found");
                    return;};
		System.out.println("pattern ([0-9][0-9][0-9]) found");
		
               int SendWebhook = 1;
               if (SendWebhook == 1){ 
               Gson gson= new Gson();
               Map<String, String> inputMap = new HashMap<String, String>();
               inputMap.put("type",          "autobroker mail message");
               inputMap.put("messageID",     messageId);
               inputMap.put("messageBody",   text);
               String requestBody = gson.toJson(inputMap);
                      
               var client = HttpClient.newHttpClient();
               var request = HttpRequest.newBuilder()
                  .uri(URI.create("http://mainappl.main.luidorauto.ru/sys_agr/hs/webhooks/anypost/v1"))
                  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                  .header("accept", "application/json") 
                  .build();
              
               client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
               };
            }
          
          } catch (Exception e) { 
		  e.printStackTrace();}

   }


    
   
}