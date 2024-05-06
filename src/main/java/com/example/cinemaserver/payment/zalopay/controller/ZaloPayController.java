package com.example.cinemaserver.payment.zalopay.controller;

import com.example.cinemaserver.payment.zalopay.config.ZaloPayConfig;
import com.example.cinemaserver.payment.zalopay.request.CreateZaloPayRequest;
import com.example.cinemaserver.payment.zalopay.request.GetZaloPayRequest;
import com.example.cinemaserver.payment.zalopay.request.RefundZaloPayRequest;
import com.example.cinemaserver.payment.zalopay.response.CreateZaloPayResponse;
import com.example.cinemaserver.payment.zalopay.response.GetZaloPayResponse;
import com.example.cinemaserver.payment.zalopay.response.RefundZaloPayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/payment/zalopay")
public class ZaloPayController {
    private static final String ORDER_CREATE_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";
    private static final String ORDER_STATUS_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/query";
    private static final String ORDER_REFUND_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/refund";
    private static final String CALLBACKURL = "http://localhost:5173/?list=";
    private static final String REDIRECTURL = "http://localhost:5173/?list=";
    private static final String key1="9phuAOYhan4urywHTh0ndEXiV3pKHr5Q";
    private static final String key2="Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3";
    private static final String appId="553";

    @GetMapping("/create_payment")
    @PreAuthorize("@userService.getUserById(#createZaloPayRequest.userId).email==principal.username")
    public ResponseEntity<?> createPayment(@ModelAttribute CreateZaloPayRequest createZaloPayRequest) {
        try{
            String apptransid = ZaloPayConfig.getCurrentTimeString("yyMMdd") + "_" + new Date().getTime();
            StringBuilder redirectUrl= new StringBuilder(REDIRECTURL);
            for(Long seatSchedule: createZaloPayRequest.getSeatScheduleId()){
                redirectUrl.append(seatSchedule.toString()).append(",");
            }
            redirectUrl.deleteCharAt(redirectUrl.length() - 1);
            Map<String, Object> order = new HashMap<String, Object>() {{
                put("app_id", appId);
                put("app_trans_id", apptransid);
                // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
                put("app_time", System.currentTimeMillis()); // miliseconds
                put("app_user", "LuxC");
                put("amount", createZaloPayRequest.getAmount());
                put("description", "LucX - Payment for the order");
                put("bank_code", "");
                put("item", "[]");
                put("embed_data", String.format("{\"redirecturl\": \"%s\"}", redirectUrl));
                put("callback_url", "");
            }};

            String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|" + order.get("amount")
                    + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|" + order.get("item");
            order.put("mac", ZaloPayConfig.hmacSha256(key1, data));

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(ORDER_CREATE_ENDPOINT);

            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> e : order.entrySet()) {

                params.add(new BasicNameValuePair(e.getKey(), e.getValue() != null ? e.getValue().toString() : ""));
            }

            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse res = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder resultJsonStr = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                resultJsonStr.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resData = objectMapper.readValue(resultJsonStr.toString(), Map.class);
            resData.put("invoice_code", apptransid);
            ObjectMapper objectMapper1 = new ObjectMapper();
            return ResponseEntity.ok(objectMapper1.convertValue(resData, CreateZaloPayResponse.class));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating payment");
        }
    }

    @PostMapping("/get_payment")
    @PreAuthorize("@userService.getUserById(#getZaloPayRequest.userId).email==principal.username")
    public ResponseEntity<?> getPayment(@ModelAttribute GetZaloPayRequest getZaloPayRequest) {
        try{
            String data = appId + "|" + getZaloPayRequest.getApp_trans_id() + "|" + key1; // appid|app_trans_id|key1
            String mac = ZaloPayConfig.hmacSha256(key1, data);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("app_id", appId));
            params.add(new BasicNameValuePair("app_trans_id", getZaloPayRequest.getApp_trans_id()));
            params.add(new BasicNameValuePair("mac", mac));

            URIBuilder uri = new URIBuilder(ORDER_STATUS_ENDPOINT);
            uri.addParameters(params);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(uri.build());
            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse res = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder resultJsonStr = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {

                resultJsonStr.append(line);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map= objectMapper.readValue(resultJsonStr.toString(), Map.class);
            ObjectMapper objectMapper1 = new ObjectMapper();
            return ResponseEntity.ok(objectMapper1.convertValue(map, GetZaloPayResponse.class));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting payment information");
        }
    }

    @PostMapping("/refund")
    @PreAuthorize("@userService.getUserById(#refundZaloPayRequest.userId).email==principal.username")
    public ResponseEntity<?> returnZaloPay(@ModelAttribute RefundZaloPayRequest refundZaloPayRequest) {
        try{
            Map<String, Object> order = new HashMap<>(){{
                put("app_id", appId);
                put("zp_trans_id", refundZaloPayRequest.getZp_trans_ip());
                put("m_refund_id", ZaloPayConfig.getCurrentTimeString("yyMMdd") +"_"+ appId +"_"+
                        System.currentTimeMillis() + "" + (111 + new Random().nextInt(888)));
                put("timestamp", System.currentTimeMillis());
                put("amount", refundZaloPayRequest.getAmount());
                put("description", refundZaloPayRequest.getDescription());
            }};

            String data = order.get("app_id") +"|"+ order.get("zp_trans_id") +"|"+ order.get("amount")
                    +"|"+ order.get("description") +"|"+ order.get("timestamp");
            order.put("mac", ZaloPayConfig.hmacSha256(key1, data));


            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(ORDER_REFUND_ENDPOINT);

            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> e : order.entrySet()) {
                params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
            }

            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse res = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder resultJsonStr = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                resultJsonStr.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map= objectMapper.readValue(resultJsonStr.toString(), Map.class);
            ObjectMapper objectMapper1 = new ObjectMapper();
            return ResponseEntity.ok(objectMapper1.convertValue(map, RefundZaloPayResponse.class));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error refunding payment");
        }
    }
}
