package jcollado.pw.numcode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.util.DisplayMetrics;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pddstudio.urlshortener.URLShortener;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jcollado.pw.numcode.Utils.Functions;
import mehdi.sakout.fancybuttons.FancyButton;



public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    @BindView(R.id.buttonShare)
    FancyButton buttonShare;
    @BindView(R.id.buttonProfile)
    FancyButton buttonProfile;
    @BindView(R.id.buttonWeb)
    FancyButton buttonWeb;
    String url;
    protected ProgressDialog progressBar;

    boolean noExists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
         sharedPref =getPreferences(Context.MODE_PRIVATE);
        buttonShare.setCustomTextFont("WorkSans-Medium.otf");
        buttonProfile.setCustomTextFont("WorkSans-Medium.otf");
        buttonWeb.setCustomTextFont("WorkSans-Medium.otf");
        url =  "http://www.numcode.com/" + getLocale();
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
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


            if(locale.contains("es"))  spanish.setChecked(true);
            else if(locale.contains("fr"))  french.setChecked(true);
            else {
                english.setChecked(true);
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
            String url = "http://www.numcode.com/"+ getLocale() + "/recherche?controller=search&orderby=position&orderway=desc&search_query="+numcode+"&submit_search=";
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
            String url = "http://www.numcode.com/"+ getLocale() + "/recherche?controller=search&orderby=position&orderway=desc&search_query="+numcode+"&submit_search=";
            shortUrl(url);

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

    public String getLocale(){
        String locale = getResources().getConfiguration().locale.toString();

        if(locale.contains("es")) return "es";
        else if(locale.contains("fr"))  return "fr";
        return "gb";
    }

    public void shortUrl(String url) {


        URLShortener.shortUrl(url, new URLShortener.LoadingCallback() {
            @Override
            public void startedLoading() {
               onPreStartConnection();
            }

            @Override
            public void finishedLoading(@Nullable String shortUrl) {
                //make sure the string is not null
                if(shortUrl != null) {
                    progressBar.hide();
                    share(shortUrl);
                }
                else Toast.makeText(MainActivity.this, "Unable to generate Link!", Toast.LENGTH_SHORT).show();
            }
        });
    }

public void share(String url){
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body)+" "+url);
    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
}


    public void onPreStartConnection() {
        progressBar.setMessage(getString(R.string.loading));
        progressBar.setCancelable(false);
        progressBar.show();
    }
}
