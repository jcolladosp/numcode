package jcollado.pw.numcode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pddstudio.urlshortener.URLShortener;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
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
    @BindView(R.id.buttonContactList)
    FancyButton buttonContactList;
    String url;
    protected ProgressDialog progressBar;
    CustomTabsIntent.Builder builder;
    CustomTabsIntent customTabsIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
         sharedPref =getPreferences(Context.MODE_PRIVATE);
        buttonShare.setCustomTextFont("WorkSans-Medium.otf");
        buttonProfile.setCustomTextFont("WorkSans-Medium.otf");
        buttonWeb.setCustomTextFont("WorkSans-Medium.otf");
        buttonContactList.setCustomTextFont("WorkSans-Medium.otf");

        url =  "http://www.numcode.com/" + getLocale();
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);

        builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.accent));

        customTabsIntent = builder.build();
        builder.setShowTitle(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @OnClick(R.id.settingsButton)
    public void onSettings(){
        String num = Functions.getNumCodePrefs(sharedPref);

        MaterialDialog dialog =new MaterialDialog.Builder(this)
                .title(getString(R.string.settings))
                .customView(R.layout.settings, true)
                .positiveText(R.string.save)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup buttons = view.findViewById(R.id.languageRG);

                    EditText editText =view.findViewById(R.id.numcodeED);

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

                })
                .show();

        View view = dialog.getCustomView();
        RadioButton spanish =  view.findViewById(R.id.spanishRB);
        RadioButton english =  view.findViewById(R.id.englishRB);
        RadioButton french =  view.findViewById(R.id.frenchRB);
        EditText editText = view.findViewById(R.id.numcodeED);
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
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
    @OnClick(R.id.buttonContactList)
    public void onContactList(){
        String contactListURL = "http://www.numcode.com/fr/index.php?controller=contact_list";
        customTabsIntent.launchUrl(this, Uri.parse(contactListURL));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    @OnClick(R.id.buttonProfile)
    public void onProfile(){
        String numcode= Functions.getNumCodePrefs(sharedPref);
        if(numcode.equals("")) {
            requestNumcode(true);

        }
        else{
            String url = "http://www.numcode.com/"+ getLocale() + "/recherche?controller=search&orderby=position&orderway=desc&search_query="+numcode+"&submit_search=";
            customTabsIntent.launchUrl(this, Uri.parse(url));

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
                .onPositive((dialog, which) -> {


                    assert dialog.getInputEditText() != null;
                    Functions.writeNumCodePrefs(sharedPref, dialog.getInputEditText().getText().toString());
                        if (profile)
                            onProfile();
                        else {
                            onShare();
                        }
                    })
                .onNegative((dialog, which) -> customTabsIntent.launchUrl(this, Uri.parse(url)));


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
    progressBar.hide();
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body)+" "+url +
    "\n" + "\n" +getString(R.string.download) + " numcode.com/app");
    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
}


    public void onPreStartConnection() {
        progressBar.setMessage(getString(R.string.loading));
        progressBar.setCancelable(false);
        progressBar.show();
    }


}
