package com.example.mp3project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class FragmentLikedPlaylist extends Fragment {

    private MusicAdapter musicAdapter;
    private RecyclerView recycleLikedPlaylist;
    private MainActivity mainActivity;


    public FragmentLikedPlaylist() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragment_likedplaylist, container, false);

        //객체 찾아오기
        findViewByIdFunc(view);

        //MP3 파일 관리
        MusicDB musicDB = MusicDB.getInstance(mainActivity.getApplicationContext());
        ArrayList<MusicData> musicDataArrayList = musicDB.saveLikeList();

        //어뎁터 만들기
        assert container != null;
        makeAdapter(container);

        //음악리스트 가져오기
        musicDataArrayList = musicDB.compareArrayList();

        //음악DB저장
        musicDB.insertQuery(musicDataArrayList);

        //어뎁터에 데이터 세팅하기
        settingAdapterDataList(mainActivity.getLikeList());

        //이벤트 처리하기
        eventHandler();

        return view;
    }


    // 객체 찾아오기
    public void findViewByIdFunc(View view) {
        recycleLikedPlaylist = view.findViewById(R.id.recycleHeart);
    }


    //어뎁터 만들기
    public void makeAdapter(ViewGroup container) {

        musicAdapter = new MusicAdapter(container.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());

        recycleLikedPlaylist.setLayoutManager(linearLayoutManager);
        recycleLikedPlaylist.setAdapter(musicAdapter);
        recycleLikedPlaylist.setLayoutManager(linearLayoutManager);

    }

    //어뎁터에 데이터를 세팅하기
    @SuppressLint("NotifyDataSetChanged")
    public void settingAdapterDataList(ArrayList<MusicData> musicDataArrayList) {

        musicAdapter.setMusicList(musicDataArrayList);

        recycleLikedPlaylist.setAdapter(musicAdapter);

        musicAdapter.notifyDataSetChanged();

    }

    //리스트 클릭시에 뮤직플레이어 띄우기
    public void eventHandler() {
        musicAdapter.setOnItemClickListener((view, position) -> mainActivity.setPlayerData(position, false));
    }
}
