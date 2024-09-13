using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;

public interface IMethod : INotifyPropertyChanged, IConceptWithID, NamedConcept
{
	public void Rename(string newName, string classIdPrefix);
	public WFType ReturnType { set; get; }
	public ObservableCollection<Parameter> Parameters { set; get; }
	[JsonIgnore]
	public IMethodsBody? Body { set; get; }
	[JsonIgnore]
	public List<Action> OnDelete { get; init; }

	public void Delete(IIDConceptManager idManager, ITagManager tagManager);

}
