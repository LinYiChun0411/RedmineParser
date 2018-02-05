import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class RedmineCtrlSoup {
	Response response = null;
	Document doc = null;
	public Map<String, String> login(String username, String password) throws IOException {
        String url = "http://pms.infortrend/login";
        response = Jsoup
                .connect(url)
                .method(Connection.Method.GET)
                .execute();
        Document responseDocument = response.parse();
        Element loginUtf8 = responseDocument.select("input[name=utf8]").first();
        Element loginToken = responseDocument.select("input[name=authenticity_token]").first();
        Element loginLogin = responseDocument.select("input[name=login]").first();
        response = Jsoup.connect(url)
                .cookies(response.cookies())
                .data("utf8",loginUtf8.attr("value") )
			    .data("authenticity_token", loginToken.attr("value"))
			    .data("username", "Yichun.Lin")
			    .data("password", "Alanlin0411!")
			    .data("login", loginLogin.attr("value"))
			    .userAgent("Mozilla")
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();
        return response.cookies();
	}
	public Map<String, String> getCookie(){
		return response.cookies();
	}
	
	public Document connect(String url) throws IOException{
		doc = Jsoup.connect(url).cookies( response.cookies() ).get();
		return doc;
	}
	
	public Document getDocument(){
		return doc;
	}
}
