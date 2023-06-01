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

    @Bean(name = "SignInEndpoint")
    public DefaultWsdl11Definition signInEndpoint(XsdSchema signInSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("8080");
        wsdl11Definition.setLocationUri("/api/v1");
        wsdl11Definition.setTargetNamespace("http://localhost/SignInEndpoint");
        wsdl11Definition.setSchema(signInSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema signInSchema() {
        return new SimpleXsdSchema(new ClassPathResource("scheme/SignInSchema.xsd"));
    }



    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new PayloadLoggingInterceptor());
        interceptors.add(new SoapEnvelopeLoggingInterceptor());

    }
}
