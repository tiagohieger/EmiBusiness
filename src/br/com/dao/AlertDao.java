package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.Alert;
import br.com.entitys.Bank;
import br.com.entitys.Entity;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.AlertFilter;
import static br.com.generic.GenericDAO.DATE_FORMATTER;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AlertDao extends GenDAO<Alert, AlertFilter> {

    public AlertDao(final Connection connection) {
        super(connection, Alert.class);
    }

    @Override
    public List<Alert> list(final AlertFilter filter) throws IllegalAccessException, InstantiationException, SQLException,
            ClassNotFoundException, IOException {

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(Alert.TABLE_NAME);

        sql.append("  JOIN  ")
                .append(User.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Alert.TABLE_NAME, Alert.Columns.USER));

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<Alert> entities = new LinkedList<>();

        while (query.next()) {

            final User user = (User) SQLUtils.entityPopulate(query, User.class);
            final Alert alert = (Alert) SQLUtils.entityPopulate(query, Alert.class);

            if (user != null) {
                alert.setUser(user);
            }

            entities.add(alert);
        }
        
        return entities;
    }

    @Override
    public Query getQuery(final AlertFilter filter, final String... colsToReturn) throws IOException {

        final String returns = PersistenceUtils.concat(Alert.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(conexao);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(Alert.TABLE_NAME);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            sql.append(" AND ( ");
            sql.append(" (lower( ").append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.TEXT)).append(") LIKE ?) ");
            sql.append(" ) ");

            for (int i = 0, paransCount = 1; i < paransCount; i++) {
                query.addParam("%" + filter.getText().toLowerCase() + "%");
            }
        }

        if (filter.getUser() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Alert.TABLE_NAME, Alert.Columns.USER)).append(" = ? ");
            query.addParam(filter.getUser());
        }

        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(Alert.fullColumn(Alert.TABLE_NAME, Alert.Columns.ID));
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
