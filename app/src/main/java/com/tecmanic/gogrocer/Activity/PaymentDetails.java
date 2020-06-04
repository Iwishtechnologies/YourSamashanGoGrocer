package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tecmanic.gogrocer.Adapters.ComplainAdapter;
import com.tecmanic.gogrocer.Adapters.Coupun_Listing_Adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Config.SharedPref;
import com.tecmanic.gogrocer.Fragments.HomeeeFragment;
import com.tecmanic.gogrocer.ModelClass.ComplainModel;
import com.tecmanic.gogrocer.ModelClass.CoupunModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.NetworkConnection;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;
import static com.tecmanic.gogrocer.Config.BaseURL.Wallet_CHECKOUT;

public class PaymentDetails extends AppCompatActivity implements PaymentResultListener {

    LinearLayout backcart;
    int status = 0;
    String payment_method;
    Button confirm;
    String wallet_amount= "00" ;
    TextView reset_text;
    String wallet_status = "no";

String total_amt;
    private DatabaseHandler db_cart;
    private Session_management sessionManagement;
    TextView payble_ammount, my_wallet_ammount, used_wallet_ammount, used_coupon_ammount, order_ammount;
    private String getlocation_id = "";
    private String getstore_id = "";

    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private Double rewards;
    RadioButton rb_Store, rb_Cod, rb_card, rb_Netbanking, rb_paytm;
    CheckBox checkBox_Wallet, checkBox_coupon;
    EditText et_Coupon;
    String getvalue;
    String text;
    String cp;
    String Used_Wallet_amount;
    String total_amount;
    String order_total_amount;
    TextView linea , lineb;
    RadioGroup radioGroup;

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),OrderSummary.class);

        startActivity(intent);
        super.onBackPressed();
    }

    String Prefrence_TotalAmmount;
    String lat, lng;
    String getwallet;
    LinearLayout Promo_code_layout, Coupon_and_wallet;
    RelativeLayout Apply_Coupon_Code, Relative_used_wallet, Relative_used_coupon;
    List<CoupunModel> coupunModelList = new ArrayList<>();
    RecyclerView coupen_recycler;

    SharedPreferences sharedPreferences12;
    SharedPreferences.Editor editor12;
    String code, cart_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        backcart = findViewById(R.id.backcart);
        confirm = findViewById(R.id.confirm_order);
        cart_id = OrderSummary.cart_id;
linea =  findViewById(R.id.line1);
lineb = findViewById(R.id.line2);

        reset_text = findViewById(R.id.reset_text);

        reset_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PaymentDetails.class);

                intent.putExtra("order_amt",total_amt);
                startActivity(intent);            }
        });
        total_amt = getIntent().getStringExtra("order_amt");
        backcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        Prefrence_TotalAmmount = SharedPref.getString(getApplicationContext(), BaseURL.TOTAL_AMOUNT);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                getvalue = radioButton.getText().toString();
            }
        });

        sharedPreferences12 = getSharedPreferences("loction", MODE_PRIVATE);
        editor12 = sharedPreferences12.edit();
        lat = sharedPreferences12.getString("lat", "77.1111");
        lng = sharedPreferences12.getString("lng", "22.02002");

        Log.e("LAnt", lat + "\n" + lng);


//        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        checkBox_Wallet = (CheckBox) findViewById(R.id.use_wallet);
//        checkBox_Wallet.setTypeface(font);
        rb_Store = (RadioButton) findViewById(R.id.use_store_pickup);
//        rb_Store.setTypeface(font);
        rb_Cod = (RadioButton) findViewById(R.id.use_COD);
//        rb_Cod.setTypeface(font);
        rb_card = (RadioButton) findViewById(R.id.use_card);
//        rb_card.setTypeface(font);
        rb_Netbanking = (RadioButton) findViewById(R.id.use_netbanking);
//        rb_Netbanking.setTypeface(font);
        rb_paytm = (RadioButton) findViewById(R.id.use_wallet_ammount);
