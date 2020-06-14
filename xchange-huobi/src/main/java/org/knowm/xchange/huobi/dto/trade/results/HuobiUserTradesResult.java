package org.knowm.xchange.huobi.dto.trade.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.huobi.dto.HuobiResult;
import org.knowm.xchange.huobi.dto.trade.HuobiUserTrade;

public class HuobiUserTradesResult extends HuobiResult<HuobiUserTrade[]> {

  public HuobiUserTradesResult(
      @JsonProperty("status") String status,
      @JsonProperty("data") HuobiUserTrade[] result,
      @JsonProperty("err-code") String errCode,
      @JsonProperty("err-msg") String errMsg) {
    super(status, errCode, errMsg, result);
  }
}
