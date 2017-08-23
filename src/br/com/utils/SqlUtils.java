package br.com.utils;

import br.com.factory.Query;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class SqlUtils {

    public static StringBuilder getCopyInSql(final List<String> columns, final String tableName, final String encoding) {

        final StringBuilder builderCopy = new StringBuilder();
        builderCopy.append(" COPY ");
        builderCopy.append(tableName);
        builderCopy.append(" ( ");

        final StringBuilder builderColumns = new StringBuilder();

        columns.stream().map((coluna) -> {
            builderColumns.append(coluna);
            return coluna;
        }).forEach((_item) -> {
            builderColumns.append(", ");
        });

        builderColumns.deleteCharAt(builderColumns.lastIndexOf(","));
        builderCopy.append(builderColumns);
        builderCopy.append(" ) ");
        builderCopy.append(" FROM STDIN WITH CSV HEADER DELIMITER AS ';' ENCODING '");
        builderCopy.append(encoding);
        builderCopy.append("'");

        return builderCopy;
    }

    public static StringBuilder getCopyInSqlWithoutHeader(final List<String> columns, final String tableName, final String encoding) {

        final StringBuilder builderCopy = new StringBuilder();
        builderCopy.append(" COPY ");
        builderCopy.append(tableName);
        builderCopy.append(" ( ");

        final StringBuilder builderColumns = new StringBuilder();

        columns.stream().map((coluna) -> {
            builderColumns.append(coluna);
            return coluna;
        }).forEach((_item) -> {
            builderColumns.append(", ");
        });

        builderColumns.deleteCharAt(builderColumns.lastIndexOf(","));
        builderCopy.append(builderColumns);
        builderCopy.append(" ) ");
        builderCopy.append(" FROM STDIN WITH CSV DELIMITER AS ';' ENCODING '");
        builderCopy.append(encoding);
        builderCopy.append("'");

        return builderCopy;
    }

    public static StringBuilder getCopyOutSql(final List<String> columns, final String tableName, final String encoding) {

        final StringBuilder builderColumns = new StringBuilder();

        columns.stream().map((coluna) -> {
            builderColumns.append(coluna);
            return coluna;
        }).forEach((_item) -> {
            builderColumns.append(", ");
        });

        builderColumns.deleteCharAt(builderColumns.lastIndexOf(","));

        final StringBuilder builderCopy = new StringBuilder();

        builderCopy.append(" COPY ( SELECT ");
        builderCopy.append(builderColumns.toString());
        builderCopy.append(" FROM ");
        builderCopy.append(tableName);
        builderCopy.append(" ) ");
        builderCopy.append(" TO STDOUT WITH DELIMITER AS ';' CSV HEADER; ");

        return builderCopy;
    }

    public static StringBuilder getCopyOutSql(final List<String> columns, final String tableName,
            final String joinWhere, final String encoding) throws IOException {

        final StringBuilder builderColumns = new StringBuilder();

        columns.stream().map((coluna) -> {
            builderColumns.append(coluna);
            return coluna;
        }).forEach((_item) -> {
            builderColumns.append(", ");
        });

        builderColumns.deleteCharAt(builderColumns.lastIndexOf(","));

        final StringBuilder builderCopy = new StringBuilder();

        builderCopy.append(" COPY ( SELECT ");
        builderCopy.append(builderColumns.toString());
        builderCopy.append(" FROM ");
        builderCopy.append(tableName);
        builderCopy.append(" ");
        builderCopy.append(joinWhere);
        builderCopy.append(" ) ");
        builderCopy.append(" TO STDOUT WITH DELIMITER AS ';' CSV HEADER; ");

        return builderCopy;
    }

    public static StringBuilder getCopyOutSql(final String sql, final String encoding) {

        final StringBuilder builderCopy = new StringBuilder();

        builderCopy.append(" COPY ( ");
        builderCopy.append(sql);
        builderCopy.append(" ) ");
        builderCopy.append(" TO STDOUT WITH DELIMITER AS ';' CSV HEADER; ");

        return builderCopy;
    }

    public static Long copyDataToDb(final String sqlCopy, final String data, final Connection conn)
            throws SQLException, IOException {

        final InputStream dadosInputStream = new ByteArrayInputStream(data.getBytes());
        final CopyManager copy = new CopyManager((BaseConnection) conn);
        return copy.copyIn(sqlCopy, dadosInputStream);
    }

    public static Long copyDataToDb(final String sqlCopy, final File file, final Connection conn, final String encoding,
            final Boolean delSpecialCharacter) throws SQLException, IOException {

        final String dadosArquivos = FileUtils.getData(file.getPath(), encoding, delSpecialCharacter);
        final CopyManager copy = new CopyManager((BaseConnection) conn);
        return copy.copyIn(sqlCopy, new ByteArrayInputStream(dadosArquivos.getBytes()));
    }

    public static Long copyDataToDb(final String tableName, final LinkedList<String> columns,
            final String path, final String encoding, final Connection conn, final Boolean delSpecialCharacter)
            throws IOException, SQLException, ClassNotFoundException, IllegalArgumentException {

        final StringBuilder builder = SqlUtils.getCopyInSql(columns, tableName, encoding);
        return SqlUtils.copyDataToDb(builder.toString(), new File(path), conn, encoding, delSpecialCharacter);
    }

    public static void copyFromDb(final String sqlCopy, final Connection conn, final String path)
            throws SQLException, FileNotFoundException, IOException {

        final CopyManager copy = new CopyManager((BaseConnection) conn);
        final FileOutputStream out = new FileOutputStream(new File(path));
        try {
            copy.copyOut(sqlCopy, out);
        } finally {
            out.flush();
            out.close();
        }
    }

    public static String getQuestionMarks(final int qntde) {

        String result = "";
        for (int i = 0; i < qntde; i++) {
            result += "?,";
        }
        return result.substring(0, result.length() - 1);
    }

    public static String getColumnsSeparetedByComma(List<String> columns) {

        StringBuilder strSQL = new StringBuilder();
        String sep = "";

        for (String coluna : columns) {
            strSQL.append(sep).append(coluna);
            sep = ", ";
        }
        return strSQL.toString();
    }

    public static void resetDbSessionState(final br.com.factory.Connection connection)
            throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        // EXISTE O COMANDO 'DISCARD ALL' QUE SUBSTITUI TODOS ESSES
        // ABAIXO MAS NÃO PODE SER EXECUTADO EM TRANSAÇÕES BLOQUEADAS, OU SEJA,
        // COM COMANDOS DE 'INICIA TRANSAÇÃO'.
        query.addSql("RESET ALL;");
        query.addSql("DEALLOCATE ALL;");
        query.addSql("CLOSE ALL;");
        query.addSql("UNLISTEN *;");
        query.addSql("DISCARD PLANS;");
        //query.addSql("DISCARD SEQUENCES;"); OBS: 9.4 PRA FRENTE.
        query.addSql("DISCARD TEMP;");
        query.addSql("SET SESSION AUTHORIZATION DEFAULT;");
        query.execute();

        query.clear();

        query.addSql("SELECT pg_advisory_unlock_all();");

        query.open();
    }

    public static void vaccumSimpleDb(final String tableName, final br.com.factory.Connection connection)
            throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        query.addSql("VACUUM ?;");
        query.addParam(1, tableName);
        query.execute();
    }

    public static void analyzeDb(final String tableName, final br.com.factory.Connection connection)
            throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        query.addSql("ANALYZE ?;");
        query.addParam(1, tableName);
        query.execute();
    }

    public static void reindexAllIndexes(final String tableName, final br.com.factory.Connection connection)
            throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        query.addSql("REINDEX TABLE ?;");
        query.addParam(1, tableName);
        query.execute();
    }

    public static void clusterIndex(final String indexName, final br.com.factory.Connection connection,
            final String nomeTabela) throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        query.addSql("CLUSTER ? ON ?;");
        query.addParam(1, indexName);
        query.addParam(2, nomeTabela);
        query.execute();
    }

    public static void clusterAllIndexes(final br.com.factory.Connection connection, final String tableName)
            throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);
        query.addSql("CLUSTER ?;");
        query.addParam(1, tableName);
        query.execute();
    }

    public static void createDefaultIndex(final br.com.factory.Connection connection, final String indexName,
            final String tableName, final List<String> columns) throws ClassNotFoundException, SQLException {

        final Query query = new Query(connection);

        final String questionsMarks = getQuestionMarks(columns.size());

        query.addSql("CREATE INDEX ? ON ? (");
        query.addSql(questionsMarks);
        query.addSql(");");
        int i = 1;
        query.addParam(i++, indexName);
        query.addParam(i++, tableName);

        for (String coluna : columns) {
            query.addParam(i++, coluna);
        }
        query.execute();
    }

}
