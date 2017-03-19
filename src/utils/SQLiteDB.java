package utils;

import storage.IPDomainStorage;

import java.sql.*;
import java.util.HashMap;

public class SQLiteDB
{
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;

    private static final String DATABASE_NAME = "DNSArchive";
    private static final String TABLE_NAME = "domains";
    private static final String ID = "id";
    private static final String DOMAIN = "domain";
    private static final String IP = "ip";
    private static final String TTL = "ttl";

    public static void connect() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME+".s3db");

        System.out.println("База Подключена!");
    }

    public static void initialize() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute(
                "CREATE TABLE if not exists '"+TABLE_NAME+"' (" +
                "'"+ID+"' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+DOMAIN+"' text, " +
                "'"+IP+"' text, " +
                "'"+TTL+"' INT" +
                ");"
        );
    }

    public static void insertEntry(String domain, String ip, int ttl) throws SQLException
    {
        statmt.execute(
                "INSERT INTO '"+TABLE_NAME+"' " +
                        "('"+DOMAIN+"', '"+IP+"', '"+TTL+"') VALUES " +
                        "('"+domain+"', '"+ip+"', "+ttl+"); "
        );
    }

    public static HashMap<String, IPDomainStorage.Entry> readAll() throws ClassNotFoundException, SQLException
    {
        HashMap<String, IPDomainStorage.Entry> map = new HashMap<>();

        resSet = statmt.executeQuery("SELECT * FROM "+TABLE_NAME+"");

        while(resSet.next())
        {
            int id = resSet.getInt(ID);
            String domain = resSet.getString(DOMAIN);
            String ip = resSet.getString(IP);
            String ttl = resSet.getString(TTL);

            map.put(domain, new IPDomainStorage.Entry(domain, ip, Integer.parseInt(ttl)));
        }

        return map;
    }

    public static void close() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();
    }

}