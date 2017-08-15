package br.com.dao;

import br.com.entitys.Address;
import br.com.entitys.Client;
import br.com.entitys.Entity;
import br.com.entitys.Indication;
import br.com.entitys.PaymentVoucher;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.IndicationFilter;
import static br.com.generic.GenericDao.DATE_FORMATTER;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class IndicationDao extends GenDao<Indication, IndicationFilter> {

    protected IndicationDao(final Connection connection) {
        super(connection, Indication.class);
    }

    @Override
    public List<Indication> list(final IndicationFilter filter) throws IllegalAccessException, InstantiationException,
            SQLException, ClassNotFoundException, IOException {

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(Indication.TABLE_NAME);

        sql.append(" JOIN  ")
                .append(User.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.USER));
        sql.append(" JOIN  ")
                .append(Client.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.CLIENT));
        sql.append(" LEFT JOIN  ")
                .append(Address.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Address.TABLE_NAME, Address.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.ADDRESS));
        sql.append(" LEFT JOIN  ")
                .append(PaymentVoucher.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(PaymentVoucher.TABLE_NAME, PaymentVoucher.Columns.ID))
                .append(" = ")
                .append(Indication.fullColumn(Indication.TABLE_NAME, Indication.Columns.PAYMENT_VOUCHER));

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<Indication> entities = new LinkedList<>();

        while (query.next()) {

            final PaymentVoucher paymentVoucher = (PaymentVoucher) SQLUtils.entityPopulate(query, PaymentVoucher.class);
            final Address address = (Address) SQLUtils.entityPopulate(query, Address.class);
            final User user = (User) SQLUtils.entityPopulate(query, User.class);
            final Client client = (Client) SQLUtils.entityPopulate(query, Client.class);
            final Indication indication = (Indication) SQLUtils.entityPopulate(query, Indication.class);

            if (paymentVoucher != null) {
                indication.setPaymentVoucher(paymentVoucher);
            }
            if (address != null) {
                indication.setAddress(address);
            }
            if (user != null) {
                user.setAddress(address);
                indication.setUser(user);
            }
            if (client != null) {
                client.setAddress(address);
                client.setUser(user);
                indication.setClient(client);
            }

            entities.add(indication);
        }
        return entities;
    }

    @Override
    public Query getQuery(final IndicationFilter filter, final String... colsToReturn) throws IOException {

        final String returns = PersistenceUtils.concat(Indication.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(connection);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(Indication.TABLE_NAME);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            sql.append(" AND ( ");
            sql.append(" (lower( ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.NAME)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.DOCUMENT)).append(") LIKE ?) ");
            sql.append(" OR (").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.PHONE)).append("::TEXT LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.PERSON_TYPE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.STATUS)).append(") LIKE ?) ");
            sql.append(" ) ");

            for (int i = 0, paransCount = 5; i < paransCount; i++) {
                query.addParam("%" + filter.getText().toLowerCase() + "%");
            }
        }

        if (filter.getType() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.PERSON_TYPE)).append(" = ? ");
            query.addParam(filter.getType().toString());
        }

        if (filter.getDocument() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.DOCUMENT)).append(" = ? ");
            query.addParam(filter.getDocument());
        }

        if (filter.getPhone() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.PHONE)).append(" = ? ");
            query.addParam(filter.getPhone());
        }

        if (filter.getUser() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.USER)).append(" = ? ");
            query.addParam(filter.getUser());
        }

        if (filter.getClient() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.CLIENT)).append(" = ? ");
            query.addParam(filter.getClient().toString());
        }

        if (filter.getStatus() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.STATUS)).append(" = ? ");
            query.addParam(filter.getClient());
        }

        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(Entity.fullColumn(Indication.TABLE_NAME, Indication.Columns.ID));
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
