package cn.hzyc.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.hzyc.im.R;
import cn.hzyc.im.po.MyAudio;
import cn.hzyc.im.util.ReadDataFromContentProvider;

public class MyMusicActivity extends Activity {

    private ListView listview;
    private List<MyAudio> data;
    private BaseAdapter adapter ;
    private RelativeLayout root ;

    private ImageView back, syq, xyq, bf;
    private TextView startTime, endTime, musicName;
    private SeekBar seekBar;
    private Timer timer;
    private TimerTask timerTask;
    private MediaPlayer mediaPlayer;
    Handler handler;

    private  String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);
        root = (RelativeLayout) findViewById(R.id.root) ;
        data = ReadDataFromContentProvider.readAudio(this) ;
        if(null == data || data.size() == 0) {
            Toast.makeText(this, "没有扫描到音频文件!", Toast.LENGTH_LONG).show() ;
            return ;
        }
        listview = (ListView) findViewById(R.id.listview) ;
        adapter = new AudioAdapter(this, data) ;
        listview.setAdapter(adapter) ;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 获取音乐路径
                path=data.get(arg2).getPath();
                tingzhi();
                replay();
            }
        }) ;

        ReadDataFromContentProvider.readImage(this) ;
        back = (ImageView) findViewById(R.id.back);
        syq = (ImageView) findViewById(R.id.syq);
        xyq = (ImageView) findViewById(R.id.xyq);
        bf = (ImageView) findViewById(R.id.bf);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        musicName = (TextView) findViewById(R.id.musicName);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {

                Bundle bundle=msg.getData();
                int start=Integer.parseInt(bundle.getString("startTime"))/1000;
                int end=Integer.parseInt(bundle.getString("endTime"))/1000;
                startTime.setText((start/60)+":"+(start%60));
                endTime.setText((end/60)+":"+(end%60));
                super.handleMessage(msg);
            }
        };


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
    }

    public void check(View view) {

        switch (view.getId()) {
            case R.id.bf:
                play();
                break;
        }
    }

    public void play() {
        if (mediaPlayer == null) {

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            bf.setImageResource(R.drawable.btn_playback_pause);
            seekBar.setMax(mediaPlayer.getDuration());
            // endTime.setText(mediaPlayer.getDuration());
            //seekBar.setProgress();

            timer = new Timer();

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    Message msg=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("endTime",mediaPlayer.getDuration()+"");
                    bundle.putString("startTime",mediaPlayer.getCurrentPosition()+"");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    if (mediaPlayer.getDuration()-1==mediaPlayer.getCurrentPosition()) {

                        timer.cancel();
                        Toast.makeText(MyMusicActivity.this,mediaPlayer.getDuration(),Toast.LENGTH_LONG).show();
                        tingzhi();

                    }

                }
            };

            timer.schedule(timerTask, 0, 100);
            return;
        }
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            bf.setImageResource(R.drawable.btn_playback_pause);
            timer = new Timer();

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    Message msg=new Message();

                    Bundle bundle=new Bundle();
                    bundle.putString("endTime",mediaPlayer.getDuration()+"");
                    bundle.putString("startTime",mediaPlayer.getCurrentPosition()+"");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    if (mediaPlayer.getDuration()-1==mediaPlayer.getCurrentPosition()) {
                        timer.cancel();
                        tingzhi();
                    }
                }
            };

            timer.schedule(timerTask, 0, 100);
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            timer.cancel();
            mediaPlayer.pause();
            bf.setImageResource(R.drawable.btn_playback_play);

            return;
        }

    }

    public void replay(){
          play();
    }
    public void tingzhi() {

        if (mediaPlayer != null) {

            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("endTime",0+"");
            bundle.putString("startTime",0+"");
            msg.setData(bundle);
            bf.setImageResource(R.drawable.btn_playback_play);
            handler.sendMessage(msg);
            seekBar.setProgress(0);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    class AudioAdapter extends BaseAdapter {
        private Context context ;
        private List<MyAudio> data ;

        public AudioAdapter(Context context, List<MyAudio> data) {
            super();
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size() ;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view ;
            if(null != convertView) {
                view = convertView ;
            }
            else {
                view = LayoutInflater.from(context).inflate(R.layout.item_mymusic, null) ;
            }
            TextView tv1 = (TextView) view.findViewById(R.id.text1) ;
            tv1.setText(data.get(position).getTitle()) ;
            TextView tv2 = (TextView) view.findViewById(R.id.text2) ;
            tv2.setText(data.get(position).getArtist() + "\t" + data.get(position).getAlbum()) ;
            return view;
        }

    }

}
