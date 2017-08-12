package br.com.dao;

import br.com.base.GenDAO;
import br.com.entitys.Client;
import br.com.entitys.Entity;
import br.com.entitys.Ticket;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.TicketFilter;
import static br.com.generic.GenericDAO.DATE_FORMATTER;
import br.com.utils.DateUtils;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TicketDao extends GenDAO<Ticket, TicketFilter> {

    public TicketDao(final Connection connection) {
        super(connection, Ticket.class);
    }

    @Override
    public List<Ticket> list(TicketFilter filter) throws IllegalAccessException, InstantiationException, SQLException,
            ClassNotFoundException, IOException {

        final Query query = getQuery(filter);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM ( ");
        sql.append(query.getText());
        sql.append(" ) AS ").append(Ticket.TABLE_NAME);

        sql.append("  JOIN  ")
                .append(Client.TABLE_NAME)
                .append(" ON ")
                .append(Entity.fullColumn(Client.TABLE_NAME, Client.Columns.ID))
                .append(" = ")
                .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLIENT));

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.setText(sql.toString());

        query.open();

        final List<Ticket> entities = new LinkedList<>();

        while (query.next()) {

            final Client client = (Client) SQLUtils.entityPopulate(query, Client.class);
            final Ticket ticket = (Ticket) SQLUtils.entityPopulate(query, Ticket.class);

            if (client != null) {
                ticket.setClient(client);
            }
            entities.add(ticket);
        }
        return entities;
    }

    @Override
    public Query getQuery(TicketFilter filter, String... colsToReturn) throws IOException {

        final String returns = PersistenceUtils.concat(Ticket.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(conexao);

        final StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(returns);
        sql.append(" FROM ");
        sql.append(Ticket.TABLE_NAME);

        if (filter.getText() != null && !filter.getText().trim().isEmpty()) {

            sql.append(" AND ( ");
            sql.append(" (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.STATUS)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.LINK)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.NOTE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.VALUE)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.PAYER_DOCUMENT)).append(") LIKE ?) ");
            sql.append(" OR (lower( ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.BENEFICIARY_DOCUMENT)).append(") LIKE ?) ");
            sql.append(" ) ");

            for (int i = 0, paransCount = 6; i < paransCount; i++) {
                query.addParam("%" + filter.getText().toLowerCase() + "%");
            }
        }

        if (filter.getBarCode() != null && !filter.getBarCode().trim().isEmpty()) {
            sql.append(" AND ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.BAR_CODE)).append(" = ? ");
            query.addParam(filter.getBarCode());
        }

        if (filter.getBeneficiaryDocument() != null && !filter.getBeneficiaryDocument().trim().isEmpty()) {
            sql.append(" AND ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.BENEFICIARY_DOCUMENT)).append(" = ? ");
            query.addParam(filter.getBeneficiaryDocument());
        }

        if (filter.getOurNumber() != null && !filter.getOurNumber().trim().isEmpty()) {
            sql.append(" AND ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.OUR_NUMBER)).append(" = ? ");
            query.addParam(filter.getOurNumber());
        }

        if (filter.getPayerDocument() != null && !filter.getPayerDocument().trim().isEmpty()) {
            sql.append(" AND ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.PAYER_DOCUMENT)).append(" = ? ");
            query.addParam(filter.getPayerDocument());
        }

        if (filter.getClient() != null) {
            sql.append(" AND ").append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLIENT)).append(" = ? ");
            query.addParam(filter.getClient());
        }

        if (filter.getMinRegistrationDate() != null) {
            sql.append(" AND ( ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.REGISTRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxRegistrationDate() != null) {
            sql.append(" AND (")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.REGISTRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.REGISTRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxRegistrationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMinClosingDate() != null) {
            sql.append(" AND ( ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLOSING_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLOSING_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinClosingDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxClosingDate() != null) {
            sql.append(" AND (")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLOSING_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.CLOSING_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxClosingDate()).format(DATE_FORMATTER));
        }

        if (filter.getMinExpirationDate() != null) {
            sql.append(" AND ( ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.EXPIRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.EXPIRATION_DATE))
                    .append("::DATE >= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMinExpirationDate()).format(DATE_FORMATTER));
        }

        if (filter.getMaxExpirationDate() != null) {
            sql.append(" AND (")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.EXPIRATION_DATE))
                    .append(" IS NOT NULL AND ")
                    .append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.EXPIRATION_DATE))
                    .append("::DATE <= ?::DATE) ");
            query.addParam(DateUtils.of(filter.getMaxExpirationDate()).format(DATE_FORMATTER));
        }

        if (filter.getGroupBy() == null) {
            sql.append(" GROUP BY ");
            sql.append(Entity.fullColumn(Ticket.TABLE_NAME, Ticket.Columns.ID));
        } else {
            sql.append(" GROUP BY ");
            sql.append(filter.getGroupBy());
        }

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        if (filter.getLimit() != null) {
            sql.append(" LIMIT ? ");
            query.addParam(filter.getLimit());
        }

        if (filter.getOffset() != null) {
            sql.append(" OFFSET ? ");
            query.addParam(filter.getOffset());
        }

        query.addSql(sql.toString().replaceFirst("AND", "WHERE"));

        return query;
    }

}
