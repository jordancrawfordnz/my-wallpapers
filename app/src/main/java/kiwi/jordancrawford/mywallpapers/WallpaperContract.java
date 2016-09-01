package kiwi.jordancrawford.mywallpapers;

import android.provider.BaseColumns;

/**
 * Created by Jordan on 30/08/16.
 */
public final class WallpaperContract {
    private WallpaperContract() { }
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DATE_TYPE = INTEGER_TYPE;
    private static final String BOOLEAN_TYPE = "BOOLEAN";

    public static class WallpaperEntry implements BaseColumns {
        public static final String TABLE_NAME = "wallpaper";

        public static final String COLUMN_NAME_DAYS_AS_WALLPAPER = "days_as_wallpaper";
        public static final String COLUMN_NAME_DAYS_AS_WALLPAPER_TYPE = INTEGER_TYPE;
        public static final String COLUMN_NAME_WALLPAPER_SINCE = "wallpaper_since";
        public static final String COLUMN_NAME_WALLPAPER_SINCE_TYPE = DATE_TYPE;
        public static final String COLUMN_NAME_IS_CURRENT = "is_current";
        public static final String COLUMN_NAME_IS_CURRENT_TYPE = BOOLEAN_TYPE;
    }
}
