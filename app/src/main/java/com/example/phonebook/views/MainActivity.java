package com.example.phonebook.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.example.phonebook.R;
import com.example.phonebook.adapter.RecycleViewAdapter;
import com.example.phonebook.database.DBHelper;
import com.example.phonebook.models.PersonInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private DBHelper dbHelper;

    private List<PersonInfo> personInfoList;

    public RecyclerView recyclerView;
    public RecycleViewAdapter recycleViewAdapter;

    private SwipeRefreshLayout swipeRefLayout;
    public SearchView searchView;
    private FloatingActionButton addPersonFab;

    AddPersonFragment addPersonFragment = new AddPersonFragment();

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
        recycleViewAdapter = new RecycleViewAdapter(this, personInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recycleViewAdapter);
        swipeRefLayout.setOnRefreshListener(this);
        swipeRefLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light, android.R.color.holo_green_light);
    }

    /**
     * Обработка нажатия кнопки добавления контакта
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPersonFab:
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
        recycleViewAdapter.updateData(personInfoList);
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
        recycleViewAdapter.getFilter().filter(searchQuery);
        return false;
    }
}