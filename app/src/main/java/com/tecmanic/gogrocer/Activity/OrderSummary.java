package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.gogrocer.Adapters.Cart_adapter;
import com.tecmanic.gogrocer.Adapters.ComplainAdapter;
import com.tecmanic.gogrocer.Adapters.SearchAdapter;
import com.tecmanic.gogrocer.Adapters.Timing_Adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Constans.RecyclerTouchListener;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.ModelClass.ComplainModel;
import com.tecmanic.gogrocer.ModelClass.DeliveryModel;
import com.tecmanic.gogrocer.ModelClass.SearchModel;
import com.tecmanic.gogrocer.ModelClass.timing_model;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import static com.tecmanic.gogrocer.Config.BaseURL.CalenderUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.MinMaxOrder;
import static com.tecmanic.gogrocer.Config.BaseURL.OrderContinue;
import static com.tecmanic.gogrocer.Config.BaseURL.SelectAddressURL;
import static com.tecmanic.gogrocer.Config.BaseURL.SocietyListUrl;

public class OrderSummary extends AppCompatActivity {
    Session_management session_management;
    String total_atm;
    Button btn_AddAddress;
    LinearLayout back;
    TextView btn_Contine,txt_deliver,btn_Change,txtTotalItems,pPrice,pMrp,totalItms,price,DeliveryCharge,Amounttotal,txt_totalPrice;
    String dname;
    JSONArray array ;

    public static  String cart_id;

    RecyclerView recycler_itemsList,recyclerTimeSlot;
    HorizontalCalendar horizontalCalendar;
    private ArrayList<timing_model> dateDayModelClasses1;
    Timing_Adapter bAdapter1;
    String timeslot;
    String addressid,totalprice;
    String user_id;
    ProgressDialog progressDialog;

    String minVAlue,maxValue;
    private List<CartModel> cartList = new ArrayList<>();
    private DatabaseHandler db;
    private Session_management sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        sessionManagement = new Session_management(getApplicationContext());
        user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        array = new JSONArray();

