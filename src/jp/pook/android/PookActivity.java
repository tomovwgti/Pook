
package jp.pook.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private static final int AUTH_REQUEST_CODE = 2;

    private WebView mWebView;
    private ProgressDialog mProgress;
    private Button mQrCode;

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

        mQrCode = (Button) findViewById(R.id.qrcode);
        mQrCode.setOnClickListener(new View.OnClickListener() {
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
        } else if (requestCode == AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mWebView.loadUrl(POOK_URL);
        }
    }

    private class WebViewClientWindow extends WebViewClient {
        private static final String FACEBOOK_AUTH = "https://www.facebook.com/login.php";
        private static final String TWITTER_AUTH = "https://twitter.com/oauth/";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgress.dismiss();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith(POOK_POST)) {
                mQrCode.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(POOK_URL)) {
                mQrCode.setVisibility(View.VISIBLE);
                return false;
            } else if (url.startsWith(FACEBOOK_AUTH) || url.startsWith(TWITTER_AUTH)) {
                Intent intent = new Intent();
                intent.setClass(PookActivity.this, AuthActivity.class);
                intent.putExtra("URL", url);
                PookActivity.this.startActivityForResult(intent, AUTH_REQUEST_CODE);
                return true;
            } else {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        }
    }
}