//        rb_paytm.setTypeface(font);
        checkBox_coupon = (CheckBox) findViewById(R.id.use_coupon);
        checkBox_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Coupen.class));
            }
        });
        et_Coupon = (EditText) findViewById(R.id.et_coupon_code);

        Promo_code_layout = (LinearLayout) findViewById(R.id.prommocode_layout);
        Apply_Coupon_Code = (RelativeLayout) findViewById(R.id.apply_coupoun_code);

        code = getIntent().getStringExtra("code");

        if (code == null) {
            code = "";
            status = 1;
            Promo_code_layout.setVisibility(View.GONE);
            checkBox_coupon.setChecked(false);

        } else {

            checkBox_coupon.setChecked(true);
            et_Coupon.setText(code);
            Promo_code_layout.setVisibility(View.VISIBLE);
        }

        Log.d("dff", code);
//        checkBox_coupon.setTypeface(font);

        Apply_Coupon_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Coupen.class));
                apply();
            }
        });


        sessionManagement = new Session_management(this);


        Coupon_and_wallet = (LinearLayout) findViewById(R.id.coupon_and_wallet);
        Relative_used_wallet = (RelativeLayout) findViewById(R.id.relative_used_wallet);
        Relative_used_coupon = (RelativeLayout) findViewById(R.id.relative_used_coupon);

        //Show  Wallet
        getwallet = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
        my_wallet_ammount = (TextView) findViewById(R.id.user_wallet);
        db_cart = new DatabaseHandler(this);
        payble_ammount = (TextView) findViewById(R.id.payable_ammount);
        order_ammount = (TextView) findViewById(R.id.order_ammount);
        used_wallet_ammount = (TextView) findViewById(R.id.used_wallet_ammount);
        used_coupon_ammount = (TextView) findViewById(R.id.used_coupon_ammount);
        order_ammount.setText(total_amt +" "+ getApplicationContext().getString(R.string.currency) );

        getRefresrh();
        rewardliness();

        sessionManagement = new Session_management(this);
        final String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        final String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        final String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status==1){
                    startActivity(new Intent(getApplicationContext(),OrderSuccessful.class));

                    makeAddOrderRequest(getuser_id,cart_id,payment_method,wallet_status,"success");
                    return;
                }
                if (status==2){
                    startActivity(new Intent(getApplicationContext(),OrderSuccessful.class));
                    makeAddOrderRequest(getuser_id,cart_id,payment_method,wallet_status,"success");

return;
                }
                if (status== 3){
                    startPayment(name,total_amt,email,mobile);
                    return;
                }

                if (status==0){

                    Toast.makeText(PaymentDetails.this, "Select One plz", Toast.LENGTH_SHORT).show();
                }

//                    confirm.setEnabled(false);
//                    if (checkBox_Wallet.isChecked()){
//
//
//                        getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
//
//                        Usewalletfororder(getuser_id,Used_Wallet_amount);
//                        checked();
//
//                    }
//                    else {
//                        checked();
//
//                    }
//
//
//
//                } else {
//                    confirm.setEnabled(true);
//
//                    //  ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
//                }

            }
        } );
//





        checkBox_Wallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
//                    Use_Wallet_Ammont();

                    int amt = Integer.parseInt(total_amt);
                    int wallet = Integer.parseInt(wallet_amount);

                    if (amt<=wallet )
                    {
                     int balnce = wallet- amt;
                        my_wallet_ammount.setText(balnce + getApplicationContext().getString(R.string.currency));
                        wallet_status = "yes";
                        payment_method = "wallet";
                        radioGroup.setEnabled(false);
                        rb_Cod.setEnabled(false);
                        rb_card.setEnabled(false);
                        checkBox_coupon.setEnabled(false);


                    }
                    else
                    {

                        startActivity(new Intent(getApplicationContext(),RechargeWallet.class));
                    }
                    Coupon_and_wallet.setVisibility(View.VISIBLE);
                    Relative_used_wallet.setVisibility(View.VISIBLE);
                    if (rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
                        rb_card.setChecked(false);
                        rb_Netbanking.setChecked(false);
                        rb_paytm.setChecked(false);
                    }
