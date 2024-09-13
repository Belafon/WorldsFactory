using System.Collections.ObjectModel;
using System.ComponentModel;
using Avalonia.Platform.Storage;
using WorldsFactory.world.objects;

namespace WorldsFactory.world.library.classStructure;

public interface IMethodsBody : INotifyPropertyChanged
{
	public string Code { set; get; }

	void Delete();
	public ObservableCollection<WFObject> ReferencesToObjects { get; }
}