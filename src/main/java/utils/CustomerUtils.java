package utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public abstract class CustomerUtils {
    private static final String baseUrl = "https://polar-thicket-63660.herokuapp.com/";
    private static final String customerEndpoint = "api/v1/customers/";

    public static String getLastCreatedCustomerId() {
        Response responseAllCustomers = given().when().get(baseUrl + customerEndpoint).then()
                .extract().response();
        String customerUrl = responseAllCustomers.path("customers.customerUrl[0]");
        String[] customerData = customerUrl.split("/");
        String customerId = customerData[4];
        return customerId;
    }
}
