package com.piekie.pracly2.utils.interfaces;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class InterfaceMail {
    Context mContext;

    public InterfaceMail(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void performClick() {
        String[] arr = {"aaa@example.com"};
        mailSomebody(arr, "something", "another something");
    }

    private void mailSomebody(String[] to, String subject, String text) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}