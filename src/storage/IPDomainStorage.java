package storage;

import utils.SQLiteDB;

import java.sql.SQLException;
import java.util.HashMap;

public abstract class IPDomainStorage
{
    public static final String NOT_FOUND = "not_found";

    private static HashMap<String, Entry> cache;
    private static TTLCleaner cleaner;

    public static void initialize() throws SQLException, ClassNotFoundException
    {
        SQLiteDB.connect();
        SQLiteDB.initialize();

        cache = SQLiteDB.readAll();
        cleaner = new TTLCleaner(cache);
    }

    public static String getBoundValue(String key)
    {
        if (cache.containsKey(key))
            return cache.get(key).getValue();

        return NOT_FOUND;
    }

    public static void putEntry(String key, String value, int ttl)
    {
        cache.put(key, new Entry(key, value, ttl));
        cache.put(value, new Entry(value, key, ttl));

        try
        {
            SQLiteDB.insertEntry(key, value, ttl);
            SQLiteDB.insertEntry(value, key, ttl);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static class Entry
    {
        String key, value;
        int ttl;

        public Entry(String key, String value, int ttl)
        {
            this.key = key;
            this.value = value;
            this.ttl = ttl;
        }

        public String getValue()
        {
            return value;
        }

        public String getKey()
        {
            return key;
        }

        public int getTTL()
        {
            return ttl;
        }
    }
}
