package tqs;

import java.util.ArrayList;
import java.util.List;

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

    
}