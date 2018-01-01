/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.spam.exceptions;

public abstract class ExceptionWithDefaultHelp extends SpamException
{
	public ExceptionWithDefaultHelp(String message, int line)
	{
		super(message, line);
	}
	
	@Override
	public String getHelp()
	{
		return "<html><body width=\"512px\">" + "<h1>WSPAM</h1>"
			+ "<h2>Introduction</h2>"
			+ "<p>Spammer doesn't just use plain text for it's spam. It uses WSPAM, a markup language for spam that is based on tags and variables.</p>"
			+ "<p>For more information, please click the button below to go to the WSPAM reference.</p>"
			+ "<h2>Syntax for tags</h2>"
			+ "<div bgcolor=\"#000000\" color=\"#00ff00\">"
			+ "<code>&lt;name arg<sub>1</sub> arg<sub>2</sub>... arg<sub>n</sub>&gt;content&lt;/name&gt;</code>"
			+ "</div>"
			+ "<p>A list of all tags can be found in the Help menu.</p>"
			+ "<h2>Syntax for variables</h2>" + "<table cellpadding=\"0\"><tr>"
			+ "<td>Declaration:</td>"
			+ "<td><code bgcolor=\"#000000\" color=\"#00ff00\">&lt;var name&gt;value&lt;/var&gt;</code></td>"
			+ "</tr><tr>" + "<td>Usage: </td>"
			+ "<td><code bgcolor=\"#000000\" color=\"#00ff00\">§name;</code></td>"
			+ "</tr><tr>" + "<td>Usage of pre-defined variables: </td>"
			+ "<td><code bgcolor=\"#000000\" color=\"#00ff00\">§_name;</code></td>"
			+ "</tr></table>"
			+ "<p>A list of all pre-defined variables can be found in the Help menu.</p>"
			+ "<h2>Syntax for comments</h2>"
			+ "<div bgcolor=\"#000000\" color=\"#00ff00\">"
			+ "<code>&lt;!-- comment --&gt;</code>" + "</div>"
			+ "<h2>How to spam &lt; and &gt;</h2>"
			+ "<p>If you don't care about WSPAM and just want to spam the &lt; and &gt; characters, you can do that by typing <code bgcolor=\"#000000\" color=\"#00ff00\">§_lt;</code> and <code bgcolor=\"#000000\" color=\"#00ff00\">§_gt;</code>.</p>"
			+ "<p>Note that you can't spam the § character because sending it would instantly kick you from the server.</p>"
			+ "</body></html>";
	}
}
