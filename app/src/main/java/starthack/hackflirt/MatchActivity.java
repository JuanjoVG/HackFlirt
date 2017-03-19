package starthack.hackflirt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private DatabaseReference mDatabase;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private User user;
    private Map<String, Object> proposedUsers = new HashMap<>();
    private Map<Integer, Boolean> mapBadAudio;

    private RelativeLayout mProgressBar;

    private RecyclerView mRecyclerAudio;

    private FloatingActionButton mFabSkip;
    private FloatingActionButton mFabLike;
    private TextView mUserInfo;
    private ImageView userImage;

    private String nextUid;
    private Map<String, Object> nextUser;

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
    }

    private void initViews() {
        mProgressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        mUserInfo = (TextView) findViewById(R.id.user_info);
        mRecyclerAudio = (RecyclerView) findViewById(R.id.audio_list);

        mFabSkip = (FloatingActionButton) findViewById(R.id.fab_skip);
        mFabLike = (FloatingActionButton) findViewById(R.id.fab_like);

        userImage = (ImageView) findViewById(R.id.user_image);
    }

    private void updateView() {
        if (proposedUsers.isEmpty()) {
            setVisibilityListAndButtons(View.INVISIBLE);
            mUserInfo.setText(R.string.no_suggestions);
        } else {
            nextUid = (String) proposedUsers.keySet().toArray()[0];
            nextUser = (Map<String, Object>) proposedUsers.get(nextUid);

            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hackflirt.appspot.com");
            StorageReference pathReference = storageRef.child("image/" + nextUid);
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            pathReference.getFile(localFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                            Bitmap blur = BlurBuilder.blur(getApplicationContext(), bitmap);
                            userImage.setImageBitmap(blur);
                        }
                    });

            proposedUsers.remove(nextUid);
            Map<String, Object> audiosMap = (Map<String, Object>) nextUser.get("audio");
            List<String> audios = new ArrayList<>();
            for (String audio : audiosMap.keySet()) {
                audios.add(audio);
            }

            setVisibilityListAndButtons(View.VISIBLE);

            String name = (String) nextUser.get("name");
            String age = (String) nextUser.get("age");
            String userInfo = name + ", " + age;
            mUserInfo.setText(userInfo);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerAudio.setLayoutManager(layoutManager);

            mapBadAudio = new HashMap<>();
            AudioAdapter adapter = new AudioAdapter(SentenceGenerator.getSentenceSubject(), audios, mapBadAudio);
            mRecyclerAudio.setAdapter(adapter);
        }
    }

    private void setVisibilityListAndButtons(int visibility) {
        userImage.setVisibility(visibility);
        mRecyclerAudio.setVisibility(visibility);
        mFabLike.setVisibility(visibility);
        mFabSkip.setVisibility(visibility);
    }

    public void skip(View view) {
        updateAudioScore();
        updateView();
    }

    public void like(View view) {
        updateAudioScore();
        updateView();
    }

    private void updateAudioScore() {
        Map<String, Object> audiosMap = (Map<String, Object>) nextUser.get("audio");

        List<String> audios = new ArrayList<>();
        for (String audio : audiosMap.keySet()) {
            audios.add(audio);
        }

        for (String audio : audiosMap.keySet()) {
            int pos = audios.indexOf(audio);
            if (mapBadAudio.containsKey(pos) && mapBadAudio.get(pos)) {
                Map<String, Object> audioMap = (Map<String, Object>) audiosMap.get(audio);
                Long oldScore = (Long) audioMap.get("negScore");
                mDatabase.child("user").child(nextUid).child("audio").child(audio).child("negScore").setValue(oldScore + 1);
            }
        }
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
