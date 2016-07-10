package eu.wikijourney.wikijourney.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eu.wikijourney.wikijourney.functions.PoiListAdapter;

/**
 *
 */
public class WebFragment extends Fragment {
    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(eu.wikijourney.wikijourney.R.layout.fragment_web, container, false);

        // TODO Implement this also in the InfoBubbles
        // TODO Change the layout padding so the WebView takes the whole space
        // TODO Add checks so the user can only go to Wikipedia pages
        Bundle args = getArguments();
        String url = args.getString(PoiListAdapter.EXTRA_URL);
        WebView myWebView = (WebView) view.findViewById(eu.wikijourney.wikijourney.R.id.webView);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().endsWith("wikipedia.org")) {
                // This is Wikipedia, so do not override; let the WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

}
