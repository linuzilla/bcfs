package ncu.cc.commons.api;

import ncu.cc.commons.api.exceptions.APINotFoundException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public interface MetaApi {
	Mono<ClientResponse> apiCall(String name, Object... objects);
	Set<String> listAPIs();

	WebClient.ResponseSpec retrieveApiCall(String name, Object... objects) throws APINotFoundException;

	WebClient.RequestBodySpec prepareApiCall(String name, Object... objects) throws APINotFoundException;
}
