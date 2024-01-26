package com.example.biblioteca;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DeserializarLibro implements JsonDeserializer<Libro> {
    @Override
    public Libro deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject contenidoJson = json.getAsJsonObject();
        String titulo;
        if (contenidoJson.has("titulo") && !contenidoJson.get("titulo").isJsonNull()) {
            titulo = contenidoJson.get("titulo").getAsString();
        } else {
            titulo = "";
        }

        String fecha;
        if (contenidoJson.has("fecha") && !contenidoJson.get("fecha").isJsonNull()) {
            fecha = contenidoJson.get("fecha").getAsString();
        } else {
            fecha = "";
        }

        Integer paginas;
        if (contenidoJson.has("paginas") && !contenidoJson.get("paginas").isJsonNull()) {
            paginas = contenidoJson.get("paginas").getAsInt();
        } else {
            paginas = 0;
        }

        String autor;
        if (contenidoJson.has("autor") && !contenidoJson.get("autor").isJsonNull()) {
            autor = contenidoJson.get("autor").getAsString();
        } else {
            autor = "";
        }


        return new Libro(titulo,autor,paginas,fecha);
    }
}
