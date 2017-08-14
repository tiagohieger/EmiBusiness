package br.com.rn;

import br.com.base.GenDao;
import br.com.base.GenRn;
import br.com.dao.IndicationDao;
import br.com.entitys.Address;
import br.com.entitys.Indication;
import br.com.entitys.PaymentVoucher;
import br.com.factory.Connection;
import br.com.filters.IndicationFilter;
import java.util.List;

public class IndicationRn extends GenRn {

    public List<Indication> list(final IndicationFilter indicationFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(indicationFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Indication> list(final IndicationFilter indicationFilter, final Connection connection) throws Throwable {

        final IndicationDao dao = GenDao.newInstance(Indication.class, connection);
        return dao.list(indicationFilter);
    }

    public Indication save(final Indication indication) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(indication, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Indication save(final Indication indication, final Connection connection) throws Throwable {

        final IndicationDao indicationDao = GenDao.newInstance(Indication.class, connection);       

        if (indication.getAddress() != null) {
            final GenDao genDao = GenDao.newInstance(Address.class, connection);
            genDao.save(indication.getAddress());
        }

        if (indication.getPaymentVoucher() != null) {
            final GenDao genDao = GenDao.newInstance(PaymentVoucher.class, connection);
            genDao.save(indication.getPaymentVoucher());
        }
        
         indicationDao.save(indication);

        return indication;
    }

}
