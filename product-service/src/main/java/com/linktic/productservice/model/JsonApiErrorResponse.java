package com.linktic.productservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiErrorResponse {

    private List<Error> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error {
        private String status;
        private String title;
        private String detail;
    }
}