using System.Collections.ObjectModel;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.objects;

public interface IObjects
{
	public void CreateNewObject(string name, WFType type);
	public ObservableCollection<WFObject> Collection { get; }
}