/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package com.bsiag.geneclipsetoc.maven;

import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

public class HelpContext {
  static final String ID = "id";
  static final String TITLE = "title";
  static final String DESCRIPTION = "description";
  static final String TOPIC_PAGES = "topicPages";

  @Parameter(property = ID)
  private String id;

  @Parameter(property = TITLE)
  private String title;

  @Parameter(property = DESCRIPTION)
  private String description;

  @Parameter(property = TOPIC_PAGES, required = true)
  private List<String> topicPages;

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getTopicPages() {
    return topicPages;
  }
}
