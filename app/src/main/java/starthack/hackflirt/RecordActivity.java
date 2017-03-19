package starthack.hackflirt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecordActivity extends AppCompatActivity {
    private User user;

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
                DatabaseReference audioReference = mDatabase.child("user").child(user.getUid()).child("audio").child(newFileCode);
                audioReference.child("sentence").setValue(sentences.get(entry.getKey()));
                audioReference.child("negScore").setValue(0);
            }
            mDatabase.child("user").child(user.getUid()).child("status").setValue("complete");

            Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
            intent.putExtra("user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_upload_record_msg), Toast.LENGTH_LONG).show();
        }
    }
}
