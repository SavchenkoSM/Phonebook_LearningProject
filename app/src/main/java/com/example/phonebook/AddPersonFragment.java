package com.example.phonebook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddPersonFragment
        extends DialogFragment
        implements TextWatcher, InputFilter {

    public static final int MAX_TEXT_LENGTH = 20;
    public static final int MIN_PHONE_LENGTH = 6;

    private View view;
    public EditText lastName, name, middleName, phone;
    public TextView tvLastName, tvName, tvPhone;
    //@param flag используется для определения типа вызываемого окна (добаление/редактирование)
    public boolean flag = false;
    private DBHelper dbHelper;
    private List<PersonInfo> personInfoList;
    public List<PersonInfo> filteredInfoList;
    private PersonInfo personInfo;
    public int editPosition;
    private AlertDialog builder;
    private Button btnPositive, btnNegative;
    public static String lastNameIn, nameIn, middleNameIn, phoneIn;
    public boolean isPhoneIncorrect = false;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_add_person, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_add_person, null);
        dbHelper = new DBHelper(getActivity());

        lastName = view.findViewById(R.id.lastName);
        name = view.findViewById(R.id.name);
        middleName = view.findViewById(R.id.middleName);
        phone = view.findViewById(R.id.phone);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);

        lastName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        middleName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        //phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        lastName.addTextChangedListener(this);
        name.addTextChangedListener(this);
        phone.addTextChangedListener(this);

        if (flag) {
            if (filteredInfoList.isEmpty()) {
                personInfoList = dbHelper.getAllData();
            } else
                personInfoList = filteredInfoList;

            personInfo = personInfoList.get(editPosition);
            id = personInfo.getID();

            lastName.setText(personInfo.getLastName());
            name.setText(personInfo.getName());
            middleName.setText(personInfo.getMiddleName());
            phone.setText(personInfo.getPhone());
        }

        builder = new AlertDialog.Builder(getContext())
                .setTitle("Please, enter the specified information")
                .setIcon(R.drawable.ic_baseline_person_add_24)
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();

        builder.setOnShowListener(dialog -> {

            btnNegative = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
            btnPositive = builder.getButton(AlertDialog.BUTTON_POSITIVE);

            btnNegative.setTextColor(getResources().getColor(R.color.cancelColor));
            btnPositive.setOnClickListener(view -> {
                lastNameIn = lastName.getText().toString();
                nameIn = name.getText().toString();
                middleNameIn = middleName.getText().toString();
                phoneIn = phone.getText().toString();

                if (isFieldsEmpty() | isPhoneIncorrect) {
                    Toast.makeText(getContext(),
                            "Operation failed! " +
                                    "Check that the required fields are filled in correctly",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (flag) {
                        dbHelper.updateContact(id, lastNameIn, nameIn, middleNameIn, phoneIn);
                        Toast.makeText(getContext(),
                                "Contact is updated successfully",
                                Toast.LENGTH_LONG).show();
                    } else {
                        dbHelper.createNewRow(lastNameIn, nameIn, middleNameIn, phoneIn);
                        Toast.makeText(getContext(),
                                "Contact is created successfully",
                                Toast.LENGTH_LONG).show();
                    }
                    builder.cancel();
                }
            });
        });
        return builder;
    }

    /**
     * Метод проверки обязательных полей на пустоту
     */
    public boolean isFieldsEmpty() {

        if (lastNameIn.isEmpty()) {
            tvLastName.setText(R.string.is_empty);
        }
        if (nameIn.isEmpty()) {
            tvName.setText(R.string.is_empty);
        }
        if (phoneIn.isEmpty()) {
            tvPhone.setText(R.string.is_empty);
        }
        return (lastNameIn.isEmpty() | nameIn.isEmpty() | phoneIn.isEmpty());
    }

    /*public EditText findEditText(View v, CharSequence s)
    {
        EditText editText;
        ViewGroup vg;
        int i, n;

        if (v instanceof EditText)
        {
            editText = (EditText) v;
            if (editText.getText()==s) return(editText);
        }

        else if (v instanceof ViewGroup)
        {
            vg = (ViewGroup) v;
            n = vg.getChildCount();
            for(i=0;i<n;i++)
            {
                editText = findEditText(vg.getChildAt(i), s);
                if (editText!=null) return(editText);
            }
        }
        return(null);
    }*/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * Проверка правильности ввода данных
     */
    @Override
    public void afterTextChanged(Editable enteredSequence) {

        if (lastName.isFocused()) {
            if (enteredSequence.length() == 0)
                tvLastName.setText(R.string.not_entered);
           /* else if (s.length() > 19)
                tvLastName.setText(R.string.txt_limit);*/
            else tvLastName.setText("");
        } else if (name.isFocused()) {
            if (enteredSequence.length() == 0)
                tvName.setText(R.string.not_entered);
            /*else if (s.length() > 19)
                tvName.setText(R.string.txt_limit);*/
            else tvName.setText("");
        } else if (phone.isFocused()) {
            if (enteredSequence.length() == 0)
                tvPhone.setText(R.string.not_entered);
            else if (enteredSequence.length() < MIN_PHONE_LENGTH) {
                tvPhone.setText(R.string.lower_limit);
                isPhoneIncorrect = true;
            }
            /*else if (s.length() > 10)
                tvPhone.setText(R.string.upper_limit);*/
            else {
                tvPhone.setText("");
                isPhoneIncorrect = false;
            }
        }

        /*EditText editText = findEditText(view.findViewById(android.R.id.content), s);
        if (editText==null) return;

        switch (editText.getId()) {
            case R.id.lastName:
                if (s.length() == 0)
                    tvLastName.setText(R.string.not_entered);
                else if (s.length() > 19)
                    tvLastName.setText(R.string.txt_limit);
                else tvLastName.setText("");
                break;
            case R.id.name:
                if (s.length() == 0)
                    tvName.setText(R.string.not_entered);
                else if (s.length() > 19)
                    tvName.setText(R.string.txt_limit);
                else tvName.setText("");
                break;
            case R.id.phone:
                if (s.length() == 0)
                    tvPhone.setText(R.string.not_entered);
                else if (s.length() < 6)
                    tvPhone.setText(R.string.lower_limit);
                else if (s.length() > 10)
                    tvPhone.setText(R.string.upper_limit);
                else tvPhone.setText("");
                break;*/
    }

    /**
     * Фильтр, допускающий ввод только букв
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }

    /**
     * Проверка на правильность заполнения полей (Фамилии, Имени, Номера телефона)
     */
    /*public void isEnterDataCorrect() {

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(),
                        "The last name must not contain more than 20 characters",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    tvLastName.setText(R.string.not_entered);
                else if (s.length() > 19)
                    tvLastName.setText(R.string.txt_limit);
                else tvLastName.setText("");
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(),
                        "The name must not contain more than 20 characters",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    tvName.setText(R.string.not_entered);
                else if (s.length() > 19)
                    tvName.setText(R.string.txt_limit);
                else tvName.setText("");
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(),
                        "The phone number must contain at least 6 and no more than 10 digits",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    tvPhone.setText(R.string.not_entered);
                else if (s.length() < 6)
                    tvPhone.setText(R.string.lower_limit);
                else if (s.length() > 10)
                    tvPhone.setText(R.string.upper_limit);
                else tvPhone.setText("");
            }
        });
    }*/
}