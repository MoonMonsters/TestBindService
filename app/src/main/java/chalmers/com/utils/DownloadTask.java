package chalmers.com.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import chalmers.com.bean.ThreadInfo;
import chalmers.com.db.DownloadDao;
import chalmers.com.db.IDownload;
import chalmers.com.interfaces.Config;

/**
 * Created by Chalmers on 2016-06-15 21:30.
 * email:qxinhai@yeah.net
 */
public class DownloadTask {

    IDownload dao = null;

    private String path = Environment.getExternalStorageDirectory().toString() + File.separatorChar
            + "downloads" + File.separatorChar;

    private boolean isDownloading = false;

    public DownloadTask(Context context){
        dao = new DownloadDao(context);
    }

    public void start(Context context, int position, ThreadInfo threadInfo){
        setDownloading(true);
        threadInfo = dao.query(threadInfo.getUrl());

        if(threadInfo.getFinished() == 0){
            dao.insert(threadInfo);
        }else if(threadInfo.getFinished() == threadInfo.getEnd()){
            Toast.makeText(context,"下载已经完成，请不要重复下载",Toast.LENGTH_SHORT).show();

            return;
        }

        new DownloadThread(context,position,threadInfo).start();
    }

    public void stop(){
        setDownloading(false);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    private class DownloadThread extends Thread{
        private Context context = null;
        private int position = 0;
        private ThreadInfo threadInfo = null;

        public DownloadThread(Context context, int position, ThreadInfo threadInfo){
            this.context = context;
            this.position = position;
            this.threadInfo = threadInfo;
        }


        @Override
        public void run() {
            super.run();
            try{
                URL url = new URL(threadInfo.getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                long totle = threadInfo.getStart() + threadInfo.getFinished();
                connection.setRequestProperty("range","bytes="+totle+"-"+threadInfo.getEnd());
                if(connection.getResponseCode() >= HttpURLConnection.HTTP_OK){
                    File file = new File(path);
                    if(!file.exists()){
                        file.mkdir();
                    }
                    File file2 = new File(file,threadInfo.getFilename());
                    RandomAccessFile raf = new RandomAccessFile(file2,"rwd");
                    raf.seek(totle);
                    raf.setLength(threadInfo.getEnd());

                    InputStream is = connection.getInputStream();
                    byte[] buf = new byte[2048];
                    int size = -1;
                    long time = System.currentTimeMillis();
                    while(isDownloading && (size = is.read(buf)) != -1){
                        raf.write(buf,0,size);
                        totle += size;

                        //每隔500毫秒发送一次广播（更新一次界面）
                        if (System.currentTimeMillis() - time >= 500) {
                            sendBroadcastToMainUI(totle);
                            time = System.currentTimeMillis();
                        }
                    }
                    //在下载完成后再次发送一次广播
                    sendBroadcastToMainUI(totle);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * 发送广播
         * @param totle 携带的数据
         */
        private void sendBroadcastToMainUI(long totle){
            Intent intent = new Intent();
            intent.setAction(Config.ACTION_UPDATE);
            intent.putExtra(Config.POSITION,position);
            intent.putExtra(Config.PROGRESS_BAR,totle);
            //更新数据库数据
            dao.update(threadInfo.getUrl(),totle);
            context.sendBroadcast(intent);
        }
    }
}
