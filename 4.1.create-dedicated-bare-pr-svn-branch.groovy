// Groovy ant-script


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
}

def script = new ScriptCommands("E:/tmp/mult_bare/ant");

script.git( ".","svn", "clone", "--prefix", "svn/kaksi/", "http://localhost/svn-repos/myrepo/branches/kaksi", "kaksi.git.gatekeeper", "--username", "adm");
script.git( "kaksi.git.gatekeeper", "branch", "-m", "master", "kaksi");
script.create_dir( "kaksi_bare" )
script.git( "kaksi_bare", "init", "--bare" )
script.git( "kaksi.git.gatekeeper","remote" ,"add", "bare_repo", "../kaksi_bare" )
script.git( "kaksi.git.gatekeeper", "push" ,"-u", "bare_repo", "kaksi" )

script.git( ".", "clone", "kaksi_bare", "dev" )
script.git( "dev","checkout", "-t", "remotes/origin/kaksi")