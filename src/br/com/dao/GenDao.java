package br.com.dao;

import br.com.constants.HasFilter;
import br.com.entitys.Entity;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.EntityFilter;
import br.com.generic.GenericDao;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class GenDao<E extends Entity, F extends EntityFilter> extends GenericDao<E> implements HasFilter<E, F> {

    protected GenDao(Connection connection, Class especificClass) {
        super(connection, especificClass);
    }

    protected String getTableName() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        // Lê o valor do atributo estático TABLE_NAME que estiver na classe 
        // especifica
        final String tableName = String.valueOf(specificClass.getField("TABLE_NAME").get(null));

        return tableName;
    }

    public static <DAO extends GenDao, E extends Entity> DAO newInstance(Class<E> entity, Connection connection) {

        // Monta o caminho da Dao
        final StringBuilder classPath = new StringBuilder("br.com.dao.");
        classPath.append(entity.getSimpleName());
        classPath.append("Dao");

        try {

            // Carrega a classe da Dao especifia da classe passada
            final Class daoClass = GenDao.class.getClassLoader().loadClass(classPath.toString());

            final Constructor c = daoClass.getDeclaredConstructor(Connection.class);
            c.setAccessible(true);

            // Cria uma instância da Dao
            return (DAO) c.newInstance(connection);

        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ignore) {

            // Como não foi encontrada uma Dao específica retorna uma genérica
            return (DAO) new GenDao(connection, entity);

        }

    }

    @Override
    public int count(F filter) throws Throwable {

        final String columnName = PersistenceUtils.columnName(PersistenceUtils.getIdField(specificClass));
        final String tableName = PersistenceUtils.tableName(specificClass);
        final String fullColumnName = (tableName == null ? "" : tableName + ".") + columnName;

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(");
        sql.append(fullColumnName);
        sql.append(") AS count ");
        sql.append("FROM (");
        sql.append(query.getText());
        sql.append(") AS ");
        sql.append(tableName);

        query.setText(sql.toString());
        query.open();

        if (query.next()) {
            final int count = query.getValueAsInteger("count");
            return count;
        }

        return 0;
    }

    @Override
    public List<E> list(F filter) throws Throwable {

        final String tableName = getTableName();

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(tableName);

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<E> entities = new LinkedList<>();

        while (query.next()) {
            final E entity = (E) SQLUtils.entityPopulate(query, specificClass);
            entities.add(entity);
        }

        return entities;

    }

    @Override
    public Query getQuery(F filter, String... colsToReturn) throws Throwable {

        final String tableName = getTableName();

        final String returns = PersistenceUtils.concat(tableName + ".*", colsToReturn);

        final Query query = new Query(connection);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(tableName);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(E.fullColumn(tableName, E.Columns.ID));
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
