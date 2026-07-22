package com.example.doan1;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslationHelper {

    public interface OnTranslationListener {
        void onTranslationSuccess(String translatedText);
        void onTranslationFailure(Exception e);
    }

    private static Translator translator;

    private static void initTranslator() {
        if (translator == null) {
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                    .build();
            translator = Translation.getClient(options);
        }
    }

    public static void translate(String text, OnTranslationListener listener) {
        if (text == null || text.trim().isEmpty()) {
            listener.onTranslationSuccess(text);
            return;
        }

        initTranslator();

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    translator.translate(text)
                            .addOnSuccessListener(listener::onTranslationSuccess)
                            .addOnFailureListener(listener::onTranslationFailure);
                })
                .addOnFailureListener(listener::onTranslationFailure);
    }

    public static void translateMultiLine(String text, OnTranslationListener listener) {
        if (text == null || text.trim().isEmpty()) {
            listener.onTranslationSuccess(text);
            return;
        }

        String[] lines = text.split("\n");
        final String[] translatedLines = new String[lines.length];
        final int[] completedCount = {0};

        for (int i = 0; i < lines.length; i++) {
            final int index = i;
            if (lines[i].trim().isEmpty()) {
                translatedLines[index] = "";
                completedCount[0]++;
                checkCompletion(lines.length, completedCount[0], translatedLines, listener);
                continue;
            }

            translate(lines[i], new OnTranslationListener() {
                @Override
                public void onTranslationSuccess(String translatedText) {
                    translatedLines[index] = translatedText;
                    completedCount[0]++;
                    checkCompletion(lines.length, completedCount[0], translatedLines, listener);
                }

                @Override
                public void onTranslationFailure(Exception e) {
                    translatedLines[index] = lines[index]; // Fallback to original
                    completedCount[0]++;
                    checkCompletion(lines.length, completedCount[0], translatedLines, listener);
                }
            });
        }
    }

    private static synchronized void checkCompletion(int total, int current, String[] results, OnTranslationListener listener) {
        if (current == total) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < results.length; i++) {
                sb.append(results[i]);
                if (i < results.length - 1) sb.append("\n");
            }
            listener.onTranslationSuccess(sb.toString());
        }
    }
}
