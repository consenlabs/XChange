package info.bitrich.xchangestream.huobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.reactivex.Completable;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuobiStreamingAuthService extends JsonNettyStreamingService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  private final String apiUrl;
  private final String apiKey;
  private final String secretKey;

  public HuobiStreamingAuthService(String apiUrl, String apiKey, String secretKey) {
    super(apiUrl, Integer.MAX_VALUE, Duration.ofSeconds(5), Duration.ofSeconds(20), 20);
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
    this.secretKey = secretKey;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    String ch = null;

    if (message.has("ch")) {
      ch = message.get("ch").textValue();
    }

    if (message.has("action")) {
      String action = message.get("action").textValue();
      if (action.equals("ping")) {
        JsonNode data = message.get("data");
        if (data != null) {
          long ping = data.get("ts").longValue();
          String reply = String.format("{\"action\": \"pong\",\"data\": {\"ts\": %d}}", ping);
          sendMessage(reply);
        }
      }
    }

    if (message.has("code")) {
      int code = message.get("code").asInt();
      if (code == 200) {
        LOG.debug("Subscribe [{}] is ok", ch);
      } else {
        LOG.debug("Subscribe [{}] is not ok", ch);
      }
    }
    return ch;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    JSONObject json = new JSONObject();
    if (channelName.equals("auth")) {
      json.put("ch", channelName);
      json.put("action", "req");
      JSONObject params = getAuthParams(apiUrl, apiKey, secretKey);
      json.put("params", params);
      LOG.debug("auth params: {}", params);
    } else {
      json.put("ch", channelName);
      json.put("action", "sub");
    }
    return json.toJSONString();
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    JSONObject json = new JSONObject();
    json.put("ch", channelName);
    json.put("action", "unsub");

    return json.toJSONString();
  }

  @Override
  protected Completable openConnection() {
    return super.openConnection()
        .doOnComplete(
            () -> {
              subscribeChannel("auth")
                  .subscribe(
                      message -> {
                        LOG.debug("message from auth: {}", message);
                      });
            });
  }

  JSONObject getAuthParams(String apiUrl, String apiKey, String secretKey) {
    HuobiApiSignature as = new HuobiApiSignature();
    UrlParamsBuilder builder = UrlParamsBuilder.build();
    try {
      URI uri = new URI(apiUrl);
      as.createSignature(apiKey, secretKey, "GET", uri.getHost(), uri.getPath(), builder);
    } catch (Exception e) {
      LOG.error("failed to create signature: ", e);
      return null;
    }
    JSONObject signObj = JSON.parseObject(builder.buildUrlToJsonString());
    signObj.put("authType", "api");
    return signObj;
  }
}
