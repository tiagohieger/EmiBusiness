package br.com.rn;

import br.com.dao.GenDao;
import br.com.dao.TicketDao;
import br.com.entitys.Ticket;
import br.com.factory.Connection;
import br.com.filters.TicketFilter;
import java.util.List;

public class TicketRn extends GenRn {

    protected TicketRn() {
    }

    public List<Ticket> list(final TicketFilter ticketFilter) throws Throwable {

        final Connection connection = DB.getConnection();
        try {
            return list(ticketFilter, connection);
        } finally {
            DB.closeConnection(connection);
        }
    }

    protected List<Ticket> list(final TicketFilter ticketFilter, final Connection connection) throws Throwable {

        final TicketDao dao = GenDao.newInstance(Ticket.class, connection);
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

        final TicketDao ticketDao = GenDao.newInstance(Ticket.class, connection);
        return ticketDao.save(ticket);
    }

}
