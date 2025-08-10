package com.linktic.inventaryservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.linktic.inventaryservice.dto.ProductAttributes;
import com.linktic.inventaryservice.integration.feign.ProductClient;
import com.linktic.inventaryservice.model.JsonApiResponse;

@SpringBootTest
@ActiveProfiles("test")
class ProductClientIT {

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private ProductClient productClient;

    @DynamicPropertySource
    static void configureFeignUrl(DynamicPropertyRegistry registry) {
        registry.add("integration.product-service.url", wiremock::baseUrl);
    }

    @Test
    void shouldReturnProductWhenExists() {
        wiremock.stubFor(get(urlEqualTo("/123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "data": {
                                "type": "product",
                                "id": "123",
                                "attributes": {
                                  "name": "Laptop",
                                  "price": 1500
                                }
                              }
                            }
                            """)
                        .withStatus(200)));

        JsonApiResponse<ProductAttributes> response = productClient.getByProductById("123");

        assertThat(response).isNotNull();
        assertThat(response.getData().getType()).isEqualTo("product");
        assertThat(response.getData().getId()).isEqualTo("123");
        assertThat(response.getData().getAttributes().getName()).isEqualTo("Laptop");
        assertThat(response.getData().getAttributes().getPrice()).isEqualTo(1500L);
    }
}