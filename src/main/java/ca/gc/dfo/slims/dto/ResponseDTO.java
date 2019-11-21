package ca.gc.dfo.slims.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 * @param <T>
 */
@Getter
@Setter
public class ResponseDTO<T> {
    
    /**
     * 200 - OK 400 - BAD REQUEST 401 - UNAUTHORIZED 404 - NOT FOUND 500 - SERVER ERROR
     */
    private int      status;
    private String   message;
    private T        data;

    /**
     * Constructor
     * @param status int of the HTTP status
     * @param message String of the message for the response
     * @param data object to be included in this response
     */
    public ResponseDTO(int status, String message, T data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
