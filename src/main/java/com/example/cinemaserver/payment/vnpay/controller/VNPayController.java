package com.example.cinemaserver.payment.vnpay.controller;

import com.example.cinemaserver.payment.vnpay.config.VNPayConfig;
import com.example.cinemaserver.payment.vnpay.request.CreatePaymentRequest;
import com.example.cinemaserver.payment.vnpay.request.GetPaymentRequest;
import com.example.cinemaserver.payment.vnpay.request.RefundRequest;
import com.example.cinemaserver.payment.vnpay.response.CreatePaymentResponse;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/payment/vnpay")
public class VNPayController {
    @GetMapping("/create_payment")
    @PreAuthorize("@userService.getUserById(#createPaymentRequest.userId).email==principal.username")
    public ResponseEntity<?> createPayment(HttpServletRequest req, @ModelAttribute CreatePaymentRequest createPaymentRequest) {
        try{
            String orderType = "other";
            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_IpAddr = VNPayConfig.getIpAddress(req);
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

            StringBuilder vnp_ReturnUrl= new StringBuilder(VNPayConfig.vnp_ReturnUrl);
            for(Long seatSchedule: createPaymentRequest.getSeatScheduleId()){
                vnp_ReturnUrl.append(seatSchedule.toString()).append(",");
            }
            vnp_ReturnUrl.deleteCharAt(vnp_ReturnUrl.length() - 1);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType",orderType);
            vnp_Params.put("vnp_Amount", String.valueOf(createPaymentRequest.getAmount()*100L));
            vnp_Params.put("vnp_ReturnUrl", String.valueOf(vnp_ReturnUrl));
            vnp_Params.put("vnp_IpAddr",vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

            CreatePaymentResponse createPaymentResponse=new CreatePaymentResponse();
            createPaymentResponse.setURL(paymentUrl);
            return ResponseEntity.ok(createPaymentResponse);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment");
        }
    }
    // return URL sau khi thanh toan
    //http://localhost:5173/?list=1,2,3,4
    // &vnp_Amount=1500000
    // &vnp_BankCode=NCB
    // &vnp_BankTranNo=VNP14395816
    // &vnp_CardType=ATM
    // &vnp_OrderInfo=Thanh+toan+don+hang%3A88377484
    // &vnp_PayDate=20240429152805
    // &vnp_ResponseCode=00
    // &vnp_TmnCode=4OAZQBHM
    // &vnp_TransactionNo=14395816
    // &vnp_TransactionStatus=00
    // &vnp_TxnRef=88377484
    // &vnp_SecureHash=b6c098103ccf248cec513913bdf5d42d1386dab06ec35d040b906c86f0dbf9fc093b6ef4a0516b4390f7d52efbcd80a5371c928222a30770be03058942275098



    // tạm thời chưa dùng được, thằng vnpay nó ngu quá
    @GetMapping ("/refund")
    @PreAuthorize("@userService.getUserById(#refundRequest.userId).email==principal.username")
    public void refundPayment(HttpServletRequest req, @ModelAttribute RefundRequest refundRequest) throws IOException {

        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_TransactionType = "02";
        String vnp_TxnRef = refundRequest.getVnp_TxnRef();
        String vnp_Amount = String.valueOf(refundRequest.getAmount());
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef +" do gap mot so su co";
        String vnp_TransactionNo = "";
        String vnp_TransactionDate = refundRequest.getVnp_PayDate();
        String vnp_CreateBy = "anv@vnpay.vn";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject ();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
        {
            vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        }

        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL (VNPayConfig.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());
    }

    // tạm thời chưa dùng được, thằng vnpay nó ngu quá
    @GetMapping("/get_payment")
    @PreAuthorize("@userService.getUserById(#getPaymentRequest.userId).email==principal.username")
    public void getPayment(HttpServletRequest req, @ModelAttribute GetPaymentRequest getPaymentRequest) throws IOException{
        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_TxnRef = getPaymentRequest.getVnp_TxnRef();//
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = "";
        String vnp_TransactionDate = getPaymentRequest.getVnp_TransactionDate();//

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        JsonObject  vnp_Params = new JsonObject ();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransactionDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(VNPayConfig.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());
    }
}
