package kiwi.jordancrawford.mywallpapers;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Loads and displays a Flickr search result.
 */
public class FlickrSearchResultActivity extends AppCompatActivity {
    private static final String API_KEY = "c4b0bc11e918734dd50f7f0eb21051a5";
    private static final String DOWNLOADED_PHOTO_IDS_KEY = "downloaded_photo_ids";
    private ImageLoader imageLoader;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private ArrayList<FlickrPhoto> photos = new ArrayList<>();
    private ArrayList<String> downloadedPhotoIds;
    private LinearLayoutCompat noFlickrResultsMessage;
    private ProgressBar loadingSpinner;

    private BroadcastReceiver addFlickrPhotoMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final FlickrPhoto photo = (FlickrPhoto) intent.getParcelableExtra(FlickrPreviewListAdapter.FLICKR_PHOTO_EXTRA);

            Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_download_started, Toast.LENGTH_SHORT).show();

            // Download the photos.
                // The way this is implemented is not ideal as only one image will be downloaded at once.
                // However, as we don't know which image will download first this would be complicated to implement in parallel.
            String smallImageUrl = photo.getSmallUrl();
            FlickrRequestQueue.getInstance(context).getImageLoader().get(smallImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer smallImageResponse, boolean isImmediate) {
                    Bitmap smallImage = smallImageResponse.getBitmap();
                    if (smallImage != null) {
                        // Download the large image.
                        String largeImageUrl = photo.getLargeUrl();
                        FlickrRequestQueue.getInstance(context).getImageLoader().get(largeImageUrl, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer largeImageResponse, boolean isImmediate) {
                                Bitmap largeImage = largeImageResponse.getBitmap();
                                if (largeImage != null) {
                                    WallpaperBitmaps bitmaps = new WallpaperBitmaps();
                                    bitmaps.setSmallImage(smallImageResponse.getBitmap());
                                    bitmaps.setLargeImage(largeImageResponse.getBitmap());

                                    // Start an async task to save the wallpaper.
                                    new ProcessDownloadedWallpaperTask(context, photo.getDescription()).execute(bitmaps);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Show an error message. This will prevent the photo being added as a wallpaper.
                                error.printStackTrace();
                                Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_get_large_image_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // Show an error message. This will prevent the photo being added as a wallpaper.
                    error.printStackTrace();
                    Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_get_small_image_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_search_result);
        queue = FlickrRequestQueue.getInstance(this).getRequestQueue();
        imageLoader = FlickrRequestQueue.getInstance(this).getImageLoader();

        // Hide the 'no results' message until loaded.
        noFlickrResultsMessage = (LinearLayoutCompat) findViewById(R.id.no_flickr_results_message);
        noFlickrResultsMessage.setVisibility(View.GONE);

        // Spin the loading spinner until loaded.
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.isIndeterminate();

        if (savedInstanceState != null) {
            downloadedPhotoIds = savedInstanceState.getStringArrayList(DOWNLOADED_PHOTO_IDS_KEY);
        }
        if (downloadedPhotoIds == null) {
            downloadedPhotoIds = new ArrayList<>();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(addFlickrPhotoMessageReceiver, new IntentFilter(FlickrPreviewListAdapter.ADD_FLICKR_PHOTO));

        // Display the Flickr results list.
        recyclerView = (RecyclerView) findViewById(R.id.flickr_preview_recycler_view);
        int deviceOrientation = getResources().getConfiguration().orientation;
        int numberOfColumns = deviceOrientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        recyclerViewLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new FlickrPreviewListAdapter(this, photos, downloadedPhotoIds, imageLoader);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Get the intent for the query.
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            String titleFormat = getResources().getString(R.string.flickr_search_results_title);
            String title = String.format(titleFormat, query);
            getSupportActionBar().setTitle(title);

            String encodedQuery = null;
            try {
                encodedQuery = URLEncoder.encode(query, "utf-8");

                String requestUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&extras=description&api_key=" + API_KEY + "&text=" + encodedQuery + "&format=json&nojsoncallback=1";
                JsonObjectRequest searchRequest = new CachedJsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Process the results into the photos list.
                            photos.clear();
                            JSONObject photosObject = response.getJSONObject("photos");
                            JSONArray photoArray = photosObject.getJSONArray("photo");
                            for (int photoIndex = 0; photoIndex < photoArray.length(); photoIndex++) {
                                JSONObject photoObject = photoArray.getJSONObject(photoIndex);
                                JSONObject descriptionObject = photoObject.getJSONObject("description");

                                // Populate the FlickrPhoto object.
                                FlickrPhoto photo = new FlickrPhoto();
                                photo.setId(photoObject.getString("id"));
                                photo.setFarmId(photoObject.getString("farm"));
                                photo.setSecret(photoObject.getString("secret"));
                                photo.setServerId(photoObject.getString("server"));

                                // Strip HTML tags with Android.
                                String description = Html.fromHtml(descriptionObject.getString("_content")).toString();
                                if (description.length() > 0) {
                                    photo.setDescription(description);
                                }

                                photos.add(photo);
                            }

                            // Hide the loading spinner and display the 'no results' message if needed.
                            loadingSpinner.setVisibility(View.GONE);
                            if (photos.size() == 0) {
                                noFlickrResultsMessage.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                recyclerViewAdapter.notifyDataSetChanged();
                                noFlickrResultsMessage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException exception) {
                            // Exception occurred while parsing the JSON, display an error.
                            exception.printStackTrace();
                            Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_json_exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_search_error, Toast.LENGTH_SHORT).show();
                    }
                });

                // Start the request processing.
                queue.add(searchRequest);
            } catch (UnsupportedEncodingException exception) {
                exception.printStackTrace();
                Toast.makeText(FlickrSearchResultActivity.this, R.string.flickr_unsupported_encoding_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Unregister from local async events.
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addFlickrPhotoMessageReceiver);
        super.onDestroy();
    }

    // Save the downloaded photo ID's.
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putStringArrayList(DOWNLOADED_PHOTO_IDS_KEY, downloadedPhotoIds);
    }
}
