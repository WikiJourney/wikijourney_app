package eu.wikijourney.wikijourney.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import eu.wikijourney.wikijourney.GlobalState;
import eu.wikijourney.wikijourney.functions.CustomInfoWindow;
import eu.wikijourney.wikijourney.functions.Map;
import eu.wikijourney.wikijourney.functions.POI;
import eu.wikijourney.wikijourney.functions.UI;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;

public class MapFragment extends Fragment {

    private static final String LOG_PROGRESS = "WJ-Map:Progress";
    private static final String LOG_ERROR = "WJ-Map:Error";
    private GlobalState gs;

    // Variables for API
    private String language = "en";
    private double paramRange;
    private int paramMaxPoi;
    private String paramPlace;
    private int paramMethod; //Could be around or place, depends on which button was clicked.

    //Now the variables we are going to use for the rest of the program.
    private LocationManager locationManager;
    private LocationListener locationListener;

    private MapView map;
    private GeoPoint userLocation;
    private boolean isUserLocatedOnce = false;
    private Marker userLocationMarker;

    private Snackbar locatingSnackbar;
    private Snackbar downloadSnackbar;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(eu.wikijourney.wikijourney.R.layout.fragment_map, container, false);

        // These lines initialize the map settings
        map = (MapView) view.findViewById(eu.wikijourney.wikijourney.R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        IMapController mapController = map.getController();
        mapController.setZoom(16);

        userLocationMarker = new Marker(map);

        language = Locale.getDefault().getLanguage();

        // We get the Bundle values
        Bundle args = getArguments();

        try {
            paramMaxPoi = args.getInt(HomeFragment.EXTRA_OPTIONS[0]);
        } catch (Exception e) {
            paramMaxPoi = getResources().getInteger(eu.wikijourney.wikijourney.R.integer.default_maxPOI);
        }
        try {
            paramRange = args.getDouble(HomeFragment.EXTRA_OPTIONS[1]);
        } catch (Exception e) {
            paramRange = getResources().getInteger(eu.wikijourney.wikijourney.R.integer.default_range);
        }
        try {
            paramPlace = args.getString(HomeFragment.EXTRA_OPTIONS[2]);
        } catch (Exception e) {
            paramPlace = "null"; // Place value
        }
        try {
            paramMethod = args.getInt(HomeFragment.EXTRA_OPTIONS[3]);
        } catch (Exception e) {
            paramMethod = HomeFragment.METHOD_AROUND;
        }

        if (paramMethod == HomeFragment.METHOD_AROUND) {
            locateUser();
        } else if(paramMethod == HomeFragment.METHOD_PLACE) {
            drawMap(paramPlace);
        } else if(paramMethod == HomeFragment.METHOD_URI) {
            GeoUri parser = new GeoUri(GeoUri.OPT_DEFAULT);
            String geoUriString = args.getString(HomeFragment.EXTRA_OPTIONS[4], null);

            IGeoPointInfo geoPoint = parser.fromUri(geoUriString);
            if (geoPoint != null) {
                if (!GeoPointDto.isEmpty(geoPoint)) {
                    paramMethod = HomeFragment.METHOD_AROUND;
                    Location placeLocation = new Location(geoPoint.getName());
                    placeLocation.setLatitude(geoPoint.getLatitude());
                    placeLocation.setLongitude(geoPoint.getLongitude());
                    drawCurrentLocation(placeLocation);
                    drawMap();
                } else if (geoPoint.getName() != null) {
                    paramMethod = HomeFragment.METHOD_PLACE;
                    paramPlace = geoPoint.getName();
                    drawMap(paramPlace);
                }
            }
        }

        return view;
    }

