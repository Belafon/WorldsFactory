using System.Reflection.PortableExecutable;
using System;
using System.Runtime.InteropServices;
using System.Collections.Generic;
using System.IO;
using Newtonsoft.Json;
using Serilog;
using System.Diagnostics;

namespace WorldsFactory.project;


/// <summary>
/// All projects saved in own KnownProjects.json file 
/// and recently opened projects saved in RecentlyOpenedProjects.json file.
/// Handles references to all known projects.
/// </summary>
public class AllProjects
{
	private HashSet<ProjectReference> allKnownProjects;
	private List<ProjectReference> recentlyOpenedProjects;
	public Action<ProjectReference>? AddAllProjectAction { get; set; }
	public Action<ProjectReference>? RemoveAllProjectAction { get; set; }
	public Action<ProjectReference>? AddRecentProjectAction { get; set; }
	public Action<ProjectReference>? RemoveRecentProjectAction { get; set; }
	public Action? FindProjectInExplorerAction { get; init; }
	public Action? CreateNewProjectAction { get; init; }

	internal static readonly string KnownProjectsFileName = "KnownProjects.json";
	internal static readonly string RecentlyOpenedProjectsFileName = "RecentlyOpenedProjects.json";

	public AllProjects()
	{
		RemoveAllProjectAction += (ProjectReference r) => { };
		AddRecentProjectAction += (ProjectReference r) => { };
		RemoveRecentProjectAction += (ProjectReference r) => { };

		allKnownProjects = new HashSet<ProjectReference>();
		recentlyOpenedProjects = new List<ProjectReference>();

		loadAllProjects();
		loadRecentlyOpenedProjects();
	}

	/// <summary>
	///  Adds a new project reference, so the program will now where is the project
	///  located and what is its name.
	/// </summary>
	/// <param name="path"></param>
	/// <returns></returns>
	public ProjectReference AddNewProjectFromDirectory(string path)
	{
		if (!Directory.Exists(path))
		{
			throw new ArgumentException("The directory doesn't exist.");
		}

		string projectPath = Path.Combine(path, ProjectFile.Name);

		if (!File.Exists(projectPath))
		{
			throw new ArgumentException("The directory doesn't contain a project file.");
		}

		string name = Path.GetFileName(path);

		return addProject(name, path);
	}

	private ProjectReference addProject(string name, string path)
	{
		ProjectReference newProject = new ProjectReference(name, path);
		if (allKnownProjects.Contains(newProject))
		{
			Log.Warning("Project {project} is already in all known projects.", newProject);
			return newProject;
		}
		allKnownProjects.Add(newProject);
		AddAllProjectAction?.Invoke(newProject);
		saveAllProjects();
		return newProject;
	}

	/// <summary>
	/// Changes Recently opened project list and saves it.
	/// </summary>
	/// <param name="project"></param>
	public void OpenProject(ProjectReference project)
	{
		// move the project to the end of the queue
		recentlyOpenedProjects.Remove(project);
		RemoveRecentProjectAction?.Invoke(project);
		
		if(recentlyOpenedProjects.Count >= 5)
		{
			recentlyOpenedProjects.RemoveAt(0);
		}
		recentlyOpenedProjects.Add(project);
		AddRecentProjectAction?.Invoke(project);

		saveRecentlyOpenedProjects();
	}

	public HashSet<ProjectReference> GetAllKnownProjects()
	{
		return allKnownProjects;
	}

	public List<ProjectReference> GetRecentlyOpenedProjects()
	{
		return recentlyOpenedProjects;
	}

	private void loadAllProjects()
	{
		string filePath = getFilePathToFileInAppDataFolder(KnownProjectsFileName);
		string json = File.ReadAllText(filePath);
		var projects = JsonConvert.DeserializeObject<HashSet<ProjectReference>>(json);
		if (projects is not null)
		{
			var validatedProjects = new HashSet<ProjectReference>();
			foreach (var project in projects)
			{
				if (ValidateProjectStructure(project.Path, out string error))
				{
					validatedProjects.Add(project);
				}
				else
				{
					Log.Error("Projects structure {project} is not valid. Error: {error}", project, error);
					//						Debug.Assert(false, "Project {project} is not valid. Error: {error}");
				}
			}

			allKnownProjects = validatedProjects;
			if (validatedProjects.Count != projects.Count)
				saveAllProjects();
		}
	}

	private void saveAllProjects()
	{
		string filePath = getFilePathToFileInAppDataFolder(KnownProjectsFileName);
		string json = JsonConvert.SerializeObject(allKnownProjects, Formatting.Indented);
		File.WriteAllText(filePath, json);
	}

	public void RemoveProject(ProjectReference project)
	{
		allKnownProjects.Remove(project);
		RemoveAllProjectAction?.Invoke(project);
		if (recentlyOpenedProjects.Contains(project))
		{
			recentlyOpenedProjects.Remove(project);
			RemoveRecentProjectAction?.Invoke(project);
		}

		saveAllProjects();
	}


	private void loadRecentlyOpenedProjects()
	{
		string filePath = getFilePathToFileInAppDataFolder(RecentlyOpenedProjectsFileName);
		string json = File.ReadAllText(filePath);
		var projects = JsonConvert.DeserializeObject<List<ProjectReference>>(json);
		if (projects is not null)
		{
			var validatedProjects = new List<ProjectReference>();
			foreach (var project in projects)
			{
				if (ValidateProjectStructure(project.Path, out string error))
				{
					validatedProjects.Add(project);
				}
				else
				{
					Log.Error("Projects structure {project} is not valid. Error: {error}", project, error);
				}
			}

			recentlyOpenedProjects = validatedProjects;
			if (validatedProjects.Count != projects.Count)
				saveAllProjects();
		}
	}

		private void saveRecentlyOpenedProjects()
		{
			string filePath = getFilePathToFileInAppDataFolder(RecentlyOpenedProjectsFileName);
			string json = JsonConvert.SerializeObject(recentlyOpenedProjects, Formatting.Indented);
			File.WriteAllText(filePath, json);
		}

		private string getFilePathToFileInAppDataFolder(String fileName)
		{
			string filePath = Path.Combine(
				Program.getAppDataPath(),
				Program.appDataFolderName,
				fileName);

			if (!File.Exists(filePath))
			{
				File.Create(filePath).Close();
			}

			return filePath;
		}

		public bool ValidateProjectStructure(string pathToProjectFolder, out string error)
		{
			error = "";
			if (!Directory.Exists(pathToProjectFolder))
			{
				error = "The directory doesn't exist.";
				return false;
			}

			if (!ProjectFile.Validate(pathToProjectFolder, out error))
			{
				return false;
			}

			return true;
		}

	}

