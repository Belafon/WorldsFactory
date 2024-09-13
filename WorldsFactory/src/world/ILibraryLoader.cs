using Avalonia.Platform.Storage;

namespace WorldsFactory.world;

public interface ILibraryLoader
{
	public IStorageFolder? MethodsFolder { get; }
}
