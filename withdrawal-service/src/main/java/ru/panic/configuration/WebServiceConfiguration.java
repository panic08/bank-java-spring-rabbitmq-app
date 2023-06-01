package ru.panic.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import java.util.List;

@Configuration
@EnableWs
public class WebServiceConfiguration extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/api/v1/*");
    }

    @Bean(name = "WithdrawalEndpoint")
    public DefaultWsdl11Definition withdrawalEndpoint(XsdSchema withdrawalSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("8084");
        wsdl11Definition.setLocationUri("/api/v1");
        wsdl11Definition.setTargetNamespace("http://localhost/WithdrawalEndpoint");
        wsdl11Definition.setSchema(withdrawalSchema);
        return wsdl11Definition;
    }
    @Bean(name = "PreWithdrawalEndpoint")
    public DefaultWsdl11Definition preWithdrawalEndpoint(XsdSchema preWithdrawalSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("8084");
        wsdl11Definition.setLocationUri("/api/v1");
        wsdl11Definition.setTargetNamespace("http://localhost/PreWithdrawalEndpoint");
        wsdl11Definition.setSchema(preWithdrawalSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema withdrawalSchema() {
        return new SimpleXsdSchema(new ClassPathResource("scheme/WithdrawalSchema.xsd"));
    }
    @Bean
    public XsdSchema preWithdrawalSchema() {
        return new SimpleXsdSchema(new ClassPathResource("scheme/PreWithdrawalSchema.xsd"));
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new PayloadLoggingInterceptor());
        interceptors.add(new SoapEnvelopeLoggingInterceptor());

    }
}
