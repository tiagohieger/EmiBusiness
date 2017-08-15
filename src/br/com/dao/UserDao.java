package br.com.dao;

import br.com.entitys.Address;
import br.com.entitys.Bank;
import br.com.entitys.Entity;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.UserFilter;
import static br.com.generic.GenericDao.DATE_FORMATTER;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UserDao extends GenDao<User, UserFilter> {

    protected UserDao(final Connection connection) {
        super(connection, User.class);
    }

    @Override
    public List<User> list(final UserFilter filter) throws IllegalAccessException, InstantiationException, SQLException,
            ClassNotFoundException, IOException {

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(User.TABLE_NAME);

        sql.append(" LEFT  JOIN  ")
                .append(Bank.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Bank.TABLE_NAME, Bank.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.BANK));
        sql.append(" LEFT JOIN  ")
                .append(Address.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Address.TABLE_NAME, Address.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.ADDRESS));

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<User> entities = new LinkedList<>();

        while (query.next()) {

            final Bank bank = (Bank) SQLUtils.entityPopulate(query, Bank.class);
            final Address address = (Address) SQLUtils.entityPopulate(query, Address.class);
            final User user = (User) SQLUtils.entityPopulate(query, User.class);

            if (bank != null) {
                user.setBank(bank);
            }
            if (address != null) {
                user.setAddress(address);
            }

            entities.add(user);
        }
        return entities;

    }

    @Override
    public Query getQuery(final UserFilter filter, final String... colsToReturn) throws IOException {

        final String returns = PersistenceUtils.concat(User.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(connection);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(User.TABLE_NAME);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            sql.append(" AND ( ");
            sql.append(" (lower( ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.NAME)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.LOGIN)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.DOCUMENT)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.PHONE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.PERSON_TYPE)).append(") LIKE ?) ");
            sql.append(" ) ");

            for (int i = 0, paransCount = 5; i < paransCount; i++) {
                query.addParam("%" + filter.getText().toLowerCase() + "%");
            }
        }

        if (filter.getType() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.PERSON_TYPE)).append(" = ? ");
            query.addParam(filter.getType().toString());
        }

        if (filter.getDocument() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.DOCUMENT)).append(" = ? ");
            query.addParam(filter.getDocument());
        }

        if (filter.getLogin() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.LOGIN)).append(" = ? ");
            query.addParam(filter.getLogin());
        }

        if (filter.getPassword() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.PASSWORD)).append(" = ? ");
            query.addParam(filter.getPassword());
        }

        if (filter.getIsActive() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.IS_ACTIVE)).append(" = ? ");
            query.addParam(filter.getIsActive());
        }

        if (filter.getPhone() != null) {
            sql.append(" AND ").append(Entity.fullColumn(User.TABLE_NAME, User.Columns.PHONE)).append(" = ? ");
            query.addParam(filter.getPhone());
        }

        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(User.fullColumn(User.TABLE_NAME, User.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(User.fullColumn(User.TABLE_NAME, User.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(User.fullColumn(User.TABLE_NAME, User.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(User.fullColumn(User.TABLE_NAME, User.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(User.fullColumn(User.TABLE_NAME, User.Columns.ID));
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
