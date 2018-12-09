package cs184.cs.ucsb.edu.couchsurfer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class FilterFragment extends Fragment implements View.OnClickListener{
    MainActivity main;
    Button fSaveButton, fDateButton;
    EditText fPriceMin, fPriceMax, fDistance;
    TextView fDate;
    int month,day,year;
    private DatePickerDialogListener dateListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main = (MainActivity) getActivity();
        fSaveButton = view.findViewById(R.id.filter_savebutton);
        fPriceMin = view.findViewById(R.id.filter_minprice);
        fPriceMax = view.findViewById(R.id.filter_maxprice);
        fDistance = view.findViewById(R.id.filter_miles);
        fDateButton = view.findViewById(R.id.filter_datebutton);
        fDate = view.findViewById(R.id.filter_datetv);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        dateListener = new DatePickerDialogListener();

        fDateButton.setOnClickListener(this);
        fSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.filter_datebutton:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateListener,
                        FilterFragment.this.year, FilterFragment.this.month, FilterFragment.this.day);
                datePickerDialog.show();
                break;
            case R.id.filter_savebutton:
                // Check if edit text is empty, skip empty fields
                if(!fPriceMin.getText().toString().equals("")){
                    main.fPriceMin = Double.parseDouble(fPriceMin.getText().toString());
                }
                if(!fPriceMax.getText().toString().equals("")){
                    main.fPriceMax = Double.parseDouble(fPriceMin.getText().toString());
                }
                if(!fDistance.getText().toString().equals("")){
                    main.fDistance = Double.parseDouble(fDistance.getText().toString());
                }
                // Update data structure that listview and mapview use
                // Close fragment
                main.defaultFragment();
                break;
        }
    }


    private class DatePickerDialogListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            FilterFragment.this.setYear(year);
            FilterFragment.this.setMonth(monthOfYear);
            FilterFragment.this.setDay(dayOfMonth);
            fDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
        }
    }

    public void setDay(int day) { this.day = day; }

    public void setMonth(int month) { this.month = month; }

    public void setYear(int year) { this.year = year; }
}
