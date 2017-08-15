package br.com.constants;

import br.com.entitys.Entity;
import br.com.factory.Query;
import br.com.filters.EntityFilter;
import java.util.List;

public interface HasFilter<E extends Entity, F extends EntityFilter> {

    public List<E> list(F filter) throws Throwable;

    public int count(F filter) throws Throwable;

    public Query getQuery(F filter, String... colsToReturn) throws Throwable;

}
