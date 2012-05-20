
git-svn-gatekeeper
=================

This project creates a sample-setup with the following:
- A Subversion repository that is considered the master VCS
- A Git-bare repository for git devs for push/pull
- A git-svn-gatekeeper that pulls changes from the bare and commits them to the subversion repository

And you can test it all with test-scripts

This setup require.

1. Apache HTTP Server, found at: http://httpd.apache.org/
2. Apache configured with mod-modules for making Apache a subversion server, e.g. from: http://sourceforge.net/projects/win32svn/
3. Groovy, in order to run test scripts: http://groovy.codehaus.org/


Extend the Apache httpd.conf with the following:

<Location /svn-repos>
  DAV svn
  SVNParentPath %WDIR%\apache-httpd-svn-repos
  SVNListParentPath on
  AuthType Basic
  AuthName "Subversion repository"
  AuthUserFile %WDIR%\apache-httpd-svn-repos\svn-auth-file
  Require valid-user
</Location>

You need to replace %WDIR% with the value you set this property in the environment.

