import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RedmineController {
	private WebClient webClient;
	private String projectName;
	
	public RedmineController() {
		this.webClient = new WebClient(BrowserVersion.CHROME);
		this.projectName="gs2016p2";
	}
	public WebClient getWebClient(){
          return webClient;
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public void close(){
		this.webClient.close();
	}
	public void loginRedmine(String inputName,String inputPassword){
		try {
			HtmlPage htmlPage=(HtmlPage) webClient.getPage("http://pms.infortrend/login");
			DomElement userName = htmlPage.getElementById("username");
			userName.setAttribute("value", inputName);
			DomElement passWord = htmlPage.getElementById("password");
			passWord.setAttribute("value", inputPassword);
			DomElement login = htmlPage.getElementByName("login");
			login.click();
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
	}
	public void addVersion(String versionName) {//ex: searchVersionName("2.3.d.06")
		String url="http://pms.infortrend/projects/"+projectName+"/versions/new?back_url=";
		
		try {
			HtmlPage htmlPage = (HtmlPage) webClient.getPage(url);
			DomElement version_name = htmlPage.getElementById("version_name");
			version_name.setAttribute("value", versionName);
			DomElement createVersionBtn = htmlPage.getElementByName("commit");
			int statusCode=createVersionBtn.click().getWebResponse().getStatusCode();
			if(statusCode==200)
			{
				System.out.println("add version "+versionName);
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String searchVersionName(String versionName){//ex: searchVersionName("2.3.d.06")
		String resultStr=null;
		try {
			HtmlPage settingPage = webClient.getPage("http://pms.infortrend/projects/"+projectName+"/settings");
			DomElement domElement;
			List<?> resultList=settingPage.getByXPath("//*[contains(text(),'"+versionName+"')]");
			if(resultList.size() != 0)
			{
				domElement=(DomElement) resultList.get(0);
			}else{
				return "can not find this verison";
			}
			resultStr=domElement.getTextContent();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultStr;
	}
	
	public void showVersionName(String versionName){//ex: searchVersionName("2.3.d.06")
		try {
			HtmlPage settingPage = webClient.getPage("http://pms.infortrend/projects/"+projectName+"/settings");
			
			List<?> resultList=settingPage.getByXPath("//*[contains(text(),'"+versionName+"')]");
			if(resultList.size() != 0)
			{
				for(int i=0;i<resultList.size();i++){
					DomElement domElement=(DomElement) resultList.get(i);
					System.out.println(domElement.getTextContent());
				}
				
			}else{
				System.out.println("cant not find any version");
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isVersionExist(String versionName){//ex: searchVersionName("2.3.d.06")
		boolean exist=false;
		try {
			HtmlPage settingPage = webClient.getPage("http://pms.infortrend/projects/"+projectName+"/settings");
			List<?> resultList=settingPage.getByXPath("//*[contains(text(),'"+versionName+"')]");
			if(resultList.size() != 0)
			{
				exist=true;
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exist;
	}
	public void editVersion(String sourceVersionName,String targetVersionName){
		try {
			//setting version page 
			final HtmlPage settingPage=webClient.getPage("http://pms.infortrend/projects/"+projectName+"/settings");
//			DomNodeList tagList=settingPage.getElementById("tab-content-versions").getElementsByTagName("a");
			DomElement a=(DomElement) settingPage.getByXPath("//*[contains(text(),'"+sourceVersionName+"')]").get(0);
			String url="http://pms.infortrend"+a.getAttribute("href")+"/edit";
			final HtmlPage finalPage=webClient.getPage(url);
			DomElement inputText=finalPage.getElementById("version_name");
			inputText.setAttribute("value", targetVersionName);
			DomElement savsBtn=finalPage.getElementByName("commit");
			int statusCode=savsBtn.click().getWebResponse().getStatusCode();
			if(statusCode==200)
			{
				System.out.println("Edit version from "+sourceVersionName+" to "+targetVersionName);
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
