package starthack.hackflirt;

import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RecordActivity extends AppCompatActivity {

    private TextView mRecordLabel;

    private User user;

    private static final String LOG_TAG = "Record_log";

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        user = getIntent().getParcelableExtra("user");

        Set<String> sentences = SentenceGenerator.generateSentences(user);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Map<Integer, String> recordedFiles = new HashMap<>();
        MyCustomAdapter adapter = new MyCustomAdapter(sentences, recordedFiles);
        recyclerView.setAdapter(adapter);

        mStorage = FirebaseStorage.getInstance().getReference();
    }

    private void uploadAudio() {
//        UUID uuid = new UUID(64, 64);
//        String newFileName = uuid.randomUUID().toString() + ".aac";
//        StorageReference filepath = mStorage.child("audio").child(newFileName);
//        Uri uri = Uri.fromFile(new File(mFileName));
//        filepath.putFile(uri);

        //mDatabase.child("user").child(uid).child("audio").child(newFileName).setValue(sentence);
    }
}
