package com.example.ejercicioequiposjugadoresfinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ejercicioequiposjugadoresfinal.model.objects.Equipo;
import com.example.ejercicioequiposjugadoresfinal.model.repository.Repository;
import com.example.ejercicioequiposjugadoresfinal.model.view.model.MainViewModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddEquipo extends AppCompatActivity {

    //Vistas
    private ImageButton ibEscudoEquipo;
    private EditText etNombreEquipo, etCiudadEquipo, etEstadioEquipo, etAforoEquipo;
    private Button btCrearEquipo;
    private TextView tvError;

    public MainViewModel viewModel;

    //Simples
    private Equipo equipoAux;
    private String nombreEscudo, url;
    private Uri myUri = null;

    //finales
    private final int IMAGEN_SUBIR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipo);

        initComponents();
    }

    private void initComponents() {
        //Url del Repository
        Repository repository = new Repository();
        url = repository.getURL();
        //View Model
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //Edit Texts
        tvError = findViewById(R.id.tvErrorCrear);
        etNombreEquipo = findViewById(R.id.etNombreEquipoCrear);
        etCiudadEquipo = findViewById(R.id.etCiudadEquipoCrear);
        etEstadioEquipo = findViewById(R.id.etEstadioEquipoCrear);
        etAforoEquipo = findViewById(R.id.etAforoEquipoCrear);
        //Botones
        ibEscudoEquipo = findViewById(R.id.ibCrearEquipo);
        btCrearEquipo = findViewById(R.id.btCrearEquipo);

        initEvents();
    }

    private void initEvents() {
        //Botones
        ibEscudoEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFoto();
            }
        });

        btCrearEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haRellenado()) {
                    asignarCampos();
                    viewModel.añadirEquipo(equipoAux);
                    Intent i = new Intent(AddEquipo.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    //Mirar si funciona con el loadingPage
                }
            }
        });
    }

    private boolean haRellenado() {
        if(etNombreEquipo.length() == 0){
            tvError.setText(R.string.errorNombre);
        }else if(etCiudadEquipo.length() == 0){
            tvError.setText(R.string.errorCiudad);
        }else if(etEstadioEquipo.length() == 0){
            tvError.setText(R.string.errorEstadio);
        }else if(etAforoEquipo.length() == 0){
            tvError.setText(R.string.errorAforo);
        }else if(myUri == null){
            tvError.setText(R.string.errorFoto);
        }else{
            return true;
        }
        return false;
    }

    private void asignarCampos() {
        saveSelectedImageInFile(myUri);
        equipoAux = new Equipo();
        equipoAux.setEscudo(nombreEscudo);
        equipoAux.setNombre(etNombreEquipo.getText().toString());
        equipoAux.setCiudad(etCiudadEquipo.getText().toString());
        equipoAux.setEstadio(etEstadioEquipo.getText().toString());
        equipoAux.setAforo(Integer.parseInt(etAforoEquipo.getText().toString()));
    }

    private void elegirFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*/*");
        String[] types = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
        startActivityForResult(intent, IMAGEN_SUBIR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGEN_SUBIR && resultCode == Activity.RESULT_OK && null != data) {
            Uri uri = data.getData();
            myUri = uri;
            Glide.with(this)
                    .load(uri)
                    .override(500, 500)
                    .into(ibEscudoEquipo);
        }
    }

    private void saveSelectedImageInFile(Uri uri) {
        Bitmap bitmap = null;
        if(Build.VERSION.SDK_INT < 28) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                bitmap = null;
            }
        } else {
            try {
                final InputStream in = this.getContentResolver().openInputStream(uri);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                bitmap = null;
            }
        }
        if(bitmap != null) {
            File file = saveBitmapInFile(bitmap);
            if(file != null) {
                viewModel.uploadImage(file);
            }
        }
    }

    private File saveBitmapInFile(Bitmap bitmap) {
        nombreEscudo = generarNombre(10)+".jpg";
        File file = new File(getFilesDir(), nombreEscudo);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            file = null;
        }
        return file;
    }

    private String generarNombre(int i) {
        String cadena ="LlevaLaTArarAunVesTIDoBlaNCoLLenoDeCASCabelesmuYBoniTOlEGusTaPoNERseLoPorLANocHEyELdiA";
        String nombre ="";
        int len = cadena.length()-1, aux;
        for (int e= 0; e<i; e++){
            aux = (int) (Math.random()*len);
            nombre = nombre + cadena.substring(aux,aux+1);
        }
        return nombre;
    }
}
