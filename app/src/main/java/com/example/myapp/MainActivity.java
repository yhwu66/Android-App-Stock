package com.example.myapp;

import static android.content.ContentValues.TAG;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    private static final String TAG = MainActivity.class.getName();
    private static final String portfolio = "portfolio";
    private static final String favorites = "favorites";
    private AtomicInteger totalRequests;
    private AtomicInteger totalRequests_update;
    private Timer timer = new Timer ();
    private String[] nameArr;
    private String[] tickerArr;
    private double[] priceArr;
    private double[] priceChangeArr;
    private double[] priceChangePerArr;
    private String searchinput;
    private int[] sharesNum;
    private String[] tickerArr_por;
    private double[] priceArr_por;
    private double[] priceChangeArr_por;
    private double[] priceChangePerArr_por;
    private double[] sharesCost;
    TimerTask hourlyTask;

    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    private RecyclerView recyclerView;
    String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    String dataArr[] = {};
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueue_update;
    private StringRequest mStringRequest;
    private JsonObjectRequest mJsonObjectRequest;
    private String url = "https://nodejs-786565.wl.r.appspot.com/api/stockdetail/TSLA";
    private List<String> autoCompleteList = new ArrayList<>();
    private double moneyLeft;

    private favitem[] favArr;
    private int favsize;
    private favitem[] porArr;
    private int porsize = 3;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);




    }


    @Override
    protected void onResume() {
        super.onResume();
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue_update = Volley.newRequestQueue(this);
        RecyclerView tmpRecycle = findViewById(R.id.recyclerview);
        tmpRecycle.setVisibility(View.GONE);
        TextView tmpText = findViewById(R.id.textView_today);
        tmpText.setVisibility(View.GONE);
        TextView tmpText2 = findViewById(R.id.myfooter);
        tmpText2.setVisibility(View.GONE);
        tmpText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finnhub.io"));
                startActivity(browserIntent);
            }
        });


        TextView dateView = findViewById(R.id.textView_today);
        Formatter fmt1 = new Formatter();
        Calendar cal1 = Calendar.getInstance();
        fmt1 = new Formatter();
        fmt1.format("%tB",cal1);
        String todaymonth =fmt1.toString();
        Formatter fmt2 = new Formatter();
        Calendar cal2 = Calendar.getInstance();
        fmt2 = new Formatter();
        fmt2.format("%td",cal2);
        String todaydate =fmt2.toString();
        Formatter fmt3 = new Formatter();
        Calendar cal3 = Calendar.getInstance();
        fmt3 = new Formatter();
        fmt3.format("%tY",cal3);
        String todayyear =fmt3.toString();



        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        //editor.clear();
        //editor.commit();

        Log.i(TAG,"is exist fav:" + (sharedpreferences.contains(favorites)));
        Log.i(TAG,"is exist por:" + (sharedpreferences.contains(portfolio)));
        Log.i(TAG,"is exist money:" + (sharedpreferences.contains("money")));
        Log.i(TAG,"is exist AAPL  :" + (sharedpreferences.contains("AAPL")));
        Log.i(TAG,"is exist AAPL_ :" + (sharedpreferences.contains("AAPL_")));
        //Log.i(TAG,"is exist AAPL :" + (sharedpreferences.contains("AAPL_")));
        if(!sharedpreferences.contains(favorites)){
            Set<String> newfavset = new HashSet<String>();
            editor.putStringSet(favorites,newfavset);
            editor.commit();
        }
        if(!sharedpreferences.contains(portfolio)){
            Set<String> newporset = new HashSet<String>();
            editor.putStringSet(portfolio,newporset);
            editor.commit();
        }
        if(!sharedpreferences.contains("money")){
            editor.putString("money","25000.00");
            editor.commit();
        }
        //if(!sharedpreferences.contains(favorites+"_string")){
           // editor.putString(favorites+"_string","{}");
            //editor.commit();
        //}

        Set<String> favset = sharedpreferences.getStringSet(favorites,null);
        Set<String> porset = sharedpreferences.getStringSet(portfolio,null);
        //String favString =  sharedpreferences.getString(favorites+"_string",null);
        //Log.i(TAG,"is exist favsize_string :" + favString);
        favsize = favset.size();
        porsize = porset.size();
        Log.i(TAG,"is exist favsize_ :" + favsize);
        Log.i(TAG,"is exist porsize_ :" +  porsize);
        if(favsize==0 && porsize==0){
            List<favitem> favList = new ArrayList<>();
            favitem[] tmppor = new favitem[1];
            moneyLeft = Double.parseDouble(sharedpreferences.getString("money",null));
            tmppor[0] = new favitem("Net Worth","money",moneyLeft,moneyLeft,1.0,1,1.0);
            List<favitem> porList = Arrays.asList(tmppor);
            porsize++;
            sectionAdapter.addSection(portfolio,new PorSection(porList));
            sectionAdapter.addSection(favorites,new FavSection(favList));
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(sectionAdapter);
            dateView.setText(todaydate+" "+ todaymonth+" "+todayyear);
            //tmpRecycle = findViewById(R.id.recyclerview);
            tmpRecycle.setVisibility(View.VISIBLE);
            //TextView tmpText = findViewById(R.id.textView_today);
            tmpText.setVisibility(View.VISIBLE);
            tmpText2.setVisibility(View.VISIBLE);
            ProgressBar p2 = findViewById(R.id.progressBar2);
            p2.setVisibility(View.GONE);
            return;
        }
        nameArr = new String[favsize];
        tickerArr = new String[favsize];
        priceArr = new double[favsize];
        priceChangeArr = new double[favsize];
        priceChangePerArr = new double[favsize];

        sharesNum = new int[porsize];
        tickerArr_por = new String[porsize];
        priceArr_por = new double[porsize];
        priceChangeArr_por = new double[porsize];
        priceChangePerArr_por = new double[porsize];
        sharesCost = new double[porsize];

        Log.i(TAG,"is exist fav :" + favset.toString());
        Log.i(TAG,"is exist por :" + porset.toString());
        for(String ob:porset){
            Log.i(TAG,"is prodetail "+ob+" amt :" + sharedpreferences.getInt(ob,-1));
            Log.i(TAG,"is prodetail "+ob+" cost :" + sharedpreferences.getString(ob+"_cost",null));
        }

        //Log.i(TAG,"is exist AAPL amt :" + sharedpreferences.getInt("AAPL",-1));
        Log.i(TAG,"is exist money amt :" + sharedpreferences.getString("money",null));
        moneyLeft = Double.parseDouble(sharedpreferences.getString("money",null));
        dateView.setText(todaydate+" "+ todaymonth+" "+todayyear);
        String[] xx = {"T","TS"};
        Log.i("","tst String"+ Arrays.toString(xx));
        String[] kk = getMyArr("{T}");
        Log.i("","tst String size"+ kk.length);
        Log.i("","tst String size"+ kk[0]);
        //Toast.makeText(getApplicationContext(),sharedpreferences.getStringSet(favorites,null).toString(), Toast.LENGTH_LONG).show();
        Object[] favArray = favset.toArray();
        Object[] porArray = porset.toArray();
        totalRequests = new AtomicInteger(2*favsize+porsize);
        for (int i = 0; i < favArray.length; i++) {
            Log.i(TAG,"tickername :" + favArray[i]);
            tickerArr[i] = favArray[i].toString();
            getStockDetail(favArray[i].toString(),i);
            getLatestPrice(favArray[i].toString(),i,0);
        }
        for (int i = 0; i < porArray.length; i++) {
            Log.i(TAG,"por tickername :" + porArray[i]);
            Log.i(TAG,"por tickername shares:" + sharedpreferences.getInt(porArray[i].toString(),-1));
            sharesNum[i] = sharedpreferences.getInt(porArray[i].toString(),-1);
            sharesCost[i] = Double.parseDouble(sharedpreferences.getString(porArray[i].toString()+"_cost",null));
            tickerArr_por[i] = porArray[i].toString();
            //getStockDetail(favArray[i].toString(),i);
            getLatestPrice(porArray[i].toString(),i,1);
        }
        /*Button tst = findViewById(R.id.button_tst);
        tst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("update", "update start");
                mRequestQueue_update = Volley.newRequestQueue(MainActivity.this);
                totalRequests_update = new AtomicInteger(favsize+porsize-1);
                for(int i=0;i<favsize;i++){
                    update_price(0,favArr[i].getTicker(),i);
                }
                for(int i=1;i<porsize;i++){
                    update_price(1,porArr[i].getTicker(),i);
                }

                mRequestQueue_update.addRequestEventListener((request, event) -> {
                    if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                        //sectionAdapter.notifyItemChangedInSection(favorites,0);
                        if(totalRequests_update.decrementAndGet() == 0){
                            Log.i("update","update complete");
                            double networth = 0.0;
                            for(int i=1;i<porsize;i++){
                                networth += sharesNum[i-1]*priceArr_por[i-1];
                                //porArr[i] = new favitem(tickerArr_por[i-1],sharesNum[i-1]+" shares",priceArr_por[i-1],priceChangeArr_por[i-1],priceChangePerArr_por[i-1],sharesNum[i-1],sharesCost[i-1]);
                            }
                            networth +=moneyLeft;

                            porArr[0] = new favitem("Net Worth","money",moneyLeft,networth,1.0,1,1.0);
                            List<favitem> porList = Arrays.asList(porArr);
                            sectionAdapter.addSection(portfolio,new PorSection(porList));
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerView.setAdapter(sectionAdapter);
                        }
                    }
                });

            }
        });*/

        mRequestQueue.addRequestEventListener((request, event) -> {
            if(event == RequestQueue.RequestEvent.REQUEST_FINISHED ){
                //nnnn -= 1;
                //Log.d("tag", "now: n = "+nnnn);
                if(totalRequests.decrementAndGet() == 0){
                    favArr = new favitem[favsize];
                    for(int i=0;i<favsize;i++){
                        Log.i(TAG,"tickername :" + tickerArr[i]);
                        Log.i(TAG,"tickername :" + nameArr[i]);
                        Log.i(TAG,"tickername :" + priceArr[i]);
                        Log.i(TAG,"tickername :" + priceChangeArr[i]);
                        Log.i(TAG,"tickername :" + priceChangePerArr[i]);
                        favArr[i] = new favitem(tickerArr[i],nameArr[i],priceArr[i],priceChangeArr[i],priceChangePerArr[i],0,0.0);
                    }
                    List<favitem> favList = Arrays.asList(favArr);


                    porArr = new favitem[porsize+1];
                    porsize++;
                    double networth = 0.0;
                    for(int i=1;i<porsize;i++){
                        Log.i(TAG,"tickername :" + tickerArr_por[i-1]);
                        Log.i(TAG,"tickername :" + "ll");
                        Log.i(TAG,"tickername :" + priceArr_por[i-1]);
                        Log.i(TAG,"tickername :" + priceChangeArr_por[i-1]);
                        Log.i(TAG,"tickername :" + priceChangePerArr_por[i-1]);
                        Log.i(TAG,"tickername :" + sharesCost[i-1]);
                        networth += sharesNum[i-1]*priceArr_por[i-1];
                        porArr[i] = new favitem(tickerArr_por[i-1],sharesNum[i-1]+" shares",priceArr_por[i-1],priceChangeArr_por[i-1],priceChangePerArr_por[i-1],sharesNum[i-1],sharesCost[i-1]);
                    }
                    networth +=moneyLeft;


                    porArr[0] = new favitem("Net Worth","money",moneyLeft,networth,1.0,1,1.0);
                    List<favitem> porList = Arrays.asList(porArr);


// Add your Sections
                    sectionAdapter.addSection(portfolio,new PorSection(porList));
                    sectionAdapter.addSection(favorites,new FavSection(favList));


// Set up your RecyclerView with the SectionedRecyclerViewAdapter

                    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(sectionAdapter);

                    //tmpRecycle = findViewById(R.id.recyclerview);
                    tmpRecycle.setVisibility(View.VISIBLE);
                    //TextView tmpText = findViewById(R.id.textView_today);
                    tmpText.setVisibility(View.VISIBLE);
                    tmpText2.setVisibility(View.VISIBLE);
                    ProgressBar p2 = findViewById(R.id.progressBar2);
                    p2.setVisibility(View.GONE);
                    //sectionAdapter.


                    ItemTouchHelper.SimpleCallback callback_swipe = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                            Log.i("MainActivity", "drag yes  onMove");
                            Log.i("MainActivity", "drag yes  onMove postion: "+viewHolder.getAdapterPosition());
                            Log.i("MainActivity", "drag yes  onMove postion: "+target.getAdapterPosition());
                            int fromPos = viewHolder.getAdapterPosition();
                            int toPos = target.getAdapterPosition();
                            if(fromPos>=porsize+2){
                                if(toPos>=porsize+2){
                                    int from = fromPos - (porsize+2);
                                    int to = toPos - (porsize+2);
                                    //sectionAdapter.notifyItemMovedInSection(favorites,from,to);
                                    favitem tmp = favArr[from];
                                    favArr[from] = favArr[to];
                                    favArr[to] = tmp;
                                    sectionAdapter.notifyItemMovedInSection(favorites,from,to);
                                }
                            }
                            else if(fromPos>=2 && fromPos<=porsize){
                                if(toPos>=2 && toPos<=porsize){
                                    int from = fromPos - 1;
                                    int to = toPos - 1;
                                    favitem tmp = porArr[from];
                                    porArr[from] = porArr[to];
                                    porArr[to] = tmp;
                                    int tmp_num = sharesNum[from-1];
                                    sharesNum[from-1] = sharesNum[to-1];
                                    sharesNum[to-1] = tmp_num;
                                    double tmp_cost = sharesCost[from-1];
                                    sharesCost[from-1] = sharesCost[to-1];
                                    sharesCost[to-1] = tmp_cost;
                                    sectionAdapter.notifyItemMovedInSection(portfolio,from,to);

                                }
                            }

                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            try {
                                final int position = viewHolder.getAdapterPosition();

                                Log.i("MainActivity", "swipe yes  pos: "+position);
                                if(position>=porsize+2){

                                    Log.i("MainActivity", "swipe yes  pos:  "+position);
                                    sectionAdapter.removeSection(favorites);
                                    TextView xx = viewHolder.itemView.findViewById(R.id.tvItem);
                                    String txtt = xx.getText().toString();
                                    Log.i("MainActivity", "swipe yes  txtt: "+txtt);
                                    //String targetticker ="TSLA";
                                    //txtt = "TSLA";
                                    String txt = "TSLA";
                                    //txtt = txtt.substring(0,txtt.length()-1);
                                    Log.i("MainActivity", "swipe yes length"+txtt.charAt(0));
                                    Log.i("MainActivity", "swipe yes  "+txt.equals(txtt));

                                    String[] strArr = txtt.split(" ");
                                    Log.i("MainActivity", "swipe yes  "+strArr[0]);
                                    String targetticker =strArr[0];
                                    sectionAdapter.removeSection(favorites);
                                    favitem[] newfavarr = new favitem[favsize-1];
                                    int index=0;
                                    while (!favArr[index].getTicker().equals(targetticker)) {
                                        newfavarr[index] = favArr[index];
                                        index++;
                                    }
                                    for(int i=index;i<favsize-1;i++){
                                        newfavarr[i] = favArr[i+1];
                                    }
                                    favArr = new favitem[favsize-1];
                                    favsize--;
                                    favArr = newfavarr;

                                    editor = sharedpreferences.edit();
                                    Set<String> favset = sharedpreferences.getStringSet(favorites,null);
                                    favset.remove(targetticker);
                                    editor.putStringSet(favorites,favset);
                                    editor.remove(targetticker+"_");
                                    editor.commit();


                                    //Log.i("this tst", "tst+"+myFavSection;
                                    List<favitem> newfavList = Arrays.asList(newfavarr);
                                    sectionAdapter.addSection(favorites,new FavSection(newfavList));
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    recyclerView.setAdapter(sectionAdapter);



                                }
                                else if(position>=1 && position<=porsize){
                                    TextView xx = viewHolder.itemView.findViewById(R.id.tvItem);
                                    String txtt = xx.getText().toString();
                                    Log.i("MainActivity", "swipe yes  txtt: "+txtt);
                                    //String targetticker ="TSLA";
                                    //txtt = "TSLA";
                                    String txt = "TSLA";
                                    txtt = txtt.substring(0,txtt.length()-1);
                                    Log.i("MainActivity", "swipe yes  txt:  "+txt);
                                    Log.i("MainActivity", "swipe yes  "+txt.equals(txtt));
                                    Log.i("MainActivity", "swipe yes  length txtt:  "+txtt.length()+"length txt:  "+txt.length());
                                    //String targetticker ="TSLA";
                                    String targetticker =txtt;
                                    //sectionAdapter.removeSection(portfolio);
                                    favitem[] newporarr = new favitem[porsize-1];
                                    int index=0;
                                    while (!porArr[index].getTicker().equals(targetticker)) {
                                        newporarr[index] = porArr[index];
                                        index++;
                                    }
                                    for(int i=index;i<porsize-1;i++){
                                        newporarr[i] = porArr[i+1];
                                    }
                                    porArr = new favitem[porsize-1];
                                    porsize--;
                                    porArr = newporarr;

                            /*editor = sharedpreferences.edit();
                            Set<String> porset = sharedpreferences.getStringSet(portfolio,null);
                            porset.remove(targetticker);
                            editor.putStringSet(portfolio,porset);
                            editor.remove(targetticker+"_");
                            editor.commit();*/


                                    //Log.i("this tst", "tst+"+myFavSection;
                                    List<favitem> newporList = Arrays.asList(newporarr);
                                    sectionAdapter.addSection(portfolio,new PorSection(newporList));
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    recyclerView.setAdapter(sectionAdapter);


                                }



                            } catch(Exception e) {
                                Log.e("MainActivity", e.getMessage());
                            }
                        }

                        // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method
                        @Override
                        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_down))
                                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_down))
                                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                                    //.addSwipeRightLabel("delete")
                                    //.setSwipeRightLabelColor(Color.WHITE)
                                    .addSwipeLeftLabel("")
                                    .setSwipeLeftLabelColor(Color.WHITE)
                                    //.addCornerRadius(TypedValue.COMPLEX_UNIT_DIP, 16)
                                    //.addPadding(TypedValue.COMPLEX_UNIT_DIP, 8, 16, 8)
                                    .create()
                                    .decorate();
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            Log.i("MainActivity", "swipe yes");

                        }

                        @Override
                        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                            return 0.7f;
                        }





                        @Override
                        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                                      int actionState) {


                            /*if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                                if (viewHolder instanceof RecyclerViewAdapter.MyViewHolder) {
                                    RecyclerViewAdapter.MyViewHolder myViewHolder=
                                            (RecyclerViewAdapter.MyViewHolder) viewHolder;
                                    mAdapter.onRowSelected(myViewHolder);
                                }

                            }*/

                            super.onSelectedChanged(viewHolder, actionState);
                        }

                    };
                    ItemTouchHelper itemTouchHelper_swipe = new ItemTouchHelper(callback_swipe);
                    itemTouchHelper_swipe.attachToRecyclerView(recyclerView);

                    /*ItemTouchHelper.SimpleCallback callback_drag = new ItemTouchHelper.SimpleCallback(
                            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }
                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        }
                    } ;
                    ItemTouchHelper itemTouchHelper_drag = new ItemTouchHelper(callback_drag);
                    itemTouchHelper_drag.attachToRecyclerView(recyclerView);*/


                };


            }
        });
        timer = new Timer ();

        hourlyTask = new TimerTask () {
            @Override
            public void run ()
            {
                Log.i("autorefresh","1");
                Log.i("update", "update start");
                mRequestQueue_update = Volley.newRequestQueue(MainActivity.this);
                totalRequests_update = new AtomicInteger(favsize+porsize-1);
                for(int i=0;i<favsize;i++){
                    update_price(0,favArr[i].getTicker(),i);
                }
                for(int i=1;i<porsize;i++){
                    update_price(1,porArr[i].getTicker(),i);
                }

                mRequestQueue_update.addRequestEventListener((request, event) -> {
                    if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                        //sectionAdapter.notifyItemChangedInSection(favorites,0);
                        if(totalRequests_update.decrementAndGet() == 0){
                            Log.i("update","update complete");
                            double networth = 0.0;
                            for(int i=1;i<porsize;i++){
                                networth += sharesNum[i-1]*priceArr_por[i-1];
                                //porArr[i] = new favitem(tickerArr_por[i-1],sharesNum[i-1]+" shares",priceArr_por[i-1],priceChangeArr_por[i-1],priceChangePerArr_por[i-1],sharesNum[i-1],sharesCost[i-1]);
                            }
                            networth +=moneyLeft;

                            porArr[0] = new favitem("Net Worth","money",moneyLeft,networth,1.0,1,1.0);
                            List<favitem> porList = Arrays.asList(porArr);
                            sectionAdapter.addSection(portfolio,new PorSection(porList));
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerView.setAdapter(sectionAdapter);
                        }
                    }
                });

            }};
        timer.schedule (hourlyTask, 15000, 15000);

        //resume continues here
    }

    @Override
    protected void onStop() {
        super.onStop();
        //hourlyTask.cancel();
        //timer.cancel();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search menu action bar.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_actions, menu);
        // Get the search menu.
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Get SearchView object.
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);

        // Get SearchView autocomplete object.
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        // Create a new ArrayAdapter and add data to search auto complete object.
        //String dataArr[] = {};

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        //searchAutoComplete.setThreshold(2);
        searchAutoComplete.setAdapter(newsAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                String[] arrOfQuery = queryString.split(" ");
                searchAutoComplete.setText("" + arrOfQuery[0]);
                //Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                searchView.setQuery(arrOfQuery[0], true);
            }
        });
        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Autocomplete
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // yourMethod();
                    }
                }, 500);   //
                String usrInput = searchView.getQuery().toString().toUpperCase(Locale.ROOT);
                if(!usrInput.equals("")) {
                    getAutoComplete(usrInput,searchAutoComplete);
                }
                else{
                    dataArr = new String[]{};
                    ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, dataArr);
                    searchAutoComplete.setAdapter(newsAdapter);
                }


                //Toast.makeText(MainActivity.this, "you clicked "+usrInput, Toast.LENGTH_LONG).show();

                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));





        return true;
    }

    private void sendAndRequestResponse() {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        //Toast.makeText(getApplicationContext(),"Response :" , Toast.LENGTH_LONG).show();
        //String Request initialized
        url ="https://finnhub.io/api/v1/stock/candle?symbol="+"TSLA"+"&resolution=5&from="+"1631022248"+"&to="+"1631627048"+"&token="+"c94ij8qad3if4j50t1tg";

        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                    Log.d("MYAPP", "STring of JSONNNN"+response.toString());

                //Toast.makeText(getApplicationContext(),"Response :" + response.getString("country"), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext()," ERROROR", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error :" + error.toString());
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getAutoComplete(String input,SearchView.SearchAutoComplete searchAutoComplete) {
//RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        input = input.toUpperCase(Locale.ROOT);//Log.i(TAG,"Error :");
        searchinput = input;
        //Toast.makeText(getApplicationContext(),"Response :" , Toast.LENGTH_LONG).show();
        //String Request initialized
        String autoCompleteUrl = "https://nodejs-786565.wl.r.appspot.com/api/autocomplete/"+ input;
        //String autoCompleteUrl = "https://finnhub.io/api/v1/search?q="+input+"&"+"token=c94ij8qad3if4j50t1tg";
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, autoCompleteUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    int numberOfAuto = response.getInt("count");
                    autoCompleteList.clear();
                    List<String> desList = new ArrayList<>();
                    //dataArr = new String[numberOfAuto];
                    JSONArray aa = response.getJSONArray("result");
                    for(int i=0;i<numberOfAuto;i++){
                        JSONObject itemAA = aa.getJSONObject(i);
                        String sys = itemAA.getString("symbol");
                        String des = itemAA.getString("description");
                        //Toast.makeText(getApplicationContext(),"Response :" + sys, Toast.LENGTH_LONG).show();
                        //dataArr[i] = sys;
                        //Log.i("MYAPP", "autolist"+i+sys+" "+sys.contains("."));
                        if(!sys.contains(".")) {
                            autoCompleteList.add(sys);
                            desList.add(des);
                            Log.i("MYAPP", "autolist"+i+sys+" "+sys.contains("."));
                        }
                    }
                    dataArr = new String[autoCompleteList.size()];
                    for(int i=0;i<autoCompleteList.size();i++){
                        dataArr[i] = autoCompleteList.get(i) + " | "+desList.get(i);
                    }
                    ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, dataArr);
                    searchAutoComplete.setAdapter(newsAdapter);

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);

                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);

    }

    private void getStockDetail(String ticker,int index){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/stockdetail/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, detailUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String comName = response.getString("name");
                    nameArr[index] = comName;

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }
    private void getLatestPrice(String ticker,int index,int mode){
        String priceUrl = "https://nodejs-786565.wl.r.appspot.com/api/latestprice/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, priceUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double currentPrice = response.getDouble("c");
                    double changeprice = response.getDouble("d");
                    //double changeprice = 3.0;
                    double changepriceper = response.getDouble("dp");
                    DecimalFormat df=new DecimalFormat("0.00");
                    if(mode==0){
                        priceArr[index] = round(currentPrice,2);
                        priceChangeArr[index]= round(changeprice,2);
                        priceChangePerArr[index]= round(changepriceper,2);
                    }
                    else{
                        priceArr_por[index] = round(currentPrice,2);
                        priceChangeArr_por[index]= round(changeprice,2);
                        priceChangePerArr_por[index]= round(changepriceper,2);
                    }


                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private String[] getMyArr(String str){
        str = str.substring(1,str.length()-1);
        return str.split(",");
    }

    private void update_price(int mode, String myticker,int index){
        String priceUrl = "https://nodejs-786565.wl.r.appspot.com/api/latestprice/"+myticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, priceUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("update","update" + myticker);
                    double currentPrice = response.getDouble("c");
                    double changeprice = response.getDouble("d");
                    double changepriceper = response.getDouble("dp");
                    currentPrice = round(currentPrice,2);;
                    changeprice = round(changeprice,2);
                    changepriceper = round(changepriceper,2);

                    if(mode==0){
                        favArr[index] = new favitem(myticker,favArr[index].getComName(),currentPrice,changeprice,changepriceper,0,0.0);
                        List<favitem> favList = Arrays.asList(favArr);
                        sectionAdapter.addSection(favorites,new FavSection(favList));
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(sectionAdapter);
                    }
                    else{
                        //currentPrice = 157.97;
                        priceArr_por[index-1] = round(currentPrice,2);
                        priceChangeArr_por[index-1]= round(changeprice,2);
                        priceChangePerArr_por[index-1]= round(changepriceper,2);
                        porArr[index] = new favitem(myticker,sharesNum[index-1]+" shares",priceArr_por[index-1],priceChangeArr_por[index-1],priceChangePerArr_por[index-1],sharesNum[index-1],sharesCost[index-1]);
                        List<favitem> porList = Arrays.asList(porArr);
                        sectionAdapter.addSection(portfolio,new PorSection(porList));
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(sectionAdapter);
                    }


                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue_update.add(mJsonObjectRequest);
    }


}