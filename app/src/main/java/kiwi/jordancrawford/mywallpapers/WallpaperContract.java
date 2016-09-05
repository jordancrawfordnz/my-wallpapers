package kiwi.jordancrawford.mywallpapers;

import android.provider.BaseColumns;

/**
 * Stores types and column names for the database.
 *
 * Created by Jordan on 30/08/16.
 */
public final class WallpaperContract {
    private WallpaperContract() { }
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DATE_TYPE = INTEGER_TYPE;
    private static final String BOOLEAN_TYPE = "BOOLEAN";
    private static final String TEXT_TYPE = "TEXT";

    public static class WallpaperEntry implements BaseColumns {
        public static final String TABLE_NAME = "wallpaper";

        public static final String DAYS_AS_WALLPAPER_COLUMN_NAME = "days_as_wallpaper";
        public static final String DAYS_AS_WALLPAPER_TYPE = INTEGER_TYPE;
        public static final String WALLPAPER_SINCE_COLUMN_NAME = "wallpaper_since";
        public static final String WALLPAPER_SINCE_TYPE = DATE_TYPE;
        public static final String IS_CURRENT_COLUMN_NAME = "is_current";
        public static final String IS_CURRENT_TYPE = BOOLEAN_TYPE;
        public static final String DESCRIPTION_COLUMN_NAME = "description";
        public static final String DESCRIPTION_TYPE = TEXT_TYPE;
    }
}
