/*
 * Copyright 1997-1998 by Marc Eaddy, Jonathan Shapiro, Shao Rong; ALL RIGHTS
 * RESERVED
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for research and educational purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all copies and
 * that both that the copyright notice and warranty disclaimer appear in
 * supporting documentation, and that the names of the copyright holders or any
 * of their entities not be used in advertising or publicity pertaining to
 * distribution of the software without specific, written prior permission. Use
 * of this software in whole or in parts for direct commercial advantage
 * requires explicit prior permission.
 * 
 * The copyright holders disclaim all warranties with regard to this software,
 * including all implied warranties of merchantability and fitness. In no event
 * shall the copyright holders be liable for any special, indirect or
 * consequential damages or any damages whatsoever resulting from loss of use,
 * data or profits, whether in an action of contract, negligence or other
 * tortuous action, arising out of or in connection with the use or performance
 * of this software.
 */
package ru.myx.webdav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.help.Dom;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/**
 * The PropFindMethod class encapsulates the Commands necessary to get the names
 * and values of properties defined on a resource.
 * 
 * @see Method
 * @author Jonathan Shapiro, Marc Eaddy
 * @version 2.0, 14 Dec 1997
 */
@SuppressWarnings("javadoc")
class MethodPropFind extends Method {
	
	private static final List<String> PROP_DEFAULT = Arrays.asList(new String[]{
			"getcontentlength", "getlastmodified", "creationdate", "href", "getcontenttype", "resourcetype", "isroot"
	});
	
	private static final List<String> INCORRECT = new ArrayList<>();
	
	private static final Collection<String> getPropertiesCollection(final BaseObject map) {
		
		final BaseObject object = map.baseGet("prop", BaseObject.UNDEFINED);
		assert object != null : "NULL java value";
		if (object == BaseObject.UNDEFINED) {
			return MethodPropFind.PROP_DEFAULT;
		}
		final BaseArray array = object.baseArray();
		if (array != null) {
			final int length = array.length();
			final Collection<String> result = Create.tempSet();
			for (int i = 0; i < length; ++i) {
				result.add(array.baseGet(i, BaseObject.UNDEFINED).baseToJavaString());
			}
			return result;
		}
		if (Base.hasKeys(object)) {
			final Collection<String> result = Create.tempSet();
			for (final Iterator<String> iterator = Base.keys(object); iterator.hasNext();) {
				final String key = iterator.next();
				result.add(key);
			}
			return result;
		}
		return Collections.singleton(String.valueOf(object));
	}
	
	private final StorageManager storageManager;
	
	private final Collection<String> properties;
	
	private final Depth depth;
	
	/**
	 * Create and initialize the PropFindMethod.
	 * 
	 * @param storageManager
	 * 			
	 * @param uri
	 *            - Location of the resource file
	 * @param query
	 */
	MethodPropFind(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super(uri, query);
		this.storageManager = storageManager;
		this.depth = new Depth(Base.getString(query.getAttributes(), "Depth", null));
		final boolean incorrect = !Method.checkParseXmlParameters("WEBDAV:PROPFIND", query);
		this.properties = incorrect
			? MethodPropFind.INCORRECT
			: MethodPropFind.getPropertiesCollection(query.getParameters());
	}
	
