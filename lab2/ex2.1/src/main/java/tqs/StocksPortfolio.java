package tqs;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class StocksPortfolio {
    private List<Stock> stocks;
    private IStockmarketService stockmarket;


    public StocksPortfolio(IStockmarketService stockmarket) {
        this.stocks = new ArrayList<Stock>();
        this.stockmarket = stockmarket;
    }


    public void add(Stock stock) {
        this.stocks.add(stock);
    }

    public Double getTotalValue() {
        // summing the current value of owned stock, looked up in the stock marketservice
        Double value = 0.0;

        for (Stock s : this.stocks) 
            value += s.getQuantity() * this.stockmarket.lookUpPrice( s.getLabel() );
        
        return value;
    }

    public List<Stock> mostValuableStocks(int topN) {
        if (topN <= 0 || stocks.isEmpty()) {
            return new ArrayList<>();
        }
    
        return stocks.stream()
                .sorted((a, b) -> Double.compare(
                        b.getQuantity() * stockmarket.lookUpPrice(b.getLabel()),
                        a.getQuantity() * stockmarket.lookUpPrice(a.getLabel())
                ))
                .limit(topN)
                .collect(Collectors.toList());
    }
    
}