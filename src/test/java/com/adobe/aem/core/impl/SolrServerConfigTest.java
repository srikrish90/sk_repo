package com.adobe.aem.core.impl;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;

import org.junit.Test;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;


public class SolrServerConfigTest {
	private static final Logger LOG = LoggerFactory.getLogger(SolrServerConfigTest.class);
	
	private SolrServerConfigImpl solrServerConfig;
	
	private ValueMap properties;
	
	private ComponentContext ctx;
	
	public SolrServerConfigTest() throws MalformedURLException{
		LOG.info("inside test initialization method");
		
		// Creating mock for the test class
		solrServerConfig = mock(SolrServerConfigImpl.class);
		
		// Create a test objects for mocking
		properties = new ValueMapDecorator(new HashMap());

        // Calling Real methods
        when(solrServerConfig.getSolrProtocol()).thenCallRealMethod();
        when(solrServerConfig.getContentPagePath()).thenCallRealMethod();
        when(solrServerConfig.getSolrCoreName()).thenCallRealMethod();
        when(solrServerConfig.getSolrServerName()).thenCallRealMethod();
        when(solrServerConfig.getSolrServerPort()).thenCallRealMethod();
        
        doCallRealMethod().when(solrServerConfig).activate(ctx);
		
	}
	
	// Tests
    @Test
    public void testGetSolrProtocol() throws MalformedURLException {
        String testProtocol = "http";
        properties.put("http", testProtocol);
        solrServerConfig.activate(ctx);
        String protocol = solrServerConfig.getSolrProtocol();
        assertEquals(solrServerConfig, protocol);
    }

    @Test
    public void testGetContentPagePath() throws MalformedURLException {
        String testContentPagePath = "/content/solr";
        properties.put("/content/solr", testContentPagePath);
        solrServerConfig.activate(ctx);
        String contentPagePath = solrServerConfig.getContentPagePath();
        assertEquals(solrServerConfig, contentPagePath);
    }
    
    @Test
    public void testGetSolrCoreName() throws MalformedURLException {
        String testCoreName = "collection123";
        properties.put("collection123", testCoreName);
        solrServerConfig.activate(ctx);
        String coreName = solrServerConfig.getContentPagePath();
        assertEquals(solrServerConfig, coreName);
    }
    
    @Test
    public void testGetSolrServerName() throws MalformedURLException {
        String testServerName = "localhost";
        properties.put("localhost", testServerName);
        solrServerConfig.activate(ctx);
        String serverName = solrServerConfig.getContentPagePath();
        assertEquals(solrServerConfig, serverName);
    }
    
    @Test
    public void testGetSolrServerPort() throws MalformedURLException {
        String testServerPort = "8983";
        properties.put("8983", testServerPort);
        solrServerConfig.activate(ctx);
        String serverPort = solrServerConfig.getContentPagePath();
        assertEquals(solrServerConfig, serverPort);
    }
	
	
	
}
