package jcollado.pw.numcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thefinestartist.finestwebview.FinestWebView;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
         sharedPref =getPreferences(Context.MODE_PRIVATE);


    }
    @OnClick(R.id.buttonWeb)
    public void onWeb(){
        String url = "http://www.numcode.com/nouveau/";
        new FinestWebView.Builder(this).show(url);
    }
    @OnClick(R.id.buttonProfile)
    public void onProfile(){
        String numcode= getNumCodePrefs();
        if(numcode.equals("")) {
            requestNumcode();

        }
        else{
            String url = "http://www.numcode.com/nouveau/fr/profile-"+numcode+".html";
            new FinestWebView.Builder(this).show(url);

        }


    }

    public void requestNumcode(){
        new MaterialDialog.Builder(this)
                .title("Introduce tu Numcode")
                .content("Parece ser que no tenemos tu Numcode, introducelo")
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
                       writeNumCodePrefs(dialog.getInputEditText().getText().toString());
                        onProfile();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = "http://www.numcode.com/nouveau/";
                        new FinestWebView.Builder(getApplicationContext()).show(url);
                    }
                })
                .show();
    }
    public  String getNumCodePrefs(){
        return   sharedPref.getString("numcode", "");
    }
    public void writeNumCodePrefs(String numcode){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("numcode", numcode);
        editor.commit();
    }
}
