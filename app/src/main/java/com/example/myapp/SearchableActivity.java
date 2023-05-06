package com.example.myapp;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class SearchableActivity extends AppCompatActivity {
    private String ticker="";
    private String compName="";
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private JsonObjectRequest mJsonObjectRequest2;
    private StringRequest mStringRequest;
    private JsonArrayRequest mJsonArrayrRequest;
    private static final String TAG = MainActivity.class.getName();
    private boolean isDetailReady = false;
    private AtomicInteger totalRequests;
    private String hisChartsJsonString="";
    private String hourChartsJsonString="";
    private int nnnn = 3;
    private double pchange;
    private boolean isInFavorate = false;
    private static final String portfolio = "portfolio";
    private static final String favorites = "favorites";
    private Button buttonClick;
    private double curPirce;
    private int tradeamt = 0;
    private double moneyLeft;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double prePrice;

    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    private RecyclerView recyclerView;
    private boolean isAddSec = false;
    private JSONObject firstnews = new JSONObject();
    private String firstsource = "";
    private String firstheadline = "";
    private String firstsummary = "";
    private String firsturl="";
    private int firsttime = 0;
    private String compWeb="";
    private String[] comPeers;



    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        //check if in fav
        // set toolbar
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        */


        handleIntent(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isInFavorate){
            getMenuInflater().inflate(R.menu.details_actions_full, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.details_actions, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                // User chose the "Settings" item, show the app settings UI...
                //Toast.makeText(getApplicationContext()," selected yes", Toast.LENGTH_LONG).show();
                if(!isInFavorate){
                    item.setIcon(R.drawable.ic_favoritefull);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Set<String> favset = sharedpreferences.getStringSet(favorites,null);
                    favset.add(ticker);
                    editor.putStringSet(favorites,favset);
                    editor.putInt(ticker+"_",1);

                    /*String[] favTickerArr;
                    if(favset.size()==1){
                        favTickerArr = new String[1];
                        favTickerArr[0] = ticker;
                    }
                    else{
                        String tmpstring = sharedpreferences.getString(favorites+"_string",null);
                        favTickerArr = new String[favset.size()];
                        String[] tmp = getMyArr(tmpstring);
                        for(int i=0;i< tmp.length;i++){
                            favTickerArr[i] = tmp[i];
                        }
                        favTickerArr[tmp.length] = ticker;
                    }*/


                    //editor.putString(favorites+"_string",Arrays.toString(favTickerArr));
                    editor.commit();
                    isInFavorate = !isInFavorate;
                    Toast.makeText(getApplicationContext(),ticker+" is added to favorites", Toast.LENGTH_LONG).show();
                }
                else{
                    item.setIcon(R.drawable.ic_favorite);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Set<String> favset = sharedpreferences.getStringSet(favorites,null);
                    favset.remove(ticker);
                    /*String[] favTickerArr;
                    if(favset.size()==0){
                        editor.putString(favorites+"_string","{}");
                    }
                    else{
                        String tmpstring = sharedpreferences.getString(favorites+"_string",null);
                        favTickerArr = new String[favset.size()];
                        for(int i=0;i< favset.size();i++){
                            favTickerArr[i]= "";
                        }
                        String[] tmp = getMyArr(tmpstring);
                        Log.i("string","size" + tmp.length);
                        for(int i=0;i<tmp.length;i++){
                            Log.i("string","size" + tmp[i].split(" ")[0]);
                        }
                        int ii=0;
                        while(!tmp[ii].equals(ticker)){
                            favTickerArr[ii] = tmp[ii];
                            ii++;
                        }
                        for(int i=ii+1;i<favset.size()+1;i++){
                            favTickerArr[ii-1] = tmp[ii];
                        }
                        editor.putString(favorites+"_string",Arrays.toString(favTickerArr));
                    }*/
                    editor.putStringSet(favorites,favset);
                    editor.remove(ticker+"_");
                    editor.commit();
                    //editor.apply();
                    isInFavorate = !isInFavorate;
                    Toast.makeText(getApplicationContext(),ticker+" is removed from to favorites", Toast.LENGTH_LONG).show();
                }
                return true;

            case android.R.id.home:
                //Toast.makeText(getApplicationContext()," selected back", Toast.LENGTH_LONG).show();
                //onBackPressed();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }*/

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ticker = query;
            //check in fav


            Set<String> tmpset = sharedpreferences.getStringSet(favorites,null);
            isInFavorate = tmpset.contains(ticker);
            // set toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);

            // Get a support ActionBar corresponding to this toolbar
            ActionBar ab = getSupportActionBar();

            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {


        mRequestQueue = Volley.newRequestQueue(this);
        totalRequests = new AtomicInteger(9);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        //constraintLayout.setVisibility(View.GONE);
        ScrollView scrollView = findViewById(R.id.myscroll);
        scrollView.setVisibility(View.GONE);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        //TextView tt = findViewById(R.id.tv_counter);
        ticker = query;

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String htmlString1 = "<h1>"+ticker+"</h1>";
        Spanned spanned = HtmlCompat.fromHtml(htmlString1, HtmlCompat.FROM_HTML_MODE_COMPACT);
        TextView symbol = findViewById(R.id.symbol);
        symbol.setText(spanned);
        symbol.setTextColor(getResources().getColor(R.color.black));
        nnnn= 9;
        getSupportActionBar().setTitle(ticker);
        getStockDetail();
        getLatestPrice();
        getHisCharts();
        getHourCharts();
        getCompanyPeer();
        getSocialSentiment();
        getRecommTrend();
        getCompanyEarning();
        getNews();

        mRequestQueue.addRequestEventListener((request, event) -> {
            if(event == RequestQueue.RequestEvent.REQUEST_FINISHED ){
                nnnn -= 1;
                Log.d("tag", "now: n = "+nnnn);
                //if(totalRequests.decrementAndGet() == 0){
                if(nnnn==0){
                    Log.d("tag", "finished: n = "+nnnn);
                    Log.d("tag", "finished");

                    Log.d("tag", "change:"+pchange);
                    TabLayout tabLayout;
                    ViewPager2 viewPager;
                    viewPager = findViewById(R.id.pager1);
                    tabLayout = findViewById(R.id.tabLayout);
                    Log.i(TAG,"hisChartsJsonString:" +  hisChartsJsonString);
                    Log.i(TAG,"hourChartsJsonString:" +  hourChartsJsonString);

                    viewPager.setAdapter(new ViewPagerAdapter(this,hisChartsJsonString,ticker,hourChartsJsonString,pchange));
                    new TabLayoutMediator(tabLayout, viewPager,
                            new TabLayoutMediator.TabConfigurationStrategy() {
                                @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                    //tab.setText("Tabbb " + (position + 1));
                                    if(position==1) {
                                        tab.setIcon(R.drawable.ic_his);
                                    }
                                    else{
                                        tab.setIcon(R.drawable.ic_hour);
                                    }
                                }
                            }).attach();

                    buttonClick = (Button) findViewById(R.id.button_trade);

                    // add listener to button
                    buttonClick.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            // Create custom dialog object
                            final Dialog dialog = new Dialog(SearchableActivity.this);
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog);
                            // Set dialog title
                            dialog.setTitle("Custom Dialog");
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            // set values for custom dialog components - text, image and button
                            TextView text = (TextView) dialog.findViewById(R.id.textDialog);
                            text.setText("Custom dialog Android example.");
                            text.setTextColor(Color.BLACK);
                            String htmlString2 = "<h1>Trade "+compName+" Shares</h1>";
                            Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                            text.setText(spanned2);
                            text.setTextColor(Color.BLACK);
                            TextView text3 = (TextView) dialog.findViewById(R.id.textView3);
                            text3.setTextColor(Color.BLACK);
                            EditText editText = (EditText) dialog.findViewById(R.id.num_input);
                            TextView text8 = (TextView) dialog.findViewById(R.id.textView8);
                            text8.setText("0*$"+curPirce+"/share = " + String.format("%.2f",0*curPirce));
                            //text3.setTextColor(Color.BLACK);
                            moneyLeft = Double.parseDouble(sharedpreferences.getString("money",null));
                            TextView textm = (TextView) dialog.findViewById(R.id.moneyleft);
                            textm.setText("$"+String.format("%.2f",moneyLeft)+" to buy "+ticker);

                            dialog.show();

                            editText.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {}

                                public void beforeTextChanged(CharSequence s, int start,
                                                              int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start,
                                                          int before, int count) {
                                    String input = editText.getText().toString();
                                    if(!input.equals("")) {
                                        double tmp = Double.parseDouble(editText.getText().toString());
                                        String tmpout="";
                                        tradeamt = (int) tmp;
                                        tmpout += round(tmp, 2)+"";
                                        tmpout += "*$"+curPirce+"/share = " + String.format("%.2f",tmp*curPirce);
                                        text8.setText(tmpout);
                                    }
                                    else{
                                        double tmp = 0;
                                        String tmpout="";
                                        tradeamt = 0;
                                        //tmpout += round(tmp, 2)+"";
                                        tmpout += "0*$"+curPirce+"/share = " + String.format("%.2f",tmp*curPirce);
                                        text8.setText(tmpout);
                                    }
                                }
                            });

                            Button buyButton = (Button) dialog.findViewById(R.id.button_buy);
                            // if decline button is clicked, close the custom dialog
                            buyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Close dialog
                                    if(tradeamt==0){
                                        Toast.makeText(getApplicationContext(),"Please enter a valid amount", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if(moneyLeft<tradeamt*curPirce){
                                        Toast.makeText(getApplicationContext(),"Not enough money to buy", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Set<String> porset = sharedpreferences.getStringSet(portfolio,null);
                                    porset.add(ticker);
                                    int myamt = 0;
                                    double mytotalcost = 0.0;
                                    if(sharedpreferences.contains(ticker)){
                                        myamt = sharedpreferences.getInt(ticker,-1);
                                    }
                                    if(sharedpreferences.contains(ticker+"_cost")){
                                        mytotalcost = Double.parseDouble(sharedpreferences.getString(ticker+"_cost",null));
                                    }
                                    double changeMoney = tradeamt * curPirce;
                                    moneyLeft -= changeMoney;
                                    mytotalcost+=changeMoney;
                                    editor.putString("money",round(moneyLeft,2)+"");
                                    editor.putStringSet(portfolio,porset);
                                    editor.putInt(ticker,tradeamt+myamt);
                                    editor.putString(ticker+"_cost",round(mytotalcost,2)+"");
                                    editor.commit();
                                    dialog.dismiss();
                                    final Dialog dialog2 = new Dialog(SearchableActivity.this);
                                    // Include dialog.xml file
                                    dialog2.setContentView(R.layout.dialog_con);
                                    // Set dialog title
                                    dialog2.setTitle("Con Dialog");
                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    String htmlString2 = "<h1>Congratulations!</h1>";
                                    Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                                    TextView textcon = (TextView) dialog2.findViewById(R.id.textCon);
                                    TextView textbs = (TextView) dialog2.findViewById(R.id.textView_bs);
                                    textbs.setText("You have successfully bought "+tradeamt);
                                    TextView textbs2 = (TextView) dialog2.findViewById(R.id.textView_bs2);
                                    textbs2.setText("shares of "+ticker);
                                    textcon.setText(spanned2);
                                    Button doneButton = (Button) dialog2.findViewById(R.id.button_done);
                                    // if decline button is clicked, close the custom dialog
                                    doneButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int shareowned = 0;
                                            double avg = 0.0;
                                            double totalCost = 0.0;
                                            double changePP = 0.0;
                                            double marketValue = 0.0;
                                            if(sharedpreferences.contains(ticker)){
                                                shareowned = sharedpreferences.getInt(ticker,-1);
                                                totalCost = Double.parseDouble(sharedpreferences.getString(ticker+"_cost",null));
                                                avg = totalCost /(shareowned*1.0);
                                                avg = round(avg,2);
                                                marketValue = shareowned * curPirce;
                                                changePP = marketValue - totalCost;
                                            }
                                            changePP = round(changePP,2);
                                            TextView shaowned = findViewById(R.id.sharedownedVal);
                                            shaowned.setText((shareowned)+"");
                                            TextView avgcost = findViewById(R.id.avgVal);
                                            avgcost.setText(String.format("%.2f",avg));
                                            TextView tcost = findViewById(R.id.costVal);
                                            tcost.setText(String.format("%.2f",totalCost));
                                            TextView changeval = findViewById(R.id.changeVal);
                                            changeval.setText(String.format("%.2f",changePP));
                                            TextView marVal = findViewById(R.id.marketVal);
                                            marVal.setText(String.format("%.2f",marketValue));
                                            if(changePP>0){
                                                changeval.setTextColor(getResources().getColor(R.color.green_up));
                                                marVal.setTextColor(getResources().getColor(R.color.green_up));
                                            }
                                            else if(changePP<0){
                                                changeval.setTextColor(getResources().getColor(R.color.red_down));
                                                marVal.setTextColor(getResources().getColor(R.color.red_down));
                                            }
                                            Log.d("tag", "done"+"done done");

                                            dialog2.dismiss();
                                        }
                                    });
                                    dialog2.show();
                                }
                            });

                            Button sellButton = (Button) dialog.findViewById(R.id.button_sell);
                            // if decline button is clicked, close the custom dialog
                            sellButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Close dialog
                                    if(tradeamt==0){
                                        Toast.makeText(getApplicationContext(),"Please enter a valid amount", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Set<String> porset = sharedpreferences.getStringSet(portfolio,null);
                                    porset.add(ticker);
                                    int myamt = 0;
                                    double mytotalcost = 0.0;
                                    if(sharedpreferences.contains(ticker)){
                                        myamt = sharedpreferences.getInt(ticker,-1);
                                    }
                                    if(tradeamt>myamt){
                                        Toast.makeText(getApplicationContext(),"Not enough shares to sell", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if(sharedpreferences.contains(ticker+"_cost")){
                                        mytotalcost = Double.parseDouble(sharedpreferences.getString(ticker+"_cost",null));
                                    }
                                    double changeMoney = tradeamt * curPirce;
                                    moneyLeft += changeMoney;
                                    mytotalcost -= changeMoney;
                                    if(myamt-tradeamt>0) {
                                        editor.putString("money", round(moneyLeft, 2) + "");
                                        editor.putStringSet(portfolio, porset);
                                        editor.putInt(ticker, myamt - tradeamt);
                                        editor.putString(ticker + "_cost", round(mytotalcost, 2) + "");
                                    }
                                    else{
                                        porset.remove(ticker);
                                        editor.putString("money", round(moneyLeft, 2) + "");
                                        editor.putStringSet(portfolio, porset);
                                        editor.remove(ticker);
                                        editor.remove(ticker+"_cost");
                                    }
                                    editor.commit();
                                    dialog.dismiss();
                                    final Dialog dialog2 = new Dialog(SearchableActivity.this);
                                    // Include dialog.xml file
                                    dialog2.setContentView(R.layout.dialog_con);
                                    // Set dialog title
                                    dialog2.setTitle("Con Dialog");
                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    String htmlString2 = "<h1>Congratulations!</h1>";
                                    Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                                    TextView textcon = (TextView) dialog2.findViewById(R.id.textCon);
                                    TextView textbs = (TextView) dialog2.findViewById(R.id.textView_bs);
                                    textbs.setText("You have successfully sold "+tradeamt);
                                    TextView textbs2 = (TextView) dialog2.findViewById(R.id.textView_bs2);
                                    textbs2.setText("shares of "+ticker);
                                    textcon.setText(spanned2);
                                    Button doneButton = (Button) dialog2.findViewById(R.id.button_done);
                                    // if decline button is clicked, close the custom dialog
                                    doneButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int shareowned = 0;
                                            double avg = 0.0;
                                            double totalCost = 0.0;
                                            double changePP = 0.0;
                                            double marketValue = 0.0;
                                            if(sharedpreferences.contains(ticker)){
                                                shareowned = sharedpreferences.getInt(ticker,-1);
                                                totalCost = Double.parseDouble(sharedpreferences.getString(ticker+"_cost",null));
                                                avg = totalCost /(shareowned*1.0);
                                                avg = round(avg,2);
                                                marketValue = shareowned * curPirce;
                                                changePP = marketValue - totalCost;
                                            }
                                            changePP = round(changePP,2);
                                            TextView shaowned = findViewById(R.id.sharedownedVal);
                                            shaowned.setText((shareowned)+"");
                                            TextView avgcost = findViewById(R.id.avgVal);
                                            avgcost.setText(String.format("%.2f",avg));
                                            TextView tcost = findViewById(R.id.costVal);
                                            tcost.setText(String.format("%.2f",totalCost));
                                            TextView changeval = findViewById(R.id.changeVal);
                                            changeval.setText(String.format("%.2f",changePP));
                                            TextView marVal = findViewById(R.id.marketVal);
                                            marVal.setText(String.format("%.2f",marketValue));
                                            if(changePP>0){
                                                changeval.setTextColor(getResources().getColor(R.color.green_up));
                                                marVal.setTextColor(getResources().getColor(R.color.green_up));
                                            }
                                            else if(changePP<0){
                                                changeval.setTextColor(getResources().getColor(R.color.red_down));
                                                marVal.setTextColor(getResources().getColor(R.color.red_down));
                                            }
                                            else{
                                                changeval.setTextColor(Color.BLACK);
                                                marVal.setTextColor(Color.BLACK);
                                            }
                                            Log.d("tag", "done"+"done done");
                                            dialog2.dismiss();
                                        }
                                    });
                                    dialog2.show();
                                }
                            });


                        }

                    });


                    //por section
                    TextView porTitle = findViewById(R.id.porTitle);
                    String htmlStringpor = "<h1>Portfolio</h1>";
                    Spanned spannedpor = HtmlCompat.fromHtml(htmlStringpor, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    porTitle.setText(spannedpor);
                    porTitle.setTextColor(Color.BLACK);
                    int shareowned = 0;
                    double avg = 0.0;
                    double totalCost = 0.0;
                    double changePP = 0.0;
                    double marketValue = 0.0;
                    if(sharedpreferences.contains(ticker)){
                        shareowned = sharedpreferences.getInt(ticker,-1);
                        totalCost = Double.parseDouble(sharedpreferences.getString(ticker+"_cost",null));
                        avg = totalCost /(shareowned*1.0);
                        avg = round(avg,2);
                        marketValue = shareowned * curPirce;
                        changePP = marketValue - totalCost;
                    }
                    changePP = round(changePP,2);
                    TextView shaowned = findViewById(R.id.sharedownedVal);
                    shaowned.setText(shareowned+"");
                    TextView avgcost = findViewById(R.id.avgVal);
                    avgcost.setText("$"+String.format("%.2f",avg));
                    TextView tcost = findViewById(R.id.costVal);
                    tcost.setText("$"+String.format("%.2f",totalCost));
                    TextView changeval = findViewById(R.id.changeVal);
                    changeval.setText("$"+String.format("%.2f",changePP));
                    TextView marVal = findViewById(R.id.marketVal);
                    marVal.setText("$"+String.format("%.2f",marketValue));
                    if(changePP>0){
                        changeval.setTextColor(getResources().getColor(R.color.green_up));
                        marVal.setTextColor(getResources().getColor(R.color.green_up));
                    }
                    else if(changePP<0){
                        changeval.setTextColor(getResources().getColor(R.color.red_down));
                        marVal.setTextColor(getResources().getColor(R.color.red_down));
                    }

                    //stats section
                    TextView statsTitle = findViewById(R.id.statsTitle);
                    String htmlStringsta = "<h1>Stats</h1>";
                    Spanned spannedsta = HtmlCompat.fromHtml(htmlStringsta, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    statsTitle.setText(spannedsta);
                    TextView openP = findViewById(R.id.openprice);
                    TextView lowP = findViewById(R.id.lowprice);
                    TextView highP = findViewById(R.id.highprice);
                    TextView prevP = findViewById(R.id.preclose);
                    openP.setText("Open Price: $"+String.format("%.2f",openPrice));
                    lowP.setText( "Low Price:  $"+String.format("%.2f",lowPrice));
                    highP.setText("High Price:  $"+String.format("%.2f",highPrice));
                    prevP.setText("Prev. Close: $"+String.format("%.2f",prePrice));

                    //about section
                    TextView aboutTitle = findViewById(R.id.aboutTitle);
                    String htmlStringabout = "<h1>About</h1>";
                    Spanned spannedabout = HtmlCompat.fromHtml(htmlStringabout, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    aboutTitle.setText(spannedabout);

                    TextView webpage = findViewById(R.id.webpage);
                    webpage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(compWeb));
                            startActivity(browserIntent);

                        }
                    });

                    TextView peers1 = findViewById(R.id.peers1);
                    peers1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                            intent.setAction(Intent.ACTION_SEARCH);
                            intent.putExtra(SearchManager.QUERY, comPeers[0]);
                            startActivity(intent);
                            Log.i("this peer", "peers"+comPeers[1]);
                        }
                    });

                    TextView peers2 = findViewById(R.id.peers2);
                    //peers2.setText(comPeers[1]+",");
                    peers2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                            intent.setAction(Intent.ACTION_SEARCH);
                            intent.putExtra(SearchManager.QUERY, comPeers[1]);
                            startActivity(intent);
                            Log.i("this peer", "peers"+comPeers[1]);
                        }
                    });

                    TextView peers3 = findViewById(R.id.peers3);
                    //peers3.setText(comPeers[2]+",");
                    peers3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                            intent.setAction(Intent.ACTION_SEARCH);
                            intent.putExtra(SearchManager.QUERY, comPeers[2]);
                            startActivity(intent);
                            Log.i("this peer", "peers"+comPeers[1]);
                        }
                    });







                    //insight section
                    TextView insightTitle = findViewById(R.id.insightTitle);
                    String htmlStringinsight = "<h1>Insight</h1>";
                    Spanned spannedinsight = HtmlCompat.fromHtml(htmlStringinsight, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    insightTitle.setText(spannedinsight);

                    //news section

                    TextView newsTitle = findViewById(R.id.newsTitle);
                    String htmlStringnews = "<h1>News</h1>";
                    Spanned spannednews = HtmlCompat.fromHtml(htmlStringnews, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    newsTitle.setText(spannednews);
                    //ImageView fImg = findViewById(R.id.firstNewsImage);

                    //sectionAdapter.addSection(new PorSection());
                    String source = "";
                    Log.d("tag", "firstnews1"+firstnews.toString());
                    /*try{
                        //source = firstnews.getString("source");
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }*/
                    ConstraintLayout firstNews = findViewById(R.id.firstNewsArea);
                    firstNews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog2 = new Dialog(SearchableActivity.this);
                            // Include dialog.xml file
                            dialog2.setContentView(R.layout.dialog_news);
                            // Set dialog title
                            dialog2.setTitle("Con Dialog");
                            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            long newtimelong = (long) firsttime;
                            long newtimestamp = newtimelong*1000;
                            Log.i(TAG,""+newtimestamp);
                            Timestamp ts=new Timestamp(newtimestamp);
                            Date date=ts;
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(date);
                            Formatter fmt1 = new Formatter();
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
                            TextView text = (TextView) dialog2.findViewById(R.id.dialog_source);
                            String htmlString2 = "<h1>"+firstsource+"</h1>";
                            Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                            text.setText(spanned2);
                            text.setTextColor(Color.BLACK);
                            TextView text2 = (TextView) dialog2.findViewById(R.id.dialog_date);
                            text2.setText(todaymonth+" "+todaydate+", "+todayyear);

                            TextView text3 = (TextView) dialog2.findViewById(R.id.dialog_headline);
                            text3.setText(firstheadline);

                            TextView text4 = (TextView) dialog2.findViewById(R.id.dialog_summary);
                            text4.setText(firstsummary);

                            ImageView chrome = (ImageView) dialog2.findViewById(R.id.chromeImg);
                            chrome.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(firsturl));
                                    startActivity(browserIntent);
                                }
                            });

                            ImageView twitter = (ImageView) dialog2.findViewById(R.id.twitterImg);
                            twitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String sharedUrl =firsturl;
                                    String url = "http://www.twitter.com/intent/tweet?url="+sharedUrl+"&text="+"Check out this Link";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

                            ImageView facebook = (ImageView) dialog2.findViewById(R.id.facebookImg);
                            facebook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String sharedUrl =firsturl;
                                    String url = "https://www.facebook.com/sharer/sharer.php?u=" + sharedUrl;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                            dialog2.show();
                        }
                    });




                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);




                };
            }
        });
    }
    private void getStockDetail(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/stockdetail/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, detailUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String comName = response.getString("name");
                    TextView name = findViewById(R.id.name);
                    name.setText(comName);
                    compName = comName;
                    TextView comNameField = findViewById(R.id.comNameField);
                    comNameField.setText(comName);
                    ImageView imageView = findViewById(R.id.imageView);
                    String logo = response.getString("logo");
                    if(!logo.equals("")) {
                        Picasso.get().load(logo).into(imageView);
                    }
                    //Glide.with(SearchableActivity.this).load("https://s.yimg.com/ny/api/res/1.2/ny2LI5UFRHEWBtxxjQ0Dgw--/YXBwaWQ9aGlnaGxhbmRlcjt3PTEyMDA7aD02NzU-/https://s.yimg.com/os/creatr-uploaded-images/2022-04/1f016080-c7fd-11ec-afde-7e50a0b40944").into(imageView);
                    //isDetailReady = true;
                    TextView ipodate = findViewById(R.id.ipodate);
                    String ipostring = response.getString("ipo");
                    String[] strArr = ipostring.split("-");
                    ipodate.setText(strArr[1]+"-"+strArr[2]+"-"+strArr[0]);

                    TextView industry = findViewById(R.id.industry);
                    industry.setText(response.getString("finnhubIndustry"));

                    TextView webpage = findViewById(R.id.webpage);
                    webpage.setPaintFlags(webpage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    webpage.setText(response.getString("weburl"));
                    compWeb = response.getString("weburl");



                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }
    private void getLatestPrice(){
        String priceUrl = "https://nodejs-786565.wl.r.appspot.com/api/latestprice/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, priceUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double currentPrice = round(response.getDouble("c"),2);
                    double change = round(response.getDouble("d"),2);
                    double changeper = round(response.getDouble("dp"),2);
                    openPrice = round(response.getDouble("o"),2);
                    lowPrice = round(response.getDouble("l"),2);
                    highPrice = round(response.getDouble("h"),2);
                    prePrice = round(response.getDouble("pc"),2);
                    TextView cPrice = findViewById(R.id.currentPrice);
                    curPirce = currentPrice;
                    String htmlString1 = "<h1>$"+String.format("%.2f",currentPrice)+"</h1>";
                    Spanned spanned1 = HtmlCompat.fromHtml(htmlString1, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    cPrice.setText(spanned1);
                    cPrice.setTextColor(getResources().getColor(R.color.black));
                    String htmlString2 = "<h1>$"+currentPrice+"</h1>";
                    Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                    TextView changePrice = findViewById(R.id.changePrice);
                    ImageView imageView = findViewById(R.id.imageView2);
                    changePrice.setText("$"+String.format("%.2f",change)+"("+String.format("%.2f",changeper)+"%)");
                    int gettime = response.getInt("t");
                    long curtimemili = System.currentTimeMillis();
                    int curtime = (int) curtimemili;
                    int fromtime, totime;
                    if(  (curtime-gettime)>5*60  ){
                        fromtime = gettime-6*60*60;
                        totime = gettime;
                    }
                    else{
                        fromtime = curtime-6*60*60;
                        totime = curtime;
                    }
                    Log.d("tag", "curtime: "+ curtime);
                    String hourUrl = "http://nodejs-786565.wl.r.appspot.com/api/hourdata/AMZN/1651145789/1651167389";
                    Log.d("tag", "hourUrl: "+ hourUrl);

                    pchange = change;
                    if(change>0) {
                        imageView.setBackgroundResource(R.drawable.ic_up);
                        changePrice.setTextColor(getResources().getColor(R.color.green_up));
                    }
                    else if(change<0) {
                        imageView.setBackgroundResource(R.drawable.ic_down);
                        changePrice.setTextColor(getResources().getColor(R.color.red_down));
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

    private void getHisCharts(){
        String hisUrl = "https://nodejs-786565.wl.r.appspot.com/api/hisdata/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, hisUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //try {

                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();
                /*WebView webView = findViewById(R.id.charts);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.clearCache(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setAllowFileAccessFromFileURLs(true);
                webSettings.setAllowFileAccess(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("file:///android_asset/highcharts.html");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        String jString = response.toString();
                        view.loadUrl("javascript:setTicker('"+jString+  "','"+ ticker   +"')");
                    }
                });*/
                Log.i(TAG,"Sting of JSON:" +  response.toString());
                hisChartsJsonString = response.toString();
                //} catch (JSONException e) {
                    //Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                //}
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

    private void getHourCharts(){

        long curtimemili = System.currentTimeMillis();
        int curtime = (int) (curtimemili/1000);
        int fromtime = curtime-6*60*60;
        int totime =  curtime;
        String hourUrl = "https://nodejs-786565.wl.r.appspot.com/api/hour/"+ticker+"/"+fromtime+"/"+totime;
        //String hourUrl = "https://finnhub.io/api/v1/stock/candle?symbol="+ticker+"&resolution=5&from="+fromtime+"&to="+totime+"&token="+"c94ij8qad3if4j50t1tg";
        //String hourUrl ="https://finnhub.io/api/v1/stock/candle?symbol=AAPL&resolution=5&from=1631022248&to=1631043848&token=c94ij8qad3if4j50t1tg";
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, hourUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,"Sting of JSONHour:" +  response.toString());
                hourChartsJsonString = response.toString();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROR_HOUR", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getCompanyPeer(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/companypeer/"+ticker;
        mStringRequest = new StringRequest(Request.Method.GET, detailUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                Log.i(TAG,"peer:" + response);
                int len0 = response.length();
                String sub1 = response.substring(1,len0-1);
                Log.i(TAG,"peer:" + sub1);
                String[] tmparr = sub1.split(",");
                int len1 = tmparr.length;
                String[] peer = new String[len1];
                for(int i=0;i<len1;i++){
                    Log.i(TAG,"peer:" + tmparr[i]);
                    peer[i] = tmparr[i].substring(1,tmparr[i].length()-1);
                }
                for(int i=0;i<len1;i++){
                    Log.i(TAG,"peer:" + peer[i]);
                }
                comPeers = peer;
                TextView peers1 = findViewById(R.id.peers1);
                String htmlString2 = "<u>"+peer[0]+"</u>, ";
                Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers1.setText(spanned2);

                TextView peers2 = findViewById(R.id.peers2);
                htmlString2 = "<u>"+peer[1]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers2.setText(spanned2);

                TextView peers3 = findViewById(R.id.peers3);
                htmlString2 = "<u>"+peer[2]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers3.setText(spanned2);
                TextView peers4 = findViewById(R.id.peers4);
                htmlString2 = "<u>"+peer[3]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers4.setText(spanned2);
                TextView peers5 = findViewById(R.id.peers5);
                htmlString2 = "<u>"+peer[4]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers5.setText(spanned2);
                TextView peers6 = findViewById(R.id.peers6);
                htmlString2 = "<u>"+peer[5]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers6.setText(spanned2);
                TextView peers7 = findViewById(R.id.peers7);
                htmlString2 = "<u>"+peer[6]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers7.setText(spanned2);
                TextView peers8 = findViewById(R.id.peers8);
                htmlString2 = "<u>"+peer[7]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers8.setText(spanned2);
                TextView peers9 = findViewById(R.id.peers9);
                htmlString2 = "<u>"+peer[8]+"</u>, ";
                spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                peers9.setText(spanned2);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:  " + error.toString());

            }
        });

        mRequestQueue.add(mStringRequest);
    }

    private void getSocialSentiment(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/socialsentiment/"+ticker;
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, detailUrl, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //String comName = response.getString("reddit");
                    JSONArray reddit = response.getJSONArray("reddit");
                    JSONArray twitter = response.getJSONArray("twitter");

                    int reddit_total = 0, reddit_pos = 0, reddit_neg = 0;
                    int twitter_total = 0, twitter_pos = 0, twitter_neg = 0;
                    for(int i=0;i<reddit.length();i++){
                        JSONObject tmp = reddit.getJSONObject(i);
                        reddit_total += tmp.getInt("mention");
                        reddit_pos += tmp.getInt("positiveMention");
                        reddit_neg += tmp.getInt("negativeMention");
                    }
                    for(int i=0;i<twitter.length();i++){
                        JSONObject tmp = twitter.getJSONObject(i);
                        twitter_total += tmp.getInt("mention");
                        twitter_pos += tmp.getInt("positiveMention");
                        twitter_neg += tmp.getInt("negativeMention");
                    }
                    TextView rt = findViewById(R.id.reddittotal);
                    rt.setText(reddit_total+"");
                    TextView rp = findViewById(R.id.redditpos);
                    rp.setText(reddit_pos+"");
                    TextView rn = findViewById(R.id.redditneg);
                    rn.setText(reddit_neg+"");

                    TextView tt = findViewById(R.id.twittertotal);
                    tt.setText(twitter_total+"");
                    TextView tp = findViewById(R.id.twitterpos);
                    tp.setText(twitter_pos+"");
                    TextView tn = findViewById(R.id.twitterneg);
                    tn.setText(twitter_neg+"");
                    //Toast.makeText(getApplicationContext()," SOCIAL"+response.toString(), Toast.LENGTH_LONG).show();



                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                }
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getRecommTrend(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/recommtrend/"+ticker;
        mStringRequest = new StringRequest(Request.Method.GET, detailUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                WebView recomCharts = findViewById(R.id.recommCharts);
                WebSettings webSettingsRecom = recomCharts.getSettings();
                webSettingsRecom.setJavaScriptEnabled(true);
                recomCharts.clearCache(true);
                webSettingsRecom.setDomStorageEnabled(true);
                webSettingsRecom.setAllowFileAccessFromFileURLs(true);
                webSettingsRecom.setAllowFileAccess(true);
                recomCharts.setWebViewClient(new WebViewClient());
                recomCharts.loadUrl("file:///android_asset/recommcharts.html");
                recomCharts.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        view.loadUrl(   "javascript:setTicker('"+response+  "','"+ ticker  +"')"     );
                    }
                });
                //Toast.makeText(getApplicationContext(),"Response :" + response, Toast.LENGTH_LONG).show();//display the response on screen
                Log.i(TAG,"Error 11:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }
    private void getCompanyEarning(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/companyearning/"+ticker;
        mStringRequest = new StringRequest(Request.Method.GET, detailUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                WebView epsCharts = findViewById(R.id.epsCharts);
                WebSettings webSettingsEps = epsCharts.getSettings();
                webSettingsEps.setJavaScriptEnabled(true);
                epsCharts.clearCache(true);
                webSettingsEps.setDomStorageEnabled(true);
                webSettingsEps.setAllowFileAccessFromFileURLs(true);
                webSettingsEps.setAllowFileAccess(true);
                epsCharts.setWebViewClient(new WebViewClient());
                epsCharts.loadUrl("file:///android_asset/epscharts.html");
                epsCharts.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        view.loadUrl(   "javascript:setTicker('"+response+  "','"+ ticker  +"')"     );
                    }
                });
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                Log.i(TAG,"Error 11:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    private void getNews(){
        String detailUrl = "https://nodejs-786565.wl.r.appspot.com/api/news/"+ticker;
        mJsonArrayrRequest = new JsonArrayRequest(Request.Method.GET, detailUrl, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject aa = response.getJSONObject(0);
                    //Toast.makeText(getApplicationContext(),"Response :" + aa.getString("source"), Toast.LENGTH_LONG).show();
                    int news_num = 20;
                    if(response.length()<20){
                        news_num = response.length();
                    }
                    firstnews = response.getJSONObject(0);
                    Log.d("tag", "firstnews2"+firstnews.toString());
                    ConstraintLayout firstNews = findViewById(R.id.firstNewsArea);
                    String imgurl = "https://s.yimg.com/ny/api/res/1.2/sOoUNwejZAgP.lA33MwoYQ--/YXBwaWQ9aGlnaGxhbmRlcjt3PTEyMDA7aD02NzQ-/https://s.yimg.com/os/creatr-uploaded-images/2022-04/edbd8f10-c7f9-11ec-93fb-cdc227cb1432";
                    JSONObject first = response.getJSONObject(0);
                    int ii = 0;
                    //first = response.getJSONObject(0);
                    while(first.getString("image").equals("")){
                        ii++;
                        first = response.getJSONObject(ii);
                    }
                    imgurl = first.getString("image");
                    //Log.i("dd","imgurl"+imgurl);
                    ImageView fImg = findViewById(R.id.firstNewsImage);
                    if(!imgurl.equals("")) {
                        Picasso.get().load(imgurl).into(fImg);
                    }
                    TextView fSource = findViewById(R.id.firstNewsSource);
                    String sourceF = first.getString("source");
                    firstsource = first.getString("source");
                    fSource.setText(first.getString("source"));
                    TextView fHeadLine = findViewById(R.id.firstNewsHeadline);
                    String headlineF = first.getString("headline");
                    firstheadline = first.getString("headline");
                    fHeadLine.setText(first.getString("headline"));
                    String summaryF = first.getString("summary");
                    firstsummary = first.getString("summary");
                    String urlF = first.getString("url");
                    firsturl = first.getString("url");
                    long curtimemili = System.currentTimeMillis();
                    int curtime = (int) (curtimemili/1000);
                    int newstime = first.getInt("datetime");
                    firsttime = first.getInt("datetime");;
                    int diffi = curtime-newstime;
                    long diff = diffi *1000;
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    String diffstr = "";
                    diffstr = diffHours + " hours ago";
                    TextView fTime = findViewById(R.id.firstNewsTime);
                    fTime.setText(diffstr);
                    /*firstNews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog2 = new Dialog(SearchableActivity.this);
                            // Include dialog.xml file
                            dialog2.setContentView(R.layout.dialog_news);
                            // Set dialog title
                            dialog2.setTitle("Con Dialog");
                            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            long newtimelong = (long) newstime;
                            long newtimestamp = newtimelong*1000;
                            Log.i(TAG,""+newtimestamp);
                            Timestamp ts=new Timestamp(newtimestamp);
                            Date date=ts;
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(date);
                            Formatter fmt1 = new Formatter();
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
                            TextView text = (TextView) dialog2.findViewById(R.id.dialog_source);
                            String htmlString2 = "<h1>"+sourceF+"</h1>";
                            Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                            text.setText(spanned2);
                            text.setTextColor(Color.BLACK);
                            TextView text2 = (TextView) dialog2.findViewById(R.id.dialog_date);
                            text2.setText(todaymonth+" "+todaydate+", "+todayyear);

                            TextView text3 = (TextView) dialog2.findViewById(R.id.dialog_headline);
                            text3.setText(headlineF);

                            TextView text4 = (TextView) dialog2.findViewById(R.id.dialog_summary);
                            text4.setText(summaryF);

                            ImageView chrome = (ImageView) dialog2.findViewById(R.id.chromeImg);
                            chrome.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlF));
                                    startActivity(browserIntent);
                                }
                            });

                            ImageView twitter = (ImageView) dialog2.findViewById(R.id.twitterImg);
                            twitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String sharedUrl =urlF;
                                    String url = "http://www.twitter.com/intent/tweet?url="+sharedUrl+"&text="+"Check out this Link";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

                            ImageView facebook = (ImageView) dialog2.findViewById(R.id.facebookImg);
                            facebook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String sharedUrl =urlF;
                                    String url = "https://www.facebook.com/sharer/sharer.php?u=" + sharedUrl;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                            dialog2.show();
                        }
                    });*/
                    ArrayList<newsitem>  newsList = new ArrayList<>();
                    int numnews=0;
                    JSONObject curNews = response.getJSONObject(ii+1);
                    while(numnews<=15 && numnews<=news_num){
                        if(!curNews.getString("image").equals("")) {
                            String source = curNews.getString("source");
                            String headline = curNews.getString("headline");
                            String summary = curNews.getString("summary");
                            String url = curNews.getString("url");
                            int timeStamp = curNews.getInt("datetime");
                            String image = curNews.getString("image");
                            newsitem tmpItem = new newsitem(source, headline, summary, url, image, timeStamp);
                            newsList.add(tmpItem);
                            numnews++;
                        }
                        ii++;
                        curNews = response.getJSONObject(ii + 1);
                    }

                    /*newsitem[] newsArr = new newsitem[news_num-1];
                    for(int i=ii+1;i<news_num;i++){
                        String source = response.getJSONObject(i).getString("source");
                        String headline = response.getJSONObject(i).getString("headline");
                        String summary = response.getJSONObject(i).getString("summary");
                        String url = response.getJSONObject(i).getString("url");
                        int timeStamp = response.getJSONObject(i).getInt("datetime");
                        String image = response.getJSONObject(i).getString("image");
                        newsArr[i-1] = new newsitem(source,headline,summary,url,image,timeStamp);
                    }
                    List<newsitem> newsList = Arrays.asList(newsArr);*/


                    if(!isAddSec) {
                        sectionAdapter.addSection(new NewsSection(newsList,SearchableActivity.this));
                        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchableActivity.this));
                        recyclerView.setAdapter(sectionAdapter);
                        isAddSec = true;
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
                //Toast.makeText(getApplicationContext()," ERROROR_DETAIL", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error 11:" + error.toString());
            }
        });

        mRequestQueue.add(mJsonArrayrRequest);
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
}



