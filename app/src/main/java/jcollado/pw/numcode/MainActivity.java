package jcollado.pw.numcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jcollado.pw.numcode.Utils.Functions;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    OkHttpClient client;
    String url = "http://www.numcode.com/nouveau/";
    @BindView(R.id.buttonShare)
    FancyButton buttonShare;
    @BindView(R.id.buttonProfile)
    FancyButton buttonProfile;
    @BindView(R.id.buttonWeb)
    FancyButton buttonWeb;

    boolean noExists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        ButterKnife.bind(this);
         sharedPref =getPreferences(Context.MODE_PRIVATE);
        buttonShare.setCustomTextFont("WorkSans-Medium.otf");
        buttonProfile.setCustomTextFont("WorkSans-Medium.otf");
        buttonWeb.setCustomTextFont("WorkSans-Medium.otf");

    }

    @OnClick(R.id.settingsButton)
    public void onSettings(){
        String num = Functions.getNumCodePrefs(sharedPref);
        boolean wrapInScrollView = true;

        MaterialDialog dialog =new MaterialDialog.Builder(this)
                .title(getString(R.string.settings))
                .customView(R.layout.settings, wrapInScrollView)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dialog.getCustomView();
                        RadioGroup buttons = (RadioGroup) view.findViewById(R.id.languageRG);

                        EditText editText =(EditText) view.findViewById(R.id.numcodeED);

                        int radioButtonID = buttons.getCheckedRadioButtonId();
                        View radioButton = buttons.findViewById(radioButtonID);
                        int idx = buttons.indexOfChild(radioButton);
                        switch (idx) {
                            case 0:
                                setLocale("es");
                                break;
                            case 1:
                                setLocale("fr");
                                break;
                            case 2:
                            default:
                                setLocale("en");
                                break;
                        }
                        Functions.writeNumCodePrefs(sharedPref,editText.getText().toString());

                    }
                })
                .show();

        View view = dialog.getCustomView();
        RadioButton spanish = (RadioButton) view.findViewById(R.id.spanishRB);
        RadioButton english = (RadioButton) view.findViewById(R.id.englishRB);
        RadioButton french = (RadioButton) view.findViewById(R.id.frenchRB);
        EditText editText =(EditText) view.findViewById(R.id.numcodeED);
        editText.setText(num);

        String locale = getResources().getConfiguration().locale.toString();

        switch (locale) {
            case "es":
                spanish.setChecked(true);
                break;
            case "fr":
                french.setChecked(true);
                break;

            default:
                english.setChecked(true);
                break;
        }

    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
    @OnClick(R.id.buttonWeb)
    public void onWeb(){

        new FinestWebView.Builder(this).titleDefault(getString(R.string.numcode_button)).titleColor(ContextCompat.getColor(this, R.color.accent)).updateTitleFromHtml(false).show(url);
    }
    @OnClick(R.id.buttonProfile)
    public void onProfile(){
        String numcode= Functions.getNumCodePrefs(sharedPref);
        if(numcode.equals("")) {
            requestNumcode(true);

        }
        else{
            String url = "http://www.numcode.com/nouveau/fr/profile-"+numcode+".html";
            new FinestWebView.Builder(this).titleDefault(getString(R.string.numcode_profile)).titleColor(ContextCompat.getColor(this, R.color.accent)).updateTitleFromHtml(false).show(url);

        }


    }
    @OnClick(R.id.buttonShare)
    public void onShare(){
        String numcode= Functions.getNumCodePrefs(sharedPref);
        if(numcode.equals("")) {
            requestNumcode(false);

        }
        else{
            String url = " http://www.numcode.com/nouveau/fr/profile-"+numcode+".html";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body)+url);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

        }

    }

    public void requestNumcode(final boolean  profile){
        new MaterialDialog.Builder(this)
                .title(getString(R.string.save_numcode))
                .content(getString(R.string.numcode_alert_body))
                .inputType(InputType.TYPE_CLASS_NUMBER )


                .negativeText(getString(R.string.register))
                .input("","", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                            Functions.writeNumCodePrefs(sharedPref, dialog.getInputEditText().getText().toString());
                            if (profile)
                                onProfile();
                            else {
                                onShare();
                            }
                        }

                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        new FinestWebView.Builder(getApplicationContext()).show(url);
                    }
                })
                .show();
    }



}
