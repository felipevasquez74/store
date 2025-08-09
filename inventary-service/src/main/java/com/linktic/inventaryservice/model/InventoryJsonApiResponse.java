package com.linktic.inventaryservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linktic.inventaryservice.dto.ProductAttributes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryJsonApiResponse {
	
	private static final String INVENTORIES = "inventories";
	private static final String PRODUCTS = "products";
	
    private Data data;
    private List<IncludedProduct> included;

    public InventoryJsonApiResponse(String productId, Integer quantity) {
        this.data = new Data(
            productId,
            INVENTORIES,
            new Attributes(quantity),
            new Relationships(
                new RelationshipData(
                    new RelationshipData.DataInner(productId, PRODUCTS)
                )
            )
        );
    }

    public InventoryJsonApiResponse(String productId, Integer quantity, ProductAttributes productAttributes) {
        this.data = new Data(
            productId,
            "inventories",
            new Attributes(quantity),
            new Relationships(
                new RelationshipData(
                    new RelationshipData.DataInner(productId, PRODUCTS)
                )
            )
        );
        this.included = List.of(new IncludedProduct(productId, PRODUCTS, productAttributes));
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String id;
        private String type = INVENTORIES;
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
            private String type = PRODUCTS;
        }
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IncludedProduct {
        private String id;
        private String type = PRODUCTS;
        private ProductAttributes attributes;
    }
}
