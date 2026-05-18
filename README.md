# 👟 Shoe Mart Shop — User & Admin Manuals
> The complete running manual for Shoe Mart Shop, organized by User Role to help developers and testers run the application either as a Customer or as an Administrator.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Backend](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Theme](https://img.shields.io/badge/Theme-Pink_Premium-FF4081?style=for-the-badge)](https://material.io)

---

## 👤 Part 1: Regular User (Customer) Manual

This section explains how to compile the project, run it, create a shopper account, and use all the storefront features.

### 1. Developer Setup & Running the App
Follow these steps to compile and install the application on your test device:

1.  **Clone the Project**:
    ```bash
    git clone https://github.com/Taufique115/Shoe_Mart_Shop.git
    cd Shoe_Mart_Shop
    ```
2.  **Add your Firebase Configurations**:
    *   Create a project on the [Firebase Console](https://console.firebase.google.com).
    *   Add an Android app with the package name `com.example.shoemartshop`.
    *   Download your `google-services.json` and place it in the `app/` folder of the project.
3.  **Activate Cloud Databases**:
    *   Enable **Cloud Firestore** and deploy rules from the local file [`firestore.rules`](file:///e:/ShoeMartShop/firestore.rules).
    *   Enable **Realtime Database** and deploy rules from the local file [`database.rules.json`](file:///e:/ShoeMartShop/database.rules.json).
4.  **Launch & Run**:
    *   Open the project folder inside **Android Studio**.
    *   Wait for Gradle to finish indexing and syncing the project.
    *   Launch an **Emulator** or connect a physical Android phone.
    *   Press the green **Run (Play)** button in Android Studio to compile and deploy the APK.

---

### 2. Shopper Application Walkthrough

#### 📧 Account Sign Up & Email Verification Loop
1.  On launching the app, tap the **Get Started** button on the Splash screen.
2.  On the Login screen, tap **Register** to create a new profile.
3.  Enter your details (Name, Email, Phone, Location, and Password). Click **Sign Up**.
4.  **Email Verification Required**: An automated verification link is sent to your inbox. The app signs you out immediately so you cannot log in without verification.
5.  Go to your email client, open the message from Firebase, and tap the verification link.
6.  Return to the application, sign in with your email/password, and explore the app!

#### 🔍 Browsing the Sneaker Catalog
*   **Dynamic Carousels**: Swipe through the high-fidelity promotional banners at the top of the homepage.
*   **Brand Filters**: Tap any category brand chip (Nike, Adidas, Puma, Reebok, Crocodile) to instantly filter the store catalog. Tap the **Home** icon in the navigation bar to refresh and reset the lists.
*   **Live Search**: Type inside the search bar. The grid results update in real-time as you type.

#### 👟 Variant Selections & Purchases
1.  Tap on any product card on the homepage to open the **[ProductDetailsActivity](file:///e:/ShoeMartShop/app/src/main/java/com/example/shoemartshop/Activity/ProductDetailsActivity.kt)**.
2.  Tap on the custom thumbnail images to browse alternative sneaker angles.
3.  Select your desired **Size Chip** (e.g. US 7, US 8, US 9, US 10).
4.  Configure the quantity using the `+` and `-` controls and tap **Add to Cart**.
5.  Open your **Cart** from the bottom navigation bar.
6.  Verify quantities and subtotals. Tap **Proceed to Checkout** -> **Place Order**. A receipt screen will appear displaying your purchase details and tracking identifiers.

#### ❤️ Bookmarking Favorites
*   Tap the **Heart** icon on any sneaker card or detail page.
*   Your bottom navigation bar displays a live red badge reflecting your favorite count.
*   Tap the **Heart** in the navigation bar to inspect your wish list.

#### 📊 Customer Profile Metrics
1.  Tap the **Profile** icon in the bottom navigation bar.
2.  Your account portal displays dynamic contact details, your verified badge, and registered delivery address.
3.  **Real-Time Analytics Dashboard**:
    *   **Total Spent**: Sum total in BDT spent across your successful orders.
    *   **Total Shoes Purchased**: Sum total of sneaker boxes ordered.

---

## 🛡️ Part 2: Admin User Manual

This section explains how to enable Admin privileges and operate the real-time catalog management tools.

### 1. Granting Administrator Privileges
The application automatically checks user roles to unlock administrative panels. To elevate an account:

1.  Register a standard user account in the app using **Part 1** steps.
2.  Go to your **Firebase Console** -> **Cloud Firestore** -> **`users`** collection.
3.  Locate the document matching your user's UID (verify it via the profile screen in the app).
4.  Change the **`role`** field value from `"Customer"` to **`"Admin"`**:
    ```json
    {
      "role": "Admin"
    }
    ```
5.  Relaunch or resume the application.

---

### 2. Admin Operations Walkthrough

#### ➕ Launching the Control Center
*   When logged in as an Administrator, a **"+" Floating Action Button** dynamically appears on the storefront dashboard.
*   Tap this **"+" Floating Action Button** to immediately open the secure **Product Control Panel** ([ProductEditActivity](file:///e:/ShoeMartShop/app/src/main/java/com/example/shoemartshop/Activity/ProductEditActivity.kt)).

#### 📝 Managing the Product Catalog (CRUD)
*   **Add a Product**: Fill in the title, price in BDT, product description, default Cloudinary image link, ratings score, and target brand category. Tap **Save Product** to upload. The product is added to Firebase and rendered on all customer devices instantly!
*   **Update a Product**: Open the product control screen to adjust pricing, revise sneaker descriptions, or replace detail thumbnails. Revisions automatically sync and refresh on active customer screens in real-time.

---

<p align="center">Made with ❤️ by Mostafa Jaman Taufique</p>
