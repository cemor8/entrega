package com.example.biblioteca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControllerModificarLibro extends AppCompatActivity {
    private ArrayList<Libro> libros;
    private Libro libroSeleccionado;
    private EditText modificarAutor;
    private EditText modificarTitulo;
    private EditText modificarPag;
    private EditText modificarFecha;
    private Context context;
    Map<String, String> columnasExpresiones = new HashMap<String, String>() {
        {
            put("Paginas", "^\\d{1,5}$");
            put("Titulo", "^[\\w\\s.,!?-]{1,30}$");
            put("Autor", "^(?=.*[a-z])(?=.*[A-Z]).{4,30}$");
            put("Fecha", "^\\d{2}-\\d{2}-\\d{4}$");
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar_libro);
        Intent intent = getIntent();
        context = getApplicationContext();
        if (intent.hasExtra("libros")) {
            this.libros = (ArrayList<Libro>) intent.getSerializableExtra("libros");
        }

        if(intent.hasExtra("posicionLibro")){
            this.libroSeleccionado = this.libros.get(intent.getIntExtra("posicionLibro",0));
        }
        this.modificarAutor = findViewById(R.id.modificarAutor);
        this.modificarFecha = findViewById(R.id.modificarFecha);
        this.modificarPag = findViewById(R.id.modificarPag);
        this.modificarTitulo = findViewById(R.id.modificarTitulo);

    }
    public void volver(View view){
        Intent intent = new Intent(this, ControllerCadaLibro.class);
        intent.putExtra("posicionLibro", this.libros.indexOf(this.libroSeleccionado));
        intent.putExtra("libros",this.libros);
        startActivity(intent);
    }
    public void guardar(View view){
        String tituloInicial = this.libroSeleccionado.getTitulo();
        boolean error = false;

        if(!this.modificarTitulo.getText().toString().isEmpty() && !validarDatos(this.columnasExpresiones.get("Titulo"),this.modificarTitulo.getText().toString())){
            this.modificarTitulo.setText("");
            System.out.println("titulo mal");
            error = true;
        }

        if(!this.modificarAutor.getText().toString().isEmpty() &&!validarDatos(this.columnasExpresiones.get("Autor"),this.modificarAutor.getText().toString())){
            System.out.println("autor mal");
            this.modificarAutor.setText("");
            error = true;
        }

        if(!this.modificarPag.getText().toString().isEmpty() &&!validarDatos(this.columnasExpresiones.get("Paginas"),this.modificarPag.getText().toString())){
            System.out.println("pagina mal");
            this.modificarPag.setText("");
            error = true;
        }
        if(!this.modificarFecha.getText().toString().isEmpty() &&!validarDatos(this.columnasExpresiones.get("Fecha"),this.modificarFecha.getText().toString())){
            System.out.println("fecha mal");
            this.modificarFecha.setText("");
            error = true;
        }
        if(error){
            Toast.makeText(context,"Datos inválidos",Toast.LENGTH_LONG).show();
            return;
        }
        boolean cambiado = false;
        if(!this.modificarTitulo.getText().toString().isEmpty()){
            Optional<Libro> libroRepetidoOptional = this.libros.stream().filter(libro -> libro.getTitulo().equalsIgnoreCase(this.modificarTitulo.getText().toString())).findAny();
            if(libroRepetidoOptional.isPresent()){
                Toast.makeText(this,"Libro con Id Existente",Toast.LENGTH_LONG).show();
                return;
            }
            this.libroSeleccionado.setTitulo(this.modificarTitulo.getText().toString());
            cambiado = true;
        }
        if(!this.modificarFecha.getText().toString().isEmpty()){
            this.libroSeleccionado.setFecha(this.modificarFecha.getText().toString());
            cambiado = true;
        }
        if(!this.modificarAutor.getText().toString().isEmpty()){
            this.libroSeleccionado.setAutor(this.modificarAutor.getText().toString());
            cambiado = true;
        }
        if(!this.modificarPag.getText().toString().isEmpty()){
            this.libroSeleccionado.setPaginas(Integer.valueOf(this.modificarPag.getText().toString()));
            cambiado = true;
        }
        this.modificarPag.setText("");
        this.modificarFecha.setText("");
        this.modificarTitulo.setText("");
        this.modificarAutor.setText("");
        if (!cambiado){
            return;
        }
        Api api = ConexionRetrofit.getConexion().create(Api.class);
        Call<ResponseBody> call = api.actualizarLibro(tituloInicial,this.libroSeleccionado);
        call.enqueue(new Callback<ResponseBody>() {
            @Override   
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("hola");
                Toast.makeText(context,"Modificacion correcta",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("error");
            }
        });

    }
    /**
     * Método que se encarga de validar los datos para que se cumpla la
     * expresion regular.
     *
     * @param patronCumplir patron a cumplir
     * @param textoBuscar   string donde buscar el patron
     */
    public boolean validarDatos(String patronCumplir, String textoBuscar) {
        Pattern patron = Pattern.compile(patronCumplir);
        Matcher matcher = patron.matcher(textoBuscar);
        return matcher.matches();
    }
}
