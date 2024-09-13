using Newtonsoft.Json;
using System;
using System.IO;
using Avalonia.Platform.Storage;

namespace WorldsFactory.project;

/// <summary>
/// Represents informations about project file, 
/// It tries to load it, or creates a new one.
///
/// The project file is a json file located in the
/// root of the project.
/// It contains basic informations about the project.
/// </summary>
public class ProjectFile
{
	public static readonly string Name = "project.json";
	public static readonly string WorldsFileName = "world.json";
	public static readonly string WorldsFolderName = "World";
	public ProjectFileData? Data { get; private set; }

	/// <summary>
	/// Loads the project file data.
	/// Can be called only if the project has right structure.
	/// </summary>
	/// <param name="pathToRoot">Path to the prject's root.</param>
	/// <exception cref="InvalidateProjectFileException">Is thrown when the validation fails.</exception>
	public ProjectFile(string pathToRoot)
	{
		ReloadData(pathToRoot);
	}

	private ProjectFile(IStorageFolder projectRoot, string name)
	{
		Data = new ProjectFileData(name);
		string? path = StorageProviderExtensions.TryGetLocalPath(projectRoot);
		if (path is not null)
			SaveData(path);
		else throw new InvalidateProjectFileException("Project File constructor with projectPath that does not exit.");
	}
	
	/// <summary>
	/// Creates new project file object and saves it.
	/// </summary>
	/// <param name="pathToRoot">Path to the prject's root.</param>
	/// <param name="name">Name of the project.</param>
	/// <exception cref="InvalidateProjectFileException">Is thrown when the validation fails.</exception>
	public static ProjectFile CreateNewProjectFileObject(IStorageFolder projectRoot, string name)
	{
		return new ProjectFile(projectRoot, name);
	}

	public void ReloadData(string pathToRoot)
	{
		if (!Validate(pathToRoot, out string error))
		{
			throw new InvalidateProjectFileException(error);
		}

		Data = loadData(Path.Combine(pathToRoot, Name));
	}

	public static bool Validate(string pathToRoot, out string error)
	{
		error = "";
		string projectPath = Path.Combine(pathToRoot, Name);

		if (!File.Exists(projectPath))
		{
			error = "The directory doesn't contain a project file.";
			return false;
		}

		try
		{
			ProjectFileData? project = loadData(projectPath);

			if (ProjectFileData.validate(project, error))
			{
				return true;
			}
		}
		catch (Exception e)
		{
			error = $"Error while reading or parsing the project file: {e.Message}";
			return false;
		}


		return true;
	}

	private static ProjectFileData? loadData(string projectFilePath)
	{
		string json = File.ReadAllText(projectFilePath);
		ProjectFileData? project = JsonConvert.DeserializeObject<ProjectFileData>(json);
		return project;
	}

	public void SaveData(string pathToRoot)
	{
		string json = JsonConvert.SerializeObject(Data, Formatting.Indented);
		File.WriteAllText(Path.Combine(pathToRoot, Name), json);
	}
	public class InvalidateProjectFileException : Exception
	{
		public InvalidateProjectFileException(string message) : base(message)
		{
		}
	}
}
