
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @author rod
 */
public class ExecSQLCallable implements Callable<String> {

    private Connection conn;
    //private Gson gson;
    private DateFormat df;
    private SQLRequest request;

    public ExecSQLCallable(Connection conn, DateFormat df, SQLRequest request) {
        this.conn = conn;
        this.df = df;
        this.request = request;
    }

    public String call() throws Exception {
        String result = execSQLJsonSimple();
        System.out.println(result);
        //safePrintln(result);
        return result;
    }

    private String execSQLJsonSimple() {
        JSONObject response = new JSONObject();
        response.put("msgId", request.msgId);
        JSONArray rss = new JSONArray();
        response.put("result", rss);

        // columns
        JSONArray columnsArray = new JSONArray();
        response.put("columns", columnsArray);

        try {
            Statement stmt = conn.createStatement();
            boolean isRS = stmt.execute(request.sql);
            while (isRS || (stmt.getUpdateCount() != -1)) {
                if (!isRS) {
                    isRS = stmt.getMoreResults();
                    continue;
                }
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData meta = rs.getMetaData();

                // get column names;
                int colCount = meta.getColumnCount();
                String[] columns = new String[colCount];
                for (int c = 0; c < colCount; c++)
                    columns[c] = meta.getColumnLabel(c + 1);

                columnsArray.add(columns);

                JSONArray jsonRS = new JSONArray();
                rss.add(jsonRS);
                while (rs.next()) {
                    JSONObject row = new JSONObject();
                    jsonRS.add(row);

                    for (int c = 1; c < colCount + 1; c++) {
                        Object val = rs.getObject(c);
                        if (val == null) continue;

                        int dataType = meta.getColumnType(c);
                        switch (dataType) {
                            case SybaseDB.TYPE_TIME_STAMP:
                            case SybaseDB.TYPE_DATE:
                                String my8601formattedDate = df.format(new Date(rs.getTimestamp(c).getTime()));
                                row.put(columns[c - 1], my8601formattedDate);
                                break;
                            default:
                                row.put(columns[c - 1], rs.getObject(c));
                        }
                    }
                }
                rs.close();
                isRS = stmt.getMoreResults();
            }
            stmt.close();
        } catch (Exception ex) {
            response.put("error", ex.getMessage());
        }

        response.put("javaStartTime", request.javaStartTime);
        long beforeParse = System.currentTimeMillis();
        response.put("javaEndTime", beforeParse);

        return response.toJSONString();
    }

    public void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }
}