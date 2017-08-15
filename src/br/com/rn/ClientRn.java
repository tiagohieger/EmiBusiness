package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.ClientDao;
import br.com.entitys.Address;
import br.com.entitys.Client;
import br.com.entitys.Product;
import br.com.factory.Connection;
import br.com.filters.ClientFilter;
import java.util.List;

public class ClientRn extends GenRn {

    protected ClientRn() {
    }

    public List<Client> list(final ClientFilter clientFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(clientFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Client> list(final ClientFilter clientFilter, final Connection connection) throws Throwable {

        final ClientDao dao = GenDao.newInstance(Client.class, connection);
        return dao.list(clientFilter);
    }

    public Client save(Client client) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            connection.startTransaction();
            client = save(client, connection);
            connection.saveTransaction();
            return client;
        } catch (Throwable ex) {
            connection.cancelTransaction();
            throw ex;
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Client save(final Client client, final Connection connection) throws Throwable {

        final ClientDao clientDao = GenDao.newInstance(Client.class, connection);

        if (client.getUser() != null) {
            final UserRn userRn = new UserRn();
            userRn.save(client.getUser(), connection);
        }

        if (client.getAddress() != null) {
            final GenDao genDao = GenDao.newInstance(Address.class, connection);
            genDao.save(client.getAddress());
        }

        if (!client.getProducts().isEmpty()) {
            final GenDao genDao = GenDao.newInstance(Product.class, connection);
            for (final Product product : client.getProducts()) {
                genDao.save(product);
            }
        }

        clientDao.save(client);

        return client;
    }

}
