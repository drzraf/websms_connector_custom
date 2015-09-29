/*
 * Copyright (C) 2015 Raphaël Droz
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package de.ub0r.android.websms.connector.custom;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.ub0r.android.websms.connector.common.BasicConnector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.Log;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

import android.text.TextUtils;
// import java.text.SimpleDateFormat;
// import java.util.Calendar;


/**
 * AsyncTask to manage IO to custom HTTP webserver.
 * 
 * @author Raph
 */
public final class ConnectorCustom extends BasicConnector {
	/** Tag for output. */
	private static final String TAG = "custom";
	private static final String SUBCONN0 = "SUBCONN0";

	@Override
	public ConnectorSpec initSpec(final Context context) {
		String name = context.getString(R.string.connector_name);
		ConnectorSpec c = new ConnectorSpec(name);
		c.setAuthor(context.getString(R.string.connector_author));
		c.setBalance(null);
		c.setCapabilities(ConnectorSpec.CAPABILITIES_UPDATE
				  | ConnectorSpec.CAPABILITIES_SEND
				  | ConnectorSpec.CAPABILITIES_PREFS);
		c.addSubConnector(SUBCONN0, SUBCONN0, 0);

		return c;
	}

	@Override
	public ConnectorSpec updateSpec(final Context context,
					final ConnectorSpec connectorSpec) {
		final SharedPreferences p = PreferenceManager
			.getDefaultSharedPreferences(context);
		if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
			if (p.getString(Preferences.PREFS_URL, "").length() > 0) {
				connectorSpec.setReady();
			} else {
				connectorSpec.setStatus(ConnectorSpec.STATUS_ENABLED);
			}
		} else {
			connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
		}
		return connectorSpec;
	}

	/**
	 * Check return code
	 * 
	 * @param context
	 *            {@link Context}
	 * @param ret
	 *            return code
	 * @return true if no error code
	 */
	private static boolean checkReturnCode(final Context context, final int ret) {
		Log.d(TAG, "ret=" + ret);
		switch (ret) {
		case 100:
			return true;
		case 110:
			throw new WebSMSException(context, R.string.error_input);
		case 131:
			throw new WebSMSException(context, R.string.error_recipient);
		case 132:
			throw new WebSMSException(context, R.string.error_sender);
		case 133:
			throw new WebSMSException(context, R.string.error_text);
		default:
			throw new WebSMSException(context, R.string.error, " code: " + ret);
		}
	}

	@Override
	protected String getParamUsername() {
		return "user";
	}

	@Override
	protected String getParamPassword() {
		return "password";
	}

	@Override
	protected String getParamRecipients() {
		return "to";
	}

	@Override
	protected String getParamSender() {
		return "from";
	}

	@Override
	protected String getParamText() {
		return "text";
	}

	@Override
	protected String getUsername(final Context context,
				     final ConnectorCommand command, final ConnectorSpec cs) {
		return "";
	}

	@Override
	protected String getPassword(final Context context,
				     final ConnectorCommand command, final ConnectorSpec cs) {
		final SharedPreferences p = PreferenceManager
			.getDefaultSharedPreferences(context);
		return "";
	}

	@Override
	protected String getRecipients(final ConnectorCommand command) {
		return Utils.joinRecipientsNumbers(Utils.national2international(command.getDefPrefix(),
										command.getRecipients()), ";", true);
	}

	@Override
	protected String getSender(final Context context,
				   final ConnectorCommand command, final ConnectorSpec cs) {
		return Utils.international2oldformat(Utils.getSender(context,
								     command.getDefSender()));
	}

	@Override
	protected String getUrlSend(final Context context, final ArrayList<BasicNameValuePair> d) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p.getString(Preferences.PREFS_URL, "");
	}

	@Override
	protected boolean usePost(final ConnectorCommand command) {
		return false;
	}

	@Override
	protected void addExtraArgs(final Context context,
				    final ConnectorCommand command, final ConnectorSpec cs,
				    final ArrayList<BasicNameValuePair> d) {
		d.add(new BasicNameValuePair("from", "websms"));
		// number cleanup (national, (un)prefixing, spliting, ... is done server-side
	}

	@Override
	protected void parseResponse(final Context context,
				     final ConnectorCommand command, final ConnectorSpec cs,
				     final String htmlText) {
		if (command.getType() == ConnectorCommand.TYPE_UPDATE) {
			// throw new IllegalArgumentException("unknown ConnectorCommand: UPDATE");
		} else if (command.getType() == ConnectorCommand.TYPE_SEND) {
			if (TextUtils.isEmpty(htmlText)) {
				throw new WebSMSException(context, R.string.error);
			}
			int resp = -1;
			try {
				resp = Integer.parseInt(htmlText.trim());
			} catch (NumberFormatException e) {
				throw new WebSMSException(context, R.string.error);
			}
			checkReturnCode(context, resp);
		} else {
			throw new IllegalArgumentException("unknown ConnectorCommand: "
							   + command.getType());
		}
	}
}
