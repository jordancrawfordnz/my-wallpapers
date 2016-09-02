package kiwi.jordancrawford.mywallpapers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by Jordan on 2/09/16.
 */
public class FlickrPreviewListAdapter extends RecyclerView.Adapter<FlickrPreviewListAdapter.FlickrPreviewViewHolder> {
    private ArrayList<FlickrPhoto> photos;
    private ImageLoader imageLoader;

    public FlickrPreviewListAdapter(ArrayList<FlickrPhoto> photos, ImageLoader imageLoader) {
        this.photos = photos;
        this.imageLoader = imageLoader;
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
            String url = photo.getSmallUrl();
            imageLoader.get(url, ImageLoader.getImageListener(imageView, R.drawable.blank, R.drawable.blank));
            imageView.setImageUrl(url, imageLoader);
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
