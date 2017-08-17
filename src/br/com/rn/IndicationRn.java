package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.IndicationDao;
import br.com.entitys.Address;
import br.com.entitys.Indication;
import br.com.entitys.IndicationHistoric;
import br.com.entitys.PaymentVoucher;
import br.com.factory.Connection;
import br.com.filters.IndicationFilter;
import br.com.json.Converter;
import java.util.List;

public class IndicationRn extends GenRn {

    protected IndicationRn() {
    }

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

    public Indication save(Indication indication) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            connection.startTransaction();
            indication = save(indication, connection);
            connection.saveTransaction();
            return indication;
        } catch (Throwable ex) {
            connection.cancelTransaction();
            throw ex;
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

        final IndicationHistoric historic = new IndicationHistoric();
        historic.setIndication(indication);
        historic.setUser(indication.getUser());
        historic.setJson(Converter.objectToJson(indication));

        final GenDao genDao = GenDao.newInstance(IndicationHistoric.class, connection);
        genDao.save(historic);

        return indication;
    }

}
