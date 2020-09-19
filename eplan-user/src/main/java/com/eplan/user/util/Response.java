package com.eplan.user.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Data
@NoArgsConstructor
public class Response {

    public Object data;
    public String message;
    public Boolean result;

    public Response(Object data, String message, Boolean result){
        this.data = data;
        this.message = message;
        this.result = result;
    }
    
}
