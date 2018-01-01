/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods.chat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.Category;
import net.wurstclient.features.Mod;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.files.WurstFolders;
import net.wurstclient.hooks.FrameHook;
import net.wurstclient.spam.SpamProcessor;
import net.wurstclient.spam.exceptions.UnreadableTagException;
import net.wurstclient.spam.tag.Tag;
import net.wurstclient.utils.MiscUtils;

@Mod.Bypasses
@Mod.DontSaveState
public final class SpammerMod extends Mod
{
	private JDialog dialog;
	private static JSpinner delaySpinner;
	private JTextArea spamArea;
	private String spam;
	
	public SpammerMod()
	{
		super("Spammer",
			"Automatically spams messages in the chat. It can also run Wurst commands automatically.");
		setCategory(Category.CHAT);
	}
	
	@Override
	public void onEnable()
	{
		new Thread("Spammer")
		{
			@Override
			public void run()
			{
				dialog =
					new JDialog((JFrame)null, SpammerMod.this.getName(), false);
				dialog
					.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent e)
					{
						wurst.mods.spammerMod.setEnabled(false);
					}
				});
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
				JMenuBar menubar = new JMenuBar();
				
				JMenu fileMenu = new JMenu("File");
				JMenuItem fileLoad = new JMenuItem("Load spam from file");
				fileLoad.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser fileChooser =
							new JFileChooser(WurstFolders.SPAM.toFile())
							{
								@Override
								protected JDialog createDialog(Component parent)
									throws HeadlessException
								{
									JDialog dialog = super.createDialog(parent);
									dialog.setAlwaysOnTop(true);
									return dialog;
								}
							};
						fileChooser
							.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						fileChooser.addChoosableFileFilter(
							new FileNameExtensionFilter("All supported files",
								"wspam", "txt"));
						fileChooser.addChoosableFileFilter(
							new FileNameExtensionFilter("WSPAM files",
								"wspam"));
						fileChooser.addChoosableFileFilter(
							new FileNameExtensionFilter("TXT files", "txt"));
						int action = fileChooser.showOpenDialog(dialog);
						if(action == JFileChooser.APPROVE_OPTION)
							try
							{
								File file = fileChooser.getSelectedFile();
								BufferedReader load =
									new BufferedReader(new InputStreamReader(
										new FileInputStream(file), "UTF-8"));
								String newspam = load.readLine();
								for(String line =
									""; (line = load.readLine()) != null;)
									newspam += "\n" + line;
								load.close();
								spamArea.setText(newspam);
								updateSpam();
							}catch(IOException e1)
							{
								e1.printStackTrace();
								MiscUtils.simpleError(e1, fileChooser);
							}
					}
				});
				fileMenu.add(fileLoad);
				JMenuItem fileSave = new JMenuItem("Save spam to file");
				fileSave.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser fileChooser =
							new JFileChooser(WurstFolders.SPAM.toFile())
							{
								@Override
								protected JDialog createDialog(Component parent)
									throws HeadlessException
								{
									JDialog dialog = super.createDialog(parent);
									dialog.setAlwaysOnTop(true);
									return dialog;
								}
							};
						fileChooser
							.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						fileChooser.addChoosableFileFilter(
							new FileNameExtensionFilter("WSPAM files",
								"wspam"));
						int action = fileChooser.showSaveDialog(dialog);
						if(action == JFileChooser.APPROVE_OPTION)
							try
							{
								File file = fileChooser.getSelectedFile();
								if(!file.getName().endsWith(".wspam"))
									file = new File(file.getPath() + ".wspam");
								PrintWriter save =
									new PrintWriter(new OutputStreamWriter(
										new FileOutputStream(file), "UTF-8"));
								updateSpam();
								for(String line : spam.split("\n"))
									save.println(line);
								save.close();
							}catch(IOException e1)
							{
								e1.printStackTrace();
								MiscUtils.simpleError(e1, fileChooser);
							}
					}
				});
				fileMenu.add(fileSave);
				fileMenu.add(new JSeparator());
				JMenuItem fileOpenFolder = new JMenuItem("Open spam folder");
				fileOpenFolder.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MiscUtils.openFile(WurstFolders.SPAM);
					}
				});
				fileMenu.add(fileOpenFolder);
				menubar.add(fileMenu);
				JMenuItem fileOpenLink = new JMenuItem("Get more spam online");
				fileOpenLink.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MiscUtils.openLink(
							"https://www.wurstclient.net/downloads/wspam/");
					}
				});
				fileMenu.add(fileOpenLink);
				menubar.add(fileMenu);
				
				JMenu editMenu = new JMenu("Edit");
				JMenuItem editNewVar = new JMenuItem("New variable");
				editNewVar.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						final JDialog editDialog =
							new JDialog(dialog, "New variable");
						JPanel mainPanel = new JPanel();
						mainPanel.setLayout(
							new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
						JPanel namePanel = new JPanel();
						JLabel nameLabel = new JLabel("Variable name");
						namePanel.add(nameLabel);
						final JTextField nameField = new JTextField(16);
						namePanel.add(nameField);
						mainPanel.add(namePanel);
						JPanel valuePanel = new JPanel();
						JLabel valueLabel = new JLabel("Variable value");
						valuePanel.add(valueLabel);
						final JTextField valueField = new JTextField(16);
						valuePanel.add(valueField);
						mainPanel.add(valuePanel);
						JPanel createPanel = new JPanel();
						JButton createButton = new JButton("Create variable");
						createButton.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								updateSpam();
								spamArea.setText("<var "
									+ (nameField.getText().isEmpty()
										? "undefined" : nameField.getText())
									+ ">"
									+ (valueField.getText().isEmpty()
										? "undefined" : valueField.getText())
									+ "</var><!--\n-->" + spam);
								editDialog.dispose();
							}
						});
						createPanel.add(createButton);
						mainPanel.add(createPanel);
						editDialog.setContentPane(mainPanel);
						editDialog.pack();
						editDialog.setLocationRelativeTo(dialog);
						editDialog.setAlwaysOnTop(true);
						editDialog.setVisible(true);
					}
				});
				editMenu.add(editNewVar);
				menubar.add(editMenu);
				
				JMenu viewMenu = new JMenu("View");
				JCheckBoxMenuItem viewFont = new JCheckBoxMenuItem(
					"Simulate ingame font", wurst.options.spamFont);
				viewFont.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						wurst.options.spamFont = !wurst.options.spamFont;
						ConfigFiles.OPTIONS.save();
						updateFont();
					}
				});
				viewMenu.add(viewFont);
				menubar.add(viewMenu);
				
				JMenu helpMenu = new JMenu("Help");
				JMenuItem helpIntro = new JMenuItem("Introduction to WSPAM");
				helpIntro.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JOptionPane.showOptionDialog(dialog,
							new UnreadableTagException("", 0).getHelp(), "Help",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null,
							new String[]{"OK"}, 0);
					}
				});
				helpMenu.add(helpIntro);
				JMenuItem helpTaglist = new JMenuItem("Available Tags");
				helpTaglist.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JDialog helpDialog =
							new JDialog(dialog, "Available tags");
						Object[][] rowData = new Object[SpamProcessor.tagManager
							.getActiveTags().size()][3];
						Iterator itr =
							SpamProcessor.tagManager.getActiveTags().iterator();
						for(int i = 0; itr.hasNext(); i++)
						{
							Tag tag = (Tag)itr.next();
							rowData[i][0] = tag.getName();
							rowData[i][1] = tag.getDescription();
							rowData[i][2] = tag.getSyntax();
						}
						JTable table = new JTable(rowData,
							new Object[]{"Name", "Description", "Syntax"});
						table.setDefaultEditor(Object.class, null);
						table.setFillsViewportHeight(true);
						table.setCellSelectionEnabled(true);
						JScrollPane tablePane = new JScrollPane(table);
						helpDialog.setContentPane(tablePane);
						helpDialog.pack();
						helpDialog.setLocationRelativeTo(dialog);
						helpDialog.setAlwaysOnTop(true);
						helpDialog.setVisible(true);
					}
				});
				helpMenu.add(helpTaglist);
				JMenuItem helpVarlist = new JMenuItem("Pre-defined variables");
				helpVarlist.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JDialog helpDialog =
							new JDialog(dialog, "Pre-defined variables");
						Object[][] rowData = new Object[SpamProcessor.varManager
							.getSpammerVars().size()][2];
						Iterator itr = SpamProcessor.varManager.getSpammerVars()
							.entrySet().iterator();
						for(int i = 0; itr.hasNext(); i++)
						{
							Map.Entry var = (Map.Entry)itr.next();
							rowData[i][0] = "§_" + var.getKey() + ";";
							rowData[i][1] = "\"" + var.getValue() + "\"";
							if(var.getValue().equals(" "))
								rowData[i][1] = "\" \" (space)";
							else if(var.getValue().equals("\n"))
								rowData[i][1] = "\"\" (line break)";
						}
						JTable table =
							new JTable(rowData, new Object[]{"Name", "Value"});
						table.setDefaultEditor(Object.class, null);
						table.setFillsViewportHeight(true);
						table.setCellSelectionEnabled(true);
						JScrollPane tablePane = new JScrollPane(table);
						helpDialog.setContentPane(tablePane);
						helpDialog.pack();
						helpDialog.setLocationRelativeTo(dialog);
						helpDialog.setAlwaysOnTop(true);
						helpDialog.setVisible(true);
					}
				});
				helpMenu.add(helpVarlist);
				menubar.add(helpMenu);
				
				menubar.add(Box.createHorizontalGlue());
				
				panel.add(menubar);
				
				JPanel delayPanel =
					new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
				JLabel delayLabel = new JLabel("Delay between messages:");
				delayPanel.add(delayLabel);
				delaySpinner =
					new JSpinner(new SpinnerNumberModel(wurst.options.spamDelay,
						0, 3600000, 50));
				delaySpinner.addChangeListener(new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						wurst.options.spamDelay =
							(Integer)delaySpinner.getValue();
						ConfigFiles.OPTIONS.save();
					}
				});
				delaySpinner.setEditor(
					new JSpinner.NumberEditor(delaySpinner, "#'ms'"));
				delayPanel.add(delaySpinner);
				panel.add(delayPanel);
				
				spamArea = new JTextArea();
				spamArea.getDocument()
					.addDocumentListener(new DocumentListener()
					{
						@Override
						public void removeUpdate(DocumentEvent e)
						{
							updateSpam();
						}
						
						@Override
						public void insertUpdate(DocumentEvent e)
						{
							updateSpam();
						}
						
						@Override
						public void changedUpdate(DocumentEvent e)
						{
							updateSpam();
						}
					});
				JScrollPane spamPane = new JScrollPane(spamArea);
				updateFont();
				spamPane.setPreferredSize(new Dimension(500, 200));
				panel.add(spamPane);
				
				JButton startButton = new JButton("Spam");
				startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				startButton.setFont(
					new Font(startButton.getFont().getFamily(), Font.BOLD, 18));
				startButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						new Thread()
						{
							@Override
							public void run()
							{
								try
								{
									updateSpam();
									SpamProcessor.process(spam, SpammerMod.this,
										true);
									spam = SpamProcessor.process(spam,
										SpammerMod.this, false);
									if(spam == null)
										return;
									for(int i =
										0; i < spam.split("\n").length; i++)
									{
										String message = spam.split("\n")[i];
										WMinecraft.getPlayer()
											.sendAutomaticChatMessage(message);
										Thread.sleep(wurst.options.spamDelay);
									}
								}catch(Exception e)
								{
									System.err.println("Exception in Spammer:");
									e.printStackTrace();
								}
							};
						}.start();
					}
				});
				panel.add(startButton);
				
				dialog.setContentPane(panel);
				dialog.pack();
				dialog.setLocationRelativeTo(FrameHook.getFrame());
				dialog.setAlwaysOnTop(true);
				mc.setIngameNotInFocus();
				dialog.setVisible(true);
			}
		}.start();
	}
	
	@Override
	public void onDisable()
	{
		spam = null;
		new Thread()
		{
			@Override
			public void run()
			{
				if(dialog != null)
					dialog.dispose();
			}
		}.start();
	}
	
	private void updateSpam()
	{
		try
		{
			spam = spamArea.getDocument().getText(0,
				spamArea.getDocument().getLength());
		}catch(BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	private void updateFont()
	{
		try
		{
			Font mcfont = Font.createFont(Font.TRUETYPE_FONT,
				this.getClass().getClassLoader()
					.getResourceAsStream("assets/minecraft/font/mcfont.ttf"));
			mcfont = mcfont.deriveFont(12F);
			Font defaultFont = new Font("Monospaced", Font.PLAIN, 14);
			spamArea.setFont(wurst.options.spamFont ? mcfont : defaultFont);
		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public static void updateDelaySpinner()
	{
		if(delaySpinner != null)
			delaySpinner.setValue(wurst.options.spamDelay);
	}
	
	public JDialog getDialog()
	{
		return dialog;
	}
	
	public void goToLine(int line)
	{
		int lineStart = 0;
		int lineEnd = 0;
		int currentLine = 0;
		if(line >= spam.split("\n").length)
		{
			lineStart = spam.lastIndexOf("\n") + 1;
			currentLine = line;
		}
		while(currentLine < line)
		{
			lineStart = spam.indexOf("\n", lineStart) + 1;
			currentLine++;
		}
		if(spam.indexOf("\n", lineStart) > -1)
			lineEnd = spam.indexOf("\n", lineStart);
		else
			lineEnd = spam.length();
		spamArea.setCaretPosition(lineStart);
		spamArea.setSelectionStart(lineStart);
		spamArea.setSelectionEnd(lineEnd);
		spamArea.requestFocus();
	}
}
