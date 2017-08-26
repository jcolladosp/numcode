package jcollado.pw.numcode.Utils;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;



/**
 * Created by colla on 02/01/2017.
 */

public class Functions   {
    public static String getNumCodePrefs(SharedPreferences sharedPref){
        return   sharedPref.getString("numcode", "");
    }
    public static void writeNumCodePrefs(SharedPreferences sharedPref,String numcode){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("numcode", numcode);
        editor.commit();
    }
    public static void changeLocale(Resources res, String locale) {

        Configuration config;
        config = new Configuration(res.getConfiguration());


        switch (locale) {
            case "es":
                config.locale = new Locale("es");
                break;
                      case "fr":
                config.locale = Locale.FRENCH;
                break;
            default:
                config.locale = Locale.ENGLISH;
                break;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

 /*
    void numcodeExists(){
        noExists = false;

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
                        if(!res.contains("<div id=\"Nom\"> </div>")&& !res.contains("<div class=\"espacecoordonnee\"> -  - </div>")){
                            numcodeExists();
                        }
                        else{
                            createXD();
                        }
                    }
                });
    }
    */



    }
