package tqs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.core.Is.is;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith( MockitoExtension.class )
public class StocksPortfolioTest  {

    @InjectMocks
    private StocksPortfolio portfolio;

    @Mock
    private IStockmarketService market;


    @Test
    public void getTotalValueTest() {
        
        portfolio.add( new Stock("EBAY", 2) );
        portfolio.add( new Stock("HUAWEI", 4) );

        Mockito.when( market.lookUpPrice("EBAY") ).thenReturn(3.0);
        Mockito.when( market.lookUpPrice("HUAWEI") ).thenReturn(2.0);
        
        Double result = portfolio.getTotalValue();

        assertEquals( 14, result );
        assertThat( result, is(14.0) );
        Mockito.verify( market, Mockito.times(2) ).lookUpPrice( Mockito.anyString() );
    }
}