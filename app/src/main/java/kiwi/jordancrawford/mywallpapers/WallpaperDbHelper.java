package kiwi.jordancrawford.mywallpapers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import static kiwi.jordancrawford.mywallpapers.WallpaperContract.WallpaperEntry;

/**
 * Created by Jordan on 30/08/16.
 */
public class WallpaperDbHelper extends SQLiteOpenHelper {
    private static final String NO_ID_EXCEPTION_MESSAGE = "No ID on record. Is it saved?";
    private static final String HAS_ID_EXCEPTION_MESSAGE = "Record has ID. Has it already been saved?";
    private static final String ID_PROPERTIES = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String SQL_CREATE_TABLE_WALLPAPERS =
            "CREATE TABLE " + WallpaperEntry.TABLE_NAME + "("
            + WallpaperEntry._ID + " " + ID_PROPERTIES + ","
            + WallpaperEntry.COLUMN_NAME_DAYS_AS_WALLPAPER + " " + WallpaperEntry.COLUMN_NAME_DAYS_AS_WALLPAPER_TYPE + ","
            + WallpaperEntry.COLUMN_NAME_IS_CURRENT + " " + WallpaperEntry.COLUMN_NAME_IS_CURRENT + ","
            + WallpaperEntry.COLUMN_NAME_WALLPAPER_SINCE + " " + WallpaperEntry.COLUMN_NAME_WALLPAPER_SINCE_TYPE + ")";
    private static final String SQL_DROP_TABLE_WALLPAPERS =
            "DROP TABLE IF EXISTS " + WallpaperEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wallpapers.db";

    public static WallpaperDbHelper instance = null;

    /*
        As per: http://stackoverflow.com/questions/8888530/is-it-ok-to-have-one-instance-of-sqliteopenhelper-shared-by-all-activities-in-an
        If this gets cleared it doesn't matter, a new helper instance can be created. This ensures all accesses will use the same SQLIteDatabase object.
     */
    public static WallpaperDbHelper getInstance(Context context) {
        if (instance == null) {
            // Instantiate with the application context so the activity context isn't leaked.
            instance = new WallpaperDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private WallpaperDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private ContentValues getWallpaperFields(Wallpaper wallpaper) {
        ContentValues values = new ContentValues();
        values.put(WallpaperEntry.COLUMN_NAME_DAYS_AS_WALLPAPER, wallpaper.getDaysAsWallpaper());
        values.put(WallpaperEntry.COLUMN_NAME_IS_CURRENT, wallpaper.isCurrent());
        values.put(WallpaperEntry.COLUMN_NAME_WALLPAPER_SINCE, wallpaper.getWallpaperSince());
        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_WALLPAPERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If the schema changes, just delete everything and re-create it.
        db.execSQL(SQL_DROP_TABLE_WALLPAPERS);
        onCreate(db);
    }

    // Gets all the wallpapers, with the most commonly used on top.
        // TODO: Make most commonly used go to the top.
    public ArrayList<Wallpaper> getAllWallpapers() {
        String[] projection = {
                WallpaperEntry._ID,
                WallpaperEntry.COLUMN_NAME_DAYS_AS_WALLPAPER,
                WallpaperEntry.COLUMN_NAME_IS_CURRENT,
                WallpaperEntry.COLUMN_NAME_WALLPAPER_SINCE
        };
        Cursor queryResult = getReadableDatabase().query(
                WallpaperEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        ArrayList<Wallpaper> wallpaperResult = new ArrayList<>();
        if (queryResult.moveToFirst()) {
            do {
                Wallpaper currentWallpaper = new Wallpaper();
                currentWallpaper.setId(queryResult.getInt(0));
                currentWallpaper.setDaysAsWallpaper(queryResult.getInt(1));
                currentWallpaper.setCurrent(queryResult.getInt(2) == 1);
                currentWallpaper.setWallpaperSince(queryResult.getLong(3));
                wallpaperResult.add(currentWallpaper);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return wallpaperResult;
    }

    // Adds a wallpaper.
    public void addWallpaper(Wallpaper toAdd) {
        if (toAdd.getId() != -1) {
            throw new IllegalArgumentException(HAS_ID_EXCEPTION_MESSAGE);
        }
        long id = getWritableDatabase().insert(
                WallpaperEntry.TABLE_NAME,
                null,
                getWallpaperFields(toAdd));
        toAdd.setId(id);
    }

    // Updates the wallpaper. True if updated, false if an error occured.
    public boolean updateWallpaper(Wallpaper toUpdate) {
        if (toUpdate.getId() == -1) {
            throw new IllegalArgumentException(NO_ID_EXCEPTION_MESSAGE);
        }
        int updateResult = getWritableDatabase().update(
                WallpaperEntry.TABLE_NAME,
                getWallpaperFields(toUpdate),
                WallpaperEntry._ID + " = ?",
                new String[] { String.valueOf(toUpdate.getId()) });
        return updateResult == 1;
    }

    // Deletes the wallpaper. True if deleted, false if an error occurred.
    public boolean deleteWallpaper(Wallpaper toDelete) {
        if (toDelete.getId() == -1) {
            throw new IllegalArgumentException(NO_ID_EXCEPTION_MESSAGE);
        }
        int deleteResult = getWritableDatabase().delete(
                WallpaperEntry.TABLE_NAME,
                WallpaperEntry._ID +  " = ?",
                new String[] { String.valueOf(toDelete.getId()) });
        return deleteResult == 1;
    }
}
