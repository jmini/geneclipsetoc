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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Tests for {@link ContextUtility}
 */
public class ContextUtilityTest {

  @Test
  public void testToXmlEmpty() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendEnd(sb);
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.<Context> emptyList()));
  }

  @Test
  public void testToXmlNull() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendEnd(sb);
    assertEquals(sb.toString(), ContextUtility.toXml(null));
  }

  @Test
  public void testToXmlSingleContext() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithId() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendNewLine(sb);
    sb.append("    <context id=\"some-id\">");
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setId("some-id");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithEmptyId() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setId("");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithComplexId() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendNewLine(sb);
    sb.append("    <context id=\"some&lt;complex&gt;id&quot;like.this\">");
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setId("some<complex>id\"like.this");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithIdAndTitle() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendNewLine(sb);
    sb.append("    <context id=\"some.id\" title=\"This is a title\">");
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setId("some.id");
    context.setTitle("This is a title");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithDescription() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendNewLine(sb);
    sb.append("        <description>This is a description.</description>");
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setDescription("This is a description.");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithDescriptionComplex() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendNewLine(sb);
    sb.append("        <description>This is a &quot;description&quot;.");
    appendNewLine(sb);
    sb.append("Of the &lt;tag&gt; context.</description>");
    appendEndContext(sb);
    appendEnd(sb);

    Context context = new Context();
    context.setDescription("This is a \"description\".\nOf the <tag> context.");
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithSingleTopic() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendNewLine(sb);
    sb.append("        <topic label=\"My Label\" href=\"mypage.html\"/>");
    appendEndContext(sb);
    appendEnd(sb);

    Topic topic = new Topic();
    topic.setHref("mypage.html");
    topic.setLabel("My Label");
    Context context = new Context();
    context.setTopics(Collections.singletonList(topic));
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlSingleContextWithThreeTopics() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendStartContext(sb);
    appendNewLine(sb);
    sb.append("        <topic label=\"First Label\"/>");
    appendNewLine(sb);
    sb.append("        <topic label=\"Second Label\"/>");
    appendNewLine(sb);
    sb.append("        <topic label=\"Third Label\"/>");
    appendEndContext(sb);
    appendEnd(sb);

    Topic t1 = new Topic();
    t1.setLabel("First Label");
    Topic t2 = new Topic();
    t2.setLabel("Second Label");
    Topic t3 = new Topic();
    t3.setLabel("Third Label");
    Context context = new Context();
    context.setTopics(Arrays.asList(t1, t2, t3));
    assertEquals(sb.toString(), ContextUtility.toXml(Collections.singletonList(context)));
  }

  @Test
  public void testToXmlTwoContextsWithId() {
    StringBuilder sb = new StringBuilder();
    appendStart(sb);
    appendNewLine(sb);
    sb.append("    <context id=\"first\">");
    appendEndContext(sb);
    appendNewLine(sb);
    sb.append("    <context id=\"second\">");
    appendEndContext(sb);
    appendEnd(sb);

    Context c1 = new Context();
    c1.setId("first");
    Context c2 = new Context();
    c2.setId("second");
    assertEquals(sb.toString(), ContextUtility.toXml(Arrays.asList(c1, c2)));
  }

  private static void appendStartContext(StringBuilder sb) {
    appendNewLine(sb);
    sb.append("    <context>");
  }

  private static void appendEndContext(StringBuilder sb) {
    appendNewLine(sb);
    sb.append("    </context>");
  }

  private static void appendStart(StringBuilder sb) {
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    appendNewLine(sb);
    sb.append("<?NLS TYPE=\"org.eclipse.help.contexts\"?>");
    appendNewLine(sb);
    sb.append("<contexts>");
  }

  private static void appendEnd(StringBuilder sb) {
    appendNewLine(sb);
    sb.append("</contexts>");
  }

  private static void appendNewLine(StringBuilder sb) {
    sb.append("\n");
  }

}
