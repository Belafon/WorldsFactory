using System;
using System.IO;
using System.Collections.Generic;
using System.Configuration;
using Avalonia.Controls;
using Avalonia.Input;
using Newtonsoft.Json;
using System.Reflection;
using System.Threading;
using WorldsFactory.boot;
using Serilog;

namespace WorldsFactory.keyListener;

/// <summary>
/// Key listener, that can load keyboard sets (<see cref="KeyboardPanel"/>) from files. 
/// </summary>
public class KeyListener
{
	/// <summary>
	/// contains all of possible functions that can be assigned to keys
	/// this dictionary has to be full before keyborad set is loaded from file
	/// </summary>
	public static Dictionary<string, KeyFunction> KeyFunctions = new Dictionary<string, KeyFunction>();

	/// <summary>
	/// Key listener, that can load keyboard sets (<see cref="KeyboardPanel"/>) from files. 
	/// </summary>
	public KeyListener()
	{
		// lets setup all keyboard sets
		Queue<string> dependencies = new Queue<string>();
		CurrentKeyboardSet = loadKeyboardSet(loadKeyboardSetName());
		if (CurrentKeyboardSet is not null)
			CurrentKeyboardSet.Setup(this, dependencies);

		while (dependencies.Count != 0)
		{
			string keyboardSetName = dependencies.Dequeue();
			KeyboardPanel? keyboardSet = loadKeyboardSet(keyboardSetName);
			if (keyboardSet is not null)
				if (!keyboardSet.Name!.Equals("default") || keyboardSetName.Equals("default"))
					keyboardSet.Setup(this, dependencies);
				else CurrentKeyboardSet = keyboardSet;
			else CurrentKeyboardSet = loadKeyboardSet("default");
		}
	}

	private int keyDownCount = 0;
	public Dictionary<string, KeyboardPanel> KeyboardPanelList = new Dictionary<string, KeyboardPanel>();

	public KeyboardPanel? CurrentKeyboardSet;


	private Mutex keyChangedMutex = new Mutex();

	private Dictionary<Avalonia.Input.Key, bool> downKeys = new Dictionary<Avalonia.Input.Key, bool>();

	public void OnKeyChanged(KeyAction keyAction, Window window, KeyEventArgs e)
	{
		keyChangedMutex.WaitOne();

		if (CurrentKeyboardSet is not null)
		{
			switch (keyAction)
			{
				case KeyAction.down:
					if (!downKeys.ContainsKey(e.Key))
					{
						downKeys.Add(e.Key, true);
						keyDownCount++;
					}
					else if (!downKeys[e.Key])
					{
						downKeys[e.Key] = true;
						keyDownCount++;
					}
					break;
				case KeyAction.up:
					if (!downKeys.ContainsKey(e.Key))
						break;
					downKeys[e.Key] = false;
					keyDownCount--;
					if (keyDownCount <= 0)
					{
						keyDownCount = 0;
						CurrentKeyboardSet.JumpToRoot();
					}
					break;
			}
			CurrentKeyboardSet.KeyChanged(keyAction, window, e, this);
		}

		keyChangedMutex.ReleaseMutex();
	}

	public void OnLostFocusClear()
	{
		keyChangedMutex.WaitOne();
		foreach (var key in downKeys) // BUG repair this shady code
			downKeys[key.Key] = false;
		if (CurrentKeyboardSet is not null)
			CurrentKeyboardSet.JumpToRoot();
		keyChangedMutex.ReleaseMutex();
	}

	private string loadKeyboardSetName()
	{
		string? v = ConfigurationManager.AppSettings.Get("defaultKeyboardSet");
		
		if (v is not null)
			return v;
		else
		{
			Log.Error("Boot-KeyListener: loadKeyboardSetName() cannot load data from App.config");
			return "default";
		}
	}

	/// <summary>
	/// Recursively loads all required keyboard sets, 
	/// that are required by the specified keyboard set.
	/// </summary>
	/// <param name="keyboardSetName"></param>
	/// <returns></returns>
	private KeyboardPanel? loadKeyboardSet(string keyboardSetName)
	{
		/* convert string to readable form for file manager */
		string fileName = keyboardSetName;
		foreach (char c in Path.GetInvalidFileNameChars())
		{
			fileName = fileName.Replace(c, '_');
		}
		string path = $"res{Path.DirectorySeparatorChar}keyboardPanels{Path.DirectorySeparatorChar}"
			+ fileName
			+ ".json";

		string? json = FilesHandlers.loadFile(path);
		if (json is null)
		{
			Log.Information("Boot-KeyListener: keyboard set cannot be loaded, the default set will be loaded instead, file doesn't exist " + path);
			if (keyboardSetName.Equals("default"))
			{
				failLoadKeyboardSet();
				return null;
			}
			CurrentKeyboardSet = loadKeyboardSet("default");
			return CurrentKeyboardSet;
		}

		KeyboardPanel? set = null;
		try
		{
			set = JsonConvert.DeserializeObject<KeyboardPanel>(json);
		}
		catch (Exception e)
		{
			Log.Error("Boot-loadKeyboard: error in json file to load keyboard set from \'" + keyboardSetName + "\' ... " + e);
			if (keyboardSetName.Equals("default"))
			{
				failLoadKeyboardSet();
				return null;
			}
			CurrentKeyboardSet = loadKeyboardSet("default");
			return CurrentKeyboardSet;
		}

		if (set is null)
		{
			if (keyboardSetName.Equals("default"))
			{
				failLoadKeyboardSet();
				return null;
			}
			Log.Error("Boot-loadKeyboard: keyboard set in \'" + keyboardSetName + "\' cannot be converted");
		}
		else
		{
			if (!KeyboardPanelList.ContainsKey(set.Name!))
				KeyboardPanelList.Add(set.Name!, set);
			else
			{
				Log.Error("Boot-loadKeyboard: keyboard set \'" + keyboardSetName + "\' already exists");
				return KeyboardPanelList[set.Name!];
			}
		}
		Log.Information("kyeboard set \'" + keyboardSetName + "\' successfully loaded");
		return set;
	}

	private void failLoadKeyboardSet()
	{
		// TODO: show window to user
		//Log.Error("Boot-KeyListener: keyboard set load fail");
		//throw new NotImplementedException();
	}


}
