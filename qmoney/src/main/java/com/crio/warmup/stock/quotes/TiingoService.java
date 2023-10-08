
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {
  private static final String token="3856183435d51823ee87c632617cfcd67b4fd1d9";

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    // TODO Auto-generated method stub
    if (from.compareTo(to) >= 0) {
      throw new RuntimeException();
    }
    String url = buildUri(symbol, from, to);

    // try {
    //   Candle[] candleList = restTemplate.getForObject(url, TiingoCandle[].class);
    //   if (candleList != null) {
    //     List<Candle> candleLi = Arrays.asList(candleList);
    //     return candleLi;

    //   }
    //   return new ArrayList<Candle>();
    // } catch (Exception e) {
    //   e.printStackTrace();
    // }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String tiingoResponse = restTemplate.getForObject(url,String.class);
    Candle[] candleList = objectMapper.readValue(tiingoResponse, TiingoCandle[].class);
    // System.out.println(candleList);
    // System.out.println("hi");
    if (candleList == null) {
      return new ArrayList<Candle>();
    }
    List<Candle> candleLi = Arrays.asList(candleList);
    return candleLi;
 
  }

  private String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate
        + "&endDate=" + endDate + "&token=" + token;

    return url;

  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
