package com.example.phonebook;

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

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter
        extends RecyclerView.Adapter<RecycleViewAdapter.PersonViewHolder>
        implements Filterable {

    private final Context ctx;
    private List<PersonInfo> personInfoList;
    private View view;
    private final ArrayList<PersonInfo> arrayList;
    private PersonInfo personInfo;
    private DBHelper dbHelper;
    public int editPosition;
    private String fullName;
    private final ItemFilter searchPersonFilter = new ItemFilter();

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
        TextView personFio, personNumber;
        Button btnEdit, btnDelete;

        public PersonViewHolder(View itemView) {
            super(itemView);
            personFio = itemView.findViewById(R.id.personFio);
            personNumber = itemView.findViewById(R.id.personNumber);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
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
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        personInfo = personInfoList.get(position);
        int id = personInfo.getID();
        dbHelper = new DBHelper(ctx);
        fullName = String.format("%s %s %s", personInfo.getLastName(),
                personInfo.getName(), personInfo.getMiddleName());

        holder.personFio.setText(fullName);
        holder.personNumber.setText(personInfo.getPhone());
        holder.btnEdit.setOnClickListener(v -> {
            editPosition = position;
            showDialogFragment(view);
        });
        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteContact(id);
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
        dialogFragment.flag = true;
        dialogFragment.editPosition = editPosition;
        dialogFragment.filteredInfoList = personInfoList;
        MainActivity activity = (MainActivity) view.getContext();
        dialogFragment.show(activity.getSupportFragmentManager(), null);
    }

    /**
     * Обновление списков данных
     */
    public void DataRefresh() {
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

            DataRefresh();
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            DataRefresh();
            personInfoList = (ArrayList<PersonInfo>) results.values;
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

    /**
     * Фильтр для поиска записи (по всем полям)
     */
    /*  public void filter(String charText) {
        charText = charText.toLowerCase();

        if (personInfoList.size() > 0) {
            if (charText.length() > 0) {
                //filteredPersonInfoList.clear();
                for (PersonInfo personInfo : arrayList) {
                    if (personInfo.getLastName().toLowerCase().contains(charText)
                            || personInfo.getName().toLowerCase().contains(charText)
                            || personInfo.getMiddleName().toLowerCase().contains(charText)
                            || personInfo.getPhone().toLowerCase().contains(charText)) {
                        //filteredPersonInfoList.add(personInfo);
                        personInfoList.add(personInfo);
                    }
                    notifyDataSetChanged();
                }
            } else {
                personInfoList.clear();
                personInfoList.addAll(arrayList);
                //filteredPersonInfoList.clear();
                //filteredPersonInfoList.addAll(personInfoList);
                notifyDataSetChanged();
            }
        }
    }*/
}