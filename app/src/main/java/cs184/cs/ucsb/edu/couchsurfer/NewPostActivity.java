package cs184.cs.ucsb.edu.couchsurfer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private int year, month, day;

    private Button dateButton, postButton;
    private FloatingActionButton photoButton;
    private TextView titleTextView, dateTextView;
    private EditText priceEditText, descriptionEditText, locationEditText;
    private ImageView photoImageView;

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
        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        dateButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);



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
            case R.id.postButton:
                break;
            case R.id.photoButton:
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
}