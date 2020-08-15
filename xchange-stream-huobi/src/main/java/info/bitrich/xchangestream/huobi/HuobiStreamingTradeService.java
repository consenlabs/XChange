package info.bitrich.xchangestream.huobi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.huobi.HuobiAdapters;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.trade.HuobiOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuobiStreamingTradeService implements StreamingTradeService {
  protected final Logger LOG = LoggerFactory.getLogger(getClass());

  private final HuobiStreamingAuthService streamingService;

  public HuobiStreamingTradeService(HuobiStreamingAuthService streamingService) {
    this.streamingService = streamingService;
  }

  @Override
  public Observable<UserTrade> getUserTrades(CurrencyPair currencyPair, Object... args) {
    String channelName =
        String.format("orders#%s", HuobiUtils.createHuobiCurrencyPair(currencyPair));

    return streamingService
        .subscribeChannel(channelName)
        .map(
            message -> {
              LOG.debug("OrderEvent: {}", message);
              ObjectMapper objectMapper = new ObjectMapper();
              JsonNode data = message.get("data");
              if (data == null) return null;
              HuobiOrderEvent event =
                  objectMapper.readValue(data.traverse(), HuobiOrderEvent.class);
              if (event == null) return null;
              return event;
            })
        .filter(
            event -> {
              if (event == null) return false;
              String eventType = event.getEventType();
              if (eventType == null) return false;
              return eventType.equals("trade");
            })
        .map(
            event -> {
              return HuobiAdapters.adaptTrade(event);
            });
  }
}
