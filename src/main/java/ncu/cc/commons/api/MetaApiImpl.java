package ncu.cc.commons.api;

import com.google.gson.Gson;
import ncu.cc.commons.api.exceptions.APINotFoundException;
import ncu.cc.commons.api.models.APIEntry;
import ncu.cc.commons.api.models.APIMetaData;
import ncu.cc.commons.api.models.APIMetaItem;
import ncu.cc.commons.utils.ReadfileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class MetaApiImpl implements MetaApi {
	private static final String USER_AGENT = "Reactive MetaApi Client/1.0";
	private static final Logger logger = LoggerFactory.getLogger(MetaApiImpl.class);
	
	protected static Pattern urlpattern1, urlpattern2, argpattern1, argpattern2, argpattern3;

	protected final String	baseURI;
	protected final String	accessToken;
	
	protected Map<String,APIEntry> apis;
	
	static {
		urlpattern1 = Pattern.compile("^(.*)@([a-z][a-z0-9]+)(.*)$");
		urlpattern2 = Pattern.compile("^(.*)\\$([0-9]+)(.*)$");
		argpattern1 = Pattern.compile("^([a-z][_a-z0-9]+)\\(\\$([0-9]+)\\)$");
		argpattern2 = Pattern.compile("^\\$(#?)([0-9]+)$");
		argpattern3 = Pattern.compile("^@([a-z][a-z0-9]+)$");
	}
	
	public MetaApiImpl(String metaFile, String baseURI, String accessToken) throws IOException {
		super();
		this.baseURI = baseURI;
		this.accessToken = accessToken;
		apis = new HashMap<String,APIEntry>();

		parseMetaData(ReadfileUtil.readFrom(metaFile));

		logger.info("Base: {}, {} apis registered", this.baseURI, apis.size());
	}

	protected void registEntry(APIMetaItem parent, String name, APIMetaItem item) {
		item.inherit(parent);
		
		if (item.apis != null) {
			for (Entry<String, APIMetaItem> subentry : item.apis.entrySet()) {
				registEntry(item, subentry.getKey(), subentry.getValue());
			}
		} else {
			logger.debug(name);
			this.apis.put(name, new APIEntry(name, item));
		}
	}
	
	protected void parseMetaData(InputStream in) throws IOException {
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null) {
			responseStrBuilder.append(inputStr);
		}

		APIMetaData meta = new Gson().fromJson(responseStrBuilder.toString(), APIMetaData.class);

		logger.debug(meta.description);
		
		APIMetaItem top = new APIMetaItem(meta);
		
		for (Entry<String, APIMetaItem> entry : meta.apis.entrySet()) {
			registEntry(top, entry.getKey(), entry.getValue());
		}
	}
	
	protected String getVariable(String varname) {
		return "";
	}
	
	protected String callArgFunction(String fcnName, String arg) {
		if ("base64".equals(fcnName)) {
			return Base64.getEncoder().encodeToString(arg.getBytes());
		} else {
			return arg;
		}
	}
	
	protected String generateURL(String path, Object...objects) {
		String url = path;
		Matcher m1 = urlpattern1.matcher(url);
		
		if (m1.find()) {
			url = m1.group(1) + getVariable(m1.group(2)) + m1.group(3);
		}
		
		String head = url;
		String tail = "";
		
		Matcher m2 = urlpattern2.matcher(url);
		
		while (m2.find()) {
			head = m2.group(1);
			int i = Integer.parseInt(m2.group(2));
			tail = objects[i - 1].toString() + m2.group(3) + tail;
			
			m2 = urlpattern2.matcher(head);
		}

		url = head + tail;
		
		return url;
	}
	
	protected Object generateDATA(String value, Object...objects) {
		Matcher m1 = argpattern1.matcher(value);
		
		if (m1.find()) {
			int i = Integer.parseInt(m1.group(2)) - 1;
			
			return callArgFunction(m1.group(1), objects[i].toString());
		} else {
			Matcher m2 = argpattern2.matcher(value);
			
			if (m2.find()) {
				int i = Integer.parseInt(m2.group(2)) - 1;
				
				if ("".equals(m2.group(1))) {
					return objects[i];
				} else {
					if (objects[i].getClass() == String.class) {
						return Integer.parseInt(objects[i].toString());
					} else {
						return objects[i];
					}
				}
			} else {
				Matcher m3 = argpattern3.matcher(value);

				if (m3.find()) {
					return getVariable(m3.group(1)); 
				} else {
					return value;
				}
			}
		}
	}
	
	@Override
	public Set<String> listAPIs() {
		return this.apis.keySet();
	}

	@Override
	public Mono<ClientResponse> apiCall(String name, Object... objects) {
		try {
			return prepareApiCall(name, objects).exchange();
		} catch (APINotFoundException e) {
			return Mono.error(e);
		}
    }

	@Override
	public WebClient.ResponseSpec retrieveApiCall(String name, Object... objects) throws APINotFoundException {
		return prepareApiCall(name, objects).retrieve();
	}

	@Override
	public WebClient.RequestBodySpec prepareApiCall(String name, Object... objects) throws APINotFoundException {
		if (! this.apis.containsKey(name)) {
			throw new APINotFoundException();
		} else {
			APIEntry entry = this.apis.get(name);

			String url = generateURL("/" + entry.path, objects);
			logger.info(entry.method + " " + this.baseURI + url);

			WebClient webClient = WebClient
					.builder()
					.baseUrl(this.baseURI)
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
					.build();

			WebClient.RequestBodySpec request = webClient
					.method(getHttpMethodByName(entry.method))
					.uri(url)
					.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
					.acceptCharset(StandardCharsets.UTF_8);

			if (entry.requireToken) {
				request.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			}

			if (entry.data != null) {
				request.body(BodyInserters.fromObject(generateRequestBody(entry, objects)));
			}
			return request;
		}
	}

	private HttpMethod getHttpMethodByName(String method) {
		switch (method) {
			case "POST":
				return HttpMethod.POST;
			case "PATCH":
				return HttpMethod.PATCH;
			case "PUT":
				return HttpMethod.PUT;
			case "DELETE":
				return HttpMethod.DELETE;
			default:
				return HttpMethod.GET;
		}
	}

	private Map<String,Object> generateRequestBody(APIEntry entry, Object[] objects) {
		Map<String,Object> argMap = new HashMap<String,Object>();

		for (Entry<String,String> item : entry.data.entrySet()) {
            String key = item.getKey();
            boolean optional = false;

            if (key.contains(":")) {
                String[] parts = item.getKey().split(":");
                key = parts[0];
                optional = "optional".equals(parts[1]);
            }

            try {
                argMap.put(key, generateDATA(item.getValue(), objects));
            } catch (ArrayIndexOutOfBoundsException e) {
                if (! optional) {
                    throw e;
                }
            }
        }

        if (argMap.size() > 0) {
			logger.info(new Gson().toJson(argMap));
		}

		return argMap;
	}
}
