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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailServlet extends HttpServlet {
   private String greeting="Hello World from MailServlet";
   private static final Logger LOG = LoggerFactory.getLogger(MailServlet.class);

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

      private void readlpartsMail(){

         final String host = A.LPARTSMAIL_HOST;  
	      final String user = A.LPARTSMAIL_LOGIN;
         final String pass = A.LPARTSMAIL_PASSWORD;

         try {
           
            Properties props = new Properties();
            props.put("mail.debug", "false");
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props);

            Store store = session.getStore();
            store.connect(host, user, pass);

            Folder inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY);
            
            final FromTerm fromTerm  = new FromTerm(new InternetAddress("info@l.parts"));
                      
            var a = java.time.LocalDate.now().minusDays(15);
            Date  receivedDate = java.sql.Date.valueOf(a);
            LOG.info("receivedDate = {}", receivedDate);
            final ReceivedDateTerm received  = new ReceivedDateTerm(ComparisonTerm.GT, receivedDate);
               
            final var foundMessages = inbox.search(received);  
            LOG.info(" ***** foundMessages = {}", foundMessages.length);

            for (var i: foundMessages){
               LOG.info("{}","***********************************************************************************")
            
               //Filter by sender
               if (i.match(fromTerm)!=true) continue;

               readLPartsMessage(i);
            }
          
          } catch (Exception e) { 
		  e.printStackTrace();}

   }

   private void readLPartsMessage(Message i){

      try {
      MimeMessage m = (MimeMessage) i;
      String messageId = m.getMessageID();
      messageId = messageId.replace("<", "");
      messageId = messageId.replace(">", "");
              
      Multipart mp = (Multipart) i.getContent();
      int count = mp.getCount();
      for (int j = 0; j < count; j++) {
         BodyPart bodyPart = mp.getBodyPart(j);
         if (bodyPart.isMimeType("text/plain")) {
            /*
            result = result + "\n" + bodyPart.getContent();
            break; 
            */ 
            // without break same text appears twice in my tests
            }    
         else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            LOG.info("html= {}", html);
            readLPartsMessageText(html, messageId);
            } 
         else if (bodyPart.getContent() instanceof MimeMultipart){
            //result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
         }
      } catch(Exception e) { 
		   e.printStackTrace();}       
   }

   private void readLPartsMessageText(String html, String messageId){

      try {    
      Document doc   = Jsoup.parse(html);
      String text    = doc.body().text();  
      LOG.info("{}", text);

	   //Pattern p = Pattern.compile("Телефон.*:.*((8|\\+7)[\\- ]?)?(9\\(?\\d\\d\\d\\)?[\\- ]?)?[\\d\\- ]{7,10}\\d\\d");
      Pattern p = Pattern.compile("Телефон.*:.*((8|\\+7))");
		Matcher mr = p.matcher(text);
		boolean hasPhone = mr.matches();
	   System.out.println(hasPhone);
      if (!hasPhone){
         LOG.info("text for pattern not found");
         return;};

      LOG.info("text for pattern found");
 
      Gson gson= new Gson();
      Map<String, String> inputMap = new HashMap<String, String>();
      inputMap.put("type",          "lparts mail message");
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
      }
      catch(Exception e) { 
		   e.printStackTrace();}  

   }
}
