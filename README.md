To config the local server url, see app/build.gradle. To produce unsigned release for prod.:

$ SELB_SERVER_URL="http://159.65.65.75/" ./gradlew assembleDebug

Note the last "/" in the above url. You can use installDebug to assemble&install.
