package jcollado.pw.numcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.thefinestartist.finestwebview.FinestWebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       String url = "http://www.numcode.com/nouveau/";
        new FinestWebView.Builder(this).show(url);
    }
}
