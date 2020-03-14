## Change Log

### v2.3.2 (2018-05-21)

[JENKINS-50603](https://issues.jenkins-ci.org/browse/JENKINS-50603) Fix
for bug where renaming a build makes it favorite for everyone  
[JENKINS-48136](https://issues.jenkins-ci.org/browse/JENKINS-48136) Job
name encoding fix  
Add Chinese translation
(<https://github.com/jenkinsci/favorite-plugin/pull/19>)

### v2.3.1 (2017-10-20)

-   [JENKINS-44797](https://issues.jenkins-ci.org/browse/JENKINS-44797) User
    config page broken with "ConversionException: No value specified for
    'Class'"

### V2.3.0 (2017-06-06)

-   Fix security issue, see
    <https://jenkins.io/security/advisory/2017-06-06/>

### V2.2.0 (2017-06-05)

-   Fix security issue, see
    <https://jenkins.io/security/advisory/2017-06-06/>

### V2.0.4

-   [JENKINS-39990](https://issues.jenkins-ci.org/browse/JENKINS-39990) Fixes
    bug where the settings page becomes unusable 

###   V2.0.3

-   Updates to improve for Jenkins Classic theming

###   V2.0.2

-   Fix for ABI compatibility

###   V2.0.1

-   Favourites API returned unfavourited items

###   V2.0 @ 2016-11-04

-   Introduce new public API for manipulating Favorite state
-   Internal UserProperty data structure changed from List to Map to
    allow checking if something has been unfavorited.
-   Moved item listener to a new package

###   V1.16 @ 2013-07-05

-   Fix issue with links in folders.

###    V1.15 @ 2013-07-03

-   [JENKINS-18604](https://issues.jenkins-ci.org/browse/JENKINS-18604):
    Fix for recursive views in Jenkins 1.512+

###    V1.14 @ 2013-05-29

-   Support Cloudbees Folders plugin
-   Use PNG instead of GIF images
-   Prevent use for Matrix configurations

###    V1.13 @ 2012-09-11

-   Fix problem with project page action when "Display Name" is used.

###    V1.12 @ 2012-09-10

-   Fixing favorite link on project pages where the root URL was
    something other than just /

###    V1.10 @ 2012-09-07

-   Column sorting will now work correctly.

###    V1.9 @ 2012-06-21

-   Use a [Token
    Macro](https://wiki.jenkins.io/display/JENKINS/Token+Macro+Plugin)
    of ${FAVORITE\_USERS\_EMAIL} that will expand to a list of users who
    have marked that job a favorite. This can be used in the
    [Email-ext](https://wiki.jenkins.io/display/JENKINS/Email-ext+plugin) plugin
-   Fix issue for unauthenticated users sometimes seeing all jobs on a
    "Favorites" view.

###    V1.8 @ 2012-06-19

-   Prevent NullPointerException in some cases during startup

###    V1.7 @ 2012-05-18

-   Encode Project names for URLs

###    V1.6 @ 2011-11-29

-   Japanese translation
-   Ability to add/remove favorites from job page
-   Update to Jenkins & GitHub

###    V1.5 @ 2010-10-27

-   Fixed issue when saving user, a null pointer exception was thrown.

###    V1.4 @ 2010-09-14

-   Add ItemListener to automatically delete or rename favorites from
    users that have that job as a favorite.

###    V1.3 @ 2010-09-07

-   Fix for
    ([JENKINS-7398](https://issues.jenkins-ci.org/browse/JENKINS-7398))

###    V1.2 @ 2010-09-03

-   Allow you from the user configuration page to see favorites, and
    remove them. Admins will be able to use this feature as well.

###    V1.1 @ 2010-09-01

-   Fixed a few bugs when no favorites existed for a user.

###    V1.0 @ 2010-08-31

-   initial release
