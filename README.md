# Firebase-And-Maps-People
This is created with testing porpouses.

Firebase remote config is not fetching data when we use the Maps Indoors SDK 3.5.3 but it actully works with version 3.1.4-beta1
This project is already set up. 
For making bug works comment line 69 and uncomment line 68 in app:build.gradle
For making app works perfectly comment line 68 and uncomment line 69 in app:build.gradle

When opening the app remote config should be fetched.
When Clicking on "FETCH CONFIG" button it will update a tex in the label at center of screen. Hello world from remote (Fetched from remote) or Hello world from local (default value in xml).
