package ncu.cc.commons.api.models;

import java.util.Map;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class APIEntry {
	public String name;
	public String path;
	public String method;
	public boolean requireAuth;
	public boolean requireToken;
	public Map<String,String> data;
	
	public APIEntry(String name, APIMetaItem item) {
		super();
		this.name = name;
		this.path = item.path;
		this.method = item.method;
		this.requireAuth = item.requireAuth;
		this.requireToken = item.requireToken;
		this.data = item.data;
	}
}
