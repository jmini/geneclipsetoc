/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package com.bsiag.geneclipsetoc.internal.contexts;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.xml.XmlEscapers;

/**
 * @author jbr
 */
public class ContextUtility {
  /**
   *
   */
  private static final String INDENTATION = "    ";
  private static final String NEW_LINE = "\n";

  private ContextUtility() {
  }

  public static String toXml(List<Context> contexts) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append(NEW_LINE);
    sb.append("<?NLS TYPE=\"org.eclipse.help.contexts\"?>");
    sb.append(NEW_LINE);
    sb.append("<contexts>");
    if (contexts != null) {
      for (Context context : contexts) {
        sb.append(NEW_LINE);
        sb.append(INDENTATION);
        sb.append("<context");
        appendAttr(sb, "id", context.getId());
        appendAttr(sb, "title", context.getTitle());
        sb.append(">");
        if (context.getDescription() != null && context.getDescription().length() > 0) {
          sb.append(NEW_LINE);
          sb.append(INDENTATION);
          sb.append(INDENTATION);
          sb.append("<description>");
          Splitter splitter = Splitter.onPattern("\r?\n");
          boolean needNewLine = false;
          for (String line : splitter.split(context.getDescription())) {
            if (needNewLine) {
              sb.append(NEW_LINE);
            }
            sb.append(XmlEscapers.xmlAttributeEscaper().escape(line));
            needNewLine = true;
          }
          sb.append("</description>");
        }
        if (context.getTopics() != null) {
          for (Topic topic : context.getTopics()) {
            sb.append(NEW_LINE);
            sb.append(INDENTATION);
            sb.append(INDENTATION);
            sb.append("<topic");
            appendAttr(sb, "label", topic.getLabel());
            appendAttr(sb, "href", topic.getHref());
            sb.append("/>");
          }
        }
        sb.append(NEW_LINE);
        sb.append(INDENTATION);
        sb.append("</context>");
      }
    }
    sb.append(NEW_LINE);
    sb.append("</contexts>");
    return sb.toString();
  }

  private static void appendAttr(StringBuilder sb, String attribute, String value) {
    if (value != null && value.length() > 0) {
      sb.append(" ");
      sb.append(attribute);
      sb.append("=\"");
      sb.append(XmlEscapers.xmlAttributeEscaper().escape(value));
      sb.append("\"");
    }
  }
}
