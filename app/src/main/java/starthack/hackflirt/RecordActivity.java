package starthack.hackflirt;

import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class RecordActivity extends AppCompatActivity {

    private TextView mRecordLabel;

    private MediaRecorder mRecorder;
    private String mFileName = null;
    private User user;

    private static final String LOG_TAG = "Record_log";

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        user = getIntent().getParcelableExtra("user");

        Set<String> sentences = SentenceGenerator.generateSentences(user);

        ListView sentencesList = (ListView) findViewById(R.id.sentencesList);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                sentences.toArray(new String[sentences.size()]));

        sentencesList.setAdapter(adapter);

        mStorage = FirebaseStorage.getInstance().getReference();

        mRecordLabel = (TextView) findViewById(R.id.recordLabel);
        Button mRecordButton = (Button) findViewById(R.id.recordButton);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/tmp_audio.aac";

        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    mRecordLabel.setText("Recording Started...");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    mRecordLabel.setText("Recording Stoped...");

                }
                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }

    private void uploadAudio() {
        UUID uuid = new UUID(64, 64);
        String newFileName = uuid.randomUUID().toString() + ".aac";
        StorageReference filepath = mStorage.child("audio").child(newFileName);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri);

        //mDatabase.child("user").child(uid).child("audio").child(newFileName).setValue(sentence);
    }
}
