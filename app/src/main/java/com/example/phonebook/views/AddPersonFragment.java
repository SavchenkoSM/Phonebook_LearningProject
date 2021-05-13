package com.example.phonebook.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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

import com.example.phonebook.R;
import com.example.phonebook.adapter.RecycleViewAdapter;
import com.example.phonebook.database.DBHelper;
import com.example.phonebook.models.PersonInfo;

import java.util.List;
import java.util.Objects;

public class AddPersonFragment
        extends DialogFragment
        implements TextWatcher, InputFilter {

    private static final int MAX_TEXT_LENGTH = 20;
    private static final int MAX_PHONE_LENGTH = 25;
    private static final int MIN_PHONE_LENGTH = 6;

    //@param isEditButtonPressed используется для определения
    //типа отображения диалогового окна (true - окно редактирования, false - добавления)
    public boolean isEditButtonPressed = false;
    public boolean isPhoneIncorrect = false;

    private DBHelper dbHelper;

    private PersonInfo personInfo;
    private List<PersonInfo> personInfoList;
    public List<PersonInfo> filteredPersonsInfoList;

    private static String lastName, name, middleName, phone;
    public int editPosition;
    private int editPersonId;

    private View view;
    public EditText lastNameEditText, nameEditText, middleNameEditText, phoneEditText;
    public TextView lastNameTextView, nameTextView, phoneTextView;
    private Button positiveButton, negativeButton;

    private AlertDialog builder;
    private RecycleViewAdapter recycleViewAdapter;

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

        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        middleNameEditText = view.findViewById(R.id.middleNameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);

        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);

        lastNameEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        nameEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        middleNameEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(MAX_TEXT_LENGTH), this});
        phoneEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(MAX_PHONE_LENGTH)});

        lastNameEditText.addTextChangedListener(this);
        nameEditText.addTextChangedListener(this);
        phoneEditText.addTextChangedListener(this);

        if (isEditButtonPressed) {

            if (filteredPersonsInfoList.isEmpty()) {
                personInfoList = dbHelper.getAllData();
            } else
                personInfoList = filteredPersonsInfoList;

            personInfo = personInfoList.get(editPosition);
            editPersonId = personInfo.getID();

            lastNameEditText.setText(personInfo.getLastName());
            nameEditText.setText(personInfo.getName());
            middleNameEditText.setText(personInfo.getMiddleName());
            phoneEditText.setText(personInfo.getPhone());
        }

        builder = new AlertDialog.Builder(getContext())
                .setTitle("Please, enter the specified information")
                .setIcon(R.drawable.ic_baseline_person_add_24)
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();

        builder.setOnShowListener(dialog -> {

            negativeButton = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
            positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);

            negativeButton.setTextColor(getResources().getColor(R.color.cancelColor));
            positiveButton.setOnClickListener(view -> {
                lastName = lastNameEditText.getText().toString();
                name = nameEditText.getText().toString();
                middleName = middleNameEditText.getText().toString();
                phone = phoneEditText.getText().toString();

                if (isFieldsEmpty() | isPhoneIncorrect) {
                    Toast.makeText(getContext(),
                            "Operation failed! " +
                                    "Check that the required fields are filled in correctly",
                            Toast.LENGTH_LONG).show();
                } else if (isEditButtonPressed) {
                    dbHelper.updateContact(editPersonId, lastName, name, middleName, phone);
                    Toast.makeText(getContext(),
                            "Contact is updated successfully",
                            Toast.LENGTH_LONG).show();
                    builder.cancel();
                } else {
                    dbHelper.createNewRow(lastName, name, middleName, phone);
                    Toast.makeText(getContext(),
                            "Contact is created successfully",
                            Toast.LENGTH_LONG).show();
                    builder.cancel();
                }
                updateRecyclerViewAdapter();
            });
        });
        return builder;
    }

    /**
     * Обновление списка карточек
     */
    public void updateRecyclerViewAdapter() {
        MainActivity mainActivity = (MainActivity) getActivity();
        recycleViewAdapter = mainActivity.recycleViewAdapter;
        personInfoList = dbHelper.getAllData();
        recycleViewAdapter.updateData(personInfoList);
    }

    /**
     * Проверка обязательных полей на пустоту
     */
    public boolean isFieldsEmpty() {

        if (lastName.isEmpty()) {
            lastNameTextView.setText(R.string.is_empty);
        }
        if (name.isEmpty()) {
            nameTextView.setText(R.string.is_empty);
        }
        if (phone.isEmpty()) {
            phoneTextView.setText(R.string.is_empty);
        }
        return (lastName.isEmpty() | name.isEmpty() | phone.isEmpty());
    }

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

        if (phoneEditText.isFocused() && enteredSequence.length() < MIN_PHONE_LENGTH) {
            phoneTextView.setText(R.string.lower_limit);
            isPhoneIncorrect = true;
        } else {
            phoneTextView.setText("");
            isPhoneIncorrect = false;
        }
    }

    /**
     * Фильтр, допускающий ввод только букв
     */
    @Override
    public CharSequence filter(CharSequence inputText, int start, int end,
                               Spanned dest, int dstart, int dend) {

        if (inputText instanceof SpannableStringBuilder) {
            SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) inputText;
            for (int i = end - 1; i >= start; i--) {
                char currentChar = inputText.charAt(i);
                if (!Character.isLetter(currentChar)) {
                    sourceAsSpannableBuilder.delete(i, i + 1);
                }
            }
            return inputText;
        } else {
            StringBuilder filteredStringBuilder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char currentChar = inputText.charAt(i);
                if (Character.isLetter(currentChar)) {
                    filteredStringBuilder.append(currentChar);
                }
            }
            return filteredStringBuilder.toString();
        }
    }
}