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

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import de.ub0r.android.websms.connector.common.ConnectorPreferenceActivity;

/**
 * Preferences.
 * 
 * @author Raph
 */
public final class Preferences extends ConnectorPreferenceActivity {
	/** Preference key: enabled. */
	static final String PREFS_ENABLED = "enable";
	/** Preference's name: url. */
	static final String PREFS_URL = "url";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.connector_custom_prefs);
	}
}
