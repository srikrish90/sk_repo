package com.adobe.aem.core.impl;

import java.net.MalformedURLException;
import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.core.SolrServerConfiguration;

@Component(
        label = "Solr Configuration Service - AEM",
        description = "A service for configuring AEM to Solr",
        immediate = true,
        metatype = true)
@Service
public class SolrServerConfigImpl implements SolrServerConfiguration  {	
	private static final Logger LOG = LoggerFactory.getLogger(SolrServerConfigImpl.class);
	
	private String solrProtocol;
	@Property(label = "Protocol", value = "http", description = "Either 'http' or 'https'")
	public static final String SOLR_PROTOCOL = "solr.protocol";
	
	private String solrServerName;
	@Property(label = "Solr Server Name", value = "localhost", description = "Server name or IP address ")
	public static final String SOLR_SERVER_URL = "solr.server.name";
	
	private String solrServerPort;
	@Property(label = "Solr Server Port", value = "8983", description = "Server port ")
	public static final String SOLR_SERVER_PORT = "solr.server.port";
	
	private String solrCoreName;
	@Property(label = "Solr Core Name", value = "collection1", description = "Core name in solr server")
	public static final String SOLR_CORE_NAME = "solr.core.name";
	
	private String contentPagePath;
	@Property(label = "Content page path", value = "/content/solr", description = "Content page path for indexing")
	public static final String SOLR_PAGE_PATH = "solr.core.pagepath";
	
	
	@Activate
	protected void activate(ComponentContext ctx) throws MalformedURLException {
		LOG.info("inside activate method");
		final Dictionary<?, ?> config = ctx.getProperties();		
		solrProtocol = PropertiesUtil.toString(config.get(SOLR_PROTOCOL),"http");
		solrServerName = PropertiesUtil.toString(config.get(SOLR_SERVER_URL),"localhost");
		solrServerPort = PropertiesUtil.toString(config.get(SOLR_SERVER_PORT),"8983");
		solrCoreName = PropertiesUtil.toString(config.get(SOLR_CORE_NAME),"collection");
		contentPagePath = PropertiesUtil.toString(config.get(SOLR_PAGE_PATH),"/content/solr");
	}

	public String getSolrProtocol() {
		return solrProtocol;
	}

	public String getSolrServerName() {
		return solrServerName;
	}

	public String getSolrServerPort() {
		return solrServerPort;
	}

	public String getSolrCoreName() {
		return solrCoreName;
	}
	
	public String getContentPagePath() {
		return contentPagePath;
	}

}
