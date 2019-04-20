package com.example.predatorx21.cebsmartmeter.dashboard;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.predatorx21.cebsmartmeter.R;

import java.util.ArrayList;

public class ControlFragment extends Fragment {

    private Switch threshold_sw;
    private Spinner typeList;


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
        setupSpinnerTypeList();
    }

    private void setupSpinnerTypeList() {
        ArrayList<String> list=new ArrayList<>();
        list.add("Daily");
        list.add("Monthly");
        list.add("Custom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeList.setAdapter(dataAdapter);
    }
}
