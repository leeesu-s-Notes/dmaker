package com.fastcam.programming.dmaker.exception;

import lombok.Getter;

@Getter
public class DMakerException extends RuntimeException{
    private DMakerErrorCode dMakerErrorCode;
    private String detailMessage;

    /*
    단순히 코드만 넣고, 기본메세지를
     */
    public DMakerException(DMakerErrorCode errorCode){
        super(errorCode.getMessage());
        this.dMakerErrorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }
    // 기본메세지 대신에 디테일 메세지를 넣어주면 좀 더 다양한 정보를 표헌 가능
    public DMakerException(DMakerErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.dMakerErrorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
