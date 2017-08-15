package br.com.dao;

import br.com.entitys.IndicationComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.IndicationCommentFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class IndicationCommentDao extends GenDao<IndicationComment, IndicationCommentFilter> {

    protected IndicationCommentDao(final Connection connection) {
        super(connection, IndicationComment.class);
    }

    @Override
    public List<IndicationComment> list(IndicationCommentFilter filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException {
        return super.list(filter);
    }

    @Override
    public Query getQuery(IndicationCommentFilter filter, String... colsToReturn) throws IOException {
        return super.getQuery(filter, colsToReturn);
    }

}
