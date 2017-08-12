package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.EvaluationDao;
import br.com.entitys.Evaluation;
import br.com.factory.Connection;
import br.com.filters.EvaluationFilter;
import java.util.List;

public class EvaluationRn extends GenRn {

    public List<Evaluation> list(final EvaluationFilter evaluationFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(evaluationFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Evaluation> list(final EvaluationFilter evaluationFilter, final Connection connection) throws Throwable {

        final EvaluationDao dao = new EvaluationDao(connection);
        return dao.list(evaluationFilter);
    }

    public Evaluation save(final Evaluation evaluation) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(evaluation, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Evaluation save(final Evaluation evaluation, final Connection connection) throws Throwable {

        final EvaluationDao evaluationDao = new EvaluationDao(connection);
        return evaluationDao.save(evaluation);
    }

}
