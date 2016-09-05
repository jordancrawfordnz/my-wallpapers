package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String CHANGE_WALLPAPER_DESCRIPTION_BROADCAST_INTENT = "change_wallpaper_description_message";
    public static final String WALLPAPER_EXTRA = "wallpaper";
    private static int MAX_DESCRIPTION_CHARACTERS = 200;

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
        private TextView descriptionView;
        private ImageButton moreOptionsButton;
        private Button setButton;

        public WallpaperViewHolder(View view) {
            super(view);
            this.view = view;

            this.previewImageView = (ImageView) view.findViewById(R.id.wallpaper_image_view);
            this.daysAsWallpaperView = (TextView) view.findViewById(R.id.wallpaper_days_as_wallpaper);
            this.setButton = (Button) view.findViewById(R.id.wallpaper_card_set_button);
            this.moreOptionsButton = (ImageButton) view.findViewById(R.id.wallpaper_card_more_options_button);
            this.wallpaperIsCurrentImageView = (ImageView) view.findViewById(R.id.wallpaper_is_current);
            this.descriptionView = (TextView) view.findViewById(R.id.wallpaper_card_description);
        }

        public void setupView(final Wallpaper wallpaper) {
            previewImageView.setImageURI(WallpaperUtils.getSmallImageUri(context, wallpaper));

            int actualDaysAsWallpaper = WallpaperUtils.getActualDaysAsWallpaper(wallpaper);
            String daysAsWallpaperText;
            if (actualDaysAsWallpaper == 0 && !wallpaper.isCurrent()) {
                // Use the never text.
                daysAsWallpaperText = context.getResources().getString(R.string.time_as_wallpaper_never);
            } else if (actualDaysAsWallpaper == 1) {
                daysAsWallpaperText = context.getResources().getString(R.string.time_as_wallpaper_days_singular);
            } else {
                    // Use the day display text.
                    String format = context.getResources().getString(R.string.time_as_wallpaper_days);
                    daysAsWallpaperText = String.format(format, actualDaysAsWallpaper);
            }
            daysAsWallpaperView.setText(daysAsWallpaperText);

            if (wallpaper.getDescription() != null && wallpaper.getDescription().length() > 0) {
                String descriptionToShow;
                if (wallpaper.getDescription().length() > MAX_DESCRIPTION_CHARACTERS) {
                    descriptionToShow = wallpaper.getDescription().substring(0, 200);
                    descriptionToShow += "...";
                } else {
                    descriptionToShow = wallpaper.getDescription();
                }
                descriptionView.setText(descriptionToShow);
                descriptionView.setVisibility(View.VISIBLE);
            } else {
                descriptionView.setText("");
                descriptionView.setVisibility(View.GONE);
            }

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

            // Setup the wallpaper options.
            moreOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Setup a popup menu.
                    PopupMenu popup = new PopupMenu(context, moreOptionsButton);
                    popup.getMenuInflater().inflate(R.menu.wallpaper_options, popup.getMenu());

                    // When the popup items are clicked.
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.wallpaper_delete_button) {
                                // When the delete button is clicked, send a broadcast to delete the wallpaper.
                                Intent intent = new Intent(DELETE_WALLPAPER_BROADCAST_INTENT);
                                intent.putExtra(WALLPAPER_EXTRA, wallpaper);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                return true;
                            } else if (item.getItemId() == R.id.wallpaper_change_description_button) {
                                // When the change description button is clicked, send a broadcast to start this process.
                                Intent intent = new Intent(CHANGE_WALLPAPER_DESCRIPTION_BROADCAST_INTENT);
                                intent.putExtra(WALLPAPER_EXTRA, wallpaper);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                return true;
                            }
                            return false;
                        }
                    });

                    popup.show(); // Show the popup menu.
                }
            });
        }
    }


}
