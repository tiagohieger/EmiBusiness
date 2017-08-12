package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.Client;
import br.com.entitys.Entity;
import br.com.entitys.Evaluation;
import br.com.entitys.User;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.EvaluationFilter;
import static br.com.generic.GenericDAO.DATE_FORMATTER;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EvaluationDao extends GenDAO<Evaluation, EvaluationFilter> {
    
    public EvaluationDao(final Connection connection) {
        super(connection, Evaluation.class);
    }
    
    @Override
    public List<Evaluation> list(EvaluationFilter filter) throws IllegalAccessException, InstantiationException,
            SQLException, ClassNotFoundException, IOException {
        
        final Query query = getQuery(filter);
        
        final StringBuilder sql = new StringBuilder();
        
        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(Evaluation.TABLE_NAME);
        
        sql.append("  JOIN  ")
                .append(User.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(User.TABLE_NAME, User.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.USER));
        sql.append("  JOIN  ")
                .append(Client.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.CLIENT));
        
        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }
        
        query.setText(sql.toString());
        
        query.open();
        
        final List<Evaluation> entities = new LinkedList<>();
        
        while (query.next()) {
            
            final User user = (User) SQLUtils.entityPopulate(query, User.class);
            final Client client = (Client) SQLUtils.entityPopulate(query, Client.class);
            final Evaluation evaluation = (Evaluation) SQLUtils.entityPopulate(query, Evaluation.class);
            
            if (user != null) {
                evaluation.setUser(user);
            }
            if (client != null) {
                evaluation.setClient(client);
            }
            
            entities.add(evaluation);
        }
        
        return entities;
    }
    
    @Override
    public Query getQuery(EvaluationFilter filter, String... colsToReturn) throws IOException {
        
        final String returns = PersistenceUtils.concat(Evaluation.TABLE_NAME + ".*", colsToReturn);
        
        final Query query = new Query(conexao);
        
        final StringBuilder sql = new StringBuilder();
        
        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(Evaluation.TABLE_NAME);
        
        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            // nothing to implement for a while
        }
        
        if (filter.getUser() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.USER)).append(" = ? ");
            query.addParam(filter.getUser());
        }
        
        if (filter.getClient() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.USER)).append(" = ? ");
            query.addParam(filter.getClient());
        }
        
        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(Evaluation.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Evaluation.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }
        
        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(Evaluation.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Evaluation.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }
        
        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(Evaluation.fullColumn(Evaluation.TABLE_NAME, Evaluation.Columns.ID));
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
