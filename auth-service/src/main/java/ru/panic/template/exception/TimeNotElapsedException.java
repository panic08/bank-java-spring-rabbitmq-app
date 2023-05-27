package ru.panic.template.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class TimeNotElapsedException extends RuntimeException{
    public TimeNotElapsedException(String message){
        super(message);
    }
}
