package starthack.hackflirt;

import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public static final String YOU_HAVE_TO_RECORD_ALL_SENTENCES = "You have to record all sentences";
    private TextView mRecordLabel;

    private User user;

    private static final String LOG_TAG = "Record_log";

    private StorageReference mStorage;
    private Map<Integer, String> recordedFiles;
    private DatabaseReference mDatabase;
    private List<String> sentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = getIntent().getParcelableExtra("user");

        sentences = SentenceGenerator.generateSentences(user);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recordedFiles = new HashMap<>();
        MyCustomAdapter adapter = new MyCustomAdapter(sentences, recordedFiles);
        recyclerView.setAdapter(adapter);

        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public void uploadAudios(View view) {
        if(sentences.size() == recordedFiles.size()) {
            for (Map.Entry<Integer, String> entry : recordedFiles.entrySet()) {
                UUID uuid = new UUID(64, 64);
                String newFileCode = uuid.randomUUID().toString();
                String newFilePath = newFileCode + ".aac";
                StorageReference filepath = mStorage.child("audio").child(newFilePath);
                Uri uri = Uri.fromFile(new File(entry.getValue()));
                filepath.putFile(uri);
                mDatabase.child("user").child(user.getUid()).child("audio").child(newFileCode).setValue(sentences.get(entry.getKey()));
            }
            mDatabase.child("user").child(user.getUid()).child("status").setValue("complete");


        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_upload_record_msg), Toast.LENGTH_LONG);
        }
    }
}
