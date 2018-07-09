package ru.myx.webdav;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

class MethodPut extends Method {
	
	private final StorageManager storageManager;

	/**
	 * Create and initialize the PutMethod.
	 *
	 * @param storageManager
	 * @param uri
	 * @param query
	 *
	 * @see Method
	 */
	MethodPut(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super(uri, query);
		this.storageManager = storageManager;
	}

	@Override
	protected ReplyAnswer run() {
		
		final Entry file = this.storageManager.getResourceFile(this.uri, true);
		if (file == null) {
			return Reply.string("DAV", this.query, "Cannot create here - parent folder seems to be read-only!").setCode(Reply.CD_CONFLICT);
		}
		final boolean existent = file.isExist();
		final TransferCopier copier;
		try {
			copier = this.query.toBinary().getBinary();
		} catch (final Throwable ex) {
			Report.exception("WEBDAV", "Exception getting query contents", ex);
			return Reply.string("DAV", this.query, Format.Throwable.toText(ex)).setCode(Reply.CD_EXCEPTION);
		}
		Report.info("WEBDAV", "PUT: " + this.query.getParameters() + ", uri=" + this.uri + ", length=" + copier.length());
		this.storageManager.putResource(this.uri, copier, true);
		return Reply.string("DAV", this.query, "Done.").setCode(existent
			? Reply.CD_OK
			: Reply.CD_CREATED);
	}
}
