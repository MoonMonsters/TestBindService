package chalmers.com.adapter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import chalmers.com.bean.ThreadInfo;
import chalmers.com.service.DownloadService;
import chalmers.com.testbindservice.R;

/**
 * Created by Chalmers on 2016-06-15 18:44.
 * email:qxinhai@yeah.net
 */
public class FileAdapter extends BaseAdapter {

    private ArrayList<ThreadInfo> infoList = null;
    private Context context = null;

    public FileAdapter(Context context, ArrayList<ThreadInfo> infoList){
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.file_item,parent,false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(position);
        viewHolder.pb_finished.setProgress(transPercent(infoList.get(position).getEnd(),infoList.get(position).getFinished()));

        return convertView;
    }

    /**
     * 将long值按比例转换
     * @param end 最大值
     * @param finished 当前值
     * @return %
     */
    public int transPercent(long end, long finished){
        int t = (int) (100.0 / end * finished);
        return t;
    }

    private class ViewHolder{
        TextView tv_filename;
        Button btn_start;
        Button btn_stop;
        ProgressBar pb_finished;
        DownloadService service = null;

        public ViewHolder(View view){
            tv_filename = (TextView) view.findViewById(R.id.tv_filename);
            btn_start = (Button) view.findViewById(R.id.btn_start);
            btn_stop = (Button) view.findViewById(R.id.btn_stop);
            pb_finished = (ProgressBar) view.findViewById(R.id.pb_finished);

            pb_finished.setMax(100);
        }

        /**
         * 绑定第position项
         * @param position 位置
         */
        public void bindData(int position){
            tv_filename.setText(infoList.get(position).getFilename());

            initListener(position);
        }

        /**
         * 添加监听器
         * @param position 监听器位于的item项
         */
        private void initListener(int position){
            btn_start.setOnClickListener(new MyClickListener(position));
            btn_stop.setOnClickListener(new MyClickListener(position));
        }

        class MyClickListener implements View.OnClickListener{
            private int position = 0;
            public MyClickListener(int position){
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btn_start){

                    if(service == null){
                        /**
                         * 绑定服务
                         */
                        Intent intent = new Intent(context, DownloadService.class);
                        context.bindService(intent,connection, Service.BIND_AUTO_CREATE);

                        //暂停100毫秒，等待广播注册，服务绑定完成
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                service.start(context,position,infoList.get(position));
                            }
                        },100);
                    }
                }else if(v.getId() == R.id.btn_stop){
                    if(service != null){
                        service.stop();
                        //解绑服务
                        context.unbindService(connection);
                        service = null;
                    }
                }
            }
        }

        /**
         * 绑定服务所需要的连接
         */
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = ((DownloadService.MyBind)binder).getBinder();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }
        };
    }
}