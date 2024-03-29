package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The list adapter used for Flickr search result lists.
 *
 * Created by Jordan on 2/09/16.
 */
public class FlickrPreviewListAdapter extends RecyclerView.Adapter<FlickrPreviewListAdapter.FlickrPreviewViewHolder> {
    public static final String ADD_FLICKR_PHOTO = "add_flickr_photo";
    public static final String FLICKR_PHOTO_EXTRA = "flickr_photo";

    private static final float CHOSEN_ALPHA = 0.5f;
    private static final float NOT_CHOSEN_ALPHA = 1f;

    private ArrayList<FlickrPhoto> photos;
    private ArrayList<String> downloadedPhotoIds;
    private ImageLoader imageLoader;
    private Context context;

    public FlickrPreviewListAdapter(Context context, ArrayList<FlickrPhoto> photos, ArrayList<String> downloadedPhotoIds, ImageLoader imageLoader) {
        this.photos = photos;
        this.imageLoader = imageLoader;
        this.context = context.getApplicationContext();
        this.downloadedPhotoIds = downloadedPhotoIds;
    }

    public class FlickrPreviewViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private NetworkImageView imageView;

        public FlickrPreviewViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = (NetworkImageView) view.findViewById(R.id.flickr_preview_image);
        }

        public void setupView(final FlickrPhoto photo) {
            // Load the thumbnail image. (see FlickrRequestQueue, this uses a cache).
            String url = photo.getThumbnailUrl();
            imageLoader.get(url, ImageLoader.getImageListener(imageView, R.drawable.blank, R.drawable.blank));
            imageView.setImageUrl(url, imageLoader);
            if (downloadedPhotoIds.contains(photo.getId())) {
                imageView.setAlpha(CHOSEN_ALPHA);
            } else {
                imageView.setAlpha(NOT_CHOSEN_ALPHA);
            }

            // Setup a click listener.
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!downloadedPhotoIds.contains(photo.getId())) {
                        imageView.setAlpha(CHOSEN_ALPHA);
                        downloadedPhotoIds.add(photo.getId());

                        Intent intent = new Intent(ADD_FLICKR_PHOTO);
                        intent.putExtra(FLICKR_PHOTO_EXTRA, photo);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
            });
        }
    }

    @Override
    public FlickrPreviewListAdapter.FlickrPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a view to display the photo.
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.flickr_preview, parent, false);

        return new FlickrPreviewViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(FlickrPreviewListAdapter.FlickrPreviewViewHolder holder, int position) {
        // Get the photo.
        FlickrPhoto photo = photos.get(position);
        holder.setupView(photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
