package chalmers.com.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import chalmers.com.bean.ThreadInfo;
import chalmers.com.utils.DownloadTask;

public class DownloadService extends Service {
    private DownloadTask task = null;

    @Override
    public IBinder onBind(Intent intent) {

        return new MyBind();
    }

    public class MyBind extends Binder{
        public DownloadService getBinder(){
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void start(Context context, int position, ThreadInfo threadInfo){
        task = new DownloadTask(context);
        task.start(context,position,threadInfo);
    }

    public void stop(){
        task.stop();
    }
}
