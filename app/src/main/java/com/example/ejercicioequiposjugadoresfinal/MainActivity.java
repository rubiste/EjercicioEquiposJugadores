package com.example.ejercicioequiposjugadoresfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.ejercicioequiposjugadoresfinal.model.adapters.EquipoAdapter;
import com.example.ejercicioequiposjugadoresfinal.model.objects.Equipo;
import com.example.ejercicioequiposjugadoresfinal.model.repository.Repository;
import com.example.ejercicioequiposjugadoresfinal.model.view.model.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainViewModel viewModel;
    private EquipoAdapter equipoAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView recyclerEquipos;
    private Repository repository;

    public static String myUrl ="0.0.0.0";
    private final String URL = "SettingsUrl";
    private final String SHARED = "misShared";
    private final String ID_EQUIPO_SELECTED ="idEquipoSelected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddEquipo.class);
                startActivity(i);
            }
        });

        initComponents();
    }

    private void initComponents() {
        cogerShared();
        recyclerEquipos = findViewById(R.id.recycler_equipos);
        equipoAdapter = new EquipoAdapter(this, new EquipoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Equipo equipo) {
                Intent i = new Intent(MainActivity.this, EquipoSelected.class);
                i.putExtra(ID_EQUIPO_SELECTED, equipo.getId());
                startActivity(i);
            }
        });

        layoutManager = new GridLayoutManager(this, 3);
        recyclerEquipos.setLayoutManager(layoutManager);
        recyclerEquipos.setAdapter(equipoAdapter);
        recyclerEquipos.setHasFixedSize(true);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveDataEquiposList().observe(this, new Observer<List<Equipo>>() {
            @Override
            public void onChanged(List<Equipo> equipos) {
                equipoAdapter.setData(equipos);
            }
        });
    }

    private void cogerShared() {
        SharedPreferences mySharedUrl = PreferenceManager.getDefaultSharedPreferences(this);
        myUrl = mySharedUrl.getString(URL, "0.0.0.0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cogerShared();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
