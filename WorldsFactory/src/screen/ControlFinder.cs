using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using Avalonia;
using Avalonia.Controls;
using Avalonia.VisualTree;
using Serilog;
using System.Collections.Specialized;

namespace WorldsFactory.src.screen;
public class ControlFinder
{
	/// <summary>
	/// Custom method for Control finding in the VisualTree, 
	/// this is because for some unknown reason, the Find function
	/// from Avalonia did not work as expected.
	/// </summary>
	/// <param name="control"></param>
	/// <param name="name"></param>
	/// <param name="maxDeepness"></param>
	/// <typeparam name="T"></typeparam>
	/// <returns></returns>
	public static T? Find<T>(Visual control, string name, int maxDeepness = 8) where T : Visual
	{
		if (maxDeepness == 0)
			return null;

		var children = control.GetVisualChildren();
		foreach (var child in children)
		{
			Log.Information("ControlFinder: {0}", child.GetType().Name);
			if (child is T t && child.Name == name)
			{
				return t;
			}
			else if (child is Control nextControl)
			{
				var result = Find<T>(nextControl, name, maxDeepness - 1);
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
}
