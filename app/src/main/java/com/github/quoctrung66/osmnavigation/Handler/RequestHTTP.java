package com.github.quoctrung66.osmnavigation.Handler;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class RequestHTTP {
    //TAG name
    private static final String TAG = RequestHTTP.class.getSimpleName();

    public static String readUrl(String urlString) throws Exception {
        Log.i(TAG + " URL", urlString);
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
