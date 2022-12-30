package ncu.cc.commons.api.models;

import java.util.Map;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class APIMetaItem {
	public String path;
	public String method;
	public Boolean requireAuth;
	public Boolean requireToken;
	public Map<String,APIMetaItem> apis = null;
	public Map<String,String> data = null;
	
	public APIMetaItem() {
		super();
	}
	
	public APIMetaItem(APIMetaData meta) {
		this.path = meta.path == null ? "" : meta.path;
		this.requireAuth = new Boolean(false);
		this.requireToken = new Boolean(false);
	}

	public void inherit(APIMetaItem parent) {
		if (this.method == null) {
			this.method = "GET";
		} else {
			this.method = this.method.toUpperCase();
		}
		
		if (this.requireAuth == null) {
			this.requireAuth = parent.requireAuth;
		}
		
		if (this.requireToken == null) {
			this.requireToken = parent.requireToken;
		}
		
		if (this.path == null) {
			this.path = parent.path;
		} else {
			if (! "".equals(parent.path)) {
				this.path = parent.path + "/" + this.path; 
			}
		}
	}
}