    private void locateUser() {
        // Display a Snackbar while the phone locates the user, so he doesn't think the app crashed
        if (getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container) != null) {
            locatingSnackbar = Snackbar.make(getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container), eu.wikijourney.wikijourney.R.string.snackbar_locating, Snackbar.LENGTH_INDEFINITE);
            locatingSnackbar.show();
        }

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Once located, download the info from the API and display the map
                if (locatingSnackbar != null) {
                    locatingSnackbar.dismiss();
                }
                // TODO Temporary fix
                // This stop the location updates, so the map doesn't always refresh
                // locationManager.removeUpdates(locationListener);
                drawCurrentLocation(location);
                if (!isUserLocatedOnce) {
                    isUserLocatedOnce = true;
                    drawMap();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Stop the Geolocation if the user leaves the MapFragment early
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (locatingSnackbar != null) {
            locatingSnackbar.dismiss();
        }
        if (downloadSnackbar != null) {
            downloadSnackbar.dismiss();
        }
    }

    private void drawCurrentLocation(Location location) {

        IMapController mapController = map.getController();

        // This starts the map at the desired point
        userLocation = new GeoPoint(location);
        if (!isUserLocatedOnce) {
            mapController.setCenter(userLocation);
        }

        // Now we add a marker using osmBonusPack
        userLocationMarker.setPosition(userLocation);
        userLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(userLocationMarker);

        // And we have to use this to refresh the map
        // map.invalidate();

        // We can change some properties of the marker (don't forget to refresh the map !!)
        userLocationMarker.setInfoWindow(new CustomInfoWindow(map));
        Drawable icon = null;
        if (getActivity() != null) {
            icon = ContextCompat.getDrawable(getActivity(), eu.wikijourney.wikijourney.R.drawable.ic_place);
        }
        try {
            userLocationMarker.setIcon(icon);
            userLocationMarker.setTitle(getString(eu.wikijourney.wikijourney.R.string.you_are_here));
            map.invalidate();
        } catch (Exception e) {
            Log.e(LOG_ERROR, "drawCurrentLocation: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void drawMap() {
        // We get the POI around the user with WikiJourney API
        String url;
        url = gs.API_URL + "long=" + userLocation.getLongitude() + "&lat=" + userLocation.getLatitude()
                + "&maxPOI=" + paramMaxPoi + "&range=" + paramRange + "&lg=" + language;

        // Check if the Internet is up
        final ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // These are needed for the download library and the methods called later
        final Context context = this.getActivity();
        final MapFragment mapFragment = this;

        if (networkInfo != null && networkInfo.isConnected()) {
            // Show a Snackbar while we wait for WikiJourney server, so the user doesn't think the app crashed
            if (getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container) != null) {
                downloadSnackbar = Snackbar.make(getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container), eu.wikijourney.wikijourney.R.string.snackbar_downloading, Snackbar.LENGTH_INDEFINITE);
                downloadSnackbar.show();
            }
            new DownloadWjApi(url, HomeFragment.METHOD_AROUND, context, mapFragment).invoke(false);

        } else {
            UI.openPopUp(mapFragment.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet));
        }
    }
    private void drawMap(String paramPlace) {
        // We get the POI around the user with WikiJourney API
        String url;
        String encodedPlace = "";
        try { // https://stackoverflow.com/a/10786112/3641865
            encodedPlace = URLEncoder.encode(paramPlace, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_ERROR, "drawMap: " + e.getMessage(), e);

            e.printStackTrace();
        }
        url = gs.API_URL + "place=" + encodedPlace + "&maxPOI=" + paramMaxPoi + "&range="
                + paramRange + "&lg=" + language;

        // Check if the Internet is up
        final ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // These are needed for the download library and the methods called later
        final Context context = this.getActivity();
        final MapFragment mapFragment = this;

        if (networkInfo != null && networkInfo.isConnected()) {
            // Show a Snackbar while we wait for WikiJourney server, so the user doesn't think the app crashed
            if (getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container) != null) {
                downloadSnackbar = Snackbar.make(getActivity().findViewById(eu.wikijourney.wikijourney.R.id.fragment_container), eu.wikijourney.wikijourney.R.string.snackbar_downloading, Snackbar.LENGTH_INDEFINITE);
                downloadSnackbar.show();
            }
            new DownloadWjApi(url, HomeFragment.METHOD_PLACE, context, mapFragment).invoke(false);

        } else {
            UI.openPopUp(mapFragment.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet));
        }
    }

    private class DownloadWjApi {
        private final Context context;
        private final MapFragment mapFragment;
        private String url;
        private final int paramMethod;

        public DownloadWjApi(String url, int paramMethod, Context context, MapFragment mapFragment) {
            this.url = url;
            this.context = context;
            this.mapFragment = mapFragment;
            this.paramMethod = paramMethod;
        }

        public void invoke(boolean useSelfSignedSSL) {
            // Download from the WJ API
            AsyncHttpClient client = new AsyncHttpClient();
            if (useSelfSignedSSL) {
                try { // We add the certificate chain, because the intermediate cert issued by Let's Encrypt isn't in default KeyStore
                    KeyStore trustStore = MySSLSocketFactory.getKeystoreOfCA(getResources().openRawResource(eu.wikijourney.wikijourney.R.raw.fullchain));
                    MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                    client.setSSLSocketFactory(sf);
                }
                catch (Exception e) {
                    Log.e(LOG_ERROR, "useSelfSignedSSL: " + e.getMessage(), e);
                    // Empty catch, what should we put here?
                }
            }

            Log.d(LOG_PROGRESS,"Downloading " + url);

            client.setTimeout(30_000); // Set timeout to 30s, the server may be slow...
            client.get(context, url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (downloadSnackbar != null) {
                        downloadSnackbar.dismiss();
                    }
                    ArrayList<POI> poiArrayList;
                    boolean errorOccurred = true;
                    String errorMessage = null;
                    boolean isPoiAround = false;
                    // We check if the server answered correctly and if there is any POI around
                    try {
                        errorOccurred = response.getJSONObject("err_check").getBoolean("value");
                        if (errorOccurred) {
                            errorMessage = response.getJSONObject("err_check").getString("err_msg");
                        } else {
                            errorOccurred = false;
                        }

                        if (response.getJSONObject("poi").getInt("nb_poi") == 0) {
                            isPoiAround = false;
                        } else {
                            isPoiAround = true;
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_ERROR,"While downloading " + url + ":" + e.getMessage(),e);
                        errorOccurred = true;
                        e.printStackTrace();
                    }
                    if (errorOccurred) {
                        UI.openPopUp(mapFragment.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_download_api_response_title), errorMessage);
                    } else if (!isPoiAround) {
                        Log.i(LOG_PROGRESS,"No found for POI for " + url);
                        UI.openPopUp(mapFragment.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_no_poi_around_title),
                                getResources().getString(eu.wikijourney.wikijourney.R.string.error_no_poi_around));
                    } else {
                        if (paramMethod == HomeFragment.METHOD_PLACE) {
                            JSONObject placeLocationJson = null;
                            double placeLat = 0;
                            double placeLong = 0;
                            try {
                                placeLocationJson = response.getJSONObject("user_location");
                                placeLat = placeLocationJson.getDouble("latitude");
                                placeLong = placeLocationJson.getDouble("longitude");

                                Location placeLocation = new Location("test");
                                placeLocation.setLatitude(placeLat);
                                placeLocation.setLongitude(placeLong);
                                drawCurrentLocation(placeLocation);
                            } catch (JSONException e) {
                                Log.e(LOG_ERROR,"cannot get lat/lon from " + url + ":" + e.getMessage(),e);
                                e.printStackTrace();
                            }
                        }

                        poiArrayList = POI.parseApiJson(response, paramMethod, context);
                        if (poiArrayList != null && poiArrayList.size() != 0) {
                            Map.drawPOI(mapFragment, poiArrayList);
                        } else {
                            UI.openPopUp(context, getResources().getString(eu.wikijourney.wikijourney.R.string.error_no_poi_around_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_no_poi_around));
                        }
                    }
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.d(LOG_PROGRESS, "Downloading " + bytesWritten + " of " + totalSize);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    try {
                        Log.e(LOG_ERROR, errorResponse.toString());
                    } catch (Exception e) {
                        Log.e(LOG_ERROR, "Error while downloading the API response: " + e.getMessage(), e);
                    }
                    finally {
                        UI.openPopUp(mapFragment.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_download_api_response_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_download_api_response));
                        if (downloadSnackbar != null) {
                            downloadSnackbar.dismiss();
                        }
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    Log.e(LOG_ERROR, "Retrying for the " + retryNo + " time");
                    super.onRetry(retryNo);
                }
            });
        }
    }

}