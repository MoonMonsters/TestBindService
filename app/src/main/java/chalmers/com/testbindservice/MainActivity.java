package chalmers.com.testbindservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import chalmers.com.adapter.FileAdapter;
import chalmers.com.bean.ThreadInfo;
import chalmers.com.interfaces.Config;

public class MainActivity extends AppCompatActivity {

    String urls[] = new String[]{"http://www.imooc.com/mobile/imooc.apk",
            "http://www.imooc.com/download/Activator.exe",
            "http://s1.music.126.net/download/android/CloudMusic_3.4.1.133604_official.apk",
            "http://study.163.com/pub/study-android-official.apk"};

    private DownloadReceiver receiver = null;
    private ArrayList<ThreadInfo> infoList = null;
    private ListView lv_filelist = null;
    private FileAdapter fileAdapter = null;

    private final int MSG_UPDATE = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_UPDATE){
                fileAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 注册广播
         */
        receiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_UPDATE);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    private void initView(){
        lv_filelist = (ListView) findViewById(R.id.lv_filelist);
    }

    private void initData(){
        infoList = new ArrayList<>();
        for(String mUrl : urls){
            new DownloadThread(mUrl).start();
        }

        fileAdapter = new FileAdapter(this,infoList);
        lv_filelist.setAdapter(fileAdapter);
    }

    private String getFilenameFromUrl(String mUrl){
        int index = mUrl.lastIndexOf('/');
        String s = mUrl.substring(index + 1);

        return s;
    }

    //开启子线程获得下载数据长度
    private class DownloadThread extends Thread{
        private String mUrl = null;
        public DownloadThread(String mUrl){
            this.mUrl = mUrl;
        }

        @Override
        public void run() {
            super.run();
            try{
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);

                int code = conn.getResponseCode();
                if(code == HttpURLConnection.HTTP_OK){
                    int length = conn.getContentLength();

                    ThreadInfo threadInfo = new ThreadInfo();
                    threadInfo.setEnd(length);
                    threadInfo.setFilename(getFilenameFromUrl(mUrl));
                    threadInfo.setFinished(0);
                    threadInfo.setStart(0);
                    threadInfo.setUrl(mUrl);

                    infoList.add(threadInfo);

                    Message message = handler.obtainMessage();
                    message.what = MSG_UPDATE;
                    handler.sendMessage(message);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 在广播中改变progressBar的进度
     */
    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是更新广播
            if(intent.getAction().equals(Config.ACTION_UPDATE)){
                //获得当前值
                long finished = intent.getLongExtra(Config.PROGRESS_BAR, 0);
                Log.i("TAG","finished="+finished);
                //获得位置
                int position = intent.getIntExtra(Config.POSITION,0);
                //更新数据
                infoList.get(position).setFinished(finished);

                Message message = handler.obtainMessage();
                message.what = MSG_UPDATE;
                handler.sendMessage(message);
            }
        }
    }
}