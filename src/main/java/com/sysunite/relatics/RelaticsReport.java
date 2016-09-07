package com.sysunite.relatics;

import com.sysunite.relatics.util.FreeMarkerTemplate;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohamad Alamili
 */
public class RelaticsReport {

  private String url, workspace, operationName, entryCode;
  private Map<String,String> parameters = new HashMap<>();

  public RelaticsReport(String url, String workspace, String operationName, String entryCode) {
    this.url = url;
    this.workspace = workspace;
    this.operationName = operationName;
    this.entryCode = entryCode;
  }

  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }

  /**
   * created a soap envelope to retrieve a report from relatics.
   * Uses a freemarker template to accomplish this.
   *
   * @return filled in soapEnvelope
   */
  private String getSoapEnvelope() {
    String soapEnvelope = FreeMarkerTemplate.get("soap-envelope.ftl")
      .put("reportName", operationName)
      .put("workspace", workspace)
      .put("entryCode", entryCode)
      .put("parameters", parameters)
      .render();

    return soapEnvelope;
  }

  public String fetch() {
    try {
      String soapEnvelope = getSoapEnvelope();
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

      connection.setDoOutput(true);

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8; action=\"http://www.relatics.com/GetResult\"");
      connection.setRequestProperty("Content-Length", Integer.toString(soapEnvelope.length()));

      OutputStream reqStream = connection.getOutputStream();
      reqStream.write(soapEnvelope.getBytes());
      DataInputStream dis = new DataInputStream(connection.getInputStream());

      return IOUtils.toString(dis, "UTF-8");
    } catch (IOException e) {
      System.out.println("Error during soap request to relatics: " + e.getMessage());
      return null;
    }
  }
}