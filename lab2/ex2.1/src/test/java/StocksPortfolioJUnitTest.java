import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.IStockmarketService;
import tqs.Stock;
import tqs.StocksPortfolio;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StocksPortfolioJUnitTest {

    @InjectMocks
    private StocksPortfolio portfolio;

    @Mock
    private IStockmarketService market;

    @BeforeEach
    void setup() {
        portfolio.add(new Stock("APPLE", 5));
        portfolio.add(new Stock("GOOG", 2));
        portfolio.add(new Stock("AMZN", 1));
    }

    @Test
    void mostValuableStocks_top2_shouldReturnCorrectOrder() {
        Mockito.when(market.lookUpPrice("APPLE")).thenReturn(10.0);
        Mockito.when(market.lookUpPrice("GOOG")).thenReturn(20.0);
        Mockito.when(market.lookUpPrice("AMZN")).thenReturn(100.0);

        List<Stock> result = portfolio.mostValuableStocks(2);

        assertEquals(2, result.size());
        assertEquals("AMZN", result.get(0).getLabel());
        assertEquals("APPLE", result.get(1).getLabel());
    }

    @Test
    void mostValuableStocks_topGreaterThanPortfolioSize_shouldReturnAll() {
        Mockito.when(market.lookUpPrice("APPLE")).thenReturn(10.0);
        Mockito.when(market.lookUpPrice("GOOG")).thenReturn(20.0);
        Mockito.when(market.lookUpPrice("AMZN")).thenReturn(100.0);

        List<Stock> result = portfolio.mostValuableStocks(10);

        assertEquals(3, result.size());
        assertEquals("AMZN", result.get(0).getLabel());
        assertEquals("APPLE", result.get(1).getLabel());
        assertEquals("GOOG", result.get(2).getLabel());
    }

    @Test
    void mostValuableStocks_zeroOrNegative_shouldReturnEmptyList() {
        assertTrue(portfolio.mostValuableStocks(0).isEmpty());
        assertTrue(portfolio.mostValuableStocks(-5).isEmpty());
    }

    @Test
    void mostValuableStocks_emptyPortfolio_shouldReturnEmptyList() {
        StocksPortfolio empty = new StocksPortfolio(market);
        assertTrue(empty.mostValuableStocks(3).isEmpty());
    }
}