package com.amanatpay.onramp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JwtTest {

    public static void main(String[] args) {
        String jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImd0eSI6WyJwYXNzd29yZCJdLCJraWQiOiJsR1luVnBVSjlaNDhiYUtEeWFhb05rTTRSczgifQ.eyJhdWQiOiJmNzQyNzk0OC0zNGFlLTRkYzAtOTIwZC1mNTAzMTZkNjdkNzAiLCJleHAiOjE3MjUwNDAzMjksImlhdCI6MTcyNTAzNjcyOSwiaXNzIjoibG9jYWxob3N0OjkwMTEiLCJzdWIiOiI3YWJhZTU1NC1kYjZkLTRmOTItYTIwZC0zYjU4ZmI0MmNjZWQiLCJqdGkiOiI5ODRmODY1NC03YTZmLTQ4ZWQtYTJjYy05ZjlkYWM2YWI3YWIiLCJhdXRoZW50aWNhdGlvblR5cGUiOiJQQVNTV09SRCIsImFwcGxpY2F0aW9uSWQiOiJmNzQyNzk0OC0zNGFlLTRkYzAtOTIwZC1mNTAzMTZkNjdkNzAiLCJyb2xlcyI6WyJBRE1JTiIsIlNVUFBPUlQiXSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImF1dGhfdGltZSI6MTcyNTAzNjcyOSwidGlkIjoiY2I2Njg0NWYtODcyZS0wNTQ5LTY3ZGUtNDk0MWI2MDQ2YjY5In0.qsyMYJ7N1bA0UJ-keExIcOEi98pC_PurNjvHag22rJ4FoRSKIj7kOFz9r_BrAh0poDSoSvquCBrHbuVLEETyTUmhx_Cb6Z3t9XyBxNqdZ7yRh3EIievUyqlcWYFxnCp1FAUA_mQ1czTGSG2ucMkPLMsTuEvp1ntNK1b0RRqWPlxGH_aQT_MdrkrSc20C2I_Nwj20c2OX1IjGxK9VX91n2AWBQDsnjSgC8DoYkJjoSDwTIRnjBRT2-T0gD1DDOZ3gHH--Lpa_kwVQWnBRwf3CGsBCKlALBr3Tg4GX3uKNvmtBSaEZPrQ657b_5HLwzAP92zQCi7tY6Ndq3ajW_hcilw";
        String url = "http://localhost:8080/secure";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // Create entity
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Print response
        System.out.println("Response: " + response.getBody());
    }
}