package response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AllCustomerResponse {
    @JsonProperty("categories")
    private List<CustomerResponse> customers;
}
