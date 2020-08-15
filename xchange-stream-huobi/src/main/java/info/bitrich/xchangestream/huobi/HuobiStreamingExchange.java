package info.bitrich.xchangestream.huobi;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.ArrayList;
import org.knowm.xchange.huobi.HuobiExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuobiStreamingExchange extends HuobiExchange implements StreamingExchange {
  private static final Logger LOG = LoggerFactory.getLogger(HuobiStreamingExchange.class);

  private static final String API_BASE_URI = "wss://api.huobi.pro/ws";
  private static final String API_URI_AWS = "wss://api-aws.huobi.pro/ws";

  private HuobiStreamingService streamingService;
  private HuobiStreamingAuthService streamingAuthService;
  private HuobiStreamingMarketDataService streamingMarketDataService;
  private HuobiStreamingTradeService streamingTradeService;

  @Override
  protected void initServices() {
    super.initServices();
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    Boolean aws =
        (Boolean)
            getExchangeSpecification()
                .getExchangeSpecificParameters()
                .getOrDefault("AWS", Boolean.FALSE);
    String noAuthUrl = aws ? API_URI_AWS : API_BASE_URI;

    ArrayList<Completable> completables = new ArrayList<Completable>();

    // stream without auth
    streamingService = new HuobiStreamingService(noAuthUrl);
    streamingService.useCompressedMessages(true);
    streamingMarketDataService = new HuobiStreamingMarketDataService(streamingService);
    completables.add(streamingService.connect());

    // stream with auth
    String apiKey = getExchangeSpecification().getApiKey();
    String secretKey = getExchangeSpecification().getSecretKey();
    if (apiKey == null || secretKey == null) {
      return Completable.concat(completables);
    }

    String authUrl = noAuthUrl + "/v2";
    streamingAuthService = new HuobiStreamingAuthService(authUrl, apiKey, secretKey);
    completables.add(streamingAuthService.connect());
    streamingTradeService = new HuobiStreamingTradeService(streamingAuthService);
    return Completable.concat(completables);
  }

  @Override
  public Completable disconnect() {
    ArrayList<Completable> completables = new ArrayList<Completable>();
    completables.add(streamingService.disconnect());
    completables.add(streamingAuthService.disconnect());
    return Completable.concat(completables);
  }

  @Override
  public boolean isAlive() {
    if (streamingAuthService == null) {
      if (streamingService == null) return false;
      return streamingService.isSocketOpen();
    }

    if (streamingService == null) return false;
    return streamingService.isSocketOpen() && streamingAuthService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return Observable.concatArray(
        streamingService.subscribeReconnectFailure(),
        streamingAuthService.subscribeReconnectFailure());
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return Observable.concatArray(
        streamingService.subscribeConnectionSuccess(),
        streamingAuthService.subscribeConnectionSuccess());
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public StreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
