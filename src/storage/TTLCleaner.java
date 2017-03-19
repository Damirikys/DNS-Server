package storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TTLCleaner extends Thread
{
    private HashMap<String, IPDomainStorage.Entry> cache;
    private List<String> basket;

    TTLCleaner(HashMap<String, IPDomainStorage.Entry> cache)
    {
        this.cache = cache;
        this.basket = new ArrayList<>();
        start();
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (basket.size() != 0)
            {
                basket.forEach(s -> {
                    cache.remove(s);
                    System.out.println(s + " was removed by TTL");
                });

                basket.clear();
            }

            cache.values().forEach(entry -> {
                if (entry.getTTL() < (System.currentTimeMillis() / 1000))
                    basket.add(entry.getKey());
            });
        }
    }
}
