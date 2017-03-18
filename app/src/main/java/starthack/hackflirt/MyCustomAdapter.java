package starthack.hackflirt;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {
    private ArrayList<String> list = new ArrayList<>();
    private Map<Integer, String> recordedFiles;

    private MediaRecorder mRecorder;

    public MyCustomAdapter(List<String> sentences, Map<Integer, String> recordedFiles) {
        list.addAll(sentences);
        this.recordedFiles = recordedFiles;
    }

    @Override
    public MyCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView sentence = (TextView) holder.view.findViewById(R.id.list_item_string);
        sentence.setText(list.get(position));

        final ImageButton playImage = (ImageButton) holder.view.findViewById(R.id.play);
        ImageButton recordImage = (ImageButton) holder.view.findViewById(R.id.record);
        recordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording(position);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    playImage.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        playImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("PLAY","Pressed play button");

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(recordedFiles.get(position));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {}
                }
                return false;
            }
        });
    }

    private void startRecording(int position) {
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/tmp_audio_" + position + ".aac";
        recordedFiles.put(position, mFileName);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
        } catch (IOException e) {}

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }

    }

}