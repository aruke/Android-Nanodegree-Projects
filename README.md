# UAND-P2

This repository has source code for Udacity Nanodegree Program project **Popular Movies, Stage 2**.

The initial commit to this repos is part of project "Popular Movies, Stage 1" and has same code that of commit https://github.com/rajanikantdeshmukh/UAND-P1/tree/a130166d4e5e3874e82439f2c5b2608cf7387885. For commit history prior to this, refer the link given.

## Steps to import project

1. Clone the repository
```
git clone https://github.com/rajanikantdeshmukh/UAND-P2.git
```
2. Open Android Studio and select **Import Project**
  ![Android Studio Welcome Screen](/screenshots/android_studio.png)
3. Navigate to path where the cloned respository exists import project
  ![Android Studio Import Screen](/screenshots/android_studio_import.png)
4. After importing project, open gradle.properties file. You will see line at end of file.

  ```
  TMDBApiToken="/** ADD YOUR TMDB API TOKEN HERE **/"
  ```
  Replace the string by your own API key. The key can be obtained from TMDb site. For APIs' documentation, visit http://docs.themoviedb.apiary.io.
5. Build the project and run it.
  ![App Screenshot](/screenshots/app_screenshot.png)
