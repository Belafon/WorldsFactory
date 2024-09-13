using System.Collections.ObjectModel;
using System.ComponentModel;
using WorldsFactory.src.screen;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library;
public interface IClass : IConceptWithID, NamedConcept
{
	public string GetPostfixId();
	public ObservableCollection<string> GetTags();
	public string Description { get; set; }
	public Reference<IClass>? Parent { get; set; }
	public FullyObservableCollection<Reference<IClass>> Children { get; set; }
	public ObservableCollection<Property> Properties { get; set; }
	public ObservableCollection<IMethod> Methods { get; set; }
	public Reference<IClass> GetReference();
	public void SaveToFile();
	public IMethod CreateNewMethod(string name, WFType type, List<Parameter> parameters, ILibraryLoader library);
	public Property CreateNewProperty(string name, WFType type, ObservableCollection<string> tags);
	public void Rename(string newName);
	public void Delete(ILibrary? library);
	public event EventHandler? OnDelete;
	/// <summary>
	/// Triggered, when parent is changed to another
	/// </summary>
	public event EventHandler? OnParentChanged;
	public event PropertyChangedEventHandler? PropertyChanged;

}