package com.ryx.epubtest;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.readium.r2.shared.Link;
import org.readium.r2.shared.Metadata;
import org.readium.r2.shared.Publication;
import org.readium.r2.streamer.parser.EpubParser;
import org.readium.r2.streamer.parser.PubBox;
import org.readium.r2.streamer.server.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static com.ryx.epubtest.AppConstants.LOCALHOST;
import static com.ryx.epubtest.AppConstants.STREAMER_URL_TEMPLATE;

public class MainActivity extends AppCompatActivity implements HtmlTaskCallback {

    private static final String TAG = "nexa_" + MainActivity.class.getSimpleName();

    private WebView webView;
    private Uri streamerUri;
    private int portNumber = AppConstants.DEFAULT_PORT_NUMBER;
    private String bookFileName = "sample";
    private String base_url = "";

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);

//        webView = findViewById(R.id.web_view);

        parseBook();
    }

    private String getFile() {

        File f = new File(getFilesDir() + "/sample.epub");
        if (!f.exists()) try {

            InputStream is = getAssets().open("sample.epub");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Log.d(TAG, "getFile: " + f.getPath());

        return f.getPath();
    }

    private void parseBook() {
        EpubParser parser = new EpubParser();
        PubBox pubBox = parser.parse(getFile(), "Title");
        Publication publication = pubBox.getPublication();
        Metadata metadata = publication.getMetadata();
        List<Link> spine = publication.getReadingOrder();

        try {
            portNumber = AppConstants.getAvailablePortNumber(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Server server = new Server(portNumber);
        server.addEpub(publication, pubBox.getContainer(), "/" + bookFileName,
                null);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BookPagerAdapter adapter = new BookPagerAdapter(getSupportFragmentManager(),
                spine, bookFileName, portNumber);
        viewPager.setAdapter(adapter);

        /*Log.d(TAG, "parseBook: link: " + spine.get(4).getProperties());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        String href = spine.get(4).getHref();
        String page = href.substring(1);
        base_url = getStreamerUri().concat(page);
        Log.d(TAG, "parseBook: StreamUri: " + getStreamerUri());
        Log.d(TAG, "parseBook: " + getStreamerUri().concat(page));

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

//        webView.loadUrl(getStreamerUri().concat(page));
        new HtmlTask(this).execute(base_url);*/
    }

    private String getStreamerUri() {
        if (streamerUri == null) {
            streamerUri = Uri.parse(String.format(Locale.US,
                    STREAMER_URL_TEMPLATE, LOCALHOST, portNumber, bookFileName));
        }
        return streamerUri.toString();
    }

    @Override
    public void onReceiveHtml(String html) {
        Log.d(TAG, "onReceiveHtml: " + html);
        String content = HtmlUtil.getHtmlContent(MainActivity.this, html);
        webView.loadDataWithBaseURL(base_url, content, "text/html",
                "UTF-8", "");
    }

    @Override
    public void onError() {
        Log.d(TAG, "onError: error occured");
    }
}
