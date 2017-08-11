package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.ClientDao;
import br.com.entitys.Address;
import br.com.entitys.Client;
import br.com.entitys.Product;
import br.com.factory.Connection;
import br.com.filters.ClientFilter;
import br.com.generic.GenericDAO;
import java.util.List;

public class ClientRn extends GenRn {

    public List<Client> list(final ClientFilter clientFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(clientFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Client> list(final ClientFilter clientFilter, final Connection connection) throws Throwable {

        final ClientDao clientDao = new ClientDao(connection, Client.class);
        return clientDao.list(clientFilter);
    }

    public Client save(final Client client) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(client, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Client save(final Client client, final Connection connection) throws Throwable {

        final ClientDao clientDao = new ClientDao(connection, Client.class);
        clientDao.save(client);

        if (client.getUser() != null) {
            final UserRn userRn = new UserRn();
            userRn.save(client.getUser(), connection);
        }

        if (client.getAddress() != null) {
            final GenericDAO<Address> genDao = new GenericDAO<>(connection, Address.class);
            genDao.save(client.getAddress());
        }

        if (!client.getProducts().isEmpty()) {
            final GenericDAO<Product> genDao = new GenericDAO<>(connection, Product.class);
            for (final Product product : client.getProducts()) {
                genDao.save(product);
            }
        }

        return client;
    }

}
