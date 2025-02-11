package com.example.teamcity.api.request;

import com.example.teamcity.api.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

public class Request {
    /**
     * Request contains changing parameters of request, such as specification, endpoint ( relative URL, model)
     */
    protected final RequestSpecification spec;
    protected final Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint){
        this.spec = spec;
        this.endpoint = endpoint;
    }
}
