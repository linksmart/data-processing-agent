package org.fraunhofer.fit.almanac.protocols;

import android.util.Log;

import org.fraunhofer.fit.almanac.almanaccitizenapp.Config;
import org.fraunhofer.fit.almanac.model.PicIssue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by devasya on 27.07.2015.
 */
public class HttpRequester {
    String TAG = "CitizenApp/HttpRequester";
    static int count = 1;

    public String publishIssue(String issueJSON){
        if(Config.TESTLOCALLY) {
            count =(int) (Math.random()*1000.0);
            return " { \"ticketId\" : \"" + Integer.toString(count) + "\", \"eventType\": \"created\" } ";
        }else {

            String retStr = null;
            try {

                URL url = new URL("http://almanac.fit.fraunhofer.de:8888/waste/ticket");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
             //   conn.setConnectTimeout(120000);
              //  conn.setReadTimeout(120000);


                OutputStream os = conn.getOutputStream();
                os.write(issueJSON.getBytes());
                os.flush();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG,"Something terribly went wrong in server");
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                String location = conn.getHeaderField("Location");
                retStr = location.substring(location.lastIndexOf("/") + 1);
                Log.i(TAG, "Created successfully id:"+retStr);
//                String output;
//                StringBuilder retVal = new StringBuilder();
//                Log.i(TAG, "Output from Server .... \n");
//                while ((output = br.readLine()) != null) {
//                    retVal.append(output);
//                }

                conn.disconnect();
//                retStr = retVal.toString();
            } catch (MalformedURLException e) {

                Log.e(TAG,e.toString());

            } catch (IOException e) {

                Log.e(TAG, e.toString());

            }
            return retStr;
        }
    }
}
