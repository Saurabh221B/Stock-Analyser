
package com.crio.warmup.stock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<String> symbl = new ArrayList<>();
    for (PortfolioTrade t : trades) {
      symbl.add(t.getSymbol());
    }
    return symbl;
  }


  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.







  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/saurabhpatilsp54-ME_QMONEY_V2/qmoney/bin/test/assessments/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@77fbd92c";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "19";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    //return Collections.emptyList();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    List<TotalReturnsDto> sortedList = mainReadQuotesHelper(args, trades);
    List<String> stocks = new ArrayList<>();
    if (sortedList == null) {
      return stocks;
    }
    Collections.sort(sortedList, TotalReturnsDto.closingComparator);
    
    for (TotalReturnsDto t : sortedList) {
      stocks.add(t.getSymbol());
    }
    return stocks;


  }

  public static List<TotalReturnsDto> mainReadQuotesHelper(String []args,
      List<PortfolioTrade> trades) {
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> tests = new ArrayList<>();
   
    for (PortfolioTrade t : trades) {
    
      TiingoCandle[] result = restTemplate.getForObject(
          prepareUrl(t, 
              LocalDate.parse(args[1]), "3856183435d51823ee87c632617cfcd67b4fd1d9"),
          TiingoCandle[].class);

      if (result != null) {
        tests.add(new TotalReturnsDto(t.getSymbol(), result[result.length - 1].getClose()));
      }
    }
    return tests;
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    //return Collections.emptyList();
    File file2 = resolveFileFromResources(filename);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(file2, PortfolioTrade[].class);
    List<PortfolioTrade> ls = new ArrayList<>();
    for (PortfolioTrade s : trades) {
      ls.add(s);
    }
    return ls;
  }

  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    //return Collections.emptyList();

    return "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
        + trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;

  }

  public static String getToken() {
    return "3856183435d51823ee87c632617cfcd67b4fd1d9";
  }
  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest

  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    int size = 0;
    while (candles.get(size).getOpen() == null) {
      size++;
    }
    return candles.get(size).getOpen();
     
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    int size = candles.size() - 1;
    while (candles.get(size).getClose() == null) {
      size--;
    }
     return candles.get(size).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    Candle[] candleList = restTemplate.getForObject(
    prepareUrl(trade,endDate,token),TiingoCandle[].class);  
    List<Candle> candleLi = Arrays.asList(candleList);
    return candleLi;
      }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        List<AnnualizedReturn> ans = new ArrayList<>();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    for (PortfolioTrade trade : trades) {
      List<Candle> candleList =
        fetchCandles(trade, LocalDate.parse(args[1]), "3856183435d51823ee87c632617cfcd67b4fd1d9");
      double buyPrice = getOpeningPriceOnStartDate(candleList);
      double sellPrice = getClosingPriceOnEndDate(candleList);
      AnnualizedReturn entity =
          calculateAnnualizedReturns(LocalDate.parse(args[1]), trade, buyPrice, sellPrice);

      ans.add(entity);
    }
    Collections.sort(ans,AnnualizedReturn.AnnnulComparator);
    
    
        

    //return Collections.emptyList();
    return ans;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    double totalReturns = (sellPrice - buyPrice) / buyPrice;
    double diffInDays = (double) (ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate));
    double noOfYears = diffInDays / 365.0;
    double power = (1 / noOfYears);
    double annualizedRReturns = Math.pow(1 + totalReturns, power) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedRReturns, totalReturns);
  }
  
   public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateSingleReturn(args));
  }
}

