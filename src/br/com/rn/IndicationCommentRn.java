package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.IndicationCommentDao;
import br.com.entitys.IndicationComment;
import br.com.factory.Connection;
import br.com.filters.IndicationCommentFilter;
import java.util.List;

public class IndicationCommentRn extends GenRn {

    public List<IndicationComment> list(final IndicationCommentFilter icFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(icFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<IndicationComment> list(final IndicationCommentFilter icFilter, final Connection connection) throws Throwable {

        final IndicationCommentDao dao = new IndicationCommentDao(connection);
        return dao.list(icFilter);
    }

    public IndicationComment save(final IndicationComment ic) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(ic, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected IndicationComment save(final IndicationComment ic, final Connection connection) throws Throwable {

        final IndicationCommentDao icDao = new IndicationCommentDao(connection);
        return icDao.save(ic);
    }

}
