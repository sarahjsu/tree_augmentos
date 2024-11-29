package com.teamopensourcesmartglasses.example_augmentos_app;

import android.util.Log;

import com.teamopensmartglasses.augmentoslib.AugmentOSCommand;
import com.teamopensmartglasses.augmentoslib.SmartGlassesAndroidService;
import com.teamopensmartglasses.augmentoslib.DataStreamType;
import com.teamopensmartglasses.augmentoslib.FocusStates;
import com.teamopensmartglasses.augmentoslib.AugmentOSLib;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.UUID;

public class ExampleAugmentosAppService extends SmartGlassesAndroidService {
    public final String TAG = "ExampleAugmentOSApp_ExampleService";

    // Our instance of the AugmentOS library
    public AugmentOSLib augmentOSLib;

    public ExampleAugmentosAppService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create AugmentOSLib instance with context: this
        augmentOSLib = new AugmentOSLib(this);

        // Subscribe to a data stream (ex: transcription), and specify a callback function
        augmentOSLib.subscribe(DataStreamType.TRANSCRIPTION_ENGLISH_STREAM, this::processTranscriptionCallback);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Called");
        augmentOSLib.deinit();
        super.onDestroy();
    }

    public void processTranscriptionCallback(String transcript, long timestamp, boolean isFinal) {
        Log.d(TAG, "Got a transcript: " + transcript);

        // Display transcript on the glasses if it's a final transcript
        if(isFinal) {
            augmentOSLib.sendReferenceCard("Example TPA Live Captions", transcript);

            // We could also send a text wall, if we wanted a different layout
            // augmentOSLib.sendTextWall(transcript);

            // We could also send a bitmap image
            // augmentOSLib.sendBitmap(myBitmapHere);

            // We will eventually have a way to send any arbitrary layout using our custom JSON schema
            // augmentOSLib.sendCustomContent();
        }
    }

}
