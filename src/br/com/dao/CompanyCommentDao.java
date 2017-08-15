package br.com.dao;

import br.com.entitys.CompanyComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.CompanyCommentFilter;
import java.util.List;

public class CompanyCommentDao extends GenDao<CompanyComment, CompanyCommentFilter> {

    protected CompanyCommentDao(final Connection connection) {
        super(connection, CompanyComment.class);
    }

    @Override
    public List<CompanyComment> list(CompanyCommentFilter filter) throws Throwable {
        return super.list(filter);
    }

    @Override
    public Query getQuery(CompanyCommentFilter filter, String... colsToReturn) throws Throwable {
        return super.getQuery(filter, colsToReturn);
    }

}
