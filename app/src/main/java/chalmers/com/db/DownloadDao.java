package chalmers.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import chalmers.com.bean.ThreadInfo;

/**
 * Created by Chalmers on 2016-06-15 21:18.
 * email:qxinhai@yeah.net
 */
public class DownloadDao implements IDownload{

    SqliteHelper helper = null;
    public DownloadDao(Context context){
        helper = new SqliteHelper(context);
    }

    @Override
    public void insert(ThreadInfo threadInfo) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url",threadInfo.getUrl());
        values.put("filename",threadInfo.getFilename());
        values.put("start",threadInfo.getStart());
        values.put("end",threadInfo.getEnd());
        values.put("finished",threadInfo.getFinished());

        writableDatabase.insert("thread_info",null,values);

        writableDatabase.close();
    }

    @Override
    public void update(String url, long finished) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("finished",finished);

        writableDatabase.update("thread_info",values,"url=?",new String[]{url});

        writableDatabase.close();
    }

    @Override
    public ThreadInfo query(String url) {
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("thread_info", null, "url=?", new String[]{url}, null, null, null);

        ThreadInfo threadInfo = new ThreadInfo();
        if(cursor.moveToNext()){
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setFinished(cursor.getLong(cursor.getColumnIndex("finished")));
            threadInfo.setStart(cursor.getLong(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            threadInfo.setFilename(cursor.getString(cursor.getColumnIndex("filename")));
        }

        cursor.close();
        readableDatabase.close();

        return threadInfo;
    }
}
