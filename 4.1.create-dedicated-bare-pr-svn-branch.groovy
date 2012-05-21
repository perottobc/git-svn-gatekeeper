println( "SETUP: One bare pr branch" )

class ScriptCommands {

	def String rootDir; 	
	def ant = new AntBuilder();
	
	public ScriptCommands( String _rootDir ) {
	   this.rootDir = _rootDir;
	}

	def git(String dir, String command,String ... arguments) {
		def gitWorkingDir = rootDir + "/" + dir;		
		println("GIT: " + command + " " + arguments + ", gitWorkingDir = " + gitWorkingDir );		
		ant.exec(executable:"git",dir:gitWorkingDir, resultproperty:"cmdExit") {
			arg(value:command)	
			for( argument in arguments ) { 
				arg(value:argument)
			}
		}
		if( "0" != "${ant.project.properties.cmdExit}" ) throw new RuntimeException("Error executing ant command: " + command );
		return this;						
	}
	
	def create_dir( String dir ) {
		ant.mkdir( dir: rootDir + "/" + dir )
	}
	
	
	def setup_gatekeeper_and_bare(String branchName, String svnUrl ) {
		
		def bareRepo = branchName + "_bare"
		def gatekeeper = branchName +".git.gatekeeper";
		
		git( ".","svn", "clone", "--prefix", "svn/"+branchName+"/", svnUrl, gatekeeper, "--username", "adm");
		git( gatekeeper, "branch", "-m", "master", branchName );
		
		create_dir( bareRepo )
		git( bareRepo, "init", "--bare" )
		git( gatekeeper,"remote" ,"add", "bare_repo", "../" + bareRepo )
		git( gatekeeper, "push" ,"-u", "bare_repo", branchName )
		
		//just for testing
		def devRepo = "dev_" + branchName;		
		git( ".", "clone", bareRepo, devRepo  )
		git( devRepo,"checkout", "-t", "remotes/origin/" + branchName )	
	}
	
}

def script = new ScriptCommands("E:/tmp/mult_bare/ant");
/*
script.setup_gatekeeper_and_bare("kaksi", "http://localhost/svn-repos/myrepo/branches/kaksi")
script.setup_gatekeeper_and_bare("yksi", "http://localhost/svn-repos/myrepo/branches/yksi")
script.setup_gatekeeper_and_bare("trunk", "http://localhost/svn-repos/myrepo/trunk")
*/
/*
script.setup_gatekeeper_and_bare("kaksi", "http://localhost/svn-repos/company-repo/websites/branches/kaksi")
script.setup_gatekeeper_and_bare("yksi", "http://localhost/svn-repos/company-repo/websites/branches/yksi")
script.setup_gatekeeper_and_bare("trunk", "http://localhost/svn-repos/company-repo/websites/trunk/")
*/



