/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.font;

import java.awt.Font;

public class Fonts
{
	public static WurstFontRenderer segoe22;
	public static WurstFontRenderer segoe18;
	public static WurstFontRenderer segoe15;
	
	public static void loadFonts()
	{
		segoe22 = new WurstFontRenderer(new Font("Segoe UI", Font.PLAIN, 44),
			true, 8);
		segoe18 = new WurstFontRenderer(new Font("Segoe UI", Font.PLAIN, 36),
			true, 8);
		segoe15 = new WurstFontRenderer(new Font("Segoe UI", Font.PLAIN, 30),
			true, 8);
	}
}
