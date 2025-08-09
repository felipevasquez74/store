package com.linktic.inventaryservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryJsonApiResponse {
    private Data data;

    public InventoryJsonApiResponse(String productId, Integer quantity) {
        this.data = new Data(
            productId,
            "inventories",
            new Attributes(quantity),
            new Relationships(
                new RelationshipData(
                    new RelationshipData.DataInner(productId, "products")
                )
            )
        );
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String id;
        private String type = "inventories";
        private Attributes attributes;
        private Relationships relationships;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attributes {
        private Integer quantity;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Relationships {
        private RelationshipData product;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationshipData {
        private DataInner data;

        @lombok.Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class DataInner {
            private String id;
            private String type = "products";
        }
    }
}