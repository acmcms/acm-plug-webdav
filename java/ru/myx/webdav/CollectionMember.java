package ru.myx.webdav;

import org.w3c.dom.Element;

import ru.myx.ae3.vfs.Entry;

class CollectionMember {
	/** Uri of the collection member */
	private Uri		uri				= null;
	
	/** Whether or not this member is also a collection */
	private boolean	bIsCollection	= false;
	
	// CREATORS
	/**
	 * Creates a collection member based on a uri.
	 * 
	 * @param file
	 * 
	 * @param uri
	 *            [IN] Uri of the collection member
	 */
	CollectionMember(final Entry file, final Uri uri) {
		this.uri = uri;
		this.bIsCollection = file.isContainer();
	}
	
	// HELPERS
	/**
	 * Creates a &lt;resourcetype&gt; XML element describing this
	 * CollectionMember.
	 * 
	 * @return Returns a &lt;resourcetype&gt; XML element
	 */
	public Element createResourceTypeElement() {
		final Element elemResourceType = WebdavXML.createElement( "resourcetype" );
		if (this.bIsCollection) {
			elemResourceType.appendChild( WebdavXML.createElement( "collection" ) );
		}
		return elemResourceType;
	}
	
	/**
	 * Creates an HTML line describing this CollectionMember. The HTML is meant
	 * to be used to display to the user when the user performs a GET on a
	 * directory (collection). The directory listing will contain multiple
	 * CollectionMember entries.
	 * <P>
	 * For example:<BR>
	 * 
	 * <PRE>
	 * 
	 * &lt;IMG SRC=&quot;/icons/member.gif&quot; ALT=&quot;[MEMBER]&quot;&gt; &lt;A
	 * HREF=&quot;\index.html&quot;&gt;\index.html&lt;/A&gt; 31-Dec-69 6:59:59 PM 1796
	 * 
	 * </PRE>
	 * 
	 * Will display as:<BR>
	 * <IMG SRC="/icons/member.gif" ALT="[MEMBER]"> <A
	 * HREF="\index.html">\index.html</A> 31-Dec-69 6:59:59 PM 1796
	 */
	@Override
	public String toString() {
		return "CollectionMember: " + this.uri;
	}
	
	/**
	 * @return The Uri representation of this CollectionMember.
	 */
	public Uri uri() {
		return this.uri;
	}
}
