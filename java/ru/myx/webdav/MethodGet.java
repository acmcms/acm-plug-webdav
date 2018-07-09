package ru.myx.webdav;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Dom;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;

/**
 * The GetMethod class encapsulates the Commands necessary to validate and
 * retrieve a resource file pointed to by a Uri.
 * 
 * @see Method
 * @author Marc Eaddy
 * @version 1.0, 8 Nov 1997
 */
@SuppressWarnings("javadoc")
class MethodGet extends Method {
	private final StorageManager	storageManager;
	
	/**
	 * @param storageManager
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodGet(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super( uri, query );
		this.storageManager = storageManager;
	}
	
	@Override
	protected ReplyAnswer run() {
		final String uri = this.uri.toString();
		final Entry file = this.storageManager.getResourceFile( this.uri, false );
		if (file == null || !file.isExist() || uri.length() == 1) {
			final Uri identifierUrl = new Uri( uri.substring( 0, uri.lastIndexOf( '/' ) ) );
			final Entry folder = this.storageManager.getResourceFile( identifierUrl, false );
			if (folder == null || !folder.isExist()) {
				return Reply.string( "DAV", this.query, "folder not found: " + this.query.getResourceIdentifier() )
						.setCode( Reply.CD_UNKNOWN );
			}
			if (uri.endsWith( "/.project" )) {
				return this.skinResponse( identifierUrl, folder, ".project" );
			}
			if (uri.endsWith( "/index.html" )) {
				return this.skinResponse( identifierUrl, folder, "index.html" );
			}
			if (uri.length() == 1) {
				assert "/".equals( uri );
				return this.scriptResponse( identifierUrl, folder, "/default.html" );
			}
			{
				final ReplyAnswer answer = this.scriptResponse( identifierUrl, folder, uri );
				if (answer != null) {
					return answer;
				}
			}
			{
				return Reply.string( "DAV", this.query, "resource not found: " + this.query.getResourceIdentifier() )
						.setCode( Reply.CD_UNKNOWN );
			}
		}
		if (file.isBinary()) {
			final TransferCopier binary = file.toBinary().getBinaryContent().baseValue();
			return Reply.binary( "DAV", this.query, binary ).setContentType( MimeType.forName( this.uri.toString(),
					"application/octet-stream" ) );
		}
		
		final List<CollectionMember> members = new ArrayList<>();
		this.storageManager.listCollection( this.uri, members, null );
		final Element response = WebdavXML.createElement( "multistatus" );
		for (final CollectionMember member : members) {
			final Element elemProps = WebdavXML.createElement( "prop" );
			elemProps.appendChild( member.createResourceTypeElement() );
			final Element elemResponse = WebdavXML.createElement( "response" );
			elemResponse.appendChild( UriHelper.createHrefElement( member.uri().toString() ) );
			elemResponse.appendChild( elemProps );
			elemResponse.appendChild( WebDavResponse.createHttpStatusElement( 200 ) );
			response.appendChild( elemResponse );
		}
		return Reply.string( "DAV", this.query, Dom.toXmlCompact( response ) ).setCode( Reply.CD_MULTISTATUS )
				.setContentType( "text/xml" );
	}
	
	private ReplyAnswer scriptResponse(final Uri identifierUrl, final Entry folder, final String name) {
		try {
			final ExecProcess ctx = Exec.currentProcess();
			{
				final BaseList<Entry> members = folder.toContainer().getContentCollection( null ).baseValue();
				ctx.contextCreateMutableBinding( "folder", Base.forUnknown( folder ), false );
				ctx.contextCreateMutableBinding( "path", Base.forArray( identifierUrl.toString().split( "/" ) ), false );
				ctx.contextCreateMutableBinding( "children", members, false );
			}
			final Skinner davSkinner = Context.getServer( ctx ).getSkinner( "skin-internal-webdav" );
			{
				final String resourceIdentifier = this.query.getResourceIdentifier();
				this.query.setResourceIdentifier( name );
				final ReplyAnswer answer = davSkinner.onQuery( this.query );
				if (answer != null) {
					return davSkinner.handleReply( answer )//
							.setNoCaching();
				}
				this.query.setResourceIdentifier( resourceIdentifier );
			}
		} catch (final Throwable e) {
			Report.exception( "DVSRV", "while building default response", e );
			return Reply.string( "DAV",//
					this.query,
					"resource error: " + this.query.getResourceIdentifier() + "\r\n" + Format.Throwable.toText( e ) ).//
					setCode( Reply.CD_EXCEPTION );
		}
		return null;
	}
	
	private ReplyAnswer skinResponse(final Uri identifierUrl, final Entry folder, final String name) {
		final ExecProcess ctx = Exec.currentProcess();
		final BaseList<Entry> members = folder.toContainer().getContentCollection( null ).baseValue();
		final Skinner davSkinner = Context.getServer( ctx ).getSkinner( "skin-internal-webdav" );
		{
			final ReplyAnswer answer = Reply.object( "WEBDAV", //
					this.query,
					new BaseNativeObject()//
							.putAppend( "template", "dav-ui-" + name )//
							.putAppend( "folder", Base.forUnknown( folder ) )//
							.putAppend( "path", Base.forArray( identifierUrl.toString().split( "/" ) ) )//
							.putAppend( "children", members )//
					);
			if (answer != null) {
				return davSkinner.handleReply( answer )//
						.setNoCaching();
			}
		}
		return null;
	}
}
