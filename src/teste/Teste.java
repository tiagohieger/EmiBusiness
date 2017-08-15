package teste;

import br.com.dao.GenDao;
import br.com.rn.GenRn;
import br.com.entitys.Address;
import br.com.entitys.Alert;
import br.com.scripts.Scripts;
import br.com.types.ConnectionType;
import br.com.utils.SQLUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teste {

    private static void resetDataBase() {
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

    private static void instanceDaoTnTest() {

        final GenDao alertDao = GenDao.newInstance(Alert.class, null);
        final GenDao addressDao = GenDao.newInstance(Address.class, null);

        System.out.println("alertDao: " + alertDao.getClass().getName());
        System.out.println("addressDao: " + addressDao.getClass().getName());

        final GenRn alertRn = GenRn.newInstance(Alert.class);
        final GenRn addressRn = GenRn.newInstance(Address.class);

        System.out.println("alertRn: " + alertRn.getClass().getName());
        System.out.println("addressRn: " + addressRn.getClass().getName());
    }

    public static void main(String[] args) {
        instanceDaoTnTest();
    }

}
