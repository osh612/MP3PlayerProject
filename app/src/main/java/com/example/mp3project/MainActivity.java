package com.example.mp3project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int HOME = 1, HEART = 2;
    private BottomNavigationView bottomNavigationView;
    private ConstraintLayout dragView;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    private TextView title, Artist, startTime, songTime, tvTitle2 ,tvSingerName2;
    private ImageButton ibSongHeart, previous, play, next;
    private ImageView album, album3;
    private SeekBar seekBar;

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    private FragmentPlaylist fragmentPlaylist;
    private FragmentLikedPlaylist fragmentHeart;
    //-----------------------------------------------------------

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<>();
    private ArrayList<MusicData> musicLikeArrayList = new ArrayList<>();

    //-----------------------------------------------------------

    private MusicDB musicDB;
    private MusicData musicData;

    //-----------------------------------------------------------

    private long backTime = 0L;
    private boolean nowPlaying = false;
    private int index;

    public MainActivity() {
    }

    //????????? ????????? ???
    @Override
    protected void onPause() {
        super.onPause();
    }

    //????????? ???????????? ?????? ???????????? ???
    @Override
    protected void onResume() {
        super.onResume();
    }

    //?????? ????????? ???
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long gapTime = currentTime - backTime;

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); //to close

        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            if (gapTime >= 0 && gapTime <= 2000) {
                super.onBackPressed();
                toastMessage("?????? ??????");
            } else {
                backTime = currentTime;
                toastMessage("???????????? ??? ??? ????????? ?????? ??????");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //?????? ????????????
        findViewByIdFunc();

        //?????? ????????????
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);

        //DB????????????
        musicDB = MusicDB.getInstance(MainActivity.this);

        musicDataArrayList = musicDB.compareArrayList();
        musicLikeArrayList = musicDB.saveLikeList();

        musicDB.insertQuery(musicDataArrayList);

        //??????????????? ?????? ?????????
        changFragment();

        //????????? ????????????
        eventHandlerFunc();

        title.setSelected(true);
        Artist.setSelected(true);
        tvTitle2.setSelected(true);
        tvSingerName2.setSelected(true);

        //???????????? ?????? ??????
        slidingUpPanelColorControl();
    }

    private void slidingUpPanelColorControl() {
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                album3.setAlpha(1-slideOffset);
                tvTitle2.setAlpha(1-slideOffset);
                tvSingerName2.setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
    }

    private void findViewByIdFunc() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        slidingUpPanelLayout = findViewById(R.id.slidingUpPanelLayout);


        fragmentPlaylist = new FragmentPlaylist();
        fragmentHeart = new FragmentLikedPlaylist();

        ibSongHeart = findViewById(R.id.ibSongHeart);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        album = findViewById(R.id.album);
        album3 = findViewById(R.id.album3);
        seekBar = findViewById(R.id.seekbar);
        title = findViewById(R.id.title);
        startTime = findViewById(R.id.startTime);
        songTime = findViewById(R.id.songTime);
        Artist = findViewById(R.id.Artist);
        dragView = findViewById(R.id.dragView);
        tvTitle2 = findViewById(R.id.tvTitle2);
        tvSingerName2 = findViewById(R.id.tvSingerName2);
    }

    //??????????????? ???????????? ?????? ?????????

    @SuppressLint("NonConstantResourceId")
    public void changFragment() {

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainPlaylist:
                    setFragmentChange(HOME);
                    break;
                case R.id.likedPlaylist:
                    setFragmentChange(HEART);
                    break;
                default:
                    Log.d("MainActivity", "menuBar error");
                    break;
            }

            return true;
        });
        setFragmentChange(HOME);

    }

    //?????? ????????? ?????? ????????? ??????
    public void setFragmentChange(int i) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (i) {

            case HOME:
                ft.replace(R.id.frameLayout, fragmentPlaylist);
                ft.commit();
                break;

            case HEART:
                ft.replace(R.id.frameLayout, fragmentHeart);
                ft.commit();
                break;
        }
    }



    //????????? ?????? ??????
    private void eventHandlerFunc() {

        play.setOnClickListener(view -> {
            if (nowPlaying) {

                nowPlaying = false;
                mediaPlayer.pause();
                play.setImageResource(R.drawable.play);

            } else {

                nowPlaying = true;
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                setSeekBarThread();
            }
        });

        previous.setOnClickListener(view -> {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
            int nowDurationForSec = Integer.parseInt(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));

            mediaPlayer.stop();
            mediaPlayer.reset();
            nowPlaying = false;

            play.setImageResource(R.drawable.play);

            try {
                if (nowDurationForSec <= 5) {
                    if (index == 0) {
                        index = musicDataArrayList.size() - 1;
                        setPlayerData(index, true);
                    } else {
                        index--;
                        setPlayerData(index, true);
                    }
                } else {
                    setPlayerData(index, true);
                }
            } catch (Exception e) {
                Log.d("Prev", e.getMessage());
            }
        });

        next.setOnClickListener(view -> {

            mediaPlayer.stop();
            mediaPlayer.reset();
            nowPlaying = false;
            play.setImageResource(R.drawable.play);

            try {
                if (index == musicDataArrayList.size() - 1) {
                    index = 0;
                    setPlayerData(index, true);
                } else {
                    index++;
                    setPlayerData(index, true);
                }
            } catch (Exception e) {
                Log.d("Next", e.getMessage());
            }
        });

        ibSongHeart.setOnClickListener(view -> {
            try {
                if (musicData.getLiked() == 1) {

                    musicData.setLiked(0);
                    musicLikeArrayList.remove(musicData);
                    ibSongHeart.setImageResource(R.drawable.like);

                } else {

                    musicData.setLiked(1);
                    musicLikeArrayList.add(musicData);
                    ibSongHeart.setImageResource(R.drawable.heart);

                }
                musicDB.updateQuery(musicDataArrayList);

            } catch (Exception e) {
                toastMessage("??? ?????? ??????");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // ????????? ?????????, seekbar ??????
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //????????????????????? ????????? ????????????
    public void setPlayerData(int position, boolean flag) {

        index = position;

        mediaPlayer.stop();
        mediaPlayer.reset();

        MusicAdapter musicAdapter = new MusicAdapter(MainActivity.this);

        if (flag) {
            musicData = musicDataArrayList.get(position);

        } else {
            musicData = musicLikeArrayList.get(position);
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

        title.setText(musicData.getTitle());
        tvTitle2.setText(musicData.getTitle());
        Artist.setText(musicData.getArtists());
        tvSingerName2.setText(musicData.getArtists());
        startTime.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));

        ibSongHeart.setActivated(musicData.getLiked() == 1);

        if (musicData.getLiked() == 1) {

            ibSongHeart.setImageResource(R.drawable.heart);
        } else {
            ibSongHeart.setImageResource(R.drawable.like);
        }

        // ?????? ????????? ??????
        Bitmap bitmap = musicAdapter.getAlbumImg(this, Long.parseLong(musicData.getAlbumArt()), 2500);
        if (bitmap != null) {

            album.setImageBitmap(bitmap);
            album3.setImageBitmap(bitmap);

            Palette.from(bitmap).generate(palette -> {

                if (palette == null) return;
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                if (vibrantSwatch != null) {
                    dragView.setBackgroundColor(vibrantSwatch.getRgb());
                }
            });

        } else {
            album.setImageResource(R.drawable.itunes);
        }

        // ?????? ??????




        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
        try {
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            play.setImageResource(R.drawable.pause);

            nowPlaying = true;
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            play.setActivated(true);

            setSeekBarThread();



            // ???????????? ?????????
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                musicData.setCount(musicData.getCount() + 1);
                next.callOnClick();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //????????? ????????? ????????????
    private void setSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {

                //???????????? ?????? ????????? ???????????? ???????????????
                seekBar.setMax(mediaPlayer.getDuration());

                while (mediaPlayer.isPlaying()) {

                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    runOnUiThread(() -> {

                        startTime.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                        songTime.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
                    });
                    SystemClock.sleep(10);
                }
            }
        });
        thread.start();
    }


    //????????? ????????? ?????? ????????????
    public ArrayList<MusicData> getLikeList() {

        musicLikeArrayList = musicDB.saveLikeList();

        if (musicLikeArrayList.isEmpty()) {
            toastMessage("????????? ????????? ???????????? ??????");
        } else {
            toastMessage("????????? ????????? ???????????? ??????");
        }

        return musicLikeArrayList;
    }

    //?????? ??????
    public void toastMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}