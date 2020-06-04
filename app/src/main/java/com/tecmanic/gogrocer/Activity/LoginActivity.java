package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.BASE_URL;
import static com.tecmanic.gogrocer.Config.BaseURL.Login;

public class LoginActivity extends AppCompatActivity {

    Button SignIn;
    TextView forgotPAss,btnignUp;
    EditText etMob,etPass;
    String androidID , token;
    ProgressDialog progressDialog;
    protected void attachBaseContext(Context newBase) {

        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        token = FirebaseInstanceId.getInstance().getToken();
        init();
    }

    private void init() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        etMob=findViewById(R.id.etMob);
        etPass=findViewById(R.id.etPass);
        SignIn=findViewById(R.id.btn_Login);
        forgotPAss=findViewById(R.id.btn_ForgotPass);
        btnignUp=findViewById(R.id.btn_Signup);


        forgotPAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassOtp.class);
                startActivity(intent);

            }
        });
        btnignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMob.getText().toString().trim().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Mobile Number required!",Toast.LENGTH_SHORT).show(); }
                else if (etMob.getText().toString().trim().length()<9) {
                    Toast.makeText(getApplicationContext(),"Valid Mobile Number required!",Toast.LENGTH_SHORT).show(); }
                else if(etPass.getText().toString().trim().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Password required!",Toast.LENGTH_SHORT).show();
                }
                else if(!isOnline()){
                    Toast.makeText(getApplicationContext(),"Please check your Internet Connection!",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    loginUrl();
                    progressDialog.dismiss();
                }            }
        });
    }

    private void loginUrl() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Login",response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray( "data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            String user_id = obj.getString("user_id");
                            String user_fullname = obj.getString("user_name");
                            String user_email = obj.getString("user_email");
                            String user_phone = obj.getString("user_phone");
                            String password = obj.getString("user_password");
                        /*    String user_image = obj.getString("user_image");
                            String wallet_ammount = obj.getString("wallet");
                            String reward_points = obj.getString("rewards");
*/
                        progressDialog.dismiss();
                            Session_management sessionManagement = new Session_management(LoginActivity.this);
                            SharedPreferences.Editor editor = getSharedPreferences(BaseURL.MyPrefreance, MODE_PRIVATE).edit();
                            editor.putString(BaseURL.KEY_MOBILE, user_phone);
                            editor.putString(BaseURL.KEY_PASSWORD, password);
                            editor.apply();
                            sessionManagement.createLoginSession(user_id, user_email, user_fullname, user_phone, password);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);


                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error",error.getMessage());
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();

                param.put("user_phone",etMob.getText().toString());
                param.put("user_password",etPass.getText().toString());
                param.put("device_id",token);
                Log.d("ff",token);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }



    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
