
package jp.pook.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

/**
 * Pook for Android
 */
public class PookActivity extends Activity {
    public static final String TAG = PookActivity.class.getSimpleName();

    private static final String POOK_URL = "http://pook.jp";
    private static final String POOK_POST = "http://pook.jp/q/note?title=";
    private static final String QRCODE = "com.google.zxing.client.android.SCAN";
    private static final int REQUEST_CODE = 1;

    private WebView mWebView;
    private ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.show();

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientWindow());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // mWebView.addJavascriptInterface(new JsInterface(this), "android");
        mWebView.loadUrl(POOK_URL);

        Button qrCode = (Button) findViewById(R.id.qrcode);
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCODE);
                try {
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PookActivity.this, "not found Barcode Scanner",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String isbn = data.getStringExtra("SCAN_RESULT");
            mWebView.loadUrl(POOK_POST + isbn);
            if (mProgress == null) {
                mProgress = new ProgressDialog(this);
            }
            mProgress.setMessage("Loading...");
            mProgress.show();
        }
    }

    private class WebViewClientWindow extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgress.dismiss();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://pook.jp/") || url.startsWith("https://twitter.com/oauth/")) {
                return false;
            } else {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        }
    }
}
