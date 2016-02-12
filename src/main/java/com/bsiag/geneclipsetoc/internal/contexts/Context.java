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

/**
 * Representation of Help Context
 */
public class Context {

  private String id;
  private String title;
  private String description;
  private List<Topic> topics;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Topic> getTopics() {
    return topics;
  }

  public void setTopics(List<Topic> topics) {
    this.topics = topics;
  }
}
