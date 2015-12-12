package com.example.anurag.instabook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewForm extends Fragment implements AdapterView.OnItemSelectedListener,ActionMode.Callback,View.OnClickListener,ExpandableListViewFragment.OnFragmentInteractionListener {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String,List<String>> data2;
    AutoCompleteTextView stationsFrom,stationsTo;
    String[] s_array,s_arrayTo;
    SQLDBhelper  handler;
    private TextView pDisplayDate;
    public Button pPickDate;
    private int pYear;
    private int pMonth;
    private int pDay;
    private int test;
    public View view;
    /** This integer will uniquely define the dialog to be used for displaying date picker.*/
    static final int DATE_DIALOG_ID = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    public NewForm() {
        // Required empty public constructor
    }
    public void setTest(){
        this.test=1;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewForm.
     */
    public static NewForm newInstance(String param1, String param2) {
        NewForm fragment = new NewForm();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate thgetSupportFragmentManager().beginTransaction().add(myFragment, "Some Tag").commit();e layout for this fragment
        view= inflater.inflate(R.layout.fragment_new_form, container, false);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        //Date Picking
        ExpandableListViewFragment listfragment= new ExpandableListViewFragment();
        listfragment.newInstance("EXPLISTVW","EXPLISTVW");
        Bundle args = new Bundle();
        listfragment.setArguments(getActivity().getIntent().getExtras());
        listfragment.setArguments(args);
        android.support.v4.app.FragmentManager fragmentManagera = getActivity().getSupportFragmentManager();
        getActivity().getSupportFragmentManager().beginTransaction().add(listfragment, "EXPLISTVW").commit();
        fragmentManagera.beginTransaction().replace(R.id.flContent2,listfragment).commit();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManagera.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        pDisplayDate = (TextView)view.findViewById(R.id.dateText);
        pPickDate = (Button) view.findViewById(R.id.journeydate);
        final DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                R.style.AppTheme, datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");

        /** Listener for click event of the button */
        pPickDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getContext(),"Date clicked",Toast.LENGTH_LONG).show();
                //noinspection deprecation
                datePicker.show();
                getActivity().showDialog(DATE_DIALOG_ID);
            }
        });
        /** Get the current date */



// Create the DatePickerDialog instance

        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);

        /** Display the current date in the TextView */
        updateDisplay();

        TextView date=(TextView)view.findViewById(R.id.dateText);
        String today= DateFormat.getDateInstance().format(new Date());
        date.setText(today);
//        Auto Complete From
        stationsFrom=(AutoCompleteTextView)view.findViewById(R.id.fromAutoComplete);
        s_array=getResources().getStringArray(R.array.Stations);
        ArrayAdapter<String> stationAdapter=new ArrayAdapter<String>(this.getContext() ,android.R.layout.simple_list_item_1,s_array);
        stationsFrom.setAdapter(stationAdapter);
        stationsFrom.setThreshold(3);

        // Auto Complete To
        stationsTo=(AutoCompleteTextView) view.findViewById(R.id.toAutoComplete);
        s_arrayTo=getResources().getStringArray(R.array.Stations);
        ArrayAdapter<String> stationAdapterTo=new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1,s_arrayTo);
        stationsTo.setAdapter(stationAdapterTo);
        stationsTo.setThreshold(3);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("E-Ticket");
        categories.add("i-Ticket");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
//



        return view;
    }
    public void refresh(int i){

    }
    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            TextView tvDt = (TextView)getView().findViewById(R.id.dateText);
            tvDt.setText(day1 + "-" + month1 + "-" + year1);

        }
    };
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        handler=new SQLDBhelper(this.getContext());


        List<Passanger> l =handler.dBtoPassanger();
        Passanger [] p= l.toArray(new Passanger[l.size()]);
        Integer integer = Integer.valueOf(l.size());
        for (int i=0;i<integer;i++){
            listDataHeader.add(p[i].getName());
            List<String> data = new ArrayList<>();
            data.add("Age :"+p[i].getAge());
            data.add("Sex :"+p[i].getSex());
            data.add("UID :"+p[i].getUID());
            data.add("Berth Preference :" + p[i].getBerth());
            data.add(p[i].getuserID());
            listDataChild.put(listDataHeader.get(i), data);


        }


    }

    /** Callback received when the user "picks" a date in the dialog */
    private DatePickerDialog.OnDateSetListener pDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    pYear = year;
                    pMonth = monthOfYear;
                    pDay = dayOfMonth;
                    updateDisplay();

                }
            };

    /** Updates the date in the TextView */
    private void updateDisplay() {
        pDisplayDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(pMonth + 1).append("/")
                        .append(pDay).append("/")
                        .append(pYear).append(" "));
    }


//    public void date(View v){
//        Intent i = new Intent(getActivity().getApplicationContext(),FloatingDatePicker.class);
//        startActivity(i);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    String msg = "Android : ";
    public void onStart() {
        super.onStart();
        Log.d(msg, "The onStart() event");
        prepareListData();
//        ExpandableListView expListView = (ExpandableListView)getView().findViewById(R.id.lvExp);
//        // preparing list data
//        prepareListData();
//        listAdapter = new ExpandableListAdapter(this.getContext(), listDataHeader, listDataChild);
//        // setting list adapter
//        expListView.setAdapter(listAdapter);
//        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                return ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP;
//            }
//        });
    }



}
