package ncu.cc.iota.exceptions;

import java.text.MessageFormat;

public class IotaStoreException extends Exception {
    public enum ResultEnum {
        NOT_A_BLOCKCHAIN_FORMAT("Not a blockchain store format"),
        CHECK_SUM_ERROR("Check sum error"),
        ILLEGAL_CHARACTER("Illegal Character"),
        BUNDLE_NOT_FOUND("Bundle {0} not found"),
        TRYTE_OUT_OF_RANGE("Out of range"),
        ALIGNMENT_ERROR("Alignment Error");

        private final String message;

        ResultEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public IotaStoreException(ResultEnum resultEnum) {
        super(resultEnum.message);
    }

    public IotaStoreException(ResultEnum resultEnum, String ...args) {
        super(MessageFormat.format(resultEnum.message, args));
    }
}
