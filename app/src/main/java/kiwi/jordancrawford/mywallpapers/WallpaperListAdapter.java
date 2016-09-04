package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Used to display a list of Wallpapers with metadata and options to use or delete.
 *
 * Created by Jordan on 31/08/16.
 */
public class WallpaperListAdapter extends RecyclerView.Adapter<WallpaperListAdapter.WallpaperViewHolder> {
    private Context context;
    private ArrayList<Wallpaper> wallpapers;
    public static final String SET_WALLPAPER_BROADCAST_INTENT = "set_wallpaper_message";
    public static final String DELETE_WALLPAPER_BROADCAST_INTENT = "delete_wallpaper_message";
    public static final String WALLPAPER_EXTRA = "wallpaper";

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
        private ImageView wallpaperIsCurrentImageView;
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
            this.wallpaperIsCurrentImageView = (ImageView) view.findViewById(R.id.wallpaper_is_current);
        }

        public void setupView(final Wallpaper wallpaper) {
            previewImageView.setImageURI(WallpaperUtils.getSmallImageUri(context, wallpaper));

            String daysAsWallpaperText;
            if (wallpaper.getDaysAsWallpaper() == 0 && !wallpaper.isCurrent()) {
                // Use the never text.
                daysAsWallpaperText = context.getResources().getString(R.string.time_as_wallpaper_never);
            } else {
                // Use the day display text.
                String format = context.getResources().getString(R.string.time_as_wallpaper_days);
                daysAsWallpaperText = String.format(format, WallpaperUtils.getActualDaysAsWallpaper(wallpaper));
            }
            daysAsWallpaperView.setText(daysAsWallpaperText);

            // Show a check mark if the wallpaper is the current wallpaper.
            if (wallpaper.isCurrent()) {
                wallpaperIsCurrentImageView.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                wallpaperIsCurrentImageView.setImageResource(R.drawable.blank);
            }

            // When the set button is clicked, send a broadcast to set the wallpaper.
            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SET_WALLPAPER_BROADCAST_INTENT);
                    intent.putExtra(WALLPAPER_EXTRA, wallpaper);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
            setButton.setEnabled(!wallpaper.isCurrent);

            // When the delete button is clicked, send a broadcast to delete the wallpaper.
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DELETE_WALLPAPER_BROADCAST_INTENT);
                    intent.putExtra(WALLPAPER_EXTRA, wallpaper);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }


}
