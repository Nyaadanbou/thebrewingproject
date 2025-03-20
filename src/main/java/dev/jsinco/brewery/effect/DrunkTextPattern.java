package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.util.Pair;

import java.util.Locale;
import java.util.Random;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record DrunkTextPattern(Pattern pattern, String text, int percentage, int alcohol) implements DrunkTextElement {

    private static final Random RANDOM = new Random();

    @Override
    public String transform(String initial) {
        Matcher matcher = pattern.matcher(initial);
        return matcher.replaceAll(this::transformWithMatch);
    }

    private String transformWithMatch(MatchResult matchResult) {
        if (percentage < RANDOM.nextInt(0, 101)) {
            return matchResult.group();
        }
        boolean isNotCaps = matchResult.group().equals(matchResult.group().toLowerCase(Locale.ROOT));
        String text = !isNotCaps ? text().toUpperCase(Locale.ROOT) : text().toLowerCase(Locale.ROOT);
        if (matchResult.groupCount() > 0) {
            Pair<Integer, Integer> range = findRange(matchResult);
            String string = matchResult.group();
            return string.substring(0, range.first()) + text + string.substring(range.second());
        }
        return text;
    }

    private Pair<Integer, Integer> findRange(MatchResult matchResult) {
        int matchStart = matchResult.start();
        int start = 0;
        int end = matchResult.end() - matchStart;
        for (int i = 1; i < matchResult.groupCount() + 1; i++) {
            int groupEnd = matchResult.end(i) - matchStart;
            int groupStart = matchResult.start(i) - matchStart;
            if (groupStart <= start) {
                start = groupEnd;
            } else if (groupEnd >= end) {
                end = groupStart;
            }
        }
        return new Pair<>(start, end);
    }
}
