package polarthicket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import model.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import response.CustomerResponse;
import utils.CustomerUtils;
import utils.ServerInformation;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerTest {
    private static final String baseUrl = "https://polar-thicket-63660.herokuapp.com/";
    private static final String customerEndpoint = "api/v1/customers/";

    private RequestSpecification requestSpecification = given().
            baseUri(baseUrl).
            basePath(customerEndpoint).
            contentType(ContentType.JSON);
    private ResponseSpecBuilder responseSpecBuilder;
    private ResponseSpecification responseSpecification;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    private String currentCustomerId = null;

    @Test
    @Order(1)
    public void checkServerInformation() {
        responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectHeader(String.valueOf(ServerInformation.Server), "Cowboy");
        responseSpecBuilder.expectHeader(String.valueOf(ServerInformation.Connection), "keep-alive");
        responseSpecBuilder.expectHeader(String.valueOf(ServerInformation.ContentType), "application/json;charset=UTF-8");
        responseSpecBuilder.expectHeader(String.valueOf(ServerInformation.TransferEncoding), "chunked");
        responseSpecBuilder.expectHeader(String.valueOf(ServerInformation.Via), "1.1 vegur");
        responseSpecification = responseSpecBuilder.build();

        given().spec(requestSpecification).
                when().then().
                spec(responseSpecification);
    }

    @Test
    @Order(2)
    public void GETAllCustomers() {
        given().
                when().
                get(baseUrl + customerEndpoint).
                then().statusCode(200).log().all();
    }

    @Test
    @Order(3)
    public void GETAllCustomersWithRequestSpecification() {
        given().
                spec(requestSpecification).
                when().then().statusCode(200);
    }

    @Test
    @Order(4)
    public void createCustomerWithStringPayloadUsingRequestSpecification() {
        String customerPayload = "{\n" +
                "    \"firstName\": \"Cristian\",\n" +
                "    \"lastName\": \"Popescu\"\n" +
                "}";


        given().spec(requestSpecification).
                contentType(ContentType.JSON).
                body(customerPayload).post().
                then().statusCode(201);
    }

    @Test
    @Order(5)
    public void createCustomerWithStringPayloadUsingURLStringBuilder() {
        StringBuilder URL = new StringBuilder();
        URL.append(baseUrl);
        URL.append(customerEndpoint);

        String customerPayload = "{\n" +
                "    \"firstName\": \"Cristian\",\n" +
                "    \"lastName\": \"Faina\"\n" +
                "}";

        given().when().contentType(ContentType.JSON)
                .body(customerPayload).post(URL.toString()).
                then().statusCode(201);

    }

    @Test
    @Order(6)
    public void checkPreviouslyCreatedCustomer() {
        currentCustomerId = CustomerUtils.getLastCreatedCustomerId();
        System.out.println("The last customer created had the id " + currentCustomerId);
        given().when().get(baseUrl + customerEndpoint + currentCustomerId).then()
                .statusCode(200);

        CustomerResponse customerResponse = given().
                when().
                get(baseUrl + customerEndpoint + currentCustomerId).
                as(CustomerResponse.class);
        assertEquals(customerResponse.getFirstName(), "Cristian");
        assertEquals(customerResponse.getLastName(), "Faina");
    }

    @Test
    @Order(7)
    public void updatePreviouslyCreatedCustomer() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("Robert");
        updatedCustomer.setLastName("De Niro");
        String updatedDataPayload = null;
        try {
            updatedDataPayload = MAPPER.writeValueAsString(updatedCustomer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        given().when().contentType("application/json")
                .body(updatedDataPayload).
                put(baseUrl + customerEndpoint + currentCustomerId).
                then().statusCode(200);
    }

    @Test
    @Order(8)
    public void checkPreviouslyUpdatedCustomer() {
        System.out.println("The last customer created had the id " + currentCustomerId);
        given().when().get(baseUrl + customerEndpoint + currentCustomerId).then()
                .statusCode(200);

        CustomerResponse customerResponse = given().
                when().
                get(baseUrl + customerEndpoint + currentCustomerId).
                as(CustomerResponse.class);
        assertEquals(customerResponse.getFirstName(), "Robert");
        assertEquals(customerResponse.getLastName(), "De Niro");
    }

    @Test
    @Order(9)
    public void deleteCustomerTest() {
        System.out.println("The customer with the id " +
                currentCustomerId + " will be deleted");
        given().when().
                delete(baseUrl + customerEndpoint + currentCustomerId).
                then().statusCode(200);
    }

    @Test
    @Order(10)
    public void checkDeletedCustomerTest() {
        given().when().
                get(baseUrl + customerEndpoint + currentCustomerId).
                then().statusCode(404);
        System.out.println("The customer with the id " +
                currentCustomerId + " was deleted");
    }
}
