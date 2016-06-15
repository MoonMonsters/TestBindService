package chalmers.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chalmers on 2016-06-15 21:11.
 * email:qxinhai@yeah.net
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static final int VERSION = 1;

    private final String CREATE_TABLE_THREAD_INFO = "create table thread_info(_id integer primary key autoincrement," +
            "url text, " +
            "filename, " +
            "start number, " +
            "end number, " +
            "finished num)";

    private final String DROP_TABLE_THREAD_INFO = "drop table if exists thread_info";

    public SqliteHelper(Context context){
        this(context,DB_NAME,null,VERSION);
    }

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_THREAD_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_THREAD_INFO);
        db.execSQL(CREATE_TABLE_THREAD_INFO);
    }
}
