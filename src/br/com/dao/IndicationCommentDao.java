package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.IndicationComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.IndicationCommentFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class IndicationCommentDao extends GenDAO<IndicationComment, IndicationCommentFilter> {

    public IndicationCommentDao(final Connection connection) {
        super(connection, IndicationComment.class);
    }

    @Override
    public List<IndicationComment> list(IndicationCommentFilter filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Query getQuery(IndicationCommentFilter filter, String... colsToReturn) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
