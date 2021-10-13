package com.example.mp3project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicDB extends SQLiteOpenHelper {
    private static final String DATABASE = "musicDB";
    private static final int VERSION = 1;
    private final Context context;

    @SuppressLint("StaticFieldLeak")
    private static MusicDB musicDB;

    private MusicDB(@Nullable Context context) {
        super(context, DATABASE, null, VERSION);
        this.context = context;
    }

    public static MusicDB getInstance(Context context) {
        if (musicDB == null) {
            musicDB = new MusicDB(context);
        }
        return musicDB;
    }

    //테이블 생성함수
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table if not exists mp3TBL(" +
                        "id varchar(20) primary key," +
                        "artists varchar(20)," +
                        "title varchar(50)," +
                        "albumArt varchar(30)," +
                        "duration varchar(20)," +
                        "count integer," +
                        "liked integer);");
    }

    //테이블 업데이트 함수
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists mp3TBL");
        onCreate(sqLiteDatabase);
    }

    //테이블 출력 함수
    public ArrayList<MusicData> selectMusicTBL() {

        ArrayList<MusicData> musicDBlist = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select*from mp3TBL;", null);
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            musicDBlist.add(musicData);
        }

        cursor.close();

        return musicDBlist;
    }

    // 데이터 삽입하기
    public void insertQuery(ArrayList<MusicData> musicDataArrayList) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {
                ArrayList<MusicData> dataArrayList = selectMusicTBL();

                if (!dataArrayList.contains(musicData)) {

                    String query = "insert into mp3TBL values(" +
                            "'" + musicData.getId() + "'," +
                            "'" + musicData.getArtists() + "'," +
                            "'" + musicData.getTitle() + "'," +
                            "'" + musicData.getAlbumArt() + "'," +
                            "'" + musicData.getDuration() + "',"
                            + 0 + "," + 0 + ");";

                    sqLiteDatabase.execSQL(query);
                }
            }

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
        }
    }


    //데이터 업데이트 하기
    public void updateQuery(ArrayList<MusicData> musicDataArrayList) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {

                String query = "update mp3TBL set count = " + musicData.getCount() + ", liked = " + musicData.getLiked() + "  where id ='" + musicData.getId() + "';";
                sqLiteDatabase.execSQL(query);
            }

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");

        }
    }

    //파일을 저장하고 관리해준는 구문.
    public ArrayList<MusicData> findContentProvMp3List() {
        ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

        String[] musicData = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};


        assert context != null;
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                musicData, null, null, musicData[2] + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(musicData[0]));
                String artists = cursor.getString(cursor.getColumnIndexOrThrow(musicData[1]));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(musicData[2]));
                String albumArt = cursor.getString(cursor.getColumnIndexOrThrow(musicData[3]));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(musicData[4]));

                MusicData mData = new MusicData(id, artists, title, albumArt, duration, 0, 0);
                musicDataArrayList.add(mData);
            }
        }
        return musicDataArrayList;
    }

    //비교함수
    public ArrayList<MusicData> compareArrayList() {
        ArrayList<MusicData> sdCardList = findContentProvMp3List();
        ArrayList<MusicData> dbList = selectMusicTBL();

        if (dbList.isEmpty()) {
            return sdCardList;
        }
        if (dbList.containsAll(sdCardList)){
            return dbList;

        }

        int size = dbList.size();
        for (int i = 0; i < size; i++) {
            if (dbList.contains(sdCardList.get(i))) {
                continue;
            }
            dbList.add(sdCardList.get(i));
            ++size;
        }
        return dbList;
    }

    //좋아요 리스트 출력함수
    public ArrayList<MusicData> saveLikeList() {
        ArrayList<MusicData> likeMusicList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select*from mp3TBL where liked = 1;", null);
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            likeMusicList.add(musicData);
        }
        cursor.close();

        return likeMusicList;
    }

}
