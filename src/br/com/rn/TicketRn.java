package br.com.rn;

import br.com.base.GenRn;
import br.com.dao.TicketDao;
import br.com.entitys.Ticket;
import br.com.factory.Connection;
import br.com.filters.TicketFilter;
import java.util.List;

public class TicketRn extends GenRn {

    public List<Ticket> list(final TicketFilter ticketFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(ticketFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Ticket> list(final TicketFilter ticketFilter, final Connection connection) throws Throwable {

        final TicketDao dao = new TicketDao(connection);
        return dao.list(ticketFilter);
    }

    public Ticket save(final Ticket ticket) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return save(ticket, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected Ticket save(final Ticket ticket, final Connection connection) throws Throwable {

        final TicketDao ticketDao = new TicketDao(connection);
        return ticketDao.save(ticket);
    }

}
