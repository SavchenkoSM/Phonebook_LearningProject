package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private DBHelper dbHelper;
    private List<PersonInfo> personInfoList;
    private RecycleViewAdapter adapter;
    private SwipeRefreshLayout swipeRefLayout;
    private SearchView searchView;
    private FloatingActionButton addPersonFab;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        addPersonFab = findViewById(R.id.addPersonFab);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefLayout = findViewById(R.id.swipeRefLayout);

        searchView.setOnQueryTextListener(this);
        addPersonFab.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        personInfoList = dbHelper.getAllData();
        adapter = new RecycleViewAdapter(this, personInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        swipeRefLayout.setOnRefreshListener(this);
        swipeRefLayout.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light, android.R.color.holo_green_light);
    }

    /**
     * Обработка нажатия кнопки добавления контакта
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPersonFab:
                AddPersonFragment addPersonFragment = new AddPersonFragment();
                addPersonFragment.show(getSupportFragmentManager(), "AddPerson");
                break;
        }
    }

    /**
     * Обновление списка контактов по свайпу
     */
    @Override
    public void onRefresh() {
        personInfoList = dbHelper.getAllData();
        adapter.updateData(personInfoList);
        swipeRefLayout.setRefreshing(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Вызов фильтрации карточек по поисковому запросу (по всем полям)
     */
    @Override
    public boolean onQueryTextChange(String searchQuery) {
        adapter.getFilter().filter(searchQuery);
        return false;
    }
}