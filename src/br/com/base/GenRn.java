package br.com.base;

import br.com.entitys.Entity;
import br.com.factory.ConnectionManage;
import br.com.types.ConnectionType;

public class GenRn {

    protected static final ConnectionManage DB = ConnectionManage.newInstance();

    private final static ConnectionType CONNECTION_TYPE = ConnectionType.POSTGRES;
    private final static String PASSWORD = "trimoutec@123";
    private final static String HOST = "192.168.2.7";
    private final static String BASE_NAME = "teste_tiago";
    private final static String SCHEMA = "public";
    private final static String USER = "postgres";
    private final static Integer PORT = 5432;

    static {
        DB.setConnectionData(HOST, PORT, USER, PASSWORD, BASE_NAME, SCHEMA, CONNECTION_TYPE);
    }
    
    public static <RN extends GenRn, E extends Entity> RN newInstance(Class<E> entity) {

        // Monta o caminho da Dao
        final StringBuilder classPath = new StringBuilder("br.com.rn.");
        classPath.append(entity.getSimpleName());
        classPath.append("Rn");

        try {

            // Carrega a classe da Rn especifia da classe passada
            final Class rnClass = GenDao.class.getClassLoader().loadClass(classPath.toString());

            // Cria uma instância da Rn
            return (RN) rnClass.newInstance();

        } catch (ClassNotFoundException | IllegalAccessException | 
                IllegalArgumentException | InstantiationException | SecurityException ignore) {
            
            // Como não foi encontrada uma Rn específica retorna uma genérica
            return (RN) new GenRn();
            
        }

    }

}
