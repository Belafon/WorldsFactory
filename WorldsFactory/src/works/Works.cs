using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Avalonia.Platform.Storage;
using System.Reactive.Linq;
using AvaloniaEdit.Utils;
using WorldsFactory.project;
using WorldsFactory.world;
using WorldsFactory.works.storyInterpreterWork.export;

namespace WorldsFactory.works;

/// <summary>
/// Collection of works, it is a container for all works in the project.
/// A Work epresents concrete type of work, 
/// it can be a book, screenplay, gamebook, etc.
/// </summary>
public class Works
{
	public ObservableCollection<IWork> Collection { get; private set; }
	IWorkLoader loader;
	public Works(IStorageFolder worksFolder)
	{
		Collection = new ObservableCollection<IWork>();
		loader = new WorkLoader(worksFolder);
		loader.LoadAll().ContinueWith(task =>
		{
			Collection.AddRange(task.Result);
		});
	}

	/// <summary>
	/// TODO Temporary function to export whole world, similar method should be in <see cref="Work"/>
	/// </summary>
	/// <param name="folder"></param>
	/// <param name="currentlyOpenedProject"></param>
	public async Task Export(IStorageFolder folder, World world)
	{
		await new ExportWholeWorld(folder, world)
			.ExportAndSave("allStoriesWorldsFactoryData");
	}
}
