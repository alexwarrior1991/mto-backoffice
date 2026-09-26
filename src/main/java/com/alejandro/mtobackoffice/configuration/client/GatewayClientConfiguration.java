package com.alejandro.mtobackoffice.configuration.client;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.SectionInsulatorClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.error.ApiErrorDecoder;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.KeycloakClientRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.ObjectMapper;

/**
 * El cliente HTTP hacia el gateway y las interfaces declarativas que se montan sobre el.
 *
 * <p>Un solo {@link RestClient} con dos interceptores —el Bearer de la persona y el
 * {@code X-Correlation-Id}— y un traductor de errores. No hay circuit breaker aqui: el gateway ya
 * tiene el suyo por servicio y su 503 llega traducido como {@code ServiceUnavailableApiException}.
 * Los timeouts los fija {@code spring.http.clients.*}.</p>
 */
@Configuration
@EnableConfigurationProperties({GatewayProperties.class, CorrelationProperties.class})
public class GatewayClientConfiguration {

    /**
     * Manager que no necesita peticion HTTP en curso: lee el cliente autorizado que guardo el login
     * (Boot lo guarda por principal, en {@code AuthenticatedPrincipalOAuth2AuthorizedClientRepository}
     * sobre este mismo servicio) y solo sabe refrescar, que es lo unico que hace falta despues de
     * entrar.
     */
    @Bean
    public OAuth2AuthorizedClientManager userAuthorizedClientManager(ClientRegistrationRepository clientRegistrations,
                                                                     OAuth2AuthorizedClientService authorizedClients) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrations, authorizedClients);
        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .refreshToken()
                .build());
        return manager;
    }

    @Bean
    public UserTokenProvider userTokenProvider(OAuth2AuthorizedClientManager userAuthorizedClientManager) {
        return new UserTokenProvider(userAuthorizedClientManager, KeycloakClientRegistrations.REGISTRATION_ID);
    }

    @Bean
    public ApiErrorDecoder apiErrorDecoder(ObjectMapper objectMapper, CorrelationProperties correlation) {
        return new ApiErrorDecoder(objectMapper, correlation.headerName());
    }

    @Bean
    public RestClient gatewayRestClient(RestClient.Builder builder, GatewayProperties gateway,
                                        CorrelationProperties correlation, UserTokenProvider tokens,
                                        ApiErrorDecoder errors) {
        return gatewayRestClient(builder, gateway.baseUrl(), correlation.headerName(), tokens, errors);
    }

    /** Fabrica compartida con los tests, para que prueben los interceptores y el traductor reales. */
    public static RestClient gatewayRestClient(RestClient.Builder builder, String baseUrl, String correlationHeader,
                                               UserTokenProvider tokens, ApiErrorDecoder errors) {
        return builder.clone()
                .baseUrl(baseUrl)
                .requestInterceptor(new BearerTokenInterceptor(tokens))
                .requestInterceptor(new CorrelationIdInterceptor(correlationHeader))
                .defaultStatusHandler(HttpStatusCode::isError, errors)
                .build();
    }

    @Bean
    public MaterialClient materialClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(MaterialClient.class);
    }

    @Bean
    public WarehouseClient warehouseClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(WarehouseClient.class);
    }

    @Bean
    public SupplierClient supplierClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(SupplierClient.class);
    }

    @Bean
    public ProjectClient projectClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(ProjectClient.class);
    }

    @Bean
    public AssemblyClient assemblyClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(AssemblyClient.class);
    }

    @Bean
    public MovementClient movementClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(MovementClient.class);
    }

    @Bean
    public ReservationClient reservationClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(ReservationClient.class);
    }

    @Bean
    public LovClient lovClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(LovClient.class);
    }

    @Bean
    public ExecutionPackageClient executionPackageClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(ExecutionPackageClient.class);
    }

    @Bean
    public StationClient stationClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(StationClient.class);
    }

    @Bean
    public TrackClient trackClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(TrackClient.class);
    }

    @Bean
    public ProfileClient profileClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(ProfileClient.class);
    }

    @Bean
    public DisconnectorClient disconnectorClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(DisconnectorClient.class);
    }

    @Bean
    public SectionInsulatorClient sectionInsulatorClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(SectionInsulatorClient.class);
    }

    @Bean
    public BusinessEntityClient businessEntityClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(BusinessEntityClient.class);
    }

    @Bean
    public JobsClient jobsClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(JobsClient.class);
    }

    @Bean
    public UsersClient usersClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(UsersClient.class);
    }

    @Bean
    public OrderClient orderClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(OrderClient.class);
    }

    @Bean
    public AssetClient assetClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(AssetClient.class);
    }

    @Bean
    public MaintenanceCatalogClient maintenanceCatalogClient(RestClient gatewayRestClient) {
        return proxyFactory(gatewayRestClient).createClient(MaintenanceCatalogClient.class);
    }

    public static HttpServiceProxyFactory proxyFactory(RestClient restClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    }
}
