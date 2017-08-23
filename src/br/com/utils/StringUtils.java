package br.com.utils;

import java.text.Normalizer;
import java.util.List;

public class StringUtils {

    private static String delAllAccentuation(String string) {

        if (string == null || string.trim().isEmpty()) {
            return string;
        }
        return Normalizer.normalize(
                string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String delAllDifferentOf(final String string, final String diferentOf) {

        if (string == null || string.trim().isEmpty()) {
            return string;
        }
        if (diferentOf != null && !diferentOf.trim().isEmpty()) {
            return string.replaceAll(diferentOf, "");
        }
        return "";
    }

    public static String delAllSpaces(String string) {

        if (string == null || string.trim().isEmpty()) {
            return string;
        }
        return string.replaceAll("\\s+", "");
    }

    public static String leaveOnlyOneSpace(String string) {

        if (string == null || string.trim().isEmpty()) {
            return string;
        }
        return string.replaceAll("[\\s]+", " ");
    }

    private static String getValueFrom(final List<String> listChaves, final String string) {

        String valor = "";
        int tamanhoChave = 0;

        for (String chaveValue : listChaves) {

            int posicaoInicial = string.toUpperCase().lastIndexOf(chaveValue.toUpperCase());

            if (posicaoInicial > -1) {
                int chaveValueLenght = chaveValue.length();
                if (chaveValueLenght > tamanhoChave) {
                    String value = getValueFrom(posicaoInicial + chaveValueLenght, string);
                    if (!value.trim().isEmpty() && !listChaves.contains(value)) {
                        tamanhoChave = chaveValueLenght;
                        valor = value;
                    }
                }
            }
        }
        return valor;
    }

    private static String getValueFrom(int posicaoInicial, String string) {

        String value = string.substring(posicaoInicial, string.length()).trim();
        value = delAllDifferentOf(value, "[^a-zA-Z0-9 ]+").trim();

        if (value.contains(" ")) {
            return value.substring(0, value.indexOf(" ")).trim();
        }
        return value;
    }

}
