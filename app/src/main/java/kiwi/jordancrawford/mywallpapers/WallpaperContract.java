package kiwi.jordancrawford.mywallpapers;

import android.provider.BaseColumns;

/**
 * Created by Jordan on 30/08/16.
 */
public final class WallpaperContract {
    private WallpaperContract() { }
    private static final String TEXT_TYPE = "TEXT";
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DATE_TYPE = INTEGER_TYPE;

    public static class WallpaperEntry implements BaseColumns {
        public static final String TABLE_NAME = "wallpaper";
        public static final String COLUMN_NAME_SMALL_PICTURE = "small_picture_filename";
        public static final String COLUMN_NAME_SMALL_PICTURE_TYPE = TEXT_TYPE;

        public static final String COLUMN_NAME_LARGE_PICTURE = "large_picture_filename";
        public static final String COLUMN_NAME_LARGE_PICTURE_TYPE = TEXT_TYPE;

        public static final String COLUMN_NAME_DAYS_AS_WALLPAPER = "days_as_wallpaper";
        public static final String COLUMN_NAME_DAYS_AS_WALLPAPER_TYPE = INTEGER_TYPE;
    }

    public static class ConfigEntry implements BaseColumns {
        public static final String TABLE_NAME = "config";
        public static final String COLUMN_NAME_SET_WALLPAPER_DATE = "set_wallpaper_date";
        public static final String COLUMN_NAME_SET_WALLPAPER_DATE_TYPE = DATE_TYPE;

        public static final String COLUMN_NAME_CURRENT_WALLPAPER = "current_wallpaper";
        public static final String COLUMN_NAME_CURRENT_WALLPAPER_TYPE = INTEGER_TYPE;
    }
}
