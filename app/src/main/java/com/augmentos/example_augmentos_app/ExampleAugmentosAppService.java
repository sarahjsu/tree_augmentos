package com.augmentos.example_augmentos_app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.augmentos.augmentoslib.TranscriptProcessor;
import com.augmentos.augmentoslib.AugmentOSLib;
import com.augmentos.augmentoslib.AugmentOSSettingsManager;
import com.augmentos.augmentoslib.SmartGlassesAndroidService;
import com.augmentos.augmentoslib.events.SpeechRecOutputEvent;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;


public class ExampleAugmentosAppService extends SmartGlassesAndroidService {
//
    public static final String TAG = "LiveCaptionsService";

    public AugmentOSLib augmentOSLib;
    ArrayList<String> responsesBuffer;
    ArrayList<String> transcriptsBuffer;
    ArrayList<String> responsesToShare;
    Handler debugTranscriptsHandler = new Handler(Looper.getMainLooper());
    private boolean debugTranscriptsRunning = false;

    private Handler transcribeLanguageCheckHandler;
    private String lastTranscribeLanguage = null;
    private final int maxNormalTextCharsPerTranscript = 30;
    private final int maxLines = 3;

    private final TranscriptProcessor normalTextTranscriptProcessor = new TranscriptProcessor(maxNormalTextCharsPerTranscript, maxLines);
    private String currentLiveCaption = "";
    private String finalLiveCaption = "";
    private final Handler callTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    public ExampleAugmentosAppService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void setup() {
        // Create AugmentOSLib instance with context: this
        augmentOSLib = new AugmentOSLib(this);

        augmentOSLib.sendReferenceCard("tESTING", "hi sanjith.");

        // Subscribe to a data stream (ex: transcription), and specify a callback function
        // Initialize the language check handler
        transcribeLanguageCheckHandler = new Handler(Looper.getMainLooper());

        // Start periodic language checking
        startTranscribeLanguageCheckTask();

        //setup event bus subscribers
//        setupEventBusSubscribers();

        //make responses holder
        responsesBuffer = new ArrayList<>();
        responsesToShare = new ArrayList<>();
        responsesBuffer.add("Welcome to AugmentOS.");

        //make responses holder
        transcriptsBuffer = new ArrayList<>();

        Log.d(TAG, "Convoscope service started");

        completeInitialization();
    }

    public void processTranscriptionCallback(String transcript, String languageCode, long timestamp, boolean isFinal) {
        Log.d(TAG, "Got a transcript: " + transcript + ", which is FINAL? " + isFinal + " and has language code: " + languageCode);

    }

    public void processTranslationCallback(String transcript, String languageCode, long timestamp, boolean isFinal, boolean foo) {
        Log.d(TAG, "Got a translation: " + transcript + ", which is FINAL? " + isFinal + " and has language code: " + languageCode);
    }

    public void completeInitialization(){
        Log.d(TAG, "COMPLETE CONVOSCOPE INITIALIZATION");
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy: Called");
        augmentOSLib.deinit();
        if (debugTranscriptsRunning) {
            debugTranscriptsHandler.removeCallbacksAndMessages(null);
        }
        Log.d(TAG, "ran onDestroy");
        super.onDestroy();
    }

    @Subscribe
    public void onTranscript(SpeechRecOutputEvent event) {
        String text = event.text;
        String languageCode = event.languageCode;
        long time = event.timestamp;
        boolean isFinal = event.isFinal;

        if (isFinal){
            Log.d(TAG, "Live Captions got final: " + text);
        }

        debounceAndShowTranscriptOnGlasses(text, isFinal);
    }

    private final Handler glassesTranscriptDebounceHandler = new Handler(Looper.getMainLooper());
    private Runnable glassesTranscriptDebounceRunnable;
    private long glassesTranscriptLastSentTime = 0;
    private final long GLASSES_TRANSCRIPTS_DEBOUNCE_DELAY = 400; // in milliseconds

