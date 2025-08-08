package com.linktic.productservice.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiListResponse<T> {
    private List<JsonApiData<T>> data;
    private Map<String, String> links;
    private Map<String, Object> meta;
}