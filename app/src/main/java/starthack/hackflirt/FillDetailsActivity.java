package starthack.hackflirt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FillDetailsActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private String gender;
    private String preference;
    private EditText city;
    private DatabaseReference mDatabase;
    private EditText age;
    private User user;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);
        user = getIntent().getParcelableExtra("user");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        age = (EditText) findViewById(R.id.age);
        city = (EditText) findViewById(R.id.city);
        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });
    }

    private void getImageFromAlbum() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                Bitmap orientedImage = BlurBuilder.modifyOrientation(selectedImage, imageUri.getPath());
                Bitmap blur = BlurBuilder.blur(this, selectedImage);
                imageView.setImageBitmap(blur);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "You haven't picked any image", Toast.LENGTH_LONG).show();
        }
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
        String cityString = city.getText().toString();
        if (gender == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Gender was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        } else if (preference == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Preferences was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        } else if (ageString.trim().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Age was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        } else if (cityString.trim().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "City was not introduced", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            mDatabase.child("user").child(user.getUid()).child("gender").setValue(gender);
            mDatabase.child("user").child(user.getUid()).child("preference").setValue(preference);
            mDatabase.child("user").child(user.getUid()).child("age").setValue(ageString);
            mDatabase.child("user").child(user.getUid()).child("city").setValue(cityString);
            mDatabase.child("user").child(user.getUid()).child("status").setValue("pending_recording");
            user.setAge(Integer.valueOf(age.getText().toString()));
            user.setCity(cityString);
            user.setGender(gender);
            user.setPreference(preference);
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

}
