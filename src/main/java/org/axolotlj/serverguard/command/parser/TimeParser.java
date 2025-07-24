package org.axolotlj.serverguard.command.parser;

/**
 * Analiza duraciones expresadas en segundos (s) o ticks (t).
 */
public class TimeParser {

    public static int parse(String input) {
        try {
            if (input.endsWith("t")) {
                return Integer.parseInt(input.replace("t", "")) / 20;
            } else if (input.endsWith("s")) {
                return Integer.parseInt(input.replace("s", ""));
            } else {
                return Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            return 60;
        }
    }
}
