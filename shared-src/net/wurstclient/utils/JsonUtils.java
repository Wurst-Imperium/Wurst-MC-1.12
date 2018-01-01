/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class JsonUtils
{
	public static final Gson gson = new Gson();
	public static final Gson prettyGson =
		new GsonBuilder().setPrettyPrinting().create();
	public static final JsonParser jsonParser = new JsonParser();
}
