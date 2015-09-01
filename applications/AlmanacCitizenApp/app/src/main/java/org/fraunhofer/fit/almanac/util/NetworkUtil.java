package org.fraunhofer.fit.almanac.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by devasya on 01.09.2015.
 */
public class NetworkUtil {
    public static final boolean isConnectionAvailable(Context context)
    {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo.State mobile = cm.getNetworkInfo(0).getState();

        NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();

        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
        {
            return true;
        }
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
        {
            return true;
        }

        return false;
    }
}
