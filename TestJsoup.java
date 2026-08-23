import java.net.HttpURLConnection;
import java.net.URL;

public class TestJsoup {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://mangalik.net/?s=solo&post_type=wp-manga");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        System.out.println("Response Code: " + conn.getResponseCode());
        
        URL url2 = new URL("https://mangalik.net/?s=solo");
        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
        conn2.setRequestProperty("User-Agent", "Mozilla/5.0");
        System.out.println("Response Code 2: " + conn2.getResponseCode());
    }
}
