package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jordan on 31/08/16.
 */
public class WallpaperListAdapter extends RecyclerView.Adapter<WallpaperListAdapter.WallpaperViewHolder> {
    private Context context;
    private ArrayList<Wallpaper> wallpapers;

    public WallpaperListAdapter(Context context, ArrayList<Wallpaper> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a view to display the wallpaper.
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_card, parent, false);

        return new WallpaperViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(WallpaperViewHolder holder, int position) {
        // Get the Wallpaper.
        Wallpaper wallpaper = wallpapers.get(position);
        holder.setupView(wallpaper);
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    public class WallpaperViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private ImageView previewImageView;
        private TextView daysAsWallpaperView;
        private Button deleteButton;
        private Button setButton;

        public WallpaperViewHolder(View view) {
            super(view);
            this.view = view;

            this.previewImageView = (ImageView) view.findViewById(R.id.wallpaper_image_view);
            this.daysAsWallpaperView = (TextView) view.findViewById(R.id.wallpaper_days_as_wallpaper);
            this.setButton = (Button) view.findViewById(R.id.wallpaper_card_set_button);
            this.deleteButton = (Button) view.findViewById(R.id.wallpaper_card_delete_button);
        }

        public void setupView(final Wallpaper wallpaper) {
            previewImageView.setImageURI(WallpaperUtils.getSmallImageUri(context, wallpaper));
            daysAsWallpaperView.setText(String.valueOf(wallpaper.getDaysAsWallpaper()));
            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Send an event to the activity.
                    System.out.println("Set");
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Send an event to the activity.
                    System.out.println("Delete");
                }
            });
        }
    }


}
