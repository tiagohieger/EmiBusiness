package br.com.dao;

import br.com.entitys.IndicationComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.IndicationCommentFilter;
import java.util.List;

public class IndicationCommentDao extends GenDao<IndicationComment, IndicationCommentFilter> {

    protected IndicationCommentDao(final Connection connection) {
        super(connection, IndicationComment.class);
    }

    @Override
    public List<IndicationComment> list(IndicationCommentFilter filter) throws Throwable {
        return super.list(filter);
    }

    @Override
    public Query getQuery(IndicationCommentFilter filter, String... colsToReturn) throws Throwable {
        return super.getQuery(filter, colsToReturn);
    }

}
