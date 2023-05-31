package ru.panic.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
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

    @Bean(name = "P2PTransactionEndpoint")
    public DefaultWsdl11Definition p2pTransaction(XsdSchema p2pTransactionSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("8081");
        wsdl11Definition.setLocationUri("/api/v1");
        wsdl11Definition.setTargetNamespace("http://localhost/P2PTransactionEndpoint");
        wsdl11Definition.setSchema(p2pTransactionSchema);
        return wsdl11Definition;
    }
    @Bean(name = "P2PPreTransactionEndpoint")
    public DefaultWsdl11Definition p2pPreTransaction(XsdSchema p2pPreTransactionSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("8081");
        wsdl11Definition.setLocationUri("/api/v1");
        wsdl11Definition.setTargetNamespace("http://localhost/P2PPreTransactionEndpoint");
        wsdl11Definition.setSchema(p2pPreTransactionSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema p2pTransactionSchema() {
        return new SimpleXsdSchema(new ClassPathResource("scheme/P2PTransactionSchema.xsd"));
    }
    @Bean
    public XsdSchema p2pPreTransactionSchema() {
        return new SimpleXsdSchema(new ClassPathResource("scheme/P2PPreTransactionSchema.xsd"));
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new PayloadLoggingInterceptor());
        interceptors.add(new SoapEnvelopeLoggingInterceptor());

    }
}
