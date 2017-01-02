package jcollado.pw.numcode.Utils;


import android.content.SharedPreferences;


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



    }
