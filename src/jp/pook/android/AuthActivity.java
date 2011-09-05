
package jp.pook.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthActivity extends Activity {
    public static final String TAG = AuthActivity.class.getSimpleName();
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.bottom).setVisibility(View.GONE);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientWindow());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // mWebView.addJavascriptInterface(new JsInterface(this), "android");
        mWebView.loadUrl(getIntent().getStringExtra("URL"));
    }

    private class WebViewClientWindow extends WebViewClient {
        private static final String POOK_URL = "http://pook.jp";

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith(POOK_URL)) {
                setResult(RESULT_OK);
                finish();
            }
            super.onPageFinished(view, url);
        }
    }
}
