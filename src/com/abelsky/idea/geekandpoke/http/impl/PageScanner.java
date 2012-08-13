package com.abelsky.idea.geekandpoke.http.impl;

import com.abelsky.idea.geekandpoke.entries.EntryInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for parsing Geek&Poke site page.
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
    public List<EntryInfo> update(int pageNumber) {
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

        final List<EntryInfo> entries = new ArrayList<EntryInfo>();
        while (idMatcher.find() & dateMatcher.find() & titleMatcher.find() & imageMatcher.find()) {
            try {
                final String id = idMatcher.group(1);
                final String title = titleMatcher.group(2);
                final Date pubDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).parse(dateMatcher.group(1));
                final URL permLink = new URL(titleMatcher.group(1));
                final URL imageUrl = new URL(imageMatcher.group(0));
                final EntryInfo entry = new EntryInfo(id, title, pubDate, permLink, imageUrl);

                entries.add(entry);

            } catch (ParseException e) {
                log.error("Cannot parse entry", e);
            } catch (MalformedURLException e) {
                log.error("Cannot parse entry", e);
            }
        }

        return entries;
    }

    private String fetchPage(int pageNumber) throws IOException {
        log.assertTrue(pageNumber >= 0);

        final URL url = new URL(pageNumber == 0 ? BASE_URL : BASE_URL + "/page/" + pageNumber + "/");

        // Ensure that proxy (if any) is set up for this request.
        final HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        httpConfigurable.prepareURL(url.toExternalForm());

        if (log.isDebugEnabled()) {
            log.debug("Fetching page #" + pageNumber + " from \"" + url + "\"");
        }

        final InputStream stream = url.openStream();
        try {
            return StreamUtil.readText(stream);
        } finally {
            stream.close();
        }
    }

    public static void main(String[] args) {
        System.out.println(new PageScanner().update(0));
    }
}

