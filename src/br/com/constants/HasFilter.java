package br.com.constants;

import br.com.entitys.Entity;
import br.com.factory.Query;
import br.com.filters.EntityFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface HasFilter<E extends Entity, F extends EntityFilter> {

    public List<E> list(F filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException;

    public int count(F filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException;

    public Query getQuery(F filter, String... colsToReturn) throws IOException;

}
