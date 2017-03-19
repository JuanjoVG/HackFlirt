package starthack.hackflirt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<String> sentences;
    private List<String> audios;

    public AudioAdapter(List<String> sentenceSubject, List<String> audios) {
        sentences = sentenceSubject;
        this.audios = audios;
    }

    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new AudioAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AudioAdapter.ViewHolder holder, int position) {
        TextView sentence = (TextView) holder.view.findViewById(R.id.list_audio_string);
        sentence.setText(sentences.get(position));

        final ImageButton playImage = (ImageButton) holder.view.findViewById(R.id.play_audio);

        playImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }

    }
}
