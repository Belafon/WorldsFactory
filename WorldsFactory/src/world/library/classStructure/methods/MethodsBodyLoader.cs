using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Avalonia.Platform.Storage;
using Serilog;
using WorldsFactory.world.ids;

namespace WorldsFactory.world.library.classStructure.methods;

/// <summary>
/// This is a loader for one particular method body.
/// </summary>
public class MethodsBodyLoader : IMethodsBodyLoader
{
	private const string JSON_FILE_NAME_POSTFIX = ".json";
	private IStorageFile? file;
	private IStorageFolder methodsFodler;
	private string fileName;
	private string methodsName;
	public IIDConceptManager IdManager { get; }

	/// <summary>
	/// This is a loader for one particular method body.
	/// </summary>

	public MethodsBodyLoader(IStorageFolder methodsFolder, IMethod method, IIDConceptManager idManager)
	{
		this.methodsFodler = methodsFolder;
		this.fileName = method.Id + JSON_FILE_NAME_POSTFIX;
		this.methodsName = method.Name;
		IdManager = idManager;
		method.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "Name")
				Rename(method.Name, method.Id);
		};
	}
	
	/// <summary>
	/// Loads the methods body from the file.
	/// </summary>
	/// <returns></returns>
	public async Task<string?> Load()
	{
		await foreach (var currentFile in methodsFodler.GetItemsAsync())
		{
			if (currentFile.Name == fileName
				&& currentFile is IStorageFile file)
			{
				this.file = file;
				break;
			}
		}

		if (file is null)
		{
			return null;
			//throw new ArgumentException("MethodsBody File does not exist");
		}

		await using var stream = await file.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		string allContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();
		return allContent;
	}

	public async Task<IMethodsBody> CreateNewMethodsBody(List<Parameter> parameters)
	{
		//checkIfFileAlreadyExists(fileName);
		string emptyBody = MethodsBody.GetMethodsHeader(methodsName, parameters) + "\n\t" + "pass";

		file = await methodsFodler.CreateFileAsync(fileName);
		if (file is null)
			throw new FileLoadException("MethodsBody File could not be created");

		fillFileWithData(emptyBody);
		return new MethodsBody(this, emptyBody);
	}

	private async void checkIfFileAlreadyExists(string name)
	{
		await foreach (var currentFile in methodsFodler.GetItemsAsync())
		{
			if (currentFile.Name == name
				&& currentFile is IStorageFile file)
				throw new ArgumentException("MethodsBody File already exists");
		}
		return;
	}

	public async void Rename(string newName, string newId)
	{
		if (await checkIfFileIsNull() is false)
			throw new NullReferenceException("MethodsBody file is null");

		checkIfFileAlreadyExists(newName);
		string? body = await Load();
		await file!.DeleteAsync();


		fileName = newId + JSON_FILE_NAME_POSTFIX;

		file = await methodsFodler.CreateFileAsync(fileName);
		if (file is null)
			throw new FileLoadException("MethodsBody File could not be created");

		if (body is null)
			throw new NullReferenceException("MethodsBody body is null");

		methodsName = newName;
		
		var regex = new Regex(@"def (\w+)(.*)", RegexOptions.Singleline);
		var match = regex.Match(body);
		var name = match.Groups[1].Value;
		var rest = match.Groups[2].Value;
		var updatedBody = "def " + newName + rest;

		fillFileWithData(updatedBody);
	}

	private async void fillFileWithData(string data)
	{
		if (await checkIfFileIsNull() is false)
			throw new NullReferenceException("MethodsBody file is null");

		await using var stream = await file!.OpenWriteAsync();
		using var streamWriter = new StreamWriter(stream);
		await streamWriter.WriteAsync(data);
		streamWriter.Close();
		stream.Close();
	}

	private async Task<bool> checkIfFileIsNull()
	{
		if (file is null)
		{
			Log.Error("MethodsBody file is null");
			Log.Warning("New file for MethodsBody will be created");
			await CreateNewMethodsBody(new List<Parameter>());
		}

		if (file is null)
			return false;

		return true;
	}

	public void Save(string data)
	{
		fillFileWithData(data);
	}

	public void DeleteMethodsBodyFile()
	{
		file?.DeleteAsync();
		file = null;
	}
}