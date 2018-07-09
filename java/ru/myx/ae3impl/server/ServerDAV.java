package ru.myx.ae3impl.server;

/*
 * Created on 17.10.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.util.Properties;

import ru.myx.ae1.AbstractPluginInstance;
import ru.myx.ae1.know.AbstractServer;
import ru.myx.ae3.Engine;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.serve.Serve;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.webdav.Executive;

/**
 * @author myx
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
class ServerDAV extends AbstractServer {

	private final AbstractPluginInstance settingsSelf;

	private final Executive executive;

	ServerDAV(final String id, final BaseObject attributes) {
		super(id, Base.getString(attributes, "domain", id), Exec.currentProcess());
		this.settingsSelf = new AbstractPluginInstance() {
			// empty
		};
		{
			final Properties serverPluginProperties = new Properties();
			serverPluginProperties.setProperty("id", "$$self");
			this.settingsSelf.setup(this, serverPluginProperties);
		}
		final BaseObject selfSettings = this.settingsSelf.getSettingsProtected();
		final String accessUser = Base.getString(selfSettings, "user", "admin");
		final String accessPassword = Base.getString(selfSettings, "password", "1234");
		selfSettings.baseDefine("user", accessUser);
		selfSettings.baseDefine("password", accessPassword);
		this.settingsSelf.commitProtectedSettings();
		this.executive = new Executive(this.getVfsRootEntry());
	}

	@Override
	public boolean absorb(final ServeRequest query) {

		Serve.checkParsePostParameters(query);
		final ReplyAnswer response = this.renderQuery(query).setAttribute("Server-Id", this.getServerIdentity());
		query.getResponseTarget().apply(response);
		return true;
	}

	private final String getServerIdentity() {

		final String serverIdentity;
		{
			final String identityCheck = Base.getString(this.settingsSelf.getSettingsPrivate(), "identity", "").trim();
			if (identityCheck.length() == 0) {
				serverIdentity = Engine.createGuid();
				this.settingsSelf.getSettingsPrivate().baseDefine("identity", serverIdentity);
				this.settingsSelf.commitPrivateSettings();
			} else {
				serverIdentity = identityCheck;
			}
		}
		return serverIdentity;
	}

	private final ReplyAnswer renderQuery(final ServeRequest query) {

		return this.executive.doMethod(query);
	}
}
