##Before Git Commit

#####Update App build.gradle

 - Increment versionCode by 1
 - Increment versionName appropriately
 
#####Update CHANGELOG.md

 - Add release log header with versionCode from build.gradle and date
 - Add release log details with desciptions of source code changes
 
#####Update copyright comments

 - From Android Studio Code menu, click the Update Copyright option to update all file copyright comments
 
#####Confirm/Update Github user
 - Run git command: git config --global user.name "davidlcassidy"
 - Run git command: git config --global user.email "david@davidlcassidy.com"
 
##After Git Commit
 
#####Create new Github release

 - Create new release using versionCode and details from CHANGELOG.md