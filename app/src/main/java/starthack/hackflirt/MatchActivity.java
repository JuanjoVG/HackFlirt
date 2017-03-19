package starthack.hackflirt;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private DatabaseReference mDatabase;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private User user;
    private Map<String, Object> proposedUsers = new HashMap<>();

    private RelativeLayout mProgressBar;
    private RecyclerView mRecyclerAudio;

    private FloatingActionButton mFabSkip;
    private FloatingActionButton mFabLike;
    private TextView mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        initViews();
        user = getIntent().getParcelableExtra("user");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String uid : td.keySet()) {
                    if (!uid.equals(user.getUid())) {
                        Map<String, Object> userMap = (Map<String, Object>) td.get(uid);
                        String gender = (String) userMap.get("gender");
                        String preference = (String) userMap.get("preference");
                        String status = (String) userMap.get("status");
                        if (isValidUser(gender, preference, status)) {
                            proposedUsers.put(uid, userMap);
                        }
                    }
                }
                mProgressBar.setVisibility(View.INVISIBLE);
                updateView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

//
//        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hackflirt.appspot.com");
//        StorageReference pathReference = storageRef.child("audio/8f088e92-c0f1-46eb-a37f-d0599b781ee9.aac");
//
//        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                try {
//                    // Download url of file
//                    final String url = uri.toString();
//                    mMediaPlayer.setDataSource(url);
//                    // wait for media player to get prepare
//                    mMediaPlayer.setOnPreparedListener(MatchActivity.this);
//                    mMediaPlayer.prepareAsync();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i("TAG", e.getMessage());
//            }
//        });
    }

    private void initViews() {
        mProgressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        mUserInfo = (TextView) findViewById(R.id.user_info);
        mRecyclerAudio = (RecyclerView) findViewById(R.id.audio_list);

        mFabSkip = (FloatingActionButton) findViewById(R.id.fab_skip);
        mFabLike = (FloatingActionButton) findViewById(R.id.fab_like);
    }

    private void updateView() {
        if (proposedUsers.isEmpty()) {
            setVisibilityListAndButtons(View.INVISIBLE);
            mUserInfo.setText(R.string.no_suggestions);
        } else {
            String nextUid = (String) proposedUsers.keySet().toArray()[0];
            Map<String, Object> nextUser = (Map<String, Object>) proposedUsers.get(nextUid);
            proposedUsers.remove(nextUid);
            Map<String, Object> audiosMap = (Map<String, Object>) nextUser.get("audio");
            List<String> audios = new ArrayList<>();
            for (String audio : audiosMap.keySet()) {
                audios.add(audio);
            }

            setVisibilityListAndButtons(View.VISIBLE);

            String name = (String) nextUser.get("name");
            String age = (String) nextUser.get("age");
            String userInfo = name + ", " + age + " years";
            mUserInfo.setText(userInfo);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerAudio.setLayoutManager(layoutManager);

            AudioAdapter adapter = new AudioAdapter(SentenceGenerator.getSentenceSubject(), audios);
            mRecyclerAudio.setAdapter(adapter);
        }
    }

    private void setVisibilityListAndButtons(int visibility) {
        mRecyclerAudio.setVisibility(visibility);
        mFabLike.setVisibility(visibility);
        mFabSkip.setVisibility(visibility);
    }

    public void skip(View view) {
        updateView();
    }

    public void like(View view) {
        updateView();
    }

    private boolean isValidUser(String gender, String preference, String status) {
        if (!status.equals("complete")) return false;
        if (!matchGenderPreference(gender, user.getPreference())) return false;
        if (!matchGenderPreference(user.getGender(), preference)) return false;
        return true;
    }

    private boolean matchGenderPreference(String gender, String preference) {
        if (preference.equals("both")) return true;
        if (preference.equals(gender)) return true;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

}
