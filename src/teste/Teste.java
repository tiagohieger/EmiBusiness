package teste;

import br.com.scripts.Scripts;
import br.com.types.ConnectionType;
import br.com.utils.SQLUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teste {

    public static void main(String[] args) {

        try {
            SQLUtils.resetDataBase(true, Teste.class.getClassLoader(),
                    "postgres",
                    "trimoutec@123",
                    "192.168.2.7",
                    5432,
                    "teste_tiago",
                    ConnectionType.POSTGRES,
                    "br.com.entitys",
                    Scripts.listScripts());
        } catch (Exception ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
