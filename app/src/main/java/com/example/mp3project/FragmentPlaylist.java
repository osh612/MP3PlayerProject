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

public class FragmentPlaylist extends Fragment {

    private MusicAdapter musicAdapter;
    private RecyclerView recyclePlaylist;
    private MainActivity mainActivity;

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

        View view = inflater.inflate(R.layout.activity_fragment_playlist, container, false);

        //객체 찾는 함수
        findViewByIdFunc(view);

        //MP3 파일 관리
        MusicDB musicDB = MusicDB.getInstance(mainActivity.getApplicationContext());
        ArrayList<MusicData> musicDataArrayList = musicDB.findContentProvMp3List();

        //어뎁터 만들기
        assert container != null;
        makeAdapter(container);

        //음악리스트 가져오기
        musicDataArrayList = musicDB.compareArrayList();
        //음악DB저장
        musicDB.insertQuery(musicDataArrayList);
        //어뎁터에 데이터 세팅하기
        settingAdapterDataList(musicDataArrayList);

        //
        eventHandler();

        return view;
    }


    //객체 찾아오기
    public void findViewByIdFunc(View view) {
        recyclePlaylist = view.findViewById(R.id.recycleHome);
    }


    //어뎁터 만들기
    public void makeAdapter(ViewGroup container) {

        musicAdapter = new MusicAdapter(container.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());

        recyclePlaylist.setLayoutManager(linearLayoutManager);
        recyclePlaylist.setAdapter(musicAdapter);

    }

    //어뎁터에 데이터 세팅하기
    @SuppressLint("NotifyDataSetChanged")
    public void settingAdapterDataList(ArrayList<MusicData> musicDataArrayList) {

        musicAdapter.setMusicList(musicDataArrayList);

        recyclePlaylist.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();

    }

    //리스트 클릭 시에 플레이 정보 가져오기

    public void eventHandler() {
        musicAdapter.setOnItemClickListener((view, position) -> mainActivity.setPlayerData(position, true));
    }


}
