package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.UserFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserDao extends GenDAO<User, UserFilter> {

    public UserDao(final Connection connection) {
        super(connection, User.class);
    }

    @Override
    public List<User> list(UserFilter filter) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Query getQuery(UserFilter filter, String... colsToReturn) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
