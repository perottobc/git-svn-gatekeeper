// Groovy ant-script


println( "SETUP: One bare pr branch" )

class ScriptCommands {	
	def ant = new AntBuilder();

	def git(String gitWorkingDir, String command,String ... arguments) {		
		println("GIT: " + command + " " + arguments );		
		ant.exec(executable:"git",dir:gitWorkingDir,resultproperty:"cmdExit") {
			arg(value:command)	
			for( argument in arguments ) { 
				arg(value:argument)
			}
		}
		if( "0" != "${ant.project.properties.cmdExit}" ) throw new RuntimeException("Error executing ant command: " + command );
		return this;						
	}
	
	def create_dir( String parent, String dir ) {
		ant.mkdir( dir: parent + "/" + dir )
	}
}

def script = new ScriptCommands();

def mydir = "E:/tmp/mult_bare/ant"

script.git( mydir,"svn", "clone", "--prefix", "svn/kaksi/", "http://localhost/svn-repos/myrepo/branches/kaksi", "kaksi.git.gatekeeper", "--username", "adm");
//script.git( mydir + "/kaksi.git.gatekeeper", "branch", "-m", "master", "kaksi");
script.create_dir( mydir, "kaksi_bare" )
script.git( mydir + "/kaksi_bare", "init", "--bare" )
script.git( mydir + "/kaksi.git.gatekeeper","remote" ,"add", "bare_repo", "../kaksi_bare" )
script.git( mydir + "/kaksi.git.gatekeeper", "push" ,"-u", "bare_repo", "master" )

script.git( mydir, "clone", "kaksi_bare", "dev" )