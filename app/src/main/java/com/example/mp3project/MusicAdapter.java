package com.example.mp3project;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private final Context context;
    private ArrayList<MusicData> musicList;

    private OnItemClickListener mListener = null;

    public MusicAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {

        Bitmap albumImg = getAlbumImg(context, Long.parseLong(musicList.get(position).getAlbumArt()), 200);

        if (albumImg != null) {
            customViewHolder.albumArt.setImageBitmap(albumImg);
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        customViewHolder.title.setText(musicList.get(position).getTitle());
        customViewHolder.artist.setText(musicList.get(position).getArtists());
        customViewHolder.duration.setText(simpleDateFormat.format(Integer.parseInt(musicList.get(position).getDuration())));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // ???????????? ???????????? ??????
    public Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt);

        if (uri != null) {
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");

                //?????? true??? ???????????? ???????????? ????????? ???????????? ???????????? ??????.
                // fields??? ?????? ???????????? ????????? Load ????????? ???????????? ????????? ????????? ???????????? ????????? ??? ??????.
                options.inJustDecodeBounds = true;

                int scale = 0;
                if(options.outHeight > imgMaxSize || options.outWidth > imgMaxSize){
                    scale = (int)Math.pow(2,(int) Math.round(Math.log(imgMaxSize / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }

                options.inJustDecodeBounds = false; // true?????? ?????? ???????????? ????????? ?????????
                options.inSampleSize = scale; // ?????? ???????????? ????????? ???????????? ??????

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor(), null, options);

                if(bitmap != null){
                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;

            } catch (FileNotFoundException e) {
                Log.d("MusicAdapter", "????????? ????????? ??????");
            } finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        Log.d("MusicAdapter", "fd.close");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (musicList != null) ? (musicList.size()) : (0);
    }

    public interface OnItemClickListener{

        void onItemClick(View view, int position);
    }

    // OnItemClickListener ?????? ????????? ???????????? ???????????? ??????
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    //??????????????? ???????????? ?????????.
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView artist;
        private final TextView duration;
        private final ImageView albumArt;

        public CustomViewHolder(@NonNull View itemView) {

            super(itemView);
            albumArt = itemView.findViewById(R.id.imgAlbum);
            title = itemView.findViewById(R.id.tvTitle);
            artist = itemView.findViewById(R.id.tvSingerName);
            duration = itemView.findViewById(R.id.tvTime);

            title.setSelected(true);
            artist.setSelected(true);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){

                    mListener.onItemClick(view,position);


                }
            });
        }
    }

    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
        Log.d("musicList", musicList+"");
    }
}