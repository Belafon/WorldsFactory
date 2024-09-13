using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using Serilog;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;
public class ClassLoader : IClassLoader
{
	private IStorageFile classFile;
	public IIDConceptManager IdClassesManager { get; init; }
	public IIDConceptManager IdManager { get; init; }
	public ITagManager TagManager { get; init; }

	public ClassLoader(
		IStorageFile classFile,
		IDClassesManager idManager,
		ITagManager tagManager)
	{
		this.classFile = classFile;
		TagManager = tagManager;
		IdClassesManager = idManager;
		IdManager = new IDClassManager(idManager);
	}
	public async void SaveToFile(Class clazz)
	{
		string checkName = classFile.Name;
		// remove .json from the end
		checkName = checkName.Substring(0, checkName.Length - 5);
		if (clazz.GetPostfixId() != checkName)
		{
			Log.Warning("ClassLoader: Class name and file name are not the same. Class name: {0}, File name: {1}", clazz.GetPostfixId(), checkName);
		}

		string json = JsonConvert.SerializeObject(clazz, Formatting.Indented, new JsonSerializerSettings
		{
			Converters = {
				new ClassReferenceConverter(IdManager)
			}
		});

		var streamWriter = await classFile.OpenWriteAsync();
		var str = new StreamWriter(streamWriter);
		await str.WriteAsync(json);
		str.Close();
		streamWriter.Close();
	}

	public async void Delete(ILibrary library)
	{
		await classFile.DeleteAsync();
	}

	public async Task Rename(string newName)
	{
		string? data = await Load();
		if(data is null)
			data = "";

		var folder = await classFile.GetParentAsync();
		await classFile!.DeleteAsync();
		if(folder is null)
		{
			Log.Error("ClassLoader: Could not get parent folder");
			throw new NullReferenceException("ClassLoader: Could not get Classes folder.");
		}
		
		var newFile = await folder.CreateFileAsync(newName + ".json");
		if(newFile is null)
		{
			Log.Error("ClassLoader: Could not create new file");
			throw new NullReferenceException("ClassLoader: Could not create new file");
		}

		writeTofile(data, newFile);
		classFile = newFile;
	}

	private async void writeTofile(string data, IStorageFile file)
	{
		var streamWriter = await file.OpenWriteAsync();
		var str = new StreamWriter(streamWriter);
		await str.WriteAsync(data);
		str.Close();
		streamWriter.Close();
	}

	public async Task<string?> Load()
	{
		await using var stream = await classFile.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		string allContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();
		return allContent;
	}
}
