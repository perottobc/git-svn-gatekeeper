class GitRepo {
	def String repoDir; 	
	def ant = new AntBuilder();
	
	def mkdir() {
		ant.mkdir( dir: repoDir )
	}
	
	def git(String command,String ... arguments) {				
		println("GIT: " + command + " " + arguments + ", repoDir = " + repoDir );		
		ant.exec(executable:"git",dir:repoDir, resultproperty:"cmdExit") {
			arg(value:command)	
			for( argument in arguments ) { 
				arg(value:argument)
			}
		}
		if( "0" != "${ant.project.properties.cmdExit}" ) throw new RuntimeException("Error executing ant command: " + command );
		return this;						
	}
}


class Executables {
	def String rootDir; 	
	def ant = new AntBuilder();
	
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
}

class GatekeeperSetup extends Executables {

	def setup_gatekeeper_and_bare(String branchName, String svnUrl ) {
		
		def bareRepo = branchName + "_bare"
		def gatekeeper = branchName +".git.gatekeeper";
		
		git( ".","svn", "clone", "--prefix", "svn/"+branchName+"/", svnUrl, gatekeeper, "--username", "adm");
		git( gatekeeper, "branch", "-m", "master", branchName );
				
		ant.mkdir( dir: rootDir + "/" + bareRepo )
		
		git( bareRepo, "init", "--bare" )
		git( gatekeeper,"remote" ,"add", "bare_repo", "../" + bareRepo )
		git( gatekeeper, "push" ,"-u", "bare_repo", branchName )		
	}	
}

class DevSetup {
	String devDir;
	String bareReposDir;
	
	def create_dev(String dev ) {	
		def repo = new GitRepo( repoDir : devDir + "/" + dev + "/git_websites" )		
		repo.mkdir() 	
		repo.git( "init" )		
		repo.git( "config","user.name", dev )
		repo.git( "config","user.email", dev +"@doit.com" )
						
		for ( branch in ["trunk","yksi","kaksi"] ) {
		    repo.git( "remote", "add",branch+"_bare", bareReposDir + "/"+branch+"_bare" )
		    repo.git( "fetch", branch+"_bare" )
		    repo.git( "checkout", "-t", branch+"_bare" + "/" + branch )
		}				 	
	}	
}

def devScript = new DevSetup(devDir:"E:/tmp/mult_bare/devs",bareReposDir:"E:/tmp/mult_bare/");
/*
def gatekeeperSetup = new GatekeeperSetup(rootDir:"E:/tmp/mult_bare/");
gatekeeperSetup.setup_gatekeeper_and_bare("kaksi", "http://localhost/svn-repos/company-repo/websites/branches/kaksi")
gatekeeperSetup.setup_gatekeeper_and_bare("yksi", "http://localhost/svn-repos/company-repo/websites/branches/yksi")
gatekeeperSetup.setup_gatekeeper_and_bare("trunk", "http://localhost/svn-repos/company-repo/websites/trunk/")


for ( dev in ["per","siv","ola"] ) {
	devScript.create_dev( dev )
}
*/
devScript.create_dev( "kar4" )
