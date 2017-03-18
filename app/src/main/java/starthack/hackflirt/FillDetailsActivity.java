package starthack.hackflirt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FillDetailsActivity extends AppCompatActivity {

    private String gender;
    private String preference;
    private String uid;
    private DatabaseReference mDatabase;
    private EditText age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);
        uid = getIntent().getStringExtra("UID");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        age = (EditText) findViewById(R.id.age);

        final Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });
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

    public void submit() {

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
            mDatabase.child("user").child(uid).child("gender").setValue(gender);
            mDatabase.child("user").child(uid).child("preference").setValue(preference);
            mDatabase.child("user").child(uid).child("age").setValue(ageString);
            mDatabase.child("user").child(uid).child("status").setValue("pending_recording");
        }
    }

}
