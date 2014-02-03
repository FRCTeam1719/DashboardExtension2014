/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.firstteam1719.dashboard.verticalebar;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author Chance
 */
public class HttpWraper {

    CredentialsProvider cred;
    static CloseableHttpClient httpClient;

    public HttpWraper() {
        cred = new BasicCredentialsProvider();
        cred.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("FRC", "FRC"));
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(cred).build();
    }

    public static void send(String url) {
        
        try {
            
            HttpGet httpGet = new HttpGet(url);
            httpClient.execute(httpGet);
            
        } catch (ClientProtocolException e) {
           //cammera sends back invalid headers
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            
        }
    }
}
