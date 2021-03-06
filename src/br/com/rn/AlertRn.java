package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.AlertDao;
import br.com.entitys.Alert;
import br.com.factory.Connection;
import br.com.filters.AlertFilter;
import java.util.List;

public class AlertRn extends GenRn {

    protected AlertRn() {
    }
       
    public List<Alert> list(final AlertFilter alertFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(alertFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Alert> list(final AlertFilter alertFilter, final Connection connection) throws Throwable {

        final AlertDao dao = GenDao.newInstance(Alert.class, connection);
        return dao.list(alertFilter);
    }

    public Alert save(final Alert alert) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(alert, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Alert save(final Alert alert, final Connection connection) throws Throwable {

        final AlertDao alertDao = GenDao.newInstance(Alert.class, connection);
        return alertDao.save(alert);
    }

}
