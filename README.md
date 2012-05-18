
git-svn-gatekeeper
=================

This project creates a sample-setup with the following:
- A subversion repository that is considered the master VCS
- A git-bare repository for git devs for push/pull
- A git-svn-gatekeeper that pulls changes from the bare and commits them to the subversion repository

This setup require.

1. Apache HTTP Server installed: http://httpd.apache.org/
2. Apache configured with mod-modules for making Apache a subversion server, e.g. from: http://sourceforge.net/projects/win32svn/
3. Groovy, in order to run test scripts: http://groovy.codehaus.org/
