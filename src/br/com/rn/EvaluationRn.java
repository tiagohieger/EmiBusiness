package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.EvaluationDao;
import br.com.entitys.Evaluation;
import br.com.factory.Connection;
import br.com.filters.EvaluationFilter;
import java.util.List;

public class EvaluationRn extends GenRn {
    
    protected EvaluationRn() {
    }

    public List<Evaluation> list(final EvaluationFilter evaluationFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(evaluationFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Evaluation> list(final EvaluationFilter evaluationFilter, final Connection connection) throws Throwable {

        final EvaluationDao dao = GenDao.newInstance(Evaluation.class, connection);
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

        final EvaluationDao evaluationDao = GenDao.newInstance(Evaluation.class, connection);
        return evaluationDao.save(evaluation);
    }

}
