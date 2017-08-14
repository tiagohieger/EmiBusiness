package br.com.rn;

import br.com.base.GenDao;
import br.com.base.GenRn;
import br.com.dao.CompanyCommentDao;
import br.com.entitys.CompanyComment;
import br.com.factory.Connection;
import br.com.filters.CompanyCommentFilter;
import java.util.List;

public class CompanyCommentRn extends GenRn {

    public List<CompanyComment> list(final CompanyCommentFilter ccFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(ccFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<CompanyComment> list(final CompanyCommentFilter ccFilter, final Connection connection) throws Throwable {

        final CompanyCommentDao dao = GenDao.newInstance(CompanyComment.class, connection);
        return dao.list(ccFilter);
    }

    public CompanyComment save(final CompanyComment cc) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(cc, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected CompanyComment save(final CompanyComment cc, final Connection connection) throws Throwable {

        final CompanyCommentDao ccDao = GenDao.newInstance(CompanyComment.class, connection);
        return ccDao.save(cc);
    }

}