	@Override
	protected ReplyAnswer run() {
		
		if (this.properties == MethodPropFind.INCORRECT) {
			return Reply.empty("DAV", this.query).setCode(Reply.CD_BADQUERY);
		}
		final Entry root = this.storageManager.getResourceFile(this.uri, false);
		if (root == null || !root.isExist()) {
			return Reply.string("DAV", this.query, "No such resource!").setCode(Reply.CD_UNKNOWN).setContentType("text/plain");
		}
		Report.info("WEBDAV", "PROPFIND: " + this.query.getParameters() + ", uri=" + this.uri + ", props=" + this.properties);
		final List<CollectionMember> members = new ArrayList<>();
		this.storageManager.listCollection(this.uri, members, this.depth);
		if (members.isEmpty()) {
			return Reply.string("DAV", this.query, "No such resource!").setCode(Reply.CD_UNKNOWN).setContentType("text/plain");
		}
		final Element response = WebdavXML.createElement("multistatus");
		response.setAttribute("xmlns", "DAV:");
		for (final CollectionMember member : members) {
			final Uri uri = member.uri();
			final Entry file = this.storageManager.getResourceFile(uri, false);
			if (file == null || !file.isExist()) {
				continue;
			}
			final Element elemResponse = WebdavXML.createElement("response");
			final boolean collection = file.isContainer();
			final boolean document = file.isBinary();
			elemResponse.appendChild(UriHelper.createHrefElement(uri, collection));
			final Element nfoundProperties = WebdavXML.createElement("propstat");
			final Element nfoundProperty = WebdavXML.createElement("prop");
			final Element foundProperties = WebdavXML.createElement("propstat");
			final Element foundProperty = WebdavXML.createElement("prop");
			for (final String property : this.properties) {
				// if("href".equals(property)){
				// final Element outProp = WebdavXML.createElement(property);
				// outProp.appendChild(WebdavXML.createTextNode(uri.toString()));
				// outProps.appendChild(outProp);
				// continue;
				// }
				if ("displayname".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					outProp.appendChild(WebdavXML.createTextNode(file.getKey()));
					foundProperty.appendChild(outProp);
					continue;
				}
				if ("ishidden".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					if (file.isHidden()) {
						outProp.appendChild(WebdavXML.createTextNode("1"));
						foundProperty.appendChild(outProp);
					} else {
						nfoundProperty.appendChild(outProp);
					}
					continue;
				}
				if ("isreadonly".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					if (document) {
						if (!file.canWrite()) {
							outProp.appendChild(WebdavXML.createTextNode("1"));
							foundProperty.appendChild(outProp);
						} else {
							nfoundProperty.appendChild(outProp);
						}
					} else {
						nfoundProperty.appendChild(outProp);
					}
					continue;
				}
				if ("collection".equals(property) || "iscollection".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					if (collection) {
						outProp.appendChild(WebdavXML.createTextNode("1"));
						foundProperty.appendChild(outProp);
					} else {
						nfoundProperty.appendChild(outProp);
					}
					continue;
				}
				if ("getcontentlength".equals(property)) {
					if (document) {
						final Element outProp = WebdavXML.createElement(property);
						outProp.appendChild(WebdavXML.createTextNode(String.valueOf(file.toBinary().getBinaryContentLength())));
						foundProperty.appendChild(outProp);
					}
					continue;
				}
				if ("getcontenttype".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					if (document) {
						outProp.appendChild(WebdavXML.createTextNode(MimeType.forName(file.getKey(), "application/octet-stream")));
					} else {
						outProp.appendChild(WebdavXML.createTextNode("httpd/unix-directory"));
					}
					foundProperty.appendChild(outProp);
					continue;
				}
				if ("getlastmodified".equals(property) || "creationdate".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					// outProp.setAttributeNS("http://www.w3.org/2000/xmlns/",
					// "xmlns:b",
					// "urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/");
					// outProp.setAttributeNS("urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/",
					// "b:dt", "dateTime.rfc1123");
					outProp.appendChild(WebdavXML.createTextNode(Format.Web.date(file.getLastModified())));
					foundProperty.appendChild(outProp);
					continue;
				}
				if ("resourcetype".equals(property)) {
					foundProperty.appendChild(member.createResourceTypeElement());
					continue;
				}
				if ("isroot".equals(property)) {
					final Element outProp = WebdavXML.createElement(property);
					if (uri.toString().length() > 1) {
						nfoundProperty.appendChild(outProp);
					} else {
						outProp.appendChild(WebdavXML.createTextNode("1"));
						foundProperty.appendChild(outProp);
					}
					continue;
				}
				nfoundProperty.appendChild(WebdavXML.createElement(property));
			}
			foundProperties.appendChild(foundProperty);
			foundProperties.appendChild(WebDavResponse.createHttpStatusElement(200));
			elemResponse.appendChild(foundProperties);
			if (this.properties != MethodPropFind.PROP_DEFAULT && nfoundProperty.getChildNodes().getLength() > 0) {
				nfoundProperties.appendChild(nfoundProperty);
				nfoundProperties.appendChild(WebDavResponse.createHttpStatusElement(404));
				elemResponse.appendChild(nfoundProperties);
			}
			response.appendChild(elemResponse);
		}
		return Reply.string(
				"DAV", //
				this.query,
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + Dom.toXmlCompact(response) //
		) //
				.setAttribute("Content-Location", UriHelper.fixUri(this.uri.toString(), root.isContainer())) //
				.setCode(Reply.CD_MULTISTATUS) //
				.setContentType("text/xml");
	}
}
