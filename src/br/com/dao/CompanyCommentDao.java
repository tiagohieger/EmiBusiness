package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.CompanyComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.CompanyCommentFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CompanyCommentDao extends GenDAO<CompanyComment, CompanyCommentFilter> {

    public CompanyCommentDao(final Connection connection) {
        super(connection, CompanyComment.class);
    }

    @Override
    public List<CompanyComment> list(CompanyCommentFilter filter) throws IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Query getQuery(CompanyCommentFilter filter, String... colsToReturn) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