        init();
    }

    private void init() {
        session_management = new Session_management(OrderSummary.this);
        Log.d("hj",session_management.getUserDetails().get(BaseURL.KEY_ID));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        sessionManagement = new Session_management(this);
        sessionManagement.cleardatetime();
        db = new DatabaseHandler(this);

        dname=getIntent().getStringExtra("dName");
        addressid=getIntent().getStringExtra("dId");
        btn_AddAddress=findViewById(R.id.btn_AddAddress);
        btn_Change=findViewById(R.id.btn_Change);
        txtTotalItems=findViewById(R.id.txtTotalItems);
        btn_Contine=findViewById(R.id.btn_Contine);
        txt_deliver=findViewById(R.id.txt_deliver);
        recycler_itemsList=findViewById(R.id.recycler_itemsList);
        recyclerTimeSlot=findViewById(R.id.recyclerTimeSlot);
        pPrice=findViewById(R.id.pPrice);
        pMrp=findViewById(R.id.pMrp);
        totalItms=findViewById(R.id.totalItms);
        price=findViewById(R.id.price);
        DeliveryCharge=findViewById(R.id.DeliveryCharge);
        Amounttotal=findViewById(R.id.Amounttotal);
        txt_totalPrice=findViewById(R.id.txt_totalPrice);
        back=findViewById(R.id.back);

        totalprice=DeliveryCharge.getText().toString()+price.getText().toString();
        DeliveryCharge.setText(totalprice);

        Amounttotal.setText(totalprice);
deliverychrge();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btn_AddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent In=new Intent(getApplicationContext(),SelectAddress.class);
                startActivity(In);            }
        });

        btn_Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent In=new Intent(getApplicationContext(),SelectAddress.class);
                startActivity(In);            }
        });



        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        String todaydate= String.valueOf(DateFormat.format("yyyy-MM-dd",startDate));
        dateDayModelClasses1 = new ArrayList<>();
        Log.d("shdhf",todaydate);

        horizontalCalendar = new HorizontalCalendar.Builder(OrderSummary.this,R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .textSize(11f, 20f, 12f)
                .showTopText(true)
                .textColor(Color.GRAY, Color.BLACK)
                .end()
                .build();

        recycler_itemsList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_itemsList, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String time_slot = dateDayModelClasses1.get(position).getTiming();
                timeslot = time_slot;

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                dateDayModelClasses1.clear();
                String todaydatee = String.valueOf(DateFormat.format("yyyy-MM-dd", date));
                makeGetAddressRequests(todaydatee);
            }

        });

     //   makeGetAddressRequest(todaydate);

        ArrayList<HashMap<String, String>> map = db.getCartAll();

            try {
                JSONArray object = new JSONArray(map);
                for (int i = 0 ; i<object.length();i++)

                {
                    Log.d("sadf",object.toString());
                    JSONObject object1 = object.getJSONObject(i);

                    JSONObject product_array = new JSONObject();

                    product_array.put("qty",object1.getString("qty"));
                    product_array.put("varient_id",object1.getString("varient_id"));

                    Log.d("sdf",product_array.toString());
                    array.put(product_array);


                }




            } catch (JSONException e) {
                e.printStackTrace();
            }





        JSONObject object = new JSONObject();


        Log.d("sdfa",array.toString());
        Cart_adapter adapter = new Cart_adapter(this, map);


        recycler_itemsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        updateData();

        Toast.makeText(getApplicationContext(),"Please select a time slot on available date!",Toast.LENGTH_LONG).show();
        minimaxAmount();
        btn_Contine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timeslot = bAdapter1.gettimeslot();
                continueUrl(todaydate, timeslot, map);
                /*if(txt_totalPrice.getText().toString()!=minVAlue){
                    Toast.makeText(getApplicationContext(),"Cart amount must be more than "+minVAlue,Toast.LENGTH_SHORT).show();
                }else {
                    timeslot = bAdapter1.gettimeslot();
                    continueUrl(todaydate, timeslot, map);
                }*/
            }
        });

    }

    private void minimaxAmount() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MinMaxOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MinMaxOrder value",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String id = jsonObject1.getString("min_max_id");
                        String min = jsonObject1.getString("min_value");
                        String max = jsonObject1.getString("max_value");

                        minVAlue=min.toString();
                        maxValue=max.toString();

                        Log.d("minmax",minVAlue+" "+maxValue);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummary.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void continueUrl(final String todaydate,final String timeslot,final ArrayList<HashMap<String, String>> map) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, OrderContinue, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ordermake",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")){

                        JSONObject object = jsonObject.getJSONObject("data");

                         cart_id = object.getString("cart_id");
                        Intent intent=new Intent(getApplicationContext(),PaymentDetails.class);

                        intent.putExtra("order_amt",total_atm);
                        startActivity(intent);
                        }


                    else {
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss(); }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("time_slot","timeslot");
                param.put("user_id",user_id);
                param.put("delivery_date",todaydate);
                param.put("order_array", array.toString());
                Log.d("hj",session_management.getUserDetails().get(BaseURL.KEY_ID));
                Log.d("timeslot",timeslot);
                Log.d("todaydate",todaydate);
                Log.d("order_array",array.toString());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void updateData() {
        pPrice.setText("" + db.getTotalAmount());
        price.setText("" + db.getTotalAmount());
        total_atm = db.getTotalAmount();
        txt_totalPrice.setText(total_atm);
        txtTotalItems.setText("" + db.getCartCount());
        totalItms.setText(""  + db.getCartCount()+" "+" Items");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // ((MainActivity) getActivity()).setCartCounter("" + db.getCartCount());
        }

    }
    private void makeGetAddressRequests(String date) {
        dateDayModelClasses1.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_get_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("selected_date", date);
        Log.d("dd",date);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                CalenderUrl,params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("", response.toString());

                try {
                    String status = response.getString("status");
                    String msg = response.getString("message");
                    if (status.contains("1")) {
                        dateDayModelClasses1.clear();
                        JSONArray jsonArray = response.getJSONArray("data");

                        if (jsonArray.length() > 0) {


                            recyclerTimeSlot.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String data = jsonArray.getString(i);
                                timing_model mycreditList = new timing_model();
                                mycreditList.setTiming(data);
                                dateDayModelClasses1.add(mycreditList);

                            }

                            timeslot = jsonArray.getString(0);
                            bAdapter1 = new Timing_Adapter(OrderSummary.this, dateDayModelClasses1);
                            recyclerTimeSlot.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                            recyclerTimeSlot.setAdapter(bAdapter1);
                            bAdapter1.notifyDataSetChanged();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("selected_date",date);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);

    }

    private void deliverychrge() {


        String tag_json_obj = "json_category_req";



        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.delivery_info, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {


                        JSONObject jsonObject = response.getJSONObject("data");

                        String min_cart_value = jsonObject.getString("min_cart_value");

                        String del_charge = jsonObject.getString("del_charge");

                        if (Integer.parseInt(min_cart_value ) >= 500 ){
                            Toast.makeText(OrderSummary.this, "Minimum Order Amount Should be Greater then ₹500", Toast.LENGTH_SHORT).show();

                            DeliveryCharge.setText("0");
                        }
                        else {
                            Toast.makeText(OrderSummary.this, "sdjfwhuf", Toast.LENGTH_SHORT).show();

                            DeliveryCharge.setText(del_charge);
                        }
                    }

                    else{

                        Toast.makeText(OrderSummary.this, ""+message, Toast.LENGTH_SHORT).show();

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

}
