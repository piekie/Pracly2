package com.piekie.pracly2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.piekie.pracly2.R;
import com.piekie.pracly2.utils.interfaces.InterfaceCall;

public class Html5WebView extends WebView {

    static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private Context                                     mContext;
    private ChromeClient                                mWebChromeClient;
    private WebChromeClient.CustomViewCallback          mCustomViewCallback;
    private View                                        mCustomView;
    private FrameLayout                                 mLayout;
    private FrameLayout                                 mBrowserFrameLayout;
    private FrameLayout                                 mContentView;
    private FrameLayout                                 mCustomViewContainer;


    public Html5WebView(Context context) {
        super(context);
        init(context);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        Activity activity = (Activity) mContext;

        mLayout = new FrameLayout(context);
        mBrowserFrameLayout = (FrameLayout) LayoutInflater.from(activity).inflate(R.layout.custom_screen, null);
        mContentView = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.main_content);
        mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);

        //TODO: add layoutParams
        mLayout.addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);

        WebSettings settings = getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);

        //TODO: watch on vulnerabilities
        settings.setJavaScriptEnabled(true);

        addJavascriptInterface(new InterfaceCall(context), "btnCall");

        mWebChromeClient = new ChromeClient();
        setWebChromeClient(mWebChromeClient);
        setWebViewClient(new WebViewClient());

        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //TODO: geolocation?

        settings.setDomStorageEnabled(true);
        mContentView.addView(this);

    }

    public FrameLayout getLayout() {
        return mLayout;
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((mCustomView == null) && canGoBack()) {
                goBack();
                return true;
            }
        }
        return false;
    }

    private class ChromeClient extends WebChromeClient {
        private Bitmap                      mDefaultVideoPoster;
        private View                        mVideoProgressView;

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            Html5WebView.this.setVisibility(View.GONE);

            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewCallback.onCustomViewHidden();

            Html5WebView.this.setVisibility(View.VISIBLE);
            Html5WebView.this.goBack();
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null)
                return;

            mCustomView.setVisibility(View.GONE);

            mCustomViewContainer.removeView(mContentView);
            mCustomView = null;
            mCustomViewContainer.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();

            Html5WebView.this.setVisibility(View.VISIBLE);
            Html5WebView.this.goBack();
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            ((Activity) mContext).setTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            ((Activity) mContext).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }
}