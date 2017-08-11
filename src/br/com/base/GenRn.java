package br.com.base;

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

}
