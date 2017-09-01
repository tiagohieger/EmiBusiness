package br.com.dao;

import br.com.entitys.Entity;
import br.com.entitys.IndicationComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.IndicationCommentFilter;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IndicationCommentDao extends GenDao<IndicationComment, IndicationCommentFilter> {

    protected IndicationCommentDao(final Connection connection) {
        super(connection, IndicationComment.class);
    }

    @Override
    public List<IndicationComment> list(final IndicationCommentFilter filter) throws Throwable {

        final Query query = getQuery(filter);

        query.open();

        final Map<Integer, IndicationComment> map = new HashMap<>();
        final Map<Integer, IndicationComment> mapTmp = new HashMap<>();

        while (query.next()) {

            final IndicationComment grupo = (IndicationComment) SQLUtils.classPopulate(query, IndicationComment.class);

            final Integer id = (Integer) query.getValue(IndicationComment.Columns.INDICATION_COMMENT);
            final IndicationComment daughterComment = new IndicationComment();
            daughterComment.setId(id);

            grupo.setIndicationComments(daughterComment);

            final IndicationComment grupoTmp = mapTmp.get(grupo.getIndicationComments().getId());

            if (grupoTmp != null) {
                grupoTmp.getIndicationsComments().add(grupo);
            } else {
                map.put(grupo.getId(), grupo);
            }
            mapTmp.put(grupo.getId(), grupo);
        }
        return new LinkedList<>(map.values());
    }

    @Override
    public Query getQuery(final IndicationCommentFilter filter, final String... colsToReturn) throws Throwable {

        final String returns = PersistenceUtils.concat(IndicationComment.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(connection);

        final StringBuilder sql = new StringBuilder();

        sql.append(" WITH RECURSIVE comments_tree ( ");
        sql.append(IndicationComment.Columns.ID);
        sql.append(", ");
        sql.append(IndicationComment.Columns.REGISTRATION_DATE);
        sql.append(", ");
        sql.append(IndicationComment.Columns.USER);
        sql.append(", ");
        sql.append(IndicationComment.Columns.INDICATION_COMMENT);
        sql.append(", ");
        sql.append(IndicationComment.Columns.INDICATION);
        sql.append(", ");
        sql.append(IndicationComment.Columns.TEXT);
        sql.append(", depth  ) AS ( ( SELECT ");
        sql.append(returns);
        sql.append(", 1 AS depth FROM ");
        sql.append(IndicationComment.TABLE_NAME);
        sql.append(" WHERE ");

        if (filter.getIndicationComment() == null) {
            sql.append(IndicationComment.Columns.INDICATION_COMMENT);
            sql.append(" IS NULL ");
            if (filter.getLimit() != null) {
                sql.append(" LIMIT ? ");
                query.addParam(filter.getLimit());
            }
            if (filter.getOffset() != null) {
                if (filter.getLimit() != null) {
                    sql.append(" OFFSET ? ");
                    query.addParam(filter.getOffset());
                }
            }
        } else {
            sql.append(IndicationComment.Columns.ID);
            sql.append(" = ? ");
            query.addParam(filter.getIndicationComment());
        }

        sql.append(" ) UNION ALL ( SELECT ");
        sql.append(returns);
        sql.append(",  depth + 1 FROM ");
        sql.append(IndicationComment.TABLE_NAME);
        sql.append(" JOIN comments_tree ON comments_tree.id = ");
        sql.append(Entity.fullColumn(IndicationComment.TABLE_NAME, IndicationComment.Columns.INDICATION_COMMENT));

        if (filter.getCountLevel() != null) {
            sql.append(" WHERE depth <= ? ");
            query.addParam(filter.getCountLevel());
        }

        sql.append(" ) ) SELECT * FROM comments_tree ");

        if (filter.getGroupBy() != null) {
            sql.append(" GROUP BY ");
            sql.append(filter.getGroupBy());
        }

        if (filter.getOrderBy() != null) {
            sql.append(" ORDER BY ");
            sql.append(filter.getOrderBy());
            sql.append(" ");
            sql.append(filter.getDirection());
        }

        query.addSql(sql.toString().replaceFirst("AND", "WHERE"));

        return query;
    }

}
