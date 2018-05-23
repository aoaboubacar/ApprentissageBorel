package univ.m2acdi.apprentissageborel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import univ.m2acdi.apprentissageborel.R;
import univ.m2acdi.apprentissageborel.util.TextSpeaker;

public class SectionIntroductActivity extends Activity {

    private final int SHORT_DURATION = 1000;
    private int section;

    private TextSpeaker textSpeaker;
    private JSONArray jsonArray ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_introduct);

        section = getIntent().getExtras().getInt("section");
        textSpeaker = (TextSpeaker) getIntent().getSerializableExtra("speaker");

        Toast.makeText(this, "Section" + section, Toast.LENGTH_SHORT).show();

        introductSection();
        jsonArray = readJsonDataFile(this);

    }

    /**
     * Méthode permettant de déterminer le titre de la section a afficher
     */
    private void introductSection() {

        String text = "";

        switch (section){
            case 1:
                text = this.getResources().getString(R.string.introduct_section_1);
                break;
            case 2:
                text = this.getResources().getString(R.string.introduct_section_2);
                break;
            case 3:
                text = this.getResources().getString(R.string.introduct_section_3);
                break;

                default:
                    break;
        }

        speakOut(text);
    }

    /**
     *
     * @param text
     */
    protected void speakOut(String text) {
        if (!textSpeaker.isSpeaking()) {
            textSpeaker.speakText(text);
            textSpeaker.pause(SHORT_DURATION);
        }

    }


    @Override
    protected void onStop() {

        super.onStop();
    }

    /**
     * Méthode de lecture du fichier de données
     *
     * Initialise la liste de données (tableau JSON)
     * @param context
     * @return
     */
    public JSONArray readJsonDataFile(Context context){
        String jsonStr;
        try {
            InputStream is = context.getAssets().open("word_file.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonStr = new String(buffer, "UTF-8");
            jsonArray = new JSONArray(jsonStr);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     *
     */
    public void goToSection(){
        Intent intent = new Intent();
        int section = getIntent().getExtras().getInt("section");
        TextSpeaker textSpeaker = (TextSpeaker)getIntent().getSerializableExtra("speaker");
        intent.putExtra("speaker", textSpeaker);
        intent.putExtra("jsonArray", jsonArray.toString());
        switch (section){
            case 1:
                intent.setClass(this, TextToSpeechActivity.class);
                break;
            case 2:
                intent.setClass(this, GestureToSpeechActivity.class);
                break;
            case 3:
                intent.setClass(this, OrderGestActivity.class);
                break;
            default:
                //intent.setClass(this, SectionIntroductActivity.class);
                break;
        }
        startActivity(intent);
    }

}
