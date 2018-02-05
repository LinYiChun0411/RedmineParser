
public class versionEdit {

	public static void main(String[] args) {
		String[] projectList ={"gs-gse-ec-for-synccloud","gs-fully-test"};
		String fromVersion="2.5.d.02_1211";
		String toVersion="2.5.d.02_12111714";
		String newversion="2.5.d.02_1215";
		
		for(String project : projectList){
			
			RedmineController redmineController2=new RedmineController();
			redmineController2.loginRedmine("Yichun.Lin","Alanlin0411!");
			redmineController2.setProjectName(project);
			redmineController2.editVersion(fromVersion, toVersion);
			redmineController2.editVersion("e"+fromVersion, "e"+toVersion);
			redmineController2.addVersion(newversion);
			redmineController2.addVersion("e"+newversion);
//			
			redmineController2.close();
		}
		


	}

}
