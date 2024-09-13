using System;
using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Serilog;

namespace WorldsFactory.keyListener;
	public enum TypeFunction
	{
		writeText, callFunctionWithoutParameters, changeKeyboardSet
	}

	/// <summary>
	/// This represents a list of keys that has to be pressed
	/// to activate the reactions. 
	/// </summary>
	public class KeyNode
	{

		[JsonProperty("keys")]
		public List<Key>? keys;

		[JsonProperty("reaction")]
		public KeyReaction? reaction;


		public Dictionary<Avalonia.Input.Key, Tuple<KeyNode?, KeyNode?>> childes = new Dictionary<Avalonia.Input.Key, Tuple<KeyNode?, KeyNode?>>();
		public List<KeyReaction> functions = new List<KeyReaction>();
		public void run(KeyListener keyListener)
		{
			/* TODO: check right modes of window */
			foreach (var function in functions)
			{
				switch (function.TypeFunction)
				{
					case TypeFunction.writeText:
						break;
					case TypeFunction.callFunctionWithoutParameters:
						if (KeyListener.KeyFunctions.ContainsKey(function.Name!))
							KeyListener.KeyFunctions[function.Name!].Run();
						else
						{
							Log.Warning("KeyNode: attemption to call function whitch hasn't been defined in KEY_FUNCTIONS, the name is \'" + function + "\'");
						}
						break;
					case TypeFunction.changeKeyboardSet:
						if (keyListener.CurrentKeyboardSet is not null)
							keyListener.CurrentKeyboardSet.JumpToRoot();
						keyListener.CurrentKeyboardSet = keyListener.KeyboardPanelList[function.Name!];
						break;
				}
			}
		}
	}