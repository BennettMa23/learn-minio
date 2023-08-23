package com.tuling.miniodemo.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fox
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Res implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message = "";
    private Object data = "";

    public static Res ok(String message,Object data){
        return new Res(200,message,data);
    }

    public static Res error(String message){
        return new Res(500,message,null);
    }
}
