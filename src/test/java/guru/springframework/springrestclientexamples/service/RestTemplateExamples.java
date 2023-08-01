package guru.springframework.springrestclientexamples.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertZ on 2023-08-01.
 */
public class RestTemplateExamples {
    public static final String API_ROOT = "https://api.predic8.de/shop/v2";

    @Test
    public void getCustomers() throws Exception {
        String apiUrl = API_ROOT + "/customers";

        RestTemplate restTemplate = new RestTemplate();

        JsonNode jsonNode = restTemplate.getForObject(apiUrl, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void getProducts() throws Exception {
        String apiUrl = API_ROOT + "/products";

        RestTemplate restTemplate = new RestTemplate();

        JsonNode jsonNode = restTemplate.getForObject(apiUrl, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void createProduct() throws Exception {
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "Ticket");
        postMap.put("price", "100");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void updateProduct() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "Bread");
        postMap.put("price", "11.8");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        Integer id = jsonNode.get("id").intValue();

        System.out.println("Created product id: " + id);

        postMap.put("name", "Bread");
        postMap.put("price", "12.1");

        restTemplate.put(apiUrl + id, postMap);

        JsonNode updatedNode = restTemplate.getForObject(apiUrl + id, JsonNode.class);

        System.out.println(updatedNode.toString());

    }

    @Test(expected = ResourceAccessException.class)
    public void updateProductUsingPatchSunHttp() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "Apple");
        postMap.put("price", "7.1");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        Integer id = jsonNode.get("id").intValue();

        System.out.println("Created product id: " + id);

        postMap.put("price", "3.1");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(postMap, headers);

        //fails due to sun.net.www.protocol.http.HttpURLConnection not supporting patch
        JsonNode updatedNode = restTemplate.patchForObject(apiUrl + id, entity, JsonNode.class);

        System.out.println(updatedNode.toString());

    }

    @Test
    public void updateCustomerUsingPatch() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/products/";

        // Use Apache HTTP client factory
        //see: https://github.com/spring-cloud/spring-cloud-netflix/issues/1777
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "Apple");
        postMap.put("price", "7.1");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        Integer id = jsonNode.get("id").intValue();

        System.out.println("Created product id: " + id);

        postMap.put("name", "Plum");
        postMap.put("price", "3.1");

        //example of setting headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(postMap, headers);

        JsonNode updatedNode = restTemplate.patchForObject(apiUrl + id, entity, JsonNode.class);

        System.out.println(updatedNode.toString());
    }

    @Test(expected = HttpClientErrorException.class)
    public void deleteCustomer() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/customers/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("firstname", "Les");
        postMap.put("lastname", "Claypool");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("customer_url").textValue();

        String id = customerUrl.split("/")[3];

        System.out.println("Created customer id: " + id);

        restTemplate.delete(apiUrl + id); //expects 200 status

        System.out.println("Customer deleted");

        //should go boom on 404
        restTemplate.getForObject(apiUrl + id, JsonNode.class);
    }
}
