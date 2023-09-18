package org.example.model.ML.thread;


import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.example.data.entity.ESpellingVariants;
import org.example.data.mydata.DDocData;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TWaitDictionaryWordsFind implements Runnable {

    public static class WordFind {

        public Boolean isWord;
        public Integer id;
        public String word;

        WordFind(String word, Boolean isWord) {
            this.word = word;
            this.isWord = isWord;
        }
    }

    public Thread getThread() {
        return thread;
    }

    private final Thread thread;

    private String message = "";
    private IFileUtils fileUtils = new FileUtils();
    private ArrayList<Map<String, Object>> columns;
    Consumer<String> updateDocCallback;
    ArrayList<ESpellingVariants> eSpellingVariants;


    public TWaitDictionaryWordsFind(String threadName, ArrayList<Map<String, Object>> columns, ArrayList<ESpellingVariants> eSpellingVariants, Consumer<String> updateDocCallback) {
        this.columns = columns;
        this.updateDocCallback = updateDocCallback;
        this.eSpellingVariants = eSpellingVariants;
        thread = new Thread(this, threadName);
    }

    @Override
    public void run() {
        try {

            finding();

            if (!message.isEmpty()) {
                try {
                    fileUtils.logs(message);
                } catch (Exception e) {
                    System.out.println(message);
                }
            }
        } catch (Exception e) {
            System.out.println("|Ошибка потока " + thread.getName() + ": " + e.getMessage());
        }
    }

    public void finding() {

        String messageText;
        Jsonb jsonb = JsonbBuilder.create();
        try {
            DDocData dDocData = new DDocData();
            ArrayList<DDocData.Data> allData = new ArrayList<>();

            columns.forEach(column -> {

                String text = (String) column.get("comment");
                int index = (int) column.get("number");


                ArrayList<WordFind> words = new ArrayList<>();

                Pattern pattern = Pattern.compile("([a-zA-Zа-яёА-ЯЁ]+)([^a-zA-Zа-яёА-ЯЁ]+)");
                Matcher matcher = pattern.matcher(text);

                while (matcher.find()) {
                    words.add(new WordFind(matcher.group(1), true));
                    words.add(new WordFind(matcher.group(2), false));
                }

                words.forEach((word -> {
                    if (!word.isWord)
                        return;

                    eSpellingVariants.stream().filter(eSpelling ->
                            word.word.equals(eSpelling.getWord().toLowerCase())
                    ).findAny().ifPresent(spelling -> word.id = spelling.getId());

                }));


                DDocData.Data data = new DDocData.Data();

                data.setType("analysis");
                data.setIndex(String.valueOf(index));
                data.setAnalysisText(jsonb.toJson(words));

                allData.add(data);

            });


            dDocData.setData(allData.toArray(DDocData.Data[]::new));

            updateDocCallback.accept(jsonb.toJson(dDocData));

        } catch (Exception e) {
            messageText = "|Ошибка потока " + thread.getName() + ": " + e.getMessage();
            System.out.println(messageText);
            message = messageText;
        }
    }
}