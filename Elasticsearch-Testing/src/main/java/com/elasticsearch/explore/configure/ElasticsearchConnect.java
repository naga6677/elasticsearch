package com.elasticsearch.explore.configure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public class ElasticsearchConnect {

	private static final String ELASTIC_HOST = "172.23.214.59";
	private static final String ELASTIC_PROTOCOL = "https";

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {

		RestClient restClient = null;
		HttpHost[] hosts = new HttpHost[1];
		hosts[0] = new HttpHost(ELASTIC_HOST, 9200, ELASTIC_PROTOCOL);

		restClient = RestClient.builder(hosts).setHttpClientConfigCallback(httpClientBuilder -> {
			SSLContext sslContext = null;
			HttpAsyncClientBuilder httpAsynchClient = null;

			try {

				File readFile = new File("C:\\Users\\venka\\workspace\\Elasticsearch\\SSL\\http-local.p12");
				URL certificateURL = readFile.toURI().toURL();

				sslContext = SSLContextBuilder.create().create()
						.loadTrustMaterial(certificateURL, "dhanvikaEDE1!".toCharArray()).build();

				final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials("elastic", "dhanvikaEDE1!"));

				httpAsynchClient = httpClientBuilder.setSSLContext(sslContext)
						.setDefaultCredentialsProvider(credentialsProvider)
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return httpAsynchClient;
		}).build();

		System.out.println("RestClient Created");

		// Create the transport with a Jackson mapper
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		System.out.println("Connected to Transport");

		// And create the API client
		ElasticsearchClient esClient = new ElasticsearchClient(transport);

		ElasticsearchIndicesClient indices = esClient.indices();

		GetAliasResponse alias = indices.getAlias();

		System.out.println("Get Alias completed!!");

	}

}
