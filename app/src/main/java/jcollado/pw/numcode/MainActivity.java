package jcollado.pw.numcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.InputType;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.IOException;

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
                .positiveText(getString(R.string.ok))
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
                        try {
                            String web =   "http://www.numcode.com/nouveau/fr/profile-"+dialog.getInputEditText().getText().toString()+".html";
                             doGetRequest(web);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Functions.writeNumCodePrefs(sharedPref,dialog.getInputEditText().getText().toString());
                        if(profile)
                        onProfile();
                        else{onShare();}
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
    void doGetRequest(String url) throws IOException{
        String res;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {


                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.i("sauce",res);
                    }
                });
    }

}
