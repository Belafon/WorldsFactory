using Avalonia.Platform.Storage;
using Microsoft.CodeAnalysis.CSharp;
using WorldsFactory.works;
using WorldsFactory.world;
using WorldsFactory.world.ids;

namespace WorldsFactory.project;

public interface ICurrentlyOpenedProject
{
	World World { get; }
	Works? Works { get; }
}

public class CurrentlyOpenedProject : ICurrentlyOpenedProject
{
	private ProjectReference projectReference;
	public World World { get; private set; }
	public Works? Works { get; private set; }
	
	/// <summary>
	/// Loads the projects data, like <see cref="World"/> or <see cref="Works"/>.
	/// </summary>
	/// <param name="projectReference"></param>
	/// <param name="storageProvider"></param>
	public CurrentlyOpenedProject(ProjectReference projectReference, IStorageProvider storageProvider)
	{
		this.projectReference = projectReference;
		var worldLoader = new WorldLoader(storageProvider, new TagManager());
		World = worldLoader.Load(projectReference)!;
		loadWorks(storageProvider);
	}
	
	private async void loadWorks(IStorageProvider storageProvider)
	{
		Works = await projectReference.GetWorksFolder(storageProvider)
		.ContinueWith(task =>
		{
			if (task.Result is IStorageFolder folder)
				return new Works(folder);
			else throw new FileNotFoundException("Works folder not found");
		});
	}

}