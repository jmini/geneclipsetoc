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
package com.bsiag.geneclipsetoc.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.util.MarkupToEclipseToc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class GenerateEclipseTocUtility {

  private static final String CHAR_QUOTE = "\"";
  private static final String CHAR_8220 = Character.toString((char) 8220);
  private static final String CHAR_8221 = Character.toString((char) 8221);
  private static final String CHAR_8222 = Character.toString((char) 8222);
  private static final int ROOT_LEVEL = 0;
  private static final String ROOT_ID = "id";

  public static void generate(File rootInFolder, List<String> pages, String helpPrefix, File outTocFile) throws IOException {
    if (!rootInFolder.exists() || !rootInFolder.isDirectory()) {
      throw new IllegalStateException("Folder rootInFolder '" + rootInFolder.getAbsolutePath() + "' not found.");
    }
    if (pages == null || pages.isEmpty()) {
      throw new IllegalArgumentException("pages can not be null, it should contains at least one element");
    }
    if (outTocFile == null) {
      throw new IllegalStateException("File outTocFile is not set.");
    }

    List<File> inFiles = new ArrayList<File>();
    for (String p : pages) {
      if (p != null && p.length() > 0) {
        File f = new File(rootInFolder, p);
        if (!f.exists() || !f.isFile()) {
          throw new IllegalStateException("File '" + f.getAbsolutePath() + "' not found.");
        }
        inFiles.add(f);
      }
    }

    Map<Integer, OutlineItemEx> nodeMap = new HashMap<Integer, OutlineItemEx>();
    for (int i = 0; i < inFiles.size(); i++) {
      File inFile = inFiles.get(i);

      String html = Files.toString(inFile, Charsets.UTF_8);
      String filePath = calculateFilePath(rootInFolder, inFile, helpPrefix);
      Document doc = Jsoup.parse(html);

      computeOutlineNodes(nodeMap, doc, filePath);
    }

    OutlineItemEx root = nodeMap.get(ROOT_LEVEL);
    if (root == null) {
      throw new IllegalStateException("No header found in the html files");
    }

    MarkupToEclipseToc eclipseToc = new MarkupToEclipseToc() {
      @Override
      protected String computeFile(OutlineItem item) {
        if (item instanceof OutlineItemEx && ((OutlineItemEx) item).getFilePath() != null) {
          return ((OutlineItemEx) item).getFilePath();
        }
        return super.computeFile(item);
      }
    };
    eclipseToc.setBookTitle(root.getLabel());
    eclipseToc.setHtmlFile(root.getFilePath());
    eclipseToc.setHelpPrefix(helpPrefix);
    String tocContent = eclipseToc.createToc(root);
    Files.createParentDirs(outTocFile);
    Files.write(tocContent, outTocFile, Charsets.UTF_8);
  }

  private static String calculateFilePath(File rootFolder, File file, String subPath) {
    return file.getAbsolutePath()
        .substring(rootFolder.getAbsolutePath().length() + 1)
        .replaceAll("\\\\", "/");
  }

  static void computeOutlineNodes(Map<Integer, OutlineItemEx> nodeMap, Document doc, String filePath) {
    Elements elements = doc.getAllElements();
    for (Element element : elements) {
      if (isHeaderTag(element)) {
        int level = Integer.parseInt(element.nodeName().substring(1));
        String title = sanitize(element.text());
        OutlineItem parent = findParent(nodeMap, level);
        if (parent == null) {
          level = ROOT_LEVEL;
        }
        String id = findId(element);
        if (id == null) {
          if (parent == null) {
            id = ROOT_ID;
          }
          else {
            throw new IllegalStateException("id is not found for node " + element.nodeName() + " '" + element.text() + "'");
          }
        }
        OutlineItemEx node = new OutlineItemEx(parent, level, id, 0, 0, title);
        node.setFilePath(filePath);
        putNode(nodeMap, node, level);
      }
    }
  }

  private static boolean isHeaderTag(Element element) {
    return element.nodeName().matches("h[1-6]");
  }

  private static String sanitize(String text) {
    String result = text;
    result = result.replaceAll(CHAR_8220, CHAR_QUOTE);
    result = result.replaceAll(CHAR_8221, CHAR_QUOTE);
    result = result.replaceAll(CHAR_8222, CHAR_QUOTE);
    return result;
  }

  /**
   * Put the node in
   *
   * @param nodeMap
   *          the map containing the last known node for each level
   * @param node
   *          the node that needs to be added
   * @param level
   *          the level (1 for h1, 2 for h2 ...)
   */
  static void putNode(Map<Integer, OutlineItemEx> nodeMap, OutlineItemEx node, int level) {
    nodeMap.put(level, node);
  }

  /**
   * Find the parent node given a specific level.
   *
   * @param nodeMap
   *          the map containing the last known node for each level
   * @param level
   *          the level of the current node
   * @return parentNode
   */
  static OutlineItem findParent(Map<Integer, OutlineItemEx> nodeMap, int level) {
    int i = level - 1;
    while (nodeMap.get(i) == null && i > 0) {
      i = i - 1;
    }
    return nodeMap.get(i);
  }

  /**
   * Find the id of a header tag. id is defined as id attribute of the header,
   * or as id attribute of a nested "a" tag
   *
   * @param element
   *          element corresponding to the HTML header tag (h1, h2, h3, h4,
   *          h5 or h6)
   * @return id
   */
  static String findId(Element element) {
    String id = findIdForElement(element);
    if (id == null) {
      Elements childElements = element.getElementsByTag("a");
      int i = 0;
      while (id == null && i < childElements.size()) {
        Element childElement = childElements.get(i);
        id = findIdForElement(childElement);
        i = i + 1;
      }
    }
    return id;
  }

  /**
   * Find the Id of a given element.
   *
   * @param element
   * @return id
   */
  private static String findIdForElement(Element element) {
    if (element.id() != null && element.id().length() > 0) {
      return element.id();
    }
    return null;
  }

  /**
   * Find the first header (h1, h2, h3, h4, h5 or h6) and returns the text
   * content .
   *
   * @param doc
   *          the html content as JSoup document
   * @return title or null if not found
   */
  private static String findFirstHeader(Document doc) {
    Elements elements = doc.getAllElements();
    for (Element element : elements) {
      if (isHeaderTag(element)) {
        return sanitize(element.text());
      }
    }
    return null;
  }
}
