# ShopSmart Intelligent Grocery Manager

ShopSmart is a modern Android application built using the MVVM architecture that allows users to manage a real time, cloud synchronised shopping list. 

## Features
* **MVVM Architecture:** Clean separation of concerns using Android ViewModels.
* **Full CRUD Functionality:** Create, Read (real time), Update, and Delete grocery items.
* **Secure Authentication:** User accounts managed via Firebase Authentication using a custom username flow.
* **Cloud Persistence:** Powered by Firebase Cloud Firestore.

## Setup Instructions
To run this project locally on your machine, please follow these steps:

1. **Clone the repository** (or extract the ZIP file) to your local machine.
2. Open **Android Studio** and select File > Open, then navigate to the project directory.
3. **Firebase Configuration:**
   * This project requires a connection to Firebase Cloud Firestore and Firebase Authentication.
   * Go to Tools > Firebase in Android Studio.
   * Under Authentication, click Connect to Firebase to link the project to your own Google account.
   * Enable Email and Password authentication in your Firebase Console.
   * Create a Firestore database in the Firebase Console and set the security rules to allow read and write only if the user is authenticated.
4. Click **Sync Project with Gradle Files** to ensure all dependencies are downloaded correctly.
5. Select an Android emulator or plug in a physical device and click **Run**.
