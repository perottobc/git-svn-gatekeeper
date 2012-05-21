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
		
	}
		
	def setup_gatekeeper_and_bare(String branchName, String svnUrl ) {
		
		def bareRepo = branchName + "_bare"
		def gatekeeper = branchName +".git.gatekeeper";
		
		git( ".","svn", "clone", "--prefix", "svn/"+branchName+"/", svnUrl, gatekeeper, "--username", "adm");
		git( gatekeeper, "branch", "-m", "master", branchName );
				
		ant.mkdir( dir: rootDir + "/" + bareRepo )
		
		git( bareRepo, "init", "--bare" )
		git( gatekeeper,"remote" ,"add", "bare_repo", "../" + bareRepo )
		git( gatekeeper, "push" ,"-u", "bare_repo", branchName )dir		
	}
	
	def create_dev(String dev ) {		
		ant.mkdir( dir: rootDir + "/" + dev )
		git( dev, "init" )		
		git( dev, "config","user.name", dev )
		git( dev, "config","user.email", dev +"@doit.com" )
						
		for ( branch in ["trunk","yksi","kaksi"] ) {
		    git( dev, "remote", "add",branch+"_bare", "../"+branch+"_bare" )
		    git( dev, "fetch", branch+"_bare" )
		    git( dev, "checkout", "-t", branch+"_bare" + "/" + branch )
		}				 	
	}
	
}

def script = new ScriptCommands("E:/tmp/mult_bare/ant");

script.setup_gatekeeper_and_bare("kaksi", "http://localhost/svn-repos/company-repo/websites/branches/kaksi")
script.setup_gatekeeper_and_bare("yksi", "http://localhost/svn-repos/company-repo/websites/branches/yksi")
script.setup_gatekeeper_and_bare("trunk", "http://localhost/svn-repos/company-repo/websites/trunk/")

for ( dev in ["per","siv","ola"] ) {
	script.create_dev( dev )
}
