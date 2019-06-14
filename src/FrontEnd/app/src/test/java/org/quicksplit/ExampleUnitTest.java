package org.quicksplit;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;
import static org.quicksplit.Utils.QrTicketReader.getCostWithCurrency;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws MalformedURLException {

        URL url = new URL("https://www.efactura.dgi.gub.uy/consultaQR/cfe?216839180011,101,A,38956,240.00,25/04/2019,BTw9Sf7p2ZTwgEekCL8%2fhWT8SPc%3d");
        CostWithCurrency costWithCurrency = getCostWithCurrency(url);
        assertEquals(240, costWithCurrency.cost, 0.1);
        assertEquals("Uyu", costWithCurrency.currency);
    }


}