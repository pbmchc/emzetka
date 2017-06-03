package com.pbmchc.emzetka.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.pbmchc.emzetka.mzkandroid.R;

/**
 * Created by Piotrek on 2017-01-20.
 */
public class NetworkHelper {

    public static void checkErrorCause(Context context){
        if(isConnectedOrConnecting(context))
            Toast.makeText(context, context.getString(R.string.server_error),
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, context.getString(R.string.internet_error),
                    Toast.LENGTH_SHORT).show();
    }

    public static void checkInternetConnection(Context context){
        if(!isConnectedOrConnecting(context))
            Toast.makeText(context, context.getString(R.string.internet_error),
                    Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnectedOrConnecting(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
