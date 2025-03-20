package io.demo.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class BookUtils {
    private static final String SEP = "(?:\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";
    private static final String ISBN10_REGEX =
            "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";
    private static final String ISBN13_REGEX =
            "^(978|979)(?:(\\d{10})|(?:"
                    + SEP
                    + GROUP
                    + SEP
                    + PUBLISHER
                    + SEP
                    + TITLE
                    + SEP
                    + "([0-9])))$";

    private static final Pattern isbn10Pattern = Pattern.compile(ISBN10_REGEX);
    private static final Pattern isbn13Pattern = Pattern.compile(ISBN13_REGEX);

    public boolean isValid(String value) {
        return isbn10Pattern.matcher(value).matches() || isbn13Pattern.matcher(value).matches();
    }
}
