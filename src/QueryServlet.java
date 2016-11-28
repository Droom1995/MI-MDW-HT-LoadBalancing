import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weblogic.servlet.annotation.WLServlet;
 
@WLServlet (
        name = "QueryServlet", mapping = {"/queryServlet"}
)
public class QueryServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	ArrayList<String> serverNames;
	ArrayList<Integer> serverResponses;
	int round;
	
	public QueryServlet(){
		serverNames = new ArrayList<String>();
		serverNames.add("http://147.32.233.18:8888/MI-MDW-LastMinute1/list");
		serverNames.add("http://147.32.233.18:8888/MI-MDW-LastMinute2/list");
		serverNames.add("http://147.32.233.18:8888/MI-MDW-LastMinute3/list");
		serverResponses = new ArrayList<Integer>();
		for(int i=0;i<3;i++)
			serverResponses.add(200);
		new Thread(){
			public void run(){
				while(true){
					for(int i=0;i<serverNames.size();i++){
						String url = serverNames.get(i);
						HttpURLConnection connection;
						int code = 500;
						try {
							connection = (HttpURLConnection) (new URL(url)).openConnection();
							connection.setRequestMethod("GET");
							code = connection.getResponseCode();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						synchronized(serverResponses){
							serverResponses.set(i, code);
						}
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		round = 0;
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // url
		String url = null;
		synchronized(serverResponses){
			for(int i=0;i<serverResponses.size();i++){
				if(serverResponses.get((round+i)%serverResponses.size()) == 200){
					url = serverNames.get((round+i)%serverResponses.size());
					round = (round + i + 1)%serverResponses.size();
					break;
				}
			}
		}
        HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
        
        // HTTP method
        connection.setRequestMethod("GET");
        // copy headers
        Collections.list(request.getHeaderNames()).forEach(head -> connection.setRequestProperty(head, request.getHeader(head)));
        // copy body
        BufferedReader inputStream = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        //ServletOutputStream sout = response.getOutputStream();
        PrintWriter out;
		out = response.getWriter();
        out.write("Redirected to url: " + url + " " + round +"\n");
        while ((inputLine = inputStream.readLine()) != null) {
            out.write(inputLine);
        }
        // close
        inputStream.close();
        out.flush();
    }
}