//                } else {
//                    if (payble_ammount != null) {
//                        rb_Cod.setText(getResources().getString(R.string.cash));
//                        rb_card.setClickable(true);
//                        rb_card.setTextColor(getResources().getColor(R.color.black));
//                        rb_Netbanking.setClickable(true);
//                        rb_Netbanking.setTextColor(getResources().getColor(R.color.black));
//                        rb_paytm.setClickable(true);
//                        rb_paytm.setTextColor(getResources().getColor(R.color.black));
//                        checkBox_coupon.setClickable(true);
//                        checkBox_coupon.setTextColor(getResources().getColor(R.color.black));
//                    }
//                    final String Ammount = SharedPref.getString(getApplicationContext(), BaseURL.TOTAL_AMOUNT);
//                    final String WAmmount = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
//                    my_wallet_ammount.setText(WAmmount + getApplicationContext().getResources().getString(R.string.currency));
//                    payble_ammount.setText(Ammount + getResources().getString(R.string.currency));
//                    used_wallet_ammount.setText("");
//                    Relative_used_wallet.setVisibility(View.GONE);
//                    if (checkBox_coupon.isChecked()) {
//                        final String ammount = SharedPref.getString(getApplicationContext(), BaseURL.COUPON_TOTAL_AMOUNT);
//                        payble_ammount.setText(ammount + getResources().getString(R.string.currency));
//                    }
                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    checkBox_coupon.setEnabled(false);
                    checkBox_Wallet.setEnabled(false);
                    // Changes the textview's text to "Checked: example radiobutton text"
                    Log.d("afs", checkedRadioButton.getText().toString());

                    if (checkedRadioButton.getText().equals("COD"))

                    {
                        status = 2;
                        payment_method = "COD";
                        wallet_status = "no";
                        return;
                    }
                    if (checkedRadioButton.getText().equals("Credit/Debit Card / Net Banking"));

                    {
                        status =3 ;
                        payment_method = "cards";
                        wallet_status = "no";

                    }

                }
            }
        });





        checkBox_coupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Promo_code_layout.setVisibility(View.GONE);
                    Coupon_and_wallet.setVisibility(View.GONE);
                    Relative_used_coupon.setVisibility(View.VISIBLE);
                    if (rb_Store.isChecked() || rb_Cod.isChecked() || rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
                        rb_Store.setChecked(false);
                        rb_Cod.setChecked(false);
                        rb_card.setChecked(false);
                        rb_Netbanking.setChecked(false);
                        rb_paytm.setChecked(false);
                    }
                } else {
                    et_Coupon.setText(code);
                    Promo_code_layout.setVisibility(View.VISIBLE);

                }
            }
        });

        rb_Cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    Promo_code_layout.setVisibility(View.GONE);
//                    Coupon_and_wallet.setVisibility(View.GONE);
                    checkBox_Wallet.setChecked(false);
                    wallet_status = "no";
