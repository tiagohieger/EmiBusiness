package br.com.base;

import br.com.constants.HasFilter;
import br.com.entitys.Entity;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.EntityFilter;
import br.com.generic.GenericDAO;
import br.com.utils.PersistenceUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public abstract class GenDAO<E extends Entity, F extends EntityFilter> extends GenericDAO<E> implements HasFilter<E, F> {

    public GenDAO(Connection connection, Class especificClass) {
        super(connection, especificClass);
    }

    @Override
    public int count(F filter) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {

        final String columnName = PersistenceUtils.columnName(PersistenceUtils.getIdField(classe));
        final String tableName = PersistenceUtils.tableName(classe);
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
    public abstract List<E> list(F filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException;

    @Override
    public abstract Query getQuery(F filter, String... colsToReturn) throws IOException;
}
