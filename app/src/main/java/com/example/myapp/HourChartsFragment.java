package com.example.myapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HourChartsFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private static final String ARG_STR = "param2";
    private static final String ARG_TICKER = "param3";
    private static final String ARG_CHANGE = "param4";
    private Integer counter;
    private String str1;
    private String ticker;
    private Double pchange;

    private int[] COLOR_MAP = {
            R.color.red_100, R.color.red_300, R.color.red_500, R.color.red_700, R.color.blue_100,
            R.color.blue_300, R.color.blue_500, R.color.blue_700, R.color.green_100, R.color.green_300,
            R.color.green_500, R.color.green_700
    };

    public HourChartsFragment() {
        // Required empty public constructor
    }

    public static HourChartsFragment newInstance(Integer counter,String str,String ticker,double change) {
        HourChartsFragment fragment = new HourChartsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        args.putString(ARG_STR, str);
        args.putString(ARG_TICKER, ticker);
        args.putDouble(ARG_CHANGE, change);
        fragment.setArguments(args);
        fragment.str1 = str;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
            str1 = getArguments().getString(ARG_STR);
            ticker = getArguments().getString(ARG_TICKER);
            pchange = getArguments().getDouble(ARG_CHANGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_hour, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), COLOR_MAP[counter]));


        WebView webView = view.findViewById(R.id.chartsinsidehour);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.clearCache(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/hourcharts.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String jString = str1;
                String color = "grey";
                if(pchange>0){
                    color = "green";
                }
                else if(pchange<0){
                    color = "red";
                }
                //view.loadUrl("javascript:setTicker('"+jString+  "','"+ ticker   +"')");
                view.loadUrl(   "javascript:setTicker('"+jString+  "','"+ ticker  +"','"+ color+"')"     );
            }
        });
    }
}