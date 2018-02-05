import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class Parse_redmine { 

	public static void main(String[] args) throws IOException{
		Document doc;
		Elements searchResult;
		int currentPage = 1;
		String mode = "EVT";
//		String[] projectList = { "gs-gse-ec-for-synccloud","gs-fully-test"};
		String[] projectList = { "gs-gse-ec-for-synccloud"};
		

		
		String version = "2.5.d";
		
//		if(StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1]) || StringUtils.isBlank(args[2])){
//			System.out.println("請輸入正確參數格式!");
//			return;
//		}
//		if( !(args[0].equals("EVT")  || args[0].equals("DVT")) ){
//			System.out.println("請輸入正確參數格式!");
//			return;
//		}
//		mode = args[0];
//		version = args[1];
//		projectList = args[2].split(":");
		
		int latestVersionLength = 65536;
		String latestVersion = "";
		int[] queryVersionIdList;
		int issueCount = 0;
		int issueTotal = 0;
		ArrayList<String> DVT_fixed = new ArrayList<String>();
		ArrayList<String> EVT_fixed = new ArrayList<String>();
		ArrayList<String> QMS = new ArrayList<String>();
		ArrayList<String> Spec = new ArrayList<String>();
		ArrayList<String> NotBug = new ArrayList<String>();
		ArrayList<String> Limit = new ArrayList<String>();
		ArrayList<String> Waive = new ArrayList<String>();
		ArrayList<String> CannotDuplicate = new ArrayList<String>();
		ArrayList<String> Unknow = new ArrayList<String>();
		ArrayList<String> unResolvedList = new ArrayList<String>();
		
		RedmineCtrlSoup redmineCtrlSoup = new RedmineCtrlSoup();
		redmineCtrlSoup.login("Yichun.Lin", "Alanlin0411!");

		for(String project:projectList){		
			doc = redmineCtrlSoup.connect("http://pms.infortrend/projects/"+project+"/settings");
			searchResult = doc.select("tr:contains("+version+")");

			if(latestVersion.length()==0){
				for(Element row :searchResult){
		            Elements columns = row.select("td");
		            if(columns.get(0).text().length() < latestVersionLength){
		            	latestVersionLength = columns.get(0).text().length();
		            	latestVersion = columns.get(0).text();
		            }
				}
				System.out.println("latestVersion : "+latestVersion);
			}
			
			System.out.print("query Version : ");
			if(mode.equals("EVT")){
				searchResult = searchResult.select("tr:contains("+latestVersion+")");
				Elements columns = searchResult.first().select("a");
				queryVersionIdList = new int[1];
				queryVersionIdList[0] = Integer.parseInt(columns.toString().substring(columns.toString().indexOf("/versions/")+10, columns.toString().indexOf("\">")));
				System.out.print(columns.text().substring(0,columns.text().indexOf(" ")));
			}
			else{
				String latestRootVersion = latestVersion.substring(0,latestVersion.indexOf("_"));
				searchResult = searchResult.select("tr:contains("+latestRootVersion+")").select("tr:not(:contains("+latestVersion+"))");
				queryVersionIdList = new int[searchResult.size()];
				int count = 0;
				for(Element row:searchResult){
					Elements columns = row.select("a");
					if(!columns.text().substring(0,columns.text().indexOf("_")).equals(latestRootVersion))
						continue;
					queryVersionIdList[count++] = Integer.parseInt(columns.toString().substring(columns.toString().indexOf("/versions/")+10, columns.toString().indexOf("\">")));
					System.out.print(columns.text().substring(0,columns.text().indexOf(" "))+", ");
				}
			}
			System.out.println();
			
				issueCount = 0;
				currentPage = 1;
				do{
					doc = redmineCtrlSoup.connect(getQueryUrl(project, issueCount, queryVersionIdList));
					searchResult = doc.select(".pagination");
			        if(searchResult.size()>0){
			        	issueTotal = Integer.parseInt(searchResult.text().substring(searchResult.text().indexOf("/")+1, searchResult.text().indexOf(")")));
				        searchResult = doc.select("tr");

				        for(Element row :searchResult){
				            Elements columns = row.select("td");
//				          1 BugNo, 2 Status, 3 Priority, 4 Subject, 5 Assignee, 6 Upadted, 7 Target Version, 8 Fixed in Version, 9 Resolution 10 category, 11 tracker 12.testScope 13. project
				            if(columns.size()<13)
				            	continue;
				            
				            if(mode.equals("DVT")){
				            	if(!(columns.get(4).text().contains("[DVT]")||columns.get(11).text().contains("QMS")||columns.get(10).text().contains("Feature"))||  //DVT, QMS, Feature都撈 
				            		 columns.get(2).text().equals("Feedback")||  
				            		 columns.get(2).text().equals("Closed")||
				            		 columns.get(2).text().equals("Reviewing")||
				            		 columns.get(2).text().equals("Maintaining")||
				            		 columns.get(2).text().equals("Pending")||
				            		 columns.get(2).text().equals("In Progress")
				            		 )//Feedback, Closed, Reviewing, Pending, In Progress不撈
				            	{
				            		//--if條件成立則不撈此issue--//				            		1
				            		issueCount++;
					            	continue;
				            	}
				            }else if(mode.equals("EVT")){
				            	if(!columns.get(2).text().equals("Confirmed")){
				            		issueCount++;
					            	continue;
				            	}
				            }

				            if(columns.get(9).text().equals("Fixed")){
				    			if(columns.get(4).text().contains("DVT")){
				    				DVT_fixed.add(getIssueStr(columns, mode));
				    			}else if(columns.get(11).text().contains("QMS")){
				    				QMS.add(getIssueStr(columns, mode));
				    			}else{
				    				EVT_fixed.add(getIssueStr(columns, mode));
				    			}
				    				
				    		}
				    		else if(columns.get(9).text().equals("Specification")){
				    			Spec.add(getIssueStr(columns, mode));
				    		}
				    		else if(columns.get(9).text().equals("Not Bug")){
				    			NotBug.add(getIssueStr(columns, mode));
				    		}
				    		else if(columns.get(9).text().equals("Limitation")){
				    			Limit.add(getIssueStr(columns, mode));
				    		}
				    		else if(columns.get(9).text().equals("Waive")){
				    			Waive.add(getIssueStr(columns, mode));
				    		}
				    		else if(columns.get(9).text().equals("Cannot Duplicate")){
				    			CannotDuplicate.add(getIssueStr(columns, mode));
				    		}
				    		else{
				    			Unknow.add(getIssueStr(columns, mode));
				    		}
				            
				            issueCount++;
				        }
				        if(issueCount>issueTotal)
				        	System.out.println("ERROR!!!! The total issues count is not match!!!!!!");
				        currentPage++;
			        }
			        else
			        	break;
				}while(issueCount<issueTotal);
		}
		if("EVT".equals(mode)){
			unResolvedList = printfUnResolve(redmineCtrlSoup, projectList,  version, latestVersion);
		}
		
		int totalIssue = 0;
		if(DVT_fixed.size()>0){
//			System.out.println("[Fixed Bugs DVT]\n");
	        for(String issue:DVT_fixed)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=DVT_fixed.size();
		}
		if(QMS.size()>0){
			System.out.println("[QMS]\n");
	        for(String issue:QMS)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=QMS.size();
		}
		if(EVT_fixed.size()>0){
//			System.out.println("[Fixed Bugs EVT]\n");
	        for(String issue:EVT_fixed)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=EVT_fixed.size();
		}
		if(Spec.size()>0){
			System.out.println("[Spec]\n");
	        for(String issue:Spec)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=Spec.size();
		}
		if(NotBug.size()>0){
			System.out.println("[Not Bug]\n");
	        for(String issue:NotBug)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=NotBug.size();
		}
		if(Limit.size()>0){
			System.out.println("[Limitation]\n");
	        for(String issue:Limit)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=Limit.size();
		}
		if(Waive.size()>0){
			System.out.println("[Waive]\n");
	        for(String issue:Waive)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=Waive.size();
		}
		if(CannotDuplicate.size()>0){
			System.out.println("[Cannot Duplicate]\n");
	        for(String issue:CannotDuplicate)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=CannotDuplicate.size();
		}
		if(Unknow.size()>0){
			System.out.println("[Unknow]\n");
	        for(String issue:Unknow)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=Unknow.size();
		}
		if(unResolvedList.size()>0){
			System.out.println("[previous fixed bugs, wait for verify]\n");
	        for(String issue:unResolvedList)
	        	System.out.println(issue);
	        System.out.println();
	        totalIssue+=unResolvedList.size();
		}
		
		System.out.println("totalIssue : "+totalIssue);
	}
	
	public static ArrayList<String> printfUnResolve(RedmineCtrlSoup redmineCtrlSoup, String[] projectList, String version, String latestVersion) throws IOException{
		int currentPage = 1;
		String mode = "unResolved";
		Document doc;
		int latestVersionLength = 65536;
		int[] queryVersionIdList;
		int issueCount = 0;
		int issueTotal = 0;
		ArrayList<String> unResolvedList = new ArrayList<String>();
		
		for(String project:projectList){
			doc = redmineCtrlSoup.connect("http://pms.infortrend/projects/"+project+"/settings");
			Elements searchResult = doc.select(".name");
			//resolved-------------------------------------------
			List<String> domNodeDvtStrList = new ArrayList<String>();
			for(Element element : searchResult){
				if( !element.text().startsWith(version) || element.text().equals(latestVersion)){
					continue;
				}
				Element aElement = element.select("a").get(0);
				String aElementHtml = aElement.toString();
				domNodeDvtStrList.add(aElementHtml.substring(aElementHtml.indexOf("/versions/")+10, aElementHtml.indexOf("\">")));
			}
			queryVersionIdList = new int[domNodeDvtStrList.size()];
			for(int i = 0; i < domNodeDvtStrList.size(); i++){
				queryVersionIdList[i] = Integer.parseInt(domNodeDvtStrList.get(i));
			}
				issueCount = 0;
				currentPage = 1;
				do{
					doc = redmineCtrlSoup.connect( getQueryUrl(project, currentPage, queryVersionIdList) );
					Elements paginations = doc.select(".pagination");
			        if(paginations.size()>0){
			        	issueTotal = Integer.parseInt(paginations.text().substring(paginations.text().indexOf("/")+1, paginations.text().indexOf(")")));
			        	Elements trs = doc.select("tr");
			        	
			        	 for(Element tr :trs){
					            Elements columns = tr.select("td");
					            if(columns.size()<14){
					            	continue;
					            }
					            if(!columns.get(2).text().equals("Confirmed")){//is not feedback
					            	issueCount++;
						            continue;
					            }
					            unResolvedList.add(getIssueStr(columns, mode));
					            issueCount++;
						        if(issueCount>issueTotal){
						        	System.out.println("ERROR!!!! The total issues count is not match!!!!!!");
						        }
						        currentPage++;
			        	 }
			        }else{
			        	break;
			        }
				}while(issueCount<issueTotal);
		}
		return unResolvedList;
	}
	
	static String getIssueStr(Elements columns, String mode){
		String result = "#"+columns.get(1).text()+"\t"+columns.get(4).text();//+"\n  "+"(RD: "+columns.get(5).asText()+", "+columns.get(13).asText()+")";
		if("EVT".equals(mode)|| "unResolved".equals(mode)){
			return result;
		}
		if(StringUtils.isNotBlank(columns.get(12).text())){
			return result+
					"\n  (testScope:\n"+columns.get(12).text()+")\n";
		}else{
			return result;
		}
	}
	static String getQueryUrl(String project, int currentPage, int versionId){
		int[] is = new int[1];
		is[0] = versionId;
		return getQueryUrl(project, currentPage, is);
	}
	
	static String getQueryUrl(String project, int currentPage, int[] versionIds){
		String url= "http://pms.infortrend/projects/"+project+"/issues?c[]=status&c[]=priority&c[]=subject&c[]=assigned_to&c[]=updated_on&c[]=fixed_version&c[]=cf_7&c[]=cf_8&f[]=cf_7&f[]=&c[]=category&c[]=tracker&c[]=cf_19&c[]=project&c[]=tags&"+
				"group_by=&op[cf_7]==&page="+currentPage+"&per_page=100&"+
				"set_filter=1&"+
				"utf8=✓&"+
				"f[]=cf_7&op[cf_7]==&";
		for(int i: versionIds){
			url=url+"v[cf_7][]="+i+"&";
		}
		url+="f[]=&";
		return url;
	}
}
