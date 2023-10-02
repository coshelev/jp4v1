import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletHandler;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import org.eclipse.jetty.servlet.FilterHolder;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.DefaultHandler;


public class JettyServer {

	private Server server;

	public void start() throws Exception {
		System.out.print("Starting jetty...");

	    Server server = new Server(8680);

	    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

	    FilterHolder filterHolder = new FilterHolder(org.eclipse.jetty.servlets.CrossOriginFilter.class);
 	    filterHolder.setInitParameter("allowedOrigins", "*");
	    context.addFilter(filterHolder, "/*", null);

        server.setHandler(context);
 
        context.addServlet(new ServletHolder(new HelloServlet()),"/*");
        context.addServlet(new ServletHolder(new HelloServlet("Buongiorno Mondo")),"/it/*");
        context.addServlet(new ServletHolder(new HelloServlet("Bonjour le Monde")),"/fr/*");
	
	    //Read autobroker mail
	    context.addServlet(new ServletHolder(new MailServlet("mail")),"/mail/*");	

            try {
        	    server.start();
        	    server.dumpStdErr();
                server.join();
        	} catch (Exception e) {           
            	e.printStackTrace();
		    System.out.println("exception on server.start()");
            };
   	}
}