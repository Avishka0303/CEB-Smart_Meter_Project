package com.example.predatorx21.cebsmartmeter.dashboard;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.example.predatorx21.cebsmartmeter.utilities.ConsumptionCharge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class ControlFragment extends Fragment {

    private Switch threshold_sw;
    private Switch notification_sw;
    private Spinner typeList;
    private EditText consumption;
    private EditText charge;
    private Button updatebtn;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year,month,day;

    public ControlFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        threshold_sw=(Switch) getView().findViewById(R.id.threshold_switch);
        typeList=(Spinner)getView().findViewById(R.id.type_list);
        consumption=(EditText)getView().findViewById(R.id.edit_consumption);
        charge=(EditText)getView().findViewById(R.id.edit_charge);
        notification_sw=(Switch)getView().findViewById(R.id.notification_switch);
        updatebtn=(Button)getView().findViewById(R.id.updateBtn);


        setupSpinnerTypeList();
        initializeDetails();
        setTextFieldActionListeners();
        setSwitchActionListeners();
        setButtonActionListeners();

    }

//--------------------------------------------------------------------------UPDATE BUTTON -----------------------------------------------------------------------------------
    private void setButtonActionListeners() {
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=typeList.getSelectedItem().toString();
                double cons=Double.parseDouble(consumption.getText().toString());
                int notification_status=0;
                if(notification_sw.isChecked()){
                    notification_status=1;
                }
                String query="UPDATE Meter SET ThresholdStatus='1',ThresholdValue='"+cons+"',ThresholdType='"+type+"',ThresholdNotifi='"+notification_status+"' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
                if(!DB.updateDB(query)){
                    Toast.makeText(getContext(),"Successfully updated",Toast.LENGTH_SHORT).show();
                    updatebtn.setVisibility(View.INVISIBLE);
                    threshold_sw.setChecked(true);
                }else{
                    Toast.makeText(getContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        updatebtn.setVisibility(View.INVISIBLE);
    }

//--------------------------------------------------------------------------SWITCHES ACTIONS----------------------------------------------------------------------------------
    private void setSwitchActionListeners() {
        threshold_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    String query="UPDATE Meter SET ThresholdStatus='1' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
                    if(!DB.updateDB(query)){
                        Toast.makeText(getContext(),"Power limit On",Toast.LENGTH_SHORT).show();
                        threshold_sw.setText("on");
                    }else{
                        Toast.makeText(getContext(),"not updated",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String query="UPDATE Meter SET ThresholdStatus='0' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
                    if(!DB.updateDB(query)){
                        Toast.makeText(getContext(),"Power limit off",Toast.LENGTH_SHORT).show();
                        threshold_sw.setText("off");
                    }else{
                        Toast.makeText(getContext(),"not updated",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        notification_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    String query="UPDATE Meter SET ThresholdNotifi='1' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
                    if(!DB.updateDB(query)){
                        threshold_sw.setText("on");
                        Toast.makeText(getContext(),"Notification on",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"not updated",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String query="UPDATE Meter SET ThresholdNotifi='0' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
                    if(!DB.updateDB(query)){
                        threshold_sw.setText("off");
                        Toast.makeText(getContext(),"Notification off",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"not updated",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

//------------------------------------------------------------------------------------TEXT FIELD LISTENERS-------------------------------------------------------------------------
    private void setTextFieldActionListeners() {

        consumption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                updatebtn.setVisibility(View.VISIBLE);

                if(charSequence.length()!=0 && !charSequence.equals(".")){
                    double units=Double.parseDouble(consumption.getText().toString());
                    double charges[]=ConsumptionCharge.UsageInCharge(units);
                    charge.setText(charges[2]+"");
                }else{
                    charge.setText("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//--------------------------------------------------------------------------------------INITIALIZE GUI DETAILS----------------------------------------------------------------------
    private void initializeDetails() {

        String query="SELECT * FROM Meter WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
        ResultSet resultSet= DB.searchDB(query);
        try {
            if(resultSet.next()){
                String thresholdStatus=resultSet.getString("ThresholdStatus");
                if(thresholdStatus.equals("1")){
                    threshold_sw.setChecked(true);
                    consumption.setText(resultSet.getString("ThresholdValue"));
                    typeList.setSelection(getPosition(resultSet.getString("ThresholdType")));
                    double charges[]=ConsumptionCharge.UsageInCharge(Double.parseDouble(resultSet.getString("ThresholdValue")));
                    charge.setText(charges[2]+"");
                    if(resultSet.getString("ThresholdNotifi").equals("1"))
                        notification_sw.setChecked(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getPosition(String type) {
        if(type.equals("Daily")) return 0;
        else if(type.equals("Monthly")) return 1;
        else return 2;
    }

    private void setupSpinnerTypeList() {
        ArrayList<String> list=new ArrayList<>();
        list.add("Daily");
        list.add("Monthly");
        list.add("Custom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeList.setAdapter(dataAdapter);

        typeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updatebtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
