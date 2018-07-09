package ru.myx.ae3impl.skinner;

import java.util.Collections;

import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.l2.LayoutDefinition;
import ru.myx.ae3.l2.TargetContext;
import ru.myx.ae3.l2.skin.Skin;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.SkinScanner;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;
import ru.myx.webdav.Executive;

/** @author myx */
public final class SkinnerDAV implements Skinner {

	private static final String OWNER = "SKINNER/WEBDAV";
	
	private static final BaseObject TITLE;
	
	static {
		TITLE = MultivariantString.getString(
				"WebDAV: site file access for developer/supervisor", //
				Collections.singletonMap(
						"ru", //
						"WebDAV: доступ к файлам сайта для разработчика/супервизора"));
	}
	
	private final Entry root;
	
	private final Executive executive;
	
	/** @param root */
	public SkinnerDAV(final Entry root) {
		this.root = root;
		this.executive = new Executive(root);
	}
	
	@Override
	public LayoutDefinition<TargetContext<?>> getLayoutDefinition(final String name) {

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getName() {

		return "skin-skinner-dav";
	}
	
	@Override
	public Entry getRoot() {

		return this.root;
	}
	
	@Override
	public Skin getSkinParent() {

		return SkinScanner.getSystemSkinner("skin-standard-html");
	}

	@Override
	public BaseObject getTitle() {

		return SkinnerDAV.TITLE;
	}
	
	@Override
	public final ReplyAnswer handleReply(final ReplyAnswer response) {

		return response;
	}
	
	@Override
	public ReplyAnswer handleReplyOnce(final ReplyAnswer response) {

		return response;
	}
	
	@Override
	public boolean isAbstract() {

		return false;
	}
	
	@Override
	public final ReplyAnswer onQuery(final ServeRequest query) {

		if (!Base.getBoolean(query.getAttributes(), "Secure", true)) {
			return Reply
					.string(
							SkinnerDAV.OWNER, //
							query,
							"Not secure interface: try another one!") //
					.setCode(Reply.CD_DENIED);
		}
		final ExecProcess process = Exec.currentProcess();
		final AccessManager manager = Context.getServer(process).getAccessManager();
		final String userId = Context.getUserId(process);
		if (!manager.isInGroup(userId, "def.supervisor")) {
			return Reply
					.string(
							SkinnerDAV.OWNER, //
							query,
							"Not enough priveleges to access this resource, sorry!") //
					.setCode(Reply.CD_DENIED);
		}
		return this.executive.doMethod(query);
	}
	
	@Override
	public boolean requireAuth() {

		return true;
	}
	
	@Override
	public boolean requireSecure() {

		return true;
	}
	
	@Override
	public boolean scan() {

		return true;
	}
}
