package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.IndicationDao;
import br.com.entitys.Address;
import br.com.entitys.Indication;
import br.com.entitys.PaymentVoucher;
import br.com.factory.Connection;
import br.com.filters.IndicationFilter;
import br.com.generic.GenericDAO;
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

        final IndicationDao dao = new IndicationDao(connection);
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

        final IndicationDao indicationDao = new IndicationDao(connection);
        indicationDao.save(indication);

        if (indication.getAddress() != null) {
            final GenericDAO<Address> genDao = new GenericDAO<>(connection, Address.class);
            genDao.save(indication.getAddress());
        }

        if (indication.getPaymentVoucher() != null) {
            final GenericDAO<PaymentVoucher> genDao = new GenericDAO<>(connection, PaymentVoucher.class);
            genDao.save(indication.getPaymentVoucher());
        }

        return indication;
    }

}
