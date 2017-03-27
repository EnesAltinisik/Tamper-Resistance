package com.example.enes.clientforcert;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
public class MainActivity extends AppCompatActivity {
    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        loadData();
    }

    private void loadData() {
        new FetchTask().execute(checkAppSignature(this));
    }
    public class FetchTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sign = params[0];
            String[] ret = new String[1];

            try {

                Socket s = new Socket("192.168.56.1", 4444);

                BufferedReader input =
                        new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(
                        s.getOutputStream(), true);
                input.readLine();
                out.println(sign);
                input.readLine();
                ret[0]=input.readLine();
                if(ret[0].equals("Knock! Knock!"))
                    ret[0]="imzanda sorun var";


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return ret;
        }
        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                for (String string : data) {
                    mWeatherTextView.append(string + "\n\n\n");
                }
            }
        }
    }
    public static String checkAppSignature(Context context) {

        try {

            PackageInfo packageInfo = context.getPackageManager()

                    .getPackageInfo(context.getPackageName(),

                            PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                return  Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //compare signatures


            }

        } catch (Exception e) {

//assumes an issue in checking signature., but we let the caller decide on what to do.

        }

        return null;

    }
}
