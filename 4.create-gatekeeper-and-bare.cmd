call check_env_var.cmd
if "%ENV_CHECKED%" == "OK" GOTO DOIT
@echo Check environment variables that must be set
Exit /b

:DOIT
@echo on
mkdir %WDIR%\devs\adm

@echo ----------------------------------
@echo 1. Clone Subversion repo
cd %WDIR%\devs\adm
call git svn clone -s --prefix=svn/ http://localhost/svn-repos/company-repo/websites --username adm

cd %WDIR%\devs\adm\websites

REM XXXXXXXXXXXXXXXXXXXXXXXXXXXX
call git reset --hard svn/trunk

@echo ----------------------------------
@echo  2. Create bare repo
cd %WDIR%\devs\adm
mkdir websites.git
cd websites.git
call git init --bare 

@echo ----------------------------------
@echo  3. Populate bare with content from gatekeeper 
cd %WDIR%\devs\adm\websites

REM XXXXXXXXXXXXXXXXXXXXXXXXXXXX
call git push --all ../websites.git


call git push ../websites.git "refs/remotes/svn/*:refs/heads/svn/*"
  

@echo ----------------------------------
@echo  4. Setup bare as a remote in gatekeeper  
cd %WDIR%\devs\adm\websites
call git remote add bare_repo ../websites.git


@echo -----------------------------------
@echo 5. Create svn tracking branches in gatekeeper
call git checkout -t svn/trunk
call git checkout -t svn/yksi
call git checkout -t svn/kaksi


