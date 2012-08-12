package com.abelsky.idea.geekandpoke.http;

import com.abelsky.idea.geekandpoke.Util;
import com.abelsky.idea.geekandpoke.entries.OnlineEntry;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NonNls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for parsing Geek&Poke RSS feed.
 *
 * @author andy
 */
class PageScanner {

    @NonNls
    private static final String BASE_URL = "http://geekandpoke.typepad.com/";

    private final Logger log = Logger.getInstance(getClass());

    private final Pattern idPattern = Pattern.compile("<div class=\"entry-author-oliver_widder entry-type-post entry\" id=\"([^<>]+)\">");
    private final Pattern datePattern = Pattern.compile("<h2 class=\"date-header\">([^<>/]+)</h2>");
    private final Pattern titlePattern = Pattern.compile("<h3 class=\"entry-header\"><a href=\"([^<>]+)\">([^<>]+)</a></h3>");
    private final Pattern imagePattern = Pattern.compile("http://geekandpoke\\.typepad\\.com/.a/([\\w\\d]+-800wi)");

    /**
     * @return The list of updated entries (may be empty).
     */
    public List<OnlineEntry> update(int pageNumber) {
        final String contents;
        try {
            contents = fetchPage(pageNumber);
        } catch (IOException e) {
            log.warn("Cannot fetch RSS feed", e);
            return Collections.emptyList();
        }

        final Matcher idMatcher = idPattern.matcher(contents);
        final Matcher dateMatcher = datePattern.matcher(contents);
        final Matcher titleMatcher = titlePattern.matcher(contents);
        final Matcher imageMatcher = imagePattern.matcher(contents);

        final List<OnlineEntry> entries = new ArrayList<OnlineEntry>();
        while (idMatcher.find() & dateMatcher.find() & titleMatcher.find() & imageMatcher.find()) {
            try {
                Date pubDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).parse(dateMatcher.group(1));
                final OnlineEntry entry = new OnlineEntry(idMatcher.group(1), titleMatcher.group(2), pubDate, titleMatcher.group(1), imageMatcher.group(0));
                entries.add(entry);
            } catch (ParseException e) {
                log.error("Cannot parse entry", e);
            }
        }

        return entries ;
    }

    private String fetchPage(int pageNumber) throws IOException {
        assert pageNumber >= 0;

        final URL url = new URL(pageNumber == 0 ? BASE_URL : BASE_URL + "/page/" + pageNumber + "/");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        try {
            return Util.readToString(reader);
        } finally {
            reader.close();
        }
    }

    public static void main(String[] args) {
        System.out.println(new PageScanner().update(0));
    }
}

