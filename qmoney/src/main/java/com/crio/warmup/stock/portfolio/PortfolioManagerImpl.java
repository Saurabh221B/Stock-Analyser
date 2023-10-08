
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.io.IOException;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  
  private StockQuotesService stockQuotesService;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService=stockQuotesService;
    
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  @Override
  public  List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
        AnnualizedReturn annualizedReturn;
    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();
    for (PortfolioTrade trade : portfolioTrades) {
      annualizedReturn = calculateAnnualReturnval(endDate, trade);
      annualizedReturnsList.add(annualizedReturn);
    }
    Collections.sort(annualizedReturnsList, PortfolioManagerImpl.getComparator());
    return annualizedReturnsList;
  }
      
  public  AnnualizedReturn calculateAnnualReturnval(LocalDate endDate, PortfolioTrade trade
  )   {
    AnnualizedReturn annualizedReturn;
    String symbol = trade.getSymbol();
    LocalDate startDate = trade.getPurchaseDate();
    try  {

       List<Candle> candleList = stockQuotesService.getStockQuote(symbol,startDate, endDate);
    double buyPrice = getOpeningPriceOnStartDate(candleList);
    double sellPrice = getClosingPriceOnEndDate(candleList);

    double totalReturns = (sellPrice - buyPrice) / buyPrice;
    double diffInDays = (double) (ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate));
    double noOfYears = diffInDays / 365.0;
    double power = (1 / noOfYears);
    double annualizedRReturns = Math.pow(1 + totalReturns, power) - 1;
    annualizedReturn= new AnnualizedReturn(symbol, annualizedRReturns, totalReturns);
    
  } catch (IOException e) {
    annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
  }
  return annualizedReturn;
   
  } 
    
  private static double getClosingPriceOnEndDate(List<Candle> candles) {
    int size = candles.size() - 1;
    while (candles.get(size).getClose() == null) {
      size--;
    }
    return candles.get(size).getClose();
  }

  private static double getOpeningPriceOnStartDate(List<Candle> candles) {
    int size = 0;
    while (candles.get(size).getOpen() == null) {
      size++;
    }
    return candles.get(size).getOpen();
  }

  private static Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }


  public  Candle[] tiingoAPI(String url) {
    RestTemplate restTemplate = new RestTemplate();
    Candle[] candleList =
        restTemplate.getForObject(url, TiingoCandle[].class);
    return candleList;
  
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

    if (from.compareTo(to) >= 0) {
      throw new RuntimeException();
    }
    String url = buildUri(symbol, from, to);

    Candle[] candleList = tiingoAPI(url);      
    if (candleList != null) {
      List<Candle> candleLi = Arrays.asList(candleList);
      return candleLi;
    }
   
     return new ArrayList<Candle>();
  }

  protected  String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    //String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
    //    + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String token="3856183435d51823ee87c632617cfcd67b4fd1d9";

    String url= "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate="
        + startDate + "&endDate=" + endDate + "&token=" + token;
        
    return url;
  }
  
}
