package com.piekie.pracly2.utils.interfaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.JavascriptInterface;

public class InterfaceCall {
    Context mContext;

    public InterfaceCall(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void performClick() {
        callSomebody("*121#");
    }

    private void callSomebody(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (mContext.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            mContext.startActivity(callIntent);
        }
    }
}
