package ncu.cc.commons.api.models;

import java.util.Map;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class APIMetaData {
	public String description;
	public String version;
	public String path;
	public Map<String,APIMetaItem> apis;
}
