package com.ryx.epubtest;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.readium.r2.shared.Link;

import java.util.Locale;

import static com.ryx.epubtest.AppConstants.LOCALHOST;
import static com.ryx.epubtest.AppConstants.STREAMER_URL_TEMPLATE;

public class ChapterFragment extends Fragment implements HtmlTaskCallback {

    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String PARAM3 = "param3";
    private final String TAG = "nexa_" + this.getClass().getSimpleName();
    /////////////////////////////////////WIDGETS////////////////////////////////////////////////////
    private View rootView;
    private WebView webView;
    private ProgressBar progressBar;

    ////////////////////////////////////VARIABLES///////////////////////////////////////////////////
    private Link link;
    private Uri streamerUri;
    private int mPortNumber = AppConstants.DEFAULT_PORT_NUMBER;
    private String mBookFileName = "sample";
    private String mBaseUrl = "";

    public static ChapterFragment newInstance(String bookFileName, int portNumber, Link link) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, bookFileName);
        args.putInt(PARAM2, portNumber);
        args.putSerializable(PARAM3, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBookFileName = getArguments().getString(PARAM1);
            mPortNumber = getArguments().getInt(PARAM2);
            link = (Link) getArguments().getSerializable(PARAM3);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chapter, container, false);

        initView();
        setView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        new HtmlTask(this).execute(mBaseUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void initView() {
        webView = rootView.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        webView.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().toLowerCase().contains("/favicon.ico")) {
                    try {
                        return new WebResourceResponse("image/png", null, null);
                    } catch (Exception e) {
                        Log.e(TAG, "shouldInterceptRequest failed", e);
                    }
                }

                return null;
            }
        });
    }

    private void setView() {
        String href = link.getHref();
        String page = href.substring(1);
        mBaseUrl = getStreamerUri().concat(page);
    }

    private String getStreamerUri() {
        if (streamerUri == null) {
            streamerUri = Uri.parse(String.format(Locale.US,
                    STREAMER_URL_TEMPLATE, LOCALHOST, mPortNumber, mBookFileName));
        }
        return streamerUri.toString();
    }

    @Override
    public void onReceiveHtml(String html) {
        Log.d(TAG, "onReceiveHtml: " + html);
        String content = HtmlUtil.getHtmlContent(getContext(), html);
        webView.loadDataWithBaseURL(mBaseUrl, content, "text/html",
                "UTF-8", "");
    }

    @Override
    public void onError() {
        Log.d(TAG, "onError: error occured");
    }
}

