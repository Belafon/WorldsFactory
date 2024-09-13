using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;
public class Parameter : INotifyPropertyChanged
{
	public event PropertyChangedEventHandler? PropertyChanged;
	private string name;
	public string Name
	{
		get { return name; }
		set
		{
			if (name != value)
			{
				name = value;
				 PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Name"));
			}
		}
	}

	private WFType type;
	public WFType Type
	{
		get { return type; }
		set
		{
			if (type != value)
			{
				type = value;
				 PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Type"));
			}
		}
	}
	public Parameter(String name, WFType type)
	{
		this.name = name ?? throw new ArgumentNullException(nameof(name));
		this.type = type ?? throw new ArgumentNullException(nameof(type));
	}
}