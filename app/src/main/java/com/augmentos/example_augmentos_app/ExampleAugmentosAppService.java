package com.augmentos.example_augmentos_app;

import android.util.Log;

import com.augmentos.augmentoslib.AugmentOSCommand;
import com.augmentos.augmentoslib.AugmentOSSettingsManager;
import com.augmentos.augmentoslib.PhoneNotification;
import com.augmentos.augmentoslib.SmartGlassesAndroidService;
import com.augmentos.augmentoslib.DataStreamType;
import com.augmentos.augmentoslib.FocusStates;
import com.augmentos.augmentoslib.AugmentOSLib;
import com.augmentos.augmentoslib.SpeechRecUtils;
import com.augmentos.augmentoslib.events.NotificationEvent;
import com.augmentos.augmentoslib.events.SpeechRecOutputEvent;
import com.augmentos.augmentoslib.events.StartAsrStreamRequestEvent;
import com.augmentos.augmentoslib.events.StopAsrStreamRequestEvent;
import com.augmentos.augmentoslib.events.TranslateOutputEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

public class ExampleAugmentosAppService extends SmartGlassesAndroidService {
    public final String TAG = "ExampleService";

    // Our instance of the AugmentOS library
    public AugmentOSLib augmentOSLib;

    public ExampleAugmentosAppService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create AugmentOSLib instance
        augmentOSLib = new AugmentOSLib(this);

        // To start receiving speech transcriptions, request a speech transcription stream
        augmentOSLib.requestTranscription("English");

        // To start receiving speech translations, request a speech translation stream.
        // augmentOSLib.requestTranslation("Spanish", "English");

        // To start receiving phone notifications, request a phone notification stream
        // TODO: This isn't necessary currently
        // TODO: For now, just subscribe to notifications without requesting
        // augmentOSLib.requestNotifications();

        // To access your AugmentOS settings, you can do the following:
        boolean exampleToggleSetting = AugmentOSSettingsManager.getBooleanSetting(this, "exampleToggleSetting");
        String exampleTextSetting = AugmentOSSettingsManager.getStringSetting(this, "exampleTextSetting");
        Integer exampleSliderSetting = AugmentOSSettingsManager.getSliderSetting(this, "exampleSliderSetting");
        String exampleSelectSetting = AugmentOSSettingsManager.getSelectSetting(this, "exampleSelectSetting");
        List<String> exampleMultiselectSetting = AugmentOSSettingsManager.getMultiSelectSetting(this, "exampleMultiselectSetting");
    }

    // To get speech transcription, subscribe to them using EventBus
    @Subscribe
    public void onSpeechTranscriptionTranscript(SpeechRecOutputEvent event) {
        String text = event.text;
        String languageCode = event.languageCode;
        long time = event.timestamp;
        boolean isFinal = event.isFinal;

        String title = "Got a " + languageCode + " transcript:";

        if (isFinal) {
            augmentOSLib.sendReferenceCard(title, text);
        }
    }

    // To get speech translation, subscribe to them using EventBus
//    @Subscribe
//    public void onSpeechTranslationTranscript(TranslateOutputEvent event) {
//        String text = event.text;
//        String fromLanguageCode = event.fromLanguageCode;
//        String toLanaguageCode = event.toLanguageCode;
//        Long timestamp = event.timestamp;
//        boolean isFinal = event.isFinal;
//
//        String formattedMessage = "Got a translation from " + fromLanguageCode + " to " + toLanaguageCode + ": " + text;
//
//        if (isFinal) {
//            augmentOSLib.sendTextWall(formattedMessage);
//        }
//    }

    // To get phone notifications, subscribe to them with EventBus
    @Subscribe
    public void onNotificationEvent(NotificationEvent event) {
        Log.d(TAG, "Received event: " + event + ", " + event.text);
        String formattedNotification = event.title + ": " + event.text;
        augmentOSLib.sendReferenceCard("You've got mail!", formattedNotification);
    }

    @Override
    public void onDestroy() {
        // deInit your augmentOSLib instance onDestroy
        augmentOSLib.deinit();
        super.onDestroy();
    }

}
