package univ.m2acdi.apprentissageborel.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import univ.m2acdi.apprentissageborel.R;
import univ.m2acdi.apprentissageborel.fragment.ListenSpeakOutFragment;
import univ.m2acdi.apprentissageborel.util.TextSpeaker;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TextToSpeechActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int SHORT_DURATION = 1000;
    private ImageButton speechBtnPrompt;
    private ImageButton imageButton;
    private ImageView speechTextCheickStatus;

    private TextSpeaker textSpeaker;
    private static boolean isOk = false;
    private static int repeatCount = 0;

    TTSpeechAsyncTask textSpeechTask;

    private ListenSpeakOutFragment lspFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        lspFragment = new ListenSpeakOutFragment();

        setFragment(lspFragment);

        textSpeaker = (TextSpeaker) getIntent().getSerializableExtra("speaker");

        speechBtnPrompt = findViewById(R.id.speech_prompt_btn);
        speechBtnPrompt.setOnClickListener(onClickListener);

        speechTextCheickStatus = findViewById(R.id.speech_text_cheick_status);

        imageButton = findViewById(R.id.btn_next);
        //imageButton.setOnClickListener(onClickListener);

        //speakOutViewText();

        textSpeechTask = new TTSpeechAsyncTask();

    }

    @Override
    protected void onStart() {
        super.onStart();

        textSpeechTask.execute();
    }

    /**
     * Speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Lis la lettre ou le mot");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Non supporté", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Réception du texte entendu
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<String> result = null;
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    speechTextCheickStatus.setImageDrawable(getImageViewByName("good"));
                    //txtSpeechCheick.setText(result.get(0));
                }
                break;
            }

        }

        repeatCount++;

        Toast.makeText(getApplicationContext(), "ReapeatCount: "+repeatCount, Toast.LENGTH_LONG).show();

        if (result != null) {
            textSpeechTask = new TTSpeechAsyncTask();
            textSpeechTask.execute();
            for (int i = 0; i < result.size(); i++) {
                System.out.println("\n Sequence: " + result.get(i));
            }
        }else {
            textSpeechTask = new TTSpeechAsyncTask();
            textSpeechTask.execute();
        }

        if (repeatCount == 2) {
            isOk = true;
            repeatCount = 0;
            lspFragment = new ListenSpeakOutFragment();
            setFragment(lspFragment);
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            promptSpeechInput();
        }
    };

    void setFragment(Fragment fragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.lspFragmentContainer, fragment, null);
        ft.commit();
    }

    public void speakOutViewText() {
        TextView textView = findViewById(R.id.word_text_view);
        String text = textView.getText().toString();

        try {
            SECONDS.sleep(3);
            speakOut(text);
            SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    protected void speakOut(String text) {
        if (!textSpeaker.isSpeaking()) {
            textSpeaker.speakText(text);
            textSpeaker.pause(SHORT_DURATION);
        }

    }


    /**
     * Récupère une image (Objet Drawable)
     *
     * @param geste
     * @return
     */
    private Drawable getImageViewByName(String geste) {

        Context context = getApplicationContext();

        int image_id = context.getResources().getIdentifier(geste, "drawable", getPackageName());

        return context.getResources().getDrawable(image_id);
    }

    private class TTSpeechAsyncTask extends AsyncTask<Void, Boolean, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            speakOutViewText();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Toast.makeText(getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
            promptSpeechInput();
        }

    }

}
