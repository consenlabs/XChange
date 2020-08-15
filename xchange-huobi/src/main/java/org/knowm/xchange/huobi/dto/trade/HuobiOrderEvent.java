package org.knowm.xchange.huobi.dto.trade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuobiOrderEvent {
  private final boolean aggressor;
  private final String clientOrderId;
  private final int errCode;
  private final String errMessage;
  private final String eventType;
  private final long lastActTime;
  private final long orderCreateTime;
  private final long orderId;
  private final String orderPrice;
  private final String orderSide;
  private final String orderSize;
  private final String orderStatus;
  private final String type;
  private final String orderValue;
  private final String remainAmt;
  private final String symbol;
  private final long tradeId;
  private final String tradePrice;
  private final long tradeTime;
  private final String tradeVolume;

  public HuobiOrderEvent(
      @JsonProperty("aggressor") boolean aggressor,
      @JsonProperty("clientOrderId") String clientOrderId,
      @JsonProperty("errCode") int errCode,
      @JsonProperty("errMessage") String errMessage,
      @JsonProperty("eventType") String eventType,
      @JsonProperty("lastActTime") long lastActTime,
      @JsonProperty("orderCreateTime") long orderCreateTime,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("orderPrice") String orderPrice,
      @JsonProperty("orderSide") String orderSide,
      @JsonProperty("orderSize") String orderSize,
      @JsonProperty("orderStatus") String orderStatus,
      @JsonProperty("orderValue") String orderValue,
      @JsonProperty("remainAmt") String remainAmt,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("tradeId") long tradeId,
      @JsonProperty("tradePrice") String tradePrice,
      @JsonProperty("tradeTime") long tradeTime,
      @JsonProperty("tradeVolume") String tradeVolume,
      @JsonProperty("type") String type) {
    this.aggressor = aggressor;
    this.clientOrderId = clientOrderId;
    this.errCode = errCode;
    this.errMessage = errMessage;
    this.eventType = eventType;
    this.lastActTime = lastActTime;
    this.orderCreateTime = orderCreateTime;
    this.orderId = orderId;
    this.orderPrice = orderPrice;
    this.orderSide = orderSide;
    this.orderSize = orderSize;
    this.orderStatus = orderStatus;
    this.type = type;
    this.orderValue = orderValue;
    this.remainAmt = remainAmt;
    this.symbol = symbol;
    this.tradeId = tradeId;
    this.tradePrice = tradePrice;
    this.tradeTime = tradeTime;
    this.tradeVolume = tradeVolume;
  }

  public boolean isLimit() { // startswith to support -fok and -ioc
    return getType().startsWith("buy-limit")
        || getType().startsWith("sell-limit")
        || getType().startsWith("buy-ioc")
        || getType().startsWith("sell-ioc");
  }

  public boolean isMarket() {
    return getType().equals("buy-market") || getType().equals("sell-market");
  }

  public boolean isStop() {
    return getType().startsWith("buy-stop") || getType().startsWith("sell-stop");
  }
}
