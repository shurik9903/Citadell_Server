package org.example.model.dictionary;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.entity.ESimpleWord;
import org.example.data.entity.ESpellingVariants;
import org.example.data.entity.ETypeWord;
import org.example.data.mydata.DWord;
import org.example.model.database.dictionaryWork.IDBDictionaryWork;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dictionary implements IDictionary {

    @Inject
    private IDBDictionaryWork dataBase;

    @Override
    public Response loadSimpleWord(String wordID){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ESimpleWord eSimpleWord = dataBase.loadSimpleWord(wordID);
            if (eSimpleWord == null)
                throw new Exception("Данное слово не найдено");

            Result.put("word", eSimpleWord.getWord());

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    @Override
    public Response loadSimpleWords() {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ArrayList<ESimpleWord> eSimpleWords = dataBase.loadSimpleWords();

            Result.put("words", jsonb.toJson(eSimpleWords));

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response saveSimpleWord(String word) {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            DWord dWord =  jsonb.fromJson(word, DWord.class);

            dataBase.saveSimpleWord(dWord.getWord(), dWord.getTypeID(), dWord.getDescription());

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateSimpleWord(String word) {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            DWord dWord =  jsonb.fromJson(word, DWord.class);

            System.out.println("test " + dWord.getId() + " " + dWord.getWord() + " " + dWord.getDescription() + " " + dWord.getTypeID());

            dataBase.updateSimpleWord(dWord.getId(), dWord.getWord(), dWord.getTypeID(), dWord.getDescription());

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteSimpleWord(int wordID){
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            dataBase.deleteSimpleWord(wordID);

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response loadSpellingWord(String wordID){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ESpellingVariants eSpellingVariants = dataBase.loadSpellingWord(wordID);
            if (eSpellingVariants == null)
                throw new Exception("Данное слово не найдено");

            Result.put("word", eSpellingVariants.getWord());

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    @Override
    public Response loadSpellingWords() {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ArrayList<ESpellingVariants> eSpellingVariants = dataBase.loadSpellingWords();

            Result.put("words", jsonb.toJson(eSpellingVariants));

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response saveSpellingWord(String word) {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            DWord dWord =  jsonb.fromJson(word, DWord.class);

            dataBase.saveSpellingWord(dWord.getWord(), dWord.getSimpleID(), dWord.getDescription());

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response loadTypeWords() {
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> Result = new HashMap<>();

        try {
            dataBase.ping();

            ArrayList<ETypeWord> eTypeWords = dataBase.loadTypeWords();

            Result.put("words", jsonb.toJson(eTypeWords));

            return Response.ok(jsonb.toJson(Result)).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateSpellingWord(String word) {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            DWord dWord =  jsonb.fromJson(word, DWord.class);

            dataBase.updateSpellingWord(dWord.getId(), dWord.getWord(), dWord.getDescription());

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteSpellingWord(int wordID){
        Jsonb jsonb = JsonbBuilder.create();

        try {
            dataBase.ping();

            dataBase.deleteSpellingWord(wordID);

            return Response.ok(jsonb.toJson("ok")).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}

