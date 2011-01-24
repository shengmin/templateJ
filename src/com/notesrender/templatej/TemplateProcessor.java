/**
 * Copyright (c) 2011 Shengmin Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.notesrender.templatej;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Shengmin
 *
 */
public final class TemplateProcessor extends DefaultHandler {
	private String _nsPrefix;
	private final String _TAG_TEMPLATE = "template";
	private final String _TAG_PLACEHOLDER = "placeholder";
	private final String _TAG_CONTENT = "content";
	private final String _ATTR_ID = "id";
	private final String _ATTR_REF = "ref";
	private final String _ATTR_OUT = "out";
	private PrintWriter _writer;

	private String _ref;
	private String _out;

	private boolean _isContentTag = false;
	private StringBuilder _contentBuilder = new StringBuilder(512);
	private String _contentRef;
	private HashMap<String, String> _contents = new HashMap<String, String>();

	TemplateProcessor(String prefix) {
		_nsPrefix = prefix + ":";
	}

	@Override
	public void characters(char[] ch, int start, int len) {
		int end = start + len;

		if (_isContentTag) {

			for (int i = start; i < end; i++) {
				System.out.println("append: " + ch[i]);
				_contentBuilder.append(ch[i]);
			}
		} else if (_writer != null) {
			for (int i = start; i < end; i++)
				_writer.print(ch[i]);
			_writer.flush();
		}
	}

	@Override
	public void startElement(String uri, String localName, String fullName, Attributes attrs) {
		if (fullName.startsWith(_nsPrefix)) {
			if (localName.equals(_TAG_CONTENT)) processStartContentTag(attrs);
			else if (localName.equals(_TAG_PLACEHOLDER)) processStartPlaceholderTag(attrs);
			else if (localName.equals(_TAG_TEMPLATE)) processStartTemplateTag(attrs);
		}
	}

	private void processStartPlaceholderTag(Attributes attrs) {
		String content = _contents.get(attrs.getValue(_ATTR_ID));
		System.out.println("Content: " + content);
		_writer.print(content);
		_writer.flush();
	}

	@Override
	public void endElement(String uri, String localName, String fullName) {
		if (fullName.startsWith(_nsPrefix)) {
			if (localName.equals(_TAG_CONTENT)) processEndContentTag();
		}
	}

	private void processStartTemplateTag(Attributes attrs) {
		_ref = attrs.getValue(_ATTR_REF);
		_out = attrs.getValue(_ATTR_OUT);
	}

	private void processEndContentTag() {
		_isContentTag = false;
		String content = _contentBuilder.toString();
		System.out.println("End Content Tag" + content);
		_contents.put(_contentRef, content);
	}

	private void processStartContentTag(Attributes attrs) {
		_isContentTag = true;
		_contentRef = attrs.getValue(_ATTR_REF);
		_contentBuilder.setLength(0);
	}

	public void process(String filePath) {
		try {
			XMLReader rd = XMLReaderFactory.createXMLReader();
			rd.setContentHandler(this);
			rd.parse(new InputSource(new FileInputStream(filePath)));

			String fullFilePath = new File(filePath).getAbsolutePath();
			String dir = FilenameUtils.getFullPath(fullFilePath);
			String tplFullFilePath = new File(dir, _ref).getAbsolutePath();
			String outFullFilePath = new File(dir, _out).getAbsolutePath();

			_writer = new PrintWriter(new BufferedWriter(new FileWriter(outFullFilePath)));
			rd.parse(new InputSource(new FileInputStream(tplFullFilePath)));
			_writer.close();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
