package br.com.rn;

import br.com.base.GenDao;
import br.com.base.GenRn;
import br.com.dao.UserDao;
import br.com.entitys.Address;
import br.com.entitys.Bank;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.filters.UserFilter;
import java.util.List;

public class UserRn extends GenRn {

    public List<User> list(final UserFilter userFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(userFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<User> list(final UserFilter userFilter, final Connection connection) throws Throwable {

        final UserDao dao = GenDao.newInstance(User.class, connection);
        return dao.list(userFilter);
    }

    public User save(final User user) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(user, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected User save(final User user, final Connection connection) throws Throwable {

        final UserDao userDao = new UserDao(connection);
        
        if (user.getAddress() != null) {
            final GenDao genDao = GenDao.newInstance(Address.class, connection);
            genDao.save(user.getAddress());
        }

        if (user.getBank() != null) {
            final GenDao genDao = GenDao.newInstance(Bank.class, connection);
            genDao.save(user.getBank());
        }
        
        userDao.save(user);

        return user;
    }

}
