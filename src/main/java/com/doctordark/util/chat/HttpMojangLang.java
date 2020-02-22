package com.doctordark.util.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class HttpMojangLang extends MojangLang {

    //TODO: Customisable..
    private static final String HASH_17 = "03f31164d234f10a3230611656332f1756e570a9";

    @Override
    public void index(String minecraftVersion, Locale locale) throws IllegalArgumentException, IOException {
        super.index(minecraftVersion, locale);

        if (HASH_17.length() < 2) {
            return;
        }

        String url = "http://resources.download.minecraft.net/" + HASH_17.substring(0, 2) + "/" + HASH_17;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            this.finallyIndex(locale, reader);
        }
    }
}
