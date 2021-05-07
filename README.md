# clij2k

This is a hack which makes it easier to run JetBrains' java-to-kotlin conversion (nj2k) on a file from the command line.

It works by wrapping a call to the conversion inside an inspection, since inspections can be run readily from the command line.

To use this tool on a file of your choice:
1. Install the plugin in Intellij or Android Studio and restart your IDE
2. Close your IDE (you can only have one instance--headless or otherwise--of the IDE running at a time, and since CLI inspections use a headless instance...)
3. Run this command with your own values substituted for `PROJECT_PATH` and `DIRECTORY_TO_CONVERT`: 
``` /Applications/Android\ Studio.app/Contents/bin/inspect.sh PROJECT_PATH PROJECT_PATH/java/com/facebook/tools/intellij/kotlinator/resources/profiles/Kotlinator_Inspection_Profile.xml PROJECT_PATH/.idea/inspectionResults -v2 -d DIRECTORY_TO_CONVERT ```
