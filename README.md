# Example AugmentOS App

This repository provides a **bare-bones example** of how to build a Third-Party App (TPA) for **[AugmentOS](https://www.augmentos.org/)**, the operating system for smart glasses. If you want to get started with building apps for AugmentOS, start here.

---

# How to make an AugmentOS app

## **Braindead Mode: Clone This Repo**

If you just want to dive in:
1. Clone this repo.
2. [Install AugmentOSLib](#augmentoslib-installation)
3. Build and deploy the app to your AugmentOS Puck or other device running AugmentOS Core.

You're now running an AugmentOS TPA!

---

## **Easy Mode: Start from Scratch**

If you want to start from scratch:
1. Start a fresh Android Studio Project.
2. [Install AugmentOSLib](#augmentoslib-installation)
3. Add the following to your app's `AndroidManifest.xml` file:

```
<service android:name="com.yourpackage.YourAugmentosService"
    android:exported="true">
    <!-- Intent filter required to communicate with AugmentOS -->
    <intent-filter>
        <action android:name="AUGMENTOS_INTENT" />
    </intent-filter>
    <!-- Metadata marking this app as a TPA -->
    <meta-data android:name="com.augmentos.tpa.name" android:value="Example App" />
    <meta-data android:name="com.augmentos.tpa.description" android:value="Example App Description" />
</service>
```

4. Build and deploy the app to your AugmentOS Puck or other device running AugmentOS Core.

You're now running an AugmentOS TPA!

---

## **AugmentOSLib installation**

1. Clone the [AugmentOS repository](https://github.com/teamopensmartglasses/augmentos) next to your app's directory (default setup)

2. If you cloned the [AugmentOS repository](https://github.com/teamopensmartglasses/augmentos) elsewhere, update the path to AugmentOSLib in `settings.gradle`:
   ```
   project(':AugmentOSLib').projectDir = new File(rootProject.projectDir, '../AugmentOS/augmentos_android_library/AugmentOSLib')
   ```

---

## **How It Works**

### **Service-Based Architecture**
- The core of this app is a foreground service (`ExampleAugmentosAppService`) that extends `SmartGlassesAndroidService`.
- This service allows the app to:
  - Communicate with the AugmentOS Core.
  - Subscribe to data streams like transcription.
  - Display content directly on the smart glasses.

### **AugmentOSLib**
- The library exposes essential methods to interact with AugmentOS, such as:
  - **Registering the app** with AugmentOS.
    ```java
    augmentOSLib.registerApp("Example App", "Bare-bones AugmentOS app.");
    ```
  - **Subscribing to data streams**, like real-time transcription.
    ```java
    augmentOSLib.subscribe(DataStreamType.TRANSCRIPTION_ENGLISH_STREAM, this::processTranscriptionCallback);
    ```
  - **Displaying content** on the smart glasses:
    - **Reference cards**:
      ```java
      augmentOSLib.sendReferenceCard("Title", "Body text.");
      ```
    - **Bullet point lists**:
      ```java
      augmentOSLib.sendBulletPointList("Title", new String[] {"Point 1", "Point 2"});
      ```
    - **Text layouts**:
      - Centered text:
        ```java
        augmentOSLib.sendCenteredText("Centered Text Example");
        ```
      - Text wall:
        ```java
        augmentOSLib.sendTextWall("This is a block of text that fills the screen.");
        ```
      - Double text wall:
        ```java
        augmentOSLib.sendDoubleTextWall("Top Text", "Bottom Text");
        ```
    - **Row cards**:
      ```java
      augmentOSLib.sendRowsCard(new String[] {"Row 1", "Row 2", "Row 3"});
      ```
    - **Bitmap images**:
      ```java
      augmentOSLib.sendBitmap(myBitmap);
      ```
    - **(COMING SOON) Custom layouts via JSON**:
      ```java
      augmentOSLib.sendCustomContent("{ \"custom\": \"layout\" }");
      ```

### **Data Subscriptions**
- The app can subscribe to a variety of data streams and handle the incoming events:
  - **Real-Time Transcription**:
    ```java
    augmentOSLib.subscribe(DataStreamType.TRANSCRIPTION_ENGLISH_STREAM, ::transcriptCallback);
    ```
  - **Smart Ring Button Events**:
    ```java
    augmentOSLib.subscribe(DataStreamType.SMART_RING_BUTTON,::buttonCallback);
    ```
  - **Glasses Side Tap Events**:
    ```java
    augmentOSLib.subscribe(DataStreamType.GLASSES_SIDE_TAP, ::tapCallback);
    ```
---

## **License**

This project is licensed under the MIT License. See the `LICENSE` file for more details.
