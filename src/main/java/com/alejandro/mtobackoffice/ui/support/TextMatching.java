package com.alejandro.mtobackoffice.ui.support;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.Locale;

/**
 * Como se comparan los textos de una lista que ya esta entera en pantalla (los catalogos, los perfiles
 * y los roles de cliente), igual que en mto-frontend: sin distinguir mayusculas ni tildes («seccion»
 * encuentra «Seccion» y «Sección»), y en orden natural (PT2 antes que PT10).
 */
public final class TextMatching {

    /** Orden natural sin mirar mayusculas ni tildes; {@code null} cuenta como vacio. */
    public static final Comparator<String> NATURAL_ORDER = (left, right) -> compareNatural(fold(left), fold(right));

    private TextMatching() {
    }

    /** El texto sin tildes y en minusculas, para comparar. */
    public static String fold(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    /** Si {@code value} contiene {@code needle}, sin mirar mayusculas ni tildes. Lo vacio esta en todo. */
    public static boolean contains(String value, String needle) {
        String folded = fold(needle).trim();
        return folded.isEmpty() || fold(value).contains(folded);
    }

    private static int compareNatural(String left, String right) {
        int i = 0;
        int j = 0;
        while (i < left.length() && j < right.length()) {
            boolean digits = Character.isDigit(left.charAt(i));
            if (digits != Character.isDigit(right.charAt(j))) {
                return Character.compare(left.charAt(i), right.charAt(j));
            }
            int startLeft = i;
            int startRight = j;
            while (i < left.length() && Character.isDigit(left.charAt(i)) == digits) {
                i++;
            }
            while (j < right.length() && Character.isDigit(right.charAt(j)) == digits) {
                j++;
            }
            String chunkLeft = left.substring(startLeft, i);
            String chunkRight = right.substring(startRight, j);
            int compared = digits
                    ? new BigInteger(chunkLeft).compareTo(new BigInteger(chunkRight))
                    : chunkLeft.compareTo(chunkRight);
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(left.length() - i, right.length() - j);
    }
}
