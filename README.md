# Popular Movies

This repository has source code for Udacity Nanodegree Program project **Popular Movies, Stage 1 & Stage 2**.

This is extended from project "Popular Movies, Stage 1" and the stage 2 starts from commit https://github.com/rajanikantdeshmukh/Popular-Movies/tree/9b1e4234facceda59e138979db7efa91499fef06.

## Steps to import project

1. Clone the repository
```
git clone https://github.com/rajanikantdeshmukh/Popular-Movies.git
```
2. Open Android Studio and select **Import Project**
  ![Android Studio Welcome Screen](/screenshots/android_studio.png)
3. Navigate to path where the cloned respository exists import project
  ![Android Studio Import Screen](/screenshots/android_studio_import.png)
4. After importing project, open gradle.properties file. You will see this line at end of file:
  ```
  TMDBApiToken="/** ADD YOUR TMDB API TOKEN HERE **/"
  ```
  Replace the string by your own API key. The key can be obtained from TMDb site. For APIs' documentation, visit http://docs.themoviedb.apiary.io

5. Build the project and run it.
  ![App Screenshot](/screenshots/app_screenshot.png)
