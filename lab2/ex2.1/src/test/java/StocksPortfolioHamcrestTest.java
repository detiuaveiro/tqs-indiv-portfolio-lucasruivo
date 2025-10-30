import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.StocksPortfolio;
import tqs.IStockmarketService;
import tqs.Stock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class StocksPortfolioHamcrestTest {

    @InjectMocks
    private StocksPortfolio portfolio;

    @Mock
    private IStockmarketService market;

    @Test
    void getTotalValue_withHamcrest() {

        portfolio.add(new Stock("EBAY", 2));
        portfolio.add(new Stock("HUAWEI", 4));

        Mockito.when(market.lookUpPrice("EBAY")).thenReturn(3.0);
        Mockito.when(market.lookUpPrice("HUAWEI")).thenReturn(2.0);


        double total = portfolio.getTotalValue();


        assertThat(total, is(14.0));
        Mockito.verify(market, Mockito.times(2)).lookUpPrice(Mockito.anyString());
    }
}