package com.example.butunov.mysqlconnect;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity2 extends ActionBarActivity {

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    //private static String url_create_chk = "http://10.0.2.2/create_chk.php";
    private static String url_create_chk = "http://sa7833-15393.smrtp.ru/create_chk.php";
    private static String url_get_devices_details = "http://sa7833-15393.smrtp.ru/get_devices_details.php";

    private static final String TAG_SUCCESS = "success";

    EditText inputInv;
    EditText inputSotr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        inputInv = (EditText) findViewById(R.id.editText2);
        inputSotr = (EditText) findViewById(R.id.editText3);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void posting(View view) {
        new CreateChk().execute();
    }

    class CreateChk extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity2.this);
            pDialog.setMessage("Создание продукта...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String[] args) {

            Date now = new Date();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String datech = formatter.format(now);
            String inv = inputInv.getText().toString();

            String sotr = inputSotr.getText().toString();


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("inv", inv));
            params.add(new BasicNameValuePair("date", datech));
            params.add(new BasicNameValuePair("sotr", sotr));



            Log.d("asdasd",sotr);

            JSONObject json = jsonParser.makeHttpRequest(url_create_chk, "POST", params);

            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(i);


                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }

    }
}
