package com.adobe.aem.core.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.core.SolrSearchService;
import com.adobe.aem.core.SolrServerConfiguration;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component
@Service
public class SolrSearchServiceImpl implements SolrSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

	@Reference
	private QueryBuilder queryBuilder;

	@Reference
	private SlingRepository repository;

	@Reference
	SolrServerConfiguration solrConfigurationService;

	@SuppressWarnings("deprecation")
	@Override
	public JSONArray crawlContent(String resourcePath, String resourceType) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("path", resourcePath);
		params.put("type", resourceType);
		params.put("p.offset", "0");
		params.put("p.limit", "10000");

		Session session = null;

		try {
			session = repository.loginAdministrative(null);
			Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
			SearchResult searchResults = query.getResult();
			LOG.info("Found '{}' matches for query", searchResults.getTotalMatches());
			if (resourceType.equalsIgnoreCase("cq:PageContent")) {
				return createPageMetadataArray(searchResults);
			}

		} catch (RepositoryException e) {
			LOG.error("Exception due to", e);
		} finally {
			if (session.isLive() || session != null) {
				session.logout();
			}
		}
		return null;

	}

	@Override
	public JSONArray createPageMetadataArray(SearchResult results) throws RepositoryException {
		JSONArray solrDocs = new JSONArray();
		for (Hit hit : results.getHits()) {
			Resource pageContent = hit.getResource();
			ValueMap properties = pageContent.adaptTo(ValueMap.class);
			String isPageIndexable = properties.get("notsolrindexable", String.class);
			if (null != isPageIndexable && isPageIndexable.equals("true"))
				continue;
			JSONObject propertiesMap = createPageMetadataObject(pageContent);
			solrDocs.put(propertiesMap);
		}

		return solrDocs;

	}

	@Override
	public JSONObject createPageMetadataObject(Resource pageContent) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("id", pageContent.getParent().getPath());
		propertiesMap.put("url", pageContent.getParent().getPath() + ".html");
		ValueMap properties = pageContent.adaptTo(ValueMap.class);
		String pageTitle = properties.get("jcr:title", String.class);
		if (StringUtils.isEmpty(pageTitle)) {
			pageTitle = pageContent.getParent().getName();
		}
		propertiesMap.put("title", pageTitle);
		propertiesMap.put("description", properties.get("jcr:description", String.class));
		propertiesMap.put("publishDate", properties.get("publishdate", String.class));
		propertiesMap.put("body", "");
		propertiesMap.put("lastModified", properties.get("cq:lastModified", Calendar.class));
		propertiesMap.put("contentType", "page");
		propertiesMap.put("tags", pageContent);
		return new JSONObject(propertiesMap);
	}

	@Override
	public boolean indexPagesToSolr(JSONArray indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		if (null != indexPageData) {

			for (int i = 0; i < indexPageData.length(); i++) {
				JSONObject pageJsonObject = indexPageData.getJSONObject(i);
				SolrInputDocument doc = createPageSolrDoc(pageJsonObject);
				server.add(doc);
			}
			server.commit();
			return true;
		}

		return false;
	}

	@Override
	public boolean indexPageToSolr(JSONObject indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		if (null != indexPageData) {
			SolrInputDocument doc = createPageSolrDoc(indexPageData);
			server.add(doc);
			server.commit();
			return true;
		}

		return false;
	}

	private SolrInputDocument createPageSolrDoc(JSONObject pageJsonObject) throws JSONException {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", pageJsonObject.get("id"));
		doc.addField("title", pageJsonObject.get("title"));
		doc.addField("body", pageJsonObject.get("body"));
		doc.addField("url", pageJsonObject.get("url"));
		doc.addField("description", pageJsonObject.get("description"));
		doc.addField("lastModified", pageJsonObject.get("lastModified"));
		doc.addField("contentType", pageJsonObject.get("contentType"));
		doc.addField("tags", pageJsonObject.get("tags"));
		return doc;

	}

}
