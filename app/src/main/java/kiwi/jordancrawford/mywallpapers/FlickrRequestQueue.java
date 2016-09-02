package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * With help from: http://www.truiton.com/2015/03/android-volley-imageloader-networkimageview-example/
 *
 * Created by Jordan on 2/09/16.
 */
public class FlickrRequestQueue {
    private static FlickrRequestQueue instance;
    private static Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static final int CACHE_SIZE = 30 * 1024 * 1024; // 30MB
    private static final int IMAGES_TO_CACHE = 20;

    private FlickrRequestQueue(Context context) {
        this.context = context.getApplicationContext();

        // Setup the request queue.
        Cache cache = new DiskBasedCache(this.context.getCacheDir(), CACHE_SIZE);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);

        // Start the queue.
        requestQueue.start();

        // Setup the image loader.
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(IMAGES_TO_CACHE);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized FlickrRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new FlickrRequestQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
