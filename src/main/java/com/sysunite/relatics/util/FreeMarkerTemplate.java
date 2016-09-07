package com.sysunite.relatics.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohamad Alamili
 */
public class FreeMarkerTemplate {
  public static final Logger log = LoggerFactory.getLogger(FreeMarkerTemplate.class);

  private String queryFilePath;
  private Map<String, Object> variables;
  private static Configuration config = null;

  private FreeMarkerTemplate(String queryFilePath) {
    this.queryFilePath = queryFilePath;
  }

  public static FreeMarkerTemplate get(String template) {
    if(config == null) {
      config = new Configuration();
      ClassTemplateLoader loader = new ClassTemplateLoader(FreeMarkerTemplate.class, "/");
      config.setTemplateLoader(loader);
      config.setObjectWrapper(new DefaultObjectWrapper());
      config.setDefaultEncoding("UTF-8");
      config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
      config.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    return new FreeMarkerTemplate(template);
  }

  private Map<String, Object> getVariables() {
    if(this.variables == null) {
      this.variables = new HashMap();
    }

    return this.variables;
  }

  public FreeMarkerTemplate put(String variable, Object value) {
    this.getVariables().put(variable, value);
    return this;
  }

  public String render() {
    try {
      Template e = config.getTemplate(this.queryFilePath);
      StringWriter writer = new StringWriter();
      e.process(this.variables, writer);
      writer.flush();
      writer.close();
      return writer.toString();
    } catch (Exception var3) {
      this.log.error("Could not load template.", var3);
      return null;
    }
  }

  public InputStream renderToInputStream() {
    final Template temp;
    try {
      temp = config.getTemplate(this.queryFilePath);
    } catch (IOException var6) {
      this.log.error("Could not load template.", var6);
      return null;
    }

    PipedInputStream pi = new PipedInputStream();

    try {
      final OutputStreamWriter writer = new OutputStreamWriter(new PipedOutputStream(pi));
      (new Thread(new Runnable() {
        public void run() {
          try {
            temp.process(FreeMarkerTemplate.this.variables, writer);
            writer.flush();
            writer.close();
          } catch (TemplateException var2) {
            FreeMarkerTemplate.this.log.error(var2.getMessage(), var2);
          } catch (IOException var3) {
            FreeMarkerTemplate.this.log.error("Writer error", var3);
          }

        }
      })).start();
    } catch (IOException var5) {
      this.log.error("Writer error", var5);
    }

    return pi;
  }
}