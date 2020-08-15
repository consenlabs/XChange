package info.bitrich.xchangestream.huobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
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
    super(apiUrl, Integer.MAX_VALUE, Duration.ofSeconds(5), Duration.ofSeconds(10), 20);
    this.apiUrl = apiUrl;
    this.apiKey = apiKey;
    this.secretKey = secretKey;
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    String ch = null;
    String action = null;

    if (message.has("ch")) {
      ch = message.get("ch").textValue();
    }
    if (message.has("action")) {
      action = message.get("action").textValue();
    }

    if (message.has("code")) {
      int code = message.get("code").asInt();
      LOG.info("{} {}: {}", action, ch, code);
    }

    if (action != null && action.equals("ping")) {
      JsonNode data = message.get("data");
      if (data != null) {
        long ts = data.get("ts").longValue();
        String reply = String.format("{\"action\": \"pong\",\"data\": {\"ts\": %d}}", ts);
        sendMessage(reply);
      }
    }

    return ch;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    JSONObject json = new JSONObject();
    json.put("ch", channelName);
    json.put("action", "sub");

    return json.toJSONString();
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    return null;
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

  @Override
  public void resubscribeChannels() {
    LOG.debug("resubscribeChannels: ");
    JSONObject json = new JSONObject();
    json.put("ch", "auth");
    json.put("action", "req");
    JSONObject params = getAuthParams(apiUrl, apiKey, secretKey);
    json.put("params", params);
    sendMessage(json.toJSONString());

    try {
      Thread.sleep(2000);
    } catch (Exception e) {
    }
    super.resubscribeChannels();
  }
}
