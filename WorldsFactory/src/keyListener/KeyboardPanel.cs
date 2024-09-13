using System;
using System.Collections.Generic;
using System.Linq;
using Avalonia.Controls;
using Avalonia.Input;
using Newtonsoft.Json;
using Serilog;

namespace WorldsFactory.keyListener;

/// <summary>
/// This represents set of current keyboard shortcuts, 
/// but it can also contan a shortcut for changing the keyboard panel
/// to another one to switch to another shortcut set.
/// </summary>
public class KeyboardPanel
{

	[JsonProperty("name")]
	public string? Name { get; set; }

	[JsonProperty("set")]
	public List<KeyNode> Set = new List<KeyNode>();


	public KeyNode Root = new KeyNode();
	public KeyNode? CurrentKeyNode;
	public void Setup(KeyListener keyListener, Queue<string> dependencies)
	{
		KeyNode currentKeyNode = Root;
		foreach (KeyNode keyNode in Set)
		{
			currentKeyNode = Root;
			foreach (Key key in keyNode.keys!)
			{
				KeyNode? newKeyNode;
				if (currentKeyNode!.childes.ContainsKey(key.Tag)
					&& ((currentKeyNode.childes[key.Tag].Item1 != null && key.Action == KeyAction.down)
						|| (currentKeyNode.childes[key.Tag].Item2 != null && key.Action == KeyAction.up)))
					newKeyNode = (currentKeyNode.childes[key.Tag].Item1 != null && key.Action == KeyAction.down) ?
						currentKeyNode.childes[key.Tag].Item1 : currentKeyNode.childes[key.Tag].Item2;
				else
				{
					newKeyNode = new KeyNode();
					if (key.Action == KeyAction.down)
						currentKeyNode.childes.Add(key.Tag, new Tuple<KeyNode?, KeyNode?>(newKeyNode, null));
					else
						currentKeyNode.childes.Add(key.Tag, new Tuple<KeyNode?, KeyNode?>(null, newKeyNode));
				}
				currentKeyNode = newKeyNode!;

				if (keyNode.keys.LastOrDefault() == key)
				{
					newKeyNode!.functions.Add(keyNode.reaction!);
					if(keyNode.reaction is null){
						Log.Warning("UI-KeyboardSet-Setup: reaction is null");
						continue;
					}
			
					if (keyNode.reaction!.TypeFunction == TypeFunction.changeKeyboardSet)
						if (!keyListener.KeyboardPanelList.ContainsKey(keyNode.reaction!.Name!))
							dependencies.Enqueue(keyNode.reaction!.Name!);
				}
			}
		}
		this.CurrentKeyNode = Root;
	}
	public void KeyChanged(KeyAction keyUpdate, Window window, KeyEventArgs e, KeyListener keyListener)
	{
		/* TODO: 
			move to next key node and execute all methods 
			*/
		if (CurrentKeyNode is not null)
		{
			if (!CurrentKeyNode.childes.ContainsKey(e.Key))
				return;

			KeyNode? nextKeyNode = keyUpdate == KeyAction.down ?
				CurrentKeyNode.childes[e.Key].Item1 : CurrentKeyNode.childes[e.Key].Item2;

			if (nextKeyNode is not null)
			{
				CurrentKeyNode = nextKeyNode;
				CurrentKeyNode.run(keyListener);
			}
		}
		else
		{
			Log.Error("UI-KeyboardSet-keyChanged: currentKeyNode is null");
		}
	}

	public void JumpToRoot()
	{
		CurrentKeyNode = Root;
	}
}