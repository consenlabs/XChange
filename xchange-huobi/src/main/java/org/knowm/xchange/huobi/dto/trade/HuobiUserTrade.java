package org.knowm.xchange.huobi.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class HuobiUserTrade {

  private final String id;
  private final String orderId;
  private final String matchId;
  private final String symbol;
  private final String type;
  private final String source;
  private final BigDecimal price;
  private final BigDecimal filledAmount;
  private final BigDecimal filledFees;
  private final Date createdAt;
  private final String tradeId;
  private final String role;
  private final BigDecimal filledPoints;
  private final String feeDeductCurrency;

  public HuobiUserTrade(
      @JsonProperty("id") String id,
      @JsonProperty("order-id") String orderId,
      @JsonProperty("match-id") String matchId,
      @JsonProperty("symbol") String symbol,
      @JsonProperty("type") String type,
      @JsonProperty("source") String source,
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("filled-amount") BigDecimal filledAmount,
      @JsonProperty("filled-fees") BigDecimal filledFees,
      @JsonProperty("created-at") Date createdAt,
      @JsonProperty("trade-id") String tradeId,
      @JsonProperty("role") String role,
      @JsonProperty("filled-points") BigDecimal filledPoints,
      @JsonProperty("fee-deduct-currency") String feeDeductCurrency) {
    this.id = id;
    this.orderId = orderId;
    this.matchId = matchId;
    this.symbol = symbol;
    this.type = type;
    this.source = source;
    this.price = price;
    this.filledAmount = filledAmount;
    this.filledFees = filledFees;
    this.createdAt = createdAt;
    this.tradeId = tradeId;
    this.role = role;
    this.filledPoints = filledPoints;
    this.feeDeductCurrency = feeDeductCurrency;
  }
}
