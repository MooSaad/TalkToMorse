package com.example.texttospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView result;
        final Button toMorseBtn;

        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        result = findViewById(R.id.result);
        toMorseBtn = findViewById(R.id.toMorseBtn);

        toMorseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtToConvert = editText.getText().toString();
                String convertedTxt = MorseCode.alphaToMorse(txtToConvert);
                result.setText(convertedTxt);
            }
        });





        Button b_Flash = findViewById(R.id.flash_Button);

        b_Flash.setOnClickListener(
                new Button.OnClickListener() {

                    public void onClick(View v) {
                        String s = editText.getText().toString().toLowerCase();
                        if (MorseCode.vibrateTranslate(s)== "x") {
                            result.setText(MorseCode.alphaToMorse(s));
                        }
                        else {
                            MorseCode.flashInOwnThread(s);
                        }
                    }
                }
        );






        Button b_Vibration = findViewById(R.id.vibrate_Button);

        b_Vibration.setOnClickListener(
                new Button.OnClickListener() {

                    public void onClick(View v) {
                        String s = editText.getText().toString().toLowerCase();
                        if (MorseCode.vibrateTranslate(s)== "x") {
                            result.setText(MorseCode.alphaToMorse(s));
                        } else {
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            long dot = 500;          // Length of a Morse Code "dot" in milliseconds
                            long dash = 1500;         // Length of a Morse Code "dash" in milliseconds
                            long short_gap = 500;    // Length of Gap Between dots/dashes
                            long medium_gap = 1500;   // Length of Gap Between Letters
                            long long_gap = 3500;    // Length of Gap Between Words
                            String temp = MorseCode.vibrateTranslate(s);
                            ArrayList<Long> list = new ArrayList<Long>();
                            list.add(new Long(0));
                            for (char c : temp.toCharArray()) {
                                if (c == '.') {
                                    list.add(dot);
                                } else if (c == '-') {
                                    list.add(dash);
                                } else if (c == ' ') {
                                    list.add(short_gap);
                                } else if (c == '!') {
                                    list.add(medium_gap);
                                } else if (c == '~') {
                                    int index = list.lastIndexOf(medium_gap);
                                    list.remove(index);
                                    list.add(long_gap);
                                }
                            }
                            long[] pattern = new long[list.size()];
                            int i = 0;
                            for (Long l : list) {
                                long prim = l.longValue();
                                pattern[i] = prim;
                                i++;
                            }
                            vibrator.vibrate(pattern, -1);
                        }
                    }

                }
        );






        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                editText.setText("");
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {}
            @Override
            public void onBufferReceived(byte[] bytes) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int i) {}

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    editText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {}
            @Override
            public void onEvent(int i, Bundle bundle) {}
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }
}