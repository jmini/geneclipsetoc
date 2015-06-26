/*******************************************************************************
 * Copyright (c) 2015 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package geneclipsetoc.internal;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

/**
 * Extension of the Mylyn Docs {@link OutlineItem} in order to be able to set the filePath.
 */
public class OutlineItemEx extends OutlineItem {

  private String filePath;

  /**
   * @param parent
   * @param level
   * @param id
   * @param offset
   * @param length
   * @param label
   */
  public OutlineItemEx(OutlineItem parent, int level, String id, int offset, int length, String label) {
    super(parent, level, id, offset, length, label);
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getFilePath() {
    return filePath;
  }

}
