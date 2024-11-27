package com.teamopensourcesmartglasses.example_augmentos_app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";
    private static final String TARGET_PACKAGE = "com.teamopensmartglasses.augmentos";
    private static final String DEEP_LINK_SCHEME = "augmentos";
    private static final String DEEP_LINK_HOST = "open";
    private static final String FALLBACK_URL = "https://augmentos.org";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LauncherActivity started");

        Uri deepLinkUri = Uri.parse(DEEP_LINK_SCHEME + "://" + DEEP_LINK_HOST)
                .buildUpon()
                .appendQueryParameter("sourcePackage", getPackageName())
                .build();
        Log.d(TAG, "Constructed deep link URI: " + deepLinkUri.toString());

        Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, deepLinkUri);
        deepLinkIntent.setPackage(TARGET_PACKAGE);
        Log.d(TAG, "Created deep link intent with target package: " + TARGET_PACKAGE);

        if (deepLinkIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "Target app can handle the deep link");
            try {
                startActivity(deepLinkIntent);
                Log.d(TAG, "Deep link intent launched successfully");
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Target app not found: " + e.getMessage());
                openFallbackWebpage();
            } catch (Exception e) {
                Log.e(TAG, "Error launching target app: ", e);
                openFallbackWebpage();
            }
        } else {
            Log.e(TAG, "Target app not found or cannot handle the deep link");
            openFallbackWebpage();
        }

        // Finish the activity after attempting to launch the intent
        finish();
    }

    /**
     * Opens the fallback webpage in the user's default browser.
     */
    private void openFallbackWebpage() {
        try {
            Uri webpage = Uri.parse(FALLBACK_URL);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure a new task is created
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Log the intent details
            Log.d(TAG, "Attempting to launch fallback webpage: " + FALLBACK_URL);

            // Verify that there is an app to handle the intent
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
                Log.d(TAG, "Fallback webpage launched successfully");
            } else {
                Log.e(TAG, "No application can handle the web intent for: " + FALLBACK_URL);
                Toast.makeText(this, "No browser found to open the webpage.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening fallback webpage", e);
            Toast.makeText(this, "Failed to open the webpage.", Toast.LENGTH_LONG).show();
        }
    }
}
