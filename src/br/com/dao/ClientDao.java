package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.Address;
import br.com.entitys.Client;
import br.com.entitys.Entity;
import br.com.entitys.Product;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.ClientFilter;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ClientDao extends GenDAO<Client, ClientFilter> {

    public ClientDao(final Connection connection) {
        super(connection, Client.class);
    }

    @Override
    public List<Client> list(final ClientFilter filter) throws IllegalAccessException, InstantiationException,
            SQLException, ClassNotFoundException, IOException {

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(Client.TABLE_NAME);

        sql.append(" JOIN  ")
                .append(Address.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Address.TABLE_NAME, Address.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.ADDRESS));
        sql.append(" JOIN  ")
                .append(User.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.USER));
        sql.append(" LEFT JOIN  ")
                .append(Product.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Product.TABLE_NAME, Product.Columns.CLIENT))
                .append(" = ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.ID));

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<Client> entities = new LinkedList<>();

        while (query.next()) {

            final Product product = (Product) SQLUtils.entityPopulate(query, Product.class);
            Client client = (Client) SQLUtils.entityPopulate(query, Client.class);

            if (entities.contains(client)) {

                client = entities.get(entities.indexOf(client));

                if (product != null && !client.getProducts().contains(product)) {
                    product.setClient(client);
                    client.getProducts().add(product);
                }

            } else {

                final Address address = (Address) SQLUtils.entityPopulate(query, Address.class);
                final User user = (User) SQLUtils.entityPopulate(query, User.class);

                if (address != null) {
                    client.setAddress(address);
                }
                if (user != null) {
                    client.setUser(user);
                }
                if (product != null) {
                    product.setClient(client);
                    client.getProducts().add(product);
                }

                entities.add(client);
            }
        }
        return entities;
    }

    @Override
    public Query getQuery(final ClientFilter filter, final String... colsToReturn) throws IOException {

        final String returns = PersistenceUtils.concat(Client.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(conexao);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(Client.TABLE_NAME);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            sql.append(" AND ( ");
            sql.append(" (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.NAME)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.CONTACT)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.DOCUMENT)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.EMAIL)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.FANTASY)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.PHONE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.PHONE_TYPE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.PERSON_TYPE)).append(") LIKE ?) ");
            sql.append(" ) ");

            for (int i = 0, paransCount = 8; i < paransCount; i++) {
                query.addParam("%" + filter.getText().toLowerCase() + "%");
            }
        }

        if (filter.getType() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.PERSON_TYPE)).append(" = ? ");
            query.addParam(filter.getType().toString());
        }

        if (filter.getDocument() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.DOCUMENT)).append(" = ? ");
            query.addParam(filter.getDocument());
        }

        if (filter.getIsActive() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.IS_ACTIVE)).append(" = ? ");
            query.addParam(filter.getIsActive());
        }

        if (filter.getPhone() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.PHONE)).append(" = ? ");
            query.addParam(filter.getPhone());
        }

        if (filter.getUser() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.USER)).append(" = ? ");
            query.addParam(filter.getUser());
        }

        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(Client.fullColumn(Client.TABLE_NAME, Client.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Client.fullColumn(Client.TABLE_NAME, Client.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(Client.fullColumn(Client.TABLE_NAME, Client.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Client.fullColumn(Client.TABLE_NAME, Client.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(Client.fullColumn(Client.TABLE_NAME, Client.Columns.ID));
        } else {
            sql.append(" GROUP BY ");
            sql.append(filter.getGroupBy());
        }

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        if (filter.getLimit() != null) {
            sql.append(" LIMIT ? ");
            query.addParam(filter.getLimit());
        }

        if (filter.getOffset() != null) {
            sql.append(" OFFSET ? ");
            query.addParam(filter.getOffset());
        }

        query.addSql(sql.toString().replaceFirst("AND", "WHERE"));

        return query;
    }

}
