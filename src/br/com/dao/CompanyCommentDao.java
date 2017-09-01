package br.com.dao;

import br.com.entitys.Entity;
import br.com.entitys.CompanyComment;
import br.com.factory.Connection;
import br.com.factory.Query;
import br.com.filters.CompanyCommentFilter;
import br.com.utils.PersistenceUtils;
import br.com.utils.SQLUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompanyCommentDao extends GenDao<CompanyComment, CompanyCommentFilter> {

    protected CompanyCommentDao(final Connection connection) {
        super(connection, CompanyComment.class);
    }

    @Override
    public List<CompanyComment> list(final CompanyCommentFilter filter) throws Throwable {

        final Query query = getQuery(filter);

        query.open();

        final Map<Integer, CompanyComment> map = new HashMap<>();
        final Map<Integer, CompanyComment> mapTmp = new HashMap<>();

        while (query.next()) {

            final CompanyComment grupo = (CompanyComment) SQLUtils.classPopulate(query, CompanyComment.class);

            final Integer id = (Integer) query.getValue(CompanyComment.Columns.COMPANY_COMMENT);
            final CompanyComment daughterComment = new CompanyComment();
            daughterComment.setId(id);

            grupo.setCompanyComments(daughterComment);

            final CompanyComment grupoTmp = mapTmp.get(grupo.getCompanyComments().getId());

            if (grupoTmp != null) {
                grupoTmp.getCompanysComments().add(grupo);
            } else {
                map.put(grupo.getId(), grupo);
            }
            mapTmp.put(grupo.getId(), grupo);
        }
        return new LinkedList<>(map.values());
    }

    @Override
    public Query getQuery(final CompanyCommentFilter filter, final String... colsToReturn) throws Throwable {

        final String returns = PersistenceUtils.concat(CompanyComment.TABLE_NAME + ".*", colsToReturn);

        final Query query = new Query(connection);

        final StringBuilder sql = new StringBuilder();

        sql.append(" WITH RECURSIVE comments_tree ( ");
        sql.append(CompanyComment.Columns.ID);
        sql.append(", ");
        sql.append(CompanyComment.Columns.REGISTRATION_DATE);
        sql.append(", ");
        sql.append(CompanyComment.Columns.USER);
        sql.append(", ");
        sql.append(CompanyComment.Columns.COMPANY_COMMENT);
        sql.append(", ");
        sql.append(CompanyComment.Columns.CLIENT);
        sql.append(", ");
        sql.append(CompanyComment.Columns.TEXT);
        sql.append(", depth  ) AS ( ( SELECT ");
        sql.append(returns);
        sql.append(", 1 AS depth FROM ");
        sql.append(CompanyComment.TABLE_NAME);
        sql.append(" WHERE ");

        if (filter.getCompanyComment() == null) {
            sql.append(CompanyComment.Columns.COMPANY_COMMENT);
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
            sql.append(CompanyComment.Columns.ID);
            sql.append(" = ? ");
            query.addParam(filter.getCompanyComment());
        }

        sql.append(" ) UNION ALL ( SELECT ");
        sql.append(returns);
        sql.append(",  depth + 1 FROM ");
        sql.append(CompanyComment.TABLE_NAME);
        sql.append(" JOIN comments_tree ON comments_tree.id = ");
        sql.append(Entity.fullColumn(CompanyComment.TABLE_NAME, CompanyComment.Columns.COMPANY_COMMENT));

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