//                    Relative_used_coupon.setVisibility(View.VISIBLE);
//                    if (rb_Store.isChecked() || rb_Cod.isChecked() || rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
//                        rb_Store.setChecked(false);
//                        rb_Cod.setChecked(false);
//                        rb_card.setChecked(false);
//                        rb_Netbanking.setChecked(false);
//                        rb_paytm.setChecked(false);
//                    }
                } else {
//                    et_Coupon.setText(code);
//                    Promo_code_layout.setVisibility(View.VISIBLE);

                }
            }
        });




                        checked();

    }


    private void rewardliness() {

        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id",cart_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.rewardlines, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {



                        String  line1 = response.getString("line1");
                        String  line2 = response.getString("line2");

                        linea.setText(line1);
                        lineb.setText(line2);


                    }



                    else{


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().getRequestQueue().add(jsonObjReq);


    }


    private void apply() {
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id", cart_id);
        params.put("coupon_code", code);
        Log.d("ssd", cart_id);
        Log.d("dd", code);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.apply_coupon, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {

                        JSONObject jsonObject = response.getJSONObject("data");

                        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void attemptOrder() {
        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
        rewards = Double.parseDouble(db_cart.getColumnRewards());
        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);
                JSONObject jObjP = new JSONObject();
                try {
                    jObjP.put("product_id", map.get("product_id"));
                    jObjP.put("qty", map.get("qty"));
                    jObjP.put("unit_value", map.get("unit_value"));
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    jObjP.put("rewards", map.get("rewards"));
                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + getstore_id + "\ndata:" + passArray.toString());


//                makeAddOrderRequest(cart_id,getuser_id);


            }
        }
    }

    private void makeAddOrderRequest(String userid, String cart_id,String payment_method, String wallet_status,String payment_status ) {
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
//        params.put("date", date);
//        params.put("time", gettime);
        params.put("user_id", userid);
        params.put("payment_status", payment_status);
        params.put("cart_id", cart_id);
//        params.put("total_ammount",total_amount);
        params.put("payment_method", payment_method);
        params.put("wallet", wallet_status);
//        params.put("lat",lat);
//        params.put("lng",lng);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    String status = response.getString("status");
                    String message = response.getString("message");
                    if (status.contains("1")) {
                        JSONObject jsonObject = response.getJSONObject("data");

                        db_cart.clearCart();
                        Intent intent = new Intent(getApplicationContext(), OrderSuccessful.class);
                        intent.putExtra("msg", message);
                        startActivity(intent);

                        Toast.makeText(PaymentDetails.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
               /* if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Usewalletfororder(String userid, String Wallet) {
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);
        params.put("wallet_amount", Wallet);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST, Wallet_CHECKOUT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    String status = response.getString("responce");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Use_Wallet_Ammont() {
        final String Wallet_Ammount = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
        final String Coupon_Ammount = SharedPref.getString(getApplicationContext(), BaseURL.COUPON_TOTAL_AMOUNT);
        final String Ammount = SharedPref.getString(getApplicationContext(), BaseURL.TOTAL_AMOUNT);
        if (NetworkConnection.connectionChecking(getApplicationContext())) {
            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.BASE_URL + Wallet_CHECKOUT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray Jarray = object.getJSONArray("final_amount");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject json_data = Jarray.getJSONObject(i);
                                    String Wallet_amount = json_data.getString("wallet");
                                    Used_Wallet_amount = json_data.getString("used_wallet");
                                    total_amount = json_data.getString("total");
                                    if (total_amount.equals("0")) {
                                        rb_Cod.setText("Home Delivery");
                                        getvalue = rb_Cod.getText().toString();
                                        rb_card.setClickable(false);
                                        rb_card.setTextColor(getResources().getColor(R.color.hintColor));
                                        rb_Netbanking.setClickable(false);

                                        rb_Netbanking.setTextColor(getResources().getColor(R.color.hintColor));
                                        rb_paytm.setClickable(false);
                                        rb_paytm.setTextColor(getResources().getColor(R.color.hintColor));
                                        checkBox_coupon.setClickable(false);
                                        checkBox_coupon.setTextColor(getResources().getColor(R.color.hintColor));
                                    } else {
                                        if (total_amount != null) {
                                            rb_Cod.setText("Cash On Delivery");
                                            rb_card.setClickable(true);
                                            rb_card.setTextColor(getResources().getColor(R.color.black));
                                            rb_Netbanking.setClickable(true);
                                            rb_Netbanking.setTextColor(getResources().getColor(R.color.black));
                                            rb_paytm.setClickable(true);
                                            rb_paytm.setTextColor(getResources().getColor(R.color.black));
                                            checkBox_coupon.setClickable(true);
                                            checkBox_coupon.setTextColor(getResources().getColor(R.color.black));
                                        }
                                    }
                                    payble_ammount.setText(total_amount + getResources().getString(R.string.currency));
                                    used_wallet_ammount.setText("(" + getResources().getString(R.string.currency) + Used_Wallet_amount + ")");
                                    SharedPref.putString(getApplicationContext(), BaseURL.WALLET_TOTAL_AMOUNT, total_amount);
                                    my_wallet_ammount.setText(Wallet_amount + getResources().getString(R.string.currency));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (checkBox_Wallet.isChecked()) {
                        params.put("wallet_amount", Wallet_Ammount);
                    } else {
                        params.put("total_amount", Ammount);

                    }

                    if (checkBox_coupon.isChecked()) {
                        params.put("total_amount", Coupon_Ammount);
                    } else {
                        params.put("total_amount", Ammount);

                    }
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(getApplicationContext(), NetworkError.class);
            startActivity(intent);
        }
    }

    private void checked() {
        if (checkBox_Wallet.isChecked()) {
            if (rb_Store.isChecked() || rb_Cod.isChecked()) {
                attemptOrder();
            } else {
                Toast.makeText(getApplicationContext(), "Please Select One", Toast.LENGTH_SHORT).show();
            }

        }
        if (rb_Store.isChecked()) {
            attemptOrder();
        }
        if (rb_Cod.isChecked()) {

            attemptOrder();
        }
        if (rb_card.isChecked()) {
            Intent myIntent = new Intent(getApplicationContext(), PaymentGatWay.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent.putExtra("total", total_amount);
            } else {
                myIntent.putExtra("total", Prefrence_TotalAmmount);
                myIntent.putExtra("getdate", getdate);
                myIntent.putExtra("gettime", gettime);
                myIntent.putExtra("getlocationid", getlocation_id);
                myIntent.putExtra("getstoreid", getstore_id);
                myIntent.putExtra("getpaymentmethod", getvalue);
            }
            getApplicationContext().startActivity(myIntent);
        }
        if (rb_Netbanking.isChecked()) {
            Intent myIntent1 = new Intent(getApplicationContext(), PaymentGatWay.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getApplicationContext().startActivity(myIntent1);
        }
        if (rb_paytm.isChecked()) {
            Intent myIntent1 = new Intent(getApplicationContext(), Paytm.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getApplicationContext().startActivity(myIntent1);

        }
        if (checkBox_coupon.isChecked()) {
            if (rb_Store.isChecked() || rb_Cod.isChecked()) {
                attemptOrder();
            } else {
                Toast.makeText(getApplicationContext(), "Select Store Or Cod", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void getRefresrh() {
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, BaseURL.WALLET_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Log.d("dfsd",jObj.toString());
//                            if (jObj.optString("success").equalsIgnoreCase("success")) {
                                 wallet_amount = jObj.getString("data");
                                payble_ammount.setText(wallet_amount + " "+getApplicationContext().getString(R.string.currency));

                            my_wallet_ammount.setText(getApplicationContext().getString(R.string.currency)+wallet_amount);

//                                payble_ammount.setText(wallet_amount);
//                                Wallet_Ammount.setText(wallet_amount);
//                                SharedPref.putString(getActivity(), BaseURL.KEY_WALLET_Ammount, wallet_amount);
//                            } else {
//                                 Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };
        rq.add(strReq);
    }

    public void startPayment(String name,String amount,String email,String phone) {

/*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */



        final Activity activity = this;

        final Checkout co = new Checkout();

        try {

            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");

            options.put("amount", Integer.parseInt(amount)*100);

            JSONObject preFill = new JSONObject();

            preFill.put("email", email);

            preFill.put("contact", phone);

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }



    @Override
    public void onPaymentSuccess(String s) {

        makeAddOrderRequest(getuser_id,cart_id,payment_method,wallet_status,"success");


    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }


}
