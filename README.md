# Project Details ::
## Project Name
firebase-1-to-1-chat*
### Language
Kotlin
#### Description 
firebase-1-to-1-chat is Firebase real-time chat demo.
##### Project Structure -
-- Folder structure
```bash
app
|-- google-services.json
|-- proguard-rules.pro
|-- src
|   |-- main
|   |   |-- java
|   |   |   |-- com
|   |   |   |   |-- firebasechatkotlin
|   |   |   |   |   |-- activity
|   |   |   |   |   |-- adapters
|   |   |   |   |   |-- listeners
|   |   |   |   |   |-- models
|   |   |-- res
|   |   |   |-- drawable-hdpi
|   |   |   |-- drawable-sw600dp-hdpi
|   |   |   |-- drawable-sw600dp-mdpi
|   |   |   |-- drawable-sw600dp-xhdpi
|   |   |   |-- drawable-sw720dp-hdpi
|   |   |   |-- drawable-sw720dp-mdpi
|   |   |   |-- drawable-v24
|   |   |   |-- drawable-xhdpi
|   |   |   |-- drawable-xxhdpi
|   |   |   |-- drawable
|   |   |   |-- layout
|   |   |   |-- mipmap-anydpi-v26
|   |   |   |-- mipmap-hdpi
|   |   |   |-- mipmap-mdpi
|   |   |   |-- mipmap-xhdpi
|   |   |   |-- mipmap-xxhdpi
|   |   |   |-- mipmap-xxxhdpi
|   |   |   |-- values
|   |   |   |   |-- attrs.xml
|   |   |   |   |-- colors.xml
|   |   |   |   |-- dimens.xml
|   |   |   |   |-- strings.xml
|   |   |   |   |-- styles.xml
```
***
## Folders Details

The folder structure of this app is explained below:

| Folder Name                                        | Description                                              |
|----------------------------------------------------|----------------------------------------------------------|
| app/src/main/java/com/firebasechatkotlin/activity  | This folder contains all Activity files                  |
| app/src/main/java/com/firebasechatkotlin/adapters  | This folder contain all RecyclerView Adapter             |
| app/src/main/java/com/firebasechatkotlin/listeners | This folder contains all interface files                 |
| app/src/main/java/com/firebasechatkotlin/models    | This folder contains all model files                     |
| app/src/res/drawable-hdpi                          | App icons and Images and Drawable files(hdpi)            |
| app/src/res/drawable-sw600dp-hdpi                  | App icons and Images and Drawable files(sw600dp-hdpi)    |
| app/src/res/drawable-sw600dp-mdpi                  | App icons and Images and Drawable files (sw600dp-mdpi)   |
| app/src/res/drawable-sw600dp-xhdpi                 | App icons and Images and Drawable files(sw600dp-xhdpi)   |
| app/src/res/drawable-sw720dp-hdpi                  | App icons and Images and Drawable files(sw720dp-hdpi)    |
| app/src/res/drawable-sw720dp-mdpi                  | App icons and Images and Drawable files(sw720dp-mdpi)    |
| app/src/res/drawable-v24                           | App icons and Images and Drawable files                  |
| app/src/res/drawable-xhdpi                         | App icons and Images and Drawable files                  |
| app/src/res/drawable-xxhdpi                        | App icons and Images and Drawable files                  |
| app/src/res/drawable                               | App icons and Images and Drawable files                  |
| app/src/res/layout                                 | Layout Files                                             |
| app/src/res/mipmap-anydpi-v26                      | Launcher App icon                                        |
| app/src/res/mipmap-hdpi                            | Launcher App icon                                        |
| app/src/res/mipmap-mdpi                            | Launcher App icon                                        |
| app/src/res/mipmap-xhdpi                           | Launcher App icon                                        | 
| app/src/res/mipmap-xxhdpi                          | Launcher App icon                                        |
| app/src/res/mipmap-xxxhdpi                         | Launcher App icon                                        |  
| app/src/res/values/attr.xml                        | represents an attribute of an Element object             |
| app/src/res/values/colors.xml                      | All Colors code Added in this file which are used in App |
| app/src/res/values/dimens.xml                      | Add Dimen in this file                                   |
| app/src/res/values/string.xml                      | Add All string in this file which are used in App        |
| app/src/res/values/styles.xml                      | Add All style in this file                               |
 


# Versions name with their code ::

Android Studio version : Android Studio Giraffe | 2022.3.1





## Dependencies (Packages and Library)

| name                                       | version | Details                                                                                                                     |
|--------------------------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------|
| org.jetbrains.kotlin:kotlin-stdlib-jdk7    | 1.8.0   | The Kotlin Standard Library for JDK 7, providing essential utility functions and extensions for Kotlin programming          |
| androidx.appcompat:appcompat               | 1.6.1   | library that provides backward-compatible implementations of newer Android features and UI components                       |
| androidx.constraintlayout:constraintlayout | 2.1.4   | library that helps to create flexible and responsive user interfaces in Android by using a constraint-based layout          |
| androidx.recyclerview:recyclerview         | 1.3.0   | AndroidX RecyclerView is a library that provides an improved and more flexible version of the RecyclerView widget.          |  
| com.google.firebase:firebase-core          | 21.1.1  | library is a core component of Firebase, which is a mobile and web application development platform provided by Google      |
| com.google.firebase:firebase-auth          | 22.1.0  | It provides a set of authentication services that allow developers to add user authentication to their Android apps easily. |
| com.google.firebase:firebase-database      | 20.2.2  | module within Firebase, Google's mobile and web application development platform. It provides a real-time NoSQL database    | 
| com.firebaseui:firebase-ui-database        | 4.3.2   | library that provides pre-built UI components to simplify the integration of Firebase services                              |





# SDK Version supports:
*Min SDK version required: 23*
*TargetSdk SDK version required: 34*



# Firebase chat demo using Kotlin

**Please change below information for Checking demo code :**

**1.Create your project in firebase console**

	-For the firebase chat you require to create project in firebase console and if already exist then you can add as app.
	-After adding your app with package download "google-services.json" from console and put it in your app level module.
	
**3.Configure project in firebase console**

	-In firebase console after select your app you find one new page, From this page select Auth menu from left side menu list and after that you find one tab SIGN-IN METHOD select it and after allow email/password is sign in provide.

