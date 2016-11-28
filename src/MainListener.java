import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainListener {

	public static void main(String[] args) throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		 String url = "http://fit.cvut.cz";
		 HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
		 connection.setRequestMethod("GET");
		 int code = connection.getResponseCode();
		 System.out.println(code);
	}

}
