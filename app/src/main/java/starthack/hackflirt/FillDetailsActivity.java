package starthack.hackflirt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FillDetailsActivity extends AppCompatActivity {

    private String gender;
    private String preference;
    private DatabaseReference mDatabase;
    private EditText age;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);
        user = getIntent().getParcelableExtra("user");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        age = (EditText) findViewById(R.id.age);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    gender = "male";
                break;
            case R.id.radio_female:
                if (checked)
                    gender = "female";
                break;
            case R.id.radio_males:
                if (checked)
                    preference = "male";
                break;
            case R.id.radio_females:
                if (checked)
                    preference = "female";
                break;
            case R.id.radio_both:
                if (checked)
                    preference = "both";
                break;
        }
    }

    public void submit(View v) {

        String ageString = age.getText().toString();
        if (gender == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Gender was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (preference == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Preferences was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (ageString.trim().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Age was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            mDatabase.child("user").child(user.getUid()).child("gender").setValue(gender);
            mDatabase.child("user").child(user.getUid()).child("preference").setValue(preference);
            mDatabase.child("user").child(user.getUid()).child("age").setValue(ageString);
            mDatabase.child("user").child(user.getUid()).child("status").setValue("pending_recording");
            user.setAge(Integer.valueOf(age.getText().toString()));
            user.setGender(gender);
            user.setPreference(preference);
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

}
