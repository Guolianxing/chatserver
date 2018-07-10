package com.chat.chatserver.dto;

/**
 * Create by Guolianxing on 2018/7/5.
 */
public class ResponseDto {

    private Integer status;
    private String msg;
    private Object data;

    public ResponseDto() {
    }

    public ResponseDto(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
