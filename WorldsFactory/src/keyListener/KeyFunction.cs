using System;
using Serilog;

namespace WorldsFactory.keyListener;

/// <summary>
/// Represent a function that can be called by right 
/// key combination. The function can be added to the
/// Listener at runtime. If the function would be called
/// before its definition, the function is ignored.
/// </summary>
public class KeyFunction
{
	private Action function;
	public string Name;

	/// <summary>
	/// Represent a function that can be called by right 
	/// key combination. The function can be added to the
	/// Listener at runtime. If the function would be called
	/// before its definition, the function is ignored.
	/// </summary>
	public KeyFunction(string name, Action function)
	{
		this.function = function;
		this.Name = name;
		if (KeyListener.KeyFunctions.ContainsKey(name))
		{
			Log.Warning("KeyFunction: more key functions with same name \'" + name + "\'");
		}
		else KeyListener.KeyFunctions.Add(name, this);
	}
	
	/// <summary>
	/// Runs the specified function.
	/// </summary>
	public void Run()
	{
		function();
	}
}
