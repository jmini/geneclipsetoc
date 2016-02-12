/*******************************************************************************
 * Copyright (c) 2015 Jeremie Bresson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 ******************************************************************************/
package com.bsiag.geneclipsetoc.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import com.bsiag.geneclipsetoc.internal.contexts.Context;
import com.bsiag.geneclipsetoc.maven.HelpContext;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class GenerateEclipseTocUtilityTest {

  OutlineItemEx ROOT = new OutlineItemEx(null, 0, "#x0-1000", 0, 0, "ROOT");
  OutlineItemEx NODE_1A = new OutlineItemEx(ROOT, 1, "#x1-1000", 0, 0, "H1 - A");
  OutlineItemEx NODE_1B = new OutlineItemEx(ROOT, 1, "#x2-1000", 0, 0, "H1 - B");
  OutlineItemEx NODE_2 = new OutlineItemEx(NODE_1A, 2, "#x3-1000", 0, 0, "H2");
  OutlineItemEx NODE_3 = new OutlineItemEx(NODE_2, 3, "#x4-1000", 0, 0, "H3");

  /**
   * Test method for {@link buildhelp.GenerateEclipseTocUtility.putNode(java.util.Map, buildhelp.OutlineItemEx)}.
   */
  @Test
  public void testPutNode() {
    Map<Integer, OutlineItemEx> map = new HashMap<Integer, OutlineItemEx>();
    assertNull(map.get(0));
    assertNull(map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    assertNull(map.get(4));

    GenerateEclipseTocUtility.putNode(map, NODE_1A, 1);
    assertNull(map.get(0));
    assertEquals(NODE_1A, map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    assertNull(map.get(4));

    GenerateEclipseTocUtility.putNode(map, NODE_1B, 1);
    assertNull(map.get(0));
    assertEquals(NODE_1B, map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    assertNull(map.get(4));

    GenerateEclipseTocUtility.putNode(map, NODE_3, 3);
    assertNull(map.get(0));
    assertEquals(NODE_1B, map.get(1));
    assertNull(map.get(2));
    assertEquals(NODE_3, map.get(3));
    assertNull(map.get(4));
  }

  /**
   * Test method for {@link buildhelp.GenerateEclipseTocUtility.findParent(java.util.Map, int)}.
   */
  @Test
  public void testFindParent() {
    Map<Integer, OutlineItemEx> map = new HashMap<Integer, OutlineItemEx>();
    assertNull(GenerateEclipseTocUtility.findParent(map, 3));
    assertNull(GenerateEclipseTocUtility.findParent(map, 2));
    GenerateEclipseTocUtility.putNode(map, ROOT, 0);
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 1));
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 2));
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 3));
    GenerateEclipseTocUtility.putNode(map, NODE_3, 3);
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 1));
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 2));
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 3));
    GenerateEclipseTocUtility.putNode(map, NODE_1A, 1);
    assertEquals(ROOT, GenerateEclipseTocUtility.findParent(map, 1));
    assertEquals(NODE_1A, GenerateEclipseTocUtility.findParent(map, 2));
    assertEquals(NODE_1A, GenerateEclipseTocUtility.findParent(map, 3));
  }

  /**
   * Test method for {@link buildhelp.GenerateEclipseTocUtility.computeOutlineNodes(java.util.Map, String, String)}.
   */
  @Test
  public void testComputeOutlineNodes() throws Exception {
    OutlineItemEx root = new OutlineItemEx(null, 0, "z9999", 0, 0, "root node");

    Map<Integer, OutlineItemEx> map = new HashMap<Integer, OutlineItemEx>();
    GenerateEclipseTocUtility.putNode(map, root, 0);

    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<header>");
    sb.append("<title>Lorem</title>");
    sb.append("</header>");
    sb.append("<body>");
    sb.append("<h1 id=\"n100\">Lorem</h1>");
    sb.append("<h3 id=\"n101\">Utos lorem</h3>");
    sb.append("<h3 id=\"n102\">Tardis lorem</h3>");
    sb.append("<h3 id=\"n103\">Satis lorem</h3>");
    sb.append("<h1 id=\"n200\">Ipsum</h1>");
    sb.append("<h2><a id=\"n201\"/>Dolore ipsum</h2>");
    sb.append("</body>");
    sb.append("</html>");
    Document doc = Jsoup.parse(sb.toString());
    GenerateEclipseTocUtility.computeOutlineNodes(map, doc, "page1.html");

    assertEquals("root children size", 2, root.getChildren().size());

    OutlineItem n100 = root.getChildren().get(0);
    assertEquals("n100 label", "Lorem", n100.getLabel());
    assertEquals("n100 id", "n100", n100.getId());
    assertEquals("n100 children size", 3, n100.getChildren().size());

    OutlineItem n101 = n100.getChildren().get(0);
    assertEquals("n101 label", "Utos lorem", n101.getLabel());
    assertEquals("n101 id", "n101", n101.getId());
    assertEquals("n101 children size", 0, n101.getChildren().size());

    OutlineItem n102 = n100.getChildren().get(1);
    assertEquals("n102 label", "Tardis lorem", n102.getLabel());
    assertEquals("n102 id", "n102", n102.getId());
    assertEquals("n102 children size", 0, n102.getChildren().size());

    OutlineItem n103 = n100.getChildren().get(2);
    assertEquals("n103 label", "Satis lorem", n103.getLabel());
    assertEquals("n103 id", "n103", n103.getId());
    assertEquals("n103 children size", 0, n103.getChildren().size());

    OutlineItem n200 = root.getChildren().get(1);
    assertEquals("ispum children size", 1, n200.getChildren().size());

    OutlineItem n201 = n200.getChildren().get(0);
    assertEquals("n201 label", "Dolore ipsum", n201.getLabel());
    assertEquals("n201 id", "n201", n201.getId());
    assertEquals("n201 children size", 0, n201.getChildren().size());
  }

  /**
   * Test method for {@link buildhelp.GenerateEclipseTocUtility.findId(org.jsoup.nodes.Element)}.
   */
  @Test
  public void testFindId() {
    Element e, c1, c2;
    e = new Element(Tag.valueOf("h1"), "");
    assertNull(GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h3"), "");
    e.attr("class", "test-class");
    assertNull(GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h2"), "");
    e.attr("id", "test-id");
    assertEquals("test-id", GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h3"), "");
    c1 = new Element(Tag.valueOf("a"), "");
    c1.attr("id", "my-id");
    e.appendChild(c1);
    assertEquals("my-id", GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h3"), "");
    c1 = new Element(Tag.valueOf("a"), "");
    e.appendChild(c1);
    c2 = new Element(Tag.valueOf("a"), "");
    c2.attr("id", "a-id");
    e.appendChild(c2);
    assertEquals("a-id", GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h3"), "");
    c1 = new Element(Tag.valueOf("a"), "");
    c1.attr("id", "my-id");
    e.appendChild(c1);
    c2 = new Element(Tag.valueOf("a"), "");
    c2.attr("id", "a-id");
    e.appendChild(c2);
    assertEquals("my-id", GenerateEclipseTocUtility.findId(e));

    e = new Element(Tag.valueOf("h3"), "");
    e.attr("id", "test-id");
    c1 = new Element(Tag.valueOf("a"), "");
    e.appendChild(c1);
    c2 = new Element(Tag.valueOf("a"), "");
    c2.attr("id", "a-id");
    e.appendChild(c2);
    assertEquals("test-id", GenerateEclipseTocUtility.findId(e));
  }

  @Test
  public void testComputeContexts() throws Exception {
    File rootInFolder = Files.createTempDir();
    File file1 = new File(rootInFolder, "page1.html");
    Files.write("tmp1", file1, Charsets.UTF_8);
    File file2 = new File(rootInFolder, "page2.html");
    Files.write("tmp2", file2, Charsets.UTF_8);
    String helpPrefix = "xxx";

    HelpContext helpContext1 = new HelpContext() {
      @Override
      public String getId() {
        return "first_page_context";
      }

      @Override
      public String getTitle() {
        return "Page Context 1";
      }

      @Override
      public List<String> getTopicPages() {
        return Collections.singletonList("page1.html");
      }
    };
    HelpContext helpContext2 = new HelpContext() {
      @Override
      public String getId() {
        return "second_page_context";
      }

      @Override
      public List<String> getTopicPages() {
        return Arrays.asList("page1.html", "page2.html");
      }
    };
    List<HelpContext> inContexts = Arrays.asList(helpContext1, helpContext2);
    Map<File, String> topicFileMap = new HashMap<>();
    topicFileMap.put(file1, "My first chapter");
    topicFileMap.put(file2, "My second chapter");

    List<Context> outContexts = GenerateEclipseTocUtility.computeContexts(rootInFolder, helpPrefix, inContexts, topicFileMap);
    assertEquals("outContexts size", 2, outContexts.size());
    assertEquals("first outContext id", "first_page_context", outContexts.get(0).getId());
    assertEquals("first outContext title", "Page Context 1", outContexts.get(0).getTitle());
    assertEquals("first outContext topics size", 1, outContexts.get(0).getTopics().size());
    assertEquals("first outContext first topic href", "xxx/page1.html", outContexts.get(0).getTopics().get(0).getHref());
    assertEquals("first outContext first topic label", "My first chapter", outContexts.get(0).getTopics().get(0).getLabel());
    assertEquals("second outContext id", "second_page_context", outContexts.get(1).getId());
    assertEquals("second outContext title", "My first chapter", outContexts.get(1).getTitle());
    assertEquals("second outContext topics size", 2, outContexts.get(1).getTopics().size());
    assertEquals("second outContext first topic href", "xxx/page1.html", outContexts.get(1).getTopics().get(0).getHref());
    assertEquals("second outContext first topic label", "My first chapter", outContexts.get(1).getTopics().get(0).getLabel());
    assertEquals("second outContext second topic href", "xxx/page2.html", outContexts.get(1).getTopics().get(1).getHref());
    assertEquals("second outContext second topic label", "My second chapter", outContexts.get(1).getTopics().get(1).getLabel());
  }
}
