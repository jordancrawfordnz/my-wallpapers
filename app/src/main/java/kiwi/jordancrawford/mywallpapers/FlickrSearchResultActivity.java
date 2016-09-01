package kiwi.jordancrawford.mywallpapers;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FlickrSearchResultActivity extends AppCompatActivity {
    public static final String API_KEY = "c4b0bc11e918734dd50f7f0eb21051a5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_search_result);

        // TODO: Support caching so not continuously doing network requests.

        // Get the intent for the query.
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            String titleFormat = getResources().getString(R.string.flickr_search_results_title);
            String title = String.format(titleFormat, query);
            getSupportActionBar().setTitle(title);

            RequestQueue queue = Volley.newRequestQueue(this);
            String requestUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&text=" + query + "&format=json&nojsoncallback=1";

            JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<FlickrPhoto> flickrPhotos = new ArrayList<>();
                        JSONObject photosObject = response.getJSONObject("photos");
                        JSONArray photoArray = photosObject.getJSONArray("photo");
                        for (int photoIndex = 0; photoIndex < photoArray.length(); photoIndex++) {
                            JSONObject photoObject = photoArray.getJSONObject(photoIndex);

                            // Populate the FlickrPhoto object.
                            FlickrPhoto photo = new FlickrPhoto();
                            photo.setId(photoObject.getString("id"));
                            photo.setFarmId(photoObject.getString("farm"));
                            photo.setSecret(photoObject.getString("secret"));
                            photo.setServerId(photoObject.getString("server"));
                            flickrPhotos.add(photo);

                            System.out.println(photo);
                            System.out.println(photo.getLargeUrl());
                            System.out.println(photo.getSmallUrl());
                        }
                    } catch (JSONException exception) {
                        // Exception occurred while parsing the JSON, display an error.
                        // TODO: Display error.
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error!");
                }
            });

            queue.add(searchRequest);
        }
    }
}
