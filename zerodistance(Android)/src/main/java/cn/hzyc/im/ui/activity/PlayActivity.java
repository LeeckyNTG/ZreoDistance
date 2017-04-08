package cn.hzyc.im.ui.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.hzyc.im.R;

public class PlayActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_play);


        path=getIntent().getStringExtra("path");
        Log.i("msg",path);
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
                    // startTime.setText("sdss");
                    if (mediaPlayer.getDuration()-1==mediaPlayer.getCurrentPosition()) {

                        timer.cancel();
                        Toast.makeText(PlayActivity.this,mediaPlayer.getDuration(),Toast.LENGTH_LONG).show();
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
                    // startTime.setText("sdss");
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
}
