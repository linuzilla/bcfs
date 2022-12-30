package ncu.cc.commons.api.exceptions;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class APICallException extends GeneralAPICallException {
	private static final long serialVersionUID = 2543850845772371278L;
	private int errcode;
	private String message;
	
	public APICallException(int errcode, String message) {
		super();
		this.errcode = errcode;
		this.message = message;
	}

	public int getErrcode() {
		return errcode;
	}

	public String getMessage() {
		return message;
	}
}