    private void debounceAndShowTranscriptOnGlasses(String transcript, boolean isFinal) {
        glassesTranscriptDebounceHandler.removeCallbacks(glassesTranscriptDebounceRunnable);
        long currentTime = System.currentTimeMillis();

        if (isFinal) {
            Log.d(TAG, "fINAL STRING?!?!?!?!??!? " + transcript);
//            get transcription here
            showTranscriptsToUser(transcript, true);
            return;
        }

        if (currentTime - glassesTranscriptLastSentTime >= GLASSES_TRANSCRIPTS_DEBOUNCE_DELAY) {
            showTranscriptsToUser(transcript, false);
            glassesTranscriptLastSentTime = currentTime;
        } else {
            glassesTranscriptDebounceRunnable = () -> {
                showTranscriptsToUser(transcript, false);
                glassesTranscriptLastSentTime = System.currentTimeMillis();
            };
            glassesTranscriptDebounceHandler.postDelayed(glassesTranscriptDebounceRunnable, GLASSES_TRANSCRIPTS_DEBOUNCE_DELAY);
        }
    }

    private void showTranscriptsToUser(final String transcript, final boolean isFinal) {
        String processed_transcript = transcript;

        sendTextWallLiveCaptionLL(processed_transcript, isFinal);
    }

    public void sendTextWallLiveCaptionLL(final String newLiveCaption, final boolean isFinal) {
        callTimeoutHandler.removeCallbacks(timeoutRunnable);

        timeoutRunnable = () -> {
            // Call your desired function here
            augmentOSLib.sendHomeScreen();
        };
        callTimeoutHandler.postDelayed(timeoutRunnable, 16000);

        String textBubble = "\uD83D\uDDE8";

        if (!newLiveCaption.isEmpty()) {
            int maxLen;
            maxLen = 100;
            currentLiveCaption = normalTextTranscriptProcessor.processString(finalLiveCaption + " " + newLiveCaption, isFinal);

            if (isFinal) {
                finalLiveCaption = newLiveCaption;
            }

            // Limit the length of the final live caption, in case it gets too long
            if (finalLiveCaption.length() > maxLen) {
                finalLiveCaption = finalLiveCaption.substring(finalLiveCaption.length() - maxLen);
            }
        }

        final String finalLiveCaptionString;
        if (!currentLiveCaption.isEmpty()) {
            finalLiveCaptionString = textBubble + currentLiveCaption;
        } else {
            finalLiveCaptionString = "";
        }

        augmentOSLib.sendDoubleTextWall(finalLiveCaptionString, "");
    }

    public static void saveChosenTranscribeLanguage(Context context, String transcribeLanguageString) {
        Log.d(TAG, "set saveChosenTranscribeLanguage");
        AugmentOSSettingsManager.setStringSetting(context, "transcribe_language", transcribeLanguageString);
    }

    public static String getChosenTranscribeLanguage(Context context) {
        String transcribeLanguageString = AugmentOSSettingsManager.getStringSetting(context, "transcribe_language");
        if (transcribeLanguageString.isEmpty()){
            saveChosenTranscribeLanguage(context, "Chinese");
            transcribeLanguageString = "Chinese";
        }
        return transcribeLanguageString;
    }

    private void startTranscribeLanguageCheckTask() {
        transcribeLanguageCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get the currently selected transcription language
                String currentTranscribeLanguage = getChosenTranscribeLanguage(getApplicationContext());

                // If the language has changed or this is the first call
                if (lastTranscribeLanguage == null || !lastTranscribeLanguage.equals(currentTranscribeLanguage)) {
                    if (lastTranscribeLanguage != null) {
                        augmentOSLib.stopTranscription(lastTranscribeLanguage);
                    }
                    augmentOSLib.requestTranscription(currentTranscribeLanguage);
                    finalLiveCaption = "";
                    lastTranscribeLanguage = currentTranscribeLanguage;
                }

                // Schedule the next check
                transcribeLanguageCheckHandler.postDelayed(this, 333); // Approximately 3 times a second
            }
        }, 200);
    }

}
