package com.example.app2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int curIndex;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> music_list = new ArrayList<>();

    SeekBar seekBar;
    TextView curMusic;
    TextView seekBarHint;
    Button btn_last;
    Button btn_playOrPause;
    Button btn_stop;
    Button btn_next;
    ExoPlayer player;
    Timer timer;
    boolean prepared;

    class ProgressUpdate extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(() -> {
                long position = player.getContentPosition();
                seekBar.setProgress((int) position);
                seekBarHint.setText(format(position));
            });
        }
    }

    public String format(long position) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String timeStr = sdf.format(position);
        return timeStr;
    }

    public void initListView() {
        listView = (ListView) findViewById(R.id.lv_music);
        music_list = getMusic_list();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                music_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listViewListener);
    }

    public void initMusicView() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(0);
        curMusic = (TextView) findViewById(R.id.curMusic);
        seekBarHint = (TextView) findViewById(R.id.seekBarHint);
        btn_last = (Button) findViewById(R.id.btn_last);
        btn_playOrPause = (Button) findViewById(R.id.btn_playOrPause);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_next = (Button) findViewById(R.id.btn_next);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        btn_stop.setOnClickListener(btnListener);
        btn_next.setOnClickListener(btnListener);
        btn_last.setOnClickListener(btnListener);
        btn_playOrPause.setOnClickListener(btnListener);
    }

    public void initExoPlayer() {
        player = new ExoPlayer.Builder(MainActivity.this).build();
        for (String music : music_list) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("asset:///music/" + music));
            player.addMediaItem(mediaItem);
        }
        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
        player.addListener(playerListener);
    }

    Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            Player.Listener.super.onMediaItemTransition(mediaItem, reason);
            if (timer != null) {
                timer.cancel();
            }
            if (player.getPlaybackState() != ExoPlayer.STATE_READY) {
                player.prepare();
                prepared = true;
                player.play();
            }
            Log.d("myflag0", "State:" + player.getPlaybackState());
            long realDurationMillis = player.getDuration();
            if (realDurationMillis > 0) seekBar.setMax((int) realDurationMillis);
            curIndex = player.getCurrentMediaItemIndex();
            curMusic.setText("正在播放：" + music_list.get(curIndex));
            clearHighlight();
            setCurrentHighlight();
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 300, 500);
            Log.d("myflag0", "" + getResources().getColor(android.R.color.transparent));
            Log.d("myflag0", "" + getResources().getColor(R.color.purple_200));
            Log.d("myflag", "first" + realDurationMillis);
        }
        //seekBar最大值在两个回调都做设定原因：首先不能单独在onMediaItemTransition()设置是因为该回调在切歌时
        // 只调一次，并且可能在未完成缓存期间调用,那么就会得到-9223372036854775807的无效歌曲长度，
        // 而onPlaybackStateChanged()该回调在缓存前调用，状态由缓存转变播放也会触发回调，所以无论如何最后一次都会
        // 得到正确值并覆盖无效值。其次不能单独在onPlaybackStateChanged()设置是因为，有种特殊情况是当播放列表
        // 播放完当前歌曲自然切到下一首歌曲时,播放状态保持ready不会改变，所以该回调并不会执行，那么这种情况下seekbar
        // 最大长度不能得到更新，但回调1是只要资源改变都会调用，同时也正因为处于这种保证状态ready的情况下，所以回调1在
        // 自然切换情景下获得的值也必然是有效的
        @Override
        public void onPlaybackStateChanged(int playbackState) { //播放列表自然切换时状态不变，不会回调
            Player.Listener.super.onPlaybackStateChanged(playbackState);
            if (!prepared) return;
            long realDurationMillis = player.getDuration();
            if (realDurationMillis > 0) seekBar.setMax((int) realDurationMillis);
            Log.d("myflag", "second" + realDurationMillis);
        }
    };

    public void clearHighlight() {
        for (int i = 0; i < music_list.size(); ++i) {
            ((TextView) listView.getChildAt(i)).setBackgroundColor(0);
        }
    }

    public void setCurrentHighlight() {
        ((TextView) listView.getChildAt(curIndex)).setBackgroundColor(-4487428);
    }

    public List<String> getMusic_list()  {
        List<String> list = new ArrayList<>();
        try {
            String[] musicNames = getAssets().list("music");
            list.addAll(Arrays.asList(musicNames));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    AdapterView.OnItemClickListener listViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!prepared) {
                player.prepare();
                prepared = true;
            }
            curIndex = position;
            player.seekTo(position, C.POSITION_UNSET);
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (prepared && fromUser) {
                player.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBarHint.setText(format(seekBar.getProgress()));
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!prepared) return;
            switch (v.getId()) {
                case R.id.btn_playOrPause:
                    if (player.isPlaying()) {
                        player.pause();
                        timer.cancel();
                        timer = new Timer();
                    } else {
                        player.play();
                        timer = new Timer();
                        timer.schedule(new ProgressUpdate(), 300, 500);
                    }
                    break;
                case R.id.btn_next:
                    player.seekToNextMediaItem();
                    break;
                case R.id.btn_last:
                    player.seekToPreviousMediaItem();
                    break;
                case R.id.btn_stop:
                    player.stop();
                    if (timer != null) timer.cancel();
                    seekBar.setProgress(0);
                    curMusic.setText("正在播放：");
                    seekBarHint.setText("00:00");
                    seekBar.setMax(0);
                    clearHighlight();
                    prepared = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Music");

        prepared = false;
        initListView();
        initMusicView();
        initExoPlayer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}