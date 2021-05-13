package com.example.phonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonebook.R;
import com.example.phonebook.database.DBHelper;
import com.example.phonebook.models.PersonInfo;
import com.example.phonebook.views.AddPersonFragment;
import com.example.phonebook.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter
        extends RecyclerView.Adapter<RecycleViewAdapter.PersonViewHolder>
        implements Filterable {

    private final Context ctx;

    private DBHelper dbHelper;

    private PersonInfo personInfo;
    private List<PersonInfo> personInfoList;
    private final ArrayList<PersonInfo> arrayList;

    public int editPosition;
    private String fullPersonName;

    private final ItemFilter searchPersonFilter = new ItemFilter();

    private View view;

    public RecycleViewAdapter(Context ctx, List<PersonInfo> data) {
        this.ctx = ctx;
        this.personInfoList = data;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(personInfoList);
    }

    /**
     * Установка разметки для карточки контакта
     */
    static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView personFioTextView, personPhoneTextView;
        Button editButton, deleteButton;

        public PersonViewHolder(View itemView) {
            super(itemView);
            personFioTextView = itemView.findViewById(R.id.personFio);
            personPhoneTextView = itemView.findViewById(R.id.personNumber);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        view = inflater.inflate(R.layout.card_view, null);
        return new PersonViewHolder(view);
    }

    /**
     * Заполнение полей карточки контакта и обработка взаимодействий с ней
     */
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int position) {
        personInfo = personInfoList.get(position);
        int personInfoID = personInfo.getID();
        dbHelper = new DBHelper(ctx);
        fullPersonName = String.format("%s %s %s", personInfo.getLastName(),
                personInfo.getName(), personInfo.getMiddleName());

        personViewHolder.personFioTextView.setText(fullPersonName);
        personViewHolder.personPhoneTextView.setText(personInfo.getPhone());
        personViewHolder.editButton.setOnClickListener(v -> {
            editPosition = position;
            showDialogFragment(view);
        });
        personViewHolder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteContact(personInfoID);
            personInfoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            updateData(personInfoList);
        });
    }

    /**
     * Создание и вывод диалогового окна
     */
    public void showDialogFragment(View view) {
        AddPersonFragment dialogFragment = new AddPersonFragment();
        dialogFragment.isEditButtonPressed = true;
        dialogFragment.editPosition = editPosition;
        dialogFragment.filteredPersonsInfoList = personInfoList;
        MainActivity mainActivity = (MainActivity) view.getContext();
        dialogFragment.show(mainActivity.getSupportFragmentManager(), null);
    }

    /**
     * Обновление списков данных
     */
    public void dataRefresh() {
        if (personInfoList.size() > 0) {
            personInfoList = dbHelper.getAllData();
            arrayList.clear();
            arrayList.addAll(personInfoList);
            personInfoList.clear();
        }
    }

    /**
     * Определение количества контактов
     */
    @Override
    public int getItemCount() {
        return personInfoList.size();
    }

    /**
     * Фильтр для поиска записей (по всем полям контакта)
     */
    @Override
    public Filter getFilter() {
        return searchPersonFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence searchQuery) {
            searchQuery = searchQuery.toString().toLowerCase();
            FilterResults searchResults = new FilterResults();

            dataRefresh();
            if (searchQuery.length() == 0) {
                personInfoList.addAll(arrayList);
            } else {
                for (PersonInfo personInfo : arrayList) {
                    if (personInfo.getLastName().toLowerCase().contains(searchQuery)
                            || personInfo.getName().toLowerCase().contains(searchQuery)
                            || personInfo.getMiddleName().toLowerCase().contains(searchQuery)
                            || personInfo.getPhone().toLowerCase().contains(searchQuery)) {
                        personInfoList.add(personInfo);
                    }
                }
            }
            searchResults.values = personInfoList;
            searchResults.count = getItemCount();

            return searchResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataRefresh();
            personInfoList = (ArrayList<PersonInfo>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    /**
     * Обновление данных о контактах
     */
    public void updateData(List<PersonInfo> personInfoList) {
        this.personInfoList = personInfoList;
        notifyDataSetChanged();
    }
}