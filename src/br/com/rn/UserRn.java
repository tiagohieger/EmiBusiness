package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.UserDao;
import br.com.entitys.Address;
import br.com.entitys.Bank;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.generic.GenericDAO;

public class UserRn extends GenRn {

    public User save(final User user) throws Throwable {
        final Connection connection = DB.getConnection();
        try {
            return save(user, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected User save(final User user, final Connection connection) throws Throwable {

        final UserDao dao = new UserDao(connection, User.class);
        dao.save(user);

        if (user.getAddress() != null) {
            final GenericDAO<Address> genDao = new GenericDAO<>(connection, Address.class);
            genDao.save(user.getAddress());
        }

        if (user.getBank() != null) {
            final GenericDAO<Bank> genDao = new GenericDAO<>(connection, Bank.class);
            genDao.save(user.getBank());
        }

        return user;
    }

}
