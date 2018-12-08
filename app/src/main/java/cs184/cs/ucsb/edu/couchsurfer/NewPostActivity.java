package cs184.cs.ucsb.edu.couchsurfer;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_FILE = 20;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private int year, month, day;

    private Button dateButton, postButton, locationButton;
    private FloatingActionButton photoButton;
    private TextView titleTextView, dateTextView, locationTextView;
    private EditText priceEditText, descriptionEditText;
    private ImageView photoImageView;

    private Uri imageUri;
    private DatePickerDialogListener dateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        dateListener = new DatePickerDialogListener();

        dateButton = findViewById(R.id.dateButton);
        postButton = findViewById(R.id.postButton);
        photoButton = findViewById(R.id.photoButton);
        locationButton = findViewById(R.id.locationButton);
        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationTextView = findViewById(R.id.locationTextView);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        dateButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);


    }

    public void setDay(int day) { this.day = day; }

    public void setMonth(int month) { this.month = month; }

    public void setYear(int year) { this.year = year; }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dateButton:
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewPostActivity.this, dateListener,
                        NewPostActivity.this.year, NewPostActivity.this.month, NewPostActivity.this.day);
                datePickerDialog.show();
                break;
            case R.id.locationButton:
                try{
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) { }
                  catch (GooglePlayServicesNotAvailableException e) { }
                break;
            case R.id.photoButton:
                chooseImage();
                break;
            case R.id.postButton:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class DatePickerDialogListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            NewPostActivity.this.setYear(year);
            NewPostActivity.this.setMonth(monthOfYear);
            NewPostActivity.this.setDay(dayOfMonth);
            dateTextView.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
        }
    }

    private void chooseImage() {
        //Displays dialog to choose pic from camera or gallery
        final CharSequence[] items = {"Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_FILE) {
            if (resultCode == this.RESULT_OK && data != null) {
                // the address of the image on the SD Card.
                imageUri = data.getData();
                if (requestCode == SELECT_FILE) {
                    // declare a stream to read the image data from the SD Card.
                    InputStream inputStream;
                    try {
                        inputStream = this.getContentResolver().openInputStream(imageUri);
                        Bitmap image = BitmapFactory.decodeStream(inputStream);
                        photoImageView.setImageBitmap(image);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Log.wtf("ERROR: ", "data is null");
            }
        }
        else if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                locationTextView.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}