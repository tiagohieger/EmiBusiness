package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.UserDao;
import br.com.entitys.Address;
import br.com.entitys.Bank;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.filters.UserFilter;
import java.util.List;

public class UserRn extends GenRn {

    protected UserRn() {
    }

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

    public User save(User user) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            connection.startTransaction();
            user = save(user, connection);
            connection.saveTransaction();
            return user;
        } catch (Throwable ex) {
            connection.cancelTransaction();
            throw ex;
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected User save(final User user, final Connection connection) throws Throwable {

        final UserDao userDao = GenDao.newInstance(User.class, connection);

        if (user.getAddress() != null) {
            final GenDao genDao = GenDao.newInstance(Address.class, connection);
            genDao.save(user.getAddress());
        }

        userDao.save(user);

        if (!user.getBanks().isEmpty()) {
            final GenDao genDao = GenDao.newInstance(Bank.class, connection);
            for (Bank bank : user.getBanks()) {
                genDao.save(bank);
            }
        }

        return user;
    }

}
