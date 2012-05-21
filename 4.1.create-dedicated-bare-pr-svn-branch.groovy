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

class GatekeeperSetup {
	String gatekeeperDir

	def setup_gatekeeper_and_bare(String branchName, String svnUrl ) {		
		def gatekeeper = new GitRepo( repoDir : gatekeeperDir + "/" + branchName +".git.gatekeeper" )
		gatekeeper.mkdir()
		gatekeeper.git( "svn", "init", "--prefix", "svn/"+branchName+"/", svnUrl, "--username", "adm");
		gatekeeper.git( "svn", "fetch" ) 		
		gatekeeper.git( "branch", "-m", "master", branchName );
		
		def bare = new GitRepo( repoDir : gatekeeperDir + "/" + branchName + "_bare" ) 
		bare.mkdir();
		bare.git( "init", "--bare" )
		
		gatekeeper.git( "remote" ,"add", "bare_repo", bare.repoDir )
		gatekeeper.git( "push" ,"-u", "bare_repo", branchName )		
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


String wdir = System.getenv()['WDIR'];

def gatekeeperSetup = new GatekeeperSetup(gatekeeperDir: wdir + "/devs/adm" );
gatekeeperSetup.setup_gatekeeper_and_bare("kaksi", "http://localhost/svn-repos/company-repo/websites/branches/kaksi")
gatekeeperSetup.setup_gatekeeper_and_bare("yksi", "http://localhost/svn-repos/company-repo/websites/branches/yksi")
gatekeeperSetup.setup_gatekeeper_and_bare("trunk", "http://localhost/svn-repos/company-repo/websites/trunk/")

def devScript = new DevSetup(devDir: wdir + "/devs",bareReposDir: wdir + "/devs/adm" );
for ( dev in ["per","siv","ola"] ) {
	devScript.create_dev( dev )
}
