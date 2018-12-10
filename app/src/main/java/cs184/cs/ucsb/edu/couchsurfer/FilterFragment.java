package cs184.cs.ucsb.edu.couchsurfer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

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
                //Filter Minimum Price
                if(!fPriceMin.getText().toString().isEmpty()){
                    Log.d("tag","Changing main.fPriceMin to " + fPriceMin.getText().toString());
                    main.fPriceMin = Double.parseDouble(fPriceMin.getText().toString());
                }
                else{
                    // reset to default
                    main.fPriceMin = 0;
                }

                // Filter Max Price
                if(!fPriceMax.getText().toString().isEmpty()){
                    Log.d("tag","Changing main.fPriceMax to " + fPriceMax.getText().toString());
                    main.fPriceMax = Double.parseDouble(fPriceMax.getText().toString());
                }
                else{
                    // reset to just a really big number
                    main.fPriceMax = 99999;
                }

                // Filter Distance
                if(!fDistance.getText().toString().isEmpty()){
                    Log.d("tag","Changing main.fDistance to " + fDistance.getText().toString());
                    main.fDistance = Double.parseDouble(fDistance.getText().toString());
                }
                else{
                    // reset to a big number
                    main.fDistance = 99999;
                }

                // Filter Date
                if(day != 0 && month != 0 && year != 0){
                    Log.d("tag","Changing main.fDate to " + month + "/" + day + "/" + year);
                    main.fDate = new Date();
                    main.fDate.setMonth(month);
                    main.fDate.setYear(year);
                    main.fDate.setDate(day);
                }
                else{
                    // set date to null for easy error checking
                    main.fDate = null;
                }

                // Close fragment by reopening listview
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
            fDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + (year));
        }
    }

    public void setDay(int day) { this.day = day; }

    public void setMonth(int month) { this.month = month; }

    public void setYear(int year) { this.year = year; }
}
