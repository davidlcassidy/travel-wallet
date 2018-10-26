##Before Git Commit

#####Update App build.gradle

 - Increment versionCode by 1
 - Increment versionName appropriately
 
#####Update CHANGELOG.md

 - Add release log header with versionCode from build.gradle and date
 - Add release log details with desciptions of source code changes
 
#####Update copyright comments

 - In Code menu, click the Update Copyright option to update all file copyright comments
 - Run git command: git checkout -- app/src/main/java/com/android/
 
##After Git Commit
 
#####Create new Github release

 - Create new release versionCode from build.gradle and details from release details from CHANGELOG.md