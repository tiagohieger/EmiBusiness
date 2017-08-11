package br.com.scripts;

import java.util.LinkedList;
import java.util.List;

public class Scripts {

    private static final String TRIGGERS_PACKAGE = "br/com/scripts/triggers/"; // não usar ponto, usar sempre barra

    public static List<String> listScripts() {

        final List<String> list = new LinkedList<>();
        list.add(TRIGGERS_PACKAGE + "tg_indications.sql");

        return list;
    }

}
