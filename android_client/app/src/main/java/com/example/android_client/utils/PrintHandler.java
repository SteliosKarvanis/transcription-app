package com.example.android_client.utils;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;

public class PrintHandler {
    public static void createPrintJob(View view, WebView webView) {
        PrintManager printManager = (PrintManager) view.getContext().getSystemService(Context.PRINT_SERVICE);
        String name = "Transcrição";
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(name);
        printManager.print(name, printAdapter, new PrintAttributes.Builder().build());
    }
}
