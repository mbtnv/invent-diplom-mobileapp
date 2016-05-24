package com.example.butunov.mysqlconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static String url_create_chk = "http://sa7833-15393.smrtp.ru/create_chk.php";
    private static String url_get_devices_details = "http://sa7833-15393.smrtp.ru/get_devices_details.php";
    private static String url_db_login = "http://sa7833-15393.smrtp.ru/db_login.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEVICE = "device";
    private static final String TAG_INV = "invn";
    private static final String TAG_TYPE = "type";
    private static final String TAG_MARKA = "marka";
    private static final String TAG_SN = "sn";
    private static final String TAG_EIN = "ein";
    private static final String TAG_FIO = "fio";
    private static final String TAG_KAB = "kab";
    private static final String TAG_OPIS = "opis";
    private static final String TAG_AUTH = "auth";
    private static final String TAG_FIOAUTH = "fio";

    String db_log;
    String db_pass;
    String fio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        displayAlertDialog(); //авторизация
        final EditText editTextinv= (EditText) findViewById(R.id.editText);
        editTextinv.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Toast.makeText(getBaseContext(), editTextinv.getText(), Toast.LENGTH_SHORT).show();
                    searchinv();
                    return true;
                }
                return false;
            }});
        }


    public void searchinv(){

        new GetProductDetails().execute();
    }

    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final EditText etUsername = (EditText) alertLayout.findViewById(R.id.et_Username);
        final EditText etPassword = (EditText) alertLayout.findViewById(R.id.et_Password);
        final CheckBox cbShowPassword = (CheckBox) alertLayout.findViewById(R.id.cb_ShowPassword);

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etPassword.setTransformationMethod(null);
                else
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Авторизация");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Выход", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        });

        alert.setPositiveButton("Войти", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password
                db_log = etUsername.getText().toString();
                db_pass = etPassword.getText().toString();
                new GetLogin().execute();

                //Toast.makeText(getBaseContext(), "Username: " + user + " Password: " + pass, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void scanclick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
            EditText editTextinvn = (EditText)findViewById(R.id.editText);
            editTextinvn.setText(re);
            searchinv();
        }
        else if (resultCode == RESULT_CANCELED) {
            Log.i("App","Scan unsuccessful");
            Toast.makeText(getBaseContext(), "Сканирование отменено", Toast.LENGTH_LONG).show();
        }
    }

    public void startf(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    class CreateChk extends AsyncTask<String, String, String> {

        protected String doInBackground(String[] args) {

            Date now = new Date();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String datech = formatter.format(now);
            EditText editTextinv= (EditText) findViewById(R.id.editText);
            String inv = editTextinv.getText().toString();
            String sotr = "admin";

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("inv", inv));
            params.add(new BasicNameValuePair("date", datech));
            params.add(new BasicNameValuePair("sotr", sotr));

            JSONObject json = jsonParser.makeHttpRequest(url_create_chk, "POST", params);

            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class GetProductDetails extends AsyncTask<String, String, String> {
        EditText editTextinv= (EditText) findViewById(R.id.editText);
        String inv = editTextinv.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("228Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected String doInBackground(String[] params) {

            runOnUiThread(new Runnable() {
                public void run() {
                    int success;
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("invn", inv));

                        JSONObject json = jsonParser.makeHttpRequest(url_get_devices_details, "GET", params);

                        Log.d("Single Product Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            new CreateChk().execute();
                            JSONArray devicetObj = json.getJSONArray(TAG_DEVICE);

                            // получаем первый обьект с JSON Array
                            JSONObject device = devicetObj.getJSONObject(0);

                            TextView deviceTextView = (TextView) findViewById(R.id.textView);
                            deviceTextView.setText("Инвентарный №:"+device.getString(TAG_INV)+"\n"+
                                    "Тип устройства:"+device.getString(TAG_TYPE)+"\n"+
                                    "Производитель:"+device.getString(TAG_MARKA)+"\n"+
                                    "Описание:"+device.getString(TAG_OPIS)+"\n"+
                                    "Серийный №:"+device.getString(TAG_SN)+"\n"+
                                   // "ЕИН:"+device.getString(TAG_EIN)+"\n"+
                                    "МОЛ:"+device.getString(TAG_FIO)+"\n"+
                                    "Кабинет:"+device.getString(TAG_KAB)+"\n");

                        }else{
                            Toast.makeText(getBaseContext(), "Устройство с инвентарным №"+inv+" не найдено", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }

    class GetLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected String doInBackground(String[] params) {

            runOnUiThread(new Runnable() {
                public void run() {
                    int success;
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("login", db_log));
                        params.add(new BasicNameValuePair("pass", db_pass));
                        Log.d("log", db_log);
                        Log.d("pass", db_pass);
                        JSONObject json = jsonParser.makeHttpRequest(url_db_login, "GET", params);

                        Log.d("Single Product Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            JSONArray authObj = json.getJSONArray(TAG_AUTH);

                            JSONObject auth = authObj.getJSONObject(0);

                            fio=auth.getString(TAG_FIOAUTH);
                            Toast.makeText(getBaseContext(), fio, Toast.LENGTH_LONG).show();

                        }else{

                            Toast.makeText(getBaseContext(), "Логин и пароль не верны", Toast.LENGTH_LONG).show();
                            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }


        protected void onPostExecute(String file_url) {
            // закрываем диалог прогресс
            pDialog.dismiss();
        }
    }
}
